package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

/**
 *
 *
 * <pre>
 * <code>Maximize(unary-function, variable)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the maximum of the unary function for the given <code>variable</code>.
 *
 * </p>
 *
 * <p>
 * See
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Derivative_test">Wikipedia - Derivative test</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; Maximize(-x^4-7*x^3+2*x^2 - 42,x)
 * {-42-7*(-21/8-Sqrt(505)/8)^3+2*(21/8+Sqrt(505)/8)^2-(21/8+Sqrt(505)/8)^4,{x-&gt;-21/8-Sqrt(505)/8}}
 * </code>
 * </pre>
 *
 * <p>
 * Print a message if no maximum can be found
 *
 * <pre>
 * <code>&gt;&gt; Maximize(x^4+7*x^3-2*x^2 + 42, x)
 * {Infinity,{x-&gt;-Infinity}}
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Minimize.md">Minimize</a>
 */
public class Maximize extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    IExpr x = ast.arg2();
    ISymbol head = ast.topHead();
    if (ast.isAST3()) {
      IExpr domain = ast.arg3();
      if (domain == S.Integers) {
        IExpr var = x.isList1() ? x.first() : x;
        if (var.isSymbol()) {
          return univariateIntegerExtremum(head, function, var, true, engine);
        }
        return F.NIL;
      }
      if (domain != S.Reals) {
        // only the Reals and Integers domains are supported
        return F.NIL;
      }
      // domain == Reals: continue with the real-valued logic below
    }
    if (x.isList()) {
      if (x.isList1()) {
        x = x.first();
      } else {
        IExpr result = multivariateExtremum(head, function, (IAST) x, true, engine);
        if (result.isPresent()) {
          return result;
        }
        // `1` currently not supported in `2`.
        return Errors.printMessage(S.Maximize, "unsupported",
            F.List("Multiple variables", "Maximize"), engine);
      }
    }
    if (x.isSymbol() || (x.isAST() && !x.isList())) {
      if (function.isList2()) {
        // single variable with a constraint: optimize over the feasible real interval
        IExpr result = univariateConstrainedExtremum(head, function.first(), function.second(), x,
            true, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      return maximize(head, function, x, engine);
    }

    return F.NIL;
  }

  /**
   * Dispatch the supported multivariate symbolic cases:
   *
   * <ul>
   * <li>a linear objective over an axis-aligned ellipsoid / n-sphere constraint
   * <code>{a.v + c0, Sum(k_i*v_i^2) &lt;= R^2}</code></li>
   * <li>an unconstrained multivariate quadratic <code>v^T.Q.v + b.v + c0</code> with definite
   * Hessian</li>
   * </ul>
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param function the objective (or <code>{objective, constraint}</code>)
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if no case matches
   */
  public static IExpr multivariateExtremum(ISymbol head, IExpr function, IAST varList,
      boolean isMax, EvalEngine engine) {
    for (int i = 1; i < varList.size(); i++) {
      if (!varList.get(i).isSymbol()) {
        return F.NIL;
      }
    }
    if (function.isList2()) {
      // linear objective with a single ellipsoid/sphere constraint
      IExpr result = linearEllipsoidExtremum(head, function.first(), function.second(), varList,
          isMax, engine);
      if (result.isPresent()) {
        return result;
      }
      // exact (rational) linear programming: linear objective with linear constraints
      result = linearProgrammingExtremum(head, function.first(), function.second(), varList, isMax,
          engine);
      if (result.isPresent()) {
        return result;
      }
      // general single-constraint Lagrange / KKT case
      return lagrangeExtremum(head, function.first(), function.second(), varList, isMax, engine);
    }
    if (!function.isList()) {
      // unconstrained multivariate quadratic
      return quadraticExtremum(head, function, varList, isMax, engine);
    }
    return F.NIL;
  }

  /**
   * Compute the global extremum of a univariate objective <code>f(x)</code> over the feasible real
   * region described by a single constraint (or conjunction / disjunction of constraints). The
   * feasible region is derived as {@link org.matheclipse.core.expression.F#IntervalData} via
   * {@link IntervalDataSym#toIntervalData(IExpr, IExpr, EvalEngine, boolean)}. The candidate set
   * consists of the interior stationary points (the real roots of <code>f'(x) == 0</code> that lie
   * strictly inside the region) and the closed finite interval endpoints; unbounded ends are
   * handled by the limit of <code>f</code> towards <code>+/-Infinity</code> (an objective-improving
   * unbounded end yields <code>Infinity</code> / <code>-Infinity</code>). Following the WMA
   * conventions, an open endpoint isn't attained and the global optimum is taken over all attained
   * candidates.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the univariate objective function <code>f(x)</code>
   * @param constraint the constraint defining the feasible region
   * @param x the (single) variable
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {x -> p}}</code> or {@link F#NIL} if the region couldn't be
   *         determined as an interval
   */
  public static IExpr univariateConstrainedExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IExpr x, boolean isMax, EvalEngine engine) {
    try {
      if (!x.isSymbol()) {
        return F.NIL;
      }
      IAST varList = F.list(x);
      // First detect an objective-improving unbounded end of the feasible region: derive the
      // region as interval data and, for every infinite end, test the limit of the objective.
      IExpr region = IntervalDataSym.toIntervalData(constraint, x, engine, true);
      if (region != null && region.isIntervalData()) {
        IAST intervalData = (IAST) region;
        if (intervalData.argSize() == 0) {
          // empty feasible region -> the extremum is not attained at any point
          return Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
        }
        IExpr unbounded = unboundedUnivariateExtremum(objective, x, intervalData, isMax, engine);
        if (unbounded.isPresent()) {
          return unbounded;
        }
      }
      // Delegate the finite (compact-region) case to the proven multivariate KKT / Lagrange
      // machinery with the single variable as a one-element variable list.
      return multivariateExtremum(head, F.List(objective, constraint), varList, isMax, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Detect an objective-improving unbounded end of the univariate feasible region. For every
   * interval entry <code>{min, minType, maxType, max}</code> with an infinite end, the limit of
   * <code>f(x)</code> towards that end is computed; if it tends to <code>+Infinity</code> (for a
   * maximum) or <code>-Infinity</code> (for a minimum) the problem is unbounded and
   * <code>{+/-Infinity, {x -> +/-Infinity}}</code> is returned.
   *
   * @param objective the univariate objective function
   * @param x the (single) variable
   * @param intervalData the feasible region as
   *        {@link org.matheclipse.core.expression.F#IntervalData}
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the unbounded result or {@link F#NIL} if no end is objective-improving
   */
  private static IExpr unboundedUnivariateExtremum(IExpr objective, IExpr x, IAST intervalData,
      boolean isMax, EvalEngine engine) {
    for (int i = 1; i < intervalData.size(); i++) {
      IExpr entry = intervalData.get(i);
      if (!entry.isList() || ((IAST) entry).argSize() != 4) {
        continue;
      }
      IAST sub = (IAST) entry;
      IExpr min = sub.arg1();
      IExpr max = sub.arg4();
      if (max.isInfinity()) {
        IExpr limit = S.Limit.funEval(objective, F.Rule(x, F.CInfinity));
        if (isMax ? limit.isInfinity() : limit.isNegativeInfinity()) {
          return F.list(isMax ? F.CInfinity : F.CNInfinity, F.list(F.Rule(x, F.CInfinity)));
        }
      }
      if (min.isNegativeInfinity()) {
        IExpr limit = S.Limit.funEval(objective, F.Rule(x, F.CNInfinity));
        if (isMax ? limit.isInfinity() : limit.isNegativeInfinity()) {
          return F.list(isMax ? F.CInfinity : F.CNInfinity, F.list(F.Rule(x, F.CNInfinity)));
        }
      }
    }
    return F.NIL;
  }

  /** Maximum number of integer points enumerated for an integer-domain optimization. */
  private static final int INTEGER_ENUMERATION_LIMIT = 100_000;

  /**
   * Optimize a univariate objective over the integer points of the feasible region (the
   * {@code Integers} domain of {@code Maximize}/{@code Minimize}). The feasible real region is
   * derived as interval data; an objective-improving unbounded end yields {@code +/-Infinity}, and
   * otherwise the integer candidates (enumerated within every bounded interval entry and taken from
   * the integer neighbors of the real stationary points) are evaluated and the global optimum is
   * returned.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param function the objective {@code f} or {@code {f, constraint}}
   * @param x the (single) integer variable
   * @param isMax {@code true} for a maximum, {@code false} for a minimum
   * @param engine the evaluation engine
   * @return the result {@code {value, {x -> p}}} or {@link F#NIL}
   */
  public static IExpr univariateIntegerExtremum(ISymbol head, IExpr function, IExpr x,
      boolean isMax, EvalEngine engine) {
    try {
      if (!x.isSymbol()) {
        return F.NIL;
      }
      IExpr objective;
      IExpr constraint;
      if (function.isList2()) {
        objective = function.first();
        constraint = function.second();
      } else if (!function.isList()) {
        objective = function;
        constraint = S.True;
      } else {
        return F.NIL;
      }

      IExpr region = constraint == S.True ? F.CRealsIntervalData
          : IntervalDataSym.toIntervalData(constraint, x, engine, true);
      if (region == null || !region.isIntervalData()) {
        return F.NIL;
      }
      IAST intervalData = (IAST) region;
      if (intervalData.argSize() == 0) {
        return Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
      }

      // an objective-improving unbounded end gives +/-Infinity (integer limit == real limit here)
      IExpr unbounded = unboundedUnivariateExtremum(objective, x, intervalData, isMax, engine);
      if (unbounded.isPresent()) {
        return unbounded;
      }

      List<IInteger> candidates = new ArrayList<>();
      // integer neighbors of the real stationary points (interior / unbounded-but-not-improving
      // ends)
      IExpr derivative = S.D.of(engine, objective, x);
      IExpr solution = S.Solve.of(engine, F.Equal(derivative, F.C0), x, S.Reals);
      if (solution.isListOfLists()) {
        IAST solutions = (IAST) solution;
        for (int s = 1; s < solutions.size(); s++) {
          IExpr rules = solutions.get(s);
          if (rules.isList1() && rules.first().isRule()) {
            IExpr r = rules.first().second();
            if (r.isReal()) {
              addNeighborIntegers(candidates, r, engine);
            }
          }
        }
      }
      // enumerate the integer points of every interval entry
      for (int i = 1; i < intervalData.size(); i++) {
        IExpr entry = intervalData.get(i);
        if (!entry.isList() || ((IAST) entry).argSize() != 4) {
          continue;
        }
        IAST sub = (IAST) entry;
        IExpr min = sub.arg1();
        boolean minClosed = sub.arg2() == S.LessEqual;
        boolean maxClosed = sub.arg3() == S.LessEqual;
        IExpr max = sub.arg4();
        boolean loBounded = !min.isNegativeInfinity() && !min.isInfinity();
        boolean hiBounded = !max.isInfinity() && !max.isNegativeInfinity();
        if (loBounded && hiBounded) {
          IInteger lo = integerLowerBound(min, minClosed, engine);
          IInteger hi = integerUpperBound(max, maxClosed, engine);
          if (lo == null || hi == null) {
            return F.NIL;
          }
          long loL;
          long hiL;
          try {
            loL = lo.toLong();
            hiL = hi.toLong();
          } catch (ArithmeticException ae) {
            return F.NIL;
          }
          if (hiL - loL > INTEGER_ENUMERATION_LIMIT) {
            return F.NIL;
          }
          for (long v = loL; v <= hiL; v++) {
            candidates.add(F.ZZ(v));
          }
        } else if (loBounded) {
          IInteger lo = integerLowerBound(min, minClosed, engine);
          if (lo != null) {
            candidates.add(lo);
          }
        } else if (hiBounded) {
          IInteger hi = integerUpperBound(max, maxClosed, engine);
          if (hi != null) {
            candidates.add(hi);
          }
        }
      }

      IExpr bestValue = F.NIL;
      IExpr bestPoint = F.NIL;
      for (IInteger candidate : candidates) {
        if (!isFeasibleInteger(constraint, x, candidate, engine)) {
          continue;
        }
        IExpr value = engine.evaluate(F.xreplace(objective, x, candidate));
        if (!value.isFree(x)) {
          continue;
        }
        if (!bestValue.isPresent()) {
          bestValue = value;
          bestPoint = candidate;
        } else {
          boolean better =
              isMax ? value.greater(bestValue).isTrue() : value.less(bestValue).isTrue();
          if (better) {
            bestValue = value;
            bestPoint = candidate;
          }
        }
      }
      if (bestValue.isPresent()) {
        return engine.evaluate(F.list(bestValue, F.list(F.Rule(x, bestPoint))));
      }
      return Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /** Add the integer floor and ceiling of the real value {@code r} to {@code candidates}. */
  private static void addNeighborIntegers(List<IInteger> candidates, IExpr r, EvalEngine engine) {
    IExpr floor = S.Floor.of(engine, r);
    if (floor.isInteger()) {
      candidates.add((IInteger) floor);
    }
    IExpr ceiling = S.Ceiling.of(engine, r);
    if (ceiling.isInteger()) {
      candidates.add((IInteger) ceiling);
    }
  }

  /**
   * Smallest integer satisfying the lower bound {@code min} ({@code closed} controls whether an
   * integer-valued bound is included). Returns {@code null} if the bound isn't a determined number.
   */
  private static IInteger integerLowerBound(IExpr min, boolean closed, EvalEngine engine) {
    IExpr ceiling = S.Ceiling.of(engine, min);
    if (!ceiling.isInteger()) {
      return null;
    }
    IInteger lo = (IInteger) ceiling;
    if (!closed && lo.equals(min)) {
      lo = lo.add(F.C1);
    }
    return lo;
  }

  /** Largest integer satisfying the upper bound {@code max} (see {@link #integerLowerBound}). */
  private static IInteger integerUpperBound(IExpr max, boolean closed, EvalEngine engine) {
    IExpr floor = S.Floor.of(engine, max);
    if (!floor.isInteger()) {
      return null;
    }
    IInteger hi = (IInteger) floor;
    if (!closed && hi.equals(max)) {
      hi = hi.subtract(F.C1);
    }
    return hi;
  }

  /** Test whether the integer {@code candidate} satisfies the constraint. */
  private static boolean isFeasibleInteger(IExpr constraint, IExpr x, IInteger candidate,
      EvalEngine engine) {
    if (constraint == S.True) {
      return true;
    }
    IExpr value = engine.evaluate(F.xreplace(constraint, x, candidate));
    return value.isTrue();
  }

  /**
   * Compute the symbolic closed form of a linear objective <code>a.v + c0</code> optimized over an
   * axis-aligned ellipsoid constraint <code>Sum(k_i*v_i^2) &lt;= B</code> (with all
   * <code>k_i &gt; 0</code>).
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the linear objective function
   * @param constraint the ellipsoid constraint <code>Sum(k_i*v_i^2) &lt;= B</code>
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the pattern doesn't
   *         match
   */
  public static IExpr linearEllipsoidExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      // constraint must be `lhs <= rhs`
      if (!constraint.isAST(S.LessEqual, 3)) {
        return F.NIL;
      }
      IExpr lhs = constraint.first();
      IExpr rhs = constraint.second();
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }

      // extract linear objective coefficients: objective == Sum(a_i*v_i) + c0
      IExpr[] a = new IExpr[n];
      IExpr linApprox = F.NIL;
      boolean allZero = true;
      for (int i = 0; i < n; i++) {
        a[i] = S.Coefficient.of(engine, objective, vars[i]);
        if (!isFreeOfAll(a[i], vars)) {
          return F.NIL;
        }
        linApprox = linApprox.isPresent() ? F.Plus(linApprox, F.Times(a[i], vars[i]))
            : F.Times(a[i], vars[i]);
        if (!a[i].isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          allZero = false;
        }
      }
      IExpr c0 = replaceAllByZero(objective, vars, engine);
      if (!isFreeOfAll(c0, vars)) {
        return F.NIL;
      }
      IExpr linearRemainder = engine.evaluate(F.Subtract(objective, F.Plus(linApprox, c0)));
      if (!linearRemainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return F.NIL;
      }
      if (allZero) {
        // The `1` is not attained at any point satisfying the constraints.
        return Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"));
      }

      // extract ellipsoid constraint: lhs == Sum(k_i*v_i^2) + constLhs, no cross/linear terms
      IExpr[] k = new IExpr[n];
      IExpr quadApprox = F.NIL;
      IExpr s2 = F.C0; // Sum(a_i^2 / k_i)
      for (int i = 0; i < n; i++) {
        k[i] = S.Coefficient.of(engine, lhs, vars[i], F.C2);
        if (!isFreeOfAll(k[i], vars) || !k[i].isPositiveResult()) {
          return F.NIL;
        }
        IExpr lin = S.Coefficient.of(engine, lhs, vars[i], F.C1);
        if (!lin.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          return F.NIL;
        }
        IExpr term = F.Times(k[i], F.Sqr(vars[i]));
        quadApprox = quadApprox.isPresent() ? F.Plus(quadApprox, term) : term;
        s2 = F.Plus(s2, F.Divide(F.Sqr(a[i]), k[i]));
      }
      IExpr constLhs = replaceAllByZero(lhs, vars, engine);
      // verify there are no cross terms and nothing else
      IExpr quadRemainder = engine.evaluate(F.Subtract(lhs, F.Plus(quadApprox, constLhs)));
      if (!quadRemainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return F.NIL;
      }

      // Sum(k_i*v_i^2) <= B with B = rhs - constLhs
      IExpr b = engine.evaluate(F.Subtract(rhs, constLhs));
      IExpr sumA2K = engine.evaluate(s2);
      IExpr factor = isMax ? F.C1 : F.CN1;
      IExpr normFactor = F.Sqrt(F.Divide(b, sumA2K)); // Sqrt(B / Sum(a_i^2/k_i))

      IExpr value = F.Plus(c0, F.Times(factor, F.Sqrt(F.Times(b, sumA2K))));
      IASTAppendable rules = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        rules.append(F.Rule(vars[i], F.Times(factor, F.Divide(a[i], k[i]), normFactor)));
      }
      return engine.evaluate(F.list(value, rules));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Compute the symbolic closed form of an unconstrained multivariate quadratic
   * <code>f = v^T.Q.v + b.v + c0</code>. If the Hessian is positive definite (for a minimum) or
   * negative definite (for a maximum) there is a unique extremum at the solution of
   * <code>Grad(f) == 0</code>.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param function the quadratic objective function
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the function isn't a
   *         quadratic with definite Hessian
   */
  public static IExpr quadraticExtremum(ISymbol head, IExpr function, IAST varList, boolean isMax,
      EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      IExpr[] grad = new IExpr[n];
      IASTAppendable equations = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
        grad[i] = S.D.of(engine, function, vars[i]);
        equations.append(F.Equal(grad[i], F.C0));
      }
      // Hessian must be constant (function quadratic in the variables)
      IExpr[][] hessian = new IExpr[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          IExpr hij = S.D.of(engine, grad[i], vars[j]);
          if (!isFreeOfAll(hij, vars)) {
            return F.NIL;
          }
          hessian[i][j] = hij;
        }
      }
      // definiteness via Sylvester's criterion: for a maximum test -Hessian
      IExpr[][] testMatrix = hessian;
      if (isMax) {
        testMatrix = new IExpr[n][n];
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            testMatrix[i][j] = engine.evaluate(F.Negate(hessian[i][j]));
          }
        }
      }
      if (!isPositiveDefinite(testMatrix, n, engine)) {
        return F.NIL;
      }
      IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
      if (!solution.isListOfLists() || solution.size() < 2) {
        return F.NIL;
      }
      IAST rules = (IAST) solution.first();
      IExpr value = function;
      for (int i = 1; i < rules.size(); i++) {
        IAST rule = (IAST) rules.get(i);
        value = F.xreplace(value, rule.first(), rule.second());
      }
      value = engine.evaluate(value);
      return engine.evaluate(F.list(value, rules));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Compute the symbolic extrema of an objective subject to one or more smooth constraints using
   * the Lagrange / KKT conditions.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the objective function
   * @param constraint a single equality / inequality constraint or an {@code And}/{@code List} of
   *        them
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if no candidate is found
   */
  public static IExpr lagrangeExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }

      // parse the constraint(s) into equality boundary expressions (eq == 0) and inequality
      // boundary expressions (with the feasible-interior sign)
      List<IExpr> conjuncts = new ArrayList<>();
      flattenConstraints(constraint, conjuncts);
      if (conjuncts.isEmpty()) {
        return F.NIL;
      }
      List<IExpr> equalities = new ArrayList<>();
      List<IExpr> inequalities = new ArrayList<>();
      List<Integer> inequalitySigns = new ArrayList<>(); // -1: feasible g<=0 ; +1: feasible g>=0
      for (IExpr c : conjuncts) {
        if (!parseComparator(c, engine, equalities, inequalities, inequalitySigns)) {
          return F.NIL;
        }
      }

      int m = inequalities.size();
      if (m > 16) {
        // too many inequality constraints for active-set enumeration
        return F.NIL;
      }

      // precompute the gradient of the objective
      IExpr[] df = new IExpr[n];
      for (int i = 0; i < n; i++) {
        df[i] = S.D.of(engine, objective, vars[i]);
      }

      List<IExpr> candidateValues = new ArrayList<>();
      List<IAST> candidateRules = new ArrayList<>();

      // enumerate every active set of inequality constraints (equality constraints are always
      // active). For each active set enforce the KKT stationarity condition via vanishing minors
      // and solve together with the active boundary equations.
      for (int mask = 0; mask < (1 << m); mask++) {
        List<IExpr> activeG = new ArrayList<>(equalities);
        for (int i = 0; i < m; i++) {
          if ((mask & (1 << i)) != 0) {
            activeG.add(inequalities.get(i));
          }
        }
        int k = activeG.size();

        IASTAppendable equations = F.ListAlloc();
        // stationarity: all (k+1)x(k+1) minors of [Grad(f); Grad(g_i...)] vanish
        if (k + 1 <= n) {
          IExpr[][] matrixRows = new IExpr[k + 1][n];
          matrixRows[0] = df;
          for (int r = 0; r < k; r++) {
            IExpr[] dg = new IExpr[n];
            for (int i = 0; i < n; i++) {
              dg[i] = S.D.of(engine, activeG.get(r), vars[i]);
            }
            matrixRows[r + 1] = dg;
          }
          for (int[] cols : combinations(n, k + 1)) {
            IASTAppendable matrix = F.ListAlloc(k + 1);
            for (int r = 0; r <= k; r++) {
              IASTAppendable row = F.ListAlloc(k + 1);
              for (int col : cols) {
                row.append(matrixRows[r][col]);
              }
              matrix.append(row);
            }
            IExpr det = S.Det.of(engine, matrix);
            equations.append(F.Equal(det, F.C0));
          }
        }
        // active constraint boundary equations
        for (IExpr g : activeG) {
          equations.append(F.Equal(g, F.C0));
        }
        if (equations.isEmpty()) {
          continue;
        }
        IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
        collectCandidatesMulti(solution, vars, objective, equalities, inequalities, inequalitySigns,
            candidateValues, candidateRules, engine);
      }

      if (candidateValues.isEmpty()) {
        return F.NIL;
      }

      // select the global minimum / maximum (deterministic lexicographic tie-break)
      int best = -1;
      IExpr bestValue = F.NIL;
      for (int i = 0; i < candidateValues.size(); i++) {
        IExpr value = candidateValues.get(i);
        if (best < 0) {
          best = i;
          bestValue = value;
          continue;
        }
        boolean better = isMax ? value.greater(bestValue).isTrue() : value.less(bestValue).isTrue();
        if (better) {
          best = i;
          bestValue = value;
        } else if (value.equals(bestValue)
            && isLexSmaller(candidateRules.get(i), candidateRules.get(best))) {
          best = i;
          bestValue = value;
        }
      }
      return engine.evaluate(F.list(bestValue, candidateRules.get(best)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Parse a single comparator constraint into equality / inequality boundary expressions, expanding
   * chained relations into their consecutive binary pairs.
   *
   * @param c the comparator constraint
   * @param engine the evaluation engine
   * @param equalities collector for equality boundary expressions (<code>g == 0</code>)
   * @param inequalities collector for inequality boundary expressions
   * @param inequalitySigns collector for the feasible-interior sign of each inequality
   * @return <code>true</code> if the constraint was a supported comparator, <code>false</code>
   *         otherwise
   */
  private static boolean parseComparator(IExpr c, EvalEngine engine, List<IExpr> equalities,
      List<IExpr> inequalities, List<Integer> inequalitySigns) {
    boolean equality = false;
    int sign;
    if (c.isAST(S.Equal)) {
      equality = true;
      sign = 0;
    } else if (c.isAST(S.LessEqual) || c.isAST(S.Less)) {
      sign = -1;
    } else if (c.isAST(S.GreaterEqual) || c.isAST(S.Greater)) {
      sign = 1;
    } else {
      return false;
    }
    // need at least two arguments (a binary relation); chained relations have more
    if (c.size() < 3) {
      return false;
    }
    IAST relation = (IAST) c;
    // a_1 OP a_2 OP ... OP a_p == AND of consecutive binary relations OP(a_i, a_{i+1})
    for (int i = 1; i < relation.size() - 1; i++) {
      IExpr g = engine.evaluate(F.Subtract(relation.get(i), relation.get(i + 1)));
      if (equality) {
        equalities.add(g);
      } else {
        inequalities.add(g);
        inequalitySigns.add(sign);
      }
    }
    return true;
  }

  /**
   * Recursively flatten a constraint expression into its individual conjuncts. {@link S#And} and
   * {@link S#List} nodes are descended into; everything else is added as a single conjunct.
   *
   * @param constraint the constraint expression
   * @param conjuncts the collector for the individual constraints
   */
  private static void flattenConstraints(IExpr constraint, List<IExpr> conjuncts) {
    if (constraint.isAST(S.And) || constraint.isList()) {
      IAST ast = (IAST) constraint;
      for (int i = 1; i < ast.size(); i++) {
        flattenConstraints(ast.get(i), conjuncts);
      }
    } else {
      conjuncts.add(constraint);
    }
  }

  /**
   * Collect fully determined, feasible candidate points from a {@code Solve} result, evaluating the
   * objective at each point.
   */
  private static void collectCandidatesMulti(IExpr solution, IExpr[] vars, IExpr objective,
      List<IExpr> equalities, List<IExpr> inequalities, List<Integer> inequalitySigns,
      List<IExpr> values, List<IAST> rulesList, EvalEngine engine) {
    if (!solution.isListOfLists()) {
      return;
    }
    IAST solutions = (IAST) solution;
    for (int s = 1; s < solutions.size(); s++) {
      IAST solutionRules = (IAST) solutions.get(s);
      IAST ordered = orderedVarRules(solutionRules, vars);
      if (!ordered.isPresent()) {
        continue;
      }
      // every variable must be determined to a value free of the variables
      boolean determined = true;
      for (int i = 1; i < ordered.size(); i++) {
        IExpr value = ((IAST) ordered.get(i)).second();
        if (!isFreeOfAll(value, vars)) {
          determined = false;
          break;
        }
      }
      if (!determined) {
        continue;
      }
      if (!isFeasibleAllConstraints(ordered, equalities, inequalities, inequalitySigns, engine)) {
        continue;
      }
      IExpr functionValue = engine.evaluate(applyRules(objective, ordered));
      if (!isFreeOfAll(functionValue, vars)) {
        continue;
      }
      values.add(functionValue);
      rulesList.add(ordered);
    }
  }

  /**
   * Test whether the point given by the ordered rule list satisfies every equality and inequality
   * constraint. A candidate is rejected only if a constraint is provably violated.
   */
  private static boolean isFeasibleAllConstraints(IAST ordered, List<IExpr> equalities,
      List<IExpr> inequalities, List<Integer> inequalitySigns, EvalEngine engine) {
    for (IExpr g : equalities) {
      IExpr v = engine.evaluate(applyRules(g, ordered));
      if (!v.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return false;
      }
    }
    for (int i = 0; i < inequalities.size(); i++) {
      IExpr v = engine.evaluate(applyRules(inequalities.get(i), ordered));
      if (inequalitySigns.get(i) < 0) {
        // feasible where v <= 0 -> reject if provably positive
        if (v.isPositiveResult()) {
          return false;
        }
      } else {
        // feasible where v >= 0 -> reject if provably negative
        if (v.isNegativeResult()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Build a rule list <code>{v_1 -> .., v_n -> ..}</code> in the order of <code>vars</code> from a
   * {@code Solve} solution. Returns {@link F#NIL} if some variable isn't determined.
   */
  private static IAST orderedVarRules(IAST solutionRules, IExpr[] vars) {
    IASTAppendable rules = F.ListAlloc(vars.length);
    for (IExpr var : vars) {
      IExpr value = F.NIL;
      for (int i = 1; i < solutionRules.size(); i++) {
        IAST rule = (IAST) solutionRules.get(i);
        if (rule.first().equals(var)) {
          value = rule.second();
          break;
        }
      }
      if (!value.isPresent()) {
        return F.NIL;
      }
      rules.append(F.Rule(var, value));
    }
    return rules;
  }

  /** Apply a list of replacement rules to an expression (without evaluating). */
  private static IExpr applyRules(IExpr expr, IAST rules) {
    IExpr result = expr;
    for (int i = 1; i < rules.size(); i++) {
      IAST rule = (IAST) rules.get(i);
      result = F.xreplace(result, rule.first(), rule.second());
    }
    return result;
  }

  /**
   * Lexicographic comparison of two ordered rule lists by their right-hand values. Used as a
   * deterministic tie-break preferring the "smallest" point among equal optima.
   */
  private static boolean isLexSmaller(IAST a, IAST b) {
    for (int i = 1; i < a.size(); i++) {
      IExpr av = ((IAST) a.get(i)).second();
      IExpr bv = ((IAST) b.get(i)).second();
      if (av.less(bv).isTrue()) {
        return true;
      }
      if (av.greater(bv).isTrue()) {
        return false;
      }
    }
    return false;
  }

  /**
   * Test whether an <code>n x n</code> symmetric matrix is positive definite using Sylvester's
   * criterion (all leading principal minors are positive).
   */
  private static boolean isPositiveDefinite(IExpr[][] matrix, int n, EvalEngine engine) {
    for (int size = 1; size <= n; size++) {
      IASTAppendable sub = F.ListAlloc(size);
      for (int i = 0; i < size; i++) {
        IASTAppendable row = F.ListAlloc(size);
        for (int j = 0; j < size; j++) {
          row.append(matrix[i][j]);
        }
        sub.append(row);
      }
      IExpr det = S.Det.of(engine, sub);
      if (!det.isPositiveResult()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Replace every variable in <code>vars</code> by <code>0</code> in <code>expr</code> and evaluate
   * (used to extract the constant term).
   */
  private static IExpr replaceAllByZero(IExpr expr, IExpr[] vars, EvalEngine engine) {
    IExpr result = expr;
    for (IExpr var : vars) {
      result = F.xreplace(result, var, F.C0);
    }
    return engine.evaluate(result);
  }

  /** Return <code>true</code> if <code>expr</code> is free of all the given variables. */
  private static boolean isFreeOfAll(IExpr expr, IExpr[] vars) {
    for (IExpr var : vars) {
      if (!expr.isFree(var)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Exact (rational) linear programming. Optimizes a linear (affine) objective over a feasible
   * region described by a conjunction of linear equalities / inequalities via vertex enumeration.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the linear objective function
   * @param constraint a single linear (in)equality or an {@code And} of them
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the pattern doesn't
   *         match
   */
  public static IExpr linearProgrammingExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }
      // objective must be affine in the variables
      IExpr[] objCoeff = linearCoefficients(objective, vars, engine);
      if (objCoeff == null) {
        return F.NIL;
      }

      // collect the constraints (flatten a top-level And)
      List<IExpr> conjuncts = new ArrayList<>();
      if (constraint.isAST(S.And)) {
        for (int i = 1; i < constraint.size(); i++) {
          conjuncts.add(constraint.get(i));
        }
      } else {
        conjuncts.add(constraint);
      }

      // each constraint becomes a boundary expression E (E <= 0 for inequalities, E == 0 for
      // equalities) plus its normal vector
      List<IExpr> boundary = new ArrayList<>();
      List<Boolean> isEquality = new ArrayList<>();
      List<IExpr[]> normals = new ArrayList<>();
      for (IExpr c : conjuncts) {
        IExpr expr;
        boolean eq;
        if (c.isAST(S.LessEqual, 3) || c.isAST(S.Less, 3)) {
          expr = F.Subtract(c.first(), c.second());
          eq = false;
        } else if (c.isAST(S.GreaterEqual, 3) || c.isAST(S.Greater, 3)) {
          expr = F.Subtract(c.second(), c.first());
          eq = false;
        } else if (c.isAST(S.Equal, 3)) {
          expr = F.Subtract(c.first(), c.second());
          eq = true;
        } else {
          return F.NIL; // unsupported constraint type
        }
        expr = engine.evaluate(expr);
        IExpr[] coeff = linearCoefficients(expr, vars, engine);
        if (coeff == null) {
          return F.NIL; // not linear
        }
        IExpr[] normal = new IExpr[n];
        System.arraycopy(coeff, 0, normal, 0, n);
        boundary.add(expr);
        isEquality.add(eq);
        normals.add(normal);
      }
      int m = boundary.size();

      // unbounded detection: look for an objective-improving extreme ray of the recession cone
      // (intersection of n-1 constraint hyperplanes through the origin)
      for (int[] combo : combinations(m, n - 1)) {
        IExpr nullSpace;
        if (combo.length == 0) {
          nullSpace = F.NIL;
        } else {
          IASTAppendable matrix = F.ListAlloc(combo.length);
          for (int idx : combo) {
            IExpr[] nb = normals.get(idx);
            IASTAppendable row = F.ListAlloc(n);
            for (int j = 0; j < n; j++) {
              row.append(nb[j]);
            }
            matrix.append(row);
          }
          nullSpace = S.NullSpace.of(engine, matrix);
        }
        if (!nullSpace.isList()) {
          continue;
        }
        IAST nsList = (IAST) nullSpace;
        for (int r = 1; r < nsList.size(); r++) {
          IExpr dir = nsList.get(r);
          if (!dir.isList() || ((IAST) dir).argSize() != n) {
            continue;
          }
          IExpr[] base = new IExpr[n];
          for (int j = 0; j < n; j++) {
            base[j] = ((IAST) dir).get(j + 1);
          }
          for (int sign = 0; sign < 2; sign++) {
            IExpr[] ray = base;
            if (sign == 1) {
              ray = new IExpr[n];
              for (int j = 0; j < n; j++) {
                ray[j] = engine.evaluate(F.Negate(base[j]));
              }
            }
            if (isRecession(ray, normals, isEquality, engine)
                && isImproving(objCoeff, ray, isMax, engine)) {
              IASTAppendable rules = F.ListAlloc(n);
              for (int j = 0; j < n; j++) {
                rules.append(F.Rule(vars[j], S.Indeterminate));
              }
              return F.list(isMax ? F.CInfinity : F.CNInfinity, rules);
            }
          }
        }
      }

      // vertex enumeration: choose n boundary hyperplanes, solve, keep feasible vertices
      IExpr bestValue = F.NIL;
      IAST bestRules = F.NIL;
      for (int[] combo : combinations(m, n)) {
        IASTAppendable equations = F.ListAlloc(n);
        for (int idx : combo) {
          equations.append(F.Equal(boundary.get(idx), F.C0));
        }
        IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
        if (!solution.isListOfLists()) {
          continue;
        }
        IAST solutions = (IAST) solution;
        for (int s = 1; s < solutions.size(); s++) {
          IAST ordered = orderedVarRules((IAST) solutions.get(s), vars);
          if (!ordered.isPresent()) {
            continue;
          }
          boolean determined = true;
          for (int i = 1; i < ordered.size(); i++) {
            if (!isFreeOfAll(((IAST) ordered.get(i)).second(), vars)) {
              determined = false;
              break;
            }
          }
          if (!determined || !isFeasible(boundary, isEquality, ordered, engine)) {
            continue;
          }
          IExpr value = engine.evaluate(applyRules(objective, ordered));
          if (!isFreeOfAll(value, vars)) {
            continue;
          }
          if (!bestValue.isPresent()) {
            bestValue = value;
            bestRules = ordered;
          } else {
            boolean better =
                isMax ? value.greater(bestValue).isTrue() : value.less(bestValue).isTrue();
            if (better || (value.equals(bestValue) && isLexSmaller(ordered, bestRules))) {
              bestValue = value;
              bestRules = ordered;
            }
          }
        }
      }
      if (bestValue.isPresent()) {
        return engine.evaluate(F.list(bestValue, bestRules));
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Extract the affine coefficients of <code>expr</code> with respect to <code>vars</code>. Returns
   * an array of length <code>n+1</code> or <code>null</code> if <code>expr</code> isn't affine in
   * the variables.
   */
  private static IExpr[] linearCoefficients(IExpr expr, IExpr[] vars, EvalEngine engine) {
    int n = vars.length;
    IExpr[] res = new IExpr[n + 1];
    IExpr approx = F.NIL;
    for (int i = 0; i < n; i++) {
      IExpr ci = S.Coefficient.of(engine, expr, vars[i]);
      if (!isFreeOfAll(ci, vars)) {
        return null;
      }
      res[i] = ci;
      approx = approx.isPresent() ? F.Plus(approx, F.Times(ci, vars[i])) : F.Times(ci, vars[i]);
    }
    IExpr constant = replaceAllByZero(expr, vars, engine);
    if (!isFreeOfAll(constant, vars)) {
      return null;
    }
    res[n] = constant;
    IExpr remainder =
        engine.evaluate(F.Subtract(expr, F.Plus(approx.isPresent() ? approx : F.C0, constant)));
    if (!remainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return null;
    }
    return res;
  }

  /**
   * Exact global extremum of a bounded linear-trigonometric objective of the form
   * <code>a*Sin(x) + b*Cos(x) + c</code> (with <code>a</code>, <code>b</code>, <code>c</code> free
   * of <code>x</code>). Such an objective is bounded with amplitude
   * <code>R = Sqrt(a^2 + b^2)</code>, so its global maximum is <code>c + R</code> and its global
   * minimum is <code>c - R</code>, attained at <code>x = ArcTan(b, a)</code> (maximum) or
   * <code>x = ArcTan(-b, -a)</code> (minimum). This covers <code>Sin(x)</code>, <code>Cos(x)</code>
   * and their linear combinations following the WMA conventions.
   *
   * @param objective the objective function
   * @param x the (single) variable
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {x -> p}}</code> or {@link F#NIL} if the objective isn't of
   *         the supported linear-trigonometric form
   */
  public static IExpr linearTrigExtremum(IExpr objective, IExpr x, boolean isMax,
      EvalEngine engine) {
    try {
      if (!x.isSymbol()) {
        return F.NIL;
      }
      // must actually contain Sin(x) or Cos(x)
      if (objective.isFree(S.Sin) && objective.isFree(S.Cos)) {
        return F.NIL;
      }
      IExpr sinX = F.Sin(x);
      IExpr cosX = F.Cos(x);
      // a*Sin(x) + b*Cos(x) + c, treating Sin(x) and Cos(x) as independent generators
      IExpr a = S.Coefficient.of(engine, objective, sinX);
      if (!a.isFree(x)) {
        return F.NIL;
      }
      IExpr b = S.Coefficient.of(engine, objective, cosX);
      if (!b.isFree(x)) {
        return F.NIL;
      }
      IExpr c = engine.evaluate(F.xreplace(F.xreplace(objective, sinX, F.C0), cosX, F.C0));
      if (!c.isFree(x)) {
        return F.NIL;
      }
      // verify objective == a*Sin(x) + b*Cos(x) + c (rejects Sin(x)^2, Sin(2*x), Tan(x), ...)
      IExpr remainder =
          engine.evaluate(F.Subtract(objective, F.Plus(F.Times(a, sinX), F.Times(b, cosX), c)));
      if (!remainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return F.NIL;
      }
      IExpr r = engine.evaluate(F.Sqrt(F.Plus(F.Sqr(a), F.Sqr(b))));
      if (r.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        // constant objective: leave it for the other paths
        return F.NIL;
      }
      IExpr value;
      IExpr point;
      if (isMax) {
        value = engine.evaluate(F.Plus(c, r));
        point = engine.evaluate(F.ArcTan(b, a));
      } else {
        value = engine.evaluate(F.Subtract(c, r));
        point = engine.evaluate(F.ArcTan(F.Negate(b), F.Negate(a)));
      }
      return engine.evaluate(F.list(value, F.list(F.Rule(x, point))));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** Test whether <code>point</code> satisfies every constraint (E &lt;= 0 or E == 0). */
  private static boolean isFeasible(List<IExpr> boundary, List<Boolean> isEquality, IAST point,
      EvalEngine engine) {
    for (int i = 0; i < boundary.size(); i++) {
      IExpr value = engine.evaluate(applyRules(boundary.get(i), point));
      if (isEquality.get(i)) {
        if (!value.isZero()) {
          return false;
        }
      } else if (!(value.isZero() || value.isNegativeResult())) {
        return false;
      }
    }
    return true;
  }

  /** Test whether <code>ray</code> belongs to the recession cone of the feasible region. */
  private static boolean isRecession(IExpr[] ray, List<IExpr[]> normals, List<Boolean> isEquality,
      EvalEngine engine) {
    for (int i = 0; i < normals.size(); i++) {
      IExpr d = dot(normals.get(i), ray, engine);
      if (isEquality.get(i)) {
        if (!d.isZero()) {
          return false;
        }
      } else if (!(d.isZero() || d.isNegativeResult())) {
        return false;
      }
    }
    return true;
  }

  /** Test whether moving along <code>ray</code> strictly improves the objective. */
  private static boolean isImproving(IExpr[] objCoeff, IExpr[] ray, boolean isMax,
      EvalEngine engine) {
    IExpr d = F.C0;
    for (int j = 0; j < ray.length; j++) {
      d = F.Plus(d, F.Times(objCoeff[j], ray[j]));
    }
    d = engine.evaluate(d);
    return isMax ? d.isPositiveResult() : d.isNegativeResult();
  }

  /** Exact dot product of two vectors. */
  private static IExpr dot(IExpr[] a, IExpr[] b, EvalEngine engine) {
    IExpr d = F.C0;
    for (int j = 0; j < a.length; j++) {
      d = F.Plus(d, F.Times(a[j], b[j]));
    }
    return engine.evaluate(d);
  }

  /** Generate all <code>k</code>-element index combinations from <code>{0, .., m-1}</code>. */
  private static List<int[]> combinations(int m, int k) {
    List<int[]> result = new ArrayList<>();
    if (k < 0 || k > m) {
      return result;
    }
    if (k == 0) {
      result.add(new int[0]);
      return result;
    }
    int[] combo = new int[k];
    for (int i = 0; i < k; i++) {
      combo[i] = i;
    }
    while (true) {
      result.add(combo.clone());
      int i = k - 1;
      while (i >= 0 && combo[i] == m - k + i) {
        i--;
      }
      if (i < 0) {
        break;
      }
      combo[i]++;
      for (int j = i + 1; j < k; j++) {
        combo[j] = combo[j - 1] + 1;
      }
    }
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  public static IAST maximizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    long varDegree = polynomial.degree(0);
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 3) {
      // solve cubic or quadratic maximize:
      a = F.C0;
      b = F.C0;
      c = F.C0;
      d = F.C0;
      e = F.C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Maximize::Unexpected exponent value: " + lExp);
        }
      }
      if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // quadratic
          if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              // The `1` is not attained at any point satisfying the constraints.
              return Errors.printMessage(S.Maximize, "natt", F.List("maximum"));
            } else {
              // linear
              return F.list(F.Piecewise(F.list(F.list(e, F.Equal(d, F.C0))), F.CInfinity), F.list(
                  F.Rule(x, F.Piecewise(F.list(F.list(F.C0, F.Equal(d, F.C0))), S.Indeterminate))));
            }
          } else {
            return F
                .List(
                    F.Piecewise(
                        F.list(F.list(e, F.And(F.Equal(d, 0), F.LessEqual(c, 0))),
                            F.list(
                                F.Times(F.C1D4, F.Power(c, -1),
                                    F.Plus(F.Times(-1, F.Power(d, 2)), F.Times(4, c, e))),
                                F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                    F.And(F.Less(d, 0), F.Less(c, 0))))),
                        F.CInfinity),
                    F.list(F.Rule(x,
                        F.Piecewise(
                            F.list(
                                F.list(F.Times(F.CN1D2, F.Power(c, -1), d),
                                    F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                        F.And(F.Less(d, 0), F.Less(c, 0)))),
                                F.list(F.C0, F.And(F.Equal(d, 0), F.LessEqual(c, 0)))),
                            S.Indeterminate))));
          }
        } else {
          // cubic
          return F.list(
              F.Piecewise(
                  F.list(
                      F.list(e,
                          F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                      F.list(
                          F.Times(F.C1D4, F.Power(c, F.CN1),
                              F.Plus(F.Negate(F.Sqr(d)), F.Times(F.C4, c, e))),
                          F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                  F.oo),
              F.list(F.Rule(x,
                  F.Piecewise(
                      F.list(
                          F.list(F.Times(F.CN1D2, F.Power(c, F.CN1), d),
                              F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                          F.list(F.C0,
                              F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                      F.Indeterminate))));
        }
      }
    }

    return F.NIL;
  }

  public static IAST maximizeExprPolynomial(final IExpr expr, IAST varList) {
    IAST result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = Maximize.maximizeCubicPolynomial(ePoly, varList.arg1());
      return result;
    } catch (ArithmeticException | JASConversionException e2) {
      return Errors.printMessage(S.Maximize, e2);
    }
  }

  public static IExpr maximize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    try {
      // bounded linear-trigonometric objective (a*Sin(x) + b*Cos(x) + c)
      IExpr trig = linearTrigExtremum(function, x, true, engine);
      if (trig.isPresent()) {
        return trig;
      }
      IExpr temp = maximizeExprPolynomial(function, F.list(x));
      if (temp.isPresent()) {
        return temp;
      }

      IExpr yNInf = S.Limit.funEval(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isInfinity()) {
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.funEval(function, F.Rule(x, F.CInfinity));
      if (yInf.isInfinity()) {
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CInfinity)));
      }

      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.funEval(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr maxCandidate = F.NIL;
        IExpr maxValue = F.CNInfinity;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr candidate = ((IAST) candidates).get(i).first().second();
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isNegative()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (functionValue.greater(maxValue).isTrue()) {
                maxValue = functionValue;
                maxCandidate = candidate;
              }
            }
          }
          if (maxCandidate.isPresent()) {
            return F.list(maxValue, F.list(F.Rule(x, maxCandidate)));
          }
        }
        return F.CEmptyList;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Maximize, rex);
    }
    return F.NIL;
  }
}
