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


    /**
     * Quotient and remainder by division of this by S.
     * @param S
     * @return [this/S, this - (this/S)*S].
     */
    @SuppressWarnings("unchecked")
    default public C[] quotientRemainder(C S) {
        return (C[]) new MonoidElem[] { divide(S), remainder(S) }; 
    }


    /**
     * Right division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return right, with a * right = this
     */
    default public C rightDivide(C a) {
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           return divide(a);
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Left division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return left, with left * a = this
     */
    default public C leftDivide(C a) {
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           return divide(a);
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Right remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - a * (1/right), where a * right = this.
     */
    default public C rightRemainder(C a) {
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           return remainder(a);
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Left remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - (1/left) * a, where left * a = this.
     */
    default public C leftRemainder(C a) {
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           return remainder(a);
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Two-sided division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return [left,right], with left * a * right = this
     */
    @SuppressWarnings("unchecked")
    default public C[] twosidedDivide(C a) {
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           C[] ret = (C[]) new MonoidElem[2];
           ret[0] = divide(a);
           ret[1] = ((MonoidFactory<C>)factory()).getONE();
           return ret;
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Two-sided remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - (a/left) * a * (a/right), where left * a * right = this.
     */
    default public C twosidedRemainder(C a){
        if (((MonoidFactory<C>)factory()).isCommutative()) {
           return remainder(a);
        }
        throw new UnsupportedOperationException("operation not implemented");
    }


    /**
     * Inverse of this. Some implementing classes will throw
     * NotInvertibleException if the element is not invertible.
     * @return x with this * x = 1, if it exists.
     */
    public C inverse(); /*throws NotInvertibleException*/


    /**
     * Power of this to the n-th.
     * @param n integer exponent.
     * @return a**n, with a**0 = 1 and a**{-n} = {1/a}**n.
     * Java 8 only
     */ 
    @SuppressWarnings("unchecked")
    default public C power(long n) {
        //System.out.println("this = " + this + ", n = " + n);
        return Power.<C>power((MonoidFactory<C>)factory(), (C)this, n);
    }

}
