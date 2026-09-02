package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Limit.Direction;
import org.matheclipse.core.reflection.system.Limit.LimitData;
import org.matheclipse.core.sympy.simplify.Simplify;

/**
 * The Gruntz algorithm for limits at <code>+Infinity</code> (Dominik Gruntz, <i>On Computing Limits
 * in a Symbolic Manipulation System</i>, ETH 1996): determine the set of most rapidly varying
 * subexpressions (mrv), rewrite them in a common decay variable <code>w -&gt; 0+</code>, and read
 * the limit off the leading term of the w-series.
 *
 * <p>
 * Used by {@link Limit} as the rigorous fallback (and, for a few shapes that starve the heuristics,
 * consulted first). All limits are transformed to <code>x -&gt; +Infinity</code> on entry - see
 * {@link #evaluateLimit}.
 */
public class LimitGruntz {

  private LimitGruntz() {
    // static helper class
  }

  /** Debug output for the Gruntz machinery - shared switch with {@link Limit#DEBUG}. */
  private static final boolean DEBUG = Limit.DEBUG;

  private static IExpr evalLimitQuiet(IExpr expr, LimitData data) {
    return Limit.evalLimitQuiet(expr, data);
  }

  private static IExpr logExpand(IExpr arg) {
    return Limit.logExpand(arg);
  }

  private static boolean divergesAtInfinity(IExpr expr, ISymbol x) {
    return Limit.divergesAtInfinity(expr, x);
  }

  private static IExpr digammaTailArg(IExpr z, ISymbol x, EvalEngine engine) {
    return Limit.digammaTailArg(z, x, engine);
  }

  private static IExpr digammaPrincipalArg(IExpr z, IExpr rawArg, ISymbol x, EvalEngine engine) {
    return Limit.digammaPrincipalArg(z, rawArg, x, engine);
  }

  /**
   * The per-thread state of the Gruntz machinery: the recursion depths that used to live in five
   * separate ThreadLocals, the series-expansion flag, and the two memoization caches. One object
   * per thread, reset at the true top-level entry of the Limit builtin - having every piece of
   * cross-cutting state in one place is what makes the reset story auditable.
   */
  private static final class RunState {
    /** How deep the Gruntz algorithm has recursed from external Limit fallbacks. */
    int gruntzDepth;

    /**
     * True while {@link #evalGruntz} runs its own series expansion. ASTSeriesData.taylorSeries
     * computes coefficients through engine-level Limit calls; letting those re-enter the Gruntz
     * algorithm builds a mutually recursive evalGruntz/taylorSeries tree whose combinatorial cost
     * grinds for minutes (observed on GoldenRatio-power and Sin(n*poly) inputs). Inner coefficient
     * limits may use the ordinary heuristics - just never a nested Gruntz run.
     */
    boolean inSeries;

    /** Nesting cap counter for the Gruntz-internal exponent-limit in the mrv Power cases. */
    int mrvExpDepth;

    /** Nesting cap counter for the Gruntz-internal comparison limit in {@link #compareGrowth}. */
    int compareGrowthDepth;

    /** Nesting cap counter for the comparability-coefficient limit in {@link #rewrite}. */
    int rewriteRatioDepth;

    /**
     * Memoizes {@link #compareGrowth} within one user-level Limit call. The growth comparison of a
     * pair (f, g) as x -> Infinity is a pure function of the pair, but mrv-set construction
     * re-derives the same comparisons dozens of times and each heuristic ratio-limit costs seconds
     * (observed: thesis 8.14's tower ratio recomputed 12+ times at ~1-4s each = the whole budget).
     * Keyed on the printed form, NOT the IExpr: the mrv/rewrite passes mint a fresh {@code Dummy}
     * variable each time, so structurally-identical terms are not {@code .equals()} - only their
     * printed form is stable. That is sound because the comparison is intrinsic (always "as the
     * variable -> +Infinity", direction-independent), so equal printed forms always mean equal
     * growth comparisons - and the variable prefixes the key. Cleared by
     * {@link #clearSessionCaches()} so it never grows unbounded across a session.
     */
    final Map<String, Integer> compareGrowthCache = new HashMap<>();

    /**
     * Memoizes {@link #signInf} within one user-level Limit call, same rationale and printed-form
     * keying as {@link #compareGrowthCache}. Definitive results (+1/0/-1) are cached
     * unconditionally; an "unknown" is cached tagged with the tier that failed to resolve it
     * ({@link #UNKNOWN_SHALLOW} / {@link #UNKNOWN_DEEP}) and is only served back to that same tier
     * - the other tier resolves through different means (heuristic limits vs numeric sampling) and
     * may succeed where this one failed.
     */
    final Map<String, Integer> signInfCache = new HashMap<>();

    /**
     * Memoizes the comparability coefficients <code>c = lim f/g</code> of {@link #rewrite}, keyed
     * like {@link #signInfCache} (variable-prefixed printed form). The two-sided split and the
     * several Gruntz attempts of one user call re-derive identical ratios; only clean adopted
     * values are stored.
     */
    final Map<String, IExpr> rewriteRatioCache = new HashMap<>();

    /** One-slot cache for the ubiquitous "variable -> +Infinity from below" LimitData. */
    ISymbol infinityDataVariable;

    /** See {@link #infinityDataVariable}. */
    LimitData infinityData;
  }

  private static final ThreadLocal<RunState> RUN = ThreadLocal.withInitial(RunState::new);

  /** True while a Gruntz evaluation is on the current thread's stack. */
  static boolean isActive() {
    return RUN.get().gruntzDepth > 0;
  }

  /** True while the Gruntz w-series expansion is running - see {@link RunState#inSeries}. */
  static boolean isInGruntzSeries() {
    return RUN.get().inSeries;
  }

  /** {@link RunState#signInfCache} marker: undecided by the heuristic tier (depth &lt;= 2). */
  private static final int UNKNOWN_SHALLOW = Integer.MIN_VALUE + 1;

  /** {@link RunState#signInfCache} marker: undecided by the sampling tier (depth &gt; 2). */
  private static final int UNKNOWN_DEEP = Integer.MIN_VALUE + 2;

  /**
   * Drop the growth-comparison and asymptotic-sign caches. Called by the Limit builtin at the TRUE
   * top-level entry (not on internal re-entries): entries are intrinsic facts about "expression as
   * printed variable -&gt; +Infinity", so they stay valid across the several Gruntz attempts one
   * user-level Limit makes - but they must not leak across user calls, whose assumptions
   * ($Assumptions) can differ, and the sign cache is also fed by non-Gruntz callers (Max/Min
   * rewrites, step functions) and would otherwise grow without bound.
   */
  static void clearSessionCaches() {
    RunState run = RUN.get();
    run.compareGrowthCache.clear();
    run.signInfCache.clear();
    run.rewriteRatioCache.clear();
  }

  /**
   * The shared immutable "variable -&gt; +Infinity (from below)" {@link LimitData} used by every
   * heuristic sub-limit of the Gruntz machinery - one instance per variable instead of a fresh
   * allocation (data + rule AST) per query.
   */
  private static LimitData infinityLimitData(ISymbol x) {
    RunState run = RUN.get();
    if (run.infinityDataVariable != x) {
      run.infinityDataVariable = x;
      run.infinityData =
          new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
    }
    return run.infinityData;
  }

  /**
   * Highest truncation order the adaptive w-series loop in {@link #seriesLeadingTerm} will try
   * before giving up. Each step re-expands the whole series, so this bounds the cost of chasing a
   * leading term that cancellation has pushed to a high order.
   *
   * <p>
   * Kept at {@code 2} (the loop runs once - zero-coefficient skip only). Raising it turns on
   * SymPy-style adaptive order-raising for deeper cancellation. Re-measured 2026-08 AFTER the
   * compareGrowth ratio limits were routed through the Gruntz algorithm itself (Log-bearing ratios
   * included): {@code 8} still cost ~2.6x on LimitTestOscillating with ZERO newly resolved cases -
   * the deep-cancellation shapes (Taylor tails like
   * <code>x^4*(E^(1/x)-1-1/x-1/(2x^2)-1/(6x^3))</code>, second-order digamma tails) all resolve
   * through the heuristic growth-ranking paths before they reach this loop. Raise only with a
   * concrete case in hand that fails at {@code 2} and passes at the higher order.
   */
  private static final int GRUNTZ_MAX_SERIES_ORDER = 2;

  public static IExpr combineExponentials(IExpr expr, EvalEngine engine) {
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      IASTAppendable eExponents = F.PlusAlloc(times.argSize());
      IASTAppendable newTimes = F.TimesAlloc(times.argSize());
      boolean combinedExp = false;
      boolean childChanged = false;
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = combineExponentials(times.get(i), engine);
        if (arg.isPower() && arg.base() == S.E) {
          eExponents.append(arg.exponent());
          combinedExp = true;
        } else if (arg == S.E) {
          eExponents.append(F.C1);
          combinedExp = true;
        } else {
          if (arg != times.get(i)) {
            childChanged = true;
          }
          newTimes.append(arg);
        }
      }
      if (combinedExp) {
        // Use Expand to group algebraic terms cleanly, but DO NOT evaluate the
        // outer Power/Times to prevent Symja from instantly re-splitting them!
        IExpr combinedExponent = engine.evaluate(F.Expand(eExponents));
        newTimes.append(F.Power(S.E, combinedExponent));
        return newTimes.argSize() == 1 ? newTimes.arg1() : newTimes;
      }
      if (childChanged) {
        // no E-factor at this level, but a nested rewrite must not be discarded
        return newTimes.argSize() == 1 ? newTimes.arg1() : newTimes;
      }
      return expr;
    }
    if (expr.isPower()) {
      IExpr base = combineExponentials(expr.base(), engine);
      IExpr exp = combineExponentials(expr.exponent(), engine);
      if (base.isPower() && base.base() == S.E) {
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
      // {coeff, logArg} with term == coeff*Log(logArg), or null for anything else -
      // including Log(a)*Log(b) products, which must never be split (a second Log factor
      // used to overwrite the first here, silently dropping it from the rebuilt sum and
      // corrupting e.g. Log(x)*Log(Log(x)) - Log(x) into Log(Log(x)/x))
      IExpr[] parts = Simplify.singleLogTermParts(term);
      if (parts != null && (force || parts[1].isPositiveResult())) {
        IASTAppendable args = groupMap.getOrDefault(parts[0], F.TimesAlloc(4));
        args.append(parts[1]);
        groupMap.put(parts[0], args);
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
   * Splits {@code arg == w^k * rest} where {@code k >= 1} is the minimal integer w-degree over the
   * additive terms of {@code arg} (so {@code rest}'s w-free part is nonzero). Returns
   * {@code {k, rest}} or {@code null} when no positive uniform w-power factors out structurally (w
   * buried inside Log/Exp/... bails conservatively).
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
   * Integer w-degree of a single multiplicative term: 0 when free of w, the summed power of direct
   * {@code w}/{@code w^int} factors of a Times/Power, {@link Integer#MIN_VALUE} when w occurs any
   * other way (unsupported).
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
   * Cheap structural test for "eventually strictly positive as x -> +Infinity": sums and products
   * of positive constants, x itself, and powers of such. Deliberately limit-free - a conservative
   * false is always safe for callers.
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
   * current user-level Limit call (see {@link RunState#compareGrowthCache}).
   */
  private static int compareGrowth(IExpr f, IExpr g, ISymbol x, EvalEngine engine) {
    if (f.equals(g))
      return 0; // Short-circuit identical expressions

    Map<String, Integer> cache = RUN.get().compareGrowthCache;
    // the variable prefixes the key for the same reason as in signInf: identical printed
    // pairs under different limit variables are different comparisons
    String prefix = x.toString() + '\0';
    String fKey = f.toString();
    String gKey = g.toString();
    Integer cached = cache.get(prefix + fKey + '\0' + gKey);
    if (cached != null) {
      return cached;
    }

    int result = compareGrowthUncached(f, g, x, engine);

    // The comparison is antisymmetric: cmp(g, f) == -cmp(f, g) (the 0-on-failure early
    // returns are symmetric too - they turn on signInf of the shared argument). Cache both
    // directions so the reversed comparison mrvMax may issue later is also a hit.
    cache.put(prefix + fKey + '\0' + gKey, result);
    cache.put(prefix + gKey + '\0' + fKey, -result);
    return result;
  }

  /**
   * Mathematically compares the growth classes of two expressions f and g: <code>1</code> when f
   * out-grows g, <code>-1</code> when g out-grows f, <code>0</code> for the same comparability
   * class.
   *
   * <p>
   * <b>Deliberate imprecision:</b> <code>0</code> is ALSO returned when the comparison limit cannot
   * be computed - "unknown" and "equal" are merged, and {@link #mrvMax} then merges the two
   * candidate sets instead of aborting (SymPy raises here). That permissiveness lets the algorithm
   * proceed on towers whose comparison limit the engine cannot resolve; it is safe because a
   * wrongly-merged set can no longer produce a wrong VALUE - {@link #rewrite} aborts the whole run
   * (NIL) when any element of the set fails to rewrite against the chosen representative, which is
   * exactly what happens when the classes were not actually equal. Distinguishing UNKNOWN
   * explicitly (and aborting in mrvMax) is the stricter thesis behavior; measured on the current
   * suite it only loses cases, so the merge stays.
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
        // itself - Log strips one tower level per recursion, so it terminates. Log-bearing
        // ratios (nested-log towers, thesis #70/#71/#80/#89) route the same way: the
        // oscillation breakers resolve the plain x/Log(x) shapes instantly, and everything
        // deeper is exactly what the heuristic engine times out on.
        if (ratio.has(t -> t.isExp() || t.isLog(), true) && RUN.get().compareGrowthDepth < 4) {
          RUN.get().compareGrowthDepth++;
          try {
            limitResult = evalGruntz(ratio, x, engine);
          } finally {
            RUN.get().compareGrowthDepth--;
          }
        }
        if (limitResult.isNIL()) {
          // ensure Power(Infinity, -1) evaluates to 0.
          limitResult = engine.evaluate(evalLimitQuiet(ratio, infinityLimitData(x)));
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
      Errors.rethrowsInterruptException(e);
      return 0;
    }
  }

  public static IExpr evalGruntz(IExpr expr, ISymbol x, EvalEngine engine) {
    return evalGruntz(expr, x, engine, 0);
  }

  /**
   * Evaluates the limit of <code>expr</code> as <code>x -&gt; Infinity</code> using the rigorous
   * Gruntz algorithm. The stages mirror the thesis: normalize the expression ({@link #preprocess}),
   * find the most rapidly varying subexpressions ({@link #computeMrvSet}), either move the whole
   * problem up the growth scale ({@link #moveUp}) or rewrite the mrv elements in the decay variable
   * <code>w = E^-g -&gt; 0+</code> ({@link #rewriteToW}), and read the limit off the leading term
   * of the w-series ({@link #seriesLeadingTerm}).
   */
  private static IExpr evalGruntz(IExpr expr, ISymbol x, EvalEngine engine, int depth) {
    if (depth > 10) {
      return F.NIL; // Guard against infinite Gruntz recursion
    }

    IExpr breaker = oscillationBreakerLimit(expr, x, engine);
    if (breaker.isPresent()) {
      return breaker;
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

    if (hasEventuallyNegativeBasePower(expr, x, engine)) {
      return F.NIL;
    }

    IExpr continuity = exponentContinuityLimit(expr, x, engine, depth);
    if (continuity.isPresent()) {
      return continuity;
    }

    expr = preprocess(expr, x, engine);
    if (expr.isFree(x)) {
      // the Abs/Sign elimination can leave a constant - that constant is the limit
      return expr;
    }

    // Find the MRV set of the expression
    IAST mrvSet = computeMrvSet(expr, x, engine);
    if (mrvSet == null) {
      return F.NIL;
    }

    // If the MRV set contains the limit variable x, it means there are no
    // rapidly varying exponentials. We must substitute x = Exp(u) to push the
    // expression up the mathematical growth scale so Puiseux series can process it.
    if (mrvSet.contains(x)) {
      return moveUp(expr, x, engine, depth);
    }

    // Extract representative growth term g
    IExpr g = getRepresentativeG(mrvSet, x, engine);
    if (!g.isPresent() || g.isNIL()) {
      return F.NIL;
    }

    // Rewrite expression in terms of decay variable w = Exp(-g) -> 0+
    ISymbol w = F.Dummy("w");
    IExpr rewritten = rewriteToW(expr, mrvSet, g, x, w, engine);
    if (rewritten.isNIL()) {
      // an mrv element could not be rewritten - a partial rewrite would mislead the
      // series machinery into a wrong leading term, so give up honestly
      return F.NIL;
    }

    if (hasDivergentTrigArgument(rewritten, w, engine)) {
      return F.NIL;
    }

    if (hasIrrationalWPower(rewritten, w)) {
      return irrationalPowerLimit(rewritten, w, expr, x, engine, depth);
    }

    // Calculate the series expansion of the rewritten expression around w = 0.
    if (DEBUG) {
      System.out.println("GRUNTZ pre-series g=" + g + " rewritten=" + rewritten);
    }
    SeriesLead lead = seriesLeadingTerm(rewritten, w, engine);
    if (lead == null) {
      if (DEBUG) {
        System.out.println("GRUNTZ series=null/allzero for " + rewritten);
      }
      return F.NIL;
    }
    int minExp = lead.exponent;
    IExpr leadCoeff = lead.coefficient;

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
   * --- GRUNTZ OSCILLATION BREAKERS --- The Gruntz algorithm mathematically oscillates between
   * <code>E^x/x</code> and <code>x/Log(x)</code> because their Puiseux series expansions contain
   * non-polynomial logs. Intercept these canonical forms and resolve their asymptotic dominance
   * instantly.
   *
   * @return the limit for one of the canonical ratio shapes, {@link F#NIL} when not applicable
   */
  private static IExpr oscillationBreakerLimit(IExpr expr, ISymbol x, EvalEngine engine) {
    if (!expr.isTimes() && !expr.isPower()) {
      return F.NIL;
    }
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
    return F.NIL;
  }

  /**
   * A power with an eventually-NEGATIVE base and an x-dependent exponent - e.g.
   * <code>(-z)^(3+z)</code> from transforming <code>Limit(x^(x-3), x-&gt;-Infinity)</code> via
   * <code>x = -z</code> - is complex-valued (branch cuts): the real-line Gruntz algorithm cannot
   * handle it, and its mrv/rewrite recursion grinds indefinitely on the resulting
   * <code>Log(-z)</code> forms (observed hanging Maximize inside NSolve, SolveTest#testNSolve).
   * Refuse fast so the ordinary heuristics handle it (they return it unevaluated). A base is
   * "eventually negative" when its negation is structurally positive - cheap and conservative,
   * never fires on the positive bases the gruntz-first power gate legitimately targets
   * (<code>(1+1/x)</code>, <code>(x^7+..)/(2^x+..)</code>, Log-towers).
   */
  private static boolean hasEventuallyNegativeBasePower(IExpr expr, ISymbol x, EvalEngine engine) {
    return expr.has(p -> p.isPower() && !p.exponent().isFree(x)
        && isStructurallyPositive(engine.evaluate(F.Negate(p.base())), x), true);
  }

  /**
   * A pure exponential <code>E^f</code>: by continuity the limit is <code>E^(lim f)</code> -
   * recurse on the exponent directly instead of grinding the series machinery on tower-bearing
   * coefficients (thesis 8.20's E^(log-nest ratio) burned whole time budgets there). The engine
   * auto-writes <code>E^(Log(b)*c)</code> as <code>b^c</code>, so the same continuity route must
   * also catch <code>Power(Log-tower base, x-dependent exponent)</code> via
   * <code>f = c*Log(b)</code> - the Log base tends to +Infinity, making the rewrite
   * <code>b^c = E^(c*Log(b))</code> valid. Two exclusions: plain exponents (the E^u moveup
   * artifacts) must use the ordinary machinery to avoid an infinite moveup cycle, and
   * Plus-of-exponential exponents (E^A - E^B differences, thesis 8.14) mis-sign in the recursion -
   * the general machinery handles them.
   *
   * @return the limit when the route applies AND resolves, otherwise {@link F#NIL} (fall through to
   *         the general machinery)
   */
  private static IExpr exponentContinuityLimit(IExpr expr, ISymbol x, EvalEngine engine,
      int depth) {
    IExpr contExponent = F.NIL;
    if (expr.isExp()) {
      contExponent = expr.exponent();
    } else if (expr.isPower()
        && !expr.base().isFree(x) && !expr.exponent().isFree(x) && expr.base()
            .isFree(t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc), true)
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
      IExpr exponentLimit = evalGruntz(engine.evaluate(contExponent), x, engine, depth + 1);
      if (exponentLimit.isPresent()) {
        if (exponentLimit.isInfinity()) {
          return F.CInfinity;
        }
        if (exponentLimit.isNegativeInfinity()) {
          return F.C0;
        }
        if (exponentLimit.isFree(x) && exponentLimit.isFree(S.DirectedInfinity)
            && exponentLimit.isIndeterminateFree()) {
          // E^(c*Log(b)) has no auto-evaluation rule - (2^x+3^x)^(3/x) would report
          // E^(3*Log(3)) instead of 27. The result is limit-variable-free, so collapsing it
          // with Simplify is cheap; adopt it only when it stays well-defined.
          return collapseConstant(engine.evaluate(F.Exp(exponentLimit)), engine);
        }
      }
      // exponent limit unknown - fall through to the general machinery
    }
    return F.NIL;
  }

  /**
   * Normalization pipeline before mrv extraction: hyperbolics to exponentials (left intact they act
   * as opaque constants during rewrite and series expansion), Abs/Sign elimination
   * (<code>x -&gt; +Infinity</code> on the real line - also prevents the auto-evaluator from
   * turning <code>Abs(E^(-u))</code> into <code>E^(-Re(u))</code>, whose complex Re/Im cause
   * infinite MRV recursion), exponential re-combination (the engine aggressively re-splits
   * <code>E^(-1-2z+...)</code> into <code>E^-1 * E^(-2z)</code> - force them back together before
   * mrv extraction to prevent the Slower Exponential Discard Trap), and Stirling-log merging so
   * those logs cancel instead of polluting the growth classes.
   *
   * <p>
   * May return an x-FREE constant (Abs/Sign elimination) - the caller returns it as the limit.
   */
  private static IExpr preprocess(IExpr expr, ISymbol x, EvalEngine engine) {
    expr = expandHyperbolics(expr, engine);

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

    // both passes are identities when their head is absent - skip the full recursive
    // traversal in that (common) case
    if (!expr.isFree(S.E, true)) {
      expr = combineExponentials(expr, engine);
    }
    if (!expr.isFree(S.Log, true)) {
      expr = logCombine(expr, true, x);
    }
    return expr;
  }

  /** The deduplicated mrv set of <code>expr</code>, or <code>null</code> when unsupported. */
  private static IAST computeMrvSet(IExpr expr, ISymbol x, EvalEngine engine) {
    IExpr mrvResult = mrv(expr, x, engine);
    if (!mrvResult.isPresent() || !mrvResult.isAST()) {
      if (DEBUG) {
        System.out.println("GRUNTZ mrv=NIL for " + expr);
      }
      return null;
    }
    mrvResult = S.DeleteDuplicates.of(engine, mrvResult);
    if (DEBUG) {
      System.out.println("MRV " + expr + " mrvResult: " + mrvResult);
    }
    return (IAST) mrvResult;
  }

  /**
   * The mrv set contains the limit variable itself - no rapidly varying exponentials: substitute
   * <code>x = E^u</code> to push the whole problem one level up the growth scale and recurse.
   *
   * <p>
   * Simplifies <code>Log(E^f) -&gt; f</code> structurally and splits logs of products containing an
   * exponential factor (<code>Log(E^u*Log(u)) -&gt; u + Log(Log(u))</code> via logExpand): the
   * engine does neither for a plain dummy (complex branch caution), but under the Gruntz convention
   * the dummy is real and positive, and the simplification is VITAL for termination (sympy
   * gruntz.py notes the same) - without it a log-nest like <code>Log(x)+Log(Log(x))</code> moves up
   * to the opaque <code>Log(E^u + Log(E^u))</code> instead of the analyzable
   * <code>Log(E^u + u)</code>.
   */
  private static IExpr moveUp(IExpr expr, ISymbol x, EvalEngine engine, int depth) {
    ISymbol u = F.Dummy("u");
    IExpr substituted = engine.evaluate(F.subst(expr, x, F.Exp(u)));

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

  /**
   * Rewrite <code>expr</code> in the decay variable <code>w = E^-g -&gt; 0+</code> and normalize
   * the result for the series machinery: expand <code>Log(f(w)/w^k)</code> into
   * <code>Log(f(w)) - k*Log(w)</code> (avoids limit cycles on the singularity), substitute
   * <code>Log(w) = -g</code> back (removes the singularity at <code>w=0</code>), {@code Cancel}
   * nested denominators per term (a single massive fraction crashes the Laurent inversion), and
   * pre-expand exponential exponents with apparent w-poles.
   *
   * @return the normalized rewrite, or {@link F#NIL} when an mrv element could not be rewritten
   */
  private static IExpr rewriteToW(IExpr expr, IAST mrvSet, IExpr g, ISymbol x, ISymbol w,
      EvalEngine engine) {
    IExpr rewritten = rewrite(expr, mrvSet, g, x, w, engine);
    if (rewritten.isNIL()) {
      return F.NIL;
    }

    rewritten = expandGruntzLogs(rewritten, w, engine);

    rewritten = engine.evaluate(F.subst(rewritten, F.Log(w), F.Negate(g)));

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

    return preExpandExpExponents(rewritten, w, engine);
  }

  /**
   * A circular trig function whose argument DIVERGES as <code>w-&gt;0+</code> (e.g.
   * <code>Sin(n*poly(n))</code> after the moveup substitution) has no Taylor series. The series
   * machinery would not find that out cheaply - taylorSeries computes coefficients through
   * recursive Limit calls, and on such inputs the nested evalGruntz/taylorSeries tree grinds for
   * minutes (observed via AsymptoticRSolveValue). Refuse up front; a trig argument that converges
   * at <code>w=0</code> (finite valuation) is fine and keeps the Sin(1/x+E^(-x))-style cases
   * working.
   */
  private static boolean hasDivergentTrigArgument(IExpr rewritten, ISymbol w, EvalEngine engine) {
    return rewritten.has(t -> {
      if (t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc) && !t.isFree(w, true)) {
        IExpr[] argLead = leadingWPower(t.first(), w, engine);
        if (argLead == null) {
          return true; // cannot prove the argument converges - refuse
        }
        double argValuation = engine.evaluate(argLead[0]).evalfNaN();
        // a non-numeric valuation cannot be proven to converge - refuse
        return Double.isNaN(argValuation) || argValuation < -1.0e-9;
      }
      return false;
    }, true);
  }

  /**
   * Same-class exponentials with an irrational log-ratio (e.g. <code>{3^x, 5^x}</code>) rewrite to
   * powers like <code>w^(1-Log(5)/Log(3))</code>. ASTSeriesData is a rational-exponent
   * Laurent-Puiseux machine and silently drops such terms, turning <code>3^x/(3^x+5^x)</code> into
   * the wrong finite limit <code>1</code>. For those shapes bypass the series entirely.
   */
  private static boolean hasIrrationalWPower(IExpr rewritten, ISymbol w) {
    return rewritten.has(p -> p.isPower() && p.base().equals(w) && !p.exponent().isRational(),
        true);
  }

  /**
   * Extract the leading w-power and its coefficient structurally (exponents compared numerically)
   * instead of through the rational-exponent series machinery - see {@link #hasIrrationalWPower}.
   * The standard Gruntz case split on the leading exponent then applies as usual.
   */
  private static IExpr irrationalPowerLimit(IExpr rewritten, ISymbol w, IExpr expr, ISymbol x,
      EvalEngine engine, int depth) {
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
      v = valuation.evalfNaN();
      if (Double.isNaN(v)) {
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

  /** Leading term of the w-series around <code>w = 0</code>. */
  private static final class SeriesLead {
    final int exponent;
    final IExpr coefficient;

    SeriesLead(int exponent, IExpr coefficient) {
      this.exponent = exponent;
      this.coefficient = coefficient;
    }
  }

  /**
   * The leading term of the w-series of <code>rewritten</code> around <code>w = 0</code>, with
   * nested Gruntz entries blocked while the series machinery runs (see {@link RunState#inSeries}).
   *
   * <p>
   * The leading term of the w-series can vanish through cancellation - two exponentials of the same
   * comparability class subtracting (thesis 8.14's tower difference), or a second-order asymptotic
   * tail surviving after the first-order parts cancel (the <code>-1/(2*Log(x))</code> in the
   * digamma towers). A single fixed-order expansion then reports a zero leading coefficient at
   * minExponent and the caller's case split would return a spurious <code>0</code>/NIL. Two
   * responses of increasing cost:
   * <ul>
   * <li>skip leading zero coefficients inside the captured truncation - a pure scan of terms
   * already computed (SymPy gruntz's leadterm does the same). Always on, free.</li>
   * <li>when the WHOLE truncation cancels (isOrder), re-expand at a higher order (the "adaptive
   * order" of SymPy's calculate_series). This is expensive - total low-order cancellation is common
   * among the deep mrv sub-limits AND among the many depth-0 Limit calls that Series[] issues
   * internally - and, measured, it doubles SeriesTest/LimitTest for ZERO extra resolved cases: the
   * hard tower differences (#80/#89/#71/#70) that need it do not even reach here - they time out
   * earlier in compareGrowth's heuristic mrv comparison. So the order-raising is kept structural
   * but gated OFF (GRUNTZ_MAX_SERIES_ORDER == 2 =&gt; the loop runs once); raise the cap to
   * re-enable it once the compareGrowth bottleneck is addressed.</li>
   * </ul>
   * A <code>null</code> series means an unsupported shape, not an under-resolved one, so a higher
   * order will not help - stop.
   *
   * @return the leading term, or <code>null</code> when no usable series exists
   */
  private static SeriesLead seriesLeadingTerm(IExpr rewritten, ISymbol w, EvalEngine engine) {
    RunState run = RUN.get();
    boolean oldInSeries = run.inSeries;
    run.inSeries = true;
    try {
      for (int seriesOrder = 2; seriesOrder <= GRUNTZ_MAX_SERIES_ORDER; seriesOrder += 2) {
        ASTSeriesData candidate =
            ASTSeriesData.seriesDataRecursive(rewritten, w, F.C0, seriesOrder, -1, engine);
        if (candidate == null) {
          return null;
        }
        // Skip leading zero coefficients inside the captured truncation.
        int nMin = candidate.minExponent();
        while (nMin < candidate.truncateOrder() && candidate.coefficient(nMin).isZero()) {
          nMin++;
        }
        if (nMin < candidate.truncateOrder()) {
          IExpr leadCoeff = candidate.coefficient(nMin);
          return leadCoeff.isNIL() ? null : new SeriesLead(nMin, leadCoeff);
        }
        // The whole truncation is zero (isOrder): cancellation runs deeper than this order -
        // re-expand one step higher (only reached when GRUNTZ_MAX_SERIES_ORDER > 2).
        if (DEBUG) {
          System.out.println("GRUNTZ order " + seriesOrder + " all-zero, raising");
        }
      }
      return null;
    } finally {
      run.inSeries = oldInSeries;
    }
  }

  /**
   * Entry point for Gruntz limit evaluation. Transforms the limit to x -> Infinity and delegates to
   * the recursive driver.
   */
  public static IExpr evaluateLimit(IExpr expr, ISymbol x, IExpr x0, Limit.Direction direction,
      EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }

    // No nested Gruntz runs from inside our own series expansion - the ordinary heuristics
    // may still resolve the coefficient limit; see RunState.inSeries. (Even small
    // expressions must stay blocked: the recursive fan-out of the evalGruntz/taylorSeries
    // tree grinds regardless of the seed size.)
    if (RUN.get().inSeries) {
      return F.NIL;
    }

    // An unknown user function of the limit variable (e.g. a(n-2) from a recurrence fed
    // through Series by AsymptoticRSolveValue) has no defined growth class - SymPy's gruntz
    // raises "MRV set computation for UndefinedFunction is not allowed" for the same reason.
    // Without this gate the algorithm recurses through series/limit cycles on the opaque
    // function and can abort with "unexpected NIL expression encountered".
    final ISymbol variable = x;
    if (expr.has(e -> e.isAST() && !e.head().isBuiltInSymbol() && !e.isFree(variable, true),
        true)) {
      return F.NIL;
    }

    // Prevent infinite loops where Series expansions call Limit, which calls Gruntz again.
    // (The growth-comparison / sign caches are NOT cleared here: their entries are intrinsic
    // "as printed-variable -> +Infinity" facts, valid across the several Gruntz attempts one
    // user-level Limit makes. The Limit builtin clears them at its true top-level entry - see
    // clearSessionCaches.)
    int depth = RUN.get().gruntzDepth;
    if (depth > 3) {
      return F.NIL;
    }

    int oldRecursionLimit = engine.getRecursionLimit();
    try {
      RUN.get().gruntzDepth = depth + 1;

      // Give Gruntz enough recursion depth to perform deep symbolic algebra,
      // overriding any starvation caused by L'Hopital rule fallbacks.
      if (oldRecursionLimit < 1024) {
        engine.setRecursionLimit(1024);
      }

      // Properly handle TWO_SIDED limits by evaluating both directional paths.
      // If the right-sided and left-sided limits do not match, the limit is Indeterminate.
      if (direction == Limit.Direction.TWO_SIDED && !x0.isInfinity() && !x0.isNegativeInfinity()) {
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
      // Catch RecursionLimitExceeded and evaluation loops gracefully - but never eat an
      // interrupt/timeout, those must reach the caller
      Errors.rethrowsInterruptException(rle);
      return F.NIL;
    } finally {
      RUN.get().gruntzDepth = depth;
      engine.setRecursionLimit(oldRecursionLimit);
    }
  }

  /**
   * Collapse a constant (limit-variable-free) limit result to its closed form. The exponential
   * continuity route returns <code>E^(lim c*Log(b))</code> literally, and the engine has no
   * auto-evaluation for e.g. <code>E^(3*Log(3))</code>; {@link S#Simplify} turns that into
   * <code>27</code>. Cheap because the argument no longer contains the limit variable.
   *
   * @return the simplified form, or <code>constant</code> unchanged when the simplification did not
   *         produce a well-defined result
   */
  static IExpr collapseConstant(IExpr constant, EvalEngine engine) {
    if (constant.isNumber() || constant.isSymbol()) {
      return constant;
    }
    try {
      IExpr simplified = engine.evalQuiet(F.Simplify(constant));
      if (simplified.isPresent() && simplified.isFree(S.Simplify)
          && simplified.isIndeterminateFree()) {
        return simplified;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return constant;
  }

  /**
   * Stirling's approximation <code>Gamma(z) ~ Sqrt(2*Pi/z) * E^(z*Log(z) - z + 1/(12*z))</code> -
   * only valid for a DIVERGENT argument; callers must check {@link Limit#divergesAtInfinity} first.
   * One truncation, shared by the mrv extraction and {@link Limit#replaceStirling}.
   */
  static IExpr stirlingGamma(IExpr arg, EvalEngine engine) {
    return engine.evaluate(F.Times(F.Power(F.Divide(F.Times(F.C2, S.Pi), arg), F.C1D2), F.Exp(
        F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg), F.Divide(F.C1, F.Times(F.ZZ(12), arg))))));
  }

  /**
   * Stirling's approximation <code>LogGamma(z) ~ z*Log(z) - z + (1/2)*Log(2*Pi/z) + 1/(12*z)</code>
   * - only valid for a DIVERGENT argument. One truncation, shared by the mrv extraction,
   * {@link #replaceLogStirling} and {@link Limit#replaceStirling}.
   */
  static IExpr stirlingLogGamma(IExpr arg, EvalEngine engine) {
    return engine.evaluate(F.Plus(F.Times(arg, F.Log(arg)), F.Negate(arg),
        F.Times(F.C1D2, F.Log(F.Divide(F.Times(F.C2, S.Pi), arg))),
        F.Divide(F.C1, F.Times(F.ZZ(12), arg))));
  }

  /**
   * Digamma asymptotics
   * <code>PolyGamma(0, z) ~ Log(principal) - 1/(2*tail) [- 1/(12*tail^2)]</code> - only valid for a
   * DIVERGENT argument. The principal and tail argument forms may differ deliberately: see
   * {@link Limit#digammaPrincipalArg} and {@link Limit#digammaTailArg}. The mrv extraction and
   * {@link #replaceLogStirling} only need the growth class and use the first order; a limit probing
   * the <code>1/z^2</code> order needs {@code secondOrder} (without it
   * <code>n^2*(psi(n+1) - Log(n) - 1/(2n))</code> collapses to a false <code>0</code> instead of
   * <code>-1/12</code>).
   */
  static IExpr digammaAsymptotic(IExpr principal, IExpr tail, boolean secondOrder,
      EvalEngine engine) {
    IExpr order1 = F.Plus(F.Log(principal), F.Negate(F.Divide(F.C1, F.Times(F.C2, tail))));
    if (!secondOrder) {
      return engine.evaluate(order1);
    }
    return engine
        .evaluate(F.Plus(order1, F.Negate(F.Divide(F.C1, F.Times(F.ZZ(12), F.Sqr(tail))))));
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
      boolean changed = false;
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr arg = expandGruntzLogs(ast.get(i), w, engine);
        if (arg != ast.get(i)) {
          changed = true;
        }
        result.append(arg);
      }
      // only a rebuilt node needs the engine - re-evaluating every unchanged level of the
      // tree was one full evaluation per AST node
      return changed ? engine.evaluate(result) : expr;
    }
    return expr;
  }

  /**
   * Converts hyperbolic functions to exponentials. Gruntz algorithm extracts growth based strictly
   * on Exp and Log. If hyperbolics are left intact, they act as opaque constants during rewrite and
   * series expansion, causing limits to falsely evaluate to 0.
   */
  private static IExpr expandHyperbolics(IExpr expr, EvalEngine engine) {
    return expandHyperbolics(expr, engine, null);
  }

  /**
   * Converts hyperbolic functions to exponentials, like
   * {@link #expandHyperbolics(IExpr, EvalEngine)}, but when <code>variable</code> is non-null only
   * applications whose argument depends on it are rewritten. {@link Limit#evalLimit} uses this for
   * its hyperbolic-to-exponential retry: unlike a blanket <code>TrigToExp</code> it neither
   * complexifies circular trig in the same expression (Sin would become I-exponentials) nor blows
   * hyperbolic CONSTANTS like <code>Sinh(1)</code> into <code>(E-1/E)/2</code> forms the result
   * never re-simplifies (issue #42).
   */
  static IExpr expandHyperbolics(IExpr expr, EvalEngine engine, ISymbol variable) {
    if (!expr.has(y -> y.isFunctionID(ID.Sinh, ID.Cosh, ID.Tanh, ID.Coth, ID.Sech, ID.Csch),
        true)) {
      return expr;
    }
    IExpr rewritten = F.subst(expr, y -> {
      if (variable != null && y.isFunctionID(ID.Sinh, ID.Cosh, ID.Tanh, ID.Coth, ID.Sech, ID.Csch)
          && ((IAST) y).argSize() == 1 && ((IAST) y).arg1().isFree(variable, true)) {
        // a hyperbolic CONSTANT in variable-gated mode - leave it intact
        return F.NIL;
      }
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
      Errors.rethrowsInterruptException(e);
      return F.NIL;
    }
  }

  /**
   * An exponential <code>E^f</code> whose exponent has an APPARENT <code>w</code>-pole (e.g.
   * <code>E^(1/w - Log(1+w)/w^2)</code>) defeats seriesDataRecursive even when the singular parts
   * cancel. Where the exponent's own series turns out regular (minExponent &gt;= 0), substitute its
   * truncated normal form so the outer series composition succeeds.
   */
  private static IExpr preExpandExpExponents(IExpr rewritten, ISymbol w, EvalEngine engine) {
    final ISymbol wf = w;
    IExpr result = F.subst(rewritten, e -> {
      if (e.isExp()) {
        IExpr f = e.exponent();
        if (f.has(p -> p.isPower() && p.base().equals(wf) && p.exponent().isNegativeResult(),
            true)) {
          RunState run = RUN.get();
          boolean oldInSeries = run.inSeries;
          run.inSeries = true;
          try {
            ASTSeriesData fs = ASTSeriesData.seriesDataRecursive(f, wf, F.C0, 4, -1, engine);
            if (fs != null && fs.minExponent() >= 0) {
              return F.Exp(engine.evaluate(fs.normal(false)));
            }
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
          } finally {
            run.inSeries = oldInSeries;
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
   * <code>null</code> for any shape it cannot analyze soundly (the caller must then give up rather
   * than risk a wrong value).
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
      if (base == S.E) {
        // E^f: only a plain w-power analysis survives if f converges as w->0+
        IExpr[] f = leadingWPower(exponent, w, engine);
        if (f == null) {
          return null;
        }
        double fv = engine.evaluate(f[0]).evalfNaN();
        if (Double.isNaN(fv)) {
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
        double v = engine.evaluate(p[0]).evalfNaN();
        if (Double.isNaN(v)) {
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
      double fv = engine.evaluate(f[0]).evalfNaN();
      if (Double.isNaN(fv)) {
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

            if (base == S.E) {
              IExpr argMrv = mrv(exponent, x, engine);
              // Gruntz restriction: Exp(f) is only rapidly varying if f diverges - see
              // mrvExponentLimit for how that limit is computed.
              if (divergesToInfinity(mrvExponentLimit(exponent, x, engine))) {
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
              // The SAME Gruntz restriction as the E-case applies: b^f is rapidly varying
              // only when f diverges (b != 1 - the engine already collapsed 1^f; a constant
              // Log(b) factor cannot change divergence). Without the gate a bounded power
              // like 2^(1/x) becomes a bogus mrv candidate that every later growth
              // comparison has to demote at full cost.
              IExpr argMrv = mrv(exponent, x, engine);
              if (divergesToInfinity(mrvExponentLimit(exponent, x, engine))) {
                IExpr expSet = F.List(ast);
                return mrvMax(expSet, argMrv, x, engine);
              }
              return argMrv;
            }

            try {
              IExpr logExpr = engine.evaluate(F.Times(exponent, F.Log(base)));
              IExpr argMrv = mrv(logExpr, x, engine);
              if (DEBUG) {
                System.out.println("MRV power-case " + ast + " argMrv=" + argMrv);
              }
              // f(x)^g(x) == E^(g*Log(f)): rapidly varying only when that exponent
              // diverges - (1+1/x)^x has g*Log(f) -> 1 and must NOT enter the mrv set
              // (the E-case restriction, applied uniformly).
              if (divergesToInfinity(mrvExponentLimit(logExpr, x, engine))) {
                return mrvMax(F.List(ast), argMrv, x, engine);
              }
              return argMrv;
            } catch (RuntimeException e) {
              Errors.rethrowsInterruptException(e);
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
              Errors.rethrowsInterruptException(e);
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
            return mrv(stirlingGamma(arg, engine), x, engine);
          }
          case ID.LogGamma: {
            // Asymptotic expansion of LogGamma isolates polynomial/logarithmic variation;
            // like Stirling it requires a divergent argument.
            IExpr arg = ast.arg1();
            if (!divergesAtInfinity(arg, x)) {
              return mrv(arg, x, engine);
            }
            return mrv(stirlingLogGamma(arg, engine), x, engine);
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
              IExpr digamma = digammaAsymptotic(arg, digammaTailArg(arg, x, engine), false, engine);
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

  /**
   * The limit of an exponent expression as <code>x -&gt; Infinity</code>, shared by the mrv
   * {@code Power} cases. For Log/Exp-bearing exponents decide via the Gruntz limit itself (sympy's
   * mrv calls limitinf here, not a heuristic engine): tower exponents whose heuristic limit grinds
   * for the whole time budget (thesis 8.20's log-nest ratio) resolve in milliseconds this way.
   * Plain polynomial exponents - including the E^u artifacts the moveup substitution itself creates
   * - MUST use the cheap heuristic path, otherwise
   * <code>evalGruntz(x) -&gt; moveup E^u -&gt; evalGruntz(u) -&gt; ...</code> recurses forever
   * (observed as StackOverflow).
   */
  private static IExpr mrvExponentLimit(IExpr exponent, ISymbol x, EvalEngine engine) {
    IExpr argLimit = F.NIL;
    if (exponent.has(t -> t.isLog() || t.isExp(), true) && RUN.get().mrvExpDepth < 20) {
      RUN.get().mrvExpDepth++;
      try {
        argLimit = evalGruntz(exponent, x, engine);
      } finally {
        RUN.get().mrvExpDepth--;
      }
    }
    if (argLimit.isNIL()) {
      argLimit = evalLimitQuiet(exponent, infinityLimitData(x));
    }
    return argLimit;
  }

  /** True for +/-Infinity or any DirectedInfinity - the divergence the mrv E-case requires. */
  private static boolean divergesToInfinity(IExpr limit) {
    return limit.isInfinity() || limit.isNegativeInfinity() || limit.isDirectedInfinity();
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

  static IExpr replaceLogStirling(IExpr expr, ISymbol x, EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      // Match Log(Gamma(...)), Log(Factorial(...)), Log(Pochhammer(...))
      if (head == S.Log && ast.arg1().isAST()) {
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
            return stirlingLogGamma(arg, engine);
          }
        }
      } else if (head == S.LogGamma) {
        IExpr arg = replaceLogStirling(ast.arg1(), x, engine);
        if (!divergesAtInfinity(arg, x)) {
          return F.LogGamma(arg);
        }
        return stirlingLogGamma(arg, engine);
      } else if (head == S.PolyGamma && ast.argSize() == 2 && ast.arg1().isZero()) {
        // Digamma: PolyGamma(0, z) ~ Log(z) - 1/(2z) for a divergent argument free of
        // nested PolyGamma (the arg was already recursed, so psi(psi(x)) arrives here
        // with the inner level expanded; Log-bearing args are fine - see mrv PolyGamma)
        IExpr arg = replaceLogStirling(ast.arg2(), x, engine);
        if (!divergesAtInfinity(arg, x) || !arg.isFree(t -> t.isAST(S.PolyGamma), true)) {
          return F.PolyGamma(F.C0, arg);
        }
        return digammaAsymptotic(digammaPrincipalArg(arg, ast.arg2(), x, engine),
            digammaTailArg(arg, x, engine), false, engine);
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
   *
   * <p>
   * Returns {@link F#NIL} when the rewrite cannot be completed for <b>any</b> mrv element. Failing
   * hard is essential for soundness: a partially rewritten expression leaves a most rapidly varying
   * subterm in place, and the subsequent w-series expansion would treat it as a constant
   * coefficient - producing a wrong leading term instead of an honest "don't know" (e.g. a tower
   * difference where one exponential rewrites to <code>1/w</code> and the other stays would
   * series-expand to a spurious <code>+Infinity</code>).
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
          // nesting - an uncomputed c aborts the whole rewrite (graceful NIL upstream).
          RunState run = RUN.get();
          String ratioKey = x.toString() + '\0' + ratio.toString();
          IExpr cachedRatioLimit = run.rewriteRatioCache.get(ratioKey);
          if (cachedRatioLimit != null) {
            c = cachedRatioLimit;
          } else {
            int ratioDepth = run.rewriteRatioDepth;
            if (ratioDepth >= 4) {
              return F.NIL;
            }
            run.rewriteRatioDepth = ratioDepth + 1;
            try {
              c = evalLimitQuiet(ratio, infinityLimitData(x));
            } finally {
              run.rewriteRatioDepth = ratioDepth;
            }
            if (c.isPresent() && !c.isIndeterminate() && c.isFree(S.Limit, true)
                && c.isFree(S.Derivative, true) && c.isFree(x)) {
              run.rewriteRatioCache.put(ratioKey, c);
            }
          }
        }

        if (c.isIndeterminate() || c.isNIL() || !c.isFree(S.Limit, true)
            || !c.isFree(S.Derivative, true) || !c.isFree(x)) {
          return F.NIL;
        }

        IExpr remainder = engine.evaluate(F.Subtract(f, F.Times(c, g)));
        IExpr rewrittenRemainder = rewrite(remainder, mrvSet, g, x, w, engine);
        if (rewrittenRemainder.isNIL()) {
          return F.NIL;
        }

        IExpr wPart = engine.evaluate(F.Power(w, F.Negate(c)));
        IExpr expRemainder = engine.evaluate(F.Exp(rewrittenRemainder));

        return engine.evaluate(F.Times(wPart, expRemainder));
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        return F.NIL;
      }
    }

    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      if (head == S.Limit || head == S.Derivative || head == S.Integrate
          || head == S.Sum || head == S.Product || head == S.O) {
        return expr;
      }

      IASTAppendable rewrittenAST = F.ast(head, ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr rewrittenArg = rewrite(ast.get(i), mrvSet, g, x, w, engine);
        if (rewrittenArg.isNIL()) {
          return F.NIL;
        }
        rewrittenAST.append(rewrittenArg);
      }

      try {
        return engine.evaluate(rewrittenAST);
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        return F.NIL;
      }
    }

    return expr;
  }

  /**
   * Determines the asymptotic sign of an expression as x -> Infinity, memoized within the current
   * user-level Limit call (see {@link RunState#signInfCache}).
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

    Map<String, Integer> cache = RUN.get().signInfCache;
    // The variable is part of the key: identical printed forms under DIFFERENT limit
    // variables are different questions once the expression contains a second symbol
    // (sign of n*x^2 as x -> Infinity vs as n -> Infinity).
    String key = x.toString() + '\0' + expr.toString();
    boolean deepTier = RUN.get().gruntzDepth > 2;
    Integer cached = cache.get(key);
    if (cached != null) {
      int c = cached.intValue();
      if (c != UNKNOWN_SHALLOW && c != UNKNOWN_DEEP) {
        // a definitive cached sign is always reusable
        return c;
      }
      // A cached "unknown" is only reused within the tier that produced it (see the
      // signInfCache javadoc). This kills the dominant cost on tower differences
      // (observed: the same messy 0-limit sign query recomputed 150+ times at ~150ms
      // each) without blocking a better attempt by the other tier.
      if ((c == UNKNOWN_DEEP) == deepTier) {
        return Integer.MIN_VALUE;
      }
    }
    int result = signInfUncached(expr, x, engine);
    cache.put(key, Integer
        .valueOf(F.isPresent(result) ? result : (deepTier ? UNKNOWN_DEEP : UNKNOWN_SHALLOW)));
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
      if (RUN.get().gruntzDepth > 2) {
        // Test progressively smaller sample points. Exponentials like E^10000 or E^E^100
        // will throw RecursionLimitExceeded or Arithmetic overflows. We catch these
        // and degrade to smaller numbers until we safely resolve the asymptotic sign.
        return sampledSignAgreement(expr, x, engine, new int[] {10000, 100, 10, 3});
      }

      LimitData limitData = infinityLimitData(x);
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

          // Direct evaluation heuristic (two agreeing sample points - a single one can lie)
          int sampledSign = sampledSignAgreement(expr, x, engine, new int[] {10000, 100});
          if (sampledSign == 1 || sampledSign == -1) {
            return sampledSign;
          }

          // Leading Term fallback heuristic
          IExpr lt = ASTSeriesData.leadingTerm(expr, x, F.CInfinity, engine);
          if (lt.isPresent() && !lt.isNIL()) {
            if (lt.isExp())
              return 1;

            int ltSign = sampledSignAgreement(lt, x, engine, new int[] {10000, 100});
            if (ltSign == 1 || ltSign == -1) {
              return ltSign;
            }
          }
          // Directional derivative
          IExpr derivative = engine.evaluate(F.D(expr, x));
          IExpr derivLimit = evalLimitQuiet(derivative, limitData);

          if (engine.evaluate(F.Less(derivLimit, F.C0)).isTrue() || derivLimit.isNegativeInfinity())
            return 1;
          else if (engine.evaluate(F.Greater(derivLimit, F.C0)).isTrue() || derivLimit.isInfinity())
            return -1;
        }
      }
      return Integer.MIN_VALUE;
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      return Integer.MIN_VALUE;
    }
  }

  /**
   * Numeric sample sign with a two-point agreement requirement: a single sample point can lie about
   * an asymptotic sign (a subterm like <code>x - 10^5</code> flips beyond it), so a sign is only
   * reported when two sample points agree. When just ONE of the points evaluates at all (extreme
   * exponentials overflow the rest), its answer is kept - degrading those cases to "unknown" would
   * regress the E^-10000-style resolutions the point ladder exists for.
   *
   * @param samplePoints substitution values for <code>x</code>, largest first
   * @return <code>1</code>, <code>-1</code>, or {@link Integer#MIN_VALUE} when undecided (also when
   *         a sample evaluates cleanly to exactly <code>0</code> - smaller samples cannot help
   *         then, and a numeric zero never proves an asymptotic sign)
   */
  private static int sampledSignAgreement(IExpr expr, ISymbol x, EvalEngine engine,
      int[] samplePoints) {
    int firstSign = Integer.MIN_VALUE;
    for (int s : samplePoints) {
      try {
        IExpr sample = engine.evalQuiet(F.subst(expr, x, F.ZZ(s)));
        IExpr nSample = engine.evaluate(F.N(sample));
        if (nSample.isNumericFunction(true)) {
          int sampleSign;
          if (engine.evaluate(F.Greater(nSample, F.C0)).isTrue()) {
            sampleSign = 1;
          } else if (engine.evaluate(F.Less(nSample, F.C0)).isTrue()) {
            sampleSign = -1;
          } else {
            // evaluates cleanly but exactly to 0
            return Integer.MIN_VALUE;
          }
          if (!F.isPresent(firstSign)) {
            firstSign = sampleSign;
          } else {
            return firstSign == sampleSign ? firstSign : Integer.MIN_VALUE;
          }
        }
      } catch (RuntimeException rex) {
        // Mathematical overflow or recursion limit exceeded due to massive exponentials.
        // Safely ignore the crash and try the next smaller sample point.
        Errors.rethrowsInterruptException(rex);
      }
    }
    return firstSign;
  }
}
