package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.IntegerSym;
import edu.jas.arith.BigComplex;

/**
 * Implemented by all exact &quot;symbolic&quot; number interfaces (i.e. IInteger IFraction,
 * IComplex)
 */
public interface IBigNumber extends INumber {

  // -(2^53 - 1)
  public static final IInteger minSafeInt = IntegerSym.valueOf(-9007199254740991L);

  // 2^53 - 1
  public static final IInteger maxSafeInt = IntegerSym.valueOf(9007199254740991L);

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
