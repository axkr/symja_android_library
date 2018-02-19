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

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class to compute Jacobian matrices by finite differences for ODE
 * which do not compute them by themselves.
 */
class ParameterJacobianWrapper implements ODEJacobiansProvider {

    /**
     * ode base ordinary differential equation for which Jacobians
     * matrices are requested.
     */
    private final OrdinaryDifferentialEquation ode;

    /**
     * Steps for finite difference computation of the jacobian df/dy w.r.t. state.
     */
    private final double[] hY;

    /**
     * Controller to change parameters.
     */
    private final ParametersController controller;

    /**
     * Steps for finite difference computation of the Jacobian df/dp w.r.t. parameters.
     */
    private final Map<String, Double> hParam;

    /**
     * Wrap a {@link ParametersController} into a {@link NamedParameterJacobianProvider}.
     *
     * @param ode            ode base ordinary differential equation for which Jacobians
     *                       matrices are requested
     * @param hY             step used for finite difference computation with respect to state vector
     * @param controller     controller to change parameters
     * @param paramsAndSteps parameters and steps to compute the Jacobians df/dp
     * @see ParametersController#setParameterStep(String, double)
     */
    ParameterJacobianWrapper(final OrdinaryDifferentialEquation ode, final double[] hY,
                             final ParametersController controller,
                             final ParameterConfiguration[] paramsAndSteps) {
        this.ode = ode;
        this.hY = hY.clone();
        this.controller = controller;
        this.hParam = new HashMap<String, Double>();

        // set up parameters for jacobian computation
        for (final ParameterConfiguration param : paramsAndSteps) {
            final String name = param.getParameterName();
            if (controller.isSupported(name)) {
                hParam.put(name, param.getHP());
            }
        }
    }

    /**
     * Get the underlying ode.
     *
     * @return underlying ode
     */
    public OrdinaryDifferentialEquation getODE() {
        return ode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDimension() {
        return ode.getDimension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] computeDerivatives(double t, double[] y)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return ode.computeDerivatives(t, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] computeMainStateJacobian(double t, double[] y, double[] yDot)
            throws MathIllegalArgumentException, MathIllegalStateException {

        final int n = ode.getDimension();
        final double[][] dFdY = new double[n][n];
        for (int j = 0; j < n; ++j) {
            final double savedYj = y[j];
            y[j] += hY[j];
            final double[] tmpDot = ode.computeDerivatives(t, y);
            for (int i = 0; i < n; ++i) {
                dFdY[i][j] = (tmpDot[i] - yDot[i]) / hY[j];
            }
            y[j] = savedYj;
        }
        return dFdY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getParametersNames() {
        return controller.getParametersNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupported(String name) {
        return controller.isSupported(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] computeParameterJacobian(final double t, final double[] y,
                                             final double[] yDot, final String paramName)
            throws MathIllegalArgumentException, MathIllegalStateException {

        final int n = ode.getDimension();
        final double[] dFdP = new double[n];
        if (controller.isSupported(paramName)) {

            // compute the jacobian df/dp w.r.t. parameter
            final double p = controller.getParameter(paramName);
            final double hP = hParam.get(paramName);
            controller.setParameter(paramName, p + hP);
            final double[] tmpDot = ode.computeDerivatives(t, y);
            for (int i = 0; i < n; ++i) {
                dFdP[i] = (tmpDot[i] - yDot[i]) / hP;
            }
            controller.setParameter(paramName, p);
        } else {
            Arrays.fill(dFdP, 0, n, 0.0);
        }

        return dFdP;

    }

}
