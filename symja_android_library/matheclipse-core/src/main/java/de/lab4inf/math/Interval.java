/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2011,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math;

/**
 * Interface for an interval based arithmetic.
 * <p>
 * An interval is the closed set [l,r] = {x in REAL | with l &le; x  &le; r}.
 * <p>
 * An interval forms an Abelian semi-group under
 * the operations of addition and multiplication
 * with algebra:
 * <p>
 * <pre>
 *  [a,b] + [c,d] = [a+c, b+d]
 *  [a,b] - [c,d] = [a-c, b-d]
 *  [a,b] * [c,d] = [min*, max*]
 * </pre>
 * <p>
 * Where min and max are for products
 * <pre>
 *  min* = min(ac,ad,bc,bd)
 *  max* = max(ac,ad,bc,bd)
 * </pre>
 * Special care has to be taken, if the divisor interval contains zero.
 * If not the interval borders are given by
 * <pre>
 *  [a,b]/ [c,d] = [min/, max/]
 *  min/ = min(a/c,a/d,b/c,b/d)
 *  max/ = max(a/c,a/d,b/c,b/d)
 * </pre>
 * <p>
 * From the definition follows that the division '/'
 * is not the inverse of the multiplication '*'.
 * <br/>
 * You can resolve an Interval implementation via the L4MLoader:
 * <pre>
 *
 *   Interval w = L4MLoader.load(Interval.class);
 *
 * </pre>
 * <p>
 * The set operations union and intersection
 * are defined as:
 * <pre>
 *  [a,b] &amp; [c,d] = [max&amp;(a,c), min&amp;(b,d)]
 *  [a,b] | [c,d] = [min(a,c) , max(b,d) ]
 * </pre>
 * In case of the the empty intersection, i.e. max&amp; &gt; min&amp;,
 * the symbolic NULL interval will be returned (not a NullPointer!).
 * <br/>
 * A product logic is provided by the operations
 * NOT, AND, OR and IMPLIES.
 *
 * @author nwulff
 * @version $Id: Interval.java,v 1.17 2014/11/16 21:47:23 nwulff Exp $
 * @since 07.02.2011
 */
@Service
public interface Interval extends Numeric<Interval>, Cloneable {
    /**
     * Factory method, mimic a constructor for a new interval number
     * without exposing the implementing class to a client.
     *
     * @param left  interval border
     * @param right interval border
     * @return new Interval(left, right)
     */
    Interval newInterval(final double left, final double right);

    @Override
    /**
     * Indicate if this is the crisp one element, i.e. the neutral
     * element of the multiplication.
     * @return one flag
     */
    boolean isOne();

    @Override
    /**
     * Indicate if this is the crisp zero element, i.e. the neutral
     * element of the addition.
     * @return zero flag
     */
    boolean isZero();

    /**
     * Indicate if this interval contains the crisp zero value, i.e. l &le; 0 &le; r.
     *
     * @return contains zero flag
     */
    boolean containsZero();

    /**
     * Indicate if this interval contains the crisp value, i.e. l &le; value &le; r.
     *
     * @param value to contain
     * @return contains zero flag
     */
    boolean contains(double value);

    /**
     * Indicate if this is the empty interval.
     *
     * @return boolean NULL flag
     */
    boolean isNull();

    /**
     * Indicate if one of (l,r) is not a number.
     *
     * @return boolean NaN flag
     */
    boolean isNaN();

    /**
     * Indicate if one of (l,r) is infinite.
     *
     * @return boolean NaN flag
     */
    boolean isInfinite();

    /**
     * Indicate if this interval is empty, i.e. has zero
     * Lebesgue measure.
     *
     * @return boolean measure flag
     */
    boolean isEmpty();

    /**
     * Indicate if this interval is a subset of that.
     *
     * @param that interval which should contain this as subinterval
     * @return boolean subset flag
     */
    boolean isSubset(Interval that);

    /**
     * Indicate if this interval can be a logical one.
     * E.i. (l,r) is a subinterval of [0,1].
     *
     * @return boolean logical flag
     */
    boolean isLogical();

    /**
     * Make a deep copy, clone this interval.
     *
     * @return a clone
     */
    Interval clone();

    /**
     * Left border of the interval.
     *
     * @return the l value
     */
    double left();

    /**
     * Right border of the interval.
     *
     * @return the r value
     */
    double right();

    /**
     * The logical negation.
     *
     * @return not this
     */
    Interval not();

    /**
     * The logical OR operation of
     * this and that using product logic.
     *
     * @param that the interval to OR.
     * @return this OR that
     */
    Interval or(Interval that);

    /**
     * The logical AND operation of
     * this and that using product logic.
     *
     * @param that the interval to AND.
     * @return this AND that
     */
    Interval and(Interval that);

    /**
     * The logical implication of from this follow that.
     *
     * @param that the interval to deduce
     * @return this &ge; that
     */
    Interval implies(Interval that);

    /**
     * The union operation of this and that.
     *
     * @param that the interval .
     * @return this union that
     */
    Interval union(Interval that);

    /**
     * The intersection of this and that.
     *
     * @param that the interval to intersect with.
     * @return this intersect that
     */
    Interval intersection(Interval that);

    /**
     * Calculate the similarity between this and that.
     * The implementation uses the Tanimoto coefficient T,
     * which equals the Jaccard index for intervals..
     *
     * @param that the interval to compare to
     * @return Tanimoto coefficient 0 &le; T &le; 1
     */
    double similarity(Interval that);

    /**
     * Calculate the distance between this and that, via
     * the similarity Jaccard index.
     *
     * @param that the interval to compare to
     * @return d = 1 - s
     */
    double distance(Interval that);

    @Override
    /**
     * Addition of this and that interval.
     * @param that interval to add
     * @return this + that
     */
    Interval plus(Interval that);

    /**
     * Addition of this and x.
     *
     * @param x scalar to shift with
     * @return this + x
     */
    Interval plus(double x);

    @Override
    /**
     * Subtraction of this and that interval.
     * @param that interval to subtract
     * @return this - that
     */
    Interval minus(Interval that);

    /**
     * Subtraction of this and x.
     *
     * @param x scalar to subtract
     * @return this - x
     */
    Interval minus(double x);

    @Override
    /**
     * Multiplication of this and that interval.
     * @param that interval to multiply with
     * @return this * that
     */
    Interval multiply(Interval that);

    @Override
    /**
     * Multiplication of this by x.
     * @param x scalar to scale with
     * @return this * x
     */
    Interval multiply(double x);

    @Override
    /**
     * Division of this by that interval.
     * @param that interval to divide with
     * @return this / that
     */
    Interval div(Interval that);

    /**
     * Division of this by x.
     *
     * @param x scalar to divide with
     * @return this / x
     */
    Interval div(double x);

    /**
     * Raise this interval to the power of that.
     *
     * @param that interval exponent
     * @return this ^ that
     */
    Interval pow(Interval that);

    /**
     * Raise this interval to the power of x.
     *
     * @param x the real exponent
     * @return this ^ x
     */
    Interval pow(double x);

    @Override
    /**
     * The absolut value of this interval.
     * @return abs(this)
     */
    Interval abs();

    /**
     * The absolut value squared of this interval.
     *
     * @return abs2(this)
     */
    Interval abs2();

}
 