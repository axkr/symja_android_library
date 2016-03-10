package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.LaplaceTransformRules;

/**
 * See: <a href=""> Wikipedia:Laplace Transform</a>
 * 
 */
public class LaplaceTransform extends AbstractFunctionEvaluator implements LaplaceTransformRules {
	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public LaplaceTransform() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		IExpr a1 = ast.arg1();
		IExpr t = ast.arg2();
		IExpr s = ast.arg3();
		if (!t.equals(s)) {
			if (a1.isFree(t) && a1.isAtom()) {
				return F.Divide(a1, s);
			}
			if (a1.equals(t) && a1.isFree(s)) {
				return F.Power(s, F.CN2);
			}
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.isPower() && arg1.arg1().equals(t) && arg1.arg2().isAtom()) {
					IExpr n = arg1.arg2();
					return F.Divide(F.Gamma(F.Plus(F.C1, n)), F.Power(s, F.Plus(F.C1, n)));
				}
				if (arg1.isPlus()) {
					// LaplaceTransform[a_+b_+c_,t_,s_] ->
					// LaplaceTransform[a,t,s]+LaplaceTransform[b,t,s]+LaplaceTransform[c,t,s]
					return arg1.mapAt(F.LaplaceTransform(F.Null, t, s), 1);
				}
			}
		}
		return F.NIL;
	}
}
