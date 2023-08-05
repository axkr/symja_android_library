package org.matheclipse.core.interfaces;

import java.util.List;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.patternmatching.IPatternMap;

/** Interface for pattern objects (i.e. _, x_, x__) */
public interface IPatternObject extends IExpr {

  /**
   * Add this pattern to the given <code>patternIndexMap</code>.
   *
   * @param patternIndexMap a map from the pattern symbol to the intern array index
   * @return
   */
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap);

  /**
   * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code>
   * is equivalent to <code>f[a_,b_]</code> )
   *
   * @param patternExpr
   * @param pm1
   * @param pm2
   * @return
   */
  public boolean equivalent(final IPatternObject patternExpr, final IPatternMap pm1,
      IPatternMap pm2);

  /**
   * Get the associated condition if available
   *
   * @return <code>null</code> if no condition is associated.
   */
  public IExpr getHeadTest();

  /**
   * Get the flags which should be set in an AST structure, if the structure contains a pattern or
   * pattern sequence.
   *
   * @return
   */
  public int getEvalFlags();

  /**
   * Get the pattern-matchers index in the <code>PatternMap</code>
   *
   * @param pm the PatternMap from which we determine the index.
   * @return
   */
  public int getIndex(IPatternMap pm);

  /**
   * Get the associated symbol for this pattern-object
   *
   * @return <code>null</code> if no symbol is associated
   */
  public ISymbol getSymbol();

  /**
   * Check if this pattern object matches the given <code>expr</code>.
   *
   * @param expr the expression which should be matched.
   * @param patternMap a map from a pattern to a possibly found value during pattern-matching.
   * @return
   */
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap);
}
