package org.matheclipse.ortools.constraintsolver;

import org.matheclipse.ortools.constraintsolver.cp.CpStore;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;
import org.matheclipse.ortools.constraintsolver.cp.SolverAccess;

/** The implementation of {@link SolverAccess}, installed by the class initializer of Solver. */
final class SolverAccessImpl extends SolverAccess {

  @Override
  public CpVar cpVar(IntExpr expression) {
    return expression.cpVar;
  }

  @Override
  public CpStore store(Solver solver) {
    return solver.store();
  }

  @Override
  public boolean isRootFailed(Solver solver) {
    return solver.isRootFailed();
  }

  @Override
  public boolean isFinishRequested(Solver solver) {
    return solver.isFinishRequested();
  }

  @Override
  public void clearFinishRequest(Solver solver) {
    solver.clearFinishRequest();
  }
}
