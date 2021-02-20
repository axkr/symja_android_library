/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Characteristic Set interface. Defines methods for Characteristic Sets and
 * tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public interface CharacteristicSet<C extends GcdRingElem<C>> extends Serializable {


    /**
     * Characteristic set. According to the implementing algorithm (simple, Wu, etc).
     * @param A list of generic polynomials.
     * @return charSet(A) with at most one polynomial per main variable.
     */
    public List<GenPolynomial<C>> characteristicSet(List<GenPolynomial<C>> A);


    /**
     * Characteristic set test.
     * @param A list of generic polynomials.
     * @return true, if A is (at least a simple) characteristic set, else false.
     */
    public boolean isCharacteristicSet(List<GenPolynomial<C>> A);


    /**
     * Characteristic set reduction. Pseudo remainder wrt. the main variable.
     * With further pseudo reduction of the leading coefficient depending on the implementing algorithm.
     * @param P generic polynomial.
     * @param A list of generic polynomials as characteristic set.
     * @return characteristicSetRemainder(A,P) or 
     *         characteristicSetReductionCoeff(A,characteristicSetRemainder(A,P)) depending on the algorithm.
     */
    public GenPolynomial<C> characteristicSetReduction(List<GenPolynomial<C>> A, GenPolynomial<C> P);

}
