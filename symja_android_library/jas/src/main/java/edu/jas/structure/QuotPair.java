/*
 * $Id: QuotPair.java 4669 2013-10-20 17:11:16Z kredel $
 */

package edu.jas.structure;


/**
 * Quotient pair interface. 
 * Defines selectors for numerator and denominator.
 * @param C base element type
 * @author Heinz Kredel
 */
public interface QuotPair<C extends RingElem<C>> {


    /**
     * Numerator.
     */
    public C numerator();


    /**
     * Denominator.
     */
    public C denominator();


    /**
     * Test if element type is constant.
     */
    public boolean isConstant();

}
