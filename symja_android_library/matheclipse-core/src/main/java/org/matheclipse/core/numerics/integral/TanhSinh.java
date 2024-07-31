package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * A numerical integration algorithm based on the Tanh-Sinh quadrature rule [1]. This implementation
 * uses the method of weight an abcissa calculation in [2] and further extensions in [3].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Takahasi, Hidetosi, and Masatake Mori. "Double exponential formulas for numerical
 * integration." Publications of the Research Institute for Mathematical Sciences 9.3 (1974):
 * 721-741.</li>
 * <li>[2] Mori, M.. �Developments in the Double Exponential Formulas for Numerical Integration.�
 * (1990).</li>
 * <li>[3] Bailey, D.. �Tanh-Sinh High-Precision Quadrature.� (2006).</li>
 * </ul>
 * </p>
 */
public final class TanhSinh extends Quadrature {

  private final int myLevels;

  /**
   * Creates a new instance of the Tanh-Sinh quadrature integrator. The weights and abscissas are
   * pre-computed and can be reused for evaluating multiple integrals.
   * 
   * @param absoluteTolerance the smallest acceptable absolute change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of evaluations of each function permitted
   * @param maxLevels determines the number of interpolation points; choosing large values may cause
   *        a heap exception; defaults to 12
   */
  public TanhSinh(final double absoluteTolerance, final int maxEvaluations, final int maxLevels) {
    super(absoluteTolerance, maxEvaluations);
    myLevels = maxLevels;
  }

  /**
   * Creates a new instance of the Tanh-Sinh quadrature integrator. The weights and abscissas are
   * pre-computed and can be reused for evaluating multiple integrals.
   * 
   * @param absoluteTolerance the smallest acceptable absolute change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of evaluations of each function permitted
   */
  public TanhSinh(final double absoluteTolerance, final int maxEvaluations) {
    this(absoluteTolerance, maxEvaluations, 12);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {
    return tanhsinh(f, a, b);
  }

  @Override
  public final String getName() {
    return "TanhSinh";
  }

  private final QuadratureResult tanhsinh(final DoubleUnaryOperator f, final double a,
      final double b) {
    final double wid = (b - a) / 2.0;
    final double mid = (b + a) / 2.0;
    double est = f.applyAsDouble(mid);
    double estp = 0.0, fm = 0.0, fp = 0.0, error = Double.NaN, h = 1.0;
    int fev = 1;

    // main loop of tanh-sinh quadrature
    for (int k = 0; k <= myLevels; ++k) {

      // double the number of points
      h = SimpleMath.pow(2.0, -k);
      double estk = 0.0;
      int j = 1;
      while (true) {

        // compute function values at the next interpolation point
        final double t = h * j;
        final double et = Math.exp(t);
        final double u = Math.exp(1.0 / et - et);
        final double r = 2.0 * u / (1.0 + u);
        final double x = wid * r;
        if (t > 6.56 || r <= 0 || r >= 1) {
          return new QuadratureResult(est * wid * h, error, fev, false);
        }
        if (a + x > a) {
          fp = f.applyAsDouble(a + x);
          ++fev;
        }
        if (b - x < b) {
          fm = f.applyAsDouble(b - x);
          ++fev;
        }

        // update integral estimate
        final double w = (et + 1.0 / et) * r / (1.0 + u);
        final double v = (fp + fm) * w;
        estk += v;
        if (k > 0) {
          j += 2;
        } else {
          ++j;
        }

        // reached max evaluations
        if (fev >= myMaxEvals) {
          return new QuadratureResult(est * wid * h, error, fev, false);
        }

        // contribution to integral too small
        if (Math.abs(v) <= Constants.EPSILON * Math.abs(estk)) {
          break;
        }
      }

      // update integral estimate and error
      est += estk;
      error = Math.abs(2.0 * estp / est - 1.0);

      // check if the error is reached
      if (error < myTol) {
        return new QuadratureResult(est * wid * h, error, fev, true);
      }

      // continue to refine the mesh
      estp = est;
    }

    // could not converge
    return new QuadratureResult(est * wid * h, error, fev, false);
  }
}
