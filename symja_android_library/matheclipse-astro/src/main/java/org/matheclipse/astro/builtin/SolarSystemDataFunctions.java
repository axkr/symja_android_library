package org.matheclipse.astro.builtin;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.MeeusBodyProvider;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.meeus.MeeusBody;
import org.matheclipse.astro.meeus.PlanetPhotometry;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.StaticTransform;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * Properties of the solar system bodies: <code>AstronomicalData</code> and <code>PlanetData</code>.
 *
 * <p>
 * <code>AstronomicalData</code> is superseded by <code>PlanetData</code> and <code>StarData</code>,
 * so both spellings are here: the older one because that is what the Demonstrations call -
 * <code>AstronomicalData(n)</code> names the <code>n</code>th major body counting outwards from the
 * Sun, so that <code>AstronomicalData(AstronomicalData(k), {"Position",
 * t})</code> composes - and <code>PlanetData</code> because that is what is written now.
 *
 * <p>
 * They differ in what they cover and in what they answer with. <code>AstronomicalData</code> takes
 * the Sun, the Moon and Pluto as well, and gives a position in meters; <code>PlanetData</code> is
 * the eight planets, and gives <code>HelioCoordinates</code> as a <code>Quantity</code> in
 * astronomical units. Both read the same ephemerides.
 *
 * <p>
 * The position is heliocentric and referred to the mean ecliptic and equinox <em>of the date asked
 * for</em> - measured 2026-09-18, where its <code>HelioCoordinates</code> for Mars agrees with this
 * to 1.5e-6 astronomical units, about 150 km. It comes from the bundled DE ephemerides rather than
 * from a two body approximation, which is what {@link AstroOrbitFunctions} would give.
 *
 * <p>
 * The observed properties - distances, phase angle, illuminated fraction, magnitude and angular
 * diameter - take their geometry from the same ephemerides and their magnitude and size formulas
 * from {@link PlanetPhotometry}, ported from Night Vision.
 */
public class SolarSystemDataFunctions {

  /**
   * The major bodies in the order <code>AstronomicalData(n)</code> counts them, outwards from the
   * Sun.
   *
   * <p>
   * Pluto is the ninth: this is the classic list the pre-Entity convention was written for, and
   * dropping it would renumber nothing else but would make <code>AstronomicalData(9)</code> a hole.
   */
  private static final String[] MAJOR_BODIES =
      {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  /** The entity type the planets belong to. */
  private static final String PLANET = "Planet";

  /** The planets, as <code>PlanetData()</code> counts them: the eight, without Pluto. */
  private static final String[] PLANETS =
      {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};

  /**
   * The properties which depend on where the body is seen from: computed from the geocentric
   * positions of the body and the Sun, with the magnitude and size formulas of Meeus.
   */
  private static final String[] OBSERVED_PROPERTIES = {"AngularDiameter", "ApparentMagnitude",
      "DistanceFromEarth", "DistanceFromSun", "IlluminationFraction", "PhaseAngle"};

  /** What <code>PlanetData("Properties")</code> lists. */
  private static final String[] PLANET_PROPERTIES = {"AngularDiameter", "ApparentMagnitude",
      "DistanceFromEarth", "DistanceFromSun", "HelioCoordinates", "IlluminationFraction",
      "PhaseAngle"};

  /** The visual magnitude of the Sun at one astronomical unit. */
  private static final double SUN_MAGNITUDE = -26.74;

  private static class Initializer {

    private static void init() {
      S.AstronomicalData.setEvaluator(new AstronomicalData());
      S.PlanetData.setEvaluator(new PlanetData());
      Entities.register(PLANET, S.PlanetData);
    }
  }

  /**
   * <code>AstronomicalData(n)</code>, <code>AstronomicalData(body)</code> or
   * <code>AstronomicalData(body, {"Position", date})</code>.
   */
  private static final class AstronomicalData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int index = arg1.toIntDefault();
      if (index != Config.INVALID_INT) {
        // AstronomicalData(n) is a name, which needs no ephemerides and no data files
        return ast.isAST1() && index >= 1 && index <= MAJOR_BODIES.length
            ? F.stringx(MAJOR_BODIES[index - 1])
            : F.NIL;
      }
      String bodyName = AstroBodies.nameOf(Entities.nameOf(arg1, PLANET));
      if (bodyName == null) {
        return Errors.printMessage(S.AstronomicalData, "astrobody", F.List(arg1, ast), engine);
      }
      if (ast.isAST1()) {
        // the body named as this function spells it, which is what the position form takes
        return F.stringx(bodyName);
      }
      IExpr property = ast.arg2();
      IExpr dateExpr = F.NIL;
      if (property.isList()) {
        IAST spec = (IAST) property;
        if (spec.argSize() < 1) {
          return Errors.printMessage(S.AstronomicalData, "astroprop", F.List(property, ast),
              engine);
        }
        property = spec.arg1();
        if (spec.argSize() >= 2) {
          dateExpr = spec.arg2();
        }
      }
      property = Entities.propertyOf(property, PLANET);
      boolean observed = isObservedProperty(property);
      if (!property.isString("Position") && !observed) {
        return Errors.printMessage(S.AstronomicalData, "astroprop", F.List(property, ast), engine);
      }
      if (!AstroDataContext.checkAvailable(S.AstronomicalData, engine)) {
        return F.NIL;
      }
      AbsoluteDate date = dateOf(dateExpr, S.AstronomicalData, ast, engine);
      if (date == null) {
        return F.NIL;
      }
      try {
        if (observed) {
          return observedProperty(bodyName, property.toString(), date);
        }
        Vector3D position = heliocentricEclipticPosition(bodyName, date);
        return F.List(F.num(position.getX()), F.num(position.getY()), F.num(position.getZ()));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstronomicalData, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
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
   * <code>PlanetData()</code>, or <code>PlanetData(planet, "HelioCoordinates")</code> - the
   * spelling which replaced <code>AstronomicalData</code>.
   *
   * <p>
   * Only the eight planets answer to it, and the coordinates come back in astronomical units
   * (measured 2026-09-18).
   */
  private static final class PlanetData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        // the planets themselves, as the entities standing for them
        IASTAppendable planets = F.ListAlloc(PLANETS.length);
        for (String planet : PLANETS) {
          planets.append(Entities.entity(PLANET, F.stringx(planet)));
        }
        return planets;
      }
      if (ast.isAST1() && ast.arg1().isString("Properties")) {
        IASTAppendable properties = F.ListAlloc(PLANET_PROPERTIES.length);
        for (String property : PLANET_PROPERTIES) {
          properties.append(F.stringx(property));
        }
        return properties;
      }
      String bodyName = planetNamed(ast.arg1());
      if (bodyName == null) {
        return Errors.printMessage(S.PlanetData, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      if (ast.isAST1()) {
        // a lone planet is the entity that stands for it, as ElementData answers for an element
        return Entities.entity(PLANET, F.stringx(bodyName));
      }
      IExpr property = ast.arg2();
      IExpr dateExpr = F.NIL;
      if (property.isList() && ((IAST) property).argSize() >= 1) {
        IAST spec = (IAST) property;
        property = spec.arg1();
        if (spec.argSize() >= 2) {
          dateExpr = spec.arg2();
        }
      }
      property = Entities.propertyOf(property, PLANET);
      boolean observed = isObservedProperty(property);
      if (!property.isString("HelioCoordinates") && !observed) {
        return Errors.printMessage(S.PlanetData, "astroprop", F.List(property, ast), engine);
      }
      if (!AstroDataContext.checkAvailable(S.PlanetData, engine)) {
        return F.NIL;
      }
      AbsoluteDate date = dateOf(dateExpr, S.PlanetData, ast, engine);
      if (date == null) {
        return F.NIL;
      }
      try {
        if (observed) {
          return observedProperty(bodyName, property.toString(), date);
        }
        Vector3D position = heliocentricEclipticPosition(bodyName, date);
        return F.List(astronomicalUnits(position.getX()), astronomicalUnits(position.getY()),
            astronomicalUnits(position.getZ()));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.PlanetData, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /** The name of the planet {@code expr} names, or {@code null} when it names something else. */
  private static String planetNamed(IExpr expr) {
    String name = AstroBodies.nameOf(Entities.nameOf(expr, PLANET));
    if (name != null) {
      for (String planet : PLANETS) {
        if (planet.equals(name)) {
          return planet;
        }
      }
    }
    return null;
  }

  /** A length in meters as a <code>Quantity</code> in astronomical units. */
  private static IExpr astronomicalUnits(double meters) {
    return F.Quantity(F.num(meters / Constants.IAU_2012_ASTRONOMICAL_UNIT),
        F.stringx("AstronomicalUnit"));
  }

  /**
   * The date a property specification asked for, defaulting to now; {@code null} when it is no date
   * at all, in which case the message is already reported.
   */
  private static AbsoluteDate dateOf(IExpr dateExpr, ISymbol symbol, IAST ast, EvalEngine engine) {
    if (!dateExpr.isPresent()) {
      return AstroConvert.nowUTC();
    }
    AbsoluteDate date = AstroConvert.toAbsoluteDate(dateExpr);
    if (date == null) {
      AstroConvert.reportUnreadableArgument(symbol, dateExpr, ast, engine);
    }
    return date;
  }

  /**
   * Where {@code bodyName} is at {@code date}, seen from the Sun, in metres along the axes of the
   * mean ecliptic and equinox of that same date.
   *
   * <p>
   * The ephemerides are read in the Earth centred GCRF and the Sun is subtracted there, since the
   * difference of two positions in one frame is the same vector whichever of them is the origin.
   * The ecliptic frame is then taken <em>at the date</em>: pinned at J2000 instead, the vector
   * stays turned by the precession since then - a quarter of a degree by 2026, which puts Mars
   * 0.014 astronomical units out. The price is that a body plotted over centuries carries the slow
   * turning of the frame with it, which is a property of the coordinates that were asked for rather
   * than of the body.
   */
  static Vector3D heliocentricEclipticPosition(String bodyName, AbsoluteDate date) {
    Frame gcrf = FramesFactory.getGCRF();
    Vector3D heliocentric = AstroBodies.provider(bodyName).getPosition(date, gcrf)
        .subtract(AstroBodies.sun().getPosition(date, gcrf));
    StaticTransform toEcliptic =
        gcrf.getStaticTransformTo(FramesFactory.getEcliptic(IERSConventions.IERS_2010), date);
    return toEcliptic.transformVector(heliocentric);
  }

  private static boolean isObservedProperty(IExpr property) {
    if (property.isString()) {
      String name = property.toString();
      for (String observed : OBSERVED_PROPERTIES) {
        if (observed.equals(name)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * A property of {@link #OBSERVED_PROPERTIES} for {@code bodyName} at {@code date}.
   *
   * <p>
   * The geometry comes from the ephemerides (JPL, or Meeus outside their range); only the
   * magnitude and diameter formulas are Meeus's, from Night Vision. Positions are geometric, so a
   * planet's distance differs from the light-time corrected one Night Vision shows by a fraction of
   * the distance it travels in a few minutes, far below the precision of these formulas.
   *
   * @return the value, or <code>Missing("NotApplicable")</code> for a property which does not
   *         apply to the body, such as the phase angle of the Sun or the magnitude of the Moon
   */
  static IExpr observedProperty(String bodyName, String property, AbsoluteDate date) {
    MeeusBody body = MeeusBody.of(bodyName);
    if (body == null) {
      // the Earth, seen from the Earth
      if (!"DistanceFromSun".equals(property)) {
        return notApplicable();
      }
    }
    Frame gcrf = FramesFactory.getGCRF();
    double au = Constants.IAU_2012_ASTRONOMICAL_UNIT;
    Vector3D sun = AstroBodies.sun().getPosition(date, gcrf);
    Vector3D position =
        body == null ? Vector3D.ZERO : AstroBodies.provider(bodyName).getPosition(date, gcrf);
    double earthDistance = position.getNorm() / au;
    double sunDistance = position.subtract(sun).getNorm() / au;
    double earthSunDistance = sun.getNorm() / au;
    boolean isSun = body == MeeusBody.SUN;
    switch (property) {
      case "DistanceFromEarth":
        return F.Quantity(F.num(earthDistance), F.stringx("AstronomicalUnit"));
      case "DistanceFromSun":
        return isSun ? notApplicable()
            : F.Quantity(F.num(sunDistance), F.stringx("AstronomicalUnit"));
      case "AngularDiameter":
        return F.Quantity(F.num(PlanetPhotometry.angularDiameter(body, earthDistance)),
            F.stringx("Arcseconds"));
      default:
        break;
    }
    if (isSun) {
      if ("ApparentMagnitude".equals(property)) {
        return F.num(SUN_MAGNITUDE + 5 * Math.log10(earthSunDistance));
      }
      return notApplicable();
    }
    // the angle Sun - body - Earth
    double phaseAngle = Vector3D.angle(sun.subtract(position), position.negate());
    switch (property) {
      case "PhaseAngle":
        return AstroConvert.degrees(phaseAngle);
      case "IlluminationFraction":
        return F.num((1.0 + Math.cos(phaseAngle)) / 2.0);
      case "ApparentMagnitude":
        if (!body.isPlanet()) {
          // Night Vision has no magnitude formula for the Moon
          return notApplicable();
        }
        double lambda = 0.0;
        double beta = 0.0;
        if (body == MeeusBody.SATURN) {
          // the tilt of the rings needs the geocentric ecliptic coordinates of date
          Vector3D ecliptic = gcrf
              .getStaticTransformTo(FramesFactory.getEcliptic(IERSConventions.IERS_2010), date)
              .transformVector(position);
          lambda = ecliptic.getAlpha();
          beta = ecliptic.getDelta();
        }
        return F.num(PlanetPhotometry.magnitude(body, earthDistance, sunDistance,
            earthSunDistance, MeeusBodyProvider.julianEphemerisDay(date), lambda, beta));
      default:
        return notApplicable();
    }
  }

  private static IExpr notApplicable() {
    return F.Missing(F.stringx("NotApplicable"));
  }

  public static void initialize() {
    Initializer.init();
  }

  private SolarSystemDataFunctions() {}
}
