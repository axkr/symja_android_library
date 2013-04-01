package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

import java.math.BigInteger;

/**
 * ContinuedFraction of a double or fraction number
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Continued_fraction">Continued
 * fraction</a>
 * 
 * @see org.matheclipse.core.reflection.system.FromContinuedFraction
 */
public class ContinuedFraction implements IFunctionEvaluator {

	public ContinuedFraction() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 2) {
			throw new WrongNumberOfArguments(functionList, 1, functionList.size() - 1);
		}
		IExpr arg1 = functionList.get(1);
		if (arg1 instanceof INum) {
			arg1 = F.fraction(((INum) arg1).getRealPart());
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
				while (temp.getDenominator().isGreaterThan(F.C1)) {
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
