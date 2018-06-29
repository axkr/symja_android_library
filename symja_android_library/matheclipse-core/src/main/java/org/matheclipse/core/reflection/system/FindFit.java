package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * FindFit(list - of - data - points, function, parameters, variable)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * solve a least squares problem using the Levenberg-Marquardt algorithm.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia - Levenberg–Marquardt
 * algorithm</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, a*Log(b*x), {a, b}, x)
 * {a-&gt;1.54503,b-&gt;20.28258}
 * 
 * &gt;&gt; FindFit({{1,1},{2,4},{3,9},{4,16}}, a+b*x+c*x^2, {a, b, c}, x)
 * {a-&gt;0.0,b-&gt;0.0,c-&gt;1.0}
 * </pre>
 */
public class FindFit extends AbstractFunctionEvaluator {

	static class FindFitParametricFunction implements ParametricUnivariateFunction {
		EvalEngine engine;
		ISymbol x;
		IAST listOfSymbols;
		IASTAppendable dList;
		IASTAppendable listOfRules;
		IExpr function;

		public FindFitParametricFunction(IExpr function, IAST listOfSymbols, ISymbol x, EvalEngine engine) {
			this.function = function;
			this.engine = engine;
			this.x = x;
			this.listOfSymbols = listOfSymbols;
			this.dList = F.ListAlloc(listOfSymbols.argSize());
			for (int i = 1; i < listOfSymbols.size(); i++) {
				this.dList.append(engine.evaluate(F.D(function, listOfSymbols.get(i))));
			}
			this.listOfRules = F.ListAlloc(dList.size());
			this.listOfRules.append(F.Rule(x, F.Null));
			for (int i = 1; i < listOfSymbols.size(); i++) {
				this.listOfRules.append(F.Rule(listOfSymbols.get(i), F.Null));
			}
		}

		/** {@inheritDoc} */
		@Override
		public double value(final double t, final double... parameters) throws MathIllegalArgumentException {
			createSubstitutionRules(t, parameters);
			return F.subst(function, listOfRules).evalDouble();
		}

		public double[] gradient(double t, double... parameters) {
			createSubstitutionRules(t, parameters);
			final double[] gradient = new double[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				gradient[i] = F.subst(dList.get(i + 1), listOfRules).evalDouble();
			}
			return gradient;
		}

		private void createSubstitutionRules(double t, double... parameters) {
			IASTMutable substitutionRules = (IASTMutable) this.listOfRules.get(1);
			substitutionRules.set(2, F.num(t));
			for (int i = 2; i < listOfRules.size(); i++) {
				substitutionRules = (IASTMutable) this.listOfRules.get(i);
				substitutionRules.set(2, F.num(parameters[i - 2]));
			}
		}
	}

	public FindFit() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 5);

		if (ast.arg1().isList() && ast.arg3().isList() && ast.arg4().isSymbol()) {
			IAST data = (IAST) ast.arg1();
			IExpr function = ast.arg2();
			IAST listOfSymbols = (IAST) ast.arg3();
			ISymbol x = (ISymbol) ast.arg4();
			final int numberofParameters = listOfSymbols.size() - 1; 
			double[] initialGuess = new double[numberofParameters];
			for (int i = 0; i < numberofParameters; i++) {
				initialGuess[i] = 1.0;
			}
			AbstractCurveFitter fitter = SimpleCurveFitter
					.create(new FindFitParametricFunction(function, listOfSymbols, x, engine), initialGuess);
			int[] isMatrix = data.isMatrix();
			WeightedObservedPoints obs = new WeightedObservedPoints();

			if (isMatrix != null && isMatrix[1] == 2) {
				final double[][] elements = ((IAST) ast.arg1()).toDoubleMatrix();
				if (elements == null) {
					return F.NIL;
				}
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, elements[i][0], elements[i][1]);
				}
			} else {
				int rowSize = ast.arg1().isVector();
				if (rowSize < 0) {
					return F.NIL;
				}
				final double[] elements = ((IAST) ast.arg1()).toDoubleVector();
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, i + 1, elements[i]);
				}
			}
			double[] values = fitter.fit(obs.toList());
			IASTAppendable result = F.ListAlloc(listOfSymbols.size());
			for (int i = 1; i < listOfSymbols.size(); i++) {
				result.append(F.Rule(listOfSymbols.get(i), F.num(values[i - 1])));
			}
			return result;
		}
		return F.NIL;
	}
}