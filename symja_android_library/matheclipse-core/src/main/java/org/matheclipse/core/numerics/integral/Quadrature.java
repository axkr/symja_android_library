package org.matheclipse.core.numerics.integral;

import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * An abstract class for the base of all numerical integrators for 1d functions.
 */
public abstract class Quadrature {

  /**
   * 
   */
  public static final class QuadratureResult {

    public final double estimate;
    public final double error;
    public final int evaluations;
    public final boolean converged;

    public QuadratureResult(final double est, final double err, final int evals,
        final boolean success) {
      estimate = est;
      error = err;
      evaluations = evals;
      converged = success;
    }

    @Override
    public String toString() {
      return String.format("%.08f", estimate) + " +- " + String.format("%.08f", error) + "\n"
          + "evaluations: " + evaluations + "\n" + "converged: " + converged;
    }
  }

  protected final double myTol;
  protected final int myMaxEvals;

  /**
   * Creates a new instance of the current numerical integrator.
   * 
   * @param tolerance the smallest acceptable change in integral estimates in consecutive iterations
   *        that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of evaluations of each function permitted
   */
  public Quadrature(final double tolerance, final int maxEvaluations) {
    myTol = tolerance;
    myMaxEvals = maxEvaluations;
  }

  abstract QuadratureResult properIntegral(DoubleUnaryOperator f, double a, double b);

  /**
   * Returns a string representation of the current algorithm.
   * 
   * @return a string representation of the current algorithm
   */
  public abstract String getName();

  /**
   * Estimates a definite integral, or indefinite integral in some limited cases. All algorithms
   * support definite integrals. The only algorithm that currently supports indefinite integrals is
   * Gauss Kronrod.
   * 
   * @param f the function to integrate
   * @param a the left endpoint of the integration interval
   * @param b the right endpoint of the integration interval
   * @return an estimate of the definite or indefinite integral
   */
  public QuadratureResult integrate(final DoubleUnaryOperator f, final double a, final double b) {

    // empty integral (a, a)
    if (a == b) {
      return new QuadratureResult(0.0, 0.0, 0, true);
    }

    // opposite bounds
    if (a > b) {
      return integrate(f, b, a);
    }

    // finite integral (a, b)
    if (Double.isFinite(a) && Double.isFinite(b)) {
      return properIntegral(f, a, b);
    }

    // improper integral of the form [a, infinity]
    if (Double.isFinite(a) && !Double.isFinite(b)) {
      final DoubleUnaryOperator ft = t -> {
        if (t <= 0.0) {
          return 0.0;
        }
        final double x = a - 1.0 + 1.0 / t;
        final double dx = 1.0 / t / t;
        return f.applyAsDouble(x) * dx;
      };
      return properIntegral(ft, 0.0, 1.0);
    }

    // improper integral of the form [-infinity, b]
    if (!Double.isFinite(a) && Double.isFinite(b)) {
      final DoubleUnaryOperator ft = t -> {
        if (t <= 0.0) {
          return 0.0;
        }
        final double x = b + 1.0 - 1.0 / t;
        final double dx = 1.0 / t / t;
        return f.applyAsDouble(x) * dx;
      };
      return properIntegral(ft, 0.0, 1.0);
    }

    // doubly improper integral
    final QuadratureResult left = integrate(f, a, 0.0);
    final QuadratureResult right = integrate(f, 0.0, b);
    return new QuadratureResult(left.estimate + right.estimate, left.error + right.error,
        left.evaluations + right.evaluations, left.converged && right.converged);
  }

  /**
   * Estimates a definite integral of a function by first applying a change of variable to the
   * function, suitable for estimating indefinite integrals. Given a function to integrate, f, and a
   * differentiable function t, this method integrates f(t(x)) * t'(x).
   * 
   * @param f the function to integrate
   * @param t the change of variable
   * @param dt the derivative of the change of variable function t
   * @param a the left endpoint of the integration interval
   * @param b the right endpoint of the integration interval
   * @return an estimate of the definite integral
   */
  public QuadratureResult integrate(final Function<? super Double, Double> f,
      final Function<? super Double, Double> t, final Function<? super Double, Double> dt,
      final double a, final double b) {

    // construct the integrand f(t(x)) t(x) dt(x)
    final DoubleUnaryOperator func = (x) -> f.apply(t.apply(x)) * dt.apply(x);

    // dispatch to integration routine
    return integrate(func, a, b);
  }

  /**
   * This method splits a potentially indefinite integration interval into disjoint definite
   * intervals. It returns a sequence of integral estimates on each interval. This is very useful
   * for evaluating highly oscillating integrals on infinite intervals, called Longman's method [1].
   * 
   * <p>
   * References:
   * <ul>
   * <li>[1] Longman, I. (1956). Note on a method for computing infinite integrals of oscillatory
   * functions. Mathematical Proceedings of the Cambridge Philosophical Society, 52(4), 764-768.
   * doi:10.1017/S030500410003187X</li>
   * </ul>
   * </p>
   * 
   * @param f the function to integrate
   * @param splitPoints the points at which to split the integration region when defining definite
   *        integrals
   * @return an Iterable representing the sequence of definite integrals
   */
  public Iterable<Double> integratePiecewise(final DoubleUnaryOperator f,
      final Iterable<Double> splitPoints) {
    return () -> new Iterator<>() {

      private final Iterator<Double> it = splitPoints.iterator();
      private double left = 0.0;
      private double right = it.hasNext() ? it.next() : Double.NaN;

      @Override
      public final boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public final Double next() {
        left = right;
        right = it.next();
        return integrate(f, left, right).estimate;
      }
    };
  }
}
