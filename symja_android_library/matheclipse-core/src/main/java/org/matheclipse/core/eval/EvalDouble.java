package org.matheclipse.core.eval;

import org.matheclipse.core.basic.Alloc;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class EvalDouble {
	public static double eval(final double[] stack, final int top, final IExpr expr) {
		if (expr instanceof IAST) {
			return evalAST(stack, top, (IAST) expr);
		}
		if (expr instanceof ISignedNumber) {
			return ((ISignedNumber) expr).doubleValue();
		}
		if (expr instanceof ISymbol) {
			return evalSymbol(((ISymbol) expr));
		}
		throw new UnsupportedOperationException("EvalDouble#eval(): "+expr);
	}

	public static double evalAST(double[] stack, final int top, final IAST ast) {
		final ISymbol symbol = (ISymbol) ast.get(0);
		final IEvaluator module = symbol.getEvaluator();
		if (module instanceof INumeric) {
			int newTop = top;
			// fast evaluation path
			if (top + ast.size() >= stack.length) {
				Alloc alloc = Alloc.get();
				stack = alloc.vector(ast.size() + 50);
			}
			for (int i = 1; i < ast.size(); i++) {
				++newTop;
				stack[newTop] = eval(stack, newTop, ast.get(i));
			}
			return ((INumeric) module).evalReal(stack, newTop, ast.size() - 1);
		}
		// slow evaluation path
		final IExpr result = F.evaln(ast);
		if (result instanceof Num) {
			return ((Num) result).doubleValue();
		}
		throw new UnsupportedOperationException("EvalDouble#evalAST(): "+ast);
	}

	public static double evalSymbol(final ISymbol symbol) {
		if (symbol.hasLocalVariableStack()) {
			return ((ISignedNumber) symbol.get()).doubleValue();
		}
		final IEvaluator module = symbol.getEvaluator();
		if (module instanceof INumericConstant) {
			// fast evaluation path
			return ((INumericConstant) module).evalReal();
		}
		// slow evaluation path
		final IExpr result = F.evaln(symbol);
		if (result instanceof Num) {
			return ((Num) result).doubleValue();
		}
		throw new UnsupportedOperationException("EvalDouble#evalSymbol() - no value assigned for symbol: "+symbol);
	}
}
