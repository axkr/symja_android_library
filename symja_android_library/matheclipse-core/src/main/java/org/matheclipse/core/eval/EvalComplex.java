package org.matheclipse.core.eval;

import org.matheclipse.core.basic.Alloc;
import org.matheclipse.core.eval.interfaces.INumericComplex;
import org.matheclipse.core.eval.interfaces.INumericComplexConstant;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.eval.util.DoubleStack;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class EvalComplex {
	public static double[] eval(final DoubleStack stack, final int top, final IExpr expr) {
		if (expr instanceof IAST) {
			return evalAST(stack, top, (IAST) expr);
		}
		if (expr instanceof ISignedNumber) {
			final double[] result = Alloc.vector(2);
			result[0] = ((ISignedNumber) expr).doubleValue();
			result[1] = 0.0;
			return result;
		}
		if (expr instanceof ComplexNum) {
			final double[] res = Alloc.vector(2);
			res[0] = ((ComplexNum) expr).getRealPart();
			res[1] = ((ComplexNum) expr).getImaginaryPart();
			return res;
		}
		if (expr instanceof ISymbol) {
			return evalSymbol(((ISymbol) expr));
		}
		throw new UnsupportedOperationException();
	}

	public static double[] evalAST(final DoubleStack stack, final int top, final IAST ast) {
		final int newTop = top;
		final ISymbol symbol = (ISymbol) ast.get(0);
		final IEvaluator module = symbol.getEvaluator();
		if (module instanceof INumericComplex) {
			// fast evaluation path
			stack.ensureCapacity(top + ast.size() * 2);
			for (int i = 1; i < ast.size(); i++) {
				final double[] result = eval(stack, newTop, ast.get(i));
				stack.push(result[0]);
				stack.push(result[1]);
			}
			return ((INumericComplex) module).evalComplex(stack, ast.size() - 1);
		}
		// slow evaluation path
		final IExpr result = F.evaln(ast);
		if (result instanceof ComplexNum) {
			final double[] res = Alloc.vector(2);
			res[0] = ((ComplexNum) result).getRealPart();
			res[1] = ((ComplexNum) result).getImaginaryPart();
			return res;
		}
		if (result instanceof Num) {
			final double[] res = Alloc.vector(2);
			res[0] = ((Num) result).doubleValue();
			res[1] = 0.0;
			return res;
		}
		throw new UnsupportedOperationException();
	}

	public static double[] evalSymbol(final ISymbol symbol) {
		if (symbol.hasLocalVariableStack()) {
			final IExpr expr = symbol.get();
			if (expr instanceof ISignedNumber) {
				final double[] result = Alloc.vector(2);
				result[0] = ((ISignedNumber) expr).doubleValue();
				result[1] = 0.0;
				return result;
			}
			if (expr instanceof ComplexNum) {
				final double[] result = Alloc.vector(2);
				result[0] = ((ComplexNum) expr).getRealPart();
				result[1] = ((ComplexNum) expr).getImaginaryPart();
				return result;
			}
		}
		final IEvaluator module = symbol.getEvaluator();
		if (module instanceof INumericConstant) {
			// fast evaluation path
			final double[] result = Alloc.vector(2);
			result[0] = ((INumericConstant) module).evalReal();
			result[1] = 0.0;
			return result;
		} else if (module instanceof INumericComplexConstant) {
			// fast evaluation path
			return ((INumericComplexConstant) module).evalComplex();
		}

		// slow evaluation path
		final IExpr result = F.evaln(symbol);
		if (result instanceof ComplexNum) {
			final double[] res = Alloc.vector(2);
			res[0] = ((ComplexNum) result).getRealPart();
			res[1] = ((ComplexNum) result).getImaginaryPart();
			return res;
		}
		if (result instanceof Num) {
			final double[] res = Alloc.vector(2);
			res[0] = ((Num) result).doubleValue();
			res[1] = 0.0;
			return res;
		}
		throw new UnsupportedOperationException();
	}
}
