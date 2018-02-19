/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
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
package org.hipparchus.stat.descriptive.rank;


import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.random.RandomGenerator;
import org.hipparchus.random.Well19937c;
import org.hipparchus.stat.StatUtils;
import org.hipparchus.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.hipparchus.stat.descriptive.AggregatableStatistic;
import org.hipparchus.stat.descriptive.StorelessUnivariateStatistic;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * A {@link StorelessUnivariateStatistic} estimating percentiles using the
 * <a href=http://dimacs.rutgers.edu/~graham/pubs/papers/nquantiles.pdf>RANDOM</a>
 * Algorithm.
 * <p>
 * Storage requirements for the RANDOM algorithm depend on the desired
 * accuracy of quantile estimates. Quantile estimate accuracy is defined as follows.
 * <p>
 * Let \(X\) be the set of all data values consumed from the stream and let \(q\)
 * be a quantile (measured between 0 and 1) to be estimated. If <ul>
 * <li>\(\epsilon\) is the configured accuracy</li>
 * <li> \(\hat{q}\) is a RandomPercentile estimate for \(q\) (what is returned
 * by {@link #getResult()} or {@link #getResult(double)}) with \(100q\) as
 * actual parameter)</li>
 * <li> \(rank(\hat{q}) = |\{x \in X : x < \hat{q}\}|\) is the actual rank of
 * \(\hat{q}\) in the full data stream</li>
 * <li>\(n = |X|\) is the number of observations</li></ul>
 * then we can expect \((q - \epsilon)n < rank(\hat{q}) < (q + \epsilon)n\).
 * <p>
 * The algorithm maintains \(\left\lceil {log_{2}(1/\epsilon)}\right\rceil + 1\) buffers
 * of size \(\left\lceil {1/\epsilon \sqrt{log_2(1/\epsilon)}}\right\rceil\).  When
 * {@code epsilon} is set to the default value of \(10^{-4}\), this makes 15 buffers
 * of size 36,453.
 * <p>
 * The algorithm uses the buffers to maintain samples of data from the stream.  Until
 * all buffers are full, the entire sample is stored in the buffers.
 * If one of the {@code getResult} methods is called when all data are available in memory
 * and there is room to make a copy of the data (meaning the combined set of buffers is
 * less than half full), the {@code getResult} method delegates to a {@link Percentile}
 * instance to compute and return the exact value for the desired quantile.
 * For default {@code epsilon}, this means exact values will be returned whenever fewer than
 * \(\left\lceil {15 \times 36453 / 2} \right\rceil = 273,398\) values have been consumed
 * from the data stream.
 * <p>
 * When buffers become full, the algorithm merges buffers so that they effectively represent
 * a larger set of values than they can hold. Subsequently, data values are sampled from the
 * stream to fill buffers freed by merge operations.  Both the merging and the sampling
 * require random selection, which is done using a {@code RandomGenerator}.  To get
 * repeatable results for large data streams, users should provide {@code RandomGenerator}
 * instances with fixed seeds. {@code RandomPercentile} itself does not reseed or otherwise
 * initialize the {@code RandomGenerator} provided to it.  By default, it uses a
 * {@link Well19937c} generator with the default seed.
 * <p>
 * Note: This implementation is not thread-safe.
 */
public class RandomPercentile
        extends AbstractStorelessUnivariateStatistic implements StorelessUnivariateStatistic,
        AggregatableStatistic<RandomPercentile>, Serializable {

    /**
     * Default quantile estimation error setting
     */
    public static final double DEFAULT_EPSILON = 1e-4;
    /**
     * Serialization version id
     */
    private static final long serialVersionUID = 1L;
    /**
     * Storage size of each buffer
     */
    private final int s;
    /**
     * Maximum number of buffers minus 1
     */
    private final int h;
    /**
     * Data structure used to manage buffers
     */
    private final BufferMap bufferMap;
    /**
     * Bound on the quantile estimation error
     */
    private final double epsilon;
    /**
     * Source of random data
     */
    private final RandomGenerator randomGenerator;
    /**
     * Number of elements consumed from the input data stream
     */
    private long n = 0;
    /**
     * Buffer currently being filled
     */
    private Buffer currentBuffer = null;

    /**
     * Constructs a {@code RandomPercentile} with quantile estimation error
     * {@code epsilon} using {@code randomGenerator} as its source of random data.
     *
     * @param epsilon         bound on quantile estimation error (see class javadoc)
     * @param randomGenerator PRNG used in sampling and merge operations
     * @throws MathIllegalArgumentException if percentile is not in the range [0, 100]
     */
    public RandomPercentile(double epsilon, RandomGenerator randomGenerator) {
        if (epsilon <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL,
                    epsilon, 0);
        }
        this.h = (int) FastMath.ceil(log2(1 / epsilon));
        this.s = (int) FastMath.ceil(FastMath.sqrt(log2(1 / epsilon)) / epsilon);
        this.randomGenerator = randomGenerator;
        bufferMap = new BufferMap(h + 1, s, randomGenerator);
        currentBuffer = bufferMap.create(0);
        this.epsilon = epsilon;
    }

    /**
     * Constructs a {@code RandomPercentile} with default estimation error
     * using {@code randomGenerator} as its source of random data.
     *
     * @param randomGenerator PRNG used in sampling and merge operations
     * @throws MathIllegalArgumentException if percentile is not in the range [0, 100]
     */
    public RandomPercentile(RandomGenerator randomGenerator) {
        this(DEFAULT_EPSILON, randomGenerator);
    }

    /**
     * Constructs a {@code RandomPercentile} with quantile estimation error
     * {@code epsilon} using the default PRNG as source of random data.
     *
     * @param epsilon bound on quantile estimation error (see class javadoc)
     * @throws MathIllegalArgumentException if percentile is not in the range [0, 100]
     */
    public RandomPercentile(double epsilon) {
        this(epsilon, new Well19937c());
    }

    /**
     * Constructs a {@code RandomPercentile} with quantile estimation error
     * set to the default ({@link #DEFAULT_EPSILON}), using the default PRNG
     * as source of random data.
     */
    public RandomPercentile() {
        this(DEFAULT_EPSILON, new Well19937c());
    }

    /**
     * Copy constructor, creates a new {@code RandomPercentile} identical
     * to the {@code original}.  Note: the RandomGenerator used by the new
     * instance is referenced, not copied - i.e., the new instance shares
     * a generator with the original.
     *
     * @param original the {@code PSquarePercentile} instance to copy
     */
    public RandomPercentile(RandomPercentile original) {
        super();
        this.h = original.h;
        this.n = original.n;
        this.s = original.s;
        this.epsilon = original.epsilon;
        this.bufferMap = new BufferMap(original.bufferMap);
        this.randomGenerator = original.randomGenerator;
        Iterator<Buffer> iterator = bufferMap.iterator();
        Buffer current = null;
        Buffer curr = null;
        // See if there is a partially filled buffer - that will be currentBuffer
        while (current == null && iterator.hasNext()) {
            curr = iterator.next();
            if (curr.hasCapacity()) {
                current = curr;
            }
        }
        // If there is no partially filled buffer, just assign the last one.
        // Next increment() will find no capacity and create a new one or trigger
        // a merge.
        this.currentBuffer = current == null ? curr : current;
    }

    /**
     * Computes base 2 log of the argument.
     *
     * @param x input value
     * @return the value y such that 2^y = x
     */
    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    /**
     * Returns the maximum number of {@code double} values that a {@code RandomPercentile}
     * instance created with the given {@code epsilon} value will retain in memory.
     * <p>
     * If the number of values that have been consumed from the stream is less than 1/2
     * of this value, reported statistics are exact.
     *
     * @param epsilon bound on the relative quantile error (see class javadoc)
     * @return upper bound on the total number of primitive double values retained in memory
     * @throws MathIllegalArgumentException if epsilon is not in the interval (0,1)
     */
    public static long maxValuesRetained(double epsilon) {
        if (epsilon >= 1) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED, epsilon, 1);
        }
        if (epsilon <= 0) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED, epsilon, 0);
        }
        final long h = (long) FastMath.ceil(log2(1 / epsilon));
        final long s = (long) FastMath.ceil(FastMath.sqrt(log2(1 / epsilon)) / epsilon);
        return (h + 1) * s;
    }

    @Override
    public long getN() {
        return n;
    }

    /**
     * Returns an estimate of the given percentile, computed using the designated
     * array segment as input data.
     *
     * @param values     source of input data
     * @param begin      position of the first element of the values array to include
     * @param length     number of array elements to include
     * @param percentile desired percentile (scaled 0 - 100)
     * @return estimated percentile
     * @throws MathIllegalArgumentException if percentile is out of the range [0, 100]
     */
    public double evaluate(final double percentile, final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {
        if (MathArrays.verifyValues(values, begin, length)) {
            RandomPercentile randomPercentile = new RandomPercentile(this.epsilon,
                    this.randomGenerator);
            randomPercentile.incrementAll(values, begin, length);
            return randomPercentile.getResult(percentile);
        }
        return Double.NaN;
    }

    /**
     * Returns an estimate of the median, computed using the designated
     * array segment as input data.
     *
     * @param values source of input data
     * @param begin  position of the first element of the values array to include
     * @param length number of array elements to include
     * @return estimated percentile
     * @throws MathIllegalArgumentException if percentile is out of the range [0, 100]
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length) {
        return evaluate(50d, values, begin, length);
    }

    /**
     * Returns an estimate of percentile over the given array.
     *
     * @param values     source of input data
     * @param percentile desired percentile (scaled 0 - 100)
     * @return estimated percentile
     * @throws MathIllegalArgumentException if percentile is out of the range [0, 100]
     */
    public double evaluate(final double percentile, final double[] values) {
        return evaluate(percentile, values, 0, values.length);
    }

    @Override
    public RandomPercentile copy() {
        return new RandomPercentile(this);
    }

    @Override
    public void clear() {
        n = 0;
        bufferMap.clear();
        currentBuffer = bufferMap.create(0);
    }

    /**
     * Returns an estimate of the median.
     */
    @Override
    public double getResult() {
        return getResult(50d);

    }

    /**
     * Returns an estimate of the given percentile.
     *
     * @param percentile desired percentile (scaled 0 - 100)
     * @return estimated percentile
     * @throws MathIllegalArgumentException if percentile is out of the range [0, 100]
     */
    public double getResult(double percentile) {
        if (percentile > 100 || percentile < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.OUT_OF_RANGE,
                    percentile, 0, 100);
        }
        // Convert to internal quantile scale
        final double q = percentile / 100;
        // First get global min and max to bound search.
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double bMin;
        double bMax;
        Iterator<Buffer> bufferIterator = bufferMap.iterator();
        while (bufferIterator.hasNext()) {
            Buffer buffer = bufferIterator.next();
            bMin = buffer.min();
            if (bMin < min) {
                min = bMin;
            }
            bMax = buffer.max();
            if (bMax > max) {
                max = bMax;
            }
        }

        // Handle degenerate cases
        if (Double.compare(q, 0d) == 0 || n == 1) {
            return min;
        }
        if (Double.compare(q, 1) == 0) {
            return max;
        }
        if (n == 0) {
            return Double.NaN;
        }

        // See if we have all data in memory and enough free memory to copy.
        // If so, use Percentile to perform exact computation.
        if (bufferMap.halfEmpty()) {
            return new Percentile(percentile).evaluate(bufferMap.levelZeroData());
        }

        // Compute target rank
        final double targetRank = q * n;

        // Start with initial guess min + quantile * (max - min).
        double estimate = min + q * (max - min);
        double estimateRank = getRank(estimate);
        double lower;
        double upper;
        if (estimateRank == targetRank) {
            return estimate;
        }
        if (estimateRank > targetRank) {
            upper = estimate;
            lower = min;
        } else {
            lower = estimate;
            upper = max;
        }
        final double eps = epsilon / 2;
        final double rankTolerance = eps * n;
        final double minWidth = eps / n;
        double intervalWidth = FastMath.abs(upper - lower);
        while (FastMath.abs(estimateRank - targetRank) > rankTolerance && intervalWidth > minWidth) {
            if (estimateRank > targetRank) {
                upper = estimate;
            } else {
                lower = estimate;
            }
            intervalWidth = upper - lower;
            estimate = lower + intervalWidth / 2;
            estimateRank = getRank(estimate);
        }
        return estimate;
    }

    /**
     * Gets the estimated rank of {@code value}, i.e.  \(|\{x \in X : x < value\}|\)
     * where \(X\) is the set of values that have been consumed from the stream.
     *
     * @param value value whose overall rank is sought
     * @return estimated number of sample values that are strictly less than {@code value}
     */
    public double getRank(double value) {
        double rankSum = 0;
        Iterator<Buffer> bufferIterator = bufferMap.iterator();
        while (bufferIterator.hasNext()) {
            Buffer buffer = bufferIterator.next();
            rankSum += buffer.rankOf(value) * FastMath.pow(2, buffer.level);
        }
        return rankSum;
    }

    /**
     * Returns the estimated quantile position of value in the dataset.
     * Specifically, what is returned is an estimate of \(|\{x \in X : x < value\}| / |X|\)
     * where \(X\) is the set of values that have been consumed from the stream.
     *
     * @param value value whose quantile rank is sought.
     * @return estimated proportion of sample values that are strictly less than {@code value}
     */
    public double getQuantileRank(double value) {
        return getRank(value) / getN();
    }

    @Override
    public void increment(double d) {
        n++;
        if (!currentBuffer.hasCapacity()) { // Need to get a new buffer to fill
            // First see if we have not yet created all the buffers
            if (bufferMap.canCreate()) {
                final int level = (int) Math.ceil(Math.max(0, log2(n / (s * FastMath.pow(2, h - 1)))));
                currentBuffer = bufferMap.create(level);
            } else { // All buffers have been created - need to merge to free one
                currentBuffer = bufferMap.merge();
            }
        }
        currentBuffer.consume(d);
    }

    /**
     * Computes the given percentile by combining the data from the collection
     * of aggregates. The result describes the combined sample of all data added
     * to any of the aggregates.
     *
     * @param percentile desired percentile (scaled 0-100)
     * @param aggregates RandomPercentile instances to combine data from
     * @return estimate of the given percentile using combined data from the aggregates
     * @throws MathIllegalArgumentException if percentile is out of the range [0, 100]
     */
    public double reduce(double percentile, Collection<RandomPercentile> aggregates) {
        if (percentile > 100 || percentile < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.OUT_OF_RANGE,
                    percentile, 0, 100);
        }

        // First see if we can copy all data and just compute exactly.
        // The following could be improved to verify that all have only level 0 buffers
        // and the sum of the data sizes is less than 1/2 total capacity.  Here we
        // just check that each of the aggregates is less than half full.
        Iterator<RandomPercentile> iterator = aggregates.iterator();
        boolean small = true;
        while (small && iterator.hasNext()) {
            small = iterator.next().bufferMap.halfEmpty();
        }
        if (small) {
            iterator = aggregates.iterator();
            double[] combined = {};
            while (iterator.hasNext()) {
                combined = MathArrays.concatenate(combined, iterator.next().bufferMap.levelZeroData());
            }
            final Percentile exactP = new Percentile(percentile);
            return exactP.evaluate(combined);
        }

        // Below largely duplicates code in getResult(percentile).
        // Common binary search code with function parameter should be factored out.

        // Get global max and min to bound binary search and total N
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double combinedN = 0;
        iterator = aggregates.iterator();
        while (iterator.hasNext()) {
            final RandomPercentile curr = iterator.next();
            final double curMin = curr.getResult(0);
            final double curMax = curr.getResult(100);
            if (curMin < min) {
                min = curMin;
            }
            if (curMax > max) {
                max = curMax;
            }
            combinedN += curr.getN();
        }

        final double q = percentile / 100;
        // Handle degenerate cases
        if (Double.compare(q, 0d) == 0) {
            return min;
        }
        if (Double.compare(q, 1) == 0) {
            return max;
        }

        // Compute target rank
        final double targetRank = q * combinedN;

        // Perform binary search using aggregated rank computation
        // Start with initial guess min + quantile * (max - min).
        double estimate = min + q * (max - min);
        double estimateRank = getAggregateRank(estimate, aggregates);
        double lower;
        double upper;
        if (estimateRank == targetRank) {
            return estimate;
        }
        if (estimateRank > targetRank) {
            upper = estimate;
            lower = min;
        } else {
            lower = estimate;
            upper = max;
        }
        final double eps = epsilon / 2;
        double intervalWidth = FastMath.abs(upper - lower);
        while (FastMath.abs(estimateRank / combinedN - q) > eps && intervalWidth > eps / combinedN) {
            if (estimateRank == targetRank) {
                return estimate;
            }
            if (estimateRank > targetRank) {
                upper = estimate;
            } else {
                lower = estimate;
            }
            intervalWidth = FastMath.abs(upper - lower);
            estimate = lower + intervalWidth / 2;
            estimateRank = getAggregateRank(estimate, aggregates);
        }
        return estimate;
    }

    /**
     * Computes the estimated rank of value in the combined dataset of the aggregates.
     * Sums the values from {@link #getRank(double)}.
     *
     * @param value      value whose rank is sought
     * @param aggregates collection to aggregate rank over
     * @return estimated number of elements in the combined dataset that are less than value
     */
    public double getAggregateRank(double value, Collection<RandomPercentile> aggregates) {
        double result = 0;
        final Iterator<RandomPercentile> iterator = aggregates.iterator();
        while (iterator.hasNext()) {
            result += iterator.next().getRank(value);
        }
        return result;
    }

    /**
     * Returns the estimated quantile position of value in the combined dataset of the aggregates.
     * Specifically, what is returned is an estimate of \(|\{x \in X : x < value\}| / |X|\)
     * where \(X\) is the set of values that have been consumed from all of the datastreams
     * feeding the aggregates.
     *
     * @param value      value whose quantile rank is sought.
     * @param aggregates collection of RandomPercentile instances being combined
     * @return estimated proportion of combined sample values that are strictly less than {@code value}
     */
    public double getAggregateQuantileRank(double value, Collection<RandomPercentile> aggregates) {
        return getAggregateRank(value, aggregates) / getAggregateN(aggregates);
    }

    /**
     * Returns the total number of values that have been consumed by the aggregates.
     *
     * @param aggregates collection of RandomPercentile instances whose combined sample size is sought
     * @return total number of values that have been consumbed by the aggregates
     */
    public double getAggregateN(Collection<RandomPercentile> aggregates) {
        double result = 0;
        final Iterator<RandomPercentile> iterator = aggregates.iterator();
        while (iterator.hasNext()) {
            result += iterator.next().getN();
        }
        return result;
    }

    /**
     * Aggregates the provided instance into this instance.
     * <p>
     * Other must have the same buffer size as this. If the combined data size
     * exceeds the maximum storage configured for this instance, buffers are
     * merged to create capacity. If all that is needed is computation of
     * aggregate results, {@link #reduce(double, Collection)} is faster,
     * may be more accurate and does not require the buffer sizes to be the same.
     *
     * @param other the instance to aggregate into this instance
     * @throws NullArgumentException    if the input is null
     * @throws IllegalArgumentException if other has different buffer size than this
     */
    @Override
    public void aggregate(RandomPercentile other)
            throws NullArgumentException {
        if (other == null) {
            throw new NullArgumentException();
        }
        if (other.s != s) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INTERNAL_ERROR);
        }
        bufferMap.absorb(other.bufferMap);
        n += other.n;
    }

    /**
     * Maintains a buffer of values sampled from the input data stream.
     * <p>
     * The {@link #level} of a buffer determines its sampling frequency.
     * The {@link #consume(double)} method retains 1 out of every 2^level values
     * read in from the stream.
     * <p>
     * The {@link #size} of the buffer is the number of values that it can store
     * The buffer is considered full when it has consumed 2^level * size values.
     * <p>
     * The {@link #blockSize} of a buffer is 2^level.
     * The consume method starts each block by generating a random integer in
     * [0, blockSize - 1].  It then skips over all but the element with that offset
     * in the block, retaining only the selected value. So after 2^level * size
     * elements have been consumed, it will have retained size elements - one
     * from each 2^level block.
     * <p>
     * The {@link #mergeWith(Buffer)} method merges this buffer with another one,
     * The merge operation merges the data from the other buffer into this and clears
     * the other buffer (so it can consume data). Both buffers have their level
     * incremented by the merge. This operation is only used on full buffers.
     */
    private static class Buffer implements Serializable {
        /**
         * Serialization version id
         */
        private static final long serialVersionUID = 1L;
        /**
         * Number of values actually stored in the buffer
         */
        private final int size;
        /**
         * Data sampled from the stream
         */
        private final double[] data;
        /**
         * PRNG used for merges and stream sampling
         */
        private final RandomGenerator randomGenerator;
        /**
         * ID
         */
        private final UUID id;
        /**
         * Level of the buffer
         */
        private int level = 0;
        /**
         * Block size  = 2^level
         */
        private long blockSize;
        /**
         * Next location in backing array for stored (taken) value
         */
        private int next = 0;
        /**
         * Number of values consumed in current 2^level block of values from the stream
         */
        private long consumed = 0;
        /**
         * Index of next value to take in current 2^level block
         */
        private long nextToTake = 0;

        /**
         * Creates a new buffer capable of retaining size values with the given level.
         *
         * @param size            number of values the buffer can retain
         * @param level           the base 2 log of the sampling frequency
         *                        (one out of every 2^level values is retained)
         * @param randomGenerator PRNG used for sampling and merge operations
         */
        Buffer(int size, int level, RandomGenerator randomGenerator) {
            this.size = size;
            data = new double[size];
            this.level = level;
            this.randomGenerator = randomGenerator;
            this.id = UUID.randomUUID();
            computeBlockSize();
        }

        /**
         * Sets blockSize and nextToTake based on level.
         */
        private void computeBlockSize() {
            if (level == 0) {
                blockSize = 1;
            } else {
                long product = 1;
                for (int i = 0; i < level; i++) {
                    product *= 2;
                }
                blockSize = product;
            }
            if (blockSize > 1) {
                nextToTake = randomGenerator.nextLong(blockSize);
            }
        }

        /**
         * Consumes a value from the input stream.
         * <p>
         * For each 2^level values consumed, one is added to the buffer.
         * The buffer is not considered full until 2^level * size values
         * have been consumed.
         * <p>
         * Sorts the data array if the consumption renders the buffer full.
         * <p>
         * There is no capacity check in this method.  Clients are expected
         * to use {@link #hasCapacity()} before invoking this method.  If
         * it is invoked on a full buffer, an ArrayIndexOutOfBounds exception
         * will result.
         *
         * @param value value to consume from the stream
         */
        public void consume(double value) {
            if (consumed == nextToTake) {
                data[next] = value;
                next++;
            }
            consumed++;
            if (consumed == blockSize) {
                if (next == size) {   // Buffer is full
                    Arrays.sort(data);
                } else {              // Reset in-block counter and nextToTake
                    consumed = 0;
                    if (blockSize > 1) {
                        nextToTake = randomGenerator.nextLong(blockSize);
                    }
                }
            }
        }

        /**
         * Merges this with other.
         * <p>
         * After the merge, this will be the merged buffer and other will be free.
         * Both will have level+1.
         * Post-merge, other can be used to accept new data.
         * <p>
         * The contents of the merged buffer (this after the merge) are determined
         * by randomly choosing one of the two retained elements in each of the
         * [0...size - 1] positions in the two buffers.
         * <p>
         * This and other must have the same level and both must be full.
         *
         * @param other initially full other buffer at the same level as this.
         * @throws MathIllegalArgumentException if either buffer is not full or they
         *                                      have different levels
         */
        public void mergeWith(Buffer other) {
            // Make sure both this and other are full and have the same level
            if (this.hasCapacity() || other.hasCapacity() || other.level != this.level) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.INTERNAL_ERROR);
            }
            // Randomly select one of the two entries for each slot
            for (int i = 0; i < size; i++) {
                if (randomGenerator.nextBoolean()) {
                    data[i] = other.data[i];
                }
            }
            // Re-sort data
            Arrays.sort(data);
            // Bump level of both buffers
            other.setLevel(level + 1);
            this.setLevel(level + 1);
            // Clear the free one (and compute new blocksize)
            other.clear();
        }

        /**
         * Merge this into a higher-level buffer.
         * <p>
         * Does not alter this; but after the merge, higher may have some of its
         * data replaced by data from this.  Levels are not changed for either buffer.
         * <p>
         * Probability of selection into the newly constituted higher buffer is weighted
         * according to level. So for example, if this has level 0 and higher has level
         * 2, the ith element of higher is 4 times more likely than the corresponding
         * element of this to retain its spot.
         * <p>
         * This method is only used when aggregating RandomPercentile instances.
         * <p>
         * Preconditions:
         * <ol><li> this.level < higher.level </li>
         * <li> this.size = higher.size </li>
         * <li> Both buffers are full </li>
         * </ol>
         *
         * @param higher higher-level buffer to merge this into
         * @throws MathIllegalArgumentException if the buffers have different sizes,
         *                                      either buffer is not full or this has level greater than or equal to higher
         */
        public void mergeInto(Buffer higher) {
            // Check preconditions
            if (this.size != higher.size || this.hasCapacity() || higher.hasCapacity() ||
                    this.level >= higher.level) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.INTERNAL_ERROR);
            }
            final int levelDifference = higher.level - this.level;
            int m = 1;
            for (int i = 0; i < levelDifference; i++) {
                m *= 2;
            }
            // Randomly select one of the two entries for each slot in higher, giving
            // m-times higher weight to the entries of higher.
            for (int i = 0; i < size; i++) {
                // data[i] <-> {0}, higher.data[i] <-> {1, ..., m}
                if (randomGenerator.nextInt(m + 1) == 0) {
                    higher.data[i] = data[i];
                }
            }
            // Resort higher's data
            Arrays.sort(higher.data);
        }

        /**
         * @return true if the buffer has capacity; false if it is full
         */
        public boolean hasCapacity() {
            // Buffer has capacity if it has not yet set all of its data
            // values or if it has but still has not finished its last block
            return next < size || consumed < blockSize;
        }

        /**
         * Clears data, recomputes blockSize and resets consumed and nextToTake.
         */
        public void clear() {
            consumed = 0;
            next = 0;
            computeBlockSize();
        }

        /**
         * Returns a copy of the data that has been added to the buffer
         *
         * @return possibly unsorted copy of the portion of the buffer that has been filled
         */
        public double[] getData() {
            final double[] out = new double[next];
            System.arraycopy(data, 0, out, 0, next);
            return out;
        }

        /**
         * Returns the ordinal rank of value among the sampled values in this buffer.
         *
         * @param value value whose rank is sought
         * @return |{v in data : v < value}|
         */
        public int rankOf(double value) {
            int ret = 0;
            if (!hasCapacity()) { // Full sorted buffer, can do binary search
                ret = Arrays.binarySearch(data, value);
                if (ret < 0) {
                    return -ret - 1;
                } else {
                    return ret;
                }
            } else { // have to count - not sorted yet and can't sort yet
                for (int i = 0; i < next; i++) {
                    if (data[i] < value) {
                        ret++;
                    }
                }
                return ret;
            }
        }

        /**
         * @return the smallest value held in this buffer
         */
        public double min() {
            if (!hasCapacity()) {
                return data[0];
            } else {
                return StatUtils.min(getData());
            }
        }

        /**
         * @return the largest value held in this buffer
         */
        public double max() {
            if (!hasCapacity()) {
                return data[data.length - 1];
            } else {
                return StatUtils.max(getData());
            }
        }

        /**
         * @return the level of this buffer
         */
        public int getLevel() {
            return level;
        }

        /**
         * Sets the level of the buffer.
         *
         * @param level new level value
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * @return the id
         */
        public UUID getId() {
            return id;
        }
    }

    /**
     * A map structure to hold the buffers.
     * Keys are levels and values are lists of buffers at the given level.
     * Overall capacity is limited by the total number of buffers.
     */
    private static class BufferMap implements Iterable<Buffer>, Serializable {
        /**
         * Serialization version ID
         */
        private static final long serialVersionUID = 1L;
        /**
         * Total number of buffers that can be created - cap for count
         */
        private final int capacity;
        /**
         * PRNG used in merges
         */
        private final RandomGenerator randomGenerator;
        /**
         * Uniform buffer size
         */
        private final int bufferSize;
        /**
         * Backing store for the buffer map. Keys are levels, values are lists of registered buffers.
         */
        private final HashMap<Integer, List<Buffer>> registry = new HashMap<>();
        /**
         * Total count of all buffers
         */
        private int count = 0;
        /**
         * Maximum buffer level
         */
        private int maxLevel = 0;

        /**
         * Creates a BufferMap that can manage up to capacity buffers.
         * Buffers created by the pool with have size = buffersize.
         *
         * @param capacity        cap on the number of buffers
         * @param bufferSize      size of each buffer
         * @param randomGenerator RandomGenerator to use in merges
         */
        BufferMap(int capacity, int bufferSize, RandomGenerator randomGenerator) {
            this.bufferSize = bufferSize;
            this.capacity = capacity;
            this.randomGenerator = randomGenerator;
        }

        /**
         * Copy constructor.
         *
         * @param original BufferMap to copy
         */
        BufferMap(BufferMap original) {
            super();
            this.bufferSize = original.bufferSize;
            this.capacity = original.capacity;
            this.count = 0;
            this.randomGenerator = original.randomGenerator;
            Iterator<Buffer> iterator = original.iterator();
            Buffer current = null;
            Buffer newCopy = null;
            while (iterator.hasNext()) {
                current = iterator.next();
                // Create and register a new buffer at the same level
                newCopy = create(current.getLevel());
                // Consume the data
                final double[] data = current.getData();
                for (double value : data) {
                    newCopy.consume(value);
                }
            }
        }

        /**
         * Tries to create a buffer with the given level.
         * <p>
         * If there is capacity to create a new buffer (i.e., fewer than
         * count have been created), a new buffer is created with the given
         * level, registered and returned.  If capacity has been reached,
         * null is returned.
         *
         * @param level level of the new buffer
         * @return an empty buffer or null if a buffer can't be provided
         */
        public Buffer create(int level) {
            if (!canCreate()) {
                return null;
            }
            count++;
            Buffer buffer = new Buffer(bufferSize, level, randomGenerator);
            List<Buffer> bufferList = registry.get(level);
            if (bufferList == null) {
                bufferList = new ArrayList<Buffer>();
                registry.put(level, bufferList);
            }
            bufferList.add(buffer);
            if (level > maxLevel) {
                maxLevel = level;
            }
            return buffer;
        }

        /**
         * Returns true if there is capacity to create a new buffer.
         *
         * @return true if fewer than capacity buffers have been created.
         */
        public boolean canCreate() {
            return count < capacity;
        }

        /**
         * Returns true if we have used less than half of the allocated storage.
         * <p>
         * Includes a check to make sure all buffers have level 0;
         * but this should always be the case.
         * <p>
         * When this method returns true, we have all consumed data in storage
         * and enough space to make a copy of the combined dataset.
         *
         * @return true if all buffers have level 0 and less than half of the
         * available storage has been used
         */
        public boolean halfEmpty() {
            return count * 2 < capacity &&
                    registry.size() == 1 &&
                    registry.containsKey(0);
        }

        /**
         * Returns a fresh copy of all data from level 0 buffers.
         *
         * @return combined data stored in all level 0 buffers
         */
        public double[] levelZeroData() {
            List<Buffer> levelZeroBuffers = registry.get(0);
            // First determine the combined size of the data
            int length = 0;
            for (Buffer buffer : levelZeroBuffers) {
                if (!buffer.hasCapacity()) { // full buffer
                    length += buffer.size;
                } else {
                    length += buffer.next;  // filled amount
                }
            }
            // Copy the data
            int pos = 0;
            int currLen;
            final double[] out = new double[length];
            for (Buffer buffer : levelZeroBuffers) {
                if (!buffer.hasCapacity()) {
                    currLen = buffer.size;
                } else {
                    currLen = buffer.next;
                }
                System.arraycopy(buffer.data, 0, out, pos, currLen);
                pos += currLen;
            }
            return out;
        }

        /**
         * Finds the lowest level l where there exist at least two buffers,
         * merges them to create a new buffer with level l+1 and returns
         * a free buffer with level l+1.
         *
         * @return free buffer that can accept data
         */
        public Buffer merge() {
            int l = 0;
            List<Buffer> mergeCandidates = null;
            // Find the lowest level containing at least two buffers
            while (mergeCandidates == null && l <= maxLevel) {
                final List<Buffer> bufferList = registry.get(l);
                if (bufferList != null && bufferList.size() > 1) {
                    mergeCandidates = bufferList;
                } else {
                    l++;
                }
            }
            if (mergeCandidates == null) {
                // Should never happen
                throw new MathIllegalStateException(LocalizedCoreFormats.INTERNAL_ERROR);
            }
            Buffer buffer1 = mergeCandidates.get(0);
            Buffer buffer2 = mergeCandidates.get(1);
            // Remove buffers to be merged
            mergeCandidates.remove(0);
            mergeCandidates.remove(0);
            // If these are the last level-l buffers, remove the empty list
            if (registry.get(l).size() == 0) {
                registry.remove(l);
            }
            // Merge the buffers
            buffer1.mergeWith(buffer2);
            // Now both buffers have level l+1; buffer1 is full and buffer2 is free.
            // Register both buffers
            register(buffer1);
            register(buffer2);

            // Return the free one
            return buffer2;
        }

        /**
         * Clears the buffer map.
         */
        public void clear() {
            for (List<Buffer> bufferList : registry.values()) {
                bufferList.clear();
            }
            registry.clear();
            count = 0;
        }

        /**
         * Registers a buffer.
         *
         * @param buffer Buffer to be registered.
         */
        public void register(Buffer buffer) {
            final int level = buffer.getLevel();
            List<Buffer> list = registry.get(level);
            if (list == null) {
                list = new ArrayList<Buffer>();
                registry.put(level, list);
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
            list.add(buffer);
        }

        /**
         * De-register a buffer.
         *
         * @param buffer Buffer to be de-registered
         * @throws IllegalStateException if the buffer is not registered
         */
        public void deRegister(Buffer buffer) {
            List<Buffer> bufferList = registry.get(buffer.getLevel());
            final UUID targetId = buffer.getId();
            int i = 0;
            boolean found = false;
            while (i < bufferList.size() && !found) {
                if (bufferList.get(i).getId().equals(targetId)) {
                    bufferList.remove(i);
                    found = true;
                    buffer.clear();
                }
            }
            if (!found) {
                throw new MathIllegalStateException(LocalizedCoreFormats.INTERNAL_ERROR);
            }
        }

        /**
         * Returns an iterator over all of the buffers. Iteration goes by level, with
         * level 0 first.  Assumes there are no empty buffer lists.
         */
        @Override
        public Iterator<Buffer> iterator() {
            Iterator<Buffer> it = new Iterator<Buffer>() {
                private final Iterator<Integer> levelIterator = registry.keySet().iterator();
                private List<Buffer> currentList = registry.get(levelIterator.next());
                private Iterator<Buffer> bufferIterator =
                        currentList == null ? null : currentList.iterator();

                @Override
                public boolean hasNext() {
                    if (bufferIterator == null) {
                        return false;
                    }
                    if (bufferIterator.hasNext()) {
                        return true;
                    }
                    // The current level iterator has just finished, try to bump level
                    if (levelIterator.hasNext()) {
                        List<Buffer> currentList = registry.get(levelIterator.next());
                        bufferIterator = currentList.iterator();
                        return true;
                    } else {
                        // Nothing left, signal this by nulling bufferIterator
                        bufferIterator = null;
                        return false;
                    }
                }

                @Override
                public Buffer next() {
                    if (hasNext()) {
                        return bufferIterator.next();
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return it;
        }

        /**
         * Absorbs the data in other into this, merging buffers as necessary to trim
         * the aggregate down to capacity. This method is only used when aggregating
         * RandomPercentile instances.
         *
         * @param other other BufferMap to merge in
         */
        public void absorb(BufferMap other) {
            // Add all of other's buffers to the map - possibly exceeding cap
            int fullCount = 0;
            Buffer notFull = null;
            Iterator<Buffer> otherIterator = other.iterator();
            while (otherIterator.hasNext()) {
                Buffer buffer = otherIterator.next();
                if (buffer.hasCapacity()) {
                    notFull = buffer;
                } else {
                    fullCount++;
                }
                register(buffer);
                count++;
            }
            // Determine how many extra buffers we now have: new + old - capacity
            final int excess = fullCount + (notFull == null ? 0 : 1) + count - capacity;
            // Now eliminate the excess by merging
            for (int i = 0; i < excess - 1; i++) {
                mergeUp();
                count--;
            }
        }

        /**
         * Find two buffers, first and second, of minimal level. Then merge
         * first into second and discard first.
         * <p>
         * If the buffers have different levels, make second the higher level
         * buffer and make probability of selection in the merge proportional
         * to level weight ratio.
         * <p>
         * This method is only used when aggregating RandomPercentile instances.
         */
        public void mergeUp() {
            // Find two minimum-level buffers to merge
            // Loop depends on two invariants:
            //   0) iterator goes in level order
            //   1) there are no empty lists in the registry
            Iterator<Buffer> bufferIterator = iterator();
            Buffer first = null;
            Buffer second = null;
            while ((first == null || second == null) && bufferIterator.hasNext()) {
                Buffer buffer = bufferIterator.next();
                if (!buffer.hasCapacity()) { // Skip not full buffers
                    if (first == null) {
                        first = buffer;
                    } else {
                        second = buffer;
                    }
                }
            }
            if (first == null || second == null || first.level > second.level) {
                throw new MathIllegalStateException(LocalizedCoreFormats.INTERNAL_ERROR);
            }
            // Merge first into second and deregister first.
            // Assumes that first has level <= second (checked above).
            if (first.getLevel() == second.getLevel()) {
                second.mergeWith(first);
            } else {
                first.mergeInto(second);
            }
            deRegister(first);
        }
    }
}
