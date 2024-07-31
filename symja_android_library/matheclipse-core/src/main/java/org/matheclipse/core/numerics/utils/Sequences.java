package org.matheclipse.core.numerics.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * 
 */
public final class Sequences {

  /**
   * Converts a function representing a sequence indexed by the integers to an Iterable.
   * 
   * @param <T> the type of objects
   * @param func a Function representing a sequence explicitly
   * @param start an long representing the starting index at which the sequence begins
   * @return an Iterable object equivalent to the given sequence
   */
  public static final <T> Iterable<T> toIterable(final Function<? super Long, ? extends T> func,
      final long start) {
    return () -> new Iterator<>() {

      private long k = start;

      @Override
      public final boolean hasNext() {
        return k <= Long.MAX_VALUE - 1;
      }

      @Override
      public final T next() {
        return func.apply(k++);
      }
    };
  }

  public static final <T> Iterable<T> toIterable(final Function<? super Long, ? extends T> func,
      final long start, final long end) {
    return () -> new Iterator<>() {

      private long k = start;
      private long length = end;

      @Override
      public final boolean hasNext() {
        return k <= length - 1;
      }

      @Override
      public final T next() {
        return func.apply(k++);
      }
    };
  }

  /**
   * Converts an Iterable representing a sequence into a function with random access. The Iterable's
   * elements are assumed to be zero-based, such that the call to the returning function with
   * argument 0 returns the first element of the Iterable, call with argument 1 returns the second
   * element, and so on.
   * 
   * <p>
   * In order to implement random access, a memory buffer is created with the maximum capacity
   * determined by the maxIndex. Objects are read from the Iterable sequentially until the returning
   * function's argument is reached, and stored in the buffer, if never accessed before. If the
   * elements of the Iterable have already been accessed in previous calls to the returning
   * function, then elements are simply read from the buffer. This allows the elements of the
   * Iterable that have previously been consumed to be reused, without iterating through the
   * Iterable's Iterator again.
   * </p>
   * 
   * @param seq the Iterable representing a sequence
   * @param maxIndex the maximum index of the returning function
   * @return a Function whose elements at each index are the elements of the Iterable at the
   *         corresponding index
   */
  public static final Function<Long, Double> toFunction(final Iterable<Double> seq,
      final int maxIndex) {
    final Iterator<Double> it = seq.iterator();
    final SequentialDoubleArray memory = new SequentialDoubleArray(maxIndex + 1);

    return (k) -> {
      final int index = Math.toIntExact(k);

      // add objects to the memory if not observed yet
      while (index >= memory.effectiveLength()) {
        if (it.hasNext()) {
          memory.add(it.next());
        } else {
          throw new NoSuchElementException("Index " + index + " is out of range of the sequence.");
        }
      }

      // read the element at the desired index from the memory
      return memory.get(index);
    };
  }

  /**
   * Given a sequence of elements a(k), a(k + 1), ..., returns a sequence of consecutive differences
   * of the elements, e.g. a(k + 1) - a(k), a(k + 2) - a(k + 1), ....
   * 
   * @param seq the original sequence to transform
   * @return an Iterable representing the sequence of differences of consecutive elements of the
   *         original sequence
   */
  public static final Iterable<Double> difference(final Iterable<Double> seq) {
    return () -> new Iterator<>() {

      private final Iterator<Double> it = seq.iterator();
      private double prevE = 0.0;

      @Override
      public final boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public final Double next() {
        final double e = it.next();
        final double delta = e - prevE;
        prevE = e;
        return delta;
      }
    };
  }

  /**
   * Extracts a subsequence of elements from the original sequence that converges linearly, using
   * the algorithm in [1].
   * 
   * <p>
   * Formally, this method computes a sequence of integers a(0) < a(1) < a(2) ... such that the
   * elements of the sequence u(i) = s[a(i)] converge linearly. In other words, the ratio of the
   * consecutive differences u(i+1) - u(i) approaches rate, where rate is a desired linear rate of
   * convergence.
   * </p>
   * 
   * <p>
   * References:
   * <ul>
   * <li>[1] Brezinski, C., J. P. Delahaye, and B. Germain-Bonne. "Convergence acceleration by
   * extraction of linear subsequences." SIAM journal on numerical analysis 20.6 (1983):
   * 1099-1105.</li>
   * </ul>
   * </p>
   * 
   * @param seq the original subsequence
   * @param rate the desired linear rate of convergence in the open interval (0, 1)
   * @return an Iterable representing a subsequence of the original sequence
   */
  public static final Iterable<Double> extractLinearSubsequence(final Iterable<Double> seq,
      final double rate) {
    return () -> new Iterator<>() {

      final Iterator<Double> it = seq.iterator();
      boolean first = true;
      double coeff = 1.0;
      double eps = Double.NaN;
      double xa = Double.NaN;

      @Override
      public final boolean hasNext() {
        return it.hasNext() && rate > 0.0 && coeff * rate > 0.0;
      }

      @Override
      public final Double next() {

        // at the very first iteration, extract the first two elements
        // return the first element, but compute the difference between the first two
        if (first) {
          final double x0 = it.next();
          final double x1 = it.next();
          eps = Math.abs(x1 - x0);
          coeff *= rate;
          xa = x1;
          first = false;
          return x0;
        }

        // consume elements of the sequence one at a time
        // if the bound condition in [1] is satisfied then the current element belongs
        // in the linear subsequence, otherwise move to the next element in the sequence
        while (it.hasNext()) {
          final double xap1 = it.next();
          if (Math.abs(xap1 - xa) <= coeff * eps) {
            coeff *= rate;
            final double result = xa;
            xa = xap1;
            return result;
          }
          xa = xap1;
        }
        return Double.NaN;
      }
    };
  }

  /**
   * When the elements of a series can be expanded as a power series with known coefficients, the
   * sum of the original series is equivalent to one whose elements are the coefficients of this
   * power series, multiplied by the zeta function at the positive integers.
   * 
   * <p>
   * Formally, given a series S = f(1/2) + f(1/3) ..., we assume that f(x) = a(2) x^2 + a(3) x^3 +
   * ... for all x >= 1. In this case, by performing a double summation and interchanging the order
   * of summation, it is shown that S = a(2) * (1-zeta(2)) + a(3) * (1 - zeta(3)) + .... The
   * advantage of this approach is that 1 - zeta(n) tends to zero exponentially fast, and typically
   * much faster than f(1/k) as k -> infinity. This method is very useful for summing infinite
   * series when f can be expanded in powers and decays slowly in 1/k.
   * </p>
   * 
   * @param powerSeries the power series expansion coefficients a(n) of f
   * @return an Iterable of elements given as a(n) * (1 - zeta(n)), n >= 2.
   */
  public static final Iterable<Double> zetaTransform(final Iterable<Double> powerSeries) {
    return () -> new Iterator<>() {

      private final Iterator<Double> it = powerSeries.iterator();
      private int n = 2;

      @Override
      public final boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public final Double next() {
        final double z = zeta(n);
        final double coeff = it.next();
        final double result = (1.0 - z) * coeff;
        ++n;
        return result;
      }
    };
  }

  private static final double[] ZETA_TABLE = { //
      Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.6449340668482264, //
      1.2020569031595943, 1.0823232337111382, 1.0369277551433699, //
      1.0173430619844491, 1.0083492773819228, 1.0040773561979443, //
      1.0020083928260822, 1.0009945751278181, 1.0004941886041195, //
      1.0002460865533080, 1.0001227133475785, 1.0000612481350587, //
      1.0000305882363070};

  private static final double zeta(final int n) {
    if (n < ZETA_TABLE.length) {
      return ZETA_TABLE[n];
    } else {
      final int kmax = (int) (Math.pow(10, 20. / n) + 1.0);
      double sum = 0.0;
      for (int k = 1; k <= kmax; ++k) {
        sum += Math.pow(1.0 / k, n);
      }
      return sum;
    }
  }

  private static final class SequentialDoubleArray {

    private final int myMaxLen;

    private double[] myMemory;
    private int myEffectiveLen;

    SequentialDoubleArray(final int maxLength) {
      myMaxLen = maxLength;
      myMemory = new double[0];
      myEffectiveLen = 0;
    }

    final int effectiveLength() {
      return myEffectiveLen;
    }

    final double get(int index) {
      if (index >= myEffectiveLen) {
        throw new IllegalArgumentException(
            "The element with index " + index + " has not been assigned yet.");
      }

      return myMemory[index];
    }

    final void add(double value) {
      final int index = myEffectiveLen;

      if (index >= myMaxLen) {
        throw new IllegalArgumentException("Index " + index + " >= capacity " + myMaxLen + ".");
      }

      if (index >= myMemory.length) {
        int newLength = Math.max(1, myMemory.length);
        while (newLength <= index) {
          newLength = Math.min(myMaxLen, newLength << 1);
        }
        final double[] newMemory = new double[newLength];
        System.arraycopy(myMemory, 0, newMemory, 0, myMemory.length);
        myMemory = newMemory;
      }

      myMemory[index] = value;
      myEffectiveLen = Math.max(myEffectiveLen, index + 1);
    }
  }

  private Sequences() {}
}
