package org.matheclipse.ortools.constraintsolver;

import java.util.function.Supplier;
import org.matheclipse.ortools.constraintsolver.cp.Propagator;

/**
 * A constraint of a {@link Solver}, mirroring
 * <code>com.google.ortools.constraintsolver.Constraint</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * A constraint is created by one of the <code>make...</code> methods of the solver and becomes
 * active when it is passed to {@link Solver#addConstraint(Constraint)}. Custom constraints written
 * in Java (C++ directors in the original wrapper) are not supported.
 */
public class Constraint extends PropagationBaseObject {

  final Propagator[] propagators;

  private final String description;

  private final Supplier<IntVar> reifier;

  private IntVar reified;

  Constraint(Solver solver, String description, Supplier<IntVar> reifier,
      Propagator... propagators) {
    super(solver);
    this.propagators = propagators;
    this.description = description;
    this.reifier = reifier;
  }

  /** Does nothing: the constraint is posted by {@link Solver#addConstraint(Constraint)}. */
  public void post() {
    // posting is done by Solver.addConstraint
  }

  /** Does nothing: the propagation is run by the search. */
  public void initialPropagate() {
    // propagation is run by the search
  }

  /** Adds the constraint to its solver. */
  public void postAndPropagate() {
    solver.addConstraint(this);
  }

  /** Always false in this implementation. */
  public boolean isCastConstraint() {
    return false;
  }

  /**
   * A 0/1 variable equal to the truth value of this constraint; available for the comparison
   * constraints (<code>makeEquality</code>, <code>makeLess</code>, ...).
   */
  public IntVar var() {
    if (reifier == null) {
      throw new UnsupportedOperationException(
          "Constraint.var() is not supported for " + description);
    }
    if (reified == null) {
      reified = reifier.get();
    }
    return reified;
  }

  @Override
  public String toString() {
    return description;
  }
}
