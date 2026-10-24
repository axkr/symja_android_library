package org.matheclipse.ortools.routing;

import org.matheclipse.ortools.util.Duration;

/**
 * The parameters of a routing search, shaped like the protocol buffer message
 * <code>operations_research.routing.RoutingSearchParameters</code> of the OR-Tools Java wrapper:
 * immutable, created by {@link #newBuilder()} (all fields zero, as protobuf does) or modified by
 * {@link #toBuilder()}; <code>Globals.defaultRoutingSearchParameters()</code> returns the OR-Tools
 * defaults.
 *
 * <p>
 * This class reimplements the scalar fields of the message for the simplified routing engine of
 * this module. The engine uses <code>first_solution_strategy</code>,
 * <code>local_search_metaheuristic</code>, <code>guided_local_search_lambda_coefficient</code>,
 * <code>time_limit</code>, <code>solution_limit</code> and <code>log_search</code>; the other
 * fields are stored and validated but have no effect. The nested messages (local search
 * operators, insertion parameters, SAT parameters, ...) are not available.
 */
public final class RoutingSearchParameters {

  private final FirstSolutionStrategy.Value firstSolutionStrategy;

  private final boolean useUnfilteredFirstSolutionStrategy;

  private final boolean christofidesUseMinimumMatching;

  private final int firstSolutionOptimizationPeriod;

  private final double lsOperatorNeighborsRatio;

  private final int lsOperatorMinNeighbors;

  private final boolean useMultiArmedBanditConcatenateOperators;

  private final double multiArmedBanditCompoundOperatorMemoryCoefficient;

  private final double multiArmedBanditCompoundOperatorExplorationCoefficient;

  private final int maxSwapActiveChainSize;

  private final int relocateExpensiveChainNumArcsToConsider;

  private final int heuristicExpensiveChainLnsNumArcsToConsider;

  private final int heuristicCloseNodesLnsNumNodes;

  private final LocalSearchMetaheuristic.Value localSearchMetaheuristic;

  private final int numMaxLocalOptimaBeforeMetaheuristicSwitch;

  private final double guidedLocalSearchLambdaCoefficient;

  private final boolean guidedLocalSearchResetPenaltiesOnNewBestSolution;

  private final boolean guidedLocalSearchPenalizeWithVehicleClasses;

  private final boolean useGuidedLocalSearchPenaltiesInLocalSearchOperators;

  private final boolean useDepthFirstSearch;

  private final int fallbackToCpSatSizeThreshold;

  private final double optimizationStep;

  private final int numberOfSolutionsToCollect;

  private final long solutionLimit;

  private final Duration timeLimit;

  private final Duration lnsTimeLimit;

  private final double secondaryLsTimeLimitRatio;

  private final boolean useFullPropagation;

  private final boolean logSearch;

  private final double logCostScalingFactor;

  private final double logCostOffset;

  private final String logTag;


  private RoutingSearchParameters(Builder b) {
    firstSolutionStrategy = b.firstSolutionStrategy;
    useUnfilteredFirstSolutionStrategy = b.useUnfilteredFirstSolutionStrategy;
    christofidesUseMinimumMatching = b.christofidesUseMinimumMatching;
    firstSolutionOptimizationPeriod = b.firstSolutionOptimizationPeriod;
    lsOperatorNeighborsRatio = b.lsOperatorNeighborsRatio;
    lsOperatorMinNeighbors = b.lsOperatorMinNeighbors;
    useMultiArmedBanditConcatenateOperators = b.useMultiArmedBanditConcatenateOperators;
    multiArmedBanditCompoundOperatorMemoryCoefficient =
        b.multiArmedBanditCompoundOperatorMemoryCoefficient;
    multiArmedBanditCompoundOperatorExplorationCoefficient =
        b.multiArmedBanditCompoundOperatorExplorationCoefficient;
    maxSwapActiveChainSize = b.maxSwapActiveChainSize;
    relocateExpensiveChainNumArcsToConsider = b.relocateExpensiveChainNumArcsToConsider;
    heuristicExpensiveChainLnsNumArcsToConsider = b.heuristicExpensiveChainLnsNumArcsToConsider;
    heuristicCloseNodesLnsNumNodes = b.heuristicCloseNodesLnsNumNodes;
    localSearchMetaheuristic = b.localSearchMetaheuristic;
    numMaxLocalOptimaBeforeMetaheuristicSwitch = b.numMaxLocalOptimaBeforeMetaheuristicSwitch;
    guidedLocalSearchLambdaCoefficient = b.guidedLocalSearchLambdaCoefficient;
    guidedLocalSearchResetPenaltiesOnNewBestSolution =
        b.guidedLocalSearchResetPenaltiesOnNewBestSolution;
    guidedLocalSearchPenalizeWithVehicleClasses = b.guidedLocalSearchPenalizeWithVehicleClasses;
    useGuidedLocalSearchPenaltiesInLocalSearchOperators =
        b.useGuidedLocalSearchPenaltiesInLocalSearchOperators;
    useDepthFirstSearch = b.useDepthFirstSearch;
    fallbackToCpSatSizeThreshold = b.fallbackToCpSatSizeThreshold;
    optimizationStep = b.optimizationStep;
    numberOfSolutionsToCollect = b.numberOfSolutionsToCollect;
    solutionLimit = b.solutionLimit;
    timeLimit = b.timeLimit;
    lnsTimeLimit = b.lnsTimeLimit;
    secondaryLsTimeLimitRatio = b.secondaryLsTimeLimitRatio;
    useFullPropagation = b.useFullPropagation;
    logSearch = b.logSearch;
    logCostScalingFactor = b.logCostScalingFactor;
    logCostOffset = b.logCostOffset;
    logTag = b.logTag;
  }

  /** The message with all fields at their protobuf defaults (zero). */
  public static RoutingSearchParameters getDefaultInstance() {
    return new Builder().build();
  }

  /** A builder with all fields at their protobuf defaults (zero). */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** A builder initialized with <code>prototype</code>. */
  public static Builder newBuilder(RoutingSearchParameters prototype) {
    return new Builder().copyAll(prototype);
  }

  /** A builder initialized with this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** The first solution strategy. */
  public FirstSolutionStrategy.Value getFirstSolutionStrategy() {
    return firstSolutionStrategy;
  }

  /** The number of the first solution strategy. */
  public int getFirstSolutionStrategyValue() {
    return firstSolutionStrategy == FirstSolutionStrategy.Value.UNRECOGNIZED ? -1
        : firstSolutionStrategy.getNumber();
  }

  /** Whether the first solution heuristics ignore the filters (no effect). */
  public boolean getUseUnfilteredFirstSolutionStrategy() {
    return useUnfilteredFirstSolutionStrategy;
  }

  /** Christofides option (no effect). */
  public boolean getChristofidesUseMinimumMatching() {
    return christofidesUseMinimumMatching;
  }

  /** First solution optimization period (no effect). */
  public int getFirstSolutionOptimizationPeriod() {
    return firstSolutionOptimizationPeriod;
  }

  /** Neighbors ratio of the local search operators (no effect). */
  public double getLsOperatorNeighborsRatio() {
    return lsOperatorNeighborsRatio;
  }

  /** Minimal neighbors of the local search operators (no effect). */
  public int getLsOperatorMinNeighbors() {
    return lsOperatorMinNeighbors;
  }

  /** Multi armed bandit option (no effect). */
  public boolean getUseMultiArmedBanditConcatenateOperators() {
    return useMultiArmedBanditConcatenateOperators;
  }

  /** Multi armed bandit option (no effect). */
  public double getMultiArmedBanditCompoundOperatorMemoryCoefficient() {
    return multiArmedBanditCompoundOperatorMemoryCoefficient;
  }

  /** Multi armed bandit option (no effect). */
  public double getMultiArmedBanditCompoundOperatorExplorationCoefficient() {
    return multiArmedBanditCompoundOperatorExplorationCoefficient;
  }

  /** Maximal swap active chain size (no effect). */
  public int getMaxSwapActiveChainSize() {
    return maxSwapActiveChainSize;
  }

  /** Relocate expensive chain option (no effect). */
  public int getRelocateExpensiveChainNumArcsToConsider() {
    return relocateExpensiveChainNumArcsToConsider;
  }

  /** Expensive chain LNS option (no effect). */
  public int getHeuristicExpensiveChainLnsNumArcsToConsider() {
    return heuristicExpensiveChainLnsNumArcsToConsider;
  }

  /** Close nodes LNS option (no effect). */
  public int getHeuristicCloseNodesLnsNumNodes() {
    return heuristicCloseNodesLnsNumNodes;
  }

  /** The local search metaheuristic. */
  public LocalSearchMetaheuristic.Value getLocalSearchMetaheuristic() {
    return localSearchMetaheuristic;
  }

  /** The number of the local search metaheuristic. */
  public int getLocalSearchMetaheuristicValue() {
    return localSearchMetaheuristic == LocalSearchMetaheuristic.Value.UNRECOGNIZED ? -1
        : localSearchMetaheuristic.getNumber();
  }

  /** Metaheuristic switch option (no effect). */
  public int getNumMaxLocalOptimaBeforeMetaheuristicSwitch() {
    return numMaxLocalOptimaBeforeMetaheuristicSwitch;
  }

  /** The lambda coefficient of the guided local search. */
  public double getGuidedLocalSearchLambdaCoefficient() {
    return guidedLocalSearchLambdaCoefficient;
  }

  /** Guided local search option (no effect). */
  public boolean getGuidedLocalSearchResetPenaltiesOnNewBestSolution() {
    return guidedLocalSearchResetPenaltiesOnNewBestSolution;
  }

  /** Guided local search option (no effect). */
  public boolean getGuidedLocalSearchPenalizeWithVehicleClasses() {
    return guidedLocalSearchPenalizeWithVehicleClasses;
  }

  /** Guided local search option (no effect). */
  public boolean getUseGuidedLocalSearchPenaltiesInLocalSearchOperators() {
    return useGuidedLocalSearchPenaltiesInLocalSearchOperators;
  }

  /** Depth first search option (no effect). */
  public boolean getUseDepthFirstSearch() {
    return useDepthFirstSearch;
  }

  /** CP-SAT fallback threshold (no effect). */
  public int getFallbackToCpSatSizeThreshold() {
    return fallbackToCpSatSizeThreshold;
  }

  /** The optimization step (no effect). */
  public double getOptimizationStep() {
    return optimizationStep;
  }

  /** The number of solutions to collect (no effect). */
  public int getNumberOfSolutionsToCollect() {
    return numberOfSolutionsToCollect;
  }

  /** The maximal number of solutions of the search. */
  public long getSolutionLimit() {
    return solutionLimit;
  }

  /** Whether a time limit is set. */
  public boolean hasTimeLimit() {
    return timeLimit != null;
  }

  /** The time limit (zero duration if not set). */
  public Duration getTimeLimit() {
    return timeLimit == null ? Duration.getDefaultInstance() : timeLimit;
  }

  /** Whether an LNS time limit is set. */
  public boolean hasLnsTimeLimit() {
    return lnsTimeLimit != null;
  }

  /** The LNS time limit (no effect). */
  public Duration getLnsTimeLimit() {
    return lnsTimeLimit == null ? Duration.getDefaultInstance() : lnsTimeLimit;
  }

  /** Secondary local search time ratio (no effect). */
  public double getSecondaryLsTimeLimitRatio() {
    return secondaryLsTimeLimitRatio;
  }

  /** Full propagation option (no effect). */
  public boolean getUseFullPropagation() {
    return useFullPropagation;
  }

  /** Whether the search progress is logged. */
  public boolean getLogSearch() {
    return logSearch;
  }

  /** Scaling of the logged costs. */
  public double getLogCostScalingFactor() {
    return logCostScalingFactor;
  }

  /** Offset of the logged costs. */
  public double getLogCostOffset() {
    return logCostOffset;
  }

  /** A tag of the log lines. */
  public String getLogTag() {
    return logTag;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("first_solution_strategy: ").append(firstSolutionStrategy).append('\n');
    buf.append("local_search_metaheuristic: ").append(localSearchMetaheuristic).append('\n');
    buf.append("guided_local_search_lambda_coefficient: ")
        .append(guidedLocalSearchLambdaCoefficient).append('\n');
    buf.append("solution_limit: ").append(solutionLimit).append('\n');
    if (timeLimit != null) {
      buf.append("time_limit { ").append(timeLimit).append(" }\n");
    }
    if (lnsTimeLimit != null) {
      buf.append("lns_time_limit { ").append(lnsTimeLimit).append(" }\n");
    }
    buf.append("log_search: ").append(logSearch).append('\n');
    return buf.toString();
  }

  /** Builder of {@link RoutingSearchParameters}. */
  public static final class Builder {
    private FirstSolutionStrategy.Value firstSolutionStrategy = FirstSolutionStrategy.Value.UNSET;

    private boolean useUnfilteredFirstSolutionStrategy;

    private boolean christofidesUseMinimumMatching;

    private int firstSolutionOptimizationPeriod;

    private double lsOperatorNeighborsRatio;

    private int lsOperatorMinNeighbors;

    private boolean useMultiArmedBanditConcatenateOperators;

    private double multiArmedBanditCompoundOperatorMemoryCoefficient;

    private double multiArmedBanditCompoundOperatorExplorationCoefficient;

    private int maxSwapActiveChainSize;

    private int relocateExpensiveChainNumArcsToConsider;

    private int heuristicExpensiveChainLnsNumArcsToConsider;

    private int heuristicCloseNodesLnsNumNodes;

    private LocalSearchMetaheuristic.Value localSearchMetaheuristic =
        LocalSearchMetaheuristic.Value.UNSET;

    private int numMaxLocalOptimaBeforeMetaheuristicSwitch;

    private double guidedLocalSearchLambdaCoefficient;

    private boolean guidedLocalSearchResetPenaltiesOnNewBestSolution;

    private boolean guidedLocalSearchPenalizeWithVehicleClasses;

    private boolean useGuidedLocalSearchPenaltiesInLocalSearchOperators;

    private boolean useDepthFirstSearch;

    private int fallbackToCpSatSizeThreshold;

    private double optimizationStep;

    private int numberOfSolutionsToCollect;

    private long solutionLimit;

    private Duration timeLimit;

    private Duration lnsTimeLimit;

    private double secondaryLsTimeLimitRatio;

    private boolean useFullPropagation;

    private boolean logSearch;

    private double logCostScalingFactor;

    private double logCostOffset;

    private String logTag = "";


    private Builder() {}

    private Builder copyAll(RoutingSearchParameters o) {
      firstSolutionStrategy = o.firstSolutionStrategy;
      useUnfilteredFirstSolutionStrategy = o.useUnfilteredFirstSolutionStrategy;
      christofidesUseMinimumMatching = o.christofidesUseMinimumMatching;
      firstSolutionOptimizationPeriod = o.firstSolutionOptimizationPeriod;
      lsOperatorNeighborsRatio = o.lsOperatorNeighborsRatio;
      lsOperatorMinNeighbors = o.lsOperatorMinNeighbors;
      useMultiArmedBanditConcatenateOperators = o.useMultiArmedBanditConcatenateOperators;
      multiArmedBanditCompoundOperatorMemoryCoefficient =
          o.multiArmedBanditCompoundOperatorMemoryCoefficient;
      multiArmedBanditCompoundOperatorExplorationCoefficient =
          o.multiArmedBanditCompoundOperatorExplorationCoefficient;
      maxSwapActiveChainSize = o.maxSwapActiveChainSize;
      relocateExpensiveChainNumArcsToConsider = o.relocateExpensiveChainNumArcsToConsider;
      heuristicExpensiveChainLnsNumArcsToConsider = o.heuristicExpensiveChainLnsNumArcsToConsider;
      heuristicCloseNodesLnsNumNodes = o.heuristicCloseNodesLnsNumNodes;
      localSearchMetaheuristic = o.localSearchMetaheuristic;
      numMaxLocalOptimaBeforeMetaheuristicSwitch = o.numMaxLocalOptimaBeforeMetaheuristicSwitch;
      guidedLocalSearchLambdaCoefficient = o.guidedLocalSearchLambdaCoefficient;
      guidedLocalSearchResetPenaltiesOnNewBestSolution =
          o.guidedLocalSearchResetPenaltiesOnNewBestSolution;
      guidedLocalSearchPenalizeWithVehicleClasses = o.guidedLocalSearchPenalizeWithVehicleClasses;
      useGuidedLocalSearchPenaltiesInLocalSearchOperators =
          o.useGuidedLocalSearchPenaltiesInLocalSearchOperators;
      useDepthFirstSearch = o.useDepthFirstSearch;
      fallbackToCpSatSizeThreshold = o.fallbackToCpSatSizeThreshold;
      optimizationStep = o.optimizationStep;
      numberOfSolutionsToCollect = o.numberOfSolutionsToCollect;
      solutionLimit = o.solutionLimit;
      timeLimit = o.timeLimit;
      lnsTimeLimit = o.lnsTimeLimit;
      secondaryLsTimeLimitRatio = o.secondaryLsTimeLimitRatio;
      useFullPropagation = o.useFullPropagation;
      logSearch = o.logSearch;
      logCostScalingFactor = o.logCostScalingFactor;
      logCostOffset = o.logCostOffset;
      logTag = o.logTag;
      return this;
    }

    /**
     * Merges <code>o</code> into this builder with the protobuf semantics: the fields of
     * <code>o</code> which are not at their default (zero) value replace the fields of the
     * builder.
     */
    public Builder mergeFrom(RoutingSearchParameters o) {
      if (o.firstSolutionStrategy != FirstSolutionStrategy.Value.UNSET) {
        firstSolutionStrategy = o.firstSolutionStrategy;
      }
      useUnfilteredFirstSolutionStrategy |= o.useUnfilteredFirstSolutionStrategy;
      christofidesUseMinimumMatching |= o.christofidesUseMinimumMatching;
      if (o.firstSolutionOptimizationPeriod != 0) {
        firstSolutionOptimizationPeriod = o.firstSolutionOptimizationPeriod;
      }
      if (o.lsOperatorNeighborsRatio != 0) {
        lsOperatorNeighborsRatio = o.lsOperatorNeighborsRatio;
      }
      if (o.lsOperatorMinNeighbors != 0) {
        lsOperatorMinNeighbors = o.lsOperatorMinNeighbors;
      }
      useMultiArmedBanditConcatenateOperators |= o.useMultiArmedBanditConcatenateOperators;
      if (o.multiArmedBanditCompoundOperatorMemoryCoefficient != 0) {
        multiArmedBanditCompoundOperatorMemoryCoefficient =
            o.multiArmedBanditCompoundOperatorMemoryCoefficient;
      }
      if (o.multiArmedBanditCompoundOperatorExplorationCoefficient != 0) {
        multiArmedBanditCompoundOperatorExplorationCoefficient =
            o.multiArmedBanditCompoundOperatorExplorationCoefficient;
      }
      if (o.maxSwapActiveChainSize != 0) {
        maxSwapActiveChainSize = o.maxSwapActiveChainSize;
      }
      if (o.relocateExpensiveChainNumArcsToConsider != 0) {
        relocateExpensiveChainNumArcsToConsider = o.relocateExpensiveChainNumArcsToConsider;
      }
      if (o.heuristicExpensiveChainLnsNumArcsToConsider != 0) {
        heuristicExpensiveChainLnsNumArcsToConsider =
            o.heuristicExpensiveChainLnsNumArcsToConsider;
      }
      if (o.heuristicCloseNodesLnsNumNodes != 0) {
        heuristicCloseNodesLnsNumNodes = o.heuristicCloseNodesLnsNumNodes;
      }
      if (o.localSearchMetaheuristic != LocalSearchMetaheuristic.Value.UNSET) {
        localSearchMetaheuristic = o.localSearchMetaheuristic;
      }
      if (o.numMaxLocalOptimaBeforeMetaheuristicSwitch != 0) {
        numMaxLocalOptimaBeforeMetaheuristicSwitch = o.numMaxLocalOptimaBeforeMetaheuristicSwitch;
      }
      if (o.guidedLocalSearchLambdaCoefficient != 0) {
        guidedLocalSearchLambdaCoefficient = o.guidedLocalSearchLambdaCoefficient;
      }
      guidedLocalSearchResetPenaltiesOnNewBestSolution |=
          o.guidedLocalSearchResetPenaltiesOnNewBestSolution;
      guidedLocalSearchPenalizeWithVehicleClasses |= o.guidedLocalSearchPenalizeWithVehicleClasses;
      useGuidedLocalSearchPenaltiesInLocalSearchOperators |=
          o.useGuidedLocalSearchPenaltiesInLocalSearchOperators;
      useDepthFirstSearch |= o.useDepthFirstSearch;
      if (o.fallbackToCpSatSizeThreshold != 0) {
        fallbackToCpSatSizeThreshold = o.fallbackToCpSatSizeThreshold;
      }
      if (o.optimizationStep != 0) {
        optimizationStep = o.optimizationStep;
      }
      if (o.numberOfSolutionsToCollect != 0) {
        numberOfSolutionsToCollect = o.numberOfSolutionsToCollect;
      }
      if (o.solutionLimit != 0) {
        solutionLimit = o.solutionLimit;
      }
      if (o.timeLimit != null) {
        timeLimit = timeLimit == null ? o.timeLimit : timeLimit.toBuilder().mergeFrom(o.timeLimit)
            .build();
      }
      if (o.lnsTimeLimit != null) {
        lnsTimeLimit = lnsTimeLimit == null ? o.lnsTimeLimit
            : lnsTimeLimit.toBuilder().mergeFrom(o.lnsTimeLimit).build();
      }
      if (o.secondaryLsTimeLimitRatio != 0) {
        secondaryLsTimeLimitRatio = o.secondaryLsTimeLimitRatio;
      }
      useFullPropagation |= o.useFullPropagation;
      logSearch |= o.logSearch;
      if (o.logCostScalingFactor != 0) {
        logCostScalingFactor = o.logCostScalingFactor;
      }
      if (o.logCostOffset != 0) {
        logCostOffset = o.logCostOffset;
      }
      if (!o.logTag.isEmpty()) {
        logTag = o.logTag;
      }
      return this;
    }

    /** Sets the first solution strategy. */
    public Builder setFirstSolutionStrategy(FirstSolutionStrategy.Value value) {
      firstSolutionStrategy = value;
      return this;
    }

    /** Sets the first solution strategy by number. */
    public Builder setFirstSolutionStrategyValue(int value) {
      FirstSolutionStrategy.Value v = FirstSolutionStrategy.Value.forNumber(value);
      firstSolutionStrategy = v == null ? FirstSolutionStrategy.Value.UNRECOGNIZED : v;
      return this;
    }

    /** The first solution strategy set so far. */
    public FirstSolutionStrategy.Value getFirstSolutionStrategy() {
      return firstSolutionStrategy;
    }

    /** Sets the unfiltered first solution option. */
    public Builder setUseUnfilteredFirstSolutionStrategy(boolean value) {
      useUnfilteredFirstSolutionStrategy = value;
      return this;
    }

    /** Sets the Christofides option. */
    public Builder setChristofidesUseMinimumMatching(boolean value) {
      christofidesUseMinimumMatching = value;
      return this;
    }

    /** Sets the first solution optimization period. */
    public Builder setFirstSolutionOptimizationPeriod(int value) {
      firstSolutionOptimizationPeriod = value;
      return this;
    }

    /** Sets the neighbors ratio of the operators. */
    public Builder setLsOperatorNeighborsRatio(double value) {
      lsOperatorNeighborsRatio = value;
      return this;
    }

    /** Sets the minimal neighbors of the operators. */
    public Builder setLsOperatorMinNeighbors(int value) {
      lsOperatorMinNeighbors = value;
      return this;
    }

    /** Sets the multi armed bandit option. */
    public Builder setUseMultiArmedBanditConcatenateOperators(boolean value) {
      useMultiArmedBanditConcatenateOperators = value;
      return this;
    }

    /** Sets the multi armed bandit memory coefficient. */
    public Builder setMultiArmedBanditCompoundOperatorMemoryCoefficient(double value) {
      multiArmedBanditCompoundOperatorMemoryCoefficient = value;
      return this;
    }

    /** Sets the multi armed bandit exploration coefficient. */
    public Builder setMultiArmedBanditCompoundOperatorExplorationCoefficient(double value) {
      multiArmedBanditCompoundOperatorExplorationCoefficient = value;
      return this;
    }

    /** Sets the maximal swap active chain size. */
    public Builder setMaxSwapActiveChainSize(int value) {
      maxSwapActiveChainSize = value;
      return this;
    }

    /** Sets the relocate expensive chain option. */
    public Builder setRelocateExpensiveChainNumArcsToConsider(int value) {
      relocateExpensiveChainNumArcsToConsider = value;
      return this;
    }

    /** Sets the expensive chain LNS option. */
    public Builder setHeuristicExpensiveChainLnsNumArcsToConsider(int value) {
      heuristicExpensiveChainLnsNumArcsToConsider = value;
      return this;
    }

    /** Sets the close nodes LNS option. */
    public Builder setHeuristicCloseNodesLnsNumNodes(int value) {
      heuristicCloseNodesLnsNumNodes = value;
      return this;
    }

    /** Sets the local search metaheuristic. */
    public Builder setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value value) {
      localSearchMetaheuristic = value;
      return this;
    }

    /** Sets the local search metaheuristic by number. */
    public Builder setLocalSearchMetaheuristicValue(int value) {
      LocalSearchMetaheuristic.Value v = LocalSearchMetaheuristic.Value.forNumber(value);
      localSearchMetaheuristic = v == null ? LocalSearchMetaheuristic.Value.UNRECOGNIZED : v;
      return this;
    }

    /** The local search metaheuristic set so far. */
    public LocalSearchMetaheuristic.Value getLocalSearchMetaheuristic() {
      return localSearchMetaheuristic;
    }

    /** Sets the metaheuristic switch option. */
    public Builder setNumMaxLocalOptimaBeforeMetaheuristicSwitch(int value) {
      numMaxLocalOptimaBeforeMetaheuristicSwitch = value;
      return this;
    }

    /** Sets the lambda coefficient of the guided local search. */
    public Builder setGuidedLocalSearchLambdaCoefficient(double value) {
      guidedLocalSearchLambdaCoefficient = value;
      return this;
    }

    /** Sets a guided local search option. */
    public Builder setGuidedLocalSearchResetPenaltiesOnNewBestSolution(boolean value) {
      guidedLocalSearchResetPenaltiesOnNewBestSolution = value;
      return this;
    }

    /** Sets a guided local search option. */
    public Builder setGuidedLocalSearchPenalizeWithVehicleClasses(boolean value) {
      guidedLocalSearchPenalizeWithVehicleClasses = value;
      return this;
    }

    /** Sets a guided local search option. */
    public Builder setUseGuidedLocalSearchPenaltiesInLocalSearchOperators(boolean value) {
      useGuidedLocalSearchPenaltiesInLocalSearchOperators = value;
      return this;
    }

    /** Sets the depth first search option. */
    public Builder setUseDepthFirstSearch(boolean value) {
      useDepthFirstSearch = value;
      return this;
    }

    /** Sets the CP-SAT fallback threshold. */
    public Builder setFallbackToCpSatSizeThreshold(int value) {
      fallbackToCpSatSizeThreshold = value;
      return this;
    }

    /** Sets the optimization step. */
    public Builder setOptimizationStep(double value) {
      optimizationStep = value;
      return this;
    }

    /** Sets the number of solutions to collect. */
    public Builder setNumberOfSolutionsToCollect(int value) {
      numberOfSolutionsToCollect = value;
      return this;
    }

    /** Sets the maximal number of solutions of the search. */
    public Builder setSolutionLimit(long value) {
      solutionLimit = value;
      return this;
    }

    /** The solution limit set so far. */
    public long getSolutionLimit() {
      return solutionLimit;
    }

    /** Sets the time limit. */
    public Builder setTimeLimit(Duration value) {
      timeLimit = value;
      return this;
    }

    /** Sets the time limit from a duration builder. */
    public Builder setTimeLimit(Duration.Builder value) {
      timeLimit = value.build();
      return this;
    }

    /** Removes the time limit. */
    public Builder clearTimeLimit() {
      timeLimit = null;
      return this;
    }

    /** Whether a time limit is set. */
    public boolean hasTimeLimit() {
      return timeLimit != null;
    }

    /** The time limit set so far. */
    public Duration getTimeLimit() {
      return timeLimit == null ? Duration.getDefaultInstance() : timeLimit;
    }

    /** Sets the LNS time limit. */
    public Builder setLnsTimeLimit(Duration value) {
      lnsTimeLimit = value;
      return this;
    }

    /** Sets the LNS time limit from a duration builder. */
    public Builder setLnsTimeLimit(Duration.Builder value) {
      lnsTimeLimit = value.build();
      return this;
    }

    /** Removes the LNS time limit. */
    public Builder clearLnsTimeLimit() {
      lnsTimeLimit = null;
      return this;
    }

    /** Sets the secondary local search time ratio. */
    public Builder setSecondaryLsTimeLimitRatio(double value) {
      secondaryLsTimeLimitRatio = value;
      return this;
    }

    /** Sets the full propagation option. */
    public Builder setUseFullPropagation(boolean value) {
      useFullPropagation = value;
      return this;
    }

    /** Sets whether the search progress is logged. */
    public Builder setLogSearch(boolean value) {
      logSearch = value;
      return this;
    }

    /** Whether the search progress is logged. */
    public boolean getLogSearch() {
      return logSearch;
    }

    /** Sets the scaling of the logged costs. */
    public Builder setLogCostScalingFactor(double value) {
      logCostScalingFactor = value;
      return this;
    }

    /** Sets the offset of the logged costs. */
    public Builder setLogCostOffset(double value) {
      logCostOffset = value;
      return this;
    }

    /** Sets the tag of the log lines. */
    public Builder setLogTag(String value) {
      logTag = value == null ? "" : value;
      return this;
    }

    /** Creates the message. */
    public RoutingSearchParameters build() {
      return new RoutingSearchParameters(this);
    }
  }
}
