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

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.LinearExpr;

/**
 * In a field of rabbits and pheasants, there are 20 heads and 56 legs. How many rabbits and
 * pheasants are there?
 */
public class RabbitsAndPheasantsSat {
  public static void main(String[] args) throws Exception {
    Loader.loadNativeLibraries();
    // Creates the model.
    CpModel model = new CpModel();
    // Creates the variables.
    IntVar r = model.newIntVar(0, 100, "r");
    IntVar p = model.newIntVar(0, 100, "p");
    // 20 heads.
    model.addEquality(LinearExpr.newBuilder().add(r).add(p), 20);
    // 56 legs.
    model.addEquality(LinearExpr.newBuilder().addTerm(r, 4).addTerm(p, 2), 56);

    // Creates a solver and solves the model.
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);

    if (status == CpSolverStatus.OPTIMAL) {
      System.out.println(solver.value(r) + " rabbits, and " + solver.value(p) + " pheasants");
    }
  }
}
// [END program]
