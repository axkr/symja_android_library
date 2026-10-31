package org.matheclipse.core.eval.util.positions;

import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class DeletePositions extends PositionsVisitor {

  public DeletePositions(Function<IExpr, IExpr> f, IAST ast) {
    super(ast, F.CEmptyList);
  }

  public DeletePositions(Function<IExpr, IExpr> f, IAST ast, IAST positions) {
    super(ast, positions);
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
    IAST result = deletePositions.mapAtRecursive(ast);
    return removeNILRecursive(result);
  }

  // Note: `All` and `Span` cannot be used in Delete and Insert

  @Override
  protected boolean reachesHeadFromEnd() {
    return false;
  }

  /** Mark the value of the rule as deleted. */
  @Override
  protected IAST keyLeaf(IAST ast, int index, IAST rule) {
    IASTAppendable association = getAppendableAST(ast);
    association.set(index, rule.setAtCopy(2, F.NIL));
    return association;
  }

  /** Mark the part <code>p</code> as deleted; deleting the head splices the parts. */
  @Override
  protected IAST indexLeaf(IAST ast, int p) {
    if (p == 0) {
      if (ast.isAssociation()) {
        // deleting the head yields the bare values as a Sequence(...),
        // consistent with the non-association head removal below
        IASTAppendable result = F.ast(S.Sequence, ast.argSize());
        result.addFlag(Flag.IS_COPIED);
        for (int i = 1; i < ast.size(); i++) {
          IExpr rule = ast.getRule(i);
          result.append(rule.second());
        }
        return result;
      }
      IASTAppendable subResult = getAppendableAST(ast);
      subResult.set(0, S.Sequence);
      return subResult;
    }
    if (ast.isAssociation()) {
      IASTAppendable result = getAppendableAST(ast);
      IExpr rule = ((IAST) ast.getRule(p)).setAtCopy(2, F.NIL);
      result.set(p, rule);
      return result;
    }
    IASTAppendable result = getAppendableAST(ast);
    result.set(p, F.NIL);
    return result;
  }

  /**
   * Remove all {@link F#NIL} entries from the list.
   */
  private static IAST removeNILRecursive(IAST list) {
    if (list.hasFlag(Flag.IS_COPIED)) {
      list.clearFlag(Flag.IS_COPIED);

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
