package de.tilman_neumann.jml.primes.exact;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * This Java source file is a multiple threaded implementation to perform an extremely fast
 * Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.
 * 
 * Inputs are a range values N, of 64-bits, 0 -- 2^64 - 1. Output is the number of twin primes <= N;
 * the last twin prime value for the range; and the total time of execution.
 * 
 * Example : 0 2e11 Max threads = 8 Using Prime Generator parameters for given Pn 13 segment size =
 * 524288 resgroups; seg array is [1 x 8192] 64-bits twinprime candidates = 9890110395 ; resgroups =
 * 6660007 each 1485 threads has nextp[2 x 37493] array setup time = 0.111 secs perform twinprimes
 * ssoz sieve 1485 of 1485 threads done sieve time = 23.793 secs last segment = 368551 resgroups;
 * segment slices = 13 total twins = 424084653; last twin = 199999999890+/-1 total time = 23.904
 * secs
 * 
 * Original nim source file, and updates, available here:
 * https://gist.github.com/jzakiya/6c7e1868bd749a6b1add62e3e3b2341e Original d source file, and
 * updates, available here: https://gist.github.com/jzakiya/ae93bfa03dbc8b25ccc7f97ff8ad0f61
 * Original rust source file, and updates, available here:
 * https://gist.github.com/jzakiya/b96b0b70cf377dfd8feb3f35eb437225
 * 
 * Mathematical and technical basis for implementation are explained here:
 * https://www.academia.edu/37952623The_Use_of_Prime_Generators_to_Implement_Fast_Twin_Primes_Sieve_of_Zakiya_SoZ_Applications_to_Number_Theory_and_Implications_for_the_Riemann_Hypotheses
 * https://www.academia.edu/7583194/The_Segmented_Sieve_of_Zakiya_SSoZ
 * https://www.academia.edu/19786419/PRIMES-UTILS_HANDBOOK
 * 
 * This code is provided free and subject to copyright and terms of the GNU General Public License
 * Version 3, GPLv3, or greater. License copy/terms are here: http://www.gnu.org/licenses/
 * 
 * Copyright (c) 2017-20 Jabari Zakiya -- jzakiya at gmail dot com Java version 0.21.1B - Pascal
 * Pechard -- pascal at priveyes dot net Version Date: 2020/01/12
 */

public class SSOZJ {

  static final BigInteger TWO = ONE.add(ONE);
  static final BigInteger THREE = TWO.add(ONE);

  static long KB = 0L; // segment size for each seg restrack
  static BigInteger start_num; // lo number for range
  static BigInteger end_num; // hi number for range
  static BigInteger Kmax = ZERO; // number of resgroups to end_num
  static BigInteger Kmin; // number of resgroups to start_num
  static ArrayDeque<Long> primes; // list of primes r1..sqrt(N)
  static long[] cnts; // hold twin primes counts for seg bytes
  static long[] lastwins; // holds largest twin prime <= num in each thread
  static BigInteger modpg; // PG's modulus value
  static long res_0; // PG's first residue value
  static LinkedList<Long> restwins; // PG's list of twinpair residues
  static long[] resinvrs; // PG's list of residues inverses
  static int Bn; // segment size factor for PG and input number
  static final int S = 6; // 0|3 for 1|8 resgroups/byte for 'small|large' ranges
  static final long BMASK = (1L << S) - 1L;

  /**
   * Create Prime Generator parameters for given Pn. Using BigInteger permit Optimized gcd and
   * ModInverse At build time, both version (Long and BigInteger are created)
   * 
   * @param prime int
   */
  private static void genPGparameters(int prime) {
    System.out.println("Using Prime Generator parameters for given Pn " + prime);
    final long[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23};
    modpg = ONE;
    res_0 = 0L;
    for (long prm : primes) {
      res_0 = prm;
      if (prm > prime)
        break;
      modpg = modpg.multiply(BigInteger.valueOf(res_0));
    }

    LinkedList<Long> restwins = new LinkedList<>(); // save upper twin pair residues here
    long[] inverses = new long[modpg.intValue() + 2]; // save PG's residues inverses here
    BigInteger pc = THREE.add(TWO);
    int inc = 2;
    BigInteger res = ZERO;
    while (pc.compareTo(modpg.divide(TWO)) < 0) { // find a residue, then modular complement
      if (modpg.gcd(pc).equals(ONE)) { // if pc a residue
        final BigInteger pc_mc = modpg.subtract(pc); // create its modular complement
        Integer inv_r = pc.modInverse(modpg).intValue(); // compute residues's inverse
        inverses[pc.intValue()] = inv_r; // save its inverse
        inverses[inv_r] = pc.intValue(); // save its inverse inverse
        inv_r = pc_mc.modInverse(modpg).intValue(); // compute residues's complement inverse
        inverses[pc_mc.intValue()] = inv_r; // save its inverse
        inverses[inv_r] = pc_mc.intValue(); // save its inverse inverse
        if (res.add(TWO).equals(pc)) {
          restwins.add(pc.longValue());
          restwins.add(pc_mc.add(TWO).longValue());
        } // save hi_tp residues
        res = pc;
      }
      pc = BigInteger.valueOf(pc.longValue() + inc);
      inc ^= 0b110;
    }
    restwins.sort(Comparator.naturalOrder());
    restwins.add(modpg.add(ONE).longValue()); // last residue is last hi_tp
    inverses[modpg.add(ONE).intValue()] = ONE.intValue();
    inverses[modpg.subtract(ONE).intValue()] = modpg.subtract(ONE).intValue(); // last 2 residues
                                                                               // are self inverses

    new PGparam(modpg, res_0, restwins, inverses);
  }

  /**
   * Here we store all we need This doesnt penalize nothing Migration en cours...
   */
  static class PGparam {
    public static Long Lmodpg;
    public static Long Lkmin;
    public static Long Lkmax;
    public static Long Lstart;
    public static Long Lend;
    public static int primesSize;
    static int segByteSize = 0;
    private static Long Lrange;

    public PGparam(BigInteger modpg, Long res_0, LinkedList<Long> restwins, long[] inverses) {
      SSOZJ.modpg = modpg;
      SSOZJ.res_0 = res_0;
      SSOZJ.restwins = restwins;
      SSOZJ.resinvrs = inverses;
      if (modpg.bitLength() <= 63) {
        // Android doesn't know method longValueExact
        Lmodpg = modpg.longValue();
      } else {
        throw new ArithmeticException("BigInteger out of long range");
      }
      Kmin = start_num.subtract(TWO).divide(modpg).add(ONE); // number of resgroups to start_num
      Kmax = end_num.subtract(TWO).divide(modpg).add(ONE); // number of resgroups to end_num

      Lkmin = Kmin.longValue();
      Lkmax = Kmax.longValue();

      Lrange = Lkmax - Lkmin + 1; // number of range resgroups, at least 1
      int n = Lrange < 37_500_000_000_000L ? 4 : Lrange < 975_000_000_000_000L ? 6 : 8;
      long B = (Bn * 1024L * n); // set seg size to optimize for selected PG
      KB = Math.min(Lrange, B); // segments resgroups size
      // Doing that one time is enough
      segByteSize = (int) ((KB - 1 >>> S) + 1);

      System.out.println(
          "segment size = " + KB + " resgroups; seg array is [1 x " + segByteSize + "] 64-bits");
      long maxpairs = Lrange * SSOZJ.restwins.size(); // maximum number of twinprime pcs
      System.out.println("twinprime candidates = " + maxpairs + " ; resgroups = " + Lrange);
    }
  }

  /**
   * Select at runtime best PG and segment size factor to use for input value. These are good
   * estimates derived from PG data profiling. Can be improved.
   *
   * @param start_range
   * @param end_range
   */
  static void setSieveParameters(BigInteger start_range, BigInteger end_range) {
    final BigInteger range = end_range.subtract(start_range);
    int pg = 3;
    if (end_range.compareTo(BigInteger.valueOf(49L)) < 0) {
      Bn = 1;
      pg = 3;
    } else if (range.compareTo(BigInteger.valueOf(24_000_000L)) < 0) {
      Bn = 16;
      pg = 5;
    } else if (range.compareTo(BigInteger.valueOf(1_100_000_000L)) < 0) {
      Bn = 32;
      pg = 7;
    } else if (range.compareTo(BigInteger.valueOf(35_500_000_000L)) < 0) {
      Bn = 64;
      pg = 11;
    } else if (range.compareTo(BigInteger.valueOf(15_000_000_000_000L)) < 0) {
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
    } else {
      Bn = 384;
      pg = 17;
    }
    // Set value for 'small' or 'large' ranges to opt sieving
    // S = range.compareTo(BigInteger.valueOf(100_000_000_000L))<0 ? 0 : 3;
    genPGparameters(pg);
  }

  /**
   * Compute the primes r1..sqrt(input_num) and store in global 'primes' array. Any algorithm
   * (fast|small) is usable. Here the SoZ for P5 is used.
   * 
   * @param val
   */
  static void sozpg(BigInteger val) {
    final int md = 30; // P5's modulus value
    final int rscnt = 8; // P5's residue count

    final int[] res = {7, 11, 13, 17, 19, 23, 29, 31}; // P5's residues list
    final int[] posn =
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 3, 0, 4, 0, 0, 0, 5, 0, 0, 0, 0, 0, 6, 0, 7};

    final long sqrtN = Bsqrt(val).longValue(); // integer sqrt of sqrt(input value)
    final int kmax =
        val.subtract(BigInteger.valueOf(7L)).divide(BigInteger.valueOf(md)).add(ONE).intValue(); // number
                                                                                                 // of
                                                                                                 // resgroups
                                                                                                 // to
                                                                                                 // input
                                                                                                 // value

    short[] prms = new short[kmax]; // byte array of prime candidates init '0'
    int modk = 0;
    int r = -1;
    int k = 0; // init residue parameters
    // mark the multiples of the primes r1..sqrtN in 'prms'
    while (true) {
      r++;
      if (r == rscnt) {
        r = 0;
        modk += md;
        k++;
      }
      if ((prms[k] & (1 << r) & 0XFF) != 0)
        continue; // skip pc if not prime
      final int prm_r = res[r]; // if prime save its residue value
      final int prime = modk + prm_r; // numerate the prime value
      if (prime > sqrtN)
        break; // we're finished when it's > sqrtN
      for (int ri : res) { // mark prime's multiples in prms
        final int prod = prm_r * ri - 2; // compute cross-product for prm_r|ri pair
        final int bit_r = (1 << posn[(int) mod(prod, md)]) & 0XFF; // bit mask for prod's residue
        int kpm = k * (prime + ri) + prod / md; // 1st resgroup for prime mult
        while (kpm < kmax) {
          prms[kpm] |= bit_r;
          kpm += prime;
        }
      }
    }
    // prms now contains the nonprime positions for the prime candidates r1..N
    // extract primes into global var 'primes'
    primes = new ArrayDeque<Long>(); // create empty dynamic array for primes
    IntStream.range(0, kmax).forEach(km -> { // for each resgroup
      int[] ri = {0};
      for (int res_r : res) { // extract the primes in numerical order
        if ((prms[km] & (1 << ri[0]++) & 0XFF) == 0) {
          primes.add((long) md * km + res_r);
        }
      }
    });
    while (primes.getFirst() < res_0)
      primes.pollFirst();
    while (primes.getLast() > val.longValue())
      primes.pollLast();

  }

  /**
   * Print twinprimes for given twinpair for given segment slice. Primes will not be displayed in
   * sorted order, collect|sort later for that.
   * 
   * @param Kn
   * @param Ki
   * @param indx
   * @param seg
   */
  private static void printprms(Long Kn, Long Ki, int indx, long[] seg) {
    long modk = Ki * PGparam.Lmodpg; // base value of 1st resgroup in slice
    final long r_hi = restwins.get(indx); // for upper twinpair residue value
    for (int k = (int) ((Kn - 1) >>> S); k > -1; k--) // for each byte of resgroups in slice
      for (int r = 7; r > -1; r-- /* in 0..7 */) { // extract the primes for each resgroup
        if ((seg[k] & (1 << r)/* &0XFF */) == 0 && modk + r_hi <= PGparam.Lend)
          System.out.println((modk + r_hi - 1)); // print twinprime mid val on a line
        modk += PGparam.Lmodpg; // set base value for next resgroup
      }
  }

  /**
   * Initialize 'nextp' array for twinpair upper residue rhi in 'restwins'. Compute 1st prime
   * multiple resgroups for each prime r0..sqrt(N) and store consecutively as lo_tp|hi_tp pairs for
   * their restracks.
   * 
   * @param hi_r twin pair residues
   * @return nextp 1st mults array for twin pair
   */
  private static long[] nextp_init(long hi_r, final long kmin) {
    long[] nextp = new long[PGparam.primesSize << 1]; // 1st mults array for twin pair

    long r_hi = hi_r;
    long r_lo = r_hi - 2; // upper|lower twin pair residues

    final int[] j = {0};
    // for each prime r1..sqrt(N)
    for (Long prime : primes) {
      long k = (prime - 2) / PGparam.Lmodpg; // find the resgroup it's in
      int r = (int) (mod(prime - 2, PGparam.Lmodpg) + 2); // and its residue value
      long r_inv = resinvrs[r]; // and its residue inverse
      long ro = mod(r_lo * r_inv - 2, PGparam.Lmodpg) + 2; // compute the ri for r for lo_tp
      long ko = (k * (prime + ro) + (r * ro - 2) / PGparam.Lmodpg);
      if (ko < kmin) { // if 1st mult index < start_num's
        ko = mod(kmin - ko, prime); // how many indices short is it
        if (ko > 0)
          ko = prime - ko; // adjust index value into range
      } else
        ko -= kmin; // else here, adjust index if it was >
      nextp[j[0] << 1] = ko; // lo_tp index val >= start of range
      long ri = mod(r_hi * r_inv - 2, PGparam.Lmodpg) + 2; // compute the ri for r for hi_tp
      long ki = k * (prime + ri) + (r * ri - 2) / PGparam.Lmodpg;
      if (ki < kmin) { // if 1st mult index < start_num's
        ki = mod(kmin - ki, prime); // how many indices short is it
        if (ki > 0)
          ki = prime - ki; // adjust index value into range
      } else
        ki -= kmin; // else here, adjust index if it was >
      nextp[j[0] << 1 | 1] = ki; // hi_tp index val >= start of range
      j[0]++;
    }
    return nextp;
  }

  /**
   * Perform in a thread, the ssoz for a given twinpair, for Kmax resgroups. First create|init
   * 'nextp' array of 1st prime mults for given twin pair, (stored consequtively in 'nextp') and
   * init seg byte array for KB resgroups. For sieve, mark resgroup bits to '1' if either twinpair
   * restrack is nonprime, for primes mults resgroups, and update 'nextp' restrack slices
   * acccordingly.
   * <p>
   * Find last twin prime|sum for range, store in their arrays for this twinpair. Can optionally
   * build to print mid twinprime values generated by twinpair. Uses optimum segment sieve structure
   * for 'small' and 'large' range values.
   *
   * @param indx thread index
   * @param r_hi long
   */
  private static void twins_sieve(int indx, long r_hi) {

    long kmin = PGparam.Lkmin - 1;
    long kmax = PGparam.Lkmax;
    long sum = 0;
    long Kn = KB; // init twins cnt|1st resgroup for slice
    long hi_tp = 0; // max tp|resgroup, Kmax for slice

    long[] seg = new long[PGparam.segByteSize]; // seg byte array for Kb resgroups
    if ((kmax - 1) * PGparam.Lmodpg + (r_hi) > PGparam.Lend)
      kmax--; // and hi tps in range
    if (kmin * PGparam.Lmodpg + (r_hi - 2) < PGparam.Lstart)
      kmin++;// ensure lo tps in range
    long[] nextp = nextp_init(r_hi, kmin); // 1st prime mults for twin pair restracks

    final int[] j = {0};
    // for Kn resgroup size slices upto Kmax
    while (kmin < kmax) {
      if (KB > kmax - kmin)
        Kn = kmax - kmin; // set last slice resgroup size
      for (Long prime : primes) {
        // for lower twin pair residue track
        long k = nextp[(j[0] << 1)]; // starting from this resgroup in seg
        while (k < Kn) { // mark primenth resgroup bits prime mults
          seg[(int) (k >>> S)] |= (1L << (k & BMASK));
          k += prime; // set next prime multiple resgroup
        }
        nextp[(j[0] << 1)] = (k - Kn); // save 1st resgroup in next eligible seg
        // for upper twin pair residue track
        k = nextp[j[0] << 1 | 1]; // starting from this resgroup in seg
        while (k < Kn) { // mark primenth resgroup bits prime mults
          seg[(int) (k >>> S)] |= (1L << (k & BMASK));
          k += prime; // set next prime multiple resgroup
        }
        nextp[j[0] << 1 | 1] = (k - Kn); // save 1st resgroup in next eligible seg
        j[0]++;
      }
      int upk = (int) (Kn - 1);
      seg[upk >>> S] |= ((-(2L << (upk & BMASK))));
      int cnt = 0; // init seg twin primes count then find seg sum
      for (int m = (upk >>> S); m > -1; m--)
        cnt += (1 << S) - Long.bitCount(seg[m]);

      if (cnt > 0) { // if segment has twin primes
        sum += cnt; // add the segment count to total count
        // from end of seg, count backwards to largest tp
        while ((seg[(upk >>> S)] & (1L << (upk & BMASK))) != 0)
          upk--;
        hi_tp = kmin + upk; // numerate its full resgroup value
      }
      // printprms(Kn, kmin, indx, seg); // optional: display twin primes in seg
      kmin += KB; // set 1st resgroup val of next seg slice
      // if (kmin < kmax) for (int b=upk>>>S; b>-1;b--) seg[b] = 0;
      if (kmin < kmax)
        Arrays.fill(seg, 0);// set seg to all primes
      // when sieve done for full range
      // numerate largest twinprime in segs
      j[0] = 0;
    }
    // numerate largest twin prime in segs
    hi_tp = r_hi > PGparam.Lend ? 0 : hi_tp * PGparam.Lmodpg + r_hi;

    lastwins[indx] = (sum == 0 ? 1 : hi_tp);// store final seg tp value
    cnts[indx] = sum; // sum for twin pair
  }

  /**
   * Main routine to setup, time, and display results for twin primes sieve.
   */
  static void twinprimes_ssoz() {
    System.out.println(" Max threads = " + (countProcessors()));
    long ts = epochTime(); // start timing sieve setup execution

    setSieveParameters(start_num, end_num); // select PG and seg factor Bn for input range
    final int pairscnt = restwins.size(); // number of twin pairs for selected PG
    cnts = new long[pairscnt]; // array to hold count of tps for each thread
    lastwins = new long[pairscnt]; // array to hold largest tp for each thread

    if (PGparam.Lend < 49L)
      primes.add((5L)); // generate sieving primes
    else
      sozpg(Bsqrt(end_num)); // <= sqrt(end_num)

    PGparam.primesSize = primes.size();
    System.out.println(
        "each " + pairscnt + " threads has nextp[" + 2 + " x " + PGparam.primesSize + "] array");

    long twinscnt = 0; // number of twin primes in range
    final long lo_range = restwins.getFirst() - 3; // lo_range = lo_tp - 1

    for (int tp : new int[] {3, 5, 11, 17}) { // excluded low tp values for PGs used
      if (end_num.equals(THREE))
        break; // if 3 end of range, no twin primes
      if (tp >= PGparam.Lstart && tp <= lo_range)
        twinscnt++;
    }
    long te = epochTime() - ts; // sieve setup time

    System.out.println("setup time = " + te / 1e3 + " secs");
    System.out.println("perform twinprimes ssoz sieve with s=" + S);

    ExecutorService stealingPool = Executors.newWorkStealingPool();

    List<Runnable> sieveTask = new ArrayList<>();
    // For printing progress
    final Callback<String> callback = System.out::print;

    AtomicInteger indx = new AtomicInteger();
    for (long r_hi : restwins) { // for each twin pair row index
      sieveTask.add(() -> {
        callback.on("\r" + indx.get() + " of " + pairscnt + " threads done");
        twins_sieve(indx.getAndIncrement(), r_hi); // sieve selected twin pair restracks
      });
    }
    final long t1 = epochTime(); // start timing ssoz sieve execution
    // Implement parallel things
    try {
      stealingPool.submit(() -> sieveTask.parallelStream().forEach(Runnable::run)).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } finally {
      // when all the threads finish
      stealingPool.shutdown();
      System.out.println("\r" + indx + " of " + pairscnt + " threads done");
    }
    // OR Simple parallel without specific pool
    // sieveTask.parallelStream().forEach(Runnable::run);

    long last_twin = 0L; // find largest twin prime in range
    twinscnt += Arrays.stream(cnts).sum();
    last_twin = Arrays.stream(lastwins).max().orElse(0L);

    if (PGparam.Lend == 5L && twinscnt == 1)
      last_twin = 5L;
    long Kn = mod(PGparam.Lrange, KB); // set number of resgroups in last slice
    if (Kn == 0)
      Kn = KB; // if multiple of seg size set to seg size

    cnts = null;
    lastwins = null; // Free memory
    long t2 = epochTime() - t1; // sieve execution time

    System.out.println("sieve time = " + t2 / 1e3 + " secs");
    System.out.println(
        "last segment = " + Kn + " resgroups; segment slices = " + ((PGparam.Lrange - 1) / KB + 1));
    System.out.println("total twins = " + twinscnt + "; last twin = " + (last_twin - 1) + "+/-1");

    System.out.println("total time = " + (t2 + te) / 1e3 + " secs\n");
  }

  public static void main(String[] args) {
    // if (args==null)
    Scanner userInput = new Scanner(System.in);
    userInput.useDelimiter("[,\\s+]"); // see
                                       // https://stackoverflow.com/questions/11463327/is-this-a-memory-leak-or-a-false-positive
    System.out.println("Please enter an range of integer (comma or space separated): ");
    // Only BigDecimal understand scientific notation
    BigInteger stop = userInput.nextBigDecimal().toBigIntegerExact();
    BigInteger start =
        userInput.hasNextLine() ? userInput.nextBigDecimal().toBigIntegerExact() : THREE;

    userInput.close();

    if (stop.compareTo(start) < 0) {
      BigInteger tmp = start;
      start = stop;
      stop = tmp;
    }

    start = start.max(THREE);
    BigInteger end = stop.max(THREE);

    start_num = start.or(ONE); // if start_num even add 1
    end_num = end.subtract(ONE).or(ONE); // if end_num even subtract 1

    PGparam.Lstart = start_num.longValue();
    PGparam.Lend = end_num.longValue();

    twinprimes_ssoz();
  }

  private static int countProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

  /** Granularity tens of milliseconds. */
  private static long epochTime() {
    return System.currentTimeMillis();
  }

  public static BigInteger Bsqrt(BigInteger x) {
    BigInteger div = BigInteger.ZERO.setBit(x.bitLength() >> 1);
    BigInteger div2 = div;
    // Loop until we hit the same value twice in a row, or wind up alternating.
    for (;;) {
      BigInteger y = div.add(x.divide(div)).shiftRight(1);
      if (y.equals(div) || y.equals(div2))
        return div.min(div2);
      div2 = div;
      div = y;
    }
  }

  /**
   * According to the Java Language Spec, Java's % operator is a remainder operator, not a modulo
   * operator.
   * 
   * @param x a long
   * @param y a long
   * @return a long modulo
   */
  private static long mod(long x, long y) {
    // Original
    // long mod = x % y;
    // if the signs are different and modulo not zero, adjust result
    // if ((mod ^ y) < 0 && mod != 0) {
    // mod += y;
    // }
    // return mod;
    // equivalent of nimlang definition
    // return x - (Math.floorDiv(x, y) * y);
    // same as
    return Math.floorMod(x, y);
  }

  @FunctionalInterface
  public interface Callback<T> {
    void on(T event);
  }
}

