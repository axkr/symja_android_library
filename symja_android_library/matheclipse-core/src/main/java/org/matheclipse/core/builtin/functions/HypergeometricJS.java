package org.matheclipse.core.builtin.functions;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ResultException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/**
 * Ported from JavaScript file <a
 * href="https://github.com/paulmasson/math/blob/master/src/functions/hypergeometric.js">hypergeometric.js</a>
 */
public class HypergeometricJS {

  private HypergeometricJS() {}

  public static Complex complexAverage(Function<Complex, Complex> f, Complex x) {
    return complexAverage(f, x, 1e-5);
  }

  public static Complex complexAverage(Function<Complex, Complex> f, Complex x, double offset) {
    return f.apply(x.add(offset)).add(f.apply(x.subtract(offset))).divide(2.0);
  }

  public static Complex hypergeometricSeries(
      Complex[] A, Complex[] B, Complex x) { // , double tolerance
    // see https://github.com/paulmasson/math/issues/12
    Complex s = Complex.ONE;
    Complex p = Complex.ONE;
    int i = 0;
    while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
        || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      for (int j = 0; j < A.length; j++) {
        p = p.multiply(A[j]);
        A[j] = A[j].add(1.0);
      }

      for (int j = 0; j < B.length; j++) {
        p = p.divide(B[j]);
        B[j] = B[j].add(1.0);
      }

      p = p.multiply(x).divide(++i);
      s = s.add(p);

      if (i > 500) {
        throw new ArgumentTypeException(
            "maximum iteration exceeded in hypergeometricSeries (Complex)");
      }
    }
    return s;
  }

  // public static double hypergeometricSeries(double[] A, double[] B, double x) {
  // return hypergeometricSeries(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  // }

  public static double hypergeometricSeries(
      double[] A, double[] B, double x) { // boolean complexArguments,
    // double tolerance) {
    // see https://github.com/paulmasson/math/issues/12
    double sOld1 = 0.0, sOld2;
    double s = 1;
    double p = 1;
    int i = 0;

    do {
      sOld2 = sOld1;
      sOld1 = s;
      for (int j = 0; j < A.length; j++) {
        p *= A[j];
        A[j]++;
      }

      for (int j = 0; j < B.length; j++) {
        p /= B[j];
        B[j]++;
      }

      p *= x / (++i);
      s += p;

      if (i > 500) {
        throw new ArgumentTypeException(
            "maximum iteration exceeded in hypergeometricSeries (double)");
      }
    } while (!hasReachedAccuracy(s, sOld1, 1.E-12)
        || //
        !hasReachedAccuracy(sOld1, sOld2, 1.E-12));

    return s;
  }

  /**
   * Indicate if xn and xo have the relative/absolute accuracy epsilon. In case that the true value
   * is less than one this is based on the absolute difference, otherwise on the relative
   * difference:
   *
   * <pre>
   *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
   * </pre>
   *
   * @param xn the actual argument x[n]
   * @param xo the older argument x[n-1]
   * @param eps accuracy to reach
   * @return flag indicating if accuracy is reached.
   */
  public static boolean hasReachedAccuracy(final double xn, final double xo, final double eps) {
    final double z = abs(xn + xo) / 2;
    double error = abs(xn - xo);
    if (z > 1) {
      error /= z;
    }
    return error <= eps;
  }

  public static double hypergeometric0F1(double a, double x) {
    if (F.isNumIntValue(a) && a <= 0) {
      throw new ArgumentTypeException("Hypergeometric function pole");
    }
    final double useAsymptotic = 100.0;
    // asymptotic form is complex
    if (Math.abs(x) > useAsymptotic) {
      return hypergeometric0F1(new Complex(a), new Complex(x)).getReal();
    }

    double s = 1.0;
    double p = 1.0;
    long i = 1;
    long iterationLimit = EvalEngine.get().getIterationLimit();
    while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p *= x / a / i;
      s += p;
      a++;
      if (i++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(i, S.Hypergeometric0F1);
      }
    }

    return s;
  }

  // public static Complex hypergeometric0F1(Complex a, Complex x) {
  // return hypergeometric0F1(a, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  // }

  public static Complex hypergeometric0F1(Complex a, Complex x) {

    final double useAsymptotic = 100;
    if (a.isMathematicalInteger() && a.getReal() <= 0) {
      throw new ArgumentTypeException("hypergeometric function pole");
    }

    // asymptotic form as per Johansson
    if (x.norm() > useAsymptotic) {
      // transform variables for convenience
      Complex b = a.multiply(2).subtract(1);
      a = a.subtract(0.5);
      x = x.sqrt().multiply(4.0);

      // copied from hypergeometric1F1
      Complex t1 =
          Arithmetic.lanczosApproxGamma(b)
              .multiply(x.negate().pow(a.negate()))
              .multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)).reciprocal());
      t1 = t1.multiply(hypergeometric2F0(a, a.add(b.negate()).add(1), new Complex(-1.0).divide(x)));

      Complex t2 =
          Arithmetic.lanczosApproxGamma(b)
              .multiply(x.pow(a.subtract(b)))
              .multiply(x.exp())
              .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal());
      t2 =
          t2.multiply(
              hypergeometric2F0(b.subtract(a), Complex.ONE.subtract(a), Complex.ONE.divide(x)));

      return x.divide(-2.0).exp().multiply(t1.add(t2));
    }

    Complex s = Complex.ONE;
    Complex p = Complex.ONE;
    long i = 1;
    long iterationLimit = EvalEngine.get().getIterationLimit();
    while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
        || //
        Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p = p.multiply(x).multiply(a.reciprocal()).divide(i);
      s = s.add(p);
      a = a.add(1);
      if (i++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(i, S.Hypergeometric0F1);
      }
    }

    return s;
  }

  // public static Complex hypergeometric1F1(Complex a, Complex b, Complex x) {
  // return hypergeometric1F1(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  // }

  public static Complex hypergeometric1F1(Complex a, Complex b, Complex x) {

    final double useAsymptotic = 30;
    if (b.isMathematicalInteger() && b.getReal() <= 0) {
      throw new ArgumentTypeException("hypergeometric function pole");
    }
    // Kummer transformation
    if (x.getReal() < 0) {
      return x.exp().multiply(hypergeometric1F1(b.subtract(a), b, x.negate()));
    }

    // asymptotic form as per Johansson arxiv.org/abs/1606.06977
    if (x.norm() > useAsymptotic) {
      Complex t1 =
          Arithmetic.lanczosApproxGamma(b)
              .multiply(x.negate().pow(a.negate()))
              .multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)).reciprocal());
      t1 =
          t1.multiply(
              hypergeometric2F0(a, a.add(b.negate()).add(1.0), new Complex(-1.0).divide(x)));

      Complex t2 =
          Arithmetic.lanczosApproxGamma(b)
              .multiply(x.pow(a.subtract(b)))
              .multiply(x.exp())
              .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal());
      t2 =
          t2.multiply(
              hypergeometric2F0(b.subtract(a), Complex.ONE.subtract(a), Complex.ONE.divide(x)));

      return t1.add(t2);
    }

    Complex s = Complex.ONE;
    Complex p = Complex.ONE;
    long i = 1;

    long iterationLimit = EvalEngine.get().getIterationLimit();
    while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
        || //
        Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p = p.multiply(x).multiply(a).multiply(b.reciprocal()).divide(i);
      s = s.add(p);
      a = a.add(1.0);
      b = b.add(1.0);
      if (i++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(i, S.Hypergeometric1F1);
      }
    }

    return s;
  }

  // public static double hypergeometric1F1(double a, double b, double x) {
  // return hypergeometric1F1(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  // }

  public static double hypergeometric1F1(double a, double b, double x) {
    final double useAsymptotic = 30;
    if (F.isNumIntValue(b) && b <= 0) {
      throw new ArgumentTypeException("hypergeometric function pole");
    }

    // Kummer transformation
    if (x < 0) {
      return Math.exp(x) * hypergeometric1F1(b - a, b, -x);
    }

    // asymptotic form is complex
    if (Math.abs(x) > useAsymptotic) {
      return hypergeometric1F1(new Complex(a), new Complex(b), new Complex(x)).getReal();
    }

    double s = 1;
    double p = 1;
    long i = 1;

    long iterationLimit = EvalEngine.get().getIterationLimit();
    while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p *= x * a / b / i;
      s += p;
      a++;
      b++;
      if (i++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(i, S.Hypergeometric1F1);
      }
    }

    return s;
  }

  public static Complex hypergeometric2F0(Complex a, Complex b, Complex x) {
    return hypergeometric2F0(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static Complex hypergeometric2F0(Complex a, Complex b, Complex x, double tolerance) {
    int terms = 50;

    Complex s = Complex.ONE;
    Complex p = Complex.ONE;
    Complex pLast = p;
    boolean converging = false; // first few terms can be larger than unity
    int i = 1;

    while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {

      p = p.multiply(x).multiply(a).multiply(b).divide(i);

      if (p.norm() > pLast.norm() && converging) {
        break; // prevent runaway sum
      }
      if (p.norm() < pLast.norm()) {
        converging = true;
      }
      if (i > terms) {
        throw new ArgumentTypeException("not converging after " + terms + " terms");
      }

      s = s.add(p);
      a = a.add(1.0);
      b = b.add(1.0);
      i++;
      pLast = p;
    }

    return s;
  }

  public static double hypergeometric2F0(double a, double b, double x) {
    return hypergeometric2F0(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static double hypergeometric2F0(double a, double b, double x, double tolerance) {
    int terms = 50;
    double s = 1;
    double p = 1, pLast = p;
    boolean converging = false;
    double i = 1;

    while (Math.abs(p) > tolerance) {

      p *= (x * a * b) / i;

      if (Math.abs(p) > Math.abs(pLast) && converging) {
        break; // prevent runaway sum
      }
      if (Math.abs(p) < Math.abs(pLast)) {
        converging = true;
      }
      if (i > terms) {
        throw new ArgumentTypeException("not converging after " + terms + " terms");
      }
      s += p;
      a++;
      b++;
      i++;
      pLast = p;
    }

    return s;
  }

  public static Complex hypergeometric2F1(
      final Complex a, final Complex b, final Complex c, final Complex x) {
    return hypergeometric2F1(a, b, c, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static Complex hypergeometric2F1(
      Complex a, Complex b, Complex c, Complex x, double tolerance) {

    if (F.isFuzzyEquals(a, c, tolerance)) {
      return Complex.ONE.subtract(x).pow(b.negate());
    }
    if (F.isFuzzyEquals(b, c, tolerance)) {
      return Complex.ONE.subtract(x).pow(a.negate());
    }

    EvalEngine engine = EvalEngine.get();
    final int recursionLimit = engine.getRecursionLimit();
    try {
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(
              counter, //
              F.Hypergeometric2F1(
                  F.complexNum(a), F.complexNum(b), F.complexNum(c), F.complexNum(x)));
        }
      }
      // choose smallest absolute value of transformed argument
      // transformations from Abramowitz & Stegun p.559
      // fewer operations compared to dlmf.nist.gov/15.8

      double[] absArray =
          new double[] {
            x.norm(), //
            x.divide(x.subtract(1)).norm(), //
            new Complex(1).subtract(x).norm(), //
            x.reciprocal().norm(), //
            new Complex(1).subtract(x).reciprocal().norm(), //
            new Complex(1).subtract(x.reciprocal()).norm()
          };

      double min = Double.POSITIVE_INFINITY;
      double newMin = Double.POSITIVE_INFINITY;
      int index = -1;
      for (int i = 0; i < absArray.length; i++) {
        newMin = Math.min(min, absArray[i]);
        if (newMin != min) {
          min = newMin;
          index = i;
        }
      }

      final Complex subtractCA = c.subtract(a);
      final Complex subtractCB = c.subtract(b);
      final Complex af = a;
      final Complex bf = b;
      final Complex cf = c;
      final Complex xf = x;
      switch (index) {
        case 0:
          break;

        case 1:
          return new Complex(1.0)
              .subtract(x)
              .pow(a.negate())
              .multiply(hypergeometric2F1(a, c.subtract(b), c, x.divide(x.subtract(1))));

        case 2:
          {
            if (c.subtract(a.add(b)).isMathematicalInteger()
                || (subtractCA.isMathematicalInteger() && subtractCA.getReal() <= 0)) {
              return complexAverage(v -> hypergeometric2F1(v, bf, cf, xf), af);
            }
            if (subtractCB.isMathematicalInteger() && subtractCB.getReal() <= 0) {
              return complexAverage(v -> hypergeometric2F1(af, v, cf, xf), bf);
            }

            Complex t1 =
                Arithmetic.lanczosApproxGamma(c)
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(a.add(b))))
                    .multiply(Arithmetic.lanczosApproxGamma(subtractCA).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
                    .multiply(
                        hypergeometric2F1(
                            a, b, a.add(b).add(c.negate()).add(1), new Complex(1).subtract(x)));

            Complex t2 =
                new Complex(1)
                    .subtract(x)
                    .pow(c.subtract(a.add(b)))
                    .multiply(Arithmetic.lanczosApproxGamma(c))
                    .multiply(Arithmetic.lanczosApproxGamma(a.add(b).subtract(c)))
                    .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
                    .multiply(
                        hypergeometric2F1(
                            subtractCA,
                            c.subtract(b),
                            a.add(a.negate()).add(b.negate()).add(1),
                            new Complex(1).subtract(x)));

            return t1.add(t2);
          }

        case 3:
          {
            if (a.subtract(b).isMathematicalInteger()
                || (subtractCA.isMathematicalInteger() && subtractCA.getReal() <= 0)) {
              return complexAverage(v -> hypergeometric2F1(v, bf, cf, xf), af);
            }
            if (subtractCB.isMathematicalInteger() && subtractCB.getReal() <= 0) {
              return complexAverage(v -> hypergeometric2F1(af, v, cf, xf), bf);
            }
            Complex t1 =
                Arithmetic.lanczosApproxGamma(c)
                    .multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)))
                    .multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(subtractCA).reciprocal())
                    .multiply(x.negate().pow(a.negate()))
                    .multiply(
                        hypergeometric2F1(
                            a, a.add(1).add(c.negate()), a.add(1).add(b.negate()), x.reciprocal()));

            Complex t2 =
                Arithmetic.lanczosApproxGamma(c)
                    .multiply(Arithmetic.lanczosApproxGamma(a.subtract(b)))
                    .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
                    .multiply(x.negate().pow(b.negate()))
                    .multiply(
                        hypergeometric2F1(
                            b, b.add(1).add(c.negate()), b.add(1).add(a.negate()), x.reciprocal()));

            return t1.add(t2);
          }
        case 4:
          {
            if (a.subtract(b).isMathematicalInteger()
                || (subtractCA.isMathematicalInteger() && subtractCA.getReal() <= 0)) {
              return complexAverage(v -> hypergeometric2F1(v, bf, cf, xf), af);
            }
            if (subtractCB.isMathematicalInteger() && subtractCB.getReal() <= 0) {
              return complexAverage(v -> hypergeometric2F1(af, v, cf, xf), bf);
            }
            Complex t1 =
                new Complex(1.0)
                    .subtract(x)
                    .pow(a.negate())
                    .multiply(Arithmetic.lanczosApproxGamma(c))
                    .multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)))
                    .multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(subtractCA).reciprocal())
                    .multiply(
                        hypergeometric2F1(
                            a,
                            c.subtract(b),
                            a.add(b.negate()).add(1),
                            new Complex(1).subtract(x).reciprocal()));

            Complex t2 =
                new Complex(1)
                    .subtract(x)
                    .pow(b.negate())
                    .multiply(Arithmetic.lanczosApproxGamma(c))
                    .multiply(Arithmetic.lanczosApproxGamma(a.subtract(b)))
                    .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
                    .multiply(
                        hypergeometric2F1(
                            b,
                            subtractCA,
                            b.add(a.negate()).add(1),
                            new Complex(1).subtract(x).reciprocal()));

            return t1.add(t2);
          }
        case 5:
          {
            if (c.subtract(a.add(b)).isMathematicalInteger()
                || (subtractCA.isMathematicalInteger() && subtractCA.getReal() <= 0)) {
              return complexAverage(v -> hypergeometric2F1(v, bf, cf, xf), af);
            }
            if (subtractCB.isMathematicalInteger() && subtractCB.getReal() <= 0) {
              return complexAverage(v -> hypergeometric2F1(af, v, cf, xf), bf);
            }
            Complex t1 =
                Arithmetic.lanczosApproxGamma(c)
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(a.add(b))))
                    .multiply(Arithmetic.lanczosApproxGamma(subtractCA).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
                    .multiply(x.pow(a.negate()))
                    .multiply(
                        hypergeometric2F1(
                            a,
                            a.add(c.negate()).add(1),
                            a.add(b).add(c.negate()).add(1),
                            new Complex(1).subtract(x.reciprocal())));

            Complex t2 =
                Arithmetic.lanczosApproxGamma(c)
                    .multiply(Arithmetic.lanczosApproxGamma(a.add(b).subtract(c)))
                    .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
                    .multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
                    .multiply(new Complex(1).subtract(x).pow(c.subtract(a.add(b))))
                    .multiply(x.pow(a.subtract(c)))
                    .multiply(
                        hypergeometric2F1(
                            subtractCA,
                            new Complex(1).subtract(a),
                            c.add(a.negate()).add(b.negate()).add(1),
                            new Complex(1).subtract(x.reciprocal())));

            return t1.add(t2);
          }
      }

      if (c.isMathematicalInteger() && c.getReal() <= 0) {
        throw new ResultException(F.CComplexInfinity);
        // throw new ArgumentTypeException("hypergeometric function pole");
      }

      Complex s = Complex.ONE;
      Complex p = Complex.ONE;
      int i = 1;

      long iterationLimit = engine.getIterationLimit();
      while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
        p = p.multiply(x).multiply(a).multiply(b).multiply(c.reciprocal()).divide(i);
        s = s.add(p);
        a = a.add(1);
        b = b.add(1);
        c = c.add(1);
        if (i++ > iterationLimit && iterationLimit > 0) {
          IterationLimitExceeded.throwIt(i, S.Hypergeometric2F1);
        }
      }

      return s;
    } finally {
      if (recursionLimit > 0) {
        engine.decRecursionCounter();
      }
    }
  }

  public static double hypergeometric2F1(double a, double b, double c, double x) {

    return hypergeometric2F1(a, b, c, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static double hypergeometric2F1(double a, double b, double c, double x, double tolerance) {

    if (F.isFuzzyEquals(a, c, tolerance)) {
      return Math.pow(1 - x, -b);
    }
    if (F.isFuzzyEquals(b, c, tolerance)) {
      return Math.pow(1 - x, -a);
    }

    if (F.isNumIntValue(c) && c <= 0) {
      throw new ResultException(F.CComplexInfinity);
      // throw new ArgumentTypeException("hypergeometric function pole");
    }

    // transformation from Abramowitz & Stegun p.559
    if (x < -1) {
      double t1 =
          Gamma.gamma(c)
              * Gamma.gamma(b - a)
              / Gamma.gamma(b)
              / Gamma.gamma(c - a)
              * Math.pow(-x, -a)
              * hypergeometric2F1(a, 1 - c + a, 1 - b + a, 1 / x);
      double t2 =
          Gamma.gamma(c)
              * Gamma.gamma(a - b)
              / Gamma.gamma(a)
              / Gamma.gamma(c - b)
              * Math.pow(-x, -b)
              * hypergeometric2F1(b, 1 - c + b, 1 - a + b, 1 / x);
      return t1 + t2;
    }

    if (F.isNumIntValue(x, -1)) {
      return hypergeometric2F1(new Complex(a), new Complex(b), new Complex(c), new Complex(x))
          .getReal();
      // throw new ArgumentTypeException("unsupported real hypergeometric argument");
    }

    if (F.isNumIntValue(x, 1)) {
      if (c - a - b > 0) {
        return Gamma.gamma(c) * Gamma.gamma(c - a - b) / Gamma.gamma(c - a) / Gamma.gamma(c - b);
      } else {
        throw new ResultException(F.CComplexInfinity);
        // throw new ArithmeticException("Divergent Gauss hypergeometric function");
      }
    }

    if (x > 1) {
      throw new ArgumentTypeException("unsupported real hypergeometric argument");
      // return hypergeometric2F1( new Complex(a), new Complex(b), new Complex(c), new Complex(x) );
    }

    double s = 1;
    double p = 1;
    int i = 1;
    long iterationLimit = EvalEngine.get().getIterationLimit();
    while (Math.abs(p) > tolerance) {
      p *= x * a * b / c / i;
      s += p;
      a++;
      b++;
      c++;
      if (i++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(i, S.Hypergeometric2F1);
      }
    }

    return s;
  }

  public static double hypergeometric1F2(double a, double b, double c, double x, double tolerance) {
    int useAsymptotic = 200;
    // asymptotic form is complex
    if (Math.abs(x) > useAsymptotic) {
      return hypergeometric1F2(new Complex(a), new Complex(b), new Complex(c), new Complex(x))
          .getReal();
    }
    return hypergeometricSeries(new double[] {a}, new double[] {b, c}, x);
  }

  public static Complex hypergeometricPFQ(Complex[] A, Complex[] B, Complex x) {
    return hypergeometricPFQ(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  }

  public static Complex hypergeometricPFQ(Complex[] A, Complex[] B, Complex x, double tolerance) {
    // dlmf.nist.gov/16.11 for general transformations
    if (x.norm() > 1.0) {

      throw new ArgumentTypeException("general hypergeometric argument currently restricted");
    }
    return hypergeometricSeries(A, B, x);
  }

  public static Complex hypergeometric1F2(Complex a, Complex b, Complex c, Complex x) {

    final int useAsymptotic = 200;

    if (x.norm() > useAsymptotic) {

      Complex p = a.add(b.negate()).add(c.negate()).add(0.5).divide(2.0);

      ArrayList<Complex> ck = new ArrayList<Complex>();
      ck.add(Complex.ONE); //
      ck.add(
          ((a.multiply(3.0).add(b).add(c).add(-2.0)).multiply(a.subtract(b.add(c))).multiply(0.5))
              .add(b.multiply(c).multiply(2))
              .add(-3.0 / 8.0)); //
      ck.add(
          (a.multiply(3.0).add(b).add(c).add(-2.0))
              .multiply(a.subtract(b.add(c)))
              .multiply(0.25)
              .add(b.multiply(c).add(-3.0 / 16.0))
              .pow(2)
              .multiply(2)); //
      ck.add(new Complex(-1.0).multiply(a.multiply(2.0).subtract(3.0)).multiply(b).multiply(c)); //
      ck.add(
          a.pow(2.0)
              .multiply(-8.0)
              .add(a.multiply(11.0))
              .add(b)
              .add(c)
              .add(-2.0)
              .multiply(a.subtract(b.add(c)))
              .multiply(0.25)); //
      ck.add(new Complex(-3.0 / 16.0));

      IntFunction<Complex> w =
          k -> ck.get(k).multiply(x.negate().pow(-k / 2.0)).divide(Math.pow(2.0, k));

      Complex u1 =
          Complex.I.multiply(p.multiply(Math.PI).add(x.negate().sqrt().multiply(2.0))).exp();
      Complex u2 =
          new Complex(0.0, -1.0)
              .multiply(p.multiply(Math.PI).add(x.negate().sqrt().multiply(2.0)))
              .exp();

      Complex wLast = w.apply(2);
      Complex w2Negate = wLast.negate();
      Complex s =
          u1.multiply(new Complex(0.0, -1.0).multiply(w.apply(1)).add(w2Negate).add(1.0))
              .add( //
                  u2.multiply(Complex.I.multiply(w.apply(1)).add(w2Negate).add(1.0)));
      int k = 3;

      while (wLast.norm() > w.apply(k).norm()) {

        ck.add( //
            a.multiply(-6.0)
                .add(b.multiply(2))
                .add(c.multiply(2.0))
                .add(-4.0)
                .multiply(k)
                .add(a.pow(a).multiply(3.0))
                .add(b.subtract(c).pow(2.0).negate())
                .add(a.multiply(b.add(c).add(-2)).multiply(2.0).negate())
                .add(0.25)
                .add(3.0 * k * k)
                .multiply(1.0 / (2.0 * k))
                .multiply(ck.get(k - 1))
                .subtract( //
                    a.negate()
                        .add(b)
                        .add(c.negate())
                        .add(-0.5)
                        .add(k)
                        .multiply(a.negate().add(b.negate()).add(c).add(-0.5).add(k))
                        .multiply(a.negate().add(b).add(c).add(-2.5).add(k))
                        .multiply(ck.get(k - 2))) //
            );

        wLast = w.apply(k);
        s =
            s.add(
                u1.multiply(new Complex(0.0, -1.0).pow(k))
                    .multiply(wLast)
                    .add( //
                        u2.multiply(Complex.I.pow(k)).multiply(wLast)));
        k++;
      }

      Complex t1 =
          Arithmetic.lanczosApproxGamma(a)
              .reciprocal()
              .multiply(x.negate().pow(p))
              .multiply(s)
              .divide(2.0 * Math.sqrt(Math.PI));

      Complex t2 =
          Arithmetic.lanczosApproxGamma(b.subtract(a))
              .reciprocal()
              .multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal())
              .multiply(x.negate().pow(a.negate()))
              .multiply(
                  hypergeometricSeries(
                      new Complex[] {a, a.add(b.negate()).add(1), a.add(c.negate().add(1.0))},
                      new Complex[] {},
                      x.reciprocal())); // , true ) );
      // var t2 = mul( inv(gamma(sub(b,a))), inv(gamma(sub(c,a))), pow(neg(x),neg(a)),
      // hypergeometricSeries( [ a, add(a,neg(b),1), add(a,neg(c),1) ], [], inv(x), true ) );
      return Arithmetic.lanczosApproxGamma(b)
          .multiply(Arithmetic.lanczosApproxGamma(c))
          .multiply(t1.add(t2));
    }

    return hypergeometricSeries(new Complex[] {a}, new Complex[] {b, c}, x);
  }

  public static double hypergeometric1F2(double a, double b, double c, double x) {
    final double useAsymptotic = 200;
    // asymptotic form is complex
    if (Math.abs(x) > useAsymptotic)
      return hypergeometric1F2(new Complex(a), new Complex(b), new Complex(c), new Complex(x))
          .getReal();

    return hypergeometricSeries(new double[] {a}, new double[] {b, c}, x);
  }

  // public static double hypergeometricPFQ(double[] A, double[] B, double x) {
  // return hypergeometricPFQ(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
  // }

  public static double hypergeometricPFQ(double[] A, double[] B, double x) {
    // dlmf.nist.gov/16.11 for general transformations
    if (Math.abs(x) > 1.0) {
      throw new ArgumentTypeException("general hypergeometric argument currently restricted");
    }
    return hypergeometricSeries(A, B, x);
  }

  public static Complex hypergeometricU(Complex a, Complex b, Complex x) {

    double useAsymptotic = 20;

    // asymptotic form as per Johansson arxiv.org/abs/1606.06977
    if (x.norm() > useAsymptotic) {

      return x.pow(a.negate())
          .multiply(hypergeometric2F0(a, a.add(b.negate()).add(1.0), x.reciprocal().negate()));
    }

    if (b.equals(Complex.ONE)
        || //
        (F.isNumIntValue(b.getReal(), 1) && F.isZero(b.getImaginary()))) {
      return complexAverage(arg -> hypergeometricU(a, arg, x), b);
    }

    Complex t1 =
        Arithmetic.lanczosApproxGamma(b.subtract(1))
            .multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
            .multiply(
                x.pow(Complex.ONE.subtract(b))
                    .multiply(
                        hypergeometric1F1(a.add(b.negate()).add(1.0), b.negate().add(2.0), x)));

    Complex t2 =
        Arithmetic.lanczosApproxGamma(Complex.ONE.subtract(b))
            .multiply(Arithmetic.lanczosApproxGamma(a.add(b.negate()).add(1.0)).reciprocal())
            .multiply(hypergeometric1F1(a, b, x));

    return t1.add(t2);
  }

  public static Complex whittakerM(Complex k, Complex m, Complex x) {
    return x.multiply(-0.5)
        .exp()
        .multiply(x.pow(m.add(0.5)))
        .multiply(hypergeometric1F1(m.add(k.negate()).add(0.5), m.multiply(2.0).add(1.0), x));
  }

  public static Complex whittakerW(Complex k, Complex m, Complex x) {
    return x.multiply(-0.5)
        .exp()
        .multiply(x.pow(m.add(0.5)))
        .multiply(hypergeometricU(m.add(k.negate()).add(0.5), m.multiply(2.0).add(1.0), x));
  }
}
