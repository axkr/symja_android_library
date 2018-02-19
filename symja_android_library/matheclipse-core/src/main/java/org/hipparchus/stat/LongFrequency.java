///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.hipparchus.stat;
//
//import java.util.Comparator;
//
///**
// * Maintains a frequency distribution of Long values.
// * <p>
// * Accepts byte, short, int, long primitive or Integer and Long values.
// * <p>
// * Integer values (byte, short, int, long, Integer, Long) are not
// * distinguished by type, i.e. {@code addValue(Long.valueOf(2)),
// * addValue(2), addValue(2L)} all have the same effect (similarly
// * for arguments to {@code getCount()} etc.).
// * <p>
// * NOTE: byte and short values will be implicitly converted to int values
// * by the compiler, thus there are no explicit overloaded methods for these
// * primitive types.
// * <p>
// * The values are ordered using the default (natural order), unless a
// * {@code Comparator} is supplied in the constructor.
// */
//public class LongFrequency extends Frequency<Long> {
//
//    /**
//     * Serializable version identifier
//     */
//    private static final long serialVersionUID = 20160322L;
//
//    /**
//     * Default constructor.
//     */
//    public LongFrequency() {
//    }
//
//    /**
//     * Constructor allowing values Comparator to be specified.
//     *
//     * @param comparator Comparator used to order values
//     */
//    public LongFrequency(Comparator<? super Long> comparator) {
//        super(comparator);
//    }
//
//    /**
//     * Adds 1 to the frequency count for v.
//     *
//     * @param v the value to add.
//     */
//    public void addValue(int v) {
//        incrementValue(Long.valueOf(v), 1);
//    }
//
//    /**
//     * Increments the frequency count for v.
//     *
//     * @param v         the value to add.
//     * @param increment the amount by which the value should be incremented
//     */
//    public void incrementValue(int v, long increment) {
//        incrementValue(Long.valueOf(v), increment);
//    }
//
//    //-------------------------------------------------------------------------
//
//    /**
//     * Returns the number of values equal to v.
//     *
//     * @param v the value to lookup.
//     * @return the frequency of v.
//     */
//    public long getCount(int v) {
//        return getCount(Long.valueOf(v));
//    }
//
//    /**
//     * Returns the percentage of values that are equal to v
//     * (as a proportion between 0 and 1).
//     * <p>
//     * Returns {@code Double.NaN} if no values have been added.
//     *
//     * @param v the value to lookup
//     * @return the proportion of values equal to v
//     */
//    public double getPct(int v) {
//        return getPct(Long.valueOf(v));
//    }
//
//    //-----------------------------------------------------------------------------------------
//
//    /**
//     * Returns the cumulative frequency of values less than or equal to v.
//     *
//     * @param v the value to lookup.
//     * @return the proportion of values equal to v
//     */
//    public long getCumFreq(int v) {
//        return getCumFreq(Long.valueOf(v));
//    }
//
//    //----------------------------------------------------------------------------------------------
//
//    /**
//     * Returns the cumulative percentage of values less than or equal to v
//     * (as a proportion between 0 and 1).
//     * <p>
//     * Returns {@code Double.NaN} if no values have been added.
//     *
//     * @param v the value to lookup
//     * @return the proportion of values less than or equal to v
//     */
//    public double getCumPct(int v) {
//        return getCumPct(Long.valueOf(v));
//    }
//
//}
