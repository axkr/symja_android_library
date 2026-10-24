package org.matheclipse.ortools.constraintsolver;

/**
 * The common part of the entries of an {@link Assignment}, mirroring
 * <code>com.google.ortools.constraintsolver.AssignmentElement</code> of the OR-Tools Java wrapper:
 * an entry can be deactivated, meaning that it is ignored when the assignment is restored.
 * Reimplemented for the simplified engine of this module.
 */
public class AssignmentElement {

  private boolean activated = true;

  /** Creates an activated element. */
  public AssignmentElement() {}

  /** Creates a copy of the activation state of <code>other</code>. */
  public AssignmentElement(AssignmentElement other) {
    this.activated = other.activated;
  }

  /** Activates the element. */
  public void activate() {
    activated = true;
  }

  /** Deactivates the element. */
  public void deactivate() {
    activated = false;
  }

  /** Whether the element is activated. */
  public boolean activated() {
    return activated;
  }
}
