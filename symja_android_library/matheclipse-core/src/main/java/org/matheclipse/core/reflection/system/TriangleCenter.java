package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * TriangleCenter(tri)
 * TriangleCenter(tri, type)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the coordinates of the specified type of center for the triangle <code>tri</code>.
 * </p>
 * </blockquote>
 */
public class TriangleCenter extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IAST pts = null;

    // Safely extract the three points from the given triangle, polygon, or bare list
    if (arg1.isList() && arg1.argSize() == 3) {
      pts = (IAST) arg1;
    } else if (arg1.isAST(S.Triangle) || arg1.isAST(S.Polygon)) {
      IAST poly = (IAST) arg1;
      if (poly.argSize() >= 1 && poly.arg1().isList() && poly.arg1().argSize() == 3) {
        pts = (IAST) poly.arg1();
      } else if (poly.argSize() == 0) {
        // Fallback for default empty Triangle() or Polygon() -> {{0, 0}, {1, 0}, {0, 1}}
        pts = F.List(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
      }
      }

    if (pts == null) {
      return F.NIL;
    }

    String typeStr = "Centroid";
    IExpr pArg = F.NIL;
    IExpr centerArg = F.NIL;
    boolean isAll = false;

    // Parse the optional type and vertex arguments
    if (ast.argSize() == 2) {
      IExpr arg2 = ast.arg2();
      if (arg2.isString()) {
        typeStr = arg2.toString();
      } else if (arg2.isList() && arg2.argSize() >= 1) {
        IAST list = (IAST) arg2;
        if (list.arg1().isString()) {
          typeStr = list.arg1().toString();
          if (list.argSize() == 2) {
            pArg = list.arg2();
          } else if (list.argSize() == 3) {
            centerArg = list.arg2();
            pArg = list.arg3();
          }
        } else {
          return F.NIL;
        }
      } else {
        return F.NIL;
          }
      }

      if (pArg.equals(S.All)) {
        isAll = true;
      } else if (pArg.isNIL()) {
        pArg = F.C2; // Defaults to p2 according to documentation
      }

      IExpr p1 = pts.arg1();
      IExpr p2 = pts.arg2();
      IExpr p3 = pts.arg3();

      int pIndex = 2;
      if (pArg.isInteger()) {
        pIndex = pArg.toIntDefault(2);
      } else if (pArg.equals(p1) || (pArg.isAST(S.Point, 2) && pArg.first().equals(p1))) {
        pIndex = 1;
      } else if (pArg.equals(p2) || (pArg.isAST(S.Point, 2) && pArg.first().equals(p2))) {
        pIndex = 2;
      } else if (pArg.equals(p3) || (pArg.isAST(S.Point, 2) && pArg.first().equals(p3))) {
        pIndex = 3;
      }

      // Return a list of all three points if 'All' was specified
      if (isAll) {
        IASTAppendable res = F.ListAlloc(3);
        IExpr c1 = evalCenter(engine, typeStr, pts, 1, centerArg);
        IExpr c2 = evalCenter(engine, typeStr, pts, 2, centerArg);
        IExpr c3 = evalCenter(engine, typeStr, pts, 3, centerArg);

        if (c1.isPresent() && c2.isPresent() && c3.isPresent()) {
          res.append(c1);
          res.append(c2);
          res.append(c3);
          return res;
        }
        return F.NIL;
      } else {
        return evalCenter(engine, typeStr, pts, pIndex, centerArg);
      }
    }

  private IExpr evalCenter(EvalEngine engine, String typeStr, IAST pts, int pIndex,
      IExpr centerArg) {
    IExpr p1 = pts.arg1();
    IExpr p2 = pts.arg2();
    IExpr p3 = pts.arg3();

    // Fast-path: Centroid requires no side-length computation
    if ("Centroid".equals(typeStr)) {
      return engine.evaluate(F.Divide(F.Plus(p1, p2, p3), F.C3));
    }

    // Fast-path: NinePointCenter is a composite of Circumcenter and Orthocenter
    if ("NinePointCenter".equals(typeStr)) {
      IExpr c1 = evalCenter(engine, "Circumcenter", pts, pIndex, centerArg);
      IExpr c2 = evalCenter(engine, "Orthocenter", pts, pIndex, centerArg);
      if (c1.isPresent() && c2.isPresent()) {
        return engine.evaluate(F.Times(F.C1D2, F.Plus(c1, c2)));
      }
      return F.NIL;
        }

    // Compute squared side lengths through vectorized dot products for remaining barycentric models
    IExpr diff1 = engine.evaluate(F.Subtract(p2, p3));
    IExpr A2 = engine.evaluate(F.Dot(diff1, diff1));

    IExpr diff2 = engine.evaluate(F.Subtract(p1, p3));
    IExpr B2 = engine.evaluate(F.Dot(diff2, diff2));

    IExpr diff3 = engine.evaluate(F.Subtract(p1, p2));
    IExpr C2 = engine.evaluate(F.Dot(diff3, diff3));

    // Compute Conway's S values
    IExpr SA = engine.evaluate(F.Plus(B2, C2, F.Negate(A2)));
    IExpr SB = engine.evaluate(F.Plus(A2, C2, F.Negate(B2)));
    IExpr SC = engine.evaluate(F.Plus(A2, B2, F.Negate(C2)));

    // Endpoint of a cevian passing through a given center
    if ("CevianEndpoint".equals(typeStr) && centerArg.isPresent()) {
      String centerType = null;
      if (centerArg.isString()) {
        centerType = centerArg.toString();
      }
      if (centerType != null) {
        IExpr[] b = getBarycentrics(centerType, A2, B2, C2, SA, SB, SC, pIndex);
        if (b[0].isPresent()) {
          IExpr u = b[0], v = b[1], w = b[2];
          if (pIndex == 1) {
            u = F.C0;
          } else if (pIndex == 2) {
            v = F.C0;
          } else {
            w = F.C0;
          }
          IExpr sum = engine.evaluate(F.Plus(u, v, w));
          IExpr ux = F.Times(u, p1);
          IExpr vx = F.Times(v, p2);
          IExpr wx = F.Times(w, p3);
          return engine.evaluate(F.Divide(F.Plus(ux, vx, wx), sum));
        }
      }
      return F.NIL;
    }

    IExpr[] b = getBarycentrics(typeStr, A2, B2, C2, SA, SB, SC, pIndex);
    IExpr u = b[0];
    IExpr v = b[1];
    IExpr w = b[2];

    if (u.isPresent() && v.isPresent() && w.isPresent()) {
      // Converts exact barycentric coordinates into Cartesian
      IExpr sum = engine.evaluate(F.Plus(u, v, w));
      IExpr ux = F.Times(u, p1);
      IExpr vx = F.Times(v, p2);
      IExpr wx = F.Times(w, p3);
      return engine.evaluate(F.Divide(F.Plus(ux, vx, wx), sum));
    }

    return F.NIL;
  }

  private IExpr[] getBarycentrics(String typeStr, IExpr A2, IExpr B2, IExpr C2, IExpr SA, IExpr SB,
      IExpr SC, int pIndex) {
    IExpr u = F.NIL, v = F.NIL, w = F.NIL;

    if ("Centroid".equals(typeStr)) {
      u = F.C1;
      v = F.C1;
      w = F.C1;

    } else if ("Circumcenter".equals(typeStr)) {
      u = F.Times(A2, SA);
      v = F.Times(B2, SB);
      w = F.Times(C2, SC);

    } else if ("Incenter".equals(typeStr)) {
      u = F.Sqrt(A2);
      v = F.Sqrt(B2);
      w = F.Sqrt(C2);

    } else if ("Orthocenter".equals(typeStr)) {
      u = F.Times(SB, SC);
      v = F.Times(SA, SC);
      w = F.Times(SA, SB);

    } else if ("SymmedianPoint".equals(typeStr)) {
      u = A2;
      v = B2;
      w = C2;

    } else if ("Excenter".equals(typeStr)) {
      IExpr a = F.Sqrt(A2);
      IExpr b = F.Sqrt(B2);
      IExpr c = F.Sqrt(C2);
      if (pIndex == 1) {
        u = F.Negate(a);
        v = b;
        w = c;
      } else if (pIndex == 2) {
        u = a;
        v = F.Negate(b);
        w = c;
      } else {
        u = a;
        v = b;
        w = F.Negate(c);
      }

    } else if ("Midpoint".equals(typeStr)) {
      if (pIndex == 1) {
        u = F.C0;
        v = F.C1;
        w = F.C1;
      } else if (pIndex == 2) {
        u = F.C1;
        v = F.C0;
        w = F.C1;
      } else {
        u = F.C1;
        v = F.C1;
        w = F.C0;
      }

    } else if ("Foot".equals(typeStr)) {
      if (pIndex == 1) {
        u = F.C0;
        v = SC;
        w = SB;
      } else if (pIndex == 2) {
        u = SC;
        v = F.C0;
        w = SA;
      } else {
        u = SB;
        v = SA;
        w = F.C0;
      }

    } else if ("AngleBisectingCevianEndpoint".equals(typeStr)) {
      IExpr a = F.Sqrt(A2);
      IExpr b = F.Sqrt(B2);
      IExpr c = F.Sqrt(C2);
      if (pIndex == 1) {
        u = F.C0;
        v = b;
        w = c;
      } else if (pIndex == 2) {
        u = a;
        v = F.C0;
        w = c;
      } else {
        u = a;
        v = b;
        w = F.C0;
      }

    } else if ("SymmedianEndpoint".equals(typeStr)) {
      if (pIndex == 1) {
        u = F.C0;
        v = B2;
        w = C2;
      } else if (pIndex == 2) {
        u = A2;
        v = F.C0;
        w = C2;
      } else {
        u = A2;
        v = B2;
        w = F.C0;
      }
    }

    return new IExpr[] {u, v, w};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
    }
}