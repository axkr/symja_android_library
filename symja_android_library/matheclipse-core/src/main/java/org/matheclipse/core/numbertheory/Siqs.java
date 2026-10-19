package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.eval.exception.TimeoutException;

/**
 * The self-initializing quadratic sieve, which splits a composite number without small factors.
 *
 * <p>
 * A relation is a congruence <code>Y^2 = A*Q(x) (mod k*n)</code> with the polynomials
 * <code>Q(x) = A*x^2 + 2*B*x + C</code>, <code>B^2 - A*C = k*n</code>, whose value factors over the
 * factor base, the primes <code>p</code> for which <code>k*n</code> is a quadratic residue. The
 * multiplier <code>k</code> is chosen with the Knuth-Schroeppel function. The leading coefficient
 * <code>A</code> is a product of <code>s</code> primes of the factor base, which gives
 * <code>2^(s-1)</code> values of <code>B</code>; consecutive ones differ by one term in Gray code
 * order, so the roots of the next polynomial modulo every prime follow by one modular addition.
 * The interval <code>[-M, M)</code> is sieved with byte logarithms, the small primes block by
 * block, the larger ones without branches over the whole interval, and these are sieved once more
 * at the candidates to find their divisors. A value which is smooth up to one prime below a bound,
 * or for large numbers up to two of them, is kept as a partial relation. The partial relations are
 * the edges of a graph on the large primes, and every cycle of it combines into a full relation. A
 * set of relations whose product is a square, found by {@link GF2Solver}, gives a congruence of
 * squares <code>X^2 = Y^2 (mod n)</code> and with <code>gcd(X-Y, n)</code> a factor, which is
 * verified by division. Numbers of {@link #PARALLEL_DIGITS} and more digits are sieved on several
 * threads, each with its own polynomials.
 *
 * <p>
 * See S. Contini, "Factoring integers with the self-initializing quadratic sieve", 1997; R. D.
 * Silverman, "The multiple polynomial quadratic sieve", Math. Comp. 48 (1987); R. Crandall, C.
 * Pomerance, "Prime numbers: a computational perspective", section 6.1; A. K. Lenstra, M. S.
 * Manasse, "Factoring with two large primes", EUROCRYPT '90.
 */
final class Siqs {

  /** The smallest and the largest number of decimal digits the sieve is meant for. */
  static final int MIN_DIGITS = 18;

  static final int MAX_DIGITS = 110;

  /** Numbers with at least this many digits are sieved on several threads. */
  private static final int PARALLEL_DIGITS = 50;

  /** The length of a sieve block in bytes. */
  private static final int BLOCK = 1 << 15;

  /**
   * Decimal digits, the size of the factor base, the number of sieve blocks of the interval and the
   * bound of the large prime as a multiple of the largest prime of the factor base. The values are
   * interpolated linearly.
   */
  private static final int[][] PARAMETERS = {//
      {18, 50, 1, 20}, {25, 100, 1, 25}, {30, 160, 1, 30}, {35, 300, 1, 35}, {40, 540, 1, 40},
      {45, 1000, 1, 50}, {50, 1800, 1, 60}, {55, 2800, 2, 70}, {60, 4400, 2, 80},
      {65, 6800, 3, 90}, {70, 10000, 4, 100}, {75, 13000, 6, 110}, {80, 16500, 6, 120},
      {85, 24000, 6, 130}, {90, 34000, 6, 140}, {95, 48000, 8, 150}, {100, 64000, 8, 160},
      {110, 100000, 10, 180}};

  /** The odd square free multipliers which are tried. */
  private static final int[] MULTIPLIERS = {1, 3, 5, 7, 11, 13, 15, 17, 19, 21, 23, 29, 31, 33, 35,
      37, 39, 41, 43, 47, 51, 53, 55, 57, 59, 61, 65, 67, 69, 71, 73};

  /** The primes below this bound aren't sieved; their logarithms are part of the threshold. */
  private static final int SMALL_PRIME_BOUND = 100;

  /**
   * The threshold of the sieve is the logarithm of the largest value minus this multiple of the
   * logarithm of the largest prime of the factor base.
   */
  private static final double THRESHOLD_FACTOR = 2.5;

  /** Numbers with at least this many digits use the double large prime variation. */
  private static final int DOUBLE_LARGE_DIGITS = 75;

  /** The product of two large primes is below the large prime bound to this power. */
  private static final double DOUBLE_LARGE_EXPONENT = 1.8;

  /** {@link #THRESHOLD_FACTOR} with the double large prime variation. */
  private static final double DOUBLE_LARGE_THRESHOLD_FACTOR = 3.3;

  /** The number of relations collected in excess of the size of the factor base. */
  private static final int EXCESS_RELATIONS = 64;

  /** The number of guard bytes behind the sieve interval, a power of 2. */
  private static final int GUARD = 256;

  /** A root which is never reached, for the primes which divide <code>A</code>. */
  private static final int NO_ROOT = Integer.MAX_VALUE >>> 1;

  /**
   * A relation <code>y^2 = (-1)^e0 * prod p_j^ej * root^2 (mod n)</code>, or a partial one
   * <code>y^2 = (-1)^e0 * prod p_j^ej * large1 * large2 (mod n)</code> with one or two large
   * primes.
   */
  static final class Relation {
    final BigInteger y;

    /** the column indices with multiplicity: <code>0</code> for -1, <code>j+1</code> for p_j */
    final int[] factors;

    /** the product of the large primes which occur squared, or 1 */
    final BigInteger root;

    /** the large primes of a partial relation, 1 if there is none */
    final long large1;

    final long large2;

    Relation(BigInteger y, int[] factors, BigInteger root) {
      this.y = y;
      this.factors = factors;
      this.root = root;
      this.large1 = 1;
      this.large2 = 1;
    }

    Relation(BigInteger y, int[] factors, long large1, long large2) {
      this.y = y;
      this.factors = factors;
      this.root = BigInteger.ONE;
      this.large1 = large1;
      this.large2 = large2;
    }

    boolean isPartial() {
      return large1 != 1 || large2 != 1;
    }
  }

  final BigInteger n;
  final BigInteger kn;
  final int multiplier;

  /** factor base: <code>prime[0] = 2</code>, then the odd primes */
  final int[] prime;
  /** square root of <code>k*n</code> modulo <code>prime[j]</code> */
  final int[] sqrtKN;
  final byte[] logp;
  /** inverse of the prime as <code>ceil(2^40/p)</code> for the remainder of an index */
  final long[] reciprocal;
  final int size;
  /** the first index of a prime which is sieved */
  final int sieveStart;
  /** the first index of a prime which is sieved without blocks */
  final int mediumEnd;
  /**
   * The primes <code>prime[classEnd[k+1]..classEnd[k]-1]</code> hit the interval <code>k</code> or
   * <code>k+1</code> times per root, for <code>k = 0..hitClasses</code>.
   */
  final int[] classEnd;
  final int hitClasses;
  /** the first index of a prime which is not smaller than the sieve interval */
  final int largeEnd;
  /** the length of the sieve interval <code>2*M</code> */
  final int interval;
  final int halfInterval;
  final byte threshold;
  final long largePrimeBound;
  final BigInteger targetA;
  final Set<BigInteger> usedA = ConcurrentHashMap.newKeySet();
  volatile boolean stop;

  /** the bound of the product of two large primes, 0 without the double large prime variation */
  final long doubleLargeBound;

  final List<Relation> relations = new ArrayList<>();

  /**
   * The partial relations are the edges of a graph whose vertices are the large primes and 1.
   * Every edge which closes a cycle gives one more combination of partial relations without large
   * primes of odd exponent.
   */
  final List<Relation> partials = new ArrayList<>();
  final Map<Long, Integer> vertexOf = new HashMap<>();
  long[] vertexValue = new long[1024];
  int[] parent = new int[1024];
  int vertices;
  int cycles;

  /**
   * Split the composite number <code>n</code>.
   *
   * @param n an odd composite number which isn't a perfect power, with {@link #MIN_DIGITS} to
   *        {@link #MAX_DIGITS} digits
   * @return a divisor <code>d</code> with <code>1 &lt; d &lt; n</code>, or <code>null</code> if
   *         none was found
   * @throws TimeoutException if the thread is interrupted
   */
  static BigInteger factor(BigInteger n) {
    if (!n.testBit(0)) {
      return BigInteger.TWO;
    }
    BigInteger root = n.sqrt();
    if (root.multiply(root).equals(n)) {
      return root;
    }
    int digits = n.toString().length();
    if (digits > MAX_DIGITS || n.isProbablePrime(32)) {
      return null;
    }
    int[] p = parameters(Math.max(digits, PARAMETERS[0][0]));
    int k = multiplier(n);
    BigInteger kn = n.multiply(BigInteger.valueOf(k));
    Siqs siqs;
    try {
      siqs = new Siqs(n, k, kn, p[0], p[1], p[2], digits >= DOUBLE_LARGE_DIGITS);
    } catch (FactorFound found) {
      return found.factor;
    }
    int threads = threads(digits);
    int granted = threads > 1 ? IntegerSplitter.acquireWorkers(threads) : 0;
    if (granted < 2) {
      // a single worker is the calling thread: its permit is free for a concurrent factorization
      IntegerSplitter.releaseWorkers(granted);
      return siqs.run(1);
    }
    try {
      return siqs.run(granted);
    } finally {
      IntegerSplitter.releaseWorkers(granted);
    }
  }

  /** Thrown by the constructor when a prime of the factor base divides <code>n</code>. */
  private static final class FactorFound extends RuntimeException {
    private static final long serialVersionUID = 1L;
    final BigInteger factor;

    FactorFound(BigInteger factor) {
      super(null, null, false, false);
      this.factor = factor;
    }
  }

  /** The number of threads to sieve on for a number of <code>digits</code> digits. */
  static int threads(int digits) {
    return digits < PARALLEL_DIGITS ? 1 : IntegerSplitter.workerThreads();
  }

  /** The interpolated parameters for a number of <code>digits</code> digits. */
  static int[] parameters(int digits) {
    int i = 0;
    while (i < PARAMETERS.length - 2 && PARAMETERS[i + 1][0] <= digits) {
      i++;
    }
    int[] a = PARAMETERS[i];
    int[] b = PARAMETERS[i + 1];
    double t = Math.min(1.5, (double) (digits - a[0]) / (b[0] - a[0]));
    int[] result = new int[3];
    for (int j = 0; j < 3; j++) {
      result[j] = (int) Math.round(a[j + 1] + t * (b[j + 1] - a[j + 1]));
    }
    return result;
  }

  /** The odd primes below this bound take part in the Knuth-Schroeppel function. */
  private static final int MULTIPLIER_PRIMES = 1000;

  /**
   * The multiplier <code>k</code> which maximizes the Knuth-Schroeppel function, the expected
   * contribution of the small primes to the logarithm of a value minus <code>log(k)/2</code>.
   */
  static int multiplier(BigInteger n) {
    int[] primes = IntegerSplitter.primesUpTo(MULTIPLIER_PRIMES);
    int count = 1;
    while (count < primes.length && primes[count] < MULTIPLIER_PRIMES) {
      count++;
    }
    int[] nWords = words(n);
    // the Legendre symbols (n/q); (k*n/q) = (k/q)*(n/q)
    int[] symbol = new int[count];
    for (int i = 1; i < count; i++) {
      symbol[i] = jacobi(mod(nWords, nWords.length, primes[i]), primes[i]);
    }
    int n8 = n.intValue() & 7;
    int best = 1;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (int k : MULTIPLIERS) {
      double value = -0.5 * Math.log(k);
      int kn8 = (k * n8) & 7;
      if (kn8 == 1) {
        value += 2 * Math.log(2);
      } else if (kn8 == 5) {
        value += Math.log(2);
      } else {
        value += 0.5 * Math.log(2);
      }
      for (int i = 1; i < count; i++) {
        int q = primes[i];
        if (k % q == 0) {
          value += Math.log(q) / q;
        } else if (symbol[i] * jacobi(k % q, q) == 1) {
          value += 2 * Math.log(q) / (q - 1);
        }
      }
      if (value > bestValue) {
        bestValue = value;
        best = k;
      }
    }
    return best;
  }

  /** The Jacobi symbol <code>(a/m)</code> for <code>0 &lt;= a</code> and an odd <code>m</code>. */
  static int jacobi(long a, long m) {
    a %= m;
    int result = 1;
    while (a != 0) {
      int twos = Long.numberOfTrailingZeros(a);
      a >>>= twos;
      if ((twos & 1) != 0 && ((m & 7) == 3 || (m & 7) == 5)) {
        result = -result;
      }
      if ((a & 3) == 3 && (m & 3) == 3) {
        result = -result;
      }
      long t = m % a;
      m = a;
      a = t;
    }
    return m == 1 ? result : 0;
  }

  Siqs(BigInteger n, int multiplier, BigInteger kn, int fbSize, int blocks, int largeMultiplier,
      boolean doubleLarge) {
    this.n = n;
    this.kn = kn;
    this.multiplier = multiplier;
    int[] words = words(kn);
    int[] nWords = words(n);
    // the factor base
    int[] fb = new int[fbSize];
    int[] roots = new int[fbSize];
    fb[0] = 2;
    roots[0] = kn.testBit(0) ? 1 : 0;
    int count = 1;
    int limit = Math.max(1000, (int) (fbSize * Math.log(fbSize) * 3) + 1000);
    while (count < fbSize) {
      int[] primes = IntegerSplitter.primesUpTo(limit);
      count = 1;
      for (int i = 1; i < primes.length && count < fbSize; i++) {
        int q = primes[i];
        if (mod(nWords, nWords.length, q) == 0) {
          throw new FactorFound(BigInteger.valueOf(q));
        }
        int a = mod(words, words.length, q);
        if (a == 0) {
          // q divides the multiplier
          fb[count] = q;
          roots[count++] = 0;
        } else if (jacobi(a, q) == 1) {
          fb[count] = q;
          roots[count++] = sqrtMod(a, q);
        }
      }
      limit *= 2;
    }
    prime = fb;
    sqrtKN = roots;
    size = fbSize;
    interval = blocks * BLOCK;
    halfInterval = interval / 2;
    int j = 1;
    final int smallBound = Math.min(SMALL_PRIME_BOUND, prime[size / 10]);
    while (j < size && prime[j] < smallBound) {
      j++;
    }
    sieveStart = j;
    // the primes from interval/(k+1) to interval/k hit the interval k or k+1 times per root
    int mediumBound = Math.min(BLOCK, interval / 16);
    hitClasses = interval / mediumBound - 1;
    classEnd = new int[hitClasses + 2];
    classEnd[0] = size;
    for (int k = hitClasses + 1; k >= 1; k--) {
      int bound = interval / k;
      while (j < size && prime[j] < bound) {
        j++;
      }
      classEnd[k] = j;
    }
    mediumEnd = classEnd[hitClasses + 1];
    largeEnd = classEnd[1];
    int largest = prime[size - 1];
    largePrimeBound = Math.min((long) largest * largeMultiplier, (long) largest * largest - 1);
    doubleLargeBound = doubleLarge
        ? (long) Math.min(Math.pow(largePrimeBound, DOUBLE_LARGE_EXPONENT), (double) (1L << 62))
        : 0L;
    // A close to sqrt(2*k*n)/M, so that |Q(x)| <= M*sqrt(k*n/2) on the interval
    targetA = kn.shiftLeft(1).sqrt().divide(BigInteger.valueOf(halfInterval));
    double log2MaxValue =
        Math.log(halfInterval) / Math.log(2) + 0.5 * (kn.bitLength() - 1) - 0.5;
    double thresholdBits = log2MaxValue
        - (doubleLarge ? DOUBLE_LARGE_THRESHOLD_FACTOR : THRESHOLD_FACTOR) * Math.log(largest)
            / Math.log(2);
    double scale = Math.min(1.0, 127.0 / thresholdBits);
    threshold = (byte) Math.max(1, Math.min(127, Math.round(thresholdBits * scale)));
    logp = new byte[size];
    reciprocal = new long[size];
    for (int i = 0; i < size; i++) {
      logp[i] = (byte) Math.round(Math.log(prime[i]) / Math.log(2) * scale);
      reciprocal[i] = (1L << 40) / prime[i] + 1;
      if (sqrtKN[i] == 0 && i > 0) {
        // a prime of the multiplier has a single root, which is checked by trial division
        logp[i] = 0;
      }
    }
  }

  /** Collect relations until there are enough, then find the dependencies. */
  private BigInteger run(int threads) {
    int wanted = size + EXCESS_RELATIONS;
    List<Sieve> sieves = new ArrayList<>();
    for (int i = 0; i < Math.max(1, threads); i++) {
      sieves.add(new Sieve(this, 0x5eedL + 7919L * i));
    }
    for (int attempt = 0; attempt < 4; attempt++) {
      boolean enough =
          threads > 1 ? collectParallel(sieves, wanted) : collect(sieves.get(0), wanted);
      if (!enough) {
        return null;
      }
      BigInteger d = squareRoots();
      if (d != null) {
        return d;
      }
      wanted += size / 16 + 16;
    }
    return null;
  }

  /**
   * Sieve on the calling thread until there are <code>wanted</code> relations.
   *
   * @return false if no new polynomial could be found
   */
  private boolean collect(Sieve sieve, int wanted) {
    while (relations.size() + cycles < wanted) {
      TimeLimits.check();
      List<Relation> found = sieve.nextA();
      if (found == null) {
        return false;
      }
      for (Relation r : found) {
        add(r);
      }
    }
    return true;
  }

  /**
   * Sieve on one thread per element of <code>sieves</code> until there are <code>wanted</code>
   * relations. The calling thread combines the relations; when it is interrupted it stops the
   * workers and waits for them before it throws the timeout.
   *
   * @return false if no new polynomial could be found
   */
  private boolean collectParallel(List<Sieve> sieves, int wanted) {
    final LinkedBlockingQueue<List<Relation>> queue = new LinkedBlockingQueue<>();
    final AtomicReference<Throwable> failure = new AtomicReference<>();
    final AtomicInteger running = new AtomicInteger(sieves.size());
    final AtomicBoolean exhausted = new AtomicBoolean();
    stop = false;
    Thread[] workers = new Thread[sieves.size()];
    for (int i = 0; i < workers.length; i++) {
      final Sieve sieve = sieves.get(i);
      workers[i] = Config.THREAD_FACTORY.newThread(() -> {
        try {
          while (!stop) {
            List<Relation> found = sieve.nextA();
            if (found == null) {
              exhausted.set(true);
              break;
            }
            queue.add(found);
          }
        } catch (TimeoutException e) {
          // stopped
        } catch (Throwable t) {
          failure.compareAndSet(null, t);
        } finally {
          running.decrementAndGet();
        }
      });
      workers[i].setDaemon(true);
      workers[i].start();
    }
    try {
      while (relations.size() + cycles < wanted) {
        List<Relation> found;
        try {
          found = queue.poll(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw TimeoutException.TIMED_OUT;
        }
        if (failure.get() != null) {
          Throwable t = failure.get();
          if (t instanceof Error) {
            throw (Error) t;
          }
          throw t instanceof RuntimeException ? (RuntimeException) t : new IllegalStateException(t);
        }
        TimeLimits.check();
        if (found != null) {
          for (Relation r : found) {
            add(r);
          }
        } else if (running.get() == 0) {
          return relations.size() + cycles >= wanted || !exhausted.get();
        }
      }
      return true;
    } finally {
      stop = true;
      IntegerSplitter.stopWorkers(workers);
    }
  }

  /** Add a full or a partial relation. */
  void add(Relation r) {
    if (!r.isPartial()) {
      relations.add(r);
      return;
    }
    partials.add(r);
    int u = find(vertex(r.large1));
    int v = find(vertex(r.large2));
    if (u == v) {
      cycles++;
    } else {
      parent[u] = v;
    }
  }

  /** The vertex of the large prime <code>value</code>; 1 is the vertex 0. */
  private int vertex(long value) {
    if (vertices == 0) {
      vertexOf.put(1L, 0);
      vertexValue[0] = 1;
      parent[0] = 0;
      vertices = 1;
    }
    Integer index = vertexOf.get(value);
    if (index != null) {
      return index;
    }
    if (vertices == parent.length) {
      parent = Arrays.copyOf(parent, 2 * vertices);
      vertexValue = Arrays.copyOf(vertexValue, 2 * vertices);
    }
    parent[vertices] = vertices;
    vertexValue[vertices] = value;
    vertexOf.put(value, vertices);
    return vertices++;
  }

  /** The root of the component of <code>v</code> in the union-find forest. */
  private int find(int v) {
    while (parent[v] != v) {
      parent[v] = parent[parent[v]];
      v = parent[v];
    }
    return v;
  }

  /**
   * One relation for every cycle of the graph of the partial relations: a spanning forest is grown
   * by breadth first search, and every edge which isn't part of it closes a cycle with the paths to
   * the common ancestor of its ends. Every large prime of the cycle occurs twice in the product.
   */
  private List<Relation> cycleRelations() {
    final int edges = partials.size();
    final int count = vertices;
    int[] start = new int[count + 1];
    int[][] ends = new int[edges][2];
    for (int e = 0; e < edges; e++) {
      ends[e][0] = vertexOf.get(partials.get(e).large1);
      ends[e][1] = vertexOf.get(partials.get(e).large2);
      start[ends[e][0] + 1]++;
      start[ends[e][1] + 1]++;
    }
    for (int v = 0; v < count; v++) {
      start[v + 1] += start[v];
    }
    int[] adjacent = new int[2 * edges];
    int[] fill = Arrays.copyOf(start, count);
    for (int e = 0; e < edges; e++) {
      adjacent[fill[ends[e][0]]++] = e;
      adjacent[fill[ends[e][1]]++] = e;
    }
    int[] parentEdge = new int[count];
    int[] depth = new int[count];
    boolean[] visited = new boolean[count];
    boolean[] treeEdge = new boolean[edges];
    int[] queue = new int[count];
    for (int root = 0; root < count; root++) {
      if (visited[root]) {
        continue;
      }
      visited[root] = true;
      parentEdge[root] = -1;
      int head = 0;
      int tail = 0;
      queue[tail++] = root;
      while (head < tail) {
        int x = queue[head++];
        for (int k = start[x]; k < start[x + 1]; k++) {
          int e = adjacent[k];
          int other = ends[e][0] == x ? ends[e][1] : ends[e][0];
          if (!visited[other]) {
            visited[other] = true;
            parentEdge[other] = e;
            depth[other] = depth[x] + 1;
            treeEdge[e] = true;
            queue[tail++] = other;
          }
        }
      }
    }
    List<Relation> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    for (int e = 0; e < edges; e++) {
      if (treeEdge[e]) {
        continue;
      }
      path.clear();
      path.add(e);
      int a = ends[e][0];
      int b = ends[e][1];
      BigInteger root = BigInteger.ONE;
      while (a != b) {
        if (depth[a] >= depth[b]) {
          root = root.multiply(BigInteger.valueOf(vertexValue[a]));
          int pe = parentEdge[a];
          path.add(pe);
          a = ends[pe][0] == a ? ends[pe][1] : ends[pe][0];
        } else {
          root = root.multiply(BigInteger.valueOf(vertexValue[b]));
          int pe = parentEdge[b];
          path.add(pe);
          b = ends[pe][0] == b ? ends[pe][1] : ends[pe][0];
        }
      }
      root = root.multiply(BigInteger.valueOf(vertexValue[a])).mod(n);
      BigInteger y = BigInteger.ONE;
      int length = 0;
      for (int edge : path) {
        Relation r = partials.get(edge);
        y = y.multiply(r.y).mod(n);
        length += r.factors.length;
      }
      if (path.size() == 2 && partials.get(path.get(0)).y.equals(partials.get(path.get(1)).y)) {
        // the same relation twice
        continue;
      }
      int[] factors = new int[length];
      int k = 0;
      for (int edge : path) {
        int[] f = partials.get(edge).factors;
        System.arraycopy(f, 0, factors, k, f.length);
        k += f.length;
      }
      result.add(new Relation(y, factors, root));
    }
    return result;
  }

  /** Find the dependencies of the relations and try the congruences of squares they give. */
  private BigInteger squareRoots() {
    final int columns = size + 1;
    List<Relation> all = new ArrayList<>(relations);
    all.addAll(cycleRelations());
    int[][] rows = new int[all.size()][];
    for (int i = 0; i < rows.length; i++) {
      rows[i] = oddColumns(all.get(i).factors);
    }
    List<int[]> dependencies = GF2Solver.dependencies(rows, columns, 64);
    for (int[] dependency : dependencies) {
      BigInteger d = congruence(all, dependency);
      if (d != null) {
        return d;
      }
    }
    return null;
  }

  /** The columns which occur an odd number of times in <code>factors</code>, each once. */
  static int[] oddColumns(int[] factors) {
    int[] sorted = factors.clone();
    Arrays.sort(sorted);
    int[] result = new int[sorted.length];
    int count = 0;
    for (int i = 0; i < sorted.length;) {
      int j = i;
      while (j < sorted.length && sorted[j] == sorted[i]) {
        j++;
      }
      if (((j - i) & 1) != 0) {
        result[count++] = sorted[i];
      }
      i = j;
    }
    return Arrays.copyOf(result, count);
  }

  /** <code>gcd(X - Y, n)</code> for the relations of one dependency, if it's a proper factor. */
  private BigInteger congruence(List<Relation> all, int[] dependency) {
    int[] exponents = new int[size + 1];
    BigInteger x = BigInteger.ONE;
    BigInteger y = BigInteger.ONE;
    for (int i : dependency) {
      Relation r = all.get(i);
      x = x.multiply(r.y).mod(n);
      if (!r.root.equals(BigInteger.ONE)) {
        y = y.multiply(r.root).mod(n);
      }
      for (int c : r.factors) {
        exponents[c]++;
      }
    }
    for (int c = 0; c <= size; c++) {
      if ((exponents[c] & 1) != 0) {
        // not a square; can't happen for a correct dependency
        return null;
      }
      if (c > 0 && exponents[c] > 0) {
        BigInteger power =
            BigInteger.valueOf(prime[c - 1]).modPow(BigInteger.valueOf(exponents[c] >>> 1), n);
        y = y.multiply(power).mod(n);
      }
    }
    BigInteger g = x.subtract(y).gcd(n);
    if (g.compareTo(BigInteger.ONE) > 0 && g.compareTo(n) < 0 && n.mod(g).signum() == 0) {
      return g;
    }
    return null;
  }

  /**
   * The state of the sieve for one polynomial at a time. All the arrays of the factor base primes
   * are indexed like {@link Siqs#prime}.
   */
  static final class Sieve {
    final Siqs s;
    final int[] prime;
    final byte[] logp;
    final int size;
    final byte[] sieve;
    final ByteBuffer longView;
    final int[] soln1;
    final int[] soln2;
    final int[] pos1;
    final int[] pos2;
    final int[] ainv;
    /** the change of the roots from {@link Siqs#mediumEnd} on which is still to be applied */
    int[] pendingDelta;
    boolean pendingPlus = true;
    final int[] noDelta;
    /** <code>2*B_l/A mod p</code> for every term <code>B_l</code> of <code>B</code> */
    int[][] bainv2;
    int[] aIndices = new int[0];
    BigInteger a;
    BigInteger b;
    BigInteger c;
    BigInteger[] bTerms;
    final int[] words = new int[64];
    final int[] factorBuffer = new int[1024];
    final Random random;
    int[] candidate = new int[64];
    int[] hitPosition = new int[256];
    int[] hitPrime = new int[256];
    int hitCount;

    Sieve(Siqs s, long seed) {
      this.s = s;
      random = new Random(seed);
      prime = s.prime;
      logp = s.logp;
      size = s.size;
      sieve = new byte[s.interval + GUARD + 8];
      longView = ByteBuffer.wrap(sieve).order(ByteOrder.nativeOrder());
      soln1 = new int[size];
      soln2 = new int[size];
      pos1 = new int[size];
      pos2 = new int[size];
      ainv = new int[size];
      noDelta = new int[size];
      pendingDelta = noDelta;
    }

    /** Choose a new <code>A</code> and sieve all its polynomials. */
    List<Relation> nextA() {
      if (!newA()) {
        return null;
      }
      List<Relation> result = new ArrayList<>();
      final int count = 1 << (aIndices.length - 1);
      for (int poly = 0; poly < count && !s.stop; poly++) {
        if (Thread.currentThread().isInterrupted()) {
          throw TimeoutException.TIMED_OUT;
        }
        if (poly > 0) {
          nextB(poly);
        }
        c = b.multiply(b).subtract(s.kn).divide(a);
        sieve();
        scan(result);
      }
      return result;
    }

    /** Choose the primes of <code>A</code> and compute the first <code>B</code> and the roots. */
    boolean newA() {
      final BigInteger target = s.targetA;
      double logTarget = Math.log(target.doubleValue());
      if (Double.isInfinite(logTarget)) {
        logTarget = target.bitLength() * Math.log(2);
      }
      // prefer primes of about 2000, or smaller ones for a small factor base
      int preferred = Math.max(prime[s.sieveStart], Math.min(2000, prime[(size * 2) / 3]));
      int count = Math.max(2, (int) Math.round(logTarget / Math.log(preferred)));
      while (count > 2 && Math.exp(logTarget / count) < prime[s.sieveStart] * 2) {
        count--;
      }
      while (Math.exp(logTarget / count) > prime[size - 1] / 2.0) {
        count++;
      }
      double center = Math.exp(logTarget / count);
      int centerIndex = indexOfPrime(center);
      int low = Math.max(s.sieveStart, centerIndex - Math.max(8, centerIndex / 4));
      int high = Math.min(size - 1, centerIndex + Math.max(8, centerIndex / 4));
      while (high - low < 3 * count) {
        low = Math.max(s.sieveStart, low - 4);
        high = Math.min(size - 1, high + 4);
        if (low == s.sieveStart && high == size - 1) {
          break;
        }
      }
      int[] chosen = new int[count];
      for (int tries = 0;; tries++) {
        if (tries > 100_000) {
          return false;
        }
        if ((tries & 0xFF) == 0xFF) {
          // widen the range if the products repeat
          low = Math.max(s.sieveStart, low - 4);
          high = Math.min(size - 1, high + 4);
        }
        BigInteger product = BigInteger.ONE;
        boolean ok = true;
        for (int i = 0; i < count - 1 && ok; i++) {
          int index = low + random.nextInt(high - low + 1);
          for (int k = 0; k < i; k++) {
            if (chosen[k] == index) {
              ok = false;
            }
          }
          if (index < s.sieveStart || s.sqrtKN[index] == 0) {
            ok = false;
          }
          chosen[i] = index;
          product = product.multiply(BigInteger.valueOf(prime[index]));
        }
        if (!ok) {
          continue;
        }
        // the last prime makes the product closest to the target
        double rest = target.doubleValue() / product.doubleValue();
        if (Double.isInfinite(rest) || Double.isNaN(rest)) {
          rest = Math.exp(logTarget - Math.log(product.doubleValue()));
        }
        int last = indexOfPrime(rest);
        if (last < s.sieveStart || s.sqrtKN[last] == 0) {
          continue;
        }
        for (int k = 0; k < count - 1; k++) {
          if (chosen[k] == last) {
            ok = false;
          }
        }
        if (!ok) {
          continue;
        }
        chosen[count - 1] = last;
        BigInteger newA = product.multiply(BigInteger.valueOf(prime[last]));
        double ratio = newA.doubleValue() / target.doubleValue();
        if (ratio < 0.5 || ratio > 2.0) {
          continue;
        }
        if (s.usedA.add(newA)) {
          a = newA;
          break;
        }
      }
      Arrays.sort(chosen);
      aIndices = chosen;
      pendingDelta = noDelta;
      pendingPlus = true;
      initializeA();
      return true;
    }

    /** The index of the prime of the factor base closest to <code>value</code>. */
    int indexOfPrime(double value) {
      if (value >= prime[size - 1]) {
        return size - 1;
      }
      int i = Arrays.binarySearch(prime, (int) Math.max(2, value));
      if (i >= 0) {
        return i;
      }
      i = -i - 1;
      if (i > 0 && value - prime[i - 1] < prime[i] - value) {
        i--;
      }
      return i;
    }

    /** The terms of <code>B</code>, the inverses of <code>A</code> and the first roots. */
    void initializeA() {
      final int count = aIndices.length;
      final BigInteger kn = s.kn;
      bTerms = new BigInteger[count];
      b = BigInteger.ZERO;
      int[] gamma = new int[count];
      for (int l = 0; l < count; l++) {
        int q = prime[aIndices[l]];
        BigInteger aq = a.divide(BigInteger.valueOf(q));
        int aqModQ = aq.mod(BigInteger.valueOf(q)).intValue();
        long g = (long) s.sqrtKN[aIndices[l]] * modInverse(aqModQ, q) % q;
        if (g > q / 2) {
          g = q - g;
        }
        gamma[l] = (int) g;
        bTerms[l] = aq.multiply(BigInteger.valueOf(g));
        b = b.add(bTerms[l]);
      }
      if (b.multiply(b).subtract(kn).mod(a).signum() != 0) {
        throw new IllegalStateException("B^2 != kn mod A");
      }
      if (bainv2 == null || bainv2.length != count) {
        bainv2 = new int[count][size];
      }
      final int m = s.halfInterval;
      long[] prefix = new long[count + 1];
      long[] qMod = new long[count];
      for (int j = 1; j < size; j++) {
        final int p = prime[j];
        prefix[0] = 1;
        for (int l = 0; l < count; l++) {
          qMod[l] = prime[aIndices[l]] % p;
          prefix[l + 1] = prefix[l] * qMod[l] % p;
        }
        final long aModP = prefix[count];
        if (aModP == 0 || j < s.sieveStart) {
          // a prime of A, or a small prime which is checked by trial division
          soln1[j] = soln2[j] = NO_ROOT;
          for (int l = 0; l < count; l++) {
            bainv2[l][j] = 0;
          }
          ainv[j] = 0;
          continue;
        }
        final long inverse = modInverse((int) aModP, p);
        ainv[j] = (int) inverse;
        long bModP = 0;
        long suffix = 1;
        for (int l = count - 1; l >= 0; l--) {
          // B_l = (A/q_l)*gamma_l
          long term = prefix[l] * suffix % p * (gamma[l] % p) % p;
          suffix = suffix * qMod[l] % p;
          bModP += term;
          bainv2[l][j] = (int) (2 * term % p * inverse % p);
        }
        bModP %= p;
        final long t = s.sqrtKN[j];
        long r1 = (t - bModP + p) % p * inverse % p;
        long r2 = (2L * p - t - bModP) % p * inverse % p;
        soln1[j] = (int) ((r1 + m) % p);
        soln2[j] = (int) ((r2 + m) % p);
      }
    }

    /**
     * Switch to the polynomial number <code>poly</code> in Gray code order. The roots of the primes
     * from {@link Siqs#mediumEnd} on are moved by {@link #sieve()}.
     */
    void nextB(int poly) {
      final int l = Integer.numberOfTrailingZeros(poly);
      final boolean plus = ((poly >>> (l + 1)) & 1) != 0;
      final int[] delta = bainv2[l];
      final int start = s.sieveStart;
      final int end = s.mediumEnd;
      if (plus) {
        // B + 2*B_l moves the roots by -2*B_l/A
        b = b.add(bTerms[l].shiftLeft(1));
        for (int j = start; j < end; j++) {
          final int p = prime[j];
          int r = soln1[j] - delta[j];
          soln1[j] = r + ((r >> 31) & p);
          r = soln2[j] - delta[j];
          soln2[j] = r + ((r >> 31) & p);
        }
      } else {
        b = b.subtract(bTerms[l].shiftLeft(1));
        for (int j = start; j < end; j++) {
          final int p = prime[j];
          int r = soln1[j] + delta[j] - p;
          soln1[j] = r + ((r >> 31) & p);
          r = soln2[j] + delta[j] - p;
          soln2[j] = r + ((r >> 31) & p);
        }
      }
      for (int index : aIndices) {
        soln1[index] = soln2[index] = NO_ROOT;
      }
      pendingDelta = delta;
      pendingPlus = plus;
    }

    void sieve() {
      final byte[] sieve = this.sieve;
      final int length = s.interval;
      Arrays.fill(sieve, 0, length, (byte) (128 - s.threshold));
      final int start = s.sieveStart;
      final int mediumEnd = s.mediumEnd;
      System.arraycopy(soln1, start, pos1, start, mediumEnd - start);
      System.arraycopy(soln2, start, pos2, start, mediumEnd - start);
      for (int blockEnd = BLOCK; blockEnd <= length; blockEnd += BLOCK) {
        for (int j = start; j < mediumEnd; j++) {
          final int p = prime[j];
          final byte lg = logp[j];
          int r1 = pos1[j];
          int r2 = pos2[j];
          if (r1 > r2) {
            int t = r1;
            r1 = r2;
            r2 = t;
          }
          final int d = r2 - r1;
          final int p2 = p + p;
          final int p3 = p2 + p;
          final int limit = blockEnd - d - p3;
          while (r1 < limit) {
            sieve[r1] += lg;
            sieve[r1 + d] += lg;
            sieve[r1 + p] += lg;
            sieve[r1 + p + d] += lg;
            sieve[r1 + p2] += lg;
            sieve[r1 + p2 + d] += lg;
            sieve[r1 + p3] += lg;
            sieve[r1 + p3 + d] += lg;
            r1 += p3 + p;
          }
          while (r1 + d < blockEnd) {
            sieve[r1] += lg;
            sieve[r1 + d] += lg;
            r1 += p;
          }
          r2 = r1 + d;
          if (r1 < blockEnd) {
            sieve[r1] += lg;
            r1 += p;
          }
          pos1[j] = r1;
          pos2[j] = r2;
        }
      }
      // move the roots to the current polynomial, then sieve without branches: k certain hits
      // and one which may fall on a guard byte behind the interval; the guard bytes differ from
      // prime to prime, so that the misses don't wait for each other
      final int[] delta = pendingDelta;
      final boolean plus = pendingPlus;
      final int[] classEnd = s.classEnd;
      for (int k = s.hitClasses; k >= 0; k--) {
        final int end = classEnd[k];
        for (int j = classEnd[k + 1]; j < end; j++) {
          final int p = prime[j];
          final byte lg = logp[j];
          final int d = plus ? delta[j] : p - delta[j];
          int r1 = soln1[j] - d;
          r1 += (r1 >> 31) & p;
          int r2 = soln2[j] - d;
          r2 += (r2 >> 31) & p;
          soln1[j] = r1;
          soln2[j] = r2;
          final int guard1 = length + (j & (GUARD - 1));
          final int guard2 = length + ((j + GUARD / 2) & (GUARD - 1));
          for (int i = 0; i < k; i++) {
            sieve[Math.min(r1, guard1)] += lg;
            sieve[Math.min(r2, guard2)] += lg;
            r1 += p;
            r2 += p;
          }
          sieve[Math.min(r1, guard1)] += lg;
          sieve[Math.min(r2, guard2)] += lg;
        }
      }
      for (int index : aIndices) {
        soln1[index] = soln2[index] = NO_ROOT;
      }
      pendingDelta = noDelta;
      pendingPlus = true;
    }

    void scan(List<Relation> result) {
      final int length = s.interval;
      int count = 0;
      for (int i = 0; i < length; i += 8) {
        if ((longView.getLong(i) & 0x8080808080808080L) != 0) {
          for (int k = i; k < i + 8; k++) {
            if (sieve[k] < 0) {
              if (count == candidate.length) {
                candidate = Arrays.copyOf(candidate, 2 * count);
              }
              candidate[count++] = k;
            }
          }
        }
      }
      if (count == 0) {
        return;
      }
      resieve();
      for (int i = 0; i < count; i++) {
        Relation r = trialDivide(candidate[i]);
        if (r != null) {
          result.add(r);
        }
      }
    }

    /**
     * Find the primes from {@link Siqs#mediumEnd} on which divide the value of a candidate, by
     * sieving them once more and recording the hits at the candidates, whose bytes are negative.
     */
    void resieve() {
      final byte[] sieve = this.sieve;
      final int length = s.interval;
      Arrays.fill(sieve, length, length + GUARD, (byte) 0);
      final int[] classEnd = s.classEnd;
      int hits = 0;
      for (int k = s.hitClasses; k >= 0; k--) {
        final int end = classEnd[k];
        for (int j = classEnd[k + 1]; j < end; j++) {
          final int p = prime[j];
          final int guard = length + (j & (GUARD - 1));
          int r1 = soln1[j];
          int r2 = soln2[j];
          for (int i = 0; i <= k; i++) {
            int x1 = Math.min(r1, guard);
            int x2 = Math.min(r2, guard);
            if ((sieve[x1] | sieve[x2]) < 0) {
              if (hits + 2 > hitPosition.length) {
                hitPosition = Arrays.copyOf(hitPosition, 2 * hitPosition.length);
                hitPrime = Arrays.copyOf(hitPrime, 2 * hitPrime.length);
              }
              if (sieve[x1] < 0) {
                hitPosition[hits] = x1;
                hitPrime[hits++] = j;
              }
              if (sieve[x2] < 0) {
                hitPosition[hits] = x2;
                hitPrime[hits++] = j;
              }
            }
            r1 += p;
            r2 += p;
          }
        }
      }
      hitCount = hits;
    }

    /** The relation of the sieve index <code>index</code>, or null if the value isn't smooth. */
    Relation trialDivide(int index) {
      final long x = index - s.halfInterval;
      final BigInteger bx = BigInteger.valueOf(x);
      final BigInteger ax = a.multiply(bx);
      BigInteger value = ax.add(b.shiftLeft(1)).multiply(bx).add(c);
      int count = 0;
      final int[] factors = factorBuffer;
      if (value.signum() < 0) {
        factors[count++] = 0;
        value = value.negate();
      } else if (value.signum() == 0) {
        return null;
      }
      for (int index2 : aIndices) {
        factors[count++] = index2 + 1;
      }
      int twos = value.getLowestSetBit();
      for (int i = 0; i < twos; i++) {
        factors[count++] = 1;
      }
      value = value.shiftRight(twos);
      final int[] w = words;
      int len = toWords(value, w);
      // the small primes and the primes of A by their remainders
      final int[] prime = this.prime;
      for (int j = 1; j < s.sieveStart; j++) {
        while (mod(w, len, prime[j]) == 0) {
          len = divide(w, len, prime[j]);
          factors[count++] = j + 1;
        }
      }
      for (int j : aIndices) {
        while (mod(w, len, prime[j]) == 0) {
          len = divide(w, len, prime[j]);
          factors[count++] = j + 1;
        }
      }
      // the sieved primes below mediumEnd by their roots
      final long longIndex = index;
      final long[] reciprocal = s.reciprocal;
      final int[] soln1 = this.soln1;
      final int[] soln2 = this.soln2;
      final int mediumEnd = s.mediumEnd;
      for (int j = s.sieveStart; j < mediumEnd; j++) {
        final int p = prime[j];
        final int r = (int) (longIndex - ((longIndex * reciprocal[j]) >>> 40) * p);
        if (r == soln1[j] || r == soln2[j]) {
          while (mod(w, len, p) == 0) {
            len = divide(w, len, p);
            factors[count++] = j + 1;
          }
        }
      }
      // the larger primes from the hits of resieve()
      for (int h = 0; h < hitCount; h++) {
        if (hitPosition[h] == index) {
          final int p = prime[hitPrime[h]];
          while (mod(w, len, p) == 0) {
            len = divide(w, len, p);
            factors[count++] = hitPrime[h] + 1;
          }
        }
      }
      final BigInteger y = ax.add(b).mod(s.n);
      if (len == 1 && w[0] == 1) {
        return new Relation(y, Arrays.copyOf(factors, count), BigInteger.ONE);
      }
      if (len > 2) {
        return null;
      }
      final long rest = (w[0] & 0xFFFFFFFFL) | (len == 2 ? ((long) w[1]) << 32 : 0L);
      if (rest < 0) {
        return null;
      }
      if (rest < s.largePrimeBound) {
        // no prime factor up to the largest prime of the factor base, so rest is a prime
        return new Relation(y, Arrays.copyOf(factors, count), rest, 1L);
      }
      if (rest >= s.doubleLargeBound || isPrime(rest)) {
        return null;
      }
      long f = rhoSplit(rest);
      if (f <= 1 || f >= rest) {
        return null;
      }
      long g = rest / f;
      if (f >= s.largePrimeBound || g >= s.largePrimeBound) {
        return null;
      }
      if (f == g) {
        return new Relation(y, Arrays.copyOf(factors, count), BigInteger.valueOf(f));
      }
      return new Relation(y, Arrays.copyOf(factors, count), Math.min(f, g), Math.max(f, g));
    }
  }

  /**
   * <code>a*b/2^64 mod m</code> for <code>a, b &lt; m &lt; 2^63</code> and
   * <code>mInverse = -1/m mod 2^64</code>.
   */
  private static long montgomeryMultiply(long a, long b, long m, long mInverse) {
    long low = a * b;
    long high = Math.multiplyHigh(a, b);
    long q = low * mInverse;
    // low + q*m = 0 mod 2^64, with a carry unless low = 0
    long t = high + Math.multiplyHigh(q, m) + ((q >> 63) & m) + (low != 0 ? 1 : 0);
    // t < 2*m, unsigned
    return Long.compareUnsigned(t, m) >= 0 ? t - m : t;
  }

  /** <code>-1/m mod 2^64</code> for an odd <code>m</code>. */
  private static long negativeInverse(long m) {
    long inverse = m;
    for (int i = 0; i < 5; i++) {
      inverse *= 2 - m * inverse;
    }
    return -inverse;
  }

  /** <code>2^64 mod m</code>, the Montgomery form of 1. */
  private static long montgomeryOne(long m) {
    return Long.remainderUnsigned(-m, m);
  }

  /** The Miller-Rabin test with the bases which decide it for all numbers below 2^64. */
  static boolean isPrime(long m) {
    if (m < 2) {
      return false;
    }
    for (int q : new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
      if (m % q == 0) {
        return m == q;
      }
    }
    final long inverse = negativeInverse(m);
    final long one = montgomeryOne(m);
    final long minusOne = m - one;
    long d = m - 1;
    int twos = Long.numberOfTrailingZeros(d);
    d >>>= twos;
    for (int base : new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
      // the Montgomery form base*2^64 mod m
      long b = 0;
      for (int i = 0; i < base; i++) {
        // b + one < 2*m can exceed 2^63 for m > 2^62, so compare unsigned
        b += one;
        if (Long.compareUnsigned(b, m) >= 0) {
          b -= m;
        }
      }
      long x = one;
      for (int i = 63 - Long.numberOfLeadingZeros(d); i >= 0; i--) {
        x = montgomeryMultiply(x, x, m, inverse);
        if ((d >>> i & 1L) != 0) {
          x = montgomeryMultiply(x, b, m, inverse);
        }
      }
      if (x == one || x == minusOne) {
        continue;
      }
      boolean composite = true;
      for (int r = 1; r < twos; r++) {
        x = montgomeryMultiply(x, x, m, inverse);
        if (x == minusOne) {
          composite = false;
          break;
        }
      }
      if (composite) {
        return false;
      }
    }
    return true;
  }

  /**
   * A divisor of the odd composite <code>m &lt; 2^63</code> by Brent's variant of Pollard's rho
   * method in 64 bit Montgomery arithmetic, or 1 if none was found.
   */
  static long rhoSplit(long m) {
    final long inverse = negativeInverse(m);
    for (long c = 1; c < 20; c++) {
      long y = 2;
      long x = y;
      long ys = y;
      long q = montgomeryOne(m);
      long g = 1;
      for (int r = 1; g == 1 && r < (1 << 22); r <<= 1) {
        if (r >= (1 << 16) && Thread.currentThread().isInterrupted()) {
          throw TimeoutException.TIMED_OUT;
        }
        x = y;
        for (int i = 0; i < r; i++) {
          y = montgomeryMultiply(y, y, m, inverse) + c;
          if (Long.compareUnsigned(y, m) >= 0) {
            y -= m;
          }
        }
        for (int k = 0; k < r && g == 1; k += 64) {
          ys = y;
          for (int i = 0; i < Math.min(64, r - k); i++) {
            y = montgomeryMultiply(y, y, m, inverse) + c;
            if (Long.compareUnsigned(y, m) >= 0) {
              y -= m;
            }
            q = montgomeryMultiply(q, x > y ? x - y : y - x, m, inverse);
          }
          g = gcd(q, m);
        }
      }
      if (g == m) {
        do {
          ys = montgomeryMultiply(ys, ys, m, inverse) + c;
          if (Long.compareUnsigned(ys, m) >= 0) {
            ys -= m;
          }
          g = gcd(x > ys ? x - ys : ys - x, m);
        } while (g == 1);
      }
      if (g != m && g != 1) {
        return g;
      }
    }
    return 1;
  }

  private static long gcd(long a, long b) {
    while (b != 0) {
      long t = a % b;
      a = b;
      b = t;
    }
    return a;
  }

  /** The magnitude of <code>value</code> as 32 bit words, the least significant first. */
  static int[] words(BigInteger value) {
    int[] w = new int[(value.bitLength() + 31) / 32 + 1];
    int len = toWords(value, w);
    return Arrays.copyOf(w, len);
  }

  /** Store the words of the nonnegative <code>value</code> in <code>w</code>. */
  static int toWords(BigInteger value, int[] w) {
    int len = (value.bitLength() + 31) >>> 5;
    if (len == 0) {
      w[0] = 0;
      return 1;
    }
    byte[] bytes = value.toByteArray();
    int end = bytes.length;
    for (int i = 0; i < len; i++) {
      int word = 0;
      for (int k = 0; k < 4; k++) {
        int at = end - 1 - 4 * i - k;
        if (at >= 0) {
          word |= (bytes[at] & 0xFF) << (8 * k);
        }
      }
      w[i] = word;
    }
    return len;
  }

  /** The remainder of the number with the words <code>w[0..len-1]</code> modulo <code>p</code>. */
  static int mod(int[] w, int len, int p) {
    long r = 0;
    for (int i = len - 1; i >= 0; i--) {
      r = ((r << 32) | (w[i] & 0xFFFFFFFFL)) % p;
    }
    return (int) r;
  }

  /** Divide the number with the words <code>w[0..len-1]</code> by <code>p</code> in place. */
  static int divide(int[] w, int len, int p) {
    long r = 0;
    for (int i = len - 1; i >= 0; i--) {
      long current = (r << 32) | (w[i] & 0xFFFFFFFFL);
      w[i] = (int) (current / p);
      r = current % p;
    }
    while (len > 1 && w[len - 1] == 0) {
      len--;
    }
    return len;
  }

  static long powMod(long base, long exponent, long p) {
    long result = 1;
    base %= p;
    while (exponent > 0) {
      if ((exponent & 1) != 0) {
        result = result * base % p;
      }
      base = base * base % p;
      exponent >>= 1;
    }
    return result;
  }

  /** The inverse of <code>0 &lt; a &lt; p</code> modulo the prime <code>p</code>. */
  static long modInverse(int a, int p) {
    int r0 = p;
    int r1 = a % p;
    int t0 = 0;
    int t1 = 1;
    while (r1 != 0) {
      int q = r0 / r1;
      int r = r0 - q * r1;
      r0 = r1;
      r1 = r;
      int t = t0 - q * t1;
      t0 = t1;
      t1 = t;
    }
    return t0 < 0 ? t0 + p : t0;
  }

  /** A square root of the quadratic residue <code>a</code> modulo the odd prime <code>p</code>. */
  static int sqrtMod(int a, int p) {
    if ((p & 3) == 3) {
      return (int) powMod(a, (p + 1) >>> 2, p);
    }
    // Tonelli-Shanks
    int q = p - 1;
    int e = 0;
    while ((q & 1) == 0) {
      q >>= 1;
      e++;
    }
    int z = 2;
    while (powMod(z, (p - 1) >>> 1, p) != p - 1) {
      z++;
    }
    long c = powMod(z, q, p);
    long r = powMod(a, (q + 1) >>> 1, p);
    long t = powMod(a, q, p);
    int m = e;
    while (t != 1) {
      int i = 0;
      long tt = t;
      while (tt != 1) {
        tt = tt * tt % p;
        i++;
      }
      long bb = c;
      for (int k = 0; k < m - i - 1; k++) {
        bb = bb * bb % p;
      }
      r = r * bb % p;
      c = bb * bb % p;
      t = t * c % p;
      m = i;
    }
    return (int) r;
  }
}
