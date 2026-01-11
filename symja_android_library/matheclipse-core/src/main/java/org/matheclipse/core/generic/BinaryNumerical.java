package org.matheclipse.core.generic;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import org.hipparchus.analysis.BivariateFunction;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Binary numerical function for functions like Plot3D
 *
 * @see org.matheclipse.core.reflection.system.Plot3D
 */
public final class BinaryNumerical implements BinaryOperator<IExpr>, BivariateFunction {
  final IExpr fun;

  final ISymbol variable1;

  final ISymbol variable2;

  final EvalEngine fEngine;

  public BinaryNumerical(final IExpr fn, final ISymbol v1, final ISymbol v2,
      final EvalEngine engine) {
    if (!v1.isVariable() || v1.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(v1), EvalEngine.get()));
    }
    if (!v2.isVariable() || v2.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(v2), EvalEngine.get()));
    }
    variable1 = v1;
    variable2 = v2;
    fun = fn;
    fEngine = engine;
  }

  @Override
  public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
    return fEngine.evalNumericFunction(
        F.subst(fun, new ISymbol[] {variable1, variable2}, new IExpr[] {firstArg, secondArg}));
  }

  @Override
  public double value(final double x, final double y) {
    double result = 0.0;
    try {
      final INum xValue = F.num(x);
      final INum yValue = F.num(y);
      // substitution is more thread safe than direct value assigning to global variables
      final Function<IExpr, IExpr> function = arg -> {
        if (arg.equals(variable1)) {
          return xValue;
        }
        if (arg.equals(variable2)) {
          return yValue;
        }
        return F.NIL;
      };
      result = fun.evalf(function);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
    return result;
  }

  public ComplexNum value(final ComplexNum z1, final ComplexNum z2) {
    final Object temp = apply(z1, z2);
    if (temp instanceof ComplexNum) {
      return (ComplexNum) temp;
    }
    if (temp instanceof Num) {
      return ComplexNum.valueOf(((Num) temp).getRealPart());
    }
    throw new ArithmeticException("Numerical complex value expected");
  }
}
