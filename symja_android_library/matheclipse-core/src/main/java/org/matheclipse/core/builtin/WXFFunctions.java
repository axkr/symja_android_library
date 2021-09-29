package org.matheclipse.core.builtin;

import java.util.Base64;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;

public class WXFFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BinarySerialize.setEvaluator(new BinarySerialize());
      S.BinaryDeserialize.setEvaluator(new BinaryDeserialize());
      S.ByteArray.setEvaluator(new ByteArray());
    }
  }

  private static class BinarySerialize extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        byte[] bArray = WL.serialize(arg1);
        if (bArray != null) {
          return ByteArrayExpr.newInstance(bArray);
        }
      }
      return F.NIL;
    }
  }

  private static class ByteArray extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // try {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          if (arg1.isEmptyList()) {
            return F.CEmptyList;
          }
          byte[] bArray = WL.toByteArray((IAST) arg1);
          if (bArray == null) {
            // The argument at position `1` in `2` should be a vector of unsigned byte values or a
            // Base64 encoded string.
            String message = IOFunctions.getMessage("lend", F.List(F.C1, ast));
            throw new ArgumentTypeStopException(message);
          }
          return ByteArrayExpr.newInstance(bArray);
        } else if (arg1.isString()) {
          String str = arg1.toString();
          if (str.isEmpty()) {
            return F.CEmptyList;
          }
          try {
            byte[] bArray = Base64.getDecoder().decode(str);
            return ByteArrayExpr.newInstance(bArray);
          } catch (IllegalArgumentException iae) {
            //
          }
        }
        // The argument at position `1` in `2` should be a vector of unsigned byte values or a
        // Base64 encoded string.
        String message = IOFunctions.getMessage("lend", F.List(F.C1, ast));
        throw new ArgumentTypeStopException(message);
      }
      return F.NIL;
    }
  }

  private static class BinaryDeserialize extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (isByteArray(arg1)) {
          byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
          if (bArray.length > 2) {
            IExpr temp = WL.deserialize(bArray);
            return temp;
          }
        }
      }
      return F.NIL;
    }
  }

  public static boolean isByteArray(IExpr arg1) {
    return arg1 instanceof ByteArrayExpr;
  }

  public static void initialize() {
    Initializer.init();
  }

  private WXFFunctions() {}
}
