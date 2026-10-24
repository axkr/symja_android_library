package org.matheclipse.ortools.constraintsolver.cp;

/**
 * Signals a failure (an empty domain or a violated constraint) inside the small constraint
 * propagation engine which backs the OR-Tools <code>Solver</code> facade of this module.
 *
 * <p>
 * The engine reimplements the behaviour of the OR-Tools original CP solver API on a much simpler
 * design; a failure aborts the current propagation and makes the search backtrack. The single
 * instance carries no stack trace, so throwing it is cheap.
 */
public final class CpFail extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** The shared instance; failures carry no information besides their occurrence. */
  public static final CpFail INSTANCE = new CpFail();

  private CpFail() {
    super("constraint solver failure", null, false, false);
  }
}
