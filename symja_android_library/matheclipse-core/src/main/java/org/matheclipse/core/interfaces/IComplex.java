package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.UniformFlags;

/** An expression representing a complex number */
public interface IComplex extends IBigNumber {
  public IComplex add(IComplex val);

  void checkBitLength();

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   */
  @Override
  public IRational im();

  @Override
  public IComplex inverse();

  public IComplex multiply(IComplex val);

  /**
   * Return the normalized form of this number (i.e. if the imaginary part equals zero, return the
   * real part as a fractional or integer number).
   *
   * @return
   */
  public INumber normalize();

  /**
   * Returns this number raised at the specified exponent. See
   * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation
   * by squaring</a>
   *
   * @param n the exponent.
   * @return <code>this<sup>exp</sup></code>
   * @throws ArithmeticException if {@code 0^0} is given.
   */
  @Override
  public IComplex pow(int n);

  public IComplex[] quotientRemainder(final IComplex c2);

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  @Override
  public IRational re();

  /**
   * If possible, calculate the square root of this complex number. Otherwise return <code>null
   * </code>.
   *
   * @return <code>null</code> if no symbolic complex root is found
   */
  public IComplex sqrtCC();

  public IComplex subtract(IComplex that);

  @Override
  default int uniformFlags() {
    return UniformFlags.COMPLEX | UniformFlags.NUMBER | UniformFlags.ATOM;
  }
}
