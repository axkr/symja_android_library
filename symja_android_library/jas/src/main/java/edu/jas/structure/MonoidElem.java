/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Monoid element interface. Defines the multiplicative methods.
 * @param <C> element type
 * @author Heinz Kredel
 */

public interface MonoidElem<C extends MonoidElem<C>> extends Element<C> {


    /**
     * Test if this is one.
     * @return true if this is 1, else false.
     */
    public boolean isONE();


    /**
     * Test if this is a unit. I.e. there exists x with this.multiply(x).isONE()
     * == true.
     * @return true if this is a unit, else false.
     */
    public boolean isUnit();


    /**
     * Multiply this with S.
     * @param S
     * @return this * S.
     */
    public C multiply(C S);


    /**
     * Divide this by S.
     * @param S
     * @return this / S.
     */
    public C divide(C S);


    /**
     * Remainder after division of this by S.
     * @param S
     * @return this - (this / S) * S.
     */
    public C remainder(C S);


    /* for a later release:
     * Quotient and remainder by division of this by S.
     * @param S
     * @return [this/S, this - (this/S)*S].
     */
    //public C[] quotientRemainder(C S);


    /**
     * Inverse of this. Some implementing classes will throw
     * NotInvertibleException if the element is not invertible.
     * @return x with this * x = 1, if it exists.
     */
    public C inverse(); /*throws NotInvertibleException*/


    /*
     * Power of this to the n-th.
     * @param n integer exponent.
     * @return a**n, with 0**0 = 0, a**0 = 1 and a**{-n} = {1/a}**n.
     * Java 8 only
     */ 
    //default public C power(long n) {
    //  //System.out.println("this = " + this + ", n = " + n);
    //  return Power.<C>power((MonoidFactory<C>)factory(),(C)this,n);
    //}

}
