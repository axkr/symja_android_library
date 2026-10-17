package org.matheclipse.core.numbertheory;

import java.util.Arrays;
import org.matheclipse.core.eval.exception.TimeoutException;

/**
 * Montgomery's PRAC algorithm: scalar multiplication <code>[k]P</code> on a curve which only has a
 * doubling and a differential addition (<code>P+Q</code> from <code>P</code>, <code>Q</code> and
 * <code>P-Q</code>), with Lucas chains which are shorter than the Montgomery ladder.
 *
 * <p>
 * The chain for a prime <code>k</code> keeps three points <code>A, B, C = A-B</code> and two
 * numbers <code>d, e</code> with <code>[k]P = [d]A + [e]B</code>, and applies the first of nine
 * rules whose condition holds until <code>d = e = 1</code>. The chains are computed once as a
 * program of opcodes, which {@link #run(DifferentialCurve, Object, byte[])} then executes on any
 * curve.
 *
 * <p>
 * See P. L. Montgomery, "Evaluating recurrences of form X_{m+n} = f(X_m, X_n, X_{m-n}) via Lucas
 * chains" (1983, revised 1992).
 */
final class LucasChains {

  /** The arithmetic of a curve with x-only points of type <code>P</code>. */
  interface DifferentialCurve<P> {
    P newPoint();

    /** <code>to = from</code> */
    void copy(P from, P to);

    /** <code>out = [2]p</code>; <code>out</code> may be <code>p</code> */
    void dbl(P p, P out);

    /**
     * <code>out = p+q</code> from <code>p</code>, <code>q</code> and
     * <code>difference = &plusmn;(p-q)</code>; <code>out</code> may be any of the inputs
     */
    void add(P p, P q, P difference, P out);
  }

  /** <code>A = [2]A</code> */
  static final byte DOUBLE = 0;
  /** <code>B = A, C = A, A = [2]A</code>: the start of a chain */
  static final byte START = 1;
  /** <code>A = A+B</code>: the end of a chain */
  static final byte FINISH = 2;
  /** swap <code>A</code> and <code>B</code> */
  static final byte SWAP = 3;
  /** The opcodes of the rules 1 to 9 are <code>RULE + 1</code> to <code>RULE + 9</code>. */
  static final byte RULE = 3;

  /** The cost of a differential addition and a doubling in multiplications. */
  private static final int ADD_COST = 6;
  private static final int DUP_COST = 5;

  /** Distance of the tried multipliers <code>r</code> from <code>k/phi</code>. */
  private static final int SEARCH_RADIUS = 4;

  private static final double INVERSE_GOLDEN_RATIO = (Math.sqrt(5.0) - 1.0) / 2.0;

  private LucasChains() {}

  /**
   * The rule which applies to <code>d &gt;= e</code>.
   *
   * @return the rule number 1 to 9
   */
  private static int rule(long d, long e) {
    if (4 * d <= 5 * e && (d + e) % 3 == 0) {
      return 1;
    }
    if (4 * d <= 5 * e && (d - e) % 6 == 0) {
      return 2;
    }
    if (d <= 4 * e) {
      return 3;
    }
    if ((d + e) % 2 == 0) {
      return 4;
    }
    // d+e is odd
    if (d % 2 == 0) {
      return 5;
    }
    // d is odd, e is even
    if (d % 3 == 0) {
      return 6;
    }
    if ((d + e) % 3 == 0) {
      return 7;
    }
    if ((d - e) % 3 == 0) {
      return 8;
    }
    return 9;
  }

  /** The cost of a rule in multiplications. */
  private static int ruleCost(int rule) {
    switch (rule) {
      case 1:
        return 3 * ADD_COST;
      case 3:
        return ADD_COST;
      case 6:
      case 7:
      case 8:
        return 3 * ADD_COST + DUP_COST;
      default:
        return ADD_COST + DUP_COST;
    }
  }

  /**
   * Walk the chain for <code>k</code> and the multiplier <code>r</code>.
   *
   * @param program the opcodes are appended here from position <code>length</code>, or
   *        <code>null</code> to compute the cost only
   * @return the cost in multiplications, or the new length of the program
   */
  private static long walk(long k, long r, byte[] program, int length) {
    long d = k - r;
    long e = 2 * r - k;
    long cost = DUP_COST + ADD_COST;
    if (program != null) {
      program[length++] = START;
    }
    while (d != e) {
      if (d < e) {
        long swap = d;
        d = e;
        e = swap;
        if (program != null) {
          program[length++] = SWAP;
        }
      }
      int rule = rule(d, e);
      switch (rule) {
        case 1:
          d = (2 * d - e) / 3;
          e = (e - d) / 2;
          break;
        case 2:
        case 4:
          d = (d - e) / 2;
          break;
        case 3:
          d -= e;
          break;
        case 5:
          d /= 2;
          break;
        case 6:
          d = d / 3 - e;
          break;
        case 7:
          d = (d - 2 * e) / 3;
          break;
        case 8:
          d = (d - e) / 3;
          break;
        default:
          e /= 2;
          break;
      }
      cost += ruleCost(rule);
      if (program != null) {
        program[length++] = (byte) (RULE + rule);
      }
    }
    if (program != null) {
      program[length++] = FINISH;
      return length;
    }
    return cost;
  }

  /**
   * The multiplier <code>r</code> with the cheapest chain for the odd prime <code>k</code>, tried
   * near <code>k/phi</code>.
   */
  static long bestMultiplier(long k) {
    long center = Math.round(k * INVERSE_GOLDEN_RATIO);
    long best = -1;
    long bestCost = Long.MAX_VALUE;
    for (long r = center - SEARCH_RADIUS; r <= center + SEARCH_RADIUS; r++) {
      // d = k-r > 0, e = 2r-k > 0, and gcd(r,k) = 1 since k is prime
      if (2 * r <= k || r >= k) {
        continue;
      }
      long cost = walk(k, r, null, 0);
      if (cost < bestCost) {
        bestCost = cost;
        best = r;
      }
    }
    return best;
  }

  /**
   * The program which multiplies a point by every prime power <code>q^e &lt;= b1</code> of the
   * primes <code>q</code> in <code>primes[0..count-1]</code>.
   *
   * @throws TimeoutException if the thread is interrupted
   */
  static byte[] stage1Program(int[] primes, int count, int b1) {
    byte[] program = new byte[1024];
    int length = 0;
    for (int i = 0; i < count; i++) {
      if ((i & 0x3FF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      int q = primes[i];
      byte[] chain = program(q);
      int chainLength = chain.length;
      for (long qk = q; qk <= b1; qk *= q) {
        if (length + chainLength > program.length) {
          program = Arrays.copyOf(program, Math.max(2 * program.length, length + chainLength));
        }
        System.arraycopy(chain, 0, program, length, chainLength);
        length += chainLength;
      }
    }
    return Arrays.copyOf(program, length);
  }

  /** The program which multiplies a point by the prime <code>k</code>. */
  static byte[] program(int k) {
    if (k == 2) {
      return new byte[] {DOUBLE};
    }
    byte[] chain = new byte[256];
    return Arrays.copyOf(chain, (int) walk(k, bestMultiplier(k), chain, 0));
  }

  /**
   * Execute the <code>program</code> on <code>point</code>.
   *
   * @throws TimeoutException if the thread is interrupted
   */
  static <P> void run(DifferentialCurve<P> curve, P point, byte[] program) {
    P a = curve.newPoint();
    P b = curve.newPoint();
    P c = curve.newPoint();
    P t = curve.newPoint();
    P t2 = curve.newPoint();
    P swap;
    curve.copy(point, a);
    for (int i = 0; i < program.length; i++) {
      if ((i & 0xFFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      switch (program[i]) {
        case DOUBLE:
          curve.dbl(a, a);
          break;
        case START:
          curve.copy(a, b);
          curve.copy(a, c);
          curve.dbl(a, a);
          break;
        case FINISH:
          curve.add(a, b, c, a);
          break;
        case SWAP:
          swap = a;
          a = b;
          b = swap;
          break;
        case RULE + 1:
          // A, B = 2A+B, A+2B
          curve.add(a, b, c, t);
          curve.add(t, a, b, t2);
          curve.add(b, t, a, b);
          swap = a;
          a = t2;
          t2 = swap;
          break;
        case RULE + 2:
        case RULE + 4:
          // B = A+B, A = 2A
          curve.add(b, a, c, b);
          curve.dbl(a, a);
          break;
        case RULE + 3:
          // B = A+B, C = B
          curve.add(b, a, c, t);
          swap = b;
          b = t;
          t = c;
          c = swap;
          break;
        case RULE + 5:
          // C = A+C = 2A-B, A = 2A
          curve.add(c, a, b, c);
          curve.dbl(a, a);
          break;
        case RULE + 6:
          // A = 3A, B = 3A+B, C = B
          curve.dbl(a, t);
          curve.add(a, b, c, t2);
          curve.add(t, a, a, a);
          curve.add(t, t2, c, t);
          swap = c;
          c = b;
          b = t;
          t = swap;
          break;
        case RULE + 7:
          // B = 2A+B, A = 3A
          curve.add(a, b, c, t);
          curve.add(t, a, b, b);
          curve.dbl(a, t);
          curve.add(a, t, a, a);
          break;
        case RULE + 8:
          // B = A+B, C = 2A-B, A = 3A
          curve.add(a, b, c, t);
          curve.add(c, a, b, c);
          swap = b;
          b = t;
          t = swap;
          curve.dbl(a, t);
          curve.add(a, t, a, a);
          break;
        default:
          // RULE + 9: C = C-B = A-2B from C+B = A, B = 2B
          curve.add(c, b, a, c);
          curve.dbl(b, b);
          break;
      }
    }
    curve.copy(a, point);
  }
}
