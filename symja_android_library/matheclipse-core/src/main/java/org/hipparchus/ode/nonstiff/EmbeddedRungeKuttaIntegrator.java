/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hipparchus.ode.nonstiff;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.LocalizedODEFormats;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.util.FastMath;

/**
 * This class implements the common part of all embedded Runge-Kutta
 * integrators for Ordinary Differential Equations.
 * <p>
 * <p>These methods are embedded explicit Runge-Kutta methods with two
 * sets of coefficients allowing to estimate the error, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 *       |  b'1  b'2 ...   b's-1 b's
 * </pre>
 * </p>
 * <p>
 * <p>In fact, we rather use the array defined by ej = bj - b'j to
 * compute directly the error rather than computing two estimates and
 * then comparing them.</p>
 * <p>
 * <p>Some methods are qualified as <i>fsal</i> (first same as last)
 * methods. This means the last evaluation of the derivatives in one
 * step is the same as the first in the next step. Then, this
 * evaluation can be reused from one step to the next one and the cost
 * of such a method is really s-1 evaluations despite the method still
 * has s stages. This behaviour is true only for successful steps, if
 * the step is rejected after the error estimation phase, no
 * evaluation is saved. For an <i>fsal</i> method, we have cs = 1 and
 * asi = bi for all i.</p>
 */

public abstract class EmbeddedRungeKuttaIntegrator
        extends AdaptiveStepsizeIntegrator
        implements ButcherArrayProvider {

    /**
     * Index of the pre-computed derivative for <i>fsal</i> methods.
     */
    private final int fsal;

    /**
     * Time steps from Butcher array (without the first zero).
     */
    private final double[] c;

    /**
     * Internal weights from Butcher array (without the first empty row).
     */
    private final double[][] a;

    /**
     * External weights for the high order method from Butcher array.
     */
    private final double[] b;

    /**
     * Stepsize control exponent.
     */
    private final double exp;

    /**
     * Safety factor for stepsize control.
     */
    private double safety;

    /**
     * Minimal reduction factor for stepsize control.
     */
    private double minReduction;

    /**
     * Maximal growth factor for stepsize control.
     */
    private double maxGrowth;

    /**
     * Build a Runge-Kutta integrator with the given Butcher array.
     *
     * @param name                  name of the method
     * @param fsal                  index of the pre-computed derivative for <i>fsal</i> methods
     *                              or -1 if method is not <i>fsal</i>
     * @param minStep               minimal step (sign is irrelevant, regardless of
     *                              integration direction, forward or backward), the last step can
     *                              be smaller than this
     * @param maxStep               maximal step (sign is irrelevant, regardless of
     *                              integration direction, forward or backward), the last step can
     *                              be smaller than this
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     */
    protected EmbeddedRungeKuttaIntegrator(final String name, final int fsal,
                                           final double minStep, final double maxStep,
                                           final double scalAbsoluteTolerance,
                                           final double scalRelativeTolerance) {

        super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);

        this.fsal = fsal;
        this.c = getC();
        this.a = getA();
        this.b = getB();

        exp = -1.0 / getOrder();

        // set the default values of the algorithm control parameters
        setSafety(0.9);
        setMinReduction(0.2);
        setMaxGrowth(10.0);

    }

    /**
     * Build a Runge-Kutta integrator with the given Butcher array.
     *
     * @param name                 name of the method
     * @param fsal                 index of the pre-computed derivative for <i>fsal</i> methods
     *                             or -1 if method is not <i>fsal</i>
     * @param minStep              minimal step (must be positive even for backward
     *                             integration), the last step can be smaller than this
     * @param maxStep              maximal step (must be positive even for backward
     *                             integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     */
    protected EmbeddedRungeKuttaIntegrator(final String name, final int fsal,
                                           final double minStep, final double maxStep,
                                           final double[] vecAbsoluteTolerance,
                                           final double[] vecRelativeTolerance) {

        super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);

        this.fsal = fsal;
        this.c = getC();
        this.a = getA();
        this.b = getB();

        exp = -1.0 / getOrder();

        // set the default values of the algorithm control parameters
        setSafety(0.9);
        setMinReduction(0.2);
        setMaxGrowth(10.0);

    }

    /**
     * Create an interpolator.
     *
     * @param forward             integration direction indicator
     * @param yDotK               slopes at the intermediate points
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param mapper              equations mapper for the all equations
     * @return external weights for the high order method from Butcher array
     */
    protected abstract RungeKuttaStateInterpolator createInterpolator(boolean forward, double[][] yDotK,
                                                                      ODEStateAndDerivative globalPreviousState,
                                                                      ODEStateAndDerivative globalCurrentState,
                                                                      EquationsMapper mapper);

    /**
     * Get the order of the method.
     *
     * @return order of the method
     */
    public abstract int getOrder();

    /**
     * Get the safety factor for stepsize control.
     *
     * @return safety factor
     */
    public double getSafety() {
        return safety;
    }

    /**
     * Set the safety factor for stepsize control.
     *
     * @param safety safety factor
     */
    public void setSafety(final double safety) {
        this.safety = safety;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative integrate(final ExpandableODE equations,
                                           final ODEState initialState, final double finalTime)
            throws MathIllegalArgumentException, MathIllegalStateException {

        sanityChecks(initialState, finalTime);
        setStepStart(initIntegration(equations, initialState, finalTime));
        final boolean forward = finalTime > initialState.getTime();

        // create some internal working arrays
        final int stages = c.length + 1;
        final double[][] yDotK = new double[stages][];
        final double[] yTmp = new double[equations.getMapper().getTotalDimension()];

        // set up integration control objects
        double hNew = 0;
        boolean firstTime = true;

        // main integration loop
        setIsLastStep(false);
        do {

            // iterate over step size, ensuring local normalized error is smaller than 1
            double error = 10;
            while (error >= 1.0) {

                // first stage
                final double[] y = getStepStart().getCompleteState();
                yDotK[0] = getStepStart().getCompleteDerivative();

                if (firstTime) {
                    final double[] scale = new double[mainSetDimension];
                    if (vecAbsoluteTolerance == null) {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = scalAbsoluteTolerance + scalRelativeTolerance * FastMath.abs(y[i]);
                        }
                    } else {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * FastMath.abs(y[i]);
                        }
                    }
                    hNew = initializeStep(forward, getOrder(), scale, getStepStart(), equations.getMapper());
                    firstTime = false;
                }

                setStepSize(hNew);
                if (forward) {
                    if (getStepStart().getTime() + getStepSize() >= finalTime) {
                        setStepSize(finalTime - getStepStart().getTime());
                    }
                } else {
                    if (getStepStart().getTime() + getStepSize() <= finalTime) {
                        setStepSize(finalTime - getStepStart().getTime());
                    }
                }

                // next stages
                for (int k = 1; k < stages; ++k) {

                    for (int j = 0; j < y.length; ++j) {
                        double sum = a[k - 1][0] * yDotK[0][j];
                        for (int l = 1; l < k; ++l) {
                            sum += a[k - 1][l] * yDotK[l][j];
                        }
                        yTmp[j] = y[j] + getStepSize() * sum;
                    }

                    yDotK[k] = computeDerivatives(getStepStart().getTime() + c[k - 1] * getStepSize(), yTmp);

                }

                // estimate the state at the end of the step
                for (int j = 0; j < y.length; ++j) {
                    double sum = b[0] * yDotK[0][j];
                    for (int l = 1; l < stages; ++l) {
                        sum += b[l] * yDotK[l][j];
                    }
                    yTmp[j] = y[j] + getStepSize() * sum;
                }

                // estimate the error at the end of the step
                error = estimateError(yDotK, y, yTmp, getStepSize());
                if (Double.isNaN(error)) {
                    throw new MathIllegalStateException(LocalizedODEFormats.NAN_APPEARING_DURING_INTEGRATION,
                            getStepStart().getTime() + getStepSize());
                }
                if (error >= 1.0) {
                    // reject the step and attempt to reduce error by stepsize control
                    final double factor =
                            FastMath.min(maxGrowth,
                                    FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
                    hNew = filterStep(getStepSize() * factor, forward, false);
                }

            }
            final double stepEnd = getStepStart().getTime() + getStepSize();
            final double[] yDotTmp = (fsal >= 0) ? yDotK[fsal] : computeDerivatives(stepEnd, yTmp);
            final ODEStateAndDerivative stateTmp = equations.getMapper().mapStateAndDerivative(stepEnd, yTmp, yDotTmp);

            // local error is small enough: accept the step, trigger events and step handlers
            setStepStart(acceptStep(createInterpolator(forward, yDotK, getStepStart(), stateTmp, equations.getMapper()),
                    finalTime));

            if (!isLastStep()) {

                // stepsize control for next step
                final double factor =
                        FastMath.min(maxGrowth, FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
                final double scaledH = getStepSize() * factor;
                final double nextT = getStepStart().getTime() + scaledH;
                final boolean nextIsLast = forward ? (nextT >= finalTime) : (nextT <= finalTime);
                hNew = filterStep(scaledH, forward, nextIsLast);

                final double filteredNextT = getStepStart().getTime() + hNew;
                final boolean filteredNextIsLast = forward ? (filteredNextT >= finalTime) : (filteredNextT <= finalTime);
                if (filteredNextIsLast) {
                    hNew = finalTime - getStepStart().getTime();
                }

            }

        } while (!isLastStep());

        final ODEStateAndDerivative finalState = getStepStart();
        resetInternalState();
        return finalState;

    }

    /**
     * Get the minimal reduction factor for stepsize control.
     *
     * @return minimal reduction factor
     */
    public double getMinReduction() {
        return minReduction;
    }

    /**
     * Set the minimal reduction factor for stepsize control.
     *
     * @param minReduction minimal reduction factor
     */
    public void setMinReduction(final double minReduction) {
        this.minReduction = minReduction;
    }

    /**
     * Get the maximal growth factor for stepsize control.
     *
     * @return maximal growth factor
     */
    public double getMaxGrowth() {
        return maxGrowth;
    }

    /**
     * Set the maximal growth factor for stepsize control.
     *
     * @param maxGrowth maximal growth factor
     */
    public void setMaxGrowth(final double maxGrowth) {
        this.maxGrowth = maxGrowth;
    }

    /**
     * Compute the error ratio.
     *
     * @param yDotK derivatives computed during the first stages
     * @param y0    estimate of the step at the start of the step
     * @param y1    estimate of the step at the end of the step
     * @param h     current step
     * @return error ratio, greater than 1 if step should be rejected
     */
    protected abstract double estimateError(double[][] yDotK,
                                            double[] y0, double[] y1,
                                            double h);

}
