package org.matheclipse.core.form.output;

import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.collect.ImmutableMap;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public class JavaDoubleFormFactory extends DoubleFormFactory {
  private static final Map<ISymbol, String> FUNCTIONS_STR;

  static {
    ImmutableMap.Builder<ISymbol, String> builder = ImmutableMap.builder();
    builder.put(S.ArithmeticGeometricMean, "DMath.agm");
    builder.put(S.AiryAi, "DMath.airyAi");
    builder.put(S.AiryAiPrime, "DMath.airyAiPrime");
    builder.put(S.AiryBi, "DMath.airyBi");
    builder.put(S.AiryBiPrime, "DMath.airyBiPrime");
    builder.put(S.AngerJ, "DMath.angerJ");
    builder.put(S.BesselI, "DMath.besselI");
    builder.put(S.BesselJ, "DMath.besselJ");
    builder.put(S.BesselK, "DMath.besselK");
    builder.put(S.BesselY, "DMath.besselY");
    builder.put(S.Beta, "DMath.beta");
    builder.put(S.ChebyshevT, "DMath.chebyshevT");
    builder.put(S.ChebyshevU, "DMath.chebyshevU");
    builder.put(S.EllipticE, "DMath.ellipticE");
    builder.put(S.EllipticK, "DMath.ellipticK");
    builder.put(S.Erf, "DMath.erf");
    builder.put(S.Erfc, "DMath.erfc");
    builder.put(S.Erfi, "DMath.erfi");
    builder.put(S.Fibonacci, "DMath.fibonacci");
    builder.put(S.FresnelC, "DMath.fresnelC");
    builder.put(S.FresnelS, "DMath.fresnelS");
    builder.put(S.Gamma, "DMath.gamma");
    builder.put(S.GegenbauerC, "DMath.gegenbauerC");
    builder.put(S.HarmonicNumber, "DMath.harmonicNumber");
    builder.put(S.HermiteH, "DMath.hermiteH");
    builder.put(S.Hypergeometric0F1Regularized, "DMath.hypergeometric0F1Regularized");
    builder.put(S.Hypergeometric1F1Regularized, "DMath.hypergeometric1F1Regularized");
    builder.put(S.Hypergeometric2F1, "DMath.hypergeometric2F1");
    builder.put(S.Hypergeometric2F1Regularized, "DMath.hypergeometric2F1Regularized");
    builder.put(S.InverseErf, "DMath.inverseErf");
    builder.put(S.InverseErfc, "DMath.inverseErfc");
    builder.put(S.JacobiP, "DMath.jacobiP");
    builder.put(S.LaguerreL, "DMath.laguerreL");
    builder.put(S.LegendreP, "DMath.legendreP");
    builder.put(S.LegendreQ, "DMath.legendreQ");
    builder.put(S.LogGamma, "DMath.logGamma");
    builder.put(S.LogisticSigmoid, "DMath.logisticSigmoid");
    builder.put(S.Pochhammer, "DMath.pochhammer");
    builder.put(S.PolyGamma, "DMath.polyGamma");
    builder.put(S.PolyLog, "DMath.polyLog");

    // --- Added Arc- and Hyperbolic Functions via DMath ---
    builder.put(S.ArcCosh, "DMath.acosh");
    builder.put(S.ArcSinh, "DMath.asinh");
    builder.put(S.ArcTanh, "DMath.atanh");

    builder.put(S.Csc, "DMath.csc");
    builder.put(S.Sec, "DMath.sec");
    builder.put(S.Cot, "DMath.cot");
    builder.put(S.Csch, "DMath.csch");
    builder.put(S.Sech, "DMath.sech");
    builder.put(S.Coth, "DMath.coth");

    builder.put(S.ArcCsc, "DMath.acsc");
    builder.put(S.ArcSec, "DMath.asec");
    builder.put(S.ArcCot, "DMath.acot");
    builder.put(S.ArcCsch, "DMath.acsch");
    builder.put(S.ArcSech, "DMath.asech");
    builder.put(S.ArcCoth, "DMath.acoth");

    // --- Native java.lang.Math Methods ---
    builder.put(S.Abs, "Math.abs");
    builder.put(S.RealAbs, "Math.abs");
    builder.put(S.Sign, "Math.signum");
    builder.put(S.RealSign, "Math.signum");

    builder.put(S.ArcCos, "Math.acos");
    builder.put(S.ArcSin, "Math.asin");
    builder.put(S.ArcTan, "Math.atan");

    builder.put(S.Ceiling, "Math.ceil");
    builder.put(S.Cos, "Math.cos");
    builder.put(S.Cosh, "Math.cosh");
    builder.put(S.CubeRoot, "Math.cbrt");
    builder.put(S.Floor, "Math.floor");
    builder.put(S.Log, "Math.log");
    builder.put(S.Log10, "Math.log10");
    builder.put(S.Max, "Math.max");
    builder.put(S.Min, "Math.min");
    // Power is handled manually by coding logic below
    // builder.put(S.Power, "Math.pow");

    builder.put(S.Sin, "Math.sin");
    builder.put(S.Sinh, "Math.sinh");
    builder.put(S.Tan, "Math.tan");
    builder.put(S.Tanh, "Math.tanh");
    FUNCTIONS_STR = builder.build();
  }

  private JavaDoubleFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
  }

  /**
   * Get an <code>JavaDoubleFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax If <code>true</code> use paranthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
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
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *        be printed in reversed order
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
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *        be printed in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @return
   */
  public static JavaDoubleFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
      int exponentFigures, int significantFigures) {
    return new JavaDoubleFormFactory(relaxedSyntax, plusReversed, exponentFigures,
        significantFigures);
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
  public static JavaDoubleFormFactory get() {
    return get(false);
  }

  @Override
  public void convertAST(final StringBuilder buf, final IAST function, final int precedence,
      boolean eval) {
    if (function.isNumericFunction(true)) {
      try {
        INumber value = function.evalNumber();
        if (value != null) {
          convertNumber(buf, value, precedence, NO_PLUS_CALL);
          return;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol()) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        buf.append(str);
        if (function.isAST(S.ArcTan, 3)) {
          buf.append("2");
        }
        convertArgs(buf, head, function);
        return;
      }
    }
    if (function.headID() > 0) {
      if (function.isAST(S.Defer, 2) || function.isAST(S.Evaluate, 2) || function.isAST(S.Hold, 2)
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
        convertArgs(buf, head, function);
        return;
      } else if (function.isDirectedInfinity()) {
        if (function.isInfinity()) {
          buf.append("Double.POSITIVE_INFINITY");
          return;
        } else if (function.isNegativeInfinity()) {
          buf.append("Double.NEGATIVE_INFINITY");
          return;
        }
      }
      buf.append("F.");
      buf.append(head.toString());
      buf.append(".ofN");
      convertArgs(buf, head, function);
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