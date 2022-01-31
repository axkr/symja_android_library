package org.matheclipse.core.interfaces;

/** An expression representing a complex number */
public interface IComplex extends IBigNumber {
  void checkBitLength();

  public IComplex add(IComplex val);

  /**
   * Returns the imaginary part of a complex number
   *
   * @return imaginary part
   */
  public IRational getImaginaryPart();

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  public IRational getRealPart();

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
   * Returns this number raised at the specified exponent. See
   * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation
   * by squaring</a>
   *
   * @param n the exponent.
   * @return <code>this<sup>exp</sup></code>
   * @throws ArithmeticException if {@code 0^0} is given.
   */
  public IComplex pow(int n);

  public IComplex[] quotientRemainder(final IComplex c2);

  /**
   * If possible, calculate the square root of this complex number. Otherwise return <code>null
   * </code>.
   *
   * @return <code>null</code> if no symbolic complex root is found
   */
  public IComplex sqrtCC();

  /**
   * Return the normalized form of this number (i.e. if the imaginary part equals zero, return the
   * real part as a fractional or integer number).
   *
   * @return
   */
  public INumber normalize();

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  @Override
  public IRational re();
}
