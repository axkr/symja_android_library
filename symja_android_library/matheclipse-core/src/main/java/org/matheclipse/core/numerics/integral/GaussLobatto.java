package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;

/**
 * Implements an adaptive numerical integrator based on the 7-point Gauss-Lobatto rule, as described
 * in [1].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Gander, W., Gautschi, W. Adaptive Quadrature�Revisited. BIT Numerical Mathematics 40,
 * 84�101 (2000). https://doi.org/10.1023/A:1022318402393</li>
 * </ul>
 * </p>
 */
public final class GaussLobatto extends Quadrature {

  private static final double ALPHA = Constants.SQRT2 / Constants.SQRT3;
  private static final double BETA = 1.0 / Constants.SQRT5;

  private static final double[] X =
      {0.94288241569547971906, 0.64185334234578130578, 0.23638319966214988028};
  private static final double[] Y =
      {0.0158271919734801831, 0.0942738402188500455, 0.1550719873365853963, 0.1888215739601824544,
          0.1997734052268585268, 0.2249264653333395270, 0.2426110719014077338};
  private static final double[] C = {77.0, 432.0, 625.0, 672.0};

  private int fev;

  public GaussLobatto(final double tolerance, final int maxEvaluations) {
    super(tolerance, maxEvaluations);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {
    return dlob8e(f, a, b);
  }

  @Override
  public final String getName() {
    return "Gauss-Lobatto";
  }

  private final QuadratureResult dlob8e(final DoubleUnaryOperator f, final double a,
      final double b) {

    // compute interpolation points
    final double mid = 0.5 * (a + b);
    final double h = 0.5 * (b - a);
    final double y1 = f.applyAsDouble(a);
    final double y3 = f.applyAsDouble(mid - h * ALPHA);
    final double y5 = f.applyAsDouble(mid - h * BETA);
    final double y7 = f.applyAsDouble(mid);
    final double y9 = f.applyAsDouble(mid + h * BETA);
    final double y11 = f.applyAsDouble(mid + h * ALPHA);
    final double y13 = f.applyAsDouble(b);
    final double f1 = f.applyAsDouble(mid - h * X[0]);
    final double f2 = f.applyAsDouble(mid + h * X[0]);
    final double f3 = f.applyAsDouble(mid - h * X[1]);
    final double f4 = f.applyAsDouble(mid + h * X[1]);
    final double f5 = f.applyAsDouble(mid - h * X[2]);
    final double f6 = f.applyAsDouble(mid + h * X[2]);
    fev = 13;

    // compute a crude initial estimate of the integral
    final double est1 = (y1 + y13 + 5.0 * (y5 + y9)) * (h / 6.0);

    // compute a more refined estimate of the integral
    double est2 = C[0] * (y1 + y13) + C[1] * (y3 + y11) + C[2] * (y5 + y9) + C[3] * y7;
    est2 *= (h / 1470.0);

    // compute the error estimate
    double s = Y[0] * (y1 + y13) + Y[1] * (f1 + f2);
    s += Y[2] * (y3 + y11) + Y[3] * (f3 + f4);
    s += Y[4] * (y5 + y9) + Y[5] * (f5 + f6) + Y[6] * y7;
    s *= h;
    double rtol = myTol;
    if (est1 != s) {
      final double r = Math.abs(est2 - s) / Math.abs(est1 - s);
      if (r > 0.0 && r < 1.0) {
        rtol /= r;
      }
    }
    double sign = Math.signum(s);
    if (sign == 0) {
      sign = 1.0;
    }
    double s1 = sign * Math.abs(s) * rtol / Constants.EPSILON;
    if (s == 0) {
      s1 = Math.abs(b - a);
    }

    // call the recursive subroutine
    final double result = dlob8(f, a, b, y1, y13, s1, rtol);
    return new QuadratureResult(result, Double.NaN, fev, Double.isFinite(result));
  }

  private final double dlob8(final DoubleUnaryOperator f, final double a, final double b,
      final double fa, final double fb, final double s, final double rtol) {

    // check the budget of evaluations
    if (fev >= myMaxEvals) {
      return Double.NaN;
    }

    // compute the interpolation points
    final double h = 0.5 * (b - a);
    final double mid = 0.5 * (a + b);
    final double mll = mid - h * ALPHA;
    final double ml = mid - h * BETA;
    final double mr = mid + BETA * h;
    final double mrr = mid + h * ALPHA;
    final double fmll = f.applyAsDouble(mll);
    final double fml = f.applyAsDouble(ml);
    final double fmid = f.applyAsDouble(mid);
    final double fmr = f.applyAsDouble(mr);
    final double fmrr = f.applyAsDouble(mrr);
    fev += 8;

    // compute a crude estimate of the integral
    final double est1 = (fa + fb + 5.0 * (fml + fmr)) * (h / 6.0);

    // compute a more refined estimate of the integral
    double est2 = C[0] * (fa + fb) + C[1] * (fmll + fmrr) + C[2] * (fml + fmr) + C[3] * fmid;
    est2 *= (h / 1470.0);

    // check the convergence
    if (s + (est2 - est1) == s || mll <= a || b <= mrr) {
      return est2;
    }

    // subdivide the integration region and repeat
    return dlob8(f, a, mll, fa, fmll, s, rtol) + dlob8(f, mll, ml, fmll, fml, s, rtol)
        + dlob8(f, ml, mid, fml, fmid, s, rtol) + dlob8(f, mid, mr, fmid, fmr, s, rtol)
        + dlob8(f, mr, mrr, fmr, fmrr, s, rtol) + dlob8(f, mrr, b, fmrr, fb, s, rtol);
  }
}
