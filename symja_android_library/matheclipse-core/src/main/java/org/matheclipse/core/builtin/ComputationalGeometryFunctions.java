package org.matheclipse.core.builtin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
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
        return Errors.printMessage(S.AASTriangle, "npa", F.List(a, S.Pi), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The angle `1` should be a positive number less than `2`.
        return Errors.printMessage(S.AASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      if (angleSum.greaterEqualThan(S.Pi).isTrue()) {
        // The sum of angles `1` and `2` should be less than `3`.
        return Errors.printMessage(S.AASTriangle, "asm", F.List(a, b, S.Pi), engine);
      }
      return F.Triangle(//
          F.List(F.CListC0C0, //
              F.List(F.Times(c, F.Csc(a), F.Sin(angleSum)), F.C0), //
              F.List(F.Times(c, F.Cot(a), F.Sin(b)), F.Times(c, F.Sin(b)))) //
      );
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        return Errors.printMessage(S.ASATriangle, "asm", F.List(a, c, S.Pi), engine);
      }
      // Triangle({{0,0}, {b,0}, {b*Cos(a)*Csc(a+c)*Sin(c), b*Csc(a+c)*Sin(a)*Sin(c)}})
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(b, F.C0), //
          F.list(F.Times(b, F.Cos(a), F.Csc(angleSum), F.Sin(c)), //
              F.Times(b, F.Csc(angleSum), F.Sin(a), F.Sin(c)))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        return Errors.printMessage(S.SASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      IAST plus = F.Plus(F.Sqr(a), F.Sqr(c), F.Times(F.CN2, a, c, F.Cos(b)));
      IExpr sqrtNumerator = F.Power(plus, F.C1D2);
      IExpr sqrtDenominator = F.Power(plus, F.CN1D2);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(a), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(b), engine);
      }
      if (c.isNegativeResult() || c.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(c), engine);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class ConvexHullMesh extends AbstractEvaluator {
    /**
     * Three points are a counter-clockwise turn (ccw) if <code>ccw > 0</code>, clockwise if
     * <code>ccw < 0</code>, and co-linear if <code>ccw = 0</code> because <code>ccw</code> is a
     * determinant that gives twice the signed area of the triangle formed by p1, p2 and p3. (from
     * Wikipedia) * @param p1
     * 
     * @param p2
     * @param p3
     * @return Det2D[p2 - p1, p3 - p1]
     */
    private static IExpr counterClockwise(IAST p1, IAST p2, IAST p3) {
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
        if (counterClockwise(point0, point1, point).isZero()) {
          ++k1;
        } else {
          break;
        }
      }
      stack.push(list.get(k1 - 1));
      for (IAST point : list.subList(k1, list.size())) {
        IAST top = stack.pop();
        while (!stack.isEmpty()) {
          IExpr ccw = counterClockwise(stack.peek(), top, point);
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
        IntArrayList dimensions = LinearAlgebraUtil.dimensions(listOfPoints);
        try {
          if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
            if (dimensions.getInt(0) <= 2) {
              // `1` should be a list of `2` or more affinely independent points.
              return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C3),
                  engine);
            }
            return grahamScann2D(listOfPoints, engine);
          } else if (dimensions.size() == 2 && dimensions.getInt(1) == 3) {
            if (dimensions.getInt(0) <= 3) {
              // `1` should be a list of `2` or more affinely independent points.
              return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C4),
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
          int dim = points0.argSize();
          if (dim > 0) {
            IASTAppendable minList = F.ListAlloc(dim);
            IASTAppendable maxList = F.ListAlloc(dim);

            for (int i = 1; i <= dim; i++) {
              minList.append(F.ast(S.Min, listOfPoints.argSize()));
              maxList.append(F.ast(S.Max, listOfPoints.argSize()));
            }

            for (int i = 1; i <= listOfPoints.argSize(); i++) {
              IExpr ptExpr = listOfPoints.get(i);
              if (!ptExpr.isList()) {
                return F.NIL;
              }
              IAST pt = (IAST) ptExpr;
              if (pt.argSize() != dim) {
                return F.NIL;
              }
              for (int j = 1; j <= dim; j++) {
                ((IASTAppendable) minList.get(j)).append(pt.get(j));
                ((IASTAppendable) maxList.get(j)).append(pt.get(j));
              }
            }

            // evaluate the Min and Max calculations
            for (int j = 1; j <= dim; j++) {
              minList.set(j, engine.evaluate(minList.get(j)));
              maxList.set(j, engine.evaluate(maxList.get(j)));
            }

            if (ast.isAST1()) {
              return F.List(minList, maxList);
            }

            if (ast.isAST2()) {
              IExpr padArg = ast.arg2();
              boolean isScaled = false;
              if (padArg.isAST(S.Scaled, 2)) {
                isScaled = true;
                padArg = padArg.first();
              }

              IASTAppendable finalMinList = F.ListAlloc(dim);
              IASTAppendable finalMaxList = F.ListAlloc(dim);

              for (int j = 1; j <= dim; j++) {
                IExpr minPart = minList.get(j);
                IExpr maxPart = maxList.get(j);
                IExpr padMin = F.C0;
                IExpr padMax = F.C0;

                if (padArg.isList()) {
                  IAST padList = (IAST) padArg;
                  // Handle different padding lengths if provided as lists
                  if (padList.argSize() != dim) {
                    return F.NIL;
                  }
                  IExpr p = padList.get(j);
                  if (p.isList2()) {
                    padMin = ((IAST) p).arg1();
                    padMax = ((IAST) p).arg2();
                  } else {
                    padMin = p;
                    padMax = p;
                  }
                } else {
                  padMin = padArg;
                  padMax = padArg;
                }

                IExpr newMin;
                IExpr newMax;

                // Scale calculations identically to maintain formula AST matches in tests
                if (isScaled) {
                  IExpr diffMin = engine.evaluate(F.Subtract(minPart, maxPart));
                  IExpr diffMax = engine.evaluate(F.Subtract(maxPart, minPart));
                  newMin = engine.evaluate(F.Plus(minPart, F.Times(padMin, diffMin)));
                  newMax = engine.evaluate(F.Plus(maxPart, F.Times(padMax, diffMax)));
                } else {
                  newMin = engine.evaluate(F.Subtract(minPart, padMin));
                  newMax = engine.evaluate(F.Plus(maxPart, padMax));
                }

                finalMinList.append(newMin);
                finalMaxList.append(newMax);
              }

              return F.List(finalMinList, finalMaxList);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
          // CoordinateBounds(pts, pad) behaves identically to Transpose(CoordinateBoundingBox(pts,
          // pad))
          IExpr bbox;
          if (ast.isAST1()) {
            bbox = F.CoordinateBoundingBox.funEval(engine, listOfPoints);
          } else if (ast.isAST2()) {
            bbox = F.CoordinateBoundingBox.funEval(engine, listOfPoints, ast.arg2());
          } else {
            return F.NIL;
          }

          if (bbox.isList()) {
            return S.Transpose.funEval(engine, bbox);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
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
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
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
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
      if (arg1.isList2()) {
        IAST listOfVectors = (IAST) arg1;
        IExpr arg11 = listOfVectors.arg1();
        IExpr arg12 = listOfVectors.arg2();
        return compareRecursive(arg11, arg12, engine);
      }
      return F.NIL;
    }


    private IExpr compareRecursive(IExpr arg11, IExpr arg12, EvalEngine engine) {
      int n1 = -1;
      int n2 = -1;
      ITensorAccess v1 = F.NIL;
      ITensorAccess v2 = F.NIL;
      if (arg11 instanceof ITensorAccess) {
        v1 = (ITensorAccess) arg11;
        IntArrayList dim1 = LinearAlgebraUtil.dimensions(v1, S.List);
        if (dim1.size() < 1) {
          return F.NIL;
        }
        n1 = dim1.getInt(0);
      }
      if (arg12 instanceof ITensorAccess) {
        v2 = (ITensorAccess) arg12;
        IntArrayList dim2 = LinearAlgebraUtil.dimensions(v2, S.List);
        if (dim2.size() < 1) {
          return F.NIL;
        }
        n2 = dim2.getInt(0);
        if (n1 > 0 && n1 != n2) {
          return S.False;
        }
      }
      if (arg11.isReal()) {
        if (arg12.isReal()) {
          v1 = F.List(arg11);
          v2 = F.List(arg12);
        } else {
          if (n2 > 0) {
            v1 = F.constantArray(arg11, n2);
          }
        }
      } else {
        if (arg12.isReal()) {
          if (n1 > 0) {
            v2 = F.constantArray(arg12, n1);
          }
        }
      }
      if (v1.isPresent() && v2.isPresent()) {
        for (int i = 1; i < v1.size(); i++) {
          IExpr subV1 = v1.get(i);
          IExpr subV2 = v2.get(i);
          if (subV1 instanceof ITensorAccess || subV2 instanceof ITensorAccess) {
            final IExpr compareResult = compareRecursive(subV1, subV2, engine);
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
          IExpr a1 = v1.get(i);
          IExpr a2 = v2.get(i);

          final IExpr compareResult = compare(a1, a2, engine);
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
      return F.NIL;
    }


    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.Greater.ofNIL(engine, v1, v2);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class VectorGreaterEqual extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.GreaterEqual.ofNIL(engine, v1, v2);
    }

  }

  private static class VectorLess extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.Less.ofNIL(engine, v1, v2);
    }

  }

  private static class VectorLessEqual extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.LessEqual.ofNIL(engine, v1, v2);
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private ComputationalGeometryFunctions() {}
}
