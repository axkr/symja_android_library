package org.matheclipse.core.dsolve;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Nonlinear equations of the second order which can be integrated once.
 *
 * <p>
 * An equation <code>y'' == Phi(x, y, y')</code> is integrated once if some <code>mu</code> makes
 * <code>mu*(y'' - Phi)</code> the derivative of something: then that something,
 * <code>R(x, y, y') == C(1)</code>, is a first order equation, and solving it solves the original.
 * Writing out what it means for <code>R</code> to have that derivative gives
 * <code>mu == dR/dy'</code> and the condition <code>R_x + y'*R_y + Phi*R_y' == 0</code>, and the
 * question becomes whether a <code>mu</code> of a restricted form exists.
 *
 * <p>
 * The form taken here is <code>mu(x, y)</code>, with <code>Phi</code> a polynomial of degree at
 * most two in <code>y'</code>, which is the first of the three cases in Cheb-Terrab and Roche's
 * classification of the integrating factors a second order equation can have. Writing
 * <code>Phi == a*y'^2 + b*y' + c</code>, the two cases are told apart by whether
 * <code>2*a_x - b_y</code> vanishes: one gives <code>mu</code> in closed form, the other gives it
 * as a solution of one linear equation of the second order, which the cascade is asked for.
 *
 * <p>
 * A wrong <code>mu</code> can only ever make this decline. Before anything is solved, the
 * <code>R</code> built from it has to satisfy the condition above symbolically -- that is the
 * paper's own exactness test -- and the solution which comes back at the end has to be seen to
 * solve the original equation numerically.
 *
 * <p>
 * Nonlinear equations only. A linear one has an integrating factor as well, the methods for those
 * answer it better, and letting this recognize one would also keep it from ending: the second case
 * asks the cascade for a linear equation of the same order.
 */
final class DSolveIntegratingFactor {

  private DSolveIntegratingFactor() {}

  /** How long the whole search may take. */
  private static final int DEADLINE_SECONDS = 6;

  /** How long one of the quadratures may take. */
  private static final int INTEGRATE_SECONDS = 2;

  /** How long the equation for the second case may be solved for. */
  private static final int SUB_SOLVE_SECONDS = 3;

  /** How big any of the expressions along the way may become. */
  private static final int MAX_LEAF_COUNT = 400;

  /** Where the answer is put back into the equation it came from. */
  private static final int[][] SAMPLES =
      new int[][] {{17, 10}, {23, 10}, {23, 20}, {3, 5}, {31, 10}};

  /**
   * The general solution of the equation, or {@link F#NIL} if no integrating factor of this form
   * exists, or if the first order equation it leaves cannot be solved.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr yDummy = F.Dummy("Y");
    IExpr pDummy = F.Dummy("p");
    IExpr field =
        DSolveUtil.solveForSecondDerivative(lhs, yFunction, xVar, yDummy, pDummy, engine);
    if (field.isNIL() || field.leafCount() > MAX_LEAF_COUNT
        || DSolveUtil.hasUndefinedFunction(field)) {
      return F.NIL;
    }
    // A linear equation is not this method's, and letting it in would keep the second case below
    // from ending: it asks the cascade for a linear equation of the second order.
    IExpr byFunction = engine.evaluate(F.D(field, yDummy));
    IExpr byDerivative = engine.evaluate(F.D(field, pDummy));
    if (byFunction.isFree(yDummy, true) && byFunction.isFree(pDummy, true)
        && byDerivative.isFree(yDummy, true) && byDerivative.isFree(pDummy, true)) {
      return F.NIL;
    }
    if (!engine.evaluate(F.PolynomialQ(field, pDummy)).isTrue()
        || engine.evaluate(F.Exponent(field, pDummy)).toIntDefault() > 2) {
      return F.NIL;
    }
    IExpr a = engine.evaluate(F.Coefficient(field, pDummy, F.C2));
    IExpr b = engine.evaluate(F.Coefficient(field, pDummy, F.C1));
    IExpr c = engine.evaluate(F.Coefficient(field, pDummy, F.C0));

    ctx.startDeadline(DEADLINE_SECONDS);
    try {
      IExpr factor = integratingFactor(a, b, c, xVar, yDummy, ctx);
      if (factor == null || ctx.expired()) {
        return F.NIL;
      }
      IExpr firstIntegral = firstIntegral(factor, field, a, b, c, xVar, yDummy, pDummy, ctx);
      if (firstIntegral == null || ctx.expired()) {
        return F.NIL;
      }
      return reduce(firstIntegral, lhs, yFunction, xVar, yDummy, pDummy, c_n, ctx);
    } finally {
      ctx.clearDeadline();
    }
  }

  /**
   * An integrating factor <code>mu(x, y)</code> of the equation, or <code>null</code>.
   *
   * <p>
   * Both cases are Cheb-Terrab and Roche's, written in the notation of their paper: the first is
   * their (2.15) to (2.17), the second their (2.18) to (2.21). Each carries a condition for the
   * factor to exist which is checked before it is built.
   */
  private static IExpr integratingFactor(IExpr a, IExpr b, IExpr c, IExpr xVar, IExpr yDummy,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr discriminant = engine.evaluate(
        F.Subtract(F.Times(F.C2, F.D(a, xVar)), F.D(b, yDummy)));
    IExpr overFunction = ctx.integrate(a, yDummy, MAX_LEAF_COUNT, INTEGRATE_SECONDS);
    if (overFunction.isNIL()) {
      return null;
    }
    IExpr mixed = engine.evaluate(F.D(overFunction, xVar));

    IExpr factor;
    if (!DSolveODE.isVanishing(discriminant, engine)) {
      IExpr phi = engine.evaluate(F.Subtract(F.Subtract(F.D(c, yDummy), F.Times(a, c)),
          F.D(b, xVar)));
      IExpr upsilon = engine.evaluate(F.Plus(F.D(a, F.List(xVar, F.C2)),
          F.Times(F.D(a, xVar), b), F.D(phi, yDummy)));
      if (upsilon.leafCount() > MAX_LEAF_COUNT) {
        return null;
      }
      boolean exists = DSolveODE
          .isVanishing(engine.evaluate(F.Subtract(F.D(upsilon, yDummy), F.D(a, xVar))), engine)
          && DSolveODE.isVanishing(engine.evaluate(F.Plus(F.D(upsilon, xVar), phi,
              F.Times(b, upsilon), F.Negate(F.Sqr(upsilon)))), engine);
      if (!exists) {
        return null;
      }
      IExpr exponent = ctx.integrate(engine.evaluate(F.Plus(F.Negate(upsilon), mixed)), xVar,
          MAX_LEAF_COUNT, INTEGRATE_SECONDS);
      if (exponent.isNIL()) {
        return null;
      }
      factor = engine.evaluate(F.Exp(F.Subtract(exponent, overFunction)));
    } else {
      IExpr phi = engine.evaluate(F.Subtract(F.D(c, yDummy), F.Times(a, c)));
      if (!DSolveODE.isVanishing(engine.evaluate(F.Subtract(
          F.Subtract(F.D(a, F.List(xVar, F.C2)), F.Times(F.D(a, xVar), b)), F.D(phi, yDummy))),
          engine)) {
        return null;
      }
      IExpr difference = engine.evaluate(F.Subtract(b, mixed));
      IExpr first = engine.evaluate(F.Subtract(F.Times(F.C2, mixed), b));
      IExpr zeroth = engine.evaluate(F.Subtract(F.Plus(phi, F.Times(mixed, difference)),
          F.D(difference, xVar)));
      if (!first.isFree(yDummy, true) || !zeroth.isFree(yDummy, true)) {
        // The equation for the factor would not be one in x alone.
        return null;
      }
      IExpr basis = oneSolutionOf(first, zeroth, xVar, ctx);
      if (basis == null) {
        return null;
      }
      factor = engine.evaluate(F.Times(basis, F.Exp(F.Negate(overFunction))));
    }
    return factor.isZero() || factor.leafCount() > MAX_LEAF_COUNT ? null : factor;
  }

  /**
   * One solution of <code>v''(x) - first*v'(x) - zeroth*v(x) == 0</code>, or <code>null</code>.
   *
   * <p>
   * Any one of them will do -- the factor only has to exist, not to be the general one -- so the
   * first arbitrary constant is set to one and the rest to zero.
   */
  private static IExpr oneSolutionOf(IExpr first, IExpr zeroth, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr unknown = F.unaryAST1(F.Dummy("v"), xVar);
    IExpr equation = F.Equal(F.Plus(F.D(unknown, F.List(xVar, F.C2)),
        F.Times(F.CN1, first, F.D(unknown, xVar)), F.Times(F.CN1, zeroth, unknown)), F.C0);
    IExpr solutions = ctx.evalTimeConstrained(
        F.DSolve(F.List(equation), F.List(unknown), xVar), SUB_SOLVE_SECONDS);
    if (solutions.isNIL()) {
      return null;
    }
    IAST extracted = DSolveUtil.extractSolveResults(solutions);
    if (extracted.argSize() == 0) {
      return null;
    }
    IExpr body = extracted.arg1();
    if (body.isAST(S.Function)) {
      body = engine.evaluate(F.unaryAST1(body, xVar));
    }
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(body, constants);
    IASTAppendable rules = F.ListAlloc(constants.argSize());
    for (int i = 1; i <= constants.argSize(); i++) {
      rules.append(F.Rule(constants.get(i), i == 1 ? F.C1 : F.C0));
    }
    body = engine.evaluate(F.subst(body, rules));
    return body.isZero() || !DSolveContext.isUsable(body) ? null : body;
  }

  /**
   * The <code>R</code> with <code>mu == dR/dy'</code> whose derivative along the equation
   * vanishes, or <code>null</code> if there is none.
   *
   * <p>
   * <code>R</code> is <code>Integrate(mu, y')</code> plus a function of <code>x</code> and
   * <code>y</code>, and that function is what the condition determines: it is an exact equation in
   * two variables, solved by the two quadratures below.
   */
  private static IExpr firstIntegral(IExpr factor, IExpr field, IExpr a, IExpr b, IExpr c,
      IExpr xVar, IExpr yDummy, IExpr pDummy, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    // The factor does not contain the derivative, so its integral over it is a product.
    IExpr overDerivative = engine.evaluate(F.Times(factor, pDummy));
    IExpr condition = engine.evaluate(F.Cancel(F.Together(F.Plus( //
        F.D(overDerivative, xVar), //
        F.Times(pDummy, F.D(overDerivative, yDummy)), //
        F.Times(field, factor)))));
    if (condition.leafCount() > MAX_LEAF_COUNT
        || !DSolveODE.isVanishing(engine.evaluate(F.D(condition, F.List(pDummy, F.C2))), engine)) {
      // What is left has to be of the first degree in the derivative, or the factor is not of
      // the form this method is about.
      return null;
    }
    IExpr overFunctionDerivative = engine.evaluate(F.Negate(F.D(condition, pDummy)));
    IExpr overVariable = engine.evaluate(F.Negate(F.subst(condition, pDummy, F.C0)));
    IExpr first = ctx.integrate(overVariable, xVar, MAX_LEAF_COUNT, INTEGRATE_SECONDS);
    if (first.isNIL()) {
      return null;
    }
    IExpr second = ctx.integrate(
        engine.evaluate(F.Subtract(overFunctionDerivative, F.D(first, yDummy))), yDummy,
        MAX_LEAF_COUNT, INTEGRATE_SECONDS);
    if (second.isNIL()) {
      return null;
    }
    IExpr integral = engine.evaluate(F.Plus(overDerivative, first, second));
    if (integral.isFree(pDummy, true) || integral.leafCount() > MAX_LEAF_COUNT) {
      return null;
    }
    // The paper's exactness test, and the reason a wrong factor can only make this decline.
    IExpr derivativeAlong = engine.evaluate(F.Plus(F.D(integral, xVar),
        F.Times(pDummy, F.D(integral, yDummy)), F.Times(field, F.D(integral, pDummy))));
    return DSolveODE.isVanishing(derivativeAlong, engine) ? integral : null;
  }

  /**
   * The solutions of the first order equation <code>R == C(1)</code>, put back into the original
   * equation to see that they solve it.
   */
  private static IExpr reduce(IExpr integral, IExpr lhs, IExpr yFunction, IExpr xVar,
      IExpr yDummy, IExpr pDummy, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    // R is of the first degree in the derivative, so solving R == C(1) for it is a division.
    IExpr slope = engine.evaluate(F.D(integral, pDummy));
    if (slope.isZero()) {
      return F.NIL;
    }
    IExpr constantPart = engine.evaluate(F.subst(integral, pDummy, F.C0));
    IExpr right = engine.evaluate(
        F.Cancel(F.Together(F.Divide(F.Subtract(c_n, constantPart), slope))));
    right = engine.evaluate(F.subst(right, yDummy, yFunction));
    if (!right.isFree(yDummy, true) || !right.isFree(pDummy, true)) {
      return F.NIL;
    }
    IExpr equation =
        F.Equal(F.Subtract(engine.evaluate(F.D(yFunction, xVar)), right), F.C0);
    IAST branches = DSolveODE.solveSubODE(equation, xVar, yFunction, ctx.nextConstant(), ctx);

    IAST residuals = F.list(lhs);
    IASTAppendable samples = F.ListAlloc(SAMPLES.length);
    for (int[] fraction : SAMPLES) {
      samples.append(F.QQ(fraction[0], fraction[1]));
    }
    IASTAppendable accepted = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr body = branches.get(i);
      if (body.isNIL() || !body.isFree(yDummy, true) || !body.isFree(pDummy, true)
          || !DSolveContext.isUsable(body) || body.leafCount() > MAX_LEAF_COUNT) {
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
}
