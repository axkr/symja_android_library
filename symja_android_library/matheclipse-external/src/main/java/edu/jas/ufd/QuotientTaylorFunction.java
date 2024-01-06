/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.ps.TaylorFunction;
import edu.jas.structure.GcdRingElem;


/**
 * Polynomial quotient functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class QuotientTaylorFunction<C extends GcdRingElem<C>> implements TaylorFunction<C> {


    final Quotient<C> quo;


    final long facul;


    public QuotientTaylorFunction(Quotient<C> q) {
        this(q, 1L);
    }


    public QuotientTaylorFunction(Quotient<C> q, long f) {
        quo = q;
        facul = f;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        return quo.toString();
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
        return quo.isZERO();
    }


    /**
     * Derivative.
     * @return derivative of this.
     */
    @Override
    public TaylorFunction<C> derivative() {
        return new QuotientTaylorFunction<C>(PolyUfdUtil.<C> derivative(quo));
    }


    /**
     * Partial derivative.
     * @param r index of the variable.
     * @return partial derivative of this with respect to variable r.
     */
    public TaylorFunction<C> derivative(int r) {
        return new QuotientTaylorFunction<C>(PolyUfdUtil.<C> derivative(quo, r));
    }


    /**
     * Multi-partial derivative.
     * @param i exponent vector.
     * @return partial derivative of this with respect to all variables.
     */
    public TaylorFunction<C> derivative(ExpVector i) {
        Quotient<C> q = quo;
        long f = 1L;
        if (i.signum() == 0 || quo.isZERO()) {
            return new QuotientTaylorFunction<C>(q, f);
        }
        for (int j = 0; j < i.length(); j++) {
            long e = i.getVal(j);
            if (e == 0) {
                continue;
            }
            int jl = i.length() - 1 - j;
            for (long k = 0; k < e; k++) {
                q = PolyUfdUtil.<C> derivative(q, jl);
                f *= (k + 1);
                if (q.isZERO()) {
                    return new QuotientTaylorFunction<C>(q, f);
                }
            }
        }
        //System.out.println("i = " + i + ", f = " + f + ", der = " + q);
        return new QuotientTaylorFunction<C>(q, f);
    }


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    @Override
    public C evaluate(C a) {
        C e = PolyUfdUtil.<C> evaluateMain(quo.ring.ring.coFac, quo, a);
        return e;
    }


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a) {
        return PolyUfdUtil.<C> evaluateAll(quo.ring.ring.coFac, quo, a);
    }

}
