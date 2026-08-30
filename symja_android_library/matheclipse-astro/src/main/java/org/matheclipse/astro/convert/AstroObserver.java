package org.matheclipse.astro.convert;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.Frame;
import org.orekit.frames.TopocentricFrame;
import org.orekit.models.earth.EarthStandardAtmosphereRefraction;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinatesProvider;
import org.orekit.utils.TrackingCoordinates;

/**
 * A celestial body watched from one place on Earth.
 *
 * <p>
 * Bundles the topocentric frame, the body and the refraction model so that the rise, set,
 * culmination and daylight searches all share one definition of "how high is it right now".
 */
public class AstroObserver {

  private final TopocentricFrame topocentric;

  private final Frame frame;

  private final PVCoordinatesProvider body;

  /** Mean radius of the body in meters, zero for a point. */
  private final double bodyRadius;

  private final EarthStandardAtmosphereRefraction refraction =
      new EarthStandardAtmosphereRefraction();

  public AstroObserver(GeodeticPoint point, PVCoordinatesProvider body, double bodyRadius) {
    this.frame = AstroConvert.earthFrame();
    this.topocentric = AstroConvert.toTopocentricFrame(point);
    this.body = body;
    this.bodyRadius = bodyRadius;
  }

  /** Azimuth, elevation and range of the centre of the body, without refraction. */
  public TrackingCoordinates tracking(AbsoluteDate date) {
    Vector3D position = body.getPosition(date, frame);
    return topocentric.getTrackingCoordinates(position, frame, date);
  }

  /** Geometric elevation of the centre of the body, in radians. */
  public double trueElevation(AbsoluteDate date) {
    return tracking(date).getElevation();
  }

  /**
   * Elevation of the centre of the body as an observer sees it, in radians. Orekit's standard
   * atmosphere model returns zero correction outside <code>-2</code> to <code>89.89</code> degrees,
   * which is the right behaviour here: the twilight angles are defined geometrically anyway.
   */
  public double apparentElevation(AbsoluteDate date) {
    double trueElevation = trueElevation(date);
    return trueElevation + refraction.getRefraction(trueElevation);
  }

  /** Distance from the observer to the centre of the body, in meters. */
  public double range(AbsoluteDate date) {
    return tracking(date).getRange();
  }

  /** Angular radius of the disk of the body as seen by the observer, in radians. */
  public double apparentRadius(AbsoluteDate date) {
    if (bodyRadius <= 0.0) {
      return 0.0;
    }
    double range = range(date);
    return range <= bodyRadius ? 0.0 : FastMath.asin(bodyRadius / range);
  }

  /**
   * How far the body is above the altitude which {@code reference} asks for, in radians. This is
   * the function whose zero crossings are the rise and set events: positive means up, negative
   * means down.
   */
  public double elevationExcess(AbsoluteDate date, ReferenceAltitudes reference) {
    double elevation =
        reference.isRefracted() ? apparentElevation(date) : trueElevation(date);
    return elevation - reference.centerElevation(apparentRadius(date));
  }
}
