package org.matheclipse.core.reflection.system;

import java.util.HashMap;
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
	private static Map<IExpr, IExpr> INVERSE_FUNCTIONS = new HashMap<IExpr, IExpr>();

	static {
		INVERSE_FUNCTIONS.put(F.Cos, F.ArcCos);
		INVERSE_FUNCTIONS.put(F.Sin, F.ArcSin);
		INVERSE_FUNCTIONS.put(F.Tan, F.ArcTan);
		
		INVERSE_FUNCTIONS.put(F.ArcCos, F.Cos);
		INVERSE_FUNCTIONS.put(F.ArcSin, F.Sin);
		INVERSE_FUNCTIONS.put(F.ArcTan, F.Tan);
		
		INVERSE_FUNCTIONS.put(F.Cosh, F.ArcCosh);
		INVERSE_FUNCTIONS.put(F.Sinh, F.ArcSinh);
		INVERSE_FUNCTIONS.put(F.Tanh, F.ArcTanh);
		
		INVERSE_FUNCTIONS.put(F.ArcCosh, F.Cosh);
		INVERSE_FUNCTIONS.put(F.ArcSinh, F.Sinh);
		INVERSE_FUNCTIONS.put(F.ArcTanh, F.Tanh);
	}

	public InverseFunction() {

	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		IExpr inverseFunction = INVERSE_FUNCTIONS.get(arg1);
		if (inverseFunction != null) {
			return inverseFunction;
		}
		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL);
	}
	
}