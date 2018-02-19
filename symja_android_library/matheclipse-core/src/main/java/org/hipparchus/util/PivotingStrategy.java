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

import org.hipparchus.exception.MathIllegalArgumentException;

/**
 * A strategy to pick a pivoting index of an array for doing partitioning.
 */
public enum PivotingStrategy {

    /**
     * A mid point strategy based on the average of begin and end indices.
     */
    CENTRAL {
        /**
         * {@inheritDoc}
         * This in particular picks a average of begin and end indices
         * @return The index corresponding to a simple average of
         * the first and the last element indices of the array slice
         * @throws MathIllegalArgumentException when indices exceeds range
         */
        @Override
        public int pivotIndex(final double[] work, final int begin, final int end)
                throws MathIllegalArgumentException {
            MathArrays.verifyValues(work, begin, end - begin);
            return begin + (end - begin) / 2;
        }
    },

    /**
     * Classic median of 3 strategy given begin and end indices.
     */
    MEDIAN_OF_3 {
        /**
         * {@inheritDoc}
         * This in specific makes use of median of 3 pivoting.
         * @return The index corresponding to a pivot chosen between the
         * first, middle and the last indices of the array slice
         * @throws MathIllegalArgumentException when indices exceeds range
         */
        @Override
        public int pivotIndex(final double[] work, final int begin, final int end)
                throws MathIllegalArgumentException {
            MathArrays.verifyValues(work, begin, end - begin);
            final int inclusiveEnd = end - 1;
            final int middle = begin + (inclusiveEnd - begin) / 2;
            final double wBegin = work[begin];
            final double wMiddle = work[middle];
            final double wEnd = work[inclusiveEnd];

            if (wBegin < wMiddle) {
                if (wMiddle < wEnd) {
                    return middle;
                } else {
                    return wBegin < wEnd ? inclusiveEnd : begin;
                }
            } else {
                if (wBegin < wEnd) {
                    return begin;
                } else {
                    return wMiddle < wEnd ? inclusiveEnd : middle;
                }
            }
        }
    };

    /**
     * Find pivot index of the array so that partition and K<sup>th</sup>
     * element selection can be made
     *
     * @param work  data array
     * @param begin index of the first element of the slice
     * @param end   index after the last element of the slice
     * @return the index of the pivot element chosen between the
     * first and the last element of the array slice
     * @throws MathIllegalArgumentException when indices exceeds range
     */
    public abstract int pivotIndex(double[] work, int begin, int end)
            throws MathIllegalArgumentException;

}
