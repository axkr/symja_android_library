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
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.MultistepIntegrator;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;


/**
 * Base class for {@link AdamsBashforthIntegrator Adams-Bashforth} and
 * {@link AdamsMoultonIntegrator Adams-Moulton} integrators.
 */
public abstract class AdamsIntegrator extends MultistepIntegrator {

    /**
     * Transformer.
     */
    private final AdamsNordsieckTransformer transformer;

    /**
     * Build an Adams integrator with the given order and step control parameters.
     *
     * @param name                  name of the method
     * @param nSteps                number of steps of the method excluding the one being computed
     * @param order                 order of the method
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
    public AdamsIntegrator(final String name, final int nSteps, final int order,
                           final double minStep, final double maxStep,
                           final double scalAbsoluteTolerance,
                           final double scalRelativeTolerance)
            throws MathIllegalArgumentException {
        super(name, nSteps, order, minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
        transformer = AdamsNordsieckTransformer.getInstance(nSteps);
    }

    /**
     * Build an Adams integrator with the given order and step control parameters.
     *
     * @param name                 name of the method
     * @param nSteps               number of steps of the method excluding the one being computed
     * @param order                order of the method
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
    public AdamsIntegrator(final String name, final int nSteps, final int order,
                           final double minStep, final double maxStep,
                           final double[] vecAbsoluteTolerance,
                           final double[] vecRelativeTolerance)
            throws IllegalArgumentException {
        super(name, nSteps, order, minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
        transformer = AdamsNordsieckTransformer.getInstance(nSteps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ODEStateAndDerivative integrate(final ExpandableODE equations,
                                                    final ODEState initialState, final double finalTime)
            throws MathIllegalArgumentException, MathIllegalStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Array2DRowRealMatrix initializeHighOrderDerivatives(final double h, final double[] t,
                                                                  final double[][] y,
                                                                  final double[][] yDot) {
        return transformer.initializeHighOrderDerivatives(h, t, y, yDot);
    }

    /**
     * Update the high order scaled derivatives for Adams integrators (phase 1).
     * <p>The complete update of high order derivatives has a form similar to:
     * <pre>
     * r<sub>n+1</sub> = (s<sub>1</sub>(n) - s<sub>1</sub>(n+1)) P<sup>-1</sup> u + P<sup>-1</sup> A P r<sub>n</sub>
     * </pre>
     * this method computes the P<sup>-1</sup> A P r<sub>n</sub> part.</p>
     *
     * @param highOrder high order scaled derivatives
     *                  (h<sup>2</sup>/2 y'', ... h<sup>k</sup>/k! y(k))
     * @return updated high order derivatives
     * @see #updateHighOrderDerivativesPhase2(double[], double[], Array2DRowRealMatrix)
     */
    public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final Array2DRowRealMatrix highOrder) {
        return transformer.updateHighOrderDerivativesPhase1(highOrder);
    }

    /**
     * Update the high order scaled derivatives Adams integrators (phase 2).
     * <p>The complete update of high order derivatives has a form similar to:
     * <pre>
     * r<sub>n+1</sub> = (s<sub>1</sub>(n) - s<sub>1</sub>(n+1)) P<sup>-1</sup> u + P<sup>-1</sup> A P r<sub>n</sub>
     * </pre>
     * this method computes the (s<sub>1</sub>(n) - s<sub>1</sub>(n+1)) P<sup>-1</sup> u part.</p>
     * <p>Phase 1 of the update must already have been performed.</p>
     *
     * @param start     first order scaled derivatives at step start
     * @param end       first order scaled derivatives at step end
     * @param highOrder high order scaled derivatives, will be modified
     *                  (h<sup>2</sup>/2 y'', ... h<sup>k</sup>/k! y(k))
     * @see #updateHighOrderDerivativesPhase1(Array2DRowRealMatrix)
     */
    public void updateHighOrderDerivativesPhase2(final double[] start,
                                                 final double[] end,
                                                 final Array2DRowRealMatrix highOrder) {
        transformer.updateHighOrderDerivativesPhase2(start, end, highOrder);
    }

}
