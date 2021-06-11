package org.matheclipse.core.builtin;

import java.util.Iterator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class ComputationalGeometryFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ConvexHullMesh.setEvaluator(new ConvexHullMesh());
      S.CoplanarPoints.setEvaluator(new CoplanarPoints());
    }
  }

  private static class ConvexHullMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {}
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

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
              return IOFunctions.printMessage(ast.topHead(), "pts", F.List(listOfPoints), engine);
            }
          }
          return S.True;
        } else if (ast.arg1().argSize() > 3
            && listOfPoints.arg1().isList3()
            && listOfPoints.arg2().isList3()
            && listOfPoints.arg3().isList3()) {

          IASTAppendable result = F.ast(S.And, listOfPoints.size() - 3, false);
          for (int i = 4; i < listOfPoints.size(); i++) {
            IAST p1 = (IAST) listOfPoints.get(i - 3);
            IAST p2 = (IAST) listOfPoints.get(i - 2);
            IAST p3 = (IAST) listOfPoints.get(i - 1);
            // equation of plane is: a*x + b*y + c*z = 0
            if (listOfPoints.get(i).isList3()) {
              IAST p4 = (IAST) listOfPoints.get(i);
              IExpr temp = coplanarPoints(p1, p2, p3, p4, engine);
              result.append(temp);
            } else {
              // `1` should be a non-empty list of points.
              return IOFunctions.printMessage(ast.topHead(), "pts", F.List(listOfPoints), engine);
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
    private static IExpr coplanarPoints(IAST p1, IAST p2, IAST p3, IAST p4, EvalEngine engine) {
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

      IExpr times1 =
          S.Times.of(
              engine,
              a1,
              F.Plus(F.Times(py, a22), F.Times(x3, y1), F.Times(-1, x1, y3), F.Times(px, b2)));
      IExpr times2 =
          S.Times.of(
              engine,
              b1,
              F.Plus(F.Times(pz, a22), F.Times(x3, z1), F.Times(-1, x1, z3), F.Times(px, c2)));

      IExpr times3 =
          S.Times.of(
              engine,
              c1,
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

  public static void initialize() {
    Initializer.init();
  }

  private ComputationalGeometryFunctions() {}
}
