package org.matheclipse.ortools.routing.engine;

import org.matheclipse.ortools.constraintsolver.cp.CapOps;

/**
 * Computes the cumul values of one dimension along one route: the feasible window of every
 * cumul (forward and backward interval propagation of
 * <code>cumul(next) = cumul(i) + transit + slack</code>), the placement of the vehicle breaks,
 * and the cost terms which depend on the cumuls (span, soft bounds).
 *
 * <p>
 * Part of the simplified routing engine of this module which reimplements the behaviour of
 * OR-Tools' dimension constraints and cumul optimizers with these direct computations: the span
 * is minimized by the earliest end and the latest start compatible with it; soft bounds are
 * evaluated on the earliest schedule from that start.
 */
public final class Scheduler {

  private static final long NOT_FIXED = Long.MIN_VALUE;

  private long[] lo = new long[16];

  private long[] hi = new long[16];

  private long[] lo2 = new long[16];

  private long[] hi2 = new long[16];

  private long[] transit = new long[16];

  private long[] cumul = new long[16];

  /** The break chosen on the arc leaving each position, -1 if none. */
  private int[] breakOnArc = new int[16];

  private int len;

  /** The start chosen for span costs. */
  public long start;

  /** The earliest end. */
  public long end;

  /** The breaks placed before the start, as indices into the break list, count. */
  private int breaksBeforeStart;

  /** Number of breaks of the vehicle and the break start chosen for each (or MIN if skipped). */
  public long[] breakStarts = new long[0];

  private void ensure(int n) {
    if (lo.length < n) {
      int m = Math.max(n, lo.length * 2);
      lo = new long[m];
      hi = new long[m];
      lo2 = new long[m];
      hi2 = new long[m];
      transit = new long[m];
      cumul = new long[m];
      breakOnArc = new int[m];
    }
  }

  private static long windowMin(DimensionData d, int index, int v) {
    return Math.max(0, d.cumulMin[index]);
  }

  private static long windowMax(DimensionData d, int index, int v) {
    return Math.min(d.cumulMax[index], d.capacity[v]);
  }

  /**
   * Runs the forward and backward passes for <code>route</code> (start ... end) of vehicle
   * <code>v</code>.
   *
   * @param fixed per index a value the cumul must take, <code>Long.MIN_VALUE</code> for none;
   *     may be <code>null</code>
   * @return whether a feasible schedule exists
   */
  public boolean run(DimensionData d, int v, int[] route, long[] fixed) {
    final int n = route.length;
    ensure(n);
    len = n;
    for (int k = 0; k + 1 < n; k++) {
      transit[k] = d.transitOfVehicle[v].get(route[k], route[k + 1]);
    }
    boolean breaks = d.hasBreaks(v);
    if (breaks) {
      if (!forwardWithBreaks(d, v, route, fixed)) {
        return false;
      }
    } else {
      if (!forward(d, v, route, fixed)) {
        return false;
      }
    }
    // backward pass
    lo2[n - 1] = lo[n - 1];
    hi2[n - 1] = hi[n - 1];
    for (int k = n - 2; k >= 0; k--) {
      long t = effectiveTransit(d, k);
      long h = Math.min(hi[k], CapOps.capSub(hi2[k + 1], t));
      long l = Math.max(lo[k], CapOps.capSub(CapOps.capSub(lo2[k + 1], t), d.slackMax));
      h = d.previousAllowed(route[k], h);
      l = d.nextAllowed(route[k], l);
      if (l > h) {
        return false;
      }
      lo2[k] = l;
      hi2[k] = h;
    }
    // span: earliest end, latest start compatible with it
    end = lo2[n - 1];
    long h = end;
    for (int k = n - 2; k >= 0; k--) {
      h = Math.min(hi2[k], CapOps.capSub(h, effectiveTransit(d, k)));
    }
    start = Math.max(h, lo2[0]);
    // concrete earliest schedule from start
    cumul[0] = start;
    for (int k = 0; k + 1 < n; k++) {
      long c = Math.max(CapOps.capAdd(cumul[k], effectiveTransit(d, k)), lo2[k + 1]);
      if (breakOnArc[k] >= 0 && breaks) {
        DimensionData.BreakSpec b = d.breaks[v][breakOnArc[k]];
        long s = Math.max(b.startMin,
            CapOps.capAdd(cumul[k], visit(d, v, route[k], route[k + 1])));
        c = Math.max(c, CapOps.capAdd(s, b.duration));
        c = Math.max(c, CapOps.capAdd(CapOps.capAdd(cumul[k], effectiveTransit(d, k)),
            b.duration));
      }
      cumul[k + 1] = Math.min(d.nextAllowed(route[k + 1], c), hi2[k + 1]);
    }
    if (breaks) {
      placeBreaks(d, v, route);
    }
    long span = CapOps.capSub(end, start);
    return span <= d.spanUpperBound[v];
  }

  private long effectiveTransit(DimensionData d, int k) {
    return transit[k];
  }

  private static long visit(DimensionData d, int v, int index, int next) {
    ArcMatrix pre = d.preTravel == null ? null : d.preTravel[v];
    if (pre != null) {
      return pre.get(index, next);
    }
    long[] vt = d.visitTransit == null ? null : d.visitTransit[v];
    return vt == null || index >= vt.length ? 0 : vt[index];
  }

  private boolean forward(DimensionData d, int v, int[] route, long[] fixed) {
    final int n = route.length;
    for (int k = 0; k < n; k++) {
      breakOnArc[k] = -1;
    }
    int idx = route[0];
    long l = windowMin(d, idx, v);
    long h = windowMax(d, idx, v);
    if (fixed != null && fixed[idx] != NOT_FIXED) {
      l = Math.max(l, fixed[idx]);
      h = Math.min(h, fixed[idx]);
    }
    l = d.nextAllowed(idx, l);
    h = d.previousAllowed(idx, h);
    if (l > h) {
      return false;
    }
    lo[0] = l;
    hi[0] = h;
    for (int k = 0; k + 1 < n; k++) {
      int next = route[k + 1];
      long t = transit[k];
      long nl = Math.max(CapOps.capAdd(lo[k], t), windowMin(d, next, v));
      long nh = Math.min(CapOps.capAdd(CapOps.capAdd(hi[k], t), d.slackMax),
          windowMax(d, next, v));
      if (fixed != null && fixed[next] != NOT_FIXED) {
        nl = Math.max(nl, fixed[next]);
        nh = Math.min(nh, fixed[next]);
      }
      nl = d.nextAllowed(next, nl);
      nh = d.previousAllowed(next, nh);
      if (nl > nh) {
        return false;
      }
      lo[k + 1] = nl;
      hi[k + 1] = nh;
    }
    return true;
  }

  /**
   * Forward pass with breaks: a dynamic program over (position, number of breaks handled) which
   * keeps the state with the earliest cumul. Breaks are handled in the order of their earliest
   * start; each may be taken before the start, on one arc (using slack), after the end, or
   * skipped when optional.
   */
  private boolean forwardWithBreaks(DimensionData d, int v, int[] route, long[] fixed) {
    final int n = route.length;
    DimensionData.BreakSpec[] all = d.breaks[v];
    final int nb = all.length;
    long[][] stLo = new long[n][nb + 1];
    long[][] stHi = new long[n][nb + 1];
    int[][] from = new int[n][nb + 1]; // previous number of breaks handled
    boolean[][] took = new boolean[n][nb + 1];
    for (int k = 0; k < n; k++) {
      for (int b = 0; b <= nb; b++) {
        stLo[k][b] = Long.MAX_VALUE;
        stHi[k][b] = Long.MIN_VALUE;
      }
    }
    int idx = route[0];
    long l0 = windowMin(d, idx, v);
    long h0 = windowMax(d, idx, v);
    if (fixed != null && fixed[idx] != NOT_FIXED) {
      l0 = Math.max(l0, fixed[idx]);
      h0 = Math.min(h0, fixed[idx]);
    }
    if (l0 > h0) {
      return false;
    }
    // breaks before the start: the first b breaks end before the start cumul
    long t0 = l0;
    long prevEnd = Long.MIN_VALUE;
    stLo[0][0] = l0;
    stHi[0][0] = h0;
    from[0][0] = -1;
    for (int b = 0; b < nb; b++) {
      DimensionData.BreakSpec br = all[b];
      long s = Math.max(br.startMin, prevEnd);
      if (s > br.startMax) {
        if (!br.optional) {
          break;
        }
        // skipped
      } else {
        prevEnd = CapOps.capAdd(s, br.duration);
      }
      long nl = Math.max(t0, prevEnd);
      if (nl > h0) {
        break;
      }
      stLo[0][b + 1] = nl;
      stHi[0][b + 1] = h0;
      from[0][b + 1] = -2; // before start
    }
    for (int k = 0; k + 1 < n; k++) {
      int next = route[k + 1];
      long t = transit[k];
      long wl = windowMin(d, next, v);
      long wh = windowMax(d, next, v);
      if (fixed != null && fixed[next] != NOT_FIXED) {
        wl = Math.max(wl, fixed[next]);
        wh = Math.min(wh, fixed[next]);
      }
      for (int b = 0; b <= nb; b++) {
        if (stLo[k][b] > stHi[k][b]) {
          continue;
        }
        long l = stLo[k][b];
        long h = stHi[k][b];
        // no break on this arc
        long nl = d.nextAllowed(next, Math.max(CapOps.capAdd(l, t), wl));
        long nh = d.previousAllowed(next,
            Math.min(CapOps.capAdd(CapOps.capAdd(h, t), d.slackMax), wh));
        relax(stLo, stHi, from, took, k + 1, b, nl, nh, b, false);
        // the next break (or skip it if optional) on this arc
        if (b < nb) {
          DimensionData.BreakSpec br = all[b];
          if (br.optional) {
            relax(stLo, stHi, from, took, k + 1, b + 1, nl, nh, b, false);
          }
          if (br.duration <= d.slackMax) {
            long s = Math.max(br.startMin,
                CapOps.capAdd(l, visit(d, v, route[k], route[k + 1])));
            if (s <= br.startMax) {
              long bl = Math.max(nl, Math.max(CapOps.capAdd(CapOps.capAdd(l, t), br.duration),
                  CapOps.capAdd(s, br.duration)));
              bl = d.nextAllowed(next, bl);
              relax(stLo, stHi, from, took, k + 1, b + 1, bl, nh, b, true);
            }
          }
        }
      }
    }
    // breaks after the end
    int last = n - 1;
    int best = -1;
    long bestLo = Long.MAX_VALUE;
    long bestHi = Long.MIN_VALUE;
    for (int b = 0; b <= nb; b++) {
      if (stLo[last][b] > stHi[last][b]) {
        continue;
      }
      long l = stLo[last][b];
      long h = stHi[last][b];
      long t = l;
      boolean ok = true;
      for (int r = b; r < nb; r++) {
        DimensionData.BreakSpec br = all[r];
        long s = Math.max(br.startMin, t);
        if (s > br.startMax) {
          if (!br.optional) {
            // the end must be early enough for the break
            ok = false;
            break;
          }
          continue;
        }
        t = CapOps.capAdd(s, br.duration);
      }
      if (!ok) {
        // try reducing the end: the first mandatory break after the end bounds it
        long limit = Long.MAX_VALUE;
        for (int r = b; r < nb; r++) {
          if (!all[r].optional) {
            limit = Math.min(limit, all[r].startMax);
          }
        }
        if (l <= limit) {
          // the breaks fit if the end is at most the limit (sequential breaks ignored)
          h = Math.min(h, limit);
          ok = true;
        }
      }
      if (ok && (l < bestLo || (l == bestLo && h > bestHi))) {
        best = b;
        bestLo = l;
        bestHi = h;
      }
    }
    if (best < 0) {
      return false;
    }
    // reconstruct the path: windows per position and the break per arc
    for (int k = 0; k < n; k++) {
      breakOnArc[k] = -1;
    }
    int b = best;
    lo[last] = bestLo;
    hi[last] = bestHi;
    breaksBeforeStart = 0;
    for (int k = last; k > 0; k--) {
      int pb = from[k][b];
      if (took[k][b]) {
        breakOnArc[k - 1] = pb;
      }
      b = pb;
      lo[k - 1] = stLo[k - 1][b];
      hi[k - 1] = stHi[k - 1][b];
    }
    breaksBeforeStart = b;
    lastHandled = best;
    return true;
  }

  private int lastHandled;

  private static void relax(long[][] stLo, long[][] stHi, int[][] from, boolean[][] took, int k,
      int b, long l, long h, int prev, boolean tookBreak) {
    if (l > h) {
      return;
    }
    if (l < stLo[k][b] || (l == stLo[k][b] && h > stHi[k][b])) {
      stLo[k][b] = l;
      stHi[k][b] = h;
      from[k][b] = prev;
      took[k][b] = tookBreak;
    }
  }

  private void placeBreaks(DimensionData d, int v, int[] route) {
    DimensionData.BreakSpec[] all = d.breaks[v];
    breakStarts = new long[all.length];
    java.util.Arrays.fill(breakStarts, Long.MIN_VALUE);
    // before the start
    long t = Long.MIN_VALUE;
    for (int r = 0; r < breaksBeforeStart; r++) {
      DimensionData.BreakSpec br = all[r];
      long s = Math.max(br.startMin, t);
      if (s <= br.startMax && CapOps.capAdd(s, br.duration) <= cumul[0]) {
        breakStarts[r] = s;
        t = CapOps.capAdd(s, br.duration);
      }
    }
    for (int k = 0; k + 1 < len; k++) {
      int r = breakOnArc[k];
      if (r >= 0) {
        DimensionData.BreakSpec br = all[r];
        breakStarts[r] = Math.max(br.startMin,
            CapOps.capAdd(cumul[k], visit(d, v, route[k], route[k + 1])));
      }
    }
    t = cumul[len - 1];
    for (int r = lastHandled; r < all.length; r++) {
      DimensionData.BreakSpec br = all[r];
      long s = Math.max(br.startMin, t);
      if (s <= br.startMax) {
        breakStarts[r] = s;
        t = CapOps.capAdd(s, br.duration);
      }
    }
  }

  /** The dimension cost of the route after a successful {@link #run}. */
  public long cost(DimensionData d, int v, int[] route) {
    long cost = 0;
    if (d.spanCost[v] != 0) {
      cost = CapOps.capAdd(cost, CapOps.capProd(d.spanCost[v], CapOps.capSub(end, start)));
    }
    if (d.slackCost[v] != 0) {
      long transits = 0;
      for (int k = 0; k + 1 < len; k++) {
        transits = CapOps.capAdd(transits, transit[k]);
      }
      long slack = Math.max(0, CapOps.capSub(CapOps.capSub(end, start), transits));
      cost = CapOps.capAdd(cost, CapOps.capProd(d.slackCost[v], slack));
    }
    long span = CapOps.capSub(end, start);
    if (d.softSpanCost[v] != 0 && span > d.softSpanBound[v]) {
      cost = CapOps.capAdd(cost, CapOps.capProd(d.softSpanCost[v], span - d.softSpanBound[v]));
    }
    if (d.quadSoftSpanCost[v] != 0 && span > d.quadSoftSpanBound[v]) {
      long excess = span - d.quadSoftSpanBound[v];
      cost = CapOps.capAdd(cost,
          CapOps.capProd(d.quadSoftSpanCost[v], CapOps.capProd(excess, excess)));
    }
    for (int k = 0; k < len; k++) {
      int idx = route[k];
      if (d.softUpperCost[idx] != 0 && cumul[k] > d.softUpperBound[idx]) {
        cost = CapOps.capAdd(cost,
            CapOps.capProd(d.softUpperCost[idx], cumul[k] - d.softUpperBound[idx]));
      }
      if (d.softLowerCost[idx] != 0 && cumul[k] < d.softLowerBound[idx]) {
        cost = CapOps.capAdd(cost,
            CapOps.capProd(d.softLowerCost[idx], d.softLowerBound[idx] - cumul[k]));
      }
    }
    return cost;
  }

  /** The earliest-from-start cumul at position <code>k</code> after {@link #run}. */
  public long cumul(int k) {
    return cumul[k];
  }

  /** The smallest feasible cumul at position <code>k</code> after {@link #run}. */
  public long min(int k) {
    return lo2[k];
  }

  /** The largest feasible cumul at position <code>k</code> after {@link #run}. */
  public long max(int k) {
    return hi2[k];
  }

  /** The sum of the transits of the route after {@link #run}. */
  public long transitSum() {
    long t = 0;
    for (int k = 0; k + 1 < len; k++) {
      t = CapOps.capAdd(t, transit[k]);
    }
    return t;
  }

  /** The transit of the arc leaving position <code>k</code> after {@link #run}. */
  public long transit(int k) {
    return transit[k];
  }
}
