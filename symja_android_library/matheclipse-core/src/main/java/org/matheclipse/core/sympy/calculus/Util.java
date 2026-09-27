package org.matheclipse.core.sympy.calculus;

import java.util.Optional;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ITrigonometricFunction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.Expr;
import org.matheclipse.core.sympy.solvers.Decompogen;

public class Util {

  /**
   * Determines the convexity of the function in the given domain.
   *
   * @param f the function
   * @param x the variable
   * @param domain a condition like <code>0&lt;x&lt;1</code>, use {@link S#True} for all real
   *        numbers
   * @return {@link S#True} if the function is convex, {@link S#False} if the function isn't
   *         convex or {@link F#NIL} if the convexity can't be decided
   */
  public static IExpr isConvex(IExpr f, ISymbol x, IExpr domain, EvalEngine engine) {
    // >>> is_convex(exp(x), x)
    // True
    // >>> is_convex(x**3, x, domain = Interval(-1, oo))
    // False
    // >>> is_convex(1/x**2, x, domain=Interval.open(0, oo))
    // True

    // if any(s in domain for s in singularities(f, var)):
    // return False
    if (Singularities.hasSingularity(f, domain, x, engine).isTrue()) {
      return S.False;
    }
    // condition = f.diff(var, 2) < 0
    // if solve_univariate_inequality(condition, var, False, domain):
    // return False
    IExpr derivative = engine.evaluate(F.D(f, F.List(x, F.C2)));
    if (!derivative.isFree(S.D, true) || !derivative.isFree(S.Derivative, true)) {
      return F.NIL;
    }
    IExpr reduced = Singularities.reduce(F.Less(derivative, F.C0), domain, x, engine);
    if (reduced.isNIL()) {
      return F.NIL;
    }
    return reduced.isFalse() ? S.True : S.False;
  }

  /**
   * Returns the stationary points of a function (where derivative of the function is 0) in the
   * given domain.
   *
   * @param f the function
   * @param x the variable
   * @param domain a condition like <code>0&lt;x&lt;1</code>, use {@link S#True} for all real
   *        numbers
   * @return the reduced condition for the stationary points, for example
   *         <code>x==-1||x==1</code>; {@link S#False} if there are no stationary points or
   *         {@link F#NIL} if the stationary points can't be determined
   */
  public static IExpr stationaryPoints(IExpr f, ISymbol x, IExpr domain, EvalEngine engine) {
    // >>> stationary_points(1/x, x, S.Reals)
    // EmptySet
    // >>> stationary_points(sin(x),x, Interval(0, 4*pi))
    // {pi/2, 3*pi/2, 5*pi/2, 7*pi/2}
    IExpr derivative = engine.evaluate(F.D(f, x));
    if (!derivative.isFree(S.D, true) || !derivative.isFree(S.Derivative, true)) {
      return F.NIL;
    }
    if (derivative.isZero()) {
      return domain;
    }
    return Singularities.reduce(F.Equal(derivative, F.C0), domain, x, engine);
  }
  /**
   * Return the checked period or raise an error.
   * 
   * @param orig_f
   * @param period
   * @return
   */
  private static IExpr _check(IExpr orig_f, IExpr period, ISymbol symbol) {
    IExpr new_f = orig_f.subs(symbol, F.Plus(symbol, period));
    new_f = F.eval(new_f);
    if (new_f.equals(orig_f)) {
      return period;
    } else {
      throw new UnsupportedOperationException("The period of the given function cannot be verified."
          + "When `%s` was replaced with `%s + %s` in `%s`, the result"
          + "was `%s` which was not recognized as being the same as" + "the original function."
          + "So either the period was wrong or the two forms were"
          + "not recognized as being equal." + "Set check=False to obtain the value. "
          + "(symbol, symbol, period, orig_f, new_f))");
    }
  }

  public static IExpr periodicity(IExpr f, ISymbol symbol) {
    return periodicity(f, symbol, false);
  }

  public static IExpr periodicity(IExpr f, ISymbol symbol, boolean check) {

    if (f.isFree(symbol)) {
      return F.C0;
    }

    // the method periodicity is called recursively, so we need to make sure that the dummy symbols
    // are unique
    ISymbol temp = F.Dummy("x" + EvalEngine.incModuleCounter(), F.Element(F.Slot1, F.Reals));
    f = f.subs(symbol, temp);
    symbol = temp;

    IExpr orig_f = f;
    IExpr period = F.NIL;

    if (f.isRelationalBinary()) {
      f = f.first().subtract(f.second());
    }
    final EvalEngine engine = EvalEngine.get();
    f = engine.evaluate(F.Simplify(f));

    if (f.isAST()) {
      Optional<IEvaluator> trigFunction = f.isInstance(ITrigonometricFunction.class);
      if (trigFunction.isPresent()) {
        try {
          period = ((ITrigonometricFunction) trigFunction.get()).period((IAST) f, period, symbol);
        } catch (UnsupportedOperationException uoe) {

        }
      }

      if (f.isAbs()) {
      }

      if (f.isExp()) {
        f = engine.evaluate(F.Power(S.E, F.Expand(f.exponent())));
        IExpr imF = f.im();
        if (!imF.isZero()) {
          IExpr period_real = periodicity(f.re(), symbol);
          if (period_real.isPresent()) {
            IExpr period_imag = periodicity(imF, symbol);
            if (period_imag.isPresent()) {
              period = lcim(F.List(period_real, period_imag));
            }
          }
        }
        // f = Pow(S.Exp1, expand_mul(f.exp))
        // if im(f) != 0:
        // period_real = periodicity(re(f), symbol)
        // period_imag = periodicity(im(f), symbol)
        // if period_real is not None and period_imag is not None:
        // period = lcim([period_real, period_imag])
      }

      if (f.isPower() && f.base() != S.E) {
        IExpr base = f.base();
        IExpr expo = f.exponent();
        boolean base_has_sym = base.has(symbol);
        boolean expo_has_sym = expo.has(symbol);
        if (base_has_sym && !expo_has_sym) {
          period = periodicity(base, symbol);
        } else if (expo_has_sym && !base_has_sym) {
          period = periodicity(expo, symbol);
        } else {
          period = _periodicity((IAST) f, symbol);
        }
        // base, expo = f.args
        // base_has_sym = base.has(symbol)
        // expo_has_sym = expo.has(symbol)
        //
        // if base_has_sym and not expo_has_sym:
        // period = periodicity(base, symbol)
        //
        // elif expo_has_sym and not base_has_sym:
        // period = periodicity(expo, symbol)
        //
        // else:
        // period = _periodicity(f.args, symbol)


      } else if (f.isTimes()) {
        Pair pair = Expr.asIndependent(f, F.List(symbol));
        IExpr coeff = pair.first();
        IExpr g = pair.second();
        trigFunction = g.isInstance(ITrigonometricFunction.class);
        if (!coeff.equalTo(F.C1).isTrue() || trigFunction.isPresent()) {
          period = periodicity(g, symbol);
        } else {
          if (g.isAST()) {
            period = _periodicity((IAST) g, symbol);
          }
        }
        // elif f.is_Mul:
        // coeff, g = f.as_independent(symbol, as_Add=False)
        // if isinstance(g, TrigonometricFunction) or not equal_valued(coeff, 1):
        // period = periodicity(g, symbol)
        // else:
        // period = _periodicity(g.args, symbol)
      } else if (f.isPlus()) {
        Pair pair = Expr.asIndependent(f, F.List(symbol));
        IExpr k = pair.first();
        IExpr g = pair.second();
        if (!k.isZero()) {
          return periodicity(g, symbol);
        }
        if (g.isAST()) {
          period = _periodicity((IAST) g, symbol);
        }
        // k, g = f.as_independent(symbol)
        // if k is not S.Zero:
        // return periodicity(g, symbol)
        //
        // period = _periodicity(g.args, symbol)
      } else if (period.isNIL()) {
        IAST g_s = Decompogen.decompogen(f, symbol);
        int num_of_gs = g_s.argSize();
        if (num_of_gs > 1) {
          for (int index = num_of_gs; index >= 1; index--) {
            IExpr g = g_s.get(index);
            int start_index = num_of_gs - index + 1;
            g = Decompogen.compogen(g_s.copyFrom(start_index), symbol);
            if (g.isPresent() && !g.equals(orig_f) && !g.equals(f)) {
              period = periodicity(g, symbol);
              if (period.isPresent()) {
                break;
              }
            }
          }
          // for index, g in enumerate(reversed(g_s)):
          // start_index = num_of_gs - 1 - index
          // g = compogen(g_s[start_index:], symbol)
          // if g not in (orig_f, f): # Fix for issue 12620
          // period = periodicity(g, symbol)
          // if period is not None:
          // break
        }
      }
    }
    if (period.isPresent()) {
      if (check) {
        return _check(orig_f, period, symbol);
      }
      return period;
    }
    return F.NIL;
  }



  private static IExpr _periodicity(IAST args, ISymbol symbol) {
    IASTAppendable periods = F.ListAlloc();
    for (int i = 1; i < args.size(); i++) {
      IExpr f = args.get(i);
      IExpr period = periodicity(f, symbol);
      if (period.isNIL()) {
        return F.NIL;
      }
      if (!period.isZero()) {
        periods.append(period);
      }
    }
    if (periods.argSize() > 1) {
      return lcim(periods);
    }
    if (periods.argSize() == 1) {
      return periods.arg1();
    }
    return F.NIL;
  }

  static IExpr lcim(IAST numbers) {
    IExpr result = F.NIL;
    if (numbers.forAll(x -> x.isIrrational() == COMPARE_TERNARY.TRUE)) {
      final EvalEngine engine = EvalEngine.get();
      IAST factorized_nums = numbers.map(num -> S.Factor.of(engine, num));
      IAST factors_num = factorized_nums.map(num -> num.asCoeffMul());
      if (factors_num.argSize() > 0) {
        IExpr term = factors_num.getPart(1, 2);
        if (factors_num.forAll(x -> x.second().equals(term))) {
          IExpr common_term = term;
          IAST coeffs = factors_num.map(x -> x.first());
          result = F.Times.of(engine, coeffs.apply(S.PolynomialLCM), common_term);
        }
      }
    } else if (numbers.forAll(x -> x.isRational())) {
      result = EvalEngine.get().evaluate(numbers.apply(S.PolynomialLCM));
    }
    return result;
  }
}
