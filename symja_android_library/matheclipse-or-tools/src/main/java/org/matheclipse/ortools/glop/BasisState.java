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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/variables_info.h
// C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import org.matheclipse.ortools.lpdata.VariableStatus;

/**
 * The statuses of all the variables (including the slack variables) of a simplex basis. It can be
 * given to {@link RevisedSimplex#loadStateForNextSolve(BasisState)} to warm-start a solve.
 */
public final class BasisState {

  /** The variable statuses, indexed by column; empty means no state. */
  public VariableStatus[] statuses = new VariableStatus[0];

  /** Creates an empty state. */
  public BasisState() {}

  /** Creates a state holding a copy of the given statuses. */
  public BasisState(VariableStatus[] statuses) {
    this.statuses = statuses.clone();
  }

  /** Returns true if this state is empty. */
  public boolean isEmpty() {
    return statuses.length == 0;
  }

  /** Returns a deep copy. */
  public BasisState copy() {
    return new BasisState(statuses);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BasisState && Arrays.equals(statuses, ((BasisState) o).statuses);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(statuses);
  }
}
