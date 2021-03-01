/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Algebraic number coefficients factorization algorithms. This class implements
 * factorization methods for polynomials over algebraic numbers over rational
 * numbers or over (prime) modular integers.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorAlgebraic<C extends GcdRingElem<C>> extends FactorAbsolute<AlgebraicNumber<C>> {


    private static final Logger logger = LogManager.getLogger(FactorAlgebraic.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    public final FactorAbstract<C> factorCoeff;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorAlgebraic() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     */
    public FactorAlgebraic(AlgebraicNumberRing<C> fac) {
        this(fac, FactorFactory.<C> getImplementation(fac.ring.coFac));
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     * @param factorCoeff factorization engine for polynomials over base
     *            coefficients.
     */
    public FactorAlgebraic(AlgebraicNumberRing<C> fac, FactorAbstract<C> factorCoeff) {
        super(fac);
        this.factorCoeff = factorCoeff;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsSquarefree(
                    GenPolynomial<AlgebraicNumber<C>> P) {
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
        if (debug) {
            Squarefree<AlgebraicNumber<C>> sqengine = SquarefreeFactory
                            .<AlgebraicNumber<C>> getImplementation(afac);
            if (!sqengine.isSquarefree(P)) {
                throw new RuntimeException("P not squarefree: " + sqengine.squarefreeFactors(P));
            }
            GenPolynomial<C> modu = afac.modul;
            if (!factorCoeff.isIrreducible(modu)) {
                throw new RuntimeException("modul not irreducible: " + factorCoeff.factors(modu));
            }
            System.out.println("P squarefree and modul irreducible");
            //GreatestCommonDivisor<AlgebraicNumber<C>> aengine //= GCDFactory.<AlgebraicNumber<C>> getProxy(afac);
            //  = new GreatestCommonDivisorSimple<AlgebraicNumber<C>>( /*cfac.coFac*/ );
        }

        // search squarefree norm
        long k = 0L;
        long ks = k;
        GenPolynomial<C> res = null;
        boolean sqf = false;
        //int[] klist = new int[]{ 0, 1, 2, 3, -1, -2, -3 , 5, -5, 7, -7, 101, -101, 1001, -1001 };
        //int[] klist = new int[]{ 0, 1, 2, 3, -1, -2, -3 , 5, -5, 7, -7, 23, -23, 167, -167 };
        //int[] klist = new int[] { 0, -1, -2, 1, 2, -3, 3 };
        int[] klist = new int[] { 0, -1, -2, 1, 2 };
        int ki = 0;
        while (!sqf) {
            // k = 0,1,2,-1,-2
            if (ki >= klist.length) {
                break;
            }
            k = klist[ki];
            ki++;
            // compute norm with x -> ( y - k x )
            ks = k;
            res = PolyUfdUtil.<C> norm(P, ks);
            //System.out.println("res = " + res);
            if (res.isZERO() || res.isConstant()) {
                continue;
            }
            sqf = factorCoeff.isSquarefree(res);
        }
        // if Res is now squarefree, else must take radical factorization
        List<GenPolynomial<C>> nfacs;
        if (!sqf) {
            //System.out.println("\nres = " + res); 
            logger.warn("sqf(" + ks + ") = " + res.degree());
            //res = factorCoeff.squarefreePart(res); // better use obtained factors
            //res = factorCoeff.baseFactors(res).lastKey();
        }
        //res = res.monic();
        if (logger.isInfoEnabled()) {
            logger.info("res = " + res);
        }
        nfacs = factorCoeff.baseFactorsRadical(res);
        if (logger.isInfoEnabled()) {
            logger.info("res facs = " + nfacs); // Q[X]
        }
        if (nfacs.size() == 1) {
            factors.add(P);
            return factors;
        }

        // compute gcds of factors with polynomial in Q(alpha)[X]
        GenPolynomial<AlgebraicNumber<C>> Pp = P;
        //System.out.println("Pp = " + Pp);
        GenPolynomial<AlgebraicNumber<C>> Ni;
        for (GenPolynomial<C> nfi : nfacs) {
            //System.out.println("nfi = " + nfi);
            Ni = PolyUfdUtil.<C> substituteConvertToAlgebraicCoefficients(pfac, nfi, ks);
            if (logger.isInfoEnabled()) {
                logger.info("Ni = " + Ni);
                //System.out.println("Pp = " + Pp);
            }
            // compute gcds of factors with polynomial
            GenPolynomial<AlgebraicNumber<C>> pni = engine.gcd(Ni, Pp);
            if (!pni.leadingBaseCoefficient().isONE()) {
                pni = pni.monic();
            }
            if (logger.isInfoEnabled()) {
                logger.info("gcd(Ni,Pp) = " + pni);
            }
            if (!pni.isONE()) {
                factors.add(pni);
                Pp = Pp.divide(pni);
            }
        }
        if (!Pp.isZERO() && !Pp.isONE()) { // irreducible rest
            factors.add(Pp);
        }
        //System.out.println("afactors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! (respectively monic)
     *            GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> factorsSquarefree(GenPolynomial<AlgebraicNumber<C>> P) {
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
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring; // Q(alpha)[x1,...,xn]
        if (pfac.nvar <= 1) {
            throw new IllegalArgumentException("only for multivariate polynomials");
        }
        //AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        AlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        if (P.degreeVector().totalDeg() <= 1L) {
            factors.add(P);
            return factors;
        }
        //System.out.println("\nP = " + P);

        // search squarefree norm
        long k = 0L;
        long ks = k;
        GenPolynomial<C> res = null;
        boolean sqf = false;
        //int[] klist = new int[]{ 0, 1, 2, 3, -1, -2, -3 , 5, -5, 7, -7, 101, -101, 1001, -1001 };
        //int[] klist = new int[]{ 0, 1, 2, 3, -1, -2, -3 , 5, -5, 7, -7, 23, -23, 167, -167 };
        //int[] klist = new int[] { 0, -1, -2, 1, 2, -3, 3 };
        int[] klist = new int[] { 0, -1, -2, 1, 2 };
        int ki = 0;
        while (!sqf) {
            // k = 0,1,2,-1,-2
            if (ki >= klist.length) {
                logger.warn("sqf(" + ks + ") = " + res.degree());
                break;
            }
            k = klist[ki];
            ki++;
            // compute norm with x -> ( y - k x )
            ks = k;
            res = PolyUfdUtil.<C> norm(P, ks);
            //System.out.println("res = " + res);
            if (res.isZERO() || res.isConstant()) {
                continue;
            }
            sqf = factorCoeff.isSquarefree(res);
            //System.out.println("resfact = " + factorCoeff.factors(res) + "\n");
        }
        // if Res is now squarefree, else must take radical factorization
        List<GenPolynomial<C>> nfacs;
        if (!sqf) {
            System.out.println("sqf_" + pfac.nvar + "(" + ks + ") = " + res.degree());
        }
        //res = res.monic();
        if (logger.isInfoEnabled()) {
            logger.info("res = " + res);
            logger.info("factorCoeff = " + factorCoeff);
        }
        nfacs = factorCoeff.factorsRadical(res);
        //System.out.println("\nnfacs = " + nfacs); // Q[X]
        if (logger.isInfoEnabled()) {
            logger.info("res facs = " + nfacs); // Q[X]
        }
        if (nfacs.size() == 1) {
            factors.add(P);
            return factors;
        }

        // compute gcds of factors with polynomial in Q(alpha)[X]
        GenPolynomial<AlgebraicNumber<C>> Pp = P;
        //System.out.println("Pp = " + Pp);
        GenPolynomial<AlgebraicNumber<C>> Ni;
        for (GenPolynomial<C> nfi : nfacs) {
            //System.out.println("nfi = " + nfi);
            Ni = PolyUfdUtil.<C> substituteConvertToAlgebraicCoefficients(pfac, nfi, ks);
            if (logger.isInfoEnabled()) {
                logger.info("Ni = " + Ni);
                //System.out.println("Pp = " + Pp);
            }
            // compute gcds of factors with polynomial
            GenPolynomial<AlgebraicNumber<C>> pni = engine.gcd(Ni, Pp);
            if (!pni.leadingBaseCoefficient().isONE()) {
                //System.out.println("gcd(Ni,Pp) not monic " + pni);
                pni = pni.monic();
            }
            if (logger.isInfoEnabled()) {
                logger.info("gcd(Ni,Pp) = " + pni);
            }
            //System.out.println("gcd(Ni,Pp) = " + pni);
            if (!pni.isONE()) {
                factors.add(pni);
                Pp = Pp.divide(pni);
            }
        }
        if (!Pp.isZERO() && !Pp.isONE()) { // irreducible rest
            factors.add(Pp);
        }
        //factors.add(P);
        return factors;
    }

}
