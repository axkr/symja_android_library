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
// ortools/glop/preprocessor_test.cc (OR-Tools 10.0) to JUnit 5 (the LP text parser is replaced by
// explicit construction), plus tests of the ShiftVariableBoundsPreprocessor postsolve and rounding.

package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.VariableStatus;

/** Tests of the Glop preprocessors. */
public class PreprocessorTest {

  private static LinearProgram boundedVariables(double[] lower, double[] upper, double[] cost) {
    LinearProgram lp = new LinearProgram();
    for (int i = 0; i < lower.length; ++i) {
      final int col = lp.createNewVariable();
      lp.setVariableBounds(col, lower[i], upper[i]);
      lp.setObjectiveCoefficient(col, cost[i]);
    }
    return lp;
  }

  @Test
  public void testColumnDeletionHelperDeletionsAnyOrder() {
    Preprocessor.ColumnDeletionHelper helper = new Preprocessor.ColumnDeletionHelper();
    helper.markColumnForDeletionWithState(3, 1.0, VariableStatus.FREE);
    helper.markColumnForDeletionWithState(2, 2.0, VariableStatus.FREE);
    helper.markColumnForDeletionWithState(7, 3.0, VariableStatus.FREE);
    assertFalse(helper.isEmpty());
    final boolean[] deleted = helper.getMarkedColumns();
    assertFalse(deleted[0]);
    assertTrue(deleted[2]);
    assertTrue(deleted[3]);
    assertTrue(deleted[7]);
    assertEquals(8, deleted.length);

    ProblemSolution solution = new ProblemSolution(0, 10);
    java.util.Arrays.fill(solution.primalValues, -1.0);
    helper.restoreDeletedColumns(solution);
    assertEquals(13, solution.primalValues.length);
    assertEquals(1.0, solution.primalValues[3]);
    assertEquals(2.0, solution.primalValues[2]);
    assertEquals(3.0, solution.primalValues[7]);
    assertEquals(-1.0, solution.primalValues[0]);
    assertEquals(-1.0, solution.primalValues[12]);
  }

  /** clear() also forgets the stored statuses, a reused helper restores the new ones. */
  @Test
  public void testColumnDeletionHelperClearForgetsStatuses() {
    Preprocessor.ColumnDeletionHelper helper = new Preprocessor.ColumnDeletionHelper();
    helper.markColumnForDeletionWithState(0, 1.0, VariableStatus.AT_UPPER_BOUND);
    helper.markColumnForDeletionWithState(2, 1.0, VariableStatus.AT_UPPER_BOUND);
    helper.clear();
    assertTrue(helper.isEmpty());
    helper.markColumnForDeletion(1);
    ProblemSolution solution = new ProblemSolution(0, 2);
    solution.variableStatuses[0] = VariableStatus.BASIC;
    solution.variableStatuses[1] = VariableStatus.AT_LOWER_BOUND;
    helper.restoreDeletedColumns(solution);
    assertEquals(3, solution.variableStatuses.length);
    assertEquals(VariableStatus.BASIC, solution.variableStatuses[0]);
    assertEquals(VariableStatus.FREE, solution.variableStatuses[1]);
    assertEquals(VariableStatus.AT_LOWER_BOUND, solution.variableStatuses[2]);
  }

  /** The postsolve adds the shift to the BASIC and to the (non-basic) FREE variables. */
  @Test
  public void testShiftVariableBoundsRecoversFreeVariables() {
    LinearProgram lp = boundedVariables(new double[] {2.0, -7.0, 1.0},
        new double[] {5.0, -3.0, 4.0}, new double[] {1.0, 1.0, 1.0});
    ShiftVariableBoundsPreprocessor preprocessor =
        new ShiftVariableBoundsPreprocessor(new GlopParameters());
    assertTrue(preprocessor.run(lp));
    assertEquals(0.0, lp.variableLowerBounds()[0]);
    assertEquals(3.0, lp.variableUpperBounds()[0]);
    assertEquals(-4.0, lp.variableLowerBounds()[1]);
    assertEquals(0.0, lp.variableUpperBounds()[1]);
    assertEquals(2.0 - 3.0 + 1.0, lp.objectiveOffset());

    ProblemSolution solution = new ProblemSolution(0, 3);
    solution.primalValues = new double[] {1.5, -1.0, 0.0};
    solution.variableStatuses = new VariableStatus[] {VariableStatus.FREE, VariableStatus.BASIC,
        VariableStatus.AT_LOWER_BOUND};
    preprocessor.recoverSolution(solution);
    assertEquals(3.5, solution.primalValues[0]);
    assertEquals(-4.0, solution.primalValues[1]);
    assertEquals(1.0, solution.primalValues[2]);
  }

  /** In a MIP context the shift of an integer variable is rounded to the nearest integer. */
  @Test
  public void testShiftVariableBoundsRoundsIntegerOffsets() {
    LinearProgram lp = boundedVariables(new double[] {2.75, -10.0, 1.25},
        new double[] {10.0, -3.5, 10.0}, new double[] {0.0, 0.0, 0.0});
    lp.setVariableType(0, LinearProgram.VariableType.INTEGER);
    lp.setVariableType(1, LinearProgram.VariableType.INTEGER);
    ShiftVariableBoundsPreprocessor preprocessor =
        new ShiftVariableBoundsPreprocessor(new GlopParameters());
    preprocessor.useInMipContext();
    assertTrue(preprocessor.run(lp));
    // 2.75 -> 3, -3.5 -> -4 (halfway away from zero), the continuous 1.25 is not rounded.
    assertEquals(-0.25, lp.variableLowerBounds()[0]);
    assertEquals(7.0, lp.variableUpperBounds()[0]);
    assertEquals(-6.0, lp.variableLowerBounds()[1]);
    assertEquals(0.5, lp.variableUpperBounds()[1]);
    assertEquals(0.0, lp.variableLowerBounds()[2]);
    assertEquals(8.75, lp.variableUpperBounds()[2]);

    ProblemSolution solution = new ProblemSolution(0, 3);
    solution.primalValues = new double[] {1.0, 0.0, 0.5};
    preprocessor.recoverSolution(solution);
    assertEquals(4.0, solution.primalValues[0]);
    assertEquals(-4.0, solution.primalValues[1]);
    assertEquals(1.75, solution.primalValues[2]);
  }

  @Test
  public void testRoundHalfAwayFromZero() {
    assertEquals(3.0, ShiftVariableBoundsPreprocessor.roundHalfAwayFromZero(2.5));
    assertEquals(-3.0, ShiftVariableBoundsPreprocessor.roundHalfAwayFromZero(-2.5));
    assertEquals(0.0, ShiftVariableBoundsPreprocessor.roundHalfAwayFromZero(0.49999999999999994));
    assertEquals(2.0, ShiftVariableBoundsPreprocessor.roundHalfAwayFromZero(2.0000001));
    assertEquals(-2.0, ShiftVariableBoundsPreprocessor.roundHalfAwayFromZero(-1.9999999));
  }

  /** The OR-Tools 10.0 fields of GlopParameters: defaults and text format. */
  @Test
  public void testGlopParameters10Fields() {
    GlopParameters params = new GlopParameters();
    assertFalse(params.getPrimalPolish());
    assertFalse(params.getDualPolish());
    assertTrue(params.getUseShiftVariableBoundsPreprocessor());
    assertTrue(params.getUseBoundPropagationPreprocessor());
    assertEquals(3, GlopParameters.ScalingAlgorithm.RUIZ.ordinal());
    assertTrue(params.mergeFromString("scaling_method: RUIZ primal_polish: true "
        + "use_singleton_preprocessor: false use_dualizer_preprocessor: false"));
    assertEquals(GlopParameters.ScalingAlgorithm.RUIZ, params.getScalingMethod());
    assertTrue(params.getPrimalPolish());
    assertFalse(params.getUseSingletonPreprocessor());
    assertFalse(params.getUseDualizerPreprocessor());
    assertTrue(params.toString().contains("use_singleton_preprocessor: false"));
    GlopParameters copy = params.copy();
    assertFalse(copy.getUseSingletonPreprocessor());
    assertTrue(copy.getPrimalPolish());
  }

  // ConfigurablePreprocessorFlagsTest

  @Test
  public void testDisableFixedVariablePreprocessor() {
    // min 0; 1 <= x1 <= 5; 3 <= x2 <= 3 (fixed); 4 <= x3 <= 8
    final double[] lower = {1, 3, 4};
    final double[] upper = {5, 3, 8};
    final double[] cost = {0, 0, 0};
    LinearProgram lp = boundedVariables(lower, upper, cost);
    assertTrue(new FixedVariablePreprocessor(new GlopParameters()).run(lp));
    assertEquals(2, lp.numVariables());

    lp = boundedVariables(lower, upper, cost);
    GlopParameters params = new GlopParameters().setUseFixedVariablePreprocessor(false);
    FixedVariablePreprocessor preprocessor = new FixedVariablePreprocessor(params);
    assertFalse(preprocessor.run(lp));
    assertEquals(ProblemStatus.INIT, preprocessor.status());
    assertEquals(3, lp.numVariables());
  }

  @Test
  public void testDisableEmptyColumnPreprocessor() {
    // min 0 x1 + 2 x2; 1 <= x1 <= 5; 2 <= x2 <= 6
    LinearProgram lp =
        boundedVariables(new double[] {1, 2}, new double[] {5, 6}, new double[] {0, 2});
    GlopParameters params = new GlopParameters().setUseEmptyColumnPreprocessor(false);
    EmptyColumnPreprocessor preprocessor = new EmptyColumnPreprocessor(params);
    assertFalse(preprocessor.run(lp));
    assertEquals(ProblemStatus.INIT, preprocessor.status());
    assertEquals(2, lp.numVariables());
  }

  @Test
  public void testDisableFreeConstraintPreprocessor() {
    // min x1 + x2; -inf <= x1 + x2 <= inf; 1 <= x1 <= 5; 2 <= x2 <= 6
    LinearProgram lp =
        boundedVariables(new double[] {1, 2}, new double[] {5, 6}, new double[] {1, 1});
    final int row = lp.createNewConstraint();
    lp.setConstraintBounds(row, -LpTypes.INFINITY, LpTypes.INFINITY);
    lp.setCoefficient(row, 0, 1.0);
    lp.setCoefficient(row, 1, 1.0);
    GlopParameters params = new GlopParameters().setUseFreeConstraintPreprocessor(false);
    FreeConstraintPreprocessor preprocessor = new FreeConstraintPreprocessor(params);
    assertFalse(preprocessor.run(lp));
    assertEquals(ProblemStatus.INIT, preprocessor.status());
    assertEquals(1, lp.numConstraints());
  }

  @Test
  public void testDisableShiftVariableBounds() {
    // min x1; 10 <= x1 <= 20
    LinearProgram lp = boundedVariables(new double[] {10}, new double[] {20}, new double[] {1});
    GlopParameters params = new GlopParameters().setUseShiftVariableBoundsPreprocessor(false);
    ShiftVariableBoundsPreprocessor preprocessor = new ShiftVariableBoundsPreprocessor(params);
    assertFalse(preprocessor.run(lp));
    assertEquals(ProblemStatus.INIT, preprocessor.status());
    assertEquals(10.0, lp.variableLowerBounds()[0]);
  }

  /** The other switches of the ported preprocessors, and an LP solved with all of them off. */
  @Test
  public void testDisableOtherPreprocessors() {
    // min x + y; 0 <= 0 <= 1 (empty); 2 <= x <= 5 (singleton); x - y >= 4; 0 <= x, y <= 10.
    LinearProgram lp =
        boundedVariables(new double[] {0, 0}, new double[] {10, 10}, new double[] {1, 1});
    final int empty = lp.createNewConstraint();
    lp.setConstraintBounds(empty, 0.0, 1.0);
    final int singleton = lp.createNewConstraint();
    lp.setConstraintBounds(singleton, 2.0, 5.0);
    lp.setCoefficient(singleton, 0, 1.0);
    final int both = lp.createNewConstraint();
    lp.setConstraintBounds(both, 4.0, LpTypes.INFINITY);
    lp.setCoefficient(both, 0, 1.0);
    lp.setCoefficient(both, 1, -1.0);
    lp.cleanUp();
    GlopParameters off = new GlopParameters().setUseEmptyConstraintPreprocessor(false)
        .setUseSingletonPreprocessor(false).setUseSingletonColumnSignPreprocessor(false);
    LinearProgram copy = new LinearProgram();
    copy.populateFromLinearProgram(lp);
    assertFalse(new EmptyConstraintPreprocessor(off).run(copy));
    assertFalse(new SingletonPreprocessor(off).run(copy));
    assertFalse(new SingletonColumnSignPreprocessor(off).run(copy));
    assertEquals(3, copy.numConstraints());
    assertEquals(2, copy.numVariables());
    assertTrue(new EmptyConstraintPreprocessor(new GlopParameters()).run(copy));
    assertEquals(2, copy.numConstraints());

    GlopParameters allOff = off.copy().setUseFixedVariablePreprocessor(false)
        .setUseFreeConstraintPreprocessor(false).setUseEmptyColumnPreprocessor(false)
        .setUseShiftVariableBoundsPreprocessor(false);
    for (GlopParameters params : new GlopParameters[] {new GlopParameters(), allOff}) {
      LPSolver solver = new LPSolver();
      solver.setParameters(params);
      assertEquals(ProblemStatus.OPTIMAL, solver.solve(lp));
      assertEquals(4.0, solver.getObjectiveValue(), 1e-9);
      assertEquals(4.0, solver.variableValues()[0], 1e-9);
      assertEquals(0.0, solver.variableValues()[1], 1e-9);
    }
  }
}
