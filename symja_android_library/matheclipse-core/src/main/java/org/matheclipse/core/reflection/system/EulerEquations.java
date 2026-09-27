package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.calculus.Euler;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * <pre>
 * <code>EulerEquations(f, u(x), x)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * returns the Euler-Lagrange differential equation obeyed by <code>u(x)</code> derived from the
 * functional <code>f</code>, where <code>f</code> depends on the function <code>u(x)</code> and
 * its derivatives, as well as the independent variable <code>x</code>.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>VariationalD(f, u(x), x)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * returns the variational derivative of the integral of <code>f</code> with respect to
 * <code>u(x)</code>.
 * </p>
 * </blockquote>
 */
public class EulerEquations extends AbstractFunctionEvaluator {

  private final boolean equations;

  public EulerEquations() {
    this(true);
  }

  public EulerEquations(boolean equations) {
    this.equations = equations;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr lagrangian = ast.arg1();
    final boolean functionList = ast.arg2().isList();
    IAST funcs = functionList ? (IAST) ast.arg2() : F.List(ast.arg2());
    IAST vars = ast.arg3().isList() ? (IAST) ast.arg3() : F.List(ast.arg3());
    if (funcs.argSize() == 0 || vars.argSize() == 0) {
      return F.NIL;
    }
    try {
      IASTAppendable result = F.ListAlloc(funcs.argSize());
      for (int i = 1; i < funcs.size(); i++) {
        IExpr derivative = Euler.variationalD(lagrangian, funcs.get(i), vars);
        result.append(equations ? engine.evaluate(F.Equal(derivative, F.C0)) : derivative);
      }
      return functionList ? result : result.arg1();
    } catch (ValueError ve) {
      return Errors.printMessage(ast.topHead(), ve, engine);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }
}
