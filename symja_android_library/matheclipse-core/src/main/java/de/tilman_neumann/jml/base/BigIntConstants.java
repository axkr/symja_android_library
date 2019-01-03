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

public class BigIntConstants {
	
	/** -1 */				public static final BigInteger I_MINUS_1 = BigInteger.ONE.negate();
	
	/** 0 */				public static final BigInteger I_0 = BigInteger.ZERO;
	/** 1 */				public static final BigInteger I_1 = BigInteger.ONE;
	/** 2 */				public static final BigInteger I_2 = BigInteger.valueOf(2);
	/** 3 */				public static final BigInteger I_3 = BigInteger.valueOf(3);
	/** 4 */				public static final BigInteger I_4 = BigInteger.valueOf(4);
	/** 5 */				public static final BigInteger I_5 = BigInteger.valueOf(5);
	/** 6 */				public static final BigInteger I_6 = BigInteger.valueOf(6);
	/** 7 */				public static final BigInteger I_7 = BigInteger.valueOf(7);
	/** 8 */				public static final BigInteger I_8 = BigInteger.valueOf(8);
	/** 9 */				public static final BigInteger I_9 = BigInteger.valueOf(9);
	/** 10 */				public static final BigInteger I_10 = BigInteger.TEN;
	/** 11 */				public static final BigInteger I_11 = BigInteger.valueOf(11);
	/** 12 */				public static final BigInteger I_12 = BigInteger.valueOf(12);
	/** 13 */				public static final BigInteger I_13 = BigInteger.valueOf(13);
	/** 14 */				public static final BigInteger I_14 = BigInteger.valueOf(14);
	/** 15 */				public static final BigInteger I_15 = BigInteger.valueOf(15);
	/** 16 */				public static final BigInteger I_16 = BigInteger.valueOf(16);
	/** 17 */				public static final BigInteger I_17 = BigInteger.valueOf(17);
	/** 18 */				public static final BigInteger I_18 = BigInteger.valueOf(18);
	/** 19 */				public static final BigInteger I_19 = BigInteger.valueOf(19);
	/** 20 */				public static final BigInteger I_20 = BigInteger.valueOf(20);
	/** 21 */				public static final BigInteger I_21 = BigInteger.valueOf(21);
	/** 22 */				public static final BigInteger I_22 = BigInteger.valueOf(22);
	/** 23 */				public static final BigInteger I_23 = BigInteger.valueOf(23);
	/** 24 */				public static final BigInteger I_24 = BigInteger.valueOf(24);
	/** 25 */				public static final BigInteger I_25 = BigInteger.valueOf(25);
	/** 26 */				public static final BigInteger I_26 = BigInteger.valueOf(26);
	/** 27 */				public static final BigInteger I_27 = BigInteger.valueOf(27);
	/** 28 */				public static final BigInteger I_28 = BigInteger.valueOf(28);
	/** 29 */				public static final BigInteger I_29 = BigInteger.valueOf(29);
	/** 30 */				public static final BigInteger I_30 = BigInteger.valueOf(30);
	
	/** 32 */				public static final BigInteger I_32 = BigInteger.valueOf(32);
	/** 40 */				public static final BigInteger I_40 = BigInteger.valueOf(40);
	/** 48 */				public static final BigInteger I_48 = BigInteger.valueOf(48);
	/** 50 */				public static final BigInteger I_50 = BigInteger.valueOf(50);
	/** 60 */				public static final BigInteger I_60 = BigInteger.valueOf(60);
	/** 64 */				public static final BigInteger I_64 = BigInteger.valueOf(64);
	/** 100 */				public static final BigInteger I_100 = BigInteger.valueOf(100);
	/** 1000 */				public static final BigInteger I_1E3 = BigInteger.valueOf(1000);
	/** 10000 */			public static final BigInteger I_1E4 = BigInteger.valueOf(10000);
	/** 100000 */			public static final BigInteger I_1E5 = BigInteger.valueOf(100000);
	/** a million */		public static final BigInteger I_1E6 = BigInteger.valueOf(1000000);
	/** ten million */		public static final BigInteger I_1E7 = BigInteger.valueOf(10000000);
	/** 100 million */		public static final BigInteger I_1E8 = BigInteger.valueOf(100000000);
	/** max pow exponent */	public static final BigInteger I_MAX_EXPONENT = BigInteger.valueOf(999999999);
	/** 10^9 */				public static final BigInteger I_1E9 = BigInteger.valueOf(1000000000);
	/** max integer */		public static final BigInteger I_MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
	/** 10^10 */			public static final BigInteger I_1E10 = BigInteger.valueOf(10000000000L);
	/** 10^11 */			public static final BigInteger I_1E11 = BigInteger.valueOf(100000000000L);
	/** 10^12 */			public static final BigInteger I_1E12 = BigInteger.valueOf(1000000000000L);
	/** 10^13 */			public static final BigInteger I_1E13 = BigInteger.valueOf(10000000000000L);
	/** 10^14 */			public static final BigInteger I_1E14 = BigInteger.valueOf(100000000000000L);
	/** 10^15 */			public static final BigInteger I_1E15 = BigInteger.valueOf(1000000000000000L);
	/** 10^16 */			public static final BigInteger I_1E16 = BigInteger.valueOf(10000000000000000L);
	/** 10^17 */			public static final BigInteger I_1E17 = BigInteger.valueOf(100000000000000000L);
	/** 10^18 */			public static final BigInteger I_1E18 = BigInteger.valueOf(1000000000000000000L);
	/** 10^19 */			public static final BigInteger I_1E19 = I_1E10.multiply(I_1E9);
	/** 10^20 */			public static final BigInteger I_1E20 = I_1E10.multiply(I_1E10);
}
