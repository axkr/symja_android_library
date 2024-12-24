package org.matheclipse.core.eval.util.positions;

import java.util.function.Function;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class DeletePositions {
  int index;
  IAST positions;

  public DeletePositions(Function<IExpr, IExpr> f) {
    reset(F.CEmptyList);
  }

  public DeletePositions(Function<IExpr, IExpr> f, IAST positions) {
    reset(positions);
  }

  protected void reset(IAST positions) {
    this.index = 1;
    this.positions = positions;
  }

  /**
   * Map a function `f` over the elements of a list of positions
   * <code>{{p11, p12, ...},{p21, p22, ...}}</code>. The function `f` is applied to the part of the
   * expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfPositions
   */
  public static IAST deleteListOfPositions(IAST ast, IAST listOfPositions) {
    DeletePositions deletePositions = new DeletePositions(x -> null);
    for (int i = 1; i < listOfPositions.size(); i++) {
      deletePositions.reset(listOfPositions.getAST(i));
      IExpr temp = deletePositions.mapAtRecursive(ast);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          ast = (IAST) temp;
        }
      }
    }
    return removeNILRecursive(ast);
  }

  /**
   * Map a function `f` over the elements of a list of positions <code>{p1, p1, ...}</code>. The
   * function `f` is applied to the part of the expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfPositions list of positions
   * @return
   */
  public static IAST deletePositions(IAST ast, IAST listOfPositions) {
    DeletePositions deletePositions = new DeletePositions(x -> null, listOfPositions);
    IASTAppendable result = deletePositions.mapAtRecursive(ast);
    return removeNILRecursive(result);
  }

  protected IASTAppendable mapAtRecursive(IAST ast) {
    IExpr pos = positions.get(index);
    if (pos.equals(S.All)) {
      IASTAppendable subResult = MapPositions.getAppendableAST(ast);
      if (index == positions.size() - 1) {
        for (int i = 1; i < ast.size(); i++) {
          subResult.set(i, F.NIL);
        }
      } else {
        index++;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = mapAtRecursive(subResult.getAST(i));
          if (temp.isPresent()) {
            subResult.set(i, temp);
          }
        }
        index--;
      }
      return subResult;
    }
    if (pos.isString() || pos.isKey()) {
      if (ast.isAssociation()) {
        IExpr key = pos.isString() ? pos : pos.first();
        IAST rule = ((IAssociation) ast).getRule(key);
        if (rule.isPresent()) {
          if (index == positions.size() - 1) {
            rule = rule.setAtCopy(2, F.NIL);
            IASTAppendable association = MapPositions.getAppendableAST(ast);
            association.appendRule(rule);
            return association;
          } else {
            IExpr arg = rule.second();
            if (arg.isASTOrAssociation()) {
              index++;
              IExpr temp = mapAtRecursive(((IAST) arg));
              if (temp.isPresent()) {
                rule = rule.setAtCopy(2, temp);
                IASTAppendable association = MapPositions.getAppendableAST(ast);
                association.appendRule(rule);
                index--;
                return association;
              }
              index--;
            }
          }
        }
        // Part `1` of `2` does not exist.
        throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
      }
    }

    int p = pos.toIntDefault();
    if (p == Integer.MIN_VALUE) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
    }
    if (p < 0) {
      p = ast.size() + p;
    }

    if (p >= 0 && p < ast.size()) {
      if (index == positions.size() - 1) {
        if (p == 0) {
          if (ast.isAssociation()) {
            IASTAppendable result = F.IdentityAlloc(ast.argSize());
            result.addEvalFlags(IAST.IS_COPIED);
            for (int i = 1; i < ast.size(); i++) {
              IExpr rule = ast.getRule(i);
              result.append(rule.second());
            }
            return result;
          }
          IASTAppendable subResult = MapPositions.getAppendableAST(ast);
          subResult.set(0, S.Sequence);
          return subResult;
        }
        if (ast.isAssociation()) {
          IASTAppendable result = MapPositions.getAppendableAST(ast);
          IExpr rule = ((IAST) ast.getRule(p)).setAtCopy(2, F.NIL);
          result.set(p, rule);
          return result;
        }
        IASTAppendable result = MapPositions.getAppendableAST(ast);
        result.set(p, F.NIL);
        return result;
      } else {
        IExpr arg = ast.get(p);
        if (arg.isASTOrAssociation()) {
          IASTAppendable subResult = MapPositions.getAppendableAST(ast);
          index++;
          IExpr temp = mapAtRecursive((IAST) arg);
          if (temp.isPresent()) {
            index--;
            subResult.set(p, temp);
            return subResult;
          }
          index--;
        }
      }
    }
    // Part `1` of `2` does not exist.
    throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
  }

  /**
   * Remove all {@link F#NIL} entries from the list.
   *
   * @param list the list in which NIL entries should be removed
   * @return
   */
  private static IAST removeNILRecursive(IAST list) {
    if (list.isEvalFlagOn(IAST.IS_COPIED)) {
      int evalFlags = list.getEvalFlags();
      list.setEvalFlags(evalFlags ^ IAST.IS_COPIED);

      if (list.isAssociation()) {
        IAssociation result = (IAssociation) list;
        for (int i = 1; i < list.size(); i++) {
          IAST rule = (IAST) list.getRule(i);
          IExpr rhs = rule.second();
          if (rhs.isPresent()) {
            if (rhs.isASTOrAssociation()) {
              IAST temp = removeNILRecursive((IAST) rhs);
              if (temp.isPresent()) {
                result.appendRule(rule.setAtCopy(i, temp));
              } else {
                result.remove(i);
              }
            }
          } else {
            result.remove(i);
          }
        }
        return result;
      }

      IASTAppendable result = F.ast(list.head(), list.size());
      for (int i = 1; i < list.size(); i++) {
        IExpr x = list.getRule(i);
        if (x.isPresent()) {
          if (x.isASTOrAssociation()) {
            IAST temp = removeNILRecursive((IAST) x);
            if (temp.isPresent()) {
              result.append(temp);
            }
          } else {
            result.append(x);
          }
        }
      }
      return result;
    }
    return list;
  }

}
