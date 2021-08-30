/*
 * $Id$
 */

package edu.jas.ufd;


/**
 * Non existing Hensel lifting. Exception to be thrown when a valid
 * Hensel lifting cannot be constructed.
 * @author Heinz Kredel
 */

public class NoLiftingException extends Exception {


    public NoLiftingException() {
        super("NoLiftingException");
    }


    public NoLiftingException(String c) {
        super(c);
    }


    public NoLiftingException(String c, Throwable t) {
        super(c, t);
    }


    public NoLiftingException(Throwable t) {
        super("NoLiftingException", t);
    }

}
