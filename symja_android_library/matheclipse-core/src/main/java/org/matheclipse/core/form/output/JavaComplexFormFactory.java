package org.matheclipse.core.form.output;

import java.util.HashMap;
import java.util.Map;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public class JavaComplexFormFactory extends ComplexFormFactory {
  private static final Map<ISymbol, String> FUNCTIONS_STR = new HashMap<ISymbol, String>();

  static {
    FUNCTIONS_STR.put(S.Abs, ".abs");

    FUNCTIONS_STR.put(S.ArcCos, ".acos");
    FUNCTIONS_STR.put(S.ArcSin, ".asin");
    FUNCTIONS_STR.put(S.ArcTan, ".atan");

    FUNCTIONS_STR.put(S.Ceiling, ".ceil");
    FUNCTIONS_STR.put(S.Cos, ".cos");
    FUNCTIONS_STR.put(S.Cosh, ".cosh");
    FUNCTIONS_STR.put(S.Floor, ".floor");

    FUNCTIONS_STR.put(S.Log, ".log");
    FUNCTIONS_STR.put(S.Max, ".max");
    FUNCTIONS_STR.put(S.Min, ".min");
    // Power is handled by coding
    FUNCTIONS_STR.put(S.Power, ".pow");

    FUNCTIONS_STR.put(S.Sin, ".sin");
    FUNCTIONS_STR.put(S.Sinh, ".sinh");
    FUNCTIONS_STR.put(S.Tan, ".tan");
    FUNCTIONS_STR.put(S.Tanh, ".tanh");
  }

  private JavaComplexFormFactory(
      final boolean relaxedSyntax,
      final boolean reversed,
      int exponentFigures,
      int significantFigures) {
    this(relaxedSyntax, reversed, exponentFigures, significantFigures, false);
  }

  private JavaComplexFormFactory(
      final boolean relaxedSyntax,
      final boolean reversed,
      int exponentFigures,
      int significantFigures,
      boolean packagePrefix) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures, packagePrefix);
  }

  /**
   * Get an <code>JavaDoubleFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax If <code>true</code> use paranthesis instead of square brackets and ignore
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @return
   */
  public static JavaComplexFormFactory get(final boolean relaxedSyntax) {
    return get(relaxedSyntax, false);
  }

  /**
   * Get an <code>JavaDoubleFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax if <code>true</code> use paranthesis instead of square brackets and ignore
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *     be printed in reversed order
   * @return
   */
  public static JavaComplexFormFactory get(
      final boolean relaxedSyntax, final boolean plusReversed) {
    return get(relaxedSyntax, plusReversed, -1, -1, false);
  }

  /**
   * Get an <code>JavaDoubleFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax if <code>true</code> use paranthesis instead of square brackets and ignore
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *     be printed in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @param packagePrefix
   * @return
   */
  public static JavaComplexFormFactory get(
      final boolean relaxedSyntax,
      final boolean plusReversed,
      int exponentFigures,
      int significantFigures,
      boolean packagePrefix) {
    return new JavaComplexFormFactory(
        relaxedSyntax, plusReversed, exponentFigures, significantFigures, packagePrefix);
  }

  @Override
  public String functionHead(ISymbol symbol) {
    return FUNCTIONS_STR.get(symbol);
  }

  /**
   * Get an <code>JavaDoubleFormFactory</code> for converting an internal expression to a user
   * readable string, with <code>relaxedSyntax</code> set to false.
   *
   * @return
   * @see #get(boolean)
   */
  public static JavaComplexFormFactory get() {
    return get(false);
  }

  @Override
  public void convertAST(final StringBuilder buf, final IAST function) {
    if (function.isNumericFunction(true)) {
      try {
        Complex value = EvalEngine.get().evalComplex(function);
        if (value != null) {
          buf.append("Complex.valueOf(");
          buf.append(value.getReal());
          buf.append(",");
          buf.append(value.getImaginary());
          buf.append(")");
          return;
        }
      } catch (RuntimeException rex) {
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol() && function.size() > 1) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        if (function.isPower()) {
          IExpr base = function.base();
          IExpr exponent = function.exponent();
          if (exponent.isNumEqualRational(F.C1D2)) {
            buf.append("(");
            convertInternal(buf, base);
            buf.append(").sqrt()");
            return;
          } else if (exponent.isNumEqualRational(F.CN1D2)) {
            buf.append("(");
            convertInternal(buf, base);
            buf.append(").reciprocal().sqrt()");
            return;
          } else if (exponent.isMinusOne()) {
            buf.append("(");
            convertInternal(buf, base);
            buf.append(").reciprocal()");
            return;
          }
        }
        //  Complex atan2(Complex x) {
        buf.append("(");
        convertInternal(buf, function.first());
        buf.append(")" + str);
        if (function.isAST(S.ArcTan, 3)) {
          buf.append("2");
        }
        convertArgs(buf, head, function, 2);
        return;
      }
    }
    if (function.headID() > 0) {
      if (function.isAST(S.Defer, 2)
          || function.isAST(S.Evaluate, 2)
          || function.isAST(S.Hold, 2)
          || function.isUnevaluated()) {
        convertInternal(buf, function.first());
        return;
      }
      if (function.isPower()) {
        IExpr base = function.base();
        IExpr exponent = function.exponent();
        if (exponent.isMinusOne()) {
          buf.append("1.0/(");
          convertInternal(buf, base);
          buf.append(")");
          return;
        }
        if (exponent.isNumEqualRational(F.C1D2)) {
          buf.append("Math.sqrt(");
          convertInternal(buf, base);
          buf.append(")");
          return;
        }
        if (exponent.isNumEqualRational(F.C1D3)) {
          buf.append("Math.cbrt(");
          convertInternal(buf, base);
          buf.append(")");
          return;
        }
        buf.append("Math.pow");
        convertArgs(buf, head, function, 1);
        return;
      }
      buf.append("F.");
      buf.append(head.toString());
      buf.append(".ofN(");
      convertArgs(buf, head, function, 1);
      buf.append(")");
      return;
    }
    if (function.isInfinity()) {
      buf.append("Double.POSITIVE_INFINITY");
      return;
    }
    if (function.isNegativeInfinity()) {
      buf.append("Double.NEGATIVE_INFINITY");
      return;
    }
    convertInternal(buf, head);
    convertArgs(buf, head, function, 1);
  }
}
