package org.matheclipse.core.builtin.functions;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.S;

public class ZetaJS extends JS {
  private static final int MAX_VALUE_HALF = Integer.MAX_VALUE / 2;

  private ZetaJS() {}

  public static Complex summation(java.util.function.Function<Complex, Complex> f, int a, int b,
      int iterationLimit) {

    Complex s = Complex.ZERO;
    int counter = 0;
    for (int i = a; i <= b; i++) {
      if (counter++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(counter, S.Sum);
      }
      s = s.add(f.apply(new Complex(i)));
    }

    return s;
  }

  public static Complex complexAverage(java.util.function.DoubleFunction<Complex> f, double x) {
    double offset = 1e-5;
    Complex arg1 = f.apply(x + offset);
    Complex arg2 = f.apply(x - offset);
    return arg1.add(arg2).divide(2.0);
  }

  public static Complex complexSummation(java.util.function.DoubleFunction<Complex> f, int a, int b,
      int iterationLimit) {
    Complex s = Complex.ZERO;
    int counter = 0;
    for (int i = a; i <= b; i++) {
      if (counter++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(counter, S.Sum);
      }
      s = s.add(f.apply(i));
    }
    return s;
  }

  public static double sumDouble(java.util.function.DoubleUnaryOperator f, double a, double b,
      int iterationLimit) {
    double s = 0.0;
    int counter = 0;
    for (double i = a; i <= b; i++) {
      if (counter++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(counter, S.Sum);
      }
      s += f.applyAsDouble(i);
    }
    return s;
  }

  public static double sumInt(java.util.function.IntToDoubleFunction f, int a, int b,
      int iterationLimit) {
    double s = 0;
    if ((b - a) > iterationLimit && iterationLimit > 0) {
      IterationLimitExceeded.throwIt((b - a), S.Sum);
    }
    for (int i = a; i <= b; i++) {
      s += f.applyAsDouble(i);
    }
    return s;
  }

  public static double zeta(double x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();

    Apfloat apfloat = new Apfloat(x);
    Apfloat zeta = h.zeta(apfloat);
    return zeta.doubleValue();
  }

  public static Complex zeta(Complex x) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();

    Apcomplex apcomplex = new Apcomplex(new Apfloat(x.getReal()), new Apfloat(x.getImaginary()));
    Apcomplex zeta = h.zeta(apcomplex);
    return new Complex(zeta.real().doubleValue(), zeta.imag().doubleValue());
  }

  public static Complex dirichletEta(double x) {
    return dirichletEta(new Complex(x));
  }

  public static Complex dirichletEta(Complex x) {
    return zeta(x).multiply(Complex.ONE.subtract(new Complex(2.0).pow(Complex.ONE.subtract(x))));
  }

  // public static Complex zeta(Complex x ) {
  //
  // // Borwein algorithm
  //
  // int n = 14; // from error bound for tolerance
  //
  // if ( x.getImaginary() != 0.0 ) {//isComplex(x) &&
  // n = Math.max( n, Math.ceil( log( 2.0 / GammaJS.gamma(x),abs( ) / tolerance ) / log( 3.0 +
  // Math.Sqrt(8.0) ) ) );
  // }
  // int[] d = new int[] { 1 };
  // for ( int i = 1 ; i <= n ; i++ ) {
  // // order of multiplication reduces overflow, but factorial overflows at 171
  // d.push( d[i-1] + n * factorial( n+i-1 ) / factorial( n-i ) / factorial( 2*i ) * 4**i );
  // }
  // if ( x.getImaginary() != 0.0 ) {//isComplex(x)
  //
  // // functional equation dlmf.nist.gov/25.4.2
  // if ( x.re < 0 ) {
  // return mul( pow(2,x), pow(pi,sub(x,1)), sin( mul(pi/2,x) ), GammaJS.gamma( sub(1,x) ), zeta(
  // sub(1,x) ) );
  // }
  // Complex s = summation( k => div( (-1)**k * ( d[k] - d[n] ), pow( k+1, x ) ), [0,n-1] );
  //
  // return div( div( s, -d[n] ), sub( 1, pow( 2, sub(1,x) ) ) );
  //
  // } else {
  //
  // // functional equation dlmf.nist.gov/25.4.2
  // if ( x < 0 ) {
  // return 2**x * pi**(x-1) * sin(pi*x/2) * GammaJS.gamma(1-x) * zeta(1-x);
  // }
  //
  // Complex s = summation( k => (-1)**k * ( d[k] - d[n] ) / (k+1)**x, [0,n-1] );
  //
  // return -s / d[n] / ( 1 - 2**(1-x) );
  //
  // }
  //
  // }

  // public static Complex dirichletEta(Complex x ) {
  // return mul( zeta(x), sub( 1, pow( 2, sub(1,x) ) ) );
  // }

  public static double bernoulliInt(int n) {
    return NumberTheory.bernoulliDouble(n);
  }

  // public static Complex harmonic(int n ) {
  //
  // if ( !Number.isInteger(n) ) throw Error( 'Noninteger argument for harmonic number' );
  //
  // return summation( i => 1/i, [1,n] );
  //
  // }

  public static Complex bernoulli(int n, Complex x) {

    // if (!Number.isInteger(n) ) {
    // throw Error( 'Noninteger index for Bernoulli number' );
    // }

    if (n < 0) {
      throw new ArgumentTypeException("Unsupported index for Bernoulli number");
    }

    if (!x.equals(Complex.ZERO)) {
      return hurwitzZeta(new Complex(1 - n), x).multiply(-n);
    }

    if (n == 0) {
      return Complex.ONE;
    }

    if (n == 1) {
      return new Complex(-0.5);
    }

    if ((n & 1) == 1) {
      return Complex.ZERO;
    }

    return new Complex(-n * zeta(1 - n));
  }

  /**
   * @param x
   * @param a
   * @return
   */
  public static Complex hurwitzZeta(final Complex x, final Complex a) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();

    Apcomplex xApfloat = new Apcomplex(new Apfloat(x.getReal()), new Apfloat(x.getImaginary()));
    Apcomplex aApfloat = new Apcomplex(new Apfloat(a.getReal()), new Apfloat(a.getImaginary()));
    Apcomplex zeta = h.zeta(xApfloat, aApfloat);
    return new Complex(zeta.real().doubleValue(), zeta.imag().doubleValue());

    // // TODO INVALID at the moment
    // if (x.getReal() == 1.0 && x.getImaginary() == 0.0) {
    // throw new ArgumentTypeException("Hurwitz zeta pole");
    // }
    //
    // // direct summation more accurate than dlmf.nist.gov/25.11.4 for positive a
    // int iterationLimit = EvalEngine.get().getIterationLimit();
    //
    // if (a.getReal() < 0.0) {
    // int m = -(int) Math.floor(a.getReal());
    // if (m == Integer.MIN_VALUE || m == Integer.MAX_VALUE) {
    // throw new ArgumentTypeException("Summation exceeeded");
    // }
    // return hurwitzZeta(x, a.add(m))
    // .add(summation(i -> a.add(i).pow(x.negate()), 0, m - 1, iterationLimit));
    // }
    //
    // // Johansson arxiv.org/abs/1309.2877
    //
    // int n = 15; // recommendation of Vepstas, Efficient Algorithm, p.12
    //
    // // Euler-Maclaurin has differences of large values in left-hand plane
    // boolean useArbitrary = x.getReal() < 0;
    // // if ( useArbitrary ) {
    // //
    // // }
    //
    // double switchForms = -5.0;
    //
    // if (x.getReal() < switchForms) {
    // throw new ArgumentTypeException("Currently unsuppported complex Hurwitz zeta");
    // }
    //
    // Complex S = summation(i -> a.add(i).pow(x.negate()), 0, n - 1, iterationLimit);
    //
    // Complex I = a.add(n).pow(Complex.ONE.subtract(x)).divide(x.subtract(1.0));
    //
    // Complex p = x.multiply(0.5).multiply(a.add(n).reciprocal());
    // Complex t = p.multiply(bernoulliInt(2));
    // int i = 1;
    //
    // // converges rather quickly
    // while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
    // || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
    // if (i++ > iterationLimit && iterationLimit > 0) {
    // IterationLimitExceeded.throwIt(i, org.matheclipse.core.expression.S.HurwitzZeta);
    // }
    // if (i > MAX_VALUE_HALF) {
    // throw new ArgumentTypeException("Hurwitz zeta: i > MAX_VALUE_HALF");
    // }
    // int iPlusi = i + i;
    // p = p.multiply(x.add(iPlusi - 2.0).multiply(x.add(iPlusi - 3.0))
    // .multiply(a.add(n).pow(2.0).multiply(iPlusi * (iPlusi - 1)).reciprocal()));
    // t = t.add(p.multiply(bernoulliInt(iPlusi)));
    // }
    //
    // Complex T = t.add(0.5).divide(a.add(n).pow(x));
    //
    // return S.add(I).add(T);
  }

  public static double hurwitzZeta(final double x, final double a) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();

    Apfloat xApfloat = new Apfloat(x);
    Apfloat aApfloat = new Apfloat(a);
    Apfloat zeta = h.zeta(xApfloat, aApfloat);
    return zeta.doubleValue();

    //
    // // Johansson arxiv.org/abs/1309.2877
    //
    // if (x == 1.0) {
    // throw new ArgumentTypeException("Hurwitz zeta pole");
    // }
    //
    // if (a < 0.0) {
    // throw new ArgumentTypeException("Hurwitz zeta a < 0.0 ");
    // // return hurwitzZeta( x, complex(a) );
    // }
    // // direct summation more accurate than dlmf.nist.gov/25.11.4
    //
    // int iterationLimit = EvalEngine.get().getIterationLimit();
    //
    // // Euler-Maclaurin has differences of large values in left-hand plane
    // // swith to difference summation: dlmf.nist.gov/25.11.9
    //
    // double switchForms = -5.0;
    //
    // if (x < switchForms) {
    //
    // final double xValue = 1 - x;
    // double t = Math.cos(Math.PI * xValue / 2.0 - 2.0 * Math.PI * a);
    // double s = t;
    // int i = 1;
    //
    // while (Math.abs(t) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
    // if (i++ > iterationLimit && iterationLimit > 0) {
    // IterationLimitExceeded.throwIt(i, S.HurwitzZeta);
    // }
    // t = Math.cos(Math.PI * xValue / 2.0 - 2.0 * i * Math.PI * a) / Math.pow(i, xValue);
    // s += t;
    // }
    //
    // return 2.0 * GammaJS.gamma(xValue) / Math.pow(2.0 * Math.PI, xValue) * s;
    // }
    //
    // // Johansson arxiv.org/abs/1309.2877
    // final int n = 15; // recommendation of Vepstas, Efficient Algorithm, p.12
    //
    // double S = sumDouble(i -> 1.0 / Math.pow(a + i, x), 0, n - 1, iterationLimit);
    //
    // double I = Math.pow(a + n, 1.0 - x) / (x - 1.0);
    //
    // double p = x / 2.0 / (a + n);
    // double t = bernoulliInt(2) * p;
    // int i = 1;
    // // converges rather quickly
    // while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
    // if (i++ > iterationLimit && iterationLimit > 0) {
    // IterationLimitExceeded.throwIt(i, org.matheclipse.core.expression.S.HurwitzZeta);
    // }
    // if (Double.isNaN(t)) {
    // throw new ArgumentTypeException("Hurwitz zeta: t == NaN");
    // }
    // if (Double.isInfinite(p)) {
    // throw new ArgumentTypeException("Hurwitz zeta: p == Infinity");
    // }
    // if (i > MAX_VALUE_HALF) {
    // throw new ArgumentTypeException("Hurwitz zeta: i > MAX_VALUE_HALF");
    // }
    // int iPlusi = i + i;
    // p *= (x + iPlusi - 2.0) * (x + iPlusi - 3.0)
    // / (iPlusi * (iPlusi - 1.0) * Math.pow(a + n, 2.0));
    // t += bernoulliInt(iPlusi) * p;
    // }
    //
    // double T = (0.5 + t) / Math.pow(a + n, x);
    //
    // return S + I + T;
  }

  public static Complex polyLog(final Complex n, final Complex x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
    Apcomplex nApfloat = new Apcomplex(new Apfloat(n.getReal()), new Apfloat(n.getImaginary()));
    Apcomplex xApfloat = new Apcomplex(new Apfloat(x.getReal()), new Apfloat(x.getImaginary()));
    Apcomplex polylog = h.polylog(nApfloat, xApfloat);
    return new Complex(polylog.real().doubleValue(), polylog.imag().doubleValue());
    //
    // if (x.equals(Complex.ONE)) {
    // return zeta(n);
    // }
    //
    // if (x.equals(Complex.MINUS_ONE)) {
    // return dirichletEta(n).negate();
    // }
    //
    // if (n.equals(Complex.ONE)) {
    // return Complex.ONE.subtract(x).log().negate();
    // }
    //
    // if (n.equals(Complex.ZERO)) {
    // return x.divide(Complex.ONE.subtract(x));
    // }
    //
    // if (n.equals(Complex.MINUS_ONE)) {
    // return x.divide(Complex.ONE.subtract(x).multiply(Complex.ONE.subtract(x)));
    // }
    //
    // if (cabs(x) > 1.0) {
    //
    // if (F.isZero(n.getImaginary()) && F.isNumIntValue(n.getReal()) && n.getReal() > 0.0) {
    //
    // final int nInt = NumberUtil.toInt(n.getReal());
    // Complex twoPiI = new Complex(0, 2 * Math.PI);
    //
    // // Crandall, Note on Fast Polylogarithm Computation
    //
    // Complex t1 = polyLog(n, x.reciprocal()).multiply(Math.pow(-1.0, nInt));
    // Complex t2 = twoPiI.pow(nInt).divide(GammaJS.factorialInt(nInt))
    // .multiply(bernoulli(nInt, x.log().divide(twoPiI)));
    //
    // Complex t3 = x.getImaginary() < 0.0 || (F.isZero(x.getImaginary()) && x.getReal() >= 1.0)
    // ? twoPiI.multiply(x.log().pow(nInt - 1).divide(GammaJS.factorialInt(nInt - 1)))
    // : Complex.ZERO;
    //
    // return t1.add(t2).add(t3).negate();
    // }
    //
    // Complex v = Complex.ONE.subtract(n);
    // Complex I = Complex.I;
    // Complex L = x.negate().log().divide(new Complex(0, 2.0 * Math.PI));
    //
    // Complex z1 = I.pow(v).multiply(hurwitzZeta(v, L.add(0.5)));
    // if (z1.isInfinite() || z1.isNaN()) {
    // throw new ArgumentTypeException("Infinite or NaN number in z1 calculation.");
    // }
    // Complex z2 = I.pow(v.negate()).multiply(hurwitzZeta(v, new Complex(0.5).subtract(L)));
    // if (z2.isInfinite() || z2.isNaN()) {
    // throw new ArgumentTypeException("Infinite or NaN number in z2 calculation.");
    // }
    // return GammaJS.gamma(v).multiply(new Complex(2.0 * Math.PI).pow(v.negate()))
    // .multiply(z1.add(z2));
    // }
    //
    // Complex s = x;
    // Complex p = Complex.ONE; // complex(1);
    // int i = 1;
    //
    // int iterationLimit = EvalEngine.get().getIterationLimit();
    // while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
    // || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
    // if (i++ > iterationLimit && iterationLimit > 0) {
    // IterationLimitExceeded.throwIt(i, S.PolyLog);
    // }
    // p = x.pow(i).divide(new Complex(i).pow(n));
    // s = s.add(p);
    // }
    //
    // return s;
  }

  public static double polyLog(final double n, final double x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();

    Apfloat nApfloat = new Apfloat(n);
    Apfloat xApfloat = new Apfloat(x);
    Apfloat polylog = h.polylog(nApfloat, xApfloat);
    return polylog.doubleValue();
    // if (F.isEqual(x, 1.0)) {
    // return new Complex(zeta(n));
    // }
    //
    // if (F.isEqual(x, -1.0)) {
    // return dirichletEta(n).negate();
    // }
    //
    // double oneMinusX = 1.0 - x;
    // if (F.isEqual(n, 1.0)) {
    // return new Complex(-Math.log(oneMinusX));
    // }
    //
    // if (F.isEqual(n, 0.0)) {
    // return new Complex(x / oneMinusX);
    // }
    //
    // if (F.isEqual(n, -1.0)) {
    // return new Complex(x / (oneMinusX * oneMinusX));
    // }
    //
    // if (Math.abs(x) > 1.0) {
    // if (F.isNumIntValue(n) && n > 0.0) {
    // final int nInt = NumberUtil.toInt(n);
    // Complex twoPiI = new Complex(0, 2 * Math.PI);
    //
    // // Crandall, Note on Fast Polylogarithm Computation
    //
    // Complex t1 = polyLog(n, 1 / x).multiply(Math.pow(-1.0, nInt));
    //
    // Complex t2 = twoPiI.pow(nInt).divide(GammaJS.factorialInt(n))
    // .multiply(bernoulli(nInt, new Complex(x).log().divide(twoPiI)));
    //
    // Complex y = new Complex(x); // just for test
    // Complex t3 = y.getImaginary() < 0.0 || (F.isZero(y.getImaginary()) && y.getReal() >= 1.0)
    // ? twoPiI.multiply(Math.pow(Math.log(x), n - 1) / GammaJS.factorialInt(n - 1))
    // : Complex.ZERO;
    //
    // Complex result = t1.add(t2).add(t3).negate();
    //
    // // real on negative real axis
    // if (x < 0) {
    // return new Complex(result.getReal());
    // }
    //
    // return result;
    // }
    // return polyLog(new Complex(n), new Complex(x));
    // }
    //
    // double s = x;
    // double p = 1;
    // int i = 1;
    //
    // int iterationLimit = EvalEngine.get().getIterationLimit();
    // while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
    // if (i++ > iterationLimit && iterationLimit > 0) {
    // IterationLimitExceeded.throwIt(i, S.PolyLog);
    // }
    // p = Math.pow(x, i) / Math.pow(i, n);
    // s += p;
    // }
    //
    // return new Complex(s);
  }
}
