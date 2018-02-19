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
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * This class implements the Logistic distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Logistic_distribution">Logistic Distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/LogisticDistribution.html">Logistic Distribution (Mathworld)</a>
 */
public class LogisticDistribution extends AbstractRealDistribution {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20141003L;

    /**
     * The location parameter.
     */
    private final double mu;
    /**
     * The scale parameter.
     */
    private final double s;

    /**
     * Build a new instance.
     *
     * @param mu location parameter
     * @param s  scale parameter (must be positive)
     * @throws MathIllegalArgumentException if {@code beta <= 0}
     */
    public LogisticDistribution(double mu, double s)
            throws MathIllegalArgumentException {
        if (s <= 0.0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NOT_POSITIVE_SCALE,
                    s);
        }

        this.mu = mu;
        this.s = s;
    }

    /**
     * Access the location parameter, {@code mu}.
     *
     * @return the location parameter.
     */
    public double getLocation() {
        return mu;
    }

    /**
     * Access the scale parameter, {@code s}.
     *
     * @return the scale parameter.
     */
    public double getScale() {
        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        double z = (x - mu) / s;
        double v = FastMath.exp(-z);
        return 1 / s * v / ((1.0 + v) * (1.0 + v));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        double z = 1 / s * (x - mu);
        return 1.0 / (1.0 + FastMath.exp(-z));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(double p) throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);

        if (p == 0) {
            return 0.0;
        } else if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return s * Math.log(p / (1.0 - p)) + mu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalMean() {
        return mu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalVariance() {
        return (MathUtils.PI_SQUARED / 3.0) * (1.0 / (s * s));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }

}
