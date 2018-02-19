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

package org.hipparchus.ode;

import org.hipparchus.Field;
import org.hipparchus.RealFieldElement;
import org.hipparchus.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.hipparchus.analysis.solvers.FieldBracketingNthOrderBrentSolver;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.events.FieldEventState;
import org.hipparchus.ode.events.FieldEventState.EventOccurrence;
import org.hipparchus.ode.events.FieldODEEventHandler;
import org.hipparchus.ode.sampling.AbstractFieldODEStateInterpolator;
import org.hipparchus.ode.sampling.FieldODEStepHandler;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.Incrementor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Base class managing common boilerplate for all integrators.
 *
 * @param <T> the type of the field elements
 */
public abstract class AbstractFieldIntegrator<T extends RealFieldElement<T>> implements FieldODEIntegrator<T> {

    /**
     * Default relative accuracy.
     */
    private static final double DEFAULT_RELATIVE_ACCURACY = 0;

    /**
     * Default function value accuracy.
     */
    private static final double DEFAULT_FUNCTION_VALUE_ACCURACY = 0;
    /**
     * Field to which the time and state vector elements belong.
     */
    private final Field<T> field;
    /**
     * Name of the method.
     */
    private final String name;
    /**
     * Step handler.
     */
    private Collection<FieldODEStepHandler<T>> stepHandlers;
    /**
     * Current step start.
     */
    private FieldODEStateAndDerivative<T> stepStart;
    /**
     * Current stepsize.
     */
    private T stepSize;
    /**
     * Indicator for last step.
     */
    private boolean isLastStep;
    /**
     * Indicator that a state or derivative reset was triggered by some event.
     */
    private boolean resetOccurred;
    /**
     * Events states.
     */
    private Collection<FieldEventState<T>> eventsStates;
    /**
     * Initialization indicator of events states.
     */
    private boolean statesInitialized;
    /**
     * Counter for number of evaluations.
     */
    private Incrementor evaluations;

    /**
     * Differential equations to integrate.
     */
    private transient FieldExpandableODE<T> equations;

    /**
     * Build an instance.
     *
     * @param field field to which the time and state vector elements belong
     * @param name  name of the method
     */
    protected AbstractFieldIntegrator(final Field<T> field, final String name) {
        this.field = field;
        this.name = name;
        stepHandlers = new ArrayList<FieldODEStepHandler<T>>();
        stepStart = null;
        stepSize = null;
        eventsStates = new ArrayList<FieldEventState<T>>();
        statesInitialized = false;
        evaluations = new Incrementor();
    }

    /**
     * Get the field to which state vector elements belong.
     *
     * @return field to which state vector elements belong
     */
    public Field<T> getField() {
        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStepHandler(final FieldODEStepHandler<T> handler) {
        stepHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<FieldODEStepHandler<T>> getStepHandlers() {
        return Collections.unmodifiableCollection(stepHandlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearStepHandlers() {
        stepHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventHandler(final FieldODEEventHandler<T> handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount) {
        addEventHandler(handler, maxCheckInterval, convergence,
                maxIterationCount,
                new FieldBracketingNthOrderBrentSolver<T>(field.getZero().add(DEFAULT_RELATIVE_ACCURACY),
                        field.getZero().add(convergence),
                        field.getZero().add(DEFAULT_FUNCTION_VALUE_ACCURACY),
                        5));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventHandler(final FieldODEEventHandler<T> handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount,
                                final BracketedRealFieldUnivariateSolver<T> solver) {
        eventsStates.add(new FieldEventState<T>(handler, maxCheckInterval, field.getZero().add(convergence),
                maxIterationCount, solver));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<FieldODEEventHandler<T>> getEventHandlers() {
        final List<FieldODEEventHandler<T>> list = new ArrayList<FieldODEEventHandler<T>>(eventsStates.size());
        for (FieldEventState<T> state : eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEventHandlers() {
        eventsStates.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getCurrentSignedStepsize() {
        return stepSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxEvaluations() {
        return evaluations.getMaximalCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxEvaluations(int maxEvaluations) {
        evaluations = evaluations.withMaximalCount((maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEvaluations() {
        return evaluations.getCount();
    }

    /**
     * Prepare the start of an integration.
     *
     * @param eqn equations to integrate
     * @param s0  initial state vector
     * @param t   target time for the integration
     * @return initial state with derivatives added
     */
    protected FieldODEStateAndDerivative<T> initIntegration(final FieldExpandableODE<T> eqn,
                                                            final FieldODEState<T> s0, final T t) {

        this.equations = eqn;
        evaluations = evaluations.withCount(0);

        // initialize ODE
        eqn.init(s0, t);

        // set up derivatives of initial state (including primary and secondary components)
        final T t0 = s0.getTime();
        final T[] y0 = s0.getCompleteState();
        final T[] y0Dot = computeDerivatives(t0, y0);

        // built the state
        final FieldODEStateAndDerivative<T> s0WithDerivatives =
                eqn.getMapper().mapStateAndDerivative(t0, y0, y0Dot);

        // initialize event handlers
        for (final FieldEventState<T> state : eventsStates) {
            state.getEventHandler().init(s0WithDerivatives, t);
        }

        // initialize step handlers
        for (FieldODEStepHandler<T> handler : stepHandlers) {
            handler.init(s0WithDerivatives, t);
        }

        setStateInitialized(false);

        return s0WithDerivatives;

    }

    /**
     * Get the differential equations to integrate.
     *
     * @return differential equations to integrate
     */
    protected FieldExpandableODE<T> getEquations() {
        return equations;
    }

    /**
     * Get the evaluations counter.
     *
     * @return evaluations counter
     */
    protected Incrementor getEvaluationsCounter() {
        return evaluations;
    }

    /**
     * Compute the derivatives and check the number of evaluations.
     *
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @return state completed with derivatives
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws NullPointerException         if the ODE equations have not been set (i.e. if this method
     *                                      is called outside of a call to {@link #integrate(FieldExpandableODE, FieldODEState,
     *                                      RealFieldElement) integrate}
     */
    public T[] computeDerivatives(final T t, final T[] y)
            throws MathIllegalArgumentException, MathIllegalStateException, NullPointerException {
        evaluations.increment();
        return equations.computeDerivatives(t, y);
    }

    /**
     * Set the stateInitialized flag.
     * <p>This method must be called by integrators with the value
     * {@code false} before they start integration, so a proper lazy
     * initialization is done automatically on the first step.</p>
     *
     * @param stateInitialized new value for the flag
     */
    protected void setStateInitialized(final boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }

    /**
     * Accept a step, triggering events and step handlers.
     *
     * @param interpolator step interpolator
     * @param tEnd         final integration time
     * @return state at end of step
     * @throws MathIllegalStateException    if the interpolator throws one because the
     *                                      number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the location of an event cannot be
     *                                      bracketed
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations
     *                                      settings
     */
    protected FieldODEStateAndDerivative<T> acceptStep(final AbstractFieldODEStateInterpolator<T> interpolator,
                                                       final T tEnd)
            throws MathIllegalArgumentException, MathIllegalStateException {

        FieldODEStateAndDerivative<T> previousState = interpolator.getGlobalPreviousState();
        final FieldODEStateAndDerivative<T> currentState = interpolator.getGlobalCurrentState();

        // initialize the events states if needed
        if (!statesInitialized) {
            for (FieldEventState<T> state : eventsStates) {
                state.reinitializeBegin(interpolator);
            }
            statesInitialized = true;
        }

        // search for next events that may occur during the step
        final int orderingSign = interpolator.isForward() ? +1 : -1;
        final Queue<FieldEventState<T>> occurringEvents = new PriorityQueue<>(new Comparator<FieldEventState<T>>() {
            /** {@inheritDoc} */
            @Override
            public int compare(FieldEventState<T> es0, FieldEventState<T> es1) {
                return orderingSign * Double.compare(es0.getEventTime().getReal(), es1.getEventTime().getReal());
            }
        });

        for (final FieldEventState<T> state : eventsStates) {
            if (state.evaluateStep(interpolator)) {
                // the event occurs during the current step
                occurringEvents.add(state);
            }
        }

        AbstractFieldODEStateInterpolator<T> restricted = interpolator;

        do {

            eventLoop:
            while (!occurringEvents.isEmpty()) {

                // handle the chronologically first event
                final FieldEventState<T> currentEvent = occurringEvents.poll();

                // get state at event time
                FieldODEStateAndDerivative<T> eventState =
                        restricted.getInterpolatedState(currentEvent.getEventTime());

                // restrict the interpolator to the first part of the step, up to the event
                restricted = restricted.restrictStep(previousState, eventState);

                // try to advance all event states to current time
                for (final FieldEventState<T> state : eventsStates) {
                    if (state != currentEvent && state.tryAdvance(eventState, interpolator)) {
                        // we need to handle another event first
                        // remove event we just updated to prevent heap corruption
                        occurringEvents.remove(state);
                        // add it back to update its position in the heap
                        occurringEvents.add(state);
                        // re-queue the event we were processing
                        occurringEvents.add(currentEvent);
                        continue eventLoop;
                    }
                }
                // all event detectors agree we can advance to the current event time

                final EventOccurrence<T> occurrence = currentEvent.doEvent(eventState);
                final Action action = occurrence.getAction();
                isLastStep = action == Action.STOP;

                if (isLastStep) {
                    // ensure the event is after the root if it is returned STOP
                    // this lets the user integrate to a STOP event and then restart
                    // integration from the same time.
                    eventState = interpolator.getInterpolatedState(occurrence.getStopTime());
                    restricted = interpolator.restrictStep(previousState, eventState);
                }

                // handle the first part of the step, up to the event
                for (final FieldODEStepHandler<T> handler : stepHandlers) {
                    handler.handleStep(restricted, isLastStep);
                }

                if (isLastStep) {
                    // the event asked to stop integration
                    return eventState;
                }

                resetOccurred = false;
                if (action == Action.RESET_DERIVATIVES || action == Action.RESET_STATE) {
                    // some event handler has triggered changes that
                    // invalidate the derivatives, we need to recompute them
                    final FieldODEState<T> newState = occurrence.getNewState();
                    final T[] y = newState.getCompleteState();
                    final T[] yDot = computeDerivatives(newState.getTime(), y);
                    resetOccurred = true;
                    return equations.getMapper().mapStateAndDerivative(newState.getTime(), y, yDot);
                }
                // at this point we know action == Action.CONTINUE

                // prepare handling of the remaining part of the step
                previousState = eventState;
                restricted = restricted.restrictStep(eventState, currentState);

                // check if the same event occurs again in the remaining part of the step
                if (currentEvent.evaluateStep(restricted)) {
                    // the event occurs during the current step
                    occurringEvents.add(currentEvent);
                }

            }

            // last part of the step, after the last event
            // may be a new event here if the last event modified the g function of
            // another event detector.
            for (final FieldEventState<T> state : eventsStates) {
                if (state.tryAdvance(currentState, interpolator)) {
                    occurringEvents.add(state);
                }
            }

        } while (!occurringEvents.isEmpty());


        isLastStep = isLastStep || currentState.getTime().subtract(tEnd).abs().getReal() <= FastMath.ulp(tEnd.getReal());

        // handle the remaining part of the step, after all events if any
        for (FieldODEStepHandler<T> handler : stepHandlers) {
            handler.handleStep(restricted, isLastStep);
        }

        return currentState;

    }

    /**
     * Check the integration span.
     *
     * @param initialState initial state
     * @param t            target time for the integration
     * @throws MathIllegalArgumentException if integration span is too small
     * @throws MathIllegalArgumentException if adaptive step size integrators
     *                                      tolerance arrays dimensions are not compatible with equations settings
     */
    protected void sanityChecks(final FieldODEState<T> initialState, final T t)
            throws MathIllegalArgumentException {

        final double threshold = 1000 * FastMath.ulp(FastMath.max(FastMath.abs(initialState.getTime().getReal()),
                FastMath.abs(t.getReal())));
        final double dt = initialState.getTime().subtract(t).abs().getReal();
        if (dt <= threshold) {
            throw new MathIllegalArgumentException(LocalizedODEFormats.TOO_SMALL_INTEGRATION_INTERVAL,
                    dt, threshold, false);
        }

    }

    /**
     * Check if a reset occurred while last step was accepted.
     *
     * @return true if a reset occurred while last step was accepted
     */
    protected boolean resetOccurred() {
        return resetOccurred;
    }

    /**
     * Get the current step size.
     *
     * @return current step size
     */
    protected T getStepSize() {
        return stepSize;
    }

    /**
     * Set the current step size.
     *
     * @param stepSize step size to set
     */
    protected void setStepSize(final T stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldODEStateAndDerivative<T> getStepStart() {
        return stepStart;
    }

    /**
     * Set current step start.
     *
     * @param stepStart step start
     */
    protected void setStepStart(final FieldODEStateAndDerivative<T> stepStart) {
        this.stepStart = stepStart;
    }

    /**
     * Set the last state flag.
     *
     * @param isLastStep if true, this step is the last one
     */
    protected void setIsLastStep(final boolean isLastStep) {
        this.isLastStep = isLastStep;
    }

    /**
     * Check if this step is the last one.
     *
     * @return true if this step is the last one
     */
    protected boolean isLastStep() {
        return isLastStep;
    }

}
