package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import j2html.tags.ContainerTag;

/** Turns {@link Prim2D} objects into SVG elements. */
public final class SvgRenderer2D {

  /**
   * Upper bound on the number of rectangles a single raster may emit. A raster that would need more
   * than this is drawn as an embedded bitmap instead.
   *
   * <p>
   * The limit is low because a rectangle costs about eighty characters while a pixel of a
   * compressed bitmap costs well under one: past a few hundred shapes the bitmap is smaller by an
   * order of magnitude and looks the same, since it is drawn unsmoothed at the resolution of the
   * cells. What the rectangles are still better at is the small flat picture, which stays crisp at
   * any zoom and costs almost nothing either way.
   */
  private static final int MAX_RASTER_RECTS = 1000;

  /**
   * Upper bound on the number of pixels the embedded bitmap may hold. Only a raster with more cells
   * than this loses any of them, which no plot produces on its own: it is a guard against an
   * arbitrarily large {@code Raster} written out by hand.
   */
  private static final int MAX_RASTER_PIXELS = 4_000_000;

  private final Viewport2D viewport;
  private final GraphicsOptions2D options;

  public SvgRenderer2D(Viewport2D viewport, GraphicsOptions2D options) {
    this.viewport = viewport;
    this.options = options;
  }

  public Viewport2D viewport() {
    return viewport;
  }

  /** Compact number formatting: integers print without a decimal point. */
  public static String fmt(double d) {
    if (!Double.isFinite(d)) {
      return "0";
    }
    if (Math.abs(d - Math.round(d)) < 1e-9) {
      return Long.toString(Math.round(d));
    }
    return String.format(Locale.US, "%.3f", d);
  }

  // --------------------------------------------------------------- styling

  /**
   * Apply fill and stroke to an element whose stroke is the outline of a filled shape.
   *
   * <p>
   * An outline keeps its own transparency: {@code Opacity} tints the face and leaves the frame
   * around it alone.
   */
  private void paintEdged(ContainerTag<?> tag, Style2D style, Color fill, Color stroke) {
    paint(tag, style, fill, stroke, true);
  }

  /**
   * Apply fill and stroke to an element.
   *
   * @param fill the fill colour, or {@code null} for no fill
   * @param stroke the stroke colour, or {@code null} for no stroke
   */
  private void paint(ContainerTag<?> tag, Style2D style, Color fill, Color stroke) {
    paint(tag, style, fill, stroke, false);
  }

  /**
   * Apply fill and stroke to an element.
   *
   * @param fill the fill colour, or {@code null} for no fill
   * @param stroke the stroke colour, or {@code null} for no stroke
   * @param strokeIsEdge whether the stroke is an {@code EdgeForm} outline rather than the
   *        primitive's own line, which decides whose transparency it takes
   */
  private void paint(ContainerTag<?> tag, Style2D style, Color fill, Color stroke,
      boolean strokeIsEdge) {
    double fillAlpha = ColorUtil.alphaOf(fill, style.opacity);
    double strokeAlpha =
        ColorUtil.alphaOf(stroke, strokeIsEdge ? style.edgeOpacity : style.opacity);
    tag.attr("fill", fill == null ? "none" : ColorUtil.css(fill));
    if (fill != null && fillAlpha < 1.0) {
      tag.attr("fill-opacity", fmt(fillAlpha));
    }
    if (stroke == null) {
      tag.attr("stroke", "none");
      return;
    }
    tag.attr("stroke", ColorUtil.css(stroke));
    tag.attr("stroke-width", fmt(Math.max(0.0, style.strokeWidth)));
    if (strokeAlpha < 1.0) {
      tag.attr("stroke-opacity", fmt(strokeAlpha));
    }
    if (!"none".equals(style.dashArray)) {
      tag.attr("stroke-dasharray", style.dashArray);
    }
    if (!"butt".equals(style.lineCap)) {
      tag.attr("stroke-linecap", style.lineCap);
    }
    if (!"miter".equals(style.lineJoin)) {
      tag.attr("stroke-linejoin", style.lineJoin);
    }
  }

  /** The stroke colour of a filled shape: the edge form when set, otherwise nothing. */
  private Color edgeStroke(Style2D style) {
    if (style.edgeFormSet) {
      return style.edgeColor;
    }
    return null;
  }

  // -------------------------------------------------------------- dispatch

  public void draw(List<Prim2D> primitives, ContainerTag<?> parent) {
    for (Prim2D p : primitives) {
      try {
        String tooltip = p.style.tooltip;
        if (tooltip == null || tooltip.isEmpty()) {
          p.render(this, parent);
        } else {
          // an SVG <title> is what a viewer shows on hover, and it has to be a child of the
          // element it describes - so the primitive gets a group of its own to hang it on
          ContainerTag<?> group = tag("g").with(tag("title").withText(tooltip));
          p.render(this, group);
          parent.with(group);
        }
      } catch (RuntimeException rex) {
        // one primitive failing must not cost the rest of the picture
      }
    }
  }

  // ---------------------------------------------------------------- points

  void drawPoints(Prim2D.PointsPrim prim, ContainerTag<?> parent) {
    double r = Math.max(0.5, prim.style.pointRadius);
    for (double[] p : prim.points) {
      double x = viewport.mapX(p[0]);
      double y = viewport.mapY(p[1]);
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        continue;
      }
      ContainerTag<?> circle =
          tag("circle").attr("cx", fmt(x)).attr("cy", fmt(y)).attr("r", fmt(r));
      paint(circle, prim.style, prim.style.strokeColor, null);
      parent.with(circle);
    }
  }

  // ----------------------------------------------------------------- lines

  void drawLine(Prim2D.LinePrim prim, ContainerTag<?> parent) {
    StringBuilder d = new StringBuilder();
    for (List<double[]> segment : prim.segments) {
      appendPolyline(d, segment, prim.closed);
    }
    if (d.length() == 0) {
      return;
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString().trim());
    paint(path, prim.style, null, prim.style.strokeColor);
    parent.with(path);
  }

  /** Append a polyline, starting a new subpath wherever a point is not finite. */
  private void appendPolyline(StringBuilder d, List<double[]> points, boolean close) {
    boolean pending = true;
    boolean any = false;
    for (double[] p : points) {
      double x = viewport.mapX(p[0]);
      double y = viewport.mapY(p[1]);
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        pending = true;
        continue;
      }
      d.append(pending ? "M " : "L ").append(fmt(x)).append(' ').append(fmt(y)).append(' ');
      pending = false;
      any = true;
    }
    if (any && close) {
      d.append("Z ");
    }
  }

  // -------------------------------------------------------------- polygons

  void drawPolygon(Prim2D.PolygonPrim prim, ContainerTag<?> parent) {
    StringBuilder d = new StringBuilder();
    appendPolyline(d, prim.outer, true);
    for (List<double[]> hole : prim.holes) {
      appendPolyline(d, hole, true);
    }
    if (d.length() == 0) {
      return;
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString().trim());
    if (!prim.holes.isEmpty()) {
      path.attr("fill-rule", "evenodd");
    }
    paintEdged(path, prim.style, prim.style.effectiveFill(), edgeStroke(prim.style));
    parent.with(path);
  }

  // ------------------------------------------------------------- rectangle

  void drawRect(Prim2D.RectPrim prim, ContainerTag<?> parent) {
    double x1 = viewport.mapX(prim.x1);
    double x2 = viewport.mapX(prim.x2);
    double y1 = viewport.mapY(prim.y2);
    double y2 = viewport.mapY(prim.y1);
    if (!Double.isFinite(x1) || !Double.isFinite(x2) || !Double.isFinite(y1)
        || !Double.isFinite(y2)) {
      return;
    }
    ContainerTag<?> rect =
        tag("rect").attr("x", fmt(Math.min(x1, x2))).attr("y", fmt(Math.min(y1, y2)))
            .attr("width", fmt(Math.abs(x2 - x1))).attr("height", fmt(Math.abs(y2 - y1)));
    if (prim.rounding > 0) {
      rect.attr("rx", fmt(Math.abs(viewport.lengthX(prim.rounding))));
      rect.attr("ry", fmt(Math.abs(viewport.lengthY(prim.rounding))));
    }
    Color stroke = edgeStroke(prim.style);
    Color fill = prim.style.effectiveFill();
    paintEdged(rect, prim.style, fill, stroke);
    if (stroke == null && fill != null && fill.getAlpha() > 0) {
      // abutting cells of a matrix plot otherwise show hairline seams
      rect.attr("shape-rendering", "crispEdges");
    }
    parent.with(rect);
  }

  // --------------------------------------------------------------- ellipse

  void drawEllipse(Prim2D.EllipsePrim prim, ContainerTag<?> parent) {
    double cx = viewport.mapX(prim.cx);
    double cy = viewport.mapY(prim.cy);
    double rx = Math.abs(viewport.lengthX(prim.rx));
    double ry = Math.abs(viewport.lengthY(prim.ry));
    if (!Double.isFinite(cx) || !Double.isFinite(cy) || rx <= 0 && ry <= 0) {
      return;
    }
    Color fill = prim.filled ? prim.style.effectiveFill() : null;
    Color stroke = prim.filled ? edgeStroke(prim.style) : prim.style.strokeColor;

    boolean rotated = Math.abs(prim.rotation) > 1e-9;
    boolean anisotropic = Math.abs(viewport.scaleX - viewport.scaleY) > 1e-9;
    if (rotated && anisotropic) {
      // a rotated ellipse under different axis scales is no longer an SVG ellipse
      drawFlattened(prim, fill, stroke, parent);
      return;
    }

    if (prim.isFullTurn() && !prim.isAnnulus()) {
      ContainerTag<?> ellipse = tag("ellipse").attr("cx", fmt(cx)).attr("cy", fmt(cy))
          .attr("rx", fmt(rx)).attr("ry", fmt(ry));
      if (rotated) {
        ellipse.attr("transform", String.format(Locale.US, "rotate(%.4f %s %s)",
            -Math.toDegrees(prim.rotation), fmt(cx), fmt(cy)));
      }
      paint(ellipse, prim.style, fill, stroke, prim.filled);
      parent.with(ellipse);
      return;
    }

    String d = ellipsePath(prim, cx, cy, rx, ry);
    if (d.isEmpty()) {
      return;
    }
    ContainerTag<?> path = tag("path").attr("d", d);
    if (prim.isAnnulus() && prim.isFullTurn()) {
      path.attr("fill-rule", "evenodd");
    }
    if (rotated) {
      path.attr("transform", String.format(Locale.US, "rotate(%.4f %s %s)",
          -Math.toDegrees(prim.rotation), fmt(cx), fmt(cy)));
    }
    paint(path, prim.style, fill, stroke, prim.filled);
    parent.with(path);
  }

  private void drawFlattened(Prim2D.EllipsePrim prim, Color fill, Color stroke,
      ContainerTag<?> parent) {
    StringBuilder d = new StringBuilder();
    appendPolyline(d, prim.flatten(72), prim.filled || !prim.isFullTurn());
    if (d.length() == 0) {
      return;
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString().trim());
    paint(path, prim.style, fill, stroke, prim.filled);
    parent.with(path);
  }

  /**
   * The path of an elliptical arc, sector or ring segment.
   *
   * <p>
   * Angles run counterclockwise in data coordinates, and the pixel y axis points the other way, so
   * an increasing angle is a clockwise sweep on screen.
   */
  private String ellipsePath(Prim2D.EllipsePrim prim, double cx, double cy, double rx, double ry) {
    double a0 = prim.angles == null ? 0 : prim.angles[0];
    double a1 = prim.angles == null ? 2 * Math.PI : prim.angles[1];
    double innerRx = Math.abs(viewport.lengthX(prim.innerRx));
    double innerRy = Math.abs(viewport.lengthY(prim.innerRy));
    StringBuilder d = new StringBuilder();

    if (prim.isFullTurn()) {
      // a full ring: the outer circle, then the inner one, filled with the even-odd rule
      d.append(fullEllipsePath(cx, cy, rx, ry));
      if (prim.isAnnulus()) {
        d.append(fullEllipsePath(cx, cy, innerRx, innerRy));
      }
      return d.toString().trim();
    }

    double sweep = a1 - a0;
    int largeArc = Math.abs(sweep) > Math.PI ? 1 : 0;
    int sweepFlag = sweep > 0 ? 0 : 1;

    double[] outerStart = onEllipse(cx, cy, rx, ry, a0);
    double[] outerEnd = onEllipse(cx, cy, rx, ry, a1);

    if (prim.isAnnulus()) {
      double[] innerEnd = onEllipse(cx, cy, innerRx, innerRy, a1);
      double[] innerStart = onEllipse(cx, cy, innerRx, innerRy, a0);
      d.append("M ").append(fmt(outerStart[0])).append(' ').append(fmt(outerStart[1])).append(' ');
      appendArc(d, rx, ry, largeArc, sweepFlag, outerEnd);
      d.append("L ").append(fmt(innerEnd[0])).append(' ').append(fmt(innerEnd[1])).append(' ');
      appendArc(d, innerRx, innerRy, largeArc, 1 - sweepFlag, innerStart);
      d.append("Z");
      return d.toString();
    }

    if (prim.filled) {
      // a sector is closed through the centre
      d.append("M ").append(fmt(cx)).append(' ').append(fmt(cy)).append(' ');
      d.append("L ").append(fmt(outerStart[0])).append(' ').append(fmt(outerStart[1])).append(' ');
      appendArc(d, rx, ry, largeArc, sweepFlag, outerEnd);
      d.append("Z");
      return d.toString();
    }

    // a bare arc stays open
    d.append("M ").append(fmt(outerStart[0])).append(' ').append(fmt(outerStart[1])).append(' ');
    appendArc(d, rx, ry, largeArc, sweepFlag, outerEnd);
    return d.toString().trim();
  }

  private String fullEllipsePath(double cx, double cy, double rx, double ry) {
    // two half arcs, since a single arc command cannot close a full turn
    return String.format(Locale.US, "M %s %s A %s %s 0 1 0 %s %s A %s %s 0 1 0 %s %s Z ",
        fmt(cx - rx), fmt(cy), fmt(rx), fmt(ry), fmt(cx + rx), fmt(cy), fmt(rx), fmt(ry),
        fmt(cx - rx), fmt(cy));
  }

  private void appendArc(StringBuilder d, double rx, double ry, int largeArc, int sweepFlag,
      double[] end) {
    d.append("A ").append(fmt(rx)).append(' ').append(fmt(ry)).append(" 0 ").append(largeArc)
        .append(' ').append(sweepFlag).append(' ').append(fmt(end[0])).append(' ')
        .append(fmt(end[1])).append(' ');
  }

  private double[] onEllipse(double cx, double cy, double rx, double ry, double angle) {
    // the pixel y axis is inverted, hence the minus on the sine
    return new double[] {cx + rx * Math.cos(angle), cy - ry * Math.sin(angle)};
  }

  // ------------------------------------------------------------------ text

  void drawText(Prim2D.TextPrim prim, ContainerTag<?> parent) {
    double x = viewport.mapX(prim.x);
    double y = viewport.mapY(prim.y);
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return;
    }
    Style2D style = prim.style;
    String anchor = prim.offsetX <= -0.5 ? "start" : prim.offsetX >= 0.5 ? "end" : "middle";
    // move the baseline so that the requested point of the label box lands on the anchor
    double dy = (0.32 + 0.5 * prim.offsetY) * style.fontSize;

    if (prim.background != null || prim.frameColor != null) {
      double width = estimateTextWidth(prim.text, style.fontSize);
      double height = style.fontSize * 1.3;
      double left = "start".equals(anchor) ? x : "end".equals(anchor) ? x - width : x - width / 2;
      double top = y + dy - style.fontSize * 0.85;
      ContainerTag<?> box = tag("rect").attr("x", fmt(left - 3)).attr("y", fmt(top - 2))
          .attr("width", fmt(width + 6)).attr("height", fmt(height + 4))
          .attr("fill", prim.background == null ? "none" : ColorUtil.css(prim.background))
          .attr("stroke", prim.frameColor == null ? "none" : ColorUtil.css(prim.frameColor));
      parent.with(box);
    }

    ContainerTag<?> text = tag("text").attr("x", fmt(x)).attr("y", fmt(y + dy))
        .attr("fill", ColorUtil.css(style.strokeColor)).attr("font-family", style.fontFamily)
        .attr("font-size", fmt(style.fontSize)).attr("text-anchor", anchor);
    double alpha = ColorUtil.alphaOf(style.strokeColor, style.opacity);
    if (alpha < 1.0) {
      text.attr("fill-opacity", fmt(alpha));
    }
    if (!"normal".equals(style.fontWeight)) {
      text.attr("font-weight", style.fontWeight);
    }
    if (!"normal".equals(style.fontStyle)) {
      text.attr("font-style", style.fontStyle);
    }
    if (style.textDecoration != null) {
      text.attr("text-decoration", style.textDecoration);
    }
    if (prim.dirX != 1 || prim.dirY != 0) {
      double angle = -Math.toDegrees(Math.atan2(prim.dirY, prim.dirX));
      text.attr("transform",
          String.format(Locale.US, "rotate(%.3f %s %s)", angle, fmt(x), fmt(y + dy)));
    }
    text.withText(prim.text);
    parent.with(text);
  }

  static double estimateTextWidth(String text, double fontSize) {
    return text == null ? 0 : text.length() * fontSize * 0.6;
  }

  // ----------------------------------------------------------------- arrow

  void drawArrow(Prim2D.ArrowPrim prim, ContainerTag<?> parent) {
    List<double[]> pixels = new ArrayList<>(prim.points.size());
    for (double[] p : prim.points) {
      double x = viewport.mapX(p[0]);
      double y = viewport.mapY(p[1]);
      if (Double.isFinite(x) && Double.isFinite(y)) {
        pixels.add(new double[] {x, y});
      }
    }
    if (pixels.size() < 2) {
      return;
    }
    double setbackStart = Math.abs(viewport.lengthX(prim.setbackStart));
    double setbackEnd = Math.abs(viewport.lengthX(prim.setbackEnd));
    if (setbackStart > 0) {
      trimFromStart(pixels, setbackStart);
    }
    if (setbackEnd > 0) {
      java.util.Collections.reverse(pixels);
      trimFromStart(pixels, setbackEnd);
      java.util.Collections.reverse(pixels);
    }
    if (pixels.size() < 2) {
      return;
    }

    StringBuilder d = new StringBuilder();
    for (int i = 0; i < pixels.size(); i++) {
      d.append(i == 0 ? "M " : "L ").append(fmt(pixels.get(i)[0])).append(' ')
          .append(fmt(pixels.get(i)[1])).append(' ');
    }
    ContainerTag<?> shaft = tag("path").attr("d", d.toString().trim());
    paint(shaft, prim.style, null, prim.style.strokeColor);
    parent.with(shaft);

    List<Style2D.ArrowHead> heads = prim.style.arrowHeads;
    if (heads == null) {
      drawArrowHead(pixels, 1.0, false, prim.style.arrowHeadScale * options.imageSize[0],
          prim.style, parent);
      return;
    }
    for (Style2D.ArrowHead head : heads) {
      drawArrowHead(pixels, head.position, head.reversed, head.size * options.imageSize[0],
          prim.style, parent);
    }
  }

  /** Remove {@code distance} pixels of path from the front of {@code pixels}. */
  private void trimFromStart(List<double[]> pixels, double distance) {
    double remaining = distance;
    while (pixels.size() >= 2) {
      double[] a = pixels.get(0);
      double[] b = pixels.get(1);
      double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
      if (len > remaining) {
        double t = remaining / len;
        pixels.set(0, new double[] {a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t});
        return;
      }
      remaining -= len;
      pixels.remove(0);
    }
  }

  /**
   * @param position where the head sits along the path, 0 at the tail and 1 at the tip
   * @param reversed whether the head points back along the path
   */
  private void drawArrowHead(List<double[]> pixels, double position, boolean reversed, double size,
      Style2D style, ContainerTag<?> parent) {
    double t = Math.max(0.0, Math.min(1.0, position));
    double[] tip = pointAlong(pixels, t);
    double[] direction = directionAlong(pixels, t);
    if (tip == null || direction == null) {
      return;
    }
    double angle = Math.atan2(direction[1], direction[0]);
    if (reversed) {
      angle += Math.PI;
    }
    double x1 = tip[0] - size * Math.cos(angle - Math.PI / 7);
    double y1 = tip[1] - size * Math.sin(angle - Math.PI / 7);
    double x2 = tip[0] - size * Math.cos(angle + Math.PI / 7);
    double y2 = tip[1] - size * Math.sin(angle + Math.PI / 7);
    String points = fmt(tip[0]) + "," + fmt(tip[1]) + " " + fmt(x1) + "," + fmt(y1) + " " + fmt(x2)
        + "," + fmt(y2);
    ContainerTag<?> head = tag("polygon").attr("points", points);
    paint(head, style, style.strokeColor, null);
    parent.with(head);
  }

  private double[] pointAlong(List<double[]> pixels, double t) {
    double total = pathLength(pixels);
    if (total <= 0) {
      return pixels.get(pixels.size() - 1);
    }
    double target = total * t;
    double travelled = 0;
    for (int i = 0; i + 1 < pixels.size(); i++) {
      double[] a = pixels.get(i);
      double[] b = pixels.get(i + 1);
      double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
      if (travelled + len >= target) {
        double u = len <= 0 ? 0 : (target - travelled) / len;
        return new double[] {a[0] + (b[0] - a[0]) * u, a[1] + (b[1] - a[1]) * u};
      }
      travelled += len;
    }
    return pixels.get(pixels.size() - 1);
  }

  private double[] directionAlong(List<double[]> pixels, double t) {
    double total = pathLength(pixels);
    if (total <= 0) {
      return null;
    }
    double target = total * t;
    double travelled = 0;
    for (int i = 0; i + 1 < pixels.size(); i++) {
      double[] a = pixels.get(i);
      double[] b = pixels.get(i + 1);
      double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
      if (travelled + len >= target || i + 2 == pixels.size()) {
        if (len <= 0) {
          continue;
        }
        return new double[] {(b[0] - a[0]) / len, (b[1] - a[1]) / len};
      }
      travelled += len;
    }
    return null;
  }

  private double pathLength(List<double[]> pixels) {
    double total = 0;
    for (int i = 0; i + 1 < pixels.size(); i++) {
      total += Math.hypot(pixels.get(i + 1)[0] - pixels.get(i)[0],
          pixels.get(i + 1)[1] - pixels.get(i)[1]);
    }
    return total;
  }

  // ---------------------------------------------------------------- curves

  void drawBezier(Prim2D.BezierPrim prim, ContainerTag<?> parent) {
    List<double[]> pts = prim.points;
    if (pts.isEmpty()) {
      return;
    }
    StringBuilder d = new StringBuilder();
    double[] p0 = pts.get(0);
    d.append("M ").append(fmt(viewport.mapX(p0[0]))).append(' ').append(fmt(viewport.mapY(p0[1])))
        .append(' ');
    int i = 1;
    while (i < pts.size()) {
      if (prim.degree == 2) {
        if (i + 1 >= pts.size()) {
          break;
        }
        appendCurve(d, "Q", pts.get(i), pts.get(i + 1));
        i += 2;
      } else {
        if (i + 2 >= pts.size()) {
          // fewer control points than the degree needs: finish with a straight run
          for (; i < pts.size(); i++) {
            d.append("L ").append(fmt(viewport.mapX(pts.get(i)[0]))).append(' ')
                .append(fmt(viewport.mapY(pts.get(i)[1]))).append(' ');
          }
          break;
        }
        appendCurve(d, "C", pts.get(i), pts.get(i + 1), pts.get(i + 2));
        i += 3;
      }
    }
    if (prim.filled) {
      d.append("Z");
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString().trim());
    paint(path, prim.style, prim.filled ? prim.style.effectiveFill() : null,
        prim.filled ? edgeStroke(prim.style) : prim.style.strokeColor, prim.filled);
    parent.with(path);
  }

  private void appendCurve(StringBuilder d, String command, double[]... controls) {
    d.append(command).append(' ');
    for (double[] c : controls) {
      d.append(fmt(viewport.mapX(c[0]))).append(' ').append(fmt(viewport.mapY(c[1]))).append(' ');
    }
  }

  void drawBSpline(Prim2D.BSplinePrim prim, ContainerTag<?> parent) {
    StringBuilder d = new StringBuilder();
    appendPolyline(d, prim.curve, prim.closed || prim.filled);
    if (d.length() == 0) {
      return;
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString().trim());
    paint(path, prim.style, prim.filled ? prim.style.effectiveFill() : null,
        prim.filled ? edgeStroke(prim.style) : prim.style.strokeColor, prim.filled);
    parent.with(path);
  }

  // ---------------------------------------------------------------- raster

  void drawRaster(Prim2D.RasterPrim prim, ContainerTag<?> parent) {
    int rows = prim.cells.length;
    if (rows == 0) {
      return;
    }
    int cols = 0;
    for (Color[] row : prim.cells) {
      cols = Math.max(cols, row.length);
    }
    if (cols == 0) {
      return;
    }
    // A cell grid is cheapest as rectangles while equal neighbours can be merged into one, which
    // is what the plots that paint a few flat regions produce. A gradient merges into nothing, so
    // it is the run count and not the cell count that decides: a large two colour array still
    // draws as a handful of rectangles, and only a picture that genuinely needs one shape per cell
    // is handed to the bitmap.
    if (rasterRuns(prim.cells, rows, cols) > MAX_RASTER_RECTS) {
      drawRasterImage(prim, parent, rows, cols);
      return;
    }

    double x0 = viewport.mapX(prim.x1);
    double x1 = viewport.mapX(prim.x2);
    double y0 = viewport.mapY(prim.y1);
    double y1 = viewport.mapY(prim.y2);
    double cellW = (x1 - x0) / cols;
    double cellH = (y0 - y1) / rows;
    if (!Double.isFinite(cellW) || !Double.isFinite(cellH)) {
      return;
    }

    ContainerTag<?> group = tag("g").attr("shape-rendering", "crispEdges");
    for (int r = 0; r < rows; r++) {
      Color[] row = prim.cells[r];
      // merge runs of equal colour into one rectangle
      int runStart = 0;
      Color runColor = null;
      for (int c = 0; c <= cols; c++) {
        Color color = c < cols && c < row.length ? row[c] : null;
        if (runColor != null && (color == null || !runColor.equals(color))) {
          emitRasterRun(group, x0, y0, cellW, cellH, runStart, c, r, runColor);
          runColor = null;
        }
        if (color != null && runColor == null) {
          runStart = c;
          runColor = color;
        }
      }
    }
    parent.with(group);
  }

  /** The number of rectangles {@link #drawRaster} would emit for these cells. */
  private static int rasterRuns(Color[][] cells, int rows, int cols) {
    int runs = 0;
    for (int r = 0; r < rows; r++) {
      Color[] row = cells[r];
      Color runColor = null;
      for (int c = 0; c <= cols; c++) {
        Color color = c < cols && c < row.length ? row[c] : null;
        if (runColor != null && (color == null || !runColor.equals(color))) {
          // a fully transparent run is not drawn, so it does not count against the budget
          if (runColor.getAlpha() != 0) {
            runs++;
            if (runs > MAX_RASTER_RECTS) {
              return runs;
            }
          }
          runColor = null;
        }
        if (color != null && runColor == null) {
          runColor = color;
        }
      }
    }
    return runs;
  }

  /**
   * Draw the cells as one embedded bitmap.
   *
   * <p>
   * This is what keeps a plot of a smooth function to a readable size: every cell of such a raster
   * has its own colour, so as rectangles it is both enormous and past the element limit of the
   * rasterizer, while as a PNG it is an image that compresses. It also draws every cell, where the
   * rectangles used to be thinned out once there were too many of them.
   */
  private void drawRasterImage(Prim2D.RasterPrim prim, ContainerTag<?> parent, int rows,
      int cols) {
    int rowStep = 1;
    int colStep = 1;
    while ((long) (rows / rowStep) * (cols / colStep) > MAX_RASTER_PIXELS) {
      if (rows / rowStep >= cols / colStep) {
        rowStep++;
      } else {
        colStep++;
      }
    }
    int width = Math.max(1, cols / colStep);
    int height = Math.max(1, rows / rowStep);

    int[] argb = new int[width * height];
    for (int y = 0; y < height; y++) {
      // the cells are given bottom row first, a bitmap is written top row first
      Color[] row = prim.cells[rows - 1 - y * rowStep];
      int at = y * width;
      for (int x = 0; x < width; x++) {
        int c = x * colStep;
        Color color = c < row.length ? row[c] : null;
        argb[at + x] = color == null ? 0 : color.getRGB();
      }
    }

    double xa = viewport.mapX(prim.x1);
    double xb = viewport.mapX(prim.x2);
    double ya = viewport.mapY(prim.y1);
    double yb = viewport.mapY(prim.y2);
    double boxWidth = Math.abs(xb - xa);
    double boxHeight = Math.abs(ya - yb);
    if (!(boxWidth > 0) || !(boxHeight > 0)) {
      return;
    }

    parent.with(tag("image") //
        .attr("x", fmt(Math.min(xa, xb))) //
        .attr("y", fmt(Math.min(ya, yb))) //
        .attr("width", fmt(boxWidth)) //
        .attr("height", fmt(boxHeight)) //
        // the cells are square in data coordinates, not on screen, so the image is stretched to
        // the box; a grid of values keeps its cell edges, while a sampled field is smoothed so the
        // reader sees the function rather than the sampling grid
        .attr("preserveAspectRatio", "none") //
        .attr("image-rendering", prim.smooth ? "auto" : "pixelated") //
        .attr("href", PngEncoder.dataUri(argb, width, height)));
  }

  private void emitRasterRun(ContainerTag<?> group, double x0, double y0, double cellW,
      double cellH, int fromCol, int toCol, int row, Color color) {
    if (color.getAlpha() == 0 || toCol <= fromCol) {
      return;
    }
    // row 0 is the bottom row
    double x = x0 + fromCol * cellW;
    double y = y0 - (row + 1) * cellH;
    ContainerTag<?> rect = tag("rect").attr("x", fmt(x)).attr("y", fmt(y))
        .attr("width", fmt(Math.abs((toCol - fromCol) * cellW) + 0.5))
        .attr("height", fmt(Math.abs(cellH) + 0.5)).attr("fill", ColorUtil.css(color));
    if (color.getAlpha() < 255) {
      rect.attr("fill-opacity", fmt(color.getAlpha() / 255.0));
    }
    group.with(rect);
  }

  // ----------------------------------------------------------------- inset

  void drawInset(Prim2D.InsetPrim prim, ContainerTag<?> parent) {
    double x = viewport.mapX(prim.x);
    double y = viewport.mapY(prim.y);
    if (!Double.isFinite(x) || !Double.isFinite(y) || prim.svg == null) {
      return;
    }
    ContainerTag<?> group = tag("g").attr("transform",
        String.format(Locale.US, "translate(%s, %s)", fmt(x - prim.alignX), fmt(y - prim.alignY)));
    group.with(rawHtml(prim.svg));
    parent.with(group);
  }

  // ------------------------------------------------------------ half plane

  void drawHalfPlane(Prim2D.HalfPlanePrim prim, ContainerTag<?> parent) {
    double x1 = viewport.plotX1;
    double x2 = viewport.plotX2;
    double y1 = viewport.plotY1;
    double y2 = viewport.plotY2;

    if (prim.full) {
      ContainerTag<?> rect = tag("rect").attr("x", fmt(x1)).attr("y", fmt(y1))
          .attr("width", fmt(x2 - x1)).attr("height", fmt(y2 - y1));
      paintEdged(rect, prim.style, prim.style.effectiveFill(), edgeStroke(prim.style));
      parent.with(rect);
      return;
    }

    double px = viewport.mapX(prim.px);
    double py = viewport.mapY(prim.py);
    // direction in pixels, accounting for the inverted y axis
    double vx = viewport.lengthX(prim.vx);
    double vy = -viewport.lengthY(prim.vy);
    double len = Math.hypot(vx, vy);
    if (len < 1e-12) {
      return;
    }
    vx /= len;
    vy /= len;
    double reach = Math.hypot(x2 - x1, y2 - y1) * 2;
    double ax = px - vx * reach;
    double ay = py - vy * reach;
    double bx = px + vx * reach;
    double by = py + vy * reach;

    if (prim.lineOnly) {
      ContainerTag<?> line = tag("line").attr("x1", fmt(ax)).attr("y1", fmt(ay)).attr("x2", fmt(bx))
          .attr("y2", fmt(by));
      paint(line, prim.style, null, prim.style.strokeColor);
      parent.with(line);
      return;
    }

    // the filled side, taken from the normal that the side vector points along
    double wx = viewport.lengthX(prim.wx);
    double wy = -viewport.lengthY(prim.wy);
    double nx = -vy;
    double ny = vx;
    if (wx * nx + wy * ny < 0) {
      nx = -nx;
      ny = -ny;
    }
    String points =
        fmt(ax) + "," + fmt(ay) + " " + fmt(bx) + "," + fmt(by) + " " + fmt(bx + nx * reach) + ","
            + fmt(by + ny * reach) + " " + fmt(ax + nx * reach) + "," + fmt(ay + ny * reach);
    ContainerTag<?> poly = tag("polygon").attr("points", points);
    paintEdged(poly, prim.style, prim.style.effectiveFill(), edgeStroke(prim.style));
    parent.with(poly);
  }
}
