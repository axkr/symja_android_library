/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;

import edu.jas.gb.WordReduction;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction interface. Defines additionally normalformFactor
 * and normalformRecursive.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public interface WordPseudoReduction<C extends RingElem<C>> extends WordReduction<C> {


    /**
     * Left normalform with multiplication factor.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
     */
    public WordPseudoReductionEntry<C> normalformFactor(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap);


    /**
     * Left normalform recursive.
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    public GenWordPolynomial<GenPolynomial<C>> normalformRecursive(
                    List<GenWordPolynomial<GenPolynomial<C>>> Pp, GenWordPolynomial<GenPolynomial<C>> Ap);


}
