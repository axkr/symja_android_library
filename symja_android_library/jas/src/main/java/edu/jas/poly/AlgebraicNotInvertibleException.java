/*
 * $Id$
 */

package edu.jas.poly;


import edu.jas.structure.NotInvertibleException;


/**
 * Algebraic number NotInvertibleException class.
 * Runtime Exception to be thrown for not invertible algebraic numbers.
 * Container for the non-trivial factors found by the inversion algorithm.
 * <b>Note: </b> cannot be generic because of Throwable.
 * @author Heinz Kredel
 */
public class AlgebraicNotInvertibleException extends NotInvertibleException {


    public final GenPolynomial f; // = f1 * f2

    public final GenPolynomial f1;

    public final GenPolynomial f2;


    public AlgebraicNotInvertibleException() {
        this(null,null,null);
    }


    public AlgebraicNotInvertibleException(String c) {
        this(c,null,null,null);
    }


    public AlgebraicNotInvertibleException(String c, Throwable t) {
        this(c,t,null,null,null);
    }


    public AlgebraicNotInvertibleException(Throwable t) {
        this(t,null,null,null);
    }


    /**
     * Constructor.
     * @param f polynomial with f = f1 * f2.
     * @param f1 polynomial.
     * @param f2 polynomial.
     */
    public AlgebraicNotInvertibleException(GenPolynomial f, GenPolynomial f1, GenPolynomial f2) {
        super("AlgebraicNotInvertibleException");
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f polynomial with f = f1 * f2.
     * @param f1 polynomial.
     * @param f2 polynomial.
     */
    public AlgebraicNotInvertibleException(String c, GenPolynomial f, GenPolynomial f1, GenPolynomial f2) {
        super(c);
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f polynomial with f = f1 * f2.
     * @param f1 polynomial.
     * @param f2 polynomial.
     */
    public AlgebraicNotInvertibleException(String c, Throwable t, GenPolynomial f, GenPolynomial f1, GenPolynomial f2) {
        super(c,t);
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f polynomial with f = f1 * f2.
     * @param f1 polynomial.
     * @param f2 polynomial.
     */
    public AlgebraicNotInvertibleException(Throwable t, GenPolynomial f, GenPolynomial f1, GenPolynomial f2) {
        super("AlgebraicNotInvertibleException",t);
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = super.toString();
        if ( f != null || f1 != null || f2 != null) {
            s += ", f = " + f + ", f1 = " + f1 + ", f2 = " + f2;
        }
        return s;
    }

}
