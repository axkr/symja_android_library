package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomialRing;

/**
 * 
 */
public class Resultant extends AbstractFunctionEvaluator {

	public Resultant() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);
		// TODO allow multinomials
		IExpr arg3 = Validate.checkSymbolType(ast, 3);
		ISymbol x = (ISymbol) arg3;
		IExpr a = F.evalExpandAll(ast.arg1());
		IExpr b = F.evalExpandAll(ast.arg2());
		ExprPolynomialRing ring = new ExprPolynomialRing(F.List(x));
		try {
			// check if a is a polynomial otherwise check ArithmeticException,
			// ClassCastException
			ring.create(a);
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(ast, a, 1, "Polynomial expected!");
		}
		try {
			// check if b is a polynomial otherwise check ArithmeticException,
			// ClassCastException
			ring.create(b);
			return F.Together(resultant(a, b, x, engine));
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(ast, b, 2, "Polynomial expected!");
		}
	}

	public IExpr resultant(IExpr a, IExpr b, ISymbol x, EvalEngine engine) {
		IExpr aExp = engine.evaluate(F.Exponent(a, x));
		IExpr bExp = engine.evaluate(F.Exponent(b, x));
		if (b.isFree(x)) {
			return F.Power(b, aExp);
		}
		IExpr abExp = aExp.times(bExp);
		if (engine.evalTrue(F.Less(aExp, bExp))) {
			return F.Times(F.Power(F.CN1, abExp), resultant(b, a, x, engine));
		}

		IExpr r = engine.evaluate(F.PolynomialRemainder(a, b, x));
		IExpr rExp = r;
		if (!r.isZero()) {
			rExp = engine.evaluate(F.Exponent(r, x));
		}
		return F.Times(F.Power(F.CN1, abExp), F.Power(F.Coefficient(b, x, bExp), F.Subtract(aExp, rExp)),
				resultant(b, r, x, engine));
	}

	// public static IExpr resultant(IAST result, IAST resultListDiff) {
	// // create sylvester matrix
	// IAST sylvester = F.List();
	// IAST row = F.List();
	// IAST srow;
	// final int n = resultListDiff.size() - 2;
	// final int m = result.size() - 2;
	// final int n2 = m + n;
	//
	// for (int i = result.size() - 1; i > 0; i--) {
	// row.add(result.get(i));
	// }
	// for (int i = 0; i < n; i++) {
	// // for each row
	// srow = F.List();
	// int j = 0;
	// while (j < n2) {
	// if (j < i) {
	// srow.add(F.C0);
	// j++;
	// } else if (i == j) {
	// for (int j2 = 1; j2 < row.size(); j2++) {
	// srow.add(row.get(j2));
	// j++;
	// }
	// } else {
	// srow.add(F.C0);
	// j++;
	// }
	// }
	// sylvester.add(srow);
	// }
	//
	// row = F.List();
	// for (int i = resultListDiff.size() - 1; i > 0; i--) {
	// row.add(resultListDiff.get(i));
	// }
	// for (int i = n; i < n2; i++) {
	// // for each row
	// srow = F.List();
	// int j = 0;
	// int k = n;
	// while (j < n2) {
	// if (k < i) {
	// srow.add(F.C0);
	// j++;
	// k++;
	// } else if (i == k) {
	// for (int j2 = 1; j2 < row.size(); j2++) {
	// srow.add(row.get(j2));
	// j++;
	// k++;
	// }
	// } else {
	// srow.add(F.C0);
	// j++;
	// k++;
	// }
	// }
	// sylvester.add(srow);
	// }
	//
	// if (sylvester.isAST0()) {
	// return null;
	// }
	// // System.out.println(sylvester);
	// return F.eval(F.Det(sylvester));
	// }

	@Override
	public void setUp(final ISymbol newSymbol)  {
		newSymbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(newSymbol);
	}
}