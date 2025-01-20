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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.powers.Pow2;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.roots.RootsReal;

import static de.tilman_neumann.jml.base.BigDecimalConstants.*;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Implementation of the natural logarithm function for BigDecimals.
 * @author Tilman Neumann
 */
public class Ln {
	private static final Logger LOG = LogManager.getLogger(Ln.class);
    
	private static final boolean DEBUG = false;
	
	/** ln(2) computed to LN2_DEC_PREC decimal digits precision. Initialized with scale 10. */
    private static BigDecimal LN2 = Ln.lnSeriesExpansion(F_2, Scale.valueOf(10));
    private static Scale LN2_DEC_PREC = Scale.valueOf(10);

	/**
	 * The natural logarithm of x, for x>0.
	 * The result is accurate to at least outScale decimal after-comma digits.
	 * Calling this method with large arguments will be pretty slow.
	 * 
	 * @param x Argument
	 * @param outScale Wanted precision in after-comma decimal digits
	 * @return ln(x)
	 */
	static BigDecimal lnSeriesExpansion(BigDecimal x, Scale outScale) {
		if (x.compareTo(F_0)<=0) {
			throw new IllegalArgumentException("x=" + x + ", but ln(x) is only defined for x>0 !");
		}
		int cmp1 = x.compareTo(F_1);
	    if (cmp1 == 0) return F_0;

	    // Bestimme interne Genauigkeit und erlaubten Restfehler des letzten Serien-Elements:
	    // Diese muss im allgemeinen genauer sein als die geforderte Ausgabegenauigkeit,
	    // da die Serie fuer x<<1 and x>>1 sehr langsam konvergiert. Die folgende Bestimmung
	    // ist exakt, d.h. kleinere interne Genauigkeiten fuehren zu Rundungsfehlern!
	    int mag = Magnitude.of(x);
	    Scale elemPrec = outScale.add(mag + 1);
	    //LOG.debug("mag = " + mag + " -> internal prec = " + elemPrec);
        BigDecimal maxErr = elemPrec.getErrorBound();
        //LOG.debug("maxErr = " + maxErr);
        
        // Compute ratio (x-1)/(x+1):
    	BigDecimal r = BigDecimalMath.divide(x.subtract(F_1), x.add(F_1), elemPrec);
    	
        // Compute the square of the ratio: Note that r<1 -> square<1, too.
    	BigDecimal square = r.multiply(r); // slightly faster than pow(r, 2)
        
        // First series element:
    	BigDecimal xpow = r;

    	int i=1;
    	BigInteger den = I_3;
        BigDecimal sElement;
        do {
            xpow = elemPrec.applyTo(xpow.multiply(square));
            sElement = BigDecimalMath.divide(xpow, new BigDecimal(den), elemPrec); // new BigDecimal(den) is safer than BigDecimal(2*i+1) regarding overflows
            r = r.add(sElement);
            den = den.add(I_2);
            if (DEBUG) {
            	LOG.debug("ln(" + x + ", " + i + ") = " + r);
            	i++;
            }
            
            // Stopping criterion: We will stop here if the last series
            // element is absolute smaller than the allowed rest error.
            // This works exactly with the allowedError defined as above.
        } while (sElement.abs().compareTo(maxErr) > 0);

        // multiply sum with 2 (doubling the previously accumulated error):
        r = Pow2.mulPow2(r, 1);
        
        // assign return value:
        //LOG.debug("ln(" + x + ", " + i + ") = " + r);
        return outScale.applyTo(r);
	}
	
	/**
	 * The natural logarithm of 2.
	 * 
	 * @param outScale Wanted precision in after-comma decimal digits
	 * @return ln(2)
	 */
	static BigDecimal ln2SeriesExpansion(Scale outScale) {
	    //LOG.debug("outScale = " + outScale);
	    if (outScale.compareTo(LN2_DEC_PREC) > 0) {
	        LN2 = Ln.lnSeriesExpansion(F_2, outScale);
	        LN2_DEC_PREC = outScale;
	    }

	    // assign output:
	    return outScale.applyTo(LN2);
	}
	
	/**
	 * ln2 computation via the elementary series ln2 = sum( 1/(i*2^i), i=1...n).
	 * The root reduction formula is much faster.
	 * 
	 * @param outScale
	 * @return
	 */
	static BigDecimal ln2ElementarySeriesExpansion(Scale outScale) {
	    Scale internalScale = outScale.add(1);
	    //LOG.debug("internalScale = " + internalScale);
        BigDecimal maxErr = internalScale.getErrorBound();
        //LOG.debug("maxErr = " + maxErr);
        
        // 1/(1*2^1) + 1/(2*2^2) + 1/(3*2^3) = 2/3
    	BigDecimal r = BigDecimalMath.divide(F_2, F_3, internalScale);
    	BigInteger pow2 = I_1.shiftLeft(4); // 2^4
    	int i = 4;

        BigDecimal sElement;
        do {
            BigDecimal den = new BigDecimal(pow2.multiply(BigInteger.valueOf(i)), 0);
            sElement = BigDecimalMath.divide(F_1, den, internalScale);
            r = r.add(sElement);
            //LOG.debug("ln(" + x + ", " + i + ") = " + r);
            i++;
            pow2 = pow2.shiftLeft(1);
            // Stopping criterion: We will stop here if the last series
            // element is absolute smaller than the allowed rest error.
            // This works exactly with the allowedError defined as above.
        } while (sElement.abs().compareTo(maxErr) > 0);

        // assign return value:
        //LOG.debug("ln2 = " + r);
        return outScale.applyTo(r);
	}
	
	/**
	 * Faster ln2 implementation, computing the series expansion of 2^(1/k) for some optimally chosen k.
	 * 
	 * @param outScale Wanted precision in after-comma decimal digits
	 * @return ln(2)
	 */
	public static BigDecimal ln2/*RootReduction*/(Scale outScale) {
	    //LOG.debug("outScale = " + outScale);
	    if (outScale.compareTo(LN2_DEC_PREC) > 0) {
			int optimalDegree = Math.max(2, (int) Math.sqrt(outScale.digits()));
			Scale internalScale = outScale.add(Magnitude.of(optimalDegree));
			BigDecimal root = RootsReal.ithRoot(F_2, optimalDegree, internalScale);
		    BigDecimal series = Ln.lnSeriesExpansion(root, internalScale);
	        LN2 = series.multiply(BigDecimal.valueOf(optimalDegree));
	        LN2_DEC_PREC = outScale;
	    }

	    // assign output:
	    return outScale.applyTo(LN2);
	}

	public static BigDecimal ln(BigDecimal x, Precision outPrec) {
		// result magnitude is log10(|ln(x)|), ln(x) = log10(x) * ln(10)
		double lnxApproximate =  Math.abs(Magnitude.of(x)*Math.log(10));
		int resultMagnitude = Magnitude.of(lnxApproximate);
		// result scale is result precision - result magnitude
		Scale resultScale = Scale.valueOf(outPrec.digits() - resultMagnitude);
		//LOG.debug("resultMagnitude=" + resultMagnitude + ", resultScale=" + resultScale.digits());
		return ln(x, resultScale);
	}
	
	/**
	 * Compute the natural logarithm of x, for x>0.
	 * The result is accurate to at least outScale decimal after-comma digits.
	 * For this, the algorithm requires around outScale/2 series steps.
	 * 
	 * @param x Argument
	 * @param outScale Wanted precision in after-comma decimal digits
	 * @return ln(x)
	 */
	static BigDecimal lnSimpleReduction(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(x, d);
	    BigDecimal lnrTerm = Ln.lnSeriesExpansion(r, outScale.add(2));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    return outScale.applyTo(lnrTerm.subtract(ln2Term));
	}

	/**
	 * Compute the natural logarithm of x, for x>0.
	 * This algorithm takes the reciprocal of x>1 and then uses the simple reduction.
	 * 
	 * This is much faster than the simple reduction applied to x > 1.
	 * 
	 * @param x
	 * @param outScale
	 * @return ln(x)
	 */
	static BigDecimal lnReciprocalSimpleReduction(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    int xMag = Magnitude.of(x);
	    int outMag = xMag==0 ? 0 : Magnitude.of(xMag * Math.log(10));
	    int outPrecision = outMag + outScale.digits() + 3;
	    if (DEBUG) LOG.debug("xScale=" + x.scale() + ", xMag=" + xMag + ", outMag=" + outMag + ", outPrecision=" + outPrecision);

	    // if x>1 then we compute the logarithm of the reciprocal
	    boolean isReciprocal = false;
	    BigDecimal _x = x;
	    int _xMag = xMag;
	    if (x.compareTo(BigDecimal.ONE) > 0) {
	    	isReciprocal = true;
	    	// mag(1/x) = -mag(x)
	    	_xMag = -xMag;
	    	// We want 1/x to have the same precision as the output precision.
	    	Scale _xScale = Scale.valueOf(outPrecision - _xMag);
	    	if (DEBUG) LOG.debug("_xMag=" + _xMag + ",_xScale=" + _xScale);
	    	_x = BigDecimalMath.divide(BigDecimal.ONE, x, _xScale);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(_x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(_x, d);
	    BigDecimal lnrTerm = Ln.lnSeriesExpansion(r, outScale.add(2));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    BigDecimal result = lnrTerm.subtract(ln2Term);
	    if (isReciprocal) {
	    	result = result.negate();
	    }
	    return outScale.applyTo(result);
	}

	/**
	 * Compute the natural logarithm by means of the approximation
	 *               Pi
	 * ln(x) ~ --------------- - m*ln(2) , with s=x*2^m, s>2^(binPrec/2)
	 *          2*agm(1, 4/s)
	 */
	static BigDecimal lnAgm(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("Logarithm of a non-positive argument argument expected");
	    }

	    int xMag = Magnitude.of(x);
	    int outMag = xMag==0 ? 0 : Magnitude.of(xMag * Math.log(10));
	    int outPrecision = outMag + outScale.digits() + 3;
	    if (DEBUG) LOG.debug("xScale=" + x.scale() + ", xMag=" + xMag + ", outMag=" + outMag + ", outPrecision=" + outPrecision);

	    // if x<1 then we compute the logarithm of the reciprocal
	    boolean isReciprocal = false;
	    BigDecimal _x = x;
	    int _xMag = xMag;
	    if (x.compareTo(BigDecimal.ONE) < 0) {
	    	isReciprocal = true;
	    	// mag(1/x) = -mag(x)
	    	_xMag = -xMag;
	    	// We want 1/x to have the same precision as the output precision.
	    	Scale _xScale = Scale.valueOf(outPrecision - _xMag);
	    	if (DEBUG) LOG.debug("_xMag=" + _xMag + ",_xScale=" + _xScale);
	    	_x = BigDecimalMath.divide(BigDecimal.ONE, x, _xScale);
	    }
	    
	    // Compute m and s:
	    int binaryOutPrecision = Magnitude.decimalToBinary(outPrecision);
	    // Computing m = binaryOutPrecision/2 is principally exact.
	    // But the sqrt in agm(a, b) is too slow for huge differences in magnitude of a and b
	    // -> We need an upper bound for m such that the initial guess of the sqrt is not too bad.
	    // outMag = log10(ln(x)) = log10(log10(x) * ln(10))
	    // sMax = 2^1024 = x * 2^mMax => 2^mMax = 2^1024 / x = 2^1024 / 2^ld(x) = 2^(1024-ld(x))
	    // => mMax = 1024 - ld(x)
	    int mMax = 1024 - Magnitude.decimalToBinary(_xMag);
	    int m = (int) Math.min(mMax, Math.ceil(0.5*binaryOutPrecision));
	    BigDecimal s = Pow2.mulPow2(_x, m);
	    if (DEBUG) LOG.debug("m=" + m + ", s=" + s);
	    
	    // Arithmetic-geometric mean of 1 and b=4/s:
	    // b is pretty small like 10^-100 -> we need to add the magnitude of s to bScale
	    Scale bScale = outScale.add(Magnitude.of(s));
	    if (DEBUG) LOG.debug("bScale=" + bScale);
	    BigDecimal b = BigDecimalMath.divide(F_4, s, bScale);
	    if (DEBUG) LOG.debug("b=" + b);
	    int agmMag = Agm.getResultMagnitude(F_1, b);
	    Scale agmScale = outScale.add(5);
	    if (agmMag < 0) agmScale = agmScale.add(-agmMag);
	    BigDecimal agm = Agm.agm(F_1, b, agmScale); // time-critical !!
	    if (DEBUG) LOG.debug("agm(1, " + b + ") = " + agm);
	    BigDecimal r = Pow2.mulPow2(agm, 1); // r = 2*agm
	    if (DEBUG) LOG.debug("r = " + r);

	    BigDecimal pi = Pi.pi(outScale.add(4));
	    BigDecimal t = BigDecimalMath.divide(pi, r, outScale.add(2)); // pi, r need outScale + 4
	    if (DEBUG) LOG.debug("t = " + t);

	    Scale ln2Scale = outScale.add(Magnitude.of(m) + 1);
	    BigDecimal ln2Term = Ln.ln2(ln2Scale);
	    BigDecimal mln2 = BigDecimalMath.multiply(ln2Term, m); // ln2Term needs scale outScale + magnitude(m) + 1
	    if (DEBUG) LOG.debug("ln2Term.scale() = " + ln2Term.scale() + ", mln2 = " + mln2);
	    BigDecimal u = t.subtract(mln2); // t and mln2 need scale outScale + 1
	    
	    if (isReciprocal) {
	    	u = u.negate();
	    }
	    
	    // Assign output:
	    if (DEBUG) LOG.debug("lnAgm(" + x + ", " + outScale + ") = " + u);
	    return outScale.applyTo(u);
	}

	/**
	 * Computes ln(x) using the simple reduction formula followed by the AGM reduction.
	 * This is a good combination because agm(a, b) converges fastest for a ~ b;
	 * and the simple reduction reduces x to some x'~1 and lets us compute agm(1, x').
	 * @param x
	 * @param outScale
	 * @return
	 */
	static BigDecimal lnSimplePlusAgmReduction(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(x, d);
	    BigDecimal lnrTerm = Ln.lnAgm(r, outScale.add(1));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    return outScale.applyTo(lnrTerm.subtract(ln2Term));
	}
	
	/**
	 * Compute the natural logarithm of x, for x>0.
	 * This algorithm takes the reciprocal of x>1 and then uses the simple reduction, followed by an AGM reduction.
	 * 
	 * The fastest algorithm for all number ranges.
	 * 
	 * @param x
	 * @param outScale
	 * @return ln(x)
	 */
	public static BigDecimal ln/*ReciprocalSimplePlusAgmReduction*/(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    int xMag = Magnitude.of(x);
	    int outMag = xMag==0 ? 0 : Magnitude.of(xMag * Math.log(10));
	    int outPrecision = outMag + outScale.digits() + 3;
	    if (DEBUG) LOG.debug("xScale=" + x.scale() + ", xMag=" + xMag + ", outMag=" + outMag + ", outPrecision=" + outPrecision);

	    // if x>1 then we compute the logarithm of the reciprocal
	    boolean isReciprocal = false;
	    BigDecimal _x = x;
	    int _xMag = xMag;
	    if (x.compareTo(BigDecimal.ONE) > 0) {
	    	isReciprocal = true;
	    	// mag(1/x) = -mag(x)
	    	_xMag = -xMag;
	    	// We want 1/x to have the same precision as the output precision.
	    	Scale _xScale = Scale.valueOf(outPrecision - _xMag);
	    	if (DEBUG) LOG.debug("_xMag=" + _xMag + ",_xScale=" + _xScale);
	    	_x = BigDecimalMath.divide(BigDecimal.ONE, x, _xScale);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(_x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(_x, d);
	    BigDecimal lnrTerm = Ln.lnAgm(r, outScale.add(2));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    BigDecimal result = lnrTerm.subtract(ln2Term);
	    if (isReciprocal) {
	    	result = result.negate();
	    }
	    return outScale.applyTo(result);
	}

	/**
	 * ln implementation computing the series expansion of x^(1/k) for some optimally chosen k.
	 * 
	 * @param x
	 * @param outScale Wanted precision in after-comma decimal digits
	 * @return ln(x)
	 */
	static BigDecimal lnRootReduction(BigDecimal x, Scale outScale) {
		if (DEBUG) LOG.debug("outScale = " + outScale);
		Scale internalScale = outScale.add(Math.abs(Magnitude.of(x)) + 4); // fits exactly!
		int d = Math.max(2, internalScale.digits());
		if (DEBUG) LOG.debug("d = " + d + ", internalScale = " + internalScale);
		BigDecimal root = RootsReal.ithRoot(x, d, internalScale);
		if (DEBUG) LOG.debug("root = " + root);
	    BigDecimal series = Ln.lnSeriesExpansion(root, internalScale);
	    if (DEBUG) LOG.debug("series = " + series);
        BigDecimal lnx = series.multiply(BigDecimal.valueOf(d));

	    // assign output:
	    return outScale.applyTo(lnx);
	}

	/**
	 * Computes ln(x) using the simple reduction formula followed by the AGM reduction.
	 * This is a good combination because agm(a, b) converges fastest for a ~ b;
	 * and the simple reduction reduces x to some x'~1 and lets us compute agm(1, x').
	 * @param x
	 * @param outScale
	 * @return
	 */
	static BigDecimal lnSimplePlusRootReduction(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(x, d);
	    BigDecimal lnrTerm = Ln.lnRootReduction(r, outScale.add(1));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    return outScale.applyTo(lnrTerm.subtract(ln2Term));
	}
	
	/**
	 * Compute the natural logarithm of x, for x>0.
	 * This algorithm takes the reciprocal of x>1 and then uses the simple reduction, followed by an AGM reduction.
	 * 
	 * @param x
	 * @param outScale
	 * @return ln(x)
	 */
	static BigDecimal lnReciprocalSimplePlusRootReduction(BigDecimal x, Scale outScale) {
	    // check argument:
	    if (x.compareTo(F_0) <= 0) {
	    	throw new IllegalArgumentException("ln: argument should be positive, but is " + x);
	    }

	    int xMag = Magnitude.of(x);
	    int outMag = xMag==0 ? 0 : Magnitude.of(xMag * Math.log(10));
	    int outPrecision = outMag + outScale.digits() + 3;
	    if (DEBUG) LOG.debug("xScale=" + x.scale() + ", xMag=" + xMag + ", outMag=" + outMag + ", outPrecision=" + outPrecision);

	    // if x>1 then we compute the logarithm of the reciprocal
	    boolean isReciprocal = false;
	    BigDecimal _x = x;
	    int _xMag = xMag;
	    if (x.compareTo(BigDecimal.ONE) > 0) {
	    	isReciprocal = true;
	    	// mag(1/x) = -mag(x)
	    	_xMag = -xMag;
	    	// We want 1/x to have the same precision as the output precision.
	    	Scale _xScale = Scale.valueOf(outPrecision - _xMag);
	    	if (DEBUG) LOG.debug("_xMag=" + _xMag + ",_xScale=" + _xScale);
	    	_x = BigDecimalMath.divide(BigDecimal.ONE, x, _xScale);
	    }

	    // Since ln(r) is converging best for r ~ 1, I'm looking for a
	    // representation of x as x = r*2^(-d) with r = x*2^d ~ 1, such that
	    // ln(x) = ln(r) - d*ln(2).
	    // From 2^d ~ 1/x we get d ~ -ld(x), and since d has to be a whole number,
	    // we get the two possibilities d1=floor(-ld(x)) and d2=ceil(-ld(x)).
	    // It turns out that choosing the d whose r is geometrically nearer to 1
	    // is the one obtained by rounding -ld(x)...
	    //LOG.debug("x=" + x);
	    double xBits = Magnitude.of(_x)*Math.log(10)/Math.log(2);
	    int d = (int) Math.ceil(-xBits-0.5); // work for x > Double.MAX_VALUE
	    //LOG.debug("d=" + d);
	    
	    // Compute ln(x*2^d):
	    BigDecimal r = Pow2.mulPow2(_x, d);
	    BigDecimal lnrTerm = Ln.lnRootReduction(r, outScale.add(2));
	    //LOG.debug("lnrTerm.scale() = " + lnrTerm.scale());
	    
	    // Compute ln(2) with required accuracy and multiply with the exponent:
	    Scale ln2Precision = outScale.add(Magnitude.of(d));
	    //LOG.debug("ln2Precision = " + ln2Precision);
	    BigDecimal ln2Term = Ln.ln2(ln2Precision).multiply(BigDecimal.valueOf(d));
	    //LOG.debug("ln2Term.scale() = " + ln2Term.scale());
	    
	    // Assign result: ln(x) = ln(r) - d*ln(2)
	    BigDecimal result = lnrTerm.subtract(ln2Term);
	    if (isReciprocal) {
	    	result = result.negate();
	    }
	    return outScale.applyTo(result);
	}
}

