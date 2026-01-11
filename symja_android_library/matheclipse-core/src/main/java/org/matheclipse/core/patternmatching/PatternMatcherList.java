package org.matheclipse.core.patternmatching;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A specialized <b>Pattern Matcher</b> that accumulates multiple substitution results into a list.
 * <p>
 * While the standard {@link PatternMatcherAndEvaluator} is designed to find a match and return a
 * single transformed result (e.g., for {@code ReplaceAll}), {@code PatternMatcherList} is designed
 * to collect <b>all</b> resulting transformations into an internal {@link IASTAppendable} list.
 * </p>
 *
 * <h3>1. Functionality</h3>
 * <p>
 * This class is typically used internally by functions that need to return a list of matches or
 * transformed sub-expressions, such as {@code Cases} or specific list-processing algorithms.
 * </p>
 * <ul>
 * <li><b>Accumulation:</b> Instead of replacing the original expression in-place and returning, it
 * appends the result of the transformation (RHS) to {@code fReplaceList}.</li>
 * <li><b>State Management:</b> It maintains the state of the list across multiple match
 * attempts.</li>
 * </ul>
 *
 * <h3>2. Usage Scenario</h3>
 * <p>
 * When scanning a large expression tree, if the goal is to extract every sub-expression that
 * matches {@code x_Integer} and transform it via {@code x^2}, this matcher can be applied
 * repeatedly. The {@link #getReplaceList()} method is then used to retrieve the final list of
 * results {@code {1, 4, 9, ...}}.
 * </p>
 *
 * <h3>3. Internal Structure</h3>
 * <ul>
 * <li><b>LHS & RHS:</b> Like its parent, it holds a pattern (LHS) and a transformation (RHS).</li>
 * <li><b>Result List:</b> It initializes an {@code IASTAppendable} ({@code fReplaceList}) in the
 * constructor to store outcomes.</li>
 * </ul>
 *
 * @see org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator
 * @see org.matheclipse.core.interfaces.IASTAppendable
 */
public final class PatternMatcherList extends PatternMatcherAndEvaluator {
  IASTAppendable fReplaceList;

  public IASTAppendable getReplaceList() {
    return fReplaceList;
  }

  public PatternMatcherList() {
    super();
  }

  public PatternMatcherList(final int setSymbol, final IExpr leftHandSide,
      final IExpr rightHandSide) {
    super(setSymbol, leftHandSide, rightHandSide, true, 0);
    fReplaceList = F.ListAlloc();
  }

  @Override
  public IPatternMatcher copy() {
    PatternMatcherList v = new PatternMatcherList();
    v.fLHSPriority = fLHSPriority;
    v.fThrowIfTrue = fThrowIfTrue;
    v.fLhsPatternExpr = fLhsPatternExpr;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    v.fRightHandSide = fRightHandSide;
    v.fReturnResult = fReturnResult;
    v.fReplaceList = fReplaceList;
    return v;
  }

  @Override
  protected IExpr replaceSubExpressionOrderlessFlat(final IAST lhsPatternAST, final IAST lhsEvalAST,
      final IExpr rhsExpr, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public boolean checkRHSCondition(EvalEngine engine) {
    IPatternMap patternMap = createPatternMap();

    if (patternMap.isAllPatternsAssigned()) {
      IExpr result = patternMap.substituteSymbols(fRightHandSide, F.CEmptySequence);
      if (result.isPresent()) {
        fReplaceList.append(result);
        return false;
      }
    }
    return super.checkRHSCondition(engine);
  }
}
