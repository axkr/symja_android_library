package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Converts a 2D {@code Graphics} expression to SVG.
 *
 * <p>
 * The conversion runs in four stages: read the options, collect the content into {@link Prim2D}
 * objects, derive the plot range and pixel mapping from those objects, and finally render them.
 * Keeping the stages apart is what lets the bounding box and the drawing agree, and what keeps a
 * malformed primitive from costing more than itself.
 */
public class SvgGraphics2D {

  private static final double MIN_PADDING = 5.0;
  private static final double TICK_LABEL_HEIGHT = 16.0;
  private static final double PLOT_LABEL_HEIGHT = 24.0;
  private static final double AXIS_LABEL_HEIGHT = 18.0;

  private final GraphicsOptions2D options = new GraphicsOptions2D();

  /**
   * Distinguishes the element ids of this document from those of any other embedded beside it.
   *
   * <p>
   * Derived from the expression being drawn rather than from the object identity, so that rendering
   * the same graphic twice produces byte identical output. Two different graphics that happened to
   * collide here would also have identical clip geometry, so sharing an id would not change what is
   * drawn.
   */
  private String idSuffix = "";

  private Viewport2D viewport;
  private List<Prim2D> primitives = new ArrayList<>();

  /**
   * True when the caller insists on the height it passed in, as a layout cell does. A plain default
   * canvas does not: its height gives way to whatever the aspect ratio asks for, so that a short
   * wide plot comes out short and wide instead of floating in the middle of a tall box.
   */
  private boolean fixedHeight = false;

  public SvgGraphics2D() {}

  /**
   * @param width the width to draw at
   * @param height a starting height, which the aspect ratio may override
   */
  public SvgGraphics2D(double width, double height) {
    this(width, height, false);
  }

  /**
   * @param fixedHeight true to keep {@code height} whatever the aspect ratio works out to, which is
   *        what a row, column or grid cell needs
   */
  public SvgGraphics2D(double width, double height, boolean fixedHeight) {
    this();
    options.imageSize[0] = width;
    options.imageSize[1] = height;
    options.imageSizeSet = true;
    options.imageSizeHeightSet = fixedHeight;
    this.fixedHeight = fixedHeight;
  }

  public double[] getImageSize() {
    return options.imageSize;
  }

  public GraphicsOptions2D options() {
    return options;
  }

  /** Render {@code graphicsExpr} to a complete SVG document. */
  public String toSVG(IAST graphicsExpr) {
    return toSVG(graphicsExpr, true);
  }

  /**
   * @param withSVGTag when false, only the contents are returned, for embedding inside an
   *        {@code <svg>} element the caller supplies
   */
  public String toSVG(IAST graphicsExpr, boolean withSVGTag) {
    try {
      graphicsExpr = unwrapPicture(graphicsExpr);
      if (graphicsExpr.isList() || graphicsExpr.isAST(S.GraphicsRow)) {
        return new SvgLayout(options).row(graphicsExpr, withSVGTag);
      }
      if (graphicsExpr.isAST(S.GraphicsColumn)) {
        return new SvgLayout(options).column(graphicsExpr, withSVGTag);
      }
      if (graphicsExpr.isAST(S.GraphicsGrid)) {
        return new SvgLayout(options).grid(graphicsExpr, withSVGTag);
      }
      if (graphicsExpr.isAST(S.Overlay)) {
        return new SvgLayout(options).overlay(graphicsExpr, withSVGTag);
      }
      List<DomContent> elements = buildElements(graphicsExpr);
      if (elements == null) {
        return null;
      }
      if (withSVGTag) {
        return svgRoot(elements).render();
      }
      // the caller provides its own root, so hand back only the children - the title among them,
      // since the caller's root is what a layout cell hovers over
      return withPictureTitle(elements).stream().map(DomContent::render)
          .collect(Collectors.joining());
    } catch (RuntimeException rex) {
      Errors.printMessage(S.Graphics, rex);
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      // an outright failure still returns a valid, empty picture rather than nothing at all
      return withSVGTag ? emptyDocument().render() : "";
    }
  }

  /**
   * The full canvas background.
   *
   * <p>
   * A translucent colour has to carry its alpha in {@code fill-opacity}, because
   * {@link ColorUtil#css} emits only the rgb triple. An {@code Overlay} layer with a half
   * transparent {@code Background} is the documented way to let the layer beneath show through.
   */
  private ContainerTag<?> backgroundRect() {
    ContainerTag<?> rect = tag("rect").attr("width", "100%").attr("height", "100%").attr("fill",
        options.background == null ? "white" : ColorUtil.css(options.background));
    if (options.background != null && options.background.getAlpha() < 255) {
      rect.attr("fill-opacity", SvgRenderer2D.fmt(options.background.getAlpha() / 255.0));
    }
    return rect;
  }

  private ContainerTag<?> emptyDocument() {
    return svgRoot(Collections.singletonList(
        tag("rect").attr("width", "100%").attr("height", "100%").attr("fill", "white")));
  }

  /** Build the SVG element tree for a single {@code Graphics} expression. */
  public ContainerTag<?> buildSVGTag(IAST graphicsExpr) {
    List<DomContent> elements = buildElements(graphicsExpr);
    return elements == null ? null : svgRoot(elements);
  }

  /** The children of the {@code <svg>} root, in drawing order. */
  private List<DomContent> buildElements(IAST graphicsExpr) {
    if (graphicsExpr.isList() || graphicsExpr.isAST(S.GraphicsRow)
        || graphicsExpr.isAST(S.GraphicsColumn) || graphicsExpr.isAST(S.GraphicsGrid)
        || graphicsExpr.isAST(S.Overlay)) {
      return null;
    }

    idSuffix = "_" + Integer.toHexString(graphicsExpr.hashCode());

    PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
    options.parse(graphicsExpr, collector);

    if (graphicsExpr.argSize() >= 1) {
      collector.collect(graphicsExpr.arg1(), options.globalStyle.clone());
    }
    primitives = collector.primitives();

    Bounds2D bounds = new Bounds2D();
    for (Prim2D p : primitives) {
      p.accumulate(bounds);
    }
    if (options.plotGenerated && options.plotRangeAutomatic && !options.plotRangeAll) {
      refineYRange(bounds);
    }

    boolean hasLegend = LegendRenderer.isSupported(options.plotLegends);
    viewport = new Viewport2D(options);
    // padding depends on the tick labels, which depend on the range, which depends on padding:
    // lay out once with an estimate, then again with the labels that estimate produced
    viewport.configure(bounds, estimatePadding(0, hasLegend));
    double labelWidth = AxesFrameRenderer.estimateYLabelWidth(viewport, options);
    double[] padding = estimatePadding(labelWidth, hasLegend);
    fitPaddingToWidth(padding);
    fitHeightToAspectRatio(padding);
    viewport.configure(bounds, padding);

    SvgRenderer2D renderer = new SvgRenderer2D(viewport, options);
    AxesFrameRenderer axes = new AxesFrameRenderer(viewport, options);

    List<DomContent> elements = new ArrayList<>();
    // Background covers the whole image, not only the drawing area. An Overlay layer leaves it
    // out unless it asked for one of its own, so the layers below still show through.
    if (!options.transparentBackground || options.background != null) {
      elements.add(backgroundRect());
    }

    String plotAreaId = "plotArea" + idSuffix;
    String gradientId = "legendGradient" + idSuffix;
    ContainerTag<?> defs = tag("defs");
    boolean hasDefs = false;
    if (LegendRenderer.isBarLegend(options.plotLegends)) {
      defs.with(LegendRenderer.barGradient(gradientId, options.plotLegends));
      hasDefs = true;
    }
    if (options.plotRangeClipping) {
      defs.with(tag("clipPath").attr("id", plotAreaId)
          .with(tag("rect").attr("x", SvgRenderer2D.fmt(viewport.plotX1))
              .attr("y", SvgRenderer2D.fmt(viewport.plotY1))
              .attr("width", SvgRenderer2D.fmt(Math.max(0, viewport.plotX2 - viewport.plotX1)))
              .attr("height", SvgRenderer2D.fmt(Math.max(0, viewport.plotY2 - viewport.plotY1)))));
      hasDefs = true;
    }
    if (hasDefs) {
      elements.add(defs);
    }

    if (options.prolog != null) {
      ContainerTag<?> group = tag("g").attr("id", "prolog");
      renderExtra(options.prolog, renderer, group);
      elements.add(group);
    }

    if (options.gridLines != null) {
      ContainerTag<?> grid = tag("g").attr("class", "grid");
      axes.drawGridLines(grid);
      if (grid.getNumChildren() > 0) {
        elements.add(grid);
      }
    }

    ContainerTag<?> main = tag("g").attr("id", "main");
    if (options.plotRangeClipping) {
      main.attr("clip-path", "url(#" + plotAreaId + ")");
    }
    renderer.draw(primitives, main);
    elements.add(main);

    // the parts the clip path just removed, redrawn along the edge in the clipping style
    List<Prim2D> clipped = clippingHighlights(collector);
    if (!clipped.isEmpty()) {
      ContainerTag<?> group = tag("g").attr("class", "clipped");
      renderer.draw(clipped, group);
      elements.add(group);
    }

    ContainerTag<?> axesGroup = tag("g").attr("class", "axes");
    axes.drawAxes(axesGroup);
    axes.drawFrame(axesGroup);
    if (axesGroup.getNumChildren() > 0) {
      elements.add(axesGroup);
    }

    drawLabels(elements);

    if (hasLegend) {
      ContainerTag<?> legend = tag("g").attr("class", "legend");
      new LegendRenderer(viewport, options).draw(legend, gradientId);
      elements.add(legend);
    }

    if (options.epilog != null) {
      ContainerTag<?> group = tag("g").attr("id", "epilog");
      renderExtra(options.epilog, renderer, group);
      elements.add(group);
    }

    return elements;
  }

  /** Prolog and epilog content is collected and drawn, but never affects the plot range. */
  private void renderExtra(IExpr expr, SvgRenderer2D renderer, ContainerTag<?> parent) {
    PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
    collector.collect(expr, options.globalStyle.clone());
    renderer.draw(collector.primitives(), parent);
  }

  /**
   * The elements with a whole picture {@code Tooltip} in front of them.
   *
   * <p>
   * A {@code <title>} answers a hover over anything its parent draws, so as the first child of the
   * root it covers the picture; a primitive that carries a tooltip of its own sits deeper and wins
   * over it, which is the nesting a viewer already applies.
   */
  static List<DomContent> withPictureTitle(List<DomContent> elements, GraphicsOptions2D options) {
    if (options.pictureTooltip == null) {
      return elements;
    }
    List<DomContent> titled = new ArrayList<>(elements.size() + 1);
    titled.add(tag("title").withText(options.pictureTooltip));
    titled.addAll(elements);
    return titled;
  }

  private List<DomContent> withPictureTitle(List<DomContent> elements) {
    return withPictureTitle(elements, options);
  }

  private ContainerTag<?> svgRoot(List<DomContent> elements) {
    elements = withPictureTitle(elements);
    return tag("svg").attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("width", SvgRenderer2D.fmt(options.imageSize[0]))
        .attr("height", SvgRenderer2D.fmt(options.imageSize[1]))
        .attr("style", "max-width: 100%; height: auto;")
        .attr("viewBox", String.format(Locale.US, "0 0 %s %s",
            SvgRenderer2D.fmt(options.imageSize[0]), SvgRenderer2D.fmt(options.imageSize[1])))
        .with(elements);
  }

  /**
   * Shrink or grow the canvas so the drawing area actually has the requested shape.
   *
   * <p>
   * The viewport otherwise fits the drawing inside whatever canvas it was handed and centres it,
   * which turns a deliberately short plot (a number line asks for a tenth of the golden ratio) into
   * a thin band adrift in white space. Sizes the image from the width and the aspect ratio, so that
   * is what happens here whenever the height was not explicitly demanded.
   *
   * @param padding room reserved around the drawing, as left, right, bottom, top
   */
  /**
   * Keep the decoration from consuming the whole canvas.
   *
   * <p>
   * The room a frame and its numbers ask for does not shrink with the picture, so a small enough
   * {@code ImageSize} leaves nothing to draw in: at {@code ImageSize -> 70} a framed plot wants
   * more than 70 pixels for its left and right margins alone. That used to make the drawing width
   * zero or negative, which {@link #fitHeightToAspectRatio} read as "cannot tell" and gave up on -
   * leaving the height at whatever the caller had passed in, so a cell asked for at 70 pixels came
   * back 70 by 400 and drew as a sliver. Scaling the margins down instead keeps the picture the
   * shape it asked for; the numbers are cramped, which is what asking for a tiny framed plot means.
   */
  private void fitPaddingToWidth(double[] padding) {
    double share = MAX_PADDING_SHARE * options.imageSize[0];
    double wanted = padding[0] + padding[1];
    if (wanted > share && wanted > 0) {
      double scale = share / wanted;
      padding[0] *= scale;
      padding[1] *= scale;
    }
  }

  /** The most of the width the left and right margins together may take. */
  private static final double MAX_PADDING_SHARE = 0.6;

  private void fitHeightToAspectRatio(double[] padding) {
    if (fixedHeight || options.imageSizeHeightSet) {
      return;
    }
    double ratio;
    if (!options.aspectRatioAutomatic && !Double.isNaN(options.aspectRatio)
        && options.aspectRatio > 0) {
      ratio = options.aspectRatio;
    } else {
      // Automatic keeps one data unit the same length on both axes
      double rangeX = viewport.maxX - viewport.minX;
      double rangeY = viewport.maxY - viewport.minY;
      if (!(rangeX > 0) || !(rangeY > 0)) {
        return;
      }
      ratio = rangeY / rangeX;
    }
    if (!Double.isFinite(ratio) || ratio <= 0) {
      return;
    }
    double drawWidth = options.imageSize[0] - padding[0] - padding[1];
    if (drawWidth <= 0) {
      return;
    }
    // a ratio far from square would otherwise produce an unusable canvas
    double drawHeight = Math.max(MIN_DRAWING_HEIGHT, Math.min(drawWidth * ratio, drawWidth * 4.0));
    options.imageSize[1] = drawHeight + padding[2] + padding[3];
  }

  /**
   * Whether one half of a label pair says anything.
   *
   * @param axis 0 for the horizontal label, which is drawn below the picture, 1 for the vertical
   *        one, which is drawn at its left
   */
  private boolean hasLabel(IExpr spec, int axis) {
    return spec != null && labelPair(spec)[axis] != null;
  }

  /**
   * The room an unlabelled edge needs.
   *
   * <p>
   * A frame is the outermost thing drawn on that side, so it needs only enough not to have its own
   * stroke clipped; insetting it as far as a free edge wastes the room on a small picture and
   * shows up as a margin around every cell of a grid. An edge with no frame keeps the larger inset,
   * because there a curve or a disk may itself run to the boundary.
   */
  private static double edgePadding(boolean framed) {
    return framed ? FRAME_EDGE_PADDING : MIN_PADDING;
  }

  /** Enough that a frame line drawn at the boundary is not cut in half. */
  private static final double FRAME_EDGE_PADDING = 2.0;

  /** Smallest drawing area an automatically sized image is allowed to shrink to. */
  private static final double MIN_DRAWING_HEIGHT = 40.0;

  // --------------------------------------------------------------- padding

  /**
   * Room to leave around the drawing area, in the order left, right, bottom, top.
   *
   * @param yLabelWidth measured width of the widest y tick label, or 0 on the first pass
   */
  private double[] estimatePadding(double yLabelWidth, boolean hasLegend) {
    if (options.imagePadding != null) {
      double[] p = options.imagePadding;
      return new double[] {p[0], p[1] + (hasLegend ? LegendRenderer.LEGEND_WIDTH : 0), p[2], p[3]};
    }
    // Room is needed for the numbers along an edge, not for the edge itself. Asking only whether
    // there is an axis or a frame there reserved it even when the ticks had been switched off, and
    // a small picture then spent most of its width on margins holding nothing - which is what made
    // the cells of a grid of framed plots draw a fraction of the size they were given.
    boolean axesTicks = !(options.ticks.isNone() || options.ticks.isFalse());
    boolean frameTicks = !(options.frameTicks.isNone() || options.frameTicks.isFalse());
    boolean labelsLeft = (options.axesY && axesTicks) || (options.frame[0] && frameTicks);
    boolean labelsBottom = (options.axesX && axesTicks) || (options.frame[2] && frameTicks);

    double left = labelsLeft ? Math.max(24.0, yLabelWidth + 12.0) : edgePadding(options.frame[0]);
    double right = options.frame[1] && frameTicks ? 24.0 : edgePadding(options.frame[1]);
    double bottom = labelsBottom ? TICK_LABEL_HEIGHT + 8.0 : edgePadding(options.frame[2]);
    double top = options.frame[3] && frameTicks ? TICK_LABEL_HEIGHT + 8.0 : edgePadding(options.frame[3]);

    if (options.plotLabel != null) {
      top += PLOT_LABEL_HEIGHT;
    }
    // Only for a label that is actually written. Testing the option for null counted
    // `AxesLabel -> None`, which every plot emits by default, so each picture reserved a strip on
    // two sides for text it never drew - 18 pixels of a 70 pixel cell on each of them.
    if (hasLabel(options.axesLabel, 0) || hasLabel(options.frameLabel, 0)) {
      bottom += AXIS_LABEL_HEIGHT;
    }
    if (hasLabel(options.axesLabel, 1) || hasLabel(options.frameLabel, 1)) {
      left += AXIS_LABEL_HEIGHT;
    }
    if (hasLegend) {
      right += LegendRenderer.LEGEND_WIDTH;
    }
    return new double[] {left, right, bottom, top};
  }

  // ---------------------------------------------------------------- labels

  private void drawLabels(List<DomContent> elements) {
    if (options.plotLabel != null) {
      double cx = (viewport.plotX1 + viewport.plotX2) / 2.0;
      double cy = Math.max(14, viewport.plotY1 - 12);
      elements.add(
          labelled(tag("text").attr("x", SvgRenderer2D.fmt(cx)).attr("y", SvgRenderer2D.fmt(cy))
              .attr("text-anchor", "middle"), 14, true).withText(labelText(options.plotLabel)));
    }
    if (options.axesLabel != null) {
      String[] labels = labelPair(options.axesLabel);
      if (labels[0] != null) {
        elements.add(labelled(tag("text").attr("x", SvgRenderer2D.fmt(viewport.plotX2))
            .attr("y", SvgRenderer2D.fmt(viewport.plotY2 + TICK_LABEL_HEIGHT + 14))
            .attr("text-anchor", "end"), 12, false).withText(labels[0]));
      }
      if (labels[1] != null) {
        elements.add(labelled(tag("text").attr("x", SvgRenderer2D.fmt(viewport.plotX1))
            .attr("y", SvgRenderer2D.fmt(Math.max(12, viewport.plotY1 - 6)))
            .attr("text-anchor", "middle"), 12, false).withText(labels[1]));
      }
    }
    if (options.frameLabel != null) {
      String[] labels = labelPair(options.frameLabel);
      double cx = (viewport.plotX1 + viewport.plotX2) / 2.0;
      double cy = (viewport.plotY1 + viewport.plotY2) / 2.0;
      if (labels[0] != null) {
        elements.add(labelled(tag("text").attr("x", SvgRenderer2D.fmt(cx))
            .attr("y", SvgRenderer2D.fmt(viewport.plotY2 + TICK_LABEL_HEIGHT + 14))
            .attr("text-anchor", "middle"), 12, false).withText(labels[0]));
      }
      if (labels[1] != null) {
        double x = Math.max(12, viewport.plotX1 - AXIS_LABEL_HEIGHT - 14);
        elements.add(
            labelled(tag("text").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(cy))
                .attr("text-anchor", "middle").attr("transform", String.format(Locale.US,
                    "rotate(-90 %s %s)", SvgRenderer2D.fmt(x), SvgRenderer2D.fmt(cy))),
                12, false).withText(labels[1]));
      }
    }
  }

  /**
   * The parts of the curves that leave the plot range, flattened onto its edge.
   *
   * <p>
   * The clip path removes whatever falls outside the range, so a curve that runs off the top just
   * disappears and nothing says it was ever there. {@code ClippingStyle} asks for those stretches
   * to be shown at the boundary instead, which is what these extra lines are: the excursion with
   * every point pulled back to the nearest edge, drawn in the style the caller asked for.
   *
   * @param collector used to read the style expression; may be null
   * @return the extra lines to draw, empty when nothing was clipped or no style was asked for
   */
  private List<Prim2D> clippingHighlights(PrimitiveCollector collector) {
    List<Prim2D> out = new ArrayList<>();
    if (options.clippingStyle == null || !options.plotRangeClipping) {
      return out;
    }
    for (Prim2D prim : primitives) {
      if (!(prim instanceof Prim2D.LinePrim)) {
        continue;
      }
      Prim2D.LinePrim line = (Prim2D.LinePrim) prim;
      List<List<double[]>> runs = new ArrayList<>();
      for (List<double[]> segment : line.segments) {
        collectOutsideRuns(segment, runs);
      }
      if (!runs.isEmpty()) {
        out.add(new Prim2D.LinePrim(runs, false, clippingStyleFrom(line.style, collector)));
      }
    }
    return out;
  }

  /** Collect the stretches of a polyline that leave the range, clamped back onto it. */
  private void collectOutsideRuns(List<double[]> points, List<List<double[]>> runs) {
    List<double[]> run = null;
    for (int i = 0; i < points.size(); i++) {
      double[] point = points.get(i);
      if (isOutsideRange(point)) {
        if (run == null) {
          run = new ArrayList<>();
          if (i > 0) {
            // start where the curve crossed out, so the drawn line meets the visible one
            run.add(clampToRange(points.get(i - 1)));
          }
        }
        run.add(clampToRange(point));
      } else if (run != null) {
        run.add(clampToRange(point));
        runs.add(run);
        run = null;
      }
    }
    if (run != null && run.size() > 1) {
      runs.add(run);
    }
  }

  private boolean isOutsideRange(double[] point) {
    return point[0] < viewport.minX || point[0] > viewport.maxX || point[1] < viewport.minY
        || point[1] > viewport.maxY;
  }

  private double[] clampToRange(double[] point) {
    return new double[] {Math.min(Math.max(point[0], viewport.minX), viewport.maxX),
        Math.min(Math.max(point[1], viewport.minY), viewport.maxY)};
  }

  /**
   * The style for the clipped stretches of a curve.
   *
   * <p>
   * {@code Automatic} keeps the curve's own colour and dashes the line, so it reads as "the curve
   * continues past here"; anything else is taken as a style to apply over the curve's.
   */
  private Style2D clippingStyleFrom(Style2D curveStyle, PrimitiveCollector collector) {
    Style2D style = curveStyle.clone();
    if (options.clippingStyle == S.Automatic) {
      style.dashArray = "4,4";
      return style;
    }
    if (collector != null) {
      collector.applyStyleTo(options.clippingStyle, style);
    }
    return style;
  }

  /**
   * Add the font attributes of a label, with {@code LabelStyle} folded in.
   *
   * @param defaultSize the size this label uses when {@code LabelStyle} names none
   * @param bold whether this label is bold regardless of the style
   */
  private ContainerTag<?> labelled(ContainerTag<?> tag, double defaultSize, boolean bold) {
    Style2D style = options.labelStyle;
    double size = options.labelFontSizeSet ? style.fontSize : defaultSize;
    tag.attr("font-family", style != null ? style.fontFamily : "sans-serif").attr("font-size",
        SvgRenderer2D.fmt(size));
    if (bold || (style != null && "bold".equals(style.fontWeight))) {
      tag.attr("font-weight", "bold");
    }
    if (style != null) {
      tag.attr("fill", ColorUtil.css(style.strokeColor));
      if ("italic".equals(style.fontStyle)) {
        tag.attr("font-style", "italic");
      }
    }
    return tag;
  }

  private String labelText(IExpr expr) {
    return PrimitiveCollector.unquote(expr.toString());
  }

  /** Split a {@code {xlabel, ylabel}} option value; either entry may be absent. */
  private String[] labelPair(IExpr expr) {
    String[] out = new String[2];
    if (expr.isList() && ((IAST) expr).argSize() >= 2) {
      IAST list = (IAST) expr;
      if (!unlabelled(list.arg1())) {
        out[0] = labelText(list.arg1());
      }
      if (!unlabelled(list.arg2())) {
        out[1] = labelText(list.arg2());
      }
    } else if (!unlabelled(expr)) {
      out[0] = labelText(expr);
    }
    return out;
  }

  /**
   * Whether a label setting asks for no label at all.
   *
   * <p>
   * {@code Automatic} counts, for the same reason it does in three dimensions: only the plot knows
   * the names of its own variables, so it resolves the option before the picture is built. What
   * reaches here still holding {@code Automatic} has nothing to derive a name from, and drawing the
   * symbol put the word "Automatic" on the axis.
   */
  private static boolean unlabelled(IExpr expr) {
    return expr.isNone() || expr.isAutomatic();
  }

  // ------------------------------------------------------------ range work

  /**
   * Narrow an automatic y range to the body of the data.
   *
   * <p>
   * Only ever shrinks the range, and only for plot generated graphics: see
   * {@link GraphicsOptions2D#plotGenerated}.
   */
  private void refineYRange(Bounds2D bounds) {
    List<Double> values = new ArrayList<>();
    for (Prim2D p : primitives) {
      collectYValues(p, values);
    }
    if (values.size() < 10) {
      return;
    }
    Collections.sort(values);
    int n = values.size();
    double p10 = values.get(n / 10);
    double p90 = values.get(9 * n / 10);
    double body = p90 - p10;
    if (body <= 1e-10) {
      double q1 = values.get(n / 4);
      double q3 = values.get(3 * n / 4);
      if (q3 - q1 <= 1e-10) {
        return;
      }
      p10 = q1;
      p90 = q3;
      body = q3 - q1;
    }
    double q1 = values.get(n / 4);
    double q3 = values.get(3 * n / 4);
    double iqr = q3 - q1;
    double targetMin = Math.max(p10 - body, q1 - 3.0 * iqr);
    double targetMax = Math.min(p90 + body, q3 + 3.0 * iqr);

    double newMin = bounds.yMin;
    double newMax = bounds.yMax;
    for (int i = 0; i < n; i++) {
      if (values.get(i) >= targetMin) {
        newMin = values.get(i);
        break;
      }
    }
    for (int i = n - 1; i >= 0; i--) {
      if (values.get(i) <= targetMax) {
        newMax = values.get(i);
        break;
      }
    }
    if (newMin > bounds.yMin) {
      bounds.yMin = newMin;
    }
    if (newMax < bounds.yMax) {
      bounds.yMax = newMax;
    }
  }

  private void collectYValues(Prim2D prim, List<Double> out) {
    Bounds2D single = new Bounds2D();
    if (prim instanceof Prim2D.LinePrim) {
      for (List<double[]> segment : ((Prim2D.LinePrim) prim).segments) {
        for (double[] p : segment) {
          if (Double.isFinite(p[1])) {
            out.add(p[1]);
          }
        }
      }
      return;
    }
    if (prim instanceof Prim2D.PointsPrim) {
      for (double[] p : ((Prim2D.PointsPrim) prim).points) {
        if (Double.isFinite(p[1])) {
          out.add(p[1]);
        }
      }
      return;
    }
    prim.accumulate(single);
    if (!single.isEmpty()) {
      out.add(single.yMin);
      out.add(single.yMax);
    }
  }

  /** One layer of an {@code Overlay}: its element children, and the size it settled on. */
  static final class Layer {
    final String contents;
    final double width;
    final double height;

    Layer(String contents, double width, double height) {
      this.contents = contents;
      this.width = width;
      this.height = height;
    }
  }

  /**
   * Take a display wrapper off a whole picture, keeping what it said.
   *
   * <p>
   * {@code Tooltip(Plot(...), "s")} draws the plot and answers a hover with {@code s}, so the
   * wrapper comes off here and the label is handed to the root emitter. {@code Labeled} and
   * {@code Legended} are peeled too, but their label is meant to be drawn rather than hovered and
   * nothing here draws it yet - the picture is the part worth keeping.
   *
   * <p>
   * The guard is {@link IExpr#isGraphicsObject()} rather than the head alone, so
   * {@code Tooltip(1, "s")} is left as it is and keeps printing as itself.
   */
  private IAST unwrapPicture(IAST graphicsExpr) {
    if (!IExpr.isPictureWrapperHead(graphicsExpr.head()) || !graphicsExpr.isGraphicsObject()) {
      return graphicsExpr;
    }
    PlotWrapper wrapper = PlotWrapper.of(graphicsExpr);
    if (options.pictureTooltip == null) {
      options.pictureTooltip = PlotWrapper.tooltipLabel(wrapper.tooltip);
    }
    return wrapper.datum.isAST() ? (IAST) wrapper.datum : graphicsExpr;
  }

  /**
   * Render one layer of an {@code Overlay} at its natural size.
   *
   * <p>
   * The layer is measured rather than told: the aspect ratio decides the height exactly as it
   * does for a top level graphic, and the size it settled on is handed back so the caller can
   * size its canvas and place the layer. Only the element children come
   * back, never a root - the root this class emits carries a responsive style whose
   * {@code height: auto} would stretch the layer to the full canvas height, which is precisely
   * what an overlay must not do.
   *
   * @param width the width budget, which the layer's own {@code ImageSize} may override
   * @return the layer, or {@code null} when the expression is not a graphic
   */
  static Layer renderLayer(IExpr expr, double width) {
    if (!(expr instanceof IAST)) {
      return null;
    }
    IAST ast = (IAST) expr;
    if (!ast.isGraphicsObject() && !ast.isAST(S.Graphics) && !ast.isAST(S.GraphicsRow)
        && !ast.isAST(S.GraphicsColumn) && !ast.isAST(S.GraphicsGrid) && !ast.isList()) {
      return null;
    }
    // a square seed, so a layer whose range is degenerate still comes out at a usable size
    SvgGraphics2D sub = new SvgGraphics2D(width, width);
    sub.options.transparentBackground = true;
    String contents = sub.toSVG(ast, false);
    if (contents == null || contents.isEmpty()) {
      return null;
    }
    return new Layer(contents, sub.options.imageSize[0], sub.options.imageSize[1]);
  }

}
