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
package de.tilman_neumann.jml;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;

/**
 * Test Collatz or 3n+1 problem.
 * @author Tilman Neumann
 */
public class CollatzSequenceTest {
	private static final Logger LOG = Logger.getLogger(CollatzSequenceTest.class);
	
	private static final int N_COUNT = 30000000;
	private static final int START_PROGRESSION = 2; // 2 is best for dropping sequences; 2, 3, 9 are good for repeat sequences
	private static final int MAX_PROGRESSION = 1<<20;
	
	/**
	 * Run the 3n+1 sequence until we find some n<nStart.
	 */
	private static void test3nPlus1DroppingSequence() {
		TreeSet<Integer> lengths = new TreeSet<>();
		int maxLength = 0;
		ArrayList<Integer> recordLengths = new ArrayList<Integer>();
		ArrayList<BigInteger> recordLengthsN = new ArrayList<BigInteger>();
		// convention: length(1)=0
		recordLengths.add(0);
		recordLengthsN.add(I_1);
		
		// analyze the length of all dropping sequences, arranged by length
		TreeMap<Integer, ArrayList<BigInteger>> length2NStartList = new TreeMap<Integer, ArrayList<BigInteger>>();
		
		// Define arithmetic progressions of dropping sequences: progr(mul, add) = {mul*n+add, n=0,1,2,...}
		// Then we analyze the maximal length of sequences of progression progr(mul, add).
		// Many such progressions have a small maximal length.
		// Progressions that do not have a small maximal length are called "unbounded".
		// Finally we analyze the counts of unbounded progressions for sets {progr(mul, add), add=0..(mul-1)}
		// The smallest numbers of unbounded proressions ar achieved for mul being powers of 2.
		TreeMap<Integer, TreeMap<Integer, Integer>> progressionToMaxLength = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		
		HashSet<BigInteger> resolved = new HashSet<>();
		resolved.add(I_1);
		for (int i=2; i<N_COUNT; i++) {
			ArrayList<BigInteger> sequence = new ArrayList<>();
			BigInteger nStart = BigInteger.valueOf(i);
			BigInteger n = nStart;
			sequence.add(n);
			while (true) {
				if ((n.intValue()&1)==1) {
					n = n.multiply(I_3).add(I_1);
				} else {
					n = n.shiftRight(1);
				}
				sequence.add(n);
				if (resolved.contains(n)) {
					resolved.add(nStart);
					// sequence length is the number of operations, not the number of elements in the list
					int length = sequence.size() - 1;
					//LOG.info("len=" + length + ": " + sequence);
					lengths.add(length);
					if (length > maxLength) {
						maxLength = length;
						LOG.info("record length=" + length + " at n=" + nStart);
						// sequence of records = A217934 = 0, 1, 6, 11, 96, 132, 171, 220, 267, 269, 282, 287, 298, 365, 401, 468, 476, 486, 502, 613, 644, 649, 706, 729, 892, 897, 988, 1122, 1161, 1177, 1187, 1445, 1471, 1575, 1614, 1639
						recordLengths.add(length);
						recordLengthsN.add(nStart);
					}
					addToLength2NMap(length2NStartList, length, nStart);
					addToProgressionToMaxLengthMap(progressionToMaxLength, nStart, length);
					break;
				}
			}
		}
		
		LOG.info("lengths   = " + lengths); // A122437
		// data below comes from N_COUNT=100M (16GB RAM hardly enough)
		LOG.info("record lengths   = " + recordLengths); // A217934 = (0,) 1, 6, 11, 96, 132, 171, 220, 267, 269, 282, 287, 298, 365, 401, 468, 476, 486, 502, 613
		LOG.info("N(record lengths)= " + recordLengthsN); // A060412 = (1,) 2, 3, 7, 27, 703, 10087, 35655, 270271, 362343, 381727, 626331, 1027431, 1126015, 8088063, 13421671, 20638335, 26716671, 56924955, 63728127
		
		analyzeNStartSequences(length2NStartList);
		analyzeProgressions(progressionToMaxLength);
	}
	
	/**
	 * Run the 2n+1 sequence until we find some n that was already contained in the sequence of some smaller n.
	 */
	@SuppressWarnings("unused")
	private static void test3nPlus1RepeatSequence() {
		TreeSet<Integer> lengths = new TreeSet<>();
		int maxLength = 0;
		ArrayList<Integer> recordLengths = new ArrayList<Integer>();
		ArrayList<BigInteger> recordLengthsN = new ArrayList<BigInteger>();
		// convention: length(1)=0
		recordLengths.add(0);
		recordLengthsN.add(I_1);
		
		// analyze the length of all repeat sequences, arranged by length
		TreeMap<Integer, ArrayList<BigInteger>> length2NStartList = new TreeMap<Integer, ArrayList<BigInteger>>();

		// Define arithmetic progressions of "repeat" sequences: progr(mul, add) = {mul*n+add, n=0,1,2,...}
		// Then we analyze the maximal length of sequences of progression progr(mul, add).
		// Many such progressions have a small maximal length.
		// Progressions that do not have a small maximal length are called "unbounded".
		// Finally we analyze the counts of unbounded progressions for sets {progr(mul, add), add=0..(mul-1)}
		// The smallest numbers of unbounded progressions are achieved for mul of the form 2^r*3^s, s small.
		TreeMap<Integer, TreeMap<Integer, Integer>> progressionToMaxLength = new TreeMap<Integer, TreeMap<Integer, Integer>>();

		HashSet<BigInteger> resolved = new HashSet<>();
		resolved.add(I_1);
		for (int i=2; i<N_COUNT; i++) {
			ArrayList<BigInteger> sequence = new ArrayList<>();
			BigInteger nStart = BigInteger.valueOf(i);
			BigInteger n = nStart;
			sequence.add(n);
			resolved.add(n);
			while (true) {
				if ((n.intValue()&1)==1) {
					n = n.multiply(I_3).add(I_1);
				} else {
					n = n.shiftRight(1);
				}
				sequence.add(n);
				if (resolved.contains(n)) {
					// sequence length is the number of operations, not the number of elements in the list
					int length = sequence.size() - 1;
					//LOG.info("len=" + length + ": " + sequence);
					lengths.add(length);
					if (length > maxLength) {
						maxLength = length;
						LOG.info("record length=" + length + " at n=" + nStart);
						recordLengths.add(length);
						recordLengthsN.add(nStart);
					}
					addToLength2NMap(length2NStartList, length, nStart);
					addToProgressionToMaxLengthMap(progressionToMaxLength, nStart, length);
					break;
				}
				resolved.add(n);
			}
		}
		
		LOG.info("lengths   = " + lengths); // all naturals except for 2, 4
		// the following data stems from N_COUNT=30M, which required about 12GB of RAM
		LOG.info("record lengths   = " + recordLengths); // not on OEIS: 0, 1, 6, 10, 95, 132, 219, 262, 269, 271, 297, 305, 343, 357, 400, 468, 485
		LOG.info("N(record lengths)= " + recordLengthsN); // not on OEIS: 1, 2, 3, 7, 27, 703, 35655, 270271, 362343, 401151, 1027431, 1327743, 1394431, 6206655, 8088063, 13421671, 26716671
		
		analyzeNStartSequences(length2NStartList);
		analyzeProgressions(progressionToMaxLength);
	}
	
	private static void addToLength2NMap(TreeMap<Integer, ArrayList<BigInteger>> length2NList, int length, BigInteger nStart) {
		ArrayList<BigInteger> nList = length2NList.get(length);
		if (nList==null) nList = new ArrayList<BigInteger>();
		nList.add(nStart);
		length2NList.put(length, nList);
	}
	
	private static void analyzeNStartSequences(TreeMap<Integer, ArrayList<BigInteger>> length2NList) {
		for (int length : length2NList.keySet()) {
			ArrayList<BigInteger> nList = length2NList.get(length);
			//LOG.debug("length=" + length + ": n0List = " + nList);
			int period = findPeriod(nList);
			if (period>0) {
				List<BigInteger> nPeriodList = nList.subList(0, period);
				BigInteger periodDiff = nList.get(period).subtract(nList.get(0));
				LOG.info("length=" + length + ": #n0=" + nList.size() + ", period=" + period + ", periodDiff=" + periodDiff + ", nPeriodList = " + nPeriodList);
			} else {
				LOG.info("length=" + length + ": #n0=" + nList.size() + ", period not identified");
			}
		}
	}
	
	private static int findPeriod(ArrayList<BigInteger> nList) {
		int nCount = nList.size();
		ArrayList<BigInteger> diffs = new ArrayList<BigInteger>();
		for (int i=1; i<nCount; i++) {
			diffs.add(nList.get(i).subtract(nList.get(i-1)));
		}
		//LOG.debug("diffs = " + diffs);
		int diffCount = diffs.size(); // should be nCount-1
		for (int period=1; period<diffCount; period++) {
			int i;
			for (i=period; i<diffCount; i++) {
				if (!diffs.get(i).equals(diffs.get(i-period))) {
					// not the correct period
					break;
				}
			}
			if (i==diffCount && diffCount/period >= 2) {
				// all differences confirmed -> we found the correct period!
				return period;
			}
		}
		// nothing found
		return 0;
	}
	
	private static void addToProgressionToMaxLengthMap(TreeMap<Integer, TreeMap<Integer, Integer>> progressionToMaxLength, BigInteger nStart, int length) {
		for (int mul=START_PROGRESSION; mul<=MAX_PROGRESSION; mul<<=1) {
			int add = nStart.mod(BigInteger.valueOf(mul)).intValue();
			TreeMap<Integer, Integer> add2MaxLength = progressionToMaxLength.get(mul);
			if (add2MaxLength==null) add2MaxLength = new TreeMap<Integer, Integer>();
			Integer maxLength = add2MaxLength.get(add);
			if (maxLength==null || length > maxLength) {
				add2MaxLength.put(add, length);
				progressionToMaxLength.put(mul, add2MaxLength);
			}
		}
	}
	
	private static void analyzeProgressions(TreeMap<Integer, TreeMap<Integer, Integer>> progressionToMaxLength) {
		ArrayList<Integer> unboundedProgressionCounts = new ArrayList<Integer>();
		for (Integer mul : progressionToMaxLength.keySet()) {
			// Find maximal bounded maxLength for arithmetic progressions with given multiplier
			TreeSet<Integer> maxLengths = new TreeSet<Integer>();
			for (Map.Entry<Integer, Integer> add2MaxLength : progressionToMaxLength.get(mul).entrySet()) {
				maxLengths.add(add2MaxLength.getValue());
			}
			int maxBoundedLength = 2;
			for (int maxLength : maxLengths) { // bottom-up
				if (maxLength - maxBoundedLength <= 3) {
					maxBoundedLength = maxLength;
				}
			}
			// count unbounded arithmetic progressions
			int unboundedProgressionsCount = 0;
			for (Map.Entry<Integer, Integer> add2MaxLength : progressionToMaxLength.get(mul).entrySet()) {
				int progrMaxLength = add2MaxLength.getValue();
				//LOG.info("Progression " + mul + "*n + " + add2MaxLength.getKey() + ": maxLength = " + progrMaxLength);
				if (progrMaxLength > maxBoundedLength) {
					unboundedProgressionsCount++;
				}
			}
			LOG.info("mul=" + mul + ": maxBoundedLength = " + maxBoundedLength + ", unboundedProgressionsCount = " + unboundedProgressionsCount);
			unboundedProgressionCounts.add(unboundedProgressionsCount);
		}
		LOG.info("sequence of unbounded progression counts: " + unboundedProgressionCounts);
		// A076227 = Number of surviving Collatz residues mod 2^n = 1, 1, 1, 2, 3, 4, 8, 13, 19, 38, 64, 128, 226, 367, 734, 1295, 2114, 4228, 7495, 14990, 27328, 46611, 93222, 168807, 286581, 573162, 1037374, 1762293, 3524586, 6385637, 12771274, 23642078, 41347483, 82694966, 151917636, 263841377, 527682754, 967378591, 1934757182, 3611535862
	}
	
	public static void main(String[] args) {
		ConfigUtil.initProject();
		test3nPlus1DroppingSequence();
//		test3nPlus1RepeatSequence();
	}
}
