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
//import org.hipparchus.exception.NullArgumentException;
//import org.hipparchus.util.MathUtils;
//
//import java.io.Serializable;
//import java.text.NumberFormat;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.NavigableMap;
//import java.util.Objects;
//import java.util.TreeMap;
//import java.util.function.ToLongFunction;
//
///**
// * Maintains a frequency distribution of Comparable values.
// * <p>
// * The values are ordered using the default (natural order), unless a
// * {@code Comparator} is supplied in the constructor.
// *
// * @param <T> the element type
// */
//public class Frequency<T extends Comparable<T>> implements Serializable {
//
//    /**
//     * Serializable version identifier
//     */
//    private static final long serialVersionUID = 20160322L;
//
//    /**
//     * underlying collection
//     */
//    private final NavigableMap<T, Long> freqTable;
//
//    /**
//     * Default constructor.
//     */
//    public Frequency() {
//        freqTable = new TreeMap<>();
//    }
//
//    /**
//     * Constructor allowing values Comparator to be specified.
//     *
//     * @param comparator Comparator used to order values
//     */
//    public Frequency(Comparator<? super T> comparator) {
//        freqTable = new TreeMap<>(comparator);
//    }
//
//    /**
//     * Adds 1 to the frequency count for v.
//     *
//     * @param v the value to add.
//     */
//    public void addValue(T v) {
//        incrementValue(v, 1);
//    }
//
//    /**
//     * Increments the frequency count for v.
//     *
//     * @param v         the value to add.
//     * @param increment the amount by which the value should be incremented
//     */
//    public void incrementValue(T v, long increment) {
//        Long count = freqTable.getOrDefault(v, Long.valueOf(0));
//        freqTable.put(v, Long.valueOf(count.longValue() + increment));
//    }
//
//    /**
//     * Clears the frequency table
//     */
//    public void clear() {
//        freqTable.clear();
//    }
//
//    /**
//     * Returns an Iterator over the set of values that have been added.
//     *
//     * @return values Iterator
//     */
//    public Iterator<T> valuesIterator() {
//        return freqTable.keySet().iterator();
//    }
//
//    /**
//     * Return an Iterator over the set of keys and values that have been added.
//     * Using the entry set to iterate is more efficient in the case where you
//     * need to access respective counts as well as values, since it doesn't
//     * require a "get" for every key...the value is provided in the Map.Entry.
//     *
//     * @return entry set Iterator
//     */
//    public Iterator<Map.Entry<T, Long>> entrySetIterator() {
//        return freqTable.entrySet().iterator();
//    }
//
//    //-------------------------------------------------------------------------
//
//    /**
//     * Returns the sum of all frequencies.
//     *
//     * @return the total frequency count.
//     */
//    public long getSumFreq() {
//        return freqTable.values()
//                .stream()
//                .mapToLong(new ToLongFunction<Long>() {
//                    @Override
//                    public long applyAsLong(Long aLong) {
//                        return aLong.longValue();
//                    }
//                })
//                .sum();
//    }
//
//    /**
//     * Returns the number of values equal to v.
//     * Returns 0 if the value is not comparable.
//     *
//     * @param v the value to lookup.
//     * @return the frequency of v.
//     */
//    public long getCount(T v) {
//        return freqTable.getOrDefault(v, 0L);
//    }
//
//    /**
//     * Returns the number of values in the frequency table.
//     *
//     * @return the number of unique values that have been added to the frequency table.
//     * @see #valuesIterator()
//     */
//    public int getUniqueCount() {
//        return freqTable.keySet().size();
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
//    public double getPct(T v) {
//        final long sumFreq = getSumFreq();
//        if (sumFreq == 0) {
//            return Double.NaN;
//        }
//        return (double) getCount(v) / (double) sumFreq;
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
//    public long getCumFreq(T v) {
//        if (getSumFreq() == 0) {
//            return 0;
//        }
//
//        NavigableMap<T, Long> headMap = freqTable.headMap(v, true);
//
//        if (headMap.isEmpty()) {
//            // v is less than first value
//            return 0;
//        } else if (headMap.size() == freqTable.size()) {
//            // v is greater than or equal to last value
//            return getSumFreq();
//        }
//
//        return headMap.values()
//                .stream()
//                .mapToLong(new ToLongFunction<Long>() {
//                    @Override
//                    public long applyAsLong(Long aLong) {
//                        return aLong.longValue();
//                    }
//                })
//                .sum();
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
//    public double getCumPct(T v) {
//        final long sumFreq = getSumFreq();
//        if (sumFreq == 0) {
//            return Double.NaN;
//        }
//        return (double) getCumFreq(v) / (double) sumFreq;
//    }
//
//    /**
//     * Returns the mode value(s) in comparator order.
//     *
//     * @return a list containing the value(s) which appear most often.
//     */
//    public List<T> getMode() {
////        // Get the max count first
////        final long mostPopular =
////                freqTable.values()
////                        .stream()
////                        .mapToLong(new ToLongFunction<Long>() {
////                            @Override
////                            public long applyAsLong(Long aLong) {
////                                return aLong.longValue();
////                            }
////                        })
////                        .max()
////                        .orElse(0L);
////
////        return freqTable.entrySet()
////                .stream()
////                .filter(new Predicate<Map.Entry<T, Long>>() {
////                    @Override
////                    public boolean test(Map.Entry<T, Long> entry) {
////                        return entry.getValue() == mostPopular;
////                    }
////                })
////                .map(new Function<Map.Entry<T, Long>, T>() {
////                    @Override
////                    public T apply(Map.Entry<T, Long> entry) {
////                        return entry.getKey();
////                    }
////                })
////                .collect(Collectors.toList());
//        // FIXME: 10/3/2017 remove java 8
//        return null;
//    }
//
//    //----------------------------------------------------------------------------------------------
//
//    /**
//     * Merge another Frequency object's counts into this instance.
//     * This Frequency's counts will be incremented (or set when not already set)
//     * by the counts represented by other.
//     *
//     * @param other the other {@link Frequency} object to be merged
//     * @throws NullArgumentException if {@code other} is null
//     */
//    public void merge(final Frequency<? extends T> other) throws NullArgumentException {
//        MathUtils.checkNotNull(other);
//
//        Iterator<? extends Map.Entry<? extends T, Long>> iter = other.entrySetIterator();
//        while (iter.hasNext()) {
//            final Map.Entry<? extends T, Long> entry = iter.next();
//            incrementValue(entry.getKey(), entry.getValue().longValue());
//        }
//    }
//
//    /**
//     * Merge a {@link Collection} of {@link Frequency} objects into this instance.
//     * This Frequency's counts will be incremented (or set when not already set)
//     * by the counts represented by each of the others.
//     *
//     * @param others the other {@link Frequency} objects to be merged
//     * @throws NullArgumentException if the collection is null
//     */
//    public void merge(final Collection<? extends Frequency<? extends T>> others)
//            throws NullArgumentException {
//        MathUtils.checkNotNull(others);
//
//        for (final Frequency<? extends T> freq : others) {
//            merge(freq);
//        }
//    }
//
//    //----------------------------------------------------------------------------------------------
//
//    /**
//     * Return a string representation of this frequency distribution.
//     *
//     * @return a string representation.
//     */
//    @Override
//    public String toString() {
//        NumberFormat nf = NumberFormat.getPercentInstance();
//        StringBuilder outBuffer = new StringBuilder();
//        outBuffer.append("Value \tFreq. \tPct. \tCum Pct. \n");
//        Iterator<T> iter = freqTable.keySet().iterator();
//        while (iter.hasNext()) {
//            T value = iter.next();
//            outBuffer.append(value);
//            outBuffer.append('\t');
//            outBuffer.append(getCount(value));
//            outBuffer.append('\t');
//            outBuffer.append(nf.format(getPct(value)));
//            outBuffer.append('\t');
//            outBuffer.append(nf.format(getCumPct(value)));
//            outBuffer.append('\n');
//        }
//        return outBuffer.toString();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result +
//                ((freqTable == null) ? 0 : freqTable.hashCode());
//        return result;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof Frequency)) {
//            return false;
//        }
//        Frequency<?> other = (Frequency<?>) obj;
//        return Objects.equals(freqTable, other.freqTable);
//    }
//
//}
