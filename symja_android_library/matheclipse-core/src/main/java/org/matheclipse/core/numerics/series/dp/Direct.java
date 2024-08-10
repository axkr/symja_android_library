package org.matheclipse.core.numerics.series.dp;

/**
 * Naively computes the limit of a sequence or series by inspecting elements one a time. No
 * transform of the original sequence is performed.
 */
public final class Direct extends SeriesAlgorithm {

  public Direct(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
  }

  @Override
  public final double next(final double e, final double term) {
    ++myIndex;
    return term;
  }

  @Override
  public final String getName() {
    return "Direct Sum";
  }
}
