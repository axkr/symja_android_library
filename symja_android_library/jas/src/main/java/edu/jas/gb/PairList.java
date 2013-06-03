/*
 * $Id$
 */

package edu.jas.gb;

import java.util.List;

import edu.jas.structure.RingElem;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Pair list management interface.
 * @author Heinz Kredel
 */

public interface PairList<C extends RingElem<C> > {


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r);


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r);


    /**
     * toString.
     */
    @Override
    public String toString();


    /**
     * Put one Polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public int put(GenPolynomial<C> p);


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
    public Pair<C> removeNext();


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public boolean hasNext();


    /**
     * Get the size of the list of polynomials.
     * @return size of the polynomial list.
     */
    public int size();


    /**
     * Get the list of polynomials.
     * @return the polynomial list.
     */
    public List<GenPolynomial<C>> getList();


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


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij);

}

