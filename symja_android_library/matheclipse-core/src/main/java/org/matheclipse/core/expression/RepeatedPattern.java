package org.matheclipse.core.expression;

import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
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
    p.fMatcher = engine.evalPatternMatcher(patternExpr);
    p.fMin = min;
    p.fMax = max;
    return p;
  }

  protected IExpr fRepeatedExpr;

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
    if (size < fMin || size > fMax) {
      return false;
    }
    EvalEngine engine = EvalEngine.get();
    for (int i = 1; i < sequence.size(); i++) {
      if (!fMatcher.testBlank(sequence.get(i), engine)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
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
    result[0] = IAST.CONTAINS_PATTERN_SEQUENCE;
    result[1] = 1;
    return result;
  }

}
