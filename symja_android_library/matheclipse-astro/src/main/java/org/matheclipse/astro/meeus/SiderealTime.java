/*
 * SiderealTime.java  -  Sidereal time and horizontal coordinates
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
 * Modified 2026 for Symja: getLSTHrs and getJulianEphDay of com.nvastro.nvj.LST, and the LST and
 * latitude matrices with rd2aa and aa2rd of com.nvastro.nvj.Rotation (Night Vision 5.5), ported
 * to package org.matheclipse.astro.meeus as static functions of an explicit Julian day. The
 * running clock, time zones and Swing controls of LST were left out.
 */

package org.matheclipse.astro.meeus;

/**
 * Sidereal time, and conversion between apparent right ascension and declination and azimuth and
 * altitude.
 *
 * @author Brian Simpson
 */
public final class SiderealTime {

  private static final double HalfPI = Math.PI / 2;
  private static final double TwoPI = Math.PI * 2;

  private SiderealTime() {}

  /**
   * Returns the Julian Ephemeris Day.
   *
   * @param jd Julian day (UT)
   */
  public static double julianEphemerisDay(double jd) {
    // Add delta t (= TT - UT)
    return jd + DeltaT.seconds(jd) / 86400;
  }

  /**
   * Returns Greenwich mean sidereal time in hours.
   *
   * @param jd Julian day (UT)
   */
  public static double greenwichHours(double jd) {
    /* Separate jd into jd0, the Julian date at 0hr UTC, */
    /* and ut, the UTC hours of that day. */
    /* (At 0hr UTC, jd0 must end with .5) */
    double jd0 = Math.floor(jd - 0.5) + 0.5;
    double ut = jd - jd0;
    ut *= 24;

    /* Calculate Greenwich mean sidereal time (gst) using */
    /* algorithm from Practical Astronomy book (modified) */
    double t = (jd0 - 2451545.0) / 36525.0;
    double gst = 6.697374558 + t * (2400.0513369072 + t * (.0000258622 + t / 580650000));
    gst = gst % 24.0; // OK if < 0
    gst += ut * 1.00273790935;
    while (gst >= 24.0)
      gst -= 24.0;
    while (gst < 0.0)
      gst += 24.0;
    return gst;
  }

  /**
   * Returns local sidereal time in hours.
   *
   * @param jd Julian day (UT)
   * @param longitudeDeg east longitude in degrees
   */
  public static double localHours(double jd, double longitudeDeg) {
    /* Now adjust for longitude */
    double lst = greenwichHours(jd) + longitudeDeg / 15.0;
    while (lst >= 24.0)
      lst -= 24.0;
    while (lst < 0.0)
      lst += 24.0;
    return lst;
  }

  /* Latitude * LST matrix of com.nvastro.nvj.Rotation */
  private static Mat3 latLst(double lstHrs, double latDeg) {
    /* Set up rotation for LST (rotation about z axis with x axis moving */
    /* in the direction of the y axis as LST increases). */
    lstHrs *= Math.PI / 12;
    double cos = Math.cos(lstHrs);
    double sin = Math.sin(lstHrs);
    Mat3 lst = new Mat3(cos, sin, 0.0, -sin, cos, 0.0, 0.0, 0.0, 1.0);
    /* Set up rotation for latitude (rotation about y axis with z axis */
    /* moving in the direction of the x axis as latitude decreases from */
    /* 90 degrees; z axis is initially aligned with the north pole). */
    latDeg = (90.0 - latDeg) * Math.PI / 180.0;
    cos = Math.cos(latDeg);
    sin = Math.sin(latDeg);
    Mat3 lat = new Mat3(cos, 0.0, -sin, 0.0, 1.0, 0.0, sin, 0.0, cos);
    return lst.premult(lat);
  }

  /**
   * Converts apparent RA/Dec to Az/Alt.
   *
   * @param ra apparent right ascension in radians
   * @param dec apparent declination in radians
   * @param lstHrs local sidereal time in hours
   * @param latDeg latitude in degrees
   * @return <code>{azimuth, altitude}</code> in radians, azimuth from north through east in 0..2pi
   */
  public static double[] toHorizontal(double ra, double dec, double lstHrs, double latDeg) {
    double az, alt;

    double[] m = latLst(lstHrs, latDeg).mult(Mat3.unit(ra, dec));
    if (m[2] > 1)
      alt = HalfPI;
    else if (m[2] < -1)
      alt = -HalfPI;
    else
      alt = Math.asin(m[2]);
    if (m[0] == 0 && m[1] == 0)
      az = 0.0;
    else
      az = Math.atan2(m[1], -m[0]); /* az between -pi and pi */
    if (az < 0)
      az += TwoPI; /* az between 0 and 2 pi */
    return new double[] {az, alt};
  }

  /**
   * Converts Az/Alt to apparent RA/Dec.
   *
   * @param az azimuth in radians
   * @param alt altitude in radians
   * @param lstHrs local sidereal time in hours
   * @param latDeg latitude in degrees
   * @return apparent <code>{ra, dec}</code> in radians, ra in 0..2pi
   */
  public static double[] toEquatorial(double az, double alt, double lstHrs, double latDeg) {
    double ra, dec;

    double[] m = latLst(lstHrs, latDeg).invert().mult(Mat3.unit(Math.PI - az, alt));
    if (m[2] > 1)
      dec = HalfPI;
    else if (m[2] < -1)
      dec = -HalfPI;
    else
      dec = Math.asin(m[2]);
    if (m[0] == 0 && m[1] == 0)
      ra = 0.0;
    else
      ra = Math.atan2(m[1], m[0]); /* ra between -pi and pi */
    if (ra < 0)
      ra += TwoPI; /* ra between 0 and 2 pi */
    return new double[] {ra, dec};
  }
}
