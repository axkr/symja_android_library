package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.reflection.system.rules.InverseLaplaceTransformRules;

import java.util.function.Predicate;

/**
 * <pre>
 * InverseLaplaceTransform(f, s, t)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * returns the inverse laplace transform.
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
 * &gt;&gt; InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)
 * 3*E^t+2*Cos(2*t)
 * </pre>
 */

public class InverseLaplaceTransform extends AbstractFunctionEvaluator implements InverseLaplaceTransformRules {
	public InverseLaplaceTransform() {
	}

	/**
	 * See: <a href="http://www.solitaryroad.com/c913.html">Inverse Laplace transforms</a>
	 * 
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		IExpr a1 = ast.arg1();
		IExpr s = ast.arg2();
		IExpr t = ast.arg3();
		if (!s.isList() && !t.isList() && !s.equals(t)) {
			if (a1.isFree(s)) {
				return F.Times(a1, F.DiracDelta(t));
			}
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.isTimes()) {
					IASTAppendable result = F.TimesAlloc(arg1.size());
					IASTAppendable rest = F.TimesAlloc(arg1.size());
					arg1.filter(result, rest, new Predicate<IExpr>() {
                        @Override
                        public boolean test(IExpr x) {
                            return x.isFree(s);
                        }
                    });
					if (result.size() > 1) {
						return F.Times(result.getOneIdentity(F.C1), F.InverseLaplaceTransform(rest, s, t));
					}
				}
				if (arg1.isTimes() || arg1.isPower()) {
					IExpr[] parts = Algebra.fractionalParts(arg1, false);
					if (parts != null) {
						IExpr temp = Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
								s);
						if (temp.isPlus()) {
							return ((IAST) temp).mapThread(F.InverseLaplaceTransform(F.Null, s, t), 1);
						}
					}
				}
				if (arg1.isPlus()) {
					// InverseLaplaceTransform[a_+b_+c_,s_,t_] ->
					// InverseLaplaceTransform[a,s,t]+InverseLaplaceTransform[b,s,t]+InverseLaplaceTransform[c,s,t]
					return arg1.mapThread(F.InverseLaplaceTransform(F.Null, s, t), 1);
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
