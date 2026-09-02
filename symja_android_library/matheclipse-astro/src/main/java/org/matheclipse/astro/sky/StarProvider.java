package org.matheclipse.astro.sky;

import org.hipparchus.CalculusFieldElement;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.ExtendedPositionProvider;

/**
 * A fixed star presented as something Orekit can point at.
 *
 * <p>
 * This is what lets a star be used wherever a solar system body can. The rise, set, culmination,
 * elevation and azimuth code all take an {@link ExtendedPositionProvider} and know nothing about what
 * is on the other end of it, so implementing that interface is enough to give
 * <code>StarData("Sirius", "RiseTime", ...)</code> and <code>AstroRiseSet("Sirius", ...)</code> the
 * whole of the existing machinery for free.
 *
 * <p>
 * The catalogue holds a direction and no distance, so the star is placed at a nominal distance far
 * beyond the solar system. Only the direction is ever meaningful: azimuth, elevation and angular
 * separation come out right, while a range or a parallax read off this provider does not mean
 * anything, and {@code StarData} reports distance as unsupported rather than returning the nominal
 * value.
 */
public class StarProvider implements ExtendedPositionProvider {

  /**
   * One parsec is far enough that the Earth's orbit subtends an arcsecond, so no geometry in this
   * module can tell this direction from a truly infinite one.
   */
  private static final double NOMINAL_DISTANCE = 3.0856775814913673e16;

  /** Unit vector towards the star in the ICRF, which Orekit exposes as GCRF. */
  private final Vector3D direction;

  /**
   * @param rightAscension J2000 right ascension in degrees
   * @param declination J2000 declination in degrees
   */
  public StarProvider(double rightAscension, double declination) {
    double ra = FastMath.toRadians(rightAscension);
    double dec = FastMath.toRadians(declination);
    double cosDec = FastMath.cos(dec);
    this.direction = new Vector3D(cosDec * FastMath.cos(ra), cosDec * FastMath.sin(ra),
        FastMath.sin(dec));
  }

  /** The unit vector towards the star in the ICRF. */
  public Vector3D direction() {
    return direction;
  }

  @Override
  public Vector3D getPosition(AbsoluteDate date, Frame frame) {
    Vector3D icrf = direction.scalarMultiply(NOMINAL_DISTANCE);
    Frame gcrf = FramesFactory.getGCRF();
    if (frame == gcrf) {
      return icrf;
    }
    return gcrf.getStaticTransformTo(frame, date).transformPosition(icrf);
  }

  @Override
  public <T extends CalculusFieldElement<T>> FieldVector3D<T> getPosition(
      FieldAbsoluteDate<T> date, Frame frame) {
    FieldVector3D<T> icrf =
        new FieldVector3D<T>(date.getField(), direction.scalarMultiply(NOMINAL_DISTANCE));
    Frame gcrf = FramesFactory.getGCRF();
    if (frame == gcrf) {
      return icrf;
    }
    return gcrf.getStaticTransformTo(frame, date.toAbsoluteDate()).transformPosition(icrf);
  }
}
