package org.matheclipse.astro.convert;

import org.matheclipse.core.data.Entities;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;

/**
 * An Earth satellite given by a NORAD two-line element set, propagated with SGP4/SDP4.
 *
 * <p>
 * Symja has no satellite catalogue, so a satellite is written as its element set: a list
 * <code>{line1, line2}</code>, or <code>{name, line1, line2}</code> in the three-line form of
 * CelesTrak, either bare or wrapped as <code>Entity("Satellite", {...})</code>.
 */
public final class SatelliteTle {

  /** The entity type of a satellite. */
  public static final String SATELLITE = "Satellite";

  /** The equatorial radius SGP4 is defined with (WGS72), in meters. */
  public static final double SGP4_EARTH_RADIUS = 6378135.0;

  /** The satellite's name: line 0 of a three-line set, else its catalogue number. */
  public final String name;

  public final TLE tle;

  private SatelliteTle(String name, TLE tle) {
    this.name = name;
    this.tle = tle;
  }

  /**
   * A fresh propagator. Orekit propagators keep state between calls, so one is created per
   * evaluation rather than shared.
   */
  public TLEPropagator propagator() {
    return TLEPropagator.selectExtrapolator(tle);
  }

  /** Whether {@code expr} has the shape of an element set, valid or not. */
  public static boolean isCandidate(IExpr expr) {
    IExpr lines = Entities.nameOf(expr, SATELLITE);
    if (!lines.isList() || lines.argSize() < 2 || lines.argSize() > 3) {
      return false;
    }
    return ((IAST) lines).forAll(x -> x.isString());
  }

  /**
   * Read an element set.
   *
   * @return the satellite, or <code>null</code> if {@code expr} is not an element set
   * @throws IllegalArgumentException with the reason, if the lines are malformed
   */
  public static SatelliteTle of(IExpr expr) {
    if (!isCandidate(expr)) {
      return null;
    }
    IAST lines = (IAST) Entities.nameOf(expr, SATELLITE);
    String name = null;
    int first = 1;
    if (lines.argSize() == 3) {
      name = lines.arg1().toString().trim();
      if (name.startsWith("0 ")) {
        // the CelesTrak "0 NAME" variant of line 0
        name = name.substring(2).trim();
      }
      first = 2;
    }
    String line1 = lines.get(first).toString().trim();
    String line2 = lines.get(first + 1).toString().trim();
    if (!TLE.isFormatOK(line1, line2)) {
      throw new IllegalArgumentException("wrong format or checksum");
    }
    TLE tle = new TLE(line1, line2);
    if (name == null || name.isEmpty()) {
      name = Integer.toString(tle.getSatelliteNumber());
    }
    return new SatelliteTle(name, tle);
  }

  /** Semi-major axis in meters, from the mean motion of the element set. */
  public double semiMajorAxis() {
    double n = tle.getMeanMotion();
    return Math.cbrt(TLEPropagator.getMU() / (n * n));
  }
}
