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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/cp_model.proto to Java (a plain Java class replaces the generated protobuf message), see NOTICE in the module root.

package org.matheclipse.ortools.sat;

/**
 * <pre>
 * The response returned by a solver trying to solve a CpModelProto.
 *
 * Next id: 32
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CpSolverResponse} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CpSolverResponse implements ProtoMessage {
  private static final CpSolverResponse DEFAULT_INSTANCE = new CpSolverResponse();

  int status_ = 0;
  java.util.ArrayList<Long> solution_;
  double objectiveValue_ = 0.0;
  double bestObjectiveBound_ = 0.0;
  java.util.ArrayList<CpSolverSolution> additionalSolutions_;
  java.util.ArrayList<IntegerVariableProto> tightenedVariables_;
  java.util.ArrayList<Integer> sufficientAssumptionsForInfeasibility_;
  CpObjectiveProto integerObjective_;
  long innerObjectiveLowerBound_ = 0L;
  long numIntegers_ = 0L;
  long numBooleans_ = 0L;
  long numFixedBooleans_ = 0L;
  long numConflicts_ = 0L;
  long numBranches_ = 0L;
  long numBinaryPropagations_ = 0L;
  long numIntegerPropagations_ = 0L;
  long numRestarts_ = 0L;
  long numLpIterations_ = 0L;
  double wallTime_ = 0.0;
  double userTime_ = 0.0;
  double deterministicTime_ = 0.0;
  double gapIntegral_ = 0.0;
  String solutionInfo_ = "";
  String solveLog_ = "";

  private CpSolverResponse() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CpSolverResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CpSolverResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * The status of the solve.
   * </pre>
   */
  public CpSolverStatus getStatus() {
    CpSolverStatus result = CpSolverStatus.forNumber(status_);
    return result == null ? CpSolverStatus.UNRECOGNIZED : result;
  }

  /** Returns the number of {@code status}. */
  public int getStatusValue() {
    return status_;
  }

  /**
   * <pre>
   * A feasible solution to the given problem. Depending on the returned status
   * it may be optimal or just feasible. This is in one-to-one correspondence
   * with a CpModelProto::variables repeated field and lists the values of all
   * the variables.
   * </pre>
   */
  public java.util.List<Long> getSolutionList() {
    return solution_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(solution_);
  }

  /** Returns the number of elements of {@code solution}. */
  public int getSolutionCount() {
    return solution_ == null ? 0 : solution_.size();
  }

  /** Returns the element {@code index} of {@code solution}. */
  public long getSolution(int index) {
    if (solution_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return solution_.get(index);
  }

  /**
   * <pre>
   * Only makes sense for an optimization problem. The objective value of the
   * returned solution if it is non-empty. If there is no solution, then for a
   * minimization problem, this will be an upper-bound of the objective of any
   * feasible solution, and a lower-bound for a maximization problem.
   * </pre>
   */
  public double getObjectiveValue() {
    return objectiveValue_;
  }

  /**
   * <pre>
   * Only makes sense for an optimization problem. A proven lower-bound on the
   * objective for a minimization problem, or a proven upper-bound for a
   * maximization problem.
   * </pre>
   */
  public double getBestObjectiveBound() {
    return bestObjectiveBound_;
  }

  /**
   * <pre>
   * If the parameter fill_additional_solutions_in_response is set, then we
   * copy all the solutions from our internal solution pool here.
   *
   * Note that the one returned in the solution field will likely appear here
   * too. Do not rely on the solutions order as it depends on our internal
   * representation (after postsolve).
   * </pre>
   */
  public java.util.List<CpSolverSolution> getAdditionalSolutionsList() {
    return additionalSolutions_ == null ? java.util.Collections.<CpSolverSolution>emptyList() : java.util.Collections.unmodifiableList(additionalSolutions_);
  }

  /** Returns the number of elements of {@code additional_solutions}. */
  public int getAdditionalSolutionsCount() {
    return additionalSolutions_ == null ? 0 : additionalSolutions_.size();
  }

  /** Returns the element {@code index} of {@code additional_solutions}. */
  public CpSolverSolution getAdditionalSolutions(int index) {
    if (additionalSolutions_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return additionalSolutions_.get(index);
  }

  /**
   * <pre>
   * Advanced usage.
   *
   * If the option fill_tightened_domains_in_response is set, then this field
   * will be a copy of the CpModelProto.variables where each domain has been
   * reduced using the information the solver was able to derive. Note that this
   * is only filled with the info derived during a normal search and we do not
   * have any dedicated algorithm to improve it.
   *
   * Warning: if you didn't set keep_all_feasible_solutions_in_presolve, then
   * these domains might exclude valid feasible solution. Otherwise for a
   * feasibility problem, all feasible solutions should be there.
   *
   * Warning: For an optimization problem, these will correspond to valid bounds
   * for the problem of finding an improving solution to the best one found so
   * far. It might be better to solve a feasibility version if one just wants to
   * explore the feasible region.
   * </pre>
   */
  public java.util.List<IntegerVariableProto> getTightenedVariablesList() {
    return tightenedVariables_ == null ? java.util.Collections.<IntegerVariableProto>emptyList() : java.util.Collections.unmodifiableList(tightenedVariables_);
  }

  /** Returns the number of elements of {@code tightened_variables}. */
  public int getTightenedVariablesCount() {
    return tightenedVariables_ == null ? 0 : tightenedVariables_.size();
  }

  /** Returns the element {@code index} of {@code tightened_variables}. */
  public IntegerVariableProto getTightenedVariables(int index) {
    if (tightenedVariables_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return tightenedVariables_.get(index);
  }

  /**
   * <pre>
   * A subset of the model "assumptions" field. This will only be filled if the
   * status is INFEASIBLE. This subset of assumptions will be enough to still
   * get an infeasible problem.
   *
   * This is related to what is called the irreducible inconsistent subsystem or
   * IIS. Except one is only concerned by the provided assumptions. There is
   * also no guarantee that we return an irreducible (aka minimal subset).
   * However, this is based on SAT explanation and there is a good chance it is
   * not too large.
   *
   * If you really want a minimal subset, a possible way to get one is by
   * changing your model to minimize the number of assumptions at false, but
   * this is likely a harder problem to solve.
   *
   * Important: Currently, this is minimized only in single-thread and if the
   * problem is not an optimization problem, otherwise, it will always include
   * all the assumptions.
   *
   * TODO(user): Allows for returning multiple cores at once.
   * </pre>
   */
  public java.util.List<Integer> getSufficientAssumptionsForInfeasibilityList() {
    return sufficientAssumptionsForInfeasibility_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(sufficientAssumptionsForInfeasibility_);
  }

  /** Returns the number of elements of {@code sufficient_assumptions_for_infeasibility}. */
  public int getSufficientAssumptionsForInfeasibilityCount() {
    return sufficientAssumptionsForInfeasibility_ == null ? 0 : sufficientAssumptionsForInfeasibility_.size();
  }

  /** Returns the element {@code index} of {@code sufficient_assumptions_for_infeasibility}. */
  public int getSufficientAssumptionsForInfeasibility(int index) {
    if (sufficientAssumptionsForInfeasibility_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return sufficientAssumptionsForInfeasibility_.get(index);
  }

  /** Returns true if {@code integer_objective} is set. */
  public boolean hasIntegerObjective() {
    return integerObjective_ != null;
  }

  /**
   * <pre>
   * Contains the integer objective optimized internally. This is only filled if
   * the problem had a floating point objective, and on the final response, not
   * the ones given to callbacks.
   * </pre>
   */
  public CpObjectiveProto getIntegerObjective() {
    return integerObjective_ == null ? CpObjectiveProto.getDefaultInstance() : integerObjective_;
  }

  /**
   * <pre>
   * Advanced usage.
   *
   * A lower bound on the integer expression of the objective. This is either a
   * bound on the expression in the returned integer_objective or on the integer
   * expression of the original objective if the problem already has an integer
   * objective.
   *
   * TODO(user): This should be renamed integer_objective_lower_bound.
   * </pre>
   */
  public long getInnerObjectiveLowerBound() {
    return innerObjectiveLowerBound_;
  }

  /**
   * <pre>
   * Some statistics about the solve.
   *
   * Important: in multithread, this corresponds to the statistics of the first
   * subsolver. Which is usually the one with the user defined parameters. Or
   * the default-search if none are specified.
   * </pre>
   */
  public long getNumIntegers() {
    return numIntegers_;
  }

  /** Returns {@code num_booleans}. */
  public long getNumBooleans() {
    return numBooleans_;
  }

  /** Returns {@code num_fixed_booleans}. */
  public long getNumFixedBooleans() {
    return numFixedBooleans_;
  }

  /** Returns {@code num_conflicts}. */
  public long getNumConflicts() {
    return numConflicts_;
  }

  /** Returns {@code num_branches}. */
  public long getNumBranches() {
    return numBranches_;
  }

  /** Returns {@code num_binary_propagations}. */
  public long getNumBinaryPropagations() {
    return numBinaryPropagations_;
  }

  /** Returns {@code num_integer_propagations}. */
  public long getNumIntegerPropagations() {
    return numIntegerPropagations_;
  }

  /** Returns {@code num_restarts}. */
  public long getNumRestarts() {
    return numRestarts_;
  }

  /** Returns {@code num_lp_iterations}. */
  public long getNumLpIterations() {
    return numLpIterations_;
  }

  /**
   * <pre>
   * The time counted from the beginning of the Solve() call.
   * </pre>
   */
  public double getWallTime() {
    return wallTime_;
  }

  /** Returns {@code user_time}. */
  public double getUserTime() {
    return userTime_;
  }

  /** Returns {@code deterministic_time}. */
  public double getDeterministicTime() {
    return deterministicTime_;
  }

  /**
   * <pre>
   * The integral of log(1 + absolute_objective_gap) over time.
   * </pre>
   */
  public double getGapIntegral() {
    return gapIntegral_;
  }

  /**
   * <pre>
   * Additional information about how the solution was found. It also stores
   * model or parameters errors that caused the model to be invalid.
   * </pre>
   */
  public String getSolutionInfo() {
    return solutionInfo_;
  }

  /**
   * <pre>
   * The solve log will be filled if the parameter log_to_response is set to
   * true.
   * </pre>
   */
  public String getSolveLog() {
    return solveLog_;
  }

  void copyFieldsFrom(CpSolverResponse src) {
    status_ = src.status_;
    solution_ = src.solution_ == null || src.solution_.isEmpty() ? null : new java.util.ArrayList<>(src.solution_);
    objectiveValue_ = src.objectiveValue_;
    bestObjectiveBound_ = src.bestObjectiveBound_;
    additionalSolutions_ = buildListCpSolverSolution(src.additionalSolutions_);
    tightenedVariables_ = buildListIntegerVariableProto(src.tightenedVariables_);
    sufficientAssumptionsForInfeasibility_ = src.sufficientAssumptionsForInfeasibility_ == null || src.sufficientAssumptionsForInfeasibility_.isEmpty() ? null : new java.util.ArrayList<>(src.sufficientAssumptionsForInfeasibility_);
    integerObjective_ = src.integerObjective_ == null ? null : src.integerObjective_.immutableCopy();
    innerObjectiveLowerBound_ = src.innerObjectiveLowerBound_;
    numIntegers_ = src.numIntegers_;
    numBooleans_ = src.numBooleans_;
    numFixedBooleans_ = src.numFixedBooleans_;
    numConflicts_ = src.numConflicts_;
    numBranches_ = src.numBranches_;
    numBinaryPropagations_ = src.numBinaryPropagations_;
    numIntegerPropagations_ = src.numIntegerPropagations_;
    numRestarts_ = src.numRestarts_;
    numLpIterations_ = src.numLpIterations_;
    wallTime_ = src.wallTime_;
    userTime_ = src.userTime_;
    deterministicTime_ = src.deterministicTime_;
    gapIntegral_ = src.gapIntegral_;
    solutionInfo_ = src.solutionInfo_;
    solveLog_ = src.solveLog_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CpSolverResponse immutableCopy() {
    if (this instanceof Builder) {
      CpSolverResponse r = new CpSolverResponse();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<CpSolverSolution> buildListCpSolverSolution(java.util.List<CpSolverSolution> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<CpSolverSolution> r = new java.util.ArrayList<>(src.size());
    for (CpSolverSolution e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  private static java.util.ArrayList<IntegerVariableProto> buildListIntegerVariableProto(java.util.List<IntegerVariableProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<IntegerVariableProto> r = new java.util.ArrayList<>(src.size());
    for (IntegerVariableProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (status_ != 0) {
      CpSolverStatus e = CpSolverStatus.forNumber(status_);
      printer.printEnum("status", e == null ? Integer.toString(status_) : e.name());
    }
    if (solution_ != null) {
      for (Long v : solution_) {
        printer.printScalar("solution", v);
      }
    }
    if (Double.doubleToRawLongBits(objectiveValue_) != 0L) {
      printer.printScalar("objective_value", objectiveValue_);
    }
    if (Double.doubleToRawLongBits(bestObjectiveBound_) != 0L) {
      printer.printScalar("best_objective_bound", bestObjectiveBound_);
    }
    if (numBooleans_ != 0) {
      printer.printScalar("num_booleans", numBooleans_);
    }
    if (numConflicts_ != 0) {
      printer.printScalar("num_conflicts", numConflicts_);
    }
    if (numBranches_ != 0) {
      printer.printScalar("num_branches", numBranches_);
    }
    if (numBinaryPropagations_ != 0) {
      printer.printScalar("num_binary_propagations", numBinaryPropagations_);
    }
    if (numIntegerPropagations_ != 0) {
      printer.printScalar("num_integer_propagations", numIntegerPropagations_);
    }
    if (Double.doubleToRawLongBits(wallTime_) != 0L) {
      printer.printScalar("wall_time", wallTime_);
    }
    if (Double.doubleToRawLongBits(userTime_) != 0L) {
      printer.printScalar("user_time", userTime_);
    }
    if (Double.doubleToRawLongBits(deterministicTime_) != 0L) {
      printer.printScalar("deterministic_time", deterministicTime_);
    }
    if (!solutionInfo_.isEmpty()) {
      printer.printScalar("solution_info", solutionInfo_);
    }
    if (tightenedVariables_ != null) {
      for (IntegerVariableProto v : tightenedVariables_) {
        printer.printMessage("tightened_variables", v);
      }
    }
    if (Double.doubleToRawLongBits(gapIntegral_) != 0L) {
      printer.printScalar("gap_integral", gapIntegral_);
    }
    if (sufficientAssumptionsForInfeasibility_ != null) {
      for (Integer v : sufficientAssumptionsForInfeasibility_) {
        printer.printScalar("sufficient_assumptions_for_infeasibility", v);
      }
    }
    if (numRestarts_ != 0) {
      printer.printScalar("num_restarts", numRestarts_);
    }
    if (numLpIterations_ != 0) {
      printer.printScalar("num_lp_iterations", numLpIterations_);
    }
    if (!solveLog_.isEmpty()) {
      printer.printScalar("solve_log", solveLog_);
    }
    if (additionalSolutions_ != null) {
      for (CpSolverSolution v : additionalSolutions_) {
        printer.printMessage("additional_solutions", v);
      }
    }
    if (integerObjective_ != null) {
      printer.printMessage("integer_objective", integerObjective_);
    }
    if (innerObjectiveLowerBound_ != 0) {
      printer.printScalar("inner_objective_lower_bound", innerObjectiveLowerBound_);
    }
    if (numIntegers_ != 0) {
      printer.printScalar("num_integers", numIntegers_);
    }
    if (numFixedBooleans_ != 0) {
      printer.printScalar("num_fixed_booleans", numFixedBooleans_);
    }
  }

  /** Returns the message in protobuf text format. */
  @Override
  public String toString() {
    return TextFormat.printToString(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof CpSolverResponse)) {
      return false;
    }
    CpSolverResponse other = (CpSolverResponse) obj;
    return status_ == other.status_
        && getSolutionList().equals(other.getSolutionList())
        && Double.compare(objectiveValue_, other.objectiveValue_) == 0
        && Double.compare(bestObjectiveBound_, other.bestObjectiveBound_) == 0
        && getAdditionalSolutionsList().equals(other.getAdditionalSolutionsList())
        && getTightenedVariablesList().equals(other.getTightenedVariablesList())
        && getSufficientAssumptionsForInfeasibilityList().equals(other.getSufficientAssumptionsForInfeasibilityList())
        && java.util.Objects.equals(integerObjective_, other.integerObjective_)
        && innerObjectiveLowerBound_ == other.innerObjectiveLowerBound_
        && numIntegers_ == other.numIntegers_
        && numBooleans_ == other.numBooleans_
        && numFixedBooleans_ == other.numFixedBooleans_
        && numConflicts_ == other.numConflicts_
        && numBranches_ == other.numBranches_
        && numBinaryPropagations_ == other.numBinaryPropagations_
        && numIntegerPropagations_ == other.numIntegerPropagations_
        && numRestarts_ == other.numRestarts_
        && numLpIterations_ == other.numLpIterations_
        && Double.compare(wallTime_, other.wallTime_) == 0
        && Double.compare(userTime_, other.userTime_) == 0
        && Double.compare(deterministicTime_, other.deterministicTime_) == 0
        && Double.compare(gapIntegral_, other.gapIntegral_) == 0
        && solutionInfo_.equals(other.solutionInfo_)
        && solveLog_.equals(other.solveLog_);
  }

  @Override
  public int hashCode() {
    int h = 655;
    h = 31 * h + Integer.hashCode(status_);
    h = 31 * h + getSolutionList().hashCode();
    h = 31 * h + Double.hashCode(objectiveValue_);
    h = 31 * h + Double.hashCode(bestObjectiveBound_);
    h = 31 * h + getAdditionalSolutionsList().hashCode();
    h = 31 * h + getTightenedVariablesList().hashCode();
    h = 31 * h + getSufficientAssumptionsForInfeasibilityList().hashCode();
    h = 31 * h + java.util.Objects.hashCode(integerObjective_);
    h = 31 * h + Long.hashCode(innerObjectiveLowerBound_);
    h = 31 * h + Long.hashCode(numIntegers_);
    h = 31 * h + Long.hashCode(numBooleans_);
    h = 31 * h + Long.hashCode(numFixedBooleans_);
    h = 31 * h + Long.hashCode(numConflicts_);
    h = 31 * h + Long.hashCode(numBranches_);
    h = 31 * h + Long.hashCode(numBinaryPropagations_);
    h = 31 * h + Long.hashCode(numIntegerPropagations_);
    h = 31 * h + Long.hashCode(numRestarts_);
    h = 31 * h + Long.hashCode(numLpIterations_);
    h = 31 * h + Double.hashCode(wallTime_);
    h = 31 * h + Double.hashCode(userTime_);
    h = 31 * h + Double.hashCode(deterministicTime_);
    h = 31 * h + Double.hashCode(gapIntegral_);
    h = 31 * h + solutionInfo_.hashCode();
    h = 31 * h + solveLog_.hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (status_ != 0) {
      w.int32(1, status_);
    }
    if (solution_ != null && !solution_.isEmpty()) {
      w.packedInt64(2, solution_);
    }
    if (Double.doubleToRawLongBits(objectiveValue_) != 0L) {
      w.dbl(3, objectiveValue_);
    }
    if (Double.doubleToRawLongBits(bestObjectiveBound_) != 0L) {
      w.dbl(4, bestObjectiveBound_);
    }
    if (numBooleans_ != 0) {
      w.int64(10, numBooleans_);
    }
    if (numConflicts_ != 0) {
      w.int64(11, numConflicts_);
    }
    if (numBranches_ != 0) {
      w.int64(12, numBranches_);
    }
    if (numBinaryPropagations_ != 0) {
      w.int64(13, numBinaryPropagations_);
    }
    if (numIntegerPropagations_ != 0) {
      w.int64(14, numIntegerPropagations_);
    }
    if (Double.doubleToRawLongBits(wallTime_) != 0L) {
      w.dbl(15, wallTime_);
    }
    if (Double.doubleToRawLongBits(userTime_) != 0L) {
      w.dbl(16, userTime_);
    }
    if (Double.doubleToRawLongBits(deterministicTime_) != 0L) {
      w.dbl(17, deterministicTime_);
    }
    if (!solutionInfo_.isEmpty()) {
      w.string(20, solutionInfo_);
    }
    if (tightenedVariables_ != null) {
      for (IntegerVariableProto v : tightenedVariables_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(21, sub);
      }
    }
    if (Double.doubleToRawLongBits(gapIntegral_) != 0L) {
      w.dbl(22, gapIntegral_);
    }
    if (sufficientAssumptionsForInfeasibility_ != null && !sufficientAssumptionsForInfeasibility_.isEmpty()) {
      w.packedInt32(23, sufficientAssumptionsForInfeasibility_);
    }
    if (numRestarts_ != 0) {
      w.int64(24, numRestarts_);
    }
    if (numLpIterations_ != 0) {
      w.int64(25, numLpIterations_);
    }
    if (!solveLog_.isEmpty()) {
      w.string(26, solveLog_);
    }
    if (additionalSolutions_ != null) {
      for (CpSolverSolution v : additionalSolutions_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(27, sub);
      }
    }
    if (integerObjective_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      integerObjective_.writeTo(sub);
      w.bytes(28, sub);
    }
    if (innerObjectiveLowerBound_ != 0) {
      w.int64(29, innerObjectiveLowerBound_);
    }
    if (numIntegers_ != 0) {
      w.int64(30, numIntegers_);
    }
    if (numFixedBooleans_ != 0) {
      w.int64(31, numFixedBooleans_);
    }
  }

  /** Returns the message serialized in the protobuf binary format. */
  public byte[] toByteArray() {
    ProtoWire.Writer w = new ProtoWire.Writer();
    writeTo(w);
    return w.toByteArray();
  }

  /**
   * Parses a message in the protobuf binary format; returns {@code null} if the data is not a valid
   * serialized message. Unknown fields are skipped.
   */
  public static CpSolverResponse parseFrom(byte[] data) {
    Builder b = new Builder();
    try {
      b.mergeWire(new ProtoWire.Reader(data));
    } catch (ProtoWire.ParseError e) {
      return null;
    }
    return b.build();
  }

  /** Returns a new builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Returns a new builder initialized with a copy of {@code prototype}. */
  public static Builder newBuilder(CpSolverResponse prototype) {
    Builder b = new Builder();
    b.copyFieldsFrom(prototype);
    return b;
  }

  /** Returns a new builder initialized with a copy of this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** Returns a new builder. */
  public Builder newBuilderForType() {
    return new Builder();
  }

  /**
   * Builder of {@link CpSolverResponse}. A builder is also a (mutable) {@code CpSolverResponse}, so all getters are
   * available on it.
   */
  public static final class Builder extends CpSolverResponse implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CpSolverResponse build() {
      CpSolverResponse r = new CpSolverResponse();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CpSolverResponse buildPartial() {
      return build();
    }

    /** Resets all fields to their default values. */
    public Builder clear() {
      copyFieldsFrom(DEFAULT_INSTANCE);
      return this;
    }

    /** Returns a deep copy of this builder. */
    @Override
    public Builder clone() {
      Builder b = new Builder();
      b.copyFieldsFrom(this);
      return b;
    }

    /**
     * Merges {@code other} into this builder with the protobuf semantics: set scalar fields
     * overwrite, repeated fields are appended, message fields are merged recursively.
     */
    public Builder mergeFrom(CpSolverResponse other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.status_ != 0) {
        setStatusValue(other.status_);
      }
      if (other.solution_ != null) {
        addAllSolution(other.solution_);
      }
      if (Double.doubleToRawLongBits(other.objectiveValue_) != 0L) {
        setObjectiveValue(other.objectiveValue_);
      }
      if (Double.doubleToRawLongBits(other.bestObjectiveBound_) != 0L) {
        setBestObjectiveBound(other.bestObjectiveBound_);
      }
      if (other.additionalSolutions_ != null) {
        addAllAdditionalSolutions(other.additionalSolutions_);
      }
      if (other.tightenedVariables_ != null) {
        addAllTightenedVariables(other.tightenedVariables_);
      }
      if (other.sufficientAssumptionsForInfeasibility_ != null) {
        addAllSufficientAssumptionsForInfeasibility(other.sufficientAssumptionsForInfeasibility_);
      }
      if (other.integerObjective_ != null) {
        mergeIntegerObjective(other.integerObjective_);
      }
      if (other.innerObjectiveLowerBound_ != 0) {
        setInnerObjectiveLowerBound(other.innerObjectiveLowerBound_);
      }
      if (other.numIntegers_ != 0) {
        setNumIntegers(other.numIntegers_);
      }
      if (other.numBooleans_ != 0) {
        setNumBooleans(other.numBooleans_);
      }
      if (other.numFixedBooleans_ != 0) {
        setNumFixedBooleans(other.numFixedBooleans_);
      }
      if (other.numConflicts_ != 0) {
        setNumConflicts(other.numConflicts_);
      }
      if (other.numBranches_ != 0) {
        setNumBranches(other.numBranches_);
      }
      if (other.numBinaryPropagations_ != 0) {
        setNumBinaryPropagations(other.numBinaryPropagations_);
      }
      if (other.numIntegerPropagations_ != 0) {
        setNumIntegerPropagations(other.numIntegerPropagations_);
      }
      if (other.numRestarts_ != 0) {
        setNumRestarts(other.numRestarts_);
      }
      if (other.numLpIterations_ != 0) {
        setNumLpIterations(other.numLpIterations_);
      }
      if (Double.doubleToRawLongBits(other.wallTime_) != 0L) {
        setWallTime(other.wallTime_);
      }
      if (Double.doubleToRawLongBits(other.userTime_) != 0L) {
        setUserTime(other.userTime_);
      }
      if (Double.doubleToRawLongBits(other.deterministicTime_) != 0L) {
        setDeterministicTime(other.deterministicTime_);
      }
      if (Double.doubleToRawLongBits(other.gapIntegral_) != 0L) {
        setGapIntegral(other.gapIntegral_);
      }
      if (!other.solutionInfo_.isEmpty()) {
        setSolutionInfo(other.solutionInfo_);
      }
      if (!other.solveLog_.isEmpty()) {
        setSolveLog(other.solveLog_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CpSolverResponse other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code solution}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearSolution()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getSolutionList() {
      if (solution_ == null) {
        solution_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(solution_, null);
    }

    /**
     * Returns a modifiable view of {@code additional_solutions}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearAdditionalSolutions()} and {@code clear()}.
     */
    @Override
    public java.util.List<CpSolverSolution> getAdditionalSolutionsList() {
      if (additionalSolutions_ == null) {
        additionalSolutions_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(additionalSolutions_, CpSolverSolution::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code tightened_variables}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTightenedVariables()} and {@code clear()}.
     */
    @Override
    public java.util.List<IntegerVariableProto> getTightenedVariablesList() {
      if (tightenedVariables_ == null) {
        tightenedVariables_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(tightenedVariables_, IntegerVariableProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code sufficient_assumptions_for_infeasibility}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearSufficientAssumptionsForInfeasibility()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getSufficientAssumptionsForInfeasibilityList() {
      if (sufficientAssumptionsForInfeasibility_ == null) {
        sufficientAssumptionsForInfeasibility_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(sufficientAssumptionsForInfeasibility_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 8:
            setStatusValue((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addSolution(p.varint());
            }
            break;
          }
          case 16:
            addSolution(r.varint());
            break;
          case 25:
            setObjectiveValue(r.dbl());
            break;
          case 33:
            setBestObjectiveBound(r.dbl());
            break;
          case 80:
            setNumBooleans(r.varint());
            break;
          case 88:
            setNumConflicts(r.varint());
            break;
          case 96:
            setNumBranches(r.varint());
            break;
          case 104:
            setNumBinaryPropagations(r.varint());
            break;
          case 112:
            setNumIntegerPropagations(r.varint());
            break;
          case 121:
            setWallTime(r.dbl());
            break;
          case 129:
            setUserTime(r.dbl());
            break;
          case 137:
            setDeterministicTime(r.dbl());
            break;
          case 162:
            setSolutionInfo(r.string(true));
            break;
          case 170:
            addTightenedVariablesBuilder().mergeWire(r.message());
            break;
          case 177:
            setGapIntegral(r.dbl());
            break;
          case 186: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addSufficientAssumptionsForInfeasibility((int) p.varint());
            }
            break;
          }
          case 184:
            addSufficientAssumptionsForInfeasibility((int) r.varint());
            break;
          case 192:
            setNumRestarts(r.varint());
            break;
          case 200:
            setNumLpIterations(r.varint());
            break;
          case 210:
            setSolveLog(r.string(true));
            break;
          case 218:
            addAdditionalSolutionsBuilder().mergeWire(r.message());
            break;
          case 226:
            getIntegerObjectiveBuilder().mergeWire(r.message());
            break;
          case 232:
            setInnerObjectiveLowerBound(r.varint());
            break;
          case 240:
            setNumIntegers(r.varint());
            break;
          case 248:
            setNumFixedBooleans(r.varint());
            break;
          default:
            r.skip(tag);
        }
      }
    }

    void mergeText(ProtoWire.Text t, String end, boolean strict) {
      while (!t.atMessageEnd(end)) {
        String name = t.fieldName();
        switch (name) {
          case "status": {
            if (strict && status_ != 0) {
              throw ProtoWire.duplicate(t, "status");
            }
            t.consume(":");
            setStatusValue(t.enumValue(CpSolverStatus.class, CpSolverStatus::forNumber, CpSolverStatus::getNumber, false, "status"));
            break;
          }
          case "solution": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSolution(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addSolution(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "objective_value": {
            if (strict && Double.doubleToRawLongBits(objectiveValue_) != 0L) {
              throw ProtoWire.duplicate(t, "objective_value");
            }
            t.consume(":");
            setObjectiveValue(t.floating());
            break;
          }
          case "best_objective_bound": {
            if (strict && Double.doubleToRawLongBits(bestObjectiveBound_) != 0L) {
              throw ProtoWire.duplicate(t, "best_objective_bound");
            }
            t.consume(":");
            setBestObjectiveBound(t.floating());
            break;
          }
          case "additional_solutions": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addAdditionalSolutionsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addAdditionalSolutionsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "tightened_variables": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTightenedVariablesBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addTightenedVariablesBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "sufficient_assumptions_for_infeasibility": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSufficientAssumptionsForInfeasibility((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addSufficientAssumptionsForInfeasibility((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "integer_objective": {
            if (strict && integerObjective_ != null) {
              throw ProtoWire.duplicate(t, "integer_objective");
            }
            t.tryConsume(":");
            getIntegerObjectiveBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "inner_objective_lower_bound": {
            if (strict && innerObjectiveLowerBound_ != 0) {
              throw ProtoWire.duplicate(t, "inner_objective_lower_bound");
            }
            t.consume(":");
            setInnerObjectiveLowerBound(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_integers": {
            if (strict && numIntegers_ != 0) {
              throw ProtoWire.duplicate(t, "num_integers");
            }
            t.consume(":");
            setNumIntegers(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_booleans": {
            if (strict && numBooleans_ != 0) {
              throw ProtoWire.duplicate(t, "num_booleans");
            }
            t.consume(":");
            setNumBooleans(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_fixed_booleans": {
            if (strict && numFixedBooleans_ != 0) {
              throw ProtoWire.duplicate(t, "num_fixed_booleans");
            }
            t.consume(":");
            setNumFixedBooleans(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_conflicts": {
            if (strict && numConflicts_ != 0) {
              throw ProtoWire.duplicate(t, "num_conflicts");
            }
            t.consume(":");
            setNumConflicts(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_branches": {
            if (strict && numBranches_ != 0) {
              throw ProtoWire.duplicate(t, "num_branches");
            }
            t.consume(":");
            setNumBranches(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_binary_propagations": {
            if (strict && numBinaryPropagations_ != 0) {
              throw ProtoWire.duplicate(t, "num_binary_propagations");
            }
            t.consume(":");
            setNumBinaryPropagations(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_integer_propagations": {
            if (strict && numIntegerPropagations_ != 0) {
              throw ProtoWire.duplicate(t, "num_integer_propagations");
            }
            t.consume(":");
            setNumIntegerPropagations(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_restarts": {
            if (strict && numRestarts_ != 0) {
              throw ProtoWire.duplicate(t, "num_restarts");
            }
            t.consume(":");
            setNumRestarts(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "num_lp_iterations": {
            if (strict && numLpIterations_ != 0) {
              throw ProtoWire.duplicate(t, "num_lp_iterations");
            }
            t.consume(":");
            setNumLpIterations(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "wall_time": {
            if (strict && Double.doubleToRawLongBits(wallTime_) != 0L) {
              throw ProtoWire.duplicate(t, "wall_time");
            }
            t.consume(":");
            setWallTime(t.floating());
            break;
          }
          case "user_time": {
            if (strict && Double.doubleToRawLongBits(userTime_) != 0L) {
              throw ProtoWire.duplicate(t, "user_time");
            }
            t.consume(":");
            setUserTime(t.floating());
            break;
          }
          case "deterministic_time": {
            if (strict && Double.doubleToRawLongBits(deterministicTime_) != 0L) {
              throw ProtoWire.duplicate(t, "deterministic_time");
            }
            t.consume(":");
            setDeterministicTime(t.floating());
            break;
          }
          case "gap_integral": {
            if (strict && Double.doubleToRawLongBits(gapIntegral_) != 0L) {
              throw ProtoWire.duplicate(t, "gap_integral");
            }
            t.consume(":");
            setGapIntegral(t.floating());
            break;
          }
          case "solution_info": {
            if (strict && !solutionInfo_.isEmpty()) {
              throw ProtoWire.duplicate(t, "solution_info");
            }
            t.consume(":");
            setSolutionInfo(t.string());
            break;
          }
          case "solve_log": {
            if (strict && !solveLog_.isEmpty()) {
              throw ProtoWire.duplicate(t, "solve_log");
            }
            t.consume(":");
            setSolveLog(t.string());
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.CpSolverResponse", name);
        }
        t.separator();
      }
    }

    /**
     * Replaces the content of this builder by the message serialized in {@code data} (protobuf
     * binary format; unknown fields are skipped). Like the OR-Tools 10.0 wrapper, the builder is
     * cleared first.
     *
     * @throws RuntimeException {@code "parse failure"} if the data is not a valid serialized message
     */
    public void mergeFrom(byte[] data) {
      clear();
      try {
        mergeWire(new ProtoWire.Reader(data));
      } catch (ProtoWire.ParseError e) {
        throw new RuntimeException("parse failure", e);
      }
    }

    /**
     * Replaces the content of this builder by the message given in protobuf text format; returns
     * false on a syntax error, an unknown field or enum value, or a non-repeated field given twice
     * (the builder then holds the fields parsed before the error).
     */
    public boolean parseTextFormat(String text) {
      clear();
      try {
        mergeText(new ProtoWire.Text(text), null, true);
        return true;
      } catch (ProtoWire.ParseError e) {
        return false;
      }
    }

    /** Sets {@code status}. */
    public Builder setStatus(CpSolverStatus value) {
      if (value == null) {
        throw new NullPointerException();
      }
      status_ = value.getNumber();
      return this;
    }

    /** Sets the number of {@code status}. */
    public Builder setStatusValue(int value) {
      status_ = value;
      return this;
    }

    /** Resets {@code status} to its default value. */
    public Builder clearStatus() {
      status_ = 0;
      return this;
    }

    /** Replaces the element {@code index} of {@code solution}. */
    public Builder setSolution(int index, long value) {
      if (solution_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      solution_.set(index, value);
      return this;
    }

    /** Appends a value to {@code solution}. */
    public Builder addSolution(long value) {
      if (solution_ == null) {
        solution_ = new java.util.ArrayList<>();
      }
      solution_.add(value);
      return this;
    }

    /** Appends all values to {@code solution}. */
    public Builder addAllSolution(Iterable<? extends Long> values) {
      if (solution_ == null) {
        solution_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        solution_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code solution}. */
    public Builder clearSolution() {
      solution_ = null;
      return this;
    }

    /** Sets {@code objective_value}. */
    public Builder setObjectiveValue(double value) {
      objectiveValue_ = value;
      return this;
    }

    /** Resets {@code objective_value} to its default value. */
    public Builder clearObjectiveValue() {
      objectiveValue_ = 0.0;
      return this;
    }

    /** Sets {@code best_objective_bound}. */
    public Builder setBestObjectiveBound(double value) {
      bestObjectiveBound_ = value;
      return this;
    }

    /** Resets {@code best_objective_bound} to its default value. */
    public Builder clearBestObjectiveBound() {
      bestObjectiveBound_ = 0.0;
      return this;
    }

    /** Replaces the element {@code index} of {@code additional_solutions}. */
    public Builder setAdditionalSolutions(int index, CpSolverSolution value) {
      if (additionalSolutions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      additionalSolutions_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code additional_solutions}. */
    public Builder addAdditionalSolutions(CpSolverSolution value) {
      if (additionalSolutions_ == null) {
        additionalSolutions_ = new java.util.ArrayList<>();
      }
      additionalSolutions_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code additional_solutions}. */
    public Builder addAllAdditionalSolutions(Iterable<? extends CpSolverSolution> values) {
      if (additionalSolutions_ == null) {
        additionalSolutions_ = new java.util.ArrayList<>();
      }
      for (CpSolverSolution v : values) {
        additionalSolutions_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code additional_solutions}. */
    public Builder clearAdditionalSolutions() {
      additionalSolutions_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code additional_solutions} by the content of a builder. */
    public Builder setAdditionalSolutions(int index, CpSolverSolution.Builder builderForValue) {
      return setAdditionalSolutions(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code additional_solutions}. */
    public Builder addAdditionalSolutions(CpSolverSolution.Builder builderForValue) {
      return addAdditionalSolutions(builderForValue.build());
    }

    /** Inserts a value into {@code additional_solutions}. */
    public Builder addAdditionalSolutions(int index, CpSolverSolution value) {
      if (additionalSolutions_ == null) {
        additionalSolutions_ = new java.util.ArrayList<>();
      }
      additionalSolutions_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code additional_solutions}. */
    public Builder removeAdditionalSolutions(int index) {
      if (additionalSolutions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      additionalSolutions_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code additional_solutions}; changes to it are reflected in this builder. */
    public CpSolverSolution.Builder getAdditionalSolutionsBuilder(int index) {
      if (additionalSolutions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      CpSolverSolution e = additionalSolutions_.get(index);
      if (e instanceof CpSolverSolution.Builder) {
        return (CpSolverSolution.Builder) e;
      }
      CpSolverSolution.Builder b = CpSolverSolution.newBuilder(e);
      additionalSolutions_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code additional_solutions} and returns its builder. */
    public CpSolverSolution.Builder addAdditionalSolutionsBuilder() {
      if (additionalSolutions_ == null) {
        additionalSolutions_ = new java.util.ArrayList<>();
      }
      CpSolverSolution.Builder b = CpSolverSolution.newBuilder();
      additionalSolutions_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code additional_solutions}. */
    public java.util.List<CpSolverSolution.Builder> getAdditionalSolutionsBuilderList() {
      java.util.List<CpSolverSolution.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getAdditionalSolutionsCount(); k++) {
        r.add(getAdditionalSolutionsBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code tightened_variables}. */
    public Builder setTightenedVariables(int index, IntegerVariableProto value) {
      if (tightenedVariables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      tightenedVariables_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code tightened_variables}. */
    public Builder addTightenedVariables(IntegerVariableProto value) {
      if (tightenedVariables_ == null) {
        tightenedVariables_ = new java.util.ArrayList<>();
      }
      tightenedVariables_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code tightened_variables}. */
    public Builder addAllTightenedVariables(Iterable<? extends IntegerVariableProto> values) {
      if (tightenedVariables_ == null) {
        tightenedVariables_ = new java.util.ArrayList<>();
      }
      for (IntegerVariableProto v : values) {
        tightenedVariables_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code tightened_variables}. */
    public Builder clearTightenedVariables() {
      tightenedVariables_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code tightened_variables} by the content of a builder. */
    public Builder setTightenedVariables(int index, IntegerVariableProto.Builder builderForValue) {
      return setTightenedVariables(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code tightened_variables}. */
    public Builder addTightenedVariables(IntegerVariableProto.Builder builderForValue) {
      return addTightenedVariables(builderForValue.build());
    }

    /** Inserts a value into {@code tightened_variables}. */
    public Builder addTightenedVariables(int index, IntegerVariableProto value) {
      if (tightenedVariables_ == null) {
        tightenedVariables_ = new java.util.ArrayList<>();
      }
      tightenedVariables_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code tightened_variables}. */
    public Builder removeTightenedVariables(int index) {
      if (tightenedVariables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      tightenedVariables_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code tightened_variables}; changes to it are reflected in this builder. */
    public IntegerVariableProto.Builder getTightenedVariablesBuilder(int index) {
      if (tightenedVariables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      IntegerVariableProto e = tightenedVariables_.get(index);
      if (e instanceof IntegerVariableProto.Builder) {
        return (IntegerVariableProto.Builder) e;
      }
      IntegerVariableProto.Builder b = IntegerVariableProto.newBuilder(e);
      tightenedVariables_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code tightened_variables} and returns its builder. */
    public IntegerVariableProto.Builder addTightenedVariablesBuilder() {
      if (tightenedVariables_ == null) {
        tightenedVariables_ = new java.util.ArrayList<>();
      }
      IntegerVariableProto.Builder b = IntegerVariableProto.newBuilder();
      tightenedVariables_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code tightened_variables}. */
    public java.util.List<IntegerVariableProto.Builder> getTightenedVariablesBuilderList() {
      java.util.List<IntegerVariableProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getTightenedVariablesCount(); k++) {
        r.add(getTightenedVariablesBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code sufficient_assumptions_for_infeasibility}. */
    public Builder setSufficientAssumptionsForInfeasibility(int index, int value) {
      if (sufficientAssumptionsForInfeasibility_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      sufficientAssumptionsForInfeasibility_.set(index, value);
      return this;
    }

    /** Appends a value to {@code sufficient_assumptions_for_infeasibility}. */
    public Builder addSufficientAssumptionsForInfeasibility(int value) {
      if (sufficientAssumptionsForInfeasibility_ == null) {
        sufficientAssumptionsForInfeasibility_ = new java.util.ArrayList<>();
      }
      sufficientAssumptionsForInfeasibility_.add(value);
      return this;
    }

    /** Appends all values to {@code sufficient_assumptions_for_infeasibility}. */
    public Builder addAllSufficientAssumptionsForInfeasibility(Iterable<? extends Integer> values) {
      if (sufficientAssumptionsForInfeasibility_ == null) {
        sufficientAssumptionsForInfeasibility_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        sufficientAssumptionsForInfeasibility_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code sufficient_assumptions_for_infeasibility}. */
    public Builder clearSufficientAssumptionsForInfeasibility() {
      sufficientAssumptionsForInfeasibility_ = null;
      return this;
    }

    /** Sets {@code integer_objective}. */
    public Builder setIntegerObjective(CpObjectiveProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      integerObjective_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code integer_objective} to the content of a builder. */
    public Builder setIntegerObjective(CpObjectiveProto.Builder builderForValue) {
      return setIntegerObjective(builderForValue.build());
    }

    /** Merges {@code value} into {@code integer_objective}. */
    public Builder mergeIntegerObjective(CpObjectiveProto value) {
      if (integerObjective_ != null) {
        getIntegerObjectiveBuilder().mergeFrom(value);
      } else {
        setIntegerObjective(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code integer_objective}. */
    public Builder clearIntegerObjective() {
      if (true) {
        integerObjective_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code integer_objective}, setting it if needed; changes to it are reflected in this builder. */
    public CpObjectiveProto.Builder getIntegerObjectiveBuilder() {
      if (integerObjective_ != null && integerObjective_ instanceof CpObjectiveProto.Builder) {
        return (CpObjectiveProto.Builder) integerObjective_;
      }
      CpObjectiveProto.Builder b = integerObjective_ != null ? CpObjectiveProto.newBuilder(integerObjective_) : CpObjectiveProto.newBuilder();
      setIntegerObjectiveRaw(b);
      return b;
    }

    private void setIntegerObjectiveRaw(CpObjectiveProto.Builder b) {
      integerObjective_ = b;
    }

    /** Sets {@code inner_objective_lower_bound}. */
    public Builder setInnerObjectiveLowerBound(long value) {
      innerObjectiveLowerBound_ = value;
      return this;
    }

    /** Resets {@code inner_objective_lower_bound} to its default value. */
    public Builder clearInnerObjectiveLowerBound() {
      innerObjectiveLowerBound_ = 0L;
      return this;
    }

    /** Sets {@code num_integers}. */
    public Builder setNumIntegers(long value) {
      numIntegers_ = value;
      return this;
    }

    /** Resets {@code num_integers} to its default value. */
    public Builder clearNumIntegers() {
      numIntegers_ = 0L;
      return this;
    }

    /** Sets {@code num_booleans}. */
    public Builder setNumBooleans(long value) {
      numBooleans_ = value;
      return this;
    }

    /** Resets {@code num_booleans} to its default value. */
    public Builder clearNumBooleans() {
      numBooleans_ = 0L;
      return this;
    }

    /** Sets {@code num_fixed_booleans}. */
    public Builder setNumFixedBooleans(long value) {
      numFixedBooleans_ = value;
      return this;
    }

    /** Resets {@code num_fixed_booleans} to its default value. */
    public Builder clearNumFixedBooleans() {
      numFixedBooleans_ = 0L;
      return this;
    }

    /** Sets {@code num_conflicts}. */
    public Builder setNumConflicts(long value) {
      numConflicts_ = value;
      return this;
    }

    /** Resets {@code num_conflicts} to its default value. */
    public Builder clearNumConflicts() {
      numConflicts_ = 0L;
      return this;
    }

    /** Sets {@code num_branches}. */
    public Builder setNumBranches(long value) {
      numBranches_ = value;
      return this;
    }

    /** Resets {@code num_branches} to its default value. */
    public Builder clearNumBranches() {
      numBranches_ = 0L;
      return this;
    }

    /** Sets {@code num_binary_propagations}. */
    public Builder setNumBinaryPropagations(long value) {
      numBinaryPropagations_ = value;
      return this;
    }

    /** Resets {@code num_binary_propagations} to its default value. */
    public Builder clearNumBinaryPropagations() {
      numBinaryPropagations_ = 0L;
      return this;
    }

    /** Sets {@code num_integer_propagations}. */
    public Builder setNumIntegerPropagations(long value) {
      numIntegerPropagations_ = value;
      return this;
    }

    /** Resets {@code num_integer_propagations} to its default value. */
    public Builder clearNumIntegerPropagations() {
      numIntegerPropagations_ = 0L;
      return this;
    }

    /** Sets {@code num_restarts}. */
    public Builder setNumRestarts(long value) {
      numRestarts_ = value;
      return this;
    }

    /** Resets {@code num_restarts} to its default value. */
    public Builder clearNumRestarts() {
      numRestarts_ = 0L;
      return this;
    }

    /** Sets {@code num_lp_iterations}. */
    public Builder setNumLpIterations(long value) {
      numLpIterations_ = value;
      return this;
    }

    /** Resets {@code num_lp_iterations} to its default value. */
    public Builder clearNumLpIterations() {
      numLpIterations_ = 0L;
      return this;
    }

    /** Sets {@code wall_time}. */
    public Builder setWallTime(double value) {
      wallTime_ = value;
      return this;
    }

    /** Resets {@code wall_time} to its default value. */
    public Builder clearWallTime() {
      wallTime_ = 0.0;
      return this;
    }

    /** Sets {@code user_time}. */
    public Builder setUserTime(double value) {
      userTime_ = value;
      return this;
    }

    /** Resets {@code user_time} to its default value. */
    public Builder clearUserTime() {
      userTime_ = 0.0;
      return this;
    }

    /** Sets {@code deterministic_time}. */
    public Builder setDeterministicTime(double value) {
      deterministicTime_ = value;
      return this;
    }

    /** Resets {@code deterministic_time} to its default value. */
    public Builder clearDeterministicTime() {
      deterministicTime_ = 0.0;
      return this;
    }

    /** Sets {@code gap_integral}. */
    public Builder setGapIntegral(double value) {
      gapIntegral_ = value;
      return this;
    }

    /** Resets {@code gap_integral} to its default value. */
    public Builder clearGapIntegral() {
      gapIntegral_ = 0.0;
      return this;
    }

    /** Sets {@code solution_info}. */
    public Builder setSolutionInfo(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      solutionInfo_ = value;
      return this;
    }

    /** Resets {@code solution_info} to its default value. */
    public Builder clearSolutionInfo() {
      solutionInfo_ = "";
      return this;
    }

    /** Sets {@code solve_log}. */
    public Builder setSolveLog(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      solveLog_ = value;
      return this;
    }

    /** Resets {@code solve_log} to its default value. */
    public Builder clearSolveLog() {
      solveLog_ = "";
      return this;
    }

  }
}
