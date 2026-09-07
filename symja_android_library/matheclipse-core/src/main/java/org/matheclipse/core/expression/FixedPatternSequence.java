package org.matheclipse.core.expression;

import java.util.List;
import org.matheclipse.core.interfaces.EvalFlags;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.basic.RuleConfig;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.core.generic.GenericPair;

/**
 * The sequence pattern behind {@link S#PatternSequence} and {@link S#OrderlessPatternSequence}: a
 * sequence of a <b>fixed</b> number of arguments, each matching a sub-pattern of its own.
 *
 * <p>
 * Unlike {@code x__}, which matches any number of arguments, this consumes exactly as many as it
 * has sub-patterns. {@code OrderlessPatternSequence} matches the same arguments in any order.
 *
 * <p>
 * A sub-pattern which is an {@link IPatternObject} is matched with
 * {@link IPatternObject#matchPattern(IExpr, IPatternMap)}, so a name inside it binds as usual; one
 * which contains no pattern at all has to be equal to the argument. Anything else - a composite
 * pattern such as {@code f(x_)} - is <b>not</b> matched here and makes the whole sequence fail,
 * rather than matching on some weaker rule.
 */
public class FixedPatternSequence extends AbstractPatternSequence {

  private static final long serialVersionUID = 8613299214519854281L;

  /**
   * @param symbol the name the whole sequence binds to, or <code>null</code>
   * @param patterns the sub-patterns, one per argument consumed
   * @param orderless whether the arguments may appear in any order
   */
  public static FixedPatternSequence valueOf(final ISymbol symbol, final IAST patterns,
      final boolean orderless) {
    FixedPatternSequence p = new FixedPatternSequence();
    p.fSymbol = symbol;
    p.fDefault = false;
    p.fZeroArgsAllowed = patterns.argSize() == 0;
    p.fPatterns = patterns;
    p.fOrderless = orderless;
    return p;
  }

  /** The sub-patterns, as the arguments of the original {@code PatternSequence(...)}. */
  protected IAST fPatterns;

  protected boolean fOrderless;

  protected FixedPatternSequence() {
    super();
  }

  public IAST getPatterns() {
    return fPatterns;
  }

  /** There is no single head test: each sub-pattern carries its own. */
  @Override
  public IExpr getHeadTest() {
    return null;
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public ISymbol head() {
    return fOrderless ? S.OrderlessPatternSequence : S.PatternSequence;
  }

  @Override
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead) {
    if (sequence.argSize() != fPatterns.argSize()) {
      return false;
    }
    if (!(fOrderless ? matchesInAnyOrder(sequence, patternMap)
        : matchesInOrder(sequence, patternMap))) {
      return false;
    }
    if (fSymbol == null) {
      return true;
    }
    IExpr value = patternMap.getValue(this);
    if (value != null) {
      return sequence.equals(value);
    }
    return patternMap.setValue(this, sequence);
  }

  private boolean matchesInOrder(final IAST sequence, IPatternMap patternMap) {
    for (int i = 1; i < sequence.size(); i++) {
      if (!matchesElement(fPatterns.get(i), sequence.get(i), patternMap)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Try to pair every sub-pattern with a distinct argument, in any order. The sub-patterns may bind
   * names, so a failed pairing has to be undone before the next one is tried.
   */
  private boolean matchesInAnyOrder(final IAST sequence, IPatternMap patternMap) {
    return matchesInAnyOrder(sequence, patternMap, 1, new boolean[sequence.size()]);
  }

  private boolean matchesInAnyOrder(final IAST sequence, IPatternMap patternMap, int patternIndex,
      boolean[] used) {
    if (patternIndex >= fPatterns.size()) {
      return true;
    }
    IExpr pattern = fPatterns.get(patternIndex);
    for (int i = 1; i < sequence.size(); i++) {
      if (used[i]) {
        continue;
      }
      final IExpr[] saved = patternMap.copyPattern();
      used[i] = true;
      if (matchesElement(pattern, sequence.get(i), patternMap)
          && matchesInAnyOrder(sequence, patternMap, patternIndex + 1, used)) {
        return true;
      }
      used[i] = false;
      patternMap.resetPattern(saved);
    }
    return false;
  }

  private static boolean matchesElement(IExpr pattern, IExpr argument, IPatternMap patternMap) {
    if (pattern instanceof IPatternObject) {
      return ((IPatternObject) pattern).matchPattern(argument, patternMap);
    }
    if (pattern.isFreeOfPatterns()) {
      return pattern.equals(argument);
    }
    // a composite pattern is not matched here; failing is better than matching it loosely
    return false;
  }

  @Override
  public boolean isConditionMatchedSequence(IAST sequence, IPatternMap patternMap) {
    return matchPatternSequence(sequence, patternMap, S.Missing);
  }

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    // This is an atom, so the walk which builds the pattern map does not reach the sub-patterns.
    // They have to be registered here, or matching one of them would fail for want of a slot.
    for (int i = 1; i < fPatterns.size(); i++) {
      if (fPatterns.get(i) instanceof IPatternObject) {
        ((IPatternObject) fPatterns.get(i)).addPattern(patternIndexMap);
      }
    }
    int[] result = new int[2];
    result[0] = EvalFlags.Mask.CONTAINS_PATTERN_SEQUENCE;
    result[1] = RuleConfig.PRIORITY_REPEATED_PATTERN;
    return result;
  }

  @Override
  public IAST toFullFormAST() {
    IASTAppendable result = F.ast(head(), fPatterns.argSize());
    result.appendArgs(fPatterns);
    return result;
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder(head().toString());
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    for (int i = 1; i < fPatterns.size(); i++) {
      buf.append(fPatterns.get(i).fullFormString());
      if (i < fPatterns.argSize()) {
        buf.append(", ");
      }
    }
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    return buf.toString();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (fSymbol != null) {
      buf.append(fSymbol.toString());
      buf.append(':');
    }
    buf.append(fullFormString());
    return buf.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FixedPatternSequence) {
      FixedPatternSequence other = (FixedPatternSequence) obj;
      if (fOrderless != other.fOrderless || fDefault != other.fDefault) {
        return false;
      }
      if (fSymbol == null ? other.fSymbol != null : !fSymbol.equals(other.fSymbol)) {
        return false;
      }
      return fPatterns.equals(other.fPatterns);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null ? 331 : 47 + fSymbol.hashCode()) + fPatterns.hashCode();
  }
}
