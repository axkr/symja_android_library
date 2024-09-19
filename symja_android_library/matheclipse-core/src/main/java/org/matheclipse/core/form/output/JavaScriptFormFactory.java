package org.matheclipse.core.form.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.builtin.PiecewiseFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.Precedence;
import com.google.common.collect.ImmutableMap;

/**
 * Transpile an internal <code>IExpr</code> into a JavaScript string. It can especially generate
 * JavaScript output for usage with the JavaScript libraries:
 *
 * <ul>
 * <li><a href="https://github.com/paulmasson/math">github.com/paulmasson/math</a>
 * <li><a href="https://github.com/paulmasson/mathcell">github.com/paulmasson/mathcell</a>
 * </ul>
 */
public class JavaScriptFormFactory extends DoubleFormFactory {

  /** Generate pure JavaScript output */
  public static final int USE_PURE_JS = 1;

  /**
   * Generate JavaScript output for usage with the JavaScript libraries:
   *
   * <ul>
   * <li><a href="https://github.com/paulmasson/math">github.com/paulmasson/math</a>
   * <li><a href="https://github.com/paulmasson/mathcell">github.com/paulmasson/mathcell</a>
   * </ul>
   */
  public static final int USE_MATHCELL = 2;

  /**
   * If <code>true</code> the <code>Piecewise()</code> function was used in an expression, which
   * need to do inline operators with the JavaScript ternary operator. If <code>false</code> the
   * converter will use <code>if(...){...}</code> statements.
   */
  public boolean INLINE_PIECEWISE = false;

  private final int javascriptFlavor;

  private List<String> sliderNames;

  private List<String> variableNames;

  private static final Map<ISymbol, String> FUNCTIONS_STR_MATHCELL;
  private static final Map<ISymbol, String> FUNCTIONS_STR_PURE_JS;

  static {
    // see https://paulmasson.github.io/math/docs/functions.html
    ImmutableMap.Builder<ISymbol, String> mathcellBuilder = ImmutableMap.builder();
    mathcellBuilder.put(S.Abs, "abs");
    mathcellBuilder.put(S.Arg, "arg");
    mathcellBuilder.put(S.AiryAi, "airyAi");
    mathcellBuilder.put(S.AiryBi, "airyBi");

    mathcellBuilder.put(S.Beta, "beta");
    mathcellBuilder.put(S.BernoulliB, "bernoulli");
    mathcellBuilder.put(S.BesselJ, "besselJ");
    mathcellBuilder.put(S.BesselY, "besselY");
    mathcellBuilder.put(S.BesselI, "besselI");
    mathcellBuilder.put(S.BesselK, "besselK");
    mathcellBuilder.put(S.BesselJZero, "besselJZero ");
    mathcellBuilder.put(S.BesselYZero, "besselYZero ");
    mathcellBuilder.put(S.BetaRegularized, "betaRegularized");
    mathcellBuilder.put(S.Binomial, "binomial");

    mathcellBuilder.put(S.CarlsonRC, "carlsonRC");
    mathcellBuilder.put(S.CarlsonRD, "carlsonRD");
    mathcellBuilder.put(S.CarlsonRF, "carlsonRF");
    mathcellBuilder.put(S.CarlsonRG, "carlsonRG");
    mathcellBuilder.put(S.CarlsonRJ, "carlsonRJ");
    mathcellBuilder.put(S.Ceiling, "ceiling");
    mathcellBuilder.put(S.ChebyshevT, "chebyshevT");
    mathcellBuilder.put(S.ChebyshevU, "chebyshevU");
    mathcellBuilder.put(S.Chop, "chop");
    mathcellBuilder.put(S.CosIntegral, "cosIntegral");
    mathcellBuilder.put(S.CoshIntegral, "coshIntegral");

    mathcellBuilder.put(S.DirichletEta, "dirichletEta");

    mathcellBuilder.put(S.EllipticF, "ellipticF");
    mathcellBuilder.put(S.EllipticK, "ellipticK");
    mathcellBuilder.put(S.EllipticE, "ellipticE");
    mathcellBuilder.put(S.EllipticPi, "ellipticPi");
    mathcellBuilder.put(S.EllipticTheta, "jacobiTheta");
    mathcellBuilder.put(S.Erf, "erf");
    mathcellBuilder.put(S.Erfc, "erfc");
    mathcellBuilder.put(S.Erfi, "erfi");
    mathcellBuilder.put(S.Exp, "exp");
    mathcellBuilder.put(S.ExpIntegralEi, "expIntegralEi");
    mathcellBuilder.put(S.ExpIntegralE, "expIntegralE");

    mathcellBuilder.put(S.Factorial, "factorial");
    mathcellBuilder.put(S.Factorial2, "factorial2");
    mathcellBuilder.put(S.Subfactorial, "subfactorial");
    mathcellBuilder.put(S.Floor, "floor");
    mathcellBuilder.put(S.FractionalPart, "fractionalPart");
    mathcellBuilder.put(S.FresnelC, "fresnelC");
    mathcellBuilder.put(S.FresnelS, "fresnelS");

    mathcellBuilder.put(S.Gamma, "gamma");
    mathcellBuilder.put(S.GammaRegularized, "gammaRegularized");
    mathcellBuilder.put(S.Gudermannian, "gudermannian");

    mathcellBuilder.put(S.HankelH1, "hankel1");
    mathcellBuilder.put(S.HankelH2, "hankel2");
    // TODO https://github.com/paulmasson/math/issues/36
    mathcellBuilder.put(S.HarmonicNumber, "harmonic");
    mathcellBuilder.put(S.Haversine, "haversine");
    mathcellBuilder.put(S.HermiteH, "hermite");
    mathcellBuilder.put(S.HurwitzZeta, "hurwitzZeta");
    mathcellBuilder.put(S.Hypergeometric0F1, "hypergeometric0F1");
    mathcellBuilder.put(S.Hypergeometric1F1, "hypergeometric1F1");
    // FUNCTIONS_STR_MATHCELL.put(S.Hypergeometric2??, "hypergeometric2F0");
    mathcellBuilder.put(S.Hypergeometric2F1, "hypergeometric2F1");
    mathcellBuilder.put(S.HypergeometricPFQ, "hypergeometricPFQ");
    mathcellBuilder.put(S.HypergeometricU, "hypergeometricU");

    mathcellBuilder.put(S.Im, "im");
    mathcellBuilder.put(S.IntegerPart, "integerPart");
    mathcellBuilder.put(S.InverseGudermannian, "inverseGudermannian");
    mathcellBuilder.put(S.InverseHaversine, "inverseHaversine");
    mathcellBuilder.put(S.InverseWeierstrassP, "inverseWeierstrassP");

    mathcellBuilder.put(S.JacobiAmplitude, "am");
    mathcellBuilder.put(S.JacobiCN, "cn");
    mathcellBuilder.put(S.JacobiDN, "dn");
    mathcellBuilder.put(S.JacobiSN, "sn");
    mathcellBuilder.put(S.JacobiZeta, "jacobiZeta");

    mathcellBuilder.put(S.KleinInvariantJ, "kleinJ");
    mathcellBuilder.put(S.KroneckerDelta, "kronecker");

    mathcellBuilder.put(S.LaguerreL, "laguerre");
    mathcellBuilder.put(S.LegendreP, "legendreP");
    mathcellBuilder.put(S.LegendreQ, "legendreQ");
    mathcellBuilder.put(S.Log, "log");
    mathcellBuilder.put(S.LogGamma, "logGamma");
    mathcellBuilder.put(S.LogIntegral, "logIntegral");

    mathcellBuilder.put(S.Max, "Math.max");
    mathcellBuilder.put(S.Min, "Math.min");
    mathcellBuilder.put(S.Multinomial, "multinomial");

    mathcellBuilder.put(S.PolyGamma, "digamma");
    // PM: Since polylog is a shortened form of the full function name, polylogarithm, the small "l"
    // is more appropriate here:
    mathcellBuilder.put(S.PolyLog, "polylog");
    mathcellBuilder.put(S.ProductLog, "lambertW");

    mathcellBuilder.put(S.Re, "re");
    mathcellBuilder.put(S.Root, "root");
    mathcellBuilder.put(S.Round, "round");

    mathcellBuilder.put(S.Sinc, "sinc");
    mathcellBuilder.put(S.Sign, "sign");
    mathcellBuilder.put(S.SinIntegral, "sinIntegral");
    mathcellBuilder.put(S.SinhIntegral, "sinhIntegral");
    mathcellBuilder.put(S.SphericalBesselJ, "sphericalBesselJ");
    mathcellBuilder.put(S.SphericalBesselY, "sphericalBesselY");
    mathcellBuilder.put(S.SphericalHankelH1, "sphericalHankel1");
    mathcellBuilder.put(S.SphericalHankelH2, "sphericalHankel2");
    mathcellBuilder.put(S.StruveH, "struveH");
    mathcellBuilder.put(S.StruveL, "struveL");
    mathcellBuilder.put(S.Surd, "surd");

    mathcellBuilder.put(S.WeierstrassHalfPeriods, "weierstrassHalfPeriods");
    mathcellBuilder.put(S.WeierstrassInvariants, "weierstrassInvariants");
    mathcellBuilder.put(S.WeierstrassP, "weierstrassP");
    mathcellBuilder.put(S.WeierstrassPPrime, "weierstrassPPrime");
    mathcellBuilder.put(S.WhittakerM, "whittakerM");
    mathcellBuilder.put(S.WhittakerW, "whittakerW");
    mathcellBuilder.put(S.Zeta, "zeta");

    // mathcellBuilder.put(S.SphericalHarmonic, "sphericalHarmonic");
    mathcellBuilder.put(S.Sin, "sin");
    mathcellBuilder.put(S.Cos, "cos");
    mathcellBuilder.put(S.Tan, "tan");
    mathcellBuilder.put(S.Cot, "cot");
    mathcellBuilder.put(S.Sec, "sec");
    mathcellBuilder.put(S.Csc, "csc");

    mathcellBuilder.put(S.ArcSin, "arcsin");
    mathcellBuilder.put(S.ArcCos, "arccos");
    mathcellBuilder.put(S.ArcTan, "arctan");
    mathcellBuilder.put(S.ArcCot, "arccot");
    mathcellBuilder.put(S.ArcSec, "arcsec");
    mathcellBuilder.put(S.ArcCsc, "arccsc");

    mathcellBuilder.put(S.Sinh, "sinh");
    mathcellBuilder.put(S.Cosh, "cosh");
    mathcellBuilder.put(S.Tanh, "tanh");
    mathcellBuilder.put(S.Coth, "coth");
    mathcellBuilder.put(S.Sech, "sech");
    mathcellBuilder.put(S.Csch, "csch");

    mathcellBuilder.put(S.ArcSinh, "arcsinh");
    mathcellBuilder.put(S.ArcCosh, "arccosh");
    mathcellBuilder.put(S.ArcTanh, "arctanh");
    mathcellBuilder.put(S.ArcCoth, "arccoth");
    mathcellBuilder.put(S.ArcSech, "arcsech");
    mathcellBuilder.put(S.ArcCsch, "arccsch");
    FUNCTIONS_STR_MATHCELL = mathcellBuilder.build();

    //
    // pure JavaScript mappings
    //
    ImmutableMap.Builder<ISymbol, String> pureJSBuilder = ImmutableMap.builder();
    pureJSBuilder.put(S.Abs, "Math.abs");

    pureJSBuilder.put(S.ArcCos, "Math.acos");
    pureJSBuilder.put(S.ArcCosh, "Math.acosh");
    pureJSBuilder.put(S.ArcSin, "Math.asin");
    pureJSBuilder.put(S.ArcSinh, "Math.asinh");
    pureJSBuilder.put(S.ArcTan, "Math.atan");
    pureJSBuilder.put(S.ArcTanh, "Math.atanh");

    pureJSBuilder.put(S.Ceiling, "Math.ceil");
    pureJSBuilder.put(S.Cos, "Math.cos");
    pureJSBuilder.put(S.Cosh, "Math.cosh");
    pureJSBuilder.put(S.Exp, "Math.exp");
    pureJSBuilder.put(S.Floor, "Math.floor");
    pureJSBuilder.put(S.IntegerPart, "Math.trunc");

    pureJSBuilder.put(S.Log, "Math.log");
    pureJSBuilder.put(S.Max, "Math.max");
    pureJSBuilder.put(S.Min, "Math.min");
    // Power is handled by coding
    // FUNCTIONS_STR_PURE_JS.put(S.Power, "Math.pow");
    pureJSBuilder.put(S.Round, "Math.round");
    pureJSBuilder.put(S.Sign, "Math.sign");
    pureJSBuilder.put(S.Sin, "Math.sin");
    pureJSBuilder.put(S.Sinh, "Math.sinh");
    pureJSBuilder.put(S.Tan, "Math.tan");
    pureJSBuilder.put(S.Tanh, "Math.tanh");
    FUNCTIONS_STR_PURE_JS = pureJSBuilder.build();
  }

  public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures) {
    this(relaxedSyntax, reversed, exponentFigures, significantFigures, USE_PURE_JS);
  }

  /**
   * JavaScript converter constructor.
   *
   * @param relaxedSyntax
   * @param reversed
   * @param exponentFigures
   * @param significantFigures
   * @param javascriptFlavor
   */
  public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures, int javascriptFlavor) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
    this.sliderNames = new ArrayList<String>();
    this.variableNames = new ArrayList<String>();
    this.javascriptFlavor = javascriptFlavor;
  }

  public void appendSlider(String sliderName) {
    sliderNames.add(sliderName);
  }

  public void setVariables(ISymbol sliderName) {
    variableNames.clear();
    variableNames.add(sliderName.toString());
  }

  /**
   * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user
   * readable string.
   *
   * @param relaxedSyntax If <code>true</code> use paranthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @return
   */
  public static JavaScriptFormFactory get(final boolean relaxedSyntax) {
    return get(relaxedSyntax, false);
  }

  /**
   * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user
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
  public static JavaScriptFormFactory get(final boolean relaxedSyntax, final boolean plusReversed) {
    return get(relaxedSyntax, plusReversed, -1, -1);
  }

  /**
   * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user
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
  public static JavaScriptFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
      int exponentFigures, int significantFigures) {
    return new JavaScriptFormFactory(relaxedSyntax, plusReversed, exponentFigures,
        significantFigures);
  }

  @Override
  public String functionHead(ISymbol symbol) {
    if (javascriptFlavor == USE_MATHCELL) {
      return FUNCTIONS_STR_MATHCELL.get(symbol);
    }
    return FUNCTIONS_STR_PURE_JS.get(symbol);
  }

  @Override
  public void convertSymbol(final StringBuilder buf, final ISymbol symbol) {

    if (symbol.isBuiltInSymbol()) {
      String str = functionHead(symbol);
      if (str != null) {
        buf.append(str);
        return;
      }
    }
    if (sliderNames != null && sliderNames.contains(symbol.toString())) {
      buf.append(symbol.toString() + ".Value()");
      return;
    }
    if (symbol == S.Indeterminate) {
      buf.append("Number.NaN");
      return;
    }
    super.convertSymbol(buf, symbol);
  }

  /**
   * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user
   * readable string, with <code>relaxedSyntax</code> set to false.
   *
   * @return
   * @see #get(boolean)
   */
  public static JavaScriptFormFactory get() {
    return get(false);
  }

  @Override
  public void convertAST(final StringBuilder buf, final IAST function, boolean eval) {
    if (eval && function.isNumericFunction(true)) {
      try {
        double value = EvalEngine.get().evalDouble(function);
        buf.append("(" + value + ")");
        return;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol()) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        if (function.isASTSizeGE(S.Round, 3)) {
          throw new ArgumentTypeException("Cannot convert to JavaScript: " + function.toString());
        }
        if (function.isAST(S.ArcTan, 3)) {
          // swap arguments for atan2() convention in JavaScript
          IASTMutable arcTan2 = function.copy();
          arcTan2.set(1, function.arg2());
          arcTan2.set(2, function.arg1());
          buf.append("Math.atan2");
          convertArgs(buf, head, arcTan2);
          return;
        }
        buf.append(str);
        convertArgs(buf, head, function);
        return;
      }
      if (javascriptFlavor == USE_MATHCELL && function.headID() < 0) {
        if (Config.FUZZY_PARSER) {
          throw new ArgumentTypeException(
              "Cannot convert to JavaScript. Function head: " + function.head());
        }
        // avoid generating JavaScript eval(head) here
        buf.append("(window[");
        convertInternal(buf, head);
        buf.append("](");
        convertArgs(buf, head, function);
        buf.append("))");
        return;
      }
    }
    if (function.isList()) {
      // interpret List() as javascript array
      buf.append("[");
      for (int i = 1; i < function.size(); i++) {
        convertInternal(buf, function.get(i));
        if (i < function.size() - 1) {
          buf.append(",");
        }
      }
      buf.append("]");
      return;
    }
    if (function.isAST(S.Defer, 2) || function.isAST(S.Evaluate, 2) || function.isAST(S.Hold, 2)
        || function.isUnevaluated()) {
      convertInternal(buf, function.first(), Integer.MIN_VALUE, false, false);
      return;
    }
    if (javascriptFlavor == USE_MATHCELL) {
      if (function.isPlus() || function.isTimes()) {
        if (function.size() >= 3) {
          for (int i = 1; i < function.size() - 1; i++) {
            if (function.isPlus()) {
              buf.append("add(");
            } else {
              buf.append("mul(");
            }
          }
          convertInternal(buf, function.arg1());
          buf.append(",");
          for (int i = 2; i < function.size(); i++) {
            convertInternal(buf, function.get(i));
            buf.append(")");
            if (i < function.size() - 1) {
              buf.append(",");
            }
          }
          return;
        }
      } else if (function.isPower()) {
        convertPowerMathcell(buf, function);
        return;
      } else if (function.isInfinity()) {
        buf.append("Number.POSITIVE_INFINITY");
        return;
      } else if (function.isNegativeInfinity()) {
        buf.append("Number.NEGATIVE_INFINITY");
        return;
      } else if (function.head() == S.Log) {
        if (function.isAST1()) {
          IExpr arg1 = function.first();
          buf.append("log(");
          convertInternal(buf, arg1);
          buf.append(", Math.E)");
          return;
        } else if (function.isAST2()) {
          IExpr arg1 = function.first();
          IExpr arg2 = function.second();
          buf.append("log(");
          convertInternal(buf, arg1);
          buf.append(", ");
          convertInternal(buf, arg2);
          buf.append(")");
          return;
        }
      } else if (function.head() == S.Piecewise && function.size() > 1) {
        int[] dim = function.isPiecewise();
        if (dim != null && convertPiecewise(dim, function, buf)) {
          return;
        }
      } else if (function.isAST(S.ConditionalExpression, 3)) {
        convertConditionalExpression(function, buf);
        return;
      } else if (function.head() == S.HeavisideTheta && function.size() >= 2) {
        convertHeavisideTheta(function, buf);
        return;
      } else if (function.isAST(S.LogisticSigmoid, 2)) {
        convertLogisticSigmoid(function, buf);
        return;
      }
      IAST piecewiseExpand = PiecewiseFunctions.piecewiseExpand(function, S.Reals);
      int[] dim = piecewiseExpand.isPiecewise();
      if (dim != null && convertPiecewise(dim, piecewiseExpand, buf)) {
        return;
      }
    } else {
      if (function.isPower()) {
        convertPower(buf, function);
        return;
      } else if (function.isInfinity()) {
        buf.append("Number.POSITIVE_INFINITY");
        return;
      } else if (function.isNegativeInfinity()) {
        buf.append("Number.NEGATIVE_INFINITY");
        return;
      } else if (function.head() == S.Piecewise && function.size() > 1) {
        int[] dim = function.isPiecewise();
        if (dim != null && convertPiecewise(dim, function, buf)) {
          return;
        }
      } else if (function.isAST(S.ConditionalExpression, 3)) {
        convertConditionalExpression(function, buf);
        return;
      } else if (function.isAST(S.Cot, 2)) {
        buf.append("(1/Math.tan(");
        convertInternal(buf, function.arg1());
        buf.append("))");
        return;
      } else if (function.isAST(S.ArcCot, 2)) {
        buf.append("((Math.PI/2.0)-Math.atan(");
        convertInternal(buf, function.arg1());
        buf.append("))");
        return;
      } else if (function.isAST(S.LogisticSigmoid, 2)) {
        buf.append("(1/(1+Math.exp(-(");
        convertInternal(buf, function.arg1());
        buf.append("))))");
        return;
      }
      IAST piecewiseExpand = PiecewiseFunctions.piecewiseExpand(function, S.Reals);
      int[] dim = piecewiseExpand.isPiecewise();
      if (dim != null && convertPiecewise(dim, piecewiseExpand, buf)) {
        return;
      }
    }

    if (function.head() == S.If && function.size() >= 3 && function.size() <= 4) {
      // use the ternary operator
      buf.append("((");
      convertInternal(buf, function.arg1());
      buf.append(") ? (");
      convertInternal(buf, function.arg2());
      buf.append(") : ( ");
      if (function.size() == 4) {
        convertInternal(buf, function.arg3());
      } else {
        buf.append("Number.NaN");
      }
      buf.append(" ))");
      return;
    }
    if (function.isAST(S.Mod, 3)) {
      IExpr arg1 = function.arg1();
      IExpr arg2 = function.arg2();
      if ((arg1.isNonNegativeResult() && arg2.isNonNegativeResult()) //
          || (arg1.isNegativeResult() && arg2.isNegativeResult())) {
        buf.append("(");
        convertInternal(buf, arg1, Precedence.POWER, false, true);
        buf.append(" % ");
        convertInternal(buf, arg2, Precedence.POWER, false, true);
        buf.append(")");
        return;
      }
      buf.append("(((");
      convertInternal(buf, arg1, Precedence.POWER, false, true);
      buf.append(" % ");
      convertInternal(buf, arg2, Precedence.POWER, false, true);
      buf.append(")");
      buf.append(" + ");
      convertInternal(buf, arg2, Precedence.POWER, false, true);
      buf.append(") % ");
      convertInternal(buf, arg2, Precedence.POWER, false, true);
      buf.append(")");
      return;
    }
    if (function.isAST(S.Missing)) {
      // Missing value
      buf.append("Number.NaN");
      return;
    }
    if (function.headID() > 0) {
      throw new ArgumentTypeException(
          "Cannot convert to JavaScript. Function head: " + function.head());
    }

    convertInternal(buf, head);
    convertArgs(buf, head, function);
  }

  /**
   * Convert the <code>Power(base, exponent)</code> expression into JavaScript form.
   *
   * @param buf
   * @param powerAST the <code>Power(base, exponent)</code> expression
   */
  private void convertPower(final StringBuilder buf, final IAST powerAST) {
    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();
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
    convertArgs(buf, powerAST.head(), powerAST);
  }

  /**
   * Convert the <code>Power(base, exponent)</code> expression into JavaScript form.
   *
   * @param buf
   * @param powerAST the <code>Power(base, exponent)</code> expression
   */
  private void convertPowerMathcell(final StringBuilder buf, final IAST powerAST) {
    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();
    if (exponent.isMinusOne()) {
      buf.append("inv(");
      convertInternal(buf, base);
      buf.append(")");
      return;
    }
    if (exponent.isNumEqualRational(F.C1D2)) {
      buf.append("sqrt(");
      convertInternal(buf, base);
      buf.append(")");
      return;
    }
    buf.append("pow");
    convertArgs(buf, powerAST.head(), powerAST);
  }

  private void convertConditionalExpression(final IAST function, final StringBuilder buf) {
    IExpr arg1 = function.arg1();
    IExpr arg2 = function.arg2();
    buf.append("((");
    convertInternal(buf, arg2);
    buf.append(") ? (");
    convertInternal(buf, arg1);
    buf.append(") : ( Number.NaN ))");
  }

  private void convertHeavisideTheta(final IAST function, final StringBuilder buf) {
    IExpr arg1 = function.arg1();
    buf.append("((");
    convertInternal(buf, arg1);
    buf.append(" > 0 ) ");
    for (int i = 2; i < function.size(); i++) {
      buf.append("&& (");
      convertInternal(buf, function.get(i));
      buf.append(" > 0 ) ");
    }
    buf.append("? 1:0)");
  }

  private void convertLogisticSigmoid(final IAST function, final StringBuilder buf) {
    // wait for releasing: https://github.com/paulmasson/math/issues/33
    // buf.append("logisticSigmoid(");
    // convertInternal(buf, function.arg1());
    // buf.append(")");
    buf.append("div(1,add(1,exp(neg(");
    convertInternal(buf, function.arg1());
    buf.append("))))");
  }

  private boolean convertPiecewise(int dim[], final IAST function, final StringBuilder buffer) {
    IAST list = (IAST) function.arg1();
    IExpr last = function.size() == 3 ? function.arg2() : F.C0;
    StringBuilder piecewiseBuffer = new StringBuilder();
    if (INLINE_PIECEWISE) {
      // use the ternary operator
      int size = list.size();
      piecewiseBuffer.append("(");
      int countOpen = 0;
      for (int i = 1; i < size; i++) {
        IExpr arg = list.get(i);
        if (arg.isList2()) {
          IAST row = (IAST) arg;
          if (i > 1) {
            piecewiseBuffer.append("(");
            countOpen++;
          }
          piecewiseBuffer.append("(");
          convertInternal(piecewiseBuffer, row.second());
          piecewiseBuffer.append(") ? ");
          convertInternal(piecewiseBuffer, row.first());
          piecewiseBuffer.append(" : ");
        } else {
          if (i == size - 1) {
            last = arg;
          } else {
            return false;
          }
        }
      }
      piecewiseBuffer.append("( ");
      convertInternal(piecewiseBuffer, last);
      piecewiseBuffer.append(" )");
      for (int i = 0; i < countOpen; i++) {
        piecewiseBuffer.append(" )");
      }
      piecewiseBuffer.append(")");
      buffer.append(piecewiseBuffer);
      return true;
    } else {
      // use if... statements
      piecewiseBuffer.append("\n (function(");
      appendVariables(piecewiseBuffer);
      piecewiseBuffer.append(") {");
      final int size = list.size();
      for (int i = 1; i < size; i++) {
        IExpr arg = list.get(i);
        if (arg.isList2()) {
          IAST row = (IAST) arg;
          // if (i == 1) {
          // piecewiseBuffer.append("if (");
          // convertInternal(piecewiseBuffer, row.second());
          // piecewiseBuffer.append(") {\n");
          // } else {
          piecewiseBuffer.append("\nif (");
          convertInternal(piecewiseBuffer, row.second());
          piecewiseBuffer.append(") {");
          // }
          piecewiseBuffer.append(" return ");
          convertInternal(piecewiseBuffer, row.first());
          piecewiseBuffer.append(";}");
        } else {
          if (i == size - 1) {
            last = arg;
          } else {
            return false;
          }
        }
      }
      piecewiseBuffer.append("\n return ");
      convertInternal(piecewiseBuffer, last);
      piecewiseBuffer.append(";})(");
      appendVariables(piecewiseBuffer);
      piecewiseBuffer.append(")\n");
      buffer.append(piecewiseBuffer);
      return true;
    }
  }

  private void appendVariables(StringBuilder buf) {
    for (int i = 0; i < variableNames.size(); i++) {
      buf.append(variableNames.get(i));
      if (i < variableNames.size() - 1) {
        buf.append(",");
      }
    }
  }

  @Override
  protected boolean convertOperator(final Operator operator, final IAST list,
      final StringBuilder buf, final int precedence, ISymbol head) {
    if (!super.convertOperator(operator, list, buf, precedence, head)) {
      if (javascriptFlavor == USE_MATHCELL) {
        convertAST(buf, list, true);
        return true;
      }
      return false;
    }

    return true;
  }

  @Override
  public Operator getOperator(ISymbol head) {
    if (javascriptFlavor == USE_MATHCELL) {
      if (head.isSymbolID(ID.Equal, ID.Unequal, ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual,
          ID.And, ID.Or, ID.Not)) {
        return OutputFormFactory.getOperator(head, head == S.Not ? 1 : 2);
      }
      return null;
    }
    return super.getOperator(head);
  }

  @Override
  public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence,
      boolean caller) {
    buf.append("complex(");
    convertFraction(buf, c.getRealPart(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
    buf.append(",");
    convertFraction(buf, c.getImaginaryPart(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
    buf.append(")");
  }

  @Override
  public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc,
      final int precedence, boolean caller) {
    buf.append("complex(");
    convertDoubleString(buf, convertDoubleToFormattedString(dc.getRealPart()),
        Precedence.NO_PRECEDENCE, false);
    buf.append(",");
    convertDoubleString(buf, convertDoubleToFormattedString(dc.getImaginaryPart()),
        Precedence.NO_PRECEDENCE, false);
    buf.append(")");
  }
}
