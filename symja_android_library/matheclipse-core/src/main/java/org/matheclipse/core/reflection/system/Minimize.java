package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.convert.Expr2LP;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Minimize extends AbstractFunctionEvaluator {

	public Minimize() {
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
					if (yNInf.isNegativeInfinity()) {
						engine.printMessage("Minimize: the maximum cannot be found.");
						return F.List(F.CNInfinity, F.List(F.Rule(x, F.CNInfinity)));
					}
					IExpr yInf = F.Limit.of(unaryFunction, F.Rule(x, F.CInfinity));
					if (yInf.isNegativeInfinity()) {
						engine.printMessage("Minimize: the maximum cannot be found.");
						return F.List(F.CNInfinity, F.List(F.Rule(x, F.CInfinity)));
					}

					IExpr first_derivative = F.D.of(engine, unaryFunction, x);
					IExpr second_derivative = F.D.of(engine, first_derivative, x);
					IExpr candidates = F.Solve.of(engine, F.Equal(first_derivative, F.C0), x, F.Reals);
					if (candidates.isFree(F.Solve)) {
						IExpr minCandidate = F.NIL;
						IExpr minValue = F.CInfinity;
						if (candidates.isListOfLists()) {
							for (int i = 1; i < candidates.size(); i++) {
								IExpr candidate = ((IAST) candidates).get(i).first().second();
								IExpr value = engine.evaluate(F.subs(second_derivative, x, candidate));
								if (value.isPositive()) {
									IExpr functionValue = engine.evaluate(F.subs(unaryFunction, x, candidate));
									if (F.Less.ofQ(functionValue, minValue)) {
										minValue = functionValue;
										minCandidate = candidate;
									}
								}
							}
							if (minCandidate.isPresent()) {
								return F.List(minValue, F.List(F.Rule(x, minCandidate)));
							}
						}
						return F.CEmptyList;
					}
				} catch (RuntimeException rex) {
					return engine.printMessage("Minimize: exception occured:" + rex.getMessage());
				}
			}
			return engine.printMessage("Minimize: only unary functions in " + x + " are supported.");
		}
		return F.NIL;
	}

}