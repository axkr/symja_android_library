package org.matheclipse.core.builtin;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class GeodesyFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.GeoDistance.setEvaluator(new GeoDistance());
      S.GeoPosition.setEvaluator(new GeoPosition());
    }
  }

  private static class GeoDistance extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr lhs = ast.arg1();
      IExpr rhs = ast.arg2();
      if (lhs instanceof GeoPositionExpr && rhs instanceof GeoPositionExpr) {
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;

        GlobalPosition p1 = ((GeoPositionExpr) lhs).toData();
        GlobalPosition p2 = ((GeoPositionExpr) rhs).toData();

        GeodeticMeasurement gm = geoCalc.calculateGeodeticMeasurement(reference, p1, p2);
        return F.UnitConvert(F.Quantity(F.num(gm.getPointToPointDistance()), F.stringx("m")),
            F.stringx("mi"));
      }
      if (ast.arg1().isList2() && ast.arg2().isList2()) {
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;

        double[] list1 = ast.arg1().toDoubleVector();
        double[] list2 = ast.arg2().toDoubleVector();
        if (list1 != null && list2 != null) {
          GlobalPosition p1 = new GlobalPosition(list1[0], list1[1], 0.0);
          GlobalPosition p2 = new GlobalPosition(list2[0], list2[1], 0.0);

          GeodeticMeasurement gm = geoCalc.calculateGeodeticMeasurement(reference, p1, p2);
          return F.UnitConvert(F.Quantity(F.num(gm.getPointToPointDistance()), F.stringx("m")),
              F.stringx("mi"));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class GeoPosition extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        if (ast.arg1().isList()) {
          double[] list1 = ast.arg1().toDoubleVector();
          if (list1 != null) {
            if (list1.length == 2) {
              return GeoPositionExpr.newInstance(new GlobalPosition(list1[0], list1[1], 0.0));
            } else if (list1.length == 3) {
              return GeoPositionExpr.newInstance(new GlobalPosition(list1[0], list1[1], list1[2]));
            }
          }
        }
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

  private GeodesyFunctions() {}
}
