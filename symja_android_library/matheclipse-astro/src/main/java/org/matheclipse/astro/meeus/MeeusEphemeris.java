/*
 * MeeusEphemeris.java  -  Positions of the Sun, the Moon and the planets
 * Copyright (C) 2011-2024 Brian Simpson
 * This file is part of Night Vision.
 *
 * Night Vision is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Night Vision is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Night Vision.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Modified 2026 for Symja: getCoordinates, adjustForParallax, convToFK5 and getHCCoordinates of
 * com.nvastro.nvj.NearSkyDB (Night Vision 5.5) ported to package org.matheclipse.astro.meeus.
 * The MapParms/LST inputs became an explicit Julian ephemeris day and an optional Observer, the
 * cached Earth state became local variables, the "geocentric" preference became a null observer,
 * and the light-time correction can be switched off to get geometric positions.
 */

package org.matheclipse.astro.meeus;

/**
 * Positions of the Sun, the Moon and the planets from the theories in "Astronomical Algorithms"
 * 2nd Ed. by Jean Meeus (c) 1998, second printing March 2000 by Willmann-Bell, Inc.
 *
 * <ul>
 * <li>Planets except Pluto - Chap. 33, P225: periodic terms for the Earth and the planet,
 * light-time, conversion to ecliptical, aberration, FK5 conversion, conversion to equatorial with
 * nutation, parallax.</li>
 * <li>Pluto - Chap. 37, P266: periodic terms for EarthJ2000, reversed to get the Sun, Pluto
 * formula, light-time, conversion to equatorial, precession and nutation, aberration,
 * parallax.</li>
 * <li>Sun - Chap. 25, P166: periodic terms for the Earth reversed, FK5 conversion, aberration,
 * conversion to equatorial, parallax.</li>
 * <li>Moon - P337: Chap. 47 ecliptical coordinates, conversion to equatorial, parallax (no
 * adjustment for light-time, FK5, or aberration).</li>
 * </ul>
 *
 * <p>
 * All methods are pure functions of their arguments and safe to call from any thread.
 *
 * @author Brian Simpson
 */
public final class MeeusEphemeris {

  /** Astronomical Units to Kilometers */
  public static final double AU_KM = 149597870.691;

  private static final double D2R = Math.PI / 180; // Degrees to radians
  private static final double S2R = Math.PI / 648000; // Seconds to radians
  /* Some constants for parallax adjustment */
  private static final double PAR = Math.sin(8.794 * S2R); // Parallax
  // constant on P.279 (sin of 8.794")
  private static final double P2E = 6356.755 / 6378.14; // Polar to equatorial

  private MeeusEphemeris() {}

  /**
   * Where on the Earth the observer stands, for the diurnal parallax. (See P. 81-82)
   *
   * @param localSiderealTime LST in radians
   * @param rhoSinPhi Rho * sin of geocentric latitude (P. 82)
   * @param rhoCosPhi Rho * cos of geocentric latitude (P. 82)
   */
  public record Observer(double localSiderealTime, double rhoSinPhi, double rhoCosPhi) {

    /**
     * @param latitudeDeg geographic latitude in degrees
     * @param longitudeDeg east longitude in degrees
     * @param jd Julian day (UT), for the sidereal time
     */
    public static Observer of(double latitudeDeg, double longitudeDeg, double jd) {
      double phi = latitudeDeg * D2R; // Geographic latitude
      double u = Math.atan(Math.tan(phi) * P2E);
      // Ht is meters above sea level (when I get it implemented...)
      return new Observer(SiderealTime.localHours(jd, longitudeDeg) * Math.PI / 12,
          P2E * Math.sin(u), Math.cos(u));
    }
  }

  /**
   * A position computed by {@link MeeusEphemeris#position}.
   *
   * @param rightAscension right ascension in radians, in -pi..pi
   * @param declination declination in radians
   * @param distance distance from the earth (or the observer) in AU
   * @param sunDistance distance from the sun in AU; NaN for the Sun and the Moon
   * @param earthSunDistance the radius vector of the Earth in AU
   */
  public record Position(double rightAscension, double declination, double distance,
      double sunDistance, double earthSunDistance) {

    /** The position as a cartesian vector in AU. */
    public double[] cartesian() {
      double[] u = Mat3.unit(rightAscension, declination);
      return new double[] {u[0] * distance, u[1] * distance, u[2] * distance};
    }
  }

  /**
   * Returns ra, &amp; dec (J2000 or apparent), and distance for specified object. If apparent
   * coordinates are desired, corrections for precession, nutation, light-time, aberration, and
   * parallax are applied. If J2000 coordinates are desired, corrections for light-time and parallax
   * are applied.
   *
   * @param object Object whose coordinates are desired
   * @param j Julian ephemeris day
   * @param app If true, return apparent coordinates of date, else J2000
   * @param lightTime If false, skip the light-time correction of the planets, which gives their
   *        geometric position
   * @param observer the observer for the diurnal parallax, or <code>null</code> for geocentric
   *        coordinates
   */
  public static Position position(MeeusBody object, double j, boolean app, boolean lightTime,
      Observer observer) {
    double x, y, z; // All in AU
    double cosl, sinl, cosb, sinb;
    double tmpx, tmpy;
    double t; // Julian millennia from epoch J2000.0
    double lambda, beta;
    double ra, dec, dist, sdist = Double.NaN;

    Precession mp = Precession.at(j);
    Nutation nut = mp.nutation();
    t = (j - 2451545.0) / 365250; // Julian millennia from J2000.0

    /* Calculate earth coordinates */
    double[] earth = Vsop87.heliocentric(Vsop87Earth.TERMS, t);
    double L = earth[0], B = earth[1], R = earth[2];
    double cosB = Math.cos(B);
    double RcosBcosL = R * cosB * Math.cos(L);
    double RcosBsinL = R * cosB * Math.sin(L);
    double RsinB = R * Math.sin(B);

    /* Determine coordinates of object */
    double[] radec;
    if (object.isPlanet()) { /* If object = planet */
      if (object != MeeusBody.PLUTO) { // If planet (other than Pluto), Using Chap. 33, P225
        double[][][] data = Vsop87.terms(object);
        double[] lbr = Vsop87.heliocentric(data, t);
        cosl = Math.cos(lbr[0]);
        sinl = Math.sin(lbr[0]);
        cosb = Math.cos(lbr[1]);
        sinb = Math.sin(lbr[1]);
        x = lbr[2] * cosb * cosl - RcosBcosL;
        y = lbr[2] * cosb * sinl - RcosBsinL;
        z = lbr[2] * sinb - RsinB;
        dist = Math.sqrt(x * x + y * y + z * z); // Distance from earth

        if (lightTime) {
          // 2nd pass to adjust for light-time
          t -= dist * 1.5812507324e-8; // (0.0057755183 / 365250)
          lbr = Vsop87.heliocentric(data, t);
          cosl = Math.cos(lbr[0]);
          sinl = Math.sin(lbr[0]);
          cosb = Math.cos(lbr[1]);
          sinb = Math.sin(lbr[1]);
          x = lbr[2] * cosb * cosl - RcosBcosL;
          y = lbr[2] * cosb * sinl - RcosBsinL;
          z = lbr[2] * sinb - RsinB;
          dist = Math.sqrt(x * x + y * y + z * z); // Distance from earth
        }
        sdist = lbr[2]; // Distance from sun

        // Calculate ecliptical coordinates
        lambda = Math.atan2(y, x); // No problem if x = 0; Precessed
        beta = Math.atan(z / Math.sqrt(x * x + y * y)); // Coordinates
        // Adjust for aberration
        if (app) {
          double[] lb = nut.eclipticAberration(lambda, beta);
          lambda = lb[0];
          beta = lb[1];
        }
        // FK5 conversion
        double[] lb = convToFK5(j, lambda, beta);
        // Convert to equatorial
        radec = nut.eclipticToEquatorial(lb[0], lb[1]); // Handles nutation
      } else { // Else Pluto, Using Chap. 37, P266 (& P172-175)
        double[] lbr = Vsop87.heliocentric(Vsop87EarthJ2000.TERMS, t);
        cosl = Math.cos(lbr[0]);
        sinl = Math.sin(lbr[0]);
        cosb = Math.cos(lbr[1]);
        sinb = Math.sin(lbr[1]);
        x = -lbr[2] * cosb * cosl; // Convert
        y = -lbr[2] * cosb * sinl; // EJ2000 to
        z = -lbr[2] * sinb; // SunJ2000

        /*
         * Convert sun coord's from "ecliptical dynamical reference from (VSOP) of J2000.0" to the
         * "equatorial FK5 J2000.0 reference frame" P.174
         */
        tmpx = x + 0.000000440360 * y - 0.000000190919 * z;
        tmpy = -0.000000479966 * x + 0.917482137087 * y - 0.397776982902 * z;
        z = 0.397776982202 * y + 0.917482137087 * z;
        y = tmpy;
        x = tmpx;

        /* Get Pluto's J2000 coordinates */
        double[] pluto = PlutoTheory.calcPluto2000(j, x, y, z, lightTime);
        dist = pluto[2];
        sdist = pluto[3];

        radec = mp.precessNutate(pluto[0], pluto[1]);
        if (app) {
          radec = nut.equatorialAberration(radec[0], radec[1]);
        }
      }
    } else if (object == MeeusBody.SUN) {/* Else if object = Sun (Chap 25, P166) */
      dist = R;
      lambda = L + Math.PI; // Reverse earth's
      beta = -B; // coordinates
      // FK5 conversion
      double[] lb = convToFK5(j, lambda, beta);
      // Adjust for aberration
      if (app) {
        lb = nut.eclipticAberration(lb[0], lb[1]);
      }
      // Convert to equatorial
      radec = nut.eclipticToEquatorial(lb[0], lb[1]); // Handles nutation
    } else { /* Else object = moon (P. 337) */
      double[] moon = MoonTheory.coordinates(j); // Precessed coordinates
      dist = moon[2];
      radec = nut.eclipticToEquatorial(moon[0], moon[1]); // Handles nutation
      // Apparently light-time adjustment not needed
      // FK5 adjustment does not apply
      // No adjustment for aberration needed, as moon moves with earth about sun
    }

    ra = radec[0];
    dec = radec[1];
    // Adjust for parallax
    if (observer != null) {
      double[] p = adjustForParallax(ra, dec, dist, observer);
      ra = p[0];
      dec = p[1];
      dist = p[2];
    }
    if (!app) { // Convert to J2000
      radec = mp.unPrecessNutate(ra, dec);
      ra = radec[0];
      dec = radec[1];
    }
    return new Position(ra, dec, dist, sdist, R);
  }

  /**
   * Adjusts geocentric coordinates into topocentric coordinates. (P. 279)
   *
   * @return <code>{ra, dec, dist}</code> after the adjustment
   */
  private static double[] adjustForParallax(double ra, double dec, double dist,
      Observer observer) {
    double rhosinphip = observer.rhoSinPhi();
    double rhocosphip = observer.rhoCosPhi();
    double sinpi = PAR / dist;
    double hrangle = observer.localSiderealTime() - ra; // Hour angle in radians
    double coshrangle = Math.cos(hrangle);
    double sinhrangle = Math.sin(hrangle);
    double cosdelta = Math.cos(dec);
    double sindelta = Math.sin(dec);
    double dalpha = Math.atan2(-rhocosphip * sinpi * sinhrangle,
        cosdelta - rhocosphip * sinpi * coshrangle);
    double newRa = ra + dalpha;
    double newDec = Math.atan2(Math.cos(dalpha) * (sindelta - rhosinphip * sinpi),
        cosdelta - rhocosphip * sinpi * coshrangle);

    // P. 280
    double A = cosdelta * sinhrangle;
    double B = cosdelta * coshrangle - rhocosphip * sinpi;
    double C = sindelta - rhosinphip * sinpi;
    double q = Math.sqrt(A * A + B * B + C * C);
    return new double[] {newRa, newDec, dist * q}; // P. 391
  }

  /**
   * FK5 conversion. (P.219)
   *
   * @param julian Julian day
   * @return <code>{lambda, beta}</code> in radians after the adjustment
   */
  private static double[] convToFK5(double julian, double lambda, double beta) {
    double T = (julian - 2451545.0) / 36525; // Jul. cent. from J2000.0
    double lp = lambda - T * (1.397 + T * 0.00031) * D2R;
    double coslp = Math.cos(lp);
    double sinlp = Math.sin(lp);
    double tanb = Math.tan(beta);
    return new double[] {lambda + (0.03916 * (coslp + sinlp) * tanb - 0.09033) * S2R,
        beta + 0.03916 * (coslp - sinlp) * S2R};
  }

  /**
   * Gets heliocentric coordinates.
   *
   * @param body a planet, Mercury to Pluto
   * @param jde Julian ephemeris day
   * @return <code>{l, b, r}</code>: heliocentric ecliptical longitude and latitude of date in
   *         radians, and radius vector in AU
   */
  public static double[] heliocentric(MeeusBody body, double jde) {
    double t = (jde - 2451545.0) / 365250; // Julian millennia from J2000.0
    if (body == MeeusBody.PLUTO) {
      return PlutoTheory.calcHelioCentricCoord(t);
    }
    if (!body.isPlanet()) {
      throw new IllegalArgumentException("not a planet: " + body);
    }
    return Vsop87.heliocentric(Vsop87.terms(body), t);
  }

  /**
   * Heliocentric coordinates of the Earth, ecliptic and equinox of date.
   *
   * @param jde Julian ephemeris day
   * @return <code>{l, b, r}</code> in radians and AU
   */
  public static double[] earthHeliocentric(double jde) {
    return Vsop87.heliocentric(Vsop87Earth.TERMS, (jde - 2451545.0) / 365250);
  }
}
