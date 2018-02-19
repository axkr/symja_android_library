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

import org.hipparchus.RealFieldElement;
import org.hipparchus.analysis.RealFieldUnivariateFunction;
import org.hipparchus.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.hipparchus.analysis.solvers.BracketedRealFieldUnivariateSolver.Interval;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.ode.FieldODEState;
import org.hipparchus.ode.FieldODEStateAndDerivative;
import org.hipparchus.ode.sampling.FieldODEStateInterpolator;
import org.hipparchus.util.FastMath;

/**
 * This class handles the state for one {@link FieldODEEventHandler
 * event handler} during integration steps.
 * <p>
 * <p>Each time the integrator proposes a step, the event handler
 * switching function should be checked. This class handles the state
 * of one handler during one integration step, with references to the
 * state at the end of the preceding step. This information is used to
 * decide if the handler should trigger an event or not during the
 * proposed step.</p>
 *
 * @param <T> the type of the field elements
 */
public class FieldEventState<T extends RealFieldElement<T>> {

    /**
     * Event handler.
     */
    private final FieldODEEventHandler<T> handler;

    /**
     * Maximal time interval between events handler checks.
     */
    private final double maxCheckInterval;

    /**
     * Convergence threshold for event localization.
     */
    private final T convergence;

    /**
     * Upper limit in the iteration count for event localization.
     */
    private final int maxIterationCount;
    /**
     * Root-finding algorithm to use to detect state events.
     */
    private final BracketedRealFieldUnivariateSolver<T> solver;
    /**
     * Time at the beginning of the step.
     */
    private T t0;
    /**
     * Value of the events handler at the beginning of the step.
     */
    private T g0;
    /**
     * Sign of g0.
     */
    private boolean g0Positive;
    /**
     * Indicator of event expected during the step.
     */
    private boolean pendingEvent;
    /**
     * Occurrence time of the pending event.
     */
    private T pendingEventTime;
    /**
     * Time to stop propagation if the event is a stop event. Used to enable stopping at
     * an event and then restarting after that event.
     */
    private T stopTime;
    /**
     * Time after the current event.
     */
    private T afterEvent;
    /**
     * Value of the g function after the current event.
     */
    private T afterG;
    /**
     * The earliest time considered for events.
     */
    private T earliestTimeConsidered;
    /**
     * Integration direction.
     */
    private boolean forward;
    /**
     * Variation direction around pending event.
     * (this is considered with respect to the integration direction)
     */
    private boolean increasing;

    /**
     * Simple constructor.
     *
     * @param handler           event handler
     * @param maxCheckInterval  maximal time interval between switching
     *                          function checks (this interval prevents missing sign changes in
     *                          case the integration steps becomes very large)
     * @param convergence       convergence threshold in the event time search
     * @param maxIterationCount upper limit of the iteration count in
     *                          the event time search
     * @param solver            Root-finding algorithm to use to detect state events
     */
    public FieldEventState(final FieldODEEventHandler<T> handler, final double maxCheckInterval,
                           final T convergence, final int maxIterationCount,
                           final BracketedRealFieldUnivariateSolver<T> solver) {
        this.handler = handler;
        this.maxCheckInterval = maxCheckInterval;
        this.convergence = convergence.abs();
        this.maxIterationCount = maxIterationCount;
        this.solver = solver;

        // some dummy values ...
        t0 = null;
        g0 = null;
        g0Positive = true;
        pendingEvent = false;
        pendingEventTime = null;
        increasing = true;
        earliestTimeConsidered = null;
        afterEvent = null;
        afterG = null;

    }

    /**
     * Get the underlying event handler.
     *
     * @return underlying event handler
     */
    public FieldODEEventHandler<T> getEventHandler() {
        return handler;
    }

    /**
     * Get the maximal time interval between events handler checks.
     *
     * @return maximal time interval between events handler checks
     */
    public double getMaxCheckInterval() {
        return maxCheckInterval;
    }

    /**
     * Get the convergence threshold for event localization.
     *
     * @return convergence threshold for event localization
     */
    public T getConvergence() {
        return convergence;
    }

    /**
     * Get the upper limit in the iteration count for event localization.
     *
     * @return upper limit in the iteration count for event localization
     */
    public int getMaxIterationCount() {
        return maxIterationCount;
    }

    /**
     * Reinitialize the beginning of the step.
     *
     * @param interpolator valid for the current step
     * @throws MathIllegalStateException if the interpolator throws one because
     *                                   the number of functions evaluations is exceeded
     */
    public void reinitializeBegin(final FieldODEStateInterpolator<T> interpolator)
            throws MathIllegalStateException {

        forward = interpolator.isForward();
        final FieldODEStateAndDerivative<T> s0 = interpolator.getPreviousState();
        t0 = s0.getTime();
        g0 = handler.g(s0);
        while (g0.getReal() == 0) {
            // excerpt from MATH-421 issue:
            // If an ODE solver is setup with a FieldODEEventHandler that return STOP
            // when the even is triggered, the integrator stops (which is exactly
            // the expected behavior). If however the user wants to restart the
            // solver from the final state reached at the event with the same
            // configuration (expecting the event to be triggered again at a
            // later time), then the integrator may fail to start. It can get stuck
            // at the previous event. The use case for the bug MATH-421 is fairly
            // general, so events occurring exactly at start in the first step should
            // be ignored.

            // extremely rare case: there is a zero EXACTLY at interval start
            // we will use the sign slightly after step beginning to force ignoring this zero
            final T epsilon = max(solver.getAbsoluteAccuracy(),
                    solver.getRelativeAccuracy().multiply(t0).abs());
            T tStart = t0.add(epsilon.multiply(forward ? 0.5 : -0.5));
            if (tStart.equals(t0)) {
                tStart = nextAfter(t0);
            }
            t0 = tStart;
            g0 = handler.g(interpolator.getInterpolatedState(tStart));
        }
        g0Positive = g0.getReal() > 0;
        // "last" event was increasing
        increasing = g0Positive;

    }

    /**
     * Get the larger of two numbers.
     *
     * @param a first number.
     * @param b second number.
     * @return the larger of a and b.
     */
    private T max(T a, T b) {
        return a.getReal() > b.getReal() ? a : b;
    }

    /**
     * Get the smaller of two numbers.
     *
     * @param a first number.
     * @param b second number.
     * @return the smaller of a and b.
     */
    private T min(T a, T b) {
        return a.getReal() < b.getReal() ? a : b;
    }

    /**
     * Evaluate the impact of the proposed step on the event handler.
     *
     * @param interpolator step interpolator for the proposed step
     * @return true if the event handler triggers an event before
     * the end of the proposed step
     * @throws MathIllegalStateException    if the interpolator throws one because
     *                                      the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if the event cannot be bracketed
     */
    public boolean evaluateStep(final FieldODEStateInterpolator<T> interpolator)
            throws MathIllegalArgumentException, MathIllegalStateException {

        forward = interpolator.isForward();
        final FieldODEStateAndDerivative<T> s1 = interpolator.getCurrentState();
        final T t1 = s1.getTime();
        final T dt = t1.subtract(t0);
        if (dt.abs().subtract(convergence).getReal() < 0) {
            // we cannot do anything on such a small step, don't trigger any events
            return false;
        }
        final int n = FastMath.max(1, (int) FastMath.ceil(FastMath.abs(dt.getReal()) / maxCheckInterval));
        final T h = dt.divide(n);

        T ta = t0;
        T ga = g0;
        for (int i = 0; i < n; ++i) {

            // evaluate handler value at the end of the substep
            final T tb = (i == n - 1) ? t1 : t0.add(h.multiply(i + 1));
            final T gb = handler.g(interpolator.getInterpolatedState(tb));

            // check events occurrence
            if (gb.getReal() == 0.0 || (g0Positive ^ (gb.getReal() > 0))) {
                // there is a sign change: an event is expected during this step
                if (findRoot(interpolator, ta, ga, tb, gb)) {
                    return true;
                }
            } else {
                // no sign change: there is no event for now
                ta = tb;
                ga = gb;
            }

        }

        // no event during the whole step
        pendingEvent = false;
        pendingEventTime = null;
        return false;

    }

    /**
     * Find a root in a bracketing interval.
     * <p>
     * <p> When calling this method one of the following must be true. Either ga == 0, gb
     * == 0, (ga < 0  and gb > 0), or (ga > 0 and gb < 0).
     *
     * @param interpolator that covers the interval.
     * @param ta           earliest possible time for root.
     * @param ga           g(ta).
     * @param tb           latest possible time for root.
     * @param gb           g(tb).
     * @return if a zero crossing was found.
     */
    private boolean findRoot(final FieldODEStateInterpolator<T> interpolator,
                             final T ta,
                             final T ga,
                             final T tb,
                             final T gb) {
        // check there appears to be a root in [ta, tb]
        check(ga.getReal() == 0.0 || gb.getReal() == 0.0 ||
                (ga.getReal() > 0.0 && gb.getReal() < 0.0) ||
                (ga.getReal() < 0.0 && gb.getReal() > 0.0));

        final RealFieldUnivariateFunction<T> f =
                new RealFieldUnivariateFunction<T>() {
                    @Override
                    public T value(T t) {
                        return handler.g(interpolator.getInterpolatedState(t));
                    }
                };

        // event time, just at or before the actual root.
        T beforeRootT = null;
        T beforeRootG = null;
        // time on the other side of the root.
        // Initialized the the loop below executes once.
        T afterRootT = ta;
        T afterRootG = ga.getField().getZero();

        // check for some conditions that the root finders don't like
        // these conditions cannot not happen in the loop below
        // the ga == 0.0 case is handled by the loop below
        if (ta.getReal() == tb.getReal()) {
            // both non-zero but times are the same. Probably due to reset state
            beforeRootT = ta;
            beforeRootG = ga;
            afterRootT = shiftedBy(beforeRootT, convergence);
            afterRootG = f.value(afterRootT);
        } else if (ga.getReal() != 0.0 && gb.getReal() == 0.0) {
            // hard: ga != 0.0 and gb == 0.0
            // look past gb by up to convergence to find next sign
            // throw an exception if g(t) = 0.0 in [tb, tb + convergence]
            beforeRootT = tb;
            beforeRootG = gb;
            afterRootT = shiftedBy(beforeRootT, convergence);
            afterRootG = f.value(afterRootT);
        } else if (ga.getReal() != 0.0) {
            final T newGa = f.value(ta);
            if (ga.getReal() > 0 != newGa.getReal() > 0) {
                // both non-zero, step sign change at ta, possibly due to reset state
                beforeRootT = ta;
                beforeRootG = newGa;
                afterRootT = minTime(shiftedBy(beforeRootT, convergence), tb);
                afterRootG = f.value(afterRootT);
            }
        }

        // loop to skip through "fake" roots, i.e. where g(t) = g'(t) = 0.0
        // executed once if we didn't hit a special case above
        T loopT = ta;
        T loopG = ga;
        while ((afterRootG.getReal() == 0.0 || afterRootG.getReal() > 0.0 == g0Positive) &&
                strictlyAfter(afterRootT, tb)) {
            if (loopG.getReal() == 0.0) {
                // ga == 0.0 and gb may or may not be 0.0
                // handle the root at ta first
                beforeRootT = loopT;
                beforeRootG = loopG;
                afterRootT = minTime(shiftedBy(beforeRootT, convergence), tb);
                afterRootG = f.value(afterRootT);
            } else {
                // both non-zero, the usual case, use a root finder.
                if (forward) {
                    final Interval<T> interval =
                            solver.solveInterval(maxIterationCount, f, loopT, tb);
                    beforeRootT = interval.getLeftAbscissa();
                    beforeRootG = interval.getLeftValue();
                    afterRootT = interval.getRightAbscissa();
                    afterRootG = interval.getRightValue();
                } else {
                    final Interval<T> interval =
                            solver.solveInterval(maxIterationCount, f, tb, loopT);
                    beforeRootT = interval.getRightAbscissa();
                    beforeRootG = interval.getRightValue();
                    afterRootT = interval.getLeftAbscissa();
                    afterRootG = interval.getLeftValue();
                }
            }
            // tolerance is set to less than 1 ulp
            // assume tolerance is 1 ulp
            if (beforeRootT == afterRootT) {
                afterRootT = nextAfter(afterRootT);
                afterRootG = f.value(afterRootT);
            }
            // check loop is making some progress
            check((forward && afterRootT.getReal() > beforeRootT.getReal()) ||
                    (!forward && afterRootT.getReal() < beforeRootT.getReal()));
            // setup next iteration
            loopT = afterRootT;
            loopG = afterRootG;
        }

        // figure out the result of root finding, and return accordingly
        if (afterRootG.getReal() == 0.0 || afterRootG.getReal() > 0.0 == g0Positive) {
            // loop gave up and didn't find any crossing within this step
            return false;
        } else {
            // real crossing
            check(beforeRootT != null && beforeRootG != null);
            // variation direction, with respect to the integration direction
            increasing = !g0Positive;
            pendingEventTime = beforeRootT;
            stopTime = beforeRootG.getReal() == 0.0 ? beforeRootT : afterRootT;
            pendingEvent = true;
            afterEvent = afterRootT;
            afterG = afterRootG;

            // check increasing set correctly
            check(afterG.getReal() > 0 == increasing);
            check(increasing == gb.getReal() >= ga.getReal());

            return true;
        }
    }

    /**
     * Try to accept the current history up to the given time.
     * <p>
     * <p> It is not necessary to call this method before calling {@link
     * #doEvent(FieldODEStateAndDerivative)} with the same state. It is necessary to call this
     * method before you call {@link #doEvent(FieldODEStateAndDerivative)} on some other event
     * detector.
     *
     * @param state        to try to accept.
     * @param interpolator to use to find the new root, if any.
     * @return if the event detector has an event it has not detected before that is on or
     * before the same time as {@code state}. In other words {@code false} means continue
     * on while {@code true} means stop and handle my event first.
     */
    public boolean tryAdvance(final FieldODEStateAndDerivative<T> state,
                              final FieldODEStateInterpolator<T> interpolator) {
        // check this is only called before a pending event.
        check(!(pendingEvent && strictlyAfter(pendingEventTime, state.getTime())));

        final T t = state.getTime();

        // just found an event and we know the next time we want to search again
        if (earliestTimeConsidered != null && strictlyAfter(t, earliestTimeConsidered)) {
            return false;
        }

        final T g = handler.g(state);
        final boolean positive = g.getReal() > 0;

        if ((g.getReal() == 0.0 && pendingEventTime == t) || positive == g0Positive) {
            // at a root we already found, or g function has expected sign
            t0 = t;
            g0 = g; // g0Positive is the same
            return false;
        } else {
            // found a root we didn't expect -> find precise location
            return findRoot(interpolator, t0, g0, t, g);
        }
    }

    /**
     * Notify the user's listener of the event. The event occurs wholly within this method
     * call including a call to {@link FieldODEEventHandler#resetState(FieldODEStateAndDerivative)}
     * if necessary.
     *
     * @param state the state at the time of the event. This must be at the same time as
     *              the current value of {@link #getEventTime()}.
     * @return the user's requested action and the new state if the action is {@link
     * Action#RESET_STATE}. Otherwise the new state is {@code state}. The stop time
     * indicates what time propagation should stop if the action is {@link Action#STOP}.
     * This guarantees the integration will stop on or after the root, so that integration
     * may be restarted safely.
     */
    public EventOccurrence<T> doEvent(final FieldODEStateAndDerivative<T> state) {
        // check event is pending and is at the same time
        check(pendingEvent);
        check(state.getTime() == this.pendingEventTime);

        final Action action = handler.eventOccurred(state, increasing == forward);
        final FieldODEState<T> newState;
        if (action == Action.RESET_STATE) {
            newState = handler.resetState(state);
        } else {
            newState = state;
        }
        // clear pending event
        pendingEvent = false;
        pendingEventTime = null;
        // setup for next search
        earliestTimeConsidered = afterEvent;
        t0 = afterEvent;
        g0 = afterG;
        g0Positive = increasing;
        // check g0Positive set correctly
        check(g0.getReal() == 0.0 || g0Positive == (g0.getReal() > 0));
        return new EventOccurrence<>(action, newState, stopTime);
    }

    /**
     * Check the ordering of two times.
     *
     * @param t1 the first time.
     * @param t2 the second time.
     * @return true if {@code t2} is strictly after {@code t1} in the propagation
     * direction.
     */
    private boolean strictlyAfter(final T t1, final T t2) {
        return forward ? t1.getReal() < t2.getReal() : t2.getReal() < t1.getReal();
    }

    /**
     * Get the next number after the given number in the current propagation direction.
     * <p>
     * <p> Assumes T has the same precision as a double.
     *
     * @param t input time
     * @return t +/- 1 ulp depending on the direction.
     */
    private T nextAfter(final T t) {
        // direction
        final int sign = forward ? 1 : -1;
        final double ulp = FastMath.ulp(t.getReal());
        return t.add(sign * ulp);
    }

    /**
     * Same as keyword assert, but throw a {@link MathRuntimeException}.
     *
     * @param condition to check
     * @throws MathRuntimeException if {@code condition} is false.
     */
    private void check(final boolean condition) throws MathRuntimeException {
        if (!condition) {
            throw MathRuntimeException.createInternalError();
        }
    }

    /**
     * Get the time that happens first along the current propagation direction: {@link
     * #forward}.
     *
     * @param a first time
     * @param b second time
     * @return min(a, b) if forward, else max (a, b)
     */
    private T minTime(final T a, final T b) {
        return forward ? min(a, b) : max(a, b);
    }

    /**
     * Shift a time value along the current integration direction: {@link #forward}.
     *
     * @param t     the time to shift.
     * @param delta the amount to shift.
     * @return t + delta if forward, else t - delta. If the result has to be rounded it
     * will be rounded to be before the true value of t + delta.
     */
    private T shiftedBy(final T t, final T delta) {
        if (forward) {
            final T ret = t.add(delta);
            if (ret.subtract(t).getReal() > delta.getReal()) {
                // nextDown(ret)
                return ret.subtract(FastMath.ulp(ret.getReal()));
            } else {
                return ret;
            }
        } else {
            final T ret = t.subtract(delta);
            if (t.subtract(ret).getReal() > delta.getReal()) {
                // nextUp(ret)
                return ret.add(FastMath.ulp(ret.getReal()));
            } else {
                return ret;
            }
        }
    }

    /**
     * Get the occurrence time of the event triggered in the current step.
     *
     * @return occurrence time of the event triggered in the current
     * step or infinity if no events are triggered
     */
    public T getEventTime() {
        return pendingEvent ?
                pendingEventTime :
                t0.getField().getZero().add(forward ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    }

    /**
     * Class to hold the data related to an event occurrence that is needed to decide how
     * to modify integration.
     *
     * @param <T> the type of the field elements
     */
    public static class EventOccurrence<T extends RealFieldElement<T>> {

        /**
         * User requested action.
         */
        private final Action action;
        /**
         * New state for a reset action.
         */
        private final FieldODEState<T> newState;
        /**
         * The time to stop propagation if the action is a stop event.
         */
        private final T stopTime;

        /**
         * Create a new occurrence of an event.
         *
         * @param action   the user requested action.
         * @param newState for a reset event. Should be the current state unless the
         *                 action is {@link Action#RESET_STATE}.
         * @param stopTime to stop propagation if the action is {@link Action#STOP}. Used
         *                 to move the stop time to just after the root.
         */
        EventOccurrence(final Action action,
                        final FieldODEState<T> newState,
                        final T stopTime) {
            this.action = action;
            this.newState = newState;
            this.stopTime = stopTime;
        }

        /**
         * Get the user requested action.
         *
         * @return the action.
         */
        public Action getAction() {
            return action;
        }

        /**
         * Get the new state for a reset action.
         *
         * @return the new state.
         */
        public FieldODEState<T> getNewState() {
            return newState;
        }

        /**
         * Get the new time for a stop action.
         *
         * @return when to stop propagation.
         */
        public T getStopTime() {
            return stopTime;
        }

    }

}
