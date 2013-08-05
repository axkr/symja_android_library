/*
 * $Id: TimeExceededException.java 4055 2012-07-26 17:37:29Z kredel $
 */

package edu.jas.kern;


/**
 * Time exceeded exception class. Runtime Exception to be thrown when the
 * run-time has exceeded a certain limit.
 * @author Heinz Kredel
 */

public class TimeExceededException extends RuntimeException {


    public TimeExceededException() {
        super("TimeExceededException");
    }


    public TimeExceededException(String c) {
        super(c);
    }


    public TimeExceededException(String c, Throwable t) {
        super(c, t);
    }


    public TimeExceededException(Throwable t) {
        super("TimeExceededException", t);
    }

}
