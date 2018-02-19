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


import org.hipparchus.util.MathUtils;

import java.util.Arrays;

/**
 * Reporting interface for basic univariate statistics.
 */
public interface StatisticalSummary {

    /**
     * Computes aggregated statistical summaries.
     * <p>
     * This method can be used to combine statistics computed over partitions or
     * subsamples - i.e., the returned StatisticalSummary should contain
     * the same values that would have been obtained by computing a single
     * StatisticalSummary over the combined dataset.
     *
     * @param statistics StatisticalSummary instances to aggregate
     * @return summary statistics for the combined dataset
     * @throws org.hipparchus.exception.NullArgumentException if the input is null
     */
    static StatisticalSummary aggregate(StatisticalSummary... statistics) {
        MathUtils.checkNotNull(statistics);
        return aggregate(Arrays.asList(statistics));
    }

    /**
     * Computes aggregated statistical summaries.
     * <p>
     * This method can be used to combine statistics computed over partitions or
     * subsamples - i.e., the returned StatisticalSummary should contain
     * the same values that would have been obtained by computing a single
     * StatisticalSummary over the combined dataset.
     *
     * @param statistics iterable of StatisticalSummary instances to aggregate
     * @return summary statistics for the combined dataset
     * @throws org.hipparchus.exception.NullArgumentException if the input is null
     */
    static StatisticalSummary aggregate(Iterable<? extends StatisticalSummary> statistics) {
        MathUtils.checkNotNull(statistics);

        long n = 0;
        double min = Double.NaN;
        double max = Double.NaN;
        double sum = Double.NaN;
        double mean = Double.NaN;
        double m2 = Double.NaN;

        for (StatisticalSummary current : statistics) {
            if (current.getN() == 0) {
                continue;
            }

            if (n == 0) {
                n = current.getN();
                min = current.getMin();
                sum = current.getSum();
                max = current.getMax();
                m2 = current.getVariance() * (n - 1);
                mean = current.getMean();
            } else {
                if (current.getMin() < min) {
                    min = current.getMin();
                }
                if (current.getMax() > max) {
                    max = current.getMax();
                }

                sum += current.getSum();
                final double oldN = n;
                final double curN = current.getN();
                n += curN;
                final double meanDiff = current.getMean() - mean;
                mean = sum / n;
                final double curM2 = current.getVariance() * (curN - 1d);
                m2 = m2 + curM2 + meanDiff * meanDiff * oldN * curN / n;
            }
        }

        final double variance = n == 0 ? Double.NaN :
                n == 1 ? 0d :
                        m2 / (n - 1);

        return new StatisticalSummaryValues(mean, variance, n, max, min, sum);
    }

    /**
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values
     *
     * @return The mean or Double.NaN if no values have been added.
     */
    double getMean();

    /**
     * Returns the variance of the available values.
     *
     * @return The variance, Double.NaN if no values have been added
     * or 0.0 for a single value set.
     */
    double getVariance();

    /**
     * Returns the standard deviation of the available values.
     *
     * @return The standard deviation, Double.NaN if no values have been added
     * or 0.0 for a single value set.
     */
    double getStandardDeviation();

    /**
     * Returns the maximum of the available values
     *
     * @return The max or Double.NaN if no values have been added.
     */
    double getMax();

    /**
     * Returns the minimum of the available values
     *
     * @return The min or Double.NaN if no values have been added.
     */
    double getMin();

    /**
     * Returns the number of available values
     *
     * @return The number of available values
     */
    long getN();

    /**
     * Returns the sum of the values that have been added to Univariate.
     *
     * @return The sum or Double.NaN if no values have been added
     */
    double getSum();

}
