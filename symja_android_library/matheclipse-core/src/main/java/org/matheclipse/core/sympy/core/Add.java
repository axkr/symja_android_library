package org.matheclipse.core.sympy.core;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Add {

  public static Pair as_coeff_add(IAST addAST, IExpr... deps) {
    if (deps.length > 0) {
      IASTAppendable dependent = F.ListAlloc(addAST.size());
      IASTAppendable independent = F.PlusAlloc(addAST.size());
      for (int i = 1; i < addAST.size(); i++) {
        IExpr arg = addAST.get(i);
        if (arg.hasFree(deps)) {
          dependent.append(arg);
        } else {
          independent.append(arg);
        }
      }
      return F.pair(independent.oneIdentity0(), dependent);
    }

    // Default logic: return head (slot 1 if number) and rest
    IExpr first = addAST.arg1();
    if (!first.isZero()) {
      IASTAppendable rest = F.ListAlloc(addAST.size());
      rest.appendAll(addAST, 2, addAST.size());
      return F.pair(first, rest);
    }
    return F.pair(F.C0, addAST.apply(F.List));
  }

  public static Pair asCoeffAdd(IAST addAST, boolean rational) {
    IExpr first = addAST.arg1();
    // SymPy logic: if rational is True, only extract if arg1 is Rational
    if (first.isNumber()) {
      if (rational) {
        if (!first.isRational()) {
          return F.pair(F.C0, addAST);
        }
      }
      return F.pair(first, addAST.subList(2).oneIdentity0());

    }
    return F.pair(F.C0, addAST);
  }
}
