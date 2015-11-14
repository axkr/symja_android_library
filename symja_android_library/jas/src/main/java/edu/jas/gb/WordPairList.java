/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;

import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * WordPair list management interface.
 * @author Heinz Kredel
 */

public interface WordPairList<C extends RingElem<C>> {


    /**
     * Create a new WordPairList.
     * @param r word polynomial ring.
     */
    public WordPairList<C> create(GenWordPolynomialRing<C> r);


    /**
     * toString.
     */
    @Override
    public String toString();


    /**
     * Put one Word Polynomial to the pairlist and reduction matrix.
     * @param p word polynomial.
     * @return the index of the added word polynomial.
     */
    public int put(GenWordPolynomial<C> p);


    /**
     * Put all word polynomials in F to the pairlist and reduction matrix.
     * @param F word polynomial list.
     * @return the index of the last added word polynomial.
     */
    public int put(List<GenWordPolynomial<C>> F);


    /**
     * Put to ONE-Polynomial to the pairlist.
     * @return the index of the last polynomial.
     */
    public int putOne();


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public WordPair<C> removeNext();


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public boolean hasNext();


    /**
     * Get the list of word polynomials.
     * @return the word polynomial list.
     */
    public List<GenWordPolynomial<C>> getList();


    /**
     * Get the number of polynomials put to the pairlist.
     * @return the number of calls to put.
     */
    public int putCount();


    /**
     * Get the number of required pairs removed from the pairlist.
     * @return the number of non null pairs delivered.
     */
    public int remCount();

}
