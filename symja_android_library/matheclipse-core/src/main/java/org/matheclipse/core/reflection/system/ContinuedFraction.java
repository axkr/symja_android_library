package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ContinuedFraction(number)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get the continued fraction representation of <code>number</code>.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Continued_fraction">Wikipedia - Continued fraction</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FromContinuedFraction({2,3,4,5})
 * 157/68
 * 
 * &gt;&gt; ContinuedFraction(157/68)
 * {2,3,4,5} 
 * 
 * &gt;&gt;&gt; ContinuedFraction(45/16)
 * {2,1,4,3}
 * </pre>
 */
public class ContinuedFraction extends AbstractEvaluator {

	public ContinuedFraction() {

	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();

		int maxIterations = Integer.MAX_VALUE;
		if (ast.isAST2() && ast.arg2().isInteger()) {
			maxIterations = Validate.checkIntType(ast, 2);
		}

		if (arg1 instanceof INum) {
			// arg1 = F.fraction(((INum) arg1).getRealPart());
			return realToCF(((INum) arg1), maxIterations);
		} else if (arg1.isAST() || arg1.isSymbol() && arg1.isNumericFunction()) {
			IExpr num = engine.evalN(arg1);
			if (num instanceof INum) {
				// arg1 = F.fraction(((INum) num).getRealPart());
				return realToCF(((INum) num), maxIterations);
			}
		}

		if (arg1.isRational()) {
			IRational rat = (IRational) arg1;

			IASTAppendable continuedFractionList;
			if (rat.getDenominator().isOne()) {
				continuedFractionList = F.ListAlloc(1);
				continuedFractionList.append(rat.getNumerator());
			} else if (rat.getNumerator().isOne()) {
				continuedFractionList = F.ListAlloc(2);
				continuedFractionList.append(F.C0);
				continuedFractionList.append(rat.getDenominator());
			} else {
				IFraction temp = F.fraction(rat.getNumerator(), rat.getDenominator());
				IInteger quotient;
				IInteger remainder;
				continuedFractionList = F.ListAlloc(10);
				while (temp.getDenominator().compareInt(1) > 0 && (0 < maxIterations--)) {
					quotient = temp.getNumerator().div(temp.getDenominator());
					remainder = temp.getNumerator().mod(temp.getDenominator());
					continuedFractionList.append(quotient);
					temp = F.fraction(temp.getDenominator(), remainder);
					if (temp.getDenominator().isOne()) {
						continuedFractionList.append(temp.getNumerator());
					}
				}
			}
			return continuedFractionList;

		}

		return F.NIL;
	}

	private static IAST realToCF(INum d, int limit) {
		final double D = d.getRealPart();
		IASTAppendable continuedFractionList = F.ListAlloc(10);
		int ip = (int) D;
		if (d.isNumIntValue()) {
			continuedFractionList.append(F.ZZ((int) D));
			return continuedFractionList;
		}

		int aNow = ip;
		double tNow = D - aNow;
		double tNext;
		int aNext;
		continuedFractionList.append(F.ZZ(aNow));
		for (int i = 0; i < limit-1; i++) {
			double rec = 1.0 / tNow;
			aNext = (int) rec;
			tNext = rec - aNext;
			if (aNext==Integer.MAX_VALUE)  {
				break;
			}
			continuedFractionList.append(F.ZZ(aNext));
			tNow = tNext;
		}
		return continuedFractionList;

	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDREST);
	}

}
