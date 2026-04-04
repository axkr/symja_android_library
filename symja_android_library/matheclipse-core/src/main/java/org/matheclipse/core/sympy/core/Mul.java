package org.matheclipse.core.sympy.core;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

public class Mul {

  public static Pair as_coeff_mul(IAST mulAST, boolean rational, IExpr... deps) {
    if (deps.length > 0) {
      IASTAppendable l1 = F.ListAlloc(mulAST.size());
      IASTAppendable l2 = F.TimesAlloc(mulAST.size());

      for (int i = 1; i < mulAST.size(); i++) {
        IExpr arg = mulAST.get(i);
        if (arg.has(deps)) {
          l1.append(arg);
        } else {
          l2.append(arg);
        }
      }
      // _new_rawargs(*l2) in SymPy is equivalent to Times(*l2)
      return F.pair(l2.oneIdentity1(), l1);
    }
    IExpr first = mulAST.arg1();
    if (first.isNumber()) {
      // If it's a rational, always extract it
      if (first.isRational()) {
        return F.pair(first, mulAST.subList(2).apply(S.List));
      }

      // If not rational=True, extract any number (including Complex/Real)
      if (!rational) {
        return F.pair(first, mulAST.subList(2).apply(S.List));
      }

      // If rational=True but the number is negative, extract -1
    }
    if (first.isNegative() || first.isNegativeInfinity()) {
      IASTAppendable rest = F.ListAlloc(mulAST.size());
      rest.append(first.negate());
      rest.appendAll(mulAST, 2, mulAST.size());
      return F.pair(F.CN1, rest);
    }

    // Handle cases where the expression could extract a minus sign
    // but isn't a explicit Times(Number, ...)
    return F.pair(F.C1, mulAST.apply(S.List));
  }

  public static Pair as_coeff_mul(IAST mulAST, IExpr... deps) {
    if (deps.length > 0) {
      IASTAppendable l1 = F.ListAlloc(mulAST.size());
      IASTAppendable l2 = F.ListAlloc(mulAST.size());
      for (int i = 1; i < mulAST.size(); i++) {
        IExpr arg = mulAST.get(i);
        if (arg.has(deps)) {
          l1.append(arg);
        } else {
          l2.append(arg);
        }
      }
      return F.pair(F.Times(l2), l1);
    }

    IExpr first = mulAST.arg1();
    if (first.isNumber()) {
      if (first.isRational()) {
        return F.pair(first, mulAST.subList(2));
      } else if (first.isNegative()) {
        IASTAppendable rest = F.ListAlloc(mulAST.size());
        rest.append(first.negate());
        rest.appendAll(mulAST, 2, mulAST.size());
        return F.pair(F.CN1, rest);
      }
    }
    return F.pair(F.C1, mulAST.apply(F.List));
  }

  public static Pair asCoeffMul(IAST mulAST, boolean rational) {
    IExpr first = mulAST.arg1();
    if (first.isNumber()) {
      if (rational) {
        if (first.isRational()) {
          return F.pair(first, mulAST.subList(2).oneIdentity1());
        }
        // If rational=True and first is not rational, coefficient is 1
        return F.pair(F.C1, mulAST);
      }

      // Default logic (rational=False)
      return F.pair(first, mulAST.subList(2).oneIdentity1());
    }
    if (first.isNegativeInfinity()) {
      return F.pair(F.CN1, mulAST.setAtCopy(1, F.CInfinity));
    }
    return F.pair(F.C1, mulAST);
  }

  public static IExpr keepCoeff(IExpr coeff, IExpr factors) {
    return keepCoeff(coeff, factors, true, false);
  }

  public static IExpr keepCoeff(IExpr coeff, IExpr factors, boolean clear, boolean sign) {
    // """Return ``coeff*factors`` unevaluated if necessary.
    // If ``clear`` is False, do not keep the coefficient as a factor
    // if it can be distributed on a single factor such that one or
    // more terms will still have integer coefficients.
    // If ``sign`` is True, allow a coefficient of -1 to remain factored out.
    // Examples
    // ========
    // >>> from sympy.core.mul import _keep_coeff
    // >>> from sympy.abc import x, y
    // >>> from sympy import S
    // >>> _keep_coeff(S.Half, x + 2)
    // (x + 2)/2
    // >>> _keep_coeff(S.Half, x + 2, clear=False)
    // x/2 + 1
    // >>> _keep_coeff(S.Half, (x + 2)*y, clear=False)
    // y*(x + 2)/2
    // >>> _keep_coeff(S(-1), x + y)
    // -x - y
    // >>> _keep_coeff(S(-1), x + y, sign=True)
    // -(x + y)
    // """

    if (!coeff.isNumber()) {
      if (factors.isNumber()) {
        IExpr t = factors;
        factors = coeff;
        coeff = t;
      } else {
        return coeff.times(factors);
      }
    }
    if (factors.isOne()) {
      return coeff;
    }
    if (coeff.isOne()) {
      return factors;
    } else if (!sign && coeff.isMinusOne()) {
      return factors.negate();
    } else if (factors.isPlus()) {
      if (!clear && coeff.isFraction() && !((IRational) coeff).denominator().isOne()) {
        IAST plusAST = (IAST) factors;
        IASTAppendable args = F.ListAlloc(plusAST.size());
        boolean isInteger = false;
        for (int i = 1; i < plusAST.size(); i++) {
          IExpr expr = plusAST.get(i);
          Pair pairCoeffMul = expr.asCoeffMul();
          IExpr keepCoeff = keepCoeff(pairCoeffMul.first(), coeff);
          if (keepCoeff.isInteger()) {
            isInteger = true;
          }
          args.append(F.pair(keepCoeff, pairCoeffMul.second()));
        }
        if (isInteger) {
          IASTAppendable result = F.PlusAlloc(args.argSize());
          for (int i = 1; i < args.size(); i++) {
            Pair p = (Pair) args.get(i);
            result.append(p.first().isOne() ? p.second() : F.Times(p.first(), p.second()));
          }
          return result;
        }
      }
      return F.Times(coeff, factors);
    } else if (factors.isTimes()) {
      IASTAppendable margs = ((IAST) factors).copyAppendable();
      if (margs.first().isNumber()) {
        margs.set(1, margs.first().times(coeff));
        if (margs.first().isOne()) {
          margs.remove(1);
        }
      } else {
        margs.append(1, coeff);
      }
      return margs;
    } else {
      IExpr m = coeff.times(factors);
      if (m.isNumber() && !factors.isNumber()) {
        return F.Times(coeff, factors);
      }
      return m;
    }
  }

  public Mul() {

  }
}
