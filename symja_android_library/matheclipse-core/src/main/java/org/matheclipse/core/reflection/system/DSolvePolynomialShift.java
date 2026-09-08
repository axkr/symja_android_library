package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * First order equations which a shift of the unknown turns into a quadrature.
 *
 * <p>
 * <code>y' == R(x) + g(x)*(phi(x) + c*y)^p</code>, with <code>p</code> not a whole number,
 * <code>c</code> a constant and <code>R == -phi'/c</code>, becomes separable under
 * <code>u == phi(x) + c*y</code>: the shift takes the <code>R</code> with it and leaves
 * <code>u' == c*g(x)*u^p</code>, whose integral is
 * <code>u^(1-p)/(1-p) == Integrate(c*g, x) + C(1)</code>.
 *
 * <p>
 * The unknown appears under a root, so what the equation determines is
 * <code>(phi + c*y)^(1-p)</code> rather than <code>y</code> itself, and the answer here takes the
 * principal root of that. It solves the equation where that root is the branch the equation
 * follows, which is the same caveat every explicit answer to <code>y' == Sqrt(y)</code> carries.
 *
 * <p>
 * What identifies the equation is a power of something linear in the unknown whose exponent is a
 * fraction. Nothing above this in the cascade produces such a term, so recognizing it early costs
 * the methods below nothing -- and saves them a good deal, since a radical of the unknown is what
 * the searches among them spend their budget on without answering.
 */
final class DSolvePolynomialShift {

  private DSolvePolynomialShift() {}

  /** How big a right hand side is still worth looking at. */
  private static final int MAX_LEAF_COUNT = 200;

  /** How big the integrand of the quadrature may be. */
  private static final int MAX_INTEGRAL_LEAF_COUNT = 100;

  /**
   * The general solution of the equation, or {@link F#NIL} if it is not of this form.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr head = yFunction.head();
    IExpr dyx = engine.evaluate(F.D(yFunction, xVar));
    IExpr coefficient = engine.evaluate(F.Coefficient(lhs, dyx));
    if (coefficient.isZero() || !DSolveODE.isLinearInDerivative(lhs, dyx, engine)) {
      return F.NIL;
    }
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Times(coefficient, dyx)));
    IExpr yDummy = F.Dummy("Y");
    IExpr right = engine.evaluate(
        F.subst(F.Cancel(F.Together(F.Divide(F.Negate(rest), coefficient))), yFunction, yDummy));
    if (!right.isFree(head, true) || right.leafCount() > MAX_LEAF_COUNT
        || right.isFree(yDummy, true)) {
      return F.NIL;
    }

    IAST atom = rootOfLinear(right, yDummy, engine);
    if (atom == null) {
      return F.NIL;
    }
    IExpr base = atom.arg1();
    IExpr exponent = atom.arg2();
    IExpr slope = engine.evaluate(F.D(base, yDummy));
    if (!slope.isFree(yDummy, true) || !slope.isFree(xVar, true) || slope.isZero()) {
      // The coefficient of the unknown has to be a constant, or the substitution below puts the
      // unknown back into the equation instead of taking it out.
      return F.NIL;
    }
    IExpr shift = engine.evaluate(F.Subtract(base, F.Times(slope, yDummy)));
    if (!shift.isFree(yDummy, true)) {
      return F.NIL;
    }
    IExpr oneMinus = engine.evaluate(F.Subtract(F.C1, exponent));
    if (oneMinus.isZero()) {
      return F.NIL;
    }

    // Written out by plain evaluation, never by Simplify: the rate below is a product of a power
    // of the new unknown and its reciprocal, which cancels on its own, while Simplify goes looking
    // for a way to rationalize the radical and does not come back.
    IExpr uDummy = F.Dummy("u");
    IExpr substituted = engine.evaluate(F.subst(right, yDummy,
        F.Divide(F.Subtract(uDummy, shift), slope)));
    IExpr rate = engine.evaluate(F.Expand(F.Times(
        F.Plus(F.D(shift, xVar), F.Times(slope, substituted)), F.Power(uDummy, F.Negate(exponent)))));
    if (!rate.isFree(uDummy, true) || !rate.isFree(yDummy, true) || rate.isZero()
        || rate.leafCount() > MAX_INTEGRAL_LEAF_COUNT) {
      // Either the equation carries an R other than -phi'/c, or what multiplies the power is not
      // a function of the variable alone.
      return F.NIL;
    }
    IExpr quadrature = ctx.integrate(rate, xVar, MAX_INTEGRAL_LEAF_COUNT);
    if (quadrature.isNIL()) {
      return F.NIL;
    }

    IExpr shifted = engine.evaluate(F.Power(F.Times(oneMinus, F.Plus(c_n, quadrature)),
        F.Divide(F.C1, oneMinus)));
    IExpr body = engine.evaluate(F.Divide(F.Subtract(shifted, shift), slope));
    if (!body.isFree(uDummy, true) || !body.isFree(yDummy, true)
        || !DSolveContext.isUsable(body)) {
      return F.NIL;
    }
    // The same check the cascade ends with, applied here so that an answer it would refuse is not
    // built and handed on. It is what decides the equations whose quadrature runs negative: the
    // answer takes the principal root of (1-p)*(C(1) + Integrate(c*g, x)), which is the root the
    // equation follows only while that stays positive, and for those equations it does not stay
    // positive anywhere the check looks. Saying so implicitly, as
    // u^(1-p)/(1-p) - Integrate(c*g, x) == C(1), would be true on both branches, and is what this
    // would answer if DSolve returned equations rather than functions.
    return DSolveVerify.acceptODE(F.list(lhs), yFunction, xVar, body, engine) ? body : F.NIL;
  }

  /**
   * The first power of something containing the unknown whose exponent is a fraction, as
   * <code>{base, exponent}</code>, or <code>null</code>.
   *
   * <p>
   * A fraction, not a symbol: an exponent which is not known to be one thing or another would make
   * the answer below one for every value of it, and the equation has a different character when it
   * is a whole number.
   */
  private static IAST rootOfLinear(IExpr expr, IExpr yDummy, EvalEngine engine) {
    if (expr.isPower() && expr.exponent().isFraction() && !expr.base().isFree(yDummy, true)) {
      return F.list(expr.base(), expr.exponent());
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        IAST found = rootOfLinear(ast.get(i), yDummy, engine);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
