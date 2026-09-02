package org.matheclipse.astro.convert;

import org.hipparchus.geometry.euclidean.threed.Line;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

/**
 * The shadow geometry of a solar or lunar eclipse at one instant.
 *
 * <p>
 * Everything here is straight vector geometry on the Sun, Moon and Earth positions rather than the
 * Besselian element machinery an eclipse canon uses. That is enough for whether an eclipse happens,
 * when it is greatest, what kind it is and how deep it goes; it is not enough for the contact times
 * or the shadow track, which is why those are not offered.
 */
public class EclipseGeometry {

  /** Mean radius of the Sun in meters. */
  public static final double SUN_RADIUS = Constants.IAU_2015_NOMINAL_SOLAR_RADIUS;

  /** Mean radius of the Moon in meters. */
  public static final double MOON_RADIUS = 1737400.0;

  /** Mean radius of the Earth in meters. */
  public static final double EARTH_RADIUS = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

  /**
   * The traditional enlargement of the Earth's shadow, one part in fifty, which stands in for the
   * refraction and absorption of the atmosphere. Without it computed lunar eclipse magnitudes come
   * out systematically small against the published ones.
   */
  private static final double SHADOW_ENLARGEMENT = 1.0 / 50.0;

  /** The instant this geometry describes. */
  public final AbsoluteDate date;

  /** Geocentric position of the Sun, in meters. */
  public final Vector3D sun;

  /** Geocentric position of the Moon, in meters. */
  public final Vector3D moon;

  private EclipseGeometry(AbsoluteDate date, Vector3D sun, Vector3D moon) {
    this.date = date;
    this.sun = sun;
    this.moon = moon;
  }

  /** The Sun and Moon as seen from the Earth's centre at {@code date}, in GCRF. */
  public static EclipseGeometry at(AbsoluteDate date) {
    Frame frame = FramesFactory.getGCRF();
    return new EclipseGeometry(date, CelestialBodyFactory.getSun().getPosition(date, frame),
        CelestialBodyFactory.getMoon().getPosition(date, frame));
  }

  /** The direction the Moon's shadow travels: away from the Sun, through the Moon. */
  public Vector3D shadowAxis() {
    return moon.subtract(sun).normalize();
  }

  /** The point on the Moon's shadow axis which passes closest to the Earth's centre. */
  public Vector3D shadowAxisClosestPoint() {
    Vector3D axis = shadowAxis();
    return moon.subtract(axis.scalarMultiply(moon.dotProduct(axis)));
  }

  /**
   * Gamma: the least distance between the axis of the Moon's shadow and the Earth's centre,
   * measured in Earth radii and signed positive when the axis passes north of the centre.
   *
   * <p>
   * This is the number which decides whether there is a solar eclipse at all: the shadow misses
   * the Earth entirely once it grows past about 1.55.
   */
  public double gamma() {
    Vector3D closest = shadowAxisClosestPoint();
    Vector3D axis = shadowAxis();
    // north within the fundamental plane, i.e. the celestial pole with the axial part removed
    Vector3D north = Vector3D.PLUS_K.subtract(axis.scalarMultiply(Vector3D.PLUS_K.dotProduct(axis)));
    double sign = north.getNorm() == 0.0 || closest.dotProduct(north) >= 0.0 ? 1.0 : -1.0;
    return sign * closest.getNorm() / EARTH_RADIUS;
  }

  /** Distance from the Moon to the fundamental plane through the Earth's centre, in meters. */
  private double moonToFundamentalPlane() {
    return -moon.dotProduct(shadowAxis());
  }

  /** Radius of the Moon's penumbra where it crosses the fundamental plane, in meters. */
  public double penumbraRadius() {
    double sunToMoon = moon.subtract(sun).getNorm();
    double tangent = (SUN_RADIUS + MOON_RADIUS) / sunToMoon;
    return MOON_RADIUS + moonToFundamentalPlane() * tangent;
  }

  /**
   * Radius of the Moon's umbra where it crosses the fundamental plane, in meters. Negative once
   * the cone has closed to its vertex, which is what makes an eclipse annular rather than total.
   */
  public double umbraRadius() {
    double sunToMoon = moon.subtract(sun).getNorm();
    double tangent = (SUN_RADIUS - MOON_RADIUS) / sunToMoon;
    return MOON_RADIUS - moonToFundamentalPlane() * tangent;
  }

  /** Whether the penumbra reaches the Earth at all, i.e. whether a solar eclipse is in progress. */
  public boolean isSolarEclipse() {
    return FastMath.abs(gamma()) * EARTH_RADIUS < penumbraRadius() + EARTH_RADIUS;
  }

  /** Whether the shadow axis itself strikes the Earth, which is what makes an eclipse central. */
  public boolean isCentral() {
    return FastMath.abs(gamma()) < 1.0;
  }

  /**
   * The point on the Earth's surface where the eclipse is greatest: where the shadow axis meets the
   * ellipsoid, or failing that the surface point nearest the axis.
   *
   * @return the point, or <code>null</code> if the geometry degenerates
   */
  public GeodeticPoint greatestEclipsePoint() {
    Frame frame = FramesFactory.getGCRF();
    Vector3D axis = shadowAxis();
    if (isCentral()) {
      Line line = new Line(moon, moon.add(axis.scalarMultiply(1.0e9)), 1.0);
      // of the two intersections take the one on the Moon's side, which is the one in sunlight
      GeodeticPoint intersection = AstroConvert.earthEllipsoid().getIntersectionPoint(line, moon,
          frame, date);
      if (intersection != null) {
        return intersection;
      }
    }
    Vector3D closest = shadowAxisClosestPoint();
    if (closest.getNorm() == 0.0) {
      return null;
    }
    return AstroConvert.earthEllipsoid().transform(
        closest.normalize().scalarMultiply(EARTH_RADIUS), frame, date);
  }

  /**
   * The eclipse magnitude seen from {@code observer} on the Earth's surface: how much of the Sun's
   * diameter the Moon covers. Zero or less means the Sun is clear of the Moon.
   *
   * <p>
   * Once one disk lies wholly within the other there is no longer a covered fraction to measure,
   * and the convention switches to the ratio of the apparent diameters - which is why a total
   * eclipse is quoted as magnitude 1.04 rather than 1.02.
   */
  public double magnitudeAt(Vector3D observer) {
    Vector3D toSun = sun.subtract(observer);
    Vector3D toMoon = moon.subtract(observer);
    double sunRadius = FastMath.asin(SUN_RADIUS / toSun.getNorm());
    double moonRadius = FastMath.asin(MOON_RADIUS / toMoon.getNorm());
    double separation = Vector3D.angle(toSun, toMoon);
    if (separation <= FastMath.abs(moonRadius - sunRadius)) {
      return moonRadius / sunRadius;
    }
    return (sunRadius + moonRadius - separation) / (2.0 * sunRadius);
  }

  /**
   * The fraction of the Sun's <em>area</em> the Moon covers, seen from {@code observer}. This is
   * the overlap of two circles on the sky, which is why it is not simply the magnitude.
   */
  public double obscurationAt(Vector3D observer) {
    Vector3D toSun = sun.subtract(observer);
    Vector3D toMoon = moon.subtract(observer);
    double r = FastMath.asin(SUN_RADIUS / toSun.getNorm());
    double m = FastMath.asin(MOON_RADIUS / toMoon.getNorm());
    double d = Vector3D.angle(toSun, toMoon);
    if (d >= r + m) {
      return 0.0;
    }
    if (d <= FastMath.abs(r - m)) {
      // one disk sits wholly inside the other
      return m >= r ? 1.0 : (m * m) / (r * r);
    }
    double alpha = FastMath.acos((d * d + r * r - m * m) / (2.0 * d * r));
    double beta = FastMath.acos((d * d + m * m - r * r) / (2.0 * d * m));
    double overlap = r * r * (alpha - FastMath.sin(2.0 * alpha) / 2.0)
        + m * m * (beta - FastMath.sin(2.0 * beta) / 2.0);
    return overlap / (FastMath.PI * r * r);
  }

  /**
   * Whether the umbral cone still has width where it meets the Earth. True means the Moon covers
   * the Sun completely and the eclipse is total; false means the cone has closed short of the
   * surface and a ring of Sun stays visible, an annular eclipse.
   */
  public boolean isTotal() {
    double sunToMoon = moon.subtract(sun).getNorm();
    double tangent = (SUN_RADIUS - MOON_RADIUS) / sunToMoon;
    double gamma = gamma();
    // the surface is nearer to the Moon than the fundamental plane is, by the sagitta of the
    // chord the axis cuts through the Earth
    double sagitta = FastMath.abs(gamma) < 1.0
        ? EARTH_RADIUS * FastMath.sqrt(1.0 - gamma * gamma) : 0.0;
    return MOON_RADIUS - (moonToFundamentalPlane() - sagitta) * tangent > 0.0;
  }

  // ---------------------------------------------------------------- lunar eclipses

  /** The axis of the Earth's shadow: away from the Sun, through the Earth's centre. */
  public Vector3D earthShadowAxis() {
    return sun.normalize().negate();
  }

  /** The least distance from the Moon's centre to the axis of the Earth's shadow, in meters. */
  public double moonToEarthShadowAxis() {
    Vector3D axis = earthShadowAxis();
    return moon.subtract(axis.scalarMultiply(moon.dotProduct(axis))).getNorm();
  }

  /** Radius of the Earth's umbra at the Moon's distance, in meters. */
  public double earthUmbraRadius() {
    double axialDistance = moon.dotProduct(earthShadowAxis());
    double tangent = (SUN_RADIUS - EARTH_RADIUS) / sun.getNorm();
    return (1.0 + SHADOW_ENLARGEMENT) * (EARTH_RADIUS - axialDistance * tangent);
  }

  /** Radius of the Earth's penumbra at the Moon's distance, in meters. */
  public double earthPenumbraRadius() {
    double axialDistance = moon.dotProduct(earthShadowAxis());
    double tangent = (SUN_RADIUS + EARTH_RADIUS) / sun.getNorm();
    return (1.0 + SHADOW_ENLARGEMENT) * (EARTH_RADIUS + axialDistance * tangent);
  }

  /** How deep the Moon is inside the Earth's umbra, in Moon diameters. */
  public double umbralMagnitude() {
    return (earthUmbraRadius() + MOON_RADIUS - moonToEarthShadowAxis()) / (2.0 * MOON_RADIUS);
  }

  /** How deep the Moon is inside the Earth's penumbra, in Moon diameters. */
  public double penumbralMagnitude() {
    return (earthPenumbraRadius() + MOON_RADIUS - moonToEarthShadowAxis()) / (2.0 * MOON_RADIUS);
  }

  /**
   * Gamma for a lunar eclipse: the Moon's least distance from the axis of the Earth's shadow, in
   * Earth radii, signed the same way as for a solar eclipse.
   */
  public double lunarGamma() {
    Vector3D axis = earthShadowAxis();
    Vector3D offset = moon.subtract(axis.scalarMultiply(moon.dotProduct(axis)));
    Vector3D north = Vector3D.PLUS_K.subtract(axis.scalarMultiply(Vector3D.PLUS_K.dotProduct(axis)));
    double sign = north.getNorm() == 0.0 || offset.dotProduct(north) >= 0.0 ? 1.0 : -1.0;
    return sign * offset.getNorm() / EARTH_RADIUS;
  }
}
