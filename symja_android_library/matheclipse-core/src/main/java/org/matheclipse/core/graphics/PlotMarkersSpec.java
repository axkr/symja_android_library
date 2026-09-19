package org.matheclipse.core.graphics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A {@code PlotMarkers} option, read once and then asked for the marker of each dataset.
 *
 * <p>
 * The settings are the ones the reference implementation documents:
 *
 * <table>
 * <caption>the accepted forms</caption>
 * <tr>
 * <td><code>None</code></td>
 * <td>no markers - there is no spec at all, and {@link #of(IExpr)} answers <code>null</code></td>
 * </tr>
 * <tr>
 * <td><code>Automatic</code></td>
 * <td>the standard sequence, one shape per dataset, cycling</td>
 * </tr>
 * <tr>
 * <td><code>"OpenMarkers"</code></td>
 * <td>the same sequence unfilled</td>
 * </tr>
 * <tr>
 * <td><code>{Automatic, s}</code></td>
 * <td>the standard sequence at size <code>s</code></td>
 * </tr>
 * <tr>
 * <td><code>g</code></td>
 * <td>a copy of <code>g</code> at every point of every dataset</td>
 * </tr>
 * <tr>
 * <td><code>{g, s}</code></td>
 * <td><code>g</code> at size <code>s</code></td>
 * </tr>
 * <tr>
 * <td><code>{g1, g2, ...}</code></td>
 * <td><code>gi</code> for dataset <code>i</code>, cycling</td>
 * </tr>
 * <tr>
 * <td><code>{{g1,s1}, {g2,s2}, ...}</code></td>
 * <td>the same, each at its own size</td>
 * </tr>
 * </table>
 *
 * <p>
 * A marker is drawn as a {@code Text}, which is what makes it the same size wherever the axes
 * happen to run and whatever the aspect ratio is: a glyph is measured in printer's points, while a
 * {@code Disk} of a fixed radius would be an ellipse on one plot and a speck on the next. It also
 * means a marker takes the colour of the curve it belongs to without being told, because a
 * {@code Text} with no colour of its own is drawn in whatever colour is in force.
 *
 * <p>
 * {@code {g, s}} and {@code {g1, g2}} are the same shape, and are told apart by what the second
 * element is: a size specification makes it the first, anything else the second. A size is a plain
 * number, one of {@code Tiny}, {@code Small}, {@code Medium} and {@code Large}, an
 * {@code Offset(d)} in printer's points, or a {@code Scaled(s)} fraction of the plot.
 */
public final class PlotMarkersSpec {

  /** The standard sequence: a disk, a square, a diamond, and the two triangles. */
  private static final String[] FILLED = {"●", "■", "◆", "▲", "▼"};

  /** The same shapes unfilled, which is what {@code "OpenMarkers"} asks for. */
  private static final String[] OPEN = {"○", "□", "◇", "△", "▽"};

  /** The size a marker is drawn at when nothing asked for one, in printer's points. */
  private static final double DEFAULT_SIZE = 9.0;

  /** The size of the whole plot a {@code Scaled} marker is measured against. */
  private static final double SCALED_REFERENCE = 360.0;

  /** The markers, one per dataset, cycling; {@code null} for the standard sequence. */
  private final IExpr[] markers;

  /** The size of each marker, matched to {@link #markers}, or one size for all of them. */
  private final double[] sizes;

  /** Whether the standard sequence should be drawn unfilled. */
  private final boolean open;

  private PlotMarkersSpec(IExpr[] markers, double[] sizes, boolean open) {
    this.markers = markers;
    this.sizes = sizes;
    this.open = open;
  }

  /**
   * Read a {@code PlotMarkers} value.
   *
   * @return the spec, or {@code null} when nothing should be drawn - which is the case for
   *         {@code None}, for a missing option, and for a value that names no marker
   */
  public static PlotMarkersSpec of(IExpr value) {
    if (value == null || !value.isPresent() || value.isNone()) {
      return null;
    }
    if (value.isAutomatic()) {
      return new PlotMarkersSpec(null, new double[] {DEFAULT_SIZE}, false);
    }
    if (value.isString() && value.toString().equals("OpenMarkers")) {
      return new PlotMarkersSpec(null, new double[] {DEFAULT_SIZE}, true);
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      if (list.argSize() == 0) {
        return null;
      }
      if (list.argSize() == 2 && isSize(list.arg2())) {
        // {g, s}: the second element is a size, so this is one marker and not two
        return single(list.arg1(), sizeOf(list.arg2(), DEFAULT_SIZE));
      }
      IExpr[] markers = new IExpr[list.argSize()];
      double[] sizes = new double[list.argSize()];
      boolean allAutomatic = true;
      for (int i = 0; i < markers.length; i++) {
        IExpr entry = list.get(i + 1);
        double size = DEFAULT_SIZE;
        if (entry.isList() && ((IAST) entry).argSize() == 2) {
          IAST pair = (IAST) entry;
          size = sizeOf(pair.arg2(), DEFAULT_SIZE);
          entry = pair.arg1();
        }
        markers[i] = entry.isAutomatic() ? F.NIL : entry;
        sizes[i] = size;
        allAutomatic &= markers[i].isNIL();
      }
      return allAutomatic ? new PlotMarkersSpec(null, sizes, false)
          : new PlotMarkersSpec(markers, sizes, false);
    }
    return single(value, DEFAULT_SIZE);
  }

  private static PlotMarkersSpec single(IExpr marker, double size) {
    if (marker.isAutomatic()) {
      return new PlotMarkersSpec(null, new double[] {size}, false);
    }
    if (marker.isString() && marker.toString().equals("OpenMarkers")) {
      return new PlotMarkersSpec(null, new double[] {size}, true);
    }
    return new PlotMarkersSpec(new IExpr[] {marker}, new double[] {size}, false);
  }

  /**
   * Whether an expression names a size, which is what separates {@code {g, s}} from a list of two
   * markers.
   */
  private static boolean isSize(IExpr expr) {
    return (expr.isNumber() && expr.evalfNaN() > 0.0) || expr == S.Tiny || expr == S.Small
        || expr == S.Medium || expr == S.Large || expr.isAST(S.Offset, 2)
        || expr.isAST(S.Scaled, 2) || expr.isAST(S.Scaled, 1);
  }

  /** A size in printer's points, or {@code defaultValue} when the expression is not a size. */
  private static double sizeOf(IExpr expr, double defaultValue) {
    if (expr.isNumber()) {
      double size = expr.evalfNaN();
      return Double.isFinite(size) && size > 0.0 ? size : defaultValue;
    }
    if (expr == S.Tiny) {
      return 4.0;
    }
    if (expr == S.Small) {
      return 6.0;
    }
    if (expr == S.Medium) {
      return DEFAULT_SIZE;
    }
    if (expr == S.Large) {
      return 14.0;
    }
    if (expr.isAST(S.Offset, 2)) {
      double size = ((IAST) expr).arg1().evalfNaN();
      return Double.isFinite(size) && size > 0.0 ? size : defaultValue;
    }
    if (expr.isAST(S.Scaled, 2) || expr.isAST(S.Scaled, 1)) {
      // a fraction of the plot, the way PointSize and Thickness read one
      double fraction = ((IAST) expr).arg1().evalfNaN();
      return Double.isFinite(fraction) && fraction > 0.0 ? fraction * SCALED_REFERENCE
          : defaultValue;
    }
    if (expr.isAutomatic()) {
      return DEFAULT_SIZE;
    }
    return defaultValue;
  }

  /**
   * The marker of one dataset, ready to be placed at a point.
   *
   * <p>
   * It carries its size but no colour, so that it comes out in whatever colour the curve is drawn
   * in.
   */
  public IExpr markerAt(int datasetIndex) {
    int index = Math.floorMod(datasetIndex, markers == null ? FILLED.length : markers.length);
    IExpr marker = markers == null ? F.stringx((open ? OPEN : FILLED)[index]) : markers[index];
    if (marker.isNIL()) {
      // an Automatic among explicit markers falls back to the standard shape for that position
      marker = F.stringx((open ? OPEN : FILLED)[Math.floorMod(datasetIndex, FILLED.length)]);
    }
    double size = sizes[Math.floorMod(datasetIndex, sizes.length)];
    return size == DEFAULT_SIZE ? marker
        : F.binaryAST2(S.Style, marker, F.binaryAST2(S.Rule, S.FontSize, F.num(size)));
  }
}
