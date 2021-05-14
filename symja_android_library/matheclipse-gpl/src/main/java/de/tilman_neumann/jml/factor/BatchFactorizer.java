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
package de.tilman_neumann.jml.factor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.matheclipse.core.numbertheory.SortedMultiset;

import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.TimeUtil;

/**
 * Factor all entries from a batch file.
 * 
 * Sample run command: -t 6 ./src/de/tilman_neumann/jml/factor/qaTests.txt
 * 
 * @author Tilman Neumann
 */
public class BatchFactorizer {
	private static final Logger LOG = Logger.getLogger(BatchFactorizer.class);
	
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		int numberOfThreads = 1;
		String fileName = null;
		String line = null;
		try {
			System.out.println("Please insert [-t <numberOfThreads>] <batchFile> :");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			line = in.readLine();
			String input = line !=null ? line.trim() : "";
			if (input.startsWith("-t")) {
				input = input.substring(2).trim();
				StringTokenizer parser = new StringTokenizer(input);
				numberOfThreads = Integer.parseInt(parser.nextToken().trim());
				fileName = parser.nextToken().trim();
			} else {
				fileName = input;
			}
		} catch (IOException ioe) {
			System.err.println("IO-error occuring on input: " + ioe.getMessage());
			return;
		} catch (NumberFormatException nfe) {
			System.err.println("Illegal input: " + line);
			return;
		}
		test(numberOfThreads, fileName);
	}
	
	private static void test (int numberOfThreads, String fileName) {
		// We read and report all input numbers first, to be sure they were read correctly
		// if some problem occurs in the factor tests
		LOG.info("Reading test numbers from file " + fileName + ":");
		ArrayList<BigInteger> testNumbers = new ArrayList<BigInteger>();
		try (FileInputStream is = new FileInputStream(fileName)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while (line != null) {
				if (!line.trim().startsWith("//")) { // skip line comment
					try {
						BigInteger N = new BigInteger(line.trim());
						LOG.info("    Found N = " + N + " (" + N.bitLength() + " bits) ...");
						testNumbers.add(N);
					} catch (NumberFormatException nfe) {
						LOG.error("    Not a number: " + line);
					}
				}
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			LOG.error("Error reading file " + fileName + ": " + ioe, ioe);
		}
		
		// Now test all test numbers in the order they were found
		CombinedFactorAlgorithm factorizer = new CombinedFactorAlgorithm(numberOfThreads, null, true);
		for (BigInteger N : testNumbers) {
			try {
				LOG.info("Factoring " + N + " (" + N.bitLength() + " bits) ...");
				long start = System.currentTimeMillis();
				SortedMultiset<BigInteger> factors = factorizer.factor(N);
				long duration = System.currentTimeMillis() - start;
				
				Set<BigInteger> keys = factors.keySet();
				BigInteger smallestFoundFactor = keys!=null && !keys.isEmpty() ? keys.iterator().next() : null;
				int smallestFoundFactorBitLength = smallestFoundFactor!=null ? smallestFoundFactor.bitLength() : 0;
				if (smallestFoundFactorBitLength > 0) {
					LOG.info("Found factorization of N = " + N + " = " + factors + " (smallest factor has " + smallestFoundFactorBitLength + " bits) in " + TimeUtil.timeStr(duration));
				} else {
					LOG.info("No factor found of N = " + N + "; is it prime? Computation took " + TimeUtil.timeStr(duration));
				}
			} catch (Exception | Error e) {
				LOG.error("An error occurred during the factorization of N = " + N + ": " + e, e);
			}
		}
		LOG.info("Batch run complete, exit.");
	}
}
