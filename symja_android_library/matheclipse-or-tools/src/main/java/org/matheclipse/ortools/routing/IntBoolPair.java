package org.matheclipse.ortools.routing;

/**
 * A pair of an <code>int</code> and a <code>boolean</code>, mirroring
 * <code>com.google.ortools.routing.IntBoolPair</code> of the OR-Tools Java wrapper
 * (<code>std::pair&lt;int, bool&gt;</code>), reimplemented as a plain Java object. Returned by
 * <code>Model.addConstantDimension</code> and similar methods: the transit callback index
 * and whether the dimension was added.
 */
public class IntBoolPair {

  private int first;

  private boolean second;

  /** Creates <code>(0, false)</code>. */
  public IntBoolPair() {}

  /** Creates <code>(first, second)</code>. */
  public IntBoolPair(int first, boolean second) {
    this.first = first;
    this.second = second;
  }

  /** Creates a copy. */
  public IntBoolPair(IntBoolPair other) {
    this.first = other.first;
    this.second = other.second;
  }

  /** Changes the first element. */
  public void setFirst(int value) {
    first = value;
  }

  /** The first element. */
  public int getFirst() {
    return first;
  }

  /** Changes the second element. */
  public void setSecond(boolean value) {
    second = value;
  }

  /** The second element. */
  public boolean getSecond() {
    return second;
  }
}
