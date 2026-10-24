package org.matheclipse.ortools.constraintsolver;

/**
 * Produces the decisions of a search, mirroring
 * <code>com.google.ortools.constraintsolver.DecisionBuilder</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * The search calls {@link #nextWrap(Solver)} at every node; returning <code>null</code> means that
 * the current state is a solution. Part of the reimplementation of the OR-Tools constraint solver
 * API on a simpler engine; Java subclasses (usually of {@link JavaDecisionBuilder}) are supported.
 */
public class DecisionBuilder extends BaseObject {

  private String name = "";

  /** Creates a decision builder which produces no decision. */
  public DecisionBuilder() {}

  /** The next decision, or <code>null</code> when the current state is a solution. */
  public Decision nextWrap(Solver s) {
    return null;
  }

  /** Changes the name. */
  public void set_name(String name) {
    this.name = name == null ? "" : name;
  }

  /** The name. */
  public String GetName() {
    return name;
  }

  @Override
  public String toString() {
    return name.isEmpty() ? getClass().getSimpleName() : name;
  }
}
