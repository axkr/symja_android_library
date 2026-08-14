package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/** The options of a 2D {@code Graphics} expression, parsed into plain fields. */
public final class GraphicsOptions2D {

  /** A length that is either absolute or a fraction of the plot range. */
  public static final class Padding {
    public double value;
    public boolean scaled;

    Padding(double value, boolean scaled) {
      this.value = value;
      this.scaled = scaled;
    }

    /** The padding in data units for a range of the given size. */
    public double resolve(double range) {
      return scaled ? range * value : value;
    }
  }

  public boolean axesX = false;
  public boolean axesY = false;
  public double[] axesOrigin = null;
  public Style2D axesStyle = new Style2D();
  public IExpr axesLabel = null;

  /** Frame edges, in the order left, right, bottom, top. */
  public final boolean[] frame = {false, false, false, false};
  public Style2D frameStyle = new Style2D();
  public IExpr frameLabel = null;
  public IExpr frameTicks = S.Automatic;

  public Style2D globalStyle = new Style2D();

  public IExpr gridLines = null;
  public Style2D gridLinesStyle = new Style2D();

  public Color background = null;
  public double aspectRatio = Double.NaN;
  public boolean aspectRatioAutomatic = false;

  public double[] imageSize = {360, 360};
  public boolean imageSizeSet = false;
  public boolean imageSizeHeightSet = false;

  /** {@code {{left, right}, {bottom, top}}} in pixels, or {@code null} for automatic. */
  public double[] imagePadding = null;

  /** {@code [0]} is x, {@code [1]} is y; {@code NaN} means automatic. */
  public double[][] plotRange = null;
  public boolean plotRangeAutomatic = true;
  public boolean plotRangeAll = false;
  public boolean plotRangeClipping = false;
  public Padding plotRangePaddingX = new Padding(0.02, true);
  public Padding plotRangePaddingY = new Padding(0.02, true);

  /** {@code LabelStyle}, applied to every piece of text the plot labels itself with. */
  public Style2D labelStyle = null;
  /** Whether {@code LabelStyle} asked for a font size, as opposed to only a colour or weight. */
  public boolean labelFontSizeSet = false;

  /**
   * {@code ClippingStyle}: how the parts of a curve that leave the plot range are drawn. Null
   * leaves them out altogether, which is the default.
   */
  public IExpr clippingStyle = null;

  public String scalingX = "None";
  public String scalingY = "None";

  /**
   * True when this graphic was produced by one of the plot builtins rather than written by hand.
   *
   * <p>
   * The plot family always emits Symja's internal {@code $Scaling} option, which is what this is
   * detected from. It matters because a function plot may legitimately contain a spike a thousand
   * times taller than the rest of the curve ({@code Plot[1/x, {x, -1, 1}]} does), and the visible y
   * range has to be narrowed to the body of the data for the picture to be worth anything. Doing
   * the same to a hand written {@code Graphics} would silently crop the user's own coordinates, so
   * the refinement is gated on this flag.
   */
  public boolean plotGenerated = false;

  public IExpr prolog = null;
  public IExpr epilog = null;
  public IExpr plotLegends = null;
  public IExpr plotStyleRaw = null;
  public IExpr ticks = S.Automatic;
  public IExpr plotLabel = null;
  public boolean joined = false;

  public GraphicsOptions2D() {
    gridLinesStyle.strokeColor = new Color(200, 200, 200);
    gridLinesStyle.strokeWidth = 0.5;
  }

  /** True when any frame edge is drawn. */
  public boolean hasFrame() {
    return frame[0] || frame[1] || frame[2] || frame[3];
  }

  /**
   * Read the option rules of {@code ast}, starting after the first argument.
   *
   * @param collector used to interpret style options with the same rules as inline directives
   */
  public void parse(IAST ast, PrimitiveCollector collector) {
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
      try {
        apply(((IBuiltInSymbol) key).ordinal(), value, collector);
      } catch (RuntimeException rex) {
        // a malformed option falls back to its default rather than losing the picture
      }
    }
  }

  private void apply(int optionId, IExpr value, PrimitiveCollector collector) {
    switch (optionId) {
      case ID.Axes:
        if (value.isTrue()) {
          axesX = axesY = true;
        } else if (value.isFalse() || value.isNone()) {
          axesX = axesY = false;
        } else if (value.isList() && ((IAST) value).argSize() >= 2) {
          axesX = ((IAST) value).arg1().isTrue();
          axesY = ((IAST) value).arg2().isTrue();
        }
        break;
      case ID.AxesOrigin:
        if (value.isList() && ((IAST) value).argSize() >= 2) {
          axesOrigin = new double[] {ColorUtil.dbl(((IAST) value).arg1(), Double.NaN),
              ColorUtil.dbl(((IAST) value).arg2(), Double.NaN)};
        }
        break;
      case ID.AxesStyle:
        applyStyle(value, axesStyle, collector);
        break;
      case ID.AxesLabel:
        axesLabel = value;
        break;
      case ID.Frame:
        applyFrame(value);
        break;
      case ID.FrameStyle:
        applyStyle(value, frameStyle, collector);
        break;
      case ID.LabelStyle:
        applyLabelStyle(value, collector);
        break;
      case ID.ScalingFunctions:
        // the same shape as $Scaling: a bare value scales y, a pair scales both axes. It is a
        // separate case only because $Scaling also marks the picture as plot generated.
        if (value.isList()) {
          IAST pair = (IAST) value;
          if (pair.argSize() >= 1) {
            scalingX = PrimitiveCollector.unquote(pair.arg1().toString());
          }
          if (pair.argSize() >= 2) {
            scalingY = PrimitiveCollector.unquote(pair.arg2().toString());
          }
        } else if (value.isString()) {
          scalingY = PrimitiveCollector.unquote(value.toString());
        }
        break;
      case ID.ClippingStyle:
        clippingStyle = value.isNone() || value.isFalse() ? null : value;
        break;
      case ID.FrameLabel:
        frameLabel = value;
        break;
      case ID.FrameTicks:
        frameTicks = value;
        break;
      case ID.GridLines:
        gridLines = value;
        break;
      case ID.GridLinesStyle:
        applyStyle(value, gridLinesStyle, collector);
        break;
      case ID.ImageSize:
        applyImageSize(value);
        break;
      case ID.ImagePadding:
        applyImagePadding(value);
        break;
      case ID.PlotRange:
        applyPlotRange(value);
        break;
      case ID.PlotRangePadding:
        applyPlotRangePadding(value);
        break;
      case ID.PlotRangeClipping:
        plotRangeClipping = value.isTrue();
        break;
      case ID.AspectRatio:
        if (value == S.Automatic) {
          aspectRatioAutomatic = true;
          aspectRatio = Double.NaN;
        } else {
          double r = ColorUtil.dbl(value, Double.NaN);
          if (!Double.isNaN(r) && r > 0) {
            aspectRatio = r;
            aspectRatioAutomatic = false;
          } else {
            aspectRatioAutomatic = true;
          }
        }
        break;
      case ID.Background:
        if (!value.isNone()) {
          background = ColorUtil.parse(value);
        }
        break;
      case ID.PlotLabel:
        plotLabel = value.isNone() ? null : value;
        break;
      case ID.Ticks:
        ticks = value;
        break;
      case ID.Prolog:
        prolog = value;
        break;
      case ID.Epilog:
        epilog = value;
        break;
      case ID.PlotLegends:
        plotLegends = value.isNone() ? null : value;
        break;
      case ID.Joined:
        joined = value.isTrue();
        break;
      // LabelStyle used to be applied here too, but it styles the labels rather than the
      // primitives, and the colour a plot puts on its own curves overrode it anyway
      case ID.PlotStyle:
      case ID.BaseStyle:
        applyStyle(value, globalStyle, collector);
        if (optionId == ID.PlotStyle) {
          plotStyleRaw = value;
        }
        break;
      case ID.$Scaling:
        plotGenerated = true;
        if (value.isList()) {
          IAST list = (IAST) value;
          if (list.argSize() >= 1) {
            scalingX = PrimitiveCollector.unquote(list.arg1().toString());
          }
          if (list.argSize() >= 2) {
            scalingY = PrimitiveCollector.unquote(list.arg2().toString());
          }
        } else if (value.isString()) {
          scalingY = PrimitiveCollector.unquote(value.toString());
        }
        break;
      default:
        // options that do not affect the drawing are accepted and ignored
        break;
    }
  }

  private void applyStyle(IExpr value, Style2D target, PrimitiveCollector collector) {
    if (collector != null) {
      collector.applyStyleTo(value, target);
    }
  }

  /**
   * Read {@code LabelStyle}, which may be a directive, a list of them, or a bare font size.
   *
   * <p>
   * A number on its own is a font size, which the general style parser does not treat as one
   * because a bare number means nothing to a drawn primitive.
   */
  private void applyLabelStyle(IExpr value, PrimitiveCollector collector) {
    if (value.isNone() || value == S.Automatic) {
      return;
    }
    labelStyle = new Style2D();
    double before = labelStyle.fontSize;
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i < list.size(); i++) {
        applyLabelStylePart(list.get(i), collector);
      }
    } else {
      applyLabelStylePart(value, collector);
    }
    labelFontSizeSet = labelStyle.fontSize != before;
  }

  private void applyLabelStylePart(IExpr part, PrimitiveCollector collector) {
    if (part.isNumber()) {
      double size = ColorUtil.dbl(part, 0);
      if (size > 0) {
        labelStyle.fontSize = size;
      }
      return;
    }
    if (part.isAST(S.Directive)) {
      // a size written inside a directive counts too, and the style parser would skip it
      IAST directive = (IAST) part;
      for (int i = 1; i < directive.size(); i++) {
        applyLabelStylePart(directive.get(i), collector);
      }
      return;
    }
    applyStyle(part, labelStyle, collector);
  }

  private void applyFrame(IExpr value) {
    if (value.isTrue() || value == S.All) {
      java.util.Arrays.fill(frame, true);
      return;
    }
    if (value.isFalse() || value.isNone()) {
      java.util.Arrays.fill(frame, false);
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      if (list.arg1().isList() && list.arg2().isList()) {
        // {{left, right}, {bottom, top}}
        IAST lr = (IAST) list.arg1();
        IAST bt = (IAST) list.arg2();
        frame[0] = lr.argSize() >= 1 && lr.arg1().isTrue();
        frame[1] = lr.argSize() >= 2 && lr.arg2().isTrue();
        frame[2] = bt.argSize() >= 1 && bt.arg1().isTrue();
        frame[3] = bt.argSize() >= 2 && bt.arg2().isTrue();
      } else {
        // {horizontal, vertical}
        boolean h = list.arg1().isTrue();
        boolean v = list.arg2().isTrue();
        frame[0] = frame[1] = h;
        frame[2] = frame[3] = v;
      }
    }
  }

  private void applyImageSize(IExpr value) {
    imageSizeSet = true;
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      double w = namedImageSize(list.arg1(), Double.NaN);
      double h = namedImageSize(list.arg2(), Double.NaN);
      if (!Double.isNaN(w) && w > 0) {
        imageSize[0] = w;
      }
      if (!Double.isNaN(h) && h > 0) {
        imageSize[1] = h;
        imageSizeHeightSet = true;
      }
      return;
    }
    // A single value, named or numeric, gives the width; the height then follows from the aspect
    // ratio rather than making the image square.
    double s = namedImageSize(value, Double.NaN);
    if (!Double.isNaN(s) && s > 0) {
      imageSize[0] = s;
    }
  }

  private double namedImageSize(IExpr value, double def) {
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.Tiny:
          return 100;
        case ID.Small:
          return 180;
        case ID.Medium:
          return 360;
        case ID.Large:
          return 600;
        case ID.Full:
          return 600;
        case ID.Automatic:
        default:
          return def;
      }
    }
    return ColorUtil.dbl(value, def);
  }

  private void applyImagePadding(IExpr value) {
    if (value.isNone()) {
      imagePadding = new double[] {0, 0, 0, 0};
      return;
    }
    if (value == S.Automatic) {
      imagePadding = null;
      return;
    }
    double all = ColorUtil.dbl(value, Double.NaN);
    if (!Double.isNaN(all)) {
      imagePadding = new double[] {all, all, all, all};
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      if (list.arg1().isList() && list.arg2().isList()) {
        IAST lr = (IAST) list.arg1();
        IAST bt = (IAST) list.arg2();
        imagePadding = new double[] {ColorUtil.dbl(lr.arg1(), 0), ColorUtil.dbl(lr.arg2(), 0),
            ColorUtil.dbl(bt.arg1(), 0), ColorUtil.dbl(bt.arg2(), 0)};
      }
    }
  }

  private void applyPlotRange(IExpr value) {
    if (value == S.All) {
      plotRange = null;
      plotRangeAutomatic = false;
      plotRangeAll = true;
      return;
    }
    if (value == S.Automatic || value.isNone()) {
      plotRange = null;
      plotRangeAutomatic = true;
      return;
    }
    double symmetric = ColorUtil.dbl(value, Double.NaN);
    if (!Double.isNaN(symmetric)) {
      // PlotRange -> r means {{-r, r}, {-r, r}}
      plotRange = new double[][] {{-symmetric, symmetric}, {-symmetric, symmetric}};
      plotRangeAutomatic = false;
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      plotRange = new double[][] {{Double.NaN, Double.NaN}, {Double.NaN, Double.NaN}};
      if (list.arg1().isList() && list.arg2().isList()) {
        setRange(plotRange[0], (IAST) list.arg1());
        setRange(plotRange[1], (IAST) list.arg2());
      } else {
        // a bare {min, max} constrains the y axis only
        setRange(plotRange[1], list);
      }
      plotRangeAutomatic = false;
    }
  }

  private void setRange(double[] target, IAST pair) {
    if (pair.argSize() < 2) {
      return;
    }
    double lo = ColorUtil.dbl(pair.arg1(), Double.NaN);
    double hi = ColorUtil.dbl(pair.arg2(), Double.NaN);
    // a reversed range is still a range
    if (!Double.isNaN(lo) && !Double.isNaN(hi) && lo > hi) {
      double t = lo;
      lo = hi;
      hi = t;
    }
    target[0] = lo;
    target[1] = hi;
  }

  private void applyPlotRangePadding(IExpr value) {
    if (value.isNone()) {
      plotRangePaddingX = new Padding(0, false);
      plotRangePaddingY = new Padding(0, false);
      return;
    }
    if (value == S.Automatic) {
      plotRangePaddingX = new Padding(0.02, true);
      plotRangePaddingY = new Padding(0.02, true);
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      Padding px = paddingOf(list.arg1());
      Padding py = paddingOf(list.arg2());
      if (px != null) {
        plotRangePaddingX = px;
      }
      if (py != null) {
        plotRangePaddingY = py;
      }
      return;
    }
    Padding p = paddingOf(value);
    if (p != null) {
      plotRangePaddingX = p;
      plotRangePaddingY = new Padding(p.value, p.scaled);
    }
  }

  private Padding paddingOf(IExpr value) {
    if (value.isNone()) {
      return new Padding(0, false);
    }
    if (value.isAST(S.Scaled, 2)) {
      return new Padding(ColorUtil.dbl(((IAST) value).arg1(), 0.02), true);
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      // {before, after}; a single value covers both sides here
      return paddingOf(((IAST) value).arg1());
    }
    double d = ColorUtil.dbl(value, Double.NaN);
    return Double.isNaN(d) ? null : new Padding(d, false);
  }
}
