/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Abelian group factory interface. Defines get zero.
 * @author Heinz Kredel
 */

public interface AbelianGroupFactory<C extends AbelianGroupElem<C>> extends ElemFactory<C> {


    /**
     * Get the constant zero for the AbelianGroupElem.
     * @return 0.
     */
    public C getZERO();


}
