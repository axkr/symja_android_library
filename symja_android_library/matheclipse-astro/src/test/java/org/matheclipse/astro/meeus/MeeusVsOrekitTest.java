package org.matheclipse.astro.meeus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.astro.convert.MeeusBodyProvider;
import org.matheclipse.astro.data.AstroDataContext;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

/**
 * The Meeus fallback against the JPL DE 440 ephemerides, inside the span both cover.
 *
 * <p>
 * The tolerances are the accuracy the fallback is documented with: the abridged VSOP87 series of
 * Meeus are good to about an arc second for the planets, the Chapter 47 lunar theory to about ten
 * arc seconds, and the Pluto theory to about an arc second in its validity range.
 */
public class MeeusVsOrekitTest {

  private static final double ARCSEC = Math.PI / 648000;

  /** Maximum angular difference in arc seconds, by body. */
  private static double tolerance(MeeusBody body) {
    // measured over 1991-2148 at equal TT: Moon 7.0", Mars 3.1", Pluto 3.1" (to 2099), Neptune
    // 2.8", Uranus 2.4", Venus 2.1", the others below 1.7"
    return body == MeeusBody.MOON ? 10 : 4;
  }

  /**
   * Compare every body at one date. The Meeus theories are evaluated at Orekit's own TT for the
   * date, so the comparison measures the theories and not the delta T model: after 2026 Orekit
   * keeps TT - UTC frozen while {@link MeeusBodyProvider} extrapolates delta T, and the Moon moves
   * half an arc second per second.
   */
  private static void check(String utc, boolean print) {
    AstroDataContext.initialize();
    AbsoluteDate date = new AbsoluteDate(utc, TimeScalesFactory.getUTC());
    double jde = date.getJD(TimeScalesFactory.getTT());
    Frame gcrf = FramesFactory.getGCRF();
    for (MeeusBody body : MeeusBody.values()) {
      String name = body.name().charAt(0) + body.name().substring(1).toLowerCase();
      Vector3D jpl = CelestialBodyFactory.getBody(name).getPosition(date, gcrf);
      double[] xyz = MeeusEphemeris.position(body, jde, false, false, null).cartesian();
      Vector3D meeus = new Vector3D(xyz[0], xyz[1], xyz[2]);
      double angle = Vector3D.angle(jpl, meeus) / ARCSEC;
      double distance = meeus.getNorm() * Constants.IAU_2012_ASTRONOMICAL_UNIT / jpl.getNorm() - 1;
      if (print) {
        System.out.printf(Locale.US, "%s %-8s %8.3f\" %10.2e%n", utc, name, angle, distance);
      }
      if (body == MeeusBody.PLUTO
          && date.getComponents(TimeScalesFactory.getUTC()).getDate().getYear() > 2099) {
        // the Chapter 37 theory is fitted to 1885-2099; after that the error grows quickly, to
        // 53" in 2105 and 6' in 2111
        continue;
      }
      assertTrue(angle < tolerance(body), utc + " " + name + " off by " + angle + "\"");
      // measured over 1991-2148: 1.5e-5 relative for Mars, less for the others
      assertEquals(0.0, distance, 5e-5, utc + " " + name + " distance");
    }
  }

  /**
   * The provider the builtins use agrees with Orekit where both use the same TT, that is inside the
   * leap second table.
   */
  private static void checkProvider(String utc) {
    AstroDataContext.initialize();
    AbsoluteDate date = new AbsoluteDate(utc, TimeScalesFactory.getUTC());
    Frame frame = FramesFactory.getEME2000();
    for (MeeusBody body : MeeusBody.values()) {
      String name = body.name().charAt(0) + body.name().substring(1).toLowerCase();
      Vector3D jpl = CelestialBodyFactory.getBody(name).getPosition(date, frame);
      Vector3D meeus = new MeeusBodyProvider(body).getPosition(date, frame);
      double angle = Vector3D.angle(jpl, meeus) / ARCSEC;
      assertTrue(angle < tolerance(body), utc + " " + name + " off by " + angle + "\"");
    }
  }

  @Test
  public void testJ2000() {
    check("2000-01-01T12:00:00", false);
    checkProvider("2000-01-01T12:00:00");
  }

  @Test
  public void testRecent() {
    check("2026-03-20T00:00:00", false);
    checkProvider("2026-03-20T00:00:00");
  }

  /** A sweep across the bundled ephemerides, 1990 to 2149. */
  @Tag("slow")
  @Test
  public void testSweep() {
    for (int year = 1991; year <= 2148; year += 3) {
      check(year + "-07-01T00:00:00", true);
    }
  }
}
