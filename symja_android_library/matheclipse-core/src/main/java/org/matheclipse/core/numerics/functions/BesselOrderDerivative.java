package org.matheclipse.core.numerics.functions;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.ParserConfig;

/**
 * The derivative of a Bessel function with respect to its order,
 * <code>Derivative(1, 0)[f][nu, z]</code> for <code>f = BesselJ, BesselI, BesselY, BesselK</code>
 * at numbers: from the power series
 * <code>d/dnu ((z/2)^nu Sum(s^k (z^2/4)^k/(k! Gamma(k+nu+1)))) = Log(z/2) f - (z/2)^nu Sum(s^k (z^2/4)^k PolyGamma(k+nu+1)/(k! Gamma(k+nu+1)))</code>,
 * <code>s = -1</code> for <code>BesselJ</code> and <code>1</code> for <code>BesselI</code>.
 * <code>BesselY</code> and <code>BesselK</code> of a non-integer order are the derivatives of
 * their connection formulas with the functions of the orders <code>nu</code> and
 * <code>-nu</code>.
 */
public final class BesselOrderDerivative {

  /** The digits of the series which are added to those of a machine result. */
  private static final long MACHINE_DIGITS = 30L;

  /** The largest number of terms of a series. */
  private static final int MAX_TERMS = 100_000;

  private BesselOrderDerivative() {}

  /**
   * <code>Derivative(1, 0)[function][nu, z]</code> for numbers <code>nu</code> and
   * <code>z</code>, one of them inexact.
   *
   * @return {@link F#NIL} if the derivative isn't computed here
   */
  public static IExpr derivative(IExpr function, IExpr nu, IExpr z, EvalEngine engine) {
    if (!(function == S.BesselJ || function == S.BesselI || function == S.BesselY
        || function == S.BesselK)) {
      return F.NIL;
    }
    if (!nu.isNumber() || !z.isNumber() || !(nu.isInexactNumber() || z.isInexactNumber())) {
      return F.NIL;
    }
    boolean machine = isMachine(nu) || isMachine(z);
    long digits = machine ? ParserConfig.MACHINE_PRECISION
        : Math.min(precisionOf(nu), precisionOf(z));
    if ((function == S.BesselY || function == S.BesselK) && isNonNegativeInteger(nu)) {
      return integerOrder(function, ((INumber) nu).apcomplexValue().real().intValue(), z, machine,
          digits, engine);
    }
    long work = machine ? MACHINE_DIGITS : digits + 10L;
    Apcomplex order = ((INumber) nu).apcomplexValue();
    Apcomplex argument = ((INumber) z).apcomplexValue();
    if (argument.real().signum() == 0 && argument.imag().signum() == 0) {
      return F.NIL;
    }
    // the terms grow like Exp(|z|) before they decrease: guard digits for the cancellation
    long extra = (long) Math.ceil(ApcomplexMath.abs(argument).doubleValue() / Math.log(10.0)) + 5L;
    if ((function == S.BesselY || function == S.BesselK) && order.imag().signum() == 0) {
      // the connection formulas divide by Sin(nu Pi)^2: next to an integer order they cancel
      // twice the digits of the distance to it
      Apfloat re = order.real();
      Apfloat distance = ApfloatMath.abs(re.subtract(new Apfloat(Math.rint(re.doubleValue()))));
      if (distance.signum() != 0 && distance.scale() < 0) {
        extra += 2L * (-distance.scale()) + 5L;
      }
    }
    FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(work + extra);
    order = h.valueOf(order);
    argument = h.valueOf(argument);
    Apcomplex value;
    try {
      if (function == S.BesselJ || function == S.BesselI) {
        value = series(order, argument, function == S.BesselJ, h, engine)[1];
      } else {
        value = connection(order, argument, function == S.BesselY, h, engine);
      }
    } catch (ArithmeticException ae) {
      return F.NIL;
    }
    if (value == null) {
      return F.NIL;
    }
    if (machine) {
      double re = value.real().doubleValue();
      double im = value.imag().doubleValue();
      return im == 0.0 ? F.num(re) : F.complexNum(re, im);
    }
    value = value.precision(digits);
    return value.imag().signum() == 0 ? F.num(value.real()) : F.complexNum(value);
  }

  /** The largest integer order whose derivative is the finite sum of the integer order. */
  private static final int MAX_INTEGER_ORDER = 1000;

  private static boolean isNonNegativeInteger(IExpr nu) {
    if (!nu.isReal()) {
      return false;
    }
    Apfloat value = ((INumber) nu).apcomplexValue().real();
    return value.signum() >= 0 && value.isInteger()
        && value.compareTo(new Apfloat(MAX_INTEGER_ORDER)) <= 0;
  }

  /**
   * The order derivative at an integer order <code>n &gt;= 0</code>:
   * <code>-Pi/2 J_n(z) + n!/2 (z/2)^-n Sum((z/2)^k Y_k(z)/(k! (n-k)), {k, 0, n-1})</code> for
   * <code>BesselY</code>,
   * <code>n!/2 (z/2)^-n Sum((z/2)^k K_k(z)/(k! (n-k)), {k, 0, n-1})</code> for <code>BesselK</code>.
   */
  private static IExpr integerOrder(IExpr function, int n, IExpr z, boolean machine, long digits,
      EvalEngine engine) {
    IExpr half = F.Times(F.C1D2, z);
    org.matheclipse.core.interfaces.IASTAppendable sum = F.PlusAlloc(n + 1);
    for (int k = 0; k < n; k++) {
      sum.append(F.Times(F.Power(half, F.ZZ(k)), F.binaryAST2(function, F.ZZ(k), z),
          F.Power(F.Times(F.Factorial(F.ZZ(k)), F.ZZ(n - k)), F.CN1)));
    }
    IExpr finite = F.Times(F.C1D2, F.Factorial(F.ZZ(n)), F.Power(half, F.ZZ(-n)), sum.oneIdentity0());
    IExpr derivative = function == S.BesselY
        ? F.Plus(F.Times(F.CN1D2, S.Pi, F.BesselJ(F.ZZ(n), z)), finite)
        : finite;
    IExpr value = machine ? engine.evalN(derivative) : engine.evalN(derivative, digits);
    return value.isNumber() ? value : F.NIL;
  }

  private static boolean isMachine(IExpr x) {
    return x.isInexactNumber() && (x instanceof org.matheclipse.core.interfaces.INum
        || x instanceof org.matheclipse.core.interfaces.IComplexNum)
        && !(x instanceof org.matheclipse.core.expression.ApfloatNum)
        && !(x instanceof org.matheclipse.core.expression.ApcomplexNum);
  }

  private static long precisionOf(IExpr x) {
    if (!x.isInexactNumber()) {
      return Long.MAX_VALUE;
    }
    return ((INumber) x).apcomplexValue().precision();
  }

  /**
   * The function <code>{J_nu(z), dJ_nu(z)/dnu}</code> (<code>bessel</code>) or
   * <code>{I_nu(z), dI_nu(z)/dnu}</code>.
   *
   * @return <code>null</code> at a negative integer order, where the terms have poles
   */
  private static Apcomplex[] series(Apcomplex nu, Apcomplex z, boolean bessel,
      FixedPrecisionApfloatHelper h, EvalEngine engine) {
    Apfloat re = nu.real();
    if (nu.imag().signum() == 0 && re.signum() < 0 && re.isInteger()) {
      return null;
    }
    Apfloat one = new Apfloat(1, h.precision());
    Apcomplex half = h.divide(z, new Apfloat(2, h.precision()));
    Apcomplex quarter = h.multiply(half, half);
    if (bessel) {
      quarter = h.negate(quarter);
    }
    // t_k = s^k (z^2/4)^k/(k! Gamma(k+nu+1)), psi_k = PolyGamma(k+nu+1)
    Apcomplex shifted = h.add(nu, one);
    Apcomplex t = h.divide(one, h.gamma(shifted));
    Apcomplex psi = h.digamma(shifted);
    Apcomplex sum = t;
    Apcomplex weighted = h.multiply(t, psi);
    for (int k = 1; k < MAX_TERMS; k++) {
      if ((k & 255) == 0) {
        engine.checkTimeLimit();
      }
      Apcomplex index = h.add(nu, new Apfloat(k, h.precision()));
      t = h.divide(h.multiply(t, quarter), h.multiply(new Apfloat(k, h.precision()), index));
      psi = h.add(psi, h.divide(one, index));
      Apcomplex term = h.multiply(t, psi);
      sum = h.add(sum, t);
      weighted = h.add(weighted, term);
      if (k > 2.0 * ApcomplexMath.abs(half).doubleValue() && negligible(t, sum, h)
          && negligible(term, weighted, h)) {
        Apcomplex power = h.pow(half, nu);
        Apcomplex value = h.multiply(power, sum);
        Apcomplex derivative =
            h.subtract(h.multiply(h.log(half), value), h.multiply(power, weighted));
        return new Apcomplex[] {value, derivative};
      }
    }
    return null;
  }

  private static boolean negligible(Apcomplex term, Apcomplex sum,
      FixedPrecisionApfloatHelper h) {
    Apfloat size = ApcomplexMath.abs(term);
    if (size.signum() == 0) {
      return true;
    }
    Apfloat total = ApcomplexMath.abs(sum);
    return total.signum() != 0 && size.scale() < total.scale() - h.precision();
  }

  /**
   * The order derivative of
   * <code>Y_nu = (J_nu Cos(nu Pi) - J_-nu)/Sin(nu Pi)</code> (<code>bessel</code>) or of
   * <code>K_nu = Pi/2 (I_-nu - I_nu)/Sin(nu Pi)</code>.
   *
   * @return <code>null</code> at an integer order
   */
  private static Apcomplex connection(Apcomplex nu, Apcomplex z, boolean bessel,
      FixedPrecisionApfloatHelper h, EvalEngine engine) {
    if (nu.imag().signum() == 0 && nu.real().isInteger()) {
      return null;
    }
    Apfloat pi = h.pi();
    Apcomplex angle = h.multiply(nu, pi);
    Apcomplex c = h.cos(angle);
    Apcomplex s = h.sin(angle);
    Apcomplex[] plus = series(nu, z, bessel, h, engine);
    Apcomplex[] minus = series(h.negate(nu), z, bessel, h, engine);
    if (plus == null || minus == null) {
      return null;
    }
    Apcomplex numerator;
    Apcomplex dNumerator;
    if (bessel) {
      // N = J_nu c - J_-nu, dN = J'_nu c - Pi s J_nu + J'(-nu)
      numerator = h.subtract(h.multiply(plus[0], c), minus[0]);
      dNumerator = h.add(h.subtract(h.multiply(plus[1], c), h.multiply(pi, h.multiply(s, plus[0]))),
          minus[1]);
    } else {
      // M = Pi/2 (I_-nu - I_nu), dM = Pi/2 (-I'(-nu) - I'_nu)
      Apfloat halfPi = h.divide(pi, new Apfloat(2, h.precision()));
      numerator = h.multiply(halfPi, h.subtract(minus[0], plus[0]));
      dNumerator = h.multiply(halfPi, h.negate(h.add(minus[1], plus[1])));
    }
    // (dN s - N Pi c)/s^2
    return h.divide(h.subtract(h.multiply(dNumerator, s), h.multiply(numerator, h.multiply(pi, c))),
        h.multiply(s, s));
  }
}
