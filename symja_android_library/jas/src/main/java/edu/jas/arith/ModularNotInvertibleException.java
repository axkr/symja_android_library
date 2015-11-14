/*
 * $Id$
 */

package edu.jas.arith;


import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Modular integer NotInvertibleException class. Runtime Exception to be thrown
 * for not invertible modular integers. Container for the non-trivial factors
 * found by the inversion algorithm. <b>Note: </b> cannot be generic because of
 * Throwable.
 * @author Heinz Kredel
 */
public class ModularNotInvertibleException extends NotInvertibleException {


    public final GcdRingElem f; // = f1 * f2


    public final GcdRingElem f1;


    public final GcdRingElem f2;


    public ModularNotInvertibleException() {
        this(null, null, null);
    }


    public ModularNotInvertibleException(String c) {
        this(c, null, null, null);
    }


    public ModularNotInvertibleException(String c, Throwable t) {
        this(c, t, null, null, null);
    }


    public ModularNotInvertibleException(Throwable t) {
        this(t, null, null, null);
    }


    /**
     * Constructor.
     * @param f gcd ring element with f = f1 * f2.
     * @param f1 gcd ring element.
     * @param f2 gcd ring element.
     */
    public ModularNotInvertibleException(GcdRingElem f, GcdRingElem f1, GcdRingElem f2) {
        super("ModularNotInvertibleException");
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f gcd ring element with f = f1 * f2.
     * @param f1 gcd ring element.
     * @param f2 gcd ring element.
     */
    public ModularNotInvertibleException(String c, GcdRingElem f, GcdRingElem f1, GcdRingElem f2) {
        super(c);
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f gcd ring element with f = f1 * f2.
     * @param f1 gcd ring element.
     * @param f2 gcd ring element.
     */
    public ModularNotInvertibleException(String c, Throwable t, GcdRingElem f, GcdRingElem f1, GcdRingElem f2) {
        super(c, t);
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
    }


    /**
     * Constructor.
     * @param f gcd ring element with f = f1 * f2.
     * @param f1 gcd ring element.
     * @param f2 gcd ring element.
     */
    public ModularNotInvertibleException(Throwable t, GcdRingElem f, GcdRingElem f1, GcdRingElem f2) {
        super("ModularNotInvertibleException", t);
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
        if (f != null || f1 != null || f2 != null) {
            s += ", f = " + f + ", f1 = " + f1 + ", f2 = " + f2;
        }
        return s;
    }

}
