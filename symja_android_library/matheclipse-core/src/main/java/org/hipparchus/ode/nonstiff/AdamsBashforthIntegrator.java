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
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.LocalizedODEFormats;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.util.FastMath;


/**
 * This class implements explicit Adams-Bashforth integrators for Ordinary
 * Differential Equations.
 * <p>
 * <p>Adams-Bashforth methods (in fact due to Adams alone) are explicit
 * multistep ODE solvers. This implementation is a variation of the classical
 * one: it uses adaptive stepsize to implement error control, whereas
 * classical implementations are fixed step size. The value of state vector
 * at step n+1 is a simple combination of the value at step n and of the
 * derivatives at steps n, n-1, n-2 ... Depending on the number k of previous
 * steps one wants to use for computing the next value, different formulas
 * are available:</p>
 * <ul>
 * <li>k = 1: y<sub>n+1</sub> = y<sub>n</sub> + h y'<sub>n</sub></li>
 * <li>k = 2: y<sub>n+1</sub> = y<sub>n</sub> + h (3y'<sub>n</sub>-y'<sub>n-1</sub>)/2</li>
 * <li>k = 3: y<sub>n+1</sub> = y<sub>n</sub> + h (23y'<sub>n</sub>-16y'<sub>n-1</sub>+5y'<sub>n-2</sub>)/12</li>
 * <li>k = 4: y<sub>n+1</sub> = y<sub>n</sub> + h (55y'<sub>n</sub>-59y'<sub>n-1</sub>+37y'<sub>n-2</sub>-9y'<sub>n-3</sub>)/24</li>
 * <li>...</li>
 * </ul>
 * <p>
 * <p>A k-steps Adams-Bashforth method is of order k.</p>
 * <p>
 * <p> There must be sufficient time for the {@link #setStarterIntegrator(org.hipparchus.ode.ODEIntegrator)
 * starter integrator} to take several steps between the the last reset event, and the end
 * of integration, otherwise an exception may be thrown during integration. The user can
 * adjust the end date of integration, or the step size of the starter integrator to
 * ensure a sufficient number of steps can be completed before the end of integration.
 * </p>
 * <p>
 * <h3>Implementation details</h3>
 * <p>
 * <p>We define scaled derivatives s<sub>i</sub>(n) at step n as:
 * <pre>
 * s<sub>1</sub>(n) = h y'<sub>n</sub> for first derivative
 * s<sub>2</sub>(n) = h<sup>2</sup>/2 y''<sub>n</sub> for second derivative
 * s<sub>3</sub>(n) = h<sup>3</sup>/6 y'''<sub>n</sub> for third derivative
 * ...
 * s<sub>k</sub>(n) = h<sup>k</sup>/k! y<sup>(k)</sup><sub>n</sub> for k<sup>th</sup> derivative
 * </pre></p>
 *
 * <p>The definitions above use the classical representation with several previous first
 * derivatives. Lets define
 * <pre>
 *   q<sub>n</sub> = [ s<sub>1</sub>(n-1) s<sub>1</sub>(n-2) ... s<sub>1</sub>(n-(k-1)) ]<sup>T</sup>
 * </pre>
 * (we omit the k index in the notation for clarity). With these definitions,
 * Adams-Bashforth methods can be written:
 * <ul>
 * <li>k = 1: y<sub>n+1</sub> = y<sub>n</sub> + s<sub>1</sub>(n)</li>
 * <li>k = 2: y<sub>n+1</sub> = y<sub>n</sub> + 3/2 s<sub>1</sub>(n) + [ -1/2 ] q<sub>n</sub></li>
 * <li>k = 3: y<sub>n+1</sub> = y<sub>n</sub> + 23/12 s<sub>1</sub>(n) + [ -16/12 5/12 ] q<sub>n</sub></li>
 * <li>k = 4: y<sub>n+1</sub> = y<sub>n</sub> + 55/24 s<sub>1</sub>(n) + [ -59/24 37/24 -9/24 ] q<sub>n</sub></li>
 * <li>...</li>
 * </ul></p>
 * <p>
 * <p>Instead of using the classical representation with first derivatives only (y<sub>n</sub>,
 * s<sub>1</sub>(n) and q<sub>n</sub>), our implementation uses the Nordsieck vector with
 * higher degrees scaled derivatives all taken at the same step (y<sub>n</sub>, s<sub>1</sub>(n)
 * and r<sub>n</sub>) where r<sub>n</sub> is defined as:
 * <pre>
 * r<sub>n</sub> = [ s<sub>2</sub>(n), s<sub>3</sub>(n) ... s<sub>k</sub>(n) ]<sup>T</sup>
 * </pre>
 * (here again we omit the k index in the notation for clarity)
 * </p>
 * <p>
 * <p>Taylor series formulas show that for any index offset i, s<sub>1</sub>(n-i) can be
 * computed from s<sub>1</sub>(n), s<sub>2</sub>(n) ... s<sub>k</sub>(n), the formula being exact
 * for degree k polynomials.
 * <pre>
 * s<sub>1</sub>(n-i) = s<sub>1</sub>(n) + &sum;<sub>j&gt;0</sub> (j+1) (-i)<sup>j</sup> s<sub>j+1</sub>(n)
 * </pre>
 * The previous formula can be used with several values for i to compute the transform between
 * classical representation and Nordsieck vector. The transform between r<sub>n</sub>
 * and q<sub>n</sub> resulting from the Taylor series formulas above is:
 * <pre>
 * q<sub>n</sub> = s<sub>1</sub>(n) u + P r<sub>n</sub>
 * </pre>
 * where u is the [ 1 1 ... 1 ]<sup>T</sup> vector and P is the (k-1)&times;(k-1) matrix built
 * with the (j+1) (-i)<sup>j</sup> terms with i being the row number starting from 1 and j being
 * the column number starting from 1:
 * <pre>
 *        [  -2   3   -4    5  ... ]
 *        [  -4  12  -32   80  ... ]
 *   P =  [  -6  27 -108  405  ... ]
 *        [  -8  48 -256 1280  ... ]
 *        [          ...           ]
 * </pre></p>
 *
 * <p>Using the Nordsieck vector has several advantages:
 * <ul>
 * <li>it greatly simplifies step interpolation as the interpolator mainly applies
 * Taylor series formulas,</li>
 * <li>it simplifies step changes that occur when discrete events that truncate
 * the step are triggered,</li>
 * <li>it allows to extend the methods in order to support adaptive stepsize.</li>
 * </ul></p>
 *
 * <p>The Nordsieck vector at step n+1 is computed from the Nordsieck vector at step n as follows:
 * <ul>
 * <li>y<sub>n+1</sub> = y<sub>n</sub> + s<sub>1</sub>(n) + u<sup>T</sup> r<sub>n</sub></li>
 * <li>s<sub>1</sub>(n+1) = h f(t<sub>n+1</sub>, y<sub>n+1</sub>)</li>
 * <li>r<sub>n+1</sub> = (s<sub>1</sub>(n) - s<sub>1</sub>(n+1)) P<sup>-1</sup> u + P<sup>-1</sup> A P r<sub>n</sub></li>
 * </ul>
 * where A is a rows shifting matrix (the lower left part is an identity matrix):
 * <pre>
 *        [ 0 0   ...  0 0 | 0 ]
 *        [ ---------------+---]
 *        [ 1 0   ...  0 0 | 0 ]
 *    A = [ 0 1   ...  0 0 | 0 ]
 *        [       ...      | 0 ]
 *        [ 0 0   ...  1 0 | 0 ]
 *        [ 0 0   ...  0 1 | 0 ]
 * </pre></p>
 *
 * <p>The P<sup>-1</sup>u vector and the P<sup>-1</sup> A P matrix do not depend on the state,
 * they only depend on k and therefore are precomputed once for all.</p>
 */
public class AdamsBashforthIntegrator extends AdamsIntegrator {

    /**
     * Integrator method name.
     */
    private static final String METHOD_NAME = "Adams-Bashforth";

    /**
     * Build an Adams-Bashforth integrator with the given order and step control parameters.
     *
     * @param nSteps                number of steps of the method excluding the one being computed
     * @param minStep               minimal step (sign is irrelevant, regardless of
     *                              integration direction, forward or backward), the last step can
     *                              be smaller than this
     * @param maxStep               maximal step (sign is irrelevant, regardless of
     *                              integration direction, forward or backward), the last step can
     *                              be smaller than this
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @throws MathIllegalArgumentException if order is 1 or less
     */
    public AdamsBashforthIntegrator(final int nSteps,
                                    final double minStep, final double maxStep,
                                    final double scalAbsoluteTolerance,
                                    final double scalRelativeTolerance)
            throws MathIllegalArgumentException {
        super(METHOD_NAME, nSteps, nSteps, minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Build an Adams-Bashforth integrator with the given order and step control parameters.
     *
     * @param nSteps               number of steps of the method excluding the one being computed
     * @param minStep              minimal step (sign is irrelevant, regardless of
     *                             integration direction, forward or backward), the last step can
     *                             be smaller than this
     * @param maxStep              maximal step (sign is irrelevant, regardless of
     *                             integration direction, forward or backward), the last step can
     *                             be smaller than this
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @throws IllegalArgumentException if order is 1 or less
     */
    public AdamsBashforthIntegrator(final int nSteps,
                                    final double minStep, final double maxStep,
                                    final double[] vecAbsoluteTolerance,
                                    final double[] vecRelativeTolerance)
            throws IllegalArgumentException {
        super(METHOD_NAME, nSteps, nSteps, minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /**
     * Estimate error.
     * <p>
     * Error is estimated by interpolating back to previous state using
     * the state Taylor expansion and comparing to real previous state.
     * </p>
     *
     * @param previousState      state vector at step start
     * @param predictedState     predicted state vector at step end
     * @param predictedScaled    predicted value of the scaled derivatives at step end
     * @param predictedNordsieck predicted value of the Nordsieck vector at step end
     * @return estimated normalized local discretization error
     */
    private double errorEstimation(final double[] previousState,
                                   final double[] predictedState,
                                   final double[] predictedScaled,
                                   final RealMatrix predictedNordsieck) {

        double error = 0;
        for (int i = 0; i < mainSetDimension; ++i) {
            final double yScale = FastMath.abs(predictedState[i]);
            final double tol = (vecAbsoluteTolerance == null) ?
                    (scalAbsoluteTolerance + scalRelativeTolerance * yScale) :
                    (vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * yScale);

            // apply Taylor formula from high order to low order,
            // for the sake of numerical accuracy
            double variation = 0;
            int sign = predictedNordsieck.getRowDimension() % 2 == 0 ? -1 : 1;
            for (int k = predictedNordsieck.getRowDimension() - 1; k >= 0; --k) {
                variation += sign * predictedNordsieck.getEntry(k, i);
                sign = -sign;
            }
            variation -= predictedScaled[i];

            final double ratio = (predictedState[i] - previousState[i] + variation) / tol;
            error += ratio * ratio;

        }

        return FastMath.sqrt(error / mainSetDimension);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative integrate(final ExpandableODE equations,
                                           final ODEState initialState,
                                           final double finalTime)
            throws MathIllegalArgumentException, MathIllegalStateException {

        sanityChecks(initialState, finalTime);
        setStepStart(initIntegration(equations, initialState, finalTime));
        final boolean forward = finalTime > initialState.getTime();

        // compute the initial Nordsieck vector using the configured starter integrator
        start(equations, getStepStart(), finalTime);

        // reuse the step that was chosen by the starter integrator
        ODEStateAndDerivative stepEnd =
                AdamsStateInterpolator.taylor(equations.getMapper(), getStepStart(),
                        getStepStart().getTime() + getStepSize(),
                        getStepSize(), scaled, nordsieck);

        // main integration loop
        setIsLastStep(false);
        final double[] y = getStepStart().getCompleteState();
        do {

            double[] predictedY = null;
            final double[] predictedScaled = new double[y.length];
            Array2DRowRealMatrix predictedNordsieck = null;
            double error = 10;
            while (error >= 1.0) {

                // predict a first estimate of the state at step end
                predictedY = stepEnd.getCompleteState();

                // evaluate the derivative
                final double[] yDot = computeDerivatives(stepEnd.getTime(), predictedY);

                // predict Nordsieck vector at step end
                for (int j = 0; j < predictedScaled.length; ++j) {
                    predictedScaled[j] = getStepSize() * yDot[j];
                }
                predictedNordsieck = updateHighOrderDerivativesPhase1(nordsieck);
                updateHighOrderDerivativesPhase2(scaled, predictedScaled, predictedNordsieck);

                // evaluate error
                error = errorEstimation(y, predictedY, predictedScaled, predictedNordsieck);
                if (Double.isNaN(error)) {
                    throw new MathIllegalStateException(LocalizedODEFormats.NAN_APPEARING_DURING_INTEGRATION,
                            stepEnd.getTime());
                }

                if (error >= 1.0) {
                    // reject the step and attempt to reduce error by stepsize control
                    final double factor = computeStepGrowShrinkFactor(error);
                    rescale(filterStep(getStepSize() * factor, forward, false));
                    stepEnd = AdamsStateInterpolator.taylor(equations.getMapper(), getStepStart(),
                            getStepStart().getTime() + getStepSize(),
                            getStepSize(),
                            scaled,
                            nordsieck);

                }
            }

            // discrete events handling
            setStepStart(acceptStep(new AdamsStateInterpolator(getStepSize(), stepEnd,
                            predictedScaled, predictedNordsieck, forward,
                            getStepStart(), stepEnd,
                            equations.getMapper()),
                    finalTime));
            scaled = predictedScaled;
            nordsieck = predictedNordsieck;

            if (!isLastStep()) {

                if (resetOccurred()) {

                    // some events handler has triggered changes that
                    // invalidate the derivatives, we need to restart from scratch
                    start(equations, getStepStart(), finalTime);

                    final double nextT = getStepStart().getTime() + getStepSize();
                    final boolean nextIsLast = forward ?
                            (nextT >= finalTime) :
                            (nextT <= finalTime);
                    final double hNew = nextIsLast ? finalTime - getStepStart().getTime() : getStepSize();

                    rescale(hNew);
                    System.arraycopy(getStepStart().getCompleteState(), 0, y, 0, y.length);

                } else {

                    // stepsize control for next step
                    final double factor = computeStepGrowShrinkFactor(error);
                    final double scaledH = getStepSize() * factor;
                    final double nextT = getStepStart().getTime() + scaledH;
                    final boolean nextIsLast = forward ?
                            (nextT >= finalTime) :
                            (nextT <= finalTime);
                    double hNew = filterStep(scaledH, forward, nextIsLast);

                    final double filteredNextT = getStepStart().getTime() + hNew;
                    final boolean filteredNextIsLast = forward ? (filteredNextT >= finalTime) : (filteredNextT <= finalTime);
                    if (filteredNextIsLast) {
                        hNew = finalTime - getStepStart().getTime();
                    }

                    rescale(hNew);
                    System.arraycopy(predictedY, 0, y, 0, y.length);

                }

                stepEnd = AdamsStateInterpolator.taylor(equations.getMapper(), getStepStart(), getStepStart().getTime() + getStepSize(),
                        getStepSize(), scaled, nordsieck);

            }

        } while (!isLastStep());

        final ODEStateAndDerivative finalState = getStepStart();
        setStepStart(null);
        setStepSize(Double.NaN);
        return finalState;

    }

}
