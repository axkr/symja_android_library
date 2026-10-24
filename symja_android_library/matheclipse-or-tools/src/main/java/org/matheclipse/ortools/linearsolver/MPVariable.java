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

/** The class for variables of a Mathematical Programming (MP) model. */
public class MPVariable {

  private final MPSolver solver;
  private final int index;
  private double lb;
  private double ub;
  private boolean integer;
  private final String name;
  double solutionValue;
  double reducedCost;

  MPVariable(MPSolver solver, int index, double lb, double ub, boolean integer, String name) {
    this.solver = solver;
    this.index = index;
    this.lb = lb;
    this.ub = ub;
    this.integer = integer;
    this.name = (name == null || name.isEmpty()) ? String.format(Locale.ROOT, "auto_v_%09d", index) : name;
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Returns the name of the variable. */
  public String name() {
    return name;
  }

  /** Sets the integrality requirement of the variable. */
  public void setInteger(boolean integer) {
    if (this.integer != integer) {
      this.integer = integer;
      solver.invalidateSolution();
    }
  }

  /** Returns true if the variable is integer. */
  public boolean integer() {
    return integer;
  }

  /**
   * Returns the value of the variable in the current solution.
   *
   * <p>
   * If the variable is integer and the solver handles integer variables, the value is rounded to
   * the nearest integer.
   */
  public double solutionValue() {
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return 0.0;
    }
    return (integer && solver.isMip()) ? MPSolver.round(solutionValue) : solutionValue;
  }

  /** Returns the value of the variable in the current solution, without rounding. */
  public double unroundedSolutionValue() {
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return 0.0;
    }
    return solutionValue;
  }

  /** Returns the index of the variable in the MPSolver::variables_. */
  public int index() {
    return index;
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

  /**
   * Advanced usage: returns the reduced cost of the variable in the current solution (only
   * available for continuous problems).
   */
  public double reducedCost() {
    if (!solver.isContinuous()) {
      return 0.0;
    }
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return 0.0;
    }
    return reducedCost;
  }

  /**
   * Advanced usage: returns the basis status of the variable in the current solution (only
   * available for continuous problems).
   */
  public MPSolver.BasisStatus basisStatus() {
    if (!solver.isContinuous()) {
      return MPSolver.BasisStatus.FREE;
    }
    if (!solver.checkSolutionIsSynchronizedAndExists()) {
      return MPSolver.BasisStatus.FREE;
    }
    return solver.columnStatus(index);
  }

  @Override
  public String toString() {
    return name;
  }
}
