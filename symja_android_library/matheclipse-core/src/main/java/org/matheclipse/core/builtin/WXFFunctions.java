package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IASTMutable;

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
      S.RawCompress.setEvaluator(new RawCompress());
      S.RawUncompress.setEvaluator(new RawUncompress());
    }
  }

  /**
   * <code>BinarySerialize[expr]</code> and
   * <code>BinarySerialize[expr, PerformanceGoal -&gt; "Size"]</code>, which writes compressed WXF.
   */
  private static class BinarySerialize extends AbstractCoreFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 1) {
        IASTMutable evaled = ast.copy();
        for (int i = 1; i < evaled.size(); i++) {
          evaled.set(i, engine.evaluate(ast.get(i)));
        }
        Boolean compressed = sizeGoal(evaled, 2, engine);
        if (compressed == null) {
          return F.NIL;
        }
        return serialize(evaled.arg1(), compressed);
      }
      return F.NIL;
    }
  }

  /**
   * The WXF bytes of <code>expr</code> as a <code>ByteArray</code>, compressed (header
   * <code>8C:</code>) if <code>compressed</code> is <code>true</code>.
   *
   * @return {@link F#NIL} if <code>expr</code> can't be serialized
   */
  public static @AllowNIL IExpr serialize(IExpr expr, boolean compressed) {
    byte[] bArray = compressed ? WL.serializeCompressed(expr) : WL.serialize(expr);
    return bArray == null ? F.NIL : ByteArrayExpr.newInstance(bArray);
  }

  /**
   * Whether the options of <code>ast</code> from position <code>from</code> on ask for compressed
   * WXF with <code>PerformanceGoal -&gt; "Size"</code>. An option is a rule or a list of rules, its
   * name the symbol or the string <code>PerformanceGoal</code>; the last one given wins.
   *
   * @return <code>true</code> for <code>"Size"</code>, <code>false</code> for <code>"Speed"</code>,
   *         <code>Automatic</code> or no option, <code>null</code> after a message if an argument
   *         is not an option, names another option or has another value
   */
  public static Boolean sizeGoal(IAST ast, int from, EvalEngine engine) {
    boolean compressed = false;
    for (int i = from; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      IAST rules = option.isList() ? (IAST) option : F.list(option);
      for (IExpr rule : rules) {
        if (!rule.isRuleAST()) {
          // Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule
          // or a list of rules.
          Errors.printMessage(ast.topHead(), "nonopt", F.list(option, F.ZZ(from - 1), ast),
              engine);
          return null;
        }
        IExpr name = rule.first();
        if (name != S.PerformanceGoal && !name.isString("PerformanceGoal")) {
          // Unknown option `1` in `2`.
          Errors.printMessage(ast.topHead(), "optx", F.list(rule, ast), engine);
          return null;
        }
        IExpr value = rule.second();
        if (value.isString("Size")) {
          compressed = true;
        } else if (value.isString("Speed") || value == S.Automatic) {
          compressed = false;
        } else {
          // Value of option PerformanceGoal -> `1` is not Automatic, "Speed" or "Size".
          Errors.printMessage(ast.topHead(), "perfgoal", F.list(value), engine);
          return null;
        }
      }
    }
    return compressed;
  }

  private static class ByteArray extends AbstractFunctionEvaluator {
    public static boolean isBase64(String s) {
      String pattern =
          "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
      Pattern r = Pattern.compile(pattern);
      Matcher m = r.matcher(s);

      return m.find();
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // try {
        IExpr arg1 = ast.arg1();
        if (arg1 instanceof ByteArrayExpr) {
          // bytes are already bytes: ByteArray of a byte array is that byte array, which is what
          // lets a caller write ByteArray[x] without knowing which of the two it was handed
          return arg1;
        }
        if (arg1.isList()) {
          if (arg1.isEmptyList()) {
            return ByteArrayExpr.newInstance(new byte[] {});
          }
          byte[] bArray = WL.toByteArray((IAST) arg1);
          if (bArray == null) {
            // The argument at position `1` in `2` should be a vector of unsigned byte values or a
            // Base64 encoded string.
            return Errors.printMessage(ast.topHead(), "lend", F.List(F.C1, ast), engine);
          }
          return ByteArrayExpr.newInstance(bArray);
        } else if (arg1.isString()) {
          String str = arg1.toString();
          if (str.isEmpty()) {
            return F.CEmptyList;
          }
          if (!isBase64(str)) {
            // The argument at position `1` in `2` should be a vector of unsigned byte values or a
            // Base64 encoded string.
            return Errors.printMessage(ast.topHead(), "lend", F.List(F.C1, ast), engine);
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
        return Errors.printMessage(ast.topHead(), "lend", F.List(F.C1, ast), engine);
      }
      return F.NIL;
    }
  }

  /**
   * The bytes of a list of byte values or of a <code>ByteArray</code>, or <code>null</code> when
   * <code>arg</code> is neither.
   */
  private static byte[] bytesOf(IExpr arg) {
    if (arg instanceof ByteArrayExpr) {
      return (byte[]) ((ByteArrayExpr) arg).toData();
    }
    if (arg.isList()) {
      return arg.isEmptyList() ? new byte[0] : WL.toByteArray((IAST) arg);
    }
    return null;
  }

  /** The bytes in the form <code>original</code> had: a <code>ByteArray</code> or a list. */
  private static IExpr sameFormAs(IExpr original, byte[] bytes) {
    if (original instanceof ByteArrayExpr) {
      return ByteArrayExpr.newInstance(bytes);
    }
    IASTAppendable list = F.ListAlloc(bytes.length);
    for (byte b : bytes) {
      list.append(F.ZZ(b & 0xFF));
    }
    return list;
  }

  /**
   * <code>Developer`RawCompress[bytes]</code> - the bytes compressed with zlib.
   *
   * <p>
   * The WLJS notebook sends every object larger than 2 KB - every plot - to the browser as
   * <code>BaseEncode[ByteArray[Developer`RawCompress[bytes]]]</code>, and the browser inflates it
   * as zlib. Without it each plot drew a <code>ByteArray::lend</code> warning.
   */
  private static class RawCompress extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      byte[] bytes = bytesOf(ast.arg1());
      if (bytes == null) {
        return F.NIL;
      }
      return sameFormAs(ast.arg1(), org.matheclipse.core.expression.WMACompress.deflate(bytes));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>Developer`RawUncompress[bytes]</code> - undoes <code>Developer`RawCompress</code>. */
  private static class RawUncompress extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      byte[] bytes = bytesOf(ast.arg1());
      if (bytes == null) {
        return F.NIL;
      }
      try {
        byte[] inflated = org.matheclipse.core.expression.WMACompress.inflate(bytes);
        return inflated == null ? F.NIL : sameFormAs(ast.arg1(), inflated);
      } catch (java.util.zip.DataFormatException dfe) {
        // not a zlib stream: nothing to undo
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BinaryDeserialize extends AbstractCoreFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
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
