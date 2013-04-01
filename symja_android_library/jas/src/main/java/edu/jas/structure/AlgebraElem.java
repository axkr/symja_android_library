/*
 * $Id: AlgebraElem.java 4056 2012-07-26 17:44:13Z kredel $
 */

package edu.jas.structure;


/**
 * Algabra element interface.
 * @param <A> algebra type
 * @param <C> scalar type
 * @author Heinz Kredel
 */
public interface AlgebraElem<A extends AlgebraElem<A, C>, C extends RingElem<C>> extends RingElem<A> {


    /**
     * Scalar multiplication. Multiply this by a scalar.
     * @param s scalar
     * @return this * s.
     */
    public A scalarMultiply(C s);


    /**
     * Linear combination.
     * @param a scalar
     * @param b algebra element
     * @param s scalar
     * @return a * b + this * s.
     */
    public A linearCombination(C a, A b, C s);


    /**
     * Linear combination.
     * @param b algebra element
     * @param s scalar
     * @return b + this * s.
     */
    public A linearCombination(A b, C s);

}
