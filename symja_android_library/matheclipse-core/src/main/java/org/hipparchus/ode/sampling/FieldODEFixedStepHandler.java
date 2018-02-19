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

package org.hipparchus.ode.sampling;

import org.hipparchus.RealFieldElement;
import org.hipparchus.ode.FieldODEStateAndDerivative;

/**
 * This interface represents a handler that should be called after
 * each successful fixed step.
 * <p>
 * <p>This interface should be implemented by anyone who is interested
 * in getting the solution of an ordinary differential equation at
 * fixed time steps. Objects implementing this interface should be
 * wrapped within an instance of {@link FieldStepNormalizer} that itself
 * is used as the general {@link FieldODEStepHandler} by the integrator. The
 * {@link FieldStepNormalizer} object is called according to the integrator
 * internal algorithms and it calls objects implementing this
 * interface as necessary at fixed time steps.</p>
 *
 * @param <T> the type of the field elements
 * @see FieldODEStepHandler
 * @see FieldStepNormalizer
 * @see FieldODEStateInterpolator
 */

public interface FieldODEFixedStepHandler<T extends RealFieldElement<T>> {

    /**
     * Initialize step handler at the start of an ODE integration.
     * <p>
     * This method is called once at the start of the integration. It
     * may be used by the step handler to initialize some internal data
     * if needed.
     * </p>
     * <p>
     * The default implementation does nothing.
     * </p>
     *
     * @param initialState initial time, state vector and derivative
     * @param finalTime    target time for the integration
     */
    default void init(FieldODEStateAndDerivative<T> initialState, T finalTime) {
        // nothing by default
    }

    /**
     * Handle the last accepted step
     *
     * @param state  current value of the independent <i>time</i> variable,
     *               state vector and derivative
     *               For efficiency purposes, the {@link FieldStepNormalizer} class reuses
     *               the same array on each call, so if
     *               the instance wants to keep it across all calls (for example to
     *               provide at the end of the integration a complete array of all
     *               steps), it should build a local copy store this copy.
     * @param isLast true if the step is the last one
     */
    void handleStep(FieldODEStateAndDerivative<T> state, boolean isLast);

}
