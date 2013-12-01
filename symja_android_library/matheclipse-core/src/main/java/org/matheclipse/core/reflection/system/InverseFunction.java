package org.matheclipse.core.reflection.system;

import java.util.IdentityHashMap;
import java.util.Map;

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
	private static Map<ISymbol, ISymbol> INVERSE_FUNCTIONS = new IdentityHashMap<ISymbol, ISymbol>();

	static {
		INVERSE_FUNCTIONS.put(F.Cos, F.ArcCos);
		INVERSE_FUNCTIONS.put(F.Cot, F.ArcCot);
		INVERSE_FUNCTIONS.put(F.Csc, F.ArcCsc);
		INVERSE_FUNCTIONS.put(F.Sec, F.ArcSec);
		INVERSE_FUNCTIONS.put(F.Sin, F.ArcSin);
		INVERSE_FUNCTIONS.put(F.Tan, F.ArcTan);

		INVERSE_FUNCTIONS.put(F.ArcCos, F.Cos);
		INVERSE_FUNCTIONS.put(F.ArcCot, F.Cot);
		INVERSE_FUNCTIONS.put(F.ArcCsc, F.Csc);
		INVERSE_FUNCTIONS.put(F.ArcSec, F.Sec);
		INVERSE_FUNCTIONS.put(F.ArcSin, F.Sin);
		INVERSE_FUNCTIONS.put(F.ArcTan, F.Tan);

		INVERSE_FUNCTIONS.put(F.Cosh, F.ArcCosh);
		INVERSE_FUNCTIONS.put(F.Coth, F.ArcCoth);
		INVERSE_FUNCTIONS.put(F.Csch, F.ArcCsch);
		INVERSE_FUNCTIONS.put(F.Sech, F.ArcSech);
		INVERSE_FUNCTIONS.put(F.Sinh, F.ArcSinh);
		INVERSE_FUNCTIONS.put(F.Tanh, F.ArcTanh);

		INVERSE_FUNCTIONS.put(F.ArcCosh, F.Cosh);
		INVERSE_FUNCTIONS.put(F.ArcCoth, F.Coth);
		INVERSE_FUNCTIONS.put(F.ArcCsch, F.Csch);
		INVERSE_FUNCTIONS.put(F.ArcSech, F.Sech);
		INVERSE_FUNCTIONS.put(F.ArcSinh, F.Sinh);
		INVERSE_FUNCTIONS.put(F.ArcTanh, F.Tanh);
	}

	public InverseFunction() {

	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		ISymbol arg1 = Validate.checkSymbolType(ast, 1);
		return getInverseFunction(arg1);
	}

	/**
	 * Get the inverse function symbol if possible.
	 * 
	 * @param headSymbol
	 *            the symbol which represents a function name (i.e. <code>Cos, Sin, ArcSin,...</code>)
	 * @return <code>null</code> if there is no inverse function defined.
	 */
	public static ISymbol getInverseFunction(ISymbol headSymbol) {
		return INVERSE_FUNCTIONS.get(headSymbol);
	}

	/**
	 * Get a new constructed inverse function AST from the given <code>ast</code>, with empty arguments (i.e.
	 * <code>inverseAST.size()==1)</code>.
	 * 
	 * @param ast
	 *            the AST which represents a function (i.e. <code>Cos(x), Sin(x), ArcSin(x),...</code>)
	 * @return <code>null</code> if there is no inverse function defined.
	 */
	public static IAST getInverseFunction(IAST ast) {
		IExpr expr = ast.head();
		if (expr.isSymbol()) {
			ISymbol inverseSymbol = INVERSE_FUNCTIONS.get((ISymbol) expr);
			if (inverseSymbol != null) {
				return F.ast(inverseSymbol);
			}
		}
		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL);
	}

}