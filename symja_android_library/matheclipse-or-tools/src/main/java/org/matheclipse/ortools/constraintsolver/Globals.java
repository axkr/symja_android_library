package org.matheclipse.ortools.constraintsolver;

/**
 * The global functions of the OR-Tools constraint solver wrapper (the SWIG module class
 * <code>com.google.ortools.constraintsolver.Globals</code> of OR-Tools 10.0, named
 * <code>main</code> before), reimplemented for this module: helpers on variable arrays and integer
 * division. The routing globals are in <code>org.matheclipse.ortools.routing.Globals</code>.
 */
public class Globals {

  /** Not meant to be instantiated; the class only holds static methods. */
  protected Globals() {}

  /** Whether all variables are bound. */
  public static boolean areAllBound(IntVar[] vars) {
    for (IntVar v : vars) {
      if (!v.bound()) {
        return false;
      }
    }
    return true;
  }

  /** Whether all variables have domains within {0, 1}. */
  public static boolean areAllBooleans(IntVar[] vars) {
    for (IntVar v : vars) {
      if (v.min() < 0 || v.max() > 1) {
        return false;
      }
    }
    return true;
  }

  /** Whether all variables are bound to <code>value</code>. */
  public static boolean areAllBoundTo(IntVar[] vars, long value) {
    for (IntVar v : vars) {
      if (!v.bound() || v.value() != value) {
        return false;
      }
    }
    return true;
  }

  /** The largest maximum of the variables. */
  public static long maxVarArray(IntVar[] vars) {
    long m = Long.MIN_VALUE;
    for (IntVar v : vars) {
      m = Math.max(m, v.max());
    }
    return m;
  }

  /** The smallest minimum of the variables. */
  public static long minVarArray(IntVar[] vars) {
    long m = Long.MAX_VALUE;
    for (IntVar v : vars) {
      m = Math.min(m, v.min());
    }
    return m;
  }

  /** <code>ceil(e / v)</code> for <code>v &gt; 0</code>. */
  public static long posIntDivUp(long e, long v) {
    return e < 0 ? -(-e / v) : (e + v - 1) / v;
  }

  /** <code>floor(e / v)</code> for <code>v &gt; 0</code>. */
  public static long posIntDivDown(long e, long v) {
    return e >= 0 ? e / v : -((-e + v - 1) / v);
  }

  /** The constant 0. */
  public static long zero() {
    return 0;
  }

  /** The constant 1 (not renamed by the OR-Tools wrapper, hence the capital letter). */
  public static long One() {
    return 1;
  }
}
