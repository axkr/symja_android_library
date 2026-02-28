package org.matheclipse.core.form.output;

import java.util.Map;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.collect.ImmutableMap;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public class JavaComplexFormFactory extends ComplexFormFactory {
  private static final Map<ISymbol, String> FUNCTIONS_STR;

  static {
    ImmutableMap.Builder<ISymbol, String> builder = ImmutableMap.builder();
    builder.put(S.ArithmeticGeometricMean, "CMath.agm");
    builder.put(S.AiryAi, "CMath.airyAi");
    builder.put(S.AiryAiPrime, "CMath.airyAiPrime");
    builder.put(S.AiryBi, "CMath.airyBi");
    builder.put(S.AiryBiPrime, "CMath.airyBiPrime");
    builder.put(S.AngerJ, "CMath.angerJ");
    builder.put(S.BarnesG, "CMath.barnesG");
    builder.put(S.BesselI, "CMath.besselI");
    builder.put(S.BesselJ, "CMath.besselJ");
    builder.put(S.BesselK, "CMath.besselK");
    builder.put(S.BesselY, "CMath.besselY");
    builder.put(S.Beta, "CMath.beta");
    builder.put(S.ChebyshevT, "CMath.chebyshevT");
    builder.put(S.ChebyshevU, "CMath.chebyshevU");
    builder.put(S.CoshIntegral, "CMath.coshIntegral");
    builder.put(S.CosIntegral, "CMath.cosIntegral");
    builder.put(S.EllipticE, "CMath.ellipticE");
    builder.put(S.EllipticK, "CMath.ellipticK");
    builder.put(S.Erf, "CMath.erf");
    builder.put(S.Erfc, "CMath.erfc");
    builder.put(S.Erfi, "CMath.erfi");
    builder.put(S.ExpIntegralE, "CMath.expIntegralE");
    builder.put(S.ExpIntegralEi, "CMath.expIntegralEi");
    builder.put(S.Fibonacci, "CMath.fibonacci");
    builder.put(S.FresnelC, "CMath.fresnelC");
    builder.put(S.FresnelS, "CMath.fresnelS");
    builder.put(S.Gamma, "CMath.gamma");
    builder.put(S.GegenbauerC, "CMath.gegenbauerC");
    builder.put(S.HarmonicNumber, "CMath.harmonicNumber");
    builder.put(S.HermiteH, "CMath.hermiteH");
    builder.put(S.Hypergeometric0F1, "CMath.hypergeometric0F1");
    builder.put(S.Hypergeometric0F1Regularized, "CMath.hypergeometric0F1Regularized");
    builder.put(S.Hypergeometric1F1, "CMath.hypergeometric1F1");
    builder.put(S.Hypergeometric1F1Regularized, "CMath.hypergeometric1F1Regularized");
    builder.put(S.Hypergeometric2F1, "CMath.hypergeometric2F1");
    builder.put(S.Hypergeometric2F1Regularized, "CMath.hypergeometric2F1Regularized");
    builder.put(S.HypergeometricU, "CMath.hypergeometricU");
    builder.put(S.JacobiP, "CMath.jacobiP");
    builder.put(S.LaguerreL, "CMath.laguerreL");
    builder.put(S.LegendreP, "CMath.legendreP");
    builder.put(S.LegendreQ, "CMath.legendreQ");
    builder.put(S.LogBarnesG, "CMath.logBarnesG");
    builder.put(S.LogGamma, "CMath.logGamma");
    builder.put(S.LogIntegral, "CMath.logIntegral");
    builder.put(S.LogisticSigmoid, "CMath.logisticSigmoid");
    builder.put(S.Pochhammer, "CMath.pochhammer");
    builder.put(S.PolyGamma, "CMath.polyGamma");
    builder.put(S.PolyLog, "CMath.polyLog");
    builder.put(S.SinhIntegral, "CMath.sinhIntegral");
    builder.put(S.SinIntegral, "CMath.sinIntegral");
    builder.put(S.StruveH, "CMath.struveH");
    builder.put(S.StruveL, "CMath.struveL");

    // --- Added Arc- and Hyperbolic Functions via CMath ---
    builder.put(S.ArcCosh, "CMath.acosh");
    builder.put(S.ArcSinh, "CMath.asinh");
    builder.put(S.ArcTanh, "CMath.atanh");

    builder.put(S.Csc, "CMath.csc");
    builder.put(S.Sec, "CMath.sec");
    builder.put(S.Cot, "CMath.cot");
    builder.put(S.Csch, "CMath.csch");
    builder.put(S.Sech, "CMath.sech");
    builder.put(S.Coth, "CMath.coth");

    builder.put(S.ArcCsc, "CMath.acsc");
    builder.put(S.ArcSec, "CMath.asec");
    builder.put(S.ArcCot, "CMath.acot");
    builder.put(S.ArcCsch, "CMath.acsch");
    builder.put(S.ArcSech, "CMath.asech");
    builder.put(S.ArcCoth, "CMath.acoth");

    // --- Native Hipparchus Instance Methods ---
    builder.put(S.Abs, ".abs");
    builder.put(S.ArcCos, ".acos");
    builder.put(S.ArcSin, ".asin");
    builder.put(S.ArcTan, ".atan");
    builder.put(S.Ceiling, ".ceil");
    builder.put(S.Cos, ".cos");
    builder.put(S.Cosh, ".cosh");
    builder.put(S.Floor, ".floor");
    builder.put(S.Log, ".log");
    builder.put(S.Max, ".max");
    builder.put(S.Min, ".min");
    // Power is handled manually by coding logic below
    builder.put(S.Power, ".pow");
    builder.put(S.Sin, ".sin");
    builder.put(S.Sinh, ".sinh");
    builder.put(S.Tan, ".tan");
    builder.put(S.Tanh, ".tanh");
    FUNCTIONS_STR = builder.build();
  }

  private JavaComplexFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures) {
    this(relaxedSyntax, reversed, exponentFigures, significantFigures, false);
  }

  private JavaComplexFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures, boolean packagePrefix) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures, packagePrefix);
  }

  /**
   * Get an <code>JavaComplexFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax If <code>true</code> use paranthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @return
   */
  public static JavaComplexFormFactory get(final boolean relaxedSyntax) {
    return get(relaxedSyntax, false);
  }

  /**
   * Get an <code>JavaComplexFormFactory</code> for converting an internal expression to a user
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
  public static JavaComplexFormFactory get(final boolean relaxedSyntax,
      final boolean plusReversed) {
    return get(relaxedSyntax, plusReversed, -1, -1, false);
  }

  /**
   * Get an <code>JavaComplexFormFactory</code> for converting an internal expression to a user
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
   * @param packagePrefix
   * @return
   */
  public static JavaComplexFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
      int exponentFigures, int significantFigures, boolean packagePrefix) {
    return new JavaComplexFormFactory(relaxedSyntax, plusReversed, exponentFigures,
        significantFigures, packagePrefix);
  }

  @Override
  public String functionHead(ISymbol symbol) {
    return FUNCTIONS_STR.get(symbol);
  }

  /**
   * Get an <code>JavaComplexFormFactory</code> for converting an internal expression to a user
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
        Errors.rethrowsInterruptException(rex);
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol() && function.size() > 1) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        if (str.startsWith(".")) {
          // Native Hipparchus Complex Instance Methods (e.g., .abs(), .sin())
          if (function.isPower()) {
            IExpr base = function.base();
            IExpr exponent = function.exponent();
            if (exponent.isNumEqualRational(F.C1D2)) {
              buf.append("(");
              convertInternal(buf, base);
              buf.append(").sqrt()");
              return;
            } else if (exponent.isNumEqualRational(F.C1D3)) {
              buf.append("(");
              convertInternal(buf, base);
              buf.append(").cbrt()");
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
          } else if (function.isDirectedInfinity()) {
            if (function.isInfinity()) {
              buf.append("Complex.INF");
              return;
            } else if (function.isNegativeInfinity()) {
              buf.append("(-Complex.INF)");
              return;
            }
            buf.append("(");
            convertInternal(buf, function.first());
            buf.append(")" + str);
            if (function.isAST(S.ArcTan, 3)) {
              buf.append("2");
            }
            convertArgs(buf, head, function, 2);
            return;
          }

          buf.append("(");
          convertInternal(buf, function.first());
          buf.append(")" + str);
          if (function.isAST(S.ArcTan, 3)) {
            buf.append("2");
          }
          convertArgs(buf, head, function, 2);
          return;
        } else {
          // Static CMath Function Calls (e.g. CMath.gamma(x), CMath.acosh(x))
          buf.append(str);
          convertArgs(buf, head, function, 1);
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
          convertArgs(buf, head, function, 1);
          return;
        } else if (function.isDirectedInfinity()) {
          if (function.isInfinity()) {
            buf.append("Complex.INF");
            return;
          }
          if (function.isNegativeInfinity()) {
            buf.append("(-Complex.INF)");
            return;
          }
        }
        buf.append("F.");
        buf.append(head.toString());
        buf.append(".ofN");
        convertArgs(buf, head, function, 1);
        return;
      }
      convertInternal(buf, head);
      convertArgs(buf, head, function, 1);
    }
  }
}
