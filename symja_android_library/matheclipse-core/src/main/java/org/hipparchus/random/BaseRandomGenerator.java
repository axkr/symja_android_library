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

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FastMath;

/**
 * Base class with default implementations for common methods.
 */
abstract class BaseRandomGenerator implements RandomGenerator {

    /**
     * Next gaussian.
     */
    private double nextGaussian = Double.NaN;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int seed) {
        setSeed(new int[]{seed});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        setSeed(new int[]{(int) (seed >>> 32), (int) (seed & 0xffffffffL)});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt(int n) throws IllegalArgumentException {
        if (n <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED,
                    n, 0);
        }

        if ((n & -n) == n) {
            return (int) ((n * (long) (nextInt() >>> 1)) >> 31);
        }
        int bits;
        int val;
        do {
            bits = nextInt() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong(long n) {
        if (n <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED,
                    n, 0);
        }

        long bits;
        long val;
        do {
            bits = nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextGaussian() {

        final double random;
        if (Double.isNaN(nextGaussian)) {
            // generate a new pair of gaussian numbers
            final double x = nextDouble();
            final double y = nextDouble();
            final double alpha = 2 * FastMath.PI * x;
            final double r = FastMath.sqrt(-2 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            nextGaussian = r * FastMath.sin(alpha);
        } else {
            // use the second element of the pair already generated
            random = nextGaussian;
            nextGaussian = Double.NaN;
        }

        return random;

    }

    /**
     * Clears the cache used by the default implementation of
     * {@link #nextGaussian}.
     */
    protected void clearCache() {
        nextGaussian = Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getName();
    }

}
