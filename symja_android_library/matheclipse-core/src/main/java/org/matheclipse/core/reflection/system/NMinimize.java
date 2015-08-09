package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.exception.MathIllegalStateException;
import org.apache.commons.math4.optim.OptimizationData;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.linear.LinearConstraint;
import org.apache.commons.math4.optim.linear.LinearConstraintSet;
import org.apache.commons.math4.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math4.optim.linear.NonNegativeConstraint;
import org.apache.commons.math4.optim.linear.PivotSelectionRule;
import org.apache.commons.math4.optim.linear.SimplexSolver;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.convert.Expr2LP;
import org.matheclipse.core.convert.VariablesSet;
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
	public IExpr evaluate(final IAST ast) {
		// switch to numeric calculation
		return numericEval(ast);
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList() && ast.arg2().isList()) {
			IAST list1 = (IAST) ast.arg1();
			IAST list2 = (IAST) ast.arg2();
			VariablesSet vars = new VariablesSet(list2);
			if (list1.size() == 3) {
				IExpr function = list1.arg1();
				IExpr listOfconstraints = list1.arg2();
				if (listOfconstraints.isAnd()) {
					// lc1 && lc2 && lc3...
					LinearObjectiveFunction objectiveFunction = getObjectiveFunction(vars, function);
					List<LinearConstraint> constraints = getConstraints(vars, listOfconstraints);
					return simplexSolver(vars, objectiveFunction, objectiveFunction, new LinearConstraintSet(constraints),
							GoalType.MINIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
				}
			}
		}
		return null;
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
			IAST result = F.List(f.value(values));
			result.add(F.List(values));
			return result;
		} catch (MathIllegalStateException oe) {
			throw new WrappedException(oe);
			// if (Config.SHOW_STACKTRACE) {
			// oe.printStackTrace();
			// }
		}
	}
}