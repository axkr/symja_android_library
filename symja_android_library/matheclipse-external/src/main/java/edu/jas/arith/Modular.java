/*
 * $Id$
 */

package edu.jas.arith;


/**
 * Interface with getInteger and getSymmetricInteger methods.
 * @author Heinz Kredel
 */

public interface Modular {


    /**
     * Return a BigInteger from this Element.
     * @return a BigInteger of this.
     */
    public BigInteger getInteger();


    /**
     * Return a symmetric BigInteger from this Element.
     * @return a symmetric BigInteger of this.
     */
    public BigInteger getSymmetricInteger();

}
