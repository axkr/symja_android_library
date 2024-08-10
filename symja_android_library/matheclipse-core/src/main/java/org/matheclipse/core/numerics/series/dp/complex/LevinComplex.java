package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * Implements the family of Generalized Levin algorithms for convergence of infinite series based on
 * remainder sequences described in [1, 2] and as outlined in [3].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Levin, David. "Development of non-linear transformations for improving convergence of
 * sequences." International Journal of Computer Mathematics 3.1-4 (1972): 371-388.</li>
 * <li>[2] Smith, David A., and William F. Ford. "Acceleration of linear and logarithmic
 * convergence." SIAM Journal on Numerical Analysis 16.2 (1979): 223-240.</li>
 * <li>[3] Weniger, Ernst Joachim. "Nonlinear sequence transformations for the acceleration of
 * convergence and the summation of divergent series." arXiv preprint math/0306302 (2003).</li>
 * </ul>
 * </p>
 */
public final class LevinComplex extends SeriesAlgorithmComplex {

  /**
   * The type of remainder sequence to assume when accelerating the convergence of a series.
   */
  public static enum RemainderSequence {
    T, D, U, V
  }

  private final String prefix;
  private final double myBeta;
  private final RemainderSequence myRemainder;

  private Complex myPrevE, myPrevTerm;
  private final Complex[] myTabHi, myTabLo;

  /**
   * Creates a new instance of the Generalized Levin algorithm with customizable remainder
   * sequences.
   * 
   * @param tolerance the smallest acceptable change in series evaluation in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxIters the maximum number of sequence terms to evaluate before giving up
   * @param patience the number of consecutive iterations required for the tolerance criterion to be
   *        satisfied to stop the algorithm
   * @param beta the constant described in [1, 3] for the Levin u-transform remainder sequence: if
   *        this sequence is not selected this argument is ignored
   * @param remainder the type of assumed remainder sequence as described in [3]
   */
  public LevinComplex(final double tolerance, final int maxIters, final int patience,
      final double beta, final RemainderSequence remainder) {
    super(tolerance, maxIters, patience);
    myBeta = beta;
    myRemainder = remainder;
    prefix = remainder.name();

    myTabHi = new Complex[maxIters];
    myTabLo = new Complex[maxIters];
  }

  /**
   * Creates a new instance of the Generalized Levin algorithm with customizable remainder
   * sequences.
   * 
   * @param tolerance the smallest acceptable change in series evaluation in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxIters the maximum number of sequence terms to evaluate before giving up
   * @param patience the number of consecutive iterations required for the tolerance criterion to be
   *        satisfied to stop the algorithm
   * @param remainder the type of assumed remainder sequence as described in [3]
   */
  public LevinComplex(final double tolerance, final int maxIters, final int patience,
      final RemainderSequence remainder) {
    this(tolerance, maxIters, patience, 1.0, remainder);
  }

  public LevinComplex(final double tolerance, final int maxIters, final int patience) {
    this(tolerance, maxIters, patience, RemainderSequence.U);
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {

    // shift over elements by one by skipping first iteration so that
    // the iteration is actually computing the term one over from the
    // one used in the current computation
    if (myIndex == 0) {
      myPrevE = e;
      myPrevTerm = term;
      if (myRemainder == RemainderSequence.V || myRemainder == RemainderSequence.D) {
        ++myIndex;
        return myPrevTerm;
      }
    }

    // compute the remainder term
    final int k;
    final Complex rem;
    final Complex currentTerm;
    switch (myRemainder) {
      case T:
        k = myIndex;
        rem = e;
        currentTerm = term;
        break;
      case U:
        k = myIndex;
        rem = e.multiply(myBeta + k);
        currentTerm = term;
        break;
      case V:
        k = myIndex - 1;
        rem = myPrevE.multiply(e).divide(myPrevE.subtract(e));
        currentTerm = myPrevTerm;
        break;
      case D:
        k = myIndex - 1;
        rem = e;
        currentTerm = myPrevTerm;
        break;
      default:
        return Complex.NaN;
    }
    if (rem.norm() < TINY) {
      return Complex.NaN;
    }

    // update table
    myTabHi[k] = currentTerm.divide(rem);
    myTabLo[k] = rem.reciprocal();
    if (k > 0) {
      myTabHi[k - 1] = myTabHi[k].subtract(myTabHi[k - 1]);
      myTabLo[k - 1] = myTabLo[k].subtract(myTabLo[k - 1]);
      if (k > 1) {
        final double base = (myBeta + k - 1) / (myBeta + k);
        for (int j = 2; j <= k; ++j) {
          final int kmj = k - j;
          final double cjm2 = SimpleMath.pow(base, j - 2);
          final double fac = (myBeta + kmj) * cjm2 / (myBeta + k);
          myTabHi[kmj] = myTabHi[kmj + 1].subtract(myTabHi[kmj].multiply(fac));
          myTabLo[kmj] = myTabLo[kmj + 1].subtract(myTabLo[kmj].multiply(fac));
        }
      }
    }

    // increment counters and temporaries
    myPrevE = e;
    myPrevTerm = term;
    ++myIndex;

    // correct for underflow
    if (myTabLo[0].norm() < TINY) {
      return new Complex(HUGE, HUGE);
    } else {
      return myTabHi[0].divide(myTabLo[0]);
    }
  }

  @Override
  public final String getName() {
    return "Levin" + " " + prefix;
  }
}
