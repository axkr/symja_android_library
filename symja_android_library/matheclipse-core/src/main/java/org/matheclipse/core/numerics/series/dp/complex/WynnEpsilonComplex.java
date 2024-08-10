package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * Implements the Wynn Epsilon algorithm and its variants for convergence of infinite series.
 * Fundamentally, this implementation contains several methods for implementing the Shanks transform
 * efficiently. There are several implementations:
 * <ol>
 * <li>the classic moving lozenge technique due to Wynn [1, 2]</li>
 * <li>the Shanks transform, implemented using the G-transform with a certain remainder sequence
 * [3]</li>
 * <li>the one-parameter generalized epsilon transform of [4]</li>
 * <li>the alternating epsilon transform of [5]</li>
 * </ol>
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Wynn, Peter. "On a device for computing the e m (S n) transformation." Mathematical
 * Tables and Other Aids to Computation (1956): 91-96.</li>
 * <li>[2] Weniger, Ernst Joachim. "Nonlinear sequence transformations for the acceleration of
 * convergence and the summation of divergent series." arXiv preprint math/0306302 (2003).</li>
 * <li>[3] Varga, R.S. Extrapolation methods: theory and practice. Numer Algor 4, 305 (1993).
 * https://doi.org/10.1007/BF02144109</li>
 * <li>[4] Broeck, Jean-Marc Vanden, and Leonard W. Schwartz. "A one-parameter family of sequence
 * transformations." SIAM Journal on Mathematical Analysis 10.3 (1979): 658-666.</li>
 * <li>[5] Barber, Michael N., and C. J. Hamer. "Extrapolation of sequences using a generalized
 * epsilon-algorithm." The ANZIAM Journal 23.3 (1982): 229-240.</li>
 * </ul>
 * </p>
 */
public final class WynnEpsilonComplex extends SeriesAlgorithmComplex {

  public static enum ShanksMethod {
    WYNN, SHANKS, GENERALIZED, ALTERNATING
  }

  private final ShanksMethod myMethod;
  private final double myAlpha;

  private Complex myTerm;
  private final Complex[] myTab;
  private final Complex[] myTab2;
  private final Complex[][] myEps;
  private final Complex[][] myF;

  /**
   * Creates a new instance of the Shanks transform with customizable method.
   * 
   * @param tolerance the smallest acceptable change in series evaluation in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxIters the maximum number of sequence terms to evaluate before giving up
   * @param patience the number of consecutive iterations required for the tolerance criterion to be
   *        satisfied to stop the algorithm
   * @param method the method to use in computing the Shanks transform
   * @param alpha the parameter alpha to use for the one-parameter epsilon transform; ignored if
   *        method is not set appropriately
   */
  public WynnEpsilonComplex(final double tolerance, final int maxIters, final int patience,
      final ShanksMethod method, final double alpha) {
    super(tolerance, maxIters, patience);
    myMethod = method;
    myAlpha = alpha;

    switch (method) {
      case SHANKS:
        myTab = new Complex[maxIters];
        myTab2 = new Complex[maxIters];
        myEps = myF = null;
        break;
      case GENERALIZED:
      case ALTERNATING:
        myTab = myTab2 = null;
        myEps = new Complex[2][maxIters];
        myF = new Complex[2][maxIters];
        break;
      case WYNN:
      default:
        myTab = new Complex[maxIters];
        myTab2 = null;
        myEps = myF = null;
        break;
    }
  }

  /**
   * Creates a new instance of the Shanks transform with customizable method.
   * 
   * @param tolerance the smallest acceptable change in series evaluation in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxIters the maximum number of sequence terms to evaluate before giving up
   * @param patience the number of consecutive iterations required for the tolerance criterion to be
   *        satisfied to stop the algorithm
   * @param method the method to use in computing the Shanks transform
   */
  public WynnEpsilonComplex(final double tolerance, final int maxIters, final int patience,
      final ShanksMethod method) {
    this(tolerance, maxIters, patience, method, -1.0);
  }

  public WynnEpsilonComplex(final double tolerance, final int maxIters, final int patience) {
    this(tolerance, maxIters, patience, ShanksMethod.WYNN);
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {
    switch (myMethod) {
      case SHANKS:
        return nextGTransform(e, term);
      case GENERALIZED:
      case ALTERNATING:
        return nextAlternatingEpsilon(e, term);
      case WYNN:
      default:
        return nextWynnEpsilon(e, term);
    }
  }

  private final Complex nextAlternatingEpsilon(final Complex e, final Complex term) {

    // i is the index of old diagonal, j is new
    final int i = myIndex & 1;
    final int j = (i + 1) & 1;
    final Complex[] eps0 = myEps[i], eps1 = myEps[j];
    final Complex[] f0 = myF[i], f1 = myF[j];

    // compute the new diagonals of the epsilon and f tables
    Complex result = f1[0] = term;
    for (int k = 0; k < myIndex; ++k) {
      if ((k & 1) == 0) {

        // finite difference for epsilon values
        final int idx = k >> 1;
        final Complex diff = f1[idx].subtract(f0[idx]);
        if (diff.norm() < TINY) {
          return Complex.NaN;
        }

        // determine the parameter alpha
        final double alpha;
        if (myMethod == ShanksMethod.ALTERNATING) {
          alpha = -1.0 * (idx & 1);
        } else {
          alpha = myAlpha;
        }

        // update epsilon
        if (idx == 0) {
          eps1[idx] = diff.reciprocal();
        } else {
          eps1[idx] = eps0[idx - 1].multiply(alpha).add(diff.reciprocal());
        }
      } else {

        // finite difference for f values
        final int idx = (k >> 1) + 1;
        final Complex diff = eps1[idx - 1].subtract(eps0[idx - 1]);
        if (diff.norm() < TINY) {
          return Complex.NaN;
        }

        // update f
        result = f1[idx] = f0[idx - 1].add(diff.reciprocal());
      }
    }
    ++myIndex;
    return result;
  }

  private final Complex nextWynnEpsilon(final Complex e, final Complex term) {

    // initialization
    myTab[myIndex] = term;
    if (myIndex == 0) {
      ++myIndex;
      return term;
    }

    // main loop of Wynn Epsilon algorithm
    Complex aux = Complex.ZERO;
    for (int j = myIndex; j >= 1; --j) {

      // compute the next epsilon
      final Complex temp = aux;
      aux = myTab[j - 1];
      Complex diff = myTab[j].subtract(aux);

      // correct denominators for underflow
      if (diff.norm() <= TINY) {
        diff = new Complex(HUGE, HUGE);
      } else {

        diff = temp.add(diff.reciprocal());
      }
      myTab[j - 1] = diff;
    }

    // prepare result
    final Complex result = myTab[myIndex & 1];
    ++myIndex;
    return result;
  }

  private final Complex nextGTransform(final Complex e, final Complex term) {

    // wait to compute series difference
    if (myIndex == 0) {
      ++myIndex;
      myTerm = term;
      return term;
    }

    // initialize table on the first element and difference
    if (myIndex == 1) {
      myTab2[0] = term.subtract(myTerm);
      myTab[0] = myTerm;
      ++myIndex;
      myTerm = term;
      return term;
    }

    // process the next element
    ++myIndex;
    final int idx = Math.min(myIndex - 1, myMaxIters) - 1;
    Complex tmp1 = term.subtract(myTerm);
    Complex tmp2 = Complex.ZERO, tmp3 = Complex.ZERO;
    Complex extrap = myTerm;
    for (int i = 0; i <= idx - 1; ++i) {
      if (myTab2[i].norm() < TINY) {
        myTerm = term;
        return Complex.NaN;
      }
      final Complex rs = tmp1.divide(myTab2[i]).subtract(1.0);
      final Complex tmp0;
      if (i == 0) {
        tmp0 = rs;
      } else {
        tmp0 = rs.multiply(myTab2[i - 1]);
      }
      if ((i & 1) == 0) {
        if (rs.norm() < TINY) {
          myTerm = term;
          return Complex.NaN;
        }
        if (i == 0) {
          tmp3 = myTab[0].subtract(extrap.subtract(myTab[0])).divide(rs);
          myTab[0] = extrap;
          if (myIndex <= 4) {
            extrap = tmp3;
          }
        } else {
          extrap = myTab[i - 1].subtract(myTab[i].subtract(myTab[i - 1])).divide(rs);
          myTab[i - 1] = myTab[i];
          myTab[i] = tmp3;
          tmp3 = extrap;
        }
      }
      if (i > 0) {
        myTab2[i - 1] = tmp2;
      }
      tmp2 = tmp1;
      tmp1 = tmp0;
    }
    myTab2[idx - 1] = tmp2;
    if (idx < myMaxIters) {
      myTab2[idx] = tmp1;
    }
    myTab[idx] = extrap;
    myTerm = term;
    return extrap;
  }

  @Override
  public final String getName() {
    switch (myMethod) {
      case SHANKS:
        return "Shanks";
      case GENERALIZED:
        return "Gen. Epsilon(" + myAlpha + ")";
      case ALTERNATING:
        return "Alt. Epsilon";
      case WYNN:
      default:
        return "Wynn Epsilon";
    }
  }
}
