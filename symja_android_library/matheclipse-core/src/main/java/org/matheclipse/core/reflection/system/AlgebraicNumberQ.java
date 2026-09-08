package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.AlgebraicNumberUtils;

/**
 * AlgebraicNumberQ(x)
 *
 * <p>
 * Gives True if x is an explicit algebraic number, and False otherwise.
 */
public class AlgebraicNumberQ extends AbstractFunctionEvaluator {

  public AlgebraicNumberQ() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return F.booleSymbol(AlgebraicNumberUtils.isExplicitAlgebraicNumber(ast.arg1()));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
  }
}
