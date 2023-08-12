// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.Integers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 
 * tensor::BSplineFunction is parameterized over the interval [0, control.length() - 1]
 * 
 * tensor::BSplineFunction can be instantiated for all degrees regardless of the length of the
 * control points.
 * 
 * Mathematica::BSplineFunction throws an exception if number of control points is insufficient for
 * the specified degree.
 * 
 * tensor::BSplineFunction uses uniform knot spacing except for string alignment at the terminal
 * points.
 * 
 * <p>
 * Quote from Wikipedia: The term "B-spline" was coined by Isaac Jacob Schoenberg and is short for
 * basis spline. A spline is a piecewise polynomial function of a given degree in a variable x. The
 * values of x where the pieces of polynomial meet are known as knots, denoted ..., t0, t1, t2, ...
 * and sorted into non-decreasing order.
 */
public abstract class BSplineFunction implements Function<IExpr, IExpr> {
  private static final int CACHE_SIZE = 16;
  // ---
  // private final Cache<Integer, DeBoor> cache = Cache.of(new Inner(), CACHE_SIZE);
  public Cache<Integer, DeBoor> cache = Caffeine.newBuilder()//
      .maximumSize(CACHE_SIZE)//
      .build();
  protected final BinaryAverage binaryAverage;
  protected final int degree;
  private final IAST sequence;
  /** half == degree / 2 */
  private final int half;
  /** shift is 0 for odd degree and 1/2 for even degree */
  protected final IExpr shift;

  protected BSplineFunction(BinaryAverage binaryAverage, int degree, IAST sequence) {
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
    this.degree = Integers.requirePositiveOrZero(degree);
    this.sequence = Objects.requireNonNull(sequence);
    half = degree / 2;
    shift = Integers.isEven(degree) //
        ? F.C1D2
        : F.C0;
  }

  /**
   * @param k
   * @return
   */
  public final DeBoor deBoor(int k) {
    DeBoor deBoor = null;
    deBoor = cache.getIfPresent(k);
    if (deBoor == null) {
      deBoor = createDeBoor(k);
      cache.put(k, deBoor);
    }
    return deBoor;
  }

  // private class Inner extends CacheLoader<Integer, DeBoor> {
  // @Override
  // public DeBoor load(Integer k) {
  // return createDeBoor(k);
  // }
  // }

  private DeBoor createDeBoor(int k) {
    IAST knots = knots(k);
    IAST control = F.ListAlloc(IntStream.range(k - half, k + degree + 1 - half)//
        .map(i -> BSplineFunction.this.bound(i + 1)) //
        .mapToObj(i -> sequence.get(i)));
    return new DeBoor(binaryAverage, degree, knots, //
        control);

    // return new DeBoor(binaryAverage, degree, knots(k), //
    // Tensor.of(IntStream.range(k - half, k + degree + 1 - half) // control
    // .map(BSplineFunction.this::bound) //
    // .mapToObj(sequence::get)));
  }

  /**
   * @param k
   * @return knot vector corresponding to interval k
   */
  protected abstract IAST knots(int k);

  /**
   * @param index
   * @return index mapped to position in control point sequence
   */
  protected abstract int bound(int index);
}
