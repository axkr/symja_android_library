package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Equations of the second order whose solutions can be written down with exponentials, roots and
 * integrals of them.
 *
 * <p>
 * The equation <code>y'' + p*y' + q*y == 0</code> becomes <code>z'' == r*z</code> under
 * <code>y == w*z</code> with <code>w == Exp(-Integrate(p/2))</code> and
 * <code>r == p^2/4 + p'/2 - q</code>, which leaves one rational function to work with instead of
 * two. A solution of that is <code>Exp(Integrate(w))</code> for a logarithmic derivative
 * <code>w</code> satisfying <code>w' + w^2 == r</code>, and the question is what kind of function
 * that <code>w</code> is.
 *
 * <p>
 * This is the case where it is rational. It can only become infinite where <code>r</code> does, and
 * only to half the order, so it is guessed with a part at each of those places and a polynomial
 * part whose degree the behaviour of <code>r</code> at infinity fixes.
 */
final class DSolveKovacic {

  private DSolveKovacic() {}

  /** How many places <code>r</code> may become infinite at, and how far its denominator reaches. */
  private static final int MAX_POLES = 6, MAX_DENOMINATOR_DEGREE = 8;

  /** How far the polynomial part of the guess reaches. */
  private static final int MAX_POLYNOMIAL_DEGREE = 8;

  /** How big an integral is still worth carrying. */
  private static final int MAX_LEAF_COUNT = 400;

  /** The general solution, or {@link F#NIL} if it is not of this kind. */
  static IExpr solve(LinearODEForm lf, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (lf.order != 2 || !lf.g.isZero() || lf.a[2].isZero() || lf.constantCoefficients) {
      return F.NIL;
    }
    IExpr p = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[1], lf.a[2]))));
    IExpr q = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[0], lf.a[2]))));

    // y == w*z takes the first derivative out of the equation and leaves z'' == r*z.
    IExpr halfP = engine.evaluate(F.Divide(p, F.C2));
    IExpr integral = ctx.integrate(halfP, xVar, MAX_LEAF_COUNT);
    if (integral.isNIL()
        || !DSolveODE.isVanishing(engine.evaluate(F.Subtract(F.D(integral, xVar), halfP)), engine)) {
      return F.NIL;
    }
    IExpr recovery = engine.evaluate(F.Exp(F.Negate(integral)));
    IExpr r = engine.evaluate(F.Cancel(F.Together(
        F.Subtract(F.Plus(F.Divide(F.Sqr(p), F.C4), F.Divide(F.D(p, xVar), F.C2)), q))));

    IExpr together = engine.evaluate(F.Together(r));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (!engine.evaluate(F.PolynomialQ(numerator, xVar)).isTrue()
        || !engine.evaluate(F.PolynomialQ(denominator, xVar)).isTrue()) {
      return F.NIL;
    }
    // A coefficient carrying a symbol of its own leaves the equations for the unknowns of the
    // guess with two kinds of unknown in them, which Solve does not finish eliminating.
    if (!engine.evaluate(F.NumberQ(F.subst(r, xVar, F.QQ(17, 13)))).isTrue()) {
      return F.NIL;
    }

    IExpr factorList = engine.evaluate(F.FactorList(denominator));
    if (!factorList.isList()) {
      return F.NIL;
    }
    IAST places = (IAST) factorList;
    int poles = 0;
    for (int i = 1; i <= places.argSize(); i++) {
      IExpr entry = places.get(i);
      if (entry.isList() && entry.size() > 1 && !entry.first().isFree(xVar)) {
        poles++;
      }
    }
    int denominatorDegree = engine.evaluate(F.Exponent(denominator, xVar)).toIntDefault();
    if (poles > MAX_POLES || denominatorDegree > MAX_DENOMINATOR_DEGREE
        || denominatorDegree < 0) {
      return F.NIL;
    }
    int numeratorDegree = engine.evaluate(F.Exponent(numerator, xVar)).toIntDefault();
    if (numeratorDegree < 0) {
      return F.NIL;
    }
    int difference = numeratorDegree - denominatorDegree;
    int polynomialDegree = difference > 0 ? (difference + 1) / 2 : 0;
    if (polynomialDegree > MAX_POLYNOMIAL_DEGREE) {
      polynomialDegree = MAX_POLYNOMIAL_DEGREE;
    }

    IASTAppendable unknowns = F.ListAlloc();
    IExpr guess = ansatz(places, xVar, polynomialDegree, unknowns, engine);
    IExpr equation = engine.evaluate(
        F.Subtract(F.Plus(F.D(guess, xVar), F.Sqr(guess)), r));
    IExpr logarithmicDerivative = fit(equation, guess, unknowns, r, xVar, ctx);
    if (logarithmicDerivative.isNIL()) {
      return F.NIL;
    }

    IExpr z1 = expIntegral(logarithmicDerivative, xVar, ctx);
    if (z1.isNIL()) {
      return F.NIL;
    }
    IExpr z2 = secondSolution(z1, xVar, ctx);
    if (z2.isNIL() || !independent(z1, z2, xVar, engine)) {
      return F.NIL;
    }
    return assemble(z1, z2, recovery, c_n, ctx);
  }

  /**
   * A polynomial of the given degree plus a part at each place <code>r</code> becomes infinite.
   *
   * <p>
   * A place where <code>r</code> has a pole of order <code>o</code> is one where the guess may have
   * one of order <code>Ceiling(o/2)</code>, because squaring it is what has to produce the pole of
   * <code>r</code>.
   */
  private static IExpr ansatz(IAST places, IExpr xVar, int degree, IASTAppendable unknowns,
      EvalEngine engine) {
    IASTAppendable terms = F.PlusAlloc(degree + 1);
    for (int j = 0; j <= degree; j++) {
      IExpr unknown = F.Dummy("kv" + unknowns.argSize());
      unknowns.append(unknown);
      terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(j))));
    }
    for (int i = 1; i <= places.argSize(); i++) {
      IExpr entry = places.get(i);
      if (!entry.isList() || entry.argSize() != 2) {
        continue;
      }
      IExpr place = entry.first();
      int placeDegree = engine.evaluate(F.Exponent(place, xVar)).toIntDefault();
      if (placeDegree < 1) {
        continue;
      }
      int order = entry.second().toIntDefault();
      if (order < 1) {
        order = 1;
      }
      for (int k = 1; k <= (order + 1) / 2; k++) {
        for (int l = 0; l < placeDegree; l++) {
          IExpr unknown = F.Dummy("kv" + unknowns.argSize());
          unknowns.append(unknown);
          terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(l)), F.Power(place, F.ZZ(-k))));
        }
      }
    }
    return engine.evaluate(terms);
  }

  /**
   * The guess made to satisfy the equation, if it can be.
   *
   * <p>
   * Unknowns the solution does not fix are set to zero, which is a choice; it is safe here because
   * what comes out is put back into the equation by the caller of this method in any case.
   */
  private static IExpr fit(IExpr equation, IExpr guess, IAST unknowns, IExpr r, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr numerator = engine.evaluate(F.Numerator(F.Together(equation)));
    IExpr coefficients = engine.evaluate(F.CoefficientList(numerator, xVar));
    if (!coefficients.isList()) {
      return F.NIL;
    }
    IASTAppendable equations = F.ListAlloc(((IAST) coefficients).argSize());
    for (int i = 1; i <= ((IAST) coefficients).argSize(); i++) {
      IExpr coefficient = ((IAST) coefficients).get(i);
      if (!coefficient.isZero()) {
        equations.append(F.Equal(coefficient, F.C0));
      }
    }
    if (unknowns.argSize() == 0) {
      return equations.argSize() == 0 ? guess : F.NIL;
    }
    if (equations.argSize() == 0) {
      return F.NIL;
    }
    IExpr solutions = ctx.evalTimeConstrained(F.Solve(equations, unknowns), STEP_SECONDS);
    if (solutions.isNIL() || !solutions.isList() || ((IAST) solutions).argSize() == 0) {
      return F.NIL;
    }
    IExpr branch = ((IAST) solutions).arg1();
    if (!branch.isList()) {
      return F.NIL;
    }
    IExpr fitted = engine.evaluate(F.ReplaceAll(guess, branch));
    for (int i = 1; i <= unknowns.argSize(); i++) {
      fitted = F.subst(fitted, unknowns.get(i), F.C0);
    }
    fitted = engine.evaluate(F.Cancel(F.Together(fitted)));
    // Setting the unfixed unknowns to zero is a choice rather than a consequence, so what comes
    // out is put back into the equation it had to satisfy.
    IExpr residual = engine.evaluate(
        F.Together(F.Subtract(F.Plus(F.D(fitted, xVar), F.Sqr(fitted)), r)));
    return DSolveODE.isVanishing(residual, engine) ? fitted : F.NIL;
  }

  /** How long one search step may take. */
  private static final int STEP_SECONDS = 5;

  /** <code>Exp(Integrate(w))</code>, with the antiderivative checked. */
  private static IExpr expIntegral(IExpr w, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr integral = ctx.integrate(w, xVar, MAX_LEAF_COUNT);
    if (integral.isNIL()
        || !DSolveODE.isVanishing(engine.evaluate(F.Subtract(F.D(integral, xVar), w)), engine)) {
      return F.NIL;
    }
    return engine.evaluate(F.Exp(integral));
  }

  /**
   * The second solution of <code>z'' == r*z</code> from the first.
   *
   * <p>
   * The two solutions of that equation have a constant Wronskian, which is what makes
   * <code>z1*Integrate(1/z1^2)</code> the other one.
   */
  private static IExpr secondSolution(IExpr z1, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr integral = ctx.integrate(engine.evaluate(F.Power(z1, F.CN2)), xVar, MAX_LEAF_COUNT);
    if (integral.isNIL()) {
      return F.NIL;
    }
    return foldConstantFactor(engine.evaluate(F.Times(z1, integral)), xVar, engine);
  }

  /**
   * Divides out what does not depend on the variable.
   *
   * <p>
   * The second solution is only ever seen multiplied by an arbitrary constant, and the integral
   * above tends to leave a factor in front of it which belongs in that constant rather than in the
   * answer.
   */
  private static IExpr foldConstantFactor(IExpr expr, IExpr xVar, EvalEngine engine) {
    if (!expr.isTimes()) {
      return expr;
    }
    IAST product = (IAST) expr;
    IASTAppendable content = F.TimesAlloc(product.argSize());
    boolean any = false;
    for (int i = 1; i <= product.argSize(); i++) {
      if (product.get(i).isFree(xVar)) {
        content.append(product.get(i));
        any = true;
      }
    }
    return any ? engine.evaluate(F.Divide(expr, engine.evaluate(content))) : expr;
  }

  /** Whether the two solutions are different ones, checked where a ratio can be evaluated. */
  private static boolean independent(IExpr z1, IExpr z2, IExpr xVar, EvalEngine engine) {
    IExpr slope = engine.evaluate(F.D(F.Divide(z1, z2), xVar));
    for (int i = 0; i < 3; i++) {
      IExpr at = engine.evaluate(F.N(F.Abs(F.subst(slope, xVar, F.num(0.7 + 0.6 * i)))));
      if (at.isReal() && at.evalf() > 1.0e-9) {
        return true;
      }
    }
    return false;
  }

  /**
   * The general solution, with the imaginary unit taken out of it where that can be done.
   *
   * <p>
   * A pair of solutions which are conjugates of one another spans the same thing as their sum and
   * their difference over the imaginary unit, and those two are real. This is what turns
   * <code>x*Exp(I*x)</code> and <code>x*Exp(-I*x)</code> into <code>x*Cos(x)</code> and
   * <code>x*Sin(x)</code>, which is the pair such an equation is answered with.
   */
  private static IExpr assemble(IExpr z1, IExpr z2, IExpr recovery, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr y1 = engine.evaluate(F.Simplify(F.Times(recovery, z1)));
    IExpr y2 = engine.evaluate(F.Simplify(F.Times(recovery, z2)));
    if (!isReal(y1, engine) || !isReal(y2, engine)) {
      IExpr sum = engine.evaluate(F.Simplify(F.ComplexExpand(F.Divide(F.Plus(y1, y2), F.C2))));
      IExpr difference = engine.evaluate(
          F.Simplify(F.ComplexExpand(F.Divide(F.Subtract(y1, y2), F.Times(F.C2, F.CI)))));
      if (isReal(sum, engine) && isReal(difference, engine) && !sum.isZero()
          && !difference.isZero()) {
        y1 = sum;
        y2 = difference;
      }
    }
    IExpr general = engine.evaluate(
        F.Plus(F.Times(c_n, y1), F.Times(ctx.nextConstant(), y2)));
    IExpr simplified = engine.evaluate(F.Simplify(general));
    return simplified.isPresent() ? simplified : general;
  }

  /** Whether the expression is free of the imaginary unit. */
  private static boolean isReal(IExpr expr, EvalEngine engine) {
    return expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), true);
  }
}
