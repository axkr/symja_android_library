/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
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
package org.hipparchus.analysis.differentiation;

import org.hipparchus.Field;
import org.hipparchus.FieldElement;
import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.MathArrays;

import java.util.Arrays;

/**
 * Factory for {@link FieldDerivativeStructure}.
 * <p>This class is a factory for {@link FieldDerivativeStructure} instances.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 *
 * @param <T> the type of the function parameters and value
 * @see FieldDerivativeStructure
 */
public class FDSFactory<T extends RealFieldElement<T>> {

    /**
     * Compiler for the current dimensions.
     */
    private final DSCompiler compiler;

    /**
     * Field the value and parameters of the function belongs to.
     */
    private final Field<T> valueField;

    /**
     * Field the {@link FieldDerivativeStructure} instances belong to.
     */
    private final Field<FieldDerivativeStructure<T>> derivativeField;

    /**
     * Simple constructor.
     *
     * @param valueField field for the function parameters and value
     * @param parameters number of free parameters
     * @param order      derivation order
     */
    public FDSFactory(final Field<T> valueField, final int parameters, final int order) {
        this.compiler = DSCompiler.getCompiler(parameters, order);
        this.valueField = valueField;
        this.derivativeField = new DerivativeField<T>(constant(valueField.getZero()),
                constant(valueField.getOne()));
    }

    /**
     * Get the {@link Field} the value and parameters of the function belongs to.
     *
     * @return {@link Field} the value and parameters of the function belongs to
     */
    public Field<T> getValueField() {
        return valueField;
    }

    /**
     * Get the {@link Field} the {@link FieldDerivativeStructure} instances belong to.
     *
     * @return {@link Field} the {@link FieldDerivativeStructure} instances belong to
     */
    public Field<FieldDerivativeStructure<T>> getDerivativeField() {
        return derivativeField;
    }

    /**
     * Build a {@link FieldDerivativeStructure} representing a constant value.
     *
     * @param value value of the constant
     * @return a {@link FieldDerivativeStructure} representing a constant value
     */
    public FieldDerivativeStructure<T> constant(double value) {
        return constant(valueField.getZero().add(value));
    }

    /**
     * Build a {@link FieldDerivativeStructure} representing a constant value.
     *
     * @param value value of the constant
     * @return a {@link FieldDerivativeStructure} representing a constant value
     */
    public FieldDerivativeStructure<T> constant(final T value) {
        final T[] data = buildArray();
        Arrays.fill(data, valueField.getZero());
        data[0] = value;
        return new FieldDerivativeStructure<>(this, data);
    }

    /**
     * Build a {@link FieldDerivativeStructure} representing a variable.
     * <p>Instances built using this method are considered
     * to be the free variables with respect to which differentials
     * are computed. As such, their differential with respect to
     * themselves is +1.</p>
     *
     * @param index index of the variable (from 0 to
     *              {@link #getCompiler()}.{@link DSCompiler#getFreeParameters() getFreeParameters()} - 1)
     * @param value value of the variable
     * @return a {@link FieldDerivativeStructure} representing a variable
     * @throws MathIllegalArgumentException if index if greater or
     *                                      equal to {@link #getCompiler()}.{@link DSCompiler#getFreeParameters() getFreeParameters()}.
     */
    public FieldDerivativeStructure<T> variable(final int index, final T value)
            throws MathIllegalArgumentException {

        if (index >= getCompiler().getFreeParameters()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED,
                    index, getCompiler().getFreeParameters());
        }

        final T[] data = buildArray();
        Arrays.fill(data, valueField.getZero());
        data[0] = value;
        if (getCompiler().getOrder() > 0) {
            // the derivative of the variable with respect to itself is 1.
            data[DSCompiler.getCompiler(index, getCompiler().getOrder()).getSize()] = valueField.getOne();
        }

        return new FieldDerivativeStructure<>(this, data);

    }

    /**
     * Build a {@link FieldDerivativeStructure} representing a variable.
     * <p>Instances built using this method are considered
     * to be the free variables with respect to which differentials
     * are computed. As such, their differential with respect to
     * themselves is +1.</p>
     *
     * @param index index of the variable (from 0 to
     *              {@link #getCompiler()}.{@link DSCompiler#getFreeParameters() getFreeParameters()} - 1)
     * @param value value of the variable
     * @return a {@link FieldDerivativeStructure} representing a variable
     * @throws MathIllegalArgumentException if index if greater or
     *                                      equal to {@link #getCompiler()}.{@link DSCompiler#getFreeParameters() getFreeParameters()}.
     */
    public FieldDerivativeStructure<T> variable(final int index, final double value)
            throws MathIllegalArgumentException {

        if (index >= getCompiler().getFreeParameters()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED,
                    index, getCompiler().getFreeParameters());
        }

        final T[] data = buildArray();
        Arrays.fill(data, valueField.getZero());
        data[0] = valueField.getZero().add(value);
        if (getCompiler().getOrder() > 0) {
            // the derivative of the variable with respect to itself is 1.
            data[DSCompiler.getCompiler(index, getCompiler().getOrder()).getSize()] = valueField.getOne();
        }

        return new FieldDerivativeStructure<>(this, data);

    }

    /**
     * Build a {@link FieldDerivativeStructure} from all its derivatives.
     *
     * @param derivatives derivatives sorted according to
     *                    {@link DSCompiler#getPartialDerivativeIndex(int...)}
     * @return {@link FieldDerivativeStructure} with specified derivatives
     * @throws MathIllegalArgumentException if derivatives array does not match the
     *                                      {@link DSCompiler#getSize() size} expected by the compiler
     * @throws MathIllegalArgumentException if order is too large
     * @see FieldDerivativeStructure#getAllDerivatives()
     */
    @SafeVarargs
    public final FieldDerivativeStructure<T> build(final T... derivatives)
            throws MathIllegalArgumentException {

        final T[] data = buildArray();
        if (derivatives.length != data.length) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    derivatives.length, data.length);
        }
        System.arraycopy(derivatives, 0, data, 0, data.length);

        return new FieldDerivativeStructure<>(this, data);

    }

    /**
     * Build a {@link FieldDerivativeStructure} from all its derivatives.
     *
     * @param derivatives derivatives sorted according to
     *                    {@link DSCompiler#getPartialDerivativeIndex(int...)}
     * @return {@link FieldDerivativeStructure} with specified derivatives
     * @throws MathIllegalArgumentException if derivatives array does not match the
     *                                      {@link DSCompiler#getSize() size} expected by the compiler
     * @throws MathIllegalArgumentException if order is too large
     * @see FieldDerivativeStructure#getAllDerivatives()
     */
    public FieldDerivativeStructure<T> build(final double... derivatives)
            throws MathIllegalArgumentException {

        final T[] data = buildArray();
        if (derivatives.length != data.length) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    derivatives.length, data.length);
        }
        for (int i = 0; i < data.length; ++i) {
            data[i] = valueField.getZero().add(derivatives[i]);
        }

        return new FieldDerivativeStructure<>(this, data);

    }

    /**
     * Build a {@link FieldDerivativeStructure} with an uninitialized array.
     * <p>This method is intended only for FieldDerivativeStructure internal use.</p>
     *
     * @return a {@link FieldDerivativeStructure} with an uninitialized array
     */
    FieldDerivativeStructure<T> build() {
        return new FieldDerivativeStructure<>(this, buildArray());
    }

    /**
     * Build an uninitialized array for derivatives data.
     *
     * @return uninitialized array for derivatives data
     */
    private T[] buildArray() {
        return MathArrays.buildArray(valueField, compiler.getSize());
    }

    /**
     * Get the compiler for the current dimensions.
     *
     * @return compiler for the current dimensions
     */
    public DSCompiler getCompiler() {
        return compiler;
    }

    /**
     * Check rules set compatibility.
     *
     * @param factory other factory field to check against instance
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     */
    void checkCompatibility(final FDSFactory<T> factory) throws MathIllegalArgumentException {
        compiler.checkCompatibility(factory.compiler);
    }

    /**
     * Field for {link FieldDerivativeStructure} instances.
     *
     * @param <T> the type of the function parameters and value
     */
    private static class DerivativeField<T extends RealFieldElement<T>> implements Field<FieldDerivativeStructure<T>> {

        /**
         * Constant function evaluating to 0.0.
         */
        private final FieldDerivativeStructure<T> zero;

        /**
         * Constant function evaluating to 1.0.
         */
        private final FieldDerivativeStructure<T> one;

        /**
         * Simple constructor.
         *
         * @param zero constant function evaluating to 0.0
         * @param one  constant function evaluating to 1.0
         */
        DerivativeField(final FieldDerivativeStructure<T> zero,
                        final FieldDerivativeStructure<T> one) {
            this.zero = zero;
            this.one = one;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FieldDerivativeStructure<T> getZero() {
            return zero;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FieldDerivativeStructure<T> getOne() {
            return one;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends FieldElement<FieldDerivativeStructure<T>>> getRuntimeClass() {
            return (Class<? extends FieldElement<FieldDerivativeStructure<T>>>) zero.getClass();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof DerivativeField) {
                FDSFactory<T> lhsFactory = zero.getFactory();
                FDSFactory<?> rhsFactory = ((DerivativeField<?>) other).zero.getFactory();
                return lhsFactory.compiler == rhsFactory.compiler &&
                        lhsFactory.valueField.equals(rhsFactory.valueField);
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final DSCompiler compiler = zero.getFactory().getCompiler();
            return 0x58d35de8 ^ (compiler.getFreeParameters() << 16 & compiler.getOrder());
        }

    }

}
