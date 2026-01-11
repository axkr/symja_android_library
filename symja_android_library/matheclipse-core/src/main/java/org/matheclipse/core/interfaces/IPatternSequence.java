package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.IPatternMap;

/**
 * Interface for <b>Sequence Patterns</b> that match a variable number of expressions.
 * <p>
 * Unlike {@link IPattern}, which matches exactly <i>one</i> expression (e.g., {@code _Integer}), an
 * {@code IPatternSequence} can match <b>zero, one, or multiple</b> expressions in an argument list.
 * This corresponds to the constructs {@code BlankSequence} ({@code __}) and
 * {@code BlankNullSequence} ({@code ___}).
 * </p>
 *
 * <h3>1. Common Implementations</h3>
 * <ul>
 * <li><b>{@link org.matheclipse.core.expression.PatternSequence}:</b> Represents standard sequence
 * wildcards.
 * <ul>
 * <li>{@code x__} (BlankSequence): Matches 1 or more expressions.</li>
 * <li>{@code x___} (BlankNullSequence): Matches 0 or more expressions.</li>
 * </ul>
 * </li>
 * <li><b>{@link org.matheclipse.core.expression.RepeatedPattern}:</b> Represents repetition
 * patterns.
 * <ul>
 * <li>{@code p..} (Repeated): Matches one or more occurrences of pattern {@code p}.</li>
 * <li>{@code p...} (RepeatedNull): Matches zero or more occurrences of pattern {@code p}.</li>
 * </ul>
 * </li>
 * <li><b>{@link org.matheclipse.core.expression.OptionsPattern}:</b> Specifically matches a
 * sequence of options ({@code Rule} or {@code RuleDelayed}) at the end of a function call.</li>
 * </ul>
 *
 * <h3>2. Matching Mechanics</h3>
 * <p>
 * When the pattern matcher encounters an {@code IPatternSequence} in a function definition (e.g.,
 * {@code f[a_, b___]}), it attempts to bind a sub-sequence of the input arguments to this pattern.
 * This often involves backtracking to find the correct split of arguments between multiple sequence
 * patterns.
 * </p>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Example 1: BlankSequence (__)</h4>
 * 
 * <pre>
 * // Define f[x__] := Length[{x}]
 * // 'x__' matches the entire sequence of arguments (1 or more)
 * ISymbol f = F.Dummy("f");
 * ISymbol x = F.Dummy("x");
 * IAST lhs = F.unary(f, F.$ps(x, null, false)); // x__
 *
 * // f[1, 2, 3] -> x matches sequence (1, 2, 3)
 * </pre>
 *
 * <h4>Example 2: Splitting Arguments</h4>
 * 
 * <pre>
 * // Define g[a__, b_]
 * // 'a__' matches everything except the last argument, which 'b_' consumes.
 * // g[1, 2, 3, 4]
 * // a__ matches (1, 2, 3)
 * // b_ matches 4
 * </pre>
 *
 * @see org.matheclipse.core.interfaces.IPatternObject
 * @see org.matheclipse.core.interfaces.IPattern
 * @see org.matheclipse.core.expression.PatternSequence
 * @see org.matheclipse.core.expression.RepeatedPattern
 */
public interface IPatternSequence extends IPatternObject, IExpr {
  /**
   * Retrieves the specific condition or "Head Test" for this sequence.
   * <p>
   * For example, in the pattern {@code x__Integer}, the head test is {@code Integer}. All elements
   * in the matched sequence must satisfy this test.
   * </p>
   *
   * @return the head test expression, or {@code null} if none is defined.
   */
  @Override
  public IExpr getHeadTest();

  /**
   * Checks if the provided {@code sequence} of expressions matches this pattern object.
   * <p>
   * This method verifies if the sequence length fits the constraints (e.g., {@code >= 1} for
   * {@code __}) and if the individual elements satisfy the head test. If successful, it binds the
   * sequence to the pattern variable in the {@link IPatternMap}.
   * </p>
   *
   * @param sequence the AST containing the sequence of expressions to match.
   * @param patternMap the map to store the bound result (variable name -> matched sequence).
   * @param optionsPatternHead specific handling for OptionPatterns, typically {@code S.Missing}
   *        otherwise.
   * @return {@code true} if the sequence matches; {@code false} otherwise.
   */
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead);

  /**
   * Verifies if <b>all</b> elements in the given {@code sequence} fulfill the pattern's condition.
   * <p>
   * For a pattern like {@code x__Integer}, this iterates through the list {@code {1, 2, 3}} and
   * checks {@code 1.head().equals(Integer)}, {@code 2.head().equals(Integer)}, etc.
   * </p>
   *
   * @param sequence the sequence of expressions to test.
   * @param patternMap map for context during condition checks.
   * @return {@code true} if every element satisfies the condition.
   */
  public boolean isConditionMatchedSequence(IAST sequence, IPatternMap patternMap);

  /**
   * Checks if this pattern sequence allows a default value (like {@code x___.}).
   *
   * @return {@code true} if a default value mechanism is enabled.
   */
  public boolean isDefault();

  public boolean isNullSequence();
}
