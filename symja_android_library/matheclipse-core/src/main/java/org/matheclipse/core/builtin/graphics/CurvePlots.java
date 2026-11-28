package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Curves given by a construction rather than by a function, returned as primitives:
 *
 * <ul>
 * <li><code>KochCurve(n)</code>: the Koch curve of order <code>n</code> from <code>{0, 0}</code> to
 * <code>{1, 0}</code>, <code>KochCurve(n, {theta1, ...})</code> with the turning angles of another
 * generator;</li>
 * <li><code>HilbertCurve(n)</code>, <code>PeanoCurve(n)</code>: the space filling curves through
 * the cells of the <code>2^n x 2^n</code> and <code>3^n x 3^n</code> grids, as a <code>Line</code>
 * of integer points (<code>DataRange</code> maps them into a rectangle);</li>
 * <li><code>SierpinskiCurve(n)</code>: the closed Sierpinski square curve of order <code>n</code>;
 * </li>
 * <li><code>AnglePath3D({{alpha, beta, gamma}, ...})</code>: the path of unit steps, each after
 * turning the frame by the Euler angles (about z, then the new y, then the new x);</li>
 * <li><code>RulePlot(CellularAutomaton(rule))</code> and <code>RulePlot(SubstitutionSystem(rules))
 * </code>: a picture of the rules.</li>
 * </ul>
 */
public final class CurvePlots {

  private CurvePlots() {}

  /** The order of a curve: a positive machine integer up to <code>max</code>, else -1. */
  private static int order(IAST ast, int max, EvalEngine engine) {
    if (ast.argSize() < 1) {
      return -1;
    }
    int n = ast.arg1().isInteger() ? ast.arg1().toIntDefault(-1) : -1;
    if (n < 1) {
      // Positive machine-sized integer expected at position `2` in `1`.
      Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      return -1;
    }
    if (n > max) {
      // The order `1` is larger than the largest order `2` that is computed.
      Errors.printMessage(ast.topHead(), "maxorder", F.list(F.ZZ(n), F.ZZ(max)), engine);
      return -1;
    }
    return n;
  }

  /**
   * The <code>DataRange</code> of a curve call as <code>{{xmin, xmax}, {ymin, ymax}}</code>, or
   * {@code null} without a valid one.
   */
  private static double[][] dataRange(IAST ast) {
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first() == S.DataRange && option.second().isList2()
          && option.second().first().isList2() && option.second().second().isList2()) {
        IExpr r = option.second();
        double[][] range =
            new double[][] {{r.first().first().evalfNaN(), r.first().second().evalfNaN()},
                {r.second().first().evalfNaN(), r.second().second().evalfNaN()}};
        for (double[] axis : range) {
          if (!Double.isFinite(axis[0]) || !Double.isFinite(axis[1])) {
            return null;
          }
        }
        return range;
      }
    }
    return null;
  }

  /**
   * The <code>Line</code> through integer grid points, or through the points mapped into
   * <code>DataRange -&gt; {{xmin, xmax}, {ymin, ymax}}</code>.
   */
  private static IAST gridLine(IAST ast, long side, List<long[]> points) {
    double[][] range = dataRange(ast);
    IASTAppendable list = F.ListAlloc(points.size());
    double denominator = Math.max(1, side - 1);
    for (long[] p : points) {
      if (range == null) {
        list.append(F.List(F.ZZ(p[0]), F.ZZ(p[1])));
      } else {
        list.append(F.List(F.num(range[0][0] + p[0] * (range[0][1] - range[0][0]) / denominator),
            F.num(range[1][0] + p[1] * (range[1][1] - range[1][0]) / denominator)));
      }
    }
    return F.Line(list);
  }

  // ---------------------------------------------------------------- KochCurve

  /** The largest number of points of a Koch curve. */
  private static final double KOCH_MAX_POINTS = 1.0e6;

  /** The turning angles of the Koch generator: a peak of an equilateral triangle. */
  private static final double[] KOCH = {0.0, Math.PI / 3, -2 * Math.PI / 3, Math.PI / 3};

  /**
   * <code>KochCurve(n)</code> and <code>KochCurve(n, {theta1, theta2, ...})</code>: every segment
   * is replaced <code>n</code> times by the generator, the path of unit steps each after turning by
   * <code>theta_i</code>, scaled and turned to run from the segment's start to its end.
   */
  public static final class KochCurve extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.argSize() > 2) {
        return F.NIL;
      }
      int n = ast.arg1().isInteger() ? ast.arg1().toIntDefault(-1) : -1;
      if (n < 0) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.KochCurve, "intnm", F.list(ast, F.C1), engine);
      }
      double[] angles = KOCH;
      if (ast.argSize() == 2) {
        IExpr spec = engine.evalN(ast.arg2());
        if (!spec.isList() || spec.argSize() < 1) {
          return F.NIL;
        }
        angles = new double[spec.argSize()];
        for (int i = 0; i < angles.length; i++) {
          angles[i] = spec.getAt(i + 1).evalfNaN();
          if (!Double.isFinite(angles[i])) {
            return F.NIL;
          }
        }
      }
      // the number of points, (generator steps)^n + 1, stays below a million
      int maxOrder = angles.length == 1 ? 100
          : (int) Math.floor(Math.log(KOCH_MAX_POINTS) / Math.log(angles.length));
      if (n > maxOrder) {
        // The order `1` is larger than the largest order `2` that is computed.
        return Errors.printMessage(S.KochCurve, "maxorder", F.list(F.ZZ(n), F.ZZ(maxOrder)),
            engine);
      }
      // the generator from {0, 0}, then its end
      List<double[]> generator = new ArrayList<>();
      generator.add(new double[] {0, 0});
      double direction = 0;
      for (double angle : angles) {
        direction += angle;
        double[] last = generator.get(generator.size() - 1);
        generator.add(new double[] {last[0] + Math.cos(direction), last[1] + Math.sin(direction)});
      }
      double[] end = generator.get(generator.size() - 1);
      double span = end[0] * end[0] + end[1] * end[1];
      // a generator which ends (up to rounding) at its start has no direction to scale to
      if (!(span > 1.0e-20 * angles.length * angles.length)) {
        return F.NIL;
      }
      List<double[]> points = new ArrayList<>();
      points.add(new double[] {0, 0});
      points.add(new double[] {1, 0});
      for (int level = 0; level < n; level++) {
        List<double[]> next = new ArrayList<>(points.size() * angles.length);
        for (int i = 0; i + 1 < points.size(); i++) {
          double[] a = points.get(i);
          double[] b = points.get(i + 1);
          // the similarity taking {0, 0} to a and the generator's end to b
          double dx = b[0] - a[0];
          double dy = b[1] - a[1];
          double cr = (dx * end[0] + dy * end[1]) / span;
          double ci = (dy * end[0] - dx * end[1]) / span;
          for (int k = 0; k + 1 < generator.size(); k++) {
            double[] g = generator.get(k);
            next.add(new double[] {a[0] + cr * g[0] - ci * g[1], a[1] + ci * g[0] + cr * g[1]});
          }
        }
        next.add(points.get(points.size() - 1));
        points = next;
        engine.checkTimeLimit();
      }
      IASTAppendable list = F.ListAlloc(points.size());
      for (double[] p : points) {
        list.append(F.List(F.num(p[0]), F.num(p[1])));
      }
      return F.Line(list);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------- HilbertCurve

  /**
   * <code>HilbertCurve(n)</code>: the cells <code>{x, y}</code>, <code>0 &lt;= x, y &lt; 2^n</code>,
   * in the order of the Hilbert curve, which starts at <code>{0, 0}</code> and ends at
   * <code>{2^n - 1, 0}</code>.
   */
  public static final class HilbertCurve extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      int n = order(ast, 10, engine);
      if (n < 0) {
        return F.NIL;
      }
      long side = 1L << n;
      long count = side * side;
      List<long[]> points = new ArrayList<>((int) count);
      for (long d = 0; d < count; d++) {
        long x = 0;
        long y = 0;
        long t = d;
        for (long s = 1; s < side; s *= 2) {
          // the quadrant of this level, turned so the curve joins its neighbours
          long rx = 1 & (t / 2);
          long ry = 1 & (t ^ rx);
          if (ry == 0) {
            if (rx == 1) {
              x = s - 1 - x;
              y = s - 1 - y;
            }
            long swap = x;
            x = y;
            y = swap;
          }
          x += s * rx;
          y += s * ry;
          t /= 4;
        }
        points.add(new long[] {x, y});
        if ((d & 0xFFF) == 0) {
          engine.checkTimeLimit();
        }
      }
      return gridLine(ast, side, points);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------- PeanoCurve

  /**
   * <code>PeanoCurve(n)</code>: the cells of the <code>3^n x 3^n</code> grid in Peano's order. The
   * base 3 digits of the index give the digits of y and x by turns, y first; a digit
   * <code>d</code> becomes <code>2 - d</code> when the digits of the other coordinate before it add
   * up to an odd number, which turns the curve back at the end of every row.
   */
  public static final class PeanoCurve extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      int n = order(ast, 6, engine);
      if (n < 0) {
        return F.NIL;
      }
      int digitCount = 2 * n;
      long side = 1;
      for (int k = 0; k < n; k++) {
        side *= 3;
      }
      long count = side * side;
      List<long[]> points = new ArrayList<>((int) count);
      int[] digits = new int[digitCount];
      for (long index = 0; index < count; index++) {
        long t = index;
        for (int slot = digitCount - 1; slot >= 0; slot--) {
          digits[slot] = (int) (t % 3);
          t /= 3;
        }
        long x = 0;
        long y = 0;
        // the sums of the y digits and of the x digits so far
        int ySum = 0;
        int xSum = 0;
        for (int i = 0; i < digitCount; i++) {
          boolean yDigit = i % 2 == 0;
          int flip = yDigit ? xSum : ySum;
          int e = flip % 2 == 1 ? 2 - digits[i] : digits[i];
          if (yDigit) {
            y = y * 3 + e;
            ySum += digits[i];
          } else {
            x = x * 3 + e;
            xSum += digits[i];
          }
        }
        points.add(new long[] {x, y});
        if ((index & 0xFFF) == 0) {
          engine.checkTimeLimit();
        }
      }
      return gridLine(ast, side, points);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------- SierpinskiCurve

  /**
   * <code>SierpinskiCurve(n)</code>: the closed Sierpinski square curve, from Wirth's four mutually
   * recursive parts, with diagonal steps of <code>{+-32, +-32}</code> and straight steps of
   * <code>64</code>, starting and ending at <code>{0, 0}</code>.
   */
  public static final class SierpinskiCurve extends AbstractFunctionEvaluator {

    /** Half the length of a straight step. */
    private static final long H = 32;

    /** The points of one curve, walked by the four parts. */
    private static final class Walk {
      final List<long[]> points = new ArrayList<>();
      long x;
      long y;

      Walk() {
        points.add(new long[] {0, 0});
      }

      void step(long dx, long dy) {
        x += dx;
        y += dy;
        points.add(new long[] {x, y});
      }

      void a(int k) {
        if (k > 0) {
          a(k - 1);
          step(H, -H);
          b(k - 1);
          step(2 * H, 0);
          d(k - 1);
          step(H, H);
          a(k - 1);
        }
      }

      void b(int k) {
        if (k > 0) {
          b(k - 1);
          step(-H, -H);
          c(k - 1);
          step(0, -2 * H);
          a(k - 1);
          step(H, -H);
          b(k - 1);
        }
      }

      void c(int k) {
        if (k > 0) {
          c(k - 1);
          step(-H, H);
          d(k - 1);
          step(-2 * H, 0);
          b(k - 1);
          step(-H, -H);
          c(k - 1);
        }
      }

      void d(int k) {
        if (k > 0) {
          d(k - 1);
          step(H, H);
          a(k - 1);
          step(0, 2 * H);
          c(k - 1);
          step(-H, H);
          d(k - 1);
        }
      }
    }

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      int n = order(ast, 8, engine);
      if (n < 0) {
        return F.NIL;
      }
      Walk walk = new Walk();
      walk.a(n);
      walk.step(H, -H);
      walk.b(n);
      walk.step(-H, -H);
      walk.c(n);
      walk.step(-H, H);
      walk.d(n);
      walk.step(H, H);
      if (dataRange(ast) != null) {
        // DataRange maps the bounding square of the curve
        long minX = Long.MAX_VALUE;
        long minY = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        for (long[] p : walk.points) {
          minX = Math.min(minX, p[0]);
          minY = Math.min(minY, p[1]);
          maxX = Math.max(maxX, p[0]);
        }
        List<long[]> shifted = new ArrayList<>(walk.points.size());
        for (long[] p : walk.points) {
          shifted.add(new long[] {p[0] - minX, p[1] - minY});
        }
        return gridLine(ast, maxX - minX + 1, shifted);
      }
      IASTAppendable list = F.ListAlloc(walk.points.size());
      for (long[] p : walk.points) {
        list.append(F.List(F.ZZ(p[0]), F.ZZ(p[1])));
      }
      return F.Line(list);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------- AnglePath3D

  /**
   * <code>AnglePath3D({{alpha, beta, gamma}, ...})</code> and <code>AnglePath3D({{r, {alpha, beta,
   * gamma}}, ...})</code>: the points of a path from the origin, which takes each step of length 1
   * (or <code>r</code>) along the first axis of a frame, after turning the frame by the rotation
   * about z by alpha, about the new y by beta and about the new x by gamma. The frame starts as
   * the identity, so the first step without turning runs along x. Exact angles give exact points.
   */
  public static final class AnglePath3D extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() != 1 || !ast.arg1().isList()) {
        return F.NIL;
      }
      IAST steps = (IAST) ast.arg1();
      IExpr frame = F.IdentityMatrix(3);
      IExpr position = F.List(F.C0, F.C0, F.C0);
      IASTAppendable path = F.ListAlloc(steps.argSize() + 1);
      path.append(position);
      for (IExpr stepSpec : steps) {
        IExpr length = F.C1;
        IExpr angles = stepSpec;
        if (stepSpec.isList2() && stepSpec.second().isList()) {
          length = stepSpec.first();
          angles = stepSpec.second();
        }
        if (!angles.isList() || angles.argSize() != 3) {
          return F.NIL;
        }
        IAST rotation = F.Dot(rotation(3, angles.first()), rotation(2, angles.second()),
            rotation(1, angles.getAt(3)));
        frame = engine.evaluate(F.Dot(frame, rotation));
        IExpr forward = engine.evaluate(F.Part(frame, S.All, F.C1));
        position = engine.evaluate(F.Plus(position, F.Times(length, forward)));
        path.append(position);
        engine.checkTimeLimit();
      }
      return path;
    }

    /** The rotation by <code>angle</code> about axis 1 (x), 2 (y) or 3 (z). */
    private static IAST rotation(int axis, IExpr angle) {
      IExpr c = F.Cos(angle);
      IExpr s = F.Sin(angle);
      switch (axis) {
        case 1:
          return F.List(F.List(F.C1, F.C0, F.C0), F.List(F.C0, c, F.Negate(s)),
              F.List(F.C0, s, c));
        case 2:
          return F.List(F.List(c, F.C0, s), F.List(F.C0, F.C1, F.C0),
              F.List(F.Negate(s), F.C0, c));
        default:
          return F.List(F.List(c, F.Negate(s), F.C0), F.List(s, c, F.C0),
              F.List(F.C0, F.C0, F.C1));
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------- RulePlot

  /** The edges of the cells of a rule icon. */
  private static final IAST ICON_EDGE = F.EdgeForm(F.GrayLevel(F.num(0.618)));

  /** The height of an icon: the cells above, a gap of 1/4, the cells below, and margins. */
  private static final double ICON_HEIGHT = 3.25;

  /**
   * One rule icon in its own coordinates: the cells of the left side in <code>0 &lt;= y &lt;= 1
   * </code>, those of the right side in <code>-5/4 &lt;= y &lt;= -1/4</code>, and the horizontal
   * extent <code>left .. right</code> it takes; its vertical extent is <code>-7/4 .. 3/2</code>.
   */
  private static final class Icon {
    final List<double[]> cells = new ArrayList<>();
    final List<IExpr> colors = new ArrayList<>();
    double left;
    double right;

    Icon(double left, double right) {
      this.left = left;
      this.right = right;
    }

    void add(double x, double y, double width, IExpr color) {
      cells.add(new double[] {x, y, width});
      colors.add(color);
    }
  }

  /**
   * <code>RulePlot(CellularAutomaton(rule))</code>: one icon per neighbourhood, from the highest
   * to the lowest, the neighbourhood's cells above and the new cell below them; value 0 is white,
   * the highest value black. The rule is a number (two colours, range 1), <code>{n, k}</code>,
   * <code>{n, k, r}</code>, or a totalistic <code>{n, {k, 1}}</code> or <code>{n, {k, 1}, r}</code>,
   * whose icons show the sum of the neighbourhood as the gray of one bar.
   * <code>RulePlot(SubstitutionSystem(rules))</code> draws one icon per rule, the cells of the
   * left side above those of the right side; the characters of strings take grays in alphabetical
   * order. The icons stand in cells of a light gray grid, more than 16 of them in rows of 9.
   */
  public static final class RulePlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1) {
        return F.NIL;
      }
      IExpr system = ast.arg1();
      List<Icon> icons = null;
      if (system.isAST(S.CellularAutomaton, 2)) {
        icons = cellularAutomaton(system.first(), engine);
      } else if (system.isAST(S.SubstitutionSystem, 2)) {
        icons = substitutionSystem(system.first());
      }
      if (icons == null || icons.isEmpty()) {
        return F.NIL;
      }
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 1);
      graphics.append(layout(icons));
      for (int i = 2; i <= ast.argSize(); i++) {
        if (ast.get(i).isRuleAST()) {
          graphics.append(ast.get(i));
        }
      }
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * The icons in a grid: every icon is scaled to the height 1 of its grid cell, all cells as wide
   * as the widest icon; light gray lines separate the cells.
   */
  private static IAST layout(List<Icon> icons) {
    int n = icons.size();
    int perRow = n <= 16 ? n : 9;
    int rows = (n + perRow - 1) / perRow;
    double iconWidth = 0;
    for (Icon icon : icons) {
      iconWidth = Math.max(iconWidth, icon.right - icon.left);
    }
    double scale = 1.0 / ICON_HEIGHT;
    double cellWidth = iconWidth * scale;
    IASTAppendable drawn = F.ListAlloc(n + 1);
    for (int i = 0; i < n; i++) {
      Icon icon = icons.get(i);
      double x0 = (i % perRow) * cellWidth;
      double y0 = rows - 1 - i / perRow;
      // the icon's left edge and its bottom (-7/4) go to the corner of the cell
      double shift = (iconWidth - (icon.right - icon.left)) / 2;
      IASTAppendable group = F.ListAlloc(2 * icon.cells.size() + 1);
      group.append(ICON_EDGE);
      for (int k = 0; k < icon.cells.size(); k++) {
        double[] c = icon.cells.get(k);
        double x = x0 + (c[0] - icon.left + shift) * scale;
        double y = y0 + (c[1] + 1.75) * scale;
        group.append(F.List(icon.colors.get(k), F.Rectangle(F.List(F.num(x), F.num(y)),
            F.List(F.num(x + c[2] * scale), F.num(y + scale)))));
      }
      drawn.append(group);
    }
    IASTAppendable grid = F.ListAlloc(perRow + rows + 3);
    grid.append(F.GrayLevel(F.num(0.8)));
    double width = perRow * cellWidth;
    for (int k = 0; k <= perRow; k++) {
      grid.append(F.Line(F.List(F.List(F.num(k * cellWidth), F.C0),
          F.List(F.num(k * cellWidth), F.ZZ(rows)))));
    }
    for (int k = 0; k <= rows; k++) {
      grid.append(F.Line(F.List(F.List(F.C0, F.ZZ(k)), F.List(F.num(width), F.ZZ(k)))));
    }
    return F.List(drawn, grid);
  }

  /** The gray of a cell value: 0 white, the highest black. */
  private static IAST cellColor(int value, int colors) {
    return F.GrayLevel(F.num(1.0 - (double) value / Math.max(1, colors - 1)));
  }

  /** The icons of a cellular automaton rule, or {@code null} for a rule of another form. */
  private static List<Icon> cellularAutomaton(IExpr rule, EvalEngine engine) {
    java.math.BigInteger number;
    int colors = 2;
    int range = 1;
    boolean totalistic = false;
    if (rule.isInteger()) {
      number = ((org.matheclipse.core.interfaces.IInteger) rule).toBigNumerator();
    } else if (rule.isList() && rule.argSize() >= 2 && rule.argSize() <= 3
        && rule.first().isInteger()) {
      number = ((org.matheclipse.core.interfaces.IInteger) rule.first()).toBigNumerator();
      IExpr k = rule.second();
      if (k.isList2() && k.second().isOne()) {
        totalistic = true;
        colors = k.first().toIntDefault(-1);
      } else {
        colors = k.toIntDefault(-1);
      }
      if (rule.argSize() == 3) {
        range = rule.getAt(3).toIntDefault(-1);
      }
    } else {
      return null;
    }
    int width = 2 * range + 1;
    if (colors < 2 || colors > 10 || range < 1 || range > 3 || number.signum() < 0) {
      return null;
    }
    int maxSum = (colors - 1) * width;
    int cases = totalistic ? maxSum + 1 : (int) Math.round(Math.pow(colors, width));
    if (cases > 512) {
      return null;
    }
    java.math.BigInteger base = java.math.BigInteger.valueOf(colors);
    List<Icon> icons = new ArrayList<>(cases);
    double half = width / 2.0;
    for (int c = cases - 1; c >= 0; c--) {
      int result = number.divide(base.pow(c)).mod(base).intValue();
      Icon icon = new Icon(-half - 0.5, half + 0.5);
      if (totalistic) {
        // the sum of the neighbourhood as the gray of one bar
        icon.add(-half, 0, width, F.GrayLevel(F.num(1.0 - (double) c / maxSum)));
      } else {
        // the cells of the neighbourhood, the first one the most significant digit
        int rest = c;
        int[] neighbourhood = new int[width];
        for (int i = width - 1; i >= 0; i--) {
          neighbourhood[i] = rest % colors;
          rest /= colors;
        }
        for (int i = 0; i < width; i++) {
          icon.add(-half + i, 0, 1, cellColor(neighbourhood[i], colors));
        }
      }
      icon.add(-0.5, -1.25, 1, cellColor(result, colors));
      icons.add(icon);
      engine.checkTimeLimit();
    }
    return icons;
  }

  /**
   * The icons of the rules of a substitution system, or {@code null} unless every side is a string
   * or cell values (a value or a list of them).
   */
  private static List<Icon> substitutionSystem(IExpr rules) {
    IAST list = rules.isRuleAST() ? F.List(rules) : rules.makeList();
    if (list.argSize() == 0 || !list.forAll(r -> r.isRuleAST())) {
      return null;
    }
    java.util.TreeSet<Character> characters = new java.util.TreeSet<>();
    int colors = 2;
    int longest = 1;
    for (IExpr rule : list) {
      for (IExpr side : new IExpr[] {rule.first(), rule.second()}) {
        if (side.isString()) {
          String string = side.toString();
          if (string.isEmpty()) {
            return null;
          }
          for (char ch : string.toCharArray()) {
            characters.add(ch);
          }
          longest = Math.max(longest, string.length());
          continue;
        }
        IAST values = side.makeList();
        if (values.argSize() == 0) {
          return null;
        }
        for (IExpr value : values) {
          int v = value.toIntDefault(-1);
          if (v < 0 || v > 255) {
            return null;
          }
          colors = Math.max(colors, v + 1);
        }
        longest = Math.max(longest, values.argSize());
      }
    }
    List<Character> alphabet = new ArrayList<>(characters);
    List<Icon> icons = new ArrayList<>(list.argSize());
    for (IExpr rule : list) {
      // left aligned at x = -1/2
      Icon icon = new Icon(-1, longest);
      sideCells(icon, rule.first(), 0, colors, alphabet);
      sideCells(icon, rule.second(), -1.25, colors, alphabet);
      icons.add(icon);
    }
    return icons;
  }

  private static void sideCells(Icon icon, IExpr side, double y, int colors,
      List<Character> alphabet) {
    if (side.isString()) {
      String string = side.toString();
      for (int i = 0; i < string.length(); i++) {
        icon.add(-0.5 + i, y, 1,
            cellColor(alphabet.indexOf(string.charAt(i)), Math.max(2, alphabet.size())));
      }
      return;
    }
    IAST values = side.makeList();
    for (int i = 0; i < values.argSize(); i++) {
      icon.add(-0.5 + i, y, 1, cellColor(values.getAt(i + 1).toIntDefault(0), colors));
    }
  }
}
