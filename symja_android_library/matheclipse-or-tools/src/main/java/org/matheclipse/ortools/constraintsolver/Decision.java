package org.matheclipse.ortools.constraintsolver;

/**
 * A choice point of the search, mirroring <code>com.google.ortools.constraintsolver.Decision</code>
 * of the OR-Tools Java wrapper: the left branch is {@link #apply(Solver)}, the right branch (taken
 * after the left one failed or was explored) is {@link #refute(Solver)}.
 *
 * <p>
 * Part of the reimplementation of the OR-Tools constraint solver API on a simpler engine;
 * subclasses written in Java are supported.
 */
public class Decision extends BaseObject {

  /** Creates a decision which does nothing in both branches. */
  public Decision() {}

  /** The left branch. */
  public void apply(Solver s) {}

  /** The right branch. */
  public void refute(Solver s) {}
}
