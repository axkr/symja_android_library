package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class RegionBounds extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
          case ID.Line:
          case ID.Triangle:
          case ID.Polygon:
          case ID.Simplex:
            return pointBounds(reg, engine);
          case ID.HalfLine:
            return halfLineBounds(reg, engine);
          case ID.InfiniteLine:
            return infiniteLineBounds(reg, engine);
          case ID.Disk:
          case ID.Circle:
          case ID.Ball:
          case ID.Sphere:
            return ballBounds(reg, engine);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxBounds(reg, engine);
        }
      }
    }
    return F.NIL;
  }

  private IExpr pointBounds(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      if (reg.head() == S.Triangle || reg.head() == S.Simplex) {
        return F.List(F.List(F.C0, F.C1), F.List(F.C0, F.C1));
      }
      return F.NIL;
    }
    if (reg.argSize() >= 1 && reg.arg1().isList()) {
      return engine.evaluate(F.CoordinateBounds(reg.arg1()));
    }
    return F.NIL;
  }

  private IExpr boxBounds(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    int dim = (head == S.Rectangle) ? 2 : 3;
    IExpr min = F.NIL;
    IExpr max = F.NIL;
    if (reg.argSize() == 0) {
      min = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
      max = (dim == 2) ? F.List(F.C1, F.C1) : F.List(F.C1, F.C1, F.C1);
    } else if (reg.argSize() == 1) {
      IExpr p = reg.arg1();
      if (p.isList()) {
        min = p;
        IAST ptAST = (IAST) p;
        IASTAppendable maxList = F.ListAlloc(ptAST.argSize());
        for (int i = 1; i <= ptAST.argSize(); i++) {
          maxList.append(engine.evaluate(F.Plus(ptAST.get(i), F.C1)));
        }
        max = maxList;
      }
    } else if (reg.argSize() >= 2) {
      min = reg.arg1();
      max = reg.arg2();
    }
    if (min.isList() && max.isList() && min.argSize() == max.argSize()) {
      IASTAppendable res = F.ListAlloc(min.argSize());
      IAST minL = (IAST) min;
      IAST maxL = (IAST) max;
      for (int i = 1; i <= min.argSize(); i++) {
        res.append(F.List(engine.evaluate(F.Min(minL.get(i), maxL.get(i))),
            engine.evaluate(F.Max(minL.get(i), maxL.get(i)))));
      }
      return res;
    }
    return F.NIL;
  }

  private IExpr ballBounds(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    int dim = (head == S.Disk || head == S.Circle) ? 2 : 3;
    IExpr c = F.NIL;
    IExpr r = F.C1;
    if (reg.argSize() == 0) {
      c = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
    } else if (reg.argSize() >= 1) {
      c = reg.arg1();
      if (reg.argSize() >= 2) {
        r = reg.arg2();
      }
    }
    if (c.isList()) {
      IAST cL = (IAST) c;
      IASTAppendable res = F.ListAlloc(cL.argSize());
      for (int i = 1; i <= cL.argSize(); i++) {
        IExpr ri = r;
        if (r.isList()) {
          ri = ((IAST) r).get(i);
        }
        res.append(F.List(engine.evaluate(F.Subtract(cL.get(i), ri)),
            engine.evaluate(F.Plus(cL.get(i), ri))));
      }
      return res;
    }
    return F.NIL;
  }

  private IExpr halfLineBounds(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList2()) {
      IAST pts = (IAST) reg.arg1();
      IExpr p1 = pts.arg1();
      IExpr p2 = pts.arg2();
      if (p1.isList() && p2.isList() && p1.argSize() == p2.argSize()) {
        IAST p1L = (IAST) p1;
        IAST p2L = (IAST) p2;
        IASTAppendable res = F.ListAlloc(p1L.argSize());
        for (int i = 1; i <= p1L.argSize(); i++) {
          IExpr diff = engine.evaluate(F.Subtract(p2L.get(i), p1L.get(i)));
          if (diff.isPositiveResult()) {
            res.append(F.List(p1L.get(i), S.Infinity));
          } else if (diff.isNegativeResult()) {
            res.append(F.List(F.CNInfinity, p1L.get(i)));
          } else if (diff.isZero()) {
            res.append(F.List(p1L.get(i), p1L.get(i)));
          } else {
            return F.NIL;
          }
        }
        return res;
      }
    }
    return F.NIL;
  }

  private IExpr infiniteLineBounds(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList2()) {
      IAST pts = (IAST) reg.arg1();
      IExpr p1 = pts.arg1();
      IExpr p2 = pts.arg2();
      if (p1.isList() && p2.isList() && p1.argSize() == p2.argSize()) {
        IAST p1L = (IAST) p1;
        IAST p2L = (IAST) p2;
        IASTAppendable res = F.ListAlloc(p1L.argSize());
        for (int i = 1; i <= p1L.argSize(); i++) {
          IExpr diff = engine.evaluate(F.Subtract(p2L.get(i), p1L.get(i)));
          if (!diff.isZero()) {
            res.append(F.List(F.CNInfinity, S.Infinity));
          } else {
            res.append(F.List(p1L.get(i), p1L.get(i)));
          }
        }
        return res;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
