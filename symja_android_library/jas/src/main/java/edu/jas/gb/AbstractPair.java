/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;


/**
 * Serializable abstract subclass to hold pairs of polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public abstract class AbstractPair<C extends RingElem<C> > 
                      implements Serializable {

    public final ExpVector e;
    public final GenPolynomial<C> pi;
    public final GenPolynomial<C> pj;
    public final int i;
    public final int j;
    protected int s;


    /**
     * AbstractPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public AbstractPair(GenPolynomial<C> a, GenPolynomial<C> b, 
                        int i, int j) {
        this(a.leadingExpVector().lcm(b.leadingExpVector()),a,b,i,j); 
    }


    /**
     * AbstractPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public AbstractPair(GenPolynomial<C> a, GenPolynomial<C> b, 
                        int i, int j, int s) {
        this(a.leadingExpVector().lcm(b.leadingExpVector()),a,b,i,j,s); 
    }


    /**
     * AbstractPair constructor.
     * @param lcm least common multiple of lt(a) and lt(b).
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public AbstractPair(ExpVector lcm, GenPolynomial<C> a, GenPolynomial<C> b, 
                        int i, int j) {
        this(lcm,a,b,i,j,0); 
    }


    /**
     * AbstractPair constructor.
     * @param lcm least common multiple of lt(a) and lt(b).
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public AbstractPair(ExpVector lcm, GenPolynomial<C> a, GenPolynomial<C> b, 
                        int i, int j, int s) {
        e = lcm;
        pi = a; 
        pj = b; 
        this.i = i; 
        this.j = j;
        s = Math.max(i,s);
        s = Math.max(j,s);
        this.s = s;
    }


    /**
     * Set maximal index.
     * @param s maximal index for pair polynomials.
     */
    public void maxIndex(int s) {
        s = Math.max(this.i,s);
        s = Math.max(this.j,s);
        this.s = s;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "pair(" + i + "," + j + "," + s + ",{" + pi.length() + "," + pj.length() + "},"
                       + e + ")";
    }

}
