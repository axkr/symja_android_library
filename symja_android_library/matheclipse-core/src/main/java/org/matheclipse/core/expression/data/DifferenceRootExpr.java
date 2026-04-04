package org.matheclipse.core.expression.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class DifferenceRootExpr extends DataExpr<IExpr> {

  private static final long serialVersionUID = 1L;

  public DifferenceRootExpr(IExpr function) {
    super(S.DifferenceRoot, function);
  }

  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        try {
          IExpr res = evaluateRecurrence(arg1.toIntDefault(), engine);
          if (res.isPresent()) {
            return res;
          }
        } catch (Exception ex) {
          // fall through
        }
      } else if (arg1.isList()) {
        return ((IAST) arg1).map(x -> {
          if (x.isInteger()) {
            try {
              IExpr res = evaluateRecurrence(x.toIntDefault(), engine);
              if (res.isPresent()) {
                return res;
              }
            } catch (Exception ex) {
              // fall through
            }
          }
          return F.unaryAST1(ast.head(), x);
        });
      }
    }
    return F.NIL;
  }

  private IExpr evaluateRecurrence(int k, EvalEngine engine) {
    if (!fData.isFunction()) {
      return F.NIL;
    }
    IAST function = (IAST) fData;
    IExpr vars = function.arg1();
    IExpr body = function.arg2();

    if (!vars.isList() || ((IAST) vars).size() != 3) {
      return F.NIL;
    }

    // Only `y` needs to be strictly a symbol to parse initial bounds
    ISymbol y = ((IAST) vars).get(1).isSymbol() ? (ISymbol) ((IAST) vars).get(1) : null;
    IExpr n = ((IAST) vars).get(2);

    if (y == null) {
      return F.NIL;
    }

    Map<Integer, IExpr> initialValues = new HashMap<>();
    IExpr[] recurrenceRef = new IExpr[1];

    parseEquations(body, y, initialValues, recurrenceRef);

    // If the index directly maps to an initial bound, return it WITHOUT symbolic verification
    if (initialValues.containsKey(k)) {
      return initialValues.get(k);
    }

    if (recurrenceRef[0] == null) {
      return F.NIL;
    }

    // If we need to iterate, `n` absolutely must be a valid un-substituted symbol
    if (!n.isSymbol()) {
      return F.NIL;
    }
    ISymbol nSym = (ISymbol) n;

    IExpr eq = recurrenceRef[0];

    List<Integer> shifts = new ArrayList<>();
    collectShifts(eq, y, nSym, shifts);

    if (shifts.isEmpty()) {
      return F.NIL;
    }

    int maxShift = Integer.MIN_VALUE;
    int minShift = Integer.MAX_VALUE;
    for (int s : shifts) {
      maxShift = Math.max(maxShift, s);
      minShift = Math.min(minShift, s);
    }

    int maxInitial = Integer.MIN_VALUE;
    int minInitial = Integer.MAX_VALUE;
    for (int key : initialValues.keySet()) {
      if (key > maxInitial) {
        maxInitial = key;
      }
      if (key < minInitial) {
        minInitial = key;
      }
    }

    if (maxInitial == Integer.MIN_VALUE) {
      return F.NIL;
    }

    ISymbol dY = F.Dummy("y");
    ISymbol dN = F.Dummy("n");

    IExpr safeEq = F.subst(eq, x -> {
      if (x.equals(y))
        return dY;
      if (x.equals(nSym))
        return dN;
      return F.NIL;
    });

    if (k > maxInitial) {
      IExpr headTerm = (maxShift == 0) ? F.unary(dY, dN) : F.unary(dY, F.Plus(dN, F.ZZ(maxShift)));

      IAST solveRes = (IAST) engine.evaluate(F.Solve(safeEq, headTerm));
      if (solveRes.size() < 2 || !solveRes.arg1().isList()) {
        return F.NIL;
      }
      IAST rules = (IAST) solveRes.arg1();
      if (rules.size() < 2 || !rules.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr rhs = ((IAST) rules.arg1()).arg2();

      for (int i = maxInitial + 1; i <= k; i++) {
        final int currentN = i - maxShift;

        IExpr step = F.subst(rhs, x -> {
          if (x.equals(dN))
            return F.ZZ(currentN);
          return F.NIL;
        });

        step = engine.evaluate(step);

        IExpr evaluatedStep = F.subst(step, x -> {
          if (x.isAST(dY, 2)) {
            IExpr arg = ((IAST) x).arg1();
            if (arg.isInteger()) {
              int idx = arg.toIntDefault();
              if (initialValues.containsKey(idx)) {
                return initialValues.get(idx);
              }
            }
          }
          return F.NIL;
        });

        IExpr val = engine.evaluate(F.Expand(evaluatedStep));
        initialValues.put(i, val);
      }
    } else if (k < minInitial) {
      IExpr tailTerm = (minShift == 0) ? F.unary(dY, dN) : F.unary(dY, F.Plus(dN, F.ZZ(minShift)));

      IAST solveRes = (IAST) engine.evaluate(F.Solve(safeEq, tailTerm));
      if (solveRes.size() < 2 || !solveRes.arg1().isList()) {
        return F.NIL;
      }
      IAST rules = (IAST) solveRes.arg1();
      if (rules.size() < 2 || !rules.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr rhs = ((IAST) rules.arg1()).arg2();

      for (int i = minInitial - 1; i >= k; i--) {
        final int currentN = i - minShift;

        IExpr step = F.subst(rhs, x -> {
          if (x.equals(dN))
            return F.ZZ(currentN);
          return F.NIL;
        });

        step = engine.evaluate(step);

        IExpr evaluatedStep = F.subst(step, x -> {
          if (x.isAST(dY, 2)) {
            IExpr arg = ((IAST) x).arg1();
            if (arg.isInteger()) {
              int idx = arg.toIntDefault();
              if (initialValues.containsKey(idx)) {
                return initialValues.get(idx);
              }
            }
          }
          return F.NIL;
        });

        IExpr val = engine.evaluate(F.Expand(evaluatedStep));
        initialValues.put(i, val);
      }
    } else {
      return F.NIL;
    }

    return initialValues.get(k);
  }

  private void parseEquations(IExpr body, ISymbol y, Map<Integer, IExpr> initialValues,
      IExpr[] recurrenceRef) {
    if (body.isList() || body.isAST(S.And)) {
      IAST list = (IAST) body;
      for (int i = 1; i < list.size(); i++) {
        parseEquation(list.get(i), y, initialValues, recurrenceRef);
      }
    } else {
      parseEquation(body, y, initialValues, recurrenceRef);
    }
  }

  private void parseEquation(IExpr eq, ISymbol y, Map<Integer, IExpr> initialValues,
      IExpr[] recurrenceRef) {
    if (eq.isAST(S.Equal, 3)) {
      IExpr lhs = ((IAST) eq).arg1();
      IExpr rhs = ((IAST) eq).arg2();
      if (lhs.isAST(y, 2)) {
        IExpr arg = ((IAST) lhs).arg1();
        if (arg.isInteger()) {
          initialValues.put(arg.toIntDefault(), rhs);
          return;
        }
      }
      recurrenceRef[0] = eq;
    }
  }

  private void collectShifts(IExpr expr, ISymbol y, ISymbol n, List<Integer> shifts) {
    if (expr.isAST(y, 2)) {
      IExpr arg = ((IAST) expr).arg1();
      if (arg.equals(n)) {
        shifts.add(0);
      } else if (arg.isAST(S.Plus, 3)) {
        IExpr a1 = ((IAST) arg).arg1();
        IExpr a2 = ((IAST) arg).arg2();
        if (a1.equals(n) && a2.isInteger()) {
          shifts.add(a2.toIntDefault());
        } else if (a2.equals(n) && a1.isInteger()) {
          shifts.add(a1.toIntDefault());
        }
      }
    } else if (expr.isAST()) {
      for (IExpr child : (IAST) expr) {
        collectShifts(child, y, n, shifts);
      }
    }
  }

  @Override
  public IExpr copy() {
    return new DifferenceRootExpr(fData);
  }
}