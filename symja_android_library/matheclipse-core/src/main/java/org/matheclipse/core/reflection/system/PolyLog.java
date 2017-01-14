package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.PolyLogRules;

public class PolyLog extends AbstractFunctionEvaluator implements PolyLogRules {

	public PolyLog() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();

		if (arg2.isZero()) {
			return F.C0;
		}
		if (arg2.isOne()) {
			IExpr temp = engine.evaluate(org.matheclipse.core.reflection.system.Re.evalRe(arg1, engine));
			if (temp.isSignedNumber()) {
				ISignedNumber num = (ISignedNumber) temp;
				if (num.isOne()) {
					return F.Indeterminate;
				} else if (num.isGreaterThan(F.C1)) {
					return F.Zeta(arg1);
				} else {
					return F.CComplexInfinity;
				}
			}
		} else if (arg2.isMinusOne()) {
			// (2^(1-arg1)-1)*Zeta(arg1)
			return Times(Plus(CN1, Power(C2, Plus(C1, Negate(arg1)))), Zeta(arg1));
		}

		if (arg1.isSignedNumber()) {
			if (arg1.isZero()) {
				// arg2/(1 - arg2)
				return Times(arg2, Power(Plus(C1, Negate(arg2)), -1));
			} else if (arg1.isOne()) {
				// -Log(1 - arg2))
				return Negate(Log(Plus(C1, Negate(arg2))));
			} else if (arg1.isMinusOne()) {
				// arg2/(arg2 - 1)^2
				return Times(arg2, Power(Plus(C1, Negate(arg2)), -2));
			} else if (arg1.equals(F.CN2)) {
				// -((arg2*(1 + arg2))/(arg2 - 1)^3)
				return Times(CN1, arg2, Plus(C1, arg2), Power(Plus(CN1, arg2), -3));
			} else if (arg1.equals(F.CN3)) {
				// (arg2*(1 + 4*arg2 + arg2^2))/(arg2 - 1)^4
				return Times(arg2, Plus(C1, Times(C4, arg2), Sqr(arg2)), Power(Plus(C1, Negate(arg2)), -4));
			}
		}
		return F.NIL;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}

}
