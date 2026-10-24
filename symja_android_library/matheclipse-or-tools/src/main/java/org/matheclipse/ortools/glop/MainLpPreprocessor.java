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
// Modified for the Symja matheclipse-or-tools module: ported from
// ortools/glop/preprocessor.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.ProblemSolution;

/**
 * The presolve driver of Glop: runs the preprocessors in sequence, keeps those that need a
 * postsolve on a stack and undoes them in reverse order in {@link #recoverSolution}.
 *
 * <p>
 * With <code>use_preprocessing</code> the sequence is: {@link ShiftVariableBoundsPreprocessor},
 * then up to 20 passes of {@link FixedVariablePreprocessor}, {@link SingletonPreprocessor} and
 * {@link FreeConstraintPreprocessor} until nothing changes, then
 * {@link EmptyColumnPreprocessor}, {@link EmptyConstraintPreprocessor} and
 * {@link SingletonColumnSignPreprocessor}. The {@link ScalingPreprocessor} runs if
 * <code>use_scaling</code> is set (independently of <code>use_preprocessing</code>).
 *
 * <p>
 * <b>Not ported</b> (the corresponding steps of the C++ sequence are skipped, which only makes the
 * presolve weaker, never wrong): ForcingAndImpliedFreeConstraintPreprocessor,
 * ImpliedFreePreprocessor, UnconstrainedVariablePreprocessor, DoubletonFreeColumnPreprocessor,
 * DoubletonEqualityRowPreprocessor, ProportionalColumnPreprocessor, ProportionalRowPreprocessor and
 * DualizerPreprocessor (so <code>solve_dual_problem</code> is ignored).
 * {@link ToMinimizationPreprocessor} and {@link AddSlackVariablesPreprocessor} are ported but, as
 * in C++, not part of the default sequence.
 */
public final class MainLpPreprocessor extends Preprocessor {

  private final List<Preprocessor> preprocessors = new ArrayList<>();
  private SolverLogger logger = new SolverLogger();
  private int initialNumRows;
  private int initialNumCols;
  private long initialNumEntries;

  /** Creates the driver. */
  public MainLpPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  /** Sets the logger. */
  public void setLogger(SolverLogger logger) {
    this.logger = logger;
  }

  @Override
  public boolean run(LinearProgram lp) {
    logger.log("");
    logger.log("Starting presolve...");
    initialNumRows = lp.numConstraints();
    initialNumCols = lp.numVariables();
    initialNumEntries = lp.numEntries();
    if (parameters.getUsePreprocessing()) {
      runAndPushIfRelevant(new ShiftVariableBoundsPreprocessor(parameters),
          "ShiftVariableBoundsPreprocessor", lp);
      final int kMaxNumPasses = 20;
      for (int i = 0; i < kMaxNumPasses; ++i) {
        final int oldStackSize = preprocessors.size();
        runAndPushIfRelevant(new FixedVariablePreprocessor(parameters),
            "FixedVariablePreprocessor", lp);
        runAndPushIfRelevant(new SingletonPreprocessor(parameters), "SingletonPreprocessor", lp);
        runAndPushIfRelevant(new FreeConstraintPreprocessor(parameters),
            "FreeConstraintPreprocessor", lp);
        if (preprocessors.size() == oldStackSize) {
          logger.log("Reached fixed point after presolve pass #", i);
          break;
        }
      }
      runAndPushIfRelevant(new EmptyColumnPreprocessor(parameters), "EmptyColumnPreprocessor",
          lp);
      runAndPushIfRelevant(new EmptyConstraintPreprocessor(parameters),
          "EmptyConstraintPreprocessor", lp);
      runAndPushIfRelevant(new SingletonColumnSignPreprocessor(parameters),
          "SingletonColumnSignPreprocessor", lp);
    }
    runAndPushIfRelevant(new ScalingPreprocessor(parameters), "ScalingPreprocessor", lp);
    return !preprocessors.isEmpty();
  }

  private void runAndPushIfRelevant(Preprocessor preprocessor, String name, LinearProgram lp) {
    if (status != ProblemStatus.INIT || timeLimit.limitReached()) {
      return;
    }
    final double startTime = timeLimit.getElapsedTime();
    preprocessor.setTimeLimit(timeLimit);
    if (inMipContext) {
      preprocessor.useInMipContext();
    }
    if (lp.numVariables() == 0 && lp.numConstraints() == 0) {
      status = ProblemStatus.OPTIMAL;
      return;
    }
    if (preprocessor.run(lp)) {
      final long newNumEntries = lp.numEntries();
      final double preprocessTime = timeLimit.getElapsedTime() - startTime;
      logger.log(String.format(Locale.ROOT, "%-45s: %d(%d) rows, %d(%d) columns, %d(%d) entries. (%fs)", name,
          lp.numConstraints(), lp.numConstraints() - initialNumRows, lp.numVariables(),
          lp.numVariables() - initialNumCols, newNumEntries, newNumEntries - initialNumEntries,
          preprocessTime));
      status = preprocessor.status();
      preprocessors.add(preprocessor);
    } else {
      status = preprocessor.status();
      if (status != ProblemStatus.INIT) {
        logger.log(name, " detected that the problem is ", status);
      }
    }
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    for (int i = preprocessors.size() - 1; i >= 0; --i) {
      preprocessors.get(i).recoverSolution(solution);
    }
  }

  /** Same as {@link #recoverSolution} but releases the preprocessors. */
  public void destructiveRecoverSolution(ProblemSolution solution) {
    while (!preprocessors.isEmpty()) {
      preprocessors.remove(preprocessors.size() - 1).recoverSolution(solution);
    }
  }
}
