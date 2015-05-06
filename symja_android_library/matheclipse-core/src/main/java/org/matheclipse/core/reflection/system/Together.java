package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Determine a common denominator for the expressions of a sum.
 */
public class Together extends AbstractFunctionEvaluator {

	public static IExpr together(IAST ast) {
		IExpr temp = ExpandAll.expandAll(ast, null, true, false);
		if (temp == null) {
			temp = ast;
		}
		if (temp.isAST()) {
			IExpr result = togetherPlusTimesPower((IAST) temp);
			if (result != null) {
				return F.eval(result);
			}
		}
		return temp;
	}

	private static IAST togetherForEach(final IAST ast, IAST result) {
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				IExpr temp = togetherNull((IAST) ast.get(i));
				if (temp != null) {
					if (result == null) {
						result = ast.clone();
					}
					result.set(i, temp);
				}
			}
		}
		return result;
	}

	/**
	 * Do a <code>ExpandAll(ast)</code> and call <code>togetherAST</code> afterwards with the result..
	 * 
	 * @param ast
	 * @return <code>null</code> couldn't be transformed by <code>ExpandAll(()</code> od <code>togetherAST()</code>
	 */
	private static IExpr togetherNull(IAST ast) {
		boolean evaled = false;
		IExpr temp = ExpandAll.expandAll(ast, null, true, false);
		if (temp == null) {
			temp = ast;
		} else {
			evaled = true;
		}
		if (temp.isAST()) {
			IExpr result = togetherPlusTimesPower((IAST) temp);
			if (result != null) {
				return F.eval(result);
			}
		}
		if (evaled) {
			return temp;
		}
		return null;
	}

	private static IAST togetherPlus(IAST plusAST) {
		if (plusAST.size() <= 2) {
			return null;
		}
		IAST ni;
		IExpr temp;
		IAST numer = F.ast(F.Plus, plusAST.size(), false);
		IAST denom = F.ast(F.Times, plusAST.size(), false);

		IExpr[] parts;
		boolean evaled = false;
		for (int i = 1; i < plusAST.size(); i++) {
			parts = Apart.getFractionalPartsRational(plusAST.get(i));
			if (parts != null) {
				numer.add(i, parts[0]);
				temp = parts[1];
				if (!temp.isOne()) {
					evaled = true;
				}
				denom.add(i, temp);
			} else {
				numer.add(i, plusAST.get(i));
				denom.add(i, F.C1);
			}
		}
		if (!evaled) {
			return null;
		}
		for (int i = 1; i < plusAST.size(); i++) {
			ni = F.Times(numer.get(i));
			for (int j = 1; j < plusAST.size(); j++) {
				if (i == j) {
					continue;
				}
				temp = denom.get(j);
				if (!temp.isOne()) {
					ni.add(temp);
				}
			}
			numer.set(i, ni.getOneIdentity(F.C1));
		}
		int i = 1;
		while (denom.size() > i) {
			if (denom.get(i).isOne()) {
				denom.remove(i);
				continue;
			}
			i++;
		}
		if (denom.size() == 1) {
			return null;
		}

		temp = F.eval(numer.getOneIdentity(F.C0));
		IExpr exprNumerator = F.evalExpandAll(temp);
		// IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(exprNumerator);
		// if (negExpr != null) {
		// exprNumerator = negExpr;
		// denom.add(F.CN1);
		// }
		temp = F.eval(denom.getOneIdentity(F.C1));
		IExpr exprDenominator = F.evalExpandAll(temp);

		if (!exprDenominator.isOne()) {
			try {
				IExpr[] result = Cancel.cancelGCD(exprNumerator, exprDenominator);
				if (result != null) {
					IExpr pInv;
					if (result[2].isNumber()) {
						pInv = result[2].inverse();
					} else {
						pInv = F.Power(result[2], F.CN1);
					}
					if (result[0].isOne()) {
						return F.Times(pInv, result[1]);
					}
					return F.Times(result[0], result[1], pInv);
				}
				return null;
			} catch (JASConversionException jce) {
				if (Config.DEBUG) {
					jce.printStackTrace();
				}
			}
		}
		return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
	}

	private static IExpr togetherPlusTimesPower(final IAST ast) {
		if (ast.isPlus()) {
			IAST result = null;
			result = togetherForEach(ast, result);
			if (result != null) {
				return togetherPlus(result);
			}
			return togetherPlus(ast);
		} else if (ast.isTimes() || ast.isPower()) {
			try {
				IAST result = null;
				if (ast.isTimes()) {
					result = togetherForEach(ast, result);
				} else {
					// Power
					if (ast.arg1().isAST()) {
						IExpr temp = togetherNull((IAST) ast.arg1());
						if (temp != null) {
							if (result == null) {
								result = ast.clone();
							}
							result.set(1, temp);
						}
					}
				}
				if (result != null) {
					IExpr temp = F.eval(result);
					if (temp.isTimes() || temp.isPower()) {
						IExpr temp2 = Cancel.cancelPowerTimes((IAST) temp);
						if (temp2 != null) {
							return temp2;
						}
					}
					return temp;
				}
				return Cancel.cancelPowerTimes(ast);
			} catch (JASConversionException jce) {
				// if (Config.DEBUG) {
				jce.printStackTrace();
				// }
			}
		}

		return null;
	}

	public Together() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isAtom()) {
			return arg1;
		}
		if (arg1.isPlusTimesPower()) {
			IExpr temp = togetherNull((IAST) arg1);
			if (temp != null) {
				return temp;
			}
		}
		return arg1;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}