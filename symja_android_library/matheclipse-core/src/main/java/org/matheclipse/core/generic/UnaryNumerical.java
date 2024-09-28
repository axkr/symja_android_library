package org.matheclipse.core.generic;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
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
    DoubleFunction<IExpr>, DoubleUnaryOperator {
  final IExpr fUnaryFunction;
  final ISymbol fVariable;
  final ISymbol fDummyVariable;
  final EvalEngine fEngine;

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
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable) {
    this(unaryFunction, variable, false, true, EvalEngine.get());
  }

  /**
   * <p>
   * This class represents a unary function which computes both the value and the first derivative
   * of a mathematical function. The derivative is computed with respect to the input
   * {@code variable}. Functions which contains the {@link S#Abs} function will be converted to
   * {@link S#AbsReal} function, because of assuming real input values
   * </p>
   * 
   * @param unaryFunction the unary function
   * @param variable the functions variable name
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable,
      final EvalEngine engine) {
    this(unaryFunction, variable, false, true, engine);
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
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, boolean useAbsReal,
      final EvalEngine engine) {
    this(unaryFunction, variable, false, useAbsReal, engine);
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
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   * @param engine the evaluation engine
   */
  public UnaryNumerical(final IExpr unaryFunction, final ISymbol variable, boolean firstDerivative,
      boolean useAbsReal, final EvalEngine engine) {
    if (!variable.isVariable() || variable.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(variable), EvalEngine.get()));
    }
    fVariable = variable;
    IExpr function;
    if (useAbsReal) {
      function = F.substAbs(unaryFunction);
    } else {
      function = unaryFunction;
    }
    fEngine = engine;
    if (firstDerivative) {
      IExpr temp = engine.evaluate(F.D(function, fVariable));
      fFirstDerivative = new UnaryNumerical(temp, fVariable, false, useAbsReal, engine);
    }
    fDummyVariable = F.Dummy("$" + fVariable.toString());
    fUnaryFunction = F.subst(unaryFunction, x -> x.equals(variable) ? fDummyVariable : F.NIL);
  }

  @Override
  public IExpr apply(final IExpr value) {
    fDummyVariable.assignValue(value);
    return fEngine.evalNumericFunction(fUnaryFunction);
  }

  /**
   * Evaluate the {@link S#Limit} of the {@code unaryFunction} for the {@code value} in the form
   * {@code F.N( F.Limit(unaryFunction, F.Rule(variable, value)) )} and return the numerical result
   * or {@link S#Indeterminate}
   * 
   * @param value
   */
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
      fDummyVariable.assignValue(F.num(value));
      return fEngine.evalNumericFunction(fUnaryFunction);
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
  @Override
  public double value(double value) {
    try {
      fDummyVariable.assignValue(F.num(value));
      return fUnaryFunction.evalf();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
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
    final IAST ast = F.D(fUnaryFunction, fVariable);
    IExpr expr = fEngine.evaluate(ast);
    fFirstDerivative = new UnaryNumerical(expr, fVariable, false, true, fEngine);
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
    fDummyVariable.assignValue(F.num(value));
    return fUnaryFunction.evalf();
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
