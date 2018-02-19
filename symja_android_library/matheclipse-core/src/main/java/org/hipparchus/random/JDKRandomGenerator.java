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
import org.hipparchus.util.MathUtils;

import java.io.Serializable;
import java.util.Random;

/**
 * A {@link RandomGenerator} adapter that delegates the random number
 * generation to the standard {@link Random} class.
 */
public class JDKRandomGenerator extends IntRandomGenerator implements Serializable {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20151227L;

    /**
     * JDK's RNG.
     */
    private final Random delegate;

    /**
     * Creates an instance with an arbitrary seed.
     */
    public JDKRandomGenerator() {
        delegate = new Random();
    }

    /**
     * Creates an instance with the given seed.
     *
     * @param seed Initial seed.
     */
    public JDKRandomGenerator(long seed) {
        delegate = new Random(seed);
    }

    /**
     * Creates an instance that wraps the given {@link Random} instance.
     *
     * @param random JDK {@link Random} instance that will generate the
     *               the random data.
     * @throws MathIllegalArgumentException if random is null
     */
    public JDKRandomGenerator(Random random) {
        MathUtils.checkNotNull(random);
        delegate = random;
    }

    /**
     * Converts seed from one representation to another.
     *
     * @param seed Original seed.
     * @return the converted seed.
     */
    private static long convertToLong(int[] seed) {
        // The following number is the largest prime that fits
        // in 32 bits (i.e. 2^32 - 5).
        final long prime = 4294967291l;

        long combined = 0l;
        for (int s : seed) {
            combined = combined * prime + s;
        }

        return combined;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int seed) {
        delegate.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        delegate.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int[] seed) {
        delegate.setSeed(convertToLong(seed));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextBytes(byte[] bytes) {
        delegate.nextBytes(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt() {
        return delegate.nextInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong() {
        return delegate.nextLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean nextBoolean() {
        return delegate.nextBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float nextFloat() {
        return delegate.nextFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextDouble() {
        return delegate.nextDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextGaussian() {
        return delegate.nextGaussian();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt(int n) {
        try {
            return delegate.nextInt(n);
        } catch (IllegalArgumentException e) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED,
                    n, 0);
        }
    }

}
