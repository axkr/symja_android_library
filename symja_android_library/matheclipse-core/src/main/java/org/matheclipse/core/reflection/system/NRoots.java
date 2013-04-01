package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Determine the numerical roots of a univariate polynomial
 * 
 * See Wikipedia entries for: <a
 * href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation
 * </a>, <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic
 * function</a> and <a
 * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
 * 
 * @see Roots
 */
public class NRoots extends Roots {

  public NRoots() {
  }

  @Override
  public IExpr evaluate(final IAST ast) {
    if (ast.size() != 2) {
      return null;
    }
    IExpr temp = roots(ast, true);
    if (temp == null || !temp.isList()) {
      return null;
    }
    IAST list = (IAST)temp;
    IAST result = F.List();
    for (int i = 1; i < list.size(); i++) {
      result.add(F.evaln(list.get(i)));
    }
    return result;
  }

}