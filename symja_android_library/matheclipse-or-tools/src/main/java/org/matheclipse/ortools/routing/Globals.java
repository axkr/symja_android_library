package org.matheclipse.ortools.routing;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.constraintsolver.ConstraintSolverParameters;
import org.matheclipse.ortools.constraintsolver.Solver;
import org.matheclipse.ortools.util.Duration;

/**
 * The global functions of the OR-Tools routing wrapper (the SWIG module class
 * <code>com.google.ortools.routing.Globals</code> of OR-Tools 10.0, the functions of
 * <code>ortools/routing/parameters.h</code>), reimplemented for this module: the default routing
 * parameters and their validation.
 *
 * <p>
 * Iterated local search is not modelled by this module: <code>RoutingSearchParameters</code> has
 * no <code>iterated_local_search_parameters</code>, so neither the defaults nor the validation
 * contain them (in OR-Tools 10.0 the defaults no longer set them either, and the iterated local
 * search is used exactly when they are present).
 *
 * <p>
 * Deviation from the OR-Tools 10.0 Java API: {@link #findErrorsInRoutingSearchParameters}
 * returns a <code>List&lt;String&gt;</code>. The OR-Tools wrapper returns the C++
 * <code>std::vector&lt;std::string&gt;</code> as an opaque SWIG pointer type, which Java code can't
 * read, so a program written against OR-Tools uses {@link #findErrorInRoutingSearchParameters},
 * which both have with the same signature.
 */
public class Globals {

  /** Not meant to be instantiated; the class only holds static methods. */
  protected Globals() {}

  /** The default parameters of a routing model. */
  public static RoutingModelParameters defaultRoutingModelParameters() {
    ConstraintSolverParameters solverParameters = Solver.defaultSolverParameters().toBuilder()
        .setCompressTrail(ConstraintSolverParameters.TrailCompression.COMPRESS_WITH_ZLIB)
        .setSkipLocallyOptimalPaths(true).build();
    return RoutingModelParameters.newBuilder().setSolverParameters(solverParameters)
        .setReduceVehicleCostModel(true).build();
  }

  /**
   * The default parameters of a routing search, the scalar fields as set by OR-Tools 10.0:
   * automatic first solution strategy and metaheuristic, guided local search lambda 0.1, no time
   * or solution limit, LNS time limit 0.1 s.
   */
  public static RoutingSearchParameters defaultRoutingSearchParameters() {
    return defaultBuilder().build();
  }

  /**
   * The default parameters of a secondary routing search: the default parameters with the
   * <code>GREEDY_DESCENT</code> metaheuristic (the local search operator selection of OR-Tools is
   * not modelled by this module).
   */
  public static RoutingSearchParameters defaultSecondaryRoutingSearchParameters() {
    return defaultBuilder()
        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GREEDY_DESCENT).build();
  }

  private static RoutingSearchParameters.Builder defaultBuilder() {
    return RoutingSearchParameters.newBuilder()
        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.AUTOMATIC)
        .setUseUnfilteredFirstSolutionStrategy(false).setLsOperatorNeighborsRatio(1)
        .setLsOperatorMinNeighbors(1).setUseMultiArmedBanditConcatenateOperators(false)
        .setMultiArmedBanditCompoundOperatorMemoryCoefficient(0.04)
        .setMultiArmedBanditCompoundOperatorExplorationCoefficient(1e12)
        .setMaxSwapActiveChainSize(10).setRelocateExpensiveChainNumArcsToConsider(4)
        .setHeuristicExpensiveChainLnsNumArcsToConsider(4).setHeuristicCloseNodesLnsNumNodes(5)
        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.AUTOMATIC)
        .setNumMaxLocalOptimaBeforeMetaheuristicSwitch(200)
        .setGuidedLocalSearchLambdaCoefficient(0.1)
        .setGuidedLocalSearchResetPenaltiesOnNewBestSolution(false).setUseDepthFirstSearch(false)
        .setFallbackToCpSatSizeThreshold(20).setOptimizationStep(0.0)
        .setNumberOfSolutionsToCollect(1).setSolutionLimit(Long.MAX_VALUE)
        .setLnsTimeLimit(Duration.newBuilder().setNanos(100000000).build())
        .setSecondaryLsTimeLimitRatio(0).setUseFullPropagation(false).setLogSearch(false)
        .setLogCostScalingFactor(1.0).setLogCostOffset(0.0);
  }

  /** The first error of {@link #findErrorsInRoutingSearchParameters}, or the empty string. */
  public static String findErrorInRoutingSearchParameters(
      RoutingSearchParameters searchParameters) {
    List<String> errors = findErrorsInRoutingSearchParameters(searchParameters);
    return errors.isEmpty() ? "" : errors.get(0);
  }

  /**
   * The descriptions of all invalid fields of the parameters, in the order and with the messages
   * of OR-Tools 10.0, for the fields modelled by this module. The return type deviates from the
   * OR-Tools Java wrapper, which exposes the <code>std::vector&lt;std::string&gt;</code> only as an
   * opaque pointer (see the class comment).
   */
  public static List<String> findErrorsInRoutingSearchParameters(
      RoutingSearchParameters p) {
    List<String> errors = new ArrayList<>();
    double neighborsRatio = p.getLsOperatorNeighborsRatio();
    if (Double.isNaN(neighborsRatio) || neighborsRatio <= 0 || neighborsRatio > 1) {
      errors.add("Invalid ls_operator_neighbors_ratio: " + format(neighborsRatio));
    }
    if (p.getLsOperatorMinNeighbors() < 1) {
      errors.add("Invalid ls_operator_min_neighbors: " + p.getLsOperatorMinNeighbors()
          + ". Must be greater or equal to 1.");
    }
    int numArcs = p.getRelocateExpensiveChainNumArcsToConsider();
    if (numArcs < 2 || numArcs > 1e6) {
      errors.add("Invalid relocate_expensive_chain_num_arcs_to_consider: " + numArcs
          + ". Must be between 2 and 10^6 (included).");
    }
    numArcs = p.getHeuristicExpensiveChainLnsNumArcsToConsider();
    if (numArcs < 2 || numArcs > 1e6) {
      errors.add("Invalid heuristic_expensive_chain_lns_num_arcs_to_consider: " + numArcs
          + ". Must be between 2 and 10^6 (included).");
    }
    int numNodes = p.getHeuristicCloseNodesLnsNumNodes();
    if (numNodes < 0 || numNodes > 1e4) {
      errors.add("Invalid heuristic_close_nodes_lns_num_nodes: " + numNodes
          + ". Must be between 0 and 10000 (included).");
    }
    double lambda = p.getGuidedLocalSearchLambdaCoefficient();
    if (Double.isNaN(lambda) || lambda < 0 || Double.isInfinite(lambda)) {
      errors.add("Invalid guided_local_search_lambda_coefficient: " + format(lambda));
    }
    double step = p.getOptimizationStep();
    if (Double.isNaN(step) || step < 0.0) {
      errors.add("Invalid optimization_step: " + format(step));
    }
    if (p.getNumberOfSolutionsToCollect() < 1) {
      errors.add("Invalid number_of_solutions_to_collect: " + p.getNumberOfSolutionsToCollect());
    }
    if (p.getSolutionLimit() < 1) {
      errors.add("Invalid solution_limit: " + p.getSolutionLimit());
    }
    if (!isValidNonNegativeDuration(p.getTimeLimit())) {
      errors.add("Invalid time_limit: " + shortDebugString(p.getTimeLimit()));
    }
    if (!isValidNonNegativeDuration(p.getLnsTimeLimit())) {
      errors.add("Invalid lns_time_limit: " + shortDebugString(p.getLnsTimeLimit()));
    }
    double ratio = p.getSecondaryLsTimeLimitRatio();
    if (Double.isNaN(ratio) || ratio < 0 || ratio >= 1) {
      errors.add("Invalid secondary_ls_time_limit_ratio: " + format(ratio));
    }
    if (p.getFirstSolutionStrategy() == FirstSolutionStrategy.Value.UNRECOGNIZED) {
      errors.add("Invalid first_solution_strategy: " + p.getFirstSolutionStrategy());
    }
    if (p.getLocalSearchMetaheuristic() == LocalSearchMetaheuristic.Value.UNRECOGNIZED) {
      errors.add("Invalid metaheuristic: " + p.getLocalSearchMetaheuristic());
    }
    double scalingFactor = p.getLogCostScalingFactor();
    if (scalingFactor == 0 || Double.isNaN(scalingFactor) || Double.isInfinite(scalingFactor)) {
      errors.add("Invalid value for log_cost_scaling_factor: " + format(scalingFactor));
    }
    double offset = p.getLogCostOffset();
    if (Double.isNaN(offset) || Double.isInfinite(offset)) {
      errors.add("Invalid value for log_cost_offset: " + format(offset));
    }
    double memoryCoefficient = p.getMultiArmedBanditCompoundOperatorMemoryCoefficient();
    if (Double.isNaN(memoryCoefficient) || memoryCoefficient < 0 || memoryCoefficient > 1) {
      errors.add("Invalid value for multi_armed_bandit_compound_operator_memory_coefficient: "
          + format(memoryCoefficient));
    }
    double explorationCoefficient = p.getMultiArmedBanditCompoundOperatorExplorationCoefficient();
    if (Double.isNaN(explorationCoefficient) || explorationCoefficient < 0) {
      errors.add("Invalid value for multi_armed_bandit_compound_operator_exploration_coefficient: "
          + format(explorationCoefficient));
    }
    return errors;
  }

  /** A double as printed by <code>absl::StrCat</code> (at most 6 significant digits). */
  private static String format(double value) {
    if (Double.isNaN(value)) {
      return "nan";
    }
    if (Double.isInfinite(value)) {
      return value > 0 ? "inf" : "-inf";
    }
    if (value == Math.rint(value) && Math.abs(value) < 1e6) {
      return Long.toString((long) value);
    }
    String s = String.format(java.util.Locale.ROOT, "%.6g", value);
    if (s.contains("e")) {
      String mantissa = s.substring(0, s.indexOf('e'));
      String exponent = s.substring(s.indexOf('e'));
      return stripZeros(mantissa) + exponent;
    }
    return stripZeros(s);
  }

  private static String stripZeros(String s) {
    if (s.indexOf('.') < 0) {
      return s;
    }
    int end = s.length();
    while (s.charAt(end - 1) == '0') {
      end--;
    }
    if (s.charAt(end - 1) == '.') {
      end--;
    }
    return s.substring(0, end);
  }

  /** A duration as printed by <code>ProtobufShortDebugString</code>. */
  private static String shortDebugString(Duration d) {
    StringBuilder buf = new StringBuilder();
    if (d.getSeconds() != 0) {
      buf.append("seconds: ").append(d.getSeconds());
    }
    if (d.getNanos() != 0) {
      if (buf.length() > 0) {
        buf.append(' ');
      }
      buf.append("nanos: ").append(d.getNanos());
    }
    return buf.toString();
  }

  /**
   * Whether the duration is valid (seconds and nanos within the protobuf ranges and of the same
   * sign) and not negative.
   */
  private static boolean isValidNonNegativeDuration(Duration d) {
    long seconds = d.getSeconds();
    int nanos = d.getNanos();
    if (seconds < -315576000000L || seconds > 315576000000L || nanos <= -1_000_000_000
        || nanos >= 1_000_000_000 || (seconds < 0 && nanos > 0) || (seconds > 0 && nanos < 0)) {
      return false;
    }
    return seconds > 0 || (seconds == 0 && nanos >= 0);
  }
}
