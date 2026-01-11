package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.IPatternMap;

/**
 * Interface for <b>Single-Expression Patterns</b> in the Symja system.
 * <p>
 * An {@code IPattern} represents a pattern object that matches exactly <b>one</b> expression in a
 * list or argument sequence. This distinguishes it from {@link IPatternSequence}, which can match
 * zero, one, or multiple expressions.
 * </p>
 *
 * <h3>1. Common Implementations</h3>
 * <p>
 * The most frequent implementations of this interface correspond to the following pattern
 * constructs:
 * </p>
 * <ul>
 * <li><b>{@link org.matheclipse.core.expression.Pattern} ({@code x_}):</b> A named pattern. It
 * matches a single expression and binds it to the symbol {@code x}.</li>
 * <li><b>{@link org.matheclipse.core.expression.Blank} ({@code _}):</b> An unnamed wildcard. It
 * matches any single expression but does not bind it to a name.</li>
 * <li><b>{@link org.matheclipse.core.expression.PatternNested} ({@code x:pattern}):</b> Matches the
 * inner {@code pattern} and binds the result to the symbol {@code x}.</li>
 * </ul>
 *
 * <h3>2. Usage in Transformation Rules</h3>
 * <p>
 * {@code IPattern} is the fundamental building block for defining functions and transformation
 * rules. When the evaluation engine encounters an {@code IPattern} during matching, it checks if
 * the target expression satisfies constraints (like head tests or conditions) and then adds the
 * match to the {@link IPatternMap}.
 * </p>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Example 1: Named Pattern (x_)</h4>
 * 
 * <pre>
 * // Define a rule: f[x_] -> x^2
 * // 'x_' is an IPattern instance (specifically class Pattern)
 * ISymbol f = F.Dummy("f");
 * ISymbol x = F.Dummy("x");
 * IAST lhs = F.unary(f, F.Pattern(x, null)); // f[x_]
 *
 * // If we match f[5] against f[x_]:
 * // 1. The engine sees 'x_' is an IPattern.
 * // 2. It matches '5' (a single Integer).
 * // 3. It binds 'x' -> '5' in the pattern map.
 * </pre>
 *
 * <h4>Example 2: Blank Pattern (_)</h4>
 * 
 * <pre>
 * // Define a rule: g[_] -> 0
 * // '_' is an IPattern instance (specifically class Blank)
 * ISymbol g = F.Dummy("g");
 * IAST lhs = F.unary(g, F.$b()); // g[_]
 *
 * // This matches g[a], g[10], g[Sin[x]] because they are all single expressions.
 * // It does NOT match g[1, 2] because that requires matching a sequence of 2 expressions.
 * </pre>
 *
 * @see org.matheclipse.core.interfaces.IPatternObject
 * @see org.matheclipse.core.interfaces.IPatternSequence
 * @see org.matheclipse.core.expression.Pattern
 * @see org.matheclipse.core.expression.Blank
 */
public interface IPattern extends IPatternObject, IExpr {

  /**
   * Checks if the given expression fulfills the pattern's specific condition.
   * <p>
   * This method verifies if the target {@code expr} satisfies constraints such as:
   * </p>
   * <ul>
   * <li><b>Head Test:</b> Does {@code expr.head()} match the required head (e.g.,
   * {@code _Integer})?</li>
   * <li><b>Condition:</b> Does the arbitrary condition predicate evaluate to {@code true}?</li>
   * </ul>
   *
   * @param expr the expression being tested against this pattern.
   * @param patternMap the current state of matched variables; used to check consistency if the
   *        pattern appears multiple times.
   * @return {@code true} if the expression matches the condition; {@code false} otherwise.
   */
  public boolean isConditionMatched(IExpr expr, IPatternMap patternMap);
}
