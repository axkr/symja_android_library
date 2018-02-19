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
package org.hipparchus.analysis.differentiation;

import org.hipparchus.Field;
import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;

import java.io.Serializable;

/**
 * Class representing both the value and the differentials of a function.
 * <p>This class is the workhorse of the differentiation package.</p>
 * <p>This class is an implementation of the extension to Rall's
 * numbers described in Dan Kalman's paper <a
 * href="http://www1.american.edu/cas/mathstat/People/kalman/pdffiles/mmgautodiff.pdf">Doubly
 * Recursive Multivariate Automatic Differentiation</a>, Mathematics Magazine, vol. 75,
 * no. 3, June 2002. Rall's numbers are an extension to the real numbers used
 * throughout mathematical expressions; they hold the derivative together with the
 * value of a function. Dan Kalman's derivative structures hold all partial derivatives
 * up to any specified order, with respect to any number of free parameters. Rall's
 * numbers therefore can be seen as derivative structures for order one derivative and
 * one free parameter, and real numbers can be seen as derivative structures with zero
 * order derivative and no free parameters.</p>
 * <p>{@link DerivativeStructure} instances can be used directly thanks to
 * the arithmetic operators to the mathematical functions provided as
 * methods by this class (+, -, *, /, %, sin, cos ...).</p>
 * <p>Implementing complex expressions by hand using these classes is
 * a tedious and error-prone task but has the advantage of having no limitation
 * on the derivation order despite not requiring users to compute the derivatives by
 * themselves. Implementing complex expression can also be done by developing computation
 * code using standard primitive double values and to use {@link
 * UnivariateFunctionDifferentiator differentiators} to create the {@link
 * DerivativeStructure}-based instances. This method is simpler but may be limited in
 * the accuracy and derivation orders and may be computationally intensive (this is
 * typically the case for {@link FiniteDifferencesDifferentiator finite differences
 * differentiator}.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 *
 * @see DSCompiler
 * @see FieldDerivativeStructure
 */
public class DerivativeStructure implements RealFieldElement<DerivativeStructure>, Serializable {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20161220L;

    /**
     * Factory that built the instance.
     */
    private final DSFactory factory;

    /**
     * Combined array holding all values.
     */
    private final double[] data;

    /**
     * Build an instance with all values and derivatives set to 0.
     *
     * @param factory factory that built the instance
     * @param data    combined array holding all values
     */
    DerivativeStructure(final DSFactory factory, final double[] data) {
        this.factory = factory;
        this.data = data;
    }

    /**
     * Build an instance with all values and derivatives set to 0.
     *
     * @param parameters number of free parameters
     * @param order      derivation order
     * @throws MathIllegalArgumentException if order is too large
     * @deprecated as of 1.1, replaced by {@link DSFactory#build()}
     */
    @Deprecated
    public DerivativeStructure(final int parameters, final int order)
            throws MathIllegalArgumentException {
        this(new DSFactory(parameters, order).build());
    }

    /**
     * Build an instance representing a constant value.
     *
     * @param parameters number of free parameters
     * @param order      derivation order
     * @param value      value of the constant
     * @throws MathIllegalArgumentException if order is too large
     * @deprecated as of 1.1, replaced by {@link DSFactory#constant(double)}
     */
    @Deprecated
    public DerivativeStructure(final int parameters, final int order, final double value)
            throws MathIllegalArgumentException {
        this(new DSFactory(parameters, order).constant(value));
    }

    /**
     * Build an instance representing a variable.
     * <p>Instances built using this constructor are considered
     * to be the free variables with respect to which differentials
     * are computed. As such, their differential with respect to
     * themselves is +1.</p>
     *
     * @param parameters number of free parameters
     * @param order      derivation order
     * @param index      index of the variable (from 0 to {@code parameters - 1})
     * @param value      value of the variable
     * @throws MathIllegalArgumentException if {@code index &ge; parameters}.
     * @deprecated as of 1.1, replaced by {@link DSFactory#variable(int, double)}
     */
    @Deprecated
    public DerivativeStructure(final int parameters, final int order,
                               final int index, final double value)
            throws MathIllegalArgumentException {
        this(new DSFactory(parameters, order).variable(index, value));
    }

    /**
     * Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2
     *
     * @param a1  first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2  second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @deprecated as of 1.1, replaced by {@link #linearCombination(double, DerivativeStructure, double, DerivativeStructure)}
     */
    @Deprecated
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2)
            throws MathIllegalArgumentException {
        this(ds1.linearCombination(a1, ds1, a2, ds2));
    }

    /**
     * Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2 + a3 * ds3
     *
     * @param a1  first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2  second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @param a3  third scale factor
     * @param ds3 third base (unscaled) derivative structure
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @deprecated as of 1.1, replaced by {@link #linearCombination(double, DerivativeStructure,
     * double, DerivativeStructure, double, DerivativeStructure)}
     */
    @Deprecated
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2,
                               final double a3, final DerivativeStructure ds3)
            throws MathIllegalArgumentException {
        this(ds1.linearCombination(a1, ds1, a2, ds2, a3, ds3));
    }

    /**
     * Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2 + a3 * ds3 + a4 * ds4
     *
     * @param a1  first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2  second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @param a3  third scale factor
     * @param ds3 third base (unscaled) derivative structure
     * @param a4  fourth scale factor
     * @param ds4 fourth base (unscaled) derivative structure
     * @throws MathIllegalArgumentException if number of free parameters or orders are inconsistent
     * @deprecated as of 1.1, replaced by {@link #linearCombination(double, DerivativeStructure,
     * double, DerivativeStructure, double, DerivativeStructure, double, DerivativeStructure)}
     */
    @Deprecated
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2,
                               final double a3, final DerivativeStructure ds3,
                               final double a4, final DerivativeStructure ds4)
            throws MathIllegalArgumentException {
        this(ds1.linearCombination(a1, ds1, a2, ds2, a3, ds3, a4, ds4));
    }

    /**
     * Build an instance from all its derivatives.
     *
     * @param parameters  number of free parameters
     * @param order       derivation order
     * @param derivatives derivatives sorted according to
     *                    {@link DSCompiler#getPartialDerivativeIndex(int...)}
     * @throws MathIllegalArgumentException if derivatives array does not match the
     *                                      {@link DSCompiler#getSize() size} expected by the compiler
     * @throws MathIllegalArgumentException if order is too large
     * @deprecated as of 1.1, replaced by {@link DSFactory#build(double...)}
     */
    @Deprecated
    public DerivativeStructure(final int parameters, final int order, final double... derivatives)
            throws MathIllegalArgumentException {
        this(new DSFactory(parameters, order).build(derivatives));
    }

    /**
     * Copy constructor.
     *
     * @param ds instance to copy
     * @deprecated as of 1.1, this method is used only for implementing other deprecated constructors
     */
    @Deprecated
    private DerivativeStructure(final DerivativeStructure ds) {
        this.factory = ds.factory;
        this.data = ds.data.clone();
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
     * @param x a value
     * @param y a value
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    public static DerivativeStructure hypot(final DerivativeStructure x, final DerivativeStructure y)
            throws MathIllegalArgumentException {
        return x.hypot(y);
    }

    /**
     * Compute a<sup>x</sup> where a is a double and x a {@link DerivativeStructure}
     *
     * @param a number to exponentiate
     * @param x power to apply
     * @return a<sup>x</sup>
     */
    public static DerivativeStructure pow(final double a, final DerivativeStructure x) {
        final DerivativeStructure result = x.factory.build();
        x.factory.getCompiler().pow(a, x.data, 0, result.data, 0);
        return result;
    }

    /**
     * Two arguments arc tangent operation.
     *
     * @param y first argument of the arc tangent
     * @param x second argument of the arc tangent
     * @return atan2(y, x)
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    public static DerivativeStructure atan2(final DerivativeStructure y, final DerivativeStructure x)
            throws MathIllegalArgumentException {
        return y.atan2(x);
    }

    /**
     * Get the factory that built the instance.
     *
     * @return factory that built the instance
     */
    public DSFactory getFactory() {
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
     * Create a constant compatible with instance order and number of parameters.
     * <p>
     * This method is a convenience factory method, it simply calls
     * {@code new DerivativeStructure(getFreeParameters(), getOrder(), c)}
     * </p>
     *
     * @param c value of the constant
     * @return a constant compatible with instance order and number of parameters
     * @deprecated as of 1.1, replaced by {@link DSFactory#constant(double)}
     */
    @Deprecated
    public DerivativeStructure createConstant(final double c) {
        return factory.constant(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getReal() {
        return data[0];
    }

    /**
     * Get the value part of the derivative structure.
     *
     * @return value part of the derivative structure
     * @see #getPartialDerivative(int...)
     */
    public double getValue() {
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
    public double getPartialDerivative(final int... orders)
            throws MathIllegalArgumentException {
        return data[getFactory().getCompiler().getPartialDerivativeIndex(orders)];
    }

    /**
     * Get all partial derivatives.
     *
     * @return a fresh copy of partial derivatives, in an array sorted according to
     * {@link DSCompiler#getPartialDerivativeIndex(int...)}
     */
    public double[] getAllDerivatives() {
        return data.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure add(final double a) {
        final DerivativeStructure ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] += a;
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public DerivativeStructure add(final DerivativeStructure a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final DerivativeStructure ds = factory.build();
        factory.getCompiler().add(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure subtract(final double a) {
        return add(-a);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public DerivativeStructure subtract(final DerivativeStructure a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final DerivativeStructure ds = factory.build();
        factory.getCompiler().subtract(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure multiply(final int n) {
        return multiply((double) n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure multiply(final double a) {
        final DerivativeStructure ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i] * a;
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
    public DerivativeStructure multiply(final DerivativeStructure a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().multiply(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure divide(final double a) {
        final DerivativeStructure ds = factory.build();
        final double inv = 1.0 / a;
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = data[i] * inv;
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
    public DerivativeStructure divide(final DerivativeStructure a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().divide(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure remainder(final double a) {
        final DerivativeStructure ds = factory.build();
        System.arraycopy(data, 0, ds.data, 0, data.length);
        ds.data[0] = FastMath.IEEEremainder(ds.data[0], a);
        return ds;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public DerivativeStructure remainder(final DerivativeStructure a)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(a.factory);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().remainder(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure negate() {
        final DerivativeStructure ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = -data[i];
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure abs() {
        if (Double.doubleToLongBits(data[0]) < 0) {
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
    public DerivativeStructure ceil() {
        return factory.constant(FastMath.ceil(data[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure floor() {
        return factory.constant(FastMath.floor(data[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure rint() {
        return factory.constant(FastMath.rint(data[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long round() {
        return FastMath.round(data[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure signum() {
        return factory.constant(FastMath.signum(data[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure copySign(final DerivativeStructure sign) {
        long m = Double.doubleToLongBits(data[0]);
        long s = Double.doubleToLongBits(sign.data[0]);
        if ((m >= 0 && s >= 0) || (m < 0 && s < 0)) { // Sign is currently OK
            return this;
        }
        return negate(); // flip sign
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure copySign(final double sign) {
        long m = Double.doubleToLongBits(data[0]);
        long s = Double.doubleToLongBits(sign);
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
        return FastMath.getExponent(data[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure scalb(final int n) {
        final DerivativeStructure ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.scalb(data[i], n);
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
    public DerivativeStructure hypot(final DerivativeStructure y)
            throws MathIllegalArgumentException {

        factory.checkCompatibility(y.factory);

        if (Double.isInfinite(data[0]) || Double.isInfinite(y.data[0])) {
            return factory.constant(Double.POSITIVE_INFINITY);
        } else if (Double.isNaN(data[0]) || Double.isNaN(y.data[0])) {
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
                final DerivativeStructure scaledX = scalb(-middleExp);
                final DerivativeStructure scaledY = y.scalb(-middleExp);

                // compute scaled hypotenuse
                final DerivativeStructure scaledH =
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
    public DerivativeStructure compose(final double... f)
            throws MathIllegalArgumentException {

        MathUtils.checkDimension(f.length, getOrder() + 1);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().compose(data, 0, f, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure reciprocal() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().pow(data, 0, -1, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure sqrt() {
        return rootN(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure cbrt() {
        return rootN(3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure rootN(final int n) {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().rootN(data, 0, n, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<DerivativeStructure> getField() {
        return factory.getDerivativeField();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure pow(final double p) {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().pow(data, 0, p, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure pow(final int n) {
        final DerivativeStructure result = factory.build();
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
    public DerivativeStructure pow(final DerivativeStructure e)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(e.factory);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().pow(data, 0, e.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure exp() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().exp(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure expm1() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().expm1(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure log() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().log(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure log1p() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().log1p(data, 0, result.data, 0);
        return result;
    }

    /**
     * Base 10 logarithm.
     *
     * @return base 10 logarithm of the instance
     */
    @Override
    public DerivativeStructure log10() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().log10(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure cos() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().cos(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure sin() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().sin(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure tan() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().tan(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure acos() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().acos(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure asin() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().asin(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure atan() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().atan(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure atan2(final DerivativeStructure x)
            throws MathIllegalArgumentException {
        factory.checkCompatibility(x.factory);
        final DerivativeStructure result = factory.build();
        factory.getCompiler().atan2(data, 0, x.data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure cosh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().cosh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure sinh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().sinh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure tanh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().tanh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure acosh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().acosh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure asinh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().asinh(data, 0, result.data, 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DerivativeStructure atanh() {
        final DerivativeStructure result = factory.build();
        factory.getCompiler().atanh(data, 0, result.data, 0);
        return result;
    }

    /**
     * Convert radians to degrees, with error of less than 0.5 ULP
     *
     * @return instance converted into degrees
     */
    public DerivativeStructure toDegrees() {
        final DerivativeStructure ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toDegrees(data[i]);
        }
        return ds;
    }

    /**
     * Convert degrees to radians, with error of less than 0.5 ULP
     *
     * @return instance converted into radians
     */
    public DerivativeStructure toRadians() {
        final DerivativeStructure ds = factory.build();
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toRadians(data[i]);
        }
        return ds;
    }

    /**
     * Evaluate Taylor expansion a derivative structure.
     *
     * @param delta parameters offsets (&Delta;x, &Delta;y, ...)
     * @return value of the Taylor expansion at x + &Delta;x, y + &Delta;y, ...
     * @throws MathRuntimeException if factorials becomes too large
     */
    public double taylor(final double... delta) throws MathRuntimeException {
        return factory.getCompiler().taylor(data, 0, delta);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if number of free parameters
     *                                      or orders do not match
     */
    @Override
    public DerivativeStructure linearCombination(final DerivativeStructure[] a, final DerivativeStructure[] b)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double[] aDouble = new double[a.length];
        for (int i = 0; i < a.length; ++i) {
            aDouble[i] = a[i].getValue();
        }
        final double[] bDouble = new double[b.length];
        for (int i = 0; i < b.length; ++i) {
            bDouble[i] = b[i].getValue();
        }
        final double accurateValue = MathArrays.linearCombination(aDouble, bDouble);

        // compute a simple value, with all partial derivatives
        DerivativeStructure simpleValue = a[0].getField().getZero();
        for (int i = 0; i < a.length; ++i) {
            simpleValue = simpleValue.add(a[i].multiply(b[i]));
        }

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final double[] a, final DerivativeStructure[] b)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double[] bDouble = new double[b.length];
        for (int i = 0; i < b.length; ++i) {
            bDouble[i] = b[i].getValue();
        }
        final double accurateValue = MathArrays.linearCombination(a, bDouble);

        // compute a simple value, with all partial derivatives
        DerivativeStructure simpleValue = b[0].getField().getZero();
        for (int i = 0; i < a.length; ++i) {
            simpleValue = simpleValue.add(b[i].multiply(a[i]));
        }

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1,
                                                 final DerivativeStructure a2, final DerivativeStructure b2)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1,
                                                 final double a2, final DerivativeStructure b2)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(),
                a2, b2.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1,
                                                 final DerivativeStructure a2, final DerivativeStructure b2,
                                                 final DerivativeStructure a3, final DerivativeStructure b3)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue(),
                a3.getValue(), b3.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1,
                                                 final double a2, final DerivativeStructure b2,
                                                 final double a3, final DerivativeStructure b3)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(),
                a2, b2.getValue(),
                a3, b3.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1,
                                                 final DerivativeStructure a2, final DerivativeStructure b2,
                                                 final DerivativeStructure a3, final DerivativeStructure b3,
                                                 final DerivativeStructure a4, final DerivativeStructure b4)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(),
                a2.getValue(), b2.getValue(),
                a3.getValue(), b3.getValue(),
                a4.getValue(), b4.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3)).add(a4.multiply(b4));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
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
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1,
                                                 final double a2, final DerivativeStructure b2,
                                                 final double a3, final DerivativeStructure b3,
                                                 final double a4, final DerivativeStructure b4)
            throws MathIllegalArgumentException {

        // compute an accurate value, taking care of cancellations
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(),
                a2, b2.getValue(),
                a3, b3.getValue(),
                a4, b4.getValue());

        // compute a simple value, with all partial derivatives
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3)).add(b4.multiply(a4));

        // create a result with accurate value and all derivatives (not necessarily as accurate as the value)
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return factory.build(all);

    }

    /**
     * Test for the equality of two derivative structures.
     * <p>
     * Derivative structures are considered equal if they have the same number
     * of free parameters, the same derivation order, and the same derivatives.
     * </p>
     *
     * @param other Object to test for equality to this
     * @return true if two derivative structures are equal
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other instanceof DerivativeStructure) {
            final DerivativeStructure rhs = (DerivativeStructure) other;
            return (getFreeParameters() == rhs.getFreeParameters()) &&
                    (getOrder() == rhs.getOrder()) &&
                    MathArrays.equals(data, rhs.data);
        }

        return false;

    }

    /**
     * Get a hashCode for the derivative structure.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return 227 + 229 * getFreeParameters() + 233 * getOrder() + 239 * MathUtils.hash(data);
    }

}
