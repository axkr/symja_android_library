package org.matheclipse.core.graphics.svg;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.distribution.continuous.NormalDistribution;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The tick specifications <code>Charting`ScaledTicks[{f, finv}]</code> and
 * <code>Charting`ScaledFrameTicks[{f, finv}]</code> of an axis whose coordinates are
 * <code>f</code> of the values it is labelled with. <code>ScaledFrameTicks</code> is the opposite
 * frame edge: the same tick marks without labels.
 *
 * <p>
 * Only the scales of the probability scale plots are known, a pair naming any other function gets
 * the automatic ticks:
 * <ul>
 * <li><code>{"NormalY", "NormalYInverse"}</code> - percent at <code>1 + q</code>, where
 * <code>q</code> is the standard normal quantile of the probability</li>
 * <li><code>{"WeibullY", "WeibullYInverse"}</code> - percent at <code>Log(-Log(1 - p))</code></li>
 * <li><code>{Log, Exp}</code> - 1, 2 and 5 times the powers of ten at their logarithms</li>
 * </ul>
 */
public final class ScaledTicks {

  /** The context of the two heads, which are not built-in symbols. */
  private static final String CONTEXT = "Charting`";

  private static final String TICKS = "ScaledTicks";

  private static final String FRAME_TICKS = "ScaledFrameTicks";

  /** The percentages labelled on a probability scale. */
  private static final int[] PERCENTS = {1, 5, 10, 25, 50, 75, 90, 95, 99};

  private static final NormalDistribution STANDARD_NORMAL = new NormalDistribution(0.0, 1.0);

  private ScaledTicks() {}

  /** The scale of a normal probability plot. */
  public static IAST normalPair() {
    return F.list(F.stringx("NormalY"), F.stringx("NormalYInverse"));
  }

  /** The scale of a Weibull probability plot. */
  public static IAST weibullPair() {
    return F.list(F.stringx("WeibullY"), F.stringx("WeibullYInverse"));
  }

  /** A logarithmic scale. */
  public static IAST logPair() {
    return F.list(S.Log, S.Exp);
  }

  /**
   * <code>{Charting`ScaledTicks[pair], Charting`ScaledFrameTicks[pair]}</code>: the ticks of the
   * two frame edges along one axis.
   */
  public static IAST edges(IAST pair, EvalEngine engine) {
    return F.list(F.unaryAST1(symbol(TICKS, engine), pair),
        F.unaryAST1(symbol(FRAME_TICKS, engine), pair));
  }

  private static ISymbol symbol(String name, EvalEngine engine) {
    return ContextPath.getSymbol(name, engine.getContextPath().getContext(CONTEXT), false);
  }

  /** The height of the probability <code>p</code> on a normal probability scale. */
  public static double normalY(double p) {
    return 1.0 + STANDARD_NORMAL.inverseCumulativeProbability(p);
  }

  /** The height of the probability <code>p</code> on a Weibull probability scale. */
  public static double weibullY(double p) {
    return Math.log(-Math.log1p(-p));
  }

  /**
   * The ticks of a scaled tick specification over the axis from <code>min</code> to
   * <code>max</code>; ticks outside of it are left to the caller to drop.
   *
   * @return {@code null} if <code>spec</code> is not a scaled specification of a known scale
   */
  static List<TickGenerator.Tick> ticks(IExpr spec, double min, double max) {
    if (!spec.isAST1() || !spec.head().isSymbol()) {
      return null;
    }
    ISymbol head = (ISymbol) spec.head();
    if (!CONTEXT.equals(head.getContext().getContextName())) {
      return null;
    }
    boolean labels = TICKS.equals(head.getSymbolName());
    if (!labels && !FRAME_TICKS.equals(head.getSymbolName())) {
      return null;
    }
    IExpr pair = spec.first();
    if (!pair.isList2()) {
      return null;
    }
    List<TickGenerator.Tick> ticks = new ArrayList<>();
    if (pair.first().isString("NormalY") || pair.first().isString("WeibullY")) {
      boolean normal = pair.first().isString("NormalY");
      for (int percent : PERCENTS) {
        double position = normal ? normalY(percent / 100.0) : weibullY(percent / 100.0);
        ticks.add(new TickGenerator.Tick(position, labels ? Integer.toString(percent) : "", true));
      }
      return ticks;
    }
    if (pair.first() == S.Log && pair.second() == S.Exp && Double.isFinite(min)
        && Double.isFinite(max)) {
      final double ln10 = Math.log(10.0);
      int first = (int) Math.floor(Math.min(min, max) / ln10);
      // a few hundred decades are all a double has
      int last = Math.min(first + 700, (int) Math.ceil(Math.max(min, max) / ln10));
      for (int decade = first; decade <= last; decade++) {
        for (int mantissa : new int[] {1, 2, 5}) {
          double position = Math.log(mantissa) + decade * ln10;
          String label = labels ? TickGenerator.trim(mantissa * Math.pow(10.0, decade)) : "";
          ticks.add(new TickGenerator.Tick(position, label, true));
        }
      }
      return ticks;
    }
    return null;
  }
}
