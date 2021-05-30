package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

import static org.matheclipse.core.builtin.functions.EllipticIntegralsJS.*;

/**
 * Ported from JavaScript file <a href=
 * "https://github.com/paulmasson/math/blob/master/src/functions/elliptic-functions.js">elliptic-functions.js</a>
 */
public class EllipticFunctionsJS {
  private EllipticFunctionsJS() {}

  public static double trunc(double value) {
    return value < 0 ? Math.ceil(value) : Math.floor(value);
  }

  public static Complex jacobiTheta(int n, double x, double q) {
    return jacobiTheta(n, x, q, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static Complex jacobiTheta(int n, double x, double q, double tolerance) {
    if (Math.abs(q) >= 1) {
      throw new ArgumentTypeException("unsupported elliptic nome");
    }

    if (n < 1 || n > 4) {
      throw new ArgumentTypeException("undefined Jacobi theta index");
    }

    if (F.isZero(q)) {
      switch (n) {
        case 1:
        case 2:
          return Complex.ZERO;
        case 3:
        case 4:
          return Complex.ONE;
      }
    }
    // dlmf.nist.gov/20.2 to reduce overflow
    if (Math.abs(x) > Math.PI) {

      double p = trunc(x / Math.PI);
      x = x - p * Math.PI;

      switch (n) {
        case 1:
        case 2:
          return new Complex(Math.pow(-1, p)).multiply(jacobiTheta(n, x, q));

        case 3:
        case 4:
          return jacobiTheta(n, x, q);
      }
    }

    long iterationLimit = EvalEngine.get().getIterationLimit();
    switch (n) {
      case 1:
        if (q < 0) {
          return jacobiTheta(n, new Complex(x), new Complex(q));
        }

        double s = 0;
        double p = 1;
        int i = 0;

        while (Math.abs(p) > tolerance) {
          p = Math.pow(-1, i) * Math.pow(q, (i * i + i)) * Math.sin((2 * i + 1) * x);
          s += p;
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return new Complex(2 * Math.pow(q, 0.25) * s);
      case 2:
        if (q < 0) {
          return jacobiTheta(n, new Complex(x), new Complex(q));
        }

        s = 0;
        p = 1;
        i = 0;

        while (Math.abs(p) > tolerance) {
          p = Math.pow(q, (i * i + i)) * Math.cos((2 * i + 1) * x);
          s += p;
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return new Complex(2 * Math.pow(q, 0.25) * s);
      case 3:
        s = 0;
        p = 1;
        i = 1;
        while (Math.abs(p) > tolerance) {
          p = Math.pow(q, (i * i)) * Math.cos(2 * i * x);
          s += p;
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return new Complex(1 + 2 * s);
      case 4:
        s = 0;
        p = 1;
        i = 1;

        while (Math.abs(p) > tolerance) {
          p = Math.pow(-q, (i * i)) * Math.cos(2 * i * x);
          s += p;
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }

        return new Complex(1 + 2 * s);
    }
    throw new ArgumentTypeException("undefined Jacobi theta index");
  }

  public static Complex jacobiTheta(int n, Complex x, Complex q) {
    return jacobiTheta(n, x, q, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static Complex jacobiTheta(int n, Complex x, Complex q, double tolerance) {

    if (q.norm() >= 1) {
      throw new ArgumentTypeException("unsupported elliptic nome");
    }

    if (n < 1 || n > 4) {
      throw new ArgumentTypeException("undefined Jacobi theta index");
    }

    if (F.isZero(q)) {
      switch (n) {
        case 1:
        case 2:
          return Complex.ZERO;
        case 3:
        case 4:
          return Complex.ONE;
      }
    }

    Complex piTau = q.log().divide(Complex.I);

    // dlmf.nist.gov/20.2 to reduce overflow
    if (Math.abs(x.getImaginary()) > Math.abs(piTau.getImaginary())
        || Math.abs(x.getReal()) > Math.PI) {

      double pt = trunc(x.getImaginary() / piTau.getImaginary());
      x = x.subtract(piTau.multiply(pt));

      double p = trunc(x.getReal() / Math.PI);
      x = x.subtract(p * Math.PI);

      Complex qFactor = q.pow(-pt * pt);
      Complex eFactor = x.multiply(Complex.I).multiply(-2 * pt).exp();

      // factors can become huge, so chop spurious parts first
      switch (n) {
        case 1:
          return qFactor
              .multiply(eFactor)
              .multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
              .multiply(Math.pow((-1), (p + pt)));

        case 2:
          return qFactor
              .multiply(eFactor)
              .multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
              .multiply(Math.pow((-1), p));

        case 3:
          return qFactor.multiply(eFactor).multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance));
        case 4:
          return qFactor
              .multiply(eFactor)
              .multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
              .multiply(Math.pow((-1), pt));
      }
    }
    Complex s = Complex.ZERO;
    Complex p = Complex.ONE;
    long iterationLimit = EvalEngine.get().getIterationLimit();
    int i = 0;
    switch (n) {
      case 1:
        while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
          p = q.pow(i * i + i).multiply(x.multiply(2 * i + 1).sin()).multiply(Math.pow(-1, i));
          s = s.add(p);
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }

        return q.pow(0.25).multiply(s).multiply(2);
      case 2:
        while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
          p = q.pow(i * i + i).multiply(x.multiply(2 * i + 1).cos());
          s = s.add(p);
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return q.pow(0.25).multiply(s).multiply(2);
      case 3:
        i = 1;
        while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
          p = q.pow(i * i).multiply(x.multiply(2 * i).cos());
          s = s.add(p);
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return s.multiply(2.0).add(1.0);
      case 4:
        i = 1;
        while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
          p = q.negate().pow(i * i).multiply(x.multiply(2 * i).cos());
          s = s.add(p);
          if (i++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(i, S.EllipticTheta);
          }
        }
        return s.multiply(2.0).add(1.0);
    }

    throw new ArgumentTypeException("undefined Jacobi theta index");
  }

  public static Complex ellipticNome(Complex m) {
    return EllipticIntegralsJS.ellipticK(m.negate().add(1.0))
        .multiply(-Math.PI)
        .divide(EllipticIntegralsJS.ellipticK(m))
        .exp();
  }

  public static Complex ellipticNome(double m) {
    if (m > 1) {
      return ellipticNome(new Complex(m));
    }
    if (m < 0) {
      return EllipticIntegralsJS.ellipticK(1.0 / (1.0 - m))
          .divide(EllipticIntegralsJS.ellipticK(m / (m - 1.0)))
          .multiply(-Math.PI)
          .exp()
          .negate();
    }
    return EllipticIntegralsJS.ellipticK(1 - m)
        .divide(EllipticIntegralsJS.ellipticK(m))
        .multiply(-Math.PI)
        .exp();
  }

  /**
   * JacobiSN function for complex values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiSN(Complex x, Complex m) {

    Complex q = ellipticNome(m);

    // if ( m > 1 || isComplex(x) || isComplex(m) ) {

    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = x.divide(a2);

    return jacobiTheta(3, Complex.ZERO, q)
        .divide(jacobiTheta(2, Complex.ZERO, q))
        .multiply(jacobiTheta(1, t, q).divide(jacobiTheta(4, t, q)));

    // }
  }

  /**
   * JacobiSN function for real values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiSN(double x, double m) {
    if (m > 1) {
      return jacobiSN(new Complex(x), new Complex(m));
    }

    // dlmf.nist.gov/22.5#ii
    if (m == 0) {
      return new Complex(Math.sin(x));
    }
    if (m == 1) {
      return new Complex(Math.tanh(x));
    }

    Complex q = ellipticNome(new Complex(m));
    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = new Complex(x).divide(a2);

    if (m < 0) {
      return jacobiTheta(3, Complex.ZERO, q)
          .divide(jacobiTheta(4, t, q))
          .multiply(jacobiTheta(1, t, q).divide(jacobiTheta(2, Complex.ZERO, q)).getReal());
    }
    return jacobiTheta(3, Complex.ZERO, q)
        .divide(jacobiTheta(2, Complex.ZERO, q))
        .multiply(jacobiTheta(1, t, q).divide(jacobiTheta(4, t, q)));
  }

  /**
   * JacobiCN function for real values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiCN(Complex x, Complex m) {
    Complex q = ellipticNome(m);
    // if ( m > 1 || isComplex(x) || isComplex(m) ) {
    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = x.divide(a2);

    return jacobiTheta(4, Complex.ZERO, q)
        .divide(jacobiTheta(2, Complex.ZERO, q))
        .multiply(jacobiTheta(2, t, q).divide(jacobiTheta(4, t, q)));
    // }
  }

  /**
   * JacobiCN function for real values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiCN(double x, double m) {
    if (m > 1) {
      return jacobiCN(new Complex(x), new Complex(m));
    }

    // dlmf.nist.gov/22.5#ii
    if (m == 0) {
      return new Complex(Math.cos(x));
    }
    if (m == 1) {
      // sech(x)
      return new Complex(1.0D / Math.cosh(x));
    }

    Complex q = ellipticNome(new Complex(m));
    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = new Complex(x).divide(a2);

    if (m < 0) {
      return jacobiTheta(4, Complex.ZERO, q)
          .divide(jacobiTheta(4, t, q))
          .multiply(jacobiTheta(2, t, q).divide(jacobiTheta(2, Complex.ZERO, q)).getReal());
    }
    return jacobiTheta(4, Complex.ZERO, q)
        .divide(jacobiTheta(2, Complex.ZERO, q))
        .multiply(jacobiTheta(2, t, q).divide(jacobiTheta(4, t, q)));
  }

  /**
   * JacobiDN function for complex values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiDN(Complex x, Complex m) {

    Complex q = ellipticNome(m);

    // if ( m > 1 || isComplex(x) || isComplex(m) ) {

    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = x.divide(a2);

    return jacobiTheta(4, Complex.ZERO, q)
        .divide(jacobiTheta(3, Complex.ZERO, q))
        .multiply(jacobiTheta(3, t, q).divide(jacobiTheta(4, t, q)));

    // }
  }

  /**
   * JacobiDN function for real values.
   *
   * @param x
   * @param m
   * @return
   */
  public static Complex jacobiDN(double x, double m) {
    if (m > 1) {
      return jacobiDN(new Complex(x), new Complex(m));
    }

    // dlmf.nist.gov/22.5#ii
    if (m == 0) {
      return Complex.ONE;
    }
    if (m == 1) {
      // sech(x)
      return new Complex(1.0D / Math.cosh(x));
    }

    Complex q = ellipticNome(new Complex(m));
    Complex a2 = jacobiTheta(3, Complex.ZERO, q);
    a2 = a2.multiply(a2);
    Complex t = new Complex(x).divide(a2);

    return jacobiTheta(4, Complex.ZERO, q)
        .divide(jacobiTheta(3, Complex.ZERO, q))
        .multiply(jacobiTheta(3, t, q).divide(jacobiTheta(4, t, q)));
  }

  public static Complex jacobiAmplitude(Complex x, Complex m) {

    // if ( m > 1 || isComplex(x) || isComplex(m) ) {

    if (m.getImaginary() == 0.0 && m.getReal() <= 1) {

      Complex K = EllipticIntegralsJS.ellipticK(m.getReal());
      long n = Math.round(x.getReal() / 2.0 / K.getReal()); // ??? getReal() inserted
      x = x.subtract(2.0 * n * K.getReal());

      if (m.getReal() < 0.0) {

        Complex Kp = EllipticIntegralsJS.ellipticK(1 - m.getReal());
        long p = Math.round(x.getImaginary() / 2.0 / Kp.getReal());

        // bitwise test for odd integer
        if ((p & 1) == 1) {
          return jacobiSN(x, m).asin().negate().add(n * Math.PI);
        }
      }

      return jacobiSN(x, m).asin().add(n * Math.PI);
    }

    return jacobiSN(x, m).asin();

    // }
  }

  public static Complex jacobiAmplitude(double x, double m) {
    if (m > 1) {
      return jacobiAmplitude(new Complex(x), new Complex(m));
    }

    Complex K = EllipticIntegralsJS.ellipticK(m);
    long n = Math.round(x / 2.0 / K.getReal()); // ??? .getReal() inserted
    x = x - 2 * n * K.getReal(); // ??? .getReal() inserted

    return jacobiSN(x, m).asin().add(n * Math.PI);
  }

  private static Complex cubicTrigSolution(Complex p, Complex q, int n) {
    // p, q both negative in defining cubic
    return p.sqrt()
        .multiply(
            q.multiply(p.pow(-1.5))
                .multiply(3.0 * Math.sqrt(3.0) / 2.0)
                .acos()
                .divide(3.0)
                .subtract(2.0 * Math.PI * n / 3.0)
                .cos())
        .multiply(2.0 / Math.sqrt(3));
  }

  public static Complex[] weierstrassRoots(Complex g2, Complex g3) {
    g2 = g2.divide(4);
    g3 = g3.divide(4);

    Complex e1 = cubicTrigSolution(g2, g3, 0);
    Complex e2 = cubicTrigSolution(g2, g3, 1);
    Complex e3 = cubicTrigSolution(g2, g3, 2);

    return new Complex[] {e1, e2, e3};
  }

  public static Complex[] weierstrassHalfPeriods(Complex g2, Complex g3) {

    Complex[] sol = weierstrassRoots(g2, g3);
    Complex e1 = sol[0];
    Complex e2 = sol[1];
    Complex e3 = sol[2];
    Complex lambda = e1.subtract(e3).sqrt();
    Complex m = e2.subtract(e3).divide(e1.subtract(e3));

    Complex w1 = ellipticK(m).divide(lambda);
    Complex w3 = Complex.I.multiply(ellipticK(Complex.ONE.subtract(m))).divide(lambda);

    return new Complex[] {w1, w3};

    // Complex e1 = sol[0];
    // Complex e2 = sol[1];
    // Complex e3 = sol[2];
    // Complex w1 = inverseWeierstrassP(e1, g2, g3);
    // Complex w2 = inverseWeierstrassP(e2, g2, g3);
    // Complex w3 = inverseWeierstrassP(e3, g2, g3);
    //
    // Complex[] w = new Complex[] { w1, w2, w3 };
    // Arrays.sort(w, (a, b) -> (int) Math.signum((a.abs() - b.abs())));
    // // w.sort( (a,b) => abs(a) - abs(b) );
    // Complex smallest = w[0];
    // Complex[] v = new Complex[] { w[1], w[2], //
    // w[1].subtract(smallest), w[2].subtract(smallest) };
    // // w.sort( (a,b) => abs(a) - abs(b) );
    // Arrays.sort(v, (a, b) -> (int) Math.signum((a.abs() - b.abs())));
    // return new Complex[] { smallest, v[0] };

  }

  public static Complex[] weierstrassInvariants(Complex w1, Complex w3) {

    // if ( !isComplex(w1) ) w1 = complex(w1);
    // if ( !isComplex(w3) ) w3 = complex(w3);

    // order half periods by complex slope
    if (w3.getImaginary() / w3.getReal() < w1.getImaginary() / w1.getReal()) {
      Complex temp = w1;
      w1 = w3;
      w3 = temp;
    }

    Complex ratio = w3.divide(w1);
    boolean conjugate = false;

    if (ratio.getImaginary() < 0) {
      ratio = ratio.conjugate();
      conjugate = true;
    }

    Complex q = Complex.I.multiply(Math.PI).multiply(ratio).exp();

    // en.wikipedia.org/wiki/Weierstrass's_elliptic_functions
    // modified for input of half periods

    Complex a = jacobiTheta(2, Complex.ZERO, q);
    Complex b = jacobiTheta(3, Complex.ZERO, q);

    Complex aPow4 = a.multiply(a);
    aPow4 = aPow4.multiply(aPow4);
    Complex aPow8 = aPow4.multiply(aPow4);

    Complex bPow4 = b.multiply(b);
    bPow4 = bPow4.multiply(bPow4);
    Complex bPow8 = bPow4.multiply(bPow4);

    Complex g2 =
        w1.multiply(2)
            .pow(-4)
            .multiply(aPow8.add(aPow4.multiply(bPow4).negate()).add(bPow8))
            .multiply(4.0 / 3.0 * Math.pow(Math.PI, 4));

    Complex g3 =
        w1.multiply(2.0)
            .pow(-6.0)
            .multiply(8.0 / 27.0 * Math.pow(Math.PI, 6.0))
            .multiply(
                a.pow(12)
                    .add(aPow8.multiply(bPow4).multiply(-1.5))
                    .add(aPow4.multiply(bPow8).multiply(-1.5))
                    .add(b.pow(12)));

    if (conjugate) {
      g2 = g2.conjugate();
      g3 = g3.conjugate();
    }

    return new Complex[] {g2, g3};
  }

  public static Complex weierstrassP(Complex x, Complex g2, Complex g3) {

    // if ( !isComplex(x) ) x = complex(x);

    Complex[] sol = weierstrassRoots(g2, g3);
    Complex e1 = sol[0];
    Complex e2 = sol[1];
    Complex e3 = sol[2];
    // Whittaker & Watson, Section 22.351

    Complex m = e2.subtract(e3).divide(e1.subtract(e3));

    Complex pow = jacobiSN(x.multiply(e1.subtract(e3).sqrt()), m).reciprocal();
    pow = pow.multiply(pow);
    return e3.add(e1.subtract(e3).multiply(pow));
  }

  public static Complex weierstrassPPrime(Complex x, Complex g2, Complex g3) {

    // if ( !isComplex(x) ) x = complex(x);

    Complex[] sol = weierstrassRoots(g2, g3);
    Complex e1 = sol[0];
    Complex e2 = sol[1];
    Complex e3 = sol[2];

    // Whittaker & Watson, Section 22.351

    Complex m = e2.subtract(e3).divide(e1.subtract(e3));

    Complex argument = x.multiply(e1.subtract(e3).sqrt());

    return e1.subtract(e3)
        .pow(1.5)
        .multiply(jacobiCN(argument, m))
        .multiply(jacobiDN(argument, m))
        .multiply(jacobiSN(argument, m).pow(-3))
        .multiply(-2);
  }

  public static Complex inverseWeierstrassP(Complex x, Complex g2, Complex g3) {

    Complex[] sol = weierstrassRoots(g2, g3);
    Complex e1 = sol[0];
    Complex e2 = sol[1];
    Complex e3 = sol[2];

    // Johansson arxiv.org/pdf/1806.06725.pdf p.17
    // sign of imaginary part on real axis differs from MMA

    return EllipticIntegralsJS.carlsonRF(x.subtract(e1), x.subtract(e2), x.subtract(e3));
  }
}
