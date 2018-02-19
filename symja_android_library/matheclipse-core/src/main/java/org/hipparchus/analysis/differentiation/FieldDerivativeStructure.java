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
import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

/**
 * Class representing both the value and the differentials of a function.
 * <p>This class is similar to {@link DerivativeStructure} except function
 * parameters and value can be any {@link RealFieldElement}.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 *
 * @param <T> the type of the field elements
 * @see DerivativeStructure
 * @see FDSFactory
 * @see DSCompiler
 */
public class FieldDerivativeStructure<T extends RealFieldElement<T>>
        implements RealFieldElement<FieldDerivativeStructure<T>> {

    /**
     * Factory that built the instance.
     */
    private final FDSFactory<T> factory;

    /**
     * Combined array holding all values.
     */
    private final T[] data;

    /**
     * Build an instance with all values and derivatives set to 0.
     *
     * @param factory factory that built the instance
     * @param data    combined array holding all values
     */
    FieldDerivativeStructure(final FDSFactory<T> factory, final T[] data) {
        this.factory = factory;
        this.data = data;
    }

    /**
     * Returns the hypotenuse of a triangle with sides {@code x} and {@code y}
     * - sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * avoiding intermediate overflow or underflow.
     * <p>
     * <ul>
     * <li> If either argument is infinite, then the result is positive infinity.</li>
     * <li> else, if either argument is NaN then the result is NaN.</li>
     * </ul>
     *
     * @param x   a value
     * @param y   a value
     * @param <T> the type of the field elements
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    public static <T extends RealFieldElement<T>> FieldDerivativeStructure<T>
    hypot(final FieldDerivativeStructure<T> x, final FieldDerivativeStructure<T> y)
            throws MathIllegalArgumentException {
        return x.hypot(y);
    }

    /**
     * Compute a<sup>x</sup> where a is a double and x a {@link FieldDerivativeStructure}
     *
     * @param a   number to exponentiate
     * @param x   power to apply
     * @param <T> the type of the field elements
     * @return a<sup>x</sup>
     */
    public static <T extends RealFieldElement<T>> FieldDerivativeStructure<T> pow(final double a, final FieldDerivativeStructure<T> x) {
        final FieldDerivativeStructure<T> result = x.factory.build();
        x.factory.getCompiler().pow(a, x.data, 0, result.data, 0);
        return result;
    }

    /**
     * Two arguments arc tangent operation.
     *
     * @param y   first argument of the arc tangent
     * @param x   second argument of the arc tangent
     * @param <T> the type of the field elements
     * @return atan2(y, x)
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    public static <T extends RealFieldElement<T>> FieldDerivativeStructure<T> atan2(final FieldDerivativeStructure<T> y,
                                                                                    final FieldDerivativeStructure<T> x)
            throws MathIllegalArgumentException {
        return y.atan2(x);
    }

    /**
     * Get the factory that built the instance.
     *
     * @return factory that built the instance
     */
    public FDSFactory<T> getFactory() {
        return factory;
    }

    /**
     * Get the number of free parameters.
     *
     * @return number of free parameters
     */
    public int getFreeParameters() {
        return getFactory().getCompiler().getFreeParameters();
    }

    /**
     * Get the derivation order.
     *
     * @return derivation order
     */
    public int getOrder() {
        return getFactory().getCompiler().getOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getReal() {
        return data[0].getReal();
    }

    /**
     * Get the value part of the derivative structure.
     *
     * @return value part of the derivative structure
     * @see #getPartialDerivative(int...)
     */
    public T getValue() {
        return data[0];
    }

    /**
     * Get a partial derivative.
     *
     * @param orders derivation orders with respect to each variable (if all orders are 0,
     *               the value is returned)
     * @return partial derivative
     * @throws MathIllegalArgumentException if the numbers of variables does not
     *                                      match the instance
     * @throws MathIllegalArgumentException if sum of derivation orders is larger
     *                                      than the instance limits
     * @see #getValue()
     */
    public T getPartialDerivative(final int... orders)
            throws MathIllegalArgumentException {
        return data[factory.getCompiler().getPartialDerivativeIndex(orders)];
    }

    /**
     * Get all partial derivatives.
     *
     * @return a fresh copy of partial derivatives, in an array sorted according to
     * {@link DSCompiler#getPartialDerivativeIndex(int...)}
     */
    public T[] getAllDerivatives() {
        return data.clone();
    }

    /**
     * '+' operator.
     *
     * @param a right hand side parameter of the operator
     * @return this+a
     */
    public FieldDerivativeStructure<T> add(T a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = ds.data[0].add(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> add(final double a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = ds.data[0].add(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> add(final FieldDerivativeStructure<T> a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().add(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /**
     * '-' operator.
     *
     * @param a right hand side parameter of the operator
     * @return this-a
     */
    public FieldDerivativeStructure<T> subtract(final T a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = ds.data[0].subtract(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> subtract(final double a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = ds.data[0].subtract(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> subtract(final FieldDerivativeStructure<T> a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().subtract(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /**
     * '&times;' operator.
     *
     * @param a right hand side parameter of the operator
     * @return this&times;a
     */
    public FieldDerivativeStructure<T> multiply(final T a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].multiply(a);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> multiply(final int n) {
        return multiply((double) n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> multiply(final double a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].multiply(a);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> multiply(final FieldDerivativeStructure<T> a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().multiply(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * '&divide;' operator.
     *
     * @param a right hand side parameter of the operator
     * @return this&divide;a
     */
    public FieldDerivativeStructure<T> divide(final T a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].divide(a);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> divide(final double a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].divide(a);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> divide(final FieldDerivativeStructure<T> a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().divide(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * IEEE remainder operator.
     *
     * @param a right hand side parameter of the operator
     * @return this - n &times; a where n is the closest integer to this/a
     * (the even integer is chosen for n if this/a is halfway between two integers)
     */
    public FieldDerivativeStructure<T> remainder(final T a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = data[0].remainder(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> remainder(final double a) {
        final FieldDerivativeStructure<T> ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = data[0].remainder(a);
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> remainder(final FieldDerivativeStructure<T> a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().remainder(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> negate() {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].negate();
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> abs() {
        if (Double.doubleToLongBits(data[0].getReal()) < 0) {
            // we use the bits representation to also handle -0.0
            return negate();
        } else {
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> ceil() {
        return factory.constant(data[0].ceil());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> floor() {
        return factory.constant(data[0].floor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> rint() {
        return factory.constant(data[0].rint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long round() {
        return data[0].round();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> signum() {
        return factory.constant(data[0].signum());
    }

    /**
     * Returns the instance with the sign of the argument.
     * A NaN {@code sign} argument is treated as positive.
     *
     * @param sign the sign for the returned value
     * @return the instance with the same sign as the {@code sign} argument
     */
    public FieldDerivativeStructure<T> copySign(final T sign) {
        long m = Double.doubleToLongBits(data[0].getReal());
        long s = Double.doubleToLongBits(sign.getReal());
        if ((m >= 0 && s >= 0) || (m < 0 && s < 0)) { // Sign is currently OK
            return this;
        }
        return negate(); // flip sign
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> copySign(final double sign) {
        long m = Double.doubleToLongBits(data[0].getReal());
        long s = Double.doubleToLongBits(sign);
        if ((m >= 0 && s >= 0) || (m < 0 && s < 0)) { // Sign is currently OK
            return this;
        }
        return negate(); // flip sign
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> copySign(final FieldDerivativeStructure<T> sign) {
        long m = Double.doubleToLongBits(data[0].getReal());
        long s = Double.doubleToLongBits(sign.data[0].getReal());
        if ((m >= 0 && s >= 0) || (m < 0 && s < 0)) { // Sign is currently OK
            return this;
        }
        return negate(); // flip sign
    }

    /**
     * Return the exponent of the instance value, removing the bias.
     * <p>
     * For double numbers of the form 2<sup>x</sup>, the unbiased
     * exponent is exactly x.
     * </p>
     *
     * @return exponent for instance in IEEE754 representation, without bias
     */
    public int getExponent() {
        return FastMath.getExponent(data[0].getReal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> scalb(final int n) {
        final FieldDerivativeStructure<T> ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].scalb(n);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> hypot(final FieldDerivativeStructure<T> y)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(y.factory);

        if (Double.isInfinite(data[0].getReal()) || Double.isInfinite(y.data[0].getReal())) {
            return factory.constant(Double.POSITIVE_INFINITY);
        } else if (Double.isNaN(data[0].getReal()) || Double.isNaN(y.data[0].getReal())) {
            return factory.constant(Double.NaN);
        } else {

            final int expX = getExponent();
            final int expY = y.getExponent();
            if (expX > expY + 27) {
                // y is neglectible with respect to x
                return abs();
            } else if (expY > expX + 27) {
                // x is neglectible with respect to y
                return y.abs();
            } else {

                // find an intermediate scale to avoid both overflow and underflow
                final int middleExp = (expX + expY) / 2;

                // scale parameters without losing precision
                final FieldDerivativeStructure<T> scaledX = scalb(-middleExp);
                final FieldDerivativeStructure<T> scaledY = y.scalb(-middleExp);

                // compute scaled hypotenuse
                final FieldDerivativeStructure<T> scaledH =
                        scaledX.multiply(scaledX).add(scaledY.multiply(scaledY)).sqrt();

                // remove scaling
                return scaledH.scalb(middleExp);

            }

        }
    }

    /**
     * Compute composition of the instance by a univariate function.
     *
     * @param f array of value and derivatives of the function at
     *          the current point (i.e. [f({@link #getValue()}),
     *          f'({@link #getValue()}), f''({@link #getValue()})...]).
     * @return f(this)
     * @throws MathIllegalArgumentException if the number of derivatives
     *                                      in the array is not equal to {@link #getOrder() order} + 1
     */
    @SafeVarargs
    public final FieldDerivativeStructure<T> compose(final T... f)
            throws MathIllegalArgumentException {

        MathUtils.checkDimension(f.length, getOrder() + 1);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().compose(data, 0, f, result.data, 0);
        return result;
    }

    /**
     * Compute composition of the instance by a univariate function.
     *
     * @param f array of value and derivatives of the function at
     *          the current point (i.e. [f({@link #getValue()}),
     *          f'({@link #getValue()}), f''({@link #getValue()})...]).
     * @return f(this)
     * @throws MathIllegalArgumentException if the number of derivatives
     *                                      in the array is not equal to {@link #getOrder() order} + 1
     */
    public FieldDerivativeStructure<T> compose(final double... f)
            throws MathIllegalArgumentException {

        MathUtils.checkDimension(f.length, getOrder() + 1);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().compose(data, 0, f, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> reciprocal() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().pow(data, 0, -1, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> sqrt() {
        return rootN(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> cbrt() {
        return rootN(3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> rootN(final int n) {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().rootN(data, 0, n, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<FieldDerivativeStructure<T>> getField() {
        return factory.getDerivativeField();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> pow(final double p) {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().pow(data, 0, p, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> pow(final int n) {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().pow(data, 0, n, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> pow(final FieldDerivativeStructure<T> e)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(e.factory);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().pow(data, 0, e.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> exp() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().exp(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> expm1() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().expm1(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> log() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().log(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> log1p() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().log1p(data, 0, result.data, 0);
        return result;
    }

    /**
     * Base 10 logarithm.
     *
     * @return base 10 logarithm of the instance
     */
    @Override
    public FieldDerivativeStructure<T> log10() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().log10(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> cos() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().cos(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> sin() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().sin(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> tan() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().tan(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> acos() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().acos(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> asin() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().asin(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> atan() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().atan(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> atan2(final FieldDerivativeStructure<T> x)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(x.factory);
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().atan2(data, 0, x.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> cosh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().cosh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> sinh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().sinh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> tanh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().tanh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> acosh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().acosh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> asinh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().asinh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDerivativeStructure<T> atanh() {
        final FieldDerivativeStructure<T> result = factory.build();
        factory.getCompiler().atanh(data, 0, result.data, 0);
        return result;
    }

    /**
     * Convert radians to degrees, with error of less than 0.5 ULP
     *
     * @return instance converted into degrees
     */
    public FieldDerivativeStructure<T> toDegrees() {
        final FieldDerivativeStructure<T> ds = factory.build();
        final double factor = 180 / FastMath.PI;
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].multiply(factor);
        }
        return ds;
    }

    /**
     * Convert degrees to radians, with error of less than 0.5 ULP
     *
     * @return instance converted into radians
     */
    public FieldDerivativeStructure<T> toRadians() {
        final FieldDerivativeStructure<T> ds = factory.build();
        final double factor = FastMath.PI / 180;
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i].multiply(factor);
        }
        return ds;
    }

    /**
     * Evaluate Taylor expansion of a derivative structure.
     *
     * @param delta parameters offsets (&Delta;x, &Delta;y, ...)
     * @return value of the Taylor expansion at x + &Delta;x, y + &Delta;y, ...
     * @throws MathRuntimeException if factorials becomes too large
     */
    @SafeVarargs
    public final T taylor(final T... delta) throws MathRuntimeException {
        return factory.getCompiler().taylor(data, 0, delta);
    }

    /**
     * Evaluate Taylor expansion of a derivative structure.
     *
     * @param delta parameters offsets (&Delta;x, &Delta;y, ...)
     * @return value of the Taylor expansion at x + &Delta;x, y + &Delta;y, ...
     * @throws MathRuntimeException if factorials becomes too large
     */
    public T taylor(final double... delta) throws MathRuntimeException {
        return factory.getCompiler().taylor(data, 0, delta);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final FieldDerivativeStructure<T>[] a,
                                                         final FieldDerivativeStructure<T>[] b)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T[] aT = MathArrays.buildArray(factory.getValueField(), a.length);
        for (int i = 0; i < a.length; ++i) {
            aT[i] = a[i].getValue();
        }
        final T[] bT = MathArrays.buildArray(factory.getValueField(), b.length);
        for (int i = 0; i < b.length; ++i) {
            bT[i] = b[i].getValue();
        }
        final T accurateValue = aT[0].linearCombination(aT, bT);

        // compute a simple value, with all partial derivatives
        FieldDerivativeStructure<T> simpleValue = a[0].getField().getZero();
        for (int i = 0; i < a.length; ++i) {
            simpleValue = simpleValue.add(a[i].multiply(b[i]));
        }

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * Compute a linear combination.
     *
     * @param a Factors.
     * @param b Factors.
     * @return <code>&Sigma;<sub>i</sub> a<sub>i</sub> b<sub>i</sub></code>.
     * @throws MathIllegalArgumentException if arrays dimensions don't match
     */
    public FieldDerivativeStructure<T> linearCombination(final T[] a, final FieldDerivativeStructure<T>[] b)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T[] bT = MathArrays.buildArray(factory.getValueField(), b.length);
        for (int i = 0; i < b.length; ++i) {
            bT[i] = b[i].getValue();
        }
        final T accurateValue = bT[0].linearCombination(a, bT);

        // compute a simple value, with all partial derivatives
        FieldDerivativeStructure<T> simpleValue = b[0].getField().getZero();
        for (int i = 0; i < a.length; ++i) {
            simpleValue = simpleValue.add(b[i].multiply(a[i]));
        }

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final double[] a, final FieldDerivativeStructure<T>[] b)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T[] bT = MathArrays.buildArray(factory.getValueField(), b.length);
        for (int i = 0; i < b.length; ++i) {
            bT[i] = b[i].getValue();
        }
        final T accurateValue = bT[0].linearCombination(a, bT);

        // compute a simple value, with all partial derivatives
        FieldDerivativeStructure<T> simpleValue = b[0].getField().getZero();
        for (int i = 0; i < a.length; ++i) {
            simpleValue = simpleValue.add(b[i].multiply(a[i]));
        }

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final FieldDerivativeStructure<T> a1, final FieldDerivativeStructure<T> b1,
                                                         final FieldDerivativeStructure<T> a2, final FieldDerivativeStructure<T> b2)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T accurateValue = a1.getValue().linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue());

        // compute a simple value, with all partial derivatives
        final FieldDerivativeStructure<T> simpleValue = a1.multiply(b1).add(a2.multiply(b2));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * Compute a linear combination.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub>
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @see #linearCombination(double, Object, double, Object, double, Object)
     * @see #linearCombination(double, Object, double, Object, double, Object, double, Object)
     */
    public FieldDerivativeStructure<T> linearCombination(final T a1, final FieldDerivativeStructure<T> b1,
                                                         final T a2, final FieldDerivativeStructure<T> b2)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                ds.data, 0);

        return ds;

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final double a1, final FieldDerivativeStructure<T> b1,
                                                         final double a2, final FieldDerivativeStructure<T> b2)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                ds.data, 0);

        return ds;

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final FieldDerivativeStructure<T> a1, final FieldDerivativeStructure<T> b1,
                                                         final FieldDerivativeStructure<T> a2, final FieldDerivativeStructure<T> b2,
                                                         final FieldDerivativeStructure<T> a3, final FieldDerivativeStructure<T> b3)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T accurateValue = a1.getValue().linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue(),
                a3.getValue(), b3.getValue());

        // compute a simple value, with all partial derivatives
        final FieldDerivativeStructure<T> simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * Compute a linear combination.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub>
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @see #linearCombination(double, Object, double, Object)
     * @see #linearCombination(double, Object, double, Object, double, Object, double, Object)
     */
    public FieldDerivativeStructure<T> linearCombination(final T a1, final FieldDerivativeStructure<T> b1,
                                                         final T a2, final FieldDerivativeStructure<T> b2,
                                                         final T a3, final FieldDerivativeStructure<T> b3)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);
        factory.checkCompatibility(b3.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                a3, b3.data, 0,
                ds.data, 0);

        return ds;

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final double a1, final FieldDerivativeStructure<T> b1,
                                                         final double a2, final FieldDerivativeStructure<T> b2,
                                                         final double a3, final FieldDerivativeStructure<T> b3)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);
        factory.checkCompatibility(b3.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                a3, b3.data, 0,
                ds.data, 0);

        return ds;

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final FieldDerivativeStructure<T> a1, final FieldDerivativeStructure<T> b1,
                                                         final FieldDerivativeStructure<T> a2, final FieldDerivativeStructure<T> b2,
                                                         final FieldDerivativeStructure<T> a3, final FieldDerivativeStructure<T> b3,
                                                         final FieldDerivativeStructure<T> a4, final FieldDerivativeStructure<T> b4)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final T accurateValue = a1.getValue().linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue(),
                a3.getValue(), b3.getValue(),
                a4.getValue(), b4.getValue());

        // compute a simple value, with all partial derivatives
        final FieldDerivativeStructure<T> simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3)).add(a4.multiply(b4));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final T[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * Compute a linear combination.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @param a4 first factor of the third term
     * @param b4 second factor of the third term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub> +
     * a<sub>4</sub>&times;b<sub>4</sub>
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @see #linearCombination(double, Object, double, Object)
     * @see #linearCombination(double, Object, double, Object, double, Object)
     */
    public FieldDerivativeStructure<T> linearCombination(final T a1, final FieldDerivativeStructure<T> b1,
                                                         final T a2, final FieldDerivativeStructure<T> b2,
                                                         final T a3, final FieldDerivativeStructure<T> b3,
                                                         final T a4, final FieldDerivativeStructure<T> b4)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);
        factory.checkCompatibility(b3.factory);
        factory.checkCompatibility(b4.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                a3, b3.data, 0,
                a4, b4.data, 0,
                ds.data, 0);

        return ds;

    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public FieldDerivativeStructure<T> linearCombination(final double a1, final FieldDerivativeStructure<T> b1,
                                                         final double a2, final FieldDerivativeStructure<T> b2,
                                                         final double a3, final FieldDerivativeStructure<T> b3,
                                                         final double a4, final FieldDerivativeStructure<T> b4)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(b1.factory);
        factory.checkCompatibility(b2.factory);
        factory.checkCompatibility(b3.factory);
        factory.checkCompatibility(b4.factory);

        final FieldDerivativeStructure<T> ds = factory.build();
        factory.getCompiler().linearCombination(a1, b1.data, 0,
                a2, b2.data, 0,
                a3, b3.data, 0,
                a4, b4.data, 0,
                ds.data, 0);

        return ds;

    }

}
