package org.matheclipse.core.form.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Transpile an internal <code>IExpr</code> into a JavaScript string. It can especially generate
 * JavaScript output for usage with the JavaScript libraries:
 *
 * <ul>
 *   <li><a href="https://github.com/paulmasson/math">github.com/paulmasson/math</a>
 *   <li><a href="https://github.com/paulmasson/mathcell">github.com/paulmasson/mathcell</a>
 * </ul>
 */
public class JavaScriptFormFactory extends DoubleFormFactory {

  /** Generate pure JavaScript output */
  public static final int USE_PURE_JS = 1;

  /**
   * Generate JavaScript output for usage with the JavaScript libraries:
   *
   * <ul>
   *   <li><a href="https://github.com/paulmasson/math">github.com/paulmasson/math</a>
   *   <li><a href="https://github.com/paulmasson/mathcell">github.com/paulmasson/mathcell</a>
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

  private static final Map<ISymbol, String> FUNCTIONS_STR_MATHCELL = new HashMap<ISymbol, String>();
  private static final Map<ISymbol, String> FUNCTIONS_STR_PURE_JS = new HashMap<ISymbol, String>();

  static {
    FUNCTIONS_STR_MATHCELL.put(S.BetaRegularized, "betaRegularized");
    FUNCTIONS_STR_MATHCELL.put(S.AiryAi, "airyAi");
    FUNCTIONS_STR_MATHCELL.put(S.AiryBi, "airyBi");
    FUNCTIONS_STR_MATHCELL.put(S.DirichletEta, "dirichletEta");
    FUNCTIONS_STR_MATHCELL.put(S.HankelH1, "hankel1");
    FUNCTIONS_STR_MATHCELL.put(S.HankelH2, "hankel2");
    FUNCTIONS_STR_MATHCELL.put(S.GammaRegularized, "gammaRegularized");
    FUNCTIONS_STR_MATHCELL.put(S.InverseWeierstrassP, "inverseWeierstrassP");
    FUNCTIONS_STR_MATHCELL.put(S.PolyGamma, "digamma");
    FUNCTIONS_STR_MATHCELL.put(S.SphericalBesselJ, "sphericalBesselJ");
    FUNCTIONS_STR_MATHCELL.put(S.SphericalBesselY, "sphericalBesselY");
    FUNCTIONS_STR_MATHCELL.put(S.SphericalHankelH1, "sphericalHankel1");
    FUNCTIONS_STR_MATHCELL.put(S.SphericalHankelH2, "sphericalHankel2");

    FUNCTIONS_STR_MATHCELL.put(S.WeierstrassHalfPeriods, "weierstrassHalfPeriods");
    FUNCTIONS_STR_MATHCELL.put(S.WeierstrassInvariants, "weierstrassInvariants");
    FUNCTIONS_STR_MATHCELL.put(S.WeierstrassP, "weierstrassP");
    FUNCTIONS_STR_MATHCELL.put(S.WeierstrassPPrime, "weierstrassPPrime");
    FUNCTIONS_STR_MATHCELL.put(S.WhittakerM, "whittakerM");
    FUNCTIONS_STR_MATHCELL.put(S.WhittakerW, "whittakerW");

    FUNCTIONS_STR_MATHCELL.put(S.Abs, "abs");
    FUNCTIONS_STR_MATHCELL.put(S.Arg, "arg");
    FUNCTIONS_STR_MATHCELL.put(S.Chop, "chop");

    FUNCTIONS_STR_MATHCELL.put(S.BesselJ, "besselJ");
    FUNCTIONS_STR_MATHCELL.put(S.BesselY, "besselY");
    FUNCTIONS_STR_MATHCELL.put(S.BesselI, "besselI");
    FUNCTIONS_STR_MATHCELL.put(S.BesselK, "besselK");
    FUNCTIONS_STR_MATHCELL.put(S.StruveH, "struveH");
    FUNCTIONS_STR_MATHCELL.put(S.StruveL, "struveL");

    FUNCTIONS_STR_MATHCELL.put(S.BesselJZero, "besselJZero ");
    FUNCTIONS_STR_MATHCELL.put(S.BesselYZero, "besselYZero ");

    // TODO see math.js - https://github.com/paulmasson/math
    // FUNCTIONS_STR_MATHCELL.put(S.Hankel1, "hankel1");
    // FUNCTIONS_STR_MATHCELL.put(S.Hankel2, "hankel2");

    FUNCTIONS_STR_MATHCELL.put(S.AiryAi, "airyAi");
    FUNCTIONS_STR_MATHCELL.put(S.AiryBi, "airyBi");

    FUNCTIONS_STR_MATHCELL.put(S.EllipticF, "ellipticF");
    FUNCTIONS_STR_MATHCELL.put(S.EllipticK, "ellipticK");
    FUNCTIONS_STR_MATHCELL.put(S.EllipticE, "ellipticE");
    FUNCTIONS_STR_MATHCELL.put(S.EllipticPi, "ellipticPi");
    FUNCTIONS_STR_MATHCELL.put(S.EllipticTheta, "jacobiTheta");

    FUNCTIONS_STR_MATHCELL.put(S.JacobiAmplitude, "am");
    FUNCTIONS_STR_MATHCELL.put(S.JacobiCN, "cn");
    FUNCTIONS_STR_MATHCELL.put(S.JacobiDN, "dn");
    FUNCTIONS_STR_MATHCELL.put(S.JacobiSN, "sn");
    FUNCTIONS_STR_MATHCELL.put(S.JacobiZeta, "jacobiZeta");
    FUNCTIONS_STR_MATHCELL.put(S.KleinInvariantJ, "kleinJ");
    FUNCTIONS_STR_MATHCELL.put(S.Factorial, "factorial");
    FUNCTIONS_STR_MATHCELL.put(S.Factorial2, "factorial2");
    FUNCTIONS_STR_MATHCELL.put(S.Binomial, "binomial");
    FUNCTIONS_STR_MATHCELL.put(S.LogGamma, "logGamma");
    FUNCTIONS_STR_MATHCELL.put(S.Gamma, "gamma");
    FUNCTIONS_STR_MATHCELL.put(S.Beta, "beta");
    FUNCTIONS_STR_MATHCELL.put(S.Erf, "erf");
    FUNCTIONS_STR_MATHCELL.put(S.Erfc, "erfc");
    FUNCTIONS_STR_MATHCELL.put(S.FresnelC, "fresnelC");
    FUNCTIONS_STR_MATHCELL.put(S.FresnelS, "fresnelS");
    FUNCTIONS_STR_MATHCELL.put(S.Gudermannian, "gudermannian");
    FUNCTIONS_STR_MATHCELL.put(S.InverseGudermannian, "inverseGudermannian"); 
    // PM: Since polylog is a shortened form of the full function name, polylogarithm, the small "l"
    // is
    // more appropriate here:
    FUNCTIONS_STR_MATHCELL.put(S.PolyLog, "polylog");

    FUNCTIONS_STR_MATHCELL.put(S.CosIntegral, "cosIntegral");
    FUNCTIONS_STR_MATHCELL.put(S.CoshIntegral, "coshIntegral");
    FUNCTIONS_STR_MATHCELL.put(S.LogIntegral, "logIntegral");
    FUNCTIONS_STR_MATHCELL.put(S.SinIntegral, "sinIntegral");
    FUNCTIONS_STR_MATHCELL.put(S.SinhIntegral, "sinhIntegral");

    FUNCTIONS_STR_MATHCELL.put(S.ExpIntegralEi, "expIntegralEi");
    FUNCTIONS_STR_MATHCELL.put(S.ExpIntegralE, "expIntegralE");

    FUNCTIONS_STR_MATHCELL.put(S.Hypergeometric0F1, "hypergeometric0F1");
    FUNCTIONS_STR_MATHCELL.put(S.Hypergeometric1F1, "hypergeometric1F1");
    // FUNCTIONS_STR_MATHCELL.put(S.Hypergeometric2??, "hypergeometric2F0");
    FUNCTIONS_STR_MATHCELL.put(S.Hypergeometric2F1, "hypergeometric2F1");
    FUNCTIONS_STR_MATHCELL.put(S.HypergeometricPFQ, "hypergeometricPFQ");
    FUNCTIONS_STR_MATHCELL.put(S.Exp, "exp");
    FUNCTIONS_STR_MATHCELL.put(S.Im, "im");
    FUNCTIONS_STR_MATHCELL.put(S.Log, "log");
    FUNCTIONS_STR_MATHCELL.put(S.Re, "re");

    FUNCTIONS_STR_MATHCELL.put(S.ProductLog, "lambertW");
    FUNCTIONS_STR_MATHCELL.put(S.KroneckerDelta, "kronecker");

    FUNCTIONS_STR_MATHCELL.put(S.HermiteH, "hermite");
    FUNCTIONS_STR_MATHCELL.put(S.LaguerreL, "laguerre");
    FUNCTIONS_STR_MATHCELL.put(S.ChebyshevT, "chebyshevT");
    FUNCTIONS_STR_MATHCELL.put(S.ChebyshevU, "chebyshevU");
    FUNCTIONS_STR_MATHCELL.put(S.LegendreP, "legendreP");
    FUNCTIONS_STR_MATHCELL.put(S.LegendreQ, "legendreQ");
    
    // FUNCTIONS_STR_MATHCELL.put(S.SpheriacelHarmonic, "sphericalHarmonic");

    FUNCTIONS_STR_MATHCELL.put(S.Sin, "sin");
    FUNCTIONS_STR_MATHCELL.put(S.Cos, "cos");
    FUNCTIONS_STR_MATHCELL.put(S.Tan, "tan");
    FUNCTIONS_STR_MATHCELL.put(S.Cot, "cot");
    FUNCTIONS_STR_MATHCELL.put(S.Sec, "sec");
    FUNCTIONS_STR_MATHCELL.put(S.Csc, "csc");

    FUNCTIONS_STR_MATHCELL.put(S.ArcSin, "arcsin");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCos, "arccos");
    FUNCTIONS_STR_MATHCELL.put(S.ArcTan, "arctan");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCot, "arccot");
    FUNCTIONS_STR_MATHCELL.put(S.ArcSec, "arcsec");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCsc, "arccsc");

    FUNCTIONS_STR_MATHCELL.put(S.Sinh, "sinh");
    FUNCTIONS_STR_MATHCELL.put(S.Cosh, "cosh");
    FUNCTIONS_STR_MATHCELL.put(S.Tanh, "tanh");
    FUNCTIONS_STR_MATHCELL.put(S.Coth, "coth");
    FUNCTIONS_STR_MATHCELL.put(S.Sech, "sech");
    FUNCTIONS_STR_MATHCELL.put(S.Csch, "csch");

    FUNCTIONS_STR_MATHCELL.put(S.ArcSinh, "arcsinh");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCosh, "arccosh");
    FUNCTIONS_STR_MATHCELL.put(S.ArcTanh, "arctanh");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCoth, "arccoth");
    FUNCTIONS_STR_MATHCELL.put(S.ArcSech, "arcsech");
    FUNCTIONS_STR_MATHCELL.put(S.ArcCsch, "arccsch");

    FUNCTIONS_STR_MATHCELL.put(S.Sinc, "sinc");
    FUNCTIONS_STR_MATHCELL.put(S.HurwitzZeta, "hurwitzZeta");
    FUNCTIONS_STR_MATHCELL.put(S.Zeta, "zeta");
    // FUNCTIONS_STR_MATHCELL.put(S.DirichletEta, "dirichletEta");
    FUNCTIONS_STR_MATHCELL.put(S.BernoulliB, "bernoulli");

    FUNCTIONS_STR_MATHCELL.put(S.Ceiling, "ceiling");
    FUNCTIONS_STR_MATHCELL.put(S.Floor, "floor");
    FUNCTIONS_STR_MATHCELL.put(S.KroneckerDelta, "kronecker");
    FUNCTIONS_STR_MATHCELL.put(S.Round, "round");

    FUNCTIONS_STR_MATHCELL.put(S.IntegerPart, "integerPart");
    FUNCTIONS_STR_MATHCELL.put(S.FractionalPart, "fractionalPart");
    FUNCTIONS_STR_MATHCELL.put(S.Sign, "sign");
    FUNCTIONS_STR_MATHCELL.put(S.Max, "Math.max");
    FUNCTIONS_STR_MATHCELL.put(S.Min, "Math.min");
    FUNCTIONS_STR_MATHCELL.put(S.Surd, "surd");
    FUNCTIONS_STR_MATHCELL.put(S.Root, "root");

    //
    // pure JavaScript mappings
    //
    FUNCTIONS_STR_PURE_JS.put(S.Abs, "Math.abs");

    FUNCTIONS_STR_PURE_JS.put(S.ArcCos, "Math.acos");
    FUNCTIONS_STR_PURE_JS.put(S.ArcCosh, "Math.acosh");
    FUNCTIONS_STR_PURE_JS.put(S.ArcSin, "Math.asin");
    FUNCTIONS_STR_PURE_JS.put(S.ArcSinh, "Math.asinh");
    FUNCTIONS_STR_PURE_JS.put(S.ArcTan, "Math.atan");
    FUNCTIONS_STR_PURE_JS.put(S.ArcTanh, "Math.atanh");

    FUNCTIONS_STR_PURE_JS.put(S.Ceiling, "Math.ceil");
    FUNCTIONS_STR_PURE_JS.put(S.Cos, "Math.cos");
    FUNCTIONS_STR_PURE_JS.put(S.Cosh, "Math.cosh");
    FUNCTIONS_STR_PURE_JS.put(S.Exp, "Math.exp");
    FUNCTIONS_STR_PURE_JS.put(S.Floor, "Math.floor");
    FUNCTIONS_STR_PURE_JS.put(S.IntegerPart, "Math.trunc");

    FUNCTIONS_STR_PURE_JS.put(S.Log, "Math.log");
    FUNCTIONS_STR_PURE_JS.put(S.Max, "Math.max");
    FUNCTIONS_STR_PURE_JS.put(S.Min, "Math.min");
    // Power is handled by coding
    // FUNCTIONS_STR_PURE_JS.put(S.Power, "Math.pow");
    FUNCTIONS_STR_PURE_JS.put(S.Round, "Math.round");
    FUNCTIONS_STR_PURE_JS.put(S.Sign, "Math.sign");
    FUNCTIONS_STR_PURE_JS.put(S.Sin, "Math.sin");
    FUNCTIONS_STR_PURE_JS.put(S.Sinh, "Math.sinh");
    FUNCTIONS_STR_PURE_JS.put(S.Tan, "Math.tan");
    FUNCTIONS_STR_PURE_JS.put(S.Tanh, "Math.tanh");
  }

  public JavaScriptFormFactory(
      final boolean relaxedSyntax,
      final boolean reversed,
      int exponentFigures,
      int significantFigures) {
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
  public JavaScriptFormFactory(
      final boolean relaxedSyntax,
      final boolean reversed,
      int exponentFigures,
      int significantFigures,
      int javascriptFlavor) {
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
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
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
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *     be printed in reversed order
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
   *     case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *     brackets instead of double square brackets for extracting parts of an expression, i.e.
   *     {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *     be printed in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @return
   */
  public static JavaScriptFormFactory get(
      final boolean relaxedSyntax,
      final boolean plusReversed,
      int exponentFigures,
      int significantFigures) {
    return new JavaScriptFormFactory(
        relaxedSyntax, plusReversed, exponentFigures, significantFigures);
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
  public void convertAST(final StringBuilder buf, final IAST function) {
    if (function.isNumericFunction(true)) {
      try {
        double value = EvalEngine.get().evalDouble(function);
        buf.append("(" + value + ")");
        return;
      } catch (RuntimeException rex) {
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
          buf.append("Math.atan2");
        } else {
          buf.append(str);
        }
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
    if (function.isAST(S.Defer, 2)
        || function.isAST(S.Evaluate, 2)
        || function.isAST(S.Hold, 2)
        || function.isUnevaluated()) {
      convertInternal(buf, function.first());
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
      } else if (function.head() == S.ConditionalExpression && function.size() == 3) {
        convertConditionalExpression(function, buf);
        return;
      } else if (function.head() == S.HeavisideTheta && function.size() >= 2) {
        convertHeavisideTheta(function, buf);
        return;
      }
      IAST piecewiseExpand = Arithmetic.piecewiseExpand(function, S.Reals);
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
      } else if (function.head() == S.ConditionalExpression && function.size() == 3) {
        convertConditionalExpression(function, buf);
        return;
      } else if (function.head() == S.Cot && function.size() == 2) {
        buf.append("(1/Math.tan(");
        convertInternal(buf, function.arg1());
        buf.append("))");
        return;
      } else if (function.head() == S.ArcCot && function.size() == 2) {
        buf.append("((Math.PI/2.0)-Math.atan(");
        convertInternal(buf, function.arg1());
        buf.append("))");
        return;
      }
      IAST piecewiseExpand = Arithmetic.piecewiseExpand(function, S.Reals);
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
    // use the ternary operator
    buf.append("((");
    convertInternal(buf, arg2);
    buf.append(") ? (");
    convertInternal(buf, arg1);
    buf.append(") : ( Number.NaN ))");
  }

  private void convertHeavisideTheta(final IAST function, final StringBuilder buf) {
    IExpr arg1 = function.arg1();
    // use the ternary operator
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
          //          if (i == 1) {
          //            piecewiseBuffer.append("if (");
          //            convertInternal(piecewiseBuffer, row.second());
          //            piecewiseBuffer.append(") {\n");
          //          } else {
          piecewiseBuffer.append("\nif (");
          convertInternal(piecewiseBuffer, row.second());
          piecewiseBuffer.append(") {");
          //          }
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
  protected boolean convertOperator(
      final Operator operator,
      final IAST list,
      final StringBuilder buf,
      final int precedence,
      ISymbol head) {
    if (!super.convertOperator(operator, list, buf, precedence, head)) {
      if (javascriptFlavor == USE_MATHCELL) {
        convertAST(buf, list);
        return true;
      }
      return false;
    }

    return true;
  }

  @Override
  public Operator getOperator(ISymbol head) {
    if (javascriptFlavor == USE_MATHCELL) {
      if (head.isSymbolID(
          ID.Equal,
          ID.Unequal,
          ID.Less,
          ID.LessEqual,
          ID.Greater,
          ID.GreaterEqual,
          ID.And,
          ID.Or,
          ID.Not)) {
        return OutputFormFactory.getOperator(head);
      }
      return null;
    }
    return super.getOperator(head);
  }

  @Override
  public void convertComplex(
      final StringBuilder buf, final IComplex c, final int precedence, boolean caller) {
    buf.append("complex(");
    convertFraction(buf, c.getRealPart(), 0, NO_PLUS_CALL);
    buf.append(",");
    convertFraction(buf, c.getImaginaryPart(), 0, NO_PLUS_CALL);
    buf.append(")");
  }

  @Override
  public void convertDoubleComplex(
      final StringBuilder buf, final IComplexNum dc, final int precedence, boolean caller) {
    buf.append("complex(");
    convertDoubleString(buf, convertDoubleToFormattedString(dc.getRealPart()), 0, false);
    buf.append(",");
    convertDoubleString(buf, convertDoubleToFormattedString(dc.getImaginaryPart()), 0, false);
    buf.append(")");
  }
}
