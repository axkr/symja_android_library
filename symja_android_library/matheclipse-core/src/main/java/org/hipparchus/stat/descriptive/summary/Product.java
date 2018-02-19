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
package org.hipparchus.stat.descriptive.summary;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.hipparchus.stat.descriptive.AggregatableStatistic;
import org.hipparchus.stat.descriptive.WeightedEvaluation;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Returns the product of the available values.
 * <p>
 * If there are no values in the dataset, then 1 is returned.
 * If any of the values are
 * <code>NaN</code>, then <code>NaN</code> is returned.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class Product extends AbstractStorelessUnivariateStatistic
        implements AggregatableStatistic<Product>, WeightedEvaluation, Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * The number of values that have been added
     */
    private long n;

    /**
     * The current Running Product
     */
    private double value;

    /**
     * Create a Product instance.
     */
    public Product() {
        n = 0;
        value = 1;
    }

    /**
     * Copy constructor, creates a new {@code Product} identical
     * to the {@code original}.
     *
     * @param original the {@code Product} instance to copy
     * @throws NullArgumentException if original is null
     */
    public Product(Product original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.n = original.n;
        this.value = original.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(final double d) {
        value *= d;
        n++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getResult() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getN() {
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        value = 1;
        n = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregate(Product other) {
        MathUtils.checkNotNull(other);
        if (other.n > 0) {
            this.n += other.n;
            this.value *= other.value;
        }
    }

    /**
     * Returns the product of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     *
     * @param values the input array
     * @param begin  index of the first array element to include
     * @param length the number of elements to include
     * @return the product of the values or 1 if length = 0
     * @throws MathIllegalArgumentException if the array is null or the array index
     *                                      parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {
        double product = Double.NaN;
        if (MathArrays.verifyValues(values, begin, length, true)) {
            product = 1.0;
            for (int i = begin; i < begin + length; i++) {
                product *= values[i];
            }
        }
        return product;
    }

    /**
     * Returns the weighted product of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     * <li>the weights array is null</li>
     * <li>the weights array does not have the same length as the values array</li>
     * <li>the weights array contains one or more infinite values</li>
     * <li>the weights array contains one or more NaN values</li>
     * <li>the weights array contains negative values</li>
     * <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     * <p>
     * Uses the formula,
     * <pre>
     *    weighted product = &prod;values[i]<sup>weights[i]</sup>
     * </pre>
     * <p>
     * that is, the weights are applied as exponents when computing the weighted product.
     *
     * @param values  the input array
     * @param weights the weights array
     * @param begin   index of the first array element to include
     * @param length  the number of elements to include
     * @return the product of the values or 1 if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) throws MathIllegalArgumentException {
        double product = Double.NaN;
        if (MathArrays.verifyValues(values, weights, begin, length, true)) {
            product = 1.0;
            for (int i = begin; i < begin + length; i++) {
                product *= FastMath.pow(values[i], weights[i]);
            }
        }
        return product;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product copy() {
        return new Product(this);
    }

}
