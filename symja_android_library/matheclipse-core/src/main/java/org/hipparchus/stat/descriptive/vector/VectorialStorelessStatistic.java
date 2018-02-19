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
package org.hipparchus.stat.descriptive.vector;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.stat.descriptive.StorelessMultivariateStatistic;
import org.hipparchus.stat.descriptive.StorelessUnivariateStatistic;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Uses an independent {@link StorelessUnivariateStatistic} instance
 * for each component of a vector.
 */
public class VectorialStorelessStatistic
        implements StorelessMultivariateStatistic, Serializable {

    /**
     * Serializable UID
     */
    private static final long serialVersionUID = 20160413L;

    /**
     * Statistic for each component
     */
    private final StorelessUnivariateStatistic[] stats;

    /**
     * Create a new VectorialStorelessStatistic with the given dimension
     * and statistic implementation. A copy of the provided statistic
     * will be created for each component of the vector.
     *
     * @param dimension           the vector dimension
     * @param univariateStatistic the prototype statistic
     * @throws MathIllegalArgumentException if dimension < 1
     */
    public VectorialStorelessStatistic(int dimension,
                                       StorelessUnivariateStatistic univariateStatistic) {
        if (dimension < 1) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL,
                    dimension, 1);
        }
        stats = new StorelessUnivariateStatistic[dimension];
        for (int i = 0; i < dimension; i++) {
            stats[i] = univariateStatistic.copy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(double[] d) {
        MathUtils.checkDimension(d.length, stats.length);
        for (int i = 0; i < stats.length; i++) {
            stats[i].increment(d[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getResult() {
        double[] result = new double[stats.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = stats[i].getResult();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return stats[0].getN();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        for (StorelessUnivariateStatistic stat : stats) {
            stat.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDimension() {
        return stats.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(stats);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VectorialStorelessStatistic)) {
            return false;
        }
        VectorialStorelessStatistic other = (VectorialStorelessStatistic) obj;
        if (!Arrays.equals(stats, other.stats)) {
            return false;
        }
        return true;
    }

}
