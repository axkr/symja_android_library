/*
 * $Id$
 */

package edu.jas.gbufd;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial reduction container.
 * Used as container for the return value of normalformFactor.
 * @author Heinz Kredel
 */

public class PseudoReductionEntry<C extends RingElem<C>> {

    public final GenPolynomial<C> pol;
    public final C multiplicator;

    public PseudoReductionEntry(GenPolynomial<C> pol,
                                C multiplicator) {
        this.pol = pol;
        this.multiplicator = multiplicator;
    }

}
