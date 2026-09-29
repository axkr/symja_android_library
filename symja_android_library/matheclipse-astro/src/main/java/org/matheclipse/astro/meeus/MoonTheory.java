/*
 * MoonTheory.java  -  Lunar coordinates
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
 * Modified 2026 for Symja: the Moon class of com.nvastro.nvj.NearSkyDB (Night Vision 5.5) ported
 * to package org.matheclipse.astro.meeus; the double[1] out-parameters became return values.
 */

package org.matheclipse.astro.meeus;

/**
 * Lunar coordinates, illuminated fraction and bright limb.
 *
 * <p>
 * Method from "Astronomical Algorithms" 2nd Edition by Jean Meeus (c) March 2000 by Willmann-Bell,
 * Inc. Chapter 47.
 *
 * @author Brian Simpson
 */
public final class MoonTheory {
  private static final double D2R = Math.PI / 180;

  private MoonTheory() {}

  /**
   * Calculates Precessed coordinates for the Moon.
   *
   * @param jde Julian ephemeris day
   * @return <code>{lambda, beta, dist}</code>: ecliptical longitude and latitude of date in radians
   *         (without nutation), and the distance between earth-moon centers in AUs
   */
  public static double[] coordinates(double jde) {
    double arg, sin, cos;
    double Lambda, Beta, Delta;
    int i, j;

    double T = (jde - 2451545) / 36525;

    double Lp = 218.3164477 + T * (481267.88123421 - T * (0.0015786 - T / (538841 - 65194000 / T)));
    double D = 297.8501921 + T * (445267.1114034 - T * (0.0018819 - T / (545868 - 113065000 / T)));
    double M = 357.5291092 + T * (35999.0502909 - T * (0.0001536 - T / 24490000));
    double Mp = 134.9633964 + T * (477198.8675055 + T * (0.0087414 + T / (69699 - 14712000 / T)));
    double F = 93.2720950 + T * (483202.0175233 - T * (0.0036539 + T / (3526000 - 863310000 / T)));
    double A1 = 119.75 + 131.849 * T;
    double A2 = 53.09 + 479264.290 * T;
    double A3 = 313.45 + 481266.484 * T;
    double E = 1 - T * (0.002516 + T * 0.0000074);
    Lp %= 360; // 2 % 3 = 2, (-2) % 3 = -2
    D %= 360;
    M %= 360;
    Mp %= 360;
    F %= 360;
    A1 %= 360;
    A2 %= 360;
    A3 %= 360;

    Lambda = 0;
    Beta = 0;
    Delta = 0;
    for (i = 0; i < lr.length; i += 6) {
      arg = (lr[i] * D + lr[i + 1] * M + lr[i + 2] * Mp + lr[i + 3] * F) % 360 * D2R;
      sin = lr[i + 4] * Math.sin(arg);
      cos = lr[i + 5] * Math.cos(arg);
      j = lr[i + 1] * lr[i + 1];
      if (j > 0) {
        sin *= E;
        cos *= E;
      }
      if (j > 1) {
        sin *= E;
        cos *= E;
      }
      Lambda += sin;
      Delta += cos;
    }
    for (i = 0; i < b.length; i += 5) {
      arg = (b[i] * D + b[i + 1] * M + b[i + 2] * Mp + b[i + 3] * F) % 360 * D2R;
      sin = b[i + 4] * Math.sin(arg);
      j = b[i + 1] * b[i + 1];
      if (j > 0) {
        sin *= E;
      }
      if (j > 1) {
        sin *= E;
      }
      Beta += sin;
    }
    Lambda += 3958 * Math.sin(A1 * D2R) + 1962 * Math.sin((Lp - F) * D2R)
        + 318 * Math.sin(A2 * D2R);
    Beta += -2235 * Math.sin(Lp * D2R) + 382 * Math.sin(A3 * D2R)
        + 175 * Math.sin((A1 - F) * D2R) + 175 * Math.sin((A1 + F) * D2R)
        + 127 * Math.sin((Lp - Mp) * D2R) + -115 * Math.sin((Lp + Mp) * D2R);
    Lambda = Lambda / 1000000 + Lp;
    Beta /= 1000000;
    Delta = 385000.56 + Delta / 1000;

    return new double[] {Lambda * D2R, Beta * D2R, Delta / MeeusEphemeris.AU_KM}; // km to AU
  }

  /**
   * Returns the illuminated fraction of the moon.
   *
   * <p>
   * Uses slightly simplified formula that doesn't take into account the relative distances of the
   * sun and the moon.
   *
   * @param ra1 Right ascension of moon (or sun) in rad
   * @param dec1 Declination of moon (or sun) in rad
   * @param ra2 Right ascension of sun (or moon) in rad
   * @param dec2 Declination of sun (or moon) in rad
   */
  public static double illuminatedFraction(double ra1, double dec1, double ra2, double dec2) {
    double cospsi =
        Math.sin(dec1) * Math.sin(dec2) + Math.cos(dec1) * Math.cos(dec2) * Math.cos(ra1 - ra2);
    return (1 - cospsi) / 2;
  }

  /**
   * Returns the position angle of the moon's bright limb in radians, measured from the north point
   * of the disk towards the east.
   *
   * @param ram Right ascension of moon in rad
   * @param decm Declination of moon in rad
   * @param ras Right ascension of sun in rad
   * @param decs Declination of sun in rad
   */
  public static double brightLimbAngle(double ram, double decm, double ras, double decs) {
    double num = Math.cos(decs) * Math.sin(ras - ram);
    double den =
        Math.sin(decs) * Math.cos(decm) - Math.cos(decs) * Math.sin(decm) * Math.cos(ras - ram);
    return Math.atan2(num, den);
  }

  // @formatter:off
  private static final int lr[] = {
    0,  0,  1,  0, 6288774, -20905355,
    2,  0, -1,  0, 1274027,  -3699111,
    2,  0,  0,  0,  658314,  -2955968,
    0,  0,  2,  0,  213618,   -569925,
    0,  1,  0,  0, -185116,     48888,
    0,  0,  0,  2, -114332,     -3149,
    2,  0, -2,  0,   58793,    246158,
    2, -1, -1,  0,   57066,   -152138,
    2,  0,  1,  0,   53322,   -170733,
    2, -1,  0,  0,   45758,   -204586,
    0,  1, -1,  0,  -40923,   -129620,
    1,  0,  0,  0,  -34720,    108743,
    0,  1,  1,  0,  -30383,    104755,
    2,  0,  0, -2,   15327,     10321,
    0,  0,  1,  2,  -12528,         0,
    0,  0,  1, -2,   10980,     79661,
    4,  0, -1,  0,   10675,    -34782,
    0,  0,  3,  0,   10034,    -23210,
    4,  0, -2,  0,    8548,    -21636,
    2,  1, -1,  0,   -7888,     24208,
    2,  1,  0,  0,   -6766,     30824,
    1,  0, -1,  0,   -5163,     -8379,
    1,  1,  0,  0,    4987,    -16675,
    2, -1,  1,  0,    4036,    -12831,
    2,  0,  2,  0,    3994,    -10445,
    4,  0,  0,  0,    3861,    -11650,
    2,  0, -3,  0,    3665,     14403,
    0,  1, -2,  0,   -2689,     -7003,
    2,  0, -1,  2,   -2602,         0,
    2, -1, -2,  0,    2390,     10056,
    1,  0,  1,  0,   -2348,      6322,
    2, -2,  0,  0,    2236,     -9884,
    0,  1,  2,  0,   -2120,      5751,
    0,  2,  0,  0,   -2069,         0,
    2, -2, -1,  0,    2048,     -4950,
    2,  0,  1, -2,   -1773,      4130,
    2,  0,  0,  2,   -1595,         0,
    4, -1, -1,  0,    1215,     -3958,
    0,  0,  2,  2,   -1110,         0,
    3,  0, -1,  0,    -892,      3258,
    2,  1,  1,  0,    -810,      2616,
    4, -1, -2,  0,     759,     -1897,
    0,  2, -1,  0,    -713,     -2117,
    2,  2, -1,  0,    -700,      2354,
    2,  1, -2,  0,     691,         0,
    2, -1,  0, -2,     596,         0,
    4,  0,  1,  0,     549,     -1423,
    0,  0,  4,  0,     537,     -1117,
    4, -1,  0,  0,     520,     -1571,
    1,  0, -2,  0,    -487,     -1739,
    2,  1,  0, -2,    -399,         0,
    0,  0,  2, -2,    -381,     -4421,
    1,  1,  1,  0,     351,         0,
    3,  0, -2,  0,    -340,         0,
    4,  0, -3,  0,     330,         0,
    2, -1,  2,  0,     327,         0,
    0,  2,  1,  0,    -323,      1165,
    1,  1, -1,  0,     299,         0,
    2,  0,  3,  0,     294,         0,
    2,  0, -1, -2,       0,      8752
  };
  private static final int b[] = {
    0,  0,  0,  1, 5128122,
    0,  0,  1,  1,  280602,
    0,  0,  1, -1,  277693,
    2,  0,  0, -1,  173237,
    2,  0, -1,  1,   55413,
    2,  0, -1, -1,   46271,
    2,  0,  0,  1,   32573,
    0,  0,  2,  1,   17198,
    2,  0,  1, -1,    9266,
    0,  0,  2, -1,    8822,
    2, -1,  0, -1,    8216,
    2,  0, -2, -1,    4324,
    2,  0,  1,  1,    4200,
    2,  1,  0, -1,   -3359,
    2, -1, -1,  1,    2463,
    2, -1,  0,  1,    2211,
    2, -1, -1, -1,    2065,
    0,  1, -1, -1,   -1870,
    4,  0, -1, -1,    1828,
    0,  1,  0,  1,   -1794,
    0,  0,  0,  3,   -1749,
    0,  1, -1,  1,   -1565,
    1,  0,  0,  1,   -1491,
    0,  1,  1,  1,   -1475,
    0,  1,  1, -1,   -1410,
    0,  1,  0, -1,   -1344,
    1,  0,  0, -1,   -1335,
    0,  0,  3,  1,    1107,
    4,  0,  0, -1,    1021,
    4,  0, -1,  1,     833,
    0,  0,  1, -3,     777,
    4,  0, -2,  1,     671,
    2,  0,  0, -3,     607,
    2,  0,  2, -1,     596,
    2, -1,  1, -1,     491,
    2,  0, -2,  1,    -451,
    0,  0,  3, -1,     439,
    2,  0,  2,  1,     422,
    2,  0, -3, -1,     421,
    2,  1, -1,  1,    -366,
    2,  1,  0,  1,    -351,
    4,  0,  0,  1,     331,
    2, -1,  1,  1,     315,
    2, -2,  0, -1,     302,
    0,  0,  1,  3,    -283,
    2,  1,  1, -1,    -229,
    1,  1,  0, -1,     223,
    1,  1,  0,  1,     223,
    0,  1, -2, -1,    -220,
    2,  1, -1, -1,    -220,
    1,  0,  1,  1,    -185,
    2, -1, -2, -1,     181,
    0,  1,  2,  1,    -177,
    4,  0, -2, -1,     176,
    4, -1, -1, -1,     166,
    1,  0,  1, -1,    -164,
    4,  0,  1, -1,     132,
    1,  0, -1, -1,    -119,
    4, -1,  0, -1,     115,
    2, -2,  0,  1,     107
  };
  // @formatter:on
}
