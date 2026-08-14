package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/** Lays several graphics out in a row, a column or a grid. */
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
