package org.matheclipse.core.interfaces;

import edu.jas.arith.BigComplex;

/**
 * Implemented by all exact &quot;symbolic&quot; number interfaces (i.e. IInteger IFraction,
 * IComplex)
 */
public interface IBigNumber extends INumber {
  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  public IRational getRealPart();

  /**
   * Returns the imaginary part of a complex number
   *
   * @return imaginary part
   */
  public IRational getImaginaryPart();

  /**
   * Create a numeric number from this exact &quot;symbolic&quot; number.
   *
   * @return
   */
  public INumber numericNumber();

  /**
   * Get the real part as rational number
   *
   * @return
   */
  public IRational reRational();

  /**
   * Get the imaginary part as rational number
   *
   * @return
   */
  public IRational imRational();

  /**
   * Returns this number as {@link edu.jas.arith.BigComplex} number.
   *
   * @return <code>this</code> number s big complex representation.
   */
  public BigComplex toBigComplex();

}
