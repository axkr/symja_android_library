package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;

/** Draws axes, frame, tick marks, tick labels and grid lines. */
public final class AxesFrameRenderer {

  private static final double TICK_LENGTH = 4.0;
  private static final double LABEL_GAP = 4.0;
  private static final double TICK_FONT_SIZE = 10.0;

  private final Viewport2D viewport;
  private final GraphicsOptions2D options;

  public AxesFrameRenderer(Viewport2D viewport, GraphicsOptions2D options) {
    this.viewport = viewport;
    this.options = options;
  }

  // ------------------------------------------------------------ grid lines

  public void drawGridLines(ContainerTag<?> parent) {
    IExpr spec = options.gridLines;
    if (spec == null || spec.isFalse() || spec.isNone()) {
      return;
    }
    IExpr xSpec = spec;
    IExpr ySpec = spec;
    if (spec.isList() && ((IAST) spec).argSize() == 2 && !isPlainValueList(spec)) {
      xSpec = ((IAST) spec).arg1();
      ySpec = ((IAST) spec).arg2();
    }
    String style = gridCss();
    for (double v : gridPositions(xSpec, true)) {
      double x = viewport.mapX(v);
      if (Double.isFinite(x) && x >= viewport.plotX1 - 0.5 && x <= viewport.plotX2 + 0.5) {
        parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(x))
            .attr("y1", SvgRenderer2D.fmt(viewport.plotY1)).attr("x2", SvgRenderer2D.fmt(x))
            .attr("y2", SvgRenderer2D.fmt(viewport.plotY2)).attr("style", style));
      }
    }
    for (double v : gridPositions(ySpec, false)) {
      double y = viewport.mapY(v);
      if (Double.isFinite(y) && y >= viewport.plotY1 - 0.5 && y <= viewport.plotY2 + 0.5) {
        parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(viewport.plotX1))
            .attr("y1", SvgRenderer2D.fmt(y)).attr("x2", SvgRenderer2D.fmt(viewport.plotX2))
            .attr("y2", SvgRenderer2D.fmt(y)).attr("style", style));
      }
    }
  }

  /** True for a list of bare numbers, which specifies positions for both axes at once. */
  private boolean isPlainValueList(IExpr spec) {
    IAST list = (IAST) spec;
    for (int i = 1; i <= list.argSize(); i++) {
      if (list.get(i).isList() || list.get(i) == S.Automatic || list.get(i).isNone()) {
        return false;
      }
    }
    return true;
  }

  private List<Double> gridPositions(IExpr spec, boolean isX) {
    List<Double> out = new ArrayList<>();
    if (spec == null || spec.isNone() || spec.isFalse()) {
      return out;
    }
    if (spec == S.Automatic || spec.isTrue()) {
      for (TickGenerator.Tick t : autoTicks(isX)) {
        out.add(t.value);
      }
      return out;
    }
    if (spec.isList()) {
      IAST list = (IAST) spec;
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr entry = list.get(i);
        // an entry may carry its own style, which the shared grid style stands in for
        IExpr value =
            entry.isList() && ((IAST) entry).argSize() >= 1 ? ((IAST) entry).arg1() : entry;
        double v = ColorUtil.dbl(value, Double.NaN);
        if (!Double.isNaN(v)) {
          out.add(v);
        }
      }
    }
    return out;
  }

  private String gridCss() {
    Style2D s = options.gridLinesStyle;
    StringBuilder sb = new StringBuilder();
    sb.append("stroke:").append(ColorUtil.css(s.strokeColor)).append(";stroke-width:")
        .append(SvgRenderer2D.fmt(s.strokeWidth)).append("px");
    double alpha = ColorUtil.alphaOf(s.strokeColor, s.opacity);
    if (alpha < 1.0) {
      sb.append(";stroke-opacity:").append(SvgRenderer2D.fmt(alpha));
    }
    if (!"none".equals(s.dashArray)) {
      sb.append(";stroke-dasharray:").append(s.dashArray);
    }
    return sb.toString();
  }

  // ----------------------------------------------------------------- axes

  public void drawAxes(ContainerTag<?> parent) {
    if (!options.axesX && !options.axesY) {
      return;
    }
    double originX = axisOriginX();
    double originY = axisOriginY();
    String lineStyle = axisCss(options.axesStyle);
    String textStyle = textCss(options.axesStyle);

    IExpr xTickSpec = S.Automatic;
    IExpr yTickSpec = S.Automatic;
    if (options.ticks.isList() && ((IAST) options.ticks).argSize() == 2) {
      xTickSpec = ((IAST) options.ticks).arg1();
      yTickSpec = ((IAST) options.ticks).arg2();
    } else if (options.ticks.isNone() || options.ticks.isFalse()) {
      xTickSpec = yTickSpec = S.None;
    }

    if (options.axesX) {
      parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(viewport.plotX1))
          .attr("y1", SvgRenderer2D.fmt(originY)).attr("x2", SvgRenderer2D.fmt(viewport.plotX2))
          .attr("y2", SvgRenderer2D.fmt(originY)).attr("style", lineStyle));
      drawXTicks(ticksFor(xTickSpec, true), originY, lineStyle, textStyle, true,
          !options.hasFrame(), parent);
    }
    if (options.axesY) {
      parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(originX))
          .attr("y1", SvgRenderer2D.fmt(viewport.plotY1)).attr("x2", SvgRenderer2D.fmt(originX))
          .attr("y2", SvgRenderer2D.fmt(viewport.plotY2)).attr("style", lineStyle));
      drawYTicks(ticksFor(yTickSpec, false), originX, lineStyle, textStyle, true,
          !options.hasFrame(), parent);
    }
  }

  private double axisOriginX() {
    double value;
    if (options.axesOrigin != null && !Double.isNaN(options.axesOrigin[0])) {
      value = viewport.mapX(options.axesOrigin[0]);
    } else {
      value = viewport.isLogX() ? viewport.plotX1 : viewport.mapX(0.0);
    }
    return clamp(value, viewport.plotX1, viewport.plotX2);
  }

  private double axisOriginY() {
    double value;
    if (options.axesOrigin != null && !Double.isNaN(options.axesOrigin[1])) {
      value = viewport.mapY(options.axesOrigin[1]);
    } else {
      value = viewport.isLogY() ? viewport.plotY2 : viewport.mapY(0.0);
    }
    return clamp(value, viewport.plotY1, viewport.plotY2);
  }

  private static double clamp(double v, double lo, double hi) {
    if (!Double.isFinite(v)) {
      return lo;
    }
    return Math.max(lo, Math.min(hi, v));
  }

  // ---------------------------------------------------------------- frame

  public void drawFrame(ContainerTag<?> parent) {
    if (!options.hasFrame()) {
      return;
    }
    String lineStyle = axisCss(options.frameStyle);
    String textStyle = textCss(options.frameStyle);
    double x1 = viewport.plotX1;
    double x2 = viewport.plotX2;
    double y1 = viewport.plotY1;
    double y2 = viewport.plotY2;

    if (options.frame[0]) {
      parent.with(edge(x1, y1, x1, y2, lineStyle));
    }
    if (options.frame[1]) {
      parent.with(edge(x2, y1, x2, y2, lineStyle));
    }
    if (options.frame[2]) {
      parent.with(edge(x1, y2, x2, y2, lineStyle));
    }
    if (options.frame[3]) {
      parent.with(edge(x1, y1, x2, y1, lineStyle));
    }

    if (options.frameTicks.isNone() || options.frameTicks.isFalse()) {
      return;
    }
    // Only the bottom and left edges are numbered, unless the caller named ticks for the far
    // edges too, as a plot that wants a scale on all four sides does.
    boolean farSideLabels = !options.frameTicks.isAutomatic();
    if (options.frame[2]) {
      drawXTicks(ticksFor(frameTickSpec(2), true), y2, lineStyle, textStyle, true, true, parent);
    }
    if (options.frame[3]) {
      drawXTicks(ticksFor(frameTickSpec(3), true), y1, lineStyle, textStyle, false, farSideLabels,
          parent);
    }
    if (options.frame[0]) {
      drawYTicks(ticksFor(frameTickSpec(0), false), x1, lineStyle, textStyle, true, true, parent);
    }
    if (options.frame[1]) {
      drawYTicks(ticksFor(frameTickSpec(1), false), x2, lineStyle, textStyle, false, farSideLabels,
          parent);
    }
  }

  private ContainerTag<?> edge(double x1, double y1, double x2, double y2, String style) {
    return tag("line").attr("x1", SvgRenderer2D.fmt(x1)).attr("y1", SvgRenderer2D.fmt(y1))
        .attr("x2", SvgRenderer2D.fmt(x2)).attr("y2", SvgRenderer2D.fmt(y2)).attr("style", style);
  }

  /** The tick specification for one frame edge, indexed left, right, bottom, top. */
  private IExpr frameTickSpec(int edge) {
    IExpr spec = options.frameTicks;
    if (spec.isList() && ((IAST) spec).argSize() == 2) {
      IAST outer = (IAST) spec;
      if (outer.arg1().isList() && outer.arg2().isList() && ((IAST) outer.arg1()).argSize() == 2
          && ((IAST) outer.arg2()).argSize() == 2) {
        // {{left, right}, {bottom, top}}
        IAST lr = (IAST) outer.arg1();
        IAST bt = (IAST) outer.arg2();
        switch (edge) {
          case 0:
            return lr.arg1();
          case 1:
            return lr.arg2();
          case 2:
            return bt.arg1();
          default:
            return bt.arg2();
        }
      }
      // {xspec, yspec}
      return edge <= 1 ? outer.arg2() : outer.arg1();
    }
    return spec;
  }

  // ---------------------------------------------------------------- ticks

  private List<TickGenerator.Tick> autoTicks(boolean isX) {
    if (isX) {
      return viewport.isLogX() ? TickGenerator.logarithmic(viewport.rawMinX, viewport.rawMaxX)
          : TickGenerator.linear(viewport.minX, viewport.maxX);
    }
    return viewport.isLogY() ? TickGenerator.logarithmic(viewport.rawMinY, viewport.rawMaxY)
        : TickGenerator.linear(viewport.minY, viewport.maxY);
  }

  private List<TickGenerator.Tick> ticksFor(IExpr spec, boolean isX) {
    if (spec == null || spec == S.Automatic || spec.isTrue()) {
      return autoTicks(isX);
    }
    if (spec.isNone() || spec.isFalse()) {
      return new ArrayList<>();
    }
    List<TickGenerator.Tick> explicit = TickGenerator.explicit(spec);
    return explicit == null ? autoTicks(isX) : explicit;
  }

  private void drawXTicks(List<TickGenerator.Tick> ticks, double y, String lineStyle,
      String textStyle, boolean below, boolean labels, ContainerTag<?> parent) {
    double tickDir = below ? -TICK_LENGTH : TICK_LENGTH;
    double labelDy =
        below ? TICK_LENGTH + LABEL_GAP + TICK_FONT_SIZE * 0.8 : -(TICK_LENGTH + LABEL_GAP);
    for (TickGenerator.Tick t : ticks) {
      double x = viewport.mapX(t.value);
      if (!Double.isFinite(x) || x < viewport.plotX1 - 0.5 || x > viewport.plotX2 + 0.5) {
        continue;
      }
      parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(x)).attr("y1", SvgRenderer2D.fmt(y))
          .attr("x2", SvgRenderer2D.fmt(x)).attr("y2", SvgRenderer2D.fmt(y - tickDir))
          .attr("style", lineStyle));
      if (labels && t.major && !t.label.isEmpty()) {
        parent.with(label(x, y + labelDy, t.label, textStyle + ";text-anchor:middle"));
      }
    }
  }

  private void drawYTicks(List<TickGenerator.Tick> ticks, double x, String lineStyle,
      String textStyle, boolean left, boolean labels, ContainerTag<?> parent) {
    double tickDir = left ? -TICK_LENGTH : TICK_LENGTH;
    double labelDx = left ? -(TICK_LENGTH + LABEL_GAP) : TICK_LENGTH + LABEL_GAP;
    String anchor = left ? "end" : "start";
    for (TickGenerator.Tick t : ticks) {
      double y = viewport.mapY(t.value);
      if (!Double.isFinite(y) || y < viewport.plotY1 - 0.5 || y > viewport.plotY2 + 0.5) {
        continue;
      }
      parent.with(tag("line").attr("x1", SvgRenderer2D.fmt(x)).attr("y1", SvgRenderer2D.fmt(y))
          .attr("x2", SvgRenderer2D.fmt(x + tickDir)).attr("y2", SvgRenderer2D.fmt(y))
          .attr("style", lineStyle));
      if (labels && t.major && !t.label.isEmpty()) {
        parent.with(label(x + labelDx, y + TICK_FONT_SIZE * 0.35, t.label,
            textStyle + ";text-anchor:" + anchor));
      }
    }
  }

  /** A tick label, rendered with a superscript when it is a power of ten. */
  private ContainerTag<?> label(double x, double y, String text, String style) {
    ContainerTag<?> tag = tag("text").attr("x", SvgRenderer2D.fmt(x))
        .attr("y", SvgRenderer2D.fmt(y)).attr("style", style);
    if (TickGenerator.isPowerLabel(text)) {
      return tag.with(rawHtml("10<tspan dy=\"-0.6em\" font-size=\"70%\">"
          + TickGenerator.powerExponent(text) + "</tspan>"));
    }
    return tag.withText(text);
  }

  // ---------------------------------------------------------------- styles

  private String axisCss(Style2D style) {
    StringBuilder sb = new StringBuilder();
    sb.append("stroke:").append(ColorUtil.css(style.strokeColor)).append(";stroke-width:")
        .append(SvgRenderer2D.fmt(Math.max(0.5, style.strokeWidth))).append("px;fill:none");
    double alpha = ColorUtil.alphaOf(style.strokeColor, style.opacity);
    if (alpha < 1.0) {
      sb.append(";stroke-opacity:").append(SvgRenderer2D.fmt(alpha));
    }
    if (!"none".equals(style.dashArray)) {
      sb.append(";stroke-dasharray:").append(style.dashArray);
    }
    return sb.toString();
  }

  private String textCss(Style2D style) {
    // LabelStyle covers the tick labels too, so it overrides the axis or frame style here
    Style2D label = options.labelStyle;
    Style2D effective = label != null ? label : style;
    StringBuilder sb = new StringBuilder();
    sb.append(String.format(Locale.US, "fill:%s;font-family:%s;font-size:%spx",
        ColorUtil.css(effective.strokeColor), effective.fontFamily,
        SvgRenderer2D.fmt(tickFontSize(options))));
    if (label != null) {
      if ("bold".equals(label.fontWeight)) {
        sb.append(";font-weight:bold");
      }
      if ("italic".equals(label.fontStyle)) {
        sb.append(";font-style:italic");
      }
    }
    return sb.toString();
  }

  /** Size of the tick labels, which {@code LabelStyle} may have asked to change. */
  private static double tickFontSize(GraphicsOptions2D options) {
    return options.labelFontSizeSet ? options.labelStyle.fontSize : TICK_FONT_SIZE;
  }

  /** Widest tick label on the y axis, used to reserve left padding. */
  public static double estimateYLabelWidth(Viewport2D viewport, GraphicsOptions2D options) {
    List<TickGenerator.Tick> ticks =
        viewport.isLogY() ? TickGenerator.logarithmic(viewport.rawMinY, viewport.rawMaxY)
            : TickGenerator.linear(viewport.minY, viewport.maxY);
    double widest = 0;
    for (TickGenerator.Tick t : ticks) {
      widest = Math.max(widest, SvgRenderer2D.estimateTextWidth(t.label, tickFontSize(options)));
    }
    return widest;
  }
}
