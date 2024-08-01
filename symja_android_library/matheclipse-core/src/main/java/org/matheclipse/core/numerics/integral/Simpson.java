package org.matheclipse.core.numerics.integral;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.DoubleUnaryOperator;

/**
 * A local and global adaptive numerical integrator based on Simpson's rule [1]. For all
 * implementations, we use the Lyness-Richardson criterion to decide when to stop subdividing an
 * interval [2]. There are two versions:
 * <ol>
 * <li>the local error estimation version (SQUANK) bisects the integration region recursively, until
 * the request error is reached on each interval [3]</li>
 * <li>the global error estimation version bisects the integration region as in the local version,
 * but maintains instead a global estimate of the error; subintervals are stored in a priority queue
 * and the intervals with the largest contribution to the error are chosen at each iteration for
 * further subdivision[4]</li>
 * </ol>
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Guy F. Kuncir. 1962. Algorithm 103: Simpson's rule integrator. Commun. ACM 5, 6 (June
 * 1962), 347. DOI:https://doi.org/10.1145/367766.368179</li>
 * <li>[2] Lyness, James N. "Notes on the adaptive Simpson quadrature routine." Journal of the ACM
 * (JACM) 16.3 (1969): 483-495.</li>
 * <li>[3] J. N. Lyness. 1970. Algorithm 379: Squank (Simpson Quadrature used adaptivity�noise
 * killed) [D1]. Commun. ACM 13, 4 (April 1970), 260�262.
 * DOI:https://doi.org/10.1145/362258.362289</li>
 * <li>[4] Michael A. Malcolm and R. Bruce Simpson. 1975. Local Versus Global Strategies for
 * Adaptive Quadrature. ACM Trans. Math. Softw. 1, 2 (June 1975), 129�146.
 * DOI:https://doi.org/10.1145/355637.355640</li>
 * </ul>
 * </p>
 */
public final class Simpson extends Quadrature {

  /**
   * Sorts intervals in descending order according to their estimated error.
   */
  private final class IntervalErrorComparator implements Comparator<double[]> {

    @Override
    public int compare(double[] o1, double[] o2) {
      final int n = o1.length;
      return Double.compare(o2[n - 1], o1[n - 1]);
    }
  }

  /**
   * The type of error estimation and subdivision method to use for adaptive integration.
   */
  public static enum SimpsonAdaptationType {
    LOCAL, GLOBAL
  }

  private final SimpsonAdaptationType myMethod;

  /**
   * Creates a new instance of a local or global adaptive Simpson integrator.
   * 
   * @param tolerance the smallest acceptable change in integral estimates in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of evaluations of each function permitted
   * @param method the type of adaptation to use (e.g. local or global) for subdividing intervals
   */
  public Simpson(final double tolerance, final int maxEvaluations,
      final SimpsonAdaptationType method) {
    super(tolerance, maxEvaluations);
    myMethod = method;
  }

  public Simpson(final double tolerance, final int maxEvaluations) {
    this(tolerance, maxEvaluations, SimpsonAdaptationType.GLOBAL);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {
    switch (myMethod) {
      case GLOBAL:
        return globalSimpson(f, a, b);
      case LOCAL:
        return localSimpson(f, a, b);
      default:
        return new QuadratureResult(Double.NaN, Double.NaN, 0, false);
    }
  }

  @Override
  public final String getName() {
    return myMethod.toString() + "-Simpson";
  }

  private static final double[] simpson13(final DoubleUnaryOperator func, final double a,
      final double mid, final double b, final double fa, final double fmid, final double fb) {
    final double lmid = 0.5 * (a + mid);
    final double rmid = 0.5 * (mid + b);
    final double flmid = func.applyAsDouble(lmid);
    final double frmid = func.applyAsDouble(rmid);
    final double h = b - a;
    final double crude = (h / 6) * (fa + 4 * fmid + fb);
    final double fine = (h / 12) * (fa + 4 * flmid + 2 * fmid + 4 * frmid + fb);
    final double est = fine;
    final double error = Math.abs(fine - crude) / 15;
    final double[] result = {a, lmid, mid, rmid, b, fa, flmid, fmid, frmid, fb, est, error};
    return result;
  }

  private final QuadratureResult globalSimpson(final DoubleUnaryOperator func, final double a,
      final double b) {

    // estimate the error on [a, b] using Lyness'-Richardson
    final double m0 = 0.5 * (a + b);
    final double fa0 = func.applyAsDouble(a);
    final double fm0 = func.applyAsDouble(m0);
    final double fb0 = func.applyAsDouble(b);
    double[] interval = simpson13(func, a, m0, b, fa0, fm0, fb0);
    double est = interval[10];
    double error = interval[11];
    int fev = 5;

    // make the stack, and push the interval onto it
    final AbstractQueue<double[]> queue = new PriorityQueue<>(100, new IntervalErrorComparator());
    queue.add(interval);

    // unrolled version of the adaptive Simpson method
    while (!queue.isEmpty()) {

      // retrieve the interval with the largest error
      interval = queue.poll();
      final double a1 = interval[0];
      final double lm1 = interval[1];
      final double m1 = interval[2];
      final double rm1 = interval[3];
      final double b1 = interval[4];
      final double fa1 = interval[5];
      final double flm1 = interval[6];
      final double fm1 = interval[7];
      final double frm1 = interval[8];
      final double fb1 = interval[9];
      final double area1 = interval[10];
      final double error1 = interval[11];

      // very small intervals are not subdivided
      if (Math.abs(b1 - a1) == 0.0) {
        continue;
      }

      // estimate the error on [a, mid] using Lyness'-Richardson
      final double[] linterval = simpson13(func, a1, lm1, m1, fa1, flm1, fm1);
      final double lest = linterval[10];
      final double lerror = linterval[11];
      queue.add(linterval);

      // estimate the error on [mid, b] using Lyness'-Richardson
      final double[] rinterval = simpson13(func, m1, rm1, b1, fm1, frm1, fb1);
      final double rest = rinterval[10];
      final double rerror = rinterval[11];
      queue.add(rinterval);

      // update global estimate of the integral and error
      est = est - area1 + lest + rest;
      error = error - error1 + lerror + rerror;
      fev += 4;

      // check for budget reached or invalid value
      if (fev >= myMaxEvals || !Double.isFinite(est)) {
        return new QuadratureResult(est, error, fev, false);
      }

      // check for tolerance level reached
      if (error < myTol && fev > 9) {
        break;
      }
    }

    // reached the final requested tolerance
    return new QuadratureResult(est, error, fev, true);
  }

  private final QuadratureResult localSimpson(final DoubleUnaryOperator func, final double a,
      final double b) {

    // initialize the first interval
    final double m0 = 0.5 * (a + b);
    final double fa = func.applyAsDouble(a);
    final double fm = func.applyAsDouble(m0);
    final double fb = func.applyAsDouble(b);
    int fev = 3;
    double est = 0.0;

    // make the stack, and push the interval onto it
    final Stack<double[]> stack = new Stack<>();
    double[] interval = {a, m0, b, fa, fm, fb, myTol};
    stack.push(interval);

    // unrolled version of the adaptive Simpson method
    while (!stack.isEmpty()) {

      // retrieve the top interval off the stack
      interval = stack.pop();
      final double a1 = interval[0];
      final double m1 = interval[1];
      final double b1 = interval[2];
      final double fa1 = interval[3];
      final double fm1 = interval[4];
      final double fb1 = interval[5];
      final double eps = interval[6];

      // estimate the error using Lyness'-Richardson
      final double wid = b1 - a1;
      final double lmid = 0.5 * (a1 + m1);
      final double rmid = 0.5 * (m1 + b1);
      final double flmid = func.applyAsDouble(lmid);
      final double frmid = func.applyAsDouble(rmid);
      fev += 2;
      final double crude = (fa1 + 4.0 * fm1 + fb1) * (wid / 6.0);
      final double left = (fa1 + 4.0 * flmid + fm1) * (wid / 6.0);
      final double right = (fb1 + 4.0 * frmid + fm1) * (wid / 6.0);
      final double fine = 0.5 * (left + right);
      final double err = Math.abs(fine - crude);

      // if the error is within the tolerance, update the global integral estimate
      if (fev > 5 && (err <= 15.0 * eps || wid == 0.0 || eps * 2 == eps)) {
        est += fine + (fine - crude) / 15.0;
        continue;
      }

      // check for budget reached or invalid value
      if (fev >= myMaxEvals || !Double.isFinite(est)) {
        return new QuadratureResult(est, Double.NaN, fev, false);
      }

      // push the subdivided interval onto the stack
      final double[] lint = {a1, lmid, m1, fa1, flmid, fm1, eps / 2};
      stack.push(lint);
      final double[] rint = {m1, rmid, b1, fm1, frmid, fb1, eps / 2};
      stack.push(rint);
    }

    // reached the final requested tolerance on all subdivisions
    return new QuadratureResult(est, Double.NaN, fev, true);
  }
}
