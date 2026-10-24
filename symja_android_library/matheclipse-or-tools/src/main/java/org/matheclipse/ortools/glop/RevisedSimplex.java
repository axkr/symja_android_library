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
// ortools/glop/revised_simplex.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ScatteredVector;
import org.matheclipse.ortools.lpdata.SparseColumn;
import org.matheclipse.ortools.lpdata.SparseMatrix;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.lpdata.VariableType;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The revised simplex algorithm of Glop, with a primal and a dual simplex.
 *
 * <p>
 * The problem is internally in the equation form A.x = 0 where the last columns of A are the slack
 * variables of the constraints (with bounds -ub(row) &lt;= s &lt;= -lb(row)). The primal simplex
 * uses a composite phase I with the costs +-1 of the infeasible basic variables, the Harris ratio
 * test with bound flips and a steepest edge, Devex or Dantzig pricing. The dual simplex uses the
 * dedicated dual phase I of Koberstein and Suhl (or the artificial bounding of Maros), the Harris
 * ratio test with bound flipping, dual steepest edge pricing and optional cost perturbation and
 * shifting. After the optimization the shifts and perturbations are removed and the solution is
 * re-optimized with the other algorithm if needed.
 *
 * <p>
 * Solves can be warm-started: by default the state of the last solve is reused when only the
 * bounds (dual simplex) or only the objective (primal simplex) changed, and a given
 * {@link BasisState} can be loaded with {@link #loadStateForNextSolve(BasisState)}.
 *
 * <p>
 * Not ported: the "push" phase towards a vertex used when starting values are given
 * ({@link #setStartingVariableValuesForNextSolve(double[])} only sets the values of the FREE
 * variables) and the MIP polishing of the final basis (the parameters <code>primal_polish</code>
 * and <code>dual_polish</code> are accepted and ignored).
 *
 * <p>
 * OR-Tools 10.0: the port follows the 10.0 primal simplex (an underflowed zero step to the bound
 * is a degenerate iteration, the phase I ratio test stops when the improvement is below
 * <code>harris_tolerance_ratio * primal_feasibility_tolerance</code>, FREE statuses of columns
 * with a starting value are kept on warm start). Two 10.0 changes are deliberately not ported:
 * the dual phase I calling the primal minimization (upstream commit 7be0b5a936, a regression of
 * the dual phase I), and the Farkas dual ray check that turns PRIMAL_INFEASIBLE into IMPRECISE
 * (upstream commit 2aacd0818a), which would make infeasible answers with residuals between 1e-8
 * and 1e-6 ABNORMAL.
 */
public final class RevisedSimplex {

  /** A mutable reference to an int array that can be replaced. */
  static final class IntArrayRef {
    private int[] a = new int[0];

    int[] get() {
      return a;
    }

    void set(int[] a) {
      this.a = a;
    }
  }

  /** A mutable reference to a double array that can be replaced. */
  static final class DoubleArrayRef {
    private double[] a = new double[0];

    double[] get() {
      return a;
    }

    void set(double[] a) {
      this.a = a;
    }
  }

  /** A mutable reference to the current parameters. */
  static final class ParametersRef {
    private GlopParameters p = new GlopParameters();

    GlopParameters get() {
      return p;
    }

    void set(GlopParameters p) {
      this.p = p;
    }
  }

  private enum Phase {
    FEASIBILITY, OPTIMIZATION, PUSH
  }

  private ProblemStatus problemStatus = ProblemStatus.INIT;
  private int numRows;
  private int numCols;
  private int firstSlackCol;

  private final CompactSparseMatrix compactMatrix = new CompactSparseMatrix();
  private final CompactSparseMatrix transposedMatrix = new CompactSparseMatrix();

  private double primalObjectiveLimit;
  private double dualObjectiveLimit;
  private final DoubleArrayRef objective = new DoubleArrayRef();
  private double objectiveOffset;
  private double objectiveScalingFactor = 1.0;

  private double[] dualInfeasibilityImprovementDirection = new double[0];
  private int numDualInfeasiblePositions;
  private final ScatteredVector initiallyAllZeroScratchpad = new ScatteredVector();

  private final IntArrayRef basis = new IntArrayRef();

  private double solutionObjectiveValue;
  private double[] solutionDualValues = new double[0];
  private double[] solutionReducedCosts = new double[0];
  private double[] solutionPrimalRay = new double[0];
  private double[] solutionDualRay = new double[0];
  private double[] solutionDualRayRowCombination = new double[0];
  private BasisState solutionState = new BasisState();
  private boolean solutionStateHasBeenSetExternally;
  private double[] variableStartingValues = new double[0];
  private boolean transposeWasChanged = false;

  private final ScatteredVector direction = new ScatteredVector();
  private double directionInfinityNorm;
  private double[] error = new double[0];

  private final Random random = new Random(1);
  private final SolverLogger defaultLogger = new SolverLogger();
  private SolverLogger logger = defaultLogger;

  private final ParametersRef parameters = new ParametersRef();
  private GlopParameters initialParameters = new GlopParameters();

  private final BasisFactorization basisFactorization;
  private final VariablesInfo variablesInfo;
  private final PrimalEdgeNorms primalEdgeNorms;
  private final DualEdgeNorms dualEdgeNorms;
  private final DynamicMaximum dualPrices;
  private final VariableValues variableValues;
  private final UpdateRow updateRow;
  private final ReducedCosts reducedCosts;
  private final EnteringVariable enteringVariable;
  private final PrimalPrices primalPrices;

  private double[] dualPricingVector = new double[0];
  private final List<Integer> boundFlipCandidates = new ArrayList<>();

  private long numIterations;
  private long numFeasibilityIterations;
  private long numOptimizationIterations;
  private long numPushIterations;
  private long numUpdatePriceOperations;
  private double totalTime;
  private double feasibilityTime;
  private double optimizationTime;
  private double lastDeterministicTimeUpdate;

  private final LuFactorization testLu = new LuFactorization();
  private int numConsecutiveDegenerateIterations;
  private Phase phase = Phase.FEASIBILITY;
  private boolean objectiveLimitReached;
  private final SparseColumn leavingCandidates = new SparseColumn();
  private final List<Integer> equivalentLeavingChoices = new ArrayList<>();

  /** Creates a solver with the default parameters. */
  public RevisedSimplex() {
    basisFactorization = new BasisFactorization(compactMatrix, basis);
    variablesInfo = new VariablesInfo(compactMatrix);
    primalEdgeNorms = new PrimalEdgeNorms(compactMatrix, variablesInfo, basisFactorization);
    dualEdgeNorms = new DualEdgeNorms(basisFactorization);
    dualPrices = new DynamicMaximum(random);
    variableValues = new VariableValues(parameters, compactMatrix, basis, variablesInfo,
        basisFactorization, dualEdgeNorms, dualPrices);
    updateRow = new UpdateRow(compactMatrix, transposedMatrix, variablesInfo, basis,
        basisFactorization);
    reducedCosts =
        new ReducedCosts(compactMatrix, objective, basis, variablesInfo, basisFactorization, random);
    enteringVariable = new EnteringVariable(variablesInfo, random, reducedCosts);
    primalPrices = new PrimalPrices(random, variablesInfo, primalEdgeNorms, reducedCosts);
    setParameters(new GlopParameters());
  }

  /** Sets the parameters used by the next solve. */
  public void setParameters(GlopParameters params) {
    random.setSeed(params.getRandomSeed());
    initialParameters = params.copy();
    parameters.set(params.copy());
    propagateParameters();
  }

  /** The parameters. */
  public GlopParameters getParameters() {
    return parameters.get();
  }

  /** Uses the given logger. */
  public void setLogger(SolverLogger logger) {
    this.logger = logger;
  }

  private void propagateParameters() {
    final GlopParameters p = parameters.get();
    basisFactorization.setParameters(p);
    enteringVariable.setParameters(p);
    reducedCosts.setParameters(p);
    dualEdgeNorms.setParameters(p);
    primalEdgeNorms.setParameters(p);
    updateRow.setParameters(p);
  }

  /** The next solve behaves as if the object was just created. */
  public void clearStateForNextSolve() {
    solutionState = new BasisState();
    variableStartingValues = new double[0];
  }

  /** Uses the given basis state to warm-start the next solve. */
  public void loadStateForNextSolve(BasisState state) {
    if (Arrays.equals(state.statuses, solutionState.statuses)) {
      return;
    }
    solutionState = state.copy();
    solutionStateHasBeenSetExternally = true;
  }

  /** The starting values of the FREE variables for the next solve. */
  public void setStartingVariableValuesForNextSolve(double[] values) {
    variableStartingValues = values.clone();
  }

  /**
   * Solves the linear program. It may be given in equation form (with slack columns, see
   * {@link LinearProgram#addSlackVariablesWhereNecessary(boolean)}) or not; in the latter case the
   * slacks are added internally. The result is available through the getters.
   */
  public Status solve(LinearProgram lp, TimeLimit timeLimit) {
    final double startTime = timeLimit.getElapsedTime();
    defaultLogger.enableLogging(parameters.get().getLogSearchProgress());
    defaultLogger.setLogToStdOut(parameters.get().getLogToStdout());
    Status status = initialize(lp);
    if (!status.ok()) {
      return status;
    }
    return solveInternal(startTime, lp.isMaximizationProblem(), lp.objectiveCoefficients(),
        timeLimit);
  }

  private Status solveInternal(double startTime, boolean isMaximizationProblem,
      double[] objectiveCoefficients, TimeLimit timeLimit) {
    try {
      return solveInternalImpl(startTime, isMaximizationProblem, objectiveCoefficients,
          timeLimit);
    } finally {
      advanceDeterministicTime(timeLimit);
    }
  }

  private Status solveInternalImpl(double startTime, boolean isMaximizationProblem,
      double[] objectiveCoefficients, TimeLimit timeLimit) {
    final GlopParameters params = parameters.get();
    logger.log("");
    primalEdgeNorms.setTimeLimit(timeLimit);
    dualEdgeNorms.setTimeLimit(timeLimit);
    if (logger.loggingIsEnabled()) {
      displayBasicVariableStatistics();
    }
    dualInfeasibilityImprovementDirection = new double[0];
    updateRow.invalidate();
    testLu.clear();
    problemStatus = ProblemStatus.INIT;
    phase = Phase.FEASIBILITY;
    numIterations = 0;
    numFeasibilityIterations = 0;
    numOptimizationIterations = 0;
    numPushIterations = 0;
    feasibilityTime = 0.0;
    optimizationTime = 0.0;
    totalTime = 0.0;
    solutionStateHasBeenSetExternally = true;

    final boolean useDual = params.getUseDualSimplex();
    logger.log("");
    primalEdgeNorms.setPricingRule(params.getFeasibilityRule());
    Status status;
    if (useDual) {
      if (params.getPerturbCostsInDualSimplex()) {
        reducedCosts.perturbCosts();
      }
      if (params.getUseDedicatedDualFeasibilityAlgorithm()) {
        variablesInfo.makeBoxedVariableRelevant(false);
        status = dualMinimize(phase == Phase.FEASIBILITY, timeLimit);
        if (!status.ok()) {
          return status;
        }
        if (problemStatus != ProblemStatus.DUAL_INFEASIBLE) {
          status = basisFactorization.refactorize();
          if (!status.ok()) {
            return status;
          }
          permuteBasis();
          variablesInfo.makeBoxedVariableRelevant(true);
          reducedCosts.makeReducedCostsPrecise();
          makeBoxedVariableDualFeasible(bitSetToList(variablesInfo.getNonBasicBoxedVariables()),
              false);
          variableValues.recomputeBasicVariableValues();
        }
      } else {
        reducedCosts.makeReducedCostsPrecise();
        boolean[] refactorize = {reducedCosts.needsBasisRefactorization()};
        status = refactorizeBasisIfNeeded(refactorize);
        if (!status.ok()) {
          return status;
        }
        final double initialInfeasibility =
            reducedCosts.computeMaximumDualInfeasibilityOnNonBoxedVariables();
        if (initialInfeasibility < reducedCosts.getDualFeasibilityTolerance()) {
          logger.log("Initial basis is dual feasible.");
          problemStatus = ProblemStatus.DUAL_FEASIBLE;
          makeBoxedVariableDualFeasible(bitSetToList(variablesInfo.getNonBasicBoxedVariables()),
              false);
          variableValues.recomputeBasicVariableValues();
        } else {
          variablesInfo.transformToDualPhaseIProblem(reducedCosts.getDualFeasibilityTolerance(),
              reducedCosts.getReducedCosts());
          variableValues.resetAllNonBasicVariableValues(new double[0]);
          variableValues.recomputeBasicVariableValues();
          displayErrors();
          status = dualMinimize(false, timeLimit);
          if (!status.ok()) {
            return status;
          }
          variablesInfo.endDualPhaseI(reducedCosts.getDualFeasibilityTolerance(),
              reducedCosts.getFullReducedCosts());
          variableValues.resetAllNonBasicVariableValues(variableStartingValues);
          variableValues.recomputeBasicVariableValues();
          if (problemStatus == ProblemStatus.OPTIMAL) {
            if (reducedCosts.computeMaximumDualInfeasibility() < reducedCosts
                .getDualFeasibilityTolerance() + 1e-6) {
              problemStatus = ProblemStatus.DUAL_FEASIBLE;
            } else {
              logger.log("Infeasible after first phase.");
              problemStatus = ProblemStatus.DUAL_INFEASIBLE;
            }
          }
        }
      }
    } else {
      status = primalMinimize(timeLimit);
      if (!status.ok()) {
        return status;
      }
      if (problemStatus != ProblemStatus.PRIMAL_INFEASIBLE) {
        final double[] obj = Arrays.copyOf(objectiveCoefficients, numCols);
        if (isMaximizationProblem) {
          for (int i = 0; i < objectiveCoefficients.length; ++i) {
            obj[i] = -obj[i];
          }
        }
        objective.set(obj);
        reducedCosts.resetForNewObjective();
      }
    }

    displayErrors();
    phase = Phase.OPTIMIZATION;
    feasibilityTime = timeLimit.getElapsedTime() - startTime;
    primalEdgeNorms.setPricingRule(params.getOptimizationRule());
    numFeasibilityIterations = numIterations;

    logger.log("");
    for (int numOptims = 0; numOptims <= params.getMaxNumberOfReoptimizations()
        && !objectiveLimitReached
        && (numIterations == 0 || numIterations < params.getMaxNumberOfIterations()
            || params.getMaxNumberOfIterations() < 0)
        && !timeLimit.limitReached() && (problemStatus == ProblemStatus.PRIMAL_FEASIBLE
            || problemStatus == ProblemStatus.DUAL_FEASIBLE);
        ++numOptims) {
      if (problemStatus == ProblemStatus.PRIMAL_FEASIBLE) {
        status = primalMinimize(timeLimit);
      } else {
        status = dualMinimize(phase == Phase.FEASIBILITY, timeLimit);
      }
      if (!status.ok()) {
        return status;
      }

      variableValues.resetAllNonBasicVariableValues(variableStartingValues);
      status = basisFactorization.refactorize();
      if (!status.ok()) {
        return status;
      }
      permuteBasis();
      variableValues.recomputeBasicVariableValues();
      reducedCosts.clearAndRemoveCostShifts();

      displayErrors();

      if (problemStatus == ProblemStatus.PRIMAL_UNBOUNDED) {
        final double tolerance = params.getSolutionFeasibilityTolerance();
        if (reducedCosts.computeMaximumDualResidual() > tolerance
            || variableValues.computeMaximumPrimalResidual() > tolerance
            || variableValues.computeMaximumPrimalInfeasibility() > tolerance) {
          logger.log("PRIMAL_UNBOUNDED was reported, but the residual and/or "
              + "dual infeasibility is above the tolerance");
          if (params.getChangeStatusToImprecise()) {
            problemStatus = ProblemStatus.IMPRECISE;
          }
          break;
        }
        double minDistance = LpTypes.INFINITY;
        final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
        final double[] upperBounds = variablesInfo.getVariableUpperBounds();
        double costDelta = 0.0;
        final double[] obj = objective.get();
        for (int col = 0; col < numCols; ++col) {
          costDelta += solutionPrimalRay[col] * obj[col];
          if (solutionPrimalRay[col] > 0 && upperBounds[col] != LpTypes.INFINITY) {
            final double value = variableValues.get(col);
            final double distance = (upperBounds[col] - value + tolerance) / solutionPrimalRay[col];
            minDistance = Math.min(distance, minDistance);
          }
          if (solutionPrimalRay[col] < 0 && lowerBounds[col] != -LpTypes.INFINITY) {
            final double value = variableValues.get(col);
            final double distance =
                (value - lowerBounds[col] + tolerance) / -solutionPrimalRay[col];
            minDistance = Math.min(distance, minDistance);
          }
        }
        if (minDistance * Math.abs(costDelta) < 1
            && reducedCosts.computeMaximumDualInfeasibility() <= tolerance) {
          logger.log("PRIMAL_UNBOUNDED was reported, but the tolerance are good "
              + "and the unbounded ray is not great.");
          problemStatus = ProblemStatus.OPTIMAL;
        }
        break;
      }
      if (problemStatus == ProblemStatus.DUAL_UNBOUNDED) {
        final double tolerance = params.getSolutionFeasibilityTolerance();
        if (reducedCosts.computeMaximumDualResidual() > tolerance
            || variableValues.computeMaximumPrimalResidual() > tolerance
            || reducedCosts.computeMaximumDualInfeasibility() > tolerance) {
          logger.log("DUAL_UNBOUNDED was reported, but the residual and/or "
              + "dual infeasibility is above the tolerance");
          if (params.getChangeStatusToImprecise()) {
            problemStatus = ProblemStatus.IMPRECISE;
          }
        }
        final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
        final double[] upperBounds = variablesInfo.getVariableUpperBounds();
        double impliedLb = 0.0;
        double err = 0.0;
        for (int col = 0; col < numCols; ++col) {
          final double coeff = solutionDualRayRowCombination[col];
          if (coeff > 0) {
            if (lowerBounds[col] == -LpTypes.INFINITY) {
              err = Math.max(err, coeff);
            } else {
              impliedLb += coeff * lowerBounds[col];
            }
          } else if (coeff < 0) {
            if (upperBounds[col] == LpTypes.INFINITY) {
              err = Math.max(err, -coeff);
            } else {
              impliedLb += coeff * upperBounds[col];
            }
          }
        }
        logger.log("Dual ray error=", err, " infeasibility=", impliedLb);
        if (impliedLb < tolerance || err > tolerance) {
          logger.log("DUAL_UNBOUNDED was reported, but the dual ray is not "
              + "proving infeasibility with high enough tolerance");
          if (params.getChangeStatusToImprecise()) {
            problemStatus = ProblemStatus.IMPRECISE;
          }
        }
        break;
      }

      if (problemStatus == ProblemStatus.OPTIMAL) {
        final double solutionTolerance = params.getSolutionFeasibilityTolerance();
        final double primalResidual = variableValues.computeMaximumPrimalResidual();
        final double dualResidual = reducedCosts.computeMaximumDualResidual();
        if (primalResidual > solutionTolerance || dualResidual > solutionTolerance) {
          logger.log("OPTIMAL was reported, yet one of the residuals is above the solution "
              + "feasibility tolerance after the shift/perturbation are removed.");
          if (params.getChangeStatusToImprecise()) {
            problemStatus = ProblemStatus.IMPRECISE;
          }
        } else {
          final double primalTolerance =
              Math.max(primalResidual, params.getPrimalFeasibilityTolerance());
          final double dualTolerance = Math.max(dualResidual, params.getDualFeasibilityTolerance());
          final double primalInfeasibility = variableValues.computeMaximumPrimalInfeasibility();
          final double dualInfeasibility = reducedCosts.computeMaximumDualInfeasibility();
          if (primalInfeasibility > primalTolerance && dualInfeasibility > dualTolerance) {
            logger.log("OPTIMAL was reported, yet both of the infeasibility "
                + "are above the tolerance after the shift/perturbation are removed.");
            if (params.getChangeStatusToImprecise()) {
              problemStatus = ProblemStatus.IMPRECISE;
            }
          } else if (primalInfeasibility > primalTolerance) {
            if (numOptims == params.getMaxNumberOfReoptimizations()) {
              logger.log("The primal infeasibility is still higher than the requested internal "
                  + "tolerance, but the maximum number of optimization is reached.");
              break;
            }
            logger.log("");
            logger.log("Re-optimizing with dual simplex ... ");
            problemStatus = ProblemStatus.DUAL_FEASIBLE;
          } else if (dualInfeasibility > dualTolerance) {
            if (numOptims == params.getMaxNumberOfReoptimizations()) {
              logger.log("The dual infeasibility is still higher than the requested internal "
                  + "tolerance, but the maximum number of optimization is reached.");
              break;
            }
            logger.log("");
            logger.log("Re-optimizing with primal simplex ... ");
            problemStatus = ProblemStatus.PRIMAL_FEASIBLE;
          }
        }
      }
    }

    if (params.getChangeStatusToImprecise() && problemStatus != ProblemStatus.DUAL_INFEASIBLE) {
      final double tolerance = params.getSolutionFeasibilityTolerance();
      if (variableValues.computeMaximumPrimalResidual() > tolerance
          || reducedCosts.computeMaximumDualResidual() > tolerance) {
        problemStatus = ProblemStatus.IMPRECISE;
      } else if (problemStatus == ProblemStatus.DUAL_FEASIBLE
          || problemStatus == ProblemStatus.DUAL_UNBOUNDED
          || problemStatus == ProblemStatus.PRIMAL_INFEASIBLE) {
        if (reducedCosts.computeMaximumDualInfeasibility() > tolerance) {
          problemStatus = ProblemStatus.IMPRECISE;
        }
      } else if (problemStatus == ProblemStatus.PRIMAL_FEASIBLE
          || problemStatus == ProblemStatus.PRIMAL_UNBOUNDED
          || problemStatus == ProblemStatus.DUAL_INFEASIBLE) {
        if (variableValues.computeMaximumPrimalInfeasibility() > tolerance) {
          problemStatus = ProblemStatus.IMPRECISE;
        }
      }
    }

    totalTime = timeLimit.getElapsedTime() - startTime;
    optimizationTime = totalTime - feasibilityTime;
    numOptimizationIterations = numIterations - numFeasibilityIterations;

    solutionObjectiveValue = computeInitialProblemObjectiveValue();
    solutionDualValues = reducedCosts.getDualValues().clone();
    solutionReducedCosts = reducedCosts.getReducedCosts().clone();
    saveState();

    if (isMaximizationProblem) {
      LpUtils.changeSign(solutionDualValues);
      LpUtils.changeSign(solutionReducedCosts);
    }
    if (problemStatus == ProblemStatus.DUAL_UNBOUNDED
        || problemStatus == ProblemStatus.PRIMAL_UNBOUNDED) {
      solutionObjectiveValue = (problemStatus == ProblemStatus.DUAL_UNBOUNDED) ? LpTypes.INFINITY
          : -LpTypes.INFINITY;
      if (isMaximizationProblem) {
        solutionObjectiveValue = -solutionObjectiveValue;
      }
    }
    variableStartingValues = new double[0];
    return Status.OK;
  }

  private static List<Integer> bitSetToList(BitSet bs) {
    final List<Integer> list = new ArrayList<>(bs.cardinality());
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      list.add(i);
    }
    return list;
  }

  /** The status of the last solve. */
  public ProblemStatus getProblemStatus() {
    return problemStatus;
  }

  /** The objective value of the last solve, in the units of the given problem. */
  public double getObjectiveValue() {
    return solutionObjectiveValue;
  }

  /** The number of simplex iterations of the last solve. */
  public long getNumberOfIterations() {
    return numIterations;
  }

  /** The number of rows of the internal problem. */
  public int getProblemNumRows() {
    return numRows;
  }

  /** The number of columns (with slacks) of the internal problem. */
  public int getProblemNumCols() {
    return numCols;
  }

  /** The value of a variable. */
  public double getVariableValue(int col) {
    return variableValues.get(col);
  }

  /** The reduced cost of a variable. */
  public double getReducedCost(int col) {
    return solutionReducedCosts[col];
  }

  /** The reduced costs of all the variables. */
  public double[] getReducedCosts() {
    return solutionReducedCosts;
  }

  /** The dual value of a constraint. */
  public double getDualValue(int row) {
    return solutionDualValues[row];
  }

  /** The status of a variable. */
  public VariableStatus getVariableStatus(int col) {
    return variablesInfo.getStatusRow()[col];
  }

  /** The basis state of the last solve. */
  public BasisState getState() {
    return solutionState;
  }

  /** The activity of a constraint. */
  public double getConstraintActivity(int row) {
    return -variableValues.get(slackColIndex(row));
  }

  /** The status of a constraint, from the status of its slack. */
  public ConstraintStatus getConstraintStatus(int row) {
    final VariableStatus s = variablesInfo.getStatusRow()[slackColIndex(row)];
    if (s == VariableStatus.AT_LOWER_BOUND) {
      return ConstraintStatus.AT_UPPER_BOUND;
    }
    if (s == VariableStatus.AT_UPPER_BOUND) {
      return ConstraintStatus.AT_LOWER_BOUND;
    }
    return LpTypes.variableToConstraintStatus(s);
  }

  /** The primal ray of a PRIMAL_UNBOUNDED problem. */
  public double[] getPrimalRay() {
    return solutionPrimalRay;
  }

  /** The dual ray (Farkas proof) of a DUAL_UNBOUNDED problem. */
  public double[] getDualRay() {
    return solutionDualRay;
  }

  /** The combination of the matrix rows given by the dual ray. */
  public double[] getDualRayRowCombination() {
    return solutionDualRayRowCombination;
  }

  /** The column in the basis at the given row. */
  public int getBasis(int row) {
    return basis.get()[row];
  }

  /** A copy of the basis. */
  public int[] getBasisVector() {
    return basis.get().clone();
  }

  /** The basis factorization. */
  public BasisFactorization getBasisFactorization() {
    return basisFactorization;
  }

  /** Whether the last solve stopped on the objective limit. */
  public boolean objectiveLimitReached() {
    return objectiveLimitReached;
  }

  /** The deterministic time of the solver. */
  public double deterministicTime() {
    return LpTypes.deterministicTimeForFpOperations(numUpdatePriceOperations)
        + basisFactorization.deterministicTime() + updateRow.deterministicTime()
        + enteringVariable.deterministicTime() + reducedCosts.deterministicTime()
        + primalEdgeNorms.deterministicTime();
  }

  /** Returns a short summary of the last solve. */
  public String getPrettySolverStats() {
    return String.format(Locale.ROOT,
        "Problem status                               : %s%n"
            + "Solving time                                 : %-6.4g%n"
            + "Number of iterations                         : %d%n"
            + "Time for solvability (first phase)           : %-6.4g%n"
            + "Number of iterations for solvability         : %d%n"
            + "Time for optimization                        : %-6.4g%n"
            + "Number of iterations for optimization        : %d%n",
        problemStatus, totalTime, numIterations, feasibilityTime, numFeasibilityIterations,
        optimizationTime, numOptimizationIterations);
  }

  private void setNonBasicVariableStatusAndDeriveValue(int col, VariableStatus status) {
    variablesInfo.updateToNonBasicStatus(col, status);
    variableValues.setNonBasicVariableValueFromStatus(col);
  }

  private boolean basisIsConsistent() {
    final BitSet isBasic = variablesInfo.getIsBasicBitRow();
    final VariableStatus[] statuses = variablesInfo.getStatusRow();
    final int[] b = basis.get();
    for (int row = 0; row < numRows; ++row) {
      final int col = b[row];
      if (!isBasic.get(col)) {
        return false;
      }
      if (statuses[col] != VariableStatus.BASIC) {
        return false;
      }
    }
    int colsInBasis = 0;
    for (int col = 0; col < numCols; ++col) {
      if (isBasic.get(col)) {
        ++colsInBasis;
      }
      if (isBasic.get(col) != (statuses[col] == VariableStatus.BASIC)) {
        return false;
      }
    }
    return colsInBasis == numRows;
  }

  private void updateBasis(int enteringCol, int basisRow, VariableStatus leavingVariableStatus) {
    final int[] b = basis.get();
    final int leavingCol = b[basisRow];
    variablesInfo.updateToNonBasicStatus(leavingCol, leavingVariableStatus);
    b[basisRow] = enteringCol;
    variablesInfo.updateToBasicStatus(enteringCol);
    updateRow.invalidate();
  }

  private void useSingletonColumnInInitialBasis(int[] b) {
    final List<Integer> singletonColumn = new ArrayList<>();
    final double[] costVariation = new double[numCols];
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final double[] obj = objective.get();
    for (int col = 0; col < numCols; ++col) {
      if (compactMatrix.columnNumEntries(col) != 1) {
        continue;
      }
      if (lowerBounds[col] == upperBounds[col]) {
        continue;
      }
      final double slope = compactMatrix.columnFirstCoefficient(col);
      if (variableValues.get(col) == lowerBounds[col]) {
        costVariation[col] = obj[col] / Math.abs(slope);
      } else {
        costVariation[col] = -obj[col] / Math.abs(slope);
      }
      singletonColumn.add(col);
    }
    if (singletonColumn.isEmpty()) {
      return;
    }
    singletonColumn.sort((x, y) -> Double.compare(costVariation[x], costVariation[y]));
    final double[] values = variableValues.getDenseRow();
    for (int col : singletonColumn) {
      final int row = compactMatrix.columnFirstRow(col);
      if (b[row] == LpTypes.INVALID_COL) {
        b[row] = col;
      }
      if (error[row] == 0.0) {
        continue;
      }
      final double coeff = compactMatrix.columnFirstCoefficient(col);
      final double newValue = values[col] + error[row] / coeff;
      if (newValue >= lowerBounds[col] && newValue <= upperBounds[col]) {
        error[row] = 0.0;
        b[row] = col;
        continue;
      }
      final double boxWidth = variablesInfo.getBoundDifference(col);
      final double errorSign = error[row] / coeff;
      if (values[col] == lowerBounds[col] && errorSign > 0.0) {
        error[row] -= coeff * boxWidth;
        setNonBasicVariableStatusAndDeriveValue(col, VariableStatus.AT_UPPER_BOUND);
        continue;
      }
      if (values[col] == upperBounds[col] && errorSign < 0.0) {
        error[row] += coeff * boxWidth;
        setNonBasicVariableStatusAndDeriveValue(col, VariableStatus.AT_LOWER_BOUND);
        continue;
      }
    }
  }

  // returns {matrixIsUnchanged, onlyChangeIsNewRows, onlyChangeIsNewCols, numNewCols}
  private int[] initializeMatrixAndTestIfUnchanged(LinearProgram lp,
      boolean lpIsInEquationForm) {
    final boolean oldPartOfMatrixIsUnchanged = SparseMatrix
        .areFirstColumnsAndRowsExactlyEquals(numRows, firstSlackCol, lp.getSparseMatrix(),
            compactMatrix);
    final int lpFirstSlack =
        lpIsInEquationForm ? lp.getFirstSlackVariable() : lp.numVariables();
    if (oldPartOfMatrixIsUnchanged && lp.numConstraints() == numRows
        && lpFirstSlack == firstSlackCol) {
      if (parameters.get().getUseTransposedMatrix()) {
        if (transposedMatrix.isEmpty()) {
          transposedMatrix.populateFromTranspose(compactMatrix);
        }
      } else {
        transposedMatrix.reset(0);
      }
      return new int[] {1, 0, 0, 0};
    }
    final boolean onlyChangeIsNewRows = oldPartOfMatrixIsUnchanged
        && lp.numConstraints() > numRows && lpFirstSlack == firstSlackCol;
    final boolean onlyChangeIsNewCols = oldPartOfMatrixIsUnchanged
        && lp.numConstraints() == numRows && lpFirstSlack > firstSlackCol;
    final int numNewCols = onlyChangeIsNewCols ? lpFirstSlack - firstSlackCol : 0;
    firstSlackCol = lpFirstSlack;
    numRows = lp.numConstraints();
    numCols = lpFirstSlack + lp.numConstraints();
    if (lpIsInEquationForm) {
      compactMatrix.populateFromMatrixView(lp.getSparseMatrix());
    } else {
      compactMatrix.populateFromSparseMatrixAndAddSlacks(lp.getSparseMatrix());
    }
    if (parameters.get().getUseTransposedMatrix()) {
      transposedMatrix.populateFromTranspose(compactMatrix);
    } else {
      transposedMatrix.reset(0);
    }
    return new int[] {0, onlyChangeIsNewRows ? 1 : 0, onlyChangeIsNewCols ? 1 : 0, numNewCols};
  }

  private boolean oldBoundsAreUnchangedAndNewVariablesHaveOneBoundAtZero(LinearProgram lp,
      boolean lpIsInEquationForm, int numNewCols) {
    final int firstNewCol = firstSlackCol - numNewCols;
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final double[] lpLb = lp.variableLowerBounds();
    final double[] lpUb = lp.variableUpperBounds();
    for (int col = 0; col < firstNewCol; ++col) {
      if (lowerBounds[col] != lpLb[col] || upperBounds[col] != lpUb[col]) {
        return false;
      }
    }
    for (int col = firstNewCol; col < firstSlackCol; ++col) {
      if (lpLb[col] != 0.0 && lpUb[col] != 0.0) {
        return false;
      }
    }
    if (lpIsInEquationForm) {
      for (int col = firstSlackCol; col < numCols; ++col) {
        if (lowerBounds[col - numNewCols] != lpLb[col]
            || upperBounds[col - numNewCols] != lpUb[col]) {
          return false;
        }
      }
    } else {
      final double[] clb = lp.constraintLowerBounds();
      final double[] cub = lp.constraintUpperBounds();
      for (int row = 0; row < numRows; ++row) {
        final int col = firstSlackCol + row;
        if (lowerBounds[col - numNewCols] != -cub[row]
            || upperBounds[col - numNewCols] != -clb[row]) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean initializeObjectiveAndTestIfUnchanged(LinearProgram lp) {
    boolean objectiveIsUnchanged = true;
    double[] obj = objective.get();
    if (obj.length != numCols) {
      obj = Arrays.copyOf(obj, numCols);
      objective.set(obj);
    }
    final double[] objCoeffs = lp.objectiveCoefficients();
    for (int col = objCoeffs.length; col < numCols; ++col) {
      if (obj[col] != 0.0) {
        objectiveIsUnchanged = false;
        obj[col] = 0.0;
      }
    }
    if (lp.isMaximizationProblem()) {
      for (int col = 0; col < objCoeffs.length; ++col) {
        final double coeff = -objCoeffs[col];
        if (obj[col] != coeff) {
          objectiveIsUnchanged = false;
          obj[col] = coeff;
        }
      }
      objectiveOffset = -lp.objectiveOffset();
      objectiveScalingFactor = -lp.objectiveScalingFactor();
    } else {
      for (int col = 0; col < objCoeffs.length; ++col) {
        final double coeff = objCoeffs[col];
        if (obj[col] != coeff) {
          objectiveIsUnchanged = false;
          obj[col] = coeff;
        }
      }
      objectiveOffset = lp.objectiveOffset();
      objectiveScalingFactor = lp.objectiveScalingFactor();
    }
    return objectiveIsUnchanged;
  }

  private void initializeObjectiveLimit() {
    objectiveLimitReached = false;
    for (int k = 0; k < 2; ++k) {
      final boolean setDual = k == 0;
      final double limit = (objectiveScalingFactor >= 0.0) != setDual
          ? parameters.get().getObjectiveLowerLimit()
          : parameters.get().getObjectiveUpperLimit();
      final double shiftedLimit = limit / objectiveScalingFactor - objectiveOffset;
      if (setDual) {
        dualObjectiveLimit = shiftedLimit;
      } else {
        primalObjectiveLimit = shiftedLimit;
      }
    }
  }

  private Status createInitialBasis() {
    final GlopParameters params = parameters.get();
    variablesInfo.initializeToDefaultStatus();
    variableValues.resetAllNonBasicVariableValues(variableStartingValues);
    int[] b = new int[numRows];
    for (int row = 0; row < numRows; ++row) {
      b[row] = slackColIndex(row);
    }
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    if (!params.getUseDualSimplex()
        && params.getInitialBasis() != GlopParameters.InitialBasisHeuristic.MAROS
        && params.getExploitSingletonColumnInInitialBasis()) {
      final double[] obj = objective.get();
      for (int col = 0; col < numCols; ++col) {
        if (compactMatrix.columnNumEntries(col) != 1) {
          continue;
        }
        final VariableStatus status = variablesInfo.getStatusRow()[col];
        final double o = obj[col];
        if (o > 0 && LpTypes.isFinite(lowerBounds[col])
            && status == VariableStatus.AT_UPPER_BOUND) {
          setNonBasicVariableStatusAndDeriveValue(col, VariableStatus.AT_LOWER_BOUND);
        } else if (o < 0 && LpTypes.isFinite(upperBounds[col])
            && status == VariableStatus.AT_LOWER_BOUND) {
          setNonBasicVariableStatusAndDeriveValue(col, VariableStatus.AT_UPPER_BOUND);
        }
      }
      computeVariableValuesError();
      Arrays.fill(b, LpTypes.INVALID_COL);
      useSingletonColumnInInitialBasis(b);
      for (int row = 0; row < numRows; ++row) {
        if (b[row] == LpTypes.INVALID_COL) {
          b[row] = slackColIndex(row);
        }
      }
    }
    if (params.getInitialBasis() == GlopParameters.InitialBasisHeuristic.NONE) {
      return initializeFirstBasis(b);
    }
    if (params.getInitialBasis() == GlopParameters.InitialBasisHeuristic.MAROS) {
      final InitialBasis initialBasis = new InitialBasis(compactMatrix, objective.get(),
          lowerBounds, upperBounds, variablesInfo.getTypeRow());
      if (params.getUseDualSimplex()) {
        initialBasis.getDualMarosBasis(numCols, b);
      } else {
        initialBasis.getPrimalMarosBasis(numCols, b);
      }
    } else if (params.getInitialBasis() == GlopParameters.InitialBasisHeuristic.BIXBY
        || params.getInitialBasis() == GlopParameters.InitialBasisHeuristic.TRIANGULAR) {
      int numFixedVariables = 0;
      for (int row = 0; row < b.length; ++row) {
        final int col = b[row];
        if (lowerBounds[col] == upperBounds[col]) {
          b[row] = LpTypes.INVALID_COL;
          ++numFixedVariables;
        }
      }
      if (numFixedVariables == 0) {
        logger.log("Crash is set to ", params.getInitialBasis(),
            " but there is no equality rows to remove from initial all slack basis. "
                + "Starting from there.");
      } else {
        logger.log("Trying to remove ", numFixedVariables,
            " fixed variables from the initial basis.");
        final InitialBasis initialBasis = new InitialBasis(compactMatrix, objective.get(),
            lowerBounds, upperBounds, variablesInfo.getTypeRow());
        if (params.getInitialBasis() == GlopParameters.InitialBasisHeuristic.BIXBY) {
          if (params.getUseScaling()) {
            initialBasis.completeBixbyBasis(firstSlackCol, b);
          }
        } else {
          if (params.getUseDualSimplex()) {
            initialBasis.completeTriangularDualBasis(numCols, b);
          } else {
            initialBasis.completeTriangularPrimalBasis(numCols, b);
          }
          final Status status = initializeFirstBasis(b);
          if (status.ok()) {
            return status;
          } else {
            logger.log("Advanced basis algo failed, Reverting to all slack basis.");
            b = new int[numRows];
            for (int row = 0; row < numRows; ++row) {
              b[row] = slackColIndex(row);
            }
          }
        }
      }
    }
    return initializeFirstBasis(b);
  }

  private Status initializeFirstBasis(int[] initialBasis) {
    final int[] b = Arrays.copyOf(initialBasis, numRows);
    for (int row = initialBasis.length; row < numRows; ++row) {
      b[row] = LpTypes.INVALID_COL;
    }
    for (int row = 0; row < numRows; ++row) {
      if (b[row] == LpTypes.INVALID_COL) {
        b[row] = slackColIndex(row);
      }
    }
    basis.set(b);
    Status status = basisFactorization.initialize();
    if (!status.ok()) {
      return status;
    }
    permuteBasis();
    final double conditionNumberUb =
        basisFactorization.computeInfinityNormConditionNumberUpperBound();
    if (conditionNumberUb > parameters.get().getInitialConditionNumberThreshold()) {
      final String errorMessage =
          "The matrix condition number upper bound is too high: " + conditionNumberUb;
      logger.log(errorMessage);
      return new Status(Status.ErrorCode.ERROR_LU, errorMessage);
    }
    for (int row = 0; row < numRows; ++row) {
      variablesInfo.updateToBasicStatus(basis.get()[row]);
    }
    variableValues.resetAllNonBasicVariableValues(variableStartingValues);
    variableValues.recomputeBasicVariableValues();
    return Status.OK;
  }

  private Status initialize(LinearProgram lp) {
    parameters.set(initialParameters.copy());
    propagateParameters();
    final boolean lpIsInEquationForm =
        lp.getFirstSlackVariable() != LpTypes.INVALID_COL && lp.isInEquationForm();
    final int[] changes = initializeMatrixAndTestIfUnchanged(lp, lpIsInEquationForm);
    final boolean matrixIsUnchanged = changes[0] == 1;
    final boolean onlyChangeIsNewRows = changes[1] == 1;
    final boolean onlyChangeIsNewCols = changes[2] == 1;
    final int numNewCols = changes[3];
    final boolean onlyNewBounds = onlyChangeIsNewCols && numNewCols > 0
        && oldBoundsAreUnchangedAndNewVariablesHaveOneBoundAtZero(lp, lpIsInEquationForm,
            numNewCols);
    final boolean objectiveIsUnchanged = initializeObjectiveAndTestIfUnchanged(lp);
    final boolean boundsAreUnchanged = lpIsInEquationForm
        ? variablesInfo.loadBoundsAndReturnTrueIfUnchanged(lp.variableLowerBounds(),
            lp.variableUpperBounds())
        : variablesInfo.loadBoundsAndReturnTrueIfUnchanged(lp.variableLowerBounds(),
            lp.variableUpperBounds(), lp.constraintLowerBounds(), lp.constraintUpperBounds());

    if (matrixIsUnchanged && parameters.get().getAllowSimplexAlgorithmChange()) {
      if (objectiveIsUnchanged && !boundsAreUnchanged) {
        parameters.get().setUseDualSimplex(true);
        propagateParameters();
      }
      if (boundsAreUnchanged && !objectiveIsUnchanged) {
        parameters.get().setUseDualSimplex(false);
        propagateParameters();
      }
    }
    initializeObjectiveLimit();

    boolean solveFromScratch = true;
    if (!solutionState.isEmpty() && !solutionStateHasBeenSetExternally) {
      if (!parameters.get().getUseDualSimplex()) {
        dualEdgeNorms.clear();
        dualPricingVector = new double[0];
        if (matrixIsUnchanged && boundsAreUnchanged) {
          reducedCosts.clearAndRemoveCostShifts();
          solveFromScratch = false;
        } else if (onlyChangeIsNewCols && onlyNewBounds) {
          variablesInfo.initializeFromBasisState(firstSlackCol, numNewCols, solutionState,
              variableStartingValues.length);
          variableValues.resetAllNonBasicVariableValues(variableStartingValues);
          final int firstNewCol = firstSlackCol - numNewCols;
          final int[] b = basis.get();
          for (int i = 0; i < b.length; ++i) {
            if (b[i] >= firstNewCol) {
              b[i] += numNewCols;
            }
          }
          primalEdgeNorms.clear();
          reducedCosts.clearAndRemoveCostShifts();
          solveFromScratch = false;
        }
      } else {
        primalEdgeNorms.clear();
        if (objectiveIsUnchanged) {
          if (matrixIsUnchanged) {
            if (!boundsAreUnchanged) {
              variablesInfo.initializeFromBasisState(firstSlackCol, 0, solutionState,
                variableStartingValues.length);
              variableValues.resetAllNonBasicVariableValues(variableStartingValues);
              variableValues.recomputeBasicVariableValues();
            }
            solveFromScratch = false;
          } else if (onlyChangeIsNewRows) {
            variablesInfo.initializeFromBasisState(firstSlackCol, 0, solutionState,
                variableStartingValues.length);
            dualEdgeNorms.resizeOnNewRows(numRows);
            reducedCosts.clearAndRemoveCostShifts();
            dualPricingVector = new double[0];
            if (initializeFirstBasis(basis.get()).ok()) {
              solveFromScratch = false;
            }
          }
        }
      }
    }
    return finishInitialization(solveFromScratch);
  }

  private Status finishInitialization(boolean solveFromScratch) {
    if (solveFromScratch && !solutionState.isEmpty()) {
      basisFactorization.clear();
      reducedCosts.clearAndRemoveCostShifts();
      primalEdgeNorms.clear();
      dualEdgeNorms.clear();
      dualPricingVector = new double[0];
      variablesInfo.initializeFromBasisState(firstSlackCol, 0, solutionState,
                variableStartingValues.length);
      final List<Integer> candidates = bitSetToList(variablesInfo.getIsBasicBitRow());
      logger.log("The warm-start state contains ", candidates.size(),
          " candidates for the basis (num_rows = ", numRows, ").");
      if (candidates.size() == numRows) {
        final int[] b = new int[numRows];
        for (int i = 0; i < numRows; ++i) {
          b[i] = candidates.get(i);
        }
        basis.set(b);
        if (initializeFirstBasis(b).ok()) {
          solveFromScratch = false;
        }
      }
      if (solveFromScratch) {
        basis.set(basisFactorization.computeInitialBasis(candidates));
        final int numSuperBasic = variablesInfo.changeUnusedBasicVariablesToFree(basis.get());
        final int numSnapped = variablesInfo.snapFreeVariablesToBound(
            parameters.get().getCrossoverBoundSnappingDistance(), variableStartingValues);
        if (logger.loggingIsEnabled()) {
          logger.log("The initial basis did not use ", numSuperBasic,
              " BASIC columns from the initial state and used ",
              numRows - (candidates.size() - numSuperBasic),
              " slack variables that were not marked BASIC.");
          if (numSnapped > 0) {
            logger.log(numSnapped, " of the FREE variables where moved to their bound.");
          }
        }
        if (initializeFirstBasis(basis.get()).ok()) {
          solveFromScratch = false;
        } else {
          logger.log("RevisedSimplex is not using the warm start "
              + "basis because it is not factorizable.");
        }
      }
    }
    if (solveFromScratch) {
      logger.log("Starting basis: create from scratch.");
      basisFactorization.clear();
      reducedCosts.clearAndRemoveCostShifts();
      primalEdgeNorms.clear();
      dualEdgeNorms.clear();
      dualPricingVector = new double[0];
      final Status status = createInitialBasis();
      if (!status.ok()) {
        return status;
      }
    } else {
      logger.log("Starting basis: incremental solve.");
    }
    transposeWasChanged = false;
    return Status.OK;
  }

  private void displayBasicVariableStatistics() {
    int numFixedVariables = 0;
    int numFreeVariables = 0;
    int numVariablesAtBound = 0;
    int numSlackVariables = 0;
    int numInfeasibleVariables = 0;
    final double[] values = variableValues.getDenseRow();
    final VariableType[] types = variablesInfo.getTypeRow();
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final double tolerance = parameters.get().getPrimalFeasibilityTolerance();
    final int[] b = basis.get();
    for (int row = 0; row < numRows; ++row) {
      final int col = b[row];
      final double value = values[col];
      if (types[col] == VariableType.UNCONSTRAINED) {
        ++numFreeVariables;
      }
      if (value > upperBounds[col] + tolerance || value < lowerBounds[col] - tolerance) {
        ++numInfeasibleVariables;
      }
      if (col >= firstSlackCol) {
        ++numSlackVariables;
      }
      if (lowerBounds[col] == upperBounds[col]) {
        ++numFixedVariables;
      } else if (values[col] == lowerBounds[col] || values[col] == upperBounds[col]) {
        ++numVariablesAtBound;
      }
    }
    logger.log("The matrix with slacks has ", compactMatrix.numRows(), " rows, ",
        compactMatrix.numCols(), " columns, ", compactMatrix.numEntries(), " entries.");
    logger.log("Number of basic infeasible variables: ", numInfeasibleVariables);
    logger.log("Number of basic slack variables: ", numSlackVariables);
    logger.log("Number of basic variables at bound: ", numVariablesAtBound);
    logger.log("Number of basic fixed variables: ", numFixedVariables);
    logger.log("Number of basic free variables: ", numFreeVariables);
    logger.log("Number of super-basic variables: ", computeNumberOfSuperBasicVariables());
  }

  private void saveState() {
    solutionState = new BasisState(variablesInfo.getStatusRow());
    solutionStateHasBeenSetExternally = false;
  }

  private int computeNumberOfSuperBasicVariables() {
    final VariableStatus[] statuses = variablesInfo.getStatusRow();
    int numSuperBasic = 0;
    for (int col = 0; col < numCols; ++col) {
      if (statuses[col] == VariableStatus.FREE && variableValues.get(col) != 0.0) {
        ++numSuperBasic;
      }
    }
    return numSuperBasic;
  }

  private void correctErrorsOnVariableValues() {
    final double primalResidual = variableValues.computeMaximumPrimalResidual();
    if (primalResidual >= parameters.get().getHarrisToleranceRatio()
        * parameters.get().getPrimalFeasibilityTolerance()) {
      variableValues.recomputeBasicVariableValues();
    }
  }

  private void computeVariableValuesError() {
    error = new double[numRows];
    final double[] values = variableValues.getDenseRow();
    for (int col = 0; col < numCols; ++col) {
      compactMatrix.columnAddMultipleToDenseColumn(col, -values[col], error);
    }
  }

  private void computeDirection(int col) {
    basisFactorization.rightSolveForProblemColumn(col, direction);
    double norm = 0.0;
    for (int i = 0; i < direction.numNonZeros; ++i) {
      norm = Math.max(norm, Math.abs(direction.values[direction.nonZeros[i]]));
    }
    directionInfinityNorm = norm;
  }

  private double getRatio(boolean isEnteringReducedCostPositive, double[] lowerBounds,
      double[] upperBounds, int row) {
    final int col = basis.get()[row];
    final double dir = direction.values[row];
    final double value = variableValues.get(col);
    if (isEnteringReducedCostPositive) {
      if (dir > 0.0) {
        return (upperBounds[col] - value) / dir;
      } else {
        return (lowerBounds[col] - value) / dir;
      }
    } else {
      if (dir > 0.0) {
        return (value - lowerBounds[col]) / dir;
      } else {
        return (value - upperBounds[col]) / dir;
      }
    }
  }

  private double computeHarrisRatioAndLeavingCandidates(boolean isEnteringReducedCostPositive,
      double boundFlipRatio, SparseColumn candidates) {
    final GlopParameters params = parameters.get();
    final double harrisTolerance =
        params.getHarrisToleranceRatio() * params.getPrimalFeasibilityTolerance();
    final double minimumDelta =
        params.getDegenerateMinistepFactor() * params.getPrimalFeasibilityTolerance();
    double harrisRatio = boundFlipRatio;
    candidates.clear();
    final double threshold = basisFactorization.isRefactorized()
        ? params.getMinimumAcceptablePivot()
        : params.getRatioTestZeroThreshold();
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    for (int i = 0; i < direction.numNonZeros; ++i) {
      final int row = direction.nonZeros[i];
      final double magnitude = Math.abs(direction.values[row]);
      if (magnitude <= threshold) {
        continue;
      }
      final double ratio = getRatio(isEnteringReducedCostPositive, lowerBounds, upperBounds, row);
      if (ratio <= harrisRatio) {
        candidates.setCoefficient(row, ratio);
        harrisRatio = Math.min(harrisRatio,
            Math.max(minimumDelta / magnitude, ratio + harrisTolerance / magnitude));
      }
    }
    return harrisRatio;
  }

  private static boolean isRatioMoreOrEquallyStable(double candidate, double current) {
    if (current >= 0.0) {
      return candidate >= 0.0 && candidate <= current;
    } else {
      return candidate >= current;
    }
  }

  /** The result of a ratio test. */
  private static final class LeavingChoice {
    boolean refactorize;
    int leavingRow = LpTypes.INVALID_ROW;
    double stepLength;
    double targetBound;
  }

  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "exact ties between equally good rows, as in C++")
  private Status chooseLeavingVariableRow(int enteringCol, double reducedCost,
      LeavingChoice result) {
    final GlopParameters params = parameters.get();
    equivalentLeavingChoices.clear();
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final double[] dir = direction.values;
    while (true) {
      final double enteringValue = variableValues.get(enteringCol);
      double currentRatio = (reducedCost > 0.0) ? enteringValue - lowerBounds[enteringCol]
          : upperBounds[enteringCol] - enteringValue;
      final double harrisRatio =
          computeHarrisRatioAndLeavingCandidates(reducedCost > 0.0, currentRatio,
              leavingCandidates);
      if (currentRatio <= harrisRatio) {
        result.leavingRow = LpTypes.INVALID_ROW;
        result.stepLength = currentRatio;
        break;
      }
      double pivotMagnitude = 0.0;
      result.leavingRow = LpTypes.INVALID_ROW;
      equivalentLeavingChoices.clear();
      for (int i = 0; i < leavingCandidates.numEntries(); ++i) {
        final double ratio = leavingCandidates.entryCoefficient(i);
        if (ratio > harrisRatio) {
          continue;
        }
        final int row = leavingCandidates.entryRow(i);
        final double candidateMagnitude = Math.abs(dir[row]);
        if (candidateMagnitude < pivotMagnitude) {
          continue;
        }
        if (candidateMagnitude == pivotMagnitude) {
          if (!isRatioMoreOrEquallyStable(ratio, currentRatio)) {
            continue;
          }
          if (ratio == currentRatio) {
            equivalentLeavingChoices.add(row);
            continue;
          }
        }
        equivalentLeavingChoices.clear();
        currentRatio = ratio;
        pivotMagnitude = candidateMagnitude;
        result.leavingRow = row;
      }
      if (!equivalentLeavingChoices.isEmpty()) {
        equivalentLeavingChoices.add(result.leavingRow);
        result.leavingRow =
            equivalentLeavingChoices.get(random.nextInt(equivalentLeavingChoices.size()));
      }
      if (currentRatio <= 0.0) {
        final double minimumDelta =
            params.getDegenerateMinistepFactor() * params.getPrimalFeasibilityTolerance();
        result.stepLength = minimumDelta / pivotMagnitude;
      } else {
        result.stepLength = currentRatio;
      }
      if (pivotMagnitude < params.getSmallPivotThreshold() * directionInfinityNorm) {
        if (!basisFactorization.isRefactorized()) {
          result.refactorize = true;
          return Status.OK;
        }
      }
      break;
    }
    if (result.leavingRow != LpTypes.INVALID_ROW) {
      final boolean isReducedCostPositive = (reducedCost > 0.0);
      final boolean isLeavingCoeffPositive = (dir[result.leavingRow] > 0.0);
      final int leavingCol = basis.get()[result.leavingRow];
      result.targetBound = (isReducedCostPositive == isLeavingCoeffPositive)
          ? upperBounds[leavingCol]
          : lowerBounds[leavingCol];
    }
    return Status.OK;
  }

  /** A breakpoint of the primal phase I ratio test. */
  private static final class BreakPoint {
    final int row;
    final double ratio;
    final double coeffMagnitude;
    final double targetBound;

    BreakPoint(int row, double ratio, double coeffMagnitude, double targetBound) {
      this.row = row;
      this.ratio = ratio;
      this.coeffMagnitude = coeffMagnitude;
      this.targetBound = targetBound;
    }
  }

  private void primalPhaseIChooseLeavingVariableRow(int enteringCol, double reducedCost,
      LeavingChoice result) {
    final GlopParameters params = parameters.get();
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final double enteringValue = variableValues.get(enteringCol);
    double currentRatio = (reducedCost > 0.0) ? enteringValue - lowerBounds[enteringCol]
        : upperBounds[enteringCol] - enteringValue;
    // increasing ratio, then decreasing magnitude, then increasing row
    final PriorityQueue<BreakPoint> breakpoints = new PriorityQueue<>(
        Math.max(1, direction.numNonZeros), (a, b) -> {
          if (a.ratio == b.ratio) {
            if (a.coeffMagnitude == b.coeffMagnitude) {
              return Integer.compare(a.row, b.row);
            }
            return a.coeffMagnitude > b.coeffMagnitude ? -1 : 1;
          }
          return a.ratio < b.ratio ? -1 : 1;
        });
    final double tolerance = params.getPrimalFeasibilityTolerance();
    final int[] b = basis.get();
    for (int i = 0; i < direction.numNonZeros; ++i) {
      final int row = direction.nonZeros[i];
      final double d = reducedCost > 0.0 ? direction.values[row] : -direction.values[row];
      final double magnitude = Math.abs(d);
      if (magnitude < tolerance) {
        continue;
      }
      final int col = b[row];
      final double value = variableValues.get(col);
      final double lowerBound = lowerBounds[col];
      final double upperBound = upperBounds[col];
      final double toLower = (lowerBound - tolerance - value) / d;
      final double toUpper = (upperBound + tolerance - value) / d;
      if (toLower >= 0.0 && toLower < currentRatio) {
        breakpoints.add(new BreakPoint(row, toLower, magnitude, lowerBound));
      }
      if (toUpper >= 0.0 && toUpper < currentRatio) {
        breakpoints.add(new BreakPoint(row, toUpper, magnitude, upperBound));
      }
    }
    double improvement = Math.abs(reducedCost);
    double bestMagnitude = 0.0;
    result.leavingRow = LpTypes.INVALID_ROW;
    final double toleranceTimesHarrisRatio = params.getHarrisToleranceRatio() * tolerance;
    while (!breakpoints.isEmpty()) {
      final BreakPoint top = breakpoints.peek();
      if (top.coeffMagnitude > bestMagnitude) {
        result.leavingRow = top.row;
        currentRatio = top.ratio;
        bestMagnitude = top.coeffMagnitude;
        result.targetBound = top.targetBound;
      }
      // As long as the sum of primal infeasibilities is decreasing, look for pivots that are
      // numerically more stable. The tolerance is needed as improvement is the derivative of the
      // maximized function, which can have a flat spot at the peak and thus be close to zero but
      // not exactly zero due to numerical errors.
      improvement -= top.coeffMagnitude;
      if (improvement <= toleranceTimesHarrisRatio) {
        break;
      }
      breakpoints.poll();
    }
    if (result.leavingRow != LpTypes.INVALID_ROW) {
      final double threshold = params.getSmallPivotThreshold() * directionInfinityNorm;
      if (bestMagnitude < threshold && !basisFactorization.isRefactorized()) {
        result.refactorize = true;
        return;
      }
    }
    result.stepLength = currentRatio;
  }

  // returns {leavingRow, costVariation, targetBound}
  private double[] dualChooseLeavingVariableRow() {
    if (dualPrices.size() == 0) {
      variableValues.recomputeDualPrices(parameters.get().getDualPricePrioritizeNorm());
    }
    final int leavingRow = dualPrices.getMaximum();
    if (leavingRow == LpTypes.INVALID_ROW) {
      return new double[] {LpTypes.INVALID_ROW, 0.0, 0.0};
    }
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final int leavingCol = basis.get()[leavingRow];
    final double value = variableValues.get(leavingCol);
    if (value < lowerBounds[leavingCol]) {
      return new double[] {leavingRow, lowerBounds[leavingCol] - value, lowerBounds[leavingCol]};
    } else {
      return new double[] {leavingRow, upperBounds[leavingCol] - value, upperBounds[leavingCol]};
    }
  }

  private static boolean isDualPhaseILeavingCandidate(double cost, VariableType type,
      double threshold) {
    if (cost == 0.0) {
      return false;
    }
    return type == VariableType.UPPER_AND_LOWER_BOUNDED || type == VariableType.FIXED_VARIABLE
        || (type == VariableType.UPPER_BOUNDED && cost < -threshold)
        || (type == VariableType.LOWER_BOUNDED && cost > threshold);
  }

  private void onDualPriceChange(boolean useDenseUpdate, double[] squaredNorms, int row,
      VariableType type, double threshold) {
    final double price = dualPricingVector[row];
    final boolean isCandidate = isDualPhaseILeavingCandidate(price, type, threshold);
    if (isCandidate) {
      if (useDenseUpdate) {
        dualPrices.denseAddOrUpdate(row, price * price / squaredNorms[row]);
      } else {
        dualPrices.addOrUpdate(row, price * price / squaredNorms[row]);
      }
    } else {
      dualPrices.remove(row);
    }
  }

  private void dualPhaseIUpdatePrice(int leavingRow, int enteringCol) {
    if (reducedCosts.areReducedCostsRecomputed() || dualEdgeNorms.needsBasisRefactorization()
        || dualPricingVector.length == 0) {
      return;
    }
    final VariableType[] variableType = variablesInfo.getTypeRow();
    final double threshold = parameters.get().getRatioTestZeroThreshold();
    final double[] squaredNorms = dualEdgeNorms.getEdgeSquaredNorms();
    final int[] b = basis.get();
    final double step = dualPricingVector[leavingRow] / direction.values[leavingRow];
    for (int i = 0; i < direction.numNonZeros; ++i) {
      final int row = direction.nonZeros[i];
      dualPricingVector[row] -= direction.values[row] * step;
      onDualPriceChange(false, squaredNorms, row, variableType[b[row]], threshold);
    }
    dualPricingVector[leavingRow] = step;
    dualPricingVector[leavingRow] -= dualInfeasibilityImprovementDirection[enteringCol];
    if (dualInfeasibilityImprovementDirection[enteringCol] != 0.0) {
      --numDualInfeasiblePositions;
    }
    dualInfeasibilityImprovementDirection[enteringCol] = 0.0;
    dualInfeasibilityImprovementDirection[b[leavingRow]] = 0.0;
    onDualPriceChange(false, squaredNorms, leavingRow, variableType[enteringCol], threshold);
  }

  private void dualPhaseIUpdatePriceOnReducedCostChange(int[] cols, int numColsToUpdate) {
    boolean somethingToDo = false;
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final double[] rc = reducedCosts.getReducedCosts();
    final double tolerance = reducedCosts.getDualFeasibilityTolerance();
    final double[] improvementDirection = dualInfeasibilityImprovementDirection;
    for (int i = 0; i < numColsToUpdate; ++i) {
      final int col = cols[i];
      final double reducedCost = rc[col];
      final double sign = (canIncrease.get(col) && reducedCost < -tolerance) ? 1.0
          : (canDecrease.get(col) && reducedCost > tolerance) ? -1.0 : 0.0;
      if (sign != improvementDirection[col]) {
        if (sign == 0.0) {
          --numDualInfeasiblePositions;
        } else if (improvementDirection[col] == 0.0) {
          ++numDualInfeasiblePositions;
        }
        if (!somethingToDo) {
          initiallyAllZeroScratchpad.resize(numRows);
          initiallyAllZeroScratchpad.clearSparseMask();
          initiallyAllZeroScratchpad.clearNonZeros();
          somethingToDo = true;
        }
        numUpdatePriceOperations += 10L * compactMatrix.columnNumEntries(col);
        compactMatrix.columnAddMultipleToSparseScatteredColumn(col,
            sign - improvementDirection[col], initiallyAllZeroScratchpad);
        improvementDirection[col] = sign;
      }
    }
    if (somethingToDo) {
      initiallyAllZeroScratchpad.clearNonZerosIfTooDense();
      initiallyAllZeroScratchpad.clearSparseMask();
      final double[] squaredNorms = dualEdgeNorms.getEdgeSquaredNorms();
      final VariableType[] variableType = variablesInfo.getTypeRow();
      final double threshold = parameters.get().getRatioTestZeroThreshold();
      basisFactorization.rightSolve(initiallyAllZeroScratchpad);
      final int[] b = basis.get();
      if (initiallyAllZeroScratchpad.numNonZeros == 0) {
        dualPrices.startDenseUpdates();
        for (int row = 0; row < numRows; ++row) {
          if (initiallyAllZeroScratchpad.values[row] == 0.0) {
            continue;
          }
          dualPricingVector[row] += initiallyAllZeroScratchpad.values[row];
          onDualPriceChange(true, squaredNorms, row, variableType[b[row]], threshold);
        }
        initiallyAllZeroScratchpad.assignToZero(numRows);
      } else {
        for (int i = 0; i < initiallyAllZeroScratchpad.numNonZeros; ++i) {
          final int row = initiallyAllZeroScratchpad.nonZeros[i];
          dualPricingVector[row] += initiallyAllZeroScratchpad.values[row];
          onDualPriceChange(false, squaredNorms, row, variableType[b[row]], threshold);
          initiallyAllZeroScratchpad.values[row] = 0.0;
        }
      }
      initiallyAllZeroScratchpad.clearNonZeros();
    }
  }

  // returns {leavingRow, costVariation, targetBound}
  private double[] dualPhaseIChooseLeavingVariableRow() {
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    if (reducedCosts.areReducedCostsRecomputed() || dualEdgeNorms.needsBasisRefactorization()
        || dualPricingVector.length == 0) {
      numDualInfeasiblePositions = 0;
      dualPricingVector = new double[numRows];
      dualPrices.clearAndResize(numRows);
      dualInfeasibilityImprovementDirection = new double[numCols];
      final List<Integer> relevant = bitSetToList(variablesInfo.getIsRelevantBitRow());
      final int[] cols = new int[relevant.size()];
      for (int i = 0; i < cols.length; ++i) {
        cols[i] = relevant.get(i);
      }
      dualPhaseIUpdatePriceOnReducedCostChange(cols, cols.length);
    } else {
      dualPhaseIUpdatePriceOnReducedCostChange(updateRow.getNonZeroPositions(),
          updateRow.getNumNonZeroPositions());
    }
    if (numDualInfeasiblePositions == 0) {
      return new double[] {LpTypes.INVALID_ROW, 0.0, 0.0};
    }
    final int leavingRow = dualPrices.getMaximum();
    if (leavingRow == LpTypes.INVALID_ROW) {
      return new double[] {LpTypes.INVALID_ROW, 0.0, 0.0};
    }
    final double costVariation = dualPricingVector[leavingRow];
    final int leavingCol = basis.get()[leavingRow];
    final double targetBound =
        costVariation < 0.0 ? upperBounds[leavingCol] : lowerBounds[leavingCol];
    return new double[] {leavingRow, costVariation, targetBound};
  }

  private void makeBoxedVariableDualFeasible(List<Integer> cols, boolean updateBasicValues) {
    final List<Integer> changedCols = new ArrayList<>();
    final double threshold = reducedCosts.getDualFeasibilityTolerance();
    final double[] rc = reducedCosts.getReducedCosts();
    final VariableStatus[] status = variablesInfo.getStatusRow();
    for (int col : cols) {
      final double reducedCost = rc[col];
      final VariableStatus s = status[col];
      if (reducedCost > threshold && s == VariableStatus.AT_UPPER_BOUND) {
        variablesInfo.updateToNonBasicStatus(col, VariableStatus.AT_LOWER_BOUND);
        changedCols.add(col);
      } else if (reducedCost < -threshold && s == VariableStatus.AT_LOWER_BOUND) {
        variablesInfo.updateToNonBasicStatus(col, VariableStatus.AT_UPPER_BOUND);
        changedCols.add(col);
      }
    }
    if (!changedCols.isEmpty()) {
      variableValues.updateGivenNonBasicVariables(changedCols, updateBasicValues);
    }
  }

  private double computeStepToMoveBasicVariableToBound(int leavingRow, double targetBound) {
    final int leavingCol = basis.get()[leavingRow];
    final double leavingVariableValue = variableValues.get(leavingCol);
    final double unscaledStep = leavingVariableValue - targetBound;
    return unscaledStep / direction.values[leavingRow];
  }

  private boolean testPivot(int enteringCol, int leavingRow) {
    final int[] b = basis.get();
    final int leavingCol = b[leavingRow];
    b[leavingRow] = enteringCol;
    final boolean isOk = testLu.computeFactorization(compactMatrix, b).ok();
    b[leavingRow] = leavingCol;
    return isOk;
  }

  private void permuteBasis() {
    final int[] colPerm = basisFactorization.getColumnPermutation();
    if (colPerm.length == 0) {
      return;
    }
    LpUtils.applyColumnPermutationToRowIndexedVector(colPerm, basis.get());
    if (dualPricingVector.length != 0) {
      LpUtils.applyColumnPermutationToRowIndexedVector(colPerm, dualPricingVector);
    }
    reducedCosts.updateDataOnBasisPermutation();
    dualEdgeNorms.updateDataOnBasisPermutation(colPerm);
    basisFactorization.setColumnPermutationToIdentity();
  }

  private Status updateAndPivot(int enteringCol, int leavingRow, double targetBound) {
    final GlopParameters params = parameters.get();
    double pivotFromUpdateRow;
    if (updateRow.isComputedFor(leavingRow)) {
      pivotFromUpdateRow = updateRow.getCoefficient(enteringCol);
    } else {
      updateRow.computeUnitRowLeftInverse(leavingRow);
      pivotFromUpdateRow = compactMatrix.columnScalarProduct(enteringCol,
          updateRow.getUnitRowLeftInverse().values);
    }
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final int leavingCol = basis.get()[leavingRow];
    final VariableStatus leavingVariableStatus =
        lowerBounds[leavingCol] == upperBounds[leavingCol] ? VariableStatus.FIXED_VALUE
            : targetBound == lowerBounds[leavingCol] ? VariableStatus.AT_LOWER_BOUND
                : VariableStatus.AT_UPPER_BOUND;
    updateBasis(enteringCol, leavingRow, leavingVariableStatus);
    final double pivotFromDirection = direction.values[leavingRow];
    final double diff = Math.abs(pivotFromUpdateRow - pivotFromDirection);
    Status status;
    if (diff > params.getRefactorizationThreshold()
        * (1.0 + Math.min(Math.abs(pivotFromUpdateRow), Math.abs(pivotFromDirection)))) {
      if (basisFactorization.numUpdates() < 10) {
        double threshold = params.getLuFactorizationPivotThreshold();
        threshold = Math.min(threshold * 1.5, 0.9);
        params.setLuFactorizationPivotThreshold(threshold);
        basisFactorization.setParameters(params);
      }
      status = basisFactorization.forceRefactorization();
    } else {
      status = basisFactorization.update(enteringCol, leavingRow, direction);
    }
    if (!status.ok()) {
      return status;
    }
    if (basisFactorization.isRefactorized()) {
      permuteBasis();
    }
    return Status.OK;
  }

  private Status refactorizeBasisIfNeeded(boolean[] refactorize) {
    if (refactorize[0] && !basisFactorization.isRefactorized()) {
      final Status status = basisFactorization.refactorize();
      if (!status.ok()) {
        return status;
      }
      updateRow.invalidate();
      permuteBasis();
    }
    refactorize[0] = false;
    return Status.OK;
  }

  private Status primalMinimize(TimeLimit timeLimit) {
    try {
      return primalMinimizeImpl(timeLimit);
    } finally {
      advanceDeterministicTime(timeLimit);
    }
  }

  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "the step is exact for a non-degenerate pivot, as in C++")
  private Status primalMinimizeImpl(TimeLimit timeLimit) {
    final GlopParameters params = parameters.get();
    numConsecutiveDegenerateIterations = 0;
    final boolean[] refactorize = {false};
    primalPrices.forceRecomputation();
    if (phase == Phase.FEASIBILITY) {
      objective.set(new double[numCols]);
      variableValues.updatePrimalPhaseICosts(null, numRows, objective.get());
      reducedCosts.resetForNewObjective();
    }
    final LeavingChoice choice = new LeavingChoice();
    while (true) {
      advanceDeterministicTime(timeLimit);
      if (timeLimit.limitReached()) {
        break;
      }
      if (!refactorize[0] && reducedCosts.needsBasisRefactorization()) {
        refactorize[0] = true;
      }
      if (!refactorize[0] && primalEdgeNorms.needsBasisRefactorization()) {
        refactorize[0] = true;
      }
      Status status = refactorizeBasisIfNeeded(refactorize);
      if (!status.ok()) {
        return status;
      }
      if (basisFactorization.isRefactorized()) {
        correctErrorsOnVariableValues();
        displayIterationInfo(true);
        if (phase == Phase.FEASIBILITY) {
          if (variableValues.updatePrimalPhaseICosts(null, numRows, objective.get())) {
            reducedCosts.resetForNewObjective();
          }
        }
        if (phase == Phase.OPTIMIZATION && computeObjectiveValue() < primalObjectiveLimit) {
          problemStatus = ProblemStatus.PRIMAL_FEASIBLE;
          objectiveLimitReached = true;
          return Status.OK;
        }
      } else if (phase == Phase.FEASIBILITY) {
        if (variableValues.updatePrimalPhaseICosts(direction.nonZeros, direction.numNonZeros,
            objective.get())) {
          reducedCosts.resetForNewObjective();
        }
      }

      final int enteringCol = primalPrices.getBestEnteringColumn();
      if (enteringCol == LpTypes.INVALID_COL) {
        if (reducedCosts.areReducedCostsPrecise() && basisFactorization.isRefactorized()) {
          if (phase == Phase.FEASIBILITY) {
            final double primalInfeasibility = variableValues.computeMaximumPrimalInfeasibility();
            if (primalInfeasibility < params.getPrimalFeasibilityTolerance()) {
              problemStatus = ProblemStatus.PRIMAL_FEASIBLE;
            } else {
              problemStatus = ProblemStatus.PRIMAL_INFEASIBLE;
            }
          } else {
            problemStatus = ProblemStatus.OPTIMAL;
          }
          break;
        }
        reducedCosts.makeReducedCostsPrecise();
        refactorize[0] = true;
        continue;
      }

      computeDirection(enteringCol);
      if (!primalEdgeNorms.testEnteringEdgeNormPrecision(enteringCol, direction)) {
        primalPrices.recomputePriceAt(enteringCol);
        continue;
      }
      final double reducedCost =
          reducedCosts.testEnteringReducedCostPrecision(enteringCol, direction);
      primalPrices.recomputePriceAt(enteringCol);
      if (!reducedCosts.isValidPrimalEnteringCandidate(enteringCol)) {
        reducedCosts.makeReducedCostsPrecise();
        continue;
      }
      if (numIterations == params.getMaxNumberOfIterations()) {
        break;
      }

      choice.refactorize = false;
      choice.leavingRow = LpTypes.INVALID_ROW;
      if (phase == Phase.FEASIBILITY) {
        primalPhaseIChooseLeavingVariableRow(enteringCol, reducedCost, choice);
      } else {
        status = chooseLeavingVariableRow(enteringCol, reducedCost, choice);
        if (!status.ok()) {
          return status;
        }
      }
      if (choice.refactorize) {
        refactorize[0] = true;
        continue;
      }
      final double stepLength = choice.stepLength;
      final int leavingRow = choice.leavingRow;
      final double targetBound = choice.targetBound;

      if (Double.isInfinite(stepLength)) {
        if (!basisFactorization.isRefactorized() || !reducedCosts.areReducedCostsPrecise()) {
          reducedCosts.makeReducedCostsPrecise();
          refactorize[0] = true;
          continue;
        }
        if (phase == Phase.FEASIBILITY) {
          problemStatus = ProblemStatus.ABNORMAL;
        } else {
          problemStatus = ProblemStatus.PRIMAL_UNBOUNDED;
          solutionPrimalRay = new double[numCols];
          final int[] b = basis.get();
          for (int row = 0; row < numRows; ++row) {
            solutionPrimalRay[b[row]] = -direction.values[row];
          }
          solutionPrimalRay[enteringCol] = 1.0;
          if (reducedCost > 0.0) {
            LpUtils.changeSign(solutionPrimalRay);
          }
        }
        break;
      }

      double step = (reducedCost > 0.0) ? -stepLength : stepLength;
      if (phase == Phase.FEASIBILITY && leavingRow != LpTypes.INVALID_ROW) {
        step = computeStepToMoveBasicVariableToBound(leavingRow, targetBound);
      }
      final int leavingCol =
          (leavingRow == LpTypes.INVALID_ROW) ? LpTypes.INVALID_COL : basis.get()[leavingRow];
      boolean isDegenerate = false;
      if (leavingRow != LpTypes.INVALID_ROW) {
        // Also check stepToBound == 0.0 in case (val - targetBound) / dir underflows to 0.0 and
        // triggers a degenerate ministep.
        final double stepToBound = computeStepToMoveBasicVariableToBound(leavingRow, targetBound);
        final double dir = -direction.values[leavingRow] * step;
        isDegenerate = (dir == 0.0) || (stepToBound == 0.0)
            || (dir > 0.0 && variableValues.get(leavingCol) >= targetBound)
            || (dir < 0.0 && variableValues.get(leavingCol) <= targetBound);
        // If the iteration is not degenerate, the leaving variable goes to its exact target bound
        // (it is how the step is computed).
        assert isDegenerate || step == stepToBound : "step " + step + " != " + stepToBound;
      }

      variableValues.updateOnPivoting(direction, enteringCol, step);
      if (leavingRow != LpTypes.INVALID_ROW) {
        primalEdgeNorms.updateBeforeBasisPivot(enteringCol, basis.get()[leavingRow], leavingRow,
            direction, updateRow);
        reducedCosts.updateBeforeBasisPivot(enteringCol, leavingRow, direction, updateRow);
        primalPrices.updateBeforeBasisPivot(enteringCol, updateRow);
        if (!isDegenerate) {
          variableValues.set(leavingCol, targetBound);
        }
        status = updateAndPivot(enteringCol, leavingRow, targetBound);
        if (!status.ok()) {
          return status;
        }
      } else {
        if (step > 0.0) {
          setNonBasicVariableStatusAndDeriveValue(enteringCol, VariableStatus.AT_UPPER_BOUND);
        } else if (step < 0.0) {
          setNonBasicVariableStatusAndDeriveValue(enteringCol, VariableStatus.AT_LOWER_BOUND);
        }
        primalPrices.setAndDebugCheckThatColumnIsDualFeasible(enteringCol);
      }

      if (phase == Phase.FEASIBILITY && leavingRow != LpTypes.INVALID_ROW) {
        variableValues.setNonBasicVariableValueFromStatus(leavingCol);
        reducedCosts.setNonBasicVariableCostToZero(leavingCol);
        primalPrices.recomputePriceAt(leavingCol);
      }

      if (stepLength == 0.0) {
        numConsecutiveDegenerateIterations++;
      } else {
        numConsecutiveDegenerateIterations = 0;
      }
      ++numIterations;
    }
    return Status.OK;
  }

  private Status dualMinimize(boolean feasibilityPhase, TimeLimit timeLimit) {
    try {
      return dualMinimizeImpl(feasibilityPhase, timeLimit);
    } finally {
      advanceDeterministicTime(timeLimit);
    }
  }

  private Status dualMinimizeImpl(boolean feasibilityPhase, TimeLimit timeLimit) {
    final GlopParameters params = parameters.get();
    numConsecutiveDegenerateIterations = 0;
    final boolean[] refactorize = {false};
    boundFlipCandidates.clear();
    int leavingRow;
    double costVariation;
    double targetBound;
    int enteringCol;
    while (true) {
      advanceDeterministicTime(timeLimit);
      if (timeLimit.limitReached()) {
        break;
      }
      final boolean oldRefactorizeValue = refactorize[0];
      if (!refactorize[0] && reducedCosts.needsBasisRefactorization()) {
        refactorize[0] = true;
      }
      if (!refactorize[0] && dualEdgeNorms.needsBasisRefactorization()) {
        refactorize[0] = true;
      }
      Status status = refactorizeBasisIfNeeded(refactorize);
      if (!status.ok()) {
        return status;
      }
      if (basisFactorization.isRefactorized()) {
        if (feasibilityPhase || oldRefactorizeValue) {
          reducedCosts.makeReducedCostsPrecise();
        }
        if (!feasibilityPhase) {
          makeBoxedVariableDualFeasible(bitSetToList(variablesInfo.getNonBasicBoxedVariables()),
              false);
          variableValues.recomputeBasicVariableValues();
          variableValues.recomputeDualPrices(params.getDualPricePrioritizeNorm());
          if (phase == Phase.OPTIMIZATION && dualObjectiveLimit != LpTypes.INFINITY
              && computeObjectiveValue() > dualObjectiveLimit) {
            logger.log("Stopping the dual simplex because the objective limit ",
                dualObjectiveLimit, " has been reached.");
            problemStatus = ProblemStatus.DUAL_FEASIBLE;
            objectiveLimitReached = true;
            return Status.OK;
          }
        }
        displayIterationInfo(false);
      } else {
        if (!feasibilityPhase) {
          makeBoxedVariableDualFeasible(new ArrayList<>(boundFlipCandidates), true);
          boundFlipCandidates.clear();
          variableValues.updateDualPrices(direction.nonZeros, direction.numNonZeros);
        }
      }

      final double[] leaving = feasibilityPhase ? dualPhaseIChooseLeavingVariableRow()
          : dualChooseLeavingVariableRow();
      leavingRow = (int) leaving[0];
      costVariation = leaving[1];
      targetBound = leaving[2];
      if (leavingRow == LpTypes.INVALID_ROW) {
        if (!basisFactorization.isRefactorized() || reducedCosts.hasCostShift()) {
          reducedCosts.clearAndRemoveCostShifts();
          refactorize[0] = true;
          continue;
        }
        if (feasibilityPhase) {
          if (numDualInfeasiblePositions == 0) {
            problemStatus = ProblemStatus.DUAL_FEASIBLE;
          } else {
            problemStatus = ProblemStatus.DUAL_INFEASIBLE;
          }
        } else {
          problemStatus = ProblemStatus.OPTIMAL;
        }
        return Status.OK;
      }

      updateRow.computeUnitRowLeftInverse(leavingRow);
      if (!dualEdgeNorms.testPrecision(leavingRow, updateRow.getUnitRowLeftInverse())) {
        if (feasibilityPhase) {
          final double price = dualPricingVector[leavingRow];
          final double[] squaredNorms = dualEdgeNorms.getEdgeSquaredNorms();
          dualPrices.addOrUpdate(leavingRow, price * price / squaredNorms[leavingRow]);
        } else {
          variableValues.updateDualPrices(new int[] {leavingRow}, 1);
        }
        continue;
      }
      updateRow.computeUpdateRow(leavingRow);

      if (feasibilityPhase) {
        enteringCol = enteringVariable.dualPhaseIChooseEnteringColumn(
            reducedCosts.areReducedCostsPrecise(), updateRow, costVariation);
      } else {
        enteringCol = enteringVariable.dualChooseEnteringColumn(
            reducedCosts.areReducedCostsPrecise(), updateRow, costVariation,
            boundFlipCandidates);
      }

      if (enteringCol == LpTypes.INVALID_COL) {
        if (!reducedCosts.areReducedCostsPrecise()) {
          refactorize[0] = true;
          continue;
        }
        if (feasibilityPhase) {
          problemStatus = ProblemStatus.ABNORMAL;
        } else {
          problemStatus = ProblemStatus.DUAL_UNBOUNDED;
          solutionDualRay = updateRow.getUnitRowLeftInverse().values.clone();
          solutionDualRayRowCombination = updateRow.computeFullUpdateRow(leavingRow);
          if (costVariation < 0) {
            LpUtils.changeSign(solutionDualRay);
            LpUtils.changeSign(solutionDualRayRowCombination);
          }
        }
        return Status.OK;
      }

      final double enteringCoeff = updateRow.getCoefficient(enteringCol);
      if (Math.abs(enteringCoeff) < params.getDualSmallPivotThreshold()
          && !reducedCosts.areReducedCostsPrecise()) {
        refactorize[0] = true;
        continue;
      }

      computeDirection(enteringCol);
      if (Math.abs(direction.values[leavingRow]) < params.getSmallPivotThreshold()
          * directionInfinityNorm) {
        if (!reducedCosts.areReducedCostsPrecise()) {
          refactorize[0] = true;
          continue;
        }
      }
      if (Math.abs(direction.values[leavingRow]) <= 1e-20) {
        final String errorMessage =
            "trying to pivot with number too small: " + direction.values[leavingRow];
        logger.log(errorMessage);
        return new Status(Status.ErrorCode.ERROR_LU, errorMessage);
      }
      if (numIterations == params.getMaxNumberOfIterations()) {
        return Status.OK;
      }

      final boolean increasingRcIsNeeded = (costVariation > 0.0) == (enteringCoeff > 0.0);
      reducedCosts.shiftCostIfNeeded(increasingRcIsNeeded, enteringCol);

      reducedCosts.updateBeforeBasisPivot(enteringCol, leavingRow, direction, updateRow);
      dualEdgeNorms.updateBeforeBasisPivot(enteringCol, leavingRow, direction,
          updateRow.getUnitRowLeftInverse());

      if (feasibilityPhase) {
        dualPhaseIUpdatePrice(leavingRow, enteringCol);
      } else {
        final double primalStep = computeStepToMoveBasicVariableToBound(leavingRow, targetBound);
        variableValues.updateOnPivoting(direction, enteringCol, primalStep);
      }

      final int leavingCol = basis.get()[leavingRow];
      status = updateAndPivot(enteringCol, leavingRow, targetBound);
      if (!status.ok()) {
        return status;
      }
      variableValues.setNonBasicVariableValueFromStatus(leavingCol);
      ++numIterations;
    }
    return Status.OK;
  }

  private int slackColIndex(int row) {
    return firstSlackCol + row;
  }

  private double computeObjectiveValue() {
    return LpUtils.preciseScalarProduct(objective.get(), variableValues.getDenseRow());
  }

  private double computeInitialProblemObjectiveValue() {
    final double sum =
        LpUtils.preciseScalarProduct(objective.get(), variableValues.getDenseRow());
    return objectiveScalingFactor * (sum + objectiveOffset);
  }

  private void displayIterationInfo(boolean primal) {
    if (!logger.loggingIsEnabled()) {
      return;
    }
    final String firstWord = primal ? "Primal " : "Dual ";
    switch (phase) {
      case FEASIBILITY: {
        final long iter = numIterations;
        String name;
        double obj;
        if (parameters.get().getUseDualSimplex()) {
          if (parameters.get().getUseDedicatedDualFeasibilityAlgorithm()) {
            obj = reducedCosts.computeSumOfDualInfeasibilities();
          } else {
            obj = -LpUtils.preciseScalarProduct(objective.get(), variableValues.getDenseRow());
          }
          name = "sum_dual_infeasibilities";
        } else {
          obj = variableValues.computeSumOfPrimalInfeasibilities();
          name = "sum_primal_infeasibilities";
        }
        logger.log(firstWord, "feasibility phase, iteration # ", iter, ", ", name, " = ",
            String.format(Locale.ROOT, "%.15E", obj));
        break;
      }
      case OPTIMIZATION: {
        final long iter = numIterations - numFeasibilityIterations;
        final double obj = computeInitialProblemObjectiveValue();
        logger.log(firstWord, "optimization phase, iteration # ", iter, ", objective = ",
            String.format(Locale.ROOT, "%.15E", obj));
        break;
      }
      default:
        break;
    }
  }

  private void displayErrors() {
    if (!logger.loggingIsEnabled()) {
      return;
    }
    logger.log("Current status: ", problemStatus);
    logger.log("Primal infeasibility (bounds) = ",
        variableValues.computeMaximumPrimalInfeasibility());
    logger.log("Primal residual |A.x - b| = ", variableValues.computeMaximumPrimalResidual());
    logger.log("Dual infeasibility (reduced costs) = ",
        reducedCosts.computeMaximumDualInfeasibility());
    logger.log("Dual residual |c_B - y.B| = ", reducedCosts.computeMaximumDualResidual());
  }

  /**
   * Computes the dictionary B^-1.A row by row: entry (row, col) is the coefficient of column col
   * in the basic variable of the given row.
   */
  public double[][] computeDictionary() {
    final double[][] dictionary = new double[numRows][numCols];
    for (int col = 0; col < numCols; ++col) {
      computeDirection(col);
      for (int i = 0; i < direction.numNonZeros; ++i) {
        final int row = direction.nonZeros[i];
        dictionary[row][col] = direction.values[row];
      }
    }
    return dictionary;
  }

  private void advanceDeterministicTime(TimeLimit timeLimit) {
    final double currentDeterministicTime = deterministicTime();
    final double deterministicTimeDelta = currentDeterministicTime - lastDeterministicTimeUpdate;
    timeLimit.advanceDeterministicTime(deterministicTimeDelta);
    lastDeterministicTimeUpdate = currentDeterministicTime;
  }

  /** Debug helper: returns true if the basis and the statuses are consistent. */
  boolean debugBasisIsConsistent() {
    return basisIsConsistent();
  }

  /** Debug helper: returns true if the basis obtained by the pivot can be factorized. */
  boolean debugTestPivot(int enteringCol, int leavingRow) {
    return testPivot(enteringCol, leavingRow);
  }
}
