package de.tilman_neumann.jml.primes.exact;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.math.BigInteger.ONE;

/**
 This Java source file is a multiple threaded implementation to perform an
 extremely fast Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.

 Inputs are single values N, of 64-bits, 0 -- 2^64 - 1.
 Output is the number of twin primes <= N; the last
 twin prime value for the range; and the total time of execution.

 Run as Java application, and enter a range (comma or space separated) when asked in console.
 (Accept scientific notation)

 This java source file, and updates, will be available here:
 <a href="https://gist.github.com/Pascal66/4d4229e88f4002641ddcaa5eccd0f6d5">gist.github.com/Pascal66</a>

 Example :
 Please enter an range of integer (comma or space separated):
 0 1e11
 Logicals processors used = 12
 Using Prime Generator parameters for given Pn 13
 segment size = 524288 resgroups; seg array is [1 x 8192] 64-bits
 twinprime candidates = 4945055940 ; resgroups = 3330004
 each 1485 threads has nextp[2 x 27287] array
 setup time = 0.015 secs
 perform twinprimes ssoz sieve
 1485 of 1485 threads done
 sieve time = 4.538 secs
 last segment = 184276 resgroups; segment slices = 7
 total twins = 224376048; last twin = 99999999762+/-1
 total time = 4.553 secs

 Please enter an range of integer (comma or space separated):
 0 1e12
 Logicals processors used = 12
 Using Prime Generator parameters for given Pn 13
 segment size = 802816 resgroups; seg array is [1 x 12544] 64-bits
 twinprime candidates = 49450550490 ; resgroups = 33300034
 each 1485 threads has nextp[2 x 78492] array
 setup time = 0.045 secs
 perform twinprimes ssoz sieve
 1485 of 1485 threads done
 sieve time = 51.672 secs
 last segment = 384578 resgroups; segment slices = 42
 total twins = 1870585220; last twin = 999999999960+/-1
 total time = 51.717 secs

 The Jabari Zakiya code source files can be found here: <a href="https://gist.github.com/jzakiya">gist.github.com/jzakiya</a>

 Mathematical and technical basis for implementation are explained here:
 https://www.academia.edu/81206391/Twin_Primes_Segmented_Sieve_of_Zakiya_SSoZ_Explained

 This code is provided free and subject to copyright and terms of the
 GNU General Public License Version 3, GPLv3, or greater.
 License copy/terms are here:  <a href="http://www.gnu.org/licenses/">gnu.org/licenses</a>

 Copyright (c) 2017-22 Jabari Zakiya -- jzakiya at gmail dot com
 Java version 0.21.5 - Pascal Pechard -- pascal at priveyes dot net
 Version Date: 2022/07/01
 */

public class SSOZJ5 {

	static final BigInteger TWO = ONE.add(ONE);
	static final BigInteger THREE = TWO.add(ONE);

	static long KB = 0L;				// segment size for each seg restrack
	static BigInteger start_num;		// lo number for range
	static BigInteger end_num;			// hi number for range
//	static BigInteger Kmax = ZERO;		// number of resgroups to end_num
//	static BigInteger Kmin;				// number of resgroups to start_num
	static ArrayDeque<Long> primes;		// list of primes r1..sqrt(N)
	static long[] cnts;					// hold twin primes counts for seg bytes
	static long[] lastwins;				// holds largest twin prime <= num in each thread
//	static BigInteger modpg;			// PG's modulus value
//	static long Lmodpg;			// PG's modulus value
	static long res_0;					// PG's first residue value
	static LinkedList<Long> restwins;	// PG's list of twinpair residues
	static long[] resinvrs;				// PG's list of residues inverses

	static BitSet segment;

	static int Bn;       				// segment size factor for PG and input number
	static final int S = 6;				// 0|3 for 1|8 resgroups/byte for 'small|large' ranges
	static final long BMASK= (1L << S) - 1L;

	/**
	 * Create Prime Generator parameters for given Pn.
	 * Using BigInteger permit Optimized gcd and ModInverse
	 * At build time, both version (Long and BigInteger are created)
	 * @param prime int
	 */
	private static void genPGparameters(int prime){
		System.out.println("Using Prime Generator parameters for given Pn "+ prime);
		final long[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23};
		PGparam.Lmodpg = 1L/*modpg = ONE*/;  res_0 = 0L;
		for (long prm : primes){ res_0 = prm ;
			if (prm > prime) break;
			//modpg = modpg.multiply(BigInteger.valueOf(res_0));
			PGparam.Lmodpg = PGparam.Lmodpg * res_0;
		}

		LinkedList<Long> restwins = new LinkedList<>(); 		// save upper twin pair residues here
		long[] inverses = new long[(int) (PGparam.Lmodpg+2)/*modpg.intValue()+2*/];			// save PG's residues inverses here
		long Lpc = 5/*BigInteger pc = THREE.add(TWO)*/; int inc = 2; long/*BigInteger*/ res = 0; //ZERO;
		while (Lpc < PGparam.Lmodpg >> 1) { //.compareTo(modpg.divide(TWO)) < 0) { while (pc.compareTo(modpg.divide(TWO)) < 0) {       	// find a residue, then modular complement
			if (eGcd(PGparam.Lmodpg, Lpc)[0] == 1) { //if (modpg.gcd(pc).equals(ONE)) {          			// if pc a residue
				final long Lpc_mc = PGparam.Lmodpg - Lpc; // final BigInteger pc_mc = Lmodpg - Lpc; //.subtract(pc);	// create its modular complement
				long inv_r = eModInv(Lpc, PGparam.Lmodpg); //pc.modInverse(modpg).intValue();// modinv(pc, modpg);  // compute residues's inverse
				inverses[(int) Lpc/*pc.intValue()*/]= inv_r;					// save its inverse
				inverses[(int) inv_r] = Lpc; //pc.intValue();     			// save its inverse inverse

				inv_r = eModInv( Lpc_mc, PGparam.Lmodpg); //pc_mc.modInverse(modpg).intValue(); 	// compute residues's complement inverse
				inverses[(int) Lpc_mc/*.intValue()*/] = inv_r;				// save its inverse
				inverses[(int) inv_r] = Lpc_mc/*.intValue()*/;   			// save its inverse inverse
				if (res + 2 == Lpc) { //if (res.add(TWO).equals(pc)){
					restwins.add(Lpc/*.longValue()*/);
					restwins.add(Lpc_mc + 2); } //.add(TWO).longValue());} 	// save hi_tp residues
				res = Lpc;
			}
			Lpc += inc; // BigInteger.valueOf(Lpc.longValue() + inc);
			inc ^= 0b110;
		}
//		restwins.sort(Comparator.naturalOrder()); // Already sorted
		restwins.add(PGparam.Lmodpg + 1L); //.add(ONE).longValue());   // last residue is last hi_tp
		inverses[(int) (PGparam.Lmodpg + 1)/*.add(ONE).intValue()*/]= 1; //ONE.intValue();
		inverses[(int) (PGparam.Lmodpg - 1)/*.subtract(ONE).intValue()*/]= PGparam.Lmodpg - 1; //.subtract(ONE).intValue(); // last 2 residues are self inverses

		new PGparam(/*PGparam.Lmodpg,*/ res_0, restwins, inverses);
	}

	/**
	 * Here we store all we need
	 * This doesnt penalize nothing
	 * Migration en cours...
	 */
	static class PGparam{
		public static Long Lmodpg;
		public static Long Lkmin;
		public static Long Lkmax;
		public static Long Lstart;
		public static Long Lend;
		public static int primesSize;
		static int segByteSize = 0;
		private static Long Lrange;

		public PGparam(/*long modpg,*/ Long res_0, LinkedList<Long> restwins, long[] inverses) {
//			SSOZJ5.modpg = modpg;
//			Lmodpg = modpg/*.intValueExact()*/;
			SSOZJ5.res_0 = res_0;
			SSOZJ5.restwins = restwins;
			SSOZJ5.resinvrs = inverses;

//			Kmin = start_num.subtract(TWO).divide(modpg).add(ONE);	// number of resgroups to start_num
//			Kmax = end_num.subtract(TWO).divide(modpg).add(ONE);	// number of resgroups to end_num

			Lkmin= (Lstart - 2) / Lmodpg + 1; //Kmin.longValue();
			Lkmax = (Lend - 2) / Lmodpg + 1; //Kmax.longValue();

			Lrange = Lkmax - Lkmin + 1;			// number of range resgroups, at least 1
			int n = Lrange < 37_500_000_000_000L? 4 : Lrange < 975_000_000_000_000L? 6 : 8;
			long B = (Bn * 1024L * n);			// set seg size to optimize for selected PG
			KB = Math.min(Lrange, B);			// segments resgroups size
			// Doing that one time is enough
			segByteSize = (int) ((KB-1 >>> S) + 1);

			System.out.println("segment size = "+KB+" resgroups; seg array is [1 x "+segByteSize+"] "+(BMASK+1L)+"-bits");
			long maxpairs = Lrange * SSOZJ5.restwins.size();     // maximum number of twinprime pcs
			System.out.println("twinprime candidates = "+ maxpairs+ " ; resgroups = "+ Lrange);
			}
	}

	/**
	 * Select at runtime best PG and segment size factor to use for input value.
	 * These are good estimates derived from PG data profiling. Can be improved.
	 *
	 * @param start_range
	 * @param end_range
	 */
	static void setSieveParameters(BigInteger start_range, BigInteger end_range) {
		final BigInteger range = end_range.subtract(start_range);

//		if(range.compareTo(TWO.pow(31).subtract(ONE)) > 0)
//			System.out.println("Range Warning, Integer Overflow ");
		System.out.println(start_range.bitLength() +" "+ end_range.bitLength());

		int pg = 3;
		if (end_range.compareTo(BigInteger.valueOf(49L)) < 0) {
			Bn = 1;
			pg = 3;
		} else if (range.compareTo(BigInteger.valueOf(24_000_000L))<0) {
			Bn = 16;
			pg = 5;
		} else if (range.compareTo(BigInteger.valueOf(1_100_000_000L))<0) {
			Bn = 32;
			pg = 7;
		} else if (range.compareTo(BigInteger.valueOf(35_500_000_000L))<0) {
			Bn = 64;
			pg = 11;
		} else if (range.compareTo(BigInteger.valueOf(15_000_000_000_000L))<0) {
			pg = 13;
			if (range.compareTo(BigInteger.valueOf(7_000_000_000_000L)) > 0) {
				Bn = 384;
			} else if (range.compareTo(BigInteger.valueOf(2_500_000_000_000L)) > 0) {
				Bn = 320;
			} else if (range.compareTo(BigInteger.valueOf(250_000_000_000L)) > 0) {
				Bn = 196;
			} else {
				Bn = 128;
			}
		}	else
		{
			Bn = 384;
			pg = 17;
		}
		// Set value for 'small' or 'large' ranges to opt sieving
//		S = range.compareTo(BigInteger.valueOf(100_000_000_000L))<0 ? 0 : 3;
		genPGparameters(pg);
	}

	/**
	 * Compute the primes r1..sqrt(input_num) and store in global 'primes' array.
	 * Any algorithm (fast|small) is usable. Here the SoZ for P5 is used.
	 * @param val
	 */
	static void sozpg(long val) {
		final int md = 30;             // P5's modulus value
		final int rscnt = 8;           // P5's residue count

		final int[] res ={7,11,13,17,19,23,29,31}; // P5's residues list
		final int[] posn = {0,0,0,0,0,0,0,0,0,1, 0,2,0,0,0,3,0,4,0,0, 0,5,0,0,0,0,0,6,0,7};

		final long sqrtN = Lsqrt(val);  	// integer sqrt of sqrt(input value)
		final int kmax = (int) ((val - 7L) / md + 1L);   // number of resgroups to input value

		short[] prms = new short[kmax]; 		// byte array of prime candidates init '0'
		int modk = 0; int r = -1; int k = 0; 	// init residue parameters
		// mark the multiples of the primes r1..sqrtN in 'prms'
		while (true){
			r++;
			if (r == rscnt){ r = 0; modk += md; k++; }
			if ((prms[k] & (1 << r)&0XFF) != 0) continue; 	// skip pc if not prime
			final int prm_r = res[r];   			// if prime save its residue value
			final int prime = modk + prm_r; 		// numerate the prime value
			if (prime > sqrtN) break;  				// we're finished when it's > sqrtN
			// mark prime's multiples in prms
			for (int ri : res) {
				final int prod = prm_r * ri - 2;    // compute cross-product for prm_r|ri pair
				final int bit_r = (1 << posn[(int) floorMod(prod, md)]) & 0XFF; // bit mask for prod's residue
				int kpm = k * (prime + ri) + prod / md;   // 1st resgroup for prime mult
				while (kpm < kmax) {
					prms[kpm] |= bit_r;
					kpm += prime;
				}
			}
		}
		primes = new ArrayDeque<Long>();  				// create empty dynamic array for primes

			//  prms now contains the nonprime positions for the prime candidates r1..N
			//  extract primes into global var 'primes'
				IntStream.range(0, kmax).forEach(km -> {            // for each resgroup
					int[] ri = {0};
					for (int res_r : res) {                        // extract the primes in numerical order
						if ((prms[km] & (1 << ri[0]++) & 0XFF) == 0) {
							primes.add((long) md * km + res_r);
						}
					}
				});
		while (primes.getFirst() < res_0) primes.pollFirst();
		while (primes.getLast() > val/*.longValue()*/) primes.pollLast();
	}

	/**
	 Initialize 'nextp' array for twinpair upper residue rhi in 'restwins'.
	 Compute 1st prime multiple resgroups for each prime r0..sqrt(N) and
	 store consecutively as lo_tp|hi_tp pairs for their restracks.
	 * @param hi_r twin pair residues
	 * @param indexStart start of search range
	 * @return nextp 1st mults array for twin pair
	 */
	private static long[] nextp_init(long hi_r, final long indexStart) {
		final long[] nextp = new long[PGparam.primesSize << 1];	// 1st mults array for twin pair
		long r_lo = hi_r - 2;				// upper|lower twin pair residues

		final int[] j = {0};
		// for each prime r1..sqrt(N)
		// No parallel -> faster but error in primes last & cnt
		primes/*.parallelStream()*/.forEach(prime-> {
			long residueGroup = (prime - 2) / PGparam.Lmodpg;					// find the resgroup it's in
			int residue = (int)(floorMod((prime - 2) , PGparam.Lmodpg) + 2);    // and its residue value
			long residueInverse = resinvrs[residue];							// and its residue inverse

			long residueLow = floorMod((r_lo * residueInverse - 2) , PGparam.Lmodpg) + 2;	// compute the ri for r for lo_tp
			long indexLow = (residueGroup * (prime + residueLow) + (residue * residueLow - 2) / PGparam.Lmodpg);
			if (indexStart > indexLow) {								// if 1st mult index < start_num's
				indexLow = floorMod((indexStart - indexLow) , prime); 			// how many indices short is it
				if (indexLow > 0) indexLow = prime - indexLow;                // adjust index value into range
			} else indexLow -= indexStart;								// else here, adjust index if it was >
			nextp[j[0] << 1] = indexLow;							// lo_tp index val >= start of range

			long residueHigh = floorMod((hi_r * residueInverse - 2) , PGparam.Lmodpg) + 2;	// compute the ri for r for hi_tp
			long indexHigh = residueGroup * (prime + residueHigh) + (residue * residueHigh - 2) / PGparam.Lmodpg;
			if (indexStart > indexHigh) { 								// if 1st mult index < start_num's
				indexHigh = floorMod((indexStart - indexHigh) , prime);			// how many indices short is it
				if (indexHigh > 0) indexHigh = prime - indexHigh;				// adjust index value into range
			} else indexHigh -= indexStart;								// else here, adjust index if it was >
			nextp[j[0] << 1 | 1] = indexHigh;						// hi_tp index val >= start of range
			j[0]++;
		});
		return nextp;
	}

	/**
	 * Perform in a thread, the ssoz for a given twinpair, for Kmax resgroups.
	 * First create|init 'nextp' array of 1st prime mults for given twin pair,
	 * (stored consequtively in 'nextp') and init seg byte array for KB resgroups.
	 * For sieve, mark resgroup bits to '1' if either twinpair restrack is nonprime,
	 * for primes mults resgroups, and update 'nextp' restrack slices acccordingly.
	 * <p>
	 * Find last twin prime|sum for range, store in their arrays for this twinpair.
	 * Can optionally build to print mid twinprime values generated by twinpair.
	 * Uses optimum segment sieve structure for 'small' and 'large' range values.
	 *
	 * @param indx thread index
     * @param r_hi long
	 */
	private static void twins_sieve(int indx, long r_hi) {
		long kmin = PGparam.Lkmin-1;
		long kmax = PGparam.Lkmax;
		long sum = 0; long Kn = KB; // init twins cnt|1st resgroup for slice
		long hi_tp = 0;  			// max tp|resgroup, Kmax for slice

		final long[] seg = new long[PGparam.segByteSize];	// seg byte array for Kb resgroups

		if ((kmax-1) * PGparam.Lmodpg + (r_hi) > PGparam.Lend) kmax--; 	// and hi tps in range
		if (kmin * PGparam.Lmodpg + (r_hi - 2) < PGparam.Lstart) kmin++;// ensure lo tps in range
		final long[] nextp = nextp_init(r_hi, kmin);			// 1st prime mults for twin pair restracks
		final int[] j = {0};
		// for Kn resgroup size slices upto Kmax
		for (;kmin < kmax; kmin += KB) {
			if (KB > kmax - kmin) Kn = kmax - kmin;				// set last slice resgroup size
			long finalKn = Kn;
			// No parallel -> No change
			primes/*.parallelStream()*/.forEach(prime-> {
				// for lower twin pair residue track
				long k = nextp[j[0] << 1];						// starting from this resgroup in seg
				for(; k < finalKn; k += prime)
					seg[(int) (k >>> S)] |= (1L << (k & BMASK));// mark primenth resgroup bits prime mults and set next prime multiple resgroup
				nextp[j[0] << 1] = (k - finalKn);					// save 1st resgroup in next eligible seg
				// for upper twin pair residue track
				k = nextp[j[0] << 1 | 1];						// starting from this resgroup in seg
				for(; k < finalKn; k += prime) 						// mark primenth resgroup bits prime mults
					seg[(int) (k >>> S)] |= (1L << (k & BMASK));// set next prime multiple resgroup
				nextp[j[0] << 1 | 1] = (k - finalKn);				// save 1st resgroup in next eligible seg
				j[0]++;
			});

			int upk = (int) (Kn - 1);
			seg[upk >>> S] |= ((-(2L << (upk & BMASK))));
			int cnt = 0;                        // init seg twin primes count then find seg sum
			for (int m = (upk >>> S); m > -1; m--) cnt += (1 << S) - Long.bitCount(seg[m]);
			if (cnt > 0) {                     // if segment has twin primes
				sum += cnt;                    // add the segment count to total count
				// from end of seg, count backwards to largest tp
				while ((seg[(upk >>> S)] & (1L << (upk & BMASK))) != 0) upk--;
				hi_tp = kmin + upk;            // numerate its full resgroup value
			}

			//			kmin += KB;                        // set 1st resgroup val of next seg slice
			if (kmin < kmax) Arrays.fill(seg, 0); // NEEDED
			//			if (kmin < kmax) for (int b=upk>>>S; b>-1;b--) seg[b] = 0;// set seg to all primes
												// when sieve done for full range
												// numerate largest twinprime in segs
			j[0] = 0;
		}
		// numerate largest twin prime in segs
		hi_tp = r_hi > PGparam.Lend ? 0 : hi_tp * PGparam.Lmodpg + r_hi;

		lastwins[indx] = (sum == 0 ? 1 : hi_tp);// store final seg tp value
		cnts[indx] = sum;                     	// sum for twin pair
	}

	/**
	 * Main routine to setup, time, and display results for twin primes sieve.
	 */
	public static void twinprimes_ssoz(BigInteger start, BigInteger stop) {
		start = start.max(THREE);
		BigInteger end = stop.max(THREE);

		start_num = start.or(ONE); 			 // if start_num even add 1
		end_num = end.subtract(ONE).or(ONE); // if end_num even subtract 1

		PGparam.Lstart = start_num.longValue();
		PGparam.Lend = end_num.longValue();

		System.out.println(" Logicals processors used = "+ (countProcessors()));
		long ts = epochTime();             // start timing sieve setup execution

		setSieveParameters(start_num, end_num);	// select PG and seg factor Bn for input range
		final int pairscnt = restwins.size();	// number of twin pairs for selected PG
		cnts = new long[pairscnt];				// array to hold count of tps for each thread
		lastwins = new long[pairscnt];			// array to hold largest tp for each thread

		if (PGparam.Lend < 49L) primes.add((5L));	// generate sieving primes
		else sozpg(Lsqrt(PGparam.Lend));					// <= sqrt(end_num)

		PGparam.primesSize = primes.size();
		System.out.println("each "+ pairscnt+ " threads has nextp["+ 2+ " x "+ PGparam.primesSize + "] array");

		long twinscnt = 0;                 		// number of twin primes in range
		final long lo_range = restwins.getFirst() - 3; 	// lo_range = lo_tp - 1

		for (int tp : new int[]{3, 5, 11, 17}) {		// excluded low tp values for PGs used
			if (end_num.equals(THREE)) break;        	// if 3 end of range, no twin primes
			if (tp >= PGparam.Lstart && tp <= lo_range)  twinscnt++;
		}
		long te = epochTime() - ts;				// sieve setup time

		System.out.println("setup time = "+ te/1e3 + " secs");
		System.out.println("perform twinprimes ssoz sieve");

		ExecutorService stealingPool = Executors.newWorkStealingPool();

		List<Runnable> sieveTask = new ArrayList<>();
		// For printing progress just for debug
//		final Callback<String> callback = System.out::print;

		AtomicInteger indx = new AtomicInteger();
		for (long r_hi : restwins) {    // for each twin pair row index
			sieveTask.add(() -> {
//				callback.on("\r"+indx.get() + " of "+ pairscnt+ " threads done");
				twins_sieve(indx.getAndIncrement(), r_hi); // sieve selected twin pair restracks
			});
		}
		final long t1 = epochTime();				// start timing ssoz sieve execution
//		// Implement parallel things
		try {
			stealingPool.submit(()->sieveTask.parallelStream().forEach(Runnable::run)).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
//			// when all the threads finish
			stealingPool.shutdown();
			System.out.println("\r"+indx + " of "+ pairscnt+ " threads done");
		}
//		// OR Simple parallel without specific pool
//		sieveTask.parallelStream().forEach(Runnable::run);

		long last_twin = 0L;            // find largest twin prime in range
		twinscnt += Arrays.stream(cnts).sum();
		last_twin = Arrays.stream(lastwins).max().orElse(0L);

		if (PGparam.Lend == 5L && twinscnt == 1) last_twin = 5L;
		long Kn = floorMod(PGparam.Lrange, KB);		// set number of resgroups in last slice
		if (Kn == 0) Kn = KB;              		// if multiple of seg size set to seg size

		cnts = null; lastwins = null;			// Free memory
		long t2 = epochTime() - t1;				// sieve execution time

		System.out.println("sieve time = "+ t2/1e3 + " secs");
		System.out.println("last segment = "+ Kn+ " resgroups; segment slices = "+ ((PGparam.Lrange-1) / KB + 1));
		System.out.println("total twins = "+ twinscnt+ "; last twin = "+ (last_twin-1) + "+/-1");

		System.out.println("total time = "+ (t2 + te)/1e3 + " secs\n");

	}

	private static int countProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/** Granularity tens of milliseconds.*/
	private static long epochTime() {
		return System.currentTimeMillis();
	}

	public static BigInteger Bsqrt(BigInteger x) {
		BigInteger div = BigInteger.ZERO.setBit(x.bitLength() >> 1);
		BigInteger div2 = div;
		// Loop until we hit the same value twice in a row, or wind up alternating.
		for(;;) {
			BigInteger y = div.add(x.divide(div)).shiftRight(1);
			if (y.equals(div) || y.equals(div2))
				return div.min(div2);
			div2 = div;
			div = y;
		}
	}

	private static long Lsqrt(long val) {
//		System.out.println(Long.bitCount((long) Math.sqrt(Long.MAX_VALUE)));
		return (long) Math.sqrt(val);
	}

	/** This function performs the extended euclidean algorithm on two numbers a and b.
	 The function returns the gcd(a,b) as well as the numbers x and y such
	 that ax + by = gcd(a,b). This calculation is important in number theory
	 and can be used for several things such as finding modular inverses and
	 solutions to linear Diophantine equations.*/
	private static long[] eGcd(long a, long b) {
		if (b == 0) return new long[] {a, 1L, 0L};
		long[] v = eGcd(b, a % b);
		long tmp = v[1] - v[2] * (a / b);
		v[1] = v[2];
		v[2] = tmp;
		return v;
	}

	/** Returns the modular inverse of 'a' mod 'm' if it exists.
	 Make sure m > 0 and 'a' & 'm' are relatively prime.*/
	public static Long eModInv(long a, long m) {

		if (m <= 0) throw new ArithmeticException("mod must be > 0");

		// Avoid a being negative
		a = ((a % m) + m) % m;

		long[] v = eGcd(a, m);
		long gcd = v[0];
		long x = v[1];

		if (gcd != 1) return null;
		return ((x + m) % m) % m;
	}

	/**
	 * According to the Java Language Spec, Java's % operator is a remainder operator, not a modulo operator.
	 * @param x a long
	 * @param y a long
	 * @return a long modulo
	 */
	private static long floorMod(long x, long y) {
		return ((x >> 31) & (y - 1)) + (x % y);
	}
	
	@FunctionalInterface
	public interface Callback<T> {
		void on(T event);
	}
}

