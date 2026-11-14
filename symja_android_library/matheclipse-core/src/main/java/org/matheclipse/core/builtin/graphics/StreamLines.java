package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The streamlines of a plane vector field over a rectangle: curves which follow the field.
 *
 * <p>
 * The field is sampled once on a regular grid and interpolated between the samples. From a seed a
 * line is traced forwards and backwards along the field's direction; it stops where it leaves the
 * plot, where the field vanishes or is not numeric, where it runs into another line, or when it
 * reaches the length it may have. Coordinates <code>u, v</code> are the plot's coordinates scaled
 * to <code>0..1</code>.
 */
final class StreamLines {

  /** Samples of the field along each axis. */
  private static final int SAMPLES = 61;

  /** The integration step, in plot widths. */
  private static final double STEP = 0.004;

  /** At most this many points are kept along one line. */
  private static final int MAX_POINTS = 30;

  /** One streamline: its points in scaled coordinates and the field's speed at its seed. */
  static final class Line {
    final List<double[]> points;
    final double seedSpeed;

    Line(List<double[]> points, double seedSpeed) {
      this.points = points;
      this.seedSpeed = seedSpeed;
    }
  }

  final double x0;
  final double x1;
  final double y0;
  final double y1;
  private final double[][] du = new double[SAMPLES][SAMPLES];
  private final double[][] dv = new double[SAMPLES][SAMPLES];
  private final double[][] speed = new double[SAMPLES][SAMPLES];
  private final RegionFunctionFilter region;

  /** The highest speed among the samples. */
  double fastest = 0.0;

  /** The lowest speed among the samples. */
  double slowest = Double.MAX_VALUE;

  /**
   * Samples the field <code>{vx, vy}</code> in the variables <code>x, y</code>.
   *
   * @param region the <code>RegionFunction</code>, or <code>null</code>
   */
  StreamLines(IExpr field, ISymbol x, ISymbol y, double x0, double x1, double y0, double y1,
      RegionFunctionFilter region, EvalEngine engine) {
    this.x0 = x0;
    this.x1 = x1;
    this.y0 = y0;
    this.y1 = y1;
    this.region = region;
    ISymbol[] variables = {x, y};
    for (int i = 0; i < SAMPLES; i++) {
      engine.checkTimeLimit();
      for (int j = 0; j < SAMPLES; j++) {
        double px = x0 + (x1 - x0) * i / (SAMPLES - 1);
        double py = y0 + (y1 - y0) * j / (SAMPLES - 1);
        double[] vector = VectorPlot.fieldAt(field, variables, new double[] {px, py}, engine);
        if (vector == null || (region != null && !region.accepts(px, py, vector[0], vector[1],
            Math.hypot(vector[0], vector[1])))) {
          // no field here, or outside the region: no line starts at or runs through this sample
          du[i][j] = Double.NaN;
          dv[i][j] = Double.NaN;
          speed[i][j] = Double.NaN;
          continue;
        }
        du[i][j] = vector[0] / (x1 - x0);
        dv[i][j] = vector[1] / (y1 - y0);
        speed[i][j] = Math.hypot(vector[0], vector[1]);
        fastest = Math.max(fastest, speed[i][j]);
        slowest = Math.min(slowest, speed[i][j]);
      }
    }
  }

  /**
   * The lines a <code>StreamPoints</code> value asks for: <code>n</code> seeds along each axis,
   * <code>Coarse</code> or <code>Fine</code> for fewer or more than <code>automatic</code>, a list
   * of points to draw a line through each, or one of these as the first element of
   * <code>{spec, dmin}</code> or <code>{spec, dmin, lmax}</code>.
   *
   * @param automatic the seeds along each axis for <code>Automatic</code>
   * @param limit the longest a line may run either way from its seed, in plot widths
   * @return the lines, or <code>null</code> if <code>spec</code> asks for none
   */
  List<Line> fromSpec(IExpr spec, int automatic, double limit, EvalEngine engine) {
    double[][] points = points(spec);
    if (points != null) {
      return fromPoints(points, limit, engine);
    }
    if (spec.isList() && spec.argSize() >= 1 && spec.argSize() <= 3) {
      return fromSpec(spec.first(), automatic, limit, engine);
    }
    int n = seedCount(spec, automatic);
    return n > 0 ? fromGrid(n, limit, engine) : null;
  }

  /**
   * The seeds along each axis a <code>StreamPoints</code> value asks for.
   *
   * @return <code>0</code> for <code>None</code> or a value which is no count
   */
  static int seedCount(IExpr spec, int automatic) {
    if (spec.isAutomatic()) {
      return automatic;
    }
    if (spec == S.Coarse) {
      return Math.max(2, (int) Math.round(automatic * COARSE));
    }
    if (spec.isSymbol()) {
      // Fine has no built-in symbol yet
      if (((ISymbol) spec).getSymbolName().equals("Fine")) {
        return (int) Math.round(automatic * FINE);
      }
      return 0;
    }
    int n = spec.toIntDefault();
    // some 40000 seeds are plenty
    return n >= 1 ? Math.min(n, 200) : 0;
  }

  /** How many seeds <code>Coarse</code> and <code>Fine</code> take of the automatic count. */
  private static final double COARSE = 0.6;

  private static final double FINE = 1.6;

  /** The points of a list of real pairs, or <code>null</code>. */
  private static double[][] points(IExpr spec) {
    if (!spec.isListOfLists() || spec.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) spec;
    double[][] result = new double[list.argSize()][];
    for (int k = 1; k < list.size(); k++) {
      IExpr p = list.get(k);
      if (!p.isList2()) {
        return null;
      }
      double px = p.first().evalfNaN();
      double py = p.second().evalfNaN();
      if (!Double.isFinite(px) || !Double.isFinite(py)) {
        return null;
      }
      result[k - 1] = new double[] {px, py};
    }
    return result;
  }

  /**
   * The lines from seeds at the centres of an <code>n x n</code> grid of cells. A seed in a cell
   * an earlier line already went through starts no line of its own.
   *
   * @param limit the longest a line may run either way from its seed, in plot widths
   */
  List<Line> fromGrid(int n, double limit, EvalEngine engine) {
    List<double[]> seeds = new ArrayList<>(n * n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        seeds.add(new double[] {(i + 0.5) / n, (j + 0.5) / n});
      }
    }
    return fromSeeds(seeds, 2 * n, limit, engine);
  }

  /**
   * The lines through points of the plot given in its own coordinates; every point which lies in
   * the plot and where the field has a direction gets a line.
   */
  List<Line> fromPoints(double[][] points, double limit, EvalEngine engine) {
    List<Line> result = new ArrayList<>(points.length);
    for (double[] p : points) {
      double u = (p[0] - x0) / (x1 - x0);
      double v = (p[1] - y0) / (y1 - y0);
      if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0) {
        continue;
      }
      List<double[]> one = new ArrayList<>(1);
      one.add(new double[] {u, v});
      // a line of its own: the cells of the others do not stop it
      result.addAll(fromSeeds(one, 1, limit, engine));
    }
    return result;
  }

  /**
   * The lines from the seeds, in order; a line stops where it enters a cell of the
   * <code>cells x cells</code> grid which another line already went through.
   */
  private List<Line> fromSeeds(List<double[]> seeds, int cells, double limit, EvalEngine engine) {
    int[][] owner = new int[cells][cells];
    List<Line> result = new ArrayList<>();
    int id = 0;
    for (double[] seed : seeds) {
      engine.checkTimeLimit();
      double u = seed[0];
      double v = seed[1];
      if (cells > 1 && owner[cell(u, cells)][cell(v, cells)] != 0) {
        continue;
      }
      double seedSpeed = interpolate(speed, u, v);
      if (Double.isNaN(seedSpeed) || seedSpeed == 0.0) {
        continue;
      }
      id++;
      List<double[]> backward = trace(u, v, -1.0, limit, owner, cells, id);
      List<double[]> forward = trace(u, v, 1.0, limit, owner, cells, id);
      if (region != null) {
        // the samples only say where a line may run; its own points are asked as well
        if (!inRegion(u, v)) {
          continue;
        }
        backward = insideRegion(backward);
        forward = insideRegion(forward);
      }
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
      result.add(new Line(line, seedSpeed));
    }
    return result;
  }

  /**
   * Follows the field's direction from <code>(u, v)</code>, forwards for <code>sign = 1</code> and
   * backwards for <code>sign = -1</code>, with midpoint steps; the seed itself is not part of the
   * result.
   */
  private List<double[]> trace(double u, double v, double sign, double limit, int[][] owner,
      int cells, int id) {
    List<double[]> path = new ArrayList<>();
    double travelled = 0.0;
    while (travelled < limit) {
      double[] d1 = direction(u, v, sign);
      if (d1 == null) {
        break;
      }
      double[] d2 = direction(u + d1[0] * STEP / 2, v + d1[1] * STEP / 2, sign);
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

  /** Whether the point <code>(u, v)</code> of the unit square lies in the region. */
  private boolean inRegion(double u, double v) {
    double[] vector = vectorAt(u, v);
    return region.accepts(x0 + (x1 - x0) * u, y0 + (y1 - y0) * v, vector[0], vector[1],
        Math.hypot(vector[0], vector[1]));
  }

  /** The points of a path, which leads away from its seed, up to the first one outside. */
  private List<double[]> insideRegion(List<double[]> path) {
    for (int i = 0; i < path.size(); i++) {
      double[] p = path.get(i);
      if (!inRegion(p[0], p[1])) {
        return new ArrayList<>(path.subList(0, i));
      }
    }
    return path;
  }

  /** The unit direction of the field at <code>(u, v)</code>, or <code>null</code>. */
  private double[] direction(double u, double v, double sign) {
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

  /**
   * A speed scaled over the speeds of the samples, from 0 for the slowest to 1 for the fastest; 0
   * for every speed of a field which is equally fast everywhere.
   */
  double scaledSpeed(double s) {
    return fastest > slowest ? (s - slowest) / (fastest - slowest) : 0.0;
  }

  /** The field's vector at <code>(u, v)</code>, in the plot's units. */
  double[] vectorAt(double u, double v) {
    return new double[] {interpolate(du, u, v) * (x1 - x0), interpolate(dv, u, v) * (y1 - y0)};
  }

  /** Bilinear interpolation in a grid of samples over 0..1 x 0..1. */
  private static double interpolate(double[][] grid, double u, double v) {
    double fi = Math.max(0.0, Math.min(1.0, u)) * (SAMPLES - 1);
    double fj = Math.max(0.0, Math.min(1.0, v)) * (SAMPLES - 1);
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

  /** The points of a line in plot coordinates, thinned to at most {@link #MAX_POINTS}. */
  IAST points(List<double[]> line) {
    int stride = Math.max(1, (line.size() + MAX_POINTS - 1) / MAX_POINTS);
    IASTAppendable result = F.ListAlloc(line.size() / stride + 2);
    for (int k = 0; k < line.size(); k += stride) {
      result.append(point(line.get(k)));
    }
    if ((line.size() - 1) % stride != 0) {
      result.append(point(line.get(line.size() - 1)));
    }
    return result;
  }

  /** A point in plot coordinates. */
  IAST point(double[] p) {
    return F.list(F.num(x0 + p[0] * (x1 - x0)), F.num(y0 + p[1] * (y1 - y0)));
  }
}
