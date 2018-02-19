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
package org.hipparchus.distribution.continuous;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.special.Beta;
import org.hipparchus.special.Gamma;
import org.hipparchus.util.FastMath;

/**
 * Implementation of Student's t-distribution.
 */
public class TDistribution extends AbstractRealDistribution {
    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * The degrees of freedom.
     */
    private final double degreesOfFreedom;
    /**
     * Static computation factor based on degreesOfFreedom.
     */
    private final double factor;

    /**
     * Create a t distribution using the given degrees of freedom.
     *
     * @param degreesOfFreedom Degrees of freedom.
     * @throws MathIllegalArgumentException if {@code degreesOfFreedom <= 0}
     */
    public TDistribution(double degreesOfFreedom)
            throws MathIllegalArgumentException {
        this(degreesOfFreedom, DEFAULT_SOLVER_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a t distribution using the given degrees of freedom and the
     * specified inverse cumulative probability absolute accuracy.
     *
     * @param degreesOfFreedom   Degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse
     *                           cumulative probability estimates
     *                           (defaults to {@link #DEFAULT_SOLVER_ABSOLUTE_ACCURACY}).
     * @throws MathIllegalArgumentException if {@code degreesOfFreedom <= 0}
     */
    public TDistribution(double degreesOfFreedom, double inverseCumAccuracy)
            throws MathIllegalArgumentException {
        super(inverseCumAccuracy);

        if (degreesOfFreedom <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DEGREES_OF_FREEDOM,
                    degreesOfFreedom);
        }
        this.degreesOfFreedom = degreesOfFreedom;

        final double n = degreesOfFreedom;
        final double nPlus1Over2 = (n + 1) / 2;
        factor = Gamma.logGamma(nPlus1Over2) -
                0.5 * (FastMath.log(FastMath.PI) + FastMath.log(n)) -
                Gamma.logGamma(n / 2);
    }

    /**
     * Access the degrees of freedom.
     *
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        return FastMath.exp(logDensity(x));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double logDensity(double x) {
        final double n = degreesOfFreedom;
        final double nPlus1Over2 = (n + 1) / 2;
        return factor - nPlus1Over2 * FastMath.log(1 + x * x / n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        double ret;
        if (x == 0) {
            ret = 0.5;
        } else {
            double t =
                    Beta.regularizedBeta(
                            degreesOfFreedom / (degreesOfFreedom + (x * x)),
                            0.5 * degreesOfFreedom,
                            0.5);
            if (x < 0.0) {
                ret = 0.5 * t;
            } else {
                ret = 1.0 - 0.5 * t;
            }
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For degrees of freedom parameter {@code df}, the mean is
     * <ul>
     * <li>if {@code df > 1} then {@code 0},</li>
     * <li>else undefined ({@code Double.NaN}).</li>
     * </ul>
     */
    @Override
    public double getNumericalMean() {
        final double df = getDegreesOfFreedom();

        if (df > 1) {
            return 0;
        }

        return Double.NaN;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For degrees of freedom parameter {@code df}, the variance is
     * <ul>
     * <li>if {@code df > 2} then {@code df / (df - 2)},</li>
     * <li>if {@code 1 < df <= 2} then positive infinity
     * ({@code Double.POSITIVE_INFINITY}),</li>
     * <li>else undefined ({@code Double.NaN}).</li>
     * </ul>
     */
    @Override
    public double getNumericalVariance() {
        final double df = getDegreesOfFreedom();

        if (df > 2) {
            return df / (df - 2);
        }

        if (df > 1 && df <= 2) {
            return Double.POSITIVE_INFINITY;
        }

        return Double.NaN;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always negative infinity no matter the
     * parameters.
     *
     * @return lower bound of the support (always
     * {@code Double.NEGATIVE_INFINITY})
     */
    @Override
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is always positive infinity no matter the
     * parameters.
     *
     * @return upper bound of the support (always
     * {@code Double.POSITIVE_INFINITY})
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
