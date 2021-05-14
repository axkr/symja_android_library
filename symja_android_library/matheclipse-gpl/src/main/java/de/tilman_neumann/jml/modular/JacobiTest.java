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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Test of Legendre and Jacobi symbol.
 * 
 * Result: Jacobi is always faster than Eulers formula!
 * 
 * @author Tilman Neumann
 */
public class JacobiTest {
	private static final Logger LOG = Logger.getLogger(JacobiTest.class);
	private static final boolean TEST_SLOW = false;
	
	private static final int NCOUNT_CORRECTNESS = 200;
	private static final int NCOUNT_PERFORMANCE = 1000;
	private static final int MAX_BITS = 500;
	
	private static final BPSWTest bpsw = new BPSWTest();
	
	private static void testCorrectness() {
		LegendreSymbol legendreEngine = new LegendreSymbol();
		JacobiSymbol jacobiEngine = new JacobiSymbol();
		SecureRandom rng = new SecureRandom();
		ArrayList<Integer> aList_int=null, pList_int=null;
		for (int bits=10; bits<100; bits+=10) {
			// generate test numbers
			ArrayList<BigInteger> aList = new ArrayList<BigInteger>();
			ArrayList<BigInteger> pList = new ArrayList<BigInteger>();
			for (int i=0; i<NCOUNT_CORRECTNESS; i++) {
				aList.add(new BigInteger(bits, rng));
			}
			int i=0;
			while (i<NCOUNT_CORRECTNESS) {
				// the p must be odd, and to allow comparison with the Legendre symbol it should be odd primes
				BigInteger p = bpsw.nextProbablePrime(new BigInteger(bits, rng));
				if (p.and(I_1).intValue()==1) {
					pList.add(p);
					i++;
				}
			}
			
			if (bits <31) {
				aList_int = new ArrayList<Integer>();
				for (BigInteger a : aList) aList_int.add(a.intValue());
				pList_int = new ArrayList<Integer>();
				for (BigInteger p : pList) pList_int.add(p.intValue());
			}
			
			LOG.info("test Legendre(a|p) and Jacobi(a|p) for test numbers with " + bits + " bits");
			
			Iterator<Integer> aIntIter = aList_int.iterator();
			for (BigInteger a : aList) {
				int aInt = aIntIter.next();
				Iterator<Integer> pIntIter = pList_int.iterator();
				for (BigInteger p : pList) {
					int pInt = pIntIter.next();
					// test big argument formulas
					int correct = jacobiEngine.jacobiSymbol_v01(a, p);
					int jacobi02 = jacobiEngine.jacobiSymbol_v02(a, p);
					int jacobi03 = jacobiEngine.jacobiSymbol/*_v03*/(a, p);
					int legendre = legendreEngine.EulerFormula(a, p);
//					assertEquals(correct, jacobi02);
//					assertEquals(correct, jacobi03);
//					assertEquals(correct, legendre);
					// test formulas with big a, int p (p and pInt will be different for bits>30)
					correct = jacobiEngine.jacobiSymbol_v01(a, BigInteger.valueOf(pInt));
					jacobi03 = jacobiEngine.jacobiSymbol/*_v03*/(a, pInt);
					legendre = legendreEngine.EulerFormula(a, pInt);
//					assertEquals(correct, jacobi03);
//					assertEquals(correct, legendre);
					// test formulas with int a, big p (a and aInt will be different for bits>30)
					correct = jacobiEngine.jacobiSymbol_v01(BigInteger.valueOf(aInt), p);
					jacobi03 = jacobiEngine.jacobiSymbol/*_v03*/(aInt, p);
//					assertEquals(correct, jacobi03);
					// test formulas with all int arguments (a, aInt will differ as well as p, pInt for bits>30)
					correct = jacobiEngine.jacobiSymbol_v01(BigInteger.valueOf(aInt), BigInteger.valueOf(pInt));
					jacobi03 = jacobiEngine.jacobiSymbol/*_v03*/(aInt, pInt);
					legendre = legendreEngine.EulerFormula(aInt, pInt);
//					assertEquals(correct, jacobi03);
//					assertEquals(correct, legendre);
				}
			}
		}
	}
	
	private static void testPerformance() {
		SecureRandom rng = new SecureRandom();
		ArrayList<Integer> aList_int=null, pList_int=null;
		for (int bits=10; bits<MAX_BITS; bits+=10) {
			// generate test numbers
			ArrayList<BigInteger> aList = new ArrayList<BigInteger>();
			ArrayList<BigInteger> pList = new ArrayList<BigInteger>();
			for (int i=0; i<NCOUNT_PERFORMANCE; i++) {
				aList.add(new BigInteger(bits, rng));
			}
			int i=0;
			while (i<NCOUNT_PERFORMANCE) {
				// the p must be odd, and to allow comparison with the Legendre symbol it should be odd primes
				BigInteger p = bpsw.nextProbablePrime(new BigInteger(bits, rng));
				if (p.and(I_1).intValue()==1) {
					pList.add(p);
					i++;
				}
			}
			
			LOG.info("test Legendre(a|p) and Jacobi(a|p) for test numbers with " + bits + " bits");
			LegendreSymbol legendreEngine = new LegendreSymbol();
			JacobiSymbol jacobiEngine = new JacobiSymbol();
			long t0;
			
			if (TEST_SLOW) {
				t0 = System.currentTimeMillis();
				for (BigInteger a : aList) {
					for (BigInteger p : pList) {
						jacobiEngine.jacobiSymbol_v01(a, p);
					}
				}
				LOG.info("    Jacobi 01 implementation took " + (System.currentTimeMillis()-t0) + " ms");
				
				t0 = System.currentTimeMillis();
				for (BigInteger a : aList) {
					for (BigInteger p : pList) {
						jacobiEngine.jacobiSymbol_v02(a, p);
					}
				}
				LOG.info("    Jacobi 02 implementation took " + (System.currentTimeMillis()-t0) + " ms");
				
				t0 = System.currentTimeMillis();
				for (BigInteger a : aList) {
					for (BigInteger p : pList) {
						jacobiEngine.jacobiSymbol/*_v03*/(a, p);
					}
				}
				LOG.info("    Jacobi 03 implementation took " + (System.currentTimeMillis()-t0) + " ms");
				
				t0 = System.currentTimeMillis();
				for (BigInteger a : aList) {
					for (BigInteger p : pList) {
						legendreEngine.EulerFormula(a, p);
					}
				}
				LOG.info("    Legendre(BigInt, BigInt) took " + (System.currentTimeMillis()-t0) + " ms");
			}
		
			if (bits<31) {
				// test signatures with integers, too
				aList_int = new ArrayList<Integer>();
				for (BigInteger a : aList) aList_int.add(a.intValue());
				pList_int = new ArrayList<Integer>();
				for (BigInteger p : pList) pList_int.add(p.intValue());

				t0 = System.currentTimeMillis();
				for (int a : aList_int) {
					for (int p : pList_int) {
						jacobiEngine.jacobiSymbol/*_v03*/(a, p);
					}
				}
				LOG.info("    Jacobi03(int, int) took " + (System.currentTimeMillis()-t0) + " ms");
				
				t0 = System.currentTimeMillis();
				for (int a : aList_int) {
					for (int p : pList_int) {
						legendreEngine.EulerFormula(a, p);
					}
				}
				LOG.info("    Legendre(int, int) took " + (System.currentTimeMillis()-t0) + " ms");
			}

			t0 = System.currentTimeMillis();
			for (BigInteger a : aList) {
				for (int p : pList_int) {
					jacobiEngine.jacobiSymbol/*_v03*/(a, p);
				}
			}
			LOG.info("    Jacobi03(BigInt, int) took " + (System.currentTimeMillis()-t0) + " ms");

			t0 = System.currentTimeMillis();
			for (int a : aList_int) {
				for (BigInteger p : pList) {
					jacobiEngine.jacobiSymbol/*_v03*/(a, p);
				}
			}
			LOG.info("    Jacobi03(int, BigInt) took " + (System.currentTimeMillis()-t0) + " ms");

			t0 = System.currentTimeMillis();
			for (BigInteger a : aList) {
				for (int p : pList_int) {
					legendreEngine.EulerFormula(a, p);
				}
			}
			LOG.info("    Legendre(BigInt, int) took " + (System.currentTimeMillis()-t0) + " ms");
		}
	}
	
	/**
	 * Test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ConfigUtil.initProject();
		testCorrectness();
		testPerformance();
	}
}
