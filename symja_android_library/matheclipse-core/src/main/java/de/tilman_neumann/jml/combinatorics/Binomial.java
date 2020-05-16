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
package de.tilman_neumann.jml.combinatorics;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.BigIntGrid;
import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Implementation of the binomial coefficient.
 * @author Tilman Neumann
 */
public class Binomial {
	private static final Logger LOG = Logger.getLogger(Binomial.class);
	
    /**
     * Returns the binomial coefficient C(n, k). Works for negative n,k, too.
	 * 
     * @param n
     * @param k
     * @return binomial coefficient C(n, k)
     */
    public static final BigInteger binomial(int n, int k) {
    	if (k==0 || n==k) return I_1; // holds for negative n, k, too

    	if (n>=0) {
    		if (k<0 || k>n) return I_0;
    		
    		// standard case C(n, k) with n>k>0
    		return core(n, k);
    	}

    	// now treat n<0
    	if (k>0) {
    		BigInteger C = core(k-n-1, k);
    		if ((k&1)==1) C = C.negate();
    		return C;
    	}
    	
    	// n, k < 0
    	if (k>n) return I_0;
		BigInteger C = core(-k-1, -n-1);
		if ((Math.abs(n-k)&1)==1) C = C.negate();
		return C;

    }
    
    /**
     * Computes the binomial coefficient C(n, k) for n >= k >= 0.
     * Applies "early fraction reduction".
     * 
	 * Adapted from http://www.jonelo.de by Johann Nepomuk Loefflmann (jonelo@jonelo.de),
	 * published under GNU General Public License.
	 * 
     * @param n
     * @param k
     * @return binomial coefficient C(n, k)
     */
    private static final BigInteger core(int n, int k) {
    	// C(n, k) == C(n, n-k); the smaller choice of k means less iterations in the loop below 
    	if (k<<1 > n) k = n-k;
    	
        // initialize with 1. factor in numerator
        BigInteger num = BigInteger.valueOf(n-k+1);
        BigInteger den = I_1;

        BigInteger result = I_1;
        for (int i=0; i<k; i++) {
            result = result.multiply(num).divide(den);
            num=num.add(I_1);
            den=den.add(I_1);
        }
        return result;
    }
    
    /**
     * Test
     * @param args ignored
     */
    public static void main(String[] args) {
    	ConfigUtil.initProject();
    	
    	int max = 10;
    	BigIntGrid grid = new BigIntGrid("n", -max, "k", -max); // works for negative k, too
    	for (int n=-max; n<=max; n++) {
    		ArrayList<BigInteger> row = new ArrayList<>();
        	for (int k=-max; k<=max; k++) {
        		row.add(binomial(n,k));
        	}
        	grid.add(row);
    	}
    	LOG.info("binomial coefficients:\n" + grid);
    }
}
