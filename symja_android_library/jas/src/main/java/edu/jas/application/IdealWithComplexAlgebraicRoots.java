/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
//import edu.jas.root.RealAlgebraicNumber;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;


/**
 * Container for Ideals together with univariate polynomials and complex algebraic
 * roots.
 * @author Heinz Kredel
 */
public class IdealWithComplexAlgebraicRoots<D extends GcdRingElem<D> & Rational>
        extends IdealWithUniv<D> {


    /**
     * The list of complex algebraic roots.
     */
    public final List<List<Complex<RealAlgebraicNumber<D>>>> can;


    /**
     * The list of decimal approximations of the complex algebraic roots.
     */
    protected List<List<Complex<BigDecimal>>> droots = null;


    /**
     * Constructor not for use.
     */
    protected IdealWithComplexAlgebraicRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     * @param cr the list of complex algebraic roots
     */
    public IdealWithComplexAlgebraicRoots(Ideal<D> id, List<GenPolynomial<D>> up,
            List<List<Complex<RealAlgebraicNumber<D>>>> cr) {
        super(id, up);
        can = cr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param cr the list of real algebraic roots
     */
    public IdealWithComplexAlgebraicRoots(IdealWithUniv<D> iu, List<List<Complex<RealAlgebraicNumber<D>>>> cr) {
        super(iu.ideal, iu.upolys);
        can = cr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString() + "\ncomplex roots:\n");
        sb.append("[");
        boolean f1 = true;
        for (List<Complex<RealAlgebraicNumber<D>>> lr : can) {
            if (!f1) {
                sb.append(", ");
            } else {
                f1 = false;
            }
            sb.append("[");
            boolean f2 = true;
            for (Complex<RealAlgebraicNumber<D>> rr : lr) {
                if (!f2) {
                    sb.append(", ");
                } else {
                    f2 = false;
                }
                sb.append(rr.ring.toScript());
            }
            sb.append("]");
        }
        sb.append("]");
        if (droots != null) {
            sb.append("\ndecimal complex root approximation:\n");
            for (List<Complex<BigDecimal>> d : droots) {
                sb.append(d.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return super.toScript() + ",  " + can.toString();
    }


    /**
     * Get decimal approximation of the real root tuples.
     */
    public synchronized List<List<Complex<BigDecimal>>> decimalApproximation() {
        if (this.droots != null) {
            return droots;
        }
        List<List<Complex<BigDecimal>>> rroots = new ArrayList<List<Complex<BigDecimal>>>();
        ComplexRing<BigDecimal> cfac = new ComplexRing<BigDecimal>(new BigDecimal());
        for (List<Complex<RealAlgebraicNumber<D>>> rri : this.can) {
            List<Complex<BigDecimal>> r = new ArrayList<Complex<BigDecimal>>();
            for (Complex<RealAlgebraicNumber<D>> rr : rri) {
                BigDecimal dr = new BigDecimal(rr.getRe().magnitude());
                BigDecimal di = new BigDecimal(rr.getIm().magnitude());
                Complex<BigDecimal> d = new Complex<BigDecimal>(cfac,dr,di);
                r.add(d);
            }
            rroots.add(r);
        }
        droots = rroots;
        return rroots;
    }


    /**
     * compute decimal approximation of the real root tuples.
     */
    public void doDecimalApproximation() {
        List<List<Complex<BigDecimal>>> unused = decimalApproximation();
        if ( unused.isEmpty() ) { // use for findbugs
            System.out.println("unused is empty");
        }
        return;
    }

}
