/*
 * $Id: Value.java 5204 2015-04-05 10:30:15Z kredel $
 */

package edu.jas.structure;


/**
 * Value interface. Defines selector for value.
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
