/*
 * DeltaT.java  -  Determines delta t (= TT - UT)
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
 * Modified 2026 for Symja: ported from com.nvastro.nvj.DeltaT (Night Vision 5.5) to package
 * org.matheclipse.astro.meeus; the Preferences.usedeltat switch was removed.
 */

package org.matheclipse.astro.meeus;

/**
 * Determines delta t (= TT - UT).
 *
 * <p>
 * Methods from "Astronomical Algorithms" 2nd Ed. by Jean Meeus (c) 1998, second printing March
 * 2000 by Willmann-Bell, Inc.
 *
 * <p>
 * The deltat table (below) is from https://webspace.science.uu.nl/~gent0113/deltat/deltat_modern.htm
 * This table agrees very closely with table from Jean Meeus' Astronomical Algorithms (2nd ed. 1998,
 * P. 79) in 1900s, and diverges to only 3 seconds at 1620 (most likely due to slightly different
 * lunar acceleration).
 *
 * <p>
 * Bessel Interpolation is used for intermediate values.
 *
 * <p>
 * Values before 948 use formula from Jean Meeus' book (P. 78). Small change done to constant term
 * to get exact match to succeeding formula at 948.
 *
 * <p>
 * Values beteen 948-1620 use a formula from JPL Horizons (according to above website, and derived
 * from Stephenson &amp; Houlden (1986)), since it closely matches table value at 1620. (Small
 * adjustment is made between years 1600 - 1620 to get an exact match at 1620.) (Formula in Meeus'
 * book diverged by over 40 seconds at 1620.)
 *
 * <p>
 * Values after 2024 use formula from Jean Meeus' book (P. 78). Constant term reduced for smooth
 * transition at 2024.
 *
 * <p>
 * The algorithm used is Bessel's Interpolation Formula, using up to the 4th difference. This allows
 * calculation of values between two points using the influence of 6 evenly spaced points, (2 on
 * either side of the original 2).
 *
 * @author Brian Simpson
 */
public final class DeltaT {

  private DeltaT() {}

  /**
   * Calculates delta t
   *
   * @param julian Julian date
   * @return DeltaT in seconds
   */
  public static double seconds(double julian) {
    // Look at Meeus book and above formulas for methodology
    double u, d;
    double Y = 2000 + (julian - 2451545) / 365.25;
    // Y is derived from the Julian day and represents the calendar year.
    // Won't worry about slight mismatch between Julian year and calendar year
    // since it's only 3 days every 4 centuries, and DeltaT will not change
    // appreciably over 3 days...

    if (Y >= YSTOP) {
      u = (Y - 2000) / 100.0;
      // Constant term reduced for smooth transition at YSTOP
      d = 43.2427 + u * (102.0 + u * 25.3);
    } else if (Y < 948) {
      u = (Y - 2000) / 100.0;
      // 2178.45936 (instead of 2177) to get smooth transition
      d = 2178.45936 + u * (497 + u * 44.1);
    } else if (Y < YSTART) {
      u = (Y - 2000) / 100.0;
      d = 50.6 + u * (67.5 + u * 22.5);

      // Allow 20 years to achieve a smooth transition at YSTART
      if (Y > YSTART - 20) {
        u = (YSTART - 2000) / 100.0;
        d += ((Y - YSTART + 20) / 20) * (dt[0] / 100.0 - (50.6 + u * (67.5 + u * 22.5)));
      }
    } else { // YSTART <= Y < YSTOP
      int x = (int) Y - YSTART; // (always >= 0)
      u = Y - (int) Y; // 0 <= u < 1 (fractional year)
      d = dt[x] + u * (d1[x] + u * (d2[x] + u * (d3[x] + u * d4[x])));
      d *= 0.01; // Convert centiseconds to seconds
    }

    return d;
  }

  private static final int dt[] = { // Values are in centiseconds
      // 1620 - 1699
      12400, 11900, 11500, 11000, 10600, 10200, 9800, 9500, 9100, 8800, //
      8500, 8200, 7900, 7700, 7400, 7200, 7000, 6700, 6500, 6300, //
      6200, 6000, 5800, 5700, 5500, 5400, 5300, 5100, 5000, 4900, //
      4800, 4700, 4600, 4500, 4400, 4300, 4200, 4100, 4000, 3800, //
      3700, 3600, 3500, 3400, 3300, 3200, 3100, 3000, 2800, 2700, //
      2600, 2500, 2400, 2300, 2200, 2100, 2000, 1900, 1800, 1700, //
      1600, 1500, 1400, 1400, 1300, 1200, 1200, 1100, 1100, 1000, //
      1000, 1000, 900, 900, 900, 900, 900, 900, 900, 900, //
      // 1700 - 1799
      900, 900, 900, 900, 900, 900, 900, 900, 1000, 1000, //
      1000, 1000, 1000, 1000, 1000, 1000, 1000, 1100, 1100, 1100, //
      1100, 1100, 1100, 1100, 1100, 1100, 1100, 1100, 1100, 1100, //
      1100, 1100, 1100, 1100, 1200, 1200, 1200, 1200, 1200, 1200, //
      1200, 1200, 1200, 1200, 1300, 1300, 1300, 1300, 1300, 1300, //
      1300, 1400, 1400, 1400, 1400, 1400, 1400, 1400, 1500, 1500, //
      1500, 1500, 1500, 1500, 1500, 1600, 1600, 1600, 1600, 1600, //
      1600, 1600, 1600, 1600, 1600, 1700, 1700, 1700, 1700, 1700, //
      1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, //
      1700, 1700, 1600, 1600, 1600, 1600, 1500, 1500, 1400, 1400, //
      // 1800 - 1899
      1370, 1340, 1310, 1290, 1270, 1260, 1250, 1250, 1250, 1250, //
      1250, 1250, 1250, 1250, 1250, 1250, 1250, 1240, 1230, 1220, //
      1200, 1170, 1140, 1110, 1060, 1020, 960, 910, 860, 800, //
      750, 700, 660, 630, 600, 580, 570, 560, 560, 560, //
      570, 580, 590, 610, 620, 630, 650, 660, 680, 690, //
      710, 720, 730, 740, 750, 760, 770, 770, 780, 780, //
      788, 782, 754, 697, 640, 602, 541, 410, 292, 182, //
      161, 10, -102, -128, -269, -324, -364, -454, -471, -511, //
      -540, -542, -520, -546, -546, -579, -563, -564, -580, -566, //
      -587, -601, -619, -664, -644, -647, -609, -576, -466, -374, //
      // 1900 - 1999
      -272, -154, -2, 124, 264, 386, 537, 614, 775, 913, //
      1046, 1153, 1336, 1465, 1601, 1720, 1824, 1906, 2025, 2095, //
      2116, 2225, 2241, 2303, 2349, 2362, 2386, 2449, 2434, 2408, //
      2402, 2400, 2387, 2395, 2386, 2393, 2373, 2392, 2396, 2402, //
      2433, 2483, 2530, 2570, 2624, 2677, 2728, 2778, 2825, 2871, //
      2915, 2957, 2997, 3036, 3072, 3107, 3135, 3168, 3218, 3268, //
      3315, 3359, 3400, 3447, 3503, 3573, 3654, 3743, 3829, 3920, //
      4018, 4117, 4223, 4337, 4449, 4548, 4646, 4752, 4853, 4959, //
      5054, 5138, 5217, 5296, 5379, 5434, 5487, 5532, 5582, 5630, //
      5686, 5757, 5831, 5912, 5998, 6078, 6163, 6229, 6297, 6347, //
      // 2000 - 2024
      6383, 6409, 6430, 6447, 6457, 6469, 6485, 6515, 6546, 6578, //
      6607, 6632, 6660, 6691, 6728, 6764, 6810, 6859, 6897, 6922, //
      6936, 6936, 6929, 6920, 6918};
  private static final int dtlength = dt.length;
  private static final int YSTART = 1620;
  private static final int YSTOP = YSTART + dtlength - 1; // (2024)
  private static final double[] d1 = new double[dtlength - 1];
  private static final double[] d2 = new double[dtlength - 1];
  private static final double[] d3 = new double[dtlength - 1];
  private static final double[] d4 = new double[dtlength - 1];

  static {
    int i;

    for (i = 0; i < dtlength - 1; i++) // 1st differences
      d1[i] = dt[i + 1] - dt[i];
    for (i = 0; i < dtlength - 2; i++) // 2nd differences
      d2[i] = d1[i + 1] - d1[i];
    for (i = 0; i < dtlength - 3; i++) // 3rd differences
      d3[i] = d2[i + 1] - d2[i];
    for (i = 0; i < dtlength - 4; i++) // 4th differences
      d4[i] = d3[i + 1] - d3[i];

    // Set up linear interpolation at end
    d2[dtlength - 2] = d3[dtlength - 2] = d4[dtlength - 2] = 0;

    // Set up quadratic interpolation next to end
    d2[dtlength - 3] = (d2[dtlength - 3] + d2[dtlength - 4]) / 4;
    d1[dtlength - 3] -= d2[dtlength - 3];
    d4[dtlength - 3] = d3[dtlength - 3] = 0;

    // Set up 4th order interpolation in middle
    for (i = dtlength - 4; i >= 2; i--) { // Polynomial coefficients
      d4[i] = d4[i - 1] + d4[i - 2];
      d3[i] = d3[i - 1];
      d2[i] = d2[i] + d2[i - 1];
      d1[i] = d1[i] - d2[i] / 4 + d3[i] / 12 + d4[i] / 24; // Coefficient of u
      d2[i] = d2[i] / 4 - d3[i] / 4 - d4[i] / 48; // Coefficient of u^2
      d3[i] = d3[i] / 6 - d4[i] / 24; // Coefficient of u^3
      d4[i] = d4[i] / 48; // Coefficient of u^4
    }

    // Set up quadratic interpolation next to beginning
    d2[1] = (d2[1] + d2[0]) / 4;
    d1[1] -= d2[1];
    d4[1] = d3[1] = 0;

    // Set up linear interpolation at beginning
    d2[0] = d3[0] = d4[0] = 0;
  }
}
