package org.matheclipse.core.numerics.series;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The value of an infinite series <code>Sum(f(n), {n, a, Infinity})</code> whose terms are only
 * known at the integers: a function defined for integer arguments, or one which is constant
 * between the integers like <code>f(Round(n))</code>. The first terms classify the series:
 * <ul>
 * <li><b>smooth</b>: the terms follow one smooth law, the Levin u-transform of the partial sums is
 * the value.
 * <li><b>blocked</b>: the terms are smooth on the blocks <code>b^(j-1)+c &lt;= n &lt; b^j+c</code>
 * and jump at the block ends, like a function of the number of digits of <code>n</code>. Each
 * block is summed on its own: a short block term by term, a long one by the Gregory form of the
 * Euler-Maclaurin formula - the integral of the smooth interpolant of the terms plus the end
 * corrections from the differences of the terms. The series of the block sums decreases smoothly
 * in <code>j</code> once the exponentially small part of the block sums is below the digits, and
 * its tail is the Levin u-transform of its terms.
 * <li><b>rough</b>: neither; there is no value.
 * </ul>
 */
public final class IntegerSeriesSum {

  /** The number of terms which classify the series. */
  private static final int HEAD = 1100;

  /** A block of at most this many terms is added term by term. */
  private static final int DIRECT_MAX = 2000;

  /** The terms below this index are added term by term. */
  private static final BigInteger GREGORY_START = BigInteger.valueOf(1000L);

  /** The largest bit length of an index whose term is evaluated. */
  private static final int MAX_INDEX_BITS = 60000;

  /** The largest order of the end differences of the Gregory formula. */
  private static final int MAX_DIFFERENCES = 70;

  /** The largest number of Gauss-Legendre nodes on a panel. */
  private static final int MAX_NODES = 128;

  /** The block sums are smooth in the number of digits up to at least this number. */
  private static final double LEVEL_TWO_DIGITS = 130.0;

  /** The largest number of block sums which are added before the tail. */
  private static final int MAX_BLOCKS = 1500;

  /** The coefficients <code>c(k)</code> of the end differences of the Gregory formula. */
  private static final BigFraction[] GREGORY = gregoryCoefficients(MAX_DIFFERENCES + 1);

  /** A series which isn't summed here. */
  private static final class Decline extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Decline() {
      super(null, null, false, false);
    }
  }

  private static final Decline DECLINE = new Decline();

  private final IExpr function;
  private final ISymbol variable;
  /** the digits of the result */
  private final long digits;
  /** the digits of the sums */
  private final long work;
  /** the digits of the terms */
  private final long precision;
  private final EvalEngine engine;
  private final FixedPrecisionApfloatHelper helper;
  private final Map<BigInteger, Apfloat> cache = new HashMap<>();
  private final Map<Integer, Apfloat[][]> nodes = new HashMap<>();
  /** the ratio <code>z/a</code> of a block summed by the Gregory formula, and its logarithm */
  private Apfloat referenceRatio;
  private Apfloat referenceWidth;
  /** the fractions of the width at the nodes of the panels, and their exponentials */
  private final Map<Integer, Apfloat[][]> exponentials = new HashMap<>();

  private IntegerSeriesSum(IExpr function, ISymbol variable, long digits, EvalEngine engine) {
    this.function = function;
    this.variable = variable;
    this.digits = digits;
    this.work = digits + 10L;
    this.precision = work + 20L;
    this.engine = engine;
    this.helper = new FixedPrecisionApfloatHelper(precision);
  }

  /**
   * Test if the terms of <code>function</code> are only known at the integers: near the integers
   * <code>k</code> the function has no numeric value or the value at <code>k</code>, and it isn't
   * constant.
   */
  public static boolean isIntegerOnly(IExpr function, ISymbol variable, long lower,
      EvalEngine engine) {
    IExpr previous = null;
    boolean constant = true;
    for (long k : new long[] {lower + 5L, lower + 17L, lower + 40L}) {
      IExpr value = numeric(function, variable, F.ZZ(k), engine);
      if (!value.isReal()) {
        return false;
      }
      if (previous != null && !previous.equals(value)) {
        constant = false;
      }
      previous = value;
      for (IExpr offset : new IExpr[] {F.C1D4, F.CN1D4, F.QQ(2, 5), F.QQ(-2, 5)}) {
        IExpr near = numeric(function, variable, F.Plus(F.ZZ(k), offset), engine);
        if (near.isNumber() && !near.equals(value)) {
          return false;
        }
      }
    }
    return !constant;
  }

  private static IExpr numeric(IExpr function, ISymbol variable, IExpr x, EvalEngine engine) {
    IExpr term = engine.evalQuiet(F.subst(function, v -> v.equals(variable) ? x : F.NIL));
    return engine.evalQuiet(F.N(term));
  }

  /**
   * The value of <code>Sum(function, {variable, lower, Infinity})</code> with <code>digits</code>
   * digits, for terms which are only known at the integers.
   *
   * @return <code>null</code> if the series is none of the kinds summed here
   */
  public static Apfloat sum(IExpr function, ISymbol variable, long lower, long digits,
      EvalEngine engine) {
    try {
      return new IntegerSeriesSum(function, variable, digits, engine).sum(lower);
    } catch (Decline decline) {
      return null;
    }
  }

  /** The real value of the term with index <code>n</code>. */
  private Apfloat term(BigInteger n) {
    Apfloat value = cache.get(n);
    if (value != null) {
      return value;
    }
    if (n.bitLength() > MAX_INDEX_BITS) {
      throw DECLINE;
    }
    if (cache.size() > 200_000) {
      cache.clear();
    }
    IExpr term =
        engine.evaluate(F.subst(function, v -> v.equals(variable) ? F.ZZ(n) : F.NIL));
    if (term.isRational()) {
      // an exact term isn't evaluated again by N
      IRational rational = (IRational) term;
      value = helper.divide(new Apfloat(rational.numerator().toBigNumerator(), precision),
          new Apfloat(rational.denominator().toBigNumerator(), precision));
    } else {
      term = engine.evalN(term, precision);
      if (!term.isReal()) {
        throw DECLINE;
      }
      value = helper.valueOf(((IReal) term).apfloatValue());
    }
    cache.put(n, value);
    return value;
  }

  private Apfloat sum(long lower) {
    double[] logs = new double[HEAD];
    int[] signs = new int[HEAD];
    for (int i = 0; i < HEAD; i++) {
      if ((i & 63) == 0) {
        engine.checkTimeLimit();
      }
      Apfloat t = term(BigInteger.valueOf(lower + i));
      signs[i] = t.signum();
      if (signs[i] == 0) {
        throw DECLINE;
      }
      logs[i] = log(t);
    }
    Classifier classifier = new Classifier(lower, logs, signs);
    if (classifier.smoothFrom(0, HEAD)) {
      return smooth(lower, logs, signs);
    }
    long[] blocks = classifier.blocks();
    if (blocks == null) {
      throw DECLINE;
    }
    return blocked(lower, blocks[0], blocks[1]);
  }

  /** <code>Log(Abs(x))</code> as a double, also beyond the range of a double. */
  private static double log(Apfloat x) {
    Apfloat a = ApfloatMath.abs(x);
    long scale = a.scale();
    Apfloat mantissa = a.divide(ApfloatMath.pow(new Apfloat(10, a.precision()), scale));
    return Math.log(mantissa.doubleValue()) + scale * Math.log(10.0);
  }

  /**
   * The smooth and the blocked laws of the first terms: a term is smooth if the cubic through the
   * logarithms of the four terms before it predicts its own, and its sign is that of the term
   * before it or its negative throughout.
   */
  private static final class Classifier {
    private final long lower;
    private final double[] logs;
    private final int[] signs;
    /** <code>1</code> for terms of one sign, <code>-1</code> for alternating signs */
    private final int signRule;

    Classifier(long lower, double[] logs, int[] signs) {
      this.lower = lower;
      this.logs = logs;
      this.signs = signs;
      this.signRule = signs[1] == signs[0] ? 1 : -1;
    }

    /**
     * Test if the term with head index <code>i</code> follows the four terms before it, whose signs
     * change by the factor <code>rule</code>.
     */
    boolean smoothAt(int i, int rule) {
      for (int k = i - 4; k < i; k++) {
        if (signs[k + 1] != rule * signs[k]) {
          return false;
        }
      }
      double predicted = 4.0 * logs[i - 1] - 6.0 * logs[i - 2] + 4.0 * logs[i - 3] - logs[i - 4];
      double n = (double) (lower + i);
      double tolerance = Math.max(1e-7, 200.0 / (n * n * n * n));
      return Math.abs(predicted - logs[i]) <= tolerance;
    }

    /** Test if the terms with head indices <code>from &lt;= i &lt; to</code> are smooth. */
    boolean smoothFrom(int from, int to) {
      for (int i = from + 4; i < to; i++) {
        if (!smoothAt(i, signRule)) {
          return false;
        }
      }
      return true;
    }

    /**
     * The base <code>b</code> and offset <code>c</code> of the smallest blocks
     * <code>b^(j-1)+c &lt;= n &lt; b^j+c</code> on which the terms are smooth, with a jump at a
     * block end. In a block the terms have one sign.
     *
     * @return <code>null</code> if there are no such blocks
     */
    long[] blocks() {
      for (long b = 2; b <= 16; b++) {
        for (long c : new long[] {0L, 1L, -1L}) {
          if (blocksOf(b, c)) {
            return new long[] {b, c};
          }
        }
      }
      return null;
    }

    private boolean blocksOf(long b, long c) {
      int end = logs.length;
      int start = 0;
      int jumps = 0;
      long boundary = b + c;
      while (start < end) {
        while (boundary <= lower + start) {
          boundary = Math.multiplyExact(boundary - c, b) + c;
        }
        int next = (int) Math.min(end, boundary - lower);
        // the block [start, next): smooth terms of one sign
        for (int i = start + 1; i < next; i++) {
          if (signs[i] != signs[start]) {
            return false;
          }
        }
        for (int i = start + 4; i < next; i++) {
          if (!smoothAt(i, 1)) {
            return false;
          }
        }
        if (next < end && next - start >= 4 && !smoothAt(next, 1)) {
          jumps++;
        } else if (next < end && next - start >= 1 && signs[next] != signs[next - 1]) {
          jumps++;
        }
        start = next;
      }
      return jumps > 0;
    }
  }

  /**
   * The sum of smooth terms: the Levin u-transform of their partial sums, if the terms alternate
   * or decrease faster than <code>1/n</code>.
   */
  private Apfloat smooth(long lower, double[] logs, int[] signs) {
    boolean alternating = signs[1] != signs[0];
    // the exponent s of n^-s from the terms at n and 2 n
    int i = 500;
    double n = (double) (lower + i);
    double s = (logs[i] - logs[2 * i]) / Math.log((n + i) / n);
    if (!alternating && s <= 1.05) {
      throw DECLINE;
    }
    long from = lower;
    double beta = Math.max(1.0, lower);
    IntegerSeriesSum fine = finer(beta, levinOrder());
    return levinTail(index -> fine.term(BigInteger.valueOf(from + index)), beta, Apfloat.ZERO);
  }

  /** {@link PrecisionSum#levinU} of real terms. */
  private static Apfloat levinU(Apfloat[] terms, int k, double beta,
      FixedPrecisionApfloatHelper h) {
    org.apfloat.Apcomplex value = PrecisionSum.levinU(terms, k, beta, h, h.precision());
    return value == null ? null : value.real();
  }

  /** The order of the Levin u-transform. */
  private int levinOrder() {
    return (int) Math.min(80L, (digits <= 16L ? 14L : digits) + 14L);
  }

  /**
   * This sum with more digits if the Levin u-transform of order <code>order</code> of terms smooth
   * on the scale <code>beta</code> cancels digits: about <code>Log10((2 beta)^order/order!)</code>.
   */
  private IntegerSeriesSum finer(double beta, int order) {
    double cancelled = cancelled(beta, order);
    if (cancelled <= 0.0) {
      return this;
    }
    return new IntegerSeriesSum(function, variable, digits + (long) Math.ceil(cancelled) + 5L,
        engine);
  }

  /** <code>Log10((2 beta)^order/order!)</code>. */
  private static double cancelled(double beta, int order) {
    double cancelled = order * Math.log10(2.0 * beta);
    for (int i = 2; i <= order; i++) {
      cancelled -= Math.log10(i);
    }
    return cancelled;
  }

  /** A term of a sequence by its index from the start. */
  @FunctionalInterface
  private interface Terms {
    Apfloat get(int index);
  }

  /**
   * <code>head</code> plus the sum of <code>terms</code>, the term with index <code>j</code>
   * like <code>(beta + j)^-s</code> or alternating: the Levin u-transforms of two orders have to
   * agree to the digits of the result, to 14 digits for a machine result.
   */
  private Apfloat levinTail(Terms terms, double beta, Apfloat head) {
    final long need = digits <= 16L ? 14L : digits;
    final int order = levinOrder();
    Apfloat[] values = new Apfloat[order + 1];
    for (int k = 0; k <= order; k++) {
      engine.checkTimeLimit();
      values[k] = terms.get(k);
      if (values[k].signum() == 0) {
        throw DECLINE;
      }
    }
    final long levinPrecision = Math.max(2L * work, values[0].precision()) + order;
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(levinPrecision);
    Apfloat first = levinU(values, order, beta, h);
    Apfloat second = levinU(values, order - 4, beta, h);
    if (first == null || second == null) {
      throw DECLINE;
    }
    Apfloat value = helper.add(head, helper.valueOf(first));
    Apfloat difference = ApfloatMath.abs(helper.subtract(first, second));
    if (value.signum() == 0
        || (difference.signum() != 0 && difference.scale() >= value.scale() - need)) {
      throw DECLINE;
    }
    return value;
  }

  /**
   * The sum of terms which are smooth on the blocks <code>b^(j-1)+c &lt;= n &lt; b^j+c</code>: the
   * block sums are added while they aren't smooth in <code>j</code>, or until they are negligible;
   * the rest is the Levin u-transform of the following block sums.
   */
  private Apfloat blocked(long lower, long b, long c) {
    BigInteger base = BigInteger.valueOf(b);
    BigInteger offset = BigInteger.valueOf(c);
    // the block ends b^j+c above lower
    BigInteger power = base;
    // the exponent of power: the terms of the block starting at b^j+c have j+1 digits
    int exponent = 1;
    while (power.add(offset).compareTo(BigInteger.valueOf(lower)) <= 0) {
      power = power.multiply(base);
      exponent++;
    }
    // the block sums at the start of the tail have an exponentially small part, which the Levin
    // u-transform amplifies like the rounding errors: below the digits and the cancelled ones
    double smoothDigits = work + 2.0;
    for (int i = 0; i < 4; i++) {
      double j = smoothDigits / Math.log10(b) + 1.0;
      smoothDigits = work + 2.0 + Math.max(0.0, cancelled(j, levinOrder()));
    }
    BigInteger smooth = BigInteger.TEN.pow((int) Math.ceil(smoothDigits));
    Apfloat sum = blockSum(BigInteger.valueOf(lower), power.add(offset).subtract(BigInteger.ONE));
    // the block sums of the whole blocks by the number of digits j of their terms
    final int firstDigits = exponent + 1;
    java.util.List<Apfloat> blockSums = new java.util.ArrayList<>();
    int small = 0;
    int blocks = 0;
    while (power.compareTo(smooth) < 0) {
      engine.checkTimeLimit();
      if (++blocks > MAX_BLOCKS) {
        throw DECLINE;
      }
      BigInteger next = power.multiply(base);
      Apfloat s = blockSum(power.add(offset), next.add(offset).subtract(BigInteger.ONE));
      blockSums.add(s);
      sum = helper.add(sum, s);
      power = next;
      exponent++;
      if (negligible(s, sum)) {
        if (++small >= 3) {
          return sum;
        }
      } else {
        small = 0;
      }
    }
    final BigInteger tailPower = power;
    final Apfloat head = sum;
    // the Levin u-transform of order k of terms smooth on the scale beta cancels about
    // Log10((2 beta)^k/k!) digits: the block sums of the tail have them in addition
    final int order = levinOrder();
    double beta = exponent + 1.0;
    final IntegerSeriesSum fine = finer(beta, order);
    Map<Integer, Apfloat> sums = new HashMap<>();
    Terms tail = index -> sums.computeIfAbsent(index, k -> {
      BigInteger from = tailPower.multiply(base.pow(k));
      return fine.blockSum(from.add(offset),
          from.multiply(base).add(offset).subtract(BigInteger.ONE));
    });
    // the block sums which are negligible end the sum, else they decrease like j^-s, s > 1, or
    // alternate
    Apfloat s0 = tail.get(0);
    if (negligible(s0, head)) {
      boolean negligible = true;
      for (int k = 1; k < 3; k++) {
        negligible &= negligible(tail.get(k), head);
      }
      if (negligible) {
        return helper.add(head, helper.add(s0, helper.add(tail.get(1), tail.get(2))));
      }
    }
    // the block sums follow one smooth law in j, from j = 8 to beyond the tail and to at least
    // j = 130: no jump of a function of the number of digits of j, which the terms of the tail
    // wouldn't show
    int tailTerms = Math.max(order, (int) (LEVEL_TWO_DIGITS - beta)) + 1;
    int from = Math.max(8, firstDigits);
    int count = (int) beta + tailTerms - from;
    double[] logs = new double[count];
    int[] signs = new int[count];
    for (int i = 0; i < count; i++) {
      int j = from + i;
      Apfloat sj = j < (int) beta ? blockSums.get(j - firstDigits) : tail.get(j - (int) beta);
      signs[i] = sj.signum();
      if (signs[i] == 0) {
        throw DECLINE;
      }
      logs[i] = log(sj);
    }
    Classifier smoothness = new Classifier(from, logs, signs);
    if (!smoothness.smoothFrom(0, count)) {
      throw DECLINE;
    }
    if (signs[count - 1] == signs[count - 2]) {
      // block sums of one sign like j^-s, j the number of digits of the first tail block
      int last = (int) beta + order - from;
      double s = (logs[last - order] - logs[last]) / Math.log((beta + order) / beta);
      if (s <= 1.05) {
        throw DECLINE;
      }
    }
    return levinTail(tail, beta, head);
  }

  /** Test if <code>t</code> is below the last working digit of <code>sum</code>. */
  private boolean negligible(Apfloat t, Apfloat sum) {
    if (t.signum() == 0) {
      return true;
    }
    if (sum.signum() == 0) {
      return false;
    }
    return t.scale() < sum.scale() - work - 3L;
  }

  /** The sum of the terms with indices <code>from &lt;= n &lt;= to</code>, smooth in between. */
  private Apfloat blockSum(BigInteger from, BigInteger to) {
    Apfloat sum = Apfloat.ZERO;
    BigInteger n = from;
    BigInteger direct = from.max(GREGORY_START);
    if (to.subtract(from).compareTo(BigInteger.valueOf(DIRECT_MAX)) < 0) {
      direct = to.add(BigInteger.ONE);
    }
    int count = 0;
    for (; n.compareTo(direct) < 0 && n.compareTo(to) <= 0; n = n.add(BigInteger.ONE)) {
      if ((++count & 63) == 0) {
        engine.checkTimeLimit();
      }
      sum = helper.add(sum, term(n));
    }
    if (n.compareTo(to) > 0) {
      return sum;
    }
    if (to.subtract(n).compareTo(BigInteger.valueOf(2L * MAX_DIFFERENCES + 2L)) < 0) {
      for (; n.compareTo(to) <= 0; n = n.add(BigInteger.ONE)) {
        sum = helper.add(sum, term(n));
      }
      return sum;
    }
    Apfloat gregory = gregory(n, to);
    cache.clear();
    return helper.add(sum, gregory);
  }

  /**
   * The Gregory form of the Euler-Maclaurin formula
   * <code>Sum(f(n), {n, a, z}) = Integrate(f, {x, a, z}) + (f(a) + f(z))/2 + Sum(c(k) (Nabla^k f(z) + (-1)^k Delta^k f(a)), {k, 1, ...})</code>,
   * with the integral of the interpolant of the terms. The terms between <code>a</code> and
   * <code>z</code> vary on the scale of <code>n</code>, <code>a &gt;= 1000</code>.
   */
  private Apfloat gregory(BigInteger a, BigInteger z) {
    // the differences Delta^k f(a) ~ f(a) k!/a^k
    double logA = log(new Apfloat(a, 30)) / Math.log(10.0);
    int differences = 1;
    double logFactorial = 0.0;
    while (differences < MAX_DIFFERENCES) {
      logFactorial += Math.log10(differences);
      if (logFactorial - differences * logA < -(work + 2.0)) {
        break;
      }
      differences++;
    }
    Apfloat ends = helper.divide(helper.add(term(a), term(z)), new Apfloat(2, precision));
    Apfloat scale = ApfloatMath.abs(ends);
    Apfloat[] forward = new Apfloat[differences + 1];
    Apfloat[] backward = new Apfloat[differences + 1];
    for (int i = 0; i <= differences; i++) {
      forward[i] = term(a.add(BigInteger.valueOf(i)));
      backward[i] = term(z.subtract(BigInteger.valueOf(i)));
    }
    Apfloat correction = Apfloat.ZERO;
    Apfloat last = Apfloat.ZERO;
    for (int k = 1; k <= differences; k++) {
      for (int i = 0; i <= differences - k; i++) {
        forward[i] = helper.subtract(forward[i + 1], forward[i]);
        backward[i] = helper.subtract(backward[i], backward[i + 1]);
      }
      Apfloat delta = (k & 1) == 0 ? forward[0] : helper.negate(forward[0]);
      Apfloat c = helper.divide(new Apfloat(GREGORY[k].getNumerator(), precision),
          new Apfloat(GREGORY[k].getDenominator(), precision));
      last = helper.multiply(c, helper.add(backward[0], delta));
      correction = helper.add(correction, last);
    }
    Apfloat integral = integral(a, z);
    Apfloat sum = helper.add(integral, helper.add(ends, correction));
    scale = scale.compareTo(ApfloatMath.abs(sum)) > 0 ? scale : ApfloatMath.abs(sum);
    if (last.signum() != 0 && last.scale() >= scale.scale() - work - 2L) {
      throw DECLINE;
    }
    return sum;
  }

  /**
   * The integral of the interpolant of the terms from <code>a</code> to <code>z</code>, by
   * Gauss-Legendre quadrature in <code>Log(x)</code> on panels of width at most <code>1</code>.
   */
  private Apfloat integral(BigInteger a, BigInteger z) {
    // x = a Exp(s), 0 <= s <= w = Log(z/a): the blocks of the digit lengths have nearly one width
    // w0, and the exponentials of the nodes are those of w0 times Exp(tau (w - w0)), tau the
    // fraction of the width at the node
    Apfloat start = new Apfloat(a, precision);
    Apfloat ratio = helper.divide(new Apfloat(z, precision), start);
    Apfloat shift = Apfloat.ZERO;
    if (referenceRatio != null) {
      Apfloat epsilon = helper.subtract(helper.divide(ratio, referenceRatio), Apfloat.ONE);
      if (epsilon.signum() == 0 || epsilon.scale() < -2L) {
        shift = log1p(epsilon);
      } else {
        referenceRatio = null;
      }
    }
    if (referenceRatio == null) {
      referenceRatio = ratio;
      referenceWidth = ApfloatMath.log(ratio);
      exponentials.clear();
    }
    int panels = Math.max(1, (int) Math.ceil(referenceWidth.doubleValue()));
    Apfloat width = helper.add(referenceWidth, shift);
    // the stencil of the interpolation: N!/a^N below the digits
    double log10A = log(new Apfloat(a, 30)) / Math.log(10.0);
    int stencil = 4;
    double logFactorial = Math.log10(24.0);
    while (stencil < 80 && logFactorial - stencil * log10A > -(work + 2.0)) {
      stencil++;
      logFactorial += Math.log10(stencil);
    }
    Apfloat integral = Apfloat.ZERO;
    for (int p = 0; p < panels; p++) {
      engine.checkTimeLimit();
      integral = helper.add(integral, panel(p, panels, width, shift, start, a, z, stencil));
    }
    return integral;
  }

  /** <code>Log(1 + e)</code> for a small <code>e</code>, by its power series. */
  private Apfloat log1p(Apfloat e) {
    Apfloat sum = Apfloat.ZERO;
    Apfloat power = e;
    for (int k = 1; k < 10000; k++) {
      Apfloat term = helper.divide(power, new Apfloat(k, precision));
      sum = (k & 1) == 1 ? helper.add(sum, term) : helper.subtract(sum, term);
      if (term.signum() == 0 || sum.signum() != 0 && term.scale() < sum.scale() - precision) {
        break;
      }
      power = helper.multiply(power, e);
    }
    return sum;
  }

  /** <code>Exp(y)</code> for a small <code>y</code>, by its power series. */
  private Apfloat expSmall(Apfloat y) {
    Apfloat sum = new Apfloat(1, precision);
    Apfloat term = sum;
    for (int k = 1; k < 10000; k++) {
      term = helper.divide(helper.multiply(term, y), new Apfloat(k, precision));
      sum = helper.add(sum, term);
      if (term.signum() == 0 || term.scale() < -precision) {
        break;
      }
    }
    return sum;
  }

  /** The integral on one panel: Gauss-Legendre rules of 8, 16, ... nodes until two agree. */
  private Apfloat panel(int p, int panels, Apfloat width, Apfloat shift, Apfloat start,
      BigInteger a, BigInteger z, int stencil) {
    Apfloat previous = null;
    for (int m = 8; m <= MAX_NODES; m *= 2) {
      Apfloat value = gaussLegendre(p, panels, width, shift, m, start, a, z, stencil);
      if (previous != null) {
        Apfloat difference = ApfloatMath.abs(helper.subtract(value, previous));
        if (difference.signum() == 0 || value.signum() != 0
            && difference.scale() < value.scale() - work - 2L) {
          return value;
        }
      }
      previous = value;
    }
    throw DECLINE;
  }

  private Apfloat gaussLegendre(int p, int panels, Apfloat width, Apfloat shift, int m,
      Apfloat start, BigInteger a, BigInteger z, int stencil) {
    Apfloat[][] rule = nodes.computeIfAbsent(m, this::legendreRule);
    // the node of the panel p at the fraction tau = (p + (1 + xi)/2)/panels of the width
    Apfloat[][] reference = exponentials.computeIfAbsent(p * (2 * MAX_NODES) + m, key -> {
      Apfloat[] tau = new Apfloat[m];
      Apfloat[] e = new Apfloat[m];
      for (int i = 0; i < m; i++) {
        tau[i] = helper.divide(
            helper.add(new Apfloat(p, precision),
                helper.divide(helper.add(Apfloat.ONE, rule[0][i]), new Apfloat(2, precision))),
            new Apfloat(panels, precision));
        e[i] = ApfloatMath.exp(helper.multiply(tau[i], referenceWidth));
      }
      return new Apfloat[][] {tau, e};
    });
    Apfloat half = helper.divide(width, new Apfloat(2L * panels, precision));
    Apfloat sum = Apfloat.ZERO;
    for (int i = 0; i < m; i++) {
      Apfloat factor = shift.signum() == 0 ? reference[1][i]
          : helper.multiply(reference[1][i], expSmall(helper.multiply(reference[0][i], shift)));
      Apfloat x = helper.multiply(start, factor);
      sum = helper.add(sum, helper.multiply(rule[1][i], interpolant(x, a, z, stencil)));
    }
    return helper.multiply(half, sum);
  }

  /**
   * <code>x f(x)</code> with <code>f</code> the Lagrange interpolant of the terms at the
   * <code>stencil</code> integers next to <code>x</code> in <code>[a, z]</code>.
   */
  private Apfloat interpolant(Apfloat x, BigInteger a, BigInteger z, int stencil) {
    if (x.scale() > precision + 2L) {
      // moving the node to an integer changes the integral below the precision
      BigInteger n = x.floor().toBigInteger();
      if (n.compareTo(a) >= 0 && n.compareTo(z) <= 0) {
        return helper.multiply(term(n), x);
      }
    }
    org.apfloat.Apint floor = x.floor();
    BigInteger m0 = floor.toBigInteger();
    // the node m0 + t, t in [0, 1)
    Apfloat t = x.subtract(floor).precision(precision);
    BigInteger first = m0.subtract(BigInteger.valueOf(stencil / 2 - 1));
    if (first.compareTo(a) < 0) {
      first = a;
    }
    BigInteger lastIndex = first.add(BigInteger.valueOf(stencil - 1));
    if (lastIndex.compareTo(z) > 0) {
      first = z.subtract(BigInteger.valueOf(stencil - 1));
    }
    if (m0.compareTo(a) < 0) {
      m0 = a;
      t = Apfloat.ZERO;
    } else if (m0.compareTo(z) >= 0) {
      m0 = z;
      t = Apfloat.ZERO;
    }
    // offsets d_i = first + i - m0
    int shift = first.subtract(m0).intValueExact();
    Apfloat numerator = Apfloat.ZERO;
    Apfloat denominator = Apfloat.ZERO;
    Apfloat weight = new Apfloat(1, precision);
    Apfloat value = null;
    for (int i = 0; i < stencil; i++) {
      int d = shift + i;
      Apfloat sample = term(first.add(BigInteger.valueOf(i)));
      Apfloat distance = helper.subtract(t, new Apfloat(d, precision));
      if (distance.signum() == 0) {
        value = sample;
        break;
      }
      Apfloat w = helper.divide((i & 1) == 0 ? weight : helper.negate(weight), distance);
      numerator = helper.add(numerator, helper.multiply(w, sample));
      denominator = helper.add(denominator, w);
      // the binomial C(stencil - 1, i + 1)
      weight = helper.divide(helper.multiply(weight, new Apfloat(stencil - 1 - i, precision)),
          new Apfloat(i + 1, precision));
    }
    if (value == null) {
      value = helper.divide(numerator, denominator);
    }
    return helper.multiply(value, helper.add(new Apfloat(m0, precision), t));
  }

  /** The nodes and weights of the Gauss-Legendre rule with <code>m</code> nodes on [-1, 1]. */
  private Apfloat[][] legendreRule(int m) {
    Apfloat[] x = new Apfloat[m];
    Apfloat[] w = new Apfloat[m];
    Apfloat one = new Apfloat(1, precision);
    Apfloat two = new Apfloat(2, precision);
    for (int i = 0; i < (m + 1) / 2; i++) {
      Apfloat z = new Apfloat(Math.cos(Math.PI * (i + 0.75) / (m + 0.5)), precision);
      Apfloat derivative = one;
      for (int iteration = 0; iteration < 100; iteration++) {
        // P_m(z) and P_m'(z) by the three-term recurrence
        Apfloat p0 = one;
        Apfloat p1 = z;
        for (int k = 2; k <= m; k++) {
          Apfloat p2 = helper.divide(
              helper.subtract(helper.multiply(new Apfloat(2 * k - 1, precision),
                  helper.multiply(z, p1)), helper.multiply(new Apfloat(k - 1, precision), p0)),
              new Apfloat(k, precision));
          p0 = p1;
          p1 = p2;
        }
        derivative = helper.divide(
            helper.multiply(new Apfloat(m, precision), helper.subtract(helper.multiply(z, p1), p0)),
            helper.subtract(helper.multiply(z, z), one));
        Apfloat delta = helper.divide(p1, derivative);
        z = helper.subtract(z, delta);
        if (delta.signum() == 0 || delta.scale() < -precision + 2L) {
          break;
        }
      }
      Apfloat weight = helper.divide(two,
          helper.multiply(helper.subtract(one, helper.multiply(z, z)),
              helper.multiply(derivative, derivative)));
      x[i] = helper.negate(z);
      x[m - 1 - i] = z;
      w[i] = weight;
      w[m - 1 - i] = weight;
    }
    return new Apfloat[][] {x, w};
  }

  /**
   * The coefficients <code>c(k) = |G(k+1)|</code> of the Gregory formula, from the Gregory
   * coefficients <code>t/Log(1+t) = Sum(G(n) t^n)</code>.
   */
  private static BigFraction[] gregoryCoefficients(int n) {
    BigFraction[] g = new BigFraction[n + 2];
    g[0] = BigFraction.ONE;
    for (int i = 1; i <= n + 1; i++) {
      BigFraction s = BigFraction.ZERO;
      for (int k = 0; k < i; k++) {
        BigFraction term = g[k].divide(i - k + 1);
        s = ((i - k) & 1) == 0 ? s.add(term) : s.subtract(term);
      }
      g[i] = s.negate();
    }
    BigFraction[] c = new BigFraction[n + 1];
    c[0] = BigFraction.ZERO;
    for (int k = 1; k <= n; k++) {
      c[k] = g[k + 1].abs();
    }
    return c;
  }
}
