/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Value interface. Defines selector for value.
 *
 * @param C base element type
 * @author Heinz Kredel
 */
public interface Value<C extends RingElem<C>> {


    /**
     * Value getter.
     */
    public C value();


    /**
     * Test if element type is constant.
     */
    public boolean isConstant();

}
