package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.AlgebraUtil;
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
        return integerExtremum(head, function, x, true, engine);
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
        // a constrained problem which isn't decided stays unevaluated; the unconstrained method
        // cannot read the {objective, constraint} list
        return result;
      }
      return maximize(head, function, x, engine);
    }

    return F.NIL;
  }

  /**
   * A problem with roots <code>g^(p/q)</code> of expressions in the variables: each root gets a
   * variable <code>u</code> with <code>u^q == g</code>, <code>u &gt;= 0</code> and
   * <code>g &gt;= 0</code>, which is where the real root is defined, and the problem in the
   * variables and the <code>u</code> is one of polynomials.
   *
   * @return {@link F#NIL} if there is no such root or the polynomial problem is not solved
   */
  private static IExpr radicalExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    List<IExpr> radicands = new ArrayList<>();
    List<IExpr> roots = new ArrayList<>();
    List<IExpr> denominators = new ArrayList<>();
    IExpr[] problem = {objective, constraint};
    for (int round = 0; round < 8; round++) {
      // innermost roots first
      IExpr[] found = {F.NIL};
      for (IExpr part : problem) {
        if (found[0].isNIL()) {
          found[0] = innermostRoot(part, varList);
        }
      }
      if (found[0].isNIL()) {
        break;
      }
      final IExpr base = found[0].base();
      final IExpr q = ((org.matheclipse.core.interfaces.IRational) found[0].exponent())
          .denominator();
      final ISymbol u = F.Dummy("u" + radicands.size());
      radicands.add(base);
      roots.add(u);
      denominators.add(q);
      for (int i = 0; i < problem.length; i++) {
        problem[i] = engine.evaluate(F.subst(problem[i], t -> {
          if (t.isPower() && t.base().equals(base) && t.exponent().isFraction()
              && ((org.matheclipse.core.interfaces.IRational) t.exponent()).denominator()
                  .equals(q)) {
            return F.Power(u,
                ((org.matheclipse.core.interfaces.IRational) t.exponent()).numerator());
          }
          return F.NIL;
        }));
      }
    }
    if (radicands.isEmpty() || innermostRoot(problem[0], varList).isPresent()
        || innermostRoot(problem[1], varList).isPresent()) {
      return F.NIL;
    }
    IASTAppendable extendedVars = varList.copyAppendable();
    IASTAppendable extended = F.ast(S.And);
    List<IExpr> conjuncts = new ArrayList<>();
    flattenConstraints(problem[1], conjuncts);
    extended.appendAll(conjuncts);
    for (int i = 0; i < roots.size(); i++) {
      extendedVars.append(roots.get(i));
      extended.append(F.Equal(F.Power(roots.get(i), denominators.get(i)), radicands.get(i)));
      extended.append(F.GreaterEqual(roots.get(i), F.C0));
      if (!radicands.get(i).isSymbol() || !roots.contains(radicands.get(i))) {
        extended.append(F.GreaterEqual(radicands.get(i), F.C0));
      }
    }
    IExpr result = lagrangeExtremum(head, problem[0], extended, extendedVars, isMax, engine);
    if (!result.isList2() || !result.second().isList()) {
      return F.NIL;
    }
    IASTAppendable rules = F.ListAlloc(varList.argSize());
    for (IExpr rule : (IAST) result.second()) {
      if (rule.isRule() && !roots.contains(rule.first())) {
        rules.append(rule);
      }
    }
    return rules.argSize() == varList.argSize() ? F.list(result.first(), rules) : F.NIL;
  }

  /** A power with a fractional exponent of a base which has a variable and no such power. */
  private static IExpr innermostRoot(IExpr expr, IAST varList) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    for (IExpr arg : (IAST) expr) {
      IExpr inner = innermostRoot(arg, varList);
      if (inner.isPresent()) {
        return inner;
      }
    }
    if (expr.isPower() && expr.exponent().isFraction() && !expr.base().isFree(v -> {
      for (IExpr variable : varList) {
        if (variable.equals(v)) {
          return true;
        }
      }
      return false;
    }, true)) {
      return expr;
    }
    return F.NIL;
  }

  /** The extremum over the integer points: one variable, or a bounded region of several. */
  static IExpr integerExtremum(ISymbol head, IExpr function, IExpr x, boolean isMax,
      EvalEngine engine) {
    IExpr var = x.isList1() ? x.first() : x;
    if (var.isSymbol()) {
      return univariateIntegerExtremum(head, function, var, isMax, engine);
    }
    if (x.isList() && function.isList2()) {
      return boundedIntegerExtremum(function.first(), function.second(), (IAST) x, isMax, engine);
    }
    return F.NIL;
  }

  /**
   * The extremum over the integer points of a bounded region: every variable is bounded by its
   * real minimum and maximum on the region, and the integer points of that box are tested one by
   * one. Of several points with the best value the first in the order of the coordinates is
   * returned.
   *
   * @return {@link F#NIL} if a bound is not found or the box is too large
   */
  private static IExpr boundedIntegerExtremum(IExpr objective, IExpr constraint, IAST varList,
      boolean isMax, EvalEngine engine) {
    try {
      final int n = varList.argSize();
      final long deadline = deadline();
      IInteger[] from = new IInteger[n];
      IInteger[] to = new IInteger[n];
      long points = 1;
      for (int i = 0; i < n; i++) {
        IExpr v = varList.get(i + 1);
        if (!v.isSymbol()) {
          return F.NIL;
        }
        IExpr min = realBound(v, constraint, varList, false, deadline, engine);
        IExpr max = realBound(v, constraint, varList, true, deadline, engine);
        if (min.isNIL() || max.isNIL()) {
          return F.NIL;
        }
        IExpr lo = engine.evaluate(F.Ceiling(min));
        IExpr hi = engine.evaluate(F.Floor(max));
        if (!lo.isInteger() || !hi.isInteger()) {
          return F.NIL;
        }
        from[i] = (IInteger) lo;
        to[i] = (IInteger) hi;
        if (from[i].isGT(to[i])) {
          // no integer point
          return F.NIL;
        }
        long width = to[i].subtract(from[i]).toLongDefault(Long.MAX_VALUE);
        if (width >= INTEGER_ENUMERATION_LIMIT) {
          return F.NIL;
        }
        points *= width + 1;
        if (points > INTEGER_ENUMERATION_LIMIT) {
          return F.NIL;
        }
      }
      IInteger[] point = from.clone();
      IExpr bestValue = F.NIL;
      IAST bestRules = F.NIL;
      while (true) {
        IASTAppendable rules = F.ListAlloc(n);
        for (int i = 0; i < n; i++) {
          rules.append(F.Rule(varList.get(i + 1), point[i]));
        }
        IExpr feasible = engine.evalQuiet(F.ReplaceAll(constraint, rules));
        if (feasible.isAST(S.List)) {
          feasible = engine.evalQuiet(((IAST) feasible).apply(S.And));
        }
        if (feasible.isTrue()) {
          IExpr value = engine.evaluate(F.ReplaceAll(objective, rules));
          int order = bestValue.isNIL() ? (isMax ? 1 : -1) : compareValues(value, bestValue, engine);
          if (order == Integer.MIN_VALUE) {
            return F.NIL;
          }
          // the points are visited in the order of their coordinates: the first best one stays
          if (isMax ? order > 0 : order < 0) {
            bestValue = value;
            bestRules = rules;
          }
        } else if (!feasible.isFalse()) {
          return F.NIL;
        }
        int i = n - 1;
        while (i >= 0 && point[i].equals(to[i])) {
          point[i] = from[i];
          i--;
        }
        if (i < 0) {
          break;
        }
        point[i] = point[i].inc();
      }
      return bestRules.isNIL() ? F.NIL : F.list(bestValue, bestRules);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** The real minimum or maximum of one variable on the region, or {@link F#NIL}. */
  private static IExpr realBound(IExpr v, IExpr constraint, IAST varList, boolean isMax,
      long deadline, EvalEngine engine) {
    IExpr result = lagrangeExtremum(isMax ? S.Maximize : S.Minimize, v, constraint, varList,
        isMax, MODE_BOUND, deadline, engine);
    if (result.isList2() && isRealValue(result.first(), engine)) {
      return result.first();
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
      if (innermostRoot(function.first(), varList).isPresent()
          || innermostRoot(function.second(), varList).isPresent()) {
        // the stationary points of the form with the roots miss the points where a root is 0
        return radicalExtremum(head, function.first(), function.second(), varList, isMax, engine);
      }
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
        if (isPoleAwareQuotient(objective, x, engine)) {
          // a rational objective (or one with only polynomial poles) is decided with its poles
          IAST domain = quotientDomain(objective, x, engine);
          IAST feasible = domain == null ? F.NIL
              : isRealLine(domain) ? intervalData
                  : IntervalDataSym.intersection(intervalData, domain, engine, true);
          if (feasible.isPresent()) {
            if (feasible.argSize() == 0) {
              // no feasible point where the objective is real valued (like WMA)
              Errors.printMessage(head, "infeas", F.List(F.List(x), constraint, objective),
                  engine);
              return F.list(isMax ? F.CNInfinity : F.CInfinity,
                  F.list(F.Rule(x, S.Indeterminate)));
            }
            IExpr quotient =
                quotientConstrainedExtremum(head, objective, x, feasible, isMax, engine);
            if (quotient.isPresent()) {
              return quotient;
            }
          }
        }
        IExpr unbounded = unboundedUnivariateExtremum(objective, x, intervalData, isMax, engine);
        if (unbounded.isPresent()) {
          return unbounded;
        }
        IExpr interval = univariateIntervalExtremum(objective, x, intervalData, isMax, engine);
        if (interval.isPresent() || !isRationalObjective(objective, x, engine)) {
          // the Lagrange method drops periodic families of stationary points and misses limits
          // at the ends of the region; a non rational objective which cannot be decided here
          // stays unevaluated
          return interval;
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

  /** The most members of a periodic family of stationary points which are enumerated. */
  private static final int FAMILY_ENUMERATION_LIMIT = 1000;

  /**
   * Optimize a univariate objective which isn't a rational function over the feasible intervals.
   * The candidates are the real stationary points inside an interval - including the members of a
   * periodic family <code>a+b*C(1)</code> which lie in it, which the Lagrange method doesn't see -
   * the closed endpoints, and the limits at the open or infinite ends, which are not attained:
   * <code>Minimize({Sin(t), t&gt;0}, t)</code> is <code>{-1, {t-&gt;3*Pi/2}}</code>.
   *
   * @return <code>{value, {x-&gt;point}}</code> or {@link F#NIL} if a candidate cannot be
   *         determined
   */
  private static IExpr univariateIntervalExtremum(IExpr objective, IExpr x, IAST intervalData,
      boolean isMax, EvalEngine engine) {
    IExpr derivative = S.D.of(engine, objective, x);
    IExpr solution = engine.evalQuiet(F.Solve(F.Equal(derivative, F.C0), x, S.Reals));
    if (!solution.isList() || !solution.isFree(S.Solve)) {
      return F.NIL;
    }
    List<IExpr> stationary = new ArrayList<IExpr>();
    List<IExpr[]> families = new ArrayList<IExpr[]>();
    ISymbol k = F.Dummy("k");
    for (IExpr rules : (IAST) solution) {
      if (!rules.isList1() || !rules.first().isRule()) {
        return F.NIL;
      }
      IExpr point = rules.first().second();
      if (point.isConditionalExpression()) {
        IExpr condition = point.second();
        if (!condition.isAST(S.Element, 3) || !condition.first().isAST(S.C, 2)
            || condition.second() != S.Integers) {
          return F.NIL;
        }
        IExpr c = condition.first();
        IExpr member = F.subst(point.first(), x2 -> x2.equals(c) ? k : F.NIL);
        if (!member.isFree(S.C, true)) {
          return F.NIL;
        }
        IExpr offset = engine.evaluate(F.subst(member, k, F.C0));
        IExpr step = engine.evaluate(F.D(member, k));
        if (!offset.isRealResult() || !step.isRealResult() || step.isZero()
            || !engine.evaluate(F.D(step, k)).isZero()) {
          return F.NIL;
        }
        families.add(new IExpr[] {offset, step});
      } else if (point.isRealResult()) {
        stationary.add(point);
      } else {
        return F.NIL;
      }
    }

    Extremum best = new Extremum(isMax);
    for (int i = 1; i < intervalData.size(); i++) {
      IExpr entry = intervalData.get(i);
      if (!entry.isList() || ((IAST) entry).argSize() != 4) {
        return F.NIL;
      }
      IAST interval = (IAST) entry;
      IExpr lo = interval.arg1();
      IExpr hi = interval.arg4();
      boolean loClosed = interval.arg2() == S.LessEqual;
      boolean hiClosed = interval.arg3() == S.LessEqual;
      for (IExpr point : stationary) {
        if (isInside(point, lo, loClosed, hi, hiClosed, engine)
            && !best.consider(engine.evaluate(F.xreplace(objective, x, point)), point, true,
                engine)) {
          return F.NIL;
        }
      }
      // on an unbounded interval every family is constant valued (else it declines), so its
      // members bound the values which a non existing limit at infinity leaves open
      boolean periodic = !families.isEmpty();
      for (IExpr[] family : families) {
        if (!familyCandidates(objective, x, family[0], family[1], lo, loClosed, hi, hiClosed,
            best, engine)) {
          return F.NIL;
        }
      }
      if (!endCandidate(objective, x, lo, loClosed, F.CNInfinity, periodic, best, engine)
          || !endCandidate(objective, x, hi, hiClosed, F.CInfinity, periodic, best, engine)) {
        return F.NIL;
      }
    }
    if (best.value.isNIL()) {
      return F.NIL;
    }
    return F.list(best.value, F.list(F.Rule(x, best.point)));
  }

  /** Test if the objective is a rational function of <code>x</code>. */
  private static boolean isRationalObjective(IExpr objective, IExpr x, EvalEngine engine) {
    IExpr together = engine.evalQuiet(F.Together(objective));
    return engine.evalQuiet(F.Numerator(together)).isPolynomial(x)
        && engine.evalQuiet(F.Denominator(together)).isPolynomial(x);
  }

  /** The best value found so far; an attained value wins a tie against a limit. */
  private static final class Extremum {
    final boolean isMax;
    IExpr value = F.NIL;
    IExpr point = F.NIL;
    double numeric;
    boolean attained;

    Extremum(boolean isMax) {
      this.isMax = isMax;
    }

    /** @return <code>false</code> if the value is not a real number */
    boolean consider(IExpr candidate, IExpr at, boolean isAttained, EvalEngine engine) {
      double d = engine.evalQuiet(F.N(candidate)).evalfNaN();
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        return false;
      }
      if (value.isPresent()) {
        double tolerance = Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, Math.abs(d));
        double improvement = isMax ? d - numeric : numeric - d;
        if (improvement < -tolerance || (improvement <= tolerance && (attained || !isAttained))) {
          return true;
        }
      }
      value = candidate;
      point = at;
      numeric = d;
      attained = isAttained;
      return true;
    }
  }

  private static boolean isInside(IExpr point, IExpr lo, boolean loClosed, IExpr hi,
      boolean hiClosed, EvalEngine engine) {
    return engine.evalTrue(loClosed ? F.LessEqual(lo, point) : F.Less(lo, point))
        && engine.evalTrue(hiClosed ? F.LessEqual(point, hi) : F.Less(point, hi));
  }

  /**
   * Whether the objective takes the same value at the five members <code>-2, ..., 2</code> of a
   * family of points.
   *
   * @param member the member of the family for an index
   * @return <code>1</code> if it does, <code>0</code> if the values differ and <code>-1</code> if
   *         one of them is no real number
   */
  private static int sameValueAlongFamily(IExpr objective, IExpr x,
      java.util.function.IntFunction<IExpr> member, EvalEngine engine) {
    boolean constant = true;
    double reference = Double.NaN;
    for (int m = -2; m <= 2; m++) {
      double d = engine.evalQuiet(F.N(F.xreplace(objective, x, member.apply(m)))).evalfNaN();
      if (Double.isNaN(d)) {
        return -1;
      }
      if (m == -2) {
        reference = d;
      } else if (Math.abs(d - reference) > Config.SPECIAL_FUNCTIONS_TOLERANCE
          * Math.max(1.0, Math.abs(reference))) {
        constant = false;
      }
    }
    return constant ? 1 : 0;
  }

  /**
   * Whether a solution of <code>f'(x) == 0</code> is a periodic family
   * <code>ConditionalExpression(offset + step*C(1), C(1) &isin; Integers)</code>. A solution with
   * any other condition - one on a parameter, say - is none.
   */
  private static boolean isPeriodicFamily(IExpr solution) {
    return solution.isConditionalExpression() && solution.second().isAST(S.Element, 3)
        && solution.second().first().isAST(S.C, 2) && solution.second().second() == S.Integers
        && !solution.first().isFree(solution.second().first());
  }

  private static IExpr familyMemberAt(IExpr solution, int index) {
    final IExpr constant = solution.second().first();
    final IExpr k = F.ZZ(index);
    return F.subst(solution.first(), e -> e.equals(constant) ? k : F.NIL);
  }

  /**
   * The stationary point which stands for a solution of <code>f'(x) == 0</code>: the solution
   * itself, or for a periodic family its member with <code>C(1) = 0</code>, if the function takes
   * the same value at every member - as <code>Sin(2*t)</code> does at <code>Pi/4 + Pi*C(1)</code>.
   *
   * @return {@link F#NIL} if the function changes along the family
   */
  static IExpr familyMember(IExpr function, IExpr x, IExpr solution, EvalEngine engine) {
    if (!isPeriodicFamily(solution)) {
      return solution;
    }
    if (sameValueAlongFamily(function, x, m -> familyMemberAt(solution, m), engine) != 1) {
      return F.NIL;
    }
    return engine.evaluate(familyMemberAt(solution, 0));
  }

  /** How many points of one period are sampled to confirm an extremum of a periodic family. */
  private static final int PERIOD_SAMPLES = 720;

  /**
   * Whether the extremum found at a member of a periodic family holds over the whole period: the
   * function is sampled between two neighbouring members, and a sample which is no real number or
   * lies beyond the value - near a pole, say - refutes it. A stationary point alone is only a
   * local extremum.
   *
   * @param solution a solution of <code>f'(x) == 0</code>; anything but a periodic family is
   *        confirmed without sampling
   * @param maximum <code>true</code> for a maximum, <code>false</code> for a minimum
   */
  static boolean holdsOverPeriod(IExpr function, IExpr x, IExpr solution, IExpr value,
      boolean maximum, EvalEngine engine) {
    if (!isPeriodicFamily(solution)) {
      return true;
    }
    double start = engine.evalQuiet(F.N(familyMemberAt(solution, 0))).evalfNaN();
    double end = engine.evalQuiet(F.N(familyMemberAt(solution, 1))).evalfNaN();
    double bound = engine.evalQuiet(F.N(value)).evalfNaN();
    if (Double.isNaN(start) || Double.isNaN(end) || Double.isNaN(bound) || start == end) {
      return false;
    }
    double tolerance = Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, Math.abs(bound));
    for (int i = 1; i < PERIOD_SAMPLES; i++) {
      double point = start + (end - start) * i / PERIOD_SAMPLES;
      double sample = engine.evalQuiet(F.N(F.xreplace(function, x, F.num(point)))).evalfNaN();
      if (!Double.isFinite(sample)
          || (maximum ? sample > bound + tolerance : sample < bound - tolerance)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Add the members <code>offset+step*k</code> of a periodic family of stationary points which lie
   * in the interval. On an unbounded interval this is only possible if the objective takes the
   * same value at every member.
   */
  private static boolean familyCandidates(IExpr objective, IExpr x, IExpr offset, IExpr step,
      IExpr lo, boolean loClosed, IExpr hi, boolean hiClosed, Extremum best, EvalEngine engine) {
    double a = engine.evalQuiet(F.N(offset)).evalfNaN();
    double b = engine.evalQuiet(F.N(step)).evalfNaN();
    if (Double.isNaN(a) || Double.isNaN(b)) {
      return false;
    }
    int sameValue = sameValueAlongFamily(objective, x,
        m -> F.Plus(offset, F.Times(F.ZZ(m), step)), engine);
    if (sameValue < 0) {
      return false;
    }
    boolean constant = sameValue == 1;
    double loD = lo.isNegativeInfinity() ? Double.NEGATIVE_INFINITY
        : engine.evalQuiet(F.N(lo)).evalfNaN();
    double hiD = hi.isInfinity() ? Double.POSITIVE_INFINITY
        : engine.evalQuiet(F.N(hi)).evalfNaN();
    if (Double.isNaN(loD) || Double.isNaN(hiD)) {
      return false;
    }
    double k1 = (loD - a) / b;
    double k2 = (hiD - a) / b;
    double kMin = Math.min(k1, k2);
    double kMax = Math.max(k1, k2);
    long first;
    long last;
    if (Double.isInfinite(kMin) && Double.isInfinite(kMax)) {
      first = -1;
      last = 1;
    } else if (Double.isInfinite(kMin)) {
      first = (long) Math.floor(kMax) - 2;
      last = (long) Math.floor(kMax) + 1;
    } else if (Double.isInfinite(kMax)) {
      first = (long) Math.ceil(kMin) - 1;
      last = (long) Math.ceil(kMin) + 2;
    } else {
      first = (long) Math.ceil(kMin) - 1;
      last = (long) Math.floor(kMax) + 1;
    }
    if (!constant && (Double.isInfinite(kMin) || Double.isInfinite(kMax))) {
      return false;
    }
    if (last - first > FAMILY_ENUMERATION_LIMIT) {
      if (!constant) {
        return false;
      }
      last = first + 3;
    }
    for (long m = first; m <= last; m++) {
      IExpr point = engine.evaluate(F.Plus(offset, F.Times(F.ZZ(m), step)));
      if (isInside(point, lo, loClosed, hi, hiClosed, engine)) {
        if (!best.consider(engine.evaluate(F.xreplace(objective, x, point)), point, true,
            engine)) {
          return false;
        }
        if (constant) {
          return true;
        }
      }
    }
    return true;
  }

  /**
   * Add the value at a closed finite end, or the limit towards an open or infinite end, which is
   * not attained. A limit which doesn't exist, like the one of <code>Sin(x)</code> at infinity,
   * adds no candidate if a periodic family of stationary points bounds the values.
   */
  private static boolean endCandidate(IExpr objective, IExpr x, IExpr end, boolean closed,
      IExpr infinity, boolean periodic, Extremum best, EvalEngine engine) {
    if (end.equals(infinity)) {
      IExpr limit = engine.evalQuiet(F.Limit(objective, F.Rule(x, infinity)));
      if (periodic && (limit.isAST(S.Interval) || limit.isIndeterminate()
          || limit.isAST(S.Limit))) {
        return true;
      }
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        // an end which improves the objective was already detected
        return true;
      }
      return best.consider(limit, infinity, false, engine);
    }
    IExpr value = engine.evalQuiet(F.xreplace(objective, x, end));
    if (closed) {
      return best.consider(value, end, true, engine);
    }
    if (value.isRealResult() && !value.isDirectedInfinity() && !value.isIndeterminate()) {
      return best.consider(value, end, false, engine);
    }
    IExpr limit = engine.evalQuiet(F.Limit(objective, F.Rule(x, end)));
    if (limit.isInfinity() || limit.isNegativeInfinity()) {
      return true;
    }
    return best.consider(limit, end, false, engine);
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
        IExpr limit = S.Limit.of(objective, F.Rule(x, F.CInfinity));
        if (isMax ? limit.isInfinity() : limit.isNegativeInfinity()) {
          return F.list(isMax ? F.CInfinity : F.CNInfinity, F.list(F.Rule(x, F.CInfinity)));
        }
      }
      if (min.isNegativeInfinity()) {
        IExpr limit = S.Limit.of(objective, F.Rule(x, F.CNInfinity));
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
    // a caller which only looks for a point of the region takes any stationary point in it
    return lagrangeExtremum(head, objective, constraint, varList, isMax,
        head == S.Minimize || head == S.Maximize ? MODE_PROVED : MODE_ANY_POINT, deadline(),
        engine);
  }

  /** The extremum, which is returned only with a proof that there is one. */
  private static final int MODE_PROVED = 0;

  /**
   * Only the value is wanted, as a bound of the objective on the region: a family of stationary
   * points along which the objective is constant counts with its value, and so does a point
   * whose feasibility is not decided. The value is then a bound, which may not be attained.
   */
  private static final int MODE_BOUND = 1;

  /** Any stationary point which is in the region, for a caller which looks for an instance. */
  private static final int MODE_ANY_POINT = 2;

  /** The time one optimization may take with all of its Solve calls. */
  private static final long OPTIMIZATION_MILLIS = 30_000L;

  /** The time in nanoseconds at which an optimization which starts now gives up. */
  private static long deadline() {
    return System.nanoTime() + (long) (OPTIMIZATION_MILLIS * 1.0e6
        * org.matheclipse.core.basic.MachineProfile.getScale());
  }

  private static IExpr lagrangeExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, int mode, long deadline, EvalEngine engine) {
    final boolean boundOnly = mode == MODE_BOUND;
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
      List<Boolean> strict = new ArrayList<>();
      for (IExpr c : conjuncts) {
        if (!parseComparator(c, engine, equalities, inequalities, inequalitySigns, strict)) {
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
      // the values of families of stationary points along which the objective is constant
      List<IExpr> familyValues = new ArrayList<>();

      // enumerate every active set of inequality constraints (equality constraints are always
      // active). For each active set enforce the KKT stationarity condition via vanishing minors
      // and solve together with the active boundary equations.
      for (int mask = 0; mask < (1 << m); mask++) {
        if (System.nanoTime() > deadline) {
          // the active sets which are left may hold the extremum
          return F.NIL;
        }
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
        IExpr solution = engine.evalQuiet(F.Solve(equations, varList, S.Reals));
        if (!collectCandidatesMulti(solution, vars, objective, equalities, inequalities,
            inequalitySigns, candidateValues, candidateRules, familyValues, mode, engine)) {
          // the stationary points of this active set are not known completely: a family of
          // them, or a system which is not solved. The best of the others proves nothing.
          return F.NIL;
        }
      }

      if (mode == MODE_ANY_POINT) {
        // the best of the points which satisfy the strict inequalities strictly
        int chosen = -1;
        for (int i = 0; i < candidateRules.size(); i++) {
          if (!isStrictlyFeasible(candidateRules.get(i), inequalities, inequalitySigns, strict,
              engine)) {
            continue;
          }
          if (chosen < 0) {
            chosen = i;
            continue;
          }
          int order = compareValues(candidateValues.get(i), candidateValues.get(chosen), engine);
          if (order != Integer.MIN_VALUE && ((isMax ? order > 0 : order < 0) || (order == 0
              && isLexSmaller(candidateRules.get(i), candidateRules.get(chosen))))) {
            chosen = i;
          }
        }
        return chosen < 0 ? F.NIL
            : engine.evaluate(F.list(candidateValues.get(chosen), candidateRules.get(chosen)));
      }
      if (candidateValues.isEmpty()) {
        if (!boundOnly && isReducedToFalse(constraint, varList, engine)) {
          // There are no values of `1` for which the constraints `2` are satisfied and the
          // objective function `3` is real valued.
          Errors.printMessage(head, "infeas", F.List(varList, constraint, objective), engine);
          return F.list(isMax ? F.CNInfinity : F.CInfinity,
              varList.map(v -> F.Rule(v, S.Indeterminate), 1));
        }
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
        int order = compareValues(value, bestValue, engine);
        if (order == Integer.MIN_VALUE) {
          return F.NIL;
        }
        boolean better = isMax ? order > 0 : order < 0;
        if (better) {
          best = i;
          bestValue = value;
        } else if (order == 0 && !boundOnly
            && isLexSmaller(candidateRules.get(i), candidateRules.get(best))) {
          best = i;
          bestValue = value;
        }
      }
      if (boundOnly) {
        // a bound over the closure of the region is a bound over the region
        if (!isPolynomialProblem(objective, equalities, inequalities, varList)
            || !isBoundedRegion(equalities, inequalities, inequalitySigns, vars, engine)) {
          return F.NIL;
        }
        return F.list(simplifyCriticalValue(bestValue, engine), F.CEmptyList);
      }
      for (IExpr familyValue : familyValues) {
        // a family may have no point in the region: it is harmless only if its value is not
        // better than the one of a point which is in the region
        int order = compareValues(familyValue, bestValue, engine);
        if (order == Integer.MIN_VALUE || (isMax ? order > 0 : order < 0)) {
          return F.NIL;
        }
      }
      IAST bestRules = candidateRules.get(best);
      // The best stationary point is the extremum only if there is one. A continuous objective
      // has one on a closed and bounded region; otherwise the bound itself is proved.
      boolean closed = !strict.contains(Boolean.TRUE);
      if (!closed && !isStrictlyFeasible(bestRules, inequalities, inequalitySigns, strict, engine)) {
        // the extremum over the closure lies on an excluded boundary: it is not attained
        return F.NIL;
      }
      if (!isPolynomialProblem(objective, equalities, inequalities, varList)
          || !(isBoundedRegion(equalities, inequalities, inequalitySigns, vars, engine)
              || isCoercive(objective, vars, isMax, engine))) {
        if (!isBoundProved(objective, constraint, varList, bestValue, isMax, engine)) {
          return F.NIL;
        }
      }
      IASTAppendable simplifiedRules = F.ListAlloc(bestRules.argSize());
      for (int i = 1; i <= bestRules.argSize(); i++) {
        IAST rule = (IAST) bestRules.get(i);
        simplifiedRules.append(F.Rule(rule.first(), simplifyCriticalValue(rule.second(), engine)));
      }
      return engine.evaluate(F.list(simplifyCriticalValue(bestValue, engine), simplifiedRules));
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
      List<IExpr> inequalities, List<Integer> inequalitySigns, List<Boolean> strict) {
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
        strict.add(c.isAST(S.Less) || c.isAST(S.Greater));
      }
    }
    return true;
  }

  /** The point satisfies every strict inequality strictly. */
  private static boolean isStrictlyFeasible(IAST ordered, List<IExpr> inequalities,
      List<Integer> inequalitySigns, List<Boolean> strict, EvalEngine engine) {
    for (int i = 0; i < inequalities.size(); i++) {
      if (!strict.get(i)) {
        continue;
      }
      IExpr v = engine.evaluate(applyRules(inequalities.get(i), ordered));
      int sign = signAtPoint(v, engine);
      if (sign == Integer.MIN_VALUE || sign == 0 || sign != inequalitySigns.get(i)) {
        return false;
      }
    }
    return true;
  }

  /** Objective and constraints are polynomials in the variables. */
  private static boolean isPolynomialProblem(IExpr objective, List<IExpr> equalities,
      List<IExpr> inequalities, IAST varList) {
    if (!objective.isPolynomial(varList)) {
      return false;
    }
    for (IExpr g : equalities) {
      if (!g.isPolynomial(varList)) {
        return false;
      }
    }
    for (IExpr g : inequalities) {
      if (!g.isPolynomial(varList)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test if the region of polynomial constraints is bounded, by rules which are sufficient but
   * not necessary:
   * <ul>
   * <li>a linear constraint bounds a variable from one side if the other variables in it are
   * bounded from the sides which matter (a box, a simplex);</li>
   * <li>a constraint <code>g &lt;= 0</code> or <code>g == 0</code> whose terms of the highest
   * degree are a positive definite form in all of its variables bounds these variables (a
   * ball, an ellipsoid, <code>x^4+y^4 &lt;= 1</code>);</li>
   * <li>an equation <code>c*v^q + r == 0</code> with a constant <code>c</code> bounds
   * <code>v</code> if the variables of <code>r</code> are bounded.</li>
   * </ul>
   */
  private static boolean isBoundedRegion(List<IExpr> equalities, List<IExpr> inequalities,
      List<Integer> inequalitySigns, IExpr[] vars, EvalEngine engine) {
    final int n = vars.length;
    boolean[] lower = new boolean[n];
    boolean[] upper = new boolean[n];
    // all constraints as g <= 0
    List<IExpr> atMostZero = new ArrayList<>();
    for (IExpr g : equalities) {
      atMostZero.add(g);
      atMostZero.add(engine.evaluate(F.Negate(g)));
    }
    for (int i = 0; i < inequalities.size(); i++) {
      IExpr g = inequalities.get(i);
      atMostZero.add(inequalitySigns.get(i) < 0 ? g : engine.evaluate(F.Negate(g)));
    }
    for (IExpr g : atMostZero) {
      boundByDefiniteForm(g, vars, lower, upper, engine);
    }
    List<IExpr[]> linear = new ArrayList<>();
    for (IExpr g : atMostZero) {
      IExpr[] c = linearCoefficients(g, vars, engine);
      if (c != null) {
        linear.add(c);
      }
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (IExpr[] c : linear) {
        for (int i = 0; i < n; i++) {
          int sign = constantSign(c[i]);
          if (sign == 0 || (sign > 0 ? upper[i] : lower[i])) {
            continue;
          }
          boolean bounded = true;
          for (int j = 0; j < n && bounded; j++) {
            if (j != i && !c[j].isZero()) {
              int sj = constantSign(c[j]);
              // the other terms must not run to -Infinity
              bounded = sj > 0 ? lower[j] : sj < 0 ? upper[j] : false;
            }
          }
          if (bounded) {
            if (sign > 0) {
              upper[i] = true;
            } else {
              lower[i] = true;
            }
            changed = true;
          }
        }
      }
      for (IExpr g : equalities) {
        for (int i = 0; i < n; i++) {
          if ((lower[i] && upper[i]) || g.isFree(vars[i]) || !g.isPolynomial(vars[i])) {
            continue;
          }
          IExpr degree = engine.evaluate(F.Exponent(g, vars[i]));
          IExpr lead = engine.evaluate(F.Coefficient(g, vars[i], degree));
          if (!degree.isInteger() || !degree.isPositive() || !lead.isNumber() || lead.isZero()) {
            continue;
          }
          IExpr rest = engine.evaluate(F.Expand(F.Subtract(g, F.Times(lead, F.Power(vars[i], degree)))));
          boolean bounded = rest.isFree(vars[i]);
          for (int j = 0; j < n && bounded; j++) {
            if (j != i && !rest.isFree(vars[j])) {
              bounded = lower[j] && upper[j];
            }
          }
          if (bounded) {
            lower[i] = true;
            upper[i] = true;
            changed = true;
          }
        }
      }
    }
    for (int i = 0; i < n; i++) {
      if (!lower[i] || !upper[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * The objective runs to <code>Infinity</code> (for a minimum) in every direction: the points
   * with a value up to the one of a candidate are a bounded set, on which the extremum exists.
   */
  private static boolean isCoercive(IExpr objective, IExpr[] vars, boolean isMax,
      EvalEngine engine) {
    boolean[] lower = new boolean[vars.length];
    boolean[] upper = new boolean[vars.length];
    boundByDefiniteForm(isMax ? engine.evaluate(F.Negate(objective)) : objective, vars, lower,
        upper, engine);
    for (int i = 0; i < vars.length; i++) {
      if (!lower[i] || !upper[i]) {
        return false;
      }
    }
    return true;
  }

  /** The sign of a number, or 0 if it is 0 or not a real number. */
  private static int constantSign(IExpr c) {
    return c.isPositive() ? 1 : c.isNegative() ? -1 : c.isPositiveResult() ? 1
        : c.isNegativeResult() ? -1 : 0;
  }

  /**
   * <code>g &lt;= 0</code> with a positive definite form of the highest degree in all variables
   * of <code>g</code>: these variables are bounded.
   */
  private static void boundByDefiniteForm(IExpr g, IExpr[] vars, boolean[] lower,
      boolean[] upper, EvalEngine engine) {
    List<Integer> own = new ArrayList<>();
    IASTAppendable ownVars = F.ListAlloc(vars.length);
    for (int i = 0; i < vars.length; i++) {
      if (!g.isFree(vars[i])) {
        own.add(i);
        ownVars.append(vars[i]);
      }
    }
    if (own.isEmpty() || !g.isPolynomial(ownVars)) {
      return;
    }
    // the terms of the highest total degree: scale every variable by t
    ISymbol t = F.Dummy("t");
    IExpr scaled = g;
    for (int i : own) {
      scaled = F.xreplace(scaled, vars[i], F.Times(t, vars[i]));
    }
    scaled = engine.evaluate(F.Expand(scaled));
    int degree = engine.evaluate(F.Exponent(scaled, t)).toIntDefault();
    if (degree < 2 || (degree & 1) == 1) {
      return;
    }
    IExpr form = engine.evaluate(F.Expand(F.Coefficient(scaled, t, F.ZZ(degree))));
    final int k = own.size();
    boolean definite;
    if (degree == 2) {
      IExpr[][] matrix = new IExpr[k][k];
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < k; j++) {
          matrix[i][j] = engine.evaluate(
              F.Times(F.C1D2, F.D(F.D(form, vars[own.get(i)]), vars[own.get(j)])));
        }
      }
      definite = isPositiveDefinite(matrix, k, engine);
    } else {
      // a sum of positive multiples of the pure powers of all variables
      IExpr rest = form;
      definite = true;
      for (int i : own) {
        IExpr c = engine.evaluate(F.Coefficient(form, vars[i], F.ZZ(degree)));
        definite &= c.isNumber() && c.isPositive();
        rest = F.Subtract(rest, F.Times(c, F.Power(vars[i], F.ZZ(degree))));
      }
      definite &= engine.evaluate(F.Expand(rest)).isZero();
    }
    if (definite) {
      for (int i : own) {
        lower[i] = true;
        upper[i] = true;
      }
    }
  }

  /** The time the proof of a bound over an unbounded region may take. */
  private static final long BOUND_PROOF_MILLIS = 10_000L;

  /**
   * Prove that no point of the region has a value beyond <code>bound</code>: the statement
   * <code>constraint &amp;&amp; f &lt; bound</code> (or <code>&gt;</code>) reduces to
   * <code>False</code> over the reals.
   */
  private static boolean isBoundProved(IExpr objective, IExpr constraint, IAST varList,
      IExpr bound, boolean isMax, EvalEngine engine) {
    IASTAppendable statement = F.ast(S.And);
    List<IExpr> conjuncts = new ArrayList<>();
    flattenConstraints(constraint, conjuncts);
    statement.appendAll(conjuncts);
    statement.append(isMax ? F.Greater(objective, bound) : F.Less(objective, bound));
    return isReducedToFalse(statement, varList, engine);
  }

  /**
   * <code>Reduce</code> of the statement over the reals is <code>False</code>, within a time
   * limit. The call is marked as one from an optimizer, so that <code>Reduce</code> does not
   * come back to the optimizers for it.
   */
  private static boolean isReducedToFalse(IExpr statement, IAST varList, EvalEngine engine) {
    final long millis =
        (long) (BOUND_PROOF_MILLIS * org.matheclipse.core.basic.MachineProfile.getScale());
    engine.incOptimizeExpressionDepth();
    try {
      IExpr reduced = org.matheclipse.core.integrate.IntegrateTimeBudget
          .runWithin(() -> engine.evalQuiet(F.Reduce(statement, varList, S.Reals)), millis);
      return reduced.isPresent() && reduced.isFalse();
    } finally {
      engine.decOptimizeExpressionDepth();
    }
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
  private static boolean collectCandidatesMulti(IExpr solution, IExpr[] vars, IExpr objective,
      List<IExpr> equalities, List<IExpr> inequalities, List<Integer> inequalitySigns,
      List<IExpr> values, List<IAST> rulesList, List<IExpr> familyValues, int mode,
      EvalEngine engine) {
    final boolean boundOnly = mode == MODE_BOUND;
    final boolean anyPoint = mode == MODE_ANY_POINT;
    if (solution.isEmptyList()) {
      return true;
    }
    if (!solution.isListOfLists() || !solution.isFree(S.ConditionalExpression, true)) {
      // for a point of the region the other active sets are still worth a look
      return anyPoint;
    }
    IAST solutions = (IAST) solution;
    for (int s = 1; s < solutions.size(); s++) {
      IAST solutionRules = (IAST) solutions.get(s);
      IAST ordered = orderedVarRules(solutionRules, vars);
      boolean determined = ordered.isPresent();
      // every variable must be determined to a value free of the variables
      for (int i = 1; determined && i < ordered.size(); i++) {
        determined = isFreeOfAll(((IAST) ordered.get(i)).second(), vars);
      }
      if (!determined) {
        if (anyPoint) {
          continue;
        }
        // a family of points: its value counts if it is the same for all of them
        IExpr familyValue = engine.evaluate(F.ReplaceAll(objective, solutionRules));
        if (!isFreeOfAll(familyValue, vars) || !isRealValue(familyValue, engine)) {
          return false;
        }
        if (boundOnly) {
          values.add(familyValue);
          rulesList.add(solutionRules);
        } else {
          familyValues.add(familyValue);
        }
        continue;
      }
      int feasible =
          feasibility(ordered, equalities, inequalities, inequalitySigns, engine);
      if (feasible == 0 && !boundOnly) {
        if (anyPoint) {
          continue;
        }
        return false;
      }
      if (feasible < 0) {
        continue;
      }
      IExpr functionValue = engine.evaluate(applyRules(objective, ordered));
      if (!isFreeOfAll(functionValue, vars) || !isRealValue(functionValue, engine)) {
        if (anyPoint) {
          continue;
        }
        return false;
      }
      values.add(functionValue);
      rulesList.add(ordered);
    }
    return true;
  }

  /**
   * The sign of a real value at a candidate point. A value which is 0 is recognized exactly: on
   * an active constraint the numeric value is rounding noise of either sign.
   *
   * @return <code>-1</code>, <code>0</code>, <code>1</code> or {@link Integer#MIN_VALUE} if the
   *         sign is not decided
   */
  private static int signAtPoint(IExpr value, EvalEngine engine) {
    if (value.isZero()) {
      return 0;
    }
    if (value.isReal()) {
      return value.isNegative() ? -1 : 1;
    }
    if (engine.evalQuiet(F.Simplify(value)).isZero()) {
      return 0;
    }
    // with 60 digits a value which is not 0 shows its sign; the machine value of a constraint
    // which holds with equality is rounding noise
    IExpr precise = engine.evalQuiet(F.N(value, F.ZZ(60)));
    double d = precise.evalfNaN();
    if (Double.isNaN(d) || Double.isInfinite(d)) {
      return Integer.MIN_VALUE;
    }
    if (Math.abs(d) > 1.0e-30) {
      return d < 0.0 ? -1 : 1;
    }
    // 0 to 30 digits, which the simplifier did not prove
    return engine.evalQuiet(F.PossibleZeroQ(value)).isTrue() ? 0 : Integer.MIN_VALUE;
  }

  /**
   * Test whether the point satisfies every constraint.
   *
   * @return 1 if it does, -1 if a constraint is violated, 0 if a constraint is not decided
   */
  private static int feasibility(IAST ordered, List<IExpr> equalities, List<IExpr> inequalities,
      List<Integer> inequalitySigns, EvalEngine engine) {
    for (IExpr g : equalities) {
      IExpr v = engine.evaluate(applyRules(g, ordered));
      int sign = signAtPoint(v, engine);
      if (sign == Integer.MIN_VALUE) {
        return 0;
      }
      if (sign != 0) {
        return -1;
      }
    }
    for (int i = 0; i < inequalities.size(); i++) {
      IExpr v = engine.evaluate(applyRules(inequalities.get(i), ordered));
      int sign = signAtPoint(v, engine);
      if (sign == Integer.MIN_VALUE) {
        return 0;
      }
      if (sign != 0 && sign != inequalitySigns.get(i)) {
        return -1;
      }
    }
    return 1;
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
      List<IExpr> conjuncts =
          constraint.isAST(S.And) ? ((IAST) constraint).asArgsList() : List.of(constraint);

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
      // not a polynomial of this ring: the other methods are tried
      return F.NIL;
    }
  }

  /**
   * The global extremum of a univariate function which is a quotient <code>num/den</code> of a
   * pole free numerator and a polynomial denominator, e.g. a rational function or
   * <code>E^x/(x^2-1)</code>. Unlike a pure critical point search this takes the real poles and
   * the limits at <code>+/-Infinity</code> into account:
   * <ul>
   * <li>if the function tends to <code>-Infinity</code> (minimum) at a real pole, the result is
   * <code>{-Infinity, {x -&gt; pole}}</code> for the smallest such pole,</li>
   * <li>if a limit at <code>+/-Infinity</code> is smaller than every critical value, the extremum
   * isn't attained and the result is <code>{limit, {x -&gt; -Infinity}}</code> (or
   * <code>Infinity</code>),</li>
   * <li>otherwise the smallest critical value with the smallest critical point.</li>
   * </ul>
   * The message <code>natt</code> is printed if the extremum is not attained (like WMA).
   *
   * @param head {@code Maximize} or {@code Minimize}, used for messages
   * @param function the objective
   * @param x the variable
   * @param isMax <code>true</code> for the maximum
   * @param engine the evaluation engine
   * @return the extremum or {@link F#NIL} if the function isn't of this form or the extremum
   *         couldn't be determined
   */
  public static IExpr quotientExtremum(ISymbol head, IExpr function, IExpr x, boolean isMax,
      EvalEngine engine) {
    if (!isPoleAwareQuotient(function, x, engine)) {
      return F.NIL;
    }
    IAST domain = quotientDomain(function, x, engine);
    if (domain == null) {
      return F.NIL;
    }
    if (!isRealLine(domain)) {
      // e.g. `Sqrt(x)/(x^2+1)` is only real for x>=0; a closed end of the domain is attained
      return quotientConstrainedExtremum(head, function, x, domain, isMax, engine);
    }
    QuotientExtremum extremum =
        quotientExtremumOn(function, x, F.CNInfinity, F.CInfinity, isMax, engine);
    if (extremum == null) {
      return F.NIL;
    }
    boolean unboundedAtInfinity = extremum.unbounded
        && (extremum.point.isInfinity() || extremum.point.isNegativeInfinity());
    if (!extremum.attained && !unboundedAtInfinity) {
      Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
    }
    return F.list(extremum.value, F.list(F.Rule(x, extremum.point)));
  }

  /**
   * The extremum of a function for which {@link #isPoleAwareQuotient(IExpr, IExpr, EvalEngine)}
   * holds over a feasible region given as {@link F#IntervalData}. The interior of every interval
   * is decided by {@link #quotientExtremumOn(IExpr, IExpr, IExpr, IExpr, boolean, EvalEngine)},
   * its closed finite ends which aren't poles are attained candidates. An unbounded interval
   * (e.g. <code>-Infinity</code> at a pole inside the region) decides the result; the first one in
   * ascending order is taken. An attained value wins a tie against a limit.
   *
   * @return <code>{value, {x -&gt; point}}</code> or {@link F#NIL} if it couldn't be determined
   */
  private static IExpr quotientConstrainedExtremum(ISymbol head, IExpr objective, IExpr x,
      IAST intervalData, boolean isMax, EvalEngine engine) {
    List<IExpr> poles = quotientPoles(objective, x, engine);
    if (poles == null) {
      return F.NIL;
    }
    IExpr bestValue = F.NIL;
    IExpr bestPoint = F.NIL;
    boolean bestAttained = false;
    for (int i = 1; i < intervalData.size(); i++) {
      IExpr entry = intervalData.get(i);
      if (!entry.isList() || ((IAST) entry).argSize() != 4) {
        return F.NIL;
      }
      IAST interval = (IAST) entry;
      IExpr lo = interval.arg1();
      IExpr hi = interval.arg4();
      if (!(lo.isNegativeInfinity() || lo.isNumericFunction(true))
          || !(hi.isInfinity() || hi.isNumericFunction(true))) {
        // a bound with a parameter
        return F.NIL;
      }
      List<QuotientExtremum> candidates = new ArrayList<QuotientExtremum>();
      if (lo.equals(hi)) {
        // a single point
        if (!isPole(lo, poles)) {
          IExpr value = engine.evaluate(F.xreplace(objective, x, lo));
          candidates.add(new QuotientExtremum(value, lo, true, false));
        }
      } else {
        QuotientExtremum inner = quotientExtremumOn(objective, x, lo, hi, isMax, engine);
        if (inner == null) {
          return F.NIL;
        }
        if (inner.unbounded) {
          boolean atInfinity = inner.point.isInfinity() || inner.point.isNegativeInfinity();
          if (!atInfinity) {
            Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
          }
          return F.list(inner.value, F.list(F.Rule(x, inner.point)));
        }
        candidates.add(inner);
        for (int j = 0; j < 2; j++) {
          IExpr end = j == 0 ? lo : hi;
          boolean closed = (j == 0 ? interval.arg2() : interval.arg3()) == S.LessEqual;
          if (closed && !end.isInfinity() && !end.isNegativeInfinity() && !isPole(end, poles)) {
            IExpr value = engine.evaluate(F.xreplace(objective, x, end));
            candidates.add(new QuotientExtremum(value, end, true, false));
          }
        }
      }
      for (QuotientExtremum candidate : candidates) {
        if (!isRealValue(candidate.value, engine)) {
          return F.NIL;
        }
        if (bestValue.isNIL()) {
          bestValue = candidate.value;
          bestPoint = candidate.point;
          bestAttained = candidate.attained;
          continue;
        }
        int compare = compareValues(candidate.value, bestValue, engine);
        if (compare == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (isMax) {
          compare = -compare;
        }
        if (compare < 0) {
          bestValue = candidate.value;
          bestPoint = candidate.point;
          bestAttained = candidate.attained;
        } else if (compare == 0 && candidate.attained && !bestAttained) {
          bestPoint = candidate.point;
          bestAttained = true;
        }
      }
    }
    if (bestValue.isNIL()) {
      return F.NIL;
    }
    if (!bestAttained) {
      Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"), engine);
    }
    return F.list(bestValue, F.list(F.Rule(x, bestPoint)));
  }

  /** Test whether the numeric value of <code>point</code> is one of the <code>poles</code>. */
  private static boolean isPole(IExpr point, List<IExpr> poles) {
    for (IExpr pole : poles) {
      if (pole.equals(point)) {
        return true;
      }
      try {
        double p = pole.evalDouble();
        double d = point.evalDouble();
        if (Math.abs(p - d) <= Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, Math.abs(p))) {
          return true;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    return false;
  }

  /** The extremum of a function on an open interval. */
  static final class QuotientExtremum {
    /** the extremal value, or the infimum/supremum if it isn't attained */
    final IExpr value;
    /** the point where the value is attained, or approached */
    final IExpr point;
    /** <code>true</code> if the value is attained at <code>point</code> */
    final boolean attained;
    /** <code>true</code> if the value is <code>+/-Infinity</code> */
    final boolean unbounded;

    QuotientExtremum(IExpr value, IExpr point, boolean attained, boolean unbounded) {
      this.value = value;
      this.point = point;
      this.attained = attained;
      this.unbounded = unbounded;
    }
  }

  /**
   * Test whether <code>function</code> is a quotient <code>num/den</code> of a pole free numerator
   * and a polynomial denominator in <code>x</code>, which isn't a polynomial itself.
   */
  static boolean isPoleAwareQuotient(IExpr function, IExpr x, EvalEngine engine) {
    if (!function.isAST() || function.isPolynomial(x) || !x.isSymbol()
        || !function.isFree(t -> t.isInexactNumber(), true)) {
      return false;
    }
    IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) function, true, engine);
    IExpr denominator = parts[1];
    IExpr numerator = denominator.isOne() ? parts[2] : parts[0];
    // a numerator like `Sqrt(x)` which isn't real for every x restricts the domain, see
    // quotientDomain()
    return denominator.isPolynomial(x) && !Reduce.hasPole(numerator, x);
  }

  /**
   * The real domain of a function for which
   * {@link #isPoleAwareQuotient(IExpr, IExpr, EvalEngine)} holds, without its poles: all
   * {@link S#Reals} if the numerator is real for every real <code>x</code>, otherwise the
   * {@link S#FunctionDomain} as {@link F#IntervalData}, e.g. <code>0&lt;=x&lt;1||x&gt;1</code> for
   * <code>Sqrt(x)/(x-1)</code>.
   *
   * @return the domain or <code>null</code> if it couldn't be determined as intervals with
   *         numeric bounds
   */
  static IAST quotientDomain(IExpr function, IExpr x, EvalEngine engine) {
    IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) function, true, engine);
    IExpr numerator = parts[1].isOne() ? parts[2] : parts[0];
    if (Reduce.isTotalRealFunction(numerator, x)) {
      return F.CRealsIntervalData;
    }
    IExpr domain = engine.evalQuiet(F.FunctionDomain(function, x));
    if (domain.isAST(S.FunctionDomain) || domain.isFalse()) {
      return null;
    }
    IAST intervalData = IntervalDataSym.toIntervalData(domain, x, engine, true);
    if (intervalData == null || intervalData.isNIL() || !intervalData.isIntervalData()) {
      return null;
    }
    for (int i = 1; i < intervalData.size(); i++) {
      IExpr entry = intervalData.get(i);
      if (!entry.isList() || ((IAST) entry).argSize() != 4) {
        return null;
      }
      IExpr lo = entry.first();
      IExpr hi = ((IAST) entry).arg4();
      if (!(lo.isNegativeInfinity() || lo.isNumericFunction(true))
          || !(hi.isInfinity() || hi.isNumericFunction(true))) {
        return null;
      }
    }
    return intervalData;
  }

  /** Test whether the interval data is the whole real line. */
  static boolean isRealLine(IAST intervalData) {
    if (intervalData.argSize() != 1 || !intervalData.arg1().isList()) {
      return false;
    }
    IAST only = (IAST) intervalData.arg1();
    return only.argSize() == 4 && only.arg1().isNegativeInfinity() && only.arg4().isInfinity();
  }

  /**
   * The real poles of a function for which {@link #isPoleAwareQuotient(IExpr, IExpr, EvalEngine)}
   * holds, sorted ascending, or <code>null</code> if they couldn't be determined.
   */
  static List<IExpr> quotientPoles(IExpr function, IExpr x, EvalEngine engine) {
    IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) function, true, engine);
    return realSolutions(parts[1], x, engine);
  }

  /**
   * The extremum of a function for which {@link #isPoleAwareQuotient(IExpr, IExpr, EvalEngine)}
   * holds on the open interval <code>lower &lt; x &lt; upper</code>. The ends may be
   * <code>-Infinity</code> / <code>Infinity</code> or poles of the function.
   *
   * @return the extremum or <code>null</code> if it couldn't be determined
   */
  static QuotientExtremum quotientExtremumOn(IExpr function, IExpr x, IExpr lower, IExpr upper,
      boolean isMax, EvalEngine engine) {
    // minimize s*f
    final IExpr s = isMax ? F.CN1 : F.C1;
    final IExpr g = engine.evaluate(F.Times(s, function));
    final IExpr worst = isMax ? F.CInfinity : F.CNInfinity;
    final double lowerValue = lower.isNegativeInfinity() ? Double.NEGATIVE_INFINITY //
        : lower.evalDouble();
    final double upperValue = upper.isInfinity() ? Double.POSITIVE_INFINITY //
        : upper.evalDouble();

    // an interior pole where the function tends to -Infinity
    List<IExpr> poles = quotientPoles(function, x, engine);
    if (poles == null) {
      return null;
    }
    for (IExpr pole : poles) {
      double d = pole.evalDouble();
      if (d <= lowerValue || d >= upperValue) {
        continue;
      }
      for (String direction : new String[] {"FromBelow", "FromAbove"}) {
        if (oneSidedLimit(g, x, pole, direction, engine).isNegativeInfinity()) {
          return new QuotientExtremum(worst, pole, false, true);
        }
      }
    }

    // the limits at the ends of the interval
    IExpr lowerLimit = lower.isNegativeInfinity() //
        ? engine.evalQuiet(F.Limit(g, F.Rule(x, F.CNInfinity))) //
        : oneSidedLimit(g, x, lower, "FromAbove", engine);
    IExpr upperLimit = upper.isInfinity() //
        ? engine.evalQuiet(F.Limit(g, F.Rule(x, F.CInfinity))) //
        : oneSidedLimit(g, x, upper, "FromBelow", engine);
    if (lowerLimit.isNegativeInfinity()) {
      return new QuotientExtremum(worst, lower, false, true);
    }
    if (upperLimit.isNegativeInfinity()) {
      return new QuotientExtremum(worst, upper, false, true);
    }

    // the critical points inside the interval, in ascending order
    IExpr derivative = engine.evaluate(F.Together(F.D(g, x)));
    IExpr derivativeNumerator = derivative;
    if (derivative.isAST()) {
      IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) derivative, true, engine);
      derivativeNumerator = parts[1].isOne() ? parts[2] : parts[0];
    }
    List<IExpr> critical = realSolutions(derivativeNumerator, x, engine);
    if (critical == null) {
      // e.g. `ArcSin(x)/(x-2)`: the critical points can't be solved, but on a bounded interval the
      // derivative may be proven to keep its sign - then the extrema lie at the ends
      if (lower.isNegativeInfinity() || upper.isInfinity()) {
        return null;
      }
      if (derivativeSign(engine.evaluate(F.D(g, x)), x, lower, upper, engine) != 0) {
        critical = new ArrayList<IExpr>();
      } else {
        // e.g. `ArcCos(x)/(x-1/2)`: locate the critical points numerically as
        // `Root({eq&, c})`
        critical = numericCriticalPoints(derivativeNumerator, x, lowerValue, upperValue, engine);
        if (critical == null) {
          return null;
        }
      }
    }
    IExpr bestValue = F.NIL;
    IExpr bestPoint = F.NIL;
    for (IExpr point : critical) {
      double d = engine.evalQuiet(F.N(point)).evalfNaN();
      if (Double.isNaN(d) || d <= lowerValue || d >= upperValue) {
        continue;
      }
      IExpr value = engine.evaluate(F.xreplace(g, x, point));
      if (!isRealValue(value, engine)) {
        continue;
      }
      if (bestValue.isNIL()) {
        bestValue = value;
        bestPoint = point;
        continue;
      }
      int compare = compareValues(value, bestValue, engine);
      if (compare == Integer.MIN_VALUE) {
        return null;
      }
      if (compare < 0) {
        bestValue = value;
        bestPoint = point;
      }
    }

    // the smaller one of the limits at the ends, which is never attained
    IExpr limit = F.NIL;
    IExpr limitPoint = F.NIL;
    for (int i = 0; i < 2; i++) {
      IExpr value = i == 0 ? lowerLimit : upperLimit;
      if (value.isInfinity()) {
        continue;
      }
      if (!value.isRealResult() || value.isDirectedInfinity() || value.isComplexInfinity()) {
        return null;
      }
      if (limit.isNIL() || S.Less.ofQ(engine, value, limit)) {
        limit = value;
        limitPoint = i == 0 ? lower : upper;
      }
    }
    if (bestValue.isPresent()) {
      int compare = limit.isNIL() ? -1 : compareValues(bestValue, limit, engine);
      if (compare == Integer.MIN_VALUE) {
        return null;
      }
      if (compare <= 0) {
        IExpr value = simplifyCriticalValue(engine.evaluate(F.Times(s, bestValue)), engine);
        return new QuotientExtremum(value, simplifyCriticalValue(bestPoint, engine), true, false);
      }
    }
    if (limit.isNIL()) {
      // both limits are +Infinity and there's no critical point
      return null;
    }
    return new QuotientExtremum(engine.evaluate(F.Times(s, limit)), limitPoint, false, false);
  }

  /**
   * Simplify the value of the objective (or the point) at a critical point, e.g.
   * <code>(-1+Sqrt(3)+(2-Sqrt(3))^2)/(-1+(2-Sqrt(3))^2)</code> to <code>-Sqrt(3)/2</code>. The
   * simplified form is only taken if it isn't larger.
   */
  private static IExpr simplifyCriticalValue(IExpr value, EvalEngine engine) {
    if (value.isNumber() || !value.isAST() || !value.isFree(S.Root, true)) {
      return value;
    }
    IExpr simplified = engine.evalQuiet(F.Simplify(value));
    if (simplified.isPresent() && simplified.leafCount() <= value.leafCount()) {
      return simplified;
    }
    return value;
  }

  /**
   * Compare two real values: exactly by the sign of their difference (<code>Less(-2/15*Pi,
   * -2/15*Pi)</code> isn't decided), otherwise numerically, e.g. for values at a numeric
   * <code>Root({eq&amp;, c})</code>.
   *
   * @return <code>-1</code>, <code>0</code>, <code>1</code> or {@link Integer#MIN_VALUE} if the
   *         values can't be compared
   */
  private static int compareValues(IExpr a, IExpr b, EvalEngine engine) {
    IExpr difference = engine.evaluate(F.Subtract(a, b));
    if (difference.isZero()) {
      return 0;
    }
    if (difference.isNegativeResult()) {
      return -1;
    }
    if (difference.isPositiveResult()) {
      return 1;
    }
    double d = engine.evalQuiet(F.N(difference)).evalfNaN();
    double scale = Math.abs(engine.evalQuiet(F.N(a)).evalfNaN());
    if (Double.isNaN(d) || Double.isInfinite(d) || Double.isNaN(scale)) {
      return Integer.MIN_VALUE;
    }
    if (Math.abs(d) <= Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, scale)) {
      return 0;
    }
    return d < 0.0 ? -1 : 1;
  }

  /** Test whether the value is real: exactly or numerically, e.g. with a numeric root inside. */
  private static boolean isRealValue(IExpr value, EvalEngine engine) {
    if (value.isDirectedInfinity() || value.isComplexInfinity() || value.isIndeterminate()) {
      return false;
    }
    if (value.isRealResult()) {
      return true;
    }
    if (!value.isFree(S.Root, true)) {
      IExpr numeric = engine.evalQuiet(F.N(value));
      if (numeric.isReal()) {
        double d = numeric.evalfNaN();
        return !Double.isNaN(d) && !Double.isInfinite(d);
      }
    }
    return false;
  }

  /** The number of samples of the numeric critical point search. */
  private static final int CRITICAL_POINT_SAMPLES = 400;

  /**
   * Locate the real roots of <code>eq</code> inside the open interval <code>(lower, upper)</code>
   * numerically by the sign changes between equidistant samples, refined by bisection. Every root
   * is returned as <code>Root({eq&amp;, c})</code>.
   *
   * @return the roots sorted ascending, or <code>null</code> if <code>eq</code> can't be evaluated
   *         numerically
   */
  private static List<IExpr> numericCriticalPoints(IExpr eq, IExpr x, double lower,
      double upper, EvalEngine engine) {
    IAST pureFunction = F.Function(F.subst(eq, x, F.Slot1));
    List<IExpr> roots = new ArrayList<IExpr>();
    double width = (upper - lower) / CRITICAL_POINT_SAMPLES;
    double previousX = lower + 0.5 * width;
    double previous = numericValue(eq, x, previousX, engine);
    if (Double.isNaN(previous)) {
      return null;
    }
    for (int k = 1; k < CRITICAL_POINT_SAMPLES; k++) {
      double currentX = lower + (k + 0.5) * width;
      double current = numericValue(eq, x, currentX, engine);
      if (Double.isNaN(current)) {
        return null;
      }
      if (current == 0.0 || previous * current < 0.0) {
        double a = previousX;
        double b = currentX;
        double fa = previous;
        for (int i = 0; i < 100 && current != 0.0; i++) {
          double m = 0.5 * (a + b);
          double fm = numericValue(eq, x, m, engine);
          if (Double.isNaN(fm)) {
            return null;
          }
          if (fm == 0.0) {
            a = m;
            b = m;
            break;
          }
          if (fa * fm < 0.0) {
            b = m;
          } else {
            a = m;
            fa = fm;
          }
        }
        double c = current == 0.0 ? currentX : 0.5 * (a + b);
        roots.add(F.Root(F.list(pureFunction, F.num(c))));
      }
      previousX = currentX;
      previous = current;
    }
    return roots;
  }

  private static double numericValue(IExpr expr, IExpr x, double value, EvalEngine engine) {
    try {
      double d = engine.evalQuiet(F.N(F.subst(expr, x, F.num(value)))).evalfNaN();
      return Double.isInfinite(d) ? Double.NaN : d;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
  }

  /** The number of subintervals for the interval arithmetic sign test of a derivative. */
  private static final int SIGN_TEST_SUBINTERVALS = 64;

  /**
   * Prove with interval arithmetic that <code>derivative</code> keeps its sign on the bounded
   * interval <code>[lower, upper]</code>: the interval is split into subintervals, the derivative
   * is evaluated on each of them, and every enclosure must lie in <code>[0, Infinity]</code> (or
   * every one in <code>[-Infinity, 0]</code>). Enclosures with an infinite end, e.g. from
   * <code>1/Sqrt(1-x^2)</code> at the end <code>x==1</code>, are allowed.
   *
   * @return <code>1</code> if the derivative is non negative, <code>-1</code> if it is non positive,
   *         <code>0</code> if its sign couldn't be proven
   */
  private static int derivativeSign(IExpr derivative, IExpr x, IExpr lower, IExpr upper,
      EvalEngine engine) {
    if (derivative.isAST(S.D)) {
      return 0;
    }
    IExpr step = engine.evaluate(F.Divide(F.Subtract(upper, lower), F.ZZ(SIGN_TEST_SUBINTERVALS)));
    int sign = 0;
    for (int k = 0; k < SIGN_TEST_SUBINTERVALS; k++) {
      IExpr a = engine.evaluate(F.Plus(lower, F.Times(F.ZZ(k), step)));
      IExpr b = k == SIGN_TEST_SUBINTERVALS - 1 ? upper
          : engine.evaluate(F.Plus(lower, F.Times(F.ZZ(k + 1), step)));
      IExpr enclosure =
          engine.evalQuiet(F.subst(derivative, x, F.Interval(F.List(a, b))));
      if (!enclosure.isAST(S.Interval) || enclosure.argSize() < 1) {
        return 0;
      }
      for (IExpr part : (IAST) enclosure) {
        if (!part.isList2()) {
          return 0;
        }
        double min;
        double max;
        try {
          min = part.first().isNegativeInfinity() ? Double.NEGATIVE_INFINITY
              : part.first().evalDouble();
          max = part.second().isInfinity() ? Double.POSITIVE_INFINITY
              : part.second().evalDouble();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return 0;
        }
        final int partSign = min >= 0.0 ? 1 : max <= 0.0 ? -1 : 0;
        if (partSign == 0 || (sign != 0 && partSign != sign)) {
          return 0;
        }
        sign = partSign;
      }
    }
    return sign;
  }

  private static IExpr oneSidedLimit(IExpr g, IExpr x, IExpr point, String direction,
      EvalEngine engine) {
    return engine.evalQuiet(
        F.Limit(g, F.Rule(x, point), F.Rule(S.Direction, F.stringx(direction))));
  }

  /**
   * The real solutions of <code>expr == 0</code> sorted ascending, or <code>null</code> if they
   * couldn't be determined.
   */
  private static List<IExpr> realSolutions(IExpr expr, IExpr x, EvalEngine engine) {
    List<IExpr> result = new ArrayList<>();
    if (expr.isFree(x)) {
      return expr.isZero() ? null : result;
    }
    IExpr solutions = engine.evalQuiet(F.Solve(F.Equal(expr, F.C0), x, S.Reals));
    if (!solutions.isList() || !solutions.isFree(S.Solve, true)
        || !solutions.isFree(S.ConditionalExpression, true)) {
      return null;
    }
    List<Double> values = new ArrayList<>();
    for (IExpr rules : (IAST) solutions) {
      if (!rules.isList1() || !rules.first().isRule() || !rules.first().first().equals(x)) {
        return null;
      }
      IExpr value = rules.first().second();
      double d;
      try {
        d = value.evalDouble();
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return null;
      }
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        return null;
      }
      int pos = 0;
      while (pos < values.size() && values.get(pos) < d) {
        pos++;
      }
      values.add(pos, d);
      result.add(pos, value);
    }
    return result;
  }

  /**
   * The global extremum of a function of one variable which is a polynomial in <code>x</code>
   * and in terms <code>Abs(g)</code> with polynomials <code>g</code>. Between the real roots of
   * the <code>g</code> the function is one polynomial, so the extremum is at such a root or at a
   * stationary point of a piece; the two outer pieces decide whether it is unbounded.
   *
   * @return {@link F#NIL} if the function is not of this form or a root is not found
   */
  static IExpr absoluteValueExtremum(IExpr function, IExpr x, boolean isMax, EvalEngine engine) {
    if (function.isFree(S.Abs, true) || !x.isSymbol()) {
      return F.NIL;
    }
    List<IExpr> arguments = new ArrayList<>();
    collectAbsArguments(function, arguments);
    List<IExpr> breakpoints = new ArrayList<>();
    List<Double> positions = new ArrayList<>();
    for (IExpr g : arguments) {
      if (!g.isPolynomial(x)) {
        return F.NIL;
      }
      List<IExpr> roots = realSolutions(g, x, engine);
      if (roots == null) {
        return F.NIL;
      }
      for (IExpr root : roots) {
        double d = root.evalDouble();
        int pos = 0;
        while (pos < positions.size() && positions.get(pos) < d - 1.0e-12) {
          pos++;
        }
        if (pos < positions.size() && Math.abs(positions.get(pos) - d) <= 1.0e-12) {
          continue;
        }
        positions.add(pos, d);
        breakpoints.add(pos, root);
      }
    }
    final int cells = positions.size() + 1;
    List<IExpr> candidates = new ArrayList<>(breakpoints);
    for (int cell = 0; cell < cells; cell++) {
      final double from = cell == 0 ? Double.NEGATIVE_INFINITY : positions.get(cell - 1);
      final double to = cell == cells - 1 ? Double.POSITIVE_INFINITY : positions.get(cell);
      final double sample = cells == 1 ? 0.0
          : cell == 0 ? to - 1.0 : cell == cells - 1 ? from + 1.0 : (from + to) / 2.0;
      final boolean[] signKnown = {true};
      IExpr piece = engine.evaluate(F.Expand(F.subst(function, t -> {
        if (t.isAbs()) {
          double v = numericValue(t.first(), x, sample, engine);
          signKnown[0] &= v < 0.0 || v > 0.0;
          return v < 0.0 ? F.Negate(t.first()) : t.first();
        }
        return F.NIL;
      })));
      if (!signKnown[0] || !piece.isPolynomial(x)) {
        return F.NIL;
      }
      IExpr derivative = engine.evaluate(F.D(piece, x));
      if (derivative.isZero()) {
        // a constant piece: attained at each of its points
        if (cells == 1 || cell == 0 || cell == cells - 1) {
          candidates.add(cell == 0 && cells > 1 ? breakpoints.get(0)
              : cells == 1 ? F.C0 : breakpoints.get(breakpoints.size() - 1));
        }
      } else {
        List<IExpr> stationary = realSolutions(derivative, x, engine);
        if (stationary == null) {
          return F.NIL;
        }
        for (IExpr point : stationary) {
          double d = point.evalDouble();
          if (d > from && d < to) {
            candidates.add(point);
          }
        }
        if (cell == 0 || cell == cells - 1) {
          for (int end = 0; end < 2; end++) {
            if ((end == 0 && cell != 0) || (end == 1 && cell != cells - 1)) {
              continue;
            }
            IExpr direction = end == 0 ? F.CNInfinity : F.CInfinity;
            IExpr limit = engine.evalQuiet(F.Limit(piece, F.Rule(x, direction)));
            if (isMax ? limit.isInfinity() : limit.isNegativeInfinity()) {
              return F.list(limit, F.list(F.Rule(x, direction)));
            }
            if (!(isMax ? limit.isNegativeInfinity() : limit.isInfinity())) {
              return F.NIL;
            }
          }
        }
      }
    }
    IExpr bestValue = F.NIL;
    IExpr bestPoint = F.NIL;
    double bestPosition = Double.NaN;
    for (int i = 0; i < candidates.size(); i++) {
      IExpr point = candidates.get(i);
      IExpr value = engine.evaluate(F.xreplace(function, x, point));
      if (!isRealValue(value, engine)) {
        return F.NIL;
      }
      int order = bestValue.isNIL() ? (isMax ? 1 : -1) : compareValues(value, bestValue, engine);
      if (order == Integer.MIN_VALUE) {
        return F.NIL;
      }
      double position = point.evalDouble();
      if ((isMax ? order > 0 : order < 0) || (order == 0 && position < bestPosition)) {
        bestValue = value;
        bestPoint = point;
        bestPosition = position;
      }
    }
    if (bestValue.isNIL()) {
      return F.NIL;
    }
    return F.list(simplifyCriticalValue(bestValue, engine), F.list(F.Rule(x, bestPoint)));
  }

  private static void collectAbsArguments(IExpr expr, List<IExpr> arguments) {
    if (expr.isAbs()) {
      if (!arguments.contains(expr.first())) {
        arguments.add(expr.first());
      }
      return;
    }
    if (expr.isAST()) {
      ((IAST) expr).forEach(arg -> collectAbsArguments(arg, arguments));
    }
  }

  public static IExpr maximize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    try {
      if (function.isFree(x) && function.isRealResult()) {
        // a constant: attained everywhere
        return F.list(function, F.list(F.Rule(x, F.C0)));
      }
      IExpr piecewise = absoluteValueExtremum(function, x, true, engine);
      if (piecewise.isPresent()) {
        return piecewise;
      }
      // bounded linear-trigonometric objective (a*Sin(x) + b*Cos(x) + c)
      IExpr trig = linearTrigExtremum(function, x, true, engine);
      if (trig.isPresent()) {
        return trig;
      }
      if (function.isPolynomial(x)) {
        // the objective of `x+1/x` must not be multiplied by `x`
        IExpr temp = maximizeExprPolynomial(function, F.list(x));
        if (temp.isPresent()) {
          return temp;
        }
      }
      IExpr quotient = quotientExtremum(head, function, x, true, engine);
      if (quotient.isPresent()) {
        return quotient;
      }

      IExpr yNInf = S.Limit.of(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isInfinity()) {
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.of(function, F.Rule(x, F.CInfinity));
      if (yInf.isInfinity()) {
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CInfinity)));
      }

      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.of(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr maxCandidate = F.NIL;
        IExpr maxValue = F.CNInfinity;
        IExpr maxSolution = F.NIL;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr solution = ((IAST) candidates).get(i).first().second();
            IExpr candidate = familyMember(function, x, solution, engine);
            if (candidate.isNIL()) {
              // a family of stationary points along which the function changes decides nothing
              return F.NIL;
            }
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isNegative()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (functionValue.greater(maxValue).isTrue()) {
                maxValue = functionValue;
                maxCandidate = candidate;
                maxSolution = solution;
              }
            }
          }
          if (maxCandidate.isPresent()) {
            if (!holdsOverPeriod(function, x, maxSolution, maxValue, true, engine)) {
              // only a local maximum: the function is larger elsewhere in the period
              return F.NIL;
            }
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
