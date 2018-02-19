/*
 * $Id$
 */

package edu.jas.arith;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Integer prime factorization. Code from ALDES/SAC2 and MAS module SACPRIM.
 * <p>
 * See ALDES/SAC2 or MAS code in SACPRIM.
 * See Symja <code>org/matheclipse/core/expression/Primality.java</code> for Pollard
 * algorithm.
 *
 * @author Heinz Kredel
 */

public final class PrimeInteger {


    /**
     * Maximal long, which can be factored by IFACT(long). Has nothing to do
     * with SAC2.BETA.
     */
    final public static long BETA = PrimeList.getLongPrime(61, 1).longValue();
    /**
     * List of units of Z mod 210.
     */
    final public static List<Long> UZ210 = getUZ210();
    /**
     * Medium prime divisor range.
     */
    //final static long IMPDS_MIN = 1000;  // SAC2/Aldes 
    //final static long IMPDS_MAX = 5000;  // "
    //final static long IMPDS_MIN = 2000;  
    //final static long IMPDS_MAX = 10000; 
    final static long IMPDS_MIN = 10000;
    /**
     * List of small prime numbers.
     */
    final public static List<Long> SMPRM = smallPrimes(2, (int) (IMPDS_MIN >> 1));
    final static long IMPDS_MAX = 128000;
    /**
     * Random number generator.
     */
    //final static SecureRandom random = new SecureRandom();
    final static Random random = new Random();
    private static final Logger logger = Logger.getLogger(PrimeInteger.class);

    /**
     * Digit prime generator. K and m are positive beta-integers. L is the list
     * (p(1),...,p(r)) of all prime numbers p such that m le p lt m+2*K, with
     * p(1) lt p(2) lt ... lt p(r).
     * See also SACPRIM.DPGEN.
     *
     * @param m start integer
     * @param K number of integers
     * @return the list L of prime numbers p with m &le; p &lt; m + 2*K.
     */
    public static List<Long> smallPrimes(long m, int K) {
        int k;
        long ms;
        ms = m;
        if (ms <= 1) {
            ms = 1;
        }
        m = ms;
        if (m % 2 == 0) {
            m++;
            K--;
        }
        //if (kp % 2 == 0) {
        //    k = kp/2;
        //} else {
        //    k = (kp+1)/2;
        //}
        k = K;

        /* init */
        long h = 2 * (k - 1);
        long m2 = m + h; // mp
        BitSet p = new BitSet(k);
        p.set(0, k);
        //for (int i = 0; i < k; i++) {
        //    p.set(i);
        //}

        /* compute */
        int r, d = 0;
        int i, c = 0;
        while (true) {
            switch (c) {
            /* mark multiples of d for d=3 and d=6n-/+1 with d**2<=m2 */
                case 2:
                    d += 2;
                    c = 3;
                    break;
                case 3:
                    d += 4;
                    c = 2;
                    break;
                case 0:
                    d = 3;
                    c = 1;
                    break;
                case 1:
                    d = 5;
                    c = 2;
                    break;
                default:
                    throw new RuntimeException("this should not happen");
            }
            if (d > (m2 / d)) {
                break;
            }
            r = (int) (m % d);
            if (r + h >= d || r == 0) {
                if (r == 0) {
                    i = 0;
                } else {
                    if (r % 2 == 0) {
                        i = d - (r / 2);
                    } else {
                        i = (d - r) / 2;
                    }
                }
                if (m <= d) {
                    i += d;
                }
                while (i < k) {
                    p.set(i, false);
                    i += d;
                }
            }
        }
        /* output */
        int l = p.cardinality(); // l = 0
        //for (i=0; i<k; i++) {
        //    if (p.get(i)) {
        //         l++;
        //     }
        //}
        if (ms <= 2) {
            l++;
        }
        //if (ms <= 1) {
        //}
        List<Long> po = new ArrayList<Long>(l);
        if (l == 0) {
            return po;
        }
        //l = 0;
        if (ms == 1) {
            //po.add(2);
            //l++;
            p.set(0, false);
        }
        if (ms <= 2) {
            po.add(2L);
            //l++;
        }
        long pl = m;
        //System.out.println("pl = " + pl + " p[0] = " + p[0]);
        //System.out.println("k-1 = " + (k-1) + " p[k-1] = " + p[k-1]);
        for (i = 0; i < k; i++) {
            if (p.get(i)) {
                po.add(pl);
                //l++;
            }
            pl += 2;
        }
        //System.out.println("SMPRM = " + po);
        return po;
    }

    /**
     * Integer small prime divisors. n is a positive integer. F is a list of
     * primes (q(1),q(2),...,q(h)), h non-negative, q(1) le q(2) le ... lt q(h),
     * such that n is equal to m times the product of the q(i) and m is not
     * divisible by any prime in SMPRM. Either m=1 or m gt 1,000,000.
     * <br /> In JAS F is a map and m=1 or m &gt; 4.000.000.
     * See also SACPRIM.ISPD.
     *
     * @param n integer to factor.
     * @param F a map of pairs of prime numbers and multiplicities (p,e) with p**e
     *          divides n and e maximal, F is modified.
     * @return n/F a factor of n not divisible by any prime number in SMPRM.
     */
    public static long smallPrimeDivisors(long n, SortedMap<Long, Integer> F) {
        //SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        List<Long> LP;
        long QL = 0;
        long PL;
        long RL = 0;
        boolean TL;

        long ML = n;
        LP = SMPRM; //smallPrimes(2, 500); //SMPRM;
        TL = false;
        int i = 0;
        do {
            PL = LP.get(i);
            QL = ML / PL;
            RL = ML % PL;
            if (RL == 0) {
                Integer e = F.get(PL);
                if (e == null) {
                    e = 1;
                } else {
                    e++;
                }
                F.put(PL, e);
                ML = QL;
            } else {
                i++;
            }
            TL = (QL <= PL);
        } while (!(TL || (i >= LP.size())));
        //System.out.println("TL = " + TL + ", ML = " + ML + ", PL = " + PL + ", QL = " + QL);
        if (TL && (ML != 1L)) {
            Integer e = F.get(ML);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(ML, e);
            ML = 1;
        }
        //F.put(ML, 0); // hack
        return ML;
    }

    /**
     * Integer small prime divisors. n is a positive integer. F is a list of
     * primes (q(1),q(2),...,q(h)), h non-negative, q(1) le q(2) le ... lt q(h),
     * such that n is equal to m times the product of the q(i) and m is not
     * divisible by any prime in SMPRM. Either m=1 or m gt 1,000,000.
     * <br /> In JAS F is a map and m=1 or m &gt; 4.000.000.
     * See also SACPRIM.ISPD.
     *
     * @param n integer to factor.
     * @param F a map of pairs of prime numbers and multiplicities (p,e) with p**e
     *          divides n and e maximal, F is modified.
     * @return n/F a factor of n not divisible by any prime number in SMPRM.
     */
    public static java.math.BigInteger smallPrimeDivisors(java.math.BigInteger n, SortedMap<java.math.BigInteger, Integer> F) {
        List<Long> LP;
        java.math.BigInteger QL = java.math.BigInteger.ZERO;
        java.math.BigInteger PL;
        java.math.BigInteger RL = java.math.BigInteger.ZERO;
        boolean TL;

        java.math.BigInteger ML = n;
        LP = SMPRM; //smallPrimes(2, 500); //SMPRM;
        TL = false;
        int i = 0;
        do {
            PL = java.math.BigInteger.valueOf(LP.get(i));
            java.math.BigInteger[] xx = ML.divideAndRemainder(PL);
            QL = xx[0]; //ML.divide(PL);
            RL = xx[1]; //ML.remainder(PL);
            if (RL.equals(java.math.BigInteger.ZERO)) {
                Integer e = F.get(PL);
                if (e == null) {
                    e = 1;
                } else {
                    e++;
                }
                F.put(PL, e);
                ML = QL;
            } else {
                i++;
            }
            TL = (QL.compareTo(PL) <= 0);
        } while (!(TL || (i >= LP.size())));
        //System.out.println("TL = " + TL + ", ML = " + ML + ", PL = " + PL + ", QL = " + QL);
        if (TL && (!ML.equals(java.math.BigInteger.ONE))) {
            Integer e = F.get(ML);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(ML, e);
            ML = java.math.BigInteger.ONE;
        }
        //F.put(ML, 0); // hack
        return ML;
    }

    /**
     * Integer primality test. n is a positive integer. r is true, if n is
     * prime, else false.
     *
     * @param n integer to test.
     * @return true if n is prime, else false.
     */
    public static boolean isPrime(long n) {
        java.math.BigInteger N = java.math.BigInteger.valueOf(n);
        if (N.isProbablePrime(N.bitLength())) {
            return true;
        }
        SortedMap<Long, Integer> F = factors(n);
        return (F.size() == 1) && F.values().contains(1);
    }

    /**
     * Test prime factorization. n is a positive integer. r is true, if n =
     * product_i(pi**ei) and each pi is prime, else false.
     *
     * @param n integer to test.
     * @param F a map of pairs of prime numbers (p,e) with p**e divides n.
     * @return true if n = product_i(pi**ei) and each pi is prime, else false.
     */
    public static boolean isPrimeFactorization(long n, SortedMap<Long, Integer> F) {
        long f = 1L;
        for (Map.Entry<Long, Integer> m : F.entrySet()) {
            long p = m.getKey();
            if (!isPrime(p)) {
                return false;
            }
            int e = m.getValue();
            long pe = java.math.BigInteger.valueOf(p).pow(e).longValue();
            f *= pe;
        }
        return n == f;
    }

    /**
     * Test factorization. n is a positive integer. r is true, if n =
     * product_i(pi**ei), else false.
     *
     * @param n integer to test.
     * @param F a map of pairs of numbers (p,e) with p**e divides n.
     * @return true if n = product_i(pi**ei), else false.
     */
    public static boolean isFactorization(long n, SortedMap<Long, Integer> F) {
        long f = 1L;
        for (Map.Entry<Long, Integer> m : F.entrySet()) {
            long p = m.getKey();
            int e = m.getValue();
            long pe = java.math.BigInteger.valueOf(p).pow(e).longValue();
            f *= pe;
        }
        return n == f;
    }

    /**
     * Integer factorization. n is a positive integer. F is a list (q(1),
     * q(2),...,q(h)) of the prime factors of n, q(1) le q(2) le ... le q(h),
     * with n equal to the product of the q(i). <br /> In JAS F is a map.
     * See also SACPRIM.IFACT.
     *
     * @param n integer to factor.
     * @return a map of pairs of numbers (p,e) with p**e divides n.
     */
    public static SortedMap<Long, Integer> factors(long n) {
        if (n > BETA) {
            throw new UnsupportedOperationException("factors(long) only for longs less than BETA: " + BETA);
        }
        long ML, PL, AL, BL, CL, MLP, RL, SL;
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        SortedMap<Long, Integer> FP = null;
        // search small prime factors
        ML = smallPrimeDivisors(n, F); // , F, ML
        if (ML == 1L) {
            return F;
        }
        //System.out.println("F = " + F);
        // search medium prime factors
        AL = IMPDS_MIN;
        do {
            MLP = ML - 1;
            RL = (new ModLong(new ModLongRing(ML), 3)).power(MLP).getVal(); //(3**MLP) mod ML;
            if (RL == 1L) {
                FP = factors(MLP);
                SL = primalityTestSelfridge(ML, MLP, FP);
                if (SL == 1) {
                    logger.info("primalityTestSelfridge: FP = " + FP);
                    Integer e = F.get(ML);
                    if (e == null) {
                        e = 1;
                    } else { // will not happen
                        e++;
                    }
                    F.put(ML, e);
                    return F;
                }
            }
            CL = Roots.sqrtInt(new BigInteger(ML)).getVal().longValue(); //SACI.ISQRT( ML, CL, TL );
            //System.out.println("CL = " + CL + ", ML = " + ML + ", CL^2 = " + (CL*CL));
            BL = Math.max(IMPDS_MAX, CL / 3L);
            if (AL > BL) {
                PL = 1L;
            } else {
                logger.info("mediumPrimeDivisorSearch: a = " + AL + ", b = " + BL);
                PL = mediumPrimeDivisorSearch(ML, AL, BL); //, PL, ML );
                //System.out.println("PL = " + PL);
                if (PL != 1L) {
                    AL = PL;
                    Integer e = F.get(PL);
                    if (e == null) {
                        e = 1;
                    } else {
                        e++;
                    }
                    F.put(PL, e);
                    ML = ML / PL;
                }
            }
        } while (PL != 1L);
        // fixed: the ILPDS should also be in the while loop, was already wrong in SAC2/Aldes and MAS
        // seems to be okay for integers smaller than beta
        java.math.BigInteger N = java.math.BigInteger.valueOf(ML);
        if (N.isProbablePrime(N.bitLength())) {
            F.put(ML, 1);
            return F;
        }
        AL = BL;
        BL = CL;
        logger.info("largePrimeDivisorSearch: a = " + AL + ", b = " + BL + ", m = " + ML);
        // search large prime factors
        do {
            //ILPDS( ML, AL, BL, PL, ML );
            PL = largePrimeDivisorSearch(ML, AL, BL);
            if (PL != 1L) {
                Integer e = F.get(PL);
                if (e == null) {
                    e = 1;
                } else {
                    e++;
                }
                F.put(PL, e);
                ML = ML / PL;
                AL = PL;
                CL = Roots.sqrtInt(BigInteger.valueOf(ML)).getVal().longValue(); //SACI.ISQRT( ML, CL, TL );
                //System.out.println("CL = " + CL + ", ML = " + ML + ", CL^2 = " + (CL*CL));
                BL = Math.min(BL, CL);
                if (AL > BL) {
                    PL = 1L;
                }
            }
        } while (PL != 1L);
        //System.out.println("PL = " + PL + ", ML = " + ML);
        if (ML != 1L) {
            Integer e = F.get(ML);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(ML, e);
        }
        return F;
    }

    /**
     * Integer factorization, Pollard rho algorithm. n is a positive integer. F
     * is a list (q(1), q(2),...,q(h)) of the prime factors of n, q(1) le q(2)
     * le ... le q(h), with n equal to the product of the q(i). <br /> In
     * JAS F is a map.
     * See also SACPRIM.IFACT.
     *
     * @param n integer to factor.
     * @return a map F of pairs of numbers (p,e) with p**e divides n and p
     * probable prime.
     */
    public static SortedMap<Long, Integer> factorsPollard(long n) {
        if (n > BETA) {
            throw new UnsupportedOperationException("factors(long) only for longs less than BETA: " + BETA);
        }
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        factorsPollardRho(n, F);
        return F;
    }

    /**
     * Integer medium prime divisor search. n, a and b are positive integers
     * such that a le b le n and n has no positive divisors less than a. If n
     * has a prime divisor in the closed interval from a to b then p is the
     * least such prime and q=n/p. Otherwise p=1 and q=n.
     * See also SACPRIM.IMPDS.
     *
     * @param n integer to factor.
     * @param a lower bound.
     * @param b upper bound.
     * @return p a prime factor of n, with a &le; p &le; b &lt; n.
     */
    public static long mediumPrimeDivisorSearch(long n, long a, long b) {
        List<Long> LP;
        long R, J1Y, RL1, RL2, RL, PL;

        RL = a % 210;
        LP = UZ210;
        long ll = LP.size();
        int i = 0;
        while (RL > LP.get(i)) {
            i++;
        }
        RL1 = LP.get(i);
        PL = a + (RL1 - RL);
        //System.out.println("PL = " + PL + ", BL = " + BL);
        while (PL <= b) {
            R = n % PL; //SACI.IQR( NL, PL, QL, R );
            if (R == 0) {
                return PL;
            }
            i++;
            if (i >= ll) {
                LP = UZ210;
                RL2 = (RL1 - 210L);
                i = 0;
            } else {
                RL2 = RL1;
            }
            RL1 = LP.get(i);
            J1Y = (RL1 - RL2);
            PL = PL + J1Y;
        }
        PL = 1L; //SACI.IONE;
        //QL = NL;
        return PL;
    }

    /**
     * Integer selfridge primality test. m is an integer greater than or equal
     * to 3. mp=m-1. F is a list (q(1),q(2),...,q(k)), q(1) le q(2) le ... le
     * q(k), of the prime factors of mp, with mp equal to the product of the
     * q(i). An attempt is made to find a root of unity modulo m of order m-1.
     * If the existence of such a root is discovered then m is prime and s=1. If
     * it is discovered that no such root exists then m is not a prime and s=-1.
     * Otherwise the primality of m remains uncertain and s=0.
     * See also SACPRIM.ISPT.
     *
     * @param m  integer to test.
     * @param mp integer m-1.
     * @param F  a map of pairs (p,e), with primes p, multiplicity e and with
     *           p**e divides mp and e maximal.
     * @return s = -1 (not prime), 0 (unknown) or 1 (prime).
     */
    public static int primalityTestSelfridge(long m, long mp, SortedMap<Long, Integer> F) {
        long AL, BL, QL, QL1, MLPP, PL1, PL;
        int SL;
        //List<Long> SMPRM = smallPrimes(2, 500); //SMPRM;
        List<Long> PP;

        List<Map.Entry<Long, Integer>> FP = new ArrayList<Map.Entry<Long, Integer>>(F.entrySet());
        QL1 = 1L; //SACI.IONE;
        PL1 = 1L;
        int i = 0;
        while (true) {
            do {
                if (i == FP.size()) {
                    logger.info("SL=1: m = " + m);
                    SL = 1;
                    return SL;
                }
                QL = FP.get(i).getKey();
                i++;
            } while (!(QL > QL1));
            QL1 = QL;
            PP = SMPRM;
            int j = 0;
            do {
                if (j == PP.size()) {
                    logger.info("SL=0: m = " + m);
                    SL = 0;
                    return SL;
                }
                PL = PP.get(j);
                j++;
                if (PL > PL1) {
                    PL1 = PL;
                    AL = (new ModLong(new ModLongRing(m), PL)).power(mp).getVal(); //(PL**MLP) mod ML;
                    if (AL != 1) {
                        logger.info("SL=-1: m = " + m);
                        SL = (-1);
                        return SL;
                    }
                }
                MLPP = mp / QL;
                BL = (new ModLong(new ModLongRing(m), PL)).power(MLPP).getVal(); //(PL**MLPP) mod ML;
            } while (BL == 1L);
        }
    }

    /**
     * Integer large prime divisor search. n is a positive integer with no prime
     * divisors less than 17. 1 le a le b le n. A search is made for a divisor p
     * of the integer n, with a le p le b. If such a p is found then np=n/p,
     * otherwise p=1 and np=n. A modular version of Fermats method is used, and
     * the search goes from a to b.
     * See also SACPRIM.ILPDS.
     *
     * @param n integer to factor.
     * @param a lower bound.
     * @param b upper bound.
     * @return p a prime factor of n, with a &le; p &le; b &lt; n.
     */
    public static long largePrimeDivisorSearch(long n, long a, long b) { // return PL, NLP ignored
        if (n > BETA) {
            throw new UnsupportedOperationException(
                    "largePrimeDivisorSearch only for longs less than BETA: " + BETA);
        }
        List<ModLong> L = null;
        List<ModLong> LP;
        long RL1, RL2, J1Y, r, PL, TL;
        long RL, J2Y, XL1, XL2, QL, XL, YL, YLP;
        long ML = 0L;
        long SL = 0L;
        QL = n / b;
        RL = n % b;
        XL1 = b + QL;
        SL = XL1 % 2L;
        XL1 = XL1 / 2L; // after SL
        if ((RL != 0) || (SL != 0)) {
            XL1 = XL1 + 1L;
        }
        QL = n / a;
        XL2 = a + QL;
        XL2 = XL2 / 2L;
        L = residueListFermat(n); //FRESL( NL, ML, L ); // ML not returned
        if (L.isEmpty()) {
            return n;
        }
        ML = L.get(0).ring.getModul().longValue(); // sic
        // check is okay: sort: L = SACSET.LBIBMS( L ); revert: L = MASSTOR.INV( L );
        Collections.sort(L);
        Collections.reverse(L);
        //System.out.println("FRESL: " + L);
        r = XL2 % ML;
        LP = L;
        int i = 0;
        while (i < LP.size() && r < LP.get(i).getVal()) {
            i++;
        }
        if (i == LP.size()) {
            i = 0; //LP = L;
            SL = ML;
        } else {
            SL = 0L;
        }
        RL1 = LP.get(i).getVal();
        i++;
        SL = ((SL + r) - RL1);
        XL = XL2 - SL;
        TL = 0L;
        while (XL >= XL1) {
            J2Y = XL * XL;
            YLP = J2Y - n;
            //System.out.println("YLP = " + YLP + ", J2Y = " + J2Y);
            YL = Roots.sqrtInt(BigInteger.valueOf(YLP)).getVal().longValue(); // SACI.ISQRT( YLP, YL, TL );
            //System.out.println("YL = sqrt(YLP) = " + YL);
            TL = YLP - YL * YL;
            if (TL == 0L) {
                PL = XL - YL;
                return PL;
            }
            if (i < LP.size()) {
                RL2 = LP.get(i).getVal();
                i++;
                SL = (RL1 - RL2);
            } else {
                i = 0;
                RL2 = LP.get(i).getVal();
                i++;
                J1Y = (ML + RL1);
                SL = (J1Y - RL2);
            }
            RL1 = RL2;
            XL = XL - SL;
        }
        PL = 1L;
        // unused NLP = NL;
        return PL;
    }

    /**
     * Fermat residue list, single modulus. m is a positive beta-integer. a
     * belongs to Z(m). L is a list of the distinct b in Z(m) such that b**2-a
     * is a square in Z(m).
     * See also SACPRIM.FRLSM.
     *
     * @param m integer to factor.
     * @param a element of Z mod m.
     * @return Lp a list of Fermat residues for modul m.
     */
    public static List<ModLong> residueListFermatSingle(long m, long a) {
        List<ModLong> Lp;
        SortedSet<ModLong> L;
        List<ModLong> S, SP;
        int MLP;
        ModLong SL, SLP, SLPP;

        ModLongRing ring = new ModLongRing(m);
        ModLong am = ring.fromInteger(a);
        MLP = (int) (m / 2L);
        S = new ArrayList<ModLong>();
        for (int i = 0; i <= MLP; i++) {
            SL = ring.fromInteger(i);
            SL = SL.multiply(SL); //SACM.MDPROD( ML, IL, IL );
            S.add(SL);
        }
        L = new TreeSet<ModLong>();
        SP = S;
        for (int i = MLP; i >= 0; i -= 1) {
            SL = SP.get(i);
            SLP = SL.subtract(am); //SACM.MDDIF( ML, SL, AL );
            int j = S.indexOf(SLP);
            if (j >= 0) { // != 0
                SLP = ring.fromInteger(i);
                L.add(SLP);
                SLPP = SLP.negate();
                if (!SLPP.equals(SLP)) {
                    L.add(SLPP);
                }
            }
        }
        Lp = new ArrayList<ModLong>(L);
        return Lp;
    }

    /**
     * Fermat residue list. n is a positive integer with no prime divisors less
     * than 17. m is a positive beta-integer and L is an ordered list of the
     * elements of Z(m) such that if x**2-n is a square then x is congruent to a
     * (modulo m) for some a in L.
     * See also SACPRIM.FRESL.
     *
     * @param n integer to factor.
     * @return Lp a list of Fermat residues for different modules.
     */
    public static List<ModLong> residueListFermat(long n) {
        List<ModLong> L, L1;
        List<Long> H, M;
        long AL1, AL2, AL3, AL4, BL1, HL, J1Y, J2Y, KL, KL1, ML1, ML;
        //too large: long BETA = Long.MAX_VALUE - 1L;

        // modulus 2**5.
        BL1 = 0L;
        AL1 = n % 32L;
        AL2 = AL1 % 16L;
        AL3 = AL2 % 8L;
        AL4 = AL3 % 4L;
        if (AL4 == 3L) {
            ML = 4L;
            if (AL3 == 3L) {
                BL1 = 2L;
            } else {
                BL1 = 0L;
            }
        } else {
            if (AL3 == 1L) {
                ML = 8L;
                if (AL2 == 1L) {
                    BL1 = 1L;
                } else {
                    BL1 = 3L;
                }
            } else {
                ML = 16L;
                switch ((short) (AL1 / 8L)) {
                    case (short) 0:
                        BL1 = 3L;
                        break;
                    case (short) 1:
                        BL1 = 7L;
                        break;
                    case (short) 2:
                        BL1 = 5L;
                        break;
                    case (short) 3:
                        BL1 = 1L;
                        break;
                    default:
                        throw new RuntimeException("this should not happen");
                }
            }
        }
        L = new ArrayList<ModLong>();
        ModLongRing ring = new ModLongRing(ML);
        ModLongRing ring2;
        if (ML == 4L) {
            L.add(ring.fromInteger(BL1));
        } else {
            J1Y = ML - BL1;
            L.add(ring.fromInteger(BL1));
            L.add(ring.fromInteger(J1Y));
        }
        KL = L.size();

        // modulus 3**3.
        AL1 = n % 27L;
        AL2 = AL1 % 3L;
        if (AL2 == 2L) {
            ML1 = 3L;
            ring2 = new ModLongRing(ML1);
            KL1 = 1L;
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(0));
        } else {
            ML1 = 27L;
            ring2 = new ModLongRing(ML1);
            KL1 = 4L;
            L1 = residueListFermatSingle(ML1, AL1);
            // ring2 == L1.get(0).ring
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML); // == L.get(0).ring
        KL = (KL * KL1);
        //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());

        // modulus 5**2.
        AL1 = n % 25L;
        AL2 = AL1 % 5L;
        if ((AL2 == 2L) || (AL2 == 3L)) {
            ML1 = 5L;
            ring2 = new ModLongRing(ML1);
            J1Y = (AL2 - 1L);
            J2Y = (6L - AL2);
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(J1Y));
            L1.add(ring2.fromInteger(J2Y));
            KL1 = 2L;
        } else {
            ML1 = 25L;
            ring2 = new ModLongRing(ML1);
            L1 = residueListFermatSingle(ML1, AL1);
            KL1 = 7L;
        }
        if (ML1 >= BETA / ML) {
            return L;
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML);
        KL = (KL * KL1);
        //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());

        // moduli 7,11,13.
        L1 = new ArrayList<ModLong>();
        M = new ArrayList<Long>(3);
        H = new ArrayList<Long>(3);
        //M = MASSTOR.COMPi( 7, MASSTOR.COMPi( 11, 13 ) );
        M.add(7L);
        M.add(11L);
        M.add(13L);
        //H = MASSTOR.COMPi( 64, MASSTOR.COMPi( 48, 0 ) );
        H.add(64L);
        H.add(48L);
        H.add(0L);
        int i = 0;
        while (true) {
            ML1 = M.get(i);
            if (ML1 >= BETA / ML) {
                return L;
            }
            ring2 = new ModLongRing(ML1);
            AL1 = n % ML1;
            L1 = residueListFermatSingle(ML1, AL1);
            KL1 = L1.size();
            //L = SACM.MDLCRA( ML, ML1, L, L1 );
            L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
            ML = (ML * ML1);
            ring = new ModLongRing(ML);
            KL = (KL * KL1);
            //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());
            HL = H.get(i);
            i++;
            if (KL > HL) {
                return L;
            }
        }
        // return ?
    }

    /**
     * Compute units of Z sub 210.
     * See also SACPRIM.UZ210.
     *
     * @return list of units of Z sub 210.
     */
    public static List<Long> getUZ210() {
        List<Long> UZ = new ArrayList<Long>();
        java.math.BigInteger z210 = java.math.BigInteger.valueOf(210);
        //for (int i = 209; i >= 1; i -= 2) {
        for (long i = 1; i <= 209; i += 2) {
            if (z210.gcd(java.math.BigInteger.valueOf(i)).equals(java.math.BigInteger.ONE)) {
                UZ.add(i);
            }
        }
        return UZ;
    }

    /**
     * Integer factorization. n is a positive integer. F is a list (q(1),
     * q(2),...,q(h)) of the prime factors of n, q(1) le q(2) le ... le q(h),
     * with n equal to the product of the q(i). <br /> In JAS F is a map.
     * See also SACPRIM.IFACT, uses Pollards rho method.
     *
     * @param n integer to factor.
     * @return a map of pairs of numbers (p,e) with p**e divides n.
     */
    public static SortedMap<java.math.BigInteger, Integer> factors(java.math.BigInteger n) {
        java.math.BigInteger b = java.math.BigInteger.valueOf(BETA);
        SortedMap<java.math.BigInteger, Integer> F = new TreeMap<java.math.BigInteger, Integer>();
        if (n.compareTo(b) > 0) {
            n = smallPrimeDivisors(n, F);
            if (n.compareTo(b) > 0) {
                logger.info("run factorsPollardRho on n = " + n);
                factorsPollardRho(n, F);
                return F;
            }
        }
        long s = n.longValue();
        SortedMap<Long, Integer> ff = factors(s); // useless 2nd smallPrimeDiv search
        for (Map.Entry<Long, Integer> m : ff.entrySet()) {
            java.math.BigInteger mm = java.math.BigInteger.valueOf(m.getKey());
            F.put(mm, m.getValue());
        }
        return F;
    }

    /**
     * Integer factorization using Pollards rho algorithm. n is a positive
     * integer. F is a list (q(1), q(2),...,q(h)) of the prime factors of n,
     * q(1) le q(2) le ... le q(h), with n equal to the product of the q(i).
     * <br /> In JAS F is a map.
     *
     * @param n integer to factor.
     * @param F a map of pairs of numbers (p,e) with p**e divides n and p is
     *          probable prime, F is modified.
     */
    public static void factorsPollardRho(java.math.BigInteger n, SortedMap<java.math.BigInteger, Integer> F) {
        java.math.BigInteger factor;
        java.math.BigInteger temp = n;
        int iterationCounter = 0;
        Integer count;
        while (!temp.isProbablePrime(32)) {
            factor = rho(temp);
            if (factor.equals(temp)) {
                if (iterationCounter++ > 4) {
                    break;
                }
            } else {
                iterationCounter = 1;
            }
            count = F.get(factor);
            if (count == null) {
                F.put(factor, 1);
            } else {
                F.put(factor, count + 1);
            }
            temp = temp.divide(factor);
        }
        count = F.get(temp);
        if (count == null) {
            F.put(temp, 1);
        } else {
            F.put(temp, count + 1);
        }
    }

    /**
     * Search cycle with Pollards rho algorithm x**2 + c mod n. n is a positive
     * integer. <br />
     *
     * @param n integer test.
     * @return x-y with gcd(x-y, n) = 1.
     */
    static java.math.BigInteger rho(java.math.BigInteger n) {
        java.math.BigInteger divisor;
        java.math.BigInteger c = new java.math.BigInteger(n.bitLength(), random);
        java.math.BigInteger x = new java.math.BigInteger(n.bitLength(), random);
        java.math.BigInteger xx = x;
        do {
            x = x.multiply(x).mod(n).add(c).mod(n);
            xx = xx.multiply(xx).mod(n).add(c).mod(n);
            xx = xx.multiply(xx).mod(n).add(c).mod(n);
            divisor = x.subtract(xx).gcd(n);
        } while (divisor.equals(java.math.BigInteger.ONE));
        return divisor;
    }


    /**
     * Integer factorization using Pollards rho algorithm. n is a positive
     * integer. F is a list (q(1), q(2),...,q(h)) of the prime factors of n,
     * q(1) le q(2) le ... le q(h), with n equal to the product of the q(i).
     * <br /> In JAS F is a map.
     *
     * @param n integer to factor.
     * @param F a map of pairs of numbers (p,e) with p**e divides n and p is
     *          probable prime, F is modified.
     */
    public static void factorsPollardRho(long n, SortedMap<Long, Integer> F) {
        long factor;
        long temp = n;
        int iterationCounter = 0;
        Integer count;
        while (!java.math.BigInteger.valueOf(temp).isProbablePrime(32)) {
            factor = rho(temp);
            if (factor == temp) {
                if (iterationCounter++ > 4) {
                    break;
                }
            } else {
                iterationCounter = 1;
            }
            count = F.get(factor);
            if (count == null) {
                F.put(factor, 1);
            } else {
                F.put(factor, count + 1);
            }
            temp = temp / factor;
        }
        count = F.get(temp);
        if (count == null) {
            F.put(temp, 1);
        } else {
            F.put(temp, count + 1);
        }
        //System.out.println("random = " + random.getAlgorithm());
    }


    /**
     * Search cycle with Pollards rho algorithm x**2 + c mod n. n is a positive
     * integer. c is a random constant.
     *
     * @param n integer test.
     * @return x-y with gcd(x-y, n) == 1.
     */
    static long rho(long n) {
        long divisor;
        int bl = java.math.BigInteger.valueOf(n).bitLength();
        long c = new java.math.BigInteger(bl, random).longValue(); // .abs()
        long x = new java.math.BigInteger(bl, random).longValue(); // .abs()
        ModLongRing ring = new ModLongRing(n);
        ModLong cm = new ModLong(ring, c);
        ModLong xm = new ModLong(ring, x);
        ModLong xxm = xm;
        do {
            xm = xm.multiply(xm).sum(cm);
            xxm = xxm.multiply(xxm).sum(cm);
            xxm = xxm.multiply(xxm).sum(cm);
            divisor = gcd(xm.getVal() - xxm.getVal(), n);
        } while (divisor == 1L);
        return divisor;
    }


    static long gcd(long a, long b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).getVal().longValue();
    }

}
