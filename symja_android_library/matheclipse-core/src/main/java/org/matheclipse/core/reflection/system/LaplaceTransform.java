package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.LaplaceTransformRules;

/**
 * <pre>
 * LaplaceTransform(f, s, t)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * returns the laplace transform.
 * </p>
 * </blockquote>
 * <p>
 * See:
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Laplace_transform">Wikipedia - Laplace transform</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; LaplaceTransform(t^2*Exp(2+3*t), t, s)
 * (-2*E^2)/(3-s)^3
 * </pre>
 */
public class LaplaceTransform extends AbstractFunctionEvaluator implements LaplaceTransformRules {
	public LaplaceTransform() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		IExpr a1 = ast.arg1();
		IExpr t = ast.arg2();
		IExpr s = ast.arg3();
		if (!t.isList() && !s.isList() && !t.equals(s)) {
			if (a1.isFree(t)) {// && a1.isAtom()) {
				return F.Divide(a1, s);
			}
			if (a1.equals(t) && a1.isFree(s)) {
				return F.Power(s, F.CN2);
			}
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.isTimes()) {
					IASTAppendable result = F.TimesAlloc(arg1.size());
					IASTAppendable rest = F.TimesAlloc(arg1.size());
					arg1.filter(result, rest, x -> x.isFree(t));
					if (result.size() > 1) {
						return F.Times(result.getOneIdentity(F.C1), F.LaplaceTransform(rest, t, s));
					}
				}
				if (arg1.isPower() && arg1.base().equals(t)) {
					IExpr n = arg1.exponent();
					if (n.isAtom() && !n.isMinusOne()) {
						return F.Divide(F.Gamma(F.Plus(F.C1, n)), F.Power(s, F.Plus(F.C1, n)));
					}
				}
				if (arg1.isPlus()) {
					// LaplaceTransform[a_+b_+c_,t_,s_] ->
					// LaplaceTransform[a,t,s]+LaplaceTransform[b,t,s]+LaplaceTransform[c,t,s]
					return arg1.mapThread(F.LaplaceTransform(F.Null, t, s), 1);
				}
			}
		}
		return F.NIL;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
}
