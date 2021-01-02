package org.matheclipse.core.visit;

import java.util.ArrayList;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/** Visitor for the <code>ReplacePart()</code> function */
public class VisitorReplacePart extends AbstractVisitor {

  final IExpr fReplaceExpr;

  ArrayList<int[]> fPositionList;

  ArrayList<IPatternMatcher> fPatternList;
  int fPatternListSize;
  EvalEngine engine;

  public VisitorReplacePart(IAST rule) {
    super();
    IExpr fromPositions = rule.arg1();
    this.fReplaceExpr = rule.arg2();
    try {
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        if (list.isListOfLists()) {
          fPositionList = new ArrayList<int[]>(list.size());
          for (int j = 1; j < list.size(); j++) {
            IAST subList = list.getAST(j);
            int[] fPositions = new int[subList.argSize()];
            for (int k = 1; k < subList.size(); k++) {
              fPositions[k - 1] = subList.get(k).toIntDefault();
              if (fPositions[k - 1] == Integer.MIN_VALUE) {
                throw ReturnException.RETURN_FALSE;
              }
            }
            fPositionList.add(fPositions);
          }
        } else {
          int[] fPositions = new int[list.argSize()];
          fPositionList = new ArrayList<int[]>(1);
          for (int j = 1; j < list.size(); j++) {
            fPositions[j - 1] = list.get(j).toIntDefault();
            if (fPositions[j - 1] == Integer.MIN_VALUE) {
              throw ReturnException.RETURN_FALSE;
            }
          }
          fPositionList.add(fPositions);
        }

      } else {
        fPositionList = new ArrayList<int[]>(1);
        int[] fPositions = new int[1];
        fPositions[0] = rule.arg1().toIntDefault();
        if (fPositions[0] == Integer.MIN_VALUE) {
          throw ReturnException.RETURN_FALSE;
        }
        fPositionList.add(fPositions);
      }
    } catch (ReturnException rex) {
      fPositionList = null;
      engine = EvalEngine.get();
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        fPatternList = new ArrayList<IPatternMatcher>(1);
        fPatternList.add(engine.evalPatternMatcher(list, fReplaceExpr));
        fPatternListSize = list.argSize();
      } else {
        fPatternList = new ArrayList<IPatternMatcher>(1);
        fPatternList.add(engine.evalPatternMatcher(fromPositions, fReplaceExpr));
        fPatternListSize = -1;
      }
    }
  }

  private IExpr visitPositionIndex(IAST ast, final int index) {
    int[] fPositions;
    IASTMutable result = ast.copyAppendable();
    boolean matched = false;
    for (int i = 0; i < fPositionList.size(); i++) {
      fPositions = fPositionList.get(i);
      if (index >= fPositions.length) {
        continue;
      }

      int position = fPositions[index];
      if (position < 0) {
        position = ast.size() + position;
      }
      if (position >= ast.size() || position < 0) {
        continue;
      }

      if (index == fPositions.length - 1) {
        matched = true;
        if (position == 0 && result.isAssociation()) {
          result = result.copyAST();
        }
        result.setValue(position, fReplaceExpr);
      } else {
        IExpr arg = result.get(position);
        if (arg.isASTOrAssociation()) {
          IExpr temp = visitPositionIndex((IAST) arg, index + 1);
          if (temp.isPresent()) {
            matched = true;
            result.setValue(position, temp);
          }
        }
      }
    }

    return matched ? result : F.NIL;
  }

  private IExpr visitPatternIndex(IAST ast) {

    IASTAppendable result = ast.copyAppendable();
    boolean matched = false;

    for (int i = 1; i < ast.size(); i++) {

      for (int j = 0; j < fPatternList.size(); j++) {
        IPatternMatcher matcher = fPatternList.get(j);
        IExpr temp = matcher.eval(F.ZZ(i), engine);
        if (temp.isPresent()) {
          matched = true;
          result.setValue(i, temp);
          break;
        }
      }
    }

    return matched ? result : F.NIL;
  }

  private IExpr visitPatternIndexList(IAST ast, IASTAppendable matchedPos, final int index) {
    if (index > matchedPos.size() - 1) {
      return F.NIL;
    }
    IASTAppendable result = ast.copyAppendable();
    boolean matched = false;
    for (int i = 1; i < ast.size(); i++) {
      try {
        matchedPos.append(F.ZZ(i));
        for (int j = 0; j < fPatternList.size(); j++) {
          IPatternMatcher matcher = fPatternList.get(j);
          IExpr temp = matcher.eval(matchedPos, engine);
          if (temp.isPresent()) {
            matched = true;
            result.setValue(i, temp);
            break;
          } else {
            if (ast.get(i).isASTOrAssociation()) {
              temp = visitPatternIndexList((IAST) ast.get(i), matchedPos, index + 1);
              if (temp.isPresent()) {
                matched = true;
                result.setValue(i, temp);
              }
            }
          }
        }
      } finally {
        matchedPos.remove(matchedPos.size() - 1);
      }
    }

    return matched ? result : F.NIL;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (fPositionList != null) {
      return visitPositionIndex(ast, 0);
    }
    if (fPatternListSize < 0) {
      return visitPatternIndex(ast);
    } else {
      IASTAppendable matchedPos = F.ListAlloc();
      return visitPatternIndexList(ast, matchedPos, 0);
    }
  }
}
