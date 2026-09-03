package org.matheclipse.core.patternmatching;

import org.matheclipse.core.combinatoric.IStepVisitor;
import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>MultisetPartitionsIterator</code> to match orderless expressions
 * in pattern matching.
 *
 * @see PatternMatcher
 * @see MultisetPartitionsIterator
 */
public class OrderlessStepVisitor extends FlatOrderlessStepVisitor implements IStepVisitor {

  /**
   * This visitor is used in an <code>MultisetPartitionsIterator</code> to match orderless
   * expressions in pattern matching. The <code>lhsPatternAST.size()</code> must be equal to <code>
   * lhsEvalAST.size()</code>.
   *
   * @see PatternMatcher
   * @see MultisetPartitionsIterator
   */
  public OrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      StackMatcher stackMatcher, IPatternMap patternMap) {
    // the sizes are equal, so every pattern argument gets exactly one evaluated argument, which
    // is pushed as it is
    super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, true);
  }

  @Override
  protected boolean matchSinglePartition(int[][] result, StackMatcher stackMatcher) {
    int lastStackSize = stackMatcher.size();
    IExpr[] savedPatterns = fPatternMap.copyPattern();
    boolean matched = false;

    try {
      for (int j = 0; j < result.length; j++) {
        int n = result[j].length;
        if (n != 1) {
          throw new IllegalArgumentException(
              "OrderlessStepVisitor#matchSinglePartition() current length is " + n);
        }

        if (!stackMatcher.push(fLhsPatternAST.get(j + 1), array[result[j][0]])) {
          // push failed -> will be cleaned up in finally via matched == false
          return false;
        }

      }

      matched = stackMatcher.matchRest();
      return matched;
    } finally {
      if (!matched) {
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(savedPatterns);
      }
    }
  }
}
