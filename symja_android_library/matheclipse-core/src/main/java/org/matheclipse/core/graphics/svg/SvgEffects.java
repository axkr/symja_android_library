package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;

/**
 * The SVG definitions behind the directives which are no plain colour or stroke:
 *
 * <ul>
 * <li><code>Haloing(color, r)</code>: a filter which spreads the shape by about <code>r</code> printer's
 * points, floods the dilation with the halo colour and draws the shape on top;
 * <li><code>DropShadowing({dx, dy}, r, color)</code>: a filter which blurs the shape's alpha by
 * <code>r</code>, moves it by <code>{dx, dy}</code> (counted upwards) and draws the shape on top;
 * <li><code>LinearGradientFilling({p1, ...} -&gt; {c1, ...}, angle)</code>: a
 * <code>&lt;linearGradient&gt;</code> across the bounding box of the face, running in the direction
 * of <code>angle</code>;
 * <li><code>HatchFilling(angle, thickness, spacing)</code>: a <code>&lt;pattern&gt;</code> of
 * parallel lines in the current colour, vertical for the angle 0.
 * </ul>
 *
 * Equal specifications share one definition. The ids carry the suffix of the picture, so several
 * pictures on one page don't take each other's definitions.
 */
final class SvgEffects {

  /** The default angle of <code>HatchFilling()</code>. */
  static final double DEFAULT_HATCH_ANGLE = Math.PI / 4.0;

  /** The default line width of <code>HatchFilling()</code> in printer's points. */
  static final double DEFAULT_HATCH_THICKNESS = 1.0;

  /** The default distance of the lines of <code>HatchFilling()</code> in printer's points. */
  static final double DEFAULT_HATCH_SPACING = 6.0;

  private final Map<String, ContainerTag<?>> definitions = new LinkedHashMap<>();
  private final Map<String, String> ids = new LinkedHashMap<>();
  private String idSuffix = "";

  void setIdSuffix(String idSuffix) {
    this.idSuffix = idSuffix == null ? "" : idSuffix;
  }

  List<ContainerTag<?>> definitions() {
    return new ArrayList<>(definitions.values());
  }

  /**
   * The id of the filter for the halo and the shadow of a style, or {@code null} when it has
   * neither.
   *
   * @param imageSize the size of the picture, which is the region the filter may paint in
   */
  String filter(Style2D style, double[] imageSize) {
    boolean halo = style.haloColor != null && style.haloRadius > 0;
    boolean shadow = style.shadow != null && style.shadowColor != null;
    if (!halo && !shadow) {
      return null;
    }
    String key = "f" + (halo ? css(style.haloColor) + fmt(style.haloRadius) : "")
        + (shadow ? "s" + fmt(style.shadow[0]) + fmt(style.shadow[1]) + fmt(style.shadow[2])
            + css(style.shadowColor) : "");
    String id = ids.get(key);
    if (id != null) {
      return id;
    }
    id = (halo && shadow ? "haloShadow" : halo ? "halo" : "shadow") + ids.size() + idSuffix;
    // the region is the whole picture: a horizontal line has a bounding box without height, and a
    // filter region measured on it would hide the line altogether
    double width = imageSize != null && imageSize.length > 0 ? imageSize[0] : 400;
    double height = imageSize != null && imageSize.length > 1 && imageSize[1] > 0 ? imageSize[1]
        : width;
    ContainerTag<?> filter = tag("filter").attr("id", id).attr("filterUnits", "userSpaceOnUse")
        .attr("x", fmt(-width)).attr("y", fmt(-height)).attr("width", fmt(3 * width))
        .attr("height", fmt(3 * height));
    ContainerTag<?> merge = tag("feMerge");
    if (shadow) {
      filter.with(tag("feGaussianBlur").attr("in", "SourceAlpha")
          .attr("stdDeviation", fmt(Math.max(0.0, style.shadow[2]) / 2.0)).attr("result", "blur"));
      // the offset counts upwards, the SVG y axis downwards
      filter.with(tag("feOffset").attr("in", "blur").attr("dx", fmt(style.shadow[0]))
          .attr("dy", fmt(-style.shadow[1])).attr("result", "moved"));
      filter.with(flood(style.shadowColor, "shadowColor"));
      filter.with(tag("feComposite").attr("in", "shadowColor").attr("in2", "moved")
          .attr("operator", "in").attr("result", "shadow"));
      merge.with(tag("feMergeNode").attr("in", "shadow"));
    }
    if (halo) {
      // the shape's alpha spread by a blur and pushed back to opaque near the shape: a soft
      // dilation which also renders where feMorphology isn't supported (the jsvg rasterizer)
      filter.with(tag("feGaussianBlur").attr("in", "SourceAlpha")
          .attr("stdDeviation", fmt(style.haloRadius / 2.0)).attr("result", "spread"));
      filter.with(tag("feComponentTransfer").attr("in", "spread").attr("result", "dilated")
          .with(tag("feFuncA").attr("type", "linear").attr("slope", "8").attr("intercept", "0")));
      filter.with(flood(style.haloColor, "haloColor"));
      filter.with(tag("feComposite").attr("in", "haloColor").attr("in2", "dilated")
          .attr("operator", "in").attr("result", "halo"));
      merge.with(tag("feMergeNode").attr("in", "halo"));
    }
    merge.with(tag("feMergeNode").attr("in", "SourceGraphic"));
    filter.with(merge);
    ids.put(key, id);
    definitions.put(id, filter);
    return id;
  }

  private static ContainerTag<?> flood(Color color, String result) {
    ContainerTag<?> flood =
        tag("feFlood").attr("flood-color", ColorUtil.css(opaque(color))).attr("result", result);
    if (color.getAlpha() < 255) {
      flood.attr("flood-opacity", fmt(color.getAlpha() / 255.0));
    }
    return flood;
  }

  /**
   * The id of the gradient or the hatch pattern the faces of a style are painted with, or
   * {@code null} when the style has none or it can't be read.
   */
  String paintServer(Style2D style) {
    IAST paint = style.facePaint;
    if (paint == null) {
      return null;
    }
    if (paint.isAST(S.LinearGradientFilling)) {
      return gradient(paint);
    }
    if (paint.isAST(S.HatchFilling)) {
      return hatch(paint, style.effectiveFill());
    }
    return null;
  }

  private String gradient(IAST filling) {
    List<double[]> stops = new ArrayList<>();
    List<Color> colors = new ArrayList<>();
    if (!gradientStops(filling, stops, colors)) {
      return null;
    }
    double angle = filling.argSize() >= 2 ? ColorUtil.dbl(filling.arg2(), 0.0) : 0.0;
    StringBuilder key = new StringBuilder("g").append(fmt(angle));
    for (int i = 0; i < stops.size(); i++) {
      key.append(':').append(fmt(stops.get(i)[0])).append(css(colors.get(i)));
    }
    String id = ids.get(key.toString());
    if (id != null) {
      return id;
    }
    id = "gradient" + ids.size() + idSuffix;
    // the angle is measured counter clockwise from the x axis, the SVG y axis runs downwards
    double dx = Math.cos(angle) / 2.0;
    double dy = -Math.sin(angle) / 2.0;
    ContainerTag<?> gradient = tag("linearGradient").attr("id", id)
        .attr("x1", fmt(0.5 - dx)).attr("y1", fmt(0.5 - dy)).attr("x2", fmt(0.5 + dx))
        .attr("y2", fmt(0.5 + dy));
    for (int i = 0; i < stops.size(); i++) {
      Color c = colors.get(i);
      ContainerTag<?> stop = tag("stop").attr("offset", fmt(stops.get(i)[0]))
          .attr("stop-color", ColorUtil.css(opaque(c)));
      if (c.getAlpha() < 255) {
        stop.attr("stop-opacity", fmt(c.getAlpha() / 255.0));
      }
      gradient.with(stop);
    }
    ids.put(key.toString(), id);
    definitions.put(id, gradient);
    return id;
  }

  /**
   * The stops of a gradient: <code>{p1, ...} -&gt; {c1, ...}</code>, <code>{{p1, c1}, ...}</code>
   * or a list of colours spread evenly from 0 to 1.
   */
  static boolean gradientStops(IAST filling, List<double[]> stops, List<Color> colors) {
    if (filling.argSize() == 0) {
      stops.add(new double[] {0.0});
      colors.add(Color.BLACK);
      stops.add(new double[] {1.0});
      colors.add(Color.WHITE);
      return true;
    }
    IExpr spec = filling.arg1();
    if (spec.isRuleAST() && spec.first().isList() && spec.second().isList()
        && spec.first().argSize() == spec.second().argSize()) {
      IAST positions = (IAST) spec.first();
      IAST values = (IAST) spec.second();
      for (int i = 1; i <= positions.argSize(); i++) {
        Color c = ColorUtil.parse(values.get(i));
        double p = ColorUtil.dbl(positions.get(i), Double.NaN);
        if (c == null || Double.isNaN(p)) {
          return false;
        }
        stops.add(new double[] {p});
        colors.add(c);
      }
      return !stops.isEmpty();
    }
    if (spec.isList() && spec.argSize() > 0) {
      IAST list = (IAST) spec;
      int n = list.argSize();
      for (int i = 1; i <= n; i++) {
        IExpr item = list.get(i);
        double p = n == 1 ? 0.0 : (i - 1.0) / (n - 1.0);
        if (item.isList() && item.argSize() == 2 && ColorUtil.parse(item.first()) == null) {
          p = ColorUtil.dbl(item.first(), Double.NaN);
          item = item.second();
        }
        Color c = ColorUtil.parse(item);
        if (c == null || Double.isNaN(p)) {
          return false;
        }
        stops.add(new double[] {p});
        colors.add(c);
      }
      if (n == 1) {
        stops.add(new double[] {1.0});
        colors.add(colors.get(0));
      }
      return true;
    }
    return false;
  }

  private String hatch(IAST filling, Color color) {
    double angle = filling.argSize() >= 1 ? ColorUtil.dbl(filling.arg1(), DEFAULT_HATCH_ANGLE)
        : DEFAULT_HATCH_ANGLE;
    double thickness =
        filling.argSize() >= 2 ? ColorUtil.dbl(filling.arg2(), DEFAULT_HATCH_THICKNESS)
            : DEFAULT_HATCH_THICKNESS;
    double spacing = filling.argSize() >= 3 ? ColorUtil.dbl(filling.arg3(), DEFAULT_HATCH_SPACING)
        : DEFAULT_HATCH_SPACING;
    if (!(spacing > 0) || !(thickness > 0)) {
      return null;
    }
    Color c = color == null ? Color.BLACK : color;
    String key = "h" + fmt(angle) + ":" + fmt(thickness) + ":" + fmt(spacing) + css(c);
    String id = ids.get(key);
    if (id != null) {
      return id;
    }
    id = "hatch" + ids.size() + idSuffix;
    // a horizontal line per tile, turned counter clockwise on the screen: the angle 0 gives
    // vertical lines, the default of 45 degrees diagonal ones
    ContainerTag<?> pattern = tag("pattern").attr("id", id).attr("patternUnits", "userSpaceOnUse")
        .attr("width", fmt(spacing)).attr("height", fmt(spacing))
        .attr("patternTransform", "rotate(" + fmt(-Math.toDegrees(angle) - 90.0) + ")");
    ContainerTag<?> line = tag("rect").attr("x", "0").attr("y", "0").attr("width", fmt(spacing))
        .attr("height", fmt(Math.min(thickness, spacing))).attr("fill", ColorUtil.css(opaque(c)));
    if (c.getAlpha() < 255) {
      line.attr("fill-opacity", fmt(c.getAlpha() / 255.0));
    }
    pattern.with(line);
    ids.put(key, id);
    definitions.put(id, pattern);
    return id;
  }

  private static Color opaque(Color c) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue());
  }

  private static String css(Color c) {
    return String.format(Locale.US, "#%02x%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue(),
        c.getAlpha());
  }

  private static String fmt(double d) {
    return SvgRenderer2D.fmt(d);
  }
}
