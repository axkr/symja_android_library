package org.matheclipse.core.reflection.system;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import org.hipparchus.analysis.integration.RombergIntegrator;
import org.hipparchus.analysis.integration.SimpsonIntegrator;
import org.hipparchus.analysis.integration.TrapezoidIntegrator;
import org.hipparchus.analysis.integration.UnivariateIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegratorFactory;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.Precision;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
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
 *
 *
 * <pre>
 * NIntegrate(f, {x,a,b})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * computes the numerical univariate real integral of <code>f</code> with respect to <code>x
 * </code> from <code>a</code> to <code>b</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})
 * -0.0208333333333333
 * </pre>
 *
 * <p>
 * LegendreGauss is the default method for numerical integration
 *
 * <pre>
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;LegendreGauss)
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
 * </pre>
 *
 * <p>
 * Other options include <code>MaxIterations</code> and <code>MaxPoints</code>
 *
 * <pre>
 * &gt;&gt; NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method-&gt;Trapezoid, MaxIterations-&gt;5000)
 * -0.0208333271245165
 * </pre>
 */
public class NIntegrate extends AbstractFunctionEvaluator {

  public static final int DEFAULT_MAX_POINTS = 100;
  public static final int DEFAULT_MAX_ITERATIONS = 10000;

  // public final static ISymbol LegendreGauss = F
  // .initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "legendregauss" : "LegendreGauss");

  /**
   * Integrate a function numerically.
   *
   * @param method the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid
   * @param list a list of the form <code>{x, lowerBound, upperBound}</code>, where <code>lowerBound
   *     </code> and <code>upperBound</code> are numbers which could be converted to a Java double
   *        value.
   * @param min Lower bound of the integration interval.
   * @param max Upper bound of the integration interval.
   * @param function the function which should be integrated.
   * @param maxPoints maximum number of points
   * @param maxIterations maximum number of iterations
   * @return
   * @throws MathIllegalStateException
   */
  public static double integrate(String method, IAST list, final double min, final double max,
      IExpr function, int maxPoints, int maxIterations) throws MathIllegalStateException {
    if (!list.arg1().isSymbol()) {
      // `1` is not a valid variable.
      String str = Errors.getMessage("ivar", F.list(list.arg1()), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    ISymbol xVar = (ISymbol) list.arg1();
    final EvalEngine engine = EvalEngine.get();
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
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, list.rest()));
    } else if ("DoubleExponential".equalsIgnoreCase(method)) {
      Quadrature quadrature = new TanhSinh(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, list.rest()));
    } else if ("GaussLobattoRule".equalsIgnoreCase(method)) {
      Quadrature quadrature = new GaussLobatto(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, list.rest()));

    } else if ("NewtonCotesRule".equalsIgnoreCase(method)) {
      Quadrature quadrature = new NewtonCotes(Config.SPECIAL_FUNCTIONS_TOLERANCE, maxIterations);
      QuadratureResult result = quadrature.integrate(f, min, max);
      if (result.converged) {
        return result.estimate;
      }
      // NIntegrate failed to converge after `1` refinements in `2` in the region `3`.
      throw new ArgumentTypeException("ncvi", F.List(F.ZZ(result.evaluations), xVar, list.rest()));
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
      GaussIntegrator integ = factory.legendre(maxPoints, min, max);
      return integ.integrate(f);
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
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    String method = "";
    int maxPoints = DEFAULT_MAX_POINTS;
    int maxIterations = DEFAULT_MAX_ITERATIONS;
    int precisionGoal = 16; // automatic scale value
    if (ast.size() >= 4) {
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
      IExpr option = options.getOption(S.Method);
      if (option.isSymbol() || option.isString()) {
        method = option.toString();
      }
      option = options.getOption(S.MaxPoints);
      if (option.isReal()) {
        maxPoints = option.toIntDefault(-1);
        if (maxPoints <= 0) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(ast.topHead(), "par", F.List(S.MaxPoints), engine);
          // maxPoints = DEFAULT_MAX_POINTS;
        }
      }
      maxIterations = options.getOptionMaxIterations(S.MaxIterations);
      if (maxIterations == Integer.MIN_VALUE) {
        return F.NIL;
      }
      if (maxIterations < 0) {
        maxIterations = DEFAULT_MAX_ITERATIONS;
      }
      option = options.getOption(S.PrecisionGoal);
      if (option.isReal()) {
        precisionGoal = option.toIntDefault(-1);
        if (precisionGoal <= 0) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(ast.topHead(), "par", F.List(S.PrecisionGoal), engine);
          // precisionGoal = 16;
        }
      }
    }

    if (ast.arg2().isList()) {
      IAST list = (IAST) ast.arg2();
      IExpr function = ast.arg1();
      if (list.isAST3() && list.arg1().isSymbol()) {
        IExpr x = list.arg1();
        double min = list.arg2().evalf();
        double max = list.arg3().evalf();
        if (function.isEqual()) {
          IAST equalAST = (IAST) function;
          function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
        }
        if (method.isEmpty()) {
          method = "Romberg";
          if (list.arg2().isDirectedInfinity() || list.arg3().isDirectedInfinity()) {
            method = "LegendreGauss";
          } else if (!function.isFree(a -> a == S.Abs || a == S.RealAbs, true)) {
            method = "LegendreGauss";
          } else if (!function.isFree(a -> a.isPower() && a.exponent().isFree(x), false)) {
            method = "GaussKronrod";
          }
        }
        try {
          double result = integrate(method, list, min, max, function, maxPoints, maxIterations);
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
      }
      // }S
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDFIRST);
  }
}
