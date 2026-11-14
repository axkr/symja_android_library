package org.matheclipse.core.builtin.graphics;

import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>StreamPlot[{vx, vy}, {x, xmin, xmax}, {y, ymin, ymax}]</code> - the vector field drawn as
 * streamlines: curves which follow the field, each ending in an arrowhead.
 *
 * <p>
 * The field is sampled once on a regular grid and interpolated between the samples. From a grid of
 * seed points a line is traced forwards and backwards along the field's direction; it stops where it
 * leaves the plot, where the field vanishes or is not numeric, where it runs into another line, or
 * when it reaches the length <code>StreamScale</code> allows. Lines are coloured by the field's speed
 * at their seed unless <code>StreamColorFunction -> None</code>.
 *
 * <p>
 * Options: <code>StreamPoints -> n</code> (seeds along each axis), <code>StreamScale -> Tiny | Small
 * | Medium | Large | s</code> (the longest line as a fraction of the plot's diagonal),
 * <code>StreamColorFunction -> None</code>; any other option is handed on to the
 * <code>Graphics</code>.
 */
public class StreamPlot extends AbstractFunctionEvaluator {

  /** Seed points along each axis. */
  private static final int DEFAULT_SEEDS = 14;

  /** The longest line as a fraction of the plot's diagonal, for StreamScale -> Automatic. */
  private static final double DEFAULT_LENGTH = 0.15;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 3) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    IExpr xIterator = ast.arg2();
    IExpr yIterator = ast.arg3();
    if (!xIterator.isList3() || !xIterator.first().isSymbol() || !yIterator.isList3()
        || !yIterator.first().isSymbol()) {
      return F.NIL;
    }
    ISymbol x = (ISymbol) xIterator.first();
    ISymbol y = (ISymbol) yIterator.first();
    double x0;
    double x1;
    double y0;
    double y1;
    try {
      x0 = engine.evalDouble(((IAST) xIterator).arg2());
      x1 = engine.evalDouble(((IAST) xIterator).arg3());
      y0 = engine.evalDouble(((IAST) yIterator).arg2());
      y1 = engine.evalDouble(((IAST) yIterator).arg3());
    } catch (RuntimeException rex) {
      IAST xRange = (IAST) xIterator;
      boolean xNumeric =
          xRange.arg2().isNumericFunction(true) && xRange.arg3().isNumericFunction(true);
      return VectorPlot.limitingValue(S.StreamPlot, xNumeric ? (IAST) yIterator : xRange, engine);
    }
    if (!(x1 > x0) || !(y1 > y0)) {
      return F.NIL;
    }

    RegionFunctionFilter region = null;
    IExpr spec = S.Automatic;
    IASTAppendable streamOptions = F.ListAlloc();
    IASTAppendable graphicsOptions = F.ListAlloc();
    for (int i = 4; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      if (key == S.StreamPoints) {
        spec = engine.evaluate(option.second());
        continue;
      }
      if (isStreamOption(key)) {
        streamOptions.append(option);
        continue;
      }
      if (key == S.RegionFunction) {
        region = RegionFunctionFilter.of(engine.evaluate(option.second()), engine);
        continue;
      }
      graphicsOptions.append(option);
    }

    IAST primitives =
        streams(field, x, y, x0, x1, y0, y1, region, spec, streamOptions, false, engine);
    if (primitives.isNIL()) {
      primitives = F.CEmptyList;
    }
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!VectorPlot.hasOption(graphicsOptions, S.PlotRange)) {
      result.append(F.Rule(S.PlotRange,
          F.list(F.list(F.num(x0), F.num(x1)), F.list(F.num(y0), F.num(y1)))));
    }
    if (!VectorPlot.hasOption(graphicsOptions, S.Frame)) {
      result.append(F.Rule(S.Frame, S.True));
    }
    if (!VectorPlot.hasOption(graphicsOptions, S.AspectRatio)) {
      result.append(F.Rule(S.AspectRatio, F.C1));
    }
    return result;
  }

  /** The size of the head of an arrow, as a share of the plot's width. */
  private static final double HEAD_SIZE = 0.02;

  /** The length of each arrow a streamline is drawn as, in the plot scaled to the unit square. */
  private static final double SEGMENT_LENGTH = 0.08;

  /** The gap between two arrows of one streamline. */
  private static final double SEGMENT_GAP = 0.002;

  /**
   * A line cut into consecutive pieces of {@link #SEGMENT_LENGTH}, a gap of {@link #SEGMENT_GAP}
   * apart; the last piece is what is left. The cuts are placed on the line between its points.
   */
  private static List<List<double[]>> segments(List<double[]> points) {
    List<List<double[]>> result = new java.util.ArrayList<>();
    List<double[]> piece = new java.util.ArrayList<>();
    piece.add(points.get(0));
    // how far the current piece has run, and whether the line is in a gap
    double run = 0.0;
    boolean inGap = false;
    for (int i = 1; i < points.size(); i++) {
      double[] from = points.get(i - 1);
      double[] to = points.get(i);
      double step = Math.hypot(to[0] - from[0], to[1] - from[1]);
      double done = 0.0;
      while (step - done > 0.0) {
        double limit = inGap ? SEGMENT_GAP : SEGMENT_LENGTH;
        double take = Math.min(limit - run, step - done);
        done += take;
        run += take;
        double t = done / step;
        double[] at = {from[0] + t * (to[0] - from[0]), from[1] + t * (to[1] - from[1])};
        if (run < limit) {
          if (!inGap) {
            piece.add(at);
          }
          break;
        }
        // the end of a piece or of a gap
        run = 0.0;
        if (inGap) {
          piece = new java.util.ArrayList<>();
          piece.add(at);
        } else {
          piece.add(at);
          result.add(piece);
          piece = null;
        }
        inGap = !inGap;
      }
    }
    if (piece != null && piece.size() > 1) {
      result.add(piece);
    }
    return result;
  }

  /** The length of a line in the plot scaled to the unit square. */
  private static double length(List<double[]> points) {
    double sum = 0.0;
    for (int i = 1; i < points.size(); i++) {
      sum += Math.hypot(points.get(i)[0] - points.get(i - 1)[0],
          points.get(i)[1] - points.get(i - 1)[1]);
    }
    return sum;
  }

  /** Whether an option is one of the streamlines'. */
  static boolean isStreamOption(IExpr key) {
    return key == S.StreamPoints || key == S.StreamScale || key == S.StreamStyle
        || key == S.StreamColorFunction || key == S.StreamColorFunctionScaling;
  }

  /**
   * The primitives of the streamlines of the field <code>{vx, vy}</code> over the rectangle from
   * <code>(x0, y0)</code> to <code>(x1, y1)</code>.
   *
   * <p>
   * <code>StreamScale -> None</code> draws each line whole and without a head, as a
   * <code>Line</code>; otherwise a line is an <code>Arrow</code> as long as the scale allows. A
   * line takes the colour of <code>StreamColorFunction</code>, which is handed the point, the
   * vector and its length, each scaled to <code>0..1</code> unless
   * <code>StreamColorFunctionScaling -> False</code>; without a function it is coloured by the
   * field's speed, and with <code>None</code> it is drawn in the <code>StreamStyle</code>.
   *
   * @param spec the <code>StreamPoints</code> value
   * @param options the other options of the streamlines
   * @param overVectors whether the lines are drawn over the arrows of a vector plot, where they are
   *        not coloured unless a function asks for it
   * @return {@link F#NIL} if there are no lines to draw
   */
  static IAST streams(IExpr field, ISymbol x, ISymbol y, double x0, double x1, double y0,
      double y1, RegionFunctionFilter region, IExpr spec, IAST options, boolean overVectors,
      EvalEngine engine) {
    double length = DEFAULT_LENGTH;
    boolean whole = false;
    IExpr style = F.NIL;
    IExpr colorFunction = overVectors ? S.None : S.Automatic;
    IExpr scaling = S.True;
    for (IExpr option : options) {
      IExpr key = option.first();
      IExpr value = engine.evaluate(option.second());
      if (key == S.StreamScale) {
        IExpr first = value.isList() && value.argSize() > 0 ? value.first() : value;
        whole = first.isNone();
        length = streamLength(first, length);
      } else if (key == S.StreamStyle) {
        style = value;
      } else if (key == S.StreamColorFunction) {
        colorFunction = value;
      } else if (key == S.StreamColorFunctionScaling) {
        scaling = value;
      }
    }
    StreamLines streams = new StreamLines(field, x, y, x0, x1, y0, y1, region, engine);
    List<StreamLines.Line> lines = streams.fromSpec(spec, DEFAULT_SEEDS,
        whole ? Double.MAX_VALUE : length * Math.sqrt(2.0) / 2, engine);
    if (lines == null) {
      return F.NIL;
    }
    PlotColorFunction colors = colorFunction.isAutomatic() || colorFunction.isNone() ? null
        : streamColors(colorFunction, scaling, streams, lines, engine);
    boolean bySpeed = colors == null && colorFunction.isAutomatic() && streams.fastest > 0.0;

    IASTAppendable primitives = F.ListAlloc(lines.size() + 2);
    // the style comes once, in front of all the lines, as a Directive of what was given
    if (style.isPresent()) {
      primitives.append(style.isAST(S.Directive) ? style
          : style.isList() ? ((IAST) style).setAtCopy(0, S.Directive)
              : F.unaryAST1(S.Directive, style));
    } else if (colors == null && !bySpeed) {
      primitives.append(VectorPlot.plainColor());
    }
    for (StreamLines.Line line : lines) {
      // a whole line is drawn as one, any other is a run of arrows of SEGMENT_LENGTH
      List<List<double[]>> pieces = whole ? java.util.Collections.singletonList(line.points)
          : segments(line.points);
      for (List<double[]> piece : pieces) {
        IAST points = streams.points(piece);
        IAST drawn = whole ? F.Line(points) : F.Arrow(points);
        // a piece is coloured where it starts
        double[] start = piece.get(0);
        double[] vector = streams.vectorAt(start[0], start[1]);
        double speed = Math.hypot(vector[0], vector[1]);
        if (colors != null) {
          drawn = F.list(colors.color(streams.x0 + start[0] * (streams.x1 - streams.x0),
              streams.y0 + start[1] * (streams.y1 - streams.y0), vector[0], vector[1], speed),
              drawn);
        } else if (bySpeed) {
          drawn = F.list(VectorPlot.color(streams.scaledSpeed(whole ? line.seedSpeed : speed)),
              drawn);
        }
        if (!whole) {
          // every arrow carries its own head, a quarter of its length and at most HEAD_SIZE
          drawn = F.list(VectorPlot.arrowheads(Math.min(HEAD_SIZE, length(piece) / 4.0)), drawn);
        }
        primitives.append(drawn);
      }
    }
    return primitives;
  }

  /**
   * The <code>StreamColorFunction</code>, handed <code>x, y, vx, vy</code> and the speed, each
   * scaled over the range it spans along the lines.
   */
  private static PlotColorFunction streamColors(IExpr colorFunction, IExpr scaling,
      StreamLines streams, List<StreamLines.Line> lines, EvalEngine engine) {
    double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
    for (StreamLines.Line line : lines) {
      for (double[] p : line.points) {
        double[] vector = streams.vectorAt(p[0], p[1]);
        double[] parts = {vector[0], vector[1], Math.hypot(vector[0], vector[1])};
        for (int i = 0; i < 3; i++) {
          if (Double.isFinite(parts[i])) {
            low[i] = Math.min(low[i], parts[i]);
            high[i] = Math.max(high[i], parts[i]);
          }
        }
      }
    }
    return PlotColorFunction
        .of(PlotColorFunction.Family.VECTOR_2D, colorFunction, scaling, S.StreamPlot, engine)
        .ranges(streams.x0, streams.x1, streams.y0, streams.y1, low[0], high[0], low[1], high[1],
            low[2], high[2])
        .build();
  }

  /** The longest line as a fraction of the plot's diagonal. */
  private static double streamLength(IExpr value, double fallback) {
    if (value.isReal()) {
      double s = value.evalf();
      return s > 0.0 ? s : fallback;
    }
    if (value.isSymbol()) {
      switch (((ISymbol) value).getSymbolName()) {
        case "Tiny":
          return 0.05;
        case "Small":
          return 0.1;
        case "Medium":
          return 0.15;
        case "Large":
          return 0.3;
        case "None":
          return 2.0;
        default:
          return fallback;
      }
    }
    return fallback;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
