/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;

import edu.jas.gb.SolvableReduction;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction interface. Defines additionally normalformFactor
 * and normalformRecursive.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public interface SolvablePseudoReduction<C extends RingElem<C>> extends SolvableReduction<C> {


    /**
     * Left normalform with multiplication factor.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
     */
    public PseudoReductionEntry<C> leftNormalformFactor(List<GenSolvablePolynomial<C>> Pp,
                    GenSolvablePolynomial<C> Ap);


    /*
     * Right normalform with multiplication factor.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
    public PseudoReductionEntry<C> rightNormalformFactor(List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap);
     */


    /**
     * Left normalform recursive.
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<GenPolynomial<C>> leftNormalformRecursive(
                    List<GenSolvablePolynomial<GenPolynomial<C>>> Pp,
                    GenSolvablePolynomial<GenPolynomial<C>> Ap);


    /*
     * Right normalform recursive.
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
    public GenSolvablePolynomial<GenPolynomial<C>> rightNormalformRecursive(List<GenSolvablePolynomial<GenPolynomial<C>>> Pp, GenSolvablePolynomial<GenPolynomial<C>> Ap);
     */

}
