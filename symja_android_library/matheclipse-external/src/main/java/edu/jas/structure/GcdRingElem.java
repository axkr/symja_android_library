/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Gcd ring element interface. Empty interface since gcd and egcd is now in
 * RingElem. Adds greatest common divisor and extended greatest common divisor.
 * @param <C> gcd element type
 * @author Heinz Kredel
 */

public interface GcdRingElem<C extends GcdRingElem<C>> extends RingElem<C> {

}
