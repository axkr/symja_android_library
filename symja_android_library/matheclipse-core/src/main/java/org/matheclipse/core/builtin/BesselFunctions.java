package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.BesselY;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CPiHalf;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import java.math.RoundingMode;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.functions.BesselJS;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.IMatch;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.math.IntMath;

public class BesselFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AngerJ.setEvaluator(new AngerJ());
      S.AiryAi.setEvaluator(new AiryAi());
      S.AiryAiPrime.setEvaluator(new AiryAiPrime());
      S.AiryBi.setEvaluator(new AiryBi());
      S.AiryBiPrime.setEvaluator(new AiryBiPrime());
      S.BesselI.setEvaluator(new BesselI());
      S.BesselJ.setEvaluator(new BesselJ());
      S.BesselJZero.setEvaluator(new BesselJZero());
      S.BesselK.setEvaluator(new BesselK());
      S.BesselY.setEvaluator(new BesselY());
      S.BesselYZero.setEvaluator(new BesselYZero());
      S.HankelH1.setEvaluator(new HankelH1());
      S.HankelH2.setEvaluator(new HankelH2());
      S.SphericalBesselJ.setEvaluator(new SphericalBesselJ());
      S.SphericalBesselY.setEvaluator(new SphericalBesselY());
      S.SphericalHankelH1.setEvaluator(new SphericalHankelH1());
      S.SphericalHankelH2.setEvaluator(new SphericalHankelH2());
      S.WeberE.setEvaluator(new WeberE());
    }
  }

  private static final class AngerJ extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2) {
        IExpr a = ast.arg1();
        IExpr b = ast.arg2();
        if (ast.isAST3()) {
          IExpr c = ast.arg2();
          return functionExpand3(a, b, c);
        }
        if (ast.isAST2()) {
          return functionExpand2(a, b);
        }
      }
      return F.NIL;
    }

    private static IExpr functionExpand2(IExpr a, IExpr b) {
      // (2*Cos(1/2*a*Pi)*HypergeometricPFQ({1},{1-a/2,1+a/2},(-1)*1/4*b^2)*Sin(1/2*a*Pi))/(a*Pi)+(-2*b*Cos(1/2*a*Pi)*HypergeometricPFQ({1},{3/2-a/2,3/2+a/2},(-1)*1/4*b^2)*Sin(1/2*a*Pi))/((-1+a)*(1+a)*Pi)
      IExpr v6 = F.list(F.C1);
      IExpr v5 = F.Times(F.C1D2, a);
      IExpr v4 = F.Times(F.CN1D2, a);
      IExpr v3 = F.Sin(F.Times(F.Pi, v5));
      IExpr v2 = F.Cos(F.Times(F.Pi, v5));
      IExpr v1 = F.Times(F.CN1, F.C1D4, F.Sqr(b));
      return F.Plus(
          F.Times(F.C2, F.Power(a, F.CN1), F.Power(F.Pi, F.CN1), v2, v3,
              F.HypergeometricPFQ(v6, F.list(F.Plus(F.C1, v4), F.Plus(F.C1, v5)), v1)),
          F.Times(F.CN2, F.Power(F.Plus(F.CN1, a), F.CN1), F.Power(F.Plus(F.C1, a), F.CN1), b,
              F.Power(F.Pi, F.CN1), v2, v3, F.HypergeometricPFQ(v6,
                  F.list(F.Plus(F.QQ(3L, 2L), v4), F.Plus(F.QQ(3L, 2L), v5)), v1)));
    }

    private static IExpr functionExpand3(IExpr a, IExpr b, IExpr c) {
      // (Cos(1/2*a*Pi)*Gamma(1+b)*HypergeometricPFQ({1/2+b/2,1+b/2},{1/2,1-a/2+b/2,1+a/2+b/2},(-1)*1/4*c^2))/(Gamma(1-a/2+b/2)*Gamma(1+a/2+b/2))+(c*Gamma(2+b)*HypergeometricPFQ({1+b/2,3/2+b/2},{3/2,3/2-a/2+b/2,3/2+a/2+b/2},(-1)*1/4*c^2)*Sin(1/2*a*Pi))/(2*Gamma(3/2-a/2+b/2)*Gamma(3/2+a/2+b/2))
      IExpr v8 = F.Sqr(c);
      IExpr v7 = F.Times(F.C1D2, b);
      IExpr v6 = F.Times(F.C1D2, a, F.Pi);
      IExpr v5 = F.Plus(F.C1, v7);
      IExpr v4 = F.Plus(F.C1, F.Times(F.CN1D2, a));
      IExpr v3 = F.Plus(F.C1, F.Times(F.C1D2, a), v7);
      IExpr v2 = F.Plus(F.QQ(3L, 2L), F.Times(F.C1D2, a), v7);
      IExpr v1 = F.Plus(F.QQ(3L, 2L), F.Times(F.CN1D2, a), v7);
      return F.Plus(
          F.Times(F.Cos(v6), F.Gamma(F.Plus(F.C1, b)), F.Power(F.Gamma(v3), F.CN1),
              F.Power(F.Gamma(F.Plus(v4, v7)), F.CN1),
              F.HypergeometricPFQ(F.list(v5, F.Plus(F.C1D2, v7)),
                  F.list(F.C1D2, v3, F.Plus(v4, v7)), F.Times(F.CN1D4, v8))),
          F.Times(F.C1D2, c, F.Gamma(F.Plus(F.C2, b)), F.Power(F.Gamma(v1), F.CN1),
              F.Power(F.Gamma(v2), F.CN1), F.HypergeometricPFQ(F.list(v5, F.Plus(F.QQ(3L, 2L), v7)),
                  F.list(F.QQ(3L, 2L), v1, v2), F.Times(F.CN1D4, v8)),
              F.Sin(v6)));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr n = ast.arg1();

      if (ast.isAST2()) {
        // https://dlmf.nist.gov/11.10#vii
        final IExpr z = ast.arg2();
        return angerJ2(n, z, engine.isNumericMode()).eval(engine);
      }
      IExpr m = ast.arg2();
      IExpr z = ast.arg3();
      return angerJ3(n, m, z, engine.isNumericMode()).eval(engine);
    }

    private static IExpr angerJ3(final IExpr n, IExpr m, IExpr z, boolean numericMode) {
      if (numericMode) {
        if (n.isNumber() && m.isNumber() && z.isNumber()) {
          return functionExpand3(n, m, z);
        }
      }
      return F.NIL;
    }

    private static IExpr angerJ2(final IExpr n, final IExpr z, boolean numericMode) {
      if (z.isZero()) {
        // Sinc(n*Pi)
        return F.Sinc(F.Times(n, F.Pi));
      }
      int ni = n.toIntDefault();
      if (ni != Integer.MIN_VALUE) {
        if (ni >= 0) {
          return F.BesselJ(n, z);
        } else {
          if ((ni & 0x1) == 0x1) {
            return F.Negate(F.BesselJ(F.ZZ(-ni), z));
          }
          return F.BesselJ(F.ZZ(-ni), z);
        }
      }

      if (numericMode) {
        if (n.isNumber() && z.isNumber()) {
          return functionExpand2(n, z);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }

  }

  private static final class AiryAi extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isZero()) {
        // 1/(3^(2/3)*Gamma(2/3))
        return F.Divide(F.Power(F.C3, F.CN2D3), F.Gamma(F.C2D3));
      }
      // if (engine.isDoubleMode() && z.isNumber()) {
      // if (!z.isComplexNumeric()) {
      // try {
      // Complex airyAi = BesselJS.airyAi(z.evalf());
      // if (F.isZero(airyAi.getImaginary())) {
      // return F.num(airyAi.getReal());
      // }
      // return F.complexNum(airyAi);
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryAi, rex, engine);
      // }
      // }
      //
      // try {
      // return F.complexNum(BesselJS.airyAi(z.evalfc()));
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryAi, rex, engine);
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ast.arg1().airyAi();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class AiryAiPrime extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isZero()) {
        // -(1/(3^(1/3)*Gamma(1/3)))
        return F.Times(F.CN1, F.Power(F.C3, F.CN1D3), F.Power(F.Gamma(F.C1D3), F.CN1));
      }
      // if (engine.isDoubleMode() && z.isNumber()) {
      // if (!z.isComplexNumeric()) {
      // try {
      // Complex airyAiPrime = BesselJS.airyAiPrime(z.evalf());
      // if (F.isZero(airyAiPrime.getImaginary())) {
      // return F.num(airyAiPrime.getReal());
      // }
      // return F.complexNum(airyAiPrime);
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryAiPrime, rex, engine);
      // }
      // }
      //
      // try {
      // return F.complexNum(BesselJS.airyAiPrime(z.evalfc()));
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryAiPrime, rex, engine);
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ast.arg1().airyAiPrime();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class AiryBi extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isZero()) {
        // 1/(3^(1/6)*Gamma(2/3))
        return F.Divide(F.Power(F.C3, F.CN1D6), F.Gamma(F.C2D3));
      }
      // if (engine.isDoubleMode() && z.isNumber()) {
      //
      // if (!z.isComplexNumeric()) {
      // try {
      // Complex airyBi = BesselJS.airyBi(z.evalf());
      // if (F.isZero(airyBi.getImaginary())) {
      // return F.num(airyBi.getReal());
      // }
      // return F.complexNum(airyBi);
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryBi, rex, engine);
      // }
      // }
      //
      // try {
      // return F.complexNum(BesselJS.airyBi(z.evalfc()));
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryBi, rex, engine);
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ast.arg1().airyBi();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }

  }

  private static final class AiryBiPrime extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isZero()) {
        // 3^(1/6)/Gamma(1/3)
        return F.Divide(F.Power(F.C3, F.C1D6), F.Gamma(F.C1D3));
      }
      // if (engine.isDoubleMode() && z.isNumber()) {
      // if (!z.isComplexNumeric()) {
      // try {
      // Complex airyBiPrime = BesselJS.airyBiPrime(z.evalf());
      // if (F.isZero(airyBiPrime.getImaginary())) {
      // return F.num(airyBiPrime.getReal());
      // }
      // return F.complexNum(airyBiPrime);
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryBiPrime, rex, engine);
      // }
      // }
      //
      // try {
      // return F.complexNum(BesselJS.airyBiPrime(z.evalfc()));
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.AiryBiPrime, rex, engine);
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ast.arg1().airyBiPrime();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>BesselJ(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Bessel function of the first kind.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BesselJ(1, 3.6)
   * 0.0954655
   * </code>
   * </pre>
   */
  private static final class BesselJ extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return BesselJRules.match3(ast, engine);
    }

    /**
     * Precondition <code> n - 1/2 </code> is an integer number.
     *
     * @param n
     * @param z
     * @return
     */
    // private IExpr besselJHalf(IExpr n, IExpr z) {
    // // (1/Sqrt(z))*Sqrt(2/Pi)*(Cos((1/2)*Pi*(n - 1/2) - z)*Sum(((-1)^j*(2*j + Abs(n) + 1/2)! *
    // // (2*z)^(-2*j -
    // // 1))/
    // // ((2*j + 1)! * (-2*j + Abs(n) - 3/2)!), {j, 0, Floor((1/4)*(2*Abs(n) - 3))}) -
    // // Sin((1/2)*Pi*(n - 1/2) -
    // // z)*Sum(((-1)^j*(2*j + Abs(n) - 1/2)!)/ ((2*j)!*(-2*j + Abs(n) - 1/2)!*(2*z)^(2*j)), {j, 0,
    // // Floor((1/4)*(2*Abs(n) - 1))}))
    // ISymbol j = F.Dummy("j");
    // return F
    // .Times(
    // F.CSqrt2, F.Power(S.Pi, F.CN1D2), F.Power(z, F.CN1D2), F
    // .Plus(
    // F.Times(
    // F.Cos(F.Plus(F.Times(F.C1D2, F.Plus(F.CN1D2, n), S.Pi),
    // F.Negate(z))),
    // F.Sum(F.Times(F.Power(F.CN1, j),
    // F.Power(F.Times(F.C2, z), F.Plus(F.CN1, F.Times(F.CN2, j))),
    // F.Factorial(F.Plus(F.Times(F.C2, j), F.Abs(n), F.C1D2)),
    // F.Power(
    // F.Times(F.Factorial(F.Plus(F.Times(F.C2, j), F.C1)),
    // F.Factorial(
    // F.Plus(F.QQ(-3L, 2L), F.Times(F.CN2, j), F.Abs(n)))),
    // -1)),
    // F.list(j, F.C0,
    // F.Floor(
    // F.Times(F.C1D4, F.Plus(F.CN3, F.Times(F.C2, F.Abs(n)))))))),
    // F.Times(F.CN1,
    // F.Sin(F.Plus(F.Times(F.C1D2, F.Plus(F.CN1D2, n), S.Pi),
    // F.Negate(z))),
    // F.Sum(F.Times(F.Power(F.CN1, j),
    // F.Power(F.Times(F.Factorial(F.Times(F.C2, j)),
    // F.Factorial(F.Plus(F.CN1D2, F.Times(F.CN2, j), F.Abs(n))),
    // F.Power(F.Times(F.C2, z), F.Times(F.C2, j))), -1),
    // F.Factorial(F.Plus(F.CN1D2, F.Times(F.C2, j), F.Abs(n)))),
    // F.list(j, F.C0, F.Floor(
    // F.Times(F.C1D4, F.Plus(F.CN1, F.Times(F.C2, F.Abs(n))))))))));
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselJ(n, z, engine.isNumericMode()).eval(engine);
    }

    private static IExpr besselJ(IExpr n, IExpr z, boolean numericMode) {
      final int order = n.toIntDefault();
      if (z.isZero()) {
        if (n.isZero()) {
          // (0, 0)
          return F.C1;
        }
        if (n.isIntegerResult() || order != Integer.MIN_VALUE) {
          return F.C0;
        }

        IExpr a = n.re();
        if (a.isPositive()) {
          // Re(arg1) > 0
          return F.C0;
        } else if (a.isNegative()) {
          // Re(arg1) < 0 && !a.isInteger()
          return F.CComplexInfinity;
        } else if (a.isZero() && !n.isZero()) {
          return S.Indeterminate;
        }
      }
      // if (n.isReal()) {
      // IExpr in = engine.evaluate(((IReal) n).add(F.CN1D2));
      // if (in.isNumIntValue()) {
      // if (z.isInfinity() || z.isNegativeInfinity()) {
      // return F.C0;
      // }
      // return besselJHalf(n, z);
      // }
      // }

      if (n.isInteger() || order != Integer.MIN_VALUE) {
        if (n.isNegative()) {
          // (-n,z) => (-1)^n*BesselJ(n,z)
          return F.Times(F.Power(F.CN1, n), F.BesselJ(n.negate(), z));
        }
      }

      // if (engine.isDoubleMode() && n.isNumber() && z.isNumber()) {
      // try {
      // double nDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // nDouble = n.evalf();
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || zDouble < 0.0) {
      // Complex nc = n.evalfc();
      // Complex zc = z.evalfc();
      // return F.complexNum(BesselJS.besselJ(nc, zc));
      // } else {
      // return F.num(BesselJS.besselJDouble(nDouble, zDouble));
      // }
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.BesselJ, rex, engine);
      // }
      // }
      if (n.isNumber() && z.isNumber()) {
        if (numericMode) {
          try {
            IExpr res = n.besselJ(z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(S.BesselJ, ve);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.BesselJ, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>BesselJZero(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the <code>k</code>th zero of the <code>BesselJ(n,z)</code> function.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BesselJZero(1.3, 3)
   * 10.61381
   * </code>
   * </pre>
   */
  private static final class BesselJZero extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselJZero(n, z, engine.isDoubleMode()).eval(engine);
    }

    private IExpr besselJZero(IExpr n, IExpr z, boolean doubleMode) {
      final int k = z.toIntDefault();

      if (k > 0 && doubleMode) {
        try {
          // numeric mode evaluation
          if (n.isReal()) {
            return F.num(BesselJS.besselJZero(n.evalf(), k));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // org.hipparchus.exception.MathIllegalArgumentException:
          // interval does not bracket a root
          return Errors.printMessage(S.BesselJZero, rex);
        }
      }


      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>BesselI(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * modified Bessel function of the first kind.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BesselI(1, 3.6)
   * 6.79271
   * </code>
   * </pre>
   */
  private static final class BesselI extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return BesselIRules.match3(ast, engine);
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselI(n, z, engine.isNumericMode()).eval(engine);
    }


    private static IExpr besselI(IExpr n, IExpr z, boolean numericMode) {
      // final int order = n.toIntDefault();
      if (z.isZero()) {
        if (n.isZero()) {
          return F.C1;
        }
        if (n.isInteger()) {
          return F.C0;
        }
        IExpr re = n.re();
        if (re.isPositiveResult()) {
          return F.C0;
        }
        if (re.isNegativeResult() && n.isNumber() && !n.isInteger()) {
          return S.ComplexInfinity;
        }
        if (re.isZero() && n.isNumber() && !n.isZero()) {
          return S.Indeterminate;
        }
      }
      if (n.isNumber() && //
          (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI))) {
        return F.C0;
      }
      if (n.isNumber() && z.isNumber()) {
        // if (engine.isDoubleMode()) {
        // try {
        // double nDouble = Double.NaN;
        // double zDouble = Double.NaN;
        // try {
        // nDouble = n.evalf();
        // zDouble = z.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || zDouble < 0.0) {
        // Complex nc = n.evalfc();
        // Complex zc = z.evalfc();
        // return F.complexNum(BesselJS.besselI(nc, zc));
        // } else {
        // return F.num(BesselJS.besselIDouble(nDouble, zDouble));
        // }
        //
        // } catch (RuntimeException rex) {
        // return Errors.printMessage(S.BesselI, rex, engine);
        // }
        // }
        if (numericMode) {
          try {
            IExpr res = n.besselI(z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(S.BesselI, ve);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.BesselI, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>BesselK(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * modified Bessel function of the second kind.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BesselK(1, 3.6)
   * 0.019795
   * </code>
   * </pre>
   */
  private static final class BesselK extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return BesselKRules.match3(ast, engine);
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselK(engine, n, z).eval(engine);
    }


    private static IExpr besselK(EvalEngine engine, IExpr n, IExpr z) {
      // final int order = n.toIntDefault();
      if (z.isZero()) {
        if (n.isZero()) {
          return F.CInfinity;
        }
        IExpr re = n.re();
        if (re.isZero() && n.isNumber() && !n.isZero()) {
          return S.Indeterminate;
        }
        if (re.isNumber() && !re.isZero()) {
          return S.ComplexInfinity;
        }
      }
      if (n.isNumber() && //
          (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI))) {
        return F.C0;
      }
      // if (engine.isDoubleMode() && n.isNumber() && z.isNumber()) {
      // try {
      // double nDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // nDouble = n.evalf();
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || zDouble < 0.0) {
      // Complex nc = n.evalfc();
      // Complex zc = z.evalfc();
      // return F.complexNum(BesselJS.besselK(nc, zc));
      // } else {
      // return F.num(BesselJS.besselKDouble(nDouble, zDouble));
      // }
      //
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.BesselK, rex, engine);
      // }
      // }
      if (n.isNumber() && z.isNumber()) {
        if (engine.isDoubleMode() || engine.isArbitraryMode()) {
          try {
            IExpr res = n.besselK(z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(S.BesselK, ve, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.BesselK, rex, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>BesselY(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Bessel function of the second kind.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BesselY(1, 3.6)
   * 0.415392
   * </code>
   * </pre>
   */
  private static final class BesselY extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return BesselYRules.match3(ast, engine);
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselY(n, z, engine.isNumericMode()).eval(engine);
    }


    private static IExpr besselY(IExpr n, IExpr z, boolean numericMode) {
      // final int order = n.toIntDefault();

      if (z.isZero()) {
        if (n.isZero()) {
          return F.CNInfinity;
        }
        IExpr re = n.re();
        if (re.isZero() && n.isNumber() && !n.isZero()) {
          return S.Indeterminate;
        }
        if (re.isNumber() && !re.isZero()) {
          return S.ComplexInfinity;
        }
      }
      if (n.isNumber() && //
          (z.isInfinity() || z.isNegativeInfinity())) {
        return F.C0;
      }
      // if (engine.isDoubleMode() && n.isNumber() && z.isNumber()) {
      // try {
      // double nDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // nDouble = n.evalf();
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || zDouble < 0.0) {
      // Complex nc = n.evalfc();
      // Complex zc = z.evalfc();
      // return F.complexNum(BesselJS.besselY(nc, zc));
      // } else {
      // return F.num(BesselJS.besselYDouble(nDouble, zDouble));
      // }
      //
      // } catch (RuntimeException rex) {
      // return Errors.printMessage(S.BesselY, rex, engine);
      // }
      // }
      if (n.isNumber() && z.isNumber()) {
        if (numericMode) {
          try {
            IExpr res = n.besselY(z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(S.BesselY, ve);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.BesselY, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class BesselYZero extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return besselYZero(n, z, engine.isDoubleMode()).eval(engine);
    }

    private IExpr besselYZero(IExpr n, IExpr z, boolean doubleMode) {
      final int k = z.toIntDefault();

      if (k > 0 && doubleMode && n.isNumber()) {
        try {
          // numeric mode evaluation
          if (n.isReal()) {
            return F.num(BesselJS.besselYZero(n.evalf(), k));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // org.hipparchus.exception.MathIllegalArgumentException: interval does not bracket a root
          return Errors.printMessage(S.BesselYZero, rex);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class HankelH1 extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      // BesselJ(n,z)+I*BesselY(n,z)
      return F.Plus(F.BesselJ(n, z), F.Times(F.CI, F.BesselY(n, z)));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();

      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (n.isNumber() && z.isNumber()) {
        // if (engine.isDoubleMode()) {
        // try {
        // double nDouble = Double.NaN;
        // double zDouble = Double.NaN;
        // try {
        // nDouble = n.evalf();
        // zDouble = z.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(nDouble) || Double.isNaN(zDouble)) {
        // Complex nc = n.evalfc();
        // Complex zc = z.evalfc();
        // return F.complexNum(BesselJS.hankelH1(nc, zc));
        // } else {
        // Complex hankelH1 = BesselJS.hankelH1(nDouble, zDouble);
        // if (F.isZero(hankelH1.getImaginary())) {
        // return F.num(hankelH1.getReal());
        // }
        // return F.complexNum(hankelH1);
        // }
        //
        // } catch (RuntimeException rex) {
        // return Errors.printMessage(S.HankelH1, rex, engine);
        // }
        // } else if (engine.isArbitraryMode()) {

        // if (n.isNumber() && z.isNumber()) {
        IExpr besselJ = n.besselJ(z);
        IExpr besselY = n.besselY(z);
        if (besselJ.isNumber() && besselY.isNumber()) {
          // BesselJ(n,z)+I*BesselY(n,z)
          return besselJ.plus(F.CI.multiply(besselY));
        }
        // }
        // }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class HankelH2 extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      // BesselJ(n,z) - I*BesselY(n,z)
      return F.Plus(F.BesselJ(n, z), F.Times(F.CNI, F.BesselY(n, z)));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (n.isNumber() && z.isNumber()) {
        // if (engine.isDoubleMode()) {
        // try {
        // double nDouble = Double.NaN;
        // double zDouble = Double.NaN;
        // try {
        // nDouble = n.evalf();
        // zDouble = z.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(nDouble) || Double.isNaN(zDouble)) {
        // Complex nc = n.evalfc();
        // Complex zc = z.evalfc();
        // return F.complexNum(BesselJS.hankelH2(nc, zc));
        // } else {
        // Complex hankelH2 = BesselJS.hankelH2(nDouble, zDouble);
        // if (F.isZero(hankelH2.getImaginary())) {
        // return F.num(hankelH2.getReal());
        // }
        // return F.complexNum(hankelH2);
        // }
        //
        // } catch (RuntimeException rex) {
        // return Errors.printMessage(S.HankelH2, rex, engine);
        // }
        // } else if (engine.isArbitraryMode()) {
        // return F.Plus(F.BesselJ(n, z), F.Times(F.CNI, F.BesselY(n, z)));
        // }
        IExpr besselJ = n.besselJ(z);
        IExpr besselY = n.besselY(z);
        if (besselJ.isNumber() && besselY.isNumber()) {
          // BesselJ(n,z)-I*BesselY(n,z)
          return besselJ.plus(F.CNI.multiply(besselY));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>SphericalBesselJ(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * spherical Bessel function <code>J(n, x)</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href=
   * "https://en.wikipedia.org/wiki/Bessel_function#Spherical_Bessel_functions">Wikipedia - Bessel
   * function - Spherical Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; SphericalBesselJ(2.5,-5)
   * I*0.204488
   * </code>
   * </pre>
   */
  private static final class SphericalBesselJ extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return SphericalBesselJRules.match3(ast, engine);
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      int ni = n.toIntDefault();
      switch (ni) {
        case 0:
          // Sin(z)/z
          return F.Times(F.Power(z, F.CN1), F.Sin(z));
        case 1:
          // -Cos(z)/z+Sin(z)/z^2
          return F.Plus(F.Times(F.CN1, F.Power(z, F.CN1), F.Cos(z)),
              F.Times(F.Power(z, F.CN2), F.Sin(z)));
        case 2:
          // ((-1)*3*Cos(z))/z^2+((3-z^2)*Sin(z))/z^3
          return F.Plus(F.Times(F.CN1, F.C3, F.Power(z, F.CN2), F.Cos(z)),
              F.Times(F.Power(z, F.CN3), F.Subtract(F.C3, F.Sqr(z)), F.Sin(z)));
      }
      // (Sqrt(Pi/2)*BesselJ(1/2*(1+2*n),z))/Sqrt(z)
      return F.Times(F.Sqrt(F.CPiHalf), F.Power(z, F.CN1D2),
          F.BesselJ(F.Times(F.C1D2, F.Plus(F.C1, F.Times(F.C2, n))), z));

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (engine.isDoubleMode() && n.isNumber() && z.isNumber()) {
        try {
          double nDouble = Double.NaN;
          double zDouble = Double.NaN;
          try {
            nDouble = n.evalf();
            zDouble = z.evalf();
          } catch (ValidateException ve) {
          }

          if (Double.isNaN(nDouble) || Double.isNaN(zDouble)) {
            Complex nc = n.evalfc();
            Complex zc = z.evalfc();
            return F.complexNum(BesselJS.sphericalBesselJ(nc, zc));
          } else {
            return F.complexNum(BesselJS.sphericalBesselJ(nDouble, zDouble));
          }

        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.SphericalBesselJ, rex, engine);
        }
      }
      // if (n.isReal() && z.isReal()) {
      // try {
      // return F.complexNum(BesselJS.sphericalBesselJ(n.evalDouble(), z.evalDouble()));
      // } catch (NegativeArraySizeException nae) {
      // return engine
      // .printMessage("SphericalBesselJ: " + ast.toString() + " caused
      // NegativeArraySizeException");
      // } catch (RuntimeException rte) {
      // return engine.printMessage("SphericalBesselJ: " + rte.getMessage());
      // }
      // }
      // if (n.isNumeric() && z.isNumeric()) {
      // try {
      // return F.complexNum(BesselJS.sphericalBesselJ(n.evalComplex(), z.evalComplex()));
      // } catch (NegativeArraySizeException nae) {
      // return engine
      // .printMessage("SphericalBesselJ: " + ast.toString() + " caused
      // NegativeArraySizeException");
      // } catch (RuntimeException rte) {
      // return engine.printMessage("SphericalBesselJ: " + rte.getMessage());
      // }
      // }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class SphericalHankelH1 extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr a = ast.arg1();
        int ai = a.toIntDefault();
        IExpr b = ast.arg2();
        switch (ai) {
          case 0:
            // ((-1)*I*E^(I*b))/b
            return F.Times(F.CN1, F.CI, F.Power(b, F.CN1), F.Exp(F.Times(F.CI, b)));
          case 1:
            // ((-I-b)*E^(I*b))/b^2
            return F.Times(F.Power(b, F.CN2), F.Subtract(F.CNI, b), F.Exp(F.Times(F.CI, b)));
          case -1:
            // E^(I*b)/b
            return F.Times(F.Power(b, F.CN1), F.Exp(F.Times(F.CI, b)));
          case 2:
            // (I*(-3+3*I*b+b^2)*E^(I*b))/b^3
            return F.Times(F.CI, F.Power(b, F.CN3), F.Plus(F.CN3, F.Times(F.C3, F.CI, b), F.Sqr(b)),
                F.Exp(F.Times(F.CI, b)));
          case 3:
            // (((-15)*I-15*b+6*I*b^2+b^3)*E^(I*b))/b^4
            return F.Times(F.Power(b, F.CN4), F.Plus(F.Times(F.ZZ(-15L), F.CI),
                F.Times(F.ZZ(-15L), b), F.Times(F.C6, F.CI, F.Sqr(b)), F.Power(b, F.C3)),
                F.Exp(F.Times(F.CI, b)));
        }
        // SphericalHankelH1(a_,b_):=(Sqrt(Pi/2)*BesselJ(1/2*(1+2*a),b))/Sqrt(b)+(I*Sqrt(Pi/2)*BesselY(1/2*(1+2*a),b))/Sqrt(b)
        return F.Plus(
            F.Times(F.Power(b, F.CN1D2), F.Sqrt(F.CPiHalf),
                F.BesselJ(F.Times(C1D2, F.Plus(F.C1, F.Times(C2, a))), b)),
            Times(CI, Power(b, CN1D2), Sqrt(CPiHalf),
                BesselY(Times(C1D2, Plus(C1, Times(C2, a))), b)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
        try {
          // Complex nc = n.evalComplex();
          // Complex zc = z.evalComplex();
          // return FunctionExpand.callMatcher(F.FunctionExpand(ast), ast, engine);
          return functionExpand(ast, engine);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.SphericalHankelH1, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class SphericalHankelH2 extends AbstractFunctionEvaluator
      implements IFunctionExpand {
    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr a = ast.arg1();
        int ai = a.toIntDefault();
        IExpr b = ast.arg2();
        switch (ai) {
          case 0:
            // I/(E^(I*b)*b)
            return F.Times(F.CI, F.Power(F.Times(F.Exp(F.Times(F.CI, b)), b), F.CN1));
          case 1:
            // (I-b)/(E^(I*b)*b^2)
            return F.Times(F.Subtract(F.CI, b),
                F.Power(F.Times(F.Exp(F.Times(F.CI, b)), F.Sqr(b)), F.CN1));
          case -1:
            // 1/(E^(I*b)*b)
            return F.Power(F.Times(F.Exp(F.Times(F.CI, b)), b), F.CN1);
          case 2:
            // ((-1)*I*(-3+(-3)*I*b+b^2))/(E^(I*b)*b^3)
            return F.Times(F.CN1, F.CI, F.Plus(F.CN3, F.Times(F.CN3, F.CI, b), F.Sqr(b)),
                F.Power(F.Times(F.Exp(F.Times(F.CI, b)), F.Power(b, F.C3)), F.CN1));
          case 3:
            // (15*I-15*b+(-6)*I*b^2+b^3)/(E^(I*b)*b^4)
            return F.Times(
                F.Plus(F.Times(F.ZZ(15L), F.CI), F.Times(F.ZZ(-15L), b),
                    F.Times(F.CN6, F.CI, F.Sqr(b)), F.Power(b, F.C3)),
                F.Power(F.Times(F.Exp(F.Times(F.CI, b)), F.Power(b, F.C4)), F.CN1));
        }
        // SphericalHankelH2(a_,b_):=(Sqrt(Pi/2)*BesselJ(1/2*(1+2*a),b))/Sqrt(b)+(-I*Sqrt(Pi/2)*BesselY(1/2*(1+2*a),b))/Sqrt(b)
        return F.Plus(
            F.Times(Power(b, CN1D2), F.Sqrt(CPiHalf),
                F.BesselJ(F.Times(F.C1D2, F.Plus(F.C1, F.Times(F.C2, a))), b)),
            F.Times(F.CNI, F.Power(b, F.CN1D2), F.Sqrt(F.CPiHalf),
                F.BesselY(F.Times(F.C1D2, F.Plus(F.C1, F.Times(C2, a))), b)));

      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
        try {
          return functionExpand(ast, engine);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.SphericalHankelH2, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>SphericalBesselY(n, z)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * spherical Bessel function <code>Y(n, x)</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href=
   * "https://en.wikipedia.org/wiki/Bessel_function#Spherical_Bessel_functions">Wikipedia - Bessel
   * function - Spherical Bessel function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   */
  private static final class SphericalBesselY extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return SphericalBesselYRules.match3(ast, engine);
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      // (Sqrt(Pi/2)*BesselY(1/2*(1+2*n),z))/Sqrt(z)
      return F.Times(F.Sqrt(F.CPiHalf), F.Power(z, F.CN1D2),
          F.BesselY(F.Times(F.C1D2, F.Plus(F.C1, F.Times(F.C2, n))), z));

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.CComplexInfinity;
      }
      if (engine.isDoubleMode() && n.isNumber() && z.isNumber()) {
        try {
          double nDouble = Double.NaN;
          double zDouble = Double.NaN;
          try {
            nDouble = n.evalf();
            zDouble = z.evalf();
          } catch (ValidateException ve) {
          }

          if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || zDouble < 0.0) {
            Complex nc = n.evalfc();
            Complex zc = z.evalfc();
            return F.complexNum(BesselJS.sphericalBesselY(nc, zc));
          } else {
            return F.complexNum(BesselJS.sphericalBesselY(nDouble, zDouble));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.SphericalBesselY, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class WeberE extends AbstractFunctionEvaluator implements IFunctionExpand {
    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      if (ast.isAST3()) {
        IExpr c = ast.arg2();
        return functionExpand3(a, b, c);
      }
      return functionExpand2(a, b);

    }

    private static IExpr functionExpand3(IExpr a, IExpr b, IExpr c) {
      // (-c*Cos(1/2*a*Pi)*Gamma(2+b)*HypergeometricPFQ({1+b/2,3/2+b/2},{3/2,3/2-a/2+b/2,
      // 3/2+a/2+b/2},(-1)*1/4*c^2))/(2*Gamma(3/2-a/2+b/2)*Gamma(3/2+a/2+b/2))+(Gamma(1+b)*HypergeometricPFQ({
      // 1/2+b/2,1+b/2},{1/2,1-a/2+b/2,1+a/2+b/2},(-1)*1/4*c^2)*Sin(1/2*a*Pi))/(Gamma(1-a/
      // 2+b/2)*Gamma(1+a/2+b/2))
      return F.Plus(
          F.Times(F.CN1, c, F.Cos(F.Times(F.C1D2, a, F.Pi)),
              F.Power(F.Times(F.C2,
                  F.Gamma(F.Plus(F.QQ(3L, 2L), F.Times(F.CN1D2, a), F.Times(F.C1D2, b))),
                  F.Gamma(F.Plus(F.QQ(3L, 2L), F.Times(F.C1D2, a), F.Times(F.C1D2, b)))), F.CN1),
              F.Gamma(F.Plus(F.C2, b)),
              F.HypergeometricPFQ(
                  F.list(F.Plus(F.C1, F.Times(F.C1D2, b)),
                      F.Plus(F.QQ(3L, 2L), F.Times(F.C1D2, b))),
                  F.list(F.QQ(3L, 2L),
                      F.Plus(F.QQ(3L, 2L), F.Times(F.CN1D2, a), F.Times(F.C1D2, b)),
                      F.Plus(F.QQ(3L, 2L), F.Times(F.C1D2, a), F.Times(F.C1D2, b))),
                  F.Times(F.CN1, F.C1D4, F.Sqr(c)))),
          F.Times(
              F.Power(F.Times(F.Gamma(F.Plus(F.C1, F.Times(F.CN1D2, a), F.Times(F.C1D2, b))),
                  F.Gamma(F.Plus(F.C1, F.Times(F.C1D2, a), F.Times(F.C1D2, b)))), F.CN1),
              F.Gamma(F.Plus(F.C1, b)),
              F.HypergeometricPFQ(
                  F.list(F.Plus(F.C1D2, F.Times(F.C1D2, b)), F.Plus(F.C1, F.Times(F.C1D2, b))),
                  F.list(F.C1D2, F.Plus(F.C1, F.Times(F.CN1D2, a), F.Times(F.C1D2, b)),
                      F.Plus(F.C1, F.Times(F.C1D2, a), F.Times(F.C1D2, b))),
                  F.Times(F.CN1, F.C1D4, F.Sqr(c))),
              F.Sin(F.Times(F.C1D2, a, F.Pi))));
    }

    private static IExpr functionExpand2(IExpr a, IExpr b) {
      // (2*b*Cos(1/2*a*Pi)^2*HypergeometricPFQ({1},{3/2-a/2,3/2+a/2},(-1)*1/4*b^2))/((-1+a)*(
      // 1+a)*Pi)+(2*HypergeometricPFQ({1},{1-a/2,1+a/2},(-1)*1/4*b^2)*Sin(1/2*a*Pi)^2)/(a*Pi)
      return F.Plus(
          F.Times(F.C2, b, F.Power(F.Times(F.Plus(F.CN1, a), F.Plus(F.C1, a), F.Pi), F.CN1),
              F.Sqr(F.Cos(F.Times(F.C1D2, a, F.Pi))),
              F.HypergeometricPFQ(F.list(F.C1),
                  F.list(F.Plus(F.QQ(3L, 2L), F.Times(F.CN1D2, a)),
                      F.Plus(F.QQ(3L, 2L), F.Times(F.C1D2, a))),
                  F.Times(F.CN1, F.C1D4, F.Sqr(b)))),
          F.Times(F.C2, F.Power(F.Times(a, F.Pi), F.CN1),
              F.HypergeometricPFQ(F.list(F.C1),
                  F.list(F.Plus(F.C1, F.Times(F.CN1D2, a)), F.Plus(F.C1, F.Times(F.C1D2, a))),
                  F.Times(F.CN1, F.C1D4, F.Sqr(b))),
              F.Sqr(F.Sin(F.Times(F.C1D2, a, F.Pi)))));
    }

    // WeberE(a_,b_) :=
    // (2*b*Cos((a*Pi)/2)^2*HypergeometricPFQ({1},{3/2-a/2,3/2+a/2},-(b^2/4)))/((-1+a)*(1+a)*Pi)+(2*HypergeometricPFQ({1},{1-a/2,1+a/2},-(b^2/4))*Sin((a*Pi)/2)^2)/(a*Pi),
    // WeberE(a_,b_,c_) :=
    // -((c*Cos((a*Pi)/2)*Gamma(2+b)*HypergeometricPFQ({1+b/2,3/2+b/2},{3/2,3/2-a/2+b/2,3/2+a/2+b/2},-(c^2/4)))/(2*Gamma(3/2-a/2+b/2)*Gamma(3/2+a/2+b/2)))+(Gamma(1+b)*HypergeometricPFQ({1/2+b/2,1+b/2},{1/2,1-a/2+b/2,1+a/2+b/2},-(c^2/4))*Sin((a*Pi)/2))/(Gamma(1-a/2+b/2)*Gamma(1+a/2+b/2)),
    //
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr n = ast.arg1();

      if (ast.isAST2()) {
        // https://dlmf.nist.gov/11.10#vii
        final IExpr z = ast.arg2();
        return weber2(n, z, engine.isNumericMode()).eval(engine);
      }
      IExpr m = ast.arg2();
      IExpr z = ast.arg3();
      return weber3(n, m, z, engine.isNumericMode()).eval(engine);
    }

    private static IExpr weber3(final IExpr n, IExpr m, IExpr z, boolean numericMode) {
      if (numericMode) {
        if (n.isNumber() && m.isNumber() && z.isNumber()) {
          try {
            return functionExpand3(n, m, z);
            // return FunctionExpand.callMatcher(F.FunctionExpand(ast), ast, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.WeberE, rex);
          }
        }
      }

      return F.NIL;
    }

    private static IExpr weber2(final IExpr n, final IExpr z, boolean numericMode) {
      if (n.isZero()) {
        if (z.isZero()) {
          return F.C0;
        }
        return F.Negate(F.StruveH(n, z));
      }
      if (z.isZero()) {
        // 1/2*n*Pi*Sinc(1/2*n*Pi)^2
        return F.Times(F.C1D2, n, F.Pi, F.Sqr(F.Sinc(F.Times(F.C1D2, n, F.Pi))));
      }

      int ni = n.toIntDefault();
      if (ni != Integer.MIN_VALUE) {
        // https://dlmf.nist.gov/11.10#vi
        if (ni > 0) {
          // -StruveH(n,z)+Sum(Gamma(k+1/2)/((z/2)^(1+2*k-n)*Gamma(-k+n+1/2)),{k,0,Floor(1/2*(-1+n))})/Pi
          int maxK = (ni - 1) / 2;
          IExpr sum = F.sum(k -> F.Times(F.Gamma(F.Plus(k, F.C1D2)),
              F.Power(F.Times(F.C1D2, z), F.Plus(F.CN1, F.Times(F.CN2, k), n)),
              F.Power(F.Gamma(F.Plus(F.Negate(k), n, F.C1D2)), F.CN1)), 0, maxK);
          return F.Plus(F.Negate(F.StruveH(n, z)), F.Times(F.Power(F.Pi, F.CN1), sum));
        }
        if (ni < 0) {
          IExpr npos = F.ZZ(-ni);
          // -StruveH(n,z)+(-1)^(npos+1)/Pi*Sum(((z/2)^(n+2*k+1)*Gamma(-1/2-k+npos))/Gamma(k+3/2),{k,0,Ceiling(1/2*(-3+npos))})
          int maxK = IntMath.divide(-ni - 3, 2, RoundingMode.CEILING);
          IExpr sum = F.sum(
              k -> F.Times(F.Times(F.Power(F.Times(F.C1D2, z), F.Plus(n, F.Times(F.C2, k), F.C1)),
                  F.Power(F.Gamma(F.Plus(k, F.QQ(3L, 2L))), F.CN1),
                  F.Gamma(F.Plus(F.CN1D2, F.Negate(k), npos)))),
              0, maxK);
          return F.Plus(F.Negate(F.StruveH(n, z)),
              F.Times(F.Power(F.CN1, F.Plus(npos, F.C1)), F.Power(F.Pi, F.CN1), sum));
        }
      }

      if (numericMode) {
        if (n.isNumber() && z.isNumber()) {
          try {
            return functionExpand2(n, z);
            // return FunctionExpand.callMatcher(F.FunctionExpand(ast), ast, engine);

          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.WeberE, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private BesselFunctions() {}
}
