package org.matheclipse.core.reflection.system;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import org.hipparchus.analysis.CalculusFieldUnivariateFunction;
import org.hipparchus.analysis.integration.IterativeLegendreGaussIntegrator;
import org.hipparchus.analysis.integration.RombergIntegrator;
import org.hipparchus.analysis.integration.SimpsonIntegrator;
import org.hipparchus.analysis.integration.TrapezoidIntegrator;
import org.hipparchus.analysis.integration.UnivariateIntegrator;
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
 * LegendreGauss is the default method for numerical integration
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
   * @param min Lower bound of the integration interval.
   * @param max Upper bound of the integration interval.
   * @param method the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid
   * @param maxPoints maximum number of points
   * @param maxIterations maximum number of iterations
   * @param list a list of the form <code>{x, lowerBound, upperBound}</code>, where <code>lowerBound
   *     </code> and <code>upperBound</code> are numbers which could be converted to a Java double
   *        value.
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
    // final EvalEngine engine = EvalEngine.get();
    IExpr tempFunction = F.eval(function);
    UnaryNumerical f = new UnaryNumerical(tempFunction, xVar, engine);

    UnivariateIntegrator integrator;
    if ("Simpson".equalsIgnoreCase(method)) {
      integrator = new SimpsonIntegrator();
    } else if ("Romberg".equalsIgnoreCase(method)) {
      integrator = new RombergIntegrator();
    } else if ("Trapezoid".equalsIgnoreCase(method)) {
      integrator = new TrapezoidIntegrator();
    } else if ("GaussKronrod".equalsIgnoreCase(method)) {
      return gausKronrodRule(maxIterations, f, min, max);
    } else if ("ClenshawCurtisRule".equalsIgnoreCase(method)) {
      Quadrature quadrature = new ClenshawCurtis(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, rest));
    } else if ("DoubleExponential".equalsIgnoreCase(method)) {
      Quadrature quadrature = new TanhSinh(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, rest));
    } else if ("GaussLobattoRule".equalsIgnoreCase(method)) {
      Quadrature quadrature = new GaussLobatto(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, rest));

    } else if ("NewtonCotesRule".equalsIgnoreCase(method)) {
      Quadrature quadrature = new NewtonCotes(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, rest));
    } else {
      if (maxPoints > 1000) {
        // github 150 - avoid StackOverflow from recursion
        // see also https://github.com/Hipparchus-Math/hipparchus/issues/61
        throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_LARGE, maxPoints,
            1000);
      }
      if (min == Double.NEGATIVE_INFINITY || max == Double.POSITIVE_INFINITY) {
        return gausKronrodRule(maxIterations, f, min, max);
      }

      // default: LegendreGauss
      GaussIntegratorFactory factory = new GaussIntegratorFactory();
      GaussIntegrator gaussIntegrator = factory.legendre(maxPoints, min, max);
      return gaussIntegrator.integrate(f);
    }
    return integrator.integrate(maxIterations, f, min, max);
  }

  private static double gausKronrodRule(int maxIterations, UnaryNumerical function, double min,
      double max) {

    class UnaryFunction implements UnaryOperator<double[]> {

      private AtomicBoolean gracefulStop;
      private int iterationCounter;

      public UnaryFunction() {
        this.gracefulStop = new AtomicBoolean(false);
        this.iterationCounter = 0;
      }

      @Override
      public double[] apply(double[] x) {
        final int n = x.length;
        iterationCounter += n;

        if (iterationCounter > maxIterations) {
          while (!gracefulStop.compareAndSet(false, true)) {
            // wait for gracefulStop to turn false again
          }
          throw new MathIllegalStateException(LocalizedCoreFormats.MAX_COUNT_EXCEEDED,
              maxIterations);
        }
        return UnaryNumerical.vectorValue(function, x);
      }

    };

    UnaryFunction unaryFunction = new UnaryFunction();
    double[] result = AdaptiveQuadrature.integrate(unaryFunction, min, max,
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

    if (ast.arg2().isList()) {
      IAST list = (IAST) ast.arg2();
      IExpr function = ast.arg1();
      int maxPoints = DEFAULT_MAX_POINTS;
      if (option[1] != S.Automatic) {
        maxPoints = option[1].toIntDefault(DEFAULT_MAX_POINTS);
      }
      int maxIterations = DEFAULT_MAX_ITERATIONS;
      if (option[2] != S.Automatic) {
        maxIterations = option[2].toIntDefault(DEFAULT_MAX_ITERATIONS);
      }
      int precisionGoal = 16; // automatic scale value
      if (option[3] != S.Automatic) {
        precisionGoal = option[3].toIntDefault(-1);
        if (precisionGoal <= 0) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(ast.topHead(), "par", F.List(S.PrecisionGoal), engine);
          // precisionGoal = 16;
        }
      }

      if (list.isAST3() && list.arg1().isSymbol()) {
        IExpr x = list.arg1();
        if (function.isEqual()) {
          IAST equalAST = (IAST) function;
          function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
        }
        String method = "Romberg";
        if (option[0] != S.Automatic) {
          method = option[0].toString();
        } else {
          if (list.arg2().isInfinite() || list.arg3().isInfinite()) {
            method = "LegendreGauss";
          } else if (!function.isFree(a -> a == S.Abs || a == S.RealAbs, true)) {
            method = "LegendreGauss";
          } else if (!function.isFree(a -> a.isPower() && a.exponent().isFree(x), false)) {
            method = "GaussKronrod";
          }
        }
        try {
          double min = list.arg2().evalf();
          double max = list.arg3().evalf();
          try {
            if (!function.isFreeAST(h -> h == S.Boole)) {
              IExpr temp = Integrate.integrateBooleTimesFxRegion(function, list, true, engine);
              if (temp.isPresent()) {
                return temp;
              }
            }
            double result = integrateDouble(function, x, min, max, method, maxPoints, maxIterations,
                list.rest(), engine);
            result = Precision.round(result, precisionGoal);
            return Num.valueOf(result);
          } catch (MathIllegalArgumentException | MathIllegalStateException miae) {
            // especially max iterations exceeded
            return Errors.printMessage(ast.topHead(), miae, engine);
          } catch (MathRuntimeException mre) {
            return Errors.printMessage(ast.topHead(), mre, engine);
          } catch (RuntimeException e) {
            Errors.rethrowsInterruptException(e);
            return Errors.printMessage(ast.topHead(), e, engine);
          }
        } catch (ArgumentTypeException ate) {
          //
        }
        try {
          Complex min = list.arg2().evalfc();
          Complex max = list.arg3().evalfc();
          try {
            Complex complexResult =
                integrateComplex(function, list.arg1(), min, max, maxIterations, maxPoints, engine);
            // complexResult = Precision.round(complexResult, precisionGoal);
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
        } catch (ArgumentTypeException atex) {

        }
      }
    }
    return F.NIL;
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
    UnaryNumerical f = new UnaryNumerical(tempFunction, xVar, engine);

    if (maxPoints > 0) {
      maxPoints = maxPoints / 4;
    }
    Complex complexResult = integrateComplex(f, min, max, maxIterations, maxPoints);
    return complexResult;
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

  private static IExpr[] defaultOptionValues(boolean jsForm, boolean joined) {
    return new IExpr[] {//
        S.Automatic, S.Automatic, S.Automatic, S.Automatic};
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDFIRST);
    setOptions(newSymbol, defaultOptionKeys(), defaultOptionValues(true, false));
  }
}
