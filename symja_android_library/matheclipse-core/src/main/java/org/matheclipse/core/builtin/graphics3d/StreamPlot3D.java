package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.graphics.VectorPlot;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>StreamPlot3D({vx, vy, vz}, {x, x0, x1}, {y, y0, y1}, {z, z0, z1})</code> - streamlines of a
 * vector field in space: curves which follow the field, as <code>Line</code>s in a
 * <code>Graphics3D</code>.
 *
 * <p>
 * The field is sampled once on a regular grid and interpolated between the samples. From every
 * seed point a line is traced backwards and forwards along the field's direction until it leaves
 * the box, the field vanishes, it closes on itself or it has run a few box diagonals. Each line is
 * coloured point by point by the field's strength there, through the default gradient of the
 * vector plots scaled over all the lines drawn, unless <code>StreamColorFunction -> None</code>.
 *
 * <p>
 * <code>StreamPoints -> n</code> asks for <code>n</code> lines, 64 by default. The seeds are the
 * largest <code>k * k * k</code> lattice with no more than <code>n</code> points over the middle
 * four fifths of every side, topped up from the lattice twice as fine, nearest the centre of the
 * box first.
 */
public class StreamPlot3D extends AbstractFunctionEvaluator {

  /** Streamlines by default. */
  private static final int DEFAULT_LINES = 64;

  /** At most this many streamlines. */
  private static final int MAX_LINES = 1000;

  /** Samples of the field along each axis. */
  private static final int SAMPLES = 21;

  /** The integration step, as a fraction of the box's diagonal. */
  private static final double STEP = 1.0 / 150.0;

  /** Only every this many integration steps, counted from the seed, become a point of the line. */
  private static final int KEEP_EVERY = 3;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 4 || !Plot3DTools.validRanges3D(ast, S.StreamPlot3D, engine)) {
      return F.NIL;
    }
    ISymbol[] vars = new ISymbol[3];
    double[] min = new double[3];
    double[] max = new double[3];
    if (!Plot3DTools.readBox(ast, S.StreamPlot3D, vars, min, max, engine)) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    if (!field.isList3()) {
      field = engine.evaluate(field);
      if (!field.isList3()) {
        return F.NIL;
      }
    }
    int count = DEFAULT_LINES;
    boolean colored = true;
    for (int i = 5; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first() == S.StreamPoints) {
        IExpr value = engine.evaluate(option.second());
        int n = (value.isList() && value.argSize() > 0 ? value.first() : value).toIntDefault();
        if (n >= 1) {
          count = Math.min(n, MAX_LINES);
        }
      } else if (option.isRuleAST() && option.first() == S.StreamColorFunction) {
        colored = !engine.evaluate(option.second()).isNone();
      }
    }

    double[][][][] grid = sample((IAST) field, vars, min, max, engine);
    if (grid == null) {
      return F.NIL;
    }
    double diagonal = 0.0;
    for (int c = 0; c < 3; c++) {
      diagonal += (max[c] - min[c]) * (max[c] - min[c]);
    }
    diagonal = Math.sqrt(diagonal);

    List<List<double[]>> lines = new ArrayList<>();
    for (double[] seed : seeds(count, min, max)) {
      engine.checkTimeLimit();
      List<double[]> line = streamline(grid, seed, min, max, diagonal);
      if (line.size() >= 2) {
        lines.add(line);
      }
    }
    if (lines.isEmpty()) {
      return F.NIL;
    }

    // the strength of the field along the lines, scaled over all of them
    double low = Double.MAX_VALUE;
    double high = -Double.MAX_VALUE;
    List<double[]> strengths = new ArrayList<>(lines.size());
    for (List<double[]> line : lines) {
      double[] strength = new double[line.size()];
      for (int i = 0; i < strength.length; i++) {
        double[] v = interpolate(grid, line.get(i), min, max);
        strength[i] = v == null ? 0.0 : VectorPlot.norm(v);
        low = Math.min(low, strength[i]);
        high = Math.max(high, strength[i]);
      }
      strengths.add(strength);
    }
    IASTAppendable primitives = F.ListAlloc(lines.size());
    for (int l = 0; l < lines.size(); l++) {
      List<double[]> line = lines.get(l);
      IASTAppendable points = F.ListAlloc(line.size());
      IASTAppendable colors = F.ListAlloc(line.size());
      for (int i = 0; i < line.size(); i++) {
        double[] p = line.get(i);
        points.append(F.List(F.num(p[0]), F.num(p[1]), F.num(p[2])));
        double t = high > low ? (strengths.get(l)[i] - low) / (high - low) : 0.5;
        colors.append(gradientColor(t));
      }
      primitives.append(colored ? F.binaryAST2(S.Line, points, F.Rule(S.VertexColors, colors))
          : F.unaryAST1(S.Line, points));
    }
    return Plot3DTools.graphics3D(primitives, ast, 4,
        Plot3DTools.cubeDefaults(min, max, false));
  }

  /**
   * The field's components on a regular grid of {@link #SAMPLES} points per axis, as
   * <code>grid[i][j][k][c]</code>; a sample without a numeric value is <code>null</code>.
   *
   * @return <code>null</code> if no sample has a value
   */
  private static double[][][][] sample(IAST field, ISymbol[] vars, double[] min, double[] max,
      EvalEngine engine) {
    MultiVariateNumerical[] components = new MultiVariateNumerical[3];
    try {
      for (int c = 0; c < 3; c++) {
        components[c] = new MultiVariateNumerical(field.get(c + 1), F.List(vars));
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
    double[][][][] grid = new double[SAMPLES][SAMPLES][SAMPLES][];
    boolean any = false;
    double[] at = new double[3];
    for (int i = 0; i < SAMPLES; i++) {
      engine.checkTimeLimit();
      at[0] = min[0] + (max[0] - min[0]) * i / (SAMPLES - 1);
      for (int j = 0; j < SAMPLES; j++) {
        at[1] = min[1] + (max[1] - min[1]) * j / (SAMPLES - 1);
        for (int k = 0; k < SAMPLES; k++) {
          at[2] = min[2] + (max[2] - min[2]) * k / (SAMPLES - 1);
          double[] v = new double[3];
          boolean finite = true;
          for (int c = 0; c < 3 && finite; c++) {
            v[c] = components[c].value(at);
            finite = Double.isFinite(v[c]);
          }
          if (finite) {
            grid[i][j][k] = v;
            any = true;
          }
        }
      }
    }
    return any ? grid : null;
  }

  /**
   * The field at <code>p</code>, interpolated between the eight samples around it; <code>null</code>
   * where one of them has no value.
   */
  private static double[] interpolate(double[][][][] grid, double[] p, double[] min,
      double[] max) {
    int[] cell = new int[3];
    double[] f = new double[3];
    for (int c = 0; c < 3; c++) {
      double u = (p[c] - min[c]) / (max[c] - min[c]) * (SAMPLES - 1);
      if (!(u >= -1e-9 && u <= SAMPLES - 1 + 1e-9)) {
        return null;
      }
      cell[c] = Math.max(0, Math.min(SAMPLES - 2, (int) Math.floor(u)));
      f[c] = Math.max(0.0, Math.min(1.0, u - cell[c]));
    }
    double[] result = new double[3];
    for (int corner = 0; corner < 8; corner++) {
      int di = corner & 1;
      int dj = (corner >> 1) & 1;
      int dk = (corner >> 2) & 1;
      double weight = (di == 1 ? f[0] : 1.0 - f[0]) * (dj == 1 ? f[1] : 1.0 - f[1])
          * (dk == 1 ? f[2] : 1.0 - f[2]);
      double[] v = grid[cell[0] + di][cell[1] + dj][cell[2] + dk];
      if (v == null) {
        if (weight > 0.0) {
          return null;
        }
        continue;
      }
      for (int c = 0; c < 3; c++) {
        result[c] += weight * v[c];
      }
    }
    return result;
  }

  /**
   * The seeds of <code>count</code> streamlines: the largest lattice of <code>k * k * k</code>
   * points that fits, spread over the middle four fifths of every side, then points of the lattice
   * twice as fine, nearest the centre of the box first.
   */
  private static List<double[]> seeds(int count, double[] min, double[] max) {
    int k = 1;
    while ((k + 1) * (k + 1) * (k + 1) <= count) {
      k++;
    }
    List<double[]> seeds = lattice(k, min, max);
    if (seeds.size() > count) {
      seeds = new ArrayList<>(seeds.subList(0, count));
    }
    if (seeds.size() < count) {
      List<double[]> extra = new ArrayList<>();
      for (double[] p : lattice(2 * k, min, max)) {
        boolean taken = false;
        for (double[] q : seeds) {
          if (distance(p, q) < 1e-9 * (1.0 + Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]))) {
            taken = true;
            break;
          }
        }
        if (!taken) {
          extra.add(p);
        }
      }
      // a stable sort keeps the lattice order among points equally far from the centre
      extra.sort(java.util.Comparator.comparingLong(p -> Math.round(offCentre(p, min, max) * 1e9)));
      for (int i = 0; i < extra.size() && seeds.size() < count; i++) {
        seeds.add(extra.get(i));
      }
    }
    return seeds;
  }

  /** The <code>k * k * k</code> lattice over the middle four fifths of the box. */
  private static List<double[]> lattice(int k, double[] min, double[] max) {
    List<double[]> points = new ArrayList<>(k * k * k);
    for (int i = 0; i < k; i++) {
      for (int j = 0; j < k; j++) {
        for (int l = 0; l < k; l++) {
          int[] index = {i, j, l};
          double[] p = new double[3];
          for (int c = 0; c < 3; c++) {
            double fraction = k == 1 ? 0.5 : 0.1 + 0.8 * index[c] / (k - 1);
            p[c] = min[c] + fraction * (max[c] - min[c]);
          }
          points.add(p);
        }
      }
    }
    return points;
  }

  /** The squared distance from the centre of the box, measured in sides of the box. */
  private static double offCentre(double[] p, double[] min, double[] max) {
    double sum = 0.0;
    for (int c = 0; c < 3; c++) {
      double f = (p[c] - min[c]) / (max[c] - min[c]) - 0.5;
      sum += f * f;
    }
    return sum;
  }

  private static double distance(double[] p, double[] q) {
    double dx = p[0] - q[0];
    double dy = p[1] - q[1];
    double dz = p[2] - q[2];
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * The streamline through <code>seed</code>: traced backwards, then forwards, keeping every
   * {@link #KEEP_EVERY}th point counted from the seed, the seed itself and both ends.
   */
  private static List<double[]> streamline(double[][][][] grid, double[] seed, double[] min,
      double[] max, double diagonal) {
    List<double[]> backwards = trace(grid, seed, -1.0, min, max, diagonal);
    List<double[]> forwards = trace(grid, seed, 1.0, min, max, diagonal);
    List<double[]> all = new ArrayList<>(backwards.size() + forwards.size() + 1);
    for (int i = backwards.size() - 1; i >= 0; i--) {
      all.add(backwards.get(i));
    }
    int seedIndex = all.size();
    all.add(seed);
    all.addAll(forwards);
    List<double[]> line = new ArrayList<>();
    for (int i = 0; i < all.size(); i++) {
      if (Math.abs(i - seedIndex) % KEEP_EVERY == 0 || i == 0 || i == all.size() - 1) {
        line.add(all.get(i));
      }
    }
    return line;
  }

  /**
   * One direction of a streamline: unit speed Runge-Kutta steps from the seed, the seed itself not
   * included.
   */
  private static List<double[]> trace(double[][][][] grid, double[] seed, double sign,
      double[] min, double[] max, double diagonal) {
    double step = diagonal * STEP;
    int steps = (int) (4.0 / STEP);
    List<double[]> points = new ArrayList<>();
    double[] p = seed;
    for (int n = 0; n < steps; n++) {
      double[] k1 = direction(grid, p, sign, min, max);
      double[] k2 = k1 == null ? null : direction(grid, add(p, k1, step / 2.0), sign, min, max);
      double[] k3 = k2 == null ? null : direction(grid, add(p, k2, step / 2.0), sign, min, max);
      double[] k4 = k3 == null ? null : direction(grid, add(p, k3, step), sign, min, max);
      if (k4 == null) {
        break;
      }
      double[] next = new double[3];
      for (int c = 0; c < 3; c++) {
        next[c] = p[c] + step / 6.0 * (k1[c] + 2.0 * k2[c] + 2.0 * k3[c] + k4[c]);
      }
      if (!inside(next, min, max)) {
        break;
      }
      points.add(next);
      if (points.size() > 20 && distance(next, seed) < step) {
        // a closed orbit is traced once
        break;
      }
      p = next;
    }
    return points;
  }

  /** The unit direction of the field at <code>p</code>, times <code>sign</code>. */
  private static double[] direction(double[][][][] grid, double[] p, double sign, double[] min,
      double[] max) {
    double[] v = interpolate(grid, p, min, max);
    if (v == null) {
      return null;
    }
    double length = VectorPlot.norm(v);
    if (!(length > 1e-12)) {
      return null;
    }
    return new double[] {sign * v[0] / length, sign * v[1] / length, sign * v[2] / length};
  }

  private static double[] add(double[] p, double[] v, double factor) {
    return new double[] {p[0] + factor * v[0], p[1] + factor * v[1], p[2] + factor * v[2]};
  }

  private static boolean inside(double[] p, double[] min, double[] max) {
    for (int c = 0; c < 3; c++) {
      double tolerance = (max[c] - min[c]) * 1e-9;
      if (!(p[c] >= min[c] - tolerance && p[c] <= max[c] + tolerance)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The colour for the field's strength <code>t</code>, 0 the weakest and 1 the strongest, of the
   * default gradient of the vector plots, as an <code>RGBColor</code>.
   */
  static IAST gradientColor(double t) {
    IAST lab = VectorPlot.color(t);
    double[] xyz = ColorUtil.labToXyz(lab.arg1().evalf(), lab.arg2().evalf(), lab.arg3().evalf());
    double[] rgb = ColorUtil.xyzToRgb(xyz[0], xyz[1], xyz[2]);
    return F.RGBColor(F.num(round(rgb[0])), F.num(round(rgb[1])), F.num(round(rgb[2])));
  }

  /** Six decimals are all a colour needs. */
  private static double round(double value) {
    return Math.round(value * 1e6) / 1e6;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
