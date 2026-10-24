package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Propagators of the scheduling constraints no_overlap, no_overlap_2d, cumulative and reservoir
 * of the Java engine which replaces the C++ CP-SAT solver of OR-Tools.
 * An interval is given by affine views of its start, size and end and by its presence literals
 * (the enforcement literals of the interval constraint); the relation start + size == end of a
 * present interval is propagated by a separate linear constraint.
 */
final class SchedulingPropagators {
  private SchedulingPropagators() {}

  /** An interval variable. */
  static final class Interval {
    final Affine start;
    final Affine size;
    final Affine end;
    final int[] presence;

    Interval(Affine start, Affine size, Affine end, int[] presence) {
      this.start = start;
      this.size = size;
      this.end = end;
      this.presence = presence;
    }

    boolean isPresent(DomainStore s) {
      for (int lit : presence) {
        if (!s.litTrue(lit)) {
          return false;
        }
      }
      return true;
    }

    boolean isAbsent(DomainStore s) {
      for (int lit : presence) {
        if (s.litFalse(lit)) {
          return true;
        }
      }
      return false;
    }

    /**
     * Makes the interval absent if this is possible with a single literal; returns false if the
     * interval is known present (or cannot be made absent).
     */
    boolean makeAbsent(DomainStore s, boolean[] done) {
      int unfixed = 0;
      int last = 0;
      for (int lit : presence) {
        if (s.litFalse(lit)) {
          done[0] = true;
          return true;
        }
        if (!s.litTrue(lit)) {
          unfixed++;
          last = lit;
        }
      }
      if (unfixed == 0) {
        return false;
      }
      if (unfixed == 1) {
        done[0] = true;
        return s.setLit(last, false);
      }
      done[0] = false;
      return true;
    }

    int[] vars() {
      return Propagator.concat(Propagator.affVars(start, size, end),
          Propagator.litVars(presence));
    }
  }

  static int[] intervalVars(Interval[] intervals) {
    List<int[]> parts = new ArrayList<>();
    for (Interval i : intervals) {
      parts.add(i.vars());
    }
    return Propagator.concat(parts.toArray(new int[0][]));
  }

  /**
   * Handles a pair of intervals of which at most one ordering is possible: fails if both are
   * present and no ordering is possible, makes one of them absent if the other is present.
   * Returns false on failure.
   */
  private static boolean conflict(DomainStore s, Interval a, Interval b, boolean pa, boolean pb) {
    boolean[] done = new boolean[1];
    if (pa && pb) {
      return false;
    }
    if (pa) {
      return b.makeAbsent(s, done);
    }
    if (pb) {
      return a.makeAbsent(s, done);
    }
    return true;
  }

  /** All present intervals are pairwise disjoint (disjunctive resource). */
  static final class NoOverlap extends Propagator.Enforced {
    final Interval[] intervals;
    private final int[] watched;

    NoOverlap(Interval[] intervals, int[] enforcement) {
      super(enforcement);
      this.intervals = intervals;
      this.watched = concat(intervalVars(intervals), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      final int n = intervals.length;
      for (int i = 0; i < n; i++) {
        Interval a = intervals[i];
        if (!a.isPresent(s)) {
          continue;
        }
        for (int j = i + 1; j < n; j++) {
          Interval b = intervals[j];
          if (!b.isPresent(s)) {
            continue;
          }
          if (a.end.min(s) > b.start.max(s) && b.end.min(s) > a.start.max(s)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = intervals.length;
      boolean changed = false;
      for (int i = 0; i < n; i++) {
        Interval a = intervals[i];
        if (a.isAbsent(s)) {
          continue;
        }
        for (int j = i + 1; j < n; j++) {
          Interval b = intervals[j];
          if (b.isAbsent(s)) {
            continue;
          }
          boolean pa = a.isPresent(s);
          boolean pb = b.isPresent(s);
          boolean ab = a.end.min(s) <= b.start.max(s);
          boolean ba = b.end.min(s) <= a.start.max(s);
          if (!ab && !ba) {
            if (!conflict(s, a, b, pa, pb)) {
              return false;
            }
            continue;
          }
          if (!pa || !pb) {
            continue;
          }
          if (!ab) {
            // b before a
            long am = a.start.min(s);
            long bm = b.end.max(s);
            if (!a.start.setMin(s, b.end.min(s)) || !b.end.setMax(s, a.start.max(s))) {
              return false;
            }
            changed |= am != a.start.min(s) || bm != b.end.max(s);
          } else if (!ba) {
            long bm = b.start.min(s);
            long am = a.end.max(s);
            if (!b.start.setMin(s, a.end.min(s)) || !a.end.setMax(s, b.start.max(s))) {
              return false;
            }
            changed |= bm != b.start.min(s) || am != a.end.max(s);
          }
        }
      }
      if (changed) {
        // the start/size/end links are propagated by the linear constraints first
        s.enqueue(this);
      }
      return true;
    }
  }

  /** The present rectangles do not overlap. */
  static final class NoOverlap2D extends Propagator.Enforced {
    final Interval[] xs;
    final Interval[] ys;
    private final int[] watched;

    NoOverlap2D(Interval[] xs, Interval[] ys, int[] enforcement) {
      super(enforcement);
      this.xs = xs;
      this.ys = ys;
      this.watched = concat(intervalVars(xs), intervalVars(ys), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private boolean present(DomainStore s, int i) {
      return xs[i].isPresent(s) && ys[i].isPresent(s);
    }

    private boolean absent(DomainStore s, int i) {
      return xs[i].isAbsent(s) || ys[i].isAbsent(s);
    }

    // the possible separations of boxes i and j; bit 0: i left of j, 1: j left of i,
    // 2: i below j, 3: j below i, 4: both x sizes zero, 5: both y sizes zero
    private int options(DomainStore s, int i, int j) {
      int r = 0;
      if (xs[i].end.min(s) <= xs[j].start.max(s)) {
        r |= 1;
      }
      if (xs[j].end.min(s) <= xs[i].start.max(s)) {
        r |= 2;
      }
      if (ys[i].end.min(s) <= ys[j].start.max(s)) {
        r |= 4;
      }
      if (ys[j].end.min(s) <= ys[i].start.max(s)) {
        r |= 8;
      }
      if (xs[i].size.min(s) <= 0 && xs[j].size.min(s) <= 0) {
        r |= 16;
      }
      if (ys[i].size.min(s) <= 0 && ys[j].size.min(s) <= 0) {
        r |= 32;
      }
      return r;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      for (int i = 0; i < xs.length; i++) {
        if (!present(s, i)) {
          continue;
        }
        for (int j = i + 1; j < xs.length; j++) {
          if (present(s, j) && options(s, i, j) == 0) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = xs.length;
      boolean changed = false;
      for (int i = 0; i < n; i++) {
        if (absent(s, i)) {
          continue;
        }
        for (int j = i + 1; j < n; j++) {
          if (absent(s, j)) {
            continue;
          }
          boolean pi = present(s, i);
          boolean pj = present(s, j);
          int opt = options(s, i, j);
          if (opt == 0) {
            if (pi && pj) {
              return false;
            }
            if (pi && !makeBoxAbsent(s, j)) {
              return false;
            }
            if (pj && !makeBoxAbsent(s, i)) {
              return false;
            }
            continue;
          }
          if (!pi || !pj || Integer.bitCount(opt) != 1) {
            continue;
          }
          long before = boxBoundsHash(s, i) * 31 + boxBoundsHash(s, j);
          boolean ok;
          switch (opt) {
            case 1:
              ok = xs[j].start.setMin(s, xs[i].end.min(s)) && xs[i].end.setMax(s, xs[j].start.max(s));
              break;
            case 2:
              ok = xs[i].start.setMin(s, xs[j].end.min(s)) && xs[j].end.setMax(s, xs[i].start.max(s));
              break;
            case 4:
              ok = ys[j].start.setMin(s, ys[i].end.min(s)) && ys[i].end.setMax(s, ys[j].start.max(s));
              break;
            case 8:
              ok = ys[i].start.setMin(s, ys[j].end.min(s)) && ys[j].end.setMax(s, ys[i].start.max(s));
              break;
            case 16:
              ok = xs[i].size.setMax(s, 0) && xs[j].size.setMax(s, 0);
              break;
            default:
              ok = ys[i].size.setMax(s, 0) && ys[j].size.setMax(s, 0);
              break;
          }
          if (!ok) {
            return false;
          }
          if (before != boxBoundsHash(s, i) * 31 + boxBoundsHash(s, j)) {
            changed = true;
          }
        }
      }
      if (changed) {
        s.enqueue(this);
      }
      return true;
    }

    // box i (not absent, not present) must be absent: fixes its last unfixed presence literal
    private boolean makeBoxAbsent(DomainStore s, int i) {
      int unfixed = 0;
      int last = 0;
      for (Interval iv : new Interval[] {xs[i], ys[i]}) {
        for (int lit : iv.presence) {
          if (s.litFalse(lit)) {
            return true;
          }
          if (!s.litTrue(lit)) {
            unfixed++;
            last = lit;
          }
        }
      }
      if (unfixed == 0) {
        return false;
      }
      if (unfixed == 1) {
        return s.setLit(last, false);
      }
      return true;
    }

    // a fingerprint of the bounds of box i (changes whenever a bound changes)
    private long boxBoundsHash(DomainStore s, int i) {
      long h = 17;
      for (Interval iv : new Interval[] {xs[i], ys[i]}) {
        h = h * 1_000_003L + iv.start.min(s);
        h = h * 1_000_003L + iv.start.max(s);
        h = h * 1_000_003L + iv.end.min(s);
        h = h * 1_000_003L + iv.end.max(s);
        h = h * 1_000_003L + iv.size.max(s);
      }
      return h;
    }
  }

  /** Time-table propagation of {@code sum of demands of the tasks running at t <= capacity}. */
  static final class Cumulative extends Propagator.Enforced {
    final Interval[] tasks;
    final Affine[] demands;
    final Affine capacity;
    private final int[] watched;

    Cumulative(Interval[] tasks, Affine[] demands, Affine capacity, int[] enforcement) {
      super(enforcement);
      this.tasks = tasks;
      this.demands = demands;
      this.capacity = capacity;
      this.watched = concat(intervalVars(tasks), affVars(demands), affVars(capacity),
          enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    /** Profile segments: times[k] .. times[k+1] has load loads[k]. */
    private long[][] profile(DomainStore s) {
      List<long[]> events = new ArrayList<>();
      for (int i = 0; i < tasks.length; i++) {
        Interval t = tasks[i];
        if (!t.isPresent(s)) {
          continue;
        }
        long d = demands[i].min(s);
        if (d <= 0 || t.size.min(s) <= 0) {
          continue;
        }
        long a = t.start.max(s);
        long b = t.end.min(s);
        if (a < b) {
          events.add(new long[] {a, d});
          events.add(new long[] {b, -d});
        }
      }
      events.sort((x, y) -> Long.compare(x[0], y[0]));
      List<long[]> segs = new ArrayList<>();
      long load = 0;
      int k = 0;
      while (k < events.size()) {
        long time = events.get(k)[0];
        while (k < events.size() && events.get(k)[0] == time) {
          load += events.get(k)[1];
          k++;
        }
        if (k < events.size()) {
          segs.add(new long[] {time, events.get(k)[0], load});
        }
      }
      return segs.toArray(new long[0][]);
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      long capMax = capacity.max(s);
      if (capMax < 0) {
        return true;
      }
      for (long[] seg : profile(s)) {
        if (seg[2] > capMax) {
          return true;
        }
      }
      return false;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      if (!capacity.setMin(s, 0)) {
        return false;
      }
      long[][] segs = profile(s);
      long maxLoad = 0;
      for (long[] seg : segs) {
        maxLoad = Math.max(maxLoad, seg[2]);
      }
      if (!capacity.setMin(s, maxLoad)) {
        return false;
      }
      long capMax = capacity.max(s);
      boolean changed = false;
      for (int i = 0; i < tasks.length; i++) {
        Interval t = tasks[i];
        if (t.isAbsent(s)) {
          continue;
        }
        long d = demands[i].min(s);
        long p = t.size.min(s);
        if (d <= 0 || p <= 0) {
          continue;
        }
        boolean present = t.isPresent(s);
        // own compulsory part
        long ownA = Long.MAX_VALUE;
        long ownB = Long.MIN_VALUE;
        if (present && t.start.max(s) < t.end.min(s)) {
          ownA = t.start.max(s);
          ownB = t.end.min(s);
        }
        // earliest feasible start
        long st = t.start.min(s);
        for (long[] seg : segs) {
          long load = seg[2] - ((seg[0] >= ownA && seg[1] <= ownB) ? d : 0);
          if (load + d <= capMax) {
            continue;
          }
          if (seg[0] < st + p && seg[1] > st) {
            st = seg[1];
          }
        }
        if (st > t.start.max(s)) {
          if (present) {
            return false;
          }
          boolean[] done = new boolean[1];
          if (!t.makeAbsent(s, done)) {
            return false;
          }
          continue;
        }
        // latest feasible end
        long en = t.end.max(s);
        for (int k = segs.length - 1; k >= 0; k--) {
          long[] seg = segs[k];
          long load = seg[2] - ((seg[0] >= ownA && seg[1] <= ownB) ? d : 0);
          if (load + d <= capMax) {
            continue;
          }
          if (seg[0] < en && seg[1] > en - p) {
            en = seg[0];
          }
        }
        if (en < t.end.min(s)) {
          if (present) {
            return false;
          }
          boolean[] done = new boolean[1];
          if (!t.makeAbsent(s, done)) {
            return false;
          }
          continue;
        }
        if (present) {
          long s0 = t.start.min(s);
          long e0 = t.end.max(s);
          if (!t.start.setMin(s, st) || !t.end.setMax(s, en)) {
            return false;
          }
          changed |= s0 != t.start.min(s) || e0 != t.end.max(s);
        }
      }
      if (changed) {
        s.enqueue(this);
      }
      return true;
    }
  }

  /** The reservoir level stays within [minLevel, maxLevel] at all times. */
  static final class Reservoir extends Propagator.Enforced {
    final Affine[] times;
    final Affine[] changes;
    final int[] actives;
    final long minLevel;
    final long maxLevel;
    private final int[] watched;

    Reservoir(Affine[] times, Affine[] changes, int[] actives, long minLevel, long maxLevel,
        int[] enforcement) {
      super(enforcement);
      this.times = times;
      this.changes = changes;
      this.actives = actives;
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
      this.watched = concat(affVars(times), affVars(changes), litVars(actives), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      return !check(s);
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      return check(s);
    }

    private boolean check(DomainStore s) {
      final int n = times.length;
      long[] candidates = new long[2 * n];
      for (int i = 0; i < n; i++) {
        candidates[2 * i] = times[i].min(s);
        candidates[2 * i + 1] = times[i].max(s);
      }
      Arrays.sort(candidates);
      for (int c = 0; c < candidates.length; c++) {
        if (c > 0 && candidates[c] == candidates[c - 1]) {
          continue;
        }
        long t = candidates[c];
        long lo = 0;
        long hi = 0;
        for (int i = 0; i < n; i++) {
          int lit = actives[i];
          if (s.litFalse(lit)) {
            continue;
          }
          boolean surelyActive = s.litTrue(lit);
          if (surelyActive && times[i].max(s) <= t) {
            lo += changes[i].min(s);
            hi += changes[i].max(s);
          } else if (times[i].min(s) <= t) {
            lo += Math.min(0, changes[i].min(s));
            hi += Math.max(0, changes[i].max(s));
          }
        }
        if (lo > maxLevel || hi < minLevel) {
          return false;
        }
      }
      return true;
    }
  }
}
