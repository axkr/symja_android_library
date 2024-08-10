package org.matheclipse.core.numerics.series.dp.complex;

import java.util.Iterator;
import java.util.function.Function;
import org.hipparchus.complex.Complex;

/**
 * The base class of all convergence acceleration algorithms. Provides functionality for evaluating
 * infinite series and limits of sequences of complex values.
 */
public abstract class SeriesAlgorithmComplex {


  public static final class SeriesSolutionComplex {

    public final Complex limit;
    public final double error;
    public final int evaluations;
    public final boolean converged;

    public SeriesSolutionComplex(final Complex est, final double err, final int evals,
        final boolean success) {
      limit = est;
      error = err;
      evaluations = evals;
      converged = success;
    }

    @Override
    public String toString() {
      return String.format("%.08f", limit) + " +- " + String.format("%.08f", error) + "\n"
          + "evaluations: " + evaluations + "\n" + "converged: " + converged;
    }
  }

  protected static final double HUGE = 1e60;
  protected static final double TINY = 1e-60;

  protected final double myTol;
  protected final int myMaxIters;
  protected final int myPatience;

  protected int myIndex;

  /**
   * Creates a new instance of the current convergence acceleration algorithm.
   * 
   * @param tolerance the smallest acceptable change in series evaluation in consecutive iterations
   *        that indicates the algorithm has converged
   * @param patience the number of consecutive iterations required for the tolerance criterion to be
   *        satisfied to stop the algorithm
   * @param maxIters the maximum number of sequence terms to evaluate before giving up
   */
  public SeriesAlgorithmComplex(final double tolerance, final int maxIters, final int patience) {
    myTol = tolerance;
    myMaxIters = maxIters;
    myPatience = patience;
  }

  /**
   * Update the current algorithm after observing the specified element and partial sum.
   * 
   * @param e the next element of the sequence
   * @param term the partial sum of all observed elements of the sequence thus far
   * @return the next estimate of the limit of the sequence
   */
  public abstract Complex next(Complex e, Complex term);

  /**
   * Returns a string representation of the current algorithm.
   * 
   * @return a string representation of the current algorithm
   */
  public abstract String getName();

  // ==========================================================================
  // LIMITS
  // ==========================================================================
  /**
   * Given a sequence represented as an Iterable object, numerically evaluates the limit of the
   * sequence or the corresponding series.
   * 
   * @param seq an Iterable object of type Complex whose limit to evaluate
   * @param series a boolean variable indicating whether to evaluate the limit of the series or the
   *        sequence
   * @param extrapolateStart the number of terms to observe (and sum trivially) before starting
   *        extrapolation
   * @return a Double that approximates the limit of the sequence or corresponding series. If the
   *         limit cannot be determined, returns NaN
   */
  public SeriesSolutionComplex limit(final Iterable<Complex> seq, final boolean series) {
    return limit(seq, series, 0);
  }

  public SeriesSolutionComplex limit(final Iterable<Complex> seq, final boolean series,
      final int extrapolateStart) {
    myIndex = 0;
    int convergeSteps = 0;
    int indexBeforeExtrap = 0;
    int evals = 0;
    Complex partial = Complex.ZERO;
    Complex term = Complex.ZERO;
    Complex est = Complex.NaN;

    for (final Complex e : seq) {
      ++evals;

      // check if extrapolation starts
      if (indexBeforeExtrap < extrapolateStart) {
        if (series) {
          partial = partial.add(e);
        }
        ++indexBeforeExtrap;
        continue;
      }

      // get the next term of the sequence
      if (series) {
        term = term.add(e);
      } else {
        term = e;
      }

      // estimate the next term
      final Complex oldest = est;
      est = next(e, term);
      if (est.isNaN()) {
        break;
      }

      // monitor convergence
      if (myIndex >= 2) {
        final double error = oldest.subtract(est).norm();
        if (error <= myTol && est.getReal() != HUGE && est.getImaginary() != HUGE) {
          ++convergeSteps;
        } else {
          convergeSteps = 0;
        }
        if (convergeSteps >= myPatience) {
          return new SeriesSolutionComplex(est.add(partial), error, evals, true);
        }
        if (myIndex >= myMaxIters || !Double.isFinite(error)) {
          break;
        }
      }
    }

    // did not achieve the desired error
    return new SeriesSolutionComplex(est.add(partial), Double.NaN, evals, false);
  }

  /**
   * Accelerates a sequence or series according to the transformation of the current instance, and
   * returns the transformed series.
   * 
   * @param seq an Iterable object of type Double to transform
   * @param series a boolean variable indicating whether to evaluate the limit of the series or the
   *        sequence
   * @return an Iterable of type Double representing the transformed series or sequence
   */
  public Iterable<Complex> transform(final Iterable<Complex> seq, final boolean series) {
    return () -> new Iterator<>() {

      private final Iterator<Complex> it = seq.iterator();

      private Complex term = Complex.ZERO;
      private int index = 0;

      @Override
      public final boolean hasNext() {
        return index < SeriesAlgorithmComplex.this.myMaxIters && it.hasNext();
      }

      @Override
      public final Complex next() {
        final Complex e = it.next();
        if (series) {
          term = term.add(e);
        } else {
          term = e;
        }
        ++index;
        return SeriesAlgorithmComplex.this.next(e, term);
      }
    };
  }

  // ==========================================================================
  // TRANSLATION OR REPRESENTATION OF SEQUENCES/SERIES
  // ==========================================================================
  /**
   * The Van Wijngaarden transformation converts a convergent sequence of positive terms into a
   * sequence of alternating terms that has the same limit. This trick is incredibly useful for
   * accelerating convergence of slowly-increasing sequences of positive terms, because alternating
   * series are generally easier to accelerate.
   * 
   * <p>
   * Formally, given a sequence of terms a(1), a(2)... converts this into an alternating sequence
   * where the kth term is
   * <p>
   * a(k) + 2a(2k) + 4a(4k) + ...
   * </p>
   * This method uses the current algorithm's implementation to evaluate the limit of the above
   * expression. The resulting series are evalated lazily and represented internally as a function.
   * </p>
   * 
   * <p>
   * References:
   * <ul>
   * <li>[1] William H. Press, Saul A. Teukolsky, William T. Vetterling, and Brian P. Flannery.
   * 2007. Numerical Recipes 3rd Edition: The Art of Scientific Computing (3rd. ed.). Cambridge
   * University Press, USA.</li>
   * </ul>
   * </p>
   * 
   * @param seq a sequence of non-negative terms represented as a Function
   * @param start a long representing the starting index at which the sequence begins
   * @return a Function representing the resulting alternating sequence obtained with Van
   *         Wijngaarden's transformation
   */
  public Function<Long, Complex> toAlternatingSeries(final Function<? super Long, Complex> func,
      final long start) {
    return (k) -> {

      // create the condensation sequence with kth term 2^k a(2^k)
      final Iterable<Complex> condensed = () -> new Iterator<>() {

        private long i = k;
        private double coeff = 1.0;

        @Override
        public final boolean hasNext() {
          return i <= (Long.MAX_VALUE >> 1L) && coeff <= (Double.MAX_VALUE / 2.0);
        }

        @Override
        public final Complex next() {
          final Complex term = func.apply(i + start - 1);
          final Complex result = term.multiply(coeff);
          i <<= 1L;
          coeff *= 2.0;
          return result;
        }
      };

      // determine the next term as the infinite series of this sequence
      final SeriesSolutionComplex sol = limit(condensed, true);
      final Complex term = sol.limit;
      if (((k - 1) & 1L) == 0) {
        return term;
      } else {
        return term.negate();
      }
    };
  }
}
