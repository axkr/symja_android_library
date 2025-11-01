package org.matheclipse.core.patternmatching;

import org.matheclipse.core.combinatoric.IStepVisitor;
import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>MultisetPartitionsIterator</code> to match {@link ISymbol#FLAT}
 * and {@link ISymbol#ORDERLESS} expressions in pattern matching.
 *
 * @see PatternMatcher
 * @see MultisetPartitionsIterator
 */
public class FlatOrderlessStepVisitor extends FlatStepVisitor implements IStepVisitor {
  protected int[] multiset;

  /**
   * @param sym
   * @param lhsPatternAST
   * @param lhsEvalAST
   * @param patternMatcher
   * @param patternMap
   * @deprecated used only for JUnit tests
   */
  @Deprecated
  public FlatOrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      PatternMatcher patternMatcher, IPatternMap patternMap) {
    this(sym, lhsPatternAST, lhsEvalAST, //
        patternMatcher.new StackMatcher(EvalEngine.get()), patternMap);
  }

  public FlatOrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      StackMatcher stackMatcher, IPatternMap patternMap) {
    super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap);
    toIntArray(lhsEvalAST, 1, lhsEvalAST.size());
  }

  public FlatOrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      StackMatcher stackMatcher, IPatternMap patternMap, boolean oneIdentity) {
    super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, oneIdentity);
    toIntArray(lhsEvalAST, 1, lhsEvalAST.size());
  }

  /**
   * Convert the <code>sortedList</code> to an <code>int[]</code> array. Equal elements get the same
   * index in the resulting <code>int[]</code> array.
   *
   * @param sortedList
   * @param start
   * @param end
   */
  private final void toIntArray(IAST sortedList, int start, int end) {
    int size = end - start;
    multiset = new int[size];
    array = new IExpr[size];

    // initialize with the first element
    IExpr last = sortedList.get(start);
    int index = 0;
    int pos = 0;
    multiset[pos++] = index;
    array[index] = last;

    for (int k = start + 1; k < end; k++) {
      IExpr x = sortedList.get(k);
      if (x.equals(last)) {
        multiset[pos++] = index;
      } else {
        index++;
        multiset[pos++] = index;
        array[index] = x;
        last = x;
      }
    }
  }

  @Override
  public int[] getMultisetArray() {
    return multiset;
  }
}
