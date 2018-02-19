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
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import com.duy.lambda.DoubleConsumer;

/**
 * Extends the definition of {@link UnivariateStatistic} with
 * {@link #increment} and {@link #incrementAll(double[])} methods for adding
 * values and updating internal state.
 * <p>
 * This interface is designed to be used for calculating statistics that can be
 * computed in one pass through the data without storing the full array of
 * sample values.
 * <p>
 * Note: unless otherwise stated, the {@link #evaluate(double[])} and
 * {@link #evaluate(double[], int, int)} methods do <b>NOT</b> alter the internal
 * state of the respective statistic.
 */
public interface StorelessUnivariateStatistic extends UnivariateStatistic, DoubleConsumer {

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation creates a copy of this {@link StorelessUnivariateStatistic}
     * instance, calls {@link #clear} on it, then calls {@link #incrementAll} with the specified
     * portion of the input array, and then uses {@link #getResult} to compute the return value.
     * <p>
     * Note that this implementation does not change the internal state of the statistic.
     * <p>
     * Implementations may override this method with a more efficient and possibly more
     * accurate implementation that works directly with the input array.
     *
     * @param values the input array
     * @param begin  the index of the first element to include
     * @param length the number of elements to include
     * @return the value of the statistic applied to the included array entries
     * @throws MathIllegalArgumentException if the array is null or the indices are not valid
     * @see UnivariateStatistic#evaluate(double[], int, int)
     */
    @Override
    default double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {

        if (MathArrays.verifyValues(values, begin, length)) {
            StorelessUnivariateStatistic stat = copy();
            stat.clear();
            stat.incrementAll(values, begin, length);
            return stat.getResult();
        }
        return Double.NaN;
    }

    /**
     * Updates the internal state of the statistic to reflect the addition of the new value.
     *
     * @param d the new value.
     */
    void increment(double d);

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(double value) {
        increment(value);
    }

    /**
     * Updates the internal state of the statistic to reflect addition of
     * all values in the values array. Does not clear the statistic first --
     * i.e., the values are added <strong>incrementally</strong> to the dataset.
     * <p>
     * The default implementation delegates to
     * <code>incrementAll(double[], int, int)</code> in the natural way.
     *
     * @param values array holding the new values to add
     * @throws MathIllegalArgumentException if the array is null
     */
    default void incrementAll(double[] values) throws MathIllegalArgumentException {
        MathUtils.checkNotNull(values, LocalizedCoreFormats.INPUT_ARRAY);
        incrementAll(values, 0, values.length);
    }


    /**
     * Updates the internal state of the statistic to reflect addition of
     * the values in the designated portion of the values array.  Does not
     * clear the statistic first -- i.e., the values are added
     * <strong>incrementally</strong> to the dataset.
     * <p>
     * The default implementation just calls {@link #increment} in a loop over
     * the specified portion of the input array.
     *
     * @param values array holding the new values to add
     * @param start  the array index of the first value to add
     * @param length the number of elements to add
     * @throws MathIllegalArgumentException if the array is null or the index
     */
    default void incrementAll(double[] values, int start, int length)
            throws MathIllegalArgumentException {

        if (MathArrays.verifyValues(values, start, length)) {
            int k = start + length;
            for (int i = start; i < k; i++) {
                increment(values[i]);
            }
        }
    }


    /**
     * Returns the current value of the Statistic.
     *
     * @return value of the statistic, <code>Double.NaN</code> if it
     * has been cleared or just instantiated.
     */
    double getResult();

    /**
     * Returns the number of values that have been added.
     *
     * @return the number of values.
     */
    long getN();

    /**
     * Clears the internal state of the Statistic
     */
    void clear();

    /**
     * Returns a copy of the statistic with the same internal state.
     *
     * @return a copy of the statistic
     */
    @Override
    StorelessUnivariateStatistic copy();

}
