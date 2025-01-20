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
package de.tilman_neumann.jml.factor;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.TimeUtil;

/**
 * Runner for CombinedFactorAlgorithm.
 * 
 * @author Tilman Neumann
 */
public class CombinedFactorAlgorithmRunner {
	private static final Logger LOG = LogManager.getLogger(CombinedFactorAlgorithmRunner.class);

	/**
	 * <strong>This is the only main() function we want to keep in the main scope of the Maven build, because it is the executable jar target.</strong><br>
	 * Run with command-line arguments or console input (if no command-line arguments are given).<br>
	 * Usage for executable jar file:<br>
	 * java [-Dlog4j2.configurationFile=some-log4j2-config-file] -jar jml-jar-file [[-t numberOfThreads] numberToFactor]<br><br>
	 * 
	 * Some test numbers:<br>
	 * 
	 * 15841065490425479923 (64 bit) = 2604221509 * 6082841047<br>
	 * 
	 * 11111111111111111111111111 (84 bit) = {11=1, 53=1, 79=1, 859=1, 265371653=1, 1058313049=1}<br>
	 * 
	 * 5679148659138759837165981543 (93 bit) = {3=3, 466932157=1, 450469808245315337=1}<br>
	 * 
	 * 874186654300294020965320730991996026550891341278308 (170 bit) = {2=2, 3=1, 471997=1, 654743=1, 2855761=1, 79833227=1, 982552477=1, 1052328969055591=1}<br>
	 * 
	 * 11111111111111111111111111155555555555111111111111111 (173 bit) = {67=1, 157=1, 1056289676880987842105819104055096069503860738769=1} (only tdiv needed)<br>
	 * 
	 * 1388091470446411094380555803943760956023126025054082930201628998364642 (230 bit) = {2=1, 3=1, 1907=1, 1948073=1, 1239974331653=1, 50222487570895420624194712095309533522213376829=1}<br>
	 * 
	 * 10^71-1 = 99999999999999999999999999999999999999999999999999999999999999999999999 (236 bit) = {3=2, 241573142393627673576957439049=1, 45994811347886846310221728895223034301839=1}<br>
	 * 
	 * 10^79+5923 = 10000000000000000000000000000000000000000000000000000000000000000000000000005923 (263 bit) = {1333322076518899001350381760807974795003=1, 7500063320115780212377802894180923803641=1}<br>
     *
     * 1794577685365897117833870712928656282041295031283603412289229185967719140138841093599 (280 bit) = 42181796536350966453737572957846241893933 * 42543889372264778301966140913837516662044603<br>
     *
	 * 2900608971182010301486951469292513060638582965350239259380273225053930627446289431038392125 (301 bit)<br>
	 * = 33333 * 33335 * 33337 * 33339 * 33341 * 33343 * 33345 * 33347 * 33349 * 33351 * 33353 * 33355 * 33357 * 33359 * 33361 * 33363 * 33367 * 33369 * 33369 * 33371<br>
	 * = {3=11, 5=3, 7=6, 11=2, 13=2, 17=2, 19=1, 37=1, 41=1, 53=1, 59=1, 61=1, 73=1, 113=1, 151=1, 227=2, 271=1, 337=1, 433=1, 457=1, 547=1, 953=1, 11113=1, 11117=1, 11119=1, 33343=1, 33347=1, 33349=1, 33353=1, 33359=1}<br>
	 * (only tdiv)
	 * 
	 * @param args [-t numberOfThreads] numberToFactor
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
    	
    	try {
	    	if (args.length==0) {
	    		// test standard input in a loop
	    		testInput();
	    	}
	    	
	    	// otherwise we have commandline arguments -> parse them
	    	testArgs(args);
    	} catch (Exception ite) {
    		// when the jar is shut down with Ctrl-C, an InvocationTargetException is thrown (log4j?).
    		// just suppress it and exit
    		System.exit(0);
    	}
	}
	
	private static int testInput() {
		while(true) {
			int numberOfThreads = 1;
			BigInteger N;
			String line = null;
			try {
				LOG.info("Please insert [-t <numberOfThreads>] <numberToFactor> :");
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
				LOG.error("IO-error occurring on input: " + ioe.getMessage());
				continue;
			} catch (NumberFormatException nfe) {
				LOG.error("Illegal input: " + line);
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
    			LOG.error("Invalid numberToFactor = " + args[0].trim());
    			System.exit(-1);
    		}
    	} else if (args.length==3) {
    		if (!args[0].trim().equals("-t")) {
    			LOG.error("Illegal option: '" + args[0] + "'. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
    			System.exit(-1);
    		}
    		try {
    			numberOfThreads = Integer.parseInt(args[1].trim());
    		} catch (NumberFormatException nfe) {
    			LOG.error("Invalid numberOfThreads = " + args[1].trim());
    			System.exit(-1);
    		}
    		try {
    			N = new BigInteger(args[2].trim());
    		} catch (NumberFormatException nfe) {
    			LOG.error("Invalid numberToFactor = " + args[2].trim());
    			System.exit(-1);
    		}
    	} else {
    		LOG.error("Illegal number of arguments. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
			System.exit(-1);
    	}
    	// run
    	int exitCode = test(numberOfThreads, N);
		System.exit(exitCode);
	}
	
	private static int test(int numberOfThreads, BigInteger N) {
		if (numberOfThreads < 0) {
			LOG.error("numberOfThreads must be positive.");
			return -1;
		}
		if (numberOfThreads > ConfigUtil.NUMBER_OF_PROCESSORS) {
			LOG.error("Too big numberOfThreads = " + numberOfThreads + ": Your machine has only " + ConfigUtil.NUMBER_OF_PROCESSORS + " processors");
			return -1;
		}
    	
    	// size check
    	//LOG.debug("N = " + N);
		int N_bits = N.bitLength();
    	if (N.bitLength()>500) {
    		LOG.error("Too big numberToFactor: Currently only inputs <= 500 bits are supported. (Everything else would take months or years)");
			return -1;
    	}
    	// run
    	long t0 = System.currentTimeMillis();
    	CombinedFactorAlgorithm factorizer = new CombinedFactorAlgorithm(numberOfThreads, null, true);
    	SortedMultiset<BigInteger> result = factorizer.factor(N);
		long duration = System.currentTimeMillis()-t0;
		String durationStr = TimeUtil.timeStr(duration);
		if (result.totalCount()==0 || (result.totalCount()==1 && result.getSmallestElement().abs().compareTo(I_1)<=0)) {
			LOG.info(N + " is trivial");
		} else if (result.totalCount()==1 || (result.totalCount()==2 && result.keySet().contains(I_MINUS_1))) {
			LOG.info(N + " (" + N_bits + " bits) is probable prime");
		} else {
			//LOG.debug("result = " + result);
			LOG.info(N + " (" + N_bits + " bits) = " + result.toString("*", "^") + " (factored in " + durationStr + ")");
		}
		return 0;
	}
}
