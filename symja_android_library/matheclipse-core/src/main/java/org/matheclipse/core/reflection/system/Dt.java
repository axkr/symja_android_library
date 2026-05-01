package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Dt extends AbstractFunctionOptionEvaluator {

  public Dt() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    IAST constantsList = option[0].makeList();
    IExpr f = ast.arg1();

    // Dt(f) -> total differential
    if (argSize == 1) {
      return totalDifferential(f, constantsList, engine);
    }

    // Dt(f, x1, x2, ...) -> successive derivatives
    if (argSize > 2) {
      IExpr temp = f;
      boolean evaluated = false;
      for (int i = 2; i <= argSize; i++) {
        IExpr xi = ast.get(i);
        IExpr nextDt = buildDt(temp, xi, constantsList);
        IExpr evalNext = engine.evaluateNIL(nextDt);
        if (evalNext.isPresent()) {
          temp = evalNext;
          evaluated = true;
        } else {
          temp = nextDt;
        }
      }
      if (evaluated) {
        if (temp.equals(ast)) {
          return F.NIL;
        }
        return temp;
      }
      return F.NIL;
    }

    IExpr x = ast.arg2();

    // Dt(f, {x, n})
    if (x.isList()) {
      IAST xList = (IAST) x;
      if (xList.isAST2()) {
        IExpr var = xList.arg1();
        IExpr nExpr = xList.arg2();
        if (nExpr.isInteger()) {
          int n = engine.evaluate(nExpr).toIntDefault();
          if (n >= 0) {
            IExpr temp = f;
            for (int i = 0; i < n; i++) {
              IExpr nextDt = buildDt(temp, var, constantsList);
              IExpr evalNext = engine.evaluateNIL(nextDt);
              temp = evalNext.isPresent() ? evalNext : nextDt;
            }
            return temp;
          }
        }
      }
      return F.NIL;
    }

    return totalDerivative(f, x, constantsList, engine);
  }

  /**
   * Evaluate the total derivative Dt(f, x).
   */
  private IExpr totalDerivative(final IExpr f, final IExpr x, final IAST constants,
      EvalEngine engine) {
    if (isConstant(f, constants)) {
      return F.C0;
    }
    if (f.equals(x)) {
      return F.C1;
    }
    if (f.isSymbol()) {
      return F.NIL; // Keeps unevaluated Dt(y, x)
    }

    if (f.isAST()) {
      IAST ast = (IAST) f;
      IExpr head = ast.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Plus:
            return ast.mapThread(buildDt(F.Slot1, x, constants), 1);

          case ID.Times: {
            IASTAppendable plus = F.PlusAlloc(ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              IASTAppendable times = F.TimesAlloc(ast.argSize());
              for (int j = 1; j <= ast.argSize(); j++) {
                times.append(
                    i == j ? engine.evaluate(buildDt(ast.get(j), x, constants)) : ast.get(j));
              }
              plus.append(times);
            }
            return engine.evaluate(plus);
          }

          case ID.Power: {
            IExpr a = ast.base();
            IExpr b = ast.exponent();
            IExpr dtA = engine.evaluate(buildDt(a, x, constants));
            IExpr dtB = engine.evaluate(buildDt(b, x, constants));
            IExpr term1 = F.Times(dtB, F.Log(a));
            IExpr term2 = F.Times(b, dtA, F.Power(a, F.CN1));
            return engine.evaluate(F.Times(ast, F.Plus(term1, term2)));
          }

          case ID.Dt: {
            // Dt(Dt(y, x), x) collapses to Dt(y, {x, 2}).
            IAST dtAst = (IAST) f;
            if (dtAst.argSize() >= 2) {
              IExpr dtX = dtAst.arg2();
              if (dtX.isList() && dtX.isAST2()) {
                IAST list = (IAST) dtX;
                if (list.arg1().equals(x) && list.arg2().isInteger()) {
                  int n = list.arg2().toIntDefault();
                  IASTAppendable newDt = F.ast(S.Dt);
                  newDt.append(dtAst.arg1());
                  newDt.append(F.List(x, F.ZZ(n + 1)));
                  for (int j = 3; j <= dtAst.argSize(); j++) {
                    newDt.append(dtAst.get(j));
                  }
                  return newDt;
                } else if (!list.arg1().equals(x)) {
                  return F.C0;
                }
              } else if (!dtX.isList()) {
                if (dtX.equals(x)) {
                  IASTAppendable newDt = F.ast(S.Dt);
                  newDt.append(dtAst.arg1());
                  newDt.append(F.List(x, F.ZZ(2)));
                  for (int j = 3; j <= dtAst.argSize(); j++) {
                    newDt.append(dtAst.get(j));
                  }
                  return newDt;
                } else {
                  return F.C0;
                }
              }
            }
            return F.NIL;
          }
        }
      }

      // General Chain Rule calculation
      int argSize = ast.argSize();
      IASTAppendable plus = F.PlusAlloc(argSize);
      for (int i = 1; i <= argSize; i++) {
        IExpr expr = ast.get(i);
        IExpr dtExpr = engine.evaluate(buildDt(expr, x, constants));
        if (!dtExpr.isZero()) {
          IAST partial = createDerivative(i, head, ast);
          plus.append(F.Times(dtExpr, partial));
        }
      }
      return engine.evaluate(plus);
    }

    return F.NIL;
  }

  /**
   * Evaluate the total differential Dt(f).
   */
  private IExpr totalDifferential(final IExpr f, final IAST constants, EvalEngine engine) {
    if (isConstant(f, constants)) {
      return F.C0;
    }
    if (f.isSymbol()) {
      return F.NIL;
    }

    if (f.isAST()) {
      IAST ast = (IAST) f;
      IExpr head = ast.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Plus:
            return ast.mapThread(buildDt(F.Slot1, F.NIL, constants), 1);

          case ID.Times: {
            IASTAppendable plus = F.PlusAlloc(ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              IASTAppendable times = F.TimesAlloc(ast.argSize());
              for (int j = 1; j <= ast.argSize(); j++) {
                times.append(
                    i == j ? engine.evaluate(buildDt(ast.get(j), F.NIL, constants)) : ast.get(j));
              }
              plus.append(times);
            }
            return engine.evaluate(plus);
          }

          case ID.Power: {
            IExpr a = ast.base();
            IExpr b = ast.exponent();
            IExpr dtA = engine.evaluate(buildDt(a, F.NIL, constants));
            IExpr dtB = engine.evaluate(buildDt(b, F.NIL, constants));
            IExpr term1 = F.Times(dtB, F.Log(a));
            IExpr term2 = F.Times(b, dtA, F.Power(a, F.CN1));
            return engine.evaluate(F.Times(ast, F.Plus(term1, term2)));
          }

          case ID.Dt: {
            return F.NIL; // Keeps nested differentials safely unevaluated.
          }
        }
      }

      int argSize = ast.argSize();
      IASTAppendable plus = F.PlusAlloc(argSize);
      for (int i = 1; i <= argSize; i++) {
        IExpr expr = ast.get(i);
        IExpr dtExpr = engine.evaluate(buildDt(expr, F.NIL, constants));
        if (!dtExpr.isZero()) {
          IAST partial = createDerivative(i, head, ast);
          plus.append(F.Times(dtExpr, partial));
        }
      }
      return engine.evaluate(plus);
    }

    return F.NIL;
  }

  /**
   * Checks if an expression is considered constant per Dt definitions.
   */
  private boolean isConstant(IExpr expr, IAST constants) {
    if (expr.isNumber()) {
      return true;
    }
    if (expr.isSymbol()) {
      ISymbol sym = (ISymbol) expr;
      if (sym.isConstantAttribute()) {
        return true;
      }
      if (constants.contains(sym)) {
        return true;
      }
    }
    if (expr.isAST()) {
      return isConstant(expr.head(), constants);
    }
    return false;
  }

  /**
   * Helper utility for creating nested `Dt` nodes to feed back into the evaluation loop.
   */
  private IAST buildDt(IExpr expr, IExpr x, IAST constants) {
    IASTAppendable dt = F.ast(S.Dt);
    dt.append(expr);
    if (x.isPresent()) {
      dt.append(x);
    }
    if (constants.isList() && !constants.isEmpty()) {
      dt.append(F.Rule(S.Constants, constants));
    }
    return dt;
  }

  /**
   * Create Derivative(..., 1, ...)(header)(arg1, arg2, ...)
   */
  private static IAST createDerivative(final int pos, final IExpr header, final IAST args) {
    final int argSize = args.argSize();
    IASTAppendable derivativeHead1 = F.ast(S.Derivative, argSize);
    for (int i = 1; i <= argSize; i++) {
      derivativeHead1.append(i == pos ? F.C1 : F.C0);
    }
    IASTAppendable derivativeHead2 = F.ast(derivativeHead1);
    derivativeHead2.append(header);
    IASTAppendable derivativeAST = F.ast(derivativeHead2, argSize);
    derivativeAST.appendArgs(args);
    return derivativeAST;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, S.Constants, F.CEmptyList);
  }
}