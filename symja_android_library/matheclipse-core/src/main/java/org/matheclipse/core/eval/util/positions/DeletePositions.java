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

  final IAST originalAST;

  int level;
  IAST positions;

  public DeletePositions(Function<IExpr, IExpr> f, IAST ast) {
    this.originalAST = ast;
    reset(F.CEmptyList);
  }

  public DeletePositions(Function<IExpr, IExpr> f, IAST ast, IAST positions) {
    this.originalAST = ast;
    reset(positions);
  }

  protected void reset(IAST positions) {
    this.level = 1;
    this.positions = positions;
  }

  /**
   * Map a function `f` over the elements of a list of positions
   * <code>{{p11, p12, ...},{p21, p22, ...}}</code>.
   */
  public static IAST deleteListOfPositions(IAST ast, IAST listOfListsOfPositions) {
    DeletePositions deletePositions = new DeletePositions(x -> null, ast);
    for (int i = 1; i < listOfListsOfPositions.size(); i++) {
      IAST subList = (IAST) listOfListsOfPositions.get(i);
      if (subList.isEmpty()) {
        ast = F.Sequence();
        continue;
      }

      deletePositions.reset(subList);
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
   * Map a function `f` over the elements of a list of positions <code>{p1, p1, ...}</code>.
   */
  public static IAST deletePositions(IAST ast, IAST listOfPositions) {
    if (listOfPositions.isEmpty()) {
      return ast;
    }
    DeletePositions deletePositions = new DeletePositions(x -> null, ast, listOfPositions);
    IASTAppendable result = deletePositions.mapAtRecursive(ast);
    return removeNILRecursive(result);
  }

  protected IASTAppendable mapAtRecursive(IAST ast) {
    IExpr position = positions.get(level);
    // Note: `All` and `Span` cannot be used in Delete and Insert
    if (position.isString() || position.isKey()) {
      if (ast.isAssociation()) {
        IAssociation assoc = (IAssociation) ast;
        IExpr key = position.isString() ? position : position.first();
        int index = -1;
        // Find the index of the rule for this key
        for (int i = 1; i < ast.size(); i++) {
          if (ast.getRule(i).first().equals(key)) {
            index = i;
            break;
          }
        }

        if (index > 0) {
          IAST rule = assoc.getRule(index);
          if (level == positions.argSize()) {
            rule = rule.setAtCopy(2, F.NIL);
            IASTAppendable association = MapPositions.getAppendableAST(ast);
            association.set(index, rule); // FIX: Replace at index instead of appendRule()
            return association;
          } else {
            IExpr arg = rule.second();
            if (arg.isASTOrAssociation()) {
              level++;
              IExpr temp = mapAtRecursive(((IAST) arg));
              if (temp.isPresent()) {
                rule = rule.setAtCopy(2, temp);
                IASTAppendable association = MapPositions.getAppendableAST(ast);
                association.set(index, rule); // FIX: Replace at index instead of appendRule()
                level--;
                return association;
              }
              level--;
            }
          }
        }
      }
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(positions, originalAST));
    }

    int p = position.toIntDefault();
    if (F.isNotPresent(p)) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(positions, originalAST));
    }
    if (p < 0) {
      p = ast.size() + p;
    }

    if (p >= 0 && p < ast.size()) {
      if (level == positions.argSize()) {
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
          level++;
          IExpr temp = mapAtRecursive((IAST) arg);
          if (temp.isPresent()) {
            level--;
            subResult.set(p, temp);
            return subResult;
          }
          level--;
        }
      }
    }
    // Part `1` of `2` does not exist.
    throw new ArgumentTypeException("partw", F.list(positions, originalAST));
  }

  /**
   * Remove all {@link F#NIL} entries from the list.
   */
  private static IAST removeNILRecursive(IAST list) {
    if (list.isEvalFlagOn(IAST.IS_COPIED)) {
      int evalFlags = list.getEvalFlags();
      list.setEvalFlags(evalFlags ^ IAST.IS_COPIED);

      if (list.isAssociation()) {
        IAssociation assoc = (IAssociation) list;
        IAssociation result = F.assoc();
        for (int i = 1; i < list.size(); i++) {
          IAST rule = assoc.getRule(i);
          IExpr rhs = rule.second();
          if (rhs.isPresent()) { // only keep if RHS is not NIL
            if (rhs.isASTOrAssociation()) {
              IAST temp = removeNILRecursive((IAST) rhs);
              if (temp.isPresent()) {
                // FIX: Apply temp to position 2 of the rule (RHS), NOT position `i`
                result.appendRule(rule.setAtCopy(2, temp));
              }
            } else {
              result.appendRule(rule);
            }
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