/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Serializable subclass to hold pairs of polynomials.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class SigPair<C extends RingElem<C>> //extends AbstractSigPair<C>
        implements Comparable<SigPair<C>> {


    public final GenPolynomial<C> sigma;


    public final SigPoly<C> pi;


    public final SigPoly<C> pj;


    public final List<SigPoly<C>> Gs;


    /**
     * SigPair constructor.
     *
     * @param sig signature of pair.
     * @param a   polynomial i.
     * @param b   polynomial j.
     */
    public SigPair(ExpVector sig, SigPoly<C> a, SigPoly<C> b, List<SigPoly<C>> Gs) {
        this(a.poly.ring.valueOf(sig), a, b, Gs);
    }


    /**
     * SigPair constructor.
     *
     * @param sig signature of pair.
     * @param a   polynomial i.
     * @param b   polynomial j.
     */
    public SigPair(GenPolynomial<C> sig, SigPoly<C> a, SigPoly<C> b, List<SigPoly<C>> Gs) {
        this.sigma = sig;
        pi = a;
        pj = b;
        this.Gs = Gs;
    }


    /**
     * getter for sigma
     */
    GenPolynomial<C> getSigma() {
        return sigma;
    }


    /**
     * getter for sigma.degree
     */
    long getSigmaDegree() {
        return sigma.degree();
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "pair(" + sigma + " @ " + pi + ", " + pj + ")";
    }


    /**
     * equals.
     *
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        if (!(ob instanceof SigPair)) {
            return false;
            // throw new ClassCastException("SigPair "+n+" o "+o);
        }
        return 0 == compareTo((SigPair) ob);
    }


    /**
     * compareTo used in TreeMap // not used at moment. Comparison is based on
     * the number of the pairs.
     *
     * @param p a SigPair.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    public int compareTo(SigPair<C> p) {
        return sigma.compareTo(p.sigma);
    }


    /**
     * Hash code for this SigPair.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (pi.hashCode() << 16) + pj.hashCode();
    }

}
