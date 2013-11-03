package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 *  
 */
public class Exponent extends AbstractFunctionEvaluator {

	public Exponent() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		IExpr form = ast.arg2();
		ISymbol sym = F.Max;
		if (ast.size() == 4) {
			sym = Validate.checkSymbolType(ast, 3);
		}
		IAST collector = F.ast(sym);

		IExpr expr = F.evalExpandAll(ast.arg1());
		if (expr.equals(F.C0)) {
			collector.add(F.CNInfinity);
		} else if (expr.isAST()) {
			IAST arg1 = (IAST) expr;
			final IPatternMatcher matcher = new PatternMatcher(form);

			if (arg1.isPower()) {
				if (matcher.apply(arg1.arg1())) {
					collector.add(arg1.arg2());
				} else {
					collector.add(F.C0);
				}
			} else if (arg1.isPlus()) {
				for (int i = 1; i < arg1.size(); i++) {
					if (arg1.get(i).isAtom()) {
						if (arg1.get(i).isSymbol()) {
							if (matcher.apply(arg1.get(i))) {
								collector.add(F.C1);
							} else {
								collector.add(F.C0);
							}
						} else {
							collector.add(F.C0);
						}
					} else if (arg1.get(i).isPower()) {
						IAST pow = (IAST) arg1.get(i);
						if (matcher.apply(pow.arg1())) {
							collector.add(pow.arg2());
						} else {
							collector.add(F.C0);
						}
					} else if (arg1.get(i).isTimes()) {
						IAST times = (IAST) arg1.get(i);
						boolean evaled = false;
						for (int j = 1; j < times.size(); j++) {
							if (times.get(j).isPower()) {
								IAST pow = (IAST) times.get(j);
								if (matcher.apply(pow.arg1())) {
									collector.add(pow.arg2());
									evaled = true;
									break;
								}
							}
						}
						if (!evaled) {
							collector.add(F.C0);
						}
					} else {
						collector.add(F.C0);
					}
				}
			} else if (arg1.isTimes()) {
				boolean evaled = false;
				for (int i = 1; i < arg1.size(); i++) {
					if (arg1.get(i).isPower()) {
						IAST pow = (IAST) arg1.get(i);
						if (matcher.apply(pow.arg1())) {
							collector.add(pow.arg2());
							evaled = true;
							break;
						}
					}
				}
				if (!evaled) {
					collector.add(F.C0);
				}
			}

		} else if (expr.isSymbol()) {
			final PatternMatcher matcher = new PatternMatcher(form);
			if (matcher.apply(expr)) {
				collector.add(F.C1);
			} else {
				collector.add(F.C0);
			}
		} else {
			collector.add(F.C0);
		}
		return collector;
	}

}