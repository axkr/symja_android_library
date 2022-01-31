package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;

/**
 * Generate the horner scheme for univariate polynomials. See:
 * <a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia:Horner scheme</a>
 *
 * @deprecated use HornerForm
 */
@Deprecated
public class Horner extends AbstractFunctionEvaluator {

  @Deprecated
  public Horner() {}

  @Override
  @Deprecated
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isAST()) {
      IAST poly = (IAST) ast.arg1();
      VariablesSet eVar = new VariablesSet(ast.arg1());
      IAST variables = eVar.getVarList();
      if (variables.size() >= 2) {
        IExpr variable = variables.arg1();
        if (poly.isPlus()) {
          HornerScheme scheme = new HornerScheme();
          return scheme.generate(engine.isNumericMode(), poly, variable);
        }
      }
    }
    return ast.arg1();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
