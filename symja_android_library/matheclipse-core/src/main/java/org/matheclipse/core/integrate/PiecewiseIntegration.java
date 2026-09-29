package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Definite integrals of piecewise defined integrands: <code>Piecewise, UnitStep, HeavisideTheta,
 * Abs, RealAbs, Sign, RealSign, Max, Min</code> (and <code>Boole</code> next to one of them).
 *
 * <p>
 * The interval <code>(lower, upper)</code> is split at the break points of these functions - the
 * real zeros and poles of their arguments, of the differences of the arguments of
 * <code>Max, Min</code> and of the two sides of the comparisons in the conditions of
 * <code>Piecewise, Boole</code>. On every sub-interval each of these functions is replaced by its
 * active branch, which is decided at the midpoint: <code>Abs(u)</code> becomes <code>u</code> or
 * <code>-u</code>, <code>UnitStep(u)</code> becomes <code>1</code> or <code>0</code>, a
 * <code>Piecewise</code> its active piece. The pieces are integrated with <code>Integrate</code>
 * and summed:
 *
 * <pre>
 * Integrate(Abs(x)*Cos(3*x), {x,-Pi,Pi})
 *   == Integrate(-x*Cos(3*x), {x,-Pi,0}) + Integrate(x*Cos(3*x), {x,0,Pi}) == -4/9
 * </pre>
 *
 * <p>
 * The limits have to be numeric, possibly infinite. One break point <code>p</code> may be
 * symbolic, if it's linear in a single parameter <code>a</code> and comes from an argument which
 * is linear in <code>x</code>. Then the integral is computed for every position of <code>p</code>
 * relative to the numeric break points and the values are combined with <code>UnitStep</code>
 * like WMA does:
 *
 * <pre>
 * Integrate(UnitStep(x-a)*x, {x,0,1}) == 1/2*UnitStep(1-a)*(1-a^2+a^2*UnitStep(-a))
 * </pre>
 *
 * If the assumptions decide the position of <code>p</code>, only that case is returned. Several
 * symbolic break points are left to the other methods.
 */
public final class PiecewiseIntegration {

  /** More break points than this are left to the other methods. */
  private static final int MAX_BREAK_POINTS = 64;

  /** Two break points closer than this (relative) are the same point. */
  private static final double EPSILON = 1.0e-12;

  /** The symbolic break point found while collecting the break points. */
  private static final class SymbolicPoint {
    /** The break point, linear in {@link #parameter}. */
    IExpr point = F.NIL;
    IExpr parameter = F.NIL;
  }

  private PiecewiseIntegration() {}

  /**
   * <code>Integrate(f, {x, lower, upper})</code> for a piecewise defined <code>f</code>.
   *
   * @return {@link F#NIL} if <code>f</code> isn't piecewise defined in <code>x</code>, a limit
   *         isn't numeric, the break points can't be determined or one of the pieces can't be
   *         integrated
   */
  public static IExpr integrate(IExpr f, IExpr x, IExpr lower, IExpr upper, EvalEngine engine) {
    if (!x.isSymbol() || !isPiecewiseIn(f, x)) {
      return F.NIL;
    }
    try {
      final double low = limitValue(lower);
      final double up = limitValue(upper);
      if (Double.isNaN(low) || Double.isNaN(up) || low == up) {
        return F.NIL;
      }
      if (low > up) {
        // Integrate(f, {x, b, a}) == -Integrate(f, {x, a, b})
        IExpr reversed = integrate(f, x, upper, lower, engine);
        return reversed.isPresent() ? engine.evaluate(F.Negate(reversed)) : F.NIL;
      }
      TreeMap<Double, IExpr> points = new TreeMap<Double, IExpr>();
      points.put(low, lower);
      points.put(up, upper);
      SymbolicPoint symbolic = new SymbolicPoint();
      if (!collectBreakPoints(f, x, lower, upper, low, up, points, symbolic, engine)) {
        return F.NIL;
      }
      List<IExpr> nodes = new ArrayList<IExpr>(points.values());
      if (symbolic.point.isNIL()) {
        return integratePieces(f, x, nodes, nodes, F.CEmptyList, engine);
      }
      return integrateSymbolicPoint(f, x, nodes, symbolic, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * Integrate <code>f</code> piece by piece between the sorted <code>nodes</code>.
   *
   * @param nodes the sorted break points including the limits
   * @param representatives numeric values of the <code>nodes</code>, which decide the active
   *        branches (a symbolic node is represented by a numeric value in its case)
   * @param parameterRules the rule <code>{a -&gt; value}</code> for the parameter of a symbolic
   *        break point, or the empty list
   * @return {@link F#NIL} if a piece can't be integrated
   */
  private static IExpr integratePieces(IExpr f, IExpr x, List<IExpr> nodes,
      List<IExpr> representatives, IAST parameterRules, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(nodes.size());
    for (int i = 1; i < nodes.size(); i++) {
      IExpr piece = F.NIL;
      for (IExpr sample : samples(representatives.get(i - 1), representatives.get(i), engine)) {
        IASTAppendable rules = F.ListAlloc(parameterRules.argSize() + 1);
        rules.append(F.Rule(x, sample));
        rules.appendArgs(parameterRules);
        IExpr branch = rewrite(f, x, rules, engine);
        if (branch.isNIL()) {
          return F.NIL;
        }
        if (piece.isNIL()) {
          piece = branch;
        } else if (!piece.equals(branch)) {
          // the branch changes inside the sub-interval: a break point is missing
          return F.NIL;
        }
      }
      if (isPiecewiseIn(piece, x)) {
        // nothing was decided for one of the functions; integrating the piece would come back here
        return F.NIL;
      }
      // a piece which diverges is left to the other methods, which report it
      IExpr integral = engine.evalQuiet(F.Integrate(piece, F.list(x, nodes.get(i - 1), nodes.get(i))));
      if (!integral.isFreeAST(S.Integrate) || !integral.isSpecialsFree()) {
        return F.NIL;
      }
      sum.append(integral);
    }
    return engine.evaluate(sum.oneIdentity0());
  }

  /**
   * Integrate <code>f</code> with one symbolic break point <code>p</code> for every position of
   * <code>p</code> relative to the numeric break points <code>n0 < n1 < ... < nm</code> (the first
   * and last are the limits): <code>p &lt;= n0</code>, <code>n(i-1) &lt; p &lt;= n(i)</code> and
   * <code>p &gt; nm</code>. With the values <code>V0, V1,..., V(m+1)</code> of these cases the
   * integral is
   *
   * <pre>
   * V(m+1) + UnitStep(nm-p)*(V(m)-V(m+1) + UnitStep(n(m-1)-p)*(V(m-1)-V(m) + ...))
   * </pre>
   */
  private static IExpr integrateSymbolicPoint(IExpr f, IExpr x, List<IExpr> numericNodes,
      SymbolicPoint symbolic, EvalEngine engine) {
    final IExpr p = symbolic.point;
    final IExpr a = symbolic.parameter;
    // p == alpha*a + beta
    IExpr alpha = engine.evaluate(F.Coefficient(p, a));
    IExpr beta = engine.evaluate(F.Expand(F.Subtract(p, F.Times(alpha, a))));
    final int m = numericNodes.size() - 1;
    final IExpr lower = numericNodes.get(0);
    final IExpr upper = numericNodes.get(m);

    // the cases: the representative position of p and the right boundary of the case
    List<IExpr> representatives = new ArrayList<IExpr>();
    List<IExpr> rightBoundaries = new ArrayList<IExpr>();
    List<IExpr> leftBoundaries = new ArrayList<IExpr>();
    List<Integer> insertAt = new ArrayList<Integer>();
    List<IExpr> conditions = new ArrayList<IExpr>();
    if (!lower.isNegativeInfinity()) {
      representatives.add(engine.evaluate(F.Subtract(lower, F.C1)));
      leftBoundaries.add(F.NIL);
      rightBoundaries.add(lower);
      insertAt.add(-1);
      conditions.add(F.LessEqual(p, lower));
    }
    for (int i = 1; i <= m; i++) {
      IExpr left = numericNodes.get(i - 1);
      IExpr right = numericNodes.get(i);
      representatives.add(midpoint(left, right, engine));
      leftBoundaries.add(left.isNegativeInfinity() ? F.NIL : left);
      rightBoundaries.add(right.isInfinity() ? F.NIL : right);
      insertAt.add(i);
      conditions.add(left.isNegativeInfinity() ? F.LessEqual(p, right)
          : right.isInfinity() ? F.Less(left, p) : F.And(F.Less(left, p), F.LessEqual(p, right)));
    }
    if (!upper.isInfinity()) {
      representatives.add(engine.evaluate(F.Plus(upper, F.C1)));
      leftBoundaries.add(upper);
      rightBoundaries.add(F.NIL);
      insertAt.add(-1);
      conditions.add(F.Less(upper, p));
    }

    if (engine.getAssumptions() != null) {
      // the assumptions may decide the position of p
      for (int c = 0; c < conditions.size(); c++) {
        if (engine.evalTrue(conditions.get(c))) {
          return caseValue(f, x, numericNodes, p, a, alpha, beta, representatives.get(c),
              insertAt.get(c), engine);
        }
      }
    }

    List<IExpr> values = new ArrayList<IExpr>();
    for (int c = 0; c < representatives.size(); c++) {
      IExpr value = caseValue(f, x, numericNodes, p, a, alpha, beta, representatives.get(c),
          insertAt.get(c), engine);
      if (value.isNIL()) {
        return F.NIL;
      }
      values.add(engine.evaluate(F.Together(F.Expand(value))));
    }
    if (!f.has(e -> (e.isAST(S.UnitStep) || e.isAST(S.HeavisideTheta)) && !e.isFree(x), true)) {
      // like WMA a Piecewise for the other functions: Integrate(Max(x,a), {x,0,2}) gives
      // Piecewise({{2,a<=0},{2*a,a>=2}},(4+a^2)/2)
      return piecewiseForm(values, leftBoundaries, rightBoundaries, a, alpha, beta, engine);
    }
    // like WMA the differences of neighbouring cases are nested from the left for UnitStep
    final int last = values.size() - 1;
    IExpr inner = F.NIL;
    for (int c = 0; c < last; c++) {
      IExpr difference =
          engine.evaluate(F.Together(F.Expand(F.Subtract(values.get(c), values.get(c + 1)))));
      if (inner.isNIL()) {
        inner = difference;
      } else {
        IExpr step = F.UnitStep(F.Subtract(rightBoundaries.get(c - 1), p));
        inner = engine.evaluate(F.Plus(difference, F.Times(step, inner)));
      }
    }
    if (inner.isNIL()) {
      return values.get(last);
    }
    IExpr step = F.UnitStep(F.Subtract(rightBoundaries.get(last - 1), p));
    inner = engine.evaluate(F.Together(inner));
    IExpr result = engine.evaluate(F.Plus(values.get(last), F.Times(step, inner)));
    if (values.get(last).isZero()) {
      return result;
    }
    // over a common denominator, collected in the steps like WMA:
    // (1+(-1+E^a)*UnitStep(-a))/E^a
    IExpr together = engine.evaluate(F.Together(result));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    IASTAppendable steps = F.ListAlloc();
    for (int c = 0; c < last; c++) {
      steps.append(engine.evaluate(F.UnitStep(F.Subtract(rightBoundaries.get(c), p))));
    }
    numerator = engine.evaluate(F.Collect(numerator, steps));
    return engine.evaluate(F.Divide(numerator, denominator));
  }

  /**
   * <code>Piecewise({{V(first), a &lt;= b0}, {V(last), a &gt;= bm}, ...}, V(middle))</code> with the
   * conditions of the cases written for the parameter <code>a</code> of the break point
   * <code>p == alpha*a + beta</code>.
   */
  private static IExpr piecewiseForm(List<IExpr> values, List<IExpr> leftBoundaries,
      List<IExpr> rightBoundaries, IExpr a, IExpr alpha, IExpr beta, EvalEngine engine) {
    final int size = values.size();
    if (size == 1) {
      return values.get(0);
    }
    final boolean increasing = alpha.isPositiveResult();
    List<IExpr> conditions = new ArrayList<IExpr>();
    for (int c = 0; c < size; c++) {
      IExpr left = leftBoundaries.get(c);
      IExpr right = rightBoundaries.get(c);
      // p == n  <==>  a == (n - beta)/alpha
      IExpr aLeft = left.isNIL() ? F.NIL : engine.evaluate(F.Divide(F.Subtract(left, beta), alpha));
      IExpr aRight =
          right.isNIL() ? F.NIL : engine.evaluate(F.Divide(F.Subtract(right, beta), alpha));
      IExpr condition;
      if (aLeft.isNIL()) {
        condition = increasing ? F.LessEqual(a, aRight) : F.GreaterEqual(a, aRight);
      } else if (aRight.isNIL()) {
        condition = increasing ? F.GreaterEqual(a, aLeft) : F.LessEqual(a, aLeft);
      } else {
        condition = increasing ? F.And(F.Less(aLeft, a), F.LessEqual(a, aRight))
            : F.And(F.LessEqual(aRight, a), F.Less(a, aLeft));
      }
      conditions.add(condition);
    }
    IASTAppendable pieces = F.ListAlloc(size);
    pieces.append(F.list(values.get(0), conditions.get(0)));
    if (size == 2) {
      return engine.evaluate(F.Piecewise(pieces, values.get(1)));
    }
    pieces.append(F.list(values.get(size - 1), conditions.get(size - 1)));
    for (int c = 1; c < size - 2; c++) {
      pieces.append(F.list(values.get(c), conditions.get(c)));
    }
    return engine.evaluate(F.Piecewise(pieces, values.get(size - 2)));
  }

  /**
   * The integral if the symbolic break point <code>p</code> lies in one case.
   *
   * @param representative a numeric position of <code>p</code> in this case
   * @param insertAt the index of the numeric node before which <code>p</code> is inserted, or
   *        <code>-1</code> if <code>p</code> lies outside the limits
   */
  private static IExpr caseValue(IExpr f, IExpr x, List<IExpr> numericNodes, IExpr p, IExpr a,
      IExpr alpha, IExpr beta, IExpr representative, int insertAt, EvalEngine engine) {
    IExpr parameterValue = engine.evaluate(F.Divide(F.Subtract(representative, beta), alpha));
    List<IExpr> nodes = new ArrayList<IExpr>(numericNodes);
    List<IExpr> representatives = new ArrayList<IExpr>(numericNodes);
    if (insertAt > 0) {
      nodes.add(insertAt, p);
      representatives.add(insertAt, representative);
    }
    return integratePieces(f, x, nodes, representatives, F.list(F.Rule(a, parameterValue)),
        engine);
  }

  /** Does <code>f</code> contain one of the piecewise defined functions of <code>x</code>? */
  private static boolean isPiecewiseIn(IExpr f, IExpr x) {
    return f.has(e -> e.isAST() && isPiecewiseHead(e.head()) && !e.isFree(x), true);
  }

  private static boolean isPiecewiseHead(IExpr head) {
    return head == S.Piecewise || head == S.UnitStep || head == S.HeavisideTheta
        || head == S.Abs || head == S.RealAbs || head == S.Sign || head == S.RealSign
        || head == S.Max || head == S.Min;
  }

  /**
   * The numeric value of an integration limit.
   *
   * @return {@link Double#NaN} if the limit isn't numeric
   */
  private static double limitValue(IExpr limit) {
    if (limit.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    if (limit.isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }
    if (!limit.isRealResult() || !limit.isNumericFunction(true)) {
      return Double.NaN;
    }
    double value = limit.evalf();
    return Double.isInfinite(value) ? Double.NaN : value;
  }

  /** A point strictly inside the (possibly unbounded) interval <code>(a, b)</code>. */
  private static IExpr midpoint(IExpr a, IExpr b, EvalEngine engine) {
    if (a.isNegativeInfinity()) {
      return b.isInfinity() ? F.C0 : engine.evaluate(F.Subtract(b, F.C1));
    }
    if (b.isInfinity()) {
      return engine.evaluate(F.Plus(a, F.C1));
    }
    return engine.evaluate(F.Times(F.C1D2, F.Plus(a, b)));
  }

  /** Where in a bounded sub-interval the branches are decided; the first is the midpoint. */
  private static final IExpr[] SAMPLE_POSITIONS =
      {F.C1D2, F.QQ(1, 16), F.QQ(15, 16), F.QQ(5, 17), F.QQ(12, 17)};

  /**
   * How far from the finite end of an unbounded sub-interval the branches are decided. Not far: a
   * decaying argument like <code>x/E^x^2</code> is numerically zero there, which decides nothing.
   */
  private static final IExpr[] SAMPLE_DISTANCES = {F.C1, F.QQ(1, 16), F.QQ(1, 3), F.QQ(17, 5)};

  /**
   * Points strictly inside the (possibly unbounded) interval <code>(a, b)</code>. The break points
   * are meant to be the boundaries of the sub-intervals, so that a function has one branch on all
   * of it. That is tested at more than one point, because a break point can be missing - a zero
   * <code>Solve</code> didn't return, a jump of the argument itself.
   */
  private static List<IExpr> samples(IExpr a, IExpr b, EvalEngine engine) {
    List<IExpr> samples = new ArrayList<IExpr>();
    if (a.isNegativeInfinity() && b.isInfinity()) {
      samples.add(F.C0);
      for (IExpr distance : SAMPLE_DISTANCES) {
        samples.add(distance);
        samples.add(distance.negate());
      }
    } else if (a.isNegativeInfinity()) {
      for (IExpr distance : SAMPLE_DISTANCES) {
        samples.add(engine.evaluate(F.Subtract(b, distance)));
      }
    } else if (b.isInfinity()) {
      for (IExpr distance : SAMPLE_DISTANCES) {
        samples.add(engine.evaluate(F.Plus(a, distance)));
      }
    } else {
      IExpr length = F.Subtract(b, a);
      for (IExpr position : SAMPLE_POSITIONS) {
        samples.add(engine.evaluate(F.Plus(a, F.Times(position, length))));
      }
    }
    return samples;
  }

  /**
   * The functions which jump where their argument has neither a zero nor a pole. An argument of a
   * piecewise defined function which contains one of them has break points which aren't found.
   */
  private static boolean hasJumps(IExpr u, IExpr x) {
    return u.has(e -> e.isAST() && !e.isFree(x) && (e.head() == S.Floor || e.head() == S.Ceiling
        || e.head() == S.Round || e.head() == S.IntegerPart || e.head() == S.FractionalPart
        || e.head() == S.Mod || e.head() == S.Quotient), true);
  }

  /**
   * Add the poles of <code>Tan, Sec, Cot, Csc</code> (and their hyperbolic counterparts with a pole
   * on the real axis) in <code>u</code>: <code>Together</code> doesn't write them as a quotient, so
   * they aren't zeros of a denominator, but the sign of <code>u</code> changes there:
   * <code>Integrate(Sign(Tan(x)), {x,0,3}) == Pi-3</code>.
   */
  private static boolean addTrigPoles(IExpr u, IExpr x, IExpr lower, IExpr upper, double low,
      double up, TreeMap<Double, IExpr> points, SymbolicPoint symbolic, EvalEngine engine) {
    if (!u.isAST() || u.isFree(x)) {
      return true;
    }
    IAST ast = (IAST) u;
    if (ast.isAST1()) {
      IExpr head = ast.head();
      IExpr denominator = F.NIL;
      if (head == S.Tan || head == S.Sec) {
        denominator = F.Cos(ast.arg1());
      } else if (head == S.Cot || head == S.Csc) {
        denominator = F.Sin(ast.arg1());
      } else if (head == S.Coth || head == S.Csch) {
        denominator = F.Sinh(ast.arg1());
      }
      if (denominator.isPresent()
          && !addZeros(denominator, x, lower, upper, low, up, points, symbolic, engine)) {
        return false;
      }
    }
    for (IExpr arg : ast) {
      if (!addTrigPoles(arg, x, lower, upper, low, up, points, symbolic, engine)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Collect the break points of the piecewise defined functions in <code>expr</code> which are
   * strictly inside <code>(low, up)</code>.
   *
   * @return <code>false</code> if a break point can't be determined
   */
  private static boolean collectBreakPoints(IExpr expr, IExpr x, IExpr lower, IExpr upper,
      double low, double up, TreeMap<Double, IExpr> points, SymbolicPoint symbolic,
      EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(x)) {
      return true;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head == S.Abs || head == S.RealAbs || head == S.Sign || head == S.RealSign
        || head == S.UnitStep || head == S.HeavisideTheta) {
      for (IExpr arg : ast) {
        if (!addZerosAndPoles(arg, x, lower, upper, low, up, points, symbolic, engine)) {
          return false;
        }
      }
    } else if (head == S.Max || head == S.Min) {
      IAST args = ast.isAST1() && ast.arg1().isList() ? (IAST) ast.arg1() : ast;
      if (args.argSize() > 5) {
        return false;
      }
      for (int i = 1; i < args.size(); i++) {
        for (int j = i + 1; j < args.size(); j++) {
          if (!addZerosAndPoles(F.Subtract(args.get(i), args.get(j)), x, lower, upper, low, up,
              points, symbolic, engine)) {
            return false;
          }
        }
      }
    } else if (ast.isAST(S.Boole, 2)) {
      return addComparisonPoints(ast.arg1(), x, lower, upper, low, up, points, symbolic, engine);
    } else if (head == S.Piecewise) {
      if (ast.argSize() < 1 || !ast.arg1().isList()) {
        return false;
      }
      for (IExpr pair : (IAST) ast.arg1()) {
        if (!pair.isList2() || !addComparisonPoints(pair.second(), x, lower, upper, low, up,
            points, symbolic, engine)) {
          return false;
        }
        if (!collectBreakPoints(pair.first(), x, lower, upper, low, up, points, symbolic,
            engine)) {
          return false;
        }
      }
      return ast.argSize() < 2 || collectBreakPoints(ast.arg2(), x, lower, upper, low, up, points,
          symbolic, engine);
    }
    for (IExpr arg : ast) {
      if (!collectBreakPoints(arg, x, lower, upper, low, up, points, symbolic, engine)) {
        return false;
      }
    }
    return true;
  }

  /** The points where the two sides of the comparisons in <code>condition</code> are equal. */
  private static boolean addComparisonPoints(IExpr condition, IExpr x, IExpr lower, IExpr upper,
      double low, double up, TreeMap<Double, IExpr> points, SymbolicPoint symbolic,
      EvalEngine engine) {
    if (condition.isFree(x)) {
      // a condition on a parameter alone could change inside a sub-interval of the parameter
      return !hasVariables(condition);
    }
    if (!condition.isAST()) {
      return false;
    }
    IAST ast = (IAST) condition;
    if (ast.isAnd() || ast.isOr() || ast.isNot() || ast.isAST(S.Xor)) {
      for (IExpr arg : ast) {
        if (!addComparisonPoints(arg, x, lower, upper, low, up, points, symbolic, engine)) {
          return false;
        }
      }
      return true;
    }
    if (ast.isAST(S.Inequality) && ast.argSize() >= 3 && ast.argSize() % 2 == 1) {
      // Inequality(a, Less, b, LessEqual, c,...)
      for (int i = 1; i + 2 < ast.size(); i += 2) {
        if (!addZerosAndPoles(F.Subtract(ast.get(i), ast.get(i + 2)), x, lower, upper, low, up,
            points, symbolic, engine)) {
          return false;
        }
      }
      return true;
    }
    if (ast.argSize() >= 2 && (ast.isAST(S.Less) || ast.isAST(S.LessEqual)
        || ast.isAST(S.Greater) || ast.isAST(S.GreaterEqual) || ast.isAST(S.Equal)
        || ast.isAST(S.Unequal))) {
      // also chained comparisons like a < x < b
      for (int i = 1; i < ast.argSize(); i++) {
        if (!addZerosAndPoles(F.Subtract(ast.get(i), ast.get(i + 1)), x, lower, upper, low, up,
            points, symbolic, engine)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /** Does <code>expr</code> contain a variable (a parameter)? */
  private static boolean hasVariables(IExpr expr) {
    return !new VariablesSet(expr).isEmpty();
  }

  /** Add the real zeros and poles of <code>u</code> inside <code>(low, up)</code>. */
  private static boolean addZerosAndPoles(IExpr u, IExpr x, IExpr lower, IExpr upper,
      double low, double up, TreeMap<Double, IExpr> points, SymbolicPoint symbolic,
      EvalEngine engine) {
    if (u.isFree(x)) {
      // a sign which depends on a parameter alone could change inside a sub-interval of it
      return !hasVariables(u);
    }
    if (hasJumps(u, x)
        || !addTrigPoles(u, x, lower, upper, low, up, points, symbolic, engine)) {
      return false;
    }
    IExpr together = engine.evaluate(F.Together(u));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    return addZeros(numerator, x, lower, upper, low, up, points, symbolic, engine)
        && addZeros(denominator, x, lower, upper, low, up, points, symbolic, engine);
  }

  /** Add the real zeros of <code>u</code> inside <code>(low, up)</code>. */
  private static boolean addZeros(IExpr u, IExpr x, IExpr lower, IExpr upper, double low,
      double up, TreeMap<Double, IExpr> points, SymbolicPoint symbolic, EvalEngine engine) {
    if (u.isFree(x)) {
      return true;
    }
    if (hasVariables(F.subst(u, x, F.C0))) {
      return addSymbolicZero(u, x, symbolic, engine);
    }
    IAST constraints = F.list(F.Equal(u, F.C0));
    if (!lower.isNegativeInfinity()) {
      constraints = constraints.appendClone(F.Less(lower, x));
    }
    if (!upper.isInfinity()) {
      constraints = constraints.appendClone(F.Less(x, upper));
    }
    // quiet - the messages are about a search the caller never sees
    IExpr solved = engine.evalQuiet(F.Solve(constraints, x, S.Reals));
    if (!solved.isList()) {
      return false;
    }
    for (IExpr solution : (IAST) solved) {
      if (!solution.isList()) {
        return false;
      }
      IExpr point = F.NIL;
      for (IExpr rule : (IAST) solution) {
        if (rule.isRule() && rule.first().equals(x)) {
          point = rule.second();
          break;
        }
      }
      if (point.isNIL() || !point.isFree(x) || !point.isNumericFunction(true)) {
        return false;
      }
      double value = point.evalf();
      if (Double.isNaN(value) || Double.isInfinite(value)) {
        return false;
      }
      if (low < value && value < up && !containsPoint(points, value)) {
        if (points.size() >= MAX_BREAK_POINTS) {
          return false;
        }
        points.put(value, point);
      }
    }
    return true;
  }

  /**
   * The zero of <code>u == c1*x + c0</code> with a numeric <code>c1</code> and a <code>c0</code>
   * which is linear in a single parameter <code>a</code> is the symbolic break point
   * <code>-c0/c1</code>. Only one symbolic break point is supported.
   *
   * @return <code>false</code> if <code>u</code> isn't of this form or a second, different
   *         symbolic break point was found
   */
  private static boolean addSymbolicZero(IExpr u, IExpr x, SymbolicPoint symbolic,
      EvalEngine engine) {
    if (!u.isPolynomial(F.list(x)) || !engine.evaluate(F.Exponent(u, x)).isOne()) {
      return false;
    }
    IExpr c1 = engine.evaluate(F.Coefficient(u, x, F.C1));
    IExpr c0 = engine.evaluate(F.Coefficient(u, x, F.C0));
    if (!c1.isNumericFunction(true) || c1.isZero()) {
      return false;
    }
    IExpr point = engine.evaluate(F.Expand(F.Divide(F.Negate(c0), c1)));
    IAST variables = new VariablesSet(point).getVarList();
    if (variables.argSize() != 1) {
      return false;
    }
    IExpr a = variables.arg1();
    if (!point.isPolynomial(F.list(a)) || !engine.evaluate(F.Exponent(point, a)).isOne()
        || !engine.evaluate(F.Coefficient(point, a)).isNumericFunction(true)) {
      return false;
    }
    if (symbolic.point.isNIL()) {
      symbolic.point = point;
      symbolic.parameter = a;
      return true;
    }
    return engine.evaluate(F.Subtract(symbolic.point, point)).isZero();
  }

  private static boolean containsPoint(TreeMap<Double, IExpr> points, double value) {
    double tolerance = EPSILON * Math.max(1.0, Math.abs(value));
    Double floor = points.floorKey(value);
    if (floor != null && value - floor <= tolerance) {
      return true;
    }
    Double ceiling = points.ceilingKey(value);
    return ceiling != null && ceiling - value <= tolerance;
  }

  /**
   * Replace the piecewise defined functions in <code>expr</code> by their branch which is active
   * at the point given by the <code>rules</code> <code>{x -&gt; midpoint, a -&gt; value}</code>.
   * The break points are the boundaries of the sub-interval, so the branch is the same on all of
   * it.
   *
   * @return {@link F#NIL} if a branch can't be decided
   */
  private static IExpr rewrite(IExpr expr, IExpr x, IAST rules, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(x)) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Piecewise)) {
      if (ast.argSize() < 1 || !ast.arg1().isList()) {
        return F.NIL;
      }
      for (IExpr pair : (IAST) ast.arg1()) {
        if (!pair.isList2()) {
          return F.NIL;
        }
        IExpr condition = engine.evaluate(substitute(pair.second(), rules));
        if (condition.isTrue()) {
          return rewrite(pair.first(), x, rules, engine);
        }
        if (!condition.isFalse()) {
          return F.NIL;
        }
      }
      return ast.argSize() >= 2 ? rewrite(ast.arg2(), x, rules, engine) : F.C0;
    }
    if (ast.isAST(S.Boole, 2)) {
      IExpr condition = engine.evaluate(substitute(ast.arg1(), rules));
      if (condition.isTrue()) {
        return F.C1;
      }
      return condition.isFalse() ? F.C0 : F.NIL;
    }

    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = rewrite(ast.get(i), x, rules, engine);
      if (arg.isNIL()) {
        return F.NIL;
      }
      if (arg != ast.get(i)) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, arg);
      }
    }
    IAST rewritten = result.orElse(ast);
    IExpr head = rewritten.head();
    if ((head == S.Abs || head == S.RealAbs) && rewritten.isAST1()) {
      int sign = sign(rewritten.arg1(), rules, engine);
      if (sign == 0) {
        return F.NIL;
      }
      return sign > 0 ? rewritten.arg1() : F.Negate(rewritten.arg1());
    }
    if ((head == S.Sign || head == S.RealSign) && rewritten.isAST1()) {
      int sign = sign(rewritten.arg1(), rules, engine);
      return sign == 0 ? F.NIL : F.ZZ(sign);
    }
    if ((head == S.UnitStep || head == S.HeavisideTheta) && rewritten.argSize() >= 1) {
      // UnitStep(u1, u2,...) is 1 if all arguments are non-negative
      for (IExpr arg : rewritten) {
        int sign = sign(arg, rules, engine);
        if (sign == 0) {
          return F.NIL;
        }
        if (sign < 0) {
          return F.C0;
        }
      }
      return F.C1;
    }
    if ((head == S.Max || head == S.Min) && rewritten.argSize() >= 1) {
      IAST args = rewritten.isAST1() && rewritten.arg1().isList() ? (IAST) rewritten.arg1()
          : rewritten;
      if (args.argSize() < 1) {
        return F.NIL;
      }
      IExpr extremum = F.NIL;
      double extremumValue = 0.0;
      for (IExpr arg : args) {
        double value = numericValue(arg, rules, engine);
        if (Double.isNaN(value)) {
          return F.NIL;
        }
        if (extremum.isNIL() || (head == S.Max ? value > extremumValue : value < extremumValue)) {
          extremum = arg;
          extremumValue = value;
        }
      }
      return extremum;
    }
    return rewritten;
  }

  private static IExpr substitute(IExpr expr, IAST rules) {
    return expr.replaceAll(rules).orElse(expr);
  }

  /**
   * The sign of <code>u</code> at the point given by the <code>rules</code>.
   *
   * @return <code>0</code> if it's zero or not real
   */
  private static int sign(IExpr u, IAST rules, EvalEngine engine) {
    double value = numericValue(u, rules, engine);
    if (Double.isNaN(value) || Math.abs(value) < EPSILON) {
      return 0;
    }
    return value > 0.0 ? 1 : -1;
  }

  /**
   * The value of the real <code>u</code> at the point given by the <code>rules</code>.
   *
   * @return {@link Double#NaN} if it isn't a real number
   */
  private static double numericValue(IExpr u, IAST rules, EvalEngine engine) {
    IExpr value = engine.evalN(substitute(u, rules));
    if (!value.isReal()) {
      return Double.NaN;
    }
    return value.evalf();
  }
}
