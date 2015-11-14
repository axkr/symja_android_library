/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.io.Serializable;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Greatest common divisor algorithm interface.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @usage To create classes that implement this interface use the
 *        GreatestCommonDivisorFactory. It will select an appropriate
 *        implementation based on the types of polynomial coefficients CT.
 * 
 * <pre>
 * GreatestCommonDivisor&lt;CT&gt; engine = GCDFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.gcd(a, b);
 * </pre>
 * 
 * For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 * <pre>
 * BigInteger cofac = new BigInteger();
 * GreatestCommonDivisor&lt;BigInteger&gt; engine = GCDFactory.getImplementation(cofac);
 * c = engine.gcd(a, b);
 * </pre>
 * 
 * @see edu.jas.ufd.GCDFactory#getImplementation
 */

public interface GreatestCommonDivisor<C extends GcdRingElem<C>> extends Serializable {


    /**
     * GenPolynomial content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> content(GenPolynomial<C> P);


    /**
     * GenPolynomial primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P);


    /**
     * GenPolynomial greatest comon divisor.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> gcd(GenPolynomial<C> P, GenPolynomial<C> S);


    /**
     * GenPolynomial least comon multiple.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return lcm(P,S).
     */
    public GenPolynomial<C> lcm(GenPolynomial<C> P, GenPolynomial<C> S);


    /**
     * GenPolynomial resultant.
     * The input polynomials are considered as univariate polynomials in the main variable. 
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     * @throws UnsupportedOperationException if there is no implementation in the sub-class.
     */
    public GenPolynomial<C> resultant(GenPolynomial<C> P, GenPolynomial<C> S);


    /**
     * GenPolynomial co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a. B does not contain zero or
     *         constant polynomials.
     */
    public List<GenPolynomial<C>> coPrime(List<GenPolynomial<C>> A);


    /**
     * GenPolynomial test for co-prime list.
     * @param A list of GenPolynomials.
     * @return true if gcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isCoPrime(List<GenPolynomial<C>> A);

}
