package org.matheclipse.core.builtin;

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
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class MinMaxFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.ArgMax.setEvaluator(new ArgMax());
			F.ArgMin.setEvaluator(new ArgMin());
			F.Maximize.setEvaluator(new Maximize());
			F.Minimize.setEvaluator(new Minimize());
			F.NMaximize.setEvaluator(new NMaximize());
			F.NMinimize.setEvaluator(new NMinimize());
		}
	}

	private static class ArgMax extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr x = ast.arg2();
			if (x.isSymbol() || (x.isAST() && !x.isList())) {
				IExpr result = maximize(ast.topHead(), ast.arg1(), x, engine);
				if (result.isList() && result.last().isList()) {
					IAST subList = (IAST) result.last();
					if (subList.last().isRule()) {
						return subList.last().second();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	private static class ArgMin extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr x = ast.arg2();
			if (x.isSymbol() || (x.isAST() && !x.isList())) {
				IExpr result = minimize(ast.topHead(), ast.arg1(), x, engine);
				if (result.isList() && result.last().isList()) {
					IAST subList = (IAST) result.last();
					if (subList.last().isRule()) {
						return subList.last().second();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	private final static class Maximize extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				IExpr unaryFunction = ast.arg1();
				IExpr x = ast.arg2();
				ISymbol head = ast.topHead();
				if (x.isSymbol() || (x.isAST() && !x.isList())) {
					return maximize(head, unaryFunction, x, engine);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	private final static class Minimize extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				IExpr unaryFunction = ast.arg1();
				IExpr x = ast.arg2();
				ISymbol head = ast.topHead();
				if (x.isSymbol() || (x.isAST() && !x.isList())) {
					return minimize(head, unaryFunction, x, engine);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	/**
	 * <pre>
	 * NMaximize(maximize_function, constraints, variables_list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the <code>NMaximize</code> function provides an implementation of
	 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a> for solving
	 * linear optimization problems with linear equality and inequality constraints and implicit non-negative variables.
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
	private final static class NMaximize extends NMinimize {

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
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
								new LinearConstraintSet(constraints), GoalType.MAXIMIZE,
								new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

	}

	/**
	 * <pre>
	 * NMinimize(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the <code>NMinimize</code> function provides an implementation of
	 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a> for solving
	 * linear optimization problems with linear equality and inequality constraints and implicit non-negative variables.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear programming</a></li>
	 * </ul>
	 * <p>
	 * See also: <a href="LinearProgramming.md">LinearProgramming</a>, <a href="NMaximize.md">NMaximize</a>
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NMinimize({-2*x+y-5, x+2*y&lt;=6 &amp;&amp; 3*x + 2*y &lt;= 12}, {x, y})
	 * {-13.0,{x-&gt;4.0,y-&gt;0.0}
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
	private static class NMinimize extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// switch to numeric calculation
			return numericEval(ast, engine);
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
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
								new LinearConstraintSet(constraints), GoalType.MINIMIZE,
								new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		protected static LinearObjectiveFunction getObjectiveFunction(VariablesSet vars, IExpr objectiveFunction) {
			Expr2LP x2LP = new Expr2LP(objectiveFunction, vars);
			return x2LP.expr2ObjectiveFunction();
		}

		protected static List<LinearConstraint> getConstraints(VariablesSet vars, IAST listOfconstraints) {
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>(listOfconstraints.size());
			listOfconstraints.forEach(x -> {
				Expr2LP x2LP = new Expr2LP(x, vars);
				constraints.add(x2LP.expr2Constraint());
			});
			return constraints;
		}

		protected static IExpr simplexSolver(VariablesSet vars, LinearObjectiveFunction f,
				OptimizationData... optData) {
			try {
				SimplexSolver solver = new SimplexSolver();
				PointValuePair solution = solver.optimize(optData);
				double[] values = solution.getPointRef();
				IASTAppendable list = F.ListAlloc(values.length);
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

	private static IExpr maximize(ISymbol head, IExpr unaryFunction, IExpr x, EvalEngine engine) {
		VariablesSet varSet = new VariablesSet(unaryFunction);
		IAST vars = varSet.getVarList();
		if (vars.size() == 2 && vars.arg1().equals(x)) {
			try {
				IExpr yNInf = F.Limit.of(unaryFunction, F.Rule(x, F.CNInfinity));
				if (yNInf.isInfinity()) {
					engine.printMessage(head.toString() + ": the maximum cannot be found.");
					return F.List(F.CInfinity, F.List(F.Rule(x, F.CNInfinity)));
				}
				IExpr yInf = F.Limit.of(unaryFunction, F.Rule(x, F.CInfinity));
				if (yInf.isInfinity()) {
					engine.printMessage(head.toString() + ": the maximum cannot be found.");
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
				return engine.printMessage(head.toString() + ": exception occured:" + rex.getMessage());
			}
		}
		return engine.printMessage(head.toString() + ": only unary functions in " + x + " are supported.");
	}

	private final static IExpr minimize(ISymbol head, IExpr unaryFunction, IExpr x, EvalEngine engine) {
		VariablesSet varSet = new VariablesSet(unaryFunction);
		IAST vars = varSet.getVarList();
		if (vars.size() == 2 && vars.arg1().equals(x)) {
			try {
				IExpr yNInf = F.Limit.of(unaryFunction, F.Rule(x, F.CNInfinity));
				if (yNInf.isNegativeInfinity()) {
					engine.printMessage(head.toString() + ": the maximum cannot be found.");
					return F.List(F.CNInfinity, F.List(F.Rule(x, F.CNInfinity)));
				}
				IExpr yInf = F.Limit.of(unaryFunction, F.Rule(x, F.CInfinity));
				if (yInf.isNegativeInfinity()) {
					engine.printMessage(head.toString() + ": the maximum cannot be found.");
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
				return engine.printMessage(head.toString() + ": exception occured:" + rex.getMessage());
			}
		}
		return engine.printMessage(head.toString() + ": only unary functions in " + x + " are supported.");
	}

	public static void initialize() {
		Initializer.init();
	}

	private MinMaxFunctions() {

	}

}
