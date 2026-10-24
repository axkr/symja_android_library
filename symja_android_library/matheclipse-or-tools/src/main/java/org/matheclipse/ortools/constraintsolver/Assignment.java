package org.matheclipse.ortools.constraintsolver;

/**
 * A snapshot of variable bounds, mirroring
 * <code>com.google.ortools.constraintsolver.Assignment</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * An assignment holds entries for integer and interval variables plus an optional objective.
 * {@link #store()} records the current bounds of the variables, the getters return the recorded
 * bounds. The assignments returned by the routing solver hold the values of the routing
 * variables of the solution.
 */
public class Assignment extends PropagationBaseObject {

  private final AssignmentIntContainer intVars = new AssignmentIntContainer();

  private final AssignmentIntervalContainer intervalVars = new AssignmentIntervalContainer();

  private IntVarElement objective;

  /** Creates an empty assignment for variables of <code>solver</code>. */
  public Assignment(Solver solver) {
    super(solver);
  }

  /** Creates a copy of <code>copy</code>. */
  public Assignment(Assignment copy) {
    super(copy.solver);
    copy(copy);
  }

  /** Removes all entries and the objective. */
  public void clear() {
    intVars.clear();
    intervalVars.clear();
    objective = null;
  }

  /** Whether the assignment has no entry. */
  public boolean empty() {
    return intVars.empty() && intervalVars.empty();
  }

  /** The number of entries. */
  public int size() {
    return intVars.size() + intervalVars.size();
  }

  /** The number of integer variable entries. */
  public int numIntVars() {
    return intVars.size();
  }

  /** The number of interval variable entries. */
  public int numIntervalVars() {
    return intervalVars.size();
  }

  /** The number of sequence variable entries (always 0: sequences are not supported). */
  public int numSequenceVars() {
    return 0;
  }

  /** Records the current bounds of all variables and of the objective. */
  public void store() {
    intVars.store();
    intervalVars.store();
    if (objective != null) {
      objective.store();
    }
  }

  /** Restricts all activated variables to their recorded bounds. */
  public void restore() {
    intVars.restore();
    intervalVars.restore();
  }

  /** Adds an objective variable entry. */
  public void addObjective(IntVar v) {
    objective = new IntVarElement(v);
  }

  /** Removes the objective. */
  public void ClearObjective() {
    objective = null;
  }

  /** The objective variable, or null. */
  public IntVar objective() {
    return objective == null ? null : objective.var();
  }

  /** Whether an objective is present. */
  public boolean hasObjective() {
    return objective != null;
  }

  /** The recorded minimum of the objective. */
  public long objectiveMin() {
    return objective == null ? 0 : objective.min();
  }

  /** The recorded maximum of the objective. */
  public long objectiveMax() {
    return objective == null ? 0 : objective.max();
  }

  /** The recorded value of the objective (0 if there is no objective). */
  public long objectiveValue() {
    return objective == null ? 0 : objective.value();
  }

  /** Whether the recorded objective bounds are a single value. */
  public boolean objectiveBound() {
    return objective != null && objective.bound();
  }

  /** Changes the recorded objective minimum. */
  public void setObjectiveMin(long m) {
    if (objective != null) {
      objective.setMin(m);
    }
  }

  /** Changes the recorded objective maximum. */
  public void setObjectiveMax(long m) {
    if (objective != null) {
      objective.setMax(m);
    }
  }

  /** Changes the recorded objective value. */
  public void setObjectiveValue(long value) {
    if (objective != null) {
      objective.setValue(value);
    }
  }

  /** Changes the recorded objective range. */
  public void setObjectiveRange(long l, long u) {
    if (objective != null) {
      objective.setRange(l, u);
    }
  }

  /** Adds an entry for <code>var</code> (holding its current bounds). */
  public IntVarElement add(IntVar var) {
    return intVars.add(var);
  }

  /** Adds entries for all <code>vars</code>. */
  public void add(IntVar[] vars) {
    for (IntVar v : vars) {
      intVars.add(v);
    }
  }

  /** Same as {@link #add(IntVar)}. */
  public IntVarElement fastAdd(IntVar var) {
    return intVars.add(var);
  }

  /** The recorded minimum of <code>var</code>. */
  public long min(IntVar var) {
    return intVars.element(var).min();
  }

  /** The recorded maximum of <code>var</code>. */
  public long max(IntVar var) {
    return intVars.element(var).max();
  }

  /** The recorded value of <code>var</code> (its minimum if not bound). */
  public long value(IntVar var) {
    return intVars.element(var).value();
  }

  /** Whether the recorded bounds of <code>var</code> are a single value. */
  public boolean bound(IntVar var) {
    return intVars.element(var).bound();
  }

  /** Changes the recorded minimum of <code>var</code>. */
  public void setMin(IntVar var, long m) {
    intVars.element(var).setMin(m);
  }

  /** Changes the recorded maximum of <code>var</code>. */
  public void setMax(IntVar var, long m) {
    intVars.element(var).setMax(m);
  }

  /** Changes the recorded range of <code>var</code>. */
  public void setRange(IntVar var, long l, long u) {
    intVars.element(var).setRange(l, u);
  }

  /** Records a value for <code>var</code>. */
  public void setValue(IntVar var, long value) {
    intVars.element(var).setValue(value);
  }

  /** Adds an entry for the interval <code>var</code>. */
  public IntervalVarElement add(IntervalVar var) {
    return intervalVars.add(var);
  }

  /** Adds entries for all intervals. */
  public void add(IntervalVar[] vars) {
    for (IntervalVar v : vars) {
      intervalVars.add(v);
    }
  }

  /** Same as {@link #add(IntervalVar)}. */
  public IntervalVarElement fastAdd(IntervalVar var) {
    return intervalVars.add(var);
  }

  /** The recorded earliest start of <code>var</code>. */
  public long startMin(IntervalVar var) {
    return intervalVars.element(var).startMin();
  }

  /** The recorded latest start of <code>var</code>. */
  public long startMax(IntervalVar var) {
    return intervalVars.element(var).startMax();
  }

  /** The recorded start of <code>var</code>. */
  public long startValue(IntervalVar var) {
    return intervalVars.element(var).startValue();
  }

  /** The recorded smallest duration of <code>var</code>. */
  public long durationMin(IntervalVar var) {
    return intervalVars.element(var).durationMin();
  }

  /** The recorded largest duration of <code>var</code>. */
  public long durationMax(IntervalVar var) {
    return intervalVars.element(var).durationMax();
  }

  /** The recorded duration of <code>var</code>. */
  public long durationValue(IntervalVar var) {
    return intervalVars.element(var).durationValue();
  }

  /** The recorded earliest end of <code>var</code>. */
  public long endMin(IntervalVar var) {
    return intervalVars.element(var).endMin();
  }

  /** The recorded latest end of <code>var</code>. */
  public long endMax(IntervalVar var) {
    return intervalVars.element(var).endMax();
  }

  /** The recorded end of <code>var</code>. */
  public long endValue(IntervalVar var) {
    return intervalVars.element(var).endValue();
  }

  /** The recorded minimum of the performed status of <code>var</code>. */
  public long performedMin(IntervalVar var) {
    return intervalVars.element(var).performedMin();
  }

  /** The recorded maximum of the performed status of <code>var</code>. */
  public long performedMax(IntervalVar var) {
    return intervalVars.element(var).performedMax();
  }

  /** The recorded performed status of <code>var</code>. */
  public long performedValue(IntervalVar var) {
    return intervalVars.element(var).performedValue();
  }

  /** Changes the recorded earliest start. */
  public void setStartMin(IntervalVar var, long m) {
    intervalVars.element(var).setStartMin(m);
  }

  /** Changes the recorded latest start. */
  public void setStartMax(IntervalVar var, long m) {
    intervalVars.element(var).setStartMax(m);
  }

  /** Changes the recorded start range. */
  public void setStartRange(IntervalVar var, long mi, long ma) {
    intervalVars.element(var).setStartRange(mi, ma);
  }

  /** Records a start value. */
  public void setStartValue(IntervalVar var, long value) {
    intervalVars.element(var).setStartValue(value);
  }

  /** Changes the recorded smallest duration. */
  public void setDurationMin(IntervalVar var, long m) {
    intervalVars.element(var).setDurationMin(m);
  }

  /** Changes the recorded largest duration. */
  public void setDurationMax(IntervalVar var, long m) {
    intervalVars.element(var).setDurationMax(m);
  }

  /** Changes the recorded duration range. */
  public void setDurationRange(IntervalVar var, long mi, long ma) {
    intervalVars.element(var).setDurationRange(mi, ma);
  }

  /** Records a duration value. */
  public void setDurationValue(IntervalVar var, long value) {
    intervalVars.element(var).setDurationValue(value);
  }

  /** Changes the recorded earliest end. */
  public void setEndMin(IntervalVar var, long m) {
    intervalVars.element(var).setEndMin(m);
  }

  /** Changes the recorded latest end. */
  public void setEndMax(IntervalVar var, long m) {
    intervalVars.element(var).setEndMax(m);
  }

  /** Changes the recorded end range. */
  public void setEndRange(IntervalVar var, long mi, long ma) {
    intervalVars.element(var).setEndRange(mi, ma);
  }

  /** Records an end value. */
  public void setEndValue(IntervalVar var, long value) {
    intervalVars.element(var).setEndValue(value);
  }

  /** Changes the recorded minimum of the performed status. */
  public void setPerformedMin(IntervalVar var, long m) {
    intervalVars.element(var).setPerformedMin(m);
  }

  /** Changes the recorded maximum of the performed status. */
  public void setPerformedMax(IntervalVar var, long m) {
    intervalVars.element(var).setPerformedMax(m);
  }

  /** Changes the recorded range of the performed status. */
  public void setPerformedRange(IntervalVar var, long mi, long ma) {
    intervalVars.element(var).setPerformedRange(mi, ma);
  }

  /** Records a performed status. */
  public void setPerformedValue(IntervalVar var, long value) {
    intervalVars.element(var).setPerformedValue(value);
  }

  /** Activates the entry of <code>var</code>. */
  public void activate(IntVar var) {
    intVars.element(var).activate();
  }

  /** Deactivates the entry of <code>var</code>. */
  public void deactivate(IntVar var) {
    intVars.element(var).deactivate();
  }

  /** Whether the entry of <code>var</code> is activated. */
  public boolean activated(IntVar var) {
    return intVars.element(var).activated();
  }

  /** Activates the entry of the interval <code>var</code>. */
  public void activate(IntervalVar var) {
    intervalVars.element(var).activate();
  }

  /** Deactivates the entry of the interval <code>var</code>. */
  public void deactivate(IntervalVar var) {
    intervalVars.element(var).deactivate();
  }

  /** Whether the entry of the interval <code>var</code> is activated. */
  public boolean activated(IntervalVar var) {
    return intervalVars.element(var).activated();
  }

  /** Activates the objective entry. */
  public void activateObjective() {
    if (objective != null) {
      objective.activate();
    }
  }

  /** Deactivates the objective entry. */
  public void deactivateObjective() {
    if (objective != null) {
      objective.deactivate();
    }
  }

  /** Whether the objective entry is activated. */
  public boolean activatedObjective() {
    return objective != null && objective.activated();
  }

  /** Whether all recorded bounds are single values. */
  public boolean AreAllElementsBound() {
    return intVars.AreAllElementsBound() && intervalVars.AreAllElementsBound();
  }

  /** Whether <code>var</code> has an entry. */
  public boolean contains(IntVar var) {
    return intVars.contains(var);
  }

  /** Whether the interval <code>var</code> has an entry. */
  public boolean contains(IntervalVar var) {
    return intervalVars.contains(var);
  }

  /** Copies the recorded bounds of the variables present in both assignments. */
  public void copyIntersection(Assignment assignment) {
    intVars.copyIntersection(assignment.intVars);
    intervalVars.copyIntersection(assignment.intervalVars);
    if (objective != null && assignment.objective != null
        && objective.var() == assignment.objective.var()) {
      objective.copy(assignment.objective);
    }
  }

  /** Replaces the content by a copy of <code>assignment</code>. */
  public void copy(Assignment assignment) {
    intVars.copy(assignment.intVars);
    intervalVars.copy(assignment.intervalVars);
    objective = assignment.objective == null ? null : assignment.objective.clone();
  }

  /** The integer variable entries. */
  public AssignmentIntContainer intVarContainer() {
    return intVars;
  }

  /** The integer variable entries. */
  public AssignmentIntContainer mutableIntVarContainer() {
    return intVars;
  }

  /** The interval variable entries. */
  public AssignmentIntervalContainer intervalVarContainer() {
    return intervalVars;
  }

  /** The interval variable entries. */
  public AssignmentIntervalContainer mutableIntervalVarContainer() {
    return intervalVars;
  }

  IntVarElement objectiveElement() {
    return objective;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("Assignment(");
    boolean first = true;
    for (IntVarElement e : intVars.list()) {
      if (!first) {
        buf.append(", ");
      }
      first = false;
      buf.append(e.var().name()).append(' ').append(e);
    }
    for (IntervalVarElement e : intervalVars.list()) {
      if (!first) {
        buf.append(", ");
      }
      first = false;
      buf.append(e.var().name()).append(' ').append(e);
    }
    if (objective != null) {
      if (!first) {
        buf.append(", ");
      }
      buf.append("Objective").append(objective);
    }
    return buf.append(')').toString();
  }
}
