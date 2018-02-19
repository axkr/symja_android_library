/*
 * $Id$
 */

package edu.jas.gb;


import edu.jas.structure.RingElem;


/**
 * Polynomial E-Reduction interface.
 * Empty marker interface since all required methods are already
 * defined in the DReduction interface.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface EReduction<C extends RingElem<C>>
        extends DReduction<C> {


}
