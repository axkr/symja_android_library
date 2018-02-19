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
package org.hipparchus.stat.inference;

import org.hipparchus.distribution.RealDistribution;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.StatisticalSummary;

import java.util.Collection;

/**
 * A collection of static methods to create inference test instances or to
 * perform inference tests.
 */
public class InferenceTestUtils {

    /**
     * Singleton TTest instance.
     */
    private static final TTest T_TEST = new TTest();

    /**
     * Singleton ChiSquareTest instance.
     */
    private static final ChiSquareTest CHI_SQUARE_TEST = new ChiSquareTest();

    /**
     * Singleton OneWayAnova instance.
     */
    private static final OneWayAnova ONE_WAY_ANANOVA = new OneWayAnova();

    /**
     * Singleton G-Test instance.
     */
    private static final GTest G_TEST = new GTest();

    /**
     * Singleton K-S test instance
     */
    private static final KolmogorovSmirnovTest KS_TEST = new KolmogorovSmirnovTest();

    /**
     * Prevent instantiation.
     */
    private InferenceTestUtils() {
        super();
    }

    // CHECKSTYLE: stop JavadocMethodCheck

    /**
     * @see TTest#homoscedasticT(double[], double[])
     */
    public static double homoscedasticT(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.homoscedasticT(sample1, sample2);
    }

    /**
     * @see TTest#homoscedasticT(StatisticalSummary, StatisticalSummary)
     */
    public static double homoscedasticT(final StatisticalSummary sampleStats1,
                                        final StatisticalSummary sampleStats2)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.homoscedasticT(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#homoscedasticTTest(double[], double[], double)
     */
    public static boolean homoscedasticTTest(final double[] sample1, final double[] sample2,
                                             final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.homoscedasticTTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#homoscedasticTTest(double[], double[])
     */
    public static double homoscedasticTTest(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.homoscedasticTTest(sample1, sample2);
    }

    /**
     * @see TTest#homoscedasticTTest(StatisticalSummary, StatisticalSummary)
     */
    public static double homoscedasticTTest(final StatisticalSummary sampleStats1,
                                            final StatisticalSummary sampleStats2)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.homoscedasticTTest(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#pairedT(double[], double[])
     */
    public static double pairedT(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.pairedT(sample1, sample2);
    }

    /**
     * @see TTest#pairedTTest(double[], double[], double)
     */
    public static boolean pairedTTest(final double[] sample1, final double[] sample2,
                                      final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.pairedTTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#pairedTTest(double[], double[])
     */
    public static double pairedTTest(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.pairedTTest(sample1, sample2);
    }

    /**
     * @see TTest#t(double, double[])
     */
    public static double t(final double mu, final double[] observed)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.t(mu, observed);
    }

    /**
     * @see TTest#t(double, StatisticalSummary)
     */
    public static double t(final double mu, final StatisticalSummary sampleStats)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.t(mu, sampleStats);
    }

    /**
     * @see TTest#t(double[], double[])
     */
    public static double t(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.t(sample1, sample2);
    }

    /**
     * @see TTest#t(StatisticalSummary, StatisticalSummary)
     */
    public static double t(final StatisticalSummary sampleStats1,
                           final StatisticalSummary sampleStats2)
            throws MathIllegalArgumentException, NullArgumentException {
        return T_TEST.t(sampleStats1, sampleStats2);
    }

    /**
     * @see TTest#tTest(double, double[], double)
     */
    public static boolean tTest(final double mu, final double[] sample, final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.tTest(mu, sample, alpha);
    }

    /**
     * @see TTest#tTest(double, double[])
     */
    public static double tTest(final double mu, final double[] sample)
            throws MathIllegalArgumentException, NullArgumentException,
            MathIllegalStateException {
        return T_TEST.tTest(mu, sample);
    }

    /**
     * @see TTest#tTest(double, StatisticalSummary, double)
     */
    public static boolean tTest(final double mu, final StatisticalSummary sampleStats,
                                final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.tTest(mu, sampleStats, alpha);
    }

    /**
     * @see TTest#tTest(double, StatisticalSummary)
     */
    public static double tTest(final double mu, final StatisticalSummary sampleStats)
            throws MathIllegalArgumentException, NullArgumentException,
            MathIllegalStateException {
        return T_TEST.tTest(mu, sampleStats);
    }

    /**
     * @see TTest#tTest(double[], double[], double)
     */
    public static boolean tTest(final double[] sample1, final double[] sample2,
                                final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.tTest(sample1, sample2, alpha);
    }

    /**
     * @see TTest#tTest(double[], double[])
     */
    public static double tTest(final double[] sample1, final double[] sample2)
            throws MathIllegalArgumentException, NullArgumentException,
            MathIllegalStateException {
        return T_TEST.tTest(sample1, sample2);
    }

    /**
     * @see TTest#tTest(StatisticalSummary, StatisticalSummary, double)
     */
    public static boolean tTest(final StatisticalSummary sampleStats1,
                                final StatisticalSummary sampleStats2,
                                final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return T_TEST.tTest(sampleStats1, sampleStats2, alpha);
    }

    /**
     * @see TTest#tTest(StatisticalSummary, StatisticalSummary)
     */
    public static double tTest(final StatisticalSummary sampleStats1,
                               final StatisticalSummary sampleStats2)
            throws MathIllegalArgumentException, NullArgumentException,
            MathIllegalStateException {
        return T_TEST.tTest(sampleStats1, sampleStats2);
    }

    /**
     * @see ChiSquareTest#chiSquare(double[], long[])
     */
    public static double chiSquare(final double[] expected, final long[] observed)
            throws MathIllegalArgumentException {
        return CHI_SQUARE_TEST.chiSquare(expected, observed);
    }

    /**
     * @see ChiSquareTest#chiSquare(long[][])
     */
    public static double chiSquare(final long[][] counts)
            throws MathIllegalArgumentException, NullArgumentException {
        return CHI_SQUARE_TEST.chiSquare(counts);
    }

    /**
     * @see ChiSquareTest#chiSquareTest(double[], long[], double)
     */
    public static boolean chiSquareTest(final double[] expected, final long[] observed,
                                        final double alpha)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed, alpha);
    }

    /**
     * @see ChiSquareTest#chiSquareTest(double[], long[])
     */
    public static double chiSquareTest(final double[] expected, final long[] observed)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed);
    }

    /**
     * @see ChiSquareTest#chiSquareTest(long[][], double)
     */
    public static boolean chiSquareTest(final long[][] counts, final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTest(counts, alpha);
    }

    /**
     * @see ChiSquareTest#chiSquareTest(long[][])
     */
    public static double chiSquareTest(final long[][] counts)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTest(counts);
    }

    /**
     * @see ChiSquareTest#chiSquareDataSetsComparison(long[], long[])
     */
    public static double chiSquareDataSetsComparison(final long[] observed1,
                                                     final long[] observed2)
            throws MathIllegalArgumentException {
        return CHI_SQUARE_TEST.chiSquareDataSetsComparison(observed1, observed2);
    }

    /**
     * @see ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[])
     */
    public static double chiSquareTestDataSetsComparison(final long[] observed1,
                                                         final long[] observed2)
            throws MathIllegalArgumentException,
            MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @see ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[], double)
     */
    public static boolean chiSquareTestDataSetsComparison(final long[] observed1,
                                                          final long[] observed2,
                                                          final double alpha)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @see OneWayAnova#anovaFValue(Collection)
     */
    public static double oneWayAnovaFValue(final Collection<double[]> categoryData)
            throws MathIllegalArgumentException, NullArgumentException {
        return ONE_WAY_ANANOVA.anovaFValue(categoryData);
    }

    /**
     * @see OneWayAnova#anovaPValue(Collection)
     */
    public static double oneWayAnovaPValue(final Collection<double[]> categoryData)
            throws MathIllegalArgumentException, NullArgumentException,
            MathIllegalStateException {
        return ONE_WAY_ANANOVA.anovaPValue(categoryData);
    }

    /**
     * @see OneWayAnova#anovaTest(Collection, double)
     */
    public static boolean oneWayAnovaTest(final Collection<double[]> categoryData,
                                          final double alpha)
            throws MathIllegalArgumentException, NullArgumentException, MathIllegalStateException {
        return ONE_WAY_ANANOVA.anovaTest(categoryData, alpha);
    }

    /**
     * @see GTest#g(double[], long[])
     */
    public static double g(final double[] expected, final long[] observed)
            throws MathIllegalArgumentException {
        return G_TEST.g(expected, observed);
    }

    /**
     * @see GTest#gTest(double[], long[])
     */
    public static double gTest(final double[] expected, final long[] observed)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return G_TEST.gTest(expected, observed);
    }

    /**
     * @see GTest#gTestIntrinsic(double[], long[])
     */
    public static double gTestIntrinsic(final double[] expected, final long[] observed)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return G_TEST.gTestIntrinsic(expected, observed);
    }

    /**
     * @see GTest#gTest(double[], long[], double)
     */
    public static boolean gTest(final double[] expected, final long[] observed,
                                final double alpha)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return G_TEST.gTest(expected, observed, alpha);
    }

    /**
     * @see GTest#gDataSetsComparison(long[], long[])
     */
    public static double gDataSetsComparison(final long[] observed1,
                                             final long[] observed2)
            throws MathIllegalArgumentException {
        return G_TEST.gDataSetsComparison(observed1, observed2);
    }

    /**
     * @see GTest#rootLogLikelihoodRatio(long, long, long, long)
     */
    public static double rootLogLikelihoodRatio(final long k11, final long k12, final long k21, final long k22)
            throws MathIllegalArgumentException {
        return G_TEST.rootLogLikelihoodRatio(k11, k12, k21, k22);
    }


    /**
     * @see GTest#gTestDataSetsComparison(long[], long[])
     */
    public static double gTestDataSetsComparison(final long[] observed1,
                                                 final long[] observed2)
            throws MathIllegalArgumentException,
            MathIllegalStateException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @see GTest#gTestDataSetsComparison(long[], long[], double)
     */
    public static boolean gTestDataSetsComparison(final long[] observed1,
                                                  final long[] observed2,
                                                  final double alpha)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovStatistic(RealDistribution, double[])
     */
    public static double kolmogorovSmirnovStatistic(RealDistribution dist, double[] data)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovStatistic(dist, data);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovTest(RealDistribution, double[])
     */
    public static double kolmogorovSmirnovTest(RealDistribution dist, double[] data)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovTest(RealDistribution, double[], boolean)
     */
    public static double kolmogorovSmirnovTest(RealDistribution dist, double[] data, boolean strict)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data, strict);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovTest(RealDistribution, double[], double)
     */
    public static boolean kolmogorovSmirnovTest(RealDistribution dist, double[] data, double alpha)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data, alpha);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovStatistic(double[], double[])
     */
    public static double kolmogorovSmirnovStatistic(double[] x, double[] y)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovStatistic(x, y);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovTest(double[], double[])
     */
    public static double kolmogorovSmirnovTest(double[] x, double[] y)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(x, y);
    }

    /**
     * @see KolmogorovSmirnovTest#kolmogorovSmirnovTest(double[], double[], boolean)
     */
    public static double kolmogorovSmirnovTest(double[] x, double[] y, boolean strict)
            throws MathIllegalArgumentException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(x, y, strict);
    }

    /**
     * @see KolmogorovSmirnovTest#exactP(double, int, int, boolean)
     */
    public static double exactP(double d, int m, int n, boolean strict) {
        return KS_TEST.exactP(d, n, m, strict);
    }

    /**
     * @see KolmogorovSmirnovTest#approximateP(double, int, int)
     */
    public static double approximateP(double d, int n, int m) {
        return KS_TEST.approximateP(d, n, m);
    }

    // CHECKSTYLE: resume JavadocMethodCheck

}
