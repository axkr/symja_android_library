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
package org.hipparchus.random;

import org.hipparchus.distribution.EnumeratedDistribution;
import org.hipparchus.distribution.IntegerDistribution;
import org.hipparchus.distribution.RealDistribution;
import org.hipparchus.distribution.continuous.BetaDistribution;
import org.hipparchus.distribution.continuous.EnumeratedRealDistribution;
import org.hipparchus.distribution.continuous.ExponentialDistribution;
import org.hipparchus.distribution.continuous.GammaDistribution;
import org.hipparchus.distribution.continuous.LogNormalDistribution;
import org.hipparchus.distribution.continuous.NormalDistribution;
import org.hipparchus.distribution.continuous.UniformRealDistribution;
import org.hipparchus.distribution.discrete.EnumeratedIntegerDistribution;
import org.hipparchus.distribution.discrete.PoissonDistribution;
import org.hipparchus.distribution.discrete.UniformIntegerDistribution;
import org.hipparchus.distribution.discrete.ZipfDistribution;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.CombinatoricsUtils;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;
import org.hipparchus.util.Pair;
import org.hipparchus.util.Precision;
import org.hipparchus.util.ResizableDoubleArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for generating random data.
 */
public class RandomDataGenerator extends ForwardingRandomGenerator
        implements RandomGenerator, Serializable {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160529L;

    /**
     * Used when generating Exponential samples.
     * Table containing the constants
     * q_i = sum_{j=1}^i (ln 2)^j/j! = ln 2 + (ln 2)^2/2 + ... + (ln 2)^i/i!
     * until the largest representable fraction below 1 is exceeded.
     * <p>
     * Note that
     * 1 = 2 - 1 = exp(ln 2) - 1 = sum_{n=1}^infty (ln 2)^n / n!
     * thus q_i -> 1 as i -> +inf,
     * so the higher i, the closer to one we get (the series is not alternating).
     * <p>
     * By trying, n = 16 in Java is enough to reach 1.0.
     */
    private static final double[] EXPONENTIAL_SA_QI;

    /**
     * Map of <classname, switch constant> for continuous distributions
     */
    private static final Map<Class<? extends RealDistribution>, RealDistributionSampler> CONTINUOUS_SAMPLERS = new HashMap<>();
    /**
     * Map of <classname, switch constant> for discrete distributions
     */
    private static final Map<Class<? extends IntegerDistribution>, IntegerDistributionSampler> DISCRETE_SAMPLERS = new HashMap<>();
    /**
     * The default sampler for continuous distributions using the inversion technique.
     */
    private static final RealDistributionSampler DEFAULT_REAL_SAMPLER =
            new RealDistributionSampler() {
                @Override
                public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                    return dist.inverseCumulativeProbability(generator.nextDouble());
                }
            };
    /**
     * The default sampler for discrete distributions using the inversion technique.
     */
    private static final IntegerDistributionSampler DEFAULT_INTEGER_SAMPLER =
            new IntegerDistributionSampler() {
                @Override
                public int nextSample(RandomDataGenerator generator, IntegerDistribution dist) {
                    return dist.inverseCumulativeProbability(generator.nextDouble());
                }
            };

    /**
     * Initialize tables.
     */
    static {
        /**
         * Filling EXPONENTIAL_SA_QI table.
         * Note that we don't want qi = 0 in the table.
         */
        final double LN2 = FastMath.log(2);
        double qi = 0;
        int i = 1;

        /**
         * ArithmeticUtils provides factorials up to 20, so let's use that
         * limit together with Precision.EPSILON to generate the following
         * code (a priori, we know that there will be 16 elements, but it is
         * better to not hardcode it).
         */
        final ResizableDoubleArray ra = new ResizableDoubleArray(20);

        while (qi < 1) {
            qi += FastMath.pow(LN2, i) / CombinatoricsUtils.factorial(i);
            ra.addElement(qi);
            ++i;
        }

        EXPONENTIAL_SA_QI = ra.getElements();

        // Continuous samplers

        CONTINUOUS_SAMPLERS.put(BetaDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        BetaDistribution beta = (BetaDistribution) dist;
                        return generator.nextBeta(beta.getAlpha(), beta.getBeta());
                    }
                });

        CONTINUOUS_SAMPLERS.put(ExponentialDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        return generator.nextExponential(dist.getNumericalMean());
                    }
                });

        CONTINUOUS_SAMPLERS.put(GammaDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        GammaDistribution gamma = (GammaDistribution) dist;
                        return generator.nextGamma(gamma.getShape(), gamma.getScale());
                    }
                });

        CONTINUOUS_SAMPLERS.put(NormalDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        NormalDistribution normal = (NormalDistribution) dist;
                        return generator.nextNormal(normal.getMean(),
                                normal.getStandardDeviation());
                    }
                });

        CONTINUOUS_SAMPLERS.put(LogNormalDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        LogNormalDistribution logNormal = (LogNormalDistribution) dist;
                        return generator.nextLogNormal(logNormal.getShape(),
                                logNormal.getScale());
                    }
                });

        CONTINUOUS_SAMPLERS.put(UniformRealDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        return generator.nextUniform(dist.getSupportLowerBound(),
                                dist.getSupportUpperBound());
                    }
                });

        CONTINUOUS_SAMPLERS.put(EnumeratedRealDistribution.class,
                new RealDistributionSampler() {
                    @Override
                    public double nextSample(RandomDataGenerator generator, RealDistribution dist) {
                        final EnumeratedRealDistribution edist =
                                (EnumeratedRealDistribution) dist;
                        EnumeratedDistributionSampler<Double> sampler =
                                generator.new EnumeratedDistributionSampler<Double>(edist.getPmf());
                        return sampler.sample();
                    }
                });

        // Discrete samplers

        DISCRETE_SAMPLERS.put(PoissonDistribution.class,
                new IntegerDistributionSampler() {
                    @Override
                    public int nextSample(RandomDataGenerator generator, IntegerDistribution dist) {
                        return generator.nextPoisson(dist.getNumericalMean());
                    }
                });

        DISCRETE_SAMPLERS.put(UniformIntegerDistribution.class,
                new IntegerDistributionSampler() {
                    @Override
                    public int nextSample(RandomDataGenerator generator, IntegerDistribution dist) {
                        return generator.nextInt(dist.getSupportLowerBound(),
                                dist.getSupportUpperBound());
                    }
                });
        DISCRETE_SAMPLERS.put(ZipfDistribution.class,
                new IntegerDistributionSampler() {
                    @Override
                    public int nextSample(RandomDataGenerator generator, IntegerDistribution dist) {
                        ZipfDistribution zipfDist = (ZipfDistribution) dist;
                        return generator.nextZipf(zipfDist.getNumberOfElements(),
                                zipfDist.getExponent());
                    }
                });

        DISCRETE_SAMPLERS.put(EnumeratedIntegerDistribution.class,
                new IntegerDistributionSampler() {
                    @Override
                    public int nextSample(RandomDataGenerator generator, IntegerDistribution dist) {
                        final EnumeratedIntegerDistribution edist =
                                (EnumeratedIntegerDistribution) dist;
                        EnumeratedDistributionSampler<Integer> sampler =
                                generator.new EnumeratedDistributionSampler<Integer>(edist.getPmf());
                        return sampler.sample();
                    }
                });
    }

    /**
     * Source of random data
     */
    private final RandomGenerator randomGenerator;
    /**
     * The sampler to be used for the nextZipF method
     */
    private transient ZipfRejectionInversionSampler zipfSampler;

    /**
     * Construct a RandomDataGenerator with a default RandomGenerator as its source of random data.
     */
    public RandomDataGenerator() {
        this(new Well19937c());
    }

    /**
     * Construct a RandomDataGenerator with a default RandomGenerator as its source of random data, initialized
     * with the given seed value.
     *
     * @param seed seed value
     */
    public RandomDataGenerator(long seed) {
        this(new Well19937c(seed));
    }

    /**
     * Construct a RandomDataGenerator using the given RandomGenerator as its source of random data.
     *
     * @param randomGenerator the underlying PRNG
     * @throws MathIllegalArgumentException if randomGenerator is null
     */
    private RandomDataGenerator(RandomGenerator randomGenerator) {
        MathUtils.checkNotNull(randomGenerator);
        this.randomGenerator = randomGenerator;
    }

    /**
     * Factory method to create a {@code RandomData} instance using the supplied
     * {@code RandomGenerator}.
     *
     * @param randomGenerator source of random bits
     * @return a RandomData using the given RandomGenerator to source bits
     * @throws MathIllegalArgumentException if randomGenerator is null
     */
    public static RandomDataGenerator of(RandomGenerator randomGenerator) {
        return new RandomDataGenerator(randomGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RandomGenerator delegate() {
        return randomGenerator;
    }

    /**
     * Returns the next pseudo-random beta-distributed value with the given
     * shape and scale parameters.
     *
     * @param alpha First shape parameter (must be positive).
     * @param beta  Second shape parameter (must be positive).
     * @return beta-distributed random deviate
     */
    public double nextBeta(double alpha, double beta) {
        return ChengBetaSampler.sample(randomGenerator, alpha, beta);
    }

    /**
     * Returns the next pseudo-random, exponentially distributed deviate.
     *
     * @param mean mean of the exponential distribution
     * @return exponentially distributed deviate about the given mean
     */
    public double nextExponential(double mean) {
        if (mean <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.MEAN, mean);
        }
        // Step 1:
        double a = 0;
        double u = randomGenerator.nextDouble();

        // Step 2 and 3:
        while (u < 0.5) {
            a += EXPONENTIAL_SA_QI[0];
            u *= 2;
        }

        // Step 4 (now u >= 0.5):
        u += u - 1;

        // Step 5:
        if (u <= EXPONENTIAL_SA_QI[0]) {
            return mean * (a + u);
        }

        // Step 6:
        int i = 0; // Should be 1, be we iterate before it in while using 0
        double u2 = randomGenerator.nextDouble();
        double umin = u2;

        // Step 7 and 8:
        do {
            ++i;
            u2 = randomGenerator.nextDouble();

            if (u2 < umin) {
                umin = u2;
            }

            // Step 8:
        } while (u > EXPONENTIAL_SA_QI[i]); // Ensured to exit since EXPONENTIAL_SA_QI[MAX] = 1

        return mean * (a + umin * EXPONENTIAL_SA_QI[0]);
    }

    /**
     * Returns the next pseudo-random gamma-distributed value with the given shape and scale parameters.
     *
     * @param shape shape parameter of the distribution
     * @param scale scale parameter of the distribution
     * @return gamma-distributed random deviate
     */
    public double nextGamma(double shape, double scale) {
        if (shape < 1) {
            // [1]: p. 228, Algorithm GS

            while (true) {
                // Step 1:
                final double u = randomGenerator.nextDouble();
                final double bGS = 1 + shape / FastMath.E;
                final double p = bGS * u;

                if (p <= 1) {
                    // Step 2:

                    final double x = FastMath.pow(p, 1 / shape);
                    final double u2 = randomGenerator.nextDouble();

                    if (u2 > FastMath.exp(-x)) {
                        // Reject
                        continue;
                    } else {
                        return scale * x;
                    }
                } else {
                    // Step 3:

                    final double x = -1 * FastMath.log((bGS - p) / shape);
                    final double u2 = randomGenerator.nextDouble();

                    if (u2 > FastMath.pow(x, shape - 1)) {
                        // Reject
                        continue;
                    } else {
                        return scale * x;
                    }
                }
            }
        }

        // Now shape >= 1

        final double d = shape - 0.333333333333333333;
        final double c = 1 / (3 * FastMath.sqrt(d));

        while (true) {
            final double x = randomGenerator.nextGaussian();
            final double v = (1 + c * x) * (1 + c * x) * (1 + c * x);

            if (v <= 0) {
                continue;
            }

            final double x2 = x * x;
            final double u = randomGenerator.nextDouble();

            // Squeeze
            if (u < 1 - 0.0331 * x2 * x2) {
                return scale * d * v;
            }

            if (FastMath.log(u) < 0.5 * x2 + d * (1 - v + FastMath.log(v))) {
                return scale * d * v;
            }
        }
    }

    /**
     * Returns the next normally-distributed pseudo-random deviate.
     *
     * @param mean              mean of the normal distribution
     * @param standardDeviation standard deviation of the normal distribution
     * @return a random value, normally distributed with the given mean and standard deviation
     */
    public double nextNormal(double mean, double standardDeviation) {
        if (standardDeviation <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, standardDeviation, 0);
        }
        return standardDeviation * nextGaussian() + mean;
    }

    /**
     * Returns the next log-normally-distributed pseudo-random deviate.
     *
     * @param shape shape parameter of the log-normal distribution
     * @param scale scale parameter of the log-normal distribution
     * @return a random value, normally distributed with the given mean and standard deviation
     */
    public double nextLogNormal(double shape, double scale) {
        if (shape <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, shape, 0);
        }
        return FastMath.exp(scale + shape * nextGaussian());
    }

    /**
     * Returns a poisson-distributed deviate with the given mean.
     *
     * @param mean expected value
     * @return poisson deviate
     * @throws MathIllegalArgumentException if mean is not strictly positive
     */
    public int nextPoisson(double mean) {
        if (mean <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, mean, 0);
        }
        final double pivot = 40.0d;
        if (mean < pivot) {
            double p = FastMath.exp(-mean);
            long n = 0;
            double r = 1.0d;
            double rnd = 1.0d;

            while (n < 1000 * mean) {
                rnd = randomGenerator.nextDouble();
                r *= rnd;
                if (r >= p) {
                    n++;
                } else {
                    return (int) FastMath.min(n, Integer.MAX_VALUE);
                }
            }
            return (int) FastMath.min(n, Integer.MAX_VALUE);
        } else {
            final double lambda = FastMath.floor(mean);
            final double lambdaFractional = mean - lambda;
            final double logLambda = FastMath.log(lambda);
            final double logLambdaFactorial = CombinatoricsUtils.factorialLog((int) lambda);
            final long y2 = lambdaFractional < Double.MIN_VALUE ? 0 : nextPoisson(lambdaFractional);
            final double delta = FastMath.sqrt(lambda * FastMath.log(32 * lambda / FastMath.PI + 1));
            final double halfDelta = delta / 2;
            final double twolpd = 2 * lambda + delta;
            final double a1 = FastMath.sqrt(FastMath.PI * twolpd) * FastMath.exp(1 / (8 * lambda));
            final double a2 = (twolpd / delta) * FastMath.exp(-delta * (1 + delta) / twolpd);
            final double aSum = a1 + a2 + 1;
            final double p1 = a1 / aSum;
            final double p2 = a2 / aSum;
            final double c1 = 1 / (8 * lambda);

            double x = 0;
            double y = 0;
            double v = 0;
            int a = 0;
            double t = 0;
            double qr = 0;
            double qa = 0;
            for (; ; ) {
                final double u = randomGenerator.nextDouble();
                if (u <= p1) {
                    final double n = randomGenerator.nextGaussian();
                    x = n * FastMath.sqrt(lambda + halfDelta) - 0.5d;
                    if (x > delta || x < -lambda) {
                        continue;
                    }
                    y = x < 0 ? FastMath.floor(x) : FastMath.ceil(x);
                    final double e = nextExponential(1);
                    v = -e - (n * n / 2) + c1;
                } else {
                    if (u > p1 + p2) {
                        y = lambda;
                        break;
                    } else {
                        x = delta + (twolpd / delta) * nextExponential(1);
                        y = FastMath.ceil(x);
                        v = -nextExponential(1) - delta * (x + 1) / twolpd;
                    }
                }
                a = x < 0 ? 1 : 0;
                t = y * (y + 1) / (2 * lambda);
                if (v < -t && a == 0) {
                    y = lambda + y;
                    break;
                }
                qr = t * ((2 * y + 1) / (6 * lambda) - 1);
                qa = qr - (t * t) / (3 * (lambda + a * (y + 1)));
                if (v < qa) {
                    y = lambda + y;
                    break;
                }
                if (v > qr) {
                    continue;
                }
                if (v < y * logLambda - CombinatoricsUtils.factorialLog((int) (y + lambda)) + logLambdaFactorial) {
                    y = lambda + y;
                    break;
                }
            }
            return (int) FastMath.min(y2 + (long) y, Integer.MAX_VALUE);
        }
    }

    /**
     * Returns a random deviate from the given distribution.
     *
     * @param dist the distribution to sample from
     * @return a random value following the given distribution
     */
    public double nextDeviate(RealDistribution dist) {
        return getSampler(dist).nextSample(this, dist);
    }

    /**
     * Returns an array of random deviates from the given distribution.
     *
     * @param dist the distribution to sample from
     * @param size the number of values to return
     * @return an array of {@code size} values following the given distribution
     */
    public double[] nextDeviates(RealDistribution dist, int size) {
        //TODO: check parameters

        RealDistributionSampler sampler = getSampler(dist);
        double[] out = new double[size];
        for (int i = 0; i < size; i++) {
            out[i] = sampler.nextSample(this, dist);
        }
        return out;
    }

    /**
     * Returns a random deviate from the given distribution.
     *
     * @param dist the distribution to sample from
     * @return a random value following the given distribution
     */
    public int nextDeviate(IntegerDistribution dist) {
        return getSampler(dist).nextSample(this, dist);
    }

    /**
     * Returns an array of random deviates from the given distribution.
     *
     * @param dist the distribution to sample from
     * @param size the number of values to return
     * @return an array of {@code size }values following the given distribution
     */
    public int[] nextDeviates(IntegerDistribution dist, int size) {
        //TODO: check parameters

        IntegerDistributionSampler sampler = getSampler(dist);
        int[] out = new int[size];
        for (int i = 0; i < size; i++) {
            out[i] = sampler.nextSample(this, dist);
        }
        return out;
    }

    /**
     * Returns a sampler for the given continuous distribution.
     *
     * @param dist the distribution
     * @return a sampler for the distribution
     */
    private RealDistributionSampler getSampler(RealDistribution dist) {
        RealDistributionSampler sampler = CONTINUOUS_SAMPLERS.get(dist.getClass());
        if (sampler != null) {
            return sampler;
        }
        return DEFAULT_REAL_SAMPLER;
    }

    /**
     * Returns a sampler for the given discrete distribution.
     *
     * @param dist the distribution
     * @return a sampler for the distribution
     */
    private IntegerDistributionSampler getSampler(IntegerDistribution dist) {
        IntegerDistributionSampler sampler = DISCRETE_SAMPLERS.get(dist.getClass());
        if (sampler != null) {
            return sampler;
        }
        return DEFAULT_INTEGER_SAMPLER;
    }

    /**
     * Returns a uniformly distributed random integer between lower and upper (inclusive).
     *
     * @param lower lower bound for the generated value
     * @param upper upper bound for the generated value
     * @return a random integer value within the given bounds
     * @throws MathIllegalArgumentException if lower is not strictly less than or equal to upper
     */
    public int nextInt(int lower, int upper) {
        if (lower >= upper) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                    lower, upper);
        }
        final int max = (upper - lower) + 1;
        if (max <= 0) {
            // The range is too wide to fit in a positive int (larger
            // than 2^31); as it covers more than half the integer range,
            // we use a simple rejection method.
            while (true) {
                final int r = nextInt();
                if (r >= lower &&
                        r <= upper) {
                    return r;
                }
            }
        } else {
            // We can shift the range and directly generate a positive int.
            return lower + nextInt(max);
        }
    }

    /**
     * Returns a uniformly distributed random long integer between lower and upper (inclusive).
     *
     * @param lower lower bound for the generated value
     * @param upper upper bound for the generated value
     * @return a random long integer value within the given bounds
     * @throws MathIllegalArgumentException if lower is not strictly less than or equal to upper
     */
    public long nextLong(final long lower, final long upper) throws MathIllegalArgumentException {
        if (lower >= upper) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                    lower, upper);
        }
        final long max = (upper - lower) + 1;
        if (max <= 0) {
            // the range is too wide to fit in a positive long (larger than 2^63); as it covers
            // more than half the long range, we use directly a simple rejection method
            while (true) {
                final long r = randomGenerator.nextLong();
                if (r >= lower && r <= upper) {
                    return r;
                }
            }
        } else if (max < Integer.MAX_VALUE) {
            // we can shift the range and generate directly a positive int
            return lower + randomGenerator.nextInt((int) max);
        } else {
            // we can shift the range and generate directly a positive long
            return lower + nextLong(max);
        }
    }

    /**
     * Returns a double value uniformly distributed over [lower, upper]
     *
     * @param lower lower bound
     * @param upper upper bound
     * @return uniform deviate
     * @throws MathIllegalArgumentException if upper is less than or equal to upper
     */
    public double nextUniform(double lower, double upper) {
        if (upper <= lower) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper);
        }
        if (Double.isInfinite(lower) || Double.isInfinite(upper)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INFINITE_BOUND);
        }
        if (Double.isNaN(lower) || Double.isNaN(upper)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NAN_NOT_ALLOWED);
        }
        final double u = randomGenerator.nextDouble();
        return u * upper + (1 - u) * lower;
    }

    /**
     * Returns an integer value following a Zipf distribution with the given parameter.
     *
     * @param numberOfElements number of elements of the distribution
     * @param exponent         exponent of the distribution
     * @return random Zipf value
     */
    public int nextZipf(int numberOfElements, double exponent) {
        if (zipfSampler == null || zipfSampler.getExponent() != exponent || zipfSampler.getNumberOfElements() != numberOfElements) {
            zipfSampler = new ZipfRejectionInversionSampler(numberOfElements, exponent);
        }
        return zipfSampler.sample(randomGenerator);
    }

    /**
     * Generates a random string of hex characters of length {@code len}.
     * <p>
     * The generated string will be random, but not cryptographically secure.
     * <p>
     * <strong>Algorithm Description:</strong> hex strings are generated using a
     * 2-step process.
     * <ol>
     * <li>{@code len / 2 + 1} binary bytes are generated using the underlying
     * Random</li>
     * <li>Each binary byte is translated into 2 hex digits</li>
     * </ol>
     * </p>
     *
     * @param len the desired string length.
     * @return the random string.
     * @throws MathIllegalArgumentException if {@code len <= 0}.
     */
    public String nextHexString(int len) throws MathIllegalArgumentException {
        if (len <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.LENGTH, len);
        }

        // Initialize output buffer
        StringBuilder outBuffer = new StringBuilder();

        // Get int(len/2)+1 random bytes
        byte[] randomBytes = new byte[(len / 2) + 1];
        randomGenerator.nextBytes(randomBytes);

        // Convert each byte to 2 hex digits
        for (int i = 0; i < randomBytes.length; i++) {
            Integer c = Integer.valueOf(randomBytes[i]);

            /*
             * Add 128 to byte value to make interval 0-255 before doing hex
             * conversion. This guarantees <= 2 hex digits from toHexString()
             * toHexString would otherwise add 2^32 to negative arguments.
             */
            String hex = Integer.toHexString(c.intValue() + 128);

            // Make sure we add 2 hex digits for each byte
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            outBuffer.append(hex);
        }
        return outBuffer.toString().substring(0, len);
    }

    /**
     * Generates an integer array of length {@code k} whose entries are selected
     * randomly, without repetition, from the integers {@code 0, ..., n - 1}
     * (inclusive).
     * <p>
     * Generated arrays represent permutations of {@code n} taken {@code k} at a
     * time.</p>
     * This method calls {@link MathArrays#shuffle(int[], RandomGenerator)
     * MathArrays.shuffle} in order to create a random shuffle of the set
     * of natural numbers {@code { 0, 1, ..., n - 1 }}.
     *
     * @param n the domain of the permutation
     * @param k the size of the permutation
     * @return a random {@code k}-permutation of {@code n}, as an array of
     * integers
     * @throws MathIllegalArgumentException if {@code k > n}.
     * @throws MathIllegalArgumentException if {@code k <= 0}.
     */
    public int[] nextPermutation(int n, int k)
            throws MathIllegalArgumentException {
        if (k > n) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.PERMUTATION_EXCEEDS_N,
                    k, n, true);
        }
        if (k <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.PERMUTATION_SIZE,
                    k);
        }

        final int[] index = MathArrays.natural(n);
        MathArrays.shuffle(index, randomGenerator);

        // Return a new array containing the first "k" entries of "index".
        return Arrays.copyOf(index, k);
    }

    /**
     * Returns an array of {@code k} objects selected randomly from the
     * Collection {@code c}.
     * <p>
     * Sampling from {@code c} is without replacement; but if {@code c} contains
     * identical objects, the sample may include repeats.  If all elements of
     * {@code c} are distinct, the resulting object array represents a
     * <a href="http://rkb.home.cern.ch/rkb/AN16pp/node250.html#SECTION0002500000000000000000">
     * Simple Random Sample</a> of size {@code k} from the elements of
     * {@code c}.</p>
     * <p>This method calls {@link #nextPermutation(int, int) nextPermutation(c.size(), k)}
     * in order to sample the collection.
     * </p>
     *
     * @param c the collection to be sampled
     * @param k the size of the sample
     * @return a random sample of {@code k} elements from {@code c}
     * @throws MathIllegalArgumentException if {@code k > c.size()}.
     * @throws MathIllegalArgumentException if {@code k <= 0}.
     */
    public Object[] nextSample(Collection<?> c, int k) throws MathIllegalArgumentException {

        int len = c.size();
        if (k > len) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE,
                    k, len, true);
        }
        if (k <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_SAMPLES, k);
        }

        Object[] objects = c.toArray();
        int[] index = nextPermutation(len, k);
        Object[] result = new Object[k];
        for (int i = 0; i < k; i++) {
            result[i] = objects[index[i]];
        }
        return result;
    }

    /**
     * Returns an array of {@code k} double values selected randomly from the
     * double array {@code a}.
     * <p>
     * Sampling from {@code a} is without replacement; but if {@code a} contains
     * identical objects, the sample may include repeats.  If all elements of
     * {@code a} are distinct, the resulting object array represents a
     * <a href="http://rkb.home.cern.ch/rkb/AN16pp/node250.html#SECTION0002500000000000000000">
     * Simple Random Sample</a> of size {@code k} from the elements of
     * {@code a}.</p>
     *
     * @param a the array to be sampled
     * @param k the size of the sample
     * @return a random sample of {@code k} elements from {@code a}
     * @throws MathIllegalArgumentException if {@code k > c.size()}.
     * @throws MathIllegalArgumentException if {@code k <= 0}.
     */
    public double[] nextSample(double[] a, int k) throws MathIllegalArgumentException {
        int len = a.length;
        if (k > len) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE,
                    k, len, true);
        }
        if (k <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_SAMPLES, k);
        }
        int[] index = nextPermutation(len, k);
        double[] result = new double[k];
        for (int i = 0; i < k; i++) {
            result[i] = a[index[i]];
        }
        return result;
    }

    /**
     * Generates a random sample of size sampleSize from {0, 1, ... , weights.length - 1},
     * using weights as probabilities.
     * <p>
     * For 0 < i < weights.length, the probability that i is selected (on any draw) is weights[i].
     * If necessary, the weights array is normalized to sum to 1 so that weights[i] is a probability
     * and the array sums to 1.
     * <p>
     * Weights can be 0, but must not be negative, infinite or NaN.
     * At least one weight must be positive.
     *
     * @param sampleSize size of sample to generate
     * @param weights    probability sampling weights
     * @return an array of integers between 0 and weights.length - 1
     * @throws MathIllegalArgumentException if weights contains negative, NaN or infinite values or only 0s or sampleSize is less than 0
     */
    public int[] nextSampleWithReplacement(int sampleSize, double[] weights) {

        // Check sample size
        if (sampleSize < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NOT_POSITIVE_NUMBER_OF_SAMPLES);
        }

        // Check and normalize weights
        double[] normWt = EnumeratedDistribution.checkAndNormalize(weights);

        // Generate sample values by dividing [0,1] into subintervals corresponding to weights.
        final int[] out = new int[sampleSize];
        final int len = normWt.length;
        for (int i = 0; i < sampleSize; i++) {
            final double u = randomGenerator.nextDouble();
            double cum = normWt[0];
            int j = 1;
            while (cum < u && j < len) {
                cum += normWt[j++];
            }
            out[i] = --j;
        }
        return out;
    }

    /**
     * Interface for samplers of continuous distributions.
     */
    @FunctionalInterface
    private interface RealDistributionSampler {
        /**
         * Return the next sample following the given distribution.
         *
         * @param generator    the random data generator to use
         * @param distribution the distribution to use
         * @return the next sample
         */
        double nextSample(RandomDataGenerator generator, RealDistribution distribution);
    }

    /**
     * Interface for samplers of discrete distributions.
     */
    @FunctionalInterface
    private interface IntegerDistributionSampler {
        /**
         * Return the next sample following the given distribution.
         *
         * @param generator    the random data generator to use
         * @param distribution the distribution to use
         * @return the next sample
         */
        int nextSample(RandomDataGenerator generator, IntegerDistribution distribution);
    }

    /**
     * Utility class implementing Cheng's algorithms for beta distribution sampling.
     * <p>
     * <blockquote>
     * <pre>
     * R. C. H. Cheng,
     * "Generating beta variates with nonintegral shape parameters",
     * Communications of the ACM, 21, 317-322, 1978.
     * </pre>
     * </blockquote>
     */
    private static class ChengBetaSampler {

        /**
         * Returns the next sample following a beta distribution
         * with given alpha and beta parameters.
         *
         * @param generator the random generator to use
         * @param alpha     the alpha parameter
         * @param beta      the beta parameter
         * @return the next sample
         */
        public static double sample(RandomGenerator generator,
                                    double alpha,
                                    double beta) {
            // TODO: validate parameters
            final double a = FastMath.min(alpha, beta);
            final double b = FastMath.max(alpha, beta);

            if (a > 1) {
                return algorithmBB(generator, alpha, a, b);
            } else {
                return algorithmBC(generator, alpha, b, a);
            }
        }

        /**
         * Returns one Beta sample using Cheng's BB algorithm,
         * when both &alpha; and &beta; are greater than 1.
         *
         * @param generator the random generator to use
         * @param a0        distribution first shape parameter (&alpha;)
         * @param a         min(&alpha;, &beta;) where &alpha;, &beta; are the two distribution shape parameters
         * @param b         max(&alpha;, &beta;) where &alpha;, &beta; are the two distribution shape parameters
         * @return sampled value
         */
        private static double algorithmBB(final RandomGenerator generator,
                                          final double a0,
                                          final double a,
                                          final double b) {
            final double alpha = a + b;
            final double beta = FastMath.sqrt((alpha - 2.) / (2. * a * b - alpha));
            final double gamma = a + 1. / beta;

            double r;
            double w;
            double t;
            do {
                final double u1 = generator.nextDouble();
                final double u2 = generator.nextDouble();
                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                final double z = u1 * u1 * u2;
                r = gamma * v - 1.3862944;
                final double s = a + r - w;
                if (s + 2.609438 >= 5 * z) {
                    break;
                }

                t = FastMath.log(z);
                if (s >= t) {
                    break;
                }
            } while (r + alpha * (FastMath.log(alpha) - FastMath.log(b + w)) < t);

            w = FastMath.min(w, Double.MAX_VALUE);
            return Precision.equals(a, a0) ? w / (b + w) : b / (b + w);
        }

        /**
         * Returns a Beta-distribute value using Cheng's BC algorithm,
         * when at least one of &alpha; and &beta; is smaller than 1.
         *
         * @param generator the random generator to use
         * @param a0        distribution first shape parameter (&alpha;)
         * @param a         max(&alpha;, &beta;) where &alpha;, &beta; are the two distribution shape parameters
         * @param b         min(&alpha;, &beta;) where &alpha;, &beta; are the two distribution shape parameters
         * @return sampled value
         */
        private static double algorithmBC(final RandomGenerator generator,
                                          final double a0,
                                          final double a,
                                          final double b) {
            final double alpha = a + b;
            final double beta = 1. / b;
            final double delta = 1. + a - b;
            final double k1 = delta * (0.0138889 + 0.0416667 * b) / (a * beta - 0.777778);
            final double k2 = 0.25 + (0.5 + 0.25 / delta) * b;

            double w;
            for (; ; ) {
                final double u1 = generator.nextDouble();
                final double u2 = generator.nextDouble();
                final double y = u1 * u2;
                final double z = u1 * y;
                if (u1 < 0.5) {
                    if (0.25 * u2 + z - y >= k1) {
                        continue;
                    }
                } else {
                    if (z <= 0.25) {
                        final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                        w = a * FastMath.exp(v);
                        break;
                    }

                    if (z >= k2) {
                        continue;
                    }
                }

                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                if (alpha * (FastMath.log(alpha) - FastMath.log(b + w) + v) - 1.3862944 >= FastMath.log(z)) {
                    break;
                }
            }

            w = FastMath.min(w, Double.MAX_VALUE);
            return Precision.equals(a, a0) ? w / (b + w) : b / (b + w);
        }
    }

    /**
     * Utility class implementing a rejection inversion sampling method for a discrete,
     * bounded Zipf distribution that is based on the method described in
     * <p>
     * Wolfgang Hörmann and Gerhard Derflinger
     * "Rejection-inversion to generate variates from monotone discrete distributions."
     * ACM Transactions on Modeling and Computer Simulation (TOMACS) 6.3 (1996): 169-184.
     * <p>
     * The paper describes an algorithm for exponents larger than 1 (Algorithm ZRI).
     * The original method uses {@code H(x) := (v + x)^(1 - q) / (1 - q)}
     * as the integral of the hat function. This function is undefined for
     * q = 1, which is the reason for the limitation of the exponent.
     * If instead the integral function
     * {@code H(x) := ((v + x)^(1 - q) - 1) / (1 - q)} is used,
     * for which a meaningful limit exists for q = 1,
     * the method works for all positive exponents.
     * <p>
     * The following implementation uses v := 0 and generates integral numbers
     * in the range [1, numberOfElements]. This is different to the original method
     * where v is defined to be positive and numbers are taken from [0, i_max].
     * This explains why the implementation looks slightly different.
     */
    static final class ZipfRejectionInversionSampler {

        /**
         * Exponent parameter of the distribution.
         */
        private final double exponent;
        /**
         * Number of elements.
         */
        private final int numberOfElements;
        /**
         * Constant equal to {@code hIntegral(1.5) - 1}.
         */
        private final double hIntegralX1;
        /**
         * Constant equal to {@code hIntegral(numberOfElements + 0.5)}.
         */
        private final double hIntegralNumberOfElements;
        /**
         * Constant equal to {@code 2 - hIntegralInverse(hIntegral(2.5) - h(2)}.
         */
        private final double s;

        /**
         * Simple constructor.
         *
         * @param numberOfElements number of elements
         * @param exponent         exponent parameter of the distribution
         */
        ZipfRejectionInversionSampler(final int numberOfElements, final double exponent) {
            this.exponent = exponent;
            this.numberOfElements = numberOfElements;
            this.hIntegralX1 = hIntegral(1.5) - 1d;
            this.hIntegralNumberOfElements = hIntegral(numberOfElements + 0.5);
            this.s = 2d - hIntegralInverse(hIntegral(2.5) - h(2));
        }

        /**
         * Helper function that calculates {@code log(1+x)/x}.
         * <p>
         * A Taylor series expansion is used, if x is close to 0.
         *
         * @param x a value larger than or equal to -1
         * @return {@code log(1+x)/x}
         */
        static double helper1(final double x) {
            if (FastMath.abs(x) > 1e-8) {
                return FastMath.log1p(x) / x;
            } else {
                return 1. - x * ((1. / 2.) - x * ((1. / 3.) - x * (1. / 4.)));
            }
        }

        /**
         * Helper function to calculate {@code (exp(x)-1)/x}.
         * <p>
         * A Taylor series expansion is used, if x is close to 0.
         *
         * @param x free parameter
         * @return {@code (exp(x)-1)/x} if x is non-zero, or 1 if x=0
         */
        static double helper2(final double x) {
            if (FastMath.abs(x) > 1e-8) {
                return FastMath.expm1(x) / x;
            } else {
                return 1. + x * (1. / 2.) * (1. + x * (1. / 3.) * (1. + x * (1. / 4.)));
            }
        }

        /**
         * Generate one integral number in the range [1, numberOfElements].
         *
         * @param random random generator to use
         * @return generated integral number in the range [1, numberOfElements]
         */
        int sample(final RandomGenerator random) {
            while (true) {

                final double u = hIntegralNumberOfElements + random.nextDouble() * (hIntegralX1 - hIntegralNumberOfElements);
                // u is uniformly distributed in (hIntegralX1, hIntegralNumberOfElements]

                double x = hIntegralInverse(u);

                int k = (int) (x + 0.5);

                // Limit k to the range [1, numberOfElements]
                // (k could be outside due to numerical inaccuracies)
                if (k < 1) {
                    k = 1;
                } else if (k > numberOfElements) {
                    k = numberOfElements;
                }

                // Here, the distribution of k is given by:
                //
                //   P(k = 1) = C * (hIntegral(1.5) - hIntegralX1) = C
                //   P(k = m) = C * (hIntegral(m + 1/2) - hIntegral(m - 1/2)) for m >= 2
                //
                //   where C := 1 / (hIntegralNumberOfElements - hIntegralX1)

                if (k - x <= s || u >= hIntegral(k + 0.5) - h(k)) {

                    // Case k = 1:
                    //
                    //   The right inequality is always true, because replacing k by 1 gives
                    //   u >= hIntegral(1.5) - h(1) = hIntegralX1 and u is taken from
                    //   (hIntegralX1, hIntegralNumberOfElements].
                    //
                    //   Therefore, the acceptance rate for k = 1 is P(accepted | k = 1) = 1
                    //   and the probability that 1 is returned as random value is
                    //   P(k = 1 and accepted) = P(accepted | k = 1) * P(k = 1) = C = C / 1^exponent
                    //
                    // Case k >= 2:
                    //
                    //   The left inequality (k - x <= s) is just a short cut
                    //   to avoid the more expensive evaluation of the right inequality
                    //   (u >= hIntegral(k + 0.5) - h(k)) in many cases.
                    //
                    //   If the left inequality is true, the right inequality is also true:
                    //     Theorem 2 in the paper is valid for all positive exponents, because
                    //     the requirements h'(x) = -exponent/x^(exponent + 1) < 0 and
                    //     (-1/hInverse'(x))'' = (1+1/exponent) * x^(1/exponent-1) >= 0
                    //     are both fulfilled.
                    //     Therefore, f(x) := x - hIntegralInverse(hIntegral(x + 0.5) - h(x))
                    //     is a non-decreasing function. If k - x <= s holds,
                    //     k - x <= s + f(k) - f(2) is obviously also true which is equivalent to
                    //     -x <= -hIntegralInverse(hIntegral(k + 0.5) - h(k)),
                    //     -hIntegralInverse(u) <= -hIntegralInverse(hIntegral(k + 0.5) - h(k)),
                    //     and finally u >= hIntegral(k + 0.5) - h(k).
                    //
                    //   Hence, the right inequality determines the acceptance rate:
                    //   P(accepted | k = m) = h(m) / (hIntegrated(m+1/2) - hIntegrated(m-1/2))
                    //   The probability that m is returned is given by
                    //   P(k = m and accepted) = P(accepted | k = m) * P(k = m) = C * h(m) = C / m^exponent.
                    //
                    // In both cases the probabilities are proportional to the probability mass function
                    // of the Zipf distribution.

                    return k;
                }
            }
        }

        /**
         * {@code H(x) :=}
         * <ul>
         * <li>{@code (x^(1-exponent) - 1)/(1 - exponent)}, if {@code exponent != 1}</li>
         * <li>{@code log(x)}, if {@code exponent == 1}</li>
         * </ul>
         * H(x) is an integral function of h(x),
         * the derivative of H(x) is h(x).
         *
         * @param x free parameter
         * @return {@code H(x)}
         */
        private double hIntegral(final double x) {
            final double logX = FastMath.log(x);
            return helper2((1d - exponent) * logX) * logX;
        }

        /**
         * {@code h(x) := 1/x^exponent}
         *
         * @param x free parameter
         * @return h(x)
         */
        private double h(final double x) {
            return FastMath.exp(-exponent * FastMath.log(x));
        }

        /**
         * The inverse function of H(x).
         *
         * @param x free parameter
         * @return y for which {@code H(y) = x}
         */
        private double hIntegralInverse(final double x) {
            double t = x * (1d - exponent);
            if (t < -1d) {
                // Limit value to the range [-1, +inf).
                // t could be smaller than -1 in some rare cases due to numerical errors.
                t = -1;
            }
            return FastMath.exp(helper1(t) * x);
        }

        /**
         * @return the exponent of the distribution being sampled
         */
        public double getExponent() {
            return exponent;
        }

        /**
         * @return the number of elements of the distribution being sampled
         */
        public int getNumberOfElements() {
            return numberOfElements;
        }
    }

    /**
     * Sampler for enumerated distributions.
     *
     * @param <T> type of sample space objects
     */
    private final class EnumeratedDistributionSampler<T> {
        /**
         * Probabilities
         */
        private final double[] weights;
        /**
         * Values
         */
        private final List<T> values;

        /**
         * Create an EnumeratedDistributionSampler from the provided pmf.
         *
         * @param pmf probability mass function describing the distribution
         */
        EnumeratedDistributionSampler(List<Pair<T, Double>> pmf) {
            final int numMasses = pmf.size();
            weights = new double[numMasses];
            values = new ArrayList<T>();
            for (int i = 0; i < numMasses; i++) {
                weights[i] = pmf.get(i).getSecond();
                values.add(pmf.get(i).getFirst());
            }
        }

        /**
         * @return a random value from the distribution
         */
        public T sample() {
            int[] chosen = nextSampleWithReplacement(1, weights);
            return values.get(chosen[0]);
        }
    }
}
