package org.matheclipse.core.reflection.system;

import java.util.ArrayDeque;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
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
import org.matheclipse.core.numerics.integral.GaussKronrod;
import org.matheclipse.core.numerics.integral.GaussLobatto;
import org.matheclipse.core.numerics.integral.NewtonCotes;
import org.matheclipse.core.numerics.integral.OscillatoryTail;
import org.matheclipse.core.numerics.integral.Quadrature;
import org.matheclipse.core.numerics.integral.Quadrature.QuadratureResult;
import org.matheclipse.core.numerics.integral.TanhSinh;

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
 * <code>Method -&gt; Automatic</code> splits the interval where the symbolic integrand has a kink,
 * a jump or a pole, sums the half periods of an oscillatory factor on an infinite interval with
 * Wynn epsilon acceleration, and integrates the pieces with the globally adaptive Gauss-Kronrod
 * rule of QUADPACK (<code>QAGS</code>, <code>QAGI</code>), switching to the tanh-sinh rule and then
 * to symbolic integration for a piece which fails without looking divergent. An explicit method is
 * used as requested. LegendreGauss is a fixed-order rule without a convergence test, it returns a
 * finite number even for a divergent integral.
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

  /** Highest degree of a polynomial whose roots are used as break points. */
  private static final int MAX_BREAK_POINT_DEGREE = 20;

  /** How often the Automatic strategy splits a range at a sample it could not evaluate. */
  private static final int MAX_SAMPLE_SPLITS = 10;

  /** Most integer crossings of a <code>Floor()</code>-like argument used as break points. */
  private static final int MAX_STEP_BREAK_POINTS = 100;

  /**
   * Relative and absolute error tolerance for
   * <code>PrecisionGoal</code>/<code>AccuracyGoal -> Automatic</code>.
   */
  private static final double DEFAULT_TOLERANCE = Config.SPECIAL_FUNCTIONS_TOLERANCE;

  /** QUADPACK rejects a relative tolerance below <code>50</code> machine epsilons. */
  private static final double MIN_RELATIVE_TOLERANCE = 50.0 * Math.ulp(1.0);

  /** A {@link QuadratureResult} which did not converge, reported by the caller as a message. */
  private static final class NonConvergence extends RuntimeException {
    private static final long serialVersionUID = 1L;

    final transient QuadratureResult result;

    NonConvergence(QuadratureResult result) {
      super(null, null, false, false);
      this.result = result;
    }
  }

  /**
   * Integrate a function numerically.
   *
   * @param function the function which should be integrated.
   * @param variable the integration variable
   * @param min Lower bound of the integration interval.
   * @param max Upper bound of the integration interval.
   * @param method the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid,
   *        GaussKronrod (or GlobalAdaptive), ClenshawCurtisRule, DoubleExponential,
   *        GaussLobattoRule, NewtonCotesRule
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
    return integrateDouble(f, xVar, min, max, method, maxPoints, maxIterations, rest,
        DEFAULT_TOLERANCE, DEFAULT_TOLERANCE);
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

  /**
   * Integrate with the explicitly requested <code>method</code>.
   *
   * @throws NonConvergence if the QUADPACK rule did not converge
   */
  private static double integrateDouble(UnaryNumerical f, ISymbol xVar, final double min,
      final double max, String method, int maxPoints, int maxIterations, IAST rest, double epsabs,
      double epsrel) throws MathIllegalStateException {
    if ("Simpson".equalsIgnoreCase(method)) {
      return new SimpsonIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("Romberg".equalsIgnoreCase(method)) {
      return new RombergIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("Trapezoid".equalsIgnoreCase(method)) {
      return new TrapezoidIntegrator().integrate(maxIterations, f, min, max);
    }
    if ("GaussKronrod".equalsIgnoreCase(method) || "GlobalAdaptive".equalsIgnoreCase(method)) {
      return quadpack(f::value, min, max, epsabs, epsrel, maxIterations);
    }
    Quadrature quadrature = null;
    if ("ClenshawCurtisRule".equalsIgnoreCase(method)) {
      quadrature = new ClenshawCurtis(epsrel, maxIterations);
    } else if ("DoubleExponential".equalsIgnoreCase(method)) {
      quadrature = new TanhSinh(epsrel, maxIterations);
    } else if ("GaussLobattoRule".equalsIgnoreCase(method)) {
      quadrature = new GaussLobatto(epsrel, maxIterations);
    } else if ("NewtonCotesRule".equalsIgnoreCase(method)) {
      quadrature = new NewtonCotes(epsrel, maxIterations);
    }
    if (quadrature != null) {
      QuadratureResult result = quadrature.integrate(f::value, min, max);
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
      return quadpack(f::value, min, max, epsabs, epsrel, maxIterations);
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
      double[] breakPoints, String method, int maxPoints, int maxIterations, IAST rest,
      double epsabs, double epsrel) throws MathIllegalStateException {
    double sum = 0.0;
    double lower = min;
    for (int i = 0; i <= breakPoints.length; i++) {
      double upper = i < breakPoints.length ? breakPoints[i] : max;
      double piece = integrateDouble(f, xVar, lower, upper, method, maxPoints, maxIterations, rest,
          epsabs, epsrel);
      if (!Double.isFinite(piece)) {
        return piece;
      }
      sum += piece;
      lower = upper;
    }
    return sum;
  }

  /**
   * The points strictly inside <code>(min, max)</code> where the integrand has a kink, a jump or a
   * pole which the symbolic form reveals - the pre-splitting a quadrature rule cannot do itself:
   * <ul>
   * <li>the real zeros and poles of the arguments of <code>Abs, RealAbs, Sign, UnitStep,
   * HeavisideTheta</code>, of the differences of the arguments of <code>Max, Min</code>, of an
   * argument of <code>Clip</code> minus its bounds, and of <code>lhs - rhs</code> for the
   * comparisons in the conditions of <code>Piecewise</code> and <code>Boole</code>, where these are
   * rational in <code>x</code>;</li>
   * <li>where a linear argument of <code>Floor, Ceiling, Round, IntegerPart, FractionalPart</code>
   * crosses an integer (a half integer for <code>Round</code>);</li>
   * <li>the real poles of the integrand itself, if its denominator is a polynomial in
   * <code>x</code>. A pole which becomes an endpoint is one the QUADPACK extrapolation can either
   * integrate or recognize as divergent.</li>
   * </ul>
   * A rule steps over a kink when none of its nodes falls where the argument changes sign: all 21
   * Gauss-Kronrod nodes of <code>Abs(x^2-2*x)</code> on <code>[-10, 10]</code> miss
   * <code>(0, 2)</code>, the rule integrates <code>x^2-2*x</code> exactly and the error estimate
   * says it converged.
   *
   * @return the sorted break points, possibly empty
   */
  private static double[] breakPoints(IExpr function, ISymbol x, double min, double max,
      EvalEngine engine) {
    TreeSet<Double> points = new TreeSet<Double>();
    // N(Integrate(...)) arrives in numeric mode, where Together(x^2-2*x) turns into the
    // non-polynomial -2.0*x+x^2.0
    boolean numericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(false);
      collectBreakPoints(function, x, min, max, points, engine);
      if (function.isFree(S.NIntegrate) && function.leafCount() < 300) {
        IExpr together = engine.evaluate(F.Together(function));
        addRealRoots(engine.evaluate(F.Denominator(together)), x, min, max, points, engine);
      }
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

  private static void collectBreakPoints(IExpr expr, ISymbol x, double min, double max,
      Set<Double> points, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(x)) {
      return;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head == S.Abs || head == S.RealAbs || head == S.Sign || head == S.UnitStep
        || head == S.HeavisideTheta) {
      for (IExpr arg : ast) {
        addZerosAndPoles(arg, x, min, max, points, engine);
      }
    } else if ((head == S.Max || head == S.Min) && ast.argSize() <= 5) {
      IAST args = ast.isAST1() && ast.arg1().isList() ? (IAST) ast.arg1() : ast;
      for (int i = 1; i < args.size(); i++) {
        for (int j = i + 1; j < args.size(); j++) {
          addZerosAndPoles(F.Subtract(args.get(i), args.get(j)), x, min, max, points, engine);
        }
      }
    } else if (head == S.Clip && ast.argSize() >= 1) {
      IExpr lower = F.CN1;
      IExpr upper = F.C1;
      if (ast.argSize() >= 2 && ast.arg2().isList2()) {
        lower = ast.arg2().first();
        upper = ast.arg2().second();
      }
      addZerosAndPoles(F.Subtract(ast.arg1(), lower), x, min, max, points, engine);
      addZerosAndPoles(F.Subtract(ast.arg1(), upper), x, min, max, points, engine);
    } else if ((head == S.Floor || head == S.Ceiling || head == S.Round || head == S.IntegerPart
        || head == S.FractionalPart) && ast.isAST1()) {
      addIntegerCrossings(ast.arg1(), x, min, max, head == S.Round ? 0.5 : 0.0, points);
    } else if (ast.isAST(S.Boole, 2)) {
      // the indicator jumps where its condition changes
      addComparisonPoints(ast.arg1(), x, min, max, points, engine);
    } else if (ast.isAST(S.Piecewise) && ast.arg1().isList()) {
      for (IExpr pair : (IAST) ast.arg1()) {
        if (pair.isList2()) {
          addComparisonPoints(pair.second(), x, min, max, points, engine);
        }
      }
    }
    for (IExpr arg : ast) {
      collectBreakPoints(arg, x, min, max, points, engine);
    }
  }

  /** The points where the two sides of the comparisons in <code>condition</code> are equal. */
  private static void addComparisonPoints(IExpr condition, ISymbol x, double min, double max,
      Set<Double> points, EvalEngine engine) {
    if (!condition.isAST() || condition.isFree(x)) {
      return;
    }
    IAST ast = (IAST) condition;
    if (ast.isAST2() && (ast.isAST(S.Less) || ast.isAST(S.LessEqual) || ast.isAST(S.Greater)
        || ast.isAST(S.GreaterEqual) || ast.isAST(S.Equal) || ast.isAST(S.Unequal))) {
      addZerosAndPoles(F.Subtract(ast.arg1(), ast.arg2()), x, min, max, points, engine);
      return;
    }
    if (ast.isAST(S.Less) || ast.isAST(S.LessEqual) || ast.isAST(S.Greater)
        || ast.isAST(S.GreaterEqual)) {
      // a < x < b
      for (int i = 1; i < ast.size(); i++) {
        addZerosAndPoles(F.Subtract(ast.get(i), x), x, min, max, points, engine);
      }
      return;
    }
    for (IExpr arg : ast) {
      addComparisonPoints(arg, x, min, max, points, engine);
    }
  }

  /** The real zeros and poles of <code>expr</code>, if it is rational in <code>x</code>. */
  private static void addZerosAndPoles(IExpr expr, ISymbol x, double min, double max,
      Set<Double> points, EvalEngine engine) {
    if (expr.isFree(x)) {
      return;
    }
    if (expr.isPolynomial(F.list(x))) {
      // Together would pull a rationalized factor out of inexact coefficients
      addRealRoots(engine.evaluate(F.Expand(expr)), x, min, max, points, engine);
      return;
    }
    IExpr together = engine.evaluate(F.Together(expr));
    addRealRoots(engine.evaluate(F.Numerator(together)), x, min, max, points, engine);
    addRealRoots(engine.evaluate(F.Denominator(together)), x, min, max, points, engine);
  }

  /**
   * The points where the linear <code>arg</code> crosses <code>n + offset</code> for an integer
   * <code>n</code>. None if there are more than {@link #MAX_STEP_BREAK_POINTS}.
   */
  private static void addIntegerCrossings(IExpr arg, ISymbol x, double min, double max,
      double offset, Set<Double> points) {
    IExpr[] linear = arg.linear(x);
    if (linear == null) {
      return;
    }
    double l0 = linear[0].evalfNaN();
    double l1 = linear[1].evalfNaN();
    if (!Double.isFinite(l0) || !Double.isFinite(l1) || l1 == 0.0) {
      return;
    }
    double from = Math.min(l0 + l1 * min, l0 + l1 * max) - offset;
    double to = Math.max(l0 + l1 * min, l0 + l1 * max) - offset;
    if (to - from > MAX_STEP_BREAK_POINTS) {
      return;
    }
    double margin = 1.0e-12 * (max - min);
    for (double n = Math.ceil(from); n <= to; n++) {
      double point = (n + offset - l0) / l1;
      if (point > min + margin && point < max - margin) {
        points.add(point);
      }
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
    // keep a margin, a root at an endpoint needs no split
    double margin = 1.0e-12 * (max - min);
    double[] coefficients = doubleCoefficients(polynomial, x, engine);
    if (coefficients != null && coefficients.length == 3 && coefficients[2] != 0.0) {
      // a quadratic in closed form: the roots of a Boole(p^2 + q^2 < 1) condition have to be
      // exact to machine precision, or the outer integral of a nested NIntegrate sees noise
      double c = coefficients[0];
      double b = coefficients[1];
      double a = coefficients[2];
      double discriminant = b * b - 4.0 * a * c;
      if (discriminant > 0.0) {
        double q = -0.5 * (b + Math.copySign(Math.sqrt(discriminant), b));
        for (double value : new double[] {q / a, c / q}) {
          if (value > min + margin && value < max - margin) {
            points.add(value);
          }
        }
      }
      return;
    }
    IAST roots = RootsFunctions.roots(polynomial, true, F.list(x), engine);
    if (roots.isNIL()) {
      return;
    }
    for (IExpr root : roots) {
      if (root.isReal()) {
        double value = polish(coefficients, root.evalf());
        if (value > min + margin && value < max - margin) {
          points.add(value);
        }
      }
    }
  }

  /**
   * The coefficients <code>{c0, c1, ...}</code> of a univariate polynomial as doubles, or
   * <code>null</code>.
   */
  private static double[] doubleCoefficients(IExpr polynomial, ISymbol x, EvalEngine engine) {
    IExpr list = engine.evaluate(F.CoefficientList(polynomial, x));
    if (!list.isList() || list.argSize() == 0) {
      return null;
    }
    double[] coefficients = new double[list.argSize()];
    for (int i = 0; i < coefficients.length; i++) {
      coefficients[i] = list.getAt(i + 1).evalfNaN();
      if (!Double.isFinite(coefficients[i])) {
        return null;
      }
    }
    return coefficients;
  }

  /** A few Newton steps on the polynomial, keeping the root when they do not improve it. */
  private static double polish(double[] coefficients, double root) {
    if (coefficients == null || coefficients.length < 2) {
      return root;
    }
    double best = root;
    double bestValue = Math.abs(horner(coefficients, root, false));
    for (int i = 0; i < 4; i++) {
      double derivative = horner(coefficients, root, true);
      if (derivative == 0.0) {
        break;
      }
      root -= horner(coefficients, root, false) / derivative;
      double value = Math.abs(horner(coefficients, root, false));
      if (!(value < bestValue)) {
        break;
      }
      best = root;
      bestValue = value;
    }
    return best;
  }

  /** The polynomial, or its derivative, at <code>x</code>. */
  private static double horner(double[] coefficients, double x, boolean derivative) {
    double result = 0.0;
    for (int i = coefficients.length - 1; i >= (derivative ? 1 : 0); i--) {
      result = result * x + (derivative ? i * coefficients[i] : coefficients[i]);
    }
    return result;
  }

  /**
   * QAGS on a finite, QAGI on an infinite range.
   *
   * @throws NonConvergence if the rule did not converge
   */
  private static double quadpack(DoubleUnaryOperator f, double min, double max, double epsabs,
      double epsrel, int maxEvaluations) {
    QuadratureResult result =
        new GaussKronrod(epsabs, epsrel, maxEvaluations).integrate(f, min, max);
    if (result.status != QuadratureResult.STATUS_OK) {
      throw new NonConvergence(result);
    }
    return result.estimate;
  }

  /**
   * The <code>Method -> Automatic</code> strategy over <code>[min, max]</code>, piece by piece
   * between the <code>breakPoints</code>: QAGS or QAGI (the QUADPACK port in {@link GaussKronrod}).
   * <ul>
   * <li>A piece which stops at a sample that cannot be evaluated inside it - the pole of
   * <code>1/Sin(x)</code> is the centre node of <code>[-1, 1]</code> - is split there, so the point
   * becomes an endpoint the extrapolation can judge (at most {@link #MAX_SAMPLE_SPLITS}
   * times).</li>
   * <li>A piece which fails with anything but "divergent" gets a second try with the tanh-sinh
   * rule, which copes with endpoint behaviour it was not told about.</li>
   * </ul>
   *
   * @return the sum over the pieces; its status is the first failure, which stops the loop
   */
  private static QuadratureResult integrateAutomatic(UnaryNumerical sampler, double min, double max,
      double[] breakPoints, double epsabs, double epsrel, int maxEvaluations) {
    // value() turns a sample which cannot be evaluated into NaN, applyAsDouble() rethrows
    DoubleUnaryOperator f = sampler::value;
    GaussKronrod qags = new GaussKronrod(epsabs, epsrel, maxEvaluations);
    ArrayDeque<double[]> pieces = new ArrayDeque<double[]>();
    double lower = min;
    for (int i = 0; i <= breakPoints.length; i++) {
      double upper = i < breakPoints.length ? breakPoints[i] : max;
      pieces.add(new double[] {lower, upper});
      lower = upper;
    }
    double sum = 0.0;
    double error = 0.0;
    int evaluations = 0;
    int sampleSplits = 0;
    while (!pieces.isEmpty()) {
      double[] range = pieces.poll();
      QuadratureResult piece = qags.integrate(f, range[0], range[1]);
      evaluations += piece.evaluations;
      if (piece.status == QuadratureResult.STATUS_BAD_INTEGRAND && sampleSplits < MAX_SAMPLE_SPLITS
          && piece.worstPoint > range[0] && piece.worstPoint < range[1]) {
        sampleSplits++;
        pieces.addFirst(new double[] {piece.worstPoint, range[1]});
        pieces.addFirst(new double[] {range[0], piece.worstPoint});
        continue;
      }
      if (piece.status != QuadratureResult.STATUS_OK
          && piece.status != QuadratureResult.STATUS_DIVERGENT) {
        QuadratureResult tanhSinh =
            new TanhSinh(Math.max(epsrel, epsabs), maxEvaluations).integrate(f, range[0], range[1]);
        evaluations += tanhSinh.evaluations;
        if (tanhSinh.converged && Double.isFinite(tanhSinh.estimate)) {
          piece = new QuadratureResult(tanhSinh.estimate, piece.error, piece.evaluations,
              QuadratureResult.STATUS_OK, Double.NaN);
        }
      }
      if (piece.status != QuadratureResult.STATUS_OK) {
        return new QuadratureResult(sum + piece.estimate, error + piece.error, evaluations,
            piece.status, piece.worstPoint);
      }
      sum += piece.estimate;
      error += piece.error;
    }
    return new QuadratureResult(sum, error, evaluations, QuadratureResult.STATUS_OK, Double.NaN);
  }

  /**
   * <code>g(x)*Sin(w*x+c)</code> or <code>g(x)*Cos(w*x+c)</code> over an infinite range, with
   * numeric <code>w != 0</code> and <code>c</code> and a <code>g</code> which tends to
   * <code>0</code> towards each infinite limit: sum the half periods with {@link OscillatoryTail}.
   *
   * @return <code>null</code> if the integrand does not have that shape
   */
  private static QuadratureResult integrateOscillatory(IExpr function, ISymbol x, UnaryNumerical f,
      double min, double max, double epsabs, double epsrel, int maxEvaluations, EvalEngine engine) {
    if (Double.isFinite(min) && Double.isFinite(max)) {
      return null;
    }
    IAST factors = function.isTimes() ? (IAST) function : F.Times(function);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (!(factor.isSin() || factor.isCos())) {
        continue;
      }
      IExpr[] linear = factor.first().linear(x);
      if (linear == null) {
        continue;
      }
      double c = linear[0].evalfNaN();
      double w = linear[1].evalfNaN();
      if (!Double.isFinite(c) || !Double.isFinite(w) || w == 0.0) {
        continue;
      }
      IExpr g = factors.removeAtCopy(i).oneIdentity1();
      if (g.isFree(S.Sin) && g.isFree(S.Cos)) {
        boolean cosine = factor.isCos();
        return oscillatoryRange(g, x, f, min, max, w, c, cosine, epsabs, epsrel, maxEvaluations,
            engine);
      }
    }
    return null;
  }

  private static QuadratureResult oscillatoryRange(IExpr g, ISymbol x, UnaryNumerical f, double min,
      double max, double w, double c, boolean cosine, double epsabs, double epsrel,
      int maxEvaluations, EvalEngine engine) {
    boolean upperInfinite = max == Double.POSITIVE_INFINITY;
    boolean lowerInfinite = min == Double.NEGATIVE_INFINITY;
    if ((upperInfinite && !limitIsZero(g, x, F.CInfinity, engine))
        || (lowerInfinite && !limitIsZero(g, x, F.CNInfinity, engine))) {
      // Wynn acceleration would give the divergent Integrate(Sin(x),{x,0,Infinity}) the value 1
      return null;
    }
    if (upperInfinite && lowerInfinite) {
      QuadratureResult right =
          OscillatoryTail.integrate(f::value, 0.0, w, c, cosine, epsabs, epsrel, maxEvaluations);
      if (right.status != QuadratureResult.STATUS_OK) {
        return right;
      }
      QuadratureResult left = OscillatoryTail.integrate(t -> f.value(-t), 0.0, -w, c, cosine,
          epsabs, epsrel, maxEvaluations);
      return sum(right, left);
    }
    if (upperInfinite) {
      return OscillatoryTail.integrate(f::value, min, w, c, cosine, epsabs, epsrel, maxEvaluations);
    }
    // Integrate(f(x), {x, -Infinity, b}) == Integrate(f(-t), {t, -b, Infinity})
    return OscillatoryTail.integrate(t -> f.value(-t), -max, -w, c, cosine, epsabs, epsrel,
        maxEvaluations);
  }

  private static QuadratureResult sum(QuadratureResult first, QuadratureResult second) {
    QuadratureResult worse = first.status >= second.status ? first : second;
    return new QuadratureResult(first.estimate + second.estimate, first.error + second.error,
        first.evaluations + second.evaluations, worse.status, worse.worstPoint);
  }

  private static boolean limitIsZero(IExpr g, ISymbol x, IExpr point, EvalEngine engine) {
    try {
      return engine.evalQuiet(F.Limit(g, F.Rule(x, point))).isZero();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  public NIntegrate() {
    // default ctor
  }

  /**
   * Function for <a href="http://en.wikipedia.org/wiki/Numerical_integration">numerical
   * integration</a> of univariate real functions.
   *
   * <p>
   * See the class description for the <code>Method -&gt; Automatic</code> strategy.
   */
  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (!ast.arg2().isList()) {
      return F.NIL;
    }
    IAST list = (IAST) ast.arg2();
    IExpr function = ast.arg1();
    if (function.isList()) {
      // a list of integrands is integrated element by element
      final IAST call = originalAST;
      return F.mapList((IAST) function, f -> engine.evaluate(call.setAtCopy(1, f)));
    }
    int maxPoints = DEFAULT_MAX_POINTS;
    if (!option[1].isAutomatic()) {
      maxPoints = option[1].toIntDefault(DEFAULT_MAX_POINTS);
    }
    int maxIterations = DEFAULT_MAX_ITERATIONS;
    if (!option[2].isAutomatic()) {
      maxIterations = option[2].toIntDefault(DEFAULT_MAX_ITERATIONS);
    }
    int precisionGoal = 16; // automatic scale value
    double epsrel = DEFAULT_TOLERANCE;
    if (!option[3].isAutomatic()) {
      precisionGoal = option[3].toIntDefault(-1);
      if (precisionGoal <= 0) {
        // Inappropriate parameter: `1`.
        return Errors.printMessage(ast.topHead(), "par", F.List(S.PrecisionGoal), engine);
      }
      epsrel = Math.max(Math.pow(10.0, -precisionGoal), MIN_RELATIVE_TOLERANCE);
    }
    double epsabs = DEFAULT_TOLERANCE;
    if (!option[4].isAutomatic()) {
      if (option[4].isInfinity()) {
        epsabs = 0.0;
      } else {
        int accuracyGoal = option[4].toIntDefault(-1);
        if (accuracyGoal <= 0) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(ast.topHead(), "par", F.List(S.AccuracyGoal), engine);
        }
        epsabs = Math.pow(10.0, -accuracyGoal);
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
    } else if (list.isList() && list.argSize() > 3 && list.arg1().isSymbol()) {
      // NIntegrate(f, {x, x0, x1, ..., xn}) - along the path through the points, straight
      // segments between complex ones (a contour integral), split points on the real line
      IExpr sum = F.C0;
      for (int i = 2; i < list.argSize(); i++) {
        IExpr segment =
            engine.evaluate(ast.setAtCopy(2, F.list(list.arg1(), list.get(i), list.get(i + 1))));
        if (!segment.isNumber()) {
          return F.NIL;
        }
        sum = engine.evaluate(F.Plus(sum, segment));
      }
      return sum;
    } else if (!list.isAST3() || !list.arg1().isSymbol()) {
      return F.NIL;
    }

    if (function.isEqual()) {
      IAST equalAST = (IAST) function;
      function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
    }
    final IExpr x = list.arg1();
    final String method = option[0].isAutomatic() ? null : option[0].toString();
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
      final ISymbol xSymbol = (ISymbol) x;
      UnaryNumerical sampler = createSampler(function, xSymbol, engine);
      double[] breakPoints;
      if (minDouble == Double.NEGATIVE_INFINITY && maxDouble == Double.POSITIVE_INFINITY) {
        // QAGI folds (-Infinity, Infinity) into f(x)+f(-x), which cancels the divergent halves
        // of an odd integrand like x to 0 - integrate the halves separately
        breakPoints = new double[] {0.0};
      } else if (minDouble == Double.NEGATIVE_INFINITY || maxDouble == Double.POSITIVE_INFINITY) {
        breakPoints = new double[0];
      } else {
        breakPoints = breakPoints(function, xSymbol, minDouble, maxDouble, engine);
      }

      QuadratureResult failed = null;
      RuntimeException failure = null;
      if (method != null) {
        // an explicit method runs as requested, without switching
        try {
          double result = integratePieces(sampler, xSymbol, minDouble, maxDouble, breakPoints,
              method, maxPoints, maxIterations, list.rest(), epsabs, epsrel);
          if (Double.isFinite(result)) {
            return Num.valueOf(sign * Precision.round(result, precisionGoal));
          }
        } catch (NonConvergence nc) {
          failed = nc.result;
        } catch (MathRuntimeException | ArgumentTypeException e) {
          failure = e;
        }
      } else {
        // Automatic: an oscillatory factor on an infinite range, else QAGS/QAGI piece by piece
        // with a switch to tanh-sinh for pieces which fail without looking divergent
        QuadratureResult result = integrateOscillatory(function, xSymbol, sampler, minDouble,
            maxDouble, epsabs, epsrel, maxIterations, engine);
        if (result == null || result.status != QuadratureResult.STATUS_OK) {
          result = integrateAutomatic(sampler, minDouble, maxDouble, breakPoints, epsabs, epsrel,
              maxIterations);
        }
        if (result.status == QuadratureResult.STATUS_OK && Double.isFinite(result.estimate)) {
          return Num.valueOf(sign * Precision.round(result.estimate, precisionGoal));
        }
        failed = result;
      }

      // CAS "SymbolicProcessing" fallback for oscillatory/infinite integrals: solve
      // symbolically, then evaluate the result numerically. Not in numeric mode: from
      // N(Integrate(...)) Integrate would delegate straight back to NIntegrate. Quiet: the
      // NIntegrate message below says what went wrong, an Integrate::idiv beside it only
      // repeats it
      final IAST symbolicIntegral = F.Integrate(function, list);
      IExpr symbolic = engine.withQuietMode(() -> engine.evaluateNonNumeric(symbolicIntegral));
      if (symbolic.isFree(S.Integrate)) {
        IExpr numeric = engine.evalQuiet(F.N(symbolic));
        if (numeric.isNumber()) {
          if (numeric.isReal()) {
            double val = numeric.evalfNaN();
            if (Double.isFinite(val)) {
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
      if (failed != null) {
        return printNonConvergence(ast.topHead(), failed, xSymbol, engine);
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
   * Message <code>ncvb</code> for a subdivision limit or a divergent-looking integral, else message
   * <code>slwcon</code>.
   */
  private static IExpr printNonConvergence(ISymbol head, QuadratureResult failed, ISymbol x,
      EvalEngine engine) {
    if (failed.status == QuadratureResult.STATUS_LIMIT
        || failed.status == QuadratureResult.STATUS_DIVERGENT
        || failed.status == QuadratureResult.STATUS_BAD_INTEGRAND) {
      // each bisection adds one subinterval for 42 more samples of the 21 point rule
      int bisections = Math.max(0, (failed.evaluations + 21) / 42 - 1);
      IExpr near = numOrIndeterminate(failed.worstPoint);
      IExpr estimate = numOrIndeterminate(failed.estimate);
      IExpr error = numOrIndeterminate(failed.error);
      // NIntegrate failed to converge to prescribed accuracy after `1` recursive bisections in
      // `2` near `3` = `4`. NIntegrate obtained `5` and `6` for the integral and error estimates.
      return Errors.printMessage(head, "ncvb",
          F.List(F.ZZ(bisections), x, F.List(x), F.List(near), estimate, error), engine);
    }
    // Numerical integration converging too slowly; suspect one of the following: singularity,
    // value of the integration is 0, highly oscillatory integrand, or WorkingPrecision too small.
    return Errors.printMessage(head, "slwcon", F.CEmptyList, engine);
  }

  private static IExpr numOrIndeterminate(double value) {
    return Double.isFinite(value) ? F.num(value) : S.Indeterminate;
  }

  /**
   * Fallback when a limit of integration has no real double value: integrate along a complex line,
   * or print the message <code>nlim</code> if a limit is not numeric at all.
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
        S.Method, S.MaxPoints, S.MaxIterations, S.PrecisionGoal, S.AccuracyGoal};
  }

  private static IExpr[] defaultOptionValues() {
    return new IExpr[] {//
        S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic};
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDFIRST);
    setOptions(newSymbol, defaultOptionKeys(), defaultOptionValues());
  }
}
