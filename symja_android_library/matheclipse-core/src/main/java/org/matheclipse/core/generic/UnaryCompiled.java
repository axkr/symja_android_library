package org.matheclipse.core.generic;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.differentiation.Derivative;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.builtin.CompilerFunctions;
import org.matheclipse.core.builtin.CompilerFunctions.CompiledFunctionArg;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Unary numerical function for functions like Plot */
public final class UnaryCompiled implements UnaryOperator<IExpr>, UnivariateDifferentiableFunction,
    DoubleFunction<IExpr>, DoubleUnaryOperator {
  /** only use for information */
  final IExpr fUnaryFunction;
  /**
   * Maybe <code>null</code>
   */
  final CompiledFunctionExpr fUnaryCompiled;
  final ISymbol fVariable;
  final EvalEngine fEngine;

  UnaryCompiled fFirstDerivative = null;

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
  public UnaryCompiled(final IExpr unaryFunction, final ISymbol variable) {
    this(unaryFunction, variable, EvalEngine.get(), false);
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
   * @param engine the evaluation engine
   */
  public UnaryCompiled(final IExpr unaryFunction, final ISymbol variable, final EvalEngine engine) {
    this(unaryFunction, variable, engine, false);
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
   * @param engine the evaluation engine
   * @param firstDerivative if <code>true</code> evaluate the first derivative of
   *        {@code unaryFunction} directly in the constructor.
   */
  public UnaryCompiled(final IExpr unaryFunction, final ISymbol variable, final EvalEngine engine,
      boolean firstDerivative) {
    if (!variable.isVariable() || variable.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(variable), EvalEngine.get()));
    }
    fVariable = variable;
    fUnaryFunction = unaryFunction;
    fEngine = engine;
    CompilerFunctions.CompiledFunctionArg[] cf = new CompilerFunctions.CompiledFunctionArg[1];
    cf[0] = new CompiledFunctionArg(fVariable, S.Real);
    fUnaryCompiled = CompilerFunctions.compile(F.Compile(F.NIL, fUnaryFunction), cf, engine);
    if (fUnaryCompiled != null) {
      if (firstDerivative) {
        IExpr temp = engine.evaluate(F.D(fUnaryFunction, fVariable));
        fFirstDerivative = new UnaryCompiled(temp, fVariable, engine, false);
      }
    }
  }

  @Override
  public IExpr apply(final IExpr value) {
    return fUnaryCompiled.evaluate(F.List(value), fEngine);
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
      return fEngine.evalNumericFunction(F.Limit(fUnaryFunction, F.Rule(fVariable, value)));
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
      return fEngine.evalNumericFunction(F.subst(fUnaryFunction, fVariable, F.num(value)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return S.Indeterminate;
    }
  }

  /**
   * Evaluate the {@code unaryFunction} for the {@code value} in the {@code fUnaryCompiled} and
   * return the double value or {@link Double#NaN}.
   * 
   * @param value the value of the limit for the given variable
   * @return the calculated double value or {@link Double#NaN}.
   */
  @Override
  public double value(double value) {
    try {
      IExpr evaluated = fUnaryCompiled.evaluate(F.unaryAST1(fUnaryCompiled, F.num(value)), fEngine);
      if (evaluated.isPresent()) {
        return evaluated.evalf();
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);

    }
    return Double.NaN;
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
      return fEngine.evalDouble(F.Limit(fUnaryFunction, F.Rule(fVariable, F.num(value))));
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
    fFirstDerivative = new UnaryCompiled(expr, fVariable, fEngine, false);
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
    return F.subst(fUnaryFunction, fVariable, F.num(value)).evalf();
  }
}
