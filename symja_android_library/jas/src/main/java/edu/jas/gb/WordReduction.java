/*
 * $Id$
 */

package edu.jas.gb;

import java.util.List;

import java.io.Serializable;

import edu.jas.poly.Word;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial WordReduction interface.
 * Defines S-Polynomial, normalform, module criterion
 * and irreducible set.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface WordReduction<C extends RingElem<C>> 
                 extends Serializable {


    /**
     * S-Polynomials of non-commutative polynomials.
     * @param Ap word polynomial.
     * @param Bp word polynomial.
     * @return list of all spol(Ap,Bp) the S-polynomials of Ap and Bp.
     */
    public List<GenWordPolynomial<C>> SPolynomials(GenWordPolynomial<C> Ap, 
                                                   GenWordPolynomial<C> Bp);


    /**
     * S-Polynomials of non-commutative polynomials.
     * @param a leading base coefficient of B.
     * @param l1 word.
     * @param A word polynomial.
     * @param r1 word.
     * @param b leading base coefficient of A.
     * @param l2 word.
     * @param B word polynomial.
     * @param r2 word.
     * @return list of all spol(Ap,Bp) the S-polynomials of Ap and Bp.
     */
    public GenWordPolynomial<C> SPolynomial(C a, Word l1, GenWordPolynomial<C> A, Word r1,
                                            C b, Word l2, GenWordPolynomial<C> B, Word r2);


    /**
     * Is top reducible.
     * Condition is lt(B) | lt(A) for some B in F.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<GenWordPolynomial<C>> P, 
                                  GenWordPolynomial<C> A);


    /**
     * Is reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is reducible with respect to P.
     */
    public boolean isReducible(List<GenWordPolynomial<C>> P, 
                               GenWordPolynomial<C> A);


    /**
     * Is in Normalform.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is in normalform with respect to P.
     */
    public boolean isNormalform(List<GenWordPolynomial<C>> P, 
                                GenWordPolynomial<C> A);


    /**
     * Is in Normalform.
     * @param Pp polynomial list.
     * @return true if each A in Pp is in normalform with respect to Pp\{A}.
     */
    public boolean isNormalform( List<GenWordPolynomial<C>> Pp );


    /**
     * Normalform.
     * @param A polynomial.
     * @param P polynomial list.
     * @return nf(A) with respect to P.
     */
    public GenWordPolynomial<C> normalform(List<GenWordPolynomial<C>> P, 
                                           GenWordPolynomial<C> A);


    /**
     * Normalform Set.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenWordPolynomial<C>> normalform(List<GenWordPolynomial<C>> Pp, 
                                                 List<GenWordPolynomial<C>> Ap);


    /**
     * Normalform with left and right recording.
     * @param lrow left recording matrix, is modified.
     * @param rrow right recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    public GenWordPolynomial<C> 
           normalform(List<GenWordPolynomial<C>> lrow, List<GenWordPolynomial<C>> rrow,
                      List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap);


    /**
     * Irreducible set.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P and with ideal(Pp) = ideal(P).
     */
    public List<GenWordPolynomial<C>> irreducibleSet(List<GenWordPolynomial<C>> Pp);


    /**
     * Is reduction of normal form.
     * @param lrow left recording matrix.
     * @param rrow right recording matrix.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @param Np nf(Pp,Ap), a normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */
    public boolean isReductionNF(List<GenWordPolynomial<C>> lrow, List<GenWordPolynomial<C>> rrow, 
                                 List<GenWordPolynomial<C>> Pp, 
                                 GenWordPolynomial<C> Ap, GenWordPolynomial<C> Np);

}
