package org.matheclipse.core.patternmatching;

import org.matheclipse.core.combinatoric.IStepVisitor;
import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.expression.F;
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
      StackMatcher stackMatcher, IPatternMap patternMap, boolean oneIdentity) {
    super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, oneIdentity);
  }

  @Override
  protected boolean matchSinglePartition(int[][] result, StackMatcher stackMatcher) {
    int lastStackSize = stackMatcher.size();
    IExpr[] patternValues = fPatternMap.copyPattern();
    boolean matched = true;
    try {

      for (int j = 0; j < result.length; j++) {
        final int n = result[j].length;
        if (n == 1) {
          if (fOneIdentity) {
            if (!stackMatcher.push(fLhsPatternAST.get(j + 1), array[result[j][0]])) {
              matched = false;
              return false;
            }
          } else {
            if (!stackMatcher.push(fLhsPatternAST.get(j + 1),
                F.unaryAST1(fSymbol, array[result[j][0]]))) {
              matched = false;
              return false;
            }
          }
        } else {
          throw new IllegalArgumentException(
              "OrderlessStepVisitor#matchSinglePartition() current length is " + n);
        }
      }

      matched = stackMatcher.matchRest();
      return matched;
    } finally {
      if (!matched) {
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(patternValues);
      }
    }
  }
}
