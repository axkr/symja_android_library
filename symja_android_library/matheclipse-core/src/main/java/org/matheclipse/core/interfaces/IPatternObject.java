package org.matheclipse.core.interfaces;

import java.util.List;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.patternmatching.IPatternMap;

/**
 * The root interface for all pattern matching objects in the Symja system.
 * <p>
 * An {@code IPatternObject} represents any expression that acts as a placeholder or template during
 * transformation rules and pattern matching. It serves as the base for atomic patterns (like
 * {@code _} or {@code x_}) and sequence patterns (like {@code x__}).
 * </p>
 *
 * <h3>1. Core Types of Patterns</h3> The hierarchy is divided into two main categories:
 * <ul>
 * <li><b>Single-Expression Patterns ({@link IPattern}):</b> These match exactly one expression in a
 * list or argument sequence.
 * <ul>
 * <li>{@link org.matheclipse.core.expression.Blank} ({@code _}): Matches any single
 * expression.</li>
 * <li>{@link org.matheclipse.core.expression.Pattern} ({@code x_}): Matches any single expression
 * and binds it to the symbol {@code x}.</li>
 * <li>{@link org.matheclipse.core.expression.PatternNested} ({@code x:pattern}): Matches
 * {@code pattern} and binds the result to {@code x}.</li>
 * </ul>
 * </li>
 * <li><b>Sequence Patterns ({@link IPatternSequence}):</b> These can match a variable number of
 * expressions (zero or more).
 * <ul>
 * <li>{@link org.matheclipse.core.expression.PatternSequence} ({@code x__} or {@code x___}):
 * Matches a sequence of 1 or more ({@code BlankSequence}) or 0 or more ({@code BlankNullSequence})
 * expressions.</li>
 * <li>{@link org.matheclipse.core.expression.RepeatedPattern} ({@code p..}): Matches a sequence
 * where each element matches pattern {@code p}.</li>
 * <li>{@link org.matheclipse.core.expression.OptionsPattern}: Specifically matches options (rules)
 * at the end of a function call.</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <h3>2. Pattern Attributes</h3>
 * <p>
 * Every pattern object may define:
 * </p>
 * <ul>
 * <li><b>Head Test:</b> A constraint on the head of the expression being matched (e.g.,
 * {@code _Integer} matches only if {@code expr.head().equals(S.Integer)}).</li>
 * <li><b>Condition:</b> An arbitrary boolean test that must pass for the match to succeed.</li>
 * <li><b>Default Value:</b> A value to use if the pattern is optional (e.g., {@code x_.}).</li>
 * </ul>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Defining a Function with Patterns</h4>
 * 
 * <pre>
 * // Define f[x_] := x^2
 * // Here, 'x_' is an IPatternObject (specifically an instance of Pattern)
 * ISymbol f = F.Dummy("f");
 * ISymbol x = F.Dummy("x");
 * F.SetDelayed(F.unary(f, F.Pattern(x, null)), F.Sqr(x));
 * </pre>
 *
 * <h4>Pattern Matching Structure</h4>
 * 
 * <pre>
 * // Check if an expression matches "x_"
 * IExpr target = F.C10;
 * IPatternObject pattern = F.Pattern(x, null); // x_
 *
 * PatternMatcher matcher = new PatternMatcher(pattern);
 * if (matcher.test(target)) {
 *   // Match successful
 * }
 * </pre>
 *
 * @see org.matheclipse.core.interfaces.IPattern
 * @see org.matheclipse.core.interfaces.IPatternSequence
 * @see org.matheclipse.core.patternmatching.PatternMatcher
 */
public interface IPatternObject extends IExpr, IAtomicEvaluate {

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
