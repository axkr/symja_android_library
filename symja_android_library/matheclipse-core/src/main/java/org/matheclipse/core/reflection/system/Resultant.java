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
 * <pre>
 * Resultant(polynomial1, polynomial2, var)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * computes the resultant of the polynomials <code>polynomial1</code> and <code>polynomial2</code> with respect to the
 * variable <code>var</code>.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Resultant">Wikipedia - Resultant</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Resultant((x-y)^2-2 , y^3-5, y)
 * 17-60*x+12*x^2-10*x^3-6*x^4+x^6
 * </pre>
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
		IExpr a = F.evalExpandAll(ast.arg1(), engine);
		IExpr b = F.evalExpandAll(ast.arg2(), engine);
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
		IExpr aExp =  F.Exponent.of(engine, a, x);
		IExpr bExp =  F.Exponent.of(engine,b, x);
		if (b.isFree(x)) {
			return F.Power(b, aExp);
		}
		IExpr abExp = aExp.times(bExp);
		if (F.Less.ofQ(engine, aExp, bExp)) {
			return F.Times(F.Power(F.CN1, abExp), resultant(b, a, x, engine));
		}

		IExpr r =  F.PolynomialRemainder.of(engine,a, b, x);
		IExpr rExp = r;
		if (!r.isZero()) {
			rExp =  F.Exponent.of(engine,r, x);
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
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}