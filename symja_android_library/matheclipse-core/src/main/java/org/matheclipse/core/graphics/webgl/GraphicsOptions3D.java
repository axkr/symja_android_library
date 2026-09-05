package org.matheclipse.core.graphics.webgl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotRangePaddingSpec;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options of a {@code Graphics3D} expression, parsed into plain fields.
 *
 * <p>
 * Defaults follow the conventions for {@code Graphics3D} rather than whatever the renderer happened
 * to hardcode: the box is drawn, the axes are not, and the camera sits at
 * {@code ViewPoint -> {1.3, -2.4, 2}}. The plot builtins override the ones they care about by
 * emitting explicit rules, so a plot still comes out with axes.
 */
public final class GraphicsOptions3D {

  /** Default viewing position, in the box-normalised coordinate system. */
  public static final double[] DEFAULT_VIEW_POINT = {1.3, -2.4, 2.0};

  public final boolean[] axes = {false, false, false};

  /** {@code AxesLabel} entries, or {@code null} where none was given. */
  public final String[] axesLabel = {null, null, null};

  /** {@code AxesEdge} entries: a two element {@code {+-1, +-1}}, or {@code null} for automatic. */
  public final double[][] axesEdge = new double[3][];

  /** Per axis: false when {@code AxesEdge -> None} asks for that axis to be left out. */
  public final boolean[] axesEdgeNone = {false, false, false};

  public Color axesColor = null;
  public double axesThickness = 1.0;

  public boolean boxed = true;
  public Color boxColor = null;
  public double boxThickness = 1.0;

  /** {@code null} means {@code Automatic}, which keeps the data's own proportions. */
  public double[] boxRatios = null;

  public Color background = null;

  /** {@code {width, height}} in pixels. */
  public final double[] imageSize = {360, 360};

  /**
   * Height as a fraction of width, or {@code NaN} to leave the size alone.
   *
   * <p>
   * Applied only when the call did not give a height of its own, since an explicit size says the
   * same thing more directly.
   */
  public double aspectRatio = Double.NaN;

  /** Whether a height was given outright, in which case {@code AspectRatio} has nothing to do. */
  public boolean imageHeightGiven = false;

  /** Room kept inside the picture, in printer's points, as left, right, bottom, top. */
  public double[] imagePadding = null;

  /** Room kept outside the picture, in printer's points, as left, right, bottom, top. */
  public double[] imageMargins = null;

  /**
   * Half spaces the scene is cut down to, each as {@code {a, b, c, d}} of {@code ax+by+cz+d=0}.
   *
   * <p>
   * The side that is kept is the one where the expression is positive, which is the convention the
   * graphics hardware uses and so the one the interactive output follows.
   */
  public double[][] clipPlanes = null;

  /** How each clipping plane itself is drawn, or {@code null} to leave it invisible. */
  public IExpr[] clipPlanesStyle = null;

  /** The part of the display area the drawing fills, as {@code {{xmin,xmax},{ymin,ymax}}}. */
  public double[][] plotRegion = null;

  /** {@code PlotRangePadding}: the room left around the data, per axis and per side. */
  public PlotRangePaddingSpec plotRangePadding = PlotRangePaddingSpec.automatic(3);

  public String plotLabel = null;

  /**
   * Two dimensional content drawn over the finished picture, in scaled coordinates.
   *
   * <p>
   * Held as the expression it was given as, because what draws it is the two dimensional renderer
   * rather than anything in this package.
   */
  public IExpr epilog = null;

  /** The same, drawn underneath the picture instead of over it. */
  public IExpr prolog = null;

  /** Per axis {@code {min, max}}, or {@code null} where the range is left to the data. */
  public final double[][] plotRange = new double[3][];

  /** {@code Ticks -> None} suppresses tick marks and their labels. */
  public boolean showTicks = true;

  /** An explicit {@code Ticks} specification per axis, or {@code null} for automatic. */
  public final IExpr[] ticksSpec = new IExpr[3];

  public Color ticksColor = null;

  /** {@code FaceGrids}: grid lines drawn on the faces of the bounding box. */
  public boolean faceGrids = false;
  public Color faceGridsColor = null;

  public double[] viewPoint = DEFAULT_VIEW_POINT.clone();
  public double[] viewVertical = {0.0, 0.0, 1.0};
  public double[] viewCenter = null;

  /** Where the three axes cross, or {@code null} to let the renderer choose a corner. */
  public double[] axesOrigin = null;

  /**
   * An explicit camera, as the transformation and projection matrices of {@code ViewMatrix}.
   *
   * <p>
   * Each is sixteen numbers in row order. A point goes through the transformation first and the
   * projection second, and what comes out is divided by its own last component to land on the
   * picture. Giving them says outright what {@code ViewPoint} and the rest only describe, so they
   * are ignored while these are here.
   */
  public double[] viewTransform = null;

  public double[] viewProjectionMatrix = null;

  /** {@code ViewRange -> {near, far}}: the distances from the camera that are drawn. */
  public double[] viewRange = null;

  /** An explicit camera position in data coordinates, from {@code ViewVector}. */
  public double[] viewVector = null;

  /**
   * The style every primitive starts from, or {@code null} when there is none.
   *
   * <p>
   * {@code BaseStyle} is the style the contents inherit before any directive of their own, which
   * is what makes {@code BaseStyle -> Red} colour a whole graphic without touching its primitives.
   */
  public IExpr baseStyle = null;

  /** Field of view in degrees, or {@code NaN} to let the renderer fit the scene. */
  public double viewAngle = Double.NaN;

  public boolean orthographic = false;

  /** {@code SphericalRegion -> True} keeps the framing steady while the scene is rotated. */
  public boolean sphericalRegion = false;

  /** Per axis scaling function name, one of {@code Identity}, {@code Log}, {@code Reverse}. */
  public final String[] scaling = {"Identity", "Identity", "Identity"};

  public Color labelColor = null;
  public double labelFontSize = 12.0;
  public String labelFontFamily = "Arial, sans-serif";

  public IExpr lighting = null;

  public GraphicsOptions3D() {
    axesEdge[0] = null;
    axesEdge[1] = null;
    axesEdge[2] = null;
  }

  /** Read the option rules of {@code ast}, starting after the first argument. */
  public void parse(IAST ast) {
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (!arg.isRuleAST()) {
        continue;
      }
      IAST rule = (IAST) arg;
      IExpr key = rule.arg1();
      IExpr value = rule.arg2();
      if (!key.isBuiltInSymbol()) {
        continue;
      }
      switch (((IBuiltInSymbol) key).ordinal()) {
        case ID.Axes:
          parseAxes(value);
          break;
        case ID.AxesLabel:
          parseAxesLabel(value);
          break;
        case ID.AxesEdge:
          parseAxesEdge(value);
          break;
        case ID.AxesStyle: {
          Color c = firstColor(value);
          if (c != null) {
            axesColor = c;
          }
          double t = firstThickness(value);
          if (!Double.isNaN(t)) {
            axesThickness = t;
          }
          break;
        }
        case ID.Boxed:
          boxed = !value.isFalse();
          break;
        case ID.BoxStyle: {
          Color c = firstColor(value);
          if (c != null) {
            boxColor = c;
          }
          double t = firstThickness(value);
          if (!Double.isNaN(t)) {
            boxThickness = t;
          }
          break;
        }
        case ID.BoxRatios:
          parseBoxRatios(value);
          break;
        case ID.Background:
          background = ColorUtil.parse(value);
          break;
        case ID.ImageSize:
          parseImageSize(value);
          break;
        case ID.Epilog:
          epilog = value.isNone() ? null : value;
          break;
        case ID.Prolog:
          prolog = value.isNone() ? null : value;
          break;
        case ID.PlotLabel:
          if (!unlabelled(value)) {
            plotLabel = text(value);
          }
          break;
        case ID.PlotRange:
          parsePlotRange(value);
          break;
        case ID.Ticks:
          parseTicks(value);
          break;
        case ID.TicksStyle:
          ticksColor = firstColor(value);
          break;
        case ID.FaceGrids:
          faceGrids = !value.isNone() && !value.isFalse();
          faceGridsColor = firstColor(value);
          break;
        case ID.FaceGridsStyle: {
          // FaceGrids says where the grids go, FaceGridsStyle what they look like
          Color styled = firstColor(value);
          if (styled != null) {
            faceGridsColor = styled;
          }
          break;
        }
        case ID.AxesOrigin: {
          double[] origin = vector(value);
          if (origin != null) {
            axesOrigin = origin;
          }
          break;
        }
        case ID.ViewVector:
          parseViewVector(value);
          break;
        case ID.ViewRange: {
          double[] range = sides(value);
          if (range != null && range[1] > range[0]) {
            viewRange = range;
          }
          break;
        }
        case ID.ViewMatrix:
          parseViewMatrix(value);
          break;
        case ID.ClipPlanes:
          clipPlanes = parseClipPlanes(value);
          break;
        case ID.ClipPlanesStyle:
          clipPlanesStyle = parseClipPlanesStyle(value);
          break;
        case ID.AspectRatio: {
          double ratio = ColorUtil.dbl(value, Double.NaN);
          if (ratio > 0) {
            aspectRatio = ratio;
          }
          break;
        }
        case ID.ImagePadding:
          imagePadding = parseInsets(value, true);
          break;
        case ID.ImageMargins:
          imageMargins = parseInsets(value, false);
          break;
        case ID.PlotRegion:
          plotRegion = parseRegion(value);
          break;
        case ID.PlotRangePadding:
          parsePlotRangePadding(value);
          break;
        case ID.BaseStyle:
          baseStyle = value.isNone() ? null : value;
          break;
        case ID.ViewPoint:
          parseViewPoint(value);
          break;
        case ID.ViewVertical: {
          double[] v = vector(value);
          if (v != null) {
            viewVertical = v;
          }
          break;
        }
        case ID.ViewCenter: {
          double[] v = vector(value);
          if (v != null) {
            viewCenter = v;
          }
          break;
        }
        case ID.ViewAngle: {
          double a = ColorUtil.dbl(value, Double.NaN);
          if (!Double.isNaN(a)) {
            // measures ViewAngle in radians; the renderer wants degrees
            viewAngle = Math.toDegrees(a);
          }
          break;
        }
        case ID.ViewProjection:
          orthographic = "Orthographic".equalsIgnoreCase(unquote(value.toString()));
          break;
        case ID.SphericalRegion:
          sphericalRegion = value.isTrue();
          break;
        case ID.LabelStyle: {
          Color c = firstColor(value);
          if (c != null) {
            labelColor = c;
          }
          double size = firstFontSize(value);
          if (!Double.isNaN(size)) {
            labelFontSize = size;
          }
          break;
        }
        case ID.ScalingFunctions:
          parseScaling(value);
          break;
        case ID.Lighting:
          lighting = value;
          break;
        default:
          break;
      }
    }
  }

  private void parseAxes(IExpr value) {
    if (value.isList() && ((IAST) value).argSize() >= 3) {
      IAST list = (IAST) value;
      for (int i = 0; i < 3; i++) {
        axes[i] = !list.get(i + 1).isFalse();
      }
      return;
    }
    // Axes -> True and Axes -> Automatic draw all three; False and None draw none
    boolean on = !value.isFalse() && !value.isNone();
    axes[0] = axes[1] = axes[2] = on;
  }

  private void parseAxesLabel(IExpr value) {
    if (unlabelled(value)) {
      return;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 0; i < 3 && i < list.argSize(); i++) {
        IExpr label = list.get(i + 1);
        if (!unlabelled(label)) {
          axesLabel[i] = text(label);
        }
      }
      return;
    }
    axesLabel[2] = text(value);
  }

  /**
   * Whether a label setting asks for no label at all.
   *
   * <p>
   * {@code Automatic} counts: it asks for a label to be chosen, and the only thing that can choose
   * one is the plot that knows its own variables - it resolves the option before the picture is
   * built. Anything still holding {@code Automatic} by the time it reaches here, such as a hand
   * written {@code Graphics3D}, has nothing to derive a name from. Drawing the symbol itself, which
   * is what used to happen, put the word "Automatic" along the z axis.
   */
  private static boolean unlabelled(IExpr value) {
    return value.isNone() || value.isAutomatic();
  }

  private void parseAxesEdge(IExpr value) {
    if (!value.isList()) {
      return;
    }
    IAST list = (IAST) value;
    for (int i = 0; i < 3 && i < list.argSize(); i++) {
      IExpr spec = list.get(i + 1);
      if (spec.isNone()) {
        axesEdgeNone[i] = true;
      } else if (spec.isList() && ((IAST) spec).argSize() >= 2) {
        IAST pair = (IAST) spec;
        axesEdge[i] =
            new double[] {ColorUtil.dbl(pair.arg1(), 1.0), ColorUtil.dbl(pair.arg2(), 1.0)};
      }
    }
  }

  private void parseBoxRatios(IExpr value) {
    if (value.isList() && ((IAST) value).argSize() >= 3) {
      IAST list = (IAST) value;
      double x = ColorUtil.dbl(list.arg1(), Double.NaN);
      double y = ColorUtil.dbl(list.arg2(), Double.NaN);
      double z = ColorUtil.dbl(list.arg3(), Double.NaN);
      if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) && x > 0 && y > 0
          && z > 0) {
        boxRatios = new double[] {x, y, z};
      }
    }
  }

  /**
   * {@code ImagePadding} and {@code ImageMargins}, as left, right, bottom, top.
   *
   * <p>
   * Both are written the same way - one number for every side, or
   * {@code {{left,right},{bottom,top}}} - and both are measured in printer's points.
   *
   * @param automaticIsAll whether a bare {@code Automatic} means "as much as is needed", which is
   *        what {@code ImagePadding} defaults to and {@code ImageMargins} does not
   * @return the four insets, or {@code null} to leave the renderer's own choice alone
   */
  private static double[] parseInsets(IExpr value, boolean automaticIsAll) {
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.None:
          return new double[] {0, 0, 0, 0};
        case ID.All:
          return null; // as much as the contents need, which is what the renderer already does
        case ID.Automatic:
          return automaticIsAll ? null : new double[] {0, 0, 0, 0};
        default:
          return null;
      }
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      double[] horizontal = sides(list.arg1());
      double[] vertical = sides(list.arg2());
      if (horizontal != null && vertical != null) {
        return new double[] {horizontal[0], horizontal[1], vertical[0], vertical[1]};
      }
    }
    double all = ColorUtil.dbl(value, Double.NaN);
    if (Double.isFinite(all) && all >= 0) {
      return new double[] {all, all, all, all};
    }
    return null;
  }

  /**
   * The two sides of an inset or a padding: {@code {low, high}}, or one number used for both.
   *
   * <p>
   * Unlike {@link #pair}, which reads a range and insists it runs upwards, either side here may be
   * zero and they are independent of one another.
   */
  private static double[] sides(IExpr expr) {
    if (expr.isList() && ((IAST) expr).argSize() >= 2) {
      double low = ColorUtil.dbl(((IAST) expr).arg1(), Double.NaN);
      double high = ColorUtil.dbl(((IAST) expr).arg2(), Double.NaN);
      if (Double.isFinite(low) && Double.isFinite(high)) {
        return new double[] {low, high};
      }
      return null;
    }
    double both = ColorUtil.dbl(expr, Double.NaN);
    return Double.isFinite(both) ? new double[] {both, both} : null;
  }

  /** {@code PlotRegion -> {{xmin,xmax},{ymin,ymax}}} in scaled coordinates of the display area. */
  private static double[][] parseRegion(IExpr value) {
    if (!value.isList() || ((IAST) value).argSize() < 2) {
      return null;
    }
    double[] x = sides(((IAST) value).arg1());
    double[] y = sides(((IAST) value).arg2());
    if (x == null || y == null || x[1] <= x[0] || y[1] <= y[0]) {
      return null;
    }
    return new double[][] {x, y};
  }

  /**
   * {@code PlotRangePadding}, which widens the range the box covers.
   *
   * <p>
   * A plain number is in the data's own units; {@code Scaled[s]} is a fraction of the finished
   * plot, so the same setting suits any data. Either may be given once for all three axes or once
   * per axis, and each axis may name its two sides separately.
   */
  private void parsePlotRangePadding(IExpr value) {
    plotRangePadding = PlotRangePaddingSpec.parseOrAutomatic(value, 3);
  }

  /** Whether an explicit {@code PlotRange} fixed this axis. */
  public boolean plotRangePinned(int axis) {
    return plotRange[axis] != null;
  }

  /** {@code ViewMatrix -> {t, p}}, or a single matrix used as the transformation alone. */
  private void parseViewMatrix(IExpr value) {
    if (!value.isList()) {
      return;
    }
    IAST list = (IAST) value;
    double[] single = matrix4(value);
    if (single != null) {
      viewTransform = single;
      viewProjectionMatrix = null;
      return;
    }
    if (list.argSize() >= 2) {
      double[] t = matrix4(list.arg1());
      double[] p = matrix4(list.arg2());
      if (t != null) {
        viewTransform = t;
        viewProjectionMatrix = p;
      }
    }
  }

  /** A four by four matrix, read row by row. */
  private static double[] matrix4(IExpr value) {
    if (!value.isList() || ((IAST) value).argSize() != 4) {
      return null;
    }
    IAST rows = (IAST) value;
    double[] out = new double[16];
    for (int r = 0; r < 4; r++) {
      IExpr row = rows.get(r + 1);
      if (!row.isList() || ((IAST) row).argSize() != 4) {
        return null;
      }
      for (int c = 0; c < 4; c++) {
        out[r * 4 + c] = ColorUtil.dbl(((IAST) row).get(c + 1), Double.NaN);
        if (!Double.isFinite(out[r * 4 + c])) {
          return null;
        }
      }
    }
    return out;
  }

  /**
   * The clipping planes a call asks for.
   *
   * <p>
   * A plane is written either as the four coefficients of {@code ax+by+cz+d=0} or as an
   * {@code InfinitePlane} through three points, and either one plane or a list of them may be
   * given. Three points are turned into coefficients by taking the normal of the triangle they
   * make, so both spellings end up as the same four numbers.
   */
  private static double[][] parseClipPlanes(IExpr value) {
    if (value.isNone() || !value.isAST() && !value.isList()) {
      return null;
    }
    List<double[]> planes = new ArrayList<>();
    // a bare {a,b,c,d} is one plane; anything else is read as a list of them
    double[] single = clipPlane(value);
    if (single != null) {
      planes.add(single);
    } else if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        double[] plane = clipPlane(list.get(i));
        if (plane != null) {
          planes.add(plane);
        }
      }
    }
    return planes.isEmpty() ? null : planes.toArray(new double[0][]);
  }

  /** One style for every plane, or one per plane in the order they were given. */
  private static IExpr[] parseClipPlanesStyle(IExpr value) {
    if (value.isNone()) {
      return null;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      IExpr[] styles = new IExpr[list.argSize()];
      for (int i = 0; i < styles.length; i++) {
        styles[i] = list.get(i + 1);
      }
      return styles.length == 0 ? null : styles;
    }
    return new IExpr[] {value};
  }

  /** One plane, as the four coefficients of {@code ax+by+cz+d=0}. */
  private static double[] clipPlane(IExpr expr) {
    if (expr.isAST(S.InfinitePlane, 2) && ((IAST) expr).arg1().isList()) {
      IAST points = (IAST) ((IAST) expr).arg1();
      if (points.argSize() >= 3) {
        double[] a = vector(points.arg1());
        double[] b = vector(points.arg2());
        double[] c = vector(points.arg3());
        if (a != null && b != null && c != null) {
          double[] u = {b[0] - a[0], b[1] - a[1], b[2] - a[2]};
          double[] v = {c[0] - a[0], c[1] - a[1], c[2] - a[2]};
          double[] n = {u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2],
              u[0] * v[1] - u[1] * v[0]};
          double length = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
          if (length > 1e-12) {
            return new double[] {n[0] / length, n[1] / length, n[2] / length,
                -(n[0] * a[0] + n[1] * a[1] + n[2] * a[2]) / length};
          }
        }
      }
      return null;
    }
    if (expr.isList() && ((IAST) expr).argSize() == 4) {
      IAST list = (IAST) expr;
      double[] coefficients = new double[4];
      for (int i = 0; i < 4; i++) {
        coefficients[i] = ColorUtil.dbl(list.get(i + 1), Double.NaN);
        if (!Double.isFinite(coefficients[i])) {
          return null;
        }
      }
      double length = Math.sqrt(coefficients[0] * coefficients[0]
          + coefficients[1] * coefficients[1] + coefficients[2] * coefficients[2]);
      if (length <= 1e-12) {
        return null;
      }
      for (int i = 0; i < 4; i++) {
        coefficients[i] /= length;
      }
      return coefficients;
    }
    return null;
  }

  private void parseImageSize(IExpr value) {
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      double w = ColorUtil.dbl(list.arg1(), Double.NaN);
      double h = ColorUtil.dbl(list.arg2(), Double.NaN);
      if (w > 0) {
        imageSize[0] = w;
      }
      if (h > 0) {
        imageSize[1] = h;
        imageHeightGiven = true;
      }
      return;
    }
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.Tiny:
          setSquare(100);
          return;
        case ID.Small:
          setSquare(180);
          return;
        case ID.Medium:
          setSquare(360);
          return;
        case ID.Large:
          setSquare(576);
          return;
        default:
          return;
      }
    }
    double w = ColorUtil.dbl(value, Double.NaN);
    if (w > 0) {
      setSquare(w);
    }
  }

  private void setSquare(double size) {
    imageSize[0] = size;
    imageSize[1] = size;
  }

  private void parsePlotRange(IExpr value) {
    if (!value.isList()) {
      // Automatic, All and Full all leave the range to the data
      return;
    }
    IAST list = (IAST) value;
    if (list.argSize() >= 3 && list.arg1().isList()) {
      for (int i = 0; i < 3; i++) {
        plotRange[i] = pair(list.get(i + 1));
      }
      return;
    }
    if (list.argSize() == 2 && !list.arg1().isList()) {
      // a bare {min, max} constrains the z range, as it does for Plot3D
      plotRange[2] = pair(list);
    }
  }

  private static double[] pair(IExpr expr) {
    if (!expr.isList() || ((IAST) expr).argSize() < 2) {
      return null;
    }
    IAST list = (IAST) expr;
    double lo = ColorUtil.dbl(list.arg1(), Double.NaN);
    double hi = ColorUtil.dbl(list.arg2(), Double.NaN);
    if (!Double.isFinite(lo) || !Double.isFinite(hi) || hi <= lo) {
      return null;
    }
    return new double[] {lo, hi};
  }

  private void parseTicks(IExpr value) {
    if (value.isNone() || value.isFalse()) {
      showTicks = false;
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 3) {
      IAST list = (IAST) value;
      for (int i = 0; i < 3; i++) {
        IExpr spec = list.get(i + 1);
        if (spec.isList()) {
          ticksSpec[i] = spec;
        }
      }
    }
  }

  /**
   * {@code ViewVector} places the camera in the data's own coordinates.
   *
   * <p>
   * Given one vector it is the camera's position and it keeps looking at the middle of the scene;
   * given two, the second is the point it looks at. {@code ViewPoint} says the same thing in units
   * of the box, so both end up as a direction and a centre.
   */
  private void parseViewVector(IExpr value) {
    if (!value.isList()) {
      return;
    }
    IAST list = (IAST) value;
    if (list.argSize() >= 2 && list.arg1().isList() && list.arg2().isList()) {
      double[] from = vector(list.arg1());
      double[] to = vector(list.arg2());
      if (from != null && to != null) {
        viewVector = from;
        viewCenter = to;
      }
      return;
    }
    double[] from = vector(value);
    if (from != null) {
      viewVector = from;
    }
  }

  private void parseViewPoint(IExpr value) {
    double[] v = vector(value);
    if (v != null) {
      viewPoint = v;
      return;
    }
    String name = unquote(value.toString());
    // the named directions in place of a coordinate triple
    if ("Above".equalsIgnoreCase(name) || "Top".equalsIgnoreCase(name)) {
      viewPoint = new double[] {0, 0, 2};
    } else if ("Below".equalsIgnoreCase(name) || "Bottom".equalsIgnoreCase(name)) {
      viewPoint = new double[] {0, 0, -2};
    } else if ("Front".equalsIgnoreCase(name)) {
      viewPoint = new double[] {0, -2, 0};
    } else if ("Back".equalsIgnoreCase(name)) {
      viewPoint = new double[] {0, 2, 0};
    } else if ("Left".equalsIgnoreCase(name)) {
      viewPoint = new double[] {-2, 0, 0};
    } else if ("Right".equalsIgnoreCase(name)) {
      viewPoint = new double[] {2, 0, 0};
    }
  }

  private void parseScaling(IExpr value) {
    if (value.isList() && ((IAST) value).argSize() >= 3) {
      IAST list = (IAST) value;
      for (int i = 0; i < 3; i++) {
        scaling[i] = scalerName(list.get(i + 1));
      }
      return;
    }
    // a single specification applies to the vertical axis, matching Plot3D's own reading
    scaling[2] = scalerName(value);
  }

  private static String scalerName(IExpr expr) {
    String s = unquote(expr.toString());
    if ("Log".equalsIgnoreCase(s)) {
      return "Log";
    }
    if ("Reverse".equalsIgnoreCase(s)) {
      return "Reverse";
    }
    return "Identity";
  }

  /** The first colour in a style specification, which may be a bare directive or a list. */
  static Color firstColor(IExpr value) {
    Color c = ColorUtil.parse(value);
    if (c != null) {
      return c;
    }
    if (value.isList() || value.isAST(org.matheclipse.core.expression.S.Directive)) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        Color inner = firstColor(list.get(i));
        if (inner != null) {
          return inner;
        }
      }
    }
    return null;
  }

  /**
   * The transparency an {@code Opacity} inside a style specification asks for, or {@code NaN}
   * when it names none.
   *
   * <p>
   * {@code Opacity[o, colour]} is a colour rather than a directive, and its transparency reaches
   * the caller through that colour's alpha channel instead.
   */
  static double firstOpacity(IExpr value) {
    if (value.isAST(org.matheclipse.core.expression.S.Opacity, 2)) {
      return ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
    }
    if (value.isList() || value.isAST(org.matheclipse.core.expression.S.Directive)) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        double o = firstOpacity(list.get(i));
        if (!Double.isNaN(o)) {
          return o;
        }
      }
    }
    return Double.NaN;
  }

  /**
   * The width in printer's points of a named <code>Thickness</code> size, or
   * <code>Double.NaN</code>. <code>Thick</code> is <code>Thickness(Large)</code> and
   * <code>Thin</code> is <code>Thickness(Tiny)</code>, so the named sizes carry the widths those
   * two directives have always drawn with.
   */
  static double namedThicknessPoints(IExpr size) {
    if (size.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) size).ordinal()) {
        case ID.Tiny:
          return 1.0;
        case ID.Small:
          return 1.25;
        case ID.Medium:
          return 1.5;
        case ID.Large:
          return 2.0;
        default:
          break;
      }
    }
    return Double.NaN;
  }

  /** The first line width in a style specification, in printer's points. */
  static double firstThickness(IExpr value) {
    if (value.isAST(org.matheclipse.core.expression.S.AbsoluteThickness, 2)) {
      return ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
    }
    if (value.isAST(org.matheclipse.core.expression.S.Thickness, 2)) {
      IExpr size = ((IAST) value).arg1();
      double named = namedThicknessPoints(size);
      if (!Double.isNaN(named)) {
        return named;
      }
      // a fraction of the image; 0.002 is Thin and reads as one point
      double f = ColorUtil.dbl(size, Double.NaN);
      return Double.isNaN(f) ? Double.NaN : f * 500.0;
    }
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.Thin:
          return 1.0;
        case ID.Thick:
          return 2.0;
        default:
          return Double.NaN;
      }
    }
    if (value.isList() || value.isAST(org.matheclipse.core.expression.S.Directive)) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        double t = firstThickness(list.get(i));
        if (!Double.isNaN(t)) {
          return t;
        }
      }
    }
    return Double.NaN;
  }

  /** The first font size in a style specification, in points. */
  static double firstFontSize(IExpr value) {
    if (value.isRuleAST()) {
      IAST rule = (IAST) value;
      if (rule.arg1().isBuiltInSymbol()
          && ((IBuiltInSymbol) rule.arg1()).ordinal() == ID.FontSize) {
        return ColorUtil.dbl(rule.arg2(), Double.NaN);
      }
      return Double.NaN;
    }
    if (value.isList() || value.isAST(org.matheclipse.core.expression.S.Directive)) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        double s = firstFontSize(list.get(i));
        if (!Double.isNaN(s)) {
          return s;
        }
      }
    }
    return Double.NaN;
  }

  /** A three component numeric vector, or {@code null} when the expression is not one. */
  static double[] vector(IExpr expr) {
    if (!expr.isList() || ((IAST) expr).argSize() < 3) {
      return null;
    }
    IAST list = (IAST) expr;
    double x = ColorUtil.dbl(list.arg1(), Double.NaN);
    double y = ColorUtil.dbl(list.arg2(), Double.NaN);
    double z = ColorUtil.dbl(list.arg3(), Double.NaN);
    if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
      return null;
    }
    return new double[] {x, y, z};
  }

  /** The plain text of a label expression, with the quotes a string carries removed. */
  public static String text(IExpr expr) {
    if (expr.isString()) {
      return expr.toString();
    }
    return unquote(expr.toString());
  }

  public static String unquote(String s) {
    if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }
}
