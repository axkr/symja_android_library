/*
 * Vsop87.java  -  Heliocentric planetary coordinates
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
 * Modified 2026 for Symja: calcHelioCentricCoord of the Planet class in com.nvastro.nvj.NearSkyDB
 * (Night Vision 5.5) ported to package org.matheclipse.astro.meeus as a static function over the
 * flattened tables of the Vsop87* classes; the planet names were dropped.
 */

package org.matheclipse.astro.meeus;

/**
 * Heliocentric coordinates from the abridged VSOP87 series printed in "Astronomical Algorithms"
 * 2nd Ed. by Jean Meeus, Appendix III.
 *
 * @author Brian Simpson
 */
final class Vsop87 {
  private static final double PI2 = Math.PI * 2;

  private Vsop87() {}

  /** The table of a planet; Pluto has none. */
  static double[][][] terms(MeeusBody body) {
    switch (body) {
      case MERCURY:
        return Vsop87Mercury.TERMS;
      case VENUS:
        return Vsop87Venus.TERMS;
      case MARS:
        return Vsop87Mars.TERMS;
      case JUPITER:
        return Vsop87Jupiter.TERMS;
      case SATURN:
        return Vsop87Saturn.TERMS;
      case URANUS:
        return Vsop87Uranus.TERMS;
      case NEPTUNE:
        return Vsop87Neptune.TERMS;
      default:
        throw new IllegalArgumentException("no VSOP87 series for " + body);
    }
  }

  /**
   * Calculates heliocentric coordinates.
   *
   * @param data one of the <code>TERMS</code> tables
   * @param t Julian millennia from J2000.0
   * @return <code>{l, b, r}</code>: heliocentric ecliptical longitude in radians (0..2pi), latitude
   *         in radians and radius vector in AU
   */
  static double[] heliocentric(double[][][] data, double t) {
    double[] lbr = new double[3];
    for (int i = 0; i < 3; i++) {
      double sum = 0;
      for (int j = data[i].length - 1; j >= 0; j--) {
        sum *= t;
        double[] series = data[i][j];
        for (int k = series.length - 3; k >= 0; k -= 3)
          sum += series[k] * Math.cos(series[k + 1] + series[k + 2] * t);
        // Note: Tried %'ing the arg of cos with PI2, with jul = 2996000.0
        // (~ 3490AD), and difference to final RA/Dec was in 14th dec. place
        // -> Perhaps can get rid of most/all of %180 and %PI2
      }
      sum /= 1e8;
      if (i == 0) {
        sum = sum % PI2; // 2 % 3 = 2, (-2) % 3 = -2
        if (sum < 0)
          sum += PI2;
      }
      lbr[i] = sum;
    }
    return lbr;
  }
}
