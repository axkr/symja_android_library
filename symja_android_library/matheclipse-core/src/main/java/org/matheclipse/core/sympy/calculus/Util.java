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
  public static IAST continuousDomain(IExpr expr, ISymbol symbol, IExpr domain) {
    IAST cont_domain = F.NIL;
    if (domain == S.Reals) {
      cont_domain = IntervalDataSym.reals();
    } else if (IntervalDataSym.isInterval(domain)) {
      if (IntervalDataSym.isEmptySet(domain)) {
        return IntervalDataSym.emptySet();
      }
      cont_domain = (IAST) domain;
    }
    if (cont_domain.isNIL()) {
      throw new UnsupportedOperationException("Domain must be a subset of Reals.");
    }

    ISymbol x = F.Dummy('x');

    if (expr.isAST()) {
      IAST f = (IAST) expr;
      for (int i = 1; i < f.size(); i++) {
        IExpr atom = f.get(i);
        if (atom.isPower()) {
          IExpr exp = atom.exponent();
          IExpr den = exp.asNumerDenom().second();
          if (exp.isRational() && den.isOdd()) {
            // pass # 0^negative handled by singularities()
          } else {
            // IAST constraint =
            // solveUnivariateInequality(F.GreaterEqual(atom.base(), F.C0), symbol).as_set();
            // cont_domain =
            // IntervalDataSym.intersectionIntervalData(constraint, cont_domain, EvalEngine.get());
          }
        }
      }
    }
    return F.NIL;
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

    ISymbol temp = F.Dummy("x", F.Element(F.Slot1, F.Reals));
    f = f.subs(symbol, temp);
    symbol = temp;

    IExpr orig_f = f;
    IExpr period = F.NIL;

    if (f.isRelationalBinary()) {
      f = f.first().subtract(f.second());
    }
    EvalEngine engine = EvalEngine.get();
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

      if (f.isPower() && !f.base().equals(S.E)) {
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
      IAST factorized_nums = numbers.map(num -> S.Factor.of(num));
      IAST factors_num = factorized_nums.map(num -> num.asCoeffMul());
      if (factors_num.argSize() > 0) {
        IExpr term = factors_num.getPart(1, 2);
        if (factors_num.forAll(x -> x.second().equals(term))) {
          IExpr common_term = term;
          IAST coeffs = factors_num.map(x -> x.first());
          result = EvalEngine.get().evaluate(F.Times(coeffs.apply(S.PolynomialLCM), common_term));
        }
      }
    } else if (numbers.forAll(x -> x.isRational())) {
      result = EvalEngine.get().evaluate(numbers.apply(S.PolynomialLCM));
    }
    return result;
  }
}
