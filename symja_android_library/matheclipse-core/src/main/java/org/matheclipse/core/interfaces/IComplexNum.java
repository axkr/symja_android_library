package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.UniformFlags;

/** */
public interface IComplexNum extends INumber, IInexactNumber {
  public IComplexNum add(IComplexNum val);

  @Override
  public IComplexNum conjugate();

  /**
   * Return the absolute value of this complex number.
   *
   * <p>
   * Returns <code>NaN</code> if either real or imaginary part is <code>NaN</code> and <code>
   * Double.POSITIVE_INFINITY</code> if neither part is <code>NaN</code>, but at least one part
   * takes an infinite value.
   *
   * @return the absolute value
   */
  public double dabs();

  public IComplexNum divide(IComplexNum val);

  public double getImaginaryPart();

  public double getRealPart();

  /** {@inheritDoc} */
  @Override
  default COMPARE_TERNARY isIrrational() {
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  @Override
  default long leafCount() {
    return 3;
  }

  public IComplexNum multiply(IComplexNum val);

  public IComplexNum pow(IComplexNum val);

  public long precision();

  public IComplexNum subtract(IComplexNum subtrahend);

  @Override
  default int uniformFlags() {
    return UniformFlags.COMPLEX | UniformFlags.NUMBER | UniformFlags.ATOM;
  }
}
