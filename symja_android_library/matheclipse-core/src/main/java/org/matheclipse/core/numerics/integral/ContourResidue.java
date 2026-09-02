package org.matheclipse.core.numerics.integral;

import java.util.ArrayList;
import java.util.List;

/**
 * Numerical residue of a complex function by contour integration with the periodic trapezoidal
 * rule.
 *
 * <p>
 * The residue of <code>f</code> at <code>z0</code> - the coefficient of <code>(z-z0)^(-1)</code> in
 * the Laurent expansion of <code>f</code> around <code>z0</code> - is the Cauchy integral over a
 * circle of radius <code>r</code> around <code>z0</code>, discretized on <code>n</code> equally
 * spaced nodes:
 *
 * <pre>
 * Res(f, z0) = 1/(2*Pi*I) * Integrate(f, z)
 *            = r/n * Sum(f(z0 + r*E^(I*theta_k)) * E^(I*theta_k), {k, 0, n-1})
 * </pre>
 *
 * with <code>theta_k = 2*Pi*k/n</code>. The integrand is <code>2*Pi</code> periodic and analytic in
 * <code>theta</code>, so the trapezoidal rule converges geometrically and no adaptive subdivision
 * is needed - the sample count is simply doubled until the estimates agree. Unlike a Laurent series
 * this needs no expansion at all, which is why it also works for essential singularities like
 * <code>Exp(1/z)</code>.
 *
 * <p>
 * The algorithm is written once against the {@link Ops} abstraction so that the machine-precision
 * and the arbitrary-precision variants share it.
 *
 * <p>
 * References:
 * <ul>
 * <li>[1] Trefethen, Lloyd N., and J. A. C. Weideman. "The exponentially convergent trapezoidal
 * rule." SIAM Review 56.3 (2014): 385-458.</li>
 * <li>[2] Fornberg, Bengt. "Numerical differentiation of analytic functions." ACM Transactions on
 * Mathematical Software 7.4 (1981): 512-526.</li>
 * <li>[3] Bornemann, Folkmar. "Accuracy and stability of computing high-order derivatives of
 * analytic functions by Cauchy integrals." Foundations of Computational Mathematics 11.1 (2011):
 * 1-63.</li>
 * </ul>
 */
public final class ContourResidue {

  /** Number of sample points the first trapezoidal estimate uses. */
  private static final int INITIAL_SAMPLE_COUNT = 16;

  /**
   * Error-decay ratio above which the doubling errors are deemed non-geometric. A function that is
   * analytic on the contour makes the error fall off much faster than this.
   */
  private static final double BRANCH_CUT_DECAY = 0.7;

  /**
   * Largest relative jump between adjacent samples a smooth integrand may show at the final (and
   * therefore finest) sample count. A branch cut crossing the contour keeps an O(1) jump no matter
   * how fine the mesh gets.
   */
  private static final double BRANCH_CUT_REL_JUMP = 0.5;

  /** Number of radii the adaptive search may try before giving up. */
  private static final int MAX_RADIUS_STEPS = 12;

  /** Radius the adaptive search starts from. */
  private static final double INITIAL_RADIUS = 1.0;

  /** Factor the adaptive search multiplies/divides the radius with. */
  private static final double INITIAL_RADIUS_STEP = 1.6;

  /** Once the step factor drops below this the adaptive search stops refining. */
  private static final double MIN_RADIUS_STEP = 1.05;

  /**
   * Number of successive doublings that may fail to reduce the error before the run is abandoned.
   * Once the geometric convergence has run into the rounding noise floor the estimate doesn't get
   * any better, and every further doubling costs twice as many integrand evaluations as the last.
   */
  private static final int MAX_STAGNANT_STEPS = 2;

  /** How the computation ended. */
  public static enum Status {
    /** The doubling error met the precision goal. */
    OK,
    /** The precision goal wasn't met, but the integrand looks analytic on the contour. */
    NO_CONVERGENCE,
    /** The integrand doesn't look analytic on the contour - probably a branch cut crosses it. */
    BRANCH_CUT,
    /** The integrand couldn't be evaluated to a number on the contour. */
    NON_NUMERIC
  }

  /**
   * Evaluates the integrand at a point of the complex plane.
   *
   * @param <T> the complex number representation
   */
  @FunctionalInterface
  public static interface Sampler<T> {
    /**
     * @param z the sample point
     * @return the value of the integrand or <code>null</code> if it has no numerical value there
     */
    T value(T z);
  }

  /**
   * The complex arithmetic the contour sum is accumulated in. An instance is tied to one contour
   * radius.
   *
   * @param <T> the complex number representation
   */
  public static interface Ops<T> {

    T add(T a, T b);

    T subtract(T a, T b);

    T multiply(T a, T b);

    T divide(T a, T b);

    /**
     * @return the sample point <code>z0 + r*E^(2*Pi*I*k/n)</code> on the contour
     */
    T node(T z0, int k, int n);

    /**
     * @return the phase factor <code>E^(2*Pi*I*k/n)</code>
     */
    T phase(int k, int n);

    /**
     * Applies the quadrature weight <code>r/n</code> to a raw trapezoidal sum.
     */
    T weight(T sum, int n);

    double abs(T a);

    boolean isFinite(T a);

    /**
     * @return the radius of the contour this instance integrates over
     */
    double radius();
  }

  /**
   * Creates the arithmetic for one contour radius. Used by
   * {@link ContourResidue#autoRadius(OpsFactory, Sampler, Object, double, int)} which tries several
   * radii.
   *
   * @param <T> the complex number representation
   */
  @FunctionalInterface
  public static interface OpsFactory<T> {
    Ops<T> create(double radius);
  }

  /**
   * The outcome of a contour integration.
   *
   * @param <T> the complex number representation
   */
  public static final class Result<T> {

    /**
     * The estimated residue, or <code>null</code> if {@link #status} is
     * {@link Status#NON_NUMERIC}
     */
    public final T value;

    /** Estimated absolute error of {@link #value} */
    public final double error;

    /** How the computation ended */
    public final Status status;

    /** Number of integrand evaluations that went into {@link #value} */
    public final int evaluations;

    /** The radius of the contour {@link #value} was computed on */
    public final double radius;

    public Result(final T value, final double error, final Status status, final int evaluations,
        final double radius) {
      this.value = value;
      this.error = error;
      this.status = status;
      this.evaluations = evaluations;
      this.radius = radius;
    }

    @Override
    public String toString() {
      return value + " +- " + error + "\n" + "evaluations: " + evaluations + "\n" + "radius: "
          + radius + "\n" + "status: " + status;
    }
  }

  private ContourResidue() {
    // static helper class
  }

  /**
   * Estimates the residue on a contour of the radius the given {@link Ops} was created for.
   *
   * <p>
   * The sample count starts at 16 and is doubled - reusing all previous samples - until the change
   * between two successive estimates meets the precision goal or <code>maxRecursion</code> is
   * exhausted.
   *
   * @param ops the complex arithmetic, carrying the contour radius
   * @param sampler evaluates the integrand
   * @param z0 the point the residue is taken at
   * @param precisionGoalDigits the number of correct decimal digits aimed for
   * @param maxRecursion the maximum number of sample count doublings
   * @return the estimated residue
   */
  public static <T> Result<T> fixedRadius(final Ops<T> ops, final Sampler<T> sampler, final T z0,
      final double precisionGoalDigits, final int maxRecursion) {
    final double radius = ops.radius();
    final double tolerance = Math.pow(10.0, -precisionGoalDigits);

    int n = INITIAL_SAMPLE_COUNT;
    List<T> samples = new ArrayList<T>(n);
    for (int k = 0; k < n; k++) {
      final T value = sample(ops, sampler, ops.node(z0, k, n));
      if (value == null) {
        return new Result<T>(null, Double.POSITIVE_INFINITY, Status.NON_NUMERIC, k + 1, radius);
      }
      samples.add(value);
    }

    // the three most recent trapezoidal estimates, oldest first
    T older = null;
    T old = null;
    T current = trapezoid(ops, samples, n);

    T estimate = current;
    double estimateError = Double.POSITIVE_INFINITY;
    double previousError = Double.POSITIVE_INFINITY;
    double slowestDecay = 1.0;
    int stagnantSteps = 0;
    int doublings = 0;
    Status status = Status.NO_CONVERGENCE;

    for (int iteration = 0; iteration < maxRecursion; iteration++) {
      final int doubled = 2 * n;
      // the old node k sits at angle 2*Pi*k/n == 2*Pi*(2*k)/doubled, so it becomes the new even
      // node 2*k and only the odd nodes in between have to be evaluated
      final List<T> refined = new ArrayList<T>(doubled);
      for (int k = 0; k < n; k++) {
        refined.add(samples.get(k));
        final T value = sample(ops, sampler, ops.node(z0, 2 * k + 1, doubled));
        if (value == null) {
          return new Result<T>(null, Double.POSITIVE_INFINITY, Status.NON_NUMERIC,
              n + k + 1, radius);
        }
        refined.add(value);
      }
      samples = refined;
      n = doubled;
      doublings++;

      final T sum = trapezoid(ops, samples, n);
      final double error = ops.abs(ops.subtract(sum, current));
      stagnantSteps = (error < previousError) ? 0 : stagnantSteps + 1;
      final double decay =
          (previousError > 0.0 && previousError != Double.POSITIVE_INFINITY) ? error / previousError
              : (error > 0.0 ? 1.0 : 0.0);
      if (decay < slowestDecay) {
        slowestDecay = decay;
      }
      previousError = error;

      older = old;
      old = current;
      current = sum;

      // Aitken can only sharpen the value that is returned - never the convergence test below.
      // For a non-geometric (discontinuous) sequence it produces a spuriously small error which
      // would mask a branch cut.
      estimate = sum;
      estimateError = error;
      if (older != null) {
        final double[] acceleratedError = new double[1];
        final T accelerated = aitken(ops, older, old, current, acceleratedError);
        if (acceleratedError[0] < estimateError) {
          estimate = accelerated;
          estimateError = acceleratedError[0];
        }
      }

      // A residue may legitimately be zero, in which case no relative tolerance is ever reached.
      // Scaling by 1+|sum| turns the test into an absolute one in that case.
      if (error < tolerance * (1.0 + ops.abs(current))) {
        status = Status.OK;
        break;
      }
      if (stagnantSteps >= MAX_STAGNANT_STEPS) {
        break;
      }
    }

    if (status != Status.OK) {
      // Without a single doubling neither gauge has anything to look at, so the run can only be
      // reported as unconverged - never as a branch cut.
      status = (doublings > 0 && (maxRelativeJump(ops, samples, n) > BRANCH_CUT_REL_JUMP
          || slowestDecay > BRANCH_CUT_DECAY)) ? Status.BRANCH_CUT : Status.NO_CONVERGENCE;
    }
    return new Result<T>(estimate, estimateError, status, n, radius);
  }

  /**
   * Estimates the residue, choosing the contour radius automatically.
   *
   * <p>
   * A residue doesn't depend on the radius, so any radius that encloses the singularity and no
   * other one is admissible and the search is free to pick whichever converges best. It walks
   * downhill on {@link #penalty(Result)} from <code>r == 1</code>, halving the step exponent
   * whenever neither direction improves.
   *
   * @param factory creates the complex arithmetic for a radius
   * @param sampler evaluates the integrand
   * @param z0 the point the residue is taken at
   * @param precisionGoalDigits the number of correct decimal digits aimed for
   * @param maxRecursion the maximum number of sample count doublings per radius
   * @return the estimated residue of the best radius tried
   */
  public static <T> Result<T> autoRadius(final OpsFactory<T> factory, final Sampler<T> sampler,
      final T z0, final double precisionGoalDigits, final int maxRecursion) {
    double radius = INITIAL_RADIUS;
    double step = INITIAL_RADIUS_STEP;

    Result<T> current =
        fixedRadius(factory.create(radius), sampler, z0, precisionGoalDigits, maxRecursion);
    double currentPenalty = penalty(current);
    Result<T> best = current;
    double bestPenalty = currentPenalty;

    for (int iteration = 0; iteration < MAX_RADIUS_STEPS && step > MIN_RADIUS_STEP; iteration++) {
      if (current.status == Status.OK && current.error == 0.0) {
        // exactly reproduced - no radius can do better
        break;
      }
      final Result<T> larger =
          fixedRadius(factory.create(radius * step), sampler, z0, precisionGoalDigits,
              maxRecursion);
      final Result<T> smaller =
          fixedRadius(factory.create(radius / step), sampler, z0, precisionGoalDigits,
              maxRecursion);
      final double largerPenalty = penalty(larger);
      final double smallerPenalty = penalty(smaller);

      if (largerPenalty < currentPenalty && largerPenalty <= smallerPenalty) {
        radius *= step;
        current = larger;
        currentPenalty = largerPenalty;
      } else if (smallerPenalty < currentPenalty) {
        radius /= step;
        current = smaller;
        currentPenalty = smallerPenalty;
      } else {
        step = Math.sqrt(step);
        continue;
      }

      if (currentPenalty < bestPenalty) {
        best = current;
        bestPenalty = currentPenalty;
      }
    }
    return best;
  }

  /**
   * Rates a finished fixed-radius run for the adaptive radius search, lower being better. Because
   * the residue itself doesn't depend on the radius this only has to reward converged runs that got
   * a small error out of few samples.
   */
  private static <T> double penalty(final Result<T> result) {
    switch (result.status) {
      case OK:
        return Math.log10(result.error + Double.MIN_VALUE) + 1e-4 * result.evaluations;
      case NO_CONVERGENCE:
        return 50.0 + 1e-4 * result.evaluations;
      case BRANCH_CUT:
        return 100.0;
      default:
        return 1000.0;
    }
  }

  /**
   * Evaluates the integrand, mapping a non-finite value onto the <code>null</code> the callers
   * treat as a failure.
   */
  private static <T> T sample(final Ops<T> ops, final Sampler<T> sampler, final T z) {
    final T value = sampler.value(z);
    return (value != null && ops.isFinite(value)) ? value : null;
  }

  /**
   * The trapezoidal residue estimate <code>r/n * Sum(f_k * E^(2*Pi*I*k/n), {k, 0, n-1})</code>.
   */
  private static <T> T trapezoid(final Ops<T> ops, final List<T> samples, final int n) {
    T sum = null;
    for (int k = 0; k < n; k++) {
      final T term = ops.multiply(samples.get(k), ops.phase(k, n));
      sum = (sum == null) ? term : ops.add(sum, term);
    }
    return ops.weight(sum, n);
  }

  /**
   * Aitken's <code>Delta^2</code> (Shanks) acceleration of three successive estimates of a
   * geometric sequence.
   *
   * @param error a one element array the estimated absolute error of the result is written to
   * @return the accelerated estimate, or <code>s2</code> when the second difference is numerically
   *         negligible - the sequence has then either converged already or is noise limited
   */
  private static <T> T aitken(final Ops<T> ops, final T s0, final T s1, final T s2,
      final double[] error) {
    final T d1 = ops.subtract(s1, s0);
    final T d2 = ops.subtract(s2, s1);
    final T denominator = ops.subtract(d2, d1);
    final double absDenominator = ops.abs(denominator);
    if (absDenominator <= Double.MIN_VALUE
        || absDenominator < 1e-12 * (ops.abs(d1) + ops.abs(d2))) {
      error[0] = ops.abs(d2);
      return s2;
    }
    final T accelerated = ops.subtract(s2, ops.divide(ops.multiply(d2, d2), denominator));
    error[0] = ops.abs(ops.subtract(accelerated, s2));
    return accelerated;
  }

  /**
   * The largest relative jump <code>|f(k+1)-f(k)| / (|f(k+1)|+|f(k)|)</code> around the contour. It
   * shrinks with the mesh for a smooth integrand but stays O(1) where a branch cut flips the sign
   * of the integrand, which makes it a robust discontinuity gauge.
   */
  private static <T> double maxRelativeJump(final Ops<T> ops, final List<T> samples, final int n) {
    double max = 0.0;
    for (int k = 0; k < n; k++) {
      final T a = samples.get(k);
      final T b = samples.get((k + 1) % n);
      final double denominator = ops.abs(a) + ops.abs(b);
      if (denominator > 0.0 && Double.isFinite(denominator)) {
        final double jump = ops.abs(ops.subtract(b, a)) / denominator;
        if (jump > max) {
          max = jump;
        }
      }
    }
    return max;
  }
}
