package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.differentiation.DSFactory;
import org.hipparchus.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * <code>ND(function, x, value)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>returns a numerical approximation of the partial derivative of the <code>function</code> for
 * the variable <code>x</code> and the given <code>value</code>.
 *
 * </blockquote>
 *
 * <pre>
 * <code>ND(function, {x, n} , value)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>returns a numerical approximation of the partial derivative of order <code>n</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; ND(BesselY(10.0,x), x, 1)
 * 1.20940*10^9
 *
 * &gt;&gt; ND(Cos(x)^3, {x,2}, 1)
 * 1.82226
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p><a href="D.md">D</a>, <a href="Integrate.md">Integrate</a>, <a
 * href="NIntegrate.md">NIntegrate</a>
 */
public class ND extends AbstractFunctionEvaluator {

  public ND() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    IExpr arg3 = ast.arg3();
    try {
      int dim = arg2.isVector();
      if (dim == 2) {
        int order = arg2.second().toIntDefault();
        if (order > 0 && arg2.first().isSymbol()) {
          return partialDerivative(arg1, (ISymbol) arg2.first(), order, arg3, engine);
        }
      } else if (arg2.isSymbol()) {
        return partialDerivative(arg1, (ISymbol) arg2, 1, arg3, engine);
      }
    } catch (MathRuntimeException mrex) {
      if (Config.SHOW_STACKTRACE) {
        mrex.printStackTrace();
      }
      return engine.printMessage(ast.topHead(), mrex);
    } catch (ValidateException ve) {
      if (Config.SHOW_STACKTRACE) {
        ve.printStackTrace();
      }
      return engine.printMessage(ast.topHead(), ve);
    }
    return F.NIL;
  }

  private IExpr partialDerivative(
      IExpr arg1, ISymbol arg2, int order, IExpr arg3, EvalEngine engine) {
    double a3Double = Double.NaN;
    try {
      a3Double = arg3.evalDouble();
    } catch (ValidateException ve) {
    }

    if (Double.isNaN(a3Double)) {

    } else {
      DSFactory factory = new DSFactory(1, order);
      FiniteDifferencesDifferentiator differentiator =
          new FiniteDifferencesDifferentiator(15, 0.01);
      UnivariateDifferentiableFunction f =
          differentiator.differentiate(new UnaryNumerical(arg1, arg2, EvalEngine.get()));

      return F.num(f.value(factory.variable(0, a3Double)).getPartialDerivative(order));
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }
}
