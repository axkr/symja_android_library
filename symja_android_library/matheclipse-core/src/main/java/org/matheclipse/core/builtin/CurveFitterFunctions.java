package org.matheclipse.core.builtin;

import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FittedModelExpr;
import org.matheclipse.core.generic.SumCurveFitter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class CurveFitterFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FindFit.setEvaluator(new FindFit());
      S.Fit.setEvaluator(new Fit());
      S.LinearModelFit.setEvaluator(new LinearModelFit());
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
        for (int i = 0; i < parameters.length; i++) {
          gradient[i] = F.subst(gradientList.get(i + 1), listOfRules).evalf();
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
        } else if (temp.isList2() && temp.first().isSymbol()) {
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

    public IExpr numericEval(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr workingPrecision = options[0];
      if (ast.arg1().isList() && ast.arg3().isList() && ast.arg4().isVariable()) {
        IAST data = (IAST) ast.arg1();
        IExpr function = ast.arg2();
        IAST listOfSymbols = (IAST) ast.arg3();
        IExpr x = ast.arg4();

        double[] initialGuess = new double[listOfSymbols.argSize()];
        listOfSymbols = initialGuess(listOfSymbols, initialGuess);
        if (listOfSymbols.isPresent()) {
          try {
            function = F.substAbs(function);
            IExpr gradientList = S.Grad.of(engine, function, listOfSymbols);
            if (gradientList.isList()) {
              AbstractCurveFitter fitter =
                  SimpleCurveFitter.create(new FindFitParametricFunction(function,
                      (IAST) gradientList, listOfSymbols, x, engine), initialGuess);
              WeightedObservedPoints obs = new WeightedObservedPoints();
              if (addWeightedObservedPoints(data, obs)) {
                double[] values = fitter.fit(obs.toList());
                return F.mapList(listOfSymbols,
                    (symbol, i) -> F.Rule(symbol, F.num(values[i - 1])));
              }
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
   * find a fit (coefficient) vector <code>a</code> that minimizes <code>Norm(m.a - v)</code> for the
   * design matrix <code>m</code> and the response vector <code>v</code> and return the best fit
   * expression.
   *
   * </blockquote>
   *
   * <p>
   * The option <code>WorkingPrecision</code> controls the arithmetic used to solve the least squares
   * problem:
   *
   * <ul>
   * <li><code>WorkingPrecision-&gt;Automatic</code> uses machine (double) precision (default)
   * <li><code>WorkingPrecision-&gt;Infinity</code> uses exact/symbolic arithmetic
   * <li><code>WorkingPrecision-&gt;n</code> uses <code>n</code>-digit <code>Apfloat</code> arbitrary
   * precision arithmetic
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
          if (model instanceof FittedModelExpr) {
            // return the best fit (coefficient) vector
            return F.List(((FittedModelExpr) model).toData().estimateRegressionParameters());
          }
          return model;
        }
        return F.NIL;
      }

      if (argSize == 3 && ast.arg1().isList() && ast.arg3().isVariable()) {
        IAST termList = ast.arg2().makeList();
        IAST data = (IAST) ast.arg1();

        // exact (Infinity) or Apfloat (n digits) arbitrary precision linear least squares
        if (precision != DOUBLE_PRECISION) {
          return symbolicFit(data, termList, ast.arg3(), precision, engine);
        }

        IASTAppendable vars = F.ListAlloc(termList.size());
        IASTAppendable plusExpr = F.PlusAlloc(termList.size());
        ISymbol dummy = F.Dummy();
        vars.append(dummy);
        plusExpr.append(dummy);
        for (int i = 1; i < termList.size(); i++) {
          dummy = F.Dummy();
          vars.append(dummy);
          plusExpr.append(F.Times(dummy, termList.get(i)));
        }
        IExpr function = F.substAbs(plusExpr);
        IExpr gradientList = S.Grad.of(engine, function, vars);
        if (gradientList.isList()) {
          double[] initialGuess = new double[vars.argSize()];
          for (int i = 0; i < initialGuess.length; i++) {
            initialGuess[i] = 1.0;
          }
          AbstractCurveFitter fitter =
              SumCurveFitter.create(new FindFit.FindFitParametricFunction(function,
                  (IAST) gradientList, vars, ast.arg3(), engine), initialGuess, Integer.MAX_VALUE);
          WeightedObservedPoints obs = new WeightedObservedPoints();
          if (addWeightedObservedPoints(data, obs)) {
            try {
              double[] coefficients = fitter.fit(obs.toList());
              if (coefficients[0] == 0.0) {
                if (coefficients.length == 1) {
                  return F.C0;
                }
              }
              return F.mapRange(S.Plus, 0, coefficients.length, i -> {
                if (i == 0) {
                  return F.num(coefficients[0]);
                } else if (coefficients[i] != 0) {
                  return F.Times(F.num(coefficients[i]), termList.get(i));
                }
                return F.NIL;
              });
            } catch (RuntimeException rex) {
              rex.printStackTrace();
              Errors.rethrowsInterruptException(rex);
              return Errors.printMessage(S.Fit, rex, engine);
            }
          }
        }

      }
      return F.NIL;
    }

    /**
     * Solve the linear least squares problem with exact (<code>Infinity</code>) or
     * <code>Apfloat</code> (<code>precision</code> digits) arithmetic.
     *
     * <p>
     * The coefficients are computed by solving the normal equations
     * <code>(X^T.X).a == X^T.y</code> with {@link S#LinearSolve}. Solving the normal equations
     * directly keeps exact rational (or <code>Apfloat</code>) entries and avoids the square roots
     * introduced by a QR decomposition, so e.g. an exact fit reduces to a simplified expression.
     *
     * @param data the list of data points (a matrix of <code>{x, y}</code> pairs or a vector of
     *        y-values)
     * @param termList the basis functions in the given <code>variable</code>
     * @param variable the model variable
     * @param precision {@link #EXACT_PRECISION} for exact arithmetic or the number of
     *        <code>Apfloat</code> digits
     * @param engine the evaluation engine
     * @return the best fit expression or {@link F#NIL}
     */
    private static IExpr symbolicFit(IAST data, IAST termList, IExpr variable, int precision,
        EvalEngine engine) {
      try {
        IAST dataMatrix = buildDataMatrix(data);
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
            IExpr xValue = row.arg1();
            responseVector.append(toPrecision(row.arg2(), precision, engine));
            IASTAppendable designRow = F.ListAlloc(numberOfTerms);
            for (int j = 1; j <= numberOfTerms; j++) {
              IExpr basisValue =
                  engine.evaluate(F.subst(termList.get(j), F.Rule(variable, xValue)));
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
     * Build a matrix of exact <code>{x, y}</code> rows from the data.
     *
     * @param data the list of data points (a matrix of <code>{x, y}</code> pairs or a vector of
     *        y-values)
     * @return the matrix as a nested list or {@link F#NIL} if the data couldn't be interpreted
     */
    private static IAST buildDataMatrix(IAST data) {
      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == 2) {
        IASTAppendable result = F.ListAlloc(data.size());
        for (int i = 1; i < data.size(); i++) {
          IAST row = (IAST) data.get(i);
          result.append(F.List(row.arg1(), row.arg2()));
        }
        return result;
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
  }

  private static final class LinearModelFit extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isList2()) {
          IExpr m = arg1.first();
          IExpr v = arg1.second();
          return FittedModelExpr.linearModelFit(m, v);
        }
        return F.NIL;
      }
      if (ast.isAST3()) {
        IAST arg2 = ast.arg2().makeList();
        IAST arg3 = ast.arg3().makeList();
        if (arg1.isList()) {
          IAST basisFunctions = arg2;
          // VariablesSet varSet = new VariablesSet(basisFunctions);
          IAST variables = arg3;

          // Intercept will be controlled by the 'basisFunctions'-list (i.e. if '1' is included).
          if (!basisFunctions.exists(f -> f.isOne())) {
            IASTAppendable temp = F.ListAlloc(basisFunctions.size());
            temp.append(F.C1);
            temp.appendArgs(basisFunctions);
            basisFunctions = temp;
          }

          int[] dim = arg1.isMatrix(false);
          if (dim != null && dim[1] >= 2) {
            double[][] numericMatrix = arg1.toDoubleMatrix(false);
            if (numericMatrix != null) {
              if (dim[1] - 1 != variables.argSize()) {
                // The number of coordinates (`1`) is not equal to the number of variables (`2`).
                return Errors.printMessage(S.LinearModelFit, "fitc",
                    F.List(F.ZZ(dim[1] - 1), F.ZZ(variables.argSize())), engine);
              }
              return FittedModelExpr.linearModelFit(numericMatrix, basisFunctions, variables,
                  engine);
            }
            FieldMatrix<IExpr> matrix = Convert.list2Matrix(arg1);
            FieldVector<IExpr> basis = Convert.list2Vector(basisFunctions);
            FieldVector<IExpr> vars = Convert.list2Vector(variables);

            if (matrix != null && basis != null && vars != null) {
              if (dim[1] - 1 != variables.argSize()) {
                // The number of coordinates (`1`) is not equal to the number of variables (`2`).
                return Errors.printMessage(S.LinearModelFit, "fitc",
                    F.List(F.ZZ(dim[1] - 1), F.ZZ(variables.argSize())), engine);
              }
              return FittedModelExpr.linearModelFit(matrix, basis, vars, engine);
            }
          } else {
            int vectorLength = arg1.isVector();
            if (vectorLength > 1) {
              FieldVector<IExpr> vector = Convert.list2Vector(arg1);
              // IExpr vector = arg1.normal(false);
              // double[] doubleVector = vector.toDoubleVector();
              if (vector != null) {
                if (variables.argSize() != 1) {
                  // The number of coordinates (`1`) is not equal to the number of variables (`2`).
                  return Errors.printMessage(S.LinearModelFit, "fitc",
                      F.List(F.C1, F.ZZ(variables.argSize())), engine);
                }
                FieldMatrix<IExpr> matrix =
                    new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, vectorLength, 2);
                // double[][] matrix = new double[vectorLength][2];
                for (int i = 0; i < vector.getDimension(); i++) {
                  matrix.setEntry(i, 0, F.ZZ(i + 1));
                  matrix.setEntry(i, 1, vector.getEntry(i));
                }
                FieldVector<IExpr> basis = Convert.list2Vector(basisFunctions);
                FieldVector<IExpr> vars = Convert.list2Vector(variables);
                // double[][] matrix = arg1.toDoubleMatrix(false);
                if (basis != null && vars != null) {
                  return FittedModelExpr.linearModelFit(matrix, basis, vars, engine);
                }
              }
            }
          }
        }
      }
      // The first argument is not a vector or matrix or a list containing a design matrix and
      // response vector.
      return Errors.printMessage(S.LinearModelFit, "notdata", F.CEmptyList, engine);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private CurveFitterFunctions() {}
}
