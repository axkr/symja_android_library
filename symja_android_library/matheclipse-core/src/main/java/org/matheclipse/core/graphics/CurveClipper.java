package org.matheclipse.core.graphics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cuts the curves of a plot at an explicit vertical <code>PlotRange</code>.
 *
 * <p>
 * A curve which leaves the range is split into the pieces which lie inside it; the end points of a
 * piece are moved onto the boundary where the curve crosses it. The stretches outside are dropped,
 * or - with a <code>ClippingStyle</code> - drawn in that style as straight lines along the boundary
 * they went past.
 */
public final class CurveClipper {

  private CurveClipper() {}

  /**
   * The bottom and the top a curve is cut at: the ends of the vertical part of
   * <code>plotRange</code> which are numbers, the others infinite; <code>null</code> if the range
   * gives no number for the vertical axis.
   *
   * @param plotRange <code>{ymin, ymax}</code> or <code>{xrange, {ymin, ymax}}</code>
   */
  public static double[] verticalBounds(IExpr plotRange) {
    if (plotRange == null || !plotRange.isList2()) {
      return null;
    }
    IExpr yPart = plotRange;
    if (plotRange.first().isList() || plotRange.second().isList()) {
      yPart = plotRange.second();
    }
    if (!yPart.isList2()) {
      return null;
    }
    double bottom = yPart.first().isReal() ? yPart.first().evalfNaN() : Double.NaN;
    double top = yPart.second().isReal() ? yPart.second().evalfNaN() : Double.NaN;
    if (!Double.isFinite(bottom) && !Double.isFinite(top)) {
      return null;
    }
    if (Double.isFinite(bottom) && Double.isFinite(top) && bottom > top) {
      double t = bottom;
      bottom = top;
      top = t;
    }
    return new double[] {Double.isFinite(bottom) ? bottom : Double.NEGATIVE_INFINITY,
        Double.isFinite(top) ? top : Double.POSITIVE_INFINITY};
  }

  /**
   * The primitives with every <code>Line</code> cut at <code>bounds</code>.
   *
   * @param primitives the primitives of a plot
   * @param bounds the bottom and the top of the range
   * @param clippingStyle the style of the stretches outside the range: <code>None</code> drops
   *        them, <code>Automatic</code> dashes them, <code>{bottomStyle, topStyle}</code> gives a
   *        style for each side
   */
  public static IAST clip(IAST primitives, double[] bounds, IExpr clippingStyle) {
    IExpr bottomStyle = clippingStyle;
    IExpr topStyle = clippingStyle;
    if (clippingStyle.isList2()) {
      bottomStyle = clippingStyle.first();
      topStyle = clippingStyle.second();
    }
    return new CurveClipper.Run(bounds, styleOrNull(bottomStyle), styleOrNull(topStyle))
        .clipList(primitives);
  }

  private static IExpr styleOrNull(IExpr style) {
    if (style == null || style.isNone() || style.isFalse()) {
      return null;
    }
    // the curve's own style, dashed, so it reads as "the curve continues past here"
    return style.isAutomatic() ? F.Dashing(F.List(S.Small, S.Small)) : style;
  }

  /** One clipping pass with its bounds and styles. */
  private static final class Run {
    private final double bottom;
    private final double top;
    private final IExpr bottomStyle;
    private final IExpr topStyle;

    Run(double[] bounds, IExpr bottomStyle, IExpr topStyle) {
      this.bottom = bounds[0];
      this.top = bounds[1];
      this.bottomStyle = bottomStyle;
      this.topStyle = topStyle;
    }

    IAST clipList(IAST list) {
      IASTAppendable result = null;
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        IExpr clipped = clipElement(element);
        if (clipped != element && result == null) {
          result = F.ast(list.head(), list.argSize() + 4);
          result.appendArgs(list, i);
        }
        if (result != null) {
          if (clipped.isAST(S.Sequence)) {
            result.appendArgs((IAST) clipped);
          } else {
            result.append(clipped);
          }
        }
      }
      return result == null ? list : result;
    }

    /**
     * The element with its lines cut, the element itself if nothing changed, or a
     * <code>Sequence</code> of primitives which takes the place of a <code>Line</code>.
     */
    private IExpr clipElement(IExpr element) {
      if (element.isList()) {
        return clipList((IAST) element);
      }
      if (element.isAST(S.Line, 2) && element.first().isList()) {
        return clipLine((IAST) element.first(), element);
      }
      if ((element.isAST(S.Tooltip) || element.isAST(S.Annotation) || element.isAST(S.Style)
          || element.isAST(S.GraphicsGroup)) && element.argSize() >= 1) {
        IExpr inner = element.first();
        IExpr clipped = inner.isList() ? clipList((IAST) inner) : clipElement(inner);
        if (clipped == inner) {
          return element;
        }
        if (clipped.isAST(S.Sequence)) {
          clipped = ((IAST) clipped).setAtCopy(0, S.List);
        }
        return ((IAST) element).setAtCopy(1, clipped);
      }
      return element;
    }

    private IExpr clipLine(IAST points, IExpr line) {
      if (points.argSize() == 0) {
        return line;
      }
      IASTAppendable inside = F.ListAlloc();
      IASTAppendable bottomRuns = F.ListAlloc();
      IASTAppendable topRuns = F.ListAlloc();
      if (points.arg1().isList() && points.arg1().argSize() > 0 && points.arg1().first().isList()) {
        // a line of several polylines
        for (int i = 1; i < points.size(); i++) {
          if (!(points.get(i).isList()
              && clipPolyline((IAST) points.get(i), inside, bottomRuns, topRuns))) {
            return line;
          }
        }
      } else if (!clipPolyline(points, inside, bottomRuns, topRuns)) {
        return line;
      }
      if (bottomRuns.argSize() == 0 && topRuns.argSize() == 0) {
        return line;
      }
      IASTAppendable sequence = F.ast(S.Sequence, inside.argSize() + 2);
      for (IExpr piece : inside) {
        sequence.append(F.Line(piece));
      }
      appendRuns(sequence, bottomStyle, bottomRuns);
      appendRuns(sequence, topStyle, topRuns);
      return sequence;
    }

    private static void appendRuns(IASTAppendable sequence, IExpr style, IAST runs) {
      if (style == null || runs.argSize() == 0) {
        return;
      }
      IASTAppendable group = F.ListAlloc(runs.argSize() + 1);
      group.append(style);
      for (IExpr run : runs) {
        group.append(F.Line(run));
      }
      sequence.append(group);
    }

    /** The side of the range a value lies on: -1 below, 0 inside, 1 above. */
    private int side(double y) {
      return y < bottom ? -1 : (y > top ? 1 : 0);
    }

    /**
     * Cut one polyline.
     *
     * @return <code>false</code> if a point of it is not a pair of real numbers
     */
    private boolean clipPolyline(IAST polyline, IASTAppendable inside, IASTAppendable bottomRuns,
        IASTAppendable topRuns) {
      int n = polyline.argSize();
      double[] xs = new double[n];
      double[] ys = new double[n];
      for (int i = 0; i < n; i++) {
        IExpr point = polyline.get(i + 1);
        if (!point.isList2()) {
          return false;
        }
        xs[i] = point.first().evalfNaN();
        ys[i] = point.second().evalfNaN();
        if (!Double.isFinite(xs[i]) || !Double.isFinite(ys[i])) {
          return false;
        }
      }
      IASTAppendable piece = F.NIL;
      // where the stretch outside the range began
      double runStart = Double.NaN;
      int previousSide = 0;
      for (int i = 0; i < n; i++) {
        int s = side(ys[i]);
        if (i == 0) {
          if (s == 0) {
            piece = F.ListAlloc();
            piece.append(polyline.get(1));
          } else {
            runStart = xs[0];
          }
          previousSide = s;
          continue;
        }
        if (s == previousSide) {
          if (s == 0) {
            piece.append(polyline.get(i + 1));
          }
          continue;
        }
        double x0 = xs[i - 1];
        double y0 = ys[i - 1];
        double x1 = xs[i];
        double y1 = ys[i];
        // the last point of the piece inside the range
        double lastX = x0;
        double lastY = y0;
        if (previousSide != 0) {
          // the curve comes back from outside, or jumps from one side to the other
          double entryLevel = previousSide < 0 ? bottom : top;
          double entryX = crossingX(x0, y0, x1, y1, entryLevel);
          addRun(previousSide, runStart, entryX, bottomRuns, topRuns);
          piece = F.ListAlloc();
          piece.append(F.List(F.num(entryX), F.num(entryLevel)));
          lastX = entryX;
          lastY = entryLevel;
          if (s == 0) {
            appendDistinct(piece, polyline.get(i + 1), lastX, lastY, x1, y1);
            previousSide = 0;
            continue;
          }
        }
        // the curve leaves the range
        double level = s < 0 ? bottom : top;
        double crossX = crossingX(x0, y0, x1, y1, level);
        appendDistinct(piece, F.List(F.num(crossX), F.num(level)), lastX, lastY, crossX, level);
        finishPiece(piece, inside);
        piece = F.NIL;
        runStart = crossX;
        previousSide = s;
      }
      if (previousSide == 0) {
        finishPiece(piece, inside);
      } else {
        addRun(previousSide, runStart, xs[n - 1], bottomRuns, topRuns);
      }
      return true;
    }

    /** Append <code>point</code> unless it is the point <code>(lastX, lastY)</code> already. */
    private static void appendDistinct(IASTAppendable piece, IExpr point, double lastX,
        double lastY, double x, double y) {
      if (lastX != x || lastY != y) {
        piece.append(point);
      }
    }

    private static void finishPiece(IASTAppendable piece, IASTAppendable inside) {
      if (piece.isPresent() && piece.argSize() >= 2) {
        inside.append(piece);
      }
    }

    private void addRun(int side, double fromX, double toX, IASTAppendable bottomRuns,
        IASTAppendable topRuns) {
      if (side < 0) {
        if (bottomStyle != null && Double.isFinite(bottom)) {
          bottomRuns.append(F.List(F.List(F.num(fromX), F.num(bottom)),
              F.List(F.num(toX), F.num(bottom))));
        } else {
          // the stretch is dropped, but it was clipped all the same
          bottomRuns.append(F.CEmptyList);
        }
      } else if (topStyle != null && Double.isFinite(top)) {
        topRuns.append(
            F.List(F.List(F.num(fromX), F.num(top)), F.List(F.num(toX), F.num(top))));
      } else {
        topRuns.append(F.CEmptyList);
      }
    }

    /** The x where the segment from (x0, y0) to (x1, y1) reaches the height <code>level</code>. */
    private static double crossingX(double x0, double y0, double x1, double y1, double level) {
      if (y1 == y0) {
        return x0;
      }
      return x0 + (level - y0) / (y1 - y0) * (x1 - x0);
    }
  }
}
