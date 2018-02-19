/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Rational function coefficients factorization algorithms. This class
 * implements factorization methods for polynomials over rational functions,
 * that is, with coefficients from class <code>application.Quotient</code>.
 *
 * @author Heinz Kredel
 */

public class FactorQuotient<C extends GcdRingElem<C>> extends FactorAbstract<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(FactorQuotient.class);


    //private static final boolean debug = logger.isInfoEnabled();


    /**
     * Factorization engine for normal coefficients.
     */
    protected final FactorAbstract<C> nengine;


    /**
     * No argument constructor.
     */
    protected FactorQuotient() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     *
     * @param fac coefficient quotient ring factory.
     */
    public FactorQuotient(QuotientRing<C> fac) {
        this(fac, FactorFactory.<C>getImplementation(fac.ring.coFac));
    }


    /**
     * Constructor.
     *
     * @param fac     coefficient quotient ring factory.
     * @param nengine factorization engine for polynomials over base
     *                coefficients.
     */
    public FactorQuotient(QuotientRing<C> fac, FactorAbstract<C> nengine) {
        super(fac);
        this.nengine = nengine;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     *
     * @param P squarefree GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> baseFactorsSquarefree(GenPolynomial<Quotient<C>> P) {
        return factorsSquarefree(P);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     *
     * @param P squarefree GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> factorsSquarefree(GenPolynomial<Quotient<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        //System.out.println("factorsSquarefree, P = " + P);
        List<GenPolynomial<Quotient<C>>> factors = new ArrayList<GenPolynomial<Quotient<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        GenPolynomial<Quotient<C>> Pr = P;
        Quotient<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        QuotientRing<C> qi = (QuotientRing<C>) pfac.coFac;
        GenPolynomialRing<C> ci = qi.ring;
        GenPolynomialRing<GenPolynomial<C>> ifac = new GenPolynomialRing<GenPolynomial<C>>(ci, pfac);
        GenPolynomial<GenPolynomial<C>> Pi = PolyUfdUtil.<C>integralFromQuotientCoefficients(ifac, Pr);
        //System.out.println("Pi = " + Pi);

        // factor in C[x_1,...,x_n][y_1,...,y_m]
        List<GenPolynomial<GenPolynomial<C>>> irfacts = nengine.recursiveFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("irfacts = " + irfacts);
        }
        if (irfacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<Quotient<C>>> qfacts = PolyUfdUtil.<C>quotientFromIntegralCoefficients(pfac,
                irfacts);
        //System.out.println("qfacts = " + qfacts);
        //qfacts = PolyUtil.monic(qfacts);
        //System.out.println("qfacts = " + qfacts);
        if (!ldcf.isONE()) {
            GenPolynomial<Quotient<C>> r = qfacts.get(0);
            qfacts.remove(r);
            r = r.multiply(ldcf);
            qfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("qfacts = " + qfacts);
        }
        factors.addAll(qfacts);
        return factors;
    }

}
