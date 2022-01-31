package org.matheclipse.core.generic;

import java.util.function.BiFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.DoubleStackEvaluator;
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
public class BinaryNumerical implements BiFunction<IExpr, IExpr, IExpr> {
  final IExpr fun;

  final ISymbol variable1;

  final ISymbol variable2;

  final EvalEngine fEngine;

  public BinaryNumerical(final IExpr fn, final ISymbol v1, final ISymbol v2,
      final EvalEngine engine) {
    if (!v1.isVariable() || v1.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          IOFunctions.getMessage("setraw", F.List(v1), EvalEngine.get()));
    }
    if (!v2.isVariable() || v2.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          IOFunctions.getMessage("setraw", F.List(v2), EvalEngine.get()));
    }
    variable1 = v1;
    variable2 = v2;
    fun = fn;
    fEngine = engine;
  }

  @Override
  public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
    return fEngine
        .evalN(F.subst(fun, F.List(F.Rule(variable1, firstArg), F.Rule(variable2, secondArg))));
  }

  public double value(final double x, final double y) {
    double result = 0.0;
    try {
      final INum xValue = F.num(x);
      final INum yValue = F.num(y);
      // substitution is more thread safe than direct value assigning to global variables
      IExpr temp = F.subst(fun, arg -> {
        if (arg.equals(variable1)) {
          return xValue;
        }
        return arg.equals(variable2) ? yValue : arg;
      });
      final double[] stack = new double[10];
      result = DoubleStackEvaluator.eval(stack, 0, temp);
    } catch (RuntimeException rex) {
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
