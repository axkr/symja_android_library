package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * FindCurvePath({p1, p2, ...})
 * </pre>
 *
 * <blockquote>
 * <p>
 * orders the points along one curve and returns <code>{{i1, i2, ...}}</code>, the list of their
 * indices in that order.
 * </p>
 * </blockquote>
 *
 * <p>
 * The curve is open when the shortest path through the points has ends which lie much further
 * apart (more than 2.5 times the median step) than its steps; it is written from its lower-numbered
 * end. Otherwise the curve is the shortest closed tour through the points, written from the
 * predecessor of point 1 round to it again, in the direction in which the second index is the
 * larger neighbor of point 1.
 *
 * <p>
 * Up to {@link #EXACT_MAX} points the tours are optimal (dynamic programming over subsets); larger
 * inputs get a nearest neighbor tour improved by 2-opt and Or-opt moves.
 */
public class FindCurvePath extends AbstractFunctionEvaluator {

  /** The largest number of points whose tour is computed exactly. */
  private static final int EXACT_MAX = 12;

  /** A step longer than this multiple of the median step opens the curve. */
  private static final double OPEN_RATIO = 2.5;

  private static final double EPSILON = 1.0e-10;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr points = ast.arg1();
    if (!points.isList() || points.argSize() == 0 || !points.first().isList()) {
      return F.NIL;
    }
    double[][] coordinates = engine.evalN(points).toDoubleMatrix(false);
    if (coordinates == null || coordinates[0].length == 0) {
      return F.NIL;
    }
    final int n = coordinates.length;
    if (n <= 2) {
      return indices(n == 1 ? new int[] {0} : new int[] {0, 1});
    }
    double[][] dist = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double sum = 0.0;
        for (int k = 0; k < coordinates[i].length; k++) {
          double d = coordinates[i][k] - coordinates[j][k];
          sum += d * d;
        }
        dist[i][j] = Math.sqrt(sum);
      }
    }
    int[] path = openPath(dist, engine);
    // the steps of the path and the step which would close it
    double[] steps = new double[n];
    for (int k = 0; k < n; k++) {
      steps[k] = dist[path[k]][path[(k + 1) % n]];
    }
    double closing = steps[n - 1];
    Arrays.sort(steps);
    if (closing > OPEN_RATIO * steps[n / 2]) {
      if (path[0] > path[n - 1]) {
        reverse(path, 0, n - 1);
      }
      return indices(path);
    }
    // closed: from the predecessor of point 1 round to it again
    int[] tour = closedTour(dist, engine);
    int[] cycle = new int[n + 1];
    cycle[0] = tour[n - 1];
    System.arraycopy(tour, 0, cycle, 1, n);
    return indices(cycle);
  }

  private static IAST indices(int[] order) {
    return F.list(F.mapRange(0, order.length, i -> F.ZZ(order[i] + 1)));
  }

  /**
   * A shortest closed tour through all points, starting at point 0, in the direction in which its
   * second point has the larger index of the two neighbors of point 0.
   */
  private static int[] closedTour(double[][] dist, EvalEngine engine) {
    final int n = dist.length;
    int[] tour;
    if (n <= EXACT_MAX) {
      tour = heldKarp(dist, 0, -1, engine);
    } else {
      tour = nearestNeighborTour(dist, 0);
      improveTour(dist, tour, engine);
      tour = rotateTo(tour, 0);
    }
    if (tour[1] < tour[n - 1]) {
      reverse(tour, 1, n - 1);
    }
    return tour;
  }

  /**
   * A shortest path through all points with free ends: a shortest closed tour through the points
   * and an extra point at distance 0 from all of them, cut at the extra point.
   */
  private static int[] openPath(double[][] dist, EvalEngine engine) {
    final int n = dist.length;
    double[][] extended = new double[n + 1][n + 1];
    for (int i = 0; i < n; i++) {
      System.arraycopy(dist[i], 0, extended[i], 0, n);
    }
    int[] tour;
    if (n + 1 <= EXACT_MAX) {
      tour = heldKarp(extended, n, -1, engine);
    } else {
      tour = nearestNeighborTour(extended, n);
      improveTour(extended, tour, engine);
      tour = rotateTo(tour, n);
    }
    return Arrays.copyOfRange(tour, 1, n + 1);
  }

  /**
   * The optimal order by dynamic programming over the subsets of the points: a path from
   * <code>start</code> to <code>end</code>, or a closed tour from <code>start</code> (without the
   * repeated start) if <code>end &lt; 0</code>.
   */
  private static int[] heldKarp(double[][] dist, int start, int end, EvalEngine engine) {
    final int n = dist.length;
    final int full = 1 << n;
    double[] cost = new double[full * n];
    int[] parent = new int[full * n];
    Arrays.fill(cost, Double.POSITIVE_INFINITY);
    Arrays.fill(parent, -1);
    cost[(1 << start) * n + start] = 0.0;
    for (int mask = 0; mask < full; mask++) {
      if ((mask & (1 << start)) == 0) {
        continue;
      }
      engine.checkTimeLimit();
      for (int last = 0; last < n; last++) {
        double c = cost[mask * n + last];
        if (Double.isInfinite(c) || (mask & (1 << last)) == 0) {
          continue;
        }
        for (int next = 0; next < n; next++) {
          if ((mask & (1 << next)) != 0) {
            continue;
          }
          int nextMask = mask | (1 << next);
          // the fixed end is entered last
          if (next == end && nextMask != full - 1) {
            continue;
          }
          double nextCost = c + dist[last][next];
          if (nextCost < cost[nextMask * n + next] - 1.0e-12) {
            cost[nextMask * n + next] = nextCost;
            parent[nextMask * n + next] = last;
          }
        }
      }
    }
    final int all = full - 1;
    int last = end;
    if (end < 0) {
      double best = Double.POSITIVE_INFINITY;
      for (int l = 0; l < n; l++) {
        if (l != start) {
          double c = cost[all * n + l] + dist[l][start];
          if (c < best) {
            best = c;
            last = l;
          }
        }
      }
    }
    int[] order = new int[n];
    int mask = all;
    for (int k = n - 1; k >= 0 && last >= 0; k--) {
      order[k] = last;
      int p = parent[mask * n + last];
      mask &= ~(1 << last);
      last = p;
    }
    return order;
  }

  private static int[] nearestNeighborTour(double[][] dist, int start) {
    final int n = dist.length;
    boolean[] visited = new boolean[n];
    int[] tour = new int[n];
    int current = start;
    visited[current] = true;
    tour[0] = current;
    for (int k = 1; k < n; k++) {
      int best = -1;
      for (int j = 0; j < n; j++) {
        if (!visited[j] && (best < 0 || dist[current][j] < dist[current][best])) {
          best = j;
        }
      }
      visited[best] = true;
      tour[k] = best;
      current = best;
    }
    return tour;
  }

  /** 2-opt and Or-opt moves, repeated until no move shortens the closed tour. */
  private static void improveTour(double[][] dist, int[] tour, EvalEngine engine) {
    final int n = tour.length;
    boolean improved = true;
    for (int round = 0; improved && round < 1000; round++) {
      engine.checkTimeLimit();
      improved = false;
      // 2-opt: replace the edges (a, a+1) and (c, c+1) by (a, c) and (a+1, c+1)
      for (int i = 0; i < n - 1; i++) {
        for (int j = i + 2; j < n; j++) {
          int a = tour[i];
          int aNext = tour[i + 1];
          int c = tour[j];
          int cNext = tour[(j + 1) % n];
          if (cNext == a) {
            continue;
          }
          double delta = dist[a][c] + dist[aNext][cNext] - dist[a][aNext] - dist[c][cNext];
          if (delta < -EPSILON) {
            reverse(tour, i + 1, j);
            improved = true;
          }
        }
      }
      // Or-opt: move a run of 1 to 3 points between two neighbors elsewhere in the tour
      for (int length = 1; length <= 3 && length + 2 < n; length++) {
        for (int i = 0; i < n; i++) {
          if (orMove(dist, tour, i, length)) {
            improved = true;
          }
        }
      }
    }
  }

  /**
   * Move the run of <code>length</code> points starting at the position <code>i</code> to the
   * place, forward or reversed, where it shortens the closed tour most.
   *
   * @return <code>true</code> if the tour was changed
   */
  private static boolean orMove(double[][] dist, int[] tour, int i, int length) {
    final int n = tour.length;
    int first = tour[i];
    int last = tour[(i + length - 1) % n];
    int prev = tour[(i + n - 1) % n];
    int next = tour[(i + length) % n];
    double removed = dist[prev][first] + dist[last][next] - dist[prev][next];
    // the tour without the run, starting after it
    int[] rest = new int[n - length];
    for (int k = 0; k < rest.length; k++) {
      rest[k] = tour[(i + length + k) % n];
    }
    double bestGain = EPSILON;
    int bestAt = -1;
    boolean bestReversed = false;
    for (int k = 0; k < rest.length - 1; k++) {
      int c = rest[k];
      int cNext = rest[k + 1];
      double forward = dist[c][first] + dist[last][cNext] - dist[c][cNext];
      double backward = dist[c][last] + dist[first][cNext] - dist[c][cNext];
      double gain = removed - Math.min(forward, backward);
      if (gain > bestGain) {
        bestGain = gain;
        bestAt = k;
        bestReversed = backward < forward;
      }
    }
    if (bestAt < 0) {
      return false;
    }
    int[] run = new int[length];
    for (int k = 0; k < length; k++) {
      run[k] = tour[(i + k) % n];
    }
    int pos = 0;
    for (int k = 0; k <= bestAt; k++) {
      tour[pos++] = rest[k];
    }
    for (int k = 0; k < length; k++) {
      tour[pos++] = run[bestReversed ? length - 1 - k : k];
    }
    for (int k = bestAt + 1; k < rest.length; k++) {
      tour[pos++] = rest[k];
    }
    return true;
  }

  private static int[] rotateTo(int[] tour, int start) {
    final int n = tour.length;
    int at = 0;
    while (tour[at] != start) {
      at++;
    }
    int[] rotated = new int[n];
    for (int k = 0; k < n; k++) {
      rotated[k] = tour[(at + k) % n];
    }
    return rotated;
  }

  private static void reverse(int[] array, int from, int to) {
    for (int i = from, j = to; i < j; i++, j--) {
      int t = array[i];
      array[i] = array[j];
      array[j] = t;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
