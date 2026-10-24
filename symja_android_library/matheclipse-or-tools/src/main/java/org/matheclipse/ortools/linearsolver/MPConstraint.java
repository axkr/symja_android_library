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
// Modified for the Symja matheclipse-or-tools module: adapted from the OR-Tools Java wrapper
// (ortools/linear_solver/linear_solver.h and linear_solver.cc ported to Java), see NOTICE in the
// module root.

package org.matheclipse.ortools.linearsolver;

import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The class for constraints of a Mathematical Programming (MP) model.
 *
 * <p>
 * A constraint is represented as a linear equation or inequality.
 */
public class MPConstraint {

  private final MPSolver solver;
  private final int index;
  private double lb;
  private double ub;
  private final String name;
  private boolean isLazy;
  final Map<MPVariable, Double> coefficients = new LinkedHashMap<>();
  double dualValue;

  MPConstraint(MPSolver solver, int index, double lb, double ub, String name) {
    this.solver = solver;
    this.index = index;
    this.lb = lb;
    this.ub = ub;
    this.name = (name == null || name.isEmpty()) ? String.format(Locale.ROOT, "auto_c_%09d", index) : name;
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Returns the name of the constraint. */
  public String name() {
    return name;
  }

  /**
   * Sets the coefficient of the variable on the constraint.
   *
   * <p>
   * If the variable does not belong to the solver, the function just returns.
   */
  public void setCoefficient(MPVariable var, double coeff) {
    if (var == null || !solver.ownsVariable(var)) {
      return;
    }
    if (coeff == 0.0) {
      final Double old = coefficients.get(var);
      if (old != null && old != 0.0) {
        coefficients.put(var, 0.0);
        solver.invalidateSolution();
      }
      return;
    }
    coefficients.put(var, coeff);
    solver.invalidateSolution();
  }

  /**
   * Gets the coefficient of a given variable on the constraint (which is 0 if the variable does
   * not appear in the constraint).
   */
  public double getCoefficient(MPVariable var) {
    if (var == null) {
      return 0.0;
    }
    final Double c = coefficients.get(var);
    return c == null ? 0.0 : c;
  }

  /** Clears all variables and coefficients. Does not clear the bounds. */
  public void clear() {
    coefficients.clear();
    solver.invalidateSolution();
  }

  /** Returns the lower bound. */
  public double lb() {
    return lb;
  }

  /** Returns the upper bound. */
  public double ub() {
    return ub;
  }

  /** Sets the lower bound. */
  public void setLb(double lb) {
    setBounds(lb, this.ub);
  }

  /** Sets the upper bound. */
  public void setUb(double ub) {
    setBounds(this.lb, ub);
  }

  /** Sets both the lower and upper bounds. */
  public void setBounds(double lb, double ub) {
    final boolean change = lb != this.lb || ub != this.ub;
    this.lb = lb;
    this.ub = ub;
    if (change) {
      solver.invalidateSolution();
    }
  }

  /** Advanced usage: returns true if the constraint is "lazy" (see below). */
  public boolean isLazy() {
    return isLazy;
  }

  /**
   * Advanced usage: sets the constraint "laziness".
   *
   * <p>
   * This is only supported for SCIP and has no effect on other solvers (and on this port).
   */
  public void setIsLazy(boolean laziness) {
    this.isLazy = laziness;
  }

  /** Returns the index of the constraint in the MPSolver::constraints_. */
  public int index() {
    return index;
  }

  /**
   * Advanced usage: returns the dual value of the constraint in the current solution (only
   * available for continuous problems).
   */
  public double dualValue() {
    if (!solver.isContinuous()) {
      return 0.0;
    }
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return 0.0;
    }
    return dualValue;
  }

  /**
   * Advanced usage: returns the basis status of the constraint (only available for continuous
   * problems).
   *
   * <p>
   * Note that if a constraint "linear_expression in [lb, ub]" is transformed into
   * "linear_expression + slack = 0" with slack in [-ub, -lb], then this status is the same as the
   * status of the slack variable with AT_UPPER_BOUND and AT_LOWER_BOUND swapped.
   */
  public MPSolver.BasisStatus basisStatus() {
    if (!solver.isContinuous()) {
      return MPSolver.BasisStatus.FREE;
    }
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return MPSolver.BasisStatus.FREE;
    }
    return solver.rowStatus(index);
  }

  @Override
  public String toString() {
    return name;
  }
}
