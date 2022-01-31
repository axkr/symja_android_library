package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.IPatternMap;

/** Interface for pattern sequence objects (i.e. x__) */
public interface IPatternSequence extends IPatternObject, IExpr {

  /**
   * Get the additional pattern sequences condition expression
   *
   * @return may return null;
   */
  @Override
  public IExpr getHeadTest();

  /**
   * Check if this pattern sequence object matches the given <code>sequence</code>.
   *
   * @param sequence the sequence which should be matched.
   * @param patternMap a map from a pattern to a possibly found value during pattern-matching.
   * @param optionsPatternHead TODO
   * @return
   */
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead);

  /**
   * Return <code>true</code>, if all of the elements in the <code>sequence</code> fulfill the
   * pattern sequences additional condition
   *
   * @param sequence
   * @return
   */
  public boolean isConditionMatchedSequence(IAST sequence, IPatternMap patternMap);

  /**
   * Return <code>true</code>, if the expression is a pattern sequence with an associated default
   * value,
   *
   * @return
   */
  public boolean isDefault();

  public boolean isNullSequence();
}
