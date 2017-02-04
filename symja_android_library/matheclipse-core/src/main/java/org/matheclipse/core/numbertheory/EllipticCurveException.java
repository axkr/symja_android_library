package org.matheclipse.core.numbertheory;


import java.math.BigInteger;

public class EllipticCurveException extends ArithmeticException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1895217223797225916L;
	
	public BigInteger exceptionPoint;

    public EllipticCurveException(String message, BigInteger p) {
        super(message);
        exceptionPoint = p;
    }
}
