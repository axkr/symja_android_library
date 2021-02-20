/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Unary functor interface.
 * @param <C> element type
 * @param <D> element type
 * @author Heinz Kredel
 */

public interface UnaryFunctor<C extends Element<C>, D extends Element<D>> {


    /**
     * Evaluate.
     * @return evaluated element.
     */
    public D eval(C c);

}
