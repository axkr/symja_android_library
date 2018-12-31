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
package de.tilman_neumann.jml.modular;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

/**
 * Jacobi symbol. The basic implementation follows [Crandall, Pomerance 2005: Prime numbers];
 * then we apply some optimizations.
 * 
 * @author Tilman Neumann
 */
public class JacobiSymbol {
	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(JacobiSymbol.class);

	/**
	 * JacobiSymbol J(a|m), m an odd integer, for BigInteger arguments.
	 * Basic implementation. (slow)
	 */
	/* not public */ int jacobiSymbol_v01(BigInteger a, BigInteger m) {
    	int aCmpZero = a.compareTo(I_0);
        if (aCmpZero == 0) return 0;

       // make a positive
		int t=1, mMod8;
        if (aCmpZero < 0) {
            a = a.negate();
            mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
            if (mMod8==3 || mMod8==7) t = -t;
        }

		a = a.mod(m);
		
		// reduction loops
        int mMod4, aMod4;
		BigInteger tmp;
		while(!a.equals(I_0)) {
			// make a odd: we can take a.intValue() because we only need the lowest bit
			while ((a.intValue() & 1) == 0) {
				a = a.shiftRight(1);
				mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// swap variables
			tmp = a; a = m; m = tmp;
			// quadratic reciprocity
			aMod4 = a.intValue() & 3; // get lowest 2 bit
			mMod4 = m.intValue() & 3;
			if (aMod4==3 && mMod4==3) t = -t; // a == m == 3 (mod 4) -> negate t
			a = a.mod(m);
		}
		if (m.equals(I_1)) return t;
		return 0;
	}
	
	/**
	 * Jacobi symbol J(a|m), with m odd.
	 * 
	 * First optimization. (still slow)
	 * 
	 * @param a
	 * @param m
	 * @return
	 */
	/* not public */ int jacobiSymbol_v02(BigInteger a, BigInteger m) {
    	int aCmpZero = a.compareTo(I_0);
        if (aCmpZero == 0) return 0;

       // make a positive
		int t=1, mMod8;
        if (aCmpZero < 0) {
            a = a.negate();
            mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
            if (mMod8==3 || mMod8==7) t = -t;
        }

		a = a.mod(m);
		
		// reduction loops
        int lsb, mMod4, aMod4;
		boolean hasOddPowerOf2;
		BigInteger tmp;
		while(!a.equals(I_0)) {
			// make a odd
			lsb = a.getLowestSetBit();
			hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
			if (lsb > 1) {
				// powers of 4 do not change t -> remove them in one go
				a = a.shiftRight(hasOddPowerOf2 ? lsb-1 : lsb);
			}
			if (hasOddPowerOf2) {
				a = a.shiftRight(1);
				mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// swap variables
			tmp = a; a = m; m = tmp;
			// quadratic reciprocity
			aMod4 = a.intValue() & 3; // get lowest 2 bit
			mMod4 = m.intValue() & 3;
			if (aMod4==3 && mMod4==3) t = -t; // a == m == 3 (mod 4)
			a = a.mod(m);
		}
		if (m.equals(I_1)) return t;
		return 0;
	}
	
	/**
	 * Jacobi symbol J(a|m), with m an odd, positive integer.
	 * 
	 * Highly optimized, using faster quadratic reciprocity.
	 * 
	 * @param a
	 * @param m an odd integer
	 * @return
	 */
	public int jacobiSymbol/*_v03*/(BigInteger a, BigInteger m) {
    	int aCmpZero = a.compareTo(I_0);
        if (aCmpZero == 0) return 0;

       // make a positive
		int t=1, mMod8;
        if (aCmpZero < 0) {
            a = a.negate();
            mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
            if (mMod8==3 || mMod8==7) t = -t;
        }

		a = a.mod(m);
		
		// reduction loop
        int lsb;
		boolean hasOddPowerOf2;
		BigInteger tmp;
		while(!a.equals(I_0)) {
			// make a odd
			lsb = a.getLowestSetBit();
			hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
			if (lsb > 1) {
				// powers of 4 do not change t -> remove them in one go
				a = a.shiftRight(hasOddPowerOf2 ? lsb-1 : lsb);
			}
			if (hasOddPowerOf2) {
				a = a.shiftRight(1);
				mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// now both a and m are odd (m was odd from the start!)
			// swap variables
			tmp = a; a = m; m = tmp;
			// quadratic reciprocity: the fact that both a and m are odd allows a very fast test == 3 (mod 4)
			if (a.testBit(1) && m.testBit(1)) t = -t; // a == m == 3 (mod 4)
			// reduce a
			a = a.mod(m);
		}
		return (m.equals(I_1)) ? t : 0;
	}
	
	public int jacobiSymbol/*_v03*/(BigInteger a, int m) {
    	int aCmpZero = a.compareTo(I_0);
        if (aCmpZero == 0) return 0;

       // make a positive
		int t=1, mMod8;
        if (aCmpZero < 0) {
            a = a.negate();
            mMod8 = m & 7;
            if (mMod8==3 || mMod8==7) t = -t;
        }

		// a mod m must be int because m is int
		int a_int = a.mod(BigInteger.valueOf(m)).intValue();

		// reduction loop
        int lsb, tmp;
		boolean hasOddPowerOf2;
		while(a_int != 0) {
			// make a odd
			lsb = Integer.numberOfTrailingZeros(a_int);
			hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
			if (lsb > 1) {
				// powers of 4 do not change t -> remove them in one go
				a_int >>= hasOddPowerOf2 ? lsb-1 : lsb;
			}
			if (hasOddPowerOf2) {
				a_int >>= 1;
				mMod8 = m & 7;
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// now both a and m are odd (m was odd from the start!)
			// swap variables
			tmp = a_int; a_int = m; m = tmp;
			// quadratic reciprocity
			if (((a_int&3)==3) && ((m&3) == 3)) t = -t; // a == m == 3 (mod 4)
			// reduce a
			a_int %= m;
		}
		return (m==1) ? t : 0;
	}
	
	public int jacobiSymbol/*_v03*/(int a, BigInteger m) {
        if (a == 0) return 0;

       // make a positive
		int t=1;
        int mMod8 = m.intValue() & 7; // for m%8 we need only the lowest 3 bits
        if (a < 0) {
            a = -a;
            if (mMod8==3 || mMod8==7) t = -t;
        }

		// usually we have a < m here, but this is not guaranteed; so we take a mod m if m is int-sized:
		if (m.bitLength()<32) {
			a %= m.intValue();
		}
		
		// for this method signature, we need one preliminary reduction step to reduce m to int:
		// make a odd
		int lsb = Integer.numberOfTrailingZeros(a);
		boolean hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
		if (lsb > 1) {
			// powers of 4 do not change t -> remove them in one go
			a >>= hasOddPowerOf2 ? lsb-1 : lsb;
		}
		if (hasOddPowerOf2) {
			a >>= 1;
			// mMod8 is still valid
			if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
		}
		if (a==0) return (m.equals(I_1)) ? t : 0;

		// now both a and m are odd (m was odd from the start!)
		// swap variables:
		int m_int = a;
		// we skip assigning a_big = m -> therefore the following operations look a bit strange ;)
		// quadratic reciprocity
		if (m.testBit(1) && ((m_int&3) == 3)) t = -t; // a == m == 3 (mod 4)
		// reduce a
		//LOG.debug("a_big = " + a_big + ", m_int = " + m_int);
		a = m.mod(BigInteger.valueOf(m_int)).intValue();

		// reduction loop
		int tmp;
		while (a != 0) {
			// make a odd
			lsb = Integer.numberOfTrailingZeros(a);
			hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
			if (lsb > 1) {
				// powers of 4 do not change t -> remove them in one go
				a >>= hasOddPowerOf2 ? lsb-1 : lsb;
			}
			if (hasOddPowerOf2) {
				a >>= 1;
				mMod8 = m_int&7;
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// swap variables
			tmp = a; a = m_int; m_int = tmp;
			// quadratic reciprocity
			if (((a&3)==3) && ((m_int&3) == 3)) t = -t; // a == m == 3 (mod 4)
			// reduce a
			a %= m_int;
		}
		return (m_int==1) ? t : 0;
	}
	
	public int jacobiSymbol/*_v03*/(int a, int m) {
        if (a == 0) return 0;

       // make a positive
		int t=1, mMod8;
        if (a < 0) {
            a = -a;
            mMod8 = m & 7;
            if (mMod8==3 || mMod8==7) t = -t;
        }

		a %= m;
		
		// reduction loop
        int lsb, tmp;
		boolean hasOddPowerOf2;
		while(a != 0) {
			// make a odd
			lsb = Integer.numberOfTrailingZeros(a);
			//LOG.debug("a = " + a + ", lsb = " + lsb);
			hasOddPowerOf2 = (lsb&1)==1; // e.g. lsb==1 -> a has one 2
			if (lsb > 1) {
				// powers of 4 do not change t -> remove them in one go
				a >>= (hasOddPowerOf2 ? lsb-1 : lsb);
			}
			if (hasOddPowerOf2) {
				a >>= 1;
				mMod8 = m & 7;
				if (mMod8==3 || mMod8==5) t = -t; // m == 3, 5 (mod 8) -> negate t
			}
			// now both a and m are odd (m was odd from the start!)
			// swap variables
			tmp = a; a = m; m = tmp;
			// quadratic reciprocity
			if (((a&3)==3) && ((m&3) == 3)) t = -t; // a == m == 3 (mod 4)
			// reduce a
			a %= m;
		}
		return (m==1) ? t : 0;
	}

    /**
     * Kronecker Kr(a|m) symbol generalizes Jacobi symbol J(a|m) for arbitrary natural numbers m.
     * @param a
     * @param m
     * @return
     */
    public int kroneckerSymbol(BigInteger a, BigInteger m) {
    	int easyPart = 1;
    	// make n positive if necessary
    	int mCmpZero = m.compareTo(I_0);
    	if (mCmpZero == 0) throw new IllegalArgumentException("Kronecker symbol Kr(a|m): illegal argument m=0");
    	if (mCmpZero < 0) {
    		easyPart = -1;
    		m = m.negate();
    	}
    	
    	// eliminate factors of 2 in m
    	while ((m.intValue() & 1) == 0) {
    		// m has a factor of 2
    		if ((a.intValue() & 1) == 0) return 0;

    		// Kr(a|2) is +1 if a%8=1,7
    		// Kr(a|2) is -1 if a%8=3,5
    		int aMod8 = a.intValue() & 7; // for a%8 we need only the lowest 3 bits
    		if (aMod8==3 || aMod8==5) {
    			easyPart = -easyPart;
    		}
    		// continue working on the rest Kr(a| (m/2))
    		m = m.shiftRight(1);
    	}
    	
    	// result is easyPart multiplied with result for the remaining m
    	if (m.equals(I_1)) return easyPart;
    	return easyPart * jacobiSymbol(a, m);
    }
}
