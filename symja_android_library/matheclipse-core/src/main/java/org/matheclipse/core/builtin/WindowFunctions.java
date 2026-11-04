package org.matheclipse.core.builtin;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class WindowFunctions {

  public static final WindowFunction BARTLETT =
      new WindowFunction(WindowFunctions::bartlettWindow, "BartlettWindow");
  public static final WindowFunction BLACKMAN_HARRIS =
      new WindowFunction(WindowFunctions::blackmanHarrisWindow, "BlackmanHarrisWindow");
  public static final WindowFunction BLACKMAN_NUTTAL =
      new WindowFunction(WindowFunctions::blackmanNuttallWindow, "BlackmanNuttallWindow");
  public static final WindowFunction BLACKMAN =
      new WindowFunction(WindowFunctions::blackmanWindow, "BlackmanWindow");
  public static final WindowFunction DIRICHLET =
      new WindowFunction(WindowFunctions::dirichletWindow, "DirichletWindow");
  public static final WindowFunction FLAT_TOP =
      new WindowFunction(WindowFunctions::flatTopWindow, "FlatTopWindow");
  public static final WindowFunction GAUSSIAN =
      new WindowFunction(WindowFunctions::gaussianWindow, "GaussianWindow");
  public static final WindowFunction HAMMING =
      new WindowFunction(WindowFunctions::hammingWindow, "HammingWindow");
  public static final WindowFunction NUTTALL =
      new WindowFunction(WindowFunctions::nuttallWindow, "NuttallWindow");
  public static final WindowFunction PARZEN =
      new WindowFunction(WindowFunctions::parzenWindow, "ParzenWindow");
  public static final WindowFunction TUKEY =
      new WindowFunction(WindowFunctions::tukeyWindow, "TukeyWindow");

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BartlettWindow.setEvaluator(BARTLETT);
      S.BlackmanHarrisWindow.setEvaluator(BLACKMAN_HARRIS);
      S.BlackmanNuttallWindow.setEvaluator(BLACKMAN_NUTTAL);
      S.BlackmanWindow.setEvaluator(BLACKMAN);
      S.DirichletWindow.setEvaluator(DIRICHLET);
      S.FlatTopWindow.setEvaluator(FLAT_TOP);
      S.GaussianWindow.setEvaluator(GAUSSIAN);
      S.HammingWindow.setEvaluator(HAMMING);
      // windows with an optional parameter: the value inside -1/2 <= x <= 1/2 and the default
      S.HannWindow.setEvaluator(new ParametricWindow(
          (x, a) -> F.Plus(a, F.Cos(F.Times(F.C2, S.Pi, x)),
              F.Times(F.CN1, a, F.Cos(F.Times(F.C2, S.Pi, x)))),
          F.C1D2));
      S.CosineWindow.setEvaluator(
          new ParametricWindow((x, a) -> F.Power(F.Cos(F.Times(S.Pi, x)), a), F.C1));
      S.CauchyWindow.setEvaluator(new ParametricWindow(
          (x, a) -> F.Power(F.Plus(F.C1, F.Times(F.C4, F.Sqr(a), F.Sqr(x))), F.CN1), F.C3));
      S.ConnesWindow.setEvaluator(new ParametricWindow((x, a) -> F
          .Times(F.Power(a, F.CN4), F.Sqr(F.Subtract(F.Sqr(a), F.Times(F.C4, F.Sqr(x))))), F.C1));
      S.WelchWindow.setEvaluator(new ParametricWindow(
          (x, a) -> F.Times(F.Power(a, F.CN2), F.Subtract(F.Sqr(a), F.Times(F.C4, F.Sqr(x)))),
          F.C1));
      S.KaiserWindow.setEvaluator(new ParametricWindow((x, a) -> F.Times(
          F.BesselI(F.C0,
              F.Times(a, F.Sqrt(F.Subtract(F.C1, F.Times(F.C2, x))),
                  F.Sqrt(F.Plus(F.C1, F.Times(F.C2, x))))),
          F.Power(F.BesselI(F.C0, a), F.CN1)), F.C3));
      S.KaiserBesselWindow.setEvaluator(new ParametricWindow((x, a) -> F.Times(F.QQ(1L, 1000L),
          F.Plus(F.ZZ(402L), F.Times(F.ZZ(498L), F.Cos(F.Times(F.C2, S.Pi, x))),
              F.Times(F.ZZ(99L), F.Cos(F.Times(F.C4, S.Pi, x))), F.Cos(F.Times(F.C6, S.Pi, x)))),
          null));
      S.LanczosWindow.setEvaluator(new ParametricWindow(
          (x, a) -> F.Times(F.Power(F.Times(F.C2, S.Pi, x), F.CN1),
              F.Sin(F.Times(F.C2, S.Pi, x))),
          null));
      S.NuttallWindow.setEvaluator(NUTTALL);
      S.ParzenWindow.setEvaluator(PARZEN);
      S.TukeyWindow.setEvaluator(TUKEY);
    }
  }

  public static class WindowFunction extends AbstractTrigArg1 implements Function<IExpr, IExpr> {
    private final Function<IExpr, IExpr> function;
    private final String name;


    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr apply(IExpr t) {
      return function.apply(t);
    }

    public Function<IExpr, IExpr> get() {
      return function;
    }

    public String name() {
      return name;
    }

    public WindowFunction(Function<IExpr, IExpr> f, String name) {
      this.function = f;
      this.name = name;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isRealResult()) {
        return function.apply(arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * A window function <code>w(x)</code> or <code>w(x, a)</code> with an optional parameter
   * <code>a</code>, which is <code>0</code> outside of <code>-1/2 &lt;= x &lt;= 1/2</code>.
   */
  private static final class ParametricWindow extends AbstractFunctionEvaluator
      implements IFunctionExpand {
    /** the value inside <code>-1/2 &lt;= x &lt;= 1/2</code> for the parameter <code>a</code> */
    private final BinaryOperator<IExpr> function;

    /** the default parameter or <code>null</code> if the window has no parameter */
    private final IExpr defaultParameter;

    private ParametricWindow(BinaryOperator<IExpr> function, IExpr defaultParameter) {
      this.function = function;
      this.defaultParameter = defaultParameter;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isRealResult() && (ast.argSize() == 1 || ast.arg2().isRealResult())) {
        return piecewise(ast);
      }
      return F.NIL;
    }

    @Override
    public IExpr functionExpand(IAST ast, EvalEngine engine) {
      return piecewise(ast);
    }

    private IExpr piecewise(IAST ast) {
      IExpr x = ast.arg1();
      IExpr a = ast.argSize() == 2 ? ast.arg2() : defaultParameter;
      return F.Piecewise(F.list(F.list(function.apply(x, a), F.LessEqual(F.CN1D2, x, F.C1D2))),
          F.C0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return defaultParameter == null ? ARGS_1_1 : ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  public static IExpr bartlettWindow(IExpr x) {
    return
    // [$ Piecewise({{1 - 2*x, 0 <= x <= 1/2}, {1 + 2*x, Inequality(-(1/2), LessEqual, x, Less,
    // 0)}}, 0)
    // $]
    F.Piecewise(
        F.list(F.list(F.Plus(F.C1, F.Times(F.CN2, x)), F.LessEqual(F.C0, x, F.C1D2)), F
            .list(F.Plus(F.C1, F.Times(F.C2, x)), F.And(F.LessEqual(F.CN1D2, x), F.Less(x, F.C0)))),
        F.C0); // $$;
  }

  public static IExpr blackmanHarrisWindow(IExpr x) {
    return
    // [$ Piecewise({{(35875 + 48829*Cos(2*Pi*x) + 14128*Cos(4*Pi*x) + 1168*Cos(6*Pi*x))/100000,
    // -(1/2) <= x <=
    // 1/2}}, 0)
    // $]
    F.Piecewise(F.list(F.list(
        F.Times(F.QQ(1L, 100000L),
            F.Plus(F.ZZ(35875L), F.Times(F.ZZ(48829L), F.Cos(F.Times(F.C2, F.Pi, x))),
                F.Times(F.ZZ(14128L), F.Cos(F.Times(F.C4, F.Pi, x))),
                F.Times(F.ZZ(1168L), F.Cos(F.Times(F.C6, F.Pi, x))))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr blackmanNuttallWindow(IExpr x) {
    return
    // [$ Piecewise({{(4891775*Cos(2*Pi*x) + 1365995*Cos(4*Pi*x) + 106411*Cos(6*Pi*x) +
    // 3635819)/10000000, -(1/2) <=
    // x
    // <= 1/2}}, 0)
    // $]
    F.Piecewise(F.list(F.list(
        F.Times(F.QQ(1L, 10000000L),
            F.Plus(F.Times(F.ZZ(4891775L), F.Cos(F.Times(F.C2, F.Pi, x))),
                F.Times(F.ZZ(1365995L), F.Cos(F.Times(F.C4, F.Pi, x))),
                F.Times(F.ZZ(106411L), F.Cos(F.Times(F.C6, F.Pi, x))), F.ZZ(3635819L))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr blackmanWindow(IExpr x) {
    return
    // [$ Piecewise({{(1/50)*(21 + 25*Cos(2*Pi*x) + 4*Cos(4*Pi*x)), -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(
        F.list(F.list(
            F.Times(F.QQ(1L, 50L),
                F.Plus(F.ZZ(21L), F.Times(F.ZZ(25L), F.Cos(F.Times(F.C2, F.Pi, x))),
                    F.Times(F.C4, F.Cos(F.Times(F.C4, F.Pi, x))))),
            F.LessEqual(F.CN1D2, x, F.C1D2))),
        F.C0); // $$;
  }

  public static IExpr dirichletWindow(IExpr x) {
    return
    // [$ Piecewise({{1, -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(F.list(F.list(F.C1, F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr flatTopWindow(IExpr x) {
    return
    // [$ Piecewise({{(215578947 + 416631580*Cos(2*Pi*x) + 277263158*Cos(4*Pi*x) +
    // 83578947*Cos(6*Pi*x) +
    // 6947368*Cos(8*Pi*x))/1000000000, -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(F.list(F.list(
        F.Times(F.QQ(1L, 1000000000L),
            F.Plus(F.ZZ(215578947L), F.Times(F.ZZ(416631580L), F.Cos(F.Times(F.C2, F.Pi, x))),
                F.Times(F.ZZ(277263158L), F.Cos(F.Times(F.C4, F.Pi, x))),
                F.Times(F.ZZ(83578947L), F.Cos(F.Times(F.C6, F.Pi, x))),
                F.Times(F.ZZ(6947368L), F.Cos(F.Times(F.C8, F.Pi, x))))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr gaussianWindow(IExpr x) {
    return
    // [$ Piecewise({{E^(-((50*x^2)/9)), -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(F.list(F.list(F.Exp(F.Times(F.CN1, F.QQ(1L, 9L), F.ZZ(50L), F.Sqr(x))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr hammingWindow(IExpr x) {
    return
    // [$ Piecewise({{25/46 + (21/46)*Cos(2*Pi*x), -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(F
        .list(F.list(F.Plus(F.QQ(25L, 46L), F.Times(F.QQ(21L, 46L), F.Cos(F.Times(F.C2, F.Pi, x)))),
            F.LessEqual(F.CN1D2, x, F.C1D2))),
        F.C0); // $$;
  }

  public static IExpr hannWindow(IExpr x) {
    return
    // [$ Piecewise({{1/2 + (1/2)*Cos(2*Pi*x), -(1/2) <= x <= 1/2}}, 0) $]
    F.Piecewise(F.list(F.list(F.Plus(F.C1D2, F.Times(F.C1D2, F.Cos(F.Times(F.C2, F.Pi, x)))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr nuttallWindow(IExpr x) {
    return
    // [$ Piecewise[{{(88942 + 121849*Cos(2*Pi*x) + 36058*Cos(4*Pi*x) + 3151*Cos(6*Pi*x))/250000,
    // -(1/2) <= x <=
    // 1/2}}, 0] $]
    F.Piecewise(F.list(F.list(
        F.Times(F.QQ(1L, 250000L),
            F.Plus(F.ZZ(88942L), F.Times(F.ZZ(121849L), F.Cos(F.Times(F.C2, F.Pi, x))),
                F.Times(F.ZZ(36058L), F.Cos(F.Times(F.C4, F.Pi, x))),
                F.Times(F.ZZ(3151L), F.Cos(F.Times(F.C6, F.Pi, x))))),
        F.LessEqual(F.CN1D2, x, F.C1D2))), F.C0); // $$;
  }

  public static IExpr parzenWindow(IExpr x) {
    return
    // [$ Piecewise({{-2*(-1 + 2*x)^3, Inequality(1/4, Less, x, LessEqual, 1/2)},{2*(1 + 2*x)^3,
    // Inequality(-(1/2),
    // LessEqual, x, Less, -(1/4))},{1 - 24*x^2 - 48*x^3, Inequality(-(1/4), LessEqual, x, Less,
    // 0)}, {1 - 24*x^2 +
    // 48*x^3, 0 <= x <= 1/4}}, 0) $]
    F.Piecewise(F.List(
        F.list(F.Times(F.CN2, F.Power(F.Plus(F.CN1, F.Times(F.C2, x)), F.C3)),
            F.And(F.Less(F.C1D4, x), F.LessEqual(x, F.C1D2))),
        F.list(F.Times(F.C2, F.Power(F.Plus(F.C1, F.Times(F.C2, x)), F.C3)),
            F.And(F.LessEqual(F.CN1D2, x), F.Less(x, F.CN1D4))),
        F.list(F.Plus(F.C1, F.Times(F.ZZ(-24L), F.Sqr(x)), F.Times(F.ZZ(-48L), F.Power(x, F.C3))),
            F.And(F.LessEqual(F.CN1D4, x), F.Less(x, F.C0))),
        F.list(F.Plus(F.C1, F.Times(F.ZZ(-24L), F.Sqr(x)), F.Times(F.ZZ(48L), F.Power(x, F.C3))),
            F.LessEqual(F.C0, x, F.C1D4))),
        F.C0); // $$;
  }

  public static IExpr tukeyWindow(IExpr x) {
    return
    // [$ Piecewise({{1, -(1/3) - 2*x <= 0 && -(1/3) + 2*x <= 0}, {(1/2)*(1 + Cos(3*Pi*(1/6 + x))),
    // x >= -(1/2) &&
    // -(1/3) - 2*x > 0}, {(1/2)*(1 + Cos(3*Pi*(-(1/6) + x))), -(1/3) + 2*x > 0 && x <= 1/2}}, 0) $]
    F.Piecewise(
        F.list(
            F.list(
                F.C1, F
                    .And(
                        F.LessEqual(F.Plus(F.CN1D3, F.Times(F.CN2, x)), F.C0), F
                            .LessEqual(F.Plus(F.CN1D3, F.Times(F.C2, x)), F.C0))),
            F.list(
                F.Times(F.C1D2, F.Plus(F.C1, F.Cos(F.Times(F.C3, F.Pi, F.Plus(F.QQ(1L, 6L), x))))),
                F.And(F.GreaterEqual(x, F.CN1D2),
                    F.Greater(F.Plus(F.CN1D3, F.Times(F.CN2, x)), F.C0))),
            F.list(
                F.Times(F.C1D2, F.Plus(F.C1, F.Cos(F.Times(F.C3, F.Pi, F.Plus(F.QQ(-1L, 6L), x))))),
                F.And(F.Greater(F.Plus(F.CN1D3, F.Times(F.C2, x)), F.C0), F.LessEqual(x, F.C1D2)))),
        F.C0); // $$;
  }

  public static void initialize() {
    Initializer.init();
  }

  private WindowFunctions() {}
}
