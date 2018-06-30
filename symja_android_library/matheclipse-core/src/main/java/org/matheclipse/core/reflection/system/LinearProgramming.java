package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.Relationship;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import java.util.ArrayList;
import java.util.Collection;

import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * <pre>
 * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * the <code>LinearProgramming</code> function provides an implementation of
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
 * See also: <a href="NMaximize.md">NMaximize</a>, <a href="NMinimize.md">NMinimize</a>
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})
 * {4.0,0.0,1.0}
 * </pre>
 * <p>
 * solves the linear problem:
 * </p>
 * 
 * <pre>
 * Minimize -2x + y - 5
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
public class LinearProgramming extends AbstractFunctionEvaluator {

	public LinearProgramming() {
		super();
	}

	/**
	 * The LinearProgramming provides an implementation of
	 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a> for solving
	 * linear optimization problems with linear equality and inequality constraints.
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		// switch to numeric calculation
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);
		try {
			if (ast.arg1().isList() && ast.arg2().isList() && ast.arg3().isList()) {
				double[] arg1D = Expr2Object.toDoubleVector((IAST) ast.arg1());
				LinearObjectiveFunction f = new LinearObjectiveFunction(arg1D, 0);
				Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

				IAST arg2 = (IAST) ast.arg2();
				IAST arg3 = (IAST) ast.arg3();
				if (arg2.size() != arg3.size()) {
					return F.NIL;
				}
				double[] arg2D;
				double[] arg3D;
				for (int i = 1; i < arg2.size(); i++) {
					arg2D = Expr2Object.toDoubleVector((IAST) arg2.get(i));
					if (arg2.get(i).isList()) {
						if (arg3.get(i).isList()) {
							arg3D = Expr2Object.toDoubleVector((IAST) arg3.get(i));
							if (arg3D.length >= 2) {
								double val = arg3D[1];
								if (val == 0.0) {
									constraints.add(new LinearConstraint(arg2D, Relationship.EQ, arg3D[0]));
								} else if (val < 0.0) {
									constraints.add(new LinearConstraint(arg2D, Relationship.LEQ, arg3D[0]));
								} else if (val > 0.0) {
									constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
								}
							} else if (arg3D.length == 1) {
								constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
							}
						} else {
							ISignedNumber sn = arg3.get(i).evalReal();
							if (sn != null) {
								constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, sn.doubleValue()));
							} else {
								throw new WrongArgumentType(arg3, arg3.get(i), i, "Numeric vector or number expected!");
							}
						}
					} else {
						throw new WrongArgumentType(ast, ast.get(i), i, "Numeric vector expected!");
					}
				}
				SimplexSolver solver = new SimplexSolver();
				// PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
				PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints), GoalType.MINIMIZE,
						new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
				double[] values = solution.getPointRef();
				return F.List(values);
			}
		} catch (MathIllegalStateException oe) {
			throw new WrappedException(oe);
			// if (Config.SHOW_STACKTRACE) {
			// oe.printStackTrace();
			// }
		}

		return F.NIL;
	}
}