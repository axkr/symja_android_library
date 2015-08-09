package org.matheclipse.core.eval.interfaces;

import org.apache.commons.math4.complex.Complex;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;

/**
 * Base class for functions with 1 argument (i.e. Sin, Cos...) with Attributes <i>Listable</i> and <i>NumericFunction</i>
 * 
 */
public abstract class AbstractTrigArg1 extends AbstractArg1 {

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return evaluateArg1(ast.arg1());
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = ast.arg1();
		if (arg1 instanceof INum) {
			if (arg1 instanceof ApfloatNum) {
				return e1ApfloatArg(((ApfloatNum) arg1).apfloatValue());
			}
			return e1DblArg(((Num) arg1).doubleValue());
		}
		if (arg1 instanceof IComplexNum) {
			if (arg1 instanceof ApcomplexNum) {
				return e1ApcomplexArg(((ApcomplexNum) arg1).apcomplexValue());
			}
			return e1ComplexArg(((ComplexNum) arg1).complexValue());
		}
		return evaluateArg1(arg1);
	}

	public IExpr e1DblArg(final double d) {
		return null;
	}

	public IExpr e1ComplexArg(final Complex c) {
		return null;
	}

	public IExpr evaluateArg1(final IExpr arg1) {
		return null;
	}
}
