package org.matheclipse.astro.convert;

import java.util.EnumSet;
import java.util.Set;
import org.hipparchus.CalculusFieldElement;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.matheclipse.astro.data.AstroFallback;
import org.matheclipse.astro.meeus.MeeusBody;
import org.orekit.bodies.CelestialBody;
import org.orekit.errors.LocalizedException;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.ExtendedPositionProvider;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 * A solar system body which uses Orekit's JPL ephemerides where they exist and the Meeus theories
 * of {@link MeeusBodyProvider} elsewhere.
 *
 * <p>
 * The JPL ephemerides cover a fixed span - 1990 to 2149 with the bundled file - and Orekit throws
 * outside it. That failure is expensive: Orekit first tries to load more data, and the event
 * searches evaluate a body tens of thousands of times. So the first failure on each side of the
 * span is remembered, and later dates beyond it go to the Meeus theories directly.
 *
 * <p>
 * Every use of the fallback is reported to {@link AstroFallback}, which prints the
 * <code>astrofallback</code> message once per evaluation.
 */
public class FallbackBodyProvider implements ExtendedPositionProvider {

  /**
   * The Orekit messages which mean "the ephemerides do not cover this date" - and so prove where
   * the span ends, which is remembered. A missing ephemeris file is a configuration error, not a
   * date out of range, and is not among them.
   */
  private static final Set<OrekitMessages> OUT_OF_RANGE =
      EnumSet.of(OrekitMessages.NO_DATA_GENERATED, OrekitMessages.UNABLE_TO_GENERATE_NEW_DATA_BEFORE,
          OrekitMessages.UNABLE_TO_GENERATE_NEW_DATA_AFTER,
          OrekitMessages.OUT_OF_RANGE_EPHEMERIDES_DATE_BEFORE,
          OrekitMessages.OUT_OF_RANGE_EPHEMERIDES_DATE_AFTER);

  /**
   * "No cached entries", which Orekit throws for a date out of range once a first attempt has
   * failed - but which says nothing about where the span ends, so the date gets the fallback and
   * is not remembered.
   */
  private static final Set<OrekitMessages> CACHE_FAILURE = EnumSet.of(OrekitMessages.NO_CACHED_ENTRIES);

  /** J2000; every JPL ephemeris file Orekit can read covers it. */
  private static final double PIVOT_JD = 2451545.0;

  /**
   * Latest Julian day (UTC) before J2000 for which the ephemerides failed, and earliest after it.
   * The data set is shared by all bodies and never shrinks, so plain volatile fields are enough;
   * a race only costs one more failed Orekit call.
   */
  private static volatile double failedBelow = Double.NEGATIVE_INFINITY;

  private static volatile double failedAbove = Double.POSITIVE_INFINITY;

  private final CelestialBody orekitBody;

  private final MeeusBodyProvider meeus;

  public FallbackBodyProvider(CelestialBody orekitBody, MeeusBody meeusBody) {
    this.orekitBody = orekitBody;
    this.meeus = new MeeusBodyProvider(meeusBody);
  }

  /** The Orekit body, for the computations which have no fallback. */
  public CelestialBody orekitBody() {
    return orekitBody;
  }

  /** Whether {@code date} is already known to lie outside the ephemerides. */
  private static boolean knownOutside(double jd) {
    return jd <= failedBelow || jd >= failedAbove;
  }

  /**
   * Whether {@code rex} means the ephemerides do not cover the date. Orekit reports that as an
   * {@link OrekitException} on the first attempt and as an
   * {@link org.orekit.errors.OrekitIllegalStateException} ("no cached entries") afterwards.
   */
  private static boolean isOutOfRange(RuntimeException rex) {
    return isOneOf(rex, OUT_OF_RANGE) || isOneOf(rex, CACHE_FAILURE);
  }

  private static boolean isOneOf(RuntimeException rex, Set<OrekitMessages> messages) {
    return rex instanceof LocalizedException
        && ((LocalizedException) rex).getSpecifier() instanceof OrekitMessages
        && messages.contains((OrekitMessages) ((LocalizedException) rex).getSpecifier());
  }

  private static void rememberFailure(double jd, RuntimeException rex) {
    if (!isOneOf(rex, OUT_OF_RANGE)) {
      // a cache failure does not show where the ephemerides end
      return;
    }
    if (jd < PIVOT_JD) {
      if (jd > failedBelow) {
        failedBelow = jd;
      }
    } else if (jd < failedAbove) {
      failedAbove = jd;
    }
  }

  /**
   * The Meeus provider if {@code date} is outside the ephemerides and inside the range the Meeus
   * theories are used for, otherwise <code>null</code>.
   */
  private MeeusBodyProvider fallbackFor(AbsoluteDate date, RuntimeException cause) {
    if (!AstroFallback.isAllowed() || !MeeusBodyProvider.covers(date)) {
      if (cause instanceof OrekitException) {
        throw cause;
      }
      // the builtins report an OrekitException as the orekitdata message
      throw new OrekitException(cause, OrekitMessages.NO_DATA_GENERATED, date);
    }
    AstroFallback.report(date);
    return meeus;
  }

  @Override
  public Vector3D getPosition(AbsoluteDate date, Frame frame) {
    double jd = date.getJD(TimeScalesFactory.getUTC());
    if (knownOutside(jd)) {
      MeeusBodyProvider fallback = fallbackFor(date, null);
      if (fallback != null) {
        return fallback.getPosition(date, frame);
      }
    }
    try {
      return orekitBody.getPosition(date, frame);
    } catch (RuntimeException rex) {
      if (!isOutOfRange(rex)) {
        throw rex;
      }
      rememberFailure(jd, rex);
      return fallbackFor(date, rex).getPosition(date, frame);
    }
  }

  @Override
  public TimeStampedPVCoordinates getPVCoordinates(AbsoluteDate date, Frame frame) {
    double jd = date.getJD(TimeScalesFactory.getUTC());
    if (knownOutside(jd)) {
      MeeusBodyProvider fallback = fallbackFor(date, null);
      if (fallback != null) {
        return fallback.getPVCoordinates(date, frame);
      }
    }
    try {
      return orekitBody.getPVCoordinates(date, frame);
    } catch (RuntimeException rex) {
      if (!isOutOfRange(rex)) {
        throw rex;
      }
      rememberFailure(jd, rex);
      return fallbackFor(date, rex).getPVCoordinates(date, frame);
    }
  }

  @Override
  public Vector3D getVelocity(AbsoluteDate date, Frame frame) {
    return getPVCoordinates(date, frame).getVelocity();
  }

  @Override
  public <T extends CalculusFieldElement<T>> FieldVector3D<T> getPosition(FieldAbsoluteDate<T> date,
      Frame frame) {
    AbsoluteDate reference = date.toAbsoluteDate();
    double jd = reference.getJD(TimeScalesFactory.getUTC());
    if (knownOutside(jd)) {
      MeeusBodyProvider fallback = fallbackFor(reference, null);
      if (fallback != null) {
        return fallback.getPosition(date, frame);
      }
    }
    try {
      return orekitBody.getPosition(date, frame);
    } catch (RuntimeException rex) {
      if (!isOutOfRange(rex)) {
        throw rex;
      }
      rememberFailure(jd, rex);
      return fallbackFor(reference, rex).getPosition(date, frame);
    }
  }
}
