package org.matheclipse.core.patternmatching;

import org.matheclipse.core.combinatoric.AbstractListStepVisitor;
import org.matheclipse.core.combinatoric.NumberPartitionsIterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>NumberPartitionsIterator</code> to match flat expressions in
 * pattern matching.
 *
 * @see PatternMatcher
 * @see NumberPartitionsIterator
 */
public class FlatStepVisitor extends AbstractListStepVisitor<IExpr> {

  protected ISymbol fSymbol;
  protected StackMatcher stackMatcher;
  protected IPatternMap fPatternMap;
  protected IAST fLhsPatternAST;

  /**
   *        
   */
  protected final boolean fOneIdentity;

  /**
   * In an Orderless head which isn't Flat only a pattern sequence may take several arguments; any
   * other pattern takes exactly one.
   */
  protected boolean fOnlySequencesTakeSeveral = false;

  public FlatStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      StackMatcher stackMatcher, IPatternMap patternMap) {
    this(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, sym.hasOneIdentityAttribute());
  }

  public FlatStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST,
      StackMatcher stackMatcher, IPatternMap patternMap, boolean oneIdentity) {
    super(lhsEvalAST); // , 1, lhsEvalAST.size());
    this.fSymbol = sym;
    this.stackMatcher = stackMatcher;
    this.fPatternMap = patternMap;
    this.fLhsPatternAST = lhsPatternAST;
    this.fOneIdentity = oneIdentity;
  }

  @Override
  public boolean visit(int[][] result) {
    if (Thread.currentThread().isInterrupted()) {
      // the partitions of a long Plus or Times are exponentially many and are matched without an
      // evaluation, which would otherwise notice the timeout
      throw org.matheclipse.core.eval.exception.TimeoutException.TIMED_OUT;
    }
    return !matchSinglePartition(result, stackMatcher);
  }

  /** Set all pattern values to <code>null</code>; */
  protected void initPatternMap() {
    fPatternMap.initPattern();
  }

  /**
   * Match a single partition combination
   *
   * @param result
   * @param stackMatcher
   * @return
   */
  protected boolean matchSinglePartition(int[][] result, StackMatcher stackMatcher) {

    int lastStackSize = stackMatcher.size();
    boolean matched = true;
    IExpr[] patternValues = fPatternMap.copyPattern();
    try {

      for (int j = 0; j < result.length; j++) {
        final int n = result[j].length;
        final IExpr lhsPatternExpr = fLhsPatternAST.get(j + 1);
        final boolean sequence = PatternMatcher.standsForSequence(lhsPatternExpr);
        if (n > 1 && ((fOnlySequencesTakeSeveral && !sequence)
            // g_[y__] or _[x_] takes exactly one operand, never a group wrapped in the Flat head
            || (lhsPatternExpr.isAST() && lhsPatternExpr.head() instanceof IPatternObject)
            // only a pattern object takes a group: (b_.*t(y_))^n_. matches t(z)*r(z) on its own
            // with b->r(z), but in s(y_)*(b_.*t(y_))^n_. it doesn't take t(z)*r(z) of
            // s(z)*t(z)*r(z), which doesn't match
            || !takesSeveral(lhsPatternExpr, fSymbol))) {
          matched = false;
          return false;
        }
        if (n == 1 && fOneIdentity) {
          // OneIdentity here
          if (!stackMatcher.push(lhsPatternExpr, array[result[j][0]])) {
            matched = false;
            return false;
          }
        } else {
          final ISymbol head = sequence ? S.Sequence : fSymbol;
          final int row = j;
          if (n == 1 && head == fSymbol) {
            // one argument without OneIdentity: a pattern object gets the argument wrapped in
            // the head, x_ is f(a); a structure like u(a_) or a pattern the wrapped argument
            // does not fit, x_Integer, gets the argument itself
            final IExpr single = array[result[row][0]];
            if (!(lhsPatternExpr instanceof IPatternObject)
                || !stackMatcher.push(lhsPatternExpr, F.unaryAST1(fSymbol, single))) {
              if (!stackMatcher.push(lhsPatternExpr, single)) {
                matched = false;
                return false;
              }
            }
            continue;
          }
          final IAST partitionElement = F.mapRange(head, 0, n, i -> array[result[row][i]]);
          if (!stackMatcher.push(lhsPatternExpr, partitionElement)) {
            matched = false;
            return false;
          }
        }
      }

      if (!stackMatcher.matchRest()) {
        matched = false;
        return false;
      }
      return true;
    } finally {
      if (!matched) {
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Whether the pattern may take several arguments of the Flat head <code>flat</code>, wrapped in
   * it: a pattern object <code>x_</code>, <code>x__</code>, <code>x_h</code> or <code>x_.</code>,
   * also with a condition, a test, a default, <code>Longest</code>, <code>Shortest</code> or
   * <code>HoldPattern</code>, a pattern with the head <code>flat</code> itself, or alternatives of
   * which one does.
   */
  private static boolean takesSeveral(IExpr pattern, ISymbol flat) {
    if (pattern instanceof IPatternObject) {
      return true;
    }
    if (pattern.isCondition() || pattern.isAST(S.PatternTest, 3) || pattern.isOptional()
        || pattern.isAST(S.Longest) || pattern.isAST(S.Shortest)
        || pattern.isAST(S.HoldPattern, 2)) {
      return takesSeveral(pattern.first(), flat);
    }
    if (pattern.isAlternatives()) {
      return ((IAST) pattern).exists(p -> takesSeveral(p, flat));
    }
    return pattern.isAST() && pattern.head().equals(flat);
  }

  /** Debug output of one partition, used by the step iterators. */
  public String toString(int[][] result) {
    StringBuilder buf = new StringBuilder(16 + result.length * 10);
    for (int i = 0; i < result.length; i++) {
      if (result[i] == null) {
        buf.append("[null]");
      } else {
        buf.append("[");
        buf.append(fLhsPatternAST.get(i + 1).toString());
        buf.append(" -> ");
        for (int j = 0; j < result[i].length; j++) {
          buf.append(list.get(result[i][j] + 1).toString());
          if (j < result[i].length - 1) {
            buf.append(",");
          }
        }
        buf.append("]");
      }
    }
    buf.append('\n');
    return buf.toString();
  }
}
