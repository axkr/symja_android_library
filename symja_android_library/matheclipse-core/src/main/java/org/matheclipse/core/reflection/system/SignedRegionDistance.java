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

public class SignedRegionDistance extends AbstractFunctionEvaluator {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 2)
      return F.NIL;
    IExpr arg1 = ast.arg1();
    IExpr p = ast.arg2();

    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Disk:
          case ID.Ball:
            return ballDistance(reg, p, engine, true);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxDistance(reg, p, engine, true);
          case ID.Triangle:
            return triangleDistance(reg, p, engine, true);
        }
      }
    }
    return F.NIL;
    }

  public static IExpr ballDistance(IAST reg, IExpr p, EvalEngine engine, boolean signed) {
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
      IExpr dist = engine.evaluate(F.Norm(F.Subtract(p, c)));
      if (head == S.Circle || head == S.Sphere) {
        return engine.evaluate(F.Abs(F.Subtract(dist, r)));
      }
      if (signed) {
        return engine.evaluate(F.Subtract(dist, r));
      } else {
        return engine.evaluate(F.Max(F.C0, F.Subtract(dist, r)));
      }
        }
    return F.NIL;
    }

  public static IExpr boxDistance(IAST reg, IExpr p, EvalEngine engine, boolean signed) {
    IExpr head = reg.head();
    int dim = (head == S.Rectangle) ? 2 : 3;
    IExpr min = F.NIL;
    IExpr max = F.NIL;
    if (reg.argSize() == 0) {
      min = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
      max = (dim == 2) ? F.List(F.C1, F.C1) : F.List(F.C1, F.C1, F.C1);
    } else if (reg.argSize() == 1) {
      IExpr pt = reg.arg1();
      if (pt.isList()) {
        min = pt;
        IAST ptAST = (IAST) pt;
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

    if (min.isList() && max.isList() && p.isList() && p.argSize() == min.argSize()) {
      IExpr center = engine.evaluate(F.Times(F.C1D2, F.Plus(min, max)));
      IExpr halfSize = engine.evaluate(F.Times(F.C1D2, F.Subtract(max, min)));

      IExpr q = engine.evaluate(F.Subtract(F.Abs(F.Subtract(p, center)), halfSize));

      IASTAppendable maxQ0 = F.ListAlloc(((IAST) q).argSize());
      IAST qL = (IAST) q;
      for (int i = 1; i <= qL.argSize(); i++) {
        maxQ0.append(F.Max(qL.get(i), F.C0));
      }
      IExpr normMaxQ0 = engine.evaluate(F.Norm(maxQ0));

      if (signed) {
        IASTAppendable maxQ = F.ast(S.Max, qL.argSize());
        maxQ.appendArgs(qL);
        IExpr minMaxQ0 = engine.evaluate(F.Min(maxQ, F.C0));
        return engine.evaluate(F.Plus(normMaxQ0, minMaxQ0));
      } else {
        return normMaxQ0;
      }
    }
    return F.NIL;
    }

  public static IExpr triangleDistance(IAST reg, IExpr p, EvalEngine engine, boolean signed) {
    IAST pts = F.NIL;
    if (reg.argSize() == 0) {
      pts = F.List(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    } else if (reg.argSize() == 1 && reg.arg1().isList()) {
      pts = (IAST) reg.arg1();
    }

    if (pts.isPresent() && p.isList()) {
      if (pts.argSize() == 3) {
        IExpr p1 = pts.arg1();
        IExpr p2 = pts.arg2();
        IExpr p3 = pts.arg3();

        IExpr d1 = distanceToSegment(p, p1, p2, engine);
        IExpr d2 = distanceToSegment(p, p2, p3, engine);
        IExpr d3 = distanceToSegment(p, p3, p1, engine);

        IExpr minDist = engine.evaluate(F.Min(d1, d2, d3));

        if (signed) {
          IExpr sign1 = edgeSign(p, p1, p2, engine);
          IExpr sign2 = edgeSign(p, p2, p3, engine);
          IExpr sign3 = edgeSign(p, p3, p1, engine);

          IExpr isInside = engine.evaluate(F.Or(
              F.And(F.GreaterEqual(sign1, F.C0), F.GreaterEqual(sign2, F.C0),
                  F.GreaterEqual(sign3, F.C0)),
              F.And(F.LessEqual(sign1, F.C0), F.LessEqual(sign2, F.C0), F.LessEqual(sign3, F.C0))));

          if (isInside.isTrue()) {
            return engine.evaluate(F.Negate(minDist));
          } else if (isInside.isFalse()) {
            return minDist;
          } else {
            if (!p.isNumericFunction() && !p.isFree(S.Symbol))
              return F.NIL;
            return engine
                .evaluate(F.Piecewise(F.List(F.List(F.Negate(minDist), isInside)), minDist));
          }
        }
        return minDist;
      }
    }
    return F.NIL;
    }

  private static IExpr edgeSign(IExpr p, IExpr a, IExpr b, EvalEngine engine) {
    IExpr px = ((IAST) p).arg1();
    IExpr py = ((IAST) p).arg2();
    IExpr ax = ((IAST) a).arg1();
    IExpr ay = ((IAST) a).arg2();
    IExpr bx = ((IAST) b).arg1();
    IExpr by = ((IAST) b).arg2();
    return engine.evaluate(F.Subtract(F.Times(F.Subtract(px, ax), F.Subtract(by, ay)),
        F.Times(F.Subtract(py, ay), F.Subtract(bx, ax))));
    }

  public static IExpr distanceToSegment(IExpr p, IExpr v, IExpr w, EvalEngine engine) {
    IExpr l2 = engine.evaluate(F.Total(F.Sqr(F.Subtract(v, w))));
    if (l2.isZero())
      return engine.evaluate(F.Norm(F.Subtract(p, v)));

    IExpr dot = engine.evaluate(F.Dot(F.Subtract(p, v), F.Subtract(w, v)));
    IExpr t = engine.evaluate(F.Max(F.C0, F.Min(F.C1, F.Divide(dot, l2))));

    IExpr proj = engine.evaluate(F.Plus(v, F.Times(t, F.Subtract(w, v))));
    return engine.evaluate(F.Norm(F.Subtract(p, proj)));
    }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
    }
}