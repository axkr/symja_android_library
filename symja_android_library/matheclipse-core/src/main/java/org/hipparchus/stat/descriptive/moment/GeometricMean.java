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
package org.hipparchus.stat.descriptive.moment;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.hipparchus.stat.descriptive.AggregatableStatistic;
import org.hipparchus.stat.descriptive.summary.SumOfLogs;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
 * geometric mean </a> of the available values.
 * <p>
 * Uses a {@link SumOfLogs} instance to compute sum of logs and returns
 * <code> exp( 1/n  (sum of logs) ).</code>  Therefore,
 * <ul>
 * <li>If any of values are < 0, the result is <code>NaN.</code></li>
 * <li>If all values are non-negative and less than
 * <code>Double.POSITIVE_INFINITY</code>,  but at least one value is 0, the
 * result is <code>0.</code></li>
 * <li>If both <code>Double.POSITIVE_INFINITY</code> and
 * <code>Double.NEGATIVE_INFINITY</code> are among the values, the result is
 * <code>NaN.</code></li>
 * </ul>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class GeometricMean extends AbstractStorelessUnivariateStatistic
        implements AggregatableStatistic<GeometricMean>, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * Wrapped SumOfLogs instance
     */
    private final SumOfLogs sumOfLogs;

    /**
     * Determines whether or not this statistic can be incremented or cleared.
     * <p>
     * Statistics based on (constructed from) external statistics cannot
     * be incremented or cleared.
     */
    private final boolean incSumOfLogs;

    /**
     * Create a GeometricMean instance.
     */
    public GeometricMean() {
        sumOfLogs = new SumOfLogs();
        incSumOfLogs = true;
    }

    /**
     * Create a GeometricMean instance using the given SumOfLogs instance.
     *
     * @param sumOfLogs sum of logs instance to use for computation.
     */
    public GeometricMean(SumOfLogs sumOfLogs) {
        this.sumOfLogs = sumOfLogs;
        incSumOfLogs = false;
    }

    /**
     * Copy constructor, creates a new {@code GeometricMean} identical
     * to the {@code original}.
     *
     * @param original the {@code GeometricMean} instance to copy
     * @throws NullArgumentException if original is null
     */
    public GeometricMean(GeometricMean original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.sumOfLogs = original.sumOfLogs.copy();
        this.incSumOfLogs = original.incSumOfLogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeometricMean copy() {
        return new GeometricMean(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(final double d) {
        if (incSumOfLogs) {
            sumOfLogs.increment(d);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getResult() {
        if (sumOfLogs.getN() > 0) {
            return FastMath.exp(sumOfLogs.getResult() / sumOfLogs.getN());
        } else {
            return Double.NaN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        if (incSumOfLogs) {
            sumOfLogs.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregate(GeometricMean other) {
        MathUtils.checkNotNull(other);
        if (incSumOfLogs) {
            this.sumOfLogs.aggregate(other.sumOfLogs);
        }
    }

    /**
     * Returns the geometric mean of the entries in the specified portion
     * of the input array.
     * <p>
     * See {@link GeometricMean} for details on the computing algorithm.
     *
     * @param values input array containing the values
     * @param begin  first array element to include
     * @param length the number of elements to include
     * @return the geometric mean or Double.NaN if length = 0 or
     * any of the values are &lt;= 0.
     * @throws MathIllegalArgumentException if the input array is null or the array
     *                                      index parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {
        return FastMath.exp(sumOfLogs.evaluate(values, begin, length) / length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return sumOfLogs.getN();
    }

}
