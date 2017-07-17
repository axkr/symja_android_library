package org.matheclipse.core.reflection.system;

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
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.convert.Expr2LP;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class NMinimize extends AbstractFunctionEvaluator {

	public NMinimize() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		// switch to numeric calculation
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList() && ast.arg2().isList()) {
			IAST list1 = (IAST) ast.arg1();
			IAST list2 = (IAST) ast.arg2();
			VariablesSet vars = new VariablesSet(list2);
			if (list1.isAST2()) {
				IExpr function = list1.arg1();
				IExpr listOfconstraints = list1.arg2();
				if (listOfconstraints.isAnd()) {
					// lc1 && lc2 && lc3...
					LinearObjectiveFunction objectiveFunction = getObjectiveFunction(vars, function);
					List<LinearConstraint> constraints = getConstraints(vars, listOfconstraints);
					return simplexSolver(vars, objectiveFunction, objectiveFunction,
							new LinearConstraintSet(constraints), GoalType.MINIMIZE, new NonNegativeConstraint(true),
							PivotSelectionRule.BLAND);
				}
			}
		}
		return F.NIL;
	}

	protected static LinearObjectiveFunction getObjectiveFunction(VariablesSet vars, IExpr objectiveFunction) {
		Expr2LP x2LP = new Expr2LP(objectiveFunction, vars);
		return x2LP.expr2ObjectiveFunction();
	}

	protected static List<LinearConstraint> getConstraints(VariablesSet vars, IExpr listOfconstraints) {
		Expr2LP x2LP;
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		IAST andAST = (IAST) listOfconstraints;
		for (int i = 1; i < andAST.size(); i++) {
			x2LP = new Expr2LP(andAST.get(i), vars);
			constraints.add(x2LP.expr2Constraint());
		}
		return constraints;
	}

	protected static IExpr simplexSolver(VariablesSet vars, LinearObjectiveFunction f, OptimizationData... optData) {
		try {
			SimplexSolver solver = new SimplexSolver();
			PointValuePair solution = solver.optimize(optData);
			double[] values = solution.getPointRef();
			IAST list = F.ListAlloc(values.length);
			List<IExpr> varList = vars.getArrayList();
			for (int i = 0; i < values.length; i++) {
				list.append(F.Rule(varList.get(i), F.num(values[i])));
			}
			IAST result = F.List(F.num(f.value(values)), list);
			return result;
		} catch (MathIllegalStateException oe) {
			throw new WrappedException(oe);
			// if (Config.SHOW_STACKTRACE) {
			// oe.printStackTrace();
			// }
		}
	}
}