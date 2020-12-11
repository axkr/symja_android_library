package org.matheclipse.core.form.output;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public class JavaDoubleFormFactory extends DoubleFormFactory {
  private static final Map<ISymbol, String> FUNCTIONS_STR = new HashMap<ISymbol, String>();

  static {
    FUNCTIONS_STR.put(F.Abs, "Math.abs");

    FUNCTIONS_STR.put(F.ArcCos, "Math.acos");
    FUNCTIONS_STR.put(F.ArcSin, "Math.asin");
    FUNCTIONS_STR.put(F.ArcTan, "Math.atan");

    FUNCTIONS_STR.put(F.Ceiling, "Math.ceil");
    FUNCTIONS_STR.put(F.Cos, "Math.cos");
    FUNCTIONS_STR.put(F.Cosh, "Math.cosh");
    FUNCTIONS_STR.put(F.Floor, "Math.floor");

    FUNCTIONS_STR.put(F.Log, "Math.log");
    FUNCTIONS_STR.put(F.Max, "Math.max");
    FUNCTIONS_STR.put(F.Min, "Math.min");
    // Power is handled by coding
    // FUNCTIONS_STR.put(F.Power, "Math.pow");

    FUNCTIONS_STR.put(F.Sin, "Math.sin");
    FUNCTIONS_STR.put(F.Sinh, "Math.sinh");
    FUNCTIONS_STR.put(F.Tan, "Math.tan");
    FUNCTIONS_STR.put(F.Tanh, "Math.tanh");
  }

  private JavaDoubleFormFactory(
      final boolean relaxedSyntax,
      final boolean reversed,
      int exponentFigures,
      int significantFigures) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
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
  public static JavaDoubleFormFactory get(final boolean relaxedSyntax) {
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
  public static JavaDoubleFormFactory get(final boolean relaxedSyntax, final boolean plusReversed) {
    return get(relaxedSyntax, plusReversed, -1, -1);
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
   * @return
   */
  public static JavaDoubleFormFactory get(
      final boolean relaxedSyntax,
      final boolean plusReversed,
      int exponentFigures,
      int significantFigures) {
    return new JavaDoubleFormFactory(
        relaxedSyntax, plusReversed, exponentFigures, significantFigures);
  }

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
  public static JavaDoubleFormFactory get() {
    return get(false);
  }

  public void convertAST(final StringBuilder buf, final IAST function) {
    if (function.isNumericFunction()) {
      try {
        double value = EvalEngine.get().evalDouble(function);
        buf.append("(");
        buf.append(value);
        buf.append(")");
        return;
      } catch (RuntimeException rex) {
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol()) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        buf.append(str);
        if (function.isAST(F.ArcTan, 3)) {
          buf.append("2");
        }
        convertArgs(buf, head, function);
        return;
      }
    }
    if (function.headID() > 0) {
      if (function.isAST(S.Defer, 2)
          || //
          function.isAST(S.Evaluate, 2)
          || //
          function.isAST(S.Hold, 2)
          || //
          function.isAST(S.Unevaluated, 2)) {
        convertInternal(buf, function.first());
        return;
      }
      if (function.isPower()) {
        IExpr base = function.base();
        IExpr exponent = function.exponent();
        if (exponent.isMinusOne()) {
          buf.append("(1.0/");
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
        convertArgs(buf, head, function);
        return;
      }
      buf.append("F.");
      buf.append(head.toString());
      buf.append(".ofN(");
      convertArgs(buf, head, function);
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
    convertArgs(buf, head, function);
  }
}
