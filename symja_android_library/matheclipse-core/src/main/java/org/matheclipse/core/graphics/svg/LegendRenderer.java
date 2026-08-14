package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.convert.RGBColor;
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
    drawListLegend(labels, markerSource, parent);
  }

  private void drawListLegend(List<String> labels, IExpr markerSource, ContainerTag<?> parent) {
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
      if (options.joined) {
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

  /** The gradient definition a bar legend paints itself with. */
  public static ContainerTag<?> barGradient(String gradientId) {
    ContainerTag<?> gradient = tag("linearGradient").attr("id", gradientId).attr("x1", "0%")
        .attr("y1", "100%").attr("x2", "0%").attr("y2", "0%");
    double[][] colors = GraphicsOptions.SUNSET_COLORS;
    int last = colors.length - 1;
    for (int i = 0; i < colors.length; i++) {
      String color = String.format(Locale.US, "rgb(%d,%d,%d)", (int) (colors[i][0] * 255),
          (int) (colors[i][1] * 255), (int) (colors[i][2] * 255));
      gradient.with(
          tag("stop").attr("offset", String.format(Locale.US, "%.1f%%", (double) i / last * 100.0))
              .attr("style", "stop-color:" + color + ";stop-opacity:1"));
    }
    return gradient;
  }
}
