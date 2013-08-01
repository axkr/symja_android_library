package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Hyperbolic cotangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
 */
public class Coth extends AbstractTrigArg1 {

	public Coth() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Coth(Times(CN1, arg1)));
		}
		return null;
	}
  
	@Override
  public void setUp(final ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    super.setUp(symbol);
  }
}
