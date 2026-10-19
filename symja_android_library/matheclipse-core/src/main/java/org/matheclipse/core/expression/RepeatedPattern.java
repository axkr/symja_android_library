package org.matheclipse.core.expression;

import java.util.List;
import org.matheclipse.core.basic.RuleConfig;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.EvalFlags;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Represents a <b>Repeated Pattern</b> ({@code p..} or {@code p...}) which matches a sequence of
 * expressions defined by a sub-pattern.
 * <p>
 * While standard sequence patterns like {@code x__} ({@link PatternSequence}) match <i>any</i>
 * sequence of expressions, a {@code RepeatedPattern} enforces that <b>every individual element</b>
 * in the sequence must match the given pattern {@code p}. This corresponds to the constructs
 * {@link S#Repeated} and {@link S#RepeatedNull}.
 * </p>
 *
 * <h3>1. Syntax and Forms</h3>
 * <ul>
 * <li><b>{@code p..} (Repeated):</b> Matches <b>1 or more</b> arguments where each argument matches
 * {@code p}. <br>
 * <i>Example:</i> {@code _Integer..} matches {@code 1, 2, 3} but not {@code 1, 2.5, 3}.</li>
 * <li><b>{@code p...} (RepeatedNull):</b> Matches <b>0 or more</b> arguments where each argument
 * matches {@code p}. <br>
 * <i>Example:</i> {@code a...} matches an empty sequence or {@code a, a, a}.</li>
 * </ul>
 *
 * <h3>2. Constraint Checking</h3>
 * <p>
 * The matcher iterates through the target sequence and verifies that {@code p} matches every single
 * item. Unlike {@code PatternSequence} which often checks a shared "Head Test",
 * {@code RepeatedPattern} performs a full pattern match (using
 * {@link org.matheclipse.core.patternmatching.IPatternMatcher}) on each element.
 * </p>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Type-Safe Sequences</h4>
 * 
 * <pre>
 * // Define f to accept only sequences of Integers
 * // f[n: _Integer..] := Total[{n}]
 *
 * ISymbol f = F.Dummy("f");
 * ISymbol n = F.Dummy("n");
 *
 * // Create pattern: _Integer..
 * IPatternObject repeatedInts = RepeatedPattern.valueOf(F.Blank(S.Integer), engine);
 *
 * // Create nested pattern: n : (_Integer..)
 * IPatternObject nested = PatternNested.valueOf(n, repeatedInts);
 *
 * // f[1, 2, 3] -> Match. n bound to {1, 2, 3}
 * // f[1, x] -> No Match.
 * </pre>
 *
 * <h4>Specific Value Repetition</h4>
 * 
 * <pre>
 * // Match a sequence of zeros
 * // 0...
 * IPatternObject zeros = RepeatedPattern.valueOf(F.C0, 0, Integer.MAX_VALUE, true, engine);
 * </pre>
 *
 * @see org.matheclipse.core.expression.PatternSequence
 * @see org.matheclipse.core.expression.AbstractPatternSequence
 */
public class RepeatedPattern extends AbstractPatternSequence {

  private static final long serialVersionUID = 1086461999754718513L;

  public static RepeatedPattern valueOf(IExpr patternExpr, EvalEngine engine) {
    return valueOf(patternExpr, 1, Integer.MAX_VALUE, false, engine);
  }

  public static RepeatedPattern valueOf(IExpr patternExpr, int min, int max,
      boolean zeroArgsAllowed, EvalEngine engine) {
    RepeatedPattern p = new RepeatedPattern();
    p.fSymbol = null;
    p.fDefault = false;
    p.fZeroArgsAllowed = zeroArgsAllowed;
    p.fRepeatedExpr = patternExpr;
    // a unit of several arguments is matched as List(p) in matchUnits()
    p.fMatcher = spansSequence(patternExpr) ? null : engine.evalPatternMatcher(patternExpr);
    p.fMin = min;
    p.fMax = max;
    return p;
  }

  /**
   * The same repetition with a name, as <code>b:p..</code>, which stands for the whole matched
   * sequence.
   */
  public RepeatedPattern named(ISymbol symbol) {
    RepeatedPattern p = new RepeatedPattern();
    p.fSymbol = symbol;
    p.fDefault = fDefault;
    p.fZeroArgsAllowed = fZeroArgsAllowed;
    p.fRepeatedExpr = fRepeatedExpr;
    p.fMatcher = fMatcher;
    p.fMin = fMin;
    p.fMax = fMax;
    return p;
  }

  protected IExpr fRepeatedExpr;

  /** How few times the pattern may repeat, and how many. */
  public int getMin() {
    return fMin;
  }

  public int getMax() {
    return fMax;
  }

  protected int fMin;

  protected int fMax;

  protected IPatternMatcher fMatcher;

  protected RepeatedPattern() {
    super();
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof RepeatedPattern) {
      int cp = fRepeatedExpr.compareTo(((RepeatedPattern) expr).fRepeatedExpr);
      if (cp != 0) {
        return cp;
      }
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RepeatedPattern) {
      RepeatedPattern pattern = (RepeatedPattern) obj;
      if (fSymbol == null) {
        if (pattern.fSymbol == null) {
          if (fDefault == pattern.fDefault && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
            return fRepeatedExpr.equals(pattern.fRepeatedExpr);
          }
        }
        return false;
      }
      if (fSymbol.equals(pattern.fSymbol) && fDefault == pattern.fDefault
          && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
        return fRepeatedExpr.equals(pattern.fRepeatedExpr);
      }
    }
    return false;
  }

  @Override
  public String fullFormString() {
    if (fSymbol != null) {
      // "b:p.." is Pattern(b, Repeated(p))
      return toFullFormAST().fullFormString();
    }
    StringBuilder buf = new StringBuilder();
    if (fZeroArgsAllowed) {
      buf.append("RepeatedNull");
    } else {
      buf.append("Repeated");
    }
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    buf.append(fRepeatedExpr.fullFormString());
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    return buf.toString();
  }

  public IExpr getRepeatedExpr() {
    return fRepeatedExpr;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 213 : 37 + fSymbol.hashCode();
  }

  @Override
  public boolean isRepeated() {
    return true;
  }

  @Override
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead) {
    final int size = sequence.argSize();
    final boolean units = spansSequence(fRepeatedExpr);
    if (units ? size == 0 && fMin > 0 : size < fMin || size > fMax) {
      return false;
    }
    if (fSymbol != null) {
      IExpr value = patternMap.getValue(this);
      if (value != null) {
        return sequence.equals(value);
      }
    }
    EvalEngine engine = EvalEngine.get();
    if (units) {
      if (!matchUnits(sequence, engine)) {
        return false;
      }
    } else {
      for (int i = 1; i < sequence.size(); i++) {
        if (!fMatcher.testBlank(sequence.get(i), engine)) {
          return false;
        }
      }
    }
    if (fSymbol != null) {
      return patternMap.setValue(this, sequence);
    }
    return true;
  }

  /**
   * The evaluated patterns <code>List(p)</code> of a unit <code>p</code> of several arguments: one
   * for each branch of an alternative <code>p1|p2</code>, which a run has to match one of. A
   * matcher holds the values of its pattern variables, so every match builds its own from these.
   */
  private transient IExpr[] fUnitPatterns;

  /**
   * The number of arguments each branch of the unit stands for, or <code>null</code> if a branch
   * can stand for a varying number.
   */
  private transient int[] fUnitWidths;

  /** The matchers of the unit branches for one call of {@link #matchUnits}. */
  private IPatternMatcher[] unitMatchers(EvalEngine engine) {
    if (fUnitPatterns == null) {
      java.util.List<IExpr> branches = new java.util.ArrayList<IExpr>();
      addBranches(fRepeatedExpr, branches);
      IExpr[] patterns = new IExpr[branches.size()];
      int[] widths = new int[branches.size()];
      boolean fixed = true;
      for (int i = 0; i < patterns.length; i++) {
        patterns[i] = engine.evalPattern(F.unaryAST1(S.List, branches.get(i)));
        widths[i] = fixedWidth(branches.get(i));
        fixed &= widths[i] > 0;
      }
      fUnitWidths = fixed ? widths : null;
      fUnitPatterns = patterns;
    }
    IPatternMatcher[] matchers = new IPatternMatcher[fUnitPatterns.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = new org.matheclipse.core.patternmatching.PatternMatcher(fUnitPatterns[i]);
    }
    return matchers;
  }

  /**
   * The number of arguments <code>branch</code> stands for: <code>k</code> for a
   * <code>PatternSequence</code> of <code>k</code> single arguments, <code>1</code> for a single
   * argument, <code>-1</code> if it varies.
   */
  private static int fixedWidth(IExpr branch) {
    if (!spansSequence(branch)) {
      return 1;
    }
    if (branch.isAST(S.PatternSequence)) {
      for (IExpr part : (IAST) branch) {
        if (spansSequence(part)) {
          return -1;
        }
      }
      return branch.argSize();
    }
    return -1;
  }

  private static boolean matchesUnit(IPatternMatcher[] matchers, IAST run, EvalEngine engine) {
    for (IPatternMatcher matcher : matchers) {
      if (matcher.test(run, engine)) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasWidth(int[] widths, int width) {
    for (int w : widths) {
      if (w == width) {
        return true;
      }
    }
    return false;
  }

  /** The branches of nested alternatives, or the unit itself. */
  private static void addBranches(IExpr unit, java.util.List<IExpr> branches) {
    if (unit.isAlternatives()) {
      for (IExpr branch : (IAST) unit) {
        addBranches(branch, branches);
      }
    } else {
      branches.add(unit);
    }
  }

  /**
   * Whether the unit <code>p</code> of <code>p..</code> may stand for several arguments, like
   * <code>PatternSequence(a, b)</code>, <code>x__</code> or an alternative of such units.
   */
  private static boolean spansSequence(IExpr unit) {
    if (unit instanceof IPatternSequence) {
      return true;
    }
    if (unit.isAST(S.PatternSequence)) {
      return unit.argSize() != 1 || spansSequence(unit.first());
    }
    if (unit.isAlternatives()) {
      return ((IAST) unit).exists(RepeatedPattern::spansSequence);
    }
    if (unit.isAST(S.Pattern, 3) || unit.isAST(S.PatternTest, 3) || unit.isAST(S.Condition, 3)) {
      return spansSequence(unit.isAST(S.Pattern, 3) ? unit.second() : unit.first());
    }
    if (unit instanceof PatternNested) {
      return spansSequence(((PatternNested) unit).getPatternExpr());
    }
    return false;
  }

  /**
   * Whether <code>sequence</code> splits into consecutive runs which each match the unit, with a
   * number of runs between the bounds. The runs are found from the left: <code>reach[i]</code> holds
   * the numbers of units which can cover the first <code>i</code> arguments.
   */
  private boolean matchUnits(IAST sequence, EvalEngine engine) {
    final int n = sequence.argSize();
    if (n == 0) {
      return fMin == 0;
    }
    final IPatternMatcher[] matchers = unitMatchers(engine);
    // with fixed widths only the runs of those lengths are tried, not every run
    final int[] widths = fUnitWidths;
    final int maxUnits = Math.min(fMax, n);
    final java.util.BitSet[] reach = new java.util.BitSet[n + 1];
    reach[0] = new java.util.BitSet();
    reach[0].set(0);
    for (int i = 0; i < n; i++) {
      if (reach[i] == null || reach[i].isEmpty()) {
        continue;
      }
      engine.checkTimeLimit();
      for (int j = i + 1; j <= n; j++) {
        if (widths != null && !hasWidth(widths, j - i)) {
          continue;
        }
        IASTAppendable run = F.ListAlloc(j - i);
        for (int k = i + 1; k <= j; k++) {
          run.append(sequence.get(k));
        }
        if (!matchesUnit(matchers, run, engine)) {
          continue;
        }
        java.util.BitSet next = reach[j] == null ? (reach[j] = new java.util.BitSet()) : reach[j];
        for (int c = reach[i].nextSetBit(0); c >= 0 && c < maxUnits; c = reach[i].nextSetBit(c + 1)) {
          next.set(c + 1);
        }
      }
    }
    return reach[n] != null && reach[n].nextSetBit(Math.max(fMin, 0)) >= 0
        && reach[n].nextSetBit(Math.max(fMin, 0)) <= maxUnits;
  }

  @Override
  public ISymbol head() {
    if (fSymbol != null) {
      return S.Pattern;
    }
    return fZeroArgsAllowed ? S.RepeatedNull : S.Repeated;
  }

  /** {@inheritDoc} */
  @Override
  public IAST toFullFormAST() {

    // "p.." is Repeated(p), "p..." is RepeatedNull(p)
    IAST repeated =
        F.unaryAST1(fZeroArgsAllowed ? S.RepeatedNull : S.Repeated, fRepeatedExpr);
    // "b:p.." is Pattern(b, Repeated(p))
    return fSymbol != null ? F.binaryAST2(S.Pattern, fSymbol, repeated) : repeated;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol != null) {
      buffer.append(fSymbol.toString());
      buffer.append(':');
    }
    buffer.append(fRepeatedExpr.toString());
    if (fZeroArgsAllowed) {
      buffer.append("...");
    } else {
      buffer.append("..");
    }
    return buffer.toString();
  }

  @Override
  public String toWolframString() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol != null) {
      buffer.append(WolframFormFactory.get().toString(fSymbol));
      buffer.append(':');
    }
    buffer.append(WolframFormFactory.get().toString(fRepeatedExpr));
    if (fZeroArgsAllowed) {
      buffer.append("...");
    } else {
      buffer.append("..");
    }
    return buffer.toString();
  }

  @Override
  public IExpr getHeadTest() {
    return null;
  }

  @Override
  public boolean isConditionMatchedSequence(IAST sequence, IPatternMap patternMap) {
    return patternMap.setValue(this, sequence);
  }

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    // the ast contains a pattern sequence (i.e. "x__")
    int[] result = new int[2];
    result[0] = EvalFlags.Mask.CONTAINS_PATTERN_SEQUENCE;
    result[1] = RuleConfig.PRIORITY_REPEATED_PATTERN;
    return result;
  }

}
