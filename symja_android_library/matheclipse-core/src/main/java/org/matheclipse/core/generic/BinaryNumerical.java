package org.matheclipse.core.generic;

import org.matheclipse.core.basic.Alloc;
import org.matheclipse.core.eval.EvalDouble;
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
public class BinaryNumerical extends BinaryFunctorImpl<IExpr> {
	IExpr fun;

	ISymbol variable1;

	ISymbol variable2;

	public BinaryNumerical(final IExpr fn, final ISymbol v1, final ISymbol v2) {
		variable1 = v1;
		variable2 = v2;
		fun = fn;
	}

	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		return F.evaln(F.subst(fun,F.List(F.Rule(variable1, firstArg), F.Rule(variable2, secondArg)) ));
	}

	public double value(double x, double y) {
		double result = 0.0;
		try {
			variable1.pushLocalVariable(Num.valueOf(x));
			variable2.pushLocalVariable(Num.valueOf(y));
			Alloc alloc = Alloc.get();
			final double[] stack = alloc.vector(10);
			result = EvalDouble.eval(stack, 0, fun);
			alloc.freeVector(10);
		} finally {
			variable2.popLocalVariable();
			variable1.popLocalVariable();
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
