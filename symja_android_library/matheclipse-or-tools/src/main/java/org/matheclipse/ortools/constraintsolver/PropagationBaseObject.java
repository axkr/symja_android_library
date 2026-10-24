package org.matheclipse.ortools.constraintsolver;

/**
 * A named object which belongs to a {@link Solver}, mirroring
 * <code>com.google.ortools.constraintsolver.PropagationBaseObject</code> of the OR-Tools Java
 * wrapper (reimplemented on the simplified engine of this module).
 */
public class PropagationBaseObject extends BaseObject {

  final Solver solver;

  private String name = "";

  /** Creates an object owned by <code>s</code>. */
  public PropagationBaseObject(Solver s) {
    this.solver = s;
  }

  /** The solver owning this object. */
  public Solver solver() {
    return solver;
  }

  /** The name of the object, the empty string if none was given. */
  public String name() {
    return name;
  }

  /** Changes the name of the object. */
  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  /** Whether a non-empty name was given. */
  public boolean hasName() {
    return !name().isEmpty();
  }

  /** The base name used when the object has no name of its own. */
  public String baseName() {
    return "";
  }

  @Override
  public String toString() {
    return hasName() ? name() : getClass().getSimpleName();
  }
}
