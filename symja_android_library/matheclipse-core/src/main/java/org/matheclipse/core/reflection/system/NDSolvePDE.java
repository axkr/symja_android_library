package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>NDSolve</code> of a partial differential equation in one space variable, by the method of
 * lines:
 *
 * <pre>
 * NDSolve({D(u(t, x), t) == a*D(u(t, x), x, x) + b*D(u(t, x), x) + c*u(t, x) + d,
 *          u(t0, x) == f(x), u(t, x0) == g0(t), u(t, x1) == g1(t)}, u, {t, t0, t1}, {x, x0, x1})
 * </pre>
 *
 * <p>
 * The coefficients <code>a &gt; 0</code>, <code>b</code>, <code>c</code> and <code>d</code> may
 * depend on <code>t</code> and <code>x</code>; the equation has to be linear in <code>u</code> and
 * its derivatives, which is the parabolic problem the heat equation is the model of. The space
 * interval is divided into {@link #SPACE_POINTS} points and the resulting system of ordinary
 * differential equations is stepped with the Crank-Nicolson scheme: it is stable at any step size,
 * which an explicit method is not - the heat equation is stiff, and an explicit step would have to
 * be shorter than half the square of the grid spacing. Each step solves one tridiagonal system.
 *
 * <p>
 * The solution is an <code>InterpolatingFunction</code> of <code>t</code> and <code>x</code>,
 * interpolated between the grid points by a bicubic spline, and is handed back as
 * <code>{{u -&gt; InterpolatingFunction(...)}}</code>, as the Wolfram Language answers.
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
    ISymbol ut = F.Dummy("pdeUt");
    ISymbol u0 = F.Dummy("pdeU");
    ISymbol ux = F.Dummy("pdeUx");
    ISymbol uxx = F.Dummy("pdeUxx");
    IAST derivatives = F.List( //
        F.Rule(derivative(u, 1, 0, t, x), ut), //
        F.Rule(derivative(u, 0, 2, t, x), uxx), //
        F.Rule(derivative(u, 0, 1, t, x), ux), //
        F.Rule(F.binaryAST2(u, t, x), u0));

    IExpr rhs = F.NIL;
    IExpr initial = F.NIL;
    IExpr lower = F.NIL;
    IExpr upper = F.NIL;
    IAST equations = ast.arg1().makeList();
    for (IExpr equation : equations) {
      if (!equation.isEqual()) {
        return F.NIL;
      }
      IExpr lhs = equation.first();
      IExpr right = equation.second();
      IExpr difference = engine.evaluate(F.subst(F.Subtract(lhs, right), derivatives));
      if (!difference.isFree(ut)) {
        // the equation for D(u(t, x), t): solved for it, it has to be linear in it
        IExpr coefficient = engine.evaluate(F.D(difference, ut));
        if (!coefficient.isFree(ut) || !coefficient.isNumber() || coefficient.isZero()) {
          return F.NIL;
        }
        rhs = engine.evaluate(F.Together(F.Times(F.CN1,
            F.Divide(F.subst(difference, F.Rule(ut, F.C0)), coefficient))));
        continue;
      }
      // a condition: u(t0, x) == f(x) or u(t, a) == g(t), either way round
      IExpr application = lhs;
      IExpr value = right;
      if (!isApplication(application, u)) {
        application = right;
        value = lhs;
      }
      if (!isApplication(application, u)) {
        return F.NIL;
      }
      IExpr first = application.first();
      IExpr second = application.second();
      if (second == x && first.isNumber()) {
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
    if (rhs.isNIL() || initial.isNIL() || lower.isNIL() || upper.isNIL()) {
      return F.NIL;
    }

    // u_t = a u_xx + b u_x + c u + d, and nothing else
    IExpr a = engine.evaluate(F.D(rhs, uxx));
    IExpr b = engine.evaluate(F.D(rhs, ux));
    IExpr c = engine.evaluate(F.D(rhs, u0));
    IExpr d = engine.evaluate(
        F.subst(rhs, F.List(F.Rule(uxx, F.C0), F.Rule(ux, F.C0), F.Rule(u0, F.C0))));
    for (IExpr coefficient : new IExpr[] {a, b, c, d}) {
      if (!coefficient.isFree(uxx) || !coefficient.isFree(ux) || !coefficient.isFree(u0)) {
        return F.NIL;
      }
    }

    Coefficient fa = new Coefficient(a, t, x, engine);
    Coefficient fb = new Coefficient(b, t, x, engine);
    Coefficient fc = new Coefficient(c, t, x, engine);
    Coefficient fd = new Coefficient(d, t, x, engine);
    Coefficient ff = new Coefficient(initial, t, x, engine);
    Coefficient fg0 = new Coefficient(lower, t, x, engine);
    Coefficient fg1 = new Coefficient(upper, t, x, engine);

    int n = SPACE_POINTS;
    int steps = TIME_STEPS;
    double h = (x1 - x0) / (n - 1);
    double dt = (t1 - t0) / steps;
    double[] xs = new double[n];
    for (int i = 0; i < n; i++) {
      xs[i] = x0 + i * h;
    }
    double[] ts = new double[steps + 1];
    double[][] values = new double[steps + 1][n];

    // the initial values, with the boundary values at t0 at the two ends
    ts[0] = t0;
    for (int i = 0; i < n; i++) {
      values[0][i] = ff.value(t0, xs[i]);
    }
    values[0][0] = fg0.value(t0, x0);
    values[0][n - 1] = fg1.value(t0, x1);
    for (double v : values[0]) {
      if (!Double.isFinite(v)) {
        return F.NIL;
      }
    }

    double[] lowerDiagonal = new double[n];
    double[] diagonal = new double[n];
    double[] upperDiagonal = new double[n];
    double[] right = new double[n];
    for (int k = 0; k < steps; k++) {
      double tn = t0 + k * dt;
      double tn1 = (k + 1 == steps) ? t1 : t0 + (k + 1) * dt;
      ts[k + 1] = tn1;
      double[] now = values[k];
      double[] next = values[k + 1];
      next[0] = fg0.value(tn1, x0);
      next[n - 1] = fg1.value(tn1, x1);
      for (int i = 1; i < n - 1; i++) {
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
        right[i] = 0.5 * dt * alphaN * now[i - 1] + (1 + 0.5 * dt * betaN) * now[i]
            + 0.5 * dt * gammaN * now[i + 1] + 0.5 * dt * (fd.value(tn, xi) + fd.value(tn1, xi));
      }
      // the known boundary values move to the right hand side
      right[1] -= lowerDiagonal[1] * next[0];
      right[n - 2] -= upperDiagonal[n - 2] * next[n - 1];
      if (!solveTridiagonal(lowerDiagonal, diagonal, upperDiagonal, right, next, 1, n - 2)) {
        return F.NIL;
      }
    }

    // the grid of values, interpolated the way ListInterpolation interpolates a matrix
    IExpr function = ListInterpolation.grid(ts, xs, values, 3);
    if (!ruleForm) {
      return function;
    }
    IExpr rule = unknown.isSymbol() ? F.Rule(u, function)
        : F.Rule(unknown, F.binaryAST2(function, t, x));
    return F.List(F.List(rule));
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
      IExpr value = engine
          .evalN(F.subst(expr, F.List(F.Rule(t, F.num(tv)), F.Rule(x, F.num(xv)))));
      return value.isReal() ? value.evalf() : Double.NaN;
    }
  }
}
