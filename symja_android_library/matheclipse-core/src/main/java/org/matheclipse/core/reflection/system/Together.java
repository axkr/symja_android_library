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
 * 
 */
public class Together extends AbstractFunctionEvaluator {

	private static IExpr togetherAST(final IAST ast) {
		if (ast.isPlus()) {
			IAST result = togetherPlus(ast);
			if (result != null) {
				return together(result);
			}
			return null;
		} else if (ast.isTimes() || ast.isPower()) {
			try {
				IAST result = null;
				if (ast.isTimes()) {
					for (int i = 1; i < ast.size(); i++) {
						if (ast.get(i).isAST()) {
							IExpr temp = togetherAST((IAST) ast.get(i));
							if (temp != null) {
								if (result == null) {
									result = ast.clone();
								}
								result.set(i, temp);
							}
						}
					}
				} else {
					if (ast.arg1().isAST()) {
						IExpr temp = togetherAST((IAST) ast.arg1());
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
				if (!temp.equals(F.C1)) {
					ni.add(temp);
				}
			}
			numer.set(i, ni);
		}
		int i = 1;
		while (denom.size() > i) {
			if (denom.equalsAt(i, F.C1)) {
				denom.remove(i);
				continue;
			}
			i++;
		}
		// System.out.println(numer.toString());
		IExpr exprNumerator = F.evalExpandAll(numer);
		if (denom.size() == 1) {
			return null;
		}
		IExpr exprDenominator = F.evalExpandAll(denom);
		if (!exprDenominator.equals(F.C1)) {
			try {
				IExpr[] result = Cancel.cancelGCD(exprNumerator, exprDenominator);
				if (result != null) {
					if (result[0].isOne()) {
						return F.Times(result[1], F.Power(result[2], F.CN1));
					}
					return F.Times(result[0], result[1], F.Power(result[2], F.CN1));
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

	public Together() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isAtom()) {
			return arg1;
		}
		if (arg1.isAST()) {
			IExpr temp = ExpandAll.expandAll(arg1, null);// F.evalExpandAll(arg1);
			if (temp == null) {
				temp = arg1;
			}
			if (temp.isAST()) {
				// IExpr expr = together((IAST) arg1);
				// IExpr temp = arg1;
				IExpr result = null;
				// boolean evaled = false;
				// while (true) {
				result = togetherAST((IAST) temp);
				if (result != null) {
					result = F.eval(result);
					if (result.isPlus() || result.isTimes() || result.isPower()) {
						// evaled = true;
						temp = result;
						// continue;
					}
					return result;
				} else {
					// if (evaled) {
					return temp;
					// }
					// return arg1;
				}
				// }
			}
		}
		return arg1;
	}

	public static IExpr together(IAST ast) {
		IExpr temp = ExpandAll.expandAll(ast, null);// F.evalExpandAll(arg1);
		if (temp == null) {
			temp = ast;
		}
		if (temp.isAST()) {
			IExpr result = null;
			result = togetherAST((IAST) temp);
			if (result != null) {
				result = F.eval(result);
				if (result.isPlus() || result.isTimes() || result.isPower()) {
					temp = result;
				}
				return result;
			}
		}
		return temp;
		// IExpr result = togetherAST(ast);
		// if (result != null) {
		// return F.eval(result);
		// }
		// return ast;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}