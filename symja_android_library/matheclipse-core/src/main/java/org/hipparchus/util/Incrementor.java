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
package org.hipparchus.util;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.NullArgumentException;

/**
 * Utility that increments a counter until a maximum is reached, at
 * which point, the instance will by default throw a
 * {@link MathIllegalStateException}.
 * However, the user is able to override this behaviour by defining a
 * custom {@link MaxCountExceededCallback callback}, in order to e.g.
 * select which exception must be thrown.
 */
public class Incrementor {
    /**
     * Default callback.
     */
    private static final MaxCountExceededCallback DEFAULT_CALLBACK =
            new MaxCountExceededCallback() {
                @Override
                public void trigger(int max) throws MathIllegalStateException {
                    throw new MathIllegalStateException(LocalizedCoreFormats.MAX_COUNT_EXCEEDED, max);
                }
            };

    /**
     * Upper limit for the counter.
     */
    private final int maximalCount;
    /**
     * Function called at counter exhaustion.
     */
    private final MaxCountExceededCallback maxCountCallback;
    /**
     * Current count.
     */
    private int count = 0;

    /**
     * Creates an Incrementor.
     * <p>
     * The maximal value will be set to {@code Integer.MAX_VALUE}.
     */
    public Incrementor() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates an Incrementor.
     *
     * @param max Maximal count.
     * @throws MathIllegalArgumentException if {@code max} is negative.
     */
    public Incrementor(int max) {
        this(max, DEFAULT_CALLBACK);
    }

    /**
     * Creates an Incrementor.
     *
     * @param max Maximal count.
     * @param cb  Function to be called when the maximal count has been reached.
     * @throws NullArgumentException        if {@code cb} is {@code null}.
     * @throws MathIllegalArgumentException if {@code max} is negative.
     */
    public Incrementor(int max,
                       MaxCountExceededCallback cb)
            throws NullArgumentException {
        this(0, max, cb);
    }

    /**
     * Creates an Incrementor.
     *
     * @param count Initial counter value.
     * @param max   Maximal count.
     * @param cb    Function to be called when the maximal count has been reached.
     * @throws NullArgumentException        if {@code cb} is {@code null}.
     * @throws MathIllegalArgumentException if {@code max} is negative.
     */
    private Incrementor(int count,
                        int max,
                        MaxCountExceededCallback cb)
            throws NullArgumentException {
        if (cb == null) {
            throw new NullArgumentException();
        }
        if (max < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, max, 0);
        }
        this.maximalCount = max;
        this.maxCountCallback = cb;
        this.count = count;
    }

    /**
     * Creates a new instance and set the counter to the given value.
     *
     * @param value Value of the counter.
     * @return a new instance.
     */
    public Incrementor withCount(int value) {
        return new Incrementor(value,
                this.maximalCount,
                this.maxCountCallback);
    }

    /**
     * Creates a new instance with a given maximal count.
     * The counter is reset to 0.
     *
     * @param max Maximal count.
     * @return a new instance.
     * @throws MathIllegalArgumentException if {@code max} is negative.
     */
    public Incrementor withMaximalCount(int max) {
        return new Incrementor(0,
                max,
                this.maxCountCallback);
    }

    /**
     * Creates a new instance with a given callback.
     * The counter is reset to 0.
     *
     * @param cb Callback to be called at counter exhaustion.
     * @return a new instance.
     */
    public Incrementor withCallback(MaxCountExceededCallback cb) {
        return new Incrementor(0,
                this.maximalCount,
                cb);
    }

    /**
     * Gets the upper limit of the counter.
     *
     * @return the counter upper limit.
     */
    public int getMaximalCount() {
        return maximalCount;
    }

    /**
     * Gets the current count.
     *
     * @return the current count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Checks whether incrementing the counter {@code nTimes} is allowed.
     *
     * @return {@code false} if calling {@link #increment()}
     * will trigger a {@code MathIllegalStateException},
     * {@code true} otherwise.
     */
    public boolean canIncrement() {
        return canIncrement(1);
    }

    /**
     * Checks whether incrementing the counter several times is allowed.
     *
     * @param nTimes Number of increments.
     * @return {@code false} if calling {@link #increment(int)
     * increment(nTimes)} would call the {@link MaxCountExceededCallback callback}
     * {@code true} otherwise.
     * @throws MathIllegalArgumentException if {@code nTimes} is negative.
     */
    public boolean canIncrement(int nTimes) {
        if (nTimes < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL,
                    nTimes, 0);
        }
        return count + nTimes <= maximalCount;
    }

    /**
     * Performs multiple increments.
     *
     * @param nTimes Number of increments.
     * @throws MathIllegalArgumentException if {@code nTimes} is negative.
     * @see #increment()
     */
    public void increment(int nTimes) {
        if (nTimes < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL,
                    nTimes, 0);
        }

        for (int i = 0; i < nTimes; i++) {
            increment();
        }
    }

    /**
     * Adds the increment value to the current iteration count.
     * At counter exhaustion, this method will call the
     * {@link MaxCountExceededCallback#trigger(int) trigger} method of the
     * callback object passed to the
     * {@link #withCallback(MaxCountExceededCallback)} method.
     *
     * @see #increment(int)
     */
    public void increment() {
        if (++count > maximalCount) {
            maxCountCallback.trigger(maximalCount);
        }
    }

    /**
     * Resets the counter to 0.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Defines a method to be called at counter exhaustion.
     * The {@link #trigger(int) trigger} method should usually throw an exception.
     */
    public interface MaxCountExceededCallback {
        /**
         * Function called when the maximal count has been reached.
         *
         * @param maximalCount Maximal count.
         * @throws MathIllegalStateException at counter exhaustion
         */
        void trigger(int maximalCount) throws MathIllegalStateException;
    }
}
