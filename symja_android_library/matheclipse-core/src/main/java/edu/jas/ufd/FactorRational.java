/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;


/**
 * Rational number coefficients factorization algorithms. This class implements
 * factorization methods for polynomials over rational numbers.
 *
 * @author Heinz Kredel
 */

public class FactorRational extends FactorAbsolute<BigRational> {


    private static final Logger logger = Logger.getLogger(FactorRational.class);


    private static final boolean debug = logger.isInfoEnabled();


    /**
     * Factorization engine for integer base coefficients.
     */
    protected final FactorAbstract<BigInteger> iengine;


    /**
     * No argument constructor.
     */
    protected FactorRational() {
        super(BigRational.ONE);
        iengine = FactorFactory.getImplementation(BigInteger.ONE);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     *
     * @param P squarefree GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<BigRational>> baseFactorsSquarefree(GenPolynomial<BigRational> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigRational>> factors = new ArrayList<GenPolynomial<BigRational>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigRational> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<BigRational> Pr = P;
        BigRational ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        BigInteger bi = BigInteger.ONE;
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(bi, pfac);
        GenPolynomial<BigInteger> Pi = PolyUtil.integerFromRationalCoefficients(ifac, Pr);
        if (debug) {
            logger.info("Pi = " + Pi);
        }
        List<GenPolynomial<BigInteger>> ifacts = iengine.baseFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<BigRational>> rfacts = PolyUtil.fromIntegerCoefficients(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        rfacts = PolyUtil.monic(rfacts);
        //System.out.println("rfacts = " + rfacts);
        if (!ldcf.isONE()) {
            GenPolynomial<BigRational> r = rfacts.get(0);
            rfacts.remove(r);
            r = r.multiply(ldcf);
            rfacts.set(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     *
     * @param P squarefree GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<BigRational>> factorsSquarefree(GenPolynomial<BigRational> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigRational>> factors = new ArrayList<GenPolynomial<BigRational>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigRational> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactorsSquarefree(P);
        }
        GenPolynomial<BigRational> Pr = P;
        BigRational ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        BigInteger bi = BigInteger.ONE;
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(bi, pfac);
        GenPolynomial<BigInteger> Pi = PolyUtil.integerFromRationalCoefficients(ifac, Pr);
        if (debug) {
            logger.info("Pi = " + Pi);
        }
        List<GenPolynomial<BigInteger>> ifacts = iengine.factorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<BigRational>> rfacts = PolyUtil.fromIntegerCoefficients(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        rfacts = PolyUtil.monic(rfacts);
        //System.out.println("rfacts = " + rfacts);
        if (!ldcf.isONE()) {
            GenPolynomial<BigRational> r = rfacts.get(0);
            rfacts.remove(r);
            r = r.multiply(ldcf);
            rfacts.set(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * GenPolynomial factorization of a polynomial.
     *
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     * p_i**e_i and p_i irreducible.
     */
    @Override
    public SortedMap<GenPolynomial<BigRational>, Long> factors(GenPolynomial<BigRational> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        GenPolynomialRing<BigRational> pfac = P.ring;
        SortedMap<GenPolynomial<BigRational>, Long> factors = new TreeMap<GenPolynomial<BigRational>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P, 1L);
            return factors;
        }
        if (pfac.nvar == 1) {
            return baseFactors(P);
        }
        GenPolynomial<BigRational> Pr = P;
        BigInteger bi = BigInteger.ONE;
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(bi, pfac);
        GenPolynomial<BigInteger> Pi = PolyUtil.integerFromRationalCoefficients(ifac, Pr);
        if (debug) {
            logger.info("Pi = " + Pi);
        }
        SortedMap<GenPolynomial<BigInteger>, Long> ifacts = iengine.factors(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        for (Map.Entry<GenPolynomial<BigInteger>, Long> me : ifacts.entrySet()) {
            GenPolynomial<BigInteger> g = me.getKey();
            if (g.isONE()) { // skip 1
                continue;
            }
            Long d = me.getValue(); // facs.get(g);
            GenPolynomial<BigRational> rp = PolyUtil.fromIntegerCoefficients(pfac, g);
            factors.put(rp, d);
        }
        return factors;
    }

}
