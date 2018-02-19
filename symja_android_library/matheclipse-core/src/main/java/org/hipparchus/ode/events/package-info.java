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
/**
 * <h2>Events</h2>
 * <p>
 * <p>
 * This package provides classes to handle discrete events occurring during
 * Ordinary Differential Equations integration.
 * </p>
 * <p>
 * <p>
 * Discrete events detection is based on switching functions. The user provides
 * a simple {@link org.hipparchus.ode.events.ODEEventHandler#g(org.hipparchus.ode.ODEStateAndDerivative)
 * g(state)} function depending on the current time and state. The integrator will monitor
 * the value of the function throughout integration range and will trigger the
 * event when its sign changes. The magnitude of the value is almost irrelevant,
 * it should however be continuous (but not necessarily smooth) for the sake of
 * root finding. The steps are shortened as needed to ensure the events occur
 * at step boundaries (even if the integrator is a fixed-step integrator).
 * </p>
 * <p>
 * <p>
 * When an event is triggered, several different options are available:
 * </p>
 * <ul>
 * <li>integration can be stopped (this is called a G-stop facility),</li>
 * <li>the state vector or the derivatives can be changed,</li>
 * <li>or integration can simply go on.</li>
 * </ul>
 * <p>
 * <p>
 * The first case, G-stop, is the most common one. A typical use case is when an
 * ODE must be solved up to some target state is reached, with a known value of
 * the state but an unknown occurrence time. As an example, if we want to monitor
 * a chemical reaction up to some predefined concentration for the first substance,
 * we can use the following switching function setting:
 * <pre>
 *  public double g(final ODEStateAndDerivative state) {
 *    return state.getState()[0] - targetConcentration;
 *  }
 *
 *  public Action eventOccurred(final ODEStateAndDerivative state, final boolean increasing) {
 *    return STOP;
 *  }
 * </pre>
 * </p>
 * <p>
 * <p>
 * The second case, change state vector or derivatives is encountered when dealing
 * with discontinuous dynamical models. A typical case would be the motion of a
 * spacecraft when thrusters are fired for orbital maneuvers. The acceleration is
 * smooth as long as no maneuver are performed, depending only on gravity, drag,
 * third body attraction, radiation pressure. Firing a thruster introduces a
 * discontinuity that must be handled appropriately by the integrator. In such a case,
 * we would use a switching function setting similar to this:
 * <pre>
 *  public double g(final ODEStateAndDerivative state) {
 *    return (state.getTime() - tManeuverStart) &lowast; (state.getTime() - tManeuverStop);
 *  }
 *
 *  public Action eventOccurred(final ODEStateAndDerivative state, final boolean increasing) {
 *    return RESET_DERIVATIVES;
 *  }
 * </pre>
 * </p>
 * <p>
 * <p>
 * The third case is useful mainly for monitoring purposes, a simple example is:
 * <pre>
 *  public double g(final ODEStateAndDerivative state) {
 *  final double[] y = state.getState();
 *    return y[0] - y[1];
 *  }
 *
 *  public Action eventOccurred(final ODEStateAndDerivative state, final boolean increasing) {
 *    logger.log("y0(t) and y1(t) curves cross at t = " + t);
 *    return CONTINUE;
 *  }
 * </pre>
 * </p>
 * <p>
 * <h2>Rules of Event Handling</h2>
 * <p>
 * <p> These rules formalize the concept of event detection and are used to determine when
 * an event must be reported to the user and the order in which events must occur. These
 * rules assume the event handler and g function conform to the documentation on
 * {@link org.hipparchus.ode.events.ODEEventHandler ODEEventHandler} and
 * {@link org.hipparchus.ode.ODEIntegrator ODEIntegrator}.
 * <p>
 * <ol>
 * <li> An event must be detected if the g function has changed signs for longer than
 * maxCheck. Formally, if r is a root of g(t), g has one sign on [r-maxCheck, r) and
 * the opposite sign on (r, r+maxCheck] then r must be detected. Otherwise the root
 * may or may not be detected. </li>
 * <li> For a given tolerance, h, and root, r, the event may occur at any point on the
 * interval [r-h, r+h]. The tolerance is the larger of the {@code convergence}
 * parameter and the convergence settings of the root finder specified when
 * {@link org.hipparchus.ode.ODEIntegrator#addEventHandler(org.hipparchus.ode.events.ODEEventHandler, double, double, int, org.hipparchus.analysis.solvers.BracketedUnivariateSolver) adding}
 * the event handler. </li>
 * <li> At most one event is triggered per root. </li>
 * <li> Events from the same event detector must alternate between increasing and
 * decreasing events. That is, for every pair of increasing events there must exist an
 * intervening decreasing event and vice-versa. </li>
 * <li> An event starts occurring when the
 * {@link org.hipparchus.ode.events.ODEEventHandler#eventOccurred(org.hipparchus.ode.ODEStateAndDerivative, boolean) eventOccurred()}
 * method is called. An event stops occurring when eventOccurred() returns or when the
 * handler's
 * {@link org.hipparchus.ode.events.ODEEventHandler#resetState(org.hipparchus.ode.ODEStateAndDerivative) resetState()}
 * method returns if eventOccured() returned
 * {@link org.hipparchus.ode.events.Action#RESET_STATE RESET_STATE}. </li>
 * <li> If event A happens before event B then the effects of A occurring are visible
 * to B. (Including resetting the state or derivatives, or stopping) </li>
 * <li> Events occur in chronological order. If integration is forward and event A
 * happens before event B then the time of event B is greater than or equal to the
 * time of event A. </li>
 * <li> There is a total order on events. That is for two events A and B either A
 * happens before B or B happens before A. </li>
 * </ol>
 */
package org.hipparchus.ode.events;
