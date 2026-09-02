package org.matheclipse.core.patternmatching.ruleindex;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * A prefilter index over the pattern down-rules of a single symbol.
 *
 * <p>
 * Every rule is described by the set of symbols which each expression matching its left-hand-side
 * has to contain (see {@link RuleFeatureAnalyzer}). For an expression to be rewritten the index
 * determines the symbols actually contained in it and excludes all rules which require a symbol
 * that is missing. The remaining rules are reported in <b>ascending rule order</b>, so the caller
 * can confirm them with the ordinary pattern matchers and keep the "first matching rule wins"
 * semantics of a linear scan.
 *
 * <p>
 * The index is built from
 *
 * <pre>
 * requiredBy[feature] = bit set of all rules which require this symbol
 * </pre>
 *
 * and a query is a sequence of <code>candidates &amp;= ~requiredBy[absentFeature]</code>
 * operations. The bit sets are processed one 64 bit word at a time by {@link Cursor}, so a rule
 * that matches early stops the computation and nothing is allocated for the rules behind it.
 *
 * <p>
 * Features are sorted by descending number of rules requiring them, which lets the inner loop of
 * {@link Cursor#next()} leave as soon as a word has no candidates left.
 *
 * <p>
 * This class is immutable and can be shared by all threads. It has to be discarded whenever the
 * underlying rule list changes.
 */
public final class RuleFeatureIndex {

  /**
   * Rule list size from which an index is worthwhile. Used to classify a dispatch for
   * {@link RuleDispatchStats}; the threshold actually applied is
   * {@link org.matheclipse.core.basic.Config#RULE_INDEX_MIN_RULES}, which benchmarks change.
   */
  public static final int INDEXABLE_RULES = 16;

  /**
   * Expressions deeper or larger than this are not analyzed; the index then reports every rule as a
   * candidate, which is exactly the behaviour of a linear scan.
   */
  private static final int MAX_SUBJECT_DEPTH = 64;

  private static final int MAX_SUBJECT_NODES = 20000;

  private final int ruleCount;

  private final int words;

  private final long lastWordMask;

  /** Number of indexed symbols. */
  private final int featureCount;

  /** <code>requiredBy[feature][word]</code> - the rules which require the symbol. */
  private final long[][] requiredBy;

  /** Maps the ordinal of a built-in symbol to its feature index, or <code>-1</code>. */
  private final int[] builtinFeature;

  /** Feature index of the symbols which are not built-in, for example the Rubi inert trig heads. */
  private final Map<ISymbol, Integer> otherFeature;

  /**
   * Largest number of {@link org.matheclipse.core.expression.S#Times} factors which is indexed
   * separately; an expression with more factors uses the last bucket, which is a superset.
   */
  private static final int FACTOR_BUCKETS = 8;

  /**
   * <code>factorBucket[n]</code> holds the rules whose first argument can match a product of
   * <code>n + 1</code> factors, or <code>null</code> if the bound is useless for this rule set.
   *
   * @see RuleFeatureAnalyzer#maxTopLevelFactors(IExpr)
   */
  private final long[][] factorBucket;

  private RuleFeatureIndex(int ruleCount, int featureCount, long[][] requiredBy,
      int[] builtinFeature, Map<ISymbol, Integer> otherFeature, long[][] factorBucket) {
    this.ruleCount = ruleCount;
    this.words = (ruleCount + 63) >> 6;
    int rest = ruleCount & 63;
    this.lastWordMask = rest == 0 ? -1L : (1L << rest) - 1L;
    this.featureCount = featureCount;
    this.requiredBy = requiredBy;
    this.builtinFeature = builtinFeature;
    this.otherFeature = otherFeature;
    this.factorBucket = factorBucket;
  }

  /**
   * Build an index for the given rules.
   *
   * @param rules the pattern down-rules in evaluation order
   * @return the index, or <code>null</code> if no rule requires any symbol and an index would not
   *         exclude anything
   */
  public static RuleFeatureIndex build(List<IPatternMatcher> rules) {
    final int ruleCount = rules.size();
    if (ruleCount == 0) {
      return null;
    }
    List<Set<ISymbol>> required = new ArrayList<Set<ISymbol>>(ruleCount);
    Map<ISymbol, int[]> counter = new IdentityHashMap<ISymbol, int[]>();
    int[] maxFactors = new int[ruleCount];
    boolean anyFactorBound = false;
    for (int i = 0; i < ruleCount; i++) {
      IPatternMatcher matcher = rules.get(i);
      IExpr lhs = matcher == null ? null : matcher.getLHS();
      Set<ISymbol> symbols =
          lhs == null ? new HashSet<ISymbol>() : RuleFeatureAnalyzer.requiredSymbols(lhs);
      required.add(symbols);
      for (ISymbol symbol : symbols) {
        int[] count = counter.get(symbol);
        if (count == null) {
          counter.put(symbol, new int[] {1});
        } else {
          count[0]++;
        }
      }
      maxFactors[i] = firstArgumentFactorBound(lhs);
      if (maxFactors[i] != RuleFeatureAnalyzer.UNBOUNDED_FACTORS) {
        anyFactorBound = true;
      }
    }
    if (counter.isEmpty() && !anyFactorBound) {
      return null;
    }

    long[][] factorBucket = null;
    if (anyFactorBound) {
      final int words = (ruleCount + 63) >> 6;
      factorBucket = new long[FACTOR_BUCKETS][];
      for (int bucket = 0; bucket < FACTOR_BUCKETS; bucket++) {
        long[] bits = new long[words];
        final int factors = bucket + 1;
        for (int i = 0; i < ruleCount; i++) {
          // the last bucket collects everything with a bound of at least FACTOR_BUCKETS, which is a
          // superset for expressions with even more factors
          if (maxFactors[i] >= factors) {
            bits[i >> 6] |= 1L << (i & 63);
          }
        }
        factorBucket[bucket] = bits;
      }
    }

    // sort the symbols by descending number of rules requiring them, so that the most selective
    // exclusions are applied first
    List<ISymbol> ordered = new ArrayList<ISymbol>(counter.keySet());
    ordered.sort((a, b) -> {
      int diff = counter.get(b)[0] - counter.get(a)[0];
      return diff != 0 ? diff : a.compareTo(b);
    });

    final int featureCount = ordered.size();
    Map<ISymbol, Integer> featureOf = new IdentityHashMap<ISymbol, Integer>(featureCount * 2);
    int maxOrdinal = -1;
    for (int f = 0; f < featureCount; f++) {
      ISymbol symbol = ordered.get(f);
      featureOf.put(symbol, f);
      int ordinal = symbol.ordinal();
      if (ordinal > maxOrdinal) {
        maxOrdinal = ordinal;
      }
    }
    int[] builtinFeature = new int[maxOrdinal + 1];
    for (int i = 0; i < builtinFeature.length; i++) {
      builtinFeature[i] = -1;
    }
    Map<ISymbol, Integer> otherFeature = new IdentityHashMap<ISymbol, Integer>();
    for (Map.Entry<ISymbol, Integer> entry : featureOf.entrySet()) {
      int ordinal = entry.getKey().ordinal();
      if (ordinal >= 0) {
        builtinFeature[ordinal] = entry.getValue().intValue();
      } else {
        otherFeature.put(entry.getKey(), entry.getValue());
      }
    }

    final int words = (ruleCount + 63) >> 6;
    long[][] requiredBy = new long[featureCount][];
    for (int f = 0; f < featureCount; f++) {
      requiredBy[f] = new long[words];
    }
    for (int i = 0; i < ruleCount; i++) {
      for (ISymbol symbol : required.get(i)) {
        int f = featureOf.get(symbol).intValue();
        requiredBy[f][i >> 6] |= 1L << (i & 63);
      }
    }
    return new RuleFeatureIndex(ruleCount, featureCount, requiredBy, builtinFeature, otherFeature,
        factorBucket);
  }

  /**
   * Bound for the number of top level factors the first argument of a rule can match.
   *
   * <p>
   * The bound is only usable if the argument positions of the rule and of the expression line up,
   * which they do not for an {@link ISymbol#ORDERLESS} or {@link ISymbol#FLAT} head.
   */
  private static int firstArgumentFactorBound(IExpr lhs) {
    if (lhs == null || !lhs.isAST()) {
      return RuleFeatureAnalyzer.UNBOUNDED_FACTORS;
    }
    IAST ast = (IAST) lhs;
    if (ast.size() < 2 || !ast.head().isSymbol()) {
      return RuleFeatureAnalyzer.UNBOUNDED_FACTORS;
    }
    ISymbol head = (ISymbol) ast.head();
    if (head.hasOrderlessAttribute() || head.hasFlatAttribute()) {
      return RuleFeatureAnalyzer.UNBOUNDED_FACTORS;
    }
    return RuleFeatureAnalyzer.maxTopLevelFactors(ast.arg1());
  }

  /**
   * The number of factors of the expression's first argument, clamped to the indexed buckets.
   *
   * <p>
   * Bucket <code>b</code> holds the rules whose first argument can match a product of
   * <code>b + 1</code> factors, so the index of a subject is one less than its factor count. An
   * empty product has none, which no bucket describes: <code>Sum(Times(), 2)</code> arrives here
   * with a first argument of <code>Times()</code>, and the arithmetic answered <code>-1</code>,
   * which the caller then read as an array index.
   *
   * @return the bucket index, or <code>-1</code> when no bucket describes {@code subject} and the
   *         filter has to be skipped
   */
  private static int factorBucketOf(IExpr subject) {
    if (!subject.isAST() || subject.size() < 2) {
      return 0;
    }
    int factors = RuleFeatureAnalyzer.topLevelFactors(((IAST) subject).arg1());
    if (factors < 1) {
      return -1;
    }
    return Math.min(factors, FACTOR_BUCKETS) - 1;
  }

  public int ruleCount() {
    return ruleCount;
  }

  public int featureCount() {
    return featureCount;
  }

  /**
   * Create a cursor over the rules which can possibly match <code>subject</code>. The cursor
   * reports the rule indices in ascending order.
   *
   * @param subject the expression which should be rewritten
   */
  public Cursor cursor(IExpr subject) {
    return new Cursor(subject);
  }

  /**
   * Determine all candidate rules for <code>subject</code> at once. Used for validation and
   * diagnostics, the evaluation itself uses {@link #cursor(IExpr)}.
   */
  public BitSet candidates(IExpr subject) {
    BitSet result = new BitSet(ruleCount);
    Cursor cursor = new Cursor(subject);
    int index;
    while ((index = cursor.next()) >= 0) {
      result.set(index);
    }
    return result;
  }

  /**
   * Iterates the rule indices which are not excluded by the index, in ascending order.
   *
   * <p>
   * Not thread safe and not reusable; evaluation is re-entrant (a rule's right-hand-side can
   * trigger the same rules again), so every dispatch creates its own cursor.
   */
  public final class Cursor {

    private final int[] absent;

    private int absentCount;

    /** Rules whose factor bound admits this expression, or <code>null</code> for no restriction. */
    private final long[] factorMask;

    private int nextWord;

    private int word;

    private long currentBits;

    private Cursor(IExpr subject) {
      long[] present = new long[(featureCount + 63) >> 6];
      boolean complete = markPresent(subject, present, new int[] {MAX_SUBJECT_NODES}, 0);
      this.absent = new int[featureCount];
      if (complete) {
        int count = 0;
        for (int f = 0; f < featureCount; f++) {
          if ((present[f >> 6] & (1L << (f & 63))) == 0L) {
            absent[count++] = f;
          }
        }
        this.absentCount = count;
        // a null mask means every rule stays a candidate. This is a prefilter, so declining to
        // narrow can only cost a little time, never a match.
        int bucket = factorBucket == null ? -1 : factorBucketOf(subject);
        this.factorMask = bucket < 0 ? null : factorBucket[bucket];
      } else {
        // the expression could not be scanned completely - report every rule as a candidate
        this.absentCount = 0;
        this.factorMask = null;
      }
    }

    /**
     * @return the next candidate rule index or <code>-1</code> if there is none left
     */
    public int next() {
      while (true) {
        if (currentBits != 0L) {
          int bit = Long.numberOfTrailingZeros(currentBits);
          currentBits &= currentBits - 1;
          return (word << 6) + bit;
        }
        if (nextWord >= words) {
          return -1;
        }
        int w = nextWord++;
        long candidates = w == words - 1 ? lastWordMask : -1L;
        if (factorMask != null) {
          candidates &= factorMask[w];
        }
        final int count = absentCount;
        for (int i = 0; i < count && candidates != 0L; i++) {
          candidates &= ~requiredBy[absent[i]][w];
        }
        word = w;
        currentBits = candidates;
      }
    }
  }

  /**
   * Mark the features of all symbols occurring in <code>expr</code>.
   *
   * @param budget remaining number of nodes which may be visited
   * @return <code>false</code> if the expression was too deep or too large to be scanned
   *         completely; the caller must then not exclude any rule
   */
  private boolean markPresent(IExpr expr, long[] present, int[] budget, int depth) {
    if (depth > MAX_SUBJECT_DEPTH || --budget[0] < 0) {
      return false;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isSymbol()) {
        mark((ISymbol) head, present);
      } else if (!markPresent(head, present, budget, depth + 1)) {
        return false;
      }
      for (int i = 1; i < ast.size(); i++) {
        if (!markPresent(ast.get(i), present, budget, depth + 1)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isSymbol()) {
      mark((ISymbol) expr, present);
      return true;
    }
    IExpr head = expr.head();
    if (head.isSymbol()) {
      mark((ISymbol) head, present);
    }
    return true;
  }

  private void mark(ISymbol symbol, long[] present) {
    int feature = -1;
    int ordinal = symbol.ordinal();
    if (ordinal >= 0) {
      if (ordinal < builtinFeature.length) {
        feature = builtinFeature[ordinal];
      }
    } else if (!otherFeature.isEmpty()) {
      Integer boxed = otherFeature.get(symbol);
      if (boxed != null) {
        feature = boxed.intValue();
      }
    }
    if (feature >= 0) {
      present[feature >> 6] |= 1L << (feature & 63);
    }
  }
}
