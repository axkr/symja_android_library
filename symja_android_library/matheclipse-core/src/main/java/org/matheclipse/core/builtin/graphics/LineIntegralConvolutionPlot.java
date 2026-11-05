package org.matheclipse.core.builtin.graphics;

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
 * <code>LineIntegralConvolutionPlot[{vx, vy}, {x, xmin, xmax}, {y, ymin, ymax}]</code> - white
 * noise smeared along the streamlines of the field, as a <code>Graphics</code> of one RGB
 * <code>Raster</code>.
 *
 * <p>
 * Each pixel is the mean of the noise over a short streamline through it, half a pixel per step in
 * both directions. The texture is shaded on a brown to cream gradient by the norm of the field, or
 * by <code>ColorFunction</code>: a gradient name or a function of <code>x, y, vx, vy, norm</code>,
 * all of them scaled to <code>0..1</code> unless <code>ColorFunctionScaling -> False</code>.
 *
 * <p>
 * <code>RasterSize -> n</code> or <code>{nx, ny}</code> is the size of the noise raster, 240 by
 * default; <code>{{vx, vy}, {"noise", n, m}}</code> gives a square noise raster of
 * <code>n</code>. The field is sampled on a coarser grid and interpolated between its points.
 */
public class LineIntegralConvolutionPlot extends AbstractFunctionEvaluator {

  /** The side of the default noise raster. */
  private static final int DEFAULT_SIZE = 240;

  /** The largest side of a noise raster. */
  private static final int MAX_SIZE = 1000;

  /** The most intervals of the grid the field is sampled on, along each axis. */
  private static final int FIELD_GRID = 64;

  /** The most intervals of the grid the colour function is evaluated on, along each axis. */
  private static final int COLOR_GRID = 32;

  /** The colours of the weakest and of the strongest field, a brown and a cream. */
  private static final double[][] DEFAULT_GRADIENT = {{0.49, 0.30, 0.18}, {0.95, 0.87, 0.70}};

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 3 || !ast.arg2().isList3() || !ast.arg3().isList3()
        || !ast.arg2().first().isSymbol() || !ast.arg3().first().isSymbol()) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    if (!field.isList()) {
      // a field given by a symbol or a function call
      field = engine.evaluate(field);
    }
    int width = DEFAULT_SIZE;
    int height = DEFAULT_SIZE;
    boolean textureGiven = false;
    if (field.isList2() && field.first().isList2()) {
      // {{vx, vy}, {"noise", n, m}}: a square noise raster of the first size
      IExpr texture = field.second();
      if (texture.isList3() && texture.first().isString("noise")) {
        int n = size(engine.evaluate(texture.second()));
        if (n > 0) {
          width = n;
          height = n;
          textureGiven = true;
        }
      }
      field = field.first();
    }
    if (!field.isList2()) {
      return F.NIL;
    }
    ISymbol[] variables = {(ISymbol) ast.arg2().first(), (ISymbol) ast.arg3().first()};
    double x0;
    double x1;
    double y0;
    double y1;
    try {
      x0 = engine.evalDouble(((IAST) ast.arg2()).arg2());
      x1 = engine.evalDouble(((IAST) ast.arg2()).arg3());
      y0 = engine.evalDouble(((IAST) ast.arg3()).arg2());
      y1 = engine.evalDouble(((IAST) ast.arg3()).arg3());
    } catch (RuntimeException rex) {
      return F.NIL;
    }
    if (!(x1 > x0) || !(y1 > y0)) {
      return F.NIL;
    }

    IExpr colorFunction = S.Automatic;
    boolean scaling = true;
    IASTAppendable graphicsOptions = F.ListAlloc();
    for (int i = 4; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      if (key == S.RasterSize) {
        IExpr value = engine.evaluate(option.second());
        if (!textureGiven) {
          if (value.isList2()) {
            int nx = size(value.first());
            int ny = size(value.second());
            if (nx > 0 && ny > 0) {
              width = nx;
              height = ny;
            }
          } else if (size(value) > 0) {
            width = size(value);
            height = width;
          }
        }
      } else if (key == S.ColorFunction) {
        colorFunction = engine.evaluate(option.second());
      } else if (key == S.ColorFunctionScaling) {
        scaling = !engine.evaluate(option.second()).isFalse();
      } else if (key == S.VectorPoints || key == S.VectorScale || key == S.VectorStyle
          || key == S.StreamPoints || key == S.PerformanceGoal || key == S.RegionFunction) {
        // options of the arrows and streamlines drawn over the texture, which aren't drawn
      } else {
        graphicsOptions.append(option);
      }
    }

    // the field at the nodes of a coarse grid, rows from the bottom up
    int gx = Math.min(width, FIELD_GRID);
    int gy = Math.min(height, FIELD_GRID);
    double[][][] nodes = new double[gy + 1][gx + 1][];
    for (int r = 0; r <= gy; r++) {
      engine.checkTimeLimit();
      for (int c = 0; c <= gx; c++) {
        double[] point = {x0 + (x1 - x0) * c / gx, y0 + (y1 - y0) * r / gy};
        nodes[r][c] = VectorPlot.fieldAt(field, variables, point, engine);
      }
    }
    // the field at every pixel centre, interpolated between the nodes
    double px = (x1 - x0) / width;
    double py = (y1 - y0) / height;
    double[] u = new double[width * height];
    double[] v = new double[width * height];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        double[] value = interpolate(nodes, (c + 0.5) / width * gx, (r + 0.5) / height * gy);
        u[r * width + c] = value[0];
        v[r * width + c] = value[1];
      }
    }

    double[] texture = convolve(u, v, width, height, px, py, engine);
    double[][][] colors = colors(colorFunction, scaling, u, v, width, height, x0, x1, y0, y1,
        engine);
    if (colors == null) {
      return F.NIL;
    }

    // the averaged noise lies close to 1/2, so it is stretched to the full range
    double mean = 0.0;
    for (double t : texture) {
      mean += t;
    }
    mean /= texture.length;
    double variance = 0.0;
    for (double t : texture) {
      variance += (t - mean) * (t - mean);
    }
    double sd = Math.max(Math.sqrt(variance / texture.length), 1e-9);

    IASTAppendable data = F.ListAlloc(height);
    int cw = colors[0].length;
    int ch = colors.length;
    for (int r = 0; r < height; r++) {
      engine.checkTimeLimit();
      IASTAppendable row = F.ListAlloc(width);
      double[][] colorRow = colors[Math.min(r * ch / height, ch - 1)];
      for (int c = 0; c < width; c++) {
        double[] color = colorRow[Math.min(c * cw / width, cw - 1)];
        double shade =
            Math.max(0.0, Math.min(1.0, 0.5 + (texture[r * width + c] - mean) / (4 * sd)));
        double t = 0.35 + 0.65 * shade;
        row.append(F.list(F.num(color[0] * t), F.num(color[1] * t), F.num(color[2] * t)));
      }
      data.append(row);
    }
    IAST corners = F.list(F.list(F.num(x0), F.num(y0)), F.list(F.num(x1), F.num(y1)));
    IASTAppendable result = F.Graphics(F.list(F.binaryAST2(S.Raster, data, corners)));
    result.appendArgs(graphicsOptions);
    if (!VectorPlot.hasOption(graphicsOptions, S.Frame)) {
      result.append(F.Rule(S.Frame, S.True));
    }
    if (!VectorPlot.hasOption(graphicsOptions, S.AspectRatio)) {
      result.append(F.Rule(S.AspectRatio, F.C1));
    }
    if (!VectorPlot.hasOption(graphicsOptions, S.PlotRange)) {
      // the corners {{x0, y0}, {x1, y1}} as the ranges {{x0, x1}, {y0, y1}}
      result.append(F.Rule(S.PlotRange, engine.evaluate(F.Transpose(corners))));
    }
    return result;
  }

  /** A raster side of at least 2, capped at {@link #MAX_SIZE}, or 0 for no such number. */
  private static int size(IExpr value) {
    double n = value.evalfNaN();
    if (!(n >= 2.0)) {
      return 0;
    }
    return (int) Math.min(MAX_SIZE, Math.floor(n));
  }

  /**
   * The field at the grid position <code>(gx, gy)</code>, interpolated bilinearly between the
   * nodes; <code>NaN</code> where a node next to it has no value.
   */
  private static double[] interpolate(double[][][] nodes, double gx, double gy) {
    int rows = nodes.length - 1;
    int cols = nodes[0].length - 1;
    int c = Math.min((int) Math.floor(gx), cols - 1);
    int r = Math.min((int) Math.floor(gy), rows - 1);
    double fx = gx - c;
    double fy = gy - r;
    double[] a = nodes[r][c];
    double[] b = nodes[r][c + 1];
    double[] d = nodes[r + 1][c];
    double[] e = nodes[r + 1][c + 1];
    if (a == null || b == null || d == null || e == null) {
      return new double[] {Double.NaN, Double.NaN};
    }
    double[] result = new double[2];
    for (int k = 0; k < 2; k++) {
      result[k] = a[k] * (1 - fx) * (1 - fy) + b[k] * fx * (1 - fy) + d[k] * (1 - fx) * fy
          + e[k] * fx * fy;
    }
    return result;
  }

  /** A pseudo-random noise value in <code>0..1</code> for the pixel <code>i</code>. */
  private static double noise(long i) {
    long z = i + 0x9E3779B97F4A7C15L;
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    z ^= z >>> 31;
    return (z >>> 11) / (double) (1L << 53);
  }

  /**
   * The line integral convolution: each pixel the mean of the noise along the streamline through
   * it, followed half a pixel at a time forwards and backwards.
   */
  private static double[] convolve(double[] u, double[] v, int width, int height, double px,
      double py, EvalEngine engine) {
    double halfLength = Math.max(Math.max(width, height) / 16.0, 4.0);
    int steps = (int) Math.round(2 * halfLength);
    double[] texture = new double[width * height];
    for (int r = 0; r < height; r++) {
      engine.checkTimeLimit();
      for (int c = 0; c < width; c++) {
        int start = r * width + c;
        double sum = noise(start);
        int count = 1;
        for (int direction = 1; direction >= -1; direction -= 2) {
          double fx = c + 0.5;
          double fy = r + 0.5;
          int at = start;
          for (int s = 0; s < steps; s++) {
            // the direction in pixels, since the axes may be scaled differently
            double du = u[at] / px;
            double dv = v[at] / py;
            double length = Math.hypot(du, dv);
            if (!(length > 0.0) || Double.isInfinite(length)) {
              break;
            }
            fx += direction * 0.5 * du / length;
            fy += direction * 0.5 * dv / length;
            if (fx < 0.0 || fy < 0.0 || fx >= width || fy >= height) {
              break;
            }
            at = (int) fy * width + (int) fx;
            sum += noise(at);
            count++;
          }
        }
        texture[start] = sum / count;
      }
    }
    return texture;
  }

  /**
   * The colours on a grid of at most {@link #COLOR_GRID} cells along each axis, rows from the
   * bottom up, each a <code>{r, g, b}</code> triple.
   *
   * @return <code>null</code> if a colour function gives no colour
   */
  private static double[][][] colors(IExpr colorFunction, boolean scaling, double[] u,
      double[] v, int width, int height, double x0, double x1, double y0, double y1,
      EvalEngine engine) {
    double low = Double.MAX_VALUE;
    double high = -Double.MAX_VALUE;
    double uLow = Double.MAX_VALUE;
    double uHigh = -Double.MAX_VALUE;
    double vLow = Double.MAX_VALUE;
    double vHigh = -Double.MAX_VALUE;
    for (int i = 0; i < u.length; i++) {
      double norm = Math.hypot(u[i], v[i]);
      if (Double.isFinite(norm)) {
        low = Math.min(low, norm);
        high = Math.max(high, norm);
        uLow = Math.min(uLow, u[i]);
        uHigh = Math.max(uHigh, u[i]);
        vLow = Math.min(vLow, v[i]);
        vHigh = Math.max(vHigh, v[i]);
      }
    }
    int cw = Math.min(width, COLOR_GRID);
    int ch = Math.min(height, COLOR_GRID);
    double[][][] colors = new double[ch][cw][];
    for (int r = 0; r < ch; r++) {
      engine.checkTimeLimit();
      for (int c = 0; c < cw; c++) {
        int pr = Math.min((r * height + height / 2) / ch, height - 1);
        int pc = Math.min((c * width + width / 2) / cw, width - 1);
        int i = pr * width + pc;
        double norm = Math.hypot(u[i], v[i]);
        double t = Double.isFinite(norm) ? rescale(norm, low, high) : 0.0;
        if (colorFunction == S.Automatic) {
          colors[r][c] = gradient(t);
          continue;
        }
        IExpr color;
        if (colorFunction.isString()) {
          color = engine.evalQuiet(F.unaryAST1(F.unaryAST1(S.ColorData, colorFunction), F.num(t)));
        } else {
          double x = x0 + (pc + 0.5) * (x1 - x0) / width;
          double y = y0 + (pr + 0.5) * (y1 - y0) / height;
          IASTAppendable call = F.ast(colorFunction, 5);
          call.append(F.num(scaling ? rescale(x, x0, x1) : x));
          call.append(F.num(scaling ? rescale(y, y0, y1) : y));
          call.append(F.num(scaling ? rescale(u[i], uLow, uHigh) : u[i]));
          call.append(F.num(scaling ? rescale(v[i], vLow, vHigh) : v[i]));
          call.append(F.num(scaling ? t : norm));
          color = engine.evalQuiet(call);
        }
        colors[r][c] = rgb(color);
        if (colors[r][c] == null) {
          return null;
        }
      }
    }
    return colors;
  }

  private static double rescale(double value, double low, double high) {
    return high > low ? Math.max(0.0, Math.min(1.0, (value - low) / (high - low))) : 0.5;
  }

  /** The default colour of the field's strength <code>t</code>, 0 the weakest, 1 the strongest. */
  private static double[] gradient(double t) {
    double[] a = DEFAULT_GRADIENT[0];
    double[] b = DEFAULT_GRADIENT[1];
    return new double[] {a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1]),
        a[2] + t * (b[2] - a[2])};
  }

  /** The red, green and blue of a colour, or <code>null</code> if it is no colour. */
  private static double[] rgb(IExpr color) {
    org.matheclipse.core.convert.RGBColor awt =
        org.matheclipse.core.convert.Convert.toAWTColor(color);
    if (awt == null) {
      return null;
    }
    float[] components = awt.getRGBColorComponents(null);
    return new double[] {components[0], components[1], components[2]};
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
