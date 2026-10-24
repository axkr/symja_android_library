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
// Modified for the Symja matheclipse-or-tools module: tests ported from
// ortools/glop/revised_simplex_test.cc (OR-Tools 10.0) to JUnit 5.

package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.util.TimeLimit;

/** Tests of {@link RevisedSimplex} ported from {@code revised_simplex_test.cc}. */
public class RevisedSimplexTest {

  /**
   * {@code (val - target_bound) / dir} underflows to 0.0 for a subnormal right hand side; the
   * iteration must then be treated as degenerate, otherwise the leaving variable is moved to its
   * bound without the step and the basis becomes inconsistent. As upstream (a {@code DCHECK_EQ}),
   * the inconsistency is detected by the Java {@code assert} in the primal pivot, so this test
   * needs assertions enabled (the surefire default).
   */
  @Test
  public void testSubnormalStepToBoundUnderflow() {
    LinearProgram lp = new LinearProgram();
    final int x = lp.createNewVariable();
    final int y = lp.createNewVariable();
    final int r = lp.createNewConstraint();
    lp.setVariableBounds(x, 0.0, 1.0);
    lp.setVariableBounds(y, 0.0, 1.0);
    lp.setObjectiveCoefficient(x, -1.0);
    lp.setObjectiveCoefficient(y, 0.0);
    final double denormMin = Double.MIN_VALUE;
    lp.setConstraintBounds(r, denormMin, denormMin);
    lp.setCoefficient(r, x, 2.0);
    lp.setCoefficient(r, y, 1.0);
    lp.addSlackVariablesWhereNecessary(false);

    BasisState state = new BasisState(new VariableStatus[] {VariableStatus.AT_LOWER_BOUND,
        VariableStatus.BASIC, VariableStatus.FIXED_VALUE});

    RevisedSimplex simplex = new RevisedSimplex();
    simplex.loadStateForNextSolve(state);
    Status status = simplex.solve(lp, TimeLimit.infinite());
    assertTrue(status.ok(), status.toString());
    assertNotEquals(ProblemStatus.ABNORMAL, simplex.getProblemStatus());
    assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
  }

  /**
   * The primal phase I ratio test stops at the peak of the (piecewise linear) decrease of the sum
   * of infeasibilities. Here the reduced cost of x is {@code 0.1 + 0.2 = 0.30000000000000004} and the
   * derivative is flat after the breakpoints of a (0.1) and b (0.2): the remaining rounding error
   * {@code 2.8e-17} must not count as an improvement, otherwise the ratio test goes on to the
   * breakpoint of c and the solve needs a second iteration.
   */
  @Test
  public void testPrimalPhaseIStopsAtFlatPeak() {
    LinearProgram lp = new LinearProgram();
    final int x = lp.createNewVariable();
    final int a = lp.createNewVariable();
    final int b = lp.createNewVariable();
    final int c = lp.createNewVariable();
    lp.setVariableBounds(x, 0.0, 100.0);
    lp.setVariableBounds(a, 1.0, 5.0);
    lp.setVariableBounds(b, 3.0, 10.0);
    lp.setVariableBounds(c, 0.0, 100.0);
    lp.setObjectiveCoefficient(x, 1.0);
    // a = 0.1 x, b = 0.2 x, c = 20 - x
    final int r1 = lp.createNewConstraint();
    lp.setConstraintBounds(r1, 0.0, 0.0);
    lp.setCoefficient(r1, a, 1.0);
    lp.setCoefficient(r1, x, -0.1);
    final int r2 = lp.createNewConstraint();
    lp.setConstraintBounds(r2, 0.0, 0.0);
    lp.setCoefficient(r2, b, 1.0);
    lp.setCoefficient(r2, x, -0.2);
    final int r3 = lp.createNewConstraint();
    lp.setConstraintBounds(r3, 20.0, 20.0);
    lp.setCoefficient(r3, c, 1.0);
    lp.setCoefficient(r3, x, 1.0);
    lp.addSlackVariablesWhereNecessary(false);

    // a and b are basic and below their lower bounds.
    final BasisState state = new BasisState(new VariableStatus[] {VariableStatus.AT_LOWER_BOUND,
        VariableStatus.BASIC, VariableStatus.BASIC, VariableStatus.BASIC,
        VariableStatus.FIXED_VALUE, VariableStatus.FIXED_VALUE, VariableStatus.FIXED_VALUE});

    // One phase I iteration: b leaves at its lower bound (x = 15), not c (x = 20).
    RevisedSimplex simplex = new RevisedSimplex();
    simplex.setParameters(new GlopParameters().setMaxNumberOfIterations(1));
    simplex.loadStateForNextSolve(state);
    assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
    assertEquals(VariableStatus.AT_LOWER_BOUND, simplex.getVariableStatus(b));
    assertEquals(VariableStatus.BASIC, simplex.getVariableStatus(c));
    assertEquals(15.0, simplex.getVariableValue(x), 1e-12);

    // That point is optimal: the full solve needs a single iteration.
    simplex = new RevisedSimplex();
    simplex.loadStateForNextSolve(state);
    assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
    assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
    assertEquals(1, simplex.getNumberOfIterations());
    assertEquals(15.0, simplex.getObjectiveValue(), 1e-12);
    assertEquals(1.5, simplex.getVariableValue(a), 1e-12);
    assertEquals(3.0, simplex.getVariableValue(b), 1e-12);
    assertEquals(5.0, simplex.getVariableValue(c), 1e-12);
  }
}
