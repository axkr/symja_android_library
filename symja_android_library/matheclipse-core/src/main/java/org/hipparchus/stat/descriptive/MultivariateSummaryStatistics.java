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
package org.hipparchus.stat.descriptive;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.descriptive.moment.GeometricMean;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.rank.Max;
import org.hipparchus.stat.descriptive.rank.Min;
import org.hipparchus.stat.descriptive.summary.Sum;
import org.hipparchus.stat.descriptive.summary.SumOfLogs;
import org.hipparchus.stat.descriptive.summary.SumOfSquares;
import org.hipparchus.stat.descriptive.vector.VectorialCovariance;
import org.hipparchus.stat.descriptive.vector.VectorialStorelessStatistic;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Computes summary statistics for a stream of n-tuples added using the
 * {@link #addValue(double[]) addValue} method. The data values are not stored
 * in memory, so this class can be used to compute statistics for very large
 * n-tuple streams.
 * <p>
 * To compute statistics for a stream of n-tuples, construct a
 * {@link MultivariateSummaryStatistics} instance with dimension n and then use
 * {@link #addValue(double[])} to add n-tuples. The <code>getXxx</code>
 * methods where Xxx is a statistic return an array of <code>double</code>
 * values, where for <code>i = 0,...,n-1</code> the i<sup>th</sup> array element
 * is the value of the given statistic for data range consisting of the i<sup>th</sup>
 * element of each of the input n-tuples.  For example, if <code>addValue</code> is
 * called with actual parameters {0, 1, 2}, then {3, 4, 5} and finally {6, 7, 8},
 * <code>getSum</code> will return a three-element array with values {0+3+6, 1+4+7, 2+5+8}
 * <p>
 * Note: This class is not thread-safe.
 */
public class MultivariateSummaryStatistics
        implements StatisticalMultivariateSummary, Serializable {

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = 20160424L;

    /**
     * Dimension of the data.
     */
    private final int k;

    /**
     * Sum statistic implementation
     */
    private final StorelessMultivariateStatistic sumImpl;
    /**
     * Sum of squares statistic implementation
     */
    private final StorelessMultivariateStatistic sumSqImpl;
    /**
     * Minimum statistic implementation
     */
    private final StorelessMultivariateStatistic minImpl;
    /**
     * Maximum statistic implementation
     */
    private final StorelessMultivariateStatistic maxImpl;
    /**
     * Sum of log statistic implementation
     */
    private final StorelessMultivariateStatistic sumLogImpl;
    /**
     * Geometric mean statistic implementation
     */
    private final StorelessMultivariateStatistic geoMeanImpl;
    /**
     * Mean statistic implementation
     */
    private final StorelessMultivariateStatistic meanImpl;
    /**
     * Covariance statistic implementation
     */
    private final VectorialCovariance covarianceImpl;

    /**
     * Count of values that have been added
     */
    private long n = 0;

    /**
     * Construct a MultivariateSummaryStatistics instance for the given
     * dimension. The returned instance will compute the unbiased sample
     * covariance.
     * <p>
     * The returned instance is <b>not</b> thread-safe.
     *
     * @param dimension dimension of the data
     */
    public MultivariateSummaryStatistics(int dimension) {
        this(dimension, true);
    }

    /**
     * Construct a MultivariateSummaryStatistics instance for the given
     * dimension.
     * <p>
     * The returned instance is <b>not</b> thread-safe.
     *
     * @param dimension                dimension of the data
     * @param covarianceBiasCorrection if true, the returned instance will compute
     *                                 the unbiased sample covariance, otherwise the population covariance
     */
    public MultivariateSummaryStatistics(int dimension, boolean covarianceBiasCorrection) {
        this.k = dimension;

        sumImpl = new VectorialStorelessStatistic(k, new Sum());
        sumSqImpl = new VectorialStorelessStatistic(k, new SumOfSquares());
        minImpl = new VectorialStorelessStatistic(k, new Min());
        maxImpl = new VectorialStorelessStatistic(k, new Max());
        sumLogImpl = new VectorialStorelessStatistic(k, new SumOfLogs());
        geoMeanImpl = new VectorialStorelessStatistic(k, new GeometricMean());
        meanImpl = new VectorialStorelessStatistic(k, new Mean());

        covarianceImpl = new VectorialCovariance(k, covarianceBiasCorrection);
    }

    /**
     * Add an n-tuple to the data
     *
     * @param value the n-tuple to add
     * @throws MathIllegalArgumentException if the array is null or the length
     *                                      of the array does not match the one used at construction
     */
    public void addValue(double[] value) throws MathIllegalArgumentException {
        MathUtils.checkNotNull(value, LocalizedCoreFormats.INPUT_ARRAY);
        MathUtils.checkDimension(value.length, k);
        sumImpl.increment(value);
        sumSqImpl.increment(value);
        minImpl.increment(value);
        maxImpl.increment(value);
        sumLogImpl.increment(value);
        geoMeanImpl.increment(value);
        meanImpl.increment(value);
        covarianceImpl.increment(value);
        n++;
    }

    /**
     * Resets all statistics and storage.
     */
    public void clear() {
        this.n = 0;
        minImpl.clear();
        maxImpl.clear();
        sumImpl.clear();
        sumLogImpl.clear();
        sumSqImpl.clear();
        geoMeanImpl.clear();
        meanImpl.clear();
        covarianceImpl.clear();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getDimension() {
        return k;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public long getN() {
        return n;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getSum() {
        return sumImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getSumSq() {
        return sumSqImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getSumLog() {
        return sumLogImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getMean() {
        return meanImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public RealMatrix getCovariance() {
        return covarianceImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getMax() {
        return maxImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getMin() {
        return minImpl.getResult();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double[] getGeometricMean() {
        return geoMeanImpl.getResult();
    }

    /**
     * Returns an array whose i<sup>th</sup> entry is the standard deviation of the
     * i<sup>th</sup> entries of the arrays that have been added using
     * {@link #addValue(double[])}
     *
     * @return the array of component standard deviations
     */
    @Override
    public double[] getStandardDeviation() {
        double[] stdDev = new double[k];
        if (getN() < 1) {
            Arrays.fill(stdDev, Double.NaN);
        } else if (getN() < 2) {
            Arrays.fill(stdDev, 0.0);
        } else {
            RealMatrix matrix = getCovariance();
            for (int i = 0; i < k; ++i) {
                stdDev[i] = FastMath.sqrt(matrix.getEntry(i, i));
            }
        }
        return stdDev;
    }

    /**
     * Generates a text report displaying
     * summary statistics from values that
     * have been added.
     *
     * @return String with line feeds displaying statistics
     */
    @Override
    public String toString() {
        final String separator = ", ";
        final String suffix = System.getProperty("line.separator");
        StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("MultivariateSummaryStatistics:" + suffix);
        outBuffer.append("n: " + getN() + suffix);
        append(outBuffer, getMin(), "min: ", separator, suffix);
        append(outBuffer, getMax(), "max: ", separator, suffix);
        append(outBuffer, getMean(), "mean: ", separator, suffix);
        append(outBuffer, getGeometricMean(), "geometric mean: ", separator, suffix);
        append(outBuffer, getSumSq(), "sum of squares: ", separator, suffix);
        append(outBuffer, getSumLog(), "sum of logarithms: ", separator, suffix);
        append(outBuffer, getStandardDeviation(), "standard deviation: ", separator, suffix);
        outBuffer.append("covariance: " + getCovariance().toString() + suffix);
        return outBuffer.toString();
    }

    /**
     * Append a text representation of an array to a buffer.
     *
     * @param buffer    buffer to fill
     * @param data      data array
     * @param prefix    text prefix
     * @param separator elements separator
     * @param suffix    text suffix
     */
    private void append(StringBuilder buffer, double[] data,
                        String prefix, String separator, String suffix) {
        buffer.append(prefix);
        for (int i = 0; i < data.length; ++i) {
            if (i > 0) {
                buffer.append(separator);
            }
            buffer.append(data[i]);
        }
        buffer.append(suffix);
    }

    /**
     * Returns true iff <code>object</code> is a <code>MultivariateSummaryStatistics</code>
     * instance and all statistics have the same values as this.
     *
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultivariateSummaryStatistics == false) {
            return false;
        }
        MultivariateSummaryStatistics other = (MultivariateSummaryStatistics) object;
        return other.getN() == getN() &&
                MathArrays.equalsIncludingNaN(other.getGeometricMean(), getGeometricMean()) &&
                MathArrays.equalsIncludingNaN(other.getMax(), getMax()) &&
                MathArrays.equalsIncludingNaN(other.getMean(), getMean()) &&
                MathArrays.equalsIncludingNaN(other.getMin(), getMin()) &&
                MathArrays.equalsIncludingNaN(other.getSum(), getSum()) &&
                MathArrays.equalsIncludingNaN(other.getSumSq(), getSumSq()) &&
                MathArrays.equalsIncludingNaN(other.getSumLog(), getSumLog()) &&
                other.getCovariance().equals(getCovariance());
    }

    /**
     * Returns hash code based on values of statistics
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 31 + MathUtils.hash(getN());
        result = result * 31 + MathUtils.hash(getGeometricMean());
        result = result * 31 + MathUtils.hash(getMax());
        result = result * 31 + MathUtils.hash(getMean());
        result = result * 31 + MathUtils.hash(getMin());
        result = result * 31 + MathUtils.hash(getSum());
        result = result * 31 + MathUtils.hash(getSumSq());
        result = result * 31 + MathUtils.hash(getSumLog());
        result = result * 31 + getCovariance().hashCode();
        return result;
    }

}
