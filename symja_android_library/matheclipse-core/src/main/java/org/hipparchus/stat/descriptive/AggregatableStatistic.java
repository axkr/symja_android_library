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

package org.hipparchus.stat.descriptive;


import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.util.MathUtils;

/**
 * An interface for statistics that can aggregate results.
 *
 * @param <T> the type of statistic
 */
public interface AggregatableStatistic<T> {

    /**
     * Aggregates the provided instance into this instance.
     * <p>
     * This method can be used to combine statistics computed over partitions or
     * subsamples - i.e., the value of this instance after this operation should
     * be the same as if a single statistic would have been applied over the
     * combined dataset.
     *
     * @param other the instance to aggregate into this instance
     * @throws NullArgumentException if the input is null
     */
    void aggregate(T other) throws NullArgumentException;

    /**
     * Aggregates the results from the provided instances into this instance.
     * <p>
     * This method can be used to combine statistics computed over partitions or
     * subsamples - i.e., the value of this instance after this operation should
     * be the same as if a single statistic would have been applied over the
     * combined dataset.
     *
     * @param others the other instances to aggregate into this instance
     * @throws NullArgumentException if either others or any instance is null
     */
    @SuppressWarnings("unchecked")
    default void aggregate(T... others) {
        MathUtils.checkNotNull(others);
        for (T other : others) {
            aggregate(other);
        }
    }

    /**
     * Aggregates the results from the provided instances into this instance.
     * <p>
     * This method can be used to combine statistics computed over partitions or
     * subsamples - i.e., the value of this instance after this operation should
     * be the same as if a single statistic would have been applied over the
     * combined dataset.
     *
     * @param others the other instances to aggregate into this instance
     * @throws NullArgumentException if either others or any instance is null
     */
    default void aggregate(Iterable<T> others) {
        MathUtils.checkNotNull(others);
        for (T other : others) {
            aggregate(other);
        }
    }

}
