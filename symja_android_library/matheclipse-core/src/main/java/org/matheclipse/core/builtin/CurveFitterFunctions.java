package org.matheclipse.core.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.PolynomialCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.hipparchus.stat.regression.SimpleRegression;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class CurveFitterFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

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
  private static class FindFit extends AbstractFunctionEvaluator {

    private static class FindFitParametricFunction implements ParametricUnivariateFunction {
      final EvalEngine engine;
      final IExpr function;
      IAST gradientList;
      IASTAppendable listOfRules;

      public FindFitParametricFunction(final IExpr function, final IAST gradientList,
          final IAST listOfSymbols, final ISymbol x, final EvalEngine engine) {
        this.function = function;
        this.engine = engine;
        this.gradientList = gradientList;
        this.listOfRules = F.ListAlloc(gradientList.size());
        this.listOfRules.append(F.Rule(x, S.Null));
        listOfSymbols.forEach(arg -> this.listOfRules.append(F.Rule(arg, S.Null)));
        // for (int i = 1; i < listOfSymbols.size(); i++) {
        // this.listOfRules.append(F.Rule(listOfSymbols.get(i), F.Null));
        // }
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
          gradient[i] = F.subst(gradientList.get(i + 1), listOfRules).evalDouble();
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
        if (elements == null) {
          return false;
        }
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
     * @return
     */
    private static IExpr convertToRulesList(IAST listOfSymbols, double[] values) {
      IASTAppendable result = F.ListAlloc(listOfSymbols.size());
      listOfSymbols.forEach((arg, i) -> result.append(F.Rule(arg, F.num(values[i - 1]))));
      // for (int i = 1; i < listOfSymbols.size(); i++) {
      // result.append(F.Rule(listOfSymbols.get(i), F.num(values[i - 1])));
      // }
      return result;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return numericEval(ast, engine);
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
      IASTAppendable newListOfSymbols = F.ListAlloc(listOfSymbolsOrPairs.size());
      for (int i = 1; i < listOfSymbolsOrPairs.size(); i++) {
        IExpr temp = listOfSymbolsOrPairs.get(i);
        if (temp.isSymbol()) {
          initialGuess[i - 1] = 1.0;
          newListOfSymbols.append(temp);
        } else if (temp.isAST(S.List, 3) && temp.first().isSymbol()) {
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

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg3().isList() && ast.arg4().isSymbol()) {
        IAST data = (IAST) ast.arg1();
        IExpr function = ast.arg2();
        IAST listOfSymbols = (IAST) ast.arg3();
        ISymbol x = (ISymbol) ast.arg4();

        double[] initialGuess = new double[listOfSymbols.size() - 1];
        listOfSymbols = initialGuess(listOfSymbols, initialGuess);
        if (listOfSymbols.isPresent()) {
          try {
            IExpr gradientList = S.Grad.of(engine, function, listOfSymbols);
            if (gradientList.isList()) {
              AbstractCurveFitter fitter =
                  SimpleCurveFitter.create(new FindFitParametricFunction(function,
                      (IAST) gradientList, listOfSymbols, x, engine), initialGuess);
              WeightedObservedPoints obs = new WeightedObservedPoints();
              if (addWeightedObservedPoints(data, obs)) {
                double[] values = fitter.fit(obs.toList());
                return convertToRulesList(listOfSymbols, values);
              }
            }
          } catch (ValidateException ve) {
            return IOFunctions.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        }
      }
      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * Fit(list - of - data - points, degree, variable)
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
   * &gt;&gt; Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
   * x^2.0
   * </pre>
   */
  private static class Fit extends FindFit {

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg2().isReal() && ast.arg3().isSymbol()) {
        int polynomialDegree = ast.arg2().toIntDefault();
        if (polynomialDegree > 0) {
          if (Config.MAX_AST_SIZE < polynomialDegree) {
            ASTElementLimitExceeded.throwIt(polynomialDegree);
          }
          AbstractCurveFitter fitter = PolynomialCurveFitter.create(polynomialDegree);
          IAST data = (IAST) ast.arg1();
          WeightedObservedPoints obs = new WeightedObservedPoints();
          if (addWeightedObservedPoints(data, obs)) {
            try {
              return Convert.polynomialFunction2Expr(fitter.fit(obs.toList()),
                  (ISymbol) ast.arg3());
            } catch (RuntimeException rex) {
              LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  private static final class LinearModelFit extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        int[] dim = ast.arg1().isMatrix();
        if (dim != null && dim[1] == 2) {
          double[][] data = ast.arg1().toDoubleMatrix();
          // double[][] data = { { 1, 3 }, { 2, 5 }, { 3, 7 }, { 4, 14 }, { 5, 11 } };
          SimpleRegression model = new SimpleRegression();
          model.addData(data);
          double[] values = new double[] {model.getIntercept(), model.getSlope()};
          // return FittedModelExpr.newInstance(model);
          return F.Plus(F.num(model.getIntercept()), F.Times(F.num(model.getSlope()), ast.arg3()));
          // return new ASTRealVector(values, false);

          // OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
          // double[] y = new double[] { 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
          // double[][] x = new double[6][];
          // x[0] = new double[] { 0, 0, 0, 0, 0 };
          // x[1] = new double[] { 2.0, 0, 0, 0, 0 };
          // x[2] = new double[] { 0, 3.0, 0, 0, 0 };
          // x[3] = new double[] { 0, 0, 4.0, 0, 0 };
          // x[4] = new double[] { 0, 0, 0, 5.0, 0 };
          // x[5] = new double[] { 0, 0, 0, 0, 6.0 };
          // regression.newSampleData(y, x);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private CurveFitterFunctions() {}
}
