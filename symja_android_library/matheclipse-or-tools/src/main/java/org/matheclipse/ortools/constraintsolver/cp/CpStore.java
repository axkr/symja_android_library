package org.matheclipse.ortools.constraintsolver.cp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The variable store, trail and propagation queue of the small constraint engine which backs the
 * OR-Tools <code>Solver</code> facade of this module.
 *
 * <p>
 * This is a deliberately simple design which reimplements the behaviour of the OR-Tools original
 * CP solver: integer variables with bound and hole domains ({@link CpVar}), propagators which are
 * woken up when a variable they watch changes ({@link Propagator}), and a trail which records the
 * modifications so that {@link #popLevel()} restores the state of the matching
 * {@link #pushLevel()}. Propagators posted after a {@link #pushLevel()} are removed again by the
 * matching {@link #popLevel()}, which allows temporary constraints.
 */
public final class CpStore {

  private static final int TRAIL_BOUNDS = 0;

  private static final int TRAIL_BIT = 1;

  private final ArrayList<CpVar> vars = new ArrayList<>();

  private final ArrayList<Propagator> propagators = new ArrayList<>();

  // trail
  private CpVar[] trailVar = new CpVar[256];

  private long[] trailA = new long[256];

  private long[] trailB = new long[256];

  private byte[] trailType = new byte[256];

  private int trailSize;

  /** The current choice level; 0 is the root, where nothing is trailed. */
  int level;

  /** Incremented on every push and pop, used to trail each variable once per period. */
  long stamp;

  private int[] levelTrail = new int[64];

  private int[] levelPropagators = new int[64];

  // propagation queue (ring buffer)
  private Propagator[] queue = new Propagator[64];

  private int queueHead;

  private int queueSize;

  private Propagator running;

  /** Number of failures raised by {@link #propagate()} and searches. */
  public long failures;

  /** Number of propagator executions. */
  public long propagations;

  /** Creates an empty store. */
  public CpStore() {}

  /** Creates a new variable with domain <code>[min, max]</code>. */
  public CpVar newVar(long min, long max, String name) {
    if (min > max) {
      throw new IllegalArgumentException("empty domain [" + min + ", " + max + "]");
    }
    CpVar v = new CpVar(this, vars.size(), min, max, name);
    vars.add(v);
    return v;
  }

  /** Creates a new variable with the given values. */
  public CpVar newVar(long[] values, String name) {
    if (values.length == 0) {
      throw new IllegalArgumentException("empty domain");
    }
    long[] sorted = values.clone();
    Arrays.sort(sorted);
    CpVar v = newVar(sorted[0], sorted[sorted.length - 1], name);
    int k = 0;
    for (long x = sorted[0]; x <= sorted[sorted.length - 1]; x++) {
      while (k < sorted.length && sorted[k] < x) {
        k++;
      }
      if (k >= sorted.length || sorted[k] != x) {
        v.removeValue(x);
      }
    }
    return v;
  }

  /** A variable bound to <code>value</code>. */
  public CpVar newConstant(long value) {
    return newVar(value, value, Long.toString(value));
  }

  /** All variables of the store, in creation order. */
  public List<CpVar> variables() {
    return vars;
  }

  /** All propagators currently posted, in posting order. */
  public List<Propagator> propagators() {
    return propagators;
  }

  /** The number of posted propagators. */
  public int propagatorCount() {
    return propagators.size();
  }

  /** The current choice level (0 = root). */
  public int level() {
    return level;
  }

  /**
   * Posts a propagator: registers it on its variables and queues it. The propagator is executed
   * by the next {@link #propagate()}.
   */
  public void post(Propagator p) {
    p.index = propagators.size();
    propagators.add(p);
    for (CpVar v : p.vars) {
      if (v.store != this) {
        throw new IllegalArgumentException("variable of another solver");
      }
      v.addWatcher(p);
    }
    enqueue(p);
  }

  /** Queues all posted propagators. */
  public void enqueueAll() {
    for (Propagator p : propagators) {
      enqueue(p);
    }
  }

  void enqueue(Propagator p) {
    if (p.inQueue) {
      return;
    }
    p.inQueue = true;
    if (queueSize == queue.length) {
      Propagator[] q = new Propagator[queue.length * 2];
      for (int i = 0; i < queueSize; i++) {
        q[i] = queue[(queueHead + i) % queue.length];
      }
      queue = q;
      queueHead = 0;
    }
    queue[(queueHead + queueSize) % queue.length] = p;
    queueSize++;
  }

  /** Empties the propagation queue without running the queued propagators. */
  public void clearQueue() {
    while (queueSize > 0) {
      Propagator p = queue[queueHead];
      queue[queueHead] = null;
      queueHead = (queueHead + 1) % queue.length;
      queueSize--;
      p.inQueue = false;
    }
    queueHead = 0;
  }

  void changed(CpVar v) {
    Propagator[] w = v.watchers;
    for (int i = 0; i < v.watcherCount; i++) {
      Propagator p = w[i];
      if (p != running) {
        enqueue(p);
      }
    }
  }

  /**
   * Runs the queued propagators until the fixpoint.
   *
   * @throws CpFail if a domain becomes empty; the queue is emptied in that case
   */
  public void propagate() {
    try {
      while (queueSize > 0) {
        Propagator p = queue[queueHead];
        queue[queueHead] = null;
        queueHead = (queueHead + 1) % queue.length;
        queueSize--;
        p.inQueue = false;
        running = p;
        propagations++;
        p.propagate();
        running = null;
      }
    } catch (CpFail fail) {
      running = null;
      clearQueue();
      failures++;
      throw fail;
    } catch (RuntimeException e) {
      running = null;
      clearQueue();
      throw e;
    }
  }

  void trailBounds(CpVar v) {
    ensureTrail();
    trailVar[trailSize] = v;
    trailA[trailSize] = v.min;
    trailB[trailSize] = v.max;
    trailType[trailSize] = TRAIL_BOUNDS;
    trailSize++;
  }

  void trailBit(CpVar v, long value) {
    ensureTrail();
    trailVar[trailSize] = v;
    trailA[trailSize] = value;
    trailType[trailSize] = TRAIL_BIT;
    trailSize++;
  }

  private void ensureTrail() {
    if (trailSize == trailVar.length) {
      int n = trailVar.length * 2;
      trailVar = Arrays.copyOf(trailVar, n);
      trailA = Arrays.copyOf(trailA, n);
      trailB = Arrays.copyOf(trailB, n);
      trailType = Arrays.copyOf(trailType, n);
    }
  }

  /** Opens a new choice level; its modifications are undone by the matching pop. */
  public void pushLevel() {
    if (level + 1 >= levelTrail.length) {
      levelTrail = Arrays.copyOf(levelTrail, levelTrail.length * 2);
      levelPropagators = Arrays.copyOf(levelPropagators, levelPropagators.length * 2);
    }
    level++;
    levelTrail[level] = trailSize;
    levelPropagators[level] = propagators.size();
    stamp++;
  }

  /** Undoes all modifications since the matching {@link #pushLevel()}. */
  public void popLevel() {
    if (level == 0) {
      throw new IllegalStateException("no level to pop");
    }
    clearQueue();
    int target = levelTrail[level];
    for (int i = trailSize - 1; i >= target; i--) {
      CpVar v = trailVar[i];
      if (trailType[i] == TRAIL_BOUNDS) {
        v.min = trailA[i];
        v.max = trailB[i];
      } else {
        v.restoreBit(trailA[i]);
      }
      trailVar[i] = null;
    }
    trailSize = target;
    int props = levelPropagators[level];
    for (int i = propagators.size() - 1; i >= props; i--) {
      Propagator p = propagators.remove(i);
      for (CpVar v : p.vars) {
        v.removeWatcher(p);
      }
    }
    level--;
    stamp++;
  }

  /** Pops levels until the current level is <code>target</code>. */
  public void popToLevel(int target) {
    while (level > target) {
      popLevel();
    }
  }

  /**
   * A depth first search which binds <code>decisionVars</code> in the given order, trying the
   * values of each domain in increasing order (or decreasing order where <code>maximize</code>
   * is set), propagating after each choice.
   *
   * <p>
   * On success the store is left in the state of the solution, inside one extra level per
   * decision, and the method returns the number of levels pushed (which the caller pops with
   * {@link #popToLevel(int)}); on failure or when <code>failLimit</code> failures have been
   * reached it returns <code>-1</code> with the store restored.
   */
  public int depthFirstSearch(CpVar[] decisionVars, boolean[] maximize, long failLimit) {
    int startLevel = level;
    long[] fails = {0};
    pushLevel();
    boolean ok;
    try {
      propagate();
      ok = dfs(decisionVars, maximize, 0, fails, failLimit);
    } catch (CpFail f) {
      ok = false;
    }
    if (!ok) {
      popToLevel(startLevel);
      return -1;
    }
    return level - startLevel;
  }

  private boolean dfs(CpVar[] vars, boolean[] maximize, int from, long[] fails, long failLimit) {
    int k = from;
    while (k < vars.length && vars[k].isBound()) {
      k++;
    }
    if (k == vars.length) {
      return true;
    }
    CpVar v = vars[k];
    boolean down = maximize != null && maximize[k];
    while (true) {
      if (fails[0] > failLimit) {
        return false;
      }
      long value = down ? v.max() : v.min();
      pushLevel();
      try {
        v.setValue(value);
        propagate();
        if (dfs(vars, maximize, k + 1, fails, failLimit)) {
          return true;
        }
      } catch (CpFail f) {
        // try the next value
      }
      popLevel();
      fails[0]++;
      try {
        v.removeValue(value);
        propagate();
      } catch (CpFail f) {
        return false;
      }
      if (v.isBound()) {
        // the remaining value is tried by the loop
        continue;
      }
    }
  }
}
