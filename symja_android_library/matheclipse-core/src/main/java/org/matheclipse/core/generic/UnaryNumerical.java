package org.matheclipse.core.generic;

import java.util.Deque;
import java.util.function.Function;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.DoubleStackEvaluator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Unary numerical function for functions like Plot
 *
 * @see org.matheclipse.core.reflection.system.Plot
 */
public class UnaryNumerical implements Function<IExpr, IExpr>, UnivariateDifferentiableFunction {
  IExpr fFunction;
  UnaryNumerical fFirstDerivative = null;

  ISymbol fVariable;

  EvalEngine fEngine;

  public UnaryNumerical(final IExpr fn, final ISymbol v) {
    this(fn, v, EvalEngine.get(), false);
  }

  public UnaryNumerical(final IExpr fn, final ISymbol v, final EvalEngine engine) {
    this(fn, v, engine, false);
  }

  public UnaryNumerical(
      final IExpr fn, final ISymbol v, final EvalEngine engine, boolean firstDerivative) {
    if (!v.isVariable() || v.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          IOFunctions.getMessage("setraw", F.List(v), EvalEngine.get()));
    }
    fVariable = v;
    fFunction = fn;
    fEngine = engine;
    if (firstDerivative) {
      IExpr temp = engine.evaluate(F.D(fFunction, fVariable));
      fFirstDerivative = new UnaryNumerical(temp, fVariable, engine, false);
    }
  }

  @Override
  public IExpr apply(final IExpr firstArg) {
    return fEngine.evalN(F.subst(fFunction, F.Rule(fVariable, firstArg)));
  }

  @Override
  public double value(double x) {
    double result = 0.0;
    final double[] stack = new double[10];
    IExpr value = fVariable.assignedValue();
    try {
      fVariable.assignValue(Num.valueOf(x), false);
      result = DoubleStackEvaluator.eval(stack, 0, fFunction);
    } catch (RuntimeException rex) {
      return Double.NaN;
    } finally {
      fVariable.assignValue(value, false);
    }
    return result;
  }

  public DerivativeStructure value(final DerivativeStructure x) {
    // x.getPartialDerivative(1)==1.0 in the case:
    // fFirstDerivative.value(x.getValue() * x.getPartialDerivative(1)
    return x.getFactory().build(value(x.getValue()), fFirstDerivative.value(x.getValue()));
  }

  /** First derivative of unary function */
  public UnivariateFunction derivative() {
    if (fFirstDerivative != null) {
      return fFirstDerivative;
    }
    final IAST ast = F.D(fFunction, fVariable);
    IExpr expr = fEngine.evaluate(ast);
    fFirstDerivative = new UnaryNumerical(expr, fVariable, fEngine, false);
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

  public INum value(final INum z) {
    final Object temp = apply(z);
    if (temp instanceof INum) {
      return (INum) temp;
    }
    throw new ArithmeticException("Expected numerical double value object!");
  }
}
