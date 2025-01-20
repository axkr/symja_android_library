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
package de.tilman_neumann.jml.combinatorics;

import java.math.BigInteger;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigIntPoly;
import de.tilman_neumann.util.Pair;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Computation of Stirling numbers.
 * 
 * @author Tilman Neumann
 */
public class Stirling {
	private static final Logger LOG = LogManager.getLogger(Stirling.class);

	private static final boolean DEBUG = false;
	
	/** Cache for fast implementation with memory */
    private static BigInteger[][] stirling1Arr = new BigInteger[][] {new BigInteger[] {I_1} };
	
	/**
	 * Object used to synchronize access to the static Stirling numbers array.
	 * We need to call a constructor, with valueOf() we would block one of the standard values!
	 */
	private static Object syncObject = new Object();

    /** hashtable for of Stirling numbers of the first kind indexed by (n,k) */
    private static HashMap<Pair<Integer, Integer>, BigInteger> s1Map = new HashMap<Pair<Integer, Integer>, BigInteger>();

	/**
	 * (Signed) Stirling numbers of the first kind.
	 * @param n Upper parameter
	 * @param k Lower parameter
	 * @return s(n,k)
	 */
	public static BigInteger stirling1(int n, int k) {
		if (n<500) { // avoid spending too much memory
			return stirling1WithMemory(n, k);
		}
		// slower but less memory-expensive
		return stirling1ByGF(n, k);
	}

	/**
	 * Absolute Stirling numbers of the first kind.
	 * @param n Upper parameter
	 * @param k Lower parameter
	 * @return |s(n,k)|
	 */
	public static BigInteger absStirling1(int n, int k) {
		return stirling1(n, k).abs();
	}

    /**
     * Slow recursive calculation of the signed Stirling numbers
     * of the first kind. These correspond to the number of permutations of a set
     * of n symbols with exactly k permutation cycles.
     */
    static BigInteger stirling1Recurrent(int n, int k) {
    	if (n>k && k>0) {
            // recursion:
            return stirling1Recurrent(n-1,k-1).subtract(stirling1Recurrent(n-1,k).multiply(BigInteger.valueOf(n-1)));
    	}
        if (n==k) {
            return I_1;
        }
        return I_0;
    }

	/** 
	 * Evaluate s(n,k) by using the finite sum formula relating it to the Stirling numbers
	 * of the second kind. This is faster than the recurrence, but slower than the g.f.
	 * approach.
	 * 
	 * @param n the first argument to the Stirling function.
	 * @param k the second argument to the Stirling function.
	 * @return s(n,k)
	 * @see <a href="http://en.wikipedia.org/wiki/Stirling_number">http://en.wikipedia.org/wiki/Stirling_number</a>
	 */
	static BigInteger stirling1ByStirling2(int n, int k) {
    	if (n>k && k>0) {
    		BigInteger result = I_0;
			for (int j=0; j<=n-k; j++) {
				BigInteger elem = Binomial.binomial((n-1+j), (n-k+j)).multiply(Binomial.binomial((2*n-k), (n-k-j))).multiply(stirling2(n-k+j,j));
				if (j%2 != 0) {
					elem = elem.negate();
				}
				result = result.add(elem);
			}
			return result;
    	}
        if (n==k) {
            return I_1;
        }
        return I_0;
	}

	/** 
	 * Evaluate s(n,k) by building the product of the polynomials
	 * in the g.f. and extracting the corresponding factor.
	 * This is faster than the recurrence formula or computation via the
	 * Stirlings of the second kind, but slower than the version with memory.
	 * 
	 * @param n the first argument to the Stirling function.
	 * @param k the second argument to the Stirling function.
	 * @return s(n,k)
	 * @warning insufficient checks for overflow of n or m
	 * @see adapted_from http://www.strw.leidenuniv.nl/~mathar/progs/FI/oeis_8java.html
	 */
	static BigInteger stirling1ByGF(int n, int k) {
    	if (n>k && k>0) {
			// define the polynomial x
    		BigIntPoly xprod = new BigIntPoly(n);
    		BigIntPoly xn = new BigIntPoly(n);
	
			for(int i=1; i<n; i++) {
				// multiply xprod by x-i; first set the absolute coefficient of xn to -i
				xn.set(0, BigInteger.valueOf(-i));
				xprod = xprod.multiply(xn);
			}
			return xprod.get(k);
    	}
        if (n==k) {
            return I_1;
        }
        return I_0;
	}

	/**
	 * (Signed) Stirling numbers of the first kind s(n,k).
	 * Implementation with memory is very fast, but might lead to
	 * OutOfMemoryErrors for n slightly above 1000.
	 * @param n Upper parameter
	 * @param k Lower parameter
	 * @return s(n,k)
	 */
	static BigInteger stirling1WithMemory(int n, int k) {
    	if (n>k && k>0) {
	    	// pass by the synchronized block if the Stirling array is big enough
			if (n>=stirling1Arr.length) {
	    		// we need to enlarge the Stirling array, but we don't want
	    		// several threads to do this in parallel (and the latter undo
	    		// the effects of the first ones). Therefore we do this in a
	    		// synchronized block, and every thread checks again if the
	    		// array is still to small when it enters the block:
	    		synchronized (syncObject) {
	    			if (n>=stirling1Arr.length) {
	    		    	BigInteger[][] newStirlings = new BigInteger[n+10][];
	    		    	System.arraycopy(stirling1Arr, 0, newStirlings, 0, stirling1Arr.length);
	    		    	// initialize new entries:
	    		    	for (int i=stirling1Arr.length; i<newStirlings.length; i++) {
	    		    		newStirlings[i] = new BigInteger[i+1];
	    		    	}
	    		    	stirling1Arr = newStirlings;
	    			}
	    		}
			}
			
            if (stirling1Arr[n][k] == null) {
            	// Here is the recursion !!
                stirling1Arr[n][k] = stirling1WithMemory(n-1, k-1).subtract(stirling1WithMemory(n-1, k).multiply(BigInteger.valueOf(n-1)));
                if (DEBUG) LOG.debug("s(" + n + ", " + k + ") = " + stirling1Arr[n][k]);
            }

            // Get the absolute Stirling number from the internal array:
            return stirling1Arr[n][k];
    	}
        if (n==k) {
        	return I_1;
        }
	    // k==0, n>0:
        return I_0;
	}
	
    /**
     * Fast version of the 1. kind Stirling numbers with hashed memory.
     * OutOfMemoryError at n=849.
     */
	static BigInteger stirling1WithHashedMemory(int n, int k) {
    	if (n>k && k>0) {
        	Pair<Integer, Integer> nkp = new Pair<Integer, Integer>(n, k);
            BigInteger retValue = s1Map.get(nkp);
            if (retValue == null) {
                // recursion:
                retValue = stirling1WithHashedMemory(n-1, k-1).subtract(stirling1WithHashedMemory(n-1, k).multiply(BigInteger.valueOf(n-1)));
                s1Map.put(nkp, retValue);
            }
            return(retValue);
    	}
        if (n==k) {
            return I_1;
        }
        return I_0;
    }

    /**
     * Stirling numbers of the second kind S(n,k).
     * @see <a href="http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html">http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html</a>
     * 
     * @param n
     * @param k
     * @return BigInt
     */
    public static BigInteger stirling2(int n, int k) {
    	BigInteger ret = I_0;
    	if (k>0) {
	    	for (int i=0; i<=n; i++) {
	    		BigInteger elem = Binomial.binomial(k, i).multiply(BigInteger.valueOf(k-i).pow(n));
	    		if (i%2 != 0) {
	    			elem = elem.negate();
	    		}
	    		ret = ret.add(elem);
	    	}
	    	return ret.divide(Factorial.factorial(k));
    	}
    	if (k==0) { return I_0; }
    	throw new IllegalArgumentException("Parameter k must be non-negative, but is " + k);
    }

	/**
	 * Compute r-Stirling numbers of the first kind s(n,k,r)
	 * @param n Upper parameter
	 * @param k Lower parameter
	 * @param r r-value
	 * @return s(n,k,r)
	 */
    public static BigInteger rStirling1(int n, int k, int r) {
    	if (n>r) {
    		return rStirling1(n-1,k-1,r).add(rStirling1(n-1,k,r).multiply(BigInteger.valueOf(n-1)));
    	}
    	if (n==r && k==r) {
    		return I_1;
    	}
    	return I_0;
	}

    /**
     * Calculates the diagonal of Stirling numbers of the first kind
     * S1(n-k+1,1), S1(n-k+2,2), ..., S1(n-1,k-1), S1(n,k).
     * 
     * @param n
     * @param k
     * @return an array containing the Stirling numbers of the diagonal
     */
    public static BigInteger[] stirling1Diag(int n, int k) {
        // prepare initial diagonal S1(1,1), ... S1(k,k):
        BigInteger[] diag = new BigInteger[k];
        for (int i=1; i<=k; i++)
            diag[i-1] = I_1;

        // progression until n:
        BigInteger[] nextDiag;
        for (int n1=1; n1<=n-k; n1++) {
            nextDiag = nextStirling1Diag(diag, n1, k);
            diag = nextDiag;
        }

        return diag;
    }
    
    public static BigInteger[] nextStirling1Diag(BigInteger[] lastDiag, int n1, int k) {
        BigInteger[] nextDiag = new BigInteger[k];
        nextDiag[0] = lastDiag[0].multiply(BigInteger.valueOf(n1));

        for (int i=2; i<=k; i++) {
            nextDiag[i-1] = lastDiag[i-1].multiply(BigInteger.valueOf(n1+i-1));
            nextDiag[i-1] = nextDiag[i-1].add(nextDiag[i-2]);
        }
        return nextDiag;
    }
}
