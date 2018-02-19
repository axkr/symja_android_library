/*
 * $Id$
 */

package edu.jas.arith;


/**
 * Combinatoric algorithms. Similar to ALDES/SAC2 SACCOMB module.
 *
 * @author Heinz Kredel
 */
public class Combinatoric {


    /**
     * Integer binomial coefficient induction. n and k are integers with 0
     * less than or equal to k less than or equal to n. A is the binomial
     * coefficient n over k. B is the binomial coefficient n over k+1.
     *
     * @param A previous induction result.
     * @param n long.
     * @param k long.
     * @return the binomial coefficient n over k+1.
     */
    public static BigInteger binCoeffInduction(BigInteger A, long n, long k) {
        BigInteger kp, np;
        np = new BigInteger(n - k);
        kp = new BigInteger(k + 1);
        BigInteger B = A.multiply(np).divide(kp);
        return B;
    }


    /**
     * Integer binomial coefficient. n and k are integers with 0 less than
     * or equal to k less than or equal to n. A is the binomial coefficient n
     * over k.
     *
     * @param n long.
     * @param k long.
     * @return the binomial coefficient n over k+1.
     */
    public static BigInteger binCoeff(int n, int k) {
        BigInteger A = BigInteger.ONE;
        int kp = (k < n - k ? k : n - k);
        for (int j = 0; j < kp; j++) {
            A = binCoeffInduction(A, n, j);
        }
        return A;
    }


    /**
     * Integer binomial coefficient partial sum. n and k are integers, 0 le k le
     * n. A is the sum on i, from 0 to k, of the binomial coefficient n over i.
     *
     * @param n long.
     * @param k long.
     * @return the binomial coefficient partial sum n over i.
     */
    public static BigInteger binCoeffSum(int n, int k) {
        BigInteger B, S;
        S = BigInteger.ONE;
        B = BigInteger.ONE;
        for (int j = 0; j < k; j++) {
            B = binCoeffInduction(B, n, j);
            S = S.sum(B);
        }
        return S;
    }


    /**
     * Factorial.
     *
     * @param n integer.
     * @return n!, with 0! = 1.
     */
    public static BigInteger factorial(long n) {
        if (n <= 1) {
            return BigInteger.ONE;
        }
        BigInteger f = BigInteger.ONE;
        if (n >= Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(n + " >= Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        }
        for (int i = 2; i <= n; i++) {
            f = f.multiply(new BigInteger(i));
        }
        return f;
    }

}
