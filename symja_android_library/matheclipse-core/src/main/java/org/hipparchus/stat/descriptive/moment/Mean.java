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
import org.hipparchus.stat.StatUtils;
import org.hipparchus.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.hipparchus.stat.descriptive.AggregatableStatistic;
import org.hipparchus.stat.descriptive.WeightedEvaluation;
import org.hipparchus.stat.descriptive.summary.Sum;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Computes the arithmetic mean of a set of values. Uses the definitional
 * formula:
 * <p>
 * mean = sum(x_i) / n
 * <p>
 * where <code>n</code> is the number of observations.
 * <p>
 * When {@link #increment(double)} is used to add data incrementally from a
 * stream of (unstored) values, the value of the statistic that
 * {@link #getResult()} returns is computed using the following recursive
 * updating algorithm:
 * <ol>
 * <li>Initialize <code>m = </code> the first value</li>
 * <li>For each additional value, update using <br>
 * <code>m = m + (new value - m) / (number of observations)</code></li>
 * </ol>
 * <p>
 * If {@link #evaluate(double[])} is used to compute the mean of an array
 * of stored values, a two-pass, corrected algorithm is used, starting with
 * the definitional formula computed using the array of stored values and then
 * correcting this by adding the mean deviation of the data values from the
 * arithmetic mean. See, e.g. "Comparison of Several Algorithms for Computing
 * Sample Means and Variances," Robert F. Ling, Journal of the American
 * Statistical Association, Vol. 69, No. 348 (Dec., 1974), pp. 859-866.
 * <p>
 * Returns <code>Double.NaN</code> if the dataset is empty. Note that
 * Double.NaN may also be returned if the input includes NaN and / or infinite
 * values.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class Mean extends AbstractStorelessUnivariateStatistic
        implements AggregatableStatistic<Mean>, WeightedEvaluation, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * First moment on which this statistic is based.
     */
    protected final FirstMoment moment;

    /**
     * Determines whether or not this statistic can be incremented or cleared.
     * <p>
     * Statistics based on (constructed from) external moments cannot
     * be incremented or cleared.
     */
    protected final boolean incMoment;

    /**
     * Constructs a Mean.
     */
    public Mean() {
        moment = new FirstMoment();
        incMoment = true;
    }

    /**
     * Constructs a Mean with an External Moment.
     *
     * @param m1 the moment
     */
    public Mean(final FirstMoment m1) {
        this.moment = m1;
        incMoment = false;
    }

    /**
     * Copy constructor, creates a new {@code Mean} identical
     * to the {@code original}.
     *
     * @param original the {@code Mean} instance to copy
     * @throws NullArgumentException if original is null
     */
    public Mean(Mean original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.moment = original.moment.copy();
        this.incMoment = original.incMoment;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note that when {@link #Mean(FirstMoment)} is used to
     * create a Mean, this method does nothing. In that case, the
     * FirstMoment should be incremented directly.
     */
    @Override
    public void increment(final double d) {
        if (incMoment) {
            moment.increment(d);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getResult() {
        return moment.m1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return moment.getN();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregate(Mean other) {
        MathUtils.checkNotNull(other);
        if (incMoment) {
            this.moment.aggregate(other.moment);
        }
    }

    /**
     * Returns the arithmetic mean of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     *
     * @param values the input array
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the mean of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {

        if (MathArrays.verifyValues(values, begin, length)) {
            double sampleSize = length;

            // Compute initial estimate using definitional formula
            double xbar = StatUtils.sum(values, begin, length) / sampleSize;

            // Compute correction factor in second pass
            double correction = 0;
            for (int i = begin; i < begin + length; i++) {
                correction += values[i] - xbar;
            }
            return xbar + (correction / sampleSize);
        }
        return Double.NaN;
    }

    /**
     * Returns the weighted arithmetic mean of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Throws <code>IllegalArgumentException</code> if either array is null.
     * <p>
     * See {@link Mean} for details on the computing algorithm. The two-pass algorithm
     * described above is used here, with weights applied in computing both the original
     * estimate and the correction factor.
     * <p>
     * Throws <code>IllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     * <li>the weights array is null</li>
     * <li>the weights array does not have the same length as the values array</li>
     * <li>the weights array contains one or more infinite values</li>
     * <li>the weights array contains one or more NaN values</li>
     * <li>the weights array contains negative values</li>
     * <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     *
     * @param values  the input array
     * @param weights the weights array
     * @param begin   index of the first array element to include
     * @param length  the number of elements to include
     * @return the mean of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length)
            throws MathIllegalArgumentException {

        if (MathArrays.verifyValues(values, weights, begin, length)) {
            Sum sum = new Sum();

            // Compute initial estimate using definitional formula
            double sumw = sum.evaluate(weights, begin, length);
            double xbarw = sum.evaluate(values, weights, begin, length) / sumw;

            // Compute correction factor in second pass
            double correction = 0;
            for (int i = begin; i < begin + length; i++) {
                correction += weights[i] * (values[i] - xbarw);
            }
            return xbarw + (correction / sumw);
        }
        return Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mean copy() {
        return new Mean(this);
    }

}
