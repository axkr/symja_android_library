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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/lp_types.h C++
// to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

/**
 * The status of a linear program after a solve.
 *
 * <ul>
 * <li>OPTIMAL: an optimal solution was found.
 * <li>PRIMAL_INFEASIBLE, DUAL_UNBOUNDED: the problem has no feasible solution.
 * <li>DUAL_INFEASIBLE, PRIMAL_UNBOUNDED: the problem is unbounded (if it is feasible).
 * <li>INFEASIBLE_OR_UNBOUNDED: one of the two, detected by the presolve.
 * <li>INIT: the solver did not start or did not finish.
 * <li>PRIMAL_FEASIBLE, DUAL_FEASIBLE: a limit was reached with a primal (dual) feasible point.
 * <li>ABNORMAL, INVALID_PROBLEM, IMPRECISE: errors.
 * </ul>
 */
public enum ProblemStatus {
  OPTIMAL,
  PRIMAL_INFEASIBLE,
  DUAL_INFEASIBLE,
  INFEASIBLE_OR_UNBOUNDED,
  PRIMAL_UNBOUNDED,
  DUAL_UNBOUNDED,
  INIT,
  PRIMAL_FEASIBLE,
  DUAL_FEASIBLE,
  ABNORMAL,
  INVALID_PROBLEM,
  IMPRECISE;
}
