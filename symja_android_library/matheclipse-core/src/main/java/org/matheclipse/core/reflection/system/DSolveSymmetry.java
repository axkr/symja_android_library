package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Nonlinear equations of the second order, solved by finding a symmetry of them.
 *
 * <p>
 * A point symmetry is a pair of infinitesimals <code>xi(x,y)</code>, <code>eta(x,y)</code> whose
 * flow carries solutions of the equation to solutions of it. Written for
 * <code>y'' == Phi(x,y,p)</code> with <code>p == y'</code>, that is one linear condition on the
 * pair, and looking for them among the polynomials of low degree turns it into a homogeneous linear
 * system whose null space is a basis of the symmetries the equation has.
 *
 * <p>
 * A symmetry is worth having because coordinates can be chosen in which it is a translation. In
 * those coordinates the equation cannot depend on one of them, so it is of the first order in
 * <code>q == ds/dr</code>, and the cascade is asked for it. Integrating <code>q</code> and undoing
 * the change of coordinates gives the solution.
 *
 * <p>
 * This is a search rather than a recognition, so every branch of the inversion at the end is
 * required to be seen to solve the equation numerically. Keeping what merely cannot be disproved
 * would return one of the wrong branches, which is how the same method in mathilda shipped two
 * wrong answers before the check was added.
 */
final class DSolveSymmetry {

  private DSolveSymmetry() {}

  /** How long the whole search may take. */
  private static final int DEADLINE_SECONDS = 8;

  /** How long one of the evaluations inside it may take. */
  private static final int STEP_SECONDS = 3;

  /** The highest total degree of the polynomials the infinitesimals are looked for among. */
  private static final int MAX_DEGREE = 2;

  private static final int MAX_PHI_LEAF_COUNT = 250;

  private static final int MAX_CONDITION_LEAF_COUNT = 20000;

  private static final int MAX_COORDINATE_LEAF_COUNT = 400;

  private static final int MAX_REDUCED_LEAF_COUNT = 2000;

  private static final int MAX_BODY_LEAF_COUNT = 600;

  /**
   * The general solution of the equation, or {@link F#NIL} if no symmetry of the kind looked for
   * here reduces it.
   */
  static IExpr solveSecondOrder(IExpr lhs, IExpr yFunction, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr head = yFunction.head();
    IExpr second = engine.evaluate(F.D(yFunction, F.List(xVar, F.C2)));
    IExpr coefficient = engine.evaluate(F.Coefficient(lhs, second));
    // The coefficient of the second derivative may depend on the unknown, as it does in
    // 2*x^2*y''(x)*y(x) + ... == ...; all that is needed is to be able to solve for that
    // derivative, which asks for the equation to be of the first degree in it.
    if (coefficient.isZero() || !DSolveODE.isLinearInDerivative(lhs, second, engine)) {
      return F.NIL;
    }
    IExpr yDummy = F.Dummy("Y");
    IExpr pDummy = F.Dummy("p");
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Times(coefficient, second)));
    IExpr field = engine.evaluate(F.Cancel(F.Together(F.Divide(F.Negate(rest), coefficient))));
    // The first derivative is replaced before the function itself, or the function would be found
    // inside the derivative.
    field = engine.evaluate(F.subst(field, engine.evaluate(F.D(yFunction, xVar)), pDummy));
    field = engine.evaluate(F.subst(field, yFunction, yDummy));
    if (!field.isFree(head, true) || field.leafCount() > MAX_PHI_LEAF_COUNT
        || hasUndefinedFunction(field)) {
      return F.NIL;
    }
    IAST variables = F.List(xVar, yDummy, pDummy);
    IExpr together = engine.evaluate(F.Together(field));
    if (!engine.evaluate(F.PolynomialQ(F.Numerator(together), variables)).isTrue()
        || !engine.evaluate(F.PolynomialQ(F.Denominator(together), variables)).isTrue()) {
      // The condition would not split into finitely many coefficients.
      return F.NIL;
    }

    ctx.startDeadline(DEADLINE_SECONDS);
    try {
      for (int degree = 1; degree <= MAX_DEGREE && !ctx.expired(); degree++) {
        List<IExpr[]> symmetries = findSymmetries(field, degree, xVar, yDummy, pDummy, ctx);
        for (IExpr[] symmetry : symmetries) {
          if (ctx.expired()) {
            return F.NIL;
          }
          IExpr body = reduce(lhs, symmetry[0], symmetry[1], field, yFunction, xVar, yDummy,
              pDummy, c_n, ctx);
          if (body.isPresent()) {
            return body;
          }
        }
      }
      return F.NIL;
    } finally {
      ctx.clearDeadline();
    }
  }

  /**
   * The symmetries whose infinitesimals are polynomials of the given total degree, as pairs
   * <code>{xi, eta}</code>.
   */
  private static List<IExpr[]> findSymmetries(IExpr field, int degree, IExpr xVar, IExpr yDummy,
      IExpr pDummy, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    List<IExpr[]> found = new ArrayList<>();
    int monomials = (degree + 1) * (degree + 2) / 2;
    IExpr[] unknowns = new IExpr[2 * monomials];
    for (int k = 0; k < unknowns.length; k++) {
      unknowns[k] = F.Dummy("b" + k);
    }
    IExpr xi = polynomial(unknowns, 0, degree, xVar, yDummy, engine);
    IExpr eta = polynomial(unknowns, monomials, degree, xVar, yDummy, engine);

    IExpr condition = symmetryCondition(xi, eta, field, xVar, yDummy, pDummy, engine);
    if (condition.isNIL() || condition.leafCount() > MAX_CONDITION_LEAF_COUNT) {
      return found;
    }
    IExpr numerator = ctx.evalTimeConstrained(F.Numerator(F.Together(condition)), STEP_SECONDS);
    if (numerator.isNIL() || numerator.leafCount() > MAX_CONDITION_LEAF_COUNT) {
      return found;
    }
    IAST variables = F.List(xVar, yDummy, pDummy);
    IExpr forms = ctx.evalTimeConstrained(F.Flatten(F.CoefficientList(numerator, variables)),
        STEP_SECONDS);
    if (forms.isNIL() || !forms.isList()) {
      return found;
    }
    IASTAppendable unknownList = F.ListAlloc(unknowns.length);
    for (IExpr unknown : unknowns) {
      unknownList.append(unknown);
    }
    IExpr matrix = ctx.evalTimeConstrained(
        F.ternaryAST3(S.Outer, S.Coefficient, forms, unknownList), STEP_SECONDS);
    if (matrix.isNIL() || !matrix.isList() || !matrix.isFree(xVar) || !matrix.isFree(yDummy, true)
        || !matrix.isFree(pDummy, true)) {
      // The condition is not linear and homogeneous in the unknowns, so this is not a system.
      return found;
    }
    IExpr nullSpace = ctx.evalTimeConstrained(F.NullSpace(matrix), STEP_SECONDS);
    if (nullSpace.isNIL() || !nullSpace.isList()) {
      return found;
    }
    IAST vectors = (IAST) nullSpace;
    for (int i = 1; i <= vectors.argSize(); i++) {
      IExpr vector = vectors.get(i);
      if (!vector.isList() || ((IAST) vector).argSize() != unknowns.length) {
        continue;
      }
      IExpr[] coefficients = new IExpr[unknowns.length];
      for (int k = 0; k < unknowns.length; k++) {
        coefficients[k] = ((IAST) vector).get(k + 1);
      }
      IExpr xiValue = polynomial(coefficients, 0, degree, xVar, yDummy, engine);
      IExpr etaValue = polynomial(coefficients, monomials, degree, xVar, yDummy, engine);
      if (xiValue.isZero() && etaValue.isZero()) {
        continue;
      }
      found.add(new IExpr[] {xiValue, etaValue});
    }
    return found;
  }

  /**
   * The condition a point symmetry satisfies, which is the second prolongation of the infinitesimal
   * generator applied to the equation.
   */
  private static IExpr symmetryCondition(IExpr xi, IExpr eta, IExpr field, IExpr xVar,
      IExpr yDummy, IExpr pDummy, EvalEngine engine) {
    try {
      IExpr etaX = engine.evaluate(F.D(eta, xVar));
      IExpr etaY = engine.evaluate(F.D(eta, yDummy));
      IExpr etaXX = engine.evaluate(F.D(eta, F.List(xVar, F.C2)));
      IExpr etaXY = engine.evaluate(F.D(eta, F.List(xVar, F.C1), F.List(yDummy, F.C1)));
      IExpr etaYY = engine.evaluate(F.D(eta, F.List(yDummy, F.C2)));
      IExpr xiX = engine.evaluate(F.D(xi, xVar));
      IExpr xiY = engine.evaluate(F.D(xi, yDummy));
      IExpr xiXX = engine.evaluate(F.D(xi, F.List(xVar, F.C2)));
      IExpr xiXY = engine.evaluate(F.D(xi, F.List(xVar, F.C1), F.List(yDummy, F.C1)));
      IExpr xiYY = engine.evaluate(F.D(xi, F.List(yDummy, F.C2)));
      IExpr fieldX = engine.evaluate(F.D(field, xVar));
      IExpr fieldY = engine.evaluate(F.D(field, yDummy));
      IExpr fieldP = engine.evaluate(F.D(field, pDummy));

      // The second prolongation, once y'' has been replaced by the equation itself.
      IExpr prolongation = F.Plus(etaXX, //
          F.Times(F.Subtract(F.Times(F.C2, etaXY), xiXX), pDummy), //
          F.Times(F.Subtract(etaYY, F.Times(F.C2, xiXY)), F.Sqr(pDummy)), //
          F.Times(F.CN1, xiYY, F.Power(pDummy, F.C3)), //
          F.Times(F.Subtract(F.Subtract(etaY, F.Times(F.C2, xiX)),
              F.Times(F.C3, xiY, pDummy)), field));
      // The first prolongation, which is what the derivative of the equation is contracted with.
      IExpr first = F.Plus(etaX, F.Times(F.Subtract(etaY, xiX), pDummy),
          F.Times(F.CN1, xiY, F.Sqr(pDummy)));
      return engine.evaluate(F.Plus(prolongation, //
          F.Times(F.CN1, xi, fieldX), //
          F.Times(F.CN1, eta, fieldY), //
          F.Times(F.CN1, first, fieldP)));
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** A general polynomial of the given total degree in the two variables. */
  private static IExpr polynomial(IExpr[] coefficients, int offset, int degree, IExpr xVar,
      IExpr yDummy, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc((degree + 1) * (degree + 2) / 2);
    int k = offset;
    for (int total = 0; total <= degree; total++) {
      for (int i = total; i >= 0; i--) {
        int j = total - i;
        sum.append(F.Times(coefficients[k++], F.Power(xVar, F.ZZ(i)), F.Power(yDummy, F.ZZ(j))));
      }
    }
    return engine.evaluate(sum);
  }

  /**
   * Reduces the equation with one symmetry and solves what is left, or answers {@link F#NIL} if any
   * step of that does not come out in a form which can be carried on with.
   */
  private static IExpr reduce(IExpr lhs, IExpr xi, IExpr eta, IExpr field, IExpr yFunction,
      IExpr xVar, IExpr yDummy, IExpr pDummy, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr rSymbol = F.Dummy("r");
    IExpr qSymbol = F.Dummy("q");

    // Coordinates in which the symmetry is a translation: it leaves r alone and shifts s by one.
    IExpr r;
    IExpr s;
    IExpr yOfR = F.NIL;
    if (eta.isZero()) {
      r = yDummy;
      s = ctx.integrate(engine.evaluate(F.Divide(F.C1, xi)), xVar, MAX_COORDINATE_LEAF_COUNT);
    } else if (xi.isZero()) {
      r = xVar;
      s = ctx.integrate(engine.evaluate(F.Divide(F.C1, eta)), yDummy, MAX_COORDINATE_LEAF_COUNT);
    } else {
      IExpr constant = F.Dummy("c");
      IExpr slope = engine.evaluate(F.Cancel(F.Together(F.Divide(eta, xi))));
      IExpr equation = F.Equal(F.Subtract(engine.evaluate(F.D(yFunction, xVar)),
          F.subst(slope, yDummy, yFunction)), F.C0);
      IAST curves = DSolveODE.solveSubODE(equation, xVar, yFunction, constant, ctx);
      if (curves.argSize() == 0 || ctx.expired()) {
        return F.NIL;
      }
      r = solveFor(F.Equal(curves.arg1(), yDummy), constant, ctx);
      if (r.isNIL()) {
        return F.NIL;
      }
      yOfR = solveFor(F.Equal(r, rSymbol), yDummy, ctx);
      IExpr integrand = engine.evaluate(F.Divide(F.C1, xi));
      if (yOfR.isPresent()) {
        integrand = engine.evaluate(F.subst(integrand, yDummy, yOfR));
      }
      IExpr integral = ctx.integrate(integrand, xVar, MAX_COORDINATE_LEAF_COUNT);
      s = integral.isNIL() ? F.NIL : engine.evaluate(F.subst(integral, rSymbol, r));
    }
    if (s.isNIL() || r.isNIL() || s.leafCount() > MAX_COORDINATE_LEAF_COUNT) {
      return F.NIL;
    }

    // In those coordinates the equation is of the first order in q == ds/dr.
    IExpr rate = engine.evaluate(F.Plus(F.D(r, xVar), F.Times(F.D(r, yDummy), pDummy)));
    if (rate.isZero()) {
      return F.NIL;
    }
    IExpr q = engine.evaluate(F.Cancel(F.Together(F.Divide(
        F.Plus(F.D(s, xVar), F.Times(F.D(s, yDummy), pDummy)), rate))));
    IExpr slope = engine.evaluate(F.Cancel(F.Together(F.Divide(
        F.Plus(F.D(q, xVar), F.Times(F.D(q, yDummy), pDummy), F.Times(F.D(q, pDummy), field)),
        rate))));
    if (q.leafCount() > MAX_REDUCED_LEAF_COUNT || slope.leafCount() > MAX_REDUCED_LEAF_COUNT
        || ctx.expired()) {
      return F.NIL;
    }

    IExpr pOfQ = solveFor(F.Equal(q, qSymbol), pDummy, ctx);
    if (pOfQ.isNIL()) {
      return F.NIL;
    }
    IExpr reduced = engine.evaluate(F.subst(slope, pDummy, pOfQ));
    if (r.equals(xVar)) {
      reduced = ctx.evalTimeConstrained(F.Simplify(F.subst(reduced, xVar, rSymbol)), STEP_SECONDS);
      if (reduced.isNIL() || !reduced.isFree(yDummy, true)) {
        // What is left has to be a function of r and q alone; that it is, is the symmetry.
        return F.NIL;
      }
    } else {
      if (yOfR.isNIL()) {
        yOfR = solveFor(F.Equal(r, rSymbol), yDummy, ctx);
        if (yOfR.isNIL()) {
          return F.NIL;
        }
      }
      reduced = ctx.evalTimeConstrained(F.Simplify(F.subst(reduced, yDummy, yOfR)), STEP_SECONDS);
      if (reduced.isNIL() || !reduced.isFree(xVar)) {
        return F.NIL;
      }
    }

    // One equation of the first order, which the cascade is asked for.
    IExpr qFunction = F.unaryAST1(F.Dummy("qf"), rSymbol);
    IExpr qEquation = F.Equal(F.Subtract(engine.evaluate(F.D(qFunction, rSymbol)),
        F.subst(reduced, qSymbol, qFunction)), F.C0);
    IAST rates = DSolveODE.solveSubODE(qEquation, rSymbol, qFunction, c_n, ctx);
    if (rates.argSize() == 0 || ctx.expired()) {
      return F.NIL;
    }

    for (int i = 1; i <= rates.argSize(); i++) {
      IExpr integral = ctx.integrate(rates.get(i), rSymbol, MAX_COORDINATE_LEAF_COUNT);
      if (integral.isNIL()) {
        continue;
      }
      IExpr constant = ctx.nextConstant();
      IExpr relation = F.Equal(s, engine.evaluate(
          F.subst(F.Plus(integral, constant), rSymbol, r)));
      IExpr solutions = ctx.evalTimeConstrained(F.Solve(relation, yDummy), STEP_SECONDS);
      if (solutions.isNIL()) {
        continue;
      }
      IAST candidates = DSolveUtil.extractSolveResults(solutions);
      for (int j = 1; j <= candidates.argSize(); j++) {
        IExpr body = candidates.get(j);
        if (body.isFree(xVar) || body.leafCount() > MAX_BODY_LEAF_COUNT
            || !body.isFree(x -> x.isAST(S.Solve) || x.isAST(S.Integrate) || x.isAST(S.Root),
                true)) {
          continue;
        }
        if (DSolveVerify.acceptODEStrict(F.List(lhs), yFunction, xVar, body, engine)) {
          return renumberConstants(body, c_n, engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Numbers the arbitrary constants of the answer from the one this call was given.
   *
   * <p>
   * The search solves equations of its own along the way, and those take constants out of the same
   * supply, so what is left in the answer is not the first two of them.
   */
  private static IExpr renumberConstants(IExpr body, IExpr c_n, EvalEngine engine) {
    int first = c_n.isAST(S.C, 2) ? c_n.first().toIntDefault() : -1;
    if (first < 0) {
      return body;
    }
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(body, constants);
    IASTAppendable rules = F.ListAlloc(constants.argSize());
    for (int i = 1; i <= constants.argSize(); i++) {
      rules.append(F.Rule(constants.get(i), F.C(first + i - 1)));
    }
    return rules.argSize() == 0 ? body : engine.evaluate(F.subst(body, rules));
  }

  /** The first solution of one equation for one unknown. */
  private static IExpr solveFor(IExpr equation, IExpr unknown, DSolveContext ctx) {
    IExpr solutions = ctx.evalTimeConstrained(F.Solve(equation, unknown), STEP_SECONDS);
    if (solutions.isNIL()) {
      return F.NIL;
    }
    IAST values = DSolveUtil.extractSolveResults(solutions);
    return values.argSize() == 0 ? F.NIL : values.arg1();
  }

  /**
   * Whether the equation mentions a function which is not defined, whose symmetries cannot be
   * looked for among polynomials and whose quadratures would not close.
   */
  private static boolean hasUndefinedFunction(IExpr expr) {
    return !expr.isFree(x -> x.isAST() && (x.head().isAST(S.Derivative)
        || (x.head().isSymbol() && !x.head().isBuiltInSymbol())), true);
  }
}
