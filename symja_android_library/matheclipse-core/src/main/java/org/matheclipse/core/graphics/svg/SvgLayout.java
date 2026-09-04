package org.matheclipse.core.graphics.svg;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.SVGGraphics3D;
import org.matheclipse.core.graphics.WebGLGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Lays several graphics out in a row, a column, a grid or one on top of another.
 *
 * <p>
 * Every cell is drawn at its own natural size and then scaled, rather than being squeezed into a
 * box decided in advance. That is what keeps a plot's text and line weights in proportion with the
 * picture, and it is the same model {@link #overlay} already used for its layers. The options are
 * resolved once by {@link LayoutSpec}, which the MathML {@code Grid} converter shares, so the
 * cyclic list grammar behind {@code Dividers}, {@code Spacings} and the rest is implemented in one
 * place.
 */
final class SvgLayout {

  /** How the cells of a layout are sized relative to one another. */
  private enum Mode {
    /** Every picture is brought to a common height, as {@code GraphicsRow} does. */
    ROW,
    /** Every picture is brought to a common width. */
    COLUMN,
    /** Each picture keeps its natural size; columns and rows take the largest. */
    GRID
  }

  private static final String FRAME_STYLE = "stroke:black;stroke-width:1px;fill:none;";
  /** The smallest cell extent, so a degenerate picture still occupies a position. */
  private static final double MIN_EXTENT = 1.0;
  /** The root element of a {@code Graphics3D} rendering, whose children are lifted out of it. */
  private static final Pattern SVG_ROOT = Pattern.compile("^\\s*<svg\\b[^>]*>(.*)</svg>\\s*$",
      Pattern.DOTALL);
  private static final Pattern SVG_WIDTH = Pattern.compile("\\bwidth=\"([0-9.]+)\"");
  private static final Pattern SVG_HEIGHT = Pattern.compile("\\bheight=\"([0-9.]+)\"");

  private final GraphicsOptions2D options;

  SvgLayout(GraphicsOptions2D options) {
    this.options = options;
  }

  /** One laid out cell: the SVG children, the size they were drawn at, and how they may be sized. */
  private static final class Piece {
    final String contents;
    final double naturalW;
    final double naturalH;
    /**
     * Whether the piece may be scaled to match its neighbours. A picture may; a line of text may
     * not, because bringing a caption to the height of a plot beside it would set it in letters
     * several centimetres tall.
     */
    final boolean scalable;
    double displayW;
    double displayH;

    Piece(String contents, double naturalW, double naturalH, boolean scalable) {
      this.contents = contents;
      this.naturalW = Math.max(naturalW, MIN_EXTENT);
      this.naturalH = Math.max(naturalH, MIN_EXTENT);
      this.scalable = scalable;
      this.displayW = this.naturalW;
      this.displayH = this.naturalH;
    }
  }

  // ------------------------------------------------------------------ entry points

  String row(IAST expr, boolean withSVGTag) {
    IAST items = itemsOf(expr, S.GraphicsRow);
    if (items == null || items.argSize() == 0) {
      return empty(withSVGTag);
    }
    LayoutSpec spec = LayoutSpec.forRow(expr, items, 2, LayoutSpec.Units.POINTS);
    // GraphicsRow[{...}, s] gives the gap between the items
    applyPositionalSpacing(spec, positionalArg(expr, 2));
    return render(spec, Mode.ROW, withSVGTag);
  }

  String column(IAST expr, boolean withSVGTag) {
    IAST items = itemsOf(expr, S.GraphicsColumn);
    if (items == null || items.argSize() == 0) {
      return empty(withSVGTag);
    }
    LayoutSpec spec = LayoutSpec.forColumn(expr, items, 2, LayoutSpec.Units.POINTS);
    // GraphicsColumn[{...}, alignment, s]
    IExpr alignment = positionalArg(expr, 2);
    if (alignment != null) {
      double h = LayoutSpec.alignFraction(alignment, 0);
      if (!Double.isNaN(h)) {
        Arrays.fill(spec.colAlignH, h);
      }
    }
    applyPositionalSpacing(spec, positionalArg(expr, 3));
    return render(spec, Mode.COLUMN, withSVGTag);
  }

  String grid(IAST expr, boolean withSVGTag) {
    IAST rows = itemsOf(expr, S.GraphicsGrid);
    if (rows == null || rows.argSize() == 0) {
      return empty(withSVGTag);
    }
    LayoutSpec spec = LayoutSpec.forGrid(expr, rows, 2, LayoutSpec.Units.POINTS);
    return render(spec, Mode.GRID, withSVGTag);
  }

  /** The argument at {@code index} when it is a positional one rather than an option rule. */
  private static IExpr positionalArg(IAST expr, int index) {
    if (expr.argSize() < index) {
      return null;
    }
    IExpr arg = expr.get(index);
    return arg.isRuleAST() ? null : arg;
  }

  private static void applyPositionalSpacing(LayoutSpec spec, IExpr value) {
    if (value == null) {
      return;
    }
    LayoutSpec.Spacing gap = LayoutSpec.spacingOf(value);
    if (gap == null) {
      return;
    }
    Arrays.fill(spec.colGaps, gap);
    Arrays.fill(spec.rowGaps, gap);
  }

  // ------------------------------------------------------------------ the layout

  private String render(LayoutSpec spec, Mode mode, boolean withSVGTag) {
    int rows = spec.rows;
    int cols = spec.cols;
    double budget = options.imageSize[0];
    double seed = mode == Mode.COLUMN ? budget : budget / Math.max(cols, 1);

    Piece[][] pieces = new Piece[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        LayoutSpec.Cell cell = spec.cells[r][c];
        if (cell.covered || cell.isEmpty()) {
          continue;
        }
        pieces[r][c] = piece(cell.content, seed * cell.colSpan);
      }
    }
    applyModeScaling(pieces, mode);

    double[] colW = new double[cols];
    double[] rowH = new double[rows];
    measure(spec, pieces, colW, rowH);

    double[] colGap = new double[Math.max(cols - 1, 0)];
    double[] rowGap = new double[Math.max(rows - 1, 0)];
    for (int i = 0; i < colGap.length; i++) {
      colGap[i] = spec.colGaps[i].resolvePixels(mean(colW), spec.units);
    }
    for (int i = 0; i < rowGap.length; i++) {
      rowGap[i] = spec.rowGaps[i].resolvePixels(mean(rowH), spec.units);
    }
    growForSpans(spec, pieces, colW, rowH, colGap, rowGap);
    applyItemAspectRatio(spec, colW, rowH);

    double contentW = sum(colW) + sum(colGap);
    double contentH = sum(rowH) + sum(rowGap);
    double marginX = spec.imageMargins[0] + spec.imageMargins[1];
    double marginY = spec.imageMargins[2] + spec.imageMargins[3];

    // one scale for the whole layout, so the cells keep their proportions relative to each other
    double scale = 1.0;
    if (contentW > 0 && contentW + marginX > budget) {
      scale = (budget - marginX) / contentW;
    }
    if (options.imageSizeHeightSet && contentH > 0) {
      double room = options.imageSize[1] - marginY;
      if (room > 0 && contentH * scale > room) {
        scale = room / contentH;
      }
    }
    if (!(scale > 0)) {
      scale = 1.0;
    }

    double canvasW = contentW * scale + marginX;
    double canvasH = contentH * scale + marginY;
    if (options.imageSizeExplicit) {
      canvasW = Math.max(canvasW, options.imageSize[0]);
      if (options.imageSizeHeightSet) {
        canvasH = options.imageSize[1];
      }
    }
    if (!Double.isNaN(spec.aspectRatio) && spec.aspectRatio > 0) {
      canvasH = Math.max(canvasH, canvasW * spec.aspectRatio);
    }
    if (!(canvasW > 0)) {
      canvasW = MIN_EXTENT;
    }
    if (!(canvasH > 0)) {
      canvasH = MIN_EXTENT;
    }
    options.imageSize[0] = canvasW;
    options.imageSize[1] = canvasH;

    // the drawing is centred in whatever room an explicit ImageSize left over
    double originX = spec.imageMargins[0] + Math.max(0, canvasW - marginX - contentW * scale) / 2.0;
    double originY = spec.imageMargins[3] + Math.max(0, canvasH - marginY - contentH * scale) / 2.0;

    double[] colX = offsets(colW, colGap, scale, originX);
    double[] rowY = offsets(rowH, rowGap, scale, originY);

    List<DomContent> elements = new ArrayList<>();
    if (spec.background != null) {
      elements.add(canvasRect(canvasW, canvasH, spec.background));
    }
    addCellBackgrounds(spec, elements, colX, colW, rowY, rowH, colGap, rowGap, scale);
    addCells(spec, pieces, elements, colX, colW, rowY, rowH, colGap, rowGap, scale);
    addItemFrames(spec, elements, colX, colW, rowY, rowH, scale);
    addDividers(spec, elements, colX, colW, rowY, rowH, colGap, rowGap, scale, originX, originY,
        contentW, contentH);
    return wrap(elements, canvasW, canvasH, withSVGTag);
  }

  /** Bring the pictures to a common height, or a common width, depending on the layout. */
  private void applyModeScaling(Piece[][] pieces, Mode mode) {
    if (mode == Mode.GRID) {
      return;
    }
    double target = 0;
    for (Piece[] rowPieces : pieces) {
      for (Piece p : rowPieces) {
        if (p != null && p.scalable) {
          target = Math.max(target, mode == Mode.ROW ? p.naturalH : p.naturalW);
        }
      }
    }
    if (!(target > 0)) {
      return;
    }
    for (Piece[] rowPieces : pieces) {
      for (Piece p : rowPieces) {
        if (p == null || !p.scalable) {
          continue;
        }
        double factor = mode == Mode.ROW ? target / p.naturalH : target / p.naturalW;
        p.displayW = p.naturalW * factor;
        p.displayH = p.naturalH * factor;
      }
    }
  }

  /** A column is as wide as its widest single cell, and a row as tall as its tallest. */
  private void measure(LayoutSpec spec, Piece[][] pieces, double[] colW, double[] rowH) {
    for (int r = 0; r < spec.rows; r++) {
      for (int c = 0; c < spec.cols; c++) {
        Piece p = pieces[r][c];
        if (p == null) {
          continue;
        }
        LayoutSpec.Cell cell = spec.cells[r][c];
        if (cell.colSpan == 1) {
          colW[c] = Math.max(colW[c], p.displayW);
        }
        if (cell.rowSpan == 1) {
          rowH[r] = Math.max(rowH[r], p.displayH);
        }
      }
    }
    for (int c = 0; c < colW.length; c++) {
      colW[c] = Math.max(colW[c], MIN_EXTENT);
    }
    for (int r = 0; r < rowH.length; r++) {
      rowH[r] = Math.max(rowH[r], MIN_EXTENT);
    }
  }

  /** Widen the last column, or deepen the last row, a spanning cell does not fit into. */
  private void growForSpans(LayoutSpec spec, Piece[][] pieces, double[] colW, double[] rowH,
      double[] colGap, double[] rowGap) {
    for (int r = 0; r < spec.rows; r++) {
      for (int c = 0; c < spec.cols; c++) {
        Piece p = pieces[r][c];
        LayoutSpec.Cell cell = spec.cells[r][c];
        if (p == null || (cell.colSpan == 1 && cell.rowSpan == 1)) {
          continue;
        }
        if (cell.colSpan > 1) {
          double available = span(colW, colGap, c, cell.colSpan);
          if (p.displayW > available) {
            colW[c + cell.colSpan - 1] += p.displayW - available;
          }
        }
        if (cell.rowSpan > 1) {
          double available = span(rowH, rowGap, r, cell.rowSpan);
          if (p.displayH > available) {
            rowH[r + cell.rowSpan - 1] += p.displayH - available;
          }
        }
      }
    }
  }

  /** {@code ItemAspectRatio} makes every cell box the same shape. */
  private void applyItemAspectRatio(LayoutSpec spec, double[] colW, double[] rowH) {
    if (Double.isNaN(spec.itemAspectRatio) || spec.itemAspectRatio <= 0) {
      return;
    }
    double w = 0;
    for (double v : colW) {
      w = Math.max(w, v);
    }
    Arrays.fill(colW, w);
    Arrays.fill(rowH, w * spec.itemAspectRatio);
  }

  // ------------------------------------------------------------------ emitting

  private void addCells(LayoutSpec spec, Piece[][] pieces, List<DomContent> elements, double[] colX,
      double[] colW, double[] rowY, double[] rowH, double[] colGap, double[] rowGap, double scale) {
    for (int r = 0; r < spec.rows; r++) {
      for (int c = 0; c < spec.cols; c++) {
        Piece p = pieces[r][c];
        if (p == null) {
          continue;
        }
        LayoutSpec.Cell cell = spec.cells[r][c];
        double boxW = span(colW, colGap, c, cell.colSpan) * scale;
        double boxH = span(rowH, rowGap, r, cell.rowSpan) * scale;
        double w = p.displayW * scale;
        double h = p.displayH * scale;
        double x = colX[c] + spec.alignHAt(r, c) * Math.max(0, boxW - w);
        double y = rowY[r] + spec.alignVAt(r, c) * Math.max(0, boxH - h);
        elements.add(viewport(p, x, y, w, h));
      }
    }
  }

  private void addCellBackgrounds(LayoutSpec spec, List<DomContent> elements, double[] colX,
      double[] colW, double[] rowY, double[] rowH, double[] colGap, double[] rowGap, double scale) {
    for (int r = 0; r < spec.rows; r++) {
      for (int c = 0; c < spec.cols; c++) {
        if (spec.cells[r][c].covered) {
          continue;
        }
        java.awt.Color colour = spec.backgroundAt(r, c);
        if (colour == null) {
          continue;
        }
        LayoutSpec.Cell cell = spec.cells[r][c];
        // the fill runs into half of each neighbouring gap, so a striped row reads as one band
        double x0 = colX[c] - halfGap(colGap, c - 1) * scale;
        double x1 = colX[c] + span(colW, colGap, c, cell.colSpan) * scale
            + halfGap(colGap, c + cell.colSpan - 1) * scale;
        double y0 = rowY[r] - halfGap(rowGap, r - 1) * scale;
        double y1 = rowY[r] + span(rowH, rowGap, r, cell.rowSpan) * scale
            + halfGap(rowGap, r + cell.rowSpan - 1) * scale;
        elements.add(filledRect(x0, y0, x1 - x0, y1 - y0, colour));
      }
    }
  }

  /** The border an {@code Item(expr, Frame -> True)} cell draws around itself. */
  private void addItemFrames(LayoutSpec spec, List<DomContent> elements, double[] colX,
      double[] colW, double[] rowY, double[] rowH, double scale) {
    for (int r = 0; r < spec.rows; r++) {
      for (int c = 0; c < spec.cols; c++) {
        LayoutSpec.Cell cell = spec.cells[r][c];
        if (cell.covered || cell.frame == null || !cell.frame.isTrue()) {
          continue;
        }
        double w = colW[c] * scale;
        double h = rowH[r] * scale;
        elements.add(rect(colX[c], rowY[r], w, h));
      }
    }
  }

  /**
   * The frame and divider lines.
   *
   * <p>
   * A line is emitted per run of consecutive positions it may cross, so a plain frame comes out as
   * four segments rather than one per cell, and a line stops where a spanning cell would have been
   * cut in two - which is the rule the Wolfram documentation states for {@code Dividers}.
   */
  private void addDividers(LayoutSpec spec, List<DomContent> elements, double[] colX, double[] colW,
      double[] rowY, double[] rowH, double[] colGap, double[] rowGap, double scale, double originX,
      double originY, double contentW, double contentH) {
    for (int p = 0; p <= spec.cols; p++) {
      IExpr style = spec.colDividers[p];
      if (style == null) {
        continue;
      }
      double x = linePosition(colX, colW, colGap, scale, originX, contentW, p, spec.cols);
      int start = -1;
      for (int r = 0; r <= spec.rows; r++) {
        boolean crosses = r < spec.rows && spec.columnDividerCrosses(p, r);
        if (crosses && start < 0) {
          start = r;
        } else if (!crosses && start >= 0) {
          elements.add(styledLine(x, bandStart(rowY, rowGap, scale, start),
              x, bandEnd(rowY, rowH, rowGap, scale, r - 1), style));
          start = -1;
        }
      }
    }
    for (int p = 0; p <= spec.rows; p++) {
      IExpr style = spec.rowDividers[p];
      if (style == null) {
        continue;
      }
      double y = linePosition(rowY, rowH, rowGap, scale, originY, contentH, p, spec.rows);
      int start = -1;
      for (int c = 0; c <= spec.cols; c++) {
        boolean crosses = c < spec.cols && spec.rowDividerCrosses(p, c);
        if (crosses && start < 0) {
          start = c;
        } else if (!crosses && start >= 0) {
          elements.add(styledLine(bandStart(colX, colGap, scale, start), y,
              bandEnd(colX, colW, colGap, scale, c - 1), y, style));
          start = -1;
        }
      }
    }
  }

  /** Where the line at position {@code p} sits: on an edge, or in the middle of a gap. */
  private static double linePosition(double[] starts, double[] extents, double[] gaps, double scale,
      double origin, double content, int p, int n) {
    if (p <= 0) {
      return origin;
    }
    if (p >= n) {
      return origin + content * scale;
    }
    double end = starts[p - 1] + extents[p - 1] * scale;
    return end + halfGap(gaps, p - 1) * scale;
  }

  private static double bandStart(double[] starts, double[] gaps, double scale, int index) {
    return starts[index] - halfGap(gaps, index - 1) * scale;
  }

  private static double bandEnd(double[] starts, double[] extents, double[] gaps, double scale,
      int index) {
    return starts[index] + extents[index] * scale + halfGap(gaps, index) * scale;
  }

  private static double halfGap(double[] gaps, int index) {
    return index >= 0 && index < gaps.length ? gaps[index] / 2.0 : 0;
  }

  // ------------------------------------------------------------------ one cell

  /**
   * Draw one cell at its natural size.
   *
   * <p>
   * A picture is rendered by the converter that owns it - the 2D one, or the static 3D renderer for
   * a {@code Graphics3D}, which would otherwise be dropped. Anything else is a value the reader
   * still has to be able to see, so it is set as a line of text rather than left out.
   *
   * @param seedWidth the width to draw at, which the cell's own {@code ImageSize} may override
   */
  private Piece piece(IExpr content, double seedWidth) {
    if (content == null) {
      return null;
    }
    IExpr cell = content.isGraphicsObject() ? content : content.stripDisplayWrappers();
    if (cell instanceof IAST) {
      IAST ast = (IAST) cell;
      if (WebGLGraphics3D.isRenderable(ast)) {
        return piece3D(ast);
      }
      if (ast.isGraphicsObject() || ast.isAST(S.Graphics) || ast.isListOf(S.Graphics)) {
        SvgGraphics2D.Layer layer = SvgGraphics2D.renderLayer(ast, seedWidth);
        if (layer != null) {
          return new Piece(layer.contents, layer.width, layer.height, true);
        }
      }
    }
    return pieceText(content);
  }

  /**
   * A {@code Graphics3D} cell, drawn by the static renderer.
   *
   * <p>
   * Its root carries no responsive style, so the children can be lifted out of it and placed in a
   * viewport of this layout's own making, exactly as a 2D layer is.
   */
  private Piece piece3D(IAST ast) {
    String svg = SVGGraphics3D.toSVG(ast);
    if (svg == null || svg.isEmpty()) {
      return null;
    }
    Matcher root = SVG_ROOT.matcher(svg);
    if (!root.find()) {
      return null;
    }
    String head = svg.substring(0, svg.indexOf('>') + 1);
    return new Piece(root.group(1), attribute(SVG_WIDTH, head, 360),
        attribute(SVG_HEIGHT, head, 360), true);
  }

  private static double attribute(Pattern pattern, String head, double def) {
    Matcher m = pattern.matcher(head);
    return m.find() ? Double.parseDouble(m.group(1)) : def;
  }

  /**
   * A cell that is not a picture, set as a single line of text.
   *
   * <p>
   * Dropping it, which is what used to happen, loses a caption or a heading without a word; a
   * label beside the pictures it describes is the ordinary reason to reach for a layout at all.
   */
  private Piece pieceText(IExpr content) {
    IExpr value = content;
    Style2D style = new Style2D();
    style.setColor(java.awt.Color.BLACK);
    if (value.isAST(S.Style) && ((IAST) value).argSize() >= 1) {
      IAST styled = (IAST) value;
      PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
      for (int i = 2; i <= styled.argSize(); i++) {
        applyTextDirective(styled.get(i), style, collector);
      }
      value = styled.arg1();
    }
    if (value.isAST(S.Text) && ((IAST) value).argSize() >= 1) {
      value = ((IAST) value).arg1();
    }
    String text = value.isString() ? value.toString() : value.toString();
    if (text.isEmpty()) {
      return null;
    }
    double width = SvgRenderer2D.estimateTextWidth(text, style.fontSize) + style.fontSize;
    double height = style.fontSize * 2;
    ContainerTag<?> element = tag("text").attr("x", SvgRenderer2D.fmt(width / 2.0))
        .attr("y", SvgRenderer2D.fmt(height / 2.0))
        .attr("fill", ColorUtil.css(style.strokeColor))
        .attr("font-size", SvgRenderer2D.fmt(style.fontSize))
        .attr("font-family", style.fontFamily).attr("font-weight", style.fontWeight)
        .attr("font-style", style.fontStyle).attr("text-anchor", "middle")
        .attr("dominant-baseline", "middle").withText(text);
    return new Piece(element.render(), width, height, false);
  }

  /**
   * One {@code Style} directive on a text cell.
   *
   * <p>
   * A bare number is a font size. The general directive parser does not read it as one, because a
   * number means nothing to a drawn primitive - so a size written on a caption would otherwise be
   * silently dropped.
   */
  private void applyTextDirective(IExpr directive, Style2D style, PrimitiveCollector collector) {
    if (directive.isNumber()) {
      double size = ColorUtil.dbl(directive, 0);
      if (size > 0) {
        style.fontSize = size;
      }
      return;
    }
    if (directive.isAST(S.Directive) || directive.isList()) {
      IAST group = (IAST) directive;
      for (int i = 1; i <= group.argSize(); i++) {
        applyTextDirective(group.get(i), style, collector);
      }
      return;
    }
    collector.applyStyleTo(directive, style);
  }

  // ------------------------------------------------------------------ overlay

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

  // ------------------------------------------------------------------ elements

  /** The layer, positioned by a nested viewport of its own natural size. */
  private ContainerTag<?> nested(SvgGraphics2D.Layer layer, double x, double y) {
    return tag("svg").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(layer.width))
        .attr("height", SvgRenderer2D.fmt(layer.height))
        .attr("viewBox", String.format(Locale.US, "0 0 %s %s", SvgRenderer2D.fmt(layer.width),
            SvgRenderer2D.fmt(layer.height)))
        .with(rawHtml(layer.contents));
  }

  /**
   * One cell, in a viewport that scales it.
   *
   * <p>
   * The {@code viewBox} is the size the cell was drawn at and the width and height the size it is
   * shown at, so the whole picture inside - its text and its line weights with it - is scaled as
   * one rather than being redrawn to a different shape.
   */
  private ContainerTag<?> viewport(Piece piece, double x, double y, double w, double h) {
    return tag("svg").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(w)).attr("height", SvgRenderer2D.fmt(h))
        .attr("viewBox", String.format(Locale.US, "0 0 %s %s", SvgRenderer2D.fmt(piece.naturalW),
            SvgRenderer2D.fmt(piece.naturalH)))
        .with(rawHtml(piece.contents));
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

  private ContainerTag<?> canvasRect(double w, double h, java.awt.Color colour) {
    return filledRect(0, 0, w, h, colour);
  }

  private ContainerTag<?> filledRect(double x, double y, double w, double h,
      java.awt.Color colour) {
    ContainerTag<?> r = tag("rect").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(Math.max(0, w)))
        .attr("height", SvgRenderer2D.fmt(Math.max(0, h))).attr("fill", ColorUtil.css(colour));
    if (colour.getAlpha() < 255) {
      r.attr("fill-opacity", SvgRenderer2D.fmt(colour.getAlpha() / 255.0));
    }
    return r;
  }

  /**
   * A layout with nothing in it.
   *
   * <p>
   * At the top level this is an empty canvas rather than an empty string, because the servlet
   * hands whatever comes back to the page as the result: an empty string shows as a blank line
   * with no hint that a picture was asked for. Nested inside another layout the same call returns
   * nothing, which is what lets an empty cell take no room.
   */
  private String empty(boolean withSVGTag) {
    return wrap(new ArrayList<>(), options.imageSize[0], options.imageSize[1], withSVGTag);
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

  private ContainerTag<?> rect(double x, double y, double w, double h) {
    return tag("rect").attr("x", SvgRenderer2D.fmt(x)).attr("y", SvgRenderer2D.fmt(y))
        .attr("width", SvgRenderer2D.fmt(w)).attr("height", SvgRenderer2D.fmt(h))
        .attr("style", FRAME_STYLE);
  }

  /** A divider, drawn with the directive its position was given. */
  private ContainerTag<?> styledLine(double x1, double y1, double x2, double y2, IExpr spec) {
    Style2D style = new Style2D();
    style.strokeColor = java.awt.Color.BLACK;
    style.strokeWidth = 1.0;
    if (spec != null && !spec.isTrue()) {
      new PrimitiveCollector(options.imageSize[0]).applyStyleTo(spec, style);
    }
    ContainerTag<?> line = tag("line").attr("x1", SvgRenderer2D.fmt(x1))
        .attr("y1", SvgRenderer2D.fmt(y1)).attr("x2", SvgRenderer2D.fmt(x2))
        .attr("y2", SvgRenderer2D.fmt(y2)).attr("stroke", ColorUtil.css(style.strokeColor))
        .attr("stroke-width", SvgRenderer2D.fmt(style.strokeWidth));
    if (style.dashArray != null && !"none".equals(style.dashArray)) {
      line.attr("stroke-dasharray", style.dashArray);
    }
    return line;
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

  // ------------------------------------------------------------------ arithmetic

  private static double sum(double[] values) {
    double total = 0;
    for (double v : values) {
      total += v;
    }
    return total;
  }

  private static double mean(double[] values) {
    return values.length == 0 ? 0 : sum(values) / values.length;
  }

  /** The extent of {@code count} positions from {@code from}, gaps between them included. */
  private static double span(double[] extents, double[] gaps, int from, int count) {
    double total = 0;
    for (int i = from; i < from + count && i < extents.length; i++) {
      total += extents[i];
      if (i > from && i - 1 < gaps.length) {
        total += gaps[i - 1];
      }
    }
    return total;
  }

  /** The pixel position each column, or row, starts at. */
  private static double[] offsets(double[] extents, double[] gaps, double scale, double origin) {
    double[] starts = new double[extents.length];
    double at = origin;
    for (int i = 0; i < extents.length; i++) {
      starts[i] = at;
      at += extents[i] * scale;
      if (i < gaps.length) {
        at += gaps[i] * scale;
      }
    }
    return starts;
  }
}
