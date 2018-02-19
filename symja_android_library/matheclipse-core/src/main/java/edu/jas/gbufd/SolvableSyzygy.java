/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;


/**
 * Syzygy interface for solvable polynomials. Defines syzygy computations and
 * tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface SolvableSyzygy<C extends RingElem<C>> extends Serializable {


    /**
     * Left syzygy for left Groebner base.
     *
     * @param F a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelations(List<GenSolvablePolynomial<C>> F);


    /**
     * Left syzygy for left Groebner base.
     *
     * @param modv number of module variables.
     * @param F    a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelations(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Left syzygy for left module Groebner base.
     *
     * @param M a Groebner base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    public ModuleList<C> leftZeroRelations(ModuleList<C> M);


    /**
     * Test if left syzygy.
     *
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean isLeftZeroRelation(List<List<GenSolvablePolynomial<C>>> Z, List<GenSolvablePolynomial<C>> F);


    /**
     * Test if right syzygy.
     *
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean isRightZeroRelation(List<List<GenSolvablePolynomial<C>>> Z,
                                       List<GenSolvablePolynomial<C>> F);


    /**
     * Test if left sysygy of modules
     *
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean isLeftZeroRelation(ModuleList<C> Z, ModuleList<C> F);


    /**
     * Test if right sysygy of modules
     *
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean isRightZeroRelation(ModuleList<C> Z, ModuleList<C> F);


    /**
     * Resolution of a module. Only with direct GBs.
     *
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> resolution(ModuleList<C> M);


    /**
     * Resolution of a polynomial list. Only with direct GBs.
     *
     * @param F a polynomial list of a Groebner basis.
     * @return a resolution of F.
     */
    public List/*<SolvResPart<C>|SolvResPolPart<C>>*/resolution(PolynomialList<C> F);


    /**
     * Resolution of a module.
     *
     * @param M a module list of an arbitrary basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> resolutionArbitrary(ModuleList<C> M);


    /**
     * Resolution of a polynomial list.
     *
     * @param F a polynomial list of an arbitrary basis.
     * @return a resolution of F.
     */
    public List/*<SolvResPart<C>|SolvResPolPart<C>>*/resolutionArbitrary(PolynomialList<C> F);


    /**
     * Left syzygy module from arbitrary base.
     *
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F);


    /**
     * Left syzygy module from arbitrary base.
     *
     * @param modv number of module variables.
     * @param F    a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelationsArbitrary(int modv,
                                                                           List<GenSolvablePolynomial<C>> F);


    /**
     * Left syzygy for arbitrary left module base.
     *
     * @param M an arbitrary base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    public ModuleList<C> leftZeroRelationsArbitrary(ModuleList<C> M);


    /**
     * Right syzygy module from arbitrary base.
     *
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F);


    /**
     * Right syzygy module from arbitrary base.
     *
     * @param modv number of module variables.
     * @param F    a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelationsArbitrary(int modv,
                                                                            List<GenSolvablePolynomial<C>> F);


    /**
     * Test left Ore condition.
     *
     * @param a  solvable polynomial
     * @param b  solvable polynomial
     * @param oc = [p,q] two solvable polynomials
     * @return true if p*a = q*b, else false
     */
    public boolean isLeftOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b,
                                 GenSolvablePolynomial<C>[] oc);


    /**
     * Test right Ore condition.
     *
     * @param a  solvable polynomial
     * @param b  solvable polynomial
     * @param oc = [p,q] two solvable polynomials
     * @return true if a*p = b*q, else false
     */
    public boolean isRightOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b,
                                  GenSolvablePolynomial<C>[] oc);


    /**
     * Left Ore condition. Generators for the left Ore condition of two solvable
     * polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with p*a = q*b
     */
    public GenSolvablePolynomial<C>[] leftOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b);


    /**
     * Right Ore condition. Generators for the right Ore condition of two
     * solvable polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with a*p = b*q
     */
    public GenSolvablePolynomial<C>[] rightOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b);

}
