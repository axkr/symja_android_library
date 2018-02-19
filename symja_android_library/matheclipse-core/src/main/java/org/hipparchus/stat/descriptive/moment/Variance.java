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
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Computes the variance of the available values.  By default, the unbiased
 * "sample variance" definitional formula is used:
 * <p>
 * variance = sum((x_i - mean)^2) / (n - 1)
 * <p>
 * where mean is the {@link Mean} and <code>n</code> is the number
 * of sample observations.
 * <p>
 * The definitional formula does not have good numerical properties, so
 * this implementation does not compute the statistic using the definitional
 * formula.
 * <ul>
 * <li> The <code>getResult</code> method computes the variance using
 * updating formulas based on West's algorithm, as described in
 * <a href="http://doi.acm.org/10.1145/359146.359152"> Chan, T. F. and
 * J. G. Lewis 1979, <i>Communications of the ACM</i>,
 * vol. 22 no. 9, pp. 526-531.</a></li>
 * <li> The <code>evaluate</code> methods leverage the fact that they have the
 * full array of values in memory to execute a two-pass algorithm.
 * Specifically, these methods use the "corrected two-pass algorithm" from
 * Chan, Golub, Levesque, <i>Algorithms for Computing the Sample Variance</i>,
 * American Statistician, vol. 37, no. 3 (1983) pp. 242-247.</li>
 * </ul>
 * <p>
 * Note that adding values using <code>increment</code> or
 * <code>incrementAll</code> and then executing <code>getResult</code> will
 * sometimes give a different, less accurate, result than executing
 * <code>evaluate</code> with the full array of values. The former approach
 * should only be used when the full array of values is not available.
 * <p>
 * The "population variance"  ( sum((x_i - mean)^2) / n ) can also
 * be computed using this statistic.  The <code>isBiasCorrected</code>
 * property determines whether the "population" or "sample" value is
 * returned by the <code>evaluate</code> and <code>getResult</code> methods.
 * To compute population variances, set this property to <code>false.</code>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class Variance extends AbstractStorelessUnivariateStatistic
        implements AggregatableStatistic<Variance>, WeightedEvaluation, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * SecondMoment is used in incremental calculation of Variance
     */
    protected final SecondMoment moment;

    /**
     * Whether or not {@link #increment(double)} should increment
     * the internal second moment. When a Variance is constructed with an
     * external SecondMoment as a constructor parameter, this property is
     * set to false and increments must be applied to the second moment
     * directly.
     */
    protected final boolean incMoment;

    /**
     * Whether or not bias correction is applied when computing the
     * value of the statistic. True means that bias is corrected.  See
     * {@link Variance} for details on the formula.
     */
    private final boolean isBiasCorrected;

    /**
     * Constructs a Variance with default (true) <code>isBiasCorrected</code>
     * property.
     */
    public Variance() {
        this(true);
    }

    /**
     * Constructs a Variance based on an external second moment.
     * <p>
     * When this constructor is used, the statistic may only be
     * incremented via the moment, i.e., {@link #increment(double)}
     * does nothing; whereas {@code m2.increment(value)} increments
     * both {@code m2} and the Variance instance constructed from it.
     *
     * @param m2 the SecondMoment (Third or Fourth moments work here as well.)
     */
    public Variance(final SecondMoment m2) {
        this(true, m2);
    }

    /**
     * Constructs a Variance with the specified <code>isBiasCorrected</code>
     * property.
     *
     * @param isBiasCorrected setting for bias correction - true means
     *                        bias will be corrected and is equivalent to using the argumentless
     *                        constructor
     */
    public Variance(boolean isBiasCorrected) {
        this(new SecondMoment(), true, isBiasCorrected);
    }

    /**
     * Constructs a Variance with the specified <code>isBiasCorrected</code>
     * property and the supplied external second moment.
     *
     * @param isBiasCorrected setting for bias correction - true means
     *                        bias will be corrected
     * @param m2              the SecondMoment (Third or Fourth moments work
     *                        here as well.)
     */
    public Variance(boolean isBiasCorrected, SecondMoment m2) {
        this(m2, false, isBiasCorrected);
    }

    /**
     * Constructs a Variance with the specified parameters.
     *
     * @param m2              the SecondMoment (Third or Fourth moments work
     *                        here as well.)
     * @param incMoment       if the moment shall be incremented
     * @param isBiasCorrected setting for bias correction - true means
     *                        bias will be corrected
     */
    private Variance(SecondMoment m2, boolean incMoment, boolean isBiasCorrected) {
        this.moment = m2;
        this.incMoment = incMoment;
        this.isBiasCorrected = isBiasCorrected;
    }

    /**
     * Copy constructor, creates a new {@code Variance} identical
     * to the {@code original}.
     *
     * @param original the {@code Variance} instance to copy
     * @throws NullArgumentException if original is null
     */
    public Variance(Variance original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.moment = original.moment.copy();
        this.incMoment = original.incMoment;
        this.isBiasCorrected = original.isBiasCorrected;
    }

    /**
     * {@inheritDoc}
     * <p>If all values are available, it is more accurate to use
     * {@link #evaluate(double[])} rather than adding values one at a time
     * using this method and then executing {@link #getResult}, since
     * <code>evaluate</code> leverages the fact that is has the full
     * list of values together to execute a two-pass algorithm.
     * See {@link Variance}.
     * <p>
     * Note also that when {@link #Variance(SecondMoment)} is used to
     * create a Variance, this method does nothing. In that case, the
     * SecondMoment should be incremented directly.
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
    public double getResult() {
        if (moment.n == 0) {
            return Double.NaN;
        } else if (moment.n == 1) {
            return 0d;
        } else {
            if (isBiasCorrected) {
                return moment.m2 / (moment.n - 1d);
            } else {
                return moment.m2 / (moment.n);
            }
        }
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
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregate(Variance other) {
        MathUtils.checkNotNull(other);
        if (incMoment) {
            this.moment.aggregate(other.moment);
        }
    }

    /**
     * Returns the variance of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.  Note that Double.NaN may also be returned if the input
     * includes NaN and / or infinite values.
     * <p>
     * See {@link Variance} for details on the computing algorithm.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if the array is null.</p>
     *
     * @param values the input array
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {

        double var = Double.NaN;

        if (MathArrays.verifyValues(values, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double m = StatUtils.mean(values, begin, length);
                var = evaluate(values, m, begin, length);
            }
        }
        return var;
    }

    /**
     * Returns the weighted variance of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Uses the formula
     * <pre>
     *   &Sigma;(weights[i]*(values[i] - weightedMean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </pre>
     * where weightedMean is the weighted mean.
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use
     * <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length));</code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
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
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values  the input array
     * @param weights the weights array
     * @param begin   index of the first array element to include
     * @param length  the number of elements to include
     * @return the weighted variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length)
            throws MathIllegalArgumentException {

        double var = Double.NaN;
        if (MathArrays.verifyValues(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                Mean mean = new Mean();
                double m = mean.evaluate(values, weights, begin, length);
                var = evaluate(values, weights, m, begin, length);
            }
        }
        return var;
    }

    /**
     * Returns the variance of the entries in the specified portion of
     * the input array, using the precomputed mean value. Returns
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.
     * <p>
     * The formula used assumes that the supplied mean value is the arithmetic
     * mean of the sample data, not a known population parameter.  This method
     * is supplied only to save computation when the mean has already been
     * computed.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values the input array
     * @param mean   the precomputed mean value
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    public double evaluate(final double[] values, final double mean,
                           final int begin, final int length)
            throws MathIllegalArgumentException {

        double var = Double.NaN;
        if (MathArrays.verifyValues(values, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    dev = values[i] - mean;
                    accum += dev * dev;
                    accum2 += dev;
                }
                double len = length;
                if (isBiasCorrected) {
                    var = (accum - (accum2 * accum2 / len)) / (len - 1.0);
                } else {
                    var = (accum - (accum2 * accum2 / len)) / len;
                }
            }
        }
        return var;
    }

    /**
     * Returns the variance of the entries in the input array, using the
     * precomputed mean value.  Returns <code>Double.NaN</code> if the array
     * is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.
     * <p>
     * If <code>isBiasCorrected</code> is <code>true</code> the formula used
     * assumes that the supplied mean value is the arithmetic mean of the
     * sample data, not a known population parameter.  If the mean is a known
     * population parameter, or if the "population" version of the variance is
     * desired, set <code>isBiasCorrected</code> to <code>false</code> before
     * invoking this method.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values the input array
     * @param mean   the precomputed mean value
     * @return the variance of the values or Double.NaN if the array is empty
     * @throws MathIllegalArgumentException if the array is null
     */
    public double evaluate(final double[] values, final double mean)
            throws MathIllegalArgumentException {
        return evaluate(values, mean, 0, values.length);
    }

    /**
     * Returns the weighted variance of the entries in the specified portion of
     * the input array, using the precomputed weighted mean value.  Returns
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * Uses the formula
     * <pre>
     *   &Sigma;(weights[i]*(values[i] - mean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </pre>
     * <p>
     * The formula used assumes that the supplied mean value is the weighted arithmetic
     * mean of the sample data, not a known population parameter. This method
     * is supplied only to save computation when the mean has already been
     * computed.
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use
     * <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length), mean);</code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     * <li>the weights array is null</li>
     * <li>the weights array does not have the same length as the values array</li>
     * <li>the weights array contains one or more infinite values</li>
     * <li>the weights array contains one or more NaN values</li>
     * <li>the weights array contains negative values</li>
     * <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values  the input array
     * @param weights the weights array
     * @param mean    the precomputed weighted mean value
     * @param begin   index of the first array element to include
     * @param length  the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    public double evaluate(final double[] values, final double[] weights,
                           final double mean, final int begin, final int length)
            throws MathIllegalArgumentException {

        double var = Double.NaN;

        if (MathArrays.verifyValues(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    dev = values[i] - mean;
                    accum += weights[i] * (dev * dev);
                    accum2 += weights[i] * dev;
                }

                double sumWts = 0;
                for (int i = begin; i < begin + length; i++) {
                    sumWts += weights[i];
                }

                if (isBiasCorrected) {
                    var = (accum - (accum2 * accum2 / sumWts)) / (sumWts - 1.0);
                } else {
                    var = (accum - (accum2 * accum2 / sumWts)) / sumWts;
                }
            }
        }
        return var;
    }

    /**
     * Returns the weighted variance of the values in the input array, using
     * the precomputed weighted mean value.
     * <p>
     * Uses the formula
     * <pre>
     *   &Sigma;(weights[i]*(values[i] - mean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </pre>
     * <p>
     * The formula used assumes that the supplied mean value is the weighted arithmetic
     * mean of the sample data, not a known population parameter. This method
     * is supplied only to save computation when the mean has already been
     * computed.
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use
     * <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length), mean);</code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     * <li>the weights array is null</li>
     * <li>the weights array does not have the same length as the values array</li>
     * <li>the weights array contains one or more infinite values</li>
     * <li>the weights array contains one or more NaN values</li>
     * <li>the weights array contains negative values</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.
     *
     * @param values  the input array
     * @param weights the weights array
     * @param mean    the precomputed weighted mean value
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    public double evaluate(final double[] values, final double[] weights, final double mean)
            throws MathIllegalArgumentException {
        return evaluate(values, weights, mean, 0, values.length);
    }

    /**
     * @return Returns the isBiasCorrected.
     */
    public boolean isBiasCorrected() {
        return isBiasCorrected;
    }

    /**
     * Returns a new copy of this variance with the given bias correction
     * setting.
     *
     * @param biasCorrection The bias correction flag to set.
     * @return a copy of this instance with the given bias correction setting
     */
    public Variance withBiasCorrection(boolean biasCorrection) {
        return new Variance(this.moment, this.incMoment, biasCorrection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variance copy() {
        return new Variance(this);
    }

}
