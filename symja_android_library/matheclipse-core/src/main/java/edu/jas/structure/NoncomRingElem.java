/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Non-commutative ring element interface. Defines right divide and right remainder.
 *
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface NoncomRingElem<C extends NoncomRingElem<C>> extends RingElem<C> {


    /**
     * Right division.
     *
     * @param a element.
     * @return right, with a * right = this
     */
    public C rightDivide(C a);


    /**
     * Right remainder.
     *
     * @param a element.
     * @return r = this - a * (a/right), where a * right = this.
     */
    public C rightRemainder(C a);


    /**
     * Two-sided division.
     *
     * @param a element.
     * @return [left, right], with left * a * right = this
     */
    public C[] twosidedDivide(C a);


    /**
     * Two-sided remainder.
     *
     * @param a element.
     * @return r = this - (a/left) * a * (a/right), where left * a * right = this.
     */
    public C twosidedRemainder(C a);

}
