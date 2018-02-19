/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;
import java.util.Comparator;

import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;


/**
 * Comparator for critical pairs of polynomials.
 * Immutable objects.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class CriticalPairComparator<C extends RingElem<C>>
        implements Serializable, Comparator<AbstractPair<C>> {


    public final TermOrder tord;
    protected final TermOrder.EVComparator ec;


    /**
     * Constructor.
     *
     * @param t TermOrder.
     */
    public CriticalPairComparator(TermOrder t) {
        tord = t;
        ec = tord.getAscendComparator();
    }


    /**
     * Compare.
     * Compares exponents and if equal, compares polynomial indices.
     *
     * @param p1 first critical pair.
     * @param p2 second critical pair.
     * @return 0 if ( p1 == p2 ), -1 if ( p1 < p2 ) and +1 if ( p1 > p2 ).
     */
    public int compare(AbstractPair<C> p1, AbstractPair<C> p2) {
        int s = ec.compare(p1.e, p2.e);
        if (s == 0) {
            /* not ok  
           if ( p1.j < p2.j ) {
              s = -1;
           } else if ( p1.j > p2.j ) {
               s = 1;
           } else if ( p1.i < p2.i ) {
               s = -1;
           } else if ( p1.i > p2.i ) {
               s = 1;
           } else {
               s = 0;
           }
            */
           /* ok */
            if (p1.j > p2.j) {
                s = -1;
            } else if (p1.j < p2.j) {
                s = 1;
            } else if (p1.i > p2.i) {
                s = -1;
            } else if (p1.i < p2.i) {
                s = 1;
            } else {
                s = 0;
            }
           /* */
        }
        return s;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "CriticalPairComparator(" + tord + ")";
    }

}
