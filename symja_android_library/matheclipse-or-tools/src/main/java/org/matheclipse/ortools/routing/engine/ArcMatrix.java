package org.matheclipse.ortools.routing.engine;

import java.util.function.LongBinaryOperator;

/**
 * The values of a transit or cost callback between routing indices, cached in a dense matrix for
 * models of moderate size.
 *
 * <p>
 * Part of the simplified routing engine which reimplements the behaviour of the OR-Tools routing
 * library for this module.
 */
public final class ArcMatrix {

  /** Models with more indices call the callback directly instead of caching it. */
  public static final int MAX_DENSE_SIZE = 2500;

  private final int n;

  private final long[] values;

  private final LongBinaryOperator callback;

  private final boolean zero;

  /** Wraps <code>callback</code> for <code>n</code> indices. */
  public ArcMatrix(int n, LongBinaryOperator callback) {
    this.n = n;
    this.zero = callback == null;
    this.callback = callback;
    if (callback != null && n <= MAX_DENSE_SIZE) {
      values = new long[n * n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          values[i * n + j] = callback.applyAsLong(i, j);
        }
      }
    } else {
      values = null;
    }
  }

  /** The value for the arc <code>i -&gt; j</code>. */
  public long get(int i, int j) {
    if (zero) {
      return 0;
    }
    if (values != null) {
      return values[i * n + j];
    }
    return callback.applyAsLong(i, j);
  }

  /** Whether all values are zero because there is no callback. */
  public boolean isZero() {
    return zero;
  }
}
