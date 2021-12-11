package org.matheclipse.core.visit;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;

/**
 * Replace all occurrences of Slot[] expressions.
 *
 * <p>The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class VisitorReplaceSlots extends VisitorExpr {
  final IAST astSlots;

  public VisitorReplaceSlots(IAST ast) {
    super();
    this.astSlots = ast;
  }

  private IExpr getSlot(IInteger ii) {
    int i = ii.toIntDefault();
    if (i >= 0 && i < astSlots.size()) {
      return astSlots.get(i);
    }
    return F.NIL;
  }

  private IExpr getSlot(IStringX str) {
    IExpr arg1 = astSlots.arg1();
    if (arg1.isDataset()) {
      return ((IASTDataset) arg1).getValue(str);
    }
    if (arg1.isAST(S.Association)) {
      arg1 = EvalEngine.get().evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      return ((IAssociation) arg1).getValue(str);
    }
    return F.NIL;
  }

  private IExpr getSlotSequence(IInteger ii) {
    int i = ii.toIntDefault();
    if (i >= 0 && i <= astSlots.size()) {
      IASTAppendable result = F.ast(S.Sequence, astSlots.size());
      for (int j = i; j < astSlots.size(); j++) {
        result.append(astSlots.get(j));
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * @param ast
   * @param pos
   * @param startSlot it is assumed that <code>startSlot>=0 && startSlot < astSlots.size()</code>
   * @return
   */
  private int getSlotSequence(IASTAppendable ast, int pos, int startSlot) {
    for (int j = startSlot; j < astSlots.size(); j++) {
      ast.append(pos++, astSlots.get(j));
    }
    return pos;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (ast.size() == 2) {
      final IExpr arg1 = ast.arg1();
      if (ast.isSlot()) {
        if (arg1.isInteger()) {
          return getSlot((IInteger) arg1);
        } else if (arg1.isString()) {
          return getSlot((IStringX) arg1);
        }
      } else if (ast.isSlotSequence() && arg1.isInteger()) {
        return getSlotSequence((IInteger) arg1);
      }
    }
    return visitAST(ast);
  }

  @Override
  protected IExpr visitAST(IAST ast) {
    IExpr temp;
    IASTAppendable result = F.NIL;
    int i = 0;
    int j = 0;
    int size = ast.size();
    IExpr arg;
    while (i < size) {
      arg = ast.get(i);
      if (!arg.isPureFunction()) {
        if (arg.isSlotSequence()) {
          int sequ = ((IAST) arg).arg1().toIntDefault();
          // something may be evaluated - return a new IAST:
          result = ast.copyAppendable(astSlots.argSize());
          result.remove(j);
          if (sequ >= 0 && sequ < astSlots.size()) {
            j = getSlotSequence(result, i, sequ);
            i++;
          }
          break;
        }
        temp = arg.accept(this);
        if (temp.isPresent()) {
          // something was evaluated - return a new IAST:
          result = ast.setAtClone(i++, temp);
          j++;
          break;
        }
      }
      j++;
      i++;
    }
    if (result.isPresent()) {
      while (i < size) {
        arg = ast.get(i);
        if (!arg.isPureFunction()) {
          if (arg.isSlotSequence()) {
            int sequ = ((IAST) arg).arg1().toIntDefault();
            result.remove(j);
            if (sequ >= 0 && sequ < astSlots.size()) {
              j = getSlotSequence(result, j, sequ);
            }
            i++;
            continue;
          }
          temp = arg.accept(this);
          if (temp.isPresent()) {
            result.set(j, temp);
          }
        }
        i++;
        j++;
      }
    }
    return result;
  }
}
