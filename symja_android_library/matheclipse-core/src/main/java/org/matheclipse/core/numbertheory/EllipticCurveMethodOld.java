//Elliptic Curve Method (ECM) Prime Factorization
//
//Written by Dario Alejandro Alpern (Buenos Aires - Argentina)
//Modified for the Symja project by Axel Kramer.
//
//Based in Yuji Kida's implementation for UBASIC interpreter
//
//No part of this code can be used for commercial purposes without
//the written consent from the author. Otherwise it can be used freely
//except that you have to write somewhere in the code this header.
//
//

package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.util.Map;

/**
 * <p>
 * Use Elliptic Curve Method to find the prime number factors of a given BigInteger.
 * </p>
 *
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Lenstra_elliptic_curve_factorization"> Wikipedia: Lenstra elliptic curve
 * factorization </a>
 * </p>
 */
public class EllipticCurveMethod {
	/**
	 * Initial capacity for the arrays which store the factors.
	 */
	private static int START_CAPACITY = 32;

	private static final int TYP_AURIF = 100000000;
	private static final int TYP_TABLE = 150000000;
	private static final int TYP_SIQS = 200000000;
	private static final int TYP_LEHMAN = 250000000;
	private static final int TYP_EC = 300000000;

	private static final BigInteger BigInt0 = BigInteger.ZERO;
	private static final BigInteger BigInt1 = BigInteger.ONE;
	private static final BigInteger BigInt2 = BigInteger.valueOf(2L);
	private static final BigInteger BigInt3 = BigInteger.valueOf(3L);
	private static final int PWmax = 32, Qmax = 30241, LEVELmax = 11;
	private static final int NLen = 1200;
	private static final int aiP[] = { 2, 3, 5, 7, 11, 13 };
	private static final int aiQ[] = { 2, 3, 5, 7, 13, 11, 31, 61, 19, 37, 181, 29, 43, 71, 127, 211, 421, 631, 41, 73,
			281, 2521, 17, 113, 241, 337, 1009, 109, 271, 379, 433, 541, 757, 2161, 7561, 15121, 23, 67, 89, 199, 331,
			397, 463, 617, 661, 881, 991, 1321, 2311, 2377, 2971, 3697, 4159, 4621, 8317, 9241, 16633, 18481, 23761,
			101, 151, 401, 601, 701, 1051, 1201, 1801, 2801, 3301, 3851, 4201, 4951, 6301, 9901, 11551, 12601, 14851,
			15401, 19801, 97, 353, 673, 2017, 3169, 3361, 5281, 7393, 21601, 30241, 53, 79, 131, 157, 313, 521, 547,
			859, 911, 937, 1093, 1171, 1249, 1301, 1873, 1951, 2003, 2081, 41, 2731, 2861, 3121, 3433, 3511, 5851, 6007,
			6553, 7151, 7723, 8009, 8191, 8581, 8737, 9829, 11701, 13729, 14561, 15601, 16381, 17551, 20021, 20593,
			21841, 24571, 25741, 26209, 28081 };
	private static final int aiG[] = { 1, 2, 2, 3, 2, 2, 3, 2, 2, 2, 2, 2, 3, 7, 3, 2, 2, 3, 6, 5, 3, 17, 3, 3, 7, 10,
			11, 6, 6, 2, 5, 2, 2, 23, 13, 11, 5, 2, 3, 3, 3, 5, 3, 3, 2, 3, 6, 13, 3, 5, 10, 5, 3, 2, 6, 13, 15, 13, 7,
			2, 6, 3, 7, 2, 7, 11, 11, 3, 6, 2, 11, 6, 10, 2, 7, 11, 2, 6, 13, 5, 3, 5, 5, 7, 22, 7, 5, 7, 11, 2, 3, 2,
			5, 10, 3, 2, 2, 17, 5, 5, 2, 7, 2, 10, 3, 5, 3, 7, 3, 2, 7, 5, 7, 2, 3, 10, 7, 3, 3, 17, 6, 5, 10, 6, 23, 6,
			23, 2, 3, 3, 5, 11, 7, 6, 11, 19 };
	private static final int aiNP[] = { 2, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6 };
	private static final int aiNQ[] = { 5, 8, 11, 18, 22, 27, 36, 59, 79, 89, 136 };
	private static final int aiT[] = { 2 * 2 * 3, 2 * 2 * 3 * 5, 2 * 2 * 3 * 3 * 5, 2 * 2 * 3 * 3 * 5 * 7,
			2 * 2 * 2 * 3 * 3 * 5 * 7, 2 * 2 * 2 * 2 * 3 * 3 * 5 * 7, 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 7,
			2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 7 * 11, 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 5 * 7 * 11,
			2 * 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 5 * 7 * 11, 2 * 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 5 * 7 * 11 * 13 };
	private static final long DosALa32 = (long) 1 << 32;
	private static final long DosALa31 = (long) 1 << 31;
	private static final double dDosALa31 = DosALa31;
	private static final double dDosALa62 = dDosALa31 * dDosALa31;
	private static final long Mi = 1000000000;
	/* ECM limits for 30, 35, ..., 85 digits */
	static final int limits[] = { 5, 8, 15, 25, 27, 32, 43, 70, 150, 300, 350, 600, 1500 };
	static final String[] expressionText = { "Number too low (less than 2).",
			"Number too high (more than 10000 digits).", "Intermediate expression too high (more than 20000 digits).",
			"Non-integer division.", "Parentheses mismatch.", "Syntax error.", "Too many parentheses.",
			"Invalid parameter." };
	/*********************************************************/
	/* Start of code "borrowed" from Paul Zimmermann's ECM4C */
	/*********************************************************/
	final private static int ADD = 6; /*
										 * number of multiplications in an addition
										 */
	final private static int DUP = 5; /*
										 * number of multiplications in a duplicate
										 */
	/**********************/

	/* Auxiliary routines */
	/**********************/

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

	/**
	 * Perform Lehman algorithm.
	 * 
	 * @param nbr
	 * @param k
	 * @return
	 */
	private static BigInteger Lehman(BigInteger nbr, int k) {
		long bitsSqr[] = { 0x0000000000000003l, // 3
				0x0000000000000013l, // 5
				0x0000000000000017l, // 7
				0x000000000000023Bl, // 11
				0x000000000000161Bl, // 13
				0x000000000001A317l, // 17
				0x0000000000030AF3l, // 19
				0x000000000005335Fl, // 23
				0x0000000013D122F3l, // 29
				0x00000000121D47B7l, // 31
				0x000000165E211E9Bl, // 37
				0x000001B382B50737l, // 41
				0x0000035883A3EE53l, // 43
				0x000004351B2753DFl, // 47
				0x0012DD703303AED3l, // 53
				0x022B62183E7B92BBl, // 59
				0x1713E6940A59F23Bl, // 61
		};
		int primes[] = { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61 };
		int nbrs[] = new int[17];
		int diffs[] = new int[17];
		int i, j, m;
		int intLog2N;
		double log2N;
		BigInteger root, rootN, dif, nextroot;
		BigInteger bM, a, c, r, sqr, val;
		if (nbr.testBit(0) == false) { // nbr Even
			r = BigInt0;
			m = 1;
			bM = BigInt1;
		} else {
			if (k % 2 == 0) { // k Even
				r = BigInt1;
				m = 2;
				bM = BigInt2;
			} else { // k Odd
				r = BigInteger.valueOf(k).add(nbr).and(BigInt3);
				m = 4;
				bM = BigInteger.valueOf(4);
			}
		}
		sqr = nbr.multiply(BigInteger.valueOf(k)).shiftLeft(2);
		intLog2N = sqr.bitLength() - 1;
		log2N = intLog2N + Math.log(sqr.shiftRight(intLog2N - 32).add(BigInt1).doubleValue()) / Math.log(2) - 32;
		log2N /= 2;
		if (log2N < 32) {
			root = BigInteger.valueOf((long) Math.exp(log2N * Math.log(2)));
		} else {
			intLog2N = (int) Math.floor(log2N) - 32;
			root = BigInteger.valueOf((long) Math.exp((log2N - intLog2N) * Math.log(2)) + 10).shiftLeft(intLog2N);
		}
		while (true) {
			rootN = root.multiply(root);
			dif = sqr.subtract(rootN);
			if (dif.signum() == 0) { // Perfect power
				break;
			}
			nextroot = dif.add(BigInt1).divide(BigInt2.multiply(root)).add(root).subtract(BigInt1);
			if (root.compareTo(nextroot) <= 0)
				break; // Not a perfect power
			root = nextroot;
		}
		a = root;
		while (a.mod(bM).equals(r) == false || a.multiply(a).compareTo(sqr) < 0) {
			a = a.add(BigInt1);
		}
		c = a.multiply(a).subtract(sqr);
		for (i = 0; i < 17; i++) {
			BigInteger prime = BigInteger.valueOf(primes[i]);
			nbrs[i] = c.mod(prime).intValue();
			diffs[i] = bM.multiply(a.shiftLeft(1).add(bM)).mod(prime).intValue();
		}
		for (j = 0; j < 10000; j++) {
			for (i = 0; i < 17; i++) {
				if ((bitsSqr[i] & (1l << nbrs[i])) == 0) {
					// Not a perfect square
					break;
				}
			}
			if (i == 17) { // Test for perfect square
				val = a.add(BigInteger.valueOf(m).multiply(BigInteger.valueOf(j)));
				c = val.multiply(val).subtract(sqr);
				intLog2N = c.bitLength() - 1;
				log2N = intLog2N + Math.log(c.shiftRight(intLog2N - 32).add(BigInt1).doubleValue()) / Math.log(2) - 32;
				log2N /= 2;
				if (log2N < 32) {
					root = BigInteger.valueOf((long) Math.exp(log2N * Math.log(2)));
				} else {
					intLog2N = (int) Math.floor(log2N) - 32;
					root = BigInteger.valueOf((long) Math.exp((log2N - intLog2N) * Math.log(2)) + 10)
							.shiftLeft(intLog2N);
				}
				while (true) {
					rootN = root.multiply(root);
					dif = c.subtract(rootN);
					if (dif.signum() == 0) { // Perfect power -> factor found
						root = nbr.gcd(val.add(root));
						if (root.compareTo(BigInteger.valueOf(10000)) > 0) {
							return root; // Return non-trivial found
						}
					}
					nextroot = dif.add(BigInt1).divide(BigInt2.multiply(root)).add(root).subtract(BigInt1);
					if (root.compareTo(nextroot) <= 0)
						break; // Not a perfect power
					root = nextroot;
				}
			}
			for (i = 0; i < 17; i++) {
				nbrs[i] = (nbrs[i] + diffs[i]) % primes[i];
				diffs[i] = (diffs[i] + 2 * m * m) % primes[i];
			}
		}
		return BigInt1; // Factor not found
	}

	/* returns the number of modular multiplications */
	private static int lucas_cost(int n, double v) {
		int c, d, e, r;

		d = n;
		r = (int) (d / v + 0.5);
		if (r >= n)
			return (ADD * n);
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

	private final BigInteger inputNumber;

	private boolean onlyFactoring = true;
	private int digitsInGroup = 6;
	private BigInteger PD[] = new BigInteger[START_CAPACITY]; // and prime
																// factors
	private int Exp[] = new int[START_CAPACITY];
	private int Typ[] = new int[START_CAPACITY];
	private BigInteger PD1[] = new BigInteger[START_CAPACITY];
	private int Exp1[] = new int[START_CAPACITY];
	private int Typ1[] = new int[START_CAPACITY];
	private boolean foundByLehman;
	private final int aiIndx[] = new int[Qmax];
	private final int aiF[] = new int[Qmax];
	private final int aiInv[] = new int[PWmax];
	private final long biTmp[] = new long[NLen];
	private final long biExp[] = new long[NLen];
	private final long biN[] = new long[NLen];
	private final long biR[] = new long[NLen];
	private final long biS[] = new long[NLen];
	private final long biT[] = new long[NLen];
	private final long aiJS[][] = new long[PWmax][NLen];
	private final long aiJW[][] = new long[PWmax][NLen];
	private final long aiJX[][] = new long[PWmax][NLen];
	private final long aiJ0[][] = new long[PWmax][NLen];
	private final long aiJ1[][] = new long[PWmax][NLen];
	private final long aiJ2[][] = new long[PWmax][NLen];
	private final long aiJ00[][] = new long[PWmax][NLen];
	private final long aiJ01[][] = new long[PWmax][NLen];

	/**
	 * Length of multiple precision numbers.
	 */
	private int NumberLength;
	private int fCapacity;
	private int NbrFactors, NbrFactors1;

	/**
	 * Elliptic Curve number
	 */
	private int EC;

	/**
	 * Used inside GCD calculations in multiple precision numbers
	 */
	private final long CalcAuxGcdU[] = new long[NLen];
	private final long CalcAuxGcdV[] = new long[NLen];
	private final long CalcAuxGcdT[] = new long[NLen];
	private long TestNbr[] = new long[NLen];
	private final long GcdAccumulated[] = new long[NLen];
	private long[] fieldAA, fieldTX, fieldTZ, fieldUX, fieldUZ;
	private long[] fieldAux1, fieldAux2, fieldAux3, fieldAux4;
	private double dN;
	private final long BigNbr1[] = new long[NLen];
	private final int SmallPrime[] = new int[670]; /* Primes < 5000 */
	private final long MontgomeryMultR1[] = new long[NLen];
	private final long MontgomeryMultR2[] = new long[NLen];
	private final long MontgomeryMultAfterInv[] = new long[NLen];
	private long MontgomeryMultN;
	private BigInteger NumberToFactor;
	private boolean batchFinished = true;
	private boolean batchPrime = false;
	private boolean TerminateThread = true;
	private int NextEC = -1;
	private int indexM, maxIndexM;
	private BigInteger Quad1, Quad2, Quad3, Quad4;
	private boolean Computing3Squares;

	public EllipticCurveMethod(BigInteger nn) {
		fCapacity = START_CAPACITY;
		inputNumber = nn;
		BigNbrToBigInt(nn);
	}

	/**
	 * Adds Q=(x2:z2) and R=(x1:z1) and puts the result in (x3:z3), using 5/6 mul, 6 add/sub and 6 mod. One assumes that
	 * Q-R=P or R-Q=P where P=(x:z). Uses the following global variables: - n : number to factor - x, z : coordinates of
	 * P - u, v, w : auxiliary variables Modifies: x3, z3, u, v, w. (x3,z3) may be identical to (x2,z2) and to (x,z)
	 */
	private void add3(long[] x3, long[] z3, long[] x2, long[] z2, long[] x1, long[] z1, long[] x, long[] z) {
		long[] t = fieldTX;
		long[] u = fieldTZ;
		long[] v = fieldUX;
		long[] w = fieldUZ;
		SubtractBigNbrModN(x2, z2, v); // v = x2-z2
		AddBigNbrModN(x1, z1, w); // w = x1+z1
		MontgomeryMult(v, w, u); // u = (x2-z2)*(x1+z1)
		AddBigNbrModN(x2, z2, w); // w = x2+z2
		SubtractBigNbrModN(x1, z1, t); // t = x1-z1
		MontgomeryMult(t, w, v); // v = (x2+z2)*(x1-z1)
		AddBigNbrModN(u, v, t); // t = 2*(x1*x2-z1*z2)
		MontgomeryMult(t, t, w); // w = 4*(x1*x2-z1*z2)^2
		SubtractBigNbrModN(u, v, t); // t = 2*(x2*z1-x1*z2)
		MontgomeryMult(t, t, v); // v = 4*(x2*z1-x1*z2)^2
		if (BigNbrAreEqual(x, x3)) {
			System.arraycopy(x, 0, u, 0, NumberLength);
			System.arraycopy(w, 0, t, 0, NumberLength);
			MontgomeryMult(z, t, w);
			MontgomeryMult(v, u, z3);
			System.arraycopy(w, 0, x3, 0, NumberLength);
		} else {
			MontgomeryMult(w, z, x3); // x3 = 4*z*(x1*x2-z1*z2)^2
			MontgomeryMult(x, v, z3); // z3 = 4*x*(x2*z1-x1*z2)^2
		}
	}

	private void AddBigNbr(long Nbr1[], long Nbr2[], long Sum[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) + Nbr1[i] + Nbr2[i];
			Sum[i] = Cy & 0x7FFFFFFFl;
		}
	}

	private void AddBigNbr32(long Nbr1[], long Nbr2[], long Sum[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 32) + Nbr1[i] + Nbr2[i];
			Sum[i] = Cy & 0xFFFFFFFFl;
		}
	}

	private void AddBigNbrModN(long Nbr1[], long Nbr2[], long Sum[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
		long Cy = 0;
		int i;

		for (i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) + Nbr1[i] + Nbr2[i] - TestNbr[i];
			Sum[i] = Cy & MaxUInt;
		}
		if (Cy < 0) {
			Cy = 0;
			for (i = 0; i < NumberLength; i++) {
				Cy = (Cy >> 31) + Sum[i] + TestNbr[i];
				Sum[i] = Cy & MaxUInt;
			}
		}
	}

	private void AdjustModN(long Nbr[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
		long TrialQuotient;
		long Cy;
		int i;
		double dAux;

		dAux = Nbr[NumberLength] * dDosALa31 + Nbr[NumberLength - 1];
		if (NumberLength > 1) {
			dAux += Nbr[NumberLength - 2] / dDosALa31;
		}
		TrialQuotient = (long) Math.ceil(dAux / dN) + 2;
		if (TrialQuotient >= DosALa32) {
			Cy = 0;
			for (i = 0; i < NumberLength; i++) {
				Cy = Nbr[i + 1] - (TrialQuotient >>> 31) * TestNbr[i] - Cy;
				Nbr[i + 1] = Cy & MaxUInt;
				Cy = (MaxUInt - Cy) >>> 31;
			}
			TrialQuotient &= MaxUInt;
		}
		Cy = 0;
		for (i = 0; i < NumberLength; i++) {
			Cy = Nbr[i] - TrialQuotient * TestNbr[i] - Cy;
			Nbr[i] = Cy & MaxUInt;
			Cy = (MaxUInt - Cy) >>> 31;
		}
		Nbr[NumberLength] -= Cy;
		while ((Nbr[NumberLength] & MaxUInt) != 0) {
			Cy = 0;
			for (i = 0; i < NumberLength; i++) {
				Cy += Nbr[i] + TestNbr[i];
				Nbr[i] = Cy & MaxUInt;
				Cy >>= 31;
			}
			Nbr[NumberLength] += Cy;
		}
	}

	/**
	 * Prime checking routine
	 * 
	 * @param N
	 * @return 0 = Number is prime. 1 = Number is composite.
	 */
	private int AprtCle(BigInteger N) {
		int i, j, G, H, I, J, K, P, Q, T, U, W, X;
		int IV, InvX, LEVELnow, NP, PK, PL, PM, SW, VK, TestedQs, TestingQs;
		int QQ, T1, T3, U1, U3, V1, V3;
		int LengthN, LengthS;
		long Mask;
		double dS;

		// System.out.println("Starting Prime Check routine.");
		BigNbrToBigInt(N);
		GetMontgomeryParms();
		// if (Computing3Squares == false) {
		// }
		j = PK = PL = PM = 0;
		for (I = 0; I < NumberLength; I++) {
			biS[I] = 0;
			for (J = 0; J < PWmax; J++) {
				aiJX[J][I] = 0;
			}
		}
		GetPrimes2Test: for (i = 0; i < LEVELmax; i++) {
			biS[0] = 2;
			for (I = 1; I < NumberLength; I++) {
				biS[I] = 0;
			}
			for (j = 0; j < aiNQ[i]; j++) {
				Q = aiQ[j];
				U = aiT[i] * Q;
				do {
					U /= Q;
					MultBigNbrByLong(biS, Q, biS);
				} while (U % Q == 0);

				// Exit loop if S^2 > N.

				if (CompareSquare(biS, TestNbr) > 0) {
					break GetPrimes2Test;
				}
			} /* End for j */
		} /* End for i */
		if (i == LEVELmax) { /* too big */
			return ProbabilisticPrimeTest(N);
		}
		LEVELnow = i;
		TestingQs = j;
		T = aiT[LEVELnow];
		NP = aiNP[LEVELnow];

		MainStart: do {
			for (i = 0; i < NP; i++) {
				P = aiP[i];
				SW = TestedQs = 0;
				Q = W = (int) BigNbrModLong(TestNbr, ((long)P) * ((long)P));
				for (J = P - 2; J > 0; J--) {
					W = (W * Q) % (P * P);
				}
				if (P > 2 && W != 1) {
					SW = 1;
				}
				do {
					for (j = TestedQs; j <= TestingQs; j++) {
						Q = aiQ[j] - 1;
						G = aiG[j];
						K = 0;
						while (Q % P == 0) {
							K++;
							Q /= P;
						}
						Q = aiQ[j];
						if (K == 0) {
							continue;
						}
						if (Computing3Squares == false) {
							/*
							 * System.out.println( primalityString + "P = " + P + ",  Q = " + Q + "  (" + (i *
							 * (TestingQs + 1) + j) * 100 / (NP * (TestingQs + 1)) + "%)");
							 */
						}
						PM = 1;
						for (I = 1; I < K; I++) {
							PM = PM * P;
						}
						PL = (P - 1) * PM;
						PK = P * PM;
						J = 1;
						for (I = 1; I < Q; I++) {
							J = J * G % Q;
							aiIndx[J] = I;
						}
						J = 1;
						for (I = 1; I <= Q - 2; I++) {
							J = J * G % Q;
							aiF[I] = aiIndx[(Q + 1 - J) % Q];
						}
						for (I = 0; I < PK; I++) {
							for (J = 0; J < NumberLength; J++) {
								aiJ0[I][J] = aiJ1[I][J] = 0;
							}
						}
						if (P > 2) {
							JacobiSum(1, 1, P, PK, PL, PM, Q);
						} else {
							if (K != 1) {
								JacobiSum(1, 1, P, PK, PL, PM, Q);
								for (I = 0; I < PK; I++) {
									for (J = 0; J < NumberLength; J++) {
										aiJW[I][J] = 0;
									}
								}
								if (K != 2) {
									for (I = 0; I < PM; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJW[I][J] = aiJ0[I][J];
										}
									}
									JacobiSum(2, 1, P, PK, PL, PM, Q);
									for (I = 0; I < PM; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJS[I][J] = aiJ0[I][J];
										}
									}
									JS_JW(PK, PL, PM, P);
									NormalizeJS(PK, PL, PM, P);
									for (I = 0; I < PM; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJ1[I][J] = aiJS[I][J];
										}
									}
									JacobiSum(3 << (K - 3), 1 << (K - 3), P, PK, PL, PM, Q);
									for (J = 0; J < NumberLength; J++) {
										for (I = 0; I < PK; I++) {
											aiJW[I][J] = 0;
										}
										for (I = 0; I < PM; I++) {
											aiJS[I][J] = aiJ0[I][J];
										}
									}
									JS_2(PK, PL, PM, P);
									NormalizeJS(PK, PL, PM, P);
									for (I = 0; I < PM; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJ2[I][J] = aiJS[I][J];
										}
									}
								}
							}
						}
						for (J = 0; J < NumberLength; J++) {
							aiJ00[0][J] = aiJ01[0][J] = MontgomeryMultR1[J];
							for (I = 1; I < PK; I++) {
								aiJ00[I][J] = aiJ01[I][J] = 0;
							}
						}
						VK = (int) BigNbrModLong(TestNbr, PK);
						for (I = 1; I < PK; I++) {
							if (I % P != 0) {
								U1 = 1;
								U3 = I;
								V1 = 0;
								V3 = PK;
								while (V3 != 0) {
									QQ = U3 / V3;
									T1 = U1 - V1 * QQ;
									T3 = U3 - V3 * QQ;
									U1 = V1;
									U3 = V3;
									V1 = T1;
									V3 = T3;
								}
								aiInv[I] = (U1 + PK) % PK;
							} else {
								aiInv[I] = 0;
							}
						}
						if (P != 2) {
							for (IV = 0; IV <= 1; IV++) {
								for (X = 1; X < PK; X++) {
									for (I = 0; I < PK; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJS[I][J] = aiJ0[I][J];
										}
									}
									if (X % P == 0) {
										continue;
									}
									if (IV == 0) {
										LongToBigNbr(X, biExp);
									} else {
										LongToBigNbr(((long)VK) * ((long)X) / ((long)PK), biExp);
										if (VK * X / PK == 0) {
											continue;
										}
									}
									JS_E(PK, PL, PM, P);
									for (I = 0; I < PK; I++) {
										for (J = 0; J < NumberLength; J++) {
											aiJW[I][J] = 0;
										}
									}
									InvX = aiInv[X];
									for (I = 0; I < PK; I++) {
										J = I * InvX % PK;
										AddBigNbrModN(aiJW[J], aiJS[I], aiJW[J]);
									}
									NormalizeJW(PK, PL, PM, P);
									if (IV == 0) {
										for (I = 0; I < PK; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJS[I][J] = aiJ00[I][J];
											}
										}
									} else {
										for (I = 0; I < PK; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJS[I][J] = aiJ01[I][J];
											}
										}
									}
									JS_JW(PK, PL, PM, P);
									if (IV == 0) {
										for (I = 0; I < PK; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJ00[I][J] = aiJS[I][J];
											}
										}
									} else {
										for (I = 0; I < PK; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJ01[I][J] = aiJS[I][J];
											}
										}
									}
								} /* end for X */
							} /* end for IV */
						} else {
							if (K == 1) {
								MultBigNbrByLongModN(MontgomeryMultR1, Q, aiJ00[0]);
								for (J = 0; J < NumberLength; J++) {
									aiJ01[0][J] = MontgomeryMultR1[J];
								}
							} else {
								if (K == 2) {
									if (VK == 1) {
										for (J = 0; J < NumberLength; J++) {
											aiJ01[0][J] = MontgomeryMultR1[J];
										}
									}
									for (J = 0; J < NumberLength; J++) {
										aiJS[0][J] = aiJ0[0][J];
										aiJS[1][J] = aiJ0[1][J];
									}
									JS_2(PK, PL, PM, P);
									if (VK == 3) {
										for (J = 0; J < NumberLength; J++) {
											aiJ01[0][J] = aiJS[0][J];
											aiJ01[1][J] = aiJS[1][J];
										}
									}
									MultBigNbrByLongModN(aiJS[0], Q, aiJ00[0]);
									MultBigNbrByLongModN(aiJS[1], Q, aiJ00[1]);
								} else {
									for (IV = 0; IV <= 1; IV++) {
										for (X = 1; X < PK; X += 2) {
											for (I = 0; I <= PM; I++) {
												for (J = 0; J < NumberLength; J++) {
													aiJS[I][J] = aiJ1[I][J];
												}
											}
											if (X % 8 == 5 || X % 8 == 7) {
												continue;
											}
											if (IV == 0) {
												LongToBigNbr(X, biExp);
											} else {
												LongToBigNbr(((long)VK) * ((long)X) / ((long)PK), biExp);
												if (VK * X / PK == 0) {
													continue;
												}
											}
											JS_E(PK, PL, PM, P);
											for (I = 0; I < PK; I++) {
												for (J = 0; J < NumberLength; J++) {
													aiJW[I][J] = 0;
												}
											}
											InvX = aiInv[X];
											for (I = 0; I < PK; I++) {
												J = I * InvX % PK;
												AddBigNbrModN(aiJW[J], aiJS[I], aiJW[J]);
											}
											NormalizeJW(PK, PL, PM, P);
											if (IV == 0) {
												for (I = 0; I < PK; I++) {
													for (J = 0; J < NumberLength; J++) {
														aiJS[I][J] = aiJ00[I][J];
													}
												}
											} else {
												for (I = 0; I < PK; I++) {
													for (J = 0; J < NumberLength; J++) {
														aiJS[I][J] = aiJ01[I][J];
													}
												}
											}
											NormalizeJS(PK, PL, PM, P);
											JS_JW(PK, PL, PM, P);
											if (IV == 0) {
												for (I = 0; I < PK; I++) {
													for (J = 0; J < NumberLength; J++) {
														aiJ00[I][J] = aiJS[I][J];
													}
												}
											} else {
												for (I = 0; I < PK; I++) {
													for (J = 0; J < NumberLength; J++) {
														aiJ01[I][J] = aiJS[I][J];
													}
												}
											}
										} /* end for X */
										if (IV == 0 || VK % 8 == 1 || VK % 8 == 3) {
											continue;
										}
										for (I = 0; I < PM; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJW[I][J] = aiJ2[I][J];
												aiJS[I][J] = aiJ01[I][J];
											}
										}
										for (; I < PK; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJW[I][J] = aiJS[I][J] = 0;
											}
										}
										JS_JW(PK, PL, PM, P);
										for (I = 0; I < PM; I++) {
											for (J = 0; J < NumberLength; J++) {
												aiJ01[I][J] = aiJS[I][J];
											}
										}
									} /* end for IV */
								}
							}
						}
						for (I = 0; I < PL; I++) {
							for (J = 0; J < NumberLength; J++) {
								aiJS[I][J] = aiJ00[I][J];
							}
						}
						for (; I < PK; I++) {
							for (J = 0; J < NumberLength; J++) {
								aiJS[I][J] = 0;
							}
						}
						DivBigNbrByLong(TestNbr, PK, biExp);
						JS_E(PK, PL, PM, P);
						for (I = 0; I < PK; I++) {
							for (J = 0; J < NumberLength; J++) {
								aiJW[I][J] = 0;
							}
						}
						for (I = 0; I < PL; I++) {
							for (J = 0; J < PL; J++) {
								MontgomeryMult(aiJS[I], aiJ01[J], biTmp);
								AddBigNbrModN(biTmp, aiJW[(I + J) % PK], aiJW[(I + J) % PK]);
							}
						}
						NormalizeJW(PK, PL, PM, P);
						MatchingRoot: do {
							H = -1;
							W = 0;
							for (I = 0; I < PL; I++) {
								if (BigNbrIsZero(aiJW[I]) == false) {
									if (H == -1 && BigNbrAreEqual(aiJW[I], MontgomeryMultR1) == true) {
										H = I;
									} else {
										H = -2;
										AddBigNbrModN(aiJW[I], MontgomeryMultR1, biTmp);
										if (BigNbrIsZero(biTmp)) {
											W++;
										}
									}
								}
							}
							if (H >= 0) {
								break MatchingRoot;
							}
							if (W != P - 1) {
								return 1; /* Not prime */
							}
							for (I = 0; I < PM; I++) {
								AddBigNbrModN(aiJW[I], MontgomeryMultR1, biTmp);
								if (BigNbrIsZero(biTmp) == true) {
									break;
								}
							}
							if (I == PM) {
								return 1; /* Not prime */
							}
							for (J = 1; J <= P - 2; J++) {
								AddBigNbrModN(aiJW[I + J * PM], MontgomeryMultR1, biTmp);
								if (BigNbrIsZero(biTmp) == false) {
									return 1; /* Not prime */
								}
							}
							H = I + PL;
						} while (false);
						if (SW == 1 || H % P == 0) {
							continue;
						}
						if (P != 2) {
							SW = 1;
							continue;
						}
						if (K == 1) {
							if ((TestNbr[0] & 3) == 1) {
								SW = 1;
							}
							continue;
						}

						// if (Q^((N-1)/2) mod N != N-1), N is not prime.

						MultBigNbrByLongModN(MontgomeryMultR1, Q, biTmp);
						for (I = 0; I < NumberLength; I++) {
							biR[I] = biTmp[I];
						}
						I = NumberLength - 1;
						Mask = 0x40000000l;
						while ((TestNbr[I] & Mask) == 0) {
							Mask /= 2;
							if (Mask == 0) {
								I--;
								Mask = 0x40000000l;
							}
						}
						do {
							Mask /= 2;
							if (Mask == 0) {
								I--;
								Mask = 0x40000000l;
							}
							MontgomeryMult(biR, biR, biT);
							for (J = 0; J < NumberLength; J++) {
								biR[J] = biT[J];
							}
							if ((TestNbr[I] & Mask) != 0) {
								MontgomeryMult(biR, biTmp, biT);
								for (J = 0; J < NumberLength; J++) {
									biR[J] = biT[J];
								}
							}
						} while (I > 0 || Mask > 2);
						AddBigNbrModN(biR, MontgomeryMultR1, biTmp);
						if (BigNbrIsZero(biTmp) == false) {
							return 1; /* Not prime */
						}
						SW = 1;
					} /* end for j */
					if (SW == 0) {
						TestedQs = TestingQs + 1;
						if (TestingQs < aiNQ[LEVELnow] - 1) {
							TestingQs++;
							Q = aiQ[TestingQs];
							U = T * Q;
							do {
								MultBigNbrByLong(biS, Q, biS);
								U /= Q;
							} while (U % Q == 0);
							continue; /* Retry */
						}
						LEVELnow++;
						if (LEVELnow == LEVELmax) {
							return ProbabilisticPrimeTest(N); /* Cannot tell */
						}
						T = aiT[LEVELnow];
						NP = aiNP[LEVELnow];
						biS[0] = 2;
						for (J = 1; J < NumberLength; J++) {
							biS[J] = 0;
						}
						for (J = 0; J <= aiNQ[LEVELnow]; J++) {
							Q = aiQ[J];
							U = T * Q;
							do {
								MultBigNbrByLong(biS, Q, biS);
								U /= Q;
							} while (U % Q == 0);
							if (CompareSquare(biS, TestNbr) > 0) {
								TestingQs = J;
								continue MainStart; /*
													 * Retry from the beginning
													 */
							}
						} /* end for J */
						return ProbabilisticPrimeTest(N); /* Program error */
					} /* end if */
					break;
				} while (true); /* end do */
			} /* end for i */

			// Final Test

			LengthN = NumberLength;
			for (I = 0; I < NumberLength; I++) {
				biN[I] = TestNbr[I];
				TestNbr[I] = biS[I];
				biR[I] = 0;
			}
			while (true) {
				if (TestNbr[NumberLength - 1] != 0) {
					break;
				}
				NumberLength--;
			}
			dN = TestNbr[NumberLength - 1];
			if (NumberLength > 1) {
				dN += TestNbr[NumberLength - 2] / dDosALa31;
			}
			if (NumberLength > 2) {
				dN += TestNbr[NumberLength - 3] / dDosALa62;
			}
			LengthS = NumberLength;
			dS = dN;
			MontgomeryMultR1[0] = 1;
			for (I = 1; I < NumberLength; I++) {
				MontgomeryMultR1[I] = 0;
			}

			biR[0] = 1;
			BigNbrModN(biN, LengthN, biT); /* Compute N mod S */
			for (J = 1; J <= T; J++) {
				MultBigNbrModN(biR, biT, biTmp);
				for (i = NumberLength - 1; i > 0; i--) {
					if (biTmp[i] != 0) {
						break;
					}
				}
				if (i == 0 && biTmp[0] != 1) {
					return 0; /* Number is prime */
				}
				while (true) {
					if (biTmp[NumberLength - 1] != 0) {
						break;
					}
					NumberLength--;
				}
				for (I = 0; I < NumberLength; I++) {
					TestNbr[I] = biTmp[I];
				}
				dN = TestNbr[NumberLength - 1];
				if (NumberLength > 1) {
					dN += TestNbr[NumberLength - 2] / dDosALa31;
				}
				if (NumberLength > 2) {
					dN += TestNbr[NumberLength - 3] / dDosALa62;
				}
				for (i = NumberLength - 1; i > 0; i--) {
					if (TestNbr[i] != biTmp[i]) {
						break;
					}
				}
				if (TestNbr[i] > biTmp[i]) {
					BigNbrModN(biN, LengthN, biTmp); /* Compute N mod R */
					if (BigNbrIsZero(biTmp) == true) { /* If N is multiple of R.. */
						return 1; /* Number is composite */
					}
				}
				dN = dS;
				NumberLength = LengthS;
				for (I = 0; I < NumberLength; I++) {
					biR[I] = TestNbr[I];
					TestNbr[I] = biS[I];
				}
			} /* End for J */
			return 0; /* Number is prime */
		} while (true);
	}

	// Perform JS <- JS * JW

	private BigInteger BigIntToBigNbr(long[] GD) {
		byte[] Result;
		long[] Temp;
		int i, NL;
		long digit; // , MaxUInt = 0xFFFFFFFFl;

		Temp = new long[NumberLength];
		Convert31To32Bits(GD, Temp);
		NL = NumberLength * 4;
		Result = new byte[NL];
		for (i = 0; i < NumberLength; i++) {
			digit = Temp[i];
			Result[NL - 1 - 4 * i] = (byte) (digit & 0xFF);
			Result[NL - 2 - 4 * i] = (byte) (digit / 0x100 & 0xFF);
			Result[NL - 3 - 4 * i] = (byte) (digit / 0x10000 & 0xFF);
			Result[NL - 4 - 4 * i] = (byte) (digit / 0x1000000 & 0xFF);
		}
		return (new BigInteger(Result));
	}

	// Perform JS <- JS ^ 2

	private boolean BigNbrAreEqual(long Nbr1[], long Nbr2[]) {
		for (int i = 0; i < NumberLength; i++) {
			if (Nbr1[i] != Nbr2[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean BigNbrIsZero(long Nbr[]) {
		for (int i = 0; i < NumberLength; i++) {
			if (Nbr[i] != 0) {
				return false;
			}
		}
		return true;
	}

	private long BigNbrModLong(long Nbr1[], long Nbr2) {
		int i;
		long Rem = 0;

		for (i = NumberLength - 1; i >= 0; i--) {
			Rem = ((Rem << 31) + Nbr1[i]) % Nbr2;
		}
		return Rem;
	}

	private void BigNbrModN(long Nbr[], int Length, long Mod[]) {
		int i, j;
		for (i = 0; i < NumberLength; i++) {
			Mod[i] = Nbr[i + Length - NumberLength];
		}
		Mod[i] = 0;
		AdjustModN(Mod);
		for (i = Length - NumberLength - 1; i >= 0; i--) {
			for (j = NumberLength; j > 0; j--) {
				Mod[j] = Mod[j - 1];
			}
			Mod[0] = Nbr[i];
			AdjustModN(Mod);
		}
	}

	public void BigNbrToBigInt(BigInteger N) {
		byte[] Result;
		long[] Temp;
		int i, j;
		long mask, p;

		Result = N.toByteArray();
		NumberLength = (Result.length * 8 + 30) / 31;
		Temp = new long[NumberLength + 1];
		j = 0;
		mask = 1;
		p = 0;
		for (i = Result.length - 1; i >= 0; i--) {
			p += mask * (Result[i] >= 0 ? Result[i] : Result[i] + 256);
			mask *= 0x100;
			if (mask == DosALa32) {
				Temp[j++] = p;
				mask = 1;
				p = 0;
			}
		}
		Temp[j] = p;
		Convert32To31Bits(Temp, TestNbr);
		if (TestNbr[NumberLength - 1] > Mi) {
			TestNbr[NumberLength] = 0;
			NumberLength++;
		}
		TestNbr[NumberLength] = 0;
	}

	private void ChSignBigNbr(long Nbr[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) - Nbr[i];
			Nbr[i] = Cy & 0x7FFFFFFFl;
		}
	}

	/**
	 * Compare Nbr1^2 vs. Nbr2
	 * 
	 * @param Nbr1
	 * @param Nbr2
	 * @return
	 */
	private int CompareSquare(long Nbr1[], long Nbr2[]) {
		int I, k;

		for (I = NumberLength - 1; I > 0; I--) {
			if (Nbr1[I] != 0) {
				break;
			}
		}
		k = NumberLength / 2;
		if (NumberLength % 2 == 0) {
			if (I >= k) {
				return 1;
			} // Nbr1^2 > Nbr2
			if (I < k - 1 || biS[k - 1] < 65536) {
				return -1;
			} // Nbr1^2 < Nbr2
		} else {
			if (I < k) {
				return -1;
			} // Nbr1^2 < Nbr2
			if (I > k || biS[k] >= 65536) {
				return 1;
			} // Nbr1^2 > Nbr2
		}
		MultBigNbr(biS, biS, biTmp);
		SubtractBigNbr(biTmp, TestNbr, biTmp);
		if (BigNbrIsZero(biTmp) == true) {
			return 0;
		} // Nbr1^2 == Nbr2
		if (biTmp[NumberLength - 1] >= 0) {
			return 1;
		} // Nbr1^2 > Nbr2
		return -1; // Nbr1^2 < Nbr2
	}

	private boolean ComputeFourSquares(BigInteger PD[], int Exp[]) {
		if (onlyFactoring) {
			int indexPrimes;
			BigInteger p, q, K, Mult1, Mult2, Mult3, Mult4;
			BigInteger Tmp, Tmp1, Tmp2, Tmp3, M1, M2, M3, M4;

			Quad1 = BigInt1; /* 1 = 1^2 + 0^2 + 0^2 + 0^2 */
			Quad2 = BigInt0;
			Quad3 = BigInt0;
			Quad4 = BigInt0;
			for (indexPrimes = NbrFactors - 1; indexPrimes >= 0; indexPrimes--) {
				if (Exp[indexPrimes] % 2 == 0) {
					continue;
				}
				p = PD[indexPrimes];
				q = p.subtract(BigInt1); /* q = p-1 */
				if (p.equals(BigInt2)) {
					Mult1 = BigInt1; /* 2 = 1^2 + 1^2 + 0^2 + 0^2 */
					Mult2 = BigInt1;
					Mult3 = BigInt0;
					Mult4 = BigInt0;
				} else { /* Prime not 2 */
					if (p.testBit(1) == false) { /* if p = 1 (mod 4) */
						K = BigInt1;
						do { // Compute Mult1 = sqrt(-1) mod p
							K = K.add(BigInt1);
							Mult1 = K.modPow(q.shiftRight(2), p);
						} while (Mult1.equals(BigInt1) || Mult1.equals(q));
						if (Mult1.multiply(Mult1).mod(p).equals(q) == false) {
							return false; /* The number is not prime */
						}
						Mult2 = BigInt1;
						while (true) {
							K = Mult1.multiply(Mult1).add(Mult2.multiply(Mult2)).divide(p);
							if (K.equals(BigInt1)) {
								Mult3 = BigInt0;
								Mult4 = BigInt0;
								break;
							}
							if (p.mod(K).signum() == 0) {
								return false; /* The number is not prime */
							}
							M1 = Mult1.mod(K);
							M2 = Mult2.mod(K);
							if (M1.compareTo(K.shiftRight(1)) > 0) {
								M1 = M1.subtract(K);
							}
							if (M2.compareTo(K.shiftRight(1)) > 0) {
								M2 = M2.subtract(K);
							}
							Tmp = Mult1.multiply(M1).add(Mult2.multiply(M2)).divide(K);
							Mult2 = Mult1.multiply(M2).subtract(Mult2.multiply(M1)).divide(K);
							Mult1 = Tmp;
						} /* end while */
					} /* end p = 1 (mod 4) */
					else { /* if p = 3 (mod 4) */
						// Compute Mult1 and Mult2 so Mult1^2 + Mult2^2 = -1
						// (mod p)
						Mult1 = BigInt0;
						do {
							Mult1 = Mult1.add(BigInt1);
						} while (BigInt1.negate().subtract(Mult1.multiply(Mult1)).modPow(q.shiftRight(1), p)
								.compareTo(BigInt1) > 0);
						Mult2 = BigInt1.negate().subtract(Mult1.multiply(Mult1)).modPow(p.add(BigInt1).shiftRight(2),
								p);
						Mult3 = BigInt1;
						Mult4 = BigInt0;
						while (true) {
							K = Mult1.multiply(Mult1).add(Mult2.multiply(Mult2)).add(Mult3.multiply(Mult3))
									.add(Mult4.multiply(Mult4)).divide(p);
							if (K.equals(BigInt1)) {
								break;
							}
							if (K.testBit(0) == false) { // If K is even ...
								if (Mult1.add(Mult2).testBit(0)) {
									if (Mult1.add(Mult3).testBit(0) == false) {
										Tmp = Mult2;
										Mult2 = Mult3;
										Mult3 = Tmp;
									} else {
										Tmp = Mult2;
										Mult2 = Mult4;
										Mult4 = Tmp;
									}
								} // At this moment Mult1+Mult2 = even,
									// Mult3+Mult4 = even
								Tmp1 = Mult1.add(Mult2).shiftRight(1);
								Tmp2 = Mult1.subtract(Mult2).shiftRight(1);
								Tmp3 = Mult3.add(Mult4).shiftRight(1);
								Mult4 = Mult3.subtract(Mult4).shiftRight(1);
								Mult3 = Tmp3;
								Mult2 = Tmp2;
								Mult1 = Tmp1;
								continue;
							} /* end if k is even */
							M1 = Mult1.mod(K);
							M2 = Mult2.mod(K);
							M3 = Mult3.mod(K);
							M4 = Mult4.mod(K);
							if (M1.compareTo(K.shiftRight(1)) > 0) {
								M1 = M1.subtract(K);
							}
							if (M2.compareTo(K.shiftRight(1)) > 0) {
								M2 = M2.subtract(K);
							}
							if (M3.compareTo(K.shiftRight(1)) > 0) {
								M3 = M3.subtract(K);
							}
							if (M4.compareTo(K.shiftRight(1)) > 0) {
								M4 = M4.subtract(K);
							}
							Tmp1 = Mult1.multiply(M1).add(Mult2.multiply(M2)).add(Mult3.multiply(M3))
									.add(Mult4.multiply(M4)).divide(K);
							Tmp2 = Mult1.multiply(M2).subtract(Mult2.multiply(M1)).add(Mult3.multiply(M4))
									.subtract(Mult4.multiply(M3)).divide(K);
							Tmp3 = Mult1.multiply(M3).subtract(Mult3.multiply(M1)).subtract(Mult2.multiply(M4))
									.add(Mult4.multiply(M2)).divide(K);
							Mult4 = Mult1.multiply(M4).subtract(Mult4.multiply(M1)).add(Mult2.multiply(M3))
									.subtract(Mult3.multiply(M2)).divide(K);
							Mult3 = Tmp3;
							Mult2 = Tmp2;
							Mult1 = Tmp1;
						} /* end while */
					} /* end if p = 3 (mod 4) */
				} /* end prime not 2 */
				Tmp1 = Mult1.multiply(Quad1).add(Mult2.multiply(Quad2)).add(Mult3.multiply(Quad3))
						.add(Mult4.multiply(Quad4));
				Tmp2 = Mult1.multiply(Quad2).subtract(Mult2.multiply(Quad1)).add(Mult3.multiply(Quad4))
						.subtract(Mult4.multiply(Quad3));
				Tmp3 = Mult1.multiply(Quad3).subtract(Mult3.multiply(Quad1)).subtract(Mult2.multiply(Quad4))
						.add(Mult4.multiply(Quad2));
				Quad4 = Mult1.multiply(Quad4).subtract(Mult4.multiply(Quad1)).add(Mult2.multiply(Quad3))
						.subtract(Mult3.multiply(Quad2));
				Quad3 = Tmp3;
				Quad2 = Tmp2;
				Quad1 = Tmp1;
			} /* end for indexPrimes */
			for (indexPrimes = 0; indexPrimes < NbrFactors; indexPrimes++) {
				p = PD[indexPrimes].pow(Exp[indexPrimes] / 2);
				Quad1 = Quad1.multiply(p);
				Quad2 = Quad2.multiply(p);
				Quad3 = Quad3.multiply(p);
				Quad4 = Quad4.multiply(p);
			}
			Quad1 = Quad1.abs();
			Quad2 = Quad2.abs();
			Quad3 = Quad3.abs();
			Quad4 = Quad4.abs();
			// Sort squares
			if (Quad1.compareTo(Quad2) < 0) {
				Tmp = Quad1;
				Quad1 = Quad2;
				Quad2 = Tmp;
			}
			if (Quad1.compareTo(Quad3) < 0) {
				Tmp = Quad1;
				Quad1 = Quad3;
				Quad3 = Tmp;
			}
			if (Quad1.compareTo(Quad4) < 0) {
				Tmp = Quad1;
				Quad1 = Quad4;
				Quad4 = Tmp;
			}
			if (Quad2.compareTo(Quad3) < 0) {
				Tmp = Quad2;
				Quad2 = Quad3;
				Quad3 = Tmp;
			}
			if (Quad2.compareTo(Quad4) < 0) {
				Tmp = Quad2;
				Quad2 = Quad4;
				Quad4 = Tmp;
			}
			if (Quad3.compareTo(Quad4) < 0) {
				Tmp = Quad3;
				Quad3 = Quad4;
				Quad4 = Tmp;
			}
		}
		return true;
	}

	private void Convert31To32Bits(long[] nbr31bits, long[] nbr32bits) {
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
				nbr32bits[i] = nbr31bits[j] >> k;
			} else {
				nbr32bits[i] = ((nbr31bits[j] >> k) | (nbr31bits[j + 1] << (31 - k))) & 0xFFFFFFFFl;
			}
			i++;
		}
		for (; i < NumberLength; i++) {
			nbr32bits[i] = 0;
		}
	}

	private void Convert32To31Bits(long[] nbr32bits, long[] nbr31bits) {
		int i, j, k;
		j = 0;
		nbr32bits[NumberLength] = 0;
		for (i = 0; i < NumberLength; i++) {
			k = i % 32;
			if (k == 0) {
				nbr31bits[i] = nbr32bits[j] & 0x7FFFFFFFl;
			} else {
				nbr31bits[i] = ((nbr32bits[j] >> (32 - k)) | (nbr32bits[j + 1] << k)) & 0x7FFFFFFFl;
				j++;
			}
		}
	}

	private void DivBigNbrByLong(long Dividend[], long Divisor, long Quotient[]) {
		int i;
		boolean ChSignDivisor = false;
		long Divid, Rem = 0;

		if (Divisor < 0) { // If divisor is negative...
			ChSignDivisor = true; // Indicate to change sign at the end and
			Divisor = -Divisor; // convert divisor to positive.
		}
		if (Dividend[i = NumberLength - 1] >= 0x40000000l) { // If dividend is
																// negative...
			Rem = Divisor - 1;
		}
		for (; i >= 0; i--) {
			Divid = Dividend[i] + (Rem << 31);
			Rem = Divid % Divisor;
			Quotient[i] = Divid / Divisor;
		}
		if (ChSignDivisor) { // Change sign if divisor is negative.
			ChSignBigNbr(Quotient); // Convert divisor to positive.
		}
	}

	/*
	 * computes 2P=(x2:z2) from P=(x1:z1), with 5 mul, 4 add/sub, 5 mod. Uses the following global variables: - n :
	 * number to factor - b : (a+2)/4 mod n - u, v, w : auxiliary variables Modifies: x2, z2, u, v, w
	 */
	private void duplicate(long[] x2, long[] z2, long[] x1, long[] z1) {
		long[] u = fieldUZ;
		long[] v = fieldTX;
		long[] w = fieldTZ;
		AddBigNbrModN(x1, z1, w); // w = x1+z1
		MontgomeryMult(w, w, u); // u = (x1+z1)^2
		SubtractBigNbrModN(x1, z1, w); // w = x1-z1
		MontgomeryMult(w, w, v); // v = (x1-z1)^2
		MontgomeryMult(u, v, x2); // x2 = u*v = (x1^2 - z1^2)^2
		SubtractBigNbrModN(u, v, w); // w = u-v = 4*x1*z1
		MontgomeryMult(fieldAA, w, u);
		AddBigNbrModN(u, v, u); // u = (v+b*w)
		MontgomeryMult(w, u, z2); // z2 = (w*u)
	}
	/* End of code "borrowed" from Paul Zimmermann's ECM4C */

	/**
	 * Factor the integer number given in the classes constructor.
	 * 
	 * @param map
	 *            the map of prime factors
	 * @param noSIQS
	 *            return the rest number; don't call SIQS in this method
	 * @return
	 */
	public BigInteger factorize(Map<BigInteger, Integer> map) {
		BigInteger NN;// N
		long TestComp; // , New;
		BigInteger N1, N2, Tmp;
		int i, j;
		Computing3Squares = false;
		TerminateThread = false;
		if (onlyFactoring) {
			NumberToFactor = inputNumber;
		}
		BigNbr1[0] = 1;
		for (i = 1; i < NLen; i++) {
			BigNbr1[i] = 0;
		}
		try {
			if (NbrFactors == 0) {
				// System.out.println("Searching for small factors (less than
				// 131072).");
				TestComp = GetSmallFactors(NumberToFactor, PD, Exp, 0);
				if (TestComp != 1) { // There are factors greater than 131071.
					PD[NbrFactors] = BigIntToBigNbr(TestNbr);
					Exp[NbrFactors] = 1;
					Typ[NbrFactors] = -1; /* Unknown */
					incNbrFactors();
				}
			}
			factor_loop: do {
				for (i = 0; i < NbrFactors; i++) {
					if (Typ[i] < 0) { /* Unknown */
						// System.out.println("Searching for perfect power.");
						if (PowerCheck(i) != 0) {
							SortFactorsInputNbr();
							continue factor_loop;
						}
						if (PD[i].bitLength() <= 33) {
							j = 0;
						} else {
							// System.out.println("Before calling prime check
							// routine.");
							j = AprtCle(PD[i]);
							if (batchFinished == false && batchPrime) {
								NbrFactors = j;
								return BigInteger.ONE;
							}
						}
						if (j == 0) {
							if (Typ[i] < -TYP_EC) {
								Typ[i] = -Typ[i]; /* Prime */
							} else if (Typ[i] < -TYP_LEHMAN) {
								Typ[i] = TYP_LEHMAN; /* Prime */
							} else if (Typ[i] < -TYP_SIQS) {
								Typ[i] = TYP_SIQS; /* Prime */
							} else if (Typ[i] < -TYP_AURIF) {
								Typ[i] = TYP_AURIF; /* Prime */
							} else {
								Typ[i] = 0; /* Prime */
							}
						} else {
							if (Typ[i] < -TYP_EC) {
								Typ[i] = -TYP_EC - Typ[i]; /* Composite */
							} else {
								Typ[i] = -Typ[i]; /* Composite */
							}
						}
						continue factor_loop;
					}
				}
				for (i = 0; i < NbrFactors; i++) {
					EC = Typ[i];

					if (EC > 0 && EC < TYP_EC && EC != TYP_AURIF && EC != TYP_SIQS
							&& EC != TYP_LEHMAN) { /* Composite */
						EC %= 50000000;
						NN = fnECM(PD[i], i);
						if (NN.equals(BigInt1)) {

							for (i = 0; i < NbrFactors - 1; i++) {
								map.put(PD[i], Exp[i]);
							}
							return PD[i];

							// System.out.println(NN.toString());
						}
						if (foundByLehman) { // Factor found using Lehman method
							Typ[i] = TYP_LEHMAN + EC + 1;
						} else {
							Typ[i] = EC;
						}
						InsertNewFactor(NN);
						continue factor_loop;
					}
				}
				break;
			} while (true);
			for (i = 0; i < NbrFactors; i++) {
				map.put(PD[i], Exp[i]);
			}
			if (onlyFactoring) {
				ComputeFourSquares(PD, Exp); // Quad1^2 + Quad2^2 + Quad3^2 +
												// Quad4^2
				NbrFactors1 = NbrFactors;
				if (Quad4.signum() != 0) { // Check if four squares are really
											// needed.
					j = NumberToFactor.getLowestSetBit();
					if (j % 2 != 0 || NumberToFactor.shiftRight(j).and(BigInteger.valueOf(7))
							.equals(BigInteger.valueOf(7)) == false) {
						/* Only three squares are required here */

						Computing3Squares = true;
						j = j / 2;
						// System.out.println("Computing sum of three
						// squares...");
						for (N1 = BigInt1.shiftLeft(j);; N1 = N1.add(BigInt1.shiftLeft(j))) {
							if (TerminateThread) {
								throw new ArithmeticException();
							}
							N2 = NumberToFactor.subtract(N1.multiply(N1));
							TestComp = GetSmallFactors(N2, PD1, Exp1, 1); // Typ1, 1);
							if (TestComp >= 0) {
								if (TestComp == 1) { // Number has all factors <
														// 2^17
									ComputeFourSquares(PD1, Exp1); // Quad1^2 +
																	// Quad2^2
									break;
								}
								if (TestNbr[0] % 4 == 3) {
									continue;
								} // This value of c does not work
								PD1[NbrFactors] = BigIntToBigNbr(TestNbr);
								Exp1[NbrFactors] = 1;
								incNbrFactors();
								if (ComputeFourSquares(PD1, Exp1)) { // Quad1^2
																		// +
																		// Quad2^2
									break;
								}
							}
						} /* end for */
						Quad3 = N1;
						// Sort squares (only Quad3 can be out of order).
						if (Quad1.compareTo(Quad3) < 0) {
							Tmp = Quad1;
							Quad1 = Quad3;
							Quad3 = Tmp;
						}
						if (Quad2.compareTo(Quad3) < 0) {
							Tmp = Quad2;
							Quad2 = Quad3;
							Quad3 = Tmp;
						}
						Computing3Squares = false;
					}
				}
				NbrFactors = NbrFactors1;
				NextEC = -1; /* First curve of new number should be 1 */
			}
		} catch (ArithmeticException e) {

		}
		// System.gc();
		return BigInteger.ONE;
	}

	private BigInteger fnECM(BigInteger N, int FactorIndex) {
		int I, J, Pass, Qaux;
		long L1, L2, LS, P, Q, IP;
		long[] A0 = new long[NLen];
		long[] A02 = new long[NLen];
		long[] A03 = new long[NLen];
		long[] AA = new long[NLen];
		long[] DX = new long[NLen];
		long[] DZ = new long[NLen];
		long[] GD = new long[NLen];
		long[] M = new long[NLen];
		long[] TX = new long[NLen];
		fieldTX = TX;
		long[] TZ = new long[NLen];
		fieldTZ = TZ;
		long[] UX = new long[NLen];
		fieldUX = UX;
		long[] UZ = new long[NLen];
		fieldUZ = UZ;
		long[] W1 = new long[NLen];
		long[] W2 = new long[NLen];
		long[] W3 = new long[NLen];
		long[] W4 = new long[NLen];
		long[] WX = new long[NLen];
		long[] WZ = new long[NLen];
		long[] X = new long[NLen];
		long[] Z = new long[NLen];
		long[] Aux1 = new long[NLen];
		fieldAux1 = Aux1;
		long[] Aux2 = new long[NLen];
		fieldAux2 = Aux2;
		long[] Aux3 = new long[NLen];
		fieldAux3 = Aux3;
		long[] Aux4 = new long[NLen];
		fieldAux4 = Aux4;
		long[] Xaux = new long[NLen];
		long[] Zaux = new long[NLen];
		long[][] root = new long[480][NLen];
		byte[] sieve = new byte[23100];
		byte[] sieve2310 = new byte[2310];
		int[] sieveidx = new int[480];
		int i, j, u;
		BigInteger NN;
		boolean PrevCurveECMd = false;

		fieldAA = AA;
		BigNbrToBigInt(N);
		GetMontgomeryParms();
		for (I = 0; I < NumberLength; I++) {
			M[I] = DX[I] = DZ[I] = W3[I] = W4[I] = GD[I] = 0;
		}
		EC--;
		SmallPrime[0] = 2;
		P = 3;
		indexM = 1;
		for (indexM = 1; indexM < SmallPrime.length; indexM++) {
			SmallPrime[indexM] = (int) P; /* Store prime */
			calculate_new_prime1: do {
				P += 2;
				for (Q = 3; Q * Q <= P; Q += 2) { /* Check if P is prime */
					if (P % Q == 0) {
						continue calculate_new_prime1; /* Composite */
					}
				}
				break; /* Prime found */
			} while (true);
		}
		foundByLehman = false;
		do {
			new_curve: do {
				if (NextEC > 0) {
					EC = NextEC;
					NextEC = -1;
					if (EC >= TYP_SIQS) {
						return BigInt1;
					}
				} else {
					EC++;
					NN = Lehman(NumberToFactor, EC);
					if (NN.equals(BigInt1) == false) { // Factor found.
						foundByLehman = true;
						return NN;
					}
					L1 = N.toString().length(); // Get number of digits.
					if (L1 > 30 && L1 <= 90) // If between 30 and 90 digits...
					{
						if ((digitsInGroup & 0x400) == 0) { // Switch to SIQS
															// checkbox is set.
							int limit = limits[((int) L1 - 31) / 5];
							if (EC % 50000000 > limit && PrevCurveECMd == false
									|| EC % 50000000 == limit && PrevCurveECMd == true) { // Switch
																							// to
																							// SIQS.
								EC += TYP_SIQS;
								return BigInt1;
							}
						}
					}
				}
				PrevCurveECMd = true;
				Typ[FactorIndex] = EC;
				L1 = 2000;
				L2 = 200000;
				LS = 45;
				// Paux = EC;
				// NbrPrimes = 303; /* Number of primes less than 2000 */
				if (EC > 25) {
					if (EC < 326) {
						L1 = 50000;
						L2 = 5000000;
						LS = 224;
						// Paux = EC - 24;
						// NbrPrimes = 5133;
						/* Number of primes less than 50000 */
					} else {
						if (EC < 2000) {
							L1 = 1000000;
							L2 = 100000000;
							LS = 1001;
							// Paux = EC - 299;
							// NbrPrimes = 78498;
							/*
							 * Number of primes less than 1000000
							 */
						} else {
							L1 = 11000000;
							L2 = 1100000000;
							LS = 3316;
							// Paux = EC - 1900;
							// NbrPrimes = 726517;
							/*
							 * Number of primes less than 11000000
							 */
						}
					}
				}

				// System.out.println(primalityString + EC + "\n" + UpperLine +
				// "\n" + LowerLine);

				LongToBigNbr(2 * (EC + 1), Aux1);
				LongToBigNbr(3 * (EC + 1) * (EC + 1) - 1, Aux2);
				ModInvBigNbr(Aux2, Aux2, TestNbr);
				MultBigNbrModN(Aux1, Aux2, Aux3);
				MultBigNbrModN(Aux3, MontgomeryMultR1, A0);
				MontgomeryMult(A0, A0, A02);
				MontgomeryMult(A02, A0, A03);
				SubtractBigNbrModN(A03, A0, Aux1);
				MultBigNbrByLongModN(A02, 9, Aux2);
				SubtractBigNbrModN(Aux2, MontgomeryMultR1, Aux2);
				MontgomeryMult(Aux1, Aux2, Aux3);
				if (BigNbrIsZero(Aux3)) {
					continue;
				}
				MultBigNbrByLongModN(A0, 4, Z);
				MultBigNbrByLongModN(A02, 6, Aux1);
				SubtractBigNbrModN(MontgomeryMultR1, Aux1, Aux1);
				MontgomeryMult(A02, A02, Aux2);
				MultBigNbrByLongModN(Aux2, 3, Aux2);
				SubtractBigNbrModN(Aux1, Aux2, Aux1);
				MultBigNbrByLongModN(A03, 4, Aux2);
				ModInvBigNbr(Aux2, Aux2, TestNbr);
				MontgomeryMult(Aux2, MontgomeryMultAfterInv, Aux3);
				MontgomeryMult(Aux1, Aux3, A0);
				AddBigNbrModN(A0, MontgomeryMultR2, Aux1);
				LongToBigNbr(4, Aux2);
				ModInvBigNbr(Aux2, Aux3, TestNbr);
				MultBigNbrModN(Aux3, MontgomeryMultR1, Aux2);
				MontgomeryMult(Aux1, Aux2, AA);
				MultBigNbrByLongModN(A02, 3, Aux1);
				AddBigNbrModN(Aux1, MontgomeryMultR1, X);
				/**************/
				/* First step */
				/**************/
				System.arraycopy(X, 0, Xaux, 0, NumberLength);
				System.arraycopy(Z, 0, Zaux, 0, NumberLength);
				System.arraycopy(MontgomeryMultR1, 0, GcdAccumulated, 0, NumberLength);
				for (Pass = 0; Pass < 2; Pass++) {
					/* For powers of 2 */
					for (I = 1; I <= L1; I <<= 1) {
						duplicate(X, Z, X, Z);
					}
					for (I = 3; I <= L1; I *= 3) {
						duplicate(W1, W2, X, Z);
						add3(X, Z, X, Z, W1, W2, X, Z);
					}

					if (Pass == 0) {
						MontgomeryMult(GcdAccumulated, Z, Aux1);
						System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
					} else {
						GcdBigNbr(Z, TestNbr, GD);
						if (BigNbrAreEqual(GD, BigNbr1) == false) {
							break new_curve;
						}
					}

					/* for powers of odd primes */

					indexM = 1;
					do {
						P = SmallPrime[indexM];
						for (IP = P; IP <= L1; IP *= P) {
							prac((int) P, X, Z, W1, W2, W3, W4);
						}
						indexM++;
						if (Pass == 0) {
							MontgomeryMult(GcdAccumulated, Z, Aux1);
							System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
						} else {
							GcdBigNbr(Z, TestNbr, GD);
							if (BigNbrAreEqual(GD, BigNbr1) == false) {
								break new_curve;
							}
						}
					} while (SmallPrime[indexM - 1] <= LS);
					P += 2;

					/*
					 * Initialize sieve2310[n]: 1 if gcd(P+2n,2310) > 1, 0 otherwise
					 */
					u = (int) P;
					for (i = 0; i < 2310; i++) {
						sieve2310[i] = (u % 3 == 0 || u % 5 == 0 || u % 7 == 0 || u % 11 == 0 ? (byte) 1 : (byte) 0);
						u += 2;
					}
					do {
						/* Generate sieve */
						GenerateSieve((int) P, sieve, sieve2310, SmallPrime);

						/* Walk through sieve */

						for (i = 0; i < 23100; i++) {
							if (sieve[i] != 0)
								continue; /* Do not process composites */
							if (P + 2 * i > L1)
								break;
							prac((int) (P + 2 * i), X, Z, W1, W2, W3, W4);
							if (Pass == 0) {
								MontgomeryMult(GcdAccumulated, Z, Aux1);
								System.arraycopy(Aux1, 0, GcdAccumulated, 0, NumberLength);
							} else {
								GcdBigNbr(Z, TestNbr, GD);
								if (BigNbrAreEqual(GD, BigNbr1) == false) {
									break new_curve;
								}
							}
						}
						P += 46200;
					} while (P < L1);
					if (Pass == 0) {
						if (BigNbrIsZero(GcdAccumulated)) { // If GcdAccumulated
															// is
							System.arraycopy(Xaux, 0, X, 0, NumberLength);
							System.arraycopy(Zaux, 0, Z, 0, NumberLength);
							continue; // multiple of TestNbr, continue.
						}
						GcdBigNbr(GcdAccumulated, TestNbr, GD);
						if (BigNbrAreEqual(GD, BigNbr1) == false) {
							break new_curve;
						}
						break;
					}
				} /* end for Pass */

				/******************************************************/
				/* Second step (using improved standard continuation) */
				/******************************************************/
				j = 0;
				for (u = 1; u < 2310; u += 2) {
					if (u % 3 == 0 || u % 5 == 0 || u % 7 == 0 || u % 11 == 0) {
						sieve2310[u / 2] = (byte) 1;
					} else {
						sieve2310[(sieveidx[j++] = u / 2)] = (byte) 0;
					}
				}
				System.arraycopy(sieve2310, 0, sieve2310, 1155, 1155);
				System.arraycopy(X, 0, Xaux, 0, NumberLength); // (X:Z) -> Q
																// (output
				System.arraycopy(Z, 0, Zaux, 0, NumberLength); // from step 1)
				for (Pass = 0; Pass < 2; Pass++) {
					System.arraycopy(MontgomeryMultR1, 0, GcdAccumulated, 0, NumberLength);
					System.arraycopy(X, 0, UX, 0, NumberLength);
					System.arraycopy(Z, 0, UZ, 0, NumberLength); // (UX:UZ) -> Q
					ModInvBigNbr(Z, Aux2, TestNbr);
					MontgomeryMult(Aux2, MontgomeryMultAfterInv, Aux1);
					MontgomeryMult(Aux1, X, root[0]); // root[0] <- X/Z (Q)
					J = 0;
					AddBigNbrModN(X, Z, Aux1);
					MontgomeryMult(Aux1, Aux1, W1);
					SubtractBigNbrModN(X, Z, Aux1);
					MontgomeryMult(Aux1, Aux1, W2);
					MontgomeryMult(W1, W2, TX);
					SubtractBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, AA, Aux2);
					AddBigNbrModN(Aux2, W2, Aux3);
					MontgomeryMult(Aux1, Aux3, TZ); // (TX:TZ) -> 2Q
					SubtractBigNbrModN(X, Z, Aux1);
					AddBigNbrModN(TX, TZ, Aux2);
					MontgomeryMult(Aux1, Aux2, W1);
					AddBigNbrModN(X, Z, Aux1);
					SubtractBigNbrModN(TX, TZ, Aux2);
					MontgomeryMult(Aux1, Aux2, W2);
					AddBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, Aux1, Aux2);
					MontgomeryMult(Aux2, UZ, X);
					SubtractBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, Aux1, Aux2);
					MontgomeryMult(Aux2, UX, Z); // (X:Z) -> 3Q
					for (I = 5; I < 2310; I += 2) {
						System.arraycopy(X, 0, WX, 0, NumberLength);
						System.arraycopy(Z, 0, WZ, 0, NumberLength);
						SubtractBigNbrModN(X, Z, Aux1);
						AddBigNbrModN(TX, TZ, Aux2);
						MontgomeryMult(Aux1, Aux2, W1);
						AddBigNbrModN(X, Z, Aux1);
						SubtractBigNbrModN(TX, TZ, Aux2);
						MontgomeryMult(Aux1, Aux2, W2);
						AddBigNbrModN(W1, W2, Aux1);
						MontgomeryMult(Aux1, Aux1, Aux2);
						MontgomeryMult(Aux2, UZ, X);
						SubtractBigNbrModN(W1, W2, Aux1);
						MontgomeryMult(Aux1, Aux1, Aux2);
						MontgomeryMult(Aux2, UX, Z); // (X:Z) -> 5Q, 7Q, ...
						if (Pass == 0) {
							MontgomeryMult(GcdAccumulated, Aux1, Aux2);
							System.arraycopy(Aux2, 0, GcdAccumulated, 0, NumberLength);
						} else {
							GcdBigNbr(Aux1, TestNbr, GD);
							if (BigNbrAreEqual(GD, BigNbr1) == false) {
								break new_curve;
							}
						}
						if (I == 1155) {
							System.arraycopy(X, 0, DX, 0, NumberLength);
							System.arraycopy(Z, 0, DZ, 0, NumberLength); // (DX:DZ)
																			// ->
																			// 1155Q
						}
						if (I % 3 != 0 && I % 5 != 0 && I % 7 != 0 && I % 11 != 0) {
							J++;
							ModInvBigNbr(Z, Aux2, TestNbr);
							MontgomeryMult(Aux2, MontgomeryMultAfterInv, Aux1);
							MontgomeryMult(Aux1, X, root[J]); // root[J] <- X/Z
						}
						System.arraycopy(WX, 0, UX, 0, NumberLength); // (UX:UZ)
																		// <-
						System.arraycopy(WZ, 0, UZ, 0, NumberLength); // Previous
																		// (X:Z)
					} /* end for I */
					AddBigNbrModN(DX, DZ, Aux1);
					MontgomeryMult(Aux1, Aux1, W1);
					SubtractBigNbrModN(DX, DZ, Aux1);
					MontgomeryMult(Aux1, Aux1, W2);
					MontgomeryMult(W1, W2, X);
					SubtractBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, AA, Aux2);
					AddBigNbrModN(Aux2, W2, Aux3);
					MontgomeryMult(Aux1, Aux3, Z);
					System.arraycopy(X, 0, UX, 0, NumberLength);
					System.arraycopy(Z, 0, UZ, 0, NumberLength); // (UX:UZ) ->
																	// 2310Q
					AddBigNbrModN(X, Z, Aux1);
					MontgomeryMult(Aux1, Aux1, W1);
					SubtractBigNbrModN(X, Z, Aux1);
					MontgomeryMult(Aux1, Aux1, W2);
					MontgomeryMult(W1, W2, TX);
					SubtractBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, AA, Aux2);
					AddBigNbrModN(Aux2, W2, Aux3);
					MontgomeryMult(Aux1, Aux3, TZ); // (TX:TZ) -> 2*2310Q
					SubtractBigNbrModN(X, Z, Aux1);
					AddBigNbrModN(TX, TZ, Aux2);
					MontgomeryMult(Aux1, Aux2, W1);
					AddBigNbrModN(X, Z, Aux1);
					SubtractBigNbrModN(TX, TZ, Aux2);
					MontgomeryMult(Aux1, Aux2, W2);
					AddBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, Aux1, Aux2);
					MontgomeryMult(Aux2, UZ, X);
					SubtractBigNbrModN(W1, W2, Aux1);
					MontgomeryMult(Aux1, Aux1, Aux2);
					MontgomeryMult(Aux2, UX, Z); // (X:Z) -> 3*2310Q
					Qaux = (int) (L1 / 4620);
					maxIndexM = (int) (L2 / 4620);
					for (indexM = 0; indexM <= maxIndexM; indexM++) {
						if (indexM >= Qaux) { // If inside step 2 range...
							if (indexM == 0) {
								ModInvBigNbr(UZ, Aux2, TestNbr);
								MontgomeryMult(Aux2, MontgomeryMultAfterInv, Aux3);
								MontgomeryMult(UX, Aux3, Aux1); // Aux1 <- X/Z
																// (2310Q)
							} else {
								ModInvBigNbr(Z, Aux2, TestNbr);
								MontgomeryMult(Aux2, MontgomeryMultAfterInv, Aux3);
								MontgomeryMult(X, Aux3, Aux1); // Aux1 <- X/Z
																// (3,5,*
							} // 2310Q)

							/* Generate sieve */
							if (indexM % 10 == 0 || indexM == Qaux) {
								GenerateSieve(indexM / 10 * 46200 + 1, sieve, sieve2310, SmallPrime);
							}
							/* Walk through sieve */
							J = 1155 + (indexM % 10) * 2310;
							for (i = 0; i < 480; i++) {
								j = sieveidx[i]; // 0 < J < 1155
								if (sieve[J + j] != 0 && sieve[J - 1 - j] != 0) {
									continue; // Do not process if both are
												// composite numbers.
								}
								SubtractBigNbrModN(Aux1, root[i], M);
								MontgomeryMult(GcdAccumulated, M, Aux2);
								System.arraycopy(Aux2, 0, GcdAccumulated, 0, NumberLength);
							}
							if (Pass != 0) {
								GcdBigNbr(GcdAccumulated, TestNbr, GD);
								if (BigNbrAreEqual(GD, BigNbr1) == false) {
									break new_curve;
								}
							}
						}
						if (indexM != 0) { // Update (X:Z)
							System.arraycopy(X, 0, WX, 0, NumberLength);
							System.arraycopy(Z, 0, WZ, 0, NumberLength);
							SubtractBigNbrModN(X, Z, Aux1);
							AddBigNbrModN(TX, TZ, Aux2);
							MontgomeryMult(Aux1, Aux2, W1);
							AddBigNbrModN(X, Z, Aux1);
							SubtractBigNbrModN(TX, TZ, Aux2);
							MontgomeryMult(Aux1, Aux2, W2);
							AddBigNbrModN(W1, W2, Aux1);
							MontgomeryMult(Aux1, Aux1, Aux2);
							MontgomeryMult(Aux2, UZ, X);
							SubtractBigNbrModN(W1, W2, Aux1);
							MontgomeryMult(Aux1, Aux1, Aux2);
							MontgomeryMult(Aux2, UX, Z);
							System.arraycopy(WX, 0, UX, 0, NumberLength);
							System.arraycopy(WZ, 0, UZ, 0, NumberLength);
						}
					} // end for Q
					if (Pass == 0) {
						if (BigNbrIsZero(GcdAccumulated)) { // If GcdAccumulated
															// is
							System.arraycopy(Xaux, 0, X, 0, NumberLength);
							System.arraycopy(Zaux, 0, Z, 0, NumberLength);
							continue; // multiple of TestNbr, continue.
						}
						GcdBigNbr(GcdAccumulated, TestNbr, GD);
						if (BigNbrAreEqual(GD, TestNbr) == true) {
							break;
						}
						if (BigNbrAreEqual(GD, BigNbr1) == false) {
							break new_curve;
						}
						break;
					}
				} /* end for Pass */
				// performLehman = true;
			} while (true); /* end curve calculation */
		} while (BigNbrAreEqual(GD, TestNbr) == true);
		// System.out.println("");
		// StepECM = 0; /* do not show pass number on screen */
		return BigIntToBigNbr(GD);
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
	private void GcdBigNbr(long Nbr1[], long Nbr2[], long Gcd[]) {
		int i, k;
		int NumberLength = this.NumberLength;

		System.arraycopy(Nbr1, 0, CalcAuxGcdU, 0, NumberLength);
		System.arraycopy(Nbr2, 0, CalcAuxGcdV, 0, NumberLength);
		for (i = 0; i < NumberLength; i++) {
			if (CalcAuxGcdU[i] != 0) {
				break;
			}
		}
		if (i == NumberLength) {
			System.arraycopy(CalcAuxGcdV, 0, Gcd, 0, NumberLength);
			return;
		}
		for (i = 0; i < NumberLength; i++) {
			if (CalcAuxGcdV[i] != 0) {
				break;
			}
		}
		if (i == NumberLength) {
			System.arraycopy(CalcAuxGcdU, 0, Gcd, 0, NumberLength);
			return;
		}
		if (CalcAuxGcdU[NumberLength - 1] >= 0x40000000l) {
			ChSignBigNbr(CalcAuxGcdU);
		}
		if (CalcAuxGcdV[NumberLength - 1] >= 0x40000000l) {
			ChSignBigNbr(CalcAuxGcdV);
		}
		k = 0;
		while ((CalcAuxGcdU[0] & 1) == 0 && (CalcAuxGcdV[0] & 1) == 0) { // Step
																			// 1
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
			for (i = 0; i < NumberLength; i++) {
				if (CalcAuxGcdT[i] != 0) {
					break;
				}
			}
		} while (i != NumberLength);
		System.arraycopy(CalcAuxGcdU, 0, Gcd, 0, NumberLength); // Step 7
		while (k > 0) {
			AddBigNbr(Gcd, Gcd, Gcd);
			k--;
		}
	}

	private void GetMontgomeryParms() {
		int NumberLength = this.NumberLength;
		int N, x, j;

		dN = TestNbr[NumberLength - 1];
		if (NumberLength > 1) {
			dN += TestNbr[NumberLength - 2] / dDosALa31;
		}
		if (NumberLength > 2) {
			dN += TestNbr[NumberLength - 3] / dDosALa62;
		}

		x = N = (int) TestNbr[0]; // 2 least significant bits of inverse
									// correct.
		x = x * (2 - N * x); // 4 least significant bits of inverse correct.
		x = x * (2 - N * x); // 8 least significant bits of inverse correct.
		x = x * (2 - N * x); // 16 least significant bits of inverse correct.
		x = x * (2 - N * x); // 32 least significant bits of inverse correct.
		MontgomeryMultN = (-x) & 0x7FFFFFFF;
		j = NumberLength;
		MontgomeryMultR1[j] = 1;
		do {
			MontgomeryMultR1[--j] = 0;
		} while (j > 0);
		AdjustModN(MontgomeryMultR1);
		MultBigNbrModN(MontgomeryMultR1, MontgomeryMultR1, MontgomeryMultR2);
		MontgomeryMult(MontgomeryMultR2, MontgomeryMultR2, MontgomeryMultAfterInv);
		AddBigNbrModN(MontgomeryMultR1, MontgomeryMultR1, MontgomeryMultR2);
	}

	private long GetSmallFactors(BigInteger NumberToFactor, BigInteger PD[], int Exp[], int Type) {

		long Div, TestComp;
		int i;
		boolean checkExpParity = false;

		BigNbrToBigInt(NumberToFactor);
		NbrFactors = 0;
		for (i = 0; i < Exp.length; i++) {
			Exp[i] = Typ[i] = 0;
		}
		while ((TestNbr[0] & 1) == 0) { /* N even */
			if (Exp[NbrFactors] == 0) {
				PD[NbrFactors] = BigInt2;
			}
			Exp[NbrFactors]++;
			DivBigNbrByLong(TestNbr, 2, TestNbr);
		}
		if (Exp[NbrFactors] != 0) {
			Object[] temp = incNbrFactors();
			if (temp != null) {
				if (Type == 0) {
					PD = (BigInteger[]) temp[0];
					Exp = (int[]) temp[1];
					// Typ = (int[]) temp[2];
				} else {
					PD = (BigInteger[]) temp[3];
					Exp = (int[]) temp[4];
					// Typ = (int[]) temp[5];
				}
			}
		}
		while (RemDivBigNbrByLong(TestNbr, 3) == 0) {
			if (Type == 1) {
				checkExpParity = !checkExpParity;
			}
			if (Exp[NbrFactors] == 0) {
				PD[NbrFactors] = BigInt3;
			}
			Exp[NbrFactors]++;
			DivBigNbrByLong(TestNbr, 3, TestNbr);
		}
		if (checkExpParity) {
			return -1; /* Discard it */
		}
		if (Exp[NbrFactors] != 0) {
			Object[] temp = incNbrFactors();
			if (temp != null) {
				if (Type == 0) {
					PD = (BigInteger[]) temp[0];
					Exp = (int[]) temp[1];
					Typ = (int[]) temp[2];
				} else {
					PD = (BigInteger[]) temp[3];
					Exp = (int[]) temp[4];
					Typ = (int[]) temp[5];
				}
			}
		}
		Div = 5;
		TestComp = TestNbr[0] + (TestNbr[1] << 31);
		if (TestComp < 0) {
			TestComp = 10000 * DosALa31;
		} else {
			for (i = 2; i < NumberLength; i++) {
				if (TestNbr[i] != 0) {
					TestComp = 10000 * DosALa31;
					break;
				}
			}
		}
		while (Div < 131072) {
			if (Div % 3 != 0) {
				while (RemDivBigNbrByLong(TestNbr, Div) == 0) {
					if (Type == 1 && Div % 4 == 3) {
						checkExpParity = !checkExpParity;
					}
					if (Exp[NbrFactors] == 0) {
						PD[NbrFactors] = BigInteger.valueOf(Div);
					}
					Exp[NbrFactors]++;
					DivBigNbrByLong(TestNbr, Div, TestNbr);
					TestComp = TestNbr[0] + (TestNbr[1] << 31);
					if (TestComp < 0) {
						TestComp = 10000 * DosALa31;
					} else {
						for (i = 2; i < NumberLength; i++) {
							if (TestNbr[i] != 0) {
								TestComp = 10000 * DosALa31;
								break;
							}
						}
					} /* end while */
				}
				if (checkExpParity) {
					return -1; /* Discard it */
				}
				if (Exp[NbrFactors] != 0) {
					Object[] temp = incNbrFactors();
					if (temp != null) {
						if (Type == 0) {
							PD = (BigInteger[]) temp[0];
							Exp = (int[]) temp[1];
							Typ = (int[]) temp[2];
						} else {
							PD = (BigInteger[]) temp[3];
							Exp = (int[]) temp[4];
							Typ = (int[]) temp[5];
						}
					}
				}
			}
			Div += 2;
			if (TestComp < Div * Div && TestComp != 1) {
				if (Type == 1 && TestComp % 4 == 3) {
					return -1; /* Discard it */
				}
				if (Exp[NbrFactors] != 0) {
					Object[] temp = incNbrFactors();
					if (temp != null) {
						if (Type == 0) {
							PD = (BigInteger[]) temp[0];
							Exp = (int[]) temp[1];
							// Typ = (int[]) temp[2];
						} else {
							PD = (BigInteger[]) temp[3];
							Exp = (int[]) temp[4];
							// Typ = (int[]) temp[5];
						}
					}
				}
				PD[NbrFactors] = BigInteger.valueOf(TestComp);
				Exp[NbrFactors] = 1;
				TestComp = 1;
				// Object[] temp = incNbrFactors();
				//
				// if (temp != null) {
				// if (Type == 0) {
				// PD = (BigInteger[]) temp[0];
				// Exp = (int[]) temp[1];
				// Typ = (int[]) temp[2];
				// } else {
				// PD = (BigInteger[]) temp[3];
				// Exp = (int[]) temp[4];
				// Typ = (int[]) temp[5];
				// }
				// }
				incNbrFactors();
				break;
			}
		} /* end while */
		return TestComp;
	}

	private void InsertNewFactor(BigInteger InputFactor) {
		int g, exp;

		/* Insert input factor */
		for (g = NbrFactors - 1; g >= 0; g--) {
			PD[NbrFactors] = PD[g].gcd(InputFactor);
			if (PD[NbrFactors].equals(BigInt1) || PD[NbrFactors].equals(PD[g])) {
				continue;
			}
			for (exp = 0; PD[g].remainder(PD[NbrFactors]).signum() == 0; exp++) {
				PD[g] = PD[g].divide(PD[NbrFactors]);
			}
			Exp[NbrFactors] = Exp[g] * exp;
			if (Typ[g] < 100000000) {
				Typ[g] = -EC;
				Typ[NbrFactors] = -TYP_EC - EC;
			} else if (Typ[g] < 150000000) {
				Typ[NbrFactors] = -Typ[g];
				Typ[g] = TYP_AURIF - Typ[g];
			} else if (Typ[g] < 200000000) {
				Typ[NbrFactors] = -Typ[g];
				Typ[g] = TYP_TABLE - Typ[g];
			} else if (Typ[g] < 250000000) {
				Typ[NbrFactors] = -Typ[g];
				Typ[g] = TYP_SIQS - Typ[g];
			} else {
				Typ[NbrFactors] = -Typ[g];
				Typ[g] = TYP_LEHMAN - Typ[g];
			}
			incNbrFactors();
		}
		SortFactorsInputNbr();
	}

	private void JacobiSum(int A, int B, int P, int PK, int PL, int PM, int Q) {
		int I, J, K;

		for (I = 0; I < PL; I++) {
			for (J = 0; J < NumberLength; J++) {
				aiJ0[I][J] = 0;
			}
		}
		for (I = 1; I <= Q - 2; I++) {
			J = (A * I + B * aiF[I]) % PK;
			if (J < PL) {
				AddBigNbrModN(aiJ0[J], MontgomeryMultR1, aiJ0[J]);
			} else {
				for (K = 1; K < P; K++) {
					SubtractBigNbrModN(aiJ0[J - K * PM], MontgomeryMultR1, aiJ0[J - K * PM]);
				}
			}
		}
	}

	private void JS_2(int PK, int PL, int PM, int P) {
		int I, J, K;
		for (I = 0; I < PL; I++) {
			K = 2 * I % PK;
			MontgomeryMult(aiJS[I], aiJS[I], biTmp);
			AddBigNbrModN(aiJX[K], biTmp, aiJX[K]);
			AddBigNbrModN(aiJS[I], aiJS[I], biT);
			for (J = I + 1; J < PL; J++) {
				K = (I + J) % PK;
				MontgomeryMult(biT, aiJS[J], biTmp);
				AddBigNbrModN(aiJX[K], biTmp, aiJX[K]);
			}
		}
		for (I = 0; I < PK; I++) {
			for (J = 0; J < NumberLength; J++) {
				aiJS[I][J] = aiJX[I][J];
				aiJX[I][J] = 0;
			}
		}
		NormalizeJS(PK, PL, PM, P);
	}

	private void JS_E(int PK, int PL, int PM, int P) {
		int I, J, K;
		long Mask;

		for (I = NumberLength - 1; I > 0; I--) {
			if (biExp[I] != 0) {
				break;
			}
		}
		if (I == 0 && biExp[0] == 1) {
			return;
		} // Return if E == 1
		for (K = 0; K < PL; K++) {
			for (J = 0; J < NumberLength; J++) {
				aiJW[K][J] = aiJS[K][J];
			}
		}
		Mask = 0x40000000l;
		while (true) {
			if ((biExp[I] & Mask) != 0) {
				break;
			}
			Mask /= 2;
		}
		do {
			JS_2(PK, PL, PM, P);
			Mask /= 2;
			if (Mask == 0) {
				Mask = 0x40000000l;
				I--;
			}
			if ((biExp[I] & Mask) != 0) {
				JS_JW(PK, PL, PM, P);
			}
		} while (I > 0 || Mask != 1);
	}

	private void JS_JW(int PK, int PL, int PM, int P) {
		int I, J, K;
		for (I = 0; I < PL; I++) {
			for (J = 0; J < PL; J++) {
				K = (I + J) % PK;
				MontgomeryMult(aiJS[I], aiJW[J], biTmp);
				AddBigNbrModN(aiJX[K], biTmp, aiJX[K]);
			}
		}
		for (I = 0; I < PK; I++) {
			for (J = 0; J < NumberLength; J++) {
				aiJS[I][J] = aiJX[I][J];
				aiJX[I][J] = 0;
			}
		}
		NormalizeJS(PK, PL, PM, P);
	}

	private void LongToBigNbr(long Nbr, long Out[]) {
		int i;

		Out[0] = Nbr & 0x7FFFFFFFl;
		Out[1] = (Nbr >> 31) & 0x7FFFFFFFl;
		for (i = 2; i < NumberLength; i++) {
			Out[i] = (Nbr < 0 ? 0x7FFFFFFFl : 0);
		}
	}

	/**
	 * 
	 * <p>
	 * Find the inverse multiplicative modulo v.
	 * </p>
	 * 
	 * <p>
	 * The algorithm terminates with u1 = u^(-1) MOD v.
	 * </p>
	 */
	private void ModInvBigNbr(long[] a, long[] inv, long[] b) {
		int i;
		int NumberLength = this.NumberLength;
		int Dif, E;
		int st1, st2;
		long Yaa, Yab; // 2^E * A' = Yaa A + Yab B
		long Yba, Ybb; // 2^E * B' = Yba A + Ybb B
		long Ygb0; // 2^E * Mu' = Yaa Mu + Yab Gamma + Ymb0 B0
		long Ymb0; // 2^E * Gamma' = Yba Mu + Ybb Gamma + Ygb0 B0
		int Iaa, Iab, Iba, Ibb;
		long Tmp1, Tmp2, Tmp3, Tmp4, Tmp5;
		int B0l;
		int invB0l;
		int Al, Bl, T1, Gl, Ml;
		long Cy1, Cy2, Cy3, Cy4;
		int Yaah, Yabh, Ybah, Ybbh;
		int Ymb0h, Ygb0h;
		long Pr1, Pr2, Pr3, Pr4, Pr5, Pr6, Pr7;
		long[] B = this.biTmp;
		long[] CalcAuxModInvA = this.CalcAuxGcdU;
		long[] CalcAuxModInvB = this.CalcAuxGcdV;
		long[] CalcAuxModInvMu = this.CalcAuxGcdT; // Cannot be inv
		long[] CalcAuxModInvGamma = inv;

		Convert31To32Bits(a, CalcAuxModInvA);
		Convert31To32Bits(b, CalcAuxModInvB);
		System.arraycopy(CalcAuxModInvB, 0, B, 0, NumberLength);
		B0l = (int) B[0];
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
					if (CalcAuxModInvB[i] != 0)
						break;
				}
				if (i < 0)
					break; // Go out of loop if CalcAuxModInvB = 0
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
						Ymb0 = (-(int) Yaa * Ml - (int) Yab * Gl) * invB0l;
						Ygb0 = (-Iba * Ml - Ibb * Gl) * invB0l;
						Cy1 = Cy2 = Cy3 = Cy4 = 0;
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
							Pr4 = (Pr1 & 0xFFFFFFFFL) + (Pr2 & 0xFFFFFFFFL) + (Pr3 & 0xFFFFFFFFL) + Cy3;
							Pr5 = Yaa * (Tmp4 = CalcAuxModInvA[i]);
							Pr6 = Yab * (Tmp5 = CalcAuxModInvB[i]);
							Pr7 = (Pr5 & 0xFFFFFFFFL) + (Pr6 & 0xFFFFFFFFL) + Cy1;
							switch (st1) {
							case -9:
								Cy3 = -Tmp1 - Tmp2 - Tmp3;
								Cy1 = -Tmp4 - Tmp5;
								break;
							case -8:
								Cy3 = -Tmp1 - Tmp2;
								Cy1 = -Tmp4 - Tmp5;
								break;
							case -7:
								Cy3 = -Tmp1 - Tmp3;
								Cy1 = -Tmp4;
								break;
							case -6:
								Cy3 = -Tmp1;
								Cy1 = -Tmp4;
								break;
							case -5:
								Cy3 = -Tmp1 + Tmp2 - Tmp3;
								Cy1 = -Tmp4 + Tmp5;
								break;
							case -4:
								Cy3 = -Tmp1 + Tmp2;
								Cy1 = -Tmp4 + Tmp5;
								break;
							case -3:
								Cy3 = -Tmp2 - Tmp3;
								Cy1 = -Tmp5;
								break;
							case -2:
								Cy3 = -Tmp2;
								Cy1 = -Tmp5;
								break;
							case -1:
								Cy3 = -Tmp3;
								Cy1 = 0;
								break;
							case 0:
								Cy3 = 0;
								Cy1 = 0;
								break;
							case 1:
								Cy3 = Tmp2 - Tmp3;
								Cy1 = Tmp5;
								break;
							case 2:
								Cy3 = Tmp2;
								Cy1 = Tmp5;
								break;
							case 3:
								Cy3 = Tmp1 - Tmp2 - Tmp3;
								Cy1 = Tmp4 - Tmp5;
								break;
							case 4:
								Cy3 = Tmp1 - Tmp2;
								Cy1 = Tmp4 - Tmp5;
								break;
							case 5:
								Cy3 = Tmp1 - Tmp3;
								Cy1 = Tmp4;
								break;
							case 6:
								Cy3 = Tmp1;
								Cy1 = Tmp4;
								break;
							case 7:
								Cy3 = Tmp1 + Tmp2 - Tmp3;
								Cy1 = Tmp4 + Tmp5;
								break;
							case 8:
								Cy3 = Tmp1 + Tmp2;
								Cy1 = Tmp4 + Tmp5;
								break;
							}
							Cy3 += (Pr1 >>> 32) + (Pr2 >>> 32) + (Pr3 >>> 32) + (Pr4 >> 32);
							Cy1 += (Pr5 >>> 32) + (Pr6 >>> 32) + (Pr7 >> 32);
							if (i > 0) {
								CalcAuxModInvMu[i - 1] = Pr4 & 0xFFFFFFFFL;
								CalcAuxModInvA[i - 1] = Pr7 & 0xFFFFFFFFL;
							}
							Pr1 = Yba * Tmp1;
							Pr2 = Ybb * Tmp2;
							Pr3 = Ygb0 * Tmp3;
							Pr4 = (Pr1 & 0xFFFFFFFFL) + (Pr2 & 0xFFFFFFFFL) + (Pr3 & 0xFFFFFFFFL) + Cy4;
							Pr5 = Yba * Tmp4;
							Pr6 = Ybb * Tmp5;
							Pr7 = (Pr5 & 0xFFFFFFFFL) + (Pr6 & 0xFFFFFFFFL) + Cy2;
							switch (st2) {
							case -9:
								Cy4 = -Tmp1 - Tmp2 - Tmp3;
								Cy2 = -Tmp4 - Tmp5;
								break;
							case -8:
								Cy4 = -Tmp1 - Tmp2;
								Cy2 = -Tmp4 - Tmp5;
								break;
							case -7:
								Cy4 = -Tmp1 - Tmp3;
								Cy2 = -Tmp4;
								break;
							case -6:
								Cy4 = -Tmp1;
								Cy2 = -Tmp4;
								break;
							case -5:
								Cy4 = -Tmp1 + Tmp2 - Tmp3;
								Cy2 = -Tmp4 + Tmp5;
								break;
							case -4:
								Cy4 = -Tmp1 + Tmp2;
								Cy2 = -Tmp4 + Tmp5;
								break;
							case -3:
								Cy4 = -Tmp2 - Tmp3;
								Cy2 = -Tmp5;
								break;
							case -2:
								Cy4 = -Tmp2;
								Cy2 = -Tmp5;
								break;
							case -1:
								Cy4 = -Tmp3;
								Cy2 = 0;
								break;
							case 0:
								Cy4 = 0;
								Cy2 = 0;
								break;
							case 1:
								Cy4 = Tmp2 - Tmp3;
								Cy2 = Tmp5;
								break;
							case 2:
								Cy4 = Tmp2;
								Cy2 = Tmp5;
								break;
							case 3:
								Cy4 = Tmp1 - Tmp2 - Tmp3;
								Cy2 = Tmp4 - Tmp5;
								break;
							case 4:
								Cy4 = Tmp1 - Tmp2;
								Cy2 = Tmp4 - Tmp5;
								break;
							case 5:
								Cy4 = Tmp1 - Tmp3;
								Cy2 = Tmp4;
								break;
							case 6:
								Cy4 = Tmp1;
								Cy2 = Tmp4;
								break;
							case 7:
								Cy4 = Tmp1 + Tmp2 - Tmp3;
								Cy2 = Tmp4 + Tmp5;
								break;
							case 8:
								Cy4 = Tmp1 + Tmp2;
								Cy2 = Tmp4 + Tmp5;
								break;
							}
							Cy4 += (Pr1 >>> 32) + (Pr2 >>> 32) + (Pr3 >>> 32) + (Pr4 >> 32);
							Cy2 += (Pr5 >>> 32) + (Pr6 >>> 32) + (Pr7 >> 32);
							if (i > 0) {
								CalcAuxModInvGamma[i - 1] = Pr4 & 0xFFFFFFFFL;
								CalcAuxModInvB[i - 1] = Pr7 & 0xFFFFFFFFL;
							}
						}

						if ((int) CalcAuxModInvA[i - 1] < 0) {
							Cy1 -= Yaa;
							Cy2 -= Yba;
						}
						if ((int) CalcAuxModInvB[i - 1] < 0) {
							Cy1 -= Yab;
							Cy2 -= Ybb;
						}
						if ((int) CalcAuxModInvMu[i - 1] < 0) {
							Cy3 -= Yaa;
							Cy4 -= Yba;
						}
						if ((int) CalcAuxModInvGamma[i - 1] < 0) {
							Cy3 -= Yab;
							Cy4 -= Ybb;
						}
						CalcAuxModInvA[i - 1] = Cy1 & 0xFFFFFFFFL;
						CalcAuxModInvB[i - 1] = Cy2 & 0xFFFFFFFFL;
						CalcAuxModInvMu[i - 1] = Cy3 & 0xFFFFFFFFL;
						CalcAuxModInvGamma[i - 1] = Cy4 & 0xFFFFFFFFL;
						continue outer_loop;
					}
					Bl >>= 1;
					Dif++;
					E++;
					T1++;
				}
				; /* end while */
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
			if (B[i] != CalcAuxModInvMu[i])
				break;
		}
		if (i < 0 || B[i] < CalcAuxModInvMu[i]) { // If B < Mu
			SubtractBigNbr32(CalcAuxModInvMu, B, CalcAuxModInvMu); // Mu <- Mu -
																	// B
		}
		Convert32To31Bits(CalcAuxModInvMu, inv);
	}

	private long modPow(long NbrMod, long Expon, long currentPrime) {
		long Power = 1;
		long Square = NbrMod;
		while (Expon != 0) {
			if ((Expon & 1) == 1) {
				Power = (Power * Square) % currentPrime;
			}
			Square = (Square * Square) % currentPrime;
			Expon /= 2;
		}
		return Power;
	}

	private void MontgomeryMult(long Nbr1[], long Nbr2[], long Prod[]) {
		int i, j;
		long MaxUInt = 0x7FFFFFFFl;
		long Pr, Nbr;
		long Prod0, Prod1, Prod2, Prod3, Prod4, Prod5, Prod6, Prod7;
		long Prod8, Prod9, Prod10;
		long TestNbr0, TestNbr1, TestNbr2, TestNbr3, TestNbr4, TestNbr5, TestNbr6, TestNbr7;
		long TestNbr8, TestNbr9, TestNbr10;
		long Nbr2_0, Nbr2_1, Nbr2_2, Nbr2_3, Nbr2_4, Nbr2_5, Nbr2_6, Nbr2_7;
		long Nbr2_8, Nbr2_9, Nbr2_10;
		long MontDig; // New, Prd;
		int MontgomeryMultN = (int) this.MontgomeryMultN;
		long TestNbr[] = this.TestNbr;
		int NumberLength = this.NumberLength;

		if (TerminateThread) {
			throw new ArithmeticException();
		}
		TestNbr0 = TestNbr[0];
		TestNbr1 = TestNbr[1];
		Nbr2_0 = Nbr2[0];
		Nbr2_1 = Nbr2[1];
		switch (NumberLength) {
		case 2:
			Prod0 = Prod1 = 0;
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = Pr >>> 31;
				i++;
			} while (i < 2);
			if (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0)) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = ((Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			break;

		case 3:
			Prod0 = Prod1 = Prod2 = 0;
			TestNbr2 = TestNbr[2];
			Nbr2_2 = Nbr2[2];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = Pr >>> 31;
				i++;
			} while (i < 3);
			if (Prod2 > TestNbr2
					|| Prod2 == TestNbr2 && (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = ((Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			break;

		case 4:
			Prod0 = Prod1 = Prod2 = Prod3 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = Pr >>> 31;
				i++;
			} while (i < 4);
			if (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2
					|| Prod2 == TestNbr2 && (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0)))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = ((Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			break;

		case 5:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = Pr >>> 31;
				i++;
			} while (i < 5);
			if (Prod4 > TestNbr4 || Prod4 == TestNbr4 && (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2
					|| Prod2 == TestNbr2 && (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = ((Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			break;

		case 6:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = Pr >>> 31;
				i++;
			} while (i < 6);
			if (Prod5 > TestNbr5 || Prod5 == TestNbr5 && (Prod4 > TestNbr4
					|| Prod4 == TestNbr4 && (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2
							|| Prod2 == TestNbr2 && (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0)))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = ((Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			break;

		case 7:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = Pr >>> 31;
				i++;
			} while (i < 7);
			if (Prod6 > TestNbr6 || Prod6 == TestNbr6
					&& (Prod5 > TestNbr5 || Prod5 == TestNbr5 && (Prod4 > TestNbr4 || Prod4 == TestNbr4
							&& (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2 || Prod2 == TestNbr2
									&& (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0))))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = (Pr = (Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
				Prod6 = ((Pr >> 31) + Prod6 - TestNbr6) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			break;

		case 8:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = Prod7 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			TestNbr7 = TestNbr[7];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			Nbr2_7 = Nbr2[7];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = (Pr = (Pr >>> 31) + MontDig * TestNbr7 + Nbr * Nbr2_7 + Prod7) & 0x7FFFFFFFl;
				Prod7 = Pr >>> 31;
				i++;
			} while (i < 8);
			if (Prod7 > TestNbr7 || Prod7 == TestNbr7 && (Prod6 > TestNbr6 || Prod6 == TestNbr6
					&& (Prod5 > TestNbr5 || Prod5 == TestNbr5 && (Prod4 > TestNbr4 || Prod4 == TestNbr4
							&& (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2 || Prod2 == TestNbr2
									&& (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0)))))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = (Pr = (Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
				Prod6 = (Pr = (Pr >> 31) + Prod6 - TestNbr6) & MaxUInt;
				Prod7 = ((Pr >> 31) + Prod7 - TestNbr7) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			Prod[7] = Prod7;
			break;

		case 9:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = Prod7 = Prod8 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			TestNbr7 = TestNbr[7];
			TestNbr8 = TestNbr[8];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			Nbr2_7 = Nbr2[7];
			Nbr2_8 = Nbr2[8];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = (Pr = (Pr >>> 31) + MontDig * TestNbr7 + Nbr * Nbr2_7 + Prod7) & 0x7FFFFFFFl;
				Prod7 = (Pr = (Pr >>> 31) + MontDig * TestNbr8 + Nbr * Nbr2_8 + Prod8) & 0x7FFFFFFFl;
				Prod8 = Pr >>> 31;
				i++;
			} while (i < 9);
			if (Prod8 > TestNbr8 || Prod8 == TestNbr8
					&& (Prod7 > TestNbr7 || Prod7 == TestNbr7 && (Prod6 > TestNbr6 || Prod6 == TestNbr6
							&& (Prod5 > TestNbr5 || Prod5 == TestNbr5 && (Prod4 > TestNbr4 || Prod4 == TestNbr4
									&& (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2 || Prod2 == TestNbr2
											&& (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0))))))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = (Pr = (Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
				Prod6 = (Pr = (Pr >> 31) + Prod6 - TestNbr6) & MaxUInt;
				Prod7 = (Pr = (Pr >> 31) + Prod7 - TestNbr7) & MaxUInt;
				Prod8 = ((Pr >> 31) + Prod8 - TestNbr8) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			Prod[7] = Prod7;
			Prod[8] = Prod8;
			break;

		case 10:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = Prod7 = Prod8 = Prod9 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			TestNbr7 = TestNbr[7];
			TestNbr8 = TestNbr[8];
			TestNbr9 = TestNbr[9];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			Nbr2_7 = Nbr2[7];
			Nbr2_8 = Nbr2[8];
			Nbr2_9 = Nbr2[9];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = (Pr = (Pr >>> 31) + MontDig * TestNbr7 + Nbr * Nbr2_7 + Prod7) & 0x7FFFFFFFl;
				Prod7 = (Pr = (Pr >>> 31) + MontDig * TestNbr8 + Nbr * Nbr2_8 + Prod8) & 0x7FFFFFFFl;
				Prod8 = (Pr = (Pr >>> 31) + MontDig * TestNbr9 + Nbr * Nbr2_9 + Prod9) & 0x7FFFFFFFl;
				Prod9 = Pr >>> 31;
				i++;
			} while (i < 10);
			if (Prod9 > TestNbr9 || Prod9 == TestNbr9 && (Prod8 > TestNbr8 || Prod8 == TestNbr8
					&& (Prod7 > TestNbr7 || Prod7 == TestNbr7 && (Prod6 > TestNbr6 || Prod6 == TestNbr6
							&& (Prod5 > TestNbr5 || Prod5 == TestNbr5 && (Prod4 > TestNbr4 || Prod4 == TestNbr4
									&& (Prod3 > TestNbr3 || Prod3 == TestNbr3 && (Prod2 > TestNbr2 || Prod2 == TestNbr2
											&& (Prod1 > TestNbr1 || Prod1 == TestNbr1 && (Prod0 >= TestNbr0)))))))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = (Pr = (Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
				Prod6 = (Pr = (Pr >> 31) + Prod6 - TestNbr6) & MaxUInt;
				Prod7 = (Pr = (Pr >> 31) + Prod7 - TestNbr7) & MaxUInt;
				Prod8 = (Pr = (Pr >> 31) + Prod8 - TestNbr8) & MaxUInt;
				Prod9 = ((Pr >> 31) + Prod9 - TestNbr9) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			Prod[7] = Prod7;
			Prod[8] = Prod8;
			Prod[9] = Prod9;
			break;

		case 11:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = Prod7 = Prod8 = Prod9 = Prod10 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			TestNbr7 = TestNbr[7];
			TestNbr8 = TestNbr[8];
			TestNbr9 = TestNbr[9];
			TestNbr10 = TestNbr[10];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			Nbr2_7 = Nbr2[7];
			Nbr2_8 = Nbr2[8];
			Nbr2_9 = Nbr2[9];
			Nbr2_10 = Nbr2[10];
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = (Pr = (Pr >>> 31) + MontDig * TestNbr7 + Nbr * Nbr2_7 + Prod7) & 0x7FFFFFFFl;
				Prod7 = (Pr = (Pr >>> 31) + MontDig * TestNbr8 + Nbr * Nbr2_8 + Prod8) & 0x7FFFFFFFl;
				Prod8 = (Pr = (Pr >>> 31) + MontDig * TestNbr9 + Nbr * Nbr2_9 + Prod9) & 0x7FFFFFFFl;
				Prod9 = (Pr = (Pr >>> 31) + MontDig * TestNbr10 + Nbr * Nbr2_10 + Prod10) & 0x7FFFFFFFl;
				Prod10 = Pr >>> 31;
				i++;
			} while (i < 11);
			if (Prod10 > TestNbr10 || Prod10 == TestNbr10 && (Prod9 > TestNbr9 || Prod9 == TestNbr9
					&& (Prod8 > TestNbr8 || Prod8 == TestNbr8 && (Prod7 > TestNbr7 || Prod7 == TestNbr7
							&& (Prod6 > TestNbr6 || Prod6 == TestNbr6 && (Prod5 > TestNbr5 || Prod5 == TestNbr5
									&& (Prod4 > TestNbr4 || Prod4 == TestNbr4 && (Prod3 > TestNbr3 || Prod3 == TestNbr3
											&& (Prod2 > TestNbr2 || Prod2 == TestNbr2 && (Prod1 > TestNbr1
													|| Prod1 == TestNbr1 && (Prod0 >= TestNbr0))))))))))) {
				Prod0 = (Pr = Prod0 - TestNbr0) & MaxUInt;
				Prod1 = (Pr = (Pr >> 31) + Prod1 - TestNbr1) & MaxUInt;
				Prod2 = (Pr = (Pr >> 31) + Prod2 - TestNbr2) & MaxUInt;
				Prod3 = (Pr = (Pr >> 31) + Prod3 - TestNbr3) & MaxUInt;
				Prod4 = (Pr = (Pr >> 31) + Prod4 - TestNbr4) & MaxUInt;
				Prod5 = (Pr = (Pr >> 31) + Prod5 - TestNbr5) & MaxUInt;
				Prod6 = (Pr = (Pr >> 31) + Prod6 - TestNbr6) & MaxUInt;
				Prod7 = (Pr = (Pr >> 31) + Prod7 - TestNbr7) & MaxUInt;
				Prod8 = (Pr = (Pr >> 31) + Prod8 - TestNbr8) & MaxUInt;
				Prod9 = (Pr = (Pr >> 31) + Prod9 - TestNbr9) & MaxUInt;
				Prod10 = ((Pr >> 31) + Prod10 - TestNbr10) & MaxUInt;
			}
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			Prod[7] = Prod7;
			Prod[8] = Prod8;
			Prod[9] = Prod9;
			Prod[10] = Prod10;
			break;

		default:
			Prod0 = Prod1 = Prod2 = Prod3 = Prod4 = Prod5 = Prod6 = Prod7 = Prod8 = Prod9 = Prod10 = 0;
			TestNbr2 = TestNbr[2];
			TestNbr3 = TestNbr[3];
			TestNbr4 = TestNbr[4];
			TestNbr5 = TestNbr[5];
			TestNbr6 = TestNbr[6];
			TestNbr7 = TestNbr[7];
			TestNbr8 = TestNbr[8];
			TestNbr9 = TestNbr[9];
			TestNbr10 = TestNbr[10];
			Nbr2_2 = Nbr2[2];
			Nbr2_3 = Nbr2[3];
			Nbr2_4 = Nbr2[4];
			Nbr2_5 = Nbr2[5];
			Nbr2_6 = Nbr2[6];
			Nbr2_7 = Nbr2[7];
			Nbr2_8 = Nbr2[8];
			Nbr2_9 = Nbr2[9];
			Nbr2_10 = Nbr2[10];
			for (j = 11; j < NumberLength; j++) {
				Prod[j] = 0;
			}
			i = 0;
			do {
				Pr = (Nbr = Nbr1[i]) * Nbr2_0 + Prod0;
				MontDig = ((int) Pr * MontgomeryMultN) & MaxUInt;
				Prod0 = (Pr = ((MontDig * TestNbr0 + Pr) >>> 31) + MontDig * TestNbr1 + Nbr * Nbr2_1 + Prod1)
						& 0x7FFFFFFFl;
				Prod1 = (Pr = (Pr >>> 31) + MontDig * TestNbr2 + Nbr * Nbr2_2 + Prod2) & 0x7FFFFFFFl;
				Prod2 = (Pr = (Pr >>> 31) + MontDig * TestNbr3 + Nbr * Nbr2_3 + Prod3) & 0x7FFFFFFFl;
				Prod3 = (Pr = (Pr >>> 31) + MontDig * TestNbr4 + Nbr * Nbr2_4 + Prod4) & 0x7FFFFFFFl;
				Prod4 = (Pr = (Pr >>> 31) + MontDig * TestNbr5 + Nbr * Nbr2_5 + Prod5) & 0x7FFFFFFFl;
				Prod5 = (Pr = (Pr >>> 31) + MontDig * TestNbr6 + Nbr * Nbr2_6 + Prod6) & 0x7FFFFFFFl;
				Prod6 = (Pr = (Pr >>> 31) + MontDig * TestNbr7 + Nbr * Nbr2_7 + Prod7) & 0x7FFFFFFFl;
				Prod7 = (Pr = (Pr >>> 31) + MontDig * TestNbr8 + Nbr * Nbr2_8 + Prod8) & 0x7FFFFFFFl;
				Prod8 = (Pr = (Pr >>> 31) + MontDig * TestNbr9 + Nbr * Nbr2_9 + Prod9) & 0x7FFFFFFFl;
				Prod9 = (Pr = (Pr >>> 31) + MontDig * TestNbr10 + Nbr * Nbr2_10 + Prod10) & 0x7FFFFFFFl;
				Prod10 = (Pr = (Pr >>> 31) + MontDig * TestNbr[11] + Nbr * Nbr2[11] + Prod[11]) & 0x7FFFFFFFl;
				for (j = 12; j < NumberLength; j++) {
					Prod[j - 1] = (Pr = (Pr >>> 31) + MontDig * TestNbr[j] + Nbr * Nbr2[j] + Prod[j]) & 0x7FFFFFFFl;
				}
				Prod[j - 1] = (Pr >>> 31);
				i++;
			} while (i < NumberLength);
			Prod[0] = Prod0;
			Prod[1] = Prod1;
			Prod[2] = Prod2;
			Prod[3] = Prod3;
			Prod[4] = Prod4;
			Prod[5] = Prod5;
			Prod[6] = Prod6;
			Prod[7] = Prod7;
			Prod[8] = Prod8;
			Prod[9] = Prod9;
			Prod[10] = Prod10;
			for (j = NumberLength - 1; j >= 0; j--) {
				if (Prod[j] != TestNbr[j]) {
					break;
				}
			}
			if (Prod[j] >= TestNbr[j]) {
				Pr = 0;
				for (j = 0; j < NumberLength; j++) {
					Prod[j] = (Pr = (Pr >> 31) + Prod[j] - TestNbr[j]) & MaxUInt;
				}
			}
		} /* end switch */
	}

	private void MultBigNbr(long Nbr1[], long Nbr2[], long Prod[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
		long Cy, Pr;
		int j;
		Cy = Pr = 0;
		for (int i = 0; i < NumberLength; i++) {
			Pr = Cy & MaxUInt;
			Cy >>>= 31;
			for (j = 0; j <= i; j++) {
				Pr += Nbr1[j] * Nbr2[i - j];
				Cy += (Pr >>> 31);
				Pr &= MaxUInt;
			}
			Prod[i] = Pr;
		}
	}

	private void MultBigNbrByLong(long Nbr1[], long Nbr2, long Prod[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
		long Pr;
		Pr = 0;
		for (int i = 0; i < NumberLength; i++) {
			Pr = (Pr >> 31) + Nbr2 * Nbr1[i];
			Prod[i] = Pr & MaxUInt;
		}
	}

	private void MultBigNbrByLongModN(long Nbr1[], long Nbr2, long Prod[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
		long Pr;
		int j;

		Pr = 0;
		for (j = 0; j < NumberLength; j++) {
			Pr = (Pr >>> 31) + Nbr2 * Nbr1[j];
			Prod[j] = Pr & MaxUInt;
		}
		Prod[j] = (Pr >>> 31);
		AdjustModN(Prod);
	}

	private void MultBigNbrModN(long Nbr1[], long Nbr2[], long Prod[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl;
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
				Prod[j] = Pr & MaxUInt;
			}
			Prod[j] += (Pr >>> 31);
			AdjustModN(Prod);
		} while (i > 0);
	}

	/**
	 * Normalize coefficient of JS.
	 * 
	 * @param PK
	 * @param PL
	 * @param PM
	 * @param P
	 */
	private void NormalizeJS(int PK, int PL, int PM, int P) {
		int I, J;
		for (I = PL; I < PK; I++) {
			if (BigNbrIsZero(aiJS[I]) == false) {
				for (J = 0; J < NumberLength; J++) {
					biT[J] = aiJS[I][J];
				}
				for (J = 1; J < P; J++) {
					SubtractBigNbrModN(aiJS[I - J * PM], biT, aiJS[I - J * PM]);
				}
				for (J = 0; J < NumberLength; J++) {
					aiJS[I][J] = 0;
				}
			}
		}
	}

	/**
	 * Normalize coefficient of JW.
	 * 
	 * @param PK
	 * @param PL
	 * @param PM
	 * @param P
	 */
	private void NormalizeJW(int PK, int PL, int PM, int P) {
		int I, J;
		for (I = PL; I < PK; I++) {
			if (BigNbrIsZero(aiJW[I]) == false) {
				for (J = 0; J < NumberLength; J++) {
					biT[J] = aiJW[I][J];
				}
				for (J = 1; J < P; J++) {
					SubtractBigNbrModN(aiJW[I - J * PM], biT, aiJW[I - J * PM]);
				}
				for (J = 0; J < NumberLength; J++) {
					aiJW[I][J] = 0;
				}
			}
		}
	}

	private int PowerCheck(int i) {
		int maxExpon = (PD[i].bitLength() - 1) / 17;
		int h, j;
		long modulus;
		int intLog2N;
		double log2N;
		BigInteger root, rootN1, rootN, dif, nextroot;
		int prime2310x1[] = { 2311, 4621, 9241, 11551, 18481, 25411, 32341, 34651, 43891, 50821 };
		// Primes of the form 2310x+1.
		boolean expon2 = true, expon3 = true, expon5 = true;
		boolean expon7 = true, expon11 = true;
		for (h = 0; h < prime2310x1.length; h++) {
			long testprime = prime2310x1[h];
			long mod = PD[i].mod(BigInteger.valueOf(testprime)).intValue();
			if (expon2 && modPow(mod, testprime / 2, testprime) > 1)
				expon2 = false;
			if (expon3 && modPow(mod, testprime / 3, testprime) > 1)
				expon3 = false;
			if (expon5 && modPow(mod, testprime / 5, testprime) > 1)
				expon5 = false;
			if (expon7 && modPow(mod, testprime / 7, testprime) > 1)
				expon7 = false;
			if (expon11 && modPow(mod, testprime / 11, testprime) > 1)
				expon11 = false;
		}
		boolean ProcessExpon[] = new boolean[maxExpon + 1];
		boolean primes[] = new boolean[2 * maxExpon + 3];
		for (h = 2; h <= maxExpon; h++) {
			ProcessExpon[h] = true;
		}
		for (h = 2; h < primes.length; h++) {
			primes[h] = true;
		}
		for (h = 2; h * h < primes.length; h++) { // Generation of primes
			for (j = h * h; j < primes.length; j += h) { // using Eratosthenes
															// sieve
				primes[j] = false;
			}
		}
		for (h = 13; h < primes.length; h++) {
			if (primes[h]) {
				int processed = 0;
				for (j = 2 * h + 1; j < primes.length; j += 2 * h) {
					if (primes[j]) {
						modulus = PD[i].mod(BigInteger.valueOf(j)).longValue();
						if (modPow(modulus, j / h, j) > 1) {
							for (j = h; j <= maxExpon; j += h) {
								ProcessExpon[j] = false;
							}
							break;
						}
					}
					if (++processed > 10)
						break;
				}
			}
		}
		for (int Exponent = maxExpon; Exponent >= 2; Exponent--) {
			if (Exponent % 2 == 0 && expon2 == false)
				continue; // Not a square
			if (Exponent % 3 == 0 && expon3 == false)
				continue; // Not a cube
			if (Exponent % 5 == 0 && expon5 == false)
				continue; // Not a fifth power
			if (Exponent % 7 == 0 && expon7 == false)
				continue; // Not a 7th power
			if (Exponent % 11 == 0 && expon11 == false)
				continue; // Not an 11th power
			if (ProcessExpon[Exponent] == false) {
				continue;
			}
			intLog2N = PD[i].bitLength() - 1;
			log2N = intLog2N + Math.log(PD[i].shiftRight(intLog2N - 32).add(BigInt1).doubleValue()) / Math.log(2) - 32;
			log2N /= Exponent;
			if (log2N < 32) {
				root = BigInteger.valueOf((long) Math.exp(log2N * Math.log(2)));
			} else {
				intLog2N = (int) Math.floor(log2N) - 32;
				root = BigInteger.valueOf((long) Math.exp((log2N - intLog2N) * Math.log(2)) + 10).shiftLeft(intLog2N);
			}
			while (true) {
				rootN1 = root.pow(Exponent - 1);
				rootN = root.multiply(rootN1);
				dif = PD[i].subtract(rootN);
				if (dif.signum() == 0) { // Perfect power
					PD[i] = root;
					Exp[i] *= Exponent;
					return 1;
				}
				nextroot = dif.add(BigInt1).divide(BigInteger.valueOf(Exponent).multiply(rootN1)).add(root)
						.subtract(BigInt1);
				if (root.compareTo(nextroot) <= 0)
					break; // Not a perfect power
				root = nextroot;
			}
		}
		return 0;
	}

	/**
	 * Computes nP from P=(x:z) and puts the result in (x:z). Assumes n>2.
	 * 
	 * @param n
	 * @param x
	 * @param z
	 * @param xT
	 * @param zT
	 * @param xT2
	 * @param zT2
	 */
	private void prac(int n, long[] x, long[] z, long[] xT, long[] zT, long[] xT2, long[] zT2) {
		int d, e, r, i;
		long[] t;
		long[] xA = x, zA = z;
		long[] xB = fieldAux1, zB = fieldAux2;
		long[] xC = fieldAux3, zC = fieldAux4;
		double v[] = { 1.61803398875, 1.72360679775, 1.618347119656, 1.617914406529, 1.612429949509, 1.632839806089,
				1.620181980807, 1.580178728295, 1.617214616534, 1.38196601125 };

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
		duplicate(xA, zA, xA, zA); /* A=2*A */
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
				duplicate(xA, zA, xA, zA); /* A = 2*A */
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
				duplicate(xA, zA, xA, zA); /* A = 2*A */
			} else if (d % 2 == 0) { /* condition 5 */
				d /= 2;
				add3(xC, zC, xC, zC, xA, zA, xB, zB); /* C = f(C,A,B) */
				duplicate(xA, zA, xA, zA); /* A = 2*A */
			} else if (d % 3 == 0) { /* condition 6 */
				d = d / 3 - e;
				duplicate(xT, zT, xA, zA); /* T1 = 2*A */
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
				duplicate(xT, zT, xA, zA);
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
				duplicate(xT, zT, xA, zA);
				add3(xA, zA, xA, zA, xT, zT, xA, zA); /* A = 3*A */
			} else if (e % 2 == 0) { /* condition 9 */
				e /= 2;
				add3(xC, zC, xC, zC, xB, zB, xA, zA); /* C = f(C,B,A) */
				duplicate(xB, zB, xB, zB); /* B = 2*B */
			}
		}
		add3(x, z, xA, zA, xB, zB, xC, zC);
	}

	/**
	 * Prime checking routine
	 * 
	 * @param N
	 * @return return codes: 0 = Number is prime. 1 = Number is composite.
	 */
	private static int ProbabilisticPrimeTest(BigInteger N) {
		return N.isProbablePrime(32) ? 0 : 1;
	}

	private long RemDivBigNbrByLong(long Dividend[], long Divisor) {
		int i;
		long Divid, Rem = 0;

		if (Divisor < 0) { // If divisor is negative...
			Divisor = -Divisor; // Convert divisor to positive.
		}
		if (Dividend[i = NumberLength - 1] >= 0x40000000l) { // If dividend is
																// negative...
			Rem = Divisor - 1;
		}
		for (; i >= 0; i--) {
			Divid = Dividend[i] + (Rem << 31);
			Rem = Divid % Divisor;
		}
		return Rem;
	}

	private void SortFactorsInputNbr() {
		int g, i, j;
		BigInteger Nbr1;

		for (g = 0; g < NbrFactors - 1; g++) {
			for (j = g + 1; j < NbrFactors; j++) {
				if (PD[g].compareTo(PD[j]) > 0) {
					Nbr1 = PD[g];
					PD[g] = PD[j];
					PD[j] = Nbr1;
					i = Exp[g];
					Exp[g] = Exp[j];
					Exp[j] = i;
					i = Typ[g];
					Typ[g] = Typ[j];
					Typ[j] = i;
				}
			}
		}
	}

	private void SubtractBigNbr(long Nbr1[], long Nbr2[], long Diff[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) + Nbr1[i] - Nbr2[i];
			Diff[i] = Cy & 0x7FFFFFFFl;
		}
	}

	private void SubtractBigNbr32(long Nbr1[], long Nbr2[], long Diff[]) {
		int NumberLength = this.NumberLength;
		long Cy = 0;
		for (int i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 32) + Nbr1[i] - Nbr2[i];
			Diff[i] = Cy & 0xFFFFFFFFl;
		}
	}

	private void SubtractBigNbrModN(long Nbr1[], long Nbr2[], long Diff[]) {
		int NumberLength = this.NumberLength;
		long MaxUInt = 0x7FFFFFFFl; // Integer.MAX_VALUE
		long Cy = 0;
		int i;

		for (i = 0; i < NumberLength; i++) {
			Cy = (Cy >> 31) + Nbr1[i] - Nbr2[i];
			Diff[i] = Cy & MaxUInt;
		}
		if (Cy < 0) {
			Cy = 0;
			for (i = 0; i < NumberLength; i++) {
				Cy = (Cy >> 31) + Diff[i] + TestNbr[i];
				Diff[i] = Cy & MaxUInt;
			}
		}
	}

	/**
	 * Increment the &quot;number of factors&quot;. Ensure that the resulting arrays have enough memory allocated.
	 */
	private Object[] incNbrFactors() {
		NbrFactors++;
		if (NbrFactors >= fCapacity) {
			Object[] objects = new Object[6];
			int oldCapacity = PD.length;
			fCapacity += 32;

			BigInteger localPD[] = new BigInteger[fCapacity];
			System.arraycopy(PD, 0, localPD, 0, oldCapacity);
			PD = localPD;
			objects[0] = PD;

			int localExp[] = new int[fCapacity];
			System.arraycopy(Exp, 0, localExp, 0, oldCapacity);
			Exp = localExp;
			objects[1] = Exp;

			int localTyp[] = new int[fCapacity];
			System.arraycopy(Typ, 0, localTyp, 0, oldCapacity);
			Typ = localTyp;
			objects[2] = Typ;

			BigInteger localPD1[] = new BigInteger[fCapacity];
			System.arraycopy(PD1, 0, localPD1, 0, oldCapacity);
			PD1 = localPD1;
			objects[3] = PD1;

			int localExp1[] = new int[fCapacity];
			System.arraycopy(Exp1, 0, localExp1, 0, oldCapacity);
			Exp1 = localExp1;
			objects[4] = Exp1;

			int localTyp1[] = new int[fCapacity];
			System.arraycopy(Typ1, 0, localTyp1, 0, oldCapacity);
			Typ1 = localTyp1;
			objects[5] = Typ1;
			return objects;
		}
		return null;
	}

	public static void ellipticCurveFactors(final BigInteger val, Map<BigInteger, Integer> map) {
		EllipticCurveMethod ecm = new EllipticCurveMethod(val);
		ecm.factorize(map);
	}
}
