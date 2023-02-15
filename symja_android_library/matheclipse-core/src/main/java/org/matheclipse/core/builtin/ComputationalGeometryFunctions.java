package org.matheclipse.core.builtin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;
import org.matheclipse.core.tensor.opt.qh3.ConvexHull3D;
import org.matheclipse.core.tensor.opt.qh3.Vector3d;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class ComputationalGeometryFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AASTriangle.setEvaluator(new AASTriangle());
      S.ASATriangle.setEvaluator(new ASATriangle());
      S.SASTriangle.setEvaluator(new SASTriangle());
      S.SSSTriangle.setEvaluator(new SSSTriangle());
      S.ArcLength.setEvaluator(new ArcLength());
      S.Area.setEvaluator(new Area());
      S.Perimeter.setEvaluator(new Perimeter());


      S.ConvexHullMesh.setEvaluator(new ConvexHullMesh());
      S.CollinearPoints.setEvaluator(new CollinearPoints());
      S.CoordinateBoundingBox.setEvaluator(new CoordinateBoundingBox());
      S.CoordinateBounds.setEvaluator(new CoordinateBounds());
      S.CoplanarPoints.setEvaluator(new CoplanarPoints());

      S.VectorGreater.setEvaluator(new VectorGreater());
      S.VectorGreaterEqual.setEvaluator(new VectorGreaterEqual());
      S.VectorLess.setEvaluator(new VectorLess());
      S.VectorLessEqual.setEvaluator(new VectorLessEqual());
    }
  }

  private static class AASTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IAST angleSum = F.Plus(a, b);
      if (a.isNegativeResult() || a.isZero()) {
        // The angle `1` should be a positive number less than `2`.
        return IOFunctions.printMessage(S.AASTriangle, "npa", F.List(a, S.Pi), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The angle `1` should be a positive number less than `2`.
        return IOFunctions.printMessage(S.AASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      if (angleSum.greaterEqualThan(S.Pi).isTrue()) {
        // The sum of angles `1` and `2` should be less than `3`.
        return IOFunctions.printMessage(S.AASTriangle, "asm", F.List(a, b, S.Pi), engine);
      }
      return F.Triangle(//
          F.List(F.CListC0C0, //
              F.List(F.Times(c, F.Csc(a), F.Sin(angleSum)), F.C0), //
              F.List(F.Times(c, F.Cot(a), F.Sin(b)), F.Times(c, F.Sin(b)))) //
      );
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }


  private static class ASATriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IExpr angleSum = engine.evaluate(F.Plus(a, c));
      if (angleSum.greaterEqualThan(S.Pi).isTrue()) {
        // The sum of angles `1` and `2` should be less than `3`.
        return IOFunctions.printMessage(S.ASATriangle, "asm", F.List(a, c, S.Pi), engine);
      }
      // Triangle({{0,0}, {b,0}, {b*Cos(a)*Csc(a+c)*Sin(c), b*Csc(a+c)*Sin(a)*Sin(c)}})
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(b, F.C0), //
          F.list(F.Times(b, F.Cos(a), F.Csc(angleSum), F.Sin(c)), //
              F.Times(b, F.Csc(angleSum), F.Sin(a), F.Sin(c)))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class SASTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      if (b.greaterEqualThan(S.Pi).isTrue()) {
        // The angle `1` should be a positive number less than `2`
        return IOFunctions.printMessage(S.SASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      IAST plus = F.Plus(F.Sqr(a), F.Sqr(c), F.Times(F.CN2, a, c, F.Cos(b)));
      IAST sqrtNumerator = F.Power(plus, F.C1D2);
      IAST sqrtDenominator = F.Power(plus, F.CN1D2);
      // Triangle({{0, 0}, {Sqrt(a^2+c^2-2*a*c*Cos(b)), 0},
      // {(c^2-a*c*Cos(b))/Sqrt(a^2+c^2-2*a*c*Cos(b)),
      // (a*c*Sin(b))/Sqrt(a^2+c^2-2*a*c*Cos(b))}})
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(sqrtNumerator, F.C0), //
          F.list(F.Times(F.Plus(F.Sqr(c), F.Times(F.CN1, a, c, F.Cos(b))), sqrtDenominator), //
              F.Times(a, c, sqrtDenominator, F.Sin(b)))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class SSSTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      if (a.isNegativeResult() || a.isZero()) {
        // The triangle side `1`should be a positive number.
        return IOFunctions.printMessage(S.SSSTriangle, "nps", F.List(a), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The triangle side `1`should be a positive number.
        return IOFunctions.printMessage(S.SSSTriangle, "nps", F.List(b), engine);
      }
      if (c.isNegativeResult() || c.isZero()) {
        // The triangle side `1`should be a positive number.
        return IOFunctions.printMessage(S.SSSTriangle, "nps", F.List(c), engine);
      }
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(c, F.C0), //
          F.list(//
              F.Times(F.Plus(F.Negate(F.Sqr(a)), F.Sqr(b), F.Sqr(c)),
                  F.Power(F.Times(F.C2, c), F.CN1)),
              F.Times(F.Power(F.Times(F.C2, c), F.CN1), F.Sqrt(F.Times(F.Plus(a, b, F.Negate(c)),
                  F.Plus(a, F.Negate(b), c), F.Plus(F.Negate(a), b, c), F.Plus(a, b, c))))) //
      ));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class ArcLength extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST() && arg1.head().isBuiltInSymbol()) {
        IAST geoForm = (IAST) ast.arg1();
        int headID = arg1.headID();
        if (headID >= 0) {
          switch (headID) {
            case ID.Circle:
              return circle(geoForm);
            case ID.Disk:
              return S.Undefined;
            case ID.Line:
              return line(geoForm);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr circle(IAST geoForm) {
      if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
        IExpr r1 = F.C1;
        IExpr r2 = F.C1;
        IExpr t1 = F.C0;
        IExpr t2 = F.C2Pi;
        if (geoForm.argSize() == 1) {
          // default values;
        } else if (geoForm.argSize() >= 2) {
          if (geoForm.arg2().isList2()) {
            r1 = geoForm.arg2().first();
            r2 = geoForm.arg2().second();
          } else if (!geoForm.arg2().isList()) {
            r1 = geoForm.arg2();
            r2 = r1;
          } else {
            return F.NIL;
          }
        }
        if (geoForm.argSize() == 3) {
          if (geoForm.arg3().isList2()) {
            t1 = geoForm.arg3().first();
            t2 = geoForm.arg3().second();
            return
            // [$ r2*(-EllipticE(t1, 1 - r1^2/r2^2) + EllipticE(t2, 1 - r1^2/r2^2))*
            // UnitStep(2*Pi - Abs(-t1 + t2)) + 4*r2*EllipticE(1 - r1^2/r2^2)*
            // UnitStep(-2*Pi + Abs(-t1 + t2)) $]
            F.Plus(
                F.Times(r2, F.Plus(
                    F.Negate(F.EllipticE(t1,
                        F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2))))),
                    F.EllipticE(t2, F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2))))),
                    F.UnitStep(F.Subtract(F.C2Pi, F.Abs(F.Plus(F.Negate(t1), t2))))),
                F.Times(F.C4, r2,
                    F.EllipticE(F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))),
                    F.UnitStep(F.Plus(F.CN2Pi, F.Abs(F.Plus(F.Negate(t1), t2)))))); // $$;
          } else {
            return F.NIL;
          }
        }
        if (geoForm.argSize() > 3) {
          return F.NIL;
        }


        // 4*r2*EllipticE(1-r1^2/r2^2)
        return F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2)))));
      }
      return F.NIL;
    }

    private static IExpr line(IAST geoForm) {
      if (geoForm.isAST1() && geoForm.arg1().isListOfPoints(2)) {
        IAST listOfPoints2D = (IAST) geoForm.arg1();
        if (listOfPoints2D.argSize() > 1) {
          IASTAppendable result = F.PlusAlloc(listOfPoints2D.argSize() - 1);
          IAST currentPoint = (IAST) listOfPoints2D.get(1);
          for (int i = 2; i < listOfPoints2D.size(); i++) {
            IAST nextPoint = (IAST) listOfPoints2D.get(i);
            IExpr a = currentPoint.arg1();
            IExpr b = currentPoint.arg2();
            IExpr c = nextPoint.arg1();
            IExpr d = nextPoint.arg2();
            // Sqrt((a-c)^2 + (b-d)^2)
            result.append(F.Sqrt(F.Plus(F.Sqr(F.Subtract(a, c)), //
                F.Sqr(F.Subtract(b, d)))));
            currentPoint = nextPoint;
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }
  private static class Area extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST() && arg1.head().isBuiltInSymbol()) {
        IAST geoForm = (IAST) ast.arg1();
        int headID = arg1.headID();
        if (headID >= 0) {
          switch (headID) {
            case ID.Circle:
              return S.Undefined;
            case ID.Disk:
              return disk(geoForm);
            case ID.Rectangle:
              return rectangle(geoForm);
            case ID.Triangle:
              return triangle(geoForm);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr disk(IAST geoForm) {

      if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
        IExpr t1 = F.C0;
        IExpr t2 = F.C2Pi;
        IExpr r1 = F.C1;
        IExpr r2 = F.C1;
        if (geoForm.argSize() >= 2) {
          if (geoForm.arg2().isList2()) {
            r1 = geoForm.arg2().first();
            r2 = geoForm.arg2().second();
          } else if (!geoForm.arg2().isList()) {
            r1 = geoForm.arg2();
            r2 = r1;
          } else {
            return F.NIL;
          }
        }

        if (geoForm.argSize() == 3) {
          if (geoForm.arg3().isList2()) {
            t1 = geoForm.arg3().first();
            t2 = geoForm.arg3().second();
            return
            // [$ (r1*r2*Min(Pi, Abs(-t1+t2)/2)) $]
            F.Times(r1, r2, F.Min(F.Pi, F.Times(F.C1D2, F.Abs(F.Plus(F.Negate(t1), t2))))); // $$;
          } else {
            return F.NIL;
          }

        }
        if (geoForm.argSize() > 3) {
          return F.NIL;
        }
        return F.Times(F.Pi, r1, r2);
      }
      return F.NIL;
    }

    private static IExpr rectangle(IAST geoForm) {
      IExpr a, b, c, d;
      if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
        a = geoForm.arg1().first();
        b = geoForm.arg1().second();
        if (geoForm.argSize() == 1) {
          c = a.add(F.C1);
          d = b.add(F.C1);
        } else if (geoForm.argSize() == 2 && geoForm.arg2().isList2()) {
          c = geoForm.arg2().first();
          d = geoForm.arg2().second();
        } else {
          return F.NIL;
        }
        return F.Abs(F.Times(F.Plus(F.Negate(a), c), F.Plus(F.Negate(b), d)));
      }
      return F.NIL;
    }

    private static IExpr triangle(IAST geoForm) {

      if (geoForm.argSize() >= 1 && geoForm.arg1().isList3() && geoForm.arg1().isListOfPoints(2)) {
        IAST list = (IAST) geoForm.arg1();
        IExpr a1 = list.arg1().first();
        IExpr a2 = list.arg1().second();
        IExpr b1 = list.arg2().first();
        IExpr b2 = list.arg2().second();
        IExpr c1 = list.arg3().first();
        IExpr c2 = list.arg3().second();
        return
        // [$ (1/2)*Abs((-a2)*b1+a1*b2 +a2*c1-b2*c1-a1*c2+b1*c2) $]
        F.Times(F.C1D2, F.Abs(F.Plus(F.Times(F.CN1, a2, b1), F.Times(a1, b2), F.Times(a2, c1),
            F.Times(F.CN1, b2, c1), F.Times(F.CN1, a1, c2), F.Times(b1, c2)))); // $$;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class Perimeter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST() && arg1.head().isBuiltInSymbol()) {
        IAST geoForm = (IAST) ast.arg1();
        int headID = arg1.headID();
        if (headID >= 0) {
          switch (headID) {
            case ID.Circle:
            case ID.Line:
              return S.Undefined;
            case ID.Disk:
              return disk(geoForm);
            case ID.Rectangle:
              return rectangle(geoForm);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr disk(IAST geoForm) {
      if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
        IExpr t1 = F.C0;
        IExpr t2 = F.C2Pi;
        IExpr r1 = F.C1;
        IExpr r2 = F.C1;
        if (geoForm.argSize() >= 2) {
          if (geoForm.arg2().isList2()) {
            r1 = geoForm.arg2().first();
            r2 = geoForm.arg2().second();
          } else if (!geoForm.arg2().isList()) {
            r1 = geoForm.arg2();
            r2 = r1;
          } else {
            return F.NIL;
          }
        }

        if (geoForm.argSize() == 3) {
          if (geoForm.arg3().isList2()) {
            t1 = geoForm.arg3().first();
            t2 = geoForm.arg3().second();
            return
            // [$ (r2*(-EllipticE(t1, 1 - r1^2/r2^2) + EllipticE(t2, 1 - r1^2/r2^2)) +
            // Sqrt(r1^2*Cos(t1)^2 + r2^2*Sin(t1)^2) +
            // Sqrt(r1^2*Cos(t2)^2 + r2^2*Sin(t2)^2))*UnitStep(2*Pi - Abs(-t1 + t2)) +
            // 4*r2*EllipticE(1 - r1^2/r2^2)*UnitStep(-2*Pi + Abs(-t1 + t2)) $]
            F.Plus(
                F.Times(
                    F.Plus(
                        F.Times(
                            r2, F
                                .Plus(
                                    F.Negate(F.EllipticE(t1,
                                        F.Plus(F.C1,
                                            F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2))))),
                                    F.EllipticE(t2,
                                        F.Plus(F.C1,
                                            F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))))),
                        F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t1))),
                            F.Times(F.Sqr(r2), F.Sqr(F.Sin(t1))))),
                        F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t2))),
                            F.Times(F.Sqr(r2), F.Sqr(F.Sin(t2)))))),
                    F.UnitStep(F.Subtract(F.C2Pi, F.Abs(F.Plus(F.Negate(t1), t2))))),
                F.Times(F.C4, r2,
                    F.EllipticE(F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))),
                    F.UnitStep(F.Plus(F.CN2Pi, F.Abs(F.Plus(F.Negate(t1), t2)))))); // $$;
          } else {
            return F.NIL;
          }
        }
        if (geoForm.argSize() > 3) {
          return F.NIL;
        }

        return F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2)))));
      }
      return F.NIL;
    }

    private static IExpr rectangle(IAST geoForm) {
      IExpr a, b, c, d;
      if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
        a = geoForm.arg1().first();
        b = geoForm.arg1().second();
        if (geoForm.argSize() == 1) {
          c = a.add(F.C1);
          d = b.add(F.C1);
        } else if (geoForm.argSize() == 2 && geoForm.arg2().isList2()) {
          c = geoForm.arg2().first();
          d = geoForm.arg2().second();
        } else {
          return F.NIL;
        }
        return F.Times(F.C2, F.Plus(F.Abs(F.Plus(F.Negate(a), c)), F.Abs(F.Plus(F.Negate(b), d))));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }
  private static class ConvexHullMesh extends AbstractEvaluator {
    /**
     * Three points are a counter-clockwise turn if ccw > 0, clockwise if ccw < 0, and co-linear if
     * ccw = 0 because ccw is a determinant that gives twice the signed area of the triangle formed
     * by p1, p2 and p3. (from Wikipedia)
     * 
     * @param p1
     * @param p2
     * @param p3
     * @return Det2D[p2 - p1, p3 - p1]
     */
    private static IExpr ccw(IAST p1, IAST p2, IAST p3) {
      IAST v1 = (IAST) F.eval(F.Subtract(p2, p1));
      IAST v2 = (IAST) F.eval(F.Subtract(p3, p1));
      // see LinearAlgebra.determinant2x2();
      return F.eval(v1.get(1).times(v2.get(2)).subtract(v1.get(2).times(v2.get(1))));
    }

    private static final Comparator<IAST> MINY_MINX = new Comparator<IAST>() {
      @Override
      public int compare(IAST p1, IAST p2) {
        int cmp = p1.arg2().compareTo(p2.arg2());
        return cmp != 0 ? cmp : p1.arg1().compareTo(p2.arg1());
      }
    };

    /**
     * The Java API recommends to use ArrayDeque instead of Stack. However, in the implementation of
     * GrahamScan, we can't conveniently exchange Stack and ArrayDeque because ArrayDeque#stream()
     * reverses the order. GrahamScan is used in several applications. No performance issues were
     * reported so far.
     */
    public static IAST grahamScann2D(IAST ast, EvalEngine engine) {
      if (ast.isEmpty()) {
        return F.CEmptyList;
      }
      // list is permuted during computation of convex hull
      List<IAST> list = Convert.toList(ast, x -> (IAST) x);
      // VectorQ.requireLength(list.get(0), 2);
      final IAST point0 = Collections.min(list, MINY_MINX);
      Collections.sort(list, new Comparator<>() {
        @Override
        public int compare(IAST p1, IAST p2) {
          IAST d10 = (IAST) F.eval(F.Subtract(p1, point0));
          IAST d20 = (IAST) F.eval(F.Subtract(p2, point0));
          double atan1 = d10.arg1().isZero() ? 0.0 : F.ArcTan(d10.arg1(), d10.arg2()).evalf();
          double atan2 = d20.arg1().isZero() ? 0.0 : F.ArcTan(d20.arg1(), d20.arg2()).evalf();
          int cmp = F.isEqual(atan1, atan2) ? 0 : atan1 < atan2 ? -1 : 1;
          return cmp != 0 ? cmp : MINY_MINX.compare(p1, p2);
        }
      });
      // ArrayDeque::stream is reverse of Stack::stream
      Stack<IAST> stack = new Stack<>();
      stack.push(point0);
      int k1 = 1;
      IAST point1 = F.NIL; // find point1 different from point0
      for (IAST point : list.subList(k1, list.size())) {
        if (!point0.equals(point)) { // should Chop.08 be used for consistency with chop below ?
          point1 = point;
          break;
        }
        ++k1;
      }
      if (point1.isNIL()) {
        return F.List(point0);
      }
      ++k1;
      // find point not co-linear with point0 and point1
      for (IAST point : list.subList(k1, list.size())) {
        if (ccw(point0, point1, point).isZero()) {
          ++k1;
        } else {
          break;
        }
      }
      stack.push(list.get(k1 - 1));
      for (IAST point : list.subList(k1, list.size())) {
        IAST top = stack.pop();
        while (!stack.isEmpty()) {
          IExpr ccw = ccw(stack.peek(), top, point);
          if (ccw.isPositive()) {
            break;
          }
          top = stack.pop();
        }
        stack.push(top);
        stack.push(point);
      }
      return F.ListAlloc(stack.stream());
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfLists()) {
        IAST listOfPoints = (IAST) ast.arg1();
        IntArrayList dimensions = LinearAlgebra.dimensions(listOfPoints);
        try {
          if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
            if (dimensions.getInt(0) <= 2) {
              // `1` should be a list of `2` or more affinely independent points.
              return IOFunctions.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C3),
                  engine);
            }
            return grahamScann2D(listOfPoints, engine);
          } else if (dimensions.size() == 2 && dimensions.getInt(1) == 3) {
            if (dimensions.getInt(0) <= 3) {
              // `1` should be a list of `2` or more affinely independent points.
              return IOFunctions.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C4),
                  engine);
            }
            return quickHull3D(listOfPoints);
          }
        } catch (IllegalArgumentException iae) {
          //
        }
      }
      return F.NIL;
    }

    private static IExpr quickHull3D(IAST listOfPoints) {
      ConvexHull3D hull = new ConvexHull3D();
      hull.build(listOfPoints);
      Vector3d[] vertices = hull.getVertices();
      IASTAppendable resultList = F.ListAlloc(vertices.length);
      for (int i = 0; i < vertices.length; i++) {
        Vector3d pnt = vertices[i];
        resultList.append(pnt.toTensor());
      }
      return resultList;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class CoordinateBoundingBox extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfLists()) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.argSize() > 0) {
          IAST points0 = (IAST) listOfPoints.arg1();
          if (points0.argSize() > 0) {
            IASTAppendable minList = F.ListAlloc(points0.size());
            IASTAppendable maxList = F.ListAlloc(points0.size());
            for (int i = 1; i < points0.size(); i++) {
              minList.append(F.ast(S.Min, points0.argSize()));
              maxList.append(F.ast(S.Max, points0.argSize()));
            }
            IAST result = F.List(minList, maxList);
            for (int j = 1; j < points0.size(); j++) {
              IASTAppendable minAppendable = (IASTAppendable) minList.get(j);
              IASTAppendable maxAppendable = (IASTAppendable) maxList.get(j);
              for (int i = 1; i < listOfPoints.size(); i++) {
                IAST points = listOfPoints.getAST(i);
                if (points.argSize() != points0.argSize()) {
                  return F.NIL;
                }
                minAppendable.append(points.get(j));
                maxAppendable.append(points.get(j));
              }
            }

            // evaluate the Min and Max calculations inside referenced lists
            minList.forEach((x, i) -> minList.set(i, engine.evaluate(x)));
            maxList.forEach((x, i) -> maxList.set(i, engine.evaluate(x)));

            IExpr pad = F.C0;
            if (ast.isAST2()) {
              // pad the result
              pad = ast.arg2();
            }
            if (pad.isZero()) {
              return result;
            }
            if (pad.isAST(S.Scaled, 2)) {
              IExpr scaled = pad.first();
              for (int i = 1; i < minList.size(); i++) {
                IExpr minPart = minList.get(i);
                IExpr maxPart = maxList.get(i);
                minList.set(i, F.Plus(minPart, F.Times(scaled, F.Subtract(minPart, maxPart))));
                maxList.set(i, F.Plus(maxPart, F.Times(scaled, F.Subtract(maxPart, minPart))));
              }
            } else {
              for (int i = 1; i < minList.size(); i++) {
                IExpr minPart = minList.get(i);
                IExpr maxPart = maxList.get(i);
                minList.set(i, F.Subtract(minPart, pad));
                maxList.set(i, F.Plus(maxPart, pad));
              }
            }
            return result;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class CoordinateBounds extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfLists()) {
        IAST listOfPoints = (IAST) arg1;
        if (listOfPoints.argSize() > 0) {
          IExpr temp = S.Transpose.ofNIL(engine, F.CoordinateBoundingBox(listOfPoints));
          if (temp.isList()) {
            if (ast.isAST2()) {
              IAST list = (IAST) temp;
              IExpr pad = ast.arg2();
              if (pad.isNumber()) {
                IASTAppendable result = F.ListAlloc(list.argSize());
                for (int i = 1; i < list.size(); i++) {
                  IExpr expr = list.get(i);
                  if (expr.isList2()) {
                    result.append(F.List(expr.first().subtract(pad), expr.second().plus(pad)));
                  } else {
                    return F.NIL;
                  }
                }
                return result;
              }
            }
            return temp;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b,c]</code> is on the plane defined by the first three
   * points <code>{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}</code>.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Coplanarity">Wikipedia - Coplanarity</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CoplanarPoints( {{3,2,-5}, {-1,4,-3}, {-3,8,-5}, {-3,2,1}})
   * True
   *
   * &gt;&gt; CoplanarPoints( {{0,-1,-1}, {4,5,1}, {3,9,4}, {-4,4,3}})
   * False
   * </code>
   * </pre>
   */
  private static class CoplanarPoints extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg1().argSize() > 2) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.arg1().isList2()) {
          // all 2D points lie on the same plane
          for (int i = 2; i < listOfPoints.size(); i++) {
            if (listOfPoints.get(i).isList2()) {
              continue;
            } else {
              // `1` should be a non-empty list of points.
              return IOFunctions.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          return S.True;
        } else if (ast.arg1().argSize() > 3 && listOfPoints.arg1().isList3()
            && listOfPoints.arg2().isList3() && listOfPoints.arg3().isList3()) {

          IASTAppendable result = F.ast(S.And, listOfPoints.size() - 3);
          for (int i = 4; i < listOfPoints.size(); i++) {
            IAST p1 = (IAST) listOfPoints.get(i - 3);
            IAST p2 = (IAST) listOfPoints.get(i - 2);
            IAST p3 = (IAST) listOfPoints.get(i - 1);
            // equation of plane is: a*x + b*y + c*z = 0
            if (listOfPoints.get(i).isList3()) {
              IAST p4 = (IAST) listOfPoints.get(i);
              IExpr temp = coplanarPoints3D(p1, p2, p3, p4, engine);
              result.append(temp);
            } else {
              // `1` should be a non-empty list of points.
              return IOFunctions.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          if (result.argSize() == 1) {
            return result.arg1();
          }
          return result;
        }
      }

      return F.NIL;
    }

    /**
     * Gives <code>true</code>, if the point <code>p4</code> is on the plane defined through <code>
     * p1,p2,p3</code>. Return an equation equal to <code>0</code> for symbolic parameterized
     * points.
     *
     * @param p1 a point with 3 elements in list form (x,y,z coordinates)
     * @param p2 a point with 3 elements in list form (x,y,z coordinates)
     * @param p3 a point with 3 elements in list form (x,y,z coordinates)
     * @param p4 a point with 3 elements in list form (x,y,z coordinates)
     * @param engine
     */
    private static IExpr coplanarPoints3D(IAST p1, IAST p2, IAST p3, IAST p4, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr z1 = p1.arg3();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();
      IExpr z2 = p2.arg3();
      IExpr x3 = p3.arg1();
      IExpr y3 = p3.arg2();
      IExpr z3 = p3.arg3();

      IExpr a1 = z1.subtract(z2);
      IExpr b1 = y2.subtract(y1);
      IExpr a22 = x1.subtract(x3);
      IExpr b2 = y3.subtract(y1);
      IExpr b22 = y1.subtract(y3);
      IExpr c1 = x1.subtract(x2);
      IExpr c2 = z3.subtract(z1);

      IExpr px = p4.arg1();
      IExpr py = p4.arg2();
      IExpr pz = p4.arg3();

      IExpr times1 = S.Times.of(engine, a1,
          F.Plus(F.Times(py, a22), F.Times(x3, y1), F.Times(-1, x1, y3), F.Times(px, b2)));
      IExpr times2 = S.Times.of(engine, b1,
          F.Plus(F.Times(pz, a22), F.Times(x3, z1), F.Times(-1, x1, z3), F.Times(px, c2)));

      IExpr times3 = S.Times.of(engine, c1,
          F.Plus(F.Times(pz, b22), F.Times(y3, z1), F.Times(-1, y1, z3), F.Times(py, c2)));
      IExpr calc = F.Plus.of(engine, times1, times2, times3);

      if (calc.isZero()) {
        return S.True;
      } else if (calc.isNumber()) {
        return S.False;
      }
      if (calc.isPlusTimesPower()) {
        calc = engine.evaluate(F.Equal(F.Factor(calc), F.C0));
      }
      return calc;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>CollinearPoints({{x1,y1},{x2,y2},{a,b},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b]</code> is on the line defined by the first two points
   * <code>{x1,y1},{x2,y2}</code>.
   *
   * <pre>
   * <code>CollinearPoints({{x1,y1,z1},{x2,y2,z2},{a,b,c},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b,c]</code> is on the line defined by the first two points
   * <code>{x1,y1,z1},{x2,y2,z2}</code>.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Collinearity">Wikipedia - Collinearity</a>
   * <li><a href="https://youtu.be/UDt9M8_zxlw">Youtube - Collinear Points in 3D (Ch1 Pr18)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CollinearPoints({{1,2,3}, {3,8,1}, {7,20,-3}})
   * True
   * </code>
   * </pre>
   */
  private static class CollinearPoints extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg1().argSize() > 2) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (ast.arg1().argSize() > 2 && listOfPoints.arg1().isList2()
            && listOfPoints.arg2().isList2()) {

          IASTAppendable result = F.ast(S.And, listOfPoints.size() - 2);
          for (int i = 3; i < listOfPoints.size(); i++) {
            IAST p1 = (IAST) listOfPoints.get(i - 2);
            IAST p2 = (IAST) listOfPoints.get(i - 1);
            if (listOfPoints.get(i).isList2()) {
              IAST p3 = (IAST) listOfPoints.get(i);
              IExpr temp = collinearPoints2D(p1, p2, p3, engine);
              result.append(temp);
            } else {
              // `1` should be a non-empty list of points.
              return IOFunctions.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          if (result.argSize() == 1) {
            return result.arg1();
          }
          return result;
        } else if (ast.arg1().argSize() > 2 && listOfPoints.arg1().isList3()
            && listOfPoints.arg2().isList3() && listOfPoints.arg3().isList3()) {

          IASTAppendable result = F.ast(S.And, listOfPoints.size() - 3);
          for (int i = 3; i < listOfPoints.size(); i++) {
            IAST p1 = (IAST) listOfPoints.get(i - 2);
            IAST p2 = (IAST) listOfPoints.get(i - 1);
            if (listOfPoints.get(i).isList3()) {
              IAST p3 = (IAST) listOfPoints.get(i);
              IExpr temp = collinearPoints3D(p1, p2, p3, engine);
              if (temp.isNIL()) {
                return F.NIL;
              }
              result.append(temp);
            } else {
              // `1` should be a non-empty list of points.
              return IOFunctions.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          if (result.argSize() == 1) {
            return result.arg1();
          }
          return result;
        }
      }

      return F.NIL;
    }

    private static IExpr collinearPoints2D(IAST p1, IAST p2, IAST p3, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();

      IExpr px = p3.arg1();
      IExpr py = p3.arg2();

      IAST plus1 = F.Plus(px.times(y2.subtract(y1)), x2.times(y1));
      IAST plus2 = F.Plus(py.times(x2.subtract(x1)), x1.times(y2));
      IExpr calc = F.Subtract.of(engine, plus1, plus2);
      if (calc.isZero()) {
        return S.True;
      } else if (calc.isNumber()) {
        return S.False;
      }
      if (calc.isPlusTimesPower()) {
        calc = engine.evaluate(F.Equal(plus1, plus2));
      }
      return calc;
    }

    private static IExpr collinearPoints3D(IAST p1, IAST p2, IAST p3, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr z1 = p1.arg3();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();
      IExpr z2 = p2.arg3();
      IExpr x3 = p3.arg1();
      IExpr y3 = p3.arg2();
      IExpr z3 = p3.arg3();

      // vector p2-p1
      IExpr x21 = x2.subtract(x1);
      IExpr y21 = y2.subtract(y1);
      IExpr z21 = z2.subtract(z1);
      // vector p3-p1
      IExpr x31 = x3.subtract(x1);
      IExpr y31 = y3.subtract(y1);
      IExpr z31 = z3.subtract(z1);
      // factors
      IExpr fx = F.C0;
      IExpr fy = F.C0;
      IExpr fz = F.C0;
      IASTAppendable equalAST = F.ast(S.Equal, 3);
      if (!x21.isZero()) {
        fx = x31.divide(x21);
        equalAST.append(fx);
      } else if (!x31.isZero()) {
        return S.False;
      }
      if (!y21.isZero()) {
        fy = y31.divide(y21);
        equalAST.append(fy);
      } else if (!y31.isZero()) {
        return S.False;
      }
      if (!z21.isZero()) {
        fz = z31.divide(z21);
        equalAST.append(fz);
      } else if (!z31.isZero()) {
        return S.False;
      }
      IExpr calc = engine.evaluate(equalAST);
      if (calc.isTrue()) {
        return S.True;
      } else if (calc.isFalse()) {
        return S.False;
      } else if (fx.isNumber() && fy.isNumber() && fz.isNumber()) {
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  private static class VectorGreater extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[] dims = arg1.isMatrix(false);
      if (arg1.isList2() && dims != null) {
        IAST listOfVectors = (IAST) arg1;
        ITensorAccess v1 = (ITensorAccess) listOfVectors.arg1();
        ITensorAccess v2 = (ITensorAccess) listOfVectors.arg2();
        if (v1.size() >= 1) {
          int[] index = new int[] {0};
          for (int i = 1; i < v1.size(); i++) {
            index[0] = i;
            final IExpr compareResult = compare(v1, v2, index, engine);
            if (compareResult.isPresent()) {
              if (compareResult.isTrue()) {
                continue;
              } else if (compareResult.isFalse()) {
                return S.False;
              }
            }
            // undecidable
            return F.NIL;
          }
          return S.True;
        }
      }
      return F.NIL;
    }


    protected IExpr compare(ITensorAccess v1, ITensorAccess v2, int[] index, EvalEngine engine) {
      return S.Greater.ofNIL(engine, v1.getIndex(index), v2.getIndex(index));
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class VectorGreaterEqual extends VectorGreater {

    @Override
    protected IExpr compare(ITensorAccess v1, ITensorAccess v2, int[] index, EvalEngine engine) {
      return S.GreaterEqual.ofNIL(engine, v1.getIndex(index), v2.getIndex(index));
    }

  }

  private static class VectorLess extends VectorGreater {

    @Override
    protected IExpr compare(ITensorAccess v1, ITensorAccess v2, int[] index, EvalEngine engine) {
      return S.Less.ofNIL(engine, v1.getIndex(index), v2.getIndex(index));
    }

  }

  private static class VectorLessEqual extends VectorGreater {

    @Override
    protected IExpr compare(ITensorAccess v1, ITensorAccess v2, int[] index, EvalEngine engine) {
      return S.LessEqual.ofNIL(engine, v1.getIndex(index), v2.getIndex(index));
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private ComputationalGeometryFunctions() {}
}
