package org.matheclipse.core.dsolve;

import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.integrate.IntegrateTimeBudget;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
  static IExpr solve(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
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
      // After the Bessel rows, whose potential has no 1/x term and so cannot be this, and before
      // Kummer, which wants a first derivative where this wants none.
      basis = whittaker(p, q, yFunction, xVar, engine);
    }
    if (basis == null) {
      basis = kummer(p, q, xVar, engine);
    }
    if (basis == null) {
      basis = gauss(p, q, xVar, engine);
    }
    if (basis == null) {
      // After Kummer and Gauss, whose equations have a coefficient of y' which is a ratio of
      // polynomials of the same degree, where this one's grows.
      basis = hermite(p, q, xVar, engine);
    }
    if (basis == null) {
      // Last, because every row above reads a rational coefficient and none of them can claim a
      // potential built from Csc and Sec.
      basis = poschlTeller(p, q, yFunction, xVar, engine);
    }
    if (basis == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Plus(F.Times(c_n, basis[0]), F.Times(ctx.nextConstant(), basis[1])));
  }

  /**
   * The general solution of an equation which is one of those above only after being rewritten, or
   * {@link F#NIL}.
   *
   * <p>
   * Separate from {@link #solve} and asked later, once {@link DSolveKovacic} has declined. Both
   * rows here answer with hypergeometric functions or with a quadrature where a method which knows
   * the equation would answer with something shorter, and the equations they recognize include
   * ones whose solutions are elementary: <code>y'' == (2/(9*(1-x)^2) + ...)*y</code> is
   * hypergeometric, and is also a product of powers, which is the answer worth having.
   */
  static IExpr solveByRewriting(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n,
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
    IExpr[] basis = fuchsian(p, q, yFunction, xVar, ctx);
    if (basis == null) {
      basis = normalFormPrePass(p, q, yFunction, xVar, ctx);
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
    IExpr rest = engine.evaluate(F.Expand(F.Subtract(qq, F.Plus(c0, F.Times(c2, F.Sqr(xVar))))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    IExpr degree = engine.evaluate(F.Negate(c2));
    IExpr nu =
        engine.evaluate(F.Divide(F.Plus(F.CN1, F.Sqrt(F.Plus(F.C1, F.Times(F.C4, degree)))), F.C2));
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
    IExpr nu =
        engine.evaluate(F.Simplify(F.PowerExpand(F.Sqrt(engine.evaluate(F.Negate(negatedOrder))))));
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
    IExpr argument =
        engine.evaluate(F.Times(F.Divide(F.Sqrt(magnitude), F.Abs(half)), F.Power(xVar, half)));
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
    IExpr argument =
        engine.evaluate(F.Times(F.Divide(F.Times(F.C2, F.Sqrt(magnitude)), F.Abs(rate)),
            F.Exp(F.Times(F.Divide(rate, F.C2), xVar))));
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
    IExpr rest = engine
        .evaluate(F.Expand(F.Subtract(scaled, F.Plus(F.Times(squared, F.Sqr(xVar)), constant))));
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
    return new IExpr[] {F.Hypergeometric1F1(a, b, xVar), F.Times(F.Power(xVar, F.Subtract(F.C1, b)),
        F.Hypergeometric1F1(F.Plus(a, F.Subtract(F.C1, b)), F.Subtract(F.C2, b), xVar))};
  }

  /**
   * Hermite's equation <code>y'' + b*x*y' + q*y == 0</code> with <code>b</code> and <code>q</code>
   * constant, whose solutions are the two confluent series in <code>x^2</code>.
   *
   * <p>
   * Substituting <code>t = -b/2*x^2</code> turns it into Kummer's equation, and the two exponents
   * of that equation at the origin are <code>0</code> and <code>1/2</code>, so the basis is one
   * even function of <code>x</code> and one odd one. With <code>b == -2</code> and
   * <code>q == 2*n</code> for a whole <code>n</code> the even one is the Hermite polynomial
   * <code>HermiteH(n,x)</code> up to a factor when <code>n</code> is even, and the odd one is that
   * polynomial when <code>n</code> is odd; naming the polynomial instead would give a pair which is
   * a basis for one parity of <code>n</code> and the same function twice for the other, so both
   * solutions are written as series here whatever <code>q</code> is.
   *
   * <p>
   * The factor <code>Sqrt(-b/2)</code> which the substitution puts in front of the odd solution is
   * a constant, and it is left out rather than carried: it would be an imaginary constant in front
   * of a real function whenever <code>b</code> is positive, and the arbitrary constant beside it
   * absorbs it either way.
   */
  private static IExpr[] hermite(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    if (!q.isFree(xVar)) {
      return null;
    }
    IExpr b = cancel(F.Divide(p, xVar), engine);
    if (!b.isFree(xVar) || b.isZero()) {
      return null;
    }
    if (!DSolveODE.isVanishing(engine.evaluate(F.Subtract(p, F.Times(b, xVar))), engine)) {
      return null;
    }
    IExpr degree = cancel(F.Divide(F.Negate(q), b), engine);
    IExpr square = engine.evaluate(F.Times(F.CN1D2, b, F.Sqr(xVar)));
    return new IExpr[] {
        F.Hypergeometric1F1(F.Times(F.CN1D2, degree), F.C1D2, square),
        F.Times(xVar,
            F.Hypergeometric1F1(F.Times(F.C1D2, F.Subtract(F.C1, degree)), F.QQ(3L, 2L), square))};
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
    IExpr rest = engine.evaluate(F.Expand(F.Subtract(linear, F.Subtract(F.Times(slope, xVar), c))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return null;
    }
    if (isProvableInteger(c, engine)) {
      return null;
    }
    IExpr sum = engine.evaluate(F.Subtract(slope, F.C1));
    // The discriminant is (a-b)^2, which has to be factored before the root can be taken of it.
    IExpr difference = engine
        .evaluate(F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.Sqr(sum), F.Times(F.C4, product))))));
    IExpr a = engine.evaluate(F.Divide(F.Subtract(sum, difference), F.C2));
    IExpr b = engine.evaluate(F.Divide(F.Plus(sum, difference), F.C2));
    return new IExpr[] {F.Hypergeometric2F1(a, b, c, xVar),
        F.Times(F.Power(xVar, F.Subtract(F.C1, c)),
            F.Hypergeometric2F1(F.Plus(a, F.Subtract(F.C1, c)), F.Plus(b, F.Subtract(F.C1, c)),
                F.Subtract(F.C2, c), xVar))};
  }

  /** How big an exponent may be before the equation is left to another method. */
  private static final int MAX_EXPONENT_LEAF_COUNT = 80;

  /** How big the rewritten equation and its solutions may become. */
  private static final int MAX_REWRITTEN_LEAF_COUNT = 300;

  /**
   * How long one rewriting may take. The number is what it is on the machine this was tuned on; a
   * slower one is given proportionally longer, see
   * {@link org.matheclipse.core.basic.MachineProfile}.
   */
  private static final int STEP_SECONDS = 3;

  /** How long all four exponent pairs together may take. */
  private static final int TOTAL_SECONDS = 6;

  /** Where the two singular points are sampled, as fractions of the way from one to the other. */
  private static final int[][] BETWEEN_POLES =
      new int[][] {{3, 20}, {3, 10}, {9, 20}, {3, 5}, {17, 20}};

  /**
   * The hypergeometric equation about any two finite singular points, rather than about
   * <code>0</code> and <code>1</code>.
   *
   * <p>
   * An equation whose coefficients become infinite at exactly two places <code>x1</code> and
   * <code>x2</code> is carried to the row above by <code>s == (x-x1)/(x2-x1)</code>, which is a
   * change of variable with no second derivative of its own and so adds no term. What it leaves is
   * hypergeometric only after the behaviour at each end has been divided out: the solutions go like
   * <code>s^r0</code> at one end and <code>(1-s)^r1</code> at the other, with the exponents being
   * the roots of <code>rho^2 - (1-p)*rho + q == 0</code> for the limits <code>p</code> and
   * <code>q</code> of <code>s*P</code> and <code>s^2*Q</code> there. Writing
   * <code>Y == s^r0*(1-s)^r1*F</code> and asking what equation <code>F</code> satisfies gives one
   * the row above can read.
   *
   * <p>
   * There are two exponents at each end, so four ways to divide out, and only some of them leave a
   * hypergeometric equation with usable parameters. Each is tried and the first whose answer solves
   * the original equation is taken.
   *
   * <p>
   * This is what answers Gegenbauer's, Jacobi's and the associated Legendre equations with a degree
   * left symbolic, which are singular at <code>-1</code> and <code>1</code>.
   */
  private static IExpr[] fuchsian(IExpr p, IExpr q, IExpr yFunction, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr common = engine.evaluate(S.PolynomialLCM.of(engine, //
        F.Denominator(F.Together(p)), F.Denominator(F.Together(q))));
    if (!engine.evaluate(F.PolynomialQ(common, xVar)).isTrue()) {
      return null;
    }
    IAST poles = DSolveUtil.polesOf(common, xVar, engine);
    if (poles == null || poles.argSize() != 2) {
      // One place is a confluent equation, which the Bessel and Whittaker rows above answer;
      // three is Heun's, which is not written with hypergeometric functions at all.
      return null;
    }
    IExpr x1 = poles.arg1().first();
    IExpr x2 = poles.arg2().first();
    if (DSolveUtil.hasRadical(x1) || DSolveUtil.hasRadical(x2)) {
      return null;
    }
    IExpr width = engine.evaluate(F.Subtract(x2, x1));
    if (DSolveODE.isVanishing(width, engine)) {
      return null;
    }

    IExpr s = F.Dummy("s");
    IExpr back = engine.evaluate(F.Plus(x1, F.Times(width, s)));
    IExpr mappedP = cancel(F.Times(width, F.subst(p, xVar, back)), engine);
    IExpr mappedQ = cancel(F.Times(F.Sqr(width), F.subst(q, xVar, back)), engine);
    if (!mappedP.isFree(xVar) || !mappedQ.isFree(xVar)) {
      return null;
    }

    IExpr[] atZero = exponentsAt(mappedP, mappedQ, s, F.C0, engine);
    IExpr[] atOne = exponentsAt(mappedP, mappedQ, s, F.C1, engine);
    if (atZero == null || atOne == null) {
      return null;
    }

    IAST residuals = F.list(engine.evaluate(F.Plus( //
        F.D(yFunction, F.list(xVar, F.C2)), //
        F.Times(p, F.D(yFunction, xVar)), //
        F.Times(q, yFunction))));
    IASTAppendable samples = F.ListAlloc(BETWEEN_POLES.length);
    for (int[] fraction : BETWEEN_POLES) {
      samples.append(F.Plus(x1, F.Times(F.QQ(fraction[0], fraction[1]), width)));
    }

    final IExpr forward = cancel(F.Divide(F.Subtract(xVar, x1), width), engine);
    final IExpr mapP = mappedP;
    final IExpr mapQ = mappedQ;
    long deadline = System.nanoTime()
        + MachineProfile.seconds((long) TOTAL_SECONDS) * 1_000_000_000L;
    for (IExpr exponentAtZero : atZero) {
      for (IExpr exponentAtOne : atOne) {
        if (System.nanoTime() > deadline) {
          // The pairs already tried were expensive enough that the rest are not worth it.
          return null;
        }
        final IExpr r0 = exponentAtZero;
        final IExpr r1 = exponentAtOne;
        // Under a watchdog, not a TimeConstrained: what an exponent which is a root makes
        // expensive is the factoring inside the row this hands the reduced equation to, and that
        // does not come back to the evaluation loop often enough to be stopped by one. Three of
        // the four pairs are usually cheap, and the equation whose exponents are sixths of the
        // root of seventeen is left alone rather than spending minutes on it.
        IExpr packed = IntegrateTimeBudget.runWithin( //
            () -> {
              IExpr[] attempt = homotopy(mapP, mapQ, s, r0, r1, forward, ctx);
              return attempt == null ? F.NIL : F.list(attempt[0], attempt[1]);
            }, MachineProfile.seconds((long) STEP_SECONDS) * 1000L);
        if (!packed.isList2()) {
          continue;
        }
        IExpr[] basis = new IExpr[] {packed.first(), packed.second()};
        if (DSolveVerify.acceptODEStrictAt(residuals, yFunction, xVar, basis[0], samples, engine)
            && DSolveVerify.acceptODEStrictAt(residuals, yFunction, xVar, basis[1], samples,
                engine)) {
          return basis;
        }
      }
    }
    return null;
  }

  /**
   * The two exponents the solutions may behave with at one of the singular points, or
   * <code>null</code>.
   *
   * <p>
   * The limits are taken by cancelling first and substituting afterwards, because <code>s*P</code>
   * at <code>s == 0</code> is otherwise <code>0</code> times something infinite.
   */
  private static IExpr[] exponentsAt(IExpr mappedP, IExpr mappedQ, IExpr s, IExpr place,
      EvalEngine engine) {
    IExpr shifted = F.Subtract(s, place);
    IExpr limitP = engine.evaluate(F.subst(cancel(F.Times(shifted, mappedP), engine), s, place));
    IExpr limitQ =
        engine.evaluate(F.subst(cancel(F.Times(F.Sqr(shifted), mappedQ), engine), s, place));
    if (!limitP.isFree(s) || !limitQ.isFree(s) || !isFinite(limitP) || !isFinite(limitQ)) {
      return null;
    }
    IExpr sum = engine.evaluate(F.Subtract(F.C1, limitP));
    IExpr difference = engine
        .evaluate(F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.Sqr(sum), F.Times(F.C4, limitQ))))));
    IExpr first = engine.evaluate(F.Divide(F.Subtract(sum, difference), F.C2));
    IExpr second = engine.evaluate(F.Divide(F.Plus(sum, difference), F.C2));
    if (first.leafCount() > MAX_EXPONENT_LEAF_COUNT
        || second.leafCount() > MAX_EXPONENT_LEAF_COUNT) {
      return null;
    }
    // An exponent which is a root of a number is what makes the rewriting below grow: the reduced
    // coefficients become sums of radicals over a common denominator and the row this hands them
    // to spends minutes factoring one. Those equations are left to the method which answers them
    // in closed form when they have one, and to nothing when they do not. The equations this row
    // is for -- Gegenbauer's, Jacobi's, and the ones written with a parameter for a singular
    // point -- have exponents built from the parameters without roots of them.
    if (DSolveUtil.hasRadical(first) || DSolveUtil.hasRadical(second)) {
      return null;
    }
    return new IExpr[] {first, second};
  }

  /**
   * The hypergeometric pair left once <code>s^r0*(1-s)^r1</code> is divided out, written back in
   * the original variable, or <code>null</code> if what is left is not hypergeometric.
   */
  private static IExpr[] homotopy(IExpr mappedP, IExpr mappedQ, IExpr s, IExpr r0, IExpr r1,
      IExpr forward, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr logarithmicDerivative =
        cancel(F.Subtract(F.Divide(r0, s), F.Divide(r1, F.Subtract(F.C1, s))), engine);
    IExpr secondOverFirst =
        engine.evaluate(F.Plus(F.D(logarithmicDerivative, s), F.Sqr(logarithmicDerivative)));
    // An exponent which is a root rather than a number makes these grow: putting the reduced
    // coefficients over a common denominator multiplies sums of radicals together, and the
    // equation whose exponents are sixths of the root of seventeen spent minutes there. Both the
    // size and the time are bounded, and a pair which exceeds either is passed over -- one of the
    // other three often is not.
    IExpr reducedP = ctx.evalTimeConstrained(
        F.Cancel(F.Together(F.Plus(mappedP, F.Times(F.C2, logarithmicDerivative)))), STEP_SECONDS);
    if (reducedP.isNIL() || reducedP.leafCount() > MAX_REWRITTEN_LEAF_COUNT) {
      return null;
    }
    IExpr reducedQ = ctx.evalTimeConstrained(F.Cancel(F.Together(
        F.Plus(mappedQ, F.Times(mappedP, logarithmicDerivative), secondOverFirst))), STEP_SECONDS);
    if (reducedQ.isNIL() || reducedQ.leafCount() > MAX_REWRITTEN_LEAF_COUNT) {
      return null;
    }

    IExpr[] hypergeometric = gauss(reducedP, reducedQ, s, engine);
    if (hypergeometric == null) {
      return null;
    }
    IExpr weight = F.Times(F.Power(s, r0), F.Power(F.Subtract(F.C1, s), r1));
    IExpr[] basis = new IExpr[2];
    for (int i = 0; i < 2; i++) {
      IExpr written = ctx.evalTimeConstrained(
          F.subst(F.Times(weight, hypergeometric[i]), s, forward), STEP_SECONDS);
      if (written.isNIL() || !written.isFree(s, true)
          || written.leafCount() > MAX_REWRITTEN_LEAF_COUNT) {
        return null;
      }
      basis[i] = written;
    }
    return basis;
  }

  /** Whether the limit came out as a number rather than as a way of saying there is none. */
  private static boolean isFinite(IExpr expr) {
    return expr.isPresent() && !expr.isIndeterminate() && !expr.isDirectedInfinity()
        && expr.isFree(x -> x.isIndeterminate() || x.isDirectedInfinity(), true);
  }

  /** How big the potential of the reduced equation may be before it is left alone. */
  private static final int MAX_REDUCED_POTENTIAL_LEAF_COUNT = 50;

  /**
   * The equation with its first derivative removed, offered to the rows which want a potential and
   * nothing else.
   *
   * <p>
   * <code>y == Exp(-Integrate(p/2))*z</code> leaves <code>z'' == r*z</code>, so an equation which
   * is Airy's or Bessel's only after that is written this way is recognized here rather than not at
   * all: <code>y'' + 2*y'/x + y == 0</code> becomes <code>z'' == -z</code> and comes back as
   * <code>Sin(x)/x</code> and <code>Cos(x)/x</code>.
   *
   * <p>
   * Only for an equation nothing has rewritten yet, not for the ones the methods below reach on
   * their own. An equation arriving here a second time has already been rewritten once, and the
   * powers the two rewritings leave share a base without being collected, which the verification of
   * the answer then cannot finish.
   */
  private static IExpr[] normalFormPrePass(IExpr p, IExpr q, IExpr yFunction, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (p.isZero() || !ctx.mayRewrite()) {
      return null;
    }
    DSolveNormalForm normalForm = DSolveNormalForm.of(p, q, xVar, MAX_POTENTIAL_LEAF_COUNT, ctx);
    if (normalForm == null) {
      return null;
    }
    IExpr potential = engine.evaluate(F.Negate(normalForm.r));
    if (potential.leafCount() > MAX_REDUCED_POTENTIAL_LEAF_COUNT) {
      return null;
    }

    IExpr[] reduced;
    if (potential.isZero()) {
      // z'' == 0. None of the rows below takes a potential which is not there, and the method
      // which reads a rational one declines it too, so the two solutions are written out here.
      reduced = new IExpr[] {F.C1, xVar};
    } else {
      reduced = airy(F.C0, potential, xVar, engine);
      if (reduced == null) {
        reduced = besselPurePower(F.C0, potential, xVar, engine);
      }
      if (reduced == null) {
        reduced = besselExponential(F.C0, potential, xVar, engine);
      }
      if (reduced == null) {
        reduced = besselNormalForm(F.C0, potential, xVar, engine);
      }
    }
    if (reduced == null) {
      return null;
    }

    IAST residuals = F.list(engine.evaluate(F.Plus( //
        F.D(yFunction, F.list(xVar, F.C2)), //
        F.Times(p, F.D(yFunction, xVar)), //
        F.Times(q, yFunction))));
    IExpr[] basis = new IExpr[2];
    for (int i = 0; i < 2; i++) {
      basis[i] = engine.evaluate(F.Times(normalForm.recovery, reduced[i]));
      if (!DSolveVerify.acceptODEStrict(residuals, yFunction, xVar, basis[i], engine)) {
        return null;
      }
    }
    return basis;
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
    IExpr cleared = engine.evaluate(F.Expand(F.Times(inUnknowns, F.Sqr(sine), F.Sqr(cosine))));

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
    return !expr
        .isFree(x -> x.isAST1() && x.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc)
            && !x.first().isFree(xVar), true);
  }

  /** The exponent <code>t</code> with <code>t*(t-1) == -c</code>, taking the larger root. */
  private static IExpr root(IExpr c, EvalEngine engine) {
    IExpr discriminant =
        engine.evaluate(F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.C1, F.Times(F.C4, c))))));
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

  /**
   * Whittaker's equation <code>y'' + (-1/4 + k/z + (1/4 - m^2)/z^2)*y == 0</code>, in a variable
   * which is any multiple of <code>x - x0</code>.
   *
   * <p>
   * What identifies it is the shape of the potential: one double pole and nothing else, so that
   * <code>q*(x-x0)^2</code> is a quadratic. Writing that quadratic as
   * <code>b2 + b1/(x-x0) + b0/(x-x0)^2</code> and matching gives
   * <code>z == 2*Sqrt(-b2)*(x-x0)</code>, <code>k == b1/(2*Sqrt(-b2))</code> and
   * <code>m == Sqrt(1/4-b0)</code>.
   *
   * <p>
   * The answer is written with {@link S#Hypergeometric1F1} rather than with
   * <code>WhittakerM</code>: the two say the same thing, but only the first evaluates to a number,
   * and a row which checks its own answer numerically may only emit something that does.
   *
   * <p>
   * The Bessel row above accepts a potential of the form <code>A + B/x^2</code>, which is this one
   * without the <code>1/x</code> term, so it answers those before this is reached and this only
   * ever sees the equations it did not want.
   */
  private static IExpr[] whittaker(IExpr p, IExpr q, IExpr yFunction, IExpr xVar,
      EvalEngine engine) {
    if (!p.isZero()) {
      return null;
    }
    IExpr potential = cancel(q, engine);
    IExpr denominator = engine.evaluate(F.Denominator(potential));
    if (!engine.evaluate(F.PolynomialQ(denominator, xVar)).isTrue()
        || engine.evaluate(F.Exponent(denominator, xVar)).toIntDefault() != 2) {
      return null;
    }
    IAST poles = DSolveUtil.polesOf(denominator, xVar, engine);
    if (poles == null || poles.argSize() != 1 || poles.arg1().second().toIntDefault() != 2) {
      return null;
    }
    IExpr pole = poles.arg1().first();
    if (DSolveUtil.hasRadical(pole)) {
      return null;
    }

    IExpr shifted = F.Subtract(xVar, pole);
    IExpr quadratic = cancel(F.Times(q, F.Sqr(shifted)), engine);
    if (!engine.evaluate(F.PolynomialQ(quadratic, xVar)).isTrue()
        || engine.evaluate(F.Exponent(quadratic, xVar)).toIntDefault() != 2) {
      return null;
    }
    IExpr b0 = engine.evaluate(F.subst(quadratic, xVar, pole));
    // Differentiated before the point goes in, or the substitution reaches the variable of the
    // derivative as well and asks for D(..., 0).
    IExpr b1 =
        engine.evaluate(F.subst(engine.evaluate(F.D(quadratic, xVar)), xVar, pole));
    IExpr b2 = engine.evaluate(F.Coefficient(quadratic, xVar, F.C2));
    if (b2.isZero()) {
      return null;
    }
    IExpr order = engine.evaluate(F.PowerExpand(F.Sqrt(F.Factor(F.Subtract(F.C1D4, b0)))));
    IExpr scale = engine.evaluate(F.Times(F.C2, F.Sqrt(F.Negate(b2))));
    IExpr kappa = cancel(F.Divide(b1, scale), engine);
    IExpr z = engine.evaluate(F.Times(scale, shifted));
    // Twice the order being a whole number makes the two solutions one: either they coincide, or
    // the lower parameter of one of them is a non positive integer and it does not exist.
    if (isProvableInteger(engine.evaluate(F.Times(F.C2, order)), engine)) {
      return null;
    }

    IExpr first = whittakerBranch(order, kappa, z, engine);
    IExpr second = whittakerBranch(engine.evaluate(F.Negate(order)), kappa, z, engine);

    // As for the trigonometric potential below: a hypergeometric residual is not something the
    // lenient check the cascade ends with can decide, so it would accept anything, and this row
    // checks its own answer instead.
    //
    // The first solution is the one checked. The equation contains the order only as m^2, so it
    // is the same equation for m and for -m, and the second solution is the first one written
    // with -m -- there is nothing left for a second check to find. It would also fail for a
    // reason of its own: the lower parameter of the second solution is 1-2*m, and half of the
    // values the check gives a free parameter are halves, at which that is zero or a negative
    // whole number and the function does not exist. The second solution still has to pass the
    // lenient check, which asks only that it not be seen to fail.
    IAST residuals = F.list(engine.evaluate(F.Plus( //
        F.D(yFunction, F.list(xVar, F.C2)), //
        F.Times(q, yFunction))));
    if (!DSolveVerify.acceptODEStrict(residuals, yFunction, xVar, first, engine)
        || !DSolveVerify.acceptODE(residuals, yFunction, xVar, second, engine)) {
      return null;
    }
    return new IExpr[] {first, second};
  }

  /**
   * One solution of Whittaker's equation,
   * <code>Exp(-z/2)*z^(1/2+m)*Hypergeometric1F1(1/2+m-k, 1+2*m, z)</code>, which is
   * <code>WhittakerM(k, m, z)</code> written so that it evaluates.
   */
  private static IExpr whittakerBranch(IExpr order, IExpr kappa, IExpr z, EvalEngine engine) {
    IExpr exponent = engine.evaluate(F.Plus(F.C1D2, order));
    return engine.evaluate(F.Times( //
        F.Exp(F.Times(F.CN1D2, z)), //
        F.Power(z, exponent), //
        F.Hypergeometric1F1(F.Subtract(exponent, kappa), F.Plus(F.C1, F.Times(F.C2, order)), z)));
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
