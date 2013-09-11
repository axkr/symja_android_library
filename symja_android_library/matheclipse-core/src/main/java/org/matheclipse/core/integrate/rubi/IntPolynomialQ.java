package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.BiPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns <code>True</code>, if the given expression is a polynoomial object
 * 
 */
public class IntPolynomialQ extends AbstractFunctionEvaluator implements BiPredicate<IExpr> {

	public IntPolynomialQ() {
	}

	/**
	 * Returns <code>True</code> if the given expression is a polynoomial
	 * object; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		
		IAST list;
		if (ast.get(2).isList()) {
			list = (IAST) ast.get(2);
		} else {
			list = List(ast.get(2));
		}
		
		// PolynomialQ[u_List,x_Symbol] :=
		// MapAnd[PolynomialQ,u,x]
		if (ast.get(1).isList() && ast.get(2).isSymbol()) {
			IAST list1 = (IAST) ast.get(1);
			for (int i = 1; i < list1.size(); i++) {
				if (!polynomialQ(list1.get(i), list)) {
					return F.False;
				}
			}
			return F.True;
		}
		
		return F.bool(polynomialQ(ast.get(1), list));

	}

	public static boolean polynomialQ(final IExpr polnomialExpr, final IAST variables) {
		try {
			IExpr expr = F.evalExpandAll(polnomialExpr);
			ASTRange r = new ASTRange(variables, 1);
			JASConvert<IExpr> jas = new JASConvert<IExpr>(r.toList(), new ExprRingFactory());
			return jas.expr2IExprJAS(expr) != null;
		} catch (JASConversionException e) {
			// exception will be thrown if the expression is not a JAS
			// polynomial
		}
		return false;
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
