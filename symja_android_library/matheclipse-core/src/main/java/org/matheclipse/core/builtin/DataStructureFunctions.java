package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DataStructureExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Mutable containers: <code>CreateDataStructure</code> and the test which recognises one.
 *
 * <p>
 * The container itself - and every method call on it - lives in {@link DataStructureExpr}; these
 * two built-ins only make one and ask what it is.
 */
public class DataStructureFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.CreateDataStructure.setEvaluator(new CreateDataStructure());
        S.DataStructureQ.setEvaluator(new DataStructureQ());
      }
    }
  }

  private static class CreateDataStructure extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String type = ast.arg1().toString();
      List<IExpr> elements = new ArrayList<IExpr>();
      if (ast.isAST2()) {
        if (!ast.arg2().isList()) {
          return F.NIL;
        }
        IAST list = (IAST) ast.arg2();
        for (int i = 1; i < list.size(); i++) {
          elements.add(list.get(i));
        }
      }
      DataStructureExpr result = DataStructureExpr.newInstance(type, elements);
      if (result == null) {
        // `1` is not a known type of data structure.
        return Errors.printMessage(S.CreateDataStructure, "dstype", F.List(ast.arg1()), engine);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class DataStructureQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof DataStructureExpr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private DataStructureFunctions() {}
}
