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
public IExpr evaluate(final IAST functionList) {
    if (functionList.size() == 3) {
      return binaryOperator(functionList.get(1), functionList.get(2));
    }

    if (functionList.size() > 3) {
      final ISymbol sym = functionList.topHead();
      final IAST result = F.ast(sym);
      IExpr tres;
      IExpr temp = functionList.get(1);
      boolean evaled = false;
      int i = 2;

      while (i < functionList.size()) {
				tres = binaryOperator(temp, functionList.get(i));
        if (tres == null) {
          result.add(temp);
          if (i == functionList.size() - 1) {
            result.add(functionList.get(i));
          } else {
            temp = functionList.get(i);
          }
          i++;
        } else {
          evaled = true;
          temp = tres;
          if (i == (functionList.size() - 1)) {
            result.add(temp);
          }
          i++;
        }
      }

      if (evaled) {
        if ((result.size() == 2) && ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)) {
          return result.get(1);
        }
        return result;
      }
    }

    return null;
  }

}