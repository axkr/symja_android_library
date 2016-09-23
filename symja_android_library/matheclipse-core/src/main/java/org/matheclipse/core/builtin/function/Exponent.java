package org.matheclipse.core.builtin.function;

import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Get the exponent of a subexpression in a polynomial expression
 */
public class Exponent extends AbstractCoreFunctionEvaluator {

	public Exponent() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		final IExpr form = engine.evalPattern(ast.arg2());
		if (form.isList()) {
			return ((IAST) form).mapAt(ast, 2);
		}
		ISymbol sym = F.Max;
		if (ast.isAST3()) {
			final IExpr arg3 = engine.evaluate(ast.arg3());
			if (arg3.isSymbol()) {
				sym = (ISymbol) arg3;
			}
		}
		Set<IExpr> collector = new TreeSet<IExpr>();

		IExpr expr = F.evalExpandAll(ast.arg1());
		if (expr.isZero()) {
			collector.add(F.CNInfinity);
		} else if (expr.isAST()) {
			IAST arg1 = (IAST) expr;
			final IPatternMatcher matcher = new PatternMatcher(form);

			if (arg1.isPower()) {
				if (matcher.test(arg1.arg1())) {
					collector.add(arg1.arg2());
				} else {
					collector.add(F.C0);
				}
			} else if (arg1.isPlus()) {
				for (int i = 1; i < arg1.size(); i++) {
					if (arg1.get(i).isAtom()) {
						if (arg1.get(i).isSymbol()) {
							if (matcher.test(arg1.get(i))) {
								collector.add(F.C1);
							} else {
								collector.add(F.C0);
							}
						} else {
							collector.add(F.C0);
						}
					} else if (arg1.get(i).isPower()) {
						IAST pow = (IAST) arg1.get(i);
						if (matcher.test(pow.arg1())) {
							collector.add(pow.arg2());
						} else {
							collector.add(F.C0);
						}
					} else if (arg1.get(i).isTimes()) {
						timesExponent((IAST) arg1.get(i), matcher, collector);
					} else {
						collector.add(F.C0);
					}
				}
			} else if (arg1.isTimes()) {
				timesExponent(arg1, matcher, collector);
			}

		} else if (expr.isSymbol()) {
			final PatternMatcher matcher = new PatternMatcher(form);
			if (matcher.test(expr)) {
				collector.add(F.C1);
			} else {
				collector.add(F.C0);
			}
		} else {
			collector.add(F.C0);
		}
		IAST result = F.ast(sym);
		if (collector.size() == 0) {
			collector.add(F.C0);
		}
		for (IExpr exponent : collector) {
			result.append(exponent);
		}
		return result;
	}

	private void timesExponent(IAST timesAST, final IPatternMatcher matcher, Set<IExpr> collector) {
		boolean evaled = false;
		IExpr argi;
		for (int i = 1; i < timesAST.size(); i++) {
			argi = timesAST.get(i);
			if (argi.isPower()) {
				IAST pow = (IAST) timesAST.get(i);
				if (matcher.test(pow.arg1())) {
					collector.add(pow.arg2());
					evaled = true;
					break;
				}
			} else if (argi.isSymbol()) {
				if (matcher.test(argi)) {
					collector.add(F.C1);
					evaled = true;
					break;
				}
			}
		}
		if (!evaled) {
			collector.add(F.C0);
		}
	}

}