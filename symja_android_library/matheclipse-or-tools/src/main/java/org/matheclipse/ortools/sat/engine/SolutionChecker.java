// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/cp_model_checker.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.matheclipse.ortools.sat.ConstraintProto;
import org.matheclipse.ortools.sat.CpModelProto;
import org.matheclipse.ortools.sat.IntegerVariableProto;
import org.matheclipse.ortools.sat.IntervalConstraintProto;
import org.matheclipse.ortools.sat.LinearExpressionProto;
import org.matheclipse.ortools.util.Domain;

/**
 * Checks that an assignment of all variables is a solution of a {@link CpModelProto}. This is the
 * reference semantics of every constraint; the engine verifies each solution with it before
 * reporting it.
 */
public final class SolutionChecker {
  private final long[] values;

  private SolutionChecker(long[] values) {
    this.values = values;
  }

  /** Returns true if {@code values} (one per model variable) satisfies all constraints. */
  public static boolean isFeasible(CpModelProto model, long[] values) {
    return firstViolatedConstraint(model, values) == -1;
  }

  /**
   * Returns -1 if {@code values} is a solution, -2 if a value is outside of its variable domain or
   * the objective domain, otherwise the index of the first violated constraint.
   */
  public static int firstViolatedConstraint(CpModelProto model, long[] values) {
    if (values.length != model.getVariablesCount()) {
      return -2;
    }
    for (int i = 0; i < model.getVariablesCount(); i++) {
      if (!domainContains(model.getVariables(i).getDomainList(), values[i])) {
        return -2;
      }
    }
    SolutionChecker checker = new SolutionChecker(values);
    for (int c = 0; c < model.getConstraintsCount(); c++) {
      if (!checker.constraintIsFeasible(model, model.getConstraints(c))) {
        return c;
      }
    }
    if (model.hasObjective() && model.getObjective().getDomainCount() > 0) {
      long inner = 0;
      for (int i = 0; i < model.getObjective().getVarsCount(); i++) {
        inner += checker.value(model.getObjective().getVars(i)) * model.getObjective().getCoeffs(i);
      }
      if (!domainContains(model.getObjective().getDomainList(), inner)) {
        return -2;
      }
    }
    return -1;
  }

  static boolean domainContains(List<Long> flat, long value) {
    for (int i = 0; i + 1 < flat.size(); i += 2) {
      if (value >= flat.get(i) && value <= flat.get(i + 1)) {
        return true;
      }
    }
    return false;
  }

  boolean literalIsTrue(int l) {
    if (l >= 0) {
      return values[l] != 0;
    }
    return values[-l - 1] == 0;
  }

  boolean literalIsFalse(int l) {
    return !literalIsTrue(l);
  }

  long value(int var) {
    if (var >= 0) {
      return values[var];
    }
    return -values[-var - 1];
  }

  boolean constraintIsEnforced(ConstraintProto ct) {
    for (int lit : ct.getEnforcementLiteralList()) {
      if (literalIsFalse(lit)) {
        return false;
      }
    }
    return true;
  }

  long linearExpressionValue(LinearExpressionProto expr) {
    long sum = expr.getOffset();
    for (int i = 0; i < expr.getVarsCount(); i++) {
      sum += value(expr.getVars(i)) * expr.getCoeffs(i);
    }
    return sum;
  }

  private boolean constraintIsFeasible(CpModelProto model, ConstraintProto ct) {
    if (!constraintIsEnforced(ct)) {
      return true;
    }
    switch (ct.getConstraintCase()) {
      case BOOL_OR:
        for (int lit : ct.getBoolOr().getLiteralsList()) {
          if (literalIsTrue(lit)) {
            return true;
          }
        }
        return false;
      case BOOL_AND:
        for (int lit : ct.getBoolAnd().getLiteralsList()) {
          if (literalIsFalse(lit)) {
            return false;
          }
        }
        return true;
      case AT_MOST_ONE: {
        int n = 0;
        for (int lit : ct.getAtMostOne().getLiteralsList()) {
          if (literalIsTrue(lit)) {
            n++;
          }
        }
        return n <= 1;
      }
      case EXACTLY_ONE: {
        int n = 0;
        for (int lit : ct.getExactlyOne().getLiteralsList()) {
          if (literalIsTrue(lit)) {
            n++;
          }
        }
        return n == 1;
      }
      case BOOL_XOR: {
        int sum = 0;
        for (int lit : ct.getBoolXor().getLiteralsList()) {
          sum ^= literalIsTrue(lit) ? 1 : 0;
        }
        return sum == 1;
      }
      case LINEAR: {
        long sum = 0;
        for (int i = 0; i < ct.getLinear().getVarsCount(); i++) {
          sum += values[ct.getLinear().getVars(i)] * ct.getLinear().getCoeffs(i);
        }
        return domainContains(ct.getLinear().getDomainList(), sum);
      }
      case INT_PROD: {
        long prod = linearExpressionValue(ct.getIntProd().getTarget());
        long actual = 1;
        for (LinearExpressionProto e : ct.getIntProd().getExprsList()) {
          actual = Domain.capProd(actual, linearExpressionValue(e));
        }
        return prod == actual;
      }
      case INT_DIV: {
        long d = linearExpressionValue(ct.getIntDiv().getExprs(1));
        if (d == 0) {
          return false;
        }
        return linearExpressionValue(ct.getIntDiv().getTarget()) == linearExpressionValue(
            ct.getIntDiv().getExprs(0)) / d;
      }
      case INT_MOD: {
        long m = linearExpressionValue(ct.getIntMod().getExprs(1));
        if (m == 0) {
          return false;
        }
        return linearExpressionValue(ct.getIntMod().getTarget()) == linearExpressionValue(
            ct.getIntMod().getExprs(0)) % m;
      }
      case LIN_MAX: {
        long max = linearExpressionValue(ct.getLinMax().getTarget());
        long actual = Long.MIN_VALUE;
        for (LinearExpressionProto e : ct.getLinMax().getExprsList()) {
          actual = Math.max(actual, linearExpressionValue(e));
        }
        return max == actual;
      }
      case ALL_DIFF: {
        Set<Long> seen = new HashSet<>();
        for (LinearExpressionProto e : ct.getAllDiff().getExprsList()) {
          if (!seen.add(linearExpressionValue(e))) {
            return false;
          }
        }
        return true;
      }
      case INTERVAL: {
        IntervalConstraintProto iv = ct.getInterval();
        long size = linearExpressionValue(iv.getSize());
        if (size < 0) {
          return false;
        }
        return linearExpressionValue(iv.getStart()) + size == linearExpressionValue(iv.getEnd());
      }
      case NO_OVERLAP:
        return noOverlapIsFeasible(model, ct);
      case NO_OVERLAP_2D:
        return noOverlap2DIsFeasible(model, ct);
      case CUMULATIVE:
        return cumulativeIsFeasible(model, ct);
      case ELEMENT:
        return elementIsFeasible(ct);
      case TABLE:
        return tableIsFeasible(ct);
      case AUTOMATON:
        return automatonIsFeasible(ct);
      case CIRCUIT:
        return circuitIsFeasible(ct);
      case ROUTES:
        return routesIsFeasible(ct);
      case INVERSE:
        return inverseIsFeasible(ct);
      case RESERVOIR:
        return reservoirIsFeasible(ct);
      case CONSTRAINT_NOT_SET:
        return true;
      default:
        return false;
    }
  }

  private boolean noOverlapIsFeasible(CpModelProto model, ConstraintProto ct) {
    List<long[]> pairs = new ArrayList<>();
    for (int i : ct.getNoOverlap().getIntervalsList()) {
      ConstraintProto ic = model.getConstraints(i);
      if (constraintIsEnforced(ic)) {
        pairs.add(new long[] {linearExpressionValue(ic.getInterval().getStart()),
            linearExpressionValue(ic.getInterval().getSize())});
      }
    }
    pairs.sort((a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
    long previousEnd = Long.MIN_VALUE;
    for (long[] p : pairs) {
      if (p[0] < previousEnd) {
        return false;
      }
      previousEnd = p[0] + p[1];
    }
    return true;
  }

  // 1D overlap: two positive intervals overlap on their interior, a point overlaps a positive
  // interval when strictly inside, two points never overlap
  private static boolean overlap1d(long a1, long a2, long b1, long b2) {
    boolean za = a1 == a2;
    boolean zb = b1 == b2;
    if (za && zb) {
      return false;
    }
    if (za) {
      return b1 < a1 && a1 < b2;
    }
    if (zb) {
      return a1 < b1 && b1 < a2;
    }
    return a1 < b2 && b1 < a2;
  }

  private boolean noOverlap2DIsFeasible(CpModelProto model, ConstraintProto ct) {
    List<long[]> rects = new ArrayList<>();
    int n = ct.getNoOverlap2D().getXIntervalsCount();
    for (int i = 0; i < n; i++) {
      ConstraintProto x = model.getConstraints(ct.getNoOverlap2D().getXIntervals(i));
      ConstraintProto y = model.getConstraints(ct.getNoOverlap2D().getYIntervals(i));
      if (constraintIsEnforced(x) && constraintIsEnforced(y)) {
        rects.add(new long[] {linearExpressionValue(x.getInterval().getStart()),
            linearExpressionValue(x.getInterval().getEnd()),
            linearExpressionValue(y.getInterval().getStart()),
            linearExpressionValue(y.getInterval().getEnd())});
      }
    }
    for (int i = 0; i < rects.size(); i++) {
      long[] a = rects.get(i);
      for (int j = i + 1; j < rects.size(); j++) {
        long[] b = rects.get(j);
        if (overlap1d(a[0], a[1], b[0], b[1]) && overlap1d(a[2], a[3], b[2], b[3])) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean cumulativeIsFeasible(CpModelProto model, ConstraintProto ct) {
    long capacity = linearExpressionValue(ct.getCumulative().getCapacity());
    if (capacity < 0) {
      return false;
    }
    List<long[]> events = new ArrayList<>();
    for (int i = 0; i < ct.getCumulative().getIntervalsCount(); i++) {
      ConstraintProto ic = model.getConstraints(ct.getCumulative().getIntervals(i));
      if (!constraintIsEnforced(ic)) {
        continue;
      }
      long start = linearExpressionValue(ic.getInterval().getStart());
      long duration = linearExpressionValue(ic.getInterval().getSize());
      long demand = linearExpressionValue(ct.getCumulative().getDemands(i));
      if (duration == 0 || demand == 0) {
        continue;
      }
      events.add(new long[] {start, demand});
      events.add(new long[] {start + duration, -demand});
    }
    events.sort((a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
    long load = 0;
    for (long[] e : events) {
      load += e[1];
      if (load > capacity) {
        return false;
      }
    }
    return true;
  }

  private boolean elementIsFeasible(ConstraintProto ct) {
    if (ct.getElement().getVarsCount() > 0) {
      long index = value(ct.getElement().getIndex());
      if (index < 0 || index >= ct.getElement().getVarsCount()) {
        return false;
      }
      return value(ct.getElement().getVars((int) index)) == value(ct.getElement().getTarget());
    }
    if (ct.getElement().getExprsCount() > 0) {
      long index = linearExpressionValue(ct.getElement().getLinearIndex());
      if (index < 0 || index >= ct.getElement().getExprsCount()) {
        return false;
      }
      return linearExpressionValue(ct.getElement().getExprs((int) index)) == linearExpressionValue(
          ct.getElement().getLinearTarget());
    }
    return false;
  }

  private boolean tableIsFeasible(ConstraintProto ct) {
    List<Long> solution = new ArrayList<>();
    if (ct.getTable().getExprsCount() == 0) {
      for (int v : ct.getTable().getVarsList()) {
        solution.add(value(v));
      }
    } else {
      for (LinearExpressionProto e : ct.getTable().getExprsList()) {
        solution.add(linearExpressionValue(e));
      }
    }
    if (solution.isEmpty()) {
      return true;
    }
    int size = solution.size();
    List<Long> vals = ct.getTable().getValuesList();
    for (int row = 0; row + size <= vals.size(); row += size) {
      int i = 0;
      while (solution.get(i).longValue() == vals.get(row + i).longValue()) {
        i++;
        if (i == size) {
          return !ct.getTable().getNegated();
        }
      }
    }
    return ct.getTable().getNegated();
  }

  private boolean automatonIsFeasible(ConstraintProto ct) {
    Map<List<Long>, Long> transitions = new HashMap<>();
    int numTransitions = ct.getAutomaton().getTransitionTailCount();
    for (int i = 0; i < numTransitions; i++) {
      transitions.put(Arrays.asList(ct.getAutomaton().getTransitionTail(i),
          ct.getAutomaton().getTransitionLabel(i)), ct.getAutomaton().getTransitionHead(i));
    }
    long state = ct.getAutomaton().getStartingState();
    int numSteps = Math.max(ct.getAutomaton().getVarsCount(), ct.getAutomaton().getExprsCount());
    for (int i = 0; i < numSteps; i++) {
      long label = ct.getAutomaton().getVarsCount() == 0
          ? linearExpressionValue(ct.getAutomaton().getExprs(i))
          : value(ct.getAutomaton().getVars(i));
      Long next = transitions.get(Arrays.asList(state, label));
      if (next == null) {
        return false;
      }
      state = next;
    }
    for (long f : ct.getAutomaton().getFinalStatesList()) {
      if (state == f) {
        return true;
      }
    }
    return false;
  }

  private boolean circuitIsFeasible(ConstraintProto ct) {
    int numArcs = ct.getCircuit().getTailsCount();
    Set<Integer> nodes = new HashSet<>();
    Map<Integer, Integer> nexts = new HashMap<>();
    for (int i = 0; i < numArcs; i++) {
      int tail = ct.getCircuit().getTails(i);
      int head = ct.getCircuit().getHeads(i);
      nodes.add(tail);
      nodes.add(head);
      if (literalIsFalse(ct.getCircuit().getLiterals(i))) {
        continue;
      }
      if (nexts.containsKey(tail)) {
        return false;
      }
      nexts.put(tail, head);
    }
    int inCycle = -1;
    int cycleSize = 0;
    for (int node : nodes) {
      Integer next = nexts.get(node);
      if (next == null) {
        return false;
      }
      if (next == node) {
        continue;
      }
      inCycle = node;
      cycleSize++;
    }
    if (cycleSize == 0) {
      return true;
    }
    Set<Integer> visited = new HashSet<>();
    int current = inCycle;
    int numVisited = 0;
    while (!visited.contains(current)) {
      numVisited++;
      visited.add(current);
      current = nexts.get(current);
    }
    if (current != inCycle) {
      return false;
    }
    return numVisited == cycleSize;
  }

  private boolean routesIsFeasible(ConstraintProto ct) {
    int numArcs = ct.getRoutes().getTailsCount();
    int numUsedArcs = 0;
    int numSelfArcs = 0;
    int numNodes = 0;
    for (int i = 0; i < numArcs; i++) {
      numNodes = Math.max(numNodes, 1 + ct.getRoutes().getTails(i));
      numNodes = Math.max(numNodes, 1 + ct.getRoutes().getHeads(i));
    }
    int[] tailToHead = new int[numNodes];
    Arrays.fill(tailToHead, -1);
    boolean[] hasIncoming = new boolean[numNodes];
    boolean[] hasOutgoing = new boolean[numNodes];
    List<Integer> depotNexts = new ArrayList<>();
    for (int i = 0; i < numArcs; i++) {
      int tail = ct.getRoutes().getTails(i);
      int head = ct.getRoutes().getHeads(i);
      if (literalIsTrue(ct.getRoutes().getLiterals(i))) {
        if (tail != 0) {
          if (hasOutgoing[tail]) {
            return false;
          }
          hasOutgoing[tail] = true;
        }
        if (head != 0) {
          if (hasIncoming[head]) {
            return false;
          }
          hasIncoming[head] = true;
        }
        if (tail == head) {
          if (tail == 0) {
            return false;
          }
          numSelfArcs++;
          continue;
        }
        numUsedArcs++;
        if (tail == 0) {
          depotNexts.add(head);
        } else {
          tailToHead[tail] = head;
        }
      }
    }
    if (numNodes == 0) {
      return true;
    }
    int count = 0;
    for (int start : depotNexts) {
      count++;
      int steps = 0;
      while (start != 0) {
        if (tailToHead[start] == -1 || steps++ > numNodes) {
          return false;
        }
        start = tailToHead[start];
        count++;
      }
    }
    if (count != numUsedArcs) {
      return false;
    }
    return count - depotNexts.size() + 1 + numSelfArcs == numNodes;
  }

  private boolean inverseIsFeasible(ConstraintProto ct) {
    int n = ct.getInverse().getFDirectCount();
    if (n != ct.getInverse().getFInverseCount()) {
      return false;
    }
    for (int i = 0; i < n; i++) {
      long fi = value(ct.getInverse().getFDirect(i));
      if (fi < 0 || n <= fi) {
        return false;
      }
      if (i != value(ct.getInverse().getFInverse((int) fi))) {
        return false;
      }
    }
    int numExprs = ct.getInverse().getFExprDirectCount();
    if (numExprs != ct.getInverse().getFExprInverseCount()) {
      return false;
    }
    // f_expr_inverse[f_expr_direct[i]] == i for all i is sufficient
    for (int i = 0; i < numExprs; i++) {
      long fi = linearExpressionValue(ct.getInverse().getFExprDirect(i));
      if (fi < 0 || numExprs <= fi) {
        return false;
      }
      if (i != linearExpressionValue(ct.getInverse().getFExprInverse((int) fi))) {
        return false;
      }
    }
    return true;
  }

  private boolean reservoirIsFeasible(ConstraintProto ct) {
    int n = ct.getReservoir().getTimeExprsCount();
    long minLevel = ct.getReservoir().getMinLevel();
    long maxLevel = ct.getReservoir().getMaxLevel();
    TreeMap<Long, Long> deltas = new TreeMap<>();
    boolean hasActive = ct.getReservoir().getActiveLiteralsCount() > 0;
    for (int i = 0; i < n; i++) {
      long time = linearExpressionValue(ct.getReservoir().getTimeExprs(i));
      if (!hasActive || literalIsTrue(ct.getReservoir().getActiveLiterals(i))) {
        long level = linearExpressionValue(ct.getReservoir().getLevelChanges(i));
        deltas.merge(time, level, Long::sum);
      }
    }
    long current = 0;
    for (long d : deltas.values()) {
      current += d;
      if (current < minLevel || current > maxLevel) {
        return false;
      }
    }
    return true;
  }

  /** Returns the domain of a variable proto. */
  static Domain domainOf(IntegerVariableProto v) {
    long[] flat = new long[v.getDomainCount()];
    for (int i = 0; i < flat.length; i++) {
      flat[i] = v.getDomain(i);
    }
    return Domain.fromFlatIntervals(flat);
  }
}
