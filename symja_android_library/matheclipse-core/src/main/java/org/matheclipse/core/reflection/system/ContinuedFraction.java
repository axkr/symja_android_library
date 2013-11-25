package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * ContinuedFraction of a double or fraction number
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Continued_fraction">Continued fraction</a>
 * 
 * @see org.matheclipse.core.reflection.system.FromContinuedFraction
 */
public class ContinuedFraction implements IFunctionEvaluator {

	public ContinuedFraction() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		
		int maxIterations = Integer.MAX_VALUE;
		if (ast.size() == 3 && ast.arg2().isInteger()) {
			maxIterations = Validate.checkIntType(ast, 2);
		}
		
		if (arg1 instanceof INum) {
			arg1 = F.fraction(((INum) arg1).getRealPart());
		} else if (arg1.isAST() || arg1.isSymbol() && arg1.isNumericFunction()) {
			IExpr num = F.evaln(arg1);
			if (num instanceof INum) {
				arg1 = F.fraction(((INum) num).getRealPart());
			}
		}

		if (arg1.isRational()) {
			IRational rat = (IRational) arg1;

			IAST continuedFractionList = F.List();
			if (rat.getDenominator().equals(F.C1)) {
				continuedFractionList.add(rat.getNumerator());
			} else if (rat.getNumerator().equals(F.C1)) {
				continuedFractionList.add(F.C0);
				continuedFractionList.add(rat.getDenominator());
			} else {
				IFraction temp = F.fraction(rat.getNumerator(), rat.getDenominator());
				BigInteger quotient;
				BigInteger remainder;
				while (temp.getDenominator().isGreaterThan(F.C1) && (0 < maxIterations--)) {
					quotient = temp.getBigNumerator().divide(temp.getBigDenominator());
					remainder = temp.getBigNumerator().mod(temp.getBigDenominator());
					continuedFractionList.add(F.integer(quotient));
					temp = F.fraction(temp.getBigDenominator(), remainder);
					if (temp.getDenominator().equals(F.C1)) {
						continuedFractionList.add(temp.getNumerator());
					}
				}
			}
			return continuedFractionList;

		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {

	}

}
