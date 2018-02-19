/*
 * $Id$
 */

package edu.jas.gb;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Solvable polynomial Reduction interface. Defines S-Polynomial, normalform and
 * irreducible set.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface SolvableReduction<C extends RingElem<C>> extends Serializable {


    /**
     * Left S-Polynomial.
     *
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return left-spol(Ap,Bp) the left S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> leftSPolynomial(GenSolvablePolynomial<C> Ap, GenSolvablePolynomial<C> Bp);


    /**
     * S-Polynomial with recording.
     *
     * @param S  recording matrix, is modified.
     * @param i  index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j  index of Bp in basis list.
     * @param Bp a polynomial.
     * @return leftSpol(Ap, Bp), the left S-Polynomial for Ap and Bp.
     */
    public GenSolvablePolynomial<C> leftSPolynomial(List<GenSolvablePolynomial<C>> S, int i,
                                                    GenSolvablePolynomial<C> Ap, int j, GenSolvablePolynomial<C> Bp);


    /**
     * Left Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                   GenSolvablePolynomial<C> Ap);


    /**
     * LeftNormalform with recording.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the left normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> row,
                                                   List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap);


    /**
     * Left Normalform Set.
     *
     * @param Ap solvable polynomial list.
     * @param Pp solvable polynomial list.
     * @return list of left-nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenSolvablePolynomial<C>> leftNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                         List<GenSolvablePolynomial<C>> Ap);


    /**
     * Left irreducible set.
     *
     * @param Pp solvable polynomial list.
     * @return a list P of solvable polynomials which are in normalform wrt. P.
     */
    public List<GenSolvablePolynomial<C>> leftIrreducibleSet(List<GenSolvablePolynomial<C>> Pp);


    /**
     * Is reduction of normal form.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a solvable polynomial list for reduction.
     * @param Ap  a solvable polynomial.
     * @param Np  nf(Pp,Ap), a left normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */

    public boolean isLeftReductionNF(List<GenSolvablePolynomial<C>> row, List<GenSolvablePolynomial<C>> Pp,
                                     GenSolvablePolynomial<C> Ap, GenSolvablePolynomial<C> Np);


    /**
     * Right S-Polynomial.
     *
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return right-spol(Ap,Bp) the right S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> rightSPolynomial(GenSolvablePolynomial<C> Ap, GenSolvablePolynomial<C> Bp);


    /**
     * Right Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return right-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                    GenSolvablePolynomial<C> Ap);


    /**
     * RightNormalform with recording.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the right normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> row,
                                                    List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap);


    /**
     * Is top reducible. Condition is lt(B) | lt(A) for some B in F.
     * Is left right symetric.
     *
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<GenSolvablePolynomial<C>> P, GenSolvablePolynomial<C> A);


    /**
     * Is reducible. Is left right symetric.
     *
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is reducible with respect to P.
     */
    public boolean isReducible(List<GenSolvablePolynomial<C>> P, GenSolvablePolynomial<C> A);


    /**
     * Is in normalform. Is left right symetric.
     *
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is in normalform with respect to P.
     */
    public boolean isNormalform(List<GenSolvablePolynomial<C>> P, GenSolvablePolynomial<C> A);


    /**
     * Two-sided Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return two-sided-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> normalform(List<GenSolvablePolynomial<C>> Pp,
                                               GenSolvablePolynomial<C> Ap);

}
