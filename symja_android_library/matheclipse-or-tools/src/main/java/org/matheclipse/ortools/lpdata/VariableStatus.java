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

package org.matheclipse.ortools.lpdata;

/**
 * The status of a variable in a basic solution of the simplex algorithm.
 *
 * <ul>
 * <li>BASIC: the variable is part of the basis, its value is determined by the non-basic ones.
 * <li>FIXED_VALUE: a non-basic variable whose lower and upper bounds are equal.
 * <li>AT_LOWER_BOUND, AT_UPPER_BOUND: a non-basic variable at one of its bounds.
 * <li>FREE: a non-basic variable without bounds, its value is zero.
 * </ul>
 */
public enum VariableStatus {
  BASIC,
  FIXED_VALUE,
  AT_LOWER_BOUND,
  AT_UPPER_BOUND,
  FREE;
}
