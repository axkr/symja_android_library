package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Maximize extends AbstractFunctionEvaluator {

	public Maximize() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 3 && !ast.arg2().isList()) {

			IExpr unaryFunction = ast.arg1();
			VariablesSet varSet = new VariablesSet(unaryFunction);
			IAST vars = varSet.getVarList();
			IExpr x = ast.arg2();
			if (vars.size() == 2 && vars.arg1().equals(x)) {
				try {
					IExpr yNInf = F.Limit.of(unaryFunction, F.Rule(x, F.CNInfinity));
					if (yNInf.isInfinity()) {
						engine.printMessage("Maximize: the maximum cannot be found.");
						return F.List(F.CInfinity, F.List(F.Rule(x, F.CNInfinity)));
					}
					IExpr yInf = F.Limit.of(unaryFunction, F.Rule(x, F.CInfinity));
					if (yInf.isInfinity()) {
						engine.printMessage("Maximize: the maximum cannot be found.");
						return F.List(F.CInfinity, F.List(F.Rule(x, F.CInfinity)));
					}

					IExpr first_derivative = F.D.of(engine, unaryFunction, x);
					IExpr second_derivative = F.D.of(engine, first_derivative, x);
					IExpr candidates = F.Solve.of(engine, F.Equal(first_derivative, F.C0), x, F.Reals);
					if (candidates.isFree(F.Solve)) {
						IExpr maxCandidate = F.NIL;
						IExpr maxValue = F.CNInfinity;
						if (candidates.isListOfLists()) {
							for (int i = 1; i < candidates.size(); i++) {
								IExpr candidate = ((IAST) candidates).get(i).first().second();
								IExpr value = engine.evaluate(F.subs(second_derivative, x, candidate));
								if (value.isNegative()) {
									IExpr functionValue = engine.evaluate(F.subs(unaryFunction, x, candidate));
									if (F.Greater.ofQ(functionValue, maxValue)) {
										maxValue = functionValue;
										maxCandidate = candidate;
									}
								}
							}
							if (maxCandidate.isPresent()) {
								return F.List(maxValue, F.List(F.Rule(x, maxCandidate)));
							}
						}
						return F.CEmptyList;
					}
				} catch (RuntimeException rex) {
					return engine.printMessage("Maximize: exception occured:" + rex.getMessage());
				}
			}
			return engine.printMessage("Maximize: only unary functions in " + x + " are supported.");
		}
		return F.NIL;
	}

}