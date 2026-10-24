package org.matheclipse.ortools.sat.engine;

import java.util.Arrays;

/**
 * Subtour elimination of the circuit and routes constraints for the Java engine which replaces
 * the C++ CP-SAT solver of OR-Tools. The degree conditions (one selected
 * outgoing and incoming arc per node) are posted separately as exactly-one constraints; these
 * propagators forbid the arcs which would close a forbidden cycle.
 */
final class GraphPropagators {
  private GraphPropagators() {}

  /**
   * Circuit: the selected arcs which are not self-loops form a single cycle; a node not in the
   * cycle must select its self-loop.
   */
  static final class Circuit extends Propagator.Enforced {
    final int numNodes;
    final int[] tails;
    final int[] heads;
    final int[] lits;
    /** index of the self-loop arc of each node or -1 */
    final int[] loopArc;
    /** nodes with incident arcs */
    final boolean[] isNode;
    private final int[] watched;

    Circuit(int[] tails, int[] heads, int[] lits, int[] enforcement) {
      super(enforcement);
      this.tails = tails;
      this.heads = heads;
      this.lits = lits;
      int max = -1;
      for (int i = 0; i < tails.length; i++) {
        max = Math.max(max, Math.max(tails[i], heads[i]));
      }
      this.numNodes = max + 1;
      this.loopArc = new int[numNodes];
      Arrays.fill(loopArc, -1);
      this.isNode = new boolean[numNodes];
      for (int i = 0; i < tails.length; i++) {
        isNode[tails[i]] = true;
        isNode[heads[i]] = true;
        if (tails[i] == heads[i]) {
          loopArc[tails[i]] = i;
        }
      }
      this.watched = concat(litVars(lits), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private boolean canSkip(DomainStore s, int node) {
      return loopArc[node] >= 0 && !s.litFalse(lits[loopArc[node]]);
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      int[] next = new int[numNodes];
      Arrays.fill(next, -1);
      for (int i = 0; i < tails.length; i++) {
        if (tails[i] != heads[i] && s.litTrue(lits[i])) {
          if (next[tails[i]] >= 0) {
            return true;
          }
          next[tails[i]] = heads[i];
        }
      }
      boolean[] onCycle = closedCycle(next);
      if (onCycle == null) {
        return false;
      }
      for (int v = 0; v < numNodes; v++) {
        if (isNode[v] && !onCycle[v] && !canSkip(s, v)) {
          return true;
        }
      }
      return false;
    }

    // the nodes of a closed cycle of the successor map, or null
    private boolean[] closedCycle(int[] next) {
      for (int v = 0; v < numNodes; v++) {
        if (next[v] < 0) {
          continue;
        }
        int u = next[v];
        int steps = 0;
        while (u >= 0 && u != v && steps <= numNodes) {
          u = next[u];
          steps++;
        }
        if (u == v) {
          boolean[] on = new boolean[numNodes];
          on[v] = true;
          for (int w = next[v]; w != v; w = next[w]) {
            on[w] = true;
          }
          return on;
        }
      }
      return null;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      int[] next = new int[numNodes];
      int[] prev = new int[numNodes];
      Arrays.fill(next, -1);
      Arrays.fill(prev, -1);
      for (int i = 0; i < tails.length; i++) {
        if (tails[i] != heads[i] && s.litTrue(lits[i])) {
          if (next[tails[i]] >= 0 || prev[heads[i]] >= 0) {
            return false;
          }
          next[tails[i]] = heads[i];
          prev[heads[i]] = tails[i];
        }
      }
      boolean[] onCycle = closedCycle(next);
      if (onCycle != null) {
        // every other node must take its self-loop
        for (int v = 0; v < numNodes; v++) {
          if (isNode[v] && !onCycle[v]) {
            if (loopArc[v] < 0 || !s.setLit(lits[loopArc[v]], true)) {
              return false;
            }
          }
        }
        return true;
      }
      // open chains: the arc closing a chain is forbidden if a node outside must be visited
      int[] chainEndOf = new int[numNodes];
      boolean[] inChain = new boolean[numNodes];
      Arrays.fill(chainEndOf, -1);
      for (int v = 0; v < numNodes; v++) {
        if (prev[v] >= 0 || next[v] < 0) {
          continue;
        }
        int e = v;
        while (next[e] >= 0) {
          e = next[e];
        }
        chainEndOf[v] = e;
      }
      for (int i = 0; i < tails.length; i++) {
        int t = tails[i];
        int h = heads[i];
        if (t == h || s.litFixed(lits[i])) {
          continue;
        }
        // arc t -> h closes the chain starting at h if it ends at t
        if (chainEndOf[h] == t) {
          Arrays.fill(inChain, false);
          for (int w = h; w >= 0; w = next[w]) {
            inChain[w] = true;
          }
          boolean allowed = true;
          for (int v = 0; v < numNodes && allowed; v++) {
            if (isNode[v] && !inChain[v] && !canSkip(s, v)) {
              allowed = false;
            }
          }
          if (!allowed && !s.setLit(lits[i], false)) {
            return false;
          }
        }
      }
      return true;
    }
  }

  /** Routes: no cycle which does not go through the depot (node 0). */
  static final class Routes extends Propagator.Enforced {
    final int numNodes;
    final int[] tails;
    final int[] heads;
    final int[] lits;
    private final int[] watched;

    Routes(int[] tails, int[] heads, int[] lits, int[] enforcement) {
      super(enforcement);
      this.tails = tails;
      this.heads = heads;
      this.lits = lits;
      int max = -1;
      for (int i = 0; i < tails.length; i++) {
        max = Math.max(max, Math.max(tails[i], heads[i]));
      }
      this.numNodes = max + 1;
      this.watched = concat(litVars(lits), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private int[] successors(DomainStore s) {
      int[] next = new int[numNodes];
      Arrays.fill(next, -1);
      for (int i = 0; i < tails.length; i++) {
        int t = tails[i];
        int h = heads[i];
        if (t != h && t != 0 && h != 0 && s.litTrue(lits[i])) {
          if (next[t] >= 0) {
            return null;
          }
          next[t] = h;
        }
      }
      return next;
    }

    private boolean hasCycle(int[] next) {
      for (int v = 1; v < numNodes; v++) {
        int u = next[v];
        int steps = 0;
        while (u >= 0 && u != v && steps <= numNodes) {
          u = next[u];
          steps++;
        }
        if (u == v) {
          return true;
        }
      }
      return false;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      int[] next = successors(s);
      return next == null || hasCycle(next);
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      int[] next = successors(s);
      if (next == null || hasCycle(next)) {
        return false;
      }
      int[] prev = new int[numNodes];
      Arrays.fill(prev, -1);
      for (int v = 1; v < numNodes; v++) {
        if (next[v] >= 0) {
          if (prev[next[v]] >= 0) {
            return false;
          }
          prev[next[v]] = v;
        }
      }
      int[] chainEnd = new int[numNodes];
      Arrays.fill(chainEnd, -1);
      for (int v = 1; v < numNodes; v++) {
        if (prev[v] >= 0) {
          continue;
        }
        int e = v;
        while (next[e] >= 0) {
          e = next[e];
        }
        chainEnd[v] = e;
      }
      for (int i = 0; i < tails.length; i++) {
        int t = tails[i];
        int h = heads[i];
        if (t == h || t == 0 || h == 0 || s.litFixed(lits[i])) {
          continue;
        }
        if (chainEnd[h] == t && !s.setLit(lits[i], false)) {
          return false;
        }
      }
      return true;
    }
  }
}
