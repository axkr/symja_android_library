package org.matheclipse.graphtheory.eval;

import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Shortest closed tours and shortest Hamiltonian paths over a symmetric matrix of distances, as
 * <code>FindShortestTour</code> needs them.
 *
 * <p>
 * Up to {@link #EXACT_LIMIT} points the Held-Karp dynamic program finds an optimal tour. Above that
 * a deterministic local search is used: a nearest neighbour tour from several start points, each
 * improved by 2-opt and Or-opt moves until neither finds a shorter tour, and the best of them is
 * kept. The same input therefore always gives the same tour.
 *
 * <p>
 * A path from <code>s</code> to <code>t</code> is found as the shortest closed tour in which the
 * edge <code>s - t</code> is made so cheap that every tour takes it; dropping that edge leaves the
 * path.
 */
public final class TravelingSalesman {

  /** The largest number of points that is solved exactly. */
  public static final int EXACT_LIMIT = 12;

  /** Up to this many points the local search is started from every point. */
  private static final int ALL_STARTS_LIMIT = 100;

  /** The local search is started from at least this many points. */
  private static final int MIN_STARTS = 4;

  private final double[][] dist;
  private final int n;
  /** Improvements smaller than this are rounding noise, not a shorter tour. */
  private final double eps;
  private final EvalEngine engine;

  private TravelingSalesman(double[][] dist, EvalEngine engine) {
    this.dist = dist;
    this.n = dist.length;
    this.engine = engine;
    double max = 0.0;
    for (double[] row : dist) {
      for (double d : row) {
        if (Double.isFinite(d)) {
          max = Math.max(max, Math.abs(d));
        }
      }
    }
    this.eps = 1.0e-12 * Math.max(1.0, max);
  }

  /**
   * A shortest closed tour.
   *
   * @param dist the symmetric distances between the points
   * @param start the point the tour starts at (0-based)
   * @param engine checked for time limits in the local search
   * @return the points in tour order, starting with <code>start</code>, each once
   */
  public static int[] tour(double[][] dist, int start, EvalEngine engine) {
    return rotate(new TravelingSalesman(dist, engine).solve(), start);
  }

  /**
   * A shortest Hamiltonian path from <code>s</code> to <code>t</code>, <code>s != t</code>.
   *
   * @param dist the symmetric distances between the points
   * @param s the first point of the path (0-based)
   * @param t the last point of the path (0-based)
   * @param engine checked for time limits in the local search
   * @return the points in path order, from <code>s</code> to <code>t</code>
   */
  public static int[] path(double[][] dist, int s, int t, EvalEngine engine) {
    int n = dist.length;
    double total = 0.0;
    for (double[] row : dist) {
      for (double d : row) {
        total += Math.abs(d);
      }
    }
    double[][] forced = new double[n][];
    for (int i = 0; i < n; i++) {
      forced[i] = dist[i].clone();
    }
    // cheaper than the whole rest of any tour, so that every shortest tour uses this edge
    forced[s][t] = forced[t][s] = -(total + 1.0);
    int[] order = rotate(new TravelingSalesman(forced, engine).solve(), s);
    if (order[n - 1] != t) {
      // the tour runs s -> t -> ...; read it backwards from s so that it ends with t
      reverse(order, 1, n - 1);
    }
    return order;
  }

  private int[] solve() {
    if (n <= 3) {
      int[] order = new int[n];
      for (int i = 0; i < n; i++) {
        order[i] = i;
      }
      return order;
    }
    if (n <= EXACT_LIMIT) {
      return heldKarp();
    }
    return localSearch();
  }

  /** The exact dynamic program over the subsets of the points, starting at point 0. */
  private int[] heldKarp() {
    int m = n - 1;
    int full = (1 << m) - 1;
    double[][] cost = new double[1 << m][m];
    int[][] parent = new int[1 << m][m];
    for (double[] row : cost) {
      Arrays.fill(row, Double.POSITIVE_INFINITY);
    }
    for (int j = 0; j < m; j++) {
      cost[1 << j][j] = dist[0][j + 1];
      parent[1 << j][j] = -1;
    }
    for (int mask = 1; mask <= full; mask++) {
      for (int j = 0; j < m; j++) {
        double c = cost[mask][j];
        if ((mask & (1 << j)) == 0 || c == Double.POSITIVE_INFINITY) {
          continue;
        }
        for (int k = 0; k < m; k++) {
          if ((mask & (1 << k)) != 0) {
            continue;
          }
          int next = mask | (1 << k);
          double candidate = c + dist[j + 1][k + 1];
          if (candidate < cost[next][k] - eps) {
            cost[next][k] = candidate;
            parent[next][k] = j;
          }
        }
      }
    }
    double best = Double.POSITIVE_INFINITY;
    int last = 0;
    for (int j = 0; j < m; j++) {
      double candidate = cost[full][j] + dist[j + 1][0];
      if (candidate < best - eps) {
        best = candidate;
        last = j;
      }
    }
    int[] order = new int[n];
    int mask = full;
    int j = last;
    for (int pos = n - 1; pos >= 1; pos--) {
      order[pos] = j + 1;
      int previous = parent[mask][j];
      mask &= ~(1 << j);
      j = previous;
    }
    order[0] = 0;
    return order;
  }

  /** The best of several nearest neighbour tours, each improved by 2-opt and Or-opt. */
  private int[] localSearch() {
    int starts = n <= ALL_STARTS_LIMIT ? n : Math.max(MIN_STARTS, (ALL_STARTS_LIMIT * ALL_STARTS_LIMIT) / n);
    int[] best = null;
    double bestLength = Double.POSITIVE_INFINITY;
    for (int s = 0; s < starts; s++) {
      // spread the start points evenly over the input
      int start = (int) ((long) s * n / starts);
      int[] order = nearestNeighbour(start);
      improve(order);
      double length = length(order);
      if (length < bestLength - eps) {
        bestLength = length;
        best = order;
      }
    }
    return best;
  }

  private int[] nearestNeighbour(int start) {
    int[] order = new int[n];
    boolean[] visited = new boolean[n];
    order[0] = start;
    visited[start] = true;
    for (int pos = 1; pos < n; pos++) {
      int current = order[pos - 1];
      int next = -1;
      double nearest = Double.POSITIVE_INFINITY;
      for (int k = 0; k < n; k++) {
        if (!visited[k] && dist[current][k] < nearest) {
          nearest = dist[current][k];
          next = k;
        }
      }
      order[pos] = next;
      visited[next] = true;
    }
    return order;
  }

  /** Apply 2-opt and Or-opt moves until neither shortens the tour. */
  private void improve(int[] order) {
    boolean improved = true;
    while (improved) {
      engine.checkTimeLimit();
      improved = twoOpt(order);
      if (orOpt(order)) {
        improved = true;
      }
    }
  }

  /** One sweep of 2-opt: replace the edges a-b and c-d by a-c and b-d where that is shorter. */
  private boolean twoOpt(int[] order) {
    boolean improved = false;
    for (int i = 0; i < n - 1; i++) {
      int a = order[i];
      int b = order[i + 1];
      for (int j = i + 2; j < n; j++) {
        int c = order[j];
        int d = order[(j + 1) % n];
        if (d == a) {
          continue;
        }
        double delta = dist[a][c] + dist[b][d] - dist[a][b] - dist[c][d];
        if (delta < -eps) {
          reverse(order, i + 1, j);
          b = order[i + 1];
          improved = true;
        }
      }
    }
    return improved;
  }

  /**
   * One sweep of Or-opt: move a run of one to three consecutive points, either way round, between
   * two other neighbouring points where that is shorter.
   */
  private boolean orOpt(int[] order) {
    boolean improved = false;
    for (int len = 1; len <= 3 && len < n - 2; len++) {
      for (int i = 0; i < n; i++) {
        // the run order[i .. i+len-1] (cyclically), between p and q
        int first = order[i];
        int last = order[(i + len - 1) % n];
        int p = order[(i - 1 + n) % n];
        int q = order[(i + len) % n];
        double removeGain = dist[p][first] + dist[last][q] - dist[p][q];
        if (removeGain <= eps) {
          continue;
        }
        double bestDelta = -eps;
        int bestPos = -1;
        boolean bestReversed = false;
        // the edges u-v of the tour without the run
        for (int k = 0; k < n - len - 1; k++) {
          int u = order[(i + len + k) % n];
          int v = order[(i + len + k + 1) % n];
          double insert = dist[u][first] + dist[last][v] - dist[u][v];
          double insertReversed = dist[u][last] + dist[first][v] - dist[u][v];
          if (insert - removeGain < bestDelta) {
            bestDelta = insert - removeGain;
            bestPos = k;
            bestReversed = false;
          }
          if (insertReversed - removeGain < bestDelta) {
            bestDelta = insertReversed - removeGain;
            bestPos = k;
            bestReversed = true;
          }
        }
        if (bestPos >= 0) {
          int[] rest = new int[n - len];
          for (int k = 0; k < n - len; k++) {
            rest[k] = order[(i + len + k) % n];
          }
          int[] run = new int[len];
          for (int k = 0; k < len; k++) {
            run[k] = order[(i + k) % n];
          }
          if (bestReversed) {
            reverse(run, 0, len - 1);
          }
          int pos = 0;
          for (int k = 0; k <= bestPos; k++) {
            order[pos++] = rest[k];
          }
          for (int k = 0; k < len; k++) {
            order[pos++] = run[k];
          }
          for (int k = bestPos + 1; k < n - len; k++) {
            order[pos++] = rest[k];
          }
          improved = true;
        }
      }
    }
    return improved;
  }

  private double length(int[] order) {
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
      sum += dist[order[i]][order[(i + 1) % n]];
    }
    return sum;
  }

  /** The tour read from <code>start</code> on. */
  private static int[] rotate(int[] order, int start) {
    int n = order.length;
    int offset = 0;
    while (order[offset] != start) {
      offset++;
    }
    int[] result = new int[n];
    for (int i = 0; i < n; i++) {
      result[i] = order[(offset + i) % n];
    }
    return result;
  }

  private static void reverse(int[] order, int from, int to) {
    while (from < to) {
      int swap = order[from];
      order[from++] = order[to];
      order[to--] = swap;
    }
  }
}
