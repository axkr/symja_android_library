package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * Integrate a real function of one variable over a finite interval using an adaptive 8-point
 * Legendre-Gauss algorithm. This algorithm is a translation of the corresponding subroutine dgaus8
 * from the SLATEC library.
 */
public final class GaussLegendre extends Quadrature {

  private static final double X1 = 1.83434642495649805e-1;
  private static final double X2 = 5.25532409916328986e-1;
  private static final double X3 = 7.96666477413626740e-1;
  private static final double X4 = 9.60289856497536232e-1;
  private static final double W1 = 3.62683783378361983e-1;
  private static final double W2 = 3.13706645877887287e-1;
  private static final double W3 = 2.22381034453374471e-1;
  private static final double W4 = 1.01228536290376259e-1;
  private static final double SQ2 = Constants.SQRT2;

  public GaussLegendre(final double tolerance, final int maxEvaluations) {
    super(tolerance, maxEvaluations);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {

    // prepare variables
    final double[] err = {myTol};
    final double[] ans = new double[1];
    final int[] ierr = new int[1];
    final int[] fev = new int[1];

    // call main subroutine
    dgaus8(f, a, b, err, ans, ierr, fev);
    return new QuadratureResult(ans[0], err[0], fev[0], ierr[0] == 1 || ierr[0] == -1);
  }

  @Override
  public final String getName() {
    return "Gauss-Legendre";
  }

  private void dgaus8(final DoubleUnaryOperator fun, final double a, final double b,
      final double[] err, final double[] ans, final int[] ierr, final int[] fev) {
    int k, kml = 6, kmx = 5000, l, lmx, mxl, nbits, nib, nlmx;
    double ae, anib, area, c, ce, ee, ef, eps, est, gl, glr, tol, vr;
    final int[] lr = new int[61];
    final double[] aa = new double[61], hh = new double[61], vl = new double[61],
        gr = new double[61];

    // Initialize
    fev[0] = 0;
    k = 53;
    anib = SimpleMath.D1MACH[5 - 1] * k / 0.30102000;
    nbits = (int) anib;
    nlmx = Math.min(60, (nbits * 5) / 8);
    ans[0] = 0.0;
    ierr[0] = 1;
    ce = 0.0;
    if (a == b) {
      if (err[0] < 0.0) {
        err[0] = ce;
      }
      return;
    }
    lmx = nlmx;
    if (b != 0.0 && SimpleMath.sign(1.0, b) * a > 0.0) {
      c = Math.abs(1.0 - a / b);
      if (c <= 0.1) {
        if (c <= 0.0) {
          if (err[0] < 0.0) {
            err[0] = ce;
          }
          return;
        }
        anib = 0.5 - Math.log(c) * Constants.LOG2_INV;
        nib = (int) anib;
        lmx = Math.min(nlmx, nbits - nib - 7);
        if (lmx < 1) {
          ierr[0] = -1;
          if (err[0] < 0.0) {
            err[0] = ce;
          }
          return;
        }
      }
    }

    tol = Math.max(Math.abs(err[0]), SimpleMath.pow(2.0, 5 - nbits)) / 2.0;
    if (err[0] == 0.0) {
      tol = Math.sqrt(SimpleMath.D1MACH[4 - 1]);
    }
    eps = tol;
    hh[1 - 1] = (b - a) / 4.0;
    aa[1 - 1] = a;
    lr[1 - 1] = 1;
    l = 1;
    est = g8(fun, aa[l - 1] + 2.0 * hh[l - 1], 2.0 * hh[l - 1]);
    fev[0] += 8;
    k = 8;
    area = Math.abs(est);
    ef = 0.5;
    mxl = 0;

    while (true) {

      // Compute refined estimates, estimate the error, etc.
      if (fev[0] - 8 >= myMaxEvals) {
        // ans[0] = Double.NaN;
        ierr[0] = 2;
        return;
      }
      gl = g8(fun, aa[l - 1] + hh[l - 1], hh[l - 1]);
      fev[0] += 8;
      if (fev[0] - 8 >= myMaxEvals) {
        // ans[0] = Double.NaN;
        ierr[0] = 2;
        return;
      }
      gr[l - 1] = g8(fun, aa[l - 1] + 3.0 * hh[l - 1], hh[l - 1]);
      fev[0] += 8;

      k += 16;
      area += (Math.abs(gl) + Math.abs(gr[l - 1]) - Math.abs(est));
      glr = gl + gr[l - 1];
      ee = Math.abs(est - glr) * ef;
      ae = Math.max(eps * area, tol * Math.abs(glr));
      if (ee - ae > 0.0) {

        // Consider the left half of this level
        if (k > kmx) {
          lmx = kml;
        }
        if (l >= lmx) {
          mxl = 1;
        } else {
          ++l;
          eps *= 0.5;
          ef /= SQ2;
          hh[l - 1] = hh[l - 1 - 1] * 0.5;
          lr[l - 1] = -1;
          aa[l - 1] = aa[l - 1 - 1];
          est = gl;
          continue;
        }
      }

      ce += (est - glr);
      if (lr[l - 1] <= 0.0) {

        // Proceed to right half at this level
        vl[l - 1] = glr;
        est = gr[l - 1 - 1];
        lr[l - 1] = 1;
        aa[l - 1] += 4.0 * hh[l - 1];
      } else {

        // Return one level
        vr = glr;
        while (true) {
          if (l <= 1) {
            ans[0] = vr;
            if (mxl != 0 && Math.abs(ce) > 2.0 * tol * area) {
              ierr[0] = 2;
            }
            if (err[0] < 0.0) {
              err[0] = ce;
            }
            return;
          }
          --l;
          eps *= 2.0;
          ef *= SQ2;
          if (lr[l - 1] <= 0.0) {
            vl[l - 1] = vl[l + 1 - 1] + vr;
            est = gr[l - 1 - 1];
            lr[l - 1] = 1;
            aa[l - 1] += 4.0 * hh[l - 1];
            break;
          } else {
            vr += vl[l + 1 - 1];
          }
        }
      }
    }
  }

  private static double g8(final DoubleUnaryOperator fun, final double x, final double h) {
    final double fx1 = fun.applyAsDouble(x - X1 * h) + fun.applyAsDouble(x + X1 * h);
    final double fx2 = fun.applyAsDouble(x - X2 * h) + fun.applyAsDouble(x + X2 * h);
    final double fx3 = fun.applyAsDouble(x - X3 * h) + fun.applyAsDouble(x + X3 * h);
    final double fx4 = fun.applyAsDouble(x - X4 * h) + fun.applyAsDouble(x + X4 * h);
    return h * (W1 * fx1 + W2 * fx2 + W3 * fx3 + W4 * fx4);
  }
}
