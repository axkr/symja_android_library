/*
 * $Id$
 */

package edu.jas.gbufd;


import edu.jas.poly.GenWordPolynomial;
import edu.jas.structure.RingElem;


/**
 * Word polynomial reduction container. Used as container for the return value
 * of normalformFactor.
 * @author Heinz Kredel
 */

public class WordPseudoReductionEntry<C extends RingElem<C>> {


    public final GenWordPolynomial<C> pol;


    public final C multiplicator;


    public WordPseudoReductionEntry(GenWordPolynomial<C> pol, C multiplicator) {
        this.pol = pol;
        this.multiplicator = multiplicator;
    }

}
