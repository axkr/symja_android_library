/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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

package org.hipparchus.ode.events;

import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;

import java.util.Arrays;

/**
 * Wrapper used to detect only increasing or decreasing events.
 * <p>
 * <p>General {@link ODEEventHandler events} are defined implicitly
 * by a {@link ODEEventHandler#g(ODEStateAndDerivative) g function} crossing
 * zero. This function needs to be continuous in the event neighborhood,
 * and its sign must remain consistent between events. This implies that
 * during an ODE integration, events triggered are alternately events
 * for which the function increases from negative to positive values,
 * and events for which the function decreases from positive to
 * negative values.
 * </p>
 * <p>
 * <p>Sometimes, users are only interested in one type of event (say
 * increasing events for example) and not in the other type. In these
 * cases, looking precisely for all events location and triggering
 * events that will later be ignored is a waste of computing time.</p>
 * <p>
 * <p>Users can wrap a regular {@link ODEEventHandler event handler} in
 * an instance of this class and provide this wrapping instance to
 * the {@link org.hipparchus.ode.ODEIntegrator ODE solver}
 * in order to avoid wasting time looking for uninteresting events.
 * The wrapper will intercept the calls to the {@link
 * ODEEventHandler#g(ODEStateAndDerivative) g function} and to the {@link
 * ODEEventHandler#eventOccurred(ODEStateAndDerivative, boolean)
 * eventOccurred} method in order to ignore uninteresting events. The
 * wrapped regular {@link ODEEventHandler event handler} will the see only
 * the interesting events, i.e. either only {@code increasing} events or
 * {@code decreasing} events. the number of calls to the {@link
 * ODEEventHandler#g(ODEStateAndDerivative) g function} will also be reduced.</p>
 */

public class EventFilter implements ODEEventHandler {

    /**
     * Number of past transformers updates stored.
     */
    private static final int HISTORY_SIZE = 100;

    /**
     * Wrapped event handler.
     */
    private final ODEEventHandler rawHandler;

    /**
     * Filter to use.
     */
    private final FilterType filter;

    /**
     * Transformers of the g function.
     */
    private final Transformer[] transformers;

    /**
     * Update time of the transformers.
     */
    private final double[] updates;

    /**
     * Indicator for forward integration.
     */
    private boolean forward;

    /**
     * Extreme time encountered so far.
     */
    private double extremeT;

    /**
     * Wrap an {@link ODEEventHandler event handler}.
     *
     * @param rawHandler event handler to wrap
     * @param filter     filter to use
     */
    public EventFilter(final ODEEventHandler rawHandler, final FilterType filter) {
        this.rawHandler = rawHandler;
        this.filter = filter;
        this.transformers = new Transformer[HISTORY_SIZE];
        this.updates = new double[HISTORY_SIZE];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ODEStateAndDerivative initialState, double finalTime) {

        // delegate to raw handler
        rawHandler.init(initialState, finalTime);

        // initialize events triggering logic
        forward = finalTime >= initialState.getTime();
        extremeT = forward ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Arrays.fill(transformers, Transformer.UNINITIALIZED);
        Arrays.fill(updates, extremeT);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double g(final ODEStateAndDerivative state) {

        final double rawG = rawHandler.g(state);

        // search which transformer should be applied to g
        if (forward) {
            final int last = transformers.length - 1;
            if (extremeT < state.getTime()) {
                // we are at the forward end of the history

                // check if a new rough root has been crossed
                final Transformer previous = transformers[last];
                final Transformer next = filter.selectTransformer(previous, rawG, forward);
                if (next != previous) {
                    // there is a root somewhere between extremeT and t.
                    // the new transformer is valid for t (this is how we have just computed
                    // it above), but it is in fact valid on both sides of the root, so
                    // it was already valid before t and even up to previous time. We store
                    // the switch at extremeT for safety, to ensure the previous transformer
                    // is not applied too close of the root
                    System.arraycopy(updates, 1, updates, 0, last);
                    System.arraycopy(transformers, 1, transformers, 0, last);
                    updates[last] = extremeT;
                    transformers[last] = next;
                }

                extremeT = state.getTime();

                // apply the transform
                return next.transformed(rawG);

            } else {
                // we are in the middle of the history

                // select the transformer
                for (int i = last; i > 0; --i) {
                    if (updates[i] <= state.getTime()) {
                        // apply the transform
                        return transformers[i].transformed(rawG);
                    }
                }

                return transformers[0].transformed(rawG);

            }
        } else {
            if (state.getTime() < extremeT) {
                // we are at the backward end of the history

                // check if a new rough root has been crossed
                final Transformer previous = transformers[0];
                final Transformer next = filter.selectTransformer(previous, rawG, forward);
                if (next != previous) {
                    // there is a root somewhere between extremeT and t.
                    // the new transformer is valid for t (this is how we have just computed
                    // it above), but it is in fact valid on both sides of the root, so
                    // it was already valid before t and even up to previous time. We store
                    // the switch at extremeT for safety, to ensure the previous transformer
                    // is not applied too close of the root
                    System.arraycopy(updates, 0, updates, 1, updates.length - 1);
                    System.arraycopy(transformers, 0, transformers, 1, transformers.length - 1);
                    updates[0] = extremeT;
                    transformers[0] = next;
                }

                extremeT = state.getTime();

                // apply the transform
                return next.transformed(rawG);

            } else {
                // we are in the middle of the history

                // select the transformer
                for (int i = 0; i < updates.length - 1; ++i) {
                    if (state.getTime() <= updates[i]) {
                        // apply the transform
                        return transformers[i].transformed(rawG);
                    }
                }

                return transformers[updates.length - 1].transformed(rawG);

            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action eventOccurred(final ODEStateAndDerivative state, final boolean increasing) {
        // delegate to raw handler, fixing increasing status on the fly
        return rawHandler.eventOccurred(state, filter.getTriggeredIncreasing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEState resetState(final ODEStateAndDerivative state) {
        // delegate to raw handler
        return rawHandler.resetState(state);
    }

}
