package org.matheclipse.ortools.constraintsolver;

import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * The integer variable entries of an {@link Assignment} in insertion order, mirroring
 * <code>com.google.ortools.constraintsolver.AssignmentIntContainer</code> of the OR-Tools Java
 * wrapper (reimplemented for the simplified engine of this module).
 */
public class AssignmentIntContainer {

  private final ArrayList<IntVarElement> elements = new ArrayList<>();

  private final IdentityHashMap<IntVar, IntVarElement> byVar = new IdentityHashMap<>();

  /** Creates an empty container. */
  public AssignmentIntContainer() {}

  /** Adds <code>var</code> (if not yet present) and returns its element. */
  public IntVarElement add(IntVar var) {
    IntVarElement e = byVar.get(var);
    if (e == null) {
      e = new IntVarElement(var);
      byVar.put(var, e);
      elements.add(e);
    }
    return e;
  }

  /** Same as {@link #add(IntVar)}. */
  public IntVarElement fastAdd(IntVar var) {
    return add(var);
  }

  /** Adds <code>var</code>; the position is ignored by this implementation. */
  public IntVarElement addAtPosition(IntVar var, int position) {
    return add(var);
  }

  /** Removes all entries. */
  public void clear() {
    elements.clear();
    byVar.clear();
  }

  /** Does nothing (the container grows as needed). */
  public void resize(long size) {}

  /** Whether the container is empty. */
  public boolean empty() {
    return elements.isEmpty();
  }

  /** Copies the stored bounds of the variables present in both containers. */
  public void copyIntersection(AssignmentIntContainer container) {
    for (IntVarElement e : elements) {
      IntVarElement other = container.byVar.get(e.var());
      if (other != null) {
        e.copy(other);
      }
    }
  }

  /** Replaces the content by a copy of <code>container</code>. */
  public void copy(AssignmentIntContainer container) {
    clear();
    for (IntVarElement e : container.elements) {
      IntVarElement c = e.clone();
      elements.add(c);
      byVar.put(c.var(), c);
    }
  }

  /** Whether <code>var</code> has an entry. */
  public boolean contains(IntVar var) {
    return byVar.containsKey(var);
  }

  /** The entry of <code>var</code>. */
  public IntVarElement mutableElement(IntVar var) {
    return element(var);
  }

  /** The entry of <code>var</code>. */
  public IntVarElement element(IntVar var) {
    IntVarElement e = byVar.get(var);
    if (e == null) {
      throw new IllegalArgumentException("Unknown variable " + var + " in assignment");
    }
    return e;
  }

  /** The entry at position <code>index</code>. */
  public IntVarElement mutableElement(int index) {
    return elements.get(index);
  }

  /** The entry at position <code>index</code>. */
  public IntVarElement element(int index) {
    return elements.get(index);
  }

  /** The number of entries. */
  public int size() {
    return elements.size();
  }

  /** Stores the current bounds of all variables. */
  public void store() {
    for (IntVarElement e : elements) {
      e.store();
    }
  }

  /** Restricts all activated variables to their stored bounds. */
  public void restore() {
    for (IntVarElement e : elements) {
      e.restore();
    }
  }

  /** Whether all stored bounds are single values. */
  public boolean AreAllElementsBound() {
    for (IntVarElement e : elements) {
      if (!e.bound()) {
        return false;
      }
    }
    return true;
  }

  IntVarElement find(IntVar var) {
    return byVar.get(var);
  }

  Iterable<IntVarElement> list() {
    return elements;
  }
}
