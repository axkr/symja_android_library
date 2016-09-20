package org.matheclipse.core.builtin.constant;

import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
/**
 * The constant Degree converts to Pi/180 radians
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Degree_(angle)">Degree (angle)</a>
 * 
 */
public class Degree extends AbstractSymbolEvaluator implements ISignedNumberConstant {
	final static public double DEGREE = 0.017453292519943295769236907684886127134428718885417;

	public Degree() {
	}

	/**
	 * Constant Degree converted to Pi/180
	 */
	@Override
	public IExpr evaluate(final ISymbol symbol) {
		return Times(F.Pi, Power(F.integer(180), F.CN1));
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(DEGREE);
	}

	public double evalReal() {
		return DEGREE;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.CONSTANT);
	}
}
