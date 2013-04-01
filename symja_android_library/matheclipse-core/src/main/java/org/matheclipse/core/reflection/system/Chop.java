package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Chop extends AbstractFunctionEvaluator {
	public final double DEFAULT_CHOP_DELTA = 10E-10;

	public Chop() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.get(1);
		double delta = DEFAULT_CHOP_DELTA;
		if (ast.size() == 3 && ast.get(2) instanceof INum) {
			delta = ((INum) ast.get(2)).getRealPart();
		}
		try {
			arg1 = F.eval(arg1);
			if (arg1 instanceof INum) {
				if (F.isZero(((INum) arg1).getRealPart(), delta)) {
					return F.C0;
				}
				return arg1;
			} else if (arg1 instanceof IComplexNum) {
				if (F.isZero(((IComplexNum) arg1).getRealPart(), delta)) {
					if (F.isZero(((IComplexNum) arg1).getImaginaryPart(), delta)) {
						return F.C0;
					}
					return F.complexNum(0.0, ((IComplexNum) arg1).getImaginaryPart());
				}
				if (F.isZero(((IComplexNum) arg1).getImaginaryPart(), delta)) {
					return F.complexNum(((IComplexNum) arg1).getRealPart(), 0.0);
				}
				return arg1;
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE);
	}
}
