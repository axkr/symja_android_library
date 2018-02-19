/*
 * $Id$
 */

package edu.jas.gb;


import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Container for a polynomial and its signature.
 *
 * @param <C>   coefficient type
 * @param sigma a polynomial signature.
 * @param poly  a polynomial.
 */
public class SigPoly<C extends RingElem<C>> {


    public final GenPolynomial<C> sigma;


    public final GenPolynomial<C> poly;


    /**
     * Constructor.
     *
     * @param s a polynomial signature.
     * @param p a polynomial.
     */
    public SigPoly(GenPolynomial<C> s, GenPolynomial<C> p) {
        this.sigma = s;
        this.poly = p;
    }


    /**
     * getter for sigma
     */
    GenPolynomial<C> getSigma() {
        return sigma;
    }


    /**
     * getter for polynomial
     */
    GenPolynomial<C> getPoly() {
        return poly;
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("sigma(");
        s.append(sigma.toString() + "):: ");
        s.append(poly.toString());
        return s.toString();
    }

}
