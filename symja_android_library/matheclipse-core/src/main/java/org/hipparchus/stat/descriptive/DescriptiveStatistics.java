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
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.stat.descriptive.moment.GeometricMean;
import org.hipparchus.stat.descriptive.moment.Kurtosis;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.Skewness;
import org.hipparchus.stat.descriptive.moment.Variance;
import org.hipparchus.stat.descriptive.rank.Max;
import org.hipparchus.stat.descriptive.rank.Min;
import org.hipparchus.stat.descriptive.rank.Percentile;
import org.hipparchus.stat.descriptive.summary.Sum;
import org.hipparchus.stat.descriptive.summary.SumOfSquares;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.hipparchus.util.ResizableDoubleArray;

import java.io.Serializable;
import java.util.Arrays;
import com.duy.lambda.DoubleConsumer;


/**
 * Maintains a dataset of values of a single variable and computes descriptive
 * statistics based on stored data.
 * <p>
 * The {@link #getWindowSize() windowSize} property sets a limit on the number
 * of values that can be stored in the dataset. The default value, INFINITE_WINDOW,
 * puts no limit on the size of the dataset. This value should be used with
 * caution, as the backing store will grow without bound in this case.
 * <p>
 * For very large datasets, {@link StreamingStatistics}, which does not store
 * the dataset, should be used instead of this class. If <code>windowSize</code>
 * is not INFINITE_WINDOW and more values are added than can be stored in the
 * dataset, new values are added in a "rolling" manner, with new values replacing
 * the "oldest" values in the dataset.
 * <p>
 * Note: this class is not threadsafe.
 */
public class DescriptiveStatistics
        implements StatisticalSummary, DoubleConsumer, Serializable {

    /**
     * Represents an infinite window size.  When the {@link #getWindowSize()}
     * returns this value, there is no limit to the number of data values
     * that can be stored in the dataset.
     */
    protected static final int INFINITE_WINDOW = -1;

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = 20160411L;

    /**
     * The statistic used to calculate the population variance - fixed.
     */
    private static final UnivariateStatistic POPULATION_VARIANCE = new Variance(false);

    /**
     * Maximum statistic implementation.
     */
    private final UnivariateStatistic maxImpl;
    /**
     * Minimum statistic implementation.
     */
    private final UnivariateStatistic minImpl;
    /**
     * Sum statistic implementation.
     */
    private final UnivariateStatistic sumImpl;
    /**
     * Sum of squares statistic implementation.
     */
    private final UnivariateStatistic sumOfSquaresImpl;
    /**
     * Mean statistic implementation.
     */
    private final UnivariateStatistic meanImpl;
    /**
     * Variance statistic implementation.
     */
    private final UnivariateStatistic varianceImpl;
    /**
     * Geometric mean statistic implementation.
     */
    private final UnivariateStatistic geometricMeanImpl;
    /**
     * Kurtosis statistic implementation.
     */
    private final UnivariateStatistic kurtosisImpl;
    /**
     * Skewness statistic implementation.
     */
    private final UnivariateStatistic skewnessImpl;
    /**
     * Percentile statistic implementation.
     */
    private final Percentile percentileImpl;
    /**
     * Stored data values.
     */
    private final ResizableDoubleArray eDA;
    /**
     * holds the window size.
     */
    private int windowSize;

    /**
     * Construct a DescriptiveStatistics instance with an infinite window.
     */
    public DescriptiveStatistics() {
        this(INFINITE_WINDOW);
    }

    /**
     * Construct a DescriptiveStatistics instance with the specified window.
     *
     * @param size the window size.
     * @throws MathIllegalArgumentException if window size is less than 1 but
     *                                      not equal to {@link #INFINITE_WINDOW}
     */
    public DescriptiveStatistics(int size) throws MathIllegalArgumentException {
        this(size, false, null);
    }

    /**
     * Construct a DescriptiveStatistics instance with an infinite window
     * and the initial data values in double[] initialDoubleArray.
     *
     * @param initialDoubleArray the initial double[].
     * @throws org.hipparchus.exception.NullArgumentException if the input array is null
     */
    public DescriptiveStatistics(double[] initialDoubleArray) {
        this(INFINITE_WINDOW, true, initialDoubleArray);
    }

    /**
     * Copy constructor.
     * <p>
     * Construct a new DescriptiveStatistics instance that
     * is a copy of original.
     *
     * @param original DescriptiveStatistics instance to copy
     * @throws org.hipparchus.exception.NullArgumentException if original is null
     */
    protected DescriptiveStatistics(DescriptiveStatistics original) {
        MathUtils.checkNotNull(original);

        // Copy data and window size
        this.windowSize = original.windowSize;
        this.eDA = original.eDA.copy();

        // Copy implementations
        this.maxImpl = original.maxImpl.copy();
        this.minImpl = original.minImpl.copy();
        this.meanImpl = original.meanImpl.copy();
        this.sumImpl = original.sumImpl.copy();
        this.sumOfSquaresImpl = original.sumOfSquaresImpl.copy();
        this.varianceImpl = original.varianceImpl.copy();
        this.geometricMeanImpl = original.geometricMeanImpl.copy();
        this.kurtosisImpl = original.kurtosisImpl.copy();
        this.skewnessImpl = original.skewnessImpl.copy();
        this.percentileImpl = original.percentileImpl.copy();
    }

    /**
     * Construct a DescriptiveStatistics instance with the specified window.
     *
     * @param windowSize       the window size
     * @param hasInitialValues if initial values have been provided
     * @param initialValues    the initial values
     * @throws org.hipparchus.exception.NullArgumentException if initialValues is null
     * @throws MathIllegalArgumentException                   if window size is less than 1 but
     *                                                        not equal to {@link #INFINITE_WINDOW}
     */
    DescriptiveStatistics(int windowSize, boolean hasInitialValues, double[] initialValues) {
        if (windowSize < 1 && windowSize != INFINITE_WINDOW) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.NOT_POSITIVE_WINDOW_SIZE, windowSize);
        }

        if (hasInitialValues) {
            MathUtils.checkNotNull(initialValues, LocalizedCoreFormats.INPUT_ARRAY);
        }

        this.windowSize = windowSize;
        int initialCapacity = this.windowSize < 0 ? 100 : this.windowSize;
        this.eDA = hasInitialValues ?
                new ResizableDoubleArray(initialValues) :
                new ResizableDoubleArray(initialCapacity);

        maxImpl = new Max();
        minImpl = new Min();
        sumImpl = new Sum();
        sumOfSquaresImpl = new SumOfSquares();
        meanImpl = new Mean();
        varianceImpl = new Variance();
        geometricMeanImpl = new GeometricMean();
        kurtosisImpl = new Kurtosis();
        skewnessImpl = new Skewness();
        percentileImpl = new Percentile();
    }

    /**
     * Returns a copy of this DescriptiveStatistics instance with the same internal state.
     *
     * @return a copy of this
     */
    public DescriptiveStatistics copy() {
        return new DescriptiveStatistics(this);
    }

    /**
     * Adds the value to the dataset. If the dataset is at the maximum size
     * (i.e., the number of stored elements equals the currently configured
     * windowSize), the first (oldest) element in the dataset is discarded
     * to make room for the new value.
     *
     * @param v the value to be added
     */
    public void addValue(double v) {
        if (windowSize != INFINITE_WINDOW) {
            if (getN() == windowSize) {
                eDA.addElementRolling(v);
            } else if (getN() < windowSize) {
                eDA.addElement(v);
            }
        } else {
            eDA.addElement(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(double v) {
        addValue(v);
    }

    /**
     * Resets all statistics and storage.
     */
    public void clear() {
        eDA.clear();
    }

    /**
     * Removes the most recent value from the dataset.
     *
     * @throws MathIllegalStateException if there are no elements stored
     */
    public void removeMostRecentValue() throws MathIllegalStateException {
        try {
            eDA.discardMostRecentElements(1);
        } catch (MathIllegalArgumentException ex) {
            throw new MathIllegalStateException(LocalizedCoreFormats.NO_DATA);
        }
    }

    /**
     * Replaces the most recently stored value with the given value.
     * There must be at least one element stored to call this method.
     *
     * @param v the value to replace the most recent stored value
     * @return replaced value
     * @throws MathIllegalStateException if there are no elements stored
     */
    public double replaceMostRecentValue(double v) throws MathIllegalStateException {
        return eDA.substituteMostRecentElement(v);
    }

    /**
     * Apply the given statistic to the data associated with this set of statistics.
     *
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        // No try-catch or advertised exception here because arguments
        // are guaranteed valid.
        return eDA.compute(stat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMean() {
        return apply(meanImpl);
    }

    /**
     * Returns the geometric mean of the available values.
     * <p>
     * See {@link GeometricMean} for details on the computing algorithm.
     *
     * @return The geometricMean, Double.NaN if no values have been added,
     * or if any negative values have been added.
     * @see <a href="http://www.xycoon.com/geometric_mean.htm">
     * Geometric mean</a>
     */
    public double getGeometricMean() {
        return apply(geometricMeanImpl);
    }

    /**
     * Returns the standard deviation of the available values.
     *
     * @return The standard deviation, Double.NaN if no values have been added
     * or 0.0 for a single value set.
     */
    @Override
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (getN() > 0) {
            if (getN() > 1) {
                stdDev = FastMath.sqrt(getVariance());
            } else {
                stdDev = 0.0;
            }
        }
        return stdDev;
    }

    /**
     * Returns the quadratic mean of the available values.
     *
     * @return The quadratic mean or {@code Double.NaN} if no values
     * have been added.
     * @see <a href="http://mathworld.wolfram.com/Root-Mean-Square.html">
     * Root Mean Square</a>
     */
    public double getQuadraticMean() {
        final long n = getN();
        return n > 0 ? FastMath.sqrt(getSumOfSquares() / n) : Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getVariance() {
        return apply(varianceImpl);
    }

    /**
     * Returns the population variance of the available values.
     *
     * @return The population variance, Double.NaN if no values have been added,
     * or 0.0 for a single value set.
     * @see <a href="http://en.wikibooks.org/wiki/Statistics/Summary/Variance">
     * Population variance</a>
     */
    public double getPopulationVariance() {
        return apply(POPULATION_VARIANCE);
    }

    /**
     * Returns the skewness of the available values. Skewness is a
     * measure of the asymmetry of a given distribution.
     *
     * @return The skewness, Double.NaN if less than 3 values have been added.
     */
    public double getSkewness() {
        return apply(skewnessImpl);
    }

    /**
     * Returns the Kurtosis of the available values. Kurtosis is a
     * measure of the "peakedness" of a distribution.
     *
     * @return The kurtosis, Double.NaN if less than 4 values have been added.
     */
    public double getKurtosis() {
        return apply(kurtosisImpl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMax() {
        return apply(maxImpl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMin() {
        return apply(minImpl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSum() {
        return apply(sumImpl);
    }

    /**
     * Returns the sum of the squares of the available values.
     *
     * @return The sum of the squares or Double.NaN if no
     * values have been added.
     */
    public double getSumOfSquares() {
        return apply(sumOfSquaresImpl);
    }

    /**
     * Returns an estimate for the pth percentile of the stored values.
     * <p>
     * The implementation provided here follows the first estimation procedure presented
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm">here.</a>
     * </p><p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>0 &lt; p &le; 100</code> (otherwise an
     * <code>MathIllegalArgumentException</code> is thrown)</li>
     * <li>at least one value must be stored (returns <code>Double.NaN
     * </code> otherwise)</li>
     * </ul>
     *
     * @param p the requested percentile (scaled from 0 - 100)
     * @return An estimate for the pth percentile of the stored data
     * @throws MathIllegalArgumentException if p is not a valid quantile
     */
    public double getPercentile(final double p)
            throws MathIllegalArgumentException {

        percentileImpl.setQuantile(p);
        return apply(percentileImpl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return eDA.getNumElements();
    }

    /**
     * Returns the maximum number of values that can be stored in the
     * dataset, or INFINITE_WINDOW (-1) if there is no limit.
     *
     * @return The current window size or -1 if its Infinite.
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * WindowSize controls the number of values that contribute to the
     * reported statistics.  For example, if windowSize is set to 3 and the
     * values {1,2,3,4,5} have been added <strong> in that order</strong> then
     * the <i>available values</i> are {3,4,5} and all reported statistics will
     * be based on these values. If {@code windowSize} is decreased as a result
     * of this call and there are more than the new value of elements in the
     * current dataset, values from the front of the array are discarded to
     * reduce the dataset to {@code windowSize} elements.
     *
     * @param windowSize sets the size of the window.
     * @throws MathIllegalArgumentException if window size is less than 1 but
     *                                      not equal to {@link #INFINITE_WINDOW}
     */
    public void setWindowSize(int windowSize)
            throws MathIllegalArgumentException {

        if (windowSize < 1 && windowSize != INFINITE_WINDOW) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.NOT_POSITIVE_WINDOW_SIZE, windowSize);
        }

        this.windowSize = windowSize;

        // We need to check to see if we need to discard elements
        // from the front of the array.  If the windowSize is less than
        // the current number of elements.
        if (windowSize != INFINITE_WINDOW && windowSize < eDA.getNumElements()) {
            eDA.discardFrontElements(eDA.getNumElements() - windowSize);
        }
    }

    /**
     * Returns the current set of values in an array of double primitives.
     * The order of addition is preserved.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     *
     * @return the current set of numbers in the order in which they
     * were added to this set
     */
    public double[] getValues() {
        return eDA.getElements();
    }

    /**
     * Returns the current set of values in an array of double primitives,
     * sorted in ascending order.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     *
     * @return returns the current set of
     * numbers sorted in ascending order
     */
    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }

    /**
     * Returns the element at the specified index
     *
     * @param index The Index of the element
     * @return return the element at the specified index
     */
    public double getElement(int index) {
        return eDA.getElement(index);
    }

    /**
     * Generates a text report displaying univariate statistics from values
     * that have been added.  Each statistic is displayed on a separate line.
     *
     * @return String with line feeds displaying statistics
     */
    @Override
    public String toString() {
        final StringBuilder outBuffer = new StringBuilder();
        final String endl = "\n";
        outBuffer.append("DescriptiveStatistics:").append(endl);
        outBuffer.append("n: ").append(getN()).append(endl);
        outBuffer.append("min: ").append(getMin()).append(endl);
        outBuffer.append("max: ").append(getMax()).append(endl);
        outBuffer.append("mean: ").append(getMean()).append(endl);
        outBuffer.append("std dev: ").append(getStandardDeviation()).append(endl);
        try {
            // No catch for MIAE because actual parameter is valid below
            outBuffer.append("median: ").append(getPercentile(50)).append(endl);
        } catch (MathIllegalStateException ex) {
            outBuffer.append("median: unavailable").append(endl);
        }
        outBuffer.append("skewness: ").append(getSkewness()).append(endl);
        outBuffer.append("kurtosis: ").append(getKurtosis()).append(endl);
        return outBuffer.toString();
    }

}
