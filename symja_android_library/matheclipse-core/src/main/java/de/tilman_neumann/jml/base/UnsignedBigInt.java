/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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
package de.tilman_neumann.jml.base;

import java.math.BigInteger;

//import org.apache.log4j.Logger;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
/**
 * A very limited unsigned big integer implementation.
 * Currently the only implemented arithmetic methods are division and modulus of big integers by small integers.
 * These methods are notably faster than using BigInteger.divide(BigInteger),
 * like factor 2.5 for BigIntegers with 100 bit, factor 1.8 at 200 bit, factor 1.6 at 300 bit.
 * 
 * @author Tilman Neumann
 */
public class UnsignedBigInt {
//	private static final Logger LOG = Logger.getLogger(UnsignedBigInt.class);
//	private static final boolean DEBUG = false;
	
	private static final BigInteger UNSIGNED_INT_MASK_BIG = BigInteger.valueOf(0xFFFFFFFFL);

	int intLength;
	int[] intArray;

	/**
	 * Copy constructor
	 * @param N original unsigned big int
	 */
	public UnsignedBigInt(UnsignedBigInt N) {
		this(new int[50]);
		System.arraycopy(N.intArray, 0, intArray, 0, N.intLength);
		this.intLength = N.intLength;
	}

	/**
	 * Shortcut constructor for <code>new UnsignedBigInt(); set(N);</code>
	 * @param N
	 */
	public UnsignedBigInt(BigInteger N) {
		this.set(N);
	}

	/**
	 * Constructor using the given buffer. The numbers to work on must be set using the
	 * {@link #set(BigInteger)} method.
	 * 
	 * @param buffer a buffer big enough to represent all numbers that will be set using {@link #set(BigInteger)}.
	 */
	public UnsignedBigInt(int[] buffer) {
		intArray = buffer;
	}

	/**
	 * Sets this to the given BigInteger N. This method must be called before any arithmetic operation.
	 * If a buffer has been passed to the constructor then it should be big enough to represent N.
	 * 
	 * @param N
	 */
	public void set(BigInteger N) {
		intLength = (N.bitLength()+31)>>5; // round up
		if (intLength > 0) {
			if (intArray == null || intArray.length < intLength) {
				// allocate new 0-initialized array
				intArray = new int[intLength];
			} else {
				// clear existing buffer
				for (int i=intLength-1; i>=0; i--) intArray[i] = 0; // TODO: use arrayCopy() ?
			}
			
			// bytes in big-endian order, i.e. the most significant byte is at index 0
			byte[] bytes = N.toByteArray();
			// convert byte[] to unsigned int[]
			//LOG.debug("#bytes = " + bytes.length + ", #ints = " + intLength);
			int i=0, j=0;
			for (int bPos=bytes.length-1; bPos>=0; bPos--) {
				int b = bytes[bPos] & 0xFF;
				intArray[i] |= b<<(j<<3);
				if (++j==4) {
					if (++i == intLength) {
						// The most significant byte of N.toByteArray() has a sign bit, which is 0 for unsigned integers.
						// But it may lead to another byte! -> skip that one...
						//LOG.debug("i has reached maximum value " + i);
						break;
					}
					j = 0;
				}
			}
		}
		
//		if (DEBUG) {
//			try {
//				// compare with slower but safer implementation
//				int[] intArrayFromNShifts = safeConversion(N);
//				for (int i=0; i<intLength; i++) {
//					assertEquals(intArrayFromNShifts[i], intArray[i]);
//				}
//			} catch (AssertionError ae) {
//				LOG.debug("N              = " + N.toString(2));
//				LOG.debug("UnsignedBigInt = " + this.toBinaryString());
//				throw ae;
//			}
//		}
	}

	private int[] safeConversion(BigInteger N) {
		int[] intArrayFromNShifts = new int[intLength];
		for (int i2=0; i2<intLength; i2++) {
			intArrayFromNShifts[i2] = N.and(UNSIGNED_INT_MASK_BIG).intValue();
			N = N.shiftRight(32);
		}
		return intArrayFromNShifts;
	}
	
	/**
	 * Test for 0. The caller must make sure that {@link #set(BigInteger)} has been invoked before.
	 * 
	 * @return true if this==0, false else
	 */
	public boolean isZero() {
		if (intLength==0) return true;
		for (int i=intLength-1; i>=0; i--) {
			if (intArray[i]!=0) return false;
		}
		return true;
	}

	/**
	 * Test for 1. The caller must make sure that {@link #set(BigInteger)} has been invoked before.
	 * 
	 * @return true if this==1, false else
	 */
	public boolean isOne() {
		if (intLength==0 || intArray[0]!=1) return false;
		for (int i=intLength-1; i>0; i--) {
			if (intArray[i]!=0) return false;
		}
		return true;
	}
	
	public int intLength() {
		return intLength;
	}
	
    /**
     * Divide this by the given <code>divisor</code>, store the quotient in <code>quotient</code> and return the remainder.
     * The caller must make sure that {@link #set(BigInteger)} has been invoked before.
     * 
     * @param divisor
     * @param quotient output!
     * @return remainder
     */
    public int divideAndRemainder(final int divisor, UnsignedBigInt quotient) {
    	// A special treatment of intLength==1 is asymptotically bad
        long rem = 0;
        long divisor_long = divisor & 0xFFFFFFFFL;
        long currentDividend, quot;

        // loop that determines intLength by the way
        quotient.intLength = 0; // if this < divisor
        int i = intLength-1;
        for (; i >= 0; i--) {
            currentDividend = (rem << 32) | (intArray[i] & 0xFFFFFFFFL);
            quot = currentDividend / divisor_long;
    		// rem = currentDividend % divisor_long is faster than currentDividend - quot*divisor_long
            rem = currentDividend % divisor_long;
            //if (DEBUG) {
            //	assertTrue(currentDividend >= 0);
            //	assertTrue(quot <= 0xFFFFFFFFL);
            //}
            quotient.intArray[i] = (int) (quot & 0xFFFFFFFFL);
            if (quot>0) {
            	quotient.intLength = i+1;
            	i--; // loop decrement will not be carried out after break
            	break; // go to loop without intLength-test
            }
        }
        
        // loop without intLength-test
        for (; i >= 0; i--) {
            currentDividend = (rem << 32) | (intArray[i] & 0xFFFFFFFFL);
            quot = currentDividend / divisor_long;
    		// rem = currentDividend % divisor_long is faster than currentDividend - quot*divisor_long
            rem = currentDividend % divisor_long;
            //if (DEBUG) {
            //	assertTrue(currentDividend >= 0);
            //	assertTrue(quot <= 0xFFFFFFFFL);
            //}
            quotient.intArray[i] = (int) (quot & 0xFFFFFFFFL);
        }
        
        return (int) rem;
    }

    /**
     * Mutable divide and remainder computation.
     * @param divisor
     * @return
     */
    public int divideAndRemainder(final int divisor) {
    	// A special treatment of intLength==1 is asymptotically bad
        long rem = 0;
        long divisor_long = divisor & 0xFFFFFFFFL;
        long currentDividend, quot;

        // loop that determines intLength by the way
        int i = intLength-1;
        for (; i >= 0; i--) {
            currentDividend = (rem << 32) | (intArray[i] & 0xFFFFFFFFL);
            quot = currentDividend / divisor_long;
    		// rem = currentDividend % divisor_long is faster than currentDividend - quot*divisor_long
            rem = currentDividend % divisor_long;
            //if (DEBUG) {
            //	assertTrue(currentDividend >= 0);
            //	assertTrue(quot <= 0xFFFFFFFFL);
            //}
            intArray[i] = (int) (quot & 0xFFFFFFFFL);
            if (quot>0) {
            	intLength = i+1;
            	i--; // loop decrement will not be carried out after break
            	break; // go to loop without intLength-test
            }
        }
        
        // loop without intLength-test
        for (; i >= 0; i--) {
            currentDividend = (rem << 32) | (intArray[i] & 0xFFFFFFFFL);
            quot = currentDividend / divisor_long;
    		// rem = currentDividend % divisor_long is faster than currentDividend - quot*divisor_long
            rem = currentDividend % divisor_long;
            //if (DEBUG) {
            //	assertTrue(currentDividend >= 0);
            //	assertTrue(quot <= 0xFFFFFFFFL);
            //}
            intArray[i] = (int) (quot & 0xFFFFFFFFL);
        }
        
        return (int) rem;
    }

    /**
     * Compute the remainder of this modulo divisor.
     * The caller must make sure that {@link #set(BigInteger)} has been invoked before.
     * 
     * @param divisor
     * @return remainder
     */
    public int mod(final int divisor) {
    	// A special treatment of intLength==1 is asymptotically bad
        long rem = 0;
        long divisor_long = divisor & 0xFFFFFFFFL;
        long currentDividend;
        for (int i = intLength-1; i >= 0; i--) {
            currentDividend = (rem << 32) | (intArray[i] & 0xFFFFFFFFL);
    		// rem = currentDividend % divisor_long is faster than currentDividend - quot*divisor_long
            rem = currentDividend % divisor_long;
        }
        return (int) rem;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o==null || !(o instanceof UnsignedBigInt)) return false;
    	UnsignedBigInt other = (UnsignedBigInt) o;
    	if (intLength!=other.intLength) return false;
    	for (int i=intLength-1; i>=0; i--) {
    		if (intArray[i]!=other.intArray[i]) return false;
    	}
    	return true;
    }

	public BigInteger toBigInteger() {
		//LOG.debug("intLength = " + intLength);
		if (intLength==0) return I_0;
		if (intLength==1) return BigInteger.valueOf(intArray[0] & 0xFFFFFFFFL);
		// For intLength==2 we already need byte[], because intArray[1]<<32 could be a negative long
		int byteLength = intLength<<2;
		byte[] bytes = new byte[byteLength+1]; // add one extra-byte to assure that there is a zero sign-bit
		for (int i = 0; i < intLength; i++) {
			long digit = intArray[i] & 0xFFFFFFFFL; // the long mask avoids negative values
			int bPos = byteLength - (i<<2);
			bytes[bPos] = (byte) (digit & 0xFF);
			bytes[--bPos] = (byte) (digit / 0x100 & 0xFF);
			bytes[--bPos] = (byte) (digit / 0x10000 & 0xFF);
			bytes[--bPos] = (byte) (digit / 0x1000000 & 0xFF);
		}
		BigInteger N = new BigInteger(bytes);
		
//		if (DEBUG) {
//			// compare with slower but safer implementation
//			BigInteger NfromShifts = BigInteger.valueOf(intArray[intLength-1] & 0xFFFFFFFFL);
//			for (int i=intLength-2; i>=0; i--) {
//				NfromShifts = NfromShifts.shiftLeft(32).add(BigInteger.valueOf(intArray[i] & 0xFFFFFFFFL));
//			}
//			assertEquals(NfromShifts, N);
//		}
		return N;
	}

	@Override
	public String toString() {
		return this.toBigInteger().toString();
	}
	
	public String toBinaryString() {
		String bitString = "";
		for (int i=intLength-1; i>=0; i--) {
			String elem = Integer.toBinaryString(intArray[i]);
			String filler = "";
			if (i<intLength-1) {
				for (int j=32-elem.length(); j>0; j--) {
					filler+="0";
				}
			}
			bitString += filler + elem;
		}
		return bitString;
	}
}
