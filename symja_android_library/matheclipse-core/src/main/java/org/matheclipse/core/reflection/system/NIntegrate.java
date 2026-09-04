package org.matheclipse.core.reflection.system;

import java.util.function.UnaryOperator;
import org.hipparchus.analysis.CalculusFieldUnivariateFunction;
import org.hipparchus.analysis.integration.IterativeLegendreGaussIntegrator;
import org.hipparchus.analysis.integration.RombergIntegrator;
import org.hipparchus.analysis.integration.SimpsonIntegrator;
import org.hipparchus.analysis.integration.TrapezoidIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegratorFactory;
import org.hipparchus.complex.Complex;
import org.hipparchus.complex.ComplexUnivariateIntegrator;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.Precision;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.integral.ClenshawCurtis;
import org.matheclipse.core.numerics.integral.GaussLobatto;
import org.matheclipse.core.numerics.integral.NewtonCotes;
import org.matheclipse.core.numerics.integral.Quadrature;
import org.matheclipse.core.numerics.integral.Quadrature.QuadratureResult;
import org.matheclipse.core.numerics.integral.TanhSinh;
import de.labathome.AdaptiveQuadrature;

/**
 * <pre>
 * <code>NIntegrate(f, {x,a,b})
 * </code>
 * </pre>
 *
 * <p>
 * computes the numerical univariate real integral of <code>f</code> with respect to <code>x</code>
 * from <code>a</code> to <code>b</code>.
 * </p>
 *
 * <p>
 * See:
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Numerical_integration">Wikipedia - Numerical
 * integration</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Trapezoidal_rule">Wikipedia - Trapezoidal
 * rule</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Romberg%27s_method">Wikipedia - Romberg's
 * method</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Riemann_sum">Wikipedia - Riemann sum</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Simpson%27s_rule">Wikipedia - Simpson's rule</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Truncation_error_(numerical_integration)">Wikipedia -
 * Truncation error (numerical integration)</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Gauss%E2%80%93Kronrod_quadrature_formula">Wikipedia -
 * Gauss-Kronrod quadrature formula)</a></li>
 * </ul>
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})
 * -0.0208333333333333
 * </code>
 * </pre>
 * <p>
 * Romberg is the base method for numerical integration; for integrands containing
 * <code>Abs()</code> the LegendreGauss method, for infinite intervals or integrands like
 * <code>x^x</code> the adaptive GaussKronrod method is selected automatically
 * </p>
 *
 * <pre>
 * <code>&gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;LegendreGauss)
 * -0.0208333333333333
 *
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;Simpson)
 * -0.0208333320915699
 *
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;Trapezoid)
 * -0.0208333271245165
 *
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;Romberg)
 * -0.0208333333333333
 *
 * &gt;&gt; NIntegrate(Exp(-x^2),{x,-Infinity,Infinity}, Method-&gt;GaussKronrod)
 * 1.772453850905516
 *
 * &gt;&gt; NIntegrate(Cos(200*x),{x,0,1}, Method-&gt;GaussKronrod)
 * -0.004366486486070
 * </code>
 * </pre>
 * <p>
 * Other options include <code>MaxIterations</code> and <code>MaxPoints</code>
 * </p>
 *
 * <pre>
 * <code>&gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;Trapezoid, MaxIterations-&gt;5000)
 * -0.0208333271245165
 * </code>
 * </pre>
 * <p>
 * Integrate along a complex line:
 * </p>
 *
 * <pre>
 * <code>&gt;&gt; NIntegrate(1.25+I*2.0+(-3.25+I*0.125)*x+(I*3.0)*x^2,{x, -1.75+I*4.0, 1.5+I*(-12.0)})
 * -1427.4921875+I*(-709.06640625)
 * </code>
 * </pre>
 */
public class NIntegrate extends AbstractFunctionOptionEvaluator {

  public static final int DEFAULT_MAX_POINTS = 100;
  public static final int DEFAULT_MAX_ITERATIONS = 10000;

  /**
   * Integrate a function numerically.
   *
   * @param function the function which should be integrated.
   * @param variable the integration variable
   * @param min Lower bound of the integration interval.
   * @param max Upper bound of the integration interval.
   * @param method the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid,
   *        GaussKronrod, ClenshawCurtisRule, DoubleExponential, GaussLobattoRule, NewtonCotesRule
   * @param maxPoints maximum number of points
   * @param maxIterations maximum number of iterations
   * @param rest a list of the form <code>{lowerBound, upperBound}</code> used in the
   *        non-convergence message
   * @param engine the evaluation engine
   *
   * @throws MathIllegalStateException
   */
  public static double integrateDouble(IExpr function, IExpr variable, final double min,
      final double max, String method, int maxPoints, int maxIterations, IAST rest,
      EvalEngine engine) throws MathIllegalStateException {
    if (!variable.isSymbol()) {
      // `1` is not a valid variable.
      String str = Errors.getMessage("ivar", F.list(variable), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    ISymbol xVar = (ISymbol) variable;
    UnaryNumerical f = createSampler(function, xVar, engine);
    return integrateDouble(f, xVar, min, max, method, maxPoints, maxIterations, rest);
  }

  /**
   * Create the numeric sampling function for the integrand. An inner {@link S#NIntegrate} (the
   * multidimensional case) must not be pre-evaluated symbolically - it is evaluated per sample
   * point, after the outer variable has been substituted with a numeric value.
   */
  private static UnaryNumerical createSampler(IExpr function, ISymbol xVar, EvalEngine engine) {
    IExpr tempFunction = function.isAST(S.NIntegrate) ? function : F.eval(function);
    return new UnaryNumerical(tempFunction, xVar, Double.NaN, engine);
  }

  private static double integrateDouble(UnaryNumerical f, ISymbol xVar, final double min,
      final double max, String method, int maxPoints, int maxIterations, IAST rest)
      throws MathIllegalStateException {
    if ("Simpson".equalsIgnoreCase(method)) {
      return new SimpsonIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("Romberg".equalsIgnoreCase(method)) {
      return new RombergIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("Trapezoid".equalsIgnoreCase(method)) {
      return new TrapezoidIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("GaussKronrod".equalsIgnoreCase(method)) {
      return gaussKronrodRule(maxIterations, f, min, max);
    }
    Quadrature quadrature = null;
    if ("ClenshawCurtisRule".equalsIgnoreCase(method)) {
      quadrature = new ClenshawCurtis(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
    } else if ("DoubleExponential".equalsIgnoreCase(method)) {
      quadrature = new TanhSinh(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
    } else if ("GaussLobattoRule".equalsIgnoreCase(method)) {
      quadrature = new GaussLobatto(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
    } else if ("NewtonCotesRule".equalsIgnoreCase(method)) {
      quadrature = new NewtonCotes(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
    }
    if (quadrature != null) {
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, rest));
    }
    // default: LegendreGauss
    if (maxPoints > 1000) {
      // github 150 - avoid StackOverflow from recursion
      // see also https://github.com/Hipparchus-Math/hipparchus/issues/61
      throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_LARGE, maxPoints,
          1000);
    }
    if (min == Double.NEGATIVE_INFINITY || max == Double.POSITIVE_INFINITY) {
      return gaussKronrodRule(maxIterations, f, min, max);
    }
    GaussIntegrator gaussIntegrator = new GaussIntegratorFactory().legendre(maxPoints, min, max);
    return gaussIntegrator.integrate(f);
  }

  private static double gaussKronrodRule(int maxIterations, UnaryNumerical function, double min,
      double max) {
    UnaryOperator<double[]> vectorFunction = new UnaryOperator<double[]>() {
      private int evaluationCounter = 0;

      @Override
      public double[] apply(double[] x) {
        evaluationCounter += x.length;
        if (evaluationCounter > maxIterations) {
          throw new MathIllegalStateException(LocalizedCoreFormats.MAX_COUNT_EXCEEDED,
              maxIterations);
        }
        return UnaryNumerical.vectorValue(function, x);
      }
    };
    double[] result = AdaptiveQuadrature.integrate(vectorFunction, min, max,
        Config.SPECIAL_FUNCTIONS_TOLERANCE, Config.SPECIAL_FUNCTIONS_TOLERANCE, 0);
    return result[0];
  }

  public NIntegrate() {
    // default ctor
  }

  /**
   * Function for <a href="http://en.wikipedia.org/wiki/Numerical_integration">numerical
   * integration</a> of univariate real functions.
   *
   * <p>
   * Uses the LegendreGaussIntegrator, RombergIntegrator, SimpsonIntegrator, TrapezoidIntegrator
   * implementations.
   */
  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (!ast.arg2().isList()) {
      return F.NIL;
    }
    IAST list = (IAST) ast.arg2();
    IExpr function = ast.arg1();
    int maxPoints = DEFAULT_MAX_POINTS;
    if (!option[1].isAutomatic()) {
      maxPoints = option[1].toIntDefault(DEFAULT_MAX_POINTS);
    }
    int maxIterations = DEFAULT_MAX_ITERATIONS;
    if (!option[2].isAutomatic()) {
      maxIterations = option[2].toIntDefault(DEFAULT_MAX_ITERATIONS);
    }
    int precisionGoal = 16; // automatic scale value
    if (!option[3].isAutomatic()) {
      precisionGoal = option[3].toIntDefault(-1);
      if (precisionGoal <= 0) {
        // Inappropriate parameter: `1`.
        return Errors.printMessage(ast.topHead(), "par", F.List(S.PrecisionGoal), engine);
      }
    }

    if (argSize > 2) {
      // NIntegrate(f, {x,...}, {y,...}, ...) - multidimensional integration by nesting: the
      // first iterator is the outermost integral (its integrand is the NIntegrate() over the
      // remaining iterators, whose limits may depend on the outer variable)
      for (int i = 2; i <= argSize; i++) {
        IExpr iterator = ast.get(i);
        if (!iterator.isList3() || !((IAST) iterator).arg1().isSymbol()) {
          // Invalid integration variable or limit(s) in `1`.
          return Errors.printMessage(ast.topHead(), "ilim", F.List(iterator), engine);
        }
      }
      function = ast.removeAtCopy(2);
    } else if (!list.isAST3() || !list.arg1().isSymbol()) {
      return F.NIL;
    }

    if (function.isEqual()) {
      IAST equalAST = (IAST) function;
      function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
    }
    final IExpr x = list.arg1();
    String method = "Romberg";
    if (!option[0].isAutomatic()) {
      method = option[0].toString();
    } else if (list.arg2().isInfinite() || list.arg3().isInfinite()) {
      // the adaptive Gauss-Kronrod rule maps infinite intervals onto finite ones itself
      method = "GaussKronrod";
    } else if (!function.isFree(a -> a == S.Abs || a == S.RealAbs, true)) {
      method = "LegendreGauss";
    } else if (!function.isFree(a -> a.isPower() && !a.exponent().isFree(x, true), false)) {
      // x^f(x) shapes like x^x: Romberg converges poorly near their endpoint behavior, use
      // the adaptive Gauss-Kronrod rule (see issue #1419)
      method = "GaussKronrod";
    }
    double minDouble = list.arg2().evalfNaN();
    double maxDouble = list.arg3().evalfNaN();
    if (Double.isNaN(minDouble) || Double.isNaN(maxDouble)) {
      return integrateComplexOrPrintInvalidLimits(ast, function, list, maxIterations, maxPoints,
          engine);
    }
    double sign = 1.0;
    if (minDouble > maxDouble) {
      // Integrate(f, {x, a, b}) == -Integrate(f, {x, b, a}); some backends require a <= b
      double swap = minDouble;
      minDouble = maxDouble;
      maxDouble = swap;
      sign = -1.0;
    }
    try {
      if (!function.isFreeAST(h -> h == S.Boole)) {
        IExpr temp = Integrate.integrateBooleTimesFxRegion(function, list, true, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      if (!x.isSymbol()) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }
      UnaryNumerical sampler = createSampler(function, (ISymbol) x, engine);
      RuntimeException failure = null;
      try {
        double result = integrateDouble(sampler, (ISymbol) x, minDouble, maxDouble, method,
            maxPoints, maxIterations, list.rest());
        if (!Double.isNaN(result)) {
          return Num.valueOf(sign * Precision.round(result, precisionGoal));
        }
      } catch (MathRuntimeException | ArgumentTypeException e) {
        failure = e;
      }

      // Retry with the adaptive Gauss-Kronrod rule (integrals with endpoint singularities,
      // non-convergence of the simpler rules) if the method was chosen automatically
      if (option[0].isAutomatic() && !"GaussKronrod".equalsIgnoreCase(method)) {
        try {
          double result = integrateDouble(sampler, (ISymbol) x, minDouble, maxDouble,
              "GaussKronrod", maxPoints, maxIterations, list.rest());
          if (!Double.isNaN(result)) {
            return Num.valueOf(sign * Precision.round(result, precisionGoal));
          }
        } catch (MathRuntimeException | ArgumentTypeException e) {
          // keep the first failure for reporting
        }
      }

      // CAS "SymbolicProcessing" fallback for oscillatory/infinite integrals: solve
      // symbolically, then evaluate the result numerically
      IExpr symbolic = engine.evaluate(F.Integrate(function, list));
      if (symbolic.isFree(S.Integrate)) {
        IExpr numeric = engine.evaluate(F.N(symbolic));
        if (numeric.isNumber()) {
          if (numeric.isReal()) {
            double val = numeric.evalfNaN();
            if (!Double.isNaN(val)) {
              return Num.valueOf(Precision.round(val, precisionGoal));
            }
          } else {
            // the integral has a complex value
            return numeric;
          }
        }
      }

      if (sampler.failureCount() > 0) {
        // The integrand `1` has evaluated to non-numerical values for all sampling points in
        // the region with boundaries `2`.
        return Errors.printMessage(ast.topHead(), "inumr",
            F.List(function, F.List(list.arg2(), list.arg3())), engine);
      }
      if (failure != null) {
        return Errors.printMessage(ast.topHead(), failure, engine);
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      return Errors.printMessage(ast.topHead(), "ncvi", F.List(F.ZZ(maxIterations), x, list.rest()),
          engine);
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      return Errors.printMessage(ast.topHead(), e, engine);
    }
  }

  /**
   * Fallback when a limit of integration has no real double value: integrate along a complex line,
   * or print the WMA-style <code>nlim</code> message if a limit is not numeric at all.
   */
  private static IExpr integrateComplexOrPrintInvalidLimits(IAST ast, IExpr function, IAST list,
      int maxIterations, int maxPoints, final EvalEngine engine) {
    Complex min = null;
    Complex max = null;
    IExpr invalidLimit = F.NIL;
    try {
      min = list.arg2().evalfc();
    } catch (ArgumentTypeException atex) {
      invalidLimit = list.arg2();
    }
    if (invalidLimit.isNIL()) {
      try {
        max = list.arg3().evalfc();
      } catch (ArgumentTypeException atex) {
        invalidLimit = list.arg3();
      }
    }
    if (invalidLimit.isPresent()) {
      // `1` = `2` is not a valid limit of integration.
      return Errors.printMessage(ast.topHead(), "nlim", F.List(list.arg1(), invalidLimit), engine);
    }
    try {
      Complex complexResult =
          integrateComplex(function, list.arg1(), min, max, maxIterations, maxPoints, engine);
      return F.complexNum(complexResult);
    } catch (MathIllegalArgumentException | MathIllegalStateException miae) {
      // especially max iterations exceeded
      return Errors.printMessage(ast.topHead(), miae, engine);
    } catch (MathRuntimeException mre) {
      return Errors.printMessage(ast.topHead(), mre, engine);
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      return Errors.printMessage(ast.topHead(), e, engine);
    }
  }

  private static Complex integrateComplex(IExpr function, IExpr variable, Complex min, Complex max,
      int maxIterations, int maxPoints, EvalEngine engine) {
    if (!variable.isSymbol()) {
      // `1` is not a valid variable.
      String str = Errors.getMessage("ivar", F.list(variable), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    ISymbol xVar = (ISymbol) variable;
    IExpr tempFunction = F.eval(function);
    UnaryNumerical f = new UnaryNumerical(tempFunction, xVar, Double.NaN, engine);

    if (maxPoints > 0) {
      maxPoints = maxPoints / 4;
    }
    return integrateComplex(f, min, max, maxIterations, maxPoints);
  }

  private static Complex integrateComplex(final CalculusFieldUnivariateFunction<Complex> function,
      final Complex min, final Complex max, int maxEval, int maxPoints) {
    ComplexUnivariateIntegrator integrator = new ComplexUnivariateIntegrator(
        new IterativeLegendreGaussIntegrator(maxPoints, 1.0e-12, 1.0e-12));
    return integrator.integrate(maxEval, function, min, max);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  private static IBuiltInSymbol[] defaultOptionKeys() {
    return new IBuiltInSymbol[] {//
        S.Method, S.MaxPoints, S.MaxIterations, S.PrecisionGoal};
  }

  private static IExpr[] defaultOptionValues() {
    return new IExpr[] {//
        S.Automatic, S.Automatic, S.Automatic, S.Automatic};
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDFIRST);
    setOptions(newSymbol, defaultOptionKeys(), defaultOptionValues());
  }
}
