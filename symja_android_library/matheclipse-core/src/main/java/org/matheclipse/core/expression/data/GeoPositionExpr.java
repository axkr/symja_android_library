package org.matheclipse.core.expression.data;

import java.util.Arrays;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.numerics.geodesy.GeodesicSolver;

/**
 * A position on the Earth, held as <code>{latitude, longitude, altitude}</code> where the angles
 * are in degrees and the altitude is in meters above the reference ellipsoid.
 *
 * <p>
 * The coordinates are canonicalized on construction so that
 * <code>-90 &lt;= latitude &lt;= +90</code> and <code>-180 &lt; longitude &lt;= +180</code>.
 *
 * <p>
 * Deliberately backed by a plain <code>double[]</code> rather than by a third party geodesy type,
 * so that <code>matheclipse-core</code> carries no geodesy library in its public data model and
 * downstream modules are free to convert this into whatever representation they need.
 */
public class GeoPositionExpr extends DataExpr<double[]> {

  private static final long serialVersionUID = -2913225354078252971L;

  /**
   * Create a position at the given latitude and longitude, at altitude zero.
   *
   * @param latitude latitude in degrees
   * @param longitude longitude in degrees
   */
  public static GeoPositionExpr newInstance(final double latitude, final double longitude) {
    return newInstance(latitude, longitude, 0.0);
  }

  /**
   * Create a position at the given latitude, longitude and altitude.
   *
   * @param latitude latitude in degrees
   * @param longitude longitude in degrees
   * @param altitude altitude in meters above the reference ellipsoid
   */
  public static GeoPositionExpr newInstance(final double latitude, final double longitude,
      final double altitude) {
    double[] canonical = GeodesicSolver.canonicalize(latitude, longitude);
    return new GeoPositionExpr(new double[] {canonical[0], canonical[1], altitude});
  }

  protected GeoPositionExpr(final double[] position) {
    super(S.GeoPosition, position);
  }

  /** @return latitude in degrees, in the range <code>[-90, 90]</code> */
  public double latitude() {
    return fData[0];
  }

  /** @return longitude in degrees, in the range <code>(-180, 180]</code> */
  public double longitude() {
    return fData[1];
  }

  /** @return altitude in meters above the reference ellipsoid */
  public double altitude() {
    return fData[2];
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof GeoPositionExpr) {
      return Arrays.equals(fData, ((GeoPositionExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + Arrays.hashCode(fData);
  }

  @Override
  public int hierarchy() {
    return GEOPOSITIONID;
  }

  /**
   * Order by longitude, then latitude, then altitude, so that sorting a list of positions is
   * deterministic.
   */
  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof GeoPositionExpr) {
      double[] other = ((GeoPositionExpr) expr).fData;
      int result = Double.compare(fData[1], other[1]);
      if (result != 0) {
        return result;
      }
      result = Double.compare(fData[0], other[0]);
      if (result != 0) {
        return result;
      }
      return Double.compare(fData[2], other[2]);
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return new GeoPositionExpr(fData.clone());
  }

  @Override
  public IAST fullForm() {
    return F.GeoPosition(F.List(F.num(fData[0]), F.num(fData[1]), F.num(fData[2])));
  }

  @Override
  public String toString() {
    return "GeoPosition({" + fData[0] + "," + fData[1] + "," + fData[2] + "})";
  }
}
