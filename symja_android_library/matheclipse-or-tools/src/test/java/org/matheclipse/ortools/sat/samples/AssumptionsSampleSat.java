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
// Modified for the Symja matheclipse-or-tools module: sample ported to run as a JUnit test, see NOTICE in the module root.

// [START program]
package org.matheclipse.ortools.sat.samples;

// [START import]
import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.Literal;

// [END import]

/** Minimal CP-SAT example to showcase assumptions. */
public class AssumptionsSampleSat {
  public static void main(String[] args) {
    Loader.loadNativeLibraries();
    // Create the model.
    // [START model]
    CpModel model = new CpModel();
    // [END model]

    // Create the variables.
    // [START variables]
    IntVar x = model.newIntVar(0, 10, "x");
    IntVar y = model.newIntVar(0, 10, "y");
    IntVar z = model.newIntVar(0, 10, "z");
    Literal a = model.newBoolVar("a");
    Literal b = model.newBoolVar("b");
    Literal c = model.newBoolVar("c");
    // [END variables]

    // Creates the constraints.
    // [START constraints]
    model.addGreaterThan(x, y).onlyEnforceIf(a);
    model.addGreaterThan(y, z).onlyEnforceIf(b);
    model.addGreaterThan(z, x).onlyEnforceIf(c);
    // [END constraints]

    // Add assumptions
    model.addAssumptions(new Literal[] {a, b, c});

    // Create a solver and solve the model.
    // [START solve]
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);
    // [END solve]

    // Print solution.
    // [START print_solution]
    // Check that the problem is infeasible.
    if (status == CpSolverStatus.INFEASIBLE) {
      System.out.println(solver.sufficientAssumptionsForInfeasibility());
    }
    // [END print_solution]
  }

  private AssumptionsSampleSat() {}
}
// [END program]
