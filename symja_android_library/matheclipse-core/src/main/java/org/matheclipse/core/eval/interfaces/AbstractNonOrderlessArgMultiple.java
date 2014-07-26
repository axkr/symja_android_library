package org.matheclipse.core.eval.interfaces;


import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 */
public class AbstractNonOrderlessArgMultiple extends AbstractArg2 {

  @Override
public IExpr evaluate(final IAST ast) {
    if (ast.size() == 3) {
      return binaryOperator(ast.arg1(), ast.get(2));
    }

    if (ast.size() > 3) {
      final ISymbol sym = ast.topHead();
      final IAST result = F.ast(sym);
      IExpr tres;
      IExpr temp = ast.arg1();
      boolean evaled = false;
      int i = 2;

      while (i < ast.size()) {
				tres = binaryOperator(temp, ast.get(i));
        if (tres == null) {
          result.add(temp);
          if (i == ast.size() - 1) {
            result.add(ast.get(i));
          } else {
            temp = ast.get(i);
          }
          i++;
        } else {
          evaled = true;
          temp = tres;
          if (i == (ast.size() - 1)) {
            result.add(temp);
          }
          i++;
        }
      }

      if (evaled) {
        if ((result.size() == 2) && ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)) {
          return result.arg1();
        }
        return result;
      }
    }

    return null;
  }

}