package org.matheclipse.core.numerics.functions;

import org.hipparchus.analysis.integration.gauss.GaussIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegratorFactory;
import org.hipparchus.special.Erf;

/**
 * Owen's T function in machine precision,
 * <code>T(h, a) = 1/(2*Pi)*Integrate(E^(-h^2*(1+t^2)/2)/(1+t^2), {t, 0, a})</code>.
 *
 * <p>
 * For <code>|a| <= 1</code> the integral is summed by Gauss-Legendre quadrature over pieces of
 * width <code>2/h</code>, so that the bell of <code>E^(-h^2*t^2/2)</code> is resolved for every
 * <code>h</code>. A larger <code>a</code> is reduced to <code>1/a</code> by
 * <code>T(h, a) + T(a*h, 1/a) == (Phi(h)*Q(a*h) + Phi(a*h)*Q(h))/2</code> with the normal
 * distribution <code>Phi</code> and its tail <code>Q = 1 - Phi</code>, both taken from
 * <code>Erfc</code> so that no digits cancel in the tails.
 */
public final class OwenTFunction {

  private static final GaussIntegrator LEGENDRE = new GaussIntegratorFactory().legendre(20);

  private static final double TWO_PI = 2.0 * Math.PI;

  private OwenTFunction() {}

  /** <code>OwenT(h, a)</code> */
  public static double value(double h, double a) {
    if (Double.isNaN(h) || Double.isNaN(a)) {
      return Double.NaN;
    }
    if (a < 0) {
      // odd in a
      return -value(h, -a);
    }
    h = Math.abs(h);
    if (a == 0.0 || Double.isInfinite(h)) {
      return 0.0;
    }
    if (h == 0.0) {
      return Math.atan(a) / TWO_PI;
    }
    if (Double.isInfinite(a)) {
      return 0.5 * upperTail(h);
    }
    if (a <= 1.0) {
      return integral(h, a);
    }
    double ah = a * h;
    double reflected = 0.5 * (lowerTail(h) * upperTail(ah) + lowerTail(ah) * upperTail(h));
    return reflected - integral(ah, 1.0 / a);
  }

  /** <code>Q(x) = Erfc(x/Sqrt(2))/2</code>, the upper tail of the standard normal distribution */
  private static double upperTail(double x) {
    return 0.5 * Erf.erfc(x / Math.sqrt(2.0));
  }

  /** <code>Phi(x) = Erfc(-x/Sqrt(2))/2</code> */
  private static double lowerTail(double x) {
    return 0.5 * Erf.erfc(-x / Math.sqrt(2.0));
  }

  /** The defining integral over <code>[0, a]</code> for <code>0 < a <= 1</code>. */
  private static double integral(double h, double a) {
    double halfH2 = 0.5 * h * h;
    if (halfH2 > 745.0) {
      // E^(-h^2/2) is below the smallest machine number
      return 0.0;
    }
    int pieces = (int) Math.min(1000.0, Math.ceil(a * h / 2.0));
    if (pieces < 1) {
      pieces = 1;
    }
    double width = a / pieces;
    double sum = 0.0;
    int n = LEGENDRE.getNumberOfPoints();
    for (int piece = 0; piece < pieces; piece++) {
      double left = piece * width;
      double partial = 0.0;
      for (int i = 0; i < n; i++) {
        double t = left + 0.5 * width * (LEGENDRE.getPoint(i) + 1.0);
        double t2 = 1.0 + t * t;
        partial += LEGENDRE.getWeight(i) * Math.exp(-halfH2 * t2) / t2;
      }
      sum += 0.5 * width * partial;
    }
    return sum / TWO_PI;
  }
}
