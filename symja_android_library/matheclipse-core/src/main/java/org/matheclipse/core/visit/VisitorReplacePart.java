package org.matheclipse.core.visit;

import java.util.ArrayList;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/** Visitor for the <code>ReplacePart()</code> function */
public class VisitorReplacePart extends AbstractVisitor {

  int offset;

  final IExpr replaceExpr;
  ArrayList<int[]> positionList;
  int[] maxMatcherDepth;
  ArrayList<IPatternMatcher> patternMatcherList;
  boolean useSimpleMatching;
  EvalEngine engine;

  /**
   * Create a new visitor which replaces parts of an expression.
   *
   * @param rule a rule or list-of-rules
   * @param heads if <code>TRUE</code> also replace the heads of expressions
   */
  public VisitorReplacePart(IAST rule, IExpr.COMPARE_TERNARY heads) {
    super();

    IExpr fromPositions = rule.arg1();
    this.replaceExpr = rule.arg2();
    try {
      // try extracting an integer list of expressions
      offset = heads == COMPARE_TERNARY.FALSE ? 1 : 0;
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        if (list.isListOfLists()) {
          positionList = new ArrayList<int[]>(list.size());
          for (int j = 1; j < list.size(); j++) {
            IAST subList = list.getAST(j);
            int[] fPositions = new int[subList.argSize()];
            for (int k = 1; k < subList.size(); k++) {
              fPositions[k - 1] = subList.get(k).toIntDefault();
              if (fPositions[k - 1] == Integer.MIN_VALUE) {
                throw ReturnException.RETURN_FALSE;
              }
            }
            positionList.add(fPositions);
          }
        } else {
          int[] fPositions = new int[list.argSize()];
          positionList = new ArrayList<int[]>(1);
          for (int j = 1; j < list.size(); j++) {
            fPositions[j - 1] = list.get(j).toIntDefault();
            if (fPositions[j - 1] == Integer.MIN_VALUE) {
              throw ReturnException.RETURN_FALSE;
            }
          }
          positionList.add(fPositions);
        }

      } else {
        positionList = new ArrayList<int[]>(1);
        int[] fPositions = new int[1];
        fPositions[0] = rule.arg1().toIntDefault();
        if (fPositions[0] == Integer.MIN_VALUE) {
          throw ReturnException.RETURN_FALSE;
        }
        positionList.add(fPositions);
      }
    } catch (ReturnException rex) {
      // use pattern-matching
      offset = heads == IExpr.COMPARE_TERNARY.TRUE ? 0 : 1;
      positionList = null;
      engine = EvalEngine.get();
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        maxMatcherDepth = new int[1];
        maxMatcherDepth[0] = list.depth();
        patternMatcherList = new ArrayList<IPatternMatcher>(1);
        patternMatcherList.add(engine.evalPatternMatcher(list, replaceExpr));
        useSimpleMatching = false;
      } else {
        maxMatcherDepth = new int[1];
        maxMatcherDepth[0] = fromPositions.depth();
        patternMatcherList = new ArrayList<IPatternMatcher>(1);
        patternMatcherList.add(engine.evalPatternMatcher(fromPositions, replaceExpr));
        useSimpleMatching = true;
      }
    }
  }

  private IExpr visitPositionIndex(IAST ast, final int index) {
    int[] fPositions;
    IASTMutable result = F.NIL;

    for (int i = 0; i < positionList.size(); i++) {
      fPositions = positionList.get(i);
      if (index >= fPositions.length) {
        continue;
      }

      int position = fPositions[index];
      if (position < 0) {
        position = ast.size() + position;
      }
      if (position >= ast.size() || position < offset) {
        continue;
      }

      if (index == fPositions.length - 1) {
        if (!result.isPresent()) {
          result = ast.copyAppendable();
        }
        if (position == 0 && result.isAssociation()) {
          result = result.copyAST();
        }
        result.setValue(position, replaceExpr);
      } else {
        IExpr arg = ast.get(position);
        if (arg.isASTOrAssociation()) {
          IExpr temp = visitPositionIndex((IAST) arg, index + 1);
          if (temp.isPresent()) {
            if (!result.isPresent()) {
              result = ast.copyAppendable();
            }
            if (position == 0 && result.isAssociation()) {
              result = result.copyAST();
            }
            result.setValue(position, temp);
          }
        }
      }
    }

    return result;
  }

  private IExpr visitPatternIndex(IAST ast) {

    IASTAppendable result = F.NIL;

    for (int i = offset; i < ast.size(); i++) {
      for (int j = 0; j < patternMatcherList.size(); j++) {
        IPatternMatcher matcher = patternMatcherList.get(j);
        IExpr temp = matcher.eval(F.ZZ(i), engine);
        if (temp.isPresent()) {
          if (!result.isPresent()) {
            result = ast.copyAppendable();
          }
          result.setValue(i, temp);
          break;
        }
      }
    }

    return result;
  }

  private IExpr visitPatternIndexList(IAST ast, IASTAppendable matchedPos,
      final int recursionLevel) {
    IASTAppendable result = F.NIL;

    for (int i = offset; i < ast.size(); i++) {
      try {
        matchedPos.append(i);
        for (int j = 0; j < patternMatcherList.size(); j++) {
          if (recursionLevel <= maxMatcherDepth[j]) {
            IPatternMatcher matcher = patternMatcherList.get(j);
            IExpr temp = matcher.eval(matchedPos, engine);
            if (temp.isPresent()) {
              if (!result.isPresent()) {
                result = ast.copyAppendable();
              }
              result.setValue(i, temp);
              break;
            } else {
              if (ast.get(i).isASTOrAssociation()) {
                temp = visitPatternIndexList((IAST) ast.get(i), matchedPos, recursionLevel + 1);
                if (temp.isPresent()) {
                  if (!result.isPresent()) {
                    result = ast.copyAppendable();
                  }
                  result.setValue(i, temp);
                }
              }
              continue;
            }
          }
        }

        if (i > 0 && ast.isAssociation()) {
          // for associations the key instead of the position can also match
          matchedPos.set(matchedPos.size() - 1, ast.getRule(i).first());
          for (int j = 0; j < patternMatcherList.size(); j++) {
            if (recursionLevel <= maxMatcherDepth[j]) {
              IPatternMatcher matcher = patternMatcherList.get(j);
              IExpr temp = matcher.eval(matchedPos, engine);
              if (temp.isPresent()) {
                if (!result.isPresent()) {
                  result = ast.copyAppendable();
                }
                result.setValue(i, temp);
                break;
              } else {
                if (ast.get(i).isASTOrAssociation()) {
                  temp = visitPatternIndexList((IAST) ast.get(i), matchedPos, recursionLevel + 1);
                  if (temp.isPresent()) {
                    if (!result.isPresent()) {
                      result = ast.copyAppendable();
                    }
                    result.setValue(i, temp);
                  }
                }
                continue;
              }
            }
          }
        }
      } finally {
        matchedPos.remove(matchedPos.size() - 1);
      }
    }

    return result;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (positionList != null) {
      return visitPositionIndex(ast, 0);
    }
    if (useSimpleMatching) {
      return visitPatternIndex(ast);
    } else {
      IASTAppendable matchedPos = F.ListAlloc();
      return visitPatternIndexList(ast, matchedPos, 1);
    }
  }
}
