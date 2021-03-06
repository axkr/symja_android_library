package org.matheclipse.io.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.io.expression.ASTDataset;
import org.matheclipse.parser.client.FEConfig;

public class DatasetFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (Config.FILESYSTEM_ENABLED) {
        S.Dataset.setEvaluator(new Dataset());
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  /** Import semantic data into a DataSet */
  private static class Dataset extends AbstractEvaluator {

    public Dataset() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.isAST1() && ast.arg1().isList()) {
        if (((IAST) ast.arg1()).forAll(x -> x.isAssociation())) {
          // return DataSetExpr.newInstance((IAST) ast.arg1());
        }
      }
      if (ast.head().isDataset()) {
        IASTDataset dataSet = (IASTDataset) ast.head();
        IExpr arg1 = ast.arg1();
        try {
          IExpr result;
          if (ast.isAST1()) {
            result = dataSet.select(arg1, S.All);
          } else if (ast.isAST2()) {
            result = dataSet.select(arg1, ast.arg2());
          } else {
            result = dataSet.select(ast);
          }
          if (result.isPresent()) {
            return result;
          }
          IExpr arg2 = S.All;
          if (ast.size() >= 3) {
            arg2 = ast.arg2();
          }

          if (!arg1.equals(S.All)) {
            if (arg1.isBuiltInSymbol()
                || //
                arg1.isAST(S.TakeLargest, 2)
                || //
                arg1.isAST(S.TakeLargestBy, 3)) {
              IExpr expr = dataSet.select(S.All, arg2);
              if (expr.isDataset()) {
                return F.unaryAST1(arg1, ((IASTDataset) expr).normal(false));
              }
            } else {
              IExpr expr = engine.evaluate(F.unaryAST1(arg1, dataSet));
              if (expr.isDataset()) {
                return ((IASTDataset) expr).select(S.All, arg2);
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
        return F.NIL;
      }
      if (ast.head() == S.Dataset) {
        if (ast.isAST1() && ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          if (list.forAll(x -> x.isAssociation())) {
            return ASTDataset.newListOfAssociations(list);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY_0;
    }
  }
}
