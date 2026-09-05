package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Third order linear equations whose solutions are the products of the solutions of a second order
 * one.
 *
 * <p>
 * Removing the second derivative from <code>y''' + p2*y'' + p1*y' + p0*y == 0</code> with
 * <code>y == w*Exp(-Integrate(p2/3, x))</code> leaves <code>w''' + P1*w' + P0*w == 0</code>, and
 * such an equation has the products of the solutions of <code>u'' + (P1/4)*u == 0</code> as its own
 * solutions exactly when <code>P0 == P1'/2</code>. So the answer is
 * <code>Exp(-Integrate(p2/3, x))</code> times an arbitrary combination of <code>u1^2</code>,
 * <code>u1*u2</code> and <code>u2^2</code>.
 *
 * <p>
 * This is worth its own method because the second order equation may be one of those whose
 * solutions are named functions while the third order equation it comes from is not:
 * <code>y'''(x) - 4*(x+2)*y'(x) - 2*y(x) == 0</code> is built from Airy's equation.
 */
final class DSolveSymmetricSquare {

  private DSolveSymmetricSquare() {}

  /**
   * The general solution of the equation, or {@link F#NIL} if its solutions are not the products of
   * the solutions of a second order equation this can find.
   */
  static IExpr solve(LinearODEForm lf, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (lf.order != 3 || !lf.g.isZero() || lf.a[3].isZero()) {
      return F.NIL;
    }
    IExpr p2 = cancel(F.Divide(lf.a[2], lf.a[3]), engine);
    IExpr p1 = cancel(F.Divide(lf.a[1], lf.a[3]), engine);
    IExpr p0 = cancel(F.Divide(lf.a[0], lf.a[3]), engine);

    // The coefficients of the equation the substitution leaves.
    IExpr firstOrder = engine.evaluate(F.Subtract(F.Subtract(p1, F.Divide(F.Sqr(p2), F.C3)),
        F.D(p2, xVar)));
    IExpr zeroOrder = engine.evaluate(F.Plus( //
        F.Subtract(p0, F.Divide(F.Times(p1, p2), F.C3)), //
        F.Divide(F.Times(F.C2, F.Power(p2, F.C3)), F.ZZ(27)), //
        F.Divide(F.Negate(F.D(p2, F.List(xVar, F.C2))), F.C3)));

    IExpr condition = engine.evaluate(
        F.Subtract(zeroOrder, F.Divide(F.D(firstOrder, xVar), F.C2)));
    if (!DSolveODE.isVanishing(condition, engine)) {
      return F.NIL;
    }

    IExpr factor = F.C1;
    if (!p2.isZero()) {
      IExpr integral = ctx.integrate(engine.evaluate(F.Divide(p2, F.C3)), xVar);
      if (integral.isNIL()) {
        return F.NIL;
      }
      factor = engine.evaluate(F.Exp(F.Negate(integral)));
    }

    // The second order equation whose solutions multiply out to the ones asked for.
    IExpr uDummy = F.Dummy("u");
    IExpr uFunc = F.unaryAST1(uDummy, xVar);
    IExpr equation = F.Equal(F.Plus(engine.evaluate(F.D(uFunc, F.List(xVar, F.C2))),
        F.Times(F.Divide(firstOrder, F.C4), uFunc)), F.C0);
    IAST branches = DSolveODE.solveSubODE(equation, xVar, uFunc, c_n, ctx);
    if (branches.argSize() != 1) {
      return F.NIL;
    }
    IExpr general = engine.evaluate(F.ExpandAll(branches.arg1()));

    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(general, constants);
    if (constants.argSize() != 2) {
      return F.NIL;
    }
    IExpr first = engine.evaluate(F.Coefficient(general, constants.arg1()));
    IExpr second = engine.evaluate(F.Coefficient(general, constants.arg2()));
    IExpr rest = engine.evaluate(F.ExpandAll(F.Subtract(general,
        F.Plus(F.Times(first, constants.arg1()), F.Times(second, constants.arg2())))));
    if (first.isZero() || second.isZero() || !DSolveODE.isVanishing(rest, engine)) {
      return F.NIL;
    }

    IExpr third = ctx.nextConstant();
    return engine.evaluate(F.Times(factor, //
        F.Plus(F.Times(constants.arg1(), F.Sqr(first)), //
            F.Times(constants.arg2(), first, second), //
            F.Times(third, F.Sqr(second)))));
  }

  private static IExpr cancel(IExpr expr, EvalEngine engine) {
    return engine.evaluate(F.Cancel(F.Together(expr)));
  }
}
