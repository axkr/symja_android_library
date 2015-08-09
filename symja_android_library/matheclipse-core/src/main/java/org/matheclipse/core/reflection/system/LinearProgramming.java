package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math4.exception.MathIllegalStateException;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.linear.LinearConstraint;
import org.apache.commons.math4.optim.linear.LinearConstraintSet;
import org.apache.commons.math4.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math4.optim.linear.NonNegativeConstraint;
import org.apache.commons.math4.optim.linear.PivotSelectionRule;
import org.apache.commons.math4.optim.linear.Relationship;
import org.apache.commons.math4.optim.linear.SimplexSolver;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * The LinearProgramming provides an implementation of <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's
 * simplex algorithm</a> for solving linear optimization problems with linear equality and inequality constraints.
 */
public class LinearProgramming extends AbstractFunctionEvaluator {

	public LinearProgramming() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		// switch to numeric calculation
		return numericEval(ast);
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 4);
		try {
			if (ast.arg1().isList() && ast.arg2().isList() && ast.arg3().isList()) {
				double[] arg1D = Expr2Object.toDoubleVector((IAST) ast.arg1());
				LinearObjectiveFunction f = new LinearObjectiveFunction(arg1D, 0);
				Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

				IAST arg2 = (IAST) ast.arg2();
				IAST arg3 = (IAST) ast.arg3();
				if (arg2.size() != arg3.size()) {
					return null;
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
							ISignedNumber sn = arg3.get(i).evalSignedNumber();
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

		return null;
	}
}