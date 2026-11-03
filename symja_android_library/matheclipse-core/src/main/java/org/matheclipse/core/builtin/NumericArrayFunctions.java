package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
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

    /** The bits of an integer type like <code>"UnsignedInteger8"</code>. */
    private static int integerBits(String type) {
      return Integer.parseInt(type.substring(type.indexOf("Integer") + "Integer".length()));
    }

    /**
     * An element or a nested list clipped to the range of the type.
     *
     * @param type a real or an integer type
     * @param round integer types: round to the nearest integer instead of cutting off the fraction
     * @return {@link F#NIL} if there is an element which is no real number
     */
    private static IExpr clip(IExpr element, String type, boolean round, EvalEngine engine) {
      if (element.isList()) {
        IAST list = (IAST) element;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (IExpr arg : list) {
          IExpr clipped = clip(arg, type, round, engine);
          if (clipped.isNIL()) {
            return F.NIL;
          }
          result.append(clipped);
        }
        return result;
      }
      if (!element.isReal()) {
        return F.NIL;
      }
      if (type.startsWith("Real")) {
        double max = type.equals("Real32") ? Float.MAX_VALUE : Double.MAX_VALUE;
        return F.num(Math.max(-max, Math.min(max, element.evalf())));
      }
      boolean unsigned = type.startsWith("Unsigned");
      int bits = integerBits(type);
      IInteger min = unsigned ? F.C0 : F.C2.powerRational(bits - 1).negate();
      IInteger max = F.C2.powerRational(unsigned ? bits : bits - 1).subtract(F.C1);
      IExpr number = engine.evaluate(round ? F.Round(element) : F.IntegerPart(element));
      if (!number.isInteger()) {
        return F.NIL;
      }
      IInteger integer = (IInteger) number;
      return integer.isLT(min) ? min : (integer.isGT(max) ? max : integer);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (ast.isAST3()) {
        boolean clipAndRound = ast.arg3().isString("ClipAndRound");
        if (!arg2.isString() || !(clipAndRound || ast.arg3().isString("ClipAndCoerce"))
            || !arg1.isList() || !(arg2.toString().startsWith("Real")
                || arg2.toString().contains("Integer"))) {
          return ast.arg3().isString("Check") ? ast.removeFromEnd(3) : F.NIL;
        }
        IExpr clipped = clip(arg1, arg2.toString(), clipAndRound, engine);
        return clipped.isNIL() ? F.NIL : F.binaryAST2(S.NumericArray, clipped, arg2);
      }
      if (arg2.isString()) {
        byte type = NumericArrayExpr.toType(arg2.toString());
        if (arg1 instanceof ByteArrayExpr) {
          ByteArrayExpr barr = (ByteArrayExpr) arg1;
          byte[] unsignedByteArr = barr.toData();
          if (unsignedByteArr.length == 0) {
            return F.CEmptyList;
          }
          if (type == NumericArrayExpr.UnsignedInteger8) {
            int[] dimension = new int[] {unsignedByteArr.length};
            return new NumericArrayExpr(unsignedByteArr, dimension, type);
          }
        }

        if (!arg1.isList()) {
          // for sparse arrays, byte arrays...
          arg1 = arg1.normal(false);
        }
        if (arg1.isList()) {
          if (arg1.isEmptyList()) {
            return F.CEmptyList;
          }
          NumericArrayExpr result = NumericArrayExpr.newListByType((IAST) arg1, type, S.All);
          if (result != null) {
            return result;
          }
          // The argument `1` cannot be converted to a NumericArray of type `2` using method `3`
          return Errors.printMessage(ast.topHead(), "nconvss", F.list(arg1, arg2, S.Check), engine);
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
      return F.booleSymbol(ast.arg1().isNumericArray());
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
        return F.$str(numericArray.getStringType());
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
