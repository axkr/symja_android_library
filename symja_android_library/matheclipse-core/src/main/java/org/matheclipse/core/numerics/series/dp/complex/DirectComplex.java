package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * Naively computes the limit of a sequence or series by inspecting elements one a time. No
 * transform of the original sequence is performed.
 */
public final class DirectComplex extends SeriesAlgorithmComplex {

  public DirectComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
  }


  @Override
  public final Complex next(final Complex e, final Complex term) {
    ++myIndex;
    return term;
  }

  @Override
  public final String getName() {
    return "Direct Complex Sum";
  }
}
