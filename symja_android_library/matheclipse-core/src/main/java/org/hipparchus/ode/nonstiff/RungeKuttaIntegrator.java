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
import org.hipparchus.ode.AbstractIntegrator;
import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.LocalizedODEFormats;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.util.FastMath;

/**
 * This class implements the common part of all fixed step Runge-Kutta
 * integrators for Ordinary Differential Equations.
 * <p>
 * <p>These methods are explicit Runge-Kutta methods, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 * </pre>
 * </p>
 *
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see MidpointIntegrator
 */

public abstract class RungeKuttaIntegrator extends AbstractIntegrator implements ButcherArrayProvider {

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
     * Integration step.
     */
    private final double step;

    /**
     * Simple constructor.
     * Build a Runge-Kutta integrator with the given
     * step. The default step handler does nothing.
     *
     * @param name name of the method
     * @param step integration step
     */
    protected RungeKuttaIntegrator(final String name, final double step) {
        super(name);
        this.c = getC();
        this.a = getA();
        this.b = getB();
        this.step = FastMath.abs(step);
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
        double[] y = getStepStart().getCompleteState();
        final double[][] yDotK = new double[stages][];
        final double[] yTmp = new double[y.length];

        // set up integration control objects
        if (forward) {
            if (getStepStart().getTime() + step >= finalTime) {
                setStepSize(finalTime - getStepStart().getTime());
            } else {
                setStepSize(step);
            }
        } else {
            if (getStepStart().getTime() - step <= finalTime) {
                setStepSize(finalTime - getStepStart().getTime());
            } else {
                setStepSize(-step);
            }
        }

        // main integration loop
        setIsLastStep(false);
        do {

            // first stage
            y = getStepStart().getCompleteState();
            yDotK[0] = getStepStart().getCompleteDerivative();

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
                if (Double.isNaN(yTmp[j])) {
                    throw new MathIllegalStateException(LocalizedODEFormats.NAN_APPEARING_DURING_INTEGRATION,
                            getStepStart().getTime() + getStepSize());
                }

            }
            final double stepEnd = getStepStart().getTime() + getStepSize();
            final double[] yDotTmp = computeDerivatives(stepEnd, yTmp);
            final ODEStateAndDerivative stateTmp =
                    equations.getMapper().mapStateAndDerivative(stepEnd, yTmp, yDotTmp);

            // discrete events handling
            System.arraycopy(yTmp, 0, y, 0, y.length);
            setStepStart(acceptStep(createInterpolator(forward, yDotK, getStepStart(), stateTmp,
                    equations.getMapper()),
                    finalTime));

            if (!isLastStep()) {

                // stepsize control for next step
                final double nextT = getStepStart().getTime() + getStepSize();
                final boolean nextIsLast = forward ? (nextT >= finalTime) : (nextT <= finalTime);
                if (nextIsLast) {
                    setStepSize(finalTime - getStepStart().getTime());
                }
            }

        } while (!isLastStep());

        final ODEStateAndDerivative finalState = getStepStart();
        setStepStart(null);
        setStepSize(Double.NaN);
        return finalState;

    }

    /**
     * Fast computation of a single step of ODE integration.
     * <p>This method is intended for the limited use case of
     * very fast computation of only one step without using any of the
     * rich features of general integrators that may take some time
     * to set up (i.e. no step handlers, no events handlers, no additional
     * states, no interpolators, no error control, no evaluations count,
     * no sanity checks ...). It handles the strict minimum of computation,
     * so it can be embedded in outer loops.</p>
     * <p>
     * This method is <em>not</em> used at all by the {@link #integrate(ExpandableODE, ODEState, double)}
     * method. It also completely ignores the step set at construction time, and
     * uses only a single step to go from {@code t0} to {@code t}.
     * </p>
     * <p>
     * As this method does not use any of the state-dependent features of the integrator,
     * it should be reasonably thread-safe <em>if and only if</em> the provided differential
     * equations are themselves thread-safe.
     * </p>
     *
     * @param equations differential equations to integrate
     * @param t0        initial time
     * @param y0        initial value of the state vector at t0
     * @param t         target time for the integration
     *                  (can be set to a value smaller than {@code t0} for backward integration)
     * @return state vector at {@code t}
     */
    public double[] singleStep(final OrdinaryDifferentialEquation equations,
                               final double t0, final double[] y0, final double t) {

        // create some internal working arrays
        final double[] y = y0.clone();
        final int stages = c.length + 1;
        final double[][] yDotK = new double[stages][];
        final double[] yTmp = y0.clone();

        // first stage
        final double h = t - t0;
        yDotK[0] = equations.computeDerivatives(t0, y);

        // next stages
        for (int k = 1; k < stages; ++k) {

            for (int j = 0; j < y0.length; ++j) {
                double sum = a[k - 1][0] * yDotK[0][j];
                for (int l = 1; l < k; ++l) {
                    sum += a[k - 1][l] * yDotK[l][j];
                }
                yTmp[j] = y[j] + h * sum;
            }

            yDotK[k] = equations.computeDerivatives(t0 + c[k - 1] * h, yTmp);

        }

        // estimate the state at the end of the step
        for (int j = 0; j < y0.length; ++j) {
            double sum = b[0] * yDotK[0][j];
            for (int l = 1; l < stages; ++l) {
                sum += b[l] * yDotK[l][j];
            }
            y[j] += h * sum;
        }

        return y;

    }

}
