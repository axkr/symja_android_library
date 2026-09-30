package org.matheclipse.core.numerics.functions;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Carlson's symmetric elliptic integrals in arbitrary precision, by Carlson's duplication algorithms
 * (B. C. Carlson, "Numerical computation of real or complex elliptic integrals", Numer. Algorithms
 * 10, 1995).
 *
 * <p>
 * The generic field versions of hipparchus' <code>CarlsonEllipticIntegral</code> stop after 16
 * duplication steps, which bounds their accuracy to about 45 digits and loses the last digits at
 * lower precisions too. Here the duplication runs until the truncation error of the series is below
 * the requested number of digits.
 *
 * <p>
 * The arguments must be numbers of the requested precision (<code>ApfloatNum</code> or
 * <code>ApcomplexNum</code>).
 */
public final class CarlsonArbitraryPrecision {

  /** A safety net: 4^(-400) is far below any precision which is asked for. */
  private static final int MAX_ITERATIONS = 400;

  private CarlsonArbitraryPrecision() {}

  private static double abs(IExpr z) {
    return z.abs().evalf();
  }

  /**
   * <code>(3*r)^(-1/6)</code> resp. <code>(r/4)^(-1/6)</code> for the relative tolerance
   * <code>r == 10^(-digits)</code>
   */
  private static double tolerance(int digits, double factor) {
    return Math.pow(10.0, digits / 6.0) * Math.pow(factor, -1.0 / 6.0);
  }

  private static int zeros(IExpr x, IExpr y, IExpr z) {
    return (x.isZero() ? 1 : 0) + (y.isZero() ? 1 : 0) + (z.isZero() ? 1 : 0);
  }

  /** <code>Sqrt(x)*Sqrt(y) + Sqrt(x)*Sqrt(z) + Sqrt(y)*Sqrt(z)</code> */
  private static IExpr lambda(IExpr sx, IExpr sy, IExpr sz) {
    return sx.times(sy).plus(sx.times(sz)).plus(sy.times(sz));
  }

  /**
   * <code>CarlsonRF(x,y,z)</code>
   *
   * @param digits the number of correct digits which are wanted
   */
  public static IExpr rF(IExpr x, IExpr y, IExpr z, int digits) {
    if (zeros(x, y, z) >= 2) {
      // the integral diverges
      return F.CComplexInfinity;
    }
    IExpr a0 = x.plus(y).plus(z).divide(F.C3);
    double q = tolerance(digits, 3.0)
        * Math.max(abs(a0.subtract(x)), Math.max(abs(a0.subtract(y)), abs(a0.subtract(z))));
    IExpr xm = x;
    IExpr ym = y;
    IExpr zm = z;
    IExpr am = a0;
    double pow4 = 1.0;
    // 4^(-m) exactly, a double would round the result to machine precision
    IExpr quarterPower = F.C1;
    for (int m = 0; m < MAX_ITERATIONS && pow4 * q >= abs(am); m++) {
      IExpr lm = lambda(xm.sqrt(), ym.sqrt(), zm.sqrt());
      xm = xm.plus(lm).times(F.C1D4);
      ym = ym.plus(lm).times(F.C1D4);
      zm = zm.plus(lm).times(F.C1D4);
      am = am.plus(lm).times(F.C1D4);
      pow4 *= 0.25;
      quarterPower = quarterPower.times(F.C1D4);
    }
    // X == (A0-x)/(4^m*Am)
    IExpr t = am.reciprocal().times(quarterPower);
    IExpr bigX = a0.subtract(x).times(t);
    IExpr bigY = a0.subtract(y).times(t);
    IExpr bigZ = bigX.plus(bigY).negate();
    IExpr e2 = bigX.times(bigY).subtract(bigZ.times(bigZ));
    IExpr e3 = bigX.times(bigY).times(bigZ);
    // (1 - E2/10 + E3/14 + E2^2/24 - 3*E2*E3/44) / Sqrt(Am)
    IExpr series = e2.times(F.QQ(-1, 10)).plus(e3.times(F.QQ(1, 14)))
        .plus(e2.times(e2).times(F.QQ(1, 24))).subtract(e2.times(e3).times(F.QQ(3, 44)))
        .plus(F.C1);
    return series.times(am.sqrt().reciprocal());
  }

  /** <code>CarlsonRD(x,y,z) == CarlsonRJ(x,y,z,z)</code> */
  public static IExpr rD(IExpr x, IExpr y, IExpr z, int digits) {
    return rJ(x, y, z, z, digits);
  }

  /** <code>CarlsonRJ(x,y,z,p)</code> */
  public static IExpr rJ(IExpr x, IExpr y, IExpr z, IExpr p, int digits) {
    if (p.isZero() || zeros(x, y, z) >= 2) {
      // the integral diverges
      return F.CComplexInfinity;
    }
    IExpr a0 = x.plus(y).plus(z).plus(p.times(F.C2)).divide(F.C5);
    IExpr delta = p.subtract(x).times(p.subtract(y)).times(p.subtract(z));
    double q = tolerance(digits, 0.25) * Math.max(
        Math.max(abs(a0.subtract(x)), abs(a0.subtract(y))),
        Math.max(abs(a0.subtract(z)), abs(a0.subtract(p))));
    IExpr xm = x;
    IExpr ym = y;
    IExpr zm = z;
    IExpr pm = p;
    IExpr am = a0;
    IExpr sum = F.C0;
    double pow4 = 1.0;
    // 4^(-m) exactly, a double would round the result to machine precision
    IExpr quarterPower = F.C1;
    // 4^(-3*m) as an exact rational, because it underflows a double for many iterations
    IExpr pow64 = F.C1;
    int m = 0;
    for (; m < MAX_ITERATIONS; m++) {
      IExpr sx = xm.sqrt();
      IExpr sy = ym.sqrt();
      IExpr sz = zm.sqrt();
      IExpr sp = pm.sqrt();
      IExpr lm = lambda(sx, sy, sz);
      IExpr dm = sp.plus(sx).times(sp.plus(sy)).times(sp.plus(sz));
      if (pow4 * q < abs(am)) {
        break;
      }
      // e(m) == delta*4^(-3*m)/d(m)^2 and the term 4^(-m)*RC(1,1+e(m))/d(m)
      IExpr em = delta.times(pow64).divide(dm.times(dm));
      sum = sum.plus(rC1(em).times(quarterPower).divide(dm));
      xm = xm.plus(lm).times(F.C1D4);
      ym = ym.plus(lm).times(F.C1D4);
      zm = zm.plus(lm).times(F.C1D4);
      pm = pm.plus(lm).times(F.C1D4);
      am = am.plus(lm).times(F.C1D4);
      pow4 *= 0.25;
      quarterPower = quarterPower.times(F.C1D4);
      pow64 = pow64.times(F.QQ(1, 64));
    }
    IExpr t = am.reciprocal().times(quarterPower);
    IExpr bigX = a0.subtract(x).times(t);
    IExpr bigY = a0.subtract(y).times(t);
    IExpr bigZ = a0.subtract(z).times(t);
    IExpr bigP = bigX.plus(bigY).plus(bigZ).times(F.CN1D2);
    IExpr xyz = bigX.times(bigY).times(bigZ);
    IExpr p2 = bigP.times(bigP);
    IExpr p3 = p2.times(bigP);
    IExpr e2 = bigX.times(bigY).plus(bigX.times(bigZ)).plus(bigY.times(bigZ))
        .subtract(p2.times(F.C3));
    IExpr e3 = xyz.plus(e2.times(bigP).times(F.C2)).plus(p3.times(F.C4));
    IExpr e4 = xyz.times(F.C2).plus(e2.times(bigP)).plus(p3.times(F.C3)).times(bigP);
    IExpr e5 = xyz.times(p2);
    // (24024 - 5148*E2 + 2457*E2^2 + 4004*E3 - 4158*E2*E3 - 3276*E4 + 2772*E5) / 24024
    IExpr series = e2.times(F.ZZ(-5148)).plus(e2.times(e2).times(F.ZZ(2457)))
        .plus(e3.times(F.ZZ(4004))).subtract(e2.times(e3).times(F.ZZ(4158)))
        .subtract(e4.times(F.ZZ(3276))).plus(e5.times(F.ZZ(2772))).plus(F.ZZ(24024))
        .divide(F.ZZ(24024));
    // 4^(-m) * Am^(-3/2) * series + 6*sum
    IExpr v1 = am.times(am.sqrt()).reciprocal().times(quarterPower).times(series);
    return v1.plus(sum.times(F.C6));
  }

  /**
   * <code>CarlsonRG(x,y,z) == (z*RF - (x-z)*(y-z)*RD/3 + Sqrt(x*y/z))/2</code>, with a
   * permutation which makes <code>z</code> non-zero.
   */
  public static IExpr rG(IExpr x, IExpr y, IExpr z, int digits) {
    if (zeros(x, y, z) >= 2) {
      // CarlsonRG(0,0,z) == Sqrt(z)/2
      return x.plus(y).plus(z).sqrt().times(F.C1D2);
    }
    if (z.isZero()) {
      if (!x.isZero()) {
        return rG(y, z, x, digits);
      }
      return rG(z, x, y, digits);
    }
    IExpr rf = rF(x, y, z, digits);
    IExpr rd = rD(x, y, z, digits);
    return z.times(rf).subtract(x.subtract(z).times(y.subtract(z)).times(rd).divide(F.C3))
        .plus(x.times(y).divide(z).sqrt()).times(F.C1D2);
  }

  /**
   * <code>CarlsonRC(1,1+e) == ArcTan(Sqrt(e))/Sqrt(e)</code>, which tends to <code>1</code> for
   * <code>e -&gt; 0</code>.
   */
  private static IExpr rC1(IExpr e) {
    if (e.isZero()) {
      return F.C1;
    }
    IExpr s = e.sqrt();
    return EvalEngine.get().evaluate(F.ArcTan(s)).divide(s);
  }
}
