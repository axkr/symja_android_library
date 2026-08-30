package org.matheclipse.astro.builtin;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.EclipseGeometry;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.solve.DateRootFinder;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
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
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

/**
 * Solar and lunar eclipses.
 *
 * <p>
 * A solar eclipse can only happen at a new moon and a lunar one only at a full moon, so the search
 * walks the syzygies that {@link AstroEventFunctions} already finds and tests the shadow geometry at
 * each. That is much cheaper than scanning the calendar, and it is also why the accuracy of these
 * dates is the accuracy of the phase search.
 *
 * <p>
 * The circumstances come from {@link EclipseGeometry}, which works directly with the Sun, Moon and
 * Earth positions. Everything that needs the Besselian element machinery of an eclipse canon - the
 * contact times, the shadow track, the saros and inex series numbers - is reported as unsupported
 * rather than approximated.
 */
public class AstroEclipseFunctions {

  /** Mean synodic month in seconds; one step from one syzygy to the next. */
  private static final double SYNODIC_MONTH = 29.530588853 * Constants.JULIAN_DAY;

  /** How many syzygies to test before giving up. About twenty years of them. */
  private static final int MAX_SYZYGIES = 250;

  /** Scan step for refining the instant of greatest eclipse. */
  private static final double GREATEST_STEP = 600.0;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.SolarEclipse.setEvaluator(new SolarEclipse());
      S.LunarEclipse.setEvaluator(new LunarEclipse());
      S.FindSolarEclipse.setEvaluator(new FindSolarEclipse());
    }
  }

  /** The kinds a solar eclipse can be. */
  private enum SolarType {
    PARTIAL("Partial"), ANNULAR("Annular"), TOTAL("Total");

    final String name;

    SolarType(String name) {
      this.name = name;
    }

    /** Whether this kind satisfies the {@code EclipseType} option value {@code filter}. */
    boolean matches(String filter) {
      if (filter == null || "Automatic".equalsIgnoreCase(filter)) {
        return true;
      }
      if ("Central".equalsIgnoreCase(filter) || "Umbral".equalsIgnoreCase(filter)) {
        return this != PARTIAL;
      }
      return name.equalsIgnoreCase(filter);
    }
  }

  /** The kinds a lunar eclipse can be. */
  private enum LunarType {
    PENUMBRAL("Penumbral"), PARTIAL("Partial"), TOTAL("Total");

    final String name;

    LunarType(String name) {
      this.name = name;
    }

    boolean matches(String filter) {
      if (filter == null || "Automatic".equalsIgnoreCase(filter)) {
        return true;
      }
      if ("Umbral".equalsIgnoreCase(filter)) {
        return this != PENUMBRAL;
      }
      return name.equalsIgnoreCase(filter);
    }
  }

  /**
   * Refine a syzygy to the instant of greatest eclipse, which is the minimum of the distance
   * between the shadow axis and the centre of the shadowed body.
   */
  private static AbsoluteDate greatestEclipse(AbsoluteDate syzygy, boolean solar) {
    DateRootFinder.DateFunction distance = solar
        ? d -> EclipseGeometry.at(d).shadowAxisClosestPoint().getNorm()
        : d -> EclipseGeometry.at(d).moonToEarthShadowAxis();
    // the minimum lies within a few hours of the syzygy, so start half a day early and let the
    // extremum search walk forward into it
    AbsoluteDate found = DateRootFinder.findExtremum(distance,
        syzygy.shiftedBy(-0.5 * Constants.JULIAN_DAY), GREATEST_STEP, Constants.JULIAN_DAY, 1,
        false);
    return found == null ? syzygy : found;
  }

  /** Classify a solar eclipse, or <code>null</code> if there is none at this instant. */
  private static SolarType solarTypeAt(EclipseGeometry geometry) {
    if (!geometry.isSolarEclipse()) {
      return null;
    }
    if (!geometry.isCentral()) {
      return SolarType.PARTIAL;
    }
    return geometry.isTotal() ? SolarType.TOTAL : SolarType.ANNULAR;
  }

  /** Classify a lunar eclipse, or <code>null</code> if there is none at this instant. */
  private static LunarType lunarTypeAt(EclipseGeometry geometry) {
    double distance = geometry.moonToEarthShadowAxis();
    if (distance < geometry.earthUmbraRadius() - EclipseGeometry.MOON_RADIUS) {
      return LunarType.TOTAL;
    }
    if (distance < geometry.earthUmbraRadius() + EclipseGeometry.MOON_RADIUS) {
      return LunarType.PARTIAL;
    }
    if (distance < geometry.earthPenumbraRadius() + EclipseGeometry.MOON_RADIUS) {
      return LunarType.PENUMBRAL;
    }
    return null;
  }

  /**
   * Walk the new or full moons from {@code date} and return the first at which an eclipse of an
   * acceptable kind happens.
   *
   * @return the instant of greatest eclipse, or <code>null</code> if none was found
   */
  private static AbsoluteDate findEclipse(AbsoluteDate date, int direction, boolean solar,
      String typeFilter) {
    double targetPhase = solar ? 0.0 : FastMath.PI;
    AbsoluteDate from = date;
    for (int i = 0; i < MAX_SYZYGIES; i++) {
      AbsoluteDate syzygy = AstroEventFunctions.findPhase(from, targetPhase, direction);
      if (syzygy == null) {
        return null;
      }
      AbsoluteDate greatest = greatestEclipse(syzygy, solar);
      EclipseGeometry geometry = EclipseGeometry.at(greatest);
      if (solar) {
        SolarType type = solarTypeAt(geometry);
        if (type != null && type.matches(typeFilter) && isAfter(greatest, date, direction)) {
          return greatest;
        }
      } else {
        LunarType type = lunarTypeAt(geometry);
        if (type != null && type.matches(typeFilter) && isAfter(greatest, date, direction)) {
          return greatest;
        }
      }
      // step past this syzygy so the phase search does not lock onto it again
      from = syzygy.shiftedBy(direction < 0 ? -0.5 * SYNODIC_MONTH : 0.5 * SYNODIC_MONTH);
    }
    return null;
  }

  /**
   * Whether {@code candidate} really lies on the requested side of {@code reference}. Refining a
   * syzygy to greatest eclipse can move it by hours, which occasionally carries it back across the
   * starting date.
   */
  private static boolean isAfter(AbsoluteDate candidate, AbsoluteDate reference, int direction) {
    double offset = candidate.durationFrom(reference);
    return direction < 0 ? offset < 0.0 : offset > 0.0;
  }

  /** Read the {@code EclipseType} option, which may be {@link S#Automatic} or a string. */
  private static String typeFilter(IExpr option) {
    return option == null || option == S.Automatic || !option.isString() ? null
        : option.toString();
  }

  /** Shared argument handling for {@link S#SolarEclipse} and {@link S#LunarEclipse}. */
  private abstract static class AbstractEclipse extends AbstractFunctionOptionEvaluator {

    protected abstract IBuiltInSymbol symbol();

    /** Whether this is the solar or the lunar variant. */
    protected abstract boolean isSolar();

    /** The property values this variant knows about. */
    protected abstract IExpr property(String name, EclipseGeometry geometry, IExpr nameExpr,
        IAST ast, EvalEngine engine);

    /** The property returned when none is asked for. */
    protected String defaultProperty() {
      return "MaximumEclipseDate";
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(symbol(), engine)) {
        return F.NIL;
      }
      AbsoluteDate date = null;
      String propertyName = null;
      for (int i = 1; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
        } else if (arg.isString()) {
          propertyName = arg.toString();
        } else {
          return AstroConvert.reportUnreadableArgument(symbol(), arg, ast, engine);
        }
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      if (propertyName == null) {
        propertyName = defaultProperty();
      }
      int direction = options[1] != null && options[1].isReal() && options[1].evalf() < 0 ? -1 : 1;

      try {
        AbsoluteDate greatest =
            findEclipse(date, direction, isSolar(), typeFilter(options[0]));
        if (greatest == null) {
          return S.Missing;
        }
        return property(propertyName, EclipseGeometry.at(greatest), F.stringx(propertyName), ast,
            engine);
      } catch (OrekitException oex) {
        return Errors.printMessage(symbol(), "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.EclipseType, S.TimeDirection, S.TimeZone, S.CalendarType,
              S.DateGranularity}, //
          new IExpr[] {S.Automatic, F.C1, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      // the Besselian elements, the contact points, the shadow track polygons and the saros and
      // inex series numbers all need an eclipse canon and are not implemented
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** <code>SolarEclipse(date, property)</code>. */
  private static class SolarEclipse extends AbstractEclipse {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.SolarEclipse;
    }

    @Override
    protected boolean isSolar() {
      return true;
    }

    @Override
    protected IExpr property(String name, EclipseGeometry geometry, IExpr nameExpr, IAST ast,
        EvalEngine engine) {
      SolarType type = solarTypeAt(geometry);
      switch (name.toLowerCase(Locale.US)) {
        case "maximumeclipsedate":
          return AstroConvert.toDateObject(geometry.date);
        case "type":
          return F.stringx(type == null ? "None" : type.name);
        case "gamma":
          return F.num(geometry.gamma());
        case "central":
          return geometry.isCentral() ? S.True : S.False;
        case "umbral":
          return type == SolarType.TOTAL || type == SolarType.ANNULAR ? S.True : S.False;
        case "maximumeclipseposition": {
          GeodeticPoint point = geometry.greatestEclipsePoint();
          return point == null ? S.Missing
              : GeoPositionExpr.newInstance(FastMath.toDegrees(point.getLatitude()),
                  FastMath.toDegrees(point.getLongitude()));
        }
        case "magnitude":
        case "maximumeclipsemagnitude":
          return F.num(magnitudeAtGreatest(geometry));
        case "maximumeclipseobscuration":
          return F.num(obscurationAtGreatest(geometry));
        default:
          return Errors.printMessage(S.SolarEclipse, "astroprop", F.List(nameExpr, ast), engine);
      }
    }

    /** The eclipse magnitude as seen from the point of greatest eclipse. */
    private static double magnitudeAtGreatest(EclipseGeometry geometry) {
      Vector3D observer = greatestObserver(geometry);
      return observer == null ? 0.0 : geometry.magnitudeAt(observer);
    }

    private static double obscurationAtGreatest(EclipseGeometry geometry) {
      Vector3D observer = greatestObserver(geometry);
      return observer == null ? 0.0 : geometry.obscurationAt(observer);
    }

    /** The point of greatest eclipse as a geocentric vector in the inertial frame. */
    private static Vector3D greatestObserver(EclipseGeometry geometry) {
      GeodeticPoint point = geometry.greatestEclipsePoint();
      if (point == null) {
        return null;
      }
      // the ellipsoid returns the point in the Earth rotating frame; the shadow geometry lives in
      // the inertial one
      Vector3D inBodyFrame = AstroConvert.earthEllipsoid().transform(point);
      return AstroConvert.earthFrame()
          .getStaticTransformTo(FramesFactory.getGCRF(), geometry.date)
          .transformPosition(inBodyFrame);
    }
  }

  /** <code>LunarEclipse(date, property)</code>. */
  private static final class LunarEclipse extends AbstractEclipse {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.LunarEclipse;
    }

    @Override
    protected boolean isSolar() {
      return false;
    }

    @Override
    protected IExpr property(String name, EclipseGeometry geometry, IExpr nameExpr, IAST ast,
        EvalEngine engine) {
      LunarType type = lunarTypeAt(geometry);
      switch (name.toLowerCase(Locale.US)) {
        case "maximumeclipsedate":
          return AstroConvert.toDateObject(geometry.date);
        case "type":
          return F.stringx(type == null ? "None" : type.name);
        case "gamma":
          return F.num(geometry.lunarGamma());
        case "umbral":
          return type == LunarType.TOTAL || type == LunarType.PARTIAL ? S.True : S.False;
        case "magnitude":
        case "maximumeclipsemagnitude":
        case "umbralmagnitude":
          return F.num(geometry.umbralMagnitude());
        case "penumbralmagnitude":
          return F.num(geometry.penumbralMagnitude());
        case "maximumeclipseposition": {
          // the Moon is overhead at the point where the eclipse is seen highest
          GeodeticPoint point = AstroConvert.earthEllipsoid().transform(geometry.moon,
              FramesFactory.getGCRF(), geometry.date);
          return GeoPositionExpr.newInstance(FastMath.toDegrees(point.getLatitude()),
              FastMath.toDegrees(point.getLongitude()));
        }
        default:
          return Errors.printMessage(S.LunarEclipse, "astroprop", F.List(nameExpr, ast), engine);
      }
    }
  }

  /**
   * <code>FindSolarEclipse(date)</code> - the date of the next solar eclipse, which is
   * <code>SolarEclipse(date, "MaximumEclipseDate")</code> written the other way round.
   */
  private static final class FindSolarEclipse extends SolarEclipse {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.FindSolarEclipse;
    }

    @Override
    protected String defaultProperty() {
      return "MaximumEclipseDate";
    }
  }

  /** Every property name the two eclipse functions accept, for the documentation. */
  static IASTAppendable supportedProperties() {
    IASTAppendable list = F.ListAlloc(12);
    for (String name : new String[] {"MaximumEclipseDate", "Type", "Gamma", "Central", "Umbral",
        "MaximumEclipsePosition", "MaximumEclipseMagnitude", "MaximumEclipseObscuration",
        "UmbralMagnitude", "PenumbralMagnitude"}) {
      list.append(F.stringx(name));
    }
    return list;
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroEclipseFunctions() {}
}
