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

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.BracketedUnivariateSolver;
import org.hipparchus.analysis.solvers.BracketingNthOrderBrentSolver;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.events.EventState;
import org.hipparchus.ode.events.EventState.EventOccurrence;
import org.hipparchus.ode.events.ODEEventHandler;
import org.hipparchus.ode.sampling.AbstractODEStateInterpolator;
import org.hipparchus.ode.sampling.ODEStepHandler;
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
 */
public abstract class AbstractIntegrator implements ODEIntegrator {

    /**
     * Default relative accuracy.
     */
    private static final double DEFAULT_RELATIVE_ACCURACY = 0;

    /**
     * Default function value accuracy.
     */
    private static final double DEFAULT_FUNCTION_VALUE_ACCURACY = 0;
    /**
     * Name of the method.
     */
    private final String name;
    /**
     * Step handler.
     */
    private Collection<ODEStepHandler> stepHandlers;
    /**
     * Current step start time.
     */
    private ODEStateAndDerivative stepStart;
    /**
     * Current stepsize.
     */
    private double stepSize;
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
    private Collection<EventState> eventsStates;
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
    private transient ExpandableODE equations;

    /**
     * Build an instance.
     *
     * @param name name of the method
     */
    protected AbstractIntegrator(final String name) {
        this.name = name;
        stepHandlers = new ArrayList<>();
        stepStart = null;
        stepSize = Double.NaN;
        eventsStates = new ArrayList<>();
        statesInitialized = false;
        evaluations = new Incrementor();
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
    public void addStepHandler(final ODEStepHandler handler) {
        stepHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ODEStepHandler> getStepHandlers() {
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
    public void addEventHandler(final ODEEventHandler handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount) {
        addEventHandler(handler, maxCheckInterval, convergence,
                maxIterationCount,
                new BracketingNthOrderBrentSolver(DEFAULT_RELATIVE_ACCURACY,
                        convergence,
                        DEFAULT_FUNCTION_VALUE_ACCURACY,
                        5));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventHandler(final ODEEventHandler handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount,
                                final BracketedUnivariateSolver<UnivariateFunction> solver) {
        eventsStates.add(new EventState(handler, maxCheckInterval, convergence,
                maxIterationCount, solver));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ODEEventHandler> getEventHandlers() {
        final List<ODEEventHandler> list = new ArrayList<ODEEventHandler>(eventsStates.size());
        for (EventState state : eventsStates) {
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
    @Deprecated
    public double getCurrentStepStart() {
        return stepStart.getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCurrentSignedStepsize() {
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
     * @return Initial state with computed derivatives.
     */
    protected ODEStateAndDerivative initIntegration(final ExpandableODE eqn,
                                                    final ODEState s0, final double t) {

        this.equations = eqn;
        evaluations = evaluations.withCount(0);

        // initialize ODE
        eqn.init(s0, t);

        // set up derivatives of initial state (including primary and secondary components)
        final double t0 = s0.getTime();
        final double[] y0 = s0.getCompleteState();
        final double[] y0Dot = computeDerivatives(t0, y0);

        // built the state
        final ODEStateAndDerivative s0WithDerivatives =
                eqn.getMapper().mapStateAndDerivative(t0, y0, y0Dot);

        // initialize event handlers
        for (final EventState state : eventsStates) {
            state.getEventHandler().init(s0WithDerivatives, t);
        }

        // initialize step handlers
        for (ODEStepHandler handler : stepHandlers) {
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
    protected ExpandableODE getEquations() {
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
     *                                      is called outside of a call to {@link #integrate(ExpandableODE, ODEState, double) integrate}
     */
    public double[] computeDerivatives(final double t, final double[] y)
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
     * @throws MathIllegalStateException    if the interpolator throws one because
     *                                      the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the location of an event cannot be bracketed
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     */
    protected ODEStateAndDerivative acceptStep(final AbstractODEStateInterpolator interpolator,
                                               final double tEnd)
            throws MathIllegalArgumentException, MathIllegalStateException {

        ODEStateAndDerivative previousState = interpolator.getGlobalPreviousState();
        final ODEStateAndDerivative currentState = interpolator.getGlobalCurrentState();

        // initialize the events states if needed
        if (!statesInitialized) {
            for (EventState state : eventsStates) {
                state.reinitializeBegin(interpolator);
            }
            statesInitialized = true;
        }

        // search for next events that may occur during the step
        final int orderingSign = interpolator.isForward() ? +1 : -1;
        final Queue<EventState> occurringEvents = new PriorityQueue<>(new Comparator<EventState>() {
            /** {@inheritDoc} */
            @Override
            public int compare(final EventState es0, final EventState es1) {
                return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
            }
        });

        for (final EventState state : eventsStates) {
            if (state.evaluateStep(interpolator)) {
                // the event occurs during the current step
                occurringEvents.add(state);
            }
        }

        AbstractODEStateInterpolator restricted = interpolator;

        do {

            eventLoop:
            while (!occurringEvents.isEmpty()) {

                // handle the chronologically first event
                final EventState currentEvent = occurringEvents.poll();

                // get state at event time
                ODEStateAndDerivative eventState = restricted.getInterpolatedState(currentEvent.getEventTime());

                // restrict the interpolator to the first part of the step, up to the event
                restricted = restricted.restrictStep(previousState, eventState);

                // try to advance all event states to current time
                for (final EventState state : eventsStates) {
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

                final EventOccurrence occurrence = currentEvent.doEvent(eventState);
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
                for (final ODEStepHandler handler : stepHandlers) {
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
                    final ODEState newState = occurrence.getNewState();
                    final double[] y = newState.getCompleteState();
                    final double[] yDot = computeDerivatives(newState.getTime(), y);
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
            for (final EventState state : eventsStates) {
                if (state.tryAdvance(currentState, interpolator)) {
                    occurringEvents.add(state);
                }
            }

        } while (!occurringEvents.isEmpty());

        isLastStep = isLastStep || FastMath.abs(currentState.getTime() - tEnd) <= FastMath.ulp(tEnd);

        // handle the remaining part of the step, after all events if any
        for (ODEStepHandler handler : stepHandlers) {
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
    protected void sanityChecks(final ODEState initialState, final double t)
            throws MathIllegalArgumentException {

        final double threshold = 1000 * FastMath.ulp(FastMath.max(FastMath.abs(initialState.getTime()),
                FastMath.abs(t)));
        final double dt = FastMath.abs(initialState.getTime() - t);
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
    protected double getStepSize() {
        return stepSize;
    }

    /**
     * Set the current step size.
     *
     * @param stepSize step size to set
     */
    protected void setStepSize(final double stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative getStepStart() {
        return stepStart;
    }

    /**
     * Set current step start.
     *
     * @param stepStart step start
     */
    protected void setStepStart(final ODEStateAndDerivative stepStart) {
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
