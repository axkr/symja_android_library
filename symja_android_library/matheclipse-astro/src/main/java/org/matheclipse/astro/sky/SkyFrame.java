package org.matheclipse.astro.sky;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;

/**
 * The reference frame a sky chart is drawn in.
 *
 * <p>
 * The bundled catalogue stores everything as J2000 equatorial right ascension and declination.
 * Drawing that in some other frame - the local horizon, the galactic plane, the ecliptic - is a
 * single rotation, so this resolves the <code>AstroReferenceFrame</code> option once and then hands
 * out {@link #toFrame} for every direction the chart touches. Stars, constellation lines,
 * boundaries, the Milky Way and the coordinate grid all go through that one method, which is what
 * keeps a chart self consistent.
 *
 * <p>
 * The rotation is computed once per chart rather than per point. A whole sky chart rotates several
 * thousand unit vectors, and a frame lookup for each of them would dominate the cost of drawing.
 */
public final class SkyFrame {

  /**
   * The right ascension of the north galactic pole, in degrees (J2000).
   *
   * <p>
   * Orekit has no galactic frame, so the three defining angles are given here. They are the IAU 1958
   * definition carried into J2000.
   */
  private static final double GALACTIC_POLE_RIGHT_ASCENSION = 192.85948;

  /** The declination of the north galactic pole, in degrees (J2000). */
  private static final double GALACTIC_POLE_DECLINATION = 27.12825;

  /** The galactic longitude of the north celestial pole, in degrees. */
  private static final double GALACTIC_LONGITUDE_OF_POLE = 122.93192;

  /** The name the frame was selected by. */
  private final String name;

  /** Rotation from the equatorial frame the catalogue uses into this one. */
  private final Rotation rotation;

  /** The instant the frame is evaluated at; frames which do not move still carry one. */
  private final AbsoluteDate date;

  /** Where the observer stands, for the horizon frame only; otherwise <code>null</code>. */
  private final GeodeticPoint location;

  /** Whether this is the local horizon, whose coordinates are azimuth and altitude. */
  private final boolean horizon;

  private SkyFrame(String name, Rotation rotation, AbsoluteDate date, GeodeticPoint location,
      boolean horizon) {
    this.name = name;
    this.rotation = rotation;
    this.date = date;
    this.location = location;
    this.horizon = horizon;
  }

  /**
   * Build a frame.
   *
   * @param name the frame name, case insensitive
   * @param date the instant to evaluate it at
   * @param location the observer, required by {@code "Horizon"} and ignored by everything else
   * @return the frame, or <code>null</code> if the name is not one this supports or a horizon frame
   *         was asked for without a location
   */
  public static SkyFrame of(String name, AbsoluteDate date, GeodeticPoint location) {
    String key = name.toLowerCase(Locale.US);
    if ("horizon".equals(key)) {
      if (location == null) {
        return null;
      }
      Frame topocentric = AstroConvert.toTopocentricFrame(location);
      Rotation toTopocentric = FramesFactory.getGCRF().getStaticTransformTo(topocentric, date)
          .getRotation();
      return new SkyFrame("Horizon", toTopocentric, date, location, true);
    }
    if ("galactic".equals(key)) {
      return new SkyFrame("Galactic", galacticRotation(), date, null, false);
    }
    Frame target = org.matheclipse.astro.builtin.AstroPositionFunctions.celestialFrame(name);
    if (target == null) {
      return null;
    }
    Rotation toTarget =
        FramesFactory.getGCRF().getStaticTransformTo(target, date).getRotation();
    return new SkyFrame(name, toTarget, date, null, false);
  }

  /** The equatorial frame the catalogue is already in, which needs no rotation at all. */
  public static SkyFrame equatorial(AbsoluteDate date) {
    return new SkyFrame("ICRS", Rotation.IDENTITY, date, null, false);
  }

  /**
   * Map a catalogue direction into this frame.
   *
   * @param rightAscension J2000 right ascension in degrees
   * @param declination J2000 declination in degrees
   * @return <code>{longitudeLike, latitudeLike}</code> in degrees. For the horizon frame these are
   *         azimuth measured clockwise from north and altitude above the horizon; for the galactic
   *         frame galactic longitude and latitude; otherwise the frame's own right ascension and
   *         declination.
   */
  public double[] toFrame(double rightAscension, double declination) {
    if (rotation.equals(Rotation.IDENTITY)) {
      return new double[] {rightAscension, declination};
    }
    Vector3D rotated = rotation.applyTo(direction(rightAscension, declination));
    if (horizon) {
      // the topocentric frame is X east, Y north, Z zenith, so azimuth runs clockwise from north
      double azimuth = FastMath.toDegrees(FastMath.atan2(rotated.getX(), rotated.getY()));
      return new double[] {azimuth < 0.0 ? azimuth + 360.0 : azimuth,
          FastMath.toDegrees(FastMath.asin(clamp(rotated.getZ())))};
    }
    double longitude = FastMath.toDegrees(rotated.getAlpha());
    return new double[] {longitude < 0.0 ? longitude + 360.0 : longitude,
        FastMath.toDegrees(rotated.getDelta())};
  }

  /**
   * Map a direction in this frame back to catalogue equatorial coordinates.
   *
   * <p>
   * The inverse of {@link #toFrame}, used to generate a curve that is naturally defined in some
   * other frame - the ecliptic, say - and then draw it in whatever frame the chart is using.
   *
   * @return <code>{rightAscension, declination}</code> in degrees
   */
  public double[] fromFrame(double longitude, double latitude) {
    if (rotation.equals(Rotation.IDENTITY)) {
      return new double[] {longitude, latitude};
    }
    Vector3D inFrame = horizon
        // azimuth is clockwise from north over the east/north/zenith axes
        ? direction(90.0 - longitude, latitude)
        : direction(longitude, latitude);
    Vector3D equatorial = rotation.applyInverseTo(inFrame);
    double rightAscension = FastMath.toDegrees(equatorial.getAlpha());
    return new double[] {rightAscension < 0.0 ? rightAscension + 360.0 : rightAscension,
        FastMath.toDegrees(equatorial.getDelta())};
  }

  /**
   * The rotation from J2000 equatorial coordinates to galactic ones.
   *
   * <p>
   * Pinned by two correspondences rather than composed from three angles, which removes any question
   * of rotation order or sign:
   *
   * <ul>
   * <li>the north galactic pole is galactic <code>(l, b) = (anything, +90)</code>;
   * <li>the north celestial pole is galactic
   * <code>(l, b) = (122.93192, 27.12825)</code> - its galactic latitude has to equal the galactic
   * pole's declination, because the two poles are exactly that far apart.
   * </ul>
   */
  private static Rotation galacticRotation() {
    Vector3D galacticPoleInEquatorial =
        direction(GALACTIC_POLE_RIGHT_ASCENSION, GALACTIC_POLE_DECLINATION);
    Vector3D celestialPoleInGalactic =
        direction(GALACTIC_LONGITUDE_OF_POLE, GALACTIC_POLE_DECLINATION);
    return new Rotation(galacticPoleInEquatorial, Vector3D.PLUS_K, //
        Vector3D.PLUS_K, celestialPoleInGalactic);
  }

  /** A unit vector from a longitude and latitude in degrees. */
  private static Vector3D direction(double longitudeDegrees, double latitudeDegrees) {
    double longitude = FastMath.toRadians(longitudeDegrees);
    double latitude = FastMath.toRadians(latitudeDegrees);
    double cosLatitude = FastMath.cos(latitude);
    return new Vector3D(cosLatitude * FastMath.cos(longitude),
        cosLatitude * FastMath.sin(longitude), FastMath.sin(latitude));
  }

  /** @return whether this is the local horizon frame, whose latitude is an altitude */
  public boolean isHorizon() {
    return horizon;
  }

  /** @return the name the frame was selected by */
  public String name() {
    return name;
  }

  /** @return the instant the frame is evaluated at */
  public AbsoluteDate date() {
    return date;
  }

  /** @return the observer, or <code>null</code> for a frame which does not need one */
  public GeodeticPoint location() {
    return location;
  }

  /** Keep a dot product inside the domain of {@code asin} against rounding just outside it. */
  private static double clamp(double value) {
    return FastMath.max(-1.0, FastMath.min(1.0, value));
  }
}
