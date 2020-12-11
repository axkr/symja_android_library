package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumericArray;

public class NumericArrayFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.NumericArray.setEvaluator(new NumericArray());
      S.NumericArrayQ.setEvaluator(new NumericArrayQ());
      S.NumericArrayType.setEvaluator(new NumericArrayType());
    }
  }

  private static class NumericArray extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isString()) {
        if (!arg1.isList()) {
          // for sparse arrays, byte arrays...
          arg1 = arg1.normal(false);
        }
        if (arg1.isList()) {
          int type = NumericArrayExpr.getType(arg2.toString());
          NumericArrayExpr result = NumericArrayExpr.newList((IAST) arg1, type);
          if (result != null) {
            return result;
          }
          // The argument `1` cannot be converted to a NumericArray of type `2` using method `3`
          return IOFunctions.printMessage(
              ast.topHead(), "nconvss", F.list(arg1, arg2, "Check"), engine);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static class NumericArrayQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isNumericArray()) {
        return S.True;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class NumericArrayType extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNumericArray()) {
        INumericArray numericArray = (INumericArray) arg1;
        return F.$str(numericArray.getType());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private NumericArrayFunctions() {}
}
