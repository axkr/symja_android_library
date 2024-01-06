package org.matheclipse.core.sympy.solvers;

import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Decompogen {
  public static IAST decompogen(IExpr f, ISymbol symbol) {
    if (f.isRelationalBinary()) {
      ArgumentTypeException.throwArg(f, symbol);
    }
    if (f.isFree(symbol)) {
      return F.List(f);
    }

    IASTAppendable result = F.ListAlloc();
    result.append(f);
    if (f.isAST()) {
      IAST function = (IAST) f;
      IExpr arg1 = F.NIL;
      if (function.argSize() == 1) {
        arg1 = function.arg1();
      } else if (function.isExp()) {
        arg1 = function.exponent();
      }
      if (arg1.isPresent()) {
        if (arg1.equals(symbol)) {
          return F.List(f);
        }
        IAST decompogen = decompogen(arg1, symbol);
        if (decompogen.isPresent()) {
          result.set(1, f.subs(arg1, symbol));
          result.appendArgs(decompogen);
          return result;
        } else {
          result.set(1, f.subs(arg1, symbol));
          result.append(arg1);
          return result;
        }
        // if f.is_Pow and f.base == S.Exp1:
        // arg = f.exp
        // else:
        // arg = f.args[0]
        // if arg == symbol:
        // return [f]
        // return [f.subs(arg, symbol)] + decompogen(arg, symbol)
      }

      if (function.isAST(S.Min) || function.isAST(S.Max)) {
        IASTAppendable args = function.copyAppendable();
        IExpr d0 = F.NIL;
        IAST d = F.NIL;
        for (int i = 1; i < function.size(); i++) {
          IExpr a = function.get(i);
          if (!a.hasFree(symbol)) {
            continue;
          }
          d = decompogen(a, symbol);
          if (d.isPresent()) {
            if (d.argSize() == 1) {
              d = F.list(symbol, d.toArray(1));
            }
            if (d0.isNIL()) {
              d0 = d.copyFrom(2);
            } else if (!d.copyFrom(2).equals(d0)) {
              // # decomposition is not the same for each arg:
              // # mark as having no decomposition
              d = F.List(symbol);
              break;
            }
            args.set(i, d.first());
          }

        }

        if (d.isPresent() && d.arg1().equals(symbol)) {
          return F.List(function);
        }
        return F.List(args, d0);
        // args = list(f.args)
        // d0 = None
        // for i, a in enumerate(args):
        // if not a.has_free(symbol):
        // continue
        // d = decompogen(a, symbol)
        // if len(d) == 1:
        // d = [symbol] + d
        // if d0 is None:
        // d0 = d[1:]
        // elif d[1:] != d0:
        // # decomposition is not the same for each arg:
        // # mark as having no decomposition
        // d = [symbol]
        // break
        // args[i] = d[0]
        // if d[0] == symbol:
        // return [f]
        // return [f.func(*args)] + d0
      }

      // TODO improve for polynomials
      if (function.isPlus()) {
        boolean evaled = false;
        for (int i = 1; i < function.size(); i++) {
          IExpr arg = function.get(i);
          if (arg.isAST() && !arg.isFree(symbol)) {
            IAST decompogen = decompogen(arg, symbol);
            if (decompogen.isPresent()) {
              // System.out.println(decompogen);
              function = (IAST) function.subs(decompogen.first(), symbol);
              result.append(decompogen.first());
              evaled = true;
            }
          }
        }
        if (evaled) {
          result.set(1, function);
          return result;
        }
        return F.NIL;
      } else if (function.isTimes()) {
        boolean evaled = false;
        for (int i = 1; i < function.size(); i++) {
          IExpr arg = function.get(i);
          if (arg.isAST() && !arg.isFree(symbol)) {
            IAST decompogen = decompogen(arg, symbol);
            if (decompogen.isPresent()) {
              // System.out.println(decompogen);
              function = (IAST) function.subs(decompogen.first(), symbol);
              result.append(decompogen.first());
              evaled = true;
            }
          }
        }
        if (evaled) {
          result.set(1, function);
          return result;
        }
        return F.NIL;
      } else if (function.isPower()) {
        IExpr base = function.base();
        IExpr exponent = function.exponent();
        if (exponent.isReal()) {
          if (base.equals(symbol)) {
            return F.NIL;
          }
          if (base.isAST() && !base.isFree(symbol)) {
            IAST decompogen = decompogen(base, symbol);
            if (decompogen.isPresent()) {
              function = (IAST) function.subs(base, symbol);
              result.appendArgs(decompogen);
              result.set(1, function);
              return result;
            }
            if (!exponent.isInteger()) {
              function = (IAST) function.subs(base, symbol);
              result.append(base);
              result.set(1, function);
              return result;
            }
            return F.NIL;
          }
        }
      } else {
        boolean evaled = false;
        for (int i = 1; i < function.size(); i++) {
          IExpr arg = function.get(i);
          if (arg.isAST() && !arg.isFree(symbol)) {
            IAST decompogen = decompogen(arg, symbol);
            if (decompogen.isPresent()) {
              // System.out.println(decompogen);
              function = (IAST) function.subs(decompogen.first(), symbol);
              result.append(decompogen.first());
              evaled = true;
            }
          }
        }
        if (evaled) {
          result.set(1, function);
          return result;
        }
        return F.NIL;
      }
    }
    return result;

  }

  /**
   * 
   * @param g_s assumed to be a list of expressions which aren't free of symbol
   * @param symbol
   * @return
   */
  public static IAST compogen(IAST g_s, ISymbol symbol) {
    IAST result = F.NIL;
    if (g_s.argSize() > 0 && g_s.arg1().isAST()) {
      result = (IAST) g_s.arg1();
      if (g_s.argSize() == 1) {
        return result;
      }
      for (int i = 2; i < g_s.size(); i++) {
        IExpr temp = result.subs(g_s.get(i), symbol);
        if (!temp.isAST()) {
          return F.NIL;
        }
        result = (IAST) temp;
      }
    }
    return result;
  }
}
