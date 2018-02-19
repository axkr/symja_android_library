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

package org.hipparchus.fraction;

import org.hipparchus.Field;
import org.hipparchus.FieldElement;

import java.io.Serializable;

/**
 * Representation of the fractional numbers  without any overflow field.
 * <p>
 * This class is a singleton.
 * </p>
 *
 * @see Fraction
 */
public class BigFractionField implements Field<BigFraction>, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = -1699294557189741703L;

    /**
     * Private constructor for the singleton.
     */
    private BigFractionField() {
    }

    /**
     * Get the unique instance.
     *
     * @return the unique instance
     */
    public static BigFractionField getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigFraction getOne() {
        return BigFraction.ONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigFraction getZero() {
        return BigFraction.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends FieldElement<BigFraction>> getRuntimeClass() {
        return BigFraction.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        return this == other;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 0x7666e832;
    }

    // CHECKSTYLE: stop HideUtilityClassConstructor

    /**
     * Handle deserialization of the singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        // return the singleton instance
        return LazyHolder.INSTANCE;
    }
    // CHECKSTYLE: resume HideUtilityClassConstructor

    /**
     * Holder for the instance.
     * <p>We use here the Initialization On Demand Holder Idiom.</p>
     */
    private static class LazyHolder {
        /**
         * Cached field instance.
         */
        private static final BigFractionField INSTANCE = new BigFractionField();
    }

}
