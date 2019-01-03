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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver02_BlockLanczos;
import de.tilman_neumann.jml.factor.lehman.Lehman_Fast;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomery64;
import de.tilman_neumann.jml.factor.psiqs.PSIQS;
import de.tilman_neumann.jml.factor.psiqs.PSIQSBase;
import de.tilman_neumann.jml.factor.psiqs.PSIQS_U;
import de.tilman_neumann.jml.factor.siqs.SIQS;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.NoPowerFinder;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03gU;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_1Large_UBI;
import de.tilman_neumann.jml.factor.tdiv.TDiv31Inverse;
//import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.TimeUtil;

/**
 * Final combination of factor algorithms.
 * 
 * @author Tilman Neumann
 */
public class CombinedFactorAlgorithm extends FactorAlgorithmBase {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CombinedFactorAlgorithm.class);
	
	private TDiv31Inverse tDiv31 = new TDiv31Inverse();
	private Lehman_Fast lehman = new Lehman_Fast(true);
	private PollardRhoBrentMontgomery64 pollardRho = new PollardRhoBrentMontgomery64();
	private SIQS siqs_smallArgs;
	private PSIQSBase siqs_bigArgs;
	
	/**
	 * Simple constructor using PSIQS with sun.misc.Unsafe features.
	 * 
	 * @param numberOfThreads the number of parallel threads for PSIQS
	 * @param profile if true then extended profiling information is collected
	 */
	public CombinedFactorAlgorithm(int numberOfThreads, boolean profile) {
		this(numberOfThreads, true, profile);
	}
	
	/**
	 * Complete constructor.
	 * 
	 * @param numberOfThreads the number of parallel threads for PSIQS
	 * @param permitUnsafeUsage if true then PSIQS_U using sun.misc.Unsafe features is used. This may be ~10% faster.
	 * @param profile if true then extended profiling information is collected
	 */
	public CombinedFactorAlgorithm(int numberOfThreads, boolean permitUnsafeUsage, boolean profile) {
		// SIQS tuned for small N
		siqs_smallArgs = new SIQS(0.32F, 0.37F, null, 0.16F, new PowerOfSmallPrimesFinder(), new SIQSPolyGenerator(), new Sieve03gU(), new TDiv_QS_1Large_UBI(), 10, new MatrixSolver01_Gauss(), false);
		// PSIQS for bigger N: monolithic sieve is still slightly faster than SBH in the long run.
		if (permitUnsafeUsage) {
			siqs_bigArgs = new PSIQS_U(0.32F, 0.37F, null, null, numberOfThreads, new NoPowerFinder(), new MatrixSolver02_BlockLanczos(), profile);
		} else {
			siqs_bigArgs = new PSIQS(0.32F, 0.37F, null, null, numberOfThreads, new NoPowerFinder(), new MatrixSolver02_BlockLanczos(), profile);
		}
	}

	@Override
	public String getName() {
		return "combi";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		int NBits = N.bitLength();
		if (NBits<28) return tDiv31.findSingleFactor(N);
		if (NBits<50) return lehman.findSingleFactor(N);
		if (NBits<63) return pollardRho.findSingleFactor(N);
		if (NBits<97) return siqs_smallArgs.findSingleFactor(N);
		return siqs_bigArgs.findSingleFactor(N);
	}
	
	/**
	 * Run with command-line arguments or console input (if no command-line arguments are given).
	 * Usage for executable jar file:
	 * java -jar <jar_file> [[-t <numberOfThreads>] <numberToFactor>]
	 * 
	 * @param args [-t <numberOfThreads>] <numberToFactor>
	 */
	// Quite difficult 280 bit: 1794577685365897117833870712928656282041295031283603412289229185967719140138841093599 takes about 5:48 min
//	public static void main(String[] args) {
//		ConfigUtil.verbose = false;
//    	ConfigUtil.initProject();
//    	
//    	try {
//	    	if (args.length==0) {
//	    		// test standard input in a loop
//	    		testInput();
//	    	}
//	    	
//	    	// otherwise we have commandline arguments -> parse them
//	    	testArgs(args);
//    	} catch (Exception ite) {
//    		// when the jar is shut down with Ctrl-C, an InvocationTargetException is thrown (log4j?).
//    		// just suppress it and exit
//    		System.exit(0);
//    	}
//	}
	
	private static int testInput() {
		while(true) {
			int numberOfThreads = 1;
			BigInteger N;
			String line = null;
			try {
				System.out.println("Please insert [-t <numberOfThreads>] <numberToFactor> :");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				line = in.readLine();
				String input = line.trim();
				if (input.startsWith("-t")) {
					input = input.substring(2).trim();
					StringTokenizer parser = new StringTokenizer(input);
					numberOfThreads = Integer.parseInt(parser.nextToken().trim());
					N = new BigInteger(parser.nextToken().trim());
				} else {
					N = new BigInteger(input);
				}
			} catch (IOException ioe) {
				System.err.println("IO-error occuring on input: " + ioe.getMessage());
				continue;
			} catch (NumberFormatException nfe) {
				System.err.println("Illegal input: " + line);
				continue;
			}
			test(numberOfThreads, N);
		} // next input...
	}

	private static void testArgs(String[] args) {
    	int numberOfThreads = 1;
    	BigInteger N = null;
    	if (args.length==1) {
    		try {
    			N = new BigInteger(args[0].trim());
    		} catch (NumberFormatException nfe) {
    			System.err.println("Invalid numberToFactor = " + args[0].trim());
    			System.exit(-1);
    		}
    	} else if (args.length==3) {
    		if (!args[0].trim().equals("-t")) {
    			System.err.println("Illegal option: '" + args[0] + "'. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
    			System.exit(-1);
    		}
    		try {
    			numberOfThreads = Integer.parseInt(args[1].trim());
    		} catch (NumberFormatException nfe) {
    			System.err.println("Invalid numberOfThreads = " + args[1].trim());
    			System.exit(-1);
    		}
    		try {
    			N = new BigInteger(args[2].trim());
    		} catch (NumberFormatException nfe) {
    			System.err.println("Invalid numberToFactor = " + args[2].trim());
    			System.exit(-1);
    		}
    	} else {
			System.err.println("Illegal number of arguments. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
			System.exit(-1);
    	}
    	// run
    	int exitCode = test(numberOfThreads, N);
		System.exit(exitCode);
	}
	
	private static int test(int numberOfThreads, BigInteger N) {
		if (numberOfThreads < 0) {
			System.err.println("numberOfThreads must be positive.");
			return -1;
		}
//		if (numberOfThreads > ConfigUtil.NUMBER_OF_PROCESSORS) {
//			System.err.println("Too big numberOfThreads = " + numberOfThreads + ": Your machine has only " + ConfigUtil.NUMBER_OF_PROCESSORS + " processors");
//			return -1;
//		}
    	
    	// size check
    	//LOG.debug("N = " + N);
		int N_bits = N.bitLength();
    	if (N.bitLength()>400) {
			System.err.println("Too big numberToFactor: Currently only inputs <= 400 bits are supported. (Everything else would take months or years)");
			return -1;
    	}
    	// run
    	long t0 = System.currentTimeMillis();
    	CombinedFactorAlgorithm factorizer = new CombinedFactorAlgorithm(numberOfThreads, true);
    	SortedMultiset<BigInteger> result = factorizer.factor(N);
		long duration = System.currentTimeMillis()-t0;
		String durationStr = TimeUtil.timeStr(duration);
		if (result.totalCount()==1) {
			BigInteger singleElement = result.keySet().iterator().next();
			if (singleElement.abs().compareTo(I_1)<=0) {
				System.out.println(N + " is trivial");
			} else {
				System.out.println(N + " is probable prime");
			}
		} else if (result.totalCount()==2 && result.keySet().contains(I_MINUS_1)) {
			System.out.println(N + " is probable prime");
		} else {
			System.out.println(N + " (" + N_bits + " bits) = " + factorizer.getPrettyFactorString(result) + " (factored in " + durationStr + ")");
		}
		return 0;
	}
}
