package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is bound to a value.
 * 
 */
public class ValueQ extends AbstractFunctionEvaluator implements
    Predicate<IExpr> {
  /**
   * Constructor for the unary predicate
   */
  public final static ValueQ CONST = new ValueQ();

  public ValueQ() {
    // System.out.println(getClass().getCanonicalName());
  }

  /**
   * Returns <code>True</code> if the 1st argument is an atomic object;
   * <code>False</code> otherwise
   */
  @Override
  public IExpr evaluate(final IAST ast) {
  	Validate.checkSize(ast, 2);

    return F.bool(apply(ast.get(1)));
  }

  public boolean apply(final IExpr expr) {
    if (expr.isSymbol()) {
      return ((ISymbol) expr).isValue();
    }
    if (expr.isAST()) {
      return ((IAST) expr).topHead().isValue((IAST) expr);
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator#setUp(org
   * .matheclipse.core.interfaces.ISymbol)
   */
  @Override
  public void setUp(ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.HOLDALL);
  }

}
