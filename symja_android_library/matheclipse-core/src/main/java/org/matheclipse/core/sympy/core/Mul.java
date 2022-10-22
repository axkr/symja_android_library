package org.matheclipse.core.sympy.core;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

public class Mul {

  public Mul() {

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

}
