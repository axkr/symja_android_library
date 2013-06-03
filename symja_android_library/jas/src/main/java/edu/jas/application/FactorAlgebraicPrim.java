/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbsolute;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Algebraic number coefficients factorization algorithms. This class
 * implements factorization methods for polynomials over algebraic
 * numbers over rational numbers or over (prime) modular integers. The
 * algorithm uses zero dimensional ideal prime decomposition.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorAlgebraicPrim<C extends GcdRingElem<C>> extends FactorAbsolute<AlgebraicNumber<C>> {


    //FactorAbstract<AlgebraicNumber<C>>


    private static final Logger logger = Logger.getLogger(FactorAlgebraicPrim.class);


    //private final boolean debug = logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    public final FactorAbstract<C> factorCoeff;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorAlgebraicPrim() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     */
    public FactorAlgebraicPrim(AlgebraicNumberRing<C> fac) {
        this(fac, FactorFactory.<C> getImplementation(fac.ring.coFac));
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     * @param factorCoeff factorization engine for polynomials over base
     *            coefficients.
     */
    public FactorAlgebraicPrim(AlgebraicNumberRing<C> fac, FactorAbstract<C> factorCoeff) {
        super(fac);
        this.factorCoeff = factorCoeff;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsSquarefree(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring; // Q(alpha)[x]
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;

        AlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        //System.out.println("\nP = " + P);
        if (logger.isDebugEnabled()) {
            Squarefree<AlgebraicNumber<C>> sqengine = SquarefreeFactory
                            .<AlgebraicNumber<C>> getImplementation(afac);
            if (!sqengine.isSquarefree(P)) {
                throw new RuntimeException("P not squarefree: " + sqengine.squarefreeFactors(P));
            }
            GenPolynomial<C> modu = afac.modul;
            if (!factorCoeff.isIrreducible(modu)) {
                throw new RuntimeException("modul not irreducible: " + factorCoeff.factors(modu));
            }
            System.out.println("P squarefree and modul irreducible via ideal decomposition");
            //GreatestCommonDivisor<AlgebraicNumber<C>> aengine //= GCDFactory.<AlgebraicNumber<C>> getProxy(afac);
            //  = new GreatestCommonDivisorSimple<AlgebraicNumber<C>>( /*cfac.coFac*/ );
        }
        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pfac);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[2];
        vars[0] = cfac.getVars()[0];
        vars[1] = rfac.getVars()[0];
        GenPolynomialRing<C> dfac = new GenPolynomialRing<C>(cfac.coFac, to, vars);
        // transform minimal polynomial to bi-variate polynomial
        GenPolynomial<C> Ad = agen.extend(dfac, 0, 0L);
        // transform to bi-variate polynomial 
        GenPolynomial<GenPolynomial<C>> Pc = PolyUtil.<C> fromAlgebraicCoefficients(rfac, P); 
        //System.out.println("Pc = " + Pc.toScript());
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, Pc);
        //System.out.println("Ad = " + Ad.toScript());
        //System.out.println("Pd = " + Pd.toScript());

        List<GenPolynomial<C>> id = new ArrayList<GenPolynomial<C>>(2);
        id.add(Ad);
        id.add(Pd);
        Ideal<C> I = new Ideal<C>(dfac, id);
        List<IdealWithUniv<C>> Iul = I.zeroDimPrimeDecomposition();
        //System.out.println("prime decomp = " + Iul);
        if (Iul.size() == 1) {
            factors.add(P);
            return factors;
        }
        GenPolynomial<AlgebraicNumber<C>> f = pfac.getONE();
        for (IdealWithUniv<C> Iu : Iul) {
            List<GenPolynomial<C>> pl = Iu.ideal.getList();
            GenPolynomial<C> ag = PolyUtil.<C> selectWithVariable(pl, 1);
            GenPolynomial<C> pg = PolyUtil.<C> selectWithVariable(pl, 0);
            //System.out.println("ag = " + ag.toScript());
            //System.out.println("pg = " + pg.toScript());
            if (ag.equals(Ad)) {
                //System.out.println("found factor --------------------");
                GenPolynomial<GenPolynomial<C>> pgr = PolyUtil.<C> recursive(rfac, pg);
                GenPolynomial<AlgebraicNumber<C>> pga = PolyUtil.<C> convertRecursiveToAlgebraicCoefficients(
                                pfac, pgr);
                //System.out.println("pga = " + pga.toScript());
                f = f.multiply(pga);
                factors.add(pga);
            } else {
                logger.warn("algebraic number mismatch: ag = " + ag + ", expected Ad = " + Ad);
            }
        }
        f = f.subtract(P);
        //System.out.println("f = " + f.toScript());
        if (!f.isZERO()) {
            throw new RuntimeException("no factorization: " + f + ", factors = " + factors);
        }
        return factors;
        //return new edu.jas.ufd.FactorAlgebraic<C>(afac).baseFactorsSquarefree(P);
    }

}
