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

import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.sampling.AbstractODEStateInterpolator;
import org.hipparchus.util.FastMath;

/**
 * This class implements an interpolator for the Gragg-Bulirsch-Stoer
 * integrator.
 * <p>
 * <p>This interpolator compute dense output inside the last step
 * produced by a Gragg-Bulirsch-Stoer integrator.</p>
 * <p>
 * <p>
 * This implementation is basically a reimplementation in Java of the
 * <a
 * href="http://www.unige.ch/math/folks/hairer/prog/nonstiff/odex.f">odex</a>
 * fortran code by E. Hairer and G. Wanner. The redistribution policy
 * for this code is available <a
 * href="http://www.unige.ch/~hairer/prog/licence.txt">here</a>, for
 * convenience, it is reproduced below.</p>
 * </p>
 * <p>
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>Copyright (c) 2004, Ernst Hairer</td></tr>
 * <p>
 * <tr><td>Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * <ul>
 * <li>Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.</li>
 * </ul></td></tr>
 * <p>
 * <tr><td><strong>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</strong></td></tr>
 * </table>
 *
 * @see GraggBulirschStoerIntegrator
 */

class GraggBulirschStoerStateInterpolator
        extends AbstractODEStateInterpolator {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160329L;

    /**
     * Scaled derivatives at the middle of the step $\tau$.
     * (element k is $h^{k} d^{k}y(\tau)/dt^{k}$ where h is step size...)
     */
    private final double[][] yMidDots;

    /**
     * Interpolation polynomials.
     */
    private final double[][] polynomials;

    /**
     * Error coefficients for the interpolation.
     */
    private final double[] errfac;

    /**
     * Degree of the interpolation polynomials.
     */
    private final int currentDegree;

    /**
     * Simple constructor.
     *
     * @param forward             integration direction indicator
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param softPreviousState   start of the restricted step
     * @param softCurrentState    end of the restricted step
     * @param mapper              equations mapper for the all equations
     * @param yMidDots            scaled derivatives at the middle of the step $\tau$
     *                            (element k is $h^{k} d^{k}y(\tau)/dt^{k}$ where h is step size...)
     * @param mu                  degree of the interpolation polynomial
     */
    GraggBulirschStoerStateInterpolator(final boolean forward,
                                        final ODEStateAndDerivative globalPreviousState,
                                        final ODEStateAndDerivative globalCurrentState,
                                        final ODEStateAndDerivative softPreviousState,
                                        final ODEStateAndDerivative softCurrentState,
                                        final EquationsMapper mapper,
                                        final double[][] yMidDots,
                                        final int mu) {
        super(forward,
                globalPreviousState, globalCurrentState, softPreviousState, softCurrentState,
                mapper);

        this.yMidDots = yMidDots.clone();
        this.currentDegree = mu + 4;
        this.polynomials = new double[currentDegree + 1][getCurrentState().getCompleteStateDimension()];

        // initialize the error factors array for interpolation
        if (currentDegree <= 4) {
            errfac = null;
        } else {
            errfac = new double[currentDegree - 4];
            for (int i = 0; i < errfac.length; ++i) {
                final int ip5 = i + 5;
                errfac[i] = 1.0 / (ip5 * ip5);
                final double e = 0.5 * FastMath.sqrt(((double) (i + 1)) / ip5);
                for (int j = 0; j <= i; ++j) {
                    errfac[i] *= e / (j + 1);
                }
            }
        }

        // compute the interpolation coefficients
        computeCoefficients(mu);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GraggBulirschStoerStateInterpolator create(final boolean newForward,
                                                         final ODEStateAndDerivative newGlobalPreviousState,
                                                         final ODEStateAndDerivative newGlobalCurrentState,
                                                         final ODEStateAndDerivative newSoftPreviousState,
                                                         final ODEStateAndDerivative newSoftCurrentState,
                                                         final EquationsMapper newMapper) {
        return new GraggBulirschStoerStateInterpolator(newForward,
                newGlobalPreviousState, newGlobalCurrentState,
                newSoftPreviousState, newSoftCurrentState,
                newMapper, yMidDots, currentDegree - 4);
    }

    /**
     * Compute the interpolation coefficients for dense output.
     *
     * @param mu degree of the interpolation polynomial
     */
    private void computeCoefficients(final int mu) {

        final double[] y0Dot = getGlobalPreviousState().getCompleteDerivative();
        final double[] y1Dot = getGlobalCurrentState().getCompleteDerivative();
        final double[] y1 = getGlobalCurrentState().getCompleteState();

        final double[] previousState = getGlobalPreviousState().getCompleteState();
        final double h = getGlobalCurrentState().getTime() - getGlobalPreviousState().getTime();
        for (int i = 0; i < previousState.length; ++i) {

            final double yp0 = h * y0Dot[i];
            final double yp1 = h * y1Dot[i];
            final double ydiff = y1[i] - previousState[i];
            final double aspl = ydiff - yp1;
            final double bspl = yp0 - ydiff;

            polynomials[0][i] = previousState[i];
            polynomials[1][i] = ydiff;
            polynomials[2][i] = aspl;
            polynomials[3][i] = bspl;

            if (mu < 0) {
                return;
            }

            // compute the remaining coefficients
            final double ph0 = 0.5 * (previousState[i] + y1[i]) + 0.125 * (aspl + bspl);
            polynomials[4][i] = 16 * (yMidDots[0][i] - ph0);

            if (mu > 0) {
                final double ph1 = ydiff + 0.25 * (aspl - bspl);
                polynomials[5][i] = 16 * (yMidDots[1][i] - ph1);

                if (mu > 1) {
                    final double ph2 = yp1 - yp0;
                    polynomials[6][i] = 16 * (yMidDots[2][i] - ph2 + polynomials[4][i]);

                    if (mu > 2) {
                        final double ph3 = 6 * (bspl - aspl);
                        polynomials[7][i] = 16 * (yMidDots[3][i] - ph3 + 3 * polynomials[5][i]);

                        for (int j = 4; j <= mu; ++j) {
                            final double fac1 = 0.5 * j * (j - 1);
                            final double fac2 = 2 * fac1 * (j - 2) * (j - 3);
                            polynomials[j + 4][i] =
                                    16 * (yMidDots[j][i] + fac1 * polynomials[j + 2][i] - fac2 * polynomials[j][i]);
                        }

                    }
                }
            }
        }

    }

    /**
     * Estimate interpolation error.
     *
     * @param scale scaling array
     * @return estimate of the interpolation error
     */
    public double estimateError(final double[] scale) {
        double error = 0;
        if (currentDegree >= 5) {
            for (int i = 0; i < scale.length; ++i) {
                final double e = polynomials[currentDegree][i] / scale[i];
                error += e * e;
            }
            error = FastMath.sqrt(error / scale.length) * errfac[currentDegree - 5];
        }
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ODEStateAndDerivative computeInterpolatedStateAndDerivatives(final EquationsMapper mapper,
                                                                           final double time, final double theta,
                                                                           final double thetaH, final double oneMinusThetaH) {

        final int dimension = mapper.getTotalDimension();

        final double h = thetaH / theta;
        final double oneMinusTheta = 1.0 - theta;
        final double theta05 = theta - 0.5;
        final double tOmT = theta * oneMinusTheta;
        final double t4 = tOmT * tOmT;
        final double t4Dot = 2 * tOmT * (1 - 2 * theta);
        final double dot1 = 1.0 / h;
        final double dot2 = theta * (2 - 3 * theta) / h;
        final double dot3 = ((3 * theta - 4) * theta + 1) / h;

        final double[] interpolatedState = new double[dimension];
        final double[] interpolatedDerivatives = new double[dimension];
        for (int i = 0; i < dimension; ++i) {

            final double p0 = polynomials[0][i];
            final double p1 = polynomials[1][i];
            final double p2 = polynomials[2][i];
            final double p3 = polynomials[3][i];
            interpolatedState[i] = p0 + theta * (p1 + oneMinusTheta * (p2 * theta + p3 * oneMinusTheta));
            interpolatedDerivatives[i] = dot1 * p1 + dot2 * p2 + dot3 * p3;

            if (currentDegree > 3) {
                double cDot = 0;
                double c = polynomials[currentDegree][i];
                for (int j = currentDegree - 1; j > 3; --j) {
                    final double d = 1.0 / (j - 3);
                    cDot = d * (theta05 * cDot + c);
                    c = polynomials[j][i] + c * d * theta05;
                }
                interpolatedState[i] += t4 * c;
                interpolatedDerivatives[i] += (t4 * cDot + t4Dot * c) / h;
            }

        }

        if (h == 0) {
            // in this degenerated case, the previous computation leads to NaN for derivatives
            // we fix this by using the derivatives at midpoint
            System.arraycopy(yMidDots[1], 0, interpolatedDerivatives, 0, dimension);
        }

        return mapper.mapStateAndDerivative(time, interpolatedState, interpolatedDerivatives);

    }

}
