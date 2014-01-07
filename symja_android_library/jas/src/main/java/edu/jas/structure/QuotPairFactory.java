/*
 * $Id: QuotPairFactory.java 4669 2013-10-20 17:11:16Z kredel $
 */

package edu.jas.structure;


/**
 * Quotient pair factory interface. 
 * Defines constructors from numerators and denominators.
 * @param C base element type
 * @param D result element type
 * @author Heinz Kredel
 */
public interface QuotPairFactory<C extends RingElem<C>, D extends RingElem<D>> {


    /**
     * Create from numerator.
     */
    public D create(C n);


    /**
     * Create from numerator, denominator pair.
     */
    public D create(C n, C d);


    /**
     * Factory for base elements.
     */
    public RingFactory<C> pairFactory();

}
