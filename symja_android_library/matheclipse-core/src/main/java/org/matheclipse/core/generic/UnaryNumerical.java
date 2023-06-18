package org.matheclipse.core.generic;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.differentiation.Derivative;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Unary numerical function for functions like Plot */
public class UnaryNumerical
    implements UnaryOperator<IExpr>, UnivariateDifferentiableFunction, DoubleFunction<IExpr>,
    DoubleUnaryOperator {
  final IExpr fFunction;
  final ISymbol fVariable;
  final EvalEngine fEngine;

  UnaryNumerical fFirstDerivative = null;

  public UnaryNumerical(final IExpr function, final ISymbol variable) {
    this(function, variable, EvalEngine.get(), false);
  }

  public UnaryNumerical(final IExpr function, final ISymbol variable, final EvalEngine engine) {
    this(function, variable, engine, false);
  }

  public UnaryNumerical(final IExpr function, final ISymbol variable, final EvalEngine engine,
      boolean firstDerivative) {
    if (!variable.isVariable() || variable.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          IOFunctions.getMessage("setraw", F.list(variable), EvalEngine.get()));
    }
    fVariable = variable;
    fFunction = function;
    fEngine = engine;
    if (firstDerivative) {
      IExpr temp = engine.evaluate(F.D(fFunction, fVariable));
      fFirstDerivative = new UnaryNumerical(temp, fVariable, engine, false);
    }
  }

  @Override
  public IExpr apply(final IExpr value) {
    return fEngine.evalN(F.subst(fFunction, F.Rule(fVariable, value)));
  }

  @Override
  public IExpr apply(double value) {
    return fEngine.evalN(F.subst(fFunction, F.Rule(fVariable, F.num(value))));
  }

  @Override
  public double value(double d) {
    try {
      // substitution is more thread safe than direct value assigning to global variable
      return fFunction.evalf(x -> x.equals(fVariable) ? Num.valueOf(d) : F.NIL);
    } catch (RuntimeException rex) {
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
    return F.subst(fFunction, F.Rule(fVariable, F.num(value))).evalf();
  }
}
