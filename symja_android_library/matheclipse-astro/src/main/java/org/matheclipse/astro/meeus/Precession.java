/*
 * Precession.java  -  Precession and nutation rotations
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
 * Modified 2026 for Symja: the precession part of com.nvastro.nvj.Rotation (Night Vision 5.5)
 * ported to package org.matheclipse.astro.meeus. The view, azimuth, altitude and field rotation
 * matrices of the chart were left out; the class is immutable.
 */

package org.matheclipse.astro.meeus;

/**
 * The rotation from the mean equator and equinox of J2000.0 to the true equator and equinox of a
 * date: precession followed by nutation.
 *
 * <p>
 * Precession uses the constants from "Astronomical Algorithms" P.134 and the matrix method from
 * "Practical Astronomy with your Calculator".
 *
 * @author Brian Simpson
 */
public final class Precession {

  private static final double S2R = Math.PI / 648000; // Seconds to radians

  private final Nutation nutation;
  private final Mat3 ntpr; // Matrix for NutationPrecession
  private final Mat3 unpn; // Matrix for inverse NtPr

  /**
   * Sets up the precession and nutation matrices.
   *
   * @param jday Julian Ephemeris Day
   */
  public static Precession at(double jday) {
    return new Precession(jday);
  }

  private Precession(double jday) {
    nutation = Nutation.at(jday); // Sets nutation
    double t = (jday - Nutation.J2000_0) / 36525;
    // Using constants from "Astronomical Algorithms" P.134,
    // and matrix method from "Practical Astronomy with your Calculator"
    double zeta = ((0.017998 * t + 0.30188) * t + 2306.2181) * t;
    double zzzz = ((0.018203 * t + 1.09468) * t + 2306.2181) * t;
    double theta = ((0.041833 * t + 0.42665) * t + 2004.3109) * t;
    zeta *= S2R; // Seconds to radians
    zzzz *= S2R; // Seconds to radians
    theta *= S2R; // Seconds to radians
    double cx = Math.cos(zeta);
    double sx = Math.sin(zeta);
    double cz = Math.cos(zzzz);
    double sz = Math.sin(zzzz);
    double ct = Math.cos(theta);
    double st = Math.sin(theta);

    Mat3 prec = new Mat3(cx * ct * cz - sx * sz, -(sx * ct * cz + cx * sz), -st * cz, //
        cx * ct * sz + sx * cz, cx * cz - sx * ct * sz, -st * sz, //
        cx * st, -sx * st, ct);
    ntpr = prec.premult(nutation.matrix());
    unpn = ntpr.invert();
  }

  /** The nutation of the same date. */
  public Nutation nutation() {
    return nutation;
  }

  /**
   * Precesses and Nutates the coordinates.
   *
   * @param ra J2000 right ascension in radians
   * @param dec J2000 declination in radians
   * @return <code>{ra, dec}</code> of date in radians
   */
  public double[] precessNutate(double ra, double dec) {
    return ntpr.rotateRADec(ra, dec);
  }

  /**
   * Unprecesses and Unnutates the coordinates.
   *
   * @param ra Right ascension of date in radians
   * @param dec Declination of date in radians
   * @return J2000 <code>{ra, dec}</code> in radians
   */
  public double[] unPrecessNutate(double ra, double dec) {
    return unpn.rotateRADec(ra, dec);
  }
}
