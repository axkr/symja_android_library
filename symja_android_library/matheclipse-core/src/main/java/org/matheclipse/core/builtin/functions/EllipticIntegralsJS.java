package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.elliptic.carlson.CarlsonEllipticIntegral;
import org.hipparchus.special.elliptic.legendre.LegendreEllipticIntegral;
import org.matheclipse.core.eval.exception.ArgumentTypeException;

/**
 * Ported from JavaScript file <a href=
 * "https://github.com/paulmasson/math/blob/master/src/functions/elliptic-integrals.js">elliptic-integrals.js</a>
 */
public class EllipticIntegralsJS extends JS {
  private EllipticIntegralsJS() {}

  public static Complex kleinJ(Complex x) {
    // from mpmath / elliptic.py

    Complex q = new Complex(0, Math.PI).multiply(x).exp();
    // TODO add Chop()
    Complex t2 = EllipticFunctionsJS.jacobiTheta(2, Complex.ZERO, q);
    Complex t3 = EllipticFunctionsJS.jacobiTheta(3, Complex.ZERO, q);
    Complex t4 = EllipticFunctionsJS.jacobiTheta(4, Complex.ZERO, q);
    Complex P = t2.pow(8.0).add(t3.pow(8.0)).add(t4.pow(8)).pow(3.0);
    Complex Q = t2.multiply(t3).multiply(t4).pow(8.0).multiply(54.0);

    return P.divide(Q);
  }

  public static Complex kleinJ(double x) {
    return kleinJ(new Complex(x));
  }

  // Carlson symmetric integrals

  public static Complex carlsonRC(Complex x, Complex y) {
    return CarlsonEllipticIntegral.rC(x, y);
    // if (Complex.equals(x, y, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
    // return x.sqrt().reciprocal();
    // }
    // return x.sqrt()
    // .divide(y.sqrt())
    // .acos()
    // .divide(y.sqrt().multiply(Complex.ONE.subtract(x.divide(y)).sqrt()));
  }

  public static double carlsonRC(double x, double y) {
    return CarlsonEllipticIntegral.rC(x, y);
    // if (x < 0 || y < 0) {
    // return carlsonRC(new Complex(x), new Complex(y));
    // }
    // if (x == y) {
    // return new Complex(1 / x).sqrt();
    // }
    //
    // if (x < y) {
    // return new Complex(Math.acos(Math.sqrt(x / y)) / Math.sqrt(y - x));
    // }
    //
    // return new Complex(FastMath.acosh(Math.sqrt(x / y)) / Math.sqrt(x - y));
  }

  public static Complex carlsonRD(Complex x, Complex y, Complex z) {
    return carlsonRJ(x, y, z, z);
  }

  public static double carlsonRD(double x, double y, double z) {
    return carlsonRJ(x, y, z, z);
  }

  public static Complex carlsonRF(Complex x, Complex y, Complex z) {
    return CarlsonEllipticIntegral.rF(x, y, z);
    // return carlsonRF(x, y, z, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  private static Complex carlsonRF(Complex x, Complex y, Complex z, double tolerance) {

    // if ( isComplex(x) || isComplex(y) || isComplex(z) ) {
    // if (y.getImaginary()==0.0) {
    // y = new Complex(y.getReal());
    // }
    Complex xm = x;
    Complex ym = y;
    Complex zm = z;
    Complex A0 = x.add(y).add(z).divide(3.0);
    Complex Am = A0;

    double Q = Math.pow(3.0 * tolerance, -1.0 / 6.0)
        * Math.max(cabs(A0.subtract(x)), Math.max(cabs(A0.subtract(y)), cabs(A0.subtract(z))));
    double g = 0.25;
    double pow4 = 1.0;

    while (true) {
      double absAm = cabs(Am);
      if (Double.isNaN(absAm) || Double.isInfinite(absAm)) {
        throw new ArgumentTypeException("carlsonRF: Am is undefined");
      }
      Complex xs = xm.sqrt();
      Complex ys = ym.sqrt();
      Complex zs = zm.sqrt();
      Complex lm = xs.multiply(ys).add(xs.multiply(zs)).add(ys.multiply(zs));
      // Complex Am1 = Am.add(lm).multiply(g);
      xm = xm.add(lm).multiply(g);
      ym = ym.add(lm).multiply(g);
      zm = zm.add(lm).multiply(g);
      if (pow4 * Q < absAm) {
        break;
      }
      // Am=Am1;
      Am = Am.add(lm).multiply(g);
      pow4 *= g;
    }

    Complex t = new Complex(pow4).divide(Am);
    Complex X = A0.subtract(x).multiply(t);
    Complex Y = A0.subtract(y).multiply(t);
    Complex Z = X.add(Y).negate();
    Complex E2 = X.multiply(Y).subtract(Z.multiply(Z));
    Complex E3 = X.multiply(Y).multiply(Z);
    // Am.pow(-0.5)
    Complex AmPow = Am.pow(-0.5);

    return AmPow.multiply(E2.multiply(-924.0).add(E2.multiply(E2).multiply(385.0))
        .add(E3.multiply(660.0)).add(E2.multiply(E3).multiply(-630.0)).add(9240.0))
        .multiply(1.0 / 9240.0);
  }

  public static double carlsonRF(double x, double y, double z) {
    return CarlsonEllipticIntegral.rF(x, y, z);
    // return carlsonRF(x, y, z, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  private static double carlsonRF(double x, double y, double z, double tolerance) {
    if (y == z) {
      return carlsonRC(x, y);
    }
    if (x == z) {
      return carlsonRC(y, x);
    }
    if (x == y) {
      return carlsonRC(z, x);
    }

    // adapted from mpmath / elliptic.py
    double xm = x;
    double ym = y;
    double zm = z;
    double A0 = (x + y + z) / 3.0;
    double Am = A0;

    double Q = Math.pow(3.0 * tolerance, -1.0 / 6.0)
        * Math.max(Math.max(Math.abs(A0 - x), Math.abs(A0 - y)), Math.abs(A0 - z));
    double g = .25;
    double pow4 = 1.0;

    while (true) {
      double xs = Math.sqrt(xm);
      double ys = Math.sqrt(ym);
      double zs = Math.sqrt(zm);
      double lm = xs * ys + xs * zs + ys * zs;
      double Am1 = (Am + lm) * g;
      xm = (xm + lm) * g;
      ym = (ym + lm) * g;
      zm = (zm + lm) * g;
      if (pow4 * Q < Math.abs(Am)) {
        break;
      }
      Am = Am1;
      pow4 *= g;
    }

    double t = pow4 / Am;
    double X = (A0 - x) * t;
    double Y = (A0 - y) * t;
    double Z = -X - Y;
    double E2 = X * Y - Z * Z;
    double E3 = X * Y * Z;

    return Math.pow(Am, -0.5)
        * (9240.0 - 924.0 * E2 + 385.0 * E2 * E2 + 660.0 * E3 - 630.0 * E2 * E3) / 9240.0;
  }

  public static double carlsonRG(double x, double y, double z) {
    return CarlsonEllipticIntegral.rG(x, y, z);
  }

  public static Complex carlsonRG(Complex x, Complex y, Complex z) {
    return CarlsonEllipticIntegral.rG(x, y, z);
    // Complex t1 = carlsonRF(x, y, z).multiply(z);
    // Complex t2 =
    // x.subtract(z).multiply(y.subtract(z)).multiply(carlsonRD(x, y, z)).multiply(-1.0 /
    // 3.0);
    // Complex t3 = x.multiply(y).multiply(z.reciprocal()).sqrt();
    //
    // return t1.add(t2).add(t3).multiply(0.5);
  }

  public static Complex carlsonRJ(Complex x, Complex y, Complex z, Complex p) {
    return CarlsonEllipticIntegral.rJ(x, y, z, p);
    // return carlsonRJ(x, y, z, p, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  private static Complex carlsonRJ(Complex x, Complex y, Complex z, Complex p, double tolerance) {

    // if ( isComplex(x) || isComplex(y) || isComplex(z) || isComplex(p) ) {

    Complex xm = x;
    Complex ym = y;
    Complex zm = z;
    Complex pm = p;

    Complex Am = x.add(y).add(z).add(p.multiply(2)).divide(5.0);
    Complex A0 = Am;
    Complex delta = p.subtract(x).multiply(p.subtract(y)).multiply(p.subtract(z));
    double Q = Math.pow(0.25 * tolerance, -1.0 / 6.0) * Math.max(cabs(A0.subtract(x)),
        Math.max(cabs(A0.subtract(y)), Math.max(cabs(A0.subtract(z)), cabs(A0.subtract(p)))));
    double m = 0.0;
    double g = 0.25;
    double pow4 = 1.0;
    Complex S = Complex.ZERO;

    while (true) {
      Complex sx = xm.sqrt();
      Complex sy = ym.sqrt();
      Complex sz = zm.sqrt();
      Complex sp = pm.sqrt();
      Complex lm = sx.multiply(sy).add(sx.multiply(sz)).add(sy.multiply(sz));
      Complex Am1 = Am.add(lm).multiply(g);
      xm = xm.add(lm).multiply(g);
      ym = ym.add(lm).multiply(g);
      zm = zm.add(lm).multiply(g);
      pm = pm.add(lm).multiply(g);
      Complex dm = sp.add(sx).multiply(sp.add(sy)).multiply(sp.add(sz));
      Complex em = dm.reciprocal().multiply(dm.reciprocal()).multiply(delta)
          .multiply(Math.pow(4.0, -3.0 * m));
      if (pow4 * Q < cabs(Am)) {
        break;
      }
      Complex T = carlsonRC(Complex.ONE, em.add(1)).multiply(pow4).multiply(dm.reciprocal());
      S = S.add(T);
      pow4 *= g;
      m += 1;
      Am = Am1;
    }

    Complex t = Am.reciprocal().multiply(Math.pow(2, -2 * m));
    Complex X = A0.subtract(x).multiply(t);
    Complex Y = A0.subtract(y).multiply(t);
    Complex Z = A0.subtract(z).multiply(t);
    Complex P = X.add(Y.add(Z)).divide(-2);
    Complex E2 =
        X.multiply(Y).add(X.multiply(Z)).add(Y.multiply(Z)).add(P.multiply(P).multiply(-3));
    Complex E3 = X.multiply(Y).multiply(Z).add(E2.multiply(P).multiply(2))
        .add(P.multiply(P).multiply(P).multiply(4));
    Complex E4 = X.multiply(Y).multiply(Z).multiply(2).add(E2.multiply(P))
        .add(P.multiply(P).multiply(P).multiply(3)).multiply(P);
    Complex E5 = X.multiply(Y).multiply(Z).multiply(P).multiply(P);
    P = E2.multiply(-5148).add(E2.multiply(E2).multiply(2457)).add(E3.multiply(4004))
        .add(E2.multiply(E3).multiply(-4158)).add(E4.multiply(-3276)).add(E5.multiply(2772))
        .add(24024);
    Complex v1 = Am.pow(-1.5).multiply(Math.pow(g, m)).multiply(P).multiply(1.0 / 24024.0);

    return S.multiply(6.0).add(v1);
  }

  public static double carlsonRJ(double x, double y, double z, double p) {
    return CarlsonEllipticIntegral.rJ(x, y, z, p);
    // return carlsonRJ(x, y, z, p, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  private static Complex carlsonRJ(double x, double y, double z, double p, double tolerance) {
    // adapted from mpmath / elliptic.py

    double xm = x;
    double ym = y;
    double zm = z;
    double pm = p;

    double Am = (x + y + z + 2 * p) / 5.0;
    double A0 = Am;
    double delta = (p - x) * (p - y) * (p - z);
    double Q = Math.pow(.25 * tolerance, -1.0 / 6.0) * Math.max(Math.abs(A0 - x),
        Math.max(Math.abs(A0 - y), Math.max(Math.abs(A0 - z), Math.abs(A0 - p))));
    double m = 0;
    double g = 0.25;
    double pow4 = 1;
    Complex S = Complex.ZERO;

    while (true) {
      double sx = Math.sqrt(xm);
      double sy = Math.sqrt(ym);
      double sz = Math.sqrt(zm);
      double sp = Math.sqrt(pm);
      double lm = sx * sy + sx * sz + sy * sz;
      double Am1 = (Am + lm) * g;
      xm = (xm + lm) * g;
      ym = (ym + lm) * g;
      zm = (zm + lm) * g;
      pm = (pm + lm) * g;
      double dm = (sp + sx) * (sp + sy) * (sp + sz);
      double em = delta * Math.pow(4.0, -3.0 * m) / Math.pow(dm, 2);
      if (pow4 * Q < Math.abs(Am)) {
        break;
      }
      Complex T = Complex.valueOf(carlsonRC(1, 1 + em)).multiply(pow4 / dm);
      S = S.add(T);
      pow4 *= g;
      m += 1;
      Am = Am1;
    }

    double t = Math.pow(2, -2 * m) / Am;
    double X = (A0 - x) * t;
    double Y = (A0 - y) * t;
    double Z = (A0 - z) * t;
    double P = (-X - Y - Z) / 2.0;
    double E2 = X * Y + X * Z + Y * Z - 3 * Math.pow(P, 2);
    double E3 = X * Y * Z + 2 * E2 * P + 4 * Math.pow(P, 3);
    double E4 = (2 * X * Y * Z + E2 * P + 3 * Math.pow(P, 3)) * P;
    double E5 = X * Y * Z * Math.pow(P, 2);
    P = 24024 - 5148 * E2 + 2457 * Math.pow(E2, 2) + 4004 * E3 - 4158 * E2 * E3 - 3276 * E4
        + 2772 * E5;
    double v1 = Math.pow(g, m) * Math.pow(Am, -1.5) * P / 24024.0;

    return S.multiply(6.0).add(v1);
  }

  // elliptic integrals

  public static Complex ellipticF(Complex x, Complex m) {
    // https://github.com/Hipparchus-Math/hipparchus/issues/151
    // return LegendreEllipticIntegral.bigF(x, m);
    Complex period = Complex.ZERO;
    if (Math.abs(x.getReal()) > (Math.PI / 2)) {
      long p = Math.round(x.getReal() / Math.PI);
      x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
      period = ellipticK(m).multiply(p + p);
    }

    Complex sinX = x.sin();
    Complex cosX = x.cos();
    if (cosX.getImaginary() == 0.0) {
      cosX = new Complex(cosX.getReal());
    }
    Complex sqrSinX = sinX.multiply(sinX);
    Complex sqrCosX = cosX.multiply(cosX);
    return sinX.multiply(carlsonRF(sqrCosX, Complex.ONE.subtract(m.multiply(sqrSinX)), Complex.ONE))
        .add(period);
  }

  public static Complex ellipticF(double x, double m) {
    if (m > 1 && Math.abs(x) > Math.asin(1 / Math.sqrt(m))) {
      return ellipticF(new Complex(x), new Complex(m));
    }
    // https://github.com/Hipparchus-Math/hipparchus/issues/151
    // return Complex.valueOf(LegendreEllipticIntegral.bigF(x, m));
    Complex period = Complex.ZERO;
    if (Math.abs(x) > Math.PI / 2.0) {
      long p = Math.round(x / Math.PI);
      x = x - p * Math.PI;
      period = ellipticK(m).multiply(p + p);
    }

    double sinX = Math.sin(x);
    double cosX = Math.cos(x);
    double sqrSinX = sinX * sinX;
    double sqrCosX = cosX * cosX;
    double mSqrSinX = 1 - m * sqrSinX;
    // if (mSqrSinX < 0) {
    // return carlsonRF(new Complex(sqrCosX), new Complex(mSqrSinX),
    // Complex.ONE).multiply(sinX).add(period);
    // }
    return Complex.valueOf(carlsonRF(sqrCosX, mSqrSinX, 1)).multiply(sinX).add(period);
  }

  public static Complex ellipticK(double m) {
    return ellipticF(Math.PI / 2.0, m);
  }

  public static Complex ellipticK(Complex m) {
    // https://github.com/Hipparchus-Math/hipparchus/issues/148
    return ellipticF(new Complex(Math.PI / 2.0), m);
  }

  public static Complex ellipticE(Complex x, Complex m) {
    // https://github.com/Hipparchus-Math/hipparchus/issues/148
    return LegendreEllipticIntegral.bigE(x, m);
    // Complex period = Complex.ZERO;
    // if (Math.abs(x.getReal()) > Math.PI / 2.0) {
    // long p = Math.round(x.getReal() / Math.PI);
    // x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
    // period = ellipticE(new Complex(Math.PI / 2.0), m).multiply(p + p);
    // }
    //
    // Complex sinX = x.sin();
    // Complex cosX = x.cos();
    // Complex sqrSinX = sinX.multiply(sinX);
    // Complex sqrCosX = cosX.multiply(cosX);
    // Complex p3SinX = sqrSinX.multiply(sinX);
    // Complex diff = Complex.ONE.subtract(m.multiply(sqrSinX));
    // return period
    // .add(sinX.multiply(carlsonRF(sqrCosX, diff, Complex.ONE)))
    // .add(
    // m.multiply(p3SinX)
    // .multiply(carlsonRD(sqrCosX, diff, Complex.ONE))
    // .multiply(-1.0 / 3.0));
  }

  public static Complex ellipticE(double x, double m) {
    if (m > 1 && Math.abs(x) > Math.asin(1 / Math.sqrt(m))) {
      return ellipticE(new Complex(x), new Complex(m));
    }
    return Complex.valueOf(LegendreEllipticIntegral.bigE(x, m));
    // Complex period = Complex.ZERO;
    // if (Math.abs(x) > Math.PI / 2.0) {
    // long p = Math.round(x / Math.PI);
    // x = x - p * Math.PI;
    // period = ellipticE(Math.PI / 2.0, m).multiply(p + p);
    // }
    //
    // double sinX = Math.sin(x);
    // double cosX = Math.cos(x);
    // double sqrSinX = sinX * sinX;
    // double sqrCosX = cosX * cosX;
    // return period.add(Complex.valueOf(carlsonRF(sqrCosX, 1.0 - m * sqrSinX, 1.0)).multiply(sinX)
    // .subtract(Complex.valueOf(carlsonRD(sqrCosX, 1 - m * sqrSinX, 1.0))
    // .multiply(m / 3.0 * Math.pow(sinX, 3.0))));
  }

  public static Complex ellipticPi(Complex n, Complex x, Complex m) {
    // return LegendreEllipticIntegral.bigPi(n, x, m);
    Complex period = Complex.ZERO;
    if (Math.abs(x.getReal()) > Math.PI / 2.0) {
      long p = Math.round(x.getReal() / Math.PI);
      x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
      period = ellipticPi(n, new Complex(Math.PI / 2.0), m).multiply(p + p);
    }
    Complex sinX = x.sin();
    Complex cosX = x.cos();
    Complex sqrSinX = sinX.multiply(sinX);
    Complex sqrCosX = cosX.multiply(cosX);
    Complex p3SinX = sqrSinX.multiply(sinX);
    Complex a2;
    if (sinX.equals(Complex.ZERO)) {
      a2 = Complex.ONE;
    } else {
      a2 = Complex.ONE.subtract(m.multiply(sqrSinX));
    }
    return sinX.multiply(carlsonRF(sqrCosX, a2, Complex.ONE))
        .add(n.multiply(1.0 / 3.0).multiply(p3SinX)
            .multiply(carlsonRJ(sqrCosX, Complex.ONE.subtract(m.multiply(sqrSinX)), Complex.ONE,
                Complex.ONE.subtract(n.multiply(sqrSinX)))))
        .add(period);
  }

  public static Complex ellipticPi(double n, double x, double m) {
    if (n > 1 && Math.abs(x) > Math.asin(1 / Math.sqrt(n))) {
      return ellipticPi(new Complex(n), new Complex(x), new Complex(m));
    }

    if (m > 1 && Math.abs(x) > Math.asin(1 / Math.sqrt(m))) {
      return ellipticPi(new Complex(n), new Complex(x), new Complex(m));
    }
    // return Complex.valueOf(LegendreEllipticIntegral.bigPi(n, x, m));
    Complex period = Complex.ZERO;
    if (Math.abs(x) > Math.PI / 2.0) {
      long p = Math.round(x / Math.PI);
      x = x - p * Math.PI;
      period = ellipticPi(n, Math.PI / 2.0, m).multiply(p + p);
    }

    double sinX = Math.sin(x);
    double cosX = Math.cos(x);
    double sqrSinX = sinX * sinX;
    double sqrCosX = cosX * cosX;
    double p3SqrSinX = sqrSinX * sinX;
    double mSqrSinX = 1.0 - m * sqrSinX;
    double nSqrSinX = 1.0 - n * sqrSinX;
    if (mSqrSinX < 0) {
      return carlsonRF(new Complex(sqrCosX), new Complex(mSqrSinX), Complex.ONE).multiply(sinX).add(
          carlsonRJ(new Complex(sqrCosX), new Complex(mSqrSinX), Complex.ONE, new Complex(nSqrSinX))
              .multiply(n / 3.0 * p3SqrSinX))
          .add(period);
    }
    return Complex
        .valueOf(carlsonRF(sqrCosX, mSqrSinX, 1)).multiply(sinX).add(Complex
            .valueOf(carlsonRJ(sqrCosX, mSqrSinX, 1, nSqrSinX)).multiply(n / 3.0 * p3SqrSinX))
        .add(period);
  }

  public static Complex jacobiZeta(Complex x, Complex m) {
    // using definition matching elliptic integrals
    // alternate definition replaces x with am(x,m)
    return ellipticE(x, m).subtract(ellipticF(x, m)
        .multiply(ellipticE(new Complex(Math.PI / 2.0), m)).multiply(ellipticK(m).reciprocal()));
  }
}
