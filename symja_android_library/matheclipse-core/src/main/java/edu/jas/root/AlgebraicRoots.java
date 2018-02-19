/*
 * $Id$
 */

package edu.jas.root;


import java.io.Serializable;
import java.util.List;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the real and complex algebraic roots of a univariate
 * polynomial.
 *
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class AlgebraicRoots<C extends GcdRingElem<C> & Rational> implements Serializable {


    /**
     * Univariate polynomial.
     */
    public final GenPolynomial<C> p;


    /**
     * Real algebraic roots.
     */
    public final List<RealAlgebraicNumber<C>> real;


    /**
     * Univariate polynomial with complex coefficients equivalent to p.
     */
    public final GenPolynomial<Complex<C>> cp;


    /**
     * Complex algebraic roots.
     */
    public final List<ComplexAlgebraicNumber<C>> complex;


    /**
     * Constructor not for use.
     */
    protected AlgebraicRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     *
     * @param p  univariate polynomial
     * @param cp univariate polynomial with compelx coefficients
     * @param r  list of real algebraic roots
     * @param c  list of complex algebraic roots
     */
    public AlgebraicRoots(GenPolynomial<C> p, GenPolynomial<Complex<C>> cp, List<RealAlgebraicNumber<C>> r,
                          List<ComplexAlgebraicNumber<C>> c) {
        this.p = p;
        this.cp = cp;
        this.real = r;
        this.complex = c;
    }


    /**
     * String representation of AlgebraicRoots.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "[" + p + ", real=" + real + ", complex=" + complex + "]";
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this roots.
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer("[");
        sb.append(p.toScript());
        //sb.append(", ");
        //sb.append(cp.toScript());
        if (!real.isEmpty()) {
            sb.append(", real=[");
            boolean first = true;
            for (RealAlgebraicNumber<C> r : real) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(r.toScript());
            }
            sb.append("]");
        }
        if (!complex.isEmpty()) {
            sb.append(", complex=[");
            boolean first = true;
            for (ComplexAlgebraicNumber<C> c : complex) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(c.toScript());
            }
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Get a decimal number scripting compatible string representation.
     *
     * @return decimal number script compatible representation for this roots.
     */
    public String toDecimalScript() {
        // Python case
        StringBuffer sb = new StringBuffer("[");
        sb.append(p.toScript());
        //sb.append(", ");
        //sb.append(cp.toScript());
        if (!real.isEmpty()) {
            sb.append(", real=[");
            boolean first = true;
            for (RealAlgebraicNumber<C> r : real) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(r.ring.root.toDecimal().toScript());
            }
            sb.append("]");
        }
        if (!complex.isEmpty()) {
            sb.append(", complex=[");
            boolean first = true;
            for (ComplexAlgebraicNumber<C> c : complex) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(c.ring.root.getDecimalCenter().toScript());
            }
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Copy this.
     *
     * @return a copy of this.
     */
    public AlgebraicRoots<C> copy() {
        return new AlgebraicRoots<C>(p, cp, real, complex);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof AlgebraicRoots)) {
            return false;
        }
        AlgebraicRoots<C> a = null;
        try {
            a = (AlgebraicRoots<C>) b;
        } catch (ClassCastException e) {
            return false;
        }
        // && cp.equals(a.cp)
        return p.equals(a.p) && real.equals(a.real) && complex.equals(a.complex);
    }


    /**
     * Hash code for this AlgebraicRoots.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (161 * p.hashCode() + 37) * real.hashCode() + complex.hashCode();
    }


    /**
     * Algebraic number ring.
     *
     * @return algebraic ring of roots.
     */
    public AlgebraicNumberRing<C> getAlgebraicRing() {
        AlgebraicNumberRing<C> anr = new AlgebraicNumberRing<C>(p);
        return anr;
    }
}
