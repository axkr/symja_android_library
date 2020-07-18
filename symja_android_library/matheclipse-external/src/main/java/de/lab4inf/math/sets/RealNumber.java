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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import de.lab4inf.math.Operand;
import de.lab4inf.math.Real;
import de.lab4inf.math.util.Randomizer;

import static de.lab4inf.math.util.Accuracy.QEPS;

/**
 * An immutable double wrapped as a mathematical field.
 *
 * @author nwulff
 * @version $Id: RealNumber.java,v 1.9 2014/11/18 21:50:21 nwulff Exp $
 * @since 18.06.2011
 */

public class RealNumber extends Number implements Real {
    /**
     * Real epsilon constant.
     */
    public static final RealNumber EPS = new RealNumber(QEPS.divide(BigDecimal.TEN));
    /**
     * Real zero constant.
     */
    public static final RealNumber ZERO = new RealNumber(0);
    /**
     * Real unity constant.
     */
    public static final RealNumber ONE = new RealNumber(1);
    /**
     * Real minus one constant.
     */
    public static final RealNumber MINUS_ONE = new RealNumber(-1);
    private static final long serialVersionUID = 8400998180867407389L;
    // private static final MathContext MC = MathContext.DECIMAL128;
    private static final MathContext MC = new MathContext(36, RoundingMode.HALF_EVEN);
    private static final BigDecimal NAN = BigDecimal.valueOf(Double.MIN_NORMAL);
    private static final BigDecimal INFINITY = BigDecimal.valueOf(Double.MAX_VALUE);
    // private final double value;
    private final BigDecimal value;

    /**
     * Default constructor with zero value.
     */
    public RealNumber() {
        this(0);
    }

    /**
     * Construct a Real with given value.
     *
     * @param v value to assign
     */
    public RealNumber(final long v) {
        this.value = new BigDecimal(v, MC);
    }

    /**
     * Construct a Real with given value.
     *
     * @param v value to assign
     */
    public RealNumber(final double v) {
        if (Double.isInfinite(v)) {
            this.value = INFINITY;

        } else if (Double.isNaN(v)) {
            this.value = NAN;
        } else {
            this.value = new BigDecimal(v, MC);
        }
    }

    /**
     * Copy constructor for a Real.
     *
     * @param v value to copy
     */
    public RealNumber(final RealNumber v) {
        this.value = v.value;
    }

    /**
     * Copy constructor for a Real.
     *
     * @param v value to copy
     */
    public RealNumber(final BigDecimal v) {
        this.value = v;
    }

    /**
     * Wrapper method for a double.
     *
     * @param v double to decorate
     * @return Real with value v
     */
    public static RealNumber asReal(final double v) {
        return new RealNumber(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Ring#isOne()
     */
    @Override
    public boolean isOne() {
        // return Double.compare(value, 1.0) == 0;
        return BigDecimal.ONE.equals(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Ring#multiply(java.lang.Object)
     */
    @Operand(symbol = "*")
    @Override
    public RealNumber multiply(final Real that) {
        // return multiply(that.getValue());
        return new RealNumber(this.value.multiply(((RealNumber) that).value, MC));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#multiply(double)
     */
    @Override
    public RealNumber multiply(final double that) {
        // return new RealNumber(value.*that);
        return multiply(new RealNumber(that));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Group#isZero()
     */
    @Override
    public boolean isZero() {
        // return Double.compare(value, 0.0) == 0;
        return BigDecimal.ZERO.equals(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Group#plus(java.lang.Object)
     */
    @Operand(symbol = "+")
    @Override
    public RealNumber plus(final Real that) {
        // return plus(that.getValue());
        return new RealNumber(this.value.add(((RealNumber) that).value, MC));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#plus(double)
     */
    @Override
    public RealNumber plus(final double that) {
        // return new RealNumber(value+that);
        return plus(new RealNumber(that));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Field#minus(java.lang.Object)
     */
    @Operand(symbol = "-")
    @Override
    public RealNumber minus(final Real that) {
        // return minus(that.getValue());
        return new RealNumber(this.value.subtract(((RealNumber) that).value, MC));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#minus(double)
     */
    @Override
    public RealNumber minus(final double that) {
        // return new RealNumber(value-that);
        return minus(new RealNumber(that));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Field#div(java.lang.Object)
     */
    @Operand(symbol = "/")
    @Override
    public RealNumber div(final Real that) {
        // return div(that.getValue());
        return new RealNumber(this.value.divide(((RealNumber) that).value, MC));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#div(double)
     */
    @Override
    public RealNumber div(final double that) {
        // return new RealNumber(value/that);
        return div(new RealNumber(that));
    }

    /**
     * Get the absolute value of of this real.
     *
     * @return the abs(this)
     */
    @Override
    public RealNumber abs() {
        // if(value<0) return new RealNumber(-value);
        if (value.compareTo(ZERO.value) < 0)
            return new RealNumber(value.negate());
        return this;
    }

    /**
     * Get the absolute value of of this real.
     *
     * @return the abs(this)
     */
    @Override
    public double absValue() {
        // if(value<0) return -value;
        // return value;
        return abs().doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%g", value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#equals(java.lang.Object)
     */
    @Operand(symbol = "==")
    @Override
    public boolean equals(final Object obj) {
        if (null == obj)
            return false;
        if (this == obj)
            return true;
        if (getClass() == obj.getClass()) {
            final RealNumber anOther = (RealNumber) obj;
            // double aoValue = anOther.value;
            // return Accuracy.hasReachedAccuracy(value, aoValue,1E-14);
            // return this.value.equals(anOther.value);
            return this.minus(anOther).abs().leq(EPS);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#hashCode()
     */
    @Override
    public int hashCode() {
        // return Double.valueOf(value).hashCode();
        return value.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        RealNumber cl = null;
        try {
            cl = (RealNumber) super.clone();
        } catch (final CloneNotSupportedException ce) {
            // can't be...
            cl = new RealNumber(this);
        }
        return cl;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    @Operand(symbol = "<")
    public int compareTo(final Real that) {
        // return value.compareTo(((RealNumber)that).value)<0;
        return value.compareTo(((RealNumber) that).value);
    }

    /**
     * Compare this to anOther
     *
     * @param anOther to compare to
     * @return ordering
     */
    @Override
    @Operand(symbol = "<")
    public int compareTo(final Double anOther) {
        // return Double.compare(value, anOther);
        return Double.compare(getValue(), anOther);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#getValue()
     */
    @Override
    public double getValue() {
        return value.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#newReal(java.lang.Double)
     */
    @Override
    public RealNumber newReal(final Double v) {
        return asReal(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Real#newReal(double)
     */
    @Override
    public RealNumber newReal(final double v) {
        return asReal(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return value.intValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return value.longValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return value.floatValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getZero()
     */
    @Override
    public Real getZero() {
        return ZERO;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getOne()
     */
    @Override
    public Real getOne() {
        return ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getMinusOne()
     */
    @Override
    public Real getMinusOne() {
        return MINUS_ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#lt(java.lang.Object)
     */
    @Override
    public boolean lt(final Real that) {
        // return value < that.doubleValue();
        return value.compareTo(((RealNumber) that).value) < 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#leq(java.lang.Object)
     */
    @Override
    public boolean leq(final Real that) {
        // return value <= that.doubleValue();
        return value.compareTo(((RealNumber) that).value) <= 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#eq(java.lang.Object)
     */
    @Override
    public boolean eq(final Real that) {
        return this.equals(that);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#gt(java.lang.Object)
     */
    @Override
    public boolean gt(final Real that) {
        // return value > that.doubleValue();
        return value.compareTo(((RealNumber) that).value) > 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#geq(java.lang.Object)
     */
    @Override
    public boolean geq(final Real that) {
        // return value >= that.doubleValue();
        return value.compareTo(((RealNumber) that).value) >= 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Factory#create()
     */
    @Override
    public Real create() {
        return new RealNumber();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#sqrt()
     */
    @Override
    public Real sqrt() {
        // if(value<0) throw new IllegalArgumentException(String.format("%f is less zero",value));
        // return new RealNumber(Math.sqrt(value));
        RealNumber delta, t, y = this;
        final RealNumber two = new RealNumber(2);
        do {
            t = (y.plus(this.div(y))).div(two);
            // delta = t.multiply(t).minus(this);
            delta = t.minus(y);
            y = t;
        } while (delta.abs().gt(EPS));
        return y;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#rnd()
     */
    @Override
    public Real rnd() {
        return new RealNumber(Randomizer.rndBox(-1, 1));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#create(double)
     */
    @Override
    public Real create(final double x) {
        return new RealNumber(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#difference(de.lab4inf.math.Numeric)
     */
    @Override
    public double difference(final Real that) {
        return this.minus(that).doubleValue();
    }

}
 