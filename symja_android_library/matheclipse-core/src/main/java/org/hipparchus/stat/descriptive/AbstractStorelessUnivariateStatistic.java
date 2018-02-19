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
import org.hipparchus.util.Precision;

/**
 * Abstract base class for implementations of the
 * {@link StorelessUnivariateStatistic} interface.
 * <p>
 * Provides default {@code hashCode()} and {@code equals(Object)}
 * implementations.
 */
public abstract class AbstractStorelessUnivariateStatistic implements StorelessUnivariateStatistic {

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract StorelessUnivariateStatistic copy();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void clear();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double getResult();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void increment(final double d);

    /**
     * Returns true iff <code>object</code> is the same type of
     * {@link StorelessUnivariateStatistic} (the object's class equals this
     * instance) returning the same values as this for <code>getResult()</code>
     * and <code>getN()</code>.
     *
     * @param object object to test equality against.
     * @return true if object returns the same value as this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        StorelessUnivariateStatistic other = (StorelessUnivariateStatistic) object;
        return Precision.equalsIncludingNaN(other.getResult(), getResult()) &&
                Precision.equalsIncludingNaN(other.getN(), getN());
    }

    /**
     * Returns hash code based on getResult() and getN().
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 31 * (31 + MathUtils.hash(getResult())) + MathUtils.hash(getN());
    }

    @Override
    public String toString() {
        return String.format("%s: result=%f, N=%d",
                getClass().getSimpleName(),
                getResult(),
                getN());
    }
}
