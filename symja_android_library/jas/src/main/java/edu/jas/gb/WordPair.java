/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;

import edu.jas.structure.RingElem;
import edu.jas.poly.Word;
import edu.jas.poly.GenWordPolynomial;


/**
 * Serializable subclass to hold pairs of word polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class WordPair<C extends RingElem<C> > implements Comparable<WordPair> {


    public final GenWordPolynomial<C> pi;
    public final GenWordPolynomial<C> pj;
    public final int i;
    public final int j;

    protected int n;


    /**
     * WordPair constructor.
     * @param a word polynomial i.
     * @param b word polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public WordPair(GenWordPolynomial<C> a, GenWordPolynomial<C> b, 
                    int i, int j) {
        pi = a;
        pj = b;
        this.i = i;
        this.j = j;
        this.n = 0;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "wordPair(" + i + "," + j + ",{" + pi.length() + "," + pj.length() + "},"  + n + ")"; 
    }


    /**
     * Set removed pair number.
     * @param n number of this pair generated in OrderedPairlist.
     */
    public void pairNumber(int n) {
        this.n = n;
    }


    /**
     * Get removed pair number.
     * @return n number of this pair generated in OrderedPairlist.
     */
    public int getPairNumber() {
        return n;
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    public boolean equals(Object ob) {
        if ( ! (ob instanceof WordPair) ) {
           return false;
           // throw new ClassCastException("Pair "+n+" o "+o);
        }
        return 0 == compareTo( (WordPair)ob );
    }


    /**
     * compareTo used in TreeMap // not used at moment.
     * Comparison is based on the number of the pairs.
     * @param p a WordPair.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    public int compareTo(WordPair p) {
        int x = p.getPairNumber();
        if ( n > x ) { 
           return 1;
        }
        if ( n < x ) { 
           return -1;
        }
        return 0;
    }


    /**
     * Hash code for this WordPair.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (i << 16) + j;
    }

}

