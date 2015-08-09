package org.matheclipse.core.builtin.function;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Complex extends AbstractFunctionEvaluator {
	public final static Complex CONST = new Complex();

	public Complex() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		try {
			final EvalEngine engine = EvalEngine.get();
			IExpr arg1 = ast.arg1();
			arg1 = engine.evaluate(arg1);
			IExpr arg2 = ast.arg2();
			arg2 = engine.evaluate(arg2);
			if (arg2.isComplex()) {
				if (((IComplex) arg2).getRealPart().equals(BigFraction.ZERO)) {
					arg2 = F.fraction(((IComplex) arg2).getImaginaryPart());
				}
			} else if (arg2.isComplexNumeric()) {
				if (F.isZero(((IComplexNum) arg2).getRealPart())) {
					arg2 = F.num(((IComplexNum) arg2).getImaginaryPart());
				}
			}
			if (arg1.isRational() && arg2.isRational()) {
				IFraction re;
				if (arg1.isInteger()) {
					re = F.fraction((IInteger) arg1, F.C1);
				} else {
					re = (IFraction) arg1;
				}
				IFraction im;
				if (arg2.isInteger()) {
					im = F.fraction((IInteger) arg2, F.C1);
				} else {
					im = (IFraction) arg2;
				}
				return F.complex(re, im);
			}
			if (arg1 instanceof INum && arg2 instanceof INum) {
				return F.complexNum(((INum) arg1).doubleValue(), ((INum) arg2).doubleValue());
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
