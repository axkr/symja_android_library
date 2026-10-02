package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Nonlinear equations whose left side is the derivative of something.
 *
 * <p>
 * <code>2*y*y''' + 2*(y + 3*y')*y'' + 2*y'^2 == g(x)</code> is <code>(y^2)''' + (y^2)''</code> on
 * its left, so it is <code>d/dx (2*y*y'' + 2*y'^2 + 2*y*y') == g(x)</code>, and integrating both
 * sides leaves an equation of one order less with a constant of its own. That equation goes back to
 * the cascade.
 *
 * <p>
 * Whether an expression <code>F(x, y, y', ..., y^(m))</code> is a derivative is decided by taking
 * it apart from the top. A derivative <code>d/dx G(x, y, ..., y^(m-1))</code> is of the first
 * degree in <code>y^(m)</code>, with the coefficient <code>dG/dy^(m-1)</code>; so that coefficient
 * is integrated over <code>y^(m-1)</code>, the derivative of the result is taken off, and what is
 * left has to be a derivative of something of one order less. At the bottom there is a function of
 * <code>x</code> alone, or the expression was no derivative.
 *
 * <p>
 * A wrong first integral can only make this decline: its derivative is compared with the equation
 * before anything is solved, and the solutions which come back are put into the equation.
 *
 * <p>
 * Nonlinear equations only, and after every method which knows the equation by its form: a linear
 * equation which is exact has methods of its own, and those write its answer better.
 */
final class DSolveTotalDerivative {

  private DSolveTotalDerivative() {}

  /** How long the whole method may take. */
  private static final int DEADLINE_SECONDS = 6;

  /** How long one of the quadratures may take. */
  private static final int INTEGRATE_SECONDS = 2;

  /** How big any of the expressions along the way may become. */
  private static final int MAX_LEAF_COUNT = 400;

  /** Where the answer is put back into the equation it came from. */
  private static final int[][] SAMPLES =
      new int[][] {{17, 10}, {23, 10}, {23, 20}, {3, 5}, {31, 10}};

  /**
   * The general solution of the equation of order <code>n</code>, or {@link F#NIL} if its left
   * side is not a derivative, or if the equation it leaves cannot be solved.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, int n, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (n < 2 || lhs.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }
    // The derivatives become plain symbols: D and Integrate take a symbol for their variable.
    IExpr[] derivatives = new IExpr[n + 1];
    IExpr[] symbols = new IExpr[n + 1];
    for (int k = 0; k <= n; k++) {
      derivatives[k] = k == 0 ? yFunction : engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
      symbols[k] = F.Dummy("tD" + k);
    }
    IExpr jet = lhs;
    // Downwards, so that the highest derivative is replaced before the ones it contains.
    for (int k = n; k >= 0; k--) {
      jet = F.subst(jet, derivatives[k], symbols[k]);
    }
    jet = engine.evaluate(jet);
    if (!jet.isFree(yFunction.head(), true) || DSolveUtil.hasUndefinedFunction(jet)) {
      return F.NIL;
    }

    // The deadline covers the search for the integral only: the methods the reduced equation is
    // handed to keep one of their own, and there is one deadline at a time.
    IExpr integral;
    ctx.startDeadline(DEADLINE_SECONDS);
    try {
      integral = integrate(jet, symbols, n, xVar, ctx);
      if (integral.isNIL() || integral.isFree(symbols[n - 1], true)
          || integral.leafCount() > MAX_LEAF_COUNT || ctx.expired()) {
        return F.NIL;
      }
      // The reason a wrong integral can only make this decline.
      IExpr difference = F.Subtract(derivative(integral, symbols, n - 1, xVar, engine), jet);
      if (!DSolveODE.isVanishing(engine.evaluate(F.Expand(difference)), engine)) {
        return F.NIL;
      }
    } finally {
      ctx.clearDeadline();
    }
    for (int k = n - 1; k >= 0; k--) {
      integral = F.subst(integral, symbols[k], derivatives[k]);
    }
    IExpr reduced = engine.evaluate(F.Subtract(integral, c_n));
    IExpr constant = ctx.nextConstant();
    IAST branches = F.CEmptyList;
    if (n > 2) {
      // What is left may be a derivative again - (a+y)*y'' + y'^2 == C(1) is ((a+y)*y')' - and
      // then it is integrated here, before the methods for an equation of the second order are
      // asked: they answer it too, and take many times as long over it.
      IExpr again = solve(engine.evaluate(F.ExpandAll(reduced)), yFunction, xVar, n - 1, constant,
          ctx);
      if (again.isPresent()) {
        branches = again.makeList();
      }
    }
    if (branches.argSize() == 0) {
      branches = DSolveODE.solveSubODE(F.Equal(reduced, F.C0), xVar, yFunction, constant, ctx);
    }

    IAST residuals = F.list(lhs);
    IASTAppendable samples = F.ListAlloc(SAMPLES.length);
    for (int[] fraction : SAMPLES) {
      samples.append(F.QQ(fraction[0], fraction[1]));
    }
    IASTAppendable accepted = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr body = branches.get(i);
      if (body.isNIL() || !DSolveContext.isUsable(body) || body.leafCount() > MAX_LEAF_COUNT) {
        continue;
      }
      if (DSolveVerify.acceptODEStrictAt(residuals, yFunction, xVar, body, samples, engine)) {
        accepted.append(DSolveUtil.renumberConstants(body, c_n, engine));
      }
    }
    if (accepted.argSize() == 0) {
      return F.NIL;
    }
    return accepted.argSize() == 1 ? accepted.arg1() : accepted;
  }

  /**
   * The <code>G(x, y, ..., y^(m-1))</code> whose derivative is <code>expr</code>, an expression in
   * <code>x</code> and <code>symbols[0..m]</code>, or {@link F#NIL} if there is none.
   */
  private static IExpr integrate(IExpr expr, IExpr[] symbols, int m, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr integral = F.C0;
    IExpr rest = expr;
    for (int k = m; k >= 1; k--) {
      if (ctx.expired() || rest.leafCount() > MAX_LEAF_COUNT) {
        return F.NIL;
      }
      IExpr coefficient = engine.evaluate(F.D(rest, symbols[k]));
      if (!coefficient.isFree(symbols[k], true)) {
        // not of the first degree in the highest derivative left
        return F.NIL;
      }
      if (coefficient.isZero()) {
        continue;
      }
      IExpr part = ctx.integrate(coefficient, symbols[k - 1], MAX_LEAF_COUNT, INTEGRATE_SECONDS);
      if (part.isNIL()) {
        return F.NIL;
      }
      integral = F.Plus(integral, part);
      rest = engine
          .evaluate(F.Expand(F.Subtract(rest, derivative(part, symbols, k - 1, xVar, engine))));
      if (!rest.isFree(symbols[k], true)) {
        rest = engine.evaluate(F.Simplify(rest));
        if (!rest.isFree(symbols[k], true)) {
          return F.NIL;
        }
      }
    }
    if (!rest.isFree(symbols[0], true)) {
      // something of x and y alone is the derivative of nothing: that would contain y'
      return F.NIL;
    }
    if (!rest.isZero()) {
      IExpr part = ctx.integrate(rest, xVar, MAX_LEAF_COUNT, INTEGRATE_SECONDS);
      if (part.isNIL()) {
        return F.NIL;
      }
      integral = F.Plus(integral, part);
    }
    return engine.evaluate(integral);
  }

  /**
   * The derivative with respect to <code>x</code> of an expression in <code>x</code> and
   * <code>symbols[0..top]</code>, where <code>symbols[k]</code> stands for <code>y^(k)</code>.
   */
  private static IExpr derivative(IExpr expr, IExpr[] symbols, int top, IExpr xVar,
      EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(top + 2);
    sum.append(F.D(expr, xVar));
    for (int k = 0; k <= top; k++) {
      sum.append(F.Times(symbols[k + 1], F.D(expr, symbols[k])));
    }
    return engine.evaluate(sum);
  }
}
