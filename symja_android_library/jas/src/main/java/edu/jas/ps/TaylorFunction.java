/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.structure.RingElem;


/**
 * Interface for functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface TaylorFunction<C extends RingElem<C>> {


    /**
     * Get the factorial coefficient.
     * @return factorial coefficient.
     */
    public long getFacul();


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO();


    /**
     * Deriviative.
     * @return deriviative of this.
     */
    public TaylorFunction<C> deriviative();


    /**
     * Multi-partial deriviative.
     * @param i exponent vector.
     * @return partial deriviative of this with respect to all variables.
     */
    public TaylorFunction<C> deriviative(ExpVector i);


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    public C evaluate(C a);


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a);

}
