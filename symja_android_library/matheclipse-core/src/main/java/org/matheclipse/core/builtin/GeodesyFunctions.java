package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.numerics.geodesy.ReferenceEllipsoid;

public class GeodesyFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.GeodesyData.setEvaluator(new GeodesyData());
      S.GeoPosition.setEvaluator(new GeoPosition());
      // S.GeoDistance is implemented in the matheclipse-astro module, which measures it along a
      // rhumb line with Orekit's LoxodromeArc. The geodesic solver in
      // org.matheclipse.core.numerics.geodesy stays here and is what FindShortestTour uses.
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
              return GeoPositionExpr.newInstance(list1[0], list1[1]);
            } else if (list1.length == 3) {
              return GeoPositionExpr.newInstance(list1[0], list1[1], list1[2]);
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

  /**
   * <code>GeodesyData(ellipsoid, property)</code> returns a parameter of one of the supported
   * reference ellipsoids.
   */
  private static class GeodesyData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      String name = arg1.toString();
      ReferenceEllipsoid ellipsoid = ReferenceEllipsoid.of(name);
      if (ellipsoid == null) {
        // `1` is not a known reference ellipsoid.
        return Errors.printMessage(S.GeodesyData, "geoell", F.List(arg1), engine);
      }
      if (ast.isAST1()) {
        return F.List(F.stringx("SemimajorAxis"), F.stringx("SemiminorAxis"),
            F.stringx("Flattening"), F.stringx("InverseFlattening"), F.stringx("Eccentricity"));
      }

      IExpr arg2 = ast.arg2();
      if (!arg2.isString()) {
        return F.NIL;
      }
      switch (arg2.toString()) {
        case "SemimajorAxis":
          return meters(ellipsoid.semiMajorAxis());
        case "SemiminorAxis":
          return meters(ellipsoid.semiMinorAxis());
        case "Flattening":
          return F.num(ellipsoid.flattening());
        case "InverseFlattening":
          return F.num(ellipsoid.inverseFlattening());
        case "Eccentricity":
          return F.num(ellipsoid.eccentricity());
        default:
          // `1` is not a known property of the reference ellipsoid `2`.
          return Errors.printMessage(S.GeodesyData, "geoprop", F.List(arg2, arg1), engine);
      }
    }

    private static IExpr meters(double value) {
      return F.Quantity(F.num(value), F.stringx("m"));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      // only reference ellipsoid parameters are supported so far
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private GeodesyFunctions() {}
}
