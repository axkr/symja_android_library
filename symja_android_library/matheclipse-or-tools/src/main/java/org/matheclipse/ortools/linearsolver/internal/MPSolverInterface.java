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
// ortools/linear_solver/linear_solver.h (MPSolverInterface) C++ to Java, see NOTICE in the module
// root.

package org.matheclipse.ortools.linearsolver.internal;

import org.matheclipse.ortools.linearsolver.MPSolver;

/**
 * The interface between {@link MPSolver} and an underlying solver. Unlike the C++ class, which is
 * kept in sync with the model through incremental calls, a solve receives a complete snapshot of
 * the model as an {@link MPModel} (the C++ Glop interface re-extracts the whole model on each solve
 * too) and returns a {@link SolveResult}. Implementations may keep warm-start information between
 * solves until {@link #reset()}.
 */
public abstract class MPSolverInterface {

  /** The parameters of one solve. */
  public static final class SolveParameters {
    /** The relative MIP gap. */
    public double relativeMipGap = 1e-4;
    /** The primal tolerance. */
    public double primalTolerance = 1e-7;
    /** The dual tolerance. */
    public double dualTolerance = 1e-7;
    /** The presolve mode (0 off, 1 on). */
    public int presolve = 1;
    /** The LP algorithm (-1 default, 10 dual, 11 primal, 12 barrier). */
    public int lpAlgorithm = -1;
    /** The incrementality (0 off, 1 on). */
    public int incrementality = 1;
    /** The scaling (-1 default, 0 off, 1 on). */
    public int scaling = -1;
    /** The time limit in milliseconds, 0 for no limit. */
    public long timeLimitMillis;
    /** Whether the solver logs its progress. */
    public boolean enableOutput;
    /** The solver specific parameters in text format. */
    public String solverSpecificParameters = "";
    /** The number of threads. */
    public int numThreads = 1;
    /** An optional callback receiving the log lines. */
    public java.util.function.Consumer<String> logCallback;
  }

  /** The result of one solve. */
  public static final class SolveResult {
    /** The status. */
    public MPSolver.ResultStatus status = MPSolver.ResultStatus.NOT_SOLVED;
    /** The objective value (with offset) of the returned solution. */
    public double objectiveValue;
    /** The best objective bound (MIP only). */
    public double bestObjectiveBound;
    /** The variable values, indexed like the model variables. */
    public double[] variableValues = new double[0];
    /** The reduced costs (LP only). */
    public double[] reducedCosts = new double[0];
    /** The dual values (LP only). */
    public double[] dualValues = new double[0];
    /** The basis statuses of the variables (LP only). */
    public MPSolver.BasisStatus[] variableStatuses = new MPSolver.BasisStatus[0];
    /** The basis statuses of the constraints (LP only). */
    public MPSolver.BasisStatus[] constraintStatuses = new MPSolver.BasisStatus[0];
    /** The number of simplex iterations. */
    public long iterations;
    /** The number of branch-and-bound nodes, -1 if not applicable. */
    public long nodes = -1;
    /** The solve time in seconds. */
    public double wallTimeSeconds;
  }

  /** Solves the model. */
  public abstract SolveResult solve(MPModel model, SolveParameters parameters);

  /** Returns true if the solver handles integer variables. */
  public abstract boolean isMip();

  /** Returns true if dual values, reduced costs and basis statuses are available. */
  public boolean isContinuous() {
    return !isMip();
  }

  /** A string describing the solver and its version. */
  public abstract String solverVersion();

  /** Forgets the warm start information. */
  public void reset() {}

  /** Requests the interruption of the current solve; returns true if supported. */
  public boolean interruptSolve() {
    return false;
  }

  /** Checks solver specific parameters given in text format; returns false if invalid. */
  public boolean checkSolverSpecificParameters(String parameters) {
    return parameters == null || parameters.isEmpty();
  }

  /** Sets the number of threads; returns false if not supported. */
  public boolean setNumThreads(int numThreads) {
    return false;
  }

  /** The exact condition number of the last basis, 0.0 if not supported. */
  public double computeExactConditionNumber() {
    return 0.0;
  }
}
