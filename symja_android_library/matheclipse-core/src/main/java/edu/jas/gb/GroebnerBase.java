/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.ModuleList;
import edu.jas.structure.RingElem;


/**
 * Groebner Bases interface.
 * Defines methods for Groebner bases and GB test.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public interface GroebnerBase<C extends RingElem<C>>
        extends Serializable {


    /**
     * Groebner base test.
     *
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F);


    /**
     * Groebner base test.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F);


    /**
     * Groebner base using pairlist class.
     *
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>>
    GB(List<GenPolynomial<C>> F);


    /**
     * Groebner base using pairlist class.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>>
    GB(int modv,
       List<GenPolynomial<C>> F);


    /**
     * isGB.
     *
     * @param M a module basis.
     * @return true, if M is a Groebner base, else false.
     */
    public boolean isGB(ModuleList<C> M);


    /**
     * GB.
     *
     * @param M a module basis.
     * @return GB(M), a Groebner base of M.
     */
    public ModuleList<C> GB(ModuleList<C> M);


    /**
     * Extended Groebner base using critical pair class.
     *
     * @param F polynomial list.
     * @return a container for a Groebner base G of F together with back-and-forth transformations.
     */
    public ExtendedGB<C>
    extGB(List<GenPolynomial<C>> F);


    /**
     * Extended Groebner base using critical pair class.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return a container for a Groebner base G of F together with back-and-forth transformations.
     */
    public ExtendedGB<C>
    extGB(int modv,
          List<GenPolynomial<C>> F);


    /**
     * Minimal ordered groebner basis.
     *
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>>
    minimalGB(List<GenPolynomial<C>> Gp);


    /**
     * Test if reduction matrix.
     *
     * @param exgb an ExtendedGB container.
     * @return true, if exgb contains a reduction matrix, else false.
     */
    public boolean
    isReductionMatrix(ExtendedGB<C> exgb);


    /**
     * Test if reduction matrix.
     *
     * @param F  a polynomial list.
     * @param G  a Groebner base.
     * @param Mf a possible reduction matrix.
     * @param Mg a possible reduction matrix.
     * @return true, if Mg and Mf are reduction matrices, else false.
     */
    public boolean
    isReductionMatrix(List<GenPolynomial<C>> F,
                      List<GenPolynomial<C>> G,
                      List<List<GenPolynomial<C>>> Mf,
                      List<List<GenPolynomial<C>>> Mg);

}
