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
  static IExpr solve(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
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

    IExpr z1 = F.NIL;
    if (logarithmicDerivative.isPresent()) {
      z1 = expIntegral(logarithmicDerivative, xVar, ctx);
    }
    if (z1.isNIL() && denominatorDegree >= 1) {
      // The solution may have zeros where r has no pole, which the guess above cannot put
      // anywhere. Those go into a polynomial factor of their own.
      IExpr pair = withApparentSingularities(r, denominator, numeratorDegree, denominatorDegree,
          recovery, xVar, ctx);
      if (pair.isPresent() && pair.isList() && ((IAST) pair).argSize() == 2) {
        IExpr general = engine.evaluate(F.Plus(F.Times(c_n, ((IAST) pair).arg1()),
            F.Times(ctx.nextConstant(), ((IAST) pair).arg2())));
        return general;
      }
    }
    if (z1.isNIL() && denominatorDegree == 0) {
      // The guess above can only be a polynomial here, and a polynomial cannot be the logarithmic
      // derivative of a solution which has zeros in it. Those zeros are what the factor below
      // carries.
      z1 = withPolynomialFactor(r, xVar, ctx);
    }
    if (z1.isNIL()) {
      // Nothing rational works, so ask whether the logarithmic derivative is one of a pair which
      // satisfies a quadratic over the rational functions.
      return algebraicPair(r, places, xVar, polynomialDegree, recovery, yFunction, lf, c_n, ctx);
    }
    IExpr z2 = secondSolution(z1, xVar, ctx);
    if (z2.isNIL() || !independent(z1, z2, xVar, engine)) {
      return F.NIL;
    }
    return assemble(z1, z2, recovery, c_n, ctx);
  }

  /**
   * A solution whose logarithmic derivative is not rational but satisfies a quadratic over the
   * rational functions.
   *
   * <p>
   * The two roots of that quadratic are the logarithmic derivatives of the two solutions, so their
   * sum is rational and can be looked for the same way the rational case looks for the derivative
   * itself. What that sum has to satisfy is <code>d' + 2*s*d == 0</code> with
   * <code>d == 4*r - 2*s' - s^2</code>, and the two derivatives are then
   * <code>(s +- Sqrt(d))/2</code>.
   */
  private static IExpr algebraicPair(IExpr r, IAST places, IExpr xVar, int polynomialDegree,
      IExpr recovery, IExpr yFunction, LinearODEForm lf, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    // A place where r becomes infinite more than once leaves a system which is large and coupled,
    // and what it would find where the solution has one is already found above.
    for (int i = 1; i <= places.argSize(); i++) {
      IExpr entry = places.get(i);
      if (entry.isList() && entry.argSize() == 2 && !entry.first().isFree(xVar)
          && engine.evaluate(F.Exponent(entry.first(), xVar)).toIntDefault() >= 2) {
        return F.NIL;
      }
    }

    IASTAppendable unknowns = F.ListAlloc();
    IExpr guess = ansatz(places, xVar, polynomialDegree, unknowns, engine);
    IExpr discriminant = engine.evaluate(F.Subtract(
        F.Subtract(F.Times(F.C4, r), F.Times(F.C2, F.D(guess, xVar))), F.Sqr(guess)));
    IExpr equation = engine.evaluate(
        F.Plus(F.D(discriminant, xVar), F.Times(F.C2, guess, discriminant)));
    IExpr sum = fitCoefficientList(equation, guess, unknowns, xVar, ctx);
    if (sum.isNIL()) {
      return F.NIL;
    }

    IExpr fitted = engine.evaluate(F.Simplify(F.Subtract(
        F.Subtract(F.Times(F.C4, r), F.Times(F.C2, F.D(sum, xVar))), F.Sqr(sum))));
    // Zeroing the unknowns the solution left free is a choice, so the sum is put back.
    if (!DSolveODE.isVanishing(engine.evaluate(F.Together(
        F.Plus(F.D(fitted, xVar), F.Times(F.C2, sum, fitted)))), engine)) {
      return F.NIL;
    }
    if (fitted.isZero()) {
      // The two derivatives coincide, so this gives one solution rather than two.
      return F.NIL;
    }

    IExpr root = engine.evaluate(F.Sqrt(fitted));
    IExpr z1 = expIntegral(engine.evaluate(F.Divide(F.Plus(sum, root), F.C2)), xVar, ctx);
    IExpr z2 = expIntegral(engine.evaluate(F.Divide(F.Subtract(sum, root), F.C2)), xVar, ctx);
    if (z1.isNIL() || z2.isNIL() || !independent(z1, z2, xVar, engine)) {
      return F.NIL;
    }
    IExpr body = assemble(z1, z2, recovery, c_n, ctx);
    if (body.isNIL()) {
      return F.NIL;
    }
    // A solution which is algebraic cannot be seen to satisfy the equation by rearranging it, so
    // it is required to be seen to satisfy it numerically before it is returned.
    IExpr residual = engine.evaluate(F.Plus(
        F.Times(lf.a[2], F.D(yFunction, F.List(xVar, F.C2))),
        F.Times(lf.a[1], F.D(yFunction, xVar)), F.Times(lf.a[0], yFunction)));
    return DSolveVerify.acceptODEStrict(F.List(residual), yFunction, xVar, body, engine) //
        ? body
        : F.NIL;
  }

  /** How high a degree the polynomial of the apparent singularities may have. */
  private static final int MAX_APPARENT_DEGREE = 10;

  /**
   * A solution whose zeros are not among the places <code>r</code> becomes infinite.
   *
   * <p>
   * Such a zero is an apparent singularity: the logarithmic derivative has a pole there which the
   * guess of the plain case cannot put anywhere, because the guess only has parts where
   * <code>r</code> itself has one. Writing the solution as a polynomial times an exponential moves
   * those zeros into the polynomial, and what is left is fixed by how <code>r</code> behaves at
   * each of its own infinities and at infinity itself: at each place there are two ways the
   * solution can behave, and each choice of them determines the degree the polynomial must have.
   *
   * <p>
   * This is what Chebyshev's, Gegenbauer's and Laguerre's equations need.
   */
  private static IExpr withApparentSingularities(IExpr r, IExpr denominator, int numeratorDegree,
      int denominatorDegree, IExpr recovery, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr[] atInfinity = infinityData(r, xVar, denominatorDegree - numeratorDegree, ctx);
    if (atInfinity == null) {
      return F.NIL;
    }
    IAST poles = polesOf(denominator, xVar, ctx);
    if (poles == null || poles.argSize() == 0 || poles.argSize() > MAX_POLES) {
      return F.NIL;
    }
    int count = poles.argSize();
    IExpr[][] local = new IExpr[count][];
    for (int i = 0; i < count; i++) {
      IAST pole = (IAST) poles.get(i + 1);
      local[i] = poleData(r, xVar, pole.arg1(), pole.arg2().toIntDefault(), ctx);
      if (local[i] == null) {
        return F.NIL;
      }
    }

    // One first solution for each way the behaviours can be chosen. Different choices give
    // different members of the same solution space, and the simplest is the one worth carrying.
    IASTAppendable candidates = F.ListAlloc(1 << count);
    for (int infinitySign = 0; infinitySign < 2; infinitySign++) {
      IExpr alphaInfinity = atInfinity[infinitySign == 1 ? 1 : 2];
      for (int mask = 0; mask < (1 << count); mask++) {
        if (ctx.expired()) {
          break;
        }
        boolean redundant = false;
        for (int i = 0; i < count && !redundant; i++) {
          // Where the two behaviours coincide, one of the two bits says nothing.
          redundant = local[i][3].isTrue() && ((mask >> i) & 1) == 1;
        }
        if (redundant) {
          continue;
        }
        IASTAppendable sum = F.PlusAlloc(count);
        for (int i = 0; i < count; i++) {
          sum.append(local[i][((mask >> i) & 1) == 1 ? 1 : 2]);
        }
        int degree = nonNegativeInteger(
            engine.evaluate(F.Subtract(alphaInfinity, engine.evaluate(sum))),
            MAX_APPARENT_DEGREE, engine);
        if (degree < 0) {
          continue;
        }

        IASTAppendable exponentTerms = F.PlusAlloc(count + 1);
        exponentTerms.append(infinitySign == 1 ? atInfinity[0] : F.Negate(atInfinity[0]));
        for (int i = 0; i < count; i++) {
          boolean plus = ((mask >> i) & 1) == 1;
          exponentTerms.append(plus ? local[i][0] : F.Negate(local[i][0]));
          exponentTerms.append(F.Divide(local[i][plus ? 1 : 2],
              F.Subtract(xVar, ((IAST) poles.get(i + 1)).arg1())));
        }
        IExpr exponent = engine.evaluate(F.Simplify(engine.evaluate(exponentTerms)));
        IExpr exponential = expIntegral(exponent, xVar, ctx);
        if (exponential.isNIL()) {
          continue;
        }
        IExpr remainder = engine.evaluate(F.Simplify(
            F.Subtract(F.Plus(F.D(exponent, xVar), F.Sqr(exponent)), r)));
        IExpr polynomial = monicPolynomial(exponent, remainder, degree, xVar, ctx);
        if (polynomial.isPresent()) {
          candidates.append(
              engine.evaluate(F.Simplify(F.Times(recovery, polynomial, exponential))));
        }
      }
    }
    if (candidates.argSize() == 0) {
      return F.NIL;
    }

    IExpr[] sorted = new IExpr[candidates.argSize()];
    for (int i = 0; i < sorted.length; i++) {
      sorted[i] = candidates.get(i + 1);
    }
    java.util.Arrays.sort(sorted, (a, b) -> Long.compare(a.leafCount(), b.leafCount()));

    // Two different choices may give two solutions outright, which is better than asking for an
    // integral: the one which carries a first solution to a second is often not elementary even
    // where the first ones are.
    for (int a = 0; a < sorted.length; a++) {
      for (int b = a + 1; b < sorted.length; b++) {
        if (independent(sorted[a], sorted[b], xVar, engine)) {
          return F.List(sorted[a], sorted[b]);
        }
      }
    }
    IExpr weight = engine.evaluate(F.Simplify(F.Sqr(recovery)));
    for (IExpr candidate : sorted) {
      IExpr integral = ctx.integrate(
          engine.evaluate(F.Simplify(F.Divide(weight, F.Sqr(candidate)))), xVar, MAX_LEAF_COUNT);
      if (integral.isPresent()) {
        return F.List(candidate, engine.evaluate(F.Simplify(F.Times(candidate, integral))));
      }
    }
    return F.NIL;
  }

  /** The monic polynomial whose roots are the apparent singularities. */
  private static IExpr monicPolynomial(IExpr exponent, IExpr remainder, int degree, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IASTAppendable unknowns = F.ListAlloc(degree);
    IASTAppendable terms = F.PlusAlloc(degree + 1);
    terms.append(F.Power(xVar, F.ZZ(degree)));
    for (int i = 0; i < degree; i++) {
      IExpr unknown = F.Dummy("ka" + i);
      unknowns.append(unknown);
      terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(i))));
    }
    IExpr guess = engine.evaluate(terms);
    IExpr equation = engine.evaluate(F.Plus(F.D(guess, F.List(xVar, F.C2)),
        F.Times(F.C2, exponent, F.D(guess, xVar)), F.Times(remainder, guess)));
    IExpr fitted = fitCoefficientList(equation, guess, unknowns, xVar, ctx);
    if (fitted.isNIL()) {
      return F.NIL;
    }
    IExpr check = engine.evaluate(F.Simplify(F.Plus(F.D(fitted, F.List(xVar, F.C2)),
        F.Times(F.C2, exponent, F.D(fitted, xVar)), F.Times(remainder, fitted))));
    return DSolveODE.isVanishing(check, engine) ? fitted : F.NIL;
  }

  /** The places the coefficients become infinite, each with how many times over. */
  private static IAST polesOf(IExpr denominator, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr factorList = engine.evaluate(F.FactorList(denominator));
    if (!factorList.isList()) {
      return null;
    }
    IASTAppendable poles = F.ListAlloc(((IAST) factorList).argSize());
    for (int i = 1; i <= ((IAST) factorList).argSize(); i++) {
      IExpr entry = ((IAST) factorList).get(i);
      if (!entry.isList() || entry.argSize() != 2 || entry.first().isFree(xVar)) {
        continue;
      }
      int multiplicity = entry.second().toIntDefault();
      if (multiplicity < 1) {
        return null;
      }
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(entry.first(), F.C0), xVar));
      IAST roots = DSolveUtil.extractSolveResults(solutions);
      if (roots.argSize() == 0) {
        return null;
      }
      for (int j = 1; j <= roots.argSize(); j++) {
        if (!roots.get(j).isFree(xVar, true)) {
          return null;
        }
        poles.append(F.List(roots.get(j), F.ZZ(multiplicity)));
      }
    }
    return poles;
  }

  /**
   * How a solution may behave where <code>r</code> becomes infinite.
   *
   * @return <code>{the part of the square root which is singular there, one exponent, the other,
   *         whether the two coincide}</code>, or <code>null</code> if no solution of this kind can
   *         behave that way
   */
  private static IExpr[] poleData(IExpr r, IExpr xVar, IExpr place, int order, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (order == 1) {
      return new IExpr[] {F.C0, F.C1, F.C1, S.True};
    }
    if (order == 2) {
      IExpr b = engine.evaluate(F.Simplify(
          F.Limit(F.Times(F.Sqr(F.Subtract(xVar, place)), r), F.Rule(xVar, place))));
      if (!isUsable(b)) {
        return null;
      }
      IExpr discriminant = engine.evaluate(F.Sqrt(F.Plus(F.C1, F.Times(F.C4, b))));
      IExpr[] out = new IExpr[] {F.C0,
          engine.evaluate(F.Divide(F.Plus(F.C1, discriminant), F.C2)),
          engine.evaluate(F.Divide(F.Subtract(F.C1, discriminant), F.C2)),
          nonNegativeInteger(discriminant, 0, engine) == 0 ? S.True : S.False};
      return isUsable(out[1]) && isUsable(out[2]) ? out : null;
    }
    if (order % 2 == 1) {
      // An odd pole of the third order or above cannot be reached this way at all.
      return null;
    }

    int half = order / 2;
    IExpr root = F.Sqrt(r);
    IASTAppendable singular = F.PlusAlloc(half);
    IExpr leading = F.NIL;
    for (int j = half; j >= 2; j--) {
      IExpr coefficient = engine.evaluate(F.Simplify(
          F.SeriesCoefficient(root, F.List(xVar, place, F.ZZ(-j)))));
      if (!isUsable(coefficient)) {
        return null;
      }
      if (j == half) {
        leading = coefficient;
      }
      singular.append(F.Times(coefficient, F.Power(F.Subtract(xVar, place), F.ZZ(-j))));
    }
    IExpr part = engine.evaluate(F.Simplify(engine.evaluate(singular)));
    if (leading.isNIL() || leading.isZero()) {
      return null;
    }
    IExpr b = engine.evaluate(F.SeriesCoefficient(
        engine.evaluate(F.Simplify(F.Subtract(r, F.Sqr(part)))),
        F.List(xVar, place, F.ZZ(-(half + 1)))));
    if (!isUsable(b)) {
      return null;
    }
    IExpr ratio = engine.evaluate(F.Divide(b, leading));
    return new IExpr[] {part,
        engine.evaluate(F.Simplify(F.Divide(F.Plus(ratio, F.ZZ(half)), F.C2))),
        engine.evaluate(F.Simplify(F.Divide(F.Plus(F.Negate(ratio), F.ZZ(half)), F.C2))),
        S.False};
  }

  /**
   * How a solution may behave far out.
   *
   * @return <code>{the part of the square root which grows, one exponent, the other}</code>, or
   *         <code>null</code>
   */
  private static IExpr[] infinityData(IExpr r, IExpr xVar, int orderAtInfinity,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (orderAtInfinity >= 3) {
      return new IExpr[] {F.C0, F.C0, F.C1};
    }
    if (orderAtInfinity == 2) {
      IExpr b = engine.evaluate(
          F.Simplify(F.Limit(F.Times(F.Sqr(xVar), r), F.Rule(xVar, F.CInfinity))));
      if (!isUsable(b)) {
        return null;
      }
      IExpr discriminant =
          engine.evaluate(F.Simplify(F.Sqrt(F.Plus(F.C1, F.Times(F.C4, b)))));
      IExpr[] out = new IExpr[] {F.C0,
          engine.evaluate(F.Simplify(F.Divide(F.Plus(F.C1, discriminant), F.C2))),
          engine.evaluate(F.Simplify(F.Divide(F.Subtract(F.C1, discriminant), F.C2)))};
      return isUsable(out[1]) && isUsable(out[2]) ? out : null;
    }
    if (orderAtInfinity == 1 || orderAtInfinity % 2 != 0) {
      return null;
    }

    // The series is taken in 1/x, because that is where infinity is an ordinary place.
    int half = -orderAtInfinity / 2;
    IExpr y = F.Dummy("ky");
    IExpr reciprocal = engine.evaluate(F.Simplify(F.subst(r, xVar, F.Power(y, F.CN1))));
    IExpr root = F.Sqrt(reciprocal);
    IASTAppendable growing = F.PlusAlloc(half + 1);
    IExpr leading = F.NIL;
    for (int j = half; j >= 0; j--) {
      IExpr coefficient = engine.evaluate(
          F.Simplify(F.SeriesCoefficient(root, F.List(y, F.C0, F.ZZ(-j)))));
      if (!isUsable(coefficient)) {
        return null;
      }
      if (j == half) {
        leading = coefficient;
      }
      growing.append(F.Times(coefficient, F.Power(xVar, F.ZZ(j))));
    }
    IExpr part = engine.evaluate(F.Simplify(engine.evaluate(growing)));
    if (leading.isNIL() || leading.isZero()) {
      return null;
    }
    IExpr difference = engine.evaluate(F.Simplify(F.Subtract(r, F.Sqr(part))));
    IExpr b = engine.evaluate(F.SeriesCoefficient(
        engine.evaluate(F.Simplify(F.subst(difference, xVar, F.Power(y, F.CN1)))),
        F.List(y, F.C0, F.ZZ(-(half - 1)))));
    if (!isUsable(b)) {
      return null;
    }
    IExpr ratio = engine.evaluate(F.Divide(b, leading));
    return new IExpr[] {part,
        engine.evaluate(F.Simplify(F.Divide(F.Subtract(ratio, F.ZZ(half)), F.C2))),
        engine.evaluate(F.Simplify(F.Divide(F.Subtract(F.Negate(ratio), F.ZZ(half)), F.C2)))};
  }

  /** Whether a coefficient came out as something which can be worked with. */
  private static boolean isUsable(IExpr expr) {
    return expr.isPresent() && expr.isFree(x -> x.isAST(S.SeriesCoefficient) || x.isAST(S.Series)
        || x.isAST(S.SeriesData) || x.isAST(S.DirectedInfinity) || x.equals(S.Infinity)
        || x.equals(S.ComplexInfinity) || x.equals(S.Indeterminate), true);
  }

  /**
   * The expression as a whole number which is not negative, or <code>-1</code>.
   *
   * <p>
   * The test is made on the value rather than on the form: these come out built on roots of unity
   * at a pair of poles which are conjugates, and deciding what such a thing is worth symbolically
   * costs far more than the search it is guarding. A choice which passes it wrongly is caught
   * afterwards, when no polynomial of that degree is found.
   */
  private static int nonNegativeInteger(IExpr expr, int cap, EvalEngine engine) {
    IExpr value = engine.evaluate(F.N(expr));
    double real;
    double imaginary = 0.0;
    if (value.isReal()) {
      real = value.evalf();
    } else if (value.isComplexNumeric() || value.isComplex()) {
      real = value.re().evalf();
      imaginary = value.im().evalf();
    } else {
      return -1;
    }
    if (Math.abs(imaginary) > 1.0e-7) {
      return -1;
    }
    long rounded = Math.round(real);
    if (rounded < 0 || rounded > cap || Math.abs(real - rounded) > 1.0e-7) {
      return -1;
    }
    return (int) rounded;
  }

  /** How high a degree the polynomial factor may have. */
  private static final int MAX_FACTOR_DEGREE = 32;

  /**
   * A solution of <code>z'' == r*z</code> for a polynomial <code>r</code>, as a polynomial times an
   * exponential.
   *
   * <p>
   * A solution with zeros in it does not have a polynomial logarithmic derivative, so the guess
   * which only looks for one misses it. Splitting the zeros off into a factor of their own leaves
   * an exponential, whose logarithmic derivative is the polynomial part of one of the two square
   * roots of <code>r</code>, and the factor is then a polynomial of a degree the equation itself
   * fixes.
   */
  private static IExpr withPolynomialFactor(IExpr r, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int degree = engine.evaluate(F.Exponent(r, xVar)).toIntDefault();
    if (degree < 2 || degree % 2 != 0) {
      return F.NIL;
    }
    int half = degree / 2;
    IExpr leading = engine.evaluate(F.Coefficient(r, xVar, F.ZZ(degree)));

    for (int sign = 1; sign >= -1; sign -= 2) {
      if (ctx.expired()) {
        return F.NIL;
      }
      IExpr top = engine.evaluate(F.Simplify(F.Times(F.ZZ(sign), F.Sqrt(leading))));
      IASTAppendable unknowns = F.ListAlloc(half);
      IASTAppendable terms = F.PlusAlloc(half + 1);
      terms.append(F.Times(top, F.Power(xVar, F.ZZ(half))));
      for (int j = 0; j < half; j++) {
        IExpr unknown = F.Dummy("kb" + j);
        unknowns.append(unknown);
        terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(j))));
      }
      IExpr guess = engine.evaluate(terms);

      // Only the top half of the coefficients fixes the exponential; what is left over is what the
      // polynomial factor has to answer for.
      IExpr excess =
          engine.evaluate(F.Subtract(F.Plus(F.D(guess, xVar), F.Sqr(guess)), r));
      IASTAppendable equations = F.ListAlloc(half + 1);
      for (int k = 2 * half; k >= half; k--) {
        IExpr coefficient = engine.evaluate(F.Coefficient(excess, xVar, F.ZZ(k)));
        if (!coefficient.isZero()) {
          equations.append(F.Equal(coefficient, F.C0));
        }
      }
      IExpr exponent = solveFor(equations, unknowns, guess, ctx);
      if (exponent.isNIL()) {
        continue;
      }

      IExpr remainder = engine.evaluate(F.Simplify(
          F.Subtract(F.Plus(F.D(exponent, xVar), F.Sqr(exponent)), r)));
      IExpr wanted = engine.evaluate(F.Simplify(F.Divide(
          F.Negate(F.Coefficient(remainder, xVar, F.ZZ(half - 1))), F.Times(F.C2, top))));
      int factorDegree = wanted.toIntDefault();
      if (factorDegree < 0 || factorDegree > MAX_FACTOR_DEGREE) {
        continue;
      }

      // The factor is monic: a constant in front of it belongs in the arbitrary constant.
      IASTAppendable factorUnknowns = F.ListAlloc(factorDegree);
      IASTAppendable factorTerms = F.PlusAlloc(factorDegree + 1);
      factorTerms.append(F.Power(xVar, F.ZZ(factorDegree)));
      for (int i = 0; i < factorDegree; i++) {
        IExpr unknown = F.Dummy("kp" + i);
        factorUnknowns.append(unknown);
        factorTerms.append(F.Times(unknown, F.Power(xVar, F.ZZ(i))));
      }
      IExpr factorGuess = engine.evaluate(factorTerms);
      IExpr factorEquation = engine.evaluate(F.Plus(F.D(factorGuess, F.List(xVar, F.C2)),
          F.Times(F.C2, exponent, F.D(factorGuess, xVar)), F.Times(remainder, factorGuess)));
      IExpr factor =
          fitCoefficientList(factorEquation, factorGuess, factorUnknowns, xVar, ctx);
      if (factor.isNIL()) {
        continue;
      }
      // Zeroing the unknowns the solution left free is a choice, so the factor is put back.
      IExpr check = engine.evaluate(F.Simplify(F.Plus(F.D(factor, F.List(xVar, F.C2)),
          F.Times(F.C2, exponent, F.D(factor, xVar)), F.Times(remainder, factor))));
      if (!DSolveODE.isVanishing(check, engine)) {
        continue;
      }

      IExpr exponential = expIntegral(exponent, xVar, ctx);
      if (exponential.isPresent()) {
        return engine.evaluate(F.Simplify(F.Times(factor, exponential)));
      }
    }
    return F.NIL;
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
    IExpr fitted = fitCoefficientList(equation, guess, unknowns, xVar, ctx);
    if (fitted.isNIL()) {
      return F.NIL;
    }
    // Setting the unfixed unknowns to zero is a choice rather than a consequence, so what comes
    // out is put back into the equation it had to satisfy.
    IExpr residual = engine.evaluate(
        F.Together(F.Subtract(F.Plus(F.D(fitted, xVar), F.Sqr(fitted)), r)));
    return DSolveODE.isVanishing(residual, engine) ? fitted : F.NIL;
  }

  /** The guess fitted so that every coefficient of the equation vanishes. */
  private static IExpr fitCoefficientList(IExpr equation, IExpr guess, IAST unknowns, IExpr xVar,
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
    return solveFor(equations, unknowns, guess, ctx);
  }

  /**
   * The guess with the unknowns solved for, or {@link F#NIL} if they cannot be.
   *
   * <p>
   * Unknowns the solution leaves free are set to zero. That is a choice rather than a consequence,
   * so every caller puts what comes out back into what it had to satisfy.
   */
  private static IExpr solveFor(IAST equations, IAST unknowns, IExpr guess, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
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
    return engine.evaluate(F.Cancel(F.Together(fitted)));
  }

  /**
   * How long one search step may take. The number is what it is on the machine
   * the solvers were tuned on; a slower machine is given proportionally longer, see
   * {@link org.matheclipse.core.basic.MachineProfile}.
   */
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
