package org.matheclipse.core.reflection.system;

import java.util.List;

import org.apache.commons.math4.optim.linear.LinearConstraint;
import org.apache.commons.math4.optim.linear.LinearConstraintSet;
import org.apache.commons.math4.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math4.optim.linear.NonNegativeConstraint;
import org.apache.commons.math4.optim.linear.PivotSelectionRule;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class NMaximize extends NMinimize {

	public NMaximize() {
		super();
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
							GoalType.MAXIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
				}
			}
		}
		return null;
	}

}