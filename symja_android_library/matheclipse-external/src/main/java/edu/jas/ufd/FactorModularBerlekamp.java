/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.ModLongRing;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenVectorModul;
import edu.jas.vector.LinAlg;


/**
 * Modular coefficients Berlekamp factorization algorithms. This class
 * implements Berlekamp, Cantor and Zassenhaus factorization methods for
 * polynomials over (prime) modular integers.
 * @author Heinz Kredel
 */

public class FactorModularBerlekamp<MOD extends GcdRingElem<MOD>> extends FactorAbsolute<MOD> {


    private static final Logger logger = LogManager.getLogger(FactorModularBerlekamp.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * No argument constructor, do not use.
     */
    @SuppressWarnings({ "unchecked", "unused" })
    private FactorModularBerlekamp() {
        this((RingFactory<MOD>) (Object) new ModLongRing(13, true)); // hack, 13 unimportant
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorModularBerlekamp(RingFactory<MOD> cfac) {
        super(cfac);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    @Override
    public List<GenPolynomial<MOD>> baseFactorsSquarefree(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null not allowed");
        }
        //ModularRingFactory cfac = (ModularRingFactory) P.ring.coFac;
        long q = P.ring.coFac.characteristic().longValueExact();
        if (q < 100) { // todo, 32003 too high
            return baseFactorsSquarefreeSmallPrime(P);
        }
        return baseFactorsSquarefreeBigPrime(P);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial. Small prime
     * version of Berlekamps algorithm.
     * @param P squarefree and monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    @SuppressWarnings("unchecked")
    public List<GenPolynomial<MOD>> baseFactorsSquarefreeSmallPrime(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        List<GenPolynomial<MOD>> factors = new ArrayList<GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        if (!P.leadingBaseCoefficient().isONE()) {
            throw new IllegalArgumentException("ldcf(P) != 1: " + P);
        }
        if (P.isONE() || P.degree(0) <= 1) {
            factors.add(P);
            return factors;
        }
        ArrayList<ArrayList<MOD>> Q = PolyUfdUtil.<MOD> constructQmatrix(P);
        //System.out.println("Q = " + Q);
        int n = Q.size();
        int m = Q.get(0).size();
        GenMatrixRing<MOD> mfac = new GenMatrixRing<MOD>(pfac.coFac, n, m);
        GenMatrix<MOD> Qm = new GenMatrix<MOD>(mfac, Q);
        GenMatrix<MOD> Qm1 = Qm.subtract(mfac.getONE());
        //System.out.println("Qm1 = " + Qm1);
        LinAlg<MOD> lu = new LinAlg<MOD>();
        List<GenVector<MOD>> Nsb = lu.nullSpaceBasis(Qm1);
        logger.info("Nsb = " + Nsb);
        int k = Nsb.size();
        if (k == 1) {
            factors.add(P);
            return factors;
        }
        //int d = (int) P.degree(0);
        GenMatrix<MOD> Ns = mfac.fromVectors(Nsb);
        GenMatrix<MOD> L1 = Ns.negate(); //mfac.getONE().subtract(Ns);
        //System.out.println("L1 = " + L1);
        List<GenPolynomial<MOD>> trials = new ArrayList<GenPolynomial<MOD>>();
        for (int i = 0; i < L1.ring.rows; i++) {
            GenVector<MOD> rv = L1.getRow(i);
            GenPolynomial<MOD> rp = pfac.fromVector(rv);
            if (!rp.isONE()) {
                trials.add(rp);
            }
        }
        logger.info("#ofFactors k = " + k);
        logger.info("trials = " + trials);
        factors.add(P);
        for (GenPolynomial<MOD> t : trials) {
            if (factors.size() == k) {
                break;
            }
            //System.out.println("t = " + t);
            // depth first search, since Iterator is finite
            GenPolynomial<MOD> a = factors.remove(0);
            //System.out.println("a = " + a);
            MOD s = null;
            Iterator<MOD> eit = null;
            if (pfac.coFac instanceof ModularRingFactory) {
                eit = ((ModularRingFactory) pfac.coFac).iterator();
            } else if (pfac.coFac instanceof AlgebraicNumberRing) {
                eit = ((AlgebraicNumberRing) pfac.coFac).iterator();
            } else {
                throw new IllegalArgumentException("no iterator for: " + pfac.coFac);
            }
            //System.out.println("eit = " + eit);
            while (eit.hasNext()) {
                s = eit.next();
                //System.out.println("s = " + s);
                GenPolynomial<MOD> v = t.subtract(s);
                GenPolynomial<MOD> g = v.gcd(a);
                if (g.isONE() || g.equals(a)) {
                    continue;
                }
                factors.add(g);
                a = a.divide(g);
                logger.info("s = " + s + ", g = " + g + ", a = " + a);
                if (a.isONE()) {
                    break;
                }
            }
            if (!a.isONE()) {
                factors.add(a);
            }
        }
        //System.out.println("factors  = " + factors);
        factors = PolyUtil.<MOD> monic(factors);
        SortedSet<GenPolynomial<MOD>> ss = new TreeSet<GenPolynomial<MOD>>(factors);
        //System.out.println("sorted   = " + ss);
        factors.clear();
        factors.addAll(ss);
        return factors;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial. Big prime
     * version of Berlekamps algorithm.
     * @param P squarefree and monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<MOD>> baseFactorsSquarefreeBigPrime(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        List<GenPolynomial<MOD>> factors = new ArrayList<GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        if (!P.leadingBaseCoefficient().isONE()) {
            throw new IllegalArgumentException("ldcf(P) != 1: " + P);
        }
        if (P.isONE() || P.degree(0) <= 1) {
            factors.add(P);
            return factors;
        }
        ArrayList<ArrayList<MOD>> Q = PolyUfdUtil.<MOD> constructQmatrix(P);
        //System.out.println("Q = " + Q);
        int n = Q.size();
        int m = Q.get(0).size();
        GenMatrixRing<MOD> mfac = new GenMatrixRing<MOD>(pfac.coFac, n, m);
        GenMatrix<MOD> Qm = new GenMatrix<MOD>(mfac, Q);
        GenMatrix<MOD> Qm1 = Qm.subtract(mfac.getONE());
        //System.out.println("Qm1 = " + Qm1);
        LinAlg<MOD> lu = new LinAlg<MOD>();
        List<GenVector<MOD>> Nsb = lu.nullSpaceBasis(Qm1);
        logger.info("Nsb = " + Nsb);
        int k = Nsb.size();
        if (k == 1) {
            factors.add(P);
            return factors;
        }
        //int d = (int) P.degree(0);
        GenMatrix<MOD> Ns = mfac.fromVectors(Nsb);
        GenMatrix<MOD> L1 = Ns.negate(); //mfac.getONE().subtract(Ns);
        //System.out.println("L1 = " + L1);
        List<GenPolynomial<MOD>> trials = new ArrayList<GenPolynomial<MOD>>();
        for (int i = 0; i < L1.ring.rows; i++) {
            GenVector<MOD> rv = L1.getRow(i);
            GenPolynomial<MOD> rp = pfac.fromVector(rv);
            trials.add(rp);
        }
        logger.info("#ofFactors k = " + k);
        logger.info("trials = " + trials);
        factors.add(P);
        GenVectorModul<MOD> vfac = new GenVectorModul<MOD>(pfac.coFac, k);
        //long q = pfac.coFac.characteristic().longValueExact();
        //long lq = Power.logarithm(2, q);
        java.math.BigInteger q = pfac.coFac.characteristic(); //.longValueExact();
        int lq = q.bitLength(); //Power.logarithm(2, q);
        if (pfac.coFac instanceof AlgebraicNumberRing) {
            lq = (int) ((AlgebraicNumberRing) pfac.coFac).extensionDegree();
            q = q.pow(lq); //Power.power(q, lq);
        }
        logger.info("char = " + pfac.coFac.characteristic() + ", q = " + q + ", lq = " + lq);
        do {
            // breadth first search, since some a might be irreducible
            GenPolynomial<MOD> a = factors.remove(0);
            //System.out.println("a = " + a);
            if (a.degree(0) <= 1) {
                factors.add(a);
                continue;
            }
            GenVector<MOD> rv = vfac.random(8, 0.95f);
            //System.out.println("rv = " + rv.toScript());
            GenPolynomial<MOD> rpol = pfac.getZERO();
            int i = 0;
            for (GenPolynomial<MOD> t : trials) {
                MOD c = rv.get(i++);
                GenPolynomial<MOD> s = t.multiply(c);
                rpol = rpol.sum(s);
            }
            rpol = rpol.monic();
            if (rpol.isONE()) {
                factors.add(a);
                continue;
            }
            //System.out.println("rpol = " + rpol.toScript());
            if (!q.testBit(0)) { // q % 2 == 0
                long e = lq - 1;
                //System.out.println("q = " + q + ", e = " + e);
                GenPolynomial<MOD> pow = rpol;
                GenPolynomial<MOD> v = rpol;
                for (int l = 1; l < e; l++) {
                    pow = pow.multiply(pow).remainder(a);
                    v = v.sum(pow);
                }
                rpol = v.remainder(a).monic(); // automatic monic
                //System.out.println("sum_l rpol^l = " + rpol.toScript());
            } else {
                //long e = (q - 1) / 2;
                java.math.BigInteger e = q.subtract(java.math.BigInteger.ONE).shiftRight(1);
                //System.out.println("q = " + q + ", e = " + e);
                GenPolynomial<MOD> pow = Power.<GenPolynomial<MOD>> modPositivePower(rpol, e, a);
                rpol = pow.subtract(pfac.getONE()).monic();
                //System.out.println("rpol^e-1 = " + rpol.toScript());
            }
            if (rpol.isZERO() || rpol.isONE()) {
                factors.add(a);
                continue;
            }
            GenPolynomial<MOD> g = rpol.gcd(a);
            if (g.isONE()) {
                factors.add(a);
                continue;
            }
            factors.add(g);
            //System.out.println("a = " + a);
            a = a.divide(g);
            logger.info("rv = " + rv + ", g = " + g + ", a/g = " + a);
            if (!a.isONE()) {
                factors.add(a);
            }
            //System.out.println("factors  = " + factors);
        } while (factors.size() < k);

        //System.out.println("factors  = " + factors);
        factors = PolyUtil.<MOD> monic(factors);
        SortedSet<GenPolynomial<MOD>> ss = new TreeSet<GenPolynomial<MOD>>(factors);
        //System.out.println("sorted   = " + ss);
        factors.clear();
        factors.addAll(ss);
        return factors;
    }

}
