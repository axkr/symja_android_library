/*
 * $Id$
 */

package edu.jas.structure;


/**
 * NotDivisibleException class. Runtime Exception to be thrown for not
 * divisible monoid elements.
 *
 * @author Heinz Kredel
 */

public class NotDivisibleException extends RuntimeException {


    public NotDivisibleException() {
        super("NotDivisibleException");
    }


    public NotDivisibleException(String c) {
        super(c);
    }


    public NotDivisibleException(String c, Throwable t) {
        super(c, t);
    }


    public NotDivisibleException(Throwable t) {
        super("NotDivisibleException", t);
    }

}
