package org.matheclipse.ortools.constraintsolver;

import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * The interval variable entries of an {@link Assignment} in insertion order, mirroring
 * <code>com.google.ortools.constraintsolver.AssignmentIntervalContainer</code> of the OR-Tools
 * Java wrapper (reimplemented for the simplified engine of this module).
 */
public class AssignmentIntervalContainer {

  private final ArrayList<IntervalVarElement> elements = new ArrayList<>();

  private final IdentityHashMap<IntervalVar, IntervalVarElement> byVar = new IdentityHashMap<>();

  /** Creates an empty container. */
  public AssignmentIntervalContainer() {}

  /** Adds <code>var</code> (if not yet present) and returns its element. */
  public IntervalVarElement add(IntervalVar var) {
    IntervalVarElement e = byVar.get(var);
    if (e == null) {
      e = new IntervalVarElement(var);
      byVar.put(var, e);
      elements.add(e);
    }
    return e;
  }

  /** Same as {@link #add(IntervalVar)}. */
  public IntervalVarElement fastAdd(IntervalVar var) {
    return add(var);
  }

  /** Adds <code>var</code>; the position is ignored by this implementation. */
  public IntervalVarElement addAtPosition(IntervalVar var, int position) {
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
  public void copyIntersection(AssignmentIntervalContainer container) {
    for (IntervalVarElement e : elements) {
      IntervalVarElement other = container.byVar.get(e.var());
      if (other != null) {
        e.copy(other);
      }
    }
  }

  /** Replaces the content by a copy of <code>container</code>. */
  public void copy(AssignmentIntervalContainer container) {
    clear();
    for (IntervalVarElement e : container.elements) {
      IntervalVarElement c = e.clone();
      elements.add(c);
      byVar.put(c.var(), c);
    }
  }

  /** Whether <code>var</code> has an entry. */
  public boolean contains(IntervalVar var) {
    return byVar.containsKey(var);
  }

  /** The entry of <code>var</code>. */
  public IntervalVarElement mutableElement(IntervalVar var) {
    return element(var);
  }

  /** The entry of <code>var</code>. */
  public IntervalVarElement element(IntervalVar var) {
    IntervalVarElement e = byVar.get(var);
    if (e == null) {
      throw new IllegalArgumentException("Unknown interval " + var + " in assignment");
    }
    return e;
  }

  /** The entry at position <code>index</code>. */
  public IntervalVarElement mutableElement(int index) {
    return elements.get(index);
  }

  /** The entry at position <code>index</code>. */
  public IntervalVarElement element(int index) {
    return elements.get(index);
  }

  /** The number of entries. */
  public int size() {
    return elements.size();
  }

  /** Stores the current bounds of all intervals. */
  public void store() {
    for (IntervalVarElement e : elements) {
      e.store();
    }
  }

  /** Restricts all activated intervals to their stored bounds. */
  public void restore() {
    for (IntervalVarElement e : elements) {
      e.restore();
    }
  }

  /** Whether all stored bounds are single values. */
  public boolean AreAllElementsBound() {
    for (IntervalVarElement e : elements) {
      if (!e.bound()) {
        return false;
      }
    }
    return true;
  }

  IntervalVarElement find(IntervalVar var) {
    return byVar.get(var);
  }

  Iterable<IntervalVarElement> list() {
    return elements;
  }
}
