package org.matheclipse.ortools.constraintsolver;

/**
 * The root of the constraint solver object hierarchy, mirroring
 * <code>com.google.ortools.constraintsolver.BaseObject</code>.
 *
 * <p>
 * The classes of this package reimplement the API of the OR-Tools Java wrapper of the original
 * constraint solver on a much simpler engine written in Java (see the <code>cp</code> subpackage;
 * the routing library is in <code>org.matheclipse.ortools.routing</code>).
 */
public class BaseObject {

  /** Creates the object. */
  public BaseObject() {}

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
