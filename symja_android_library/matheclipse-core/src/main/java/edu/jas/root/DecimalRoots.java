/*
 * $Id$
 */

package edu.jas.root;


import java.io.Serializable;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
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
public class DecimalRoots<C extends GcdRingElem<C> & Rational> implements Serializable {


    /**
     * univariate polynomial.
     */
    public final GenPolynomial<C> p;


    /**
     * real decimal roots.
     */
    public final List<BigDecimal> real;


    /**
     * univariate polynomial with complex coefficients.
     */
    public final GenPolynomial<Complex<C>> cp;


    /**
     * complex decimal roots.
     */
    public final List<Complex<BigDecimal>> complex;


    /**
     * Constructor.
     *
     * @param p  univariate polynomial
     * @param cp univariate complex polynomial
     * @param r  list of real decimal roots
     * @param c  list of complex decimal roots
     */
    public DecimalRoots(GenPolynomial<C> p, GenPolynomial<Complex<C>> cp, List<BigDecimal> r,
                        List<Complex<BigDecimal>> c) {
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
     * @return script compatible representation for this Interval.
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer("[");
        sb.append(p.toScript());
        if (!real.isEmpty()) {
            sb.append(", real=[");
            boolean first = true;
            for (BigDecimal r : real) {
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
            for (Complex<BigDecimal> c : complex) {
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
     * Copy this.
     *
     * @return a copy of this.
     */
    public DecimalRoots<C> copy() {
        return new DecimalRoots<C>(p, cp, real, complex);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof DecimalRoots)) {
            return false;
        }
        DecimalRoots<C> a = null;
        try {
            a = (DecimalRoots<C>) b;
        } catch (ClassCastException e) {
            return false;
        }
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

}
