package org.matheclipse.core.reflection.system;

import java.util.Optional;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
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

/**
 *
 *
 * <pre>
 * Limit(expr, x -&gt; x0)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the limit of <code>expr</code> as <code>x</code> approaches <code>x0</code>
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Limit(7+Sin(x)/x, x-&gt;Infinity)
 * 7
 * </pre>
 */
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
     * Bypasses Symja's evaluation engine to directly extract the mathematical logarithm of
     * exponential functions, preventing L'Hopital ratio limits from falsely evaluating to 0.
     */
    private static IExpr getLog(IExpr expr, EvalEngine engine) {
      if (expr.isPower() && expr.base().equals(S.E)) {
        return expr.exponent();
      }
      if (expr.isAST(S.Exp, 2)) {
        return expr.first();
      }
      return engine.evaluate(F.PowerExpand(F.Log(expr)));
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

      // Prevent infinite loops where Series expansions call Limit, which calls Gruntz again.
      int depth = GRUNTZ_DEPTH.get();
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
      if (expr.isFree(x)) {
        return expr;
      }

      // Find the MRV set of the expression
      IExpr mrvResult = mrv(expr, x, engine);
      if (!mrvResult.isPresent() || !mrvResult.isAST()) {
        return F.NIL;
      }
      IAST mrvSet = (IAST) mrvResult;

      // If the MRV set contains the limit variable x, it means there are no
      // rapidly varying exponentials. We must substitute x = Exp(u) to push the
      // expression up the mathematical growth scale so Puiseux series can process it.
      if (mrvSet.contains(x)) {
        ISymbol u = F.Dummy("u");
        IExpr substituted = engine.evaluate(F.PowerExpand(F.subst(expr, x, F.Exp(u))));

        if (substituted.equals(expr)) {
          return F.NIL; // Failsafe
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

      // Calculate the series expansion of the rewritten expression around w = 0
      ASTSeriesData series = ASTSeriesData.seriesDataRecursive(rewritten, w, F.C0, 2, -1, engine);

      if (series == null) {
        return F.NIL;
      }

      // Extract the leading coefficient and exponent
      int minExp = series.minExponent();
      IExpr leadCoeff = series.coefficient(minExp);

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
     * Mathematically compares the growth classes of two expressions f and g.
     */
    private static int compareGrowth(IExpr f, IExpr g, ISymbol x, EvalEngine engine) {
      if (f.equals(g))
        return 0; // Short-circuit identical expressions

      try {
        int signF = signInf(f, x, engine);
        if (signF == Integer.MAX_VALUE)
          return 0;
        IExpr posF = (signF == -1) ? engine.evaluate(F.Negate(f)) : f;

        int signG = signInf(g, x, engine);
        if (signG == Integer.MAX_VALUE)
          return 0;
        IExpr posG = (signG == -1) ? engine.evaluate(F.Negate(g)) : g;

        IExpr logF = getLog(posF, engine);
        IExpr logG = getLog(posG, engine);
        IExpr ratio = engine.evaluate(F.Divide(logF, logG));

        IExpr limitResult;
        if (ratio.isFree(x)) {
          limitResult = ratio;
        } else {
          LimitData limitData =
              new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
          limitResult = evalLimitQuiet(ratio, limitData);
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
        if (sign == Integer.MAX_VALUE) {
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
        IExpr head = ast.head();

        // Do not traverse into scoping constructs or limits to avoid recursive evaluation traps
        if (head.equals(S.Limit) || head.equals(S.Derivative) || head.equals(S.Integrate)
            || head.equals(S.Sum) || head.equals(S.Product) || head.equals(S.O)) {
          return F.NIL;
        }

        if (head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {

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
                IExpr expSet = F.List(ast);
                return mrvMax(expSet, argMrv, x, engine);
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
                return mrvMax(F.List(ast), argMrv, x, engine);
              } catch (RuntimeException e) {
                return F.NIL;
              }
            }

            case ID.Exp: {
              IExpr arg = ast.arg1();
              IExpr argMrv = mrv(arg, x, engine);
              IExpr expSet = F.List(ast);
              return mrvMax(expSet, argMrv, x, engine);
            }

            case ID.Log: {
              return mrv(ast.arg1(), x, engine);
            }

            case ID.Sinh:
            case ID.Cosh:
            case ID.Tanh:
              try {
                return mrv(engine.evaluate(F.Expand(ast)), x, engine);
              } catch (RuntimeException e) {
                return F.NIL;
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
            LimitData limitData =
                new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
            c = evalLimitQuiet(ratio, limitData);
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
     * Determines the asymptotic sign of an expression as x -> Infinity.
     */
    public static int signInf(IExpr expr, ISymbol x, EvalEngine engine) {
      if (expr.isFree(x)) {
        if (expr.isZero())
          return 0;
        if (engine.evaluate(F.Greater(expr, F.C0)).isTrue())
          return 1;
        if (engine.evaluate(F.Less(expr, F.C0)).isTrue())
          return -1;
        return Integer.MAX_VALUE;
      }

      try {
        LimitData limitData =
            new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
        IExpr limitResult = evalLimitQuiet(expr, limitData);

        if (limitResult.isInfinity())
          return 1;
        if (limitResult.isNegativeInfinity())
          return -1;

        if (limitResult.isRealResult() || limitResult.isNumber()) {
          if (engine.evaluate(F.Greater(limitResult, F.C0)).isTrue())
            return 1;
          if (engine.evaluate(F.Less(limitResult, F.C0)).isTrue())
            return -1;

          if (limitResult.isZero()) {

            // Direct evaluation heuristic
            IExpr sample = engine.evalQuiet(F.subst(expr, x, F.ZZ(10000)));
            if (sample.isRealResult() || sample.isNumber()) {
              if (engine.evaluate(F.Greater(sample, F.C0)).isTrue())
                return 1;
              if (engine.evaluate(F.Less(sample, F.C0)).isTrue())
                return -1;
            }

            // Leading Term fallback heuristic
            IExpr lt = ASTSeriesData.leadingTerm(expr, x, F.CInfinity, engine);
            if (lt.isPresent() && !lt.isNIL()) {
              sample = engine.evalQuiet(F.subst(lt, x, F.ZZ(10000)));
              if (sample.isRealResult() || sample.isNumber()) {
                if (engine.evaluate(F.Greater(sample, F.C0)).isTrue())
                  return 1;
                if (engine.evaluate(F.Less(sample, F.C0)).isTrue())
                  return -1;
              }
            }

            // Directional derivative
            IExpr derivative = engine.evaluate(F.D(expr, x));
            IExpr derivLimit = evalLimitQuiet(derivative, limitData);

            if (derivLimit.isNegativeResult() || derivLimit.isNegativeInfinity())
              return 1;
            else if (derivLimit.isPositiveResult() || derivLimit.isInfinity())
              return -1;
          }
        }
        return Integer.MAX_VALUE;
      } catch (RuntimeException e) {
        return Integer.MAX_VALUE;
      }
    }
  }

  /** Representing the data for the current limit. */
  private static class LimitData {
    private final ISymbol variable;

    private final IExpr limitValue;

    /** The rule <code>variable->limitValue</code>. */
    private final IAST rule;

    private Direction direction;

    public LimitData(ISymbol variable, IExpr limitValue, IAST rule, Direction direction) {
      this.variable = variable;
      this.limitValue = limitValue;
      this.rule = rule;
      this.direction = direction;
    }

    /**
     * Examples:
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * <pre>
     * Limit(x ^ 2 * Sin(1 / x) ^ 3, x -> 0)
     * </pre>
     * 
     * @param x
     * @return
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
     * 
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
      // return ast.mapThread(limit(null), 1);
      IASTMutable result = ast.copy();
      boolean isIndeterminate = false;
      boolean isLimit = false;
      for (int i = 1; i < ast.size(); i++) {
        IExpr temp = evalLimitQuiet(ast.get(i), this);
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
      if (evaledResult.isAST(S.Interval, 2)) {
        // TRAP: Convert unresolved oscillating Intervals to Indeterminate
        IExpr opt = IntervalSym.toSinglePoint(evaledResult);
        return opt.isPresent() ? opt : S.Indeterminate;
      }
      return evaledResult;
    }

    public IAST rule() {
      return rule;
    }

    /**
     * Try the squeeze theorem: <a href="https://en.wikipedia.org/wiki/Squeeze_theorem">Wikipedia -
     * Squeeze theorem</a>. It is assumed that {@link #limitValue} equals <code>0</code>.
     * <p>
     * Example:
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * @param ast
     * @param defaultResult
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

  /**
   * Evaluate the limit for the given limit data.
   *
   * @param expr
   * @param data the limits data definition
   * @param engine
   * @return {@link S#NIL} if no limit could be found
   */
  private static IExpr evalLimit(final IExpr expr, LimitData data, EvalEngine engine) {
    IExpr evaledExpr = expr;
    final IExpr limitValue = data.limitValue();

    // Android-changed: do not use shared EvalEngine
    if (engine == null) {
      engine = EvalEngine.get();
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
        // if (expression.isPower() && !expression.first().isE()) {
        // temp = F.Times(expression.second(), F.Log(expression.first()));
        // temp = evalLimit(temp, data, engine);
        // if (temp.isPresent()) {
        // return temp;
        // }
        // }
      }
      IExpr temp = limitInfinityZero((IAST) evaledExpr, data, (IAST) limitValue);
      if (temp.isPresent()) {
        return temp;
      }
    }

    IExpr temp = evalLimitAST(evaledExpr, limitValue, data, engine);
    if (temp.isPresent()) {
      return temp;
    }
    if (temp.isNIL()) {
      if (evaledExpr.isNumericFunction(data.variable)) {
        // System.out.println("Standard heuristics failed for expression: " + evaledExpr
        // + " with limit value: " + limitValue);
        // Fallback to Gruntz algorithm if standard heuristics fail
        IExpr gruntzResult = GruntzLimit.evaluateLimit(evaledExpr, data.variable(),
            data.limitValue(), data.direction(), engine);
        if (gruntzResult.isPresent()) {
          return gruntzResult;
        }
      }
    }
    // If all heuristics and advanced algorithms fail, check if direct substitution
    // mathematically yields Indeterminate or an oscillating Interval.
    IExpr finalSubst = engine.evalQuiet(evaledExpr.replaceAll(data.rule()).orElse(F.NIL));
    if (finalSubst.isIndeterminate()) {
      return S.Indeterminate;
    }
    if (finalSubst.isAST(S.Interval, 2)) {
      IExpr opt = IntervalSym.toSinglePoint(finalSubst);
      return opt.isPresent() ? opt : S.Indeterminate;
    }
    return F.NIL;
  }

  private static IExpr evalLimitAST(final IExpr expression, final IExpr limitValue, LimitData data,
      EvalEngine engine) {
    if (expression.isAST()) {
      if (!limitValue.isNumericFunction(true) && limitValue.isFree(S.DirectedInfinity)
          && limitValue.isFree(data.variable())) {
        // example Limit(E^(3*x), x->a) ==> E^(3*a)
        return expression.replaceAll(data.rule()).orElse(expression);
      }
      final IAST ast = (IAST) expression;
      if (ast.isPlus()) {
        return plusLimit(ast, data, engine);
      } else if (ast.isTimes()) {
        return timesLimit(ast, data, engine);
      } else if (ast.isPower() && !ast.base().isPositive() && !ast.exponent().isPositive()) {
        return powerLimit(ast, data, engine);
      } else if (ast.isAST(S.Piecewise, 3)) {
        return piecewiseLimit(ast, data, engine);
      } else if (ast.argSize() > 0 && ast.isNumericFunctionAST()) {
        IASTMutable copy = ast.copy();
        IExpr temp = F.NIL;
        boolean indeterminate = false;
        for (int i = 1; i < ast.size(); i++) {
          temp = data.limit(ast.get(i));
          if (temp.isPresent()) {
            if (temp.isIndeterminate()) {
              if (data.direction != Direction.TWO_SIDED) {
                return S.Indeterminate;
              }
              indeterminate = true;
            }
            copy.set(i, temp);
          } else {
            copy.set(i, S.Indeterminate);
            indeterminate = true;
          }
        }
        if (!indeterminate) {
          temp = engine.evalQuiet(copy);
          if (temp.isPresent() && !temp.isIndeterminate()) {
            return temp;
          }
        }
        if (data.direction == Direction.TWO_SIDED && indeterminate) {
          return evalLimitTwoSided(copy, ast, data, engine);
        }
        return S.Indeterminate;
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
      IExpr direction =
          data.direction() == Direction.TWO_SIDED ? S.Reals : F.ZZ(data.direction().toInt());
      IExpr result = F.Limit(expr, data.rule(), F.Rule(S.Direction, direction)).eval(engine);
      return IntervalSym.toSinglePoint(result).orElse(result);
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /**
   * <p>
   * Evaluate the limit of a function by evaluating the separate directions
   * <code>({@link Direction#FROM_BELOW}, {@link Direction#FROM_ABOVE}</code> for the arguments and
   * comparing the function evaluation result for equality.
   * 
   * @param astLimitEvaluated the limit evaluation which can contain {@link S#Indeterminate} as
   *        arguments
   * @param astOriginal the original ast with all non {@link S#Indeterminate} arguments
   * @param limitTwoSided containing the <code>{@link Direction#TWO_SIDED}</code> data for limit
   *        determining
   * @param engine
   * @return {@link S#Indeterminate} if no limit can be found
   */
  private static IExpr evalLimitTwoSided(IASTMutable astLimitEvaluated, final IAST astOriginal,
      LimitData limitTwoSided, EvalEngine engine) {
    IASTMutable copy1 = astOriginal.copy();
    IASTMutable copy2 = astOriginal.copy();
    for (int i = 1; i < astOriginal.size(); i++) {
      IExpr arg = astLimitEvaluated.get(i);
      if (arg.isIndeterminate()) {
        arg = astOriginal.get(i);
        LimitData limitBelow = new LimitData(limitTwoSided.variable, limitTwoSided.limitValue,
            limitTwoSided.rule, Direction.FROM_BELOW);
        IExpr belowValue = limitBelow.limit(arg);
        if (belowValue.isPresent() && !belowValue.isIndeterminate()) {
          LimitData limitAbove = new LimitData(limitTwoSided.variable, limitTwoSided.limitValue,
              limitTwoSided.rule, Direction.FROM_ABOVE);
          IExpr aboveValue = limitAbove.limit(arg);
          if (aboveValue.isPresent() && !aboveValue.isIndeterminate()) {
            copy1.set(i, belowValue);
            copy2.set(i, aboveValue);
            continue;
          }
        }
        return S.Indeterminate;
      } else {
        copy1.set(i, astLimitEvaluated.get(i));
        copy2.set(i, astLimitEvaluated.get(i));
      }
    }
    IExpr f1 = engine.evalQuiet(copy1);
    IExpr f2 = engine.evalQuiet(copy2);
    if (f1.equals(f2) && !f1.isIndeterminate() && f1.isPresent() && f1.isFree(S.Interval)) {
      return f1;

    }
    return S.Indeterminate;
  }


  private static IExpr evalReplaceAll(IExpr expression, LimitData data, EvalEngine engine) {
    IExpr result = expression.replaceAll(data.rule());
    if (result.isPresent()) {
      result = engine.evalQuiet(result);
      if (result.isNumericFunction(true) || result.isInfinity() || result.isNegativeInfinity()) {
        if (result.isAST(S.Interval, 2)) {
          // TRAP: Convert unresolved oscillating Intervals to Indeterminate
          IExpr opt = IntervalSym.toSinglePoint(result);
          return opt.isPresent() ? opt : S.Indeterminate;
        }
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * Test if <code>y</code> matches pattern <code>Sqrt(_)</code> or
   * <code>Times(f1,...,Sqrt(_),...,fn)</code>
   * 
   * @param y
   * @return
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
    try {
      if (recursionLimit <= 0
          || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT) {
        // FIX: Give L'Hopital a safe relative budget to prevent instant starvation inside Gruntz
        engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
      }
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
      if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
        expr = engine.evalQuiet(F.Simplify(expr));
      }
      if (expr.isFree(v -> v.equals(S.D) || v.equals(S.Derivative), true)) {
        engine.incRecursionCounter();
        return evalLimit(expr, data, engine);
      }
    } catch (RecursionLimitExceeded rle) {
      engine.setRecursionLimit(recursionLimit);
    } finally {
      engine.setRecursionLimit(recursionLimit);
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

  // private static IExpr mapLimit(final IAST ast, LimitData data) {
  // return ast.mapThread(data.limit(null), 1);
  // }

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
      try {
        if (recursionLimit <= 0
            || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT) {
          // FIX: Give L'Hopital a safe relative budget
          engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
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
      if (limitExpLog.isIndeterminate() || limitExpLog.isNIL()) {
        limitExpLog = GruntzLimit.evaluateLimit(expLogBase, data.variable(), data.limitValue(),
            data.direction(), engine);
      }

      if (limitExpLog.isPresent() && !limitExpLog.isAST(S.Limit)
          && !limitExpLog.isIndeterminate()) {
        // Use PowerExpand to cleanly format terms like E^(Log(a)/2) into Sqrt(a)
        return engine.evaluate(F.PowerExpand(F.Exp(limitExpLog)));
      }
    }

    if (exponent.equals(data.variable())) {
      if (!base.isZero()) {
        if (data.limitValue().isZero()) {
          return F.C1;
        }
        if (base.isFree(data.variable())) {
          boolean isInfinityLimit = data.limitValue().isInfinity();
          if (isInfinityLimit || data.limitValue().isNegativeInfinity()) {
            if (F.Log(base).isNumericFunction(true)) {
              if (F.Log(base).greater(F.C0).isTrue()) {
                return isInfinityLimit ? F.CInfinity : F.C0;
              }
            } else if (base.isNumericFunction(s -> s.isSymbol() ? "" : null)) {
              return F.ConditionalExpression(isInfinityLimit ? F.CInfinity : F.C0,
                  F.Greater(F.Log(base), F.C0));
            }
          }
        }
      }
      if (base.isRealResult()) {
        IExpr temp = evalReplaceAll(powerAST, data, engine);
        if (temp.isPresent()) {
          return temp;
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
        if (!isFreeResult.arg2().isOne()) {
          return F.Times(F.Power(isFreeResult.arg1(), exponent),
              data.limit(F.Power(isFreeResult.arg2(), exponent)));
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
              } else if (data.direction() == Direction.FROM_BELOW) {
                return F.CNInfinity;
              } else if (data.direction() == Direction.FROM_ABOVE) {
                return F.CInfinity;
              }
            } else if (exponent.isFraction()) {
              if (data.direction() == Direction.TWO_SIDED) {
                return S.Indeterminate;
              } else if (data.direction() == Direction.FROM_ABOVE) {
                return F.CInfinity;
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
          AlgebraUtil.fractionalPartsTimesPower((IAST) temp, false, false, true, true, true, true);
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
        AlgebraUtil.fractionalPartsTimesPower(timesAST, false, false, true, true, true, true);
    if (parts.isEmpty()) {
      IAST[] timesPolyFiltered = timesAST.filter(x -> x.isPolynomial(data.variable));
      if (timesPolyFiltered[0].size() > 1 && timesPolyFiltered[1].size() > 1) {
        IExpr first = engine.evaluate(data.limit(timesPolyFiltered[0].oneIdentity1()));
        if (first.isIndeterminate()) {
          return S.Indeterminate;
        }
        IExpr second = engine.evaluate(data.limit(timesPolyFiltered[1].oneIdentity1()));
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
          ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
          ExprPolynomial denominatorPoly = ring.create(denominator);
          ExprPolynomial numeratorPoly = ring.create(numerator);
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

  // private static IExpr logLimit(final IAST logAST, LimitData data, EvalEngine engine) {
  // if (logAST.isAST2() && !logAST.isFree(data.variable())) {
  // return F.NIL;
  // }
  // IExpr firstArg = logAST.arg1();
  // if (firstArg.isPower() && firstArg.exponent().isFree(data.variable())) {
  // IAST arg1 = logAST.setAtCopy(1, firstArg.base());
  // return F.Times(firstArg.exponent(), data.limit(arg1));
  // } else if (firstArg.isTimes()) {
  // IAST isFreeResult =
  // firstArg.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
  // if (!isFreeResult.arg1().isOne()) {
  // IAST arg1 = logAST.setAtCopy(1, isFreeResult.arg1());
  // IAST arg2 = logAST.setAtCopy(1, isFreeResult.arg2());
  // return F.Plus(arg1, data.limit(arg2));
  // }
  // }
  // return F.NIL;
  // }

  // private static IExpr logLimit(final IAST logAST, LimitData data, EvalEngine engine) {
  // if (logAST.isAST2() && !logAST.isFree(data.variable())) {
  // return F.NIL;
  // }
  // IExpr firstArg = logAST.arg1();
  // if (firstArg.isPower() && firstArg.exponent().isFree(data.variable())) {
  // IAST arg1 = logAST.setAtCopy(1, firstArg.base());
  // return F.Times(firstArg.exponent(), data.limit(arg1));
  // } else if (firstArg.isTimes()) {
  // IAST isFreeResult =
  // firstArg.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
  // if (!isFreeResult.arg1().isOne()) {
  // IAST arg1 = logAST.setAtCopy(1, isFreeResult.arg1());
  // IAST arg2 = logAST.setAtCopy(1, isFreeResult.arg2());
  // return F.Plus(arg1, data.limit(arg2));
  // }
  // }
  // return F.NIL;
  // }

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
      IExpr generateConditionOption = option[2];
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        IExpr assumptionExpr = OptionArgs.determineAssumptions(assumptionOption);
        if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
          IAssumptions assumptions =
              org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
          }
        }

        if (direction == Direction.TWO_SIDED) {
          IExpr temp = S.Limit.evalDownRule(engine, F.Limit(arg1, arg2));
          if (temp.isPresent()) {
            return temp;
          }
        }

        ISymbol symbol = (ISymbol) rule.arg1();
        IExpr limit = rule.arg2();
        LimitData data = new LimitData(symbol, limit, rule, direction);
        return evalLimit(arg1, data, engine);
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    } finally {
      engine.setNumericMode(numericMode);
    }
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
