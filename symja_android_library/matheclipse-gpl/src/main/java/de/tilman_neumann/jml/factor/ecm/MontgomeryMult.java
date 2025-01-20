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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Montgomery multiplication, extracted from Dario Alpern's Ecm program.
 * 
 * @author Dario Alpern, refactoring by Tilman Neumann
 */
public class MontgomeryMult {
	
	private static final Logger LOG = LogManager.getLogger(MontgomeryMult.class);
	
	/** the modulus N */
	private final int[] TestNbr;
	
	/** the length of N in 31-bit words */
	private final int NumberLength;
	
	/** (-1/N[0]) mod R, where the reducer R = 2^31 and N[0] is the least significant 31-bit word of N */
	private final int MontgomeryMultN;

	public MontgomeryMult(int[] TestNbr, int NumberLength) {
		this.TestNbr = TestNbr;
		this.NumberLength = NumberLength;
		
		int N, x;
		x = N = (int) TestNbr[0]; // now x*N==1 mod 2^2
		x = x * (2 - N * x); // now x*N==1 mod 2^4
		x = x * (2 - N * x); // now x*N==1 mod 2^8
		x = x * (2 - N * x); // now x*N==1 mod 2^16
		x = x * (2 - N * x); // now x*N==1 mod 2^32
		MontgomeryMultN = (-x) & 0x7FFFFFFF;
	}
	
	// This "kind of over-optimized" method yields a 10-20% performance gain compared
	// to a flat j=0..NumberLength-1 loop implementation. So we keep it as it is...
	public void mul(int Nbr1[], int Nbr2[], int Prod[]) {
		int i, j;
		long MaxUInt = 0x7FFFFFFFl;
		long Pr, Nbr;
		long Prod0, Prod1, Prod2, Prod3, Prod4, Prod5, Prod6, Prod7;
		long Prod8, Prod9, Prod10;
		long TestNbr0, TestNbr1, TestNbr2, TestNbr3, TestNbr4, TestNbr5, TestNbr6, TestNbr7;
		long TestNbr8, TestNbr9, TestNbr10;
		long Nbr2_0, Nbr2_1, Nbr2_2, Nbr2_3, Nbr2_4, Nbr2_5, Nbr2_6, Nbr2_7;
		long Nbr2_8, Nbr2_9, Nbr2_10;
		long MontDig;

		TestNbr0 = TestNbr[0];
		TestNbr1 = TestNbr[1];
		Nbr2_0 = Nbr2[0];
		Nbr2_1 = Nbr2[1];
		switch (NumberLength) {
		case 0:
		case 1:
			// NumberLength<2 can only happen if ECM was invoked without running trial division up to 2^33 before
			LOG.error("Montgomery multiplication was called for a number with NumberLength = " + NumberLength, new Throwable());
			
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
			Prod[7] = (int)Prod7;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
			Prod[7] = (int)Prod7;
			Prod[8] = (int)Prod8;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
			Prod[7] = (int)Prod7;
			Prod[8] = (int)Prod8;
			Prod[9] = (int)Prod9;
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
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
			Prod[7] = (int)Prod7;
			Prod[8] = (int)Prod8;
			Prod[9] = (int)Prod9;
			Prod[10] = (int)Prod10;
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
			        Prod[j-1] = (int)((Pr = (Pr >>> 31) + MontDig * TestNbr[j] + Nbr * Nbr2[j] + Prod[j]) & 0x7FFFFFFFL);
				}
				Prod[j - 1] = (int)(Pr >>> 31);
				i++;
			} while (i < NumberLength);
			Prod[0] = (int)Prod0;
			Prod[1] = (int)Prod1;
			Prod[2] = (int)Prod2;
			Prod[3] = (int)Prod3;
			Prod[4] = (int)Prod4;
			Prod[5] = (int)Prod5;
			Prod[6] = (int)Prod6;
			Prod[7] = (int)Prod7;
			Prod[8] = (int)Prod8;
			Prod[9] = (int)Prod9;
			Prod[10] = (int)Prod10;
			for (j = NumberLength - 1; j >= 0; j--) {
				if (Prod[j] != TestNbr[j]) {
					break;
				}
			}
			if (Prod[j] >= TestNbr[j]) {
				Pr = 0;
				for (j = 0; j < NumberLength; j++) {
			        Prod[j] = (int)((Pr = (Pr >> 31) + (long)Prod[j] - (long)TestNbr[j]) & 0x7FFFFFFFL);
				}
			}
		} /* end switch */
	}
}
