package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.BezierFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.itp.LinearBinaryAverage;

public class BezierFunction extends AbstractEvaluator {
  public BezierFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();
    if (head instanceof BezierFunctionExpr) {
      try {
        return ((BezierFunctionExpr) head).evaluate(ast, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.BezierFunction, rex, engine);
      }
    } else if (head == S.BezierFunction && ast.isAST1()) {
      final IExpr arg1 = ast.arg1();
      final int[] dim = arg1.isMatrix(false);
      if (dim != null && dim[0] > 1 && dim[1] > 1 && arg1.isListOfLists()) {
        IAST list = (IAST) arg1;
        double[][] matrix = list.toDoubleMatrix(false);
        if (matrix != null) {
          return BezierFunctionExpr.newInstance(LinearBinaryAverage.INSTANCE, list, matrix);
        }
      }
      // `1` should be a rectangular array of machine-sized real numbers of any depth, whose
      // dimensions are greater than 1.
      return Errors.printMessage(S.BezierFunction, "invcpts", F.List(arg1), engine);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY_0;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
