/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.transcendental;

import java.math.BigInteger;

import java.math.BigDecimal;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.powers.Pow;
import de.tilman_neumann.jml.powers.Pow2;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.precision.Scale;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Implementation of the exponential function for big decimals.
 * @author Tilman Neumann
 */
public class Exp {
	private static final Logger LOG = LogManager.getLogger(Exp.class);

	private static final boolean DEBUG = false;
	
	/**
	 * Simple series expansion for exp(x).
	 * The convergence criterion works only for |x|<=1, so it should not be called directly, only via one of the reduction formulas.
	 */
	static BigDecimal expSeriesExpansion(BigDecimal x, Scale outScale) {
	    // get demanded precision and compute the series limit for it:
		// for large arguments, the series doesn't converge so quickly,
		// we have to add extra precision...
		int xMag = Magnitude.of(x);
		if (xMag < 0) xMag = 0; // do not reduce precision if magnitude is < 0
		Scale errorScale = outScale.add(xMag);
        BigDecimal maxErr = errorScale.getErrorBound();
        if (DEBUG) LOG.debug("outScale=" + outScale + ", errorScale=" + errorScale + ", maxErr=" + maxErr);
	    
	    // calculate series expansion:
        BigDecimal r = F_1;
	    BigDecimal xpow = F_1;
	    BigInteger iFac = I_1;
	    int i=1;
	    BigDecimal elem;
	    Scale internalScale = errorScale.add(1);
	    do {
	        xpow = internalScale.applyTo(xpow.multiply(x)); // x^i
	        iFac = iFac.multiply(BigInteger.valueOf(i)); // i!
	        if (DEBUG) LOG.debug("i=" + i + ", iFac=" + iFac);
	        elem = BigDecimalMath.divide(xpow, iFac, internalScale);
	        r = r.add(elem);
	        if (DEBUG) LOG.debug("exp(" + x + ")[" + i + "] = " + r);
	        i++;
	    // stop if the error is smaller than allowed:
	    } while (elem.abs().compareTo(maxErr) > 0);
	    
	    if (DEBUG) LOG.debug("exp(" + x + ") = " + r);
	    return outScale.applyTo(r);
	}

	/**
	 * Compute exp(x) with simple argument reduction:
	 * Since the power series for exp(x) converges best around x=0, we use
	 * the transformation exp(x)=exp(y)*2^(-d), with y=x*2^d. d is choosen such
	 * that -ln(2)/2<y<ln(2)/2.
	 */
	static BigDecimal expSimpleReduction(BigDecimal x, Scale outScale) {
	    // exp(x) converges best around x=0. The multiplication of a number with a
	    // natural power of 2 has constant complexity, i.e. costs almost nothing.
	    // Thus, it is a cheap improvement of the convergence of the exponential
	    // to look for a whole number d, such that y=x+d*ln(2) is near to 0.
	    // Then, exp(x) = exp(y) * 2^(-d) with the same precision.
	    // The number d we desire results just from rounding -x/ln(2):
	    int d = (int) Math.ceil(-x.doubleValue()/Math.log(2.0)-0.5);
	    // Compute ln(2) with required accuracy:
	    Scale ln2Scale = outScale.add(Math.max(0, Magnitude.binaryToDecimal(-d)) + Magnitude.of(d));
	    if (DEBUG) LOG.debug("d=" + d + ", ln2Scale=" + ln2Scale);
	    BigDecimal ln2 = Ln.ln2(ln2Scale);
	    if (DEBUG) LOG.debug("ln2=" + ln2);
	    // Compute y=x+d*ln(2): The multiplication raises the error of the series with |d|,
	    // thus the argument needs precision outScale + (-d)*ln(10)/ln(2) + log10(|d|)
	    BigDecimal dTerm = BigDecimalMath.multiply(ln2, d);
	    BigDecimal y = dTerm.add(x);
	    // Compute exponential series for y:
	    Scale expYScale = outScale.add(Math.max(0, Magnitude.binaryToDecimal(-d)) + 1);
	    BigDecimal r = expSeriesExpansion(y, expYScale);
	    if (DEBUG) LOG.debug("y=" + y + ", expYScale=" + expYScale + ", exp(" + y + ")=" + r);
	    
	    // Multiply with 2^(-d) and assign result: This multiplies the error with 2^-d as well,
	    // thus the argument needs precision outScale + (-d)*ln(10)/ln(2)
	    BigDecimal result = Pow2.mulPow2(r, -d);
	    return outScale.applyTo(result);
	}
	
	public static BigDecimal exp(BigDecimal x, Precision outPrec) {
		// result magnitude is roughly log10(e^x) = ln(exp(x))/ln(10) = x/ln(10)
		int outMag = (int) (x.doubleValue()/Math.log(10) + 0.5);
		// scale is result precision - result magnitude
		Scale outScale = Scale.valueOf(outPrec.digits() - outMag);
		return exp(x, outScale);
	}

	/**
	 * Compute exp(w) using a more powerful argument reduction.
	 * This function uses the transform exp(w)=exp(x)^(2^K) with x=w*2^(-K).
	 * This way, we can bring the argument to develop in a power series
	 * arbitrarily near to 0 with very high convergence speed.
	 * 
	 * @param w argument
	 * @param outScale wanted precision in after-floating point decimal digits
	 * @return exp(w)
	 */
	public static BigDecimal exp/*PowerReduction*/(BigDecimal w, Scale outScale) {
	    // Compute K and x=w*2^(-K): Derived from complexity considerations exposed in mpfr sources, experimentally refined.
		int wMag = Magnitude.of(w);
		int wMagBits = Magnitude.decimalToBinary(wMag);
	    int K = Math.max(0, wMagBits) + (int) Math.sqrt(Magnitude.decimalToBinary(outScale.digits()));
	    if (DEBUG) LOG.debug("wMag=" + wMag + ", outScale=" + outScale + ", K = " + K);
	    
	    BigDecimal x = Pow2.divPow2(w, K);
	    // this reduces the absolute error by factor 2^K, thus if w has scale outScale,
	    // x has scale outScale + binaryToDecimal(K)
	    if (DEBUG) LOG.debug("arg w=" + w + ", K=" + K + ", x=" + x);

	    // Execute series expansion:
		// Result magnitude is roughly log10(e^x) = ln(exp(x))/ln(10) = x/ln(10)
		int outMag = (int) (Math.abs(w.doubleValue())/Math.log(10) + 0.5);
	    // For output precision = outMag + outScale, expX needs to be computed with binaryToDecimal(K) extra digits,
		// because x like 0.000...000123 start with binaryToDecimal(K) 0-digits and because the final pow() will reduce the scale by K bits.
	    Scale expXScale = outScale.add(outMag + Magnitude.binaryToDecimal(K) + 2);
	    if (DEBUG) LOG.debug("expXScale = " + expXScale);
	    BigDecimal expX = expSeriesExpansion(x, expXScale);
	    if (DEBUG) LOG.debug("expX = " + expX);

	    // Compute the final result y:
	    BigInteger ex = I_1.shiftLeft(K); // may be large, like 2^1000
	    if (DEBUG) LOG.debug("ex = " + ex);
	    Precision yPrecision = Precision.valueOf(outMag + outScale.digits() + Magnitude.binaryToDecimal(K));
	    if (DEBUG) LOG.debug("yPrecision = " + yPrecision);
	    BigDecimal y = Pow.pow(expX, ex, yPrecision); // y = expX^(2^K)
	    
	    // assign result:
	    return outScale.applyTo(y);
	}

	/**
	 * Computation of exp(w) using Brent's formula: This is a combination of
	 * the simple reduction formula followed by the power reduction formula.
	 *
	 * Note that the power reduction formula alone is faster than Brent's formula,
	 * because our pow() method takes exponents of arbitrary size.
	 * 
	 * Brent's formula could only better when the pow() method has an exponent limit,
	 * because then the first reduction step allows to use bigger power reductions.
	 */
	static BigDecimal expBrent(BigDecimal x, Scale outScale) {
	    // exp(x) converges best around x=0. The multiplication of a number with a
	    // natural power of 2 has constant complexity, i.e. costs almost nothing.
	    // Thus, it is a cheap improvement of the convergence of the exponential
	    // to look for a whole number d, such that y=x+d*ln(2) is near to 0.
	    // Then, exp(x) = exp(y) * 2^(-d), where exp(y) needs precision increased by d bits.
	    // The number d we desire results just from rounding -x/ln(2);
		// here the precision of ln(2) must be the magnitude of x + some extra digits.
		Scale ln2Scale1 = Scale.valueOf(Math.max(0, Magnitude.of(x)) + 2);
		BigInteger optimalD = BigDecimalMath.divide(x.negate(), Ln.ln2(ln2Scale1), Scale.valueOf(2)).add(F_0_5).toBigInteger();
	    int d = (optimalD.compareTo(BigInteger.valueOf(Integer.MIN_VALUE))<0) ? Integer.MIN_VALUE : optimalD.intValue(); // d is negative !
	    if (DEBUG) LOG.debug("outScale=" + outScale + ", ln2Scale1=" + ln2Scale1 + ", d=" + d);
	    
	    // Compute ln(2) with required accuracy:
	    Scale ln2Scale2 = outScale.add(Magnitude.binaryToDecimal(-d) + Magnitude.of(d));
	    if (DEBUG) LOG.debug("ln2Scale2=" + ln2Scale2);
	    BigDecimal ln2 = Ln.ln2(ln2Scale2);
	    if (DEBUG) LOG.debug("ln2=" + ln2);

	    // Compute y=x+d*ln(2) ~ 0:
	    BigDecimal y = ln2.multiply(BigDecimal.valueOf(d)).add(x);
	    if (DEBUG) LOG.debug("y=" + y);
	    
	    // Precision required for exponential series of y:
	    Scale expYScale = outScale.add(Math.max(0, Magnitude.binaryToDecimal(-d)));
	    if (DEBUG) LOG.debug("expYScale = " + expYScale);
	    // Compute exp(y), with y chosen near 0 to perform well with the series expansion.
	    // Power reduction is twice as fast as simple reduction here!
	    BigDecimal expY = exp/*PowerReduction*/(y, expYScale);
	    
	    // Last not least, multiply with 2^(-d) and assign result:
	    // Since d is negative, this augments the error by d bits. (thats why we need expYScale as above)
	    BigDecimal r = Pow2.mulPow2(expY, -d);
	    return outScale.applyTo(r);
	}
}
