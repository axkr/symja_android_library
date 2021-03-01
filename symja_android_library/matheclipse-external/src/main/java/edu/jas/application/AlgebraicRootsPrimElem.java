/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.List;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Complex;
import edu.jas.root.AlgebraicRoots;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.ComplexAlgebraicNumber;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the real and complex algebraic roots of a univariate
 * polynomial together with primitive element.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class AlgebraicRootsPrimElem<C extends GcdRingElem<C> & Rational> 
             extends AlgebraicRoots<C> implements Serializable {


    /**
     * Primitive Element algebraic roots.
     */
    public final PrimitiveElement<C> pelem;


    /**
     * Roots of unity of primitive element origin representations.
     */
    public final List<AlgebraicNumber<C>> runit;


    /**
     * Constructor not for use.
     */
    protected AlgebraicRootsPrimElem() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param p univariate polynomial
     * @param cp univariate polynomial with compelx coefficients
     * @param r list of real algebraic roots
     * @param c list of complex algebraic roots
     * @param pe primitive element
     * @param ru roots of unity of primitive element origin representations
     */
    public AlgebraicRootsPrimElem(GenPolynomial<C> p, GenPolynomial<Complex<C>> cp, 
                    List<RealAlgebraicNumber<C>> r,
                    List<ComplexAlgebraicNumber<C>> c,
                    PrimitiveElement<C> pe, List<AlgebraicNumber<C>> ru) {
        super(p, cp, r, c);
        this.pelem = pe;
        this.runit = ru;
    }


    /**
     * Constructor.
     * @param ar algebraic roots container
     * @param pe primitive element
     */
    public AlgebraicRootsPrimElem(AlgebraicRoots<C> ar, PrimitiveElement<C> pe) {
        this(ar.p, ar.cp, ar.real, ar.complex, pe, null); 
    }


    /**
     * Constructor.
     * @param ar algebraic roots container
     * @param pe primitive element
     * @param ru roots of unity of primitive element origin representations
     */
    public AlgebraicRootsPrimElem(AlgebraicRoots<C> ar, PrimitiveElement<C> pe, List<AlgebraicNumber<C>> ru) {
        this(ar.p, ar.cp, ar.real, ar.complex, pe, ru);
    }


    /**
     * String representation of AlgebraicRootsPrimElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (runit == null) {
            return super.toString();
        }
        return super.toString() + ", " + runit.toString();
        //return "[" + p + ", real=" + real + ", complex=" + complex + ", " + pelem + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Interval.
     */
    public String toScript() {
        // any case
        StringBuffer sb = new StringBuffer(super.toScript());
        if (runit == null) {
            return sb.toString();
        }
        sb.append(" [");
        boolean first = true;
        for (AlgebraicNumber<C> a : runit) {
             if (first) {
                 first = false;
             } else {
                 sb.append(", ");
             }
             sb.append(a.toScript());
        }
        sb.append("] ");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Interval.
     */
    public String toDecimalScript() {
        // any case
        return super.toDecimalScript();
    }


    /**
     * Copy this.
     * @return a copy of this.
     */
    public AlgebraicRootsPrimElem<C> copy() {
        return new AlgebraicRootsPrimElem<C>(p, cp, real, complex, pelem, runit);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof AlgebraicRootsPrimElem)) {
            return false;
        }
        AlgebraicRootsPrimElem<C> a = null;
        try {
            a = (AlgebraicRootsPrimElem<C>) b;
        } catch (ClassCastException e) {
            return false;
        }
                                  // not realy required, since depends on A, B
        return super.equals(a) && pelem.equals(a.pelem) && runit.equals(a.runit);
    }


    /**
     * Hash code for this AlgebraicRootsPrimElem.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
                                               // not realy required, since depends on A, B
        return (161 * super.hashCode() + 37) * pelem.hashCode() + runit.hashCode();
    }

}
