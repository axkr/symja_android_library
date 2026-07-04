package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * FindShortestCurve(region, p1, p2)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * finds the shortest curve connecting p1 and p2 within the region.
 * </p>
 * </blockquote>
 */
public class FindShortestCurve extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr p1 = ast.arg2();
    IExpr p2 = ast.arg3();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();

      // Strict membership validation: Return NIL if point definitively lies outside
      IExpr mem1 = engine.evaluate(F.RegionMember(reg, p1));
      IExpr mem2 = engine.evaluate(F.RegionMember(reg, p2));
      if (mem1.isFalse() || mem2.isFalse()) {
        return F.NIL;
      }

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Circle:
            return circleCurve(reg, p1, p2, engine);
          case ID.Disk:
          case ID.Ball:
          case ID.Rectangle:
          case ID.Cuboid:
          case ID.Triangle:
          case ID.Polygon:
          case ID.Simplex:
          case ID.Tetrahedron:
            // In convex solids the geodesic is the straight segment, kept exact.
            return F.Line(F.List(p1, p2));
          case ID.Line:
            return lineCurve(reg, p1, p2);
        }
      }
    }
    return F.NIL;
  }

  private IExpr circleCurve(IAST reg, IExpr p1, IExpr p2, EvalEngine engine) {
    IExpr c = F.List(F.C0, F.C0);
    IExpr r = F.C1;
    if (reg.argSize() >= 1) {
      c = reg.arg1();
      if (reg.argSize() >= 2) {
        r = reg.arg2();
      }
    }

    if (c.isList2() && p1.isList2() && p2.isList2()) {
      IExpr cx = ((IAST) c).arg1();
      IExpr cy = ((IAST) c).arg2();

      // ArcTan2 natively maps coordinates strictly to angular orientations
      IExpr th1 = engine.evaluate(
          F.ArcTan(F.Subtract(((IAST) p1).arg1(), cx), F.Subtract(((IAST) p1).arg2(), cy)));
      IExpr th2 = engine.evaluate(
          F.ArcTan(F.Subtract(((IAST) p2).arg1(), cx), F.Subtract(((IAST) p2).arg2(), cy)));

      IExpr minT = engine.evaluate(F.Min(th1, th2));
      IExpr maxT = engine.evaluate(F.Max(th1, th2));
      IExpr diff = engine.evaluate(F.Subtract(maxT, minT));

      IExpr isLess = engine.evaluate(F.LessEqual(diff, S.Pi));
      if (isLess.isTrue()) {
        return F.Circle(c, r, F.List(minT, maxT));
      } else {
        // Slices backward resolving over the boundary cut natively ordered
        return F.Circle(c, r, F.List(maxT, engine.evaluate(F.Plus(minT, F.C2Pi))));
      }
    }
    return F.NIL;
  }

  private IExpr lineCurve(IAST reg, IExpr p1, IExpr p2) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      double[][] v = pts.toDoubleMatrix(false);
      if (v == null) {
        return F.NIL;
      }

      double[] p1_d = p1.toDoubleVector();
      double[] p2_d = p2.toDoubleVector();
      if (p1_d == null || p2_d == null)
        return F.NIL;

      double[] S = new double[v.length];
      S[0] = 0;
      for (int i = 1; i < v.length; i++) {
        S[i] = S[i - 1] + dist(v[i - 1], v[i]);
      }
      double L_tot = S[v.length - 1];

      List<Double> s1List = getS(v, S, p1_d);
      List<Double> s2List = getS(v, S, p2_d);

      if (s1List.isEmpty() || s2List.isEmpty()) {
        return F.NIL;
      }

      double minDist = Double.MAX_VALUE;
      double bestS1 = -1, bestS2 = -1;
      boolean bestWrap = false;
      boolean isClosed = dist(v[0], v[v.length - 1]) < 1e-9;

      for (double s1 : s1List) {
        for (double s2 : s2List) {
          double d = Math.abs(s1 - s2);
          if (d < minDist) {
            minDist = d;
            bestS1 = s1;
            bestS2 = s2;
            bestWrap = false;
          }
          if (isClosed) {
            double wrapD = L_tot - d;
            if (wrapD < minDist) {
              minDist = wrapD;
              bestS1 = s1;
              bestS2 = s2;
              bestWrap = true;
            }
          }
        }
      }

      if (minDist == Double.MAX_VALUE)
        return F.NIL;

      List<double[]> path = new ArrayList<>();
      if (!bestWrap) {
        path.addAll(buildDirect(v, S, bestS1, bestS2, p1_d, p2_d));
      } else {
        if (bestS1 <= bestS2) {
          path.addAll(buildDirect(v, S, bestS1, 0, p1_d, v[0]));
          List<double[]> p2Path = buildDirect(v, S, L_tot, bestS2, v[v.length - 1], p2_d);
          p2Path.remove(0); // Bridge start to closed seam
          path.addAll(p2Path);
        } else {
          path.addAll(buildDirect(v, S, bestS1, L_tot, p1_d, v[v.length - 1]));
          List<double[]> p2Path = buildDirect(v, S, 0, bestS2, v[0], p2_d);
          p2Path.remove(0);
          path.addAll(p2Path);
        }
      }

      IASTAppendable linePts = F.ListAlloc(path.size());
      double[] prev = null;
      for (double[] pt : path) {
        if (prev != null && dist(prev, pt) < 1e-9)
          continue;
        IASTAppendable ptAst = F.ListAlloc(pt.length);
        for (double x : pt)
          ptAst.append(F.num(x));
        linePts.append(ptAst);
        prev = pt;
      }

      if (linePts.argSize() < 2)
        return F.NIL;
      return F.Line(linePts);
    }
    return F.NIL;
  }

  private double dist(double[] a, double[] b) {
    double sum = 0;
    int len = Math.min(a.length, b.length);
    for (int i = 0; i < len; i++) {
      sum += (a[i] - b[i]) * (a[i] - b[i]);
    }
    return Math.sqrt(sum);
  }

  private List<Double> getS(double[][] v, double[] S, double[] p) {
    List<Double> res = new ArrayList<>();
    for (int i = 0; i < v.length - 1; i++) {
      double d1 = dist(p, v[i]);
      double d2 = dist(p, v[i + 1]);
      double d12 = dist(v[i], v[i + 1]);
      if (Math.abs(d1 + d2 - d12) < 1e-9) {
        res.add(S[i] + d1);
      }
    }
    return res;
  }

  private List<double[]> buildDirect(double[][] v, double[] S, double sA, double sB, double[] pA,
      double[] pB) {
    List<double[]> res = new ArrayList<>();
    res.add(pA);
    if (sA <= sB) {
      for (int i = 0; i < v.length; i++) {
        if (S[i] > sA + 1e-9 && S[i] < sB - 1e-9) {
          res.add(v[i]);
        }
      }
    } else {
      for (int i = v.length - 1; i >= 0; i--) {
        if (S[i] < sA - 1e-9 && S[i] > sB + 1e-9) {
          res.add(v[i]);
        }
      }
    }
    res.add(pB);
    return res;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }
}
