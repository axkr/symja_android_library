/*
 * $Id: StarRingElem.java 4056 2012-07-26 17:44:13Z kredel $
 */

package edu.jas.structure;


/**
 * Star ring element interface. Defines norm and conjugation.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface StarRingElem<C extends StarRingElem<C>> extends RingElem<C> {


    /**
     * Conjugate of this.
     * @return conj(this).
     */
    public C conjugate();


    /**
     * Norm of this.
     * @return norm(this).
     */
    public C norm();

}
