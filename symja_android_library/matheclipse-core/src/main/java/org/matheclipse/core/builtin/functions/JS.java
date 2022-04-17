package org.matheclipse.core.builtin.functions;

import java.util.function.Function;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;

/**
 * Wrappers for {@link org.hipparchus.complex.Complex} functions in JavaScript style.
 * 
 * See: <a href="https://github.com/paulmasson/math">Github - paulmasson/math</a>
 *
 */
public abstract class JS {
  protected static double trunc(double value) {
    return value < 0 ? Math.ceil(value) : Math.floor(value);
  }

  protected static double cabs(Complex x) {
    return x.norm();
  }

  /**
   * For approximate <code>x</code> values that are close to zero return the <code>double</code>
   * value <code>0.0</code>.
   * 
   * @param x
   * @return
   */
  protected static double chop(double x) {
    return (Math.abs(x) < Config.SPECIAL_FUNCTIONS_TOLERANCE) ? 0.0 : x;
  }

  /**
   * For approximate <code>x.getReal()</code> or <code>x.getImaginary()</code> values that are close
   * to zero return the <code>double</code> value <code>0.0</code>.
   * 
   * @param x
   * @return
   */
  protected static Complex chop(Complex x) { // , tolerance=1e-10 ) {
    return new Complex(chop(x.getReal()), chop(x.getImaginary()));
  }

  protected static Complex complexAverage(Function<Complex, Complex> f, Complex x) {
    return complexAverage(f, x, 1e-5);
  }

  protected static Complex complexAverage(Function<Complex, Complex> f, Complex x, double offset) {
    return div(add(f.apply(add(x, offset)), f.apply(sub(x, offset))), 2);
  }

  protected static boolean isUnity(Complex x) {
    return org.hipparchus.complex.Complex.equals(x, org.hipparchus.complex.Complex.ONE,
        Config.MACHINE_EPSILON);
  }


  protected static Complex exp(Complex x) {
    return x.exp();
  }

  protected static Complex gamma(Complex x) {
    return Arithmetic.lanczosApproxGamma(x);
  }

  protected static Complex inv(Complex x) {
    return x.reciprocal();
  }

  protected static Complex sqrt(Complex x) {
    return x.sqrt();
  }

  protected static Complex sqrt(double x) {
    return new Complex(x).sqrt();
  }

  protected static Complex neg(Complex x) {
    return x.negate();
  }

  /**
   * Return <code>x.pow(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex pow(Complex x, Complex y) {
    return x.pow(y);
  }

  /**
   * Return <code>x.pow(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex pow(Complex x, double y) {
    return x.pow(y);
  }

  /**
   * Return <code>new Complex(x).pow(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex pow(double x, Complex y) {
    return new Complex(x).pow(y);
  }

  /**
   * Return <code>x.add(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex add(Complex x, Complex y) {
    return x.add(y);
  }

  /**
   * Return <code>x.add(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex add(Complex x, double y) {
    return x.add(y);
  }

  protected static Complex add(Complex x, Complex y, Complex... cc) {
    Complex result = x.add(y);
    for (int i = 0; i < cc.length; i++) {
      result = result.add(cc[i]);
    }
    return result;
  }

  protected static Complex add(double x, Complex y, Complex... cc) {
    Complex result = y.add(x);
    for (int i = 0; i < cc.length; i++) {
      result = result.add(cc[i]);
    }
    return result;
  }

  /**
   * Return <code>x.divide(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex div(Complex x, Complex y) {
    return x.divide(y);
  }

  /**
   * Return <code>x.divide(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex div(Complex x, double y) {
    return x.divide(y);
  }

  /**
   * Return <code>new Complex(x).divide(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex div(double x, Complex y) {
    return new Complex(x).divide(y);
  }

  /**
   * Return <code>x.multiply(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex mul(Complex x, Complex y) {
    return x.multiply(y);
  }

  /**
   * Return <code>new Complex(x).multiply(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex mul(double x, Complex y) {
    return new Complex(x).multiply(y);
  }

  /**
   * Return <code>x.multiply(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex mul(Complex x, double y) {
    return x.multiply(y);
  }

  protected static Complex mul(double x, Complex y, Complex... cc) {
    Complex result = y.multiply(x);
    for (int i = 0; i < cc.length; i++) {
      result = result.multiply(cc[i]);
    }
    return result;
  }

  protected static Complex mul(Complex x, Complex y, Complex... cc) {
    Complex result = x.multiply(y);
    for (int i = 0; i < cc.length; i++) {
      result = result.multiply(cc[i]);
    }
    return result;
  }

  /**
   * Return <code>x.subtract(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex sub(Complex x, Complex y) {
    return x.subtract(y);
  }

  /**
   * Return <code>x.subtract(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex sub(Complex x, double y) {
    return x.subtract(y);
  }

  /**
   * Return <code>new Complex(x).subtract(y)</code>.
   * 
   * @param x
   * @param y
   * @return
   */
  protected static Complex sub(double x, Complex y) {
    return new Complex(x).subtract(y);
  }
}
