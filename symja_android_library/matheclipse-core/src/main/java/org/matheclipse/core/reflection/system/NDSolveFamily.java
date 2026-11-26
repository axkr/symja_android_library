package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GridInterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>NDSolve(eqns, u, {t, t0, t1}, {p, p0, p1})</code> whose equations differentiate the
 * unknowns in <code>t</code> only: the second variable <code>p</code> is a parameter, and the
 * equations are an ordinary differential equation in <code>t</code> for each of its values. It may
 * enter the equations and the initial conditions, <code>u(0, p) == 1 + p</code>, and stand in
 * either argument of the unknowns.
 *
 * <p>
 * The parameter is sampled on a uniform grid, fine enough that the initial values given as
 * functions of it are resolved, with at least <code>"MinPoints"</code> and at most
 * <code>"MaxPoints"</code> points. The equation of each grid value is solved on its own, its
 * solution sampled at common times, and the samples are interpolated over both variables by an
 * <code>InterpolatingFunction</code> which also has the partial derivatives.
 */
final class NDSolveFamily {

  /**
   * The number of parameter points of a family whose initial values are smooth: fine enough that
   * the cubic interpolation of solutions like (1+a)*E^(-a*t) and of their derivatives in the
   * parameter is accurate to about 10^-6.
   */
  private static final int DEFAULT_POINTS = 51;

  /** The largest number of parameter points the grid is refined to. */
  private static final int DEFAULT_MAX_POINTS = 801;

  /** The relative error of the interpolated initial values which the parameter grid allows. */
  private static final double PROFILE_TOLERANCE = 1.0e-5;

  /** The fewest and the most samples of each solution along the evolution variable. */
  private static final int MIN_TIME_POINTS = 101;

  private static final int MAX_TIME_POINTS = 1001;

  private NDSolveFamily() {}

  /** How the unknowns are called: which argument is the evolution variable. */
  private static final class Shape {
    /** the position, 1 or 2, of the evolution variable in the calls of the unknowns */
    int timePosition = -1;

    /** an unknown is differentiated in the evolution variable */
    boolean differentiated = false;

    boolean ok = true;
  }

  /**
   * The solution of a family of ordinary differential equations, or {@link F#NIL} if the equations
   * don't have this shape.
   *
   * @param ast <code>NDSolve(eqns, u, {t, t0, t1}, {p, p0, p1}, options...)</code> with the
   *        equations separated
   * @param ruleForm <code>true</code> for rules <code>{{u-&gt;InterpolatingFunction(...)}}</code>
   */
  static IExpr solve(IAST ast, EvalEngine engine, boolean ruleForm) {
    if (ast.argSize() < 4 || !ast.arg3().isList3() || !ast.arg4().isList3()
        || !ast.arg3().first().isSymbol() || !ast.arg4().first().isSymbol()) {
      return F.NIL;
    }
    IAST unknowns = ast.arg2().makeList();
    ISymbol[] heads = new ISymbol[unknowns.argSize()];
    for (int i = 0; i < heads.length; i++) {
      IExpr unknown = unknowns.get(i + 1);
      IExpr head = unknown.isAST2() ? unknown.head() : unknown;
      if (!head.isSymbol() || head.isBuiltInSymbol()) {
        return F.NIL;
      }
      heads[i] = (ISymbol) head;
    }
    IAST equations = ast.arg1().makeList();
    // the ranges in either order: the evolution variable is the one which is differentiated
    for (int swap = 0; swap < 2; swap++) {
      IAST timeRange = (IAST) (swap == 0 ? ast.arg3() : ast.arg4());
      IAST parameterRange = (IAST) (swap == 0 ? ast.arg4() : ast.arg3());
      ISymbol t = (ISymbol) timeRange.arg1();
      ISymbol p = (ISymbol) parameterRange.arg1();
      Shape shape = new Shape();
      for (IExpr equation : equations) {
        if (!equation.isEqual()) {
          return F.NIL;
        }
        scan(equation, heads, p, shape);
        if (!shape.ok) {
          break;
        }
      }
      if (!shape.ok || !shape.differentiated || shape.timePosition < 0) {
        continue;
      }
      double p0 = parameterRange.arg2().evalfNaN();
      double p1 = parameterRange.arg3().evalfNaN();
      double t0 = timeRange.arg2().evalfNaN();
      double t1 = timeRange.arg3().evalfNaN();
      if (!Double.isFinite(p0) || !Double.isFinite(p1) || !(p0 < p1) || !Double.isFinite(t0)
          || !Double.isFinite(t1) || t0 == t1) {
        return F.NIL;
      }
      return solveFamily(ast, equations, heads, t, timeRange, p, p0, p1, shape.timePosition,
          engine, ruleForm);
    }
    return F.NIL;
  }

  /**
   * Check the calls of the unknowns in <code>expr</code>: <code>u(t, p)</code> or
   * <code>Derivative(k, 0)(u)(t, p)</code>, the parameter <code>p</code> itself in the same
   * position throughout and never differentiated.
   */
  private static void scan(IExpr expr, ISymbol[] heads, ISymbol p, Shape shape) {
    if (!shape.ok) {
      return;
    }
    if (!expr.isAST()) {
      if (isUnknown(expr, heads)) {
        // an unknown not called with the variables
        shape.ok = false;
      }
      return;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (isUnknown(head, heads) || isDerivativeOfUnknown(head, heads)) {
      if (!ast.isAST2()) {
        shape.ok = false;
        return;
      }
      int parameterPosition = ast.arg1() == p ? 1 : ast.arg2() == p ? 2 : -1;
      if (parameterPosition < 0 || (ast.arg1() == p && ast.arg2() == p)) {
        shape.ok = false;
        return;
      }
      int timePosition = 3 - parameterPosition;
      if (shape.timePosition >= 0 && shape.timePosition != timePosition) {
        shape.ok = false;
        return;
      }
      shape.timePosition = timePosition;
      if (!ast.get(timePosition).isFree(p)) {
        shape.ok = false;
        return;
      }
      if (head.isAST()) {
        IAST orders = (IAST) head.head();
        if (!orders.get(parameterPosition).isZero()) {
          // a derivative in the parameter: a partial differential equation
          shape.ok = false;
          return;
        }
        if (!orders.get(timePosition).isZero()) {
          shape.differentiated = true;
        }
      }
      if (!ast.get(timePosition).isFree(x -> isUnknown(x, heads), true)) {
        shape.ok = false;
      }
      return;
    }
    for (IExpr arg : ast) {
      scan(arg, heads, p, shape);
    }
    scan(head, heads, p, shape);
  }

  private static boolean isUnknown(IExpr expr, ISymbol[] heads) {
    for (ISymbol head : heads) {
      if (expr == head) {
        return true;
      }
    }
    return false;
  }

  /** Whether <code>head</code> is <code>Derivative(i, j)(u)</code> of an unknown. */
  private static boolean isDerivativeOfUnknown(IExpr head, ISymbol[] heads) {
    return head.isAST1() && head.head().isAST(S.Derivative, 3) && isUnknown(head.first(), heads)
        && head.head().first().isInteger() && head.head().second().isInteger();
  }

  private static IExpr solveFamily(IAST ast, IAST equations, ISymbol[] heads, ISymbol t,
      IAST timeRange, ISymbol p, double p0, double p1, int timePosition, EvalEngine engine,
      boolean ruleForm) {
    int parameterPosition = 3 - timePosition;
    // the options of the member equations, without the grid options of the parameter
    IASTAppendable options = F.ListAlloc();
    int minPoints = DEFAULT_POINTS;
    int maxPoints = DEFAULT_MAX_POINTS;
    for (int i = 5; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first().isString("MinPoints")) {
        minPoints = option.second().toIntDefault(DEFAULT_POINTS);
      } else if (option.isRuleAST() && option.first().isString("MaxPoints")) {
        maxPoints = Math.max(2, option.second().toIntDefault(DEFAULT_MAX_POINTS));
      } else if (option.isRuleAST() && option.first() == S.Method && option.second().isList()
          && option.second().first().isString("MethodOfLines")) {
        // Method -> {"MethodOfLines", "SpatialDiscretization" -> {"TensorProductGrid",
        // "MinPoints" -> n, "MaxPoints" -> m}} sets the grid of the parameter
        for (IExpr sub : (IAST) option.second()) {
          if (sub.isRuleAST() && sub.first().isString("SpatialDiscretization")
              && sub.second().isList()) {
            for (IExpr grid : (IAST) sub.second()) {
              if (grid.isRuleAST() && grid.first().isString("MinPoints")) {
                minPoints = grid.second().toIntDefault(DEFAULT_POINTS);
              } else if (grid.isRuleAST() && grid.first().isString("MaxPoints")) {
                maxPoints = Math.max(2, grid.second().toIntDefault(DEFAULT_MAX_POINTS));
              }
            }
          }
        }
      } else {
        options.append(option);
      }
    }
    // "MinPoints" raises the number of points, it doesn't lower it below the default
    minPoints = Math.max(DEFAULT_POINTS, minPoints);
    maxPoints = Math.max(maxPoints, minPoints);

    double[] ps = parameterGrid(equations, heads, p, p0, p1, minPoints, maxPoints, engine);
    // the members: the unknowns renamed, called with the evolution variable only
    ISymbol[] members = new ISymbol[heads.length];
    for (int i = 0; i < heads.length; i++) {
      members[i] = F.Dummy("NDSolveFamily$" + (i + 1));
    }
    IAST memberEquations = (IAST) equations.replaceAll(x -> member(x, heads, members,
        timePosition, parameterPosition)).orElse(equations);
    if (!memberEquations.isFree(x -> isUnknown(x, heads), true)) {
      return F.NIL;
    }
    IAST range = F.List(t, timeRange.arg2(), timeRange.arg3());
    IExpr[][] solutions = new IExpr[ps.length][heads.length];
    int timePoints = MIN_TIME_POINTS;
    double t0 = timeRange.arg2().evalfNaN();
    double t1 = timeRange.arg3().evalfNaN();
    // the interval on which every member has a solution: a member which runs into a singularity,
    // like u' == u^2 with u(0) == 1 at t == 1, ends the family there
    double tMin = Math.min(t0, t1);
    double tMax = Math.max(t0, t1);
    for (int j = 0; j < ps.length; j++) {
      engine.checkTimeLimit();
      IExpr substituted = F.subst(memberEquations, p, F.num(ps[j]));
      IASTAppendable call = F.ast(S.NDSolve);
      call.append(substituted);
      call.append(F.List(members));
      call.append(range);
      call.appendArgs(options);
      // the messages of a member, like the one of a singularity, are given once for the family
      IExpr solution = engine.evalQuiet(call);
      if (!solution.isListOfLists() || solution.argSize() < 1) {
        // a member without a solution: its messages tell why
        engine.evaluate(call);
        return F.NIL;
      }
      IAST rules = (IAST) solution.first();
      for (int i = 0; i < members.length; i++) {
        IExpr function = F.NIL;
        for (IExpr rule : rules) {
          if (rule.isRuleAST() && rule.first() == members[i]) {
            function = rule.second();
          }
        }
        if (function.isNIL() || function.head() != S.InterpolatingFunction) {
          return F.NIL;
        }
        solutions[j][i] = function;
        IExpr domain = engine.evalQuiet(F.unaryAST1(function, F.stringx("Domain")));
        if (domain.isList() && domain.argSize() >= 1 && domain.first().isList2()) {
          double from = domain.first().first().evalfNaN();
          double to = domain.first().second().evalfNaN();
          if (Double.isFinite(from) && Double.isFinite(to)) {
            tMin = Math.max(tMin, Math.min(from, to));
            tMax = Math.min(tMax, Math.max(from, to));
          }
        }
        IExpr grid = engine.evalQuiet(F.unaryAST1(function, F.stringx("Grid")));
        if (grid.isList()) {
          timePoints = Math.max(timePoints, 2 * grid.argSize());
        }
      }
    }
    timePoints = Math.min(timePoints, MAX_TIME_POINTS);
    if (!(tMin < tMax)) {
      return F.NIL;
    }
    double tolerance = 1.0e-10 * Math.max(1.0, Math.abs(t1 - t0));
    if (t1 >= t0 ? tMax < t1 - tolerance : tMin > t1 + tolerance) {
      // At `1` == `2`, step size is effectively zero; singularity or stiff system suspected.
      Errors.printMessage(ast.topHead(), "ndsz", F.list(t, F.num(t1 >= t0 ? tMax : tMin)),
          engine);
    }
    double[] ts = new double[timePoints];
    for (int k = 0; k < timePoints; k++) {
      ts[k] = k == timePoints - 1 ? tMax : tMin + k * (tMax - tMin) / (timePoints - 1);
    }

    IASTAppendable functions = F.ListAlloc(heads.length);
    for (int i = 0; i < heads.length; i++) {
      // values at the grid points, the parameter or the time innermost as in the calls
      IExpr[][] data = new IExpr[ts.length * ps.length][];
      for (int k = 0; k < ts.length; k++) {
        for (int j = 0; j < ps.length; j++) {
          engine.checkTimeLimit();
          IExpr value = engine.evalQuiet(F.unaryAST1(solutions[j][i], F.num(ts[k])));
          double v = value.evalfNaN();
          if (!Double.isFinite(v)) {
            return F.NIL;
          }
          int index = timePosition == 1 ? k * ps.length + j : j * ts.length + k;
          data[index] = new IExpr[] {F.num(v)};
        }
      }
      IExpr[] timeAxis = new IExpr[ts.length];
      for (int k = 0; k < ts.length; k++) {
        timeAxis[k] = F.num(ts[k]);
      }
      IExpr[] parameterAxis = new IExpr[ps.length];
      for (int j = 0; j < ps.length; j++) {
        parameterAxis[j] = F.num(ps[j]);
      }
      IExpr[][] coordinates = timePosition == 1 ? new IExpr[][] {timeAxis, parameterAxis}
          : new IExpr[][] {parameterAxis, timeAxis};
      functions.append(
          GridInterpolatingFunctionExpr.newLocal(coordinates, data, new int[] {3, 3}, engine));
    }
    // an unknown given as a call u(t, p) gets the call of its function, u(t, p) -> f(t, p)
    IAST unknowns = ast.arg2().makeList();
    IASTAppendable values = F.ListAlloc(heads.length);
    for (int i = 0; i < heads.length; i++) {
      IExpr unknown = unknowns.get(i + 1);
      IExpr function = functions.get(i + 1);
      values.append(unknown.isAST() ? ((IAST) unknown).apply(function) : function);
    }
    if (ruleForm) {
      IASTAppendable rules = F.ListAlloc(heads.length);
      for (int i = 0; i < heads.length; i++) {
        rules.append(F.Rule(unknowns.get(i + 1), values.get(i + 1)));
      }
      return F.list(rules);
    }
    return ast.arg2().isList() ? values : values.arg1();
  }

  /**
   * <code>u(t, p)</code> as <code>member(t)</code> and <code>Derivative(k, 0)(u)(t, p)</code> as
   * <code>Derivative(k)(member)(t)</code>, or {@link F#NIL} for any other expression.
   */
  private static IExpr member(IExpr x, ISymbol[] heads, ISymbol[] members, int timePosition,
      int parameterPosition) {
    if (!x.isAST2()) {
      return F.NIL;
    }
    IExpr head = x.head();
    IExpr time = ((IAST) x).get(timePosition);
    for (int i = 0; i < heads.length; i++) {
      if (head == heads[i]) {
        return F.unaryAST1(members[i], time);
      }
      if (head.isAST1() && head.first() == heads[i] && head.head().isAST(S.Derivative, 3)) {
        IExpr order = head.head().getAt(timePosition);
        if (order.isZero()) {
          return F.unaryAST1(members[i], time);
        }
        return F.unaryAST1(F.unaryAST1(F.unaryAST1(S.Derivative, order), members[i]), time);
      }
    }
    return F.NIL;
  }

  /**
   * The uniform grid of the parameter: <code>minPoints</code> points, doubled in density until
   * the initial values given as functions of the parameter are interpolated within
   * {@link #PROFILE_TOLERANCE} of their size, but at most <code>maxPoints</code>.
   */
  private static double[] parameterGrid(IAST equations, ISymbol[] heads, ISymbol p, double p0,
      double p1, int minPoints, int maxPoints, EvalEngine engine) {
    // the sides of the conditions which are free of the unknowns and depend on the parameter
    java.util.List<IExpr> profiles = new java.util.ArrayList<>();
    for (IExpr equation : equations) {
      for (int side = 1; side <= 2; side++) {
        IExpr value = equation.getAt(side);
        IExpr other = equation.getAt(3 - side);
        if (value.isFree(x -> isUnknown(x, heads), true) && !value.isFree(p)
            && !other.isFree(x -> isUnknown(x, heads), true)) {
          profiles.add(value);
        }
      }
    }
    int n = minPoints;
    while (n < maxPoints && !resolves(profiles, p, p0, p1, n, engine)) {
      n = 2 * n - 1;
    }
    n = Math.min(n, maxPoints);
    double[] ps = new double[n];
    for (int j = 0; j < n; j++) {
      ps[j] = j == n - 1 ? p1 : p0 + j * (p1 - p0) / (n - 1);
    }
    return ps;
  }

  /**
   * Whether cubic interpolation through <code>n</code> uniform points reproduces each profile at
   * the midpoints between them.
   */
  private static boolean resolves(java.util.List<IExpr> profiles, ISymbol p, double p0,
      double p1, int n, EvalEngine engine) {
    if (profiles.isEmpty() || n < 4) {
      return true;
    }
    double h = (p1 - p0) / (n - 1);
    for (IExpr profile : profiles) {
      double[] values = new double[n];
      double size = 0.0;
      for (int j = 0; j < n; j++) {
        engine.checkTimeLimit();
        values[j] = engine.evalQuiet(F.N(F.subst(profile, p, F.num(p0 + j * h)))).evalfNaN();
        if (!Double.isFinite(values[j])) {
          return true;
        }
        size = Math.max(size, Math.abs(values[j]));
      }
      for (int j = 1; j < n - 2; j++) {
        // the cubic through the points j-1 .. j+2 at the midpoint of j and j+1
        double interpolated =
            (-values[j - 1] + 9.0 * values[j] + 9.0 * values[j + 1] - values[j + 2]) / 16.0;
        double exact =
            engine.evalQuiet(F.N(F.subst(profile, p, F.num(p0 + (j + 0.5) * h)))).evalfNaN();
        if (Double.isFinite(exact)
            && Math.abs(interpolated - exact) > PROFILE_TOLERANCE * Math.max(size, 1.0e-300)) {
          return false;
        }
      }
    }
    return true;
  }
}
