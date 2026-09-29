/*
 * PlutoTheory.java  -  Pluto's coordinates
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
 * Modified 2026 for Symja: the Pluto class of com.nvastro.nvj.NearSkyDB (Night Vision 5.5) ported
 * to package org.matheclipse.astro.meeus; the double[1] out-parameters became return values, and
 * the light-time iteration can be switched off.
 */

package org.matheclipse.astro.meeus;

/**
 * Pluto's coordinates.
 *
 * <p>
 * Method from "Astronomical Algorithms" 2nd Edition by Jean Meeus (c) March 2000 by Willmann-Bell,
 * Inc. Chapter 37. Book says method only valid for 1885 - 2099 (&lt; 1 revolution). I tried an
 * approx. Pluto year of 90712 days (= 248.36 Julian years). +/- 4 of these years yielded results
 * within a fraction of a degree within J2000. Perhaps better results if tweaked 90712 days, but
 * planet perturbations will limit how close results can get. Thus it appears that even though the
 * following method is valid (accurate) for 1885 - 2099, it does not produce bad results for 1000 -
 * 3000.
 *
 * @author Brian Simpson
 */
final class PlutoTheory {
  private static final double D2R = Math.PI / 180;
  // e2k is the mean obliquity of the ecliptic at epoch J2000.0 (23.4392911 deg)
  static final double sine2k = 0.397777156; // sin e2k
  static final double cose2k = 0.917482062; // cos e2k

  private PlutoTheory() {}

  /**
   * Calculates J2000 coordinates for Pluto.
   *
   * @param j Julian date
   * @param sunx Sun's J2000 equatorial rectangular coordinate X in AUs
   * @param suny Sun's J2000 equatorial rectangular coordinate Y in AUs
   * @param sunz Sun's J2000 equatorial rectangular coordinate Z in AUs
   * @param lightTime whether to iterate once more for the light-time
   * @return <code>{ra, dec, dist, sdist}</code>: right ascension and declination in radians,
   *         distance from earth and from sun in AUs
   */
  static double[] calcPluto2000(double j, double sunx, double suny, double sunz,
      boolean lightTime) {
    double T, J, S, P;
    double alpha, tau = 0, l, b, r = 0, cosl, sinl, cosb, sinb;
    double x, y, z;
    double xi = 0, eta = 0, zeta = 0, dist = 0;
    int i;

    int loops = lightTime ? 2 : 1;
    for (int loop = 1; loop <= loops; loop++) {
      j -= tau;
      T = (j - 2451545) / 36525;
      J = 34.35 + 3034.9057 * T;
      S = 50.08 + 1222.1138 * T;
      P = 238.96 + 144.9600 * T;
      l = b = r = 0;
      for (i = 0; i < plutotbl.length; i += 9) {
        alpha = plutotbl[i] * J + plutotbl[i + 1] * S + plutotbl[i + 2] * P;
        alpha = (alpha % 360) * D2R; // 2 % 3 = 2, (-2) % 3 = -2
        l += plutotbl[i + 3] * Math.sin(alpha) + plutotbl[i + 4] * Math.cos(alpha);
        b += plutotbl[i + 5] * Math.sin(alpha) + plutotbl[i + 6] * Math.cos(alpha);
        r += plutotbl[i + 7] * Math.sin(alpha) + plutotbl[i + 8] * Math.cos(alpha);
      }
      l = 238.958116 + 144.96 * T + l / 1e6;
      b = -3.908239 + b / 1e6;
      r = 40.7241346 + r / 1e7;
      l %= 360; // 2 % 3 = 2, (-2) % 3 = -2

      l *= D2R;
      b *= D2R;
      cosl = Math.cos(l);
      sinl = Math.sin(l);
      cosb = Math.cos(b);
      sinb = Math.sin(b);
      x = r * cosl * cosb;
      y = r * (sinl * cosb * cose2k - sinb * sine2k);
      z = r * (sinl * cosb * sine2k + sinb * cose2k);

      // Apply formula 33.10 from P. 229
      xi = sunx + x;
      eta = suny + y;
      zeta = sunz + z;
      dist = Math.sqrt(xi * xi + eta * eta + zeta * zeta);
      tau = 0.0057755183 * dist;
    }
    return new double[] {Math.atan2(eta, xi), Math.asin(zeta / dist), dist, r};
  }

  /**
   * Calculates heliocentric coordinates for solar system view.
   *
   * @param t Julian millennia from J2000.0
   * @return <code>{l, b, r}</code>: heliocentric ecliptical longitude and latitude in radians,
   *         approximately referred to the equinox of date, and radius vector in AU
   */
  static double[] calcHelioCentricCoord(double t) {
    double J, S, P;
    double alpha;
    double l = 0, b = 0, r = 0;

    t *= 10; // Convert Julian millennia to centuries (from J2000.0)
    J = 34.35 + 3034.9057 * t;
    S = 50.08 + 1222.1138 * t;
    P = 238.96 + 144.9600 * t;
    for (int i = 0; i < plutotbl.length; i += 9) {
      alpha = plutotbl[i] * J + plutotbl[i + 1] * S + plutotbl[i + 2] * P;
      alpha = (alpha % 360) * D2R; // 2 % 3 = 2, (-2) % 3 = -2
      l += plutotbl[i + 3] * Math.sin(alpha) + plutotbl[i + 4] * Math.cos(alpha);
      b += plutotbl[i + 5] * Math.sin(alpha) + plutotbl[i + 6] * Math.cos(alpha);
      r += plutotbl[i + 7] * Math.sin(alpha) + plutotbl[i + 8] * Math.cos(alpha);
    }
    l = 238.958116 + 144.96 * t + l / 1e6;
    b = -3.908239 + b / 1e6;
    r = 40.7241346 + r / 1e7;

    // Do quick and dirty conversion to current epoch
    l += t * 360 / 260; // A 360 degree advance every 26000 years

    l %= 360; // 2 % 3 = 2, (-2) % 3 = -2

    return new double[] {l * D2R, b * D2R, r};
  }

  // @formatter:off
  private static final int plutotbl[] = {
  //Argument      Longitude           Latitude          Radius vector
  //J  S  P      A         B         A         B         A         B
    0, 0, 1,-19799805, 19850055, -5452852,-14974862, 66865439, 68951812,
    0, 0, 2,   897144, -4954829,  3527812,  1672790,-11827535,  -332538,
    0, 0, 3,   611149,  1211027, -1050748,   327647,  1593179, -1438890,
    0, 0, 4,  -341243,  -189585,   178690,  -292153,   -18444,   483220,
    0, 0, 5,   129287,   -34992,    18650,   100340,   -65977,   -85431,
    0, 0, 6,   -38164,    30893,   -30697,   -25823,    31174,    -6032,
    0, 1,-1,    20442,    -9987,     4878,    11248,    -5794,    22161,
    0, 1, 0,    -4063,    -5071,      226,      -64,     4601,     4032,
    0, 1, 1,    -6016,    -3336,     2030,     -836,    -1729,      234,
    0, 1, 2,    -3956,     3039,       69,     -604,     -415,      702,
    0, 1, 3,     -667,     3572,     -247,     -567,      239,      723,
    0, 2,-2,     1276,      501,      -57,        1,       67,      -67,
    0, 2,-1,     1152,     -917,     -122,      175,     1034,     -451,
    0, 2, 0,      630,    -1277,      -49,     -164,     -129,      504,
    1,-1, 0,     2571,     -459,     -197,      199,      480,     -231,
    1,-1, 1,      899,    -1449,      -25,      217,        2,     -441,
    1, 0,-3,    -1016,     1043,      589,     -248,    -3359,      265,
    1, 0,-2,    -2343,    -1012,     -269,      711,     7856,    -7832,
    1, 0,-1,     7042,      788,      185,      193,       36,    45763,
    1, 0, 0,     1199,     -338,      315,      807,     8663,     8547,
    1, 0, 1,      418,      -67,     -130,      -43,     -809,     -769,
    1, 0, 2,      120,     -274,        5,        3,      263,     -144,
    1, 0, 3,      -60,     -159,        2,       17,     -126,       32,
    1, 0, 4,      -82,      -29,        2,        5,      -35,      -16,
    1, 1,-3,      -36,      -29,        2,        3,      -19,       -4,
    1, 1,-2,      -40,        7,        3,        1,      -15,        8,
    1, 1,-1,      -14,       22,        2,       -1,       -4,       12,
    1, 1, 0,        4,       13,        1,       -1,        5,        6,
    1, 1, 1,        5,        2,        0,       -1,        3,        1,
    1, 1, 3,       -1,        0,        0,        0,        6,       -2,
    2, 0,-6,        2,        0,        0,       -2,        2,        2,
    2, 0,-5,       -4,        5,        2,        2,       -2,       -2,
    2, 0,-4,        4,       -7,       -7,        0,       14,       13,
    2, 0,-3,       14,       24,       10,       -8,      -63,       13,
    2, 0,-2,      -49,      -34,       -3,       20,      136,     -236,
    2, 0,-1,      163,      -48,        6,        5,      273,     1065,
    2, 0, 0,        9,      -24,       14,       17,      251,      149,
    2, 0, 1,       -4,        1,       -2,        0,      -25,       -9,
    2, 0, 2,       -3,        1,        0,        0,        9,       -2,
    2, 0, 3,        1,        3,        0,        0,       -8,        7,
    3, 0,-2,       -3,       -1,        0,        1,        2,      -10,
    3, 0,-1,        5,       -3,        0,        0,       19,       35,
    3, 0, 0,        0,        0,        1,        0,       10,        3
  };
  // @formatter:on
}
