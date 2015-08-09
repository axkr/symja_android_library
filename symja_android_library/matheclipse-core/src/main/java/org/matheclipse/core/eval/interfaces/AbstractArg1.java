package org.matheclipse.core.eval.interfaces;

import org.apache.commons.math4.complex.Complex;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluate a function with 1 argument.
 */
public abstract class AbstractArg1 extends AbstractFunctionEvaluator {

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		final IExpr arg1 = ast.arg1();
		final IExpr result = e1ObjArg(arg1);
		if (result != null) {
			return result;
		}

		// argument dispatching
		if (arg1 instanceof IAST) {
			return e1FunArg((IAST) arg1);
		}
		final int hier = ast.arg1().hierarchy();
		if (hier <= IExpr.INTEGERID) {
			if (hier <= IExpr.DOUBLECOMPLEXID) {
				if (hier == IExpr.DOUBLEID) {
					if (arg1 instanceof ApfloatNum) {
						return e1ApfloatArg(((ApfloatNum) arg1).apfloatValue());
					}
					return e1DblArg(((Num) arg1).doubleValue());
				}
				if (arg1 instanceof ApcomplexNum) {
					return e1ApcomplexArg(((ApcomplexNum) arg1).apcomplexValue());
				}
				return e1ComplexArg(((ComplexNum) arg1).complexValue());
			} else {
				return e1IntArg((IInteger) arg1);
			}
		} else {
			if (hier <= IExpr.COMPLEXID) {
				if (hier == IExpr.FRACTIONID) {
					return e1FraArg((IFraction) ast.arg1());
				}
				return e1ComArg((IComplex) ast.arg1());
			} else {
				if (hier == IExpr.SYMBOLID) {
					return e1SymArg((ISymbol) ast.arg1());
				}
			}
		} 

		return null;
	}

	public IExpr e1ObjArg(final IExpr o) {
		return null;
	}

	public IExpr e1DblArg(final double d) {
		return null;
	}

	public IExpr e1ApfloatArg(final Apfloat d) {
		return null;
	}

	public IExpr e1ComplexArg(final Complex c) {
		return null;
	}
	
	public IExpr e1ApcomplexArg(final Apcomplex c) {
		return null;
	}

	public IExpr e1IntArg(final IInteger i) {
		return null;
	}

	public IExpr e1FraArg(final IFraction f) {
		return null;
	}

	public IExpr e1ComArg(final IComplex c) {
		return null;
	}

	public IExpr e1SymArg(final ISymbol s) {
		return null;
	}

	public IExpr e1FunArg(final IAST f) {
		return null;
	}

}
