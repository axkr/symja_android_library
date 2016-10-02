package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *  
 */
public class InverseFunction extends AbstractFunctionEvaluator {

	public InverseFunction() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		ISymbol arg1 = Validate.checkSymbolType(ast, 1);
		return getUnaryInverseFunction(arg1);
	}

	/**
	 * Get the inverse function symbol if possible.
	 * 
	 * @param headSymbol
	 *            the symbol which represents a function name (i.e.
	 *            <code>Cos, Sin, ArcSin,...</code>)
	 * @return <code>null</code> if there is no inverse function defined.
	 */
	public static IExpr getUnaryInverseFunction(ISymbol headSymbol) {
		return F.UNARY_INVERSE_FUNCTIONS.get(headSymbol);
	}

	/**
	 * Get a new constructed inverse function AST from the given
	 * <code>ast</code>, with empty arguments (i.e.
	 * <code>inverseAST.size()==1)</code>.
	 * 
	 * @param ast
	 *            the AST which represents a function (i.e.
	 *            <code>Cos(x), Sin(x), ArcSin(x),...</code>)
	 * @return <code>null</code> if there is no inverse function defined.
	 */
	public static IAST getUnaryInverseFunction(IAST ast) {
		IExpr expr = ast.head();
		if (expr.isSymbol()) {
			IExpr inverseSymbol = F.UNARY_INVERSE_FUNCTIONS.get(expr);
			if (inverseSymbol != null) {
				return F.ast(inverseSymbol);
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDALL);
	}

}