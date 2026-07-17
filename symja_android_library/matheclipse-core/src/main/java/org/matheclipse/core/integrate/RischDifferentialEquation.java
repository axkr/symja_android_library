package org.matheclipse.core.integrate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Solver for the <b>Risch differential equation</b> (RDE) &mdash; the core of the transcendental
 * Risch integration algorithm.
 *
 * <p>
 * This is the <b>base case</b>. For {@code f, g in Q(x)} it solves {@code D(y) + f*y = g}:
 * <ul>
 * <li>{@link #solvePolynomialRDE} &mdash; a <em>polynomial</em> {@code y} ({@code f, g}
 * polynomials);
 * <li>{@link #solveRationalRDE} &mdash; a <em>rational</em> {@code y} for arbitrary rational
 * {@code f, g}: the weak normalizer removes the simple poles of {@code f} with positive-integer
 * residue, after which the solution's denominator is bounded by {@code c = gcd(Dg, Dg')}.
 * </ul>
 * All work by a degree bound plus a linear system in the unknown coefficients. Returning
 * {@link F#NIL} (when {@code g != 0} and no solution exists) is a genuine <em>decision</em>: no
 * antiderivative of the corresponding hyperexponential shape is elementary.
 *
 * <p>
 * <b>Not yet implemented</b> (the rest of the real algorithm): the primitive ({@code log}) and
 * hypertangent ({@code tan}) monomial cases and their cancellation sub-cases; and the recursive
 * reduction of an RDE over a differential-field tower down to this base field. Those, together with
 * Hermite reduction and the Rothstein&ndash;Trager logarithmic part, are what turn this into a
 * complete transcendental integrator.
 *
 * <p>
 * Clean-room: implemented from the standard degree-bound/coefficient-comparison method (Bronstein,
 * <i>Symbolic Integration I</i>, ch. 6), not transliterated. See {@code CLEANROOM.md}.
 */
public class RischDifferentialEquation {

  /** Degree cap on the trial solution, to keep the linear system bounded. */
  private static final int MAX_DEGREE = 64;

  private RischDifferentialEquation() {}

  /**
   * Solve {@code D(y) + f*y = g} for a polynomial {@code y}.
   *
   * @param f a polynomial in {@code x}
   * @param g a polynomial in {@code x}
   * @param x the variable
   * @param engine the evaluation engine
   * @return a polynomial {@code y} solving the RDE, or {@link F#NIL} if none exists / inputs are
   *         not polynomials
   */
  public static IExpr solvePolynomialRDE(IExpr f, IExpr g, IExpr x, EvalEngine engine) {
    IExpr fp = engine.evaluate(F.ExpandAll(f));
    IExpr gp = engine.evaluate(F.ExpandAll(g));
    if (!isPolynomial(fp, x, engine) || !isPolynomial(gp, x, engine)) {
      return F.NIL;
    }
    if (gp.isZero()) {
      // D(y) + f*y = 0 has the polynomial solution y = 0 (the only one when f != 0).
      return F.C0;
    }
    if (fp.isZero()) {
      // D(y) = g -> termwise polynomial antiderivative.
      return polynomialAntiderivative(gp, x, engine);
    }
    int df = degree(fp, x, engine);
    int dg = degree(gp, x, engine);
    if (df < 0 || dg < 0) {
      return F.NIL;
    }
    // Degree bound: deg(f*y) dominates deg(y'), so deg(g) = deg(f) + deg(y) when deg(f) >= 1, and
    // deg(g) = deg(y) when f is a non-zero constant.
    int beta = (df == 0) ? dg : (dg - df);
    if (beta < 0 || beta > MAX_DEGREE) {
      return F.NIL;
    }
    // Trial solution y = sum_{i=0}^{beta} c_i x^i with undetermined coefficients.
    ISymbol[] coefficientSymbols = new ISymbol[beta + 1];
    IASTAppendable trial = F.PlusAlloc(beta + 1);
    for (int i = 0; i <= beta; i++) {
      coefficientSymbols[i] = F.Dummy("rde$c" + i);
      trial.append(F.Times(coefficientSymbols[i], F.Power(x, F.ZZ(i))));
    }
    IExpr y = trial;
    IExpr residual =
        engine.evaluate(F.ExpandAll(F.Subtract(F.Plus(F.D(y, x), F.Times(fp, y)), gp)));
    IExpr coeffList = engine.evaluate(F.CoefficientList(residual, x));
    if (!coeffList.isList()) {
      return F.NIL;
    }
    IAST coeffs = (IAST) coeffList;
    IASTAppendable equations = F.ListAlloc(coeffs.argSize());
    for (int i = 1; i < coeffs.size(); i++) {
      equations.append(F.Equal(coeffs.get(i), F.C0));
    }
    IASTAppendable variables = F.ListAlloc(coefficientSymbols.length);
    for (ISymbol c : coefficientSymbols) {
      variables.append(c);
    }
    IExpr solution = engine.evaluate(F.Solve(equations, variables));
    if (!solution.isList() || ((IAST) solution).argSize() == 0) {
      // No polynomial solution: the corresponding integral has no antiderivative of this form.
      return F.NIL;
    }
    IExpr ySolved = engine.evaluate(F.ReplaceAll(y, ((IAST) solution).arg1()));
    // A determined system leaves no free coefficients; if any survive (degenerate case), pin them
    // to 0.
    for (ISymbol c : coefficientSymbols) {
      if (!ySolved.isFree(c, true)) {
        ySolved = engine.evaluate(F.ReplaceAll(ySolved, F.Rule(c, F.C0)));
      }
    }
    return ySolved;
  }

  /**
   * Solve {@code D(y) + f*y = g} for a <em>rational</em> {@code y}, in the base case where
   * {@code f} is a polynomial and {@code g} is a rational function.
   *
   * <p>
   * With {@code f} a polynomial the solution's denominator is exactly {@code c = gcd(Dg, Dg')}
   * (where {@code Dg} is the denominator of {@code g}), and any <em>simple</em> pole of {@code g}
   * proves no rational solution exists. Substituting {@code y = z/c} yields the generalized
   * polynomial RDE {@code c*z' + (f*c - c')*z = c^2*g}, solved by
   * {@link #solveGeneralPolynomialRDE}. The case where {@code f} itself has poles (needing the weak
   * normalizer / integer-residue analysis) is not yet handled.
   */
  public static IExpr solveRationalRDE(IExpr f, IExpr g, IExpr x, EvalEngine engine) {
    IExpr ft = engine.evaluate(F.Together(f));
    IExpr gt = engine.evaluate(F.Together(g));
    if (!isRational(ft, x, engine) || !isRational(gt, x, engine)) {
      return F.NIL;
    }
    // Weak normalizer: remove the simple poles of f with positive-integer residue via y = yTilde/q,
    // so that the denominator analysis below (c = gcd(Dg, Dg')) is valid for the transformed RDE.
    IExpr q = weakNormalizer(ft, x, engine);
    if (q.isNIL()) {
      return F.NIL;
    }
    IExpr fTilde;
    IExpr gTilde;
    if (q.isFree(x, true)) {
      q = F.C1;
      fTilde = ft;
      gTilde = gt;
    } else {
      fTilde = engine.evaluate(F.Together(F.Subtract(ft, F.Divide(F.D(q, x), q))));
      gTilde = engine.evaluate(F.Together(F.Times(q, gt)));
    }
    IExpr yTilde = solveWeaklyNormalizedRDE(fTilde, gTilde, x, engine);
    if (yTilde.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Together(F.Divide(yTilde, q)));
  }

  /**
   * Weak normalizer (Bronstein <i>Symbolic Integration I</i> Alg. 6.1, base case {@code D=d/dx}):
   * return {@code q in Q[x]} such that {@code f - q'/q} has no simple pole with a positive-integer
   * residue. At a simple pole (root of the squarefree simple part {@code d1} of the denominator
   * {@code d}) the residue is {@code num/d'}; the residues that are positive integers are the
   * positive-integer roots {@code n} of {@code Resultant_x(d1, num - z*d')}, and each contributes
   * {@code gcd(num - n*d', d1)^n} to {@code q}.
   */
  private static IExpr weakNormalizer(IExpr f, IExpr x, EvalEngine engine) {
    IExpr ft = engine.evaluate(F.Together(f));
    IExpr num = engine.evaluate(F.Numerator(ft));
    IExpr d = engine.evaluate(F.Denominator(ft));
    if (d.isFree(x, true)) {
      return F.C1; // f is a polynomial: no poles
    }
    IExpr dd = engine.evaluate(F.D(d, x));
    IExpr repeated = engine.evaluate(F.PolynomialGCD(d, dd));
    IExpr radical = engine.evaluate(F.PolynomialQuotient(d, repeated, x));
    IExpr d1 =
        engine.evaluate(F.PolynomialQuotient(radical, F.PolynomialGCD(radical, repeated), x));
    if (d1.isFree(x, true)) {
      return F.C1; // no simple poles
    }
    ISymbol z = F.Dummy("wn$z");
    IExpr resultant = engine.evaluate(F.Resultant(d1, F.Subtract(num, F.Times(z, dd)), x));
    if (resultant.isZero()) {
      return F.C1;
    }
    IExpr solution = engine.evaluate(F.Solve(F.Equal(resultant, F.C0), z));
    if (!solution.isList()) {
      return F.C1;
    }
    IExpr q = F.C1;
    IAST solutions = (IAST) solution;
    for (int i = 1; i < solutions.size(); i++) {
      IExpr nValue = engine.evaluate(F.ReplaceAll(z, solutions.get(i)));
      if (nValue.isInteger() && nValue.isPositive()) {
        int n = nValue.toIntDefault(-1);
        if (n >= 1 && n <= MAX_DEGREE) {
          IExpr factor = engine.evaluate(F.PolynomialGCD(F.Subtract(num, F.Times(nValue, dd)), d1));
          q = engine.evaluate(F.Times(q, F.Power(factor, F.ZZ(n))));
        }
      }
    }
    return q;
  }

  /**
   * Solve {@code D(y) + f*y = g} for a rational {@code y}, assuming {@code f} is weakly normalized.
   * The denominator of {@code y} is then bounded by {@code c = gcd(Dg, Dg')}; substituting
   * {@code y = z/c} and clearing denominators gives a generalized polynomial RDE.
   */
  private static IExpr solveWeaklyNormalizedRDE(IExpr f, IExpr g, IExpr x, EvalEngine engine) {
    IExpr denominatorG = engine.evaluate(F.Denominator(engine.evaluate(F.Together(g))));
    IExpr c = denominatorG.isFree(x, true) //
        ? F.C1
        : engine.evaluate(F.PolynomialGCD(denominatorG, F.D(denominatorG, x)));
    IExpr bigF = engine.evaluate(F.Together(F.Subtract(f, F.Divide(F.D(c, x), c))));
    IExpr bigG = engine.evaluate(F.Together(F.Times(c, g)));
    IExpr commonDen =
        engine.evaluate(F.Together(F.Times(F.Denominator(bigF), F.Denominator(bigG))));
    IExpr a = commonDen;
    IExpr b = engine.evaluate(F.Together(F.Times(commonDen, bigF)));
    IExpr rhs = engine.evaluate(F.Together(F.Times(commonDen, bigG)));
    if (!isPolynomial(a, x, engine) || !isPolynomial(b, x, engine)
        || !isPolynomial(rhs, x, engine)) {
      return F.NIL;
    }
    IExpr z = solveGeneralPolynomialRDE(a, b, rhs, x, engine);
    if (z.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Together(F.Divide(z, c)));
  }

  /**
   * Solve the generalized polynomial RDE {@code a*D(z) + b*z = c} for a polynomial {@code z}, with
   * {@code a, b, c} polynomials, by a degree bound plus a linear system in the coefficients of
   * {@code z}. The degree bound accounts for cancellation of the two leading terms when
   * {@code deg(a)-1 == deg(b)} (a base-case {@code RdeBoundDegree}).
   */
  private static IExpr solveGeneralPolynomialRDE(IExpr a, IExpr b, IExpr c, IExpr x,
      EvalEngine engine) {
    IExpr ap = engine.evaluate(F.ExpandAll(a));
    IExpr bp = engine.evaluate(F.ExpandAll(b));
    IExpr cp = engine.evaluate(F.ExpandAll(c));
    if (cp.isZero()) {
      return F.C0;
    }
    int da = degree(ap, x, engine);
    int db = degree(bp, x, engine);
    int dc = degree(cp, x, engine);
    if (dc < 0) {
      return F.C0;
    }
    int beta = dc - Math.max(da - 1, db);
    if (da >= 1 && da - 1 == db) {
      IExpr nStar = engine.evaluate(
          F.Divide(F.Negate(F.Coefficient(bp, x, F.ZZ(db))), F.Coefficient(ap, x, F.ZZ(da))));
      if (nStar.isInteger() && !nStar.isNegative()) {
        beta = Math.max(beta, nStar.toIntDefault(-1));
      }
    }
    if (beta < 0 || beta > MAX_DEGREE) {
      return F.NIL;
    }
    ISymbol[] coefficientSymbols = new ISymbol[beta + 1];
    IASTAppendable trial = F.PlusAlloc(beta + 1);
    for (int i = 0; i <= beta; i++) {
      coefficientSymbols[i] = F.Dummy("rde$c" + i);
      trial.append(F.Times(coefficientSymbols[i], F.Power(x, F.ZZ(i))));
    }
    IExpr z = trial;
    IExpr residual = engine
        .evaluate(F.ExpandAll(F.Subtract(F.Plus(F.Times(ap, F.D(z, x)), F.Times(bp, z)), cp)));
    IExpr coeffList = engine.evaluate(F.CoefficientList(residual, x));
    if (!coeffList.isList()) {
      return F.NIL;
    }
    IAST coeffs = (IAST) coeffList;
    IASTAppendable equations = F.ListAlloc(coeffs.argSize());
    for (int i = 1; i < coeffs.size(); i++) {
      equations.append(F.Equal(coeffs.get(i), F.C0));
    }
    IASTAppendable variables = F.ListAlloc(coefficientSymbols.length);
    for (ISymbol cSym : coefficientSymbols) {
      variables.append(cSym);
    }
    IExpr solution = engine.evaluate(F.Solve(equations, variables));
    if (!solution.isList() || ((IAST) solution).argSize() == 0) {
      return F.NIL;
    }
    IExpr zSolved = engine.evaluate(F.ReplaceAll(z, ((IAST) solution).arg1()));
    for (ISymbol cSym : coefficientSymbols) {
      if (!zSolved.isFree(cSym, true)) {
        zSolved = engine.evaluate(F.ReplaceAll(zSolved, F.Rule(cSym, F.C0)));
      }
    }
    return zSolved;
  }

  private static boolean isPolynomial(IExpr expr, IExpr x, EvalEngine engine) {
    return engine.evaluate(F.PolynomialQ(expr, x)).isTrue();
  }

  private static boolean isRational(IExpr e, IExpr x, EvalEngine engine) {
    return isPolynomial(engine.evaluate(F.Numerator(e)), x, engine)
        && isPolynomial(engine.evaluate(F.Denominator(e)), x, engine);
  }

  private static int degree(IExpr poly, IExpr x, EvalEngine engine) {
    return engine.evaluate(F.Exponent(poly, x)).toIntDefault(-1);
  }

  /**
   * Termwise antiderivative of a polynomial {@code g = sum g_k x^k}: {@code sum g_k x^(k+1)/(k+1)}.
   */
  private static IExpr polynomialAntiderivative(IExpr g, IExpr x, EvalEngine engine) {
    IExpr coeffList = engine.evaluate(F.CoefficientList(g, x));
    if (!coeffList.isList()) {
      return F.NIL;
    }
    IAST coeffs = (IAST) coeffList;
    IASTAppendable sum = F.PlusAlloc(coeffs.argSize());
    for (int k = 0; k < coeffs.argSize(); k++) {
      IExpr gk = coeffs.get(k + 1);
      if (!gk.isZero()) {
        sum.append(F.Times(F.Divide(gk, F.ZZ(k + 1)), F.Power(x, F.ZZ(k + 1))));
      }
    }
    return engine.evaluate(sum);
  }
}
