package org.matheclipse.astro.convert;

import java.util.Locale;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.matheclipse.astro.sky.SkyCatalog;
import org.matheclipse.astro.sky.StarProvider;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Resolves the celestial body arguments of the astronomy functions.
 *
 * <p>
 * A body is written as a string - <code>"Sun"</code>, <code>"Moon"</code>, <code>"Mars"</code> -
 * or as the corresponding symbol. Everything the bundled DE 440 ephemerides contain is supported;
 * anything else is rejected so that the caller can emit a message instead of Orekit throwing.
 */
public class AstroBodies {

  private AstroBodies() {}

  /**
   * @return the Orekit name of the body, or <code>null</code> if {@code expr} does not name one
   */
  public static String nameOf(IExpr expr) {
    String name = expr.isString() ? expr.toString() : expr.isSymbol() ? expr.toString() : null;
    if (name == null) {
      return null;
    }
    switch (name.toLowerCase(Locale.US)) {
      case "sun":
        return CelestialBodyFactory.SUN;
      case "moon":
        return CelestialBodyFactory.MOON;
      case "earth":
        return CelestialBodyFactory.EARTH;
      case "mercury":
        return CelestialBodyFactory.MERCURY;
      case "venus":
        return CelestialBodyFactory.VENUS;
      case "mars":
        return CelestialBodyFactory.MARS;
      case "jupiter":
        return CelestialBodyFactory.JUPITER;
      case "saturn":
        return CelestialBodyFactory.SATURN;
      case "uranus":
        return CelestialBodyFactory.URANUS;
      case "neptune":
        return CelestialBodyFactory.NEPTUNE;
      case "pluto":
        return CelestialBodyFactory.PLUTO;
      case "earthmoonbarycenter":
        return CelestialBodyFactory.EARTH_MOON;
      case "solarsystembarycenter":
        return CelestialBodyFactory.SOLAR_SYSTEM_BARYCENTER;
      default:
        return null;
    }
  }

  /**
   * @return the body named by {@code expr}, or <code>null</code> if it does not name one
   */
  public static CelestialBody of(IExpr expr) {
    String name = nameOf(expr);
    return name == null ? null : CelestialBodyFactory.getBody(name);
  }

  /**
   * Anything the astronomy functions can point at: a solar system body or a catalogued star.
   *
   * <p>
   * Both reduce to a {@link PVCoordinatesProvider}, which is all the elevation, azimuth, rise, set
   * and separation code ever needs. Keeping the distinction in one place means a star can be passed
   * to {@code AstroPosition}, {@code AstroRiseSet} and {@code AstroAngularSeparation} without those
   * functions knowing that stars exist - while the few places where the difference does matter, such
   * as a distance or an orbit, can ask.
   */
  public static final class Target {

    /** The name to show in a result or a message. */
    public final String name;

    /** Where the target is, in whatever frame is asked for. */
    public final PVCoordinatesProvider provider;

    /** Mean radius in meters; zero for a star, which is a point source. */
    public final double meanRadius;

    /**
     * Whether this is a fixed star. A star's position is a direction only, so a distance read off
     * it is meaningless and it has no orbit about the Sun.
     */
    public final boolean isStar;

    Target(String name, PVCoordinatesProvider provider, double meanRadius, boolean isStar) {
      this.name = name;
      this.provider = provider;
      this.meanRadius = meanRadius;
      this.isStar = isStar;
    }
  }

  /**
   * Resolve a solar system body or a catalogued star.
   *
   * <p>
   * Solar system names win over catalogue names, so a star which happens to share a planet's name
   * cannot shadow the planet.
   *
   * @return the target, or <code>null</code> if {@code expr} names neither
   */
  public static Target target(IExpr expr) {
    String bodyName = nameOf(expr);
    if (bodyName != null) {
      return new Target(bodyName, CelestialBodyFactory.getBody(bodyName), meanRadius(bodyName),
          false);
    }
    if (!expr.isString()) {
      return null;
    }
    SkyCatalog.Star star = SkyCatalog.get().star(expr.toString());
    if (star == null) {
      return null;
    }
    String name = star.properName.isEmpty() ? expr.toString() : star.properName;
    return new Target(name, new StarProvider(star.rightAscension, star.declination), 0.0, true);
  }

  /**
   * The body which {@code orekitName} orbits: the Earth for the Moon, the Sun for everything else.
   * This is the centre the osculating orbital elements are referred to.
   */
  public static String primaryOf(String orekitName) {
    return CelestialBodyFactory.MOON.equals(orekitName) ? CelestialBodyFactory.EARTH
        : CelestialBodyFactory.SUN;
  }

  /**
   * Whether the body orbits closer to the Sun than the Earth does. Inner planets never come to
   * opposition or quadrature, which is what this distinguishes.
   */
  public static boolean isInferior(String orekitName) {
    return CelestialBodyFactory.MERCURY.equals(orekitName)
        || CelestialBodyFactory.VENUS.equals(orekitName);
  }

  /**
   * The mean radius of a body in meters, used to turn a centre-of-body direction into an apparent
   * disk. Barycenters and unknown bodies are points and get radius zero.
   */
  public static double meanRadius(String orekitName) {
    if (orekitName == null) {
      return 0.0;
    }
    switch (orekitName) {
      case CelestialBodyFactory.SUN:
        return Constants.IAU_2015_NOMINAL_SOLAR_RADIUS;
      case CelestialBodyFactory.MOON:
        // the Moon is very nearly spherical, so the IAU mean radius is used directly
        return 1737400.0;
      case CelestialBodyFactory.EARTH:
        return Constants.IAU_2015_NOMINAL_EARTH_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.MERCURY:
        return Constants.IAU_2015_NOMINAL_MERCURY_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.VENUS:
        return Constants.IAU_2015_NOMINAL_VENUS_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.MARS:
        return Constants.IAU_2015_NOMINAL_MARS_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.JUPITER:
        return Constants.IAU_2015_NOMINAL_JUPITER_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.SATURN:
        return Constants.IAU_2015_NOMINAL_SATURN_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.URANUS:
        return Constants.IAU_2015_NOMINAL_URANUS_EQUATORIAL_RADIUS;
      case CelestialBodyFactory.NEPTUNE:
        return Constants.IAU_2015_NOMINAL_NEPTUNE_EQUATORIAL_RADIUS;
      default:
        return 0.0;
    }
  }
}
