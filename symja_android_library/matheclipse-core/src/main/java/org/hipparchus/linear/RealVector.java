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

package org.hipparchus.linear;

import org.hipparchus.analysis.FunctionUtils;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.function.Add;
import org.hipparchus.analysis.function.Divide;
import org.hipparchus.analysis.function.Multiply;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.FastMath;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class defining a real-valued vector with basic algebraic operations.
 * <p>
 * vector element indexing is 0-based -- e.g., {@code getEntry(0)}
 * returns the first element of the vector.
 * </p>
 * <p>
 * The {@code code map} and {@code mapToSelf} methods operate
 * on vectors element-wise, i.e. they perform the same operation (adding a scalar,
 * applying a function ...) on each element in turn. The {@code map}
 * versions create a new vector to hold the result and do not change the instance.
 * The {@code mapToSelf} version uses the instance itself to store the
 * results, so the instance is changed by this method. In all cases, the result
 * vector is returned by the methods, allowing the <i>fluent API</i>
 * style, like this:
 * </p>
 * <pre>
 *   RealVector result = v.mapAddToSelf(3.4).mapToSelf(new Tan()).mapToSelf(new Power(2.3));
 * </pre>
 */
public abstract class RealVector {
    /**
     * Returns an unmodifiable view of the specified vector.
     * The returned vector has read-only access. An attempt to modify it will
     * result in a {@link MathRuntimeException}. However, the
     * returned vector is <em>not</em> immutable, since any modification of
     * {@code v} will also change the returned view.
     * For example, in the following piece of code
     * <pre>
     *     RealVector v = new ArrayRealVector(2);
     *     RealVector w = RealVector.unmodifiableRealVector(v);
     *     v.setEntry(0, 1.2);
     *     v.setEntry(1, -3.4);
     * </pre>
     * the changes will be seen in the {@code w} view of {@code v}.
     *
     * @param v Vector for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of {@code v}.
     */
    public static RealVector unmodifiableRealVector(final RealVector v) {
        /**
         * This anonymous class is an implementation of {@link RealVector}
         * with read-only access.
         * It wraps any {@link RealVector}, and exposes all methods which
         * do not modify it. Invoking methods which should normally result
         * in the modification of the calling {@link RealVector} results in
         * a {@link MathRuntimeException}. It should be noted
         * that {@link UnmodifiableVector} is <em>not</em> immutable.
         */
        return new RealVector() {
            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all circumstances.
             */
            @Override
            public RealVector mapToSelf(UnivariateFunction function)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector map(UnivariateFunction function) {
                return v.map(function);
            }

            /** {@inheritDoc} */
            @Override
            public Iterator<Entry> iterator() {
                final Iterator<Entry> i = v.iterator();
                return new Iterator<Entry>() {
                    /** The current entry. */
                    private final UnmodifiableEntry e = new UnmodifiableEntry();

                    /** {@inheritDoc} */
                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Entry next() {
                        e.setIndex(i.next().getIndex());
                        return e;
                    }

                    /**
                     * {@inheritDoc}
                     *
                     * @throws MathRuntimeException in all
                     * circumstances.
                     */
                    @Override
                    public void remove() throws MathRuntimeException {
                        throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
                    }
                };
            }

            /** {@inheritDoc} */
            @Override
            public Iterator<Entry> sparseIterator() {
                final Iterator<Entry> i = v.sparseIterator();

                return new Iterator<Entry>() {
                    /** The current entry. */
                    private final UnmodifiableEntry e = new UnmodifiableEntry();

                    /** {@inheritDoc} */
                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Entry next() {
                        e.setIndex(i.next().getIndex());
                        return e;
                    }

                    /**
                     * {@inheritDoc}
                     *
                     * @throws MathRuntimeException in all
                     * circumstances.
                     */
                    @Override
                    public void remove()
                            throws MathRuntimeException {
                        throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
                    }
                };
            }

            /** {@inheritDoc} */
            @Override
            public RealVector copy() {
                return v.copy();
            }

            /** {@inheritDoc} */
            @Override
            public RealVector add(RealVector w)
                    throws MathIllegalArgumentException {
                return v.add(w);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector subtract(RealVector w)
                    throws MathIllegalArgumentException {
                return v.subtract(w);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector mapAdd(double d) {
                return v.mapAdd(d);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public RealVector mapAddToSelf(double d)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector mapSubtract(double d) {
                return v.mapSubtract(d);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public RealVector mapSubtractToSelf(double d)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector mapMultiply(double d) {
                return v.mapMultiply(d);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public RealVector mapMultiplyToSelf(double d)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector mapDivide(double d) {
                return v.mapDivide(d);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public RealVector mapDivideToSelf(double d)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector ebeMultiply(RealVector w)
                    throws MathIllegalArgumentException {
                return v.ebeMultiply(w);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector ebeDivide(RealVector w)
                    throws MathIllegalArgumentException {
                return v.ebeDivide(w);
            }

            /** {@inheritDoc} */
            @Override
            public double dotProduct(RealVector w)
                    throws MathIllegalArgumentException {
                return v.dotProduct(w);
            }

            /** {@inheritDoc} */
            @Override
            public double cosine(RealVector w)
                    throws MathIllegalArgumentException, MathRuntimeException {
                return v.cosine(w);
            }

            /** {@inheritDoc} */
            @Override
            public double getNorm() {
                return v.getNorm();
            }

            /** {@inheritDoc} */
            @Override
            public double getL1Norm() {
                return v.getL1Norm();
            }

            /** {@inheritDoc} */
            @Override
            public double getLInfNorm() {
                return v.getLInfNorm();
            }

            /** {@inheritDoc} */
            @Override
            public double getDistance(RealVector w)
                    throws MathIllegalArgumentException {
                return v.getDistance(w);
            }

            /** {@inheritDoc} */
            @Override
            public double getL1Distance(RealVector w)
                    throws MathIllegalArgumentException {
                return v.getL1Distance(w);
            }

            /** {@inheritDoc} */
            @Override
            public double getLInfDistance(RealVector w)
                    throws MathIllegalArgumentException {
                return v.getLInfDistance(w);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector unitVector() throws MathRuntimeException {
                return v.unitVector();
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public void unitize() throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public RealMatrix outerProduct(RealVector w) {
                return v.outerProduct(w);
            }

            /** {@inheritDoc} */
            @Override
            public double getEntry(int index) throws MathIllegalArgumentException {
                return v.getEntry(index);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public void setEntry(int index, double value)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public void addToEntry(int index, double value)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public int getDimension() {
                return v.getDimension();
            }

            /** {@inheritDoc} */
            @Override
            public RealVector append(RealVector w) {
                return v.append(w);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector append(double d) {
                return v.append(d);
            }

            /** {@inheritDoc} */
            @Override
            public RealVector getSubVector(int index, int n)
                    throws MathIllegalArgumentException {
                return v.getSubVector(index, n);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public void setSubVector(int index, RealVector w)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public void set(double value)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** {@inheritDoc} */
            @Override
            public double[] toArray() {
                return v.toArray();
            }

            /** {@inheritDoc} */
            @Override
            public boolean isNaN() {
                return v.isNaN();
            }

            /** {@inheritDoc} */
            @Override
            public boolean isInfinite() {
                return v.isInfinite();
            }

            /** {@inheritDoc} */
            @Override
            public RealVector combine(double a, double b, RealVector y)
                    throws MathIllegalArgumentException {
                return v.combine(a, b, y);
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all
             * circumstances.
             */
            @Override
            public RealVector combineToSelf(double a, double b, RealVector y)
                    throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }

            /** An entry in the vector. */
            class UnmodifiableEntry extends Entry {
                /** {@inheritDoc} */
                @Override
                public double getValue() {
                    return v.getEntry(getIndex());
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws MathRuntimeException in all
                 * circumstances.
                 */
                @Override
                public void setValue(double value)
                        throws MathRuntimeException {
                    throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
                }
            }
        };
    }

    /**
     * Returns the size of the vector.
     *
     * @return the size of this vector.
     */
    public abstract int getDimension();

    /**
     * Return the entry at the specified index.
     *
     * @param index Index location of entry to be fetched.
     * @return the vector entry at {@code index}.
     * @throws MathIllegalArgumentException if the index is not valid.
     * @see #setEntry(int, double)
     */
    public abstract double getEntry(int index) throws MathIllegalArgumentException;

    /**
     * Set a single element.
     *
     * @param index element index.
     * @param value new value for the element.
     * @throws MathIllegalArgumentException if the index is not valid.
     * @see #getEntry(int)
     */
    public abstract void setEntry(int index, double value)
            throws MathIllegalArgumentException;

    /**
     * Change an entry at the specified index.
     *
     * @param index     Index location of entry to be set.
     * @param increment Value to add to the vector entry.
     * @throws MathIllegalArgumentException if the index is not valid.
     */
    public void addToEntry(int index, double increment)
            throws MathIllegalArgumentException {
        setEntry(index, getEntry(index) + increment);
    }

    /**
     * Construct a new vector by appending a vector to this vector.
     *
     * @param v vector to append to this one.
     * @return a new vector.
     */
    public abstract RealVector append(RealVector v);

    /**
     * Construct a new vector by appending a double to this vector.
     *
     * @param d double to append.
     * @return a new vector.
     */
    public abstract RealVector append(double d);

    /**
     * Get a subvector from consecutive elements.
     *
     * @param index index of first element.
     * @param n     number of elements to be retrieved.
     * @return a vector containing n elements.
     * @throws MathIllegalArgumentException if the index is not valid.
     * @throws MathIllegalArgumentException if the number of elements is not positive.
     */
    public abstract RealVector getSubVector(int index, int n)
            throws MathIllegalArgumentException;

    /**
     * Set a sequence of consecutive elements.
     *
     * @param index index of first element to be set.
     * @param v     vector containing the values to set.
     * @throws MathIllegalArgumentException if the index is not valid.
     */
    public abstract void setSubVector(int index, RealVector v)
            throws MathIllegalArgumentException;

    /**
     * Check whether any coordinate of this vector is {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is {@code NaN},
     * {@code false} otherwise.
     */
    public abstract boolean isNaN();

    /**
     * Check whether any coordinate of this vector is infinite and none are {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is infinite and
     * none are {@code NaN}, {@code false} otherwise.
     */
    public abstract boolean isInfinite();

    /**
     * Check if instance and specified vectors have the same dimension.
     *
     * @param v Vector to compare instance with.
     * @throws MathIllegalArgumentException if the vectors do not
     *                                      have the same dimension.
     */
    protected void checkVectorDimensions(RealVector v)
            throws MathIllegalArgumentException {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n Expected dimension.
     * @throws MathIllegalArgumentException if the dimension is
     *                                      inconsistent with the vector size.
     */
    protected void checkVectorDimensions(int n)
            throws MathIllegalArgumentException {
        int d = getDimension();
        if (d != n) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    d, n);
        }
    }

    /**
     * Check if an index is valid.
     *
     * @param index Index to check.
     * @throws MathIllegalArgumentException if {@code index} is not valid.
     */
    protected void checkIndex(final int index) throws MathIllegalArgumentException {
        if (index < 0 ||
                index >= getDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INDEX,
                    index, 0, getDimension() - 1);
        }
    }

    /**
     * Checks that the indices of a subvector are valid.
     *
     * @param start the index of the first entry of the subvector
     * @param end   the index of the last entry of the subvector (inclusive)
     * @throws MathIllegalArgumentException if {@code start} of {@code end} are not valid
     * @throws MathIllegalArgumentException if {@code end < start}
     */
    protected void checkIndices(final int start, final int end)
            throws MathIllegalArgumentException {
        final int dim = getDimension();
        if ((start < 0) || (start >= dim)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INDEX, start, 0,
                    dim - 1);
        }
        if ((end < 0) || (end >= dim)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INDEX, end, 0,
                    dim - 1);
        }
        if (end < start) {
            // TODO Use more specific error message
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INITIAL_ROW_AFTER_FINAL_ROW,
                    end, start, false);
        }
    }

    /**
     * Compute the sum of this vector and {@code v}.
     * Returns a new vector. Does not change instance data.
     *
     * @param v Vector to be added.
     * @return {@code this} + {@code v}.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public RealVector add(RealVector v) throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        RealVector result = v.copy();
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    /**
     * Subtract {@code v} from this vector.
     * Returns a new vector. Does not change instance data.
     *
     * @param v Vector to be subtracted.
     * @return {@code this} - {@code v}.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public RealVector subtract(RealVector v) throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        RealVector result = v.mapMultiply(-1d);
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    /**
     * Add a value to each entry.
     * Returns a new vector. Does not change instance data.
     *
     * @param d Value to be added to each entry.
     * @return {@code this} + {@code d}.
     */
    public RealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /**
     * Add a value to each entry.
     * The instance is changed in-place.
     *
     * @param d Value to be added to each entry.
     * @return {@code this}.
     */
    public RealVector mapAddToSelf(double d) {
        if (d != 0) {
            return mapToSelf(FunctionUtils.fix2ndArgument(new Add(), d));
        }
        return this;
    }

    /**
     * Returns a (deep) copy of this vector.
     *
     * @return a vector copy.
     */
    public abstract RealVector copy();

    /**
     * Compute the dot product of this vector with {@code v}.
     *
     * @param v Vector with which dot product should be computed
     * @return the scalar dot product between this instance and {@code v}.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public double dotProduct(RealVector v) throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        final int n = getDimension();
        for (int i = 0; i < n; i++) {
            d += getEntry(i) * v.getEntry(i);
        }
        return d;
    }

    /**
     * Computes the cosine of the angle between this vector and the
     * argument.
     *
     * @param v Vector.
     * @return the cosine of the angle between this vector and {@code v}.
     * @throws MathRuntimeException         if {@code this} or {@code v} is the null
     *                                      vector
     * @throws MathIllegalArgumentException if the dimensions of {@code this} and
     *                                      {@code v} do not match
     */
    public double cosine(RealVector v) throws MathIllegalArgumentException,
            MathRuntimeException {
        final double norm = getNorm();
        final double vNorm = v.getNorm();

        if (norm == 0 ||
                vNorm == 0) {
            throw new MathRuntimeException(LocalizedCoreFormats.ZERO_NORM);
        }
        return dotProduct(v) / (norm * vNorm);
    }

    /**
     * Element-by-element division.
     *
     * @param v Vector by which instance elements must be divided.
     * @return a vector containing this[i] / v[i] for all i.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public abstract RealVector ebeDivide(RealVector v)
            throws MathIllegalArgumentException;

    /**
     * Element-by-element multiplication.
     *
     * @param v Vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public abstract RealVector ebeMultiply(RealVector v)
            throws MathIllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * element differences, or Euclidean distance.</p>
     *
     * @param v Vector to which distance is requested.
     * @return the distance between two vectors.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     * @see #getL1Distance(RealVector)
     * @see #getLInfDistance(RealVector)
     * @see #getNorm()
     */
    public double getDistance(RealVector v) throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            final double diff = e.getValue() - v.getEntry(e.getIndex());
            d += diff * diff;
        }
        return FastMath.sqrt(d);
    }

    /**
     * Returns the L<sub>2</sub> norm of the vector.
     * <p>The L<sub>2</sub> norm is the root of the sum of
     * the squared elements.</p>
     *
     * @return the norm.
     * @see #getL1Norm()
     * @see #getLInfNorm()
     * @see #getDistance(RealVector)
     */
    public double getNorm() {
        double sum = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            final double value = e.getValue();
            sum += value * value;
        }
        return FastMath.sqrt(sum);
    }

    /**
     * Returns the L<sub>1</sub> norm of the vector.
     * <p>The L<sub>1</sub> norm is the sum of the absolute
     * values of the elements.</p>
     *
     * @return the norm.
     * @see #getNorm()
     * @see #getLInfNorm()
     * @see #getL1Distance(RealVector)
     */
    public double getL1Norm() {
        double norm = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            norm += FastMath.abs(e.getValue());
        }
        return norm;
    }

    /**
     * Returns the L<sub>&infin;</sub> norm of the vector.
     * <p>The L<sub>&infin;</sub> norm is the max of the absolute
     * values of the elements.</p>
     *
     * @return the norm.
     * @see #getNorm()
     * @see #getL1Norm()
     * @see #getLInfDistance(RealVector)
     */
    public double getLInfNorm() {
        double norm = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            norm = FastMath.max(norm, FastMath.abs(e.getValue()));
        }
        return norm;
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * the elements differences.</p>
     *
     * @param v Vector to which distance is requested.
     * @return the distance between two vectors.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     */
    public double getL1Distance(RealVector v)
            throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
        }
        return d;
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * element differences.</p>
     *
     * @param v Vector to which distance is requested.
     * @return the distance between two vectors.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVector)
     * @see #getLInfNorm()
     */
    public double getLInfDistance(RealVector v)
            throws MathIllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
        }
        return d;
    }

    /**
     * Get the index of the minimum entry.
     *
     * @return the index of the minimum entry or -1 if vector length is 0
     * or all entries are {@code NaN}.
     */
    public int getMinIndex() {
        int minIndex = -1;
        double minValue = Double.POSITIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() <= minValue) {
                minIndex = entry.getIndex();
                minValue = entry.getValue();
            }
        }
        return minIndex;
    }

    /**
     * Get the value of the minimum entry.
     *
     * @return the value of the minimum entry or {@code NaN} if all
     * entries are {@code NaN}.
     */
    public double getMinValue() {
        final int minIndex = getMinIndex();
        return minIndex < 0 ? Double.NaN : getEntry(minIndex);
    }

    /**
     * Get the index of the maximum entry.
     *
     * @return the index of the maximum entry or -1 if vector length is 0
     * or all entries are {@code NaN}
     */
    public int getMaxIndex() {
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() >= maxValue) {
                maxIndex = entry.getIndex();
                maxValue = entry.getValue();
            }
        }
        return maxIndex;
    }

    /**
     * Get the value of the maximum entry.
     *
     * @return the value of the maximum entry or {@code NaN} if all
     * entries are {@code NaN}.
     */
    public double getMaxValue() {
        final int maxIndex = getMaxIndex();
        return maxIndex < 0 ? Double.NaN : getEntry(maxIndex);
    }

    /**
     * Multiply each entry by the argument. Returns a new vector.
     * Does not change instance data.
     *
     * @param d Multiplication factor.
     * @return {@code this} * {@code d}.
     */
    public RealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    /**
     * Multiply each entry.
     * The instance is changed in-place.
     *
     * @param d Multiplication factor.
     * @return {@code this}.
     */
    public RealVector mapMultiplyToSelf(double d) {
        return mapToSelf(FunctionUtils.fix2ndArgument(new Multiply(), d));
    }

    /**
     * Subtract a value from each entry. Returns a new vector.
     * Does not change instance data.
     *
     * @param d Value to be subtracted.
     * @return {@code this} - {@code d}.
     */
    public RealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    /**
     * Subtract a value from each entry.
     * The instance is changed in-place.
     *
     * @param d Value to be subtracted.
     * @return {@code this}.
     */
    public RealVector mapSubtractToSelf(double d) {
        return mapAddToSelf(-d);
    }

    /**
     * Divide each entry by the argument. Returns a new vector.
     * Does not change instance data.
     *
     * @param d Value to divide by.
     * @return {@code this} / {@code d}.
     */
    public RealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    /**
     * Divide each entry by the argument.
     * The instance is changed in-place.
     *
     * @param d Value to divide by.
     * @return {@code this}.
     */
    public RealVector mapDivideToSelf(double d) {
        return mapToSelf(FunctionUtils.fix2ndArgument(new Divide(), d));
    }

    /**
     * Compute the outer product.
     *
     * @param v Vector with which outer product should be computed.
     * @return the matrix outer product between this instance and {@code v}.
     */
    public RealMatrix outerProduct(RealVector v) {
        final int m = this.getDimension();
        final int n = v.getDimension();
        final RealMatrix product;
        if (v instanceof SparseRealVector || this instanceof SparseRealVector) {
            product = new OpenMapRealMatrix(m, n);
        } else {
            product = new Array2DRowRealMatrix(m, n);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                product.setEntry(i, j, this.getEntry(i) * v.getEntry(j));
            }
        }
        return product;
    }

    /**
     * Find the orthogonal projection of this vector onto another vector.
     *
     * @param v vector onto which instance must be projected.
     * @return projection of the instance onto {@code v}.
     * @throws MathIllegalArgumentException if {@code v} is not the same size as
     *                                      {@code this} vector.
     * @throws MathRuntimeException         if {@code this} or {@code v} is the null
     *                                      vector
     */
    public RealVector projection(final RealVector v)
            throws MathIllegalArgumentException, MathRuntimeException {
        final double norm2 = v.dotProduct(v);
        if (norm2 == 0.0) {
            throw new MathRuntimeException(LocalizedCoreFormats.ZERO_NORM);
        }
        return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /**
     * Set all elements to a single value.
     *
     * @param value Single value to set for all elements.
     */
    public void set(double value) {
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            e.setValue(value);
        }
    }

    /**
     * Convert the vector to an array of {@code double}s.
     * The array is independent from this vector data: the elements
     * are copied.
     *
     * @return an array containing a copy of the vector elements.
     */
    public double[] toArray() {
        int dim = getDimension();
        double[] values = new double[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = getEntry(i);
        }
        return values;
    }

    /**
     * Creates a unit vector pointing in the direction of this vector.
     * The instance is not changed by this method.
     *
     * @return a unit vector pointing in direction of this vector.
     * @throws MathRuntimeException if the norm is zero.
     */
    public RealVector unitVector() throws MathRuntimeException {
        final double norm = getNorm();
        if (norm == 0) {
            throw new MathRuntimeException(LocalizedCoreFormats.ZERO_NORM);
        }
        return mapDivide(norm);
    }

    /**
     * Converts this vector into a unit vector.
     * The instance itself is changed by this method.
     *
     * @throws MathRuntimeException if the norm is zero.
     */
    public void unitize() throws MathRuntimeException {
        final double norm = getNorm();
        if (norm == 0) {
            throw new MathRuntimeException(LocalizedCoreFormats.ZERO_NORM);
        }
        mapDivideToSelf(getNorm());
    }

    /**
     * Create a sparse iterator over the vector, which may omit some entries.
     * The ommitted entries are either exact zeroes (for dense implementations)
     * or are the entries which are not stored (for real sparse vectors).
     * No guarantees are made about order of iteration.
     * <p>
     * <p>Note: derived classes are required to return an {@link Iterator} that
     * returns non-null {@link Entry} objects as long as {@link Iterator#hasNext()}
     * returns {@code true}.</p>
     *
     * @return a sparse iterator.
     */
    public Iterator<Entry> sparseIterator() {
        return new SparseEntryIterator();
    }

    /**
     * Generic dense iterator. Iteration is in increasing order
     * of the vector index.
     * <p>
     * <p>Note: derived classes are required to return an {@link Iterator} that
     * returns non-null {@link Entry} objects as long as {@link Iterator#hasNext()}
     * returns {@code true}.</p>
     *
     * @return a dense iterator.
     */
    public Iterator<Entry> iterator() {
        final int dim = getDimension();
        return new Iterator<Entry>() {

            /** Current index. */
            private int i = 0;

            /** Current entry. */
            private Entry e = new Entry();

            /** {@inheritDoc} */
            @Override
            public boolean hasNext() {
                return i < dim;
            }

            /** {@inheritDoc} */
            @Override
            public Entry next() {
                if (i < dim) {
                    e.setIndex(i++);
                    return e;
                } else {
                    throw new NoSuchElementException();
                }
            }

            /**
             * {@inheritDoc}
             *
             * @throws MathRuntimeException in all circumstances.
             */
            @Override
            public void remove() throws MathRuntimeException {
                throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
            }
        };
    }

    /**
     * Acts as if implemented as:
     * <pre>
     *  return copy().mapToSelf(function);
     * </pre>
     * Returns a new vector. Does not change instance data.
     *
     * @param function Function to apply to each entry.
     * @return a new vector.
     */
    public RealVector map(UnivariateFunction function) {
        return copy().mapToSelf(function);
    }

    /**
     * Acts as if it is implemented as:
     * <pre>
     *  Entry e = null;
     *  for(Iterator<Entry> it = iterator(); it.hasNext(); e = it.next()) {
     *      e.setValue(function.value(e.getValue()));
     *  }
     * </pre>
     * Entries of this vector are modified in-place by this method.
     *
     * @param function Function to apply to each entry.
     * @return a reference to this vector.
     */
    public RealVector mapToSelf(UnivariateFunction function) {
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            final Entry e = it.next();
            e.setValue(function.value(e.getValue()));
        }
        return this;
    }

    /**
     * Returns a new vector representing {@code a * this + b * y}, the linear
     * combination of {@code this} and {@code y}.
     * Returns a new vector. Does not change instance data.
     *
     * @param a Coefficient of {@code this}.
     * @param b Coefficient of {@code y}.
     * @param y Vector with which {@code this} is linearly combined.
     * @return a vector containing {@code a * this[i] + b * y[i]} for all
     * {@code i}.
     * @throws MathIllegalArgumentException if {@code y} is not the same size as
     *                                      {@code this} vector.
     */
    public RealVector combine(double a, double b, RealVector y)
            throws MathIllegalArgumentException {
        return copy().combineToSelf(a, b, y);
    }

    /**
     * Updates {@code this} with the linear combination of {@code this} and
     * {@code y}.
     *
     * @param a Weight of {@code this}.
     * @param b Weight of {@code y}.
     * @param y Vector with which {@code this} is linearly combined.
     * @return {@code this}, with components equal to
     * {@code a * this[i] + b * y[i]} for all {@code i}.
     * @throws MathIllegalArgumentException if {@code y} is not the same size as
     *                                      {@code this} vector.
     */
    public RealVector combineToSelf(double a, double b, RealVector y)
            throws MathIllegalArgumentException {
        checkVectorDimensions(y);
        for (int i = 0; i < getDimension(); i++) {
            final double xi = getEntry(i);
            final double yi = y.getEntry(i);
            setEntry(i, a * xi + b * yi);
        }
        return this;
    }

    /**
     * Visits (but does not alter) all entries of this vector in default order
     * (increasing index).
     *
     * @param visitor the visitor to be used to process the entries of this
     *                vector
     * @return the value returned by {@link RealVectorPreservingVisitor#end()}
     * at the end of the walk
     */
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor) {
        final int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    /**
     * Visits (but does not alter) some entries of this vector in default order
     * (increasing index).
     *
     * @param visitor visitor to be used to process the entries of this vector
     * @param start   the index of the first entry to be visited
     * @param end     the index of the last entry to be visited (inclusive)
     * @return the value returned by {@link RealVectorPreservingVisitor#end()}
     * at the end of the walk
     * @throws MathIllegalArgumentException if {@code end < start}.
     * @throws MathIllegalArgumentException if the indices are not valid.
     */
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor,
                                     final int start, final int end)
            throws MathIllegalArgumentException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    /**
     * Visits (but does not alter) all entries of this vector in optimized
     * order. The order in which the entries are visited is selected so as to
     * lead to the most efficient implementation; it might depend on the
     * concrete implementation of this abstract class.
     *
     * @param visitor the visitor to be used to process the entries of this
     *                vector
     * @return the value returned by {@link RealVectorPreservingVisitor#end()}
     * at the end of the walk
     */
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }

    /**
     * Visits (but does not alter) some entries of this vector in optimized
     * order. The order in which the entries are visited is selected so as to
     * lead to the most efficient implementation; it might depend on the
     * concrete implementation of this abstract class.
     *
     * @param visitor visitor to be used to process the entries of this vector
     * @param start   the index of the first entry to be visited
     * @param end     the index of the last entry to be visited (inclusive)
     * @return the value returned by {@link RealVectorPreservingVisitor#end()}
     * at the end of the walk
     * @throws MathIllegalArgumentException if {@code end < start}.
     * @throws MathIllegalArgumentException if the indices are not valid.
     */
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor,
                                       final int start, final int end)
            throws MathIllegalArgumentException {
        return walkInDefaultOrder(visitor, start, end);
    }

    /**
     * Visits (and possibly alters) all entries of this vector in default order
     * (increasing index).
     *
     * @param visitor the visitor to be used to process and modify the entries
     *                of this vector
     * @return the value returned by {@link RealVectorChangingVisitor#end()}
     * at the end of the walk
     */
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor) {
        final int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    /**
     * Visits (and possibly alters) some entries of this vector in default order
     * (increasing index).
     *
     * @param visitor visitor to be used to process the entries of this vector
     * @param start   the index of the first entry to be visited
     * @param end     the index of the last entry to be visited (inclusive)
     * @return the value returned by {@link RealVectorChangingVisitor#end()}
     * at the end of the walk
     * @throws MathIllegalArgumentException if {@code end < start}.
     * @throws MathIllegalArgumentException if the indices are not valid.
     */
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor,
                                     final int start, final int end)
            throws MathIllegalArgumentException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    /**
     * Visits (and possibly alters) all entries of this vector in optimized
     * order. The order in which the entries are visited is selected so as to
     * lead to the most efficient implementation; it might depend on the
     * concrete implementation of this abstract class.
     *
     * @param visitor the visitor to be used to process the entries of this
     *                vector
     * @return the value returned by {@link RealVectorChangingVisitor#end()}
     * at the end of the walk
     */
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }

    /**
     * Visits (and possibly change) some entries of this vector in optimized
     * order. The order in which the entries are visited is selected so as to
     * lead to the most efficient implementation; it might depend on the
     * concrete implementation of this abstract class.
     *
     * @param visitor visitor to be used to process the entries of this vector
     * @param start   the index of the first entry to be visited
     * @param end     the index of the last entry to be visited (inclusive)
     * @return the value returned by {@link RealVectorChangingVisitor#end()}
     * at the end of the walk
     * @throws MathIllegalArgumentException if {@code end < start}.
     * @throws MathIllegalArgumentException if the indices are not valid.
     */
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor,
                                       final int start, final int end)
            throws MathIllegalArgumentException {
        return walkInDefaultOrder(visitor, start, end);
    }

    /**
     * <p>
     * Test for the equality of two real vectors. If all coordinates of two real
     * vectors are exactly the same, and none are {@code NaN}, the two real
     * vectors are considered to be equal. {@code NaN} coordinates are
     * considered to affect globally the vector and be equals to each other -
     * i.e, if either (or all) coordinates of the real vector are equal to
     * {@code NaN}, the real vector is equal to a vector with all {@code NaN}
     * coordinates.
     * </p>
     * <p>
     * This method <em>must</em> be overriden by concrete subclasses of
     * {@link RealVector} (the current implementation throws an exception).
     * </p>
     *
     * @param other Object to test for equality.
     * @return {@code true} if two vector objects are equal, {@code false} if
     * {@code other} is null, not an instance of {@code RealVector}, or
     * not equal to this {@code RealVector} instance.
     * @throws MathRuntimeException if this method is not
     *                              overridden.
     */
    @Override
    public boolean equals(Object other)
            throws MathRuntimeException {
        throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}. This method <em>must</em> be overriden by concrete
     * subclasses of {@link RealVector} (current implementation throws an
     * exception).
     *
     * @throws MathRuntimeException if this method is not
     *                              overridden.
     */
    @Override
    public int hashCode() throws MathRuntimeException {
        throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
    }

    /**
     * An entry in the vector.
     */
    public class Entry {
        /**
         * Index of this entry.
         */
        private int index;

        /**
         * Simple constructor.
         */
        public Entry() {
            setIndex(0);
        }

        /**
         * Get the value of the entry.
         *
         * @return the value of the entry.
         */
        public double getValue() {
            return getEntry(getIndex());
        }

        /**
         * Set the value of the entry.
         *
         * @param value New value for the entry.
         */
        public void setValue(double value) {
            setEntry(getIndex(), value);
        }

        /**
         * Get the index of the entry.
         *
         * @return the index of the entry.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Set the index of the entry.
         *
         * @param index New index for the entry.
         */
        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * This class should rarely be used, but is here to provide
     * a default implementation of sparseIterator(), which is implemented
     * by walking over the entries, skipping those that are zero.
     * <p>
     * Concrete subclasses which are SparseVector implementations should
     * make their own sparse iterator, rather than using this one.
     * <p>
     * This implementation might be useful for ArrayRealVector, when expensive
     * operations which preserve the default value are to be done on the entries,
     * and the fraction of non-default values is small (i.e. someone took a
     * SparseVector, and passed it into the copy-constructor of ArrayRealVector)
     */
    protected class SparseEntryIterator implements Iterator<Entry> {
        /**
         * Dimension of the vector.
         */
        private final int dim;
        /**
         * Last entry returned by {@link #next()}.
         */
        private Entry current;
        /**
         * Next entry for {@link #next()} to return.
         */
        private Entry next;

        /**
         * Simple constructor.
         */
        protected SparseEntryIterator() {
            dim = getDimension();
            current = new Entry();
            next = new Entry();
            if (next.getValue() == 0) {
                advance(next);
            }
        }

        /**
         * Advance an entry up to the next nonzero one.
         *
         * @param e entry to advance.
         */
        protected void advance(Entry e) {
            if (e == null) {
                return;
            }
            do {
                e.setIndex(e.getIndex() + 1);
            } while (e.getIndex() < dim && e.getValue() == 0);
            if (e.getIndex() >= dim) {
                e.setIndex(-1);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return next.getIndex() >= 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Entry next() {
            int index = next.getIndex();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            current.setIndex(index);
            advance(next);
            return current;
        }

        /**
         * {@inheritDoc}
         *
         * @throws MathRuntimeException in all circumstances.
         */
        @Override
        public void remove() throws MathRuntimeException {
            throw new MathRuntimeException(LocalizedCoreFormats.UNSUPPORTED_OPERATION);
        }
    }
}
