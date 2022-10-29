package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** */
public class AbstractNonOrderlessArgMultiple extends AbstractArg2 {

  /**
   * Evaluate ast as function with no argument.
   *
   * @param ast ast with <code>size()==1</code>
   * @param engine
   * @return
   */
  public IExpr evaluateAST0(final IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Evaluate ast as function with one argument.
   *
   * @param ast ast with <code>size()==2</code>
   * @param engine
   * @return
   */
  public IExpr evaluateAST1(final IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return null;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int size = ast.size();
    if (size == 3) {
      return binaryOperator(ast, ast.arg1(), ast.arg2(), engine);
    } else if (size == 2) {
      return evaluateAST1(ast, engine);
    } else if (size == 1) {
      return evaluateAST0(ast, engine);
    }

    if (size > 3) {
      final ISymbol sym = ast.topHead();
      final IASTAppendable result = F.ast(sym);
      IExpr tres;
      IExpr temp = ast.arg1();
      boolean evaled = false;
      int i = 2;

      while (i < size) {
        tres = binaryOperator(ast, temp, ast.get(i), engine);
        if (tres.isNIL()) {
          result.append(temp);
          if (i == size - 1) {
            result.append(ast.get(i));
          } else {
            temp = ast.get(i);
          }
        } else {
          evaled = true;
          temp = tres;
          if (i == (size - 1)) {
            result.append(temp);
          }
        }
        i++;
      }

      if (evaled) {
        if ((result.isAST1()) && sym.hasOneIdentityAttribute()) {
          return result.arg1();
        }
        return result;
      }
    }

    return F.NIL;
  }
}
