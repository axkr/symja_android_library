package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * The check every function plot makes of its ranges: a range <code>{x, xmin, xmax}</code> whose
 * endpoints coincide spans nothing to plot, and the reference implementation refuses it with
 * <code>plld</code> rather than drawing an empty or degenerate picture.
 */
public final class PlotEndpoints {

  /** The wording of <code>ComplexPlot::plld</code>, whose range is a rectangle of corners. */
  private static final IStringX CORNERS = F.stringx(
      "Corners for `1` in `2` must have distinct machine-precision real and imaginary parts.");

  private PlotEndpoints() {}

  /**
   * Whether one of the ranges at positions <code>from</code> to <code>to</code> of
   * <code>ast</code> has numeric endpoints which coincide, in which case the <code>plld</code>
   * message of <code>head</code> has been printed. A range which is not numeric is left to the
   * plot's own parsing, which has its own message for it.
   *
   * @param realEndpoints whether the message shows the endpoints as machine reals, as
   *        <code>RegionPlot3D</code> does
   */
  public static boolean degenerate(ISymbol head, IAST ast, int from, int to,
      boolean realEndpoints, EvalEngine engine) {
    for (int i = from; i <= to && i < ast.size(); i++) {
      IExpr range = ast.get(i);
      if (!range.isList() || range.argSize() < 3 || !range.first().isSymbol()) {
        continue;
      }
      double min = engine.evaluate(range.second()).evalfNaN();
      double max = engine.evaluate(range.get(3)).evalfNaN();
      if (Double.isFinite(min) && min == max) {
        IExpr shown = realEndpoints
            ? ((IAST) range).setAtCopy(2, F.num(min)).setAtCopy(3, F.num(max))
            : range;
        // Endpoints for `1` in `2` must have distinct machine-precision numerical values.
        Errors.printMessage(head, "plld", F.List(range.first(), shown), engine);
        return true;
      }
    }
    return false;
  }

  /**
   * Whether the corners <code>min</code> and <code>max</code> of the range of a complex plot fail
   * to span a rectangle, in which case the plot's <code>plld</code> message has been printed.
   */
  public static boolean degenerateCorners(ISymbol head, IAST range, double minRe, double minIm,
      double maxRe, double maxIm, EvalEngine engine) {
    if (minRe != maxRe && minIm != maxIm) {
      return false;
    }
    Errors.printMessage(head, "plld", F.List(range.first(), range), engine);
    return true;
  }

  /** Give a complex plot the corner wording of <code>plld</code>. */
  public static void cornerMessage(ISymbol symbol) {
    symbol.putMessage(IPatternMatcher.SET, "plld", CORNERS);
  }
}
