package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
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

  /** Samples of the field along each axis. */
  private static final int SAMPLES = 61;

  /** The longest line as a fraction of the plot's diagonal, for StreamScale -> Automatic. */
  private static final double DEFAULT_LENGTH = 0.15;

  /** The integration step, in plot widths. */
  private static final double STEP = 0.004;

  /** At most this many points are kept along one line. */
  private static final int MAX_POINTS = 30;

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
      return F.NIL;
    }
    if (!(x1 > x0) || !(y1 > y0)) {
      return F.NIL;
    }

    int seeds = DEFAULT_SEEDS;
    double length = DEFAULT_LENGTH;
    boolean colored = true;
    IASTAppendable graphicsOptions = F.ListAlloc();
    for (int i = 4; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      if (key == S.StreamPoints) {
        IExpr value = engine.evaluate(option.second());
        IExpr first = value.isList() && value.argSize() > 0 ? value.first() : value;
        int n = first.toIntDefault();
        if (n >= 2) {
          seeds = n;
        }
        continue;
      }
      if (key == S.StreamScale) {
        IExpr value = engine.evaluate(option.second());
        IExpr first = value.isList() && value.argSize() > 0 ? value.first() : value;
        length = streamLength(first, length);
        continue;
      }
      if (key == S.StreamColorFunction) {
        colored = !engine.evaluate(option.second()).isNone();
        continue;
      }
      graphicsOptions.append(option);
    }

    // the field, sampled once; u and v are the plot's coordinates scaled to 0..1
    double[][] du = new double[SAMPLES][SAMPLES];
    double[][] dv = new double[SAMPLES][SAMPLES];
    double[][] speed = new double[SAMPLES][SAMPLES];
    double fastest = 0.0;
    for (int i = 0; i < SAMPLES; i++) {
      for (int j = 0; j < SAMPLES; j++) {
        double px = x0 + (x1 - x0) * i / (SAMPLES - 1);
        double py = y0 + (y1 - y0) * j / (SAMPLES - 1);
        double[] vector = fieldAt(field, x, y, px, py, engine);
        if (vector == null) {
          du[i][j] = Double.NaN;
          dv[i][j] = Double.NaN;
          speed[i][j] = Double.NaN;
          continue;
        }
        du[i][j] = vector[0] / (x1 - x0);
        dv[i][j] = vector[1] / (y1 - y0);
        speed[i][j] = Math.hypot(vector[0], vector[1]);
        fastest = Math.max(fastest, speed[i][j]);
      }
    }

    // a line stops where it enters a cell another line already went through
    int cells = 2 * seeds;
    int[][] owner = new int[cells][cells];
    double limit = length * Math.sqrt(2.0) / 2;

    IASTAppendable primitives = F.ListAlloc();
    primitives.append(VectorPlot.arrowheads(0.04));
    if (!colored) {
      primitives.append(VectorPlot.color(0.0));
    }
    int id = 0;
    for (int i = 0; i < seeds; i++) {
      for (int j = 0; j < seeds; j++) {
        double u = (i + 0.5) / seeds;
        double v = (j + 0.5) / seeds;
        if (owner[cell(u, cells)][cell(v, cells)] != 0) {
          continue;
        }
        double seedSpeed = interpolate(speed, u, v);
        if (Double.isNaN(seedSpeed) || seedSpeed == 0.0) {
          continue;
        }
        id++;
        List<double[]> backward = trace(du, dv, u, v, -1.0, limit, owner, cells, id);
        List<double[]> forward = trace(du, dv, u, v, 1.0, limit, owner, cells, id);
        Collections.reverse(backward);
        List<double[]> line = new ArrayList<>(backward);
        line.add(new double[] {u, v});
        line.addAll(forward);
        if (line.size() < 2) {
          continue;
        }
        for (double[] p : line) {
          owner[cell(p[0], cells)][cell(p[1], cells)] = id;
        }
        IAST arrow = F.Arrow(points(line, x0, x1, y0, y1));
        primitives.append(colored && fastest > 0.0
            ? F.list(VectorPlot.color(seedSpeed / fastest), arrow)
            : arrow);
      }
    }

    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!hasOption(graphicsOptions, S.PlotRange)) {
      result.append(F.Rule(S.PlotRange,
          F.list(F.list(F.num(x0), F.num(x1)), F.list(F.num(y0), F.num(y1)))));
    }
    if (!hasOption(graphicsOptions, S.Frame)) {
      result.append(F.Rule(S.Frame, S.True));
    }
    if (!hasOption(graphicsOptions, S.AspectRatio)) {
      result.append(F.Rule(S.AspectRatio, F.C1));
    }
    return result;
  }

  /**
   * Follows the field's direction from <code>(u, v)</code>, forwards for <code>sign = 1</code>
   * and backwards for <code>sign = -1</code>, with midpoint steps; the seed itself is not part of
   * the result.
   */
  private static List<double[]> trace(double[][] du, double[][] dv, double u, double v,
      double sign, double limit, int[][] owner, int cells, int id) {
    List<double[]> path = new ArrayList<>();
    double travelled = 0.0;
    while (travelled < limit) {
      double[] d1 = direction(du, dv, u, v, sign);
      if (d1 == null) {
        break;
      }
      double[] d2 = direction(du, dv, u + d1[0] * STEP / 2, v + d1[1] * STEP / 2, sign);
      if (d2 == null) {
        break;
      }
      double nu = u + d2[0] * STEP;
      double nv = v + d2[1] * STEP;
      if (nu < 0.0 || nu > 1.0 || nv < 0.0 || nv > 1.0) {
        break;
      }
      int taken = owner[cell(nu, cells)][cell(nv, cells)];
      if (taken != 0 && taken != id) {
        break;
      }
      u = nu;
      v = nv;
      path.add(new double[] {u, v});
      travelled += STEP;
    }
    return path;
  }

  /** The unit direction of the field at <code>(u, v)</code>, or <code>null</code>. */
  private static double[] direction(double[][] du, double[][] dv, double u, double v,
      double sign) {
    if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0) {
      return null;
    }
    double a = interpolate(du, u, v);
    double b = interpolate(dv, u, v);
    double norm = Math.hypot(a, b);
    if (Double.isNaN(norm) || norm < 1.0e-12) {
      return null;
    }
    return new double[] {sign * a / norm, sign * b / norm};
  }

  /** Bilinear interpolation in a grid of samples over 0..1 x 0..1. */
  private static double interpolate(double[][] grid, double u, double v) {
    double fi = u * (SAMPLES - 1);
    double fj = v * (SAMPLES - 1);
    int i = Math.min((int) fi, SAMPLES - 2);
    int j = Math.min((int) fj, SAMPLES - 2);
    double s = fi - i;
    double t = fj - j;
    return (1 - s) * (1 - t) * grid[i][j] + s * (1 - t) * grid[i + 1][j]
        + (1 - s) * t * grid[i][j + 1] + s * t * grid[i + 1][j + 1];
  }

  private static int cell(double w, int cells) {
    return Math.min(cells - 1, Math.max(0, (int) (w * cells)));
  }

  /** The line in plot coordinates, thinned to at most {@link #MAX_POINTS} points. */
  private static IAST points(List<double[]> line, double x0, double x1, double y0, double y1) {
    int stride = Math.max(1, (line.size() + MAX_POINTS - 1) / MAX_POINTS);
    IASTAppendable result = F.ListAlloc(line.size() / stride + 2);
    for (int k = 0; k < line.size(); k += stride) {
      result.append(point(line.get(k), x0, x1, y0, y1));
    }
    if ((line.size() - 1) % stride != 0) {
      result.append(point(line.get(line.size() - 1), x0, x1, y0, y1));
    }
    return result;
  }

  private static IAST point(double[] p, double x0, double x1, double y0, double y1) {
    return F.list(F.num(x0 + p[0] * (x1 - x0)), F.num(y0 + p[1] * (y1 - y0)));
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

  /** The field's value at <code>(px, py)</code>, or <code>null</code> where it is not numeric. */
  private static double[] fieldAt(IExpr field, ISymbol x, ISymbol y, double px, double py,
      EvalEngine engine) {
    IExpr value;
    try {
      value = engine.evalQuiet(F.N(F.Block(
          F.list(F.Set(x, F.num(px)), F.Set(y, F.num(py))), field)));
    } catch (RuntimeException rex) {
      return null;
    }
    if (!value.isList2() || !value.first().isReal() || !value.second().isReal()) {
      return null;
    }
    return new double[] {value.first().evalf(), value.second().evalf()};
  }

  private static boolean hasOption(IAST options, IExpr key) {
    for (IExpr option : options) {
      if (option.isRuleAST() && option.first() == key) {
        return true;
      }
    }
    return false;
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
