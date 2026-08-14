package org.matheclipse.core.generic;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import org.hipparchus.analysis.CalculusFieldUnivariateFunction;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.differentiation.Derivative;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Unary numerical function for functions like Plot */
public final class UnaryNumerical implements UnaryOperator<IExpr>, UnivariateDifferentiableFunction,
    DoubleFunction<IExpr>, DoubleUnaryOperator, CalculusFieldUnivariateFunction<Complex> {
  final IExpr fUnaryFunction;
  final ISymbol fVariable;
  final ISymbol fDummyVariable;
  final EvalEngine fEngine;
  final double fDefaultValue;

  /**
   * Number of sample points for which the function could not be evaluated to a real value (NaN
   * result or evaluation exception). Not thread-safe - intended for single-threaded numerical
   * algorithms which want to report "the integrand is not numerical" diagnostics afterwards.
   */
  private int fFailureCount = 0;

  UnaryNumerical fFirstDerivative = null;

  /**
   * <p>
   * This class represents a unary function which computes both the value and the first derivative
   * of a mathematical function. The derivative is computed with respect to the input
   * {@code variable}
   * </p>
   * 
   * @param unaryFunction the unary function
   * @param variable the functions variable name
   * @param defaultValue the value {@link #applyAsDouble(double)} returns when the function cannot
   *        be evaluated to a real number; pass {@link Double#NaN} to throw an exception instead
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, double defaultValue) {
    this(unaryFunction, variable, false, true, defaultValue, EvalEngine.get());
  }

  /**
   * <p>
   * This class represents a unary function which computes both the value and the first derivative
   * of a mathematical function. The derivative is computed with respect to the input
   * {@code variable}. A {@link S#Abs} in the function is rewritten to {@link S#RealAbs} for the
   * derivative only (real input values are assumed there); the sampled function keeps {@link S#Abs}
   * and therefore still returns the modulus of complex intermediate values.
   * </p>
   *
   * @param unaryFunction the unary function
   * @param variable the functions variable name
   * @param defaultValue the value {@link #applyAsDouble(double)} returns when the function cannot
   *        be evaluated to a real number; pass {@link Double#NaN} to throw an exception instead
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, double defaultValue,
      final EvalEngine engine) {
    this(unaryFunction, variable, false, true, defaultValue, engine);
  }

  /**
   * <p>
   * This class represents a unary function which computes both the value and the first derivative
   * of a mathematical function. The derivative is computed with respect to the input
   * {@code variable}
   * </p>
   * 
   * @param unaryFunction the unary function
   * @param variable the functions variable name
   * @param useAbsReal substitute {@link S#Abs} with the {@link S#RealAbs} function <i>before
   *        differentiating</i>, because of assuming real input values ({@link S#Abs} has no
   *        numerically usable derivative). The sampled function itself keeps {@link S#Abs}, see the
   *        comment in the constructor; the flag therefore has no effect unless
   *        {@code firstDerivative} is set.
   * @param defaultValue the value {@link #applyAsDouble(double)} returns when the function cannot
   *        be evaluated to a real number; pass {@link Double#NaN} to throw an exception instead
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, boolean useAbsReal,
      double defaultValue, final EvalEngine engine) {
    this(unaryFunction, variable, false, useAbsReal, defaultValue, engine);
  }

  /**
   * <p>
   * This class represents a unary function which computes both the value and the first derivative
   * of a mathematical function. The derivative is computed with respect to the input
   * {@code variable}
   * </p>
   * 
   * @param unaryFunction the unary function
   * @param variable the functions variable name
   * @param firstDerivative if <code>true</code> evaluate the first derivative of
   *        {@code unaryFunction} directly in the constructor.
   * @param useAbsReal substitute {@link S#Abs} with the {@link S#RealAbs} function <i>before
   *        differentiating</i>, because of assuming real input values ({@link S#Abs} has no
   *        numerically usable derivative). The sampled function itself keeps {@link S#Abs}, see the
   *        comment in the constructor; the flag therefore has no effect unless
   *        {@code firstDerivative} is set.
   * @param defaultValue the value {@link #applyAsDouble(double)} returns when the function cannot
   *        be evaluated to a real number; pass {@link Double#NaN} to throw an exception instead
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, boolean firstDerivative,
      boolean useAbsReal, double defaultValue, final EvalEngine engine) {
    if (!variable.isVariable() || variable.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(variable), EvalEngine.get()));
    }
    fDefaultValue = defaultValue;
    fVariable = variable;
    fEngine = engine;
    if (firstDerivative) {
      // The Abs -> RealAbs rewrite is applied to the function which gets *differentiated*, not to
      // the sampled function below, and that asymmetry is deliberate:
      // - D(Abs(x),x) is Abs'(x), which has no numerical value, while D(RealAbs(x),x) is
      // x/RealAbs(x) and evaluates - so the derivative is unusable without the rewrite;
      // - Abs(z) of a complex-valued subexpression is its modulus (a real number), whereas
      // RealAbs(z) stays unevaluated and samples as NaN. Sampling the rewritten function would
      // turn e.g. NIntegrate(Abs(Sqrt(x-2)),{x,0,1}) from 1.21895 into a failure.
      IExpr derivand = useAbsReal ? F.substAbs(unaryFunction) : unaryFunction;
      IExpr temp = engine.evaluate(F.D(derivand, fVariable));
      fFirstDerivative =
          new UnaryNumerical(temp, fVariable, false, useAbsReal, defaultValue, engine);
    }
    fDummyVariable = F.Dummy("$" + fVariable.toString());
    fUnaryFunction = F.subst(unaryFunction, x -> x.equals(variable) ? fDummyVariable : F.NIL);
  }

  /**
   * The function expression with the dummy variable substituted by <code>value</code>.
   *
   * <p>
   * Substitution is used instead of {@link ISymbol#assignValue(IExpr)} on the dummy variable: an
   * assignment bumps the global evaluation epoch (see <code>Symbol#assignValue</code>) on
   * <b>every</b> sample point, which invalidates engine-wide caches - a numerical integration may
   * sample the function ten-thousands of times.
   */
  private IExpr substituted(final IExpr value) {
    return F.subst(fUnaryFunction, e -> e.equals(fDummyVariable) ? value : F.NIL);
  }

  @Override
  public IExpr apply(final IExpr value) {
    return fEngine.evalNumericFunction(substituted(value));
  }

  /**
   * Evaluate the {@link S#Limit} of the {@code unaryFunction} for the {@code value} in the form
   * {@code F.N( F.Limit(unaryFunction, F.Rule(variable, value)) )} and return the numerical result
   * or {@link S#Indeterminate}
   * 
   * @param value
   */
  @Deprecated
  public IExpr applyLimit(IExpr value) {
    try {
      return fEngine.evalNumericFunction(F.Limit(fUnaryFunction, F.Rule(fDummyVariable, value)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return S.Indeterminate;
    }
  }

  /**
   * Evaluate the {@code unaryFunction} for the {@code value} by substituting the {@code variable}
   * in the {@code unaryFunction} and return the numerical result or {@link S#Indeterminate}
   * 
   * @param value
   */
  @Override
  public IExpr apply(double value) {
    try {
      return fEngine.evalNumericFunction(substituted(F.num(value)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return S.Indeterminate;
    }
  }

  /**
   * Evaluate the {@code unaryFunction} for the {@code value} by substituting the {@code variable}
   * in the {@code unaryFunction} and return the double value or {@link Double#NaN}.
   * 
   * @param value the value of the limit for the given variable
   * @return the calculated double value or {@link Double#NaN}.
   */
  /** Digits to work to, or a value below one for the machine precision of a {@code double}. */
  private long fPrecision = -1;

  /**
   * Work to the given number of digits instead of machine precision.
   *
   * <p>
   * This is what {@code WorkingPrecision} asks for. It matters where the arithmetic loses more
   * significance than a {@code double} has to give -- {@code (1 - Cos[x])/x^2} near zero is the
   * usual example, which comes out as noise at machine precision and smooth above it. It costs a
   * full symbolic evaluation per sample, so it is only used when asked for.
   *
   * @param digits digits of precision, or a value below one to use machine doubles
   */
  public void setPrecision(long digits) {
    this.fPrecision = digits;
  }

  @Override
  public double value(double value) {
    try {
      double result =
          fPrecision > 15 ? valueWithPrecision(value) : substituted(F.num(value)).evalfNaN();
      if (Double.isNaN(result)) {
        fFailureCount++;
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      fFailureCount++;
      return Double.NaN;
    }
  }

  /**
   * Evaluate one sample to {@link #fPrecision} digits.
   *
   * <p>
   * The position goes in as an exact rational rather than a high precision float: the expression
   * then stays exact until {@code N} evaluates the whole of it at once, which is what actually buys
   * the digits. Substituting a float instead makes the engine raise each subexpression to that
   * precision separately, and a power of one runs into its iteration limit.
   */
  private double valueWithPrecision(double value) {
    try {
      IExpr evaluated = fEngine.evaluate(F.N(substituted(F.fraction(value)), fPrecision));
      return evaluated.evalfNaN();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      fFailureCount++;
      return Double.NaN;
    }
  }

  /**
   * Number of sample points so far for which the function could not be evaluated to a real value
   * through {@link #value(double)} or {@link #applyAsDouble(double)}.
   */
  public int failureCount() {
    return fFailureCount;
  }

  /**
   * Evaluate the {@link S#Limit} of the {@code unaryFunction} for the {@code value} in the form
   * {@code engine.evalDouble( F.Limit(unaryFunction, F.Rule(variable, value)) )} and return the
   * double value or {@link Double#NaN}.
   * 
   * @param value the value of the limit for the given variable
   * @return the calculated double value or {@link Double#NaN}.
   */
  public double valueLimit(double value) {
    try {
      return fEngine.evalDouble(F.Limit(fUnaryFunction, F.Rule(fDummyVariable, F.num(value))));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
  }

  // @Override
  // public DerivativeStructure value(final DerivativeStructure x) {
  // // x.getPartialDerivative(1)==1.0 in the case:
  // // fFirstDerivative.value(x.getValue() * x.getPartialDerivative(1)
  // return x.getFactory().build(value(x.getValue()), fFirstDerivative.value(x.getValue()));
  // }

  @Override
  public <T extends Derivative<T>> T value(T x) throws MathIllegalArgumentException {
    return x.compose(value(x.getReal()), fFirstDerivative.value(x.getReal()));
  }

  /** First derivative of unary function */
  public UnivariateFunction derivative() {
    if (fFirstDerivative != null) {
      return fFirstDerivative;
    }
    // fUnaryFunction is expressed in fDummyVariable (fVariable was substituted away in the
    // constructor), so the derivative must be taken with respect to the dummy variable
    final IAST ast = F.D(fUnaryFunction, fDummyVariable);
    IExpr expr = fEngine.evaluate(ast);
    fFirstDerivative = new UnaryNumerical(expr, fDummyVariable, false, true, Double.NaN, fEngine);
    return fFirstDerivative;
  }

  public ComplexNum value(final ComplexNum z) {
    final Object temp = apply(z);
    if (temp instanceof ComplexNum) {
      return (ComplexNum) temp;
    }
    if (temp instanceof INum) {
      return ComplexNum.valueOf((INum) temp);
    }
    throw new ArithmeticException("Expected numerical complex value object!");
  }

  @Override
  public Complex value(final Complex z) {
    final Object temp = apply(F.complexNum(z));
    if (temp instanceof ComplexNum) {
      return ((ComplexNum) temp).complexValue();
    }
    if (temp instanceof INum) {
      return Complex.valueOf(((INum) temp).doubleValue());
    }
    throw new ArithmeticException("Expected numerical complex value object!");
  }

  public INum value(final INum z) {
    final Object temp = apply(z);
    if (temp instanceof INum) {
      return (INum) temp;
    }
    throw new ArithmeticException("Expected numerical double value object!");
  }

  @Override
  public double applyAsDouble(double value) {
    if (fPrecision > 15) {
      return valueWithPrecision(value);
    }
    IExpr function = substituted(F.num(value));
    if (function.isAST(S.Labeled, 3) || function.isAST(S.Style, 3)) {
      return function.first().evalfNaN();
    }
    try {
      double result = EvalEngine.get().evalDouble(function, null, fDefaultValue);
      if (Double.isNaN(result)) {
        fFailureCount++;
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      fFailureCount++;
      throw rex;
    }
  }

  public static double[] vectorValue(UnivariateFunction function, double[] t) {
    int n = t.length;
    double[] f = new double[n];
    for (int i = 0; i < n; ++i) {
      f[i] = function.value(t[i]);
    }
    return f;
  }
}
