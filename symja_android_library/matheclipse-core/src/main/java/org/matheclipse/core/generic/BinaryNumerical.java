package org.matheclipse.core.generic;

import com.duy.lambda.BiFunction;

import org.matheclipse.core.eval.DoubleStackEvaluator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Binary numerical function for functions like Plot3D
 * 
 * @see org.matheclipse.core.reflection.system.Plot3D
 */
public class BinaryNumerical implements BiFunction<IExpr, IExpr, IExpr> {
	final IExpr fun;

	final ISymbol variable1;

	final ISymbol variable2;

	final EvalEngine fEngine;

	public BinaryNumerical(final IExpr fn, final ISymbol v1, final ISymbol v2, final EvalEngine engine) {
		variable1 = v1;
		variable2 = v2;
		fun = fn;
		fEngine = engine;
	}

	@Override
	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		return fEngine.evalN(F.subst(fun, F.List(F.Rule(variable1, firstArg), F.Rule(variable2, secondArg))));
	}

	public double value(double x, double y) {
		double result = 0.0;
		try {
			fEngine.localStackCreate(variable1).push(Num.valueOf(x));
			fEngine.localStackCreate(variable2).push(Num.valueOf(y));
			final double[] stack = new double[10];
			result = DoubleStackEvaluator.eval(stack, 0, fun);
		} finally {
			// Deque<IExpr> localVariableStack = fEngine.localStack(variable2);
			fEngine.localStack(variable2).pop();
			// localVariableStack = fEngine.localStack(variable1);
			fEngine.localStack(variable1).pop();
		}
		return result;

	}

	public ComplexNum value(final ComplexNum z1, final ComplexNum z2) {
		final Object temp = apply(z1, z2);
		if (temp instanceof ComplexNum) {
			return (ComplexNum) temp;
		}
		if (temp instanceof Num) {
			return ComplexNum.valueOf(((Num) temp).getRealPart());
		}
		throw new ArithmeticException("Numerical complex value expected");
	}

}
