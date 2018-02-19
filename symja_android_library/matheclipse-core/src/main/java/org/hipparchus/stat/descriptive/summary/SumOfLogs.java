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
package org.hipparchus.stat.descriptive.summary;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.hipparchus.stat.descriptive.AggregatableStatistic;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Returns the sum of the natural logs for this collection of values.
 * <p>
 * Uses {@link FastMath#log(double)} to compute the logs.
 * Therefore,
 * <ul>
 * <li>If any of values are &lt; 0, the result is <code>NaN.</code></li>
 * <li>If all values are non-negative and less than
 * <code>Double.POSITIVE_INFINITY</code>,  but at least one value is 0, the
 * result is <code>Double.NEGATIVE_INFINITY.</code></li>
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
public class SumOfLogs extends AbstractStorelessUnivariateStatistic
        implements AggregatableStatistic<SumOfLogs>, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * Number of values that have been added
     */
    private int n;

    /**
     * The currently running value
     */
    private double value;

    /**
     * Create a SumOfLogs instance.
     */
    public SumOfLogs() {
        value = 0d;
        n = 0;
    }

    /**
     * Copy constructor, creates a new {@code SumOfLogs} identical
     * to the {@code original}.
     *
     * @param original the {@code SumOfLogs} instance to copy
     * @throws NullArgumentException if original is null
     */
    public SumOfLogs(SumOfLogs original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.n = original.n;
        this.value = original.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(final double d) {
        value += FastMath.log(d);
        n++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getResult() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        value = 0d;
        n = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregate(SumOfLogs other) {
        MathUtils.checkNotNull(other);
        if (other.n > 0) {
            this.n += other.n;
            this.value += other.value;
        }
    }

    /**
     * Returns the sum of the natural logs of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     *
     * @param values the input array
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the sum of the natural logs of the values or 0 if
     * length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {

        double sumLog = Double.NaN;
        if (MathArrays.verifyValues(values, begin, length, true)) {
            sumLog = 0.0;
            for (int i = begin; i < begin + length; i++) {
                sumLog += FastMath.log(values[i]);
            }
        }
        return sumLog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SumOfLogs copy() {
        return new SumOfLogs(this);
    }

}
