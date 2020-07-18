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

package de.lab4inf.math.sets;

import de.lab4inf.math.Field;
import de.lab4inf.math.Interval;
import de.lab4inf.math.Operand;
import de.lab4inf.math.util.Accuracy;
import de.lab4inf.math.util.Randomizer;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * An interval number is a pair [l,r] which models
 * the left and right of an interval as set.
 * <p>
 * The division implementation is based on
 * <pre>
 * T. Hickey, Q. Ju and M.H. van Emden:
 * "Interval Arithmetic - from Principles to Implementation",
 * Journal of the ACM (JACM), Volume 48 Issue 5, September 2001
 * </pre>
 *
 * @author nwulff
 * @version $Id: IntervalNumber.java,v 1.28 2014/11/18 21:50:21 nwulff Exp $
 * @see de.lab4inf.math.Interval
 * @since 06.02.2011
 */
public class IntervalNumber extends Number implements Interval, Field<Interval> {
    /**
     * the neutral element of the addition.
     */
    public static final IntervalNumber ZERO = new IntervalNumber(0);
    /**
     * the neutral element of the multiplication.
     */
    public static final IntervalNumber ONE = new IntervalNumber(1);
    /**
     * the negativ neutral element of the multiplication.
     */
    public static final IntervalNumber MINUS_ONE = new IntervalNumber(-1);
    /**
     * NULL as the empty set of an intersection of disjunct interval.
     */
    public static final IntervalNumber NULL = new IntervalNumber(true);
    /**
     * serializable id.
     */
    private static final long serialVersionUID = -563981799808422826L;
    /**
     * default format definition for toString.
     */
    private static final String DEFAULT_FMT = "[%f, %f]";
    /**
     * the left and right values.
     */
    protected final double l, r;
    /**
     * specific format definition for toString.
     */
    private String fmt = DEFAULT_FMT;

    /**
     * Internal constructor for the empty NULL interval.
     */
    private IntervalNumber(final boolean isNull) {
        l = Double.MIN_NORMAL;
        r = -l;
    }

    /**
     * Default zero constructor.
     */
    public IntervalNumber() {
        this(0, 0);
    }

    /**
     * Constructor for a crisp none interval number.
     *
     * @param mean the crisp value to take
     */
    public IntervalNumber(final double mean) {
        this(mean, mean);
    }

    /**
     * Copy constructor.
     *
     * @param v interval number to copy
     */
    public IntervalNumber(final Interval v) {
        this(v.left(), v.right());
    }

    /**
     * Construct an interval number with given (left,right) values.
     *
     * @param left  side of the interval
     * @param right side of the interval
     */
    public IntervalNumber(final double left, final double right) {
        if (right < left) {
            r = left;
            l = right;
        } else {
            l = left;
            r = right;
        }
    }

    /**
     * Calculate x + y.
     *
     * @param x first argument
     * @param y second argument
     * @return x + y
     */
    @Operand(symbol = "+")
    public static Interval plus(final Interval x, final Interval y) {
        return x.plus(y);
    }

    /**
     * Calculate x + y.
     *
     * @param x first argument
     * @param y second argument
     * @return x + y
     */
    @Operand(symbol = "+")
    public static Interval plus(final Interval x, final double y) {
        return x.plus(y);
    }

    /**
     * Calculate x + y.
     *
     * @param x first argument
     * @param y second argument
     * @return x + y
     */
    @Operand(symbol = "+")
    public static Interval plus(final double x, final Interval y) {
        return y.plus(x);
    }

    /**
     * Calculate x - y.
     *
     * @param x first argument
     * @param y second argument
     * @return x - y
     */
    @Operand(symbol = "-")
    public static Interval minus(final double x, final Interval y) {
        return new IntervalNumber(x - y.right(), x - y.left());
    }

    /**
     * Calculate x - y.
     *
     * @param x first argument
     * @param y second argument
     * @return x - y
     */
    @Operand(symbol = "-")
    public static Interval minus(final Interval x, final Interval y) {
        return x.minus(y);
    }

    /**
     * Calculate x - y.
     *
     * @param x first argument
     * @param y second argument
     * @return x - y
     */
    @Operand(symbol = "-")
    public static Interval minus(final Interval x, final double y) {
        return x.minus(y);
    }

    /**
     * Calculate x * y.
     *
     * @param x first argument
     * @param y second argument
     * @return x * y
     */
    @Operand(symbol = "*")
    public static Interval multiply(final Interval x, final Interval y) {
        return x.multiply(y);
    }

    /**
     * Calculate x * y.
     *
     * @param x first argument
     * @param y second argument
     * @return x * y
     */
    @Operand(symbol = "*")
    public static Interval multiply(final Interval x, final double y) {
        return x.multiply(y);
    }

    /**
     * Calculate x * y.
     *
     * @param x first argument
     * @param y second argument
     * @return x * y
     */
    @Operand(symbol = "*")
    public static Interval multiply(final double x, final Interval y) {
        return y.multiply(x);
    }

    /**
     * Calculate x / y.
     *
     * @param x first argument
     * @param y second argument
     * @return x / y
     */
    @Operand(symbol = "/")
    public static Interval div(final double x, final Interval y) {
        return new IntervalNumber(x).div(y);
    }

    /**
     * Calculate x / y.
     *
     * @param x first argument
     * @param y second argument
     * @return x / y
     */
    @Operand(symbol = "/")
    public static Interval div(final Interval x, final Interval y) {
        return x.div(y);
    }

    /**
     * Calculate x / y.
     *
     * @param x first argument
     * @param y second argument
     * @return x / y
     */
    @Operand(symbol = "/")
    public static Interval div(final Interval x, final double y) {
        return x.div(y);
    }

    /**
     * Calculate x to the power of y.
     *
     * @param x first argument
     * @param y second argument
     * @return pow(x, y)
     */
    @Operand(symbol = "^")
    public static Interval pow(final Interval x, final Interval y) {
        return x.pow(y);
    }

    /**
     * Calculate x to the power of y.
     *
     * @param x first argument
     * @param y second argument
     * @return pow(x, y)
     */
    @Operand(symbol = "^")
    public static Interval pow(final Interval x, final double y) {
        return x.pow(y);
    }

    /**
     * Calculate x to the power of y.
     *
     * @param x first argument
     * @param y second argument
     * @return pow(x, y)
     */
    @Operand(symbol = "^")
    public static IntervalNumber pow(final double x, final Interval y) {
        final double a = Math.pow(x, y.left());
        final double b = Math.pow(x, y.right());
        final double ll = min(a, b);
        final double rr = max(a, b);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate the logical x AND y.
     *
     * @param x first argument
     * @param y second argument
     * @return x AND y
     */
    @Operand(symbol = "&")
    public static Interval and(final Interval x, final Interval y) {
        return x.and(y);
    }

    /**
     * Calculate the intersection of x and y.
     *
     * @param x first argument
     * @param y second argument
     * @return x intersection y
     */
    public static Interval intersection(final Interval x, final Interval y) {
        return x.intersection(y);
    }

    /**
     * Calculate the logical x OR y.
     *
     * @param x first argument
     * @param y second argument
     * @return x OR y
     */
    @Operand(symbol = "|")
    public static Interval or(final Interval x, final Interval y) {
        return x.or(y);
    }

    /**
     * Calculate the logical implication   x implies y.
     *
     * @param x first argument
     * @param y second argument
     * @return x implies y
     */
    public static Interval implies(final Interval x, final Interval y) {
        return x.implies(y);
    }

    /**
     * Calculate the union if x and y.
     *
     * @param x first argument
     * @param y second argument
     * @return x union y
     */
    @Operand(symbol = "|")
    public static Interval union(final Interval x, final Interval y) {
        return x.union(y);
    }

    /**
     * Helper method to find the minimum in a set.
     *
     * @param x the set
     * @return min(x)
     */
    protected static double min(final double... x) {
        double y = x[0];
        for (int i = 1; i < x.length; i++) {
            y = Math.min(y, x[i]);
        }
        return y;
    }

    /**
     * Helper method to find the maximum in a set.
     *
     * @param x the set
     * @return max(x)
     */
    protected static double max(final double... x) {
        double y = x[0];
        for (int i = 1; i < x.length; i++) {
            y = Math.max(y, x[i]);
        }
        return y;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Interval#newInterval(double, double)
     */
    @Override
    public Interval newInterval(final double left, final double right) {
        return new IntervalNumber(left, right);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Interval clone() {
        try {
            return (Interval) super.clone();
        } catch (final Exception e) {
            // should never happen...?
            // getLogger().warn("clone failed ",e);
            return new IntervalNumber(this);
        }
    }

    /**
     * Return the left interval bound.
     *
     * @return the left value
     */
    @Override
    public double left() {
        return l;
    }

    /**
     * Return the right interval bound.
     *
     * @return the right value
     */
    @Override
    public double right() {
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return (l + r) / 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return (int) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isNull()) {
            return "Null";
        } else if (isNaN()) {
            return "NaN";
        }
        return String.format(fmt, l, r);
    }

    /**
     * Set the format for the toString method.
     *
     * @param s format specifier for (l,r).
     */
    public void setFmt(final String s) {
        final String oldFmt = fmt;
        fmt = s;
        // test it the new format is valid
        // otherwise an exception will raised...
        try {
            toString();
        } catch (final Exception e) {
            fmt = oldFmt;
            throw new IllegalArgumentException("invalid format " + s);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    @Operand(symbol = "<")
    public int compareTo(final Interval o) {
        int res = 0;
        if (right() < o.left()) {
            res = -1;
        } else if (o.right() < left()) {
            res = 1;
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @Operand(symbol = "==")
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Interval)) {
            return false;
        }
        final Interval x = (Interval) obj;
        return Accuracy.isSimilar(l, x.left()) && Accuracy.isSimilar(r, x.right());
        // return (Double.compare(l, x.left())==0)&&(Double.compare(r,
        // x.right())==0);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Double.valueOf(l).hashCode() ^ Double.valueOf(r).hashCode();
    }

    /**
     * Indicate if the interval is a subset of [0,1], i.e. can
     * be a logical value.
     *
     * @return logical flag.
     */
    @Override
    public boolean isLogical() {
        return 0 <= l && r <= 1;
    }

    /**
     * Indicate if this interval is a subset of that interval, i.e.
     * that.l <= l && r <= that.r
     *
     * @param that interval to be container
     * @return logical subset flag
     */
    @Override
    public boolean isSubset(final Interval that) {
        final double eps = 4 * Accuracy.DEPS;
        return (that.left() - eps) <= l && r <= (that.right() + eps);
    }

    /**
     * Raise an exception if not a logical interval.
     */
    private void checkLogical() {
        if (!isLogical()) {
            throw new IllegalArgumentException("not a logical " + this);
        }
    }

    /**
     * True if either the left or right part is not a number.
     *
     * @return boolean indicating the NaN status
     */
    @Override
    public boolean isNaN() {
        return Double.isNaN(r) || Double.isNaN(l);
    }

    /**
     * True if either the left or right is infinite.
     *
     * @return boolean indicating the infinite status
     */
    @Override
    public boolean isInfinite() {
        return Double.isInfinite(r) || Double.isInfinite(l);
    }

    /**
     * Indicate if this is a crisp zero interval number.
     *
     * @return zero flag
     */
    @Override
    public boolean isZero() {
        return l == 0 && r == 0;
    }

    /**
     * Indicate if this interval contains the crisp zero value, i.e. l<=0<=r.
     *
     * @return contains zero flag
     */
    @Override
    public boolean containsZero() {
        return l <= 0 && 0 <= r;
    }

    /**
     * Indicate if this interval contains the crisp value, i.e. l<=value<=r.
     *
     * @param value to contain within the interval
     * @return contains zero flag
     */
    @Override
    public boolean contains(final double value) {
        return l <= value && value <= r;
    }

    /**
     * Indicate if this is a crisp one interval number.
     *
     * @return one flag
     */
    @Override
    public boolean isOne() {
        return 1 == l && 1 == r;
    }

    /**
     * Indicate if this is a none interval, i.e. an empty
     * intersection of two intervals.
     *
     * @return null flag
     */
    @Override
    public boolean isNull() {
        return NULL == this;
    }

    /**
     * Indicate if this is an empty interval, i.e. is either
     * the null interval or has zero Borel measure and is crisp
     * singular real number.
     *
     * @return empty flag
     */
    @Override
    public boolean isEmpty() {
        if (isNull()) {
            return true;
        }
        return r <= l;
    }

    /**
     * Calculate this+that
     *
     * @param that the number to add
     * @return this + that
     */
    @Override
    @Operand(symbol = "+")
    public Interval plus(final Interval that) {
        return new IntervalNumber(l + that.left(), r + that.right());
    }

    /**
     * Calculate this + x
     *
     * @param x the real value to add
     * @return this + x
     */
    @Override
    @Operand(symbol = "+")
    public IntervalNumber plus(final double x) {
        return new IntervalNumber(l + x, r + x);
    }

    /**
     * Calculate this - that
     *
     * @param that the number to subtract
     * @return this - that
     */
    @Override
    @Operand(symbol = "-")
    public Interval minus(final Interval that) {
        return new IntervalNumber(l - that.right(), r - that.left());
    }

    /**
     * Calculate this - x
     *
     * @param x the real value to subtract
     * @return this - x
     */
    @Override
    @Operand(symbol = "-")
    public IntervalNumber minus(final double x) {
        return new IntervalNumber(l - x, r - x);
    }

    /**
     * Calculate this times that
     *
     * @param that the interval to multiply with
     * @return this * that
     */
    @Override
    @Operand(symbol = "*")
    public Interval multiply(final Interval that) {
        if (this.isNull() || that.isNull()) {
            return NULL;
        }
        final double a = l * that.left();
        final double b = r * that.right();
        final double c = l * that.right();
        final double d = r * that.left();
        final double ll = min(a, b, c, d);
        final double rr = max(a, b, c, d);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate this times x
     *
     * @param x the real number to multiply with
     * @return this * x
     */
    @Override
    @Operand(symbol = "*")
    public IntervalNumber multiply(final double x) {
        final double a = l * x;
        final double b = r * x;
        final double ll = min(a, b);
        final double rr = max(a, b);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate this divided by that
     *
     * @param that the divisor
     * @return this / that
     */
    @Override
    @Operand(symbol = "/")
    public Interval div(final Interval that) {
        double a, b, c, d, ll = 0, rr = 0;
        if (that.isZero() && !this.isZero()) {
            return NULL;
        }
        if (that.containsZero()) {
            ll = NEGATIVE_INFINITY;
            rr = POSITIVE_INFINITY;
            if (r == 0) {
                rr = 0;
            }
            if (l == 0) {
                ll = 0;
            }
            if (that.right() == 0) {
                rr = l / that.left();
            }
            if (that.left() == 0) {
                ll = r / that.right();
            }
        } else {
            a = l / that.left();
            b = r / that.right();
            c = l / that.right();
            d = r / that.left();
            ll = min(a, b, c, d);
            rr = max(a, b, c, d);
        }
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate this divided by x
     *
     * @param x the divisor
     * @return this / x
     */
    @Override
    @Operand(symbol = "/")
    public IntervalNumber div(final double x) {
        final double a = l / x;
        final double b = r / x;
        final double ll = min(a, b);
        final double rr = max(a, b);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate this to the power of that
     *
     * @param that the exponent as interval
     * @return pow(this, that)
     */
    @Override
    @Operand(symbol = "^")
    public IntervalNumber pow(final Interval that) {
        final double a = Math.pow(l, that.left());
        final double b = Math.pow(r, that.left());
        final double c = Math.pow(l, that.right());
        final double d = Math.pow(r, that.right());
        final double ll = min(a, b, c, d);
        final double rr = max(a, b, c, d);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate this to the power of x
     *
     * @param x the real exponent
     * @return pow(this, x)
     */
    @Override
    @Operand(symbol = "^")
    public IntervalNumber pow(final double x) {
        final double a = Math.pow(l, x);
        final double b = Math.pow(r, x);
        final double ll = min(a, b);
        final double rr = max(a, b);
        return new IntervalNumber(ll, rr);
    }

    /**
     * Calculate the logical AND operation of this AND that,
     * using product logic,
     *
     * @param that the number to logical AND with
     * @return this AND that
     */
    @Override
    @Operand(symbol = "&")
    public Interval and(final Interval that) {
        checkLogical();
        final IntervalNumber ret = (IntervalNumber) this.multiply(that);
        ret.checkLogical();
        return ret;
    }

    /**
     * Calculate the logical NOT operation of this.
     *
     * @return NOT this
     */
    @Override
    @Operand(symbol = "!")
    public Interval not() {
        checkLogical();
        return new IntervalNumber(1 - r, 1 - l);
    }

    /**
     * Calculate the intersection of this and that.
     *
     * @param that the number to intersect with
     * @return this intersection that
     */
    @Override
    public Interval intersection(final Interval that) {
        if (isNull() || that.isNull() || r < that.left() || that.right() < l)
            return NULL;
        final double al = Math.max(l, that.left());
        final double ar = Math.min(r, that.right());
        return new IntervalNumber(al, ar);
    }

    /**
     * Calculate the union of this and that.
     *
     * @param that the number to intersect with
     * @return this union that
     */
    @Override
    public Interval union(final Interval that) {
        if (isNull()) {
            return that;
        } else if (that.isNull()) {
            return this;
        }
        final double al = Math.min(l, that.left());
        final double ar = Math.max(r, that.right());
        return new IntervalNumber(al, ar);
    }

    /**
     * Calculate the logical OR of this OR that.
     *
     * @param that the number to or with
     * @return this OR that
     */
    @Override
    @Operand(symbol = "|")
    public Interval or(final Interval that) {
        checkLogical();
        double a, b;
        a = l + that.left() - l * that.left();
        b = r + that.right() - r * that.right();
        final IntervalNumber ret = new IntervalNumber(a, b);
        ret.checkLogical();
        return ret;
    }

    /**
     * Calculate the logical implication: from this follows that.
     *
     * @param that the number to deduce
     * @return this implies that
     */
    @Override
    public Interval implies(final Interval that) {
        checkLogical();
        double a, b;
        a = 1 - r + l * l * that.left();
        b = 1 - l + r * r * that.right();
        // double c = 1 - that.right() + l*that.left()*that.left();
        // double d = 1 - that.left() + r*that.right()*that.right();
        // double ll = min(a,b,c,d);
        // double rr = max(a,b,c,d);
        a = max(0, a);
        b = min(1, b);
        final IntervalNumber ret = new IntervalNumber(a, b);
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Interval#similarity(de.lab4inf.math.Interval)
     */
    @Override
    public double similarity(final Interval that) {
        double ret = 0;
        final Interval denominator = this.union(that);
        final Interval nominator = this.intersection(that);
        if (!denominator.isEmpty() && !nominator.isNull()) {
            ret = (nominator.right() - nominator.left()) / (denominator.right() - denominator.left());
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Interval#distance(de.lab4inf.math.Interval)
     */
    @Override
    public double distance(final Interval that) {
        return 1 - similarity(that);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Interval#abs()
     */
    @Override
    public Interval abs() {
        return newInterval(Math.abs(l), Math.abs(r));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Interval#abs2()
     */
    @Override
    public Interval abs2() {
        return newInterval(l * l, r * r);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getZero()
     */
    @Override
    public Interval getZero() {
        return ZERO;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getOne()
     */
    @Override
    public Interval getOne() {
        return ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getMinusOne()
     */
    @Override
    public Interval getMinusOne() {
        return MINUS_ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#lt(java.lang.Object)
     */
    @Override
    public boolean lt(final Interval that) {
        return this.r < that.left();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#leq(java.lang.Object)
     */
    @Override
    public boolean leq(final Interval that) {
        return this.r <= that.left();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#eq(java.lang.Object)
     */
    @Override
    public boolean eq(final Interval that) {
        return this.equals(that);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#gt(java.lang.Object)
     */
    @Override
    public boolean gt(final Interval that) {
        return this.l > that.right();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#geq(java.lang.Object)
     */
    @Override
    public boolean geq(final Interval that) {
        return this.l >= that.right();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Factory#create()
     */
    @Override
    public Interval create() {
        return new IntervalNumber();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#sqrt()
     */
    @Override
    public Interval sqrt() {
        return this.pow(0.5);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#rnd()
     */
    @Override
    public Interval rnd() {
        return new IntervalNumber(Randomizer.rndBox(-1, 1), Randomizer.rndBox(-1, 1));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#create(double)
     */
    @Override
    public Interval create(final double x) {
        return new IntervalNumber(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#difference(de.lab4inf.math.Numeric)
     */
    @Override
    public double difference(final Interval that) {
        return this.minus(that).doubleValue();
    }
}
 