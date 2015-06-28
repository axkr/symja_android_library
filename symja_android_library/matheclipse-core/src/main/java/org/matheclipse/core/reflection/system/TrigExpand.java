package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Expands the argument of sine and cosine functions.
 * 
 * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities" >List
 * of trigonometric identities</a>
 */
public class TrigExpand implements IFunctionEvaluator {
	public TrigExpand() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr temp = ast.arg1();
		IExpr result = temp;
		while (temp != null) {
			result = evalExpandAll(temp);
			temp = null;
			if (result.isAST() && ((IAST) result).size() == 2) {
				if (result.getAt(1).isPlus()) {
					if (result.isSin()) {
						temp = expandSinPlus((IAST) result.getAt(1), 1);
					} else if (result.isCos()) {
						temp = expandCosPlus((IAST) result.getAt(1), 1);
					}
				} else if (result.getAt(1).isTimes()) {
					IAST timesAST = (IAST) result.getAt(1);
					if (timesAST.arg1().isInteger()) {
						IInteger iNum = (IInteger) timesAST.arg1();
						if (iNum.isGreaterThan(C0)) {
							IAST rest = F.Times();
							rest.addAll(timesAST, 2, timesAST.size());
							if (result.isSin()) {
								return Sum(Times(Times(Times(Power(CN1, Times(Plus(F.$s("i"), Times(CN1, C1)), C1D2)), Binomial(iNum, $s("i"))),
										Power(Cos(rest), Plus(iNum, Times(CN1, $s("i"))))), Power(Sin(rest), $s("i"))), List($s("i"), C1, iNum, C2));
							} else if (result.isCos()) {
								return Sum(Times(Times(Times(Power(CN1, Times(F.$s("i"), C1D2)), Binomial(iNum, $s("i"))), Power(Cos(rest), Plus(
										iNum, Times(CN1, $s("i"))))), Power(Sin(rest), $s("i"))), List($s("i"), C0, iNum, C2));
							}
						}
					}
				}
			}
			if (temp != null) {
				result = temp;
			}
		}
		return result;
	}

	private IExpr expandSinPlus(IAST plusAST, int startPosition) {
		if (startPosition > plusAST.size() - 2) {
			return null;
		}
		IAST result = Plus();
		if (startPosition == plusAST.size() - 2) {
			result.add(Times(Sin(plusAST.get(startPosition)), Cos(plusAST.get(startPosition + 1))));
			result.add(Times(Cos(plusAST.get(startPosition)), Sin(plusAST.get(startPosition + 1))));
		} else {
			result.add(Times(Sin(plusAST.get(startPosition)), expandCosPlus(plusAST, startPosition + 1)));
			result.add(Times(Cos(plusAST.get(startPosition)), expandSinPlus(plusAST, startPosition + 1)));
		}
		return result;
	}

	private IExpr expandCosPlus(IAST plusAST, int startPosition) {
		if (startPosition > plusAST.size() - 2) {
			return null;
		}
		IAST result = Plus();
		if (startPosition == plusAST.size() - 2) {
			result.add(Times(Cos(plusAST.get(startPosition)), Cos(plusAST.get(startPosition + 1))));
			result.add(Times(CN1, Sin(plusAST.get(startPosition)), Sin(plusAST.get(startPosition + 1))));
		} else {
			result.add(Times(Cos(plusAST.get(startPosition)), expandCosPlus(plusAST, startPosition + 1)));
			result.add(Times(CN1, Sin(plusAST.get(startPosition)), expandSinPlus(plusAST, startPosition + 1)));
		}
		return result;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
