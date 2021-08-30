/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;

import edu.jas.gb.Reduction;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction interface. Defines additionaly normalformFactor.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public interface PseudoReduction<C extends RingElem<C>> extends Reduction<C> {


    /**
     * Normalform with multiplication factor.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
     */
    public PseudoReductionEntry<C> normalformFactor(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap);

    /**
     * Normalform recursive.
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    public GenPolynomial<GenPolynomial<C>> normalformRecursive(List<GenPolynomial<GenPolynomial<C>>> Pp, GenPolynomial<GenPolynomial<C>> Ap);

}
