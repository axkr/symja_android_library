package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options of a {@code Grid}, {@code GraphicsGrid}, {@code GraphicsRow} or
 * {@code GraphicsColumn}, resolved from Wolfram's option grammar into per position arrays.
 *
 * <p>
 * Deliberately free of any output format. The same specification drives the SVG layout in
 * {@link SvgLayout} and the {@code <mtable>} the MathML factory writes, so the cyclic list grammar
 * that {@code Dividers}, {@code Spacings}, {@code Alignment}, {@code Background}, {@code ItemSize}
 * and {@code ItemStyle} all share is implemented once here rather than twice in two spellings.
 *
 * <p>
 * Nothing in here throws on a malformed option. A specification that cannot be read falls back to
 * its default, for the same reason {@link GraphicsOptions2D#parse} swallows a bad rule: losing one
 * decoration is much better than losing the whole picture.
 */
public final class LayoutSpec {

  /** How a bare number in a {@code Spacings} specification is measured. */
  public enum Units {
    /**
     * {@code GraphicsGrid} and friends measure in printer's points, and the default gap is
     * {@code Scaled[0.1]} of the item size.
     */
    POINTS,
    /** {@code Grid} measures in ems of the current font, and defaults to {@code {0.8, 0.2}}. */
    EMS
  }

  /** One gap, either an absolute measure or a fraction of the items it separates. */
  public static final class Spacing {
    public final boolean scaled;
    public final double value;

    public Spacing(boolean scaled, double value) {
      this.scaled = scaled;
      this.value = value;
    }

    /**
     * @param cellSize the size of the items this gap sits between, for a {@code Scaled} value
     * @param units how an absolute value is measured
     * @return the gap in pixels, never negative
     */
    public double resolvePixels(double cellSize, Units units) {
      double px = scaled ? value * cellSize : (units == Units.POINTS ? value * 4.0 / 3.0 : value);
      return px > 0 ? px : 0;
    }

    /** The gap in ems, for a text layout. A {@code Scaled} value has no meaning there. */
    public double resolveEms() {
      return scaled ? 0 : Math.max(0, value);
    }
  }

  /** One cell of the layout. */
  public static final class Cell {
    /** What the cell shows; {@code null} for an empty cell. */
    public IExpr content;
    public int rowSpan = 1;
    public int colSpan = 1;
    /** True when a spanning neighbour covers this position, so nothing is drawn here. */
    public boolean covered = false;

    /** {@code Item[...]} overrides, all null or NaN when the cell carries none. */
    public Color background = null;
    public IExpr frame = null;
    public IExpr frameStyle = null;
    public IExpr itemSize = null;
    public IExpr baseStyle = null;
    public double alignH = Double.NaN;
    public double alignV = Double.NaN;

    public boolean isEmpty() {
      return content == null;
    }
  }

  public final int rows;
  public final int cols;
  public final Cell[][] cells;
  public final Units units;

  /**
   * The vertical line before each column, {@code cols + 1} entries: index 0 is the left edge and
   * index {@code cols} the right one. {@code null} draws nothing, {@link S#True} draws the default
   * line, anything else is a graphics directive.
   */
  public final IExpr[] colDividers;
  /** The horizontal line above each row, {@code rows + 1} entries, index 0 being the top edge. */
  public final IExpr[] rowDividers;

  /** The gap after each column, {@code cols - 1} entries. */
  public Spacing[] colGaps;
  /** The gap after each row, {@code rows - 1} entries. */
  public Spacing[] rowGaps;

  /** Per column horizontal placement, {@code NaN} for automatic. */
  public double[] colAlignH;
  /** Per row vertical placement, {@code NaN} for automatic. */
  public double[] rowAlignV;

  public Color[] colBackgrounds;
  public Color[] rowBackgrounds;
  /** The whole canvas, or {@code null} to paint nothing at all. */
  public Color background = null;

  public IExpr[] colStyles;
  public IExpr[] rowStyles;
  public IExpr baseStyle = null;

  /** Per column {@code ItemSize}, in ems for a {@code Grid} and pixels for a graphics layout. */
  public IExpr[] colItemSize;
  public IExpr[] rowItemSize;

  /** The style frame derived dividers are drawn with, or {@code null} for the default. */
  public IExpr frameStyle = null;

  /** Every cell box forced to this height/width ratio, or {@code NaN}. */
  public double itemAspectRatio = Double.NaN;
  /** The ratio of the whole picture, or {@code NaN}. */
  public double aspectRatio = Double.NaN;
  /** {@code {left, right, bottom, top}} in pixels. */
  public double[] imageMargins = {0, 0, 0, 0};

  private LayoutSpec(Cell[][] cells, int rows, int cols, Units units) {
    this.cells = cells;
    this.rows = rows;
    this.cols = cols;
    this.units = units;
    this.colDividers = new IExpr[cols + 1];
    this.rowDividers = new IExpr[rows + 1];
    this.colGaps = new Spacing[Math.max(0, cols - 1)];
    this.rowGaps = new Spacing[Math.max(0, rows - 1)];
    this.colAlignH = new double[cols];
    this.rowAlignV = new double[rows];
    java.util.Arrays.fill(colAlignH, Double.NaN);
    java.util.Arrays.fill(rowAlignV, Double.NaN);
    this.colBackgrounds = new Color[cols];
    this.rowBackgrounds = new Color[rows];
    this.colStyles = new IExpr[cols];
    this.rowStyles = new IExpr[rows];
    this.colItemSize = new IExpr[cols];
    this.rowItemSize = new IExpr[rows];
  }

  // --------------------------------------------------------------------- entry points

  /**
   * A two dimensional layout, from a list of rows.
   *
   * @param holder the whole call, whose trailing rules are the options
   * @param firstOptionIndex the first argument that may be an option rule
   */
  public static LayoutSpec forGrid(IAST holder, IAST rowList, int firstOptionIndex, Units units) {
    int rowCount = rowList.argSize();
    int colCount = 0;
    for (int r = 1; r <= rowCount; r++) {
      IExpr row = rowList.get(r);
      colCount = Math.max(colCount, row.isList() ? ((IAST) row).argSize() : 1);
    }
    IExpr[][] raw = new IExpr[Math.max(rowCount, 1)][Math.max(colCount, 1)];
    for (int r = 1; r <= rowCount; r++) {
      IExpr row = rowList.get(r);
      if (row.isList()) {
        IAST cellsOfRow = (IAST) row;
        for (int c = 1; c <= cellsOfRow.argSize(); c++) {
          raw[r - 1][c - 1] = cellsOfRow.get(c);
        }
      } else {
        // a row that is not a list is one cell, which Wolfram lets run the width of the grid
        raw[r - 1][0] = row;
      }
    }
    return build(raw, Math.max(rowCount, 1), Math.max(colCount, 1), holder, firstOptionIndex,
        units);
  }

  /** A single row of items. */
  public static LayoutSpec forRow(IAST holder, IAST items, int firstOptionIndex, Units units) {
    int n = Math.max(items.argSize(), 1);
    IExpr[][] raw = new IExpr[1][n];
    for (int i = 1; i <= items.argSize(); i++) {
      raw[0][i - 1] = items.get(i);
    }
    return build(raw, 1, n, holder, firstOptionIndex, units);
  }

  /** A single column of items. */
  public static LayoutSpec forColumn(IAST holder, IAST items, int firstOptionIndex, Units units) {
    int n = Math.max(items.argSize(), 1);
    IExpr[][] raw = new IExpr[n][1];
    for (int i = 1; i <= items.argSize(); i++) {
      raw[i - 1][0] = items.get(i);
    }
    return build(raw, n, 1, holder, firstOptionIndex, units);
  }

  // --------------------------------------------------------------------- construction

  private static LayoutSpec build(IExpr[][] raw, int rows, int cols, IAST holder,
      int firstOptionIndex, Units units) {
    Cell[][] cells = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = new Cell();
        IExpr content = raw[r][c];
        // None and Null are how an empty position is spelled; drawn as text they would read
        // "None" in the middle of the cell, which is worse than the blank they ask for
        cells[r][c].content =
            (content == null || content.isNone() || content == S.Null) ? null : content;
      }
    }
    LayoutSpec spec = new LayoutSpec(cells, rows, cols, units);
    spec.resolveSpans(raw);
    spec.readItemWrappers();
    spec.defaults();
    if (holder != null) {
      spec.readOptions(holder, firstOptionIndex);
    }
    return spec;
  }

  /**
   * Turn the span markers into merged cells.
   *
   * <p>
   * A marker extends the cell it points back to rather than standing for one of its own, so the
   * origin grows and every position the marker covered is left blank. A marker with nothing to
   * extend - one in the first column, or under an empty cell - is simply an empty cell: dropping
   * the marker costs one blank position, while guessing an origin would move real content.
   */
  private void resolveSpans(IExpr[][] raw) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        IExpr cell = raw[r][c];
        if (cell == null) {
          continue;
        }
        boolean left = cell == S.SpanFromLeft;
        boolean above = cell == S.SpanFromAbove;
        boolean both = cell == S.SpanFromBoth;
        if (!left && !above && !both) {
          continue;
        }
        cells[r][c].content = null;
        int[] origin = both ? findOrigin(r, c - 1, true) : null;
        if (both && origin == null) {
          origin = findOrigin(r - 1, c, false);
        }
        if (left) {
          origin = findOrigin(r, c - 1, true);
        } else if (above) {
          origin = findOrigin(r - 1, c, false);
        }
        if (origin == null) {
          continue;
        }
        Cell target = cells[origin[0]][origin[1]];
        target.colSpan = Math.max(target.colSpan, c - origin[1] + 1);
        target.rowSpan = Math.max(target.rowSpan, r - origin[0] + 1);
        cells[r][c].covered = true;
      }
    }
  }

  /**
   * The cell a span marker belongs to: the nearest position to the left, or above, that is not
   * itself a marker.
   *
   * @param horizontal true to walk left along the row, false to walk up the column
   */
  private int[] findOrigin(int r, int c, boolean horizontal) {
    while (r >= 0 && c >= 0) {
      if (!cells[r][c].covered && cells[r][c].content != null) {
        return new int[] {r, c};
      }
      if (!cells[r][c].covered) {
        // an empty cell is not something a span can extend
        return null;
      }
      if (horizontal) {
        c--;
      } else {
        r--;
      }
    }
    return null;
  }

  /** Read the options an {@code Item[expr, opts]} cell carries, and unwrap it. */
  private void readItemWrappers() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = cells[r][c];
        if (cell.content == null || !cell.content.isAST(S.Item)) {
          continue;
        }
        IAST item = (IAST) cell.content;
        if (item.argSize() < 1) {
          continue;
        }
        cell.content = item.arg1();
        for (int i = 2; i <= item.argSize(); i++) {
          IExpr arg = item.get(i);
          if (!arg.isRuleAST()) {
            continue;
          }
          IExpr key = ((IAST) arg).arg1();
          IExpr value = ((IAST) arg).arg2();
          if (!key.isBuiltInSymbol()) {
            continue;
          }
          try {
            switch (((IBuiltInSymbol) key).ordinal()) {
              case ID.Background:
                cell.background = value.isNone() ? null : ColorUtil.parseDirective(value);
                break;
              case ID.Frame:
                cell.frame = value;
                break;
              case ID.FrameStyle:
                cell.frameStyle = value;
                break;
              case ID.ItemSize:
                cell.itemSize = value;
                break;
              case ID.BaseStyle:
                cell.baseStyle = value;
                break;
              case ID.Alignment:
                if (value.isList() && ((IAST) value).argSize() >= 2) {
                  cell.alignH = alignFraction(((IAST) value).arg1(), 0);
                  cell.alignV = alignFraction(((IAST) value).arg2(), 1);
                } else {
                  double h = alignFraction(value, 0);
                  double v = alignFraction(value, 1);
                  if (!Double.isNaN(h)) {
                    cell.alignH = h;
                  }
                  if (!Double.isNaN(v)) {
                    cell.alignV = v;
                  }
                }
                break;
              default:
                break;
            }
          } catch (RuntimeException rex) {
            // one unreadable Item option leaves the cell with its inherited settings
          }
        }
      }
    }
  }

  private void defaults() {
    Spacing gap = units == Units.POINTS ? new Spacing(true, 0.1) : new Spacing(false, 0.8);
    Spacing vgap = units == Units.POINTS ? new Spacing(true, 0.1) : new Spacing(false, 0.2);
    java.util.Arrays.fill(colGaps, gap);
    java.util.Arrays.fill(rowGaps, vgap);
  }

  // --------------------------------------------------------------------- option reading

  private void readOptions(IAST holder, int firstOptionIndex) {
    IExpr frameValue = null;
    IExpr dividersValue = null;
    for (int i = firstOptionIndex; i <= holder.argSize(); i++) {
      IExpr arg = holder.get(i);
      if (!arg.isRuleAST()) {
        continue;
      }
      IExpr key = ((IAST) arg).arg1();
      IExpr value = ((IAST) arg).arg2();
      if (!key.isBuiltInSymbol()) {
        continue;
      }
      try {
        switch (((IBuiltInSymbol) key).ordinal()) {
          case ID.Frame:
            frameValue = value;
            break;
          case ID.Dividers:
            dividersValue = value;
            break;
          case ID.FrameStyle:
            frameStyle = value.isAutomatic() ? null : value;
            break;
          case ID.Spacings:
            applySpacings(value);
            break;
          case ID.Alignment:
            applyAlignment(value);
            break;
          case ID.Background:
            applyBackground(value);
            break;
          case ID.ItemStyle:
            applyPairOfSpecs(value, colStyles, rowStyles);
            break;
          case ID.ItemSize:
            applyPairOfSpecs(value, colItemSize, rowItemSize);
            break;
          case ID.BaseStyle:
            baseStyle = value.isList() && ((IAST) value).argSize() == 0 ? null : value;
            break;
          case ID.ItemAspectRatio:
            itemAspectRatio = ColorUtil.dbl(value, Double.NaN);
            break;
          case ID.AspectRatio:
            aspectRatio = ColorUtil.dbl(value, Double.NaN);
            break;
          case ID.ImageMargins:
            applyImageMargins(value);
            break;
          default:
            break;
        }
      } catch (RuntimeException rex) {
        // a malformed option falls back to its default rather than losing the layout
      }
    }
    applyFrameAndDividers(frameValue, dividersValue);
  }

  /**
   * Resolve {@code Frame} and {@code Dividers} together into the two line arrays.
   *
   * <p>
   * They describe the same lines from two directions: {@code Frame} names the perimeter,
   * {@code Dividers} names every position, and {@code Dividers -> {}} explicitly defers to the
   * frame. Merging them here means the renderers only ever read the arrays.
   */
  private void applyFrameAndDividers(IExpr frameValue, IExpr dividersValue) {
    IExpr lineStyle = frameStyle == null ? S.True : frameStyle;
    if (frameValue != null) {
      if (frameValue.isTrue()) {
        setPerimeter(lineStyle);
      } else if (frameValue == S.All) {
        java.util.Arrays.fill(colDividers, lineStyle);
        java.util.Arrays.fill(rowDividers, lineStyle);
      } else if (frameValue.isFalse() || frameValue.isNone()) {
        // nothing
      } else if (frameValue.isList()) {
        applyFrameEdges((IAST) frameValue, lineStyle);
      } else {
        // a colour, or any other directive, means a perimeter drawn with it
        Color colour = ColorUtil.parseDirective(frameValue);
        if (colour != null) {
          frameStyle = frameValue;
          setPerimeter(frameValue);
        }
      }
    }
    if (dividersValue == null) {
      return;
    }
    if (dividersValue.isList() && ((IAST) dividersValue).argSize() == 0) {
      // Dividers -> {} draws exactly the lines the Frame option asked for
      return;
    }
    if (dividersValue.isNone() || dividersValue.isFalse()) {
      java.util.Arrays.fill(colDividers, null);
      java.util.Arrays.fill(rowDividers, null);
      return;
    }
    if (dividersValue.isList() && ((IAST) dividersValue).argSize() == 2
        && !isRuleList((IAST) dividersValue)) {
      IAST pair = (IAST) dividersValue;
      applyDividerSpec(colDividers, pair.arg1());
      applyDividerSpec(rowDividers, pair.arg2());
      return;
    }
    // a single specification applies to both directions
    applyDividerSpec(colDividers, dividersValue);
    applyDividerSpec(rowDividers, dividersValue);
  }

  /**
   * One direction's worth of {@code Dividers}.
   *
   * <p>
   * The four names mean the same thing whether they stand for the whole option or for one side of
   * a {@code {columns, rows}} pair, so {@code Dividers -> {All, Center}} rules every column
   * boundary and only the interior row ones. Read positionally instead, {@code Center} would spell
   * "the symbol Center at every position", which draws a full set of lines and reads as though the
   * word had been ignored.
   */
  private void applyDividerSpec(IExpr[] target, IExpr spec) {
    int n = target.length;
    if (spec.isNone() || spec.isFalse()) {
      java.util.Arrays.fill(target, null);
      return;
    }
    if (spec == S.All) {
      java.util.Arrays.fill(target, S.True);
      return;
    }
    if (spec.isTrue()) {
      // the exterior only, which is what the Frame option names
      target[0] = S.True;
      target[n - 1] = S.True;
      return;
    }
    if (spec == S.Center) {
      for (int i = 1; i < n - 1; i++) {
        target[i] = S.True;
      }
      return;
    }
    if (!spec.isList() && !isDrawableDirective(spec)) {
      // a name that draws nothing - a typo, or a symbol the relaxed parser lower cased - must
      // leave the layout alone rather than ruling every position with an unpaintable style
      return;
    }
    fillDividers(target, spec, n);
  }

  /**
   * Whether {@code value} is something a line can actually be drawn with.
   *
   * <p>
   * {@code Thick} and {@code Dashed} have already become {@code Thickness[...]} and
   * {@code Dashing[...]} by the time an option is read, so a head test is needed beside the colour
   * one. Everything else - notably an unknown symbol - is rejected, which is what keeps
   * {@code Dividers -> foo} from ruling the whole grid with an invisible style.
   */
  private static boolean isDrawableDirective(IExpr value) {
    if (value == null || value.isTrue() || value.isFalse() || value.isNone()) {
      return false;
    }
    if (ColorUtil.parseDirective(value) != null) {
      return true;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i <= list.argSize(); i++) {
        if (isDrawableDirective(list.get(i))) {
          return true;
        }
      }
      return false;
    }
    IExpr head = value.head();
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Directive:
      case ID.Thickness:
      case ID.AbsoluteThickness:
      case ID.Dashing:
      case ID.AbsoluteDashing:
      case ID.Opacity:
      case ID.GrayLevel:
      case ID.RGBColor:
      case ID.Hue:
        return true;
      default:
        return false;
    }
  }

  private void setPerimeter(IExpr style) {
    colDividers[0] = style;
    colDividers[cols] = style;
    rowDividers[0] = style;
    rowDividers[rows] = style;
  }

  /** {@code Frame -> {{left, right}, {bottom, top}}}, or {@code {horizontal, vertical}}. */
  private void applyFrameEdges(IAST list, IExpr style) {
    if (list.argSize() < 2) {
      return;
    }
    if (list.arg1().isList() && list.arg2().isList()) {
      IAST lr = (IAST) list.arg1();
      IAST bt = (IAST) list.arg2();
      if (lr.argSize() >= 1 && lr.arg1().isTrue()) {
        colDividers[0] = style;
      }
      if (lr.argSize() >= 2 && lr.arg2().isTrue()) {
        colDividers[cols] = style;
      }
      if (bt.argSize() >= 1 && bt.arg1().isTrue()) {
        rowDividers[rows] = style;
      }
      if (bt.argSize() >= 2 && bt.arg2().isTrue()) {
        rowDividers[0] = style;
      }
      return;
    }
    if (list.arg1().isTrue()) {
      colDividers[0] = style;
      colDividers[cols] = style;
    }
    if (list.arg2().isTrue()) {
      rowDividers[0] = style;
      rowDividers[rows] = style;
    }
  }

  private void fillDividers(IExpr[] target, IExpr spec, int n) {
    IExpr[] resolved = positions(spec, n, null);
    for (int i = 0; i < n; i++) {
      IExpr v = resolved[i];
      if (v == null || v.isFalse() || v.isNone()) {
        target[i] = null;
      } else if (v.isTrue() || v == S.All || v == S.Center) {
        target[i] = S.True;
      } else {
        target[i] = isDrawableDirective(v) ? v : null;
      }
    }
  }

  private void applySpacings(IExpr value) {
    if (value.isAutomatic()) {
      return;
    }
    if (value.isList() && ((IAST) value).argSize() == 2 && !isRuleList((IAST) value)) {
      IAST pair = (IAST) value;
      fillGaps(colGaps, pair.arg1());
      fillGaps(rowGaps, pair.arg2());
      return;
    }
    fillGaps(colGaps, value);
    fillGaps(rowGaps, value);
  }

  private void fillGaps(Spacing[] target, IExpr spec) {
    if (target.length == 0) {
      return;
    }
    IExpr[] resolved = positions(spec, target.length, null);
    for (int i = 0; i < target.length; i++) {
      Spacing s = spacingOf(resolved[i]);
      if (s != null) {
        target[i] = s;
      }
    }
  }

  /** A single {@code Spacings} value: a number, {@code Scaled[s]} or {@code Offset[v]}. */
  static Spacing spacingOf(IExpr value) {
    if (value == null || value.isAutomatic()) {
      return null;
    }
    if (value.isAST(S.Scaled, 2)) {
      double s = ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
      return Double.isNaN(s) ? null : new Spacing(true, s);
    }
    if (value.isAST(S.Offset, 2)) {
      double s = ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
      return Double.isNaN(s) ? null : new Spacing(false, s);
    }
    double v = ColorUtil.dbl(value, Double.NaN);
    return Double.isNaN(v) ? null : new Spacing(false, v);
  }

  private void applyAlignment(IExpr value) {
    if (!value.isList()) {
      double h = alignFraction(value, 0);
      double v = alignFraction(value, 1);
      if (!Double.isNaN(h)) {
        java.util.Arrays.fill(colAlignH, h);
      }
      if (!Double.isNaN(v)) {
        java.util.Arrays.fill(rowAlignV, v);
      }
      return;
    }
    IAST list = (IAST) value;
    if (list.argSize() >= 1) {
      IExpr specX = list.arg1();
      if (specX.isList()) {
        IExpr[] perColumn = positions(specX, cols, null);
        for (int c = 0; c < cols; c++) {
          colAlignH[c] = alignFraction(perColumn[c], 0);
        }
      } else {
        double h = alignFraction(specX, 0);
        if (!Double.isNaN(h)) {
          java.util.Arrays.fill(colAlignH, h);
        }
      }
    }
    if (list.argSize() >= 2) {
      IExpr specY = list.arg2();
      if (specY.isList()) {
        IExpr[] perRow = positions(specY, rows, null);
        for (int r = 0; r < rows; r++) {
          rowAlignV[r] = alignFraction(perRow[r], 1);
        }
      } else {
        double v = alignFraction(specY, 1);
        if (!Double.isNaN(v)) {
          java.util.Arrays.fill(rowAlignV, v);
        }
      }
    }
    if (list.argSize() >= 3) {
      applyCellRules(list.arg3(), (r, c, v) -> {
        double h = alignFraction(v, 0);
        double vert = alignFraction(v, 1);
        if (!Double.isNaN(h)) {
          cells[r][c].alignH = h;
        }
        if (!Double.isNaN(vert)) {
          cells[r][c].alignV = vert;
        }
      });
    }
  }

  private void applyBackground(IExpr value) {
    if (value.isNone()) {
      background = null;
      return;
    }
    if (!value.isList()) {
      background = ColorUtil.parseDirective(value);
      return;
    }
    IAST list = (IAST) value;
    if (list.argSize() >= 1 && !list.arg1().isNone()) {
      IExpr[] perColumn = positions(list.arg1(), cols, null);
      for (int c = 0; c < cols; c++) {
        colBackgrounds[c] = colourOf(perColumn[c]);
      }
    }
    if (list.argSize() >= 2 && !list.arg2().isNone()) {
      IExpr[] perRow = positions(list.arg2(), rows, null);
      for (int r = 0; r < rows; r++) {
        rowBackgrounds[r] = colourOf(perRow[r]);
      }
    }
    if (list.argSize() >= 3) {
      applyCellRules(list.arg3(), (r, c, v) -> cells[r][c].background = colourOf(v));
    }
  }

  private static Color colourOf(IExpr value) {
    if (value == null || value.isNone() || value.isAutomatic()) {
      return null;
    }
    return ColorUtil.parseDirective(value);
  }

  /** {@code ItemStyle} and {@code ItemSize} both take {@code {columnSpec, rowSpec}}. */
  private void applyPairOfSpecs(IExpr value, IExpr[] perColumn, IExpr[] perRow) {
    if (value.isNone() || value.isAutomatic()) {
      return;
    }
    if (value.isList() && ((IAST) value).argSize() == 2 && !isRuleList((IAST) value)) {
      IAST pair = (IAST) value;
      spreadInto(pair.arg1(), perColumn);
      spreadInto(pair.arg2(), perRow);
      return;
    }
    spreadInto(value, perColumn);
  }

  /**
   * Fill {@code target} from one positional specification.
   *
   * <p>
   * A side given as {@code None} asks for nothing at all, which is not the same as the symbol
   * {@code None} being the value at every position - stored literally it would count as a setting
   * and hide the other axis.
   */
  private void spreadInto(IExpr spec, IExpr[] target) {
    if (target.length == 0 || spec == null || spec.isNone() || spec.isAutomatic()) {
      return;
    }
    IExpr[] resolved = positions(spec, target.length, null);
    for (int i = 0; i < target.length; i++) {
      IExpr v = resolved[i];
      target[i] = (v == null || v.isNone() || v.isAutomatic()) ? null : v;
    }
  }

  private void applyImageMargins(IExpr value) {
    double all = ColorUtil.dbl(value, Double.NaN);
    if (!Double.isNaN(all)) {
      imageMargins = new double[] {all, all, all, all};
      return;
    }
    if (value.isList() && ((IAST) value).argSize() >= 2) {
      IAST list = (IAST) value;
      if (list.arg1().isList() && list.arg2().isList()) {
        IAST lr = (IAST) list.arg1();
        IAST bt = (IAST) list.arg2();
        imageMargins = new double[] {ColorUtil.dbl(lr.arg1(), 0), ColorUtil.dbl(lr.arg2(), 0),
            ColorUtil.dbl(bt.arg1(), 0), ColorUtil.dbl(bt.arg2(), 0)};
      }
    }
  }

  /** A callback for the {@code {i, j} -> value} and region forms. */
  private interface CellRule {
    void apply(int row, int col, IExpr value);
  }

  /** {@code {{i, j} -> v}} names one cell; {@code {{imin,imax},{jmin,jmax}} -> v} a rectangle. */
  private void applyCellRules(IExpr spec, CellRule action) {
    if (!spec.isList()) {
      return;
    }
    IAST list = (IAST) spec;
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr entry = list.get(i);
      if (!entry.isRuleAST()) {
        continue;
      }
      IExpr lhs = ((IAST) entry).arg1();
      IExpr value = ((IAST) entry).arg2();
      if (!lhs.isList() || ((IAST) lhs).argSize() < 2) {
        continue;
      }
      IExpr first = ((IAST) lhs).arg1();
      IExpr second = ((IAST) lhs).arg2();
      int[] rowRange = range(first, rows);
      int[] colRange = range(second, cols);
      if (rowRange == null || colRange == null) {
        continue;
      }
      for (int r = rowRange[0]; r <= rowRange[1]; r++) {
        for (int c = colRange[0]; c <= colRange[1]; c++) {
          action.apply(r, c, value);
        }
      }
    }
  }

  /** An index, or an {@code {min, max}} pair, as a zero based inclusive range. */
  private static int[] range(IExpr spec, int n) {
    if (spec.isList() && ((IAST) spec).argSize() >= 2) {
      int lo = index(((IAST) spec).arg1(), n);
      int hi = index(((IAST) spec).arg2(), n);
      if (lo < 0 || hi < 0) {
        return null;
      }
      return new int[] {Math.min(lo, hi), Math.max(lo, hi)};
    }
    int i = index(spec, n);
    return i < 0 ? null : new int[] {i, i};
  }

  /** A one based index, negative counting from the end, as a zero based one; -1 when out of range. */
  private static int index(IExpr spec, int n) {
    int i = spec.toIntDefault();
    if (i == Integer.MIN_VALUE) {
      return -1;
    }
    if (i < 0) {
      i = n + 1 + i;
    }
    return i >= 1 && i <= n ? i - 1 : -1;
  }

  private static boolean isRuleList(IAST list) {
    return list.argSize() > 0 && list.forAll(x -> x.isRuleAST());
  }

  // --------------------------------------------------------------------- the shared grammar

  /**
   * Spread a Wolfram positional specification over {@code n} positions.
   *
   * <p>
   * One grammar serves {@code Dividers}, {@code Spacings}, {@code Alignment}, {@code Background},
   * {@code ItemSize} and {@code ItemStyle}: a scalar applies everywhere, a flat list is used in
   * order and then runs out, a nested list is a block cycled over the middle, and trailing rules
   * name individual positions. {@code {s1, {c}, sn}} is the general shape, with the prefix and the
   * suffix pinned to the two ends.
   *
   * @param def what a position the specification does not reach gets
   */
  public static IExpr[] positions(IExpr spec, int n, IExpr def) {
    IExpr[] result = new IExpr[Math.max(n, 0)];
    java.util.Arrays.fill(result, def);
    if (n <= 0 || spec == null || spec.isAutomatic()) {
      return result;
    }
    if (!spec.isList()) {
      java.util.Arrays.fill(result, spec);
      return result;
    }
    IAST list = (IAST) spec;
    List<IExpr> sequence = new ArrayList<>();
    List<IExpr> rules = new ArrayList<>();
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr item = list.get(i);
      if (item.isRuleAST()) {
        rules.add(item);
      } else if (item.isList() && isRuleList((IAST) item)) {
        // the {spec, rules} form: a sublist made only of rules is an override list
        IAST inner = (IAST) item;
        for (int j = 1; j <= inner.argSize(); j++) {
          rules.add(inner.get(j));
        }
      } else {
        sequence.add(item);
      }
    }
    int blockAt = -1;
    for (int i = 0; i < sequence.size(); i++) {
      if (sequence.get(i).isList()) {
        blockAt = i;
        break;
      }
    }
    if (blockAt < 0) {
      for (int i = 0; i < n && i < sequence.size(); i++) {
        result[i] = sequence.get(i);
      }
    } else {
      IAST block = (IAST) sequence.get(blockAt);
      int prefix = blockAt;
      int suffix = sequence.size() - blockAt - 1;
      for (int i = 0; i < n && i < prefix; i++) {
        result[i] = sequence.get(i);
      }
      for (int i = 0; i < suffix; i++) {
        int at = n - suffix + i;
        if (at >= prefix && at >= 0 && at < n) {
          result[at] = sequence.get(blockAt + 1 + i);
        }
      }
      int cycleLength = block.argSize();
      if (cycleLength > 0) {
        for (int i = prefix; i < n - suffix; i++) {
          if (i >= 0) {
            result[i] = block.get((i - prefix) % cycleLength + 1);
          }
        }
      }
    }
    for (IExpr rule : rules) {
      int at = index(((IAST) rule).arg1(), n);
      if (at >= 0) {
        result[at] = ((IAST) rule).arg2();
      }
    }
    return result;
  }

  /**
   * Where a named or numeric alignment puts an item, as a fraction of the room around it.
   *
   * <p>
   * Zero is the left edge on the horizontal axis and the top on the vertical one, which runs
   * downwards to match SVG. A name that belongs to the other axis gives {@code NaN} rather than a
   * guess, so {@code Alignment -> Left} moves an item sideways and leaves its height alone.
   *
   * @param axis 0 for horizontal, 1 for vertical
   */
  public static double alignFraction(IExpr value, int axis) {
    if (value == null) {
      return Double.NaN;
    }
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.Left:
          return axis == 0 ? 0.0 : Double.NaN;
        case ID.Right:
          return axis == 0 ? 1.0 : Double.NaN;
        case ID.Top:
          return axis == 1 ? 0.0 : Double.NaN;
        case ID.Bottom:
          return axis == 1 ? 1.0 : Double.NaN;
        case ID.Center:
          return 0.5;
        default:
          return Double.NaN;
      }
    }
    if (value.isSymbol()) {
      // the relaxed parser lower cases a name it does not know, and Baseline is not a builtin
      String name = value.toString();
      if (name.equalsIgnoreCase("Baseline") || name.equalsIgnoreCase("Axis")) {
        return axis == 1 ? 1.0 : Double.NaN;
      }
      return Double.NaN;
    }
    double fraction = ColorUtil.dbl(value, Double.NaN);
    if (Double.isNaN(fraction)) {
      return Double.NaN;
    }
    return Math.max(0.0, Math.min(1.0, fraction));
  }

  // --------------------------------------------------------------------- resolved lookups

  /** The background of one cell: its own, then its row's, then its column's, then none. */
  public Color backgroundAt(int row, int col) {
    Cell cell = cells[row][col];
    if (cell.background != null) {
      return cell.background;
    }
    if (rowBackgrounds[row] != null) {
      return rowBackgrounds[row];
    }
    return colBackgrounds[col];
  }

  /** Horizontal placement of one cell, defaulting to centred. */
  public double alignHAt(int row, int col) {
    double v = cells[row][col].alignH;
    if (!Double.isNaN(v)) {
      return v;
    }
    return Double.isNaN(colAlignH[col]) ? 0.5 : colAlignH[col];
  }

  /** Vertical placement of one cell, defaulting to centred. */
  public double alignVAt(int row, int col) {
    double v = cells[row][col].alignV;
    if (!Double.isNaN(v)) {
      return v;
    }
    return Double.isNaN(rowAlignV[row]) ? 0.5 : rowAlignV[row];
  }

  /** The style directives a cell inherits, or {@code null} when it has none. */
  public IExpr styleAt(int row, int col) {
    Cell cell = cells[row][col];
    if (cell.baseStyle != null) {
      return cell.baseStyle;
    }
    if (rowStyles[row] != null) {
      return rowStyles[row];
    }
    if (colStyles[col] != null) {
      return colStyles[col];
    }
    return baseStyle;
  }

  /**
   * Whether a vertical line at column position {@code p} may be drawn across row {@code row}.
   * A divider never cuts through a cell that spans the position.
   */
  public boolean columnDividerCrosses(int p, int row) {
    if (p <= 0 || p >= cols) {
      return true;
    }
    for (int c = 0; c < p; c++) {
      Cell cell = cells[row][c];
      if (!cell.covered && c + cell.colSpan > p) {
        return false;
      }
    }
    return true;
  }

  /** Whether a horizontal line at row position {@code p} may be drawn down column {@code col}. */
  public boolean rowDividerCrosses(int p, int col) {
    if (p <= 0 || p >= rows) {
      return true;
    }
    for (int r = 0; r < p; r++) {
      Cell cell = cells[r][col];
      if (!cell.covered && r + cell.rowSpan > p) {
        return false;
      }
    }
    return true;
  }
}
