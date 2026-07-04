package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class RegionWithin extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 2)
      return F.NIL;

    IExpr reg1 = ast.arg1();
    IExpr reg2 = ast.arg2();

    if (reg1.isAST(S.Region, 1))
      reg1 = reg1.first();
    if (reg2.isAST(S.Region, 1))
      reg2 = reg2.first();

    if (reg1.equals(reg2))
      return S.True;

    if (reg2.isAST()) {
      IAST r2 = (IAST) reg2;
      IExpr head2 = r2.head();

      if (head2 == S.Point) {
        if (r2.argSize() == 1) {
          IExpr pt = r2.arg1();
          if (pt.isList() && pt.argSize() > 0 && ((IAST) pt).arg1().isList()) {
            IAST pts = (IAST) pt;
            for (int i = 1; i <= pts.argSize(); i++) {
              IExpr mem = engine.evaluate(F.RegionMember(reg1, pts.get(i)));
              if (mem.isFalse())
                return S.False;
              if (!mem.isTrue())
                return F.NIL;
            }
            return S.True;
          } else {
            return engine.evaluate(F.RegionMember(reg1, pt));
          }
        }
      }
    }

    if (reg1.isAST() && reg2.isAST()) {
      IAST r1 = (IAST) reg1;
      IAST r2 = (IAST) reg2;
      IExpr h1 = r1.head();
      IExpr h2 = r2.head();

      if ((h1 == S.Disk || h1 == S.Ball) && (h2 == S.Disk || h2 == S.Ball)) {
        if (h1.equals(h2))
          return ballWithin(r1, r2, engine);
      }

      if ((h1 == S.Rectangle || h1 == S.Cuboid) && (h2 == S.Rectangle || h2 == S.Cuboid)) {
        if (h1.equals(h2))
          return boxWithin(r1, r2, engine);
      }

      if (isConvex(h1) && hasVertices(h2)) {
        IAST vertices = getVertices(r2);
        if (vertices != null) {
          for (int i = 1; i <= vertices.argSize(); i++) {
            IExpr mem = engine.evaluate(F.RegionMember(reg1, vertices.get(i)));
            if (mem.isFalse())
              return S.False;
            if (!mem.isTrue())
              return F.NIL;
          }
          return S.True;
        }
      }
    }
    return F.NIL;
  }

  private boolean isConvex(IExpr head) {
    return head == S.Disk || head == S.Ball || head == S.Rectangle || head == S.Cuboid
        || head == S.Triangle || head == S.Simplex || head == S.Line || head == S.Point;
  }

  private boolean hasVertices(IExpr head) {
    return head == S.Line || head == S.Triangle || head == S.Polygon || head == S.Simplex;
  }

  private IAST getVertices(IAST reg) {
    if (reg.argSize() == 1 && reg.arg1().isList())
      return (IAST) reg.arg1();
    if (reg.head() == S.Triangle && reg.argSize() == 0) {
      return F.List(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    }
    return null;
  }

  private IExpr ballWithin(IAST r1, IAST r2, EvalEngine engine) {
    IExpr c1 = F.NIL, c2 = F.NIL;
    IExpr rad1 = F.C1, rad2 = F.C1;
    int dim = (r1.head() == S.Disk) ? 2 : 3;

    if (r1.argSize() == 0)
      c1 = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
    else if (r1.argSize() >= 1) {
      c1 = r1.arg1();
      if (r1.argSize() >= 2)
        rad1 = r1.arg2();
    }

    if (r2.argSize() == 0)
      c2 = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
    else if (r2.argSize() >= 1) {
      c2 = r2.arg1();
      if (r2.argSize() >= 2)
        rad2 = r2.arg2();
    }

    if (rad1.isList() || rad2.isList())
      return F.NIL;

    if (c1.isList() && c2.isList()) {
      IExpr dist = engine.evaluate(F.Norm(F.Subtract(c1, c2)));
      return engine.evaluate(F.LessEqual(F.Plus(dist, rad2), rad1));
    }
    return F.NIL;
  }

  private IExpr boxWithin(IAST r1, IAST r2, EvalEngine engine) {
    int dim = (r1.head() == S.Rectangle) ? 2 : 3;
    IExpr min1 = F.NIL, max1 = F.NIL;
    IExpr min2 = F.NIL, max2 = F.NIL;

    if (r1.argSize() == 0) {
      min1 = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
      max1 = (dim == 2) ? F.List(F.C1, F.C1) : F.List(F.C1, F.C1, F.C1);
    } else if (r1.argSize() == 1 && r1.arg1().isList()) {
      min1 = r1.arg1();
      IAST ptAST = (IAST) min1;
      IASTAppendable maxList = F.ListAlloc(ptAST.argSize());
      for (int i = 1; i <= ptAST.argSize(); i++)
        maxList.append(engine.evaluate(F.Plus(ptAST.get(i), F.C1)));
      max1 = maxList;
    } else if (r1.argSize() >= 2) {
      min1 = r1.arg1();
      max1 = r1.arg2();
    }

    if (r2.argSize() == 0) {
      min2 = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
      max2 = (dim == 2) ? F.List(F.C1, F.C1) : F.List(F.C1, F.C1, F.C1);
    } else if (r2.argSize() == 1 && r2.arg1().isList()) {
      min2 = r2.arg1();
      IAST ptAST = (IAST) min2;
      IASTAppendable maxList = F.ListAlloc(ptAST.argSize());
      for (int i = 1; i <= ptAST.argSize(); i++)
        maxList.append(engine.evaluate(F.Plus(ptAST.get(i), F.C1)));
      max2 = maxList;
    } else if (r2.argSize() >= 2) {
      min2 = r2.arg1();
      max2 = r2.arg2();
    }

    if (min1.isList() && max1.isList() && min2.isList() && max2.isList()) {
      IAST lMin1 = (IAST) min1;
      IAST lMax1 = (IAST) max1;
      IAST lMin2 = (IAST) min2;
      IAST lMax2 = (IAST) max2;
      if (lMin1.argSize() == lMin2.argSize()) {
        for (int i = 1; i <= lMin1.argSize(); i++) {
          IExpr cond1 = engine.evaluate(F.LessEqual(lMin1.get(i), lMin2.get(i)));
          IExpr cond2 = engine.evaluate(F.LessEqual(lMax2.get(i), lMax1.get(i)));
          if (cond1.isFalse() || cond2.isFalse())
            return S.False;
          if (!cond1.isTrue() || !cond2.isTrue())
            return F.NIL;
        }
        return S.True;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
