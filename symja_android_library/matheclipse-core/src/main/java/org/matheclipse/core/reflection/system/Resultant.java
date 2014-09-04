package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 */
public class Resultant extends AbstractFunctionEvaluator {

	public Resultant() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);
		IExpr a = F.evalExpandAll(ast.arg1());
		IExpr b = F.evalExpandAll(ast.arg2());
		IExpr arg3 = ast.arg3();
		if (!arg3.isSymbol()) {
			// TODO allow multinomials
			return null;
		}
		ISymbol x = (ISymbol) arg3;
		IExpr aExp = F.eval(F.Exponent(a, x));
		IExpr bExp = F.eval(F.Exponent(b, x));
		if (b.isFree(x)) {
			return F.Power(b, aExp);
		}
		IExpr abExp = aExp.times(bExp);
		if (F.evalTrue(F.Less(aExp, F.Exponent(b, x)))) {
			return F.Times(F.Power(F.CN1, abExp), F.Resultant(b, a, x));
		}

		IExpr r = F.eval(F.PolynomialRemainder(a, b, x));
		// TODO optimize Together() function
		return F.Together(F.Times(F.Power(F.CN1, abExp), F.Power(F.Coefficient(b, x, bExp), F.Subtract(aExp, F.Exponent(r, x))),
				F.Resultant(b, r, x)));
		// try {
		// IAST result = F.List();
		// long degree1 = CoefficientList.univariateCoefficientList(a, (ISymbol) arg3, result);
		// if (degree1 >= Short.MAX_VALUE) {
		// throw new WrongArgumentType(ast, ast.arg1(), 1, "Polynomial degree" + degree1 + " is larger than: " + " - "
		// + Short.MAX_VALUE);
		// }
		// IAST resultListDiff = F.List();
		// long degree2 = CoefficientList.univariateCoefficientList(b, (ISymbol) arg3, resultListDiff);
		// if (degree2 >= Short.MAX_VALUE) {
		// throw new WrongArgumentType(ast, ast.arg1(), 1, "Polynomial degree" + degree2 + " is larger than: " + " - "
		// + Short.MAX_VALUE);
		// }
		// return resultant(result, resultListDiff);
		// } catch (JASConversionException jce) {
		// // toInt() conversion failed
		// if (Config.DEBUG) {
		// jce.printStackTrace();
		// }
		// }
		// return null;
	}

	public static IExpr resultant(IAST result, IAST resultListDiff) {
		// create sylvester matrix
		IAST sylvester = F.List();
		IAST row = F.List();
		IAST srow;
		final int n = resultListDiff.size() - 2;
		final int m = result.size() - 2;
		final int n2 = m + n;

		for (int i = result.size() - 1; i > 0; i--) {
			row.add(result.get(i));
		}
		for (int i = 0; i < n; i++) {
			// for each row
			srow = F.List();
			int j = 0;
			while (j < n2) {
				if (j < i) {
					srow.add(F.C0);
					j++;
				} else if (i == j) {
					for (int j2 = 1; j2 < row.size(); j2++) {
						srow.add(row.get(j2));
						j++;
					}
				} else {
					srow.add(F.C0);
					j++;
				}
			}
			sylvester.add(srow);
		}

		row = F.List();
		for (int i = resultListDiff.size() - 1; i > 0; i--) {
			row.add(resultListDiff.get(i));
		}
		for (int i = n; i < n2; i++) {
			// for each row
			srow = F.List();
			int j = 0;
			int k = n;
			while (j < n2) {
				if (k < i) {
					srow.add(F.C0);
					j++;
					k++;
				} else if (i == k) {
					for (int j2 = 1; j2 < row.size(); j2++) {
						srow.add(row.get(j2));
						j++;
						k++;
					}
				} else {
					srow.add(F.C0);
					j++;
					k++;
				}
			}
			sylvester.add(srow);
		}

		if (sylvester.size() == 1) {
			return null;
		}
		// System.out.println(sylvester);
		return F.eval(F.Det(sylvester));
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}