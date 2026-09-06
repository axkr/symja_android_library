package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * The linear equations of second order whose solutions are named functions.
 *
 * <p>
 * Each of these is recognized by putting the equation in the normal form
 * <code>y''(x) + P(x)*y'(x) + Q(x)*y(x) == 0</code> and reading the parameters of one shape of
 * <code>P</code> and <code>Q</code> off. The rows are tried in an order in which no two of them
 * claim the same equation: Airy's equation is also of the form the pure power row looks for, so
 * that row has to come later, and the row for Legendre's equation comes first because its
 * <code>P</code> is neither <code>0</code> nor <code>1/x</code>, which is what every row below it
 * requires.
 *
 * <p>
 * A row which cannot decide the sign of a parameter declines rather than guessing, because the sign
 * is what distinguishes the oscillating solutions from the growing ones.
 */
final class DSolveSpecialFunctions {

  private DSolveSpecialFunctions() {}

  /**
   * The general solution of the equation, or {@link F#NIL} if it is not one of the equations known
   * here.
   */
  static IExpr solve(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (lf.order != 2 || !lf.g.isZero() || lf.a[2].isZero()) {
      return F.NIL;
    }
    IExpr p = cancel(F.Divide(lf.a[1], lf.a[2]), engine);
    IExpr q = cancel(F.Divide(lf.a[0], lf.a[2]), engine);
    if (q.isZero()) {
      return F.NIL;
    }

    IExpr[] basis = legendre(p, q, xVar, engine);
    if (basis == null) {
      basis = airy(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = besselLiteral(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = besselPurePower(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = besselExponential(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = besselNormalForm(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = kummer(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = gauss(p, q, xVar, engine);
    }
    if (basis == null) {
      // Last, because every row above reads a rational coefficient and none of them can claim a
      // potential built from Csc and Sec.
      basis = poschlTeller(p, q, yFunction, xVar, engine);
    }
    if (basis == null) {
      return F.NIL;
    }
    return engine.evaluate(
        F.Plus(F.Times(c_n, basis[0]), F.Times(ctx.nextConstant(), basis[1])));
  }

  /**
   * Legendre's equation <code>(1-x^2)*y'' - 2*x*y' + (nu*(nu+1) - mu^2/(1-x^2))*y == 0</code>.
   */
  private static IExpr[] legendre(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    IExpr oneMinusSquare = engine.evaluate(F.Subtract(F.C1, F.Sqr(xVar)));
    if (!DSolveODE.isVanishing(
        engine.evaluate(F.Plus(F.Times(p, oneMinusSquare), F.Times(F.C2, xVar))), engine)) {
      return null;
    }
    IExpr qq = cancel(F.Times(q, F.Sqr(oneMinusSquare)), engine);
    IExpr c0 = engine.evaluate(F.subst(qq, xVar, F.C0));
    IExpr c2 = engine.evaluate(F.Coefficient(qq, xVar, F.C2));
    if (!c0.isFree(xVar) || !c2.isFree(xVar)) {
      return null;
    }
    IExpr rest = engine.evaluate(
        F.Expand(F.Subtract(qq, F.Plus(c0, F.Times(c2, F.Sqr(xVar))))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    IExpr degree = engine.evaluate(F.Negate(c2));
    IExpr nu = engine.evaluate(
        F.Divide(F.Plus(F.CN1, F.Sqrt(F.Plus(F.C1, F.Times(F.C4, degree)))), F.C2));
    IExpr orderSquared = engine.evaluate(F.Subtract(degree, c0));
    if (orderSquared.isZero()) {
      return new IExpr[] {F.LegendreP(nu, xVar), F.LegendreQ(nu, xVar)};
    }
    IExpr mu = engine.evaluate(F.PowerExpand(F.Sqrt(orderSquared)));
    return new IExpr[] {F.LegendreP(nu, mu, xVar), F.LegendreQ(nu, mu, xVar)};
  }

  /** Airy's equation <code>y'' == (a*x + b)*y</code>, for any nonzero <code>a</code>. */
  private static IExpr[] airy(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!p.isZero()) {
      return null;
    }
    IExpr slope = engine.evaluate(F.Negate(F.D(q, xVar)));
    if (!slope.isFree(xVar) || slope.isZero()) {
      return null;
    }
    IExpr shift = engine.evaluate(F.Negate(F.subst(q, xVar, F.C0)));
    if (!shift.isFree(xVar)) {
      return null;
    }
    IExpr rest = engine.evaluate(F.Expand(F.Plus(q, F.Times(slope, xVar), shift)));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    // y'' == (a*x + b)*y is Airy's equation in a*x + b, i.e. in a^(1/3)*(x + b/a).
    IExpr scale = engine.evaluate(F.Power(slope, F.QQ(1, 3)));
    IExpr argument = engine.evaluate(shift.isZero() //
        ? F.Times(scale, xVar)
        : F.Times(scale, F.Plus(xVar, F.Divide(shift, slope))));
    return new IExpr[] {F.AiryAi(argument), F.AiryBi(argument)};
  }

  /** Bessel's equation <code>x^2*y'' + x*y' + (a^2*x^2 - nu^2)*y == 0</code> as it stands. */
  private static IExpr[] besselLiteral(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!engine.evaluate(F.Simplify(F.Times(xVar, p))).isOne()) {
      return null;
    }
    IExpr expr = engine.evaluate(F.Expand(F.Simplify(F.Times(F.Sqr(xVar), q))));
    IExpr squared = engine.evaluate(F.Coefficient(expr, F.Sqr(xVar)));
    if (!squared.isFree(xVar) || squared.isZero()) {
      return null;
    }
    IExpr negatedOrder =
        engine.evaluate(F.Simplify(F.Subtract(expr, F.Times(squared, F.Sqr(xVar)))));
    if (!negatedOrder.isFree(xVar)) {
      return null;
    }
    IExpr nu = engine
        .evaluate(F.Simplify(F.PowerExpand(F.Sqrt(engine.evaluate(F.Negate(negatedOrder))))));
    IExpr scale = engine.evaluate(F.Simplify(F.PowerExpand(F.Sqrt(squared))));
    IExpr argument = engine.evaluate(F.Simplify(F.Times(scale, xVar)));
    return new IExpr[] {F.BesselJ(nu, argument), F.BesselY(nu, argument)};
  }

  /**
   * <code>y'' == A*x^m*y</code>, which is Bessel's equation of order <code>1/(m+2)</code> in the
   * variable <code>x^((m+2)/2)</code>.
   */
  private static IExpr[] besselPurePower(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!p.isZero()) {
      return null;
    }
    IExpr exponent = cancel(F.Divide(F.Times(xVar, F.D(q, xVar)), q), engine);
    if (!exponent.isNumber() || !exponent.isFree(xVar)) {
      return null;
    }
    IExpr shifted = engine.evaluate(F.Plus(exponent, F.C2));
    if (exponent.isZero() || shifted.isZero()) {
      return null;
    }
    IExpr factor = cancel(F.Divide(F.Negate(q), F.Power(xVar, exponent)), engine);
    if (!factor.isFree(xVar) || factor.isZero()) {
      return null;
    }
    int sign = numericSign(factor, engine);
    if (sign == 0) {
      return null;
    }
    IExpr magnitude = engine.evaluate(sign > 0 ? factor : F.Negate(factor));
    IExpr half = engine.evaluate(F.Divide(shifted, F.C2));
    IExpr nu = engine.evaluate(F.Divide(F.C1, F.Abs(shifted)));
    IExpr argument = engine.evaluate(
        F.Times(F.Divide(F.Sqrt(magnitude), F.Abs(half)), F.Power(xVar, half)));
    IExpr root = F.Sqrt(xVar);
    return sign > 0 //
        ? new IExpr[] {F.Times(root, F.BesselI(nu, argument)),
            F.Times(root, F.BesselK(nu, argument))}
        : new IExpr[] {F.Times(root, F.BesselJ(nu, argument)),
            F.Times(root, F.BesselY(nu, argument))};
  }

  /**
   * <code>y'' == A*E^(lambda*x)*y</code>, which is Bessel's equation of order <code>0</code> in the
   * variable <code>E^(lambda*x/2)</code>.
   */
  private static IExpr[] besselExponential(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!p.isZero()) {
      return null;
    }
    IExpr rate = cancel(F.Divide(F.D(q, xVar), q), engine);
    if (!rate.isFree(xVar) || rate.isZero()) {
      return null;
    }
    IExpr factor = cancel(F.Divide(F.Negate(q), F.Exp(F.Times(rate, xVar))), engine);
    if (!factor.isFree(xVar) || factor.isZero()) {
      return null;
    }
    int sign = numericSign(factor, engine);
    int rateSign = numericSign(rate, engine);
    if (sign == 0 || rateSign == 0) {
      return null;
    }
    IExpr magnitude = engine.evaluate(sign > 0 ? factor : F.Negate(factor));
    IExpr argument = engine.evaluate(F.Times(F.Divide(F.Times(F.C2, F.Sqrt(magnitude)),
        F.Abs(rate)), F.Exp(F.Times(F.Divide(rate, F.C2), xVar))));
    return sign > 0 //
        ? new IExpr[] {F.BesselI(F.C0, argument), F.BesselK(F.C0, argument)}
        : new IExpr[] {F.BesselJ(F.C0, argument), F.BesselY(F.C0, argument)};
  }

  /**
   * <code>y'' + (A + B/x^2)*y == 0</code>, the form without a first derivative which the two rows
   * above do not cover.
   */
  private static IExpr[] besselNormalForm(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!p.isZero() || q.isFree(xVar)) {
      return null;
    }
    IExpr scaled = cancel(F.Times(F.Sqr(xVar), q), engine);
    IExpr squared = engine.evaluate(F.Coefficient(scaled, xVar, F.C2));
    IExpr constant = engine.evaluate(F.subst(scaled, xVar, F.C0));
    if (!squared.isFree(xVar) || !constant.isFree(xVar) || squared.isZero()) {
      return null;
    }
    IExpr rest = engine.evaluate(
        F.Expand(F.Subtract(scaled, F.Plus(F.Times(squared, F.Sqr(xVar)), constant))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    IExpr nu = engine.evaluate(F.PowerExpand(F.Sqrt(F.Subtract(F.QQ(1, 4), constant))));
    boolean modified = numericSign(squared, engine) < 0;
    IExpr magnitude = engine.evaluate(modified ? F.Negate(squared) : squared);
    IExpr argument = engine.evaluate(F.Times(F.Sqrt(magnitude), xVar));
    IExpr root = F.Sqrt(xVar);
    return modified //
        ? new IExpr[] {F.Times(root, F.BesselI(nu, argument)),
            F.Times(root, F.BesselK(nu, argument))}
        : new IExpr[] {F.Times(root, F.BesselJ(nu, argument)),
            F.Times(root, F.BesselY(nu, argument))};
  }

  /** Kummer's equation <code>x*y'' + (b - x)*y' - a*y == 0</code>. */
  private static IExpr[] kummer(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    IExpr b = cancel(F.Times(xVar, F.Plus(p, F.C1)), engine);
    IExpr a = cancel(F.Times(F.CN1, xVar, q), engine);
    if (!b.isFree(xVar) || !a.isFree(xVar)) {
      return null;
    }
    IExpr rest = engine.evaluate(F.Simplify(F.Subtract(p, F.Subtract(F.Divide(b, xVar), F.C1))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    if (isProvableInteger(b, engine)) {
      // The two solutions coincide, so this is not a basis.
      return null;
    }
    return new IExpr[] {F.Hypergeometric1F1(a, b, xVar),
        F.Times(F.Power(xVar, F.Subtract(F.C1, b)),
            F.Hypergeometric1F1(F.Plus(a, F.Subtract(F.C1, b)), F.Subtract(F.C2, b), xVar))};
  }

  /**
   * The hypergeometric equation <code>x*(x-1)*y'' + ((a+b+1)*x - c)*y' + a*b*y == 0</code>.
   */
  private static IExpr[] gauss(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    IExpr weight = engine.evaluate(F.Subtract(F.Sqr(xVar), xVar));
    IExpr linear = cancel(F.Times(p, weight), engine);
    IExpr product = cancel(F.Times(q, weight), engine);
    if (!product.isFree(xVar)) {
      return null;
    }
    IExpr slope = engine.evaluate(F.D(linear, xVar));
    if (!slope.isFree(xVar)) {
      return null;
    }
    IExpr c = engine.evaluate(F.Negate(F.subst(linear, xVar, F.C0)));
    IExpr rest = engine.evaluate(
        F.Expand(F.Subtract(linear, F.Subtract(F.Times(slope, xVar), c))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    if (isProvableInteger(c, engine)) {
      return null;
    }
    IExpr sum = engine.evaluate(F.Subtract(slope, F.C1));
    // The discriminant is (a-b)^2, which has to be factored before the root can be taken of it.
    IExpr difference = engine.evaluate(
        F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.Sqr(sum), F.Times(F.C4, product))))));
    IExpr a = engine.evaluate(F.Divide(F.Subtract(sum, difference), F.C2));
    IExpr b = engine.evaluate(F.Divide(F.Plus(sum, difference), F.C2));
    return new IExpr[] {F.Hypergeometric2F1(a, b, c, xVar),
        F.Times(F.Power(xVar, F.Subtract(F.C1, c)),
            F.Hypergeometric2F1(F.Plus(a, F.Subtract(F.C1, c)), F.Plus(b, F.Subtract(F.C1, c)),
                F.Subtract(F.C2, c), xVar))};
  }

  /** How big a coefficient is still worth looking at for a trigonometric potential. */
  private static final int MAX_POTENTIAL_LEAF_COUNT = 200;

  /**
   * A potential built from <code>Csc(x)^2</code> and <code>Sec(x)^2</code>:
   * <code>y'' == (a + p*(p-1)*Csc(x)^2 + q*(q-1)*Sec(x)^2)*y</code>, which is Pöschl and Teller's.
   *
   * <p>
   * Multiplying the coefficient by <code>Sin(x)^2*Cos(x)^2</code> clears both, and what is left is
   * an even quadratic in <code>Cos(x)</code> exactly when the equation is of this kind. Reading the
   * three numbers off it gives the two exponents and the rate, and the solutions are
   * <code>Sin(x)^p*Cos(x)^q</code> times a hypergeometric function of <code>Sin(x)^2</code>, with
   * the second one having <code>1-p</code> in place of <code>p</code>.
   *
   * <p>
   * The same row answers the spellings <code>(a*Cos(x)^2 + b*Sin(x)^2 + c)/Sin(x)^2</code> and
   * <code>a + b*Cot(x)^2</code>, which are the same potential written differently.
   *
   * @param yFunction the unknown, needed because this row checks its own answer
   */
  private static IExpr[] poschlTeller(IExpr p, IExpr q, IExpr yFunction, IExpr xVar,
      EvalEngine engine) {
    if (!p.isZero() || q.leafCount() > MAX_POTENTIAL_LEAF_COUNT || !hasCircular(q, xVar)) {
      return null;
    }
    // The sine and the cosine become plain unknowns before anything is multiplied out. Left as
    // trigonometric functions they do not survive it: the evaluator writes Cos(x)^2/Sin(x)^2 back
    // as Cot(x)^2 on its own, and the rewrite below would then have nothing to work on.
    IExpr sine = F.Dummy("pts");
    IExpr cosine = F.Dummy("ptc");
    IExpr inUnknowns = engine.evaluate(F.subst(q, F.List( //
        F.Rule(F.Csc(xVar), F.Power(sine, F.CN1)), //
        F.Rule(F.Sec(xVar), F.Power(cosine, F.CN1)), //
        F.Rule(F.Cot(xVar), F.Divide(cosine, sine)), //
        F.Rule(F.Tan(xVar), F.Divide(sine, cosine)), //
        F.Rule(F.Sin(xVar), sine), //
        F.Rule(F.Cos(xVar), cosine))));
    // Anything the substitution did not reach is still written in the variable, which is what
    // sends Sin(2*x) or x*Sin(x) away without further work.
    if (!inUnknowns.isFree(xVar, true)) {
      return null;
    }
    IExpr cleared =
        engine.evaluate(F.Expand(F.Times(inUnknowns, F.Sqr(sine), F.Sqr(cosine))));

    // Every even power of the sine becomes one of the cosine, so that what is left is a polynomial
    // in the cosine alone; an odd power survives and the row declines just below.
    IExpr oneMinus = F.Subtract(F.C1, F.Sqr(cosine));
    IExpr polynomial = engine.evaluate(F.Expand(F.subst(cleared, F.List( //
        F.Rule(F.Power(sine, F.C6), F.Power(oneMinus, F.C3)), //
        F.Rule(F.Power(sine, F.C4), F.Sqr(oneMinus)), //
        F.Rule(F.Sqr(sine), oneMinus)))));
    if (!polynomial.isFree(sine, true)
        || !engine.evaluate(F.PolynomialQ(polynomial, cosine)).isTrue()
        || engine.evaluate(F.Exponent(polynomial, cosine)).toIntDefault() > 4) {
      return null;
    }
    IExpr k0 = engine.evaluate(F.Coefficient(polynomial, cosine, F.C0));
    IExpr k1 = engine.evaluate(F.Coefficient(polynomial, cosine, F.C1));
    IExpr k2 = engine.evaluate(F.Coefficient(polynomial, cosine, F.C2));
    IExpr k3 = engine.evaluate(F.Coefficient(polynomial, cosine, F.C3));
    IExpr k4 = engine.evaluate(F.Coefficient(polynomial, cosine, F.C4));
    // An odd power means the potential is not one of these, whatever the rest looks like.
    if (!DSolveODE.isVanishing(k1, engine) || !DSolveODE.isVanishing(k3, engine)) {
      return null;
    }

    IExpr c0 = engine.evaluate(F.Negate(k4));
    IExpr c2 = k0;
    IExpr c1 = engine.evaluate(F.Simplify(F.Plus(F.Subtract(k2, c0), c2)));
    if (!c0.isFree(xVar) || !c1.isFree(xVar) || !c2.isFree(xVar)) {
      return null;
    }

    // The two exponents solve t*(t-1) == -c, so the root has to be taken of a square; Factor first
    // for the same reason as in the Gauss row above.
    IExpr exponentSin = root(c1, engine);
    IExpr exponentCos = root(c2, engine);
    IExpr rate = engine.evaluate(F.PowerExpand(F.Sqrt(F.Factor(c0))));
    if (exponentSin.isNIL() || exponentCos.isNIL() || rate.isNIL()) {
      return null;
    }
    // Where the two exponents meet, the pair below is one solution twice.
    if (DSolveODE.isVanishing(engine.evaluate(F.Subtract(F.Times(F.C2, exponentSin), F.C1)),
        engine)) {
      return null;
    }

    IExpr first = hypergeometricBranch(exponentSin, exponentCos, rate, xVar, engine);
    IExpr second = hypergeometricBranch(engine.evaluate(F.Subtract(F.C1, exponentSin)), exponentCos,
        rate, xVar, engine);

    // The residual is a hypergeometric function which cannot be rearranged to zero, so this row
    // checks its answer numerically instead of leaving it to the lenient check the cascade ends
    // with - that one accepts whatever it cannot decide, which here would be everything.
    IAST residuals = F.list(engine.evaluate(F.Plus( //
        F.D(yFunction, F.list(xVar, F.C2)), //
        F.Times(p, F.D(yFunction, xVar)), //
        F.Times(q, yFunction))));
    if (!DSolveVerify.acceptODEStrict(residuals, yFunction, xVar, first, engine)
        || !DSolveVerify.acceptODEStrict(residuals, yFunction, xVar, second, engine)) {
      return null;
    }
    return new IExpr[] {first, second};
  }

  /**
   * Whether the coefficient is trigonometric in the variable at all.
   *
   * <p>
   * Without this the row claims equations it has no business with. A coefficient which is a plain
   * constant is free of the variable, so it survives the substitution below and comes out as a
   * potential with both exponents equal to one - <code>y'' - y == 0</code> answered with a
   * hypergeometric pair. That pair is not wrong, but it is not the answer that equation is known
   * by, and the constant coefficient row is the one which owns it.
   */
  private static boolean hasCircular(IExpr expr, IExpr xVar) {
    return !expr.isFree(x -> x.isAST1() && x.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec,
        ID.Csc) && !x.first().isFree(xVar), true);
  }

  /** The exponent <code>t</code> with <code>t*(t-1) == -c</code>, taking the larger root. */
  private static IExpr root(IExpr c, EvalEngine engine) {
    IExpr discriminant = engine.evaluate(
        F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.C1, F.Times(F.C4, c))))));
    return discriminant.isPresent() //
        ? engine.evaluate(F.Divide(F.Plus(F.C1, discriminant), F.C2))
        : F.NIL;
  }

  /** <code>Sin(x)^s*Cos(x)^c*Hypergeometric2F1((s+c+r)/2, (s+c-r)/2, s+1/2, Sin(x)^2)</code>. */
  private static IExpr hypergeometricBranch(IExpr exponentSin, IExpr exponentCos, IExpr rate,
      IExpr xVar, EvalEngine engine) {
    IExpr sum = F.Plus(exponentSin, exponentCos);
    return engine.evaluate(F.Times( //
        F.Power(F.Sin(xVar), exponentSin), //
        F.Power(F.Cos(xVar), exponentCos), //
        F.Hypergeometric2F1(F.Divide(F.Plus(sum, rate), F.C2),
            F.Divide(F.Subtract(sum, rate), F.C2), F.Plus(exponentSin, F.C1D2),
            F.Sqr(F.Sin(xVar)))));
  }

  private static IExpr cancel(IExpr expr, EvalEngine engine) {
    return engine.evaluate(F.Cancel(F.Together(expr)));
  }

  /**
   * The sign of a parameter, or <code>0</code> if it does not evaluate to a real number. A row
   * whose solutions depend on the sign has to decline in that case.
   */
  private static int numericSign(IExpr expr, EvalEngine engine) {
    try {
      IExpr value = engine.evalN(expr);
      if (value instanceof INumber && ((INumber) value).isReal()) {
        double d = ((INumber) value).evalf();
        if (Double.isFinite(d) && d != 0.0) {
          return d > 0.0 ? 1 : -1;
        }
      }
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
    }
    return 0;
  }

  /** Whether the parameter is an integer, which makes the second solution a copy of the first. */
  private static boolean isProvableInteger(IExpr expr, EvalEngine engine) {
    return expr.isInteger() || engine.evaluate(F.IntegerQ(expr)).isTrue();
  }
}
