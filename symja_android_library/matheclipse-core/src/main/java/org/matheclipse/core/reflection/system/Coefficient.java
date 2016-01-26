package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExpVectorLong;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia -
 * Coefficient</a>
 */
public class Coefficient extends AbstractFunctionEvaluator {

	public Coefficient() {
	}

	private boolean setExponent(IAST list, IExpr expr, long[] exponents, long value) {
		for (int j = 1; j < list.size(); j++) {
			if (list.get(j).equals(expr)) {
				int ix = ExpVectorLong.indexVar(expr, list);
				exponents[ix] = value;
				return true;
			}
		}
		return false;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		IExpr arg2 = ast.arg2();
		// list of variable expressions extracted from the second argument
		IAST listOfVariables = null;
		// array of corresponding exponents for the list of variables
		long[] exponents = null;

		if (arg2.isTimes()) {
			// Times(x, y^a,...)
			IAST arg2AST = (IAST) arg2;
			VariablesSet eVar = new VariablesSet(arg2AST);
			listOfVariables = eVar.getVarList();
			exponents = new long[listOfVariables.size() - 1];
			for (int i = 0; i < exponents.length; i++) {
				exponents[i] = 0L;
			}
			for (int i = 1; i < arg2AST.size(); i++) {
				long value = 1L;
				IExpr a1 = arg2AST.get(i);
				if (arg2AST.get(i).isPower() && arg2AST.get(i).getAt(2).isInteger()) {
					a1 = arg2AST.get(i).getAt(1);
					IInteger ii = (IInteger) arg2AST.get(i).getAt(2);
					try {
						value = ii.toLong();
					} catch (ArithmeticException ae) {
						return F.UNEVALED;
					}
				}

				if (!setExponent(listOfVariables, a1, exponents, value)) {
					return F.UNEVALED;
				}
			}
		} else {
			listOfVariables = F.List();
			listOfVariables.add(arg2);
			exponents = new long[1];
			exponents[0] = 1;
		}

		try {
			long n = 1;
			if (ast.size() == 4) {
				if (ast.arg3().isNegativeInfinity()) {
					return F.C0;
				}
				n = Validate.checkLongType(ast.arg3());
				for (int i = 0; i < exponents.length; i++) {
					exponents[i] *= n;
				}
			}
			ExpVectorLong expArr = new ExpVectorLong(exponents);
			IExpr expr = F.evalExpandAll(ast.arg1());
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, listOfVariables,
					listOfVariables.size() - 1);
			ExprPolynomial poly = ring.create(expr, true);
			return poly.coefficient(expArr);
			// }
		} catch (Exception ae) {
			if (Config.DEBUG) {
				ae.printStackTrace();
			}
			return F.C0;
		}
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}