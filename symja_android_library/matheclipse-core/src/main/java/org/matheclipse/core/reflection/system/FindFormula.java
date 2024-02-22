package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FindFormula extends AbstractFunctionOptionEvaluator {

  public FindFormula() {
    // empty constructor
  }


  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IExpr data = ast.arg1();
    IExpr x = ast.arg2();
    if (x.isVariable()) {
      int n = Integer.MAX_VALUE;
      IExpr arg4 = S.All;

      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == 2 && data.isList()) {
        IAST matrix = (IAST) data;
        double[][] doubleMatrix = matrix.toDoubleMatrix();
        if (doubleMatrix != null) {
          if (ast.argSize() > 2) {
            n = ast.arg3().toIntDefault();
            if (n < 0) {
              // Positive machine-sized integer expected at position `2` in `1`.
              return Errors.printMessage(S.FindFormula, "intpm", F.List(F.C3, ast), engine);
            }
            if (ast.argSize() > 3) {
              arg4 = ast.arg4();
            }
          }
          // TODO add implementation here


        }
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    // setOptions(newSymbol, S.GenerateConditions, S.False);
  }
}
