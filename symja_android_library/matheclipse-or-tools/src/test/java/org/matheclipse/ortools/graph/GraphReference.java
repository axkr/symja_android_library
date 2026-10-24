package org.matheclipse.ortools.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * Independent textbook implementations used to check the ported solvers: Edmonds-Karp maximum
 * flow, successive shortest paths (Bellman-Ford) minimum cost flow and brute force assignment.
 */
public final class GraphReference {
  private GraphReference() {}

  // Residual network with explicit reverse edges.
  private static final class Network {
    final int n;
    final int[] to;
    final long[] cap;
    final long[] cost;
    final int[] next;
    final int[] first;
    int size;

    Network(int n, int maxEdges) {
      this.n = n;
      to = new int[2 * maxEdges];
      cap = new long[2 * maxEdges];
      cost = new long[2 * maxEdges];
      next = new int[2 * maxEdges];
      first = new int[n];
      Arrays.fill(first, -1);
    }

    int add(int u, int v, long c, long w) {
      int e = size;
      to[size] = v;
      cap[size] = c;
      cost[size] = w;
      next[size] = first[u];
      first[u] = size++;
      to[size] = u;
      cap[size] = 0;
      cost[size] = -w;
      next[size] = first[v];
      first[v] = size++;
      return e;
    }
  }

  /** The maximum flow value from source to sink (Edmonds-Karp). */
  public static long maxFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
      int source, int sink) {
    if (source >= numNodes || sink >= numNodes) {
      return 0;
    }
    Network g = new Network(numNodes, tails.length);
    for (int i = 0; i < tails.length; ++i) {
      g.add(tails[i], heads[i], capacities[i], 0);
    }
    long flow = 0;
    while (true) {
      int[] parentEdge = new int[numNodes];
      Arrays.fill(parentEdge, -1);
      boolean[] seen = new boolean[numNodes];
      ArrayDeque<Integer> queue = new ArrayDeque<>();
      queue.add(source);
      seen[source] = true;
      while (!queue.isEmpty() && !seen[sink]) {
        int u = queue.poll();
        for (int e = g.first[u]; e >= 0; e = g.next[e]) {
          if (g.cap[e] > 0 && !seen[g.to[e]]) {
            seen[g.to[e]] = true;
            parentEdge[g.to[e]] = e;
            queue.add(g.to[e]);
          }
        }
      }
      if (!seen[sink]) {
        return flow;
      }
      long push = Long.MAX_VALUE;
      for (int v = sink; v != source; v = g.to[parentEdge[v] ^ 1]) {
        push = Math.min(push, g.cap[parentEdge[v]]);
      }
      for (int v = sink; v != source; v = g.to[parentEdge[v] ^ 1]) {
        g.cap[parentEdge[v]] -= push;
        g.cap[parentEdge[v] ^ 1] += push;
      }
      flow += push;
    }
  }

  /**
   * The minimum cost of a flow satisfying the supplies (successive shortest paths with
   * Bellman-Ford, so negative costs are fine as long as there is no negative cycle), or null if
   * the supplies are unbalanced or cannot be sent.
   */
  public static Long minCostFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
      long[] unitCosts, long[] supplies) {
    long totalSupply = 0;
    long totalDemand = 0;
    for (long s : supplies) {
      if (s > 0) {
        totalSupply += s;
      } else {
        totalDemand -= s;
      }
    }
    if (totalSupply != totalDemand) {
      return null;
    }
    final int source = numNodes;
    final int sink = numNodes + 1;
    Network g = new Network(numNodes + 2, tails.length + numNodes);
    for (int i = 0; i < tails.length; ++i) {
      g.add(tails[i], heads[i], capacities[i], unitCosts[i]);
    }
    for (int v = 0; v < numNodes; ++v) {
      if (supplies[v] > 0) {
        g.add(source, v, supplies[v], 0);
      } else if (supplies[v] < 0) {
        g.add(v, sink, -supplies[v], 0);
      }
    }
    // The tests avoid negative cycles, so successive shortest paths are optimal.
    long cost = 0;
    long sent = 0;
    final int n = numNodes + 2;
    while (sent < totalSupply) {
      long[] dist = new long[n];
      Arrays.fill(dist, Long.MAX_VALUE);
      int[] parentEdge = new int[n];
      Arrays.fill(parentEdge, -1);
      dist[source] = 0;
      for (int iter = 0; iter < n; ++iter) {
        boolean changed = false;
        for (int u = 0; u < n; ++u) {
          if (dist[u] == Long.MAX_VALUE) {
            continue;
          }
          for (int e = g.first[u]; e >= 0; e = g.next[e]) {
            if (g.cap[e] > 0 && dist[u] + g.cost[e] < dist[g.to[e]]) {
              dist[g.to[e]] = dist[u] + g.cost[e];
              parentEdge[g.to[e]] = e;
              changed = true;
            }
          }
        }
        if (!changed) {
          break;
        }
      }
      if (dist[sink] == Long.MAX_VALUE) {
        return null;
      }
      long push = totalSupply - sent;
      for (int v = sink; v != source; v = g.to[parentEdge[v] ^ 1]) {
        push = Math.min(push, g.cap[parentEdge[v]]);
      }
      for (int v = sink; v != source; v = g.to[parentEdge[v] ^ 1]) {
        g.cap[parentEdge[v]] -= push;
        g.cap[parentEdge[v] ^ 1] += push;
      }
      sent += push;
      cost += push * dist[sink];
    }
    return cost;
  }

  /**
   * The minimum cost of a perfect matching of the square matrix (by enumerating permutations),
   * where a negative entry {@link Long#MIN_VALUE} means "no arc"; null if there is no perfect
   * matching.
   */
  public static Long assignment(long[][] cost) {
    final int n = cost.length;
    int[] perm = new int[n];
    for (int i = 0; i < n; ++i) {
      perm[i] = i;
    }
    Long best = null;
    do {
      long sum = 0;
      boolean ok = true;
      for (int i = 0; i < n; ++i) {
        if (cost[i][perm[i]] == Long.MIN_VALUE) {
          ok = false;
          break;
        }
        sum += cost[i][perm[i]];
      }
      if (ok && (best == null || sum < best)) {
        best = sum;
      }
    } while (nextPermutation(perm));
    return best;
  }

  private static boolean nextPermutation(int[] a) {
    int i = a.length - 2;
    while (i >= 0 && a[i] >= a[i + 1]) {
      i--;
    }
    if (i < 0) {
      return false;
    }
    int j = a.length - 1;
    while (a[j] <= a[i]) {
      j--;
    }
    int t = a[i];
    a[i] = a[j];
    a[j] = t;
    for (int l = i + 1, r = a.length - 1; l < r; l++, r--) {
      t = a[l];
      a[l] = a[r];
      a[r] = t;
    }
    return true;
  }

  /**
   * Asserts that the arc flows respect the capacities and that every node's outflow minus inflow
   * equals its supply (or, for max flow, is zero except at source and sink).
   */
  public static void assertFlowConservation(int numNodes, int[] tails, int[] heads,
      long[] capacities, long[] flows, long[] supplies) {
    long[] balance = new long[numNodes];
    for (int i = 0; i < tails.length; ++i) {
      assertTrue(flows[i] >= 0 && flows[i] <= capacities[i],
          "flow " + flows[i] + " of arc " + i + " outside [0, " + capacities[i] + "]");
      balance[tails[i]] += flows[i];
      balance[heads[i]] -= flows[i];
    }
    for (int v = 0; v < numNodes; ++v) {
      assertEquals(supplies[v], balance[v], "flow balance of node " + v);
    }
  }
}
