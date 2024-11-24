package org.matheclipse.core.polynomials;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/** Generate the horner scheme for univariate polynomials */
public class HornerScheme {

  private TreeMap<IReal, IASTAppendable> map;

  public HornerScheme() {
    Comparator<IReal> comp = new Comparator<IReal>() {

      @Override
      public int compare(IReal arg0, IReal arg1) {
        if (arg0.isGT(arg1)) {
          return 1;
        }
        if (arg0.isLT(arg1)) {
          return -1;
        }
        return 0;
      }
    };
    map = new TreeMap<IReal, IASTAppendable>(comp);
  }

  public IAST generate(boolean numericMode, IAST poly, IExpr sym) {
    if (numericMode) {
      poly.forEach(x -> collectTermN(sym, x));
      // for (int i = 1; i < poly.size(); i++) {
      // collectTermN(sym, poly.get(i));
      // }
      IASTAppendable result = F.PlusAlloc(16);
      IAST startResult = result;
      IASTAppendable temp;
      IReal start = F.CD0;
      for (Iterator<IReal> iter = map.keySet().iterator(); iter.hasNext();) {
        IReal exponent = iter.next();
        IExpr coefficient = getCoefficient(exponent);
        if (exponent.isLT(F.CD1)) {
          if (exponent.compareTo(F.CD0) == 0) {
            result.append(coefficient);
          } else {
            result.append(F.Times(coefficient, F.Power(sym, exponent)));
          }
        } else {
          temp = F.TimesAlloc(2);
          IReal currentExponent = exponent.subtractFrom(start);
          if (currentExponent.equals(F.CD1)) {
            temp.append(sym);
          } else {
            temp.append(F.Power(sym, currentExponent));
          }
          result.append(temp);
          result = F.PlusAlloc(16);
          temp.append(result);
          result.append(coefficient);
          start = exponent;
        }
      }
      return startResult;
    } else {
      poly.forEach(x -> collectTerm(sym, x));
      // for (int i = 1; i < poly.size(); i++) {
      // collectTerm(sym, poly.get(i));
      // }
      IASTAppendable result = F.PlusAlloc(16);
      IAST startResult = result;
      IASTAppendable temp;
      IReal start = F.C0;
      for (Iterator<IReal> iter = map.keySet().iterator(); iter.hasNext();) {
        IReal exponent = iter.next();
        IExpr coefficient = getCoefficient(exponent);
        if (exponent.isLT(F.C1)) {
          if (exponent.compareTo(F.C0) == 0) {
            result.append(coefficient);
          } else {
            result.append(F.Times(coefficient, F.Power(sym, exponent)));
          }
        } else {
          temp = F.TimesAlloc(2);
          IReal currentExponent = exponent.subtractFrom(start);
          if (currentExponent.equals(F.C1)) {
            temp.append(sym);
          } else {
            temp.append(F.Power(sym, currentExponent));
          }
          result.append(temp);
          result = F.PlusAlloc(16);
          temp.append(result);
          result.append(coefficient);
          start = exponent;
        }
      }
      return startResult;
    }
  }

  private IExpr getCoefficient(IReal key) {
    IAST value = map.get(key);
    IExpr coefficient;
    if (value.isAST(S.Plus, 2)) {
      coefficient = value.arg1();
      if (coefficient.isAST(S.Times, 2)) {
        coefficient = coefficient.first();
      }
    } else {
      coefficient = value;
    }
    return coefficient;
  }

  private void collectTerm(IExpr sym, IExpr expr) {
    if (expr instanceof IAST) {
      IAST term = (IAST) expr;
      if (term.isTimes()) {
        for (int i = 1; i < term.size(); i++) {
          if (sym.equals(term.get(i))) {
            IAST temp = F.ast(term, S.Times, false, i, i + 1);
            addToMap(F.C1, temp);
            return;
          } else if (term.get(i).isPower()) {
            IAST pow = (IAST) term.get(i);
            if (pow.arg1().equals(sym) && pow.arg2() instanceof IReal) {
              IAST temp = F.ast(term, S.Times, false, i, i + 1);
              addToMap((IReal) pow.arg2(), temp);
              return;
            }
          }
        }
      } else if (term.isPower()) {
        if (term.arg1().equals(sym) && term.arg2() instanceof IReal) {
          addToMap((IReal) term.arg2(), F.C1);
          return;
        }
      }
    } else if (expr instanceof ISymbol && expr.equals(sym)) {
      addToMap(F.C1, F.C1);
      return;
    }
    addToMap(F.C0, expr);
  }

  private void collectTermN(IExpr sym, IExpr expr) {
    if (expr instanceof IAST) {
      IAST term = (IAST) expr;
      if (term.isTimes()) {
        for (int i = 1; i < term.size(); i++) {
          if (sym.equals(term.get(i))) {
            IAST temp = F.ast(term, S.Times, false, i, i + 1);
            addToMap(F.CD1, temp);
            return;
          } else if (term.get(i).isPower()) {
            IAST pow = (IAST) term.get(i);
            if (pow.arg1().equals(sym) && pow.arg2().isReal()) {
              IAST temp = F.ast(term, S.Times, false, i, i + 1);
              addToMap((IReal) pow.arg2(), temp);
              return;
            }
          }
        }
      } else if (term.isPower()) {
        if (term.arg1().equals(sym) && term.arg2().isReal()) {
          addToMap((IReal) term.arg2(), F.CD1);
          return;
        }
      }
    } else if (expr instanceof ISymbol && expr.equals(sym)) {
      addToMap(F.CD1, F.CD1);
      return;
    }
    addToMap(F.CD0, expr);
  }

  public IAST addToMap(final IReal key, final IExpr value) {
    IASTAppendable temp = map.get(key);
    if (temp == null) {
      temp = F.PlusAlloc(8);
      temp.append(value);
      map.put(key, temp);
    } else {
      temp.append(value);
    }
    return temp;
  }
}
