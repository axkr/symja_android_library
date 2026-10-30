package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reduce.CylindricalAlgebraicDecomposition;

/**
 *
 *
 * <pre>
 * CylindricalDecomposition(formula, {x1, x2, ...})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the real solutions of the formula of polynomial equations and inequalities as a formula in
 * which <code>x1</code> has constant bounds, <code>x2</code> bounds in <code>x1</code> and so on.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; CylindricalDecomposition(x^2 + y^2 &lt; 1, {x, y})
 * -1&lt;x&lt;1&amp;&amp;-Sqrt(1-x^2)&lt;y&lt;Sqrt(1-x^2)
 * </pre>
 */
public class CylindricalDecomposition extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST3()) {
      // `1` is not a valid CylindricalDecomposition output specification.
      return Errors.printMessage(S.CylindricalDecomposition, "cadop", F.List(ast.arg3()), engine);
    }
    IExpr variables = ast.arg2().isList() ? ast.arg2() : F.List(ast.arg2());
    if (variables.argSize() == 0 || !((IAST) variables).forAll(x -> x.isVariable())) {
      return F.NIL;
    }
    IExpr statement = ast.arg1().isList() ? ((IAST) ast.arg1()).setAtCopy(0, S.And) : ast.arg1();
    IExpr result = F.NIL;
    if (new VariablesSet(statement).getVarList().forAll(((IAST) variables)::contains)) {
      result = CylindricalAlgebraicDecomposition.reduce(statement, (IAST) variables, engine);
      if (result.isNIL()) {
        result = engine.evaluate(F.ternaryAST3(S.Reduce, statement, variables, S.Reals));
      }
    }
    if (result.isNIL() || result.isAST(S.Reduce)) {
      // `1` is not a logical formula consisting of polynomial equations and inequalities in `2`
      // with rational number coefficients.
      return Errors.printMessage(S.CylindricalDecomposition, "nrtpi",
          F.List(ast.arg1(), ast.arg2()), engine);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
