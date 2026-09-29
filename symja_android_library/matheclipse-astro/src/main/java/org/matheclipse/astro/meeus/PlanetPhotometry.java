/*
 * PlanetPhotometry.java  -  Planet magnitudes and angular sizes
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
 * Modified 2026 for Symja: tellPlanetMag and tellAngularSize of com.nvastro.nvj.NearSkyDB (Night
 * Vision 5.5) ported to package org.matheclipse.astro.meeus. They return numbers instead of
 * formatted strings, and Saturn's ring tilt takes the planet's ecliptical coordinates as
 * arguments instead of reading them from the chart.
 */

package org.matheclipse.astro.meeus;

/**
 * Planet magnitudes (P.283-286) and angular sizes (Chapter 55) from "Astronomical Algorithms" 2nd
 * Ed. by Jean Meeus.
 *
 * @author Brian Simpson
 */
public final class PlanetPhotometry {

  private static final double D2R = Math.PI / 180;
  /* A factor used for generating planet magnitudes */
  private static final double MAGFCTR = 5 / Math.log(10.0);

  private PlanetPhotometry() {}

  /**
   * The phase angle: the angle Sun - planet - Earth.
   *
   * @param sdist Distance from sun
   * @param edist Distance from earth
   * @param earthSunDist Distance from the earth to the sun
   * @return the phase angle in degrees
   */
  public static double phaseAngle(double sdist, double edist, double earthSunDist) {
    double sedist = sdist * edist;
    double i = (sdist * sdist + edist * edist - earthSunDist * earthSunDist) / (2 * sedist);
    i = Math.max(-1.0, Math.min(i, 1.0)); // Make sure -1 <= i <= 1
    return Math.acos(i) / D2R; // Degrees
  }

  /**
   * Returns planet magnitude. (P.283-286)
   *
   * @param object a planet, Mercury to Pluto
   * @param edist Distance from earth
   * @param sdist Distance from sun
   * @param earthSunDist Distance from the earth to the sun
   * @param jde Julian ephemeris day (For Saturn only)
   * @param lambda geocentric ecliptical longitude of the planet in radians (For Saturn only)
   * @param beta geocentric ecliptical latitude of the planet in radians (For Saturn only)
   * @return the visual magnitude
   */
  public static double magnitude(MeeusBody object, double edist, double sdist,
      double earthSunDist, double jde, double lambda, double beta) {
    double sedist = sdist * edist;
    double i = phaseAngle(sdist, edist, earthSunDist);

    double mag = MAGFCTR * Math.log(sedist);
    switch (object) {
      case MERCURY:
        return mag + -0.42 + i * (0.0380 - i * (0.000273 - i * 0.000002));
      case VENUS:
        return mag + -4.40 + i * (0.0009 + i * (0.000239 - i * 0.00000065));
      case MARS:
        return mag + -1.52 + i * 0.016;
      case JUPITER:
        return mag + -9.40 + i * 0.005;
      case SATURN: {
        mag += -8.88;

        // See Chap. 45 - Ring of Saturn
        double T = (jde - 2451545.0) / 36525; // Jul. cent.
        double I = 28.075216 - T * (0.012998 - T * 0.000004); // Ring incl.
        double Omega = 169.508470 + T * (1.394681 + T * 0.000412); // Asc. node

        // B is the Saturnicentric latitude of the Earth (sinb = sin(B))
        double sinB = Math.sin(I * D2R) * Math.cos(beta) * Math.sin(lambda - Omega * D2R)
            - Math.cos(I * D2R) * Math.sin(beta);
        // Since B is well within +/- 90 degrees, we can assume that
        // sin|B| will always be positive. Ensure sinB is positive:
        sinB = Math.abs(sinB);

        // Using formula on P. 286, with i as an approx. of deltaU
        return mag + 0.044 * Math.abs(i) - sinB * (2.60 - sinB * 1.25);
      }
      case URANUS:
        return mag + -7.19;
      case NEPTUNE:
        return mag + -6.87;
      case PLUTO:
        return mag + -1.00;
      default:
        throw new IllegalArgumentException("not a planet: " + object);
    }
  }

  /**
   * Returns angular size. (Chapter 55)
   *
   * @param object Near sky object
   * @param dist Distance from earth in AUs
   * @return the angular diameter in arc seconds
   */
  public static double angularDiameter(MeeusBody object, double dist) {
    double s;

    switch (object) {
      case MERCURY:
        s = 3.36;
        break;
      case VENUS:
        s = 8.41; // Includes clouds; use 8.34 for crust
        break;
      case MARS:
        s = 4.68;
        break;
      case JUPITER:
        s = 98.44;
        break;
      case SATURN:
        s = 82.73;
        break;
      case URANUS:
        s = 35.02;
        break;
      case NEPTUNE:
        s = 33.50;
        break;
      case PLUTO:
        s = 2.07;
        break;
      case SUN:
        s = 959.63;
        break;
      case MOON:
        s = 358473400 / MeeusEphemeris.AU_KM; // P. 391
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(object));
    }
    return 2 * s / dist;
  }
}
