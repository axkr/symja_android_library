package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class Complex extends AbstractCoreFunctionEvaluator {
	public final static Complex CONST = new Complex();

	public Complex() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		try {
			IExpr realExpr = ast.arg1();
			realExpr = engine.evaluate(realExpr);
			IExpr imaginaryExpr = ast.arg2();
			imaginaryExpr = engine.evaluate(imaginaryExpr);
			if (imaginaryExpr.isComplex()) {
				if (((IComplex) imaginaryExpr).getRealPart().isZero()) {
					imaginaryExpr = ((IComplex) imaginaryExpr).getImaginaryPart();
				}
			} else if (imaginaryExpr.isComplexNumeric()) {
				if (F.isZero(((IComplexNum) imaginaryExpr).getRealPart())) {
					imaginaryExpr = F.num(((IComplexNum) imaginaryExpr).getImaginaryPart());
				}
			}
			if (realExpr.isRational() && imaginaryExpr.isRational()) {
				IRational re;
				if (realExpr.isInteger()) {
					re = (IInteger) realExpr; // F.fraction((IInteger) arg1, F.C1);
				} else {
					re = (IFraction) realExpr;
				}
				IRational im;
				if (imaginaryExpr.isInteger()) {
					im = (IInteger) imaginaryExpr; // F.fraction((IInteger) arg2, F.C1);
				} else {
					im = (IFraction) imaginaryExpr;
				}
				return F.complex(re, im);
			}
			if (realExpr instanceof INum && imaginaryExpr instanceof INum) {
				return F.complexNum(((INum) realExpr).doubleValue(), ((INum) imaginaryExpr).doubleValue());
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
