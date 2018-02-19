/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import edu.jas.arith.BigInteger;
import edu.jas.arith.PrimeInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.structure.Power;


/**
 * Cyclotomic polynomial utilities. Adapted from Sympy Python codes.
 *
 * @author Heinz Kredel
 */

public class CycloUtil {


    private static final Logger logger = Logger.getLogger(CycloUtil.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Compute n-th cyclotomic polynomial.
     *
     * @param n    number of polynomial.
     * @param ring univariate polynomial ring of cyclotomic polynomial.
     * @return n-th cyclotomic polynomial.
     */
    public static GenPolynomial<BigInteger> cyclotomicPolynomial(GenPolynomialRing<BigInteger> ring, long n) {
        GenPolynomialRing<BigInteger> pfac = ring;
        if (pfac == null) {
            throw new IllegalArgumentException("ring must be non null");
        }
        GenPolynomial<BigInteger> h = pfac.univariate(0).subtract(pfac.getONE());
        //System.out.println("h = " + h);
        SortedMap<Long, Integer> fac = PrimeInteger.factors(n);
        logger.info("factors = " + fac);

        for (Map.Entry<Long, Integer> m : fac.entrySet()) {
            // h = dup_quo(dup_inflate(h, p, K), h, K)
            // h = dup_inflate(h, p**(k - 1), K)
            long p = m.getKey();
            int k = m.getValue();
            GenPolynomial<BigInteger> ih = h.inflate(p);
            //System.out.println("ih = " + ih);
            h = ih.divide(h);
            //System.out.println("h = " + h);
            h = h.inflate(Power.power(p, k - 1));
            //System.out.println("h = " + h + ", power = " + Power.power(p, k-1));
        }
        return h;
    }


    /**
     * Compute the factors of the n-th cyclotomic polynomial.
     *
     * @param n    number of polynomial.
     * @param ring univariate polynomial ring of cyclotomic polynomial.
     * @return list of factors of n-th cyclotomic polynomial.
     */
    public static List<GenPolynomial<BigInteger>> cyclotomicDecompose(GenPolynomialRing<BigInteger> ring,
                                                                      long n) {
        GenPolynomialRing<BigInteger> pfac = ring;
        if (pfac == null) {
            throw new IllegalArgumentException("ring must be non null");
        }
        GenPolynomial<BigInteger> q = pfac.univariate(0).subtract(pfac.getONE());
        //System.out.println("q = " + q);
        List<GenPolynomial<BigInteger>> H = new ArrayList<GenPolynomial<BigInteger>>();
        H.add(q);

        SortedMap<Long, Integer> fac = PrimeInteger.factors(n);
        logger.info("factors = " + fac);

        for (Map.Entry<Long, Integer> m : fac.entrySet()) {
            //Q = [ dup_quo(dup_inflate(h, p, K), h, K) for h in H ]
            //H.extend(Q)
            long p = m.getKey();
            int k = m.getValue();
            List<GenPolynomial<BigInteger>> Q = new ArrayList<GenPolynomial<BigInteger>>();
            for (GenPolynomial<BigInteger> h : H) {
                GenPolynomial<BigInteger> g = h.inflate(p).divide(h);
                Q.add(g);
            }
            //System.out.println("Q = " + Q);
            H.addAll(Q);
            //for i in xrange(1, k):
            //    Q = [ dup_inflate(q, p, K) for q in Q ]
            //    H.extend(Q)
            for (int i = 1; i < k; i++) {
                List<GenPolynomial<BigInteger>> P = new ArrayList<GenPolynomial<BigInteger>>();
                for (GenPolynomial<BigInteger> h : Q) {
                    GenPolynomial<BigInteger> g = h.inflate(p);
                    P.add(g);
                }
                //System.out.println("P = " + P);
                Q = P;
                H.addAll(P);
            }
        }
        return H;
    }


    /**
     * Compute the factors of the cyclotomic polynomial.
     *
     * @param p cyclotomic polynomial.
     * @return list of factors of cyclotomic polynomial p or emtpy list.
     */
    public static List<GenPolynomial<BigInteger>> cyclotomicFactors(GenPolynomial<BigInteger> p) {
        List<GenPolynomial<BigInteger>> H = new ArrayList<GenPolynomial<BigInteger>>();
        long n = p.degree();
        if (n <= 0) {
            return H;
        }
        BigInteger lc, tc;
        lc = p.leadingBaseCoefficient();
        tc = p.trailingBaseCoefficient();
        if (!lc.isONE() || (!tc.isONE() && !tc.negate().isONE())) {
            return H;
        }
        if (p.length() != 2) { // other coefficients must be zero
            return H;
        }
        // only case: x**n +/- 1
        //F = _dup_cyclotomic_decompose(n, K)
        //if not K.is_one(tc_f):
        //   return F
        GenPolynomialRing<BigInteger> pfac = p.ring;
        List<GenPolynomial<BigInteger>> F = cyclotomicDecompose(pfac, n);
        if (!tc.isONE()) {
            return F;
        }
        //else:
        //   H = []
        //   for h in _dup_cyclotomic_decompose(2*n, K):
        //       if h not in F:
        //          H.append(h)
        //return H                         
        H = new ArrayList<GenPolynomial<BigInteger>>();
        List<GenPolynomial<BigInteger>> F2 = cyclotomicDecompose(pfac, 2L * n);
        for (GenPolynomial<BigInteger> h : F2) {
            if (!F.contains(h)) {
                H.add(h);
            }
        }
        return H;
    }


    /**
     * Test for cyclotomic polynomial.
     *
     * @param p polynomial.
     * @return true if p is a cyclotomic polynomial, else false.
     */
    public static boolean isCyclotomicPolynomial(GenPolynomial<BigInteger> p) {
        long n = p.degree();
        if (n <= 0) {
            return false;
        }
        BigInteger lc, tc;
        lc = p.leadingBaseCoefficient();
        tc = p.trailingBaseCoefficient();
        if (!lc.isONE() || (!tc.isONE() && !tc.negate().isONE())) {
            //System.out.println("!lc.isONE() || (!tc.isONE() && !tc.negate().isONE())");
            return false;
        }
        if (p.length() == 2) { // other coefficients must be zero
            return true;
        }
        // ignore: if not irreducible:
        GenPolynomialRing<BigInteger> ring = p.ring;
        if (ring.nvar != 1) {
            throw new IllegalArgumentException("not univariate polynomial");
        }
        GenPolynomial<BigInteger> g, h, f;
        g = ring.getZERO().copy();
        h = ring.getZERO().copy();
        long x = n % 2;
        for (Monomial<BigInteger> m : p) {
            ExpVector e = m.e;
            long d = e.getVal(0);
            ExpVector e2 = e.subst(0, d / 2L);
            if (d % 2 == x) {
                g.doPutToMap(e2, m.c);
            } else {
                h.doPutToMap(e2, m.c);
            }
        }
        //g = dup_sqr(dup_strip(g), K)
        //h = dup_sqr(dup_strip(h), K)
        g = g.multiply(g);
        h = h.multiply(h);
        //System.out.println("g = " + g);
        //System.out.println("h = " + h);
        //F = dup_sub(g, dup_lshift(h, 1, K), K)
        ExpVector on = ExpVector.create(1, 0, 1L);
        f = g.subtract(h.multiply(on)).abs();
        //System.out.println("f = " + f + ", f==p: " + f.equals(p));
        //if F == f: return True 
        if (f.equals(p)) {
            return true;
        }
        //g = dup_mirror(f, K)
        //if F == g and dup_cyclotomic_p(g, K):
        //   return True
        g = ring.getZERO().copy();
        for (Monomial<BigInteger> m : p) {
            ExpVector e = m.e;
            long d = e.getVal(0);
            if (d % 2 == 1) {
                g.doPutToMap(e, m.c.negate());
            } else {
                g.doPutToMap(e, m.c);
            }
        }
        g = g.abs();
        //System.out.println("g = " + g + ", f==g: " + f.equals(g));
        if (f.equals(g) && isCyclotomicPolynomial(g)) {
            return true;
        }
        //G = dup_sqf_part(F, K)
        Squarefree<BigInteger> engine;
        engine = SquarefreeFactory.getImplementation(lc);
        GenPolynomial<BigInteger> G;
        G = engine.squarefreePart(f);
        //System.out.println("G = " + G + ", G^2==f: " + G.multiply(G).equals(f));
        //if dup_sqr(G, K) == F and dup_cyclotomic_p(G, K):
        //   return True
        if (G.multiply(G).equals(f) && isCyclotomicPolynomial(G)) {
            return true;
        }
        return false;
    }

}
