/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

import de.lab4inf.math.Interval;
import de.lab4inf.math.Operand;

import static de.lab4inf.math.util.Accuracy.isSimilar;

/**
 * Constraint interval implementation with borders [a,b]
 * but a constrained set theoretic definition.</br>
 * <pre>
 * 	 W.A. Lodwick: "Constrained Interval Arithmetic",
 * 	 University of Colorado at Denver, 1999
 * </pre>
 *
 * @author nwulff
 * @version $Id: ConstraintInterval.java,v 1.7 2014/09/18 17:06:25 nwulff Exp $
 * @since 02.03.2011
 */
public class ConstraintInterval extends IntervalNumber {
    /**
     * Reference to ConstrainedInterval.java.
     */
    private static final long serialVersionUID = 449810611377854873L;
    private static final ConstraintInterval MINUSONE = new ConstraintInterval(-1);

    /**
     * Constructor for a crisp singleton interval.
     *
     * @param value the crisp left==right borders.
     */
    public ConstraintInterval(final double value) {
        super(value);
    }

    /**
     * Constructor with interval borders.
     *
     * @param left  border of interval
     * @param right border of interval
     */
    public ConstraintInterval(final double left, final double right) {
        super(left, right);
    }

    /**
     * Copy constructor.
     *
     * @param v interval number to copy
     */
    public ConstraintInterval(final Interval v) {
        this(v.left(), v.right());
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Interval#newInterval(double, double)
     */
    @Override
    public Interval newInterval(final double left, final double right) {
        return new ConstraintInterval(left, right);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Interval clone() {
        //return new ConstraintInterval(this);
        return (Interval) super.clone();
    }

    /**
     * Check if this is nearly equals to that
     *
     * @param that the interval to compare to
     * @return this ~= that
     */
    protected boolean isSame(final Interval that) {
        return isSimilar(r, that.right()) && isSimilar(l, that.left());
    }

    /**
     * Check if this is nearly equals to -that
     *
     * @param that the interval to compare to
     * @return this ~= -that
     */
    protected boolean isNegative(final Interval that) {
        return isSimilar(-r, that.left()) && isSimilar(-l, that.right());
    }

    /**
     * Check if this is nearly the inverse to that
     *
     * @param that the interval to compare to
     * @return this ~= 1/that
     */
    protected boolean isInverse(final Interval that) {
        return isSimilar(l * that.right(), 1) && isSimilar(r * that.left(), 1);
    }

    /**
     * Check if this is nearly the negative inverse to that
     *
     * @param that the interval to compare to
     * @return this ~= -1/that
     */
    protected boolean isNegativeInverse(final Interval that) {
        return isSimilar(l * that.left(), -1) && isSimilar(r * that.right(), -1);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.sets.IntervalNumber#plus(de.lab4inf.math.Interval)
     */
    @Override
    @Operand(symbol = "+")
    public Interval plus(final Interval that) {
        Interval ret;
        if (isSame(that)) {
            ret = new ConstraintInterval(2 * l, 2 * r);
        } else if (isNegative(that)) {
            ret = ZERO;
        } else {
            ret = super.plus(that);
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.sets.IntervalNumber#minus(de.lab4inf.math.Interval)
     */
    @Override
    @Operand(symbol = "-")
    public Interval minus(final Interval that) {
        Interval ret;
        if (isSame(that)) {
            ret = ZERO;
        } else if (isNegative(that)) {
            ret = new ConstraintInterval(2 * l, 2 * r);
        } else {
            ret = super.minus(that);
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.sets.IntervalNumber#div(de.lab4inf.math.Interval)
     */
    @Override
    @Operand(symbol = "/")
    public Interval div(final Interval that) {
        Interval ret;
        if (isSame(that)) {
            ret = ONE;
        } else if (isNegative(that)) {
            ret = MINUSONE;
        } else {
            ret = super.div(that);
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.sets.IntervalNumber#multiply(de.lab4inf.math.Interval)
     */
    @Override
    @Operand(symbol = "*")
    public Interval multiply(final Interval that) {
        Interval ret;
        if (isInverse(that)) {
            ret = ONE;
        } else if (isNegativeInverse(that)) {
            ret = MINUSONE;
        } else {
            ret = super.multiply(that);
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Numeric#create(double)
     */
    @Override
    public Interval create(final double x) {
        return new ConstraintInterval(x);
    }

}
 