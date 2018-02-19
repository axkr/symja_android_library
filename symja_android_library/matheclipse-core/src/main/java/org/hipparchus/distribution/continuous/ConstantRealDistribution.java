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

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.MathUtils;

/**
 * Implementation of the constant real distribution.
 */
public class ConstantRealDistribution extends AbstractRealDistribution {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * Constant value of the distribution
     */
    private final double value;

    /**
     * Create a constant real distribution with the given value.
     *
     * @param value the constant value of this distribution
     */
    public ConstantRealDistribution(double value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        return x == value ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        return x < value ? 0 : 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(final double p)
            throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalMean() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalVariance() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportLowerBound() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportUpperBound() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
