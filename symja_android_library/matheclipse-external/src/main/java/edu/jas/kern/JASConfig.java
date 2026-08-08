package edu.jas.kern;


import edu.jas.ufd.FactorAbstract;


/**
 * Configuration options to truncate long running Kronecker factorization.
 * @author Axel Kramer
 */

public class JASConfig {


    /**
     * {@linkFactorAbstract#factorsSquarefreeKronecker(edu.jas.poly.GenPolynomial)}
     * will throw an {@link ArithmeticException}, if this parameter is
     * greater than <code>0</code> and the Kronecker substitution
     * degree is greater than this value.
     */
    public static int MAX_DEGREE_KRONECKER_FACTORIZATION = -1;


    /**
     * {@link FactorAbstract#factorsSquarefreeKronecker(edu.jas.poly.GenPolynomial)}
     * will throw an {@link ArithmeticException}, if this parameter is greater
     * than <code>0</code> and the Kronecker iteration counter is greater than
     * this value.
     */
    public static int MAX_ITERATIONS_KRONECKER_FACTORIZATION = -1;


    /**
     * If <code>true</code>, {@link edu.jas.ufd.GCDFactory} selects
     * {@link edu.jas.ufd.GreatestCommonDivisorZippel}, the sparse interpolation, for the modular gcd
     * and routes the integer and rational cases through it.
     *
     * <p>
     * Off by default: the sparse algorithm pays off for polynomials in many variables whose gcd has
     * few terms, and costs its extra bookkeeping on the dense low variable count problems which are
     * the common case. The engine falls back to a dense algorithm whenever its skeleton assumption
     * does not hold, so switching this on can only change the running time, never the result.
     */
    public static boolean USE_SPARSE_GCD = false;

}
