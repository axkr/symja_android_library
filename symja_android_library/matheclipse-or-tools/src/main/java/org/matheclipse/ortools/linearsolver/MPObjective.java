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

import java.util.LinkedHashMap;
import java.util.Map;

/** A class to express a linear objective. */
public class MPObjective {

  private final MPSolver solver;
  final Map<MPVariable, Double> coefficients = new LinkedHashMap<>();
  private double offset;
  private boolean maximize;

  MPObjective(MPSolver solver) {
    this.solver = solver;
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Clears the offset, all variables and coefficients, and the optimization direction. */
  public void clear() {
    coefficients.clear();
    offset = 0.0;
    maximize = false;
    solver.invalidateSolution();
  }

  /**
   * Sets the coefficient of the variable in the objective.
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
      if (old == null || old == 0.0) {
        return;
      }
      coefficients.put(var, 0.0);
    } else {
      coefficients.put(var, coeff);
    }
    solver.invalidateSolution();
  }

  /**
   * Gets the coefficient of a given variable in the objective (0 if the variable does not appear
   * in the objective).
   */
  public double getCoefficient(MPVariable var) {
    if (var == null) {
      return 0.0;
    }
    final Double c = coefficients.get(var);
    return c == null ? 0.0 : c;
  }

  /** Sets the constant term in the objective. */
  public void setOffset(double value) {
    offset = value;
    solver.invalidateSolution();
  }

  /** Gets the constant term in the objective. */
  public double offset() {
    return offset;
  }

  /** Sets the optimization direction (maximize: true or minimize: false). */
  public void setOptimizationDirection(boolean maximize) {
    this.maximize = maximize;
    solver.invalidateSolution();
  }

  /** Sets the optimization direction to minimize. */
  public void setMinimization() {
    setOptimizationDirection(false);
  }

  /** Sets the optimization direction to maximize. */
  public void setMaximization() {
    setOptimizationDirection(true);
  }

  /** Is the optimization direction set to maximize? */
  public boolean maximization() {
    return maximize;
  }

  /** Is the optimization direction set to minimize? */
  public boolean minimization() {
    return !maximize;
  }

  /**
   * Returns the objective value of the best solution found so far.
   *
   * <p>
   * It is the optimal objective value if the problem has been solved to optimality.
   */
  public double value() {
    return solver.objectiveValue();
  }

  /**
   * Returns the best objective bound.
   *
   * <p>
   * In case of minimization, it is a lower bound on the objective value of the optimal integer
   * solution. Only available for discrete problems.
   */
  public double bestBound() {
    return solver.bestObjectiveBound();
  }
}
