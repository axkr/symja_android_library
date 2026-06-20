package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class QPochhammer extends AbstractFunctionEvaluator implements IFunctionExpand {
  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.argSize() == 3 && ast.arg3().isInteger()) {
      int n = ast.arg3().toIntDefault();
      if (F.isPresent(n)) {
        IExpr a = ast.arg1();
        IExpr q = ast.arg2();

        if (n > 0) {
          // Expansion for positive integers: Product(1 - a*q^k, {k, 0, n-1})
          IASTAppendable result = F.TimesAlloc();
          for (int k = 0; k < n; k++) {
            IExpr term;
            if (k == 0) {
              term = a;
            } else if (k == 1) {
              term = F.Times(a, q);
            } else {
              term = F.Times(a, F.Power(q, F.ZZ(k)));
            }
            result.append(F.Subtract(F.C1, term));
          }
          return result;

        } else if (n < 0) {
          // Expansion for negative integers: 1 / Product(1 - a/q^k, {k, 1, -n})
          int max = -n;
          IASTAppendable result = F.TimesAlloc();
          for (int k = 1; k <= max; k++) {
            IExpr term = k == 1 ? F.Times(a, F.Power(q, F.CN1)) : F.Times(a, F.Power(q, F.ZZ(-k)));
            result.append(F.Subtract(F.C1, term));
          }
          return F.Power(result, F.CN1);

        } else {
          // n == 0
          return F.C1;
        }
      }
    }
    return F.NIL;
  }

  public QPochhammer() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();

    IExpr a = ast.arg1();

    if (argSize == 1) {
      // QPochhammer(a) is equivalent to QPochhammer(a, a)
      return F.binaryAST2(S.QPochhammer, a, a);
    }

    IExpr q = ast.arg2();

    if (argSize == 2) {
      if (a.isZero()) {
        return F.C1;
      }
      if (q.isZero()) {
        return F.Subtract(F.C1, a);
      }
      if (a.isOne() && q.isOne()) {
        return F.C0;
      }
      return F.NIL;
    }

    IExpr n = ast.arg3();

    if (n.isZero() || a.isZero()) {
      return F.C1;
    }

    if (n.isInteger()) {
      int nVal = n.toIntDefault();

      // Expansion for positive integers
      if (nVal > 0 && nVal < 100000) {
        IASTAppendable result = F.TimesAlloc();
        for (int k = 0; k < nVal; k++) {
          IExpr term;
          if (k == 0) {
            term = a;
          } else if (k == 1) {
            term = F.Times(a, q);
          } else {
            term = F.Times(a, F.Power(q, F.ZZ(k)));
          }
          result.append(F.Subtract(F.C1, term));
        }
        return result;

        // Expansion for negative integers
      } else if (nVal < 0 && nVal > -100000) {
        int max = -nVal;
        IASTAppendable result = F.TimesAlloc();
        for (int k = 1; k <= max; k++) {
          IExpr term = k == 1 ? F.Times(a, F.Power(q, F.CN1)) : F.Times(a, F.Power(q, F.ZZ(-k)));
          result.append(F.Subtract(F.C1, term));
        }
        return F.Power(result, F.CN1);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }

}
