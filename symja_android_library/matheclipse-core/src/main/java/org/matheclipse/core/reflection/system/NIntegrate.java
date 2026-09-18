package org.matheclipse.core.reflection.system;

import java.util.Set;
import java.util.TreeSet;
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
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.convert.VariablesSet;
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
 * Romberg is the base method for numerical integration; for infinite intervals and for integrands
 * containing <code>Abs()</code> or like <code>x^x</code> the adaptive GaussKronrod method is
 * selected automatically. LegendreGauss is a fixed-order rule without a convergence test, it
 * returns a finite number even for a divergent integral.
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

  /** Highest degree of an <code>Abs()</code> argument whose roots are used as break points. */
  private static final int MAX_BREAK_POINT_DEGREE = 20;

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

  /**
   * Integrate over <code>[min, max]</code> piece by piece between the <code>breakPoints</code>, so
   * that no rule has to step over a kink or a pole of the integrand.
   *
   * @param breakPoints sorted points strictly inside <code>(min, max)</code>
   */
  private static double integratePieces(UnaryNumerical f, ISymbol xVar, double min, double max,
      double[] breakPoints, String method, int maxPoints, int maxIterations, IAST rest)
      throws MathIllegalStateException {
    double sum = 0.0;
    double lower = min;
    for (int i = 0; i <= breakPoints.length; i++) {
      double upper = i < breakPoints.length ? breakPoints[i] : max;
      double piece =
          integrateDouble(f, xVar, lower, upper, method, maxPoints, maxIterations, rest);
      if (!Double.isFinite(piece)) {
        return piece;
      }
      sum += piece;
      lower = upper;
    }
    return sum;
  }

  /**
   * The real zeros and poles strictly inside <code>(min, max)</code> of the <code>Abs()</code> and
   * <code>RealAbs()</code> arguments in <code>function</code> which are rational in
   * <code>x</code>. A quadrature rule can step over the kink of such an argument when none of its
   * nodes falls into the region where the argument changes sign: all 15 Gauss-Kronrod nodes of
   * <code>Abs(x^2-2*x)</code> on <code>[-10, 10]</code> miss <code>(0, 2)</code>, both embedded
   * rules integrate <code>x^2-2*x</code> exactly, and their agreement looks like convergence.
   *
   * @return the sorted break points, possibly empty
   */
  private static double[] absBreakPoints(IExpr function, ISymbol x, double min, double max,
      EvalEngine engine) {
    TreeSet<Double> points = new TreeSet<Double>();
    // N(Integrate(...)) arrives in numeric mode, where Together(x^2-2*x) turns into the
    // non-polynomial -2.0*x+x^2.0
    boolean numericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(false);
      collectAbsBreakPoints(function, x, min, max, points, engine);
    } catch (RuntimeException rex) {
      // integrate without the break points found so far
      Errors.rethrowsInterruptException(rex);
    } finally {
      engine.setNumericMode(numericMode);
    }
    double[] result = new double[points.size()];
    int i = 0;
    for (Double point : points) {
      result[i++] = point.doubleValue();
    }
    return result;
  }

  private static void collectAbsBreakPoints(IExpr expr, ISymbol x, double min, double max,
      Set<Double> points, EvalEngine engine) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if ((ast.isAbs() || ast.isAST(S.RealAbs, 2)) && !ast.arg1().isFree(x)) {
      IExpr together = engine.evaluate(F.Together(ast.arg1()));
      addRealRoots(engine.evaluate(F.Numerator(together)), x, min, max, points, engine);
      addRealRoots(engine.evaluate(F.Denominator(together)), x, min, max, points, engine);
    }
    for (IExpr arg : ast) {
      collectAbsBreakPoints(arg, x, min, max, points, engine);
    }
  }

  private static void addRealRoots(IExpr polynomial, ISymbol x, double min, double max,
      Set<Double> points, EvalEngine engine) {
    if (polynomial.isFree(x) || !polynomial.isPolynomial(F.list(x))
        || !new VariablesSet(polynomial).isSize(1)) {
      // other variables (the outer variable of a nested NIntegrate) leave no numeric roots
      return;
    }
    if (engine.evaluate(F.Exponent(polynomial, x)).toIntDefault() > MAX_BREAK_POINT_DEGREE) {
      return;
    }
    IAST roots = RootsFunctions.roots(polynomial, true, F.list(x), engine);
    if (roots.isNIL()) {
      return;
    }
    // keep a margin, a root at an endpoint needs no split
    double margin = 1.0e-12 * (max - min);
    for (IExpr root : roots) {
      if (root.isReal()) {
        double value = root.evalf();
        if (value > min + margin && value < max - margin) {
          points.add(value);
        }
      }
    }
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
    } else if (!function.isFree(a -> a == S.Abs || a == S.RealAbs, true)
        || !function.isFree(a -> a.isPower() && !a.exponent().isFree(x, true), false)) {
      // Abs() kinks and x^f(x) shapes like x^x (issue #1419): Romberg converges poorly, use the
      // adaptive Gauss-Kronrod rule. Not the fixed-order LegendreGauss rule - it has no error
      // estimate and returns a finite number even for a divergent Abs(1/x)
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
      double[] breakPoints = maxDouble == Double.POSITIVE_INFINITY
          || minDouble == Double.NEGATIVE_INFINITY ? new double[0]
              : absBreakPoints(function, (ISymbol) x, minDouble, maxDouble, engine);
      RuntimeException failure = null;
      try {
        double result = integratePieces(sampler, (ISymbol) x, minDouble, maxDouble, breakPoints,
            method, maxPoints, maxIterations, list.rest());
        if (Double.isFinite(result)) {
          return Num.valueOf(sign * Precision.round(result, precisionGoal));
        }
      } catch (MathRuntimeException | ArgumentTypeException e) {
        failure = e;
      }

      // Retry with the adaptive Gauss-Kronrod rule (integrals with endpoint singularities,
      // non-convergence of the simpler rules) if the method was chosen automatically
      if (option[0].isAutomatic() && !"GaussKronrod".equalsIgnoreCase(method)) {
        try {
          double result = integratePieces(sampler, (ISymbol) x, minDouble, maxDouble,
              breakPoints, "GaussKronrod", maxPoints, maxIterations, list.rest());
          if (Double.isFinite(result)) {
            return Num.valueOf(sign * Precision.round(result, precisionGoal));
          }
        } catch (MathRuntimeException | ArgumentTypeException e) {
          // keep the first failure for reporting
        }
      }

      // CAS "SymbolicProcessing" fallback for oscillatory/infinite integrals: solve
      // symbolically, then evaluate the result numerically. Not in numeric mode: from
      // N(Integrate(...)) Integrate would delegate straight back to NIntegrate
      IExpr symbolic = engine.evaluateNonNumeric(F.Integrate(function, list));
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

      if (sampler.failureCount() > 0 && sampler.failureCount() == sampler.sampleCount()) {
        // The integrand `1` has evaluated to non-numerical values for all sampling points in
        // the region with boundaries `2`.
        return Errors.printMessage(ast.topHead(), "inumr",
            F.List(function, F.List(list.arg2(), list.arg3())), engine);
      }
      if (failure instanceof MathRuntimeException && ((MathRuntimeException) failure)
          .getSpecifier() == LocalizedCoreFormats.MAX_COUNT_EXCEEDED) {
        // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
        return Errors.printMessage(ast.topHead(), "ncvi",
            F.List(F.ZZ(maxIterations), x, list.rest()), engine);
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
