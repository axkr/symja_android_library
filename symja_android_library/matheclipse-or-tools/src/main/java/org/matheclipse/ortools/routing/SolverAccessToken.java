package org.matheclipse.ortools.routing;

/**
 * The key {@link org.matheclipse.ortools.constraintsolver.cp.SolverAccess#get(SolverAccessToken)}
 * requires. Only this package can create it, so only the routing layer of this module reaches the
 * engine behind the constraint solver objects. Not part of the OR-Tools API.
 */
public final class SolverAccessToken {

  /** The token of the routing layer. */
  static final SolverAccessToken INSTANCE = new SolverAccessToken();

  private SolverAccessToken() {}
}
