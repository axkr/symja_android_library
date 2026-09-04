package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.ODEUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The coefficients of a differential equation which is <b>linear</b> in the function it is solved
 * for, i.e. of the shape <code>a[n]*y^(n)(x) + ... + a[1]*y'(x) + a[0]*y(x) == g(x)</code>.
 *
 * <p>
 * This is the single place where {@link DSolve} decides whether an equation is linear. Reading the
 * coefficients with {@link org.matheclipse.core.expression.S#Coefficient} alone is not enough,
 * because a coefficient can be read off an equation the term does not actually have: the equation
 * <code>y'(x) + x*y'(x)^2 == 1</code> has the <code>y'(x)</code> coefficient <code>1</code> even
 * though the quadratic term makes it nonlinear, and reading only up to the first derivative of
 * <code>x*y''(x) + 2*y'(x) - x*y(x) == Sin(x)</code> silently leaves the second derivative behind.
 * Both used to produce a wrong answer. {@link #extract(IExpr, IExpr, IExpr, EvalEngine)} therefore
 * subtracts every term it has read and declines unless nothing containing the unknown is left over.
 */
final class LinearODEForm {

  /** The highest derivative order which occurs, <code>0</code> for an equation without one. */
  final int order;

  /** The coefficients <code>a[0]</code> ... <code>a[order]</code> of the derivatives. */
  final IExpr[] a;

  /** The right hand side, free of the function which is solved for. */
  final IExpr g;

  /** Whether every coefficient is free of the independent variable. */
  final boolean constantCoefficients;

  private LinearODEForm(int order, IExpr[] a, IExpr g, boolean constantCoefficients) {
    this.order = order;
    this.a = a;
    this.g = g;
    this.constantCoefficients = constantCoefficients;
  }

  /**
   * The coefficients of <code>residual</code> read as a linear differential equation
   * <code>residual == 0</code> for <code>yFunction</code>.
   *
   * @param residual the equation in the form which is equal to zero
   * @param yFunction the applied unknown function, e.g. <code>y(x)</code>
   * @param xVar the independent variable
   * @return <code>null</code> if the equation is not linear in <code>yFunction</code>, or if
   *         <code>yFunction</code> does not occur in it at all
   */
  static LinearODEForm extract(IExpr residual, IExpr yFunction, IExpr xVar, EvalEngine engine) {
    IExpr head = yFunction.head();
    IExpr rest = engine.evaluate(F.ExpandAll(residual));
    int n = highestDerivativeOrder(rest, head, xVar);
    if (n < 0) {
      return null;
    }
    IExpr[] a = new IExpr[n + 1];
    for (int k = n; k >= 1; k--) {
      IExpr dyx = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
      IExpr c = engine.evaluate(F.Coefficient(rest, dyx));
      a[k] = c;
      rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(c, dyx))));
    }
    IExpr c0 = engine.evaluate(F.Coefficient(rest, yFunction));
    a[0] = c0;
    rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(c0, yFunction))));

    // Everything mentioning the unknown must have been read into a coefficient. What is left over
    // is the right hand side, and if that still contains the unknown the equation is not linear.
    for (int k = 0; k <= n; k++) {
      if (!a[k].isFree(head, true)) {
        return null;
      }
    }
    if (!rest.isFree(head, true)) {
      return null;
    }
    boolean constant = true;
    for (int k = 0; k <= n; k++) {
      if (!a[k].isFree(xVar)) {
        constant = false;
        break;
      }
    }
    return new LinearODEForm(n, a, engine.evaluate(F.Negate(rest)), constant);
  }

  /**
   * The highest order in which <code>head</code> applied to <code>xVar</code> is differentiated,
   * <code>0</code> if it occurs undifferentiated only and <code>-1</code> if it does not occur.
   *
   * <p>
   * This reads the orders off the expression instead of testing every order up to a fixed limit
   * with {@link org.matheclipse.core.expression.S#Coefficient}, which cost one evaluation per order
   * on every dispatch.
   */
  static int highestDerivativeOrder(IExpr expr, IExpr head, IExpr xVar) {
    if (!expr.isAST()) {
      return -1;
    }
    IAST[] deriv = expr.isDerivativeAST1();
    if (deriv != null && deriv[2] != null && deriv[1].isAST1() && deriv[1].arg1().equals(head)
        && deriv[2].isAST1() && deriv[2].arg1().equals(xVar)) {
      return ODEUtils.derivativeOrder(deriv);
    }
    if (expr.isAST1() && expr.head().equals(head) && expr.first().equals(xVar)) {
      return 0;
    }
    int max = -1;
    IAST ast = (IAST) expr;
    for (int i = 0; i < ast.size(); i++) {
      int order = highestDerivativeOrder(ast.get(i), head, xVar);
      if (order > max) {
        max = order;
      }
    }
    return max;
  }

  /**
   * The coefficient matrices <code>M</code>, <code>N</code> and the vector <code>b</code> of a
   * system <code>M.Y' + N.Y == b</code>.
   *
   * <p>
   * As for a single equation, every term which mentions an unknown has to end up in a coefficient.
   * Without that check the system <code>{y'(x) == E^z(x) + 1, z'(x) == y(x) - x}</code> was read as
   * if <code>E^z(x)</code> were a forcing function, and an answer was built from a system which
   * bears no relation to the one which was asked about.
   *
   * @return <code>null</code> if the system is not linear in the unknowns
   */
  static IExpr[] extractSystem(IAST residuals, IAST unknowns, IExpr xVar, EvalEngine engine) {
    int n = unknowns.argSize();
    if (residuals.argSize() != n) {
      return null;
    }
    IASTAppendable m = F.ListAlloc(n);
    IASTAppendable nMatrix = F.ListAlloc(n);
    IASTAppendable b = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr rest = engine.evaluate(F.ExpandAll(residuals.get(i)));
      IASTAppendable mRow = F.ListAlloc(n);
      IASTAppendable nRow = F.ListAlloc(n);
      for (int j = 1; j <= n; j++) {
        IExpr derivative = engine.evaluate(F.D(unknowns.get(j), xVar));
        IExpr coefficient = engine.evaluate(F.Coefficient(rest, derivative));
        mRow.append(coefficient);
        rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(coefficient, derivative))));
      }
      for (int j = 1; j <= n; j++) {
        IExpr unknown = unknowns.get(j);
        IExpr coefficient = engine.evaluate(F.Coefficient(rest, unknown));
        nRow.append(coefficient);
        rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(coefficient, unknown))));
      }
      for (int j = 1; j <= n; j++) {
        IExpr head = unknowns.get(j).head();
        if (!rest.isFree(head, true)) {
          return null;
        }
        for (int k = 1; k <= n; k++) {
          if (!mRow.get(k).isFree(head, true) || !nRow.get(k).isFree(head, true)) {
            return null;
          }
        }
      }
      m.append(mRow);
      nMatrix.append(nRow);
      b.append(engine.evaluate(F.Negate(rest)));
    }
    return new IExpr[] {m, nMatrix, b};
  }

  /**
   * Whether <code>residual</code> is linear in every one of the <code>unknowns</code> which occurs
   * in it. An unknown the equation does not mention at all cannot make it nonlinear.
   */
  static boolean isLinearIn(IExpr residual, IAST unknowns, IExpr xVar, EvalEngine engine) {
    for (int i = 1; i <= unknowns.argSize(); i++) {
      IExpr unknown = unknowns.get(i);
      if (!residual.isFree(unknown.head(), true)
          && extract(residual, unknown, xVar, engine) == null) {
        return false;
      }
    }
    return true;
  }
}
