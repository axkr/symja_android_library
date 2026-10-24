package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.ortools.util.Domain;

/**
 * Domain store, trail and propagation queue of the Java constraint engine which replaces the C++
 * CP-SAT solver of OR-Tools in this module.
 *
 * <p>
 * Every variable has a current lower and upper bound. Variables with a small initial span keep an
 * additional bitset of their remaining values, so that values can be removed anywhere; for the
 * other variables the domain is the initial domain (with its holes) intersected with the current
 * bounds, and interior value removals are ignored. Every change is recorded on a trail and undone
 * on backtracking. A change of a variable wakes up the propagators watching it.
 */
final class DomainStore {
  /**
   * Variables whose initial span is at most this number of values get a bitset. Tests lower it to
   * exercise the bounds-only representation.
   */
  static long bitsetLimit = 4096;

  private static final int KIND_BOUNDS = 0;
  private static final int KIND_WORD = 1;
  private static final int KIND_REV = 2;

  int numVars;
  long[] lb = new long[16];
  long[] ub = new long[16];
  private long[] base = new long[16];
  private long[][] bits = new long[16][];
  private long[] size = new long[16];
  private Domain[] holes = new Domain[16];
  private long[] stampOfVar = new long[16];

  // trail
  private int[] tKind = new int[1024];
  private int[] tVar = new int[1024];
  private long[] tA = new long[1024];
  private long[] tB = new long[1024];
  private long[] tC = new long[1024];
  private int trailSize;
  private long stamp = 1;
  private long stampCounter = 1;

  // reversible integers of the propagators
  private long[] rev = new long[64];
  private int numRev;

  // propagators and queue
  final List<Propagator> propagators = new ArrayList<>();
  private int[][] watchers;
  private int[] queue0 = new int[64];
  private int[] queue1 = new int[64];
  private int head0, tail0, head1, tail1, count0, count1;
  private boolean[] inQueue = new boolean[0];
  private Propagator running;
  /** The propagator which failed last, or null. */
  Propagator lastFailure;

  /** Per variable accumulated weight of failed propagators (dom/wdeg heuristic). */
  double[] varWeight;

  long numPropagations;
  /** Polled during long propagation loops; returns true when the search must stop. */
  StopCheck stopCheck;

  interface StopCheck {
    boolean shouldStop();
  }

  /** Thrown from the propagation loop when the stop check fires. */
  static final class StopException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    StopException() {
      super(null, null, false, false);
    }
  }

  // ---------------------------------------------------------------------------------- variables

  /** Creates a variable with the given (non empty) domain and returns its index. */
  int newVar(Domain domain) {
    int v = numVars++;
    if (v == lb.length) {
      int n = lb.length * 2;
      lb = Arrays.copyOf(lb, n);
      ub = Arrays.copyOf(ub, n);
      base = Arrays.copyOf(base, n);
      bits = Arrays.copyOf(bits, n);
      size = Arrays.copyOf(size, n);
      holes = Arrays.copyOf(holes, n);
      stampOfVar = Arrays.copyOf(stampOfVar, n);
    }
    long min = domain.min();
    long max = domain.max();
    lb[v] = min;
    ub[v] = max;
    base[v] = min;
    long span = max - min; // fits: validated domains
    if (span >= 0 && span < bitsetLimit) {
      int numWords = (int) (span / 64) + 1;
      long[] b = new long[numWords];
      long count = 0;
      for (int i = 0; i < domain.numIntervals(); i++) {
        for (long x = domain.intervalStart(i); x <= domain.intervalEnd(i); x++) {
          long off = x - min;
          b[(int) (off >>> 6)] |= 1L << (off & 63);
          count++;
        }
      }
      bits[v] = b;
      size[v] = count;
      holes[v] = null;
    } else {
      bits[v] = null;
      holes[v] = domain.numIntervals() > 1 ? domain : null;
      size[v] = domain.size();
    }
    stampOfVar[v] = 0;
    return v;
  }

  boolean isFixed(int v) {
    return lb[v] == ub[v];
  }

  long value(int v) {
    return lb[v];
  }

  /** Number of values of the domain (an upper bound for variables without bitset). */
  long size(int v) {
    if (bits[v] != null) {
      return size[v];
    }
    return ub[v] - lb[v] + 1;
  }

  /** Returns true if the variable keeps an exact representation of its domain. */
  boolean hasBitset(int v) {
    return bits[v] != null;
  }

  boolean contains(int v, long x) {
    if (x < lb[v] || x > ub[v]) {
      return false;
    }
    long[] b = bits[v];
    if (b != null) {
      long off = x - base[v];
      return (b[(int) (off >>> 6)] & (1L << (off & 63))) != 0;
    }
    Domain h = holes[v];
    return h == null || h.contains(x);
  }

  /** Smallest value of the domain which is &gt;= x, or Long.MAX_VALUE if there is none. */
  long nextValueAtOrAfter(int v, long x) {
    if (x <= lb[v]) {
      return lb[v];
    }
    if (x > ub[v]) {
      return Long.MAX_VALUE;
    }
    long r = snapUp(v, x);
    return r > ub[v] ? Long.MAX_VALUE : r;
  }

  /** Largest value of the domain which is &lt;= x, or Long.MIN_VALUE if there is none. */
  long prevValueAtOrBefore(int v, long x) {
    if (x >= ub[v]) {
      return ub[v];
    }
    if (x < lb[v]) {
      return Long.MIN_VALUE;
    }
    long r = snapDown(v, x);
    return r < lb[v] ? Long.MIN_VALUE : r;
  }

  // smallest domain value >= x ignoring the current upper bound (x within [lb, ub])
  private long snapUp(int v, long x) {
    long[] b = bits[v];
    if (b != null) {
      long off = x - base[v];
      int w = (int) (off >>> 6);
      long word = b[w] & (-1L << (off & 63));
      while (true) {
        if (word != 0) {
          return base[v] + ((long) w << 6) + Long.numberOfTrailingZeros(word);
        }
        w++;
        if (w >= b.length) {
          return Long.MAX_VALUE;
        }
        word = b[w];
      }
    }
    Domain h = holes[v];
    if (h == null) {
      return x;
    }
    long r = h.valueAtOrAfter(x);
    return r < x ? Long.MAX_VALUE : (h.contains(r) ? r : Long.MAX_VALUE);
  }

  private long snapDown(int v, long x) {
    long[] b = bits[v];
    if (b != null) {
      long off = x - base[v];
      int w = (int) (off >>> 6);
      int bit = (int) (off & 63);
      long word = b[w] & (bit == 63 ? -1L : ((1L << (bit + 1)) - 1));
      while (true) {
        if (word != 0) {
          return base[v] + ((long) w << 6) + 63 - Long.numberOfLeadingZeros(word);
        }
        w--;
        if (w < 0) {
          return Long.MIN_VALUE;
        }
        word = b[w];
      }
    }
    Domain h = holes[v];
    if (h == null) {
      return x;
    }
    long r = h.valueAtOrBefore(x);
    return r > x ? Long.MIN_VALUE : (h.contains(r) ? r : Long.MIN_VALUE);
  }

  // number of values of the bitset in [from, to]
  private long countBits(int v, long from, long to) {
    if (from > to) {
      return 0;
    }
    long[] b = bits[v];
    long o1 = from - base[v];
    long o2 = to - base[v];
    int w1 = (int) (o1 >>> 6);
    int w2 = (int) (o2 >>> 6);
    long count = 0;
    for (int w = w1; w <= w2; w++) {
      long word = b[w];
      if (w == w1) {
        word &= -1L << (o1 & 63);
      }
      if (w == w2) {
        int bit = (int) (o2 & 63);
        word &= bit == 63 ? -1L : ((1L << (bit + 1)) - 1);
      }
      count += Long.bitCount(word);
    }
    return count;
  }

  private void saveBounds(int v) {
    if (stampOfVar[v] != stamp) {
      stampOfVar[v] = stamp;
      push(KIND_BOUNDS, v, lb[v], ub[v], size[v]);
    }
  }

  private void push(int kind, int v, long a, long b, long c) {
    if (trailSize == tKind.length) {
      int n = tKind.length * 2;
      tKind = Arrays.copyOf(tKind, n);
      tVar = Arrays.copyOf(tVar, n);
      tA = Arrays.copyOf(tA, n);
      tB = Arrays.copyOf(tB, n);
      tC = Arrays.copyOf(tC, n);
    }
    tKind[trailSize] = kind;
    tVar[trailSize] = v;
    tA[trailSize] = a;
    tB[trailSize] = b;
    tC[trailSize] = c;
    trailSize++;
  }

  /** Sets {@code v >= x}; returns false if the domain becomes empty. */
  boolean setLb(int v, long x) {
    if (x <= lb[v]) {
      return true;
    }
    if (x > ub[v]) {
      return fail();
    }
    long nx = snapUp(v, x);
    if (nx > ub[v]) {
      return fail();
    }
    saveBounds(v);
    if (bits[v] != null) {
      size[v] -= countBits(v, lb[v], nx - 1);
    }
    lb[v] = nx;
    notifyChange(v);
    return true;
  }

  /** Sets {@code v <= x}; returns false if the domain becomes empty. */
  boolean setUb(int v, long x) {
    if (x >= ub[v]) {
      return true;
    }
    if (x < lb[v]) {
      return fail();
    }
    long nx = snapDown(v, x);
    if (nx < lb[v]) {
      return fail();
    }
    saveBounds(v);
    if (bits[v] != null) {
      size[v] -= countBits(v, nx + 1, ub[v]);
    }
    ub[v] = nx;
    notifyChange(v);
    return true;
  }

  /** Sets {@code v == x}; returns false if x is not in the domain. */
  boolean fix(int v, long x) {
    if (!contains(v, x)) {
      return fail();
    }
    if (lb[v] == x && ub[v] == x) {
      return true;
    }
    saveBounds(v);
    lb[v] = x;
    ub[v] = x;
    if (bits[v] != null) {
      size[v] = 1;
    }
    notifyChange(v);
    return true;
  }

  /**
   * Removes x from the domain of v. For variables without bitset only the bounds can be removed,
   * an interior value is silently kept.
   */
  boolean remove(int v, long x) {
    if (x < lb[v] || x > ub[v]) {
      return true;
    }
    if (x == lb[v]) {
      return setLb(v, x + 1);
    }
    if (x == ub[v]) {
      return setUb(v, x - 1);
    }
    long[] b = bits[v];
    if (b == null) {
      return true;
    }
    long off = x - base[v];
    int w = (int) (off >>> 6);
    long mask = 1L << (off & 63);
    if ((b[w] & mask) == 0) {
      return true;
    }
    saveBounds(v);
    push(KIND_WORD, v, w, b[w], 0);
    b[w] &= ~mask;
    size[v]--;
    notifyChange(v);
    return true;
  }

  /** Restricts v to the values of the given domain. */
  boolean intersect(int v, Domain d) {
    if (d.isEmpty()) {
      return fail();
    }
    if (!setLb(v, d.min()) || !setUb(v, d.max())) {
      return false;
    }
    if (d.numIntervals() > 1) {
      if (bits[v] != null) {
        for (int i = 0; i + 1 < d.numIntervals(); i++) {
          long from = d.intervalEnd(i) + 1;
          long to = d.intervalStart(i + 1) - 1;
          for (long x = Math.max(from, lb[v]); x <= Math.min(to, ub[v]); x++) {
            if (!remove(v, x)) {
              return false;
            }
          }
        }
      } else {
        // keep the holes of the intersection with the initial domain
        Domain current = holes[v] == null ? new Domain(lb[v], ub[v])
            : holes[v].intersectionWith(new Domain(lb[v], ub[v]));
        Domain inter = current.intersectionWith(d);
        if (inter.isEmpty()) {
          return fail();
        }
        if (!setLb(v, inter.min()) || !setUb(v, inter.max())) {
          return false;
        }
        // the holes of a variable without bitset are part of its initial domain only; a static
        // replacement is safe at the root level
        if (trailSize == 0 || isRootLevel) {
          holes[v] = inter.numIntervals() > 1 ? inter : null;
        }
      }
    }
    return true;
  }

  /** Returns the current domain of v. */
  Domain domain(int v) {
    long[] b = bits[v];
    if (b == null) {
      Domain d = new Domain(lb[v], ub[v]);
      return holes[v] == null ? d : holes[v].intersectionWith(d);
    }
    List<Long> flat = new ArrayList<>();
    long x = lb[v];
    while (x <= ub[v]) {
      long start = x;
      long end = x;
      while (end + 1 <= ub[v] && contains(v, end + 1)) {
        end++;
      }
      flat.add(start);
      flat.add(end);
      long nx = nextValueAtOrAfter(v, end + 1);
      if (nx == Long.MAX_VALUE) {
        break;
      }
      x = nx;
    }
    long[] f = new long[flat.size()];
    for (int i = 0; i < f.length; i++) {
      f[i] = flat.get(i);
    }
    return Domain.fromFlatIntervals(f);
  }

  // ---------------------------------------------------------------------------------- literals

  static int litVar(int lit) {
    return lit >= 0 ? lit : -lit - 1;
  }

  boolean litTrue(int lit) {
    if (lit >= 0) {
      return lb[lit] == 1;
    }
    return ub[-lit - 1] == 0;
  }

  boolean litFalse(int lit) {
    if (lit >= 0) {
      return ub[lit] == 0;
    }
    return lb[-lit - 1] == 1;
  }

  boolean litFixed(int lit) {
    int v = litVar(lit);
    return lb[v] == ub[v];
  }

  /** Makes the literal true (value true) or false. */
  boolean setLit(int lit, boolean value) {
    if (lit >= 0) {
      return value ? setLb(lit, 1) : setUb(lit, 0);
    }
    int v = -lit - 1;
    return value ? setUb(v, 0) : setLb(v, 1);
  }

  // ---------------------------------------------------------------------------------- trail

  boolean isRootLevel = true;

  /** Returns the current trail position, to be passed to {@link #restore(int)}. */
  int trailPosition() {
    return trailSize;
  }

  /** Starts a new group of changes which can be undone with {@link #restore(int)}. */
  void newStamp() {
    stamp = ++stampCounter;
  }

  /** Undoes all changes recorded after the given trail position. */
  void restore(int position) {
    while (trailSize > position) {
      trailSize--;
      int v = tVar[trailSize];
      switch (tKind[trailSize]) {
        case KIND_BOUNDS:
          lb[v] = tA[trailSize];
          ub[v] = tB[trailSize];
          size[v] = tC[trailSize];
          break;
        case KIND_WORD:
          bits[v][(int) tA[trailSize]] = tB[trailSize];
          break;
        default:
          rev[v] = tA[trailSize];
          break;
      }
    }
    newStamp();
  }

  /** Allocates a reversible long and returns its handle. */
  int newRev(long initial) {
    if (numRev == rev.length) {
      rev = Arrays.copyOf(rev, rev.length * 2);
    }
    rev[numRev] = initial;
    return numRev++;
  }

  long getRev(int handle) {
    return rev[handle];
  }

  void setRev(int handle, long value) {
    if (rev[handle] != value) {
      push(KIND_REV, handle, rev[handle], 0, 0);
      rev[handle] = value;
    }
  }

  // ---------------------------------------------------------------------------------- propagation

  /** Adds a propagator; must be called before {@link #finishLoading()}. */
  void addPropagator(Propagator p) {
    p.id = propagators.size();
    propagators.add(p);
  }

  /** Builds the watch lists; all propagators are queued for the first propagation. */
  void finishLoading() {
    int[] counts = new int[numVars];
    for (Propagator p : propagators) {
      for (int v : p.watchedVars()) {
        counts[v]++;
      }
    }
    watchers = new int[numVars][];
    for (int v = 0; v < numVars; v++) {
      watchers[v] = new int[counts[v]];
    }
    int[] fill = new int[numVars];
    for (Propagator p : propagators) {
      int[] vs = p.watchedVars();
      // avoid duplicate entries for a variable used twice by the same propagator
      for (int i = 0; i < vs.length; i++) {
        int v = vs[i];
        boolean dup = false;
        int[] w = watchers[v];
        for (int k = 0; k < fill[v]; k++) {
          if (w[k] == p.id) {
            dup = true;
            break;
          }
        }
        if (!dup) {
          w[fill[v]++] = p.id;
        }
      }
    }
    for (int v = 0; v < numVars; v++) {
      if (fill[v] != watchers[v].length) {
        watchers[v] = Arrays.copyOf(watchers[v], fill[v]);
      }
    }
    inQueue = new boolean[propagators.size()];
    varWeight = new double[numVars];
    for (int v = 0; v < numVars; v++) {
      varWeight[v] = watchers[v].length;
    }
    queueAll();
  }

  /** Puts all propagators in the queue. */
  void queueAll() {
    for (Propagator p : propagators) {
      enqueue(p);
    }
  }

  int numWatchers(int v) {
    return watchers[v].length;
  }

  private void notifyChange(int v) {
    if (watchers == null) {
      return; // still loading: all propagators are queued by finishLoading()
    }
    int[] w = watchers[v];
    for (int i = 0; i < w.length; i++) {
      Propagator p = propagators.get(w[i]);
      if (p != running) {
        enqueue(p);
      }
    }
  }

  void enqueue(Propagator p) {
    if (inQueue[p.id]) {
      return;
    }
    inQueue[p.id] = true;
    if (p.priority == 0) {
      if (count0 == queue0.length) {
        queue0 = grow(queue0, head0, count0);
        head0 = 0;
        tail0 = count0;
      }
      queue0[tail0] = p.id;
      tail0 = (tail0 + 1) % queue0.length;
      count0++;
    } else {
      if (count1 == queue1.length) {
        queue1 = grow(queue1, head1, count1);
        head1 = 0;
        tail1 = count1;
      }
      queue1[tail1] = p.id;
      tail1 = (tail1 + 1) % queue1.length;
      count1++;
    }
  }

  private static int[] grow(int[] q, int head, int count) {
    int[] n = new int[q.length * 2];
    for (int i = 0; i < count; i++) {
      n[i] = q[(head + i) % q.length];
    }
    return n;
  }

  private void clearQueue() {
    while (count0 > 0) {
      inQueue[queue0[head0]] = false;
      head0 = (head0 + 1) % queue0.length;
      count0--;
    }
    while (count1 > 0) {
      inQueue[queue1[head1]] = false;
      head1 = (head1 + 1) % queue1.length;
      count1--;
    }
    head0 = tail0 = head1 = tail1 = 0;
  }

  /**
   * Runs the queued propagators until the fixpoint. Returns false on a failure (the queue is then
   * empty and {@link #lastFailure} is set).
   */
  boolean propagate() {
    lastFailure = null;
    while (count0 > 0 || count1 > 0) {
      int id;
      if (count0 > 0) {
        id = queue0[head0];
        head0 = (head0 + 1) % queue0.length;
        count0--;
      } else {
        id = queue1[head1];
        head1 = (head1 + 1) % queue1.length;
        count1--;
      }
      inQueue[id] = false;
      Propagator p = propagators.get(id);
      running = p;
      boolean ok;
      try {
        ok = p.propagate(this);
      } finally {
        running = null;
      }
      numPropagations++;
      if (!ok) {
        lastFailure = p;
        p.weight++;
        for (int v : p.watchedVars()) {
          varWeight[v] += 1;
        }
        clearQueue();
        return false;
      }
      if ((numPropagations & 1023) == 0 && stopCheck != null && stopCheck.shouldStop()) {
        clearQueue();
        throw new StopException();
      }
    }
    return true;
  }

  /** Clears the queue without propagating (used before restoring the trail). */
  void resetQueue() {
    clearQueue();
  }

  private boolean fail() {
    return false;
  }

  // ---------------------------------------------------------------------------------- affine views

  /** Minimum of {@code coeff * v + offset}; v &lt; 0 means the constant offset. */
  long affMin(int v, long coeff, long offset) {
    if (v < 0) {
      return offset;
    }
    return coeff >= 0 ? coeff * lb[v] + offset : coeff * ub[v] + offset;
  }

  /** Maximum of {@code coeff * v + offset}. */
  long affMax(int v, long coeff, long offset) {
    if (v < 0) {
      return offset;
    }
    return coeff >= 0 ? coeff * ub[v] + offset : coeff * lb[v] + offset;
  }

  /** Enforces {@code coeff * v + offset >= x}. */
  boolean affSetMin(int v, long coeff, long offset, long x) {
    if (v < 0 || coeff == 0) {
      return offset >= x || fail();
    }
    if (x <= affMin(v, coeff, offset)) {
      return true;
    }
    if (x > affMax(v, coeff, offset)) {
      return fail();
    }
    long rhs = x - offset; // coeff * v >= rhs
    if (coeff > 0) {
      return setLb(v, ceilDiv(rhs, coeff));
    }
    return setUb(v, floorDiv(rhs, coeff));
  }

  /** Enforces {@code coeff * v + offset <= x}. */
  boolean affSetMax(int v, long coeff, long offset, long x) {
    if (v < 0 || coeff == 0) {
      return offset <= x || fail();
    }
    if (x >= affMax(v, coeff, offset)) {
      return true;
    }
    if (x < affMin(v, coeff, offset)) {
      return fail();
    }
    long rhs = x - offset; // coeff * v <= rhs
    if (coeff > 0) {
      return setUb(v, floorDiv(rhs, coeff));
    }
    return setLb(v, ceilDiv(rhs, coeff));
  }

  /** Returns true if {@code coeff * v + offset} can take the value x. */
  boolean affContains(int v, long coeff, long offset, long x) {
    if (v < 0 || coeff == 0) {
      return x == offset;
    }
    long d = x - offset;
    if (d % coeff != 0) {
      return false;
    }
    return contains(v, d / coeff);
  }

  /** Removes the value x from {@code coeff * v + offset}. */
  boolean affRemove(int v, long coeff, long offset, long x) {
    if (v < 0 || coeff == 0) {
      return x != offset || fail();
    }
    long d = x - offset;
    if (d % coeff != 0) {
      return true;
    }
    return remove(v, d / coeff);
  }

  /** Fixes {@code coeff * v + offset} to x. */
  boolean affFix(int v, long coeff, long offset, long x) {
    if (v < 0 || coeff == 0) {
      return x == offset || fail();
    }
    long d = x - offset;
    if (d % coeff != 0) {
      return fail();
    }
    return fix(v, d / coeff);
  }

  static long floorDiv(long a, long b) {
    return Math.floorDiv(a, b);
  }

  static long ceilDiv(long a, long b) {
    return -Math.floorDiv(-a, b);
  }
}
