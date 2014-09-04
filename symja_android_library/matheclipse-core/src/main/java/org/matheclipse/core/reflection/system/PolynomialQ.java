package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.BiPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.Polynomial;

import edu.jas.poly.GenPolynomial;

/**
 * Returns <code>True</code>, if the given expression is a polynoomial object
 * 
 */
public class PolynomialQ extends AbstractFunctionEvaluator implements BiPredicate<IExpr> {

	public PolynomialQ() {
	}

	/**
	 * Returns <code>True</code> if the given expression is a polynoomial object; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		IAST list;
		if (ast.arg2().isList()) {
			list = (IAST) ast.arg2();
		} else {
			list = List(ast.arg2());
		}
		// return F.bool(polynomialQ(ast.arg1(), list));
		return F.bool(polynomialQ(ast.arg1(), list));

	}

	// public static boolean isPolynomial(final IExpr polnomialExpr, final IAST variables) {
	// IExpr expr = F.evalExpandAll(polnomialExpr);
	// Polynomial poly = new Polynomial(expr, variables, null, false);
	// return poly.isPolynomial(expr);
	// // return isPolynomial(expr, variables, false, true);
	// }

	public static boolean polynomialQ(final IExpr polnomialExpr, final IAST variables) {
		IExpr expr = F.evalExpandAll(polnomialExpr);
		Polynomial poly = new Polynomial(expr, variables, null, false);
		return poly.isPolynomial(expr);
		// try {
		// IExpr expr = F.evalExpandAll(polnomialExpr);
		// ASTRange r = new ASTRange(variables, 1);
		// JASIExpr jas = new JASIExpr(r.toList(), new ExprRingFactory());
		// return jas.expr2IExprJAS(expr) != null;
		// } catch (JASConversionException e) {
		// // exception will be thrown if the expression is not a JAS polynomial
		// }
		// return false;
	}

	public static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final IAST variables, boolean numericFunction) {
		IExpr expr = F.evalExpandAll(polnomialExpr);
//		ASTRange r = new ASTRange(variables, 1);
//		JASIExpr jas = new JASIExpr(r.toList(), numericFunction);
//		try {
//			return jas.expr2IExprJAS(expr);
//		} catch (JASConversionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
		Polynomial poly = new Polynomial(expr, variables, null, false);
		if (poly.createPolynomial(expr, true, true)) {
			ASTRange r = new ASTRange(variables, 1);
			JASIExpr jas = new JASIExpr(r.toList(), numericFunction);
			return jas.expr2IExprJAS(poly);
		}
		return null;
	}

	public static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final ISymbol symbol, boolean numericFunction) {
		return polynomial(polnomialExpr, List(symbol), numericFunction);
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

	public boolean apply(final IExpr firstArg, final IExpr secondArg) {
		IAST list;
		if (secondArg.isList()) {
			list = (IAST) secondArg;
		} else {
			list = List(secondArg);
		}
		return polynomialQ(firstArg, list);
	}
}
