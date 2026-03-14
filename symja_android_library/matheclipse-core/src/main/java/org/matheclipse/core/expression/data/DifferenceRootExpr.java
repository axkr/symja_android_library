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

  /**
   * @param function Function[{y, n}, {eq1, eq2, ...}]
   */
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
    ISymbol y = ((IAST) vars).get(1).isSymbol() ? (ISymbol) ((IAST) vars).get(1) : null;
    ISymbol n = ((IAST) vars).get(2).isSymbol() ? (ISymbol) ((IAST) vars).get(2) : null;

    if (y == null || n == null) {
      return F.NIL;
    }

    Map<Integer, IExpr> initialValues = new HashMap<>();
    IExpr[] recurrenceRef = new IExpr[1];

    parseEquations(body, y, n, initialValues, recurrenceRef);

    if (initialValues.containsKey(k)) {
      return initialValues.get(k);
    }

    if (recurrenceRef[0] == null) {
      return F.NIL;
    }

    IExpr eq = recurrenceRef[0];
    // Analyze shifts
    List<Integer> shifts = new ArrayList<>();
    collectShifts(eq, y, n, shifts);

    if (shifts.isEmpty()) {
      return F.NIL;
    }

    int maxShift = Integer.MIN_VALUE;
    int minShift = Integer.MAX_VALUE;
    for (int s : shifts) {
      maxShift = Math.max(maxShift, s);
      minShift = Math.min(minShift, s);
    }

    // Determine range
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
      // No initial values?
      return F.NIL;
    }

    if (k > maxInitial) {
      // headTerm = y[n + maxShift]
      IExpr headTerm = (maxShift == 0) ? F.unary(y, n) : F.unary(y, F.Plus(n, F.ZZ(maxShift)));

      // Solve for headTerm
      IAST solveRes = (IAST) engine.evaluate(F.Solve(eq, headTerm));
      if (solveRes.size() < 2 || !solveRes.arg1().isList()) {
        return F.NIL;
      }
      // {{y[n] -> rhs}}
      IAST rules = (IAST) solveRes.arg1();
      if (rules.size() < 2 || !rules.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr rhs = ((IAST) rules.arg1()).arg2();

      // cache of solved n-substitutions
      for (int i = maxInitial + 1; i <= k; i++) {
        final int currentN = i - maxShift; // n value for this step

        // Substitute n -> i in rhs
        IExpr step = F.subst(rhs, x -> {
          if (x.equals(n)) {
            return F.ZZ(currentN);
          }
          return F.NIL;
        });

        // Evaluate indices like n-1 -> i-1
        step = engine.evaluate(step);

        // Evaluate y[j] in step
        IExpr evaluatedStep = F.subst(step, x -> {
          if (x.isAST(y, 2)) {
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

        // Evaluate to simplify (arithmetic)
        IExpr val = engine.evaluate(F.Expand(evaluatedStep));
        initialValues.put(i, val);
      }
    } else if (k < minInitial) {
      // tailTerm = y[n + minShift]
      IExpr tailTerm = (minShift == 0) ? F.unary(y, n) : F.unary(y, F.Plus(n, F.ZZ(minShift)));

      // Solve for tailTerm
      IAST solveRes = (IAST) engine.evaluate(F.Solve(eq, tailTerm));
      if (solveRes.size() < 2 || !solveRes.arg1().isList()) {
        return F.NIL;
      }
      // {{y[n] -> rhs}}
      IAST rules = (IAST) solveRes.arg1();
      if (rules.size() < 2 || !rules.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr rhs = ((IAST) rules.arg1()).arg2();

      // cache of solved n-substitutions
      for (int i = minInitial - 1; i >= k; i--) {
        final int currentN = i - minShift; // n value for this step

        // Substitute n -> i in rhs
        IExpr step = F.subst(rhs, x -> {
          if (x.equals(n)) {
            return F.ZZ(currentN);
          }
          return F.NIL;
        });

        // Evaluate indices like n-1 -> i-1
        step = engine.evaluate(step);

        // Evaluate y[j] in step
        IExpr evaluatedStep = F.subst(step, x -> {
          if (x.isAST(y, 2)) {
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

        // Evaluate to simplify (arithmetic)
        IExpr val = engine.evaluate(F.Expand(evaluatedStep));
        initialValues.put(i, val);
      }
    } else {
      // k is in range but not in map or something else
      return F.NIL;
    }

    return initialValues.get(k);
  }

  private void parseEquations(IExpr body, ISymbol y, ISymbol n, Map<Integer, IExpr> initialValues,
      IExpr[] recurrenceRef) {
    if (body.isList() || body.isAST(S.And)) {
      IAST list = (IAST) body;
      for (int i = 1; i < list.size(); i++) {
        parseEquation(list.get(i), y, n, initialValues, recurrenceRef);
      }
    } else {
      parseEquation(body, y, n, initialValues, recurrenceRef);
    }
  }

  private void parseEquation(IExpr eq, ISymbol y, ISymbol n, Map<Integer, IExpr> initialValues,
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
        // Assuming n + int or int + n
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