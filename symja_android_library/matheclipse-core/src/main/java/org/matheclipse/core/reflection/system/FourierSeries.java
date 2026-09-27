package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.sympy.series.Fourier;

/**
 * Implements the functions <code>FourierSeries, FourierTrigSeries, FourierCoefficient,
 * FourierSinSeries, FourierCosSeries, FourierSinCoefficient, FourierCosCoefficient</code>.
 *
 * <p>
 * With the option <code>FourierParameters-&gt;{a, b}</code> the coefficient is defined as
 *
 * <pre>
 * FourierCoefficient(f, t, n) = (Abs(b)/(2*Pi))^((1+a)/2) * Integrate(f * E^(-I*b*n*t), {t, -Pi/Abs(b), Pi/Abs(b)})
 * FourierSeries(f, t, n) = (Abs(b)/(2*Pi))^((1-a)/2) * Sum(FourierCoefficient(f,t,k) * E^(I*b*k*t), {k, -n, n})
 * </pre>
 */
public class FourierSeries extends AbstractFunctionOptionEvaluator {
  public static final int SERIES = 0;
  public static final int TRIG_SERIES = 1;
  public static final int COEFFICIENT = 2;
  public static final int SIN_SERIES = 3;
  public static final int COS_SERIES = 4;
  public static final int SIN_COEFFICIENT = 5;
  public static final int COS_COEFFICIENT = 6;

  /** The maximum order of a series */
  private static final int MAX_ORDER = 1000;

  private final int kind;

  public FourierSeries() {
    this(SERIES);
  }

  public FourierSeries(int kind) {
    this.kind = kind;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (argSize != 3) {
      return F.NIL;
    }
    final IExpr function = ast.arg1();
    final IExpr t = ast.arg2();
    final IExpr n = ast.arg3();
    if (!t.isVariable()) {
      // `1` is not a valid variable.
      return Errors.printMessage(ast.topHead(), "ivar", F.List(t), engine);
    }
    IExpr a = F.C1;
    IExpr b = F.C1;
    if (option[0].isList2()) {
      a = option[0].first();
      b = option[0].second();
    } else if (!option[0].equals(S.Automatic)) {
      return F.NIL;
    }
    if (b.isZero() || !b.isRealResult()) {
      return F.NIL;
    }
    final IExpr absB = engine.evaluate(F.Abs(b));
    final IExpr upper = engine.evaluate(F.Divide(S.Pi, absB));
    final IExpr lower = engine.evaluate(F.Negate(upper));
    try {
      switch (kind) {
        case COEFFICIENT:
          return coefficient(function, t, n, a, b, absB, lower, upper, engine);
        case SERIES: {
          int order = order(n);
          if (order < 0) {
            return F.NIL;
          }
          IASTAppendable series = F.PlusAlloc(2 * order + 1);
          for (int k = -order; k <= order; k++) {
            IExpr kExpr = F.ZZ(k);
            IExpr c = coefficient(function, t, kExpr, a, b, absB, lower, upper, engine);
            if (c.isNIL()) {
              return F.NIL;
            }
            series.append(F.Times(c, F.Exp(F.Times(F.CI, b, kExpr, t))));
          }
          // (Abs(b)/(2*Pi))^((1-a)/2)
          IExpr factor = F.Power(F.Times(absB, F.Power(F.Times(F.C2, S.Pi), F.CN1)),
              F.Times(F.C1D2, F.Subtract(F.C1, a)));
          return engine.evaluate(F.Times(factor, series));
        }
        case TRIG_SERIES: {
          int order = order(n);
          if (order < 0) {
            return F.NIL;
          }
          return Fourier.fourierSeries(function, t, lower, upper, order, engine);
        }
        case SIN_SERIES:
        case COS_SERIES: {
          int order = order(n);
          if (order < 0) {
            return F.NIL;
          }
          // the series doesn't depend on the parameter a
          return kind == COS_SERIES //
              ? Fourier.fourierCosSeries(function, t, upper, order, engine)
              : Fourier.fourierSinSeries(function, t, upper, order, engine);
        }
        case SIN_COEFFICIENT:
        case COS_COEFFICIENT: {
          final boolean cosine = kind == COS_COEFFICIENT;
          if (n.isInteger()) {
            if (n.isNegative()) {
              return F.NIL;
            }
          } else if (!n.isVariable()) {
            return F.NIL;
          }
          // (2*Abs(b)/Pi)^((1+a)/2) * Integrate(f*Cos(b*n*t), {t, 0, Pi/Abs(b)})
          IExpr factor = a.isOne() ? F.C1
              : F.Power(F.Times(F.C2, absB, F.Power(S.Pi, F.CN1)),
                  F.Times(F.C1D2, F.Subtract(a, F.C1)));
          IExpr c = Fourier.halfRangeCoefficient(function, t, upper, n, cosine, engine);
          if (c.isNIL()) {
            return F.NIL;
          }
          if (!cosine && b.isNegative()) {
            c = F.Negate(c);
          }
          c = engine.evaluate(F.Times(factor, c));
          if (n.isInteger()) {
            return c;
          }
          IExpr c0 = zeroCoefficient(function, t, upper, cosine, engine);
          return piecewise(c, c0.isPresent() ? engine.evaluate(F.Times(factor, c0)) : F.NIL, n,
              engine);
        }
        default:
      }
    } catch (ValueError ve) {
      return Errors.printMessage(ast.topHead(), ve, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /** The coefficient for <code>n==0</code> of the half range expansion */
  private static IExpr zeroCoefficient(IExpr function, IExpr t, IExpr upper, boolean cosine,
      EvalEngine engine) {
    if (!cosine) {
      return F.C0;
    }
    // WMA: the coefficient for n==0 is twice the constant term of the series
    return Fourier.halfRangeCoefficient(function, t, upper, F.C0, true, engine);
  }

  private static int order(IExpr n) {
    int order = n.toIntDefault();
    return order > MAX_ORDER ? -1 : order;
  }

  private static IExpr coefficient(IExpr function, IExpr t, IExpr n, IExpr a, IExpr b,
      IExpr absB, IExpr lower, IExpr upper, EvalEngine engine) {
    if (!n.isInteger() && !n.isVariable()) {
      return F.NIL;
    }
    // (Abs(b)/(2*Pi))^((1+a)/2) * (2*Pi/Abs(b))
    IExpr factor = engine.evaluate(F.Times(F.C2, S.Pi, F.Power(absB, F.CN1),
        F.Power(F.Times(absB, F.Power(F.Times(F.C2, S.Pi), F.CN1)),
            F.Times(F.C1D2, F.Plus(F.C1, a)))));
    // Fourier#fourierExpCoefficient() uses the kernel E^(-2*I*n*Pi*t/L) with L == 2*Pi/Abs(b)
    IExpr index = b.isNegative() ? engine.evaluate(F.Negate(n)) : n;
    IExpr c = Fourier.fourierExpCoefficient(function, t, lower, upper, index, engine);
    if (c.isNIL()) {
      return F.NIL;
    }
    c = engine.evaluate(F.Times(factor, c));
    if (n.isInteger()) {
      return c;
    }
    IExpr c0 = Fourier.fourierExpCoefficient(function, t, lower, upper, F.C0, engine);
    return piecewise(c, c0.isPresent() ? engine.evaluate(F.Times(factor, c0)) : F.NIL, n,
        engine);
  }

  /**
   * Return <code>Piecewise({{zeroValue, n==0}}, generic)</code> if the generic formula isn't
   * valid for <code>n==0</code>.
   */
  private static IExpr piecewise(IExpr generic, IExpr zeroValue, IExpr n, EvalEngine engine) {
    if (zeroValue.isNIL()) {
      return generic;
    }
    try {
      IExpr atZero = engine.evalQuiet(F.subst(generic, n, F.C0));
      if (atZero.equals(zeroValue)) {
        return generic;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.Piecewise(F.List(F.List(zeroValue, F.Equal(n, F.C0))), generic);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.FourierParameters},
        new IExpr[] {S.Automatic});
  }
}
