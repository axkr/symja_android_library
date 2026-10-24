package org.matheclipse.ortools.constraintsolver.cp;

/**
 * An integer variable of the {@link CpStore}: a domain given by its bounds and, once a value
 * strictly between the bounds has been removed, a bitmap of the remaining values.
 *
 * <p>
 * Part of the simplified engine behind the OR-Tools constraint solver API of this module. All
 * modifications are recorded on the trail of the store, so that they are undone when the store
 * backtracks. Modifications which empty the domain throw {@link CpFail}.
 */
public final class CpVar {

  /** Domains wider than this many values can't get interior holes. */
  static final long MAX_BITMAP_RANGE = 1L << 24;

  private static final Propagator[] NO_WATCHERS = new Propagator[0];

  final CpStore store;

  /** The index of the variable in its store. */
  public final int id;

  long min;

  long max;

  /** The bounds at creation; the bitmap covers this range. */
  final long initialMin;

  final long initialMax;

  /** Presence bitmap of the values <code>initialMin + k</code>; <code>null</code> = no holes. */
  long[] words;

  /** Counter of the store at the last trailing of the bounds. */
  long stamp = -1L;

  Propagator[] watchers = NO_WATCHERS;

  int watcherCount;

  /** An optional name, used by <code>toString()</code> of the API objects. */
  public String name;

  /** Arbitrary data attached by the API layer (the owning <code>IntVar</code>). */
  public Object owner;

  CpVar(CpStore store, int id, long min, long max, String name) {
    this.store = store;
    this.id = id;
    this.min = min;
    this.max = max;
    this.initialMin = min;
    this.initialMax = max;
    this.name = name;
  }

  /** The store this variable belongs to. */
  public CpStore store() {
    return store;
  }

  /** The smallest value of the domain. */
  public long min() {
    return min;
  }

  /** The largest value of the domain. */
  public long max() {
    return max;
  }

  /** Whether the domain is a single value. */
  public boolean isBound() {
    return min == max;
  }

  /** The value of a bound variable (the minimum otherwise). */
  public long value() {
    return min;
  }

  /** Whether the domain has holes between its bounds. */
  public boolean hasHoles() {
    return words != null && size() != max - min + 1;
  }

  /** Whether <code>v</code> belongs to the domain. */
  public boolean contains(long v) {
    if (v < min || v > max) {
      return false;
    }
    if (words == null) {
      return true;
    }
    return bit(v);
  }

  private boolean bit(long v) {
    long k = v - initialMin;
    return (words[(int) (k >>> 6)] & (1L << (k & 63))) != 0;
  }

  /** The number of values of the domain (saturated at <code>Long.MAX_VALUE</code>). */
  public long size() {
    if (words == null) {
      long d = max - min;
      if (d < 0 || d == Long.MAX_VALUE) {
        return Long.MAX_VALUE;
      }
      return d + 1;
    }
    long from = min - initialMin;
    long to = max - initialMin;
    int wFrom = (int) (from >>> 6);
    int wTo = (int) (to >>> 6);
    long count = 0;
    for (int w = wFrom; w <= wTo; w++) {
      long word = words[w];
      if (w == wFrom) {
        word &= -1L << (from & 63);
      }
      if (w == wTo) {
        int top = (int) (to & 63);
        if (top < 63) {
          word &= (1L << (top + 1)) - 1;
        }
      }
      count += Long.bitCount(word);
    }
    return count;
  }

  /** The smallest value of the domain which is at least <code>v</code>, or MAX_VALUE if none. */
  public long nextValue(long v) {
    if (v <= min) {
      return min;
    }
    if (v > max) {
      return Long.MAX_VALUE;
    }
    if (words == null) {
      return v;
    }
    for (long x = v; x <= max; x++) {
      long k = x - initialMin;
      long word = words[(int) (k >>> 6)] >>> (k & 63);
      if (word != 0) {
        long candidate = x + Long.numberOfTrailingZeros(word);
        return candidate <= max ? candidate : Long.MAX_VALUE;
      }
      // skip to the next word
      x += 63 - (k & 63);
    }
    return Long.MAX_VALUE;
  }

  /** The largest value of the domain which is at most <code>v</code>, or MIN_VALUE if none. */
  public long previousValue(long v) {
    if (v >= max) {
      return max;
    }
    if (v < min) {
      return Long.MIN_VALUE;
    }
    if (words == null) {
      return v;
    }
    for (long x = v; x >= min; x--) {
      if (bit(x)) {
        return x;
      }
    }
    return Long.MIN_VALUE;
  }

  /** The values of the domain in increasing order; the domain must be reasonably small. */
  public long[] values() {
    long n = size();
    if (n > Integer.MAX_VALUE - 8) {
      throw new IllegalStateException("domain too large to enumerate");
    }
    long[] result = new long[(int) n];
    int k = 0;
    for (long v = min; v <= max && k < result.length; v++) {
      if (words == null || bit(v)) {
        result[k++] = v;
      }
      if (v == Long.MAX_VALUE) {
        break;
      }
    }
    return result;
  }

  private void saveBounds() {
    if (store.level > 0 && stamp != store.stamp) {
      store.trailBounds(this);
      stamp = store.stamp;
    }
  }

  /** Removes the values below <code>m</code>. */
  public void setMin(long m) {
    if (m <= min) {
      return;
    }
    if (m > max) {
      throw CpFail.INSTANCE;
    }
    long nm = m;
    if (words != null) {
      nm = nextValue(m);
      if (nm > max) {
        throw CpFail.INSTANCE;
      }
    }
    saveBounds();
    min = nm;
    store.changed(this);
  }

  /** Removes the values above <code>m</code>. */
  public void setMax(long m) {
    if (m >= max) {
      return;
    }
    if (m < min) {
      throw CpFail.INSTANCE;
    }
    long nm = m;
    if (words != null) {
      nm = previousValue(m);
      if (nm < min) {
        throw CpFail.INSTANCE;
      }
    }
    saveBounds();
    max = nm;
    store.changed(this);
  }

  /** Restricts the domain to <code>[l, u]</code>. */
  public void setRange(long l, long u) {
    if (l > u) {
      throw CpFail.INSTANCE;
    }
    if (l > max || u < min) {
      throw CpFail.INSTANCE;
    }
    if (l <= min && u >= max) {
      return;
    }
    long nl = Math.max(l, min);
    long nu = Math.min(u, max);
    if (words != null) {
      nl = nextValue(nl);
      if (nl > nu) {
        throw CpFail.INSTANCE;
      }
      nu = previousValue(nu);
      if (nu < nl) {
        throw CpFail.INSTANCE;
      }
    }
    saveBounds();
    min = nl;
    max = nu;
    store.changed(this);
  }

  /** Binds the variable to <code>v</code>. */
  public void setValue(long v) {
    if (!contains(v)) {
      throw CpFail.INSTANCE;
    }
    if (min == v && max == v) {
      return;
    }
    saveBounds();
    min = v;
    max = v;
    store.changed(this);
  }

  /** Removes the value <code>v</code>. */
  public void removeValue(long v) {
    if (v < min || v > max) {
      return;
    }
    if (v == min) {
      if (v == max) {
        throw CpFail.INSTANCE;
      }
      setMin(v + 1);
      return;
    }
    if (v == max) {
      setMax(v - 1);
      return;
    }
    ensureWords();
    if (!bit(v)) {
      return;
    }
    clearBit(v);
    store.changed(this);
  }

  /** Removes all values of <code>[l, u]</code>. */
  public void removeInterval(long l, long u) {
    if (l > u || u < min || l > max) {
      return;
    }
    if (l <= min) {
      if (u >= max) {
        throw CpFail.INSTANCE;
      }
      setMin(u + 1);
      return;
    }
    if (u >= max) {
      setMax(l - 1);
      return;
    }
    ensureWords();
    boolean changed = false;
    for (long v = l; v <= u; v++) {
      if (bit(v)) {
        clearBit(v);
        changed = true;
      }
    }
    if (changed) {
      store.changed(this);
    }
  }

  private void clearBit(long v) {
    long k = v - initialMin;
    words[(int) (k >>> 6)] &= ~(1L << (k & 63));
    if (store.level > 0) {
      store.trailBit(this, v);
    }
  }

  void restoreBit(long v) {
    long k = v - initialMin;
    words[(int) (k >>> 6)] |= 1L << (k & 63);
  }

  private void ensureWords() {
    if (words != null) {
      return;
    }
    long range = initialMax - initialMin;
    if (range < 0 || range >= MAX_BITMAP_RANGE) {
      throw new UnsupportedOperationException(
          "removing values inside a domain wider than 2^24 values is not supported");
    }
    int n = (int) ((range >>> 6) + 1);
    long[] w = new long[n];
    java.util.Arrays.fill(w, -1L);
    words = w;
  }

  void addWatcher(Propagator p) {
    for (int i = 0; i < watcherCount; i++) {
      if (watchers[i] == p) {
        return;
      }
    }
    if (watcherCount == watchers.length) {
      watchers = java.util.Arrays.copyOf(watchers, Math.max(4, watcherCount * 2));
    }
    watchers[watcherCount++] = p;
  }

  void removeWatcher(Propagator p) {
    for (int i = watcherCount - 1; i >= 0; i--) {
      if (watchers[i] == p) {
        System.arraycopy(watchers, i + 1, watchers, i, watcherCount - i - 1);
        watchers[--watcherCount] = null;
        return;
      }
    }
  }

  /** The domain in the format of OR-Tools <code>DebugString</code>, e.g. <code>3..5</code>. */
  public String domainString() {
    if (min == max) {
      return Long.toString(min);
    }
    if (words == null || !hasHoles()) {
      return min + ".." + max;
    }
    StringBuilder buf = new StringBuilder();
    long v = min;
    boolean first = true;
    while (v <= max) {
      long start = nextValue(v);
      if (start == Long.MAX_VALUE) {
        break;
      }
      long end = start;
      while (end < max && contains(end + 1)) {
        end++;
      }
      if (!first) {
        buf.append(' ');
      }
      first = false;
      if (end == start) {
        buf.append(start);
      } else if (end == start + 1) {
        buf.append(start).append(' ').append(end);
      } else {
        buf.append(start).append("..").append(end);
      }
      if (end == Long.MAX_VALUE) {
        break;
      }
      v = end + 1;
    }
    return buf.toString();
  }

  @Override
  public String toString() {
    return (name == null ? "Var" + id : name) + "(" + domainString() + ")";
  }
}
