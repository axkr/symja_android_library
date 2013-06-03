/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;


/**
 * Serializable subclass to hold pairs of polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class Pair<C extends RingElem<C> > extends AbstractPair<C>
             implements Comparable<Pair> {


    protected int n;
    protected boolean toZero = false;
    protected boolean useCriterion4 = true;
    protected boolean useCriterion3 = true;


    /**
     * Pair constructor.
     * @param a polynomial i (must be castable to GenPolynomial&lt;C&gt;).
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @deprecated obsolete since Java 1.5
     */
    @Deprecated
    public Pair(Object a, GenPolynomial<C> b, int i, int j) {
        this( (GenPolynomial<C>)a, b, i, j); 
    }


    /**
     * Pair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public Pair(GenPolynomial<C> a, GenPolynomial<C> b, 
                int i, int j) {
        this(a,b,i,j,0);
    }


    /**
     * Pair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public Pair(GenPolynomial<C> a, GenPolynomial<C> b, 
                int i, int j, int s) {
        this(a.leadingExpVector().lcm(b.leadingExpVector()),a,b,i,j,s);
    }


    /**
     * Pair constructor.
     * @param lcm of lt(a) lt(b).
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public Pair(ExpVector lcm, GenPolynomial<C> a, GenPolynomial<C> b, 
                int i, int j) {
        this(lcm,a,b,i,j,0);
    }


    /**
     * Pair constructor.
     * @param lcm of lt(a) lt(b).
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public Pair(ExpVector lcm, GenPolynomial<C> a, GenPolynomial<C> b, 
                int i, int j, int s) {
        super(lcm,a,b,i,j,s);
        this.n = 0;
        toZero = false; // ok
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "[" + n  
                           + ", r0=" + toZero  
                           + ", c4=" + useCriterion4  
                           + ", c3=" + useCriterion3 
                           + "]";
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
     * Set zero reduction.
     * The S-polynomial of this Pair was reduced to zero.
     */
    public void setZero() {
        toZero = true;
    }


    /**
     * Is reduced to zero.
     * @return true if the S-polynomial of this Pair was reduced to zero, else false.
     */
    public boolean isZero() {
        return toZero;
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
     public boolean equals(Object ob) {
        if ( ! (ob instanceof Pair) ) {
           return false;
           // throw new ClassCastException("Pair "+n+" o "+o);
        }
        return 0 == compareTo( (Pair)ob );
    }


    /**
     * compareTo used in TreeMap // not used at moment.
     * Comparison is based on the number of the pairs.
     * @param p a Pair.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    public int compareTo(Pair p) {
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
     * Hash code for this Pair.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (i << 16) + j;
    }


    /**
     * Set useCriterion4.
     * @param c boolean value to set.
     */
    public void setUseCriterion4(boolean c) {
        this.useCriterion4 = c;
    }


    /**
     * Get useCriterion4.
     * @return boolean value.
     */
    public boolean getUseCriterion4() {
        return this.useCriterion4;
    }


    /**
     * Set useCriterion3.
     * @param c boolean value to set.
     */
    public void setUseCriterion3(boolean c) {
        this.useCriterion3 = c;
    }


    /**
     * Get useCriterion3.
     * @return boolean value.
     */
    public boolean getUseCriterion3() {
        return this.useCriterion3;
    }

}

