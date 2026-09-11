package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ListCurvePathPlot({{x1, y1}, {x2, y2}, ...})</code> - the curves a set of points in no
 * particular order lies on, each drawn as a line through its points.
 *
 * <p>
 * The curves are rebuilt from the points: neighbours are joined, shortest joins first, as long as
 * no point gets more than two of them, no loop closes, no join is much longer than the typical gap
 * between nearest neighbours, and a curve does not turn by more than 60 degrees at a point. Points
 * sampled from a curve, in any order, come back as that curve; a cloud of random points comes back
 * as many short curves with the loose points left out, which is what Mathematica draws for it
 * (<code>ListCurvePathPlot[RandomReal[{0, 10}, {2000, 2}]]</code>: some 540 lines of 2 to 20 points,
 * all in one style). The lines are drawn by <code>ListLinePlot</code>, in its first colour.
 */
public class ListCurvePathPlot extends AbstractFunctionEvaluator {

  /** How much longer than the typical gap to a nearest neighbour one step along a curve may be. */
  private static final double GAP_FACTOR = 2.5;

  /** The cosine of the sharpest turn a curve may take at a point: 60 degrees. */
  private static final double MIN_TURN_COSINE = 0.5;

  /** How many nearest neighbours of each point are candidates for joining it. */
  private static final int NEIGHBOURS = 6;

  public ListCurvePathPlot() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr data = engine.evaluate(ast.arg1());
    if (!data.isList() || !((IAST) data).isListOfPoints(2)) {
      return F.NIL;
    }
    IAST list = (IAST) data;
    int n = list.argSize();
    double[][] points = new double[n][2];
    for (int i = 0; i < n; i++) {
      IAST point = (IAST) list.get(i + 1);
      points[i][0] = point.arg1().evalfNaN();
      points[i][1] = point.arg2().evalfNaN();
      if (!Double.isFinite(points[i][0]) || !Double.isFinite(points[i][1])) {
        return F.NIL;
      }
    }
    List<int[]> chains = curvePaths(points);
    if (chains.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable curves = F.ListAlloc(chains.size());
    for (int[] chain : chains) {
      IASTAppendable line = F.ListAlloc(chain.length);
      for (int index : chain) {
        line.append(list.get(index + 1));
      }
      curves.append(line);
    }
    IASTAppendable plot = F.ast(S.ListLinePlot, ast.argSize() + 2);
    plot.append(curves);
    boolean styled = false;
    boolean shaped = false;
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      styled |= arg.isRuleAST() && arg.first() == S.PlotStyle;
      shaped |= arg.isRuleAST() && arg.first() == S.AspectRatio;
      plot.append(arg);
    }
    if (!styled) {
      // one curve, however many pieces it was rebuilt in
      plot.append(F.Rule(S.PlotStyle, GraphicsOptions.plotStyleColorExpr(0, F.NIL)));
    }
    if (!shaped) {
      // the points keep their true shape, as in Mathematica: a square of points is drawn square
      plot.append(F.Rule(S.AspectRatio, S.Automatic));
    }
    return engine.evaluate(plot);
  }

  /**
   * The chains of point indices the points are joined into; every chain has at least two points,
   * and a point that joins nothing is in none.
   */
  static List<int[]> curvePaths(double[][] points) {
    int n = points.length;
    List<int[]> chains = new ArrayList<>();
    if (n < 2) {
      return chains;
    }
    int k = Math.min(NEIGHBOURS, n - 1);
    int[][] near = new int[n][k];
    double[][] nearDistance = new double[n][k];
    double[] nearest = new double[n];
    for (int i = 0; i < n; i++) {
      Arrays.fill(nearDistance[i], Double.MAX_VALUE);
      Arrays.fill(near[i], -1);
      for (int j = 0; j < n; j++) {
        if (j == i) {
          continue;
        }
        double d = distance(points, i, j);
        if (d < nearDistance[i][k - 1]) {
          int slot = k - 1;
          while (slot > 0 && nearDistance[i][slot - 1] > d) {
            nearDistance[i][slot] = nearDistance[i][slot - 1];
            near[i][slot] = near[i][slot - 1];
            slot--;
          }
          nearDistance[i][slot] = d;
          near[i][slot] = j;
        }
      }
      nearest[i] = nearDistance[i][0];
    }
    double[] sorted = nearest.clone();
    Arrays.sort(sorted);
    double longest = GAP_FACTOR * sorted[n / 2];

    // every neighbour pair close enough to join, each once, shortest first
    Set<Long> known = new HashSet<>();
    List<double[]> edges = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      for (int s = 0; s < k; s++) {
        int j = near[i][s];
        if (j < 0 || nearDistance[i][s] > longest) {
          continue;
        }
        long key = (long) Math.min(i, j) * n + Math.max(i, j);
        if (known.add(key)) {
          edges.add(new double[] {nearDistance[i][s], Math.min(i, j), Math.max(i, j)});
        }
      }
    }
    edges.sort((a, b) -> Double.compare(a[0], b[0]));

    int[] degree = new int[n];
    int[][] joined = new int[n][2];
    int[] parent = new int[n];
    for (int i = 0; i < n; i++) {
      parent[i] = i;
    }
    for (double[] edge : edges) {
      int a = (int) edge[1];
      int b = (int) edge[2];
      if (degree[a] == 2 || degree[b] == 2) {
        continue;
      }
      int rootA = root(parent, a);
      int rootB = root(parent, b);
      if (rootA == rootB) {
        continue; // it would close a loop
      }
      if (!continuesSmoothly(points, joined, degree, a, b)
          || !continuesSmoothly(points, joined, degree, b, a)) {
        continue;
      }
      joined[a][degree[a]++] = b;
      joined[b][degree[b]++] = a;
      parent[rootA] = rootB;
    }

    boolean[] seen = new boolean[n];
    for (int start = 0; start < n; start++) {
      if (degree[start] != 1 || seen[start]) {
        continue;
      }
      List<Integer> chain = new ArrayList<>();
      int previous = -1;
      int current = start;
      while (current >= 0) {
        chain.add(current);
        seen[current] = true;
        int next = -1;
        for (int d = 0; d < degree[current]; d++) {
          if (joined[current][d] != previous) {
            next = joined[current][d];
            break;
          }
        }
        previous = current;
        current = next;
      }
      int[] indices = new int[chain.size()];
      for (int i = 0; i < indices.length; i++) {
        indices[i] = chain.get(i);
      }
      chains.add(indices);
    }
    return chains;
  }

  /** Whether the curve arriving at <code>a</code> may go on to <code>b</code>. */
  private static boolean continuesSmoothly(double[][] points, int[][] joined, int[] degree, int a,
      int b) {
    if (degree[a] == 0) {
      return true;
    }
    int from = joined[a][0];
    double ux = points[a][0] - points[from][0];
    double uy = points[a][1] - points[from][1];
    double vx = points[b][0] - points[a][0];
    double vy = points[b][1] - points[a][1];
    double lengths = Math.hypot(ux, uy) * Math.hypot(vx, vy);
    if (lengths == 0.0) {
      return true;
    }
    return (ux * vx + uy * vy) / lengths >= MIN_TURN_COSINE;
  }

  private static int root(int[] parent, int i) {
    while (parent[i] != i) {
      parent[i] = parent[parent[i]];
      i = parent[i];
    }
    return i;
  }

  private static double distance(double[][] points, int i, int j) {
    return Math.hypot(points[i][0] - points[j][0], points[i][1] - points[j][1]);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
