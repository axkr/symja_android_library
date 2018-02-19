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
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Computes the sample standard deviation.
 * <p>
 * The standard deviation is the positive square root of the variance.
 * This implementation wraps a {@link Variance} instance.
 * <p>
 * The <code>isBiasCorrected</code> property of the wrapped Variance
 * instance is exposed, so that this class can be used to compute both
 * the "sample standard deviation" (the square root of the bias-corrected
 * "sample variance") or the "population standard deviation" (the square
 * root of the non-bias-corrected "population variance").
 * See {@link Variance} for more information.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class StandardDeviation extends AbstractStorelessUnivariateStatistic
        implements Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * Wrapped Variance instance
     */
    private final Variance variance;

    /**
     * Constructs a StandardDeviation.  Sets the underlying {@link Variance}
     * instance's <code>isBiasCorrected</code> property to true.
     */
    public StandardDeviation() {
        this(new Variance());
    }

    /**
     * Constructs a StandardDeviation from an external second moment.
     *
     * @param m2 the external moment
     */
    public StandardDeviation(final SecondMoment m2) {
        this(new Variance(m2));
    }

    /**
     * Constructs a StandardDeviation with the specified value for the
     * <code>isBiasCorrected</code> property.  If this property is set to
     * <code>true</code>, the {@link Variance} used in computing results will
     * use the bias-corrected, or "sample" formula.  See {@link Variance} for
     * details.
     *
     * @param isBiasCorrected whether or not the variance computation will use
     *                        the bias-corrected formula
     */
    public StandardDeviation(boolean isBiasCorrected) {
        this(new Variance(isBiasCorrected));
    }

    /**
     * Constructs a StandardDeviation with the specified value for the
     * <code>isBiasCorrected</code> property and the supplied external moment.
     * If <code>isBiasCorrected</code> is set to <code>true</code>, the
     * {@link Variance} used in computing results will use the bias-corrected,
     * or "sample" formula. See {@link Variance} for details.
     *
     * @param isBiasCorrected whether or not the variance computation will use
     *                        the bias-corrected formula
     * @param m2              the external moment
     */
    public StandardDeviation(boolean isBiasCorrected, SecondMoment m2) {
        this(new Variance(isBiasCorrected, m2));
    }

    /**
     * Create a new instance with the given variance.
     *
     * @param variance the variance to use
     */
    private StandardDeviation(Variance variance) {
        this.variance = variance;
    }

    /**
     * Copy constructor, creates a new {@code StandardDeviation} identical
     * to the {@code original}.
     *
     * @param original the {@code StandardDeviation} instance to copy
     * @throws NullArgumentException if original is null
     */
    public StandardDeviation(StandardDeviation original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.variance = original.variance.copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(final double d) {
        variance.increment(d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return variance.getN();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getResult() {
        return FastMath.sqrt(variance.getResult());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        variance.clear();
    }

    /**
     * Returns the Standard Deviation of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values the input array
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the standard deviation of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {
        return FastMath.sqrt(variance.evaluate(values, begin, length));
    }

    /**
     * Returns the Standard Deviation of the entries in the specified portion of
     * the input array, using the precomputed mean value.  Returns
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * The formula used assumes that the supplied mean value is the arithmetic
     * mean of the sample data, not a known population parameter.  This method
     * is supplied only to save computation when the mean has already been
     * computed.
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values the input array
     * @param mean   the precomputed mean value
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the standard deviation of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    public double evaluate(final double[] values, final double mean,
                           final int begin, final int length)
            throws MathIllegalArgumentException {
        return FastMath.sqrt(variance.evaluate(values, mean, begin, length));
    }

    /**
     * Returns the Standard Deviation of the entries in the input array, using
     * the precomputed mean value.  Returns
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * The formula used assumes that the supplied mean value is the arithmetic
     * mean of the sample data, not a known population parameter.  This method
     * is supplied only to save computation when the mean has already been
     * computed.
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values the input array
     * @param mean   the precomputed mean value
     * @return the standard deviation of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null
     */
    public double evaluate(final double[] values, final double mean)
            throws MathIllegalArgumentException {
        return FastMath.sqrt(variance.evaluate(values, mean));
    }

    /**
     * @return Returns the isBiasCorrected.
     */
    public boolean isBiasCorrected() {
        return variance.isBiasCorrected();
    }

    /**
     * Returns a new copy of this standard deviation with the given
     * bias correction setting.
     *
     * @param biasCorrection The bias correction flag to set.
     * @return a copy of this instance with the given bias correction setting
     */
    public StandardDeviation withBiasCorrection(boolean biasCorrection) {
        return new StandardDeviation(variance.withBiasCorrection(biasCorrection));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StandardDeviation copy() {
        return new StandardDeviation(this);
    }

}
