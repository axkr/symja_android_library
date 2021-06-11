package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Beta;
import static org.matheclipse.core.expression.F.BetaRegularized;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.ChebyshevT;
import static org.matheclipse.core.expression.F.ChebyshevU;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import static org.matheclipse.core.expression.S.z;

import java.util.function.Supplier;

import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.QuarticSolver;
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
 * <p>expands the special function <code>f</code>.
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
public class FunctionExpand extends AbstractEvaluator implements FunctionExpandRules {

  private static Supplier<Matcher> LAZY_MATCHER;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static Matcher init() {
      Matcher MATCHER = new Matcher();
      // Beta
      MATCHER.caseOf(
          Beta(z_, a_, b_), //
          // [$ Beta(a, b)*(1 - (1 - z)^b*Sum((Pochhammer(b, k)*z^k)/k!, {k, 0, a - 1})) /;
          // IntegerQ(a)&&a>0
          // $]
          F.Condition(
              F.Times(
                  F.Beta(a, b),
                  F.Plus(
                      F.C1,
                      F.Times(
                          F.CN1,
                          F.Power(F.Subtract(F.C1, z), b),
                          F.Sum(
                              F.Times(
                                  F.Power(z, k),
                                  F.Power(F.Factorial(k), F.CN1),
                                  F.Pochhammer(b, k)),
                              F.List(k, F.C0, F.Plus(F.CN1, a)))))),
              F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

      MATCHER.caseOf(
          Beta(a_, b_), //
          // [$ Factorial(a-1)*Product((k+b)^(-1),{k,0,a-1}) /; IntegerQ(a)&&a>0 $]
          F.Condition(
              F.Times(
                  F.Factorial(F.Plus(F.CN1, a)),
                  F.Product(F.Power(F.Plus(k, b), F.CN1), F.List(k, F.C0, F.Plus(F.CN1, a)))),
              F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

      MATCHER.caseOf(
          BetaRegularized(z_, a_, b_), //
          // [$ (Beta(z, a, b)*Gamma(a + b))/(Gamma(a)*Gamma(b)) $]
          F.Times(
              F.Beta(z, a, b),
              F.Power(F.Times(F.Gamma(a), F.Gamma(b)), F.CN1),
              F.Gamma(F.Plus(a, b)))); // $$);

      MATCHER.caseOf(
          Binomial(a_, b_), //
          // [$ If(IntegerQ(b)&&Positive(b),Product(a-c,{c,0,b-1})/b!, If(IntegerQ(a)&&Positive(a),
          // (a!*Sin(b*Pi))/(Product(b-c,{c,0,a})*Pi), Gamma(1 + a)/(Gamma(1 + b)*Gamma(1 - b + a)))
          // ) $]
          F.If(
              F.And(F.IntegerQ(b), F.Positive(b)),
              F.Times(
                  F.Power(F.Factorial(b), F.CN1),
                  F.Product(F.Subtract(a, c), F.List(c, F.C0, F.Plus(F.CN1, b)))),
              F.If(
                  F.And(F.IntegerQ(a), F.Positive(a)),
                  F.Times(
                      F.Power(
                          F.Times(F.Product(F.Subtract(b, c), F.List(c, F.C0, a)), S.Pi), F.CN1),
                      F.Factorial(a),
                      F.Sin(F.Times(b, S.Pi))),
                  F.Times(
                      F.Gamma(F.Plus(F.C1, a)),
                      F.Power(
                          F.Times(F.Gamma(F.Plus(F.C1, b)), F.Gamma(F.Plus(F.C1, F.Negate(b), a))),
                          F.CN1))))); // $$);

      // CatalanNumber
      MATCHER.caseOf(
          F.CatalanNumber(n_), //
          // [$ (2^(2*n)*Gamma(1/2 + n))/(Sqrt(Pi)*Gamma(2 + n)) $]
          F.Times(
              F.Power(F.C2, F.Times(F.C2, n)),
              F.Gamma(F.Plus(F.C1D2, n)),
              F.Power(F.Times(F.Sqrt(S.Pi), F.Gamma(F.Plus(F.C2, n))), F.CN1))); // $$);
      // ChebyshevT
      MATCHER.caseOf(
          ChebyshevT(n_, x_), //
          // [$ Cos(n*ArcCos(x)) $]
          F.Cos(F.Times(n, F.ArcCos(x)))); // $$);
      // ChebyshevU
      MATCHER.caseOf(
          ChebyshevU(n_, x_), //
          // [$ Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)) $]
          F.Times(
              F.Power(F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x))), F.CN1),
              F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(x))))); // $$);

      // Cos
      MATCHER.caseOf(
          F.Cos(F.Sqrt(F.Sqr(x_))), //
          F.Cos(x));
      // Sin
      MATCHER.caseOf(
          F.Sin(F.Sqrt(F.Sqr(x_))), //
          // [$ (Sqrt(x^2)*Sin(x))/x $]
          F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.Sin(x))); // $$);

      // CosIntegral
      MATCHER.caseOf(
          F.CosIntegral(F.Times(F.CN1, x_)), //
          // [$ CosIntegral(x) + Log(x) - Log(x)
          // $]
          F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(x))); // $$);
      MATCHER.caseOf(
          F.CosIntegral(F.Times(F.CI, x_)), //
          // [$ CoshIntegral(x) - Log(x) + Log(I*x)
          // $]
          F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CI, x)))); // $$);
      MATCHER.caseOf(
          F.CosIntegral(F.Times(F.CNI, x_)), //
          // [$ CoshIntegral(x) - Log(x) + Log(-I*x)
          // $]
          F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CNI, x)))); // $$);
      MATCHER.caseOf(
          F.CosIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
          // [$ CosIntegral(x) + Log(Sqrt(x^2)) - Log(x)
          // $]
          F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(F.Sqrt(F.Sqr(x))))); // $$);

      // SinIntegral
      MATCHER.caseOf(
          F.SinIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
          // [$ (Sqrt(x^2)/x) * SinIntegral(x)
          // $]
          F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.SinIntegral(x))); // $$);

      // Factorial
      MATCHER.caseOf(
          Factorial(x_), //
          // [$ Gamma(1+x) $]
          F.Gamma(F.Plus(F.C1, x))); // $$);

      // Haversine
      MATCHER.caseOf(
          F.Haversine(x_), //
          // [$ (1/2) * (1 - Cos(x)) $]
          F.Times(F.C1D2, F.Subtract(F.C1, F.Cos(x)))); // $$);
      // InverseHaversine
      MATCHER.caseOf(
          F.InverseHaversine(x_), //
          // [$ 2*ArcSin( Sqrt(x) ) $]
          F.Times(F.C2, F.ArcSin(F.Sqrt(x)))); // $$);

      // Pochhammer
      MATCHER.caseOf(
          F.Pochhammer(x_, y_), //
          // [$ Gamma(x+y)/Gamma(x) $]
          F.Times(F.Power(F.Gamma(x), F.CN1), F.Gamma(F.Plus(x, y)))); // $$);
      // PolyGamma
      MATCHER.caseOf(
          F.PolyGamma(F.CN2, F.C1), //
          // [$ (1/2)*(Log(2)+Log(Pi)) $]
          F.Times(F.C1D2, F.Plus(F.Log(F.C2), F.Log(S.Pi)))); // $$);
      MATCHER.caseOf(
          F.PolyGamma(F.CN3, F.C1), //
          // [$ Log(Glaisher) + (1/4)*(Log(2) + Log(Pi)) $]
          F.Plus(F.Log(S.Glaisher), F.Times(F.C1D4, F.Plus(F.Log(F.C2), F.Log(S.Pi))))); // $$);

      MATCHER.caseOf(
          S.Degree, //
          // [$ Pi/180 $]
          F.Times(F.QQ(1L, 180L), S.Pi)); // $$);
      MATCHER.caseOf(
          S.GoldenAngle, //
          // [$ (3-Sqrt(5))*Pi $]
          F.Times(F.Subtract(F.C3, F.CSqrt5), S.Pi)); // $$);
      MATCHER.caseOf(
          S.GoldenRatio, //
          // [$ 1/2*(1+Sqrt(5)) $]
          F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))); // $$);

      // Power
      MATCHER.caseOf(
          F.Power(S.E, F.ArcSinh(x_)), //
          // [$ (x+Sqrt(1+x^2)) $]
          F.Plus(x, F.Sqrt(F.Plus(F.C1, F.Sqr(x))))); // $$);
      MATCHER.caseOf(
          F.Power(S.E, F.ArcCosh(x_)), //
          // [$ (x+Sqrt(x-1)*Sqrt(x+1)) $]
          F.Plus(x, F.Times(F.Sqrt(F.Plus(F.CN1, x)), F.Sqrt(F.Plus(x, F.C1))))); // $$);
      MATCHER.caseOf(
          F.Power(S.E, F.ArcTanh(x_)), //
          // [$ ((x+1)/Sqrt(1-x^2)) $]
          F.Times(F.Plus(x, F.C1), F.Power(F.Subtract(F.C1, F.Sqr(x)), F.CN1D2))); // $$);
      MATCHER.caseOf(
          F.Power(S.E, F.ArcCsch(x_)), //
          // [$ (1/x+Sqrt(1+1/x^2)) $]
          F.Plus(F.Power(x, F.CN1), F.Sqrt(F.Plus(F.C1, F.Power(x, F.CN2))))); // $$);
      MATCHER.caseOf(
          F.Power(S.E, F.ArcSech(x_)), //
          // [$ (1/x+Sqrt(1/x-1)*Sqrt(1/x+1)) $]
          F.Plus(
              F.Power(x, F.CN1),
              F.Times(
                  F.Sqrt(F.Plus(F.CN1, F.Power(x, F.CN1))),
                  F.Sqrt(F.Plus(F.Power(x, F.CN1), F.C1))))); // $$);
      MATCHER.caseOf(
          F.Power(S.E, F.ArcCoth(x_)), //
          // [$ (1/Sqrt((x-1)/(x+1))) $]
          F.Power(F.Times(F.Power(F.Plus(x, F.C1), F.CN1), F.Plus(F.CN1, x)), F.CN1D2)); // $$);

      // Log
      MATCHER.caseOf(
          F.Log(F.Times(m_, n_)), //
          // [$ (Log(m)+Log(n)) /; Positive(m)
          // $]
          F.Condition(F.Plus(F.Log(m), F.Log(n)), F.Positive(m))); // $$);

      // Log(x^(y_?( RealNumberQ(#) && (x>-1) && (#<1) )& ))
      MATCHER.caseOf(
          F.Log(
              F.Power(
                  x_,
                  F.PatternTest(
                      y_,
                      (F.Function(
                          F.And(
                              F.RealNumberQ(F.Slot1),
                              F.Greater(F.Slot1, F.CN1),
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

      //      IAST list = (IAST) WL.deserializeResource("/rules/FunctionExpandRules.bin", true);
      IAST list = RULES;

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
    if (ast.isSqrt() && ast.base().isAST(S.Plus, 3)) {
      IAST plus = (IAST) ast.base();
      final IExpr arg1 = plus.arg1();
      final IExpr arg2 = plus.arg2();
      if (arg1.isRational()) {
        return sqrtDenest((IRational) arg1, arg2);
      }
    } else if ((ast.isCos() || ast.isSin()) && ast.first().isTimes2()) {
      IAST times = (IAST) ast.first();
      return cosSinTrivial(times, ast);
    }
    return F.NIL;
  }

  /**
   * See: <a
   * href="https://en.wikipedia.org/wiki/Trigonometric_constants_expressed_in_real_radicals#The_trivial_values">Trigonometric_constants_expressed_in_real_radicals#The_trivial_values</a>
   *
   * @param timesAST
   * @param ast
   */
  public static IAST cosSinTrivial(IAST timesAST, IAST ast) {
    if (timesAST.second().isPi() && timesAST.first().isFraction()) {
      IFraction fraction = (IFraction) timesAST.first();
      if (fraction.numerator().isOne()) {
        IAST factors = fraction.denominator().factorInteger();
        if (factors.size() == 2) {
          IInteger base = (IInteger) factors.arg1().first();
          if (base.equalsInt(2)) {
            int exponent = factors.arg1().second().toIntDefault();
            if (exponent > 3) {
              if (ast.isCos()) {
                return F.Times(
                    F.C1D2,
                    F.Sqrt(F.C2)
                        .nest(
                            F.Function(F.Sqrt(F.Plus(F.C2, F.Slot1))), //
                            exponent - 2));
              } else if (ast.isSin()) {
                return F.Times(
                    F.C1D2,
                    F.Sqrt(
                        F.Subtract(
                            F.C2,
                            F.Sqrt(F.C2)
                                .nest(
                                    F.Function(F.Sqrt(F.Plus(F.C2, F.Slot1))), //
                                    exponent - 3))));
              }
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Denests <code>Sqrt()</code> in an expression that contain other square roots if possible,
   * otherwise returns <code>F.NIL</code>.
   *
   * <pre>
   * Example: sqrt(5 + 2*Sqrt(6))
   *
   *   >> sqrtDenest(5, 2*Sqrt(6))
   *   sqrt(2) + sqrt(3)
   * </pre>
   *
   * <p>See: <a href="//
   * https://en.wikipedia.org/wiki/Nested_radical#Two_nested_square_roots">Wikipedia - Nested
   * radical - Two nested square roots</a> Github #166. References for possible improvements of this
   * method:
   *
   * <pre>
   *
   * .. [1] http://researcher.watson.ibm.com/researcher/files/us-fagin/symb85.pdf
   * .. [2] D. J. Jeffrey and A. D. Rich, 'Symplifying Square Roots of Square Roots
   *        by Denesting' (available at http://www.cybertester.com/data/denest.pdf)
   * </pre>
   *
   * @param arg1
   * @param arg2
   * @return <code>F.NIL</code> if no change could be applied
   */
  public static IExpr sqrtDenest(IRational arg1, IExpr arg2) {
    if (arg1.isNegative()) {
      return sqrtDenest(
              arg1.negate(), //
              arg2.negate())
          .mapExpr(x -> F.Times(F.CI, x));
    } else {
      final EvalEngine engine = EvalEngine.get();
      boolean arg2IsNegative = false;
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
      if (negExpr.isPresent()) {
        arg2 = negExpr;
        arg2IsNegative = true;
      }
      // (arg2/2) ^ 2
      IExpr squared = engine.evaluate(F.Sqr(F.Divide(arg2, F.C2)));
      if (squared.isRealResult()) {
        IAST list = QuarticSolver.quadraticSolve(F.C1, arg1.negate(), squared);
        if (list.isAST2()) {
          IExpr a = engine.evaluate(list.arg1());
          if (a.isRational()) {
            IExpr b = engine.evaluate(list.arg2());
            if (b.isRational()) {
              if (arg2IsNegative) {
                return F.Plus(F.Sqrt(a), F.Negate(F.Sqrt(b)));
              }
              return F.Plus(F.Sqrt(a), F.Sqrt(b));
            }
          }
        }
      }
    }
    return F.NIL;
  }

  private static Matcher getMatcher() {
    return LAZY_MATCHER.get();
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
    if (result != null) {
      return result;
    }
    IExpr arg1 = ast.arg1();
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
          assumptions = assumptions.addAssumption((IAST) assumptionExpr);
        }
        if (assumptions != null) {
          try {
            engine.setAssumptions(assumptions);
            IExpr temp = getMatcher().replaceAll(arg1, FunctionExpand::beforeRules).orElse(arg1);
            F.REMEMBER_AST_CACHE.put(ast, temp);
            return temp;
          } finally {
            engine.setAssumptions(oldAssumptions);
          }
        }
      }
    }
    // don't call PowerExpand
    IExpr temp = getMatcher().replaceAll(arg1, FunctionExpand::beforeRules).orElse(arg1);
    F.REMEMBER_AST_CACHE.put(ast, temp);
    return temp;
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
