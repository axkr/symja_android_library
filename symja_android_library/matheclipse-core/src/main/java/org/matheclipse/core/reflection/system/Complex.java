package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Complex extends AbstractFunctionEvaluator {
	public final static Complex CONST = new Complex();

	public Complex() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3)) {
			try {
				final EvalEngine engine = EvalEngine.get();
				IExpr arg0 = ast.get(1);
				arg0 = engine.evaluate(arg0);
				IExpr arg1 = ast.get(2);
				arg1 = engine.evaluate(arg1);
				if (arg0.isRational() && arg1.isRational()) {
					IFraction re;
					if (arg0.isInteger()) {
						re = F.fraction((IInteger) arg0, F.C1);
					} else {
						re = (IFraction) arg0;
					}
					IFraction im;
					if (arg1.isInteger()) {
						im = F.fraction((IInteger) arg1, F.C1);
					} else {
						im = (IFraction) arg1;
					}
					return F.complex(re, im);
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
