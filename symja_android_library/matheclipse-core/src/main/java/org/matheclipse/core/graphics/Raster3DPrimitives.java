package org.matheclipse.core.graphics;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>Raster3D(array, {{xmin, ymin, zmin}, {xmax, ymax, zmax}}, {amin, amax}, options)</code>
 * rewritten into the <code>Cuboid</code> voxels which draw it.
 *
 * <p>
 * <code>array</code> holds layers (z, from the bottom), each a list of rows (y, from the front),
 * each a list of cells (x, from the left). A cell is a value, drawn in <code>GrayLevel</code> after
 * the range <code>{amin, amax}</code> (default <code>{0, 1}</code>) is scaled to <code>0 ... 1</code>,
 * or the channels <code>{r, g, b}</code> or <code>{r, g, b, a}</code>. <code>ColorFunction</code>
 * colours a value instead. Without a box the cells fill the unit cube.
 *
 * <p>
 * A voxel whose six neighbours are all opaque can't be seen and is left out. A larger array is
 * averaged over blocks of cells until it fits {@link #MAX_VOXELS}.
 */
public final class Raster3DPrimitives {

  private Raster3DPrimitives() {}

  /** The most voxels one <code>Raster3D</code> is drawn with. */
  static final int MAX_VOXELS = 8000;

  /**
   * The voxels of a <code>Raster3D</code>.
   *
   * @return <code>EdgeForm(None)</code> and a list of <code>{color, Opacity(a), Cuboid(min, max)}</code>
   *         groups, or
   *         {@link F#NIL} when the array is no 3D array of cells
   */
  public static @AllowNIL IExpr voxels(IAST raster, EvalEngine engine) {
    if (raster.argSize() < 1 || !raster.arg1().isList()) {
      return F.NIL;
    }
    IAST layers = (IAST) raster.arg1();
    int nz = layers.argSize();
    if (nz == 0 || !layers.arg1().isList()) {
      return F.NIL;
    }
    int ny = layers.arg1().argSize();
    if (ny == 0 || !layers.arg1().first().isList()) {
      return F.NIL;
    }
    int nx = layers.arg1().first().argSize();
    if (nx == 0) {
      return F.NIL;
    }
    // without a box the array fills the unit cube
    double[] box = {0, 0, 0, 1, 1, 1};
    double aMin = 0.0;
    double aMax = 1.0;
    IExpr colorFunction = F.NIL;
    for (int i = 2; i <= raster.argSize(); i++) {
      IExpr arg = raster.get(i);
      if (arg.isRuleAST()) {
        if (arg.first() == S.ColorFunction && !arg.second().isAutomatic()) {
          colorFunction = arg.second();
        }
        continue;
      }
      if (i == 2 && arg.isList2() && arg.first().isList() && arg.second().isList()) {
        double[] lo = vector3(arg.first());
        double[] hi = vector3(arg.second());
        if (lo != null && hi != null) {
          box = new double[] {lo[0], lo[1], lo[2], hi[0], hi[1], hi[2]};
        }
      } else if (i == 3 && arg.isList2()) {
        double a = arg.first().evalfNaN();
        double b = arg.second().evalfNaN();
        if (Double.isFinite(a) && Double.isFinite(b) && a != b) {
          aMin = a;
          aMax = b;
        }
      }
    }
    // the colour of every cell as {r, g, b, a}; a colour function is worked out once per level
    java.util.Map<Long, double[]> levels = new java.util.HashMap<>();
    double[][][][] cells = new double[nz][ny][nx][];
    for (int z = 0; z < nz; z++) {
      IExpr layer = layers.get(z + 1);
      if (!layer.isList() || layer.argSize() != ny) {
        return F.NIL;
      }
      for (int y = 0; y < ny; y++) {
        IExpr row = layer.getAt(y + 1);
        if (!row.isList() || row.argSize() != nx) {
          return F.NIL;
        }
        for (int x = 0; x < nx; x++) {
          double[] rgba = cellColor(row.getAt(x + 1), aMin, aMax, colorFunction, levels, engine);
          if (rgba == null) {
            return F.NIL;
          }
          cells[z][y][x] = rgba;
        }
      }
      engine.checkTimeLimit();
    }
    int block = 1;
    while ((long) ceil(nx, block) * ceil(ny, block) * ceil(nz, block) > MAX_VOXELS) {
      block++;
    }
    if (block > 1) {
      cells = averaged(cells, block);
    }
    int mz = cells.length;
    int my = cells[0].length;
    int mx = cells[0][0].length;
    double sx = (box[3] - box[0]) / mx;
    double sy = (box[4] - box[1]) / my;
    double sz = (box[5] - box[2]) / mz;
    IASTAppendable out = F.ListAlloc();
    // a cell is a block of colour; outlining every cube would cover the volume in a grid
    out.append(F.EdgeForm(S.None));
    for (int z = 0; z < mz; z++) {
      for (int y = 0; y < my; y++) {
        for (int x = 0; x < mx; x++) {
          double[] c = cells[z][y][x];
          // a cell below 2% opacity can't be seen; leaving it out keeps the volume light
          if (c[3] < 0.02 || hidden(cells, x, y, z)) {
            continue;
          }
          IASTAppendable voxel = F.ListAlloc(3);
          voxel.append(F.RGBColor(F.num(c[0]), F.num(c[1]), F.num(c[2])));
          if (c[3] < 1.0) {
            voxel.append(F.Opacity(F.num(c[3])));
          }
          voxel.append(F.Cuboid(
              F.List(F.num(box[0] + x * sx), F.num(box[1] + y * sy), F.num(box[2] + z * sz)),
              F.List(F.num(box[0] + (x + 1) * sx), F.num(box[1] + (y + 1) * sy),
                  F.num(box[2] + (z + 1) * sz))));
          out.append(voxel);
        }
      }
    }
    return out;
  }

  private static int ceil(int n, int block) {
    return (n + block - 1) / block;
  }

  /** Whether a voxel is surrounded by opaque voxels on all six sides. */
  private static boolean hidden(double[][][][] cells, int x, int y, int z) {
    int mz = cells.length;
    int my = cells[0].length;
    int mx = cells[0][0].length;
    if (x == 0 || y == 0 || z == 0 || x == mx - 1 || y == my - 1 || z == mz - 1) {
      return false;
    }
    return cells[z][y][x - 1][3] >= 1.0 && cells[z][y][x + 1][3] >= 1.0
        && cells[z][y - 1][x][3] >= 1.0 && cells[z][y + 1][x][3] >= 1.0
        && cells[z - 1][y][x][3] >= 1.0 && cells[z + 1][y][x][3] >= 1.0;
  }

  /** The cells averaged over blocks of <code>block^3</code> cells. */
  private static double[][][][] averaged(double[][][][] cells, int block) {
    int nz = cells.length;
    int ny = cells[0].length;
    int nx = cells[0][0].length;
    int mz = ceil(nz, block);
    int my = ceil(ny, block);
    int mx = ceil(nx, block);
    double[][][][] out = new double[mz][my][mx][];
    for (int z = 0; z < mz; z++) {
      for (int y = 0; y < my; y++) {
        for (int x = 0; x < mx; x++) {
          double[] sum = new double[4];
          int count = 0;
          for (int k = z * block; k < Math.min(nz, (z + 1) * block); k++) {
            for (int j = y * block; j < Math.min(ny, (y + 1) * block); j++) {
              for (int i = x * block; i < Math.min(nx, (x + 1) * block); i++) {
                for (int c = 0; c < 4; c++) {
                  sum[c] += cells[k][j][i][c];
                }
                count++;
              }
            }
          }
          for (int c = 0; c < 4; c++) {
            sum[c] /= count;
          }
          out[z][y][x] = sum;
        }
      }
    }
    return out;
  }

  /** The colour <code>{r, g, b, a}</code> of a cell, or {@code null} when it is none. */
  private static double[] cellColor(IExpr cell, double aMin, double aMax, IExpr colorFunction,
      java.util.Map<Long, double[]> levels, EvalEngine engine) {
    if (cell.isList()) {
      int n = cell.argSize();
      if (n != 3 && n != 4) {
        return null;
      }
      double[] rgba = {0, 0, 0, 1};
      for (int i = 0; i < n; i++) {
        double v = cell.getAt(i + 1).evalfNaN();
        if (!Double.isFinite(v)) {
          return null;
        }
        rgba[i] = clamp(v);
      }
      return rgba;
    }
    double v = cell.evalfNaN();
    if (!Double.isFinite(v)) {
      return null;
    }
    double t = clamp((v - aMin) / (aMax - aMin));
    if (colorFunction.isPresent()) {
      long level = Math.round(t * 2048);
      double[] known = levels.get(level);
      if (known != null) {
        return known;
      }
      double[] rgba = functionColor(colorFunction, level / 2048.0, engine);
      if (rgba != null) {
        levels.put(level, rgba);
        return rgba;
      }
    }
    return new double[] {t, t, t, 1.0};
  }

  /** The colour <code>f(t)</code> as <code>{r, g, b, a}</code>, or {@code null}. */
  private static double[] functionColor(IExpr colorFunction, double t, EvalEngine engine) {
    try {
      java.awt.Color c = ColorUtil.parse(engine.evaluate(F.unaryAST1(colorFunction, F.num(t))));
      if (c != null) {
        return new double[] {c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0,
            c.getAlpha() / 255.0};
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return null;
  }

  private static double[] vector3(IExpr expr) {
    if (!expr.isList() || expr.argSize() != 3) {
      return null;
    }
    double[] v = new double[3];
    for (int i = 0; i < 3; i++) {
      v[i] = expr.getAt(i + 1).evalfNaN();
      if (!Double.isFinite(v[i])) {
        return null;
      }
    }
    return v;
  }

  private static double clamp(double v) {
    return Math.max(0.0, Math.min(1.0, v));
  }
}
