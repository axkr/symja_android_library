/*
 * $Id: RPseudoReduction.java 3423 2010-12-24 10:56:50Z kredel $
 */

package edu.jas.gbufd;


import edu.jas.structure.RegularRingElem;


/**
 * Polynomial R pseudo reduction interface. Combines RReduction and
 * PseudoReduction.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface RPseudoReduction<C extends RegularRingElem<C>> extends RReduction<C>,
        PseudoReduction<C> {

}
