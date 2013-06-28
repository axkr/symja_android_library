package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 */
public class Thread extends AbstractFunctionEvaluator {

  public Thread() {
  }

  @Override
  public IExpr evaluate(final IAST ast) {
  	Validate.checkRange(ast, 2, 3);
   
    if (!(ast.get(1).isAST())) {
      return null;
    }
    // LevelSpec level = null;
    // if (functionList.size() == 4) {
    // level = new LevelSpecification(functionList.get(3));
    // } else {
    // level = new LevelSpec(1);
    // }
    IExpr head = F.List;
    if (ast.size() == 3) {
      head = ast.get(2);
    }
    final IAST list = (IAST) ast.get(1);
    if (list.size() > 1) {
      return threadList(list, head, list.head(), 1);
    }
    return null;
  }

  /**
   * Thread through all lists in the arguments of the IAST [i.e. the list header
   * has the attribute ISymbol.LISTABLE] example: Sin[{2,x,Pi}] ==>
   * {Sin[2],Sin[x],Sin[Pi]}
   * 
   * @param list
   * @param head
   *          the head over which
   * @param listLength
   *          the length of the list
   * @return
   */
  public static IAST threadList(final IAST list, IExpr head, IExpr mapHead,
      final int headOffset) {

    int listLength = 0;

    for (int i = 1; i < list.size(); i++) {
      if ((list.get(i).isAST())
          && (((IAST) list.get(i)).head().equals(head))) {
        if (listLength == 0) {
          listLength = ((IAST) list.get(i)).size() - 1;
        } else {
          if (listLength != ((IAST) list.get(i)).size() - 1) {
            listLength = 0;
            return null;
            // for loop
          }
        }
      }
    }

    final IAST result = F.ast(head, listLength, true);

    for (int j = headOffset; j < listLength + headOffset; j++) {
     
      final IAST subResult = F.ast(mapHead, list.size() - headOffset, true);

      for (int i = headOffset; i < list.size(); i++) {
         
        if ((list.get(i).isAST())
            && (((IAST) list.get(i)).head().equals(head))) {
          final IAST arg = (IAST) list.get(i);
          subResult.set(i, arg.get(j));
        } else {
          subResult.set(i, list.get(i));
        }
      }
      // end for j

      result.set(j, subResult);
    }
    // end for i

    return result;
  }
}
