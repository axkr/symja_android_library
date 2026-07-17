package org.matheclipse.core.integrate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Recursive transcendental Risch integration &mdash; <b>incremental start (M4)</b>.
 *
 * <p>
 * The full Risch algorithm for transcendental elementary extensions is a large, long-term effort.
 * <ul>
 * <li>Gaussian <code>c*E^(a*x^2 + b*x + c0)</code>, <code>a &ne; 0</code> &rarr; {@code Erf};
 * <li><code>c*E^(k*x + d)/x^m</code>, <code>m &ge; 1</code> &rarr; {@code ExpIntegralEi} (plus
 * elementary terms via the integration-by-parts recurrence);
 * <li><code>c*x^a/Log(x)</code> &rarr; {@code LogIntegral} of <code>x^(a+1)</code>;
 * <li><code>c*Log(d + a*x)/x</code>, <code>d &ne; 0</code> &rarr; {@code PolyLog};
 * <li><code>c*g(k*x)/x</code> for <code>g</code> in {@code Sin/Cos/Sinh/Cosh} &rarr;
 * {@code SinIntegral/CosIntegral/SinhIntegral/CoshIntegral};
 * <li><code>c*Sin(a*x^2)</code> / <code>c*Cos(a*x^2)</code> &rarr; {@code FresnelS} /
 * {@code FresnelC};
 * <li><code>P(x)/Log(x)</code> for a polynomial <code>P</code> &rarr; a sum of
 * {@code LogIntegral}s;
 * <li><code>c*PolyLog(n, a*x)/x</code> &rarr; {@code PolyLog(n+1, a*x)} (the polylogarithm ladder);
 * <li><code>c*x^s*E^(a*x + b)</code>, <code>a &ne; 0</code> &rarr; the incomplete {@code Gamma};
 * <li><code>R(x)*E^(b(x))</code>, <code>R</code> rational and <code>b</code> a polynomial &rarr;
 * <code>q(x)*E^(b(x))</code> via the Risch differential equation {@code q' + b'*q = R} &mdash; the
 * first RDE-based recogniser (see {@link RischDifferentialEquation});
 * <li><code>P(x, Log(u))</code> a polynomial in one {@code Log(u)} &rarr; the primitive (log)
 * monomial case via the coefficient recursion over a {@link DifferentialTower};
 * <li>a polynomial (constant coefficients) in one {@code Tan(u)} (u linear) &rarr; the hypertangent
 * monomial case via the reduction recurrence;
 * <li>a polynomial in one exponential {@code E^(g*x)} &rarr; the hyperexponential monomial case via
 * per-power base-field RDEs (tower RDE recursion; integrates sums of exponentials).
 * </ul>
 * All but the last entry are a closed-form lookup table. The RDE-based hyperexponential case is the
 * start of the real algorithm; the rest (rational RDE solutions, the primitive/tangent monomials,
 * Hermite reduction, the differential-field tower, and surfacing a definitive "not elementary"
 * answer) remains future work (tier T4).
 * 
 */
public class TranscendentalRisch {

  private TranscendentalRisch() {}

  /**
   * Try the (currently Gaussian-only) transcendental recogniser.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_RISCH_TRANSCENDENTAL) {
      return F.NIL;
    }
    IExpr result = integrateGaussian(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateHyperexponential(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateExpOverX(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateReciprocalLog(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateLogOverX(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateTrigOverX(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateFresnel(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integratePolyOverLog(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integratePolyLogLadder(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateIncompleteGamma(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integratePrimitiveLog(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateHypertangent(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    result = integrateHyperexpPolynomial(integrand, x, engine);
    if (result.isPresent()) {
      return result;
    }
    return F.NIL;
  }

  /** {@code Integrate(c*E^(a*x^2+b*x+c0), x) = c*Sqrt(Pi)/(2*Sqrt(-a))*E^(c0-b^2/(4a))*Erf(...)}. */
  private static IExpr integrateGaussian(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr constFactor = F.C1;
    IExpr expFactor = F.NIL;
    IAST factors = integrand.isTimes() ? (IAST) integrand : F.Times(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        constFactor = F.Times(constFactor, factor);
      } else if (factor.isPower() && factor.base().equals(S.E)) {
        if (expFactor.isPresent()) {
          return F.NIL;
        }
        expFactor = factor;
      } else {
        return F.NIL;
      }
    }
    if (expFactor.isNIL()) {
      return F.NIL;
    }
    IExpr expo = engine.evaluate(F.ExpandAll(expFactor.exponent()));
    IExpr coeffList = engine.evaluate(F.CoefficientList(expo, x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 3) {
      return F.NIL; // need a genuine degree-2 exponent {c0, b, a}
    }
    IAST coeffs = (IAST) coeffList;
    IExpr c0 = coeffs.arg1();
    IExpr b = coeffs.arg2();
    IExpr a = coeffs.arg3();
    if (a.isZero()) {
      return F.NIL;
    }
    IExpr sqrtNegA = F.Sqrt(F.Negate(a));
    // Erf argument is Sqrt(-a)*(x + b/(2a)) = -(2*a*x + b)/(2*Sqrt(-a)); the negation matters so the
    // result differentiates back to +integrand rather than -integrand.
    IExpr erfArg = F.Divide(F.Negate(F.Plus(F.Times(F.C2, a, x), b)), F.Times(F.C2, sqrtNegA));
    IExpr prefactor = F.Times(F.Divide(F.Sqrt(S.Pi), F.Times(F.C2, sqrtNegA)),
        F.Exp(F.Subtract(c0, F.Divide(F.Power(b, F.C2), F.Times(F.ZZ(4), a)))));
    IExpr result = engine.evaluate(F.Times(constFactor, prefactor, F.Erf(erfArg)));
    if (verifyAntiderivative(result, integrand, x, engine)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Hyperexponential Risch case: {@code Integrate(R(x)*E^(b(x)), x)} with {@code R} and {@code b}
   * rational functions and {@code b} non-constant. If elementary, the antiderivative is
   * {@code q(x)*E^(b(x))} where {@code q} solves the Risch differential equation {@code q' + b'*q = R}
   * (delegated to {@link RischDifferentialEquation#solveRationalRDE}). No such {@code q} is a proof
   * that no antiderivative of this form exists (e.g. {@code Integrate(E^(x^2))} has none, so it falls
   * through to the Erf form). This is the first RDE-based (rather than table-based) recogniser.
   */
  private static IExpr integrateHyperexponential(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr rational = F.C1;
    IExpr expFactor = F.NIL;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isPower() && factor.base().equals(S.E)) {
        if (expFactor.isPresent()) {
          return F.NIL;
        }
        expFactor = factor;
      } else {
        rational = F.Times(rational, factor);
      }
    }
    if (expFactor.isNIL()) {
      return F.NIL;
    }
    IExpr b = engine.evaluate(F.Together(expFactor.exponent()));
    if (b.isFree(x, true) || !engine.evaluate(F.PolynomialQ(F.Numerator(b), x)).isTrue()
        || !engine.evaluate(F.PolynomialQ(F.Denominator(b), x)).isTrue()) {
      return F.NIL; // b must be a non-constant rational function
    }
    IExpr r = engine.evaluate(F.Together(rational));
    IExpr q = RischDifferentialEquation.solveRationalRDE(engine.evaluate(F.D(b, x)), r, x, engine);
    if (q.isNIL()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.Times(q, F.Exp(b)));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * {@code Integrate(c*E^(k*x+d)/x^m, x)} for {@code m >= 1}, via the integration-by-parts recurrence
   * {@code I_m = -E^(k*x)/((m-1) x^(m-1)) + k/(m-1) I_(m-1)}, {@code I_1 = ExpIntegralEi(k*x)}.
   */
  private static IExpr integrateExpOverX(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr expFactor = F.NIL;
    IExpr xExponent = F.NIL;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (factor.isPower() && factor.base().equals(S.E)) {
        if (expFactor.isPresent()) {
          return F.NIL;
        }
        expFactor = factor;
      } else {
        IExpr e = xPowerExponent(factor, x);
        if (e.isNIL() || xExponent.isPresent()) {
          return F.NIL;
        }
        xExponent = e;
      }
    }
    if (expFactor.isNIL() || xExponent.isNIL() || !xExponent.isNegative()
        || !xExponent.isInteger()) {
      return F.NIL; // need a reciprocal power 1/x^m, m >= 1
    }
    int m = xExponent.negate().toIntDefault();
    if (m < 1 || m > 20) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(expFactor.exponent()), x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 2) {
      return F.NIL; // need a linear exponent {d, k}
    }
    IExpr d = ((IAST) coeffList).arg1();
    IExpr k = ((IAST) coeffList).arg2();
    if (k.isZero()) {
      return F.NIL;
    }
    IExpr kx = F.Times(k, x);
    IExpr acc = F.ExpIntegralEi(kx);
    for (int mm = 2; mm <= m; mm++) {
      IExpr term = F.Divide(F.Negate(F.Exp(kx)), F.Times(F.ZZ(mm - 1), F.Power(x, F.ZZ(mm - 1))));
      acc = F.Plus(term, F.Times(F.Divide(k, F.ZZ(mm - 1)), acc));
    }
    IExpr result = engine.evaluate(F.Times(c, F.Exp(d), acc));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * {@code Integrate(c*g(k*x)/x, x)} for {@code g} in {@code {Sin, Cos, Sinh, Cosh}} gives {@code c}
   * times the matching {@code SinIntegral / CosIntegral / SinhIntegral / CoshIntegral} of {@code k*x}.
   */
  private static IExpr integrateTrigOverX(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr trigFactor = F.NIL;
    boolean hasReciprocalX = false;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (isTrigKernel(factor)) {
        if (trigFactor.isPresent()) {
          return F.NIL;
        }
        trigFactor = factor;
      } else if (xPowerExponent(factor, x).isMinusOne()) {
        if (hasReciprocalX) {
          return F.NIL;
        }
        hasReciprocalX = true;
      } else {
        return F.NIL;
      }
    }
    if (trigFactor.isNIL() || !hasReciprocalX) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(trigFactor.first()), x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 2
        || !((IAST) coeffList).arg1().isZero()) {
      return F.NIL; // need a pure linear argument k*x (no constant offset)
    }
    IExpr k = ((IAST) coeffList).arg2();
    if (k.isZero()) {
      return F.NIL;
    }
    IExpr kx = F.Times(k, x);
    IExpr integral;
    if (trigFactor.isAST(S.Sin, 2)) {
      integral = F.SinIntegral(kx);
    } else if (trigFactor.isAST(S.Cos, 2)) {
      integral = F.CosIntegral(kx);
    } else if (trigFactor.isAST(S.Sinh, 2)) {
      integral = F.SinhIntegral(kx);
    } else {
      integral = F.CoshIntegral(kx);
    }
    IExpr result = engine.evaluate(F.Times(c, integral));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * {@code Integrate(c*Sin(a*x^2), x) = c*Sqrt(Pi/(2a))*FresnelS(x*Sqrt(2a/Pi))}, and the {@code Cos}
   * analogue with {@code FresnelC} (Symja's Pi/2-normalised Fresnel convention).
   */
  private static IExpr integrateFresnel(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr trigFactor = F.NIL;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (factor.isAST(S.Sin, 2) || factor.isAST(S.Cos, 2)) {
        if (trigFactor.isPresent()) {
          return F.NIL;
        }
        trigFactor = factor;
      } else {
        return F.NIL;
      }
    }
    if (trigFactor.isNIL()) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(trigFactor.first()), x));
    // need a pure quadratic argument a*x^2 : {0, 0, a}
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 3
        || !((IAST) coeffList).arg1().isZero() || !((IAST) coeffList).arg2().isZero()) {
      return F.NIL;
    }
    IExpr a = ((IAST) coeffList).arg3();
    if (a.isZero()) {
      return F.NIL;
    }
    IExpr sqrtPiOver2a = F.Sqrt(F.Divide(S.Pi, F.Times(F.C2, a)));
    IExpr fresnelArg = F.Times(x, F.Sqrt(F.Divide(F.Times(F.C2, a), S.Pi)));
    IExpr fresnel = trigFactor.isAST(S.Sin, 2) ? F.FresnelS(fresnelArg) : F.FresnelC(fresnelArg);
    IExpr result = engine.evaluate(F.Times(c, sqrtPiOver2a, fresnel));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  private static boolean isTrigKernel(IExpr factor) {
    return factor.isAST(S.Sin, 2) || factor.isAST(S.Cos, 2) || factor.isAST(S.Sinh, 2)
        || factor.isAST(S.Cosh, 2);
  }

  /**
   * {@code Integrate(P(x)/Log(x), x) = Sum_k coeff_k * LogIntegral(x^(k+1))} for a polynomial
   * {@code P(x) = Sum_k coeff_k x^k}. The single-monomial case is handled by
   * {@link #integrateReciprocalLog}; this covers genuine sums (two or more terms).
   */
  private static IExpr integratePolyOverLog(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr numerator = F.C1;
    boolean hasReciprocalLog = false;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (isReciprocalLogX(factor, x)) {
        if (hasReciprocalLog) {
          return F.NIL;
        }
        hasReciprocalLog = true;
      } else {
        numerator = F.Times(numerator, factor);
      }
    }
    if (!hasReciprocalLog) {
      return F.NIL;
    }
    IExpr poly = engine.evaluate(F.ExpandAll(numerator));
    if (!engine.evaluate(F.PolynomialQ(poly, x)).isTrue()) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(poly, x));
    if (!coeffList.isList()) {
      return F.NIL;
    }
    IAST coeffs = (IAST) coeffList;
    IASTAppendable sum = F.PlusAlloc(coeffs.argSize());
    for (int k = 0; k < coeffs.argSize(); k++) {
      IExpr ck = coeffs.get(k + 1);
      if (!ck.isZero()) {
        sum.append(F.Times(ck, F.LogIntegral(F.Power(x, F.ZZ(k + 1)))));
      }
    }
    if (sum.argSize() < 2) {
      return F.NIL; // single-term (or empty): the monomial case, handled by integrateReciprocalLog
    }
    IExpr result = engine.evaluate(sum);
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /** {@code Integrate(c*PolyLog(n, a*x)/x, x) = c*PolyLog(n+1, a*x)} (the polylogarithm ladder). */
  private static IExpr integratePolyLogLadder(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr polylogFactor = F.NIL;
    boolean hasReciprocalX = false;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (factor.isAST(S.PolyLog, 3)) {
        if (polylogFactor.isPresent()) {
          return F.NIL;
        }
        polylogFactor = factor;
      } else if (xPowerExponent(factor, x).isMinusOne()) {
        if (hasReciprocalX) {
          return F.NIL;
        }
        hasReciprocalX = true;
      } else {
        return F.NIL;
      }
    }
    if (polylogFactor.isNIL() || !hasReciprocalX) {
      return F.NIL;
    }
    IExpr n = ((IAST) polylogFactor).arg1();
    IExpr arg = ((IAST) polylogFactor).arg2();
    if (!n.isFree(x, true)) {
      return F.NIL;
    }
    // the ladder needs arg = a*x so that arg'/arg = 1/x: a pure linear monomial.
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(arg), x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 2
        || !((IAST) coeffList).arg1().isZero() || ((IAST) coeffList).arg2().isZero()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.Times(c, F.PolyLog(F.Plus(n, F.C1), arg)));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * {@code Integrate(c*x^s*E^(a*x+b), x) = c*E^b*(-(-a)^(-1-s)*Gamma(1+s, -a*x))} &mdash; the upper
   * incomplete Gamma function (equivalently an {@code ExpIntegralE}). The pure reciprocal-integer
   * power case {@code E^(a*x)/x^m} is taken first by {@link #integrateExpOverX} (an {@code Ei} form).
   */
  private static IExpr integrateIncompleteGamma(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr expFactor = F.NIL;
    IExpr xExponent = F.NIL;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (factor.isPower() && factor.base().equals(S.E)) {
        if (expFactor.isPresent()) {
          return F.NIL;
        }
        expFactor = factor;
      } else {
        IExpr e = xPowerExponent(factor, x);
        if (e.isNIL() || xExponent.isPresent()) {
          return F.NIL;
        }
        xExponent = e;
      }
    }
    if (expFactor.isNIL() || xExponent.isNIL()) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(expFactor.exponent()), x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 2) {
      return F.NIL; // need a linear exponent {b, a}
    }
    IExpr b = ((IAST) coeffList).arg1();
    IExpr a = ((IAST) coeffList).arg2();
    if (a.isZero()) {
      return F.NIL;
    }
    IExpr s = xExponent;
    IExpr negA = F.Negate(a);
    IExpr result = engine.evaluate(F.Times(c, F.Exp(b), F.CN1, F.Power(negA, F.Subtract(F.CN1, s)),
        F.Gamma(F.Plus(F.C1, s), F.Times(negA, x))));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /** {@code Integrate(c*x^a/Log(x), x) = c*LogIntegral(x^(a+1))}. */
  private static IExpr integrateReciprocalLog(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr xExponent = F.C0;
    boolean hasReciprocalLog = false;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (isReciprocalLogX(factor, x)) {
        if (hasReciprocalLog) {
          return F.NIL;
        }
        hasReciprocalLog = true;
      } else {
        IExpr e = xPowerExponent(factor, x);
        if (e.isNIL()) {
          return F.NIL;
        }
        xExponent = F.Plus(xExponent, e);
      }
    }
    if (!hasReciprocalLog) {
      return F.NIL;
    }
    IExpr a = engine.evaluate(xExponent);
    IExpr result = engine.evaluate(F.Times(c, F.LogIntegral(F.Power(x, F.Plus(a, F.C1)))));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /** {@code Integrate(c*Log(d+a*x)/x, x) = c*(Log(d)*Log(x) - PolyLog(2, -(a/d)*x))}, {@code d!=0}. */
  private static IExpr integrateLogOverX(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr c = F.C1;
    IExpr logFactor = F.NIL;
    boolean hasReciprocalX = false;
    IAST factors = factorsOf(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
      } else if (factor.isAST(S.Log, 2)) {
        if (logFactor.isPresent()) {
          return F.NIL;
        }
        logFactor = factor;
      } else if (xPowerExponent(factor, x).isMinusOne()) {
        if (hasReciprocalX) {
          return F.NIL;
        }
        hasReciprocalX = true;
      } else {
        return F.NIL;
      }
    }
    if (logFactor.isNIL() || !hasReciprocalX) {
      return F.NIL;
    }
    IExpr coeffList = engine.evaluate(F.CoefficientList(F.ExpandAll(logFactor.first()), x));
    if (!coeffList.isList() || ((IAST) coeffList).argSize() != 2) {
      return F.NIL; // need a linear Log argument {d, a}
    }
    IExpr d = ((IAST) coeffList).arg1();
    IExpr a = ((IAST) coeffList).arg2();
    if (d.isZero() || a.isZero()) {
      return F.NIL; // Log(a*x)/x is elementary; leave it to the Rubi rules
    }
    IExpr result = engine.evaluate(F.Times(c, F.Subtract(F.Times(F.Log(d), F.Log(x)),
        F.PolyLog(F.C2, F.Times(F.CN1, F.Divide(a, d), x)))));
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * Primitive (log) monomial case: integrate a polynomial in a single {@code Log(u)} with rational
   * coefficients using the coefficient recursion {@code q_i' = p_i - (i+1)*q_{i+1}*(u'/u)} over the
   * base field {@code Q(x)} (built on the {@link DifferentialTower} representation). Conservative:
   * succeeds only when every coefficient antiderivative stays rational in {@code x} &mdash; the top
   * level may raise the degree by one {@code Log(u)} (e.g. {@code Integrate(Log(x)/x) = Log(x)^2/2}).
   */
  private static IExpr integratePrimitiveLog(IExpr integrand, IExpr x, EvalEngine engine) {
    DifferentialTower tower = DifferentialTower.build(integrand, x, engine);
    if (tower.monomials().size() != 1) {
      return F.NIL;
    }
    DifferentialTower.Monomial mono = tower.monomials().get(0);
    if (mono.type != DifferentialTower.MonomialType.PRIMITIVE) {
      return F.NIL;
    }
    ISymbol t = mono.symbol;
    IExpr logU = mono.original;
    IExpr eta = mono.derivative;
    IExpr p = tower.towerForm();
    if (!engine.evaluate(F.PolynomialQ(p, t)).isTrue()) {
      return F.NIL;
    }
    int m = engine.evaluate(F.Exponent(p, t)).toIntDefault(-1);
    if (m < 0 || m > 32) {
      return F.NIL;
    }
    IExpr[] q = new IExpr[m + 2];
    q[m + 1] = F.C0;
    for (int i = m; i >= 0; i--) {
      IExpr pi = engine.evaluate(F.Coefficient(p, t, F.ZZ(i)));
      if (!isRationalInX(pi, x, engine)) {
        return F.NIL;
      }
      IExpr rhs = engine.evaluate(F.Subtract(pi, F.Times(F.ZZ(i + 1), q[i + 1], eta)));
      IExpr integral = engine.evaluateNIL(F.Integrate(rhs, x));
      if (integral.isNIL() || !integral.isFreeAST(S.Integrate) || !integral.isSpecialsFree()) {
        return F.NIL;
      }
      if (i == m) {
        // Top level: allow integral = rationalPart + kappa*Log(u) with kappa a constant.
        IExpr kappa = engine.evaluate(F.Coefficient(integral, logU));
        if (!kappa.isFree(x, true)) {
          return F.NIL;
        }
        IExpr rationalPart = engine.evaluate(F.Subtract(integral, F.Times(kappa, logU)));
        if (!isRationalInX(rationalPart, x, engine)) {
          return F.NIL;
        }
        q[i] = rationalPart;
        q[i + 1] = engine.evaluate(F.Divide(kappa, F.ZZ(i + 1)));
      } else {
        if (!isRationalInX(integral, x, engine)) {
          return F.NIL;
        }
        q[i] = integral;
      }
    }
    IASTAppendable sum = F.PlusAlloc(m + 2);
    for (int i = 0; i <= m + 1; i++) {
      if (q[i] != null && !q[i].isZero()) {
        sum.append(F.Times(q[i], F.Power(logU, F.ZZ(i))));
      }
    }
    IExpr result = engine.evaluate(sum);
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  private static boolean isRationalInX(IExpr e, IExpr x, EvalEngine engine) {
    return engine.evaluate(F.PolynomialQ(F.Numerator(e), x)).isTrue()
        && engine.evaluate(F.PolynomialQ(F.Denominator(e), x)).isTrue();
  }

  /**
   * Hypertangent monomial case: integrate a polynomial with constant coefficients in a single
   * {@code Tan(u)}, {@code u} linear (so {@code u' = a} is constant), via the reduction recurrence
   * {@code Integrate(Tan(u)^n) = Tan(u)^(n-1)/(a*(n-1)) - Integrate(Tan(u)^(n-2))} with bases
   * {@code Integrate(1) = x} and {@code Integrate(Tan(u)) = -Log(Cos(u))/a}.
   */
  private static IExpr integrateHypertangent(IExpr integrand, IExpr x, EvalEngine engine) {
    DifferentialTower tower = DifferentialTower.build(integrand, x, engine);
    if (tower.monomials().size() != 1) {
      return F.NIL;
    }
    DifferentialTower.Monomial mono = tower.monomials().get(0);
    if (mono.type != DifferentialTower.MonomialType.HYPERTANGENT) {
      return F.NIL;
    }
    ISymbol t = mono.symbol;
    IExpr u = mono.argument;
    IExpr tanU = mono.original;
    IExpr a = engine.evaluate(F.D(u, x));
    if (!a.isFree(x, true) || a.isZero()) {
      return F.NIL; // need u linear in x
    }
    IExpr p = tower.towerForm();
    if (!engine.evaluate(F.PolynomialQ(p, t)).isTrue()) {
      return F.NIL;
    }
    int m = engine.evaluate(F.Exponent(p, t)).toIntDefault(-1);
    if (m < 0 || m > 32) {
      return F.NIL;
    }
    IExpr[] coefficient = new IExpr[m + 1];
    for (int i = 0; i <= m; i++) {
      coefficient[i] = engine.evaluate(F.Coefficient(p, t, F.ZZ(i)));
      if (!coefficient[i].isFree(x, true)) {
        return F.NIL; // constant coefficients only
      }
    }
    IExpr[] tanIntegral = new IExpr[m + 1];
    for (int n = 0; n <= m; n++) {
      if (n == 0) {
        tanIntegral[0] = x;
      } else if (n == 1) {
        tanIntegral[1] = F.Times(F.CN1, F.Divide(F.Log(F.Cos(u)), a));
      } else {
        tanIntegral[n] = F.Subtract(F.Divide(F.Power(tanU, n - 1), F.Times(a, F.ZZ(n - 1))),
            tanIntegral[n - 2]);
      }
    }
    IASTAppendable sum = F.PlusAlloc(m + 1);
    for (int i = 0; i <= m; i++) {
      if (!coefficient[i].isZero()) {
        sum.append(F.Times(coefficient[i], tanIntegral[i]));
      }
    }
    IExpr result = engine.evaluate(sum);
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  /**
   * Tower RDE recursion for the hyperexponential monomial: integrate a polynomial in a single
   * exponential monomial {@code t = E^(g*x)} (the exponents of all {@code E^(...)} building blocks
   * are integer multiples {@code k*g*x} of a common {@code g*x}), by solving one base-field RDE
   * {@code q_k' + (k*g)*q_k = p_k} per power {@code k} (delegated to
   * {@link RischDifferentialEquation#solveRationalRDE}). This is the poly-in-monomial reduction of
   * the Risch algorithm, giving {@code Integrate} sums of exponentials such as
   * {@code Integrate(E^x + E^(2*x)) = E^x + E^(2*x)/2}.
   */
  private static IExpr integrateHyperexpPolynomial(IExpr integrand, IExpr x, EvalEngine engine) {
    DifferentialTower tower = DifferentialTower.build(integrand, x, engine);
    if (tower.monomials().isEmpty()) {
      return F.NIL;
    }
    IExpr g = F.NIL;
    for (DifferentialTower.Monomial mono : tower.monomials()) {
      if (mono.type != DifferentialTower.MonomialType.HYPEREXPONENTIAL
          || engine.evaluate(F.Exponent(mono.argument, x)).toIntDefault(-1) != 1) {
        return F.NIL; // only exponentials of linear functions
      }
      IExpr slope = engine.evaluate(F.Coefficient(mono.argument, x, F.C1));
      g = g.isNIL() ? slope : engine.evaluate(F.GCD(g, slope));
    }
    if (g.isNIL() || g.isZero()) {
      return F.NIL;
    }
    // Rewrite every E^(a*x + c) as E^c * t^(a/g), giving a polynomial in t = E^(g*x).
    ISymbol t = F.Dummy("hep$t");
    IExpr inT = integrand;
    for (DifferentialTower.Monomial mono : tower.monomials()) {
      IExpr slope = engine.evaluate(F.Coefficient(mono.argument, x, F.C1));
      IExpr constant = engine.evaluate(F.Subtract(mono.argument, F.Times(slope, x)));
      IExpr power = engine.evaluate(F.Divide(slope, g));
      if (!power.isInteger() || !power.isPositive()) {
        return F.NIL; // only non-negative integer powers (a genuine polynomial in t)
      }
      inT = F.subst(inT, mono.original, F.Times(F.Exp(constant), F.Power(t, power)));
    }
    inT = engine.evaluate(inT);
    if (!engine.evaluate(F.PolynomialQ(inT, t)).isTrue()) {
      return F.NIL;
    }
    int m = engine.evaluate(F.Exponent(inT, t)).toIntDefault(-1);
    if (m < 1 || m > 32) {
      return F.NIL; // m < 1 would be a plain rational integrand (not our case)
    }
    IASTAppendable sum = F.PlusAlloc(m + 1);
    for (int k = 0; k <= m; k++) {
      IExpr pk = engine.evaluate(F.Coefficient(inT, t, F.ZZ(k)));
      if (pk.isZero()) {
        continue;
      }
      if (!isRationalInX(pk, x, engine)) {
        return F.NIL;
      }
      IExpr qk;
      if (k == 0) {
        qk = engine.evaluateNIL(F.Integrate(pk, x));
        if (qk.isNIL() || !qk.isFreeAST(S.Integrate) || !isRationalInX(qk, x, engine)) {
          return F.NIL;
        }
      } else {
        qk = RischDifferentialEquation.solveRationalRDE(F.Times(F.ZZ(k), g), pk, x, engine);
        if (qk.isNIL()) {
          return F.NIL;
        }
      }
      sum.append(F.Times(qk, F.Exp(F.Times(F.ZZ(k), g, x))));
    }
    IExpr result = engine.evaluate(sum);
    return verifyAntiderivative(result, integrand, x, engine) ? result : F.NIL;
  }

  private static IAST factorsOf(IExpr integrand) {
    return integrand.isTimes() ? (IAST) integrand : F.Times(integrand);
  }

  /** The exponent {@code e} if {@code factor} is {@code x} or {@code x^e}, else {@link F#NIL}. */
  private static IExpr xPowerExponent(IExpr factor, IExpr x) {
    if (factor.equals(x)) {
      return F.C1;
    }
    if (factor.isPower() && factor.base().equals(x)) {
      return factor.exponent();
    }
    return F.NIL;
  }

  /** True iff {@code factor} is {@code Log(x)^(-1)}. */
  private static boolean isReciprocalLogX(IExpr factor, IExpr x) {
    return factor.isPower() && factor.exponent().isMinusOne() && factor.base().isAST(S.Log, 2)
        && factor.base().first().equals(x);
  }

  /** Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand. */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(diff)).isZero()) {
        return true;
      }
      // Special-function antiderivatives (LogIntegral of x^(a+1), PolyLog, ...) often will not reduce
      // symbolically because of Log branch identities; confirm numerically at generic interior points.
      // This can only reject, so it never accepts a result a nonzero symbolic diff already disproved.
      return numericallyZero(diff, x, engine);
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /** True iff {@code expr} numerically vanishes at every sample point (all chosen in (0, Pi)). */
  private static boolean numericallyZero(IExpr expr, IExpr x, EvalEngine engine) {
    final double[] points = {0.35, 0.9, 1.4, 2.3};
    for (double pt : points) {
      IExpr check;
      try {
        check = engine.evaluate(
            F.Less(F.Abs(F.ReplaceAll(expr, F.Rule(x, F.num(pt)))), F.num(1.0e-9)));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (!check.isTrue()) {
        return false;
      }
    }
    return true;
  }
}
