package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Determine a common denominator for the expressions of a sum.
 */
public class Together extends AbstractFunctionEvaluator {
	public static final Together CONST = new Together();

	public static IExpr together(IAST ast) {
		IExpr temp = ExpandAll.expandAll(ast, null, true, false);
		if (!temp.isPresent()) {
			temp = ast;
		}
		if (temp.isAST()) {
			IExpr result = togetherPlusTimesPower((IAST) temp);
			if (result.isPresent()) {
				return F.eval(result);
			}
		}
		return temp;
	}

	/**
	 * Calls <code>Together</code> for each argument of the <code>ast</code>.
	 * 
	 * @param ast
	 * @return <code>F.NIL</code> if the <code>ast</code> couldn't be evaluated.
	 */
	private static IAST togetherForEach(final IAST ast) {
		IAST result = F.NIL;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				IExpr temp = togetherNull((IAST) ast.get(i));
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						result = ast.copy();
					}
					result.set(i, temp);
				}
			}
		}
		return result;
	}

	/**
	 * Do a <code>ExpandAll(ast)</code> and call <code>togetherAST</code>
	 * afterwards with the result..
	 * 
	 * @param ast
	 * @return <code>null</code> couldn't be transformed by
	 *         <code>ExpandAll(()</code> od <code>togetherAST()</code>
	 */
	private static IExpr togetherNull(IAST ast) {
		boolean evaled = false;
		IExpr temp = ExpandAll.expandAll(ast, null, true, false);
		if (!temp.isPresent()) {
			temp = ast;
		} else {
			evaled = true;
		}
		if (temp.isAST()) {
			IExpr result = togetherPlusTimesPower((IAST) temp);
			if (result.isPresent()) {
				return F.eval(result);
			}
		}
		if (evaled) {
			return temp;
		}
		return F.NIL;
	}

	/**
	 * 
	 * @param plusAST
	 *            a <code>Plus[...]</code> expresion
	 * @return <code>null</code> couldn't be transformed by
	 *         <code>ExpandAll(()</code> od <code>togetherAST()</code>
	 */
	private static IExpr togetherPlus(IAST plusAST) {
		if (plusAST.size() <= 2) {
			return F.NIL;
		}
		IAST numerator = F.ast(F.Plus, plusAST.size(), false);
		IAST denominator = F.ast(F.Times, plusAST.size(), false);
		boolean evaled = false;
		IExpr temp;
		IExpr[] fractionalParts;
		for (int i = 1; i < plusAST.size(); i++) {
			fractionalParts = Apart.getFractionalPartsRational(plusAST.get(i));
			if (fractionalParts != null) {
				numerator.add(i, fractionalParts[0]);
				temp = fractionalParts[1];
				if (!temp.isOne()) {
					evaled = true;
				}
				denominator.add(i, temp);
			} else {
				numerator.add(i, plusAST.get(i));
				denominator.add(i, F.C1);
			}
		}
		if (!evaled) {
			return F.NIL;
		}
		IAST ni;
		for (int i = 1; i < plusAST.size(); i++) {
			ni = F.Times(numerator.get(i));
			for (int j = 1; j < plusAST.size(); j++) {
				if (i == j) {
					continue;
				}
				temp = denominator.get(j);
				if (!temp.isOne()) {
					ni.add(temp);
				}
			}
			numerator.set(i, ni.getOneIdentity(F.C1));
		}
		int i = 1;
		while (denominator.size() > i) {
			if (denominator.get(i).isOne()) {
				denominator.remove(i);
				continue;
			}
			i++;
		}
		if (denominator.isAST0()) {
			return F.NIL;
		}

		IExpr exprNumerator = F.evalExpand(numerator.getOneIdentity(F.C0));
		IExpr exprDenominator = F.evalExpand(denominator.getOneIdentity(F.C1));

		if (!exprDenominator.isOne()) {
			try {
				IExpr[] result = Cancel.cancelGCD(exprNumerator, exprDenominator);
				if (result != null) {
					IExpr pInv = result[2].inverse();
					if (result[0].isOne()) {
						return F.Times(pInv, result[1]);
					}
					return F.Times(result[0], result[1], pInv);
				}
				return F.NIL;
			} catch (JASConversionException jce) {
				if (Config.DEBUG) {
					jce.printStackTrace();
				}
			}
			return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
		}
		return exprNumerator;
	}

	private static IExpr togetherPlusTimesPower(final IAST ast) {
		if (ast.isPlus()) {
			IAST result = togetherForEach(ast);
			if (result.isPresent()) {
				return togetherPlus(result).orElse(result);
			}
			return togetherPlus(ast);
		} else if (ast.isTimes() || ast.isPower()) {
			try {
				IAST result = F.NIL;
				if (ast.isTimes()) {
					result = togetherForEach(ast);
				} else {
					// Power
					if (ast.arg1().isAST()) {
						IExpr temp = togetherNull((IAST) ast.arg1());
						if (temp.isPresent()) {
							if (!result.isPresent()) {
								result = ast.copy();
							}
							result.set(1, temp);
						}
					}
				}
				if (result.isPresent()) {
					IExpr temp = F.eval(result);
					if (temp.isTimes() || temp.isPower()) {
						return Cancel.cancelPowerTimes(temp).orElse(temp);
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

		return F.NIL;
	}

	public Together() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		IAST temp = Thread.threadLogicEquationOperators(arg1, ast, 1);
		if (temp.isPresent()) {
			return temp;
		}
		if (arg1.isPlusTimesPower()) {
			return togetherNull((IAST) arg1).orElse(arg1);
		}
		return arg1;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}