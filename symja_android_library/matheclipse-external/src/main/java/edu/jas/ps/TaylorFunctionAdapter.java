/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.structure.RingElem;


/**
 * Adapter for functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public abstract class TaylorFunctionAdapter<C extends RingElem<C>> implements TaylorFunction<C> {


    /**
     * Get the factorial coefficient.
     * @return factorial coefficient.
     */
    public long getFacul() {
        return 1L;
    }


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO() {
        throw new UnsupportedOperationException("not implemented");
    }



    /**
     * Deriviative.
     * @return deriviative of this.
     */
    public TaylorFunction<C> deriviative() {
        throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Multi-partial deriviative.
     * @param i exponent vector.
     * @return partial deriviative of this with respect to all variables.
     */
    public TaylorFunction<C> deriviative(ExpVector i) {
        throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    public C evaluate(C a) {
        throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a) {
        throw new UnsupportedOperationException("not implemented");
    }

}
