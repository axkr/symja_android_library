package org.matheclipse.core.reflection.system;

import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import java.util.List;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * NMaximize(maximize_function, constraints, variables_list)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * the <code>NMaximize</code> function provides an implementation of
 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a> for solving linear
 * optimization problems with linear equality and inequality constraints and implicit non-negative variables.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear programming</a></li>
 * </ul>
 * <p>
 * See also: <a href="LinearProgramming.md">LinearProgramming</a>, <a href="NMinimize.md">NMinimize</a>
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; NMaximize({-2*x+y-5, x+2*y&lt;=6 &amp;&amp; 3*x + 2*y &lt;= 12 }, {x, y})
 * {-2.0,{x-&gt;0.0,y-&gt;3.0}}
 * </pre>
 * <p>
 * solves the linear problem:
 * </p>
 * 
 * <pre>
 * Maximize -2x + y - 5
 * </pre>
 * <p>
 * with the constraints:
 * </p>
 * 
 * <pre>
 *   x  + 2y &lt;=  6
 *   3x + 2y &lt;= 12
 *         x &gt;= 0
 *         y &gt;= 0
 * </pre>
 */
public class NMaximize extends NMinimize {

	public NMaximize() {
		super();
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
					List<LinearConstraint> constraints = getConstraints(vars, (IAST) listOfconstraints);
					return simplexSolver(vars, objectiveFunction, objectiveFunction,
							new LinearConstraintSet(constraints), GoalType.MAXIMIZE, new NonNegativeConstraint(true),
							PivotSelectionRule.BLAND);
				}
			}
		}
		return F.NIL;
	}

}