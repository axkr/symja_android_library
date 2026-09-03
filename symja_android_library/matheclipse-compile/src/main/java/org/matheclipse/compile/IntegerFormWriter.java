package org.matheclipse.compile;

import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Writes the Java <code>long</code> source of an expression the analyzer has proven is exactly
 * integer-valued, so a compiled function can compute with it directly instead of going through
 * <code>double</code> arithmetic and hoping the result still looks like a whole number afterward.
 *
 * <p>
 * <code>write(expr)</code> returns <code>null</code> for anything it has no case for - an
 * unsupported builtin, an operand it cannot resolve to a <code>long</code>-typed field, an
 * exponent too large to unroll - and the caller ({@link CompileFactory#convertNumeric}) falls back
 * to the existing <code>double</code> emitter exactly as if this class did not exist. That
 * fallback is what keeps every expression this class does not cover working exactly as before.
 *
 * <p>
 * Every arithmetic operation is written as an <code>IntegerMath</code>-free call to
 * {@link Math#addExact}/{@link Math#multiplyExact}/{@link Math#subtractExact} (when
 * <code>"CatchMachineIntegerOverflow"</code> is on, the default) or the plain operator otherwise -
 * matching <code>"RuntimeOptions" -&gt; "Speed"</code>'s existing "wrap silently" contract for the
 * double-based integer result today. An overflow thrown this way is caught by
 * {@code CompilerFunctions.CompiledFunction.evaluate}'s existing <code>ArithmeticException</code>
 * handler and falls back to the uncompiled evaluation, which computes the exact
 * arbitrary-precision answer - the same "a slower right answer beats a silently wrong one"
 * contract {@link CompileFactory} already uses for a non-numeric argument.
 */
final class IntegerFormWriter {

  /**
   * The largest literal, non-negative integer exponent {@link #power} unrolls into a chain of
   * multiplications. Past this it declines, which is not a correctness limit - the double-based
   * <code>Math.pow</code> fallback still runs and usually still gives an exact answer for the
   * values that actually turn up - only a limit on how much source one <code>Power</code> writes.
   */
  private static final int MAX_UNROLLED_EXPONENT = 32;

  private final CompileFactory factory;
  private final boolean checked;

  /**
   * @param factory the enclosing code generator, whose {@code nodeTypes} and
   *        {@code numericVariables} this writer reads
   * @param checked whether arithmetic is written with overflow checked
   *        ({@code "CatchMachineIntegerOverflow" -> True}, the default) or left to wrap silently
   *        ({@code "RuntimeOptions" -> "Speed"})
   */
  IntegerFormWriter(CompileFactory factory, boolean checked) {
    this.factory = factory;
    this.checked = checked;
  }

  /** Whether arithmetic this writer emits checks for overflow. See the constructor. */
  boolean isChecked() {
    return checked;
  }

  /**
   * The Java <code>long</code> source of <code>expr</code>, or <code>null</code> if this writer
   * has no case for it.
   */
  String write(IExpr expr) {
    if (expr.isInteger()) {
      return literal((IInteger) expr);
    }
    if (factory.nodeTypes.get(expr) != CompileAnalyzer.VarType.INTEGER) {
      // literals are exact regardless of whether the analyzer walked this exact node (a
      // synthetic default bound such as Do's implicit `1` never is); everything else has to be
      // proven integer first, or this would happily compute a fractional value as if it were
      // exact
      return null;
    }
    if (expr.isSymbol()) {
      return symbol(expr);
    }
    if (!expr.isAST()) {
      return null;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return null;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Plus:
        return nAry(ast, "addExact", "+");
      case ID.Times:
        return nAry(ast, "multiplyExact", "*");
      case ID.Subtract:
        return binary(ast, "subtractExact", "-");
      case ID.Power:
        return power(ast);
      case ID.Mod:
        return builtinBinary(ast, "Math.floorMod");
      case ID.Quotient:
        return builtinBinary(ast, "Math.floorDiv");
      default:
        return null;
    }
  }

  /** A literal integer, or <code>null</code> if it does not fit in a <code>long</code>. */
  private String literal(IInteger value) {
    if (value.bitLength() > 63) {
      return null;
    }
    return value.longValue() + "L";
  }

  /**
   * A symbol already proven integer: the field <code>convertScope</code>/<code>convertSet</code>/
   * <code>convertDo</code> gave it is a <code>long</code> (suffix <code>_l</code>) if it is a
   * local or loop variable, or an <code>int</code> (suffix <code>_i</code>) if it is a scalar
   * <code>_Integer</code> argument - widened here so the surrounding arithmetic always computes
   * in the full <code>long</code> range rather than silently wrapping at the much smaller
   * <code>int</code> one Java would otherwise pick for two <code>int</code> operands.
   */
  private String symbol(IExpr expr) {
    String field = factory.numericVariables.apply(expr);
    if (field == null || !field.startsWith("this.")) {
      return null;
    }
    // a vector or matrix argument's field is an array, not a number - the VarType lattice has no
    // notion of rank, so the analyzer types it INTEGER right alongside a scalar one of the same
    // declared element type, and this has to be checked separately (mirrors the same guard
    // CompileFactory#prepareForNumeric already has for the double emitter)
    for (CompiledFunctionArg arg : factory.args) {
      if (arg.argument().equals(expr) && arg.rank() != CompiledFunctionArg.Rank.SCALAR) {
        return null;
      }
    }
    if (field.endsWith("_l")) {
      return field;
    }
    if (field.endsWith("_i")) {
      return "((long) " + field + ")";
    }
    return null;
  }

  /** An n-ary <code>Plus</code>/<code>Times</code>, left-folded into nested exact calls. */
  private String nAry(IAST ast, String exactMethod, String operator) {
    String acc = write(ast.arg1());
    if (acc == null) {
      return null;
    }
    for (int i = 2; i <= ast.argSize(); i++) {
      String next = write(ast.get(i));
      if (next == null) {
        return null;
      }
      acc = checked ? "Math." + exactMethod + "(" + acc + ", " + next + ")"
          : "(" + acc + " " + operator + " " + next + ")";
    }
    return acc;
  }

  /** A binary <code>Subtract</code>. */
  private String binary(IAST ast, String exactMethod, String operator) {
    if (ast.argSize() != 2) {
      return null;
    }
    String left = write(ast.arg1());
    if (left == null) {
      return null;
    }
    String right = write(ast.arg2());
    if (right == null) {
      return null;
    }
    return checked ? "Math." + exactMethod + "(" + left + ", " + right + ")"
        : "(" + left + " " + operator + " " + right + ")";
  }

  /**
   * A binary call to a two-argument static <code>Math</code> method that cannot overflow
   * (<code>floorMod</code>/<code>floorDiv</code>): division by zero still throws
   * <code>ArithmeticException</code>, which is exactly the failure the caller already falls back
   * on, so there is nothing for "checked" vs. "unchecked" to change here.
   */
  private String builtinBinary(IAST ast, String method) {
    if (ast.argSize() != 2) {
      return null;
    }
    String left = write(ast.arg1());
    if (left == null) {
      return null;
    }
    String right = write(ast.arg2());
    if (right == null) {
      return null;
    }
    return method + "(" + left + ", " + right + ")";
  }

  /**
   * <code>base^k</code> for a literal, non-negative, not-too-large integer <code>k</code>,
   * unrolled at code-generation time into a chain of exact multiplications - Java has no integer
   * exponentiation operator, and a runtime loop would need a temporary this writer, which only
   * ever produces one Java expression, has nowhere to put. Anything else (a negative or
   * non-literal exponent, one larger than {@link #MAX_UNROLLED_EXPONENT}) declines, which is
   * correct: a negative integer power of an integer is a fraction, not a value this writer's
   * <code>long</code> result could hold anyway.
   */
  private String power(IAST ast) {
    if (ast.argSize() != 2 || !ast.arg2().isInteger()) {
      return null;
    }
    IInteger exponent = (IInteger) ast.arg2();
    if (exponent.isNegative() || exponent.bitLength() > 31) {
      return null;
    }
    int k = exponent.toIntDefault(-1);
    if (k < 0 || k > MAX_UNROLLED_EXPONENT) {
      return null;
    }
    if (k == 0) {
      return "1L";
    }
    String base = write(ast.arg1());
    if (base == null) {
      return null;
    }
    String acc = base;
    for (int i = 1; i < k; i++) {
      acc = checked ? "Math.multiplyExact(" + acc + ", " + base + ")" : "(" + acc + " * " + base + ")";
    }
    return acc;
  }
}
