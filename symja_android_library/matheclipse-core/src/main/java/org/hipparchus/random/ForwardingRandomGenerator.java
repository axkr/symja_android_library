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

/**
 * A random generator which forwards all its method calls to another random generator.
 * <p>
 * Subclasses should override one or more methods to modify the behavior of the backing
 * random generator as desired per the decorator pattern.
 */
abstract class ForwardingRandomGenerator implements RandomGenerator {

    /**
     * Returns the backing delegate instance that methods are forwarded to.
     * <p>
     * Concrete subclasses override this method to supply the instance being decorated.
     *
     * @return the delegate instance
     */
    protected abstract RandomGenerator delegate();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int seed) {
        delegate().setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int[] seed) {
        delegate().setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        delegate().setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextBytes(byte[] bytes) {
        delegate().nextBytes(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextBytes(byte[] bytes, int offset, int length) {
        delegate().nextBytes(bytes, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt() {
        return delegate().nextInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt(int n) {
        return delegate().nextInt(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong() {
        return delegate().nextLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong(long n) {
        return delegate().nextLong(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean nextBoolean() {
        return delegate().nextBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float nextFloat() {
        return delegate().nextFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextDouble() {
        return delegate().nextDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextGaussian() {
        return delegate().nextGaussian();
    }
}
