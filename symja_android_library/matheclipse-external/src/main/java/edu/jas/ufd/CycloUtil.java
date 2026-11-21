/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.arith.BigInteger;
import edu.jas.arith.PrimeInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.Power;


/**
 * Cyclotomic polynomial utilities. Adapted from Sympy Python codes.
 * @author Heinz Kredel
 */

public class CycloUtil {


    private static final Logger logger = LogManager.getLogger(CycloUtil.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Compute n-th cyclotomic polynomial.
     * @param n number of polynomial.
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
        logger.info("factors = {}", fac);

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
     * @param n number of polynomial.
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
        logger.info("factors = {}", fac);

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
     * @param p cyclotomic polynomial.
     * @return list of factors of cyclotomic polynomial p or empty list.
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


    /**
     * Largest degree for which {@link #cyclotomicFactorsDividing} searches.
     */
    private static final long MAX_CYCLOTOMIC_SPLIT_DEGREE = 2000L;


    /**
     * Per order n: {q, zeta} with q prime, q == 1 mod n, and zeta of exact order n in GF(q).
     */
    private static final java.util.concurrent.ConcurrentHashMap<Integer, long[]> ROOT_OF_UNITY_MOD_Q = new java.util.concurrent.ConcurrentHashMap<>();


    /**
     * Symja: the cyclotomic factors of a univariate integer polynomial which is not a binomial. Modular
     * factorization splits Phi_n into phi(n)/ord_n(p) factors for every prime p, so a polynomial with
     * cyclotomic factors makes the Hensel recombination search exponentially many subsets.
     * @param P univariate polynomial over the integers.
     * @return the list of cyclotomic polynomials Phi_n dividing P, each listed as often as it divides
     *         P, or an empty list.
     */
    public static List<GenPolynomial<BigInteger>> cyclotomicFactorsDividing(GenPolynomial<BigInteger> P) {
        List<GenPolynomial<BigInteger>> H = new ArrayList<GenPolynomial<BigInteger>>();
        if (P == null || P.ring.nvar != 1) {
            return H;
        }
        long d = P.degree(0);
        if (d < 2L || d > MAX_CYCLOTOMIC_SPLIT_DEGREE) {
            return H;
        }
        // n / phi(n) < 7 for n < 10^8, so phi(n) <= d implies n < 7*d
        int bound = (int) (7L * d + 30L);
        int[] phi = new int[bound + 1];
        for (int i = 0; i <= bound; i++) {
            phi[i] = i;
        }
        for (int i = 2; i <= bound; i++) {
            if (phi[i] == i) { // i is prime
                for (int j = i; j <= bound; j += i) {
                    phi[j] -= phi[j] / i;
                }
            }
        }
        GenPolynomial<BigInteger> Q = P;
        java.math.BigInteger[] dense = denseCoefficients(Q);
        for (int n = 1; n <= bound; n++) {
            PolyUtil.checkInterrupted();
            long dq = Q.degree(0);
            if (dq <= 0L) {
                break;
            }
            if (phi[n] > dq) {
                continue;
            }
            while (Q.degree(0) >= phi[n] && vanishesAtRootOfUnity(dense, n)) {
                GenPolynomial<BigInteger> c = cyclotomicPolynomial(Q.ring, n);
                if (!PolyUtil.<BigInteger> baseSparsePseudoRemainder(Q, c).isZERO()) {
                    break;
                }
                H.add(c);
                Q = Q.divide(c);
                dense = denseCoefficients(Q);
            }
        }
        return H;
    }


    /**
     * The coefficients of a univariate polynomial, indexed by degree.
     */
    private static java.math.BigInteger[] denseCoefficients(GenPolynomial<BigInteger> P) {
        java.math.BigInteger[] dense = new java.math.BigInteger[(int) P.degree(0) + 1];
        java.util.Arrays.fill(dense, java.math.BigInteger.ZERO);
        for (Monomial<BigInteger> m : P) {
            dense[(int) m.e.getVal(0)] = m.c.getVal();
        }
        return dense;
    }


    /**
     * Necessary condition for Phi_n | P: P vanishes at a primitive n-th root of unity of GF(q).
     */
    private static boolean vanishesAtRootOfUnity(java.math.BigInteger[] dense, int n) {
        long[] qz = ROOT_OF_UNITY_MOD_Q.computeIfAbsent(n, CycloUtil::rootOfUnityModQ);
        long q = qz[0];
        long z = qz[1];
        java.math.BigInteger bq = java.math.BigInteger.valueOf(q);
        long s = 0L;
        for (int i = dense.length - 1; i >= 0; i--) { // Horner
            java.math.BigInteger c = dense[i];
            long cm = c.bitLength() < 63 ? Math.floorMod(c.longValue(), q) : c.mod(bq).longValue();
            s = (s * z + cm) % q;
        }
        return s == 0L;
    }


    /**
     * A prime q == 1 mod n with 2^20 < q < 2^31 and an element of exact order n in GF(q).
     */
    private static long[] rootOfUnityModQ(int n) {
        long q = ((1L << 20) / n + 1L) * n + 1L;
        while (!java.math.BigInteger.valueOf(q).isProbablePrime(40)) {
            q += n;
        }
        if (q >= (1L << 31)) {
            throw new ArithmeticException("no prime q == 1 mod " + n + " below 2^31");
        }
        java.util.Set<Long> primeDivisors = PrimeInteger.factors(n).keySet();
        long cofactor = (q - 1L) / n;
        for (long a = 2L; a < q; a++) {
            long z = powMod(a, cofactor, q);
            boolean primitive = true;
            for (long r : primeDivisors) {
                if (powMod(z, n / r, q) == 1L) {
                    primitive = false;
                    break;
                }
            }
            if (primitive) {
                return new long[] { q, z };
            }
        }
        throw new ArithmeticException("no root of unity of order " + n + " mod " + q);
    }


    /**
     * b^e mod q for q < 2^31.
     */
    private static long powMod(long b, long e, long q) {
        long r = 1L;
        b %= q;
        while (e > 0L) {
            if ((e & 1L) != 0L) {
                r = r * b % q;
            }
            b = b * b % q;
            e >>= 1;
        }
        return r;
    }

}
