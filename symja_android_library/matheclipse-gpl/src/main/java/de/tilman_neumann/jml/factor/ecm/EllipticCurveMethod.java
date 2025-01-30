/*
 * Elliptic Curve Method (ECM) Prime Factorization
 *
 * Written by Dario Alejandro Alpern (Buenos Aires - Argentina)
 * Based in Yuji Kida's implementation for UBASIC interpreter.
 * Some code "borrowed" from Paul Zimmermann's ECM4C.
 * Modified for the Symja project by Axel Kramer.
 * Further refactorings by Tilman Neumann.
 * 
 * Big thanks to Dario Alpern for his permission to use this piece of software under the GPL3 license.
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
package de.tilman_neumann.jml.factor.ecm;

import java.math.BigInteger;
import java.util.SortedMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.factor.tdiv.TDiv;
import de.tilman_neumann.jml.powers.PurePowerTest;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.primes.probable.PrPTest;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * <p>Use Elliptic Curve Method to find the prime number factors of a given BigInteger.</p>
 *
 * @see [Le] <a href="https://en.wikipedia.org/wiki/Lenstra_elliptic_curve_factorization"> Wikipedia: Lenstra elliptic curve factorization </a>
 * @see [CP] Richard Crandall, Carl Pomerance: "Prime Numbers: A Computational Perspective", Second Edition, chapter 7.4
 */
public class EllipticCurveMethod extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(EllipticCurveMethod.class);

	private static final boolean DEBUG = false;
	
	static final int NLen = 1200;
	
	private static final long DosALa32 = 1L << 32;
	private static final long DosALa31 = 1L << 31;
	private static final long DosALa62 = 1L << 62;
	private static final double dDosALa31 = DosALa31;
	private static final double dDosALa62 = dDosALa31 * dDosALa31;

	/** 1 as "BigNbr" */
	private static final int BigNbr1[] = new int[NLen];
	
	/** Primes < 5000 */
	private static final int SmallPrime[] = new int[670]; // p_669 = 4999;

	private static final double v[] = {
			1.61803398875, 1.72360679775, 1.618347119656, 1.617914406529, 1.612429949509,
			1.632839806089, 1.620181980807, 1.580178728295, 1.617214616534, 1.38196601125
	};
	
	private static final int ADD = 6; // number of multiplications in an addition
	private static final int DUP = 5; //number of multiplications in a duplicate

	private static final PrPTest prp = new PrPTest();
	private static final PurePowerTest powerTest = new PurePowerTest();
	private static final TDiv tdiv = new TDiv().setTestLimit(131072);

	/** input N as a BigNbr */
	private final int TestNbr[] = new int[NLen];

	/** Length of multiple precision numbers. */
	int NumberLength;
	
	/** the maximum number of curves to run. -1 means no limit, 0 automatic computation of the parameter, positive values are applied directly */
	private int maxCurves;
	
	/** Elliptic curve counter */
	private int EC;
	
	private MontgomeryMult montgomery;

	// big numbers used in gcd calculation
	private final int[] CalcAuxGcdU = new int[NLen];
	private final int[] CalcAuxGcdV = new int[NLen];
	private final int[] CalcAuxGcdT = new int[NLen];
	private final int[] GcdAccumulated = new int[NLen];
	
	// arrays used in modInverse calculation
	private final long[] B = new long[NLen];
	private final long[] CalcAuxModInvA = new long[NLen];
	private final long[] CalcAuxModInvB = new long[NLen];
	private final long[] CalcAuxModInvMu = new long[NLen];
	private final long[] CalcAuxModInvGamma = new long[NLen];

	// other arrays with unique usage
	private final int[] A0 = new int[NLen];
	private final int[] A02 = new int[NLen];
	private final int[] A03 = new int[NLen];
	private final int[] bZi = new int[NLen];
	private final int[] DX = new int[NLen];
	private final int[] DZ = new int[NLen];
	private final int[] GD = new int[NLen];
	private final int[] M = new int[NLen];
	private final int[] W1 = new int[NLen];
	private final int[] W2 = new int[NLen];
	private final int[] W3 = new int[NLen];
	private final int[] W4 = new int[NLen];
	private final int[] WX = new int[NLen];
	private final int[] WZ = new int[NLen];
	private final int[] X = new int[NLen];
	private final int[] Z = new int[NLen];
	private final int[] Xaux = new int[NLen];
	private final int[] Zaux = new int[NLen];
	private final int[][] root = new int[480][NLen];
	private final byte[] sieve = new byte[23100];
	private final byte[] sieve2310 = new byte[2310];
	private final int[] sieveidx = new int[480];

	// other arrays with multiple usage
	private final int[] fieldTX = new int[NLen];
	private final int[] fieldTZ = new int[NLen];
	private final int[] fieldUX = new int[NLen];
	private final int[] fieldUZ = new int[NLen];
	private final int[] fieldAux1 = new int[NLen];
	private final int[] fieldAux2 = new int[NLen];
	private final int[] fieldAux3 = new int[NLen];
	private final int[] fieldAux4 = new int[NLen];

	static {
		BigNbr1[0] = 1;
		for (int i = 1; i < NLen; i++) {
			BigNbr1[i] = 0;
		}
		
		final AutoExpandingPrimesArray autoPrimes = AutoExpandingPrimesArray.get().ensureLimit(5000);
		SmallPrime[0] = 2;
		for (int indexM = 1; indexM < SmallPrime.length; indexM++) {
			SmallPrime[indexM] = autoPrimes.getPrime(indexM);
		}
	}

	/**
	 * Full constructor.
	 * @param maxCurves the maximum number of curves to run.
	 * -1 means no limit, 0 automatic computation of the parameter, positive values are applied directly.
	 */
	public EllipticCurveMethod(int maxCurves) {
		this.maxCurves = maxCurves;
	}
	
	@Override
	public String getName() {
		return "ECM(maxCurves = " + maxCurves + ")";
	}
	
	@Override
	public void factor(BigInteger N, SortedMultiset<BigInteger> primeFactors) {
		FactorArguments args = new FactorArguments(N, 1);
		SortedMultiset<BigInteger> compositeFactors = new SortedMultiset_BottomUp<BigInteger>();
		FactorResult result = new FactorResult(primeFactors, new SortedMultiset_BottomUp<BigInteger>(), compositeFactors, 2);
		searchFactors(args, result);
		primeFactors.addAll(compositeFactors); // in case of maxCurves != -1 we can not guarantee that all prime factors are found
	}

	/**
	 * Find small factors of some N. Returns found factors in <code>result.primeFactors</code> and eventually some
	 * unfactored composites in <code>result.compositeFactors</code>.
	 * 
	 * @param args
	 * @param result the result of the factoring attempt. Should be initialized only once by the caller to reduce overhead.
	 */
	public void searchFactors(FactorArguments args, FactorResult result) {
		// Do trial division by all primes < 131072. This is required before doing ECM to prevent that Montgomery multiplication
		// is called with 1-word arguments. Found factors are added to result.primeFactors, the rest to result.untestedFactors.
		tdiv.searchFactors(args, result);
		if (result.untestedFactors.isEmpty()) return;
		// Otherwise untestedFactors should contain exactly one prime > 131072, or one composite > 131072^2, the unfactored rest
		if (DEBUG) {
			Ensure.ensureEquals(1, result.untestedFactors.size());
			Ensure.ensureGreater(result.untestedFactors.firstKey(), BigInteger.valueOf(131072));
		}
		
		// Retrieve the unfactored rest to continue. The exponent of N can not have changed.
		BigInteger N = result.untestedFactors.firstKey();
		int Nexp = result.untestedFactors.removeAll(N);
		if (DEBUG) Ensure.ensureEquals(args.exp, Nexp);
		
		if (isProbablePrime(N)) {
			addToMap(N, args.exp, result.primeFactors);
			return;
		}
		
		// N is composite -> do ECM
		SortedMultiset<BigInteger> compositesToTest = result.compositeFactors;
		compositesToTest.add(N, args.exp); // we know that N is composite
		
		// here we store all composites ECM failed to factor
		SortedMultiset<BigInteger> failedComposites = new SortedMultiset_BottomUp<BigInteger>();

		// The elliptic curve counter should not be reset to 0 inside fnECM() so that curves do not get tested twice, no matter how many factors N has.
		EC = 0;
		
		while (!compositesToTest.isEmpty()) {
			// get next composite to test
			N = compositesToTest.firstKey();
			int exp = compositesToTest.removeAll(N);
			
			// pure power?
			PurePowerTest.Result r = powerTest.test(N);
			if (r != null) {
				// N is a pure power!
				addToMapDependingOnPrimeTest(r.base, exp*r.exponent, result.primeFactors, compositesToTest);
				continue; // test next composite
			}

			// ECM
			int maxCurvesForN = maxCurves!= 0 ? maxCurves : computeMaxCurvesForN(N);
			final BigInteger NN = fnECM(N, maxCurvesForN);
			if (NN.equals(I_1)) {
				// N is composite but could not be factored by ECM
				addToMap(N, exp, failedComposites);
				continue;
			}
			// NN is a factor of N
			addToMapDependingOnPrimeTest(NN, exp, result.primeFactors, compositesToTest);
			addToMapDependingOnPrimeTest(N.divide(NN), exp, result.primeFactors, compositesToTest);
		}
		
		// Before we finish, add all composites that could not be factored to result.compositeFactors.
		// We want the product of result factors to match N, no matter what happened.
		compositesToTest.addAll(failedComposites);
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		// TODO We'ld need trial division up to 131072 before doing ECM to prevent that Montgomery multiplication is called with 1-word arguments
		
		EC = 0;

		int maxCurvesForN = maxCurves!= 0 ? maxCurves : computeMaxCurvesForN(N);
		return fnECM(N, maxCurvesForN);
	}

	private static boolean isProbablePrime(BigInteger N) {
		// XXX The 33-bit "guard" is only safe if we did tdiv for all p <= sqrt(2^33) before
		return N.bitLength() <= 33 || prp.isProbablePrime(N);
	}

	private static void addToMapDependingOnPrimeTest(BigInteger factor, int exp, SortedMap<BigInteger, Integer> primeFactors, SortedMap<BigInteger, Integer> compositeFactors) {
		addToMap(factor, exp, isProbablePrime(factor) ? primeFactors : compositeFactors);
	}
	
	private static void addToMap(BigInteger N, int exp, SortedMap<BigInteger, Integer> map) {
		Integer oldExp = map.get(N);
		// old entry is replaced if oldExp!=null
		map.put(N, (oldExp == null) ? exp : oldExp+exp);
	}

	public static int computeMaxCurvesForN(BigInteger N) {
		int NBits = N.bitLength();
		// Dario Alpern's choice of (decimal digits -> maxCurves) was:
		// 30->5, 35->8, 40->15, 45->25, 50->27, 55->32, 60->43, 65->70, 70->150, 75->300, 80->350, 85->600, 90->1500
		// For N > 90 decimal digits the number of curves to run was unlimited, so his SIQS would never be called.
		// The following re-estimate seems to work quite fine for any number of PSIQS threads. It is rather cautious because hitting hard semi-primes
		// shall not let ECM waste more time than SIQS or PSIQS would need. Nonetheless, these few curves speed up factoring random composites a lot.
		int maxCurvesForN = NBits>130 ? (int) Math.pow((NBits-130)/15, 1.61) : 0;
		if (DEBUG) LOG.debug("ECM: NBits = " + NBits + ", maxCurvesForN = " + maxCurvesForN);
		return maxCurvesForN;
	}
	
	private BigInteger fnECM(BigInteger N, int maxCurvesForN) {
		int[] TX = fieldTX;
		int[] TZ = fieldTZ;
		int[] UX = fieldUX;
		int[] UZ = fieldUZ;
		int[] Aux1 = fieldAux1;
		int[] Aux2 = fieldAux2;
		int[] Aux3 = fieldAux3;
		
		// Compute NumberLength, the number of ints in which all computations are carried out
		this.NumberLength = computeNumberLength(N.toByteArray().length * 8);
		// Convert BigInteger N into TestNbr, a representation in NumberLength 31-bit integers
		BigNbrToBigInt(N, TestNbr, NumberLength);
		
		// set up Montgomery multiplication
		this.montgomery = new MontgomeryMult(TestNbr, NumberLength);
		
		// More initializations
		double dN = TestNbr[NumberLength - 1];
		if (NumberLength > 1) {
			dN += TestNbr[NumberLength - 2] / dDosALa31;
		}
		if (NumberLength > 2) {
			dN += TestNbr[NumberLength - 3] / dDosALa62;
		}

		final int MontgomeryMultR1[] = new int[NLen];
		final int MontgomeryMultR2[] = new int[NLen];
		final int MontgomeryMultAfterInv[] = new int[NLen];

		MontgomeryMultR1[NumberLength] = 1;
		for (int jj=NumberLength-1; jj>=0; jj--) {
			MontgomeryMultR1[jj] = 0;
		}
		
		AdjustModN(MontgomeryMultR1, dN);
		MultBigNbrModN(MontgomeryMultR1, MontgomeryMultR1, MontgomeryMultR2, dN);
		montgomery.mul(MontgomeryMultR2, MontgomeryMultR2, MontgomeryMultAfterInv);
		AddBigNbrModN(MontgomeryMultR1, MontgomeryMultR1, MontgomeryMultR2);

		// Modular curve loop:
		while (maxCurvesForN==-1 || EC < maxCurvesForN) { // maxCurvesForN==-1 means "run until a factor is found"
			EC++;

			long L1; // step 1 prime bound
			// The original estimate are the standard values for searching 15, 25, 35, 45-digit factors, see e.g.
			// https://www.rieselprime.de/ziki/Elliptic_curve_method#Choosing_the_best_parameters_for_ECM
			if (EC < 26) L1 = 2000;
			else if (EC < 326) L1 = 50000;
			else if (EC < 2000) L1 = 1000000; 
			else L1 = 11000000;

			long L2 = 100 * L1; // step 2 prime bound
			long LS = (long) Math.ceil(Math.sqrt(L1));

			// [Le] Pick a random elliptic curve over Z_N, with equation of the form y^2 = x^3 + ax + b (mod N) together with a
			// non-trivial point P(x0, y0) on it. This can be done by first picking random x0, y0, a ∈ Z_N, and then setting
			// b = y0^2 − x0^3 − a*x0 (mod N) to assure the point is on the curve.
			LongToBigNbr(2 * (EC + 1), Aux1);
			LongToBigNbr(3 * (EC + 1) * (EC + 1) - 1, Aux2);
			ModInvBigNbr(Aux2, TestNbr, Aux2);
			MultBigNbrModN(Aux1, Aux2, Aux3, dN);
			MultBigNbrModN(Aux3, MontgomeryMultR1, A0, dN);
			montgomery.mul(A0, A0, A02);
			montgomery.mul(A02, A0, A03);
			SubtractBigNbrModN(A03, A0, Aux1);
			MultBigNbrByLongModN(A02, 9, Aux2, dN);
			SubtractBigNbrModN(Aux2, MontgomeryMultR1, Aux2);
			montgomery.mul(Aux1, Aux2, Aux3);
			if (BigNbrIsZero(Aux3)) {
				continue;
			}
			MultBigNbrByLongModN(A0, 4, Z, dN);
			MultBigNbrByLongModN(A02, 3, Aux1, dN);
			AddBigNbrModN(Aux1, MontgomeryMultR1, X);
			// now we have determined the point P(X, Z)
			
			// compute Zimmermann's a,b parameters // XXX are these the curve parameters ?
			MultBigNbrByLongModN(A02, 6, Aux1, dN);
			SubtractBigNbrModN(MontgomeryMultR1, Aux1, Aux1);
			montgomery.mul(A02, A02, Aux2);
			MultBigNbrByLongModN(Aux2, 3, Aux2, dN);
			SubtractBigNbrModN(Aux1, Aux2, Aux1);
			MultBigNbrByLongModN(A03, 4, Aux2, dN);
			ModInvBigNbr(Aux2, TestNbr, Aux2);
			montgomery.mul(Aux2, MontgomeryMultAfterInv, Aux3);
			montgomery.mul(Aux1, Aux3, A0); // Zimmermann's a
			AddBigNbrModN(A0, MontgomeryMultR2, Aux1);
			LongToBigNbr(4, Aux2);
			ModInvBigNbr(Aux2, TestNbr, Aux3);
			MultBigNbrModN(Aux3, MontgomeryMultR1, Aux2, dN);
			montgomery.mul(Aux1, Aux2, bZi); // Zimmermann's b = (a+2)/4 mod N (fixed from now on)
			
			/**************/
			/* First step */
			/**************/
			System.arraycopy(X, 0, Xaux, 0, NumberLength);
			System.arraycopy(Z, 0, Zaux, 0, NumberLength);
			System.arraycopy(MontgomeryMultR1, 0, GcdAccumulated, 0, NumberLength);
			for (int Pass = 0; Pass < 2; Pass++) {
				/* For powers of 2 */
				for (int I = 1; I <= L1; I <<= 1) {
					duplicate(X, Z, X, Z, bZi);
				}
				for (int I = 3; I <= L1; I *= 3) {
					duplicate(W1, W2, X, Z, bZi);
					add3(X, Z, X, Z, W1, W2, X, Z);
				}

				if (Pass == 0) {
					montgomery.mul(GcdAccumulated, Z, Aux1);
					System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
				} else {
					GcdBigNbr(Z, TestNbr, GD);
					if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
						return BigIntToBigNbr(GD); // found factor, exit
					}
				}

				/* for powers of odd primes */
				long P;
				int indexM = 1;
				do {
					P = SmallPrime[indexM++];
					for (long IP = P; IP <= L1; IP *= P) {
						prac((int) P, X, Z, W1, W2, W3, W4, bZi);
					}
					if (Pass == 0) {
						montgomery.mul(GcdAccumulated, Z, Aux1);
						System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
					} else {
						GcdBigNbr(Z, TestNbr, GD);
						if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
							return BigIntToBigNbr(GD); // found factor, exit
						}
					}
				} while (P <= LS);
				P += 2;

				/* Initialize sieve2310[n]: 1 if gcd(P+2n,2310) > 1, 0 otherwise */
				int u = (int) P;
				for (int i = 0; i < 2310; i++) {
					sieve2310[i] = (u % 3 == 0 || u % 5 == 0 || u % 7 == 0 || u % 11 == 0 ? (byte) 1 : (byte) 0);
					u += 2;
				}
				do {
					/* Generate sieve */
					GenerateSieve((int) P, sieve, sieve2310, SmallPrime);

					/* Walk through sieve */
					for (int i = 0; i < 23100; i++) {
						if (sieve[i] != 0)
							continue; /* Do not process composites */
						if (P + 2 * i > L1)
							break;
						prac((int) (P + 2 * i), X, Z, W1, W2, W3, W4, bZi);
						if (Pass == 0) {
							montgomery.mul(GcdAccumulated, Z, Aux1);
							System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
						} else {
							GcdBigNbr(Z, TestNbr, GD);
							if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
								return BigIntToBigNbr(GD); // found factor, exit
							}
						}
					}
					P += 46200;
				} while (P < L1);
				if (Pass == 0) {
					if (BigNbrIsZero(GcdAccumulated)) { // If GcdAccumulated is...
						System.arraycopy(Xaux, 0, X, 0, NumberLength);
						System.arraycopy(Zaux, 0, Z, 0, NumberLength);
						continue; // ... a multiple of TestNbr, continue.
					}
					GcdBigNbr(GcdAccumulated, TestNbr, GD);
					if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
						return BigIntToBigNbr(GD); // found factor, exit
					}
					break;
				}
			} /* end for Pass */

			/******************************************************/
			/* Second step (using improved standard continuation) */
			/******************************************************/
			int j = 0;
			for (int u = 1; u < 2310; u += 2) {
				if (u % 3 == 0 || u % 5 == 0 || u % 7 == 0 || u % 11 == 0) {
					sieve2310[u / 2] = (byte) 1;
				} else {
					sieve2310[(sieveidx[j++] = u / 2)] = (byte) 0;
				}
			}
			System.arraycopy(sieve2310, 0, sieve2310, 1155, 1155);
			System.arraycopy(X, 0, Xaux, 0, NumberLength);
			System.arraycopy(Z, 0, Zaux, 0, NumberLength); // (X:Z) -> Q (output from step 1)
			
			for (int Pass = 0; Pass < 2; Pass++) {
				int J = 0;
				System.arraycopy(MontgomeryMultR1, 0, GcdAccumulated, 0, NumberLength);
				System.arraycopy(X, 0, UX, 0, NumberLength);
				System.arraycopy(Z, 0, UZ, 0, NumberLength); // (UX:UZ) -> Q
				ModInvBigNbr(Z, TestNbr, Aux2);
				montgomery.mul(Aux2, MontgomeryMultAfterInv, Aux1);
				montgomery.mul(Aux1, X, root[0]); // root[0] <- X/Z (Q)
				AddBigNbrModN(X, Z, Aux1);
				montgomery.mul(Aux1, Aux1, W1);
				SubtractBigNbrModN(X, Z, Aux1);
				montgomery.mul(Aux1, Aux1, W2);
				montgomery.mul(W1, W2, TX);
				SubtractBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, bZi, Aux2);
				AddBigNbrModN(Aux2, W2, Aux3);
				montgomery.mul(Aux1, Aux3, TZ); // (TX:TZ) -> 2Q
				SubtractBigNbrModN(X, Z, Aux1);
				AddBigNbrModN(TX, TZ, Aux2);
				montgomery.mul(Aux1, Aux2, W1);
				AddBigNbrModN(X, Z, Aux1);
				SubtractBigNbrModN(TX, TZ, Aux2);
				montgomery.mul(Aux1, Aux2, W2);
				AddBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, Aux1, Aux2);
				montgomery.mul(Aux2, UZ, X);
				SubtractBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, Aux1, Aux2);
				montgomery.mul(Aux2, UX, Z); // (X:Z) -> 3Q
				for (int I = 5; I < 2310; I += 2) {
					System.arraycopy(X, 0, WX, 0, NumberLength);
					System.arraycopy(Z, 0, WZ, 0, NumberLength);
					SubtractBigNbrModN(X, Z, Aux1);
					AddBigNbrModN(TX, TZ, Aux2);
					montgomery.mul(Aux1, Aux2, W1);
					AddBigNbrModN(X, Z, Aux1);
					SubtractBigNbrModN(TX, TZ, Aux2);
					montgomery.mul(Aux1, Aux2, W2);
					AddBigNbrModN(W1, W2, Aux1);
					montgomery.mul(Aux1, Aux1, Aux2);
					montgomery.mul(Aux2, UZ, X);
					SubtractBigNbrModN(W1, W2, Aux1);
					montgomery.mul(Aux1, Aux1, Aux2);
					montgomery.mul(Aux2, UX, Z); // (X:Z) -> 5Q, 7Q, ...
					if (Pass == 0) {
						montgomery.mul(GcdAccumulated, Aux1, Aux2);
						System.arraycopy(Aux2, 0, GcdAccumulated, 0, NumberLength);
					} else {
						GcdBigNbr(Aux1, TestNbr, GD);
						if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
							return BigIntToBigNbr(GD); // found factor, exit
						}
					}
					if (I == 1155) {
						System.arraycopy(X, 0, DX, 0, NumberLength);
						System.arraycopy(Z, 0, DZ, 0, NumberLength); // (DX:DZ) -> 1155Q
					}
					if (I % 3 != 0 && I % 5 != 0 && I % 7 != 0 && I % 11 != 0) {
						J++;
						ModInvBigNbr(Z, TestNbr, Aux2);
						montgomery.mul(Aux2, MontgomeryMultAfterInv, Aux1);
						montgomery.mul(Aux1, X, root[J]); // root[J] <- X/Z
					}
					System.arraycopy(WX, 0, UX, 0, NumberLength);
					System.arraycopy(WZ, 0, UZ, 0, NumberLength); // (UX:UZ) <- Previous (X:Z)
				} /* end for I */
				AddBigNbrModN(DX, DZ, Aux1);
				montgomery.mul(Aux1, Aux1, W1);
				SubtractBigNbrModN(DX, DZ, Aux1);
				montgomery.mul(Aux1, Aux1, W2);
				montgomery.mul(W1, W2, X);
				SubtractBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, bZi, Aux2);
				AddBigNbrModN(Aux2, W2, Aux3);
				montgomery.mul(Aux1, Aux3, Z);
				System.arraycopy(X, 0, UX, 0, NumberLength);
				System.arraycopy(Z, 0, UZ, 0, NumberLength); // (UX:UZ) -> 2310Q
				AddBigNbrModN(X, Z, Aux1);
				montgomery.mul(Aux1, Aux1, W1);
				SubtractBigNbrModN(X, Z, Aux1);
				montgomery.mul(Aux1, Aux1, W2);
				montgomery.mul(W1, W2, TX);
				SubtractBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, bZi, Aux2);
				AddBigNbrModN(Aux2, W2, Aux3);
				montgomery.mul(Aux1, Aux3, TZ); // (TX:TZ) -> 2*2310Q
				SubtractBigNbrModN(X, Z, Aux1);
				AddBigNbrModN(TX, TZ, Aux2);
				montgomery.mul(Aux1, Aux2, W1);
				AddBigNbrModN(X, Z, Aux1);
				SubtractBigNbrModN(TX, TZ, Aux2);
				montgomery.mul(Aux1, Aux2, W2);
				AddBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, Aux1, Aux2);
				montgomery.mul(Aux2, UZ, X);
				SubtractBigNbrModN(W1, W2, Aux1);
				montgomery.mul(Aux1, Aux1, Aux2);
				montgomery.mul(Aux2, UX, Z); // (X:Z) -> 3*2310Q
				int Qaux = (int) (L1 / 4620);
				int maxIndexM = (int) (L2 / 4620);
				for (int indexM = 0; indexM <= maxIndexM; indexM++) {
					if (indexM >= Qaux) { // If inside step 2 range...
						if (indexM == 0) {
							ModInvBigNbr(UZ, TestNbr, Aux2);
							montgomery.mul(Aux2, MontgomeryMultAfterInv, Aux3);
							montgomery.mul(UX, Aux3, Aux1); // Aux1 <- X/Z (2310Q)
						} else {
							ModInvBigNbr(Z, TestNbr, Aux2);
							montgomery.mul(Aux2, MontgomeryMultAfterInv, Aux3);
							montgomery.mul(X, Aux3, Aux1); // Aux1 <- X/Z (3,5,* 2310Q)
						}

						/* Generate sieve */
						if (indexM % 10 == 0 || indexM == Qaux) {
							GenerateSieve(indexM / 10 * 46200 + 1, sieve, sieve2310, SmallPrime);
						}
						/* Walk through sieve */
						J = 1155 + (indexM % 10) * 2310;
						for (int i = 0; i < 480; i++) {
							j = sieveidx[i]; // 0 < J < 1155
							if (sieve[J + j] != 0 && sieve[J - 1 - j] != 0) {
								continue; // Do not process if both are composite numbers.
							}
							SubtractBigNbrModN(Aux1, root[i], M);
							montgomery.mul(GcdAccumulated, M, Aux2);
							System.arraycopy(Aux2, 0, GcdAccumulated, 0, NumberLength);
						}
						if (Pass != 0) {
							GcdBigNbr(GcdAccumulated, TestNbr, GD);
							if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
								return BigIntToBigNbr(GD); // found factor, exit
							}
						}
					}
					if (indexM != 0) { // Update (X:Z)
						System.arraycopy(X, 0, WX, 0, NumberLength);
						System.arraycopy(Z, 0, WZ, 0, NumberLength);
						SubtractBigNbrModN(X, Z, Aux1);
						AddBigNbrModN(TX, TZ, Aux2);
						montgomery.mul(Aux1, Aux2, W1);
						AddBigNbrModN(X, Z, Aux1);
						SubtractBigNbrModN(TX, TZ, Aux2);
						montgomery.mul(Aux1, Aux2, W2);
						AddBigNbrModN(W1, W2, Aux1);
						montgomery.mul(Aux1, Aux1, Aux2);
						montgomery.mul(Aux2, UZ, X);
						SubtractBigNbrModN(W1, W2, Aux1);
						montgomery.mul(Aux1, Aux1, Aux2);
						montgomery.mul(Aux2, UX, Z);
						System.arraycopy(WX, 0, UX, 0, NumberLength);
						System.arraycopy(WZ, 0, UZ, 0, NumberLength);
					}
				} // end for Q
				if (Pass == 0) {
					if (BigNbrIsZero(GcdAccumulated)) { // If GcdAccumulated is...
						System.arraycopy(Xaux, 0, X, 0, NumberLength);
						System.arraycopy(Zaux, 0, Z, 0, NumberLength);
						continue; // ... a multiple of TestNbr, continue.
					}
					GcdBigNbr(GcdAccumulated, TestNbr, GD);
					if (!BigNbrAreEqual(GD, BigNbr1) && !BigNbrAreEqual(GD, TestNbr)) {
						return BigIntToBigNbr(GD); // found factor, exit
					}
					break;
				}
			} /* end for Pass */
		} /* end curve calculation */
		
		return I_1; // no factor found
	}
	
	private static void GenerateSieve(int initial, byte[] sieve, byte[] sieve2310, int[] SmallPrime) {
		int i, j, Q;
		for (i = 0; i < 23100; i += 2310) {
			System.arraycopy(sieve2310, 0, sieve, i, 2310);
		}
		j = 5;
		Q = 13; /* Point to prime 13 */
		do {
			if (initial > Q * Q) {
				for (i = (int) (((long) initial * ((Q - 1) / 2)) % Q); i < 23100; i += Q) {
					sieve[i] = 1; /* Composite */
				}
			} else {
				i = Q * Q - initial;
				if (i < 46200) {
					for (i = i / 2; i < 23100; i += Q) {
						sieve[i] = 1; /* Composite */
					}
				} else {
					break;
				}
			}
			Q = SmallPrime[++j];
		} while (Q < 5000);
	}
	
	// Start of code "borrowed" from Paul Zimmermann's ECM4C

	/**
	 * Computes nP from P=(x:z) and puts the result in (x:z). Assumes n>2.
	 * 
	 * Uses the following global variables:
	 * - N = TestNbr: number to factor<br>
	 * - bZi Zimmermann's b = (a+2)/4 mod N<br>
	 * - xB, zB, xC, zC, xT, zT, xT2, zT2: auxiliary variables
	 * 
	 * @param n the scalar to multiply P with.
	 * @param x
	 * @param z
	 * @param xT
	 * @param zT
	 * @param xT2
	 * @param zT2
	 * @param bZi Zimmermann's b = (a+2)/4 mod N
	 */
	private void prac(int n, int[] x, int[] z, int[] xT, int[] zT, int[] xT2, int[] zT2, int[] bZi) {
		int d, e, r, i;
		int[] t;
		int[] xA = x, zA = z;
		int[] xB = fieldAux1, zB = fieldAux2;
		int[] xC = fieldAux3, zC = fieldAux4;

		/* chooses the best value of v */
		r = lucas_cost(n, v[0]);
		i = 0;
		for (d = 1; d < 10; d++) {
			e = lucas_cost(n, v[d]);
			if (e < r) {
				r = e;
				i = d;
			}
		}
		d = n;
		r = (int) (d / v[i] + 0.5);
		/* first iteration always begins by Condition 3, then a swap */
		d = n - r;
		e = 2 * r - n;
		System.arraycopy(xA, 0, xB, 0, NumberLength); // B = A
		System.arraycopy(zA, 0, zB, 0, NumberLength);
		System.arraycopy(xA, 0, xC, 0, NumberLength); // C = A
		System.arraycopy(zA, 0, zC, 0, NumberLength);
		duplicate(xA, zA, xA, zA, bZi); /* A=2*A */
		while (d != e) {
			if (d < e) {
				r = d;
				d = e;
				e = r;
				t = xA;
				xA = xB;
				xB = t;
				t = zA;
				zA = zB;
				zB = t;
			}
			/* do the first line of Table 4 whose condition qualifies */
			if (4 * d <= 5 * e && ((d + e) % 3) == 0) { /* condition 1 */
				r = (2 * d - e) / 3;
				e = (2 * e - d) / 3;
				d = r;
				add3(xT, zT, xA, zA, xB, zB, xC, zC); /* T = f(A,B,C) */
				add3(xT2, zT2, xT, zT, xA, zA, xB, zB); /* T2 = f(T,A,B) */
				add3(xB, zB, xB, zB, xT, zT, xA, zA); /* B = f(B,T,A) */
				t = xA;
				xA = xT2;
				xT2 = t;
				t = zA;
				zA = zT2;
				zT2 = t; /* swap A and T2 */
			} else if (4 * d <= 5 * e && (d - e) % 6 == 0) { /* condition 2 */
				d = (d - e) / 2;
				add3(xB, zB, xA, zA, xB, zB, xC, zC); /* B = f(A,B,C) */
				duplicate(xA, zA, xA, zA, bZi); /* A = 2*A */
			} else if (d <= (4 * e)) { /* condition 3 */
				d -= e;
				add3(xT, zT, xB, zB, xA, zA, xC, zC); /* T = f(B,A,C) */
				t = xB;
				xB = xT;
				xT = xC;
				xC = t;
				t = zB;
				zB = zT;
				zT = zC;
				zC = t; /* circular permutation (B,T,C) */
			} else if ((d + e) % 2 == 0) { /* condition 4 */
				d = (d - e) / 2;
				add3(xB, zB, xB, zB, xA, zA, xC, zC); /* B = f(B,A,C) */
				duplicate(xA, zA, xA, zA, bZi); /* A = 2*A */
			} else if (d % 2 == 0) { /* condition 5 */
				d /= 2;
				add3(xC, zC, xC, zC, xA, zA, xB, zB); /* C = f(C,A,B) */
				duplicate(xA, zA, xA, zA, bZi); /* A = 2*A */
			} else if (d % 3 == 0) { /* condition 6 */
				d = d / 3 - e;
				duplicate(xT, zT, xA, zA, bZi); /* T1 = 2*A */
				add3(xT2, zT2, xA, zA, xB, zB, xC, zC); /* T2 = f(A,B,C) */
				add3(xA, zA, xT, zT, xA, zA, xA, zA); /* A = f(T1,A,A) */
				add3(xT, zT, xT, zT, xT2, zT2, xC, zC); /* T1 = f(T1,T2,C) */
				t = xC;
				xC = xB;
				xB = xT;
				xT = t;
				t = zC;
				zC = zB;
				zB = zT;
				zT = t; /* circular permutation (C,B,T) */
			} else if ((d + e) % 3 == 0) { /* condition 7 */
				d = (d - 2 * e) / 3;
				add3(xT, zT, xA, zA, xB, zB, xC, zC); /* T1 = f(A,B,C) */
				add3(xB, zB, xT, zT, xA, zA, xB, zB); /* B = f(T1,A,B) */
				duplicate(xT, zT, xA, zA, bZi);
				add3(xA, zA, xA, zA, xT, zT, xA, zA); /* A = 3*A */
			} else if ((d - e) % 3 == 0) { /* condition 8 */
				d = (d - e) / 3;
				add3(xT, zT, xA, zA, xB, zB, xC, zC); /* T1 = f(A,B,C) */
				add3(xC, zC, xC, zC, xA, zA, xB, zB); /* C = f(A,C,B) */
				t = xB;
				xB = xT;
				xT = t;
				t = zB;
				zB = zT;
				zT = t; /* swap B and T */
				duplicate(xT, zT, xA, zA, bZi);
				add3(xA, zA, xA, zA, xT, zT, xA, zA); /* A = 3*A */
			} else if (e % 2 == 0) { /* condition 9 */
				e /= 2;
				add3(xC, zC, xC, zC, xB, zB, xA, zA); /* C = f(C,B,A) */
				duplicate(xB, zB, xB, zB, bZi); /* B = 2*B */
			}
		}
		add3(x, z, xA, zA, xB, zB, xC, zC);
	}

	/**
	 * Returns the number of modular multiplications in the computation of nP.
	 * 
	 * @param n the scalar to multiply a point with
	 * @param v weight
	 * @return number of modular multiplications
	 */
	private static int lucas_cost(int n, double v) {
		int c, d, e, r;

		d = n;
		r = (int) (d / v + 0.5);
		if (r >= n) return (ADD * n);
		
		d = n - r;
		e = 2 * r - n;
		c = DUP + ADD; /* initial duplicate and final addition */
		while (d != e) {
			if (d < e) {
				r = d;
				d = e;
				e = r;
			}
			if (4 * d <= 5 * e && ((d + e) % 3) == 0) { /* condition 1 */
				r = (2 * d - e) / 3;
				e = (2 * e - d) / 3;
				d = r;
				c += 3 * ADD; /* 3 additions */
			} else if (4 * d <= 5 * e && (d - e) % 6 == 0) { /* condition 2 */
				d = (d - e) / 2;
				c += ADD + DUP; /* one addition, one duplicate */
			} else if (d <= (4 * e)) { /* condition 3 */
				d -= e;
				c += ADD; /* one addition */
			} else if ((d + e) % 2 == 0) { /* condition 4 */
				d = (d - e) / 2;
				c += ADD + DUP; /* one addition, one duplicate */
			} else if (d % 2 == 0) { /* condition 5 */
				d /= 2;
				c += ADD + DUP; /* one addition, one duplicate */
			} else if (d % 3 == 0) { /* condition 6 */
				d = d / 3 - e;
				c += 3 * ADD + DUP; /* three additions, one duplicate */
			} else if ((d + e) % 3 == 0) { /* condition 7 */
				d = (d - 2 * e) / 3;
				c += 3 * ADD + DUP; /* three additions, one duplicate */
			} else if ((d - e) % 3 == 0) { /* condition 8 */
				d = (d - e) / 3;
				c += 3 * ADD + DUP; /* three additions, one duplicate */
			} else if (e % 2 == 0) { /* condition 9 */
				e /= 2;
				c += ADD + DUP; /* one addition, one duplicate */
			}
		}
		return (c);
	}

	/**
	 * Computes 2P=(x2:z2) from P=(x1:z1), with 5 mul, 4 add/sub, 5 mod.<br><br>
	 * 
	 * Uses the following global variables:<br>
	 * - N = TestNbr: number to factor<br>
	 * - bZi Zimmermann's b = (a+2)/4 mod N<br>
	 * - u, v, w : auxiliary variables<br><br>
	 * 
	 * Modifies: x2, z2, u, v, w
	 * 
	 * @param x2 
	 * @param z2 
	 * @param x1 
	 * @param z1 
	 * @param bZi Zimmermann's b = (a+2)/4 mod N
	 */
	private void duplicate(int[] x2, int[] z2, int[] x1, int[] z1, int[] bZi) {
		int[] u = fieldUZ;
		int[] v = fieldTX;
		int[] w = fieldTZ;
		AddBigNbrModN(x1, z1, w); // w = x1+z1
		montgomery.mul(w, w, u); // u = (x1+z1)^2
		SubtractBigNbrModN(x1, z1, w); // w = x1-z1
		montgomery.mul(w, w, v); // v = (x1-z1)^2
		montgomery.mul(u, v, x2); // x2 = u*v = (x1^2 - z1^2)^2
		SubtractBigNbrModN(u, v, w); // w = u-v = 4*x1*z1
		montgomery.mul(bZi, w, u);
		AddBigNbrModN(u, v, u); // u = (v+b*w)
		montgomery.mul(w, u, z2); // z2 = (w*u)
	}

	/**
	 * Adds Q=(x2:z2) and R=(x1:z1) and puts the result in (x3:z3), using 5/6 mul, 6 add/sub and 6 mod.
	 * One assumes that Q-R=P or R-Q=P where P=(x:z).<br><br>
	 * 
	 * Uses the following global variables:<br>
	 * - N = TestNbr: number to factor<br>
	 * - x, z : coordinates of P<br>
	 * - t, u, v, w : auxiliary variables<br><br>
	 * 
	 * Modifies: x3, z3, t, u, v, w. (x3,z3) may be identical to (x2,z2) and to (x,z).
	 * 
	 * @param x3 
	 * @param z3 
	 * @param x2 
	 * @param z2 
	 * @param x1 
	 * @param z1 
	 * @param x 
	 * @param z 
	 */
	private void add3(int[] x3, int[] z3, int[] x2, int[] z2, int[] x1, int[] z1, int[] x, int[] z) {
		int[] t = fieldTX;
		int[] u = fieldTZ;
		int[] v = fieldUX;
		int[] w = fieldUZ;
		SubtractBigNbrModN(x2, z2, v); // v = x2-z2
		AddBigNbrModN(x1, z1, w);      // w = x1+z1
		montgomery.mul(v, w, u); // u = (x2-z2)*(x1+z1)
		AddBigNbrModN(x2, z2, w); // w = x2+z2
		SubtractBigNbrModN(x1, z1, t); // t = x1-z1
		montgomery.mul(t, w, v); // v = (x2+z2)*(x1-z1)
		AddBigNbrModN(u, v, t); // t = 2*(x1*x2-z1*z2)
		montgomery.mul(t, t, w); // w = 4*(x1*x2-z1*z2)^2
		SubtractBigNbrModN(u, v, t); // t = 2*(x2*z1-x1*z2)
		montgomery.mul(t, t, v); // v = 4*(x2*z1-x1*z2)^2
		if (BigNbrAreEqual(x, x3)) {
			System.arraycopy(x, 0, u, 0, NumberLength);
			System.arraycopy(w, 0, t, 0, NumberLength);
			montgomery.mul(z, t, w);
			montgomery.mul(v, u, z3);
			System.arraycopy(w, 0, x3, 0, NumberLength);
		} else {
			montgomery.mul(w, z, x3); // x3 = 4*z*(x1*x2-z1*z2)^2
			montgomery.mul(x, v, z3); // z3 = 4*x*(x2*z1-x1*z2)^2
		}
	}
	
	// End of code "borrowed" from Paul Zimmermann's ECM4C

	/**
	 * Convert a long <code>Nbr</code> into a BigNbr <code>Out</code> represented by 31-bit integers.
	 * @param Nbr input
	 * @param Out output
	 */
	void LongToBigNbr(long Nbr, int Out[]) {
		final boolean chSign = Nbr<0 ? true: false;
		final long nbrPos = Nbr<0 ? -Nbr : Nbr;
		
	    Out[0] = (int)(nbrPos & 0x7FFFFFFF);
	    Out[1] = (int)((nbrPos >> 31) & 0x7FFFFFFF);
	    if (NumberLength > 2) {
		    // bit 63 needs special consideration
	    	Out[2] = (nbrPos >= DosALa62) ? 1 : 0;
		    
		    for (int i = 3; i < NumberLength; i++) {
		    	Out[i] = 0;
		    }
	    }
	    
	    if (chSign) {
	    	ChSignBigNbr(Out);
	    }
	}

	/**
	 * Converts BigInteger N into TestNbr represented by 31-bit ints.
	 * @param N input
	 * @param TestNbr output
	 * @param NumberLength the size of numbers we are working with
	 */
	void BigNbrToBigInt(BigInteger N, int[] TestNbr, int NumberLength) {
		// Temp needs 1 extra int because of TestNbr[j] = p; in BigNbrToBigInt(long[])
	    long[] Temp = new long[NumberLength+1]; // zero-initialized
	    
	    // Convert32To31Bits does not work for negative N, so we switch the sign before and after
	    boolean chSign = N.signum()<0;
	    if (chSign) N = N.negate();
	    
		BigNbrToBigInt(N, Temp, NumberLength);
	    Convert32To31Bits(Temp, TestNbr, NumberLength);
	    
		if (chSign) ChSignBigNbr(TestNbr);
	}
	
	/**
	 * Converts BigInteger N into TestNbr represented by 32-bit ints.
	 * @param N input
	 * @param TestNbr output
	 * @param NumberLength
	 */
	void BigNbrToBigInt(BigInteger N, long[] TestNbr, int NumberLength) {
	    byte[] NBytes = N.toByteArray();
	    int j = 0;
	    int mask = 1;
	    long p = 0;
	    // process unsigned bytes
	    for (int i = NBytes.length - 1; i > 0; i--) {
	    	p += mask * (NBytes[i] & 0xFFL);
	    	mask <<= 8;
	    	if (mask == 0) { // int overflow after 4 shifts
	    		TestNbr[j++] = p;
	    		mask = 1;
		        p = 0;
	    	}
	    }
	    // the most significant byte holds BigIntegers sign bit
    	p += mask * NBytes[0];
    	mask <<= 8;
    	if (mask == 0) {
    		TestNbr[j++] = p;
    		mask = 1;
	        p = 0;
    	}
	    TestNbr[j] = p;
	    // Zero-initialize all TestNbr entries from j+1 to NumberLength.
	    // It is required that TestNbr[NumberLength] is set to 0, too.
	    while (++j <= NumberLength) {
	    	TestNbr[j] = 0;
	    }
	}

	static int computeNumberLength(int bitLength) {
		// The original computation was (bitLength + 30)/31;
		// added one bit for the sign and another to avoid overflows in additions
		return (bitLength + 32)/31;
	}

	void AddBigNbr(int Nbr1[], int Nbr2[], int Sum[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) + Nbr1[i] + Nbr2[i];
			Sum[i] = (int)(Cy & 0x7FFFFFFFL);
		}
	}

	void AddBigNbr32(long Nbr1[], long Nbr2[], long Sum[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 32) + Nbr1[i] + Nbr2[i];
			Sum[i] = Cy & 0xFFFFFFFFl;
		}
	}

	void AddBigNbrModN(int Nbr1[], int Nbr2[], int Sum[]) {
		int NumberLength = this.NumberLength;
	    long MaxUInt = 0x7FFFFFFFL;
	    long carry = 0;
	    int i;
	
	    for (i = 0; i < NumberLength; i++) {
	    	carry = (carry >> 31) + (long)Nbr1[i] + (long)Nbr2[i] - (long)TestNbr[i];
	    	Sum[i] = (int)(carry & MaxUInt);
	    }
	    if (carry < 0) {
	    	carry = 0;
	    	for (i = 0; i < NumberLength; i++) {
	    		carry = (carry >> 31) + (long)Sum[i] + (long)TestNbr[i];
	    		Sum[i] = (int)(carry & MaxUInt);
	    	}
	    }
	}

	void SubtractBigNbr(int Nbr1[], int Nbr2[], int Diff[]) {
	    long carry = 0;
	    for (int i = 0; i < NumberLength; i++) {
	    	carry = (carry >> 31) + (long)Nbr1[i] - (long)Nbr2[i];
	    	Diff[i] = (int)(carry & 0x7FFFFFFFL);
	    }
	}

	void SubtractBigNbr32(long Nbr1[], long Nbr2[], long Diff[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 32) + Nbr1[i] - Nbr2[i];
			Diff[i] = Cy & 0xFFFFFFFFl;
		}
	}

	void SubtractBigNbrModN(int Nbr1[], int Nbr2[], int Diff[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFL; // Integer.MAX_VALUE
		long carry = 0;
		int i;
		
		for (i = 0; i < NumberLength; i++) {
			carry = (carry >> 31) + (long)Nbr1[i] - (long)Nbr2[i];
			Diff[i] = (int)(carry & MaxUInt);
		}
		if (carry < 0) {
			carry = 0;
			for (i = 0; i < NumberLength; i++) {
				carry = (carry >> 31) + (long)Diff[i] + (long)TestNbr[i];
				Diff[i] = (int)(carry & MaxUInt);
			}
		}
	}

	void MultBigNbrByLongModN(int Nbr1[], long Nbr2, int Prod[], double dN) {
		int NumberLength = this.NumberLength;
	    long MaxUInt = 0x7FFFFFFFL;
	    long Pr;
	    int j;
	    
	    Pr = 0;
	    for (j = 0; j < NumberLength; j++) {
	    	Pr = (Pr >>> 31) + Nbr2 * Nbr1[j];
	    	Prod[j] = (int)(Pr & MaxUInt);
	    }
	    Prod[j] = (int)(Pr >>> 31);
	    AdjustModN(Prod, dN);
	}

	private void MultBigNbrModN(int Nbr1[], int Nbr2[], int Prod[], double dN) {
		int NumberLength = this.NumberLength;
	    long MaxUInt = 0x7FFFFFFFL;
	    int i, j;
	    long Pr, Nbr;
	    
	    i = NumberLength;
	    do {
	    	Prod[--i] = 0;
	    } while (i > 0);
	    i = NumberLength;
	    do {
	    	Nbr = Nbr1[--i];
	    	j = NumberLength;
	    	do {
	    		Prod[j] = Prod[j - 1];
	    		j--;
	    	} while (j > 0);
	    	Prod[0] = 0;
	    	Pr = 0;
	    	for (j = 0; j < NumberLength; j++) {
	    		Pr = (Pr >>> 31) + Nbr * Nbr2[j] + Prod[j];
	    		Prod[j] = (int)(Pr & MaxUInt);
	    	}
	    	Prod[j] += (Pr >>> 31);
	    	AdjustModN(Prod, dN);
	    } while (i > 0);
	}

	private void AdjustModN(int Nbr[], double dN) {
		int NumberLength = this.NumberLength;
	    long MaxUInt = 0x7FFFFFFFL;
	    long TrialQuotient;
	    long carry;
	    int i;
	    double dAux;

		dAux = Nbr[NumberLength] * dDosALa31 + Nbr[NumberLength - 1];
		if (NumberLength > 1) {
			dAux += Nbr[NumberLength - 2] / dDosALa31;
	    }
	    TrialQuotient = (long) (dAux / dN) + 3; // Axel: (long) Math.ceil(dAux / dN) + 2;
	    if (TrialQuotient >= DosALa32) {
	    	carry = 0;
	    	for (i = 0; i < NumberLength; i++) {
	    		carry = Nbr[i + 1] - (TrialQuotient >>> 31) * TestNbr[i] - carry;
	    		Nbr[i + 1] = (int)(carry & MaxUInt);
	    		carry = (MaxUInt - carry) >>> 31;
	    	}
	    	TrialQuotient &= MaxUInt;
	    }
	    carry = 0;
	    for (i = 0; i < NumberLength; i++) {
	    	carry = Nbr[i] - TrialQuotient * TestNbr[i] - carry;
	    	Nbr[i] = (int)(carry & MaxUInt);
	    	carry = (MaxUInt - carry) >>> 31;
	    }
	    Nbr[NumberLength] -= (int)carry;
	    while ((Nbr[NumberLength] & MaxUInt) != 0) {
	    	carry = 0;
	    	for (i = 0; i < NumberLength; i++) {
	    		carry += (long)Nbr[i] + (long)TestNbr[i];
	    		Nbr[i] = (int)(carry & MaxUInt);
	    		carry >>= 31;
	    	}
	    	Nbr[NumberLength] += (int)carry;
	    }
	}

	private void DivBigNbrByLong(int Dividend[], long Divisor, int Quotient[]) {
		int i;
		boolean ChSignDivisor = false;
		long Divid, Rem = 0;

		if (Divisor < 0) { // If divisor is negative...
			ChSignDivisor = true; // Indicate to change sign at the end and
			Divisor = -Divisor; // convert divisor to positive.
		}
		if (Dividend[i = NumberLength - 1] >= 0x40000000l) { // If dividend is negative...
			Rem = Divisor - 1;
		}
		for (; i >= 0; i--) {
			Divid = Dividend[i] + (Rem << 31);
			Rem = Divid % Divisor;
			Quotient[i] = (int) (Divid / Divisor);
		}
		if (ChSignDivisor) { // Change sign if divisor is negative.
			ChSignBigNbr(Quotient); // Convert divisor to positive.
		}
	}

	private boolean BigNbrAreEqual(int Nbr1[], int Nbr2[]) {
		for (int i = 0; i < NumberLength; i++) {
			if (Nbr1[i] != Nbr2[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean BigNbrIsZero(int Nbr[]) {
		for (int i = 0; i < NumberLength; i++) {
			if (Nbr[i] != 0) {
				return false;
			}
		}
		return true;
	}

	private void ChSignBigNbr(int Nbr[]) {
		int NumberLength = this.NumberLength;
		int Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) - Nbr[i];
			Nbr[i] = Cy & 0x7FFFFFFF;
		}
	}

	void Convert31To32Bits(int[] nbr31, long[] nbr32) {
		int i, j, k;
		i = 0;
		for (j = -1; j < NumberLength; j++) {
			k = i % 31;
			if (k == 0) {
				j++;
			}
			if (j == NumberLength) {
				break;
			}
			if (j == NumberLength - 1) {
				nbr32[i] = nbr31[j] >> k;
			} else {
				nbr32[i] = ((nbr31[j] >> k) | (nbr31[j + 1] << (31 - k))) & 0xFFFFFFFFl;
			}
			i++;
		}
		for (; i < NumberLength; i++) {
			nbr32[i] = 0;
		}
	}

	/**
	 * convert nbr32 into nbr31.<br/><br/>
	 * 
	 * <strong>Warning: This implementation does not work for negative arguments!</strong>
	 * 
	 * @param nbr32
	 * @param nbr31
	 * @param NumberLength
	 */
	private void Convert32To31Bits(long[] nbr32, int[] nbr31, int NumberLength) {
		int i, j, k;
		j = 0;
		for (i = 0; i < NumberLength; i++) {
		    k = i & 0x0000001F; // k = i % 32
			if (k == 0) {
		        nbr31[i] = (int)(nbr32[j] & 0x7FFFFFFF);
			} else {
		        nbr31[i] = (int)(((nbr32[j] >> (32-k)) | (nbr32[j+1] << k)) & 0x7FFFFFFF);
				j++;
			}
		}
	}

	/**
	 * Gcd calculation:
	 * <ul>
	 * <li>Step 1: Set k<-0, and then repeatedly set k<-k+1, u<-u/2, v<-v/2 zero or more times until u and v are not
	 * both even.</li>
	 * <li>Step 2: If u is odd, set t<-(-v) and go to step 4. Otherwise set t<-u.</li>
	 * <li>Step 3: Set t<-t/2</li>
	 * <li>Step 4: If t is even, go back to step 3.</li>
	 * <li>Step 5: If t>0, set u<-t, otherwise set v<-(-t).</li>
	 * <li>Step 6: Set t<-u-v. If t!=0, go back to step 3.</li>
	 * <li>Step 7: The GCD is u*2^k.</li>
	 * </ul>
	 * 
	 * @param Nbr1
	 * @param Nbr2
	 * @param Gcd
	 */
	void GcdBigNbr(int Nbr1[], int Nbr2[], int Gcd[]) {
	    int k;
		int NumberLength = this.NumberLength;

	    System.arraycopy(Nbr1, 0, CalcAuxGcdU, 0, NumberLength);
	    System.arraycopy(Nbr2, 0, CalcAuxGcdV, 0, NumberLength);
	    if (BigNbrIsZero(CalcAuxGcdU)) {
	    	System.arraycopy(CalcAuxGcdV, 0, Gcd, 0, NumberLength);
	    	return;
	    }
	    if (BigNbrIsZero(CalcAuxGcdV)) {
	    	System.arraycopy(CalcAuxGcdU, 0, Gcd, 0, NumberLength);
	    	return;
	    }
	    if (CalcAuxGcdU[NumberLength - 1] >= 0x40000000L) {
	    	ChSignBigNbr(CalcAuxGcdU);
	    }
	    if (CalcAuxGcdV[NumberLength - 1] >= 0x40000000L) {
	    	ChSignBigNbr(CalcAuxGcdV);
	    }
	    k = 0;
		while ((CalcAuxGcdU[0] & 1) == 0 && (CalcAuxGcdV[0] & 1) == 0) { // Step 1
	    	k++;
	    	DivBigNbrByLong(CalcAuxGcdU, 2, CalcAuxGcdU);
	    	DivBigNbrByLong(CalcAuxGcdV, 2, CalcAuxGcdV);
	    }
		if ((CalcAuxGcdU[0] & 1) == 1) { // Step 2
	    	System.arraycopy(CalcAuxGcdV, 0, CalcAuxGcdT, 0, NumberLength);
	    	ChSignBigNbr(CalcAuxGcdT);
		} else {
			System.arraycopy(CalcAuxGcdU, 0, CalcAuxGcdT, 0, NumberLength);
	    }
		do {
			while ((CalcAuxGcdT[0] & 1) == 0) { // Step 4
				DivBigNbrByLong(CalcAuxGcdT, 2, CalcAuxGcdT); // Step 3
			}
			if (CalcAuxGcdT[NumberLength - 1] < 0x40000000l) { // Step 5
				System.arraycopy(CalcAuxGcdT, 0, CalcAuxGcdU, 0, NumberLength);
			} else {
				System.arraycopy(CalcAuxGcdT, 0, CalcAuxGcdV, 0, NumberLength);
				ChSignBigNbr(CalcAuxGcdV);
			}                                                
			SubtractBigNbr(CalcAuxGcdU, CalcAuxGcdV, CalcAuxGcdT); // Step 6
	    } while (!BigNbrIsZero(CalcAuxGcdT));
		
	    System.arraycopy(CalcAuxGcdU, 0, Gcd, 0, NumberLength); // Step 7
		while (k > 0) {
	    	AddBigNbr(Gcd, Gcd, Gcd);
	    	k--;
	    }
	}

	/**
	 * Find the multiplicative inverse (1/a) modulo b and return the result in inv.
	 * If called for some a that is not invertible modulo b, this implementation will return a non-trivial factor of b ([CP], page 336).
	 * 
	 * @param a
	 * @param b
	 * @param inv the inverse or a non-trivial factor of b
	 */
	void ModInvBigNbr(int[] a, int[] b, int[] inv) {
	    int i;
		int NumberLength = this.NumberLength;
	    int Dif, E;
	    int st1, st2;
	    long Yaa, Yab; // 2^E * A'     = Yaa A + Yab B
	    long Yba, Ybb; // 2^E * B'     = Yba A + Ybb B
	    long Ygb0; // 2^E * Mu'    = Yaa Mu + Yab Gamma + Ymb0 B0
	    long Ymb0; // 2^E * Gamma' = Yba Mu + Ybb Gamma + Ygb0 B0
	    int Iaa, Iab, Iba, Ibb;
	    long Tmp1, Tmp2, Tmp3, Tmp4, Tmp5;
	    int B0l;
	    int invB0l;
	    int Al, Bl, T1, Gl, Ml;
	    long carry1, carry2, carry3, carry4;
	    int Yaah, Yabh, Ybah, Ybbh;
	    int Ymb0h, Ygb0h;
	    long Pr1, Pr2, Pr3, Pr4, Pr5, Pr6, Pr7;
	    
	    Convert31To32Bits(a, CalcAuxModInvA);
	    Convert31To32Bits(b, CalcAuxModInvB);
	    System.arraycopy(CalcAuxModInvB, 0, B, 0, NumberLength);
	    B0l = (int)B[0];
	    invB0l = B0l; // 2 least significant bits of inverse correct.
	    invB0l = invB0l * (2 - B0l * invB0l); // 4 LSB of inverse correct.
	    invB0l = invB0l * (2 - B0l * invB0l); // 8 LSB of inverse correct.
	    invB0l = invB0l * (2 - B0l * invB0l); // 16 LSB of inverse correct.
	    invB0l = invB0l * (2 - B0l * invB0l); // 32 LSB of inverse correct.
		for (i = NumberLength - 1; i >= 0; i--) {
			CalcAuxModInvGamma[i] = 0;
			CalcAuxModInvMu[i] = 0;
	    }
	    CalcAuxModInvMu[0] = 1;
	    Dif = 0;
		outer_loop: do {
			Iaa = Ibb = 1;
			Iab = Iba = 0;
			Al = (int) CalcAuxModInvA[0];
			Bl = (int) CalcAuxModInvB[0];
			E = 0;
			if (Bl == 0) {
				for (i = NumberLength - 1; i >= 0; i--) {
					if (CalcAuxModInvB[i] != 0) break;
				}
				if (i < 0) break; // Go out of loop if CalcAuxModInvB = 0
			}
			do {
		        T1 = 0;
				while ((Bl & 1) == 0) {
					if (E == 31) {
			            Yaa = Iaa;
			            Yab = Iab;
			            Yba = Iba;
			            Ybb = Ibb;
			            Gl = (int) CalcAuxModInvGamma[0];
			            Ml = (int) CalcAuxModInvMu[0];
			            Dif++;
			            T1++;
			            Yaa <<= T1;
			            Yab <<= T1;
			            Ymb0 = (- (int) Yaa * Ml - (int) Yab * Gl) * invB0l;
			            Ygb0 = (-Iba * Ml - Ibb * Gl) * invB0l;
			            carry1 = carry2 = carry3 = carry4 = 0;
			            Yaah = (int) (Yaa >> 32);
			            Yabh = (int) (Yab >> 32);
			            Ybah = (int) (Yba >> 32);
			            Ybbh = (int) (Ybb >> 32);
			            Ymb0h = (int) (Ymb0 >> 32);
			            Ygb0h = (int) (Ygb0 >> 32);
			            Yaa &= 0xFFFFFFFFL;
			            Yab &= 0xFFFFFFFFL;
			            Yba &= 0xFFFFFFFFL;
			            Ybb &= 0xFFFFFFFFL;
			            Ymb0 &= 0xFFFFFFFFL;
			            Ygb0 &= 0xFFFFFFFFL;
		
			            st1 = Yaah * 6 + Yabh * 2 + Ymb0h;
			            st2 = Ybah * 6 + Ybbh * 2 + Ygb0h;
			            for (i = 0; i < NumberLength; i++) {
			            	Pr1 = Yaa * (Tmp1 = CalcAuxModInvMu[i]);
			            	Pr2 = Yab * (Tmp2 = CalcAuxModInvGamma[i]);
			            	Pr3 = Ymb0 * (Tmp3 = B[i]);
			            	Pr4 = (Pr1 & 0xFFFFFFFFL) + (Pr2 & 0xFFFFFFFFL) + (Pr3 & 0xFFFFFFFFL) + carry3;
			            	Pr5 = Yaa * (Tmp4 = CalcAuxModInvA[i]);
			            	Pr6 = Yab * (Tmp5 = CalcAuxModInvB[i]);
			            	Pr7 = (Pr5 & 0xFFFFFFFFL) + (Pr6 & 0xFFFFFFFFL) + carry1;
			            	switch (st1) {
			            	case -9 :
			            		carry3 = -Tmp1 - Tmp2 - Tmp3;
			            		carry1 = -Tmp4 - Tmp5;
			            		break;
			                case -8 :
			                	carry3 = -Tmp1 - Tmp2;
			                	carry1 = -Tmp4 - Tmp5;
			                	break;
			                case -7 :
			                	carry3 = -Tmp1 - Tmp3;
			                	carry1 = -Tmp4;
			                	break;
			                case -6 :
			                	carry3 = -Tmp1;
			                	carry1 = -Tmp4;
			                	break;
			                case -5 :
			                	carry3 = -Tmp1 + Tmp2 - Tmp3;
			                	carry1 = -Tmp4 + Tmp5;
			                	break;
			                case -4 :
			                	carry3 = -Tmp1 + Tmp2;
			                	carry1 = -Tmp4 + Tmp5;
			                	break;
			                case -3 :
			                	carry3 = -Tmp2 - Tmp3;
			                	carry1 = -Tmp5;
			                	break;
			                case -2 :
			                	carry3 = -Tmp2;
			                	carry1 = -Tmp5;
			                	break;
			                case -1 :
			                	carry3 = -Tmp3;
			                	carry1 = 0;
			                	break;
			                case 0 :
			                	carry3 = 0;
			                	carry1 = 0;
			                	break;
			                case 1 :
			                	carry3 = Tmp2 - Tmp3;
			                	carry1 = Tmp5;
			                	break;
			                case 2 :
			                	carry3 = Tmp2;
			                	carry1 = Tmp5;
			                	break;
			                case 3 :
			                	carry3 = Tmp1 - Tmp2 - Tmp3;
			                	carry1 = Tmp4 - Tmp5;
			                	break;
			                case 4 :
			                	carry3 = Tmp1 - Tmp2;
			                	carry1 = Tmp4 - Tmp5;
			                	break;
			                case 5 :
			                	carry3 = Tmp1 - Tmp3;
			                	carry1 = Tmp4;
			                	break;
			                case 6 :
			                	carry3 = Tmp1;
			                	carry1 = Tmp4;
			                	break;
			                case 7 :
			                	carry3 = Tmp1 + Tmp2 - Tmp3;
			                	carry1 = Tmp4 + Tmp5;
			                	break;
			                case 8 :
			                	carry3 = Tmp1 + Tmp2;
			                	carry1 = Tmp4 + Tmp5;
			                	break;
			            	}
			            	carry3 += (Pr1 >>> 32) + (Pr2 >>> 32) + (Pr3 >>> 32) + (Pr4 >> 32);
			            	carry1 += (Pr5 >>> 32) + (Pr6 >>> 32) + (Pr7 >> 32);
			            	if (i > 0) {
			            		CalcAuxModInvMu[i - 1] = Pr4 & 0xFFFFFFFFL;
			            		CalcAuxModInvA[i - 1] = Pr7 & 0xFFFFFFFFL;
			            	}
			            	Pr1 = Yba * Tmp1;
			            	Pr2 = Ybb * Tmp2;
			            	Pr3 = Ygb0 * Tmp3;
			            	Pr4 = (Pr1 & 0xFFFFFFFFL) + (Pr2 & 0xFFFFFFFFL) + (Pr3 & 0xFFFFFFFFL) + carry4;
			            	Pr5 = Yba * Tmp4;
			            	Pr6 = Ybb * Tmp5;
			            	Pr7 = (Pr5 & 0xFFFFFFFFL) + (Pr6 & 0xFFFFFFFFL) + carry2;
			            	switch (st2) {
			            	case -9 :
			            		carry4 = -Tmp1 - Tmp2 - Tmp3;
			            		carry2 = -Tmp4 - Tmp5;
			            		break;
			            	case -8 :
			     	            carry4 = -Tmp1 - Tmp2;
			     	            carry2 = -Tmp4 - Tmp5;
			     	            break;
			            	case -7 :
			            		carry4 = -Tmp1 - Tmp3;
			            		carry2 = -Tmp4;
			            		break;
			            	case -6 :
			            		carry4 = -Tmp1;
			            		carry2 = -Tmp4;
			            		break;
			            	case -5 :
			            		carry4 = -Tmp1 + Tmp2 - Tmp3;
			            		carry2 = -Tmp4 + Tmp5;
			            		break;
			            	case -4 :
			            		carry4 = -Tmp1 + Tmp2;
			            		carry2 = -Tmp4 + Tmp5;
			            		break;
			            	case -3 :
			            		carry4 = -Tmp2 - Tmp3;
			            		carry2 = -Tmp5;
			            		break;
			            	case -2 :
			            		carry4 = -Tmp2;
			            		carry2 = -Tmp5;
			            		break;
			            	case -1 :
			            		carry4 = -Tmp3;
			            		carry2 = 0;
			            		break;
			            	case 0 :
			            		carry4 = 0;
			            		carry2 = 0;
			            		break;
			            	case 1 :
			            		carry4 = Tmp2 - Tmp3;
			            		carry2 = Tmp5;
			            		break;
			            	case 2 :
			            		carry4 = Tmp2;
			            		carry2 = Tmp5;
			            		break;
			            	case 3 :
			            		carry4 = Tmp1 - Tmp2 - Tmp3;
			            		carry2 = Tmp4 - Tmp5;
			            		break;
			            	case 4 :
			            		carry4 = Tmp1 - Tmp2;
			            		carry2 = Tmp4 - Tmp5;
			            		break;
			            	case 5 :
			            		carry4 = Tmp1 - Tmp3;
			            		carry2 = Tmp4;
			            		break;
			            	case 6 :
			            		carry4 = Tmp1;
			            		carry2 = Tmp4;
			            		break;
			            	case 7 :
			            		carry4 = Tmp1 + Tmp2 - Tmp3;
			            		carry2 = Tmp4 + Tmp5;
			            		break;
			            	case 8 :
			            		carry4 = Tmp1 + Tmp2;
			            		carry2 = Tmp4 + Tmp5;
			            		break;
			            	}
			            	carry4 += (Pr1 >>> 32) + (Pr2 >>> 32) + (Pr3 >>> 32) + (Pr4 >> 32);
			            	carry2 += (Pr5 >>> 32) + (Pr6 >>> 32) + (Pr7 >> 32);
			            	if (i > 0) {
			            		CalcAuxModInvGamma[i - 1] = Pr4 & 0xFFFFFFFFL;
			            		CalcAuxModInvB[i - 1] = Pr7 & 0xFFFFFFFFL;
			            	}
			            }
		
			            if ((int) CalcAuxModInvA[i - 1] < 0) {
			            	carry1 -= Yaa;
			            	carry2 -= Yba;
			            }
			            if ((int) CalcAuxModInvB[i - 1] < 0) {
			            	carry1 -= Yab;
			            	carry2 -= Ybb;
			            }
			            if ((int) CalcAuxModInvMu[i - 1] < 0) {
			            	carry3 -= Yaa;
			            	carry4 -= Yba;
			            }
			            if ((int) CalcAuxModInvGamma[i - 1] < 0) {
			            	carry3 -= Yab;
			            	carry4 -= Ybb;
			            }
			            CalcAuxModInvA[i - 1] = carry1 & 0xFFFFFFFFL;
			            CalcAuxModInvB[i - 1] = carry2 & 0xFFFFFFFFL;
			            CalcAuxModInvMu[i - 1] = carry3 & 0xFFFFFFFFL;
			            CalcAuxModInvGamma[i - 1] = carry4 & 0xFFFFFFFFL;
			            continue outer_loop;
					}
					Bl >>= 1;
		            Dif++;
		            E++;
		            T1++;
		        }; /* end while */
		        
		        Iaa <<= T1;
		        Iab <<= T1;
		        if (Dif >= 0) {
		        	Dif = -Dif;
		        	if (((Al + Bl) & 3) == 0) {
			            T1 = Iba;
			            Iba += Iaa;
			            Iaa = T1;
			            T1 = Ibb;
			            Ibb += Iab;
			            Iab = T1;
			            T1 = Bl;
			            Bl += Al;
			            Al = T1;
					} else {
			            T1 = Iba;
			            Iba -= Iaa;
			            Iaa = T1;
			            T1 = Ibb;
			            Ibb -= Iab;
			            Iab = T1;
			            T1 = Bl;
			            Bl -= Al;
			            Al = T1;
					}
		        } else {
		        	if (((Al + Bl) & 3) == 0) {
			            Iba += Iaa;
			            Ibb += Iab;
			            Bl += Al;
		        	} else {
						Iba -= Iaa;
			            Ibb -= Iab;
			            Bl -= Al;
		        	}
		        }
		        Dif--;
			} while (true);
		} while (true);
	    
	    if (CalcAuxModInvA[0] != 1) {
	    	SubtractBigNbr32(B, CalcAuxModInvMu, CalcAuxModInvMu);
	    }
	    if ((int) CalcAuxModInvMu[i = NumberLength - 1] < 0) {
	    	AddBigNbr32(B, CalcAuxModInvMu, CalcAuxModInvMu);
	    }
	    for (; i >= 0; i--) {
	      if (B[i] != CalcAuxModInvMu[i]) break;
	    }
	    if (i < 0 || B[i] < CalcAuxModInvMu[i]) { // If B < Mu
	    	SubtractBigNbr32(CalcAuxModInvMu, B, CalcAuxModInvMu); // Mu <- Mu - B
	    }
	    // It doesn't matter here that Convert32To31Bits() does not work for negative inputs, because CalcAuxModInvMu is always positive
	    Convert32To31Bits(CalcAuxModInvMu, inv, NumberLength);
	    if (DEBUG) {
		    BigInteger inv32 = BigIntToBigNbr(CalcAuxModInvMu);
		    Ensure.ensureGreater(inv32.signum(), 0);
		    BigInteger inv31 = BigIntToBigNbr(inv);
		    Ensure.ensureEquals(inv32, inv31);
	    }
	}

	BigInteger BigIntToBigNbr(int[] nbr) {
		int[] nbrCopy = new int[NumberLength+1];
		System.arraycopy(nbr, 0, nbrCopy, 0, NumberLength);
		boolean chSign = false;
		
		if (nbr[NumberLength-1] >= 0x40000000L) {
			ChSignBigNbr(nbrCopy);
			chSign = true;
		}
		
		long[] Temp = new long[NumberLength];
		Convert31To32Bits(nbrCopy, Temp);
		BigInteger result = BigIntToBigNbr(Temp);
		if (chSign) {
			result = result.negate();
		}
		return result;
	}

	// This method used to fail for negative inputs with nbr[NumberLength-1]==0.
    // This has been fixed with a quite ugly workaround.
	// TODO look for a more elegant solution
	BigInteger BigIntToBigNbr(long[] nbr) {
		int trueNumberLength = NumberLength;
		for (int i = NumberLength-1; i>=0; i--) {
			if (nbr[i] != 0) break;
			trueNumberLength--;
		}
		boolean isNegative = trueNumberLength>0 && nbr[trueNumberLength-1]<0;
		if (isNegative) {
			int NL = trueNumberLength*4;
			byte[] NBytes = new byte[NL];
			for (int i = 0; i < trueNumberLength; i++) {
				final long digit = nbr[i];
				NBytes[NL - 1 - 4 * i] = (byte) (digit & 0xFF);
				NBytes[NL - 2 - 4 * i] = (byte) ((digit >> 8) & 0xFF);
				NBytes[NL - 3 - 4 * i] = (byte) ((digit >> 16) & 0xFF);
				NBytes[NL - 4 - 4 * i] = (byte) ((digit >> 24) & 0xFF);
			}
			
			// remove leading 0-bytes:
			int zeroCount = 0;
			for ( ; zeroCount<NL; zeroCount++) {
				if (NBytes[zeroCount] != 0) break;
			}
			if (zeroCount == 0) return new BigInteger(NBytes);
			int NL2 = NL - zeroCount;
			if (NL2 == 0) return BigInteger.valueOf(0);
			byte[] NBytes2 = new byte[NL2];
			System.arraycopy(NBytes, zeroCount, NBytes2, 0, NL2);
			return new BigInteger(NBytes2);
		}
		
		// Old implementation for positive numbers
		int NL = NumberLength*4;
		byte[] NBytes = new byte[NL];
		
		// Originally the following loop ran up to NumberLength (say 10). But it works as well with trueNumberLength (say 1), 
		// because (big-endian) leading zero-bytes are ignored by the BigInteger constructor.
		for (int i = 0; i < /*NumberLength*/ trueNumberLength; i++) {
			final long digit = nbr[i];
			NBytes[NL - 1 - 4 * i] = (byte) (digit & 0xFF);
			NBytes[NL - 2 - 4 * i] = (byte) ((digit >> 8) & 0xFF);
			NBytes[NL - 3 - 4 * i] = (byte) ((digit >> 16) & 0xFF);
			NBytes[NL - 4 - 4 * i] = (byte) ((digit >> 24) & 0xFF);
		}
		return new BigInteger(NBytes);
	}

	/**
	 * Converts a BigNbr in 31-bit representation into a String.
	 * @param Nbr
	 * @return decimal string representation of Nbr
	 */
	String BigNbrToString(int Nbr[]) {
		return this.BigIntToBigNbr(Nbr).toString();
	}
	
	/**
	 * Converts a BigNbr in 32-bit representation into a String.
	 * @param Nbr
	 * @return decimal string representation of Nbr
	 */
	String BigNbrToString(long Nbr[]) {
		return this.BigIntToBigNbr(Nbr).toString();
	}
}
