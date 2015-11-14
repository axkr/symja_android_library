/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Polynomial functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class PolynomialTaylorFunction<C extends RingElem<C>> implements TaylorFunction<C> {


    final GenPolynomial<C> pol;


    final long facul;


    public PolynomialTaylorFunction(GenPolynomial<C> p) {
        this(p, 1L);
    }


    public PolynomialTaylorFunction(GenPolynomial<C> p, long f) {
        pol = p;
        facul = f;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        return pol.toString();
    }


    /**
     * Get the factorial coefficient.
     * @return factorial coefficient.
     */
    @Override
    public long getFacul() {
        return facul;
    }


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO() {
        return pol.isZERO();
    }


    /**
     * Deriviative.
     * @return deriviative of this.
     */
    @Override
    public TaylorFunction<C> deriviative() {
        return new PolynomialTaylorFunction<C>(PolyUtil.<C> baseDeriviative(pol));
    }


    /*
     * Partial deriviative.
     * @param r index of the variable.
     * @return partial deriviative of this with respect to variable r.
    public TaylorFunction<C> deriviative(int r) {
        return new PolynomialTaylorFunction<C>(PolyUtil. <C> baseDeriviative(pol,r)); 
    }
     */


    /**
     * Multi-partial deriviative.
     * @param i exponent vector.
     * @return partial deriviative of this with respect to all variables.
     */
    public TaylorFunction<C> deriviative(ExpVector i) {
        GenPolynomial<C> p = pol;
        long f = 1L;
        if (i.signum() == 0 || pol.isZERO()) {
            return new PolynomialTaylorFunction<C>(p, f);
        }
        for (int j = 0; j < i.length(); j++) {
            long e = i.getVal(j);
            if (e == 0) {
                continue;
            }
            int jl = i.length() - 1 - j;
            for (long k = 0; k < e; k++) {
                p = PolyUtil.<C> baseDeriviative(p, jl);
                f *= (k + 1);
                if (p.isZERO()) {
                    return new PolynomialTaylorFunction<C>(p, f);
                }
            }
        }
        //System.out.println("i = " + i + ", f = " + f + ", der = " + p);
        return new PolynomialTaylorFunction<C>(p, f);
    }


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    @Override
    public C evaluate(C a) {
        return PolyUtil.<C> evaluateMain(pol.ring.coFac, pol, a);
    }


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a) {
        return PolyUtil.<C> evaluateAll(pol.ring.coFac, pol.ring, pol, a);
    }

}
