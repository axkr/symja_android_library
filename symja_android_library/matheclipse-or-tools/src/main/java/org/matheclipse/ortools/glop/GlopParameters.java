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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/parameters.proto
// to a plain Java class, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Locale;

/**
 * The parameters of the Glop LP solver, a plain Java version of the <code>GlopParameters</code>
 * protocol buffer with the same field names and defaults. Getters are named <code>getXxx()</code>,
 * setters <code>setXxx()</code> and return <code>this</code>.
 *
 * <p>
 * {@link #mergeFromString(String)} accepts the protocol buffer text format, for example
 * <code>"use_dual_simplex: true max_time_in_seconds: 10"</code>.
 */
public final class GlopParameters {

  /** Values of the corresponding proto enum. */
  public enum ScalingAlgorithm {
    DEFAULT, EQUILIBRATION, LINEAR_PROGRAM,
    /** Ruiz equilibration to convergence, see {@code SparseMatrixScaler#ruizEquilibrate}. */
    RUIZ;
  }

  /** Values of the corresponding proto enum. */
  public enum SolverBehavior {
    ALWAYS_DO, NEVER_DO, LET_SOLVER_DECIDE;
  }

  /** Values of the corresponding proto enum. */
  public enum PricingRule {
    DANTZIG, STEEPEST_EDGE, DEVEX;
  }

  /** Values of the corresponding proto enum. */
  public enum InitialBasisHeuristic {
    NONE, BIXBY, TRIANGULAR, MAROS;
  }

  /** Values of the corresponding proto enum. */
  public enum CostScalingAlgorithm {
    NO_COST_SCALING, CONTAIN_ONE_COST_SCALING, MEAN_COST_SCALING, MEDIAN_COST_SCALING;
  }

  private ScalingAlgorithm scalingMethod = ScalingAlgorithm.EQUILIBRATION;
  private PricingRule feasibilityRule = PricingRule.STEEPEST_EDGE;
  private PricingRule optimizationRule = PricingRule.STEEPEST_EDGE;
  private double refactorizationThreshold = 1e-9;
  private double recomputeReducedCostsThreshold = 1e-8;
  private double recomputeEdgesNormThreshold = 100.0;
  private double primalFeasibilityTolerance = 1e-8;
  private double dualFeasibilityTolerance = 1e-8;
  private double ratioTestZeroThreshold = 1e-9;
  private double harrisToleranceRatio = 0.5;
  private double smallPivotThreshold = 1e-6;
  private double minimumAcceptablePivot = 1e-6;
  private double dropTolerance = 1e-14;
  private boolean useScaling = true;
  private CostScalingAlgorithm costScaling = CostScalingAlgorithm.CONTAIN_ONE_COST_SCALING;
  private InitialBasisHeuristic initialBasis = InitialBasisHeuristic.TRIANGULAR;
  private boolean useTransposedMatrix = true;
  private int basisRefactorizationPeriod = 64;
  private boolean dynamicallyAdjustRefactorizationPeriod = true;
  private SolverBehavior solveDualProblem = SolverBehavior.LET_SOLVER_DECIDE;
  private double dualizerThreshold = 1.5;
  private double solutionFeasibilityTolerance = 1e-6;
  private boolean provideStrongOptimalGuarantee = true;
  private boolean changeStatusToImprecise = true;
  private double maxNumberOfReoptimizations = 40;
  private double luFactorizationPivotThreshold = 0.01;
  private double maxTimeInSeconds = Double.POSITIVE_INFINITY;
  private double maxDeterministicTime = Double.POSITIVE_INFINITY;
  private long maxNumberOfIterations = -1L;
  private int markowitzZlatevParameter = 3;
  private double markowitzSingularityThreshold = 1e-15;
  private boolean useDualSimplex = false;
  private boolean allowSimplexAlgorithmChange = false;
  private int devexWeightsResetPeriod = 150;
  private boolean usePreprocessing = true;
  private boolean useMiddleProductFormUpdate = true;
  private boolean initializeDevexWithColumnNorms = true;
  private boolean exploitSingletonColumnInInitialBasis = true;
  private double dualSmallPivotThreshold = 1e-4;
  private double preprocessorZeroTolerance = 1e-9;
  private double objectiveLowerLimit = Double.NEGATIVE_INFINITY;
  private double objectiveUpperLimit = Double.POSITIVE_INFINITY;
  private double degenerateMinistepFactor = 0.01;
  private int randomSeed = 1;
  private boolean useAbslRandom = false;
  private int numOmpThreads = 1;
  private boolean perturbCostsInDualSimplex = false;
  private boolean useDedicatedDualFeasibilityAlgorithm = true;
  private double relativeCostPerturbation = 1e-5;
  private double relativeMaxCostPerturbation = 1e-7;
  private double initialConditionNumberThreshold = 1e50;
  private boolean logSearchProgress = false;
  private boolean logToStdout = true;
  private double crossoverBoundSnappingDistance = Double.POSITIVE_INFINITY;
  private boolean pushToVertex = true;
  private boolean useImpliedFreePreprocessor = true;
  private double maxValidMagnitude = 1e30;
  private double dropMagnitude = 1e-30;
  private boolean dualPricePrioritizeNorm = false;
  private boolean primalPolish = false;
  private boolean dualPolish = false;
  private boolean useShiftVariableBoundsPreprocessor = true;
  private boolean useFixedVariablePreprocessor = true;
  private boolean useSingletonPreprocessor = true;
  private boolean useForcingAndImpliedFreeConstraintPreprocessor = true;
  private boolean useFreeConstraintPreprocessor = true;
  private boolean useUnconstrainedVariablePreprocessor = true;
  private boolean useDoubletonFreeColumnPreprocessor = true;
  private boolean useDoubletonEqualityRowPreprocessor = true;
  private boolean useEmptyColumnPreprocessor = true;
  private boolean useEmptyConstraintPreprocessor = true;
  private boolean useProportionalColumnPreprocessor = true;
  private boolean useProportionalRowPreprocessor = true;
  private boolean useSingletonColumnSignPreprocessor = true;
  private boolean useDualizerPreprocessor = true;
  private boolean useBoundPropagationPreprocessor = true;

  /** Creates the default parameters. */
  public GlopParameters() {}

  /** Returns a copy of these parameters. */
  public GlopParameters copy() {
    GlopParameters p = new GlopParameters();
    p.mergeFrom(this);
    return p;
  }

  /** Copies all the fields of <code>other</code> into this object. */
  public void mergeFrom(GlopParameters other) {
    this.scalingMethod = other.scalingMethod;
    this.feasibilityRule = other.feasibilityRule;
    this.optimizationRule = other.optimizationRule;
    this.refactorizationThreshold = other.refactorizationThreshold;
    this.recomputeReducedCostsThreshold = other.recomputeReducedCostsThreshold;
    this.recomputeEdgesNormThreshold = other.recomputeEdgesNormThreshold;
    this.primalFeasibilityTolerance = other.primalFeasibilityTolerance;
    this.dualFeasibilityTolerance = other.dualFeasibilityTolerance;
    this.ratioTestZeroThreshold = other.ratioTestZeroThreshold;
    this.harrisToleranceRatio = other.harrisToleranceRatio;
    this.smallPivotThreshold = other.smallPivotThreshold;
    this.minimumAcceptablePivot = other.minimumAcceptablePivot;
    this.dropTolerance = other.dropTolerance;
    this.useScaling = other.useScaling;
    this.costScaling = other.costScaling;
    this.initialBasis = other.initialBasis;
    this.useTransposedMatrix = other.useTransposedMatrix;
    this.basisRefactorizationPeriod = other.basisRefactorizationPeriod;
    this.dynamicallyAdjustRefactorizationPeriod = other.dynamicallyAdjustRefactorizationPeriod;
    this.solveDualProblem = other.solveDualProblem;
    this.dualizerThreshold = other.dualizerThreshold;
    this.solutionFeasibilityTolerance = other.solutionFeasibilityTolerance;
    this.provideStrongOptimalGuarantee = other.provideStrongOptimalGuarantee;
    this.changeStatusToImprecise = other.changeStatusToImprecise;
    this.maxNumberOfReoptimizations = other.maxNumberOfReoptimizations;
    this.luFactorizationPivotThreshold = other.luFactorizationPivotThreshold;
    this.maxTimeInSeconds = other.maxTimeInSeconds;
    this.maxDeterministicTime = other.maxDeterministicTime;
    this.maxNumberOfIterations = other.maxNumberOfIterations;
    this.markowitzZlatevParameter = other.markowitzZlatevParameter;
    this.markowitzSingularityThreshold = other.markowitzSingularityThreshold;
    this.useDualSimplex = other.useDualSimplex;
    this.allowSimplexAlgorithmChange = other.allowSimplexAlgorithmChange;
    this.devexWeightsResetPeriod = other.devexWeightsResetPeriod;
    this.usePreprocessing = other.usePreprocessing;
    this.useMiddleProductFormUpdate = other.useMiddleProductFormUpdate;
    this.initializeDevexWithColumnNorms = other.initializeDevexWithColumnNorms;
    this.exploitSingletonColumnInInitialBasis = other.exploitSingletonColumnInInitialBasis;
    this.dualSmallPivotThreshold = other.dualSmallPivotThreshold;
    this.preprocessorZeroTolerance = other.preprocessorZeroTolerance;
    this.objectiveLowerLimit = other.objectiveLowerLimit;
    this.objectiveUpperLimit = other.objectiveUpperLimit;
    this.degenerateMinistepFactor = other.degenerateMinistepFactor;
    this.randomSeed = other.randomSeed;
    this.useAbslRandom = other.useAbslRandom;
    this.numOmpThreads = other.numOmpThreads;
    this.perturbCostsInDualSimplex = other.perturbCostsInDualSimplex;
    this.useDedicatedDualFeasibilityAlgorithm = other.useDedicatedDualFeasibilityAlgorithm;
    this.relativeCostPerturbation = other.relativeCostPerturbation;
    this.relativeMaxCostPerturbation = other.relativeMaxCostPerturbation;
    this.initialConditionNumberThreshold = other.initialConditionNumberThreshold;
    this.logSearchProgress = other.logSearchProgress;
    this.logToStdout = other.logToStdout;
    this.crossoverBoundSnappingDistance = other.crossoverBoundSnappingDistance;
    this.pushToVertex = other.pushToVertex;
    this.useImpliedFreePreprocessor = other.useImpliedFreePreprocessor;
    this.maxValidMagnitude = other.maxValidMagnitude;
    this.dropMagnitude = other.dropMagnitude;
    this.dualPricePrioritizeNorm = other.dualPricePrioritizeNorm;
    this.primalPolish = other.primalPolish;
    this.dualPolish = other.dualPolish;
    this.useShiftVariableBoundsPreprocessor = other.useShiftVariableBoundsPreprocessor;
    this.useFixedVariablePreprocessor = other.useFixedVariablePreprocessor;
    this.useSingletonPreprocessor = other.useSingletonPreprocessor;
    this.useForcingAndImpliedFreeConstraintPreprocessor = other.useForcingAndImpliedFreeConstraintPreprocessor;
    this.useFreeConstraintPreprocessor = other.useFreeConstraintPreprocessor;
    this.useUnconstrainedVariablePreprocessor = other.useUnconstrainedVariablePreprocessor;
    this.useDoubletonFreeColumnPreprocessor = other.useDoubletonFreeColumnPreprocessor;
    this.useDoubletonEqualityRowPreprocessor = other.useDoubletonEqualityRowPreprocessor;
    this.useEmptyColumnPreprocessor = other.useEmptyColumnPreprocessor;
    this.useEmptyConstraintPreprocessor = other.useEmptyConstraintPreprocessor;
    this.useProportionalColumnPreprocessor = other.useProportionalColumnPreprocessor;
    this.useProportionalRowPreprocessor = other.useProportionalRowPreprocessor;
    this.useSingletonColumnSignPreprocessor = other.useSingletonColumnSignPreprocessor;
    this.useDualizerPreprocessor = other.useDualizerPreprocessor;
    this.useBoundPropagationPreprocessor = other.useBoundPropagationPreprocessor;
  }

  /** Resets all fields to their default values. */
  public void clear() {
    mergeFrom(new GlopParameters());
  }

  /** The <code>scaling_method</code> field. */
  public ScalingAlgorithm getScalingMethod() {
    return scalingMethod;
  }

  /** Sets the <code>scaling_method</code> field. */
  public GlopParameters setScalingMethod(ScalingAlgorithm value) {
    this.scalingMethod = value;
    return this;
  }

  /** The <code>feasibility_rule</code> field. */
  public PricingRule getFeasibilityRule() {
    return feasibilityRule;
  }

  /** Sets the <code>feasibility_rule</code> field. */
  public GlopParameters setFeasibilityRule(PricingRule value) {
    this.feasibilityRule = value;
    return this;
  }

  /** The <code>optimization_rule</code> field. */
  public PricingRule getOptimizationRule() {
    return optimizationRule;
  }

  /** Sets the <code>optimization_rule</code> field. */
  public GlopParameters setOptimizationRule(PricingRule value) {
    this.optimizationRule = value;
    return this;
  }

  /** The <code>refactorization_threshold</code> field. */
  public double getRefactorizationThreshold() {
    return refactorizationThreshold;
  }

  /** Sets the <code>refactorization_threshold</code> field. */
  public GlopParameters setRefactorizationThreshold(double value) {
    this.refactorizationThreshold = value;
    return this;
  }

  /** The <code>recompute_reduced_costs_threshold</code> field. */
  public double getRecomputeReducedCostsThreshold() {
    return recomputeReducedCostsThreshold;
  }

  /** Sets the <code>recompute_reduced_costs_threshold</code> field. */
  public GlopParameters setRecomputeReducedCostsThreshold(double value) {
    this.recomputeReducedCostsThreshold = value;
    return this;
  }

  /** The <code>recompute_edges_norm_threshold</code> field. */
  public double getRecomputeEdgesNormThreshold() {
    return recomputeEdgesNormThreshold;
  }

  /** Sets the <code>recompute_edges_norm_threshold</code> field. */
  public GlopParameters setRecomputeEdgesNormThreshold(double value) {
    this.recomputeEdgesNormThreshold = value;
    return this;
  }

  /** The <code>primal_feasibility_tolerance</code> field. */
  public double getPrimalFeasibilityTolerance() {
    return primalFeasibilityTolerance;
  }

  /** Sets the <code>primal_feasibility_tolerance</code> field. */
  public GlopParameters setPrimalFeasibilityTolerance(double value) {
    this.primalFeasibilityTolerance = value;
    return this;
  }

  /** The <code>dual_feasibility_tolerance</code> field. */
  public double getDualFeasibilityTolerance() {
    return dualFeasibilityTolerance;
  }

  /** Sets the <code>dual_feasibility_tolerance</code> field. */
  public GlopParameters setDualFeasibilityTolerance(double value) {
    this.dualFeasibilityTolerance = value;
    return this;
  }

  /** The <code>ratio_test_zero_threshold</code> field. */
  public double getRatioTestZeroThreshold() {
    return ratioTestZeroThreshold;
  }

  /** Sets the <code>ratio_test_zero_threshold</code> field. */
  public GlopParameters setRatioTestZeroThreshold(double value) {
    this.ratioTestZeroThreshold = value;
    return this;
  }

  /** The <code>harris_tolerance_ratio</code> field. */
  public double getHarrisToleranceRatio() {
    return harrisToleranceRatio;
  }

  /** Sets the <code>harris_tolerance_ratio</code> field. */
  public GlopParameters setHarrisToleranceRatio(double value) {
    this.harrisToleranceRatio = value;
    return this;
  }

  /** The <code>small_pivot_threshold</code> field. */
  public double getSmallPivotThreshold() {
    return smallPivotThreshold;
  }

  /** Sets the <code>small_pivot_threshold</code> field. */
  public GlopParameters setSmallPivotThreshold(double value) {
    this.smallPivotThreshold = value;
    return this;
  }

  /** The <code>minimum_acceptable_pivot</code> field. */
  public double getMinimumAcceptablePivot() {
    return minimumAcceptablePivot;
  }

  /** Sets the <code>minimum_acceptable_pivot</code> field. */
  public GlopParameters setMinimumAcceptablePivot(double value) {
    this.minimumAcceptablePivot = value;
    return this;
  }

  /** The <code>drop_tolerance</code> field. */
  public double getDropTolerance() {
    return dropTolerance;
  }

  /** Sets the <code>drop_tolerance</code> field. */
  public GlopParameters setDropTolerance(double value) {
    this.dropTolerance = value;
    return this;
  }

  /** The <code>use_scaling</code> field. */
  public boolean getUseScaling() {
    return useScaling;
  }

  /** Sets the <code>use_scaling</code> field. */
  public GlopParameters setUseScaling(boolean value) {
    this.useScaling = value;
    return this;
  }

  /** The <code>cost_scaling</code> field. */
  public CostScalingAlgorithm getCostScaling() {
    return costScaling;
  }

  /** Sets the <code>cost_scaling</code> field. */
  public GlopParameters setCostScaling(CostScalingAlgorithm value) {
    this.costScaling = value;
    return this;
  }

  /** The <code>initial_basis</code> field. */
  public InitialBasisHeuristic getInitialBasis() {
    return initialBasis;
  }

  /** Sets the <code>initial_basis</code> field. */
  public GlopParameters setInitialBasis(InitialBasisHeuristic value) {
    this.initialBasis = value;
    return this;
  }

  /** The <code>use_transposed_matrix</code> field. */
  public boolean getUseTransposedMatrix() {
    return useTransposedMatrix;
  }

  /** Sets the <code>use_transposed_matrix</code> field. */
  public GlopParameters setUseTransposedMatrix(boolean value) {
    this.useTransposedMatrix = value;
    return this;
  }

  /** The <code>basis_refactorization_period</code> field. */
  public int getBasisRefactorizationPeriod() {
    return basisRefactorizationPeriod;
  }

  /** Sets the <code>basis_refactorization_period</code> field. */
  public GlopParameters setBasisRefactorizationPeriod(int value) {
    this.basisRefactorizationPeriod = value;
    return this;
  }

  /** The <code>dynamically_adjust_refactorization_period</code> field. */
  public boolean getDynamicallyAdjustRefactorizationPeriod() {
    return dynamicallyAdjustRefactorizationPeriod;
  }

  /** Sets the <code>dynamically_adjust_refactorization_period</code> field. */
  public GlopParameters setDynamicallyAdjustRefactorizationPeriod(boolean value) {
    this.dynamicallyAdjustRefactorizationPeriod = value;
    return this;
  }

  /** The <code>solve_dual_problem</code> field. */
  public SolverBehavior getSolveDualProblem() {
    return solveDualProblem;
  }

  /** Sets the <code>solve_dual_problem</code> field. */
  public GlopParameters setSolveDualProblem(SolverBehavior value) {
    this.solveDualProblem = value;
    return this;
  }

  /** The <code>dualizer_threshold</code> field. */
  public double getDualizerThreshold() {
    return dualizerThreshold;
  }

  /** Sets the <code>dualizer_threshold</code> field. */
  public GlopParameters setDualizerThreshold(double value) {
    this.dualizerThreshold = value;
    return this;
  }

  /** The <code>solution_feasibility_tolerance</code> field. */
  public double getSolutionFeasibilityTolerance() {
    return solutionFeasibilityTolerance;
  }

  /** Sets the <code>solution_feasibility_tolerance</code> field. */
  public GlopParameters setSolutionFeasibilityTolerance(double value) {
    this.solutionFeasibilityTolerance = value;
    return this;
  }

  /** The <code>provide_strong_optimal_guarantee</code> field. */
  public boolean getProvideStrongOptimalGuarantee() {
    return provideStrongOptimalGuarantee;
  }

  /** Sets the <code>provide_strong_optimal_guarantee</code> field. */
  public GlopParameters setProvideStrongOptimalGuarantee(boolean value) {
    this.provideStrongOptimalGuarantee = value;
    return this;
  }

  /** The <code>change_status_to_imprecise</code> field. */
  public boolean getChangeStatusToImprecise() {
    return changeStatusToImprecise;
  }

  /** Sets the <code>change_status_to_imprecise</code> field. */
  public GlopParameters setChangeStatusToImprecise(boolean value) {
    this.changeStatusToImprecise = value;
    return this;
  }

  /** The <code>max_number_of_reoptimizations</code> field. */
  public double getMaxNumberOfReoptimizations() {
    return maxNumberOfReoptimizations;
  }

  /** Sets the <code>max_number_of_reoptimizations</code> field. */
  public GlopParameters setMaxNumberOfReoptimizations(double value) {
    this.maxNumberOfReoptimizations = value;
    return this;
  }

  /** The <code>lu_factorization_pivot_threshold</code> field. */
  public double getLuFactorizationPivotThreshold() {
    return luFactorizationPivotThreshold;
  }

  /** Sets the <code>lu_factorization_pivot_threshold</code> field. */
  public GlopParameters setLuFactorizationPivotThreshold(double value) {
    this.luFactorizationPivotThreshold = value;
    return this;
  }

  /** The <code>max_time_in_seconds</code> field. */
  public double getMaxTimeInSeconds() {
    return maxTimeInSeconds;
  }

  /** Sets the <code>max_time_in_seconds</code> field. */
  public GlopParameters setMaxTimeInSeconds(double value) {
    this.maxTimeInSeconds = value;
    return this;
  }

  /** The <code>max_deterministic_time</code> field. */
  public double getMaxDeterministicTime() {
    return maxDeterministicTime;
  }

  /** Sets the <code>max_deterministic_time</code> field. */
  public GlopParameters setMaxDeterministicTime(double value) {
    this.maxDeterministicTime = value;
    return this;
  }

  /** The <code>max_number_of_iterations</code> field. */
  public long getMaxNumberOfIterations() {
    return maxNumberOfIterations;
  }

  /** Sets the <code>max_number_of_iterations</code> field. */
  public GlopParameters setMaxNumberOfIterations(long value) {
    this.maxNumberOfIterations = value;
    return this;
  }

  /** The <code>markowitz_zlatev_parameter</code> field. */
  public int getMarkowitzZlatevParameter() {
    return markowitzZlatevParameter;
  }

  /** Sets the <code>markowitz_zlatev_parameter</code> field. */
  public GlopParameters setMarkowitzZlatevParameter(int value) {
    this.markowitzZlatevParameter = value;
    return this;
  }

  /** The <code>markowitz_singularity_threshold</code> field. */
  public double getMarkowitzSingularityThreshold() {
    return markowitzSingularityThreshold;
  }

  /** Sets the <code>markowitz_singularity_threshold</code> field. */
  public GlopParameters setMarkowitzSingularityThreshold(double value) {
    this.markowitzSingularityThreshold = value;
    return this;
  }

  /** The <code>use_dual_simplex</code> field. */
  public boolean getUseDualSimplex() {
    return useDualSimplex;
  }

  /** Sets the <code>use_dual_simplex</code> field. */
  public GlopParameters setUseDualSimplex(boolean value) {
    this.useDualSimplex = value;
    return this;
  }

  /** The <code>allow_simplex_algorithm_change</code> field. */
  public boolean getAllowSimplexAlgorithmChange() {
    return allowSimplexAlgorithmChange;
  }

  /** Sets the <code>allow_simplex_algorithm_change</code> field. */
  public GlopParameters setAllowSimplexAlgorithmChange(boolean value) {
    this.allowSimplexAlgorithmChange = value;
    return this;
  }

  /** The <code>devex_weights_reset_period</code> field. */
  public int getDevexWeightsResetPeriod() {
    return devexWeightsResetPeriod;
  }

  /** Sets the <code>devex_weights_reset_period</code> field. */
  public GlopParameters setDevexWeightsResetPeriod(int value) {
    this.devexWeightsResetPeriod = value;
    return this;
  }

  /** The <code>use_preprocessing</code> field. */
  public boolean getUsePreprocessing() {
    return usePreprocessing;
  }

  /** Sets the <code>use_preprocessing</code> field. */
  public GlopParameters setUsePreprocessing(boolean value) {
    this.usePreprocessing = value;
    return this;
  }

  /** The <code>use_middle_product_form_update</code> field. */
  public boolean getUseMiddleProductFormUpdate() {
    return useMiddleProductFormUpdate;
  }

  /** Sets the <code>use_middle_product_form_update</code> field. */
  public GlopParameters setUseMiddleProductFormUpdate(boolean value) {
    this.useMiddleProductFormUpdate = value;
    return this;
  }

  /** The <code>initialize_devex_with_column_norms</code> field. */
  public boolean getInitializeDevexWithColumnNorms() {
    return initializeDevexWithColumnNorms;
  }

  /** Sets the <code>initialize_devex_with_column_norms</code> field. */
  public GlopParameters setInitializeDevexWithColumnNorms(boolean value) {
    this.initializeDevexWithColumnNorms = value;
    return this;
  }

  /** The <code>exploit_singleton_column_in_initial_basis</code> field. */
  public boolean getExploitSingletonColumnInInitialBasis() {
    return exploitSingletonColumnInInitialBasis;
  }

  /** Sets the <code>exploit_singleton_column_in_initial_basis</code> field. */
  public GlopParameters setExploitSingletonColumnInInitialBasis(boolean value) {
    this.exploitSingletonColumnInInitialBasis = value;
    return this;
  }

  /** The <code>dual_small_pivot_threshold</code> field. */
  public double getDualSmallPivotThreshold() {
    return dualSmallPivotThreshold;
  }

  /** Sets the <code>dual_small_pivot_threshold</code> field. */
  public GlopParameters setDualSmallPivotThreshold(double value) {
    this.dualSmallPivotThreshold = value;
    return this;
  }

  /** The <code>preprocessor_zero_tolerance</code> field. */
  public double getPreprocessorZeroTolerance() {
    return preprocessorZeroTolerance;
  }

  /** Sets the <code>preprocessor_zero_tolerance</code> field. */
  public GlopParameters setPreprocessorZeroTolerance(double value) {
    this.preprocessorZeroTolerance = value;
    return this;
  }

  /** The <code>objective_lower_limit</code> field. */
  public double getObjectiveLowerLimit() {
    return objectiveLowerLimit;
  }

  /** Sets the <code>objective_lower_limit</code> field. */
  public GlopParameters setObjectiveLowerLimit(double value) {
    this.objectiveLowerLimit = value;
    return this;
  }

  /** The <code>objective_upper_limit</code> field. */
  public double getObjectiveUpperLimit() {
    return objectiveUpperLimit;
  }

  /** Sets the <code>objective_upper_limit</code> field. */
  public GlopParameters setObjectiveUpperLimit(double value) {
    this.objectiveUpperLimit = value;
    return this;
  }

  /** The <code>degenerate_ministep_factor</code> field. */
  public double getDegenerateMinistepFactor() {
    return degenerateMinistepFactor;
  }

  /** Sets the <code>degenerate_ministep_factor</code> field. */
  public GlopParameters setDegenerateMinistepFactor(double value) {
    this.degenerateMinistepFactor = value;
    return this;
  }

  /** The <code>random_seed</code> field. */
  public int getRandomSeed() {
    return randomSeed;
  }

  /** Sets the <code>random_seed</code> field. */
  public GlopParameters setRandomSeed(int value) {
    this.randomSeed = value;
    return this;
  }

  /** The <code>use_absl_random</code> field. */
  public boolean getUseAbslRandom() {
    return useAbslRandom;
  }

  /** Sets the <code>use_absl_random</code> field. */
  public GlopParameters setUseAbslRandom(boolean value) {
    this.useAbslRandom = value;
    return this;
  }

  /** The <code>num_omp_threads</code> field. */
  public int getNumOmpThreads() {
    return numOmpThreads;
  }

  /** Sets the <code>num_omp_threads</code> field. */
  public GlopParameters setNumOmpThreads(int value) {
    this.numOmpThreads = value;
    return this;
  }

  /** The <code>perturb_costs_in_dual_simplex</code> field. */
  public boolean getPerturbCostsInDualSimplex() {
    return perturbCostsInDualSimplex;
  }

  /** Sets the <code>perturb_costs_in_dual_simplex</code> field. */
  public GlopParameters setPerturbCostsInDualSimplex(boolean value) {
    this.perturbCostsInDualSimplex = value;
    return this;
  }

  /** The <code>use_dedicated_dual_feasibility_algorithm</code> field. */
  public boolean getUseDedicatedDualFeasibilityAlgorithm() {
    return useDedicatedDualFeasibilityAlgorithm;
  }

  /** Sets the <code>use_dedicated_dual_feasibility_algorithm</code> field. */
  public GlopParameters setUseDedicatedDualFeasibilityAlgorithm(boolean value) {
    this.useDedicatedDualFeasibilityAlgorithm = value;
    return this;
  }

  /** The <code>relative_cost_perturbation</code> field. */
  public double getRelativeCostPerturbation() {
    return relativeCostPerturbation;
  }

  /** Sets the <code>relative_cost_perturbation</code> field. */
  public GlopParameters setRelativeCostPerturbation(double value) {
    this.relativeCostPerturbation = value;
    return this;
  }

  /** The <code>relative_max_cost_perturbation</code> field. */
  public double getRelativeMaxCostPerturbation() {
    return relativeMaxCostPerturbation;
  }

  /** Sets the <code>relative_max_cost_perturbation</code> field. */
  public GlopParameters setRelativeMaxCostPerturbation(double value) {
    this.relativeMaxCostPerturbation = value;
    return this;
  }

  /** The <code>initial_condition_number_threshold</code> field. */
  public double getInitialConditionNumberThreshold() {
    return initialConditionNumberThreshold;
  }

  /** Sets the <code>initial_condition_number_threshold</code> field. */
  public GlopParameters setInitialConditionNumberThreshold(double value) {
    this.initialConditionNumberThreshold = value;
    return this;
  }

  /** The <code>log_search_progress</code> field. */
  public boolean getLogSearchProgress() {
    return logSearchProgress;
  }

  /** Sets the <code>log_search_progress</code> field. */
  public GlopParameters setLogSearchProgress(boolean value) {
    this.logSearchProgress = value;
    return this;
  }

  /** The <code>log_to_stdout</code> field. */
  public boolean getLogToStdout() {
    return logToStdout;
  }

  /** Sets the <code>log_to_stdout</code> field. */
  public GlopParameters setLogToStdout(boolean value) {
    this.logToStdout = value;
    return this;
  }

  /** The <code>crossover_bound_snapping_distance</code> field. */
  public double getCrossoverBoundSnappingDistance() {
    return crossoverBoundSnappingDistance;
  }

  /** Sets the <code>crossover_bound_snapping_distance</code> field. */
  public GlopParameters setCrossoverBoundSnappingDistance(double value) {
    this.crossoverBoundSnappingDistance = value;
    return this;
  }

  /** The <code>push_to_vertex</code> field. */
  public boolean getPushToVertex() {
    return pushToVertex;
  }

  /** Sets the <code>push_to_vertex</code> field. */
  public GlopParameters setPushToVertex(boolean value) {
    this.pushToVertex = value;
    return this;
  }

  /** The <code>use_implied_free_preprocessor</code> field. */
  public boolean getUseImpliedFreePreprocessor() {
    return useImpliedFreePreprocessor;
  }

  /** Sets the <code>use_implied_free_preprocessor</code> field. */
  public GlopParameters setUseImpliedFreePreprocessor(boolean value) {
    this.useImpliedFreePreprocessor = value;
    return this;
  }

  /** The <code>max_valid_magnitude</code> field. */
  public double getMaxValidMagnitude() {
    return maxValidMagnitude;
  }

  /** Sets the <code>max_valid_magnitude</code> field. */
  public GlopParameters setMaxValidMagnitude(double value) {
    this.maxValidMagnitude = value;
    return this;
  }

  /** The <code>drop_magnitude</code> field. */
  public double getDropMagnitude() {
    return dropMagnitude;
  }

  /** Sets the <code>drop_magnitude</code> field. */
  public GlopParameters setDropMagnitude(double value) {
    this.dropMagnitude = value;
    return this;
  }

  /** The <code>dual_price_prioritize_norm</code> field. */
  public boolean getDualPricePrioritizeNorm() {
    return dualPricePrioritizeNorm;
  }

  /** Sets the <code>dual_price_prioritize_norm</code> field. */
  public GlopParameters setDualPricePrioritizeNorm(boolean value) {
    this.dualPricePrioritizeNorm = value;
    return this;
  }

  /**
   * The <code>primal_polish</code> field. Experimental (MIP context, needs integrality scales): after OPTIMAL try more pivots to make the solution more integral. Accepted but not used, the port has no polishing.
   */
  public boolean getPrimalPolish() {
    return primalPolish;
  }

  /** Sets the <code>primal_polish</code> field. */
  public GlopParameters setPrimalPolish(boolean value) {
    this.primalPolish = value;
    return this;
  }

  /**
   * The <code>dual_polish</code> field. Experimental dual counterpart of <code>primal_polish</code>. Accepted but not used, the port has no polishing.
   */
  public boolean getDualPolish() {
    return dualPolish;
  }

  /** Sets the <code>dual_polish</code> field. */
  public GlopParameters setDualPolish(boolean value) {
    this.dualPolish = value;
    return this;
  }

  /**
   * The <code>use_shift_variable_bounds_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseShiftVariableBoundsPreprocessor() {
    return useShiftVariableBoundsPreprocessor;
  }

  /** Sets the <code>use_shift_variable_bounds_preprocessor</code> field. */
  public GlopParameters setUseShiftVariableBoundsPreprocessor(boolean value) {
    this.useShiftVariableBoundsPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_fixed_variable_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseFixedVariablePreprocessor() {
    return useFixedVariablePreprocessor;
  }

  /** Sets the <code>use_fixed_variable_preprocessor</code> field. */
  public GlopParameters setUseFixedVariablePreprocessor(boolean value) {
    this.useFixedVariablePreprocessor = value;
    return this;
  }

  /**
   * The <code>use_singleton_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseSingletonPreprocessor() {
    return useSingletonPreprocessor;
  }

  /** Sets the <code>use_singleton_preprocessor</code> field. */
  public GlopParameters setUseSingletonPreprocessor(boolean value) {
    this.useSingletonPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_forcing_and_implied_free_constraint_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseForcingAndImpliedFreeConstraintPreprocessor() {
    return useForcingAndImpliedFreeConstraintPreprocessor;
  }

  /** Sets the <code>use_forcing_and_implied_free_constraint_preprocessor</code> field. */
  public GlopParameters setUseForcingAndImpliedFreeConstraintPreprocessor(boolean value) {
    this.useForcingAndImpliedFreeConstraintPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_free_constraint_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseFreeConstraintPreprocessor() {
    return useFreeConstraintPreprocessor;
  }

  /** Sets the <code>use_free_constraint_preprocessor</code> field. */
  public GlopParameters setUseFreeConstraintPreprocessor(boolean value) {
    this.useFreeConstraintPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_unconstrained_variable_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseUnconstrainedVariablePreprocessor() {
    return useUnconstrainedVariablePreprocessor;
  }

  /** Sets the <code>use_unconstrained_variable_preprocessor</code> field. */
  public GlopParameters setUseUnconstrainedVariablePreprocessor(boolean value) {
    this.useUnconstrainedVariablePreprocessor = value;
    return this;
  }

  /**
   * The <code>use_doubleton_free_column_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseDoubletonFreeColumnPreprocessor() {
    return useDoubletonFreeColumnPreprocessor;
  }

  /** Sets the <code>use_doubleton_free_column_preprocessor</code> field. */
  public GlopParameters setUseDoubletonFreeColumnPreprocessor(boolean value) {
    this.useDoubletonFreeColumnPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_doubleton_equality_row_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseDoubletonEqualityRowPreprocessor() {
    return useDoubletonEqualityRowPreprocessor;
  }

  /** Sets the <code>use_doubleton_equality_row_preprocessor</code> field. */
  public GlopParameters setUseDoubletonEqualityRowPreprocessor(boolean value) {
    this.useDoubletonEqualityRowPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_empty_column_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseEmptyColumnPreprocessor() {
    return useEmptyColumnPreprocessor;
  }

  /** Sets the <code>use_empty_column_preprocessor</code> field. */
  public GlopParameters setUseEmptyColumnPreprocessor(boolean value) {
    this.useEmptyColumnPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_empty_constraint_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseEmptyConstraintPreprocessor() {
    return useEmptyConstraintPreprocessor;
  }

  /** Sets the <code>use_empty_constraint_preprocessor</code> field. */
  public GlopParameters setUseEmptyConstraintPreprocessor(boolean value) {
    this.useEmptyConstraintPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_proportional_column_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseProportionalColumnPreprocessor() {
    return useProportionalColumnPreprocessor;
  }

  /** Sets the <code>use_proportional_column_preprocessor</code> field. */
  public GlopParameters setUseProportionalColumnPreprocessor(boolean value) {
    this.useProportionalColumnPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_proportional_row_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseProportionalRowPreprocessor() {
    return useProportionalRowPreprocessor;
  }

  /** Sets the <code>use_proportional_row_preprocessor</code> field. */
  public GlopParameters setUseProportionalRowPreprocessor(boolean value) {
    this.useProportionalRowPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_singleton_column_sign_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing).
   */
  public boolean getUseSingletonColumnSignPreprocessor() {
    return useSingletonColumnSignPreprocessor;
  }

  /** Sets the <code>use_singleton_column_sign_preprocessor</code> field. */
  public GlopParameters setUseSingletonColumnSignPreprocessor(boolean value) {
    this.useSingletonColumnSignPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_dualizer_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseDualizerPreprocessor() {
    return useDualizerPreprocessor;
  }

  /** Sets the <code>use_dualizer_preprocessor</code> field. */
  public GlopParameters setUseDualizerPreprocessor(boolean value) {
    this.useDualizerPreprocessor = value;
    return this;
  }

  /**
   * The <code>use_bound_propagation_preprocessor</code> field. If the presolve is enabled, run the corresponding preprocessor (default true; intended for testing). Accepted but not used, the port has no such preprocessor.
   */
  public boolean getUseBoundPropagationPreprocessor() {
    return useBoundPropagationPreprocessor;
  }

  /** Sets the <code>use_bound_propagation_preprocessor</code> field. */
  public GlopParameters setUseBoundPropagationPreprocessor(boolean value) {
    this.useBoundPropagationPreprocessor = value;
    return this;
  }

  /**
   * Sets one field from its proto name and text value. Returns false if the name is unknown or the
   * value cannot be parsed.
   */
  public boolean setField(String name, String value) {
    try {
      switch (name) {
        case "scaling_method":
          scalingMethod = ScalingAlgorithm.valueOf(value);
          return true;
        case "feasibility_rule":
          feasibilityRule = PricingRule.valueOf(value);
          return true;
        case "optimization_rule":
          optimizationRule = PricingRule.valueOf(value);
          return true;
        case "refactorization_threshold":
          refactorizationThreshold = parseDouble(value);
          return true;
        case "recompute_reduced_costs_threshold":
          recomputeReducedCostsThreshold = parseDouble(value);
          return true;
        case "recompute_edges_norm_threshold":
          recomputeEdgesNormThreshold = parseDouble(value);
          return true;
        case "primal_feasibility_tolerance":
          primalFeasibilityTolerance = parseDouble(value);
          return true;
        case "dual_feasibility_tolerance":
          dualFeasibilityTolerance = parseDouble(value);
          return true;
        case "ratio_test_zero_threshold":
          ratioTestZeroThreshold = parseDouble(value);
          return true;
        case "harris_tolerance_ratio":
          harrisToleranceRatio = parseDouble(value);
          return true;
        case "small_pivot_threshold":
          smallPivotThreshold = parseDouble(value);
          return true;
        case "minimum_acceptable_pivot":
          minimumAcceptablePivot = parseDouble(value);
          return true;
        case "drop_tolerance":
          dropTolerance = parseDouble(value);
          return true;
        case "use_scaling":
          useScaling = parseBoolean(value);
          return true;
        case "cost_scaling":
          costScaling = CostScalingAlgorithm.valueOf(value);
          return true;
        case "initial_basis":
          initialBasis = InitialBasisHeuristic.valueOf(value);
          return true;
        case "use_transposed_matrix":
          useTransposedMatrix = parseBoolean(value);
          return true;
        case "basis_refactorization_period":
          basisRefactorizationPeriod = Integer.parseInt(value);
          return true;
        case "dynamically_adjust_refactorization_period":
          dynamicallyAdjustRefactorizationPeriod = parseBoolean(value);
          return true;
        case "solve_dual_problem":
          solveDualProblem = SolverBehavior.valueOf(value);
          return true;
        case "dualizer_threshold":
          dualizerThreshold = parseDouble(value);
          return true;
        case "solution_feasibility_tolerance":
          solutionFeasibilityTolerance = parseDouble(value);
          return true;
        case "provide_strong_optimal_guarantee":
          provideStrongOptimalGuarantee = parseBoolean(value);
          return true;
        case "change_status_to_imprecise":
          changeStatusToImprecise = parseBoolean(value);
          return true;
        case "max_number_of_reoptimizations":
          maxNumberOfReoptimizations = parseDouble(value);
          return true;
        case "lu_factorization_pivot_threshold":
          luFactorizationPivotThreshold = parseDouble(value);
          return true;
        case "max_time_in_seconds":
          maxTimeInSeconds = parseDouble(value);
          return true;
        case "max_deterministic_time":
          maxDeterministicTime = parseDouble(value);
          return true;
        case "max_number_of_iterations":
          maxNumberOfIterations = Long.parseLong(value);
          return true;
        case "markowitz_zlatev_parameter":
          markowitzZlatevParameter = Integer.parseInt(value);
          return true;
        case "markowitz_singularity_threshold":
          markowitzSingularityThreshold = parseDouble(value);
          return true;
        case "use_dual_simplex":
          useDualSimplex = parseBoolean(value);
          return true;
        case "allow_simplex_algorithm_change":
          allowSimplexAlgorithmChange = parseBoolean(value);
          return true;
        case "devex_weights_reset_period":
          devexWeightsResetPeriod = Integer.parseInt(value);
          return true;
        case "use_preprocessing":
          usePreprocessing = parseBoolean(value);
          return true;
        case "use_middle_product_form_update":
          useMiddleProductFormUpdate = parseBoolean(value);
          return true;
        case "initialize_devex_with_column_norms":
          initializeDevexWithColumnNorms = parseBoolean(value);
          return true;
        case "exploit_singleton_column_in_initial_basis":
          exploitSingletonColumnInInitialBasis = parseBoolean(value);
          return true;
        case "dual_small_pivot_threshold":
          dualSmallPivotThreshold = parseDouble(value);
          return true;
        case "preprocessor_zero_tolerance":
          preprocessorZeroTolerance = parseDouble(value);
          return true;
        case "objective_lower_limit":
          objectiveLowerLimit = parseDouble(value);
          return true;
        case "objective_upper_limit":
          objectiveUpperLimit = parseDouble(value);
          return true;
        case "degenerate_ministep_factor":
          degenerateMinistepFactor = parseDouble(value);
          return true;
        case "random_seed":
          randomSeed = Integer.parseInt(value);
          return true;
        case "use_absl_random":
          useAbslRandom = parseBoolean(value);
          return true;
        case "num_omp_threads":
          numOmpThreads = Integer.parseInt(value);
          return true;
        case "perturb_costs_in_dual_simplex":
          perturbCostsInDualSimplex = parseBoolean(value);
          return true;
        case "use_dedicated_dual_feasibility_algorithm":
          useDedicatedDualFeasibilityAlgorithm = parseBoolean(value);
          return true;
        case "relative_cost_perturbation":
          relativeCostPerturbation = parseDouble(value);
          return true;
        case "relative_max_cost_perturbation":
          relativeMaxCostPerturbation = parseDouble(value);
          return true;
        case "initial_condition_number_threshold":
          initialConditionNumberThreshold = parseDouble(value);
          return true;
        case "log_search_progress":
          logSearchProgress = parseBoolean(value);
          return true;
        case "log_to_stdout":
          logToStdout = parseBoolean(value);
          return true;
        case "crossover_bound_snapping_distance":
          crossoverBoundSnappingDistance = parseDouble(value);
          return true;
        case "push_to_vertex":
          pushToVertex = parseBoolean(value);
          return true;
        case "use_implied_free_preprocessor":
          useImpliedFreePreprocessor = parseBoolean(value);
          return true;
        case "max_valid_magnitude":
          maxValidMagnitude = parseDouble(value);
          return true;
        case "drop_magnitude":
          dropMagnitude = parseDouble(value);
          return true;
        case "dual_price_prioritize_norm":
          dualPricePrioritizeNorm = parseBoolean(value);
          return true;
        case "primal_polish":
          primalPolish = parseBoolean(value);
          return true;
        case "dual_polish":
          dualPolish = parseBoolean(value);
          return true;
        case "use_shift_variable_bounds_preprocessor":
          useShiftVariableBoundsPreprocessor = parseBoolean(value);
          return true;
        case "use_fixed_variable_preprocessor":
          useFixedVariablePreprocessor = parseBoolean(value);
          return true;
        case "use_singleton_preprocessor":
          useSingletonPreprocessor = parseBoolean(value);
          return true;
        case "use_forcing_and_implied_free_constraint_preprocessor":
          useForcingAndImpliedFreeConstraintPreprocessor = parseBoolean(value);
          return true;
        case "use_free_constraint_preprocessor":
          useFreeConstraintPreprocessor = parseBoolean(value);
          return true;
        case "use_unconstrained_variable_preprocessor":
          useUnconstrainedVariablePreprocessor = parseBoolean(value);
          return true;
        case "use_doubleton_free_column_preprocessor":
          useDoubletonFreeColumnPreprocessor = parseBoolean(value);
          return true;
        case "use_doubleton_equality_row_preprocessor":
          useDoubletonEqualityRowPreprocessor = parseBoolean(value);
          return true;
        case "use_empty_column_preprocessor":
          useEmptyColumnPreprocessor = parseBoolean(value);
          return true;
        case "use_empty_constraint_preprocessor":
          useEmptyConstraintPreprocessor = parseBoolean(value);
          return true;
        case "use_proportional_column_preprocessor":
          useProportionalColumnPreprocessor = parseBoolean(value);
          return true;
        case "use_proportional_row_preprocessor":
          useProportionalRowPreprocessor = parseBoolean(value);
          return true;
        case "use_singleton_column_sign_preprocessor":
          useSingletonColumnSignPreprocessor = parseBoolean(value);
          return true;
        case "use_dualizer_preprocessor":
          useDualizerPreprocessor = parseBoolean(value);
          return true;
        case "use_bound_propagation_preprocessor":
          useBoundPropagationPreprocessor = parseBoolean(value);
          return true;
        default:
          return false;
      }
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static double parseDouble(String value) {
    final String v = value.toLowerCase(Locale.ROOT);
    if (v.equals("inf") || v.equals("infinity")) {
      return Double.POSITIVE_INFINITY;
    }
    if (v.equals("-inf") || v.equals("-infinity")) {
      return Double.NEGATIVE_INFINITY;
    }
    return Double.parseDouble(value);
  }

  private static boolean parseBoolean(String value) {
    if (value.equals("true") || value.equals("1")) {
      return true;
    }
    if (value.equals("false") || value.equals("0")) {
      return false;
    }
    throw new IllegalArgumentException(value);
  }

  /**
   * Merges fields given in protocol buffer text format (<code>name: value</code> pairs separated by
   * white space, commas or semicolons). Returns false, leaving this object unchanged, on a syntax
   * error or an unknown field.
   */
  public boolean mergeFromString(String text) {
    GlopParameters tmp = copy();
    final String[] tokens = text.trim().isEmpty() ? new String[0]
        : text.trim().replace(":", " : ").split("[\\s,;]+");
    int i = 0;
    while (i < tokens.length) {
      if (tokens[i].isEmpty()) {
        ++i;
        continue;
      }
      if (i + 2 >= tokens.length) {
        return false;
      }
      final String name = tokens[i];
      if (!tokens[i + 1].equals(":")) {
        return false;
      }
      String value = tokens[i + 2];
      if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
        value = value.substring(1, value.length() - 1);
      }
      if (!tmp.setField(name, value)) {
        return false;
      }
      i += 3;
    }
    mergeFrom(tmp);
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("scaling_method: ").append(scalingMethod).append('\n');
    sb.append("feasibility_rule: ").append(feasibilityRule).append('\n');
    sb.append("optimization_rule: ").append(optimizationRule).append('\n');
    sb.append("refactorization_threshold: ").append(refactorizationThreshold).append('\n');
    sb.append("recompute_reduced_costs_threshold: ").append(recomputeReducedCostsThreshold).append('\n');
    sb.append("recompute_edges_norm_threshold: ").append(recomputeEdgesNormThreshold).append('\n');
    sb.append("primal_feasibility_tolerance: ").append(primalFeasibilityTolerance).append('\n');
    sb.append("dual_feasibility_tolerance: ").append(dualFeasibilityTolerance).append('\n');
    sb.append("ratio_test_zero_threshold: ").append(ratioTestZeroThreshold).append('\n');
    sb.append("harris_tolerance_ratio: ").append(harrisToleranceRatio).append('\n');
    sb.append("small_pivot_threshold: ").append(smallPivotThreshold).append('\n');
    sb.append("minimum_acceptable_pivot: ").append(minimumAcceptablePivot).append('\n');
    sb.append("drop_tolerance: ").append(dropTolerance).append('\n');
    sb.append("use_scaling: ").append(useScaling).append('\n');
    sb.append("cost_scaling: ").append(costScaling).append('\n');
    sb.append("initial_basis: ").append(initialBasis).append('\n');
    sb.append("use_transposed_matrix: ").append(useTransposedMatrix).append('\n');
    sb.append("basis_refactorization_period: ").append(basisRefactorizationPeriod).append('\n');
    sb.append("dynamically_adjust_refactorization_period: ").append(dynamicallyAdjustRefactorizationPeriod).append('\n');
    sb.append("solve_dual_problem: ").append(solveDualProblem).append('\n');
    sb.append("dualizer_threshold: ").append(dualizerThreshold).append('\n');
    sb.append("solution_feasibility_tolerance: ").append(solutionFeasibilityTolerance).append('\n');
    sb.append("provide_strong_optimal_guarantee: ").append(provideStrongOptimalGuarantee).append('\n');
    sb.append("change_status_to_imprecise: ").append(changeStatusToImprecise).append('\n');
    sb.append("max_number_of_reoptimizations: ").append(maxNumberOfReoptimizations).append('\n');
    sb.append("lu_factorization_pivot_threshold: ").append(luFactorizationPivotThreshold).append('\n');
    sb.append("max_time_in_seconds: ").append(maxTimeInSeconds).append('\n');
    sb.append("max_deterministic_time: ").append(maxDeterministicTime).append('\n');
    sb.append("max_number_of_iterations: ").append(maxNumberOfIterations).append('\n');
    sb.append("markowitz_zlatev_parameter: ").append(markowitzZlatevParameter).append('\n');
    sb.append("markowitz_singularity_threshold: ").append(markowitzSingularityThreshold).append('\n');
    sb.append("use_dual_simplex: ").append(useDualSimplex).append('\n');
    sb.append("allow_simplex_algorithm_change: ").append(allowSimplexAlgorithmChange).append('\n');
    sb.append("devex_weights_reset_period: ").append(devexWeightsResetPeriod).append('\n');
    sb.append("use_preprocessing: ").append(usePreprocessing).append('\n');
    sb.append("use_middle_product_form_update: ").append(useMiddleProductFormUpdate).append('\n');
    sb.append("initialize_devex_with_column_norms: ").append(initializeDevexWithColumnNorms).append('\n');
    sb.append("exploit_singleton_column_in_initial_basis: ").append(exploitSingletonColumnInInitialBasis).append('\n');
    sb.append("dual_small_pivot_threshold: ").append(dualSmallPivotThreshold).append('\n');
    sb.append("preprocessor_zero_tolerance: ").append(preprocessorZeroTolerance).append('\n');
    sb.append("objective_lower_limit: ").append(objectiveLowerLimit).append('\n');
    sb.append("objective_upper_limit: ").append(objectiveUpperLimit).append('\n');
    sb.append("degenerate_ministep_factor: ").append(degenerateMinistepFactor).append('\n');
    sb.append("random_seed: ").append(randomSeed).append('\n');
    sb.append("use_absl_random: ").append(useAbslRandom).append('\n');
    sb.append("num_omp_threads: ").append(numOmpThreads).append('\n');
    sb.append("perturb_costs_in_dual_simplex: ").append(perturbCostsInDualSimplex).append('\n');
    sb.append("use_dedicated_dual_feasibility_algorithm: ").append(useDedicatedDualFeasibilityAlgorithm).append('\n');
    sb.append("relative_cost_perturbation: ").append(relativeCostPerturbation).append('\n');
    sb.append("relative_max_cost_perturbation: ").append(relativeMaxCostPerturbation).append('\n');
    sb.append("initial_condition_number_threshold: ").append(initialConditionNumberThreshold).append('\n');
    sb.append("log_search_progress: ").append(logSearchProgress).append('\n');
    sb.append("log_to_stdout: ").append(logToStdout).append('\n');
    sb.append("crossover_bound_snapping_distance: ").append(crossoverBoundSnappingDistance).append('\n');
    sb.append("push_to_vertex: ").append(pushToVertex).append('\n');
    sb.append("use_implied_free_preprocessor: ").append(useImpliedFreePreprocessor).append('\n');
    sb.append("max_valid_magnitude: ").append(maxValidMagnitude).append('\n');
    sb.append("drop_magnitude: ").append(dropMagnitude).append('\n');
    sb.append("dual_price_prioritize_norm: ").append(dualPricePrioritizeNorm).append('\n');
    sb.append("primal_polish: ").append(primalPolish).append('\n');
    sb.append("dual_polish: ").append(dualPolish).append('\n');
    sb.append("use_shift_variable_bounds_preprocessor: ").append(useShiftVariableBoundsPreprocessor).append('\n');
    sb.append("use_fixed_variable_preprocessor: ").append(useFixedVariablePreprocessor).append('\n');
    sb.append("use_singleton_preprocessor: ").append(useSingletonPreprocessor).append('\n');
    sb.append("use_forcing_and_implied_free_constraint_preprocessor: ").append(useForcingAndImpliedFreeConstraintPreprocessor).append('\n');
    sb.append("use_free_constraint_preprocessor: ").append(useFreeConstraintPreprocessor).append('\n');
    sb.append("use_unconstrained_variable_preprocessor: ").append(useUnconstrainedVariablePreprocessor).append('\n');
    sb.append("use_doubleton_free_column_preprocessor: ").append(useDoubletonFreeColumnPreprocessor).append('\n');
    sb.append("use_doubleton_equality_row_preprocessor: ").append(useDoubletonEqualityRowPreprocessor).append('\n');
    sb.append("use_empty_column_preprocessor: ").append(useEmptyColumnPreprocessor).append('\n');
    sb.append("use_empty_constraint_preprocessor: ").append(useEmptyConstraintPreprocessor).append('\n');
    sb.append("use_proportional_column_preprocessor: ").append(useProportionalColumnPreprocessor).append('\n');
    sb.append("use_proportional_row_preprocessor: ").append(useProportionalRowPreprocessor).append('\n');
    sb.append("use_singleton_column_sign_preprocessor: ").append(useSingletonColumnSignPreprocessor).append('\n');
    sb.append("use_dualizer_preprocessor: ").append(useDualizerPreprocessor).append('\n');
    sb.append("use_bound_propagation_preprocessor: ").append(useBoundPropagationPreprocessor).append('\n');
    return sb.toString();
  }
}
