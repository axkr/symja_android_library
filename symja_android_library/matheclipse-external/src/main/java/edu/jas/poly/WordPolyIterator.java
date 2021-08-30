
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;

import edu.jas.structure.RingElem;

import edu.jas.poly.Word;


/**
 * Iterator over monomials of a polynomial. 
 * Adaptor for val.entrySet().iterator().
 * @author Heinz Kredel
 */

public class WordPolyIterator<C extends RingElem<C> > 
             implements Iterator< WordMonomial<C> > {


    /** 
     * Internal iterator over polynomial map.
     */
    protected final Iterator< Map.Entry<Word,C> > ms;


    /** 
     * Constructor of polynomial iterator.
     * @param m SortetMap of a polynomial.
     */
    public WordPolyIterator( SortedMap<Word,C> m ) {
        ms = m.entrySet().iterator();
    }


    /** 
     * Test for availability of a next monomial.
     * @return true if the iteration has more monomials, else false.
     */
    public boolean hasNext() {
        return ms.hasNext();
    }


    /** 
     * Get next monomial element.
     * @return next monomial.
     */
    public WordMonomial<C> next() {
        return new WordMonomial<C>( ms.next() );
    }


    /** 
     * Remove the last monomial returned from underlying set if allowed.
     */
    public void remove() {
        ms.remove();
    }

}
