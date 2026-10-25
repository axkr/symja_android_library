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
import org.matheclipse.core.interfaces.IInteger;
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

  private int listLength;
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

      for (int i = 1; i < list.size(); i++) {
        rule = (IAST) list.get(i);
        initPatternMatcher(rule.arg1(), rule.arg2(), heads);
      }
      if (heads == COMPARE_TERNARY.FALSE) {
        // if set explicitly to FALSE, then startOffset is always 1, otherwise startOffset may be
        // changed in initPatternMatcher()
        startOffset = 1;
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
    startOffset = heads == IExpr.COMPARE_TERNARY.TRUE ? 0 : 1;
    initPatternMatcher(lhs, rhs, heads);
    if (heads == COMPARE_TERNARY.FALSE) {
      startOffset = 1;
    }
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
            IPatternMatcher evalPatternMatcher =
                engine.evalPatternMatcher(F.Sequence(positions), rhs);
            this.patternMatcherList[listLength++] = evalPatternMatcher;
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
            IPatternMatcher evalPatternMatcher =
                engine.evalPatternMatcher(F.Sequence(positions), rhs);
            this.patternMatcherList[listLength++] = evalPatternMatcher;
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
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(F.Sequence(positions), rhs);
        this.patternMatcherList[listLength++] = evalPatternMatcher;

      }
    } catch (ReturnException rex) {
      if (fromPositions.isList()) {
        IAST list = ((IAST) fromPositions).apply(S.Sequence, 1);
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(list, rhs);
        this.patternMatcherList[listLength++] = evalPatternMatcher;
      } else {
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(fromPositions, rhs);
        this.patternMatcherList[listLength++] = evalPatternMatcher;
      }
    }
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

  private IExpr visitPatternIndexList(IAST ast, IASTAppendable positions) {
    IASTAppendable result = F.NIL;
    for (int i = startOffset; i < ast.size(); i++) {
      final IInteger position = F.ZZ(i);
      for (int j = 0; j < listLength; j++) {
        IPatternMatcher matcher = patternMatcherList[j];
        if (matcher == null) {
          continue;
        }

        // 1. try to match the (positive) integer position
        IASTAppendable positionsToMatch = positions.copyAppendable();
        positionsToMatch.append(position);
        IASTAppendable temp = patternIndexRecursive(matcher, positionListPattern[j], ast,
            positionsToMatch, i, result);
        if (temp.isPresent()) {
          result = temp;
          break;
        }

        // 2. try to match the negative integer position; a pattern for the position list only
        // sees the positive positions
        if (!positionListPattern[j]) {
          IInteger negativePart = F.ZZUniqueReference(i - ast.size());
          positionsToMatch = positions.copyAppendable();
          positionsToMatch.append(negativePart);
          temp = patternIndexRecursive(matcher, false, ast, positionsToMatch, i, result);
          if (temp.isPresent()) {
            IExpr ex = temp.replaceAll(x -> {
              if (x == negativePart) {
                // compare unique reference from F.ZZUniqueReference(); don't use equals() method!
                return position;
              }
              return F.NIL;
            });
            if (ex.isPresent() && (ex instanceof IASTAppendable)) {
              result = (IASTAppendable) ex;
            } else {
              result = temp;
            }
            break;
          }
        }

        if (ast.isAssociation() && i > 0) {
          // for associations the key can also be used as position specification. Try both the
          // `Key(...)` wrapped form (e.g. from Position(...)) and the bare key. This must be
          // attempted at every nesting level to support nested key positions.
          IExpr key = ast.getRule(i).first();

          // 3a. try to match the `Key(...)` wrapped key
          positionsToMatch = positions.copyAppendable();
          positionsToMatch.append(F.Key(key));
          temp = patternIndexRecursive(matcher, positionListPattern[j], ast, positionsToMatch, i,
              result);
          if (temp.isPresent()) {
            result = temp;
            break;
          }

          // 3b. try to match the bare key
          positionsToMatch = positions.copyAppendable();
          positionsToMatch.append(key);
          temp = patternIndexRecursive(matcher, positionListPattern[j], ast, positionsToMatch, i,
              result);
          if (temp.isPresent()) {
            result = temp;
            break;
          }
        }

      }

    }
    return result;
  }

  /**
   * @param matcher
   * @param positionList if <code>true</code> the matcher is matched against the list
   *        <code>{i1, i2, ...}</code> of the positions
   * @param ast
   * @param positionsToMatch the list of positions which should be matched
   * @param position
   * @param result
   * @return {@link F#NIL} if no match was found
   */
  private IASTAppendable patternIndexRecursive(IPatternMatcher matcher, boolean positionList,
      IAST ast, IASTAppendable positionsToMatch, int position, IASTAppendable result) {
    if (positionList || matcher.getLHS().isSequence()) {
      IExpr temp = positionList ? matcher.eval(positionsToMatch.setAtCopy(0, S.List), engine)
          : matcher.eval(positionsToMatch, engine);
      if (temp.isPresent()) {
        if (position == 0 && ast.isAssociation()) {
          result = ((IAssociation) ast).copyAST();
        } else if (result.isNIL()) {
          result = ast.copyAppendable();
        }
        result.setValue(position, temp);
        return result;
      } else {
        if (ast.get(position).isASTOrAssociation()) {
          temp = visitPatternIndexList((IAST) ast.get(position), positionsToMatch);
          if (temp.isPresent()) {
            if (position == 0 && ast.isAssociation()) {
              result = ((IAssociation) ast).copyAST();
            } else if (result.isNIL()) {
              result = ast.copyAppendable();
            }

            result.setValue(position, temp);
            return result;
          }
        }
      }
    } else {
      IExpr temp = matcher.eval(F.ZZ(position), engine);
      if (temp.isNIL() && ast.isAssociation() && position > 0) {
        // for associations the key can also be used as position specification
        IExpr key = ast.getRule(position).first();
        temp = matcher.eval(F.Key(key), engine);
        if (temp.isNIL()) {
          temp = matcher.eval(key, engine);
        }
      }
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = ast.copyAppendable();
        }
        result.setValue(position, temp);
        return result;
      }
    }
    return F.NIL;
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
