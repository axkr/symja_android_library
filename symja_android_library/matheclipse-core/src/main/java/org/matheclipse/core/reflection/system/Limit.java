package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;

public final class Limit extends AbstractFunctionOptionEvaluator {

  /** Direction of limit computation */
  private static enum Direction {
    /** Compute the limit approaching from larger real values. */
    FROM_ABOVE(-1),

    /** Compute the limit approaching from larger or smaller real values automatically. */
    TWO_SIDED(0),

    /** Compute the limit approaching from smaller real values. */
    FROM_BELOW(1);

    private int direction;

    private Direction(int direction) {
      this.direction = direction;
    }

    /**
     * Convert the direction <code>FROM_ABOVE, TWO_SIDED, FROM_BELOW</code> to the corresponding
     * value <code>-1, 0, 1</code>
     *
     * @return
     */
    int toInt() {
      return direction;
    }
  }

  public static class GruntzLimit {


    // Safely tracks how deep the Gruntz algorithm has recursed from external Limit fallbacks
    private static final ThreadLocal<Integer> GRUNTZ_DEPTH = ThreadLocal.withInitial(() -> 0);

    /**
     * True while {@link #evalGruntz} runs its own series expansion. ASTSeriesData.taylorSeries
     * computes coefficients through engine-level Limit calls; letting those re-enter the Gruntz
     * algorithm builds a mutually recursive evalGruntz/taylorSeries tree whose combinatorial
     * cost grinds for minutes (observed on GoldenRatio-power and Sin(n*poly) inputs). Inner
     * coefficient limits may use the ordinary heuristics - just never a nested Gruntz run.
     */
    private static final ThreadLocal<Boolean> IN_GRUNTZ_SERIES =
        ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** Nesting cap for the Gruntz-internal exponent-limit in the mrv E-case. */
    private static final ThreadLocal<Integer> MRV_EXP_GRUNTZ_DEPTH =
        ThreadLocal.withInitial(() -> 0);

    /** Nesting cap for the Gruntz-internal comparison limit in {@link #compareGrowth}. */
    private static final ThreadLocal<Integer> COMPARE_GROWTH_GRUNTZ_DEPTH =
        ThreadLocal.withInitial(() -> 0);

    /** Nesting cap for the comparability-coefficient limit in {@link #rewrite}. */
    private static final ThreadLocal<Integer> REWRITE_RATIO_DEPTH =
        ThreadLocal.withInitial(() -> 0);

    /**
     * Memoizes {@link #compareGrowth} within one top-level Gruntz run. The growth comparison of
     * a pair (f, g) as x -> Infinity is a pure function of the pair, but mrv-set construction
     * re-derives the same comparisons dozens of times and each heuristic ratio-limit costs
     * seconds (observed: thesis 8.14's tower ratio recomputed 12+ times at ~1-4s each = the
     * whole budget). Keyed on the printed form, NOT the IExpr: the mrv/rewrite passes mint a
     * fresh {@code Dummy} variable each time, so structurally-identical terms are not
     * {@code .equals()} - only their printed form is stable. That is sound because the
     * comparison is intrinsic (always "as the variable -> +Infinity", direction-independent),
     * so equal printed forms always mean equal growth comparisons. Cleared at the top of
     * {@link #evaluateLimit} when GRUNTZ_DEPTH is 0 so it never grows unbounded across a session.
     */
    private static final ThreadLocal<Map<String, Integer>> COMPARE_GROWTH_CACHE =
        ThreadLocal.withInitial(HashMap::new);

    /**
     * Memoizes {@link #signInf} within one top-level Gruntz run, same rationale and printed-form
     * keying as {@link #COMPARE_GROWTH_CACHE}. Only definitive results (+1/0/-1) are cached, not
     * the {@code Integer.MIN_VALUE} "unknown" a shallow sample-point attempt may return, so a
     * later call at a depth that can resolve it is not poisoned.
     */
    private static final ThreadLocal<Map<String, Integer>> SIGN_INF_CACHE =
        ThreadLocal.withInitial(HashMap::new);

    /**
     * Highest truncation order the adaptive w-series loop in {@link #evalGruntz} will try before
     * giving up. Each step re-expands the whole series, so this bounds the cost of chasing a
     * leading term that cancellation has pushed to a high order.
     *
     * <p>
     * Kept at {@code 2} (the loop runs once - zero-coefficient skip only). Raising it turns on
     * SymPy-style adaptive order-raising for deeper cancellation, but that doubled
     * SeriesTest/LimitTest with no case resolved while the tower differences that need it time
     * out earlier in {@code compareGrowth}; re-enable (e.g. {@code 8}) once that bottleneck is
     * fixed. See the comment in {@link #evalGruntz}.
     */
    private static final int GRUNTZ_MAX_SERIES_ORDER = 2;

    public static IExpr combineExponentials(IExpr expr, EvalEngine engine) {
      if (expr.isTimes()) {
        IAST times = (IAST) expr;
        IASTAppendable eExponents = F.PlusAlloc(times.argSize());
        IASTAppendable newTimes = F.TimesAlloc(times.argSize());
        boolean changed = false;
        for (int i = 1; i <= times.argSize(); i++) {
          IExpr arg = combineExponentials(times.get(i), engine);
          if (arg.isPower() && arg.base().equals(S.E)) {
            eExponents.append(arg.exponent());
            changed = true;
          } else if (arg.equals(S.E)) {
            eExponents.append(F.C1);
            changed = true;
          } else {
            newTimes.append(arg);
          }
        }
        if (changed) {
          // Use Expand to group algebraic terms cleanly, but DO NOT evaluate the
          // outer Power/Times to prevent Symja from instantly re-splitting them!
          IExpr combinedExponent = engine.evaluate(F.Expand(eExponents));
          newTimes.append(F.Power(S.E, combinedExponent));
          return newTimes.argSize() == 1 ? newTimes.arg1() : newTimes;
        }
        return expr;
      }
      if (expr.isPower()) {
        IExpr base = combineExponentials(expr.base(), engine);
        IExpr exp = combineExponentials(expr.exponent(), engine);
        if (base.isPower() && base.base().equals(S.E)) {
          return F.Power(S.E, engine.evaluate(F.Expand(F.Times(base.exponent(), exp))));
        }
        if (base.equals(expr.base()) && exp.equals(expr.exponent())) {
          return expr;
        }
        return F.Power(base, exp);
      }
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IASTAppendable result = ast.copyHead();
        boolean changed = false;
        for (int i = 1; i <= ast.argSize(); i++) {
          IExpr arg = combineExponentials(ast.get(i), engine);
          result.append(arg);
          if (arg != ast.get(i)) {
            changed = true;
          }
        }
        return changed ? result : expr; // Do NOT evaluate generic ASTs
      }
      return expr;
    }

    public static IExpr combinePlusLogs(IAST plusAST, boolean force, ISymbol x) {
      Map<IExpr, IASTAppendable> groupMap = new HashMap<>();
      IASTAppendable remainingTerms = F.PlusAlloc(plusAST.size());

      for (int i = 1; i < plusAST.size(); i++) {
        IExpr term = plusAST.get(i);
        IExpr coeff = F.C1;
        IExpr logArg = F.NIL;

        if (term.isLog()) {
          logArg = term.first();
        } else if (term.isTimes()) {
          IAST times = (IAST) term;
          IASTAppendable coeffPart = F.TimesAlloc(times.size());
          IExpr sign = F.C1;

          for (IExpr factor : times) {
            if (factor.isLog()) {
              logArg = factor.first();
            } else if (factor.isNumber() && factor.isNegative()) {
              sign = F.eval(F.Times(sign, factor));
            } else {
              coeffPart.append(factor);
            }
          }

          coeff = F.eval(coeffPart.oneIdentity1());

          if (sign.isMinusOne() && logArg.isPresent()) {
            logArg = F.Power(logArg, F.CN1);
          } else if (!sign.isOne()) {
            coeff = F.eval(F.Times(coeff, sign));
          }
        }

        if (logArg.isPresent() && (force || logArg.isPositiveResult())) {
          IASTAppendable args = groupMap.getOrDefault(coeff, F.TimesAlloc(4));
          args.append(logArg);
          groupMap.put(coeff, args);
        } else {
          remainingTerms.append(term);
        }
      }

      if (groupMap.isEmpty()) {
        return plusAST;
      }

      for (Map.Entry<IExpr, IASTAppendable> entry : groupMap.entrySet()) {
        IExpr coeff = entry.getKey();
        IAST combinedArgs = entry.getValue();

        IExpr mergedLog = F.Log(F.evalExpandAll(combinedArgs));
        remainingTerms.append(F.eval(F.Times(coeff, mergedLog)));
      }

      return F.eval(remainingTerms);
    }

    /**
     * Splits {@code arg == w^k * rest} where {@code k >= 1} is the minimal integer w-degree
     * over the additive terms of {@code arg} (so {@code rest}'s w-free part is nonzero).
     * Returns {@code {k, rest}} or {@code null} when no positive uniform w-power factors out
     * structurally (w buried inside Log/Exp/... bails conservatively).
     */
    private static IExpr[] splitWFactor(IExpr arg, IExpr w, EvalEngine engine) {
      int minDegree = Integer.MAX_VALUE;
      if (arg.isPlus()) {
        for (int i = 1; i < ((IAST) arg).size(); i++) {
          int d = wDegree(((IAST) arg).get(i), w);
          if (d <= 0) {
            return null;
          }
          minDegree = Math.min(minDegree, d);
        }
      } else {
        minDegree = wDegree(arg, w);
        if (minDegree <= 0) {
          return null;
        }
      }
      // Expand is required: the engine does NOT distribute (w+w*z)/w over the sum on its
      // own, and an undistributed quotient would fail the residual-degree check below.
      IExpr rest = engine.evaluate(F.Expand(F.Divide(arg, F.Power(w, F.ZZ(minDegree)))));
      if (!rest.isPlus() && !rest.isFree(w) && wDegree(rest, w) == Integer.MIN_VALUE) {
        return null; // division did not cancel structurally
      }
      return new IExpr[] {F.ZZ(minDegree), rest};
    }

    /**
     * Integer w-degree of a single multiplicative term: 0 when free of w, the summed power
     * of direct {@code w}/{@code w^int} factors of a Times/Power, {@link Integer#MIN_VALUE}
     * when w occurs any other way (unsupported).
     */
    private static int wDegree(IExpr term, IExpr w) {
      if (term.isFree(w)) {
        return 0;
      }
      if (term.equals(w)) {
        return 1;
      }
      if (term.isPower() && term.base().equals(w)) {
        int e = term.exponent().toIntDefault();
        return e == Integer.MIN_VALUE ? Integer.MIN_VALUE : e;
      }
      if (term.isTimes()) {
        int sum = 0;
        for (int i = 1; i < ((IAST) term).size(); i++) {
          int d = wDegree(((IAST) term).get(i), w);
          if (d == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
          }
          sum += d;
        }
        return sum;
      }
      return Integer.MIN_VALUE;
    }

    /**
     * Cheap structural test for "eventually strictly positive as x -> +Infinity": sums and
     * products of positive constants, x itself, and powers of such. Deliberately
     * limit-free - a conservative false is always safe for callers.
     */
    private static boolean isStructurallyPositive(IExpr expr, ISymbol x) {
      if (expr.equals(x)) {
        return true;
      }
      if (expr.isNumber()) {
        return expr.isPositive();
      }
      if (expr.isPositiveResult()) {
        return true;
      }
      if (expr.isPower()) {
        // b^c > 0 whenever b > 0 (real c)
        return isStructurallyPositive(expr.base(), x);
      }
      if (expr.isPlus() || expr.isTimes()) {
        return ((IAST) expr).forAll(a -> isStructurallyPositive(a, x));
      }
      return false;
    }

    /**
     * Mathematically compares the growth classes of two expressions f and g, memoized within the
     * current top-level Gruntz run (see {@link #COMPARE_GROWTH_CACHE}).
     */
    private static int compareGrowth(IExpr f, IExpr g, ISymbol x, EvalEngine engine) {
      if (f.equals(g))
        return 0; // Short-circuit identical expressions

      Map<String, Integer> cache = COMPARE_GROWTH_CACHE.get();
      String fKey = f.toString();
      String gKey = g.toString();
      Integer cached = cache.get(fKey + ' ' + gKey);
      if (cached != null) {
        return cached;
      }

      int result = compareGrowthUncached(f, g, x, engine);

      // The comparison is antisymmetric: cmp(g, f) == -cmp(f, g) (the 0-on-failure early
      // returns are symmetric too - they turn on signInf of the shared argument). Cache both
      // directions so the reversed comparison mrvMax may issue later is also a hit.
      cache.put(fKey + ' ' + gKey, result);
      cache.put(gKey + ' ' + fKey, -result);
      return result;
    }

    /**
     * Mathematically compares the growth classes of two expressions f and g.
     */
    private static int compareGrowthUncached(IExpr f, IExpr g, ISymbol x, EvalEngine engine) {
      try {
        int signF = signInf(f, x, engine);
        if (!F.isPresent(signF))
          return 0;
        IExpr posF = (signF == -1) ? engine.evaluate(F.Negate(f)) : f;

        int signG = signInf(g, x, engine);
        if (!F.isPresent(signG))
          return 0;
        IExpr posG = (signG == -1) ? engine.evaluate(F.Negate(g)) : g;

        IExpr logF = getLog(posF, engine);
        IExpr logG = getLog(posG, engine);
        IExpr ratio = engine.evaluate(F.Divide(logF, logG));

        IExpr limitResult;
        if (ratio.isFree(x)) {
          limitResult = ratio;
        } else {
          limitResult = F.NIL;
          // Exponential-tower ratios drown the heuristic Limit engine in
          // L'Hopital/Apart/Simplify cycles (observed burning whole time budgets on thesis
          // 8.20). The thesis computes this comparison limit with the Gruntz algorithm
          // itself - Log strips one tower level per recursion, so it terminates.
          if (ratio.has(t -> t.isExp(), true) && COMPARE_GROWTH_GRUNTZ_DEPTH.get() < 4) {
            COMPARE_GROWTH_GRUNTZ_DEPTH.set(COMPARE_GROWTH_GRUNTZ_DEPTH.get() + 1);
            try {
              limitResult = evalGruntz(ratio, x, engine);
            } finally {
              COMPARE_GROWTH_GRUNTZ_DEPTH.set(COMPARE_GROWTH_GRUNTZ_DEPTH.get() - 1);
            }
          }
          if (limitResult.isNIL()) {
            LimitData limitData =
                new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
            // ensure Power(Infinity, -1) evaluates to 0.
            limitResult = engine.evaluate(evalLimitQuiet(ratio, limitData));
          } else {
            limitResult = engine.evaluate(limitResult);
          }
        }

        if (!limitResult.isFree(S.Limit, true) || !limitResult.isFree(S.Derivative, true)) {
          return 0;
        }

        if (limitResult.isZero()) {
          return -1;
        } else if (limitResult.isInfinity() || limitResult.isNegativeInfinity()
            || limitResult.isDirectedInfinity()) {
          return 1;
        } else if (limitResult.isPresent() && !limitResult.isIndeterminate()
            && limitResult.isFree(x)) {
          return 0;
        }
        return 0;
      } catch (RuntimeException e) {
        return 0;
      }
    }

    public static IExpr evalGruntz(IExpr expr, ISymbol x, EvalEngine engine) {
      return evalGruntz(expr, x, engine, 0);
    }

    /**
     * Evaluates the limit as x -> Infinity using the rigorous Gruntz algorithm.
     */
    private static IExpr evalGruntz(IExpr expr, ISymbol x, EvalEngine engine, int depth) {
      if (depth > 10) {
        return F.NIL; // Guard against infinite Gruntz recursion
      }

      // --- GRUNTZ OSCILLATION BREAKERS ---
      // The Gruntz algorithm mathematically oscillates between E^x/x and x/Log(x)
      // because their Puiseux series expansions contain non-polynomial logs.
      // We intercept these canonical forms and resolve their asymptotic dominance instantly.
      if (expr.isTimes() || expr.isPower()) {
        Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(expr, false);
        if (parts.isPresent() && !parts.get()[1].isOne()) {
          IExpr num = parts.get()[0];
          IExpr den = parts.get()[1];

          IExpr coeff = F.C1;
          if (num.isTimes() && num.first().isNumber()) {
            coeff = num.first();
            num = engine.evaluate(F.Divide(num, coeff));
          } else if (num.isNumber()) {
            coeff = num;
            num = F.C1;
          }

          IExpr inf = engine.evaluate(F.Times(coeff, F.CInfinity));

          if (num.equals(x)) {
            if (den.isAST(S.Log, 2) && den.first().equals(x))
              return inf; // x / Log(x) -> Infinity
            if (den.isExp() && den.second().equals(x))
              return F.C0; // x / E^x -> 0
          }
          if (den.equals(x)) {
            if (num.isAST(S.Log, 2) && num.first().equals(x))
              return F.C0; // Log(x) / x -> 0
            if (num.isExp() && num.second().equals(x))
              return inf; // E^x / x -> Infinity
          }
        }
      }

      if (expr.isFree(x)) {
        // collapse constant log-sums - series coefficients assemble them unsimplified,
        // e.g. Log(2)/2 + Log(1/(2*Pi))/2 + Log(Pi)/2 which is exactly 0
        if (expr.isPlus() && expr.has(t -> t.isLog(), true)) {
          IExpr combined = engine.evalQuiet(logCombine(expr, true, null));
          if (combined.isPresent()) {
            return combined;
          }
        }
        return expr;
      }

      // Series-based analysis of a multi-thousand-leaf expression (e.g. the Taylor
      // remainders AsymptoticRSolveValue feeds through nested Limit calls) never finishes -
      // the taylorSeries/Limit recursion grinds combinatorially. Refuse early.
      if (expr.leafCount() > 1000) {
        return F.NIL;
      }

      // A power with an eventually-NEGATIVE base and an x-dependent exponent - e.g. (-z)^(3+z)
      // from transforming Limit(x^(x-3), x->-Infinity) via x = -z - is complex-valued (branch
      // cuts): the real-line Gruntz algorithm cannot handle it, and its mrv/rewrite recursion
      // grinds indefinitely on the resulting Log(-z) forms (observed hanging Maximize inside
      // NSolve, SolveTest#testNSolve). Refuse fast so the ordinary heuristics handle it (they
      // return it unevaluated). A base is "eventually negative" when its negation is
      // structurally positive - cheap and conservative, never fires on the positive bases the
      // gruntz-first power gate legitimately targets ((1+1/x), (x^7+..)/(2^x+..), Log-towers).
      if (expr.has(p -> p.isPower() && !p.exponent().isFree(x)
          && isStructurallyPositive(engine.evaluate(F.Negate(p.base())), x), true)) {
        return F.NIL;
      }

      // A pure exponential E^f: by continuity the limit is E^(lim f) - recurse on the
      // exponent directly instead of grinding the series machinery on tower-bearing
      // coefficients (thesis 8.20's E^(log-nest ratio) burned whole time budgets there).
      // The engine auto-writes E^(Log(b)*c) as b^c, so the same continuity route must
      // also catch Power(Log-tower base, x-dependent exponent) via f = c*Log(b) - the
      // Log base tends to +Infinity, making the rewrite b^c = E^(c*Log(b)) valid.
      // Two exclusions: plain exponents (the E^u moveup artifacts) must use the ordinary
      // machinery to avoid an infinite moveup cycle, and Plus-of-exponential exponents
      // (E^A - E^B differences, thesis 8.14) mis-sign in the recursion - the general
      // machinery handles them.
      IExpr contExponent = F.NIL;
      if (expr.isExp()) {
        contExponent = expr.exponent();
      } else if (expr.isPower() && !expr.base().isFree(x) && !expr.exponent().isFree(x)
          && expr.base().isFree(
              t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc), true)
          && (expr.base().isLog() || isStructurallyPositive(expr.base(), x))) {
        // an eventually positive base makes b^c = E^(c*Log(b)) a valid rewrite
        // (mixed poly/exponential bases like ((x^7+x+1)/(2^x+x^2))^(-1/x) starve the
        // series machinery, while the exponent c*Log(b) resolves by recursion); the
        // positivity test must stay structural - signInf's evalLimitQuiet(base) IS a
        // heuristic limit of the base and can itself become the burn
        contExponent = F.Times(expr.exponent(), F.Log(expr.base()));
      }
      if (contExponent.isPresent() && contExponent.has(t -> t.isLog() || t.isExp(), true)
          && !(contExponent.isPlus()
              && ((IAST) contExponent).exists(a -> a.has(t2 -> t2.isExp(), true)))) {
        IExpr exponentLimit =
            evalGruntz(engine.evaluate(contExponent), x, engine, depth + 1);
        if (exponentLimit.isPresent()) {
          if (exponentLimit.isInfinity()) {
            return F.CInfinity;
          }
          if (exponentLimit.isNegativeInfinity()) {
            return F.C0;
          }
          if (exponentLimit.isFree(x) && exponentLimit.isFree(S.DirectedInfinity)
              && exponentLimit.isIndeterminateFree()) {
            return engine.evaluate(F.Exp(exponentLimit));
          }
        }
        // exponent limit unknown - fall through to the general machinery
      }


      // Convert hyperbolic functions to exponentials early!
      // If left intact, rewrite() and ASTSeriesData treat them as constants,
      // creating false positive exponents in w that evaluate to 0.
      expr = expandHyperbolics(expr, engine);

      // Eliminate Abs and Sign functions since z -> +Infinity on the real line.
      // This prevents the Symja auto-evaluator from converting Abs(E^(-u)) into E^(-Re(u)),
      // which introduces complex Re/Im functions that cause infinite MRV recursion.
      if (expr.has(y -> y.isFunctionID(ID.Abs, ID.Sign), true)) {
        IExpr newExpr = F.subst(expr, y -> {
          if (y.isAbs()) {
            int sign = signInf(y.first(), x, engine);
            if (sign == 1)
              return y.first();
            if (sign == -1)
              return engine.evaluate(F.Negate(y.first()));
          } else if (y.isAST(S.Sign, 2)) {
            int sign = signInf(y.first(), x, engine);
            if (sign == 1)
              return F.C1;
            if (sign == -1)
              return F.CN1;
          }
          return F.NIL;
        });
        if (newExpr.isPresent() && !newExpr.equals(expr)) {
          expr = engine.evaluate(newExpr);
        }
        if (newExpr.isFree(x)) {
          return expr;
        }
      }


      // Combine exponentials strictly AFTER the dummy variable substitution!
      // Symja's engine.evaluate() aggressively re-splits E^(-1-2z+...) into E^-1 * E^(-2z)
      // during the substitution step. We must rigorously force them back together immediately
      // before MRV extraction to prevent the Slower Exponential Discard Trap.
      expr = GruntzLimit.combineExponentials(expr, engine);

      // Merge Stirling logs before mrv extraction so they cancel out
      // and don't pollute the growth classes.
      expr = logCombine(expr, true, x);

      // Find the MRV set of the expression
      IExpr mrvResult = mrv(expr, x, engine);
      if (!mrvResult.isPresent() || !mrvResult.isAST()) {
        if (DEBUG) {
          System.out.println("GRUNTZ mrv=NIL for " + expr);
        }
        return F.NIL;
      }
      mrvResult = S.DeleteDuplicates.of(engine, mrvResult);
      if (DEBUG) {
        System.out.println("MRV " + expr + " mrvResult: " + mrvResult);
      }
      IAST mrvSet = (IAST) mrvResult;

      // If the MRV set contains the limit variable x, it means there are no
      // rapidly varying exponentials. We must substitute x = Exp(u) to push the
      // expression up the mathematical growth scale so Puiseux series can process it.
      if (mrvSet.contains(x)) {
        ISymbol u = F.Dummy("u");
        IExpr substituted = engine.evaluate(F.subst(expr, x, F.Exp(u)));

        // Simplify Log(E^f) -> f structurally, and split logs of products containing an
        // exponential factor (Log(E^u*Log(u)) -> u + Log(Log(u)) via logExpand): the engine
        // does neither for a plain dummy (complex branch caution), but under the Gruntz
        // convention the dummy is real and positive, and the simplification is VITAL for
        // termination (sympy gruntz.py notes the same) - without it a log-nest like
        // Log(x)+Log(Log(x)) moves up to the opaque Log(E^u + Log(E^u)) instead of the
        // analyzable Log(E^u + u).
        IExpr logExpSimplified = substituted;
        for (int i = 0; i < 4; i++) {
          IExpr next = F.subst(logExpSimplified, e -> {
            if (e.isLog()) {
              IExpr arg = e.first();
              if (arg.isExp()) {
                return arg.exponent();
              }
              // only split when an Exp is a DIRECT factor (Log(E^u*Log(u)) -> u+Log(Log(u)));
              // an Exp buried deeper (e.g. Log(2*Pi/(1+E^u)) from Stirling remainders) must
              // stay intact - splitting it perturbs the Gamma pipeline into wrong values
              if (arg.isTimes() && ((IAST) arg).exists(t -> t.isExp())) {
                return logExpand(arg);
              }
            }
            return F.NIL;
          });
          if (!next.isPresent() || next.equals(logExpSimplified)) {
            break;
          }
          logExpSimplified = engine.evaluate(next);
        }
        substituted = logExpSimplified;

        if (DEBUG) {
          System.out.println("GRUNTZ moveup " + expr + "  ->  " + substituted);
        }
        if (substituted.equals(expr)) {
          return F.NIL;
        }
        return evalGruntz(substituted, u, engine, depth + 1);
      }

      // Extract representative growth term g
      IExpr g = getRepresentativeG(mrvSet, x, engine);
      if (!g.isPresent() || g.isNIL()) {
        return F.NIL;
      }

      // Rewrite expression in terms of decay variable w = Exp(-g) -> 0+
      ISymbol w = F.Dummy("w");
      IExpr rewritten = rewrite(expr, mrvSet, g, x, w, engine);

      // Gruntz algorithm strictly requires expanding Log(f(w)/w^k) into Log(f(w)) - k*Log(w)
      // before passing to the series evaluator, to avoid limit cycles on the singularity.
      rewritten = expandGruntzLogs(rewritten, w, engine);

      // Mathematically w = Exp(-g), so Log(w) = -g. We must substitute this
      // back in to remove the singularity at w=0 before series expansion.
      rewritten = engine.evaluate(F.subst(rewritten, F.Log(w), F.Negate(g)));

      // Use Cancel locally on terms to clear nested denominators like 1/(6+12/w) -> w/(12+6w)
      // Map over Plus to prevent merging into a single massive fraction which crashes Laurent
      // inversion
      if (rewritten.isPlus()) {
        IAST plus = (IAST) rewritten;
        IASTAppendable newPlus = F.PlusAlloc(plus.size());
        for (int i = 1; i < plus.size(); i++) {
          newPlus.append(engine.evaluate(F.Cancel(plus.get(i))));
        }
        rewritten = engine.evaluate(newPlus);
      } else {
        rewritten = engine.evaluate(F.Cancel(rewritten));
      }

      // An exponential E^f whose exponent has an APPARENT w-pole - e.g. the
      // E^(1/w - Log(1+w)/w^2) produced by rewriting E^(x - x^2*Log(1+1/x)) - defeats
      // seriesDataRecursive even when the singular parts cancel. Where the exponent's own
      // series turns out regular (minExponent >= 0), substitute its truncated normal form so
      // the outer series composition succeeds (E^(1/2 - w/3 + ...) -> coefficient Sqrt(E)).
      rewritten = preExpandExpExponents(rewritten, w, engine);

      // Same-class exponentials with an irrational log-ratio (e.g. {3^x, 5^x}) rewrite to
      // powers like w^(1-Log(5)/Log(3)). ASTSeriesData is a rational-exponent Laurent-Puiseux
      // machine and silently drops such terms, turning 3^x/(3^x+5^x) into the wrong finite
      // limit 1. For those shapes bypass the series entirely and extract the leading w-power
      // and its coefficient structurally (exponents compared numerically); the standard Gruntz
      // case split on the leading exponent then applies as usual.
      final ISymbol wFinal = w;

      // A circular trig function whose argument DIVERGES as w->0+ (e.g. Sin(n*poly(n)) after
      // the moveup substitution) has no Taylor series. The series machinery would not find
      // that out cheaply - taylorSeries computes coefficients through recursive Limit calls,
      // and on such inputs the nested evalGruntz/taylorSeries tree grinds for minutes
      // (observed via AsymptoticRSolveValue). Refuse up front; a trig argument that converges
      // at w=0 (finite valuation) is fine and keeps the Sin(1/x+E^(-x))-style cases working.
      boolean divergentTrigArg = rewritten.has(t -> {
        if (t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc)
            && !((IAST) t).isFree(wFinal, true)) {
          IExpr[] argLead = leadingWPower(t.first(), wFinal, engine);
          if (argLead == null) {
            return true; // cannot prove the argument converges - refuse
          }
          try {
            return engine.evaluate(argLead[0]).evalf() < -1.0e-9;
          } catch (RuntimeException rex) {
            return true;
          }
        }
        return false;
      }, true);
      if (divergentTrigArg) {
        return F.NIL;
      }
      if (rewritten.has(
          p -> p.isPower() && p.base().equals(wFinal) && !p.exponent().isRational(), true)) {
        IExpr[] lead = leadingWPower(rewritten, w, engine);
        if (lead == null) {
          // Unsupported shape - the series machinery would silently produce a wrong value
          // here, so stay honest.
          return F.NIL;
        }
        IExpr valuation = engine.evaluate(lead[0]);
        IExpr leadCoefficient = engine.evaluate(lead[1]);
        double v;
        if (valuation.isZero()) {
          v = 0.0;
        } else {
          try {
            v = valuation.evalf();
          } catch (RuntimeException rex) {
            return F.NIL;
          }
        }
        if (v > 1.0e-9) {
          return F.C0;
        } else if (v < -1.0e-9) {
          int sign = signInf(leadCoefficient, x, engine);
          if (sign == 1) {
            return F.CInfinity;
          } else if (sign == -1) {
            return F.CNInfinity;
          }
          return F.NIL;
        }
        if (leadCoefficient.equals(expr)) {
          return F.NIL; // no progress
        }
        return leadCoefficient.isFree(x) ? leadCoefficient
            : evalGruntz(leadCoefficient, x, engine, depth + 1);
      }

      // Calculate the series expansion of the rewritten expression around w = 0.
      // Block nested Gruntz entries while the series machinery runs - see IN_GRUNTZ_SERIES.
      if (DEBUG) {
        System.out.println("GRUNTZ pre-series g=" + g + " rewritten=" + rewritten);
      }
      // The leading term of the w-series can vanish through cancellation - two exponentials
      // of the same comparability class subtracting (thesis 8.14's tower difference), or a
      // second-order asymptotic tail surviving after the first-order parts cancel (the
      // -1/(2*Log(x)) in the digamma towers). A single fixed-order expansion then reports a
      // zero leading coefficient at minExponent and the case split below returns a spurious
      // 0 / NIL. Two responses of increasing cost:
      //   * skip leading zero coefficients inside the captured truncation - a pure scan of
      //     terms already computed (SymPy gruntz's leadterm does the same). Always on, free.
      //   * when the WHOLE truncation cancels (isOrder), re-expand at a higher order (the
      //     "adaptive order" of SymPy's calculate_series). This is expensive - total
      //     low-order cancellation is common among the deep mrv sub-limits AND among the many
      //     depth-0 Limit calls that Series[] issues internally - and, measured, it doubles
      //     SeriesTest/LimitTest for ZERO extra resolved cases: the hard tower differences
      //     (#80/#89/#71/#70) that need it do not even reach here - they time out earlier in
      //     compareGrowth's heuristic mrv comparison. So the order-raising is kept structural
      //     but gated OFF (GRUNTZ_MAX_SERIES_ORDER == 2 => the loop runs once); raise the cap
      //     to re-enable it once the compareGrowth bottleneck is addressed.
      // Null means an unsupported shape, not an under-resolved one, so a higher order will not
      // help - stop.
      ASTSeriesData series = null;
      int minExp = 0;
      IExpr leadCoeff = F.NIL;
      boolean oldInSeries = IN_GRUNTZ_SERIES.get();
      IN_GRUNTZ_SERIES.set(Boolean.TRUE);
      try {
        for (int seriesOrder = 2; seriesOrder <= GRUNTZ_MAX_SERIES_ORDER; seriesOrder += 2) {
          ASTSeriesData candidate =
              ASTSeriesData.seriesDataRecursive(rewritten, w, F.C0, seriesOrder, -1, engine);
          if (candidate == null) {
            break;
          }
          // Skip leading zero coefficients inside the captured truncation.
          int nMin = candidate.minExponent();
          while (nMin < candidate.truncateOrder() && candidate.coefficient(nMin).isZero()) {
            nMin++;
          }
          if (nMin < candidate.truncateOrder()) {
            series = candidate;
            minExp = nMin;
            leadCoeff = candidate.coefficient(nMin);
            break;
          }
          // The whole truncation is zero (isOrder): cancellation runs deeper than this order -
          // re-expand one step higher (only reached when GRUNTZ_MAX_SERIES_ORDER > 2).
          if (DEBUG) {
            System.out.println("GRUNTZ order " + seriesOrder + " all-zero, raising");
          }
        }
      } finally {
        IN_GRUNTZ_SERIES.set(oldInSeries);
      }

      if (series == null || leadCoeff.isNIL()) {
        if (DEBUG) {
          System.out.println("GRUNTZ series=null/allzero for " + rewritten);
        }
        return F.NIL;
      }

      if (DEBUG) {
        System.out.println("GRUNTZ g=" + g + " rewritten=" + rewritten);
        System.out.println("GRUNTZ minExp=" + minExp + " leadCoeff=" + leadCoeff);
      }

      // FAILSAFE: Prevent infinite loops if rewrite fails to simplify the expression
      if (leadCoeff.equals(expr)) {
        return F.NIL;
      }

      // The coefficient belongs to a slower growth class and might still depend on x.
      IExpr limitCoeff = evalGruntz(leadCoeff, x, engine, depth + 1);

      // Reconstruct the final limit based on the degree of the leading term in w
      if (minExp > 0) {
        return F.C0;
      } else if (minExp == 0) {
        return limitCoeff;
      } else {
        int sign = signInf(leadCoeff, x, engine);
        if (sign == 1) {
          return F.CInfinity;
        } else if (sign == -1) {
          return F.CNInfinity;
        }
        // If sign is indeterminate, we cannot rigorously state it diverges to +/- Infinity.
        return F.NIL;
      }
    }

    /**
     * Entry point for Gruntz limit evaluation. Transforms the limit to x -> Infinity and delegates
     * to the recursive driver.
     */
    public static IExpr evaluateLimit(IExpr expr, ISymbol x, IExpr x0, Limit.Direction direction,
        EvalEngine engine) {
      if (expr.isFree(x)) {
        return expr;
      }

      // No nested Gruntz runs from inside our own series expansion - the ordinary heuristics
      // may still resolve the coefficient limit; see IN_GRUNTZ_SERIES. (Even small
      // expressions must stay blocked: the recursive fan-out of the evalGruntz/taylorSeries
      // tree grinds regardless of the seed size.)
      if (IN_GRUNTZ_SERIES.get()) {
        return F.NIL;
      }

      // An unknown user function of the limit variable (e.g. a(n-2) from a recurrence fed
      // through Series by AsymptoticRSolveValue) has no defined growth class - SymPy's gruntz
      // raises "MRV set computation for UndefinedFunction is not allowed" for the same reason.
      // Without this gate the algorithm recurses through series/limit cycles on the opaque
      // function and can abort with "unexpected NIL expression encountered".
      final ISymbol variable = x;
      if (expr.has(e -> e.isAST() && !e.head().isBuiltInSymbol()
          && !((IAST) e).isFree(variable, true), true)) {
        return F.NIL;
      }

      // Prevent infinite loops where Series expansions call Limit, which calls Gruntz again.
      int depth = GRUNTZ_DEPTH.get();
      if (depth == 0) {
        // Fresh top-level Gruntz run: drop any growth-comparison / sign caches from a previous
        // limit so results never leak across limits (different variable / x0) or accumulate.
        COMPARE_GROWTH_CACHE.get().clear();
        SIGN_INF_CACHE.get().clear();
      }
      if (depth > 3) {
        return F.NIL;
      }

      int oldRecursionLimit = engine.getRecursionLimit();
      try {
        GRUNTZ_DEPTH.set(depth + 1);

        // Give Gruntz enough recursion depth to perform deep symbolic algebra,
        // overriding any starvation caused by L'Hopital rule fallbacks.
        if (oldRecursionLimit < 1024) {
          engine.setRecursionLimit(1024);
        }

        // Properly handle TWO_SIDED limits by evaluating both directional paths.
        // If the right-sided and left-sided limits do not match, the limit is Indeterminate.
        if (direction == Limit.Direction.TWO_SIDED && !x0.isInfinity()
            && !x0.isNegativeInfinity()) {
          IExpr limitAbove = evaluateLimit(expr, x, x0, Limit.Direction.FROM_ABOVE, engine);
          if (!limitAbove.isPresent() || limitAbove.isNIL()) {
            return F.NIL;
          }
          IExpr limitBelow = evaluateLimit(expr, x, x0, Limit.Direction.FROM_BELOW, engine);
          if (!limitBelow.isPresent() || limitBelow.isNIL()) {
            return F.NIL;
          }

          if (limitAbove.equals(limitBelow)) {
            return limitAbove;
          } else {
            return S.Indeterminate;
          }
        }

        IExpr transformedExpr = expr;
        ISymbol z = F.Dummy("z");

        // Standardize all limits to z -> Infinity
        if (x0.isInfinity()) {
          transformedExpr = F.subst(expr, x, z);
        } else if (x0.isNegativeInfinity()) {
          transformedExpr = F.subst(expr, x, F.Negate(z));
        } else {
          // For x -> x0, substitute x = x0 +/- 1/z
          if (direction == Limit.Direction.FROM_BELOW) {
            transformedExpr = F.subst(expr, x, F.Subtract(x0, F.Power(z, F.CN1)));
          } else {
            // Approaching from above
            transformedExpr = F.subst(expr, x, F.Plus(x0, F.Power(z, F.CN1)));
          }
        }

        return evalGruntz(transformedExpr, z, engine, 0);
      } catch (RuntimeException rle) {
        // Catch RecursionLimitExceeded and evaluation loops gracefully
        return F.NIL;
      } finally {
        GRUNTZ_DEPTH.set(depth);
        engine.setRecursionLimit(oldRecursionLimit);
      }
    }

    /**
     * Gruntz algorithm strictly requires expanding Log(f(w)/w^k) into Log(f(w)) - k*Log(w) before
     * passing to the series evaluator, to avoid limit cycles on the singularity.
     */
    private static IExpr expandGruntzLogs(IExpr expr, ISymbol w, EvalEngine engine) {
      if (expr.isFree(w)) {
        return expr;
      }

      if (expr.isLog()) {
        IExpr arg = expandGruntzLogs(expr.first(), w, engine);
        IExpr together = engine.evaluate(F.Together(arg));

        IExpr num = engine.evaluate(F.Numerator(together));
        IExpr den = engine.evaluate(F.Denominator(together));

        if (!den.isOne()) {
          return engine.evaluate(F.Subtract(expandGruntzLogs(F.Log(num), w, engine),
              expandGruntzLogs(F.Log(den), w, engine)));
        }
        if (together.isTimes()) {
          IAST times = (IAST) together;
          IASTAppendable plus = F.PlusAlloc(times.size());
          for (int i = 1; i < times.size(); i++) {
            plus.append(expandGruntzLogs(F.Log(times.get(i)), w, engine));
          }
          return engine.evaluate(plus);
        }
        if (together.isPower()) {
          return engine.evaluate(
              F.Times(together.exponent(), expandGruntzLogs(F.Log(together.base()), w, engine)));
        }
        // A Plus whose every addend carries a w-monomial factor (Log(w+w*z+w*z^7) from
        // rewriting Log(2^x+x^2)) - Together is a no-op on polynomials, so the den/Times
        // branches above never see it. Split off the uniform w^k: without this the series
        // machinery faces Log(w*R) which has NO Puiseux expansion and degrades to
        // dummy-variable garbage (leaked Log(y) coefficient turning
        // ((x^7+x+1)/(2^x+x^2))^(-1/x) into 1). The emitted Log(w) is substituted by -g
        // right after this pass.
        if (together.isPlus()) {
          IExpr[] factored = splitWFactor(together, w, engine);
          if (factored != null) {
            return engine.evaluate(F.Plus(F.Times(factored[0], F.Log(w)),
                expandGruntzLogs(F.Log(factored[1]), w, engine)));
          }
        }
        // compare against the ORIGINAL inner argument, not the recursed `arg`: when the
        // recursion already split an inner log (Log(1/w+x) -> Log(1+x*w) - Log(w)) and
        // Together is then a no-op, comparing against `arg` discarded the inner rewrite and
        // returned the untouched expression (observed as a null series on Log(Log(1/w+x)))
        if (!together.equals(expr.first())) {
          return engine.evaluate(F.Log(together));
        }
        return expr;
      }
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IASTAppendable result = ast.copyHead();
        for (int i = 1; i <= ast.argSize(); i++) {
          result.append(expandGruntzLogs(ast.get(i), w, engine));
        }
        return engine.evaluate(result);
      }
      return expr;
    }

    /**
     * Converts hyperbolic functions to exponentials. Gruntz algorithm extracts growth based
     * strictly on Exp and Log. If hyperbolics are left intact, they act as opaque constants during
     * rewrite and series expansion, causing limits to falsely evaluate to 0.
     */
    private static IExpr expandHyperbolics(IExpr expr, EvalEngine engine) {
      if (!expr.has(y -> y.isFunctionID(ID.Sinh, ID.Cosh, ID.Tanh, ID.Coth, ID.Sech, ID.Csch),
          true)) {
        return expr;
      }
      IExpr rewritten = F.subst(expr, y -> {
        if (y.isAST(S.Sinh, 2)) {
          return engine
              .evaluate(F.Times(F.C1D2, F.Subtract(F.Exp(y.first()), F.Exp(F.Negate(y.first())))));
        } else if (y.isAST(S.Cosh, 2)) {
          return engine
              .evaluate(F.Times(F.C1D2, F.Plus(F.Exp(y.first()), F.Exp(F.Negate(y.first())))));
        } else if (y.isAST(S.Tanh, 2)) {
          IExpr ePos = F.Exp(y.first());
          IExpr eNeg = F.Exp(F.Negate(y.first()));
          return engine.evaluate(F.Divide(F.Subtract(ePos, eNeg), F.Plus(ePos, eNeg)));
        } else if (y.isAST(S.Coth, 2)) {
          IExpr ePos = F.Exp(y.first());
          IExpr eNeg = F.Exp(F.Negate(y.first()));
          return engine.evaluate(F.Divide(F.Plus(ePos, eNeg), F.Subtract(ePos, eNeg)));
        } else if (y.isAST(S.Sech, 2)) {
          return engine
              .evaluate(F.Divide(F.C2, F.Plus(F.Exp(y.first()), F.Exp(F.Negate(y.first())))));
        } else if (y.isAST(S.Csch, 2)) {
          return engine
              .evaluate(F.Divide(F.C2, F.Subtract(F.Exp(y.first()), F.Exp(F.Negate(y.first())))));
        }
        return F.NIL;
      });
      return rewritten.isPresent() ? rewritten : expr;
    }

    /**
     * Bypasses Symja's evaluation engine to directly extract the mathematical logarithm of
     * exponential functions, preventing L'Hopital ratio limits from falsely evaluating to 0.
     */
    private static IExpr getLog(IExpr expr, EvalEngine engine) {
      if (expr.isExp()) {
        return expr.exponent();
      }
      return engine.evaluate(F.PowerExpand(F.Log(expr)));
    }

    /**
     * Extracts a strictly positive representative growth term 'g' from the MRV set.
     */
    public static IExpr getRepresentativeG(IAST mrvSet, ISymbol x, EvalEngine engine) {
      if (mrvSet.isEmpty()) {
        return F.NIL;
      }

      try {
        IExpr firstElement = mrvSet.arg1();

        int signElement = signInf(firstElement, x, engine);
        IExpr posElement =
            (signElement == -1) ? engine.evaluate(F.Negate(firstElement)) : firstElement;

        IExpr g = getLog(posElement, engine);

        int sign = signInf(g, x, engine);

        if (!F.isPresent(sign)) {
          // If we cannot rigorously prove that the decay variable w = Exp(-g) -> 0,
          // the series expansion will evaluate over mathematically invalid bounds.
          return F.NIL;
        }
        if (sign == -1) {
          return engine.evaluate(F.Negate(g));
        }
        return g;
      } catch (RuntimeException e) {
        return F.NIL;
      }
    }

    /**
     * An exponential <code>E^f</code> whose exponent has an APPARENT <code>w</code>-pole (e.g.
     * <code>E^(1/w - Log(1+w)/w^2)</code>) defeats seriesDataRecursive even when the singular
     * parts cancel. Where the exponent's own series turns out regular (minExponent &gt;= 0),
     * substitute its truncated normal form so the outer series composition succeeds.
     */
    private static IExpr preExpandExpExponents(IExpr rewritten, ISymbol w, EvalEngine engine) {
      final ISymbol wf = w;
      IExpr result = F.subst(rewritten, e -> {
        if (e.isExp()) {
          IExpr f = e.exponent();
          if (f.has(p -> p.isPower() && p.base().equals(wf)
              && p.exponent().isNegativeResult(), true)) {
            boolean oldInSeries = IN_GRUNTZ_SERIES.get();
            IN_GRUNTZ_SERIES.set(Boolean.TRUE);
            try {
              ASTSeriesData fs =
                  ASTSeriesData.seriesDataRecursive(f, wf, F.C0, 4, -1, engine);
              if (fs != null && fs.minExponent() >= 0) {
                return F.Exp(engine.evaluate(fs.normal(false)));
              }
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            } finally {
              IN_GRUNTZ_SERIES.set(oldInSeries);
            }
          }
        }
        return F.NIL;
      });
      return result.isPresent() ? result : rewritten;
    }

    /**
     * Structural leading-power extraction for the decay variable <code>w -> 0+</code>: returns
     * <code>{exponent, coefficient}</code> such that <code>e ~ coefficient * w^exponent</code>,
     * treating every non-<code>w</code> symbol as a nonzero constant. Exponents may be irrational
     * (e.g. <code>1-Log(5)/Log(3)</code>) and are compared numerically. Conservative: returns
     * <code>null</code> for any shape it cannot analyze soundly (the caller must then give up
     * rather than risk a wrong value).
     */
    private static IExpr[] leadingWPower(IExpr e, ISymbol w, EvalEngine engine) {
      if (e.isFree(w)) {
        return e.isZero() ? null : new IExpr[] {F.C0, e};
      }
      if (e.equals(w)) {
        return new IExpr[] {F.C1, F.C1};
      }
      if (e.isPower()) {
        IExpr base = e.base();
        IExpr exponent = e.exponent();
        if (exponent.isFree(w)) {
          if (base.equals(w)) {
            return new IExpr[] {exponent, F.C1};
          }
          IExpr[] b = leadingWPower(base, w, engine);
          if (b == null) {
            return null;
          }
          // (c*w^v)^k = c^k * w^(v*k) (valid on the positive real branch used by Gruntz)
          return new IExpr[] {F.Times(b[0], exponent), F.Power(b[1], exponent)};
        }
        if (base.equals(S.E)) {
          // E^f: only a plain w-power analysis survives if f converges as w->0+
          IExpr[] f = leadingWPower(exponent, w, engine);
          if (f == null) {
            return null;
          }
          double fv;
          try {
            fv = engine.evaluate(f[0]).evalf();
          } catch (RuntimeException rex) {
            return null;
          }
          if (fv > 1.0e-9) {
            return new IExpr[] {F.C0, F.C1}; // f -> 0, E^f -> 1
          }
          if (fv < -1.0e-9) {
            return null; // f diverges: E^f is not a w-power
          }
          return new IExpr[] {F.C0, F.Exp(f[1])}; // f -> constant
        }
        return null;
      }
      if (e.isTimes()) {
        IASTAppendable expSum = F.PlusAlloc(e.argSize());
        IASTAppendable coeffProd = F.TimesAlloc(e.argSize());
        for (int i = 1; i <= e.argSize(); i++) {
          IExpr[] p = leadingWPower(((IAST) e).get(i), w, engine);
          if (p == null) {
            return null;
          }
          expSum.append(p[0]);
          coeffProd.append(p[1]);
        }
        return new IExpr[] {expSum.oneIdentity0(), coeffProd.oneIdentity1()};
      }
      if (e.isPlus()) {
        IExpr minExp = null;
        double minVal = Double.POSITIVE_INFINITY;
        IASTAppendable coeffSum = F.PlusAlloc(e.argSize());
        // first pass: find the minimal exponent numerically
        IExpr[][] parts = new IExpr[e.argSize()][];
        double[] vals = new double[e.argSize()];
        for (int i = 1; i <= e.argSize(); i++) {
          IExpr[] p = leadingWPower(((IAST) e).get(i), w, engine);
          if (p == null) {
            return null;
          }
          double v;
          try {
            v = engine.evaluate(p[0]).evalf();
          } catch (RuntimeException rex) {
            return null;
          }
          parts[i - 1] = p;
          vals[i - 1] = v;
          if (v < minVal) {
            minVal = v;
            minExp = p[0];
          }
        }
        for (int i = 0; i < parts.length; i++) {
          if (vals[i] < minVal + 1.0e-9) {
            coeffSum.append(parts[i][1]);
          }
        }
        IExpr coefficient = engine.evaluate(coeffSum.oneIdentity0());
        if (coefficient.isZero()) {
          return null; // leading terms cancel - deeper analysis needed than we can do here
        }
        return new IExpr[] {minExp, coefficient};
      }
      if (e.isLog()) {
        IExpr[] f = leadingWPower(e.first(), w, engine);
        if (f == null) {
          return null;
        }
        double fv;
        try {
          fv = engine.evaluate(f[0]).evalf();
        } catch (RuntimeException rex) {
          return null;
        }
        if (Math.abs(fv) <= 1.0e-9) {
          return new IExpr[] {F.C0, F.Log(f[1])}; // Log of a finite nonzero limit
        }
        return null; // Log diverges logarithmically - not a plain w-power
      }
      return null;
    }

    public static IExpr logCombine(IExpr expr) {
      return logCombine(expr, false, null);
    }

    public static IExpr logCombine(IExpr expr, boolean force) {
      return logCombine(expr, force, null);
    }

    // Recursive AST traversal to find and combine Plus structures anywhere
    public static IExpr logCombine(IExpr expr, boolean force, ISymbol x) {
      if (expr.isPlus()) {
        IExpr combined = combinePlusLogs((IAST) expr, force, x);
        if (combined != expr && combined.isAST()) {
          return mapLogCombine((IAST) combined, force, x);
        }
        return combined;
      } else if (expr.isAST()) {
        return mapLogCombine((IAST) expr, force, x);
      }
      return expr;
    }

    private static IExpr mapLogCombine(IAST ast, boolean force, ISymbol x) {
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      boolean changed = false;
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr arg = logCombine(ast.get(i), force, x);
        if (arg != ast.get(i)) {
          changed = true;
        }
        result.append(arg);
      }
      return changed ? F.eval(result) : ast;
    }

    /**
     * Finds the Most Rapidly Varying (MRV) set of subexpressions as x -> Infinity.
     */
    public static IExpr mrv(IExpr expr, ISymbol x, EvalEngine engine) {
      if (expr.isFree(x)) {
        return F.NIL;
      }

      if (expr.equals(x)) {
        return F.List(x);
      }

      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        if (ast.isValidBuiltInFunction()) {
          IExpr head = ast.head();
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Derivative:
            case ID.Integrate:
            case ID.Limit:
            case ID.Sum:
            case ID.O:
            case ID.Product:
              // Do not traverse into scoping constructs or limits to avoid recursive evaluation
              // traps
              return F.NIL;
            case ID.Plus:
            case ID.Times: {
              IExpr currentMrv = F.NIL;
              for (int i = 1; i <= ast.argSize(); i++) {
                IExpr argMrv = mrv(ast.get(i), x, engine);
                currentMrv = mrvMax(currentMrv, argMrv, x, engine);
              }
              return currentMrv;
            }

            case ID.Power: {
              IExpr base = ast.base();
              IExpr exponent = ast.exponent();

              if (base.equals(S.E)) {
                IExpr argMrv = mrv(exponent, x, engine);
                // Gruntz restriction: Exp(f) is only rapidly varying if f diverges.
                // For Log/Exp-bearing exponents decide via the Gruntz limit itself (sympy's
                // mrv calls limitinf here, not a heuristic engine): tower exponents whose
                // heuristic limit grinds for the whole time budget (thesis 8.20's log-nest
                // ratio) resolve in milliseconds this way. Plain polynomial exponents -
                // including the E^u artifacts the moveup substitution itself creates - MUST
                // use the cheap heuristic path, otherwise evalGruntz(x) -> moveup E^u ->
                // evalGruntz(u) -> ... recurses forever (observed as StackOverflow).
                IExpr argLimit = F.NIL;
                if (exponent.has(t -> t.isLog() || t.isExp(), true)
                    && MRV_EXP_GRUNTZ_DEPTH.get() < 20) {
                  MRV_EXP_GRUNTZ_DEPTH.set(MRV_EXP_GRUNTZ_DEPTH.get() + 1);
                  try {
                    argLimit = evalGruntz(exponent, x, engine);
                  } finally {
                    MRV_EXP_GRUNTZ_DEPTH.set(MRV_EXP_GRUNTZ_DEPTH.get() - 1);
                  }
                }
                if (argLimit.isNIL()) {
                  LimitData limitData =
                      new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
                  argLimit = evalLimitQuiet(exponent, limitData);
                }
                if (argLimit.isInfinity() || argLimit.isNegativeInfinity()
                    || argLimit.isDirectedInfinity()) {
                  IExpr expSet = F.List(ast);
                  return mrvMax(expSet, argMrv, x, engine);
                }
                return argMrv;
              }

              if (exponent.isFree(x)) {
                return mrv(base, x, engine);
              }

              if (base.isFree(x)) {
                // To bypass evaluation loops, mathematically treat base^exponent directly
                // as its expansion E^(exponent * Log(base)) without writing it to an AST.
                IExpr argMrv = mrv(exponent, x, engine);
                IExpr expSet = F.List(ast);
                return mrvMax(expSet, argMrv, x, engine);
              }

              try {
                IExpr logExpr = engine.evaluate(F.Times(exponent, F.Log(base)));
                IExpr argMrv = mrv(logExpr, x, engine);
                if (DEBUG) {
                  System.out.println("MRV power-case " + ast + " argMrv=" + argMrv);
                }
                return mrvMax(F.List(ast), argMrv, x, engine);
              } catch (RuntimeException e) {
                if (DEBUG) {
                  System.out.println("MRV power-catch " + ast + " : " + e);
                }
                return F.NIL;
              }
            }

            case ID.Log: {
              return mrv(ast.arg1(), x, engine);
            }

            case ID.Sinh:
            case ID.Cosh:
            case ID.Tanh:
              try {
                // Convert hyperbolics to exponentials so Gruntz can analyze their growth
                IExpr rewritten = engine.evaluate(F.TrigToExp(ast));

                // If the engine couldn't rewrite the expression, abort to
                // prevent an infinite StackOverflow loop.
                if (rewritten.equals(ast)) {
                  return F.NIL;
                }

                return mrv(rewritten, x, engine);
              } catch (RuntimeException e) {
                return F.NIL;
              }
            case ID.Factorial: {
              // x! -> Gamma(x + 1)
              IExpr arg = ast.arg1();
              IExpr rewritten = engine.evaluate(F.Gamma(F.Plus(arg, F.C1)));
              return mrv(rewritten, x, engine);
            }
            case ID.Pochhammer: {
              // Pochhammer(a, b) -> Gamma(a + b) / Gamma(a)
              IExpr a = ast.arg1();
              IExpr b = ast.arg2();
              IExpr rewritten = engine.evaluate(F.Divide(F.Gamma(F.Plus(a, b)), F.Gamma(a)));
              return mrv(rewritten, x, engine);
            }
            case ID.Gamma: {
              // Stirling's Approximation maps Gamma growth strictly to Exp and Log,
              // but is only valid for a divergent argument; otherwise Gamma is a
              // continuous function of its argument and varies exactly as fast.
              IExpr arg = ast.arg1();
              if (!divergesAtInfinity(arg, x)) {
                return mrv(arg, x, engine);
              }
              IExpr stirling =
                  engine.evaluate(F.Times(F.Power(F.Divide(F.Times(F.C2, S.Pi), arg), F.C1D2),
                      F.Exp(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
                          F.Divide(F.C1, F.Times(F.ZZ(12), arg))))));
              return mrv(stirling, x, engine);
            }
            case ID.LogGamma: {
              // Asymptotic expansion of LogGamma isolates polynomial/logarithmic variation;
              // like Stirling it requires a divergent argument.
              IExpr arg = ast.arg1();
              if (!divergesAtInfinity(arg, x)) {
                return mrv(arg, x, engine);
              }
              IExpr stirling = engine.evaluate(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
                  F.Times(F.C1D2, F.Log(F.Divide(F.Times(F.C2, S.Pi), arg))),
                  F.Divide(F.C1, F.Times(F.ZZ(12), arg))));
              return mrv(stirling, x, engine);
            }
            case ID.PolyGamma: {
              // Digamma asymptotics: PolyGamma(0, z) ~ Log(z) - 1/(2z) for a divergent
              // argument free of nested PolyGamma (un-expanded psi-in-psi towers grind;
              // Log-bearing arguments like PolyGamma(0, Log(x)) are handled fine since the
              // compareGrowth/logw hardening). Without the substitution the function is
              // treated as an opaque constant and E^PolyGamma(0,x)/x wrongly collapses to 0.
              if (ast.argSize() == 2 && ast.arg1().isZero()
                  && ast.arg2().isFree(t -> t.isAST(S.PolyGamma), true)) {
                IExpr arg = ast.arg2();
                if (!divergesAtInfinity(arg, x)) {
                  return mrv(arg, x, engine);
                }
                IExpr digamma = engine.evaluate(
                    F.Plus(F.Log(arg), F.Negate(F.Divide(F.C1, F.Times(F.C2, arg)))));
                return mrv(digamma, x, engine);
              }
              IExpr currentMrvPG = F.NIL;
              for (int i = 1; i <= ast.argSize(); i++) {
                IExpr argMrv = mrv(ast.get(i), x, engine);
                currentMrvPG = mrvMax(currentMrvPG, argMrv, x, engine);
              }
              return currentMrvPG;
            }
            default:
              IExpr currentMrv = F.NIL;
              for (int i = 1; i <= ast.argSize(); i++) {
                IExpr argMrv = mrv(ast.get(i), x, engine);
                currentMrv = mrvMax(currentMrv, argMrv, x, engine);
              }
              return currentMrv;
          }
        }
      }

      return F.NIL;
    }

    private static IExpr mrvMax(IExpr mrv1, IExpr mrv2, ISymbol x, EvalEngine engine) {
      if (!mrv1.isPresent() || mrv1.isNIL())
        return mrv2;
      if (!mrv2.isPresent() || mrv2.isNIL())
        return mrv1;

      IExpr f = ((IAST) mrv1).arg1();
      IExpr g = ((IAST) mrv2).arg1();

      int cmp = compareGrowth(f, g, x, engine);

      if (cmp == 1) {
        return mrv1;
      } else if (cmp == -1) {
        return mrv2;
      } else {
        IASTAppendable merged = F.ListAlloc();
        merged.appendArgs((IAST) mrv1);
        merged.appendArgs((IAST) mrv2);
        return merged;
      }
    }

    private static IExpr replaceLogStirling(IExpr expr, ISymbol x, EvalEngine engine) {
      if (expr.isFree(x)) {
        return expr;
      }
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IExpr head = ast.head();

        // Match Log(Gamma(...)), Log(Factorial(...)), Log(Pochhammer(...))
        if (head.equals(S.Log) && ast.arg1().isAST()) {
          IAST innerAst = (IAST) ast.arg1();
          switch (innerAst.validHeadID()) {
            case ID.Factorial: {
              // Log(x!) -> Log(Gamma(x+1))
              IExpr arg = replaceLogStirling(innerAst.arg1(), x, engine);
              return replaceLogStirling(engine.evaluate(F.Log(F.Gamma(F.Plus(arg, F.C1)))), x,
                  engine);
            }
            case ID.Pochhammer: {
              // Log(Pochhammer(a, b)) -> Log(Gamma(a+b)) - Log(Gamma(a))
              IExpr a = replaceLogStirling(innerAst.arg1(), x, engine);
              IExpr b = replaceLogStirling(innerAst.arg2(), x, engine);
              return engine
                  .evaluate(F.Subtract(replaceLogStirling(F.Log(F.Gamma(F.Plus(a, b))), x, engine),
                      replaceLogStirling(F.Log(F.Gamma(a)), x, engine)));
            }
            case ID.Gamma: {
              // Log(Gamma(z)) ~ z*Log(z) - z + (1/2)*Log(2*Pi/z) + 1/(12*z)
              // (Stirling - only valid for a divergent argument)
              IExpr arg = replaceLogStirling(innerAst.arg1(), x, engine);
              if (!divergesAtInfinity(arg, x)) {
                return F.Log(F.Gamma(arg));
              }
              return engine.evaluate(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
                  F.Times(F.C1D2, F.Log(F.Divide(F.Times(F.C2, S.Pi), arg))),
                  F.Divide(F.C1, F.Times(F.ZZ(12), arg))));
            }
          }
        } else if (head.equals(S.LogGamma)) {
          IExpr arg = replaceLogStirling(ast.arg1(), x, engine);
          if (!divergesAtInfinity(arg, x)) {
            return F.LogGamma(arg);
          }
          return engine.evaluate(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
              F.Times(F.C1D2, F.Log(F.Divide(F.Times(F.C2, S.Pi), arg))),
              F.Divide(F.C1, F.Times(F.ZZ(12), arg))));
        } else if (head.equals(S.PolyGamma) && ast.argSize() == 2 && ast.arg1().isZero()) {
          // Digamma: PolyGamma(0, z) ~ Log(z) - 1/(2z) for a divergent argument free of
          // nested PolyGamma (the arg was already recursed, so psi(psi(x)) arrives here
          // with the inner level expanded; Log-bearing args are fine - see mrv PolyGamma)
          IExpr arg = replaceLogStirling(ast.arg2(), x, engine);
          if (!divergesAtInfinity(arg, x)
              || !arg.isFree(t -> t.isAST(S.PolyGamma), true)) {
            return F.PolyGamma(F.C0, arg);
          }
          return engine
              .evaluate(F.Plus(F.Log(arg), F.Negate(F.Divide(F.C1, F.Times(F.C2, arg)))));
        }

        // Map across standard AST nodes
        IASTAppendable result = F.ast(head, ast.argSize());
        for (int i = 1; i <= ast.argSize(); i++) {
          result.append(replaceLogStirling(ast.get(i), x, engine));
        }
        return engine.evaluate(result);
      }
      return expr;
    }

    /**
     * Recursively rewrites the expression E in terms of the decay variable w.
     */
    public static IExpr rewrite(IExpr expr, IAST mrvSet, IExpr g, ISymbol x, ISymbol w,
        EvalEngine engine) {
      if (expr.isFree(x)) {
        return expr;
      }

      if (mrvSet.contains(expr)) {
        try {
          IExpr f = getLog(expr, engine);
          IExpr ratio = engine.evaluate(F.Divide(f, g));

          IExpr c;
          if (ratio.isFree(x)) {
            c = ratio;
          } else {
            // Every mrv element spawns a full heuristic limit evaluation here; on nested
            // Gamma/exponential towers those evaluations re-enter Gruntz and this rewrite,
            // stacking a whole engine per tower level until StackOverflowError. Cap the
            // nesting - an uncomputed c just leaves the node unrewritten (graceful NIL
            // upstream).
            int ratioDepth = REWRITE_RATIO_DEPTH.get();
            if (ratioDepth >= 4) {
              return expr;
            }
            LimitData limitData =
                new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
            REWRITE_RATIO_DEPTH.set(ratioDepth + 1);
            try {
              c = evalLimitQuiet(ratio, limitData);
            } finally {
              REWRITE_RATIO_DEPTH.set(ratioDepth);
            }
          }

          if (c.isIndeterminate() || c.isNIL() || !c.isFree(S.Limit, true)
              || !c.isFree(S.Derivative, true) || !c.isFree(x)) {
            return expr;
          }

          IExpr remainder = engine.evaluate(F.Subtract(f, F.Times(c, g)));
          IExpr rewrittenRemainder = rewrite(remainder, mrvSet, g, x, w, engine);

          IExpr wPart = engine.evaluate(F.Power(w, F.Negate(c)));
          IExpr expRemainder = engine.evaluate(F.Exp(rewrittenRemainder));

          return engine.evaluate(F.Times(wPart, expRemainder));
        } catch (RuntimeException e) {
          return expr;
        }
      }

      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IExpr head = ast.head();

        if (head.equals(S.Limit) || head.equals(S.Derivative) || head.equals(S.Integrate)
            || head.equals(S.Sum) || head.equals(S.Product) || head.equals(S.O)) {
          return expr;
        }

        IASTAppendable rewrittenAST = F.ast(head, ast.argSize());
        for (int i = 1; i <= ast.argSize(); i++) {
          rewrittenAST.append(rewrite(ast.get(i), mrvSet, g, x, w, engine));
        }

        try {
          return engine.evaluate(rewrittenAST);
        } catch (RuntimeException e) {
          return expr;
        }
      }

      return expr;
    }

    /**
     * Determines the asymptotic sign of an expression as x -> Infinity, memoized within the
     * current top-level Gruntz run (see {@link #SIGN_INF_CACHE}).
     */
    public static int signInf(IExpr expr, ISymbol x, EvalEngine engine) {
      if (expr.isFree(x)) {
        if (expr.isZero())
          return 0;
        if (engine.evaluate(F.Greater(expr, F.C0)).isTrue())
          return 1;
        if (engine.evaluate(F.Less(expr, F.C0)).isTrue())
          return -1;
        return Integer.MIN_VALUE;
      }

      Map<String, Integer> cache = SIGN_INF_CACHE.get();
      String key = expr.toString();
      Integer cached = cache.get(key);
      if (cached != null) {
        // A definitive cached sign is always reusable. A cached "unknown" (MIN_VALUE) is only
        // reused within the heuristic tier (GRUNTZ_DEPTH <= 2): once we are deep enough to use
        // the numeric sample-point path (see signInfUncached), it can resolve a sign the
        // heuristic could not, so let it retry rather than serve the stale unknown. This kills
        // the dominant cost on tower differences (observed: the same messy 0-limit sign query
        // recomputed 150+ times at ~150ms each) without blocking a better later attempt.
        if (F.isPresent(cached) || GRUNTZ_DEPTH.get() <= 2) {
          return cached;
        }
      }
      int result = signInfUncached(expr, x, engine);
      cache.put(key, result);
      return result;
    }

    /**
     * Determines the asymptotic sign of an expression as x -> Infinity.
     */
    private static int signInfUncached(IExpr expr, ISymbol x, EvalEngine engine) {
      try {
        // Prevent circular dependency recursion between Gruntz and Limit!
        // If we are already deep inside the Gruntz algorithm, do NOT spawn a new
        // deep Limit evaluation just to find the sign of a wildly oscillating expression.
        if (GRUNTZ_DEPTH.get() > 2) {
          // Test progressively smaller sample points. Exponentials like E^10000 or E^E^100
          // will throw RecursionLimitExceeded or Arithmetic overflows. We catch these
          // and degrade to smaller numbers until we safely resolve the asymptotic sign.
          int[] samplePoints = {10000, 100, 10, 3};

          for (int s : samplePoints) {
            try {
              IExpr sample = engine.evalQuiet(F.subst(expr, x, F.ZZ(s)));
              IExpr nSample = engine.evaluate(F.N(sample));

              if (nSample.isNumericFunction(true)) {
                if (engine.evaluate(F.Greater(nSample, F.C0)).isTrue())
                  return 1;
                if (engine.evaluate(F.Less(nSample, F.C0)).isTrue())
                  return -1;
                // If it evaluates cleanly but is exactly 0, smaller samples won't help
                break;
              }
            } catch (RuntimeException rex) {
              // Mathematical overflow or recursion limit exceeded due to massive exponentials.
              // Safely ignore the crash and loop to try the next smaller sample point.
            }
          }
          return Integer.MIN_VALUE;
        }

        LimitData limitData =
            new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
        IExpr limitResult = evalLimitQuiet(expr, limitData);

        if (limitResult.isInfinity())
          return 1;
        if (limitResult.isNegativeInfinity())
          return -1;

        if (limitResult.isNumericFunction(true)) {
          if (engine.evaluate(F.Greater(limitResult, F.C0)).isTrue())
            return 1;
          if (engine.evaluate(F.Less(limitResult, F.C0)).isTrue())
            return -1;

          // Safely extract the bounds of an Interval to rigorously prove its sign
          if (limitResult.isInterval()) {
            IAST intervalAST = (IAST) limitResult;
            return IntervalSym.sign(intervalAST, engine); // intervals that straddle 0 have an
            // indeterminate sign
          } else if (limitResult.isIntervalData()) {
            IAST intervalAST = (IAST) limitResult;
            return IntervalDataSym.sign(intervalAST, engine);
          }

          if (limitResult.isZero()) {
            // Symbolic Fast-Paths to prevent numeric underflow of extreme exponentials (e.g.
            // E^-10000)
            if (expr.isExp())
              return 1;

            if (expr.isTimes()) {
              int s = 1;
              for (int i = 1; i <= expr.argSize(); i++) {
                int partSign = signInf(((IAST) expr).get(i), x, engine);
                if (!F.isPresent(partSign) || partSign == 0) {
                  s = Integer.MIN_VALUE;
                  break;
                }
                s *= partSign;
              }
              if (F.isPresent(s))
                return s;
            }

            if (expr.isPower()) {
              int baseSign = signInf(expr.base(), x, engine);
              if (baseSign == 1)
                return 1;
            }

            // Direct evaluation heuristic
            IExpr sample = engine.evalQuiet(F.subst(expr, x, F.ZZ(10000)));
            IExpr nSample = engine.evaluate(F.N(sample));
            if (nSample.isNumericFunction(true)) {
              if (engine.evaluate(F.Greater(nSample, F.C0)).isTrue())
                return 1;
              if (engine.evaluate(F.Less(nSample, F.C0)).isTrue())
                return -1;
            }

            // Leading Term fallback heuristic
            IExpr lt = ASTSeriesData.leadingTerm(expr, x, F.CInfinity, engine);
            if (lt.isPresent() && !lt.isNIL()) {
              if (lt.isExp())
                return 1;

              sample = engine.evalQuiet(F.subst(lt, x, F.ZZ(10000)));
              nSample = engine.evaluate(F.N(sample));
              if (nSample.isNumericFunction(true)) {
                if (engine.evaluate(F.Greater(nSample, F.C0)).isTrue())
                  return 1;
                if (engine.evaluate(F.Less(nSample, F.C0)).isTrue())
                  return -1;
              }
            }
            // Directional derivative
            IExpr derivative = engine.evaluate(F.D(expr, x));
            IExpr derivLimit = evalLimitQuiet(derivative, limitData);

            if (engine.evaluate(F.Less(derivLimit, F.C0)).isTrue()
                || derivLimit.isNegativeInfinity())
              return 1;
            else if (engine.evaluate(F.Greater(derivLimit, F.C0)).isTrue()
                || derivLimit.isInfinity())
              return -1;
          }
        }
        return Integer.MIN_VALUE;
      } catch (RuntimeException e) {
        return Integer.MIN_VALUE;
      }
    }
  }

  /** Representing the data for the current limit. */
  private static class LimitData {
    private final ISymbol variable;

    final private IExpr limitValue;

    /** The rule <code>variable->limitValue</code>. */
    final private IAST rule;

    final private Direction direction;

    public LimitData(ISymbol variable, IExpr limitValue, IAST rule, Direction direction) {
      this.variable = variable;
      this.limitValue = limitValue;
      this.rule = rule;
      this.direction = direction;
    }

    /**
     * Examples: * *
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * * * *
     * 
     * <pre>
     * Limit(x ^ 2 * Sin(1 / x) ^ 3, x -> 0)
     * </pre>
     * 
     * * * @param x * @return
     */
    private boolean determineCosSinCases(IExpr x) {
      if (x.isPower()) {
        int exponent = x.exponent().toIntDefault();
        if (exponent > 0) {
          return determineCosSinCases(x.base());
        }
      }
      return (x.isSin() || x.isCos()) && !x.isFree(variable);
    }

    /**
     * Get the optional direction value. Default is DIRECTION_TWO_SIDED = 0.
     *
     */
    public Direction direction() {
      return direction;
    }

    /**
     * Create a new <code>F.Limit( arg1, ... )</code> expression from this <code>LimitData</code>
     * object
     *
     * @param arg1 the first argument of the Limit expression
     * @return a new <code>F.Limit( arg1, ... )</code> expression
     */
    public IExpr limit(IExpr arg1) {
      return evalLimitQuiet(arg1, this);
    }

    /**
     * Get the limit value of the limit definition <code>variable->limitValue</code>
     */
    public IExpr limitValue() {
      return limitValue;
    }

    /**
     * Map a <code>F.Limit( arg1, ... )</code> expression at each argument of the given <code>ast
     * </code>.
     *
     * @param ast the expression to map the limit on
     */
    public IExpr mapLimit(final IAST ast) {
      IASTMutable result = ast.copy();
      boolean isIndeterminate = false;
      boolean isLimit = false;
      for (int i = 1; i < ast.size(); i++) {
        IExpr temp = evalLimitQuiet(ast.get(i), this);
        if (!temp.isPresent()) {
          // FIX: If any argument's limit fails, the mapped limit fails.
          // Do not insert F.NIL into the AST!
          return F.NIL;
        }
        if (!temp.isFree(S.Limit)) {
          isLimit = true;
        } else if (temp.isIndeterminate()) {
          isIndeterminate = true;
        }
        result.set(i, temp);
      }
      if (isLimit && isIndeterminate) {
        return F.NIL;
      }
      if (isIndeterminate && limitValue.isZero() && ast.isTimes()) {
        return squeezeTheorem(ast).orElse(result);
      }
      IExpr evaledResult = EvalEngine.get().evaluate(result);
      // Convert unresolved oscillating Intervals to Indeterminate
      return IntervalSym.toAccumBoundsIndeterminate(evaledResult);
    }

    public IAST rule() {
      return rule;
    }

    /**
     * Try the squeeze theorem: <a href="https://en.wikipedia.org/wiki/Squeeze_theorem">Wikipedia -
     * Squeeze theorem</a>. It is assumed that {@link #limitValue} equals <code>0</code>.
     * <p>
     * Example: * *
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * * * @param ast * @param defaultResult
     * 
     * @return <code>F.Times(F.C0)</code> or {@link F#NIL} if squeeze theorem was not applicable.
     */
    private IAST squeezeTheorem(final IAST ast) {
      IASTAppendable[] cosSinFilter = ast.filter(x -> determineCosSinCases(x));
      if (cosSinFilter != null //
          && cosSinFilter[0].argSize() > 0 //
          && cosSinFilter[1].argSize() > 0 //
          && !cosSinFilter[0].isOne() //
          && !cosSinFilter[1].isOne()) {
        IExpr temp = evalLimitQuiet(F.Abs(cosSinFilter[1]), this);
        if (temp.isZero()) {
          temp = evalLimitQuiet(F.Abs(cosSinFilter[0]), this);
          if (temp.isIndeterminate()) {
            return F.Times(F.C0);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Get the <code>variable</code> of the limit definition <code>variable->limitValue</code>
     */
    public ISymbol variable() {
      return variable;
    }
  }

  private final static boolean DEBUG = false;

  /**
   * Minimum leaf count of an expression before {@link #evalLimit} attempts an algebraic
   * {@code Simplify} to escape a combinatorial blow-up of the limit heuristics / Gruntz algorithm
   * (see issue #1420). Kept high enough that ordinary small limit expressions are unaffected.
   */
  private final static int LIMIT_SIMPLIFY_LEAFCOUNT = 30;

  /**
   * Guards the hyperbolic-to-exponential retry in {@link #evalLimit} against re-entry: the
   * rewritten form is fed back through {@code evalLimit}, and a later {@code Simplify} step could in
   * principle turn it back into a hyperbolic function and loop. We only ever rewrite once per
   * evaluation stack.
   */
  private static final ThreadLocal<Boolean> HYPERBOLIC_EXP_RETRY =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Depth guard for the Gamma pole-shift retry in {@link #evaluateLimit}: the shift
   * <code>Gamma(z) -> Gamma(z+m+1)/(z...(z+m))</code> and FunctionExpand's opposite rewrite
   * <code>Gamma(1+z) -> z*Gamma(z)</code> would otherwise ping-pong through recursive limit
   * evaluations forever. A small cap still allows legitimately nested shifts.
   */
  private static final ThreadLocal<Integer> GAMMA_POLE_SHIFT_DEPTH =
      ThreadLocal.withInitial(() -> 0);

  /**
   * True while an {@link #lHospitalesRule} application holds the relative recursion budget -
   * nested applications must not extend the ceiling again (see the comment inside the method).
   */
  private static final ThreadLocal<Boolean> LHOSPITAL_BUDGET_ACTIVE =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /** One-shot guard for the Together retry of an Indeterminate finite-point difference limit. */
  private static final ThreadLocal<Boolean> TOGETHER_LIMIT_RETRY =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Evaluate the limit for the given limit data.
   *
   * @param expr
   * @param data the limits data definition
   * @param engine
   * @return {@link S#NIL} if no limit could be found
   */
  private static IExpr evalLimit(IExpr evaledExpr, LimitData data, EvalEngine engine) {
    final ISymbol symbol = data.variable();
    final IExpr limitValue = data.limitValue();

    // Android-changed: do not use shared EvalEngine
    if (engine == null) {
      engine = EvalEngine.get();
    }

    // Limit[RootSum[f, g], x -> x0] with finite x0 and a root polynomial f free of x is the direct
    // substitution RootSum[f, g /. x -> x0]; this is what Integrate uses to evaluate a RootSum
    // antiderivative at the bounds of a definite integral.
    IExpr rootSumLimit = limitRootSum(evaledExpr, data, engine);
    if (rootSumLimit.isPresent()) {
      return rootSumLimit;
    }

    if (data.direction() == Direction.TWO_SIDED && !limitValue.isDirectedInfinity()) {
      return evalLimitTwoSided(evaledExpr, data, engine);
    }

    // --- OSCILLATING SPECIAL FUNCTIONS AT NEGATIVE INFINITY ---
    // Gamma, Factorial, and PolyGamma have poles at every negative integer.
    // As they approach -Infinity, they oscillate wildly and their limits are Indeterminate.
    // Sin and Cos oscillate without bound as their argument approaches ±Infinity.
    // We must intercept them here to prevent the series evaluator from endlessly
    // differentiating them into a fractal recursion explosion.
    boolean hasOscillatingSpecial = isOscillatingSpecial(evaledExpr, symbol, limitValue, data);
    if (hasOscillatingSpecial) {
      return S.Indeterminate;
    }

    if (limitValue.isInfinity()) {
      evaledExpr = F.subst(evaledExpr, x -> {
        if (x.isAbs() && x.first().equals(data.variable())) {
          return data.variable();
        }
        if (x.isAST(S.Sign, 2) && x.first().equals(data.variable())) {
          return F.C1;
        }
        return F.NIL;
      });
    } else if (limitValue.isNegativeInfinity()) {
      evaledExpr = F.subst(evaledExpr, x -> {
        if (x.isAbs() && x.first().equals(data.variable())) {
          return data.variable().negate();
        }
        if (x.isAST(S.Sign, 2) && x.first().equals(data.variable())) {
          return F.CN1;
        }
        return F.NIL;
      });
    }

    // Hyperbolic functions (Cosh/Sinh/Tanh/...) confuse the downstream +/-Infinity heuristics
    // (e.g. 2*Cosh(x)*E^x -> Indeterminate, which also poisons signInf and the Gruntz fallback).
    // At an infinite limit, rewrite them to exponentials up front and retry via a recursive
    // evalLimit on the expanded form (2*Cosh(x)*E^x -> E^(2*x)+1 -> Infinity). Only adopt the
    // result if it fully resolves, so cases the normal path already handles fall through
    // unchanged; the one-shot HYPERBOLIC_EXP_RETRY guard keeps the single rewrite loop-proof.
    if (!HYPERBOLIC_EXP_RETRY.get() && (limitValue.isInfinity() || limitValue.isNegativeInfinity())
        && evaledExpr.has(
            y -> y.isFunctionID(ID.Sinh, ID.Cosh, ID.Tanh, ID.Coth, ID.Sech, ID.Csch)
                // only the oscillation cases (argument depends on the limit variable) need the
                // exp rewrite; a hyperbolic CONSTANT like Sinh(1) (from e.g. 3*Sin(I)) must stay
                // intact - TrigToExp would blow it into a (E-1/E) form the result never
                // re-simplifies (issue #42: y*3*Sin(I)*x)
                && !((IAST) y).arg1().isFree(data.variable(), true),
            true)) {
      IExpr expForm = engine.evalQuiet(F.ExpandAll(F.TrigToExp(evaledExpr)));
      if (expForm.isPresent() && !expForm.equals(evaledExpr)) {
        HYPERBOLIC_EXP_RETRY.set(Boolean.TRUE);
        try {
          IExpr hypTemp = evalLimit(expForm, data, engine);
          if (hypTemp.isPresent() && hypTemp.isFree(S.Limit) && hypTemp.isIndeterminateFree()) {
            return hypTemp;
          }
        } finally {
          HYPERBOLIC_EXP_RETRY.set(Boolean.FALSE);
        }
      }
    }

    IExpr result = engine.evalQuiet(evaledExpr);
    if (result.isNumericFunction(true)) {
      return result;
    }
    if (!result.isIndeterminate()) {
      evaledExpr = result;
    }
    if (result.isFree(data.variable(), true)) {
      // Limit[a_,sym->lim] -> a
      return evaledExpr;
    }
    if (result.equals(data.variable())) {
      // Limit[x_,x_->lim] -> lim
      return limitValue;
    }
    if (DEBUG) {
      System.out.println("Evaluating limit of " + evaledExpr + " as " + data.variable()
          + " approaches " + limitValue);
    }
    // A power f(x)^g(x) with a variable base (not E) and variable exponent drives the
    // L'Hopital heuristics into an unbounded derivative explosion - (1+1/x)^(x^2) burned
    // whole time budgets - while the Gruntz algorithm resolves such shapes in about a second
    // when it can and fails fast when it cannot. Consult Gruntz FIRST for exactly this
    // shape, but only at the TOP level: the algorithm's own sub-limits re-entering this gate
    // multiply the cost combinatorially (observed 1s -> 49s). This must run BEFORE the
    // substitution fast paths below: limitInfinityZero's argument-limit already starts the
    // L'Hopital burn (observed on thesis 8.20's E^(log-nest ratio)).
    if ((limitValue.isInfinity() || limitValue.isNegativeInfinity())
        && GruntzLimit.GRUNTZ_DEPTH.get() == 0 && !GruntzLimit.IN_GRUNTZ_SERIES.get()
        && (evaledExpr.has(p -> p.isPower() && !p.base().equals(S.E)
            && !p.base().isFree(symbol, true) && !p.exponent().isFree(symbol, true)
            // oscillatory bases like (Sin(1/x)/2)^(1/x^2) belong to the envelope
            // machinery; the Gruntz mrv/rewrite chain grinds on them
            && p.base().isFree(
                t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc), true),
            true)
            // log-nests like Log(Log(x)+Log(Log(x))) (thesis 8.19/8.20) equally starve the
            // L'Hopital heuristics while Gruntz handles them via repeated moveup; Gamma-family
            // expressions are excluded - their Stirling preprocessing produces log-sums that
            // route better through the established Gamma pipeline
            || (evaledExpr.has(t -> t.isLog() && t.first().isPlus()
                && !t.first().isFree(symbol, true)
                && ((IAST) t.first()).exists(a -> a.has(s -> s.isLog(), true)), true)
                && evaledExpr.isFree(t -> t.isFunctionID(ID.Gamma, ID.LogGamma, ID.Factorial,
                    ID.Pochhammer, ID.PolyGamma), true))
            // exponential towers E^f with a Log-bearing exponent (thesis 8.9's
            // Log(x)^2*E^(Sqrt(Log(x))*...)/Sqrt(x)) sit between power growth classes -
            // the heuristic Times path collapses them to Indeterminate via oo*oo*0
            || (evaledExpr.has(p -> p.isExp() && !p.exponent().isFree(symbol, true)
                && p.exponent().has(t -> t.isLog(), true)
                && p.exponent().isFree(
                    t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc), true),
                true)
                && evaledExpr.isFree(t -> t.isFunctionID(ID.Gamma, ID.LogGamma, ID.Factorial,
                    ID.Pochhammer, ID.PolyGamma), true)))
        && evaledExpr.isNumericFunction(new VariablesSet(evaledExpr))) {
      IExpr gruntzFirst = GruntzLimit.evaluateLimit(evaledExpr, symbol, limitValue,
          data.direction(), engine);
      if (gruntzFirst.isPresent() && gruntzFirst.isFree(S.Limit)
          && gruntzFirst.isIndeterminateFree() && !hasNestedDirectedInfinity(gruntzFirst)) {
        return gruntzFirst;
      }
    }

    if (limitValue.isNumericFunction(true) && evaledExpr.isFree(x -> x == S.Piecewise, true)) {
      IExpr temp = evalReplaceAll(evaledExpr, data, engine);
      if (temp.isPresent()) {
        return temp;
      }
    } else if ((limitValue.isInfinity() || limitValue.isNegativeInfinity()) && evaledExpr.isAST()
        && evaledExpr.size() > 1) {
      if (limitValue.isInfinity() || limitValue.isNegativeInfinity()) {
        IExpr temp = evalReplaceAll(evaledExpr, data, engine);
        if (temp.isNumericFunction(true)) {
          return temp;
        }
        if (evaledExpr.isNumericFunction(data.variable()) && evaledExpr.size() > 1
            && !evaledExpr.isPlusTimesPower()) {
          temp = limitNumericFunctionArgs((IAST) evaledExpr, data, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      IExpr temp = limitInfinityZero((IAST) evaledExpr, data, (IAST) limitValue);
      if (temp.isPresent()) {
        return temp;
      }
    }

    // Large "0 * Infinity" style forms (e.g. issue #1420) can drive the naive limit heuristics
    // and the Gruntz algorithm into a combinatorial explosion, while an algebraic Simplify
    // reduces them to a form whose limit resolves directly. Once the cheap fast paths above
    // have declined, retry on a simplified form when it is strictly smaller; the strict
    // leaf-count decrease guarantees this recursion terminates. Never Simplify when a
    // circular trig function of the limit variable is present: Simplify then runs
    // TrigSimplifyFu/TrigExpand whose output explodes combinatorially even for small inputs
    // (TimeConstrained cannot cut it - it degrades to a plain evaluation in
    // TIMECONSTRAINED_NO_THREAD mode), e.g. the Sin(n*ansatz) Taylor remainders produced by
    // AsymptoticRSolveValue ground for minutes here; the oscillation logic handles trig.
    if (evaledExpr.isAST() && evaledExpr.leafCount() > LIMIT_SIMPLIFY_LEAFCOUNT
        && !evaledExpr.has(t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc)
            && !((IAST) t).isFree(symbol, true), true)) {
      IExpr simplified = engine.evalQuiet(F.Simplify(evaledExpr));
      if (simplified.isPresent() && !simplified.equals(evaledExpr)
          && simplified.leafCount() < evaledExpr.leafCount()) {
        return evalLimit(simplified, data, engine);
      }
    }

    IExpr temp = evalLimitAST(evaledExpr, limitValue, data, engine);
    // Return early if the limit heuristically found a definitive mathematical form
    // including correctly determined Indeterminate bounds (e.g. from branch cuts).
    // A result with a DirectedInfinity nested inside a function argument (e.g. the naive
    // termwise sum Infinity-Log(Infinity+a)) is NOT definitive - let the fallbacks run.
    if (temp.isPresent() && temp.isFree(S.Limit) && temp.isIndeterminateFree()
        && !hasNestedDirectedInfinity(temp)) {
      return temp;
    }

    // 2. If heuristics failed (NIL) OR returned an unresolved Limit or infinity-junk,
    // intercept it and fall back to advanced methods.
    if (temp.isNIL() || !temp.isFree(S.Limit) || !temp.isIndeterminateFree()
        || hasNestedDirectedInfinity(temp)) {

      IExpr expandedExpr = engine.evalQuiet(F.ExpandAll(evaledExpr));
      if (expandedExpr.isPresent() && !expandedExpr.equals(evaledExpr)) {
        IExpr expTemp = evalLimitAST(expandedExpr, limitValue, data, engine);
        if (expTemp.isPresent() && expTemp.isFree(S.Limit)
            && !hasNestedDirectedInfinity(expTemp)) {
          return expTemp;
        }
      }

      // Free symbolic parameters (e.g. Limit(E^x/(a+E^x), x->Infinity)) are legal for the
      // Gruntz algorithm - treat every symbol as a numeric constant when gating.
      if (evaledExpr.isNumericFunction(new VariablesSet(evaledExpr))) {
        IExpr gruntzResult = GruntzLimit.evaluateLimit(evaledExpr, data.variable(),
            data.limitValue(), data.direction(), engine);

        if (gruntzResult.isPresent() && gruntzResult.isFree(S.Limit)) {
          return gruntzResult;
        }
        if (gruntzResult.isPresent() && temp.isNIL()) {
          return gruntzResult;
        }
      }
    }
    if (temp.isPresent()) {
      return temp;
    }
    return F.NIL;
  }

  /**
   * A limit result is junk when a {@link S#DirectedInfinity} ended up nested inside the argument
   * of a non-arithmetic function - e.g. the naive termwise sum
   * <code>Infinity-Log(Infinity+a)</code> - rather than standing alone or inside a top-level
   * sum/product. Such results must not be accepted as definitive; the Gruntz fallback usually
   * resolves them properly. Structural containers (List, Interval, ConditionalExpression) that
   * legitimately carry infinities are exempt.
   */
  private static boolean hasNestedDirectedInfinity(IExpr result) {
    if (result.isFree(S.DirectedInfinity)) {
      return false;
    }
    return result.has(sub -> {
      if (!sub.isAST() || sub.isPlus() || sub.isTimes() || sub.isDirectedInfinity()
          || sub.isList() || sub.isInterval() || sub.isIntervalData()
          || sub.isAST(S.ConditionalExpression)) {
        return false;
      }
      return ((IAST) sub).exists(arg -> !arg.isFree(S.DirectedInfinity));
    }, true);
  }

  private static IExpr evalLimitAST(final IExpr expression, final IExpr limitValue, LimitData data,
      EvalEngine engine) {

    // Safely catches shapes like (Sin(1/x)/2)^(1/x^2) as x -> 0 natively
    IExpr envelope = envelopeBounded(expression, data, engine);
    if (envelope.isPresent()) {
      return envelope;
    }

    if (expression.isAST()) {
      if (!limitValue.isNumericFunction(true) && limitValue.isFree(S.DirectedInfinity)
          && limitValue.isFree(data.variable())) {
        // example Limit(E^(3*x), x->a) ==> E^(3*a)
        IExpr temp = expression.replaceAll(data.rule()).orElse(expression);
        IExpr evalTemp = engine.evalQuiet(temp);
        if (evalTemp.isFree(S.DirectedInfinity) && evalTemp.isIndeterminateFree()) {
          return evalTemp;
        }
      }
      final IAST ast = (IAST) expression;
      if (ast.isPlus()) {
        return plusLimit(ast, data, engine);
      } else if (ast.isTimes()) {
        return timesLimit(ast, data, engine);
      } else if (ast.isPower()) {
        return powerLimit(ast, data, engine);
      } else if (ast.isAST(S.Piecewise, 3)) {
        return piecewiseLimit(ast, data, engine);
      } else if (ast.argSize() > 0 && ast.isNumericFunctionAST()) {
        IASTMutable copy = ast.copy();
        IExpr temp = F.NIL;
        boolean indeterminate = false;
        boolean hasNIL = false;
        for (int i = 1; i < ast.size(); i++) {
          temp = data.limit(ast.get(i));
          if (temp.isPresent()) {
            if (temp.isIndeterminate()) {
              if (data.direction != Direction.TWO_SIDED) {
                return S.Indeterminate;
              }
              indeterminate = true;
              copy.set(i, S.Indeterminate);
            } else {
              copy.set(i, temp);
            }
          } else {
            // Limit computation failed for this argument — don't confuse with Indeterminate
            hasNIL = true;
          }
        }
        if (!indeterminate && !hasNIL) {
          temp = engine.evalQuiet(copy);

          // Intercept unevaluated special functions with DirectedInfinity arguments
          if (temp.isAST()) {
            IExpr specialLimit = directedInfinityLimit((IAST) temp);
            if (specialLimit.isPresent()) {
              return specialLimit;
            }
          }

          // a result like PolyGamma(0,...,Infinity) - DirectedInfinity stuck inside a
          // function argument - is junk, not a limit value; decline and let fallbacks run
          if (temp.isPresent() && !temp.isIndeterminate()
              && !hasNestedDirectedInfinity(temp)) {
            return temp;
          }
        }
        if (data.direction == Direction.TWO_SIDED && (indeterminate || hasNIL)) {
          IExpr twoSided = evalLimitTwoSided(expression, data, engine);
          if (twoSided.isPresent()) {
            return twoSided;
          }
        }
        // If any argument's limit couldn't be computed at all, signal failure (F.NIL),
        // not mathematical indeterminacy (S.Indeterminate).
        if (hasNIL) {
          return F.NIL;
        }
        return indeterminate ? S.Indeterminate : F.NIL;
      }
    }
    return F.NIL;
  }



  private static IExpr evalLimitQuiet(final IExpr expr, LimitData data) {
    if (expr.isNumber()) {
      return expr;
    }
    EvalEngine engine = EvalEngine.get();
    boolean quiet = engine.isQuietMode();
    try {
      IExpr evaledExpr = engine.evaluate(expr);

      if (data.direction() == Direction.TWO_SIDED) {
        IExpr temp = S.Limit.evalDownRule(engine, F.Limit(evaledExpr, data.rule()));
        if (temp.isPresent()) {
          return temp;
        }
      } else {
        IExpr direction =
            data.direction() == Direction.TWO_SIDED ? S.Reals : F.ZZ(data.direction().toInt());
        IExpr temp = S.Limit.evalDownRule(engine,
            F.Limit(evaledExpr, data.rule(), F.Rule(S.Direction, direction)));
        if (temp.isPresent()) {
          return temp;
        }
      }
      IExpr result = evaluateLimit(evaledExpr, data.rule(), data.direction(), engine);
      if (result.isPresent()) {
        return result;
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      // A quiet internal sub-limit must never blow up its caller: recursion-budget
      // exhaustion (e.g. RecursionLimitExceeded under the constrained L'Hopital budget)
      // simply means "could not determine" - every caller handles NIL.
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /**
   * <p>
   * Evaluate the limit of a function by evaluating the separate directions
   * <code>({@link Direction#FROM_BELOW}, {@link Direction#FROM_ABOVE}</code> for the arguments and
   * comparing the function evaluation result for equality. * @param astLimitEvaluated the limit
   * evaluation which can contain {@link S#Indeterminate} as arguments * @param astOriginal the
   * original ast with all non {@link S#Indeterminate} arguments
   * 
   * @param engine
   * @return {@link S#Indeterminate} if no limit can be found
   */
  private static IExpr evalLimitTwoSided(IExpr evaledExpr, LimitData data,
      final EvalEngine engine) {
    ISymbol symbol = data.variable();
    final IExpr limitValue = data.limitValue();
    IExpr limitAbove = evalLimit(evaledExpr,
        new LimitData(symbol, limitValue, data.rule(), Direction.FROM_ABOVE), engine);
    if (!limitAbove.isPresent() || !limitAbove.isFree(S.Limit)) {
      return F.NIL;
    }

    IExpr limitBelow = evalLimit(evaledExpr,
        new LimitData(symbol, limitValue, data.rule(), Direction.FROM_BELOW), engine);
    if (!limitBelow.isPresent() || !limitBelow.isFree(S.Limit)) {
      return F.NIL;
    }

    if (limitAbove.equals(limitBelow)) {
      if (!limitAbove.isFree(x -> x.isInterval() || x.isIntervalData(), true)) {
        return S.Indeterminate;
      }
      if (limitAbove.equals(S.ComplexInfinity)) {
        return F.NIL;
      }
      // COMPLEX PRINCIPAL BRANCH PHASE CHECK
      // If the limit diverges, Symja's real-valued auto-evaluator may have dropped
      // the complex phase of a fractional power (e.g., (-x)^(-2/3) -> x^(-2/3)).
      // We mathematically verify if the base approaches 0 from below.
      if (limitAbove.isInfinity() || limitAbove.isNegativeInfinity()
          || limitAbove.isDirectedInfinity()) {
        boolean hasComplexPhase = evaledExpr.has(expr -> {
          if (expr.isPower()) {
            IExpr exponent = expr.exponent();
            if (exponent.isFraction() || (exponent.isNumber() && !exponent.isInteger())) {
              IExpr base = expr.base();
              IExpr baseLimit = evalLimitQuiet(base,
                  new LimitData(symbol, limitValue, data.rule(), Direction.FROM_BELOW));
              if (baseLimit.isZero()) {
                return signViaApproach(base, symbol, limitValue, Direction.FROM_BELOW,
                    engine) == -1;
              }
            }
          }
          return false;
        }, true);


        if (hasComplexPhase) {
          return S.Indeterminate; // Mismatched phase rays force Indeterminate
        }
      }
      return limitAbove;
    }
    return S.Indeterminate;
  }

  /**
   * <code>Limit[RootSum[f, g], x -> x0]</code> for a finite <code>x0</code> whose root polynomial
   * <code>f</code> is free of the limit variable <code>x</code>: the roots do not move with
   * <code>x</code>, so the summand <code>g</code> (typically <code>Log(x - #1)/...</code> from a
   * differentiated-log antiderivative) is continuous in <code>x</code> unless <code>x0</code> is
   * itself a root. The limit is therefore the direct substitution
   * <code>RootSum[f, g /. x -> x0]</code>, independent of the approach direction.
   *
   * <p>
   * If <code>x0</code> is a root, the substitution introduces a <code>Log(0)</code> term and the
   * RootSum evaluates to <code>ComplexInfinity</code>; that case is declined ({@link F#NIL}) so the
   * generic limit machinery can deal with the singular endpoint.
   *
   * @return the substituted RootSum, or {@link F#NIL} if this shortcut does not apply
   */
  private static IExpr limitRootSum(IExpr function, LimitData data, EvalEngine engine) {
    if (!function.isAST(S.RootSum, 3)) {
      return F.NIL;
    }
    if (data.limitValue().isDirectedInfinity()) {
      return F.NIL; // only finite endpoints substitute directly
    }
    final ISymbol x = data.variable();
    if (!function.first().isFree(x, true)) {
      return F.NIL; // the roots depend on x -> not a plain substitution
    }
    IExpr substituted = engine.evalQuiet(function.replaceAll(data.rule()).orElse(function));
    if (substituted.isPresent() && substituted.isFree(x, true) && substituted.isFree(S.Limit)
        && substituted.isIndeterminateFree() && substituted.isFree(S.ComplexInfinity)
        && substituted.isFree(S.DirectedInfinity)) {
      return substituted;
    }
    return F.NIL;
  }

  private static IExpr evalReplaceAll(IExpr expression, LimitData data, EvalEngine engine) {
    // Direct substitution assumes continuity at the limit point. For an UNKNOWN function
    // f(...) whose arguments contain the limit variable, continuity is not justified -
    // e.g. Limit(x*f(x)^2/(x^2+f(x)^4), x->0) depends on f (f=Sqrt gives 1/2, not 0) and
    // must stay unevaluated, exactly as the substitution x->0 would wrongly suggest 0.
    final ISymbol variable = data.variable();
    if (expression.has(e -> e.isAST() && e.head().isSymbol() && !e.head().isBuiltInSymbol()
        && !((IAST) e).isFree(variable, true), true)) {
      return F.NIL;
    }
    IExpr result = expression.replaceAll(data.rule());
    if (result.isPresent()) {
      result = engine.evalQuiet(result);
      if (result.isNumericFunction(true) || result.isInfinity() || result.isNegativeInfinity()) {
        return IntervalSym.toAccumBoundsIndeterminate(result);
      }
    }
    return F.NIL;
  }

  private static IExpr evaluateLimit(IExpr function, IAST rule, Direction direction,
      final EvalEngine engine) {
    ISymbol symbol = (ISymbol) rule.arg1();
    IExpr limit = rule.arg2();
    try {
      if (function.has(
          x -> x.isFunctionID(ID.Factorial, ID.Gamma, ID.LogGamma, ID.Pochhammer, ID.PolyGamma),
          true)) {
        function = engine.evaluate(F.FunctionExpand(function));

        // Shift Gamma arguments away from their poles using the exact recurrence
        // Gamma(z) = Gamma(z+m+1)/(z*(z+1)*...*(z+m)) whenever the argument's limit is a
        // non-positive integer. This turns e.g. x*Gamma(x) at x->0 into Gamma(x+1) (limit 1)
        // and x - Gamma(1/x) at x->Infinity into x - x*Gamma(1+1/x) (limit EulerGamma).
        // FunctionExpand applies the opposite rewrite Gamma(1+z) -> z*Gamma(z), so the shifted
        // form must be resolved by a direct depth-guarded evalLimit and only a clean result
        // adopted - substituting it back into `function` ping-pongs forever.
        int poleShiftDepth = GAMMA_POLE_SHIFT_DEPTH.get();
        if (poleShiftDepth < 3) {
          final LimitData poleData = new LimitData(symbol, limit, rule, direction);
          IExpr shifted = F.subst(function, sub -> {
            if (sub.isAST(S.Gamma, 2)) {
              IExpr z = sub.first();
              try {
                IExpr zLim = evalLimitQuiet(z, poleData);
                if (zLim.isInteger()) {
                  int m = -zLim.toIntDefault();
                  if (m >= 0 && m <= 12) { // pole at 0, -1, ..., -12
                    IASTAppendable den = F.TimesAlloc(m + 1);
                    for (int k = 0; k <= m; k++) {
                      den.append(F.Plus(z, F.ZZ(k)));
                    }
                    return F.Divide(F.Gamma(F.Plus(z, F.ZZ(m + 1))), den.oneIdentity1());
                  }
                }
              } catch (RuntimeException rex) {
                Errors.rethrowsInterruptException(rex);
                // fall through - leave this Gamma unshifted
              }
            }
            return F.NIL;
          });
          if (shifted.isPresent() && !shifted.equals(function)) {
            GAMMA_POLE_SHIFT_DEPTH.set(poleShiftDepth + 1);
            try {
              IExpr shiftedResult =
                  evalLimit(engine.evaluate(shifted), poleData, engine);
              if (shiftedResult.isPresent() && shiftedResult.isFree(S.Limit)
                  && shiftedResult.isIndeterminateFree()
                  && !hasNestedDirectedInfinity(shiftedResult)) {
                return shiftedResult;
              }
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              // ignore - continue with the unshifted function
            } finally {
              GAMMA_POLE_SHIFT_DEPTH.set(poleShiftDepth);
            }
          }
        }

        // Apply Stirling's approximation exclusively for limits approaching Infinity
        if (limit.isInfinity()) {

          // The Exp(Limit(Log(...))) Stirling trick only helps when at least one Gamma-family
          // argument actually diverges - otherwise replaceLogStirling substitutes nothing and
          // the inner evalLimit recurses back to this very expression (infinite descent, e.g.
          // Limit(Gamma(1/x), x->Infinity)).
          final ISymbol stirlingSymbol = symbol;
          boolean stirlingApplies = function.has(sub -> {
            if (sub.isAST(S.Gamma, 2) || sub.isAST(S.LogGamma, 2)
                || sub.isAST(S.Factorial, 2)) {
              return divergesAtInfinity(sub.first(), stirlingSymbol);
            }
            if (sub.isAST(S.Pochhammer, 3)) {
              return divergesAtInfinity(sub.first(), stirlingSymbol)
                  || divergesAtInfinity(sub.second(), stirlingSymbol);
            }
            return false;
          }, true);

          // If the expression is primarily multiplicative, evaluate Exp(Limit(Log(expr)))
          // to completely bypass massive Gruntz exponential towers.
          if (stirlingApplies
              && (function.isTimes() || function.isPower() || function.isAST(S.Gamma, 2)
                  || function.isAST(S.Factorial, 2) || function.isAST(S.Pochhammer, 3))) {


            // Force logarithms to expand (e.g. Log(a/b) -> Log(a) - Log(b))
            IExpr logExpr = engine.evaluate(F.PowerExpand(F.Log(function)));
            if (DEBUG) {
              System.out.println("Before replaceLogStirling: " + logExpr);
            }
            // Inject additive Stirling series
            logExpr = GruntzLimit.replaceLogStirling(logExpr, symbol, engine);

            // Expand to distribute terms like (x + 1/2)*Log(x + 1/2)
            logExpr = engine.evaluate(F.ExpandAll(logExpr));

            // Re-combine logs to stabilize the rational fractions before evaluation
            logExpr = GruntzLimit.logCombine(logExpr, true, symbol);
            if (DEBUG) {
              System.out.println("After logCombine: " + logExpr);
            }
            LimitData data = new LimitData(symbol, limit, rule, direction);
            IExpr logLimit = evalLimit(logExpr, data, engine);

            // If the logarithmic limit resolved cleanly, immediately return Exp(Result)
            if (logLimit.isPresent() && logLimit.isFree(S.Limit)) {
              return engine.evaluate(F.Exp(logLimit));
            }
          }

          // --- FALLBACK: STANDARD EXPONENTIAL STIRLING ---
          // Only runs if the heuristic above was additive or failed to resolve.
          IExpr stirlingFunction = replaceStirling(function, symbol, engine);
          if (!stirlingFunction.equals(function)) {
            // Cancel trivial Sqrt(1/x)*Sqrt(x) terms generated by Stirling
            stirlingFunction = engine.evaluate(F.Simplify(stirlingFunction));
            // Re-combine logarithms into stable rational fractions
            stirlingFunction = GruntzLimit.logCombine(stirlingFunction, true, symbol);
            LimitData stirlingData = new LimitData(symbol, limit, rule, direction);
            IExpr stirlingResult = evalLimit(stirlingFunction, stirlingData, engine);
            // Adopt only clean resolutions: the substituted asymptotics can strand the
            // evaluation somewhere WORSE than the original - the digamma -1/(2z) tail
            // makes PolyGamma's reflection formula fire on the re-evaluated argument,
            // producing Cot(divergent) -> a false Indeterminate (psi(psi(psi(x)))).
            if (stirlingResult.isPresent() && stirlingResult.isFree(S.Limit)
                && stirlingResult.isIndeterminateFree()) {
              return stirlingResult;
            }
            // otherwise continue below with the ORIGINAL function
          }
        }
      }

      if (direction == Direction.TWO_SIDED) {
        IExpr temp = S.Limit.evalDownRule(engine, F.Limit(function, rule));
        if (temp.isPresent()) {
          return temp;
        }
      }
      LimitData data = new LimitData(symbol, limit, rule, direction);
      IExpr result = evalLimit(function, data, engine);

      // An oo - oo cancellation at a FINITE limit point - each term has a pole, so a
      // term-by-term limit is Indeterminate - becomes a resolvable 0/0 once combined over a
      // common denominator. Concrete-exponent forms auto-combine, but a symbolic exponent does
      // not (thesis (n+1)*x^(n+1)/(x^(n+1)-1) - x/(x-1) at x->1 -> n/2). Retry once with
      // Together and reduce the correct-but-unsimplified result.
      if (result.isPresent() && result.isIndeterminate() && function.isPlus()
          && limit.isNumericFunction() && !limit.isInfinity() && !limit.isNegativeInfinity()
          && !limit.isDirectedInfinity() && !TOGETHER_LIMIT_RETRY.get()) {
        IExpr combined = engine.evalQuiet(F.Together(function));
        if (combined.isPresent() && !combined.equals(function)) {
          TOGETHER_LIMIT_RETRY.set(Boolean.TRUE);
          try {
            IExpr retry = evalLimit(combined, data, engine);
            if (retry.isPresent() && retry.isFree(S.Limit) && retry.isIndeterminateFree()) {
              return engine.evaluate(F.Simplify(retry));
            }
          } finally {
            TOGETHER_LIMIT_RETRY.set(Boolean.FALSE);
          }
        }
      }
      return result;

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(symbol, rex);
    }
    return F.NIL;
  }

  /**
   * 
   * @param function The original function being analyzed for oscillating special functions.
   * @param symbol The variable with respect to which the limit is being taken.
   * @param limitValue The value that the variable is approaching in the limit.
   * @param data Additional data related to the limit calculation.
   * @return True if the function contains oscillating special functions, false otherwise.
   */
  private static boolean isOscillatingSpecial(final IExpr function, final ISymbol symbol,
      final IExpr limitValue, LimitData data) {
    return function.has(expr -> {
      if (expr.isAST() && expr.head().isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) expr.head()).ordinal();

        // Functions with poles at every negative integer → oscillate wildly as arg → -Infinity
        if (id == ID.Gamma || id == ID.LogGamma || id == ID.Factorial || id == ID.Factorial2
            || id == ID.PolyGamma || id == ID.Zeta) {
          IAST ast = (IAST) expr;
          IExpr arg = (id == ID.PolyGamma && ast.argSize() == 3) ? ast.arg2() : ast.arg1();
          IExpr argLim =
              evalLimitQuiet(arg, new LimitData(symbol, limitValue, data.rule(), data.direction()));
          if (argLim.isNegativeInfinity()) {
            return true;
          }
        }

        // Real-valued trig functions oscillate as their argument → ±Infinity
        if (id == ID.Sin || id == ID.Cos || id == ID.Tan || id == ID.Cot || id == ID.Sec
            || id == ID.Csc) {
          if (function.isTimes()) {
            // For Times: only oscillating if no co-factor decays to zero (undamped)
            boolean hasDampingFactor = ((IAST) function).exists(factor -> {
              if (factor.isSin() || factor.isCos()
                  || factor.isFunctionID(ID.Tan, ID.Cot, ID.Sec, ID.Csc)) {
                return false; // skip the trig term itself
              }
              IExpr factorLim = evalLimitQuiet(factor,
                  new LimitData(symbol, limitValue, data.rule(), data.direction()));
              return factorLim.isZero();
            });
            if (!hasDampingFactor) {
              IAST ast = (IAST) expr;
              IExpr arg = ast.arg1();
              IExpr argLim = evalLimitQuiet(arg,
                  new LimitData(symbol, limitValue, data.rule(), data.direction()));
              if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
                return true;
              }
            }
          } else if (function.isPlus()) {
            for (int i = 1; i < function.size(); i++) {
              IExpr term = function.get(i);
              IExpr termLimit = evalLimitQuiet(term, data);
              if (termLimit.isIndeterminate()) {
                return true;
              }
            }
          }
        }

        // NOTE: hyperbolic Sinh/Cosh/Tanh/Coth/Sech/Csch are deliberately NOT treated as
        // oscillating here. They converge or diverge monotonically, so their limits - including
        // undamped products such as 2*Cosh(x)*E^x - are determinate and are resolved by the
        // hyperbolic->exponential rewrite in evalLimit. Flagging Sinh/Cosh as Indeterminate here
        // produced wrong results and poisoned signInf/Gruntz.

        // Airy functions AiryAi and AiryBi: oscillate for real negative arguments → -Infinity
        if (id == ID.AiryAi || id == ID.AiryBi) {
          IAST ast = (IAST) expr;
          IExpr arg = ast.arg1();
          IExpr argLim =
              evalLimitQuiet(arg, new LimitData(symbol, limitValue, data.rule(), data.direction()));
          if (argLim.isNegativeInfinity()) {
            return true;
          }
        }

        // Bessel functions BesselJ and BesselY oscillate for real arguments → +Infinity
        // (they behave like damped sinusoids, but the damping goes to 0, not to a finite value)
        if (id == ID.BesselJ || id == ID.BesselY) {
          if (expr.argSize() == 2) {
            IAST ast = (IAST) expr;
            IExpr arg = ast.arg2(); // second argument is the variable argument
            IExpr argLim = evalLimitQuiet(arg,
                new LimitData(symbol, limitValue, data.rule(), data.direction()));
            if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
              return true;
            }
          }
        }

        // Struve functions StruveH and StruveL: similar oscillatory behaviour to Bessel
        if (id == ID.StruveH || id == ID.StruveL) {
          if (expr.argSize() == 2) {
            IAST ast = (IAST) expr;
            IExpr arg = ast.arg2();
            IExpr argLim = evalLimitQuiet(arg,
                new LimitData(symbol, limitValue, data.rule(), data.direction()));
            if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
              return true;
            }
          }
        }
      }
      return false;
    }, true);
  }

  /**
   * Test if <code>y</code> matches pattern <code>Sqrt(_)</code> or
   * <code>Times(f1,...,Sqrt(_),...,fn)</code> * @param y * @return
   */
  private static boolean isSqrtExpression(IExpr y) {
    if (y.isTimes()) {
      return ((IAST) y).exists(x -> x.isSqrt());
    }
    return y.isSqrt();
  }

  /**
   * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">
   * Wikipedia L'Hôpital's rule</a>
   *
   * @param numerator
   * @param denominator
   * @param data the limits data definition
   * @param engine
   * @return
   */
  private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, LimitData data,
      EvalEngine engine) {
    final ISymbol x = data.variable();
    int recursionLimit = engine.getRecursionLimit();
    int currentDepth = engine.getRecursionCounter();
    // Only the OUTERMOST L'Hopital application may establish the relative recursion budget.
    // Each nested application re-reading the current depth would EXTEND the ceiling by
    // another budget-increment forever - on derivative-growing ratios (E^Gamma(x)/Gamma(x):
    // every round multiplies in Gamma*PolyGamma factors) the renewing budget let the D-tree
    // recursion run to a raw JVM StackOverflowError instead of the graceful
    // RecursionLimitExceeded below.
    boolean outermostBudget = !LHOSPITAL_BUDGET_ACTIVE.get();
    try {
      if (outermostBudget && (recursionLimit <= 0
          || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT)) {
        // Give L'Hopital a safe relative budget to prevent instant starvation inside Gruntz
        engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
        LHOSPITAL_BUDGET_ACTIVE.set(Boolean.TRUE);
      }
      engine.incRecursionCounter();
      if (data.limitValue.isInfinity() || data.limitValue.isNegativeInfinity()) {
        if (!numerator.isPower() && denominator.isPower()
            && denominator.exponent().equals(F.C1D2)) {
          IFraction frac = (IFraction) denominator.exponent();
          if (frac.numerator().isOne()) {
            IInteger exp = frac.denominator(); // == 2
            IExpr expr = engine.evalQuiet(F.Times(F.D(F.Power(numerator, exp), x),
                F.Power(F.D(denominator.base(), x), F.CN1)));
            if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
              expr = engine.evalQuiet(F.Simplify(expr));
            }
            expr = evalLimit(expr, data, engine);
            if (expr.isNumber()) {
              return F.Power(expr, frac);
            }
          }
        }
      }
      if (numerator.isPowerFraction()) {
        return lHospitalesRuleWithNumeratorRoot((IAST) numerator, denominator, data, engine);
      }
      IExpr expr =
          engine.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
      // Derivative-growing ratios (E^Gamma(x)/Gamma(x): each round multiplies in
      // Gamma*PolyGamma factors) blow up combinatorially - evaluating the grown expression
      // later triggers E^(Plus)-auto-Expand recursion deep enough for a raw
      // StackOverflowError. L'Hopital cannot converge on such shapes; refuse by size.
      if (expr.leafCount() > 2000) {
        return F.NIL;
      }
      if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
        expr = engine.evalQuiet(F.Simplify(expr));
      }
      if (expr.isFree(v -> v.equals(S.D) || v.equals(S.Derivative), true)) {
        return evalLimit(expr, data, engine);
      }
    } catch (RecursionLimitExceeded rle) {
      //
    } finally {
      if (outermostBudget && LHOSPITAL_BUDGET_ACTIVE.get()) {
        LHOSPITAL_BUDGET_ACTIVE.set(Boolean.FALSE);
      }
      engine.setRecursionLimit(recursionLimit);
      engine.decRecursionCounter();
    }
    return F.NIL;
  }

  /**
   * The <code>numerator</code> is of the form <code>base ^ (n/root)</code>. L'hospital rule is
   * tried for <code>{base ^ n, denominator ^ root}</code> and the result returned as <code>
   * result ^ (1/root)</code>.
   *
   * @param numerator is of the form <code>Power(base,n/root)</code>
   * @param denominator
   * @param data
   * @param engine
   * @return
   */
  private static IExpr lHospitalesRuleWithNumeratorRoot(IAST numerator, IExpr denominator,
      LimitData data, EvalEngine engine) {
    // see github #230
    final ISymbol x = data.variable();
    final IFraction exponentFraction = (IFraction) numerator.exponent();
    final IInteger n = exponentFraction.numerator();
    final IInteger root = exponentFraction.denominator();
    final IExpr newNumerator = engine.evalQuiet(F.Power(numerator.base(), n));
    final IExpr newDenominator = engine.evalQuiet(F.Power(denominator, root));
    final IExpr expr =
        engine.evalQuiet(F.Times(F.D(newNumerator, x), F.Power(F.D(newDenominator, x), F.CN1)));
    // see lHospitalesRule: refuse derivative blow-ups by size
    if (expr.leafCount() > 2000) {
      return F.NIL;
    }
    final IExpr temp = evalLimit(expr, data, engine);
    if (temp.isPresent()) {
      return F.Power(temp, F.QQ(F.C1, root));
    }
    return F.NIL;
  }

  /**
   * Solve for example:<br>
   * <code>Limit(Gamma(1/t),t->Infinity) ==> Infinity</code> <br>
   * <code>Limit(Gamma(1/t),t->-Infinity)  ==> -Infinity</code>
   *
   * @param ast
   * @param data
   * @param limitValue <code>Infinity</code> or <code>-Infinity</code>
   * @return
   */
  private static IExpr limitInfinityZero(IAST ast, LimitData data, final IAST limitValue) {
    Direction direction = limitValue.isNegativeInfinity() ? //
        Direction.FROM_BELOW //
        : Direction.FROM_ABOVE;
    Direction dataDirection = data.direction();
    if (dataDirection == Direction.TWO_SIDED || dataDirection == direction) {
      int variableArgPosition = -1;
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isFree(data.variable())) {
          if (variableArgPosition == -1) {
            variableArgPosition = i;
          } else {
            // more than 1 argument contains the variable
            return F.NIL;
          }
        }
      }
      if (variableArgPosition > 0) {
        IExpr arg1 = evalLimitQuiet(ast.get(variableArgPosition), data);
        if (arg1.isZero()) {

          LimitData tempData =
              new LimitData(data.variable(), F.C0, F.Rule(data.variable(), F.C0), direction);
          return evalLimitQuiet(ast.setAtCopy(variableArgPosition, data.variable()), tempData);
        }
      }
    }
    return F.NIL;
  }

  // private static IExpr mapLimit(final IAST ast, LimitData data) {
  // return ast.mapThread(data.limit(null), 1);
  // }

  /**
   * Evaluate the limits of the arguments of the <code>function</code> and evaluate the <code>
   * function</code> with these new arguments if available.
   *
   * @param function
   * @param data the data for the limit
   * @param engine
   * @return {@link F#NIL} if evaluation wasn't successful
   */
  private static IExpr limitNumericFunctionArgs(IAST function, LimitData data, EvalEngine engine) {
    IASTMutable functionLimitArgs = F.NIL;
    for (int i = 1; i < function.size(); i++) {
      IExpr arg = function.get(i);
      if (!arg.isFree(data.variable())) {
        IExpr temp = evalLimitQuiet(arg, data);
        if (temp.isPresent() && temp.isFree(data.variable()) && temp.isNumericFunction(true)) {
          if (functionLimitArgs.isNIL()) {
            functionLimitArgs = function.copy();
          }
          functionLimitArgs.set(i, temp);
        }
      }
    }
    if (functionLimitArgs.isPresent()) {
      IExpr temp = engine.evaluate(functionLimitArgs);
      if (!temp.isIndeterminate() && !temp.isComplexInfinity()) {
        return temp;
      }
    }
    return F.NIL;
  }

  /**
   * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of
   * Rational Functions</a>
   *
   * @param numeratorPoly
   * @param denominatorPoly
   * @param symbol the variable for which to approach to the limit
   * @param limit the limit value
   * @param data the limit expression which the variable should approach to
   * @return
   */
  private static IExpr limitsInfinityOfRationalFunctions(ExprPolynomial numeratorPoly,
      ExprPolynomial denominatorPoly, ISymbol symbol, IExpr limit, LimitData data) {
    long numDegree = numeratorPoly.degree();
    long denomDegree = denominatorPoly.degree();
    if (numDegree > denomDegree) {
      // If the numerator has the highest term, then the fraction is
      // called "top-heavy". If, when you divide the numerator
      // by the denominator the resulting exponent on the variable is
      // even, then the limit (at both \infty and -\infty) is
      // \infty. If it is odd, then the limit at \infty is \infty, and the
      // limit at -\infty is -\infty.
      long oddDegree = (numDegree + denomDegree) % 2;
      if (oddDegree == 1) {
        return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
            denominatorPoly.leadingBaseCoefficient()), limit));
      } else {
        return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
            denominatorPoly.leadingBaseCoefficient()), F.CInfinity));
      }
    } else if (numDegree < denomDegree) {
      // If the denominator has the highest term, then the fraction is
      // called "bottom-heavy" and the limit (at both \infty
      // and -\infty) is zero.
      return F.C0;
    }
    // If the exponent of the highest term in the numerator matches the
    // exponent of the highest term in the denominator,
    // the limit (at both \infty and -\infty) is the ratio of the
    // coefficients of the highest terms.
    return F.Divide(numeratorPoly.leadingBaseCoefficient(),
        denominatorPoly.leadingBaseCoefficient());
  }

  /**
   * Try l'Hospitales rule for numerator and denominator expression.
   *
   * @param numerator
   * @param denominator
   * @param data the limit data definition
   * @param engine
   * @return <code>F.NIL</code> if no limit was found
   */
  private static IExpr numeratorDenominatorLimit(IExpr numerator, IExpr denominator, LimitData data,
      EvalEngine engine) {
    IExpr numValue;
    IExpr denValue;
    if (denominator.isOne() && numerator.isTimes()) {
      return data.mapLimit((IAST) numerator);
    }
    if (!denominator.isNumber() || denominator.isZero()) {
      int recursionLimit = engine.getRecursionLimit();
      int currentDepth = engine.getRecursionCounter();
      // Shares the ONE L'Hopital budget with lHospitalesRule: only the outermost holder may
      // establish it - re-extending per nesting level lets derivative-growing ratios run to
      // a raw StackOverflowError (see lHospitalesRule).
      boolean outermostBudget = !LHOSPITAL_BUDGET_ACTIVE.get();
      try {
        if (outermostBudget && (recursionLimit <= 0
            || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT)) {
          // Give L'Hopital a safe relative budget
          engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
          LHOSPITAL_BUDGET_ACTIVE.set(Boolean.TRUE);
        }
        IExpr result = F.NIL;
        denValue = evalLimitQuiet(denominator, data);
        if (denValue.isIndeterminate()) {
          return F.NIL;
        } else if (denValue.isZero()) {
          numValue = evalLimitQuiet(numerator, data);
          if (numValue.isZero()) {
            return lHospitalesRule(numerator, denominator, data, engine);
          }
          return F.NIL;
        } else if (denValue.isInfinity()) {
          numValue = evalLimitQuiet(numerator, data);
          if (numValue.isInfinity()) {
            return lHospitalesRule(numerator, denominator, data, engine);
          } else if (numValue.isNegativeInfinity()) {
            numerator = engine.evaluate(numerator.negate());
            numValue = evalLimitQuiet(numerator, data);
            if (numValue.isInfinity()) {
              result = lHospitalesRule(numerator, denominator, data, engine);
              if (result.isPresent()) {
                return result.negate();
              }
            }
          }
          return F.NIL;
        } else if (denValue.isNegativeInfinity()) {
          denominator = engine.evaluate(denominator.negate());
          denValue = evalLimitQuiet(denominator, data);
          if (denValue.isInfinity()) {
            numValue = evalLimitQuiet(numerator, data);
            if (numValue.isInfinity()) {
              result = lHospitalesRule(numerator, denominator, data, engine);
              if (result.isPresent()) {
                return result.negate();
              }
            } else if (numValue.isNegativeInfinity()) {
              numerator = engine.evaluate(numerator.negate());
              numValue = evalLimitQuiet(numerator, data);
              if (numValue.isInfinity()) {
                return lHospitalesRule(numerator, denominator, data, engine);
              }
            }
          }
          return F.NIL;
        }
      } catch (RecursionLimitExceeded rle) {
        engine.setRecursionLimit(recursionLimit);
      } finally {
        if (outermostBudget && LHOSPITAL_BUDGET_ACTIVE.get()) {
          LHOSPITAL_BUDGET_ACTIVE.set(Boolean.FALSE);
        }
        engine.setRecursionLimit(recursionLimit);
      }
    }
    return F.NIL;
  }

  private static IExpr piecewiseLimit(final IAST piecwiseAST, LimitData data, EvalEngine engine) {
    IExpr limit = data.limitValue();
    IExpr variable = data.variable();
    if (limit.isReal()) {
      int[] piecewiseDimension = piecwiseAST.isPiecewise();
      if (piecewiseDimension != null && piecewiseDimension[0] > 0) {
        IAST matrixOfValueConditionPairs = (IAST) piecwiseAST.first();
        IExpr defaultPiecewiseValue = piecwiseAST.second();
        IExpr limitFromBelow = F.NIL;
        IExpr limitFromAbove = F.NIL;
        for (int i = 0; i < piecewiseDimension[0]; i++) {
          IAST row = matrixOfValueConditionPairs.getAST(i + 1);
          IExpr arg1Result = row.arg1();
          IExpr arg2Comparison = row.arg2();

          IExpr tempComparison = arg2Comparison;
          if (data.direction == Direction.FROM_BELOW //
              || data.direction == Direction.TWO_SIDED) {
            if (arg2Comparison.isAST(S.Less, 3) && arg2Comparison.first().equals(variable)
                && arg2Comparison.second().equals(limit)) {
              tempComparison = ((IAST) arg2Comparison).setAtCopy(0, S.LessEqual);
            }
            IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
            if (temp.isTrue()) {
              temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
              if (limitFromBelow.isPresent() && !limitFromBelow.equals(temp)) {
                return S.Indeterminate;
              }
              limitFromBelow = temp;
            } else if (!temp.isFalse()) {
              return F.NIL;
            }
          }

          tempComparison = arg2Comparison;
          if (data.direction == Direction.FROM_ABOVE //
              || data.direction == Direction.TWO_SIDED) {
            if (arg2Comparison.isAST(S.Greater, 3) && arg2Comparison.first().equals(variable)
                && arg2Comparison.second().equals(limit)) {
              tempComparison = ((IAST) arg2Comparison).setAtCopy(0, S.GreaterEqual);
            }
            IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
            if (temp.isTrue()) {
              temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
              if (limitFromAbove.isPresent() && !limitFromAbove.equals(temp)) {
                return S.Indeterminate;
              }
              limitFromAbove = temp;
            } else if (!temp.isFalse()) {
              return F.NIL;
            }

          }
        }

        if (data.direction == Direction.FROM_BELOW) {
          if (limitFromBelow.isPresent()) {
            return limitFromBelow;
          }
          return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
        }
        if (data.direction == Direction.FROM_ABOVE) {
          if (limitFromAbove.isPresent()) {
            return limitFromAbove;
          }
          return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
        }
        if (data.direction == Direction.TWO_SIDED) {
          if (limitFromBelow.isPresent() && limitFromBelow.equals(limitFromAbove)) {
            return limitFromBelow;
          }
          if (limitFromBelow.isNIL() && limitFromAbove.isNIL()) {
            return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
          }
          return S.Indeterminate;
        }
      }
    }
    return F.NIL;
  }

  private static IExpr plusLimit(final IAST plusAST, LimitData data, EvalEngine engine) {
    // Limit[a_+b_+c_,sym->lim] ->
    // Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
    // IAST rule = data.getRule();
    IExpr limit = data.limitValue();
    if (limit.isInfinity() || limit.isNegativeInfinity()) {
      ISymbol symbol = data.variable();
      if (limit.isInfinity()) {
        // Mixed polynomial-logarithmic sums like x*Log(x) - x + Log(2*Pi)/2 - Log(x)/2
        // (typical Stirling remainders): ranking terms c*x^a*Log(x)^b lexicographically by
        // (a, b) decides the limit exactly. This MUST run before the polynomial-ring
        // heuristic below, which wrongly accepts Log(x)-coefficient sums (treating them as
        // opaque coefficients) and then reports a sign from the wrong "leading" term - e.g.
        // x*Log(x) - x + Log(2*Pi/x)/2 came back -Infinity instead of +Infinity.
        IExpr growth = plusLeadingGrowthLimit(plusAST, symbol, engine);
        if (growth.isPresent()) {
          return growth;
        }
      }
      try {
        ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
        ExprPolynomial poly = ring.create(plusAST);
        IExpr coeff = poly.leadingBaseCoefficient();
        long oddDegree = poly.degree() % 2;
        if (oddDegree == 1) {
          return evalLimitQuiet(F.Times(coeff, limit), data);
        }
        return evalLimitQuiet(F.Times(coeff, F.CInfinity), data);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    IExpr mapLimit = data.mapLimit(plusAST);
    if (mapLimit.isPresent() && !mapLimit.isIndeterminate()) {
      if (mapLimit.isFree(x -> x.equals(S.Limit), true)) {
        IExpr temp = F.eval(mapLimit);
        if (temp.isIndeterminate() && plusAST.isPlus()) {
          int indexOf = plusAST.indexOf(x -> isSqrtExpression(x));
          if (indexOf > 0) {
            temp = timesConjugateLHospital(plusAST, indexOf, data);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      return mapLimit;
    }
    return F.NIL;
  }

  /**
   * For <code>x -> +Infinity</code>: rank the terms of a sum, each of the shape
   * <code>c * x^a * Log(x)^b</code> with an <code>x</code>-free coefficient <code>c</code>,
   * lexicographically by <code>(a, b)</code>. A divergent unique leader whose coefficient has a
   * determinate sign decides the limit. Returns {@link F#NIL} for any term outside this shape
   * (conservative - the ordinary machinery continues).
   */
  private static IExpr plusLeadingGrowthLimit(IAST plusAST, ISymbol x, EvalEngine engine) {
    try {
      return plusLeadingGrowthLimitImpl(plusAST, x, engine);
    } catch (RuntimeException rex) {
      // engine sub-evaluations (PowerExpand, sign checks) can exhaust a constrained
      // recursion budget - treat as "shape not analyzable"
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Formal additive expansion of a logarithm's argument: <code>Log(u*v^k) -> Log(u) +
   * k*Log(v)</code>, recursively. (PowerExpand does not reliably split quotient arguments like
   * <code>Log(2*Pi/x)</code>, which the growth ranker needs in <code>c*x^a*Log(x)^b</code>
   * form.)
   */
  private static IExpr logExpand(IExpr arg) {
    if (arg.isTimes()) {
      IASTAppendable plus = F.PlusAlloc(arg.argSize());
      for (int i = 1; i <= arg.argSize(); i++) {
        plus.append(logExpand(((IAST) arg).get(i)));
      }
      return plus;
    }
    if (arg.isPower()) {
      return F.Times(arg.exponent(), logExpand(arg.base()));
    }
    return F.Log(arg);
  }

  /** Splits every composite-argument Log inside <code>expr</code> additively. */
  private static IExpr splitCompositeLogs(IExpr expr, EvalEngine engine) {
    IExpr split = F.subst(expr,
        e -> e.isLog() && (e.first().isTimes() || e.first().isPower()) //
            ? logExpand(e.first())
            : F.NIL);
    if (split.isPresent() && !split.equals(expr)) {
      // Expand distributes coefficients over the freshly created log-sums, e.g.
      // 1/2*(Log(2)+Log(Pi)-Log(x)) -> Log(2)/2+Log(Pi)/2-Log(x)/2 (plain evaluation
      // does NOT distribute, leaving a Times(c, Plus(...)) the ranker cannot classify).
      return engine.evalQuiet(F.Expand(split));
    }
    return expr;
  }

  private static IExpr plusLeadingGrowthLimitImpl(IAST plusAST, ISymbol x, EvalEngine engine) {
    IExpr expanded = splitCompositeLogs(plusAST, engine);
    if (DEBUG) {
      System.err.println("GROWTH-RANKER in=" + plusAST + "  split=" + expanded);
    }
    IAST plus = expanded.isPlus() ? (IAST) expanded : plusAST;
    final double eps = 1.0e-9;
    double bestA = Double.NEGATIVE_INFINITY;
    double bestB = Double.NEGATIVE_INFINITY;
    double[][] exps = new double[plus.argSize()][];
    IExpr[] coeffs = new IExpr[plus.argSize()];
    for (int i = 1; i <= plus.argSize(); i++) {
      Object[] part = xLogGrowthTerm(plus.get(i), x, engine);
      if (part == null) {
        return F.NIL;
      }
      double a = ((Double) part[0]).doubleValue();
      double b = ((Double) part[1]).doubleValue();
      exps[i - 1] = new double[] {a, b};
      coeffs[i - 1] = (IExpr) part[2];
      if (a > bestA + eps || (Math.abs(a - bestA) <= eps && b > bestB + eps)) {
        bestA = a;
        bestB = b;
      }
    }
    if (bestA < eps && bestB < eps) {
      return F.NIL; // leading term does not diverge
    }
    IASTAppendable leaderSum = F.PlusAlloc(plus.argSize());
    for (int i = 0; i < exps.length; i++) {
      if (Math.abs(exps[i][0] - bestA) <= eps && Math.abs(exps[i][1] - bestB) <= eps) {
        if (coeffs[i].isNIL()) {
          // an unknown-coefficient term (Log of a divergent sum) ties the leader - give up
          return F.NIL;
        }
        leaderSum.append(coeffs[i]);
      }
    }
    IExpr coefficient = engine.evalQuiet(leaderSum.oneIdentity0());
    if (coefficient.isZero() || !coefficient.isFree(x)) {
      return F.NIL;
    }
    if (coefficient.isPositiveResult()
        || engine.evalQuiet(F.Greater(coefficient, F.C0)).isTrue()) {
      return F.CInfinity;
    }
    if (coefficient.isNegativeResult() || engine.evalQuiet(F.Less(coefficient, F.C0)).isTrue()) {
      return F.CNInfinity;
    }
    return F.NIL;
  }

  /**
   * Decomposes a term into <code>{a, b, c}</code> with <code>term == c * x^a * Log(x)^b</code>
   * and <code>c</code> free of <code>x</code>; <code>null</code> if the term is not of this
   * shape. Exponents must be numerically evaluable.
   */
  private static Object[] xLogGrowthTerm(IExpr term, ISymbol x, EvalEngine engine) {
    if (term.isFree(x)) {
      return new Object[] {Double.valueOf(0.0), Double.valueOf(0.0), term};
    }
    if (term.equals(x)) {
      return new Object[] {Double.valueOf(1.0), Double.valueOf(0.0), F.C1};
    }
    if (term.isLog() && term.first().equals(x)) {
      return new Object[] {Double.valueOf(0.0), Double.valueOf(1.0), F.C1};
    }
    if (term.isLog()) {
      // Log(f) for divergent f grows like a*Log(x) (or slower): rank it in the (0,1) class
      // with an UNKNOWN coefficient (F.NIL) - usable only when strictly dominated by the
      // leading term, e.g. the -Log(x*Log(x)-x+...) remainder of a Stirling expansion.
      double[] lead = plusLeaderGrowth(term.first(), x, engine);
      if (lead != null && (lead[0] > 1.0e-9 || lead[1] > 1.0e-9)) {
        return new Object[] {Double.valueOf(0.0), Double.valueOf(1.0), F.NIL};
      }
      return null;
    }
    if (term.isPower() && term.exponent().isFree(x)) {
      double e;
      try {
        e = term.exponent().evalf();
      } catch (RuntimeException rex) {
        return null;
      }
      if (term.base().equals(x)) {
        return new Object[] {Double.valueOf(e), Double.valueOf(0.0), F.C1};
      }
      if (term.base().isLog() && term.base().first().equals(x)) {
        return new Object[] {Double.valueOf(0.0), Double.valueOf(e), F.C1};
      }
      return null;
    }
    if (term.isTimes()) {
      double a = 0.0;
      double b = 0.0;
      boolean unknownCoeff = false;
      IASTAppendable coeff = F.TimesAlloc(term.argSize());
      for (int i = 1; i <= term.argSize(); i++) {
        Object[] part = xLogGrowthTerm(((IAST) term).get(i), x, engine);
        if (part == null) {
          return null;
        }
        a += ((Double) part[0]).doubleValue();
        b += ((Double) part[1]).doubleValue();
        IExpr c = (IExpr) part[2];
        if (c.isNIL()) {
          unknownCoeff = true;
        } else {
          coeff.append(c);
        }
      }
      return new Object[] {Double.valueOf(a), Double.valueOf(b),
          unknownCoeff ? F.NIL : coeff.oneIdentity1()};
    }
    return null;
  }

  /**
   * Leading <code>(a, b)</code> growth of <code>f</code> in the <code>c*x^a*Log(x)^b</code> scale
   * (lexicographic max over the terms of a sum), or <code>null</code> if any term falls outside
   * that shape.
   */
  private static double[] plusLeaderGrowth(IExpr f, ISymbol x, EvalEngine engine) {
    final double eps = 1.0e-9;
    double bestA = Double.NEGATIVE_INFINITY;
    double bestB = Double.NEGATIVE_INFINITY;
    // split composite logs like Log(2*Pi/x) -> Log(2*Pi) - Log(x) so terms conform
    f = splitCompositeLogs(f, engine);
    IAST terms = f.isPlus() ? (IAST) f : F.Plus(f);
    for (int i = 1; i <= terms.argSize(); i++) {
      Object[] part = xLogGrowthTerm(terms.get(i), x, engine);
      if (part == null) {
        return null;
      }
      double a = ((Double) part[0]).doubleValue();
      double b = ((Double) part[1]).doubleValue();
      if (a > bestA + eps || (Math.abs(a - bestA) <= eps && b > bestB + eps)) {
        bestA = a;
        bestB = b;
      }
    }
    return new double[] {bestA, bestB};
  }

  /**
   * Checks if the expression structurally contains a bounded head (Sin, Cos, Tanh, ArcTan, ArcCot).
   */
  private static boolean containsBoundedHead(IExpr e) {
    if (!e.isAST()) {
      return false;
    }
    IAST ast = (IAST) e;
    IExpr head = ast.head();

    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Sin:
        case ID.Cos:
        case ID.Tanh:
        case ID.ArcTan:
        case ID.ArcCot:
          return true;
      }
    }

    for (int i = 1; i <= ast.argSize(); i++) {
      if (containsBoundedHead(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an expression that pointwise dominates the magnitude of e(x) over the reals.
   */
  private static IExpr magnitudeUpperBound(IExpr e, ISymbol x) {
    if (e.isFree(x)) {
      return F.Abs(e);
    }
    if (!e.isAST()) {
      return F.Abs(e);
    }

    IAST ast = (IAST) e;
    IExpr head = ast.head();

    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Sin:
        case ID.Cos:
        case ID.Tanh:
          return F.C1;
        case ID.ArcTan:
        case ID.ArcCot:
          return F.eval(F.Times(F.C1D2, S.Pi));
        case ID.Plus:
          IASTAppendable sum = F.PlusAlloc(ast.argSize());
          for (int i = 1; i <= ast.argSize(); i++) {
            sum.append(magnitudeUpperBound(ast.get(i), x));
          }
          return F.eval(sum);
        case ID.Times:
          IASTAppendable prod = F.TimesAlloc(ast.argSize());
          for (int i = 1; i <= ast.argSize(); i++) {
            prod.append(magnitudeUpperBound(ast.get(i), x));
          }
          return F.eval(prod);
        case ID.Power:
          if (ast.argSize() == 2) {
            IExpr base = ast.arg1();
            IExpr exp = ast.arg2();
            if (base.equals(S.E) && containsBoundedHead(exp) && exp.isAST(S.Sin, 2)) {
              return S.E;
            }
            // Generalize the bound through powers (e.g. Abs(base)^exp)
            return F.eval(F.Power(magnitudeUpperBound(base, x), exp));
          }
          break;
      }
    }
    return F.Abs(e);
  }

  /**
   * Resolves limits of vanishing magnitude-bounded expressions (e.g. envelope squeeze theorem).
   */
  private static IExpr envelopeBounded(final IExpr expr, LimitData data, EvalEngine engine) {
    if (!containsBoundedHead(expr)) {
      return F.NIL;
    }

    IExpr b = magnitudeUpperBound(expr, data.variable());

    // Prevent infinite recursion: if the bound still contains oscillating heads
    // (e.g., they were trapped inside Log or Power), the squeeze fails symbolically.
    if (b.equals(F.Abs(expr)) || containsBoundedHead(b)) {
      return F.NIL;
    }

    if (!b.isFree(data.variable())) {
      IExpr limBound = evalLimitQuiet(b, data);
      if (limBound.isZero()) {
        return F.C0;
      }
    }
    return F.NIL;
  }

  private static IExpr powerLimit(final IAST powerAST, LimitData data, EvalEngine engine) {
    IExpr base = powerAST.arg1();
    IExpr exponent = powerAST.arg2();

    // --- UNIVERSAL POWER FORM HEURISTIC ---
    // Transformation: Limit(f(x)^g(x), x->a) == Exp(Limit(g(x) * Log(f(x)), x->a))
    // MUST be at the very top to prevent 1^Infinity, 0^0, and Infinity^0 forms
    // from being prematurely evaluated to Indeterminate by naive base/exponent checks.
    if (!base.isFree(data.variable()) && !exponent.isFree(data.variable())) {
      IExpr expLogBase = engine.evaluate(F.Times(exponent, F.Log(base)));

      IExpr limitExpLog = evalLimitQuiet(expLogBase, data);

      // Fallback to Gruntz if L'Hopital returns Indeterminate or fails
      if (!limitExpLog.isIndeterminateFree() || limitExpLog.isNIL()) {
        limitExpLog = GruntzLimit.evaluateLimit(expLogBase, data.variable(), data.limitValue(),
            data.direction(), engine);
      }

      if (limitExpLog.isPresent() && !limitExpLog.isAST(S.Limit)
          && limitExpLog.isIndeterminateFree()) {
        // Use PowerExpand to cleanly format terms like E^(Log(a)/2) into Sqrt(a)
        return engine.evaluate(F.PowerExpand(F.Exp(limitExpLog)));
      }
    }

    // Safely processes a^f(x) by evaluating Limit(f(x)) first.
    if (base.isFree(data.variable())) {
      if (base.isZero()) {
        IExpr limitExp = evalLimitQuiet(exponent, data);
        if (limitExp.isPositiveResult() || limitExp.isInfinity()) {
          return F.C0;
        }
        if (limitExp.isNegativeResult() || limitExp.isNegativeInfinity()) {
          return F.CComplexInfinity;
        }
        if (limitExp.isZero()) {
          return S.Indeterminate;
        }
      } else {
        IExpr limitExp = evalLimitQuiet(exponent, data);
        if (limitExp.isPresent() && !limitExp.isNIL() && limitExp.isFree(S.Indeterminate)
            && limitExp.isFree(S.Limit)) {

          // Allow Symja's native power logic to resolve forms like E^Infinity -> Infinity
          IExpr result = engine.evaluate(F.Power(base, limitExp));
          if (!result.isPower() || !result.base().equals(base)) {
            return result;
          }

          // Fallback for unresolved symbolic bases (e.g., a^Infinity -> ConditionalExpression)
          if (limitExp.isInfinity() || limitExp.isNegativeInfinity()) {
            IExpr evalLogBase = engine.evaluate(F.Log(base));
            if (evalLogBase.isNumericFunction(true)) {
              boolean isInf = limitExp.isInfinity();
              if (engine.evaluate(F.Greater(evalLogBase, F.C0)).isTrue()) {
                return isInf ? F.CInfinity : F.C0;
              }
              if (engine.evaluate(F.Less(evalLogBase, F.C0)).isTrue()) {
                return isInf ? F.C0 : F.CInfinity;
              }
              if (engine.evaluate(F.Equal(evalLogBase, F.C0)).isTrue()) {
                return F.C1;
              }
            } else {
              if (limitExp.isNegativeInfinity()) {
                return F.ConditionalExpression(F.CInfinity, F.Less(evalLogBase, F.C0));
              } else {
                return F.ConditionalExpression(F.CInfinity, F.Greater(evalLogBase, F.C0));
              }
            }
          }
          return result;
        }
      }
    }

    if (exponent.isFree(data.variable())) {
      final IExpr temp = evalLimitQuiet(base, data);
      if (temp.isPresent()) {
        if (temp.isZero()) {
          if (!exponent.isNumericFunction(true)) {
            // ConditionalExpression(0, exponent > 0)
            return F.ConditionalExpression(F.C0, F.Greater(exponent, F.C0));
          }
        } else {
          if (temp.isFree(data.variable())) {
            // ConditionalExpression(0, exponent > 0)
            return F.Power(temp, exponent);
          }
        }
      }
      if (base.isTimes()) {
        IAST isFreeResult =
            base.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
        // Only factor out a constant part when there genuinely is one (arg1 != 1) and a
        // variable-dependent part remains (arg2 != 1). If the base is entirely
        // variable-dependent, arg2 equals the whole base and recursing on
        // Power(arg2, exponent) would loop forever (issue #1420).
        if (!isFreeResult.arg1().isOne() && !isFreeResult.arg2().isOne()) {
          IExpr innerLimit = data.limit(F.Power(isFreeResult.arg2(), exponent));
          if (innerLimit.isNIL()) {
            return F.NIL; // never embed the NIL sentinel into a result AST
          }
          return F.Times(F.Power(isFreeResult.arg1(), exponent), innerLimit);
        }
      }
    }
    if (exponent.isNumericFunction(true)) {
      // Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
      // IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
      IExpr temp = evalLimitQuiet(base, data);
      if (temp.isNumericFunction(true)) {
        if (temp.isZero()) {
          if (exponent.isPositive()) {
            // 0 ^ (positve exponent)
            return F.C0;
          }
          if (exponent.isNegative()) {
            // 0 ^ (negative exponent)
            if (exponent.isInteger()) {
              IInteger n = (IInteger) exponent;
              if (n.isEven()) {
                return F.CInfinity;
              }
              if (data.direction() == Direction.TWO_SIDED) {
                return S.Indeterminate;
              } else if (data.direction() == Direction.FROM_ABOVE
                  || data.direction() == Direction.FROM_BELOW) {
                return directedInfinityForZeroBase(base, exponent, data, engine);
              }

            } else if (exponent.isFraction() || (exponent.isNumber() && !exponent.isInteger())) {
              if (data.direction() == Direction.TWO_SIDED) {
                return S.Indeterminate;
              } else if (data.direction() == Direction.FROM_ABOVE
                  || data.direction() == Direction.FROM_BELOW) {
                return directedInfinityForZeroBase(base, exponent, data, engine);
              }
            }
          }
          return F.NIL;
        }
        return F.Power(temp, exponent);
      }
      if (temp.isNIL()) {
        temp = base;
      }
      if (exponent.isInteger()) {
        IInteger n = (IInteger) exponent;
        if (temp.isInfinity()) {
          if (n.isPositive()) {
            return temp;
          } else if (n.isNegative()) {
            return F.C0;
          }
          return F.NIL;
        } else if (temp.isNegativeInfinity()) {
          if (n.isPositive()) {
            if (n.isEven()) {
              return F.CInfinity;
            } else {
              return F.CNInfinity;
            }
          } else if (n.isNegative()) {
            return F.C0;
          }
          return F.NIL;
        } else if (temp.isIndeterminate() || temp.isAST(S.Limit)) {
          return F.NIL;
        }
        if (n.isPositive()) {
          return F.Power(temp, n);
        } else if (n.isNegative() && n.isEven()) {
          return F.Power(temp, n);
        }
      }
    }
    return F.NIL;
  }

  /**
   * True if <code>expr -> +Infinity</code> as <code>x -> Infinity</code>. Stirling's approximation
   * of the Gamma family is only valid for a divergent (large positive) argument - substituting it
   * for e.g. <code>Gamma(1/7 + 1/x)</code> (argument limit 1/7) produces a wrong closed form.
   * Conservative: any evaluation failure counts as "not divergent" (less simplification, never a
   * wrong substitution).
   */
  private static boolean divergesAtInfinity(IExpr expr, ISymbol x) {
    if (expr.isFree(x)) {
      return false;
    }
    if (expr.equals(x)) {
      return true; // the overwhelmingly common Gamma(x) case, without engine recursion
    }
    try {
      LimitData data = new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
      IExpr lim = evalLimitQuiet(expr, data);
      return lim.isInfinity();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  private static IExpr replaceStirling(IExpr expr, ISymbol x, EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      switch (ast.validHeadID()) {
        case ID.Factorial: {
          // x! -> Gamma(x+1)
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          return replaceStirling(engine.evaluate(F.Gamma(F.Plus(arg, F.C1))), x, engine);
        }
        case ID.Pochhammer: {
          // Pochhammer(a, b) -> Gamma(a+b)/Gamma(a)
          IExpr a = replaceStirling(ast.arg1(), x, engine);
          IExpr b = replaceStirling(ast.arg2(), x, engine);
          return replaceStirling(engine.evaluate(F.Divide(F.Gamma(F.Plus(a, b)), F.Gamma(a))), x,
              engine);
        }
        case ID.Gamma: {
          // Stirling's Approximation maps Gamma growth strictly to Exp and Log
          // Gamma(z) ~ Sqrt(2*Pi/z) * Exp(z*Log(z) - z + 1/(12*z))
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          if (!divergesAtInfinity(arg, x)) {
            return F.Gamma(arg); // Stirling invalid for a non-divergent argument
          }
          return engine.evaluate(F.Times(F.Power(F.Divide(F.Times(F.C2, S.Pi), arg), F.C1D2),
              F.Exp(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
                  F.Divide(F.C1, F.Times(F.ZZ(12), arg))))));
        }
        case ID.LogGamma: {
          // LogGamma(z) ~ z*Log(z) - z + (1/2)*Log(2*Pi/z) + 1/(12*z)
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          if (!divergesAtInfinity(arg, x)) {
            return F.LogGamma(arg); // Stirling invalid for a non-divergent argument
          }
          return engine.evaluate(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
              F.Times(F.C1D2, F.Log(F.Divide(F.Times(F.C2, S.Pi), arg))),
              F.Divide(F.C1, F.Times(F.ZZ(12), arg))));
        }
        case ID.PolyGamma: {
          // Digamma: PolyGamma(0, z) ~ Log(z) - 1/(2z) for a divergent argument free of
          // nested PolyGamma (the arg was already recursed, so psi(psi(x)) arrives here
          // with the inner level expanded; Log-bearing args are fine - see mrv PolyGamma)
          if (ast.argSize() == 2 && ast.arg1().isZero()) {
            IExpr arg = replaceStirling(ast.arg2(), x, engine);
            if (!divergesAtInfinity(arg, x)
                || !arg.isFree(t -> t.isAST(S.PolyGamma), true)) {
              return F.PolyGamma(F.C0, arg);
            }
            return engine.evaluate(
                F.Plus(F.Log(arg), F.Negate(F.Divide(F.C1, F.Times(F.C2, arg)))));
          }
          break;
        }
        default:
          break;
      }
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        result.append(replaceStirling(ast.get(i), x, engine));
      }
      return engine.evaluate(result);
    }
    return expr;
  }

  /**
   * Replace Abs(variable) with the variable or its negation based on the approach direction.
   * 
   * @param expr the expression
   * @param variable the limit variable
   * @param direction -1 for from-right (positive side), 1 for from-left (negative side)
   * @param limitValue the value being approached
   * @return the rewritten expression
   */
  private static IExpr rewriteAbsForDirection(IExpr expr, IExpr variable, Direction direction,
      IExpr limitValue) {
    if (limitValue.isZero()) {
      if (direction == Direction.FROM_ABOVE) {
        return F.subst(expr,
            x -> (x.isAST(S.RealAbs, 2) || x.isAST(S.Abs, 2)) && x.first().equals(variable),
            variable);
      } else if (direction == Direction.FROM_BELOW) {
        return F.subst(expr,
            x -> (x.isAST(S.RealAbs, 2) || x.isAST(S.Abs, 2)) && x.first().equals(variable),
            variable.negate());
      }
    }
    return F.NIL;
  }

  /**
   * Determine the directed infinity for {@code 0^(negative exponent)} when approaching from a
   * specific direction. Uses {@link #signViaApproach} to detect whether the base is positive or
   * negative near the limit point.
   *
   * @param base the base expression approaching zero
   * @param exponent the negative exponent
   * @param data the limit data (must not be {@link Direction#TWO_SIDED})
   * @param engine the evaluation engine
   * @return a directed infinity, or {@link F#NIL} if the sign could not be determined
   */
  private static IExpr directedInfinityForZeroBase(IExpr base, IExpr exponent, LimitData data,
      EvalEngine engine) {
    IExpr limitVal = data.limitValue();
    int sign = signViaApproach(base, data.variable(), limitVal, data.direction(), engine);
    if (sign == 1) {
      return F.CInfinity;
    } else if (sign == -1) {
      if (exponent.isInteger() && ((IInteger) exponent).isOdd()) {
        return F.CNInfinity;
      }
      return F.DirectedInfinity(engine.evaluate(F.Power(F.CN1, exponent)));
    }
    // Fallback based on direction
    if (data.direction() == Direction.FROM_BELOW) {
      if (exponent.isInteger() && ((IInteger) exponent).isOdd()) {
        return F.CNInfinity;
      }
      return F.DirectedInfinity(engine.evaluate(F.Power(F.CN1, exponent)));
    }
    return F.CInfinity;
  }

  /**
   * Evaluates limits for special functions when their arguments evaluate to DirectedInfinity.
   * * @param ast the unevaluated AST containing the DirectedInfinity
   * 
   * @return the resolved limit, or F.NIL if not applicable
   */
  private static IExpr directedInfinityLimit(IAST ast) {
    if (ast.arg1().isDirectedInfinity() && ast.isAST1()) {
      IExpr head = ast.head();
      IExpr z = ((IAST) ast.arg1()).arg1();

      if (z.isValidBuiltInFunction() && z.isNumericFunction()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Erf:
            try {
              double re = z.re().evalf();
              double im = z.im().evalf();

              if (Math.abs(re) >= Math.abs(im)) {
                if (re > 0) {
                  return F.C1;
                } else if (re < 0) {
                  return F.CN1;
                }
              }
            } catch (ArgumentTypeException ate) {
              // evalf can fail for some symbolic expressions
            }
            break;
          case ID.Erfc:
            try {
              double re = z.re().evalf();
              double im = z.im().evalf();

              if (Math.abs(re) >= Math.abs(im)) {
                if (re > 0) {
                  return F.C0;
                } else if (re < 0) {
                  return F.C2;
                }
              }
            } catch (ArgumentTypeException ate) {
              // evalf can fail for some symbolic expressions
            }
            break;
          case ID.Erfi:
            try {
              double re = z.re().evalf();
              double im = z.im().evalf();

              if (Math.abs(im) >= Math.abs(re)) {
                if (im > 0) {
                  return F.CI;
                } else if (im < 0) {
                  return F.CNI;
                }
              }
            } catch (ArgumentTypeException ate) {
              // evalf can fail for some symbolic expressions
            }
            break;
        }
      }
    }
    return F.NIL;
  }


  /**
   * Compute the sign of {@code baseExpr} as {@code variable} approaches {@code limitValue} from the
   * given {@code direction} by substituting a dummy variable approaching Infinity and delegating to
   * {@link GruntzLimit#signInf}.
   *
   * @param baseExpr the expression whose sign is to be determined
   * @param variable the limit variable
   * @param limitValue the value being approached
   * @param direction the approach direction ({@link Direction#FROM_BELOW} or
   *        {@link Direction#FROM_ABOVE})
   * @param engine the evaluation engine
   * @return {@code 1}, {@code -1}, or {@code 0} as determined by {@link GruntzLimit#signInf}, or
   *         {@code 0} if the approach could not be constructed
   */
  private static int signViaApproach(IExpr baseExpr, ISymbol variable, IExpr limitValue,
      Direction direction, EvalEngine engine) {
    if (limitValue.isInfinity() || limitValue.isNegativeInfinity()
        || limitValue.isDirectedInfinity()) {
      return 0;
    }
    ISymbol w = F.Dummy("w");
    IExpr approach;
    if (direction == Direction.FROM_BELOW) {
      approach =
          limitValue.isZero() ? F.Divide(F.CN1, w) : F.Subtract(limitValue, F.Divide(F.C1, w));
    } else {
      approach = limitValue.isZero() ? F.Divide(F.C1, w) : F.Plus(limitValue, F.Divide(F.C1, w));
    }
    IExpr substituted = engine.evaluate(F.subst(baseExpr, variable, approach));
    return GruntzLimit.signInf(substituted, w, engine);
  }

  /**
   * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity
   * </code> or <code>-Infinity</code>, <code>y</code> approaches <code>0</code>.
   *
   * @param arg1
   * @param data (the datas limit must be Infinity or -Infinity)
   * @param engine
   * @return <code>F.NIL</code> if the substitution didn't succeed.
   */
  private static IExpr substituteInfinity(final IAST arg1, LimitData data, EvalEngine engine) {
    ISymbol x = data.variable();
    IExpr y = F.Power(x, F.CN1); // substituting by 1/x
    IExpr temp = engine.evalQuiet(F.subst(arg1, x, y));
    if (temp.isTimes()) {
      Optional<IExpr[]> parts =
          AlgebraUtil.fractionalPartsTimesPower((IAST) temp, true, false, true, true, true, true);
      if (parts.isPresent()) {
        if (!parts.get()[1].isOne()) { // denominator != 1
          LimitData ndData = new LimitData(x, F.C0, F.Rule(x, F.C0), data.direction());
          temp = numeratorDenominatorLimit(parts.get()[0], parts.get()[1], ndData, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    return F.NIL;
  }

  private static IExpr timesConjugateLHospital(final IAST plusAST, int indexOf, LimitData data) {
    IExpr factor1 = plusAST.removeAtCopy(indexOf).oneIdentity0();
    IExpr factor2 = plusAST.get(indexOf);
    IExpr numerator = F.evalExpand(F.Subtract(F.Sqr(factor1), F.Sqr(factor2)));
    IExpr denominator = F.eval(F.Subtract(factor1, factor2));
    // IASTMutable timesConjugate = F.Times(numerator, F.Power(denominator, F.CN1));
    return numeratorDenominatorLimit(numerator, denominator, data, EvalEngine.get());
    // temp = evalLimitQuiet(timesConjugate, data);

  }

  private static IExpr timesLimit(final IAST timesAST, LimitData data, EvalEngine engine) {
    IAST isFreeResult =
        timesAST.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
    if (!isFreeResult.arg1().isOne()) {
      IExpr freeOfVariable = isFreeResult.arg1();
      IExpr limit = data.limit(isFreeResult.arg2());
      if (limit.isNIL()) {
        // never embed the NIL sentinel into a result AST - evaluating
        // Times(freeOfVariable, NIL) later aborts with "unexpected NIL expression"
        return F.NIL;
      }
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        if (engine.evaluate(F.Greater(freeOfVariable, F.C0)).isTrue()) {
          return limit.isInfinity() ? F.CInfinity : F.CNInfinity;
        }
        if (engine.evaluate(F.Less(freeOfVariable, F.C0)).isTrue()) {
          return limit.isInfinity() ? F.CNInfinity : F.CInfinity;
        }
      }

      return F.Times(freeOfVariable, limit);
    }
    Optional<IExpr[]> parts =
        AlgebraUtil.fractionalPartsTimesPower(timesAST, true, false, true, true, true, true);
    if (parts.isEmpty()) {
      IAST[] timesPolyFiltered = timesAST.filter(x -> x.isPolynomial(data.variable));
      if (timesPolyFiltered[0].size() > 1 && timesPolyFiltered[1].size() > 1) {
        IExpr first = data.limit(timesPolyFiltered[0].oneIdentity1());
        if (first.isNIL()) {
          return F.NIL; // evaluating the NIL sentinel would abort the evaluation
        }
        first = engine.evaluate(first);
        if (first.isIndeterminate()) {
          return S.Indeterminate;
        }
        IExpr second = data.limit(timesPolyFiltered[1].oneIdentity1());
        if (second.isNIL()) {
          return F.NIL;
        }
        second = engine.evaluate(second);
        if (second.isIndeterminate()) {
          return S.Indeterminate;
        }
        if (first.isRealResult() || second.isRealResult()) {
          IExpr temp = engine.evaluate(F.Times(first, second));
          if (!temp.isIndeterminate()) {
            return temp;
          }
          if (data.limitValue().isZero()) {
            // Try reciprocal of symbol and approach to +/- Infinity
            IExpr newTimes =
                timesAST.replaceAll(F.Rule(data.variable, F.Power(data.variable, F.CN1)));
            if (newTimes.isPresent()) {
              IAST infinityExpr =
                  (data.direction == Direction.FROM_BELOW) ? F.CNInfinity : F.CInfinity;
              LimitData copy = new LimitData(data.variable, infinityExpr,
                  F.Rule(data.variable, infinityExpr), data.direction);
              temp = engine.evaluate(copy.limit(newTimes));
              if (!temp.isIndeterminate()) {
                return temp;
              }
            }
          }
        }
      }
    } else {

      IExpr numerator = parts.get()[0];
      IExpr denominator = parts.get()[1];
      IExpr limit = data.limitValue();
      ISymbol symbol = data.variable();
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        try {
          // Add Expand to safely parse newly extracted negative polynomials
          IExpr expNumerator = engine.evaluate(F.Expand(numerator));
          IExpr expDenominator = engine.evaluate(F.Expand(denominator));
          ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
          ExprPolynomial denominatorPoly = ring.create(expDenominator);
          ExprPolynomial numeratorPoly = ring.create(expNumerator);
          return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit,
              data);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }

      IExpr plusResult = AlgebraUtil.partsApart(parts.get(), symbol, engine);
      // Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(),
      // parts,symbol);
      if (plusResult.isPlus()) {
        return data.mapLimit((IAST) plusResult);
      }

      if (denominator.isOne()) {
        if (limit.isInfinity() || limit.isNegativeInfinity()) {
          IExpr temp = substituteInfinity(timesAST, data, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      IExpr temp = numeratorDenominatorLimit(numerator, denominator, data, engine);
      if (temp.isPresent()) {
        return temp;
      }
    }
    return data.mapLimit(timesAST);
  }

  /**
   * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of
   * Limits</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (!arg2.isRuleAST()) {
      // Limit specification `1` is not of the form x->x0.
      return Errors.printMessage(S.Limit, "lim", F.List(arg2), engine);
    }
    IAST rule = (IAST) arg2;
    if (!(rule.arg1().isSymbol())) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.Limit, "ivar", F.List(arg2), engine);
    }
    if (arg1.isList()) {
      return arg1.mapThread(ast, 1);
    }
    boolean numericMode = engine.isNumericMode();
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      engine.setNumericMode(false);
      Direction direction = Direction.TWO_SIDED; // no direction as default

      // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      IExpr directionOption = option[0];
      if (directionOption.isPresent()) {
        if (directionOption.isOne() || directionOption.isString("FromBelow")) {
          direction = Direction.FROM_BELOW;
        } else if (directionOption.isMinusOne() || directionOption.isString("FromAbove")) {
          direction = Direction.FROM_ABOVE;
        } else if (directionOption.equals(S.Automatic) || directionOption.equals(S.Reals)
            || directionOption.isString("TwoSided")) {
          direction = Direction.TWO_SIDED;
        } else {
          // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided
          // or a list of these.
          return Errors.printMessage(S.Limit, "ldir", F.List(ast.arg3()), engine);
        }
      } else {
        // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided or
        // a list of these.
        return Errors.printMessage(S.Limit, "ldir", F.List(S.Null), engine);
      }

      IExpr assumptionOption = option[1];
      // IExpr generateConditionOption = option[2];

      IExpr assumptionExpr = OptionArgs.determineAssumptions(assumptionOption);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        if (oldAssumptions != null) {
          engine.setAssumptions(oldAssumptions.copy().addAssumption(assumptionExpr));
        } else {
          IAssumptions assumptions =
              org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
          }
        }
      }

      if (arg1.has(S.Abs, true)) {
        IExpr rewritten = rewriteAbsForDirection(arg1, rule.arg1(), direction, rule.arg2());
        if (rewritten.isPresent()) {
          // Compute the limit on the rewritten expression (without Abs)
          arg1 = rewritten;
        }
      }

      // Top-level algebraic simplification: cancel a rational expression like
      // (x^2-a^2)/(x-a) -> x+a so that 0/0 forms at a finite (possibly symbolic)
      // limit point are reduced to a polynomial whose limit can be taken by
      // direct substitution. This runs exactly once per top-level Limit call.
      if (arg1.isTimes() && arg1.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT
          && !arg1.isFree(rule.arg1())) {
        boolean hasDenominator = ((IAST) arg1).exists(x -> x.isPower() && x.exponent().isInteger()
            && x.exponent().isNegative() && !x.base().isFree(rule.arg1()));
        if (hasDenominator) {
          IExpr cancelled = engine.evalQuiet(F.Cancel(arg1));
          if (cancelled.isPresent() && !cancelled.isIndeterminate()
              && cancelled.leafCount() < arg1.leafCount()) {
            arg1 = cancelled;
          }
        }
      }

      IExpr temp = evaluateLimit(arg1, rule, direction, engine);
      if (temp.isPresent()) {
        // A RESOLVED limit must not surface machinery-internal symbols: series/Taylor
        // helpers substitute Dummy("y")-style variables and a partially aborted evaluation
        // can leak them into an otherwise definite result (observed: a Gamma-difference
        // tower "resolving" to Infinity*y). A free symbol of the result that does not
        // occur in the input marks such junk - stay unevaluated instead. Results still
        // containing Limit are exempt: a returned (possibly variable-renamed) Limit shell
        // is the engine's re-entry mechanism, not an adopted answer.
        boolean leakedSymbol = false;
        if (temp.isFree(S.Limit, true)) {
          java.util.Set<IExpr> allowedVars = new VariablesSet(ast).toSet();
          for (IExpr resultVar : new VariablesSet(temp).toSet()) {
            if (!allowedVars.contains(resultVar)) {
              if (DEBUG) {
                System.out.println("LEAK-GUARD input=" + ast + " raw=" + temp + " leakedVar="
                    + resultVar + " allowed=" + allowedVars);
              }
              leakedSymbol = true;
              break;
            }
          }
        }
        if (!leakedSymbol) {
          return IntervalSym.toAccumBoundsIndeterminate(temp);
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Limit, rex);
    } catch (StackOverflowError soe) {
      // Gamma-tower shapes (Limit(E^Gamma(x)/Gamma(x))) drive mutually recursive heuristic
      // corridors - L'Hopital derivatives, the mrv rewrite, E^(Plus) auto-Expand - through
      // raw Java recursions that no engine recursion limit observes. Recover at the builtin
      // boundary (the per-frame finally blocks have unwound the engine state) and leave the
      // Limit unevaluated; the background worker path (EvalControlledCallable) already
      // recovers from StackOverflowError the same way.
      Errors.printMessage(S.Limit, "error",
          F.List("StackOverflowError in Limit evaluation - expression stays unevaluated"),
          engine);
    } finally {
      engine.setNumericMode(numericMode);
      engine.setAssumptions(oldAssumptions);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Direction, S.Assumptions, S.GenerateConditions}, //
        new IExpr[] {S.Reals, S.$Assumptions, S.Automatic});
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
