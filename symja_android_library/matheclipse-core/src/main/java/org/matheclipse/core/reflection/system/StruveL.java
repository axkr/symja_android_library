package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.StruveLRules;

public class StruveL extends AbstractFunctionEvaluator implements StruveLRules {

	public StruveL() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();
		if (arg2.isZero()) {
			IExpr re = arg1.re();
			if (re.isMinusOne()) {
				// StruveL(n_,0):=Indeterminate/;Re(n)==(-1)
				return F.Indeterminate;
			}
			IExpr temp = re.greaterThan(F.CN1);
			if (temp.isTrue()) {
				// StruveL(n_,0):=0/;Re(n)>(-1)
				return F.C0;
			}
			if (temp.isFalse()) {
				// StruveL(n_,0):=ComplexInfinity/;Re(n)<(-1)
				return F.CComplexInfinity;
			}
		} else {
			IExpr negArg2 = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
			if (negArg2.isPresent()) {
				// StruveL(n_, arg2_)) := ((-(arg2)^n) StruveL(n,
				// negArg2))/negArg2^n
				return F.Times(F.CN1, F.Power(arg2, arg1), F.Power(negArg2, F.Negate(arg1)), F.StruveL(arg1, negArg2));
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
