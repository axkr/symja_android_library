package org.matheclipse.core.generic;

import org.hipparchus.analysis.CalculusFieldUnivariateFunction;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ComplexUnaryNumerical 
    implements CalculusFieldUnivariateFunction<Complex> {
  final IExpr fUnaryFunction;
  final ISymbol fVariable;
  final EvalEngine fEngine;

  public ComplexUnaryNumerical(final IExpr unaryFunction, final ISymbol variable,
      final EvalEngine engine
      ) {
    if (!variable.isVariable() || variable.isBuiltInSymbol()) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(variable), EvalEngine.get()));
    }
    fVariable = variable;
    fUnaryFunction = unaryFunction;
    fEngine = engine;
//    if (firstDerivative) {
//      IExpr temp = engine.evaluate(F.D(fUnaryFunction, fVariable));
//      fFirstDerivative = new UnaryNumerical(temp, fVariable, engine, false);
//    }
  }

  @Override
  public Complex value(Complex value) {
    try {
      return fEngine.evalComplex(F.subst(fUnaryFunction, F.Rule(fVariable, F.complexNum(value))));
    } catch (RuntimeException rex) {
      return Complex.NaN;
    }
  }
}
