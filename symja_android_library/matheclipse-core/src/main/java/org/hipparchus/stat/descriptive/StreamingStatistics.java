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

import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.moment.GeometricMean;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.SecondMoment;
import org.hipparchus.stat.descriptive.moment.Variance;
import org.hipparchus.stat.descriptive.rank.Max;
import org.hipparchus.stat.descriptive.rank.Min;
import org.hipparchus.stat.descriptive.rank.RandomPercentile;
import org.hipparchus.stat.descriptive.summary.Sum;
import org.hipparchus.stat.descriptive.summary.SumOfLogs;
import org.hipparchus.stat.descriptive.summary.SumOfSquares;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.hipparchus.util.Precision;

import java.io.Serializable;
import com.duy.lambda.DoubleConsumer;

/**
 * Computes summary statistics for a stream of data values added using the
 * {@link #addValue(double) addValue} method. The data values are not stored in
 * memory, so this class can be used to compute statistics for very large data
 * streams.
 * <p>
 * By default, all statistics other than percentiles are maintained.  Percentile
 * calculations use an embedded {@link RandomPercentile} which carries more memory
 * and compute overhead than the other statistics, so it is disabled by default.
 * To enable percentiles, either pass {@code true} to the constructor or use a
 * {@link StreamingStatisticsBuilder} to configure an instance with percentiles turned
 * on. Other stats can also be selectively disabled using
 * {@code StreamingStatisticsBulder}.
 * <p>
 * Note: This class is not thread-safe.
 */
public class StreamingStatistics
        implements StatisticalSummary, AggregatableStatistic<StreamingStatistics>,
        DoubleConsumer, Serializable {

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = 20160422L;
    /**
     * SecondMoment is used to compute the mean and variance
     */
    private final SecondMoment secondMoment;
    /**
     * min of values that have been added
     */
    private final Min minImpl;
    /**
     * max of values that have been added
     */
    private final Max maxImpl;
    /**
     * sum of values that have been added
     */
    private final Sum sumImpl;
    /**
     * sum of the square of each value that has been added
     */
    private final SumOfSquares sumOfSquaresImpl;
    /**
     * sumLog of values that have been added
     */
    private final SumOfLogs sumOfLogsImpl;
    /**
     * mean of values that have been added
     */
    private final Mean meanImpl;
    /**
     * variance of values that have been added
     */
    private final Variance varianceImpl;
    /**
     * geoMean of values that have been added
     */
    private final GeometricMean geoMeanImpl;
    /**
     * population variance of values that have been added
     */
    private final Variance populationVariance;
    /**
     * source of percentiles
     */
    private final RandomPercentile randomPercentile;
    /**
     * whether or not moment stats (sum, mean, variance) are maintained
     */
    private final boolean computeMoments;
    /**
     * whether or not sum of squares and quadratic mean are maintained
     */
    private final boolean computeSumOfSquares;
    /**
     * whether or not sum of logs and geometric mean are maintained
     */
    private final boolean computeSumOfLogs;
    /**
     * whether or not percentiles are maintained
     */
    private final boolean computePercentiles;
    /**
     * whether or not min and max are maintained
     */
    private final boolean computeExtrema;
    /**
     * count of values that have been added
     */
    private long n = 0;

    /**
     * Construct a new StreamingStatistics instance, maintaining all statistics
     * other than percentiles.
     */
    public StreamingStatistics() {
        this(false);
    }

    /**
     * Construct a new StreamingStatistics instance, maintaining all statistics
     * other than percentiles and with/without percentiles per the argument.
     *
     * @param computePercentiles whether or not percentiles are maintained
     */
    public StreamingStatistics(boolean computePercentiles) {
        this(computePercentiles, true, true, true, true);
    }

    /**
     * Private constructor used by {@link StreamingStatisticsBuilder}.
     *
     * @param computePercentiles  whether or not percentiles are maintained
     * @param computeMoments      whether or not moment stats (mean, sum, variance) are maintained
     * @param computeSumOfLogs    whether or not sum of logs and geometric mean are maintained
     * @param computeSumOfSquares whether or not sum of squares and quadratic mean are maintained
     * @param computeExtrema      whether or not min and max are maintained
     */
    private StreamingStatistics(boolean computePercentiles, boolean computeMoments,
                                boolean computeSumOfLogs, boolean computeSumOfSquares,
                                boolean computeExtrema) {
        this.computeMoments = computeMoments;
        this.computeSumOfLogs = computeSumOfLogs;
        this.computeSumOfSquares = computeSumOfSquares;
        this.computePercentiles = computePercentiles;
        this.computeExtrema = computeExtrema;

        this.secondMoment = computeMoments ? new SecondMoment() : null;
        this.maxImpl = computeExtrema ? new Max() : null;
        this.minImpl = computeExtrema ? new Min() : null;
        this.sumImpl = computeMoments ? new Sum() : null;
        this.sumOfSquaresImpl = computeSumOfSquares ? new SumOfSquares() : null;
        this.sumOfLogsImpl = computeSumOfLogs ? new SumOfLogs() : null;
        this.meanImpl = computeMoments ? new Mean(this.secondMoment) : null;
        this.varianceImpl = computeMoments ? new Variance(this.secondMoment) : null;
        this.geoMeanImpl = computeSumOfLogs ? new GeometricMean(this.sumOfLogsImpl) : null;
        this.populationVariance = computeMoments ? new Variance(false, this.secondMoment) : null;
        this.randomPercentile = computePercentiles ? new RandomPercentile() : null;
    }

    /**
     * A copy constructor. Creates a deep-copy of the {@code original}.
     *
     * @param original the {@code StreamingStatistics} instance to copy
     * @throws NullArgumentException if original is null
     */
    StreamingStatistics(StreamingStatistics original) throws NullArgumentException {
        MathUtils.checkNotNull(original);

        this.n = original.n;
        this.secondMoment = original.computeMoments ? original.secondMoment.copy() : null;
        this.maxImpl = original.computeExtrema ? original.maxImpl.copy() : null;
        this.minImpl = original.computeExtrema ? original.minImpl.copy() : null;
        this.sumImpl = original.computeMoments ? original.sumImpl.copy() : null;
        this.sumOfLogsImpl = original.computeSumOfLogs ? original.sumOfLogsImpl.copy() : null;
        this.sumOfSquaresImpl = original.computeSumOfSquares ? original.sumOfSquaresImpl.copy() : null;

        // Keep statistics with embedded moments in synch
        this.meanImpl = original.computeMoments ? new Mean(this.secondMoment) : null;
        this.varianceImpl = original.computeMoments ? new Variance(this.secondMoment) : null;
        this.geoMeanImpl = original.computeSumOfLogs ? new GeometricMean(this.sumOfLogsImpl) : null;
        this.populationVariance = original.computeMoments ? new Variance(false, this.secondMoment) : null;
        this.randomPercentile = original.computePercentiles ? original.randomPercentile.copy() : null;

        this.computeMoments = original.computeMoments;
        this.computeSumOfLogs = original.computeSumOfLogs;
        this.computeSumOfSquares = original.computeSumOfSquares;
        this.computePercentiles = original.computePercentiles;
        this.computeExtrema = original.computeExtrema;
    }

    /**
     * Returns a {@link StreamingStatisticsBuilder} to source configured
     * {@code StreamingStatistics} instances.
     *
     * @return a StreamingStatisticsBuilder instance
     */
    public static StreamingStatisticsBuilder builder() {
        return new StreamingStatisticsBuilder();
    }

    /**
     * Returns a copy of this StreamingStatistics instance with the same internal state.
     *
     * @return a copy of this
     */
    public StreamingStatistics copy() {
        return new StreamingStatistics(this);
    }

    /**
     * Return a {@link StatisticalSummaryValues} instance reporting current
     * statistics.
     *
     * @return Current values of statistics
     */
    public StatisticalSummary getSummary() {
        return new StatisticalSummaryValues(getMean(), getVariance(), getN(),
                getMax(), getMin(), getSum());
    }

    /**
     * Add a value to the data
     *
     * @param value the value to add
     */
    public void addValue(double value) {
        if (computeMoments) {
            secondMoment.increment(value);
            sumImpl.increment(value);
        }
        if (computeExtrema) {
            minImpl.increment(value);
            maxImpl.increment(value);
        }
        if (computeSumOfSquares) {
            sumOfSquaresImpl.increment(value);
        }
        if (computeSumOfLogs) {
            sumOfLogsImpl.increment(value);
        }
        if (computePercentiles) {
            randomPercentile.increment(value);
        }
        n++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(double value) {
        addValue(value);
    }

    /**
     * Resets all statistics and storage.
     */
    public void clear() {
        this.n = 0;
        if (computeExtrema) {
            minImpl.clear();
            maxImpl.clear();
        }
        if (computeMoments) {
            sumImpl.clear();
            secondMoment.clear();
        }
        if (computeSumOfLogs) {
            sumOfLogsImpl.clear();
        }
        if (computeSumOfSquares) {
            sumOfSquaresImpl.clear();
        }
        if (computePercentiles) {
            randomPercentile.clear();
        }
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
    public double getMax() {
        return computeExtrema ? maxImpl.getResult() : Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMin() {
        return computeExtrema ? minImpl.getResult() : Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSum() {
        return computeMoments ? sumImpl.getResult() : Double.NaN;
    }

    /**
     * Returns the sum of the squares of the values that have been added.
     * <p>
     * Double.NaN is returned if no values have been added.
     *
     * @return The sum of squares
     */
    public double getSumOfSquares() {
        return computeSumOfSquares ? sumOfSquaresImpl.getResult() : Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMean() {
        return computeMoments ? meanImpl.getResult() : Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getVariance() {
        return computeMoments ? varianceImpl.getResult() : Double.NaN;
    }

    /**
     * Returns the <a href="http://en.wikibooks.org/wiki/Statistics/Summary/Variance">
     * population variance</a> of the values that have been added.
     * <p>
     * Double.NaN is returned if no values have been added.
     *
     * @return the population variance
     */
    public double getPopulationVariance() {
        return computeMoments ? populationVariance.getResult() : Double.NaN;
    }

    /**
     * Returns the geometric mean of the values that have been added.
     * <p>
     * Double.NaN is returned if no values have been added.
     *
     * @return the geometric mean
     */
    public double getGeometricMean() {
        return computeSumOfLogs ? geoMeanImpl.getResult() : Double.NaN;
    }

    /**
     * Returns the sum of the logs of the values that have been added.
     * <p>
     * Double.NaN is returned if no values have been added.
     *
     * @return the sum of logs
     */
    public double getSumOfLogs() {
        return computeSumOfLogs ? sumOfLogsImpl.getResult() : Double.NaN;
    }

    /**
     * Returns a statistic related to the Second Central Moment. Specifically,
     * what is returned is the sum of squared deviations from the sample mean
     * among the values that have been added.
     * <p>
     * Returns <code>Double.NaN</code> if no data values have been added and
     * returns <code>0</code> if there is just one value in the data set.
     *
     * @return second central moment statistic
     */
    public double getSecondMoment() {
        return computeMoments ? secondMoment.getResult() : Double.NaN;
    }

    /**
     * Returns the quadratic mean, a.k.a.
     * <a href="http://mathworld.wolfram.com/Root-Mean-Square.html">
     * root-mean-square</a> of the available values
     *
     * @return The quadratic mean or {@code Double.NaN} if no values
     * have been added.
     */
    public double getQuadraticMean() {
        if (computeSumOfSquares) {
            long size = getN();
            return size > 0 ? FastMath.sqrt(getSumOfSquares() / size) : Double.NaN;
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns the standard deviation of the values that have been added.
     * <p>
     * Double.NaN is returned if no values have been added.
     *
     * @return the standard deviation
     */
    @Override
    public double getStandardDeviation() {
        long size = getN();
        if (computeMoments) {
            if (size > 0) {
                return size > 1 ? FastMath.sqrt(getVariance()) : 0.0;
            } else {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns an estimate of the median of the values that have been entered.
     * See {@link RandomPercentile} for a description of the algorithm used for large
     * data streams.
     *
     * @return the median
     */
    public double getMedian() {
        return randomPercentile != null ? randomPercentile.getResult(50d) : Double.NaN;
    }

    /**
     * Returns an estimate of the given percentile of the values that have been entered.
     * See {@link RandomPercentile} for a description of the algorithm used for large
     * data streams.
     *
     * @param percentile the desired percentile (must be between 0 and 100)
     * @return estimated percentile
     */
    public double getPercentile(double percentile) {
        return randomPercentile != null ? randomPercentile.getResult(percentile) : Double.NaN;
    }

    /**
     * {@inheritDoc}
     * Statistics are aggregated only when both this and other are maintaining them.  For example,
     * if this.computeMoments is false, but other.computeMoments is true, the moment data in other
     * will be lost.
     */
    @Override
    public void aggregate(StreamingStatistics other) {
        MathUtils.checkNotNull(other);

        if (other.n > 0) {
            this.n += other.n;
            if (computeMoments && other.computeMoments) {
                this.secondMoment.aggregate(other.secondMoment);
                this.sumImpl.aggregate(other.sumImpl);
            }
            if (computeExtrema && other.computeExtrema) {
                this.minImpl.aggregate(other.minImpl);
                this.maxImpl.aggregate(other.maxImpl);
            }
            if (computeSumOfLogs && other.computeSumOfLogs) {
                this.sumOfLogsImpl.aggregate(other.sumOfLogsImpl);
            }
            if (computeSumOfSquares && other.computeSumOfSquares) {
                this.sumOfSquaresImpl.aggregate(other.sumOfSquaresImpl);
            }
            if (computePercentiles && other.computePercentiles) {
                this.randomPercentile.aggregate(other.randomPercentile);
            }
        }
    }

    /**
     * Generates a text report displaying summary statistics from values that
     * have been added.
     *
     * @return String with line feeds displaying statistics
     */
    @Override
    public String toString() {
        StringBuilder outBuffer = new StringBuilder();
        String endl = "\n";
        outBuffer.append("StreamingStatistics:").append(endl);
        outBuffer.append("n: ").append(getN()).append(endl);
        outBuffer.append("min: ").append(getMin()).append(endl);
        outBuffer.append("max: ").append(getMax()).append(endl);
        outBuffer.append("sum: ").append(getSum()).append(endl);
        outBuffer.append("mean: ").append(getMean()).append(endl);
        outBuffer.append("variance: ").append(getVariance()).append(endl);
        outBuffer.append("population variance: ").append(getPopulationVariance()).append(endl);
        outBuffer.append("standard deviation: ").append(getStandardDeviation()).append(endl);
        outBuffer.append("geometric mean: ").append(getGeometricMean()).append(endl);
        outBuffer.append("second moment: ").append(getSecondMoment()).append(endl);
        outBuffer.append("sum of squares: ").append(getSumOfSquares()).append(endl);
        outBuffer.append("sum of logs: ").append(getSumOfLogs()).append(endl);
        return outBuffer.toString();
    }

    /**
     * Returns true iff <code>object</code> is a <code>StreamingStatistics</code>
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
        if (object instanceof StreamingStatistics == false) {
            return false;
        }
        StreamingStatistics other = (StreamingStatistics) object;
        return other.getN() == getN() &&
                Precision.equalsIncludingNaN(other.getMax(), getMax()) &&
                Precision.equalsIncludingNaN(other.getMin(), getMin()) &&
                Precision.equalsIncludingNaN(other.getSum(), getSum()) &&
                Precision.equalsIncludingNaN(other.getGeometricMean(), getGeometricMean()) &&
                Precision.equalsIncludingNaN(other.getMean(), getMean()) &&
                Precision.equalsIncludingNaN(other.getSumOfSquares(), getSumOfSquares()) &&
                Precision.equalsIncludingNaN(other.getSumOfLogs(), getSumOfLogs()) &&
                Precision.equalsIncludingNaN(other.getVariance(), getVariance()) &&
                Precision.equalsIncludingNaN(other.getMedian(), getMedian());
    }

    /**
     * Returns hash code based on values of statistics.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 31 + MathUtils.hash(getN());
        result = result * 31 + MathUtils.hash(getMax());
        result = result * 31 + MathUtils.hash(getMin());
        result = result * 31 + MathUtils.hash(getSum());
        result = result * 31 + MathUtils.hash(getGeometricMean());
        result = result * 31 + MathUtils.hash(getMean());
        result = result * 31 + MathUtils.hash(getSumOfSquares());
        result = result * 31 + MathUtils.hash(getSumOfLogs());
        result = result * 31 + MathUtils.hash(getVariance());
        result = result * 31 + MathUtils.hash(getMedian());
        return result;
    }

    /**
     * Builder for StreamingStatistics instances.
     */
    public static class StreamingStatisticsBuilder {
        /**
         * whether or not moment statistics are maintained by instances created by this factory
         */
        private boolean computeMoments;
        /**
         * whether or not sum of squares and quadratic mean are maintained by instances created by this factory
         */
        private boolean computeSumOfSquares;
        /**
         * whether or not sum of logs and geometric mean are maintained by instances created by this factory
         */
        private boolean computeSumOfLogs;
        /**
         * whether or not percentiles are maintained by instances created by this factory
         */
        private boolean computePercentiles;
        /**
         * whether or not min and max are maintained by instances created by this factory
         */
        private boolean computeExtrema;

        /**
         * Simple constructor.
         */
        public StreamingStatisticsBuilder() {
            computeMoments = true;
            computeSumOfSquares = true;
            computeSumOfLogs = true;
            computePercentiles = false;
            computeExtrema = true;
        }

        /**
         * Sets the computeMoments setting of the factory
         *
         * @param arg whether or not instances created using {@link #build()} will
         *            maintain moment statistics
         * @return a factory with the given computeMoments property set
         */
        public StreamingStatisticsBuilder moments(boolean arg) {
            this.computeMoments = arg;
            return this;
        }

        /**
         * Sets the computeSumOfLogs setting of the factory
         *
         * @param arg whether or not instances created using {@link #build()} will
         *            maintain log sums
         * @return a factory with the given computeSumOfLogs property set
         */
        public StreamingStatisticsBuilder sumOfLogs(boolean arg) {
            this.computeSumOfLogs = arg;
            return this;
        }

        /**
         * Sets the computeSumOfSquares setting of the factory.
         *
         * @param arg whether or not instances created using {@link #build()} will
         *            maintain sums of squares
         * @return a factory with the given computeSumOfSquares property set
         */
        public StreamingStatisticsBuilder sumOfSquares(boolean arg) {
            this.computeSumOfSquares = arg;
            return this;
        }

        /**
         * Sets the computePercentiles setting of the factory.
         *
         * @param arg whether or not instances created using {@link #build()} will
         *            compute percentiles
         * @return a factory with the given computePercentiles property set
         */
        public StreamingStatisticsBuilder percentiles(boolean arg) {
            this.computePercentiles = arg;
            return this;
        }

        /**
         * Sets the computeExtrema setting of the factory.
         *
         * @param arg whether or not instances created using {@link #build()} will
         *            compute min and max
         * @return a factory with the given computeExtrema property set
         */
        public StreamingStatisticsBuilder extrema(boolean arg) {
            this.computeExtrema = arg;
            return this;
        }

        /**
         * Builds a StreamingStatistics instance with currently defined properties.
         *
         * @return newly configured StreamingStatistics instance
         */
        public StreamingStatistics build() {
            return new StreamingStatistics(computePercentiles, computeMoments,
                    computeSumOfLogs, computeSumOfSquares,
                    computeExtrema);
        }
    }
}
