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

package org.hipparchus.ode;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.BracketedUnivariateSolver;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.events.ODEEventHandler;
import org.hipparchus.ode.sampling.ODEStepHandler;

import java.util.Collection;

/**
 * This interface represents a first order integrator for
 * differential equations.
 * <p>
 * <p>The classes which are devoted to solve first order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link
 * OrdinaryDifferentialEquation} interface.</p>
 *
 * @see OrdinaryDifferentialEquation
 * @see ODEStepHandler
 * @see ODEEventHandler
 */
public interface ODEIntegrator {

    /**
     * Get the name of the method.
     *
     * @return name of the method
     */
    String getName();

    /**
     * Add a step handler to this integrator.
     * <p>The handler will be called by the integrator for each accepted
     * step.</p>
     *
     * @param handler handler for the accepted steps
     * @see #getStepHandlers()
     * @see #clearStepHandlers()
     */
    void addStepHandler(ODEStepHandler handler);

    /**
     * Get all the step handlers that have been added to the integrator.
     *
     * @return an unmodifiable collection of the added events handlers
     * @see #addStepHandler(ODEStepHandler)
     * @see #clearStepHandlers()
     */
    Collection<ODEStepHandler> getStepHandlers();

    /**
     * Remove all the step handlers that have been added to the integrator.
     *
     * @see #addStepHandler(ODEStepHandler)
     * @see #getStepHandlers()
     */
    void clearStepHandlers();

    /**
     * Add an event handler to the integrator.
     * <p>
     * <p> Uses a default {@link org.hipparchus.analysis.solvers.UnivariateSolver} with an absolute accuracy equal to the
     * given convergence threshold, as root-finding algorithm to detect the state events.
     *
     * @param handler           event handler
     * @param maxCheckInterval  maximal time interval between switching function checks
     *                          (this interval prevents missing sign changes in case the
     *                          integration steps becomes very large)
     * @param convergence       convergence threshold in the event time search. Must be
     *                          smaller than {@code maxCheckInterval} and should be small
     *                          compared to time scale of the ODE dynamics.
     * @param maxIterationCount upper limit of the iteration count in the event time
     *                          search
     * @see #getEventHandlers()
     * @see #clearEventHandlers()
     */
    void addEventHandler(ODEEventHandler handler, double maxCheckInterval,
                         double convergence, int maxIterationCount);

    /**
     * Add an event handler to the integrator.
     *
     * @param handler           event handler
     * @param maxCheckInterval  maximal time interval between switching function checks
     *                          (this interval prevents missing sign changes in case the
     *                          integration steps becomes very large)
     * @param convergence       convergence threshold in the event time search. Must be
     *                          smaller than {@code maxCheckInterval} and should be small
     *                          compared to time scale of the ODE dynamics.
     * @param maxIterationCount upper limit of the iteration count in the event time
     *                          search
     * @param solver            The root-finding algorithm to use to detect the state
     *                          events.
     * @see #getEventHandlers()
     * @see #clearEventHandlers()
     */
    void addEventHandler(ODEEventHandler handler, double maxCheckInterval,
                         double convergence, int maxIterationCount,
                         BracketedUnivariateSolver<UnivariateFunction> solver);

    /**
     * Get all the event handlers that have been added to the integrator.
     *
     * @return an unmodifiable collection of the added events handlers
     * @see #addEventHandler(ODEEventHandler, double, double, int)
     * @see #clearEventHandlers()
     */
    Collection<ODEEventHandler> getEventHandlers();

    /**
     * Remove all the event handlers that have been added to the integrator.
     *
     * @see #addEventHandler(ODEEventHandler, double, double, int)
     * @see #getEventHandlers()
     */
    void clearEventHandlers();

    /**
     * Get the current value of the step start time t<sub>i</sub>.
     * <p>This method can be called during integration (typically by
     * the object implementing the {@link OrdinaryDifferentialEquation
     * differential equations} problem) if the value of the current step that
     * is attempted is needed.</p>
     * <p>The result is undefined if the method is called outside of
     * calls to <code>integrate</code>.</p>
     *
     * @return current value of the step start time t<sub>i</sub>
     * @deprecated as of 1.0, replaced with {@link #getStepStart()
     * getStepStart()}.{@link ODEStateAndDerivative#getTime() getTime()}
     */
    @Deprecated
    double getCurrentStepStart();

    /**
     * Get the state at step start time t<sub>i</sub>.
     * <p>This method can be called during integration (typically by
     * the object implementing the {@link OrdinaryDifferentialEquation
     * differential equations} problem) if the value of the current step that
     * is attempted is needed.</p>
     * <p>The result is undefined if the method is called outside of
     * calls to <code>integrate</code>.</p>
     *
     * @return state at step start time t<sub>i</sub>
     */
    ODEStateAndDerivative getStepStart();

    /**
     * Get the current signed value of the integration stepsize.
     * <p>This method can be called during integration (typically by
     * the object implementing the {@link OrdinaryDifferentialEquation
     * differential equations} problem) if the signed value of the current stepsize
     * that is tried is needed.</p>
     * <p>The result is undefined if the method is called outside of
     * calls to <code>integrate</code>.</p>
     *
     * @return current signed value of the stepsize
     */
    double getCurrentSignedStepsize();

    /**
     * Get the maximal number of functions evaluations.
     *
     * @return maximal number of functions evaluations
     */
    int getMaxEvaluations();

    /**
     * Set the maximal number of differential equations function evaluations.
     * <p>The purpose of this method is to avoid infinite loops which can occur
     * for example when stringent error constraints are set or when lots of
     * discrete events are triggered, thus leading to many rejected steps.</p>
     *
     * @param maxEvaluations maximal number of function evaluations (negative
     *                       values are silently converted to maximal integer value, thus representing
     *                       almost unlimited evaluations)
     */
    void setMaxEvaluations(int maxEvaluations);

    /**
     * Get the number of evaluations of the differential equations function.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * <code>integrate</code> method. It is 0 if the method has not been called yet.
     * </p>
     *
     * @return number of evaluations of the differential equations function
     */
    int getEvaluations();

    /**
     * Integrate the differential equations up to the given time.
     * <p>This method solves an Initial Value Problem (IVP).</p>
     * <p>Since this method stores some internal state variables made
     * available in its public interface during integration ({@link
     * #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
     *
     * @param equations    differential equations to integrate
     * @param initialState initial state (time, primary and secondary state vectors)
     * @param finalTime    target time for the integration
     *                     (can be set to a value smaller than {@code t0} for backward integration)
     * @return final state, its time will be the same as {@code finalTime} if
     * integration reached its target, but may be different if some {@link
     * ODEEventHandler} stops it at some point.
     * @throws MathIllegalArgumentException if integration step is too small
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the location of an event cannot be bracketed
     */
    ODEStateAndDerivative integrate(ExpandableODE equations,
                                    ODEState initialState, double finalTime)
            throws MathIllegalArgumentException, MathIllegalStateException;

    /**
     * Integrate the differential equations up to the given time.
     * <p>This method solves an Initial Value Problem (IVP).</p>
     * <p>Since this method stores some internal state variables made
     * available in its public interface during integration ({@link
     * #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
     *
     * @param equations    differential equations to integrate
     * @param initialState initial state (time, primary and secondary state vectors)
     * @param finalTime    target time for the integration
     *                     (can be set to a value smaller than {@code t0} for backward integration)
     * @return final state, its time will be the same as {@code finalTime} if
     * integration reached its target, but may be different if some {@link
     * ODEEventHandler} stops it at some point.
     * @throws MathIllegalArgumentException if integration step is too small
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the location of an event cannot be bracketed
     */
    default ODEStateAndDerivative integrate(OrdinaryDifferentialEquation equations,
                                            ODEState initialState, double finalTime)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return integrate(new ExpandableODE(equations), initialState, finalTime);
    }

    /**
     * Integrate the differential equations up to the given time.
     * <p>This method solves an Initial Value Problem (IVP).</p>
     * <p>Since this method stores some internal state variables made
     * available in its public interface during integration ({@link
     * #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
     *
     * @param equations differential equations to integrate
     * @param t0        initial time
     * @param y0        initial value of the state vector at t0
     * @param t         target time for the integration
     *                  (can be set to a value smaller than <code>t0</code> for backward integration)
     * @param y         placeholder where to put the state vector at each successful
     *                  step (and hence at the end of integration), can be the same object as y0
     * @return stop time, will be the same as target time if integration reached its
     * target, but may be different if some {@link
     * ODEEventHandler} stops it at some point.
     * @throws MathIllegalArgumentException if arrays dimension do not match equations settings
     * @throws MathIllegalArgumentException if integration step is too small
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the location of an event cannot be bracketed
     * @deprecated as of 1.0, replaced with {@link #integrate(ExpandableODE, ODEState, double)}
     */
    @Deprecated
    default double integrate(final OrdinaryDifferentialEquation equations,
                             final double t0, final double[] y0, final double t, final double[] y)
            throws MathIllegalArgumentException, MathIllegalStateException {

        if (y0.length != equations.getDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    y0.length, equations.getDimension());
        }
        if (y.length != equations.getDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    y.length, equations.getDimension());
        }

        // prepare expandable stateful equations
        final ExpandableODE expandableODE = new ExpandableODE(equations);

        // perform integration
        final ODEState initialState = new ODEState(t0, y0);
        final ODEStateAndDerivative finalState = integrate(expandableODE, initialState, t);

        // extract results back from the stateful equations
        System.arraycopy(finalState.getPrimaryState(), 0, y, 0, y.length);
        return finalState.getTime();

    }

}
