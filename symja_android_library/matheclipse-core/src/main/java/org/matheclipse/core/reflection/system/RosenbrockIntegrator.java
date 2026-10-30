package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.LocalizedODEFormats;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.sampling.AbstractODEStateInterpolator;
import org.hipparchus.util.FastMath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An adaptive Rosenbrock (linearly implicit Runge-Kutta) integrator for stiff systems: the RODAS
 * method of order 4 with an embedded formula of order 3 and a continuous extension of order 3
 * (Hairer, Wanner: Solving Ordinary Differential Equations II, section IV.7).
 *
 * <p>
 * Every step solves linear systems with the matrix <code>1/(h*gamma) I - J</code>, where
 * <code>J</code> is the Jacobian of the right hand side at the start of the step. The method is
 * L-stable and stiffly accurate, so its step size follows the accuracy of the slowly varying
 * solution rather than the fastest decaying component, which bounds the steps of an explicit
 * method.
 *
 * <p>
 * It plugs into the integrator framework as an ordinary adaptive integrator: step handlers such as
 * a {@link org.hipparchus.ode.DenseOutputModel} and event detectors work with it unchanged.
 */
final class RosenbrockIntegrator extends AdaptiveStepsizeIntegrator {

  /** The partial derivatives of the right hand side <code>f(t, y)</code>. */
  interface Jacobian {
    /**
     * Fill <code>dfdy[i][j] = df_i/dy_j</code> and <code>dfdt[i] = df_i/dt</code> at
     * <code>(t, y)</code>.
     *
     * @return <code>false</code> if they could not be computed; they are then approximated by
     *         finite differences
     */
    boolean compute(double t, double[] y, double[][] dfdy, double[] dfdt);
  }

  private static final double GAMMA = 0.25;

  private static final double C2 = 0.386, C3 = 0.21, C4 = 0.63;

  private static final double D1 = 0.25, D2 = -0.1043, D3 = 0.1035, D4 = -0.0362;

  private static final double A21 = 1.544;
  private static final double A31 = 0.9466785280815826, A32 = 0.2557011698983284;
  private static final double A41 = 3.314825187068521, A42 = 2.896124015972201,
      A43 = 0.9986419139977817;
  private static final double A51 = 1.221224509226641, A52 = 6.019134481288629,
      A53 = 12.53708332932087, A54 = -0.687886036105895;

  private static final double C21 = -5.6688;
  private static final double C31 = -2.430093356833875, C32 = -0.2063599157091915;
  private static final double C41 = -0.1073529058151375, C42 = -9.594562251023355,
      C43 = -20.47028614809616;
  private static final double C51 = 7.496443313967647, C52 = -10.24680431464352,
      C53 = -33.99990352819905, C54 = 11.7089089320616;
  private static final double C61 = 8.083246795921522, C62 = -7.981132988064893,
      C63 = -31.52159432874371, C64 = 16.3193054312314, C65 = -6.058818238834054;

  /** The coefficients of the continuous extension. */
  private static final double D21 = 10.12623508344586, D22 = -7.487995877610167,
      D23 = -34.80091861555747, D24 = -7.992771707568823, D25 = 1.025137723295662;
  private static final double D31 = -0.6762803392801253, D32 = 6.087714651680015,
      D33 = 16.43084320892478, D34 = 24.76722511418386, D35 = -6.594389125716872;

  /** The step size control: safety factor and the bounds of the change of one step. */
  private static final double SAFETY = 0.9, MIN_REDUCTION = 0.2, MAX_GROWTH = 6.0;

  /** The partial derivatives of the right hand side, or <code>null</code>. */
  private final Jacobian fJacobian;

  /** Called once per step, to stop an integration which has run out of time. */
  private final Runnable fCheck;

  /**
   * @param minStep the smallest step before the integration gives up
   * @param maxStep the largest step
   * @param absoluteTolerance the allowed absolute local error
   * @param relativeTolerance the allowed relative local error
   * @param jacobian the partial derivatives of the right hand side, or <code>null</code> to
   *        approximate them by finite differences
   * @param check called once per step; may throw to stop the integration
   */
  RosenbrockIntegrator(double minStep, double maxStep, double absoluteTolerance,
      double relativeTolerance, Jacobian jacobian, Runnable check) {
    super("Rosenbrock", minStep, maxStep, absoluteTolerance, relativeTolerance);
    this.fJacobian = jacobian;
    this.fCheck = check;
  }

  @Override
  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "an event handler which stopped the step returns a different time")
  public ODEStateAndDerivative integrate(final ExpandableODE equations,
      final ODEState initialState, final double finalTime) {
    sanityChecks(initialState, finalTime);
    setStepStart(initIntegration(equations, initialState, finalTime));
    final boolean forward = finalTime > initialState.getTime();
    final EquationsMapper mapper = equations.getMapper();
    final int n = mapper.getTotalDimension();
    final int mainDimension = getStepSizeHelper().getMainSetDimension();

    final double[][] dfdy = new double[n][n];
    final double[] dfdt = new double[n];
    final double[][] matrix = new double[n][n];
    final int[] pivot = new int[n];
    final double[] k1 = new double[n], k2 = new double[n], k3 = new double[n], k4 = new double[n],
        k5 = new double[n], k6 = new double[n];
    final double[] yStage = new double[n];
    final double[] w2 = new double[n], w3 = new double[n];
    // the derivative of the continuous extension at the end of the last step
    double[] startSlope = null;

    double hNew = 0.0;
    boolean firstTime = true;
    setIsLastStep(false);
    do {
      if (fCheck != null) {
        fCheck.run();
      }
      final ODEStateAndDerivative start = getStepStart();
      final double t = start.getTime();
      final double[] y = start.getCompleteState();
      final double[] f0 = start.getCompleteDerivative();
      jacobian(t, y, f0, dfdy, dfdt);

      if (firstTime) {
        final double[] scale = new double[mainDimension];
        for (int i = 0; i < scale.length; i++) {
          scale[i] = getStepSizeHelper().getTolerance(i, FastMath.abs(y[i]));
        }
        hNew = initializeStep(forward, 4, scale, start);
        firstTime = false;
      }

      double error = Double.POSITIVE_INFINITY;
      boolean rejected = false;
      double[] yNew = null;
      while (!(error < 1.0)) {
        setStepSize(hNew);
        if (forward ? t + getStepSize() >= finalTime : t + getStepSize() <= finalTime) {
          setStepSize(finalTime - t);
        }
        final double h = getStepSize();
        final double diagonal = 1.0 / (h * GAMMA);
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            matrix[i][j] = -dfdy[i][j];
          }
          matrix[i][i] += diagonal;
        }
        if (!decompose(matrix, pivot)) {
          // singular for this step size: try a smaller one
          rejected = true;
          hNew = getStepSizeHelper().filterStep(h * MIN_REDUCTION, forward, false);
          continue;
        }

        // stage 1
        for (int i = 0; i < n; i++) {
          k1[i] = f0[i] + h * D1 * dfdt[i];
        }
        solve(matrix, pivot, k1);

        // stage 2
        for (int i = 0; i < n; i++) {
          yStage[i] = y[i] + A21 * k1[i];
        }
        double[] f = computeDerivatives(t + C2 * h, yStage);
        for (int i = 0; i < n; i++) {
          k2[i] = f[i] + (C21 * k1[i]) / h + h * D2 * dfdt[i];
        }
        solve(matrix, pivot, k2);

        // stage 3
        for (int i = 0; i < n; i++) {
          yStage[i] = y[i] + A31 * k1[i] + A32 * k2[i];
        }
        f = computeDerivatives(t + C3 * h, yStage);
        for (int i = 0; i < n; i++) {
          k3[i] = f[i] + (C31 * k1[i] + C32 * k2[i]) / h + h * D3 * dfdt[i];
        }
        solve(matrix, pivot, k3);

        // stage 4
        for (int i = 0; i < n; i++) {
          yStage[i] = y[i] + A41 * k1[i] + A42 * k2[i] + A43 * k3[i];
        }
        f = computeDerivatives(t + C4 * h, yStage);
        for (int i = 0; i < n; i++) {
          k4[i] = f[i] + (C41 * k1[i] + C42 * k2[i] + C43 * k3[i]) / h + h * D4 * dfdt[i];
        }
        solve(matrix, pivot, k4);

        // stage 5
        for (int i = 0; i < n; i++) {
          yStage[i] = y[i] + A51 * k1[i] + A52 * k2[i] + A53 * k3[i] + A54 * k4[i];
        }
        f = computeDerivatives(t + h, yStage);
        for (int i = 0; i < n; i++) {
          k5[i] = f[i] + (C51 * k1[i] + C52 * k2[i] + C53 * k3[i] + C54 * k4[i]) / h;
        }
        solve(matrix, pivot, k5);

        // the embedded solution of order 3
        for (int i = 0; i < n; i++) {
          yStage[i] += k5[i];
        }
        f = computeDerivatives(t + h, yStage);
        for (int i = 0; i < n; i++) {
          k6[i] = f[i] + (C61 * k1[i] + C62 * k2[i] + C63 * k3[i] + C64 * k4[i] + C65 * k5[i]) / h;
        }
        solve(matrix, pivot, k6);

        // the solution of order 4; k6 is its difference to the embedded one
        yNew = new double[n];
        for (int i = 0; i < n; i++) {
          yNew[i] = yStage[i] + k6[i];
          w2[i] = D21 * k1[i] + D22 * k2[i] + D23 * k3[i] + D24 * k4[i] + D25 * k5[i];
          w3[i] = D31 * k1[i] + D32 * k2[i] + D33 * k3[i] + D34 * k4[i] + D35 * k5[i];
        }
        // The local error is k6. The steps of a stiff system near its slowly varying solution may
        // grow far beyond what the cubic continuous extension can follow, so the error of the
        // latter is controlled too: the derivative of the extension where the step starts is
        // compared with the one the step before ended with, or with the right hand side at the
        // initial state. A difference d of the end slopes moves the middle of a cubic by h*d/8.
        final double[] slope = startSlope != null ? startSlope : f0;
        double sum = 0.0;
        double denseSum = 0.0;
        for (int i = 0; i < mainDimension; i++) {
          double tolerance = getStepSizeHelper().getTolerance(i,
              FastMath.max(FastMath.abs(y[i]), FastMath.abs(yNew[i])));
          double ratio = k6[i] / tolerance;
          sum += ratio * ratio;
          double dense = (yNew[i] - y[i] + w2[i] - h * slope[i]) / (8.0 * tolerance);
          denseSum += dense * dense;
        }
        error = FastMath.sqrt(FastMath.max(sum, denseSum) / mainDimension);
        if (!(error < 1.0)) {
          // a NaN or an overflow is the sign of a step much too large: reduce it the most
          final double factor = Double.isFinite(error)
              ? FastMath.max(MIN_REDUCTION, SAFETY * FastMath.pow(error, -0.25))
              : MIN_REDUCTION;
          rejected = true;
          hNew = getStepSizeHelper().filterStep(h * factor, forward, false);
        }
      }

      final double h = getStepSize();
      final double stepEnd = t + h;
      final double[] fNew = computeDerivatives(stepEnd, yNew);
      for (double value : fNew) {
        if (!Double.isFinite(value)) {
          throw new MathIllegalStateException(LocalizedODEFormats.NAN_APPEARING_DURING_INTEGRATION,
              stepEnd);
        }
      }
      final ODEStateAndDerivative end = mapper.mapStateAndDerivative(stepEnd, yNew, fNew);
      final StepInterpolator interpolator = new StepInterpolator(forward, start, end, start, end,
          mapper, y.clone(), yNew, w2.clone(), w3.clone());
      setStepStart(acceptStep(interpolator, finalTime));
      if (resetOccurred() || getStepStart().getTime() != stepEnd) {
        // an event changed the state or stopped the step early: start afresh from its derivative
        startSlope = null;
      } else {
        startSlope = new double[n];
        for (int i = 0; i < n; i++) {
          startSlope[i] = (yNew[i] - y[i] - w2[i] - w3[i]) / h;
        }
      }

      if (!isLastStep()) {
        double factor =
            FastMath.min(MAX_GROWTH, FastMath.max(MIN_REDUCTION, SAFETY * FastMath.pow(error, -0.25)));
        if (rejected) {
          // don't grow right after a rejected attempt
          factor = FastMath.min(1.0, factor);
        }
        final double scaledH = h * factor;
        final double nextT = getStepStart().getTime() + scaledH;
        final boolean nextIsLast = forward ? nextT >= finalTime : nextT <= finalTime;
        hNew = getStepSizeHelper().filterStep(scaledH, forward, nextIsLast);
        final double filteredNextT = getStepStart().getTime() + hNew;
        if (forward ? filteredNextT >= finalTime : filteredNextT <= finalTime) {
          hNew = finalTime - getStepStart().getTime();
        }
      }
    } while (!isLastStep());

    final ODEStateAndDerivative finalState = getStepStart();
    resetInternalState();
    return finalState;
  }

  /**
   * The partial derivatives at <code>(t, y)</code>, from the {@link Jacobian} if it can, otherwise
   * by forward differences.
   *
   * @param f0 the right hand side at <code>(t, y)</code>
   */
  private void jacobian(double t, double[] y, double[] f0, double[][] dfdy, double[] dfdt) {
    if (fJacobian != null && fJacobian.compute(t, y, dfdy, dfdt) && isFinite(dfdy, dfdt)) {
      return;
    }
    final int n = y.length;
    final double root = FastMath.sqrt(FastMath.ulp(1.0));
    final double[] shifted = y.clone();
    for (int j = 0; j < n; j++) {
      final double delta = root * FastMath.max(1.0e-5, FastMath.abs(y[j]));
      shifted[j] = y[j] + delta;
      final double[] f = computeDerivatives(t, shifted);
      for (int i = 0; i < n; i++) {
        dfdy[i][j] = (f[i] - f0[i]) / delta;
      }
      shifted[j] = y[j];
    }
    final double delta = root * FastMath.max(1.0e-5, FastMath.abs(t));
    final double[] f = computeDerivatives(t + delta, y);
    for (int i = 0; i < n; i++) {
      dfdt[i] = (f[i] - f0[i]) / delta;
    }
  }

  private static boolean isFinite(double[][] dfdy, double[] dfdt) {
    for (int i = 0; i < dfdt.length; i++) {
      if (!Double.isFinite(dfdt[i])) {
        return false;
      }
      for (double value : dfdy[i]) {
        if (!Double.isFinite(value)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Replace <code>a</code> by its LU decomposition with partial pivoting.
   *
   * @return <code>false</code> if it is singular
   */
  private static boolean decompose(double[][] a, int[] pivot) {
    final int n = a.length;
    for (int k = 0; k < n; k++) {
      int p = k;
      double largest = FastMath.abs(a[k][k]);
      for (int i = k + 1; i < n; i++) {
        if (FastMath.abs(a[i][k]) > largest) {
          largest = FastMath.abs(a[i][k]);
          p = i;
        }
      }
      if (!(largest > 0.0) || !Double.isFinite(largest)) {
        return false;
      }
      pivot[k] = p;
      if (p != k) {
        double[] row = a[p];
        a[p] = a[k];
        a[k] = row;
      }
      for (int i = k + 1; i < n; i++) {
        final double factor = a[i][k] / a[k][k];
        a[i][k] = factor;
        if (factor != 0.0) {
          for (int j = k + 1; j < n; j++) {
            a[i][j] -= factor * a[k][j];
          }
        }
      }
    }
    return true;
  }

  /** Overwrite <code>b</code> with the solution of <code>a.x == b</code>, factored by decompose. */
  private static void solve(double[][] lu, int[] pivot, double[] b) {
    final int n = b.length;
    for (int k = 0; k < n; k++) {
      final int p = pivot[k];
      if (p != k) {
        double swap = b[p];
        b[p] = b[k];
        b[k] = swap;
      }
      for (int i = k + 1; i < n; i++) {
        b[i] -= lu[i][k] * b[k];
      }
    }
    for (int i = n - 1; i >= 0; i--) {
      double sum = b[i];
      for (int j = i + 1; j < n; j++) {
        sum -= lu[i][j] * b[j];
      }
      b[i] = sum / lu[i][i];
    }
  }

  /**
   * The continuous extension of one step,
   * <code>y(t0 + s h) = (1-s) y0 + s (y1 + (1-s) (w2 + s w3))</code>.
   */
  private static final class StepInterpolator extends AbstractODEStateInterpolator {
    private final double[] fY0, fY1, fW2, fW3;

    StepInterpolator(boolean forward, ODEStateAndDerivative globalPrevious,
        ODEStateAndDerivative globalCurrent, ODEStateAndDerivative softPrevious,
        ODEStateAndDerivative softCurrent, EquationsMapper mapper, double[] y0, double[] y1,
        double[] w2, double[] w3) {
      super(forward, globalPrevious, globalCurrent, softPrevious, softCurrent, mapper);
      fY0 = y0;
      fY1 = y1;
      fW2 = w2;
      fW3 = w3;
    }

    @Override
    protected StepInterpolator create(boolean newForward,
        ODEStateAndDerivative newGlobalPreviousState, ODEStateAndDerivative newGlobalCurrentState,
        ODEStateAndDerivative newSoftPreviousState, ODEStateAndDerivative newSoftCurrentState,
        EquationsMapper newMapper) {
      return new StepInterpolator(newForward, newGlobalPreviousState, newGlobalCurrentState,
          newSoftPreviousState, newSoftCurrentState, newMapper, fY0, fY1, fW2, fW3);
    }

    @Override
    protected ODEStateAndDerivative computeInterpolatedStateAndDerivatives(
        EquationsMapper equationsMapper, double time, double theta, double thetaH,
        double oneMinusThetaH) {
      final double h = getGlobalCurrentState().getTime() - getGlobalPreviousState().getTime();
      final double s = theta;
      final double s1 = 1.0 - s;
      final int n = fY0.length;
      final double[] y = new double[n];
      final double[] yDot = new double[n];
      for (int i = 0; i < n; i++) {
        y[i] = s1 * fY0[i] + s * (fY1[i] + s1 * (fW2[i] + s * fW3[i]));
        yDot[i] = (fY1[i] - fY0[i] + (1.0 - 2.0 * s) * fW2[i] + s * (2.0 - 3.0 * s) * fW3[i]) / h;
      }
      return equationsMapper.mapStateAndDerivative(time, y, yDot);
    }
  }
}
