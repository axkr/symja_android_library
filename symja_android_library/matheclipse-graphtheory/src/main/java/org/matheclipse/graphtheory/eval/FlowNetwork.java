package org.matheclipse.graphtheory.eval;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * A flow network on the nodes <code>0..n-1</code> with Dinic's maximum flow algorithm. After
 * {@link #maxFlow(int, int)} the residual network tells the two sides of the minimum cut closest to
 * the source ({@link #sourceSide()}) and closest to the sink ({@link #sinkSide()}).
 */
public final class FlowNetwork {
  private final int n;
  private int arcs = 0;
  private int[] next;
  private int[] to;
  private double[] capacity;
  private final int[] first;
  private int[] level;
  private int[] current;
  private int source = -1;
  private int sink = -1;

  public FlowNetwork(int n) {
    this.n = n;
    first = new int[n];
    Arrays.fill(first, -1);
    next = new int[16];
    to = new int[16];
    capacity = new double[16];
  }

  /**
   * Add an arc <code>u -&gt; v</code> (and its residual reverse arc).
   *
   * @return the id of the arc; {@link #flow(int)} reads its flow
   */
  public int addArc(int u, int v, double cap) {
    ensure(arcs + 2);
    int id = arcs;
    link(u, v, cap);
    link(v, u, 0.0);
    return id;
  }

  private void link(int u, int v, double cap) {
    to[arcs] = v;
    capacity[arcs] = cap;
    next[arcs] = first[u];
    first[u] = arcs;
    arcs++;
  }

  private void ensure(int size) {
    if (size > to.length) {
      int newLength = Math.max(size, 2 * to.length);
      next = Arrays.copyOf(next, newLength);
      to = Arrays.copyOf(to, newLength);
      capacity = Arrays.copyOf(capacity, newLength);
    }
  }

  /** The flow through the arc <code>id</code>, the residual capacity of its reverse arc. */
  public double flow(int id) {
    return capacity[id ^ 1];
  }

  /** The maximum flow from <code>s</code> to <code>t</code>. */
  public double maxFlow(int s, int t) {
    source = s;
    sink = t;
    level = new int[n];
    current = new int[n];
    double total = 0.0;
    while (bfs(s, t)) {
      System.arraycopy(first, 0, current, 0, n);
      double f;
      while ((f = dfs(s, t, Double.POSITIVE_INFINITY)) > 0.0) {
        total += f;
        if (Double.isInfinite(total)) {
          return total;
        }
      }
    }
    return total;
  }

  private boolean bfs(int s, int t) {
    Arrays.fill(level, -1);
    level[s] = 0;
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    queue.add(s);
    while (!queue.isEmpty()) {
      int u = queue.poll();
      for (int a = first[u]; a >= 0; a = next[a]) {
        if (capacity[a] > 1.0e-12 && level[to[a]] < 0) {
          level[to[a]] = level[u] + 1;
          queue.add(to[a]);
        }
      }
    }
    return level[t] >= 0;
  }

  private double dfs(int u, int t, double pushed) {
    if (u == t) {
      return pushed;
    }
    for (; current[u] >= 0; current[u] = next[current[u]]) {
      int a = current[u];
      int v = to[a];
      if (capacity[a] > 1.0e-12 && level[v] == level[u] + 1) {
        double f = dfs(v, t, Math.min(pushed, capacity[a]));
        if (f > 0.0) {
          capacity[a] -= f;
          capacity[a ^ 1] += f;
          return f;
        }
      }
    }
    return 0.0;
  }

  /** The nodes reachable from the source in the residual network: the cut closest to the source. */
  public boolean[] sourceSide() {
    boolean[] seen = new boolean[n];
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    seen[source] = true;
    queue.add(source);
    while (!queue.isEmpty()) {
      int u = queue.poll();
      for (int a = first[u]; a >= 0; a = next[a]) {
        if (capacity[a] > 1.0e-12 && !seen[to[a]]) {
          seen[to[a]] = true;
          queue.add(to[a]);
        }
      }
    }
    return seen;
  }

  /**
   * The nodes which can reach the sink in the residual network: the complement is the cut closest
   * to the sink.
   */
  public boolean[] sinkSide() {
    boolean[] seen = new boolean[n];
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    seen[sink] = true;
    queue.add(sink);
    while (!queue.isEmpty()) {
      int v = queue.poll();
      // arcs u -> v with residual capacity: the reverse of an arc leaving v
      for (int a = first[v]; a >= 0; a = next[a]) {
        int u = to[a];
        if (capacity[a ^ 1] > 1.0e-12 && !seen[u]) {
          seen[u] = true;
          queue.add(u);
        }
      }
    }
    return seen;
  }
}
