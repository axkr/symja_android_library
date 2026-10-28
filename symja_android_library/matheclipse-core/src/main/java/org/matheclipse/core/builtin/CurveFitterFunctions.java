package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.List;
import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoint;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.MatrixUtils;
import org.hipparchus.linear.QRDecomposition;
import org.hipparchus.linear.RealVector;
import org.hipparchus.linear.SingularValueDecomposition;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresBuilder;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresProblem;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LevenbergMarquardtOptimizer;
import org.hipparchus.optim.nonlinear.vector.leastsquares.MultivariateJacobianFunction;
import org.hipparchus.util.Pair;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FittedModelExpr;
import org.matheclipse.core.expression.data.GeneralizedFittedModelExpr;
import org.matheclipse.core.expression.data.NonlinearFittedModelExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class CurveFitterFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FindFit.setEvaluator(new FindFit());
      S.Fit.setEvaluator(new Fit());
      S.GeneralizedLinearModelFit.setEvaluator(new GeneralizedLinearModelFit());
      S.LinearModelFit.setEvaluator(new LinearModelFit());
      S.LogitModelFit.setEvaluator(new BinomialModelFit("LogitLink"));
      S.ProbitModelFit.setEvaluator(new BinomialModelFit("ProbitLink"));
      S.DiscreteIndicator.setEvaluator(new DiscreteIndicator());
      S.NonlinearModelFit.setEvaluator(new NonlinearModelFit());
    }
  }

  /**
   *
   *
   * <pre>
   * FindFit(list - of - data - points, function, parameters, variable)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solve a least squares problem using the Levenberg-Marquardt algorithm.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia -
   * Levenberg–Marquardt algorithm</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, a*Log(b*x), {a, b}, x)
   * {a-&gt;1.54503,b-&gt;20.28258}
   *
   * &gt;&gt; FindFit({{1,1},{2,4},{3,9},{4,16}}, a+b*x+c*x^2, {a, b, c}, x)
   * {a-&gt;0.0,b-&gt;0.0,c-&gt;1.0}
   * </pre>
   *
   * <p>
   * The default initial guess in the following example for the parameters <code>{a,w,f}</code> is
   * <code>{1.0, 1.0, 1.0}</code>. These initial values give a bad result:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {a,w,f}, t)
   * {a-&gt;0.6688,w-&gt;1.49588,f-&gt;3.74845}
   * </pre>
   *
   * <p>
   * The initial guess <code>{2.0, 1.0, 1.0}</code> gives a much better result:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)
   * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
   * </pre>
   *
   * <p>
   * You can omit <code>1.0</code> in the parameter list because it's the default value:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, w, f}, t)
   * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
   * </pre>
   */
  private static class FindFit extends AbstractFunctionOptionEvaluator {

    private static class FindFitParametricFunction implements ParametricUnivariateFunction {
      final EvalEngine engine;
      final IExpr function;
      IAST gradientList;
      IASTAppendable listOfRules;

      public FindFitParametricFunction(final IExpr function, final IAST gradientList,
          final IAST listOfSymbols, final IExpr x, final EvalEngine engine) {
        this.function = function;
        this.engine = engine;
        this.gradientList = gradientList;
        this.listOfRules = F.ListAlloc(gradientList.size());
        this.listOfRules.append(F.Rule(x, S.Null));
        listOfSymbols.forEach(arg -> this.listOfRules.append(F.Rule(arg, S.Null)));
      }

      private void createSubstitutionRules(double t, double... parameters) {
        IASTMutable substitutionRules = (IASTMutable) this.listOfRules.arg1();
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
        boolean numeric = true;
        for (int i = 0; i < parameters.length; i++) {
          gradient[i] = F.subst(gradientList.get(i + 1), listOfRules).evalfNaN();
          numeric &= Double.isFinite(gradient[i]);
        }
        if (!numeric) {
          // a model which is only defined for numeric arguments, f(a_?NumericQ)(t_?NumericQ),
          // has no derivative which can be evaluated: use central differences instead
          for (int i = 0; i < parameters.length; i++) {
            if (!Double.isFinite(gradient[i])) {
              final double[] shifted = parameters.clone();
              final double h = differenceStep(parameters[i]);
              shifted[i] = parameters[i] + h;
              final double upper = value(t, shifted);
              shifted[i] = parameters[i] - h;
              final double lower = value(t, shifted);
              gradient[i] = (upper - lower) / (2.0 * h);
            }
          }
          createSubstitutionRules(t, parameters);
        }
        return gradient;
      }

      /** {@inheritDoc} */
      @Override
      public double value(final double t, final double... parameters)
          throws MathIllegalArgumentException {
        createSubstitutionRules(t, parameters);
        return engine.evalDouble(F.subst(function, listOfRules));
      }
    }

    /**
     * The width of the central difference for a parameter with the value <code>p</code>: the cube
     * root of the machine epsilon, relative to the size of <code>p</code>.
     */
    static double differenceStep(double p) {
      return 6.0e-6 * Math.max(1.0, Math.abs(p));
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
        final double[][] elements = data.toDoubleMatrix(true);
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
        if (elements == null) {
          return false;
        }
        for (int i = 0; i < elements.length; i++) {
          obs.add(1.0, i + 1, elements[i]);
        }
      }
      return true;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      return numericEval(ast, argSize, options, engine, originalAST);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    /**
     * Determine the initial guess. Default is <code>[1.0, 1.0, 1.0,...]</code>
     *
     * @param listOfSymbolsOrPairs
     * @param initialGuess
     * @return <code>F.NIL</code> if the list of symbols couldn't be determined
     */
    protected static IAST initialGuess(IAST listOfSymbolsOrPairs, double[] initialGuess) {
      return F.mapList(listOfSymbolsOrPairs, (temp, i) -> {
        if (temp.isVariable()) {
          initialGuess[i - 1] = 1.0;
          return temp;
        } else if ((temp.isList2() || temp.isList3()) && temp.first().isSymbol()) {
          // {a, a0} or {a, a0, a1}: the search starts at a0
          final IReal realNumber = temp.second().evalReal();
          if (realNumber != null) {
            initialGuess[i - 1] = realNumber.doubleValue();
          } else {
            return null;
          }
          return temp.first();
        }
        return F.NIL;
      });
    }

    /**
     * Replace the parameters which are no symbols, like <code>s(1)</code>, by unique symbols in the
     * parameter specification <code>a</code> or <code>{a, a0}</code>.
     *
     * @param listOfSymbolsOrPairs the parameter specification
     * @param renamed the rules <code>s(1) -> $1</code> are appended to this list
     * @return the parameter specification with the replaced parameters
     */
    protected static IAST renameParameters(IAST listOfSymbolsOrPairs, IASTAppendable renamed) {
      return F.mapList(listOfSymbolsOrPairs, (IExpr spec) -> {
        IExpr parameter = spec.isList() && spec.argSize() > 0 ? spec.first() : spec;
        if (!parameter.isAST() || parameter.isList()) {
          return spec;
        }
        ISymbol dummy = F.Dummy();
        renamed.append(F.Rule(parameter, dummy));
        return spec.isList() ? ((IAST) spec).setAtCopy(1, dummy) : dummy;
      });
    }

    /**
     * The model <code>{form}</code> is the model <code>form</code> without constraints.
     */
    protected static IExpr modelForm(IExpr model) {
      return model.isList1() ? model.first() : model;
    }

    /**
     * Create the initial guess <code>[1.0, 1.0, 1.0,...]</code>
     *
     * @param length the length of the initial array
     * @param value the value every element of the array should be initialized
     */
    protected static double[] initialGuess(final int length, final double value) {
      double[] initialGuess = new double[length];
      for (int i = 0; i < initialGuess.length; i++) {
        initialGuess[i] = value;
      }
      return initialGuess;
    }

    /**
     * Fit <code>function</code> of <code>x</code> to the data by Levenberg-Marquardt, starting from
     * <code>initialGuess</code>.
     *
     * <p>
     * Shared by <code>FindFit</code>, which only wants the parameters, and
     * <code>NonlinearModelFit</code>, which also wants the data points the fit was made to - those
     * are added to <code>pointsOut</code> when it is given.
     *
     * @return the fitted parameter values, in the order of <code>symbols</code>, or
     *         <code>null</code> when the function has no gradient or the data are not numeric
     */
    static double[] fit(IAST data, IExpr function, IAST symbols, IExpr x, double[] initialGuess,
        java.util.List<org.hipparchus.fitting.WeightedObservedPoint> pointsOut,
        EvalEngine engine) {
      IExpr fitFunction = F.substAbs(function);
      IExpr gradientList = S.Grad.of(engine, fitFunction, symbols);
      if (!gradientList.isList()) {
        return null;
      }
      AbstractCurveFitter fitter = SimpleCurveFitter.create(new FindFitParametricFunction(
          fitFunction, (IAST) gradientList, symbols, x, engine), initialGuess);
      WeightedObservedPoints obs = new WeightedObservedPoints();
      if (!addWeightedObservedPoints(data, obs)) {
        return null;
      }
      if (pointsOut != null) {
        pointsOut.addAll(obs.toList());
      }
      return fitter.fit(obs.toList());
    }

    /**
     * The model variables <code>x</code> or <code>{x, y, ...}</code> as a list.
     *
     * @return {@link F#NIL} if <code>arg</code> is no variable and no list of variables
     */
    protected static IAST fitVariables(IExpr arg) {
      if (arg.isVariable()) {
        return F.list(arg);
      }
      if (arg.isList() && arg.argSize() > 0 && ((IAST) arg).forAll(x -> x.isVariable())) {
        return (IAST) arg;
      }
      return F.NIL;
    }

    /**
     * Fit <code>function</code> of several <code>variables</code> to the rows <code>{x, y, ...,
     * f}</code> of the data by Levenberg-Marquardt, starting from <code>initialGuess</code>.
     *
     * @return the fitted parameter values, in the order of <code>symbols</code>, or
     *         <code>null</code> when the function has no gradient or the data are not numeric rows
     *         with one more element than there are variables
     */
    private static double[] fitMultivariate(IAST data, IExpr function, IAST symbols,
        IAST variables, double[] initialGuess, EvalEngine engine) {
      final int n = variables.argSize();
      int[] dim = data.isMatrix();
      if (dim == null || dim[1] != n + 1) {
        return null;
      }
      final double[][] rows = data.toDoubleMatrix(true);
      if (rows == null) {
        return null;
      }
      final IExpr fitFunction = F.substAbs(function);
      IExpr gradientList = S.Grad.of(engine, fitFunction, symbols);
      if (!gradientList.isList()) {
        return null;
      }
      final IAST gradient = (IAST) gradientList;
      final int parameters = symbols.argSize();
      // the rules variable -> value and parameter -> value, whose values are set for each row
      final IASTAppendable rules = F.ListAlloc(n + parameters);
      variables.forEach(v -> rules.append(F.Rule(v, S.Null)));
      symbols.forEach(p -> rules.append(F.Rule(p, S.Null)));
      final double[] target = new double[rows.length];
      for (int i = 0; i < rows.length; i++) {
        target[i] = rows[i][n];
      }
      MultivariateJacobianFunction model = point -> {
        for (int j = 0; j < parameters; j++) {
          ((IASTMutable) rules.get(n + j + 1)).set(2, F.num(point.getEntry(j)));
        }
        double[] values = new double[rows.length];
        double[][] jacobian = new double[rows.length][parameters];
        for (int i = 0; i < rows.length; i++) {
          for (int k = 0; k < n; k++) {
            ((IASTMutable) rules.get(k + 1)).set(2, F.num(rows[i][k]));
          }
          values[i] = engine.evalDouble(F.subst(fitFunction, rules));
          for (int j = 0; j < parameters; j++) {
            jacobian[i][j] = F.subst(gradient.get(j + 1), rules).evalfNaN();
          }
          for (int j = 0; j < parameters; j++) {
            if (!Double.isFinite(jacobian[i][j])) {
              // no derivative which can be evaluated: a central difference
              final IASTMutable rule = (IASTMutable) rules.get(n + j + 1);
              final double p = point.getEntry(j);
              final double h = differenceStep(p);
              rule.set(2, F.num(p + h));
              final double upper = engine.evalDouble(F.subst(fitFunction, rules));
              rule.set(2, F.num(p - h));
              final double lower = engine.evalDouble(F.subst(fitFunction, rules));
              rule.set(2, F.num(p));
              jacobian[i][j] = (upper - lower) / (2.0 * h);
            }
          }
        }
        return new Pair<>(new ArrayRealVector(values, false),
            new Array2DRowRealMatrix(jacobian, false));
      };
      LeastSquaresProblem problem = new LeastSquaresBuilder() //
          .maxEvaluations(Integer.MAX_VALUE) //
          .maxIterations(Integer.MAX_VALUE) //
          .start(initialGuess) //
          .target(target) //
          .model(model) //
          .build();
      return new LevenbergMarquardtOptimizer().optimize(problem).getPoint().toArray();
    }

    public IExpr numericEval(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr workingPrecision = options[0];
      final IAST variables = fitVariables(ast.arg4());
      if (ast.arg1().isList() && ast.arg3().isList() && variables.isPresent()) {
        IAST data = (IAST) ast.arg1();
        IExpr function = modelForm(ast.arg2());
        IASTAppendable renamed = F.ListAlloc();
        IAST listOfSymbols = renameParameters((IAST) ast.arg3(), renamed);
        if (renamed.argSize() > 0) {
          function = F.subst(function, renamed);
        }

        double[] initialGuess = new double[listOfSymbols.argSize()];
        listOfSymbols = initialGuess(listOfSymbols, initialGuess);
        if (listOfSymbols.isPresent()) {
          try {
            final IAST symbols = listOfSymbols;
            final IExpr model = function;
            double[] values = variables.argSize() == 1
                ? fit(data, model, symbols, variables.arg1(), initialGuess, null, engine)
                : fitMultivariate(data, model, symbols, variables, initialGuess, engine);
            if (values != null) {
              IAST rules =
                  F.mapList(symbols, (symbol, i) -> F.Rule(symbol, F.num(values[i - 1])));
              // the dummy symbols back to the parameters they replaced
              return renamed.argSize() > 0 ? F.subst(rules, F.mapList(renamed,
                  (IExpr rule) -> F.Rule(rule.second(), rule.first()))) : rules;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.FindFit, rex, engine);
          }
        }
      }
      return F.NIL;
    }

    /**
     * The parameters and the variables are local: <code>x = 0.3; FindFit(data, a*x, {a}, x)</code>
     * fits <code>a*x</code> and not <code>0.3*a</code>.
     */
    @Override
    public boolean localIterators() {
      return true;
    }

    /** The parameters <code>a</code> or <code>{a, a0}</code> and the variables. */
    @Override
    protected IAST localVariables(IAST held) {
      IASTAppendable variables = F.ListAlloc();
      if (held.argSize() >= 4) {
        for (IExpr parameter : held.arg3().makeList()) {
          appendLocalVariable(parameter.isList() && parameter.argSize() > 0 ? parameter.first()
              : parameter, variables);
        }
        for (IExpr variable : held.arg4().makeList()) {
          appendLocalVariable(variable, variables);
        }
      }
      return variables;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.WorkingPrecision}, new IExpr[] {S.Automatic});
    }
  }

  /**
   *
   *
   * <pre>
   * Fit(list - of - data - points, functions, variable)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solve a least squares problem using the Levenberg-Marquardt algorithm.
   *
   * </blockquote>
   *
   * <pre>
   * Fit({m, v})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find a fit (coefficient) vector <code>a</code> that minimizes <code>Norm(m.a - v)</code> for
   * the design matrix <code>m</code> and the response vector <code>v</code> and return the best fit
   * expression.
   *
   * </blockquote>
   *
   * <p>
   * The option <code>WorkingPrecision</code> controls the arithmetic used to solve the least
   * squares problem:
   *
   * <ul>
   * <li><code>WorkingPrecision-&gt;Automatic</code> uses machine (double) precision (default)
   * <li><code>WorkingPrecision-&gt;Infinity</code> uses exact/symbolic arithmetic
   * <li><code>WorkingPrecision-&gt;n</code> uses <code>n</code>-digit <code>Apfloat</code>
   * arbitrary precision arithmetic
   * </ul>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia -
   * Levenberg–Marquardt algorithm</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
   * x^2.0
   * </pre>
   */
  private static class Fit extends FindFit {

    /** {@link S#WorkingPrecision} sentinel: use machine (double) precision. */
    private static final int DOUBLE_PRECISION = -2;

    /** {@link S#WorkingPrecision} sentinel: use exact/symbolic arithmetic. */
    private static final int EXACT_PRECISION = -1;

    @Override
    public IExpr numericEval(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr workingPrecision = options[0];
      int precision = workingPrecisionValue(workingPrecision);

      // Fit({m, v}) signature
      if (argSize == 1) {
        IExpr arg1 = ast.arg1();
        if (arg1.isList2()) {
          IExpr m = arg1.first();
          IExpr v = arg1.second();
          IExpr model = FittedModelExpr.linearModelFit(m, v);
          try {
            if (model instanceof FittedModelExpr) {
              // return the best fit (coefficient) vector
              return F.List(((FittedModelExpr) model).toData().estimateRegressionParameters());
            }
            return model;
          } catch (MathIllegalArgumentException miae) {
            return Errors.printMessage(S.Fit, miae);
            // if (miae.getSpecifier().equals(LocalizedCoreFormats.SINGULAR_MATRIX)) {
            // }
          }
        }
        return F.NIL;
      }

      final IAST variables = argSize == 3 ? fitVariables(ast.arg3()) : F.NIL;
      if (variables.isPresent() && ast.arg1().isList()) {
        IAST termList = ast.arg2().makeList();
        IAST data = (IAST) ast.arg1();

        // exact (Infinity) or Apfloat (n digits) arbitrary precision linear least squares
        if (precision != DOUBLE_PRECISION) {
          return symbolicFit(data, termList, variables, precision, engine);
        }

        IAST dataMatrix = buildDataMatrix(data, variables.argSize());
        final double[][] points = dataMatrix.isPresent() ? dataMatrix.toDoubleMatrix(false) : null;
        if (points != null) {
          try {
            double[] coefficients =
                linearLeastSquares(dataMatrix, points, termList, variables, engine);
            return F.mapRange(S.Plus, 0, coefficients.length, i -> {
              if (coefficients[i] != 0.0) {
                return F.Times(F.num(coefficients[i]), termList.get(i + 1));
              }
              return F.NIL;
            });
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.Fit, rex, engine);
          }
        }
        if (dataMatrix.isPresent() && dataMatrix.forAll(row -> isResponse(row.last()))) {
          return symbolicResponseFit(dataMatrix, termList, variables, engine);
        }
      }
      return F.NIL;
    }

    /** Whether a value of the data can be a term of a linear combination of the responses. */
    private static boolean isResponse(IExpr value) {
      return !(value.isList() || value.isRuleAST() || value.isString());
    }

    /**
     * The least squares fit of data whose responses are not all numbers. Each coefficient is a
     * linear combination of the responses: with the columns of the design matrix scaled to unit
     * length and <code>P</code> the pseudo-inverse of the scaled matrix, the coefficient of the
     * basis function <code>j</code> is <code>(P[j,1] y1 + P[j,2] y2 + ...)</code> over the length of
     * the column <code>j</code>.
     *
     * @param dataMatrix the data as <code>{x, y, ..., f}</code> rows with numeric coordinates
     * @return the fit or {@link F#NIL} if a coordinate or a basis function value is not a number
     */
    private static IExpr symbolicResponseFit(IAST dataMatrix, IAST termList, IAST variables,
        EvalEngine engine) {
      final int rows = dataMatrix.argSize();
      final int columns = termList.argSize();
      final double[][] design = new double[rows][columns];
      final double[] length = new double[columns];
      try {
        for (int i = 0; i < rows; i++) {
          final IAST rules = pointRules(variables, (IAST) dataMatrix.get(i + 1));
          for (int j = 0; j < columns; j++) {
            design[i][j] = engine.evalDouble(engine.evaluate(F.subst(termList.get(j + 1), rules)));
            length[j] += design[i][j] * design[i][j];
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
      for (int j = 0; j < columns; j++) {
        length[j] = Math.sqrt(length[j]);
      }
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          if (length[j] > 0.0) {
            design[i][j] /= length[j];
          }
        }
      }
      final double[][] inverse = LinearAlgebra
          .machinePseudoInverse(MatrixUtils.createRealMatrix(design)).getData();
      IASTAppendable fit = F.PlusAlloc(columns);
      for (int j = 0; j < columns; j++) {
        if (length[j] > 0.0) {
          final double[] row = inverse[j];
          IExpr combination = F.mapRange(S.Plus, 0, rows,
              k -> F.Times(F.num(row[k]), ((IAST) dataMatrix.get(k + 1)).last()));
          fit.append(F.Times(F.num(1.0 / length[j]), combination, termList.get(j + 1)));
        }
      }
      return engine.evaluate(fit);
    }

    /**
     * A column of the design matrix depends on the columns before it, if what is left of its
     * unit length after the reduction by them is not larger than this.
     */
    private static final double DEPENDENT_COLUMN = 1.0e-11;

    /**
     * The coefficients of the basis functions which minimize the sum of the squared residuals.
     * There is one coefficient for each function, and no constant which is not in the basis.
     *
     * <p>
     * The problem is linear. The columns of the design matrix are scaled to unit length, so that
     * <code>1</code> and <code>x^5</code> count alike, and the matrix is solved by its QR
     * decomposition. If the functions depend on each other at the points of the data, or if
     * there are more functions than points, the singular values of the matrix as it is give the
     * solution with the smallest coefficients. A function which is zero at every point gets the
     * coefficient 0. An iteration from a first guess ran away with such functions.
     *
     * @param dataMatrix the data as <code>{x, y, ..., f}</code> rows; the basis functions are
     *        evaluated at these <code>x, y, ...</code>, so that <code>Sin(2*x)</code> is zero at
     *        <code>Pi</code>
     * @param points the same data as machine numbers
     * @param termList the basis functions in the given <code>variables</code>
     * @param variables the model variables
     * @param engine the evaluation engine
     */
    private static double[] linearLeastSquares(IAST dataMatrix, double[][] points,
        IAST termList, IAST variables, EvalEngine engine) {
      final int rows = points.length;
      final int columns = termList.argSize();
      final int n = variables.argSize();
      final double[][] design = new double[rows][columns];
      final double[] response = new double[rows];
      for (int i = 0; i < rows; i++) {
        response[i] = points[i][n];
        final IAST rules = pointRules(variables, (IAST) dataMatrix.get(i + 1));
        for (int j = 0; j < columns; j++) {
          // the exact value first: Sin(2*x) is 0 at Pi, and not the sine of 6.283185307179586
          design[i][j] = engine.evalDouble(engine.evaluate(F.subst(termList.get(j + 1), rules)));
        }
      }
      // the columns which are not zero, and their lengths
      final double[] length = new double[columns];
      final int[] column = new int[columns];
      int size = 0;
      for (int j = 0; j < columns; j++) {
        double sum = 0.0;
        for (int i = 0; i < rows; i++) {
          sum = Math.hypot(sum, design[i][j]);
        }
        length[j] = sum;
        if (sum > 0.0) {
          column[size++] = j;
        }
      }
      final double[] coefficients = new double[columns];
      if (size == 0) {
        return coefficients;
      }
      final double[][] plain = new double[rows][size];
      final double[][] scaled = new double[rows][size];
      for (int i = 0; i < rows; i++) {
        for (int k = 0; k < size; k++) {
          plain[i][k] = design[i][column[k]];
          scaled[i][k] = plain[i][k] / length[column[k]];
        }
      }
      final RealVector vector = new ArrayRealVector(response, false);
      if (rows >= size) {
        final DecompositionSolver solver =
            new QRDecomposition(MatrixUtils.createRealMatrix(scaled), DEPENDENT_COLUMN)
                .getSolver();
        if (solver.isNonSingular()) {
          final double[] solution = solver.solve(vector).toArray();
          for (int k = 0; k < size; k++) {
            coefficients[column[k]] = solution[k] / length[column[k]];
          }
          return coefficients;
        }
      }
      final double[] solution =
          new SingularValueDecomposition(MatrixUtils.createRealMatrix(plain)).getSolver()
              .solve(vector).toArray();
      for (int k = 0; k < size; k++) {
        coefficients[column[k]] = solution[k];
      }
      return coefficients;
    }

    /** The rules <code>{x -> x1, y -> y1, ...}</code> for the data row <code>{x1, y1, ...}</code>. */
    private static IAST pointRules(IAST variables, IAST row) {
      return F.mapRange(1, variables.size(), k -> F.Rule(variables.get(k), row.get(k)));
    }

    /**
     * Solve the linear least squares problem with exact (<code>Infinity</code>) or
     * <code>Apfloat</code> (<code>precision</code> digits) arithmetic.
     *
     * <p>
     * The coefficients are computed by solving the normal equations <code>(X^T.X).a == X^T.y</code>
     * with {@link S#LinearSolve}. Solving the normal equations directly keeps exact rational (or
     * <code>Apfloat</code>) entries and avoids the square roots introduced by a QR decomposition,
     * so e.g. an exact fit reduces to a simplified expression.
     *
     * @param data the list of data points (a matrix of <code>{x, ..., f}</code> rows or a vector of
     *        f-values)
     * @param termList the basis functions in the given <code>variables</code>
     * @param variables the model variables
     * @param precision {@link #EXACT_PRECISION} for exact arithmetic or the number of
     *        <code>Apfloat</code> digits
     * @param engine the evaluation engine
     * @return the best fit expression or {@link F#NIL}
     */
    private static IExpr symbolicFit(IAST data, IAST termList, IAST variables, int precision,
        EvalEngine engine) {
      try {
        IAST dataMatrix = buildDataMatrix(data, variables.argSize());
        if (dataMatrix.isPresent()) {
          final int numberOfPoints = dataMatrix.argSize();
          final int numberOfTerms = termList.argSize();

          // build the design matrix X and the response vector y.
          // The basis functions are evaluated with the exact x-data first (so e.g. integer powers
          // stay exact) and only the resulting numeric entries are converted to the requested
          // working precision. This avoids raising an Apfloat base to a symbolic exponent.
          IASTAppendable designMatrix = F.ListAlloc(numberOfPoints);
          IASTAppendable responseVector = F.ListAlloc(numberOfPoints);
          for (int i = 1; i <= numberOfPoints; i++) {
            IAST row = (IAST) dataMatrix.get(i);
            IAST rules = pointRules(variables, row);
            responseVector.append(toPrecision(row.last(), precision, engine));
            IASTAppendable designRow = F.ListAlloc(numberOfTerms);
            for (int j = 1; j <= numberOfTerms; j++) {
              IExpr basisValue = engine.evaluate(F.subst(termList.get(j), rules));
              designRow.append(toPrecision(basisValue, precision, engine));
            }
            designMatrix.append(designRow);
          }

          // solve the normal equations (X^T.X).a == X^T.y
          IExpr transposed = S.Transpose.of(engine, designMatrix);
          IExpr xtx = S.Dot.of(engine, transposed, designMatrix);
          IExpr xty = S.Dot.of(engine, transposed, responseVector);
          IExpr coefficients = S.LinearSolve.of(engine, xtx, xty);
          if (coefficients.isList() && coefficients.argSize() == numberOfTerms) {
            final IAST coeffs = (IAST) coefficients;
            IExpr fit = F.mapRange(S.Plus, 1, numberOfTerms + 1,
                j -> F.Times(coeffs.get(j), termList.get(j)));
            return engine.evaluate(fit);
          }
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(S.Fit, ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Fit, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Build a matrix of exact <code>{x, ..., f}</code> rows from the data.
     *
     * @param data the list of data points (a matrix of <code>{x, ..., f}</code> rows or, for one
     *        variable, a vector of f-values)
     * @param numberOfVariables the number of the model variables
     * @return the matrix as a nested list or {@link F#NIL} if the data couldn't be interpreted
     */
    private static IAST buildDataMatrix(IAST data, int numberOfVariables) {
      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == numberOfVariables + 1) {
        IASTAppendable result = F.ListAlloc(data.size());
        for (int i = 1; i < data.size(); i++) {
          result.append(data.get(i));
        }
        return result;
      }
      if (numberOfVariables != 1) {
        return F.NIL;
      }
      int rowSize = data.isVector();
      if (rowSize < 0) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(data.size());
      for (int i = 1; i < data.size(); i++) {
        result.append(F.List(F.ZZ(i), data.get(i)));
      }
      return result;
    }

    /**
     * Convert a value to the requested working precision.
     *
     * @param value the value to convert
     * @param precision {@link #EXACT_PRECISION} for exact arithmetic or the number of
     *        <code>Apfloat</code> digits
     * @param engine the evaluation engine
     * @return the converted value
     */
    private static IExpr toPrecision(IExpr value, int precision, EvalEngine engine) {
      if (precision > 0) {
        return S.N.of(engine, value, F.ZZ(precision));
      }
      return engine.evaluate(value);
    }

    /**
     * Determine the requested working precision.
     *
     * @param workingPrecision the value of the {@link S#WorkingPrecision} option
     * @return {@link #DOUBLE_PRECISION} for {@link S#Automatic} (or invalid values),
     *         {@link #EXACT_PRECISION} for {@link S#Infinity}, otherwise the requested number of
     *         <code>Apfloat</code> digits
     */
    private static int workingPrecisionValue(IExpr workingPrecision) {
      if (workingPrecision == S.Automatic) {
        return DOUBLE_PRECISION;
      }
      if (workingPrecision.isInfinity()) {
        return EXACT_PRECISION;
      }
      int n = workingPrecision.toIntDefault();
      if (n > 0) {
        return n;
      }
      return DOUBLE_PRECISION;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    /** The variables of a linear fit are not localized. */
    @Override
    public boolean localIterators() {
      return false;
    }
  }

  /**
   * Unable to construct a numeric design matrix. Nominal variables may need to be specified, or
   * non-numeric entries for numeric variables may need to be replaced.
   */
  private static final String DESMAT_MESSAGE =
      "Unable to construct a numeric design matrix. Nominal variables may need to be specified, "
          + "or non-numeric entries for numeric variables may need to be replaced.";

  private static final String NOTDATA_MESSAGE =
      "The first argument is not a vector, a matrix or a list containing a design matrix and "
          + "response vector.";

  /**
   * Whether an independent variable of a model can be the given expression: a symbol, or an
   * expression like <code>Subscript(x, 1)</code>, but not a number, a string or a list.
   */
  private static boolean isModelVariable(IExpr variable) {
    return !(variable.isNumericFunction(true) || variable.isList() || variable.isString());
  }

  /**
   * The data of a linear model as machine rows <code>{x1, ..., xk, y}</code>. A vector, or a matrix
   * of one-element rows, are responses at the positions <code>1, 2, ...</code>. A data point with a
   * <code>Missing(...)</code> entry is left out.
   *
   * @param data the first argument of the fit
   * @param variables the independent variables
   * @param head the function which prints the messages
   * @param responseTag the message for a response which is not a number while the coordinates
   *        are, or <code>null</code> for the message <code>desmat</code>
   * @return <code>null</code> after a message if the data cannot be used
   */
  private static double[][] modelData(IExpr data, IAST variables, ISymbol head,
      String responseTag, EvalEngine engine) {
    if (!variables.forAll(CurveFitterFunctions::isModelVariable)) {
      // `1` is not a valid variable.
      Errors.printMessage(head, "ivar", F.list(variables), engine);
      return null;
    }
    if (!data.isList() || data.argSize() == 0 || !((IAST) data).forAll(x -> x.isList())
        && ((IAST) data).exists(x -> x.isList())) {
      // The first argument is not a vector, a matrix or a list containing a design matrix and
      // response vector.
      Errors.printMessage(head, "notdata", F.CEmptyList, engine);
      return null;
    }
    IAST list = (IAST) data;
    IASTAppendable rows = F.ListAlloc(list.argSize());
    int coordinates = 1;
    if (list.arg1().isList()) {
      final int length = list.arg1().argSize();
      if (!list.forAll(row -> row.argSize() == length) || length == 0) {
        Errors.printMessage(head, "notdata", F.CEmptyList, engine);
        return null;
      }
      if (length > 1) {
        coordinates = length - 1;
        rows.appendArgs(list);
      } else {
        list.forEach((row, i) -> rows.append(F.List(F.ZZ(i), row.first())));
      }
    } else {
      list.forEach((y, i) -> rows.append(F.List(F.ZZ(i), y)));
    }
    if (coordinates != variables.argSize()) {
      // The number of coordinates (`1`) is not equal to the number of variables (`2`).
      Errors.printMessage(head, "fitc", F.List(F.ZZ(coordinates), F.ZZ(variables.argSize())),
          engine);
      return null;
    }
    java.util.List<double[]> points = new java.util.ArrayList<double[]>();
    for (int i = 1; i < rows.size(); i++) {
      IAST row = (IAST) rows.get(i);
      if (row.exists(x -> x.head() == S.Missing || x == S.Missing)) {
        continue;
      }
      double[] point = row.toDoubleVector();
      if (point == null) {
        String tag = responseTag != null && row.removeAtCopy(row.argSize()).toDoubleVector() != null
            ? responseTag
            : "desmat";
        Errors.printMessage(head, tag, F.CEmptyList, engine);
        return null;
      }
      points.add(point);
    }
    if (points.isEmpty()) {
      Errors.printMessage(head, "desmat", F.CEmptyList, engine);
      return null;
    }
    return points.toArray(new double[points.size()][]);
  }

  /**
   * The basis functions of a model: a constant basis function is added unless the list has one
   * already, or <code>IncludeConstantBasis -> False</code> asks for a fit through the origin.
   */
  private static IAST modelBasis(IExpr functions, IExpr includeConstantBasis) {
    IAST basisFunctions = functions.makeList();
    if (!includeConstantBasis.isFalse() && !basisFunctions.exists(f -> f.isOne())) {
      IASTAppendable temp = F.ListAlloc(basisFunctions.size());
      temp.append(F.C1);
      temp.appendArgs(basisFunctions);
      basisFunctions = temp;
    }
    return basisFunctions;
  }

  private static final class LinearModelFit extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      IExpr arg1 = ast.arg1();
      // the call still holds the option; argSize counts the positional arguments alone
      if (argSize == 1) {
        if (arg1.isList2()) {
          IExpr m = arg1.first();
          IExpr v = arg1.second();
          return FittedModelExpr.linearModelFit(m, v);
        }
        return F.NIL;
      }
      if (argSize == 3) {
        IAST variables = ast.arg3().makeList();
        double[][] data = modelData(arg1, variables, S.LinearModelFit, null, engine);
        if (data == null) {
          return F.NIL;
        }
        return FittedModelExpr.linearModelFit(data, modelBasis(ast.arg2(), options[0]), variables,
            engine);
      }
      // The first argument is not a vector or matrix or a list containing a design matrix and
      // response vector.
      return Errors.printMessage(S.LinearModelFit, "notdata", F.CEmptyList, engine);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IncludeConstantBasis},
          new IExpr[] {S.True});
    }
  }

  /**
   * Fit a generalized linear model.
   *
   * @param ast the call <code>head(data, functions, variables, ...)</code>
   * @param family the exponential family of the response
   * @param link the link function
   * @param includeConstantBasis the value of the option {@link S#IncludeConstantBasis}
   * @return the model or {@link F#NIL}
   */
  private static IExpr generalizedLinearModelFit(IAST ast, String family, String link,
      IExpr includeConstantBasis, EvalEngine engine) {
    final ISymbol head = (ISymbol) ast.topHead();
    IAST variables = ast.arg3().makeList();
    double[][] data = modelData(ast.arg1(), variables, head,
        family.equals("Binomial") ? "binrsp" : null, engine);
    if (data == null) {
      return F.NIL;
    }
    IAST basisFunctions = modelBasis(ast.arg2(), includeConstantBasis);
    final int k = variables.argSize();
    double[] response = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      response[i] = data[i][k];
      if (!GeneralizedFittedModelExpr.isResponse(family, response[i])) {
        if (family.equals("Binomial")) {
          // The response values are expected to be between 0 and 1 for binomial models.
          return Errors.printMessage(head, "binrsp", F.CEmptyList, engine);
        }
        return F.NIL;
      }
    }
    double[][] design = FittedModelExpr.designMatrixNumeric(data, basisFunctions, variables, engine);
    if (design == null) {
      return Errors.printMessage(head, "desmat", F.CEmptyList, engine);
    }
    if (design[0].length == 0) {
      return F.NIL;
    }
    GeneralizedFittedModelExpr model = GeneralizedFittedModelExpr.fit(new GeneralizedFittedModelExpr.Fit(
        family, link, basisFunctions, variables, design, response));
    return model == null ? F.NIL : model;
  }

  /** The messages of the linear model fits. */
  private static void putModelMessages(ISymbol symbol) {
    symbol.putMessage(IPatternMatcher.SET, "desmat", F.stringx(DESMAT_MESSAGE));
    symbol.putMessage(IPatternMatcher.SET, "notdata", F.stringx(NOTDATA_MESSAGE));
    symbol.putMessage(IPatternMatcher.SET, "binrsp",
        F.stringx("The response values are expected to be between 0 and 1 for binomial models."));
  }

  /**
   * <code>GeneralizedLinearModelFit(data, functions, variables)</code> - a generalized linear model
   * of a response from an exponential family, with the canonical link function of the family.
   */
  private static final class GeneralizedLinearModelFit extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      if (argSize != 3) {
        return F.NIL;
      }
      IExpr family = options[0];
      if (family.isAutomatic()) {
        family = F.stringx("Gaussian");
      }
      if (!family.isString()
          || Arrays.binarySearch(GeneralizedFittedModelExpr.FAMILIES, family.toString()) < 0) {
        return F.NIL;
      }
      String name = family.toString();
      return generalizedLinearModelFit(ast, name, GeneralizedFittedModelExpr.canonicalLink(name),
          options[1], engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.ExponentialFamily, S.IncludeConstantBasis},
          new IExpr[] {F.stringx("Gaussian"), S.True});
    }
  }

  /**
   * <code>LogitModelFit(data, functions, variables)</code> and
   * <code>ProbitModelFit(data, functions, variables)</code> - the binomial generalized linear model
   * of probabilities with the logit or the probit link function.
   */
  private static final class BinomialModelFit extends AbstractFunctionOptionEvaluator {
    private final String link;

    BinomialModelFit(String link) {
      this.link = link;
    }

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      if (argSize != 3) {
        return F.NIL;
      }
      return generalizedLinearModelFit(ast, "Binomial", link, options[0], engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IncludeConstantBasis}, new IExpr[] {S.True});
    }
  }

  /**
   * <code>DiscreteIndicator(x, c, {c1, c2, ...})</code> - <code>1</code> if the level
   * <code>x</code> is <code>c</code> and <code>0</code> for another level; both must be levels.
   */
  private static final class DiscreteIndicator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg3().isList()) {
        IAST levels = (IAST) ast.arg3();
        if (levels.contains(ast.arg1()) && levels.contains(ast.arg2())) {
          return ast.arg1().equals(ast.arg2()) ? F.C1 : F.C0;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * <code>NonlinearModelFit(data, model, parameters, x)</code> - fit a model that need not be linear
   * in its parameters, and return a <code>FittedModel</code> that can be asked about the fit.
   *
   * <p>
   * The fit is the one <code>FindFit</code> makes, Levenberg-Marquardt from a start of
   * <code>1.0</code> for each parameter or the value given as <code>{a, a0}</code>. As with
   * <code>FindFit</code>, the model has one independent variable.
   */
  private static final class NonlinearModelFit extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || !ast.arg3().isList() || !ast.arg4().isVariable()) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();
      IExpr function = FindFit.modelForm(ast.arg2());
      IExpr x = ast.arg4();
      IASTAppendable renamed = F.ListAlloc();
      IAST spec = FindFit.renameParameters((IAST) ast.arg3(), renamed);
      if (renamed.argSize() > 0) {
        function = F.subst(function, renamed);
      }
      double[] initialGuess = new double[spec.argSize()];
      IAST symbols = FindFit.initialGuess(spec, initialGuess);
      if (symbols == null || !symbols.isPresent()) {
        return F.NIL;
      }
      try {
        java.util.List<org.hipparchus.fitting.WeightedObservedPoint> points =
            new java.util.ArrayList<>();
        double[] values = FindFit.fit(data, function, symbols, x, initialGuess, points, engine);
        if (values == null) {
          return F.NIL;
        }
        double[] xs = new double[points.size()];
        double[] ys = new double[points.size()];
        for (int i = 0; i < xs.length; i++) {
          xs[i] = points.get(i).getX();
          ys[i] = points.get(i).getY();
        }
        if (renamed.argSize() > 0) {
          // the model keeps the parameters as they were given
          IAST inverse = F.mapList(renamed, (IExpr rule) -> F.Rule(rule.second(), rule.first()));
          function = F.subst(function, inverse);
          symbols = (IAST) F.subst(symbols, inverse);
        }
        return new NonlinearFittedModelExpr(
            new NonlinearFittedModelExpr.Fit(function, symbols, x, values, xs, ys));
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.NonlinearModelFit, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private CurveFitterFunctions() {}
}
