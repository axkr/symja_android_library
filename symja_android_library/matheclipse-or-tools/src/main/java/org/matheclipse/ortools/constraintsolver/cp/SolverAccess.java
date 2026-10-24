package org.matheclipse.ortools.constraintsolver.cp;

import org.matheclipse.ortools.constraintsolver.IntExpr;
import org.matheclipse.ortools.constraintsolver.Solver;
import org.matheclipse.ortools.routing.SolverAccessToken;

/**
 * Access to the engine behind the constraint solver API objects for the routing layer of this
 * module (<code>org.matheclipse.ortools.routing</code>), which checks user constraints posted on
 * the routing variables with the propagation engine of the {@link Solver}. Not part of the
 * OR-Tools API.
 *
 * <p>
 * The single implementation is installed by the class initializer of {@link Solver};
 * {@link #get(SolverAccessToken)} initializes that class first. Getting it needs a
 * {@link SolverAccessToken}, which only the routing package can create, so other code can't reach
 * the engine state through this class.
 */
public abstract class SolverAccess {

  /** The name of the only class {@link #install(SolverAccess)} accepts. */
  private static final String IMPLEMENTATION =
      "org.matheclipse.ortools.constraintsolver.SolverAccessImpl";

  private static final Object LOCK = new Object();

  private static volatile SolverAccess instance;

  /** Only the constraint solver package creates the implementation. */
  protected SolverAccess() {}

  /**
   * Installs the implementation; called once by the class initializer of {@link Solver}. Any
   * other implementation, and every later call, is ignored.
   */
  public static void install(SolverAccess access) {
    synchronized (LOCK) {
      if (instance == null && access != null
          && access.getClass().getName().equals(IMPLEMENTATION)) {
        instance = access;
      }
    }
  }

  /**
   * The implementation.
   *
   * @param token the key of the routing layer
   * @throws IllegalArgumentException if <code>token</code> is <code>null</code>
   */
  public static SolverAccess get(SolverAccessToken token) {
    if (token == null) {
      throw new IllegalArgumentException("the routing layer's token is needed");
    }
    SolverAccess a = instance;
    if (a == null) {
      try {
        Class.forName(Solver.class.getName(), true, Solver.class.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
      a = instance;
    }
    return a;
  }

  /** The engine variable of an expression. */
  public abstract CpVar cpVar(IntExpr expression);

  /** The propagation store of the solver. */
  public abstract CpStore store(Solver solver);

  /** Whether a domain became empty outside of any search (the model is infeasible). */
  public abstract boolean isRootFailed(Solver solver);

  /** Whether a callback called <code>finishCurrentSearch()</code> since the last clearing. */
  public abstract boolean isFinishRequested(Solver solver);

  /** Clears the request of <code>finishCurrentSearch()</code>. */
  public abstract void clearFinishRequest(Solver solver);
}
