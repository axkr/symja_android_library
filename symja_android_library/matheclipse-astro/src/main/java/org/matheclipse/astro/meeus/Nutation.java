/*
 * Nutation.java  -  Nutation, the obliquity of the ecliptic, & aberration
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
 * Modified 2026 for Symja: ported from com.nvastro.nvj.Nutate (Night Vision 5.5) to package
 * org.matheclipse.astro.meeus. The class is immutable (created by at(jde) instead of setJDay),
 * the SphereCoords overloads were dropped and the double[1] out-parameters became return values.
 */

package org.matheclipse.astro.meeus;

/**
 * Nutation, the obliquity of the ecliptic, and aberration.
 *
 * <p>
 * Methods from "Astronomical Algorithms" 2nd Ed. by Jean Meeus (c) 1998, second printing March
 * 2000 by Willmann-Bell, Inc.
 *
 * @author Brian Simpson
 */
public final class Nutation {
  /** J2000.0 */
  public static final double J2000_0 = 2451545.0;
  private static final double HalfPI = Math.PI / 2;
  private static final double D2R = Math.PI / 180;

  /* Variables for aberration */
  private static final double k = 20.49522 * Math.PI / 648000; // (* Sec to rad)

  private final Mat3 nut; // Nutation matrix
  private final double dpsi, dep; // Nutation parameters (rad)
  private final double ep0; // Mean obliquity of the ecliptic (rad)
  private final double cep, sep; // Cos & sin of epsilon (ep0 + dep)
  private final double ec; // Eccentricity of the Earth's orbit
  private final double pi; // Longitude of perihelion of Earth's orbit (rad)
  private final double LSun; // True geometric longitude of the sun referred
  // to the mean equinox of the date (rad); represented by a circle with a dot

  /**
   * Sets up nutation parameters and matrix.
   *
   * @param jday Julian Ephemeris Day
   */
  public static Nutation at(double jday) {
    return new Nutation(jday);
  }

  private Nutation(double jday) {
    /* Calculate nutation parameters (Chapter 22, P. 144) */
    // (Using higher accuracy formula)
    int i;
    double arg;
    double dpsi = 0, dep = 0;
    double T = (jday - J2000_0) / 36525; // Julian centuries from J2000.0
    double D = 297.85036 + T * (445267.111480 - T * (0.0019142 - T / 189474));
    double M = 357.52772 + T * (35999.050340 - T * (0.0001603 + T / 300000));
    double Mp = 134.96298 + T * (477198.867398 + T * (0.0086972 + T / 56250));
    double F = 93.27191 + T * (483202.017538 - T * (0.0036825 - T / 327270));
    double Om = 125.04452 - T * (1934.136261 - T * (0.0020708 + T / 450000));
    T /= 10; // Julian millennia from J2000.0
    for (i = 0; i < n.length; i += 9) {
      arg = (n[i] * D + n[i + 1] * M + n[i + 2] * Mp + n[i + 3] * F + n[i + 4] * Om) % 360 * D2R;
      dpsi += (n[i + 5] + n[i + 6] * T) * Math.sin(arg);
      dep += (n[i + 7] + n[i + 8] * T) * Math.cos(arg);
    }
    dpsi *= D2R / 36000000; // radians
    dep *= D2R / 36000000; // radians
    this.dpsi = dpsi;
    this.dep = dep;

    /* Calculate the obliquity of the ecliptic */
    // (Using formula on P. 147)
    double U = T / 10; // 10K Julian years from J2000.0
    double ep0 = 21.448 - U * (4680.93 + U * (1.55 - U * (1999.25 - U * (51.38
        + U * (249.67 + U * (39.05 - U * (7.12 + U * (27.87 + U * (5.79 + U * 2.45)))))))));
    ep0 = (23 + (26 + ep0 / 60) / 60) * D2R;
    this.ep0 = ep0;
    double ep = ep0 + dep; // True obliquity of the ecliptic

    cep = Math.cos(ep);
    sep = Math.sin(ep);
    double ce0 = Math.cos(ep0);
    double se0 = Math.sin(ep0);
    double cdp = Math.cos(dpsi);
    double sdp = Math.sin(dpsi);

    // To nutate equatorial coordinates from J2000:
    // 1) rotate by epsilon0 about X axis (ZxY) (converts to ecliptical)
    // 2) rotate by delta psi about Z axis (XxY)
    // 3) rotate by epsilon (= epsilon0 + delta epsilon) along X axis (YxZ)
    // (converts back to equatorial)
    // | xnew | | 1 0 0 | | cdp -sdp 0 | | 1 0 0 | | xold |
    // | ynew | = | 0 cep -sep | | sdp cdp 0 | | 0 ce0 se0 | | yold |
    // | znew | | 0 sep cep | | 0 0 1 | | 0 -se0 ce0 | | zold |

    nut = new Mat3(cdp, -ce0 * sdp, -se0 * sdp, //
        cep * sdp, ce0 * cep * cdp + se0 * sep, se0 * cep * cdp - ce0 * sep, //
        sep * sdp, ce0 * sep * cdp - se0 * cep, se0 * sep * cdp + ce0 * cep);

    /* Calculate variables for aberration */
    // Use "low accuracy" formulas on P. 163-164. Accuracy is sufficient
    // for calculating aberration, as aberration amounts to arc seconds,
    // and being a very small fraction of a second off is OK. Also
    // use formulas on P. 151.
    T *= 10; // T back to Julian centuries from J2000
    double L0 = 280.46646 + T * (36000.76983 + T * 0.0003032);
    M = ((357.52911 + T * (35999.05029 - T * 0.0001537)) % 360) * D2R;
    double C = (1.914602 - T * (0.004817 + T * 0.000014)) * Math.sin(M)
        + (0.019993 - T * 0.000101) * Math.sin(2 * M) + 0.000289 * Math.sin(3 * M);
    double LSun = (L0 + C) % 360; // 2 % 360 = 2, (-2) % 360 = -2
    if (LSun < 0)
      LSun += 360;
    this.LSun = LSun * D2R;
    ec = 0.016708634 - T * (0.000042037 + T * 0.0000001267);
    pi = (102.93735 + T * (1.71946 + T * 0.00046)) * D2R;
  }

  /** The nutation matrix, rotating mean equatorial coordinates of date into true ones. */
  Mat3 matrix() {
    return nut;
  }

  /** Nutation in longitude, delta psi, in radians. */
  public double deltaPsi() {
    return dpsi;
  }

  /** Nutation in obliquity, delta epsilon, in radians. */
  public double deltaEpsilon() {
    return dep;
  }

  /** The mean obliquity of the ecliptic epsilon0 in radians. */
  public double meanObliquity() {
    return ep0;
  }

  /** The true obliquity of the ecliptic (epsilon0 + delta epsilon) in radians. */
  public double trueObliquity() {
    return (ep0 + dep);
  }

  /**
   * Converts ecliptical into equatorial (ra/dec). This function adjusts for nutation.
   *
   * @param lambda Ecliptical (or celestial) longitude, measured from the vernal equinox along the
   *        ecliptic
   * @param beta Ecliptical (or celestial) latitude, positive if north of the ecliptic, negative is
   *        south
   * @return <code>{ra, dec}</code> in radians
   */
  public double[] eclipticToEquatorial(double lambda, double beta) {
    lambda += dpsi;
    double sinlambda = Math.sin(lambda);
    double cosbeta = Math.cos(beta);
    double sinbeta = Math.sin(beta);
    // -pi <= Math.atan2 <= pi, no problem if 2nd arg is 0
    double ra = Math.atan2(sinlambda * cep - sinbeta * sep / cosbeta, Math.cos(lambda));
    double dec = sinbeta * cep + cosbeta * sep * sinlambda;
    if (dec > 1)
      dec = HalfPI;
    else if (dec < -1)
      dec = -HalfPI;
    else
      dec = Math.asin(dec);
    return new double[] {ra, dec};
  }

  /**
   * Converts equatorial into ecliptical (lambda/beta). This function adjusts for nutation.
   *
   * @param ra Right ascension in radians
   * @param dec Declination in radians
   * @return <code>{lambda, beta}</code> in radians
   */
  public double[] equatorialToEcliptic(double ra, double dec) {
    double sinalpha = Math.sin(ra);
    double sindelta = Math.sin(dec);
    double cosdelta = Math.cos(dec);
    double lambda = Math.atan2(sinalpha * cep + sindelta * sep / cosdelta, Math.cos(ra));
    double beta = sindelta * cep - cosdelta * sep * sinalpha;
    if (beta > 1)
      beta = HalfPI;
    else if (beta < -1)
      beta = -HalfPI;
    else
      beta = Math.asin(beta);
    lambda -= dpsi;
    return new double[] {lambda, beta};
  }

  /**
   * Adjusts ecliptical (lambda/beta) coordinates for aberration. (Displacement due to Earth's
   * orbital motion.)
   *
   * @return <code>{lambda, beta}</code> in radians after the adjustment
   */
  public double[] eclipticAberration(double lambda, double beta) {
    // Formula on P. 151
    double pi_lambda = pi - lambda;
    double LSun_lambda = LSun - lambda;
    return new double[] {lambda + k * (ec * Math.cos(pi_lambda) - Math.cos(LSun_lambda)) / Math.cos(beta),
        beta - k * Math.sin(beta) * (Math.sin(LSun_lambda) - ec * Math.sin(pi_lambda))};
  }

  /**
   * Adjusts equatorial (ra/dec) coordinates for aberration. (Displacement due to Earth's orbital
   * motion.)
   *
   * @return <code>{ra, dec}</code> in radians after the adjustment
   */
  public double[] equatorialAberration(double ra, double dec) {
    double[] lb = equatorialToEcliptic(ra, dec);
    lb = eclipticAberration(lb[0], lb[1]);
    return eclipticToEquatorial(lb[0], lb[1]);
  }

  // @formatter:off
  private static final int n[] = {
     0,  0,  0,  0,  1, -171996, -1742, 92025,  89,
    -2,  0,  0,  2,  2,  -13187,   -16,  5736, -31,
     0,  0,  0,  2,  2,   -2274,    -2,   977,  -5,
     0,  0,  0,  0,  2,    2062,     2,  -895,   5,
     0,  1,  0,  0,  0,    1426,   -34,    54,  -1,
     0,  0,  1,  0,  0,     712,     1,    -7,   0,
    -2,  1,  0,  2,  2,    -517,    12,   224,  -6,
     0,  0,  0,  2,  1,    -386,    -4,   200,   0,
     0,  0,  1,  2,  2,    -301,     0,   129,  -1,
    -2, -1,  0,  2,  2,     217,    -5,   -95,   3,
    -2,  0,  1,  0,  0,    -158,     0,     0,   0,
    -2,  0,  0,  2,  1,     129,     1,   -70,   0,
     0,  0, -1,  2,  2,     123,     0,   -53,   0,
     2,  0,  0,  0,  0,      63,     0,     0,   0,
     0,  0,  1,  0,  1,      63,     1,   -33,   0,
     2,  0, -1,  2,  2,     -59,     0,    26,   0,
     0,  0, -1,  0,  1,     -58,    -1,    32,   0,
     0,  0,  1,  2,  1,     -51,     0,    27,   0,
    -2,  0,  2,  0,  0,      48,     0,     0,   0,
     0,  0, -2,  2,  1,      46,     0,   -24,   0,
     2,  0,  0,  2,  2,     -38,     0,    16,   0,
     0,  0,  2,  2,  2,     -31,     0,    13,   0,
     0,  0,  2,  0,  0,      29,     0,     0,   0,
    -2,  0,  1,  2,  2,      29,     0,   -12,   0,
     0,  0,  0,  2,  0,      26,     0,     0,   0,
    -2,  0,  0,  2,  0,     -22,     0,     0,   0,
     0,  0, -1,  2,  1,      21,     0,   -10,   0,
     0,  2,  0,  0,  0,      17,    -1,     0,   0,
     2,  0, -1,  0,  1,      16,     0,    -8,   0,
    -2,  2,  0,  2,  2,     -16,     1,     7,   0,
     0,  1,  0,  0,  1,     -15,     0,     9,   0,
    -2,  0,  1,  0,  1,     -13,     0,     7,   0,
     0, -1,  0,  0,  1,     -12,     0,     6,   0,
     0,  0,  2, -2,  0,      11,     0,     0,   0,
     2,  0, -1,  2,  1,     -10,     0,     5,   0,
     2,  0,  1,  2,  2,      -8,     0,     3,   0,
     0,  1,  0,  2,  2,       7,     0,    -3,   0,
    -2,  1,  1,  0,  0,      -7,     0,     0,   0,
     0, -1,  0,  2,  2,      -7,     0,     3,   0,
     2,  0,  0,  2,  1,      -7,     0,     3,   0,
     2,  0,  1,  0,  0,       6,     0,     0,   0,
    -2,  0,  2,  2,  2,       6,     0,    -3,   0,
    -2,  0,  1,  2,  1,       6,     0,    -3,   0,
     2,  0, -2,  0,  1,      -6,     0,     3,   0,
     2,  0,  0,  0,  1,      -6,     0,     3,   0,
     0, -1,  1,  0,  0,       5,     0,     0,   0,
    -2, -1,  0,  2,  1,      -5,     0,     3,   0,
    -2,  0,  0,  0,  1,      -5,     0,     3,   0,
     0,  0,  2,  2,  1,      -5,     0,     3,   0,
    -2,  0,  2,  0,  1,       4,     0,     0,   0,
    -2,  1,  0,  2,  1,       4,     0,     0,   0,
     0,  0,  1, -2,  0,       4,     0,     0,   0,
    -1,  0,  1,  0,  0,      -4,     0,     0,   0,
    -2,  1,  0,  0,  0,      -4,     0,     0,   0,
     1,  0,  0,  0,  0,      -4,     0,     0,   0,
     0,  0,  1,  2,  0,       3,     0,     0,   0,
     0,  0, -2,  2,  2,      -3,     0,     0,   0,
    -1, -1,  1,  0,  0,      -3,     0,     0,   0,
     0,  1,  1,  0,  0,      -3,     0,     0,   0,
     0, -1,  1,  2,  2,      -3,     0,     0,   0,
     2, -1, -1,  2,  2,      -3,     0,     0,   0,
     0,  0,  3,  2,  2,      -3,     0,     0,   0,
     2, -1,  0,  2,  2,      -3,     0,     0,   0
  };
  // @formatter:on
}
