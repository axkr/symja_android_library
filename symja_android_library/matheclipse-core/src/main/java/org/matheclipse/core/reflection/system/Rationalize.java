package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Rationalize extends AbstractFunctionEvaluator {

	public Rationalize() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		IExpr arg1 = ast.arg1();
		try {
			// try to convert into a fractional number
			final EvalEngine engine = EvalEngine.get();
			arg1 = engine.evaluate(arg1);
			if (arg1.isRational()) {
				return arg1;
			}
			if (arg1 instanceof INum) {
				return F.fraction(((INum) arg1).getRealPart());
			}
			if (arg1 instanceof IComplexNum) {
				return F.complex(((IComplexNum) arg1).getRealPart(), ((IComplexNum) arg1).getImaginaryPart());
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
		symbol.setAttributes(ISymbol.HOLDALL|ISymbol.LISTABLE);
	}
}
