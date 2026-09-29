package org.matheclipse.astro.convert;

import org.hipparchus.CalculusFieldElement;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.matheclipse.astro.meeus.DeltaT;
import org.matheclipse.astro.meeus.MeeusBody;
import org.matheclipse.astro.meeus.MeeusEphemeris;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.ExtendedPositionProvider;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 * A solar system body positioned by the Meeus theories of {@link MeeusEphemeris}, presented as
 * something Orekit can point at.
 *
 * <p>
 * This is the fallback for dates the bundled JPL ephemerides do not cover. Like
 * {@link org.matheclipse.astro.sky.StarProvider} it only has to supply positions: everything
 * downstream - frames, Earth rotation, topocentric coordinates, refraction, the event searches -
 * stays Orekit's, and none of that needs the ephemeris files.
 *
 * <p>
 * The position is the geometric geocentric one, the same quantity Orekit's
 * {@link org.orekit.bodies.CelestialBody} returns, so results do not jump by the light-time when a
 * computation crosses the edge of the ephemerides.
 *
 * <h2>Time</h2>
 *
 * Outside the range of the leap second and Earth orientation files Orekit keeps TT - UTC frozen and
 * takes UT1 = UTC. A date given by a user is a civil date, so its UTC reading is taken as UT, which
 * is exactly what Orekit's Earth rotation then does too; the bodies are placed at TT = UT + delta T
 * with the delta T model of {@link DeltaT}. Without that, the Moon would be off by about ten minutes
 * of arc in the year 2500. Inside the range of the leap second table Orekit's own TT is used.
 */
public class MeeusBodyProvider implements ExtendedPositionProvider {

  /** The first day of the Gregorian calendar, 1582-10-15, as a Julian day. */
  public static final double MIN_JD = 2299160.5;

  /**
   * 3000-01-01. Night Vision was checked against JPL Horizons up to 2999, and the extrapolated delta
   * T grows by tens of minutes per millennium beyond that.
   */
  public static final double MAX_JD = 2816787.5;

  /** 1972-01-01, the start of the leap second table and of UTC as it is defined today. */
  private static final double LEAP_SECONDS_START_JD = 2441317.5;

  /** 2027-01-01; the bundled leap second table ends with 2026. */
  private static final double LEAP_SECONDS_END_JD = 2461406.5;

  /** Step of the central difference for the velocity, in seconds. */
  private static final double VELOCITY_STEP = 60.0;

  private final MeeusBody body;

  public MeeusBodyProvider(MeeusBody body) {
    this.body = body;
  }

  public MeeusBody body() {
    return body;
  }

  /** Whether the Meeus theories are used at all for this date. */
  public static boolean covers(AbsoluteDate date) {
    double jd = date.getJD(TimeScalesFactory.getUTC());
    return jd >= MIN_JD && jd < MAX_JD;
  }

  /**
   * Whether {@code date} lies in the span of the bundled leap second table, where Orekit's TT and
   * UT1 are measured values rather than frozen extrapolations.
   */
  public static boolean inLeapSecondEra(AbsoluteDate date) {
    double jd = date.getJD(TimeScalesFactory.getUTC());
    return jd >= LEAP_SECONDS_START_JD && jd < LEAP_SECONDS_END_JD;
  }

  /**
   * The Julian ephemeris day (TT) at which the bodies are computed for {@code date}.
   */
  public static double julianEphemerisDay(AbsoluteDate date) {
    TimeScale utc = TimeScalesFactory.getUTC();
    double jd = date.getJD(utc);
    if (jd >= LEAP_SECONDS_START_JD && jd < LEAP_SECONDS_END_JD) {
      return date.getJD(TimeScalesFactory.getTT());
    }
    return jd + DeltaT.seconds(jd) / 86400.0;
  }

  /** Geocentric geometric position in the GCRF, in meters. */
  private Vector3D gcrfPosition(AbsoluteDate date) {
    MeeusEphemeris.Position p =
        MeeusEphemeris.position(body, julianEphemerisDay(date), false, false, null);
    double[] xyz = p.cartesian();
    return new Vector3D(xyz[0], xyz[1], xyz[2]).scalarMultiply(Constants.IAU_2012_ASTRONOMICAL_UNIT);
  }

  @Override
  public Vector3D getPosition(AbsoluteDate date, Frame frame) {
    Vector3D gcrf = gcrfPosition(date);
    Frame gcrfFrame = FramesFactory.getGCRF();
    if (frame == gcrfFrame) {
      return gcrf;
    }
    return gcrfFrame.getStaticTransformTo(frame, date).transformPosition(gcrf);
  }

  /**
   * Position and velocity. The velocity is a central difference, which is plenty for the uses in
   * this module; the default implementation would differentiate the field version, which only
   * carries a first order expansion here.
   */
  @Override
  public TimeStampedPVCoordinates getPVCoordinates(AbsoluteDate date, Frame frame) {
    Vector3D position = getPosition(date, frame);
    Vector3D before = getPosition(date.shiftedBy(-VELOCITY_STEP), frame);
    Vector3D after = getPosition(date.shiftedBy(VELOCITY_STEP), frame);
    Vector3D velocity = after.subtract(before).scalarMultiply(0.5 / VELOCITY_STEP);
    return new TimeStampedPVCoordinates(date, position, velocity);
  }

  @Override
  public Vector3D getVelocity(AbsoluteDate date, Frame frame) {
    return getPVCoordinates(date, frame).getVelocity();
  }

  /**
   * The field version: the position at the underlying date, expanded to first order in the offset
   * of {@code date} from it, so that derivative fields still see the motion of the body.
   */
  @Override
  public <T extends CalculusFieldElement<T>> FieldVector3D<T> getPosition(FieldAbsoluteDate<T> date,
      Frame frame) {
    AbsoluteDate reference = date.toAbsoluteDate();
    TimeStampedPVCoordinates pv = getPVCoordinates(reference, frame);
    T dt = date.durationFrom(reference);
    return new FieldVector3D<T>(date.getField(), pv.getPosition())
        .add(new FieldVector3D<T>(dt, pv.getVelocity()));
  }
}
