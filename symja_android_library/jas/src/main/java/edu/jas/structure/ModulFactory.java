/*
 * $Id: ModulFactory.java 4056 2012-07-26 17:44:13Z kredel $
 */

package edu.jas.structure;


import java.util.List;


/**
 * Module factory interface. Defines conversion from list and sparse random.
 * @param <M> module type
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public interface ModulFactory<M extends ModulElem<M, C>, C extends RingElem<C>> extends
                AbelianGroupFactory<M> {


    /**
     * Convert list to module.
     * @param v list of ring elements.
     * @return a module element with the elements from v.
     */
    public M fromList(List<C> v);


    /**
     * Random vector.
     * @param k size of coefficients.
     * @param q fraction of non zero elements.
     * @return a random vector.
     */
    public M random(int k, float q);

}
