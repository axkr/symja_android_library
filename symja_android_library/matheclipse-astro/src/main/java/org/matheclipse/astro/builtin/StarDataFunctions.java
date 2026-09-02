package org.matheclipse.astro.builtin;

import java.util.List;
import java.util.Locale;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.AstroObserver;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.sky.SkyCatalog;
import org.matheclipse.astro.sky.StarProvider;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.TrackingCoordinates;

/**
 * <code>StarData(star, property)</code> - properties of a catalogued star.
 *
 * <p>
 * Positions, magnitudes and designations come from the bundled catalogue; anything which depends on
 * where and when you are looking - altitude, azimuth, rise, set and transit - is computed through
 * {@link StarProvider}, which presents the star to the same Orekit machinery the solar system
 * functions use.
 *
 * <p>
 * The catalogue carries no astrophysics, so mass, radius, distance, parallax, spectral class and
 * luminosity are reported as unsupported rather than guessed at. Adding them would mean bundling
 * the HYG database, which is share-alike licensed; see the README beside the resources.
 */
public class StarDataFunctions {

  /** The properties returned when none is asked for. */
  private static final String[] DEFAULT_PROPERTIES =
      {"Name", "RightAscension", "Declination", "ApparentMagnitude", "Constellation"};

  /** Everything {@link #property} can return, in a readable order. */
  private static final String[] ALL_PROPERTIES =
      {"Name", "AlternateNames", "Position", "RightAscension", "Declination", "ApparentMagnitude",
          "BVColorIndex", "Color", "Constellation", "ConstellationCode", "BayerName",
          "FlamsteedName", "HDNumber", "HipparcosNumber", "GlieseName", "VariableName", "Altitude",
          "Azimuth", "RiseTime", "SetTime", "TransitTime", "DailyTimeAboveHorizon"};

  /**
   * Properties which this catalogue cannot support. Listed so the message can say the property is
   * real but unavailable, rather than simply unknown.
   */
  private static final String[] UNSUPPORTED_PROPERTIES =
      {"mass", "radius", "distance", "distancefromearth", "parallax", "spectralclass", "startype",
          "luminosity", "effectivetemperature", "density", "gravity", "age", "rotationperiod",
          "orbitperiod", "absolutemagnitude", "absolutemagnitudebolometric", "propermotion",
          "radialvelocity", "satellites", "eccentricity", "semimajoraxis"};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.StarData.setEvaluator(new StarData());
    }
  }

  private static final class StarData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the meta forms need no catalogue lookup and no Orekit data
      if (ast.isAST1() && ast.arg1().isString()) {
        String argument = ast.arg1().toString();
        if ("Properties".equalsIgnoreCase(argument)) {
          return stringList(ALL_PROPERTIES);
        }
      }
      if (ast.isAST0()) {
        return namedStarList();
      }

      IExpr starSpec = ast.arg1();
      if (starSpec.isList()) {
        IAST stars = (IAST) starSpec;
        IASTAppendable result = F.ListAlloc(stars.argSize());
        for (int i = 1; i <= stars.argSize(); i++) {
          IExpr value = evaluateSingle(stars.get(i), ast, engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          result.append(value);
        }
        return result;
      }
      return evaluateSingle(starSpec, ast, engine);
    }

    /** One star, with the property and any location or date taken from the outer call. */
    private IExpr evaluateSingle(IExpr starSpec, IAST ast, EvalEngine engine) {
      if (!starSpec.isString()) {
        return F.NIL;
      }
      SkyCatalog.Star star = SkyCatalog.get().star(starSpec.toString());
      if (star == null) {
        // `1` is not a star in the bundled catalogue.
        return Errors.printMessage(S.StarData, "astrostar", F.List(starSpec, ast), engine);
      }

      String propertyName = null;
      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
          continue;
        }
        if (arg.isString()) {
          propertyName = arg.toString();
          continue;
        }
        return AstroConvert.reportUnreadableArgument(S.StarData, arg, ast, engine);
      }

      if (propertyName == null) {
        IASTAppendable rules = F.ListAlloc(DEFAULT_PROPERTIES.length);
        for (String name : DEFAULT_PROPERTIES) {
          IExpr value = property(name, star, point, date, F.stringx(name), ast, engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          rules.append(F.Rule(F.stringx(name), value));
        }
        return F.assoc(rules);
      }
      return property(propertyName, star, point, date, F.stringx(propertyName), ast, engine);
    }

    private IExpr property(String name, SkyCatalog.Star star, GeodeticPoint point,
        AbsoluteDate date, IExpr nameExpr, IAST ast, EvalEngine engine) {
      switch (name.toLowerCase(Locale.US)) {
        case "name":
          return star.properName.isEmpty() ? S.Missing : F.stringx(star.properName);
        case "alternatenames":
          return alternateNames(star);
        case "position":
          return F.List(AstroConvert.degrees(FastMath.toRadians(star.rightAscension)),
              AstroConvert.degrees(FastMath.toRadians(star.declination)));
        case "rightascension":
          return AstroConvert.degrees(FastMath.toRadians(star.rightAscension));
        case "declination":
          return AstroConvert.degrees(FastMath.toRadians(star.declination));
        case "apparentmagnitude":
          return Double.isNaN(star.magnitude) ? S.Missing : F.num(star.magnitude);
        case "bvcolorindex":
          return Double.isNaN(star.colorIndex) ? S.Missing : F.num(star.colorIndex);
        case "color":
          return Double.isNaN(star.colorIndex) ? S.Missing : F.stringx(colorName(star.colorIndex));
        case "constellation": {
          SkyCatalog.Constellation constellation =
              SkyCatalog.get().constellation(star.constellation);
          return constellation == null ? S.Missing : F.stringx(constellation.name);
        }
        case "constellationcode":
          return star.constellation.isEmpty() ? S.Missing : F.stringx(star.constellation);
        case "bayername":
          return star.bayer.isEmpty() ? S.Missing : F.stringx(star.bayer);
        case "flamsteedname":
          return star.flamsteed.isEmpty() ? S.Missing : F.stringx(star.flamsteed);
        case "hdnumber":
          return star.henryDraper.isEmpty() ? S.Missing : F.stringx(star.henryDraper);
        case "hipparcosnumber":
          return F.ZZ(star.hipparcos);
        case "gliesename":
          return star.gliese.isEmpty() ? S.Missing : F.stringx(star.gliese);
        case "variablename":
          return star.variable.isEmpty() ? S.Missing : F.stringx(star.variable);
        case "altitude":
        case "azimuth":
        case "risetime":
        case "settime":
        case "transittime":
        case "dailytimeabovehorizon":
          return observedProperty(name, star, point, date, nameExpr, ast, engine);
        default:
          if (isKnownButUnsupported(name)) {
            // `1` needs data which is not bundled: `2`
            return Errors.printMessage(S.StarData, "astrostarprop",
                F.List(nameExpr, F.stringx("the HYG database, which is not bundled")), engine);
          }
          return Errors.printMessage(S.StarData, "astroprop", F.List(nameExpr, ast), engine);
      }
    }

    /** The properties which depend on an observer, and so need the Orekit data and a location. */
    private IExpr observedProperty(String name, SkyCatalog.Star star, GeodeticPoint point,
        AbsoluteDate date, IExpr nameExpr, IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.StarData, engine)) {
        return F.NIL;
      }
      if (point == null) {
        // these mean nothing without somewhere to stand
        return Errors.printMessage(S.StarData, "astroloc", F.List(nameExpr, ast), engine);
      }
      AbsoluteDate when = date == null ? AstroConvert.nowUTC() : date;
      try {
        StarProvider provider = new StarProvider(star.rightAscension, star.declination);
        // a star is a point source, so there is no limb to allow for
        AstroObserver observer = new AstroObserver(point, provider, 0.0);
        switch (name.toLowerCase(Locale.US)) {
          case "altitude": {
            TrackingCoordinates tracking = observer.tracking(when);
            return AstroConvert.degrees(tracking.getElevation());
          }
          case "azimuth": {
            TrackingCoordinates tracking = observer.tracking(when);
            return AstroConvert.degreesPositive(tracking.getAzimuth());
          }
          case "risetime":
            return riseSet(star, point, when, "Rise");
          case "settime":
            return riseSet(star, point, when, "Set");
          case "transittime":
            return riseSet(star, point, when, "UpperCulmination");
          case "dailytimeabovehorizon":
            return dailyTimeAboveHorizon(star, point, when);
          default:
            return Errors.printMessage(S.StarData, "astroprop", F.List(nameExpr, ast), engine);
        }
      } catch (OrekitException oex) {
        return Errors.printMessage(S.StarData, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    private static IExpr riseSet(SkyCatalog.Star star, GeodeticPoint point, AbsoluteDate date,
        String eventType) {
      AbsoluteDate found = AstroEventFunctions.riseSetDateOf(
          new StarProvider(star.rightAscension, star.declination), 0.0, eventType, point, date, 1);
      return found == null ? S.Missing : AstroConvert.toDateObject(found);
    }

    /** How long the star is up between one rise and the following set. */
    private static IExpr dailyTimeAboveHorizon(SkyCatalog.Star star, GeodeticPoint point,
        AbsoluteDate date) {
      StarProvider provider = new StarProvider(star.rightAscension, star.declination);
      AbsoluteDate rise = AstroEventFunctions.riseSetDateOf(provider, 0.0, "Rise", point, date, 1);
      if (rise == null) {
        return S.Missing;
      }
      AbsoluteDate set = AstroEventFunctions.riseSetDateOf(provider, 0.0, "Set", point, rise, 1);
      if (set == null) {
        return S.Missing;
      }
      return F.Quantity(F.num(set.durationFrom(rise) / 3600.0), F.stringx("Hours"));
    }

    /** Every designation the catalogue records, for {@code "AlternateNames"}. */
    private static IExpr alternateNames(SkyCatalog.Star star) {
      IASTAppendable result = F.ListAlloc(6);
      if (!star.bayer.isEmpty() && !star.constellation.isEmpty()) {
        result.append(F.stringx(star.bayer + " " + star.constellation));
      }
      if (!star.flamsteed.isEmpty() && !star.constellation.isEmpty()) {
        result.append(F.stringx(star.flamsteed + " " + star.constellation));
      }
      for (String designation : new String[] {star.henryDraper, star.gliese, star.variable}) {
        if (!designation.isEmpty()) {
          result.append(F.stringx(designation));
        }
      }
      result.append(F.stringx("HIP " + star.hipparcos));
      return result;
    }

    /**
     * The rough visual colour a B-V index corresponds to. The boundaries are the conventional
     * spectral class divisions, so this is the colour of the class rather than a rendered hue.
     */
    private static String colorName(double colorIndex) {
      if (colorIndex < -0.05) {
        return "blue";
      }
      if (colorIndex < 0.15) {
        return "blue-white";
      }
      if (colorIndex < 0.45) {
        return "white";
      }
      if (colorIndex < 0.8) {
        return "yellow-white";
      }
      if (colorIndex < 1.4) {
        return "orange";
      }
      return "red";
    }

    private static boolean isKnownButUnsupported(String name) {
      String key = name.toLowerCase(Locale.US);
      for (String unsupported : UNSUPPORTED_PROPERTIES) {
        if (unsupported.equals(key)) {
          return true;
        }
      }
      return false;
    }

    private static IExpr stringList(String[] values) {
      IASTAppendable result = F.ListAlloc(values.length);
      for (String value : values) {
        result.append(F.stringx(value));
      }
      return result;
    }

    /** The stars which have a proper name, brightest first. */
    private static IExpr namedStarList() {
      List<SkyCatalog.Star> stars = SkyCatalog.get().namedStars();
      IASTAppendable result = F.ListAlloc(stars.size());
      for (SkyCatalog.Star star : stars) {
        result.append(F.stringx(star.properName));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public int status() {
      // the astrophysical properties need a catalogue which is not bundled
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private StarDataFunctions() {}
}
