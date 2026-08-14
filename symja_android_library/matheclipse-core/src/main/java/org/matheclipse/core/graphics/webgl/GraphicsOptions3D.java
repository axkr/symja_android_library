package org.matheclipse.core.graphics.webgl;

import java.awt.Color;
import org.matheclipse.core.expression.ID;
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

  public String plotLabel = null;

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
        case ID.PlotLabel:
          if (!value.isNone()) {
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
    if (value.isNone()) {
      return;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 0; i < 3 && i < list.argSize(); i++) {
        IExpr label = list.get(i + 1);
        if (!label.isNone()) {
          axesLabel[i] = text(label);
        }
      }
      return;
    }
    axesLabel[2] = text(value);
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

  /** The first line width in a style specification, in printer's points. */
  static double firstThickness(IExpr value) {
    if (value.isAST(org.matheclipse.core.expression.S.AbsoluteThickness, 2)) {
      return ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
    }
    if (value.isAST(org.matheclipse.core.expression.S.Thickness, 2)) {
      // a fraction of the image; 0.002 is Thin and reads as one point
      double f = ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
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
