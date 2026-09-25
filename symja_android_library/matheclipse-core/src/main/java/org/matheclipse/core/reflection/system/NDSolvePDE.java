package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>NDSolve</code> of a partial differential equation in one space variable, by the method of
 * lines:
 *
 * <pre>
 * NDSolve({D(u(t, x), t) == a*D(u(t, x), x, x) + b*D(u(t, x), x) + c*u(t, x) + d,
 *          u(t0, x) == f(x), u(t, x0) == g0(t), u(t, x1) == g1(t)}, u, {t, t0, t1}, {x, x0, x1})
 * NDSolve({D(u(t, x), t, t) == a*D(u(t, x), x, x) + b*D(u(t, x), x) + G(t, x, u(t, x)),
 *          u(t0, x) == f(x), Derivative(1, 0)(u)(t0, x) == g(x), ...}, u, {t, t0, t1}, {x, x0, x1})
 * </pre>
 *
 * <p>
 * The first is the parabolic problem the heat equation is the model of: the coefficients
 * <code>a &gt; 0</code>, <code>b</code>, <code>c</code> and <code>d</code> may depend on
 * <code>t</code> and <code>x</code>, and the equation has to be linear in <code>u</code> and its
 * derivatives. The space interval is divided into {@link #SPACE_POINTS} points and the resulting
 * system of ordinary differential equations is stepped with the Crank-Nicolson scheme: it is
 * stable at any step size, which an explicit method is not - the heat equation is stiff. Each step
 * solves one tridiagonal system.
 *
 * <p>
 * The second is the hyperbolic problem of the wave equation, second order in time, which also
 * takes the initial velocity. Its term <code>G</code> may be nonlinear in <code>u</code> - a
 * Klein-Gordon term <code>-u + u^3/6</code>, say - so it is stepped explicitly, with the
 * velocity-Verlet scheme and a time step within the Courant limit of the grid.
 *
 * <p>
 * The ends are either given values, <code>u(t, x0) == g0(t)</code> and
 * <code>u(t, x1) == g1(t)</code>, or joined, <code>u(t, x0) == u(t, x1)</code>: a periodic domain,
 * whose last grid point is its first. Such an equation is not a value for one end - reading it as
 * one was a bug in another implementation - and a periodic parabolic step solves a cyclic
 * tridiagonal system.
 *
 * <p>
 * The solution is an <code>InterpolatingFunction</code> of <code>t</code> and <code>x</code>,
 * interpolated between the grid points by a bicubic spline, and is handed back as
 * <code>{{u -&gt; InterpolatingFunction(...)}}</code>.
 */
final class NDSolvePDE {

  /** The number of grid points in the space variable, both ends included. */
  static final int SPACE_POINTS = 101;

  /** The number of time steps. */
  static final int TIME_STEPS = 1000;

  private NDSolvePDE() {}

  /**
   * The solution of the problem in <code>ast</code>, or {@link F#NIL} where it is not one this
   * class solves - an ordinary differential equation, a second space variable, a boundary condition
   * that does not fix the value, or an equation which is not linear.
   *
   * @param ast the whole <code>NDSolve(...)</code> or <code>NDSolveValue(...)</code> expression
   * @param engine the evaluation engine
   * @param ruleForm <code>true</code> for <code>{{u -&gt; f}}</code>, <code>false</code> for the
   *        function itself
   */
  static IExpr solve(IAST ast, EvalEngine engine, boolean ruleForm) {
    if (ast.argSize() < 4 || !ast.arg3().isList3() || !ast.arg4().isList3()
        || !ast.arg3().first().isSymbol() || !ast.arg4().first().isSymbol()) {
      return F.NIL;
    }
    IAST tRange = (IAST) ast.arg3();
    IAST xRange = (IAST) ast.arg4();
    ISymbol t = (ISymbol) tRange.arg1();
    ISymbol x = (ISymbol) xRange.arg1();
    double t0 = tRange.arg2().evalfNaN();
    double t1 = tRange.arg3().evalfNaN();
    double x0 = xRange.arg2().evalfNaN();
    double x1 = xRange.arg3().evalfNaN();
    if (!(t1 > t0) || !(x1 > x0)) {
      return F.NIL;
    }

    // the unknown, written as u or as u(t, x)
    IExpr unknown = ast.arg2();
    IExpr u;
    if (unknown.isSymbol()) {
      u = unknown;
    } else if (unknown.isAST2() && unknown.first() == t && unknown.second() == x) {
      u = unknown.head();
    } else {
      return F.NIL;
    }

    // placeholders for u(t, x) and its derivatives
    ISymbol utt = F.Dummy("pdeUtt");
    ISymbol ut = F.Dummy("pdeUt");
    ISymbol u0 = F.Dummy("pdeU");
    ISymbol ux = F.Dummy("pdeUx");
    ISymbol uxx = F.Dummy("pdeUxx");
    IAST derivatives = F.List( //
        F.Rule(derivative(u, 2, 0, t, x), utt), //
        F.Rule(derivative(u, 1, 0, t, x), ut), //
        F.Rule(derivative(u, 0, 2, t, x), uxx), //
        F.Rule(derivative(u, 0, 1, t, x), ux), //
        F.Rule(F.binaryAST2(u, t, x), u0));
    IExpr velocityHead = F.unaryAST1(F.binaryAST2(S.Derivative, F.C1, F.C0), u);

    IExpr rhs = F.NIL;
    int order = 0;
    IExpr initial = F.NIL;
    IExpr velocity = F.NIL;
    IExpr lower = F.NIL;
    IExpr upper = F.NIL;
    boolean periodic = false;
    IAST equations = ast.arg1().makeList();
    for (IExpr equation : equations) {
      if (!equation.isEqual()) {
        return F.NIL;
      }
      IExpr lhs = equation.first();
      IExpr right = equation.second();
      IExpr difference = engine.evaluate(F.subst(F.Subtract(lhs, right), derivatives));
      if (!difference.isFree(utt) || !difference.isFree(ut)) {
        // the equation itself, solved for its highest time derivative, which it has to be
        // linear in with a numeric coefficient
        ISymbol highest = difference.isFree(utt) ? ut : utt;
        if (highest == utt && !difference.isFree(ut)) {
          // a damping term u_t in a second order equation is not stepped here
          return F.NIL;
        }
        IExpr coefficient = engine.evaluate(F.D(difference, highest));
        if (!coefficient.isFree(highest) || !coefficient.isNumber() || coefficient.isZero()) {
          return F.NIL;
        }
        rhs = engine.evaluate(F.Together(F.Times(F.CN1,
            F.Divide(F.subst(difference, F.Rule(highest, F.C0)), coefficient))));
        order = highest == utt ? 2 : 1;
        continue;
      }
      if (isApplication(lhs, u) && isApplication(right, u) && isPeriodic(lhs, right, t, x0, x1)) {
        // u(t, x0) == u(t, x1): the two ends are one point of a periodic domain
        periodic = true;
        continue;
      }
      // a condition: u(t0, x) == f(x), Derivative(1,0)(u)(t0, x) == g(x) or u(t, a) == g(t),
      // either way round
      IExpr application = lhs;
      IExpr value = right;
      if (!isApplication(application, u) && !isApplication(application, velocityHead)) {
        application = right;
        value = lhs;
      }
      if (!value.isFree(u) || !value.isFree(velocityHead)) {
        // a condition relating u to itself, which is not a value
        return F.NIL;
      }
      IExpr first = application.first();
      IExpr second = application.second();
      if (isApplication(application, velocityHead)) {
        if (second != x || !first.isNumber() || Math.abs(first.evalfNaN() - t0) > 1e-12) {
          return F.NIL;
        }
        velocity = value;
      } else if (!isApplication(application, u)) {
        return F.NIL;
      } else if (second == x && first.isNumber()) {
        if (Math.abs(first.evalfNaN() - t0) > 1e-12) {
          return F.NIL;
        }
        initial = value;
      } else if (first == t && second.isNumber()) {
        double at = second.evalfNaN();
        if (Math.abs(at - x0) <= 1e-12) {
          lower = value;
        } else if (Math.abs(at - x1) <= 1e-12) {
          upper = value;
        } else {
          return F.NIL;
        }
      } else {
        return F.NIL;
      }
    }
    boolean dirichlet = lower.isPresent() && upper.isPresent();
    if (rhs.isNIL() || initial.isNIL() || periodic == (lower.isPresent() || upper.isPresent())
        || (!periodic && !dirichlet)) {
      // one kind of ends, both of them
      return F.NIL;
    }
    if ((order == 2) != velocity.isPresent()) {
      return F.NIL;
    }

    // rhs = a u_xx + b u_x + (the rest), linear in u_xx and u_x
    IExpr a = engine.evaluate(F.D(rhs, uxx));
    IExpr b = engine.evaluate(F.D(rhs, ux));
    IExpr rest = engine.evaluate(F.subst(rhs, F.List(F.Rule(uxx, F.C0), F.Rule(ux, F.C0))));
    for (IExpr coefficient : new IExpr[] {a, b}) {
      if (!coefficient.isFree(uxx) || !coefficient.isFree(ux) || !coefficient.isFree(u0)) {
        return F.NIL;
      }
    }
    if (!rest.isFree(uxx) || !rest.isFree(ux)) {
      return F.NIL;
    }

    Grid grid = new Grid(x0, x1, periodic);
    Coefficient fa = new Coefficient(a, t, x, engine);
    Coefficient fb = new Coefficient(b, t, x, engine);
    Coefficient ff = new Coefficient(initial, t, x, engine);
    Coefficient fg0 = lower.isPresent() ? new Coefficient(lower, t, x, engine) : null;
    Coefficient fg1 = upper.isPresent() ? new Coefficient(upper, t, x, engine) : null;

    IExpr function;
    if (order == 1) {
      // the parabolic problem has to be linear: rest = c u + d
      IExpr c = engine.evaluate(F.D(rest, u0));
      IExpr d = engine.evaluate(F.subst(rest, F.Rule(u0, F.C0)));
      if (!c.isFree(u0) || !d.isFree(u0)) {
        return F.NIL;
      }
      function = parabolic(grid, t0, t1, fa, fb, new Coefficient(c, t, x, engine),
          new Coefficient(d, t, x, engine), ff, fg0, fg1);
    } else {
      Source source = new Source(rest, u0, t, x, engine);
      function = hyperbolic(grid, t0, t1, fa, fb, source, ff,
          new Coefficient(velocity, t, x, engine), fg0, fg1);
    }
    if (function.isNIL()) {
      return F.NIL;
    }
    if (!ruleForm) {
      return function;
    }
    IExpr rule =
        unknown.isSymbol() ? F.Rule(u, function) : F.Rule(unknown, F.binaryAST2(function, t, x));
    return F.List(F.List(rule));
  }

  /** Whether <code>u(t, x0) == u(t, x1)</code>, either way round. */
  private static boolean isPeriodic(IExpr left, IExpr right, ISymbol t, double x0, double x1) {
    if (left.first() != t || right.first() != t || !left.second().isNumber()
        || !right.second().isNumber()) {
      return false;
    }
    double a = left.second().evalfNaN();
    double b = right.second().evalfNaN();
    return (Math.abs(a - x0) <= 1e-12 && Math.abs(b - x1) <= 1e-12)
        || (Math.abs(a - x1) <= 1e-12 && Math.abs(b - x0) <= 1e-12);
  }

  /**
   * The space grid: {@link #SPACE_POINTS} points from <code>x0</code> to <code>x1</code>. On a
   * periodic domain the last point is the first one again, so only the first
   * <code>SPACE_POINTS - 1</code> are unknowns and the neighbours wrap round.
   */
  private static final class Grid {
    final int n = SPACE_POINTS;
    final double h;
    final double[] xs;
    final boolean periodic;
    /** the number of points whose values are stepped: all of them, or all but the repeated one */
    final int unknowns;

    Grid(double x0, double x1, boolean periodic) {
      this.periodic = periodic;
      this.h = (x1 - x0) / (n - 1);
      this.xs = new double[n];
      for (int i = 0; i < n; i++) {
        xs[i] = x0 + i * h;
      }
      this.unknowns = periodic ? n - 1 : n;
    }

    /** the value to the left of point i, wrapping round on a periodic grid */
    double left(double[] values, int i) {
      return i > 0 ? values[i - 1] : values[unknowns - 1];
    }

    /** the value to the right of point i, wrapping round on a periodic grid */
    double right(double[] values, int i) {
      return i < unknowns - 1 ? values[i + 1] : values[0];
    }
  }

  /** The parabolic problem, by Crank-Nicolson. */
  private static IExpr parabolic(Grid grid, double t0, double t1, Coefficient fa, Coefficient fb,
      Coefficient fc, Coefficient fd, Coefficient ff, Coefficient fg0, Coefficient fg1) {
    int n = grid.n;
    int steps = TIME_STEPS;
    double h = grid.h;
    double dt = (t1 - t0) / steps;
    double[] xs = grid.xs;
    double[] ts = new double[steps + 1];
    double[][] values = new double[steps + 1][n];

    // the initial values, with the boundary values at t0 at the two ends
    ts[0] = t0;
    for (int i = 0; i < n; i++) {
      values[0][i] = ff.value(t0, xs[i]);
    }
    if (grid.periodic) {
      values[0][n - 1] = values[0][0];
    } else {
      values[0][0] = fg0.value(t0, xs[0]);
      values[0][n - 1] = fg1.value(t0, xs[n - 1]);
    }
    for (double v : values[0]) {
      if (!Double.isFinite(v)) {
        return F.NIL;
      }
    }

    double[] lowerDiagonal = new double[n];
    double[] diagonal = new double[n];
    double[] upperDiagonal = new double[n];
    double[] right = new double[n];
    // the points whose values a step solves for: the interior, or every distinct point
    int from = grid.periodic ? 0 : 1;
    int to = grid.periodic ? grid.unknowns - 1 : n - 2;
    for (int k = 0; k < steps; k++) {
      double tn = t0 + k * dt;
      double tn1 = (k + 1 == steps) ? t1 : t0 + (k + 1) * dt;
      ts[k + 1] = tn1;
      double[] now = values[k];
      double[] next = values[k + 1];
      if (!grid.periodic) {
        next[0] = fg0.value(tn1, xs[0]);
        next[n - 1] = fg1.value(tn1, xs[n - 1]);
      }
      for (int i = from; i <= to; i++) {
        double xi = xs[i];
        // the discretised operator at the new and at the old time
        double an = fa.value(tn, xi), bn = fb.value(tn, xi), cn = fc.value(tn, xi);
        double a1 = fa.value(tn1, xi), b1 = fb.value(tn1, xi), c1 = fc.value(tn1, xi);
        double alphaN = an / (h * h) - bn / (2 * h);
        double betaN = -2 * an / (h * h) + cn;
        double gammaN = an / (h * h) + bn / (2 * h);
        double alpha1 = a1 / (h * h) - b1 / (2 * h);
        double beta1 = -2 * a1 / (h * h) + c1;
        double gamma1 = a1 / (h * h) + b1 / (2 * h);
        lowerDiagonal[i] = -0.5 * dt * alpha1;
        diagonal[i] = 1 - 0.5 * dt * beta1;
        upperDiagonal[i] = -0.5 * dt * gamma1;
        right[i] = 0.5 * dt * alphaN * grid.left(now, i) + (1 + 0.5 * dt * betaN) * now[i]
            + 0.5 * dt * gammaN * grid.right(now, i)
            + 0.5 * dt * (fd.value(tn, xi) + fd.value(tn1, xi));
      }
      if (grid.periodic) {
        // row 0 reaches round to the last unknown, the last row round to the first
        if (!solveCyclicTridiagonal(lowerDiagonal, diagonal, upperDiagonal, right, next, to + 1,
            upperDiagonal[to], lowerDiagonal[0])) {
          return F.NIL;
        }
        next[n - 1] = next[0];
      } else {
        // the known boundary values move to the right hand side
        right[1] -= lowerDiagonal[1] * next[0];
        right[n - 2] -= upperDiagonal[n - 2] * next[n - 1];
        if (!solveTridiagonal(lowerDiagonal, diagonal, upperDiagonal, right, next, 1, n - 2)) {
          return F.NIL;
        }
      }
    }

    // the grid of values, interpolated the way ListInterpolation interpolates a matrix
    return ListInterpolation.grid(ts, xs, values, 3);
  }

  /**
   * The hyperbolic problem <code>u_tt = a u_xx + b u_x + G(t, x, u)</code>, by the method of lines
   * and the velocity-Verlet scheme. The step keeps within the Courant limit
   * <code>dt &lt;= h / sqrt(a)</code> with a margin of one half.
   */
  private static IExpr hyperbolic(Grid grid, double t0, double t1, Coefficient fa, Coefficient fb,
      Source source, Coefficient ff, Coefficient fv, Coefficient fg0, Coefficient fg1) {
    int n = grid.n;
    double h = grid.h;
    double[] xs = grid.xs;
    // the fastest wave speed sqrt(a) over the grid, looked at the start, middle and end
    double maxA = 0.0;
    for (double tv : new double[] {t0, 0.5 * (t0 + t1), t1}) {
      for (double xv : xs) {
        double av = fa.value(tv, xv);
        if (!(av > 0.0)) {
          // not a wave equation
          return F.NIL;
        }
        maxA = Math.max(maxA, av);
      }
    }
    int steps = (int) Math.ceil((t1 - t0) / (0.5 * h / Math.sqrt(maxA)));
    if (steps > 2_000_000) {
      return F.NIL;
    }
    double dt = (t1 - t0) / steps;
    // at most about TIME_STEPS rows of the solution are kept for the interpolation
    int stride = Math.max(1, (int) Math.ceil((double) steps / TIME_STEPS));
    int rows = steps / stride + (steps % stride == 0 ? 1 : 2);
    double[] ts = new double[rows];
    double[][] values = new double[rows][];

    double[] now = new double[n];
    double[] speed = new double[n];
    for (int i = 0; i < n; i++) {
      now[i] = ff.value(t0, xs[i]);
      speed[i] = fv.value(t0, xs[i]);
    }
    if (grid.periodic) {
      now[n - 1] = now[0];
    } else {
      now[0] = fg0.value(t0, xs[0]);
      now[n - 1] = fg1.value(t0, xs[n - 1]);
    }
    double[] acceleration = new double[n];
    if (!acceleration(grid, t0, now, fa, fb, source, acceleration)) {
      return F.NIL;
    }
    ts[0] = t0;
    values[0] = now.clone();
    int row = 1;
    double[] next = new double[n];
    double[] nextAcceleration = new double[n];
    int from = grid.periodic ? 0 : 1;
    int to = grid.periodic ? grid.unknowns - 1 : n - 2;
    for (int k = 0; k < steps; k++) {
      double tn1 = (k + 1 == steps) ? t1 : t0 + (k + 1) * dt;
      for (int i = from; i <= to; i++) {
        next[i] = now[i] + dt * speed[i] + 0.5 * dt * dt * acceleration[i];
      }
      if (grid.periodic) {
        next[n - 1] = next[0];
      } else {
        next[0] = fg0.value(tn1, xs[0]);
        next[n - 1] = fg1.value(tn1, xs[n - 1]);
      }
      if (!acceleration(grid, tn1, next, fa, fb, source, nextAcceleration)) {
        return F.NIL;
      }
      for (int i = from; i <= to; i++) {
        speed[i] += 0.5 * dt * (acceleration[i] + nextAcceleration[i]);
      }
      double[] swap = now;
      now = next;
      next = swap;
      swap = acceleration;
      acceleration = nextAcceleration;
      nextAcceleration = swap;
      if ((k + 1) % stride == 0 || k + 1 == steps) {
        ts[row] = tn1;
        values[row++] = now.clone();
      }
    }
    return ListInterpolation.grid(ts, xs, values, 3);
  }

  /** <code>a u_xx + b u_x + G(t, x, u)</code> at every stepped point. */
  private static boolean acceleration(Grid grid, double tv, double[] values, Coefficient fa,
      Coefficient fb, Source source, double[] result) {
    double h = grid.h;
    int from = grid.periodic ? 0 : 1;
    int to = grid.periodic ? grid.unknowns - 1 : grid.n - 2;
    for (int i = from; i <= to; i++) {
      double xi = grid.xs[i];
      double left = grid.left(values, i);
      double right = grid.right(values, i);
      double uxx = (left - 2 * values[i] + right) / (h * h);
      double ux = (right - left) / (2 * h);
      double value = fa.value(tv, xi) * uxx + fb.value(tv, xi) * ux + source.value(tv, xi, values[i]);
      if (!Double.isFinite(value)) {
        return false;
      }
      result[i] = value;
    }
    return true;
  }

  /**
   * The term <code>G(t, x, u)</code> of the wave equation, which may be nonlinear in
   * <code>u</code>. A term linear in <code>u</code> is split into two {@link Coefficient}s; any
   * other is evaluated with the value substituted.
   */
  private static final class Source {
    private final Coefficient slope;
    private final Coefficient offset;
    private final IExpr expr;
    private final ISymbol u;
    private final ISymbol t;
    private final ISymbol x;
    private final EvalEngine engine;

    Source(IExpr expr, ISymbol u, ISymbol t, ISymbol x, EvalEngine engine) {
      this.expr = expr;
      this.u = u;
      this.t = t;
      this.x = x;
      this.engine = engine;
      IExpr c = engine.evaluate(F.D(expr, u));
      if (c.isFree(u)) {
        slope = new Coefficient(c, t, x, engine);
        offset = new Coefficient(engine.evaluate(F.subst(expr, F.Rule(u, F.C0))), t, x, engine);
      } else {
        slope = null;
        offset = null;
      }
    }

    double value(double tv, double xv, double uv) {
      if (slope != null) {
        return slope.value(tv, xv) * uv + offset.value(tv, xv);
      }
      IExpr value = engine.evalN(F.subst(expr,
          F.List(F.Rule(t, F.num(tv)), F.Rule(x, F.num(xv)), F.Rule(u, F.num(uv)))));
      return value.isReal() ? value.evalf() : Double.NaN;
    }
  }

  /** <code>Derivative(m, n)(u)(t, x)</code> */
  private static IAST derivative(IExpr u, int m, int n, IExpr t, IExpr x) {
    return F.binaryAST2(F.unaryAST1(F.binaryAST2(S.Derivative, F.ZZ(m), F.ZZ(n)), u), t, x);
  }

  /** <code>u(s, y)</code>: the unknown applied to two arguments. */
  private static boolean isApplication(IExpr expr, IExpr u) {
    return expr.isAST2() && expr.head().equals(u);
  }

  /**
   * Thomas' algorithm for the rows <code>from .. to</code> of a tridiagonal system; the solution is
   * written into those entries of <code>result</code>.
   *
   * @return <code>false</code> where a pivot vanishes
   */
  private static boolean solveTridiagonal(double[] lower, double[] diagonal, double[] upper,
      double[] right, double[] result, int from, int to) {
    int n = to - from + 1;
    double[] c = new double[n];
    double[] d = new double[n];
    double pivot = diagonal[from];
    if (pivot == 0.0) {
      return false;
    }
    c[0] = upper[from] / pivot;
    d[0] = right[from] / pivot;
    for (int k = 1; k < n; k++) {
      int i = from + k;
      pivot = diagonal[i] - lower[i] * c[k - 1];
      if (pivot == 0.0) {
        return false;
      }
      c[k] = upper[i] / pivot;
      d[k] = (right[i] - lower[i] * d[k - 1]) / pivot;
    }
    result[to] = d[n - 1];
    for (int k = n - 2; k >= 0; k--) {
      result[from + k] = d[k] - c[k] * result[from + k + 1];
    }
    return true;
  }

  /**
   * A cyclic tridiagonal system on the rows <code>0 .. m-1</code>: tridiagonal, and in addition
   * row <code>m-1</code> has <code>bottomLeft</code> in column 0 and row 0 has
   * <code>topRight</code> in column <code>m-1</code>. Solved by the Sherman-Morrison formula from
   * two ordinary tridiagonal solves (Numerical Recipes, <code>cyclic</code>).
   *
   * @return <code>false</code> where a pivot vanishes
   */
  static boolean solveCyclicTridiagonal(double[] lower, double[] diagonal, double[] upper,
      double[] right, double[] result, int m, double bottomLeft, double topRight) {
    double gamma = -diagonal[0];
    if (gamma == 0.0) {
      return false;
    }
    double[] modified = new double[m];
    System.arraycopy(diagonal, 0, modified, 0, m);
    modified[0] = diagonal[0] - gamma;
    modified[m - 1] = diagonal[m - 1] - bottomLeft * topRight / gamma;
    double[] solution = new double[m];
    if (!solveTridiagonal(lower, modified, upper, right, solution, 0, m - 1)) {
      return false;
    }
    double[] column = new double[m];
    column[0] = gamma;
    column[m - 1] = bottomLeft;
    double[] z = new double[m];
    if (!solveTridiagonal(lower, modified, upper, column, z, 0, m - 1)) {
      return false;
    }
    double denominator = 1.0 + z[0] + topRight * z[m - 1] / gamma;
    if (denominator == 0.0) {
      return false;
    }
    double factor = (solution[0] + topRight * solution[m - 1] / gamma) / denominator;
    for (int i = 0; i < m; i++) {
      result[i] = solution[i] - factor * z[i];
    }
    return true;
  }

  /**
   * A coefficient of the equation, or a condition, as a function of <code>t</code> and
   * <code>x</code>. One that depends on neither is evaluated once; one that depends on
   * <code>x</code> only is remembered per grid point.
   */
  private static final class Coefficient {
    private final IExpr expr;
    private final ISymbol t;
    private final ISymbol x;
    private final EvalEngine engine;
    private final boolean dependsOnT;
    private final boolean dependsOnX;
    private double constant = Double.NaN;
    private final java.util.Map<Double, Double> byX = new java.util.HashMap<>();

    Coefficient(IExpr expr, ISymbol t, ISymbol x, EvalEngine engine) {
      this.expr = expr;
      this.t = t;
      this.x = x;
      this.engine = engine;
      this.dependsOnT = !expr.isFree(t);
      this.dependsOnX = !expr.isFree(x);
    }

    double value(double tv, double xv) {
      if (!dependsOnT && !dependsOnX) {
        if (Double.isNaN(constant)) {
          constant = evaluate(tv, xv);
        }
        return constant;
      }
      if (!dependsOnT) {
        Double cached = byX.get(xv);
        if (cached == null) {
          cached = evaluate(tv, xv);
          byX.put(xv, cached);
        }
        return cached;
      }
      return evaluate(tv, xv);
    }

    private double evaluate(double tv, double xv) {
      IExpr value = engine.evalN(F.subst(expr, F.List(F.Rule(t, F.num(tv)), F.Rule(x, F.num(xv)))));
      return value.isReal() ? value.evalf() : Double.NaN;
    }
  }
}
