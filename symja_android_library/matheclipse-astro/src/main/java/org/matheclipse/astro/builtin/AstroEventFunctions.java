package org.matheclipse.astro.builtin;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.AstroObserver;
import org.matheclipse.astro.convert.ReferenceAltitudes;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.solve.DateRootFinder;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Rise, set and culmination of a celestial body, plus the Moon phase and the lunation numbering
 * which is derived from it.
 *
 * <p>
 * Everything here is a root of a smooth function of the date, found with
 * {@link DateRootFinder} rather than with Orekit's event detectors - see that class for why.
 */
public class AstroEventFunctions {

  /** Mean synodic month in seconds, the average interval between two new moons. */
  private static final double SYNODIC_MONTH = 29.530588853 * Constants.JULIAN_DAY;

  /**
   * The new moon which starts lunation 1 in Brown's numbering, 1923-01-17. Used as the origin of
   * {@link S#LunationNumber}.
   */
  private static final String BROWN_LUNATION_1 = "1923-01-17T02:41:00.000";

  /** Scan step for a rise or set search: the elevation cannot have two roots inside 10 minutes. */
  private static final double RISE_SET_STEP = 600.0;

  /** How far a rise or set search runs before giving up - polar day and night can be long. */
  private static final double RISE_SET_HORIZON = 400.0 * Constants.JULIAN_DAY;

  /** Scan step for a moon phase search. */
  private static final double PHASE_STEP = 0.25 * Constants.JULIAN_DAY;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Sunrise.setEvaluator(new Sunrise());
      S.Sunset.setEvaluator(new Sunset());
      S.AstroRiseSet.setEvaluator(new AstroRiseSet());
      S.DaylightQ.setEvaluator(new DaylightQ());
      S.MoonPhase.setEvaluator(new MoonPhase());
      S.MoonPhaseDate.setEvaluator(new MoonPhaseDate());
      S.NewMoon.setEvaluator(new NewMoon());
      S.FullMoon.setEvaluator(new FullMoon());
      S.LunationNumber.setEvaluator(new LunationNumber());
      S.FromLunationNumber.setEvaluator(new FromLunationNumber());
    }
  }

  /**
   * Collects the location and date arguments, which every function in this class accepts in either
   * order and in any subset.
   */
  private static final class Arguments {
    GeodeticPoint point;
    AbsoluteDate date;
    IExpr rejected;

    static Arguments of(IAST ast, int from, int argSize) {
      Arguments arguments = new Arguments();
      for (int i = from; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          arguments.point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          arguments.date = argDate;
          continue;
        }
        arguments.rejected = arg;
        return arguments;
      }
      if (arguments.date == null) {
        arguments.date = AstroConvert.nowUTC();
      }
      return arguments;
    }
  }

  /** Shared implementation of {@link S#Sunrise}, {@link S#Sunset} and {@link S#AstroRiseSet}. */
  private static IExpr riseSet(IBuiltInSymbol symbol, String bodyName, String eventType,
      GeodeticPoint point, AbsoluteDate date, ReferenceAltitudes reference, int direction,
      EvalEngine engine) {
    AbsoluteDate found = riseSetDate(bodyName, eventType, point, date, reference, direction);
    if (found == null) {
      // the body never reaches the reference altitude, e.g. polar day or polar night
      return S.Missing;
    }
    return AstroConvert.toDateObject(found);
  }

  /** As {@link #riseSet} but for anything {@link AstroBodies#target} resolved. */
  private static IExpr riseSetOf(AstroBodies.Target target, String eventType, GeodeticPoint point,
      AbsoluteDate date, ReferenceAltitudes reference, int direction) {
    AbsoluteDate found = riseSetDateOf(target.provider, target.meanRadius, eventType, point, date,
        reference, direction);
    if (found == null) {
      // the target never reaches the reference altitude: polar day, polar night or circumpolar
      return S.Missing;
    }
    return AstroConvert.toDateObject(found);
  }

  /**
   * The next rise, set or culmination of a body, using the default reference altitude. Shared with
   * {@link S#FindAstroEvent}.
   *
   * @return the event date, or <code>null</code> if the body never reaches the altitude
   */
  static AbsoluteDate riseSetDate(String bodyName, String eventType, GeodeticPoint point,
      AbsoluteDate date, int direction) {
    return riseSetDate(bodyName, eventType, point, date, ReferenceAltitudes.of(null, null),
        direction);
  }

  /** The next rise, set or culmination of a body. */
  static AbsoluteDate riseSetDate(String bodyName, String eventType, GeodeticPoint point,
      AbsoluteDate date, ReferenceAltitudes reference, int direction) {
    return riseSetDateOf(CelestialBodyFactory.getBody(bodyName), AstroBodies.meanRadius(bodyName),
        eventType, point, date, reference, direction);
  }

  /**
   * The next rise, set or culmination of anything Orekit can point at, using the default reference
   * altitude. This is the form a fixed star goes through - see
   * {@link org.matheclipse.astro.sky.StarProvider} - and it is where the solar system and the
   * catalogue meet.
   *
   * @param bodyRadius mean radius of the body in meters; zero for a point source such as a star,
   *        which has no limb for the reference altitude to be measured from
   */
  public static AbsoluteDate riseSetDateOf(PVCoordinatesProvider body, double bodyRadius,
      String eventType, GeodeticPoint point, AbsoluteDate date, int direction) {
    return riseSetDateOf(body, bodyRadius, eventType, point, date,
        ReferenceAltitudes.of(null, null), direction);
  }

  /** The next rise, set or culmination of anything Orekit can point at. */
  public static AbsoluteDate riseSetDateOf(PVCoordinatesProvider body, double bodyRadius,
      String eventType, GeodeticPoint point, AbsoluteDate date, ReferenceAltitudes reference,
      int direction) {
    AstroObserver observer = new AstroObserver(point, body, bodyRadius);

    if ("UpperCulmination".equalsIgnoreCase(eventType)
        || "LowerCulmination".equalsIgnoreCase(eventType)) {
      return DateRootFinder.findExtremum(observer::trueElevation, date, RISE_SET_STEP,
          RISE_SET_HORIZON, direction, "UpperCulmination".equalsIgnoreCase(eventType));
    }
    boolean rise = "Rise".equalsIgnoreCase(eventType);
    // the elevation excess crosses zero twice a day; requiring the slope to have the sign of
    // the event separates the rise from the set
    DateRootFinder.DateFunction excess = d -> observer.elevationExcess(d, reference);
    return findDirectedCrossing(excess, date, direction, rise);
  }

  /**
   * Find the next zero crossing of {@code excess} whose slope has the requested sign: ascending for
   * a rise, descending for a set.
   */
  private static AbsoluteDate findDirectedCrossing(DateRootFinder.DateFunction excess,
      AbsoluteDate start, int direction, boolean ascending) {
    AbsoluteDate from = start;
    for (int i = 0; i < 800; i++) {
      AbsoluteDate crossing =
          DateRootFinder.findCrossing(excess, from, RISE_SET_STEP, RISE_SET_HORIZON, direction);
      if (crossing == null) {
        return null;
      }
      // sample either side of the crossing to see which way the body is moving
      double before = excess.value(crossing.shiftedBy(-RISE_SET_STEP));
      double after = excess.value(crossing.shiftedBy(RISE_SET_STEP));
      if (ascending == (after > before)) {
        return crossing;
      }
      // wrong kind of crossing, step past it and keep looking
      from = crossing.shiftedBy(direction < 0 ? -RISE_SET_STEP : RISE_SET_STEP);
    }
    return null;
  }

  /** <code>Sunrise(location, date)</code>. */
  private static class Sunrise extends AbstractFunctionOptionEvaluator {

    /** The event this evaluator searches for. */
    protected String eventType() {
      return "Rise";
    }

    /** The symbol this evaluator is registered for. */
    protected IBuiltInSymbol symbol() {
      return S.Sunrise;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(symbol(), engine)) {
        return F.NIL;
      }
      Arguments arguments = Arguments.of(ast, 1, argSize);
      if (arguments.rejected != null) {
        return AstroConvert.reportUnreadableArgument(symbol(), arguments.rejected, ast, engine);
      }
      if (arguments.point == null) {
        return Errors.printMessage(symbol(), "argillegal", F.List(ast, ast), engine);
      }
      ReferenceAltitudes reference = ReferenceAltitudes.of(options[0], engine);
      if (reference == null) {
        return Errors.printMessage(symbol(), "astrorefalt", F.List(options[0], ast), engine);
      }
      int direction = options[1] != null && options[1].isReal() && options[1].evalf() < 0 ? -1 : 1;
      try {
        return riseSet(symbol(), CelestialBodyFactory.SUN, eventType(), arguments.point,
            arguments.date, reference, direction, engine);
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
          new IBuiltInSymbol[] {S.ReferenceAltitude, S.TimeDirection, S.TimeZone}, //
          new IExpr[] {S.Automatic, F.C1, S.Automatic});
    }

    @Override
    public int status() {
      // the TimeZone option is accepted but the result is always returned in UTC
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** <code>Sunset(location, date)</code>. */
  private static final class Sunset extends Sunrise {

    @Override
    protected String eventType() {
      return "Set";
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.Sunset;
    }
  }

  /** <code>AstroRiseSet(body, eventType, location, date)</code>. */
  private static final class AstroRiseSet extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(S.AstroRiseSet, engine)) {
        return F.NIL;
      }
      if (argSize < 1) {
        return F.NIL;
      }
      AstroBodies.Target target = AstroBodies.target(ast.arg1());
      if (target == null) {
        return Errors.printMessage(S.AstroRiseSet, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      IExpr eventSpec = argSize >= 2 ? ast.arg2() : F.List(F.stringx("Rise"), F.stringx("Set"));
      Arguments arguments = Arguments.of(ast, 3, argSize);
      if (arguments.rejected != null) {
        return AstroConvert.reportUnreadableArgument(S.AstroRiseSet, arguments.rejected, ast,
            engine);
      }
      if (arguments.point == null) {
        return Errors.printMessage(S.AstroRiseSet, "argillegal", F.List(ast, ast), engine);
      }
      ReferenceAltitudes reference = ReferenceAltitudes.of(options[0], engine);
      if (reference == null) {
        return Errors.printMessage(S.AstroRiseSet, "astrorefalt", F.List(options[0], ast), engine);
      }
      int direction = options[1] != null && options[1].isReal() && options[1].evalf() < 0 ? -1 : 1;

      try {
        if (eventSpec.isList()) {
          IAST events = (IAST) eventSpec;
          IExpr[] results = new IExpr[events.argSize()];
          for (int i = 1; i <= events.argSize(); i++) {
            IExpr single = riseSetChecked(events.get(i), target, arguments, reference, direction,
                ast, engine);
            if (single.isNIL()) {
              return F.NIL;
            }
            results[i - 1] = single;
          }
          return F.List(results);
        }
        return riseSetChecked(eventSpec, target, arguments, reference, direction, ast, engine);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstroRiseSet, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    private IExpr riseSetChecked(IExpr eventSpec, AstroBodies.Target target, Arguments arguments,
        ReferenceAltitudes reference, int direction, IAST ast, EvalEngine engine) {
      if (!eventSpec.isString()) {
        return Errors.printMessage(S.AstroRiseSet, "astroevent", F.List(eventSpec, ast), engine);
      }
      String eventType = eventSpec.toString();
      switch (eventType.toLowerCase(Locale.US)) {
        case "rise":
        case "set":
        case "upperculmination":
        case "lowerculmination":
          return riseSetOf(target, eventType, arguments.point, arguments.date, reference,
              direction);
        default:
          return Errors.printMessage(S.AstroRiseSet, "astroevent", F.List(eventSpec, ast), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.ReferenceAltitude, S.TimeDirection, S.TimeZone}, //
          new IExpr[] {S.Automatic, F.C1, S.Automatic});
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** <code>DaylightQ(location, date)</code>. */
  private static final class DaylightQ extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(S.DaylightQ, engine)) {
        return F.NIL;
      }
      Arguments arguments = Arguments.of(ast, 1, argSize);
      if (arguments.rejected != null || arguments.point == null) {
        return F.NIL;
      }
      ReferenceAltitudes reference = ReferenceAltitudes.of(options[0], engine);
      if (reference == null) {
        return Errors.printMessage(S.DaylightQ, "astrorefalt", F.List(options[0], ast), engine);
      }
      try {
        AstroObserver observer = new AstroObserver(arguments.point,
            CelestialBodyFactory.getSun(), AstroBodies.meanRadius(CelestialBodyFactory.SUN));
        return observer.elevationExcess(arguments.date, reference) > 0.0 ? S.True : S.False;
      } catch (OrekitException oex) {
        return Errors.printMessage(S.DaylightQ, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.ReferenceAltitude, S.Automatic);
    }
  }

  /**
   * The illuminated fraction of the Moon, which is <code>(1 + cos i) / 2</code> for the phase angle
   * <code>i</code> subtended at the Moon by the Sun and the Earth.
   */
  static double illuminationFraction(AbsoluteDate date) {
    Frame frame = FramesFactory.getGCRF();
    Vector3D moon = CelestialBodyFactory.getMoon().getPosition(date, frame);
    Vector3D sun = CelestialBodyFactory.getSun().getPosition(date, frame);
    // both vectors start at the Moon: one towards the Sun, one towards the observer on Earth
    double phaseAngle = Vector3D.angle(sun.subtract(moon), moon.negate());
    return (1.0 + FastMath.cos(phaseAngle)) / 2.0;
  }

  /**
   * The elongation of the Moon from the Sun in geocentric ecliptic longitude, normalized to
   * <code>[0, 2 pi)</code>. Zero at new moon, pi at full moon; this is what defines the lunation.
   */
  static double elongation(AbsoluteDate date) {
    Frame ecliptic = FramesFactory.getEcliptic(org.orekit.utils.IERSConventions.IERS_2010);
    Vector3D moon = CelestialBodyFactory.getMoon().getPosition(date, ecliptic);
    Vector3D sun = CelestialBodyFactory.getSun().getPosition(date, ecliptic);
    double difference = moon.getAlpha() - sun.getAlpha();
    return normalizeAngle(difference);
  }

  /** Normalize an angle to <code>[0, 2 pi)</code>. */
  private static double normalizeAngle(double angle) {
    double normalized = angle % (2.0 * FastMath.PI);
    return normalized < 0.0 ? normalized + 2.0 * FastMath.PI : normalized;
  }

  /**
   * Find the date at which the elongation reaches {@code targetAngle}.
   *
   * @param direction <code>1</code> forwards in time, <code>-1</code> backwards
   */
  static AbsoluteDate findPhase(AbsoluteDate start, double targetAngle, int direction) {
    // shifting the elongation by the target and re-centering on zero turns "reaches the target"
    // into a plain sign change, and keeps the branch cut away from the root
    DateRootFinder.DateFunction offset =
        date -> normalizeAngle(elongation(date) - targetAngle + FastMath.PI) - FastMath.PI;
    // the wrapped offset jumps by a full turn at the opposite phase; anything bigger than the
    // Moon's ~12 degrees of daily elongation change is that jump and not a root
    return DateRootFinder.findCrossing(offset, start, PHASE_STEP, 2.0 * SYNODIC_MONTH, direction,
        FastMath.PI);
  }

  /** <code>MoonPhase(date, property)</code>. */
  private static final class MoonPhase extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(S.MoonPhase, engine)) {
        return F.NIL;
      }
      AbsoluteDate date = null;
      String property = "Fraction";
      for (int i = 1; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
        } else if (arg.isString()) {
          property = arg.toString();
        } else {
          return AstroConvert.reportUnreadableArgument(S.MoonPhase, arg, ast, engine);
        }
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        return property(property, date, options[0], ast, engine);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.MoonPhase, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    private IExpr property(String property, AbsoluteDate date, IExpr method, IAST ast,
        EvalEngine engine) {
      boolean byLongitude = "EclipticLongitude".equals(AstroConvert.optionString(method, ""));
      double angle = elongation(date);
      switch (property.toLowerCase(Locale.US)) {
        case "fraction":
        case "illuminationfraction":
          return F.num(byLongitude ? (1.0 - FastMath.cos(angle)) / 2.0
              : illuminationFraction(date));
        case "signedfraction":
        case "signedilluminationfraction": {
          double fraction = byLongitude ? (1.0 - FastMath.cos(angle)) / 2.0
              : illuminationFraction(date);
          // negative while the Moon is waning, i.e. in the second half of the lunation
          return F.num(angle > FastMath.PI ? -fraction : fraction);
        }
        case "phaseangle":
          return F.num(angle);
        case "phaseanglefraction":
          return F.num(angle / (2.0 * FastMath.PI));
        case "name":
          return F.stringx(phaseName(angle));
        default:
          return Errors.printMessage(S.MoonPhase, "astroprop", F.List(F.stringx(property), ast),
              engine);
      }
    }

    /**
     * The traditional eight phase names. The quarters are the instants where the elongation is an
     * exact multiple of a quarter turn, so each name covers the interval around one of them.
     */
    private static String phaseName(double angle) {
      double eighth = FastMath.PI / 4.0;
      double sixteenth = eighth / 2.0;
      int index = (int) FastMath.floor((angle + sixteenth) / eighth) % 8;
      switch (index) {
        case 0:
          return "New Moon";
        case 1:
          return "Waxing Crescent";
        case 2:
          return "First Quarter";
        case 3:
          return "Waxing Gibbous";
        case 4:
          return "Full Moon";
        case 5:
          return "Waning Gibbous";
        case 6:
          return "Last Quarter";
        default:
          return "Waning Crescent";
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Method, F.stringx("Illumination"));
    }

    @Override
    public int status() {
      // the "Icon" property has no equivalent without a graphics back end
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** Shared implementation of {@link S#NewMoon}, {@link S#FullMoon} and {@link S#MoonPhaseDate}. */
  private abstract static class AbstractPhaseDate extends AbstractFunctionOptionEvaluator {

    /** The elongation the Moon has to reach, in radians. */
    protected abstract double targetAngle(IAST ast, int argSize, EvalEngine engine);

    protected abstract IBuiltInSymbol symbol();

    /** Index of the first argument which is a date rather than a phase specification. */
    protected int dateArgumentFrom() {
      return 1;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(symbol(), engine)) {
        return F.NIL;
      }
      Arguments arguments = Arguments.of(ast, dateArgumentFrom(), argSize);
      if (arguments.rejected != null) {
        return AstroConvert.reportUnreadableArgument(symbol(), arguments.rejected, ast, engine);
      }
      double target = targetAngle(ast, argSize, engine);
      if (Double.isNaN(target)) {
        return F.NIL;
      }
      int direction = options[0] != null && options[0].isReal() && options[0].evalf() < 0 ? -1 : 1;
      try {
        AbsoluteDate found = findPhase(arguments.date, target, direction);
        return found == null ? S.Missing : AstroConvert.toDateObject(found);
      } catch (OrekitException oex) {
        return Errors.printMessage(symbol(), "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.TimeDirection, S.TimeZone}, //
          new IExpr[] {F.C1, S.Automatic});
    }
  }

  /** <code>NewMoon(date)</code>. */
  private static final class NewMoon extends AbstractPhaseDate {

    @Override
    protected double targetAngle(IAST ast, int argSize, EvalEngine engine) {
      return 0.0;
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.NewMoon;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** <code>FullMoon(date)</code>. */
  private static final class FullMoon extends AbstractPhaseDate {

    @Override
    protected double targetAngle(IAST ast, int argSize, EvalEngine engine) {
      return FastMath.PI;
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.FullMoon;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   * <code>MoonPhaseDate(phase, date)</code> where <code>phase</code> is a fraction of the lunation
   * between <code>0</code> and <code>1</code> or one of the quarter names.
   */
  private static final class MoonPhaseDate extends AbstractPhaseDate {

    @Override
    protected int dateArgumentFrom() {
      return 2;
    }

    @Override
    protected double targetAngle(IAST ast, int argSize, EvalEngine engine) {
      if (argSize < 1) {
        return 0.0;
      }
      IExpr phase = ast.arg1();
      if (phase.isString()) {
        switch (phase.toString().toLowerCase(Locale.US)) {
          case "newmoon":
            return 0.0;
          case "firstquarter":
            return FastMath.PI / 2.0;
          case "fullmoon":
            return FastMath.PI;
          case "lastquarter":
            return 3.0 * FastMath.PI / 2.0;
          default:
            Errors.printMessage(S.MoonPhaseDate, "astrophase", F.List(phase, ast), engine);
            return Double.NaN;
        }
      }
      if (phase.isReal()) {
        return normalizeAngle(phase.evalf() * 2.0 * FastMath.PI);
      }
      Errors.printMessage(S.MoonPhaseDate, "astrophase", F.List(phase, ast), engine);
      return Double.NaN;
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.MoonPhaseDate;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** The new moon which opens lunation {@code number} in Brown's numbering. */
  static AbsoluteDate newMoonOfLunation(int number) {
    AbsoluteDate epoch =
        new AbsoluteDate(BROWN_LUNATION_1, org.orekit.time.TimeScalesFactory.getUTC());
    // the mean synodic month puts us within a couple of days of the true new moon, close enough
    // for the search below to lock onto the right one
    AbsoluteDate estimate = epoch.shiftedBy((number - 1) * SYNODIC_MONTH - 0.5 * SYNODIC_MONTH);
    return findPhase(estimate, 0.0, 1);
  }

  /** <code>LunationNumber(date)</code>. */
  private static final class LunationNumber extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      if (!AstroDataContext.checkAvailable(S.LunationNumber, engine)) {
        return F.NIL;
      }
      Arguments arguments = Arguments.of(ast, 1, argSize);
      if (arguments.rejected != null) {
        return AstroConvert.reportUnreadableArgument(S.LunationNumber, arguments.rejected, ast,
            engine);
      }
      try {
        AbsoluteDate epoch =
            new AbsoluteDate(BROWN_LUNATION_1, org.orekit.time.TimeScalesFactory.getUTC());
        int estimate =
            (int) FastMath.floor(arguments.date.durationFrom(epoch) / SYNODIC_MONTH) + 1;
        // the mean month drifts against the true one, so walk to the lunation which really
        // contains the date instead of trusting the estimate
        for (int number = estimate - 2; number <= estimate + 2; number++) {
          AbsoluteDate start = newMoonOfLunation(number);
          AbsoluteDate next = newMoonOfLunation(number + 1);
          if (start != null && next != null && arguments.date.durationFrom(start) >= 0.0
              && arguments.date.durationFrom(next) < 0.0) {
            return F.ZZ(number);
          }
        }
        return S.Missing;
      } catch (OrekitException oex) {
        return Errors.printMessage(S.LunationNumber, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** <code>FromLunationNumber(number)</code>. */
  private static final class FromLunationNumber extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      if (!AstroDataContext.checkAvailable(S.FromLunationNumber, engine)) {
        return F.NIL;
      }
      if (argSize < 1 || !ast.arg1().isInteger()) {
        return F.NIL;
      }
      try {
        AbsoluteDate found = newMoonOfLunation(ast.arg1().toIntDefault());
        return found == null ? S.Missing : AstroConvert.toDateObject(found);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.FromLunationNumber, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroEventFunctions() {}
}
