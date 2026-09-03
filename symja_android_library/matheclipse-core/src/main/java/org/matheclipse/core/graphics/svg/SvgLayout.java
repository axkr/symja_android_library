package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/** Lays several graphics out in a row, a column, a grid or one on top of another. */
final class SvgLayout {

  private static final double GAP = 20.0;
  private static final String FRAME_STYLE = "stroke:black;stroke-width:1px;fill:none;";

  private final GraphicsOptions2D options;

  SvgLayout(GraphicsOptions2D options) {
    this.options = options;
  }

  String row(IAST expr, boolean withSVGTag) {
    IAST items = itemsOf(expr, S.GraphicsRow);
    if (items == null || items.argSize() == 0) {
      return withSVGTag ? "" : "";
    }
    int n = items.argSize();
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];
    double itemW = (totalW - GAP * (n - 1)) / n;

    List<DomContent> elements = new ArrayList<>();
    double x = 0;
    for (int i = 1; i <= n; i++) {
      String svg = SvgGraphics2D.renderChild(items.get(i), itemW, totalH);
      if (svg != null) {
        elements.add(SvgGraphics2D.placed(svg, x, 0));
      }
      x += itemW + GAP;
    }
    addRowFrame(elements, n, itemW, totalW, totalH);
    return wrap(elements, totalW, totalH, withSVGTag);
  }

  String column(IAST expr, boolean withSVGTag) {
    IAST items = itemsOf(expr, S.GraphicsColumn);
    if (items == null || items.argSize() == 0) {
      return "";
    }
    int n = items.argSize();
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];
    double itemH = (totalH - GAP * (n - 1)) / n;

    List<DomContent> elements = new ArrayList<>();
    double y = 0;
    for (int i = 1; i <= n; i++) {
      String svg = SvgGraphics2D.renderChild(items.get(i), totalW, itemH);
      if (svg != null) {
        elements.add(SvgGraphics2D.placed(svg, 0, y));
      }
      y += itemH + GAP;
    }
    addColumnFrame(elements, n, itemH, totalW, totalH);
    return wrap(elements, totalW, totalH, withSVGTag);
  }

  String grid(IAST expr, boolean withSVGTag) {
    IAST rows = itemsOf(expr, S.GraphicsGrid);
    if (rows == null || rows.argSize() == 0) {
      return "";
    }
    int rowCount = rows.argSize();
    int colCount = 0;
    for (int r = 1; r <= rowCount; r++) {
      if (rows.get(r).isList()) {
        colCount = Math.max(colCount, ((IAST) rows.get(r)).argSize());
      }
    }
    if (colCount == 0) {
      return "";
    }
    if (!options.imageSizeSet) {
      options.imageSize[0] = colCount * 200.0;
      options.imageSize[1] = rowCount * 200.0;
    }
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];
    double itemW = totalW / colCount;
    double itemH = totalH / rowCount;

    List<DomContent> elements = new ArrayList<>();
    for (int r = 1; r <= rowCount; r++) {
      IExpr rowExpr = rows.get(r);
      if (!rowExpr.isList()) {
        continue;
      }
      IAST row = (IAST) rowExpr;
      for (int c = 1; c <= row.argSize(); c++) {
        IExpr cell = row.get(c);
        if (cell.isNone() || cell == S.SpanFromLeft || cell == S.SpanFromAbove
            || cell == S.SpanFromBoth) {
          continue;
        }
        String svg = SvgGraphics2D.renderChild(cell, itemW, itemH);
        if (svg != null) {
          elements.add(SvgGraphics2D.placed(svg, (c - 1) * itemW, (r - 1) * itemH));
        }
      }
    }
    if (options.hasFrame()) {
      elements.add(rect(0, 0, totalW, totalH));
      for (int c = 1; c < colCount; c++) {
        elements.add(line(c * itemW, 0, c * itemW, totalH));
      }
      for (int r = 1; r < rowCount; r++) {
        elements.add(line(0, r * itemH, totalW, r * itemH));
      }
    }
    return wrap(elements, totalW, totalH, withSVGTag);
  }

  /**
   * Stack the items of an {@code Overlay} on one canvas, later items on top.
   *
   * <p>
   * Every layer is rendered on its own at its natural size and then placed inside a nested
   * {@code <svg>} viewport of exactly that size. The canvas is the per axis maximum of the layer
   * sizes, and {@code Alignment} decides where the spare room around a smaller layer goes.
   *
   * <p>
   * The layers are rendered with no background of their own, so a lower layer shows through an
   * upper one. A layer that asked for a {@code Background} still paints it, and still hides what
   * is beneath - which is why a translucent one is the documented way to see both.
   */
  String overlay(IAST expr, boolean withSVGTag) {
    IAST items = expr.argSize() >= 1 && expr.arg1().isList() ? (IAST) expr.arg1() : null;
    // options first: an ImageSize rule changes the budget every layer is rendered against
    options.parse(expr, new PrimitiveCollector(options.imageSize[0]));
    if (items == null) {
      return blank(withSVGTag);
    }
    int n = items.argSize();
    double budget = options.imageSize[0];
    int[] order = drawOrder(expr, n);

    boolean[] needed = new boolean[n + 1];
    for (int i : order) {
      needed[i] = true;
    }
    if (options.imageSizeAll) {
      // All leaves room for the largest item whether or not it is one of the ones shown, and the
      // only way to know how large an item is, is to render it
      Arrays.fill(needed, 1, n + 1, true);
    }
    SvgGraphics2D.Layer[] layers = new SvgGraphics2D.Layer[n + 1];
    double canvasW = 0;
    double canvasH = 0;
    for (int i = 1; i <= n; i++) {
      if (!needed[i]) {
        continue;
      }
      layers[i] = SvgGraphics2D.renderLayer(items.get(i), budget);
      if (layers[i] != null) {
        canvasW = Math.max(canvasW, layers[i].width);
        canvasH = Math.max(canvasH, layers[i].height);
      }
    }
    if (options.imageSizeExplicit && !options.imageSizeAll) {
      canvasW = options.imageSize[0];
      canvasH = options.imageSize[1];
    }
    if (!(canvasW > 0) || !(canvasH > 0)) {
      // nothing rendered: a blank picture at the size asked for, never a zero sized one
      canvasW = options.imageSize[0];
      canvasH = options.imageSize[1];
    }
    options.imageSize[0] = canvasW;
    options.imageSize[1] = canvasH;

    List<DomContent> elements = new ArrayList<>();
    if (!options.transparentBackground || options.background != null) {
      elements.add(background(canvasW, canvasH));
    }
    for (int i : order) {
      SvgGraphics2D.Layer layer = layers[i];
      if (layer == null) {
        // an item that is not a graphic is dropped, as it is in a row or a grid
        continue;
      }
      double x = Math.max(0, options.alignment[0] * (canvasW - layer.width));
      double y = Math.max(0, options.alignment[1] * (canvasH - layer.height));
      elements.add(nested(layer, x, y));
    }
    if (options.hasFrame()) {
      elements.add(rect(0, 0, canvasW, canvasH));
    }
    return wrap(elements, canvasW, canvasH, withSVGTag);
  }

  /**
   * Which layers of an {@code Overlay} are drawn, and in what order.
   *
   * <p>
   * The second argument is the selection and the drawing order at once, so {@code {2, 3, 1}} puts
   * the first item on top. It is optional and an option rule may stand in its place, which is why
   * a rule is never read as a selection.
   */
  private int[] drawOrder(IAST expr, int n) {
    IExpr spec = expr.argSize() >= 2 ? expr.arg2() : S.All;
    if (spec.isRuleAST() || spec == S.All || spec == S.Automatic) {
      int[] all = new int[n];
      for (int i = 0; i < n; i++) {
        all[i] = i + 1;
      }
      return all;
    }
    if (spec.isNone()) {
      return new int[0];
    }
    IAST list = spec.isList() ? (IAST) spec : spec.makeList();
    int[] buffer = new int[list.argSize()];
    int size = 0;
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr item = list.get(i);
      int index = item.isInteger() ? item.toIntDefault() : Integer.MIN_VALUE;
      if (index < 0 && index != Integer.MIN_VALUE) {
        index = n + 1 + index;
      }
      if (index >= 1 && index <= n) {
        // a repeated index simply draws that layer twice; a malformed one costs only itself
        buffer[size++] = index;
      }
    }
    return Arrays.copyOf(buffer, size);
  }

  /** The layer, positioned by a nested viewport of its own natural size. */
  private ContainerTag<?> nested(SvgGraphics2D.Layer layer, double x, double y) {
    return tag("svg").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(layer.width))
        .attr("height", SvgRenderer2D.fmt(layer.height))
        .attr("viewBox", String.format(Locale.US, "0 0 %s %s", SvgRenderer2D.fmt(layer.width),
            SvgRenderer2D.fmt(layer.height)))
        .with(rawHtml(layer.contents));
  }

  /** The overlay canvas, under every layer. */
  private ContainerTag<?> background(double w, double h) {
    ContainerTag<?> r = tag("rect").attr("width", SvgRenderer2D.fmt(w))
        .attr("height", SvgRenderer2D.fmt(h))
        .attr("fill", options.background == null ? "white" : ColorUtil.css(options.background));
    if (options.background != null && options.background.getAlpha() < 255) {
      r.attr("fill-opacity", SvgRenderer2D.fmt(options.background.getAlpha() / 255.0));
    }
    return r;
  }

  private String blank(boolean withSVGTag) {
    List<DomContent> only = new ArrayList<>();
    only.add(background(options.imageSize[0], options.imageSize[1]));
    return wrap(only, options.imageSize[0], options.imageSize[1], withSVGTag);
  }

  /**
   * The list of child graphics. A bare list is its own item list; the named heads carry theirs as
   * the first argument, with any options following.
   */
  private IAST itemsOf(IAST expr, org.matheclipse.core.interfaces.ISymbol head) {
    if (expr.isAST(head)) {
      if (expr.argSize() >= 1 && expr.arg1().isList()) {
        PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
        options.parse(expr, collector);
        return (IAST) expr.arg1();
      }
      return null;
    }
    return expr.isList() ? expr : null;
  }

  private void addRowFrame(List<DomContent> elements, int n, double itemW, double totalW,
      double totalH) {
    if (!options.hasFrame()) {
      return;
    }
    elements.add(rect(0, 0, totalW, totalH));
    for (int i = 1; i < n; i++) {
      double x = i * (itemW + GAP) - GAP / 2.0;
      elements.add(line(x, 0, x, totalH));
    }
  }

  private void addColumnFrame(List<DomContent> elements, int n, double itemH, double totalW,
      double totalH) {
    if (!options.hasFrame()) {
      return;
    }
    elements.add(rect(0, 0, totalW, totalH));
    for (int i = 1; i < n; i++) {
      double y = i * (itemH + GAP) - GAP / 2.0;
      elements.add(line(0, y, totalW, y));
    }
  }

  private ContainerTag<?> rect(double x, double y, double w, double h) {
    return tag("rect").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(w)).attr("height", SvgRenderer2D.fmt(h))
        .attr("style", FRAME_STYLE);
  }

  private ContainerTag<?> line(double x1, double y1, double x2, double y2) {
    return tag("line").attr("x1", SvgRenderer2D.fmt(x1)).attr("y1", SvgRenderer2D.fmt(y1))
        .attr("x2", SvgRenderer2D.fmt(x2)).attr("y2", SvgRenderer2D.fmt(y2))
        .attr("style", FRAME_STYLE);
  }

  private String wrap(List<DomContent> elements, double w, double h, boolean withSVGTag) {
    if (!withSVGTag) {
      return elements.stream().map(DomContent::render).collect(Collectors.joining("\n"));
    }
    return tag("svg").attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("width", SvgRenderer2D.fmt(w)).attr("height", SvgRenderer2D.fmt(h))
        .attr("style", "max-width: 100%; height: auto;")
        .attr("viewBox",
            String.format(Locale.US, "0 0 %s %s", SvgRenderer2D.fmt(w), SvgRenderer2D.fmt(h)))
        .with(elements).render();
  }
}
