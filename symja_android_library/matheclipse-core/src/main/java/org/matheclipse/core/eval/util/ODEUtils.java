package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Helpers shared by the differential equation solvers, for reading and writing the applied
 * derivatives <code>y(x)</code>, <code>y'(x)</code>, <code>y''(x)</code>, ... which their equations
 * are written in.
 *
 * <p>
 * These belong to no single solver: <code>DSolve</code> solves such equations symbolically and
 * <code>NDSolve</code> reduces them to a first order system and integrates them, but both have to
 * recognize and construct the same expressions first.
 */
public class ODEUtils {

  private ODEUtils() {} // static use only

  /**
   * The derivative order of an applied derivative, e.g. <code>2</code> for <code>y''(x)</code>.
   *
   * @param deriveExpr the result of {@link IExpr#isDerivativeAST1()}, whose first entry is the
   *        <code>Derivative(n)</code> operator
   * @return the order, or <code>-1</code> if the expression is not an applied derivative of a
   *         definite non-negative order
   */
  public static int derivativeOrder(IAST[] deriveExpr) {
    if (deriveExpr.length == 3) {
      if (deriveExpr[0].isAST1() && deriveExpr[0].arg1().isInteger()) {
        int order = deriveExpr[0].arg1().toIntDefault();
        if (F.isPresent(order) && order >= 0) {
          return order;
        }
      }
    }
    return -1;
  }

  /**
   * The applied derivative <code>function^(order)(variable)</code>, e.g. <code>y''(x)</code> for
   * order <code>2</code>. The inverse of {@link #derivativeOrder(IAST[])}.
   *
   * @param function the dependent function symbol
   * @param order the derivative order, <code>0</code> for the function itself
   * @param variable the independent variable
   */
  public static IExpr derivative(ISymbol function, int order, IExpr variable) {
    if (order == 0) {
      return F.unaryAST1(function, variable);
    }
    return F.unaryAST1(F.unaryAST1(F.Derivative(F.ZZ(order)), function), variable);
  }
}
