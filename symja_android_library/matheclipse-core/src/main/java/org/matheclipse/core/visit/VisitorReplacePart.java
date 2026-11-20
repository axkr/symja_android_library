package org.matheclipse.core.visit;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/** Visitor for the <code>ReplacePart()</code> function */
public class VisitorReplacePart extends AbstractVisitor {

  /**
   * A list of pattern matchers which should be matched against, for every possible position of an
   * {@link IAST} structure.
   */
  private IPatternMatcher[] patternMatcherList;

  /**
   * <code>true</code> at index <code>j</code> if the matcher <code>patternMatcherList[j]</code> has
   * a pattern for a complete position list, like <code>{i_, j_} /; i &lt; j</code>; it is matched
   * against <code>{i1, i2, ...}</code> for every position of the expression.
   */
  private boolean[] positionListPattern;

  /**
   * <code>true</code> at index <code>j</code> if the matcher <code>patternMatcherList[j]</code> has
   * a negative position: <code>-(n+1)</code> is the head of an expression with <code>n</code>
   * arguments, also if {@link #startOffset} is <code>1</code>.
   */
  private boolean[] negativePosition;

  /**
   * Bit <code>k</code> of <code>negativeMask[j]</code> is set if the index <code>k</code> of the
   * position of matcher <code>patternMatcherList[j]</code> is a negative integer; that index is
   * matched in its negative form.
   */
  private int[] negativeMask;

  /**
   * <code>lhsLength[j]</code> is the number of indices of the position of matcher
   * <code>patternMatcherList[j]</code>, or <code>-1</code> if it isn't fixed.
   */
  private int[] lhsLength;

  /**
   * <code>true</code> at index <code>j</code> if the position pattern of matcher
   * <code>patternMatcherList[j]</code> has a negative integer which {@link #negativeMask} can't
   * place, like <code>{__, -1}</code> or <code>{1 | -1, 2}</code>: every combination of the
   * positive and the negative form of the indices is tried.
   */
  private boolean[] negativePattern;

  /** <code>true</code> if one of the {@link #negativePosition} entries is set. */
  private boolean negativeHead;

  /** <code>true</code> if a matcher is matched against a position list, so parts are visited. */
  private boolean nestedPositions;

  private int listLength;

  /**
   * <code>levelSizes[k]</code> is the <code>size()</code> of the expression visited at depth
   * <code>k</code>, so that a positive index of the position can be turned into the negative one.
   */
  private int[] levelSizes = new int[8];
  /**
   * <code>0</code> or <code>1</code>, depending if option <code>Heads->True</code> is set or the
   * <code>0</code> index position is used in the left-hand-side of a rule.
   */
  private int startOffset;

  /**
   * The current evaluation engine for this thread.
   */
  final private EvalEngine engine;

  /**
   * Create a new visitor which replaces parts of an expression.
   *
   * @param rule a rule or list-of-rules
   * @param heads if <code>TRUE</code> also replace the heads of expressions
   */
  public VisitorReplacePart(IAST rule, IExpr.COMPARE_TERNARY heads) {
    super();
    engine = EvalEngine.get();
    startOffset = heads == IExpr.COMPARE_TERNARY.TRUE ? 0 : 1;
    if (rule.isRuleAST()) {
      rule = F.list(rule);
    }
    if (rule.isListOfRules()) {
      IAST list = rule;
      this.patternMatcherList = new IPatternMatcher[list.argSize()];
      this.positionListPattern = new boolean[list.argSize()];
      this.negativePosition = new boolean[list.argSize()];
      this.negativeMask = new int[list.argSize()];
      this.lhsLength = new int[list.argSize()];
      this.negativePattern = new boolean[list.argSize()];

      for (int i = 1; i < list.size(); i++) {
        rule = (IAST) list.get(i);
        initPatternMatcher(rule.arg1(), rule.arg2(), heads);
      }
      if (heads == COMPARE_TERNARY.FALSE) {
        // if set explicitly to FALSE, then startOffset is always 1, otherwise startOffset may be
        // changed in initPatternMatcher(); a literal negative position still reaches the head
        startOffset = 1;
      }
    }
    setNestedPositions();
  }

  private void setNestedPositions() {
    for (int j = 0; j < listLength; j++) {
      if (positionListPattern[j] || patternMatcherList[j].getLHS().isSequence()) {
        nestedPositions = true;
      }
    }
  }

  public VisitorReplacePart(IExpr lhs, IExpr rhs, IExpr.COMPARE_TERNARY heads) {
    super();
    engine = EvalEngine.get();
    if (lhs.isList()) {
      this.patternMatcherList = new IPatternMatcher[lhs.argSize()];
    } else {
      this.patternMatcherList = new IPatternMatcher[1];
    }
    this.positionListPattern = new boolean[patternMatcherList.length];
    this.negativePosition = new boolean[patternMatcherList.length];
    this.negativeMask = new int[patternMatcherList.length];
    this.lhsLength = new int[patternMatcherList.length];
    this.negativePattern = new boolean[patternMatcherList.length];
    startOffset = heads == IExpr.COMPARE_TERNARY.TRUE ? 0 : 1;
    initPatternMatcher(lhs, rhs, heads);
    if (heads == COMPARE_TERNARY.FALSE) {
      // a literal negative position still reaches the head
      startOffset = 1;
    }
    setNestedPositions();
  }

  /**
   * Initialize the pattern matcher. If the left-hand-side is a list of lists of integers, then the
   * right-hand-side is matched against the positions in the list.
   * <p>
   * <b>Note</b>: the {@link #startOffset} is set to <code>0</code> if a position <code>0</code> is
   * found.
   * 
   * @param lhs
   * @param rhs
   * @param heads
   */
  private void initPatternMatcher(IExpr lhs, IExpr rhs, IExpr.COMPARE_TERNARY heads) {
    IExpr fromPositions = lhs;
    if (isPositionListPattern(lhs)) {
      positionListPattern[listLength] = true;
      this.patternMatcherList[listLength++] = engine.evalPatternMatcher(lhs, rhs);
      return;
    }
    try {
      // try extracting an int[] array of expressions
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        if (list.isListOfLists()) {
          for (int j = 1; j < list.size(); j++) {
            IAST subList = list.getAST(j);
            int[] positions = new int[subList.argSize()];
            for (int k = 1; k < subList.size(); k++) {
              positions[k - 1] = subList.get(k).toIntDefault();
              if (F.isNotPresent(positions[k - 1])) {
                throw ReturnException.RETURN_FALSE;
              }
              if (positions[k - 1] == 0) {
                startOffset = 0;
              }
            }
            addPositions(positions, rhs);
          }
        } else {
          if (list.argSize() > 0) {
            int[] positions = new int[list.argSize()];

            for (int j = 1; j < list.size(); j++) {
              positions[j - 1] = list.get(j).toIntDefault();
              if (F.isNotPresent(positions[j - 1])) {
                throw ReturnException.RETURN_FALSE;
              }
              if (positions[j - 1] == 0) {
                startOffset = 0;
              }
            }
            addPositions(positions, rhs);
          }
        }
      } else {
        int[] positions = new int[] {lhs.toIntDefault()};
        if (F.isNotPresent(positions[0])) {
          throw ReturnException.RETURN_FALSE;
        }
        if (positions[0] == 0) {
          startOffset = 0;
        }
        addPositions(positions, rhs);
      }
    } catch (ReturnException rex) {
      if (fromPositions.isList()) {
        IAST list = ((IAST) fromPositions).apply(S.Sequence, 1);
        setNegativeMask((IAST) fromPositions);
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(list, rhs);
        this.patternMatcherList[listLength++] = evalPatternMatcher;
      } else {
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(fromPositions, rhs);
        this.patternMatcherList[listLength++] = evalPatternMatcher;
      }
    }
  }

  /** Add the matcher for the literal position <code>positions</code>. */
  private void addPositions(int[] positions, IExpr rhs) {
    lhsLength[listLength] = positions.length;
    for (int k = 0; k < positions.length && k < 31; k++) {
      if (positions[k] < 0) {
        negativeMask[listLength] |= 1 << k;
        negativePosition[listLength] = true;
        negativeHead = true;
      }
    }
    this.patternMatcherList[listLength++] = engine.evalPatternMatcher(F.Sequence(positions), rhs);
  }

  /**
   * Mark the negative integer indices of the position pattern <code>{i1, i2, ...}</code>: in
   * {@link #negativeMask} if its length is fixed and the negative integers are indices, else in
   * {@link #negativePattern}.
   */
  private void setNegativeMask(IAST position) {
    if (!position.has(x -> x.isInteger() && x.isNegative(), false)) {
      return;
    }
    negativePosition[listLength] = true;
    negativeHead = true;
    int mask = 0;
    for (int k = 0; k < position.argSize(); k++) {
      IExpr index = position.get(k + 1);
      if (index.isInteger() && index.isNegative() && k < 31) {
        mask |= 1 << k;
      } else if (k >= 31 || index.isPatternSequence(false) || index.isPatternSequence(true)
          || index.isAST(S.Repeated) || index.isAST(S.RepeatedNull)
          || index.has(x -> x.isInteger() && x.isNegative(), false)) {
        negativePattern[listLength] = true;
        return;
      }
    }
    lhsLength[listLength] = position.argSize();
    negativeMask[listLength] = mask;
  }

  /**
   * Test if <code>lhs</code> is a pattern for a complete position list, which is not a list itself:
   * <code>{i_, j_} /; i &lt; j</code>, <code>{__}?test</code> or <code>p : {_, _}</code>.
   */
  private static boolean isPositionListPattern(IExpr lhs) {
    if (lhs.isCondition() || lhs.isAST(S.PatternTest, 3)) {
      return lhs.first().isList() || isPositionListPattern(lhs.first());
    }
    if (lhs instanceof PatternNested) {
      IExpr patternExpr = ((PatternNested) lhs).getPatternExpr();
      return patternExpr.isList() || isPositionListPattern(patternExpr);
    }
    return false;
  }

  /**
   * Visit the parts of <code>ast</code> at the position <code>positions</code>: the first rule
   * which matches a part replaces it, else the rules for longer positions are tried inside it.
   */
  private IExpr visitPatternIndexList(IAST ast, IASTAppendable positions) {
    IASTAppendable result = F.NIL;
    final int depth = positions.argSize();
    if (depth >= levelSizes.length) {
      levelSizes = java.util.Arrays.copyOf(levelSizes, 2 * depth + 2);
    }
    levelSizes[depth] = ast.size();
    for (int i = negativeHead ? 0 : startOffset; i < ast.size(); i++) {
      // the integer position, and for associations the Key(...) wrapped and the bare key
      IASTAppendable[] paths = positionPaths(ast, positions, i);
      IExpr replacement = F.NIL;
      for (int j = 0; j < listLength && replacement.isNIL(); j++) {
        if (patternMatcherList[j] == null || (i < startOffset && !negativePosition[j])) {
          // only a literal negative position reaches the head here
          continue;
        }
        for (IASTAppendable path : paths) {
          replacement = matchPosition(j, path);
          if (replacement.isPresent()) {
            break;
          }
        }
      }
      if (replacement.isPresent()) {
        result = setPart(result, ast, i, replacement);
      } else if (nestedPositions && ast.get(i).isASTOrAssociation()
          && (i >= startOffset || reachesInsideHead(depth))) {
        for (IASTAppendable path : paths) {
          IExpr temp = visitPatternIndexList((IAST) ast.get(i), path);
          if (temp.isPresent()) {
            result = setPart(result, ast, i, temp);
            break;
          }
        }
      }
    }
    return result;
  }

  private static IASTAppendable[] positionPaths(IAST ast, IASTAppendable positions, int i) {
    IASTAppendable path = positions.copyAppendable();
    path.append(F.ZZ(i));
    if (!ast.isAssociation() || i == 0) {
      return new IASTAppendable[] {path};
    }
    IExpr key = ast.getRule(i).first();
    IASTAppendable keyPath = positions.copyAppendable();
    keyPath.append(F.Key(key));
    IASTAppendable barePath = positions.copyAppendable();
    barePath.append(key);
    return new IASTAppendable[] {path, keyPath, barePath};
  }

  private static IASTAppendable setPart(IASTAppendable result, IAST ast, int position,
      IExpr value) {
    if (position == 0 && ast.isAssociation()) {
      result = ((IAssociation) ast).copyAST();
    } else if (result.isNIL()) {
      result = ast.copyAppendable();
    }
    result.setValue(position, value);
    return result;
  }

  /**
   * Match the rule <code>patternMatcherList[j]</code> against the position <code>path</code>.
   *
   * @return {@link F#NIL} if the rule doesn't match
   */
  private IExpr matchPosition(int j, IASTAppendable path) {
    IPatternMatcher matcher = patternMatcherList[j];
    if (negativePattern[j]) {
      return evalSignCombinations(j, path);
    }
    if (startOffset > 0 && !reachesHeads(j, path)) {
      return F.NIL;
    }
    if (positionListPattern[j]) {
      return matcher.eval(path.setAtCopy(0, S.List), engine);
    }
    if (matcher.getLHS().isSequence()) {
      return evalWithNegativeIndices(j, path);
    }
    // a pattern for one index matches the first level only
    return path.argSize() == 1 ? matcher.eval(path.arg1(), engine) : F.NIL;
  }

  /**
   * <code>true</code> if a rule names a part inside the head at depth <code>depth</code>, by a
   * negative index there.
   */
  private boolean reachesInsideHead(int depth) {
    for (int j = 0; j < listLength; j++) {
      if (negativePattern[j] || (depth < 31 && (negativeMask[j] & (1 << depth)) != 0
          && lhsLength[j] > depth + 1)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Match the position pattern of rule <code>patternMatcherList[j]</code> against every
   * combination of the positive and the negative form of the integer indices of
   * <code>path</code>; a head, an index <code>0</code>, is only reached in its negative form unless
   * heads are visited.
   */
  private IExpr evalSignCombinations(int j, IASTAppendable path) {
    IPatternMatcher matcher = patternMatcherList[j];
    final int depth = path.argSize();
    if (depth > 16) {
      return startOffset > 0 && !reachesHeads(j, path) ? F.NIL : matcher.eval(path, engine);
    }
    int flippable = 0;
    int required = 0;
    for (int k = 0; k < depth; k++) {
      int i = path.get(k + 1).toIntDefault();
      if (i >= 0 && F.isPresent(i)) {
        flippable |= 1 << k;
        if (i == 0 && startOffset > 0) {
          required |= 1 << k;
        }
      }
    }
    // the subsets of the flippable indices which contain the required ones
    final int free = flippable & ~required;
    for (int mask = required;; mask = (((mask | ~free) + 1) & free) | required) {
      IASTAppendable signed = path.copyAppendable();
      for (int k = 0; k < depth; k++) {
        if ((mask & (1 << k)) != 0) {
          signed.set(k + 1, F.ZZ(path.get(k + 1).toIntDefault() - levelSizes[k]));
        }
      }
      IExpr temp = matcher.eval(signed, engine);
      if (temp.isPresent()) {
        return temp;
      }
      if (mask == flippable) {
        return F.NIL;
      }
    }
  }

  /**
   * <code>true</code> if every head on the position <code>path</code>, an index <code>0</code>, is
   * named by a negative index of rule <code>patternMatcherList[j]</code>.
   */
  private boolean reachesHeads(int j, IASTAppendable path) {
    final int depth = path.argSize();
    for (int k = 0; k < depth; k++) {
      if (path.get(k + 1).isZero()
          && (lhsLength[j] != depth || (negativeMask[j] & (1 << k)) == 0)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Match the position of rule <code>patternMatcherList[j]</code> against <code>path</code>; the
   * indices which are negative integers in the rule are matched in their negative form.
   */
  private IExpr evalWithNegativeIndices(int j, IASTAppendable path) {
    IPatternMatcher matcher = patternMatcherList[j];
    final int mask = negativeMask[j];
    final int depth = path.argSize();
    if (mask == 0 || lhsLength[j] != depth) {
      return matcher.eval(path, engine);
    }
    IASTAppendable signed = path.copyAppendable();
    for (int k = 0; k < depth; k++) {
      if ((mask & (1 << k)) != 0) {
        int i = path.get(k + 1).toIntDefault();
        if (i < 0 || F.isNotPresent(i)) {
          return F.NIL;
        }
        // -(n+1) is the head of an expression with n arguments
        signed.set(k + 1, F.ZZ(i - levelSizes[k]));
      }
    }
    return matcher.eval(signed, engine);
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    IASTAppendable positionsToMatch = F.ast(S.Sequence);
    for (int j = 0; j < listLength; j++) {
      IPatternMatcher matcher = patternMatcherList[j];
      if (matcher == null) {
        continue;
      }
      IExpr lhs = matcher.getLHS();
      if (lhs.isAST(S.Sequence, 1)) {
        // empty sequence matches with complete expression
        return matcher.getRHS();
      }
    }
    return visitPatternIndexList(ast, positionsToMatch);
  }
}
