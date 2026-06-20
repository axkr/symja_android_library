package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.differentiation.DSFactory;
import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>ND(expr, x, x0)</code>
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a numerical approximation to the derivative of expr with respect to x at the point x0.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * <code>ND(expr, {x, n}, x0)</code>
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a numerical approximation to the derivative of order n of expr.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 * 
 * <pre>
 * <code>&gt;&gt; ND(BesselY(10.0, x), x, 1)
 * 1.20940*10^9
 *
 * &gt;&gt; ND(Cos(x)^3, {x, 2}, 1)
 * 1.82226
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 * <p>
 * <a href="D.md">D</a>, <a href="Integrate.md">Integrate</a>,
 * <a href="NIntegrate.md">NIntegrate</a>
 * </p>
 */
public class ND extends AbstractFunctionEvaluator {

  public ND() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    IExpr arg3 = ast.arg3();
    try {
      if (arg2.isList2()) {
        int order = arg2.second().toIntDefault();
        if (order <= 0) {
          // Positive integer expected at position `2` in `1`.
          Errors.printMessage(S.ND, "intp", F.List(arg2, F.C2), engine);
        }
        if (order > 0 && arg2.first().isSymbol()) {
          return partialDerivative(arg1, (ISymbol) arg2.first(), order, arg3, engine);
        }
      } else if (arg2.isSymbol()) {
        return partialDerivative(arg1, (ISymbol) arg2, 1, arg3, engine);
      }
    } catch (MathRuntimeException e) {
      return Errors.printMessage(S.ND, e, engine);
    }
    return F.NIL;
  }

  private IExpr partialDerivative(IExpr function, ISymbol variable, int order, IExpr value,
      EvalEngine engine) {
    double a3Double = Double.NaN;
    try {
      a3Double = value.evalf();
    } catch (ArgumentTypeException ve) {
      // Ignored to allow fallback to complex numerical evaluation
    }

    if (Double.isNaN(a3Double)) {
      Complex a3Complex = value.evalfc();
      if (a3Complex != null) {
        // Hipparchus FiniteDifferencesDifferentiator does not natively support
        // FieldDerivativeStructure<Complex>.
        // Using manual central finite difference approximation for complex fields.
        return complexFiniteDifference(function, variable, a3Complex, order);
      }
    } else {
      // Use Hipparchus DerivativeStructure for real-valued approximations
      DSFactory factory = new DSFactory(1, order);
      FiniteDifferencesDifferentiator differentiator =
          new FiniteDifferencesDifferentiator(15, 0.01);
      UnivariateDifferentiableFunction f =
          differentiator.differentiate(new UnaryNumerical(function, variable, false, Double.NaN, engine));
      DerivativeStructure x = factory.variable(0, a3Double);
      DerivativeStructure y = f.value(x);
      return F.num(y.getPartialDerivative(order));
    }
    return F.NIL;
  }

  /**
   * Computes complex derivatives using central finite differences for orders 1 and 2, and Cauchy's
   * integral formula for orders > 2 to ensure numerical stability.
   */
  private IExpr complexFiniteDifference(IExpr function, ISymbol variable, Complex x0, int order) {
    double hNum = 0.001;
    Complex h = new Complex(hNum, 0.0);

    if (order == 1) {
      Complex fPlus = evalComplex(function, variable, x0.add(h));
      Complex fMinus = evalComplex(function, variable, x0.subtract(h));
      if (fPlus != null && fMinus != null) {
        return F.complexNum(fPlus.subtract(fMinus).divide(h.multiply(2.0)));
      }
    } else if (order == 2) {
      Complex fPlus = evalComplex(function, variable, x0.add(h));
      Complex f0 = evalComplex(function, variable, x0);
      Complex fMinus = evalComplex(function, variable, x0.subtract(h));
      if (fPlus != null && f0 != null && fMinus != null) {
        Complex num = fPlus.subtract(f0.multiply(2.0)).add(fMinus);
        return F.complexNum(num.divide(h.multiply(h)));
      }
    } else if (order > 2) {
      // Use Cauchy's integral formula for higher order derivatives:
      // f^(n)(z0) = (n! / (N * r^n)) * sum_{j=0}^{N-1} f(z0 + r*e^{i*theta_j}) * e^{-i*n*theta_j}

      // Adapt the number of points based on the derivative order to maintain precision
      int nPoints = Math.max(64, 16 * order);
      double r = 0.25; // Contour integration radius
      Complex sum = Complex.ZERO;

      for (int j = 0; j < nPoints; j++) {
        double theta = 2.0 * Math.PI * j / nPoints;
        Complex zOffset = new Complex(r * Math.cos(theta), r * Math.sin(theta));
        Complex evalPoint = x0.add(zOffset);

        Complex fVal = evalComplex(function, variable, evalPoint);
        if (fVal == null) {
          return F.NIL;
        }

        // Calculate the phase term: e^{-i * n * theta}
        Complex phase = new Complex(Math.cos(-order * theta), Math.sin(-order * theta));
        sum = sum.add(fVal.multiply(phase));
      }

      // Calculate n!
      double factorial = 1.0;
      for (int i = 2; i <= order; i++) {
        factorial *= i;
      }

      // Multiply by the constant factor: n! / (N * r^n)
      double coefficient = factorial / (nPoints * Math.pow(r, order));
      return F.complexNum(sum.multiply(coefficient));
    }

    return F.NIL;
  }

  private Complex evalComplex(IExpr function, ISymbol variable, Complex value) {
    IExpr expr = F.subst(function, variable, F.complexNum(value));
    return expr.evalfc();
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // Allows optional arguments (like Method -> NIntegrate) for future extension
    return IFunctionEvaluator.ARGS_3_INFINITY;
  }
}
