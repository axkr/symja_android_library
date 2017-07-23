package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.FractionalPart;
import static org.matheclipse.core.expression.F.Negate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * FractionalPart(number)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get the fractional part of a <code>number</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FractionalPart(1.5)
 * 0.5
 * </pre>
 */
public class FractionalPart extends AbstractFunctionEvaluator {

	public FractionalPart() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isInteger()) {
			return F.C0;
		} else if (arg1.isFraction()) {
			IFraction fr = (IFraction) arg1;
			return fr.fractionalPart();
		}
		ISignedNumber signedNumber = arg1.evalSignedNumber();
		if (signedNumber != null) {
			return signedNumberFractionalPart(signedNumber);
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Negate(FractionalPart(negExpr));
		}
		return F.NIL;
	}

	private IExpr signedNumberFractionalPart(INumber arg1) {
		if (arg1.isInteger()) {
			return F.C0;
		} else if (arg1.isFraction()) {
			IFraction fr = (IFraction) arg1;
			return fr.fractionalPart();
		} else if (arg1 instanceof INum) {
			INum num = (INum) arg1;
			return F.num(num.getRealPart() % 1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}