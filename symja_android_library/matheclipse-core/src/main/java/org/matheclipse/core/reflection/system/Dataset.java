package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.FEConfig;

/** Import semantic data into a DataSet */
public class Dataset extends AbstractEvaluator {

  public Dataset() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    if (ast.isAST1() && ast.arg1().isList()) {
      if (((IAST) ast.arg1()).forAll(x -> x.isAssociation())) {
        // return DataSetExpr.newInstance((IAST) ast.arg1());
      }
    }
    if (ast.head().isDataSet()) {
      IASTDataset dataSet = (IASTDataset) ast.head();
      IExpr arg1 = ast.arg1();
      try {
        IExpr arg2 = F.All;
        if (ast.isAST2()) {
          arg2 = ast.arg2();
        }
        IExpr result = dataSet.select(arg1, arg2);
        if (result.isPresent()) {
          return result;
        }
        if (!arg1.equals(F.All)) {
          if (arg1.isBuiltInSymbol()
              || //
              arg1.isAST(F.TakeLargest, 2)
              || //
              arg1.isAST(F.TakeLargestBy, 3)) {
            IExpr expr = dataSet.select(F.All, arg2);
            if (expr.isDataSet()) {
              return F.unaryAST1(arg1, ((IASTDataset) expr).normal(false));
            }
          } else {
            IExpr expr = engine.evaluate(F.unaryAST1(arg1, dataSet));
            if (expr.isDataSet()) {
              return ((IASTDataset) expr).select(F.All, arg2);
              // } else if (expr.isList() && ((IAST) expr).forAll(x -> x.isAssociation())) {
              // return DataSetExpr.newInstance((IAST) ast.arg1());
            }
          }
        }
      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return engine.printMessage(ast.topHead(), rex);
      } finally {
      }
    }

    return F.NIL;
  }

  public int[] expectedArgSize(IAST ast) {
    return IOFunctions.ARGS_1_2;
  }
}
