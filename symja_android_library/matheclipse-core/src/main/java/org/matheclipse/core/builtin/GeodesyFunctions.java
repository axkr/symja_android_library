package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import java.time.DateTimeException;
import java.time.ZoneId;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.data.GeoLocations;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.FileSandbox;
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
      S.GeoPositionXYZ.setEvaluator(new GeoPositionXYZ());
      S.GeoPositionENU.setEvaluator(new GeoPositionENU());
      S.FindGeoLocation.setEvaluator(new FindGeoLocation());
      S.DMSList.setEvaluator(new DMSList());
      S.FromDMS.setEvaluator(new FromDMS());
      // S.GeoDistance is implemented in the matheclipse-astro module, which measures it along a
      // rhumb line with Orekit's LoxodromeArc. The geodesic solver in
      // org.matheclipse.core.numerics.geodesy stays here and is what FindShortestTour uses.
    }
  }

  /**
   * <code>FindGeoLocation()</code> - an estimate of where this computer is, and
   * <code>FindGeoLocation("zone")</code> - the principal city of an IANA time zone.
   *
   * <p>
   * There is no permission-free way for a Java process to ask the operating system where it is, so
   * the estimate comes from the <code>symja.geolocation</code> setting or, failing that, from the
   * time zone (see {@link GeoLocations}). <code>Method -&gt; "Configuration"</code> or
   * <code>"TimeZone"</code> picks one of them. Both describe the host, so outside a kernel on the
   * user's own machine the result is <code>Missing("NotAvailable")</code>; a time zone given as an
   * argument is no host information and works everywhere.
   */
  private static class FindGeoLocation extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      String method = options[0] == S.Automatic ? "Automatic" : options[0].toString();
      if (!"Automatic".equals(method) && !GeoLocations.SOURCE_CONFIGURATION.equals(method)
          && !GeoLocations.SOURCE_TIME_ZONE.equals(method)) {
        // `1` is not a supported computation method in `2`.
        return Errors.printMessage(S.FindGeoLocation, "astromethod", F.List(options[0], ast),
            engine);
      }
      GeoLocations.Estimate estimate;
      if (argSize == 1) {
        if (!ast.arg1().isString() || GeoLocations.SOURCE_CONFIGURATION.equals(method)) {
          // `1` in `2` is not a time zone offset or an IANA time zone name.
          return Errors.printMessage(S.FindGeoLocation, "astrotimezone", F.List(ast.arg1(), ast),
              engine);
        }
        if (!isZoneId(ast.arg1().toString())) {
          return Errors.printMessage(S.FindGeoLocation, "astrotimezone", F.List(ast.arg1(), ast),
              engine);
        }
        estimate = GeoLocations.ofTimeZone(ast.arg1().toString());
      } else if (!FileSandbox.isHostVisible(engine)) {
        estimate = null;
      } else if (GeoLocations.SOURCE_TIME_ZONE.equals(method)) {
        estimate = GeoLocations.ofTimeZone(ZoneId.systemDefault());
      } else if (GeoLocations.SOURCE_CONFIGURATION.equals(method)) {
        estimate = GeoLocations.configured();
      } else {
        estimate = GeoLocations.automatic(engine);
      }
      return estimate == null ? F.Missing(F.stringx("NotAvailable"))
          : GeoLocations.toGeoPosition(estimate);
    }

    private static boolean isZoneId(String name) {
      try {
        ZoneId.of(name);
        return true;
      } catch (DateTimeException dte) {
        return false;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      setOptions(newSymbol, S.Method, S.Automatic);
    }
  }

  private static class GeoPosition extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isList()) {
          double[] list1 = arg1.toDoubleVector();
          if (list1 != null) {
            if (list1.length == 2) {
              return GeoPositionExpr.newInstance(list1[0], list1[1]);
            } else if (list1.length == 3) {
              return GeoPositionExpr.newInstance(list1[0], list1[1], list1[2]);
            }
          }
        } else if (arg1.isAST(S.GeoPositionXYZ) || arg1.isAST(S.GeoPositionENU)) {
          double[] geodetic = toGeodetic(arg1, engine);
          if (geodetic != null) {
            return GeoPositionExpr.newInstance(geodetic[0], geodetic[1], geodetic[2]);
          }
        } else if (arg1.isAST(S.Entity, 3)) {
          return entityPosition((IAST) arg1, engine);
        }
      }
      return F.NIL;
    }

    /**
     * The <code>"Position"</code> of an entity, from the data function registered for its type.
     * Modules register their own types, so core needs none of them: a satellite is answered by the
     * astro module, now.
     */
    private static @AllowNIL IExpr entityPosition(IAST entity, EvalEngine engine) {
      IBuiltInSymbol dataFunction = Entities.dataFunction(entity.arg1());
      if (dataFunction == null) {
        return F.NIL;
      }
      IExpr position = Entities.ask(dataFunction, engine, entity, F.stringx("Position"));
      return position instanceof GeoPositionExpr ? position : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** The datum which a Cartesian position refers to when none is given. */
  private static final String DEFAULT_DATUM = "ITRF00";

  /**
   * The ellipsoid of a <code>GeoPosition</code>, which carries no datum of its own and is taken to
   * be in the default datum <code>"ITRF00"</code>, on the GRS80 ellipsoid.
   */
  private static final ReferenceEllipsoid GEO_POSITION_ELLIPSOID = ReferenceEllipsoid.GRS80;

  /**
   * The reference ellipsoid of a datum name, or <code>null</code> after printing the
   * <code>invdtm</code> message.
   */
  private static ReferenceEllipsoid datumEllipsoid(ISymbol head, IExpr datum, EvalEngine engine) {
    ReferenceEllipsoid ellipsoid = datum.isString() ? ReferenceEllipsoid.of(datum.toString()) : null;
    if (ellipsoid == null) {
      // Unknown geodetic datum `1`.
      Errors.printMessage(head, "invdtm", F.List(datum), engine);
    }
    return ellipsoid;
  }

  /** The three machine numbers of a list <code>{a, b, c}</code>, or <code>null</code>. */
  private static double[] triple(IExpr list) {
    if (list.isList3()) {
      return list.toDoubleVector();
    }
    return null;
  }

  /**
   * The geodetic <code>{latitude, longitude, altitude}</code> of a <code>GeoPosition</code>,
   * <code>GeoPositionXYZ</code> or <code>GeoPositionENU</code> expression, or <code>null</code> if
   * it is none of them or not numeric.
   */
  private static double[] toGeodetic(IExpr position, EvalEngine engine) {
    if (position instanceof GeoPositionExpr) {
      GeoPositionExpr geo = (GeoPositionExpr) position;
      return new double[] {geo.latitude(), geo.longitude(), geo.altitude()};
    }
    if (position.isAST(S.GeoPositionXYZ, 2, 3)) {
      double[] xyz = triple(position.first());
      if (xyz == null) {
        return null;
      }
      IExpr datum = position.argSize() == 2 ? position.second() : F.stringx(DEFAULT_DATUM);
      ReferenceEllipsoid ellipsoid = datumEllipsoid(S.GeoPositionXYZ, datum, engine);
      return ellipsoid == null ? null : ellipsoid.toGeodetic(xyz[0], xyz[1], xyz[2]);
    }
    if (position.isAST(S.GeoPositionENU, 3)) {
      double[] xyz = enuToCartesian(position, engine);
      return xyz == null ? null : GEO_POSITION_ELLIPSOID.toGeodetic(xyz[0], xyz[1], xyz[2]);
    }
    return null;
  }

  /**
   * The Earth-centred Cartesian coordinates of a position expression, on the ellipsoid of
   * <code>ellipsoid</code> when the position is geodetic. A <code>GeoPositionXYZ</code> keeps its
   * own coordinates whatever its datum, since ITRF and WGS 84 axes agree far below a millimetre.
   */
  private static double[] toCartesian(IExpr position, ReferenceEllipsoid ellipsoid,
      EvalEngine engine) {
    if (position.isAST(S.GeoPositionXYZ, 2, 3)) {
      return triple(position.first());
    }
    if (position.isAST(S.GeoPositionENU, 3)) {
      return enuToCartesian(position, engine);
    }
    double[] geodetic = toGeodetic(position, engine);
    return geodetic == null ? null : ellipsoid.toCartesian(geodetic[0], geodetic[1], geodetic[2]);
  }

  /**
   * The rows of the rotation from Earth-centred axes to the local east, north and up axes at the
   * given geodetic latitude and longitude.
   */
  private static double[][] enuAxes(double latitude, double longitude) {
    double phi = Math.toRadians(latitude);
    double lambda = Math.toRadians(longitude);
    double sinPhi = Math.sin(phi);
    double cosPhi = Math.cos(phi);
    double sinLambda = Math.sin(lambda);
    double cosLambda = Math.cos(lambda);
    return new double[][] { //
        {-sinLambda, cosLambda, 0.0}, //
        {-sinPhi * cosLambda, -sinPhi * sinLambda, cosPhi}, //
        {cosPhi * cosLambda, cosPhi * sinLambda, sinPhi}};
  }

  /** Earth-centred coordinates of <code>GeoPositionENU({e, n, u}, reference)</code>. */
  private static double[] enuToCartesian(IExpr enuPosition, EvalEngine engine) {
    double[] enu = triple(enuPosition.first());
    double[] reference = toGeodetic(enuPosition.second(), engine);
    if (enu == null || reference == null) {
      return null;
    }
    double[] origin = GEO_POSITION_ELLIPSOID.toCartesian(reference[0], reference[1], reference[2]);
    double[][] axes = enuAxes(reference[0], reference[1]);
    double[] result = origin.clone();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        // transpose: local axes back to Earth-centred
        result[i] += axes[j][i] * enu[j];
      }
    }
    return result;
  }

  private static IAST realTriple(double[] values) {
    return F.List(F.num(values[0]), F.num(values[1]), F.num(values[2]));
  }

  /**
   * <code>GeoPositionXYZ({x, y, z})</code> or <code>GeoPositionXYZ({x, y, z}, datum)</code> - a
   * position given by Earth-centred, Earth-fixed Cartesian coordinates in meters, in the datum
   * <code>"ITRF00"</code> unless another is given. <code>GeoPositionXYZ(pos)</code> converts a
   * <code>GeoPosition</code> or <code>GeoPositionENU</code>. A position is in the datum
   * <code>"ITRF00"</code>, and a transformation to another datum is not available.
   */
  private static class GeoPositionXYZ extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof GeoPositionExpr || arg1.isAST(S.GeoPositionENU, 3)) {
        if (ast.isAST2()) {
          IExpr datum = ast.arg2();
          if (datumEllipsoid(S.GeoPositionXYZ, datum, engine) == null) {
            return F.NIL;
          }
          if (!datum.isString(DEFAULT_DATUM)) {
            // No direct transformation between datums `1` and `2` is available.
            return Errors.printMessage(S.GeoPositionXYZ, "datum",
                F.List(F.stringx(DEFAULT_DATUM), datum), engine);
          }
        }
        double[] xyz = toCartesian(arg1, GEO_POSITION_ELLIPSOID, engine);
        if (xyz != null) {
          return ast.isAST2() ? F.binaryAST2(S.GeoPositionXYZ, realTriple(xyz), ast.arg2())
              : F.unaryAST1(S.GeoPositionXYZ, realTriple(xyz));
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
   * <code>GeoPositionENU({e, n, u}, ref)</code> - a position given by its east, north and up
   * offsets in meters from the reference position <code>ref</code>.
   * <code>GeoPositionENU(pos, ref)</code> converts a <code>GeoPosition</code> or
   * <code>GeoPositionXYZ</code>.
   */
  private static class GeoPositionENU extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr ref = ast.arg2();
      if (!(ref instanceof GeoPositionExpr) || arg1.isList()) {
        return F.NIL;
      }
      if (arg1 instanceof GeoPositionExpr || arg1.isAST(S.GeoPositionXYZ, 2, 3)
          || arg1.isAST(S.GeoPositionENU, 3)) {
        double[] xyz = toCartesian(arg1, GEO_POSITION_ELLIPSOID, engine);
        if (xyz == null) {
          return F.NIL;
        }
        GeoPositionExpr reference = (GeoPositionExpr) ref;
        double[] origin = GEO_POSITION_ELLIPSOID.toCartesian(reference.latitude(),
            reference.longitude(), reference.altitude());
        double[][] axes = enuAxes(reference.latitude(), reference.longitude());
        double[] enu = new double[3];
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            enu[i] += axes[i][j] * (xyz[j] - origin[j]);
          }
        }
        return F.binaryAST2(S.GeoPositionENU, realTriple(enu), ref);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>GeodesyData(ellipsoid, property)</code> returns a parameter of one of the supported
   * reference ellipsoids.
   */
  private static class GeodesyData extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
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

  /** An angle written <code>"23d30m45s"</code>, the minutes and seconds optional. */
  private static final java.util.regex.Pattern DMS_STRING =
      java.util.regex.Pattern.compile("^\\s*([-+]?)\\s*(\\d+(?:\\.\\d+)?)\\s*[d°]\\s*"
          + "(?:(\\d+(?:\\.\\d+)?)\\s*[m'′]\\s*)?(?:(\\d+(?:\\.\\d+)?)\\s*(?:s|\"|″)\\s*)?"
          + "([NnSsEeWw]?)\\s*$");

  /**
   * The degrees, minutes and seconds of an angle string like <code>"23d30m45s"</code> or
   * <code>"23d30m S"</code>. A component written with a decimal point is a machine number, the
   * others are integers. A leading minus sign or the direction south or west negates every
   * component.
   *
   * @return <code>null</code> if the string is no such angle
   */
  private static IAST parseDMS(String str) {
    java.util.regex.Matcher matcher = DMS_STRING.matcher(str);
    if (!matcher.matches()) {
      return null;
    }
    String direction = matcher.group(5).toUpperCase(java.util.Locale.US);
    boolean negative =
        "-".equals(matcher.group(1)) ^ ("S".equals(direction) || "W".equals(direction));
    IASTAppendable result = F.ListAlloc(3);
    for (int i = 2; i <= 4; i++) {
      String digits = matcher.group(i);
      IExpr component = digits == null ? F.C0
          : digits.indexOf('.') >= 0 ? F.num(Double.parseDouble(digits))
              : F.ZZ(new java.math.BigInteger(digits));
      result.append(negative ? component.negate() : component);
    }
    return result;
  }

  /**
   * The angle in degrees which <code>arg</code> gives: a real number, or an angle quantity.
   *
   * @return {@link F#NIL} if <code>arg</code> is no angle
   */
  private static @AllowNIL IExpr toDegrees(IExpr arg, EvalEngine engine) {
    if (arg.isQuantity()) {
      IExpr degrees = arg;
      if (!degrees.second().isString(ANGULAR_DEGREES)) {
        degrees = engine.evaluate(F.UnitConvert(arg, F.stringx(ANGULAR_DEGREES)));
      }
      if (!degrees.isQuantity() || !degrees.second().isString(ANGULAR_DEGREES)) {
        return F.NIL;
      }
      arg = degrees.first();
    }
    return arg.isReal() ? arg : F.NIL;
  }

  private static final String ANGULAR_DEGREES = "AngularDegrees";

  /**
   * <code>DMSList(angle)</code> - the degrees, minutes and seconds of an angle in degrees, an angle
   * quantity or an angle string.
   */
  private static class DMSList extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        IAST dms = parseDMS(arg1.toString());
        return dms == null ? F.NIL : dms;
      }
      IExpr degrees = toDegrees(arg1, engine);
      if (degrees.isNIL()) {
        return F.NIL;
      }
      if (degrees.isMachineNumber()) {
        // in machine arithmetic, so that the seconds keep the rounding of the subtractions
        double x = degrees.evalf();
        if (!Double.isFinite(x) || Math.abs(x) >= 1.0e15) {
          return F.NIL;
        }
        double d = x < 0.0 ? Math.ceil(x) : Math.floor(x);
        double r = (x - d) * 60.0;
        double m = r < 0.0 ? Math.ceil(r) : Math.floor(r);
        return F.List(F.ZZ((long) d), F.ZZ((long) m), F.num((r - m) * 60.0));
      }
      if (!degrees.isRational() && !degrees.isInexactNumber()) {
        return F.NIL;
      }
      IExpr d = engine.evaluate(F.IntegerPart(degrees));
      IExpr r = engine.evaluate(F.Times(F.ZZ(60), F.Subtract(degrees, d)));
      IExpr m = engine.evaluate(F.IntegerPart(r));
      return F.List(d, m, engine.evaluate(F.Times(F.ZZ(60), F.Subtract(r, m))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>FromDMS({d, m, s})</code> - the angle in degrees of the given degrees, minutes and
   * seconds, also given as <code>{d, m}</code>, <code>d</code> or an angle string.
   */
  private static class FromDMS extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        arg1 = parseDMS(arg1.toString());
        if (arg1 == null) {
          return F.NIL;
        }
      }
      if (arg1.isReal()) {
        return arg1;
      }
      if (!arg1.isList() || arg1.argSize() < 1 || arg1.argSize() > 3
          || !((IAST) arg1).forAll(IExpr::isReal)) {
        return F.NIL;
      }
      IAST dms = (IAST) arg1;
      IASTAppendable sum = F.PlusAlloc(3);
      sum.append(dms.arg1());
      if (dms.argSize() >= 2) {
        sum.append(F.Times(F.QQ(1, 60), dms.arg2()));
      }
      if (dms.argSize() == 3) {
        sum.append(F.Times(F.QQ(1, 3600), dms.arg3()));
      }
      return engine.evaluate(sum);
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
