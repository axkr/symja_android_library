/*
 * $Id$
 */

package edu.jas.poly;



/**
 * Invalid expression exception class.
 * Runtime Exception to be thrown for invalid algebraic / polynomial expressions.
 * @author Heinz Kredel
 */
public class InvalidExpressionException extends RuntimeException {


    public InvalidExpressionException() {
        super("InvalidExpressionException");
    }


    public InvalidExpressionException(String c) {
        super(c);
    }


    public InvalidExpressionException(String c, Throwable t) {
        super(c, t);
    }


    public InvalidExpressionException(Throwable t) {
        super("InvalidExpressionException", t);
    }

}
