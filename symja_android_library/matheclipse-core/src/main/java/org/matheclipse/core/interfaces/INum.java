package org.matheclipse.core.interfaces;

import org.apfloat.Apfloat;
import org.matheclipse.core.expression.F;

/**
 * Interface for a numeric double or {@link Apfloat} number.
 */
public interface INum extends IReal, IInexactNumber {
  public static final double Catalan = 0.91596559417721901505460351493238411077414937428167;
  /** <code> Math.PI / 180.0</code> */
  public static final double Degree = 0.01745329251994329576923690768488612713442871888541;

  public static final double EulerGamma = 0.57721566490153286060651209008240243104215933593992;
  public static final double Glaisher = 1.2824271291006226368753425688697917277676889273250;
  public static final double GoldenRatio = 1.6180339887498948482045868343656381177203091798058;
  public static final double Khinchin = 2.6854520010653064453097148354817956938203822939945;

  public double getRealPart();

  @Override
  default IReal add(IReal val) {
    return add(F.num(val.reDoubleValue()));
  }

  public INum add(INum val);

  @Override
  public IInteger ceilFraction();

  public INum divide(INum val);

  public INum multiply(INum val);


  @Override
  default IReal multiply(IReal val) {
    return multiply(F.num(val.reDoubleValue()));
  }

  public INum pow(INum val);

  public INum subtract(INum val);

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue();

  /**
   * Returns the value of this number as an <code>int</code> (by simply casting to type <code>int
   * </code>).
   *
   * @return
   */
  public int intValue();

  /**
   * Converts this double value to an <code>int</code> value; unlike {@link #intValue} this method
   * raises {@link ArithmeticException} if this integer cannot be represented by an <code>int</code>
   * type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   *         .
   * @throws ArithmeticException if conversion to <code>int</code> is not possible.
   */
  @Override
  public int toInt() throws ArithmeticException;

  public long precision();
}
