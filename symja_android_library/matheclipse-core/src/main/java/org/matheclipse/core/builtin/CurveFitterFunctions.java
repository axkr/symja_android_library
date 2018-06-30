package org.matheclipse.core.builtin;

import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.PolynomialCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

public class CurveFitterFunctions {
	static {
		F.FindFit.setEvaluator(new FindFit());
		F.Fit.setEvaluator(new Fit());
	}

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
	 * <p>
	 * The default initial guess in the following example for the parameters <code>{a,w,f}</code> is
	 * <code>{1.0, 1.0, 1.0}</code>. These initial values give a bad result:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {a,w,f}, t)
	 * {a-&gt;0.6688,w-&gt;1.49588,f-&gt;3.74845}
	 * </pre>
	 * <p>
	 * The initial guess <code>{2.0, 1.0, 1.0}</code> gives a much better result:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)
	 * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
	 * </pre>
	 * <p>
	 * You can omit <code>1.0</code> in the parameter list because it's the default value:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, w, f}, t) 
	 * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
	 * </pre>
	 */
	private static class FindFit extends AbstractFunctionEvaluator {

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

			private void createSubstitutionRules(double t, double... parameters) {
				IASTMutable substitutionRules = (IASTMutable) this.listOfRules.get(1);
				substitutionRules.set(2, F.num(t));
				for (int i = 2; i < listOfRules.size(); i++) {
					substitutionRules = (IASTMutable) this.listOfRules.get(i);
					substitutionRules.set(2, F.num(parameters[i - 2]));
				}
			}

			@Override
			public double[] gradient(double t, double... parameters) {
				createSubstitutionRules(t, parameters);
				final double[] gradient = new double[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					gradient[i] = F.subst(dList.get(i + 1), listOfRules).evalDouble();
				}
				return gradient;
			}

			/** {@inheritDoc} */
			@Override
			public double value(final double t, final double... parameters) throws MathIllegalArgumentException {
				createSubstitutionRules(t, parameters);
				return F.subst(function, listOfRules).evalDouble();
			}
		}

		public FindFit() {
			super();
		}

		/**
		 * Evaluate the data to double values and add it to the observed points.
		 * 
		 * @param data
		 * @param obs
		 * @return
		 */
		protected static boolean addWeightedObservedPoints(IAST data, WeightedObservedPoints obs) {
			int[] isMatrix = data.isMatrix();
			if (isMatrix != null && isMatrix[1] == 2) {
				final double[][] elements = data.toDoubleMatrix();
				if (elements == null) {
					return false;
				}
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, elements[i][0], elements[i][1]);
				}
			} else {
				int rowSize = data.isVector();
				if (rowSize < 0) {
					return false;
				}
				final double[] elements = data.toDoubleVector();
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, i + 1, elements[i]);
				}
			}
			return true;
		}

		/**
		 * Get a list of rules <code>{listOfSymbols[1] -> values[0], .... }</code>.
		 * 
		 * @param listOfSymbols
		 * @param values
		 * 
		 * @return
		 */
		private static IExpr convertToRulesList(IAST listOfSymbols, double[] values) {
			IASTAppendable result = F.ListAlloc(listOfSymbols.size());
			for (int i = 1; i < listOfSymbols.size(); i++) {
				result.append(F.Rule(listOfSymbols.get(i), F.num(values[i - 1])));
			}
			return result;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return numericEval(ast, engine);
		}

		/**
		 * Determine the initial guess. Default is <code>[1.0, 1.0, 1.0,...]</code>
		 * 
		 * @param listOfSymbolsOrPairs
		 * @param initialGuess
		 * @return <code>F.NIL</code> if the list of symbols couldn't be determined
		 */
		protected static IAST initialGuess(IAST listOfSymbolsOrPairs, double[] initialGuess) {
			IASTAppendable newListOfSymbols = F.ListAlloc(listOfSymbolsOrPairs.size());
			for (int i = 1; i < listOfSymbolsOrPairs.size(); i++) {
				IExpr temp = listOfSymbolsOrPairs.get(i);
				if (temp.isSymbol()) {
					initialGuess[i - 1] = 1.0;
					newListOfSymbols.append(temp);
				} else if (temp.isAST(F.List, 3) && temp.first().isSymbol()) {
					ISignedNumber signedNumber = temp.second().evalReal();
					if (signedNumber != null) {
						initialGuess[i - 1] = signedNumber.doubleValue();
					} else {
						return F.NIL;
					}
					newListOfSymbols.append(temp.first());
				} else {
					return F.NIL;
				}
			}
			return newListOfSymbols;
		}

		/**
		 * Create the initial guess <code>[1.0, 1.0, 1.0,...]</code>
		 * 
		 * @param length
		 *            the length of the initial array
		 * @param value
		 *            the value every element of the array should be initialized
		 */
		protected static double[] initialGuess(final int length, final double value) {
			double[] initialGuess = new double[length];
			for (int i = 0; i < initialGuess.length; i++) {
				initialGuess[i] = value;
			}
			return initialGuess;
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 5);

			if (ast.arg1().isList() && ast.arg3().isList() && ast.arg4().isSymbol()) {
				IAST data = (IAST) ast.arg1();
				IExpr function = ast.arg2();
				IAST listOfSymbols = (IAST) ast.arg3();
				ISymbol x = (ISymbol) ast.arg4();

				double[] initialGuess = new double[listOfSymbols.size() - 1];
				listOfSymbols = initialGuess(listOfSymbols, initialGuess);
				if (listOfSymbols.isPresent()) {
					try {
						AbstractCurveFitter fitter = SimpleCurveFitter.create(
								new FindFitParametricFunction(function, listOfSymbols, x, engine), initialGuess);
						WeightedObservedPoints obs = new WeightedObservedPoints();
						if (addWeightedObservedPoints(data, obs)) {
							double[] values = fitter.fit(obs.toList());
							return convertToRulesList(listOfSymbols, values);
						}
					} catch (MathException ex) {
						engine.printMessage("FindFit: " + ex.getMessage());
					}
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * Fit(list - of - data - points, degree, variable)
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
	 * &gt;&gt; Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
	 * x^2.0
	 * </pre>
	 */
	private static class Fit extends FindFit {

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			if (ast.arg1().isList() && ast.arg2().isReal() && ast.arg3().isSymbol()) {
				int polynomialDegree = ast.arg2().toIntDefault(Integer.MIN_VALUE);
				if (polynomialDegree > 0) {
					AbstractCurveFitter fitter = PolynomialCurveFitter.create(polynomialDegree);
					IAST data = (IAST) ast.arg1();
					WeightedObservedPoints obs = new WeightedObservedPoints();
					if (addWeightedObservedPoints(data, obs)) {
						try {
							return Convert.polynomialFunction2Expr(fitter.fit(obs.toList()), (ISymbol) ast.arg3());
						} catch (MathException ex) {
							engine.printMessage("Fit: " + ex.getMessage());
						}
					}
				}
			}

			return F.NIL;
		}
	}

	private final static CurveFitterFunctions CONST = new CurveFitterFunctions();

	public static CurveFitterFunctions initialize() {
		return CONST;
	}

	private CurveFitterFunctions() {

	}

}
