package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class ComputationalGeometryFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ConvexHullMesh.setEvaluator(new ConvexHullMesh());
      S.CollinearPoints.setEvaluator(new CollinearPoints());
      S.CoordinateBoundingBox.setEvaluator(new CoordinateBoundingBox());
      S.CoplanarPoints.setEvaluator(new CoplanarPoints());
    }
  }

  private static class ConvexHullMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfLists()) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.argSize() > 2) {

        }
      }
      return F.NIL;
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
              if (!temp.isPresent()) {
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

  public static void initialize() {
    Initializer.init();
  }

  private ComputationalGeometryFunctions() {}
}
