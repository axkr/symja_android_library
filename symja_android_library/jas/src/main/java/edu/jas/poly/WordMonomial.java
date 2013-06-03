
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;


/**
 * WordMonomial class. 
 * Represents pairs of words and coefficients.
 * Adaptor for Map.Entry.
 * @author Heinz Kredel
 */

public final class WordMonomial<C extends RingElem<C> > {

    /** 
     * Word of monomial.
     */
    public final Word e;


    /** 
     * Coefficient of monomial.
     */
    public final C c;


    /** 
     * Constructor of word monomial.
     * @param me a MapEntry.
     */
    public WordMonomial(Map.Entry<Word,C> me){
        this( me.getKey(), me.getValue() );
    }


    /** 
     * Constructor of word monomial.
     * @param e word.
     * @param c coefficient.
     */
    public WordMonomial(Word e, C c) {
        this.e = e;
        this.c = c;
    }


    /** 
     * Getter for word.
     * @return word.
     */
    public Word word() {
        return e;
    }


    /** 
     * Getter for coefficient.
     * @return coefficient.
     */
    public C coefficient() {
        return c;
    }

    /**
     * String representation of Monomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return c.toString() + " " + e.toString();
    }

}
