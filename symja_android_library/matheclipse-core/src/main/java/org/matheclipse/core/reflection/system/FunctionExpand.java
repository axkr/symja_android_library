package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import java.util.function.Supplier;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.reflection.system.rules.FunctionExpandRules;
import com.google.common.base.Suppliers;

/**
 *
 *
 * <pre>
 * FunctionExpand(f)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * expands the special function <code>f</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; FunctionExpand(Beta(10, b))
 * 362880/(b*(1+b)*(2+b)*(3+b)*(4+b)*(5+b)*(6+b)*(7+b)*(8+b)*(9+b))
 * </pre>
 */
public class FunctionExpand extends AbstractEvaluator {

  private static Supplier<Matcher> LAZY_MATCHER;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static Matcher init() {
      Matcher MATCHER = new Matcher();
      // Beta
      // MATCHER.caseOf(Beta(z_, a_, b_), //
      // // [$ Beta(a, b)*(1 - (1 - z)^b*Sum((Pochhammer(b, k)*z^k)/k!, {k, 0, a - 1})) /;
      // // IntegerQ(a)&&a>0
      // // $]
      // F.Condition(
      // F.Times(F.Beta(a, b), F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Subtract(F.C1, z), b),
      // F.Sum(F.Times(F.Power(z, k), F.Power(F.Factorial(k), F.CN1), F.Pochhammer(b, k)),
      // F.list(k, F.C0, F.Plus(F.CN1, a)))))),
      // F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);
      //
      // MATCHER.caseOf(Beta(a_, b_), //
      // // [$ Factorial(a-1)*Product((k+b)^(-1),{k,0,a-1}) /; IntegerQ(a)&&a>0 $]
      // F.Condition(
      // F.Times(F.Factorial(F.Plus(F.CN1, a)),
      // F.Product(F.Power(F.Plus(k, b), F.CN1), F.list(k, F.C0, F.Plus(F.CN1, a)))),
      // F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);
      //
      // MATCHER.caseOf(F.BetaRegularized(z_, a_, b_), //
      // // [$ (Beta(z, a, b)*Gamma(a + b))/(Gamma(a)*Gamma(b)) $]
      // F.Times(F.Beta(z, a, b), F.Power(F.Times(F.Gamma(a), F.Gamma(b)), F.CN1),
      // F.Gamma(F.Plus(a, b)))); // $$);

      MATCHER.caseOf(Binomial(a_, b_), //
          // [$ If(IntegerQ(b)&&Positive(b),Product(a-c,{c,0,b-1})/b!, If(IntegerQ(a)&&Positive(a),
          // (a!*Sin(b*Pi))/(Product(b-c,{c,0,a})*Pi), Gamma(1 + a)/(Gamma(1 + b)*Gamma(1 - b + a)))
          // ) $]
          F.If(
              F.And(F.IntegerQ(b), F
                  .Positive(b)),
              F.Times(
                  F.Power(F
                      .Factorial(b), F.CN1),
                  F.Product(F.Subtract(a, c), F.list(c, F.C0, F.Plus(F.CN1, b)))),
              F.If(F.And(F.IntegerQ(a), F.Positive(a)),
                  F.Times(F.Power(F.Times(F.Product(F.Subtract(b, c), F.list(c, F.C0, a)), F.Pi),
                      F.CN1), F.Factorial(a), F.Sin(F.Times(b, F.Pi))),
                  F.Times(F.Gamma(F.Plus(F.C1, a)),
                      F.Power(
                          F.Times(F.Gamma(F.Plus(F.C1, b)), F.Gamma(F.Plus(F.C1, F.Negate(b), a))),
                          F.CN1))))); // $$);

      // Cos
      MATCHER.caseOf(F.Cos(F.Sqrt(F.Sqr(x_))), //
          F.Cos(x));
      // Sin
      MATCHER.caseOf(F.Sin(F.Sqrt(F.Sqr(x_))), //
          // [$ (Sqrt(x^2)*Sin(x))/x $]
          F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.Sin(x))); // $$);

      // CosIntegral
      MATCHER.caseOf(F.CosIntegral(F.Times(F.CN1, x_)), //
          // [$ CosIntegral(x) + Log(x) - Log(x)
          // $]
          F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(x))); // $$);
      MATCHER.caseOf(F.CosIntegral(F.Times(F.CI, x_)), //
          // [$ CoshIntegral(x) - Log(x) + Log(I*x)
          // $]
          F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CI, x)))); // $$);
      MATCHER.caseOf(F.CosIntegral(F.Times(F.CNI, x_)), //
          // [$ CoshIntegral(x) - Log(x) + Log(-I*x)
          // $]
          F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CNI, x)))); // $$);
      MATCHER.caseOf(F.CosIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
          // [$ CosIntegral(x) + Log(Sqrt(x^2)) - Log(x)
          // $]
          F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(F.Sqrt(F.Sqr(x))))); // $$);

      // SinIntegral
      MATCHER.caseOf(F.SinIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
          // [$ (Sqrt(x^2)/x) * SinIntegral(x)
          // $]
          F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.SinIntegral(x))); // $$);

      // Factorial
      // MATCHER.caseOf(Factorial(x_), //
      // // [$ Gamma(1+x) $]
      // F.Gamma(F.Plus(F.C1, x))); // $$);

      // Haversine
      MATCHER.caseOf(F.Haversine(x_), //
          // [$ (1/2) * (1 - Cos(x)) $]
          F.Times(F.C1D2, F.Subtract(F.C1, F.Cos(x)))); // $$);
      // InverseHaversine
      MATCHER.caseOf(F.InverseHaversine(x_), //
          // [$ 2*ArcSin( Sqrt(x) ) $]
          F.Times(F.C2, F.ArcSin(F.Sqrt(x)))); // $$);

      // Pochhammer
      // MATCHER.caseOf(F.Pochhammer(x_, y_), //
      // // [$ Gamma(x+y)/Gamma(x) $]
      // F.Times(F.Power(F.Gamma(x), F.CN1), F.Gamma(F.Plus(x, y)))); // $$);
      // PolyGamma
      MATCHER.caseOf(F.PolyGamma(F.CN2, F.C1), //
          // [$ (1/2)*(Log(2)+Log(Pi)) $]
          F.Times(F.C1D2, F.Plus(F.Log(F.C2), F.Log(F.Pi)))); // $$);
      MATCHER.caseOf(F.PolyGamma(F.CN3, F.C1), //
          // [$ Log(Glaisher) + (1/4)*(Log(2) + Log(Pi)) $]
          F.Plus(F.Log(F.Glaisher), F.Times(F.C1D4, F.Plus(F.Log(F.C2), F.Log(F.Pi))))); // $$);

      MATCHER.caseOf(S.Degree, //
          // [$ Pi/180 $]
          F.Times(F.QQ(1L, 180L), F.Pi)); // $$);
      MATCHER.caseOf(S.GoldenAngle, //
          // [$ (3-Sqrt(5))*Pi $]
          F.Times(F.Subtract(F.C3, F.CSqrt5), F.Pi)); // $$);
      MATCHER.caseOf(S.GoldenRatio, //
          // [$ 1/2*(1+Sqrt(5)) $]
          F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))); // $$);

      // Power
      MATCHER.caseOf(F.Power(S.E, F.ArcSinh(x_)), //
          // [$ (x+Sqrt(1+x^2)) $]
          F.Plus(x, F.Sqrt(F.Plus(F.C1, F.Sqr(x))))); // $$);
      MATCHER.caseOf(F.Power(S.E, F.ArcCosh(x_)), //
          // [$ (x+Sqrt(x-1)*Sqrt(x+1)) $]
          F.Plus(x, F.Times(F.Sqrt(F.Plus(F.CN1, x)), F.Sqrt(F.Plus(x, F.C1))))); // $$);
      MATCHER.caseOf(F.Power(S.E, F.ArcTanh(x_)), //
          // [$ ((x+1)/Sqrt(1-x^2)) $]
          F.Times(F.Plus(x, F.C1), F.Power(F.Subtract(F.C1, F.Sqr(x)), F.CN1D2))); // $$);
      MATCHER.caseOf(F.Power(S.E, F.ArcCsch(x_)), //
          // [$ (1/x+Sqrt(1+1/x^2)) $]
          F.Plus(F.Power(x, F.CN1), F.Sqrt(F.Plus(F.C1, F.Power(x, F.CN2))))); // $$);
      MATCHER.caseOf(F.Power(S.E, F.ArcSech(x_)), //
          // [$ (1/x+Sqrt(1/x-1)*Sqrt(1/x+1)) $]
          F.Plus(F.Power(x, F.CN1), F.Times(F.Sqrt(F.Plus(F.CN1, F.Power(x, F.CN1))),
              F.Sqrt(F.Plus(F.Power(x, F.CN1), F.C1))))); // $$);
      MATCHER.caseOf(F.Power(S.E, F.ArcCoth(x_)), //
          // [$ (1/Sqrt((x-1)/(x+1))) $]
          F.Power(F.Times(F.Power(F.Plus(x, F.C1), F.CN1), F.Plus(F.CN1, x)), F.CN1D2)); // $$);

      // Log
      MATCHER.caseOf(F.Log(F.Times(m_, n_)), //
          // [$ (Log(m)+Log(n)) /; Positive(m)
          // $]
          F.Condition(F.Plus(F.Log(m), F.Log(n)), F.Positive(m))); // $$);

      // Log(x^(y_?( RealValuedNumberQ(#) && (x>-1) && (#<1) )& ))
      MATCHER.caseOf(
          F.Log(F.Power(x_,
              F.PatternTest(y_,
                  (F.Function(F.And(F.RealValuedNumberQ(F.Slot1), F.Greater(F.Slot1, F.CN1),
                      F.Less(F.Slot1, F.C1))))))), //
          // [$ (y * Log(x))
          // $]
          F.Times(y, F.Log(x))); // $$);

      MATCHER.caseOf(S.BartlettWindow.of(x_), WindowFunctions.bartlettWindow(x));
      MATCHER.caseOf(S.BlackmanHarrisWindow.of(x_), WindowFunctions.blackmanHarrisWindow(x));
      MATCHER.caseOf(S.BlackmanNuttallWindow.of(x_), WindowFunctions.blackmanNuttallWindow(x));
      MATCHER.caseOf(S.BlackmanWindow.of(x_), WindowFunctions.blackmanWindow(x));
      MATCHER.caseOf(S.DirichletWindow.of(x_), WindowFunctions.dirichletWindow(x));
      MATCHER.caseOf(S.HannWindow.of(x_), WindowFunctions.hannWindow(x));
      MATCHER.caseOf(S.FlatTopWindow.of(x_), WindowFunctions.flatTopWindow(x));
      MATCHER.caseOf(S.GaussianWindow.of(x_), WindowFunctions.gaussianWindow(x));
      MATCHER.caseOf(S.HammingWindow.of(x_), WindowFunctions.hammingWindow(x));
      MATCHER.caseOf(S.NuttallWindow.of(x_), WindowFunctions.nuttallWindow(x));
      MATCHER.caseOf(S.ParzenWindow.of(x_), WindowFunctions.parzenWindow(x));
      MATCHER.caseOf(S.TukeyWindow.of(x_), WindowFunctions.tukeyWindow(x));

      // IAST list = (IAST) WL.deserializeResource("/rules/FunctionExpandRules.bin", true);
      IAST list = FunctionExpandRules.RULES;

      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST(S.SetDelayed, 3)) {
          MATCHER.caseOf(arg.first(), arg.second());
        } else if (arg.isAST(S.Set, 3)) {
          MATCHER.caseOf(arg.first(), arg.second());
        }
      }
      return MATCHER;
    }
  }

  public FunctionExpand() {}

  private static IExpr beforeRules(IAST ast) {
    if (ast.isAST1() && ast.first().isTimes2()) {
      IAST times = (IAST) ast.first();
      return trigTrivial(times, ast);
    }
    return callFunctionExpand(ast, EvalEngine.get());
  }

  /**
   * See: <a href=
   * "https://en.wikipedia.org/wiki/Trigonometric_constants_expressed_in_real_radicals#The_trivial_values">Trigonometric_constants_expressed_in_real_radicals#The_trivial_values</a>
   *
   * @param times2ArgsAST
   * @param ast
   */
  public static IAST trigTrivial(IAST times2ArgsAST, IAST ast) {
    IExpr timesArg1 = times2ArgsAST.first();
    if (timesArg1.isFraction()) {
      if (timesArg1.equals(F.C1D2) && times2ArgsAST.second().isAST1()) {
        IExpr head = ast.head();
        if (head.isSymbol()) {
          int headID = ((ISymbol) head).ordinal();

          IExpr x = times2ArgsAST.second().first();
          IExpr subHead = times2ArgsAST.second().head();
          if (subHead.isSymbol()) {
            int subHeadID = ((ISymbol) subHead).ordinal();
            if (subHeadID > 0) {
              switch (headID) {
                case ID.Cos:
                  return cosC1D2ArcFunction(x, subHeadID);
                case ID.Cot:
                  return cotC1D2ArcFunction(x, subHeadID);
                case ID.Csc:
                  return cscC1D2ArcFunction(x, subHeadID);
                case ID.Sec:
                  return secC1D2ArcFunction(x, subHeadID);
                case ID.Sin:
                  return sinC1D2ArcFunction(x, subHeadID);
                case ID.Tan:
                  return tanC1D2ArcFunction(x, subHeadID);
              }
            }
          }
        }
      }
      if (times2ArgsAST.second().isPi()) {
        IFraction fraction = (IFraction) timesArg1;
        if (fraction.numerator().isOne()) {
          IAST factors = fraction.denominator().factorInteger();
          if (factors.size() == 2) {
            IInteger base = (IInteger) factors.arg1().first();
            if (base.equalsInt(2)) {
              int exponent = factors.arg1().second().toIntDefault();
              if (exponent > 3) {
                EvalEngine engine = EvalEngine.get();
                if (ast.isCos()) {
                  return F.Times(F.C1D2,
                      Programming.nest(F.Sqrt(F.C2), F.Function(F.Sqrt(F.Plus(F.C2, F.Slot1))), //
                          exponent - 2, engine));
                } else if (ast.isSin()) {
                  return F.Times(F.C1D2,
                      F.Sqrt(F.Subtract(F.C2,
                          Programming.nest(F.Sqrt(F.C2), F.Function(F.Sqrt(F.Plus(F.C2, F.Slot1))), //
                              exponent - 3, engine))));
                }
              }
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Expand <code>Cos( (1/2) * Arc...(x) )</code>.
   * 
   * @param x
   * @param subHeadID
   * @return
   */
  private static IAST cosC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(1+x)/Sqrt(2)
        return F.Times(F.C1DSqrt2, F.Sqrt(F.Plus(F.C1, x)));
      }
      case ID.ArcCot: {
        // Sqrt(1+(Sqrt(-x)*Sqrt(x))/Sqrt(-1-x^2))/Sqrt(2)
        return F.Times(F.C1DSqrt2, F.Sqrt(F.Plus(F.C1, F.Times(F.Sqrt(F.Negate(x)), F.Sqrt(x),
            F.Power(F.Subtract(F.CN1, F.Sqr(x)), F.CN1D2)))));
      }
      case ID.ArcCsc: {
        // Sqrt(1+Sqrt((-1+x)*(1+x))/Sqrt(x^2))/Sqrt(2)
        return F.Times(F.C1DSqrt2,
            F.Sqrt(F.Plus(F.C1, F.Times(F.Sqrt(F.Times(F.Plus(F.CN1, x), F.Plus(F.C1, x))),
                F.Power(F.Sqr(x), F.CN1D2)))));
      }
      case ID.ArcSec: {
        // Sqrt(-1-x)/(Sqrt(2)*Sqrt(-x))
        IExpr v1 = F.Negate(x);
        return F.Times(F.C1DSqrt2, F.Sqrt(F.Plus(F.CN1, v1)), F.Power(v1, F.CN1D2));
      }
      case ID.ArcSin: {
        // Sqrt(1+Sqrt(1-x)*Sqrt(1+x))/Sqrt(2)
        return F.Times(F.C1DSqrt2,
            F.Sqrt(F.Plus(F.C1, F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x))))));
      }
      case ID.ArcTan: {
        // Sqrt(1+1/Sqrt(1+x^2))/Sqrt(2)
        return F.Times(F.C1DSqrt2, F.Sqrt(F.Plus(F.C1, F.Power(F.Plus(F.C1, F.Sqr(x)), F.CN1D2))));
      }
      default:
    }
    return F.NIL;
  }

  private static IAST cotC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(1+x)/Sqrt(1-x)
        return F.Times(F.Power(F.Subtract(F.C1, x), F.CN1D2), F.Sqrt(F.Plus(F.C1, x)));
      }
      case ID.ArcCot: {
        // (Sqrt(x)*(Sqrt(-x)*Sqrt(x)+Sqrt(-1-x^2)))/Sqrt(-x)
        IExpr v1 = F.Sqrt(x);
        IExpr v2 = F.Negate(x);
        return F.Times(v1, F.Power(v2, F.CN1D2),
            F.Plus(F.Times(v1, F.Sqrt(v2)), F.Sqrt(F.Subtract(F.CN1, F.Sqr(x)))));
      }
      case ID.ArcCsc: {
        // x*Sqrt(1+Sqrt((-1+x)*(1+x))/Sqrt(x^2))*Sqrt((x^2+Sqrt(x^2)*Sqrt(-1+x^2))/x^2)
        IExpr v1 = F.Sqr(x);
        return F.Times(
            F.Sqrt(F.Times(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, v1)), F.Sqrt(v1)), v1),
                F.Power(x, F.CN2))),
            x, F.Sqrt(F.Plus(F.C1, F.Times(F.Power(v1, F.CN1D2),
                F.Sqrt(F.Times(F.Plus(F.CN1, x), F.Plus(F.C1, x)))))));
      }
      case ID.ArcSec: {
        // (Sqrt(-1-x)*Sqrt(x))/(Sqrt(-1+x)*Sqrt(-x))
        IExpr v1 = F.Negate(x);
        return F.Times(F.Sqrt(F.Plus(F.CN1, v1)), F.Power(v1, F.CN1D2),
            F.Power(F.Plus(F.CN1, x), F.CN1D2), F.Sqrt(x));
      }
      case ID.ArcSin: {
        // (1+Sqrt(1-x)*Sqrt(1+x))/x
        return F.Times(F.Power(x, F.CN1),
            F.Plus(F.C1, F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x)))));
      }
      case ID.ArcTan: {
        // (1+Sqrt(1+x^2))/x
        return F.Times(F.Power(x, F.CN1), F.Plus(F.C1, F.Sqrt(F.Plus(F.C1, F.Sqr(x)))));
      }
      default:
    }
    return F.NIL;
  }

  private static IAST cscC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(2)/Sqrt(1-x)
        return F.Times(F.CSqrt2, F.Power(F.Subtract(F.C1, x), F.CN1D2));
      }
      case ID.ArcCot: {
        // (Sqrt(2)*Sqrt(x)*Sqrt(-1-x^2)*Sqrt(1+(Sqrt(-x)*Sqrt(x))/Sqrt(-1-x^2)))/Sqrt(-x)
        IExpr v1 = F.Sqrt(x);
        IExpr v2 = F.Negate(x);
        IExpr v3 = F.Subtract(F.CN1, F.Sqr(x));
        return F.Times(F.CSqrt2, v1, F.Power(v2, F.CN1D2),
            F.Sqrt(F.Plus(F.C1, F.Times(v1, F.Sqrt(v2), F.Power(v3, F.CN1D2)))), F.Sqrt(v3));
      }
      case ID.ArcCsc: {
        // Sqrt(2)*x*Sqrt((x^2+Sqrt(x^2)*Sqrt(-1+x^2))/x^2)
        IExpr v1 = F.Sqr(x);
        return F.Times(F.CSqrt2, F.Sqrt(
            F.Times(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, v1)), F.Sqrt(v1)), v1), F.Power(x, F.CN2))),
            x);
      }
      case ID.ArcSec: {
        // (Sqrt(2)*Sqrt(x))/Sqrt(-1+x)
        return F.Times(F.CSqrt2, F.Power(F.Plus(F.CN1, x), F.CN1D2), F.Sqrt(x));
      }
      case ID.ArcSin: {
        // (Sqrt(2)*Sqrt(1+Sqrt(1-x)*Sqrt(1+x)))/x
        return F.Times(F.CSqrt2, F.Power(x, F.CN1),
            F.Sqrt(F.Plus(F.C1, F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x))))));
      }
      case ID.ArcTan: {
        // (Sqrt(2)*Sqrt(1+x^2)*Sqrt(1+1/Sqrt(1+x^2)))/x
        IExpr v1 = F.Plus(F.C1, F.Sqr(x));
        return F.Times(F.CSqrt2, F.Sqrt(F.Plus(F.C1, F.Power(v1, F.CN1D2))), F.Sqrt(v1),
            F.Power(x, F.CN1));
      }
      default:
    }
    return F.NIL;
  }

  private static IAST secC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(2)/Sqrt(1+x)
        return F.Times(F.CSqrt2, F.Power(F.Plus(F.C1, x), F.CN1D2));
      }
      case ID.ArcCot: {
        // Sqrt(2)/Sqrt(1+(Sqrt(-x)*Sqrt(x))/Sqrt(-1-x^2))
        return F.Times(F.CSqrt2, F.Power(F.Plus(F.C1,
            F.Times(F.Sqrt(F.Negate(x)), F.Sqrt(x), F.Power(F.Subtract(F.CN1, F.Sqr(x)), F.CN1D2))),
            F.CN1D2));
      }
      case ID.ArcCsc: {
        // Sqrt(2)/Sqrt(1+Sqrt((-1+x)*(1+x))/Sqrt(x^2))
        return F.Times(F.CSqrt2,
            F.Power(F.Plus(F.C1, F.Times(F.Sqrt(F.Times(F.Plus(F.CN1, x), F.Plus(F.C1, x))),
                F.Power(F.Sqr(x), F.CN1D2))), F.CN1D2));
      }
      case ID.ArcSec: {
        // (Sqrt(2)*Sqrt(-x))/Sqrt(-1-x)
        IExpr v1 = F.Negate(x);
        return F.Times(F.CSqrt2, F.Power(F.Plus(F.CN1, v1), F.CN1D2), F.Sqrt(v1));
      }
      case ID.ArcSin: {
        // Sqrt(2)/Sqrt(1+Sqrt(1-x)*Sqrt(1+x))
        return F.Times(F.CSqrt2, F.Power(
            F.Plus(F.C1, F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x)))), F.CN1D2));
      }
      case ID.ArcTan: {
        // Sqrt(2)/Sqrt(1+1/Sqrt(1+x^2))
        return F.Times(F.CSqrt2,
            F.Power(F.Plus(F.C1, F.Power(F.Plus(F.C1, F.Sqr(x)), F.CN1D2)), F.CN1D2));
      }
      default:
    }
    return F.NIL;
  }

  /**
   * Expand <code>Sin( (1/2) * Arc...(x) )</code>.
   * 
   * @param x
   * @param subHeadID
   * @return
   */
  private static IAST sinC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(1-x)/Sqrt(2)
        return F.Times(F.C1DSqrt2, F.Sqrt(F.Subtract(F.C1, x)));
      }
      case ID.ArcCot: {
        // Sqrt(-x)/(Sqrt(2)*Sqrt(x)*Sqrt(-1-x^2)*Sqrt(1+(Sqrt(-x)*Sqrt(x))/Sqrt(-1-x^2)))
        IExpr v1 = F.Sqrt(F.Negate(x));
        IExpr v2 = F.Sqrt(x);
        IExpr v3 = F.Subtract(F.CN1, F.Sqr(x));
        return F.Times(F.C1DSqrt2, v1, F.Power(v2, F.CN1),
            F.Power(F.Plus(F.C1, F.Times(v1, v2, F.Power(v3, F.CN1D2))), F.CN1D2),
            F.Power(v3, F.CN1D2));
      }
      case ID.ArcCsc: {
        // 1/(Sqrt(2)*x*Sqrt((x^2+Sqrt(x^2)*Sqrt(-1+x^2))/x^2))
        IExpr v1 = F.Sqr(x);
        return F.Times(F.C1DSqrt2, F.Power(
            F.Times(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, v1)), F.Sqrt(v1)), v1), F.Power(x, F.CN2)),
            F.CN1D2), F.Power(x, F.CN1));
      }
      case ID.ArcSec: {
        // Sqrt(-1+x)/(Sqrt(2)*Sqrt(x))
        return F.Times(F.Power(F.Times(F.CSqrt2, F.Sqrt(x)), F.CN1), F.Sqrt(F.Plus(F.CN1, x)));
      }
      case ID.ArcSin: {
        // x/(Sqrt(2)*Sqrt(1+Sqrt(1-x)*Sqrt(1+x)))
        return F
            .Times(x,
                F.Power(
                    F.Times(F.CSqrt2,
                        F.Sqrt(F.Plus(F.C1,
                            F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x)))))),
                    F.CN1));

      }
      case ID.ArcTan: {
        // x/(Sqrt(2)*Sqrt(1+x^2)*Sqrt(1+1/Sqrt(1+x^2)))
        IExpr v1 = F.Plus(F.C1, F.Sqr(x));
        return F.Times(F.C1DSqrt2, F.Power(F.Plus(F.C1, F.Power(v1, F.CN1D2)), F.CN1D2),
            F.Power(v1, F.CN1D2), x);
      }
      default:
    }
    return F.NIL;
  }

  /**
   * Expand <code>Tan( (1/2) * Arc...(x) )</code>.
   * 
   * @param x
   * @param subHeadID
   * @return
   */
  private static IAST tanC1D2ArcFunction(IExpr x, int subHeadID) {
    switch (subHeadID) {
      case ID.ArcCos: {
        // Sqrt(1-x)/Sqrt(1+x)
        return F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Power(F.Plus(F.C1, x), F.CN1D2));
      }
      case ID.ArcCot: {
        // Sqrt(-x)/(Sqrt(x)*(Sqrt(-x)*Sqrt(x)+Sqrt(-1-x^2)))
        IExpr v1 = F.Sqrt(F.Negate(x));
        IExpr v2 = F.Sqrt(x);
        return F.Times(v1, F.Power(v2, F.CN1),
            F.Power(F.Plus(F.Times(v1, v2), F.Sqrt(F.Subtract(F.CN1, F.Sqr(x)))), F.CN1));
      }
      case ID.ArcCsc: {
        // 1/(x*Sqrt(1+Sqrt((-1+x)*(1+x))/Sqrt(x^2))*Sqrt((x^2+Sqrt(x^2)*Sqrt(-1+x^2))/x^2))
        IExpr v1 = F.Sqr(x);
        return F.Times(
            F.Power(F.Times(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, v1)), F.Sqrt(v1)), v1),
                F.Power(x, F.CN2)), F.CN1D2),
            F.Power(x, F.CN1),
            F.Power(F.Plus(F.C1,
                F.Times(F.Power(v1, F.CN1D2), F.Sqrt(F.Times(F.Plus(F.CN1, x), F.Plus(F.C1, x))))),
                F.CN1D2));
      }
      case ID.ArcSec: {
        // (Sqrt(-1+x)*Sqrt(-x))/(Sqrt(-1-x)*Sqrt(x))
        IExpr v1 = F.Negate(x);
        return F.Times(F.Power(F.Plus(F.CN1, v1), F.CN1D2), F.Sqrt(v1), F.Sqrt(F.Plus(F.CN1, x)),
            F.Power(x, F.CN1D2));
      }
      case ID.ArcSin: {
        // x/(1+Sqrt(1-x)*Sqrt(1+x))
        return F.Times(x, F.Power(
            F.Plus(F.C1, F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x)))), F.CN1));
      }
      case ID.ArcTan: {
        // x/(1+Sqrt(1+x^2))
        return F.Times(x, F.Power(F.Plus(F.C1, F.Sqrt(F.Plus(F.C1, F.Sqr(x)))), F.CN1));
      }
      default:
    }
    return F.NIL;
  }

  private static Matcher getMatcher() {
    return LAZY_MATCHER.get();
  }

  private static IExpr callFunctionExpand(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof IBuiltInSymbol) {
      IEvaluator evaluator = ((IBuiltInSymbol) ast.head()).getEvaluator();
      if (evaluator instanceof IFunctionExpand) {
        IExpr temp = ((IFunctionExpand) evaluator).functionExpand(ast, engine);
        if (temp.isPresent()) {
          IExpr result = engine.evaluate(temp);
          if (result.isAST()) {
            IASTMutable newAST = ((IAST) result).copy();
            for (int i = 1; i < newAST.size(); i++) {
              IExpr arg = newAST.get(i);
              if (arg.isAST()) {
                newAST.set(i, callFunctionExpand((IAST) arg, engine).orElse(arg));
              }
            }
            return newAST;
          }
          return result;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr result = engine.getCache(ast);
    if (result != null) {
      return result;
    }
    IExpr arg1 = ast.arg1();
    IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
    if (tempAST.isPresent()) {
      return tempAST;
    }

    IExpr assumptionExpr = F.NIL;
    if (ast.size() > 2) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isRule()) {
        assumptionExpr = arg2;
      }
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      assumptionExpr = options.getOption(S.Assumptions).orElse(assumptionExpr);
    }
    if (assumptionExpr.isPresent()) {
      if (assumptionExpr.isAST()) {
        IAssumptions oldAssumptions = engine.getAssumptions();
        IAssumptions assumptions;
        if (oldAssumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        } else {
          assumptions = oldAssumptions.copy();
          assumptions = assumptions.addAssumption(assumptionExpr);
        }
        if (assumptions != null) {
          try {
            engine.setAssumptions(assumptions);
            return callMatcher(ast, arg1, engine);
          } finally {
            engine.setAssumptions(oldAssumptions);
          }
        }
      }
    }
    // don't call PowerExpand
    return callMatcher(ast, arg1, engine);
  }

  /**
   * @param ast
   * @param arg1
   * @return {@link F#NIL} if no match was found
   */
  public static IExpr callMatcher(final IAST ast, IExpr arg1, EvalEngine engine) {
    IExpr temp = getMatcher().replaceAll(arg1, FunctionExpand::beforeRules).orElse(arg1);
    engine.putCache(ast, temp);
    return temp;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // Initializer.init();
    LAZY_MATCHER = Suppliers.memoize(Initializer::init);
  }
}
