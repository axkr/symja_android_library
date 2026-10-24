package org.matheclipse.ortools.routing.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the routes printed by the routing samples and computes reference values (exact TSP
 * optima by dynamic programming) for the sample tests.
 */
public final class RoutingOutput {

  private RoutingOutput() {}

  /** One printed route. */
  public static final class Route {
    /** The vehicle number from the header, -1 if the header has none. */
    public final int vehicle;

    /** The nodes from start to end. */
    public final List<Integer> nodes = new ArrayList<>();

    /** The value of <code>Load(x)</code> per node, or null. */
    public final List<Long> loads = new ArrayList<>();

    /** The values of <code>Time(a, b)</code> or <code>Time(a)</code> per node, or null. */
    public final List<long[]> times = new ArrayList<>();

    /** The value of the following <code>... of the route: X</code> line, or -1. */
    public long reported = -1;

    Route(int vehicle) {
      this.vehicle = vehicle;
    }

    /** The first node. */
    public int start() {
      return nodes.get(0);
    }

    /** The last node. */
    public int end() {
      return nodes.get(nodes.size() - 1);
    }

    /** The visited nodes without start and end. */
    public List<Integer> visits() {
      return nodes.subList(1, nodes.size() - 1);
    }

    @Override
    public String toString() {
      return "vehicle " + vehicle + ": " + nodes;
    }
  }

  private static final Pattern HEADER = Pattern.compile("^Route(?: for [Vv]ehicle (\\d+))?:\\s*$");

  private static final Pattern LOAD = Pattern.compile("Load\\((-?\\d+)\\)");

  private static final Pattern TIME = Pattern.compile("Time\\((-?\\d+)(?:,\\s*(-?\\d+))?\\)");

  private static final Pattern REPORTED = Pattern.compile("of the route: (-?\\d+)");

  /** All routes of the output, in printing order. */
  public static List<Route> routes(String output) {
    String[] lines = output.split("\n");
    List<Route> routes = new ArrayList<>();
    for (int i = 0; i < lines.length; i++) {
      Matcher h = HEADER.matcher(lines[i].trim());
      if (!h.matches() || i + 1 >= lines.length) {
        continue;
      }
      Route r = new Route(h.group(1) == null ? -1 : Integer.parseInt(h.group(1)));
      for (String token : lines[i + 1].split(" -> ")) {
        String t = token.trim();
        Matcher num = Pattern.compile("^(\\d+)").matcher(t);
        if (!num.find()) {
          fail("can't parse route token <" + t + "> in:\n" + output);
        }
        r.nodes.add(Integer.parseInt(num.group(1)));
        Matcher load = LOAD.matcher(t);
        r.loads.add(load.find() ? Long.parseLong(load.group(1)) : null);
        Matcher time = TIME.matcher(t);
        if (time.find()) {
          long a = Long.parseLong(time.group(1));
          long b = time.group(2) == null ? a : Long.parseLong(time.group(2));
          r.times.add(new long[] {a, b});
        } else {
          r.times.add(null);
        }
      }
      if (i + 2 < lines.length) {
        Matcher rep = REPORTED.matcher(lines[i + 2]);
        if (rep.find()) {
          r.reported = Long.parseLong(rep.group(1));
        }
      }
      routes.add(r);
    }
    return routes;
  }

  /** The cost of the route on the matrix. */
  public static long length(Route r, long[][] matrix) {
    long total = 0;
    for (int k = 0; k + 1 < r.nodes.size(); k++) {
      total += matrix[r.nodes.get(k)][r.nodes.get(k + 1)];
    }
    return total;
  }

  /**
   * Asserts that the routes visit every node of <code>0 .. n-1</code> except the given depots and
   * dropped nodes exactly once, and none of the dropped nodes.
   */
  public static void assertCovers(List<Route> routes, int n, List<Integer> depots,
      List<Integer> dropped) {
    int[] count = new int[n];
    for (Route r : routes) {
      for (int x : r.visits()) {
        count[x]++;
      }
    }
    for (int x = 0; x < n; x++) {
      int expected = depots.contains(x) || dropped.contains(x) ? 0 : 1;
      assertEquals(expected, count[x], "visits of node " + x + " in " + routes);
    }
  }

  /** The node numbers following <code>prefix</code> on its line. */
  public static List<Integer> listAfter(String output, String prefix) {
    List<Integer> r = new ArrayList<>();
    for (String line : output.split("\n")) {
      int k = line.indexOf(prefix);
      if (k >= 0) {
        for (String t : line.substring(k + prefix.length()).trim().split("\\s+")) {
          if (!t.isEmpty()) {
            r.add(Integer.parseInt(t));
          }
        }
        return r;
      }
    }
    return r;
  }

  /** The length of the shortest closed tour through all nodes from node 0 (Held-Karp). */
  public static long shortestTour(long[][] d) {
    final int n = d.length;
    if (n <= 1) {
      return 0;
    }
    final int m = n - 1; // nodes 1..n-1 as bits 0..m-1
    long[] dp = new long[(1 << m) * m];
    Arrays.fill(dp, Long.MAX_VALUE);
    for (int j = 0; j < m; j++) {
      dp[(1 << j) * m + j] = d[0][j + 1];
    }
    for (int set = 1; set < (1 << m); set++) {
      for (int j = 0; j < m; j++) {
        long cur = dp[set * m + j];
        if (cur == Long.MAX_VALUE || (set & (1 << j)) == 0) {
          continue;
        }
        for (int k = 0; k < m; k++) {
          if ((set & (1 << k)) != 0) {
            continue;
          }
          int next = set | (1 << k);
          long c = cur + d[j + 1][k + 1];
          if (c < dp[next * m + k]) {
            dp[next * m + k] = c;
          }
        }
      }
    }
    long best = Long.MAX_VALUE;
    int full = (1 << m) - 1;
    for (int j = 0; j < m; j++) {
      best = Math.min(best, dp[full * m + j] + d[j + 1][0]);
    }
    return best;
  }

  /** Asserts that the route is a tour from node 0 through all nodes of the matrix. */
  public static void assertTour(Route r, int n) {
    assertEquals(0, r.start());
    assertEquals(0, r.end());
    assertEquals(n + 1, r.nodes.size(), "tour length of " + r);
    boolean[] seen = new boolean[n];
    for (int x : r.visits()) {
      assertTrue(!seen[x] && x > 0 && x < n, "node " + x + " twice or invalid in " + r);
      seen[x] = true;
    }
  }
}
