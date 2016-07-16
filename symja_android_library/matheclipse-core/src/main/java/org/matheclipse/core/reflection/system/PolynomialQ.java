package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import java.util.function.BiPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrder;

import edu.jas.poly.GenPolynomial;

/**
 * Returns <code>True</code>, if the given expression is a polynoomial object
 * 
 */
public class PolynomialQ extends AbstractFunctionEvaluator implements BiPredicate<IExpr, IExpr> {

	public PolynomialQ() {
	}

	/**
	 * Returns <code>True</code> if the given expression is a polynoomial
	 * object; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IAST variables;
		if (ast.arg2().isList()) {
			variables = (IAST) ast.arg2();
		} else {
			variables = List(ast.arg2());
		}
		return F.bool(ast.arg1().isPolynomial(variables));

	}

	/**
	 * 
	 * @param polnomialExpr
	 * @param variables
	 * @param numericFunction
	 * @return
	 * @deprecated use
	 *             <code>ExprPolynomialRing ring = new ExprPolynomialRing(variables); ExprPolynomial poly = ring.create(polnomialExpr);</code>
	 *             if possible.
	 */
	private static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final IAST variables,
			boolean numericFunction) {
		IExpr expr = F.evalExpandAll(polnomialExpr);
		int termOrder = ExprTermOrder.INVLEX;
		ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables, variables.size() - 1,
				new ExprTermOrder(termOrder), true);
		try {
			ExprPolynomial poly = ring.create(expr);
			ASTRange r = new ASTRange(variables, 1);
			JASIExpr jas = new JASIExpr(r.toList(), numericFunction);
			return jas.expr2IExprJAS(poly);
		} catch (RuntimeException ex) {

		}
		return null;
	}

	/**
	 * 
	 * @param polnomialExpr
	 * @param symbol
	 * @param numericFunction
	 * @return
	 * @deprecated use
	 *             <code>ExprPolynomialRing ring = new ExprPolynomialRing(symbol); ExprPolynomial poly = ring.create(polnomialExpr);</code>
	 *             if possible
	 */
	private static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final ISymbol symbol,
			boolean numericFunction) {
		return polynomial(polnomialExpr, List(symbol), numericFunction);
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

	@Override
	public boolean test(final IExpr firstArg, final IExpr secondArg) {
		IAST list;
		if (secondArg.isList()) {
			list = (IAST) secondArg;
		} else {
			list = List(secondArg);
		}
		return firstArg.isPolynomial(list);
	}
}
