/*
 * $Id$
 */

package edu.jas.gb;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenWordPolynomial;
import edu.jas.structure.RingElem;


/**
 * Non-commutative Groebner Bases interface for GenWordPolynomials. Defines
 * methods for Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface WordGroebnerBase<C extends RingElem<C>> extends Serializable {


    /**
     * Groebner base test.
     * @param F word polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenWordPolynomial<C>> F);


    /**
     * Groebner base using pairlist class.
     * @param F word polynomial list.
     * @return GB(F) a non-commutative Groebner base of F.
     */
    public List<GenWordPolynomial<C>> GB(List<GenWordPolynomial<C>> F);


    /**
     * Minimal ordered groebner basis.
     * @param Gp a Word Groebner base.
     * @return a reduced Word Groebner base of Gp.
     */
    public List<GenWordPolynomial<C>> minimalGB(List<GenWordPolynomial<C>> Gp);

}
