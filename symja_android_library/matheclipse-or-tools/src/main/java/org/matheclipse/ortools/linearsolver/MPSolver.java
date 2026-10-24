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
// Modified for the Symja matheclipse-or-tools module: adapted from the OR-Tools Java wrapper
// (ortools/linear_solver/linear_solver.h, linear_solver.cc and java/linear_solver.swig ported to
// Java), see NOTICE in the module root.

package org.matheclipse.ortools.linearsolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.matheclipse.ortools.linearsolver.internal.BranchAndBoundInterface;
import org.matheclipse.ortools.linearsolver.internal.GlopInterface;
import org.matheclipse.ortools.linearsolver.internal.MPModel;
import org.matheclipse.ortools.linearsolver.internal.MPSolverInterface;
import org.matheclipse.ortools.linearsolver.internal.ModelExporter;
import org.matheclipse.ortools.lpdata.LpUtils;

/**
 * This mathematical programming (MP) solver class is the main class though which users build and
 * solve problems.
 *
 * <p>
 * In this pure Java port, all the LP problem types (GLOP, CLP, PDLP, GLPK_LP, GUROBI_LP,
 * CPLEX_LP, XPRESS_LP, HIGHS_LP) are solved by the Glop revised simplex, and all the MIP problem
 * types (SCIP, CBC, GLPK, SAT, BOP, GUROBI, CPLEX, XPRESS, HIGHS) by a Java
 * branch-and-bound over Glop. The methods based on protocol buffers (exportModelToProto,
 * loadModelFromProto, solveWithProto, createSolutionResponseProto, ...) are not available.
 */
public class MPSolver {

  private static final Logger LOGGER = Logger.getLogger(MPSolver.class.getName());

  private final String name;
  private final OptimizationProblemType problemType;
  private final MPSolverInterface solverInterface;
  private final List<MPVariable> variables = new ArrayList<>();
  private final List<MPConstraint> constraints = new ArrayList<>();
  private final MPObjective objective;
  private Map<String, Integer> variableNameToIndex;
  private Map<String, Integer> constraintNameToIndex;
  private final long constructionTimeMillis = System.currentTimeMillis();
  private long timeLimitMillis;
  private String solverSpecificParameterString = "";
  private boolean quiet = true;
  private int numThreads = 1;
  private final List<MPVariable> hintVariables = new ArrayList<>();
  private final List<Double> hintValues = new ArrayList<>();

  private ResultStatus resultStatus = ResultStatus.NOT_SOLVED;
  private boolean solutionSynchronized;
  private double objectiveValue;
  private double bestObjectiveBound;
  private long iterations;
  private long nodes = -1;
  private BasisStatus[] columnStatus = new BasisStatus[0];
  private BasisStatus[] rowStatus = new BasisStatus[0];

  /** Create a solver with the given name and underlying solver backend. */
  public MPSolver(String name, MPSolver.OptimizationProblemType problemType) {
    this.name = name == null ? "" : name;
    this.problemType = problemType;
    this.solverInterface = problemType.isMip() ? new BranchAndBoundInterface(problemType)
        : new GlopInterface();
    this.objective = new MPObjective(this);
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Creates and returns an array of variables. */
  public MPVariable[] makeVarArray(int count, double lb, double ub, boolean integer) {
    MPVariable[] array = new MPVariable[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeVar(lb, ub, integer, "");
    }
    return array;
  }

  /** Creates and returns an array of named variables. */
  public MPVariable[] makeVarArray(int count, double lb, double ub, boolean integer,
      String var_name) {
    MPVariable[] array = new MPVariable[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeVar(lb, ub, integer, var_name + i);
    }
    return array;
  }

  /** Creates and returns an array of continuous variables. */
  public MPVariable[] makeNumVarArray(int count, double lb, double ub) {
    return makeVarArray(count, lb, ub, false);
  }

  /** Creates and returns an array of named continuous variables. */
  public MPVariable[] makeNumVarArray(int count, double lb, double ub, String var_name) {
    return makeVarArray(count, lb, ub, false, var_name);
  }

  /** Creates and returns an array of integer variables. */
  public MPVariable[] makeIntVarArray(int count, double lb, double ub) {
    return makeVarArray(count, lb, ub, true);
  }

  /** Creates and returns an array of named integer variables. */
  public MPVariable[] makeIntVarArray(int count, double lb, double ub, String var_name) {
    return makeVarArray(count, lb, ub, true, var_name);
  }

  /** Creates and returns an array of Boolean variables. */
  public MPVariable[] makeBoolVarArray(int count) {
    return makeVarArray(count, 0.0, 1.0, true);
  }

  /** Creates and returns an array of named Boolean variables. */
  public MPVariable[] makeBoolVarArray(int count, String var_name) {
    return makeVarArray(count, 0.0, 1.0, true, var_name);
  }

  // The (lower case) names of the problem types, as in linear_solver.cc.
  private static final Object[][] OPTIMIZATION_PROBLEM_TYPE_NAMES = {
      {OptimizationProblemType.GLOP_LINEAR_PROGRAMMING, "glop"},
      {OptimizationProblemType.CLP_LINEAR_PROGRAMMING, "clp"},
      {OptimizationProblemType.GUROBI_LINEAR_PROGRAMMING, "gurobi_lp"},
      {OptimizationProblemType.GLPK_LINEAR_PROGRAMMING, "glpk_lp"},
      {OptimizationProblemType.HIGHS_LINEAR_PROGRAMMING, "highs_lp"},
      {OptimizationProblemType.CPLEX_LINEAR_PROGRAMMING, "cplex_lp"},
      {OptimizationProblemType.XPRESS_LINEAR_PROGRAMMING, "xpress_lp"},
      {OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING, "scip"},
      {OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING, "cbc"},
      {OptimizationProblemType.SAT_INTEGER_PROGRAMMING, "sat"},
      {OptimizationProblemType.BOP_INTEGER_PROGRAMMING, "bop"},
      {OptimizationProblemType.GUROBI_MIXED_INTEGER_PROGRAMMING, "gurobi"},
      {OptimizationProblemType.GLPK_MIXED_INTEGER_PROGRAMMING, "glpk"},
      {OptimizationProblemType.HIGHS_MIXED_INTEGER_PROGRAMMING, "highs"},
      {OptimizationProblemType.PDLP_LINEAR_PROGRAMMING, "pdlp"},
      {OptimizationProblemType.CPLEX_MIXED_INTEGER_PROGRAMMING, "cplex"},
      {OptimizationProblemType.XPRESS_MIXED_INTEGER_PROGRAMMING, "xpress"}};

  /**
   * Parses a solver name: the full enum name (for example
   * <code>"SCIP_MIXED_INTEGER_PROGRAMMING"</code>) or a short name (<code>"scip"</code>), case
   * insensitive, '-' read as '_', with an optional <code>"_mip"</code> suffix; "cp_sat" means
   * "sat". Returns <code>null</code> for an unknown name.
   */
  public static OptimizationProblemType parseSolverType(String solverId) {
    if (solverId == null) {
      return null;
    }
    final String id = solverId.toUpperCase(Locale.ROOT).replace('-', '_');
    for (OptimizationProblemType t : OptimizationProblemType.values()) {
      if (t.name().equals(id)) {
        return t;
      }
    }
    String lowerId = id.toLowerCase(Locale.ROOT);
    if (lowerId.endsWith("_mip")) {
      lowerId = lowerId.substring(0, lowerId.length() - 4);
    }
    if (lowerId.equals("cp_sat")) {
      lowerId = "sat";
    }
    for (Object[] entry : OPTIMIZATION_PROBLEM_TYPE_NAMES) {
      if (entry[1].equals(lowerId)) {
        return (OptimizationProblemType) entry[0];
      }
    }
    return null;
  }

  /**
   * Recommended factory method to create a MPSolver instance, especially in non C++ languages.
   *
   * <p>
   * It returns a newly created solver instance if successful, or a nullptr otherwise.
   *
   * <p>
   * It will accept both string names of the OptimizationProblemType enum, as well as a short
   * version (i.e. "SCIP_MIXED_INTEGER_PROGRAMMING" or "SCIP"). solver_id is case insensitive, and
   * the following names are supported:
   * <ul>
   * <li>CLP_LINEAR_PROGRAMMING or CLP
   * <li>CBC_MIXED_INTEGER_PROGRAMMING or CBC
   * <li>GLOP_LINEAR_PROGRAMMING or GLOP
   * <li>BOP_INTEGER_PROGRAMMING or BOP
   * <li>SAT_INTEGER_PROGRAMMING or SAT or CP_SAT
   * <li>SCIP_MIXED_INTEGER_PROGRAMMING or SCIP
   * <li>GUROBI_LINEAR_PROGRAMMING or GUROBI_LP
   * <li>GUROBI_MIXED_INTEGER_PROGRAMMING or GUROBI or GUROBI_MIP
   * <li>CPLEX_LINEAR_PROGRAMMING or CPLEX_LP
   * <li>CPLEX_MIXED_INTEGER_PROGRAMMING or CPLEX or CPLEX_MIP
   * <li>XPRESS_LINEAR_PROGRAMMING or XPRESS_LP
   * <li>XPRESS_MIXED_INTEGER_PROGRAMMING or XPRESS or XPRESS_MIP
   * <li>GLPK_LINEAR_PROGRAMMING or GLPK_LP
   * <li>GLPK_MIXED_INTEGER_PROGRAMMING or GLPK or GLPK_MIP
   * <li>HIGHS_LINEAR_PROGRAMMING or HIGHS_LP, HIGHS_MIXED_INTEGER_PROGRAMMING or HIGHS
   * <li>PDLP_LINEAR_PROGRAMMING or PDLP
   * </ul>
   */
  public static MPSolver createSolver(String solver_id) {
    final OptimizationProblemType type = parseSolverType(solver_id);
    if (type == null) {
      LOGGER.warning("Unrecognized solver type: " + solver_id);
      return null;
    }
    if (!supportsProblemType(type)) {
      LOGGER.warning("Support for " + solver_id + " not linked in, or the license was not found.");
      return null;
    }
    return new MPSolver("", type);
  }

  /** Whether the given problem type is supported (all of them are in this port). */
  public static boolean supportsProblemType(MPSolver.OptimizationProblemType problem_type) {
    return problem_type != null;
  }

  /** Returns true if the underlying solver handles integer variables. */
  public boolean isMip() {
    return solverInterface.isMip();
  }

  boolean isContinuous() {
    return solverInterface.isContinuous();
  }

  /** Returns the optimization problem type set at construction. */
  public MPSolver.OptimizationProblemType problemType() {
    return problemType;
  }

  /** Returns the name of the solver given at construction. */
  public String name() {
    return name;
  }

  /**
   * Clears the objective (including the optimization direction), all variables and constraints.
   * All the other properties of the MPSolver (like the time limit) are kept untouched.
   */
  public void clear() {
    objective.clear();
    variables.clear();
    constraints.clear();
    variableNameToIndex = null;
    constraintNameToIndex = null;
    solverInterface.reset();
    hintVariables.clear();
    hintValues.clear();
    invalidateSolution();
    resultStatus = ResultStatus.NOT_SOLVED;
  }

  /** Returns the number of variables. */
  public int numVariables() {
    return variables.size();
  }

  /**
   * Returns the array of variables handled by the MPSolver. (They are listed in the order in which
   * they were created.)
   */
  public MPVariable[] variables() {
    return variables.toArray(new MPVariable[0]);
  }

  /** Returns the variable at position index. */
  public MPVariable variable(int index) {
    if (index < 0 || index >= variables.size()) {
      return null;
    }
    return variables.get(index);
  }

  /**
   * Looks up a variable by name, and returns null if it does not exist. The first call has a O(n)
   * complexity, as the variable name index is lazily created upon first use. If several variables
   * have the same name, the first one is returned.
   */
  public MPVariable lookupVariableOrNull(String var_name) {
    if (variableNameToIndex == null) {
      variableNameToIndex = new HashMap<>();
      for (MPVariable v : variables) {
        variableNameToIndex.putIfAbsent(v.name(), v.index());
      }
    }
    final Integer index = variableNameToIndex.get(var_name);
    return index == null ? null : variables.get(index);
  }

  /**
   * Creates a variable with the given bounds, integrality requirement and name. Bounds can be
   * finite or +/- MPSolver.infinity(). Variable names are optional. If you give an empty name,
   * name() will auto-generate one for you upon request.
   */
  public MPVariable makeVar(double lb, double ub, boolean integer, String name) {
    final int varIndex = variables.size();
    final MPVariable v = new MPVariable(this, varIndex, lb, ub, integer, name);
    if (variableNameToIndex != null) {
      variableNameToIndex.putIfAbsent(v.name(), varIndex);
    }
    variables.add(v);
    invalidateSolution();
    return v;
  }

  /** Creates a continuous variable. */
  public MPVariable makeNumVar(double lb, double ub, String name) {
    return makeVar(lb, ub, false, name);
  }

  /** Creates an integer variable. */
  public MPVariable makeIntVar(double lb, double ub, String name) {
    return makeVar(lb, ub, true, name);
  }

  /** Creates a boolean variable. */
  public MPVariable makeBoolVar(String name) {
    return makeVar(0.0, 1.0, true, name);
  }

  /** Returns the number of constraints. */
  public int numConstraints() {
    return constraints.size();
  }

  /**
   * Returns the array of constraints handled by the MPSolver.
   *
   * <p>
   * They are listed in the order in which they were created.
   */
  public MPConstraint[] constraints() {
    return constraints.toArray(new MPConstraint[0]);
  }

  /** Returns the constraint at the given index. */
  public MPConstraint constraint(int index) {
    if (index < 0 || index >= constraints.size()) {
      return null;
    }
    return constraints.get(index);
  }

  /**
   * Looks up a constraint by name, and returns null if it does not exist.
   *
   * <p>
   * The first call has a O(n) complexity, as the constraint name index is lazily created upon first
   * use. If several constraints have the same name, the first one is returned.
   */
  public MPConstraint lookupConstraintOrNull(String constraint_name) {
    if (constraintNameToIndex == null) {
      constraintNameToIndex = new HashMap<>();
      for (MPConstraint c : constraints) {
        constraintNameToIndex.putIfAbsent(c.name(), c.index());
      }
    }
    final Integer index = constraintNameToIndex.get(constraint_name);
    return index == null ? null : constraints.get(index);
  }

  /**
   * Creates a linear constraint with given bounds.
   *
   * <p>
   * Bounds can be finite or +/- MPSolver.infinity().
   *
   * @return the newly created constraint.
   */
  public MPConstraint makeConstraint(double lb, double ub) {
    return makeConstraint(lb, ub, "");
  }

  /** Creates a constraint with -infinity and +infinity bounds. */
  public MPConstraint makeConstraint() {
    return makeConstraint(-infinity(), infinity(), "");
  }

  /** Creates a named constraint with given bounds. */
  public MPConstraint makeConstraint(double lb, double ub, String name) {
    final int constraintIndex = constraints.size();
    final MPConstraint constraint = new MPConstraint(this, constraintIndex, lb, ub, name);
    if (constraintNameToIndex != null) {
      constraintNameToIndex.putIfAbsent(constraint.name(), constraintIndex);
    }
    constraints.add(constraint);
    invalidateSolution();
    return constraint;
  }

  /** Creates a named constraint with -infinity and +infinity bounds. */
  public MPConstraint makeConstraint(String name) {
    return makeConstraint(-infinity(), infinity(), name);
  }

  /** Returns the mutable objective object. */
  public MPObjective objective() {
    return objective;
  }

  /** Solves the problem using the default parameter values. */
  public MPSolver.ResultStatus solve() {
    return solve(new MPSolverParameters());
  }

  /** Solves the problem using the specified parameter values. */
  public MPSolver.ResultStatus solve(MPSolverParameters param) {
    if (hasInfeasibleConstraints()) {
      resultStatus = ResultStatus.INFEASIBLE;
      return resultStatus;
    }
    final MPModel model = exportModel();
    final MPSolverInterface.SolveParameters p = new MPSolverInterface.SolveParameters();
    p.relativeMipGap = param.getDoubleParam(MPSolverParameters.DoubleParam.RELATIVE_MIP_GAP);
    p.primalTolerance = param.getDoubleParam(MPSolverParameters.DoubleParam.PRIMAL_TOLERANCE);
    p.dualTolerance = param.getDoubleParam(MPSolverParameters.DoubleParam.DUAL_TOLERANCE);
    p.presolve = param.getIntegerParam(MPSolverParameters.IntegerParam.PRESOLVE);
    p.lpAlgorithm = param.getIntegerParam(MPSolverParameters.IntegerParam.LP_ALGORITHM);
    p.incrementality = param.getIntegerParam(MPSolverParameters.IntegerParam.INCREMENTALITY);
    p.scaling = param.getIntegerParam(MPSolverParameters.IntegerParam.SCALING);
    p.timeLimitMillis = timeLimitMillis;
    p.enableOutput = !quiet;
    p.solverSpecificParameters = solverSpecificParameterString;
    p.numThreads = numThreads;
    if (p.incrementality == MPSolverParameters.IncrementalityValues.INCREMENTALITY_OFF
        .swigValue()) {
      solverInterface.reset();
    }
    final MPSolverInterface.SolveResult result = solverInterface.solve(model, p);
    resultStatus = result.status;
    objectiveValue = result.objectiveValue;
    bestObjectiveBound = result.bestObjectiveBound;
    iterations = result.iterations;
    nodes = result.nodes;
    for (int j = 0; j < variables.size(); ++j) {
      final MPVariable v = variables.get(j);
      v.solutionValue = j < result.variableValues.length ? result.variableValues[j] : 0.0;
      v.reducedCost = j < result.reducedCosts.length ? result.reducedCosts[j] : 0.0;
    }
    for (int i = 0; i < constraints.size(); ++i) {
      constraints.get(i).dualValue = i < result.dualValues.length ? result.dualValues[i] : 0.0;
    }
    columnStatus = result.variableStatuses;
    rowStatus = result.constraintStatuses;
    solutionSynchronized = true;
    return resultStatus;
  }

  /**
   * Writes the model using the solver internal write function. Currently only available for
   * Gurobi, it does nothing here.
   */
  public void write(String file_name) {
    LOGGER.warning("Writing model not implemented in this solver interface.");
  }

  /**
   * Advanced usage: compute the "activities" of all constraints, which are the sums of their linear
   * terms. The activities are returned in the same order as constraints(), which is the order in
   * which constraints were added; but you can also use MPConstraint.index() to get a constraint's
   * index.
   */
  public double[] computeConstraintActivities() {
    if (!checkSolutionIsSynchronizedAndExists()) {
      return new double[0];
    }
    final double[] activities = new double[constraints.size()];
    for (int i = 0; i < constraints.size(); ++i) {
      final LpUtils.KahanSum sum = new LpUtils.KahanSum();
      for (Map.Entry<MPVariable, Double> entry : constraints.get(i).coefficients.entrySet()) {
        sum.add(entry.getKey().solutionValue() * entry.getValue());
      }
      activities[i] = sum.value();
    }
    return activities;
  }

  /**
   * Advanced usage: Verifies the *correctness* of the solution.
   *
   * <p>
   * It verifies that all variables must be within their domains, all constraints must be
   * satisfied, and the reported objective value must be accurate.
   *
   * <p>
   * Usage:
   * <ul>
   * <li>This can only be called after Solve() was called.
   * <li>"tolerance" is interpreted as an absolute error threshold.
   * <li>For the objective value only, if the absolute error is too large, the tolerance is
   * interpreted as a relative error threshold instead.
   * <li>If "log_errors" is true, every single violation will be logged.
   * <li>If "tolerance" is negative, it will be set to infinity().
   * </ul>
   */
  public boolean verifySolution(double tolerance, boolean log_errors) {
    double maxObservedError = 0;
    if (tolerance < 0) {
      tolerance = infinity();
    }
    int numErrors = 0;
    for (MPVariable var : variables) {
      final double value = var.solutionValue();
      if (Double.isNaN(value)) {
        ++numErrors;
        maxObservedError = infinity();
        if (log_errors) {
          LOGGER.severe("NaN value for " + var.name());
        }
        continue;
      }
      if (var.lb() != -infinity()) {
        if (value < var.lb() - tolerance) {
          ++numErrors;
          maxObservedError = Math.max(maxObservedError, var.lb() - value);
          if (log_errors) {
            LOGGER.severe("Value " + value + " too low for " + var.name());
          }
        }
      }
      if (var.ub() != infinity()) {
        if (value > var.ub() + tolerance) {
          ++numErrors;
          maxObservedError = Math.max(maxObservedError, value - var.ub());
          if (log_errors) {
            LOGGER.severe("Value " + value + " too high for " + var.name());
          }
        }
      }
      if (isMip() && var.integer()) {
        if (Math.abs(value - round(value)) > tolerance) {
          ++numErrors;
          maxObservedError = Math.max(maxObservedError, Math.abs(value - round(value)));
          if (log_errors) {
            LOGGER.severe("Non-integer value " + value + " for " + var.name());
          }
        }
      }
    }
    final double[] activities = computeConstraintActivities();
    for (int i = 0; i < constraints.size(); ++i) {
      final MPConstraint constraint = constraints.get(i);
      final double activity = i < activities.length ? activities[i] : 0.0;
      if (Double.isNaN(activity)) {
        ++numErrors;
        maxObservedError = infinity();
        if (log_errors) {
          LOGGER.severe("NaN value for " + constraint.name());
        }
        continue;
      }
      if (constraint.lb() != -infinity()) {
        if (activity < constraint.lb() - tolerance) {
          ++numErrors;
          maxObservedError = Math.max(maxObservedError, constraint.lb() - activity);
          if (log_errors) {
            LOGGER.severe("Activity " + activity + " too low for " + constraint.name());
          }
        }
      }
      if (constraint.ub() != infinity()) {
        if (activity > constraint.ub() + tolerance) {
          ++numErrors;
          maxObservedError = Math.max(maxObservedError, activity - constraint.ub());
          if (log_errors) {
            LOGGER.severe("Activity " + activity + " too high for " + constraint.name());
          }
        }
      }
    }
    final LpUtils.KahanSum objectiveSum = new LpUtils.KahanSum();
    objectiveSum.add(objective.offset());
    for (Map.Entry<MPVariable, Double> entry : objective.coefficients.entrySet()) {
      objectiveSum.add(entry.getKey().solutionValue() * entry.getValue());
    }
    final double actualObjectiveValue = objectiveSum.value();
    if (!LpUtils.areWithinAbsoluteOrRelativeTolerances(objective.value(), actualObjectiveValue,
        tolerance, tolerance)) {
      ++numErrors;
      maxObservedError =
          Math.max(maxObservedError, Math.abs(actualObjectiveValue - objective.value()));
      if (log_errors) {
        LOGGER.severe("Objective value " + objective.value() + " isn't accurate, it should be "
            + actualObjectiveValue + " (delta=" + (actualObjectiveValue - objective.value())
            + ").");
      }
    }
    if (numErrors > 0) {
      if (log_errors) {
        LOGGER.severe("There were " + numErrors + " errors above the tolerance (" + tolerance
            + "), the largest was " + maxObservedError);
      }
      return false;
    }
    return true;
  }

  /**
   * Advanced usage: resets extracted model to solve from scratch.
   *
   * <p>
   * This won't reset the parameters that were set with SetSolverSpecificParametersAsString() or
   * set_time_limit() or even clear the linear program. It will just make sure that next Solve()
   * will be as if everything was reconstructed from scratch.
   */
  public void reset() {
    solverInterface.reset();
  }

  /**
   * Interrupts the Solve() execution to terminate processing if possible.
   *
   * <p>
   * Returns true: both backends of this port support the interruption.
   */
  public boolean interruptSolve() {
    return solverInterface.interruptSolve();
  }

  /**
   * Advanced usage: pass solver specific parameters in text format.
   *
   * <p>
   * For the LP types they are Glop parameters in protocol buffer text format (for example
   * <code>"use_dual_simplex: true"</code>); the MIP types also accept <code>max_nodes: n</code>.
   * Returns true if the operation was successful.
   */
  public boolean setSolverSpecificParametersAsString(String parameters) {
    solverSpecificParameterString = parameters == null ? "" : parameters;
    return solverInterface.checkSolverSpecificParameters(solverSpecificParameterString);
  }

  /**
   * Infinity.
   *
   * <p>
   * You can use -MPSolver.infinity() for negative infinity.
   */
  public static double infinity() {
    return Double.POSITIVE_INFINITY;
  }

  /** Enables solver logging. */
  public void enableOutput() {
    quiet = false;
  }

  /** Suppresses solver logging. */
  public void suppressOutput() {
    quiet = true;
  }

  /** Returns the number of simplex iterations. */
  public long iterations() {
    return iterations;
  }

  /**
   * Returns the number of branch-and-bound nodes evaluated during the solve.
   *
   * <p>
   * Only available for discrete problems (-1 otherwise).
   */
  public long nodes() {
    return isMip() ? nodes : -1;
  }

  /** Returns a string describing the underlying solver and its version. */
  public String solverVersion() {
    return solverInterface.solverVersion();
  }

  /**
   * Advanced usage: computes the exact condition number of the current scaled basis:
   * L1norm(B) * L1norm(inverse(B)), where B is the scaled basis.
   *
   * <p>
   * It is only implemented for GLPK; this port returns 0.0.
   */
  public double computeExactConditionNumber() {
    return solverInterface.computeExactConditionNumber();
  }

  /** Sets a time limit in milliseconds, 0 means no limit. */
  public void setTimeLimit(long time_limit_milliseconds) {
    this.timeLimitMillis = time_limit_milliseconds;
  }

  /** Returns the time limit in milliseconds, 0 means no limit. */
  public long timeLimit() {
    return timeLimitMillis;
  }

  /** The number of milliseconds since the construction of the solver. */
  public long wallTime() {
    return System.currentTimeMillis() - constructionTimeMillis;
  }

  /** Export the loaded model in LP format. */
  public String exportModelAsLpFormat(boolean obfuscate) {
    final MPModelExportOptions options = new MPModelExportOptions();
    options.setObfuscate(obfuscate);
    return exportModelAsLpFormat(options);
  }

  /** Export the loaded model in LP format. */
  public String exportModelAsLpFormat() {
    return exportModelAsLpFormat(false);
  }

  /** Export the loaded model in LP format with the given options. */
  public String exportModelAsLpFormat(MPModelExportOptions options) {
    final String result =
        ModelExporter.exportModelAsLpFormat(exportModel(), toExporterOptions(options));
    return result == null ? "" : result;
  }

  /** Export the loaded model in MPS format (the free format is always used). */
  public String exportModelAsMpsFormat(boolean fixed_format, boolean obfuscate) {
    final MPModelExportOptions options = new MPModelExportOptions();
    options.setObfuscate(obfuscate);
    return exportModelAsMpsFormat(options);
  }

  /** Export the loaded model in MPS format with the given options. */
  public String exportModelAsMpsFormat(MPModelExportOptions options) {
    final String result =
        ModelExporter.exportModelAsMpsFormat(exportModel(), toExporterOptions(options));
    return result == null ? "" : result;
  }

  /** Write the loaded model to file in MPS format. */
  public boolean writeModelToMpsFile(String filename, boolean fixed_format, boolean obfuscate) {
    final String mps = exportModelAsMpsFormat(fixed_format, obfuscate);
    try {
      Files.write(Paths.get(filename), mps.getBytes(StandardCharsets.UTF_8));
      return true;
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }

  static ModelExporter.Options toExporterOptions(MPModelExportOptions options) {
    final ModelExporter.Options o = new ModelExporter.Options();
    o.obfuscate = options.getObfuscate();
    o.logInvalidNames = options.getLogInvalidNames();
    o.showUnusedVariables = options.getShowUnusedVariables();
    o.maxLineLength = options.getMaxLineLength();
    return o;
  }

  /**
   * Sets a hint for solution.
   *
   * <p>
   * If a feasible or almost-feasible solution to the problem is already known, it may be helpful
   * to pass it to the solver so that it can be used. The branch-and-bound uses a complete feasible
   * hint as its first solution; Glop ignores the hint.
   */
  public void setHint(MPVariable[] variables, double[] values) {
    if (variables.length != values.length) {
      throw new IllegalArgumentException(
          "Different number of variables and values when setting hint.");
    }
    hintVariables.clear();
    hintValues.clear();
    for (int i = 0; i < variables.length; ++i) {
      if (!ownsVariable(variables[i])) {
        throw new IllegalArgumentException("hint variable does not belong to this solver");
      }
      hintVariables.add(variables[i]);
      hintValues.add(values[i]);
    }
  }

  /**
   * Sets the number of threads to be used by the solver. Returns false if the solver does not
   * support it (Glop); the branch-and-bound accepts and ignores the setting.
   */
  public boolean setNumThreads(int num_theads) {
    if (num_theads < 1) {
      return false;
    }
    final boolean ok = solverInterface.setNumThreads(num_theads);
    if (ok) {
      numThreads = num_theads;
    }
    return ok;
  }

  // ----- internal -----

  /** Returns a snapshot of the model in the MPModelProto form. */
  MPModel exportModel() {
    final MPModel model = new MPModel();
    model.name = name;
    for (MPVariable var : variables) {
      final MPModel.Variable v = new MPModel.Variable();
      v.name = var.name();
      v.lowerBound = var.lb();
      v.upperBound = var.ub();
      v.isInteger = var.integer();
      v.objectiveCoefficient = objective.getCoefficient(var);
      model.variables.add(v);
    }
    for (MPConstraint constraint : constraints) {
      final MPModel.Constraint c = new MPModel.Constraint();
      c.name = constraint.name();
      c.lowerBound = constraint.lb();
      c.upperBound = constraint.ub();
      c.isLazy = constraint.isLazy();
      final List<int[]> order = new ArrayList<>();
      final List<Double> coeffs = new ArrayList<>();
      for (Map.Entry<MPVariable, Double> entry : constraint.coefficients.entrySet()) {
        order.add(new int[] {entry.getKey().index(), coeffs.size()});
        coeffs.add(entry.getValue());
      }
      order.sort((a, b) -> Integer.compare(a[0], b[0]));
      for (int[] o : order) {
        c.varIndex.add(o[0]);
        c.coefficient.add(coeffs.get(o[1]));
      }
      model.constraints.add(c);
    }
    model.maximize = objective.maximization();
    model.objectiveOffset = objective.offset();
    for (int i = 0; i < hintVariables.size(); ++i) {
      model.hintVarIndex.add(hintVariables.get(i).index());
      model.hintVarValue.add(hintValues.get(i));
    }
    return model;
  }

  boolean ownsVariable(MPVariable var) {
    if (var == null) {
      return false;
    }
    final int index = var.index();
    return index >= 0 && index < variables.size() && variables.get(index) == var;
  }

  void invalidateSolution() {
    solutionSynchronized = false;
  }

  boolean checkSolutionIsSynchronizedAndExists() {
    return solutionSynchronized
        && (resultStatus == ResultStatus.OPTIMAL || resultStatus == ResultStatus.FEASIBLE);
  }

  double objectiveValue() {
    if (!checkSolutionIsSynchronizedAndExists()) {
      return 0.0;
    }
    return objectiveValue;
  }

  double bestObjectiveBound() {
    final double trivialWorstBound =
        objective.maximization() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    if (!isMip()) {
      return trivialWorstBound;
    }
    if (!solutionSynchronized) {
      return trivialWorstBound;
    }
    if (variables.isEmpty() && constraints.isEmpty()) {
      return objective.offset();
    }
    return bestObjectiveBound;
  }

  BasisStatus columnStatus(int index) {
    return index < columnStatus.length ? columnStatus[index] : BasisStatus.FREE;
  }

  BasisStatus rowStatus(int index) {
    return index < rowStatus.length ? rowStatus[index] : BasisStatus.FREE;
  }

  private boolean hasInfeasibleConstraints() {
    boolean hasInfeasibleConstraints = false;
    for (int i = 0; i < constraints.size(); ++i) {
      final MPConstraint c = constraints.get(i);
      if (c.lb() > c.ub()) {
        LOGGER.warning("Constraint " + c.name() + " (" + i + ") has contradictory bounds:"
            + " lower bound = " + c.lb() + " upper bound = " + c.ub());
        hasInfeasibleConstraints = true;
      }
    }
    return hasInfeasibleConstraints;
  }

  /** The C++ <code>std::round</code> (half away from zero). */
  static double round(double x) {
    return LpUtils.round(x);
  }

  /**
   * The type of problems (LP or MIP) that will be solved and the underlying solver (GLOP, GLPK,
   * CLP, CBC or SCIP) that will solve them. This must remain consistent with
   * MPModelRequest::OptimizationProblemType (take particular care of the open-source version).
   *
   * <p>
   * The HIGHS values, which exist in C++ but not in the SWIG wrapper, are added at the end. The
   * KNAPSACK_MIXED_INTEGER_PROGRAMMING value (13) was removed in OR-Tools 10.0.
   */
  public enum OptimizationProblemType {
    CLP_LINEAR_PROGRAMMING(0, false),
    GLPK_LINEAR_PROGRAMMING(1, false),
    GLOP_LINEAR_PROGRAMMING(2, false),
    PDLP_LINEAR_PROGRAMMING(8, false),
    SCIP_MIXED_INTEGER_PROGRAMMING(3, true),
    GLPK_MIXED_INTEGER_PROGRAMMING(4, true),
    CBC_MIXED_INTEGER_PROGRAMMING(5, true),
    BOP_INTEGER_PROGRAMMING(12, true),
    SAT_INTEGER_PROGRAMMING(14, true),
    GUROBI_LINEAR_PROGRAMMING(6, false),
    GUROBI_MIXED_INTEGER_PROGRAMMING(7, true),
    CPLEX_LINEAR_PROGRAMMING(10, false),
    CPLEX_MIXED_INTEGER_PROGRAMMING(11, true),
    XPRESS_LINEAR_PROGRAMMING(101, false),
    XPRESS_MIXED_INTEGER_PROGRAMMING(102, true),
    HIGHS_LINEAR_PROGRAMMING(15, false),
    HIGHS_MIXED_INTEGER_PROGRAMMING(16, true);

    private final int swigValue;
    private final boolean mip;

    OptimizationProblemType(int swigValue, boolean mip) {
      this.swigValue = swigValue;
      this.mip = mip;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** Returns true for the MIP problem types. */
    public boolean isMip() {
      return mip;
    }

    /** The enum with the given C++ value. */
    public static OptimizationProblemType swigToEnum(int swigValue) {
      for (OptimizationProblemType e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + OptimizationProblemType.class + " with value " + swigValue);
    }
  }

  /**
   * The status of solving the problem. The straightforward translation to homonymous enum values
   * of MPSolverResponseStatus (see ./linear_solver.proto) is guaranteed by
   * ./enum_consistency_test.cc, you may rely on it.
   */
  public enum ResultStatus {
    /** optimal. */
    OPTIMAL(0),
    /** feasible, or stopped by limit. */
    FEASIBLE(1),
    /** proven infeasible. */
    INFEASIBLE(2),
    /** proven unbounded. */
    UNBOUNDED(3),
    /** abnormal, i.e., error of some kind. */
    ABNORMAL(4),
    /** the model is trivially invalid (NaN coefficients, etc). */
    MODEL_INVALID(5),
    /** not been solved yet. */
    NOT_SOLVED(6);

    private final int swigValue;

    ResultStatus(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static ResultStatus swigToEnum(int swigValue) {
      for (ResultStatus e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + ResultStatus.class + " with value " + swigValue);
    }
  }

  /**
   * Advanced usage: possible basis status values for a variable and the slack variable of a linear
   * constraint.
   */
  public enum BasisStatus {
    FREE(0),
    AT_LOWER_BOUND(1),
    AT_UPPER_BOUND(2),
    FIXED_VALUE(3),
    BASIC(4);

    private final int swigValue;

    BasisStatus(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static BasisStatus swigToEnum(int swigValue) {
      for (BasisStatus e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + BasisStatus.class + " with value " + swigValue);
    }
  }
}
