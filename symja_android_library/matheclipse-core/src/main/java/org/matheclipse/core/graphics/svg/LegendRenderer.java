package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;

/** Draws the legend box for {@code PlotLegends}. */
public final class LegendRenderer {

  /** Width reserved at the right edge of the image for a legend. */
  public static final double LEGEND_WIDTH = 100.0;

  /** How many colours a bar legend's gradient is built from. */
  private static final int GRADIENT_STOPS = 16;

  private final Viewport2D viewport;
  private final GraphicsOptions2D options;

  public LegendRenderer(Viewport2D viewport, GraphicsOptions2D options) {
    this.viewport = viewport;
    this.options = options;
  }

  /** True when the legend specification is one this renderer can draw. */
  public static boolean isSupported(IExpr spec) {
    if (spec == null || spec.isNone()) {
      return false;
    }
    if (spec.isList()) {
      return ((IAST) spec).argSize() > 0;
    }
    if (spec.isAST()) {
      IExpr head = spec.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.BarLegend:
          case ID.LineLegend:
          case ID.PointLegend:
          case ID.SwatchLegend:
            return true;
          default:
            return false;
        }
      }
    }
    return spec == S.Automatic;
  }

  public static boolean isBarLegend(IExpr spec) {
    return spec != null && spec.isAST(S.BarLegend);
  }

  public void draw(ContainerTag<?> parent, String gradientId) {
    IExpr spec = options.plotLegends;
    if (!isSupported(spec)) {
      return;
    }
    if (isBarLegend(spec)) {
      drawBarLegend((IAST) spec, gradientId, parent);
      return;
    }
    List<String> labels = new ArrayList<>();
    IExpr markerSource = spec;
    boolean swatch = spec.isAST(S.SwatchLegend);
    if (spec.isAST() && spec.head().isBuiltInSymbol()) {
      int id = ((IBuiltInSymbol) spec.head()).ordinal();
      if (id == ID.LineLegend || id == ID.PointLegend || id == ID.SwatchLegend) {
        // LineLegend[colours, labels]
        IAST ast = (IAST) spec;
        markerSource = ast.argSize() >= 1 ? ast.arg1() : spec;
        if (ast.argSize() >= 2 && ast.arg2().isList()) {
          IAST labelList = (IAST) ast.arg2();
          for (int i = 1; i <= labelList.argSize(); i++) {
            labels.add(PrimitiveCollector.unquote(labelList.get(i).toString()));
          }
        }
      }
    }
    if (labels.isEmpty() && spec.isList()) {
      IAST list = (IAST) spec;
      for (int i = 1; i <= list.argSize(); i++) {
        labels.add(PrimitiveCollector.unquote(list.get(i).toString()));
      }
    }
    if (labels.isEmpty()) {
      return;
    }
    drawListLegend(labels, markerSource, swatch, parent);
  }

  private void drawListLegend(List<String> labels, IExpr markerSource, boolean swatch,
      ContainerTag<?> parent) {
    int count = labels.size();
    double lineHeight = 18;
    double xBase = options.imageSize[0] - LEGEND_WIDTH + 10;
    double yBase = viewport.plotY1 + 12;
    double boxHeight = count * lineHeight + 8;

    parent.with(tag("rect").attr("x", SvgRenderer2D.fmt(xBase - 6))
        .attr("y", SvgRenderer2D.fmt(yBase - 12)).attr("width", SvgRenderer2D.fmt(LEGEND_WIDTH - 8))
        .attr("height", SvgRenderer2D.fmt(boxHeight)).attr("fill", "white")
        .attr("stroke", "#cccccc").attr("stroke-width", "1"));

    for (int i = 0; i < count; i++) {
      Color color = legendColor(markerSource, i);
      double y = yBase + i * lineHeight;
      if (swatch) {
        // a SwatchLegend names areas rather than curves or points, so its marker is the area
        parent.with(tag("rect").attr("x", SvgRenderer2D.fmt(xBase + 2))
            .attr("y", SvgRenderer2D.fmt(y - 11)).attr("width", "11").attr("height", "11")
            .attr("fill", ColorUtil.css(color)).attr("stroke", "#666666")
            .attr("stroke-width", "0.5"));
      } else if (options.joined) {
        parent.with(
            tag("line").attr("x1", SvgRenderer2D.fmt(xBase)).attr("y1", SvgRenderer2D.fmt(y - 4))
                .attr("x2", SvgRenderer2D.fmt(xBase + 18)).attr("y2", SvgRenderer2D.fmt(y - 4))
                .attr("stroke", ColorUtil.css(color)).attr("stroke-width", "2"));
      } else {
        parent.with(tag("circle").attr("cx", SvgRenderer2D.fmt(xBase + 9))
            .attr("cy", SvgRenderer2D.fmt(y - 4)).attr("r", "3.5")
            .attr("fill", ColorUtil.css(color)));
      }
      parent.with(tag("text").attr("x", SvgRenderer2D.fmt(xBase + 24))
          .attr("y", SvgRenderer2D.fmt(y)).attr("fill", "black").attr("font-family", "sans-serif")
          .attr("font-size", "11").withText(labels.get(i)));
    }
  }

  /** The swatch colour for legend entry {@code index}. */
  private Color legendColor(IExpr markerSource, int index) {
    if (markerSource != null && markerSource.isList()) {
      IAST list = (IAST) markerSource;
      if (index < list.argSize()) {
        Color explicit = ColorUtil.parse(list.get(index + 1));
        if (explicit != null) {
          return explicit;
        }
      }
    }
    RGBColor rgb = GraphicsOptions.plotStyleColor(index, options.plotStyleRaw);
    return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
  }

  private void drawBarLegend(IAST barLegend, String gradientId, ContainerTag<?> parent) {
    double min = 0.0;
    double max = 1.0;
    if (barLegend.argSize() >= 2 && barLegend.arg2().isList()
        && ((IAST) barLegend.arg2()).argSize() >= 2) {
      IAST range = (IAST) barLegend.arg2();
      min = ColorUtil.dbl(range.arg1(), 0.0);
      max = ColorUtil.dbl(range.arg2(), 1.0);
    }
    double xBase = options.imageSize[0] - LEGEND_WIDTH + 20;
    double yTop = viewport.plotY1 + 6;
    double barWidth = 15;
    double barHeight = Math.max(20, viewport.plotY2 - viewport.plotY1 - 12);
    double yBottom = yTop + barHeight;

    parent.with(tag("rect").attr("x", SvgRenderer2D.fmt(xBase)).attr("y", SvgRenderer2D.fmt(yTop))
        .attr("width", SvgRenderer2D.fmt(barWidth)).attr("height", SvgRenderer2D.fmt(barHeight))
        .attr("fill", "url(#" + gradientId + ")").attr("stroke", "#999999")
        .attr("stroke-width", "1"));

    String style = "fill:black;font-family:sans-serif;font-size:11px;text-anchor:start";
    double labelX = xBase + barWidth + 5;
    parent.with(
        tag("text").attr("x", SvgRenderer2D.fmt(labelX)).attr("y", SvgRenderer2D.fmt(yTop + 9))
            .attr("style", style).withText(TickGenerator.trim(max)));
    parent.with(tag("text").attr("x", SvgRenderer2D.fmt(labelX))
        .attr("y", SvgRenderer2D.fmt((yTop + yBottom) / 2 + 4)).attr("style", style)
        .withText(TickGenerator.trim((min + max) / 2)));
    parent
        .with(tag("text").attr("x", SvgRenderer2D.fmt(labelX)).attr("y", SvgRenderer2D.fmt(yBottom))
            .attr("style", style).withText(TickGenerator.trim(min)));
  }

  /**
   * The gradient definition a bar legend paints itself with.
   *
   * <p>
   * The stops come from the colour function the legend was given, so the bar reads as the scale the
   * picture was actually drawn on. Only when there is none, or it cannot be sampled, does it fall
   * back to a fixed scheme.
   *
   * @param spec the {@code PlotLegends} setting, from which the colour function is taken
   */
  public static ContainerTag<?> barGradient(String gradientId, IExpr spec) {
    ContainerTag<?> gradient = tag("linearGradient").attr("id", gradientId).attr("x1", "0%")
        .attr("y1", "100%").attr("x2", "0%").attr("y2", "0%");
    List<Color> colors = sampleColorFunction(colorFunctionOf(spec), GRADIENT_STOPS);
    int last = colors.size() - 1;
    for (int i = 0; i < colors.size(); i++) {
      gradient.with(
          tag("stop").attr("offset", String.format(Locale.US, "%.1f%%", (double) i / last * 100.0))
              .attr("style", "stop-color:" + ColorUtil.css(colors.get(i)) + ";stop-opacity:1"));
    }
    return gradient;
  }

  /** The colour function of {@code BarLegend[colorFunction, range]}, or {@link F#NIL}. */
  private static IExpr colorFunctionOf(IExpr spec) {
    if (spec != null && spec.isAST(S.BarLegend) && ((IAST) spec).argSize() >= 1) {
      return ((IAST) spec).arg1();
    }
    return F.NIL;
  }

  /**
   * {@code count} colours evenly spaced along {@code colorFunction}, or the fixed fallback scheme
   * when it is absent or does not answer with colours.
   */
  private static List<Color> sampleColorFunction(IExpr colorFunction, int count) {
    List<Color> colors = new ArrayList<>(count);
    IExpr function = colorFunction;
    if (function.isPresent() && function.isString()) {
      // a scheme named as a string, the way ColorFunction -> "Sunset" gives it
      function = EvalEngine.get().evaluate(F.ColorData(function));
    }
    if (function.isPresent() && !function.isString()) {
      EvalEngine engine = EvalEngine.get();
      for (int i = 0; i < count; i++) {
        double t = count == 1 ? 0.0 : (double) i / (count - 1);
        Color color = null;
        try {
          color = ColorUtil.parse(engine.evaluate(F.unaryAST1(function, F.num(t))));
        } catch (RuntimeException rex) {
          color = null;
        }
        if (color == null) {
          colors.clear();
          break;
        }
        colors.add(color);
      }
    }
    if (colors.isEmpty()) {
      double[][] fallback = GraphicsOptions.SUNSET_COLORS;
      for (int i = 0; i < fallback.length; i++) {
        colors.add(new Color((float) fallback[i][0], (float) fallback[i][1], (float) fallback[i][2]));
      }
    }
    return colors;
  }
}
