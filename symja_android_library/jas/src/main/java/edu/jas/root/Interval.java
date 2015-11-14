/*
 * $Id$
 */

package edu.jas.root;


import java.io.Serializable;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Interval. For example isolating interval for real roots.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class Interval<C extends RingElem<C> & Rational > implements Serializable { //findbugs


    /**
     * left interval border.
     */
    public final C left;


    /**
     * right interval border.
     */
    public final C right;


    /**
     * Constructor.
     * @param left interval border.
     * @param right interval border.
     */
    public Interval(C left, C right) {
        this.left = left;
        this.right = right;
    }


    /**
     * Constructor.
     * @param mid left and right interval border.
     */
    public Interval(C mid) {
        this(mid, mid);
    }


    /**
     * String representation of Interval.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + left + ", " + right + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Interval.
     */
    public String toScript() {
        // Python case
        return "[ " + left.toScript() + ", " + right.toScript() + " ]";
    }


    /**
     * Copy this.
     * @return a copy of this.
     */
    public Interval<C> copy() {
        return new Interval<C>(left, right);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof Interval)) {
            return false;
        }
        Interval<C> a = null;
        try {
            a = (Interval<C>) b;
        } catch (ClassCastException e) {
            return false;
        }
        return left.equals(a.left) && right.equals(a.right);
    }


    /**
     * Hash code for this Interval.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * left.hashCode() + right.hashCode();
    }


    /**
     * Test if an element is contained in this interval.
     * @param c element to test.
     * @return true, if left <= b <= right;
     */
    public boolean contains(C c) {
        return left.compareTo(c) <= 0 && c.compareTo(right) <= 0;
    }


    /**
     * Test if an interval is contained in this interval.
     * @param vc interval to test.
     * @return true, if left <= vc.left and vc.right <= right;
     */
    public boolean contains(Interval<C> vc) {
        return contains(vc.left) && contains(vc.right);
    }


    /**
     * Length.
     * @return |left-right|;
     */
    public C length() {
        C m = right.subtract(left);
        return m.abs();
    }


    /**
     * BigRational Length.
     * @return |left-right|;
     */
    public BigRational rationalLength() {
        return length().getRational();
    }


    /**
     * BigDecimal representation of Interval.
     */
    public BigDecimal toDecimal() {
        BigDecimal l = new BigDecimal(left.getRational());
        BigDecimal r = new BigDecimal(right.getRational());
        BigDecimal two = new BigDecimal(2);
        BigDecimal v = l.sum(r).divide(two);
        return v;
    }


    /**
     * Rational middle point.
     * @return (left+right)/2;
     */
    public BigRational rationalMiddle() {
        BigRational m = left.getRational().sum(right.getRational());
        BigRational t = new BigRational(1L,2L);
        m = m.multiply(t);
        return m;
    }


    /**
     * Middle point.
     * @return (left+right)/2;
     */
    public C middle() {
        C m = left.sum(right);
        C h = left.factory().parse("1/2");
        m = m.multiply(h);
        return m;
    }


    /**
     * Random point of interval.
     * @return a random point contained in this interval.
     */
    public C randomPoint() {
        C dr = right.subtract(left);
        RingFactory<C> fac = (RingFactory<C>)dr.factory();
        C r = fac.random(13);
        r = r.abs();
        if ( !r.isZERO() ) {
            if ( r.compareTo( fac.getONE() ) > 0 ) {
                r = r.inverse();
            }
        }
        // 0 <= r <= 1
        dr = dr.multiply(r);
        C rv = left.sum(dr);
        //System.out.println("rv   = " + new BigDecimal(rv.getRational()) );
        return rv;
    }

}
