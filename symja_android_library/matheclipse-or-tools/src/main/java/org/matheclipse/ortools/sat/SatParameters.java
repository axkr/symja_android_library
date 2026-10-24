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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/sat_parameters.proto to Java (a plain Java class replaces the generated protobuf message), see NOTICE in the module root.

package org.matheclipse.ortools.sat;

/**
 * <pre>
 * Contains the definitions for all the sat algorithm parameters and their
 * default values.
 *
 * NEXT TAG: 374
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.SatParameters} of OR-Tools (generated from ortools/sat/sat_parameters.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class SatParameters implements ProtoMessage {
  private static final SatParameters DEFAULT_INSTANCE = new SatParameters();

  /**
   * <pre>
   * Variables without activity (i.e. at the beginning of the search) will be
   * tried in this preferred order.
   * </pre>
   */
  public enum VariableOrder {
    /**
     * <pre>
     * As specified by the problem.
     * </pre>
     */
    IN_ORDER(0),
    IN_REVERSE_ORDER(1),
    IN_RANDOM_ORDER(2);

    /** The number of {@link #IN_ORDER}. */
    public static final int IN_ORDER_VALUE = 0;
    /** The number of {@link #IN_REVERSE_ORDER}. */
    public static final int IN_REVERSE_ORDER_VALUE = 1;
    /** The number of {@link #IN_RANDOM_ORDER}. */
    public static final int IN_RANDOM_ORDER_VALUE = 2;

    private final int value;

    VariableOrder(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static VariableOrder forNumber(int value) {
      switch (value) {
        case 0:
          return IN_ORDER;
        case 1:
          return IN_REVERSE_ORDER;
        case 2:
          return IN_RANDOM_ORDER;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static VariableOrder valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Specifies the initial polarity (true/false) when the solver branches on a
   * variable. This can be modified later by the user, or the phase saving
   * heuristic.
   *
   * NOTE(user): POLARITY_FALSE is usually a good choice because of the
   * "natural" way to express a linear boolean problem.
   * </pre>
   */
  public enum Polarity {
    POLARITY_TRUE(0),
    POLARITY_FALSE(1),
    POLARITY_RANDOM(2);

    /** The number of {@link #POLARITY_TRUE}. */
    public static final int POLARITY_TRUE_VALUE = 0;
    /** The number of {@link #POLARITY_FALSE}. */
    public static final int POLARITY_FALSE_VALUE = 1;
    /** The number of {@link #POLARITY_RANDOM}. */
    public static final int POLARITY_RANDOM_VALUE = 2;

    private final int value;

    Polarity(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static Polarity forNumber(int value) {
      switch (value) {
        case 0:
          return POLARITY_TRUE;
        case 1:
          return POLARITY_FALSE;
        case 2:
          return POLARITY_RANDOM;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static Polarity valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Do we try to minimize conflicts (greedily) when creating them.
   * </pre>
   */
  public enum ConflictMinimizationAlgorithm {
    NONE(0),
    SIMPLE(1),
    RECURSIVE(2);

    /** The number of {@link #NONE}. */
    public static final int NONE_VALUE = 0;
    /** The number of {@link #SIMPLE}. */
    public static final int SIMPLE_VALUE = 1;
    /** The number of {@link #RECURSIVE}. */
    public static final int RECURSIVE_VALUE = 2;

    private final int value;

    ConflictMinimizationAlgorithm(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static ConflictMinimizationAlgorithm forNumber(int value) {
      switch (value) {
        case 0:
          return NONE;
        case 1:
          return SIMPLE;
        case 2:
          return RECURSIVE;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static ConflictMinimizationAlgorithm valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Whether to expoit the binary clause to minimize learned clauses further.
   * </pre>
   */
  public enum BinaryMinizationAlgorithm {
    NO_BINARY_MINIMIZATION(0),
    BINARY_MINIMIZATION_FROM_UIP(1),
    BINARY_MINIMIZATION_FROM_UIP_AND_DECISIONS(5);

    /** The number of {@link #NO_BINARY_MINIMIZATION}. */
    public static final int NO_BINARY_MINIMIZATION_VALUE = 0;
    /** The number of {@link #BINARY_MINIMIZATION_FROM_UIP}. */
    public static final int BINARY_MINIMIZATION_FROM_UIP_VALUE = 1;
    /** The number of {@link #BINARY_MINIMIZATION_FROM_UIP_AND_DECISIONS}. */
    public static final int BINARY_MINIMIZATION_FROM_UIP_AND_DECISIONS_VALUE = 5;

    private final int value;

    BinaryMinizationAlgorithm(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static BinaryMinizationAlgorithm forNumber(int value) {
      switch (value) {
        case 0:
          return NO_BINARY_MINIMIZATION;
        case 1:
          return BINARY_MINIMIZATION_FROM_UIP;
        case 5:
          return BINARY_MINIMIZATION_FROM_UIP_AND_DECISIONS;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static BinaryMinizationAlgorithm valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * The clauses that will be kept during a cleanup are the ones that come
   * first under this order. We always keep or exclude ties together.
   * </pre>
   */
  public enum ClauseOrdering {
    /**
     * <pre>
     * Order clause by decreasing activity, then by increasing LBD.
     * </pre>
     */
    CLAUSE_ACTIVITY(0),
    /**
     * <pre>
     * Order clause by increasing LBD, then by decreasing activity.
     * </pre>
     */
    CLAUSE_LBD(1);

    /** The number of {@link #CLAUSE_ACTIVITY}. */
    public static final int CLAUSE_ACTIVITY_VALUE = 0;
    /** The number of {@link #CLAUSE_LBD}. */
    public static final int CLAUSE_LBD_VALUE = 1;

    private final int value;

    ClauseOrdering(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static ClauseOrdering forNumber(int value) {
      switch (value) {
        case 0:
          return CLAUSE_ACTIVITY;
        case 1:
          return CLAUSE_LBD;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static ClauseOrdering valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Restart algorithms.
   *
   * A reference for the more advanced ones is:
   * Gilles Audemard, Laurent Simon, "Refining Restarts Strategies for SAT
   * and UNSAT", Principles and Practice of Constraint Programming Lecture
   * Notes in Computer Science 2012, pp 118-126
   * </pre>
   */
  public enum RestartAlgorithm {
    NO_RESTART(0),
    /**
     * <pre>
     * Just follow a Luby sequence times restart_period.
     * </pre>
     */
    LUBY_RESTART(1),
    /**
     * <pre>
     * Moving average restart based on the decision level of conflicts.
     * </pre>
     */
    DL_MOVING_AVERAGE_RESTART(2),
    /**
     * <pre>
     * Moving average restart based on the LBD of conflicts.
     * </pre>
     */
    LBD_MOVING_AVERAGE_RESTART(3),
    /**
     * <pre>
     * Fixed period restart every restart period.
     * </pre>
     */
    FIXED_RESTART(4);

    /** The number of {@link #NO_RESTART}. */
    public static final int NO_RESTART_VALUE = 0;
    /** The number of {@link #LUBY_RESTART}. */
    public static final int LUBY_RESTART_VALUE = 1;
    /** The number of {@link #DL_MOVING_AVERAGE_RESTART}. */
    public static final int DL_MOVING_AVERAGE_RESTART_VALUE = 2;
    /** The number of {@link #LBD_MOVING_AVERAGE_RESTART}. */
    public static final int LBD_MOVING_AVERAGE_RESTART_VALUE = 3;
    /** The number of {@link #FIXED_RESTART}. */
    public static final int FIXED_RESTART_VALUE = 4;

    private final int value;

    RestartAlgorithm(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static RestartAlgorithm forNumber(int value) {
      switch (value) {
        case 0:
          return NO_RESTART;
        case 1:
          return LUBY_RESTART;
        case 2:
          return DL_MOVING_AVERAGE_RESTART;
        case 3:
          return LBD_MOVING_AVERAGE_RESTART;
        case 4:
          return FIXED_RESTART;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static RestartAlgorithm valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * In what order do we add the assumptions in a core-based max-sat algorithm
   * </pre>
   */
  public enum MaxSatAssumptionOrder {
    DEFAULT_ASSUMPTION_ORDER(0),
    ORDER_ASSUMPTION_BY_DEPTH(1),
    ORDER_ASSUMPTION_BY_WEIGHT(2);

    /** The number of {@link #DEFAULT_ASSUMPTION_ORDER}. */
    public static final int DEFAULT_ASSUMPTION_ORDER_VALUE = 0;
    /** The number of {@link #ORDER_ASSUMPTION_BY_DEPTH}. */
    public static final int ORDER_ASSUMPTION_BY_DEPTH_VALUE = 1;
    /** The number of {@link #ORDER_ASSUMPTION_BY_WEIGHT}. */
    public static final int ORDER_ASSUMPTION_BY_WEIGHT_VALUE = 2;

    private final int value;

    MaxSatAssumptionOrder(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static MaxSatAssumptionOrder forNumber(int value) {
      switch (value) {
        case 0:
          return DEFAULT_ASSUMPTION_ORDER;
        case 1:
          return ORDER_ASSUMPTION_BY_DEPTH;
        case 2:
          return ORDER_ASSUMPTION_BY_WEIGHT;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static MaxSatAssumptionOrder valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * What stratification algorithm we use in the presence of weight.
   * </pre>
   */
  public enum MaxSatStratificationAlgorithm {
    /**
     * <pre>
     * No stratification of the problem.
     * </pre>
     */
    STRATIFICATION_NONE(0),
    /**
     * <pre>
     * Start with literals with the highest weight, and when SAT, add the
     * literals with the next highest weight and so on.
     * </pre>
     */
    STRATIFICATION_DESCENT(1),
    /**
     * <pre>
     * Start with all literals. Each time a core is found with a given minimum
     * weight, do not consider literals with a lower weight for the next core
     * computation. If the subproblem is SAT, do like in STRATIFICATION_DESCENT
     * and just add the literals with the next highest weight.
     * </pre>
     */
    STRATIFICATION_ASCENT(2);

    /** The number of {@link #STRATIFICATION_NONE}. */
    public static final int STRATIFICATION_NONE_VALUE = 0;
    /** The number of {@link #STRATIFICATION_DESCENT}. */
    public static final int STRATIFICATION_DESCENT_VALUE = 1;
    /** The number of {@link #STRATIFICATION_ASCENT}. */
    public static final int STRATIFICATION_ASCENT_VALUE = 2;

    private final int value;

    MaxSatStratificationAlgorithm(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static MaxSatStratificationAlgorithm forNumber(int value) {
      switch (value) {
        case 0:
          return STRATIFICATION_NONE;
        case 1:
          return STRATIFICATION_DESCENT;
        case 2:
          return STRATIFICATION_ASCENT;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static MaxSatStratificationAlgorithm valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * The search branching will be used to decide how to branch on unfixed nodes.
   * </pre>
   */
  public enum SearchBranching {
    /**
     * <pre>
     * Try to fix all literals using the underlying SAT solver's heuristics,
     * then generate and fix literals until integer variables are fixed. New
     * literals on integer variables are generated using the fixed search
     * specified by the user or our default one.
     * </pre>
     */
    AUTOMATIC_SEARCH(0),
    /**
     * <pre>
     * If used then all decisions taken by the solver are made using a fixed
     * order as specified in the API or in the CpModelProto search_strategy
     * field.
     * </pre>
     */
    FIXED_SEARCH(1),
    /**
     * <pre>
     * Simple portfolio search used by LNS workers.
     * </pre>
     */
    PORTFOLIO_SEARCH(2),
    /**
     * <pre>
     * If used, the solver will use heuristics from the LP relaxation. This
     * exploit the reduced costs of the variables in the relaxation.
     * </pre>
     */
    LP_SEARCH(3),
    /**
     * <pre>
     * If used, the solver uses the pseudo costs for branching. Pseudo costs
     * are computed using the historical change in objective bounds when some
     * decision are taken. Note that this works whether we use an LP or not.
     * </pre>
     */
    PSEUDO_COST_SEARCH(4),
    /**
     * <pre>
     * Mainly exposed here for testing. This quickly tries a lot of randomized
     * heuristics with a low conflict limit. It usually provides a good first
     * solution.
     * </pre>
     */
    PORTFOLIO_WITH_QUICK_RESTART_SEARCH(5),
    /**
     * <pre>
     * Mainly used internally. This is like FIXED_SEARCH, except we follow the
     * solution_hint field of the CpModelProto rather than using the information
     * provided in the search_strategy.
     * </pre>
     */
    HINT_SEARCH(6),
    /**
     * <pre>
     * Similar to FIXED_SEARCH, but differ in how the variable not listed into
     * the fixed search heuristics are branched on. This will always start the
     * search tree according to the specified fixed search strategy, but will
     * complete it using the default automatic search.
     * </pre>
     */
    PARTIAL_FIXED_SEARCH(7),
    /**
     * <pre>
     * Randomized search. Used to increase entropy in the search.
     * </pre>
     */
    RANDOMIZED_SEARCH(8),
    /**
     * <pre>
     * This is a lightweight search heuristic that tries to shave variable
     * domains and literals in a round-robin fashion at decision level 0.
     * Note that unlike the dedicated shaving subsolver using assumptions, if
     * the search restarts, we simply move to the next candidate without
     * guaranteeing a fixed amount of effort is spent on the current one.
     * </pre>
     */
    ROUND_ROBIN_SHAVING_SEARCH(9),
    /**
     * <pre>
     * Alternate between fixed and automatic_search, with the probability of
     * using the fixed search starting at 1.0 and decaying.
     * The rationale is that the user defined search is usually good for
     * starting the search, but not great for continuing it.
     * </pre>
     */
    ALT_FIXED_SEARCH(10);

    /** The number of {@link #AUTOMATIC_SEARCH}. */
    public static final int AUTOMATIC_SEARCH_VALUE = 0;
    /** The number of {@link #FIXED_SEARCH}. */
    public static final int FIXED_SEARCH_VALUE = 1;
    /** The number of {@link #PORTFOLIO_SEARCH}. */
    public static final int PORTFOLIO_SEARCH_VALUE = 2;
    /** The number of {@link #LP_SEARCH}. */
    public static final int LP_SEARCH_VALUE = 3;
    /** The number of {@link #PSEUDO_COST_SEARCH}. */
    public static final int PSEUDO_COST_SEARCH_VALUE = 4;
    /** The number of {@link #PORTFOLIO_WITH_QUICK_RESTART_SEARCH}. */
    public static final int PORTFOLIO_WITH_QUICK_RESTART_SEARCH_VALUE = 5;
    /** The number of {@link #HINT_SEARCH}. */
    public static final int HINT_SEARCH_VALUE = 6;
    /** The number of {@link #PARTIAL_FIXED_SEARCH}. */
    public static final int PARTIAL_FIXED_SEARCH_VALUE = 7;
    /** The number of {@link #RANDOMIZED_SEARCH}. */
    public static final int RANDOMIZED_SEARCH_VALUE = 8;
    /** The number of {@link #ROUND_ROBIN_SHAVING_SEARCH}. */
    public static final int ROUND_ROBIN_SHAVING_SEARCH_VALUE = 9;
    /** The number of {@link #ALT_FIXED_SEARCH}. */
    public static final int ALT_FIXED_SEARCH_VALUE = 10;

    private final int value;

    SearchBranching(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static SearchBranching forNumber(int value) {
      switch (value) {
        case 0:
          return AUTOMATIC_SEARCH;
        case 1:
          return FIXED_SEARCH;
        case 2:
          return PORTFOLIO_SEARCH;
        case 3:
          return LP_SEARCH;
        case 4:
          return PSEUDO_COST_SEARCH;
        case 5:
          return PORTFOLIO_WITH_QUICK_RESTART_SEARCH;
        case 6:
          return HINT_SEARCH;
        case 7:
          return PARTIAL_FIXED_SEARCH;
        case 8:
          return RANDOMIZED_SEARCH;
        case 9:
          return ROUND_ROBIN_SHAVING_SEARCH;
        case 10:
          return ALT_FIXED_SEARCH;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static SearchBranching valueOf(int value) {
      return forNumber(value);
    }
  }

  /** Replaces the protobuf enum {@code SharedTreeSplitStrategy}. */
  public enum SharedTreeSplitStrategy {
    /**
     * <pre>
     * Uses the default strategy, currently equivalent to
     * SPLIT_STRATEGY_DISCREPANCY.
     * </pre>
     */
    SPLIT_STRATEGY_AUTO(0),
    /**
     * <pre>
     * Only accept splits if the node to be split's depth+discrepancy is minimal
     * for the desired number of leaves.
     * The preferred child for discrepancy calculation is the one with the
     * lowest objective lower bound or the original branch direction if the
     * bounds are equal. This rule allows twice as many workers to work in the
     * preferred subtree as non-preferred.
     * </pre>
     */
    SPLIT_STRATEGY_DISCREPANCY(1),
    /**
     * <pre>
     * Only split nodes with an objective lb equal to the global lb. If there is
     * no objective, this is equivalent to SPLIT_STRATEGY_FIRST_PROPOSAL.
     * </pre>
     */
    SPLIT_STRATEGY_OBJECTIVE_LB(2),
    /**
     * <pre>
     * Attempt to keep the shared tree balanced.
     * </pre>
     */
    SPLIT_STRATEGY_BALANCED_TREE(3),
    /**
     * <pre>
     * Workers race to split their subtree, the winner's proposal is accepted.
     * </pre>
     */
    SPLIT_STRATEGY_FIRST_PROPOSAL(4);

    /** The number of {@link #SPLIT_STRATEGY_AUTO}. */
    public static final int SPLIT_STRATEGY_AUTO_VALUE = 0;
    /** The number of {@link #SPLIT_STRATEGY_DISCREPANCY}. */
    public static final int SPLIT_STRATEGY_DISCREPANCY_VALUE = 1;
    /** The number of {@link #SPLIT_STRATEGY_OBJECTIVE_LB}. */
    public static final int SPLIT_STRATEGY_OBJECTIVE_LB_VALUE = 2;
    /** The number of {@link #SPLIT_STRATEGY_BALANCED_TREE}. */
    public static final int SPLIT_STRATEGY_BALANCED_TREE_VALUE = 3;
    /** The number of {@link #SPLIT_STRATEGY_FIRST_PROPOSAL}. */
    public static final int SPLIT_STRATEGY_FIRST_PROPOSAL_VALUE = 4;

    private final int value;

    SharedTreeSplitStrategy(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static SharedTreeSplitStrategy forNumber(int value) {
      switch (value) {
        case 0:
          return SPLIT_STRATEGY_AUTO;
        case 1:
          return SPLIT_STRATEGY_DISCREPANCY;
        case 2:
          return SPLIT_STRATEGY_OBJECTIVE_LB;
        case 3:
          return SPLIT_STRATEGY_BALANCED_TREE;
        case 4:
          return SPLIT_STRATEGY_FIRST_PROPOSAL;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static SharedTreeSplitStrategy valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Rounding method to use for feasibility pump.
   * </pre>
   */
  public enum FPRoundingMethod {
    /**
     * <pre>
     * Rounds to the nearest integer value.
     * </pre>
     */
    NEAREST_INTEGER(0),
    /**
     * <pre>
     * Counts the number of linear constraints restricting the variable in the
     * increasing values (up locks) and decreasing values (down locks). Rounds
     * the variable in the direction of lesser locks.
     * </pre>
     */
    LOCK_BASED(1),
    /**
     * <pre>
     * Similar to lock based rounding except this only considers locks of active
     * constraints from the last lp solve.
     * </pre>
     */
    ACTIVE_LOCK_BASED(3),
    /**
     * <pre>
     * This is expensive rounding algorithm. We round variables one by one and
     * propagate the bounds in between. If none of the rounded values fall in
     * the continuous domain specified by lower and upper bound, we use the
     * current lower/upper bound (whichever one is closest) instead of rounding
     * the fractional lp solution value. If both the rounded values are in the
     * domain, we round to nearest integer.
     * </pre>
     */
    PROPAGATION_ASSISTED(2);

    /** The number of {@link #NEAREST_INTEGER}. */
    public static final int NEAREST_INTEGER_VALUE = 0;
    /** The number of {@link #LOCK_BASED}. */
    public static final int LOCK_BASED_VALUE = 1;
    /** The number of {@link #ACTIVE_LOCK_BASED}. */
    public static final int ACTIVE_LOCK_BASED_VALUE = 3;
    /** The number of {@link #PROPAGATION_ASSISTED}. */
    public static final int PROPAGATION_ASSISTED_VALUE = 2;

    private final int value;

    FPRoundingMethod(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static FPRoundingMethod forNumber(int value) {
      switch (value) {
        case 0:
          return NEAREST_INTEGER;
        case 1:
          return LOCK_BASED;
        case 3:
          return ACTIVE_LOCK_BASED;
        case 2:
          return PROPAGATION_ASSISTED;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static FPRoundingMethod valueOf(int value) {
      return forNumber(value);
    }
  }

  String name_ = "";
  boolean hasName_;
  int preferredVariableOrder_ = 0;
  boolean hasPreferredVariableOrder_;
  int initialPolarity_ = 1;
  boolean hasInitialPolarity_;
  boolean usePhaseSaving_ = true;
  boolean hasUsePhaseSaving_;
  int polarityRephaseIncrement_ = 1000;
  boolean hasPolarityRephaseIncrement_;
  boolean polarityExploitLsHints_ = false;
  boolean hasPolarityExploitLsHints_;
  double randomPolarityRatio_ = 0.0;
  boolean hasRandomPolarityRatio_;
  double randomBranchesRatio_ = 0.0;
  boolean hasRandomBranchesRatio_;
  boolean useErwaHeuristic_ = false;
  boolean hasUseErwaHeuristic_;
  double initialVariablesActivity_ = 0.0;
  boolean hasInitialVariablesActivity_;
  boolean alsoBumpVariablesInConflictReasons_ = false;
  boolean hasAlsoBumpVariablesInConflictReasons_;
  int minimizationAlgorithm_ = 2;
  boolean hasMinimizationAlgorithm_;
  int binaryMinimizationAlgorithm_ = 5;
  boolean hasBinaryMinimizationAlgorithm_;
  boolean subsumptionDuringConflictAnalysis_ = true;
  boolean hasSubsumptionDuringConflictAnalysis_;
  boolean extraSubsumptionDuringConflictAnalysis_ = true;
  boolean hasExtraSubsumptionDuringConflictAnalysis_;
  boolean decisionSubsumptionDuringConflictAnalysis_ = true;
  boolean hasDecisionSubsumptionDuringConflictAnalysis_;
  int eagerlySubsumeLastNConflicts_ = 4;
  boolean hasEagerlySubsumeLastNConflicts_;
  boolean subsumeDuringVivification_ = true;
  boolean hasSubsumeDuringVivification_;
  boolean useChronologicalBacktracking_ = false;
  boolean hasUseChronologicalBacktracking_;
  int maxBackjumpLevels_ = 50;
  boolean hasMaxBackjumpLevels_;
  int chronologicalBacktrackMinConflicts_ = 1000;
  boolean hasChronologicalBacktrackMinConflicts_;
  boolean learnOnAssumptionsUnsat_ = true;
  boolean hasLearnOnAssumptionsUnsat_;
  int clauseCleanupPeriod_ = 10000;
  boolean hasClauseCleanupPeriod_;
  int clauseCleanupPeriodIncrement_ = 0;
  boolean hasClauseCleanupPeriodIncrement_;
  int clauseCleanupTarget_ = 0;
  boolean hasClauseCleanupTarget_;
  double clauseCleanupRatio_ = 0.5;
  boolean hasClauseCleanupRatio_;
  int clauseCleanupLbdBound_ = 5;
  boolean hasClauseCleanupLbdBound_;
  int clauseCleanupLbdTier1_ = 0;
  boolean hasClauseCleanupLbdTier1_;
  int clauseCleanupLbdTier2_ = 0;
  boolean hasClauseCleanupLbdTier2_;
  int clauseCleanupOrdering_ = 0;
  boolean hasClauseCleanupOrdering_;
  int pbCleanupIncrement_ = 200;
  boolean hasPbCleanupIncrement_;
  double pbCleanupRatio_ = 0.5;
  boolean hasPbCleanupRatio_;
  double variableActivityDecay_ = 0.8;
  boolean hasVariableActivityDecay_;
  double maxVariableActivityValue_ = 1e100;
  boolean hasMaxVariableActivityValue_;
  double glucoseMaxDecay_ = 0.95;
  boolean hasGlucoseMaxDecay_;
  double glucoseDecayIncrement_ = 0.01;
  boolean hasGlucoseDecayIncrement_;
  int glucoseDecayIncrementPeriod_ = 5000;
  boolean hasGlucoseDecayIncrementPeriod_;
  double clauseActivityDecay_ = 0.999;
  boolean hasClauseActivityDecay_;
  double maxClauseActivityValue_ = 1e20;
  boolean hasMaxClauseActivityValue_;
  java.util.ArrayList<SatParameters.RestartAlgorithm> restartAlgorithms_;
  String defaultRestartAlgorithms_ = "LUBY_RESTART,LBD_MOVING_AVERAGE_RESTART,DL_MOVING_AVERAGE_RESTART";
  boolean hasDefaultRestartAlgorithms_;
  int restartPeriod_ = 50;
  boolean hasRestartPeriod_;
  int restartRunningWindowSize_ = 50;
  boolean hasRestartRunningWindowSize_;
  double restartDlAverageRatio_ = 1.0;
  boolean hasRestartDlAverageRatio_;
  double restartLbdAverageRatio_ = 1.0;
  boolean hasRestartLbdAverageRatio_;
  boolean useBlockingRestart_ = false;
  boolean hasUseBlockingRestart_;
  int blockingRestartWindowSize_ = 5000;
  boolean hasBlockingRestartWindowSize_;
  double blockingRestartMultiplier_ = 1.4;
  boolean hasBlockingRestartMultiplier_;
  int numConflictsBeforeStrategyChanges_ = 0;
  boolean hasNumConflictsBeforeStrategyChanges_;
  double strategyChangeIncreaseRatio_ = 0.0;
  boolean hasStrategyChangeIncreaseRatio_;
  double maxTimeInSeconds_ = Double.POSITIVE_INFINITY;
  boolean hasMaxTimeInSeconds_;
  double maxDeterministicTime_ = Double.POSITIVE_INFINITY;
  boolean hasMaxDeterministicTime_;
  int maxNumDeterministicBatches_ = 0;
  boolean hasMaxNumDeterministicBatches_;
  long maxNumberOfConflicts_ = 0x7FFFFFFFFFFFFFFFL;
  boolean hasMaxNumberOfConflicts_;
  long maxMemoryInMb_ = 10000L;
  boolean hasMaxMemoryInMb_;
  double absoluteGapLimit_ = 1e-4;
  boolean hasAbsoluteGapLimit_;
  double relativeGapLimit_ = 0.0;
  boolean hasRelativeGapLimit_;
  int randomSeed_ = 1;
  boolean hasRandomSeed_;
  boolean permuteVariableRandomly_ = false;
  boolean hasPermuteVariableRandomly_;
  boolean permutePresolveConstraintOrder_ = false;
  boolean hasPermutePresolveConstraintOrder_;
  boolean useAbslRandom_ = false;
  boolean hasUseAbslRandom_;
  boolean logSearchProgress_ = false;
  boolean hasLogSearchProgress_;
  boolean logSubsolverStatistics_ = false;
  boolean hasLogSubsolverStatistics_;
  String logPrefix_ = "";
  boolean hasLogPrefix_;
  boolean logToStdout_ = true;
  boolean hasLogToStdout_;
  boolean logToResponse_ = false;
  boolean hasLogToResponse_;
  boolean usePbResolution_ = false;
  boolean hasUsePbResolution_;
  boolean minimizeReductionDuringPbResolution_ = false;
  boolean hasMinimizeReductionDuringPbResolution_;
  boolean countAssumptionLevelsInLbd_ = true;
  boolean hasCountAssumptionLevelsInLbd_;
  int presolveBveThreshold_ = 500;
  boolean hasPresolveBveThreshold_;
  boolean filterSatPostsolveClauses_ = false;
  boolean hasFilterSatPostsolveClauses_;
  int presolveBveClauseWeight_ = 3;
  boolean hasPresolveBveClauseWeight_;
  double probingDeterministicTimeLimit_ = 1.0;
  boolean hasProbingDeterministicTimeLimit_;
  double presolveProbingDeterministicTimeLimit_ = 30.0;
  boolean hasPresolveProbingDeterministicTimeLimit_;
  boolean presolveBlockedClause_ = true;
  boolean hasPresolveBlockedClause_;
  boolean presolveUseBva_ = true;
  boolean hasPresolveUseBva_;
  int presolveBvaThreshold_ = 1;
  boolean hasPresolveBvaThreshold_;
  int maxPresolveIterations_ = 3;
  boolean hasMaxPresolveIterations_;
  boolean cpModelPresolve_ = true;
  boolean hasCpModelPresolve_;
  boolean cpModelDirectSolve_ = false;
  boolean hasCpModelDirectSolve_;
  int cpModelProbingLevel_ = 2;
  boolean hasCpModelProbingLevel_;
  boolean cpModelUseSatPresolve_ = true;
  boolean hasCpModelUseSatPresolve_;
  boolean cpModelPureSatPresolve_ = false;
  boolean hasCpModelPureSatPresolve_;
  boolean loadAtMostOnesInSatPresolve_ = false;
  boolean hasLoadAtMostOnesInSatPresolve_;
  boolean removeFixedVariablesEarly_ = true;
  boolean hasRemoveFixedVariablesEarly_;
  boolean detectTableWithCost_ = false;
  boolean hasDetectTableWithCost_;
  int tableCompressionLevel_ = 2;
  boolean hasTableCompressionLevel_;
  boolean expandAlldiffConstraints_ = false;
  boolean hasExpandAlldiffConstraints_;
  int maxAlldiffDomainSize_ = 256;
  boolean hasMaxAlldiffDomainSize_;
  boolean expandReservoirConstraints_ = true;
  boolean hasExpandReservoirConstraints_;
  int maxDomainSizeForLinear2Expansion_ = 8;
  boolean hasMaxDomainSizeForLinear2Expansion_;
  boolean expandReservoirUsingCircuit_ = false;
  boolean hasExpandReservoirUsingCircuit_;
  boolean encodeCumulativeAsReservoir_ = false;
  boolean hasEncodeCumulativeAsReservoir_;
  int maxLinMaxSizeForExpansion_ = 0;
  boolean hasMaxLinMaxSizeForExpansion_;
  boolean disableConstraintExpansion_ = false;
  boolean hasDisableConstraintExpansion_;
  boolean encodeComplexLinearConstraintWithInteger_ = false;
  boolean hasEncodeComplexLinearConstraintWithInteger_;
  boolean detectEncodedComplexDomain_ = true;
  boolean hasDetectEncodedComplexDomain_;
  double mergeNoOverlapWorkLimit_ = 1e12;
  boolean hasMergeNoOverlapWorkLimit_;
  double mergeAtMostOneWorkLimit_ = 1e8;
  boolean hasMergeAtMostOneWorkLimit_;
  int presolveSubstitutionLevel_ = 1;
  boolean hasPresolveSubstitutionLevel_;
  boolean presolveExtractIntegerEnforcement_ = false;
  boolean hasPresolveExtractIntegerEnforcement_;
  long presolveInclusionWorkLimit_ = 100000000L;
  boolean hasPresolveInclusionWorkLimit_;
  boolean ignoreNames_ = true;
  boolean hasIgnoreNames_;
  boolean inferAllDiffs_ = true;
  boolean hasInferAllDiffs_;
  boolean findBigLinearOverlap_ = true;
  boolean hasFindBigLinearOverlap_;
  boolean findClausesThatAreExactlyOne_ = true;
  boolean hasFindClausesThatAreExactlyOne_;
  boolean useSatInprocessing_ = true;
  boolean hasUseSatInprocessing_;
  double inprocessingDtimeRatio_ = 0.2;
  boolean hasInprocessingDtimeRatio_;
  double inprocessingProbingDtime_ = 1.0;
  boolean hasInprocessingProbingDtime_;
  double inprocessingMinimizationDtime_ = 1.0;
  boolean hasInprocessingMinimizationDtime_;
  boolean inprocessingMinimizationUseConflictAnalysis_ = true;
  boolean hasInprocessingMinimizationUseConflictAnalysis_;
  int numOrderingInVivification_ = 1;
  boolean hasNumOrderingInVivification_;
  boolean inprocessingUseCongruenceClosure_ = true;
  boolean hasInprocessingUseCongruenceClosure_;
  boolean inprocessingDetectAndSweepCircuit_ = false;
  boolean hasInprocessingDetectAndSweepCircuit_;
  boolean inprocessingUseSatSweeping_ = false;
  boolean hasInprocessingUseSatSweeping_;
  boolean inprocessingUseTransitiveReduction_ = true;
  boolean hasInprocessingUseTransitiveReduction_;
  int numWorkers_ = 0;
  boolean hasNumWorkers_;
  int numSearchWorkers_ = 0;
  boolean hasNumSearchWorkers_;
  int numFullSubsolvers_ = 0;
  boolean hasNumFullSubsolvers_;
  java.util.ArrayList<String> subsolvers_;
  java.util.ArrayList<String> extraSubsolvers_;
  java.util.ArrayList<String> ignoreSubsolvers_;
  java.util.ArrayList<String> filterSubsolvers_;
  java.util.ArrayList<SatParameters> subsolverParams_;
  boolean interleaveSearch_ = false;
  boolean hasInterleaveSearch_;
  int interleaveBatchSize_ = 0;
  boolean hasInterleaveBatchSize_;
  boolean shareObjectiveBounds_ = true;
  boolean hasShareObjectiveBounds_;
  boolean shareLevelZeroBounds_ = true;
  boolean hasShareLevelZeroBounds_;
  boolean shareLinear2Bounds_ = false;
  boolean hasShareLinear2Bounds_;
  boolean shareBinaryClauses_ = true;
  boolean hasShareBinaryClauses_;
  boolean shareGlueClauses_ = true;
  boolean hasShareGlueClauses_;
  boolean minimizeSharedClauses_ = true;
  boolean hasMinimizeSharedClauses_;
  double shareGlueClausesDtime_ = 1.0;
  boolean hasShareGlueClausesDtime_;
  boolean checkLratProof_ = false;
  boolean hasCheckLratProof_;
  boolean checkMergedLratProof_ = false;
  boolean hasCheckMergedLratProof_;
  boolean outputLratProof_ = false;
  boolean hasOutputLratProof_;
  boolean debugPostsolveWithFullSolver_ = false;
  boolean hasDebugPostsolveWithFullSolver_;
  int debugMaxNumPresolveOperations_ = 0;
  boolean hasDebugMaxNumPresolveOperations_;
  boolean debugCrashOnBadHint_ = false;
  boolean hasDebugCrashOnBadHint_;
  boolean debugCrashIfPresolveBreaksHint_ = false;
  boolean hasDebugCrashIfPresolveBreaksHint_;
  boolean debugCrashIfLratCheckFails_ = false;
  boolean hasDebugCrashIfLratCheckFails_;
  boolean useOptimizationHints_ = true;
  boolean hasUseOptimizationHints_;
  int coreMinimizationLevel_ = 2;
  boolean hasCoreMinimizationLevel_;
  boolean findMultipleCores_ = true;
  boolean hasFindMultipleCores_;
  boolean coverOptimization_ = true;
  boolean hasCoverOptimization_;
  int maxSatAssumptionOrder_ = 0;
  boolean hasMaxSatAssumptionOrder_;
  boolean maxSatReverseAssumptionOrder_ = false;
  boolean hasMaxSatReverseAssumptionOrder_;
  int maxSatStratification_ = 1;
  boolean hasMaxSatStratification_;
  double propagationLoopDetectionFactor_ = 10.0;
  boolean hasPropagationLoopDetectionFactor_;
  boolean usePrecedencesInDisjunctiveConstraint_ = true;
  boolean hasUsePrecedencesInDisjunctiveConstraint_;
  int transitivePrecedencesWorkLimit_ = 1000000;
  boolean hasTransitivePrecedencesWorkLimit_;
  int maxSizeToCreatePrecedenceLiteralsInDisjunctive_ = 60;
  boolean hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_;
  boolean useStrongPropagationInDisjunctive_ = false;
  boolean hasUseStrongPropagationInDisjunctive_;
  boolean useDynamicPrecedenceInDisjunctive_ = false;
  boolean hasUseDynamicPrecedenceInDisjunctive_;
  boolean useDynamicPrecedenceInCumulative_ = false;
  boolean hasUseDynamicPrecedenceInCumulative_;
  boolean useOverloadCheckerInCumulative_ = false;
  boolean hasUseOverloadCheckerInCumulative_;
  boolean useConservativeScaleOverloadChecker_ = false;
  boolean hasUseConservativeScaleOverloadChecker_;
  boolean useTimetableEdgeFindingInCumulative_ = false;
  boolean hasUseTimetableEdgeFindingInCumulative_;
  int maxNumIntervalsForTimetableEdgeFinding_ = 100;
  boolean hasMaxNumIntervalsForTimetableEdgeFinding_;
  boolean useHardPrecedencesInCumulative_ = false;
  boolean hasUseHardPrecedencesInCumulative_;
  boolean exploitAllPrecedences_ = false;
  boolean hasExploitAllPrecedences_;
  boolean useDisjunctiveConstraintInCumulative_ = true;
  boolean hasUseDisjunctiveConstraintInCumulative_;
  int noOverlap2DBooleanRelationsLimit_ = 10;
  boolean hasNoOverlap2DBooleanRelationsLimit_;
  boolean useTimetablingInNoOverlap2D_ = false;
  boolean hasUseTimetablingInNoOverlap2D_;
  boolean useEnergeticReasoningInNoOverlap2D_ = false;
  boolean hasUseEnergeticReasoningInNoOverlap2D_;
  boolean useAreaEnergeticReasoningInNoOverlap2D_ = false;
  boolean hasUseAreaEnergeticReasoningInNoOverlap2D_;
  boolean useTryEdgeReasoningInNoOverlap2D_ = false;
  boolean hasUseTryEdgeReasoningInNoOverlap2D_;
  int maxPairsPairwiseReasoningInNoOverlap2D_ = 1250;
  boolean hasMaxPairsPairwiseReasoningInNoOverlap2D_;
  int maximumRegionsToSplitInDisconnectedNoOverlap2D_ = 0;
  boolean hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_;
  boolean useLinear3ForNoOverlap2DPrecedences_ = true;
  boolean hasUseLinear3ForNoOverlap2DPrecedences_;
  boolean useDualSchedulingHeuristics_ = true;
  boolean hasUseDualSchedulingHeuristics_;
  boolean useAllDifferentForCircuit_ = false;
  boolean hasUseAllDifferentForCircuit_;
  int routingCutSubsetSizeForBinaryRelationBound_ = 0;
  boolean hasRoutingCutSubsetSizeForBinaryRelationBound_;
  int routingCutSubsetSizeForTightBinaryRelationBound_ = 0;
  boolean hasRoutingCutSubsetSizeForTightBinaryRelationBound_;
  int routingCutSubsetSizeForExactBinaryRelationBound_ = 8;
  boolean hasRoutingCutSubsetSizeForExactBinaryRelationBound_;
  int routingCutSubsetSizeForShortestPathsBound_ = 8;
  boolean hasRoutingCutSubsetSizeForShortestPathsBound_;
  double routingCutDpEffort_ = 1e7;
  boolean hasRoutingCutDpEffort_;
  int routingCutMaxInfeasiblePathLength_ = 6;
  boolean hasRoutingCutMaxInfeasiblePathLength_;
  int searchBranching_ = 0;
  boolean hasSearchBranching_;
  int hintConflictLimit_ = 10;
  boolean hasHintConflictLimit_;
  boolean tryHintAsAssumptions_ = true;
  boolean hasTryHintAsAssumptions_;
  boolean repairHint_ = false;
  boolean hasRepairHint_;
  boolean fixVariablesToTheirHintedValue_ = false;
  boolean hasFixVariablesToTheirHintedValue_;
  boolean useProbingSearch_ = false;
  boolean hasUseProbingSearch_;
  boolean useExtendedProbing_ = true;
  boolean hasUseExtendedProbing_;
  int probingNumCombinationsLimit_ = 20000;
  boolean hasProbingNumCombinationsLimit_;
  double shavingDeterministicTimeInProbingSearch_ = 0.05;
  boolean hasShavingDeterministicTimeInProbingSearch_;
  double shavingSearchDeterministicTime_ = 0.1;
  boolean hasShavingSearchDeterministicTime_;
  boolean shavingSearchUseRandomSearch_ = false;
  boolean hasShavingSearchUseRandomSearch_;
  int shavingSearchRestartLimit_ = 10;
  boolean hasShavingSearchRestartLimit_;
  long shavingSearchThreshold_ = 64L;
  boolean hasShavingSearchThreshold_;
  long objectiveShavingSearchThreshold_ = 1000L;
  boolean hasObjectiveShavingSearchThreshold_;
  boolean useObjectiveLbSearch_ = false;
  boolean hasUseObjectiveLbSearch_;
  boolean useObjectiveShavingSearch_ = false;
  boolean hasUseObjectiveShavingSearch_;
  int variablesShavingLevel_ = -1;
  boolean hasVariablesShavingLevel_;
  long pseudoCostReliabilityThreshold_ = 100L;
  boolean hasPseudoCostReliabilityThreshold_;
  boolean optimizeWithCore_ = false;
  boolean hasOptimizeWithCore_;
  boolean optimizeWithLbTreeSearch_ = false;
  boolean hasOptimizeWithLbTreeSearch_;
  boolean saveLpBasisInLbTreeSearch_ = false;
  boolean hasSaveLpBasisInLbTreeSearch_;
  int binarySearchNumConflicts_ = -1;
  boolean hasBinarySearchNumConflicts_;
  boolean optimizeWithMaxHs_ = false;
  boolean hasOptimizeWithMaxHs_;
  boolean useFeasibilityJump_ = true;
  boolean hasUseFeasibilityJump_;
  boolean useLsOnly_ = false;
  boolean hasUseLsOnly_;
  double feasibilityJumpDecay_ = 0.95;
  boolean hasFeasibilityJumpDecay_;
  int feasibilityJumpLinearizationLevel_ = 2;
  boolean hasFeasibilityJumpLinearizationLevel_;
  int feasibilityJumpRestartFactor_ = 1;
  boolean hasFeasibilityJumpRestartFactor_;
  double feasibilityJumpBatchDtime_ = 0.1;
  boolean hasFeasibilityJumpBatchDtime_;
  double feasibilityJumpVarRandomizationProbability_ = 0.05;
  boolean hasFeasibilityJumpVarRandomizationProbability_;
  double feasibilityJumpVarPerburbationRangeRatio_ = 0.2;
  boolean hasFeasibilityJumpVarPerburbationRangeRatio_;
  boolean feasibilityJumpEnableRestarts_ = true;
  boolean hasFeasibilityJumpEnableRestarts_;
  int feasibilityJumpMaxExpandedConstraintSize_ = 500;
  boolean hasFeasibilityJumpMaxExpandedConstraintSize_;
  boolean feasibilityJumpIgnoreRedundantConstraints_ = true;
  boolean hasFeasibilityJumpIgnoreRedundantConstraints_;
  boolean lnsIgnoreRedundantConstraints_ = true;
  boolean hasLnsIgnoreRedundantConstraints_;
  int numViolationLs_ = 0;
  boolean hasNumViolationLs_;
  int violationLsPerturbationPeriod_ = 100;
  boolean hasViolationLsPerturbationPeriod_;
  int sharedTreeNumWorkers_ = -1;
  boolean hasSharedTreeNumWorkers_;
  boolean useSharedTreeSearch_ = false;
  boolean hasUseSharedTreeSearch_;
  int sharedTreeWorkerMinRestartsPerSubtree_ = 1;
  boolean hasSharedTreeWorkerMinRestartsPerSubtree_;
  boolean sharedTreeWorkerEnableTrailSharing_ = true;
  boolean hasSharedTreeWorkerEnableTrailSharing_;
  boolean sharedTreeWorkerEnablePhaseSharing_ = true;
  boolean hasSharedTreeWorkerEnablePhaseSharing_;
  boolean sharedTreeWorkerPropagateIntermediateNodes_ = false;
  boolean hasSharedTreeWorkerPropagateIntermediateNodes_;
  double sharedTreeOpenLeavesPerWorker_ = 2.0;
  boolean hasSharedTreeOpenLeavesPerWorker_;
  int sharedTreeMaxNodesPerWorker_ = 10000;
  boolean hasSharedTreeMaxNodesPerWorker_;
  int sharedTreeSplitStrategy_ = 0;
  boolean hasSharedTreeSplitStrategy_;
  int sharedTreeBalanceTolerance_ = 1;
  boolean hasSharedTreeBalanceTolerance_;
  double sharedTreeSplitMinDtime_ = 0.1;
  boolean hasSharedTreeSplitMinDtime_;
  boolean enumerateAllSolutions_ = false;
  boolean hasEnumerateAllSolutions_;
  boolean keepAllFeasibleSolutionsInPresolve_ = false;
  boolean hasKeepAllFeasibleSolutionsInPresolve_;
  boolean fillTightenedDomainsInResponse_ = false;
  boolean hasFillTightenedDomainsInResponse_;
  boolean fillAdditionalSolutionsInResponse_ = false;
  boolean hasFillAdditionalSolutionsInResponse_;
  boolean instantiateAllVariables_ = true;
  boolean hasInstantiateAllVariables_;
  boolean autoDetectGreaterThanAtLeastOneOf_ = true;
  boolean hasAutoDetectGreaterThanAtLeastOneOf_;
  boolean stopAfterFirstSolution_ = false;
  boolean hasStopAfterFirstSolution_;
  boolean stopAfterPresolve_ = false;
  boolean hasStopAfterPresolve_;
  boolean stopAfterRootPropagation_ = false;
  boolean hasStopAfterRootPropagation_;
  double lnsInitialDifficulty_ = 0.5;
  boolean hasLnsInitialDifficulty_;
  double lnsInitialDeterministicLimit_ = 0.1;
  boolean hasLnsInitialDeterministicLimit_;
  boolean useLns_ = true;
  boolean hasUseLns_;
  boolean useLnsOnly_ = false;
  boolean hasUseLnsOnly_;
  int solutionPoolSize_ = 3;
  boolean hasSolutionPoolSize_;
  int solutionPoolDiversityLimit_ = 10;
  boolean hasSolutionPoolDiversityLimit_;
  int alternativePoolSize_ = 1;
  boolean hasAlternativePoolSize_;
  boolean useRinsLns_ = true;
  boolean hasUseRinsLns_;
  boolean useFeasibilityPump_ = true;
  boolean hasUseFeasibilityPump_;
  boolean useLbRelaxLns_ = true;
  boolean hasUseLbRelaxLns_;
  int lbRelaxNumWorkersThreshold_ = 16;
  boolean hasLbRelaxNumWorkersThreshold_;
  int fpRounding_ = 2;
  boolean hasFpRounding_;
  boolean diversifyLnsParams_ = false;
  boolean hasDiversifyLnsParams_;
  boolean randomizeSearch_ = false;
  boolean hasRandomizeSearch_;
  long searchRandomVariablePoolSize_ = 0L;
  boolean hasSearchRandomVariablePoolSize_;
  boolean pushAllTasksTowardStart_ = false;
  boolean hasPushAllTasksTowardStart_;
  boolean useOptionalVariables_ = false;
  boolean hasUseOptionalVariables_;
  boolean useExactLpReason_ = true;
  boolean hasUseExactLpReason_;
  boolean useCombinedNoOverlap_ = false;
  boolean hasUseCombinedNoOverlap_;
  int atMostOneMaxExpansionSize_ = 3;
  boolean hasAtMostOneMaxExpansionSize_;
  boolean catchSigintSignal_ = true;
  boolean hasCatchSigintSignal_;
  boolean useImpliedBounds_ = true;
  boolean hasUseImpliedBounds_;
  boolean polishLpSolution_ = false;
  boolean hasPolishLpSolution_;
  double lpPrimalTolerance_ = 1e-7;
  boolean hasLpPrimalTolerance_;
  double lpDualTolerance_ = 1e-7;
  boolean hasLpDualTolerance_;
  boolean convertIntervals_ = true;
  boolean hasConvertIntervals_;
  int symmetryLevel_ = 2;
  boolean hasSymmetryLevel_;
  boolean useSymmetryInLp_ = false;
  boolean hasUseSymmetryInLp_;
  boolean keepSymmetryInPresolve_ = false;
  boolean hasKeepSymmetryInPresolve_;
  double symmetryDetectionDeterministicTimeLimit_ = 1.0;
  boolean hasSymmetryDetectionDeterministicTimeLimit_;
  boolean newLinearPropagation_ = true;
  boolean hasNewLinearPropagation_;
  int linearSplitSize_ = 100;
  boolean hasLinearSplitSize_;
  int linearizationLevel_ = 1;
  boolean hasLinearizationLevel_;
  int booleanEncodingLevel_ = 1;
  boolean hasBooleanEncodingLevel_;
  int maxDomainSizeWhenEncodingEqNeqConstraints_ = 16;
  boolean hasMaxDomainSizeWhenEncodingEqNeqConstraints_;
  int maxNumCuts_ = 10000;
  boolean hasMaxNumCuts_;
  int cutLevel_ = 1;
  boolean hasCutLevel_;
  boolean onlyAddCutsAtLevelZero_ = false;
  boolean hasOnlyAddCutsAtLevelZero_;
  boolean addObjectiveCut_ = false;
  boolean hasAddObjectiveCut_;
  boolean addCgCuts_ = true;
  boolean hasAddCgCuts_;
  boolean addMirCuts_ = true;
  boolean hasAddMirCuts_;
  boolean addZeroHalfCuts_ = true;
  boolean hasAddZeroHalfCuts_;
  boolean addCliqueCuts_ = true;
  boolean hasAddCliqueCuts_;
  boolean addRltCuts_ = true;
  boolean hasAddRltCuts_;
  int maxAllDiffCutSize_ = 64;
  boolean hasMaxAllDiffCutSize_;
  boolean addLinMaxCuts_ = true;
  boolean hasAddLinMaxCuts_;
  int maxIntegerRoundingScaling_ = 600;
  boolean hasMaxIntegerRoundingScaling_;
  boolean addLpConstraintsLazily_ = true;
  boolean hasAddLpConstraintsLazily_;
  int rootLpIterations_ = 2000;
  boolean hasRootLpIterations_;
  double minOrthogonalityForLpConstraints_ = 0.05;
  boolean hasMinOrthogonalityForLpConstraints_;
  int maxCutRoundsAtLevelZero_ = 1;
  boolean hasMaxCutRoundsAtLevelZero_;
  int maxConsecutiveInactiveCount_ = 100;
  boolean hasMaxConsecutiveInactiveCount_;
  double cutMaxActiveCountValue_ = 1e10;
  boolean hasCutMaxActiveCountValue_;
  double cutActiveCountDecay_ = 0.8;
  boolean hasCutActiveCountDecay_;
  int cutCleanupTarget_ = 1000;
  boolean hasCutCleanupTarget_;
  int newConstraintsBatchSize_ = 50;
  boolean hasNewConstraintsBatchSize_;
  boolean exploitIntegerLpSolution_ = true;
  boolean hasExploitIntegerLpSolution_;
  boolean exploitAllLpSolution_ = true;
  boolean hasExploitAllLpSolution_;
  boolean exploitBestSolution_ = false;
  boolean hasExploitBestSolution_;
  boolean exploitRelaxationSolution_ = false;
  boolean hasExploitRelaxationSolution_;
  boolean exploitObjective_ = true;
  boolean hasExploitObjective_;
  boolean detectLinearizedProduct_ = false;
  boolean hasDetectLinearizedProduct_;
  boolean useNewIntegerConflictResolution_ = true;
  boolean hasUseNewIntegerConflictResolution_;
  boolean create1UipBooleanDuringIcr_ = true;
  boolean hasCreate1UipBooleanDuringIcr_;
  double mipMaxBound_ = 1e7;
  boolean hasMipMaxBound_;
  double mipVarScaling_ = 1.0;
  boolean hasMipVarScaling_;
  boolean mipScaleLargeDomain_ = false;
  boolean hasMipScaleLargeDomain_;
  boolean mipAutomaticallyScaleVariables_ = true;
  boolean hasMipAutomaticallyScaleVariables_;
  boolean mipScaleToMaxBound_ = false;
  boolean hasMipScaleToMaxBound_;
  boolean mipPresolveRestrictBounds_ = true;
  boolean hasMipPresolveRestrictBounds_;
  boolean onlySolveIp_ = false;
  boolean hasOnlySolveIp_;
  double mipWantedPrecision_ = 1e-6;
  boolean hasMipWantedPrecision_;
  int mipMaxActivityExponent_ = 53;
  boolean hasMipMaxActivityExponent_;
  double mipCheckPrecision_ = 1e-5;
  boolean hasMipCheckPrecision_;
  boolean mipComputeTrueObjectiveBound_ = true;
  boolean hasMipComputeTrueObjectiveBound_;
  double mipMaxValidMagnitude_ = 1e20;
  boolean hasMipMaxValidMagnitude_;
  boolean mipTreatHighMagnitudeBoundsAsInfinity_ = false;
  boolean hasMipTreatHighMagnitudeBoundsAsInfinity_;
  double mipDropTolerance_ = 1e-16;
  boolean hasMipDropTolerance_;
  int mipPresolveLevel_ = 2;
  boolean hasMipPresolveLevel_;
  boolean mipUseTightBounds_ = true;
  boolean hasMipUseTightBounds_;
  double mipTightPrecision_ = 1e-9;
  boolean hasMipTightPrecision_;
  boolean mipVerifySolution_ = false;
  boolean hasMipVerifySolution_;
  boolean mipProperRelaxation_ = false;
  boolean hasMipProperRelaxation_;

  private SatParameters() {}

  /** Returns the immutable default instance (all fields unset). */
  public static SatParameters getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public SatParameters getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns true if {@code name} is set. */
  public boolean hasName() {
    return hasName_;
  }

  /**
   * <pre>
   * In some context, like in a portfolio of search, it makes sense to name a
   * given parameters set for logging purpose.
   * </pre>
   */
  public String getName() {
    return name_;
  }

  /** Returns true if {@code preferred_variable_order} is set. */
  public boolean hasPreferredVariableOrder() {
    return hasPreferredVariableOrder_;
  }

  /** Returns {@code preferred_variable_order}. */
  public SatParameters.VariableOrder getPreferredVariableOrder() {
    SatParameters.VariableOrder result = SatParameters.VariableOrder.forNumber(preferredVariableOrder_);
    return result == null ? SatParameters.VariableOrder.forNumber(0) : result;
  }

  /** Returns the number of {@code preferred_variable_order}. */
  public int getPreferredVariableOrderValue() {
    return preferredVariableOrder_;
  }

  /** Returns true if {@code initial_polarity} is set. */
  public boolean hasInitialPolarity() {
    return hasInitialPolarity_;
  }

  /** Returns {@code initial_polarity}. */
  public SatParameters.Polarity getInitialPolarity() {
    SatParameters.Polarity result = SatParameters.Polarity.forNumber(initialPolarity_);
    return result == null ? SatParameters.Polarity.forNumber(1) : result;
  }

  /** Returns the number of {@code initial_polarity}. */
  public int getInitialPolarityValue() {
    return initialPolarity_;
  }

  /** Returns true if {@code use_phase_saving} is set. */
  public boolean hasUsePhaseSaving() {
    return hasUsePhaseSaving_;
  }

  /**
   * <pre>
   * If this is true, then the polarity of a variable will be the last value it
   * was assigned to, or its default polarity if it was never assigned since the
   * call to ResetDecisionHeuristic().
   *
   * Actually, we use a newer version where we follow the last value in the
   * longest non-conflicting partial assignment in the current phase.
   *
   * This is called 'literal phase saving'. For details see 'A Lightweight
   * Component Caching Scheme for Satisfiability Solvers' K. Pipatsrisawat and
   * A.Darwiche, In 10th International Conference on Theory and Applications of
   * Satisfiability Testing, 2007.
   * </pre>
   */
  public boolean getUsePhaseSaving() {
    return usePhaseSaving_;
  }

  /** Returns true if {@code polarity_rephase_increment} is set. */
  public boolean hasPolarityRephaseIncrement() {
    return hasPolarityRephaseIncrement_;
  }

  /**
   * <pre>
   * If non-zero, then we change the polarity heuristic after that many number
   * of conflicts in an arithmetically increasing fashion. So x the first time,
   * 2 * x the second time, etc...
   * </pre>
   */
  public int getPolarityRephaseIncrement() {
    return polarityRephaseIncrement_;
  }

  /** Returns true if {@code polarity_exploit_ls_hints} is set. */
  public boolean hasPolarityExploitLsHints() {
    return hasPolarityExploitLsHints_;
  }

  /**
   * <pre>
   * If true and we have first solution LS workers, tries in some phase to
   * follow a LS solutions that violates has litle constraints as possible.
   * </pre>
   */
  public boolean getPolarityExploitLsHints() {
    return polarityExploitLsHints_;
  }

  /** Returns true if {@code random_polarity_ratio} is set. */
  public boolean hasRandomPolarityRatio() {
    return hasRandomPolarityRatio_;
  }

  /**
   * <pre>
   * The proportion of polarity chosen at random. Note that this take
   * precedence over the phase saving heuristic. This is different from
   * initial_polarity:POLARITY_RANDOM because it will select a new random
   * polarity each time the variable is branched upon instead of selecting one
   * initially and then always taking this choice.
   * </pre>
   */
  public double getRandomPolarityRatio() {
    return randomPolarityRatio_;
  }

  /** Returns true if {@code random_branches_ratio} is set. */
  public boolean hasRandomBranchesRatio() {
    return hasRandomBranchesRatio_;
  }

  /**
   * <pre>
   * A number between 0 and 1 that indicates the proportion of branching
   * variables that are selected randomly instead of choosing the first variable
   * from the given variable_ordering strategy.
   * </pre>
   */
  public double getRandomBranchesRatio() {
    return randomBranchesRatio_;
  }

  /** Returns true if {@code use_erwa_heuristic} is set. */
  public boolean hasUseErwaHeuristic() {
    return hasUseErwaHeuristic_;
  }

  /**
   * <pre>
   * Whether we use the ERWA (Exponential Recency Weighted Average) heuristic as
   * described in "Learning Rate Based Branching Heuristic for SAT solvers",
   * J.H.Liang, V. Ganesh, P. Poupart, K.Czarnecki, SAT 2016.
   * </pre>
   */
  public boolean getUseErwaHeuristic() {
    return useErwaHeuristic_;
  }

  /** Returns true if {@code initial_variables_activity} is set. */
  public boolean hasInitialVariablesActivity() {
    return hasInitialVariablesActivity_;
  }

  /**
   * <pre>
   * The initial value of the variables activity. A non-zero value only make
   * sense when use_erwa_heuristic is true. Experiments with a value of 1e-2
   * together with the ERWA heuristic showed slighthly better result than simply
   * using zero. The idea is that when the "learning rate" of a variable becomes
   * lower than this value, then we prefer to branch on never explored before
   * variables. This is not in the ERWA paper.
   * </pre>
   */
  public double getInitialVariablesActivity() {
    return initialVariablesActivity_;
  }

  /** Returns true if {@code also_bump_variables_in_conflict_reasons} is set. */
  public boolean hasAlsoBumpVariablesInConflictReasons() {
    return hasAlsoBumpVariablesInConflictReasons_;
  }

  /**
   * <pre>
   * When this is true, then the variables that appear in any of the reason of
   * the variables in a conflict have their activity bumped. This is addition to
   * the variables in the conflict, and the one that were used during conflict
   * resolution.
   * </pre>
   */
  public boolean getAlsoBumpVariablesInConflictReasons() {
    return alsoBumpVariablesInConflictReasons_;
  }

  /** Returns true if {@code minimization_algorithm} is set. */
  public boolean hasMinimizationAlgorithm() {
    return hasMinimizationAlgorithm_;
  }

  /** Returns {@code minimization_algorithm}. */
  public SatParameters.ConflictMinimizationAlgorithm getMinimizationAlgorithm() {
    SatParameters.ConflictMinimizationAlgorithm result = SatParameters.ConflictMinimizationAlgorithm.forNumber(minimizationAlgorithm_);
    return result == null ? SatParameters.ConflictMinimizationAlgorithm.forNumber(2) : result;
  }

  /** Returns the number of {@code minimization_algorithm}. */
  public int getMinimizationAlgorithmValue() {
    return minimizationAlgorithm_;
  }

  /** Returns true if {@code binary_minimization_algorithm} is set. */
  public boolean hasBinaryMinimizationAlgorithm() {
    return hasBinaryMinimizationAlgorithm_;
  }

  /** Returns {@code binary_minimization_algorithm}. */
  public SatParameters.BinaryMinizationAlgorithm getBinaryMinimizationAlgorithm() {
    SatParameters.BinaryMinizationAlgorithm result = SatParameters.BinaryMinizationAlgorithm.forNumber(binaryMinimizationAlgorithm_);
    return result == null ? SatParameters.BinaryMinizationAlgorithm.forNumber(5) : result;
  }

  /** Returns the number of {@code binary_minimization_algorithm}. */
  public int getBinaryMinimizationAlgorithmValue() {
    return binaryMinimizationAlgorithm_;
  }

  /** Returns true if {@code subsumption_during_conflict_analysis} is set. */
  public boolean hasSubsumptionDuringConflictAnalysis() {
    return hasSubsumptionDuringConflictAnalysis_;
  }

  /**
   * <pre>
   * At a really low cost, during the 1-UIP conflict computation, it is easy to
   * detect if some of the involved reasons are subsumed by the current
   * conflict. When this is true, such clauses are detached and later removed
   * from the problem.
   * </pre>
   */
  public boolean getSubsumptionDuringConflictAnalysis() {
    return subsumptionDuringConflictAnalysis_;
  }

  /** Returns true if {@code extra_subsumption_during_conflict_analysis} is set. */
  public boolean hasExtraSubsumptionDuringConflictAnalysis() {
    return hasExtraSubsumptionDuringConflictAnalysis_;
  }

  /**
   * <pre>
   * It is possible that "intermediate" clauses during conflict resolution
   * subsumes some of the clauses that propagated. This is quite cheap to detect
   * and result in more subsumption/strengthening of clauses.
   * </pre>
   */
  public boolean getExtraSubsumptionDuringConflictAnalysis() {
    return extraSubsumptionDuringConflictAnalysis_;
  }

  /** Returns true if {@code decision_subsumption_during_conflict_analysis} is set. */
  public boolean hasDecisionSubsumptionDuringConflictAnalysis() {
    return hasDecisionSubsumptionDuringConflictAnalysis_;
  }

  /**
   * <pre>
   * Try even more subsumption options during conflict analysis.
   * </pre>
   */
  public boolean getDecisionSubsumptionDuringConflictAnalysis() {
    return decisionSubsumptionDuringConflictAnalysis_;
  }

  /** Returns true if {@code eagerly_subsume_last_n_conflicts} is set. */
  public boolean hasEagerlySubsumeLastNConflicts() {
    return hasEagerlySubsumeLastNConflicts_;
  }

  /**
   * <pre>
   * If &gt;=0, each time we have a conflict, we try to subsume the last n learned
   * clause with it.
   * </pre>
   */
  public int getEagerlySubsumeLastNConflicts() {
    return eagerlySubsumeLastNConflicts_;
  }

  /** Returns true if {@code subsume_during_vivification} is set. */
  public boolean hasSubsumeDuringVivification() {
    return hasSubsumeDuringVivification_;
  }

  /**
   * <pre>
   * If we remove clause that we now are "implied" by others. Note that this
   * might not always be good as we might loose some propagation power.
   * </pre>
   */
  public boolean getSubsumeDuringVivification() {
    return subsumeDuringVivification_;
  }

  /** Returns true if {@code use_chronological_backtracking} is set. */
  public boolean hasUseChronologicalBacktracking() {
    return hasUseChronologicalBacktracking_;
  }

  /**
   * <pre>
   * If true, try to backtrack as little as possible on conflict and re-imply
   * the clauses later.
   * This means we discard less propagation than traditional backjumping, but
   * requites additional bookkeeping to handle reimplication.
   * See: https://doi.org/10.1007/978-3-319-94144-8_7
   * </pre>
   */
  public boolean getUseChronologicalBacktracking() {
    return useChronologicalBacktracking_;
  }

  /** Returns true if {@code max_backjump_levels} is set. */
  public boolean hasMaxBackjumpLevels() {
    return hasMaxBackjumpLevels_;
  }

  /**
   * <pre>
   * If chronological backtracking is enabled, this is the maximum number of
   * levels we will backjump over, otherwise we will backtrack.
   * </pre>
   */
  public int getMaxBackjumpLevels() {
    return maxBackjumpLevels_;
  }

  /** Returns true if {@code chronological_backtrack_min_conflicts} is set. */
  public boolean hasChronologicalBacktrackMinConflicts() {
    return hasChronologicalBacktrackMinConflicts_;
  }

  /**
   * <pre>
   * If chronological backtracking is enabled, this is the minimum number of
   * conflicts before we will consider backjumping.
   * </pre>
   */
  public int getChronologicalBacktrackMinConflicts() {
    return chronologicalBacktrackMinConflicts_;
  }

  /** Returns true if {@code learn_on_assumptions_unsat} is set. */
  public boolean hasLearnOnAssumptionsUnsat() {
    return hasLearnOnAssumptionsUnsat_;
  }

  /**
   * <pre>
   * Advanced usage. When assumptions are used and ASSUMPTION_UNSAT is returned,
   * the solver will try to learn something if possible. However, to do this,
   * the current state of the solver will be backtracked, which make it
   * impossible to do custom analysis of the last conflict. It is possible to
   * turn that off, so that on ASSUMPTION_UNSAT, the trail is left untouched.
   * </pre>
   */
  public boolean getLearnOnAssumptionsUnsat() {
    return learnOnAssumptionsUnsat_;
  }

  /** Returns true if {@code clause_cleanup_period} is set. */
  public boolean hasClauseCleanupPeriod() {
    return hasClauseCleanupPeriod_;
  }

  /**
   * <pre>
   * Trigger a cleanup when this number of "deletable" clauses is learned.
   * </pre>
   */
  public int getClauseCleanupPeriod() {
    return clauseCleanupPeriod_;
  }

  /** Returns true if {@code clause_cleanup_period_increment} is set. */
  public boolean hasClauseCleanupPeriodIncrement() {
    return hasClauseCleanupPeriodIncrement_;
  }

  /**
   * <pre>
   * Increase clause_cleanup_period by this amount after each cleanup.
   * </pre>
   */
  public int getClauseCleanupPeriodIncrement() {
    return clauseCleanupPeriodIncrement_;
  }

  /** Returns true if {@code clause_cleanup_target} is set. */
  public boolean hasClauseCleanupTarget() {
    return hasClauseCleanupTarget_;
  }

  /**
   * <pre>
   * During a cleanup, we will always keep that number of "deletable" clauses.
   * Note that this doesn't include the "protected" clauses.
   * </pre>
   */
  public int getClauseCleanupTarget() {
    return clauseCleanupTarget_;
  }

  /** Returns true if {@code clause_cleanup_ratio} is set. */
  public boolean hasClauseCleanupRatio() {
    return hasClauseCleanupRatio_;
  }

  /**
   * <pre>
   * During a cleanup, if clause_cleanup_target is 0, we will keep the
   * clause_cleanup_ratio of "deletable" clauses instead of aiming for a fixed
   * target of clauses to keep.
   * </pre>
   */
  public double getClauseCleanupRatio() {
    return clauseCleanupRatio_;
  }

  /** Returns true if {@code clause_cleanup_lbd_bound} is set. */
  public boolean hasClauseCleanupLbdBound() {
    return hasClauseCleanupLbdBound_;
  }

  /**
   * <pre>
   * All the clauses with a LBD (literal blocks distance) lower or equal to this
   * parameters will always be kept.
   *
   * Note that the LBD of a clause that just propagated is 1 + number of
   * different decision levels of its literals. So that the "classic" LBD of a
   * learned conflict is the same as its LBD when we backjump and then propagate
   * it.
   * </pre>
   */
  public int getClauseCleanupLbdBound() {
    return clauseCleanupLbdBound_;
  }

  /** Returns true if {@code clause_cleanup_lbd_tier1} is set. */
  public boolean hasClauseCleanupLbdTier1() {
    return hasClauseCleanupLbdTier1_;
  }

  /**
   * <pre>
   * All the clause with a LBD lower or equal to this will be kept except if
   * its activity hasn't been bumped in the last 32 cleanup phase. Note that
   * this has no effect if it is &lt;= clause_cleanup_lbd_bound.
   * </pre>
   */
  public int getClauseCleanupLbdTier1() {
    return clauseCleanupLbdTier1_;
  }

  /** Returns true if {@code clause_cleanup_lbd_tier2} is set. */
  public boolean hasClauseCleanupLbdTier2() {
    return hasClauseCleanupLbdTier2_;
  }

  /**
   * <pre>
   * All the clause with a LBD lower or equal to this will be kept except if its
   * activity hasn't been bumped since the previous cleanup phase. Note that
   * this has no effect if it is &lt;= clause_cleanup_lbd_bound or &lt;=
   * clause_cleanup_lbd_tier1.
   * </pre>
   */
  public int getClauseCleanupLbdTier2() {
    return clauseCleanupLbdTier2_;
  }

  /** Returns true if {@code clause_cleanup_ordering} is set. */
  public boolean hasClauseCleanupOrdering() {
    return hasClauseCleanupOrdering_;
  }

  /** Returns {@code clause_cleanup_ordering}. */
  public SatParameters.ClauseOrdering getClauseCleanupOrdering() {
    SatParameters.ClauseOrdering result = SatParameters.ClauseOrdering.forNumber(clauseCleanupOrdering_);
    return result == null ? SatParameters.ClauseOrdering.forNumber(0) : result;
  }

  /** Returns the number of {@code clause_cleanup_ordering}. */
  public int getClauseCleanupOrderingValue() {
    return clauseCleanupOrdering_;
  }

  /** Returns true if {@code pb_cleanup_increment} is set. */
  public boolean hasPbCleanupIncrement() {
    return hasPbCleanupIncrement_;
  }

  /**
   * <pre>
   * Same as for the clauses, but for the learned pseudo-Boolean constraints.
   * </pre>
   */
  public int getPbCleanupIncrement() {
    return pbCleanupIncrement_;
  }

  /** Returns true if {@code pb_cleanup_ratio} is set. */
  public boolean hasPbCleanupRatio() {
    return hasPbCleanupRatio_;
  }

  /** Returns {@code pb_cleanup_ratio}. */
  public double getPbCleanupRatio() {
    return pbCleanupRatio_;
  }

  /** Returns true if {@code variable_activity_decay} is set. */
  public boolean hasVariableActivityDecay() {
    return hasVariableActivityDecay_;
  }

  /**
   * <pre>
   * Each time a conflict is found, the activities of some variables are
   * increased by one. Then, the activity of all variables are multiplied by
   * variable_activity_decay.
   *
   * To implement this efficiently, the activity of all the variables is not
   * decayed at each conflict. Instead, the activity increment is multiplied by
   * 1 / decay. When an activity reach max_variable_activity_value, all the
   * activity are multiplied by 1 / max_variable_activity_value.
   * </pre>
   */
  public double getVariableActivityDecay() {
    return variableActivityDecay_;
  }

  /** Returns true if {@code max_variable_activity_value} is set. */
  public boolean hasMaxVariableActivityValue() {
    return hasMaxVariableActivityValue_;
  }

  /** Returns {@code max_variable_activity_value}. */
  public double getMaxVariableActivityValue() {
    return maxVariableActivityValue_;
  }

  /** Returns true if {@code glucose_max_decay} is set. */
  public boolean hasGlucoseMaxDecay() {
    return hasGlucoseMaxDecay_;
  }

  /**
   * <pre>
   * The activity starts at 0.8 and increment by 0.01 every 5000 conflicts until
   * 0.95. This "hack" seems to work well and comes from:
   *
   * Glucose 2.3 in the SAT 2013 Competition - SAT Competition 2013
   * http://edacc4.informatik.uni-ulm.de/SC13/solver-description-download/136
   * </pre>
   */
  public double getGlucoseMaxDecay() {
    return glucoseMaxDecay_;
  }

  /** Returns true if {@code glucose_decay_increment} is set. */
  public boolean hasGlucoseDecayIncrement() {
    return hasGlucoseDecayIncrement_;
  }

  /** Returns {@code glucose_decay_increment}. */
  public double getGlucoseDecayIncrement() {
    return glucoseDecayIncrement_;
  }

  /** Returns true if {@code glucose_decay_increment_period} is set. */
  public boolean hasGlucoseDecayIncrementPeriod() {
    return hasGlucoseDecayIncrementPeriod_;
  }

  /** Returns {@code glucose_decay_increment_period}. */
  public int getGlucoseDecayIncrementPeriod() {
    return glucoseDecayIncrementPeriod_;
  }

  /** Returns true if {@code clause_activity_decay} is set. */
  public boolean hasClauseActivityDecay() {
    return hasClauseActivityDecay_;
  }

  /**
   * <pre>
   * Clause activity parameters (same effect as the one on the variables).
   * </pre>
   */
  public double getClauseActivityDecay() {
    return clauseActivityDecay_;
  }

  /** Returns true if {@code max_clause_activity_value} is set. */
  public boolean hasMaxClauseActivityValue() {
    return hasMaxClauseActivityValue_;
  }

  /** Returns {@code max_clause_activity_value}. */
  public double getMaxClauseActivityValue() {
    return maxClauseActivityValue_;
  }

  /**
   * <pre>
   * The restart strategies will change each time the strategy_counter is
   * increased. The current strategy will simply be the one at index
   * strategy_counter modulo the number of strategy. Note that if this list
   * includes a NO_RESTART, nothing will change when it is reached because the
   * strategy_counter will only increment after a restart.
   *
   * The idea of switching of search strategy tailored for SAT/UNSAT comes from
   * Chanseok Oh with his COMiniSatPS solver, see http://cs.nyu.edu/~chanseok/.
   * But more generally, it seems REALLY beneficial to try different strategy.
   * </pre>
   */
  public java.util.List<SatParameters.RestartAlgorithm> getRestartAlgorithmsList() {
    return restartAlgorithms_ == null ? java.util.Collections.<SatParameters.RestartAlgorithm>emptyList() : java.util.Collections.unmodifiableList(restartAlgorithms_);
  }

  /** Returns the number of elements of {@code restart_algorithms}. */
  public int getRestartAlgorithmsCount() {
    return restartAlgorithms_ == null ? 0 : restartAlgorithms_.size();
  }

  /** Returns the element {@code index} of {@code restart_algorithms}. */
  public SatParameters.RestartAlgorithm getRestartAlgorithms(int index) {
    if (restartAlgorithms_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return restartAlgorithms_.get(index);
  }

  /** Returns true if {@code default_restart_algorithms} is set. */
  public boolean hasDefaultRestartAlgorithms() {
    return hasDefaultRestartAlgorithms_;
  }

  /** Returns {@code default_restart_algorithms}. */
  public String getDefaultRestartAlgorithms() {
    return defaultRestartAlgorithms_;
  }

  /** Returns true if {@code restart_period} is set. */
  public boolean hasRestartPeriod() {
    return hasRestartPeriod_;
  }

  /**
   * <pre>
   * Restart period for the FIXED_RESTART strategy. This is also the multiplier
   * used by the LUBY_RESTART strategy.
   * </pre>
   */
  public int getRestartPeriod() {
    return restartPeriod_;
  }

  /** Returns true if {@code restart_running_window_size} is set. */
  public boolean hasRestartRunningWindowSize() {
    return hasRestartRunningWindowSize_;
  }

  /**
   * <pre>
   * Size of the window for the moving average restarts.
   * </pre>
   */
  public int getRestartRunningWindowSize() {
    return restartRunningWindowSize_;
  }

  /** Returns true if {@code restart_dl_average_ratio} is set. */
  public boolean hasRestartDlAverageRatio() {
    return hasRestartDlAverageRatio_;
  }

  /**
   * <pre>
   * In the moving average restart algorithms, a restart is triggered if the
   * window average times this ratio is greater that the global average.
   * </pre>
   */
  public double getRestartDlAverageRatio() {
    return restartDlAverageRatio_;
  }

  /** Returns true if {@code restart_lbd_average_ratio} is set. */
  public boolean hasRestartLbdAverageRatio() {
    return hasRestartLbdAverageRatio_;
  }

  /** Returns {@code restart_lbd_average_ratio}. */
  public double getRestartLbdAverageRatio() {
    return restartLbdAverageRatio_;
  }

  /** Returns true if {@code use_blocking_restart} is set. */
  public boolean hasUseBlockingRestart() {
    return hasUseBlockingRestart_;
  }

  /**
   * <pre>
   * Block a moving restart algorithm if the trail size of the current conflict
   * is greater than the multiplier times the moving average of the trail size
   * at the previous conflicts.
   * </pre>
   */
  public boolean getUseBlockingRestart() {
    return useBlockingRestart_;
  }

  /** Returns true if {@code blocking_restart_window_size} is set. */
  public boolean hasBlockingRestartWindowSize() {
    return hasBlockingRestartWindowSize_;
  }

  /** Returns {@code blocking_restart_window_size}. */
  public int getBlockingRestartWindowSize() {
    return blockingRestartWindowSize_;
  }

  /** Returns true if {@code blocking_restart_multiplier} is set. */
  public boolean hasBlockingRestartMultiplier() {
    return hasBlockingRestartMultiplier_;
  }

  /** Returns {@code blocking_restart_multiplier}. */
  public double getBlockingRestartMultiplier() {
    return blockingRestartMultiplier_;
  }

  /** Returns true if {@code num_conflicts_before_strategy_changes} is set. */
  public boolean hasNumConflictsBeforeStrategyChanges() {
    return hasNumConflictsBeforeStrategyChanges_;
  }

  /**
   * <pre>
   * After each restart, if the number of conflict since the last strategy
   * change is greater that this, then we increment a "strategy_counter" that
   * can be use to change the search strategy used by the following restarts.
   * </pre>
   */
  public int getNumConflictsBeforeStrategyChanges() {
    return numConflictsBeforeStrategyChanges_;
  }

  /** Returns true if {@code strategy_change_increase_ratio} is set. */
  public boolean hasStrategyChangeIncreaseRatio() {
    return hasStrategyChangeIncreaseRatio_;
  }

  /**
   * <pre>
   * The parameter num_conflicts_before_strategy_changes is increased by that
   * much after each strategy change.
   * </pre>
   */
  public double getStrategyChangeIncreaseRatio() {
    return strategyChangeIncreaseRatio_;
  }

  /** Returns true if {@code max_time_in_seconds} is set. */
  public boolean hasMaxTimeInSeconds() {
    return hasMaxTimeInSeconds_;
  }

  /**
   * <pre>
   * Maximum time allowed in seconds to solve a problem.
   * The counter will starts at the beginning of the Solve() call.
   * </pre>
   */
  public double getMaxTimeInSeconds() {
    return maxTimeInSeconds_;
  }

  /** Returns true if {@code max_deterministic_time} is set. */
  public boolean hasMaxDeterministicTime() {
    return hasMaxDeterministicTime_;
  }

  /**
   * <pre>
   * Maximum time allowed in deterministic time to solve a problem.
   * The deterministic time should be correlated with the real time used by the
   * solver, the time unit being as close as possible to a second.
   * </pre>
   */
  public double getMaxDeterministicTime() {
    return maxDeterministicTime_;
  }

  /** Returns true if {@code max_num_deterministic_batches} is set. */
  public boolean hasMaxNumDeterministicBatches() {
    return hasMaxNumDeterministicBatches_;
  }

  /**
   * <pre>
   * Stops after that number of batches has been scheduled. This only make sense
   * when interleave_search is true.
   * </pre>
   */
  public int getMaxNumDeterministicBatches() {
    return maxNumDeterministicBatches_;
  }

  /** Returns true if {@code max_number_of_conflicts} is set. */
  public boolean hasMaxNumberOfConflicts() {
    return hasMaxNumberOfConflicts_;
  }

  /**
   * <pre>
   * Maximum number of conflicts allowed to solve a problem.
   *
   * TODO(user): Maybe change the way the conflict limit is enforced?
   * currently it is enforced on each independent internal SAT solve, rather
   * than on the overall number of conflicts across all solves. So in the
   * context of an optimization problem, this is not really usable directly by a
   * client.
   * kint64max
   * </pre>
   */
  public long getMaxNumberOfConflicts() {
    return maxNumberOfConflicts_;
  }

  /** Returns true if {@code max_memory_in_mb} is set. */
  public boolean hasMaxMemoryInMb() {
    return hasMaxMemoryInMb_;
  }

  /**
   * <pre>
   * Maximum memory allowed for the whole thread containing the solver. The
   * solver will abort as soon as it detects that this limit is crossed. As a
   * result, this limit is approximative, but usually the solver will not go too
   * much over.
   *
   * TODO(user): This is only used by the pure SAT solver, generalize to CP-SAT.
   * </pre>
   */
  public long getMaxMemoryInMb() {
    return maxMemoryInMb_;
  }

  /** Returns true if {@code absolute_gap_limit} is set. */
  public boolean hasAbsoluteGapLimit() {
    return hasAbsoluteGapLimit_;
  }

  /**
   * <pre>
   * Stop the search when the gap between the best feasible objective (O) and
   * our best objective bound (B) is smaller than a limit.
   * The exact definition is:
   * - Absolute: abs(O - B)
   * - Relative: abs(O - B) / max(1, abs(O)).
   *
   * Important: The relative gap depends on the objective offset! If you
   * artificially shift the objective, you will get widely different value of
   * the relative gap.
   *
   * Note that if the gap is reached, the search status will be OPTIMAL. But
   * one can check the best objective bound to see the actual gap.
   *
   * If the objective is integer, then any absolute gap &lt; 1 will lead to a true
   * optimal. If the objective is floating point, a gap of zero make little
   * sense so is is why we use a non-zero default value. At the end of the
   * search, we will display a warning if OPTIMAL is reported yet the gap is
   * greater than this absolute gap.
   * </pre>
   */
  public double getAbsoluteGapLimit() {
    return absoluteGapLimit_;
  }

  /** Returns true if {@code relative_gap_limit} is set. */
  public boolean hasRelativeGapLimit() {
    return hasRelativeGapLimit_;
  }

  /** Returns {@code relative_gap_limit}. */
  public double getRelativeGapLimit() {
    return relativeGapLimit_;
  }

  /** Returns true if {@code random_seed} is set. */
  public boolean hasRandomSeed() {
    return hasRandomSeed_;
  }

  /**
   * <pre>
   * At the beginning of each solve, the random number generator used in some
   * part of the solver is reinitialized to this seed. If you change the random
   * seed, the solver may make different choices during the solving process.
   *
   * For some problems, the running time may vary a lot depending on small
   * change in the solving algorithm. Running the solver with different seeds
   * enables to have more robust benchmarks when evaluating new features.
   * </pre>
   */
  public int getRandomSeed() {
    return randomSeed_;
  }

  /** Returns true if {@code permute_variable_randomly} is set. */
  public boolean hasPermuteVariableRandomly() {
    return hasPermuteVariableRandomly_;
  }

  /**
   * <pre>
   * This is mainly here to test the solver variability. Note that in tests, if
   * not explicitly set to false, all 3 options will be set to true so that
   * clients do not rely on the solver returning a specific solution if they are
   * many equivalent optimal solutions.
   * </pre>
   */
  public boolean getPermuteVariableRandomly() {
    return permuteVariableRandomly_;
  }

  /** Returns true if {@code permute_presolve_constraint_order} is set. */
  public boolean hasPermutePresolveConstraintOrder() {
    return hasPermutePresolveConstraintOrder_;
  }

  /** Returns {@code permute_presolve_constraint_order}. */
  public boolean getPermutePresolveConstraintOrder() {
    return permutePresolveConstraintOrder_;
  }

  /** Returns true if {@code use_absl_random} is set. */
  public boolean hasUseAbslRandom() {
    return hasUseAbslRandom_;
  }

  /** Returns {@code use_absl_random}. */
  public boolean getUseAbslRandom() {
    return useAbslRandom_;
  }

  /** Returns true if {@code log_search_progress} is set. */
  public boolean hasLogSearchProgress() {
    return hasLogSearchProgress_;
  }

  /**
   * <pre>
   * Whether the solver should log the search progress. This is the maing
   * logging parameter and if this is false, none of the logging (callbacks,
   * log_to_stdout, log_to_response, ...) will do anything.
   * </pre>
   */
  public boolean getLogSearchProgress() {
    return logSearchProgress_;
  }

  /** Returns true if {@code log_subsolver_statistics} is set. */
  public boolean hasLogSubsolverStatistics() {
    return hasLogSubsolverStatistics_;
  }

  /**
   * <pre>
   * Whether the solver should display per sub-solver search statistics.
   * This is only useful is log_search_progress is set to true, and if the
   * number of search workers is &gt; 1. Note that in all case we display a bit
   * of stats with one line per subsolver.
   * </pre>
   */
  public boolean getLogSubsolverStatistics() {
    return logSubsolverStatistics_;
  }

  /** Returns true if {@code log_prefix} is set. */
  public boolean hasLogPrefix() {
    return hasLogPrefix_;
  }

  /**
   * <pre>
   * Add a prefix to all logs.
   * </pre>
   */
  public String getLogPrefix() {
    return logPrefix_;
  }

  /** Returns true if {@code log_to_stdout} is set. */
  public boolean hasLogToStdout() {
    return hasLogToStdout_;
  }

  /**
   * <pre>
   * Log to stdout.
   * </pre>
   */
  public boolean getLogToStdout() {
    return logToStdout_;
  }

  /** Returns true if {@code log_to_response} is set. */
  public boolean hasLogToResponse() {
    return hasLogToResponse_;
  }

  /**
   * <pre>
   * Log to response proto.
   * </pre>
   */
  public boolean getLogToResponse() {
    return logToResponse_;
  }

  /** Returns true if {@code use_pb_resolution} is set. */
  public boolean hasUsePbResolution() {
    return hasUsePbResolution_;
  }

  /**
   * <pre>
   * Experimental.
   *
   * This is an old experiment, it might cause crashes in multi-thread and you
   * should double check the solver result. It can still be used if you only
   * care about feasible solutions (these are checked) and it gives good result
   * on your problem. We might revive it at some point.
   *
   * Whether to use pseudo-Boolean resolution to analyze a conflict. Note that
   * this option only make sense if your problem is modelized using
   * pseudo-Boolean constraints. If you only have clauses, this shouldn't change
   * anything (except slow the solver down).
   * </pre>
   */
  public boolean getUsePbResolution() {
    return usePbResolution_;
  }

  /** Returns true if {@code minimize_reduction_during_pb_resolution} is set. */
  public boolean hasMinimizeReductionDuringPbResolution() {
    return hasMinimizeReductionDuringPbResolution_;
  }

  /**
   * <pre>
   * A different algorithm during PB resolution. It minimizes the number of
   * calls to ReduceCoefficients() which can be time consuming. However, the
   * search space will be different and if the coefficients are large, this may
   * lead to integer overflows that could otherwise be prevented.
   * </pre>
   */
  public boolean getMinimizeReductionDuringPbResolution() {
    return minimizeReductionDuringPbResolution_;
  }

  /** Returns true if {@code count_assumption_levels_in_lbd} is set. */
  public boolean hasCountAssumptionLevelsInLbd() {
    return hasCountAssumptionLevelsInLbd_;
  }

  /**
   * <pre>
   * Whether or not the assumption levels are taken into account during the LBD
   * computation. According to the reference below, not counting them improves
   * the solver in some situation. Note that this only impact solves under
   * assumptions.
   *
   * Gilles Audemard, Jean-Marie Lagniez, Laurent Simon, "Improving Glucose for
   * Incremental SAT Solving with Assumptions: Application to MUS Extraction"
   * Theory and Applications of Satisfiability Testing - SAT 2013, Lecture Notes
   * in Computer Science Volume 7962, 2013, pp 309-317.
   * </pre>
   */
  public boolean getCountAssumptionLevelsInLbd() {
    return countAssumptionLevelsInLbd_;
  }

  /** Returns true if {@code presolve_bve_threshold} is set. */
  public boolean hasPresolveBveThreshold() {
    return hasPresolveBveThreshold_;
  }

  /**
   * <pre>
   * During presolve, only try to perform the bounded variable elimination (BVE)
   * of a variable x if the number of occurrences of x times the number of
   * occurrences of not(x) is not greater than this parameter.
   * </pre>
   */
  public int getPresolveBveThreshold() {
    return presolveBveThreshold_;
  }

  /** Returns true if {@code filter_sat_postsolve_clauses} is set. */
  public boolean hasFilterSatPostsolveClauses() {
    return hasFilterSatPostsolveClauses_;
  }

  /**
   * <pre>
   * Internal parameter. During BVE, if we eliminate a variable x, by default we
   * will push all clauses containing x and all clauses containing not(x) to the
   * postsolve. However, it is possible to write the postsolve code so that only
   * one such set is needed. The idea is that, if we push the set containing a
   * literal l, is to set l to false except if it is needed to satisfy one of
   * the clause in the set. This is always beneficial, but for historical
   * reason, not all our postsolve algorithm support this.
   * </pre>
   */
  public boolean getFilterSatPostsolveClauses() {
    return filterSatPostsolveClauses_;
  }

  /** Returns true if {@code presolve_bve_clause_weight} is set. */
  public boolean hasPresolveBveClauseWeight() {
    return hasPresolveBveClauseWeight_;
  }

  /**
   * <pre>
   * During presolve, we apply BVE only if this weight times the number of
   * clauses plus the number of clause literals is not increased.
   * </pre>
   */
  public int getPresolveBveClauseWeight() {
    return presolveBveClauseWeight_;
  }

  /** Returns true if {@code probing_deterministic_time_limit} is set. */
  public boolean hasProbingDeterministicTimeLimit() {
    return hasProbingDeterministicTimeLimit_;
  }

  /**
   * <pre>
   * The maximum "deterministic" time limit to spend in probing. A value of
   * zero will disable the probing.
   *
   * TODO(user): Clean up. The first one is used in CP-SAT, the other in pure
   * SAT presolve.
   * </pre>
   */
  public double getProbingDeterministicTimeLimit() {
    return probingDeterministicTimeLimit_;
  }

  /** Returns true if {@code presolve_probing_deterministic_time_limit} is set. */
  public boolean hasPresolveProbingDeterministicTimeLimit() {
    return hasPresolveProbingDeterministicTimeLimit_;
  }

  /** Returns {@code presolve_probing_deterministic_time_limit}. */
  public double getPresolveProbingDeterministicTimeLimit() {
    return presolveProbingDeterministicTimeLimit_;
  }

  /** Returns true if {@code presolve_blocked_clause} is set. */
  public boolean hasPresolveBlockedClause() {
    return hasPresolveBlockedClause_;
  }

  /**
   * <pre>
   * Whether we use an heuristic to detect some basic case of blocked clause
   * in the SAT presolve.
   * </pre>
   */
  public boolean getPresolveBlockedClause() {
    return presolveBlockedClause_;
  }

  /** Returns true if {@code presolve_use_bva} is set. */
  public boolean hasPresolveUseBva() {
    return hasPresolveUseBva_;
  }

  /**
   * <pre>
   * Whether or not we use Bounded Variable Addition (BVA) in the presolve.
   * </pre>
   */
  public boolean getPresolveUseBva() {
    return presolveUseBva_;
  }

  /** Returns true if {@code presolve_bva_threshold} is set. */
  public boolean hasPresolveBvaThreshold() {
    return hasPresolveBvaThreshold_;
  }

  /**
   * <pre>
   * Apply Bounded Variable Addition (BVA) if the number of clauses is reduced
   * by stricly more than this threshold. The algorithm described in the paper
   * uses 0, but quick experiments showed that 1 is a good value. It may not be
   * worth it to add a new variable just to remove one clause.
   * </pre>
   */
  public int getPresolveBvaThreshold() {
    return presolveBvaThreshold_;
  }

  /** Returns true if {@code max_presolve_iterations} is set. */
  public boolean hasMaxPresolveIterations() {
    return hasMaxPresolveIterations_;
  }

  /**
   * <pre>
   * In case of large reduction in a presolve iteration, we perform multiple
   * presolve iterations. This parameter controls the maximum number of such
   * presolve iterations.
   * </pre>
   */
  public int getMaxPresolveIterations() {
    return maxPresolveIterations_;
  }

  /** Returns true if {@code cp_model_presolve} is set. */
  public boolean hasCpModelPresolve() {
    return hasCpModelPresolve_;
  }

  /**
   * <pre>
   * Whether we presolve the cp_model before solving it.
   * </pre>
   */
  public boolean getCpModelPresolve() {
    return cpModelPresolve_;
  }

  /** Returns true if {@code cp_model_direct_solve} is set. */
  public boolean hasCpModelDirectSolve() {
    return hasCpModelDirectSolve_;
  }

  /**
   * <pre>
   * Whether we try to solve the cp_model without presolve, in parallel of the
   * presolve. This only has an effect if cp_model_presolve is true, and
   * num_workers &gt; 1.
   * </pre>
   */
  public boolean getCpModelDirectSolve() {
    return cpModelDirectSolve_;
  }

  /** Returns true if {@code cp_model_probing_level} is set. */
  public boolean hasCpModelProbingLevel() {
    return hasCpModelProbingLevel_;
  }

  /**
   * <pre>
   * How much effort do we spend on probing. 0 disables it completely.
   * </pre>
   */
  public int getCpModelProbingLevel() {
    return cpModelProbingLevel_;
  }

  /** Returns true if {@code cp_model_use_sat_presolve} is set. */
  public boolean hasCpModelUseSatPresolve() {
    return hasCpModelUseSatPresolve_;
  }

  /**
   * <pre>
   * Whether we also use the sat presolve when cp_model_presolve is true.
   * </pre>
   */
  public boolean getCpModelUseSatPresolve() {
    return cpModelUseSatPresolve_;
  }

  /** Returns true if {@code cp_model_pure_sat_presolve} is set. */
  public boolean hasCpModelPureSatPresolve() {
    return hasCpModelPureSatPresolve_;
  }

  /**
   * <pre>
   * Whether to use a simplified pure sat presolve only when cp_model_presolve
   * is true. Crashes if the model is not pure SAT.
   * </pre>
   */
  public boolean getCpModelPureSatPresolve() {
    return cpModelPureSatPresolve_;
  }

  /** Returns true if {@code load_at_most_ones_in_sat_presolve} is set. */
  public boolean hasLoadAtMostOnesInSatPresolve() {
    return hasLoadAtMostOnesInSatPresolve_;
  }

  /**
   * <pre>
   * If we try to load at most ones and exactly ones constraints when running
   * the pure SAT presolve. Or if we just ignore them.
   *
   * If one detects at_most_one via merge_at_most_one_work_limit or exactly one
   * with find_clauses_that_are_exactly_one, it might be good to also set this
   * to true.
   * </pre>
   */
  public boolean getLoadAtMostOnesInSatPresolve() {
    return loadAtMostOnesInSatPresolve_;
  }

  /** Returns true if {@code remove_fixed_variables_early} is set. */
  public boolean hasRemoveFixedVariablesEarly() {
    return hasRemoveFixedVariablesEarly_;
  }

  /**
   * <pre>
   * If cp_model_presolve is true and there is a large proportion of fixed
   * variable after the first model copy, remap all the model to a dense set of
   * variable before the full presolve even starts. This should help for LNS on
   * large models.
   * </pre>
   */
  public boolean getRemoveFixedVariablesEarly() {
    return removeFixedVariablesEarly_;
  }

  /** Returns true if {@code detect_table_with_cost} is set. */
  public boolean hasDetectTableWithCost() {
    return hasDetectTableWithCost_;
  }

  /**
   * <pre>
   * If true, we detect variable that are unique to a table constraint and only
   * there to encode a cost on each tuple. This is usually the case when a WCSP
   * (weighted constraint program) is encoded into CP-SAT format.
   *
   * This can lead to a dramatic speed-up for such problems but is still
   * experimental at this point.
   * </pre>
   */
  public boolean getDetectTableWithCost() {
    return detectTableWithCost_;
  }

  /** Returns true if {@code table_compression_level} is set. */
  public boolean hasTableCompressionLevel() {
    return hasTableCompressionLevel_;
  }

  /**
   * <pre>
   * How much we try to "compress" a table constraint. Compressing more leads to
   * less Booleans and faster propagation but can reduced the quality of the lp
   * relaxation. Values goes from 0 to 3 where we always try to fully compress a
   * table. At 2, we try to automatically decide if it is worth it.
   * </pre>
   */
  public int getTableCompressionLevel() {
    return tableCompressionLevel_;
  }

  /** Returns true if {@code expand_alldiff_constraints} is set. */
  public boolean hasExpandAlldiffConstraints() {
    return hasExpandAlldiffConstraints_;
  }

  /**
   * <pre>
   * If true, expand all_different constraints that are not permutations.
   * Permutations (#Variables = #Values) are always expanded.
   * </pre>
   */
  public boolean getExpandAlldiffConstraints() {
    return expandAlldiffConstraints_;
  }

  /** Returns true if {@code max_alldiff_domain_size} is set. */
  public boolean hasMaxAlldiffDomainSize() {
    return hasMaxAlldiffDomainSize_;
  }

  /**
   * <pre>
   * Max domain size for all_different constraints to be expanded.
   * </pre>
   */
  public int getMaxAlldiffDomainSize() {
    return maxAlldiffDomainSize_;
  }

  /** Returns true if {@code expand_reservoir_constraints} is set. */
  public boolean hasExpandReservoirConstraints() {
    return hasExpandReservoirConstraints_;
  }

  /**
   * <pre>
   * If true, expand the reservoir constraints by creating booleans for all
   * possible precedences between event and encoding the constraint.
   * </pre>
   */
  public boolean getExpandReservoirConstraints() {
    return expandReservoirConstraints_;
  }

  /** Returns true if {@code max_domain_size_for_linear2_expansion} is set. */
  public boolean hasMaxDomainSizeForLinear2Expansion() {
    return hasMaxDomainSizeForLinear2Expansion_;
  }

  /**
   * <pre>
   * Max domain size for expanding linear2 constraints (ax + by ==/!= c).
   * </pre>
   */
  public int getMaxDomainSizeForLinear2Expansion() {
    return maxDomainSizeForLinear2Expansion_;
  }

  /** Returns true if {@code expand_reservoir_using_circuit} is set. */
  public boolean hasExpandReservoirUsingCircuit() {
    return hasExpandReservoirUsingCircuit_;
  }

  /**
   * <pre>
   * Mainly useful for testing.
   *
   * If this and expand_reservoir_constraints is true, we use a different
   * encoding of the reservoir constraint using circuit instead of precedences.
   * Note that this is usually slower, but can exercise different part of the
   * solver. Note that contrary to the precedence encoding, this easily support
   * variable demands.
   *
   * WARNING: with this encoding, the constraint takes a slightly different
   * meaning. There must exist a permutation of the events occurring at the same
   * time such that the level is within the reservoir after each of these events
   * (in this permuted order). So we cannot have +100 and -100 at the same time
   * if the level must be between 0 and 10 (as authorized by the reservoir
   * constraint).
   * </pre>
   */
  public boolean getExpandReservoirUsingCircuit() {
    return expandReservoirUsingCircuit_;
  }

  /** Returns true if {@code encode_cumulative_as_reservoir} is set. */
  public boolean hasEncodeCumulativeAsReservoir() {
    return hasEncodeCumulativeAsReservoir_;
  }

  /**
   * <pre>
   * Encore cumulative with fixed demands and capacity as a reservoir
   * constraint. The only reason you might want to do that is to test the
   * reservoir propagation code!
   * </pre>
   */
  public boolean getEncodeCumulativeAsReservoir() {
    return encodeCumulativeAsReservoir_;
  }

  /** Returns true if {@code max_lin_max_size_for_expansion} is set. */
  public boolean hasMaxLinMaxSizeForExpansion() {
    return hasMaxLinMaxSizeForExpansion_;
  }

  /**
   * <pre>
   * If the number of expressions in the lin_max is less that the max size
   * parameter, model expansion replaces target = max(xi) by linear constraint
   * with the introduction of new booleans bi such that bi =&gt; target == xi.
   *
   * This is mainly for experimenting compared to a custom lin_max propagator.
   * </pre>
   */
  public int getMaxLinMaxSizeForExpansion() {
    return maxLinMaxSizeForExpansion_;
  }

  /** Returns true if {@code disable_constraint_expansion} is set. */
  public boolean hasDisableConstraintExpansion() {
    return hasDisableConstraintExpansion_;
  }

  /**
   * <pre>
   * If true, it disable all constraint expansion.
   * This should only be used to test the presolve of expanded constraints.
   * </pre>
   */
  public boolean getDisableConstraintExpansion() {
    return disableConstraintExpansion_;
  }

  /** Returns true if {@code encode_complex_linear_constraint_with_integer} is set. */
  public boolean hasEncodeComplexLinearConstraintWithInteger() {
    return hasEncodeComplexLinearConstraintWithInteger_;
  }

  /**
   * <pre>
   * Linear constraint with a complex right hand side (more than a single
   * interval) need to be expanded, there is a couple of way to do that.
   * </pre>
   */
  public boolean getEncodeComplexLinearConstraintWithInteger() {
    return encodeComplexLinearConstraintWithInteger_;
  }

  /** Returns true if {@code detect_encoded_complex_domain} is set. */
  public boolean hasDetectEncodedComplexDomain() {
    return hasDetectEncodedComplexDomain_;
  }

  /**
   * <pre>
   * Depending on the model, a variable with a "complex" domain like
   * [0][2,5][7][9] is encoded with a domain of [0, 9] + a bunch of Booleans
   * that are only used to enforce the holes. This presolve rule try to detect
   * such case and remove these Booleans. Note that it actually does more than
   * this, and in general try to remove "encoding" Boolean that are not really
   * used.
   * </pre>
   */
  public boolean getDetectEncodedComplexDomain() {
    return detectEncodedComplexDomain_;
  }

  /** Returns true if {@code merge_no_overlap_work_limit} is set. */
  public boolean hasMergeNoOverlapWorkLimit() {
    return hasMergeNoOverlapWorkLimit_;
  }

  /**
   * <pre>
   * During presolve, we use a maximum clique heuristic to merge together
   * no-overlap constraints or at most one constraints. This code can be slow,
   * so we have a limit in place on the number of explored nodes in the
   * underlying graph. The internal limit is an int64, but we use double here to
   * simplify manual input.
   * </pre>
   */
  public double getMergeNoOverlapWorkLimit() {
    return mergeNoOverlapWorkLimit_;
  }

  /** Returns true if {@code merge_at_most_one_work_limit} is set. */
  public boolean hasMergeAtMostOneWorkLimit() {
    return hasMergeAtMostOneWorkLimit_;
  }

  /** Returns {@code merge_at_most_one_work_limit}. */
  public double getMergeAtMostOneWorkLimit() {
    return mergeAtMostOneWorkLimit_;
  }

  /** Returns true if {@code presolve_substitution_level} is set. */
  public boolean hasPresolveSubstitutionLevel() {
    return hasPresolveSubstitutionLevel_;
  }

  /**
   * <pre>
   * How much substitution (also called free variable aggregation in MIP
   * litterature) should we perform at presolve. This currently only concerns
   * variable appearing only in linear constraints. For now the value 0 turns it
   * off and any positive value performs substitution.
   * </pre>
   */
  public int getPresolveSubstitutionLevel() {
    return presolveSubstitutionLevel_;
  }

  /** Returns true if {@code presolve_extract_integer_enforcement} is set. */
  public boolean hasPresolveExtractIntegerEnforcement() {
    return hasPresolveExtractIntegerEnforcement_;
  }

  /**
   * <pre>
   * If true, we will extract from linear constraints, enforcement literals of
   * the form "integer variable at bound =&gt; simplified constraint". This should
   * always be beneficial except that we don't always handle them as efficiently
   * as we could for now. This causes problem on manna81.mps (LP relaxation not
   * as tight it seems) and on neos-3354841-apure.mps.gz (too many literals
   * created this way).
   * </pre>
   */
  public boolean getPresolveExtractIntegerEnforcement() {
    return presolveExtractIntegerEnforcement_;
  }

  /** Returns true if {@code presolve_inclusion_work_limit} is set. */
  public boolean hasPresolveInclusionWorkLimit() {
    return hasPresolveInclusionWorkLimit_;
  }

  /**
   * <pre>
   * A few presolve operations involve detecting constraints included in other
   * constraint. Since there can be a quadratic number of such pairs, and
   * processing them usually involve scanning them, the complexity of these
   * operations can be big. This enforce a local deterministic limit on the
   * number of entries scanned. Default is 1e8.
   *
   * A value of zero will disable these presolve rules completely.
   * </pre>
   */
  public long getPresolveInclusionWorkLimit() {
    return presolveInclusionWorkLimit_;
  }

  /** Returns true if {@code ignore_names} is set. */
  public boolean hasIgnoreNames() {
    return hasIgnoreNames_;
  }

  /**
   * <pre>
   * If true, we don't keep names in our internal copy of the user given model.
   * </pre>
   */
  public boolean getIgnoreNames() {
    return ignoreNames_;
  }

  /** Returns true if {@code infer_all_diffs} is set. */
  public boolean hasInferAllDiffs() {
    return hasInferAllDiffs_;
  }

  /**
   * <pre>
   * Run a max-clique code amongst all the x != y we can find and try to infer
   * set of variables that are all different. This allows to close neos16.mps
   * for instance. Note that we only run this code if there is no all_diff
   * already in the model so that if a user want to add some all_diff, we assume
   * it is well done and do not try to add more.
   *
   * This will also detect and add no_overlap constraints, if all the relations
   * x != y have "offsets" between them. I.e. x &gt; y + offset.
   *
   * This also controls the presolve conversion of no_overlap constraints with
   * mandatory unit-size intervals into all_diff constraints on their starts.
   * </pre>
   */
  public boolean getInferAllDiffs() {
    return inferAllDiffs_;
  }

  /** Returns true if {@code find_big_linear_overlap} is set. */
  public boolean hasFindBigLinearOverlap() {
    return hasFindBigLinearOverlap_;
  }

  /**
   * <pre>
   * Try to find large "rectangle" in the linear constraint matrix with
   * identical lines. If such rectangle is big enough, we can introduce a new
   * integer variable corresponding to the common expression and greatly reduce
   * the number of non-zero.
   * </pre>
   */
  public boolean getFindBigLinearOverlap() {
    return findBigLinearOverlap_;
  }

  /** Returns true if {@code find_clauses_that_are_exactly_one} is set. */
  public boolean hasFindClausesThatAreExactlyOne() {
    return hasFindClausesThatAreExactlyOne_;
  }

  /**
   * <pre>
   * By propagating (or just using binary clauses), one can detect that all
   * literal of a clause are actually in at most one relationship. Thus this
   * constraint can be promoted to an exactly one constraints. This should help
   * as it convey more structure. Note that this is expensive, so we have a
   * deterministic limit in place.
   * </pre>
   */
  public boolean getFindClausesThatAreExactlyOne() {
    return findClausesThatAreExactlyOne_;
  }

  /** Returns true if {@code use_sat_inprocessing} is set. */
  public boolean hasUseSatInprocessing() {
    return hasUseSatInprocessing_;
  }

  /**
   * <pre>
   * Enable or disable "inprocessing" which is some SAT presolving done at
   * each restart to the root level.
   * </pre>
   */
  public boolean getUseSatInprocessing() {
    return useSatInprocessing_;
  }

  /** Returns true if {@code inprocessing_dtime_ratio} is set. */
  public boolean hasInprocessingDtimeRatio() {
    return hasInprocessingDtimeRatio_;
  }

  /**
   * <pre>
   * Proportion of deterministic time we should spend on inprocessing.
   * At each "restart", if the proportion is below this ratio, we will do some
   * inprocessing, otherwise, we skip it for this restart.
   * </pre>
   */
  public double getInprocessingDtimeRatio() {
    return inprocessingDtimeRatio_;
  }

  /** Returns true if {@code inprocessing_probing_dtime} is set. */
  public boolean hasInprocessingProbingDtime() {
    return hasInprocessingProbingDtime_;
  }

  /**
   * <pre>
   * The amount of dtime we should spend on probing for each inprocessing round.
   * </pre>
   */
  public double getInprocessingProbingDtime() {
    return inprocessingProbingDtime_;
  }

  /** Returns true if {@code inprocessing_minimization_dtime} is set. */
  public boolean hasInprocessingMinimizationDtime() {
    return hasInprocessingMinimizationDtime_;
  }

  /**
   * <pre>
   * Parameters for an heuristic similar to the one described in "An effective
   * learnt clause minimization approach for CDCL Sat Solvers",
   * https://www.ijcai.org/proceedings/2017/0098.pdf
   *
   * This is the amount of dtime we should spend on this technique during each
   * inprocessing phase.
   *
   * The minimization technique is the same as the one used to minimize core in
   * max-sat. We also minimize problem clauses and not just the learned clause
   * that we keep forever like in the paper.
   * </pre>
   */
  public double getInprocessingMinimizationDtime() {
    return inprocessingMinimizationDtime_;
  }

  /** Returns true if {@code inprocessing_minimization_use_conflict_analysis} is set. */
  public boolean hasInprocessingMinimizationUseConflictAnalysis() {
    return hasInprocessingMinimizationUseConflictAnalysis_;
  }

  /** Returns {@code inprocessing_minimization_use_conflict_analysis}. */
  public boolean getInprocessingMinimizationUseConflictAnalysis() {
    return inprocessingMinimizationUseConflictAnalysis_;
  }

  /** Returns true if {@code num_ordering_in_vivification} is set. */
  public boolean hasNumOrderingInVivification() {
    return hasNumOrderingInVivification_;
  }

  /**
   * <pre>
   * The number of ordering to try each time we vivify a clause.
   * This allow to spend more effort on vivivication if needed.
   * </pre>
   */
  public int getNumOrderingInVivification() {
    return numOrderingInVivification_;
  }

  /** Returns true if {@code inprocessing_use_congruence_closure} is set. */
  public boolean hasInprocessingUseCongruenceClosure() {
    return hasInprocessingUseCongruenceClosure_;
  }

  /**
   * <pre>
   * Whether we use the algorithm described in "Clausal Congruence closure",
   * Armin Biere, Katalin Fazekas, Mathias Fleury, Nils Froleyks, 2024.
   *
   * Note that we only have a basic version currently.
   * </pre>
   */
  public boolean getInprocessingUseCongruenceClosure() {
    return inprocessingUseCongruenceClosure_;
  }

  /** Returns true if {@code inprocessing_detect_and_sweep_circuit} is set. */
  public boolean hasInprocessingDetectAndSweepCircuit() {
    return hasInprocessingDetectAndSweepCircuit_;
  }

  /**
   * <pre>
   * Experimental. We don't have LRAT support for this. Recovers a "circuit"
   * structure based on the gate detection used by congruence closure, and try
   * to detect more equivalence via sampling. This is like the custom SAT
   * sweeping done on a logic circuit with binary gates.
   * </pre>
   */
  public boolean getInprocessingDetectAndSweepCircuit() {
    return inprocessingDetectAndSweepCircuit_;
  }

  /** Returns true if {@code inprocessing_use_sat_sweeping} is set. */
  public boolean hasInprocessingUseSatSweeping() {
    return hasInprocessingUseSatSweeping_;
  }

  /**
   * <pre>
   * Whether we use the SAT sweeping algorithm described in "Clausal Equivalence
   * Sweeping", Armin Biere, Katalin Fazekas, Mathias Fleury, Nils Froleyks,
   * 2025.
   * </pre>
   */
  public boolean getInprocessingUseSatSweeping() {
    return inprocessingUseSatSweeping_;
  }

  /** Returns true if {@code inprocessing_use_transitive_reduction} is set. */
  public boolean hasInprocessingUseTransitiveReduction() {
    return hasInprocessingUseTransitiveReduction_;
  }

  /**
   * <pre>
   * Whether we perform transitive reduction of the binary implication graph.
   * This might reduce the number of implication significantly, but it might
   * also loose some structure used by our detection algorithm like the logical
   * gates detection.
   * </pre>
   */
  public boolean getInprocessingUseTransitiveReduction() {
    return inprocessingUseTransitiveReduction_;
  }

  /** Returns true if {@code num_workers} is set. */
  public boolean hasNumWorkers() {
    return hasNumWorkers_;
  }

  /**
   * <pre>
   * Specify the number of parallel workers (i.e. threads) to use during search.
   * This should usually be lower than your number of available cpus +
   * hyperthread in your machine.
   *
   * A value of 0 means the solver will try to use all cores on the machine.
   * A number of 1 means no parallelism.
   *
   * Note that 'num_workers' is the preferred name, but if it is set to zero,
   * we will still read the deprecated 'num_search_workers'.
   *
   * As of 2020-04-10, if you're using SAT via MPSolver (to solve integer
   * programs) this field is overridden with a value of 8, if the field is not
   * set *explicitly*. Thus, always set this field explicitly or via
   * MPSolver::SetNumThreads().
   * </pre>
   */
  public int getNumWorkers() {
    return numWorkers_;
  }

  /** Returns true if {@code num_search_workers} is set. */
  public boolean hasNumSearchWorkers() {
    return hasNumSearchWorkers_;
  }

  /** Returns {@code num_search_workers}. */
  public int getNumSearchWorkers() {
    return numSearchWorkers_;
  }

  /** Returns true if {@code num_full_subsolvers} is set. */
  public boolean hasNumFullSubsolvers() {
    return hasNumFullSubsolvers_;
  }

  /**
   * <pre>
   * We distinguish subsolvers that consume a full thread, and the ones that are
   * always interleaved. If left at zero, we will fix this with a default
   * formula that depends on num_workers. But if you start modifying what runs,
   * you might want to fix that to a given value depending on the num_workers
   * you use.
   * </pre>
   */
  public int getNumFullSubsolvers() {
    return numFullSubsolvers_;
  }

  /**
   * <pre>
   * In multi-thread, the solver can be mainly seen as a portfolio of solvers
   * with different parameters. This field indicates the names of the parameters
   * that are used in multithread. This only applies to "full" subsolvers.
   *
   * See cp_model_search.cc to see a list of the names and the default value (if
   * left empty) that looks like:
   * - default_lp           (linearization_level:1)
   * - fixed                (only if fixed search specified or scheduling)
   * - no_lp                (linearization_level:0)
   * - max_lp               (linearization_level:2)
   * - pseudo_costs         (only if objective, change search heuristic)
   * - reduced_costs        (only if objective, change search heuristic)
   * - quick_restart        (kind of probing)
   * - quick_restart_no_lp  (kind of probing with linearization_level:0)
   * - lb_tree_search       (to improve lower bound, MIP like tree search)
   * - probing              (continuous probing and shaving)
   *
   * Also, note that some set of parameters will be ignored if they do not make
   * sense. For instance if there is no objective, pseudo_cost or reduced_cost
   * search will be ignored. Core based search will only work if the objective
   * has many terms. If there is no fixed strategy fixed will be ignored. And so
   * on.
   *
   * The order is important, as only the first num_full_subsolvers will be
   * scheduled. You can see in the log which one are selected for a given run.
   * </pre>
   */
  public java.util.List<String> getSubsolversList() {
    return subsolvers_ == null ? java.util.Collections.<String>emptyList() : java.util.Collections.unmodifiableList(subsolvers_);
  }

  /** Returns the number of elements of {@code subsolvers}. */
  public int getSubsolversCount() {
    return subsolvers_ == null ? 0 : subsolvers_.size();
  }

  /** Returns the element {@code index} of {@code subsolvers}. */
  public String getSubsolvers(int index) {
    if (subsolvers_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return subsolvers_.get(index);
  }

  /**
   * <pre>
   * A convenient way to add more workers types.
   * These will be added at the beginning of the list.
   * </pre>
   */
  public java.util.List<String> getExtraSubsolversList() {
    return extraSubsolvers_ == null ? java.util.Collections.<String>emptyList() : java.util.Collections.unmodifiableList(extraSubsolvers_);
  }

  /** Returns the number of elements of {@code extra_subsolvers}. */
  public int getExtraSubsolversCount() {
    return extraSubsolvers_ == null ? 0 : extraSubsolvers_.size();
  }

  /** Returns the element {@code index} of {@code extra_subsolvers}. */
  public String getExtraSubsolvers(int index) {
    if (extraSubsolvers_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return extraSubsolvers_.get(index);
  }

  /**
   * <pre>
   * Rather than fully specifying subsolvers, it is often convenient to just
   * remove the ones that are not useful on a given problem or only keep
   * specific ones for testing. Each string is interpreted as a "glob", so we
   * support '*' and '?'.
   *
   * The way this work is that we will only accept a name that match a filter
   * pattern (if non-empty) and do not match an ignore pattern. Note also that
   * these fields work on LNS or LS names even if these are currently not
   * specified via the subsolvers field.
   * </pre>
   */
  public java.util.List<String> getIgnoreSubsolversList() {
    return ignoreSubsolvers_ == null ? java.util.Collections.<String>emptyList() : java.util.Collections.unmodifiableList(ignoreSubsolvers_);
  }

  /** Returns the number of elements of {@code ignore_subsolvers}. */
  public int getIgnoreSubsolversCount() {
    return ignoreSubsolvers_ == null ? 0 : ignoreSubsolvers_.size();
  }

  /** Returns the element {@code index} of {@code ignore_subsolvers}. */
  public String getIgnoreSubsolvers(int index) {
    if (ignoreSubsolvers_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return ignoreSubsolvers_.get(index);
  }

  /** Returns the elements of {@code filter_subsolvers}. */
  public java.util.List<String> getFilterSubsolversList() {
    return filterSubsolvers_ == null ? java.util.Collections.<String>emptyList() : java.util.Collections.unmodifiableList(filterSubsolvers_);
  }

  /** Returns the number of elements of {@code filter_subsolvers}. */
  public int getFilterSubsolversCount() {
    return filterSubsolvers_ == null ? 0 : filterSubsolvers_.size();
  }

  /** Returns the element {@code index} of {@code filter_subsolvers}. */
  public String getFilterSubsolvers(int index) {
    if (filterSubsolvers_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return filterSubsolvers_.get(index);
  }

  /**
   * <pre>
   * It is possible to specify additional subsolver configuration. These can be
   * referred by their params.name() in the fields above. Note that only the
   * specified field will "overwrite" the ones of the base parameter. If a
   * subsolver_params has the name of an existing subsolver configuration, the
   * named parameters will be merged into the subsolver configuration.
   * </pre>
   */
  public java.util.List<SatParameters> getSubsolverParamsList() {
    return subsolverParams_ == null ? java.util.Collections.<SatParameters>emptyList() : java.util.Collections.unmodifiableList(subsolverParams_);
  }

  /** Returns the number of elements of {@code subsolver_params}. */
  public int getSubsolverParamsCount() {
    return subsolverParams_ == null ? 0 : subsolverParams_.size();
  }

  /** Returns the element {@code index} of {@code subsolver_params}. */
  public SatParameters getSubsolverParams(int index) {
    if (subsolverParams_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return subsolverParams_.get(index);
  }

  /** Returns true if {@code interleave_search} is set. */
  public boolean hasInterleaveSearch() {
    return hasInterleaveSearch_;
  }

  /**
   * <pre>
   * Experimental. If this is true, then we interleave all our major search
   * strategy and distribute the work amongst num_workers.
   *
   * The search is deterministic (independently of num_workers!), and we
   * schedule and wait for interleave_batch_size task to be completed before
   * synchronizing and scheduling the next batch of tasks.
   * </pre>
   */
  public boolean getInterleaveSearch() {
    return interleaveSearch_;
  }

  /** Returns true if {@code interleave_batch_size} is set. */
  public boolean hasInterleaveBatchSize() {
    return hasInterleaveBatchSize_;
  }

  /** Returns {@code interleave_batch_size}. */
  public int getInterleaveBatchSize() {
    return interleaveBatchSize_;
  }

  /** Returns true if {@code share_objective_bounds} is set. */
  public boolean hasShareObjectiveBounds() {
    return hasShareObjectiveBounds_;
  }

  /**
   * <pre>
   * Allows objective sharing between workers.
   * </pre>
   */
  public boolean getShareObjectiveBounds() {
    return shareObjectiveBounds_;
  }

  /** Returns true if {@code share_level_zero_bounds} is set. */
  public boolean hasShareLevelZeroBounds() {
    return hasShareLevelZeroBounds_;
  }

  /**
   * <pre>
   * Allows sharing of the bounds of modified variables at level 0.
   * </pre>
   */
  public boolean getShareLevelZeroBounds() {
    return shareLevelZeroBounds_;
  }

  /** Returns true if {@code share_linear2_bounds} is set. */
  public boolean hasShareLinear2Bounds() {
    return hasShareLinear2Bounds_;
  }

  /**
   * <pre>
   * Allows sharing of the bounds on linear2 discovered at level 0. This is
   * mainly interesting on scheduling type of problems when we branch on
   * precedences.
   *
   * Warning: This currently non-deterministic.
   * </pre>
   */
  public boolean getShareLinear2Bounds() {
    return shareLinear2Bounds_;
  }

  /** Returns true if {@code share_binary_clauses} is set. */
  public boolean hasShareBinaryClauses() {
    return hasShareBinaryClauses_;
  }

  /**
   * <pre>
   * Allows sharing of new learned binary clause between workers.
   * </pre>
   */
  public boolean getShareBinaryClauses() {
    return shareBinaryClauses_;
  }

  /** Returns true if {@code share_glue_clauses} is set. */
  public boolean hasShareGlueClauses() {
    return hasShareGlueClauses_;
  }

  /**
   * <pre>
   * Allows sharing of short glue clauses between workers.
   * Implicitly disabled if share_binary_clauses is false.
   * </pre>
   */
  public boolean getShareGlueClauses() {
    return shareGlueClauses_;
  }

  /** Returns true if {@code minimize_shared_clauses} is set. */
  public boolean hasMinimizeSharedClauses() {
    return hasMinimizeSharedClauses_;
  }

  /**
   * <pre>
   * Minimize and detect subsumption of shared clauses immediately after they
   * are imported.
   * </pre>
   */
  public boolean getMinimizeSharedClauses() {
    return minimizeSharedClauses_;
  }

  /** Returns true if {@code share_glue_clauses_dtime} is set. */
  public boolean hasShareGlueClausesDtime() {
    return hasShareGlueClausesDtime_;
  }

  /**
   * <pre>
   * The amount of dtime between each export of shared glue clauses.
   * </pre>
   */
  public double getShareGlueClausesDtime() {
    return shareGlueClausesDtime_;
  }

  /** Returns true if {@code check_lrat_proof} is set. */
  public boolean hasCheckLratProof() {
    return hasCheckLratProof_;
  }

  /**
   * <pre>
   * If true, inferred clauses are checked with an LRAT checker as they are
   * learned, in presolve (reduced to trivial simplifications if
   * cp_model_presolve is false), and in each worker. As of February 2026, this
   * only works with pure SAT problems, with
   *  - cp_model_pure_sat_presolve = true if cp_model_presolve = true,
   *  - linearization_level &lt;= 1,
   *  - symmetry_level &lt;= 1,
   *  - use_chronological_backtracking = false,
   *  - inprocessing_use_sat_sweeping = false.
   * </pre>
   */
  public boolean getCheckLratProof() {
    return checkLratProof_;
  }

  /** Returns true if {@code check_merged_lrat_proof} is set. */
  public boolean hasCheckMergedLratProof() {
    return hasCheckMergedLratProof_;
  }

  /**
   * <pre>
   * If true, and if output_lrat_proof is true and the problem is UNSAT, check
   * that the merged proof file is valid, i.e., that clause sharing between
   * workers is correct. This checks each inferred clause, so you might want to
   * disable check_lrat_proof to avoid redundant work. As of February 2026, this
   * only works for pure SAT problems.
   * </pre>
   */
  public boolean getCheckMergedLratProof() {
    return checkMergedLratProof_;
  }

  /** Returns true if {@code output_lrat_proof} is set. */
  public boolean hasOutputLratProof() {
    return hasOutputLratProof_;
  }

  /**
   * <pre>
   * If true, an LRAT proof that all the clauses inferred by the solver are
   * valid is output to several files (one for presolve -- reduced to trivial
   * simplifications if cp_model_presolve is false, one per worker, and one for
   * the merged proof). As of February 2026, this only works for pure SAT
   * problems, with
   *  - cp_model_pure_sat_presolve = true if cp_model_presolve = true,
   *  - linearization_level &lt;= 1,
   *  - symmetry_level &lt;= 1,
   *  - use_chronological_backtracking = false,
   *  - inprocessing_use_sat_sweeping = false.
   * </pre>
   */
  public boolean getOutputLratProof() {
    return outputLratProof_;
  }

  /** Returns true if {@code debug_postsolve_with_full_solver} is set. */
  public boolean hasDebugPostsolveWithFullSolver() {
    return hasDebugPostsolveWithFullSolver_;
  }

  /**
   * <pre>
   * We have two different postsolve code. The default one should be better and
   * it allows for a more powerful presolve, but it can be useful to postsolve
   * using the full solver instead.
   * </pre>
   */
  public boolean getDebugPostsolveWithFullSolver() {
    return debugPostsolveWithFullSolver_;
  }

  /** Returns true if {@code debug_max_num_presolve_operations} is set. */
  public boolean hasDebugMaxNumPresolveOperations() {
    return hasDebugMaxNumPresolveOperations_;
  }

  /**
   * <pre>
   * If positive, try to stop just after that many presolve rules have been
   * applied. This is mainly useful for debugging presolve.
   * </pre>
   */
  public int getDebugMaxNumPresolveOperations() {
    return debugMaxNumPresolveOperations_;
  }

  /** Returns true if {@code debug_crash_on_bad_hint} is set. */
  public boolean hasDebugCrashOnBadHint() {
    return hasDebugCrashOnBadHint_;
  }

  /**
   * <pre>
   * Crash if we do not manage to complete the hint into a full solution.
   * </pre>
   */
  public boolean getDebugCrashOnBadHint() {
    return debugCrashOnBadHint_;
  }

  /** Returns true if {@code debug_crash_if_presolve_breaks_hint} is set. */
  public boolean hasDebugCrashIfPresolveBreaksHint() {
    return hasDebugCrashIfPresolveBreaksHint_;
  }

  /**
   * <pre>
   * Crash if presolve breaks a feasible hint.
   * </pre>
   */
  public boolean getDebugCrashIfPresolveBreaksHint() {
    return debugCrashIfPresolveBreaksHint_;
  }

  /** Returns true if {@code debug_crash_if_lrat_check_fails} is set. */
  public boolean hasDebugCrashIfLratCheckFails() {
    return hasDebugCrashIfLratCheckFails_;
  }

  /**
   * <pre>
   * Crash if the LRAT UNSAT proof is invalid.
   * </pre>
   */
  public boolean getDebugCrashIfLratCheckFails() {
    return debugCrashIfLratCheckFails_;
  }

  /** Returns true if {@code use_optimization_hints} is set. */
  public boolean hasUseOptimizationHints() {
    return hasUseOptimizationHints_;
  }

  /**
   * <pre>
   * For an optimization problem, whether we follow some hints in order to find
   * a better first solution. For a variable with hint, the solver will always
   * try to follow the hint. It will revert to the variable_branching default
   * otherwise.
   * </pre>
   */
  public boolean getUseOptimizationHints() {
    return useOptimizationHints_;
  }

  /** Returns true if {@code core_minimization_level} is set. */
  public boolean hasCoreMinimizationLevel() {
    return hasCoreMinimizationLevel_;
  }

  /**
   * <pre>
   * If positive, we spend some effort on each core:
   * - At level 1, we use a simple heuristic to try to minimize an UNSAT core.
   * - At level 2, we use propagation to minimize the core but also identify
   *   literal in at most one relationship in this core.
   * </pre>
   */
  public int getCoreMinimizationLevel() {
    return coreMinimizationLevel_;
  }

  /** Returns true if {@code find_multiple_cores} is set. */
  public boolean hasFindMultipleCores() {
    return hasFindMultipleCores_;
  }

  /**
   * <pre>
   * Whether we try to find more independent cores for a given set of
   * assumptions in the core based max-SAT algorithms.
   * </pre>
   */
  public boolean getFindMultipleCores() {
    return findMultipleCores_;
  }

  /** Returns true if {@code cover_optimization} is set. */
  public boolean hasCoverOptimization() {
    return hasCoverOptimization_;
  }

  /**
   * <pre>
   * If true, when the max-sat algo find a core, we compute the minimal number
   * of literals in the core that needs to be true to have a feasible solution.
   * This is also called core exhaustion in more recent max-SAT papers.
   * </pre>
   */
  public boolean getCoverOptimization() {
    return coverOptimization_;
  }

  /** Returns true if {@code max_sat_assumption_order} is set. */
  public boolean hasMaxSatAssumptionOrder() {
    return hasMaxSatAssumptionOrder_;
  }

  /** Returns {@code max_sat_assumption_order}. */
  public SatParameters.MaxSatAssumptionOrder getMaxSatAssumptionOrder() {
    SatParameters.MaxSatAssumptionOrder result = SatParameters.MaxSatAssumptionOrder.forNumber(maxSatAssumptionOrder_);
    return result == null ? SatParameters.MaxSatAssumptionOrder.forNumber(0) : result;
  }

  /** Returns the number of {@code max_sat_assumption_order}. */
  public int getMaxSatAssumptionOrderValue() {
    return maxSatAssumptionOrder_;
  }

  /** Returns true if {@code max_sat_reverse_assumption_order} is set. */
  public boolean hasMaxSatReverseAssumptionOrder() {
    return hasMaxSatReverseAssumptionOrder_;
  }

  /**
   * <pre>
   * If true, adds the assumption in the reverse order of the one defined by
   * max_sat_assumption_order.
   * </pre>
   */
  public boolean getMaxSatReverseAssumptionOrder() {
    return maxSatReverseAssumptionOrder_;
  }

  /** Returns true if {@code max_sat_stratification} is set. */
  public boolean hasMaxSatStratification() {
    return hasMaxSatStratification_;
  }

  /** Returns {@code max_sat_stratification}. */
  public SatParameters.MaxSatStratificationAlgorithm getMaxSatStratification() {
    SatParameters.MaxSatStratificationAlgorithm result = SatParameters.MaxSatStratificationAlgorithm.forNumber(maxSatStratification_);
    return result == null ? SatParameters.MaxSatStratificationAlgorithm.forNumber(1) : result;
  }

  /** Returns the number of {@code max_sat_stratification}. */
  public int getMaxSatStratificationValue() {
    return maxSatStratification_;
  }

  /** Returns true if {@code propagation_loop_detection_factor} is set. */
  public boolean hasPropagationLoopDetectionFactor() {
    return hasPropagationLoopDetectionFactor_;
  }

  /**
   * <pre>
   * Some search decisions might cause a really large number of propagations to
   * happen when integer variables with large domains are only reduced by 1 at
   * each step. If we propagate more than the number of variable times this
   * parameters we try to take counter-measure. Setting this to 0.0 disable this
   * feature.
   *
   * TODO(user): Setting this to something like 10 helps in most cases, but the
   * code is currently buggy and can cause the solve to enter a bad state where
   * no progress is made.
   * </pre>
   */
  public double getPropagationLoopDetectionFactor() {
    return propagationLoopDetectionFactor_;
  }

  /** Returns true if {@code use_precedences_in_disjunctive_constraint} is set. */
  public boolean hasUsePrecedencesInDisjunctiveConstraint() {
    return hasUsePrecedencesInDisjunctiveConstraint_;
  }

  /**
   * <pre>
   * When this is true, then a disjunctive constraint will try to use the
   * precedence relations between time intervals to propagate their bounds
   * further. For instance if task A and B are both before C and task A and B
   * are in disjunction, then we can deduce that task C must start after
   * duration(A) + duration(B) instead of simply max(duration(A), duration(B)),
   * provided that the start time for all task was currently zero.
   *
   * This always result in better propagation, but it is usually slow, so
   * depending on the problem, turning this off may lead to a faster solution.
   * </pre>
   */
  public boolean getUsePrecedencesInDisjunctiveConstraint() {
    return usePrecedencesInDisjunctiveConstraint_;
  }

  /** Returns true if {@code transitive_precedences_work_limit} is set. */
  public boolean hasTransitivePrecedencesWorkLimit() {
    return hasTransitivePrecedencesWorkLimit_;
  }

  /**
   * <pre>
   * At root level, we might compute the transitive closure of "precedences"
   * relations so that we can exploit that in scheduling problems. Setting this
   * to zero disable the feature.
   * </pre>
   */
  public int getTransitivePrecedencesWorkLimit() {
    return transitivePrecedencesWorkLimit_;
  }

  /** Returns true if {@code max_size_to_create_precedence_literals_in_disjunctive} is set. */
  public boolean hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive() {
    return hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_;
  }

  /**
   * <pre>
   * Create one literal for each disjunction of two pairs of tasks. This slows
   * down the solve time, but improves the lower bound of the objective in the
   * makespan case. This will be triggered if the number of intervals is less or
   * equal than the parameter and if use_strong_propagation_in_disjunctive is
   * true.
   * </pre>
   */
  public int getMaxSizeToCreatePrecedenceLiteralsInDisjunctive() {
    return maxSizeToCreatePrecedenceLiteralsInDisjunctive_;
  }

  /** Returns true if {@code use_strong_propagation_in_disjunctive} is set. */
  public boolean hasUseStrongPropagationInDisjunctive() {
    return hasUseStrongPropagationInDisjunctive_;
  }

  /**
   * <pre>
   * Enable stronger and more expensive propagation on no_overlap constraint.
   * </pre>
   */
  public boolean getUseStrongPropagationInDisjunctive() {
    return useStrongPropagationInDisjunctive_;
  }

  /** Returns true if {@code use_dynamic_precedence_in_disjunctive} is set. */
  public boolean hasUseDynamicPrecedenceInDisjunctive() {
    return hasUseDynamicPrecedenceInDisjunctive_;
  }

  /**
   * <pre>
   * Whether we try to branch on decision "interval A before interval B" rather
   * than on intervals bounds. This usually works better, but slow down a bit
   * the time to find the first solution.
   *
   * These parameters are still EXPERIMENTAL, the result should be correct, but
   * it some corner cases, they can cause some failing CHECK in the solver.
   * </pre>
   */
  public boolean getUseDynamicPrecedenceInDisjunctive() {
    return useDynamicPrecedenceInDisjunctive_;
  }

  /** Returns true if {@code use_dynamic_precedence_in_cumulative} is set. */
  public boolean hasUseDynamicPrecedenceInCumulative() {
    return hasUseDynamicPrecedenceInCumulative_;
  }

  /** Returns {@code use_dynamic_precedence_in_cumulative}. */
  public boolean getUseDynamicPrecedenceInCumulative() {
    return useDynamicPrecedenceInCumulative_;
  }

  /** Returns true if {@code use_overload_checker_in_cumulative} is set. */
  public boolean hasUseOverloadCheckerInCumulative() {
    return hasUseOverloadCheckerInCumulative_;
  }

  /**
   * <pre>
   * When this is true, the cumulative constraint is reinforced with overload
   * checking, i.e., an additional level of reasoning based on energy. This
   * additional level supplements the default level of reasoning as well as
   * timetable edge finding.
   *
   * This always result in better propagation, but it is usually slow, so
   * depending on the problem, turning this off may lead to a faster solution.
   * </pre>
   */
  public boolean getUseOverloadCheckerInCumulative() {
    return useOverloadCheckerInCumulative_;
  }

  /** Returns true if {@code use_conservative_scale_overload_checker} is set. */
  public boolean hasUseConservativeScaleOverloadChecker() {
    return hasUseConservativeScaleOverloadChecker_;
  }

  /**
   * <pre>
   * Enable a heuristic to solve cumulative constraints using a modified energy
   * constraint. We modify the usual energy definition by applying a
   * super-additive function (also called "conservative scale" or "dual-feasible
   * function") to the demand and the durations of the tasks.
   *
   * This heuristic is fast but for most problems it does not help much to find
   * a solution.
   * </pre>
   */
  public boolean getUseConservativeScaleOverloadChecker() {
    return useConservativeScaleOverloadChecker_;
  }

  /** Returns true if {@code use_timetable_edge_finding_in_cumulative} is set. */
  public boolean hasUseTimetableEdgeFindingInCumulative() {
    return hasUseTimetableEdgeFindingInCumulative_;
  }

  /**
   * <pre>
   * When this is true, the cumulative constraint is reinforced with timetable
   * edge finding, i.e., an additional level of reasoning based on the
   * conjunction of energy and mandatory parts. This additional level
   * supplements the default level of reasoning as well as overload_checker.
   *
   * This always result in better propagation, but it is usually slow, so
   * depending on the problem, turning this off may lead to a faster solution.
   * </pre>
   */
  public boolean getUseTimetableEdgeFindingInCumulative() {
    return useTimetableEdgeFindingInCumulative_;
  }

  /** Returns true if {@code max_num_intervals_for_timetable_edge_finding} is set. */
  public boolean hasMaxNumIntervalsForTimetableEdgeFinding() {
    return hasMaxNumIntervalsForTimetableEdgeFinding_;
  }

  /**
   * <pre>
   * Max number of intervals for the timetable_edge_finding algorithm to
   * propagate. A value of 0 disables the constraint.
   * </pre>
   */
  public int getMaxNumIntervalsForTimetableEdgeFinding() {
    return maxNumIntervalsForTimetableEdgeFinding_;
  }

  /** Returns true if {@code use_hard_precedences_in_cumulative} is set. */
  public boolean hasUseHardPrecedencesInCumulative() {
    return hasUseHardPrecedencesInCumulative_;
  }

  /**
   * <pre>
   * If true, detect and create constraint for integer variable that are "after"
   * a set of intervals in the same cumulative constraint.
   *
   * Experimental: by default we just use "direct" precedences. If
   * exploit_all_precedences is true, we explore the full precedence graph. This
   * assumes we have a DAG otherwise it fails.
   * </pre>
   */
  public boolean getUseHardPrecedencesInCumulative() {
    return useHardPrecedencesInCumulative_;
  }

  /** Returns true if {@code exploit_all_precedences} is set. */
  public boolean hasExploitAllPrecedences() {
    return hasExploitAllPrecedences_;
  }

  /** Returns {@code exploit_all_precedences}. */
  public boolean getExploitAllPrecedences() {
    return exploitAllPrecedences_;
  }

  /** Returns true if {@code use_disjunctive_constraint_in_cumulative} is set. */
  public boolean hasUseDisjunctiveConstraintInCumulative() {
    return hasUseDisjunctiveConstraintInCumulative_;
  }

  /**
   * <pre>
   * When this is true, the cumulative constraint is reinforced with propagators
   * from the disjunctive constraint to improve the inference on a set of tasks
   * that are disjunctive at the root of the problem. This additional level
   * supplements the default level of reasoning.
   *
   * Propagators of the cumulative constraint will not be used at all if all the
   * tasks are disjunctive at root node.
   *
   * This always result in better propagation, but it is usually slow, so
   * depending on the problem, turning this off may lead to a faster solution.
   * </pre>
   */
  public boolean getUseDisjunctiveConstraintInCumulative() {
    return useDisjunctiveConstraintInCumulative_;
  }

  /** Returns true if {@code no_overlap_2d_boolean_relations_limit} is set. */
  public boolean hasNoOverlap2DBooleanRelationsLimit() {
    return hasNoOverlap2DBooleanRelationsLimit_;
  }

  /**
   * <pre>
   * If less than this number of boxes are present in a no-overlap 2d, we
   * create 4 Booleans per pair of boxes:
   * - Box 2 is after Box 1 on x.
   * - Box 1 is after Box 2 on x.
   * - Box 2 is after Box 1 on y.
   * - Box 1 is after Box 2 on y.
   *
   * Note that at least one of them must be true, and at most one on x and one
   * on y can be true.
   *
   * This can significantly help in closing small problem. The SAT reasoning
   * can be a lot more powerful when we take decision on such positional
   * relations.
   * </pre>
   */
  public int getNoOverlap2DBooleanRelationsLimit() {
    return noOverlap2DBooleanRelationsLimit_;
  }

  /** Returns true if {@code use_timetabling_in_no_overlap_2d} is set. */
  public boolean hasUseTimetablingInNoOverlap2D() {
    return hasUseTimetablingInNoOverlap2D_;
  }

  /**
   * <pre>
   * When this is true, the no_overlap_2d constraint is reinforced with
   * propagators from the cumulative constraints. It consists of ignoring the
   * position of rectangles in one position and projecting the no_overlap_2d on
   * the other dimension to create a cumulative constraint. This is done on both
   * axis. This additional level supplements the default level of reasoning.
   * </pre>
   */
  public boolean getUseTimetablingInNoOverlap2D() {
    return useTimetablingInNoOverlap2D_;
  }

  /** Returns true if {@code use_energetic_reasoning_in_no_overlap_2d} is set. */
  public boolean hasUseEnergeticReasoningInNoOverlap2D() {
    return hasUseEnergeticReasoningInNoOverlap2D_;
  }

  /**
   * <pre>
   * When this is true, the no_overlap_2d constraint is reinforced with
   * energetic reasoning. This additional level supplements the default level of
   * reasoning.
   * </pre>
   */
  public boolean getUseEnergeticReasoningInNoOverlap2D() {
    return useEnergeticReasoningInNoOverlap2D_;
  }

  /** Returns true if {@code use_area_energetic_reasoning_in_no_overlap_2d} is set. */
  public boolean hasUseAreaEnergeticReasoningInNoOverlap2D() {
    return hasUseAreaEnergeticReasoningInNoOverlap2D_;
  }

  /**
   * <pre>
   * When this is true, the no_overlap_2d constraint is reinforced with
   * an energetic reasoning that uses an area-based energy. This can be combined
   * with the two other overlap heuristics above.
   * </pre>
   */
  public boolean getUseAreaEnergeticReasoningInNoOverlap2D() {
    return useAreaEnergeticReasoningInNoOverlap2D_;
  }

  /** Returns true if {@code use_try_edge_reasoning_in_no_overlap_2d} is set. */
  public boolean hasUseTryEdgeReasoningInNoOverlap2D() {
    return hasUseTryEdgeReasoningInNoOverlap2D_;
  }

  /** Returns {@code use_try_edge_reasoning_in_no_overlap_2d}. */
  public boolean getUseTryEdgeReasoningInNoOverlap2D() {
    return useTryEdgeReasoningInNoOverlap2D_;
  }

  /** Returns true if {@code max_pairs_pairwise_reasoning_in_no_overlap_2d} is set. */
  public boolean hasMaxPairsPairwiseReasoningInNoOverlap2D() {
    return hasMaxPairsPairwiseReasoningInNoOverlap2D_;
  }

  /**
   * <pre>
   * If the number of pairs to look is below this threshold, do an extra step of
   * propagation in the no_overlap_2d constraint by looking at all pairs of
   * intervals.
   * </pre>
   */
  public int getMaxPairsPairwiseReasoningInNoOverlap2D() {
    return maxPairsPairwiseReasoningInNoOverlap2D_;
  }

  /** Returns true if {@code maximum_regions_to_split_in_disconnected_no_overlap_2d} is set. */
  public boolean hasMaximumRegionsToSplitInDisconnectedNoOverlap2D() {
    return hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_;
  }

  /**
   * <pre>
   * Detects when the space where items of a no_overlap_2d constraint can placed
   * is disjoint (ie., fixed boxes split the domain). When it is the case, we
   * can introduce a boolean for each pair &lt;item, component&gt; encoding whether
   * the item is in the component or not. Then we replace the original
   * no_overlap_2d constraint by one no_overlap_2d constraint for each
   * component, with the new booleans as the enforcement_literal of the
   * intervals. This is equivalent to expanding the original no_overlap_2d
   * constraint into a bin packing problem with each connected component being a
   * bin. This heuristic is only done when the number of regions to split
   * is less than this parameter and &lt;= 1 disables it.
   * </pre>
   */
  public int getMaximumRegionsToSplitInDisconnectedNoOverlap2D() {
    return maximumRegionsToSplitInDisconnectedNoOverlap2D_;
  }

  /** Returns true if {@code use_linear3_for_no_overlap_2d_precedences} is set. */
  public boolean hasUseLinear3ForNoOverlap2DPrecedences() {
    return hasUseLinear3ForNoOverlap2DPrecedences_;
  }

  /**
   * <pre>
   * When set, this activates a propagator for the no_overlap_2d constraint that
   * uses any eventual linear constraints of the model in the form
   * `&#123;start interval 1&#125; - &#123;end interval 2&#125; + c*w &lt;= ub` to detect that two
   * intervals must overlap in one dimension for some values of `w`. This is
   * particularly useful for problems where the distance between two boxes is
   * part of the model.
   * </pre>
   */
  public boolean getUseLinear3ForNoOverlap2DPrecedences() {
    return useLinear3ForNoOverlap2DPrecedences_;
  }

  /** Returns true if {@code use_dual_scheduling_heuristics} is set. */
  public boolean hasUseDualSchedulingHeuristics() {
    return hasUseDualSchedulingHeuristics_;
  }

  /**
   * <pre>
   * When set, it activates a few scheduling parameters to improve the lower
   * bound of scheduling problems. This is only effective with multiple workers
   * as it modifies the reduced_cost, lb_tree_search, and probing workers.
   * </pre>
   */
  public boolean getUseDualSchedulingHeuristics() {
    return useDualSchedulingHeuristics_;
  }

  /** Returns true if {@code use_all_different_for_circuit} is set. */
  public boolean hasUseAllDifferentForCircuit() {
    return hasUseAllDifferentForCircuit_;
  }

  /**
   * <pre>
   * Turn on extra propagation for the circuit constraint.
   * This can be quite slow.
   * </pre>
   */
  public boolean getUseAllDifferentForCircuit() {
    return useAllDifferentForCircuit_;
  }

  /** Returns true if {@code routing_cut_subset_size_for_binary_relation_bound} is set. */
  public boolean hasRoutingCutSubsetSizeForBinaryRelationBound() {
    return hasRoutingCutSubsetSizeForBinaryRelationBound_;
  }

  /**
   * <pre>
   * If the size of a subset of nodes of a RoutesConstraint is less than this
   * value, use linear constraints of size 1 and 2 (such as capacity and time
   * window constraints) enforced by the arc literals to compute cuts for this
   * subset (unless the subset size is less than
   * routing_cut_subset_size_for_tight_binary_relation_bound, in which case the
   * corresponding algorithm is used instead). The algorithm for these cuts has
   * a O(n^3) complexity, where n is the subset size. Hence the value of this
   * parameter should not be too large (e.g. 10 or 20).
   * </pre>
   */
  public int getRoutingCutSubsetSizeForBinaryRelationBound() {
    return routingCutSubsetSizeForBinaryRelationBound_;
  }

  /** Returns true if {@code routing_cut_subset_size_for_tight_binary_relation_bound} is set. */
  public boolean hasRoutingCutSubsetSizeForTightBinaryRelationBound() {
    return hasRoutingCutSubsetSizeForTightBinaryRelationBound_;
  }

  /**
   * <pre>
   * Similar to above, but with a different algorithm producing better cuts, at
   * the price of a higher O(2^n) complexity, where n is the subset size. Hence
   * the value of this parameter should be small (e.g. less than 10).
   * </pre>
   */
  public int getRoutingCutSubsetSizeForTightBinaryRelationBound() {
    return routingCutSubsetSizeForTightBinaryRelationBound_;
  }

  /** Returns true if {@code routing_cut_subset_size_for_exact_binary_relation_bound} is set. */
  public boolean hasRoutingCutSubsetSizeForExactBinaryRelationBound() {
    return hasRoutingCutSubsetSizeForExactBinaryRelationBound_;
  }

  /**
   * <pre>
   * Similar to above, but with an even stronger algorithm in O(n!). We try to
   * be defensive and abort early or not run that often. Still the value of
   * that parameter shouldn't really be much more than 10.
   * </pre>
   */
  public int getRoutingCutSubsetSizeForExactBinaryRelationBound() {
    return routingCutSubsetSizeForExactBinaryRelationBound_;
  }

  /** Returns true if {@code routing_cut_subset_size_for_shortest_paths_bound} is set. */
  public boolean hasRoutingCutSubsetSizeForShortestPathsBound() {
    return hasRoutingCutSubsetSizeForShortestPathsBound_;
  }

  /**
   * <pre>
   * Similar to routing_cut_subset_size_for_exact_binary_relation_bound but
   * use a bound based on shortest path distances (which respect triangular
   * inequality). This allows to derive bounds that are valid for any superset
   * of a given subset. This is slow, so it shouldn't really be larger than 10.
   * </pre>
   */
  public int getRoutingCutSubsetSizeForShortestPathsBound() {
    return routingCutSubsetSizeForShortestPathsBound_;
  }

  /** Returns true if {@code routing_cut_dp_effort} is set. */
  public boolean hasRoutingCutDpEffort() {
    return hasRoutingCutDpEffort_;
  }

  /**
   * <pre>
   * The amount of "effort" to spend in dynamic programming for computing
   * routing cuts. This is in term of basic operations needed by the algorithm
   * in the worst case, so a value like 1e8 should take less than a second to
   * compute.
   * </pre>
   */
  public double getRoutingCutDpEffort() {
    return routingCutDpEffort_;
  }

  /** Returns true if {@code routing_cut_max_infeasible_path_length} is set. */
  public boolean hasRoutingCutMaxInfeasiblePathLength() {
    return hasRoutingCutMaxInfeasiblePathLength_;
  }

  /**
   * <pre>
   * If the length of an infeasible path is less than this value, a cut will be
   * added to exclude it.
   * </pre>
   */
  public int getRoutingCutMaxInfeasiblePathLength() {
    return routingCutMaxInfeasiblePathLength_;
  }

  /** Returns true if {@code search_branching} is set. */
  public boolean hasSearchBranching() {
    return hasSearchBranching_;
  }

  /** Returns {@code search_branching}. */
  public SatParameters.SearchBranching getSearchBranching() {
    SatParameters.SearchBranching result = SatParameters.SearchBranching.forNumber(searchBranching_);
    return result == null ? SatParameters.SearchBranching.forNumber(0) : result;
  }

  /** Returns the number of {@code search_branching}. */
  public int getSearchBranchingValue() {
    return searchBranching_;
  }

  /** Returns true if {@code hint_conflict_limit} is set. */
  public boolean hasHintConflictLimit() {
    return hasHintConflictLimit_;
  }

  /**
   * <pre>
   * Conflict limit used in the phase that exploit the solution hint.
   * </pre>
   */
  public int getHintConflictLimit() {
    return hintConflictLimit_;
  }

  /** Returns true if {@code try_hint_as_assumptions} is set. */
  public boolean hasTryHintAsAssumptions() {
    return hasTryHintAsAssumptions_;
  }

  /**
   * <pre>
   * Try first to use the hint as assumption in the phase that exploit the
   * solution hint. This should be way faster if the hint is completable.
   * </pre>
   */
  public boolean getTryHintAsAssumptions() {
    return tryHintAsAssumptions_;
  }

  /** Returns true if {@code repair_hint} is set. */
  public boolean hasRepairHint() {
    return hasRepairHint_;
  }

  /**
   * <pre>
   * If true, the solver tries to repair the solution given in the hint. This
   * search terminates after the 'hint_conflict_limit' is reached and the solver
   * switches to regular search. If false, then  we do a FIXED_SEARCH using the
   * hint until the hint_conflict_limit is reached.
   * </pre>
   */
  public boolean getRepairHint() {
    return repairHint_;
  }

  /** Returns true if {@code fix_variables_to_their_hinted_value} is set. */
  public boolean hasFixVariablesToTheirHintedValue() {
    return hasFixVariablesToTheirHintedValue_;
  }

  /**
   * <pre>
   * If true, variables appearing in the solution hints will be fixed to their
   * hinted value.
   * </pre>
   */
  public boolean getFixVariablesToTheirHintedValue() {
    return fixVariablesToTheirHintedValue_;
  }

  /** Returns true if {@code use_probing_search} is set. */
  public boolean hasUseProbingSearch() {
    return hasUseProbingSearch_;
  }

  /**
   * <pre>
   * If true, search will continuously probe Boolean variables, and integer
   * variable bounds. This parameter is set to true in parallel on the probing
   * worker.
   * </pre>
   */
  public boolean getUseProbingSearch() {
    return useProbingSearch_;
  }

  /** Returns true if {@code use_extended_probing} is set. */
  public boolean hasUseExtendedProbing() {
    return hasUseExtendedProbing_;
  }

  /**
   * <pre>
   * Use extended probing (probe bool_or, at_most_one, exactly_one).
   * </pre>
   */
  public boolean getUseExtendedProbing() {
    return useExtendedProbing_;
  }

  /** Returns true if {@code probing_num_combinations_limit} is set. */
  public boolean hasProbingNumCombinationsLimit() {
    return hasProbingNumCombinationsLimit_;
  }

  /**
   * <pre>
   * How many combinations of pairs or triplets of variables we want to scan.
   * </pre>
   */
  public int getProbingNumCombinationsLimit() {
    return probingNumCombinationsLimit_;
  }

  /** Returns true if {@code shaving_deterministic_time_in_probing_search} is set. */
  public boolean hasShavingDeterministicTimeInProbingSearch() {
    return hasShavingDeterministicTimeInProbingSearch_;
  }

  /**
   * <pre>
   * Add a shaving phase (where the solver tries to prove that the lower or
   * upper bound of a variable are infeasible) to the probing search. (&lt;= 0
   * disables it).
   * </pre>
   */
  public double getShavingDeterministicTimeInProbingSearch() {
    return shavingDeterministicTimeInProbingSearch_;
  }

  /** Returns true if {@code shaving_search_deterministic_time} is set. */
  public boolean hasShavingSearchDeterministicTime() {
    return hasShavingSearchDeterministicTime_;
  }

  /**
   * <pre>
   * Specifies the amount of deterministic time spent of each try at shaving a
   * bound in the shaving search.
   * </pre>
   */
  public double getShavingSearchDeterministicTime() {
    return shavingSearchDeterministicTime_;
  }

  /** Returns true if {@code shaving_search_use_random_search} is set. */
  public boolean hasShavingSearchUseRandomSearch() {
    return hasShavingSearchUseRandomSearch_;
  }

  /**
   * <pre>
   * Specify the search heuristic used in the shaving search.
   * </pre>
   */
  public boolean getShavingSearchUseRandomSearch() {
    return shavingSearchUseRandomSearch_;
  }

  /** Returns true if {@code shaving_search_restart_limit} is set. */
  public boolean hasShavingSearchRestartLimit() {
    return hasShavingSearchRestartLimit_;
  }

  /**
   * <pre>
   * Number of conflicts between restarts in the shaving search.
   * If &lt;= 0, we use the default restart policy.
   * </pre>
   */
  public int getShavingSearchRestartLimit() {
    return shavingSearchRestartLimit_;
  }

  /** Returns true if {@code shaving_search_threshold} is set. */
  public boolean hasShavingSearchThreshold() {
    return hasShavingSearchThreshold_;
  }

  /**
   * <pre>
   * Specifies the threshold between two modes in the shaving procedure.
   * If the range of the variable/objective is less than this threshold, then
   * the shaving procedure will try to remove values one by one. Otherwise, it
   * will try to remove one range at a time.
   * </pre>
   */
  public long getShavingSearchThreshold() {
    return shavingSearchThreshold_;
  }

  /** Returns true if {@code objective_shaving_search_threshold} is set. */
  public boolean hasObjectiveShavingSearchThreshold() {
    return hasObjectiveShavingSearchThreshold_;
  }

  /**
   * <pre>
   * For the objective, we prefer to try as much as possible to increase the
   * current best bound. Except if the range is "large".
   * </pre>
   */
  public long getObjectiveShavingSearchThreshold() {
    return objectiveShavingSearchThreshold_;
  }

  /** Returns true if {@code use_objective_lb_search} is set. */
  public boolean hasUseObjectiveLbSearch() {
    return hasUseObjectiveLbSearch_;
  }

  /**
   * <pre>
   * If true, search will search in ascending max objective value (when
   * minimizing) starting from the lower bound of the objective.
   * </pre>
   */
  public boolean getUseObjectiveLbSearch() {
    return useObjectiveLbSearch_;
  }

  /** Returns true if {@code use_objective_shaving_search} is set. */
  public boolean hasUseObjectiveShavingSearch() {
    return hasUseObjectiveShavingSearch_;
  }

  /**
   * <pre>
   * This search differs from the previous search as it will not use assumptions
   * to bound the objective, and it will recreate a full model with the
   * hardcoded objective value.
   * </pre>
   */
  public boolean getUseObjectiveShavingSearch() {
    return useObjectiveShavingSearch_;
  }

  /** Returns true if {@code variables_shaving_level} is set. */
  public boolean hasVariablesShavingLevel() {
    return hasVariablesShavingLevel_;
  }

  /**
   * <pre>
   * This search takes all Boolean or integer variables, and maximize or
   * minimize them in order to reduce their domain. -1 is automatic, otherwise
   * value 0 disables it, and 1, 2, or 3 changes something.
   * </pre>
   */
  public int getVariablesShavingLevel() {
    return variablesShavingLevel_;
  }

  /** Returns true if {@code pseudo_cost_reliability_threshold} is set. */
  public boolean hasPseudoCostReliabilityThreshold() {
    return hasPseudoCostReliabilityThreshold_;
  }

  /**
   * <pre>
   * The solver ignores the pseudo costs of variables with number of recordings
   * less than this threshold.
   * </pre>
   */
  public long getPseudoCostReliabilityThreshold() {
    return pseudoCostReliabilityThreshold_;
  }

  /** Returns true if {@code optimize_with_core} is set. */
  public boolean hasOptimizeWithCore() {
    return hasOptimizeWithCore_;
  }

  /**
   * <pre>
   * The default optimization method is a simple "linear scan", each time trying
   * to find a better solution than the previous one. If this is true, then we
   * use a core-based approach (like in max-SAT) when we try to increase the
   * lower bound instead.
   * </pre>
   */
  public boolean getOptimizeWithCore() {
    return optimizeWithCore_;
  }

  /** Returns true if {@code optimize_with_lb_tree_search} is set. */
  public boolean hasOptimizeWithLbTreeSearch() {
    return hasOptimizeWithLbTreeSearch_;
  }

  /**
   * <pre>
   * Do a more conventional tree search (by opposition to SAT based one) where
   * we keep all the explored node in a tree. This is meant to be used in a
   * portfolio and focus on improving the objective lower bound. Keeping the
   * whole tree allow us to report a better objective lower bound coming from
   * the worst open node in the tree.
   * </pre>
   */
  public boolean getOptimizeWithLbTreeSearch() {
    return optimizeWithLbTreeSearch_;
  }

  /** Returns true if {@code save_lp_basis_in_lb_tree_search} is set. */
  public boolean hasSaveLpBasisInLbTreeSearch() {
    return hasSaveLpBasisInLbTreeSearch_;
  }

  /**
   * <pre>
   * Experimental. Save the current LP basis at each node of the search tree so
   * that when we jump around, we can load it and reduce the number of LP
   * iterations needed.
   *
   * It currently works okay if we do not change the lp with cuts or
   * simplification... More work is needed to make it robust in all cases.
   * </pre>
   */
  public boolean getSaveLpBasisInLbTreeSearch() {
    return saveLpBasisInLbTreeSearch_;
  }

  /** Returns true if {@code binary_search_num_conflicts} is set. */
  public boolean hasBinarySearchNumConflicts() {
    return hasBinarySearchNumConflicts_;
  }

  /**
   * <pre>
   * If non-negative, perform a binary search on the objective variable in order
   * to find an [min, max] interval outside of which the solver proved unsat/sat
   * under this amount of conflict. This can quickly reduce the objective domain
   * on some problems.
   * </pre>
   */
  public int getBinarySearchNumConflicts() {
    return binarySearchNumConflicts_;
  }

  /** Returns true if {@code optimize_with_max_hs} is set. */
  public boolean hasOptimizeWithMaxHs() {
    return hasOptimizeWithMaxHs_;
  }

  /**
   * <pre>
   * This requires setting optimize_with_core to true. If true, use a different
   * core-based algorithm similar to the max-HS algo for max-SAT. This uses an
   * inner CP-SAT solver to solve a linear subproblem extracted from the main
   * one. This is related to the PhD work of tobyodavies&#64; "Automatic Logic-Based
   * Benders Decomposition with MiniZinc"
   * http://aaai.org/ocs/index.php/AAAI/AAAI17/paper/view/14489
   * </pre>
   */
  public boolean getOptimizeWithMaxHs() {
    return optimizeWithMaxHs_;
  }

  /** Returns true if {@code use_feasibility_jump} is set. */
  public boolean hasUseFeasibilityJump() {
    return hasUseFeasibilityJump_;
  }

  /**
   * <pre>
   * Parameters for an heuristic similar to the one described in the paper:
   * "Feasibility Jump: an LP-free Lagrangian MIP heuristic", Bjørnar
   * Luteberget, Giorgio Sartor, 2023, Mathematical Programming Computation.
   * </pre>
   */
  public boolean getUseFeasibilityJump() {
    return useFeasibilityJump_;
  }

  /** Returns true if {@code use_ls_only} is set. */
  public boolean hasUseLsOnly() {
    return hasUseLsOnly_;
  }

  /**
   * <pre>
   * Disable every other type of subsolver, setting this turns CP-SAT into a
   * pure local-search solver.
   * </pre>
   */
  public boolean getUseLsOnly() {
    return useLsOnly_;
  }

  /** Returns true if {@code feasibility_jump_decay} is set. */
  public boolean hasFeasibilityJumpDecay() {
    return hasFeasibilityJumpDecay_;
  }

  /**
   * <pre>
   * On each restart, we randomly choose if we use decay (with this parameter)
   * or no decay.
   * </pre>
   */
  public double getFeasibilityJumpDecay() {
    return feasibilityJumpDecay_;
  }

  /** Returns true if {@code feasibility_jump_linearization_level} is set. */
  public boolean hasFeasibilityJumpLinearizationLevel() {
    return hasFeasibilityJumpLinearizationLevel_;
  }

  /**
   * <pre>
   * How much do we linearize the problem in the local search code.
   * </pre>
   */
  public int getFeasibilityJumpLinearizationLevel() {
    return feasibilityJumpLinearizationLevel_;
  }

  /** Returns true if {@code feasibility_jump_restart_factor} is set. */
  public boolean hasFeasibilityJumpRestartFactor() {
    return hasFeasibilityJumpRestartFactor_;
  }

  /**
   * <pre>
   * This is a factor that directly influence the work before each restart.
   * Increasing it leads to longer restart.
   * </pre>
   */
  public int getFeasibilityJumpRestartFactor() {
    return feasibilityJumpRestartFactor_;
  }

  /** Returns true if {@code feasibility_jump_batch_dtime} is set. */
  public boolean hasFeasibilityJumpBatchDtime() {
    return hasFeasibilityJumpBatchDtime_;
  }

  /**
   * <pre>
   * How much dtime for each LS batch.
   * </pre>
   */
  public double getFeasibilityJumpBatchDtime() {
    return feasibilityJumpBatchDtime_;
  }

  /** Returns true if {@code feasibility_jump_var_randomization_probability} is set. */
  public boolean hasFeasibilityJumpVarRandomizationProbability() {
    return hasFeasibilityJumpVarRandomizationProbability_;
  }

  /**
   * <pre>
   * Probability for a variable to have a non default value upon restarts or
   * perturbations.
   * </pre>
   */
  public double getFeasibilityJumpVarRandomizationProbability() {
    return feasibilityJumpVarRandomizationProbability_;
  }

  /** Returns true if {@code feasibility_jump_var_perburbation_range_ratio} is set. */
  public boolean hasFeasibilityJumpVarPerburbationRangeRatio() {
    return hasFeasibilityJumpVarPerburbationRangeRatio_;
  }

  /**
   * <pre>
   * Max distance between the default value and the pertubated value relative to
   * the range of the domain of the variable.
   * </pre>
   */
  public double getFeasibilityJumpVarPerburbationRangeRatio() {
    return feasibilityJumpVarPerburbationRangeRatio_;
  }

  /** Returns true if {@code feasibility_jump_enable_restarts} is set. */
  public boolean hasFeasibilityJumpEnableRestarts() {
    return hasFeasibilityJumpEnableRestarts_;
  }

  /**
   * <pre>
   * When stagnating, feasibility jump will either restart from a default
   * solution (with some possible randomization), or randomly pertubate the
   * current solution. This parameter selects the first option.
   * </pre>
   */
  public boolean getFeasibilityJumpEnableRestarts() {
    return feasibilityJumpEnableRestarts_;
  }

  /** Returns true if {@code feasibility_jump_max_expanded_constraint_size} is set. */
  public boolean hasFeasibilityJumpMaxExpandedConstraintSize() {
    return hasFeasibilityJumpMaxExpandedConstraintSize_;
  }

  /**
   * <pre>
   * Maximum size of no_overlap or no_overlap_2d constraint for a quadratic
   * expansion. This might look a lot, but by expanding such constraint, we get
   * a linear time evaluation per single variable moves instead of a slow O(n
   * log n) one.
   * </pre>
   */
  public int getFeasibilityJumpMaxExpandedConstraintSize() {
    return feasibilityJumpMaxExpandedConstraintSize_;
  }

  /** Returns true if {@code feasibility_jump_ignore_redundant_constraints} is set. */
  public boolean hasFeasibilityJumpIgnoreRedundantConstraints() {
    return hasFeasibilityJumpIgnoreRedundantConstraints_;
  }

  /**
   * <pre>
   * Whether feasibility jump solver ignores redundant constraints from
   * scheduling relaxation.
   * </pre>
   */
  public boolean getFeasibilityJumpIgnoreRedundantConstraints() {
    return feasibilityJumpIgnoreRedundantConstraints_;
  }

  /** Returns true if {@code lns_ignore_redundant_constraints} is set. */
  public boolean hasLnsIgnoreRedundantConstraints() {
    return hasLnsIgnoreRedundantConstraints_;
  }

  /**
   * <pre>
   * Whether LNS ignores redundant constraints from scheduling relaxation.
   * </pre>
   */
  public boolean getLnsIgnoreRedundantConstraints() {
    return lnsIgnoreRedundantConstraints_;
  }

  /** Returns true if {@code num_violation_ls} is set. */
  public boolean hasNumViolationLs() {
    return hasNumViolationLs_;
  }

  /**
   * <pre>
   * This will create incomplete subsolvers (that are not LNS subsolvers)
   * that use the feasibility jump code to find improving solution, treating
   * the objective improvement as a hard constraint.
   * </pre>
   */
  public int getNumViolationLs() {
    return numViolationLs_;
  }

  /** Returns true if {@code violation_ls_perturbation_period} is set. */
  public boolean hasViolationLsPerturbationPeriod() {
    return hasViolationLsPerturbationPeriod_;
  }

  /**
   * <pre>
   * How long violation_ls should wait before perturbating a solution.
   * </pre>
   */
  public int getViolationLsPerturbationPeriod() {
    return violationLsPerturbationPeriod_;
  }

  /** Returns true if {@code shared_tree_num_workers} is set. */
  public boolean hasSharedTreeNumWorkers() {
    return hasSharedTreeNumWorkers_;
  }

  /**
   * <pre>
   * Enables shared tree search.
   * If positive, start this many complete worker threads to explore a shared
   * search tree. These workers communicate objective bounds and simple decision
   * nogoods relating to the shared prefix of the tree, and will avoid exploring
   * the same subtrees as one another.
   * Specifying a negative number uses a heuristic to select an appropriate
   * number of shared tree workeres based on the total number of workers.
   * </pre>
   */
  public int getSharedTreeNumWorkers() {
    return sharedTreeNumWorkers_;
  }

  /** Returns true if {@code use_shared_tree_search} is set. */
  public boolean hasUseSharedTreeSearch() {
    return hasUseSharedTreeSearch_;
  }

  /**
   * <pre>
   * Set on shared subtree workers. Users should not set this directly.
   * </pre>
   */
  public boolean getUseSharedTreeSearch() {
    return useSharedTreeSearch_;
  }

  /** Returns true if {@code shared_tree_worker_min_restarts_per_subtree} is set. */
  public boolean hasSharedTreeWorkerMinRestartsPerSubtree() {
    return hasSharedTreeWorkerMinRestartsPerSubtree_;
  }

  /**
   * <pre>
   * Minimum restarts before a worker will replace a subtree
   * that looks "bad" based on the average LBD of learned clauses.
   * </pre>
   */
  public int getSharedTreeWorkerMinRestartsPerSubtree() {
    return sharedTreeWorkerMinRestartsPerSubtree_;
  }

  /** Returns true if {@code shared_tree_worker_enable_trail_sharing} is set. */
  public boolean hasSharedTreeWorkerEnableTrailSharing() {
    return hasSharedTreeWorkerEnableTrailSharing_;
  }

  /**
   * <pre>
   * If true, workers share more of the information from their local trail.
   * Specifically, literals implied by the shared tree decisions.
   * </pre>
   */
  public boolean getSharedTreeWorkerEnableTrailSharing() {
    return sharedTreeWorkerEnableTrailSharing_;
  }

  /** Returns true if {@code shared_tree_worker_enable_phase_sharing} is set. */
  public boolean hasSharedTreeWorkerEnablePhaseSharing() {
    return hasSharedTreeWorkerEnablePhaseSharing_;
  }

  /**
   * <pre>
   * If true, shared tree workers share their target phase when returning an
   * assigned subtree for the next worker to use.
   * </pre>
   */
  public boolean getSharedTreeWorkerEnablePhaseSharing() {
    return sharedTreeWorkerEnablePhaseSharing_;
  }

  /** Returns true if {@code shared_tree_worker_propagate_intermediate_nodes} is set. */
  public boolean hasSharedTreeWorkerPropagateIntermediateNodes() {
    return hasSharedTreeWorkerPropagateIntermediateNodes_;
  }

  /**
   * <pre>
   * If true, shared tree workers will propagate all constraints after enqueuing
   * each decision, otherwise they will only propagate at root and leaf nodes on
   * their assigned subtree.
   * </pre>
   */
  public boolean getSharedTreeWorkerPropagateIntermediateNodes() {
    return sharedTreeWorkerPropagateIntermediateNodes_;
  }

  /** Returns true if {@code shared_tree_open_leaves_per_worker} is set. */
  public boolean hasSharedTreeOpenLeavesPerWorker() {
    return hasSharedTreeOpenLeavesPerWorker_;
  }

  /**
   * <pre>
   * How many open leaf nodes should the shared tree maintain per worker.
   * </pre>
   */
  public double getSharedTreeOpenLeavesPerWorker() {
    return sharedTreeOpenLeavesPerWorker_;
  }

  /** Returns true if {@code shared_tree_max_nodes_per_worker} is set. */
  public boolean hasSharedTreeMaxNodesPerWorker() {
    return hasSharedTreeMaxNodesPerWorker_;
  }

  /**
   * <pre>
   * In order to limit total shared memory and communication overhead, limit the
   * total number of nodes that may be generated in the shared tree. If the
   * shared tree runs out of unassigned leaves, workers act as portfolio
   * workers. Note: this limit includes interior nodes, not just leaves.
   * </pre>
   */
  public int getSharedTreeMaxNodesPerWorker() {
    return sharedTreeMaxNodesPerWorker_;
  }

  /** Returns true if {@code shared_tree_split_strategy} is set. */
  public boolean hasSharedTreeSplitStrategy() {
    return hasSharedTreeSplitStrategy_;
  }

  /** Returns {@code shared_tree_split_strategy}. */
  public SatParameters.SharedTreeSplitStrategy getSharedTreeSplitStrategy() {
    SatParameters.SharedTreeSplitStrategy result = SatParameters.SharedTreeSplitStrategy.forNumber(sharedTreeSplitStrategy_);
    return result == null ? SatParameters.SharedTreeSplitStrategy.forNumber(0) : result;
  }

  /** Returns the number of {@code shared_tree_split_strategy}. */
  public int getSharedTreeSplitStrategyValue() {
    return sharedTreeSplitStrategy_;
  }

  /** Returns true if {@code shared_tree_balance_tolerance} is set. */
  public boolean hasSharedTreeBalanceTolerance() {
    return hasSharedTreeBalanceTolerance_;
  }

  /**
   * <pre>
   * How much deeper compared to the ideal max depth of the tree is considered
   * "balanced" enough to still accept a split. Without such a tolerance,
   * sometimes the tree can only be split by a single worker, and they may not
   * generate a split for some time. In contrast, with a tolerance of 1, at
   * least half of all workers should be able to split the tree as soon as a
   * split becomes required. This only has an effect on
   * SPLIT_STRATEGY_BALANCED_TREE and SPLIT_STRATEGY_DISCREPANCY.
   * </pre>
   */
  public int getSharedTreeBalanceTolerance() {
    return sharedTreeBalanceTolerance_;
  }

  /** Returns true if {@code shared_tree_split_min_dtime} is set. */
  public boolean hasSharedTreeSplitMinDtime() {
    return hasSharedTreeSplitMinDtime_;
  }

  /**
   * <pre>
   * How much dtime a worker will wait between proposing splits.
   * This limits the contention in splitting the shared tree, and also reduces
   * the number of too-easy subtrees that are generates.
   * </pre>
   */
  public double getSharedTreeSplitMinDtime() {
    return sharedTreeSplitMinDtime_;
  }

  /** Returns true if {@code enumerate_all_solutions} is set. */
  public boolean hasEnumerateAllSolutions() {
    return hasEnumerateAllSolutions_;
  }

  /**
   * <pre>
   * Whether we enumerate all solutions of a problem without objective.
   *
   * WARNING:
   * - This can be used with num_workers &gt; 1 but then each solutions can be
   *   found more than once, so it is up to the client to deduplicate them.
   * - If keep_all_feasible_solutions_in_presolve is unset, we will set it to
   *   true as otherwise, many feasible solution can just be removed by the
   *   presolve. It is still possible to manually set this to false if one only
   *   wants to enumerate all solutions of the presolved model.
   * </pre>
   */
  public boolean getEnumerateAllSolutions() {
    return enumerateAllSolutions_;
  }

  /** Returns true if {@code keep_all_feasible_solutions_in_presolve} is set. */
  public boolean hasKeepAllFeasibleSolutionsInPresolve() {
    return hasKeepAllFeasibleSolutionsInPresolve_;
  }

  /**
   * <pre>
   * If true, we disable the presolve reductions that remove feasible solutions
   * from the search space. Such solution are usually dominated by a "better"
   * solution that is kept, but depending on the situation, we might want to
   * keep all solutions.
   *
   * A trivial example is when a variable is unused. If this is true, then the
   * presolve will not fix it to an arbitrary value and it will stay in the
   * search space.
   * </pre>
   */
  public boolean getKeepAllFeasibleSolutionsInPresolve() {
    return keepAllFeasibleSolutionsInPresolve_;
  }

  /** Returns true if {@code fill_tightened_domains_in_response} is set. */
  public boolean hasFillTightenedDomainsInResponse() {
    return hasFillTightenedDomainsInResponse_;
  }

  /**
   * <pre>
   * If true, add information about the derived variable domains to the
   * CpSolverResponse. It is an option because it makes the response slighly
   * bigger and there is a bit more work involved during the postsolve to
   * construct it, but it should still have a low overhead. See the
   * tightened_variables field in CpSolverResponse for more details.
   * </pre>
   */
  public boolean getFillTightenedDomainsInResponse() {
    return fillTightenedDomainsInResponse_;
  }

  /** Returns true if {@code fill_additional_solutions_in_response} is set. */
  public boolean hasFillAdditionalSolutionsInResponse() {
    return hasFillAdditionalSolutionsInResponse_;
  }

  /**
   * <pre>
   * If true, the final response addition_solutions field will be filled with
   * all solutions from our solutions pool.
   *
   * Note that if both this field and enumerate_all_solutions is true, we will
   * copy to the pool all of the solution found. So if solution_pool_size is big
   * enough, you can get all solutions this way instead of using the solution
   * callback.
   *
   * Note that this only affect the "final" solution, not the one passed to the
   * solution callbacks.
   * </pre>
   */
  public boolean getFillAdditionalSolutionsInResponse() {
    return fillAdditionalSolutionsInResponse_;
  }

  /** Returns true if {@code instantiate_all_variables} is set. */
  public boolean hasInstantiateAllVariables() {
    return hasInstantiateAllVariables_;
  }

  /**
   * <pre>
   * If true, the solver will add a default integer branching strategy to the
   * already defined search strategy. If not, some variable might still not be
   * fixed at the end of the search. For now we assume these variable can just
   * be set to their lower bound.
   * </pre>
   */
  public boolean getInstantiateAllVariables() {
    return instantiateAllVariables_;
  }

  /** Returns true if {@code auto_detect_greater_than_at_least_one_of} is set. */
  public boolean hasAutoDetectGreaterThanAtLeastOneOf() {
    return hasAutoDetectGreaterThanAtLeastOneOf_;
  }

  /**
   * <pre>
   * If true, then the precedences propagator try to detect for each variable if
   * it has a set of "optional incoming arc" for which at least one of them is
   * present. This is usually useful to have but can be slow on model with a lot
   * of precedence.
   * </pre>
   */
  public boolean getAutoDetectGreaterThanAtLeastOneOf() {
    return autoDetectGreaterThanAtLeastOneOf_;
  }

  /** Returns true if {@code stop_after_first_solution} is set. */
  public boolean hasStopAfterFirstSolution() {
    return hasStopAfterFirstSolution_;
  }

  /**
   * <pre>
   * For an optimization problem, stop the solver as soon as we have a solution.
   * </pre>
   */
  public boolean getStopAfterFirstSolution() {
    return stopAfterFirstSolution_;
  }

  /** Returns true if {@code stop_after_presolve} is set. */
  public boolean hasStopAfterPresolve() {
    return hasStopAfterPresolve_;
  }

  /**
   * <pre>
   * Mainly used when improving the presolver. When true, stops the solver after
   * the presolve is complete (or after loading and root level propagation).
   * </pre>
   */
  public boolean getStopAfterPresolve() {
    return stopAfterPresolve_;
  }

  /** Returns true if {@code stop_after_root_propagation} is set. */
  public boolean hasStopAfterRootPropagation() {
    return hasStopAfterRootPropagation_;
  }

  /** Returns {@code stop_after_root_propagation}. */
  public boolean getStopAfterRootPropagation() {
    return stopAfterRootPropagation_;
  }

  /** Returns true if {@code lns_initial_difficulty} is set. */
  public boolean hasLnsInitialDifficulty() {
    return hasLnsInitialDifficulty_;
  }

  /**
   * <pre>
   * Initial parameters for neighborhood generation.
   * </pre>
   */
  public double getLnsInitialDifficulty() {
    return lnsInitialDifficulty_;
  }

  /** Returns true if {@code lns_initial_deterministic_limit} is set. */
  public boolean hasLnsInitialDeterministicLimit() {
    return hasLnsInitialDeterministicLimit_;
  }

  /** Returns {@code lns_initial_deterministic_limit}. */
  public double getLnsInitialDeterministicLimit() {
    return lnsInitialDeterministicLimit_;
  }

  /** Returns true if {@code use_lns} is set. */
  public boolean hasUseLns() {
    return hasUseLns_;
  }

  /**
   * <pre>
   * Testing parameters used to disable all lns workers.
   * </pre>
   */
  public boolean getUseLns() {
    return useLns_;
  }

  /** Returns true if {@code use_lns_only} is set. */
  public boolean hasUseLnsOnly() {
    return hasUseLnsOnly_;
  }

  /**
   * <pre>
   * Experimental parameters to disable everything but lns.
   * </pre>
   */
  public boolean getUseLnsOnly() {
    return useLnsOnly_;
  }

  /** Returns true if {@code solution_pool_size} is set. */
  public boolean hasSolutionPoolSize() {
    return hasSolutionPoolSize_;
  }

  /**
   * <pre>
   * Size of the top-n different solutions kept by the solver.
   * This parameter must be &gt; 0. Currently, having this larger than one mainly
   * impact the "base" solution chosen for a LNS/LS fragment.
   * </pre>
   */
  public int getSolutionPoolSize() {
    return solutionPoolSize_;
  }

  /** Returns true if {@code solution_pool_diversity_limit} is set. */
  public boolean hasSolutionPoolDiversityLimit() {
    return hasSolutionPoolDiversityLimit_;
  }

  /**
   * <pre>
   * If solution_pool_size is &lt;= this, we will use DP to keep a "diverse" set
   * of solutions (the one further apart via hamming distance) in the pool.
   * Setting this to large value might be slow, especially if your solution are
   * large.
   * </pre>
   */
  public int getSolutionPoolDiversityLimit() {
    return solutionPoolDiversityLimit_;
  }

  /** Returns true if {@code alternative_pool_size} is set. */
  public boolean hasAlternativePoolSize() {
    return hasAlternativePoolSize_;
  }

  /**
   * <pre>
   * In order to not get stuck in local optima, when this is non-zero, we try to
   * also work on "older" solutions with a worse objective value so we get a
   * chance to follow a different LS/LNS trajectory.
   * </pre>
   */
  public int getAlternativePoolSize() {
    return alternativePoolSize_;
  }

  /** Returns true if {@code use_rins_lns} is set. */
  public boolean hasUseRinsLns() {
    return hasUseRinsLns_;
  }

  /**
   * <pre>
   * Turns on relaxation induced neighborhood generator.
   * </pre>
   */
  public boolean getUseRinsLns() {
    return useRinsLns_;
  }

  /** Returns true if {@code use_feasibility_pump} is set. */
  public boolean hasUseFeasibilityPump() {
    return hasUseFeasibilityPump_;
  }

  /**
   * <pre>
   * Adds a feasibility pump subsolver along with lns subsolvers.
   * </pre>
   */
  public boolean getUseFeasibilityPump() {
    return useFeasibilityPump_;
  }

  /** Returns true if {@code use_lb_relax_lns} is set. */
  public boolean hasUseLbRelaxLns() {
    return hasUseLbRelaxLns_;
  }

  /**
   * <pre>
   * Turns on neighborhood generator based on local branching LP. Based on Huang
   * et al., "Local Branching Relaxation Heuristics for Integer Linear
   * Programs", 2023.
   * </pre>
   */
  public boolean getUseLbRelaxLns() {
    return useLbRelaxLns_;
  }

  /** Returns true if {@code lb_relax_num_workers_threshold} is set. */
  public boolean hasLbRelaxNumWorkersThreshold() {
    return hasLbRelaxNumWorkersThreshold_;
  }

  /**
   * <pre>
   * Only use lb-relax if we have at least that many workers.
   * </pre>
   */
  public int getLbRelaxNumWorkersThreshold() {
    return lbRelaxNumWorkersThreshold_;
  }

  /** Returns true if {@code fp_rounding} is set. */
  public boolean hasFpRounding() {
    return hasFpRounding_;
  }

  /** Returns {@code fp_rounding}. */
  public SatParameters.FPRoundingMethod getFpRounding() {
    SatParameters.FPRoundingMethod result = SatParameters.FPRoundingMethod.forNumber(fpRounding_);
    return result == null ? SatParameters.FPRoundingMethod.forNumber(2) : result;
  }

  /** Returns the number of {@code fp_rounding}. */
  public int getFpRoundingValue() {
    return fpRounding_;
  }

  /** Returns true if {@code diversify_lns_params} is set. */
  public boolean hasDiversifyLnsParams() {
    return hasDiversifyLnsParams_;
  }

  /**
   * <pre>
   * If true, registers more lns subsolvers with different parameters.
   * </pre>
   */
  public boolean getDiversifyLnsParams() {
    return diversifyLnsParams_;
  }

  /** Returns true if {@code randomize_search} is set. */
  public boolean hasRandomizeSearch() {
    return hasRandomizeSearch_;
  }

  /**
   * <pre>
   * Randomize fixed search.
   * </pre>
   */
  public boolean getRandomizeSearch() {
    return randomizeSearch_;
  }

  /** Returns true if {@code search_random_variable_pool_size} is set. */
  public boolean hasSearchRandomVariablePoolSize() {
    return hasSearchRandomVariablePoolSize_;
  }

  /**
   * <pre>
   * Search randomization will collect the top
   * 'search_random_variable_pool_size' valued variables, and pick one randomly.
   * The value of the variable is specific to each strategy.
   * </pre>
   */
  public long getSearchRandomVariablePoolSize() {
    return searchRandomVariablePoolSize_;
  }

  /** Returns true if {@code push_all_tasks_toward_start} is set. */
  public boolean hasPushAllTasksTowardStart() {
    return hasPushAllTasksTowardStart_;
  }

  /**
   * <pre>
   * Experimental code: specify if the objective pushes all tasks toward the
   * start of the schedule.
   * </pre>
   */
  public boolean getPushAllTasksTowardStart() {
    return pushAllTasksTowardStart_;
  }

  /** Returns true if {@code use_optional_variables} is set. */
  public boolean hasUseOptionalVariables() {
    return hasUseOptionalVariables_;
  }

  /**
   * <pre>
   * If true, we automatically detect variables whose constraint are always
   * enforced by the same literal and we mark them as optional. This allows
   * to propagate them as if they were present in some situation.
   *
   * TODO(user): This is experimental and seems to lead to wrong optimal in
   * some situation. It should however gives correct solutions. Fix.
   * </pre>
   */
  public boolean getUseOptionalVariables() {
    return useOptionalVariables_;
  }

  /** Returns true if {@code use_exact_lp_reason} is set. */
  public boolean hasUseExactLpReason() {
    return hasUseExactLpReason_;
  }

  /**
   * <pre>
   * The solver usually exploit the LP relaxation of a model. If this option is
   * true, then whatever is infered by the LP will be used like an heuristic to
   * compute EXACT propagation on the IP. So with this option, there is no
   * numerical imprecision issues.
   * </pre>
   */
  public boolean getUseExactLpReason() {
    return useExactLpReason_;
  }

  /** Returns true if {@code use_combined_no_overlap} is set. */
  public boolean hasUseCombinedNoOverlap() {
    return hasUseCombinedNoOverlap_;
  }

  /**
   * <pre>
   * This can be beneficial if there is a lot of no-overlap constraints but a
   * relatively low number of different intervals in the problem. Like 1000
   * intervals, but 1M intervals in the no-overlap constraints covering them.
   * </pre>
   */
  public boolean getUseCombinedNoOverlap() {
    return useCombinedNoOverlap_;
  }

  /** Returns true if {@code at_most_one_max_expansion_size} is set. */
  public boolean hasAtMostOneMaxExpansionSize() {
    return hasAtMostOneMaxExpansionSize_;
  }

  /**
   * <pre>
   * All at_most_one constraints with a size &lt;= param will be replaced by a
   * quadratic number of binary implications.
   * </pre>
   */
  public int getAtMostOneMaxExpansionSize() {
    return atMostOneMaxExpansionSize_;
  }

  /** Returns true if {@code catch_sigint_signal} is set. */
  public boolean hasCatchSigintSignal() {
    return hasCatchSigintSignal_;
  }

  /**
   * <pre>
   * Indicates if the CP-SAT layer should catch Control-C (SIGINT) signals
   * when calling solve. If set, catching the SIGINT signal will terminate the
   * search gracefully, as if a time limit was reached.
   * </pre>
   */
  public boolean getCatchSigintSignal() {
    return catchSigintSignal_;
  }

  /** Returns true if {@code use_implied_bounds} is set. */
  public boolean hasUseImpliedBounds() {
    return hasUseImpliedBounds_;
  }

  /**
   * <pre>
   * Stores and exploits "implied-bounds" in the solver. That is, relations of
   * the form literal =&gt; (var &gt;= bound). This is currently used to derive
   * stronger cuts.
   * </pre>
   */
  public boolean getUseImpliedBounds() {
    return useImpliedBounds_;
  }

  /** Returns true if {@code polish_lp_solution} is set. */
  public boolean hasPolishLpSolution() {
    return hasPolishLpSolution_;
  }

  /**
   * <pre>
   * Whether we try to do a few degenerate iteration at the end of an LP solve
   * to minimize the fractionality of the integer variable in the basis. This
   * helps on some problems, but not so much on others. It also cost of bit of
   * time to do such polish step.
   * </pre>
   */
  public boolean getPolishLpSolution() {
    return polishLpSolution_;
  }

  /** Returns true if {@code lp_primal_tolerance} is set. */
  public boolean hasLpPrimalTolerance() {
    return hasLpPrimalTolerance_;
  }

  /**
   * <pre>
   * The internal LP tolerances used by CP-SAT. These applies to the internal
   * and scaled problem. If the domains of your variables are large it might be
   * good to use lower tolerances. If your problem is binary with low
   * coefficients, it might be good to use higher ones to speed-up the lp
   * solves.
   * </pre>
   */
  public double getLpPrimalTolerance() {
    return lpPrimalTolerance_;
  }

  /** Returns true if {@code lp_dual_tolerance} is set. */
  public boolean hasLpDualTolerance() {
    return hasLpDualTolerance_;
  }

  /** Returns {@code lp_dual_tolerance}. */
  public double getLpDualTolerance() {
    return lpDualTolerance_;
  }

  /** Returns true if {@code convert_intervals} is set. */
  public boolean hasConvertIntervals() {
    return hasConvertIntervals_;
  }

  /**
   * <pre>
   * Temporary flag util the feature is more mature. This convert intervals to
   * the newer proto format that support affine start/var/end instead of just
   * variables.
   * </pre>
   */
  public boolean getConvertIntervals() {
    return convertIntervals_;
  }

  /** Returns true if {@code symmetry_level} is set. */
  public boolean hasSymmetryLevel() {
    return hasSymmetryLevel_;
  }

  /**
   * <pre>
   * Whether we try to automatically detect the symmetries in a model and
   * exploit them. Currently, at level 1 we detect them in presolve and try
   * to fix Booleans. At level 2, we also do some form of dynamic symmetry
   * breaking during search. At level 3, we also detect symmetries for very
   * large models, which can be slow. At level 4, we try to break as much
   * symmetry as possible in presolve.
   * </pre>
   */
  public int getSymmetryLevel() {
    return symmetryLevel_;
  }

  /** Returns true if {@code use_symmetry_in_lp} is set. */
  public boolean hasUseSymmetryInLp() {
    return hasUseSymmetryInLp_;
  }

  /**
   * <pre>
   * When we have symmetry, it is possible to "fold" all variables from the same
   * orbit into a single variable, while having the same power of LP relaxation.
   * This can help significantly on symmetric problem. However there is
   * currently a bit of overhead as the rest of the solver need to do some
   * translation between the folded LP and the rest of the problem.
   * </pre>
   */
  public boolean getUseSymmetryInLp() {
    return useSymmetryInLp_;
  }

  /** Returns true if {@code keep_symmetry_in_presolve} is set. */
  public boolean hasKeepSymmetryInPresolve() {
    return hasKeepSymmetryInPresolve_;
  }

  /**
   * <pre>
   * Experimental. This will compute the symmetry of the problem once and for
   * all. All presolve operations we do should keep the symmetry group intact
   * or modify it properly. For now we have really little support for this. We
   * will disable a bunch of presolve operations that could be supported.
   * </pre>
   */
  public boolean getKeepSymmetryInPresolve() {
    return keepSymmetryInPresolve_;
  }

  /** Returns true if {@code symmetry_detection_deterministic_time_limit} is set. */
  public boolean hasSymmetryDetectionDeterministicTimeLimit() {
    return hasSymmetryDetectionDeterministicTimeLimit_;
  }

  /**
   * <pre>
   * Deterministic time limit for symmetry detection.
   * </pre>
   */
  public double getSymmetryDetectionDeterministicTimeLimit() {
    return symmetryDetectionDeterministicTimeLimit_;
  }

  /** Returns true if {@code new_linear_propagation} is set. */
  public boolean hasNewLinearPropagation() {
    return hasNewLinearPropagation_;
  }

  /**
   * <pre>
   * The new linear propagation code treat all constraints at once and use
   * an adaptation of Bellman-Ford-Tarjan to propagate constraint in a smarter
   * order and potentially detect propagation cycle earlier.
   * </pre>
   */
  public boolean getNewLinearPropagation() {
    return newLinearPropagation_;
  }

  /** Returns true if {@code linear_split_size} is set. */
  public boolean hasLinearSplitSize() {
    return hasLinearSplitSize_;
  }

  /**
   * <pre>
   * Linear constraints that are not pseudo-Boolean and that are longer than
   * this size will be split into sqrt(size) intermediate sums in order to have
   * faster propation in the CP engine.
   * </pre>
   */
  public int getLinearSplitSize() {
    return linearSplitSize_;
  }

  /** Returns true if {@code linearization_level} is set. */
  public boolean hasLinearizationLevel() {
    return hasLinearizationLevel_;
  }

  /**
   * <pre>
   * A non-negative level indicating the type of constraints we consider in the
   * LP relaxation. At level zero, no LP relaxation is used. At level 1, only
   * the linear constraint and full encoding are added. At level 2, we also add
   * all the Boolean constraints.
   * </pre>
   */
  public int getLinearizationLevel() {
    return linearizationLevel_;
  }

  /** Returns true if {@code boolean_encoding_level} is set. */
  public boolean hasBooleanEncodingLevel() {
    return hasBooleanEncodingLevel_;
  }

  /**
   * <pre>
   * A non-negative level indicating how much we should try to fully encode
   * Integer variables as Boolean.
   * </pre>
   */
  public int getBooleanEncodingLevel() {
    return booleanEncodingLevel_;
  }

  /** Returns true if {@code max_domain_size_when_encoding_eq_neq_constraints} is set. */
  public boolean hasMaxDomainSizeWhenEncodingEqNeqConstraints() {
    return hasMaxDomainSizeWhenEncodingEqNeqConstraints_;
  }

  /**
   * <pre>
   * When loading a*x + b*y ==/!= c when x and y are both fully encoded.
   * The solver may decide to replace the linear equation by a set of clauses.
   * This is triggered if the sizes of the domains of x and y are below the
   * threshold.
   * </pre>
   */
  public int getMaxDomainSizeWhenEncodingEqNeqConstraints() {
    return maxDomainSizeWhenEncodingEqNeqConstraints_;
  }

  /** Returns true if {@code max_num_cuts} is set. */
  public boolean hasMaxNumCuts() {
    return hasMaxNumCuts_;
  }

  /**
   * <pre>
   * The limit on the number of cuts in our cut pool. When this is reached we do
   * not generate cuts anymore.
   *
   * TODO(user): We should probably remove this parameters, and just always
   * generate cuts but only keep the best n or something.
   * </pre>
   */
  public int getMaxNumCuts() {
    return maxNumCuts_;
  }

  /** Returns true if {@code cut_level} is set. */
  public boolean hasCutLevel() {
    return hasCutLevel_;
  }

  /**
   * <pre>
   * Control the global cut effort. Zero will turn off all cut. For now we just
   * have one level. Note also that most cuts are only used at linearization
   * level &gt;= 2.
   * </pre>
   */
  public int getCutLevel() {
    return cutLevel_;
  }

  /** Returns true if {@code only_add_cuts_at_level_zero} is set. */
  public boolean hasOnlyAddCutsAtLevelZero() {
    return hasOnlyAddCutsAtLevelZero_;
  }

  /**
   * <pre>
   * For the cut that can be generated at any level, this control if we only
   * try to generate them at the root node.
   * </pre>
   */
  public boolean getOnlyAddCutsAtLevelZero() {
    return onlyAddCutsAtLevelZero_;
  }

  /** Returns true if {@code add_objective_cut} is set. */
  public boolean hasAddObjectiveCut() {
    return hasAddObjectiveCut_;
  }

  /**
   * <pre>
   * When the LP objective is fractional, do we add the cut that forces the
   * linear objective expression to be greater or equal to this fractional value
   * rounded up? We can always do that since our objective is integer, and
   * combined with MIR heuristic to reduce the coefficient of such cut, it can
   * help.
   * </pre>
   */
  public boolean getAddObjectiveCut() {
    return addObjectiveCut_;
  }

  /** Returns true if {@code add_cg_cuts} is set. */
  public boolean hasAddCgCuts() {
    return hasAddCgCuts_;
  }

  /**
   * <pre>
   * Whether we generate and add Chvatal-Gomory cuts to the LP at root node.
   * Note that for now, this is not heavily tuned.
   * </pre>
   */
  public boolean getAddCgCuts() {
    return addCgCuts_;
  }

  /** Returns true if {@code add_mir_cuts} is set. */
  public boolean hasAddMirCuts() {
    return hasAddMirCuts_;
  }

  /**
   * <pre>
   * Whether we generate MIR cuts at root node.
   * Note that for now, this is not heavily tuned.
   * </pre>
   */
  public boolean getAddMirCuts() {
    return addMirCuts_;
  }

  /** Returns true if {@code add_zero_half_cuts} is set. */
  public boolean hasAddZeroHalfCuts() {
    return hasAddZeroHalfCuts_;
  }

  /**
   * <pre>
   * Whether we generate Zero-Half cuts at root node.
   * Note that for now, this is not heavily tuned.
   * </pre>
   */
  public boolean getAddZeroHalfCuts() {
    return addZeroHalfCuts_;
  }

  /** Returns true if {@code add_clique_cuts} is set. */
  public boolean hasAddCliqueCuts() {
    return hasAddCliqueCuts_;
  }

  /**
   * <pre>
   * Whether we generate clique cuts from the binary implication graph. Note
   * that as the search goes on, this graph will contains new binary clauses
   * learned by the SAT engine.
   * </pre>
   */
  public boolean getAddCliqueCuts() {
    return addCliqueCuts_;
  }

  /** Returns true if {@code add_rlt_cuts} is set. */
  public boolean hasAddRltCuts() {
    return hasAddRltCuts_;
  }

  /**
   * <pre>
   * Whether we generate RLT cuts. This is still experimental but can help on
   * binary problem with a lot of clauses of size 3.
   * </pre>
   */
  public boolean getAddRltCuts() {
    return addRltCuts_;
  }

  /** Returns true if {@code max_all_diff_cut_size} is set. */
  public boolean hasMaxAllDiffCutSize() {
    return hasMaxAllDiffCutSize_;
  }

  /**
   * <pre>
   * Cut generator for all diffs can add too many cuts for large all_diff
   * constraints. This parameter restricts the large all_diff constraints to
   * have a cut generator.
   * </pre>
   */
  public int getMaxAllDiffCutSize() {
    return maxAllDiffCutSize_;
  }

  /** Returns true if {@code add_lin_max_cuts} is set. */
  public boolean hasAddLinMaxCuts() {
    return hasAddLinMaxCuts_;
  }

  /**
   * <pre>
   * For the lin max constraints, generates the cuts described in "Strong
   * mixed-integer programming formulations for trained neural networks" by Ross
   * Anderson et. (https://arxiv.org/pdf/1811.01988.pdf)
   * </pre>
   */
  public boolean getAddLinMaxCuts() {
    return addLinMaxCuts_;
  }

  /** Returns true if {@code max_integer_rounding_scaling} is set. */
  public boolean hasMaxIntegerRoundingScaling() {
    return hasMaxIntegerRoundingScaling_;
  }

  /**
   * <pre>
   * In the integer rounding procedure used for MIR and Gomory cut, the maximum
   * "scaling" we use (must be positive). The lower this is, the lower the
   * integer coefficients of the cut will be. Note that cut generated by lower
   * values are not necessarily worse than cut generated by larger value. There
   * is no strict dominance relationship.
   *
   * Setting this to 2 result in the "strong fractional rouding" of Letchford
   * and Lodi.
   * </pre>
   */
  public int getMaxIntegerRoundingScaling() {
    return maxIntegerRoundingScaling_;
  }

  /** Returns true if {@code add_lp_constraints_lazily} is set. */
  public boolean hasAddLpConstraintsLazily() {
    return hasAddLpConstraintsLazily_;
  }

  /**
   * <pre>
   * If true, we start by an empty LP, and only add constraints not satisfied
   * by the current LP solution batch by batch. A constraint that is only added
   * like this is known as a "lazy" constraint in the literature, except that we
   * currently consider all constraints as lazy here.
   * </pre>
   */
  public boolean getAddLpConstraintsLazily() {
    return addLpConstraintsLazily_;
  }

  /** Returns true if {@code root_lp_iterations} is set. */
  public boolean hasRootLpIterations() {
    return hasRootLpIterations_;
  }

  /**
   * <pre>
   * Even at the root node, we do not want to spend too much time on the LP if
   * it is "difficult". So we solve it in "chunks" of that many iterations. The
   * solve will be continued down in the tree or the next time we go back to the
   * root node.
   * </pre>
   */
  public int getRootLpIterations() {
    return rootLpIterations_;
  }

  /** Returns true if {@code min_orthogonality_for_lp_constraints} is set. */
  public boolean hasMinOrthogonalityForLpConstraints() {
    return hasMinOrthogonalityForLpConstraints_;
  }

  /**
   * <pre>
   * While adding constraints, skip the constraints which have orthogonality
   * less than 'min_orthogonality_for_lp_constraints' with already added
   * constraints during current call. Orthogonality is defined as 1 -
   * cosine(vector angle between constraints). A value of zero disable this
   * feature.
   * </pre>
   */
  public double getMinOrthogonalityForLpConstraints() {
    return minOrthogonalityForLpConstraints_;
  }

  /** Returns true if {@code max_cut_rounds_at_level_zero} is set. */
  public boolean hasMaxCutRoundsAtLevelZero() {
    return hasMaxCutRoundsAtLevelZero_;
  }

  /**
   * <pre>
   * Max number of time we perform cut generation and resolve the LP at level 0.
   * </pre>
   */
  public int getMaxCutRoundsAtLevelZero() {
    return maxCutRoundsAtLevelZero_;
  }

  /** Returns true if {@code max_consecutive_inactive_count} is set. */
  public boolean hasMaxConsecutiveInactiveCount() {
    return hasMaxConsecutiveInactiveCount_;
  }

  /**
   * <pre>
   * If a constraint/cut in LP is not active for that many consecutive OPTIMAL
   * solves, remove it from the LP. Note that it might be added again later if
   * it become violated by the current LP solution.
   * </pre>
   */
  public int getMaxConsecutiveInactiveCount() {
    return maxConsecutiveInactiveCount_;
  }

  /** Returns true if {@code cut_max_active_count_value} is set. */
  public boolean hasCutMaxActiveCountValue() {
    return hasCutMaxActiveCountValue_;
  }

  /**
   * <pre>
   * These parameters are similar to sat clause management activity parameters.
   * They are effective only if the number of generated cuts exceed the storage
   * limit. Default values are based on a few experiments on miplib instances.
   * </pre>
   */
  public double getCutMaxActiveCountValue() {
    return cutMaxActiveCountValue_;
  }

  /** Returns true if {@code cut_active_count_decay} is set. */
  public boolean hasCutActiveCountDecay() {
    return hasCutActiveCountDecay_;
  }

  /** Returns {@code cut_active_count_decay}. */
  public double getCutActiveCountDecay() {
    return cutActiveCountDecay_;
  }

  /** Returns true if {@code cut_cleanup_target} is set. */
  public boolean hasCutCleanupTarget() {
    return hasCutCleanupTarget_;
  }

  /**
   * <pre>
   * Target number of constraints to remove during cleanup.
   * </pre>
   */
  public int getCutCleanupTarget() {
    return cutCleanupTarget_;
  }

  /** Returns true if {@code new_constraints_batch_size} is set. */
  public boolean hasNewConstraintsBatchSize() {
    return hasNewConstraintsBatchSize_;
  }

  /**
   * <pre>
   * Add that many lazy constraints (or cuts) at once in the LP. Note that at
   * the beginning of the solve, we do add more than this.
   * </pre>
   */
  public int getNewConstraintsBatchSize() {
    return newConstraintsBatchSize_;
  }

  /** Returns true if {@code exploit_integer_lp_solution} is set. */
  public boolean hasExploitIntegerLpSolution() {
    return hasExploitIntegerLpSolution_;
  }

  /**
   * <pre>
   * If true and the Lp relaxation of the problem has an integer optimal
   * solution, try to exploit it. Note that since the LP relaxation may not
   * contain all the constraints, such a solution is not necessarily a solution
   * of the full problem.
   * </pre>
   */
  public boolean getExploitIntegerLpSolution() {
    return exploitIntegerLpSolution_;
  }

  /** Returns true if {@code exploit_all_lp_solution} is set. */
  public boolean hasExploitAllLpSolution() {
    return hasExploitAllLpSolution_;
  }

  /**
   * <pre>
   * If true and the Lp relaxation of the problem has a solution, try to exploit
   * it. This is same as above except in this case the lp solution might not be
   * an integer solution.
   * </pre>
   */
  public boolean getExploitAllLpSolution() {
    return exploitAllLpSolution_;
  }

  /** Returns true if {@code exploit_best_solution} is set. */
  public boolean hasExploitBestSolution() {
    return hasExploitBestSolution_;
  }

  /**
   * <pre>
   * When branching on a variable, follow the last best solution value.
   * </pre>
   */
  public boolean getExploitBestSolution() {
    return exploitBestSolution_;
  }

  /** Returns true if {@code exploit_relaxation_solution} is set. */
  public boolean hasExploitRelaxationSolution() {
    return hasExploitRelaxationSolution_;
  }

  /**
   * <pre>
   * When branching on a variable, follow the last best relaxation solution
   * value. We use the relaxation with the tightest bound on the objective as
   * the best relaxation solution.
   * </pre>
   */
  public boolean getExploitRelaxationSolution() {
    return exploitRelaxationSolution_;
  }

  /** Returns true if {@code exploit_objective} is set. */
  public boolean hasExploitObjective() {
    return hasExploitObjective_;
  }

  /**
   * <pre>
   * When branching an a variable that directly affect the objective,
   * branch on the value that lead to the best objective first.
   * </pre>
   */
  public boolean getExploitObjective() {
    return exploitObjective_;
  }

  /** Returns true if {@code detect_linearized_product} is set. */
  public boolean hasDetectLinearizedProduct() {
    return hasDetectLinearizedProduct_;
  }

  /**
   * <pre>
   * Infer products of Boolean or of Boolean time IntegerVariable from the
   * linear constrainst in the problem. This can be used in some cuts, altough
   * for now we don't really exploit it.
   * </pre>
   */
  public boolean getDetectLinearizedProduct() {
    return detectLinearizedProduct_;
  }

  /** Returns true if {@code use_new_integer_conflict_resolution} is set. */
  public boolean hasUseNewIntegerConflictResolution() {
    return hasUseNewIntegerConflictResolution_;
  }

  /**
   * <pre>
   * This should be better on integer problems.
   * We only trigger it if there is a least 10 non-binary variables.
   * </pre>
   */
  public boolean getUseNewIntegerConflictResolution() {
    return useNewIntegerConflictResolution_;
  }

  /** Returns true if {@code create_1uip_boolean_during_icr} is set. */
  public boolean hasCreate1UipBooleanDuringIcr() {
    return hasCreate1UipBooleanDuringIcr_;
  }

  /**
   * <pre>
   * If true, and during integer conflict resolution (icr) the 1-UIP is an
   * integer literal for which we do not have an associated Boolean. Create one.
   * </pre>
   */
  public boolean getCreate1UipBooleanDuringIcr() {
    return create1UipBooleanDuringIcr_;
  }

  /** Returns true if {@code mip_max_bound} is set. */
  public boolean hasMipMaxBound() {
    return hasMipMaxBound_;
  }

  /**
   * <pre>
   * We need to bound the maximum magnitude of the variables for CP-SAT, and
   * that is the bound we use. If the MIP model expect larger variable value in
   * the solution, then the converted model will likely not be relevant.
   * </pre>
   */
  public double getMipMaxBound() {
    return mipMaxBound_;
  }

  /** Returns true if {@code mip_var_scaling} is set. */
  public boolean hasMipVarScaling() {
    return hasMipVarScaling_;
  }

  /**
   * <pre>
   * All continuous variable of the problem will be multiplied by this factor.
   * By default, we don't do any variable scaling and rely on the MIP model to
   * specify continuous variable domain with the wanted precision.
   * </pre>
   */
  public double getMipVarScaling() {
    return mipVarScaling_;
  }

  /** Returns true if {@code mip_scale_large_domain} is set. */
  public boolean hasMipScaleLargeDomain() {
    return hasMipScaleLargeDomain_;
  }

  /**
   * <pre>
   * If this is false, then mip_var_scaling is only applied to variables with
   * "small" domain. If it is true, we scale all floating point variable
   * independenlty of their domain.
   * </pre>
   */
  public boolean getMipScaleLargeDomain() {
    return mipScaleLargeDomain_;
  }

  /** Returns true if {@code mip_automatically_scale_variables} is set. */
  public boolean hasMipAutomaticallyScaleVariables() {
    return hasMipAutomaticallyScaleVariables_;
  }

  /**
   * <pre>
   * If true, some continuous variable might be automatically scaled. For now,
   * this is only the case where we detect that a variable is actually an
   * integer multiple of a constant. For instance, variables of the form k * 0.5
   * are quite frequent, and if we detect this, we will scale such variable
   * domain by 2 to make it implied integer.
   * </pre>
   */
  public boolean getMipAutomaticallyScaleVariables() {
    return mipAutomaticallyScaleVariables_;
  }

  /** Returns true if {@code mip_scale_to_max_bound} is set. */
  public boolean hasMipScaleToMaxBound() {
    return hasMipScaleToMaxBound_;
  }

  /**
   * <pre>
   * If true, both mip_var_scaling and mip_scale_large_domain are ignored.
   * Instead, we scale any floating point variable by a power of two so that
   * its domain is the largest that fit in [-mip_max_bound, mip_max_bound].
   * Note that any domain already larger than this is ignored.
   * </pre>
   */
  public boolean getMipScaleToMaxBound() {
    return mipScaleToMaxBound_;
  }

  /** Returns true if {@code mip_presolve_restrict_bounds} is set. */
  public boolean hasMipPresolveRestrictBounds() {
    return hasMipPresolveRestrictBounds_;
  }

  /**
   * <pre>
   * If true, use dual-reasoning to try to restrict the domains of the variable
   * before we scale the model to integer. This can help with the scaling
   * significantly.
   * </pre>
   */
  public boolean getMipPresolveRestrictBounds() {
    return mipPresolveRestrictBounds_;
  }

  /** Returns true if {@code only_solve_ip} is set. */
  public boolean hasOnlySolveIp() {
    return hasOnlySolveIp_;
  }

  /**
   * <pre>
   * If one try to solve a MIP model with CP-SAT, because we assume all variable
   * to be integer after scaling, we will not necessarily have the correct
   * optimal. Note however that all feasible solutions are valid since we will
   * just solve a more restricted version of the original problem.
   *
   * This parameters is here to prevent user to think the solution is optimal
   * when it might not be. One will need to manually set this to false to solve
   * a MIP model where the optimal might be different.
   *
   * Note that this is tested after some MIP presolve steps, so even if not
   * all original variable are integer, we might end up with a pure IP after
   * presolve and after implied integer detection.
   * </pre>
   */
  public boolean getOnlySolveIp() {
    return onlySolveIp_;
  }

  /** Returns true if {@code mip_wanted_precision} is set. */
  public boolean hasMipWantedPrecision() {
    return hasMipWantedPrecision_;
  }

  /**
   * <pre>
   * When scaling constraint with double coefficients to integer coefficients,
   * we will multiply by a power of 2 and round the coefficients. We will choose
   * the lowest power such that we have no potential overflow (see
   * mip_max_activity_exponent) and the worst case constraint activity error
   * does not exceed this threshold.
   *
   * Note that we also detect constraint with rational coefficients and scale
   * them accordingly when it seems better instead of using a power of 2.
   *
   * We also relax all constraint bounds by this absolute value. For pure
   * integer constraint, if this value if lower than one, this will not change
   * anything. However it is needed when scaling MIP problems.
   *
   * If we manage to scale a constraint correctly, the maximum error we can make
   * will be twice this value (once for the scaling error and once for the
   * relaxed bounds). If we are not able to scale that well, we will display
   * that fact but still scale as best as we can.
   * </pre>
   */
  public double getMipWantedPrecision() {
    return mipWantedPrecision_;
  }

  /** Returns true if {@code mip_max_activity_exponent} is set. */
  public boolean hasMipMaxActivityExponent() {
    return hasMipMaxActivityExponent_;
  }

  /**
   * <pre>
   * To avoid integer overflow, we always force the maximum possible constraint
   * activity (and objective value) according to the initial variable domain to
   * be smaller than 2 to this given power. Because of this, we cannot always
   * reach the "mip_wanted_precision" parameter above.
   *
   * This can go as high as 62, but some internal algo currently abort early if
   * they might run into integer overflow, so it is better to keep it a bit
   * lower than this.
   * </pre>
   */
  public int getMipMaxActivityExponent() {
    return mipMaxActivityExponent_;
  }

  /** Returns true if {@code mip_check_precision} is set. */
  public boolean hasMipCheckPrecision() {
    return hasMipCheckPrecision_;
  }

  /**
   * <pre>
   * As explained in mip_precision and mip_max_activity_exponent, we cannot
   * always reach the wanted precision during scaling. We use this threshold to
   * enphasize in the logs when the precision seems bad.
   * </pre>
   */
  public double getMipCheckPrecision() {
    return mipCheckPrecision_;
  }

  /** Returns true if {@code mip_compute_true_objective_bound} is set. */
  public boolean hasMipComputeTrueObjectiveBound() {
    return hasMipComputeTrueObjectiveBound_;
  }

  /**
   * <pre>
   * Even if we make big error when scaling the objective, we can always derive
   * a correct lower bound on the original objective by using the exact lower
   * bound on the scaled integer version of the objective. This should be fast,
   * but if you don't care about having a precise lower bound, you can turn it
   * off.
   * </pre>
   */
  public boolean getMipComputeTrueObjectiveBound() {
    return mipComputeTrueObjectiveBound_;
  }

  /** Returns true if {@code mip_max_valid_magnitude} is set. */
  public boolean hasMipMaxValidMagnitude() {
    return hasMipMaxValidMagnitude_;
  }

  /**
   * <pre>
   * Any finite values in the input MIP must be below this threshold, otherwise
   * the model will be reported invalid. This is needed to avoid floating point
   * overflow when evaluating bounds * coeff for instance. We are a bit more
   * defensive, but in practice, users shouldn't use super large values in a
   * MIP.
   * </pre>
   */
  public double getMipMaxValidMagnitude() {
    return mipMaxValidMagnitude_;
  }

  /** Returns true if {@code mip_treat_high_magnitude_bounds_as_infinity} is set. */
  public boolean hasMipTreatHighMagnitudeBoundsAsInfinity() {
    return hasMipTreatHighMagnitudeBoundsAsInfinity_;
  }

  /**
   * <pre>
   * By default, any variable/constraint bound with a finite value and a
   * magnitude greater than the mip_max_valid_magnitude will result with a
   * invalid model. This flags change the behavior such that such bounds are
   * silently transformed to +∞ or -∞.
   *
   * It is recommended to keep it at false, and create valid bounds.
   * </pre>
   */
  public boolean getMipTreatHighMagnitudeBoundsAsInfinity() {
    return mipTreatHighMagnitudeBoundsAsInfinity_;
  }

  /** Returns true if {@code mip_drop_tolerance} is set. */
  public boolean hasMipDropTolerance() {
    return hasMipDropTolerance_;
  }

  /**
   * <pre>
   * Any value in the input mip with a magnitude lower than this will be set to
   * zero. This is to avoid some issue in LP presolving.
   * </pre>
   */
  public double getMipDropTolerance() {
    return mipDropTolerance_;
  }

  /** Returns true if {@code mip_presolve_level} is set. */
  public boolean hasMipPresolveLevel() {
    return hasMipPresolveLevel_;
  }

  /**
   * <pre>
   * When solving a MIP, we do some basic floating point presolving before
   * scaling the problem to integer to be handled by CP-SAT. This control how
   * much of that presolve we do. It can help to better scale floating point
   * model, but it is not always behaving nicely.
   * </pre>
   */
  public int getMipPresolveLevel() {
    return mipPresolveLevel_;
  }

  /** Returns true if {@code mip_use_tight_bounds} is set. */
  public boolean hasMipUseTightBounds() {
    return hasMipUseTightBounds_;
  }

  /**
   * <pre>
   * Experimental. If true, we do not change how the scaling is done, but:
   * - We compute tighter error bounds on the result.
   * - We try to relax the constraint bound just enough so that a feasible
   *   solution of the original problem with mip_tight_precision is not lost.
   * - But we still do not relax more than mip_wanted_precision and hope for the
   *   best.
   * </pre>
   */
  public boolean getMipUseTightBounds() {
    return mipUseTightBounds_;
  }

  /** Returns true if {@code mip_tight_precision} is set. */
  public boolean hasMipTightPrecision() {
    return hasMipTightPrecision_;
  }

  /** Returns {@code mip_tight_precision}. */
  public double getMipTightPrecision() {
    return mipTightPrecision_;
  }

  /** Returns true if {@code mip_verify_solution} is set. */
  public boolean hasMipVerifySolution() {
    return hasMipVerifySolution_;
  }

  /**
   * <pre>
   * If true, we check that the returned solution is feasible with respect to
   * the input problem. This currently just display some logs with the maximum
   * constraint violation or variable bounds error of the returned solution.
   *
   * It is off by default because this require keeping a copy of the original
   * input model before we do any presolving to it.
   * </pre>
   */
  public boolean getMipVerifySolution() {
    return mipVerifySolution_;
  }

  /** Returns true if {@code mip_proper_relaxation} is set. */
  public boolean hasMipProperRelaxation() {
    return hasMipProperRelaxation_;
  }

  /**
   * <pre>
   * If true, make sure that for any feasible solution of the original problem,
   * if we round it to our "discretized" space it is still feasible and with a
   * better objective value. When this is true, the objective lower bound of the
   * CP-SAT problem, should be valid (modulo tolerance, presolve and computation
   * with double precision).
   *
   * TODO(user): By using exact floating-point utility we should be able to
   * make the claim stronger.
   * </pre>
   */
  public boolean getMipProperRelaxation() {
    return mipProperRelaxation_;
  }

  void copyFieldsFrom(SatParameters src) {
    name_ = src.name_;
    hasName_ = src.hasName_;
    preferredVariableOrder_ = src.preferredVariableOrder_;
    hasPreferredVariableOrder_ = src.hasPreferredVariableOrder_;
    initialPolarity_ = src.initialPolarity_;
    hasInitialPolarity_ = src.hasInitialPolarity_;
    usePhaseSaving_ = src.usePhaseSaving_;
    hasUsePhaseSaving_ = src.hasUsePhaseSaving_;
    polarityRephaseIncrement_ = src.polarityRephaseIncrement_;
    hasPolarityRephaseIncrement_ = src.hasPolarityRephaseIncrement_;
    polarityExploitLsHints_ = src.polarityExploitLsHints_;
    hasPolarityExploitLsHints_ = src.hasPolarityExploitLsHints_;
    randomPolarityRatio_ = src.randomPolarityRatio_;
    hasRandomPolarityRatio_ = src.hasRandomPolarityRatio_;
    randomBranchesRatio_ = src.randomBranchesRatio_;
    hasRandomBranchesRatio_ = src.hasRandomBranchesRatio_;
    useErwaHeuristic_ = src.useErwaHeuristic_;
    hasUseErwaHeuristic_ = src.hasUseErwaHeuristic_;
    initialVariablesActivity_ = src.initialVariablesActivity_;
    hasInitialVariablesActivity_ = src.hasInitialVariablesActivity_;
    alsoBumpVariablesInConflictReasons_ = src.alsoBumpVariablesInConflictReasons_;
    hasAlsoBumpVariablesInConflictReasons_ = src.hasAlsoBumpVariablesInConflictReasons_;
    minimizationAlgorithm_ = src.minimizationAlgorithm_;
    hasMinimizationAlgorithm_ = src.hasMinimizationAlgorithm_;
    binaryMinimizationAlgorithm_ = src.binaryMinimizationAlgorithm_;
    hasBinaryMinimizationAlgorithm_ = src.hasBinaryMinimizationAlgorithm_;
    subsumptionDuringConflictAnalysis_ = src.subsumptionDuringConflictAnalysis_;
    hasSubsumptionDuringConflictAnalysis_ = src.hasSubsumptionDuringConflictAnalysis_;
    extraSubsumptionDuringConflictAnalysis_ = src.extraSubsumptionDuringConflictAnalysis_;
    hasExtraSubsumptionDuringConflictAnalysis_ = src.hasExtraSubsumptionDuringConflictAnalysis_;
    decisionSubsumptionDuringConflictAnalysis_ = src.decisionSubsumptionDuringConflictAnalysis_;
    hasDecisionSubsumptionDuringConflictAnalysis_ = src.hasDecisionSubsumptionDuringConflictAnalysis_;
    eagerlySubsumeLastNConflicts_ = src.eagerlySubsumeLastNConflicts_;
    hasEagerlySubsumeLastNConflicts_ = src.hasEagerlySubsumeLastNConflicts_;
    subsumeDuringVivification_ = src.subsumeDuringVivification_;
    hasSubsumeDuringVivification_ = src.hasSubsumeDuringVivification_;
    useChronologicalBacktracking_ = src.useChronologicalBacktracking_;
    hasUseChronologicalBacktracking_ = src.hasUseChronologicalBacktracking_;
    maxBackjumpLevels_ = src.maxBackjumpLevels_;
    hasMaxBackjumpLevels_ = src.hasMaxBackjumpLevels_;
    chronologicalBacktrackMinConflicts_ = src.chronologicalBacktrackMinConflicts_;
    hasChronologicalBacktrackMinConflicts_ = src.hasChronologicalBacktrackMinConflicts_;
    learnOnAssumptionsUnsat_ = src.learnOnAssumptionsUnsat_;
    hasLearnOnAssumptionsUnsat_ = src.hasLearnOnAssumptionsUnsat_;
    clauseCleanupPeriod_ = src.clauseCleanupPeriod_;
    hasClauseCleanupPeriod_ = src.hasClauseCleanupPeriod_;
    clauseCleanupPeriodIncrement_ = src.clauseCleanupPeriodIncrement_;
    hasClauseCleanupPeriodIncrement_ = src.hasClauseCleanupPeriodIncrement_;
    clauseCleanupTarget_ = src.clauseCleanupTarget_;
    hasClauseCleanupTarget_ = src.hasClauseCleanupTarget_;
    clauseCleanupRatio_ = src.clauseCleanupRatio_;
    hasClauseCleanupRatio_ = src.hasClauseCleanupRatio_;
    clauseCleanupLbdBound_ = src.clauseCleanupLbdBound_;
    hasClauseCleanupLbdBound_ = src.hasClauseCleanupLbdBound_;
    clauseCleanupLbdTier1_ = src.clauseCleanupLbdTier1_;
    hasClauseCleanupLbdTier1_ = src.hasClauseCleanupLbdTier1_;
    clauseCleanupLbdTier2_ = src.clauseCleanupLbdTier2_;
    hasClauseCleanupLbdTier2_ = src.hasClauseCleanupLbdTier2_;
    clauseCleanupOrdering_ = src.clauseCleanupOrdering_;
    hasClauseCleanupOrdering_ = src.hasClauseCleanupOrdering_;
    pbCleanupIncrement_ = src.pbCleanupIncrement_;
    hasPbCleanupIncrement_ = src.hasPbCleanupIncrement_;
    pbCleanupRatio_ = src.pbCleanupRatio_;
    hasPbCleanupRatio_ = src.hasPbCleanupRatio_;
    variableActivityDecay_ = src.variableActivityDecay_;
    hasVariableActivityDecay_ = src.hasVariableActivityDecay_;
    maxVariableActivityValue_ = src.maxVariableActivityValue_;
    hasMaxVariableActivityValue_ = src.hasMaxVariableActivityValue_;
    glucoseMaxDecay_ = src.glucoseMaxDecay_;
    hasGlucoseMaxDecay_ = src.hasGlucoseMaxDecay_;
    glucoseDecayIncrement_ = src.glucoseDecayIncrement_;
    hasGlucoseDecayIncrement_ = src.hasGlucoseDecayIncrement_;
    glucoseDecayIncrementPeriod_ = src.glucoseDecayIncrementPeriod_;
    hasGlucoseDecayIncrementPeriod_ = src.hasGlucoseDecayIncrementPeriod_;
    clauseActivityDecay_ = src.clauseActivityDecay_;
    hasClauseActivityDecay_ = src.hasClauseActivityDecay_;
    maxClauseActivityValue_ = src.maxClauseActivityValue_;
    hasMaxClauseActivityValue_ = src.hasMaxClauseActivityValue_;
    restartAlgorithms_ = src.restartAlgorithms_ == null || src.restartAlgorithms_.isEmpty() ? null : new java.util.ArrayList<>(src.restartAlgorithms_);
    defaultRestartAlgorithms_ = src.defaultRestartAlgorithms_;
    hasDefaultRestartAlgorithms_ = src.hasDefaultRestartAlgorithms_;
    restartPeriod_ = src.restartPeriod_;
    hasRestartPeriod_ = src.hasRestartPeriod_;
    restartRunningWindowSize_ = src.restartRunningWindowSize_;
    hasRestartRunningWindowSize_ = src.hasRestartRunningWindowSize_;
    restartDlAverageRatio_ = src.restartDlAverageRatio_;
    hasRestartDlAverageRatio_ = src.hasRestartDlAverageRatio_;
    restartLbdAverageRatio_ = src.restartLbdAverageRatio_;
    hasRestartLbdAverageRatio_ = src.hasRestartLbdAverageRatio_;
    useBlockingRestart_ = src.useBlockingRestart_;
    hasUseBlockingRestart_ = src.hasUseBlockingRestart_;
    blockingRestartWindowSize_ = src.blockingRestartWindowSize_;
    hasBlockingRestartWindowSize_ = src.hasBlockingRestartWindowSize_;
    blockingRestartMultiplier_ = src.blockingRestartMultiplier_;
    hasBlockingRestartMultiplier_ = src.hasBlockingRestartMultiplier_;
    numConflictsBeforeStrategyChanges_ = src.numConflictsBeforeStrategyChanges_;
    hasNumConflictsBeforeStrategyChanges_ = src.hasNumConflictsBeforeStrategyChanges_;
    strategyChangeIncreaseRatio_ = src.strategyChangeIncreaseRatio_;
    hasStrategyChangeIncreaseRatio_ = src.hasStrategyChangeIncreaseRatio_;
    maxTimeInSeconds_ = src.maxTimeInSeconds_;
    hasMaxTimeInSeconds_ = src.hasMaxTimeInSeconds_;
    maxDeterministicTime_ = src.maxDeterministicTime_;
    hasMaxDeterministicTime_ = src.hasMaxDeterministicTime_;
    maxNumDeterministicBatches_ = src.maxNumDeterministicBatches_;
    hasMaxNumDeterministicBatches_ = src.hasMaxNumDeterministicBatches_;
    maxNumberOfConflicts_ = src.maxNumberOfConflicts_;
    hasMaxNumberOfConflicts_ = src.hasMaxNumberOfConflicts_;
    maxMemoryInMb_ = src.maxMemoryInMb_;
    hasMaxMemoryInMb_ = src.hasMaxMemoryInMb_;
    absoluteGapLimit_ = src.absoluteGapLimit_;
    hasAbsoluteGapLimit_ = src.hasAbsoluteGapLimit_;
    relativeGapLimit_ = src.relativeGapLimit_;
    hasRelativeGapLimit_ = src.hasRelativeGapLimit_;
    randomSeed_ = src.randomSeed_;
    hasRandomSeed_ = src.hasRandomSeed_;
    permuteVariableRandomly_ = src.permuteVariableRandomly_;
    hasPermuteVariableRandomly_ = src.hasPermuteVariableRandomly_;
    permutePresolveConstraintOrder_ = src.permutePresolveConstraintOrder_;
    hasPermutePresolveConstraintOrder_ = src.hasPermutePresolveConstraintOrder_;
    useAbslRandom_ = src.useAbslRandom_;
    hasUseAbslRandom_ = src.hasUseAbslRandom_;
    logSearchProgress_ = src.logSearchProgress_;
    hasLogSearchProgress_ = src.hasLogSearchProgress_;
    logSubsolverStatistics_ = src.logSubsolverStatistics_;
    hasLogSubsolverStatistics_ = src.hasLogSubsolverStatistics_;
    logPrefix_ = src.logPrefix_;
    hasLogPrefix_ = src.hasLogPrefix_;
    logToStdout_ = src.logToStdout_;
    hasLogToStdout_ = src.hasLogToStdout_;
    logToResponse_ = src.logToResponse_;
    hasLogToResponse_ = src.hasLogToResponse_;
    usePbResolution_ = src.usePbResolution_;
    hasUsePbResolution_ = src.hasUsePbResolution_;
    minimizeReductionDuringPbResolution_ = src.minimizeReductionDuringPbResolution_;
    hasMinimizeReductionDuringPbResolution_ = src.hasMinimizeReductionDuringPbResolution_;
    countAssumptionLevelsInLbd_ = src.countAssumptionLevelsInLbd_;
    hasCountAssumptionLevelsInLbd_ = src.hasCountAssumptionLevelsInLbd_;
    presolveBveThreshold_ = src.presolveBveThreshold_;
    hasPresolveBveThreshold_ = src.hasPresolveBveThreshold_;
    filterSatPostsolveClauses_ = src.filterSatPostsolveClauses_;
    hasFilterSatPostsolveClauses_ = src.hasFilterSatPostsolveClauses_;
    presolveBveClauseWeight_ = src.presolveBveClauseWeight_;
    hasPresolveBveClauseWeight_ = src.hasPresolveBveClauseWeight_;
    probingDeterministicTimeLimit_ = src.probingDeterministicTimeLimit_;
    hasProbingDeterministicTimeLimit_ = src.hasProbingDeterministicTimeLimit_;
    presolveProbingDeterministicTimeLimit_ = src.presolveProbingDeterministicTimeLimit_;
    hasPresolveProbingDeterministicTimeLimit_ = src.hasPresolveProbingDeterministicTimeLimit_;
    presolveBlockedClause_ = src.presolveBlockedClause_;
    hasPresolveBlockedClause_ = src.hasPresolveBlockedClause_;
    presolveUseBva_ = src.presolveUseBva_;
    hasPresolveUseBva_ = src.hasPresolveUseBva_;
    presolveBvaThreshold_ = src.presolveBvaThreshold_;
    hasPresolveBvaThreshold_ = src.hasPresolveBvaThreshold_;
    maxPresolveIterations_ = src.maxPresolveIterations_;
    hasMaxPresolveIterations_ = src.hasMaxPresolveIterations_;
    cpModelPresolve_ = src.cpModelPresolve_;
    hasCpModelPresolve_ = src.hasCpModelPresolve_;
    cpModelDirectSolve_ = src.cpModelDirectSolve_;
    hasCpModelDirectSolve_ = src.hasCpModelDirectSolve_;
    cpModelProbingLevel_ = src.cpModelProbingLevel_;
    hasCpModelProbingLevel_ = src.hasCpModelProbingLevel_;
    cpModelUseSatPresolve_ = src.cpModelUseSatPresolve_;
    hasCpModelUseSatPresolve_ = src.hasCpModelUseSatPresolve_;
    cpModelPureSatPresolve_ = src.cpModelPureSatPresolve_;
    hasCpModelPureSatPresolve_ = src.hasCpModelPureSatPresolve_;
    loadAtMostOnesInSatPresolve_ = src.loadAtMostOnesInSatPresolve_;
    hasLoadAtMostOnesInSatPresolve_ = src.hasLoadAtMostOnesInSatPresolve_;
    removeFixedVariablesEarly_ = src.removeFixedVariablesEarly_;
    hasRemoveFixedVariablesEarly_ = src.hasRemoveFixedVariablesEarly_;
    detectTableWithCost_ = src.detectTableWithCost_;
    hasDetectTableWithCost_ = src.hasDetectTableWithCost_;
    tableCompressionLevel_ = src.tableCompressionLevel_;
    hasTableCompressionLevel_ = src.hasTableCompressionLevel_;
    expandAlldiffConstraints_ = src.expandAlldiffConstraints_;
    hasExpandAlldiffConstraints_ = src.hasExpandAlldiffConstraints_;
    maxAlldiffDomainSize_ = src.maxAlldiffDomainSize_;
    hasMaxAlldiffDomainSize_ = src.hasMaxAlldiffDomainSize_;
    expandReservoirConstraints_ = src.expandReservoirConstraints_;
    hasExpandReservoirConstraints_ = src.hasExpandReservoirConstraints_;
    maxDomainSizeForLinear2Expansion_ = src.maxDomainSizeForLinear2Expansion_;
    hasMaxDomainSizeForLinear2Expansion_ = src.hasMaxDomainSizeForLinear2Expansion_;
    expandReservoirUsingCircuit_ = src.expandReservoirUsingCircuit_;
    hasExpandReservoirUsingCircuit_ = src.hasExpandReservoirUsingCircuit_;
    encodeCumulativeAsReservoir_ = src.encodeCumulativeAsReservoir_;
    hasEncodeCumulativeAsReservoir_ = src.hasEncodeCumulativeAsReservoir_;
    maxLinMaxSizeForExpansion_ = src.maxLinMaxSizeForExpansion_;
    hasMaxLinMaxSizeForExpansion_ = src.hasMaxLinMaxSizeForExpansion_;
    disableConstraintExpansion_ = src.disableConstraintExpansion_;
    hasDisableConstraintExpansion_ = src.hasDisableConstraintExpansion_;
    encodeComplexLinearConstraintWithInteger_ = src.encodeComplexLinearConstraintWithInteger_;
    hasEncodeComplexLinearConstraintWithInteger_ = src.hasEncodeComplexLinearConstraintWithInteger_;
    detectEncodedComplexDomain_ = src.detectEncodedComplexDomain_;
    hasDetectEncodedComplexDomain_ = src.hasDetectEncodedComplexDomain_;
    mergeNoOverlapWorkLimit_ = src.mergeNoOverlapWorkLimit_;
    hasMergeNoOverlapWorkLimit_ = src.hasMergeNoOverlapWorkLimit_;
    mergeAtMostOneWorkLimit_ = src.mergeAtMostOneWorkLimit_;
    hasMergeAtMostOneWorkLimit_ = src.hasMergeAtMostOneWorkLimit_;
    presolveSubstitutionLevel_ = src.presolveSubstitutionLevel_;
    hasPresolveSubstitutionLevel_ = src.hasPresolveSubstitutionLevel_;
    presolveExtractIntegerEnforcement_ = src.presolveExtractIntegerEnforcement_;
    hasPresolveExtractIntegerEnforcement_ = src.hasPresolveExtractIntegerEnforcement_;
    presolveInclusionWorkLimit_ = src.presolveInclusionWorkLimit_;
    hasPresolveInclusionWorkLimit_ = src.hasPresolveInclusionWorkLimit_;
    ignoreNames_ = src.ignoreNames_;
    hasIgnoreNames_ = src.hasIgnoreNames_;
    inferAllDiffs_ = src.inferAllDiffs_;
    hasInferAllDiffs_ = src.hasInferAllDiffs_;
    findBigLinearOverlap_ = src.findBigLinearOverlap_;
    hasFindBigLinearOverlap_ = src.hasFindBigLinearOverlap_;
    findClausesThatAreExactlyOne_ = src.findClausesThatAreExactlyOne_;
    hasFindClausesThatAreExactlyOne_ = src.hasFindClausesThatAreExactlyOne_;
    useSatInprocessing_ = src.useSatInprocessing_;
    hasUseSatInprocessing_ = src.hasUseSatInprocessing_;
    inprocessingDtimeRatio_ = src.inprocessingDtimeRatio_;
    hasInprocessingDtimeRatio_ = src.hasInprocessingDtimeRatio_;
    inprocessingProbingDtime_ = src.inprocessingProbingDtime_;
    hasInprocessingProbingDtime_ = src.hasInprocessingProbingDtime_;
    inprocessingMinimizationDtime_ = src.inprocessingMinimizationDtime_;
    hasInprocessingMinimizationDtime_ = src.hasInprocessingMinimizationDtime_;
    inprocessingMinimizationUseConflictAnalysis_ = src.inprocessingMinimizationUseConflictAnalysis_;
    hasInprocessingMinimizationUseConflictAnalysis_ = src.hasInprocessingMinimizationUseConflictAnalysis_;
    numOrderingInVivification_ = src.numOrderingInVivification_;
    hasNumOrderingInVivification_ = src.hasNumOrderingInVivification_;
    inprocessingUseCongruenceClosure_ = src.inprocessingUseCongruenceClosure_;
    hasInprocessingUseCongruenceClosure_ = src.hasInprocessingUseCongruenceClosure_;
    inprocessingDetectAndSweepCircuit_ = src.inprocessingDetectAndSweepCircuit_;
    hasInprocessingDetectAndSweepCircuit_ = src.hasInprocessingDetectAndSweepCircuit_;
    inprocessingUseSatSweeping_ = src.inprocessingUseSatSweeping_;
    hasInprocessingUseSatSweeping_ = src.hasInprocessingUseSatSweeping_;
    inprocessingUseTransitiveReduction_ = src.inprocessingUseTransitiveReduction_;
    hasInprocessingUseTransitiveReduction_ = src.hasInprocessingUseTransitiveReduction_;
    numWorkers_ = src.numWorkers_;
    hasNumWorkers_ = src.hasNumWorkers_;
    numSearchWorkers_ = src.numSearchWorkers_;
    hasNumSearchWorkers_ = src.hasNumSearchWorkers_;
    numFullSubsolvers_ = src.numFullSubsolvers_;
    hasNumFullSubsolvers_ = src.hasNumFullSubsolvers_;
    subsolvers_ = src.subsolvers_ == null || src.subsolvers_.isEmpty() ? null : new java.util.ArrayList<>(src.subsolvers_);
    extraSubsolvers_ = src.extraSubsolvers_ == null || src.extraSubsolvers_.isEmpty() ? null : new java.util.ArrayList<>(src.extraSubsolvers_);
    ignoreSubsolvers_ = src.ignoreSubsolvers_ == null || src.ignoreSubsolvers_.isEmpty() ? null : new java.util.ArrayList<>(src.ignoreSubsolvers_);
    filterSubsolvers_ = src.filterSubsolvers_ == null || src.filterSubsolvers_.isEmpty() ? null : new java.util.ArrayList<>(src.filterSubsolvers_);
    subsolverParams_ = buildListSatParameters(src.subsolverParams_);
    interleaveSearch_ = src.interleaveSearch_;
    hasInterleaveSearch_ = src.hasInterleaveSearch_;
    interleaveBatchSize_ = src.interleaveBatchSize_;
    hasInterleaveBatchSize_ = src.hasInterleaveBatchSize_;
    shareObjectiveBounds_ = src.shareObjectiveBounds_;
    hasShareObjectiveBounds_ = src.hasShareObjectiveBounds_;
    shareLevelZeroBounds_ = src.shareLevelZeroBounds_;
    hasShareLevelZeroBounds_ = src.hasShareLevelZeroBounds_;
    shareLinear2Bounds_ = src.shareLinear2Bounds_;
    hasShareLinear2Bounds_ = src.hasShareLinear2Bounds_;
    shareBinaryClauses_ = src.shareBinaryClauses_;
    hasShareBinaryClauses_ = src.hasShareBinaryClauses_;
    shareGlueClauses_ = src.shareGlueClauses_;
    hasShareGlueClauses_ = src.hasShareGlueClauses_;
    minimizeSharedClauses_ = src.minimizeSharedClauses_;
    hasMinimizeSharedClauses_ = src.hasMinimizeSharedClauses_;
    shareGlueClausesDtime_ = src.shareGlueClausesDtime_;
    hasShareGlueClausesDtime_ = src.hasShareGlueClausesDtime_;
    checkLratProof_ = src.checkLratProof_;
    hasCheckLratProof_ = src.hasCheckLratProof_;
    checkMergedLratProof_ = src.checkMergedLratProof_;
    hasCheckMergedLratProof_ = src.hasCheckMergedLratProof_;
    outputLratProof_ = src.outputLratProof_;
    hasOutputLratProof_ = src.hasOutputLratProof_;
    debugPostsolveWithFullSolver_ = src.debugPostsolveWithFullSolver_;
    hasDebugPostsolveWithFullSolver_ = src.hasDebugPostsolveWithFullSolver_;
    debugMaxNumPresolveOperations_ = src.debugMaxNumPresolveOperations_;
    hasDebugMaxNumPresolveOperations_ = src.hasDebugMaxNumPresolveOperations_;
    debugCrashOnBadHint_ = src.debugCrashOnBadHint_;
    hasDebugCrashOnBadHint_ = src.hasDebugCrashOnBadHint_;
    debugCrashIfPresolveBreaksHint_ = src.debugCrashIfPresolveBreaksHint_;
    hasDebugCrashIfPresolveBreaksHint_ = src.hasDebugCrashIfPresolveBreaksHint_;
    debugCrashIfLratCheckFails_ = src.debugCrashIfLratCheckFails_;
    hasDebugCrashIfLratCheckFails_ = src.hasDebugCrashIfLratCheckFails_;
    useOptimizationHints_ = src.useOptimizationHints_;
    hasUseOptimizationHints_ = src.hasUseOptimizationHints_;
    coreMinimizationLevel_ = src.coreMinimizationLevel_;
    hasCoreMinimizationLevel_ = src.hasCoreMinimizationLevel_;
    findMultipleCores_ = src.findMultipleCores_;
    hasFindMultipleCores_ = src.hasFindMultipleCores_;
    coverOptimization_ = src.coverOptimization_;
    hasCoverOptimization_ = src.hasCoverOptimization_;
    maxSatAssumptionOrder_ = src.maxSatAssumptionOrder_;
    hasMaxSatAssumptionOrder_ = src.hasMaxSatAssumptionOrder_;
    maxSatReverseAssumptionOrder_ = src.maxSatReverseAssumptionOrder_;
    hasMaxSatReverseAssumptionOrder_ = src.hasMaxSatReverseAssumptionOrder_;
    maxSatStratification_ = src.maxSatStratification_;
    hasMaxSatStratification_ = src.hasMaxSatStratification_;
    propagationLoopDetectionFactor_ = src.propagationLoopDetectionFactor_;
    hasPropagationLoopDetectionFactor_ = src.hasPropagationLoopDetectionFactor_;
    usePrecedencesInDisjunctiveConstraint_ = src.usePrecedencesInDisjunctiveConstraint_;
    hasUsePrecedencesInDisjunctiveConstraint_ = src.hasUsePrecedencesInDisjunctiveConstraint_;
    transitivePrecedencesWorkLimit_ = src.transitivePrecedencesWorkLimit_;
    hasTransitivePrecedencesWorkLimit_ = src.hasTransitivePrecedencesWorkLimit_;
    maxSizeToCreatePrecedenceLiteralsInDisjunctive_ = src.maxSizeToCreatePrecedenceLiteralsInDisjunctive_;
    hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_ = src.hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_;
    useStrongPropagationInDisjunctive_ = src.useStrongPropagationInDisjunctive_;
    hasUseStrongPropagationInDisjunctive_ = src.hasUseStrongPropagationInDisjunctive_;
    useDynamicPrecedenceInDisjunctive_ = src.useDynamicPrecedenceInDisjunctive_;
    hasUseDynamicPrecedenceInDisjunctive_ = src.hasUseDynamicPrecedenceInDisjunctive_;
    useDynamicPrecedenceInCumulative_ = src.useDynamicPrecedenceInCumulative_;
    hasUseDynamicPrecedenceInCumulative_ = src.hasUseDynamicPrecedenceInCumulative_;
    useOverloadCheckerInCumulative_ = src.useOverloadCheckerInCumulative_;
    hasUseOverloadCheckerInCumulative_ = src.hasUseOverloadCheckerInCumulative_;
    useConservativeScaleOverloadChecker_ = src.useConservativeScaleOverloadChecker_;
    hasUseConservativeScaleOverloadChecker_ = src.hasUseConservativeScaleOverloadChecker_;
    useTimetableEdgeFindingInCumulative_ = src.useTimetableEdgeFindingInCumulative_;
    hasUseTimetableEdgeFindingInCumulative_ = src.hasUseTimetableEdgeFindingInCumulative_;
    maxNumIntervalsForTimetableEdgeFinding_ = src.maxNumIntervalsForTimetableEdgeFinding_;
    hasMaxNumIntervalsForTimetableEdgeFinding_ = src.hasMaxNumIntervalsForTimetableEdgeFinding_;
    useHardPrecedencesInCumulative_ = src.useHardPrecedencesInCumulative_;
    hasUseHardPrecedencesInCumulative_ = src.hasUseHardPrecedencesInCumulative_;
    exploitAllPrecedences_ = src.exploitAllPrecedences_;
    hasExploitAllPrecedences_ = src.hasExploitAllPrecedences_;
    useDisjunctiveConstraintInCumulative_ = src.useDisjunctiveConstraintInCumulative_;
    hasUseDisjunctiveConstraintInCumulative_ = src.hasUseDisjunctiveConstraintInCumulative_;
    noOverlap2DBooleanRelationsLimit_ = src.noOverlap2DBooleanRelationsLimit_;
    hasNoOverlap2DBooleanRelationsLimit_ = src.hasNoOverlap2DBooleanRelationsLimit_;
    useTimetablingInNoOverlap2D_ = src.useTimetablingInNoOverlap2D_;
    hasUseTimetablingInNoOverlap2D_ = src.hasUseTimetablingInNoOverlap2D_;
    useEnergeticReasoningInNoOverlap2D_ = src.useEnergeticReasoningInNoOverlap2D_;
    hasUseEnergeticReasoningInNoOverlap2D_ = src.hasUseEnergeticReasoningInNoOverlap2D_;
    useAreaEnergeticReasoningInNoOverlap2D_ = src.useAreaEnergeticReasoningInNoOverlap2D_;
    hasUseAreaEnergeticReasoningInNoOverlap2D_ = src.hasUseAreaEnergeticReasoningInNoOverlap2D_;
    useTryEdgeReasoningInNoOverlap2D_ = src.useTryEdgeReasoningInNoOverlap2D_;
    hasUseTryEdgeReasoningInNoOverlap2D_ = src.hasUseTryEdgeReasoningInNoOverlap2D_;
    maxPairsPairwiseReasoningInNoOverlap2D_ = src.maxPairsPairwiseReasoningInNoOverlap2D_;
    hasMaxPairsPairwiseReasoningInNoOverlap2D_ = src.hasMaxPairsPairwiseReasoningInNoOverlap2D_;
    maximumRegionsToSplitInDisconnectedNoOverlap2D_ = src.maximumRegionsToSplitInDisconnectedNoOverlap2D_;
    hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_ = src.hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_;
    useLinear3ForNoOverlap2DPrecedences_ = src.useLinear3ForNoOverlap2DPrecedences_;
    hasUseLinear3ForNoOverlap2DPrecedences_ = src.hasUseLinear3ForNoOverlap2DPrecedences_;
    useDualSchedulingHeuristics_ = src.useDualSchedulingHeuristics_;
    hasUseDualSchedulingHeuristics_ = src.hasUseDualSchedulingHeuristics_;
    useAllDifferentForCircuit_ = src.useAllDifferentForCircuit_;
    hasUseAllDifferentForCircuit_ = src.hasUseAllDifferentForCircuit_;
    routingCutSubsetSizeForBinaryRelationBound_ = src.routingCutSubsetSizeForBinaryRelationBound_;
    hasRoutingCutSubsetSizeForBinaryRelationBound_ = src.hasRoutingCutSubsetSizeForBinaryRelationBound_;
    routingCutSubsetSizeForTightBinaryRelationBound_ = src.routingCutSubsetSizeForTightBinaryRelationBound_;
    hasRoutingCutSubsetSizeForTightBinaryRelationBound_ = src.hasRoutingCutSubsetSizeForTightBinaryRelationBound_;
    routingCutSubsetSizeForExactBinaryRelationBound_ = src.routingCutSubsetSizeForExactBinaryRelationBound_;
    hasRoutingCutSubsetSizeForExactBinaryRelationBound_ = src.hasRoutingCutSubsetSizeForExactBinaryRelationBound_;
    routingCutSubsetSizeForShortestPathsBound_ = src.routingCutSubsetSizeForShortestPathsBound_;
    hasRoutingCutSubsetSizeForShortestPathsBound_ = src.hasRoutingCutSubsetSizeForShortestPathsBound_;
    routingCutDpEffort_ = src.routingCutDpEffort_;
    hasRoutingCutDpEffort_ = src.hasRoutingCutDpEffort_;
    routingCutMaxInfeasiblePathLength_ = src.routingCutMaxInfeasiblePathLength_;
    hasRoutingCutMaxInfeasiblePathLength_ = src.hasRoutingCutMaxInfeasiblePathLength_;
    searchBranching_ = src.searchBranching_;
    hasSearchBranching_ = src.hasSearchBranching_;
    hintConflictLimit_ = src.hintConflictLimit_;
    hasHintConflictLimit_ = src.hasHintConflictLimit_;
    tryHintAsAssumptions_ = src.tryHintAsAssumptions_;
    hasTryHintAsAssumptions_ = src.hasTryHintAsAssumptions_;
    repairHint_ = src.repairHint_;
    hasRepairHint_ = src.hasRepairHint_;
    fixVariablesToTheirHintedValue_ = src.fixVariablesToTheirHintedValue_;
    hasFixVariablesToTheirHintedValue_ = src.hasFixVariablesToTheirHintedValue_;
    useProbingSearch_ = src.useProbingSearch_;
    hasUseProbingSearch_ = src.hasUseProbingSearch_;
    useExtendedProbing_ = src.useExtendedProbing_;
    hasUseExtendedProbing_ = src.hasUseExtendedProbing_;
    probingNumCombinationsLimit_ = src.probingNumCombinationsLimit_;
    hasProbingNumCombinationsLimit_ = src.hasProbingNumCombinationsLimit_;
    shavingDeterministicTimeInProbingSearch_ = src.shavingDeterministicTimeInProbingSearch_;
    hasShavingDeterministicTimeInProbingSearch_ = src.hasShavingDeterministicTimeInProbingSearch_;
    shavingSearchDeterministicTime_ = src.shavingSearchDeterministicTime_;
    hasShavingSearchDeterministicTime_ = src.hasShavingSearchDeterministicTime_;
    shavingSearchUseRandomSearch_ = src.shavingSearchUseRandomSearch_;
    hasShavingSearchUseRandomSearch_ = src.hasShavingSearchUseRandomSearch_;
    shavingSearchRestartLimit_ = src.shavingSearchRestartLimit_;
    hasShavingSearchRestartLimit_ = src.hasShavingSearchRestartLimit_;
    shavingSearchThreshold_ = src.shavingSearchThreshold_;
    hasShavingSearchThreshold_ = src.hasShavingSearchThreshold_;
    objectiveShavingSearchThreshold_ = src.objectiveShavingSearchThreshold_;
    hasObjectiveShavingSearchThreshold_ = src.hasObjectiveShavingSearchThreshold_;
    useObjectiveLbSearch_ = src.useObjectiveLbSearch_;
    hasUseObjectiveLbSearch_ = src.hasUseObjectiveLbSearch_;
    useObjectiveShavingSearch_ = src.useObjectiveShavingSearch_;
    hasUseObjectiveShavingSearch_ = src.hasUseObjectiveShavingSearch_;
    variablesShavingLevel_ = src.variablesShavingLevel_;
    hasVariablesShavingLevel_ = src.hasVariablesShavingLevel_;
    pseudoCostReliabilityThreshold_ = src.pseudoCostReliabilityThreshold_;
    hasPseudoCostReliabilityThreshold_ = src.hasPseudoCostReliabilityThreshold_;
    optimizeWithCore_ = src.optimizeWithCore_;
    hasOptimizeWithCore_ = src.hasOptimizeWithCore_;
    optimizeWithLbTreeSearch_ = src.optimizeWithLbTreeSearch_;
    hasOptimizeWithLbTreeSearch_ = src.hasOptimizeWithLbTreeSearch_;
    saveLpBasisInLbTreeSearch_ = src.saveLpBasisInLbTreeSearch_;
    hasSaveLpBasisInLbTreeSearch_ = src.hasSaveLpBasisInLbTreeSearch_;
    binarySearchNumConflicts_ = src.binarySearchNumConflicts_;
    hasBinarySearchNumConflicts_ = src.hasBinarySearchNumConflicts_;
    optimizeWithMaxHs_ = src.optimizeWithMaxHs_;
    hasOptimizeWithMaxHs_ = src.hasOptimizeWithMaxHs_;
    useFeasibilityJump_ = src.useFeasibilityJump_;
    hasUseFeasibilityJump_ = src.hasUseFeasibilityJump_;
    useLsOnly_ = src.useLsOnly_;
    hasUseLsOnly_ = src.hasUseLsOnly_;
    feasibilityJumpDecay_ = src.feasibilityJumpDecay_;
    hasFeasibilityJumpDecay_ = src.hasFeasibilityJumpDecay_;
    feasibilityJumpLinearizationLevel_ = src.feasibilityJumpLinearizationLevel_;
    hasFeasibilityJumpLinearizationLevel_ = src.hasFeasibilityJumpLinearizationLevel_;
    feasibilityJumpRestartFactor_ = src.feasibilityJumpRestartFactor_;
    hasFeasibilityJumpRestartFactor_ = src.hasFeasibilityJumpRestartFactor_;
    feasibilityJumpBatchDtime_ = src.feasibilityJumpBatchDtime_;
    hasFeasibilityJumpBatchDtime_ = src.hasFeasibilityJumpBatchDtime_;
    feasibilityJumpVarRandomizationProbability_ = src.feasibilityJumpVarRandomizationProbability_;
    hasFeasibilityJumpVarRandomizationProbability_ = src.hasFeasibilityJumpVarRandomizationProbability_;
    feasibilityJumpVarPerburbationRangeRatio_ = src.feasibilityJumpVarPerburbationRangeRatio_;
    hasFeasibilityJumpVarPerburbationRangeRatio_ = src.hasFeasibilityJumpVarPerburbationRangeRatio_;
    feasibilityJumpEnableRestarts_ = src.feasibilityJumpEnableRestarts_;
    hasFeasibilityJumpEnableRestarts_ = src.hasFeasibilityJumpEnableRestarts_;
    feasibilityJumpMaxExpandedConstraintSize_ = src.feasibilityJumpMaxExpandedConstraintSize_;
    hasFeasibilityJumpMaxExpandedConstraintSize_ = src.hasFeasibilityJumpMaxExpandedConstraintSize_;
    feasibilityJumpIgnoreRedundantConstraints_ = src.feasibilityJumpIgnoreRedundantConstraints_;
    hasFeasibilityJumpIgnoreRedundantConstraints_ = src.hasFeasibilityJumpIgnoreRedundantConstraints_;
    lnsIgnoreRedundantConstraints_ = src.lnsIgnoreRedundantConstraints_;
    hasLnsIgnoreRedundantConstraints_ = src.hasLnsIgnoreRedundantConstraints_;
    numViolationLs_ = src.numViolationLs_;
    hasNumViolationLs_ = src.hasNumViolationLs_;
    violationLsPerturbationPeriod_ = src.violationLsPerturbationPeriod_;
    hasViolationLsPerturbationPeriod_ = src.hasViolationLsPerturbationPeriod_;
    sharedTreeNumWorkers_ = src.sharedTreeNumWorkers_;
    hasSharedTreeNumWorkers_ = src.hasSharedTreeNumWorkers_;
    useSharedTreeSearch_ = src.useSharedTreeSearch_;
    hasUseSharedTreeSearch_ = src.hasUseSharedTreeSearch_;
    sharedTreeWorkerMinRestartsPerSubtree_ = src.sharedTreeWorkerMinRestartsPerSubtree_;
    hasSharedTreeWorkerMinRestartsPerSubtree_ = src.hasSharedTreeWorkerMinRestartsPerSubtree_;
    sharedTreeWorkerEnableTrailSharing_ = src.sharedTreeWorkerEnableTrailSharing_;
    hasSharedTreeWorkerEnableTrailSharing_ = src.hasSharedTreeWorkerEnableTrailSharing_;
    sharedTreeWorkerEnablePhaseSharing_ = src.sharedTreeWorkerEnablePhaseSharing_;
    hasSharedTreeWorkerEnablePhaseSharing_ = src.hasSharedTreeWorkerEnablePhaseSharing_;
    sharedTreeWorkerPropagateIntermediateNodes_ = src.sharedTreeWorkerPropagateIntermediateNodes_;
    hasSharedTreeWorkerPropagateIntermediateNodes_ = src.hasSharedTreeWorkerPropagateIntermediateNodes_;
    sharedTreeOpenLeavesPerWorker_ = src.sharedTreeOpenLeavesPerWorker_;
    hasSharedTreeOpenLeavesPerWorker_ = src.hasSharedTreeOpenLeavesPerWorker_;
    sharedTreeMaxNodesPerWorker_ = src.sharedTreeMaxNodesPerWorker_;
    hasSharedTreeMaxNodesPerWorker_ = src.hasSharedTreeMaxNodesPerWorker_;
    sharedTreeSplitStrategy_ = src.sharedTreeSplitStrategy_;
    hasSharedTreeSplitStrategy_ = src.hasSharedTreeSplitStrategy_;
    sharedTreeBalanceTolerance_ = src.sharedTreeBalanceTolerance_;
    hasSharedTreeBalanceTolerance_ = src.hasSharedTreeBalanceTolerance_;
    sharedTreeSplitMinDtime_ = src.sharedTreeSplitMinDtime_;
    hasSharedTreeSplitMinDtime_ = src.hasSharedTreeSplitMinDtime_;
    enumerateAllSolutions_ = src.enumerateAllSolutions_;
    hasEnumerateAllSolutions_ = src.hasEnumerateAllSolutions_;
    keepAllFeasibleSolutionsInPresolve_ = src.keepAllFeasibleSolutionsInPresolve_;
    hasKeepAllFeasibleSolutionsInPresolve_ = src.hasKeepAllFeasibleSolutionsInPresolve_;
    fillTightenedDomainsInResponse_ = src.fillTightenedDomainsInResponse_;
    hasFillTightenedDomainsInResponse_ = src.hasFillTightenedDomainsInResponse_;
    fillAdditionalSolutionsInResponse_ = src.fillAdditionalSolutionsInResponse_;
    hasFillAdditionalSolutionsInResponse_ = src.hasFillAdditionalSolutionsInResponse_;
    instantiateAllVariables_ = src.instantiateAllVariables_;
    hasInstantiateAllVariables_ = src.hasInstantiateAllVariables_;
    autoDetectGreaterThanAtLeastOneOf_ = src.autoDetectGreaterThanAtLeastOneOf_;
    hasAutoDetectGreaterThanAtLeastOneOf_ = src.hasAutoDetectGreaterThanAtLeastOneOf_;
    stopAfterFirstSolution_ = src.stopAfterFirstSolution_;
    hasStopAfterFirstSolution_ = src.hasStopAfterFirstSolution_;
    stopAfterPresolve_ = src.stopAfterPresolve_;
    hasStopAfterPresolve_ = src.hasStopAfterPresolve_;
    stopAfterRootPropagation_ = src.stopAfterRootPropagation_;
    hasStopAfterRootPropagation_ = src.hasStopAfterRootPropagation_;
    lnsInitialDifficulty_ = src.lnsInitialDifficulty_;
    hasLnsInitialDifficulty_ = src.hasLnsInitialDifficulty_;
    lnsInitialDeterministicLimit_ = src.lnsInitialDeterministicLimit_;
    hasLnsInitialDeterministicLimit_ = src.hasLnsInitialDeterministicLimit_;
    useLns_ = src.useLns_;
    hasUseLns_ = src.hasUseLns_;
    useLnsOnly_ = src.useLnsOnly_;
    hasUseLnsOnly_ = src.hasUseLnsOnly_;
    solutionPoolSize_ = src.solutionPoolSize_;
    hasSolutionPoolSize_ = src.hasSolutionPoolSize_;
    solutionPoolDiversityLimit_ = src.solutionPoolDiversityLimit_;
    hasSolutionPoolDiversityLimit_ = src.hasSolutionPoolDiversityLimit_;
    alternativePoolSize_ = src.alternativePoolSize_;
    hasAlternativePoolSize_ = src.hasAlternativePoolSize_;
    useRinsLns_ = src.useRinsLns_;
    hasUseRinsLns_ = src.hasUseRinsLns_;
    useFeasibilityPump_ = src.useFeasibilityPump_;
    hasUseFeasibilityPump_ = src.hasUseFeasibilityPump_;
    useLbRelaxLns_ = src.useLbRelaxLns_;
    hasUseLbRelaxLns_ = src.hasUseLbRelaxLns_;
    lbRelaxNumWorkersThreshold_ = src.lbRelaxNumWorkersThreshold_;
    hasLbRelaxNumWorkersThreshold_ = src.hasLbRelaxNumWorkersThreshold_;
    fpRounding_ = src.fpRounding_;
    hasFpRounding_ = src.hasFpRounding_;
    diversifyLnsParams_ = src.diversifyLnsParams_;
    hasDiversifyLnsParams_ = src.hasDiversifyLnsParams_;
    randomizeSearch_ = src.randomizeSearch_;
    hasRandomizeSearch_ = src.hasRandomizeSearch_;
    searchRandomVariablePoolSize_ = src.searchRandomVariablePoolSize_;
    hasSearchRandomVariablePoolSize_ = src.hasSearchRandomVariablePoolSize_;
    pushAllTasksTowardStart_ = src.pushAllTasksTowardStart_;
    hasPushAllTasksTowardStart_ = src.hasPushAllTasksTowardStart_;
    useOptionalVariables_ = src.useOptionalVariables_;
    hasUseOptionalVariables_ = src.hasUseOptionalVariables_;
    useExactLpReason_ = src.useExactLpReason_;
    hasUseExactLpReason_ = src.hasUseExactLpReason_;
    useCombinedNoOverlap_ = src.useCombinedNoOverlap_;
    hasUseCombinedNoOverlap_ = src.hasUseCombinedNoOverlap_;
    atMostOneMaxExpansionSize_ = src.atMostOneMaxExpansionSize_;
    hasAtMostOneMaxExpansionSize_ = src.hasAtMostOneMaxExpansionSize_;
    catchSigintSignal_ = src.catchSigintSignal_;
    hasCatchSigintSignal_ = src.hasCatchSigintSignal_;
    useImpliedBounds_ = src.useImpliedBounds_;
    hasUseImpliedBounds_ = src.hasUseImpliedBounds_;
    polishLpSolution_ = src.polishLpSolution_;
    hasPolishLpSolution_ = src.hasPolishLpSolution_;
    lpPrimalTolerance_ = src.lpPrimalTolerance_;
    hasLpPrimalTolerance_ = src.hasLpPrimalTolerance_;
    lpDualTolerance_ = src.lpDualTolerance_;
    hasLpDualTolerance_ = src.hasLpDualTolerance_;
    convertIntervals_ = src.convertIntervals_;
    hasConvertIntervals_ = src.hasConvertIntervals_;
    symmetryLevel_ = src.symmetryLevel_;
    hasSymmetryLevel_ = src.hasSymmetryLevel_;
    useSymmetryInLp_ = src.useSymmetryInLp_;
    hasUseSymmetryInLp_ = src.hasUseSymmetryInLp_;
    keepSymmetryInPresolve_ = src.keepSymmetryInPresolve_;
    hasKeepSymmetryInPresolve_ = src.hasKeepSymmetryInPresolve_;
    symmetryDetectionDeterministicTimeLimit_ = src.symmetryDetectionDeterministicTimeLimit_;
    hasSymmetryDetectionDeterministicTimeLimit_ = src.hasSymmetryDetectionDeterministicTimeLimit_;
    newLinearPropagation_ = src.newLinearPropagation_;
    hasNewLinearPropagation_ = src.hasNewLinearPropagation_;
    linearSplitSize_ = src.linearSplitSize_;
    hasLinearSplitSize_ = src.hasLinearSplitSize_;
    linearizationLevel_ = src.linearizationLevel_;
    hasLinearizationLevel_ = src.hasLinearizationLevel_;
    booleanEncodingLevel_ = src.booleanEncodingLevel_;
    hasBooleanEncodingLevel_ = src.hasBooleanEncodingLevel_;
    maxDomainSizeWhenEncodingEqNeqConstraints_ = src.maxDomainSizeWhenEncodingEqNeqConstraints_;
    hasMaxDomainSizeWhenEncodingEqNeqConstraints_ = src.hasMaxDomainSizeWhenEncodingEqNeqConstraints_;
    maxNumCuts_ = src.maxNumCuts_;
    hasMaxNumCuts_ = src.hasMaxNumCuts_;
    cutLevel_ = src.cutLevel_;
    hasCutLevel_ = src.hasCutLevel_;
    onlyAddCutsAtLevelZero_ = src.onlyAddCutsAtLevelZero_;
    hasOnlyAddCutsAtLevelZero_ = src.hasOnlyAddCutsAtLevelZero_;
    addObjectiveCut_ = src.addObjectiveCut_;
    hasAddObjectiveCut_ = src.hasAddObjectiveCut_;
    addCgCuts_ = src.addCgCuts_;
    hasAddCgCuts_ = src.hasAddCgCuts_;
    addMirCuts_ = src.addMirCuts_;
    hasAddMirCuts_ = src.hasAddMirCuts_;
    addZeroHalfCuts_ = src.addZeroHalfCuts_;
    hasAddZeroHalfCuts_ = src.hasAddZeroHalfCuts_;
    addCliqueCuts_ = src.addCliqueCuts_;
    hasAddCliqueCuts_ = src.hasAddCliqueCuts_;
    addRltCuts_ = src.addRltCuts_;
    hasAddRltCuts_ = src.hasAddRltCuts_;
    maxAllDiffCutSize_ = src.maxAllDiffCutSize_;
    hasMaxAllDiffCutSize_ = src.hasMaxAllDiffCutSize_;
    addLinMaxCuts_ = src.addLinMaxCuts_;
    hasAddLinMaxCuts_ = src.hasAddLinMaxCuts_;
    maxIntegerRoundingScaling_ = src.maxIntegerRoundingScaling_;
    hasMaxIntegerRoundingScaling_ = src.hasMaxIntegerRoundingScaling_;
    addLpConstraintsLazily_ = src.addLpConstraintsLazily_;
    hasAddLpConstraintsLazily_ = src.hasAddLpConstraintsLazily_;
    rootLpIterations_ = src.rootLpIterations_;
    hasRootLpIterations_ = src.hasRootLpIterations_;
    minOrthogonalityForLpConstraints_ = src.minOrthogonalityForLpConstraints_;
    hasMinOrthogonalityForLpConstraints_ = src.hasMinOrthogonalityForLpConstraints_;
    maxCutRoundsAtLevelZero_ = src.maxCutRoundsAtLevelZero_;
    hasMaxCutRoundsAtLevelZero_ = src.hasMaxCutRoundsAtLevelZero_;
    maxConsecutiveInactiveCount_ = src.maxConsecutiveInactiveCount_;
    hasMaxConsecutiveInactiveCount_ = src.hasMaxConsecutiveInactiveCount_;
    cutMaxActiveCountValue_ = src.cutMaxActiveCountValue_;
    hasCutMaxActiveCountValue_ = src.hasCutMaxActiveCountValue_;
    cutActiveCountDecay_ = src.cutActiveCountDecay_;
    hasCutActiveCountDecay_ = src.hasCutActiveCountDecay_;
    cutCleanupTarget_ = src.cutCleanupTarget_;
    hasCutCleanupTarget_ = src.hasCutCleanupTarget_;
    newConstraintsBatchSize_ = src.newConstraintsBatchSize_;
    hasNewConstraintsBatchSize_ = src.hasNewConstraintsBatchSize_;
    exploitIntegerLpSolution_ = src.exploitIntegerLpSolution_;
    hasExploitIntegerLpSolution_ = src.hasExploitIntegerLpSolution_;
    exploitAllLpSolution_ = src.exploitAllLpSolution_;
    hasExploitAllLpSolution_ = src.hasExploitAllLpSolution_;
    exploitBestSolution_ = src.exploitBestSolution_;
    hasExploitBestSolution_ = src.hasExploitBestSolution_;
    exploitRelaxationSolution_ = src.exploitRelaxationSolution_;
    hasExploitRelaxationSolution_ = src.hasExploitRelaxationSolution_;
    exploitObjective_ = src.exploitObjective_;
    hasExploitObjective_ = src.hasExploitObjective_;
    detectLinearizedProduct_ = src.detectLinearizedProduct_;
    hasDetectLinearizedProduct_ = src.hasDetectLinearizedProduct_;
    useNewIntegerConflictResolution_ = src.useNewIntegerConflictResolution_;
    hasUseNewIntegerConflictResolution_ = src.hasUseNewIntegerConflictResolution_;
    create1UipBooleanDuringIcr_ = src.create1UipBooleanDuringIcr_;
    hasCreate1UipBooleanDuringIcr_ = src.hasCreate1UipBooleanDuringIcr_;
    mipMaxBound_ = src.mipMaxBound_;
    hasMipMaxBound_ = src.hasMipMaxBound_;
    mipVarScaling_ = src.mipVarScaling_;
    hasMipVarScaling_ = src.hasMipVarScaling_;
    mipScaleLargeDomain_ = src.mipScaleLargeDomain_;
    hasMipScaleLargeDomain_ = src.hasMipScaleLargeDomain_;
    mipAutomaticallyScaleVariables_ = src.mipAutomaticallyScaleVariables_;
    hasMipAutomaticallyScaleVariables_ = src.hasMipAutomaticallyScaleVariables_;
    mipScaleToMaxBound_ = src.mipScaleToMaxBound_;
    hasMipScaleToMaxBound_ = src.hasMipScaleToMaxBound_;
    mipPresolveRestrictBounds_ = src.mipPresolveRestrictBounds_;
    hasMipPresolveRestrictBounds_ = src.hasMipPresolveRestrictBounds_;
    onlySolveIp_ = src.onlySolveIp_;
    hasOnlySolveIp_ = src.hasOnlySolveIp_;
    mipWantedPrecision_ = src.mipWantedPrecision_;
    hasMipWantedPrecision_ = src.hasMipWantedPrecision_;
    mipMaxActivityExponent_ = src.mipMaxActivityExponent_;
    hasMipMaxActivityExponent_ = src.hasMipMaxActivityExponent_;
    mipCheckPrecision_ = src.mipCheckPrecision_;
    hasMipCheckPrecision_ = src.hasMipCheckPrecision_;
    mipComputeTrueObjectiveBound_ = src.mipComputeTrueObjectiveBound_;
    hasMipComputeTrueObjectiveBound_ = src.hasMipComputeTrueObjectiveBound_;
    mipMaxValidMagnitude_ = src.mipMaxValidMagnitude_;
    hasMipMaxValidMagnitude_ = src.hasMipMaxValidMagnitude_;
    mipTreatHighMagnitudeBoundsAsInfinity_ = src.mipTreatHighMagnitudeBoundsAsInfinity_;
    hasMipTreatHighMagnitudeBoundsAsInfinity_ = src.hasMipTreatHighMagnitudeBoundsAsInfinity_;
    mipDropTolerance_ = src.mipDropTolerance_;
    hasMipDropTolerance_ = src.hasMipDropTolerance_;
    mipPresolveLevel_ = src.mipPresolveLevel_;
    hasMipPresolveLevel_ = src.hasMipPresolveLevel_;
    mipUseTightBounds_ = src.mipUseTightBounds_;
    hasMipUseTightBounds_ = src.hasMipUseTightBounds_;
    mipTightPrecision_ = src.mipTightPrecision_;
    hasMipTightPrecision_ = src.hasMipTightPrecision_;
    mipVerifySolution_ = src.mipVerifySolution_;
    hasMipVerifySolution_ = src.hasMipVerifySolution_;
    mipProperRelaxation_ = src.mipProperRelaxation_;
    hasMipProperRelaxation_ = src.hasMipProperRelaxation_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  SatParameters immutableCopy() {
    if (this instanceof Builder) {
      SatParameters r = new SatParameters();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<SatParameters> buildListSatParameters(java.util.List<SatParameters> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<SatParameters> r = new java.util.ArrayList<>(src.size());
    for (SatParameters e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (hasPreferredVariableOrder_) {
      SatParameters.VariableOrder e = SatParameters.VariableOrder.forNumber(preferredVariableOrder_);
      printer.printEnum("preferred_variable_order", e == null ? Integer.toString(preferredVariableOrder_) : e.name());
    }
    if (hasInitialPolarity_) {
      SatParameters.Polarity e = SatParameters.Polarity.forNumber(initialPolarity_);
      printer.printEnum("initial_polarity", e == null ? Integer.toString(initialPolarity_) : e.name());
    }
    if (hasMinimizationAlgorithm_) {
      SatParameters.ConflictMinimizationAlgorithm e = SatParameters.ConflictMinimizationAlgorithm.forNumber(minimizationAlgorithm_);
      printer.printEnum("minimization_algorithm", e == null ? Integer.toString(minimizationAlgorithm_) : e.name());
    }
    if (hasClauseCleanupPeriod_) {
      printer.printScalar("clause_cleanup_period", clauseCleanupPeriod_);
    }
    if (hasClauseCleanupTarget_) {
      printer.printScalar("clause_cleanup_target", clauseCleanupTarget_);
    }
    if (hasVariableActivityDecay_) {
      printer.printScalar("variable_activity_decay", variableActivityDecay_);
    }
    if (hasMaxVariableActivityValue_) {
      printer.printScalar("max_variable_activity_value", maxVariableActivityValue_);
    }
    if (hasClauseActivityDecay_) {
      printer.printScalar("clause_activity_decay", clauseActivityDecay_);
    }
    if (hasMaxClauseActivityValue_) {
      printer.printScalar("max_clause_activity_value", maxClauseActivityValue_);
    }
    if (hasGlucoseMaxDecay_) {
      printer.printScalar("glucose_max_decay", glucoseMaxDecay_);
    }
    if (hasGlucoseDecayIncrement_) {
      printer.printScalar("glucose_decay_increment", glucoseDecayIncrement_);
    }
    if (hasGlucoseDecayIncrementPeriod_) {
      printer.printScalar("glucose_decay_increment_period", glucoseDecayIncrementPeriod_);
    }
    if (hasRestartPeriod_) {
      printer.printScalar("restart_period", restartPeriod_);
    }
    if (hasRandomSeed_) {
      printer.printScalar("random_seed", randomSeed_);
    }
    if (hasRandomBranchesRatio_) {
      printer.printScalar("random_branches_ratio", randomBranchesRatio_);
    }
    if (hasBinaryMinimizationAlgorithm_) {
      SatParameters.BinaryMinizationAlgorithm e = SatParameters.BinaryMinizationAlgorithm.forNumber(binaryMinimizationAlgorithm_);
      printer.printEnum("binary_minimization_algorithm", e == null ? Integer.toString(binaryMinimizationAlgorithm_) : e.name());
    }
    if (hasUseOptimizationHints_) {
      printer.printScalar("use_optimization_hints", useOptimizationHints_);
    }
    if (hasMaxTimeInSeconds_) {
      printer.printScalar("max_time_in_seconds", maxTimeInSeconds_);
    }
    if (hasMaxNumberOfConflicts_) {
      printer.printScalar("max_number_of_conflicts", maxNumberOfConflicts_);
    }
    if (hasMaxMemoryInMb_) {
      printer.printScalar("max_memory_in_mb", maxMemoryInMb_);
    }
    if (hasLogSearchProgress_) {
      printer.printScalar("log_search_progress", logSearchProgress_);
    }
    if (hasUsePbResolution_) {
      printer.printScalar("use_pb_resolution", usePbResolution_);
    }
    if (hasUsePhaseSaving_) {
      printer.printScalar("use_phase_saving", usePhaseSaving_);
    }
    if (hasRandomPolarityRatio_) {
      printer.printScalar("random_polarity_ratio", randomPolarityRatio_);
    }
    if (hasPbCleanupIncrement_) {
      printer.printScalar("pb_cleanup_increment", pbCleanupIncrement_);
    }
    if (hasPbCleanupRatio_) {
      printer.printScalar("pb_cleanup_ratio", pbCleanupRatio_);
    }
    if (hasMinimizeReductionDuringPbResolution_) {
      printer.printScalar("minimize_reduction_during_pb_resolution", minimizeReductionDuringPbResolution_);
    }
    if (hasCountAssumptionLevelsInLbd_) {
      printer.printScalar("count_assumption_levels_in_lbd", countAssumptionLevelsInLbd_);
    }
    if (hasCoreMinimizationLevel_) {
      printer.printScalar("core_minimization_level", coreMinimizationLevel_);
    }
    if (hasMaxSatAssumptionOrder_) {
      SatParameters.MaxSatAssumptionOrder e = SatParameters.MaxSatAssumptionOrder.forNumber(maxSatAssumptionOrder_);
      printer.printEnum("max_sat_assumption_order", e == null ? Integer.toString(maxSatAssumptionOrder_) : e.name());
    }
    if (hasMaxSatReverseAssumptionOrder_) {
      printer.printScalar("max_sat_reverse_assumption_order", maxSatReverseAssumptionOrder_);
    }
    if (hasMaxSatStratification_) {
      SatParameters.MaxSatStratificationAlgorithm e = SatParameters.MaxSatStratificationAlgorithm.forNumber(maxSatStratification_);
      printer.printEnum("max_sat_stratification", e == null ? Integer.toString(maxSatStratification_) : e.name());
    }
    if (hasPresolveBveThreshold_) {
      printer.printScalar("presolve_bve_threshold", presolveBveThreshold_);
    }
    if (hasPresolveBveClauseWeight_) {
      printer.printScalar("presolve_bve_clause_weight", presolveBveClauseWeight_);
    }
    if (hasSubsumptionDuringConflictAnalysis_) {
      printer.printScalar("subsumption_during_conflict_analysis", subsumptionDuringConflictAnalysis_);
    }
    if (hasPresolveProbingDeterministicTimeLimit_) {
      printer.printScalar("presolve_probing_deterministic_time_limit", presolveProbingDeterministicTimeLimit_);
    }
    if (hasClauseCleanupLbdBound_) {
      printer.printScalar("clause_cleanup_lbd_bound", clauseCleanupLbdBound_);
    }
    if (hasClauseCleanupOrdering_) {
      SatParameters.ClauseOrdering e = SatParameters.ClauseOrdering.forNumber(clauseCleanupOrdering_);
      printer.printEnum("clause_cleanup_ordering", e == null ? Integer.toString(clauseCleanupOrdering_) : e.name());
    }
    if (restartAlgorithms_ != null) {
      for (SatParameters.RestartAlgorithm v : restartAlgorithms_) {
        printer.printEnum("restart_algorithms", v.name());
      }
    }
    if (hasRestartRunningWindowSize_) {
      printer.printScalar("restart_running_window_size", restartRunningWindowSize_);
    }
    if (hasRestartDlAverageRatio_) {
      printer.printScalar("restart_dl_average_ratio", restartDlAverageRatio_);
    }
    if (hasUseBlockingRestart_) {
      printer.printScalar("use_blocking_restart", useBlockingRestart_);
    }
    if (hasBlockingRestartWindowSize_) {
      printer.printScalar("blocking_restart_window_size", blockingRestartWindowSize_);
    }
    if (hasBlockingRestartMultiplier_) {
      printer.printScalar("blocking_restart_multiplier", blockingRestartMultiplier_);
    }
    if (hasMaxDeterministicTime_) {
      printer.printScalar("max_deterministic_time", maxDeterministicTime_);
    }
    if (hasNumConflictsBeforeStrategyChanges_) {
      printer.printScalar("num_conflicts_before_strategy_changes", numConflictsBeforeStrategyChanges_);
    }
    if (hasStrategyChangeIncreaseRatio_) {
      printer.printScalar("strategy_change_increase_ratio", strategyChangeIncreaseRatio_);
    }
    if (hasDefaultRestartAlgorithms_) {
      printer.printScalar("default_restart_algorithms", defaultRestartAlgorithms_);
    }
    if (hasRestartLbdAverageRatio_) {
      printer.printScalar("restart_lbd_average_ratio", restartLbdAverageRatio_);
    }
    if (hasPresolveUseBva_) {
      printer.printScalar("presolve_use_bva", presolveUseBva_);
    }
    if (hasPresolveBvaThreshold_) {
      printer.printScalar("presolve_bva_threshold", presolveBvaThreshold_);
    }
    if (hasUsePrecedencesInDisjunctiveConstraint_) {
      printer.printScalar("use_precedences_in_disjunctive_constraint", usePrecedencesInDisjunctiveConstraint_);
    }
    if (hasUseErwaHeuristic_) {
      printer.printScalar("use_erwa_heuristic", useErwaHeuristic_);
    }
    if (hasInitialVariablesActivity_) {
      printer.printScalar("initial_variables_activity", initialVariablesActivity_);
    }
    if (hasAlsoBumpVariablesInConflictReasons_) {
      printer.printScalar("also_bump_variables_in_conflict_reasons", alsoBumpVariablesInConflictReasons_);
    }
    if (hasUseOverloadCheckerInCumulative_) {
      printer.printScalar("use_overload_checker_in_cumulative", useOverloadCheckerInCumulative_);
    }
    if (hasUseTimetableEdgeFindingInCumulative_) {
      printer.printScalar("use_timetable_edge_finding_in_cumulative", useTimetableEdgeFindingInCumulative_);
    }
    if (hasUseDisjunctiveConstraintInCumulative_) {
      printer.printScalar("use_disjunctive_constraint_in_cumulative", useDisjunctiveConstraintInCumulative_);
    }
    if (hasSearchBranching_) {
      SatParameters.SearchBranching e = SatParameters.SearchBranching.forNumber(searchBranching_);
      printer.printEnum("search_branching", e == null ? Integer.toString(searchBranching_) : e.name());
    }
    if (hasOptimizeWithCore_) {
      printer.printScalar("optimize_with_core", optimizeWithCore_);
    }
    if (hasFindMultipleCores_) {
      printer.printScalar("find_multiple_cores", findMultipleCores_);
    }
    if (hasOptimizeWithMaxHs_) {
      printer.printScalar("optimize_with_max_hs", optimizeWithMaxHs_);
    }
    if (hasCpModelPresolve_) {
      printer.printScalar("cp_model_presolve", cpModelPresolve_);
    }
    if (hasEnumerateAllSolutions_) {
      printer.printScalar("enumerate_all_solutions", enumerateAllSolutions_);
    }
    if (hasPresolveBlockedClause_) {
      printer.printScalar("presolve_blocked_clause", presolveBlockedClause_);
    }
    if (hasCoverOptimization_) {
      printer.printScalar("cover_optimization", coverOptimization_);
    }
    if (hasLinearizationLevel_) {
      printer.printScalar("linearization_level", linearizationLevel_);
    }
    if (hasMaxNumCuts_) {
      printer.printScalar("max_num_cuts", maxNumCuts_);
    }
    if (hasOnlyAddCutsAtLevelZero_) {
      printer.printScalar("only_add_cuts_at_level_zero", onlyAddCutsAtLevelZero_);
    }
    if (hasCpModelUseSatPresolve_) {
      printer.printScalar("cp_model_use_sat_presolve", cpModelUseSatPresolve_);
    }
    if (hasExploitIntegerLpSolution_) {
      printer.printScalar("exploit_integer_lp_solution", exploitIntegerLpSolution_);
    }
    if (hasAutoDetectGreaterThanAtLeastOneOf_) {
      printer.printScalar("auto_detect_greater_than_at_least_one_of", autoDetectGreaterThanAtLeastOneOf_);
    }
    if (hasStopAfterFirstSolution_) {
      printer.printScalar("stop_after_first_solution", stopAfterFirstSolution_);
    }
    if (hasBinarySearchNumConflicts_) {
      printer.printScalar("binary_search_num_conflicts", binarySearchNumConflicts_);
    }
    if (hasNumSearchWorkers_) {
      printer.printScalar("num_search_workers", numSearchWorkers_);
    }
    if (hasUseLnsOnly_) {
      printer.printScalar("use_lns_only", useLnsOnly_);
    }
    if (hasRandomizeSearch_) {
      printer.printScalar("randomize_search", randomizeSearch_);
    }
    if (hasSearchRandomVariablePoolSize_) {
      printer.printScalar("search_random_variable_pool_size", searchRandomVariablePoolSize_);
    }
    if (hasInstantiateAllVariables_) {
      printer.printScalar("instantiate_all_variables", instantiateAllVariables_);
    }
    if (hasBooleanEncodingLevel_) {
      printer.printScalar("boolean_encoding_level", booleanEncodingLevel_);
    }
    if (hasUseOptionalVariables_) {
      printer.printScalar("use_optional_variables", useOptionalVariables_);
    }
    if (hasUseExactLpReason_) {
      printer.printScalar("use_exact_lp_reason", useExactLpReason_);
    }
    if (hasCpModelProbingLevel_) {
      printer.printScalar("cp_model_probing_level", cpModelProbingLevel_);
    }
    if (hasAddLpConstraintsLazily_) {
      printer.printScalar("add_lp_constraints_lazily", addLpConstraintsLazily_);
    }
    if (hasShareObjectiveBounds_) {
      printer.printScalar("share_objective_bounds", shareObjectiveBounds_);
    }
    if (hasShareLevelZeroBounds_) {
      printer.printScalar("share_level_zero_bounds", shareLevelZeroBounds_);
    }
    if (hasMinOrthogonalityForLpConstraints_) {
      printer.printScalar("min_orthogonality_for_lp_constraints", minOrthogonalityForLpConstraints_);
    }
    if (hasExploitAllLpSolution_) {
      printer.printScalar("exploit_all_lp_solution", exploitAllLpSolution_);
    }
    if (hasAddCgCuts_) {
      printer.printScalar("add_cg_cuts", addCgCuts_);
    }
    if (hasMaxIntegerRoundingScaling_) {
      printer.printScalar("max_integer_rounding_scaling", maxIntegerRoundingScaling_);
    }
    if (hasAddMirCuts_) {
      printer.printScalar("add_mir_cuts", addMirCuts_);
    }
    if (hasMaxConsecutiveInactiveCount_) {
      printer.printScalar("max_consecutive_inactive_count", maxConsecutiveInactiveCount_);
    }
    if (hasNewConstraintsBatchSize_) {
      printer.printScalar("new_constraints_batch_size", newConstraintsBatchSize_);
    }
    if (hasPseudoCostReliabilityThreshold_) {
      printer.printScalar("pseudo_cost_reliability_threshold", pseudoCostReliabilityThreshold_);
    }
    if (hasMipMaxBound_) {
      printer.printScalar("mip_max_bound", mipMaxBound_);
    }
    if (hasMipVarScaling_) {
      printer.printScalar("mip_var_scaling", mipVarScaling_);
    }
    if (hasMipWantedPrecision_) {
      printer.printScalar("mip_wanted_precision", mipWantedPrecision_);
    }
    if (hasMipMaxActivityExponent_) {
      printer.printScalar("mip_max_activity_exponent", mipMaxActivityExponent_);
    }
    if (hasMipCheckPrecision_) {
      printer.printScalar("mip_check_precision", mipCheckPrecision_);
    }
    if (hasUseRinsLns_) {
      printer.printScalar("use_rins_lns", useRinsLns_);
    }
    if (hasExploitBestSolution_) {
      printer.printScalar("exploit_best_solution", exploitBestSolution_);
    }
    if (hasExploitObjective_) {
      printer.printScalar("exploit_objective", exploitObjective_);
    }
    if (hasFillTightenedDomainsInResponse_) {
      printer.printScalar("fill_tightened_domains_in_response", fillTightenedDomainsInResponse_);
    }
    if (hasUseCombinedNoOverlap_) {
      printer.printScalar("use_combined_no_overlap", useCombinedNoOverlap_);
    }
    if (hasInterleaveBatchSize_) {
      printer.printScalar("interleave_batch_size", interleaveBatchSize_);
    }
    if (hasCatchSigintSignal_) {
      printer.printScalar("catch_sigint_signal", catchSigintSignal_);
    }
    if (hasInterleaveSearch_) {
      printer.printScalar("interleave_search", interleaveSearch_);
    }
    if (hasDiversifyLnsParams_) {
      printer.printScalar("diversify_lns_params", diversifyLnsParams_);
    }
    if (hasMaxPresolveIterations_) {
      printer.printScalar("max_presolve_iterations", maxPresolveIterations_);
    }
    if (hasUseImpliedBounds_) {
      printer.printScalar("use_implied_bounds", useImpliedBounds_);
    }
    if (hasMergeNoOverlapWorkLimit_) {
      printer.printScalar("merge_no_overlap_work_limit", mergeNoOverlapWorkLimit_);
    }
    if (hasMergeAtMostOneWorkLimit_) {
      printer.printScalar("merge_at_most_one_work_limit", mergeAtMostOneWorkLimit_);
    }
    if (hasPresolveSubstitutionLevel_) {
      printer.printScalar("presolve_substitution_level", presolveSubstitutionLevel_);
    }
    if (hasMaxAllDiffCutSize_) {
      printer.printScalar("max_all_diff_cut_size", maxAllDiffCutSize_);
    }
    if (hasStopAfterPresolve_) {
      printer.printScalar("stop_after_presolve", stopAfterPresolve_);
    }
    if (hasDebugMaxNumPresolveOperations_) {
      printer.printScalar("debug_max_num_presolve_operations", debugMaxNumPresolveOperations_);
    }
    if (hasAddLinMaxCuts_) {
      printer.printScalar("add_lin_max_cuts", addLinMaxCuts_);
    }
    if (hasHintConflictLimit_) {
      printer.printScalar("hint_conflict_limit", hintConflictLimit_);
    }
    if (hasMaxCutRoundsAtLevelZero_) {
      printer.printScalar("max_cut_rounds_at_level_zero", maxCutRoundsAtLevelZero_);
    }
    if (hasCutMaxActiveCountValue_) {
      printer.printScalar("cut_max_active_count_value", cutMaxActiveCountValue_);
    }
    if (hasCutActiveCountDecay_) {
      printer.printScalar("cut_active_count_decay", cutActiveCountDecay_);
    }
    if (hasCutCleanupTarget_) {
      printer.printScalar("cut_cleanup_target", cutCleanupTarget_);
    }
    if (hasAbsoluteGapLimit_) {
      printer.printScalar("absolute_gap_limit", absoluteGapLimit_);
    }
    if (hasRelativeGapLimit_) {
      printer.printScalar("relative_gap_limit", relativeGapLimit_);
    }
    if (hasExploitRelaxationSolution_) {
      printer.printScalar("exploit_relaxation_solution", exploitRelaxationSolution_);
    }
    if (hasDebugPostsolveWithFullSolver_) {
      printer.printScalar("debug_postsolve_with_full_solver", debugPostsolveWithFullSolver_);
    }
    if (hasUseSatInprocessing_) {
      printer.printScalar("use_sat_inprocessing", useSatInprocessing_);
    }
    if (hasUseFeasibilityPump_) {
      printer.printScalar("use_feasibility_pump", useFeasibilityPump_);
    }
    if (hasFpRounding_) {
      SatParameters.FPRoundingMethod e = SatParameters.FPRoundingMethod.forNumber(fpRounding_);
      printer.printEnum("fp_rounding", e == null ? Integer.toString(fpRounding_) : e.name());
    }
    if (hasMipAutomaticallyScaleVariables_) {
      printer.printScalar("mip_automatically_scale_variables", mipAutomaticallyScaleVariables_);
    }
    if (hasRepairHint_) {
      printer.printScalar("repair_hint", repairHint_);
    }
    if (hasPolarityRephaseIncrement_) {
      printer.printScalar("polarity_rephase_increment", polarityRephaseIncrement_);
    }
    if (hasAddZeroHalfCuts_) {
      printer.printScalar("add_zero_half_cuts", addZeroHalfCuts_);
    }
    if (hasExpandAlldiffConstraints_) {
      printer.printScalar("expand_alldiff_constraints", expandAlldiffConstraints_);
    }
    if (hasName_) {
      printer.printScalar("name", name_);
    }
    if (hasAddCliqueCuts_) {
      printer.printScalar("add_clique_cuts", addCliqueCuts_);
    }
    if (hasKeepAllFeasibleSolutionsInPresolve_) {
      printer.printScalar("keep_all_feasible_solutions_in_presolve", keepAllFeasibleSolutionsInPresolve_);
    }
    if (hasPresolveExtractIntegerEnforcement_) {
      printer.printScalar("presolve_extract_integer_enforcement", presolveExtractIntegerEnforcement_);
    }
    if (hasPolishLpSolution_) {
      printer.printScalar("polish_lp_solution", polishLpSolution_);
    }
    if (hasUseProbingSearch_) {
      printer.printScalar("use_probing_search", useProbingSearch_);
    }
    if (hasConvertIntervals_) {
      printer.printScalar("convert_intervals", convertIntervals_);
    }
    if (hasPermuteVariableRandomly_) {
      printer.printScalar("permute_variable_randomly", permuteVariableRandomly_);
    }
    if (hasPermutePresolveConstraintOrder_) {
      printer.printScalar("permute_presolve_constraint_order", permutePresolveConstraintOrder_);
    }
    if (hasUseAbslRandom_) {
      printer.printScalar("use_absl_random", useAbslRandom_);
    }
    if (hasDisableConstraintExpansion_) {
      printer.printScalar("disable_constraint_expansion", disableConstraintExpansion_);
    }
    if (hasExpandReservoirConstraints_) {
      printer.printScalar("expand_reservoir_constraints", expandReservoirConstraints_);
    }
    if (hasSymmetryLevel_) {
      printer.printScalar("symmetry_level", symmetryLevel_);
    }
    if (hasLogPrefix_) {
      printer.printScalar("log_prefix", logPrefix_);
    }
    if (hasLogToStdout_) {
      printer.printScalar("log_to_stdout", logToStdout_);
    }
    if (hasLogToResponse_) {
      printer.printScalar("log_to_response", logToResponse_);
    }
    if (hasOptimizeWithLbTreeSearch_) {
      printer.printScalar("optimize_with_lb_tree_search", optimizeWithLbTreeSearch_);
    }
    if (hasLogSubsolverStatistics_) {
      printer.printScalar("log_subsolver_statistics", logSubsolverStatistics_);
    }
    if (hasClauseCleanupRatio_) {
      printer.printScalar("clause_cleanup_ratio", clauseCleanupRatio_);
    }
    if (hasMaxDomainSizeWhenEncodingEqNeqConstraints_) {
      printer.printScalar("max_domain_size_when_encoding_eq_neq_constraints", maxDomainSizeWhenEncodingEqNeqConstraints_);
    }
    if (hasFixVariablesToTheirHintedValue_) {
      printer.printScalar("fix_variables_to_their_hinted_value", fixVariablesToTheirHintedValue_);
    }
    if (hasSolutionPoolSize_) {
      printer.printScalar("solution_pool_size", solutionPoolSize_);
    }
    if (hasFillAdditionalSolutionsInResponse_) {
      printer.printScalar("fill_additional_solutions_in_response", fillAdditionalSolutionsInResponse_);
    }
    if (hasDebugCrashOnBadHint_) {
      printer.printScalar("debug_crash_on_bad_hint", debugCrashOnBadHint_);
    }
    if (hasCutLevel_) {
      printer.printScalar("cut_level", cutLevel_);
    }
    if (hasAddObjectiveCut_) {
      printer.printScalar("add_objective_cut", addObjectiveCut_);
    }
    if (hasMipComputeTrueObjectiveBound_) {
      printer.printScalar("mip_compute_true_objective_bound", mipComputeTrueObjectiveBound_);
    }
    if (hasMipMaxValidMagnitude_) {
      printer.printScalar("mip_max_valid_magnitude", mipMaxValidMagnitude_);
    }
    if (hasUseTimetablingInNoOverlap2D_) {
      printer.printScalar("use_timetabling_in_no_overlap_2d", useTimetablingInNoOverlap2D_);
    }
    if (hasPresolveInclusionWorkLimit_) {
      printer.printScalar("presolve_inclusion_work_limit", presolveInclusionWorkLimit_);
    }
    if (hasIgnoreNames_) {
      printer.printScalar("ignore_names", ignoreNames_);
    }
    if (hasShareBinaryClauses_) {
      printer.printScalar("share_binary_clauses", shareBinaryClauses_);
    }
    if (hasShavingDeterministicTimeInProbingSearch_) {
      printer.printScalar("shaving_deterministic_time_in_probing_search", shavingDeterministicTimeInProbingSearch_);
    }
    if (hasShavingSearchDeterministicTime_) {
      printer.printScalar("shaving_search_deterministic_time", shavingSearchDeterministicTime_);
    }
    if (hasNumWorkers_) {
      printer.printScalar("num_workers", numWorkers_);
    }
    if (subsolvers_ != null) {
      for (String v : subsolvers_) {
        printer.printScalar("subsolvers", v);
      }
    }
    if (ignoreSubsolvers_ != null) {
      for (String v : ignoreSubsolvers_) {
        printer.printScalar("ignore_subsolvers", v);
      }
    }
    if (subsolverParams_ != null) {
      for (SatParameters v : subsolverParams_) {
        printer.printMessage("subsolver_params", v);
      }
    }
    if (hasUseEnergeticReasoningInNoOverlap2D_) {
      printer.printScalar("use_energetic_reasoning_in_no_overlap_2d", useEnergeticReasoningInNoOverlap2D_);
    }
    if (hasUseDualSchedulingHeuristics_) {
      printer.printScalar("use_dual_scheduling_heuristics", useDualSchedulingHeuristics_);
    }
    if (hasUseHardPrecedencesInCumulative_) {
      printer.printScalar("use_hard_precedences_in_cumulative", useHardPrecedencesInCumulative_);
    }
    if (hasDetectTableWithCost_) {
      printer.printScalar("detect_table_with_cost", detectTableWithCost_);
    }
    if (hasTableCompressionLevel_) {
      printer.printScalar("table_compression_level", tableCompressionLevel_);
    }
    if (extraSubsolvers_ != null) {
      for (String v : extraSubsolvers_) {
        printer.printScalar("extra_subsolvers", v);
      }
    }
    if (hasExploitAllPrecedences_) {
      printer.printScalar("exploit_all_precedences", exploitAllPrecedences_);
    }
    if (hasPropagationLoopDetectionFactor_) {
      printer.printScalar("propagation_loop_detection_factor", propagationLoopDetectionFactor_);
    }
    if (hasOnlySolveIp_) {
      printer.printScalar("only_solve_ip", onlySolveIp_);
    }
    if (hasEncodeComplexLinearConstraintWithInteger_) {
      printer.printScalar("encode_complex_linear_constraint_with_integer", encodeComplexLinearConstraintWithInteger_);
    }
    if (hasNewLinearPropagation_) {
      printer.printScalar("new_linear_propagation", newLinearPropagation_);
    }
    if (hasMipScaleLargeDomain_) {
      printer.printScalar("mip_scale_large_domain", mipScaleLargeDomain_);
    }
    if (hasProbingDeterministicTimeLimit_) {
      printer.printScalar("probing_deterministic_time_limit", probingDeterministicTimeLimit_);
    }
    if (hasRootLpIterations_) {
      printer.printScalar("root_lp_iterations", rootLpIterations_);
    }
    if (hasUseObjectiveLbSearch_) {
      printer.printScalar("use_objective_lb_search", useObjectiveLbSearch_);
    }
    if (hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_) {
      printer.printScalar("max_size_to_create_precedence_literals_in_disjunctive", maxSizeToCreatePrecedenceLiteralsInDisjunctive_);
    }
    if (hasUseStrongPropagationInDisjunctive_) {
      printer.printScalar("use_strong_propagation_in_disjunctive", useStrongPropagationInDisjunctive_);
    }
    if (hasMipDropTolerance_) {
      printer.printScalar("mip_drop_tolerance", mipDropTolerance_);
    }
    if (hasInferAllDiffs_) {
      printer.printScalar("infer_all_diffs", inferAllDiffs_);
    }
    if (hasFindBigLinearOverlap_) {
      printer.printScalar("find_big_linear_overlap", findBigLinearOverlap_);
    }
    if (hasSharedTreeNumWorkers_) {
      printer.printScalar("shared_tree_num_workers", sharedTreeNumWorkers_);
    }
    if (hasUseSharedTreeSearch_) {
      printer.printScalar("use_shared_tree_search", useSharedTreeSearch_);
    }
    if (hasSharedTreeMaxNodesPerWorker_) {
      printer.printScalar("shared_tree_max_nodes_per_worker", sharedTreeMaxNodesPerWorker_);
    }
    if (hasSharedTreeSplitStrategy_) {
      SatParameters.SharedTreeSplitStrategy e = SatParameters.SharedTreeSplitStrategy.forNumber(sharedTreeSplitStrategy_);
      printer.printEnum("shared_tree_split_strategy", e == null ? Integer.toString(sharedTreeSplitStrategy_) : e.name());
    }
    if (hasUseLsOnly_) {
      printer.printScalar("use_ls_only", useLsOnly_);
    }
    if (hasFeasibilityJumpDecay_) {
      printer.printScalar("feasibility_jump_decay", feasibilityJumpDecay_);
    }
    if (hasNumViolationLs_) {
      printer.printScalar("num_violation_ls", numViolationLs_);
    }
    if (hasFeasibilityJumpVarRandomizationProbability_) {
      printer.printScalar("feasibility_jump_var_randomization_probability", feasibilityJumpVarRandomizationProbability_);
    }
    if (hasFeasibilityJumpVarPerburbationRangeRatio_) {
      printer.printScalar("feasibility_jump_var_perburbation_range_ratio", feasibilityJumpVarPerburbationRangeRatio_);
    }
    if (hasViolationLsPerturbationPeriod_) {
      printer.printScalar("violation_ls_perturbation_period", violationLsPerturbationPeriod_);
    }
    if (hasFeasibilityJumpEnableRestarts_) {
      printer.printScalar("feasibility_jump_enable_restarts", feasibilityJumpEnableRestarts_);
    }
    if (hasStopAfterRootPropagation_) {
      printer.printScalar("stop_after_root_propagation", stopAfterRootPropagation_);
    }
    if (hasUseObjectiveShavingSearch_) {
      printer.printScalar("use_objective_shaving_search", useObjectiveShavingSearch_);
    }
    if (hasUseLbRelaxLns_) {
      printer.printScalar("use_lb_relax_lns", useLbRelaxLns_);
    }
    if (hasLinearSplitSize_) {
      printer.printScalar("linear_split_size", linearSplitSize_);
    }
    if (hasFeasibilityJumpLinearizationLevel_) {
      printer.printScalar("feasibility_jump_linearization_level", feasibilityJumpLinearizationLevel_);
    }
    if (hasFeasibilityJumpRestartFactor_) {
      printer.printScalar("feasibility_jump_restart_factor", feasibilityJumpRestartFactor_);
    }
    if (hasMaxNumIntervalsForTimetableEdgeFinding_) {
      printer.printScalar("max_num_intervals_for_timetable_edge_finding", maxNumIntervalsForTimetableEdgeFinding_);
    }
    if (hasMipPresolveLevel_) {
      printer.printScalar("mip_presolve_level", mipPresolveLevel_);
    }
    if (hasPushAllTasksTowardStart_) {
      printer.printScalar("push_all_tasks_toward_start", pushAllTasksTowardStart_);
    }
    if (hasUseDynamicPrecedenceInDisjunctive_) {
      printer.printScalar("use_dynamic_precedence_in_disjunctive", useDynamicPrecedenceInDisjunctive_);
    }
    if (hasFeasibilityJumpMaxExpandedConstraintSize_) {
      printer.printScalar("feasibility_jump_max_expanded_constraint_size", feasibilityJumpMaxExpandedConstraintSize_);
    }
    if (hasUseFeasibilityJump_) {
      printer.printScalar("use_feasibility_jump", useFeasibilityJump_);
    }
    if (hasLpPrimalTolerance_) {
      printer.printScalar("lp_primal_tolerance", lpPrimalTolerance_);
    }
    if (hasLpDualTolerance_) {
      printer.printScalar("lp_dual_tolerance", lpDualTolerance_);
    }
    if (hasUseDynamicPrecedenceInCumulative_) {
      printer.printScalar("use_dynamic_precedence_in_cumulative", useDynamicPrecedenceInCumulative_);
    }
    if (hasUseExtendedProbing_) {
      printer.printScalar("use_extended_probing", useExtendedProbing_);
    }
    if (hasAtMostOneMaxExpansionSize_) {
      printer.printScalar("at_most_one_max_expansion_size", atMostOneMaxExpansionSize_);
    }
    if (hasUseAreaEnergeticReasoningInNoOverlap2D_) {
      printer.printScalar("use_area_energetic_reasoning_in_no_overlap_2d", useAreaEnergeticReasoningInNoOverlap2D_);
    }
    if (hasProbingNumCombinationsLimit_) {
      printer.printScalar("probing_num_combinations_limit", probingNumCombinationsLimit_);
    }
    if (hasInprocessingDtimeRatio_) {
      printer.printScalar("inprocessing_dtime_ratio", inprocessingDtimeRatio_);
    }
    if (hasInprocessingProbingDtime_) {
      printer.printScalar("inprocessing_probing_dtime", inprocessingProbingDtime_);
    }
    if (hasInprocessingMinimizationDtime_) {
      printer.printScalar("inprocessing_minimization_dtime", inprocessingMinimizationDtime_);
    }
    if (hasMaxPairsPairwiseReasoningInNoOverlap2D_) {
      printer.printScalar("max_pairs_pairwise_reasoning_in_no_overlap_2d", maxPairsPairwiseReasoningInNoOverlap2D_);
    }
    if (hasDetectLinearizedProduct_) {
      printer.printScalar("detect_linearized_product", detectLinearizedProduct_);
    }
    if (hasMipTreatHighMagnitudeBoundsAsInfinity_) {
      printer.printScalar("mip_treat_high_magnitude_bounds_as_infinity", mipTreatHighMagnitudeBoundsAsInfinity_);
    }
    if (hasAddRltCuts_) {
      printer.printScalar("add_rlt_cuts", addRltCuts_);
    }
    if (hasMaxLinMaxSizeForExpansion_) {
      printer.printScalar("max_lin_max_size_for_expansion", maxLinMaxSizeForExpansion_);
    }
    if (hasSharedTreeOpenLeavesPerWorker_) {
      printer.printScalar("shared_tree_open_leaves_per_worker", sharedTreeOpenLeavesPerWorker_);
    }
    if (hasSharedTreeWorkerMinRestartsPerSubtree_) {
      printer.printScalar("shared_tree_worker_min_restarts_per_subtree", sharedTreeWorkerMinRestartsPerSubtree_);
    }
    if (hasUseLns_) {
      printer.printScalar("use_lns", useLns_);
    }
    if (hasSaveLpBasisInLbTreeSearch_) {
      printer.printScalar("save_lp_basis_in_lb_tree_search", saveLpBasisInLbTreeSearch_);
    }
    if (hasShareGlueClauses_) {
      printer.printScalar("share_glue_clauses", shareGlueClauses_);
    }
    if (hasUseConservativeScaleOverloadChecker_) {
      printer.printScalar("use_conservative_scale_overload_checker", useConservativeScaleOverloadChecker_);
    }
    if (hasEncodeCumulativeAsReservoir_) {
      printer.printScalar("encode_cumulative_as_reservoir", encodeCumulativeAsReservoir_);
    }
    if (hasExpandReservoirUsingCircuit_) {
      printer.printScalar("expand_reservoir_using_circuit", expandReservoirUsingCircuit_);
    }
    if (hasVariablesShavingLevel_) {
      printer.printScalar("variables_shaving_level", variablesShavingLevel_);
    }
    if (hasShavingSearchThreshold_) {
      printer.printScalar("shaving_search_threshold", shavingSearchThreshold_);
    }
    if (hasMaxNumDeterministicBatches_) {
      printer.printScalar("max_num_deterministic_batches", maxNumDeterministicBatches_);
    }
    if (hasFeasibilityJumpBatchDtime_) {
      printer.printScalar("feasibility_jump_batch_dtime", feasibilityJumpBatchDtime_);
    }
    if (filterSubsolvers_ != null) {
      for (String v : filterSubsolvers_) {
        printer.printScalar("filter_subsolvers", v);
      }
    }
    if (hasNumFullSubsolvers_) {
      printer.printScalar("num_full_subsolvers", numFullSubsolvers_);
    }
    if (hasSharedTreeWorkerEnableTrailSharing_) {
      printer.printScalar("shared_tree_worker_enable_trail_sharing", sharedTreeWorkerEnableTrailSharing_);
    }
    if (hasLbRelaxNumWorkersThreshold_) {
      printer.printScalar("lb_relax_num_workers_threshold", lbRelaxNumWorkersThreshold_);
    }
    if (hasInprocessingMinimizationUseConflictAnalysis_) {
      printer.printScalar("inprocessing_minimization_use_conflict_analysis", inprocessingMinimizationUseConflictAnalysis_);
    }
    if (hasUseTryEdgeReasoningInNoOverlap2D_) {
      printer.printScalar("use_try_edge_reasoning_in_no_overlap_2d", useTryEdgeReasoningInNoOverlap2D_);
    }
    if (hasMinimizeSharedClauses_) {
      printer.printScalar("minimize_shared_clauses", minimizeSharedClauses_);
    }
    if (hasUseSymmetryInLp_) {
      printer.printScalar("use_symmetry_in_lp", useSymmetryInLp_);
    }
    if (hasSymmetryDetectionDeterministicTimeLimit_) {
      printer.printScalar("symmetry_detection_deterministic_time_limit", symmetryDetectionDeterministicTimeLimit_);
    }
    if (hasKeepSymmetryInPresolve_) {
      printer.printScalar("keep_symmetry_in_presolve", keepSymmetryInPresolve_);
    }
    if (hasSharedTreeWorkerEnablePhaseSharing_) {
      printer.printScalar("shared_tree_worker_enable_phase_sharing", sharedTreeWorkerEnablePhaseSharing_);
    }
    if (hasSharedTreeBalanceTolerance_) {
      printer.printScalar("shared_tree_balance_tolerance", sharedTreeBalanceTolerance_);
    }
    if (hasDebugCrashIfPresolveBreaksHint_) {
      printer.printScalar("debug_crash_if_presolve_breaks_hint", debugCrashIfPresolveBreaksHint_);
    }
    if (hasLnsInitialDifficulty_) {
      printer.printScalar("lns_initial_difficulty", lnsInitialDifficulty_);
    }
    if (hasLnsInitialDeterministicLimit_) {
      printer.printScalar("lns_initial_deterministic_limit", lnsInitialDeterministicLimit_);
    }
    if (hasPolarityExploitLsHints_) {
      printer.printScalar("polarity_exploit_ls_hints", polarityExploitLsHints_);
    }
    if (hasRemoveFixedVariablesEarly_) {
      printer.printScalar("remove_fixed_variables_early", removeFixedVariablesEarly_);
    }
    if (hasUseAllDifferentForCircuit_) {
      printer.printScalar("use_all_different_for_circuit", useAllDifferentForCircuit_);
    }
    if (hasRoutingCutSubsetSizeForBinaryRelationBound_) {
      printer.printScalar("routing_cut_subset_size_for_binary_relation_bound", routingCutSubsetSizeForBinaryRelationBound_);
    }
    if (hasRoutingCutSubsetSizeForTightBinaryRelationBound_) {
      printer.printScalar("routing_cut_subset_size_for_tight_binary_relation_bound", routingCutSubsetSizeForTightBinaryRelationBound_);
    }
    if (hasRoutingCutDpEffort_) {
      printer.printScalar("routing_cut_dp_effort", routingCutDpEffort_);
    }
    if (hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_) {
      printer.printScalar("maximum_regions_to_split_in_disconnected_no_overlap_2d", maximumRegionsToSplitInDisconnectedNoOverlap2D_);
    }
    if (hasRoutingCutSubsetSizeForExactBinaryRelationBound_) {
      printer.printScalar("routing_cut_subset_size_for_exact_binary_relation_bound", routingCutSubsetSizeForExactBinaryRelationBound_);
    }
    if (hasRoutingCutMaxInfeasiblePathLength_) {
      printer.printScalar("routing_cut_max_infeasible_path_length", routingCutMaxInfeasiblePathLength_);
    }
    if (hasRoutingCutSubsetSizeForShortestPathsBound_) {
      printer.printScalar("routing_cut_subset_size_for_shortest_paths_bound", routingCutSubsetSizeForShortestPathsBound_);
    }
    if (hasMaxAlldiffDomainSize_) {
      printer.printScalar("max_alldiff_domain_size", maxAlldiffDomainSize_);
    }
    if (hasNoOverlap2DBooleanRelationsLimit_) {
      printer.printScalar("no_overlap_2d_boolean_relations_limit", noOverlap2DBooleanRelationsLimit_);
    }
    if (hasShareGlueClausesDtime_) {
      printer.printScalar("share_glue_clauses_dtime", shareGlueClausesDtime_);
    }
    if (hasUseLinear3ForNoOverlap2DPrecedences_) {
      printer.printScalar("use_linear3_for_no_overlap_2d_precedences", useLinear3ForNoOverlap2DPrecedences_);
    }
    if (hasFilterSatPostsolveClauses_) {
      printer.printScalar("filter_sat_postsolve_clauses", filterSatPostsolveClauses_);
    }
    if (hasAlternativePoolSize_) {
      printer.printScalar("alternative_pool_size", alternativePoolSize_);
    }
    if (hasShareLinear2Bounds_) {
      printer.printScalar("share_linear2_bounds", shareLinear2Bounds_);
    }
    if (hasTransitivePrecedencesWorkLimit_) {
      printer.printScalar("transitive_precedences_work_limit", transitivePrecedencesWorkLimit_);
    }
    if (hasSharedTreeSplitMinDtime_) {
      printer.printScalar("shared_tree_split_min_dtime", sharedTreeSplitMinDtime_);
    }
    if (hasSolutionPoolDiversityLimit_) {
      printer.printScalar("solution_pool_diversity_limit", solutionPoolDiversityLimit_);
    }
    if (hasUseChronologicalBacktracking_) {
      printer.printScalar("use_chronological_backtracking", useChronologicalBacktracking_);
    }
    if (hasMaxBackjumpLevels_) {
      printer.printScalar("max_backjump_levels", maxBackjumpLevels_);
    }
    if (hasChronologicalBacktrackMinConflicts_) {
      printer.printScalar("chronological_backtrack_min_conflicts", chronologicalBacktrackMinConflicts_);
    }
    if (hasFindClausesThatAreExactlyOne_) {
      printer.printScalar("find_clauses_that_are_exactly_one", findClausesThatAreExactlyOne_);
    }
    if (hasLoadAtMostOnesInSatPresolve_) {
      printer.printScalar("load_at_most_ones_in_sat_presolve", loadAtMostOnesInSatPresolve_);
    }
    if (hasUseNewIntegerConflictResolution_) {
      printer.printScalar("use_new_integer_conflict_resolution", useNewIntegerConflictResolution_);
    }
    if (hasClauseCleanupPeriodIncrement_) {
      printer.printScalar("clause_cleanup_period_increment", clauseCleanupPeriodIncrement_);
    }
    if (hasDebugCrashIfLratCheckFails_) {
      printer.printScalar("debug_crash_if_lrat_check_fails", debugCrashIfLratCheckFails_);
    }
    if (hasMaxDomainSizeForLinear2Expansion_) {
      printer.printScalar("max_domain_size_for_linear2_expansion", maxDomainSizeForLinear2Expansion_);
    }
    if (hasCreate1UipBooleanDuringIcr_) {
      printer.printScalar("create_1uip_boolean_during_icr", create1UipBooleanDuringIcr_);
    }
    if (hasInprocessingUseCongruenceClosure_) {
      printer.printScalar("inprocessing_use_congruence_closure", inprocessingUseCongruenceClosure_);
    }
    if (hasEagerlySubsumeLastNConflicts_) {
      printer.printScalar("eagerly_subsume_last_n_conflicts", eagerlySubsumeLastNConflicts_);
    }
    if (hasCheckLratProof_) {
      printer.printScalar("check_lrat_proof", checkLratProof_);
    }
    if (hasOutputLratProof_) {
      printer.printScalar("output_lrat_proof", outputLratProof_);
    }
    if (hasClauseCleanupLbdTier1_) {
      printer.printScalar("clause_cleanup_lbd_tier1", clauseCleanupLbdTier1_);
    }
    if (hasClauseCleanupLbdTier2_) {
      printer.printScalar("clause_cleanup_lbd_tier2", clauseCleanupLbdTier2_);
    }
    if (hasExtraSubsumptionDuringConflictAnalysis_) {
      printer.printScalar("extra_subsumption_during_conflict_analysis", extraSubsumptionDuringConflictAnalysis_);
    }
    if (hasCheckMergedLratProof_) {
      printer.printScalar("check_merged_lrat_proof", checkMergedLratProof_);
    }
    if (hasDecisionSubsumptionDuringConflictAnalysis_) {
      printer.printScalar("decision_subsumption_during_conflict_analysis", decisionSubsumptionDuringConflictAnalysis_);
    }
    if (hasInprocessingUseSatSweeping_) {
      printer.printScalar("inprocessing_use_sat_sweeping", inprocessingUseSatSweeping_);
    }
    if (hasSubsumeDuringVivification_) {
      printer.printScalar("subsume_during_vivification", subsumeDuringVivification_);
    }
    if (hasNumOrderingInVivification_) {
      printer.printScalar("num_ordering_in_vivification", numOrderingInVivification_);
    }
    if (hasDetectEncodedComplexDomain_) {
      printer.printScalar("detect_encoded_complex_domain", detectEncodedComplexDomain_);
    }
    if (hasMipScaleToMaxBound_) {
      printer.printScalar("mip_scale_to_max_bound", mipScaleToMaxBound_);
    }
    if (hasMipPresolveRestrictBounds_) {
      printer.printScalar("mip_presolve_restrict_bounds", mipPresolveRestrictBounds_);
    }
    if (hasCpModelPureSatPresolve_) {
      printer.printScalar("cp_model_pure_sat_presolve", cpModelPureSatPresolve_);
    }
    if (hasInprocessingUseTransitiveReduction_) {
      printer.printScalar("inprocessing_use_transitive_reduction", inprocessingUseTransitiveReduction_);
    }
    if (hasInprocessingDetectAndSweepCircuit_) {
      printer.printScalar("inprocessing_detect_and_sweep_circuit", inprocessingDetectAndSweepCircuit_);
    }
    if (hasTryHintAsAssumptions_) {
      printer.printScalar("try_hint_as_assumptions", tryHintAsAssumptions_);
    }
    if (hasLearnOnAssumptionsUnsat_) {
      printer.printScalar("learn_on_assumptions_unsat", learnOnAssumptionsUnsat_);
    }
    if (hasSharedTreeWorkerPropagateIntermediateNodes_) {
      printer.printScalar("shared_tree_worker_propagate_intermediate_nodes", sharedTreeWorkerPropagateIntermediateNodes_);
    }
    if (hasShavingSearchUseRandomSearch_) {
      printer.printScalar("shaving_search_use_random_search", shavingSearchUseRandomSearch_);
    }
    if (hasShavingSearchRestartLimit_) {
      printer.printScalar("shaving_search_restart_limit", shavingSearchRestartLimit_);
    }
    if (hasObjectiveShavingSearchThreshold_) {
      printer.printScalar("objective_shaving_search_threshold", objectiveShavingSearchThreshold_);
    }
    if (hasCpModelDirectSolve_) {
      printer.printScalar("cp_model_direct_solve", cpModelDirectSolve_);
    }
    if (hasMipUseTightBounds_) {
      printer.printScalar("mip_use_tight_bounds", mipUseTightBounds_);
    }
    if (hasMipTightPrecision_) {
      printer.printScalar("mip_tight_precision", mipTightPrecision_);
    }
    if (hasMipVerifySolution_) {
      printer.printScalar("mip_verify_solution", mipVerifySolution_);
    }
    if (hasMipProperRelaxation_) {
      printer.printScalar("mip_proper_relaxation", mipProperRelaxation_);
    }
    if (hasFeasibilityJumpIgnoreRedundantConstraints_) {
      printer.printScalar("feasibility_jump_ignore_redundant_constraints", feasibilityJumpIgnoreRedundantConstraints_);
    }
    if (hasLnsIgnoreRedundantConstraints_) {
      printer.printScalar("lns_ignore_redundant_constraints", lnsIgnoreRedundantConstraints_);
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
    if (!(obj instanceof SatParameters)) {
      return false;
    }
    SatParameters other = (SatParameters) obj;
    return name_.equals(other.name_)
        && hasName_ == other.hasName_
        && preferredVariableOrder_ == other.preferredVariableOrder_
        && hasPreferredVariableOrder_ == other.hasPreferredVariableOrder_
        && initialPolarity_ == other.initialPolarity_
        && hasInitialPolarity_ == other.hasInitialPolarity_
        && usePhaseSaving_ == other.usePhaseSaving_
        && hasUsePhaseSaving_ == other.hasUsePhaseSaving_
        && polarityRephaseIncrement_ == other.polarityRephaseIncrement_
        && hasPolarityRephaseIncrement_ == other.hasPolarityRephaseIncrement_
        && polarityExploitLsHints_ == other.polarityExploitLsHints_
        && hasPolarityExploitLsHints_ == other.hasPolarityExploitLsHints_
        && Double.compare(randomPolarityRatio_, other.randomPolarityRatio_) == 0
        && hasRandomPolarityRatio_ == other.hasRandomPolarityRatio_
        && Double.compare(randomBranchesRatio_, other.randomBranchesRatio_) == 0
        && hasRandomBranchesRatio_ == other.hasRandomBranchesRatio_
        && useErwaHeuristic_ == other.useErwaHeuristic_
        && hasUseErwaHeuristic_ == other.hasUseErwaHeuristic_
        && Double.compare(initialVariablesActivity_, other.initialVariablesActivity_) == 0
        && hasInitialVariablesActivity_ == other.hasInitialVariablesActivity_
        && alsoBumpVariablesInConflictReasons_ == other.alsoBumpVariablesInConflictReasons_
        && hasAlsoBumpVariablesInConflictReasons_ == other.hasAlsoBumpVariablesInConflictReasons_
        && minimizationAlgorithm_ == other.minimizationAlgorithm_
        && hasMinimizationAlgorithm_ == other.hasMinimizationAlgorithm_
        && binaryMinimizationAlgorithm_ == other.binaryMinimizationAlgorithm_
        && hasBinaryMinimizationAlgorithm_ == other.hasBinaryMinimizationAlgorithm_
        && subsumptionDuringConflictAnalysis_ == other.subsumptionDuringConflictAnalysis_
        && hasSubsumptionDuringConflictAnalysis_ == other.hasSubsumptionDuringConflictAnalysis_
        && extraSubsumptionDuringConflictAnalysis_ == other.extraSubsumptionDuringConflictAnalysis_
        && hasExtraSubsumptionDuringConflictAnalysis_ == other.hasExtraSubsumptionDuringConflictAnalysis_
        && decisionSubsumptionDuringConflictAnalysis_ == other.decisionSubsumptionDuringConflictAnalysis_
        && hasDecisionSubsumptionDuringConflictAnalysis_ == other.hasDecisionSubsumptionDuringConflictAnalysis_
        && eagerlySubsumeLastNConflicts_ == other.eagerlySubsumeLastNConflicts_
        && hasEagerlySubsumeLastNConflicts_ == other.hasEagerlySubsumeLastNConflicts_
        && subsumeDuringVivification_ == other.subsumeDuringVivification_
        && hasSubsumeDuringVivification_ == other.hasSubsumeDuringVivification_
        && useChronologicalBacktracking_ == other.useChronologicalBacktracking_
        && hasUseChronologicalBacktracking_ == other.hasUseChronologicalBacktracking_
        && maxBackjumpLevels_ == other.maxBackjumpLevels_
        && hasMaxBackjumpLevels_ == other.hasMaxBackjumpLevels_
        && chronologicalBacktrackMinConflicts_ == other.chronologicalBacktrackMinConflicts_
        && hasChronologicalBacktrackMinConflicts_ == other.hasChronologicalBacktrackMinConflicts_
        && learnOnAssumptionsUnsat_ == other.learnOnAssumptionsUnsat_
        && hasLearnOnAssumptionsUnsat_ == other.hasLearnOnAssumptionsUnsat_
        && clauseCleanupPeriod_ == other.clauseCleanupPeriod_
        && hasClauseCleanupPeriod_ == other.hasClauseCleanupPeriod_
        && clauseCleanupPeriodIncrement_ == other.clauseCleanupPeriodIncrement_
        && hasClauseCleanupPeriodIncrement_ == other.hasClauseCleanupPeriodIncrement_
        && clauseCleanupTarget_ == other.clauseCleanupTarget_
        && hasClauseCleanupTarget_ == other.hasClauseCleanupTarget_
        && Double.compare(clauseCleanupRatio_, other.clauseCleanupRatio_) == 0
        && hasClauseCleanupRatio_ == other.hasClauseCleanupRatio_
        && clauseCleanupLbdBound_ == other.clauseCleanupLbdBound_
        && hasClauseCleanupLbdBound_ == other.hasClauseCleanupLbdBound_
        && clauseCleanupLbdTier1_ == other.clauseCleanupLbdTier1_
        && hasClauseCleanupLbdTier1_ == other.hasClauseCleanupLbdTier1_
        && clauseCleanupLbdTier2_ == other.clauseCleanupLbdTier2_
        && hasClauseCleanupLbdTier2_ == other.hasClauseCleanupLbdTier2_
        && clauseCleanupOrdering_ == other.clauseCleanupOrdering_
        && hasClauseCleanupOrdering_ == other.hasClauseCleanupOrdering_
        && pbCleanupIncrement_ == other.pbCleanupIncrement_
        && hasPbCleanupIncrement_ == other.hasPbCleanupIncrement_
        && Double.compare(pbCleanupRatio_, other.pbCleanupRatio_) == 0
        && hasPbCleanupRatio_ == other.hasPbCleanupRatio_
        && Double.compare(variableActivityDecay_, other.variableActivityDecay_) == 0
        && hasVariableActivityDecay_ == other.hasVariableActivityDecay_
        && Double.compare(maxVariableActivityValue_, other.maxVariableActivityValue_) == 0
        && hasMaxVariableActivityValue_ == other.hasMaxVariableActivityValue_
        && Double.compare(glucoseMaxDecay_, other.glucoseMaxDecay_) == 0
        && hasGlucoseMaxDecay_ == other.hasGlucoseMaxDecay_
        && Double.compare(glucoseDecayIncrement_, other.glucoseDecayIncrement_) == 0
        && hasGlucoseDecayIncrement_ == other.hasGlucoseDecayIncrement_
        && glucoseDecayIncrementPeriod_ == other.glucoseDecayIncrementPeriod_
        && hasGlucoseDecayIncrementPeriod_ == other.hasGlucoseDecayIncrementPeriod_
        && Double.compare(clauseActivityDecay_, other.clauseActivityDecay_) == 0
        && hasClauseActivityDecay_ == other.hasClauseActivityDecay_
        && Double.compare(maxClauseActivityValue_, other.maxClauseActivityValue_) == 0
        && hasMaxClauseActivityValue_ == other.hasMaxClauseActivityValue_
        && getRestartAlgorithmsList().equals(other.getRestartAlgorithmsList())
        && defaultRestartAlgorithms_.equals(other.defaultRestartAlgorithms_)
        && hasDefaultRestartAlgorithms_ == other.hasDefaultRestartAlgorithms_
        && restartPeriod_ == other.restartPeriod_
        && hasRestartPeriod_ == other.hasRestartPeriod_
        && restartRunningWindowSize_ == other.restartRunningWindowSize_
        && hasRestartRunningWindowSize_ == other.hasRestartRunningWindowSize_
        && Double.compare(restartDlAverageRatio_, other.restartDlAverageRatio_) == 0
        && hasRestartDlAverageRatio_ == other.hasRestartDlAverageRatio_
        && Double.compare(restartLbdAverageRatio_, other.restartLbdAverageRatio_) == 0
        && hasRestartLbdAverageRatio_ == other.hasRestartLbdAverageRatio_
        && useBlockingRestart_ == other.useBlockingRestart_
        && hasUseBlockingRestart_ == other.hasUseBlockingRestart_
        && blockingRestartWindowSize_ == other.blockingRestartWindowSize_
        && hasBlockingRestartWindowSize_ == other.hasBlockingRestartWindowSize_
        && Double.compare(blockingRestartMultiplier_, other.blockingRestartMultiplier_) == 0
        && hasBlockingRestartMultiplier_ == other.hasBlockingRestartMultiplier_
        && numConflictsBeforeStrategyChanges_ == other.numConflictsBeforeStrategyChanges_
        && hasNumConflictsBeforeStrategyChanges_ == other.hasNumConflictsBeforeStrategyChanges_
        && Double.compare(strategyChangeIncreaseRatio_, other.strategyChangeIncreaseRatio_) == 0
        && hasStrategyChangeIncreaseRatio_ == other.hasStrategyChangeIncreaseRatio_
        && Double.compare(maxTimeInSeconds_, other.maxTimeInSeconds_) == 0
        && hasMaxTimeInSeconds_ == other.hasMaxTimeInSeconds_
        && Double.compare(maxDeterministicTime_, other.maxDeterministicTime_) == 0
        && hasMaxDeterministicTime_ == other.hasMaxDeterministicTime_
        && maxNumDeterministicBatches_ == other.maxNumDeterministicBatches_
        && hasMaxNumDeterministicBatches_ == other.hasMaxNumDeterministicBatches_
        && maxNumberOfConflicts_ == other.maxNumberOfConflicts_
        && hasMaxNumberOfConflicts_ == other.hasMaxNumberOfConflicts_
        && maxMemoryInMb_ == other.maxMemoryInMb_
        && hasMaxMemoryInMb_ == other.hasMaxMemoryInMb_
        && Double.compare(absoluteGapLimit_, other.absoluteGapLimit_) == 0
        && hasAbsoluteGapLimit_ == other.hasAbsoluteGapLimit_
        && Double.compare(relativeGapLimit_, other.relativeGapLimit_) == 0
        && hasRelativeGapLimit_ == other.hasRelativeGapLimit_
        && randomSeed_ == other.randomSeed_
        && hasRandomSeed_ == other.hasRandomSeed_
        && permuteVariableRandomly_ == other.permuteVariableRandomly_
        && hasPermuteVariableRandomly_ == other.hasPermuteVariableRandomly_
        && permutePresolveConstraintOrder_ == other.permutePresolveConstraintOrder_
        && hasPermutePresolveConstraintOrder_ == other.hasPermutePresolveConstraintOrder_
        && useAbslRandom_ == other.useAbslRandom_
        && hasUseAbslRandom_ == other.hasUseAbslRandom_
        && logSearchProgress_ == other.logSearchProgress_
        && hasLogSearchProgress_ == other.hasLogSearchProgress_
        && logSubsolverStatistics_ == other.logSubsolverStatistics_
        && hasLogSubsolverStatistics_ == other.hasLogSubsolverStatistics_
        && logPrefix_.equals(other.logPrefix_)
        && hasLogPrefix_ == other.hasLogPrefix_
        && logToStdout_ == other.logToStdout_
        && hasLogToStdout_ == other.hasLogToStdout_
        && logToResponse_ == other.logToResponse_
        && hasLogToResponse_ == other.hasLogToResponse_
        && usePbResolution_ == other.usePbResolution_
        && hasUsePbResolution_ == other.hasUsePbResolution_
        && minimizeReductionDuringPbResolution_ == other.minimizeReductionDuringPbResolution_
        && hasMinimizeReductionDuringPbResolution_ == other.hasMinimizeReductionDuringPbResolution_
        && countAssumptionLevelsInLbd_ == other.countAssumptionLevelsInLbd_
        && hasCountAssumptionLevelsInLbd_ == other.hasCountAssumptionLevelsInLbd_
        && presolveBveThreshold_ == other.presolveBveThreshold_
        && hasPresolveBveThreshold_ == other.hasPresolveBveThreshold_
        && filterSatPostsolveClauses_ == other.filterSatPostsolveClauses_
        && hasFilterSatPostsolveClauses_ == other.hasFilterSatPostsolveClauses_
        && presolveBveClauseWeight_ == other.presolveBveClauseWeight_
        && hasPresolveBveClauseWeight_ == other.hasPresolveBveClauseWeight_
        && Double.compare(probingDeterministicTimeLimit_, other.probingDeterministicTimeLimit_) == 0
        && hasProbingDeterministicTimeLimit_ == other.hasProbingDeterministicTimeLimit_
        && Double.compare(presolveProbingDeterministicTimeLimit_, other.presolveProbingDeterministicTimeLimit_) == 0
        && hasPresolveProbingDeterministicTimeLimit_ == other.hasPresolveProbingDeterministicTimeLimit_
        && presolveBlockedClause_ == other.presolveBlockedClause_
        && hasPresolveBlockedClause_ == other.hasPresolveBlockedClause_
        && presolveUseBva_ == other.presolveUseBva_
        && hasPresolveUseBva_ == other.hasPresolveUseBva_
        && presolveBvaThreshold_ == other.presolveBvaThreshold_
        && hasPresolveBvaThreshold_ == other.hasPresolveBvaThreshold_
        && maxPresolveIterations_ == other.maxPresolveIterations_
        && hasMaxPresolveIterations_ == other.hasMaxPresolveIterations_
        && cpModelPresolve_ == other.cpModelPresolve_
        && hasCpModelPresolve_ == other.hasCpModelPresolve_
        && cpModelDirectSolve_ == other.cpModelDirectSolve_
        && hasCpModelDirectSolve_ == other.hasCpModelDirectSolve_
        && cpModelProbingLevel_ == other.cpModelProbingLevel_
        && hasCpModelProbingLevel_ == other.hasCpModelProbingLevel_
        && cpModelUseSatPresolve_ == other.cpModelUseSatPresolve_
        && hasCpModelUseSatPresolve_ == other.hasCpModelUseSatPresolve_
        && cpModelPureSatPresolve_ == other.cpModelPureSatPresolve_
        && hasCpModelPureSatPresolve_ == other.hasCpModelPureSatPresolve_
        && loadAtMostOnesInSatPresolve_ == other.loadAtMostOnesInSatPresolve_
        && hasLoadAtMostOnesInSatPresolve_ == other.hasLoadAtMostOnesInSatPresolve_
        && removeFixedVariablesEarly_ == other.removeFixedVariablesEarly_
        && hasRemoveFixedVariablesEarly_ == other.hasRemoveFixedVariablesEarly_
        && detectTableWithCost_ == other.detectTableWithCost_
        && hasDetectTableWithCost_ == other.hasDetectTableWithCost_
        && tableCompressionLevel_ == other.tableCompressionLevel_
        && hasTableCompressionLevel_ == other.hasTableCompressionLevel_
        && expandAlldiffConstraints_ == other.expandAlldiffConstraints_
        && hasExpandAlldiffConstraints_ == other.hasExpandAlldiffConstraints_
        && maxAlldiffDomainSize_ == other.maxAlldiffDomainSize_
        && hasMaxAlldiffDomainSize_ == other.hasMaxAlldiffDomainSize_
        && expandReservoirConstraints_ == other.expandReservoirConstraints_
        && hasExpandReservoirConstraints_ == other.hasExpandReservoirConstraints_
        && maxDomainSizeForLinear2Expansion_ == other.maxDomainSizeForLinear2Expansion_
        && hasMaxDomainSizeForLinear2Expansion_ == other.hasMaxDomainSizeForLinear2Expansion_
        && expandReservoirUsingCircuit_ == other.expandReservoirUsingCircuit_
        && hasExpandReservoirUsingCircuit_ == other.hasExpandReservoirUsingCircuit_
        && encodeCumulativeAsReservoir_ == other.encodeCumulativeAsReservoir_
        && hasEncodeCumulativeAsReservoir_ == other.hasEncodeCumulativeAsReservoir_
        && maxLinMaxSizeForExpansion_ == other.maxLinMaxSizeForExpansion_
        && hasMaxLinMaxSizeForExpansion_ == other.hasMaxLinMaxSizeForExpansion_
        && disableConstraintExpansion_ == other.disableConstraintExpansion_
        && hasDisableConstraintExpansion_ == other.hasDisableConstraintExpansion_
        && encodeComplexLinearConstraintWithInteger_ == other.encodeComplexLinearConstraintWithInteger_
        && hasEncodeComplexLinearConstraintWithInteger_ == other.hasEncodeComplexLinearConstraintWithInteger_
        && detectEncodedComplexDomain_ == other.detectEncodedComplexDomain_
        && hasDetectEncodedComplexDomain_ == other.hasDetectEncodedComplexDomain_
        && Double.compare(mergeNoOverlapWorkLimit_, other.mergeNoOverlapWorkLimit_) == 0
        && hasMergeNoOverlapWorkLimit_ == other.hasMergeNoOverlapWorkLimit_
        && Double.compare(mergeAtMostOneWorkLimit_, other.mergeAtMostOneWorkLimit_) == 0
        && hasMergeAtMostOneWorkLimit_ == other.hasMergeAtMostOneWorkLimit_
        && presolveSubstitutionLevel_ == other.presolveSubstitutionLevel_
        && hasPresolveSubstitutionLevel_ == other.hasPresolveSubstitutionLevel_
        && presolveExtractIntegerEnforcement_ == other.presolveExtractIntegerEnforcement_
        && hasPresolveExtractIntegerEnforcement_ == other.hasPresolveExtractIntegerEnforcement_
        && presolveInclusionWorkLimit_ == other.presolveInclusionWorkLimit_
        && hasPresolveInclusionWorkLimit_ == other.hasPresolveInclusionWorkLimit_
        && ignoreNames_ == other.ignoreNames_
        && hasIgnoreNames_ == other.hasIgnoreNames_
        && inferAllDiffs_ == other.inferAllDiffs_
        && hasInferAllDiffs_ == other.hasInferAllDiffs_
        && findBigLinearOverlap_ == other.findBigLinearOverlap_
        && hasFindBigLinearOverlap_ == other.hasFindBigLinearOverlap_
        && findClausesThatAreExactlyOne_ == other.findClausesThatAreExactlyOne_
        && hasFindClausesThatAreExactlyOne_ == other.hasFindClausesThatAreExactlyOne_
        && useSatInprocessing_ == other.useSatInprocessing_
        && hasUseSatInprocessing_ == other.hasUseSatInprocessing_
        && Double.compare(inprocessingDtimeRatio_, other.inprocessingDtimeRatio_) == 0
        && hasInprocessingDtimeRatio_ == other.hasInprocessingDtimeRatio_
        && Double.compare(inprocessingProbingDtime_, other.inprocessingProbingDtime_) == 0
        && hasInprocessingProbingDtime_ == other.hasInprocessingProbingDtime_
        && Double.compare(inprocessingMinimizationDtime_, other.inprocessingMinimizationDtime_) == 0
        && hasInprocessingMinimizationDtime_ == other.hasInprocessingMinimizationDtime_
        && inprocessingMinimizationUseConflictAnalysis_ == other.inprocessingMinimizationUseConflictAnalysis_
        && hasInprocessingMinimizationUseConflictAnalysis_ == other.hasInprocessingMinimizationUseConflictAnalysis_
        && numOrderingInVivification_ == other.numOrderingInVivification_
        && hasNumOrderingInVivification_ == other.hasNumOrderingInVivification_
        && inprocessingUseCongruenceClosure_ == other.inprocessingUseCongruenceClosure_
        && hasInprocessingUseCongruenceClosure_ == other.hasInprocessingUseCongruenceClosure_
        && inprocessingDetectAndSweepCircuit_ == other.inprocessingDetectAndSweepCircuit_
        && hasInprocessingDetectAndSweepCircuit_ == other.hasInprocessingDetectAndSweepCircuit_
        && inprocessingUseSatSweeping_ == other.inprocessingUseSatSweeping_
        && hasInprocessingUseSatSweeping_ == other.hasInprocessingUseSatSweeping_
        && inprocessingUseTransitiveReduction_ == other.inprocessingUseTransitiveReduction_
        && hasInprocessingUseTransitiveReduction_ == other.hasInprocessingUseTransitiveReduction_
        && numWorkers_ == other.numWorkers_
        && hasNumWorkers_ == other.hasNumWorkers_
        && numSearchWorkers_ == other.numSearchWorkers_
        && hasNumSearchWorkers_ == other.hasNumSearchWorkers_
        && numFullSubsolvers_ == other.numFullSubsolvers_
        && hasNumFullSubsolvers_ == other.hasNumFullSubsolvers_
        && getSubsolversList().equals(other.getSubsolversList())
        && getExtraSubsolversList().equals(other.getExtraSubsolversList())
        && getIgnoreSubsolversList().equals(other.getIgnoreSubsolversList())
        && getFilterSubsolversList().equals(other.getFilterSubsolversList())
        && getSubsolverParamsList().equals(other.getSubsolverParamsList())
        && interleaveSearch_ == other.interleaveSearch_
        && hasInterleaveSearch_ == other.hasInterleaveSearch_
        && interleaveBatchSize_ == other.interleaveBatchSize_
        && hasInterleaveBatchSize_ == other.hasInterleaveBatchSize_
        && shareObjectiveBounds_ == other.shareObjectiveBounds_
        && hasShareObjectiveBounds_ == other.hasShareObjectiveBounds_
        && shareLevelZeroBounds_ == other.shareLevelZeroBounds_
        && hasShareLevelZeroBounds_ == other.hasShareLevelZeroBounds_
        && shareLinear2Bounds_ == other.shareLinear2Bounds_
        && hasShareLinear2Bounds_ == other.hasShareLinear2Bounds_
        && shareBinaryClauses_ == other.shareBinaryClauses_
        && hasShareBinaryClauses_ == other.hasShareBinaryClauses_
        && shareGlueClauses_ == other.shareGlueClauses_
        && hasShareGlueClauses_ == other.hasShareGlueClauses_
        && minimizeSharedClauses_ == other.minimizeSharedClauses_
        && hasMinimizeSharedClauses_ == other.hasMinimizeSharedClauses_
        && Double.compare(shareGlueClausesDtime_, other.shareGlueClausesDtime_) == 0
        && hasShareGlueClausesDtime_ == other.hasShareGlueClausesDtime_
        && checkLratProof_ == other.checkLratProof_
        && hasCheckLratProof_ == other.hasCheckLratProof_
        && checkMergedLratProof_ == other.checkMergedLratProof_
        && hasCheckMergedLratProof_ == other.hasCheckMergedLratProof_
        && outputLratProof_ == other.outputLratProof_
        && hasOutputLratProof_ == other.hasOutputLratProof_
        && debugPostsolveWithFullSolver_ == other.debugPostsolveWithFullSolver_
        && hasDebugPostsolveWithFullSolver_ == other.hasDebugPostsolveWithFullSolver_
        && debugMaxNumPresolveOperations_ == other.debugMaxNumPresolveOperations_
        && hasDebugMaxNumPresolveOperations_ == other.hasDebugMaxNumPresolveOperations_
        && debugCrashOnBadHint_ == other.debugCrashOnBadHint_
        && hasDebugCrashOnBadHint_ == other.hasDebugCrashOnBadHint_
        && debugCrashIfPresolveBreaksHint_ == other.debugCrashIfPresolveBreaksHint_
        && hasDebugCrashIfPresolveBreaksHint_ == other.hasDebugCrashIfPresolveBreaksHint_
        && debugCrashIfLratCheckFails_ == other.debugCrashIfLratCheckFails_
        && hasDebugCrashIfLratCheckFails_ == other.hasDebugCrashIfLratCheckFails_
        && useOptimizationHints_ == other.useOptimizationHints_
        && hasUseOptimizationHints_ == other.hasUseOptimizationHints_
        && coreMinimizationLevel_ == other.coreMinimizationLevel_
        && hasCoreMinimizationLevel_ == other.hasCoreMinimizationLevel_
        && findMultipleCores_ == other.findMultipleCores_
        && hasFindMultipleCores_ == other.hasFindMultipleCores_
        && coverOptimization_ == other.coverOptimization_
        && hasCoverOptimization_ == other.hasCoverOptimization_
        && maxSatAssumptionOrder_ == other.maxSatAssumptionOrder_
        && hasMaxSatAssumptionOrder_ == other.hasMaxSatAssumptionOrder_
        && maxSatReverseAssumptionOrder_ == other.maxSatReverseAssumptionOrder_
        && hasMaxSatReverseAssumptionOrder_ == other.hasMaxSatReverseAssumptionOrder_
        && maxSatStratification_ == other.maxSatStratification_
        && hasMaxSatStratification_ == other.hasMaxSatStratification_
        && Double.compare(propagationLoopDetectionFactor_, other.propagationLoopDetectionFactor_) == 0
        && hasPropagationLoopDetectionFactor_ == other.hasPropagationLoopDetectionFactor_
        && usePrecedencesInDisjunctiveConstraint_ == other.usePrecedencesInDisjunctiveConstraint_
        && hasUsePrecedencesInDisjunctiveConstraint_ == other.hasUsePrecedencesInDisjunctiveConstraint_
        && transitivePrecedencesWorkLimit_ == other.transitivePrecedencesWorkLimit_
        && hasTransitivePrecedencesWorkLimit_ == other.hasTransitivePrecedencesWorkLimit_
        && maxSizeToCreatePrecedenceLiteralsInDisjunctive_ == other.maxSizeToCreatePrecedenceLiteralsInDisjunctive_
        && hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_ == other.hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_
        && useStrongPropagationInDisjunctive_ == other.useStrongPropagationInDisjunctive_
        && hasUseStrongPropagationInDisjunctive_ == other.hasUseStrongPropagationInDisjunctive_
        && useDynamicPrecedenceInDisjunctive_ == other.useDynamicPrecedenceInDisjunctive_
        && hasUseDynamicPrecedenceInDisjunctive_ == other.hasUseDynamicPrecedenceInDisjunctive_
        && useDynamicPrecedenceInCumulative_ == other.useDynamicPrecedenceInCumulative_
        && hasUseDynamicPrecedenceInCumulative_ == other.hasUseDynamicPrecedenceInCumulative_
        && useOverloadCheckerInCumulative_ == other.useOverloadCheckerInCumulative_
        && hasUseOverloadCheckerInCumulative_ == other.hasUseOverloadCheckerInCumulative_
        && useConservativeScaleOverloadChecker_ == other.useConservativeScaleOverloadChecker_
        && hasUseConservativeScaleOverloadChecker_ == other.hasUseConservativeScaleOverloadChecker_
        && useTimetableEdgeFindingInCumulative_ == other.useTimetableEdgeFindingInCumulative_
        && hasUseTimetableEdgeFindingInCumulative_ == other.hasUseTimetableEdgeFindingInCumulative_
        && maxNumIntervalsForTimetableEdgeFinding_ == other.maxNumIntervalsForTimetableEdgeFinding_
        && hasMaxNumIntervalsForTimetableEdgeFinding_ == other.hasMaxNumIntervalsForTimetableEdgeFinding_
        && useHardPrecedencesInCumulative_ == other.useHardPrecedencesInCumulative_
        && hasUseHardPrecedencesInCumulative_ == other.hasUseHardPrecedencesInCumulative_
        && exploitAllPrecedences_ == other.exploitAllPrecedences_
        && hasExploitAllPrecedences_ == other.hasExploitAllPrecedences_
        && useDisjunctiveConstraintInCumulative_ == other.useDisjunctiveConstraintInCumulative_
        && hasUseDisjunctiveConstraintInCumulative_ == other.hasUseDisjunctiveConstraintInCumulative_
        && noOverlap2DBooleanRelationsLimit_ == other.noOverlap2DBooleanRelationsLimit_
        && hasNoOverlap2DBooleanRelationsLimit_ == other.hasNoOverlap2DBooleanRelationsLimit_
        && useTimetablingInNoOverlap2D_ == other.useTimetablingInNoOverlap2D_
        && hasUseTimetablingInNoOverlap2D_ == other.hasUseTimetablingInNoOverlap2D_
        && useEnergeticReasoningInNoOverlap2D_ == other.useEnergeticReasoningInNoOverlap2D_
        && hasUseEnergeticReasoningInNoOverlap2D_ == other.hasUseEnergeticReasoningInNoOverlap2D_
        && useAreaEnergeticReasoningInNoOverlap2D_ == other.useAreaEnergeticReasoningInNoOverlap2D_
        && hasUseAreaEnergeticReasoningInNoOverlap2D_ == other.hasUseAreaEnergeticReasoningInNoOverlap2D_
        && useTryEdgeReasoningInNoOverlap2D_ == other.useTryEdgeReasoningInNoOverlap2D_
        && hasUseTryEdgeReasoningInNoOverlap2D_ == other.hasUseTryEdgeReasoningInNoOverlap2D_
        && maxPairsPairwiseReasoningInNoOverlap2D_ == other.maxPairsPairwiseReasoningInNoOverlap2D_
        && hasMaxPairsPairwiseReasoningInNoOverlap2D_ == other.hasMaxPairsPairwiseReasoningInNoOverlap2D_
        && maximumRegionsToSplitInDisconnectedNoOverlap2D_ == other.maximumRegionsToSplitInDisconnectedNoOverlap2D_
        && hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_ == other.hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_
        && useLinear3ForNoOverlap2DPrecedences_ == other.useLinear3ForNoOverlap2DPrecedences_
        && hasUseLinear3ForNoOverlap2DPrecedences_ == other.hasUseLinear3ForNoOverlap2DPrecedences_
        && useDualSchedulingHeuristics_ == other.useDualSchedulingHeuristics_
        && hasUseDualSchedulingHeuristics_ == other.hasUseDualSchedulingHeuristics_
        && useAllDifferentForCircuit_ == other.useAllDifferentForCircuit_
        && hasUseAllDifferentForCircuit_ == other.hasUseAllDifferentForCircuit_
        && routingCutSubsetSizeForBinaryRelationBound_ == other.routingCutSubsetSizeForBinaryRelationBound_
        && hasRoutingCutSubsetSizeForBinaryRelationBound_ == other.hasRoutingCutSubsetSizeForBinaryRelationBound_
        && routingCutSubsetSizeForTightBinaryRelationBound_ == other.routingCutSubsetSizeForTightBinaryRelationBound_
        && hasRoutingCutSubsetSizeForTightBinaryRelationBound_ == other.hasRoutingCutSubsetSizeForTightBinaryRelationBound_
        && routingCutSubsetSizeForExactBinaryRelationBound_ == other.routingCutSubsetSizeForExactBinaryRelationBound_
        && hasRoutingCutSubsetSizeForExactBinaryRelationBound_ == other.hasRoutingCutSubsetSizeForExactBinaryRelationBound_
        && routingCutSubsetSizeForShortestPathsBound_ == other.routingCutSubsetSizeForShortestPathsBound_
        && hasRoutingCutSubsetSizeForShortestPathsBound_ == other.hasRoutingCutSubsetSizeForShortestPathsBound_
        && Double.compare(routingCutDpEffort_, other.routingCutDpEffort_) == 0
        && hasRoutingCutDpEffort_ == other.hasRoutingCutDpEffort_
        && routingCutMaxInfeasiblePathLength_ == other.routingCutMaxInfeasiblePathLength_
        && hasRoutingCutMaxInfeasiblePathLength_ == other.hasRoutingCutMaxInfeasiblePathLength_
        && searchBranching_ == other.searchBranching_
        && hasSearchBranching_ == other.hasSearchBranching_
        && hintConflictLimit_ == other.hintConflictLimit_
        && hasHintConflictLimit_ == other.hasHintConflictLimit_
        && tryHintAsAssumptions_ == other.tryHintAsAssumptions_
        && hasTryHintAsAssumptions_ == other.hasTryHintAsAssumptions_
        && repairHint_ == other.repairHint_
        && hasRepairHint_ == other.hasRepairHint_
        && fixVariablesToTheirHintedValue_ == other.fixVariablesToTheirHintedValue_
        && hasFixVariablesToTheirHintedValue_ == other.hasFixVariablesToTheirHintedValue_
        && useProbingSearch_ == other.useProbingSearch_
        && hasUseProbingSearch_ == other.hasUseProbingSearch_
        && useExtendedProbing_ == other.useExtendedProbing_
        && hasUseExtendedProbing_ == other.hasUseExtendedProbing_
        && probingNumCombinationsLimit_ == other.probingNumCombinationsLimit_
        && hasProbingNumCombinationsLimit_ == other.hasProbingNumCombinationsLimit_
        && Double.compare(shavingDeterministicTimeInProbingSearch_, other.shavingDeterministicTimeInProbingSearch_) == 0
        && hasShavingDeterministicTimeInProbingSearch_ == other.hasShavingDeterministicTimeInProbingSearch_
        && Double.compare(shavingSearchDeterministicTime_, other.shavingSearchDeterministicTime_) == 0
        && hasShavingSearchDeterministicTime_ == other.hasShavingSearchDeterministicTime_
        && shavingSearchUseRandomSearch_ == other.shavingSearchUseRandomSearch_
        && hasShavingSearchUseRandomSearch_ == other.hasShavingSearchUseRandomSearch_
        && shavingSearchRestartLimit_ == other.shavingSearchRestartLimit_
        && hasShavingSearchRestartLimit_ == other.hasShavingSearchRestartLimit_
        && shavingSearchThreshold_ == other.shavingSearchThreshold_
        && hasShavingSearchThreshold_ == other.hasShavingSearchThreshold_
        && objectiveShavingSearchThreshold_ == other.objectiveShavingSearchThreshold_
        && hasObjectiveShavingSearchThreshold_ == other.hasObjectiveShavingSearchThreshold_
        && useObjectiveLbSearch_ == other.useObjectiveLbSearch_
        && hasUseObjectiveLbSearch_ == other.hasUseObjectiveLbSearch_
        && useObjectiveShavingSearch_ == other.useObjectiveShavingSearch_
        && hasUseObjectiveShavingSearch_ == other.hasUseObjectiveShavingSearch_
        && variablesShavingLevel_ == other.variablesShavingLevel_
        && hasVariablesShavingLevel_ == other.hasVariablesShavingLevel_
        && pseudoCostReliabilityThreshold_ == other.pseudoCostReliabilityThreshold_
        && hasPseudoCostReliabilityThreshold_ == other.hasPseudoCostReliabilityThreshold_
        && optimizeWithCore_ == other.optimizeWithCore_
        && hasOptimizeWithCore_ == other.hasOptimizeWithCore_
        && optimizeWithLbTreeSearch_ == other.optimizeWithLbTreeSearch_
        && hasOptimizeWithLbTreeSearch_ == other.hasOptimizeWithLbTreeSearch_
        && saveLpBasisInLbTreeSearch_ == other.saveLpBasisInLbTreeSearch_
        && hasSaveLpBasisInLbTreeSearch_ == other.hasSaveLpBasisInLbTreeSearch_
        && binarySearchNumConflicts_ == other.binarySearchNumConflicts_
        && hasBinarySearchNumConflicts_ == other.hasBinarySearchNumConflicts_
        && optimizeWithMaxHs_ == other.optimizeWithMaxHs_
        && hasOptimizeWithMaxHs_ == other.hasOptimizeWithMaxHs_
        && useFeasibilityJump_ == other.useFeasibilityJump_
        && hasUseFeasibilityJump_ == other.hasUseFeasibilityJump_
        && useLsOnly_ == other.useLsOnly_
        && hasUseLsOnly_ == other.hasUseLsOnly_
        && Double.compare(feasibilityJumpDecay_, other.feasibilityJumpDecay_) == 0
        && hasFeasibilityJumpDecay_ == other.hasFeasibilityJumpDecay_
        && feasibilityJumpLinearizationLevel_ == other.feasibilityJumpLinearizationLevel_
        && hasFeasibilityJumpLinearizationLevel_ == other.hasFeasibilityJumpLinearizationLevel_
        && feasibilityJumpRestartFactor_ == other.feasibilityJumpRestartFactor_
        && hasFeasibilityJumpRestartFactor_ == other.hasFeasibilityJumpRestartFactor_
        && Double.compare(feasibilityJumpBatchDtime_, other.feasibilityJumpBatchDtime_) == 0
        && hasFeasibilityJumpBatchDtime_ == other.hasFeasibilityJumpBatchDtime_
        && Double.compare(feasibilityJumpVarRandomizationProbability_, other.feasibilityJumpVarRandomizationProbability_) == 0
        && hasFeasibilityJumpVarRandomizationProbability_ == other.hasFeasibilityJumpVarRandomizationProbability_
        && Double.compare(feasibilityJumpVarPerburbationRangeRatio_, other.feasibilityJumpVarPerburbationRangeRatio_) == 0
        && hasFeasibilityJumpVarPerburbationRangeRatio_ == other.hasFeasibilityJumpVarPerburbationRangeRatio_
        && feasibilityJumpEnableRestarts_ == other.feasibilityJumpEnableRestarts_
        && hasFeasibilityJumpEnableRestarts_ == other.hasFeasibilityJumpEnableRestarts_
        && feasibilityJumpMaxExpandedConstraintSize_ == other.feasibilityJumpMaxExpandedConstraintSize_
        && hasFeasibilityJumpMaxExpandedConstraintSize_ == other.hasFeasibilityJumpMaxExpandedConstraintSize_
        && feasibilityJumpIgnoreRedundantConstraints_ == other.feasibilityJumpIgnoreRedundantConstraints_
        && hasFeasibilityJumpIgnoreRedundantConstraints_ == other.hasFeasibilityJumpIgnoreRedundantConstraints_
        && lnsIgnoreRedundantConstraints_ == other.lnsIgnoreRedundantConstraints_
        && hasLnsIgnoreRedundantConstraints_ == other.hasLnsIgnoreRedundantConstraints_
        && numViolationLs_ == other.numViolationLs_
        && hasNumViolationLs_ == other.hasNumViolationLs_
        && violationLsPerturbationPeriod_ == other.violationLsPerturbationPeriod_
        && hasViolationLsPerturbationPeriod_ == other.hasViolationLsPerturbationPeriod_
        && sharedTreeNumWorkers_ == other.sharedTreeNumWorkers_
        && hasSharedTreeNumWorkers_ == other.hasSharedTreeNumWorkers_
        && useSharedTreeSearch_ == other.useSharedTreeSearch_
        && hasUseSharedTreeSearch_ == other.hasUseSharedTreeSearch_
        && sharedTreeWorkerMinRestartsPerSubtree_ == other.sharedTreeWorkerMinRestartsPerSubtree_
        && hasSharedTreeWorkerMinRestartsPerSubtree_ == other.hasSharedTreeWorkerMinRestartsPerSubtree_
        && sharedTreeWorkerEnableTrailSharing_ == other.sharedTreeWorkerEnableTrailSharing_
        && hasSharedTreeWorkerEnableTrailSharing_ == other.hasSharedTreeWorkerEnableTrailSharing_
        && sharedTreeWorkerEnablePhaseSharing_ == other.sharedTreeWorkerEnablePhaseSharing_
        && hasSharedTreeWorkerEnablePhaseSharing_ == other.hasSharedTreeWorkerEnablePhaseSharing_
        && sharedTreeWorkerPropagateIntermediateNodes_ == other.sharedTreeWorkerPropagateIntermediateNodes_
        && hasSharedTreeWorkerPropagateIntermediateNodes_ == other.hasSharedTreeWorkerPropagateIntermediateNodes_
        && Double.compare(sharedTreeOpenLeavesPerWorker_, other.sharedTreeOpenLeavesPerWorker_) == 0
        && hasSharedTreeOpenLeavesPerWorker_ == other.hasSharedTreeOpenLeavesPerWorker_
        && sharedTreeMaxNodesPerWorker_ == other.sharedTreeMaxNodesPerWorker_
        && hasSharedTreeMaxNodesPerWorker_ == other.hasSharedTreeMaxNodesPerWorker_
        && sharedTreeSplitStrategy_ == other.sharedTreeSplitStrategy_
        && hasSharedTreeSplitStrategy_ == other.hasSharedTreeSplitStrategy_
        && sharedTreeBalanceTolerance_ == other.sharedTreeBalanceTolerance_
        && hasSharedTreeBalanceTolerance_ == other.hasSharedTreeBalanceTolerance_
        && Double.compare(sharedTreeSplitMinDtime_, other.sharedTreeSplitMinDtime_) == 0
        && hasSharedTreeSplitMinDtime_ == other.hasSharedTreeSplitMinDtime_
        && enumerateAllSolutions_ == other.enumerateAllSolutions_
        && hasEnumerateAllSolutions_ == other.hasEnumerateAllSolutions_
        && keepAllFeasibleSolutionsInPresolve_ == other.keepAllFeasibleSolutionsInPresolve_
        && hasKeepAllFeasibleSolutionsInPresolve_ == other.hasKeepAllFeasibleSolutionsInPresolve_
        && fillTightenedDomainsInResponse_ == other.fillTightenedDomainsInResponse_
        && hasFillTightenedDomainsInResponse_ == other.hasFillTightenedDomainsInResponse_
        && fillAdditionalSolutionsInResponse_ == other.fillAdditionalSolutionsInResponse_
        && hasFillAdditionalSolutionsInResponse_ == other.hasFillAdditionalSolutionsInResponse_
        && instantiateAllVariables_ == other.instantiateAllVariables_
        && hasInstantiateAllVariables_ == other.hasInstantiateAllVariables_
        && autoDetectGreaterThanAtLeastOneOf_ == other.autoDetectGreaterThanAtLeastOneOf_
        && hasAutoDetectGreaterThanAtLeastOneOf_ == other.hasAutoDetectGreaterThanAtLeastOneOf_
        && stopAfterFirstSolution_ == other.stopAfterFirstSolution_
        && hasStopAfterFirstSolution_ == other.hasStopAfterFirstSolution_
        && stopAfterPresolve_ == other.stopAfterPresolve_
        && hasStopAfterPresolve_ == other.hasStopAfterPresolve_
        && stopAfterRootPropagation_ == other.stopAfterRootPropagation_
        && hasStopAfterRootPropagation_ == other.hasStopAfterRootPropagation_
        && Double.compare(lnsInitialDifficulty_, other.lnsInitialDifficulty_) == 0
        && hasLnsInitialDifficulty_ == other.hasLnsInitialDifficulty_
        && Double.compare(lnsInitialDeterministicLimit_, other.lnsInitialDeterministicLimit_) == 0
        && hasLnsInitialDeterministicLimit_ == other.hasLnsInitialDeterministicLimit_
        && useLns_ == other.useLns_
        && hasUseLns_ == other.hasUseLns_
        && useLnsOnly_ == other.useLnsOnly_
        && hasUseLnsOnly_ == other.hasUseLnsOnly_
        && solutionPoolSize_ == other.solutionPoolSize_
        && hasSolutionPoolSize_ == other.hasSolutionPoolSize_
        && solutionPoolDiversityLimit_ == other.solutionPoolDiversityLimit_
        && hasSolutionPoolDiversityLimit_ == other.hasSolutionPoolDiversityLimit_
        && alternativePoolSize_ == other.alternativePoolSize_
        && hasAlternativePoolSize_ == other.hasAlternativePoolSize_
        && useRinsLns_ == other.useRinsLns_
        && hasUseRinsLns_ == other.hasUseRinsLns_
        && useFeasibilityPump_ == other.useFeasibilityPump_
        && hasUseFeasibilityPump_ == other.hasUseFeasibilityPump_
        && useLbRelaxLns_ == other.useLbRelaxLns_
        && hasUseLbRelaxLns_ == other.hasUseLbRelaxLns_
        && lbRelaxNumWorkersThreshold_ == other.lbRelaxNumWorkersThreshold_
        && hasLbRelaxNumWorkersThreshold_ == other.hasLbRelaxNumWorkersThreshold_
        && fpRounding_ == other.fpRounding_
        && hasFpRounding_ == other.hasFpRounding_
        && diversifyLnsParams_ == other.diversifyLnsParams_
        && hasDiversifyLnsParams_ == other.hasDiversifyLnsParams_
        && randomizeSearch_ == other.randomizeSearch_
        && hasRandomizeSearch_ == other.hasRandomizeSearch_
        && searchRandomVariablePoolSize_ == other.searchRandomVariablePoolSize_
        && hasSearchRandomVariablePoolSize_ == other.hasSearchRandomVariablePoolSize_
        && pushAllTasksTowardStart_ == other.pushAllTasksTowardStart_
        && hasPushAllTasksTowardStart_ == other.hasPushAllTasksTowardStart_
        && useOptionalVariables_ == other.useOptionalVariables_
        && hasUseOptionalVariables_ == other.hasUseOptionalVariables_
        && useExactLpReason_ == other.useExactLpReason_
        && hasUseExactLpReason_ == other.hasUseExactLpReason_
        && useCombinedNoOverlap_ == other.useCombinedNoOverlap_
        && hasUseCombinedNoOverlap_ == other.hasUseCombinedNoOverlap_
        && atMostOneMaxExpansionSize_ == other.atMostOneMaxExpansionSize_
        && hasAtMostOneMaxExpansionSize_ == other.hasAtMostOneMaxExpansionSize_
        && catchSigintSignal_ == other.catchSigintSignal_
        && hasCatchSigintSignal_ == other.hasCatchSigintSignal_
        && useImpliedBounds_ == other.useImpliedBounds_
        && hasUseImpliedBounds_ == other.hasUseImpliedBounds_
        && polishLpSolution_ == other.polishLpSolution_
        && hasPolishLpSolution_ == other.hasPolishLpSolution_
        && Double.compare(lpPrimalTolerance_, other.lpPrimalTolerance_) == 0
        && hasLpPrimalTolerance_ == other.hasLpPrimalTolerance_
        && Double.compare(lpDualTolerance_, other.lpDualTolerance_) == 0
        && hasLpDualTolerance_ == other.hasLpDualTolerance_
        && convertIntervals_ == other.convertIntervals_
        && hasConvertIntervals_ == other.hasConvertIntervals_
        && symmetryLevel_ == other.symmetryLevel_
        && hasSymmetryLevel_ == other.hasSymmetryLevel_
        && useSymmetryInLp_ == other.useSymmetryInLp_
        && hasUseSymmetryInLp_ == other.hasUseSymmetryInLp_
        && keepSymmetryInPresolve_ == other.keepSymmetryInPresolve_
        && hasKeepSymmetryInPresolve_ == other.hasKeepSymmetryInPresolve_
        && Double.compare(symmetryDetectionDeterministicTimeLimit_, other.symmetryDetectionDeterministicTimeLimit_) == 0
        && hasSymmetryDetectionDeterministicTimeLimit_ == other.hasSymmetryDetectionDeterministicTimeLimit_
        && newLinearPropagation_ == other.newLinearPropagation_
        && hasNewLinearPropagation_ == other.hasNewLinearPropagation_
        && linearSplitSize_ == other.linearSplitSize_
        && hasLinearSplitSize_ == other.hasLinearSplitSize_
        && linearizationLevel_ == other.linearizationLevel_
        && hasLinearizationLevel_ == other.hasLinearizationLevel_
        && booleanEncodingLevel_ == other.booleanEncodingLevel_
        && hasBooleanEncodingLevel_ == other.hasBooleanEncodingLevel_
        && maxDomainSizeWhenEncodingEqNeqConstraints_ == other.maxDomainSizeWhenEncodingEqNeqConstraints_
        && hasMaxDomainSizeWhenEncodingEqNeqConstraints_ == other.hasMaxDomainSizeWhenEncodingEqNeqConstraints_
        && maxNumCuts_ == other.maxNumCuts_
        && hasMaxNumCuts_ == other.hasMaxNumCuts_
        && cutLevel_ == other.cutLevel_
        && hasCutLevel_ == other.hasCutLevel_
        && onlyAddCutsAtLevelZero_ == other.onlyAddCutsAtLevelZero_
        && hasOnlyAddCutsAtLevelZero_ == other.hasOnlyAddCutsAtLevelZero_
        && addObjectiveCut_ == other.addObjectiveCut_
        && hasAddObjectiveCut_ == other.hasAddObjectiveCut_
        && addCgCuts_ == other.addCgCuts_
        && hasAddCgCuts_ == other.hasAddCgCuts_
        && addMirCuts_ == other.addMirCuts_
        && hasAddMirCuts_ == other.hasAddMirCuts_
        && addZeroHalfCuts_ == other.addZeroHalfCuts_
        && hasAddZeroHalfCuts_ == other.hasAddZeroHalfCuts_
        && addCliqueCuts_ == other.addCliqueCuts_
        && hasAddCliqueCuts_ == other.hasAddCliqueCuts_
        && addRltCuts_ == other.addRltCuts_
        && hasAddRltCuts_ == other.hasAddRltCuts_
        && maxAllDiffCutSize_ == other.maxAllDiffCutSize_
        && hasMaxAllDiffCutSize_ == other.hasMaxAllDiffCutSize_
        && addLinMaxCuts_ == other.addLinMaxCuts_
        && hasAddLinMaxCuts_ == other.hasAddLinMaxCuts_
        && maxIntegerRoundingScaling_ == other.maxIntegerRoundingScaling_
        && hasMaxIntegerRoundingScaling_ == other.hasMaxIntegerRoundingScaling_
        && addLpConstraintsLazily_ == other.addLpConstraintsLazily_
        && hasAddLpConstraintsLazily_ == other.hasAddLpConstraintsLazily_
        && rootLpIterations_ == other.rootLpIterations_
        && hasRootLpIterations_ == other.hasRootLpIterations_
        && Double.compare(minOrthogonalityForLpConstraints_, other.minOrthogonalityForLpConstraints_) == 0
        && hasMinOrthogonalityForLpConstraints_ == other.hasMinOrthogonalityForLpConstraints_
        && maxCutRoundsAtLevelZero_ == other.maxCutRoundsAtLevelZero_
        && hasMaxCutRoundsAtLevelZero_ == other.hasMaxCutRoundsAtLevelZero_
        && maxConsecutiveInactiveCount_ == other.maxConsecutiveInactiveCount_
        && hasMaxConsecutiveInactiveCount_ == other.hasMaxConsecutiveInactiveCount_
        && Double.compare(cutMaxActiveCountValue_, other.cutMaxActiveCountValue_) == 0
        && hasCutMaxActiveCountValue_ == other.hasCutMaxActiveCountValue_
        && Double.compare(cutActiveCountDecay_, other.cutActiveCountDecay_) == 0
        && hasCutActiveCountDecay_ == other.hasCutActiveCountDecay_
        && cutCleanupTarget_ == other.cutCleanupTarget_
        && hasCutCleanupTarget_ == other.hasCutCleanupTarget_
        && newConstraintsBatchSize_ == other.newConstraintsBatchSize_
        && hasNewConstraintsBatchSize_ == other.hasNewConstraintsBatchSize_
        && exploitIntegerLpSolution_ == other.exploitIntegerLpSolution_
        && hasExploitIntegerLpSolution_ == other.hasExploitIntegerLpSolution_
        && exploitAllLpSolution_ == other.exploitAllLpSolution_
        && hasExploitAllLpSolution_ == other.hasExploitAllLpSolution_
        && exploitBestSolution_ == other.exploitBestSolution_
        && hasExploitBestSolution_ == other.hasExploitBestSolution_
        && exploitRelaxationSolution_ == other.exploitRelaxationSolution_
        && hasExploitRelaxationSolution_ == other.hasExploitRelaxationSolution_
        && exploitObjective_ == other.exploitObjective_
        && hasExploitObjective_ == other.hasExploitObjective_
        && detectLinearizedProduct_ == other.detectLinearizedProduct_
        && hasDetectLinearizedProduct_ == other.hasDetectLinearizedProduct_
        && useNewIntegerConflictResolution_ == other.useNewIntegerConflictResolution_
        && hasUseNewIntegerConflictResolution_ == other.hasUseNewIntegerConflictResolution_
        && create1UipBooleanDuringIcr_ == other.create1UipBooleanDuringIcr_
        && hasCreate1UipBooleanDuringIcr_ == other.hasCreate1UipBooleanDuringIcr_
        && Double.compare(mipMaxBound_, other.mipMaxBound_) == 0
        && hasMipMaxBound_ == other.hasMipMaxBound_
        && Double.compare(mipVarScaling_, other.mipVarScaling_) == 0
        && hasMipVarScaling_ == other.hasMipVarScaling_
        && mipScaleLargeDomain_ == other.mipScaleLargeDomain_
        && hasMipScaleLargeDomain_ == other.hasMipScaleLargeDomain_
        && mipAutomaticallyScaleVariables_ == other.mipAutomaticallyScaleVariables_
        && hasMipAutomaticallyScaleVariables_ == other.hasMipAutomaticallyScaleVariables_
        && mipScaleToMaxBound_ == other.mipScaleToMaxBound_
        && hasMipScaleToMaxBound_ == other.hasMipScaleToMaxBound_
        && mipPresolveRestrictBounds_ == other.mipPresolveRestrictBounds_
        && hasMipPresolveRestrictBounds_ == other.hasMipPresolveRestrictBounds_
        && onlySolveIp_ == other.onlySolveIp_
        && hasOnlySolveIp_ == other.hasOnlySolveIp_
        && Double.compare(mipWantedPrecision_, other.mipWantedPrecision_) == 0
        && hasMipWantedPrecision_ == other.hasMipWantedPrecision_
        && mipMaxActivityExponent_ == other.mipMaxActivityExponent_
        && hasMipMaxActivityExponent_ == other.hasMipMaxActivityExponent_
        && Double.compare(mipCheckPrecision_, other.mipCheckPrecision_) == 0
        && hasMipCheckPrecision_ == other.hasMipCheckPrecision_
        && mipComputeTrueObjectiveBound_ == other.mipComputeTrueObjectiveBound_
        && hasMipComputeTrueObjectiveBound_ == other.hasMipComputeTrueObjectiveBound_
        && Double.compare(mipMaxValidMagnitude_, other.mipMaxValidMagnitude_) == 0
        && hasMipMaxValidMagnitude_ == other.hasMipMaxValidMagnitude_
        && mipTreatHighMagnitudeBoundsAsInfinity_ == other.mipTreatHighMagnitudeBoundsAsInfinity_
        && hasMipTreatHighMagnitudeBoundsAsInfinity_ == other.hasMipTreatHighMagnitudeBoundsAsInfinity_
        && Double.compare(mipDropTolerance_, other.mipDropTolerance_) == 0
        && hasMipDropTolerance_ == other.hasMipDropTolerance_
        && mipPresolveLevel_ == other.mipPresolveLevel_
        && hasMipPresolveLevel_ == other.hasMipPresolveLevel_
        && mipUseTightBounds_ == other.mipUseTightBounds_
        && hasMipUseTightBounds_ == other.hasMipUseTightBounds_
        && Double.compare(mipTightPrecision_, other.mipTightPrecision_) == 0
        && hasMipTightPrecision_ == other.hasMipTightPrecision_
        && mipVerifySolution_ == other.mipVerifySolution_
        && hasMipVerifySolution_ == other.hasMipVerifySolution_
        && mipProperRelaxation_ == other.mipProperRelaxation_
        && hasMipProperRelaxation_ == other.hasMipProperRelaxation_;
  }

  @Override
  public int hashCode() {
    int h = 724;
    h = 31 * h + name_.hashCode();
    h = 31 * h + Integer.hashCode(preferredVariableOrder_);
    h = 31 * h + Integer.hashCode(initialPolarity_);
    h = 31 * h + Boolean.hashCode(usePhaseSaving_);
    h = 31 * h + Integer.hashCode(polarityRephaseIncrement_);
    h = 31 * h + Boolean.hashCode(polarityExploitLsHints_);
    h = 31 * h + Double.hashCode(randomPolarityRatio_);
    h = 31 * h + Double.hashCode(randomBranchesRatio_);
    h = 31 * h + Boolean.hashCode(useErwaHeuristic_);
    h = 31 * h + Double.hashCode(initialVariablesActivity_);
    h = 31 * h + Boolean.hashCode(alsoBumpVariablesInConflictReasons_);
    h = 31 * h + Integer.hashCode(minimizationAlgorithm_);
    h = 31 * h + Integer.hashCode(binaryMinimizationAlgorithm_);
    h = 31 * h + Boolean.hashCode(subsumptionDuringConflictAnalysis_);
    h = 31 * h + Boolean.hashCode(extraSubsumptionDuringConflictAnalysis_);
    h = 31 * h + Boolean.hashCode(decisionSubsumptionDuringConflictAnalysis_);
    h = 31 * h + Integer.hashCode(eagerlySubsumeLastNConflicts_);
    h = 31 * h + Boolean.hashCode(subsumeDuringVivification_);
    h = 31 * h + Boolean.hashCode(useChronologicalBacktracking_);
    h = 31 * h + Integer.hashCode(maxBackjumpLevels_);
    h = 31 * h + Integer.hashCode(chronologicalBacktrackMinConflicts_);
    h = 31 * h + Boolean.hashCode(learnOnAssumptionsUnsat_);
    h = 31 * h + Integer.hashCode(clauseCleanupPeriod_);
    h = 31 * h + Integer.hashCode(clauseCleanupPeriodIncrement_);
    h = 31 * h + Integer.hashCode(clauseCleanupTarget_);
    h = 31 * h + Double.hashCode(clauseCleanupRatio_);
    h = 31 * h + Integer.hashCode(clauseCleanupLbdBound_);
    h = 31 * h + Integer.hashCode(clauseCleanupLbdTier1_);
    h = 31 * h + Integer.hashCode(clauseCleanupLbdTier2_);
    h = 31 * h + Integer.hashCode(clauseCleanupOrdering_);
    h = 31 * h + Integer.hashCode(pbCleanupIncrement_);
    h = 31 * h + Double.hashCode(pbCleanupRatio_);
    h = 31 * h + Double.hashCode(variableActivityDecay_);
    h = 31 * h + Double.hashCode(maxVariableActivityValue_);
    h = 31 * h + Double.hashCode(glucoseMaxDecay_);
    h = 31 * h + Double.hashCode(glucoseDecayIncrement_);
    h = 31 * h + Integer.hashCode(glucoseDecayIncrementPeriod_);
    h = 31 * h + Double.hashCode(clauseActivityDecay_);
    h = 31 * h + Double.hashCode(maxClauseActivityValue_);
    h = 31 * h + getRestartAlgorithmsList().hashCode();
    h = 31 * h + defaultRestartAlgorithms_.hashCode();
    h = 31 * h + Integer.hashCode(restartPeriod_);
    h = 31 * h + Integer.hashCode(restartRunningWindowSize_);
    h = 31 * h + Double.hashCode(restartDlAverageRatio_);
    h = 31 * h + Double.hashCode(restartLbdAverageRatio_);
    h = 31 * h + Boolean.hashCode(useBlockingRestart_);
    h = 31 * h + Integer.hashCode(blockingRestartWindowSize_);
    h = 31 * h + Double.hashCode(blockingRestartMultiplier_);
    h = 31 * h + Integer.hashCode(numConflictsBeforeStrategyChanges_);
    h = 31 * h + Double.hashCode(strategyChangeIncreaseRatio_);
    h = 31 * h + Double.hashCode(maxTimeInSeconds_);
    h = 31 * h + Double.hashCode(maxDeterministicTime_);
    h = 31 * h + Integer.hashCode(maxNumDeterministicBatches_);
    h = 31 * h + Long.hashCode(maxNumberOfConflicts_);
    h = 31 * h + Long.hashCode(maxMemoryInMb_);
    h = 31 * h + Double.hashCode(absoluteGapLimit_);
    h = 31 * h + Double.hashCode(relativeGapLimit_);
    h = 31 * h + Integer.hashCode(randomSeed_);
    h = 31 * h + Boolean.hashCode(permuteVariableRandomly_);
    h = 31 * h + Boolean.hashCode(permutePresolveConstraintOrder_);
    h = 31 * h + Boolean.hashCode(useAbslRandom_);
    h = 31 * h + Boolean.hashCode(logSearchProgress_);
    h = 31 * h + Boolean.hashCode(logSubsolverStatistics_);
    h = 31 * h + logPrefix_.hashCode();
    h = 31 * h + Boolean.hashCode(logToStdout_);
    h = 31 * h + Boolean.hashCode(logToResponse_);
    h = 31 * h + Boolean.hashCode(usePbResolution_);
    h = 31 * h + Boolean.hashCode(minimizeReductionDuringPbResolution_);
    h = 31 * h + Boolean.hashCode(countAssumptionLevelsInLbd_);
    h = 31 * h + Integer.hashCode(presolveBveThreshold_);
    h = 31 * h + Boolean.hashCode(filterSatPostsolveClauses_);
    h = 31 * h + Integer.hashCode(presolveBveClauseWeight_);
    h = 31 * h + Double.hashCode(probingDeterministicTimeLimit_);
    h = 31 * h + Double.hashCode(presolveProbingDeterministicTimeLimit_);
    h = 31 * h + Boolean.hashCode(presolveBlockedClause_);
    h = 31 * h + Boolean.hashCode(presolveUseBva_);
    h = 31 * h + Integer.hashCode(presolveBvaThreshold_);
    h = 31 * h + Integer.hashCode(maxPresolveIterations_);
    h = 31 * h + Boolean.hashCode(cpModelPresolve_);
    h = 31 * h + Boolean.hashCode(cpModelDirectSolve_);
    h = 31 * h + Integer.hashCode(cpModelProbingLevel_);
    h = 31 * h + Boolean.hashCode(cpModelUseSatPresolve_);
    h = 31 * h + Boolean.hashCode(cpModelPureSatPresolve_);
    h = 31 * h + Boolean.hashCode(loadAtMostOnesInSatPresolve_);
    h = 31 * h + Boolean.hashCode(removeFixedVariablesEarly_);
    h = 31 * h + Boolean.hashCode(detectTableWithCost_);
    h = 31 * h + Integer.hashCode(tableCompressionLevel_);
    h = 31 * h + Boolean.hashCode(expandAlldiffConstraints_);
    h = 31 * h + Integer.hashCode(maxAlldiffDomainSize_);
    h = 31 * h + Boolean.hashCode(expandReservoirConstraints_);
    h = 31 * h + Integer.hashCode(maxDomainSizeForLinear2Expansion_);
    h = 31 * h + Boolean.hashCode(expandReservoirUsingCircuit_);
    h = 31 * h + Boolean.hashCode(encodeCumulativeAsReservoir_);
    h = 31 * h + Integer.hashCode(maxLinMaxSizeForExpansion_);
    h = 31 * h + Boolean.hashCode(disableConstraintExpansion_);
    h = 31 * h + Boolean.hashCode(encodeComplexLinearConstraintWithInteger_);
    h = 31 * h + Boolean.hashCode(detectEncodedComplexDomain_);
    h = 31 * h + Double.hashCode(mergeNoOverlapWorkLimit_);
    h = 31 * h + Double.hashCode(mergeAtMostOneWorkLimit_);
    h = 31 * h + Integer.hashCode(presolveSubstitutionLevel_);
    h = 31 * h + Boolean.hashCode(presolveExtractIntegerEnforcement_);
    h = 31 * h + Long.hashCode(presolveInclusionWorkLimit_);
    h = 31 * h + Boolean.hashCode(ignoreNames_);
    h = 31 * h + Boolean.hashCode(inferAllDiffs_);
    h = 31 * h + Boolean.hashCode(findBigLinearOverlap_);
    h = 31 * h + Boolean.hashCode(findClausesThatAreExactlyOne_);
    h = 31 * h + Boolean.hashCode(useSatInprocessing_);
    h = 31 * h + Double.hashCode(inprocessingDtimeRatio_);
    h = 31 * h + Double.hashCode(inprocessingProbingDtime_);
    h = 31 * h + Double.hashCode(inprocessingMinimizationDtime_);
    h = 31 * h + Boolean.hashCode(inprocessingMinimizationUseConflictAnalysis_);
    h = 31 * h + Integer.hashCode(numOrderingInVivification_);
    h = 31 * h + Boolean.hashCode(inprocessingUseCongruenceClosure_);
    h = 31 * h + Boolean.hashCode(inprocessingDetectAndSweepCircuit_);
    h = 31 * h + Boolean.hashCode(inprocessingUseSatSweeping_);
    h = 31 * h + Boolean.hashCode(inprocessingUseTransitiveReduction_);
    h = 31 * h + Integer.hashCode(numWorkers_);
    h = 31 * h + Integer.hashCode(numSearchWorkers_);
    h = 31 * h + Integer.hashCode(numFullSubsolvers_);
    h = 31 * h + getSubsolversList().hashCode();
    h = 31 * h + getExtraSubsolversList().hashCode();
    h = 31 * h + getIgnoreSubsolversList().hashCode();
    h = 31 * h + getFilterSubsolversList().hashCode();
    h = 31 * h + getSubsolverParamsList().hashCode();
    h = 31 * h + Boolean.hashCode(interleaveSearch_);
    h = 31 * h + Integer.hashCode(interleaveBatchSize_);
    h = 31 * h + Boolean.hashCode(shareObjectiveBounds_);
    h = 31 * h + Boolean.hashCode(shareLevelZeroBounds_);
    h = 31 * h + Boolean.hashCode(shareLinear2Bounds_);
    h = 31 * h + Boolean.hashCode(shareBinaryClauses_);
    h = 31 * h + Boolean.hashCode(shareGlueClauses_);
    h = 31 * h + Boolean.hashCode(minimizeSharedClauses_);
    h = 31 * h + Double.hashCode(shareGlueClausesDtime_);
    h = 31 * h + Boolean.hashCode(checkLratProof_);
    h = 31 * h + Boolean.hashCode(checkMergedLratProof_);
    h = 31 * h + Boolean.hashCode(outputLratProof_);
    h = 31 * h + Boolean.hashCode(debugPostsolveWithFullSolver_);
    h = 31 * h + Integer.hashCode(debugMaxNumPresolveOperations_);
    h = 31 * h + Boolean.hashCode(debugCrashOnBadHint_);
    h = 31 * h + Boolean.hashCode(debugCrashIfPresolveBreaksHint_);
    h = 31 * h + Boolean.hashCode(debugCrashIfLratCheckFails_);
    h = 31 * h + Boolean.hashCode(useOptimizationHints_);
    h = 31 * h + Integer.hashCode(coreMinimizationLevel_);
    h = 31 * h + Boolean.hashCode(findMultipleCores_);
    h = 31 * h + Boolean.hashCode(coverOptimization_);
    h = 31 * h + Integer.hashCode(maxSatAssumptionOrder_);
    h = 31 * h + Boolean.hashCode(maxSatReverseAssumptionOrder_);
    h = 31 * h + Integer.hashCode(maxSatStratification_);
    h = 31 * h + Double.hashCode(propagationLoopDetectionFactor_);
    h = 31 * h + Boolean.hashCode(usePrecedencesInDisjunctiveConstraint_);
    h = 31 * h + Integer.hashCode(transitivePrecedencesWorkLimit_);
    h = 31 * h + Integer.hashCode(maxSizeToCreatePrecedenceLiteralsInDisjunctive_);
    h = 31 * h + Boolean.hashCode(useStrongPropagationInDisjunctive_);
    h = 31 * h + Boolean.hashCode(useDynamicPrecedenceInDisjunctive_);
    h = 31 * h + Boolean.hashCode(useDynamicPrecedenceInCumulative_);
    h = 31 * h + Boolean.hashCode(useOverloadCheckerInCumulative_);
    h = 31 * h + Boolean.hashCode(useConservativeScaleOverloadChecker_);
    h = 31 * h + Boolean.hashCode(useTimetableEdgeFindingInCumulative_);
    h = 31 * h + Integer.hashCode(maxNumIntervalsForTimetableEdgeFinding_);
    h = 31 * h + Boolean.hashCode(useHardPrecedencesInCumulative_);
    h = 31 * h + Boolean.hashCode(exploitAllPrecedences_);
    h = 31 * h + Boolean.hashCode(useDisjunctiveConstraintInCumulative_);
    h = 31 * h + Integer.hashCode(noOverlap2DBooleanRelationsLimit_);
    h = 31 * h + Boolean.hashCode(useTimetablingInNoOverlap2D_);
    h = 31 * h + Boolean.hashCode(useEnergeticReasoningInNoOverlap2D_);
    h = 31 * h + Boolean.hashCode(useAreaEnergeticReasoningInNoOverlap2D_);
    h = 31 * h + Boolean.hashCode(useTryEdgeReasoningInNoOverlap2D_);
    h = 31 * h + Integer.hashCode(maxPairsPairwiseReasoningInNoOverlap2D_);
    h = 31 * h + Integer.hashCode(maximumRegionsToSplitInDisconnectedNoOverlap2D_);
    h = 31 * h + Boolean.hashCode(useLinear3ForNoOverlap2DPrecedences_);
    h = 31 * h + Boolean.hashCode(useDualSchedulingHeuristics_);
    h = 31 * h + Boolean.hashCode(useAllDifferentForCircuit_);
    h = 31 * h + Integer.hashCode(routingCutSubsetSizeForBinaryRelationBound_);
    h = 31 * h + Integer.hashCode(routingCutSubsetSizeForTightBinaryRelationBound_);
    h = 31 * h + Integer.hashCode(routingCutSubsetSizeForExactBinaryRelationBound_);
    h = 31 * h + Integer.hashCode(routingCutSubsetSizeForShortestPathsBound_);
    h = 31 * h + Double.hashCode(routingCutDpEffort_);
    h = 31 * h + Integer.hashCode(routingCutMaxInfeasiblePathLength_);
    h = 31 * h + Integer.hashCode(searchBranching_);
    h = 31 * h + Integer.hashCode(hintConflictLimit_);
    h = 31 * h + Boolean.hashCode(tryHintAsAssumptions_);
    h = 31 * h + Boolean.hashCode(repairHint_);
    h = 31 * h + Boolean.hashCode(fixVariablesToTheirHintedValue_);
    h = 31 * h + Boolean.hashCode(useProbingSearch_);
    h = 31 * h + Boolean.hashCode(useExtendedProbing_);
    h = 31 * h + Integer.hashCode(probingNumCombinationsLimit_);
    h = 31 * h + Double.hashCode(shavingDeterministicTimeInProbingSearch_);
    h = 31 * h + Double.hashCode(shavingSearchDeterministicTime_);
    h = 31 * h + Boolean.hashCode(shavingSearchUseRandomSearch_);
    h = 31 * h + Integer.hashCode(shavingSearchRestartLimit_);
    h = 31 * h + Long.hashCode(shavingSearchThreshold_);
    h = 31 * h + Long.hashCode(objectiveShavingSearchThreshold_);
    h = 31 * h + Boolean.hashCode(useObjectiveLbSearch_);
    h = 31 * h + Boolean.hashCode(useObjectiveShavingSearch_);
    h = 31 * h + Integer.hashCode(variablesShavingLevel_);
    h = 31 * h + Long.hashCode(pseudoCostReliabilityThreshold_);
    h = 31 * h + Boolean.hashCode(optimizeWithCore_);
    h = 31 * h + Boolean.hashCode(optimizeWithLbTreeSearch_);
    h = 31 * h + Boolean.hashCode(saveLpBasisInLbTreeSearch_);
    h = 31 * h + Integer.hashCode(binarySearchNumConflicts_);
    h = 31 * h + Boolean.hashCode(optimizeWithMaxHs_);
    h = 31 * h + Boolean.hashCode(useFeasibilityJump_);
    h = 31 * h + Boolean.hashCode(useLsOnly_);
    h = 31 * h + Double.hashCode(feasibilityJumpDecay_);
    h = 31 * h + Integer.hashCode(feasibilityJumpLinearizationLevel_);
    h = 31 * h + Integer.hashCode(feasibilityJumpRestartFactor_);
    h = 31 * h + Double.hashCode(feasibilityJumpBatchDtime_);
    h = 31 * h + Double.hashCode(feasibilityJumpVarRandomizationProbability_);
    h = 31 * h + Double.hashCode(feasibilityJumpVarPerburbationRangeRatio_);
    h = 31 * h + Boolean.hashCode(feasibilityJumpEnableRestarts_);
    h = 31 * h + Integer.hashCode(feasibilityJumpMaxExpandedConstraintSize_);
    h = 31 * h + Boolean.hashCode(feasibilityJumpIgnoreRedundantConstraints_);
    h = 31 * h + Boolean.hashCode(lnsIgnoreRedundantConstraints_);
    h = 31 * h + Integer.hashCode(numViolationLs_);
    h = 31 * h + Integer.hashCode(violationLsPerturbationPeriod_);
    h = 31 * h + Integer.hashCode(sharedTreeNumWorkers_);
    h = 31 * h + Boolean.hashCode(useSharedTreeSearch_);
    h = 31 * h + Integer.hashCode(sharedTreeWorkerMinRestartsPerSubtree_);
    h = 31 * h + Boolean.hashCode(sharedTreeWorkerEnableTrailSharing_);
    h = 31 * h + Boolean.hashCode(sharedTreeWorkerEnablePhaseSharing_);
    h = 31 * h + Boolean.hashCode(sharedTreeWorkerPropagateIntermediateNodes_);
    h = 31 * h + Double.hashCode(sharedTreeOpenLeavesPerWorker_);
    h = 31 * h + Integer.hashCode(sharedTreeMaxNodesPerWorker_);
    h = 31 * h + Integer.hashCode(sharedTreeSplitStrategy_);
    h = 31 * h + Integer.hashCode(sharedTreeBalanceTolerance_);
    h = 31 * h + Double.hashCode(sharedTreeSplitMinDtime_);
    h = 31 * h + Boolean.hashCode(enumerateAllSolutions_);
    h = 31 * h + Boolean.hashCode(keepAllFeasibleSolutionsInPresolve_);
    h = 31 * h + Boolean.hashCode(fillTightenedDomainsInResponse_);
    h = 31 * h + Boolean.hashCode(fillAdditionalSolutionsInResponse_);
    h = 31 * h + Boolean.hashCode(instantiateAllVariables_);
    h = 31 * h + Boolean.hashCode(autoDetectGreaterThanAtLeastOneOf_);
    h = 31 * h + Boolean.hashCode(stopAfterFirstSolution_);
    h = 31 * h + Boolean.hashCode(stopAfterPresolve_);
    h = 31 * h + Boolean.hashCode(stopAfterRootPropagation_);
    h = 31 * h + Double.hashCode(lnsInitialDifficulty_);
    h = 31 * h + Double.hashCode(lnsInitialDeterministicLimit_);
    h = 31 * h + Boolean.hashCode(useLns_);
    h = 31 * h + Boolean.hashCode(useLnsOnly_);
    h = 31 * h + Integer.hashCode(solutionPoolSize_);
    h = 31 * h + Integer.hashCode(solutionPoolDiversityLimit_);
    h = 31 * h + Integer.hashCode(alternativePoolSize_);
    h = 31 * h + Boolean.hashCode(useRinsLns_);
    h = 31 * h + Boolean.hashCode(useFeasibilityPump_);
    h = 31 * h + Boolean.hashCode(useLbRelaxLns_);
    h = 31 * h + Integer.hashCode(lbRelaxNumWorkersThreshold_);
    h = 31 * h + Integer.hashCode(fpRounding_);
    h = 31 * h + Boolean.hashCode(diversifyLnsParams_);
    h = 31 * h + Boolean.hashCode(randomizeSearch_);
    h = 31 * h + Long.hashCode(searchRandomVariablePoolSize_);
    h = 31 * h + Boolean.hashCode(pushAllTasksTowardStart_);
    h = 31 * h + Boolean.hashCode(useOptionalVariables_);
    h = 31 * h + Boolean.hashCode(useExactLpReason_);
    h = 31 * h + Boolean.hashCode(useCombinedNoOverlap_);
    h = 31 * h + Integer.hashCode(atMostOneMaxExpansionSize_);
    h = 31 * h + Boolean.hashCode(catchSigintSignal_);
    h = 31 * h + Boolean.hashCode(useImpliedBounds_);
    h = 31 * h + Boolean.hashCode(polishLpSolution_);
    h = 31 * h + Double.hashCode(lpPrimalTolerance_);
    h = 31 * h + Double.hashCode(lpDualTolerance_);
    h = 31 * h + Boolean.hashCode(convertIntervals_);
    h = 31 * h + Integer.hashCode(symmetryLevel_);
    h = 31 * h + Boolean.hashCode(useSymmetryInLp_);
    h = 31 * h + Boolean.hashCode(keepSymmetryInPresolve_);
    h = 31 * h + Double.hashCode(symmetryDetectionDeterministicTimeLimit_);
    h = 31 * h + Boolean.hashCode(newLinearPropagation_);
    h = 31 * h + Integer.hashCode(linearSplitSize_);
    h = 31 * h + Integer.hashCode(linearizationLevel_);
    h = 31 * h + Integer.hashCode(booleanEncodingLevel_);
    h = 31 * h + Integer.hashCode(maxDomainSizeWhenEncodingEqNeqConstraints_);
    h = 31 * h + Integer.hashCode(maxNumCuts_);
    h = 31 * h + Integer.hashCode(cutLevel_);
    h = 31 * h + Boolean.hashCode(onlyAddCutsAtLevelZero_);
    h = 31 * h + Boolean.hashCode(addObjectiveCut_);
    h = 31 * h + Boolean.hashCode(addCgCuts_);
    h = 31 * h + Boolean.hashCode(addMirCuts_);
    h = 31 * h + Boolean.hashCode(addZeroHalfCuts_);
    h = 31 * h + Boolean.hashCode(addCliqueCuts_);
    h = 31 * h + Boolean.hashCode(addRltCuts_);
    h = 31 * h + Integer.hashCode(maxAllDiffCutSize_);
    h = 31 * h + Boolean.hashCode(addLinMaxCuts_);
    h = 31 * h + Integer.hashCode(maxIntegerRoundingScaling_);
    h = 31 * h + Boolean.hashCode(addLpConstraintsLazily_);
    h = 31 * h + Integer.hashCode(rootLpIterations_);
    h = 31 * h + Double.hashCode(minOrthogonalityForLpConstraints_);
    h = 31 * h + Integer.hashCode(maxCutRoundsAtLevelZero_);
    h = 31 * h + Integer.hashCode(maxConsecutiveInactiveCount_);
    h = 31 * h + Double.hashCode(cutMaxActiveCountValue_);
    h = 31 * h + Double.hashCode(cutActiveCountDecay_);
    h = 31 * h + Integer.hashCode(cutCleanupTarget_);
    h = 31 * h + Integer.hashCode(newConstraintsBatchSize_);
    h = 31 * h + Boolean.hashCode(exploitIntegerLpSolution_);
    h = 31 * h + Boolean.hashCode(exploitAllLpSolution_);
    h = 31 * h + Boolean.hashCode(exploitBestSolution_);
    h = 31 * h + Boolean.hashCode(exploitRelaxationSolution_);
    h = 31 * h + Boolean.hashCode(exploitObjective_);
    h = 31 * h + Boolean.hashCode(detectLinearizedProduct_);
    h = 31 * h + Boolean.hashCode(useNewIntegerConflictResolution_);
    h = 31 * h + Boolean.hashCode(create1UipBooleanDuringIcr_);
    h = 31 * h + Double.hashCode(mipMaxBound_);
    h = 31 * h + Double.hashCode(mipVarScaling_);
    h = 31 * h + Boolean.hashCode(mipScaleLargeDomain_);
    h = 31 * h + Boolean.hashCode(mipAutomaticallyScaleVariables_);
    h = 31 * h + Boolean.hashCode(mipScaleToMaxBound_);
    h = 31 * h + Boolean.hashCode(mipPresolveRestrictBounds_);
    h = 31 * h + Boolean.hashCode(onlySolveIp_);
    h = 31 * h + Double.hashCode(mipWantedPrecision_);
    h = 31 * h + Integer.hashCode(mipMaxActivityExponent_);
    h = 31 * h + Double.hashCode(mipCheckPrecision_);
    h = 31 * h + Boolean.hashCode(mipComputeTrueObjectiveBound_);
    h = 31 * h + Double.hashCode(mipMaxValidMagnitude_);
    h = 31 * h + Boolean.hashCode(mipTreatHighMagnitudeBoundsAsInfinity_);
    h = 31 * h + Double.hashCode(mipDropTolerance_);
    h = 31 * h + Integer.hashCode(mipPresolveLevel_);
    h = 31 * h + Boolean.hashCode(mipUseTightBounds_);
    h = 31 * h + Double.hashCode(mipTightPrecision_);
    h = 31 * h + Boolean.hashCode(mipVerifySolution_);
    h = 31 * h + Boolean.hashCode(mipProperRelaxation_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (hasPreferredVariableOrder_) {
      w.int32(1, preferredVariableOrder_);
    }
    if (hasInitialPolarity_) {
      w.int32(2, initialPolarity_);
    }
    if (hasMinimizationAlgorithm_) {
      w.int32(4, minimizationAlgorithm_);
    }
    if (hasClauseCleanupPeriod_) {
      w.int32(11, clauseCleanupPeriod_);
    }
    if (hasClauseCleanupTarget_) {
      w.int32(13, clauseCleanupTarget_);
    }
    if (hasVariableActivityDecay_) {
      w.dbl(15, variableActivityDecay_);
    }
    if (hasMaxVariableActivityValue_) {
      w.dbl(16, maxVariableActivityValue_);
    }
    if (hasClauseActivityDecay_) {
      w.dbl(17, clauseActivityDecay_);
    }
    if (hasMaxClauseActivityValue_) {
      w.dbl(18, maxClauseActivityValue_);
    }
    if (hasGlucoseMaxDecay_) {
      w.dbl(22, glucoseMaxDecay_);
    }
    if (hasGlucoseDecayIncrement_) {
      w.dbl(23, glucoseDecayIncrement_);
    }
    if (hasGlucoseDecayIncrementPeriod_) {
      w.int32(24, glucoseDecayIncrementPeriod_);
    }
    if (hasRestartPeriod_) {
      w.int32(30, restartPeriod_);
    }
    if (hasRandomSeed_) {
      w.int32(31, randomSeed_);
    }
    if (hasRandomBranchesRatio_) {
      w.dbl(32, randomBranchesRatio_);
    }
    if (hasBinaryMinimizationAlgorithm_) {
      w.int32(34, binaryMinimizationAlgorithm_);
    }
    if (hasUseOptimizationHints_) {
      w.bool(35, useOptimizationHints_);
    }
    if (hasMaxTimeInSeconds_) {
      w.dbl(36, maxTimeInSeconds_);
    }
    if (hasMaxNumberOfConflicts_) {
      w.int64(37, maxNumberOfConflicts_);
    }
    if (hasMaxMemoryInMb_) {
      w.int64(40, maxMemoryInMb_);
    }
    if (hasLogSearchProgress_) {
      w.bool(41, logSearchProgress_);
    }
    if (hasUsePbResolution_) {
      w.bool(43, usePbResolution_);
    }
    if (hasUsePhaseSaving_) {
      w.bool(44, usePhaseSaving_);
    }
    if (hasRandomPolarityRatio_) {
      w.dbl(45, randomPolarityRatio_);
    }
    if (hasPbCleanupIncrement_) {
      w.int32(46, pbCleanupIncrement_);
    }
    if (hasPbCleanupRatio_) {
      w.dbl(47, pbCleanupRatio_);
    }
    if (hasMinimizeReductionDuringPbResolution_) {
      w.bool(48, minimizeReductionDuringPbResolution_);
    }
    if (hasCountAssumptionLevelsInLbd_) {
      w.bool(49, countAssumptionLevelsInLbd_);
    }
    if (hasCoreMinimizationLevel_) {
      w.int32(50, coreMinimizationLevel_);
    }
    if (hasMaxSatAssumptionOrder_) {
      w.int32(51, maxSatAssumptionOrder_);
    }
    if (hasMaxSatReverseAssumptionOrder_) {
      w.bool(52, maxSatReverseAssumptionOrder_);
    }
    if (hasMaxSatStratification_) {
      w.int32(53, maxSatStratification_);
    }
    if (hasPresolveBveThreshold_) {
      w.int32(54, presolveBveThreshold_);
    }
    if (hasPresolveBveClauseWeight_) {
      w.int32(55, presolveBveClauseWeight_);
    }
    if (hasSubsumptionDuringConflictAnalysis_) {
      w.bool(56, subsumptionDuringConflictAnalysis_);
    }
    if (hasPresolveProbingDeterministicTimeLimit_) {
      w.dbl(57, presolveProbingDeterministicTimeLimit_);
    }
    if (hasClauseCleanupLbdBound_) {
      w.int32(59, clauseCleanupLbdBound_);
    }
    if (hasClauseCleanupOrdering_) {
      w.int32(60, clauseCleanupOrdering_);
    }
    if (restartAlgorithms_ != null) {
      for (SatParameters.RestartAlgorithm v : restartAlgorithms_) {
        w.int32(61, v.getNumber());
      }
    }
    if (hasRestartRunningWindowSize_) {
      w.int32(62, restartRunningWindowSize_);
    }
    if (hasRestartDlAverageRatio_) {
      w.dbl(63, restartDlAverageRatio_);
    }
    if (hasUseBlockingRestart_) {
      w.bool(64, useBlockingRestart_);
    }
    if (hasBlockingRestartWindowSize_) {
      w.int32(65, blockingRestartWindowSize_);
    }
    if (hasBlockingRestartMultiplier_) {
      w.dbl(66, blockingRestartMultiplier_);
    }
    if (hasMaxDeterministicTime_) {
      w.dbl(67, maxDeterministicTime_);
    }
    if (hasNumConflictsBeforeStrategyChanges_) {
      w.int32(68, numConflictsBeforeStrategyChanges_);
    }
    if (hasStrategyChangeIncreaseRatio_) {
      w.dbl(69, strategyChangeIncreaseRatio_);
    }
    if (hasDefaultRestartAlgorithms_) {
      w.string(70, defaultRestartAlgorithms_);
    }
    if (hasRestartLbdAverageRatio_) {
      w.dbl(71, restartLbdAverageRatio_);
    }
    if (hasPresolveUseBva_) {
      w.bool(72, presolveUseBva_);
    }
    if (hasPresolveBvaThreshold_) {
      w.int32(73, presolveBvaThreshold_);
    }
    if (hasUsePrecedencesInDisjunctiveConstraint_) {
      w.bool(74, usePrecedencesInDisjunctiveConstraint_);
    }
    if (hasUseErwaHeuristic_) {
      w.bool(75, useErwaHeuristic_);
    }
    if (hasInitialVariablesActivity_) {
      w.dbl(76, initialVariablesActivity_);
    }
    if (hasAlsoBumpVariablesInConflictReasons_) {
      w.bool(77, alsoBumpVariablesInConflictReasons_);
    }
    if (hasUseOverloadCheckerInCumulative_) {
      w.bool(78, useOverloadCheckerInCumulative_);
    }
    if (hasUseTimetableEdgeFindingInCumulative_) {
      w.bool(79, useTimetableEdgeFindingInCumulative_);
    }
    if (hasUseDisjunctiveConstraintInCumulative_) {
      w.bool(80, useDisjunctiveConstraintInCumulative_);
    }
    if (hasSearchBranching_) {
      w.int32(82, searchBranching_);
    }
    if (hasOptimizeWithCore_) {
      w.bool(83, optimizeWithCore_);
    }
    if (hasFindMultipleCores_) {
      w.bool(84, findMultipleCores_);
    }
    if (hasOptimizeWithMaxHs_) {
      w.bool(85, optimizeWithMaxHs_);
    }
    if (hasCpModelPresolve_) {
      w.bool(86, cpModelPresolve_);
    }
    if (hasEnumerateAllSolutions_) {
      w.bool(87, enumerateAllSolutions_);
    }
    if (hasPresolveBlockedClause_) {
      w.bool(88, presolveBlockedClause_);
    }
    if (hasCoverOptimization_) {
      w.bool(89, coverOptimization_);
    }
    if (hasLinearizationLevel_) {
      w.int32(90, linearizationLevel_);
    }
    if (hasMaxNumCuts_) {
      w.int32(91, maxNumCuts_);
    }
    if (hasOnlyAddCutsAtLevelZero_) {
      w.bool(92, onlyAddCutsAtLevelZero_);
    }
    if (hasCpModelUseSatPresolve_) {
      w.bool(93, cpModelUseSatPresolve_);
    }
    if (hasExploitIntegerLpSolution_) {
      w.bool(94, exploitIntegerLpSolution_);
    }
    if (hasAutoDetectGreaterThanAtLeastOneOf_) {
      w.bool(95, autoDetectGreaterThanAtLeastOneOf_);
    }
    if (hasStopAfterFirstSolution_) {
      w.bool(98, stopAfterFirstSolution_);
    }
    if (hasBinarySearchNumConflicts_) {
      w.int32(99, binarySearchNumConflicts_);
    }
    if (hasNumSearchWorkers_) {
      w.int32(100, numSearchWorkers_);
    }
    if (hasUseLnsOnly_) {
      w.bool(101, useLnsOnly_);
    }
    if (hasRandomizeSearch_) {
      w.bool(103, randomizeSearch_);
    }
    if (hasSearchRandomVariablePoolSize_) {
      w.int64(104, searchRandomVariablePoolSize_);
    }
    if (hasInstantiateAllVariables_) {
      w.bool(106, instantiateAllVariables_);
    }
    if (hasBooleanEncodingLevel_) {
      w.int32(107, booleanEncodingLevel_);
    }
    if (hasUseOptionalVariables_) {
      w.bool(108, useOptionalVariables_);
    }
    if (hasUseExactLpReason_) {
      w.bool(109, useExactLpReason_);
    }
    if (hasCpModelProbingLevel_) {
      w.int32(110, cpModelProbingLevel_);
    }
    if (hasAddLpConstraintsLazily_) {
      w.bool(112, addLpConstraintsLazily_);
    }
    if (hasShareObjectiveBounds_) {
      w.bool(113, shareObjectiveBounds_);
    }
    if (hasShareLevelZeroBounds_) {
      w.bool(114, shareLevelZeroBounds_);
    }
    if (hasMinOrthogonalityForLpConstraints_) {
      w.dbl(115, minOrthogonalityForLpConstraints_);
    }
    if (hasExploitAllLpSolution_) {
      w.bool(116, exploitAllLpSolution_);
    }
    if (hasAddCgCuts_) {
      w.bool(117, addCgCuts_);
    }
    if (hasMaxIntegerRoundingScaling_) {
      w.int32(119, maxIntegerRoundingScaling_);
    }
    if (hasAddMirCuts_) {
      w.bool(120, addMirCuts_);
    }
    if (hasMaxConsecutiveInactiveCount_) {
      w.int32(121, maxConsecutiveInactiveCount_);
    }
    if (hasNewConstraintsBatchSize_) {
      w.int32(122, newConstraintsBatchSize_);
    }
    if (hasPseudoCostReliabilityThreshold_) {
      w.int64(123, pseudoCostReliabilityThreshold_);
    }
    if (hasMipMaxBound_) {
      w.dbl(124, mipMaxBound_);
    }
    if (hasMipVarScaling_) {
      w.dbl(125, mipVarScaling_);
    }
    if (hasMipWantedPrecision_) {
      w.dbl(126, mipWantedPrecision_);
    }
    if (hasMipMaxActivityExponent_) {
      w.int32(127, mipMaxActivityExponent_);
    }
    if (hasMipCheckPrecision_) {
      w.dbl(128, mipCheckPrecision_);
    }
    if (hasUseRinsLns_) {
      w.bool(129, useRinsLns_);
    }
    if (hasExploitBestSolution_) {
      w.bool(130, exploitBestSolution_);
    }
    if (hasExploitObjective_) {
      w.bool(131, exploitObjective_);
    }
    if (hasFillTightenedDomainsInResponse_) {
      w.bool(132, fillTightenedDomainsInResponse_);
    }
    if (hasUseCombinedNoOverlap_) {
      w.bool(133, useCombinedNoOverlap_);
    }
    if (hasInterleaveBatchSize_) {
      w.int32(134, interleaveBatchSize_);
    }
    if (hasCatchSigintSignal_) {
      w.bool(135, catchSigintSignal_);
    }
    if (hasInterleaveSearch_) {
      w.bool(136, interleaveSearch_);
    }
    if (hasDiversifyLnsParams_) {
      w.bool(137, diversifyLnsParams_);
    }
    if (hasMaxPresolveIterations_) {
      w.int32(138, maxPresolveIterations_);
    }
    if (hasUseImpliedBounds_) {
      w.bool(144, useImpliedBounds_);
    }
    if (hasMergeNoOverlapWorkLimit_) {
      w.dbl(145, mergeNoOverlapWorkLimit_);
    }
    if (hasMergeAtMostOneWorkLimit_) {
      w.dbl(146, mergeAtMostOneWorkLimit_);
    }
    if (hasPresolveSubstitutionLevel_) {
      w.int32(147, presolveSubstitutionLevel_);
    }
    if (hasMaxAllDiffCutSize_) {
      w.int32(148, maxAllDiffCutSize_);
    }
    if (hasStopAfterPresolve_) {
      w.bool(149, stopAfterPresolve_);
    }
    if (hasDebugMaxNumPresolveOperations_) {
      w.int32(151, debugMaxNumPresolveOperations_);
    }
    if (hasAddLinMaxCuts_) {
      w.bool(152, addLinMaxCuts_);
    }
    if (hasHintConflictLimit_) {
      w.int32(153, hintConflictLimit_);
    }
    if (hasMaxCutRoundsAtLevelZero_) {
      w.int32(154, maxCutRoundsAtLevelZero_);
    }
    if (hasCutMaxActiveCountValue_) {
      w.dbl(155, cutMaxActiveCountValue_);
    }
    if (hasCutActiveCountDecay_) {
      w.dbl(156, cutActiveCountDecay_);
    }
    if (hasCutCleanupTarget_) {
      w.int32(157, cutCleanupTarget_);
    }
    if (hasAbsoluteGapLimit_) {
      w.dbl(159, absoluteGapLimit_);
    }
    if (hasRelativeGapLimit_) {
      w.dbl(160, relativeGapLimit_);
    }
    if (hasExploitRelaxationSolution_) {
      w.bool(161, exploitRelaxationSolution_);
    }
    if (hasDebugPostsolveWithFullSolver_) {
      w.bool(162, debugPostsolveWithFullSolver_);
    }
    if (hasUseSatInprocessing_) {
      w.bool(163, useSatInprocessing_);
    }
    if (hasUseFeasibilityPump_) {
      w.bool(164, useFeasibilityPump_);
    }
    if (hasFpRounding_) {
      w.int32(165, fpRounding_);
    }
    if (hasMipAutomaticallyScaleVariables_) {
      w.bool(166, mipAutomaticallyScaleVariables_);
    }
    if (hasRepairHint_) {
      w.bool(167, repairHint_);
    }
    if (hasPolarityRephaseIncrement_) {
      w.int32(168, polarityRephaseIncrement_);
    }
    if (hasAddZeroHalfCuts_) {
      w.bool(169, addZeroHalfCuts_);
    }
    if (hasExpandAlldiffConstraints_) {
      w.bool(170, expandAlldiffConstraints_);
    }
    if (hasName_) {
      w.string(171, name_);
    }
    if (hasAddCliqueCuts_) {
      w.bool(172, addCliqueCuts_);
    }
    if (hasKeepAllFeasibleSolutionsInPresolve_) {
      w.bool(173, keepAllFeasibleSolutionsInPresolve_);
    }
    if (hasPresolveExtractIntegerEnforcement_) {
      w.bool(174, presolveExtractIntegerEnforcement_);
    }
    if (hasPolishLpSolution_) {
      w.bool(175, polishLpSolution_);
    }
    if (hasUseProbingSearch_) {
      w.bool(176, useProbingSearch_);
    }
    if (hasConvertIntervals_) {
      w.bool(177, convertIntervals_);
    }
    if (hasPermuteVariableRandomly_) {
      w.bool(178, permuteVariableRandomly_);
    }
    if (hasPermutePresolveConstraintOrder_) {
      w.bool(179, permutePresolveConstraintOrder_);
    }
    if (hasUseAbslRandom_) {
      w.bool(180, useAbslRandom_);
    }
    if (hasDisableConstraintExpansion_) {
      w.bool(181, disableConstraintExpansion_);
    }
    if (hasExpandReservoirConstraints_) {
      w.bool(182, expandReservoirConstraints_);
    }
    if (hasSymmetryLevel_) {
      w.int32(183, symmetryLevel_);
    }
    if (hasLogPrefix_) {
      w.string(185, logPrefix_);
    }
    if (hasLogToStdout_) {
      w.bool(186, logToStdout_);
    }
    if (hasLogToResponse_) {
      w.bool(187, logToResponse_);
    }
    if (hasOptimizeWithLbTreeSearch_) {
      w.bool(188, optimizeWithLbTreeSearch_);
    }
    if (hasLogSubsolverStatistics_) {
      w.bool(189, logSubsolverStatistics_);
    }
    if (hasClauseCleanupRatio_) {
      w.dbl(190, clauseCleanupRatio_);
    }
    if (hasMaxDomainSizeWhenEncodingEqNeqConstraints_) {
      w.int32(191, maxDomainSizeWhenEncodingEqNeqConstraints_);
    }
    if (hasFixVariablesToTheirHintedValue_) {
      w.bool(192, fixVariablesToTheirHintedValue_);
    }
    if (hasSolutionPoolSize_) {
      w.int32(193, solutionPoolSize_);
    }
    if (hasFillAdditionalSolutionsInResponse_) {
      w.bool(194, fillAdditionalSolutionsInResponse_);
    }
    if (hasDebugCrashOnBadHint_) {
      w.bool(195, debugCrashOnBadHint_);
    }
    if (hasCutLevel_) {
      w.int32(196, cutLevel_);
    }
    if (hasAddObjectiveCut_) {
      w.bool(197, addObjectiveCut_);
    }
    if (hasMipComputeTrueObjectiveBound_) {
      w.bool(198, mipComputeTrueObjectiveBound_);
    }
    if (hasMipMaxValidMagnitude_) {
      w.dbl(199, mipMaxValidMagnitude_);
    }
    if (hasUseTimetablingInNoOverlap2D_) {
      w.bool(200, useTimetablingInNoOverlap2D_);
    }
    if (hasPresolveInclusionWorkLimit_) {
      w.int64(201, presolveInclusionWorkLimit_);
    }
    if (hasIgnoreNames_) {
      w.bool(202, ignoreNames_);
    }
    if (hasShareBinaryClauses_) {
      w.bool(203, shareBinaryClauses_);
    }
    if (hasShavingDeterministicTimeInProbingSearch_) {
      w.dbl(204, shavingDeterministicTimeInProbingSearch_);
    }
    if (hasShavingSearchDeterministicTime_) {
      w.dbl(205, shavingSearchDeterministicTime_);
    }
    if (hasNumWorkers_) {
      w.int32(206, numWorkers_);
    }
    if (subsolvers_ != null) {
      for (String v : subsolvers_) {
        w.string(207, v);
      }
    }
    if (ignoreSubsolvers_ != null) {
      for (String v : ignoreSubsolvers_) {
        w.string(209, v);
      }
    }
    if (subsolverParams_ != null) {
      for (SatParameters v : subsolverParams_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(210, sub);
      }
    }
    if (hasUseEnergeticReasoningInNoOverlap2D_) {
      w.bool(213, useEnergeticReasoningInNoOverlap2D_);
    }
    if (hasUseDualSchedulingHeuristics_) {
      w.bool(214, useDualSchedulingHeuristics_);
    }
    if (hasUseHardPrecedencesInCumulative_) {
      w.bool(215, useHardPrecedencesInCumulative_);
    }
    if (hasDetectTableWithCost_) {
      w.bool(216, detectTableWithCost_);
    }
    if (hasTableCompressionLevel_) {
      w.int32(217, tableCompressionLevel_);
    }
    if (extraSubsolvers_ != null) {
      for (String v : extraSubsolvers_) {
        w.string(219, v);
      }
    }
    if (hasExploitAllPrecedences_) {
      w.bool(220, exploitAllPrecedences_);
    }
    if (hasPropagationLoopDetectionFactor_) {
      w.dbl(221, propagationLoopDetectionFactor_);
    }
    if (hasOnlySolveIp_) {
      w.bool(222, onlySolveIp_);
    }
    if (hasEncodeComplexLinearConstraintWithInteger_) {
      w.bool(223, encodeComplexLinearConstraintWithInteger_);
    }
    if (hasNewLinearPropagation_) {
      w.bool(224, newLinearPropagation_);
    }
    if (hasMipScaleLargeDomain_) {
      w.bool(225, mipScaleLargeDomain_);
    }
    if (hasProbingDeterministicTimeLimit_) {
      w.dbl(226, probingDeterministicTimeLimit_);
    }
    if (hasRootLpIterations_) {
      w.int32(227, rootLpIterations_);
    }
    if (hasUseObjectiveLbSearch_) {
      w.bool(228, useObjectiveLbSearch_);
    }
    if (hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_) {
      w.int32(229, maxSizeToCreatePrecedenceLiteralsInDisjunctive_);
    }
    if (hasUseStrongPropagationInDisjunctive_) {
      w.bool(230, useStrongPropagationInDisjunctive_);
    }
    if (hasMipDropTolerance_) {
      w.dbl(232, mipDropTolerance_);
    }
    if (hasInferAllDiffs_) {
      w.bool(233, inferAllDiffs_);
    }
    if (hasFindBigLinearOverlap_) {
      w.bool(234, findBigLinearOverlap_);
    }
    if (hasSharedTreeNumWorkers_) {
      w.int32(235, sharedTreeNumWorkers_);
    }
    if (hasUseSharedTreeSearch_) {
      w.bool(236, useSharedTreeSearch_);
    }
    if (hasSharedTreeMaxNodesPerWorker_) {
      w.int32(238, sharedTreeMaxNodesPerWorker_);
    }
    if (hasSharedTreeSplitStrategy_) {
      w.int32(239, sharedTreeSplitStrategy_);
    }
    if (hasUseLsOnly_) {
      w.bool(240, useLsOnly_);
    }
    if (hasFeasibilityJumpDecay_) {
      w.dbl(242, feasibilityJumpDecay_);
    }
    if (hasNumViolationLs_) {
      w.int32(244, numViolationLs_);
    }
    if (hasFeasibilityJumpVarRandomizationProbability_) {
      w.dbl(247, feasibilityJumpVarRandomizationProbability_);
    }
    if (hasFeasibilityJumpVarPerburbationRangeRatio_) {
      w.dbl(248, feasibilityJumpVarPerburbationRangeRatio_);
    }
    if (hasViolationLsPerturbationPeriod_) {
      w.int32(249, violationLsPerturbationPeriod_);
    }
    if (hasFeasibilityJumpEnableRestarts_) {
      w.bool(250, feasibilityJumpEnableRestarts_);
    }
    if (hasStopAfterRootPropagation_) {
      w.bool(252, stopAfterRootPropagation_);
    }
    if (hasUseObjectiveShavingSearch_) {
      w.bool(253, useObjectiveShavingSearch_);
    }
    if (hasUseLbRelaxLns_) {
      w.bool(255, useLbRelaxLns_);
    }
    if (hasLinearSplitSize_) {
      w.int32(256, linearSplitSize_);
    }
    if (hasFeasibilityJumpLinearizationLevel_) {
      w.int32(257, feasibilityJumpLinearizationLevel_);
    }
    if (hasFeasibilityJumpRestartFactor_) {
      w.int32(258, feasibilityJumpRestartFactor_);
    }
    if (hasMaxNumIntervalsForTimetableEdgeFinding_) {
      w.int32(260, maxNumIntervalsForTimetableEdgeFinding_);
    }
    if (hasMipPresolveLevel_) {
      w.int32(261, mipPresolveLevel_);
    }
    if (hasPushAllTasksTowardStart_) {
      w.bool(262, pushAllTasksTowardStart_);
    }
    if (hasUseDynamicPrecedenceInDisjunctive_) {
      w.bool(263, useDynamicPrecedenceInDisjunctive_);
    }
    if (hasFeasibilityJumpMaxExpandedConstraintSize_) {
      w.int32(264, feasibilityJumpMaxExpandedConstraintSize_);
    }
    if (hasUseFeasibilityJump_) {
      w.bool(265, useFeasibilityJump_);
    }
    if (hasLpPrimalTolerance_) {
      w.dbl(266, lpPrimalTolerance_);
    }
    if (hasLpDualTolerance_) {
      w.dbl(267, lpDualTolerance_);
    }
    if (hasUseDynamicPrecedenceInCumulative_) {
      w.bool(268, useDynamicPrecedenceInCumulative_);
    }
    if (hasUseExtendedProbing_) {
      w.bool(269, useExtendedProbing_);
    }
    if (hasAtMostOneMaxExpansionSize_) {
      w.int32(270, atMostOneMaxExpansionSize_);
    }
    if (hasUseAreaEnergeticReasoningInNoOverlap2D_) {
      w.bool(271, useAreaEnergeticReasoningInNoOverlap2D_);
    }
    if (hasProbingNumCombinationsLimit_) {
      w.int32(272, probingNumCombinationsLimit_);
    }
    if (hasInprocessingDtimeRatio_) {
      w.dbl(273, inprocessingDtimeRatio_);
    }
    if (hasInprocessingProbingDtime_) {
      w.dbl(274, inprocessingProbingDtime_);
    }
    if (hasInprocessingMinimizationDtime_) {
      w.dbl(275, inprocessingMinimizationDtime_);
    }
    if (hasMaxPairsPairwiseReasoningInNoOverlap2D_) {
      w.int32(276, maxPairsPairwiseReasoningInNoOverlap2D_);
    }
    if (hasDetectLinearizedProduct_) {
      w.bool(277, detectLinearizedProduct_);
    }
    if (hasMipTreatHighMagnitudeBoundsAsInfinity_) {
      w.bool(278, mipTreatHighMagnitudeBoundsAsInfinity_);
    }
    if (hasAddRltCuts_) {
      w.bool(279, addRltCuts_);
    }
    if (hasMaxLinMaxSizeForExpansion_) {
      w.int32(280, maxLinMaxSizeForExpansion_);
    }
    if (hasSharedTreeOpenLeavesPerWorker_) {
      w.dbl(281, sharedTreeOpenLeavesPerWorker_);
    }
    if (hasSharedTreeWorkerMinRestartsPerSubtree_) {
      w.int32(282, sharedTreeWorkerMinRestartsPerSubtree_);
    }
    if (hasUseLns_) {
      w.bool(283, useLns_);
    }
    if (hasSaveLpBasisInLbTreeSearch_) {
      w.bool(284, saveLpBasisInLbTreeSearch_);
    }
    if (hasShareGlueClauses_) {
      w.bool(285, shareGlueClauses_);
    }
    if (hasUseConservativeScaleOverloadChecker_) {
      w.bool(286, useConservativeScaleOverloadChecker_);
    }
    if (hasEncodeCumulativeAsReservoir_) {
      w.bool(287, encodeCumulativeAsReservoir_);
    }
    if (hasExpandReservoirUsingCircuit_) {
      w.bool(288, expandReservoirUsingCircuit_);
    }
    if (hasVariablesShavingLevel_) {
      w.int32(289, variablesShavingLevel_);
    }
    if (hasShavingSearchThreshold_) {
      w.int64(290, shavingSearchThreshold_);
    }
    if (hasMaxNumDeterministicBatches_) {
      w.int32(291, maxNumDeterministicBatches_);
    }
    if (hasFeasibilityJumpBatchDtime_) {
      w.dbl(292, feasibilityJumpBatchDtime_);
    }
    if (filterSubsolvers_ != null) {
      for (String v : filterSubsolvers_) {
        w.string(293, v);
      }
    }
    if (hasNumFullSubsolvers_) {
      w.int32(294, numFullSubsolvers_);
    }
    if (hasSharedTreeWorkerEnableTrailSharing_) {
      w.bool(295, sharedTreeWorkerEnableTrailSharing_);
    }
    if (hasLbRelaxNumWorkersThreshold_) {
      w.int32(296, lbRelaxNumWorkersThreshold_);
    }
    if (hasInprocessingMinimizationUseConflictAnalysis_) {
      w.bool(297, inprocessingMinimizationUseConflictAnalysis_);
    }
    if (hasUseTryEdgeReasoningInNoOverlap2D_) {
      w.bool(299, useTryEdgeReasoningInNoOverlap2D_);
    }
    if (hasMinimizeSharedClauses_) {
      w.bool(300, minimizeSharedClauses_);
    }
    if (hasUseSymmetryInLp_) {
      w.bool(301, useSymmetryInLp_);
    }
    if (hasSymmetryDetectionDeterministicTimeLimit_) {
      w.dbl(302, symmetryDetectionDeterministicTimeLimit_);
    }
    if (hasKeepSymmetryInPresolve_) {
      w.bool(303, keepSymmetryInPresolve_);
    }
    if (hasSharedTreeWorkerEnablePhaseSharing_) {
      w.bool(304, sharedTreeWorkerEnablePhaseSharing_);
    }
    if (hasSharedTreeBalanceTolerance_) {
      w.int32(305, sharedTreeBalanceTolerance_);
    }
    if (hasDebugCrashIfPresolveBreaksHint_) {
      w.bool(306, debugCrashIfPresolveBreaksHint_);
    }
    if (hasLnsInitialDifficulty_) {
      w.dbl(307, lnsInitialDifficulty_);
    }
    if (hasLnsInitialDeterministicLimit_) {
      w.dbl(308, lnsInitialDeterministicLimit_);
    }
    if (hasPolarityExploitLsHints_) {
      w.bool(309, polarityExploitLsHints_);
    }
    if (hasRemoveFixedVariablesEarly_) {
      w.bool(310, removeFixedVariablesEarly_);
    }
    if (hasUseAllDifferentForCircuit_) {
      w.bool(311, useAllDifferentForCircuit_);
    }
    if (hasRoutingCutSubsetSizeForBinaryRelationBound_) {
      w.int32(312, routingCutSubsetSizeForBinaryRelationBound_);
    }
    if (hasRoutingCutSubsetSizeForTightBinaryRelationBound_) {
      w.int32(313, routingCutSubsetSizeForTightBinaryRelationBound_);
    }
    if (hasRoutingCutDpEffort_) {
      w.dbl(314, routingCutDpEffort_);
    }
    if (hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_) {
      w.int32(315, maximumRegionsToSplitInDisconnectedNoOverlap2D_);
    }
    if (hasRoutingCutSubsetSizeForExactBinaryRelationBound_) {
      w.int32(316, routingCutSubsetSizeForExactBinaryRelationBound_);
    }
    if (hasRoutingCutMaxInfeasiblePathLength_) {
      w.int32(317, routingCutMaxInfeasiblePathLength_);
    }
    if (hasRoutingCutSubsetSizeForShortestPathsBound_) {
      w.int32(318, routingCutSubsetSizeForShortestPathsBound_);
    }
    if (hasMaxAlldiffDomainSize_) {
      w.int32(320, maxAlldiffDomainSize_);
    }
    if (hasNoOverlap2DBooleanRelationsLimit_) {
      w.int32(321, noOverlap2DBooleanRelationsLimit_);
    }
    if (hasShareGlueClausesDtime_) {
      w.dbl(322, shareGlueClausesDtime_);
    }
    if (hasUseLinear3ForNoOverlap2DPrecedences_) {
      w.bool(323, useLinear3ForNoOverlap2DPrecedences_);
    }
    if (hasFilterSatPostsolveClauses_) {
      w.bool(324, filterSatPostsolveClauses_);
    }
    if (hasAlternativePoolSize_) {
      w.int32(325, alternativePoolSize_);
    }
    if (hasShareLinear2Bounds_) {
      w.bool(326, shareLinear2Bounds_);
    }
    if (hasTransitivePrecedencesWorkLimit_) {
      w.int32(327, transitivePrecedencesWorkLimit_);
    }
    if (hasSharedTreeSplitMinDtime_) {
      w.dbl(328, sharedTreeSplitMinDtime_);
    }
    if (hasSolutionPoolDiversityLimit_) {
      w.int32(329, solutionPoolDiversityLimit_);
    }
    if (hasUseChronologicalBacktracking_) {
      w.bool(330, useChronologicalBacktracking_);
    }
    if (hasMaxBackjumpLevels_) {
      w.int32(331, maxBackjumpLevels_);
    }
    if (hasChronologicalBacktrackMinConflicts_) {
      w.int32(332, chronologicalBacktrackMinConflicts_);
    }
    if (hasFindClausesThatAreExactlyOne_) {
      w.bool(333, findClausesThatAreExactlyOne_);
    }
    if (hasLoadAtMostOnesInSatPresolve_) {
      w.bool(335, loadAtMostOnesInSatPresolve_);
    }
    if (hasUseNewIntegerConflictResolution_) {
      w.bool(336, useNewIntegerConflictResolution_);
    }
    if (hasClauseCleanupPeriodIncrement_) {
      w.int32(337, clauseCleanupPeriodIncrement_);
    }
    if (hasDebugCrashIfLratCheckFails_) {
      w.bool(339, debugCrashIfLratCheckFails_);
    }
    if (hasMaxDomainSizeForLinear2Expansion_) {
      w.int32(340, maxDomainSizeForLinear2Expansion_);
    }
    if (hasCreate1UipBooleanDuringIcr_) {
      w.bool(341, create1UipBooleanDuringIcr_);
    }
    if (hasInprocessingUseCongruenceClosure_) {
      w.bool(342, inprocessingUseCongruenceClosure_);
    }
    if (hasEagerlySubsumeLastNConflicts_) {
      w.int32(343, eagerlySubsumeLastNConflicts_);
    }
    if (hasCheckLratProof_) {
      w.bool(344, checkLratProof_);
    }
    if (hasOutputLratProof_) {
      w.bool(345, outputLratProof_);
    }
    if (hasClauseCleanupLbdTier1_) {
      w.int32(349, clauseCleanupLbdTier1_);
    }
    if (hasClauseCleanupLbdTier2_) {
      w.int32(350, clauseCleanupLbdTier2_);
    }
    if (hasExtraSubsumptionDuringConflictAnalysis_) {
      w.bool(351, extraSubsumptionDuringConflictAnalysis_);
    }
    if (hasCheckMergedLratProof_) {
      w.bool(352, checkMergedLratProof_);
    }
    if (hasDecisionSubsumptionDuringConflictAnalysis_) {
      w.bool(353, decisionSubsumptionDuringConflictAnalysis_);
    }
    if (hasInprocessingUseSatSweeping_) {
      w.bool(354, inprocessingUseSatSweeping_);
    }
    if (hasSubsumeDuringVivification_) {
      w.bool(355, subsumeDuringVivification_);
    }
    if (hasNumOrderingInVivification_) {
      w.int32(356, numOrderingInVivification_);
    }
    if (hasDetectEncodedComplexDomain_) {
      w.bool(357, detectEncodedComplexDomain_);
    }
    if (hasMipScaleToMaxBound_) {
      w.bool(358, mipScaleToMaxBound_);
    }
    if (hasMipPresolveRestrictBounds_) {
      w.bool(359, mipPresolveRestrictBounds_);
    }
    if (hasCpModelPureSatPresolve_) {
      w.bool(360, cpModelPureSatPresolve_);
    }
    if (hasInprocessingUseTransitiveReduction_) {
      w.bool(361, inprocessingUseTransitiveReduction_);
    }
    if (hasInprocessingDetectAndSweepCircuit_) {
      w.bool(362, inprocessingDetectAndSweepCircuit_);
    }
    if (hasTryHintAsAssumptions_) {
      w.bool(363, tryHintAsAssumptions_);
    }
    if (hasLearnOnAssumptionsUnsat_) {
      w.bool(364, learnOnAssumptionsUnsat_);
    }
    if (hasSharedTreeWorkerPropagateIntermediateNodes_) {
      w.bool(365, sharedTreeWorkerPropagateIntermediateNodes_);
    }
    if (hasShavingSearchUseRandomSearch_) {
      w.bool(366, shavingSearchUseRandomSearch_);
    }
    if (hasShavingSearchRestartLimit_) {
      w.int32(367, shavingSearchRestartLimit_);
    }
    if (hasObjectiveShavingSearchThreshold_) {
      w.int64(368, objectiveShavingSearchThreshold_);
    }
    if (hasCpModelDirectSolve_) {
      w.bool(369, cpModelDirectSolve_);
    }
    if (hasMipUseTightBounds_) {
      w.bool(370, mipUseTightBounds_);
    }
    if (hasMipTightPrecision_) {
      w.dbl(371, mipTightPrecision_);
    }
    if (hasMipVerifySolution_) {
      w.bool(372, mipVerifySolution_);
    }
    if (hasMipProperRelaxation_) {
      w.bool(373, mipProperRelaxation_);
    }
    if (hasFeasibilityJumpIgnoreRedundantConstraints_) {
      w.bool(715, feasibilityJumpIgnoreRedundantConstraints_);
    }
    if (hasLnsIgnoreRedundantConstraints_) {
      w.bool(716, lnsIgnoreRedundantConstraints_);
    }
  }

  /** Returns the message serialized in the protobuf binary format. */
  public byte[] toByteArray() {
    ProtoWire.Writer w = new ProtoWire.Writer();
    writeTo(w);
    return w.toByteArray();
  }

  /**
   * Parses a message in the protobuf binary format. Unknown fields are skipped.
   *
   * @throws InvalidProtocolBufferException if the data is not a valid serialized message
   */
  public static SatParameters parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return newBuilder().mergeFrom(data).build();
  }

  /** Returns a new builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Returns a new builder initialized with a copy of {@code prototype}. */
  public static Builder newBuilder(SatParameters prototype) {
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
   * Builder of {@link SatParameters}. A builder is also a (mutable) {@code SatParameters}, so all getters are
   * available on it.
   */
  public static final class Builder extends SatParameters implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public SatParameters build() {
      SatParameters r = new SatParameters();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public SatParameters buildPartial() {
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
    public Builder mergeFrom(SatParameters other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.hasName_) {
        setName(other.name_);
      }
      if (other.hasPreferredVariableOrder_) {
        setPreferredVariableOrderValue(other.preferredVariableOrder_);
      }
      if (other.hasInitialPolarity_) {
        setInitialPolarityValue(other.initialPolarity_);
      }
      if (other.hasUsePhaseSaving_) {
        setUsePhaseSaving(other.usePhaseSaving_);
      }
      if (other.hasPolarityRephaseIncrement_) {
        setPolarityRephaseIncrement(other.polarityRephaseIncrement_);
      }
      if (other.hasPolarityExploitLsHints_) {
        setPolarityExploitLsHints(other.polarityExploitLsHints_);
      }
      if (other.hasRandomPolarityRatio_) {
        setRandomPolarityRatio(other.randomPolarityRatio_);
      }
      if (other.hasRandomBranchesRatio_) {
        setRandomBranchesRatio(other.randomBranchesRatio_);
      }
      if (other.hasUseErwaHeuristic_) {
        setUseErwaHeuristic(other.useErwaHeuristic_);
      }
      if (other.hasInitialVariablesActivity_) {
        setInitialVariablesActivity(other.initialVariablesActivity_);
      }
      if (other.hasAlsoBumpVariablesInConflictReasons_) {
        setAlsoBumpVariablesInConflictReasons(other.alsoBumpVariablesInConflictReasons_);
      }
      if (other.hasMinimizationAlgorithm_) {
        setMinimizationAlgorithmValue(other.minimizationAlgorithm_);
      }
      if (other.hasBinaryMinimizationAlgorithm_) {
        setBinaryMinimizationAlgorithmValue(other.binaryMinimizationAlgorithm_);
      }
      if (other.hasSubsumptionDuringConflictAnalysis_) {
        setSubsumptionDuringConflictAnalysis(other.subsumptionDuringConflictAnalysis_);
      }
      if (other.hasExtraSubsumptionDuringConflictAnalysis_) {
        setExtraSubsumptionDuringConflictAnalysis(other.extraSubsumptionDuringConflictAnalysis_);
      }
      if (other.hasDecisionSubsumptionDuringConflictAnalysis_) {
        setDecisionSubsumptionDuringConflictAnalysis(other.decisionSubsumptionDuringConflictAnalysis_);
      }
      if (other.hasEagerlySubsumeLastNConflicts_) {
        setEagerlySubsumeLastNConflicts(other.eagerlySubsumeLastNConflicts_);
      }
      if (other.hasSubsumeDuringVivification_) {
        setSubsumeDuringVivification(other.subsumeDuringVivification_);
      }
      if (other.hasUseChronologicalBacktracking_) {
        setUseChronologicalBacktracking(other.useChronologicalBacktracking_);
      }
      if (other.hasMaxBackjumpLevels_) {
        setMaxBackjumpLevels(other.maxBackjumpLevels_);
      }
      if (other.hasChronologicalBacktrackMinConflicts_) {
        setChronologicalBacktrackMinConflicts(other.chronologicalBacktrackMinConflicts_);
      }
      if (other.hasLearnOnAssumptionsUnsat_) {
        setLearnOnAssumptionsUnsat(other.learnOnAssumptionsUnsat_);
      }
      if (other.hasClauseCleanupPeriod_) {
        setClauseCleanupPeriod(other.clauseCleanupPeriod_);
      }
      if (other.hasClauseCleanupPeriodIncrement_) {
        setClauseCleanupPeriodIncrement(other.clauseCleanupPeriodIncrement_);
      }
      if (other.hasClauseCleanupTarget_) {
        setClauseCleanupTarget(other.clauseCleanupTarget_);
      }
      if (other.hasClauseCleanupRatio_) {
        setClauseCleanupRatio(other.clauseCleanupRatio_);
      }
      if (other.hasClauseCleanupLbdBound_) {
        setClauseCleanupLbdBound(other.clauseCleanupLbdBound_);
      }
      if (other.hasClauseCleanupLbdTier1_) {
        setClauseCleanupLbdTier1(other.clauseCleanupLbdTier1_);
      }
      if (other.hasClauseCleanupLbdTier2_) {
        setClauseCleanupLbdTier2(other.clauseCleanupLbdTier2_);
      }
      if (other.hasClauseCleanupOrdering_) {
        setClauseCleanupOrderingValue(other.clauseCleanupOrdering_);
      }
      if (other.hasPbCleanupIncrement_) {
        setPbCleanupIncrement(other.pbCleanupIncrement_);
      }
      if (other.hasPbCleanupRatio_) {
        setPbCleanupRatio(other.pbCleanupRatio_);
      }
      if (other.hasVariableActivityDecay_) {
        setVariableActivityDecay(other.variableActivityDecay_);
      }
      if (other.hasMaxVariableActivityValue_) {
        setMaxVariableActivityValue(other.maxVariableActivityValue_);
      }
      if (other.hasGlucoseMaxDecay_) {
        setGlucoseMaxDecay(other.glucoseMaxDecay_);
      }
      if (other.hasGlucoseDecayIncrement_) {
        setGlucoseDecayIncrement(other.glucoseDecayIncrement_);
      }
      if (other.hasGlucoseDecayIncrementPeriod_) {
        setGlucoseDecayIncrementPeriod(other.glucoseDecayIncrementPeriod_);
      }
      if (other.hasClauseActivityDecay_) {
        setClauseActivityDecay(other.clauseActivityDecay_);
      }
      if (other.hasMaxClauseActivityValue_) {
        setMaxClauseActivityValue(other.maxClauseActivityValue_);
      }
      if (other.restartAlgorithms_ != null) {
        addAllRestartAlgorithms(other.restartAlgorithms_);
      }
      if (other.hasDefaultRestartAlgorithms_) {
        setDefaultRestartAlgorithms(other.defaultRestartAlgorithms_);
      }
      if (other.hasRestartPeriod_) {
        setRestartPeriod(other.restartPeriod_);
      }
      if (other.hasRestartRunningWindowSize_) {
        setRestartRunningWindowSize(other.restartRunningWindowSize_);
      }
      if (other.hasRestartDlAverageRatio_) {
        setRestartDlAverageRatio(other.restartDlAverageRatio_);
      }
      if (other.hasRestartLbdAverageRatio_) {
        setRestartLbdAverageRatio(other.restartLbdAverageRatio_);
      }
      if (other.hasUseBlockingRestart_) {
        setUseBlockingRestart(other.useBlockingRestart_);
      }
      if (other.hasBlockingRestartWindowSize_) {
        setBlockingRestartWindowSize(other.blockingRestartWindowSize_);
      }
      if (other.hasBlockingRestartMultiplier_) {
        setBlockingRestartMultiplier(other.blockingRestartMultiplier_);
      }
      if (other.hasNumConflictsBeforeStrategyChanges_) {
        setNumConflictsBeforeStrategyChanges(other.numConflictsBeforeStrategyChanges_);
      }
      if (other.hasStrategyChangeIncreaseRatio_) {
        setStrategyChangeIncreaseRatio(other.strategyChangeIncreaseRatio_);
      }
      if (other.hasMaxTimeInSeconds_) {
        setMaxTimeInSeconds(other.maxTimeInSeconds_);
      }
      if (other.hasMaxDeterministicTime_) {
        setMaxDeterministicTime(other.maxDeterministicTime_);
      }
      if (other.hasMaxNumDeterministicBatches_) {
        setMaxNumDeterministicBatches(other.maxNumDeterministicBatches_);
      }
      if (other.hasMaxNumberOfConflicts_) {
        setMaxNumberOfConflicts(other.maxNumberOfConflicts_);
      }
      if (other.hasMaxMemoryInMb_) {
        setMaxMemoryInMb(other.maxMemoryInMb_);
      }
      if (other.hasAbsoluteGapLimit_) {
        setAbsoluteGapLimit(other.absoluteGapLimit_);
      }
      if (other.hasRelativeGapLimit_) {
        setRelativeGapLimit(other.relativeGapLimit_);
      }
      if (other.hasRandomSeed_) {
        setRandomSeed(other.randomSeed_);
      }
      if (other.hasPermuteVariableRandomly_) {
        setPermuteVariableRandomly(other.permuteVariableRandomly_);
      }
      if (other.hasPermutePresolveConstraintOrder_) {
        setPermutePresolveConstraintOrder(other.permutePresolveConstraintOrder_);
      }
      if (other.hasUseAbslRandom_) {
        setUseAbslRandom(other.useAbslRandom_);
      }
      if (other.hasLogSearchProgress_) {
        setLogSearchProgress(other.logSearchProgress_);
      }
      if (other.hasLogSubsolverStatistics_) {
        setLogSubsolverStatistics(other.logSubsolverStatistics_);
      }
      if (other.hasLogPrefix_) {
        setLogPrefix(other.logPrefix_);
      }
      if (other.hasLogToStdout_) {
        setLogToStdout(other.logToStdout_);
      }
      if (other.hasLogToResponse_) {
        setLogToResponse(other.logToResponse_);
      }
      if (other.hasUsePbResolution_) {
        setUsePbResolution(other.usePbResolution_);
      }
      if (other.hasMinimizeReductionDuringPbResolution_) {
        setMinimizeReductionDuringPbResolution(other.minimizeReductionDuringPbResolution_);
      }
      if (other.hasCountAssumptionLevelsInLbd_) {
        setCountAssumptionLevelsInLbd(other.countAssumptionLevelsInLbd_);
      }
      if (other.hasPresolveBveThreshold_) {
        setPresolveBveThreshold(other.presolveBveThreshold_);
      }
      if (other.hasFilterSatPostsolveClauses_) {
        setFilterSatPostsolveClauses(other.filterSatPostsolveClauses_);
      }
      if (other.hasPresolveBveClauseWeight_) {
        setPresolveBveClauseWeight(other.presolveBveClauseWeight_);
      }
      if (other.hasProbingDeterministicTimeLimit_) {
        setProbingDeterministicTimeLimit(other.probingDeterministicTimeLimit_);
      }
      if (other.hasPresolveProbingDeterministicTimeLimit_) {
        setPresolveProbingDeterministicTimeLimit(other.presolveProbingDeterministicTimeLimit_);
      }
      if (other.hasPresolveBlockedClause_) {
        setPresolveBlockedClause(other.presolveBlockedClause_);
      }
      if (other.hasPresolveUseBva_) {
        setPresolveUseBva(other.presolveUseBva_);
      }
      if (other.hasPresolveBvaThreshold_) {
        setPresolveBvaThreshold(other.presolveBvaThreshold_);
      }
      if (other.hasMaxPresolveIterations_) {
        setMaxPresolveIterations(other.maxPresolveIterations_);
      }
      if (other.hasCpModelPresolve_) {
        setCpModelPresolve(other.cpModelPresolve_);
      }
      if (other.hasCpModelDirectSolve_) {
        setCpModelDirectSolve(other.cpModelDirectSolve_);
      }
      if (other.hasCpModelProbingLevel_) {
        setCpModelProbingLevel(other.cpModelProbingLevel_);
      }
      if (other.hasCpModelUseSatPresolve_) {
        setCpModelUseSatPresolve(other.cpModelUseSatPresolve_);
      }
      if (other.hasCpModelPureSatPresolve_) {
        setCpModelPureSatPresolve(other.cpModelPureSatPresolve_);
      }
      if (other.hasLoadAtMostOnesInSatPresolve_) {
        setLoadAtMostOnesInSatPresolve(other.loadAtMostOnesInSatPresolve_);
      }
      if (other.hasRemoveFixedVariablesEarly_) {
        setRemoveFixedVariablesEarly(other.removeFixedVariablesEarly_);
      }
      if (other.hasDetectTableWithCost_) {
        setDetectTableWithCost(other.detectTableWithCost_);
      }
      if (other.hasTableCompressionLevel_) {
        setTableCompressionLevel(other.tableCompressionLevel_);
      }
      if (other.hasExpandAlldiffConstraints_) {
        setExpandAlldiffConstraints(other.expandAlldiffConstraints_);
      }
      if (other.hasMaxAlldiffDomainSize_) {
        setMaxAlldiffDomainSize(other.maxAlldiffDomainSize_);
      }
      if (other.hasExpandReservoirConstraints_) {
        setExpandReservoirConstraints(other.expandReservoirConstraints_);
      }
      if (other.hasMaxDomainSizeForLinear2Expansion_) {
        setMaxDomainSizeForLinear2Expansion(other.maxDomainSizeForLinear2Expansion_);
      }
      if (other.hasExpandReservoirUsingCircuit_) {
        setExpandReservoirUsingCircuit(other.expandReservoirUsingCircuit_);
      }
      if (other.hasEncodeCumulativeAsReservoir_) {
        setEncodeCumulativeAsReservoir(other.encodeCumulativeAsReservoir_);
      }
      if (other.hasMaxLinMaxSizeForExpansion_) {
        setMaxLinMaxSizeForExpansion(other.maxLinMaxSizeForExpansion_);
      }
      if (other.hasDisableConstraintExpansion_) {
        setDisableConstraintExpansion(other.disableConstraintExpansion_);
      }
      if (other.hasEncodeComplexLinearConstraintWithInteger_) {
        setEncodeComplexLinearConstraintWithInteger(other.encodeComplexLinearConstraintWithInteger_);
      }
      if (other.hasDetectEncodedComplexDomain_) {
        setDetectEncodedComplexDomain(other.detectEncodedComplexDomain_);
      }
      if (other.hasMergeNoOverlapWorkLimit_) {
        setMergeNoOverlapWorkLimit(other.mergeNoOverlapWorkLimit_);
      }
      if (other.hasMergeAtMostOneWorkLimit_) {
        setMergeAtMostOneWorkLimit(other.mergeAtMostOneWorkLimit_);
      }
      if (other.hasPresolveSubstitutionLevel_) {
        setPresolveSubstitutionLevel(other.presolveSubstitutionLevel_);
      }
      if (other.hasPresolveExtractIntegerEnforcement_) {
        setPresolveExtractIntegerEnforcement(other.presolveExtractIntegerEnforcement_);
      }
      if (other.hasPresolveInclusionWorkLimit_) {
        setPresolveInclusionWorkLimit(other.presolveInclusionWorkLimit_);
      }
      if (other.hasIgnoreNames_) {
        setIgnoreNames(other.ignoreNames_);
      }
      if (other.hasInferAllDiffs_) {
        setInferAllDiffs(other.inferAllDiffs_);
      }
      if (other.hasFindBigLinearOverlap_) {
        setFindBigLinearOverlap(other.findBigLinearOverlap_);
      }
      if (other.hasFindClausesThatAreExactlyOne_) {
        setFindClausesThatAreExactlyOne(other.findClausesThatAreExactlyOne_);
      }
      if (other.hasUseSatInprocessing_) {
        setUseSatInprocessing(other.useSatInprocessing_);
      }
      if (other.hasInprocessingDtimeRatio_) {
        setInprocessingDtimeRatio(other.inprocessingDtimeRatio_);
      }
      if (other.hasInprocessingProbingDtime_) {
        setInprocessingProbingDtime(other.inprocessingProbingDtime_);
      }
      if (other.hasInprocessingMinimizationDtime_) {
        setInprocessingMinimizationDtime(other.inprocessingMinimizationDtime_);
      }
      if (other.hasInprocessingMinimizationUseConflictAnalysis_) {
        setInprocessingMinimizationUseConflictAnalysis(other.inprocessingMinimizationUseConflictAnalysis_);
      }
      if (other.hasNumOrderingInVivification_) {
        setNumOrderingInVivification(other.numOrderingInVivification_);
      }
      if (other.hasInprocessingUseCongruenceClosure_) {
        setInprocessingUseCongruenceClosure(other.inprocessingUseCongruenceClosure_);
      }
      if (other.hasInprocessingDetectAndSweepCircuit_) {
        setInprocessingDetectAndSweepCircuit(other.inprocessingDetectAndSweepCircuit_);
      }
      if (other.hasInprocessingUseSatSweeping_) {
        setInprocessingUseSatSweeping(other.inprocessingUseSatSweeping_);
      }
      if (other.hasInprocessingUseTransitiveReduction_) {
        setInprocessingUseTransitiveReduction(other.inprocessingUseTransitiveReduction_);
      }
      if (other.hasNumWorkers_) {
        setNumWorkers(other.numWorkers_);
      }
      if (other.hasNumSearchWorkers_) {
        setNumSearchWorkers(other.numSearchWorkers_);
      }
      if (other.hasNumFullSubsolvers_) {
        setNumFullSubsolvers(other.numFullSubsolvers_);
      }
      if (other.subsolvers_ != null) {
        addAllSubsolvers(other.subsolvers_);
      }
      if (other.extraSubsolvers_ != null) {
        addAllExtraSubsolvers(other.extraSubsolvers_);
      }
      if (other.ignoreSubsolvers_ != null) {
        addAllIgnoreSubsolvers(other.ignoreSubsolvers_);
      }
      if (other.filterSubsolvers_ != null) {
        addAllFilterSubsolvers(other.filterSubsolvers_);
      }
      if (other.subsolverParams_ != null) {
        addAllSubsolverParams(other.subsolverParams_);
      }
      if (other.hasInterleaveSearch_) {
        setInterleaveSearch(other.interleaveSearch_);
      }
      if (other.hasInterleaveBatchSize_) {
        setInterleaveBatchSize(other.interleaveBatchSize_);
      }
      if (other.hasShareObjectiveBounds_) {
        setShareObjectiveBounds(other.shareObjectiveBounds_);
      }
      if (other.hasShareLevelZeroBounds_) {
        setShareLevelZeroBounds(other.shareLevelZeroBounds_);
      }
      if (other.hasShareLinear2Bounds_) {
        setShareLinear2Bounds(other.shareLinear2Bounds_);
      }
      if (other.hasShareBinaryClauses_) {
        setShareBinaryClauses(other.shareBinaryClauses_);
      }
      if (other.hasShareGlueClauses_) {
        setShareGlueClauses(other.shareGlueClauses_);
      }
      if (other.hasMinimizeSharedClauses_) {
        setMinimizeSharedClauses(other.minimizeSharedClauses_);
      }
      if (other.hasShareGlueClausesDtime_) {
        setShareGlueClausesDtime(other.shareGlueClausesDtime_);
      }
      if (other.hasCheckLratProof_) {
        setCheckLratProof(other.checkLratProof_);
      }
      if (other.hasCheckMergedLratProof_) {
        setCheckMergedLratProof(other.checkMergedLratProof_);
      }
      if (other.hasOutputLratProof_) {
        setOutputLratProof(other.outputLratProof_);
      }
      if (other.hasDebugPostsolveWithFullSolver_) {
        setDebugPostsolveWithFullSolver(other.debugPostsolveWithFullSolver_);
      }
      if (other.hasDebugMaxNumPresolveOperations_) {
        setDebugMaxNumPresolveOperations(other.debugMaxNumPresolveOperations_);
      }
      if (other.hasDebugCrashOnBadHint_) {
        setDebugCrashOnBadHint(other.debugCrashOnBadHint_);
      }
      if (other.hasDebugCrashIfPresolveBreaksHint_) {
        setDebugCrashIfPresolveBreaksHint(other.debugCrashIfPresolveBreaksHint_);
      }
      if (other.hasDebugCrashIfLratCheckFails_) {
        setDebugCrashIfLratCheckFails(other.debugCrashIfLratCheckFails_);
      }
      if (other.hasUseOptimizationHints_) {
        setUseOptimizationHints(other.useOptimizationHints_);
      }
      if (other.hasCoreMinimizationLevel_) {
        setCoreMinimizationLevel(other.coreMinimizationLevel_);
      }
      if (other.hasFindMultipleCores_) {
        setFindMultipleCores(other.findMultipleCores_);
      }
      if (other.hasCoverOptimization_) {
        setCoverOptimization(other.coverOptimization_);
      }
      if (other.hasMaxSatAssumptionOrder_) {
        setMaxSatAssumptionOrderValue(other.maxSatAssumptionOrder_);
      }
      if (other.hasMaxSatReverseAssumptionOrder_) {
        setMaxSatReverseAssumptionOrder(other.maxSatReverseAssumptionOrder_);
      }
      if (other.hasMaxSatStratification_) {
        setMaxSatStratificationValue(other.maxSatStratification_);
      }
      if (other.hasPropagationLoopDetectionFactor_) {
        setPropagationLoopDetectionFactor(other.propagationLoopDetectionFactor_);
      }
      if (other.hasUsePrecedencesInDisjunctiveConstraint_) {
        setUsePrecedencesInDisjunctiveConstraint(other.usePrecedencesInDisjunctiveConstraint_);
      }
      if (other.hasTransitivePrecedencesWorkLimit_) {
        setTransitivePrecedencesWorkLimit(other.transitivePrecedencesWorkLimit_);
      }
      if (other.hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_) {
        setMaxSizeToCreatePrecedenceLiteralsInDisjunctive(other.maxSizeToCreatePrecedenceLiteralsInDisjunctive_);
      }
      if (other.hasUseStrongPropagationInDisjunctive_) {
        setUseStrongPropagationInDisjunctive(other.useStrongPropagationInDisjunctive_);
      }
      if (other.hasUseDynamicPrecedenceInDisjunctive_) {
        setUseDynamicPrecedenceInDisjunctive(other.useDynamicPrecedenceInDisjunctive_);
      }
      if (other.hasUseDynamicPrecedenceInCumulative_) {
        setUseDynamicPrecedenceInCumulative(other.useDynamicPrecedenceInCumulative_);
      }
      if (other.hasUseOverloadCheckerInCumulative_) {
        setUseOverloadCheckerInCumulative(other.useOverloadCheckerInCumulative_);
      }
      if (other.hasUseConservativeScaleOverloadChecker_) {
        setUseConservativeScaleOverloadChecker(other.useConservativeScaleOverloadChecker_);
      }
      if (other.hasUseTimetableEdgeFindingInCumulative_) {
        setUseTimetableEdgeFindingInCumulative(other.useTimetableEdgeFindingInCumulative_);
      }
      if (other.hasMaxNumIntervalsForTimetableEdgeFinding_) {
        setMaxNumIntervalsForTimetableEdgeFinding(other.maxNumIntervalsForTimetableEdgeFinding_);
      }
      if (other.hasUseHardPrecedencesInCumulative_) {
        setUseHardPrecedencesInCumulative(other.useHardPrecedencesInCumulative_);
      }
      if (other.hasExploitAllPrecedences_) {
        setExploitAllPrecedences(other.exploitAllPrecedences_);
      }
      if (other.hasUseDisjunctiveConstraintInCumulative_) {
        setUseDisjunctiveConstraintInCumulative(other.useDisjunctiveConstraintInCumulative_);
      }
      if (other.hasNoOverlap2DBooleanRelationsLimit_) {
        setNoOverlap2DBooleanRelationsLimit(other.noOverlap2DBooleanRelationsLimit_);
      }
      if (other.hasUseTimetablingInNoOverlap2D_) {
        setUseTimetablingInNoOverlap2D(other.useTimetablingInNoOverlap2D_);
      }
      if (other.hasUseEnergeticReasoningInNoOverlap2D_) {
        setUseEnergeticReasoningInNoOverlap2D(other.useEnergeticReasoningInNoOverlap2D_);
      }
      if (other.hasUseAreaEnergeticReasoningInNoOverlap2D_) {
        setUseAreaEnergeticReasoningInNoOverlap2D(other.useAreaEnergeticReasoningInNoOverlap2D_);
      }
      if (other.hasUseTryEdgeReasoningInNoOverlap2D_) {
        setUseTryEdgeReasoningInNoOverlap2D(other.useTryEdgeReasoningInNoOverlap2D_);
      }
      if (other.hasMaxPairsPairwiseReasoningInNoOverlap2D_) {
        setMaxPairsPairwiseReasoningInNoOverlap2D(other.maxPairsPairwiseReasoningInNoOverlap2D_);
      }
      if (other.hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_) {
        setMaximumRegionsToSplitInDisconnectedNoOverlap2D(other.maximumRegionsToSplitInDisconnectedNoOverlap2D_);
      }
      if (other.hasUseLinear3ForNoOverlap2DPrecedences_) {
        setUseLinear3ForNoOverlap2DPrecedences(other.useLinear3ForNoOverlap2DPrecedences_);
      }
      if (other.hasUseDualSchedulingHeuristics_) {
        setUseDualSchedulingHeuristics(other.useDualSchedulingHeuristics_);
      }
      if (other.hasUseAllDifferentForCircuit_) {
        setUseAllDifferentForCircuit(other.useAllDifferentForCircuit_);
      }
      if (other.hasRoutingCutSubsetSizeForBinaryRelationBound_) {
        setRoutingCutSubsetSizeForBinaryRelationBound(other.routingCutSubsetSizeForBinaryRelationBound_);
      }
      if (other.hasRoutingCutSubsetSizeForTightBinaryRelationBound_) {
        setRoutingCutSubsetSizeForTightBinaryRelationBound(other.routingCutSubsetSizeForTightBinaryRelationBound_);
      }
      if (other.hasRoutingCutSubsetSizeForExactBinaryRelationBound_) {
        setRoutingCutSubsetSizeForExactBinaryRelationBound(other.routingCutSubsetSizeForExactBinaryRelationBound_);
      }
      if (other.hasRoutingCutSubsetSizeForShortestPathsBound_) {
        setRoutingCutSubsetSizeForShortestPathsBound(other.routingCutSubsetSizeForShortestPathsBound_);
      }
      if (other.hasRoutingCutDpEffort_) {
        setRoutingCutDpEffort(other.routingCutDpEffort_);
      }
      if (other.hasRoutingCutMaxInfeasiblePathLength_) {
        setRoutingCutMaxInfeasiblePathLength(other.routingCutMaxInfeasiblePathLength_);
      }
      if (other.hasSearchBranching_) {
        setSearchBranchingValue(other.searchBranching_);
      }
      if (other.hasHintConflictLimit_) {
        setHintConflictLimit(other.hintConflictLimit_);
      }
      if (other.hasTryHintAsAssumptions_) {
        setTryHintAsAssumptions(other.tryHintAsAssumptions_);
      }
      if (other.hasRepairHint_) {
        setRepairHint(other.repairHint_);
      }
      if (other.hasFixVariablesToTheirHintedValue_) {
        setFixVariablesToTheirHintedValue(other.fixVariablesToTheirHintedValue_);
      }
      if (other.hasUseProbingSearch_) {
        setUseProbingSearch(other.useProbingSearch_);
      }
      if (other.hasUseExtendedProbing_) {
        setUseExtendedProbing(other.useExtendedProbing_);
      }
      if (other.hasProbingNumCombinationsLimit_) {
        setProbingNumCombinationsLimit(other.probingNumCombinationsLimit_);
      }
      if (other.hasShavingDeterministicTimeInProbingSearch_) {
        setShavingDeterministicTimeInProbingSearch(other.shavingDeterministicTimeInProbingSearch_);
      }
      if (other.hasShavingSearchDeterministicTime_) {
        setShavingSearchDeterministicTime(other.shavingSearchDeterministicTime_);
      }
      if (other.hasShavingSearchUseRandomSearch_) {
        setShavingSearchUseRandomSearch(other.shavingSearchUseRandomSearch_);
      }
      if (other.hasShavingSearchRestartLimit_) {
        setShavingSearchRestartLimit(other.shavingSearchRestartLimit_);
      }
      if (other.hasShavingSearchThreshold_) {
        setShavingSearchThreshold(other.shavingSearchThreshold_);
      }
      if (other.hasObjectiveShavingSearchThreshold_) {
        setObjectiveShavingSearchThreshold(other.objectiveShavingSearchThreshold_);
      }
      if (other.hasUseObjectiveLbSearch_) {
        setUseObjectiveLbSearch(other.useObjectiveLbSearch_);
      }
      if (other.hasUseObjectiveShavingSearch_) {
        setUseObjectiveShavingSearch(other.useObjectiveShavingSearch_);
      }
      if (other.hasVariablesShavingLevel_) {
        setVariablesShavingLevel(other.variablesShavingLevel_);
      }
      if (other.hasPseudoCostReliabilityThreshold_) {
        setPseudoCostReliabilityThreshold(other.pseudoCostReliabilityThreshold_);
      }
      if (other.hasOptimizeWithCore_) {
        setOptimizeWithCore(other.optimizeWithCore_);
      }
      if (other.hasOptimizeWithLbTreeSearch_) {
        setOptimizeWithLbTreeSearch(other.optimizeWithLbTreeSearch_);
      }
      if (other.hasSaveLpBasisInLbTreeSearch_) {
        setSaveLpBasisInLbTreeSearch(other.saveLpBasisInLbTreeSearch_);
      }
      if (other.hasBinarySearchNumConflicts_) {
        setBinarySearchNumConflicts(other.binarySearchNumConflicts_);
      }
      if (other.hasOptimizeWithMaxHs_) {
        setOptimizeWithMaxHs(other.optimizeWithMaxHs_);
      }
      if (other.hasUseFeasibilityJump_) {
        setUseFeasibilityJump(other.useFeasibilityJump_);
      }
      if (other.hasUseLsOnly_) {
        setUseLsOnly(other.useLsOnly_);
      }
      if (other.hasFeasibilityJumpDecay_) {
        setFeasibilityJumpDecay(other.feasibilityJumpDecay_);
      }
      if (other.hasFeasibilityJumpLinearizationLevel_) {
        setFeasibilityJumpLinearizationLevel(other.feasibilityJumpLinearizationLevel_);
      }
      if (other.hasFeasibilityJumpRestartFactor_) {
        setFeasibilityJumpRestartFactor(other.feasibilityJumpRestartFactor_);
      }
      if (other.hasFeasibilityJumpBatchDtime_) {
        setFeasibilityJumpBatchDtime(other.feasibilityJumpBatchDtime_);
      }
      if (other.hasFeasibilityJumpVarRandomizationProbability_) {
        setFeasibilityJumpVarRandomizationProbability(other.feasibilityJumpVarRandomizationProbability_);
      }
      if (other.hasFeasibilityJumpVarPerburbationRangeRatio_) {
        setFeasibilityJumpVarPerburbationRangeRatio(other.feasibilityJumpVarPerburbationRangeRatio_);
      }
      if (other.hasFeasibilityJumpEnableRestarts_) {
        setFeasibilityJumpEnableRestarts(other.feasibilityJumpEnableRestarts_);
      }
      if (other.hasFeasibilityJumpMaxExpandedConstraintSize_) {
        setFeasibilityJumpMaxExpandedConstraintSize(other.feasibilityJumpMaxExpandedConstraintSize_);
      }
      if (other.hasFeasibilityJumpIgnoreRedundantConstraints_) {
        setFeasibilityJumpIgnoreRedundantConstraints(other.feasibilityJumpIgnoreRedundantConstraints_);
      }
      if (other.hasLnsIgnoreRedundantConstraints_) {
        setLnsIgnoreRedundantConstraints(other.lnsIgnoreRedundantConstraints_);
      }
      if (other.hasNumViolationLs_) {
        setNumViolationLs(other.numViolationLs_);
      }
      if (other.hasViolationLsPerturbationPeriod_) {
        setViolationLsPerturbationPeriod(other.violationLsPerturbationPeriod_);
      }
      if (other.hasSharedTreeNumWorkers_) {
        setSharedTreeNumWorkers(other.sharedTreeNumWorkers_);
      }
      if (other.hasUseSharedTreeSearch_) {
        setUseSharedTreeSearch(other.useSharedTreeSearch_);
      }
      if (other.hasSharedTreeWorkerMinRestartsPerSubtree_) {
        setSharedTreeWorkerMinRestartsPerSubtree(other.sharedTreeWorkerMinRestartsPerSubtree_);
      }
      if (other.hasSharedTreeWorkerEnableTrailSharing_) {
        setSharedTreeWorkerEnableTrailSharing(other.sharedTreeWorkerEnableTrailSharing_);
      }
      if (other.hasSharedTreeWorkerEnablePhaseSharing_) {
        setSharedTreeWorkerEnablePhaseSharing(other.sharedTreeWorkerEnablePhaseSharing_);
      }
      if (other.hasSharedTreeWorkerPropagateIntermediateNodes_) {
        setSharedTreeWorkerPropagateIntermediateNodes(other.sharedTreeWorkerPropagateIntermediateNodes_);
      }
      if (other.hasSharedTreeOpenLeavesPerWorker_) {
        setSharedTreeOpenLeavesPerWorker(other.sharedTreeOpenLeavesPerWorker_);
      }
      if (other.hasSharedTreeMaxNodesPerWorker_) {
        setSharedTreeMaxNodesPerWorker(other.sharedTreeMaxNodesPerWorker_);
      }
      if (other.hasSharedTreeSplitStrategy_) {
        setSharedTreeSplitStrategyValue(other.sharedTreeSplitStrategy_);
      }
      if (other.hasSharedTreeBalanceTolerance_) {
        setSharedTreeBalanceTolerance(other.sharedTreeBalanceTolerance_);
      }
      if (other.hasSharedTreeSplitMinDtime_) {
        setSharedTreeSplitMinDtime(other.sharedTreeSplitMinDtime_);
      }
      if (other.hasEnumerateAllSolutions_) {
        setEnumerateAllSolutions(other.enumerateAllSolutions_);
      }
      if (other.hasKeepAllFeasibleSolutionsInPresolve_) {
        setKeepAllFeasibleSolutionsInPresolve(other.keepAllFeasibleSolutionsInPresolve_);
      }
      if (other.hasFillTightenedDomainsInResponse_) {
        setFillTightenedDomainsInResponse(other.fillTightenedDomainsInResponse_);
      }
      if (other.hasFillAdditionalSolutionsInResponse_) {
        setFillAdditionalSolutionsInResponse(other.fillAdditionalSolutionsInResponse_);
      }
      if (other.hasInstantiateAllVariables_) {
        setInstantiateAllVariables(other.instantiateAllVariables_);
      }
      if (other.hasAutoDetectGreaterThanAtLeastOneOf_) {
        setAutoDetectGreaterThanAtLeastOneOf(other.autoDetectGreaterThanAtLeastOneOf_);
      }
      if (other.hasStopAfterFirstSolution_) {
        setStopAfterFirstSolution(other.stopAfterFirstSolution_);
      }
      if (other.hasStopAfterPresolve_) {
        setStopAfterPresolve(other.stopAfterPresolve_);
      }
      if (other.hasStopAfterRootPropagation_) {
        setStopAfterRootPropagation(other.stopAfterRootPropagation_);
      }
      if (other.hasLnsInitialDifficulty_) {
        setLnsInitialDifficulty(other.lnsInitialDifficulty_);
      }
      if (other.hasLnsInitialDeterministicLimit_) {
        setLnsInitialDeterministicLimit(other.lnsInitialDeterministicLimit_);
      }
      if (other.hasUseLns_) {
        setUseLns(other.useLns_);
      }
      if (other.hasUseLnsOnly_) {
        setUseLnsOnly(other.useLnsOnly_);
      }
      if (other.hasSolutionPoolSize_) {
        setSolutionPoolSize(other.solutionPoolSize_);
      }
      if (other.hasSolutionPoolDiversityLimit_) {
        setSolutionPoolDiversityLimit(other.solutionPoolDiversityLimit_);
      }
      if (other.hasAlternativePoolSize_) {
        setAlternativePoolSize(other.alternativePoolSize_);
      }
      if (other.hasUseRinsLns_) {
        setUseRinsLns(other.useRinsLns_);
      }
      if (other.hasUseFeasibilityPump_) {
        setUseFeasibilityPump(other.useFeasibilityPump_);
      }
      if (other.hasUseLbRelaxLns_) {
        setUseLbRelaxLns(other.useLbRelaxLns_);
      }
      if (other.hasLbRelaxNumWorkersThreshold_) {
        setLbRelaxNumWorkersThreshold(other.lbRelaxNumWorkersThreshold_);
      }
      if (other.hasFpRounding_) {
        setFpRoundingValue(other.fpRounding_);
      }
      if (other.hasDiversifyLnsParams_) {
        setDiversifyLnsParams(other.diversifyLnsParams_);
      }
      if (other.hasRandomizeSearch_) {
        setRandomizeSearch(other.randomizeSearch_);
      }
      if (other.hasSearchRandomVariablePoolSize_) {
        setSearchRandomVariablePoolSize(other.searchRandomVariablePoolSize_);
      }
      if (other.hasPushAllTasksTowardStart_) {
        setPushAllTasksTowardStart(other.pushAllTasksTowardStart_);
      }
      if (other.hasUseOptionalVariables_) {
        setUseOptionalVariables(other.useOptionalVariables_);
      }
      if (other.hasUseExactLpReason_) {
        setUseExactLpReason(other.useExactLpReason_);
      }
      if (other.hasUseCombinedNoOverlap_) {
        setUseCombinedNoOverlap(other.useCombinedNoOverlap_);
      }
      if (other.hasAtMostOneMaxExpansionSize_) {
        setAtMostOneMaxExpansionSize(other.atMostOneMaxExpansionSize_);
      }
      if (other.hasCatchSigintSignal_) {
        setCatchSigintSignal(other.catchSigintSignal_);
      }
      if (other.hasUseImpliedBounds_) {
        setUseImpliedBounds(other.useImpliedBounds_);
      }
      if (other.hasPolishLpSolution_) {
        setPolishLpSolution(other.polishLpSolution_);
      }
      if (other.hasLpPrimalTolerance_) {
        setLpPrimalTolerance(other.lpPrimalTolerance_);
      }
      if (other.hasLpDualTolerance_) {
        setLpDualTolerance(other.lpDualTolerance_);
      }
      if (other.hasConvertIntervals_) {
        setConvertIntervals(other.convertIntervals_);
      }
      if (other.hasSymmetryLevel_) {
        setSymmetryLevel(other.symmetryLevel_);
      }
      if (other.hasUseSymmetryInLp_) {
        setUseSymmetryInLp(other.useSymmetryInLp_);
      }
      if (other.hasKeepSymmetryInPresolve_) {
        setKeepSymmetryInPresolve(other.keepSymmetryInPresolve_);
      }
      if (other.hasSymmetryDetectionDeterministicTimeLimit_) {
        setSymmetryDetectionDeterministicTimeLimit(other.symmetryDetectionDeterministicTimeLimit_);
      }
      if (other.hasNewLinearPropagation_) {
        setNewLinearPropagation(other.newLinearPropagation_);
      }
      if (other.hasLinearSplitSize_) {
        setLinearSplitSize(other.linearSplitSize_);
      }
      if (other.hasLinearizationLevel_) {
        setLinearizationLevel(other.linearizationLevel_);
      }
      if (other.hasBooleanEncodingLevel_) {
        setBooleanEncodingLevel(other.booleanEncodingLevel_);
      }
      if (other.hasMaxDomainSizeWhenEncodingEqNeqConstraints_) {
        setMaxDomainSizeWhenEncodingEqNeqConstraints(other.maxDomainSizeWhenEncodingEqNeqConstraints_);
      }
      if (other.hasMaxNumCuts_) {
        setMaxNumCuts(other.maxNumCuts_);
      }
      if (other.hasCutLevel_) {
        setCutLevel(other.cutLevel_);
      }
      if (other.hasOnlyAddCutsAtLevelZero_) {
        setOnlyAddCutsAtLevelZero(other.onlyAddCutsAtLevelZero_);
      }
      if (other.hasAddObjectiveCut_) {
        setAddObjectiveCut(other.addObjectiveCut_);
      }
      if (other.hasAddCgCuts_) {
        setAddCgCuts(other.addCgCuts_);
      }
      if (other.hasAddMirCuts_) {
        setAddMirCuts(other.addMirCuts_);
      }
      if (other.hasAddZeroHalfCuts_) {
        setAddZeroHalfCuts(other.addZeroHalfCuts_);
      }
      if (other.hasAddCliqueCuts_) {
        setAddCliqueCuts(other.addCliqueCuts_);
      }
      if (other.hasAddRltCuts_) {
        setAddRltCuts(other.addRltCuts_);
      }
      if (other.hasMaxAllDiffCutSize_) {
        setMaxAllDiffCutSize(other.maxAllDiffCutSize_);
      }
      if (other.hasAddLinMaxCuts_) {
        setAddLinMaxCuts(other.addLinMaxCuts_);
      }
      if (other.hasMaxIntegerRoundingScaling_) {
        setMaxIntegerRoundingScaling(other.maxIntegerRoundingScaling_);
      }
      if (other.hasAddLpConstraintsLazily_) {
        setAddLpConstraintsLazily(other.addLpConstraintsLazily_);
      }
      if (other.hasRootLpIterations_) {
        setRootLpIterations(other.rootLpIterations_);
      }
      if (other.hasMinOrthogonalityForLpConstraints_) {
        setMinOrthogonalityForLpConstraints(other.minOrthogonalityForLpConstraints_);
      }
      if (other.hasMaxCutRoundsAtLevelZero_) {
        setMaxCutRoundsAtLevelZero(other.maxCutRoundsAtLevelZero_);
      }
      if (other.hasMaxConsecutiveInactiveCount_) {
        setMaxConsecutiveInactiveCount(other.maxConsecutiveInactiveCount_);
      }
      if (other.hasCutMaxActiveCountValue_) {
        setCutMaxActiveCountValue(other.cutMaxActiveCountValue_);
      }
      if (other.hasCutActiveCountDecay_) {
        setCutActiveCountDecay(other.cutActiveCountDecay_);
      }
      if (other.hasCutCleanupTarget_) {
        setCutCleanupTarget(other.cutCleanupTarget_);
      }
      if (other.hasNewConstraintsBatchSize_) {
        setNewConstraintsBatchSize(other.newConstraintsBatchSize_);
      }
      if (other.hasExploitIntegerLpSolution_) {
        setExploitIntegerLpSolution(other.exploitIntegerLpSolution_);
      }
      if (other.hasExploitAllLpSolution_) {
        setExploitAllLpSolution(other.exploitAllLpSolution_);
      }
      if (other.hasExploitBestSolution_) {
        setExploitBestSolution(other.exploitBestSolution_);
      }
      if (other.hasExploitRelaxationSolution_) {
        setExploitRelaxationSolution(other.exploitRelaxationSolution_);
      }
      if (other.hasExploitObjective_) {
        setExploitObjective(other.exploitObjective_);
      }
      if (other.hasDetectLinearizedProduct_) {
        setDetectLinearizedProduct(other.detectLinearizedProduct_);
      }
      if (other.hasUseNewIntegerConflictResolution_) {
        setUseNewIntegerConflictResolution(other.useNewIntegerConflictResolution_);
      }
      if (other.hasCreate1UipBooleanDuringIcr_) {
        setCreate1UipBooleanDuringIcr(other.create1UipBooleanDuringIcr_);
      }
      if (other.hasMipMaxBound_) {
        setMipMaxBound(other.mipMaxBound_);
      }
      if (other.hasMipVarScaling_) {
        setMipVarScaling(other.mipVarScaling_);
      }
      if (other.hasMipScaleLargeDomain_) {
        setMipScaleLargeDomain(other.mipScaleLargeDomain_);
      }
      if (other.hasMipAutomaticallyScaleVariables_) {
        setMipAutomaticallyScaleVariables(other.mipAutomaticallyScaleVariables_);
      }
      if (other.hasMipScaleToMaxBound_) {
        setMipScaleToMaxBound(other.mipScaleToMaxBound_);
      }
      if (other.hasMipPresolveRestrictBounds_) {
        setMipPresolveRestrictBounds(other.mipPresolveRestrictBounds_);
      }
      if (other.hasOnlySolveIp_) {
        setOnlySolveIp(other.onlySolveIp_);
      }
      if (other.hasMipWantedPrecision_) {
        setMipWantedPrecision(other.mipWantedPrecision_);
      }
      if (other.hasMipMaxActivityExponent_) {
        setMipMaxActivityExponent(other.mipMaxActivityExponent_);
      }
      if (other.hasMipCheckPrecision_) {
        setMipCheckPrecision(other.mipCheckPrecision_);
      }
      if (other.hasMipComputeTrueObjectiveBound_) {
        setMipComputeTrueObjectiveBound(other.mipComputeTrueObjectiveBound_);
      }
      if (other.hasMipMaxValidMagnitude_) {
        setMipMaxValidMagnitude(other.mipMaxValidMagnitude_);
      }
      if (other.hasMipTreatHighMagnitudeBoundsAsInfinity_) {
        setMipTreatHighMagnitudeBoundsAsInfinity(other.mipTreatHighMagnitudeBoundsAsInfinity_);
      }
      if (other.hasMipDropTolerance_) {
        setMipDropTolerance(other.mipDropTolerance_);
      }
      if (other.hasMipPresolveLevel_) {
        setMipPresolveLevel(other.mipPresolveLevel_);
      }
      if (other.hasMipUseTightBounds_) {
        setMipUseTightBounds(other.mipUseTightBounds_);
      }
      if (other.hasMipTightPrecision_) {
        setMipTightPrecision(other.mipTightPrecision_);
      }
      if (other.hasMipVerifySolution_) {
        setMipVerifySolution(other.mipVerifySolution_);
      }
      if (other.hasMipProperRelaxation_) {
        setMipProperRelaxation(other.mipProperRelaxation_);
      }
      return this;
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 8:
            {
              int v = (int) r.varint();
              if (SatParameters.VariableOrder.forNumber(v) != null) {
                setPreferredVariableOrderValue(v);
              }
            }
            break;
          case 16:
            {
              int v = (int) r.varint();
              if (SatParameters.Polarity.forNumber(v) != null) {
                setInitialPolarityValue(v);
              }
            }
            break;
          case 32:
            {
              int v = (int) r.varint();
              if (SatParameters.ConflictMinimizationAlgorithm.forNumber(v) != null) {
                setMinimizationAlgorithmValue(v);
              }
            }
            break;
          case 88:
            setClauseCleanupPeriod((int) r.varint());
            break;
          case 104:
            setClauseCleanupTarget((int) r.varint());
            break;
          case 121:
            setVariableActivityDecay(r.dbl());
            break;
          case 129:
            setMaxVariableActivityValue(r.dbl());
            break;
          case 137:
            setClauseActivityDecay(r.dbl());
            break;
          case 145:
            setMaxClauseActivityValue(r.dbl());
            break;
          case 177:
            setGlucoseMaxDecay(r.dbl());
            break;
          case 185:
            setGlucoseDecayIncrement(r.dbl());
            break;
          case 192:
            setGlucoseDecayIncrementPeriod((int) r.varint());
            break;
          case 240:
            setRestartPeriod((int) r.varint());
            break;
          case 248:
            setRandomSeed((int) r.varint());
            break;
          case 257:
            setRandomBranchesRatio(r.dbl());
            break;
          case 272:
            {
              int v = (int) r.varint();
              if (SatParameters.BinaryMinizationAlgorithm.forNumber(v) != null) {
                setBinaryMinimizationAlgorithmValue(v);
              }
            }
            break;
          case 280:
            setUseOptimizationHints(r.varint() != 0);
            break;
          case 289:
            setMaxTimeInSeconds(r.dbl());
            break;
          case 296:
            setMaxNumberOfConflicts(r.varint());
            break;
          case 320:
            setMaxMemoryInMb(r.varint());
            break;
          case 328:
            setLogSearchProgress(r.varint() != 0);
            break;
          case 344:
            setUsePbResolution(r.varint() != 0);
            break;
          case 352:
            setUsePhaseSaving(r.varint() != 0);
            break;
          case 361:
            setRandomPolarityRatio(r.dbl());
            break;
          case 368:
            setPbCleanupIncrement((int) r.varint());
            break;
          case 377:
            setPbCleanupRatio(r.dbl());
            break;
          case 384:
            setMinimizeReductionDuringPbResolution(r.varint() != 0);
            break;
          case 392:
            setCountAssumptionLevelsInLbd(r.varint() != 0);
            break;
          case 400:
            setCoreMinimizationLevel((int) r.varint());
            break;
          case 408:
            {
              int v = (int) r.varint();
              if (SatParameters.MaxSatAssumptionOrder.forNumber(v) != null) {
                setMaxSatAssumptionOrderValue(v);
              }
            }
            break;
          case 416:
            setMaxSatReverseAssumptionOrder(r.varint() != 0);
            break;
          case 424:
            {
              int v = (int) r.varint();
              if (SatParameters.MaxSatStratificationAlgorithm.forNumber(v) != null) {
                setMaxSatStratificationValue(v);
              }
            }
            break;
          case 432:
            setPresolveBveThreshold((int) r.varint());
            break;
          case 440:
            setPresolveBveClauseWeight((int) r.varint());
            break;
          case 448:
            setSubsumptionDuringConflictAnalysis(r.varint() != 0);
            break;
          case 457:
            setPresolveProbingDeterministicTimeLimit(r.dbl());
            break;
          case 472:
            setClauseCleanupLbdBound((int) r.varint());
            break;
          case 480:
            {
              int v = (int) r.varint();
              if (SatParameters.ClauseOrdering.forNumber(v) != null) {
                setClauseCleanupOrderingValue(v);
              }
            }
            break;
          case 490: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              SatParameters.RestartAlgorithm e = SatParameters.RestartAlgorithm.forNumber((int) p.varint());
              if (e != null) {
                addRestartAlgorithms(e);
              }
            }
            break;
          }
          case 488:
            {
              SatParameters.RestartAlgorithm e = SatParameters.RestartAlgorithm.forNumber((int) r.varint());
              if (e != null) {
                addRestartAlgorithms(e);
              }
            }
            break;
          case 496:
            setRestartRunningWindowSize((int) r.varint());
            break;
          case 505:
            setRestartDlAverageRatio(r.dbl());
            break;
          case 512:
            setUseBlockingRestart(r.varint() != 0);
            break;
          case 520:
            setBlockingRestartWindowSize((int) r.varint());
            break;
          case 529:
            setBlockingRestartMultiplier(r.dbl());
            break;
          case 537:
            setMaxDeterministicTime(r.dbl());
            break;
          case 544:
            setNumConflictsBeforeStrategyChanges((int) r.varint());
            break;
          case 553:
            setStrategyChangeIncreaseRatio(r.dbl());
            break;
          case 562:
            setDefaultRestartAlgorithms(r.string(false));
            break;
          case 569:
            setRestartLbdAverageRatio(r.dbl());
            break;
          case 576:
            setPresolveUseBva(r.varint() != 0);
            break;
          case 584:
            setPresolveBvaThreshold((int) r.varint());
            break;
          case 592:
            setUsePrecedencesInDisjunctiveConstraint(r.varint() != 0);
            break;
          case 600:
            setUseErwaHeuristic(r.varint() != 0);
            break;
          case 609:
            setInitialVariablesActivity(r.dbl());
            break;
          case 616:
            setAlsoBumpVariablesInConflictReasons(r.varint() != 0);
            break;
          case 624:
            setUseOverloadCheckerInCumulative(r.varint() != 0);
            break;
          case 632:
            setUseTimetableEdgeFindingInCumulative(r.varint() != 0);
            break;
          case 640:
            setUseDisjunctiveConstraintInCumulative(r.varint() != 0);
            break;
          case 656:
            {
              int v = (int) r.varint();
              if (SatParameters.SearchBranching.forNumber(v) != null) {
                setSearchBranchingValue(v);
              }
            }
            break;
          case 664:
            setOptimizeWithCore(r.varint() != 0);
            break;
          case 672:
            setFindMultipleCores(r.varint() != 0);
            break;
          case 680:
            setOptimizeWithMaxHs(r.varint() != 0);
            break;
          case 688:
            setCpModelPresolve(r.varint() != 0);
            break;
          case 696:
            setEnumerateAllSolutions(r.varint() != 0);
            break;
          case 704:
            setPresolveBlockedClause(r.varint() != 0);
            break;
          case 712:
            setCoverOptimization(r.varint() != 0);
            break;
          case 720:
            setLinearizationLevel((int) r.varint());
            break;
          case 728:
            setMaxNumCuts((int) r.varint());
            break;
          case 736:
            setOnlyAddCutsAtLevelZero(r.varint() != 0);
            break;
          case 744:
            setCpModelUseSatPresolve(r.varint() != 0);
            break;
          case 752:
            setExploitIntegerLpSolution(r.varint() != 0);
            break;
          case 760:
            setAutoDetectGreaterThanAtLeastOneOf(r.varint() != 0);
            break;
          case 784:
            setStopAfterFirstSolution(r.varint() != 0);
            break;
          case 792:
            setBinarySearchNumConflicts((int) r.varint());
            break;
          case 800:
            setNumSearchWorkers((int) r.varint());
            break;
          case 808:
            setUseLnsOnly(r.varint() != 0);
            break;
          case 824:
            setRandomizeSearch(r.varint() != 0);
            break;
          case 832:
            setSearchRandomVariablePoolSize(r.varint());
            break;
          case 848:
            setInstantiateAllVariables(r.varint() != 0);
            break;
          case 856:
            setBooleanEncodingLevel((int) r.varint());
            break;
          case 864:
            setUseOptionalVariables(r.varint() != 0);
            break;
          case 872:
            setUseExactLpReason(r.varint() != 0);
            break;
          case 880:
            setCpModelProbingLevel((int) r.varint());
            break;
          case 896:
            setAddLpConstraintsLazily(r.varint() != 0);
            break;
          case 904:
            setShareObjectiveBounds(r.varint() != 0);
            break;
          case 912:
            setShareLevelZeroBounds(r.varint() != 0);
            break;
          case 921:
            setMinOrthogonalityForLpConstraints(r.dbl());
            break;
          case 928:
            setExploitAllLpSolution(r.varint() != 0);
            break;
          case 936:
            setAddCgCuts(r.varint() != 0);
            break;
          case 952:
            setMaxIntegerRoundingScaling((int) r.varint());
            break;
          case 960:
            setAddMirCuts(r.varint() != 0);
            break;
          case 968:
            setMaxConsecutiveInactiveCount((int) r.varint());
            break;
          case 976:
            setNewConstraintsBatchSize((int) r.varint());
            break;
          case 984:
            setPseudoCostReliabilityThreshold(r.varint());
            break;
          case 993:
            setMipMaxBound(r.dbl());
            break;
          case 1001:
            setMipVarScaling(r.dbl());
            break;
          case 1009:
            setMipWantedPrecision(r.dbl());
            break;
          case 1016:
            setMipMaxActivityExponent((int) r.varint());
            break;
          case 1025:
            setMipCheckPrecision(r.dbl());
            break;
          case 1032:
            setUseRinsLns(r.varint() != 0);
            break;
          case 1040:
            setExploitBestSolution(r.varint() != 0);
            break;
          case 1048:
            setExploitObjective(r.varint() != 0);
            break;
          case 1056:
            setFillTightenedDomainsInResponse(r.varint() != 0);
            break;
          case 1064:
            setUseCombinedNoOverlap(r.varint() != 0);
            break;
          case 1072:
            setInterleaveBatchSize((int) r.varint());
            break;
          case 1080:
            setCatchSigintSignal(r.varint() != 0);
            break;
          case 1088:
            setInterleaveSearch(r.varint() != 0);
            break;
          case 1096:
            setDiversifyLnsParams(r.varint() != 0);
            break;
          case 1104:
            setMaxPresolveIterations((int) r.varint());
            break;
          case 1152:
            setUseImpliedBounds(r.varint() != 0);
            break;
          case 1161:
            setMergeNoOverlapWorkLimit(r.dbl());
            break;
          case 1169:
            setMergeAtMostOneWorkLimit(r.dbl());
            break;
          case 1176:
            setPresolveSubstitutionLevel((int) r.varint());
            break;
          case 1184:
            setMaxAllDiffCutSize((int) r.varint());
            break;
          case 1192:
            setStopAfterPresolve(r.varint() != 0);
            break;
          case 1208:
            setDebugMaxNumPresolveOperations((int) r.varint());
            break;
          case 1216:
            setAddLinMaxCuts(r.varint() != 0);
            break;
          case 1224:
            setHintConflictLimit((int) r.varint());
            break;
          case 1232:
            setMaxCutRoundsAtLevelZero((int) r.varint());
            break;
          case 1241:
            setCutMaxActiveCountValue(r.dbl());
            break;
          case 1249:
            setCutActiveCountDecay(r.dbl());
            break;
          case 1256:
            setCutCleanupTarget((int) r.varint());
            break;
          case 1273:
            setAbsoluteGapLimit(r.dbl());
            break;
          case 1281:
            setRelativeGapLimit(r.dbl());
            break;
          case 1288:
            setExploitRelaxationSolution(r.varint() != 0);
            break;
          case 1296:
            setDebugPostsolveWithFullSolver(r.varint() != 0);
            break;
          case 1304:
            setUseSatInprocessing(r.varint() != 0);
            break;
          case 1312:
            setUseFeasibilityPump(r.varint() != 0);
            break;
          case 1320:
            {
              int v = (int) r.varint();
              if (SatParameters.FPRoundingMethod.forNumber(v) != null) {
                setFpRoundingValue(v);
              }
            }
            break;
          case 1328:
            setMipAutomaticallyScaleVariables(r.varint() != 0);
            break;
          case 1336:
            setRepairHint(r.varint() != 0);
            break;
          case 1344:
            setPolarityRephaseIncrement((int) r.varint());
            break;
          case 1352:
            setAddZeroHalfCuts(r.varint() != 0);
            break;
          case 1360:
            setExpandAlldiffConstraints(r.varint() != 0);
            break;
          case 1370:
            setName(r.string(false));
            break;
          case 1376:
            setAddCliqueCuts(r.varint() != 0);
            break;
          case 1384:
            setKeepAllFeasibleSolutionsInPresolve(r.varint() != 0);
            break;
          case 1392:
            setPresolveExtractIntegerEnforcement(r.varint() != 0);
            break;
          case 1400:
            setPolishLpSolution(r.varint() != 0);
            break;
          case 1408:
            setUseProbingSearch(r.varint() != 0);
            break;
          case 1416:
            setConvertIntervals(r.varint() != 0);
            break;
          case 1424:
            setPermuteVariableRandomly(r.varint() != 0);
            break;
          case 1432:
            setPermutePresolveConstraintOrder(r.varint() != 0);
            break;
          case 1440:
            setUseAbslRandom(r.varint() != 0);
            break;
          case 1448:
            setDisableConstraintExpansion(r.varint() != 0);
            break;
          case 1456:
            setExpandReservoirConstraints(r.varint() != 0);
            break;
          case 1464:
            setSymmetryLevel((int) r.varint());
            break;
          case 1482:
            setLogPrefix(r.string(false));
            break;
          case 1488:
            setLogToStdout(r.varint() != 0);
            break;
          case 1496:
            setLogToResponse(r.varint() != 0);
            break;
          case 1504:
            setOptimizeWithLbTreeSearch(r.varint() != 0);
            break;
          case 1512:
            setLogSubsolverStatistics(r.varint() != 0);
            break;
          case 1521:
            setClauseCleanupRatio(r.dbl());
            break;
          case 1528:
            setMaxDomainSizeWhenEncodingEqNeqConstraints((int) r.varint());
            break;
          case 1536:
            setFixVariablesToTheirHintedValue(r.varint() != 0);
            break;
          case 1544:
            setSolutionPoolSize((int) r.varint());
            break;
          case 1552:
            setFillAdditionalSolutionsInResponse(r.varint() != 0);
            break;
          case 1560:
            setDebugCrashOnBadHint(r.varint() != 0);
            break;
          case 1568:
            setCutLevel((int) r.varint());
            break;
          case 1576:
            setAddObjectiveCut(r.varint() != 0);
            break;
          case 1584:
            setMipComputeTrueObjectiveBound(r.varint() != 0);
            break;
          case 1593:
            setMipMaxValidMagnitude(r.dbl());
            break;
          case 1600:
            setUseTimetablingInNoOverlap2D(r.varint() != 0);
            break;
          case 1608:
            setPresolveInclusionWorkLimit(r.varint());
            break;
          case 1616:
            setIgnoreNames(r.varint() != 0);
            break;
          case 1624:
            setShareBinaryClauses(r.varint() != 0);
            break;
          case 1633:
            setShavingDeterministicTimeInProbingSearch(r.dbl());
            break;
          case 1641:
            setShavingSearchDeterministicTime(r.dbl());
            break;
          case 1648:
            setNumWorkers((int) r.varint());
            break;
          case 1658:
            addSubsolvers(r.string(false));
            break;
          case 1674:
            addIgnoreSubsolvers(r.string(false));
            break;
          case 1682:
            addSubsolverParamsBuilder().mergeWire(r.message());
            break;
          case 1704:
            setUseEnergeticReasoningInNoOverlap2D(r.varint() != 0);
            break;
          case 1712:
            setUseDualSchedulingHeuristics(r.varint() != 0);
            break;
          case 1720:
            setUseHardPrecedencesInCumulative(r.varint() != 0);
            break;
          case 1728:
            setDetectTableWithCost(r.varint() != 0);
            break;
          case 1736:
            setTableCompressionLevel((int) r.varint());
            break;
          case 1754:
            addExtraSubsolvers(r.string(false));
            break;
          case 1760:
            setExploitAllPrecedences(r.varint() != 0);
            break;
          case 1769:
            setPropagationLoopDetectionFactor(r.dbl());
            break;
          case 1776:
            setOnlySolveIp(r.varint() != 0);
            break;
          case 1784:
            setEncodeComplexLinearConstraintWithInteger(r.varint() != 0);
            break;
          case 1792:
            setNewLinearPropagation(r.varint() != 0);
            break;
          case 1800:
            setMipScaleLargeDomain(r.varint() != 0);
            break;
          case 1809:
            setProbingDeterministicTimeLimit(r.dbl());
            break;
          case 1816:
            setRootLpIterations((int) r.varint());
            break;
          case 1824:
            setUseObjectiveLbSearch(r.varint() != 0);
            break;
          case 1832:
            setMaxSizeToCreatePrecedenceLiteralsInDisjunctive((int) r.varint());
            break;
          case 1840:
            setUseStrongPropagationInDisjunctive(r.varint() != 0);
            break;
          case 1857:
            setMipDropTolerance(r.dbl());
            break;
          case 1864:
            setInferAllDiffs(r.varint() != 0);
            break;
          case 1872:
            setFindBigLinearOverlap(r.varint() != 0);
            break;
          case 1880:
            setSharedTreeNumWorkers((int) r.varint());
            break;
          case 1888:
            setUseSharedTreeSearch(r.varint() != 0);
            break;
          case 1904:
            setSharedTreeMaxNodesPerWorker((int) r.varint());
            break;
          case 1912:
            {
              int v = (int) r.varint();
              if (SatParameters.SharedTreeSplitStrategy.forNumber(v) != null) {
                setSharedTreeSplitStrategyValue(v);
              }
            }
            break;
          case 1920:
            setUseLsOnly(r.varint() != 0);
            break;
          case 1937:
            setFeasibilityJumpDecay(r.dbl());
            break;
          case 1952:
            setNumViolationLs((int) r.varint());
            break;
          case 1977:
            setFeasibilityJumpVarRandomizationProbability(r.dbl());
            break;
          case 1985:
            setFeasibilityJumpVarPerburbationRangeRatio(r.dbl());
            break;
          case 1992:
            setViolationLsPerturbationPeriod((int) r.varint());
            break;
          case 2000:
            setFeasibilityJumpEnableRestarts(r.varint() != 0);
            break;
          case 2016:
            setStopAfterRootPropagation(r.varint() != 0);
            break;
          case 2024:
            setUseObjectiveShavingSearch(r.varint() != 0);
            break;
          case 2040:
            setUseLbRelaxLns(r.varint() != 0);
            break;
          case 2048:
            setLinearSplitSize((int) r.varint());
            break;
          case 2056:
            setFeasibilityJumpLinearizationLevel((int) r.varint());
            break;
          case 2064:
            setFeasibilityJumpRestartFactor((int) r.varint());
            break;
          case 2080:
            setMaxNumIntervalsForTimetableEdgeFinding((int) r.varint());
            break;
          case 2088:
            setMipPresolveLevel((int) r.varint());
            break;
          case 2096:
            setPushAllTasksTowardStart(r.varint() != 0);
            break;
          case 2104:
            setUseDynamicPrecedenceInDisjunctive(r.varint() != 0);
            break;
          case 2112:
            setFeasibilityJumpMaxExpandedConstraintSize((int) r.varint());
            break;
          case 2120:
            setUseFeasibilityJump(r.varint() != 0);
            break;
          case 2129:
            setLpPrimalTolerance(r.dbl());
            break;
          case 2137:
            setLpDualTolerance(r.dbl());
            break;
          case 2144:
            setUseDynamicPrecedenceInCumulative(r.varint() != 0);
            break;
          case 2152:
            setUseExtendedProbing(r.varint() != 0);
            break;
          case 2160:
            setAtMostOneMaxExpansionSize((int) r.varint());
            break;
          case 2168:
            setUseAreaEnergeticReasoningInNoOverlap2D(r.varint() != 0);
            break;
          case 2176:
            setProbingNumCombinationsLimit((int) r.varint());
            break;
          case 2185:
            setInprocessingDtimeRatio(r.dbl());
            break;
          case 2193:
            setInprocessingProbingDtime(r.dbl());
            break;
          case 2201:
            setInprocessingMinimizationDtime(r.dbl());
            break;
          case 2208:
            setMaxPairsPairwiseReasoningInNoOverlap2D((int) r.varint());
            break;
          case 2216:
            setDetectLinearizedProduct(r.varint() != 0);
            break;
          case 2224:
            setMipTreatHighMagnitudeBoundsAsInfinity(r.varint() != 0);
            break;
          case 2232:
            setAddRltCuts(r.varint() != 0);
            break;
          case 2240:
            setMaxLinMaxSizeForExpansion((int) r.varint());
            break;
          case 2249:
            setSharedTreeOpenLeavesPerWorker(r.dbl());
            break;
          case 2256:
            setSharedTreeWorkerMinRestartsPerSubtree((int) r.varint());
            break;
          case 2264:
            setUseLns(r.varint() != 0);
            break;
          case 2272:
            setSaveLpBasisInLbTreeSearch(r.varint() != 0);
            break;
          case 2280:
            setShareGlueClauses(r.varint() != 0);
            break;
          case 2288:
            setUseConservativeScaleOverloadChecker(r.varint() != 0);
            break;
          case 2296:
            setEncodeCumulativeAsReservoir(r.varint() != 0);
            break;
          case 2304:
            setExpandReservoirUsingCircuit(r.varint() != 0);
            break;
          case 2312:
            setVariablesShavingLevel((int) r.varint());
            break;
          case 2320:
            setShavingSearchThreshold(r.varint());
            break;
          case 2328:
            setMaxNumDeterministicBatches((int) r.varint());
            break;
          case 2337:
            setFeasibilityJumpBatchDtime(r.dbl());
            break;
          case 2346:
            addFilterSubsolvers(r.string(false));
            break;
          case 2352:
            setNumFullSubsolvers((int) r.varint());
            break;
          case 2360:
            setSharedTreeWorkerEnableTrailSharing(r.varint() != 0);
            break;
          case 2368:
            setLbRelaxNumWorkersThreshold((int) r.varint());
            break;
          case 2376:
            setInprocessingMinimizationUseConflictAnalysis(r.varint() != 0);
            break;
          case 2392:
            setUseTryEdgeReasoningInNoOverlap2D(r.varint() != 0);
            break;
          case 2400:
            setMinimizeSharedClauses(r.varint() != 0);
            break;
          case 2408:
            setUseSymmetryInLp(r.varint() != 0);
            break;
          case 2417:
            setSymmetryDetectionDeterministicTimeLimit(r.dbl());
            break;
          case 2424:
            setKeepSymmetryInPresolve(r.varint() != 0);
            break;
          case 2432:
            setSharedTreeWorkerEnablePhaseSharing(r.varint() != 0);
            break;
          case 2440:
            setSharedTreeBalanceTolerance((int) r.varint());
            break;
          case 2448:
            setDebugCrashIfPresolveBreaksHint(r.varint() != 0);
            break;
          case 2457:
            setLnsInitialDifficulty(r.dbl());
            break;
          case 2465:
            setLnsInitialDeterministicLimit(r.dbl());
            break;
          case 2472:
            setPolarityExploitLsHints(r.varint() != 0);
            break;
          case 2480:
            setRemoveFixedVariablesEarly(r.varint() != 0);
            break;
          case 2488:
            setUseAllDifferentForCircuit(r.varint() != 0);
            break;
          case 2496:
            setRoutingCutSubsetSizeForBinaryRelationBound((int) r.varint());
            break;
          case 2504:
            setRoutingCutSubsetSizeForTightBinaryRelationBound((int) r.varint());
            break;
          case 2513:
            setRoutingCutDpEffort(r.dbl());
            break;
          case 2520:
            setMaximumRegionsToSplitInDisconnectedNoOverlap2D((int) r.varint());
            break;
          case 2528:
            setRoutingCutSubsetSizeForExactBinaryRelationBound((int) r.varint());
            break;
          case 2536:
            setRoutingCutMaxInfeasiblePathLength((int) r.varint());
            break;
          case 2544:
            setRoutingCutSubsetSizeForShortestPathsBound((int) r.varint());
            break;
          case 2560:
            setMaxAlldiffDomainSize((int) r.varint());
            break;
          case 2568:
            setNoOverlap2DBooleanRelationsLimit((int) r.varint());
            break;
          case 2577:
            setShareGlueClausesDtime(r.dbl());
            break;
          case 2584:
            setUseLinear3ForNoOverlap2DPrecedences(r.varint() != 0);
            break;
          case 2592:
            setFilterSatPostsolveClauses(r.varint() != 0);
            break;
          case 2600:
            setAlternativePoolSize((int) r.varint());
            break;
          case 2608:
            setShareLinear2Bounds(r.varint() != 0);
            break;
          case 2616:
            setTransitivePrecedencesWorkLimit((int) r.varint());
            break;
          case 2625:
            setSharedTreeSplitMinDtime(r.dbl());
            break;
          case 2632:
            setSolutionPoolDiversityLimit((int) r.varint());
            break;
          case 2640:
            setUseChronologicalBacktracking(r.varint() != 0);
            break;
          case 2648:
            setMaxBackjumpLevels((int) r.varint());
            break;
          case 2656:
            setChronologicalBacktrackMinConflicts((int) r.varint());
            break;
          case 2664:
            setFindClausesThatAreExactlyOne(r.varint() != 0);
            break;
          case 2680:
            setLoadAtMostOnesInSatPresolve(r.varint() != 0);
            break;
          case 2688:
            setUseNewIntegerConflictResolution(r.varint() != 0);
            break;
          case 2696:
            setClauseCleanupPeriodIncrement((int) r.varint());
            break;
          case 2712:
            setDebugCrashIfLratCheckFails(r.varint() != 0);
            break;
          case 2720:
            setMaxDomainSizeForLinear2Expansion((int) r.varint());
            break;
          case 2728:
            setCreate1UipBooleanDuringIcr(r.varint() != 0);
            break;
          case 2736:
            setInprocessingUseCongruenceClosure(r.varint() != 0);
            break;
          case 2744:
            setEagerlySubsumeLastNConflicts((int) r.varint());
            break;
          case 2752:
            setCheckLratProof(r.varint() != 0);
            break;
          case 2760:
            setOutputLratProof(r.varint() != 0);
            break;
          case 2792:
            setClauseCleanupLbdTier1((int) r.varint());
            break;
          case 2800:
            setClauseCleanupLbdTier2((int) r.varint());
            break;
          case 2808:
            setExtraSubsumptionDuringConflictAnalysis(r.varint() != 0);
            break;
          case 2816:
            setCheckMergedLratProof(r.varint() != 0);
            break;
          case 2824:
            setDecisionSubsumptionDuringConflictAnalysis(r.varint() != 0);
            break;
          case 2832:
            setInprocessingUseSatSweeping(r.varint() != 0);
            break;
          case 2840:
            setSubsumeDuringVivification(r.varint() != 0);
            break;
          case 2848:
            setNumOrderingInVivification((int) r.varint());
            break;
          case 2856:
            setDetectEncodedComplexDomain(r.varint() != 0);
            break;
          case 2864:
            setMipScaleToMaxBound(r.varint() != 0);
            break;
          case 2872:
            setMipPresolveRestrictBounds(r.varint() != 0);
            break;
          case 2880:
            setCpModelPureSatPresolve(r.varint() != 0);
            break;
          case 2888:
            setInprocessingUseTransitiveReduction(r.varint() != 0);
            break;
          case 2896:
            setInprocessingDetectAndSweepCircuit(r.varint() != 0);
            break;
          case 2904:
            setTryHintAsAssumptions(r.varint() != 0);
            break;
          case 2912:
            setLearnOnAssumptionsUnsat(r.varint() != 0);
            break;
          case 2920:
            setSharedTreeWorkerPropagateIntermediateNodes(r.varint() != 0);
            break;
          case 2928:
            setShavingSearchUseRandomSearch(r.varint() != 0);
            break;
          case 2936:
            setShavingSearchRestartLimit((int) r.varint());
            break;
          case 2944:
            setObjectiveShavingSearchThreshold(r.varint());
            break;
          case 2952:
            setCpModelDirectSolve(r.varint() != 0);
            break;
          case 2960:
            setMipUseTightBounds(r.varint() != 0);
            break;
          case 2969:
            setMipTightPrecision(r.dbl());
            break;
          case 2976:
            setMipVerifySolution(r.varint() != 0);
            break;
          case 2984:
            setMipProperRelaxation(r.varint() != 0);
            break;
          case 5720:
            setFeasibilityJumpIgnoreRedundantConstraints(r.varint() != 0);
            break;
          case 5728:
            setLnsIgnoreRedundantConstraints(r.varint() != 0);
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
          case "name": {
            if (strict && hasName_) {
              throw ProtoWire.duplicate(t, "name");
            }
            t.consume(":");
            setName(t.string());
            break;
          }
          case "preferred_variable_order": {
            if (strict && hasPreferredVariableOrder_) {
              throw ProtoWire.duplicate(t, "preferred_variable_order");
            }
            t.consume(":");
            setPreferredVariableOrderValue(t.enumValue(SatParameters.VariableOrder.class, SatParameters.VariableOrder::forNumber, SatParameters.VariableOrder::getNumber, true, "preferred_variable_order"));
            break;
          }
          case "initial_polarity": {
            if (strict && hasInitialPolarity_) {
              throw ProtoWire.duplicate(t, "initial_polarity");
            }
            t.consume(":");
            setInitialPolarityValue(t.enumValue(SatParameters.Polarity.class, SatParameters.Polarity::forNumber, SatParameters.Polarity::getNumber, true, "initial_polarity"));
            break;
          }
          case "use_phase_saving": {
            if (strict && hasUsePhaseSaving_) {
              throw ProtoWire.duplicate(t, "use_phase_saving");
            }
            t.consume(":");
            setUsePhaseSaving(t.bool());
            break;
          }
          case "polarity_rephase_increment": {
            if (strict && hasPolarityRephaseIncrement_) {
              throw ProtoWire.duplicate(t, "polarity_rephase_increment");
            }
            t.consume(":");
            setPolarityRephaseIncrement((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "polarity_exploit_ls_hints": {
            if (strict && hasPolarityExploitLsHints_) {
              throw ProtoWire.duplicate(t, "polarity_exploit_ls_hints");
            }
            t.consume(":");
            setPolarityExploitLsHints(t.bool());
            break;
          }
          case "random_polarity_ratio": {
            if (strict && hasRandomPolarityRatio_) {
              throw ProtoWire.duplicate(t, "random_polarity_ratio");
            }
            t.consume(":");
            setRandomPolarityRatio(t.floating());
            break;
          }
          case "random_branches_ratio": {
            if (strict && hasRandomBranchesRatio_) {
              throw ProtoWire.duplicate(t, "random_branches_ratio");
            }
            t.consume(":");
            setRandomBranchesRatio(t.floating());
            break;
          }
          case "use_erwa_heuristic": {
            if (strict && hasUseErwaHeuristic_) {
              throw ProtoWire.duplicate(t, "use_erwa_heuristic");
            }
            t.consume(":");
            setUseErwaHeuristic(t.bool());
            break;
          }
          case "initial_variables_activity": {
            if (strict && hasInitialVariablesActivity_) {
              throw ProtoWire.duplicate(t, "initial_variables_activity");
            }
            t.consume(":");
            setInitialVariablesActivity(t.floating());
            break;
          }
          case "also_bump_variables_in_conflict_reasons": {
            if (strict && hasAlsoBumpVariablesInConflictReasons_) {
              throw ProtoWire.duplicate(t, "also_bump_variables_in_conflict_reasons");
            }
            t.consume(":");
            setAlsoBumpVariablesInConflictReasons(t.bool());
            break;
          }
          case "minimization_algorithm": {
            if (strict && hasMinimizationAlgorithm_) {
              throw ProtoWire.duplicate(t, "minimization_algorithm");
            }
            t.consume(":");
            setMinimizationAlgorithmValue(t.enumValue(SatParameters.ConflictMinimizationAlgorithm.class, SatParameters.ConflictMinimizationAlgorithm::forNumber, SatParameters.ConflictMinimizationAlgorithm::getNumber, true, "minimization_algorithm"));
            break;
          }
          case "binary_minimization_algorithm": {
            if (strict && hasBinaryMinimizationAlgorithm_) {
              throw ProtoWire.duplicate(t, "binary_minimization_algorithm");
            }
            t.consume(":");
            setBinaryMinimizationAlgorithmValue(t.enumValue(SatParameters.BinaryMinizationAlgorithm.class, SatParameters.BinaryMinizationAlgorithm::forNumber, SatParameters.BinaryMinizationAlgorithm::getNumber, true, "binary_minimization_algorithm"));
            break;
          }
          case "subsumption_during_conflict_analysis": {
            if (strict && hasSubsumptionDuringConflictAnalysis_) {
              throw ProtoWire.duplicate(t, "subsumption_during_conflict_analysis");
            }
            t.consume(":");
            setSubsumptionDuringConflictAnalysis(t.bool());
            break;
          }
          case "extra_subsumption_during_conflict_analysis": {
            if (strict && hasExtraSubsumptionDuringConflictAnalysis_) {
              throw ProtoWire.duplicate(t, "extra_subsumption_during_conflict_analysis");
            }
            t.consume(":");
            setExtraSubsumptionDuringConflictAnalysis(t.bool());
            break;
          }
          case "decision_subsumption_during_conflict_analysis": {
            if (strict && hasDecisionSubsumptionDuringConflictAnalysis_) {
              throw ProtoWire.duplicate(t, "decision_subsumption_during_conflict_analysis");
            }
            t.consume(":");
            setDecisionSubsumptionDuringConflictAnalysis(t.bool());
            break;
          }
          case "eagerly_subsume_last_n_conflicts": {
            if (strict && hasEagerlySubsumeLastNConflicts_) {
              throw ProtoWire.duplicate(t, "eagerly_subsume_last_n_conflicts");
            }
            t.consume(":");
            setEagerlySubsumeLastNConflicts((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "subsume_during_vivification": {
            if (strict && hasSubsumeDuringVivification_) {
              throw ProtoWire.duplicate(t, "subsume_during_vivification");
            }
            t.consume(":");
            setSubsumeDuringVivification(t.bool());
            break;
          }
          case "use_chronological_backtracking": {
            if (strict && hasUseChronologicalBacktracking_) {
              throw ProtoWire.duplicate(t, "use_chronological_backtracking");
            }
            t.consume(":");
            setUseChronologicalBacktracking(t.bool());
            break;
          }
          case "max_backjump_levels": {
            if (strict && hasMaxBackjumpLevels_) {
              throw ProtoWire.duplicate(t, "max_backjump_levels");
            }
            t.consume(":");
            setMaxBackjumpLevels((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "chronological_backtrack_min_conflicts": {
            if (strict && hasChronologicalBacktrackMinConflicts_) {
              throw ProtoWire.duplicate(t, "chronological_backtrack_min_conflicts");
            }
            t.consume(":");
            setChronologicalBacktrackMinConflicts((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "learn_on_assumptions_unsat": {
            if (strict && hasLearnOnAssumptionsUnsat_) {
              throw ProtoWire.duplicate(t, "learn_on_assumptions_unsat");
            }
            t.consume(":");
            setLearnOnAssumptionsUnsat(t.bool());
            break;
          }
          case "clause_cleanup_period": {
            if (strict && hasClauseCleanupPeriod_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_period");
            }
            t.consume(":");
            setClauseCleanupPeriod((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_period_increment": {
            if (strict && hasClauseCleanupPeriodIncrement_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_period_increment");
            }
            t.consume(":");
            setClauseCleanupPeriodIncrement((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_target": {
            if (strict && hasClauseCleanupTarget_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_target");
            }
            t.consume(":");
            setClauseCleanupTarget((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_ratio": {
            if (strict && hasClauseCleanupRatio_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_ratio");
            }
            t.consume(":");
            setClauseCleanupRatio(t.floating());
            break;
          }
          case "clause_cleanup_lbd_bound": {
            if (strict && hasClauseCleanupLbdBound_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_lbd_bound");
            }
            t.consume(":");
            setClauseCleanupLbdBound((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_lbd_tier1": {
            if (strict && hasClauseCleanupLbdTier1_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_lbd_tier1");
            }
            t.consume(":");
            setClauseCleanupLbdTier1((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_lbd_tier2": {
            if (strict && hasClauseCleanupLbdTier2_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_lbd_tier2");
            }
            t.consume(":");
            setClauseCleanupLbdTier2((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_cleanup_ordering": {
            if (strict && hasClauseCleanupOrdering_) {
              throw ProtoWire.duplicate(t, "clause_cleanup_ordering");
            }
            t.consume(":");
            setClauseCleanupOrderingValue(t.enumValue(SatParameters.ClauseOrdering.class, SatParameters.ClauseOrdering::forNumber, SatParameters.ClauseOrdering::getNumber, true, "clause_cleanup_ordering"));
            break;
          }
          case "pb_cleanup_increment": {
            if (strict && hasPbCleanupIncrement_) {
              throw ProtoWire.duplicate(t, "pb_cleanup_increment");
            }
            t.consume(":");
            setPbCleanupIncrement((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "pb_cleanup_ratio": {
            if (strict && hasPbCleanupRatio_) {
              throw ProtoWire.duplicate(t, "pb_cleanup_ratio");
            }
            t.consume(":");
            setPbCleanupRatio(t.floating());
            break;
          }
          case "variable_activity_decay": {
            if (strict && hasVariableActivityDecay_) {
              throw ProtoWire.duplicate(t, "variable_activity_decay");
            }
            t.consume(":");
            setVariableActivityDecay(t.floating());
            break;
          }
          case "max_variable_activity_value": {
            if (strict && hasMaxVariableActivityValue_) {
              throw ProtoWire.duplicate(t, "max_variable_activity_value");
            }
            t.consume(":");
            setMaxVariableActivityValue(t.floating());
            break;
          }
          case "glucose_max_decay": {
            if (strict && hasGlucoseMaxDecay_) {
              throw ProtoWire.duplicate(t, "glucose_max_decay");
            }
            t.consume(":");
            setGlucoseMaxDecay(t.floating());
            break;
          }
          case "glucose_decay_increment": {
            if (strict && hasGlucoseDecayIncrement_) {
              throw ProtoWire.duplicate(t, "glucose_decay_increment");
            }
            t.consume(":");
            setGlucoseDecayIncrement(t.floating());
            break;
          }
          case "glucose_decay_increment_period": {
            if (strict && hasGlucoseDecayIncrementPeriod_) {
              throw ProtoWire.duplicate(t, "glucose_decay_increment_period");
            }
            t.consume(":");
            setGlucoseDecayIncrementPeriod((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "clause_activity_decay": {
            if (strict && hasClauseActivityDecay_) {
              throw ProtoWire.duplicate(t, "clause_activity_decay");
            }
            t.consume(":");
            setClauseActivityDecay(t.floating());
            break;
          }
          case "max_clause_activity_value": {
            if (strict && hasMaxClauseActivityValue_) {
              throw ProtoWire.duplicate(t, "max_clause_activity_value");
            }
            t.consume(":");
            setMaxClauseActivityValue(t.floating());
            break;
          }
          case "restart_algorithms": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addRestartAlgorithms(ProtoWire.known(t, SatParameters.RestartAlgorithm.forNumber(t.enumValue(SatParameters.RestartAlgorithm.class, SatParameters.RestartAlgorithm::forNumber, SatParameters.RestartAlgorithm::getNumber, true, "restart_algorithms")), "restart_algorithms"));
                } while (t.nextInList());
              }
            } else {
              addRestartAlgorithms(ProtoWire.known(t, SatParameters.RestartAlgorithm.forNumber(t.enumValue(SatParameters.RestartAlgorithm.class, SatParameters.RestartAlgorithm::forNumber, SatParameters.RestartAlgorithm::getNumber, true, "restart_algorithms")), "restart_algorithms"));
            }
            break;
          }
          case "default_restart_algorithms": {
            if (strict && hasDefaultRestartAlgorithms_) {
              throw ProtoWire.duplicate(t, "default_restart_algorithms");
            }
            t.consume(":");
            setDefaultRestartAlgorithms(t.string());
            break;
          }
          case "restart_period": {
            if (strict && hasRestartPeriod_) {
              throw ProtoWire.duplicate(t, "restart_period");
            }
            t.consume(":");
            setRestartPeriod((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "restart_running_window_size": {
            if (strict && hasRestartRunningWindowSize_) {
              throw ProtoWire.duplicate(t, "restart_running_window_size");
            }
            t.consume(":");
            setRestartRunningWindowSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "restart_dl_average_ratio": {
            if (strict && hasRestartDlAverageRatio_) {
              throw ProtoWire.duplicate(t, "restart_dl_average_ratio");
            }
            t.consume(":");
            setRestartDlAverageRatio(t.floating());
            break;
          }
          case "restart_lbd_average_ratio": {
            if (strict && hasRestartLbdAverageRatio_) {
              throw ProtoWire.duplicate(t, "restart_lbd_average_ratio");
            }
            t.consume(":");
            setRestartLbdAverageRatio(t.floating());
            break;
          }
          case "use_blocking_restart": {
            if (strict && hasUseBlockingRestart_) {
              throw ProtoWire.duplicate(t, "use_blocking_restart");
            }
            t.consume(":");
            setUseBlockingRestart(t.bool());
            break;
          }
          case "blocking_restart_window_size": {
            if (strict && hasBlockingRestartWindowSize_) {
              throw ProtoWire.duplicate(t, "blocking_restart_window_size");
            }
            t.consume(":");
            setBlockingRestartWindowSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "blocking_restart_multiplier": {
            if (strict && hasBlockingRestartMultiplier_) {
              throw ProtoWire.duplicate(t, "blocking_restart_multiplier");
            }
            t.consume(":");
            setBlockingRestartMultiplier(t.floating());
            break;
          }
          case "num_conflicts_before_strategy_changes": {
            if (strict && hasNumConflictsBeforeStrategyChanges_) {
              throw ProtoWire.duplicate(t, "num_conflicts_before_strategy_changes");
            }
            t.consume(":");
            setNumConflictsBeforeStrategyChanges((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "strategy_change_increase_ratio": {
            if (strict && hasStrategyChangeIncreaseRatio_) {
              throw ProtoWire.duplicate(t, "strategy_change_increase_ratio");
            }
            t.consume(":");
            setStrategyChangeIncreaseRatio(t.floating());
            break;
          }
          case "max_time_in_seconds": {
            if (strict && hasMaxTimeInSeconds_) {
              throw ProtoWire.duplicate(t, "max_time_in_seconds");
            }
            t.consume(":");
            setMaxTimeInSeconds(t.floating());
            break;
          }
          case "max_deterministic_time": {
            if (strict && hasMaxDeterministicTime_) {
              throw ProtoWire.duplicate(t, "max_deterministic_time");
            }
            t.consume(":");
            setMaxDeterministicTime(t.floating());
            break;
          }
          case "max_num_deterministic_batches": {
            if (strict && hasMaxNumDeterministicBatches_) {
              throw ProtoWire.duplicate(t, "max_num_deterministic_batches");
            }
            t.consume(":");
            setMaxNumDeterministicBatches((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_number_of_conflicts": {
            if (strict && hasMaxNumberOfConflicts_) {
              throw ProtoWire.duplicate(t, "max_number_of_conflicts");
            }
            t.consume(":");
            setMaxNumberOfConflicts(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "max_memory_in_mb": {
            if (strict && hasMaxMemoryInMb_) {
              throw ProtoWire.duplicate(t, "max_memory_in_mb");
            }
            t.consume(":");
            setMaxMemoryInMb(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "absolute_gap_limit": {
            if (strict && hasAbsoluteGapLimit_) {
              throw ProtoWire.duplicate(t, "absolute_gap_limit");
            }
            t.consume(":");
            setAbsoluteGapLimit(t.floating());
            break;
          }
          case "relative_gap_limit": {
            if (strict && hasRelativeGapLimit_) {
              throw ProtoWire.duplicate(t, "relative_gap_limit");
            }
            t.consume(":");
            setRelativeGapLimit(t.floating());
            break;
          }
          case "random_seed": {
            if (strict && hasRandomSeed_) {
              throw ProtoWire.duplicate(t, "random_seed");
            }
            t.consume(":");
            setRandomSeed((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "permute_variable_randomly": {
            if (strict && hasPermuteVariableRandomly_) {
              throw ProtoWire.duplicate(t, "permute_variable_randomly");
            }
            t.consume(":");
            setPermuteVariableRandomly(t.bool());
            break;
          }
          case "permute_presolve_constraint_order": {
            if (strict && hasPermutePresolveConstraintOrder_) {
              throw ProtoWire.duplicate(t, "permute_presolve_constraint_order");
            }
            t.consume(":");
            setPermutePresolveConstraintOrder(t.bool());
            break;
          }
          case "use_absl_random": {
            if (strict && hasUseAbslRandom_) {
              throw ProtoWire.duplicate(t, "use_absl_random");
            }
            t.consume(":");
            setUseAbslRandom(t.bool());
            break;
          }
          case "log_search_progress": {
            if (strict && hasLogSearchProgress_) {
              throw ProtoWire.duplicate(t, "log_search_progress");
            }
            t.consume(":");
            setLogSearchProgress(t.bool());
            break;
          }
          case "log_subsolver_statistics": {
            if (strict && hasLogSubsolverStatistics_) {
              throw ProtoWire.duplicate(t, "log_subsolver_statistics");
            }
            t.consume(":");
            setLogSubsolverStatistics(t.bool());
            break;
          }
          case "log_prefix": {
            if (strict && hasLogPrefix_) {
              throw ProtoWire.duplicate(t, "log_prefix");
            }
            t.consume(":");
            setLogPrefix(t.string());
            break;
          }
          case "log_to_stdout": {
            if (strict && hasLogToStdout_) {
              throw ProtoWire.duplicate(t, "log_to_stdout");
            }
            t.consume(":");
            setLogToStdout(t.bool());
            break;
          }
          case "log_to_response": {
            if (strict && hasLogToResponse_) {
              throw ProtoWire.duplicate(t, "log_to_response");
            }
            t.consume(":");
            setLogToResponse(t.bool());
            break;
          }
          case "use_pb_resolution": {
            if (strict && hasUsePbResolution_) {
              throw ProtoWire.duplicate(t, "use_pb_resolution");
            }
            t.consume(":");
            setUsePbResolution(t.bool());
            break;
          }
          case "minimize_reduction_during_pb_resolution": {
            if (strict && hasMinimizeReductionDuringPbResolution_) {
              throw ProtoWire.duplicate(t, "minimize_reduction_during_pb_resolution");
            }
            t.consume(":");
            setMinimizeReductionDuringPbResolution(t.bool());
            break;
          }
          case "count_assumption_levels_in_lbd": {
            if (strict && hasCountAssumptionLevelsInLbd_) {
              throw ProtoWire.duplicate(t, "count_assumption_levels_in_lbd");
            }
            t.consume(":");
            setCountAssumptionLevelsInLbd(t.bool());
            break;
          }
          case "presolve_bve_threshold": {
            if (strict && hasPresolveBveThreshold_) {
              throw ProtoWire.duplicate(t, "presolve_bve_threshold");
            }
            t.consume(":");
            setPresolveBveThreshold((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "filter_sat_postsolve_clauses": {
            if (strict && hasFilterSatPostsolveClauses_) {
              throw ProtoWire.duplicate(t, "filter_sat_postsolve_clauses");
            }
            t.consume(":");
            setFilterSatPostsolveClauses(t.bool());
            break;
          }
          case "presolve_bve_clause_weight": {
            if (strict && hasPresolveBveClauseWeight_) {
              throw ProtoWire.duplicate(t, "presolve_bve_clause_weight");
            }
            t.consume(":");
            setPresolveBveClauseWeight((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "probing_deterministic_time_limit": {
            if (strict && hasProbingDeterministicTimeLimit_) {
              throw ProtoWire.duplicate(t, "probing_deterministic_time_limit");
            }
            t.consume(":");
            setProbingDeterministicTimeLimit(t.floating());
            break;
          }
          case "presolve_probing_deterministic_time_limit": {
            if (strict && hasPresolveProbingDeterministicTimeLimit_) {
              throw ProtoWire.duplicate(t, "presolve_probing_deterministic_time_limit");
            }
            t.consume(":");
            setPresolveProbingDeterministicTimeLimit(t.floating());
            break;
          }
          case "presolve_blocked_clause": {
            if (strict && hasPresolveBlockedClause_) {
              throw ProtoWire.duplicate(t, "presolve_blocked_clause");
            }
            t.consume(":");
            setPresolveBlockedClause(t.bool());
            break;
          }
          case "presolve_use_bva": {
            if (strict && hasPresolveUseBva_) {
              throw ProtoWire.duplicate(t, "presolve_use_bva");
            }
            t.consume(":");
            setPresolveUseBva(t.bool());
            break;
          }
          case "presolve_bva_threshold": {
            if (strict && hasPresolveBvaThreshold_) {
              throw ProtoWire.duplicate(t, "presolve_bva_threshold");
            }
            t.consume(":");
            setPresolveBvaThreshold((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_presolve_iterations": {
            if (strict && hasMaxPresolveIterations_) {
              throw ProtoWire.duplicate(t, "max_presolve_iterations");
            }
            t.consume(":");
            setMaxPresolveIterations((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "cp_model_presolve": {
            if (strict && hasCpModelPresolve_) {
              throw ProtoWire.duplicate(t, "cp_model_presolve");
            }
            t.consume(":");
            setCpModelPresolve(t.bool());
            break;
          }
          case "cp_model_direct_solve": {
            if (strict && hasCpModelDirectSolve_) {
              throw ProtoWire.duplicate(t, "cp_model_direct_solve");
            }
            t.consume(":");
            setCpModelDirectSolve(t.bool());
            break;
          }
          case "cp_model_probing_level": {
            if (strict && hasCpModelProbingLevel_) {
              throw ProtoWire.duplicate(t, "cp_model_probing_level");
            }
            t.consume(":");
            setCpModelProbingLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "cp_model_use_sat_presolve": {
            if (strict && hasCpModelUseSatPresolve_) {
              throw ProtoWire.duplicate(t, "cp_model_use_sat_presolve");
            }
            t.consume(":");
            setCpModelUseSatPresolve(t.bool());
            break;
          }
          case "cp_model_pure_sat_presolve": {
            if (strict && hasCpModelPureSatPresolve_) {
              throw ProtoWire.duplicate(t, "cp_model_pure_sat_presolve");
            }
            t.consume(":");
            setCpModelPureSatPresolve(t.bool());
            break;
          }
          case "load_at_most_ones_in_sat_presolve": {
            if (strict && hasLoadAtMostOnesInSatPresolve_) {
              throw ProtoWire.duplicate(t, "load_at_most_ones_in_sat_presolve");
            }
            t.consume(":");
            setLoadAtMostOnesInSatPresolve(t.bool());
            break;
          }
          case "remove_fixed_variables_early": {
            if (strict && hasRemoveFixedVariablesEarly_) {
              throw ProtoWire.duplicate(t, "remove_fixed_variables_early");
            }
            t.consume(":");
            setRemoveFixedVariablesEarly(t.bool());
            break;
          }
          case "detect_table_with_cost": {
            if (strict && hasDetectTableWithCost_) {
              throw ProtoWire.duplicate(t, "detect_table_with_cost");
            }
            t.consume(":");
            setDetectTableWithCost(t.bool());
            break;
          }
          case "table_compression_level": {
            if (strict && hasTableCompressionLevel_) {
              throw ProtoWire.duplicate(t, "table_compression_level");
            }
            t.consume(":");
            setTableCompressionLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "expand_alldiff_constraints": {
            if (strict && hasExpandAlldiffConstraints_) {
              throw ProtoWire.duplicate(t, "expand_alldiff_constraints");
            }
            t.consume(":");
            setExpandAlldiffConstraints(t.bool());
            break;
          }
          case "max_alldiff_domain_size": {
            if (strict && hasMaxAlldiffDomainSize_) {
              throw ProtoWire.duplicate(t, "max_alldiff_domain_size");
            }
            t.consume(":");
            setMaxAlldiffDomainSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "expand_reservoir_constraints": {
            if (strict && hasExpandReservoirConstraints_) {
              throw ProtoWire.duplicate(t, "expand_reservoir_constraints");
            }
            t.consume(":");
            setExpandReservoirConstraints(t.bool());
            break;
          }
          case "max_domain_size_for_linear2_expansion": {
            if (strict && hasMaxDomainSizeForLinear2Expansion_) {
              throw ProtoWire.duplicate(t, "max_domain_size_for_linear2_expansion");
            }
            t.consume(":");
            setMaxDomainSizeForLinear2Expansion((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "expand_reservoir_using_circuit": {
            if (strict && hasExpandReservoirUsingCircuit_) {
              throw ProtoWire.duplicate(t, "expand_reservoir_using_circuit");
            }
            t.consume(":");
            setExpandReservoirUsingCircuit(t.bool());
            break;
          }
          case "encode_cumulative_as_reservoir": {
            if (strict && hasEncodeCumulativeAsReservoir_) {
              throw ProtoWire.duplicate(t, "encode_cumulative_as_reservoir");
            }
            t.consume(":");
            setEncodeCumulativeAsReservoir(t.bool());
            break;
          }
          case "max_lin_max_size_for_expansion": {
            if (strict && hasMaxLinMaxSizeForExpansion_) {
              throw ProtoWire.duplicate(t, "max_lin_max_size_for_expansion");
            }
            t.consume(":");
            setMaxLinMaxSizeForExpansion((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "disable_constraint_expansion": {
            if (strict && hasDisableConstraintExpansion_) {
              throw ProtoWire.duplicate(t, "disable_constraint_expansion");
            }
            t.consume(":");
            setDisableConstraintExpansion(t.bool());
            break;
          }
          case "encode_complex_linear_constraint_with_integer": {
            if (strict && hasEncodeComplexLinearConstraintWithInteger_) {
              throw ProtoWire.duplicate(t, "encode_complex_linear_constraint_with_integer");
            }
            t.consume(":");
            setEncodeComplexLinearConstraintWithInteger(t.bool());
            break;
          }
          case "detect_encoded_complex_domain": {
            if (strict && hasDetectEncodedComplexDomain_) {
              throw ProtoWire.duplicate(t, "detect_encoded_complex_domain");
            }
            t.consume(":");
            setDetectEncodedComplexDomain(t.bool());
            break;
          }
          case "merge_no_overlap_work_limit": {
            if (strict && hasMergeNoOverlapWorkLimit_) {
              throw ProtoWire.duplicate(t, "merge_no_overlap_work_limit");
            }
            t.consume(":");
            setMergeNoOverlapWorkLimit(t.floating());
            break;
          }
          case "merge_at_most_one_work_limit": {
            if (strict && hasMergeAtMostOneWorkLimit_) {
              throw ProtoWire.duplicate(t, "merge_at_most_one_work_limit");
            }
            t.consume(":");
            setMergeAtMostOneWorkLimit(t.floating());
            break;
          }
          case "presolve_substitution_level": {
            if (strict && hasPresolveSubstitutionLevel_) {
              throw ProtoWire.duplicate(t, "presolve_substitution_level");
            }
            t.consume(":");
            setPresolveSubstitutionLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "presolve_extract_integer_enforcement": {
            if (strict && hasPresolveExtractIntegerEnforcement_) {
              throw ProtoWire.duplicate(t, "presolve_extract_integer_enforcement");
            }
            t.consume(":");
            setPresolveExtractIntegerEnforcement(t.bool());
            break;
          }
          case "presolve_inclusion_work_limit": {
            if (strict && hasPresolveInclusionWorkLimit_) {
              throw ProtoWire.duplicate(t, "presolve_inclusion_work_limit");
            }
            t.consume(":");
            setPresolveInclusionWorkLimit(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "ignore_names": {
            if (strict && hasIgnoreNames_) {
              throw ProtoWire.duplicate(t, "ignore_names");
            }
            t.consume(":");
            setIgnoreNames(t.bool());
            break;
          }
          case "infer_all_diffs": {
            if (strict && hasInferAllDiffs_) {
              throw ProtoWire.duplicate(t, "infer_all_diffs");
            }
            t.consume(":");
            setInferAllDiffs(t.bool());
            break;
          }
          case "find_big_linear_overlap": {
            if (strict && hasFindBigLinearOverlap_) {
              throw ProtoWire.duplicate(t, "find_big_linear_overlap");
            }
            t.consume(":");
            setFindBigLinearOverlap(t.bool());
            break;
          }
          case "find_clauses_that_are_exactly_one": {
            if (strict && hasFindClausesThatAreExactlyOne_) {
              throw ProtoWire.duplicate(t, "find_clauses_that_are_exactly_one");
            }
            t.consume(":");
            setFindClausesThatAreExactlyOne(t.bool());
            break;
          }
          case "use_sat_inprocessing": {
            if (strict && hasUseSatInprocessing_) {
              throw ProtoWire.duplicate(t, "use_sat_inprocessing");
            }
            t.consume(":");
            setUseSatInprocessing(t.bool());
            break;
          }
          case "inprocessing_dtime_ratio": {
            if (strict && hasInprocessingDtimeRatio_) {
              throw ProtoWire.duplicate(t, "inprocessing_dtime_ratio");
            }
            t.consume(":");
            setInprocessingDtimeRatio(t.floating());
            break;
          }
          case "inprocessing_probing_dtime": {
            if (strict && hasInprocessingProbingDtime_) {
              throw ProtoWire.duplicate(t, "inprocessing_probing_dtime");
            }
            t.consume(":");
            setInprocessingProbingDtime(t.floating());
            break;
          }
          case "inprocessing_minimization_dtime": {
            if (strict && hasInprocessingMinimizationDtime_) {
              throw ProtoWire.duplicate(t, "inprocessing_minimization_dtime");
            }
            t.consume(":");
            setInprocessingMinimizationDtime(t.floating());
            break;
          }
          case "inprocessing_minimization_use_conflict_analysis": {
            if (strict && hasInprocessingMinimizationUseConflictAnalysis_) {
              throw ProtoWire.duplicate(t, "inprocessing_minimization_use_conflict_analysis");
            }
            t.consume(":");
            setInprocessingMinimizationUseConflictAnalysis(t.bool());
            break;
          }
          case "num_ordering_in_vivification": {
            if (strict && hasNumOrderingInVivification_) {
              throw ProtoWire.duplicate(t, "num_ordering_in_vivification");
            }
            t.consume(":");
            setNumOrderingInVivification((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "inprocessing_use_congruence_closure": {
            if (strict && hasInprocessingUseCongruenceClosure_) {
              throw ProtoWire.duplicate(t, "inprocessing_use_congruence_closure");
            }
            t.consume(":");
            setInprocessingUseCongruenceClosure(t.bool());
            break;
          }
          case "inprocessing_detect_and_sweep_circuit": {
            if (strict && hasInprocessingDetectAndSweepCircuit_) {
              throw ProtoWire.duplicate(t, "inprocessing_detect_and_sweep_circuit");
            }
            t.consume(":");
            setInprocessingDetectAndSweepCircuit(t.bool());
            break;
          }
          case "inprocessing_use_sat_sweeping": {
            if (strict && hasInprocessingUseSatSweeping_) {
              throw ProtoWire.duplicate(t, "inprocessing_use_sat_sweeping");
            }
            t.consume(":");
            setInprocessingUseSatSweeping(t.bool());
            break;
          }
          case "inprocessing_use_transitive_reduction": {
            if (strict && hasInprocessingUseTransitiveReduction_) {
              throw ProtoWire.duplicate(t, "inprocessing_use_transitive_reduction");
            }
            t.consume(":");
            setInprocessingUseTransitiveReduction(t.bool());
            break;
          }
          case "num_workers": {
            if (strict && hasNumWorkers_) {
              throw ProtoWire.duplicate(t, "num_workers");
            }
            t.consume(":");
            setNumWorkers((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "num_search_workers": {
            if (strict && hasNumSearchWorkers_) {
              throw ProtoWire.duplicate(t, "num_search_workers");
            }
            t.consume(":");
            setNumSearchWorkers((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "num_full_subsolvers": {
            if (strict && hasNumFullSubsolvers_) {
              throw ProtoWire.duplicate(t, "num_full_subsolvers");
            }
            t.consume(":");
            setNumFullSubsolvers((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "subsolvers": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSubsolvers(t.string());
                } while (t.nextInList());
              }
            } else {
              addSubsolvers(t.string());
            }
            break;
          }
          case "extra_subsolvers": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addExtraSubsolvers(t.string());
                } while (t.nextInList());
              }
            } else {
              addExtraSubsolvers(t.string());
            }
            break;
          }
          case "ignore_subsolvers": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addIgnoreSubsolvers(t.string());
                } while (t.nextInList());
              }
            } else {
              addIgnoreSubsolvers(t.string());
            }
            break;
          }
          case "filter_subsolvers": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFilterSubsolvers(t.string());
                } while (t.nextInList());
              }
            } else {
              addFilterSubsolvers(t.string());
            }
            break;
          }
          case "subsolver_params": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSubsolverParamsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addSubsolverParamsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "interleave_search": {
            if (strict && hasInterleaveSearch_) {
              throw ProtoWire.duplicate(t, "interleave_search");
            }
            t.consume(":");
            setInterleaveSearch(t.bool());
            break;
          }
          case "interleave_batch_size": {
            if (strict && hasInterleaveBatchSize_) {
              throw ProtoWire.duplicate(t, "interleave_batch_size");
            }
            t.consume(":");
            setInterleaveBatchSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "share_objective_bounds": {
            if (strict && hasShareObjectiveBounds_) {
              throw ProtoWire.duplicate(t, "share_objective_bounds");
            }
            t.consume(":");
            setShareObjectiveBounds(t.bool());
            break;
          }
          case "share_level_zero_bounds": {
            if (strict && hasShareLevelZeroBounds_) {
              throw ProtoWire.duplicate(t, "share_level_zero_bounds");
            }
            t.consume(":");
            setShareLevelZeroBounds(t.bool());
            break;
          }
          case "share_linear2_bounds": {
            if (strict && hasShareLinear2Bounds_) {
              throw ProtoWire.duplicate(t, "share_linear2_bounds");
            }
            t.consume(":");
            setShareLinear2Bounds(t.bool());
            break;
          }
          case "share_binary_clauses": {
            if (strict && hasShareBinaryClauses_) {
              throw ProtoWire.duplicate(t, "share_binary_clauses");
            }
            t.consume(":");
            setShareBinaryClauses(t.bool());
            break;
          }
          case "share_glue_clauses": {
            if (strict && hasShareGlueClauses_) {
              throw ProtoWire.duplicate(t, "share_glue_clauses");
            }
            t.consume(":");
            setShareGlueClauses(t.bool());
            break;
          }
          case "minimize_shared_clauses": {
            if (strict && hasMinimizeSharedClauses_) {
              throw ProtoWire.duplicate(t, "minimize_shared_clauses");
            }
            t.consume(":");
            setMinimizeSharedClauses(t.bool());
            break;
          }
          case "share_glue_clauses_dtime": {
            if (strict && hasShareGlueClausesDtime_) {
              throw ProtoWire.duplicate(t, "share_glue_clauses_dtime");
            }
            t.consume(":");
            setShareGlueClausesDtime(t.floating());
            break;
          }
          case "check_lrat_proof": {
            if (strict && hasCheckLratProof_) {
              throw ProtoWire.duplicate(t, "check_lrat_proof");
            }
            t.consume(":");
            setCheckLratProof(t.bool());
            break;
          }
          case "check_merged_lrat_proof": {
            if (strict && hasCheckMergedLratProof_) {
              throw ProtoWire.duplicate(t, "check_merged_lrat_proof");
            }
            t.consume(":");
            setCheckMergedLratProof(t.bool());
            break;
          }
          case "output_lrat_proof": {
            if (strict && hasOutputLratProof_) {
              throw ProtoWire.duplicate(t, "output_lrat_proof");
            }
            t.consume(":");
            setOutputLratProof(t.bool());
            break;
          }
          case "debug_postsolve_with_full_solver": {
            if (strict && hasDebugPostsolveWithFullSolver_) {
              throw ProtoWire.duplicate(t, "debug_postsolve_with_full_solver");
            }
            t.consume(":");
            setDebugPostsolveWithFullSolver(t.bool());
            break;
          }
          case "debug_max_num_presolve_operations": {
            if (strict && hasDebugMaxNumPresolveOperations_) {
              throw ProtoWire.duplicate(t, "debug_max_num_presolve_operations");
            }
            t.consume(":");
            setDebugMaxNumPresolveOperations((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "debug_crash_on_bad_hint": {
            if (strict && hasDebugCrashOnBadHint_) {
              throw ProtoWire.duplicate(t, "debug_crash_on_bad_hint");
            }
            t.consume(":");
            setDebugCrashOnBadHint(t.bool());
            break;
          }
          case "debug_crash_if_presolve_breaks_hint": {
            if (strict && hasDebugCrashIfPresolveBreaksHint_) {
              throw ProtoWire.duplicate(t, "debug_crash_if_presolve_breaks_hint");
            }
            t.consume(":");
            setDebugCrashIfPresolveBreaksHint(t.bool());
            break;
          }
          case "debug_crash_if_lrat_check_fails": {
            if (strict && hasDebugCrashIfLratCheckFails_) {
              throw ProtoWire.duplicate(t, "debug_crash_if_lrat_check_fails");
            }
            t.consume(":");
            setDebugCrashIfLratCheckFails(t.bool());
            break;
          }
          case "use_optimization_hints": {
            if (strict && hasUseOptimizationHints_) {
              throw ProtoWire.duplicate(t, "use_optimization_hints");
            }
            t.consume(":");
            setUseOptimizationHints(t.bool());
            break;
          }
          case "core_minimization_level": {
            if (strict && hasCoreMinimizationLevel_) {
              throw ProtoWire.duplicate(t, "core_minimization_level");
            }
            t.consume(":");
            setCoreMinimizationLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "find_multiple_cores": {
            if (strict && hasFindMultipleCores_) {
              throw ProtoWire.duplicate(t, "find_multiple_cores");
            }
            t.consume(":");
            setFindMultipleCores(t.bool());
            break;
          }
          case "cover_optimization": {
            if (strict && hasCoverOptimization_) {
              throw ProtoWire.duplicate(t, "cover_optimization");
            }
            t.consume(":");
            setCoverOptimization(t.bool());
            break;
          }
          case "max_sat_assumption_order": {
            if (strict && hasMaxSatAssumptionOrder_) {
              throw ProtoWire.duplicate(t, "max_sat_assumption_order");
            }
            t.consume(":");
            setMaxSatAssumptionOrderValue(t.enumValue(SatParameters.MaxSatAssumptionOrder.class, SatParameters.MaxSatAssumptionOrder::forNumber, SatParameters.MaxSatAssumptionOrder::getNumber, true, "max_sat_assumption_order"));
            break;
          }
          case "max_sat_reverse_assumption_order": {
            if (strict && hasMaxSatReverseAssumptionOrder_) {
              throw ProtoWire.duplicate(t, "max_sat_reverse_assumption_order");
            }
            t.consume(":");
            setMaxSatReverseAssumptionOrder(t.bool());
            break;
          }
          case "max_sat_stratification": {
            if (strict && hasMaxSatStratification_) {
              throw ProtoWire.duplicate(t, "max_sat_stratification");
            }
            t.consume(":");
            setMaxSatStratificationValue(t.enumValue(SatParameters.MaxSatStratificationAlgorithm.class, SatParameters.MaxSatStratificationAlgorithm::forNumber, SatParameters.MaxSatStratificationAlgorithm::getNumber, true, "max_sat_stratification"));
            break;
          }
          case "propagation_loop_detection_factor": {
            if (strict && hasPropagationLoopDetectionFactor_) {
              throw ProtoWire.duplicate(t, "propagation_loop_detection_factor");
            }
            t.consume(":");
            setPropagationLoopDetectionFactor(t.floating());
            break;
          }
          case "use_precedences_in_disjunctive_constraint": {
            if (strict && hasUsePrecedencesInDisjunctiveConstraint_) {
              throw ProtoWire.duplicate(t, "use_precedences_in_disjunctive_constraint");
            }
            t.consume(":");
            setUsePrecedencesInDisjunctiveConstraint(t.bool());
            break;
          }
          case "transitive_precedences_work_limit": {
            if (strict && hasTransitivePrecedencesWorkLimit_) {
              throw ProtoWire.duplicate(t, "transitive_precedences_work_limit");
            }
            t.consume(":");
            setTransitivePrecedencesWorkLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_size_to_create_precedence_literals_in_disjunctive": {
            if (strict && hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_) {
              throw ProtoWire.duplicate(t, "max_size_to_create_precedence_literals_in_disjunctive");
            }
            t.consume(":");
            setMaxSizeToCreatePrecedenceLiteralsInDisjunctive((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_strong_propagation_in_disjunctive": {
            if (strict && hasUseStrongPropagationInDisjunctive_) {
              throw ProtoWire.duplicate(t, "use_strong_propagation_in_disjunctive");
            }
            t.consume(":");
            setUseStrongPropagationInDisjunctive(t.bool());
            break;
          }
          case "use_dynamic_precedence_in_disjunctive": {
            if (strict && hasUseDynamicPrecedenceInDisjunctive_) {
              throw ProtoWire.duplicate(t, "use_dynamic_precedence_in_disjunctive");
            }
            t.consume(":");
            setUseDynamicPrecedenceInDisjunctive(t.bool());
            break;
          }
          case "use_dynamic_precedence_in_cumulative": {
            if (strict && hasUseDynamicPrecedenceInCumulative_) {
              throw ProtoWire.duplicate(t, "use_dynamic_precedence_in_cumulative");
            }
            t.consume(":");
            setUseDynamicPrecedenceInCumulative(t.bool());
            break;
          }
          case "use_overload_checker_in_cumulative": {
            if (strict && hasUseOverloadCheckerInCumulative_) {
              throw ProtoWire.duplicate(t, "use_overload_checker_in_cumulative");
            }
            t.consume(":");
            setUseOverloadCheckerInCumulative(t.bool());
            break;
          }
          case "use_conservative_scale_overload_checker": {
            if (strict && hasUseConservativeScaleOverloadChecker_) {
              throw ProtoWire.duplicate(t, "use_conservative_scale_overload_checker");
            }
            t.consume(":");
            setUseConservativeScaleOverloadChecker(t.bool());
            break;
          }
          case "use_timetable_edge_finding_in_cumulative": {
            if (strict && hasUseTimetableEdgeFindingInCumulative_) {
              throw ProtoWire.duplicate(t, "use_timetable_edge_finding_in_cumulative");
            }
            t.consume(":");
            setUseTimetableEdgeFindingInCumulative(t.bool());
            break;
          }
          case "max_num_intervals_for_timetable_edge_finding": {
            if (strict && hasMaxNumIntervalsForTimetableEdgeFinding_) {
              throw ProtoWire.duplicate(t, "max_num_intervals_for_timetable_edge_finding");
            }
            t.consume(":");
            setMaxNumIntervalsForTimetableEdgeFinding((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_hard_precedences_in_cumulative": {
            if (strict && hasUseHardPrecedencesInCumulative_) {
              throw ProtoWire.duplicate(t, "use_hard_precedences_in_cumulative");
            }
            t.consume(":");
            setUseHardPrecedencesInCumulative(t.bool());
            break;
          }
          case "exploit_all_precedences": {
            if (strict && hasExploitAllPrecedences_) {
              throw ProtoWire.duplicate(t, "exploit_all_precedences");
            }
            t.consume(":");
            setExploitAllPrecedences(t.bool());
            break;
          }
          case "use_disjunctive_constraint_in_cumulative": {
            if (strict && hasUseDisjunctiveConstraintInCumulative_) {
              throw ProtoWire.duplicate(t, "use_disjunctive_constraint_in_cumulative");
            }
            t.consume(":");
            setUseDisjunctiveConstraintInCumulative(t.bool());
            break;
          }
          case "no_overlap_2d_boolean_relations_limit": {
            if (strict && hasNoOverlap2DBooleanRelationsLimit_) {
              throw ProtoWire.duplicate(t, "no_overlap_2d_boolean_relations_limit");
            }
            t.consume(":");
            setNoOverlap2DBooleanRelationsLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_timetabling_in_no_overlap_2d": {
            if (strict && hasUseTimetablingInNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "use_timetabling_in_no_overlap_2d");
            }
            t.consume(":");
            setUseTimetablingInNoOverlap2D(t.bool());
            break;
          }
          case "use_energetic_reasoning_in_no_overlap_2d": {
            if (strict && hasUseEnergeticReasoningInNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "use_energetic_reasoning_in_no_overlap_2d");
            }
            t.consume(":");
            setUseEnergeticReasoningInNoOverlap2D(t.bool());
            break;
          }
          case "use_area_energetic_reasoning_in_no_overlap_2d": {
            if (strict && hasUseAreaEnergeticReasoningInNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "use_area_energetic_reasoning_in_no_overlap_2d");
            }
            t.consume(":");
            setUseAreaEnergeticReasoningInNoOverlap2D(t.bool());
            break;
          }
          case "use_try_edge_reasoning_in_no_overlap_2d": {
            if (strict && hasUseTryEdgeReasoningInNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "use_try_edge_reasoning_in_no_overlap_2d");
            }
            t.consume(":");
            setUseTryEdgeReasoningInNoOverlap2D(t.bool());
            break;
          }
          case "max_pairs_pairwise_reasoning_in_no_overlap_2d": {
            if (strict && hasMaxPairsPairwiseReasoningInNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "max_pairs_pairwise_reasoning_in_no_overlap_2d");
            }
            t.consume(":");
            setMaxPairsPairwiseReasoningInNoOverlap2D((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "maximum_regions_to_split_in_disconnected_no_overlap_2d": {
            if (strict && hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_) {
              throw ProtoWire.duplicate(t, "maximum_regions_to_split_in_disconnected_no_overlap_2d");
            }
            t.consume(":");
            setMaximumRegionsToSplitInDisconnectedNoOverlap2D((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_linear3_for_no_overlap_2d_precedences": {
            if (strict && hasUseLinear3ForNoOverlap2DPrecedences_) {
              throw ProtoWire.duplicate(t, "use_linear3_for_no_overlap_2d_precedences");
            }
            t.consume(":");
            setUseLinear3ForNoOverlap2DPrecedences(t.bool());
            break;
          }
          case "use_dual_scheduling_heuristics": {
            if (strict && hasUseDualSchedulingHeuristics_) {
              throw ProtoWire.duplicate(t, "use_dual_scheduling_heuristics");
            }
            t.consume(":");
            setUseDualSchedulingHeuristics(t.bool());
            break;
          }
          case "use_all_different_for_circuit": {
            if (strict && hasUseAllDifferentForCircuit_) {
              throw ProtoWire.duplicate(t, "use_all_different_for_circuit");
            }
            t.consume(":");
            setUseAllDifferentForCircuit(t.bool());
            break;
          }
          case "routing_cut_subset_size_for_binary_relation_bound": {
            if (strict && hasRoutingCutSubsetSizeForBinaryRelationBound_) {
              throw ProtoWire.duplicate(t, "routing_cut_subset_size_for_binary_relation_bound");
            }
            t.consume(":");
            setRoutingCutSubsetSizeForBinaryRelationBound((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "routing_cut_subset_size_for_tight_binary_relation_bound": {
            if (strict && hasRoutingCutSubsetSizeForTightBinaryRelationBound_) {
              throw ProtoWire.duplicate(t, "routing_cut_subset_size_for_tight_binary_relation_bound");
            }
            t.consume(":");
            setRoutingCutSubsetSizeForTightBinaryRelationBound((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "routing_cut_subset_size_for_exact_binary_relation_bound": {
            if (strict && hasRoutingCutSubsetSizeForExactBinaryRelationBound_) {
              throw ProtoWire.duplicate(t, "routing_cut_subset_size_for_exact_binary_relation_bound");
            }
            t.consume(":");
            setRoutingCutSubsetSizeForExactBinaryRelationBound((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "routing_cut_subset_size_for_shortest_paths_bound": {
            if (strict && hasRoutingCutSubsetSizeForShortestPathsBound_) {
              throw ProtoWire.duplicate(t, "routing_cut_subset_size_for_shortest_paths_bound");
            }
            t.consume(":");
            setRoutingCutSubsetSizeForShortestPathsBound((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "routing_cut_dp_effort": {
            if (strict && hasRoutingCutDpEffort_) {
              throw ProtoWire.duplicate(t, "routing_cut_dp_effort");
            }
            t.consume(":");
            setRoutingCutDpEffort(t.floating());
            break;
          }
          case "routing_cut_max_infeasible_path_length": {
            if (strict && hasRoutingCutMaxInfeasiblePathLength_) {
              throw ProtoWire.duplicate(t, "routing_cut_max_infeasible_path_length");
            }
            t.consume(":");
            setRoutingCutMaxInfeasiblePathLength((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "search_branching": {
            if (strict && hasSearchBranching_) {
              throw ProtoWire.duplicate(t, "search_branching");
            }
            t.consume(":");
            setSearchBranchingValue(t.enumValue(SatParameters.SearchBranching.class, SatParameters.SearchBranching::forNumber, SatParameters.SearchBranching::getNumber, true, "search_branching"));
            break;
          }
          case "hint_conflict_limit": {
            if (strict && hasHintConflictLimit_) {
              throw ProtoWire.duplicate(t, "hint_conflict_limit");
            }
            t.consume(":");
            setHintConflictLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "try_hint_as_assumptions": {
            if (strict && hasTryHintAsAssumptions_) {
              throw ProtoWire.duplicate(t, "try_hint_as_assumptions");
            }
            t.consume(":");
            setTryHintAsAssumptions(t.bool());
            break;
          }
          case "repair_hint": {
            if (strict && hasRepairHint_) {
              throw ProtoWire.duplicate(t, "repair_hint");
            }
            t.consume(":");
            setRepairHint(t.bool());
            break;
          }
          case "fix_variables_to_their_hinted_value": {
            if (strict && hasFixVariablesToTheirHintedValue_) {
              throw ProtoWire.duplicate(t, "fix_variables_to_their_hinted_value");
            }
            t.consume(":");
            setFixVariablesToTheirHintedValue(t.bool());
            break;
          }
          case "use_probing_search": {
            if (strict && hasUseProbingSearch_) {
              throw ProtoWire.duplicate(t, "use_probing_search");
            }
            t.consume(":");
            setUseProbingSearch(t.bool());
            break;
          }
          case "use_extended_probing": {
            if (strict && hasUseExtendedProbing_) {
              throw ProtoWire.duplicate(t, "use_extended_probing");
            }
            t.consume(":");
            setUseExtendedProbing(t.bool());
            break;
          }
          case "probing_num_combinations_limit": {
            if (strict && hasProbingNumCombinationsLimit_) {
              throw ProtoWire.duplicate(t, "probing_num_combinations_limit");
            }
            t.consume(":");
            setProbingNumCombinationsLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shaving_deterministic_time_in_probing_search": {
            if (strict && hasShavingDeterministicTimeInProbingSearch_) {
              throw ProtoWire.duplicate(t, "shaving_deterministic_time_in_probing_search");
            }
            t.consume(":");
            setShavingDeterministicTimeInProbingSearch(t.floating());
            break;
          }
          case "shaving_search_deterministic_time": {
            if (strict && hasShavingSearchDeterministicTime_) {
              throw ProtoWire.duplicate(t, "shaving_search_deterministic_time");
            }
            t.consume(":");
            setShavingSearchDeterministicTime(t.floating());
            break;
          }
          case "shaving_search_use_random_search": {
            if (strict && hasShavingSearchUseRandomSearch_) {
              throw ProtoWire.duplicate(t, "shaving_search_use_random_search");
            }
            t.consume(":");
            setShavingSearchUseRandomSearch(t.bool());
            break;
          }
          case "shaving_search_restart_limit": {
            if (strict && hasShavingSearchRestartLimit_) {
              throw ProtoWire.duplicate(t, "shaving_search_restart_limit");
            }
            t.consume(":");
            setShavingSearchRestartLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shaving_search_threshold": {
            if (strict && hasShavingSearchThreshold_) {
              throw ProtoWire.duplicate(t, "shaving_search_threshold");
            }
            t.consume(":");
            setShavingSearchThreshold(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "objective_shaving_search_threshold": {
            if (strict && hasObjectiveShavingSearchThreshold_) {
              throw ProtoWire.duplicate(t, "objective_shaving_search_threshold");
            }
            t.consume(":");
            setObjectiveShavingSearchThreshold(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "use_objective_lb_search": {
            if (strict && hasUseObjectiveLbSearch_) {
              throw ProtoWire.duplicate(t, "use_objective_lb_search");
            }
            t.consume(":");
            setUseObjectiveLbSearch(t.bool());
            break;
          }
          case "use_objective_shaving_search": {
            if (strict && hasUseObjectiveShavingSearch_) {
              throw ProtoWire.duplicate(t, "use_objective_shaving_search");
            }
            t.consume(":");
            setUseObjectiveShavingSearch(t.bool());
            break;
          }
          case "variables_shaving_level": {
            if (strict && hasVariablesShavingLevel_) {
              throw ProtoWire.duplicate(t, "variables_shaving_level");
            }
            t.consume(":");
            setVariablesShavingLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "pseudo_cost_reliability_threshold": {
            if (strict && hasPseudoCostReliabilityThreshold_) {
              throw ProtoWire.duplicate(t, "pseudo_cost_reliability_threshold");
            }
            t.consume(":");
            setPseudoCostReliabilityThreshold(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "optimize_with_core": {
            if (strict && hasOptimizeWithCore_) {
              throw ProtoWire.duplicate(t, "optimize_with_core");
            }
            t.consume(":");
            setOptimizeWithCore(t.bool());
            break;
          }
          case "optimize_with_lb_tree_search": {
            if (strict && hasOptimizeWithLbTreeSearch_) {
              throw ProtoWire.duplicate(t, "optimize_with_lb_tree_search");
            }
            t.consume(":");
            setOptimizeWithLbTreeSearch(t.bool());
            break;
          }
          case "save_lp_basis_in_lb_tree_search": {
            if (strict && hasSaveLpBasisInLbTreeSearch_) {
              throw ProtoWire.duplicate(t, "save_lp_basis_in_lb_tree_search");
            }
            t.consume(":");
            setSaveLpBasisInLbTreeSearch(t.bool());
            break;
          }
          case "binary_search_num_conflicts": {
            if (strict && hasBinarySearchNumConflicts_) {
              throw ProtoWire.duplicate(t, "binary_search_num_conflicts");
            }
            t.consume(":");
            setBinarySearchNumConflicts((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "optimize_with_max_hs": {
            if (strict && hasOptimizeWithMaxHs_) {
              throw ProtoWire.duplicate(t, "optimize_with_max_hs");
            }
            t.consume(":");
            setOptimizeWithMaxHs(t.bool());
            break;
          }
          case "use_feasibility_jump": {
            if (strict && hasUseFeasibilityJump_) {
              throw ProtoWire.duplicate(t, "use_feasibility_jump");
            }
            t.consume(":");
            setUseFeasibilityJump(t.bool());
            break;
          }
          case "use_ls_only": {
            if (strict && hasUseLsOnly_) {
              throw ProtoWire.duplicate(t, "use_ls_only");
            }
            t.consume(":");
            setUseLsOnly(t.bool());
            break;
          }
          case "feasibility_jump_decay": {
            if (strict && hasFeasibilityJumpDecay_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_decay");
            }
            t.consume(":");
            setFeasibilityJumpDecay(t.floating());
            break;
          }
          case "feasibility_jump_linearization_level": {
            if (strict && hasFeasibilityJumpLinearizationLevel_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_linearization_level");
            }
            t.consume(":");
            setFeasibilityJumpLinearizationLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "feasibility_jump_restart_factor": {
            if (strict && hasFeasibilityJumpRestartFactor_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_restart_factor");
            }
            t.consume(":");
            setFeasibilityJumpRestartFactor((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "feasibility_jump_batch_dtime": {
            if (strict && hasFeasibilityJumpBatchDtime_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_batch_dtime");
            }
            t.consume(":");
            setFeasibilityJumpBatchDtime(t.floating());
            break;
          }
          case "feasibility_jump_var_randomization_probability": {
            if (strict && hasFeasibilityJumpVarRandomizationProbability_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_var_randomization_probability");
            }
            t.consume(":");
            setFeasibilityJumpVarRandomizationProbability(t.floating());
            break;
          }
          case "feasibility_jump_var_perburbation_range_ratio": {
            if (strict && hasFeasibilityJumpVarPerburbationRangeRatio_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_var_perburbation_range_ratio");
            }
            t.consume(":");
            setFeasibilityJumpVarPerburbationRangeRatio(t.floating());
            break;
          }
          case "feasibility_jump_enable_restarts": {
            if (strict && hasFeasibilityJumpEnableRestarts_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_enable_restarts");
            }
            t.consume(":");
            setFeasibilityJumpEnableRestarts(t.bool());
            break;
          }
          case "feasibility_jump_max_expanded_constraint_size": {
            if (strict && hasFeasibilityJumpMaxExpandedConstraintSize_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_max_expanded_constraint_size");
            }
            t.consume(":");
            setFeasibilityJumpMaxExpandedConstraintSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "feasibility_jump_ignore_redundant_constraints": {
            if (strict && hasFeasibilityJumpIgnoreRedundantConstraints_) {
              throw ProtoWire.duplicate(t, "feasibility_jump_ignore_redundant_constraints");
            }
            t.consume(":");
            setFeasibilityJumpIgnoreRedundantConstraints(t.bool());
            break;
          }
          case "lns_ignore_redundant_constraints": {
            if (strict && hasLnsIgnoreRedundantConstraints_) {
              throw ProtoWire.duplicate(t, "lns_ignore_redundant_constraints");
            }
            t.consume(":");
            setLnsIgnoreRedundantConstraints(t.bool());
            break;
          }
          case "num_violation_ls": {
            if (strict && hasNumViolationLs_) {
              throw ProtoWire.duplicate(t, "num_violation_ls");
            }
            t.consume(":");
            setNumViolationLs((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "violation_ls_perturbation_period": {
            if (strict && hasViolationLsPerturbationPeriod_) {
              throw ProtoWire.duplicate(t, "violation_ls_perturbation_period");
            }
            t.consume(":");
            setViolationLsPerturbationPeriod((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shared_tree_num_workers": {
            if (strict && hasSharedTreeNumWorkers_) {
              throw ProtoWire.duplicate(t, "shared_tree_num_workers");
            }
            t.consume(":");
            setSharedTreeNumWorkers((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_shared_tree_search": {
            if (strict && hasUseSharedTreeSearch_) {
              throw ProtoWire.duplicate(t, "use_shared_tree_search");
            }
            t.consume(":");
            setUseSharedTreeSearch(t.bool());
            break;
          }
          case "shared_tree_worker_min_restarts_per_subtree": {
            if (strict && hasSharedTreeWorkerMinRestartsPerSubtree_) {
              throw ProtoWire.duplicate(t, "shared_tree_worker_min_restarts_per_subtree");
            }
            t.consume(":");
            setSharedTreeWorkerMinRestartsPerSubtree((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shared_tree_worker_enable_trail_sharing": {
            if (strict && hasSharedTreeWorkerEnableTrailSharing_) {
              throw ProtoWire.duplicate(t, "shared_tree_worker_enable_trail_sharing");
            }
            t.consume(":");
            setSharedTreeWorkerEnableTrailSharing(t.bool());
            break;
          }
          case "shared_tree_worker_enable_phase_sharing": {
            if (strict && hasSharedTreeWorkerEnablePhaseSharing_) {
              throw ProtoWire.duplicate(t, "shared_tree_worker_enable_phase_sharing");
            }
            t.consume(":");
            setSharedTreeWorkerEnablePhaseSharing(t.bool());
            break;
          }
          case "shared_tree_worker_propagate_intermediate_nodes": {
            if (strict && hasSharedTreeWorkerPropagateIntermediateNodes_) {
              throw ProtoWire.duplicate(t, "shared_tree_worker_propagate_intermediate_nodes");
            }
            t.consume(":");
            setSharedTreeWorkerPropagateIntermediateNodes(t.bool());
            break;
          }
          case "shared_tree_open_leaves_per_worker": {
            if (strict && hasSharedTreeOpenLeavesPerWorker_) {
              throw ProtoWire.duplicate(t, "shared_tree_open_leaves_per_worker");
            }
            t.consume(":");
            setSharedTreeOpenLeavesPerWorker(t.floating());
            break;
          }
          case "shared_tree_max_nodes_per_worker": {
            if (strict && hasSharedTreeMaxNodesPerWorker_) {
              throw ProtoWire.duplicate(t, "shared_tree_max_nodes_per_worker");
            }
            t.consume(":");
            setSharedTreeMaxNodesPerWorker((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shared_tree_split_strategy": {
            if (strict && hasSharedTreeSplitStrategy_) {
              throw ProtoWire.duplicate(t, "shared_tree_split_strategy");
            }
            t.consume(":");
            setSharedTreeSplitStrategyValue(t.enumValue(SatParameters.SharedTreeSplitStrategy.class, SatParameters.SharedTreeSplitStrategy::forNumber, SatParameters.SharedTreeSplitStrategy::getNumber, true, "shared_tree_split_strategy"));
            break;
          }
          case "shared_tree_balance_tolerance": {
            if (strict && hasSharedTreeBalanceTolerance_) {
              throw ProtoWire.duplicate(t, "shared_tree_balance_tolerance");
            }
            t.consume(":");
            setSharedTreeBalanceTolerance((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "shared_tree_split_min_dtime": {
            if (strict && hasSharedTreeSplitMinDtime_) {
              throw ProtoWire.duplicate(t, "shared_tree_split_min_dtime");
            }
            t.consume(":");
            setSharedTreeSplitMinDtime(t.floating());
            break;
          }
          case "enumerate_all_solutions": {
            if (strict && hasEnumerateAllSolutions_) {
              throw ProtoWire.duplicate(t, "enumerate_all_solutions");
            }
            t.consume(":");
            setEnumerateAllSolutions(t.bool());
            break;
          }
          case "keep_all_feasible_solutions_in_presolve": {
            if (strict && hasKeepAllFeasibleSolutionsInPresolve_) {
              throw ProtoWire.duplicate(t, "keep_all_feasible_solutions_in_presolve");
            }
            t.consume(":");
            setKeepAllFeasibleSolutionsInPresolve(t.bool());
            break;
          }
          case "fill_tightened_domains_in_response": {
            if (strict && hasFillTightenedDomainsInResponse_) {
              throw ProtoWire.duplicate(t, "fill_tightened_domains_in_response");
            }
            t.consume(":");
            setFillTightenedDomainsInResponse(t.bool());
            break;
          }
          case "fill_additional_solutions_in_response": {
            if (strict && hasFillAdditionalSolutionsInResponse_) {
              throw ProtoWire.duplicate(t, "fill_additional_solutions_in_response");
            }
            t.consume(":");
            setFillAdditionalSolutionsInResponse(t.bool());
            break;
          }
          case "instantiate_all_variables": {
            if (strict && hasInstantiateAllVariables_) {
              throw ProtoWire.duplicate(t, "instantiate_all_variables");
            }
            t.consume(":");
            setInstantiateAllVariables(t.bool());
            break;
          }
          case "auto_detect_greater_than_at_least_one_of": {
            if (strict && hasAutoDetectGreaterThanAtLeastOneOf_) {
              throw ProtoWire.duplicate(t, "auto_detect_greater_than_at_least_one_of");
            }
            t.consume(":");
            setAutoDetectGreaterThanAtLeastOneOf(t.bool());
            break;
          }
          case "stop_after_first_solution": {
            if (strict && hasStopAfterFirstSolution_) {
              throw ProtoWire.duplicate(t, "stop_after_first_solution");
            }
            t.consume(":");
            setStopAfterFirstSolution(t.bool());
            break;
          }
          case "stop_after_presolve": {
            if (strict && hasStopAfterPresolve_) {
              throw ProtoWire.duplicate(t, "stop_after_presolve");
            }
            t.consume(":");
            setStopAfterPresolve(t.bool());
            break;
          }
          case "stop_after_root_propagation": {
            if (strict && hasStopAfterRootPropagation_) {
              throw ProtoWire.duplicate(t, "stop_after_root_propagation");
            }
            t.consume(":");
            setStopAfterRootPropagation(t.bool());
            break;
          }
          case "lns_initial_difficulty": {
            if (strict && hasLnsInitialDifficulty_) {
              throw ProtoWire.duplicate(t, "lns_initial_difficulty");
            }
            t.consume(":");
            setLnsInitialDifficulty(t.floating());
            break;
          }
          case "lns_initial_deterministic_limit": {
            if (strict && hasLnsInitialDeterministicLimit_) {
              throw ProtoWire.duplicate(t, "lns_initial_deterministic_limit");
            }
            t.consume(":");
            setLnsInitialDeterministicLimit(t.floating());
            break;
          }
          case "use_lns": {
            if (strict && hasUseLns_) {
              throw ProtoWire.duplicate(t, "use_lns");
            }
            t.consume(":");
            setUseLns(t.bool());
            break;
          }
          case "use_lns_only": {
            if (strict && hasUseLnsOnly_) {
              throw ProtoWire.duplicate(t, "use_lns_only");
            }
            t.consume(":");
            setUseLnsOnly(t.bool());
            break;
          }
          case "solution_pool_size": {
            if (strict && hasSolutionPoolSize_) {
              throw ProtoWire.duplicate(t, "solution_pool_size");
            }
            t.consume(":");
            setSolutionPoolSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "solution_pool_diversity_limit": {
            if (strict && hasSolutionPoolDiversityLimit_) {
              throw ProtoWire.duplicate(t, "solution_pool_diversity_limit");
            }
            t.consume(":");
            setSolutionPoolDiversityLimit((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "alternative_pool_size": {
            if (strict && hasAlternativePoolSize_) {
              throw ProtoWire.duplicate(t, "alternative_pool_size");
            }
            t.consume(":");
            setAlternativePoolSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_rins_lns": {
            if (strict && hasUseRinsLns_) {
              throw ProtoWire.duplicate(t, "use_rins_lns");
            }
            t.consume(":");
            setUseRinsLns(t.bool());
            break;
          }
          case "use_feasibility_pump": {
            if (strict && hasUseFeasibilityPump_) {
              throw ProtoWire.duplicate(t, "use_feasibility_pump");
            }
            t.consume(":");
            setUseFeasibilityPump(t.bool());
            break;
          }
          case "use_lb_relax_lns": {
            if (strict && hasUseLbRelaxLns_) {
              throw ProtoWire.duplicate(t, "use_lb_relax_lns");
            }
            t.consume(":");
            setUseLbRelaxLns(t.bool());
            break;
          }
          case "lb_relax_num_workers_threshold": {
            if (strict && hasLbRelaxNumWorkersThreshold_) {
              throw ProtoWire.duplicate(t, "lb_relax_num_workers_threshold");
            }
            t.consume(":");
            setLbRelaxNumWorkersThreshold((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "fp_rounding": {
            if (strict && hasFpRounding_) {
              throw ProtoWire.duplicate(t, "fp_rounding");
            }
            t.consume(":");
            setFpRoundingValue(t.enumValue(SatParameters.FPRoundingMethod.class, SatParameters.FPRoundingMethod::forNumber, SatParameters.FPRoundingMethod::getNumber, true, "fp_rounding"));
            break;
          }
          case "diversify_lns_params": {
            if (strict && hasDiversifyLnsParams_) {
              throw ProtoWire.duplicate(t, "diversify_lns_params");
            }
            t.consume(":");
            setDiversifyLnsParams(t.bool());
            break;
          }
          case "randomize_search": {
            if (strict && hasRandomizeSearch_) {
              throw ProtoWire.duplicate(t, "randomize_search");
            }
            t.consume(":");
            setRandomizeSearch(t.bool());
            break;
          }
          case "search_random_variable_pool_size": {
            if (strict && hasSearchRandomVariablePoolSize_) {
              throw ProtoWire.duplicate(t, "search_random_variable_pool_size");
            }
            t.consume(":");
            setSearchRandomVariablePoolSize(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "push_all_tasks_toward_start": {
            if (strict && hasPushAllTasksTowardStart_) {
              throw ProtoWire.duplicate(t, "push_all_tasks_toward_start");
            }
            t.consume(":");
            setPushAllTasksTowardStart(t.bool());
            break;
          }
          case "use_optional_variables": {
            if (strict && hasUseOptionalVariables_) {
              throw ProtoWire.duplicate(t, "use_optional_variables");
            }
            t.consume(":");
            setUseOptionalVariables(t.bool());
            break;
          }
          case "use_exact_lp_reason": {
            if (strict && hasUseExactLpReason_) {
              throw ProtoWire.duplicate(t, "use_exact_lp_reason");
            }
            t.consume(":");
            setUseExactLpReason(t.bool());
            break;
          }
          case "use_combined_no_overlap": {
            if (strict && hasUseCombinedNoOverlap_) {
              throw ProtoWire.duplicate(t, "use_combined_no_overlap");
            }
            t.consume(":");
            setUseCombinedNoOverlap(t.bool());
            break;
          }
          case "at_most_one_max_expansion_size": {
            if (strict && hasAtMostOneMaxExpansionSize_) {
              throw ProtoWire.duplicate(t, "at_most_one_max_expansion_size");
            }
            t.consume(":");
            setAtMostOneMaxExpansionSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "catch_sigint_signal": {
            if (strict && hasCatchSigintSignal_) {
              throw ProtoWire.duplicate(t, "catch_sigint_signal");
            }
            t.consume(":");
            setCatchSigintSignal(t.bool());
            break;
          }
          case "use_implied_bounds": {
            if (strict && hasUseImpliedBounds_) {
              throw ProtoWire.duplicate(t, "use_implied_bounds");
            }
            t.consume(":");
            setUseImpliedBounds(t.bool());
            break;
          }
          case "polish_lp_solution": {
            if (strict && hasPolishLpSolution_) {
              throw ProtoWire.duplicate(t, "polish_lp_solution");
            }
            t.consume(":");
            setPolishLpSolution(t.bool());
            break;
          }
          case "lp_primal_tolerance": {
            if (strict && hasLpPrimalTolerance_) {
              throw ProtoWire.duplicate(t, "lp_primal_tolerance");
            }
            t.consume(":");
            setLpPrimalTolerance(t.floating());
            break;
          }
          case "lp_dual_tolerance": {
            if (strict && hasLpDualTolerance_) {
              throw ProtoWire.duplicate(t, "lp_dual_tolerance");
            }
            t.consume(":");
            setLpDualTolerance(t.floating());
            break;
          }
          case "convert_intervals": {
            if (strict && hasConvertIntervals_) {
              throw ProtoWire.duplicate(t, "convert_intervals");
            }
            t.consume(":");
            setConvertIntervals(t.bool());
            break;
          }
          case "symmetry_level": {
            if (strict && hasSymmetryLevel_) {
              throw ProtoWire.duplicate(t, "symmetry_level");
            }
            t.consume(":");
            setSymmetryLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "use_symmetry_in_lp": {
            if (strict && hasUseSymmetryInLp_) {
              throw ProtoWire.duplicate(t, "use_symmetry_in_lp");
            }
            t.consume(":");
            setUseSymmetryInLp(t.bool());
            break;
          }
          case "keep_symmetry_in_presolve": {
            if (strict && hasKeepSymmetryInPresolve_) {
              throw ProtoWire.duplicate(t, "keep_symmetry_in_presolve");
            }
            t.consume(":");
            setKeepSymmetryInPresolve(t.bool());
            break;
          }
          case "symmetry_detection_deterministic_time_limit": {
            if (strict && hasSymmetryDetectionDeterministicTimeLimit_) {
              throw ProtoWire.duplicate(t, "symmetry_detection_deterministic_time_limit");
            }
            t.consume(":");
            setSymmetryDetectionDeterministicTimeLimit(t.floating());
            break;
          }
          case "new_linear_propagation": {
            if (strict && hasNewLinearPropagation_) {
              throw ProtoWire.duplicate(t, "new_linear_propagation");
            }
            t.consume(":");
            setNewLinearPropagation(t.bool());
            break;
          }
          case "linear_split_size": {
            if (strict && hasLinearSplitSize_) {
              throw ProtoWire.duplicate(t, "linear_split_size");
            }
            t.consume(":");
            setLinearSplitSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "linearization_level": {
            if (strict && hasLinearizationLevel_) {
              throw ProtoWire.duplicate(t, "linearization_level");
            }
            t.consume(":");
            setLinearizationLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "boolean_encoding_level": {
            if (strict && hasBooleanEncodingLevel_) {
              throw ProtoWire.duplicate(t, "boolean_encoding_level");
            }
            t.consume(":");
            setBooleanEncodingLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_domain_size_when_encoding_eq_neq_constraints": {
            if (strict && hasMaxDomainSizeWhenEncodingEqNeqConstraints_) {
              throw ProtoWire.duplicate(t, "max_domain_size_when_encoding_eq_neq_constraints");
            }
            t.consume(":");
            setMaxDomainSizeWhenEncodingEqNeqConstraints((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_num_cuts": {
            if (strict && hasMaxNumCuts_) {
              throw ProtoWire.duplicate(t, "max_num_cuts");
            }
            t.consume(":");
            setMaxNumCuts((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "cut_level": {
            if (strict && hasCutLevel_) {
              throw ProtoWire.duplicate(t, "cut_level");
            }
            t.consume(":");
            setCutLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "only_add_cuts_at_level_zero": {
            if (strict && hasOnlyAddCutsAtLevelZero_) {
              throw ProtoWire.duplicate(t, "only_add_cuts_at_level_zero");
            }
            t.consume(":");
            setOnlyAddCutsAtLevelZero(t.bool());
            break;
          }
          case "add_objective_cut": {
            if (strict && hasAddObjectiveCut_) {
              throw ProtoWire.duplicate(t, "add_objective_cut");
            }
            t.consume(":");
            setAddObjectiveCut(t.bool());
            break;
          }
          case "add_cg_cuts": {
            if (strict && hasAddCgCuts_) {
              throw ProtoWire.duplicate(t, "add_cg_cuts");
            }
            t.consume(":");
            setAddCgCuts(t.bool());
            break;
          }
          case "add_mir_cuts": {
            if (strict && hasAddMirCuts_) {
              throw ProtoWire.duplicate(t, "add_mir_cuts");
            }
            t.consume(":");
            setAddMirCuts(t.bool());
            break;
          }
          case "add_zero_half_cuts": {
            if (strict && hasAddZeroHalfCuts_) {
              throw ProtoWire.duplicate(t, "add_zero_half_cuts");
            }
            t.consume(":");
            setAddZeroHalfCuts(t.bool());
            break;
          }
          case "add_clique_cuts": {
            if (strict && hasAddCliqueCuts_) {
              throw ProtoWire.duplicate(t, "add_clique_cuts");
            }
            t.consume(":");
            setAddCliqueCuts(t.bool());
            break;
          }
          case "add_rlt_cuts": {
            if (strict && hasAddRltCuts_) {
              throw ProtoWire.duplicate(t, "add_rlt_cuts");
            }
            t.consume(":");
            setAddRltCuts(t.bool());
            break;
          }
          case "max_all_diff_cut_size": {
            if (strict && hasMaxAllDiffCutSize_) {
              throw ProtoWire.duplicate(t, "max_all_diff_cut_size");
            }
            t.consume(":");
            setMaxAllDiffCutSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "add_lin_max_cuts": {
            if (strict && hasAddLinMaxCuts_) {
              throw ProtoWire.duplicate(t, "add_lin_max_cuts");
            }
            t.consume(":");
            setAddLinMaxCuts(t.bool());
            break;
          }
          case "max_integer_rounding_scaling": {
            if (strict && hasMaxIntegerRoundingScaling_) {
              throw ProtoWire.duplicate(t, "max_integer_rounding_scaling");
            }
            t.consume(":");
            setMaxIntegerRoundingScaling((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "add_lp_constraints_lazily": {
            if (strict && hasAddLpConstraintsLazily_) {
              throw ProtoWire.duplicate(t, "add_lp_constraints_lazily");
            }
            t.consume(":");
            setAddLpConstraintsLazily(t.bool());
            break;
          }
          case "root_lp_iterations": {
            if (strict && hasRootLpIterations_) {
              throw ProtoWire.duplicate(t, "root_lp_iterations");
            }
            t.consume(":");
            setRootLpIterations((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "min_orthogonality_for_lp_constraints": {
            if (strict && hasMinOrthogonalityForLpConstraints_) {
              throw ProtoWire.duplicate(t, "min_orthogonality_for_lp_constraints");
            }
            t.consume(":");
            setMinOrthogonalityForLpConstraints(t.floating());
            break;
          }
          case "max_cut_rounds_at_level_zero": {
            if (strict && hasMaxCutRoundsAtLevelZero_) {
              throw ProtoWire.duplicate(t, "max_cut_rounds_at_level_zero");
            }
            t.consume(":");
            setMaxCutRoundsAtLevelZero((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "max_consecutive_inactive_count": {
            if (strict && hasMaxConsecutiveInactiveCount_) {
              throw ProtoWire.duplicate(t, "max_consecutive_inactive_count");
            }
            t.consume(":");
            setMaxConsecutiveInactiveCount((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "cut_max_active_count_value": {
            if (strict && hasCutMaxActiveCountValue_) {
              throw ProtoWire.duplicate(t, "cut_max_active_count_value");
            }
            t.consume(":");
            setCutMaxActiveCountValue(t.floating());
            break;
          }
          case "cut_active_count_decay": {
            if (strict && hasCutActiveCountDecay_) {
              throw ProtoWire.duplicate(t, "cut_active_count_decay");
            }
            t.consume(":");
            setCutActiveCountDecay(t.floating());
            break;
          }
          case "cut_cleanup_target": {
            if (strict && hasCutCleanupTarget_) {
              throw ProtoWire.duplicate(t, "cut_cleanup_target");
            }
            t.consume(":");
            setCutCleanupTarget((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "new_constraints_batch_size": {
            if (strict && hasNewConstraintsBatchSize_) {
              throw ProtoWire.duplicate(t, "new_constraints_batch_size");
            }
            t.consume(":");
            setNewConstraintsBatchSize((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "exploit_integer_lp_solution": {
            if (strict && hasExploitIntegerLpSolution_) {
              throw ProtoWire.duplicate(t, "exploit_integer_lp_solution");
            }
            t.consume(":");
            setExploitIntegerLpSolution(t.bool());
            break;
          }
          case "exploit_all_lp_solution": {
            if (strict && hasExploitAllLpSolution_) {
              throw ProtoWire.duplicate(t, "exploit_all_lp_solution");
            }
            t.consume(":");
            setExploitAllLpSolution(t.bool());
            break;
          }
          case "exploit_best_solution": {
            if (strict && hasExploitBestSolution_) {
              throw ProtoWire.duplicate(t, "exploit_best_solution");
            }
            t.consume(":");
            setExploitBestSolution(t.bool());
            break;
          }
          case "exploit_relaxation_solution": {
            if (strict && hasExploitRelaxationSolution_) {
              throw ProtoWire.duplicate(t, "exploit_relaxation_solution");
            }
            t.consume(":");
            setExploitRelaxationSolution(t.bool());
            break;
          }
          case "exploit_objective": {
            if (strict && hasExploitObjective_) {
              throw ProtoWire.duplicate(t, "exploit_objective");
            }
            t.consume(":");
            setExploitObjective(t.bool());
            break;
          }
          case "detect_linearized_product": {
            if (strict && hasDetectLinearizedProduct_) {
              throw ProtoWire.duplicate(t, "detect_linearized_product");
            }
            t.consume(":");
            setDetectLinearizedProduct(t.bool());
            break;
          }
          case "use_new_integer_conflict_resolution": {
            if (strict && hasUseNewIntegerConflictResolution_) {
              throw ProtoWire.duplicate(t, "use_new_integer_conflict_resolution");
            }
            t.consume(":");
            setUseNewIntegerConflictResolution(t.bool());
            break;
          }
          case "create_1uip_boolean_during_icr": {
            if (strict && hasCreate1UipBooleanDuringIcr_) {
              throw ProtoWire.duplicate(t, "create_1uip_boolean_during_icr");
            }
            t.consume(":");
            setCreate1UipBooleanDuringIcr(t.bool());
            break;
          }
          case "mip_max_bound": {
            if (strict && hasMipMaxBound_) {
              throw ProtoWire.duplicate(t, "mip_max_bound");
            }
            t.consume(":");
            setMipMaxBound(t.floating());
            break;
          }
          case "mip_var_scaling": {
            if (strict && hasMipVarScaling_) {
              throw ProtoWire.duplicate(t, "mip_var_scaling");
            }
            t.consume(":");
            setMipVarScaling(t.floating());
            break;
          }
          case "mip_scale_large_domain": {
            if (strict && hasMipScaleLargeDomain_) {
              throw ProtoWire.duplicate(t, "mip_scale_large_domain");
            }
            t.consume(":");
            setMipScaleLargeDomain(t.bool());
            break;
          }
          case "mip_automatically_scale_variables": {
            if (strict && hasMipAutomaticallyScaleVariables_) {
              throw ProtoWire.duplicate(t, "mip_automatically_scale_variables");
            }
            t.consume(":");
            setMipAutomaticallyScaleVariables(t.bool());
            break;
          }
          case "mip_scale_to_max_bound": {
            if (strict && hasMipScaleToMaxBound_) {
              throw ProtoWire.duplicate(t, "mip_scale_to_max_bound");
            }
            t.consume(":");
            setMipScaleToMaxBound(t.bool());
            break;
          }
          case "mip_presolve_restrict_bounds": {
            if (strict && hasMipPresolveRestrictBounds_) {
              throw ProtoWire.duplicate(t, "mip_presolve_restrict_bounds");
            }
            t.consume(":");
            setMipPresolveRestrictBounds(t.bool());
            break;
          }
          case "only_solve_ip": {
            if (strict && hasOnlySolveIp_) {
              throw ProtoWire.duplicate(t, "only_solve_ip");
            }
            t.consume(":");
            setOnlySolveIp(t.bool());
            break;
          }
          case "mip_wanted_precision": {
            if (strict && hasMipWantedPrecision_) {
              throw ProtoWire.duplicate(t, "mip_wanted_precision");
            }
            t.consume(":");
            setMipWantedPrecision(t.floating());
            break;
          }
          case "mip_max_activity_exponent": {
            if (strict && hasMipMaxActivityExponent_) {
              throw ProtoWire.duplicate(t, "mip_max_activity_exponent");
            }
            t.consume(":");
            setMipMaxActivityExponent((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "mip_check_precision": {
            if (strict && hasMipCheckPrecision_) {
              throw ProtoWire.duplicate(t, "mip_check_precision");
            }
            t.consume(":");
            setMipCheckPrecision(t.floating());
            break;
          }
          case "mip_compute_true_objective_bound": {
            if (strict && hasMipComputeTrueObjectiveBound_) {
              throw ProtoWire.duplicate(t, "mip_compute_true_objective_bound");
            }
            t.consume(":");
            setMipComputeTrueObjectiveBound(t.bool());
            break;
          }
          case "mip_max_valid_magnitude": {
            if (strict && hasMipMaxValidMagnitude_) {
              throw ProtoWire.duplicate(t, "mip_max_valid_magnitude");
            }
            t.consume(":");
            setMipMaxValidMagnitude(t.floating());
            break;
          }
          case "mip_treat_high_magnitude_bounds_as_infinity": {
            if (strict && hasMipTreatHighMagnitudeBoundsAsInfinity_) {
              throw ProtoWire.duplicate(t, "mip_treat_high_magnitude_bounds_as_infinity");
            }
            t.consume(":");
            setMipTreatHighMagnitudeBoundsAsInfinity(t.bool());
            break;
          }
          case "mip_drop_tolerance": {
            if (strict && hasMipDropTolerance_) {
              throw ProtoWire.duplicate(t, "mip_drop_tolerance");
            }
            t.consume(":");
            setMipDropTolerance(t.floating());
            break;
          }
          case "mip_presolve_level": {
            if (strict && hasMipPresolveLevel_) {
              throw ProtoWire.duplicate(t, "mip_presolve_level");
            }
            t.consume(":");
            setMipPresolveLevel((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "mip_use_tight_bounds": {
            if (strict && hasMipUseTightBounds_) {
              throw ProtoWire.duplicate(t, "mip_use_tight_bounds");
            }
            t.consume(":");
            setMipUseTightBounds(t.bool());
            break;
          }
          case "mip_tight_precision": {
            if (strict && hasMipTightPrecision_) {
              throw ProtoWire.duplicate(t, "mip_tight_precision");
            }
            t.consume(":");
            setMipTightPrecision(t.floating());
            break;
          }
          case "mip_verify_solution": {
            if (strict && hasMipVerifySolution_) {
              throw ProtoWire.duplicate(t, "mip_verify_solution");
            }
            t.consume(":");
            setMipVerifySolution(t.bool());
            break;
          }
          case "mip_proper_relaxation": {
            if (strict && hasMipProperRelaxation_) {
              throw ProtoWire.duplicate(t, "mip_proper_relaxation");
            }
            t.consume(":");
            setMipProperRelaxation(t.bool());
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.SatParameters", name);
        }
        t.separator();
      }
    }

    /**
     * Merges the message serialized in {@code data} (protobuf binary format) into this builder;
     * unknown fields are skipped.
     *
     * @throws InvalidProtocolBufferException if the data is not a valid serialized message
     */
    public Builder mergeFrom(byte[] data) throws InvalidProtocolBufferException {
      try {
        mergeWire(new ProtoWire.Reader(data));
      } catch (ProtoWire.ParseError e) {
        throw new InvalidProtocolBufferException(e.getMessage());
      }
      return this;
    }

    /** Sets {@code name}. */
    public Builder setName(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      name_ = value;
      hasName_ = true;
      return this;
    }

    /** Resets {@code name} to its default value. */
    public Builder clearName() {
      name_ = "";
      hasName_ = false;
      return this;
    }

    /** Sets {@code preferred_variable_order}. */
    public Builder setPreferredVariableOrder(SatParameters.VariableOrder value) {
      if (value == null) {
        throw new NullPointerException();
      }
      preferredVariableOrder_ = value.getNumber();
      hasPreferredVariableOrder_ = true;
      return this;
    }

    /** Sets the number of {@code preferred_variable_order}. */
    public Builder setPreferredVariableOrderValue(int value) {
      preferredVariableOrder_ = value;
      hasPreferredVariableOrder_ = true;
      return this;
    }

    /** Resets {@code preferred_variable_order} to its default value. */
    public Builder clearPreferredVariableOrder() {
      preferredVariableOrder_ = 0;
      hasPreferredVariableOrder_ = false;
      return this;
    }

    /** Sets {@code initial_polarity}. */
    public Builder setInitialPolarity(SatParameters.Polarity value) {
      if (value == null) {
        throw new NullPointerException();
      }
      initialPolarity_ = value.getNumber();
      hasInitialPolarity_ = true;
      return this;
    }

    /** Sets the number of {@code initial_polarity}. */
    public Builder setInitialPolarityValue(int value) {
      initialPolarity_ = value;
      hasInitialPolarity_ = true;
      return this;
    }

    /** Resets {@code initial_polarity} to its default value. */
    public Builder clearInitialPolarity() {
      initialPolarity_ = 1;
      hasInitialPolarity_ = false;
      return this;
    }

    /** Sets {@code use_phase_saving}. */
    public Builder setUsePhaseSaving(boolean value) {
      usePhaseSaving_ = value;
      hasUsePhaseSaving_ = true;
      return this;
    }

    /** Resets {@code use_phase_saving} to its default value. */
    public Builder clearUsePhaseSaving() {
      usePhaseSaving_ = true;
      hasUsePhaseSaving_ = false;
      return this;
    }

    /** Sets {@code polarity_rephase_increment}. */
    public Builder setPolarityRephaseIncrement(int value) {
      polarityRephaseIncrement_ = value;
      hasPolarityRephaseIncrement_ = true;
      return this;
    }

    /** Resets {@code polarity_rephase_increment} to its default value. */
    public Builder clearPolarityRephaseIncrement() {
      polarityRephaseIncrement_ = 1000;
      hasPolarityRephaseIncrement_ = false;
      return this;
    }

    /** Sets {@code polarity_exploit_ls_hints}. */
    public Builder setPolarityExploitLsHints(boolean value) {
      polarityExploitLsHints_ = value;
      hasPolarityExploitLsHints_ = true;
      return this;
    }

    /** Resets {@code polarity_exploit_ls_hints} to its default value. */
    public Builder clearPolarityExploitLsHints() {
      polarityExploitLsHints_ = false;
      hasPolarityExploitLsHints_ = false;
      return this;
    }

    /** Sets {@code random_polarity_ratio}. */
    public Builder setRandomPolarityRatio(double value) {
      randomPolarityRatio_ = value;
      hasRandomPolarityRatio_ = true;
      return this;
    }

    /** Resets {@code random_polarity_ratio} to its default value. */
    public Builder clearRandomPolarityRatio() {
      randomPolarityRatio_ = 0.0;
      hasRandomPolarityRatio_ = false;
      return this;
    }

    /** Sets {@code random_branches_ratio}. */
    public Builder setRandomBranchesRatio(double value) {
      randomBranchesRatio_ = value;
      hasRandomBranchesRatio_ = true;
      return this;
    }

    /** Resets {@code random_branches_ratio} to its default value. */
    public Builder clearRandomBranchesRatio() {
      randomBranchesRatio_ = 0.0;
      hasRandomBranchesRatio_ = false;
      return this;
    }

    /** Sets {@code use_erwa_heuristic}. */
    public Builder setUseErwaHeuristic(boolean value) {
      useErwaHeuristic_ = value;
      hasUseErwaHeuristic_ = true;
      return this;
    }

    /** Resets {@code use_erwa_heuristic} to its default value. */
    public Builder clearUseErwaHeuristic() {
      useErwaHeuristic_ = false;
      hasUseErwaHeuristic_ = false;
      return this;
    }

    /** Sets {@code initial_variables_activity}. */
    public Builder setInitialVariablesActivity(double value) {
      initialVariablesActivity_ = value;
      hasInitialVariablesActivity_ = true;
      return this;
    }

    /** Resets {@code initial_variables_activity} to its default value. */
    public Builder clearInitialVariablesActivity() {
      initialVariablesActivity_ = 0.0;
      hasInitialVariablesActivity_ = false;
      return this;
    }

    /** Sets {@code also_bump_variables_in_conflict_reasons}. */
    public Builder setAlsoBumpVariablesInConflictReasons(boolean value) {
      alsoBumpVariablesInConflictReasons_ = value;
      hasAlsoBumpVariablesInConflictReasons_ = true;
      return this;
    }

    /** Resets {@code also_bump_variables_in_conflict_reasons} to its default value. */
    public Builder clearAlsoBumpVariablesInConflictReasons() {
      alsoBumpVariablesInConflictReasons_ = false;
      hasAlsoBumpVariablesInConflictReasons_ = false;
      return this;
    }

    /** Sets {@code minimization_algorithm}. */
    public Builder setMinimizationAlgorithm(SatParameters.ConflictMinimizationAlgorithm value) {
      if (value == null) {
        throw new NullPointerException();
      }
      minimizationAlgorithm_ = value.getNumber();
      hasMinimizationAlgorithm_ = true;
      return this;
    }

    /** Sets the number of {@code minimization_algorithm}. */
    public Builder setMinimizationAlgorithmValue(int value) {
      minimizationAlgorithm_ = value;
      hasMinimizationAlgorithm_ = true;
      return this;
    }

    /** Resets {@code minimization_algorithm} to its default value. */
    public Builder clearMinimizationAlgorithm() {
      minimizationAlgorithm_ = 2;
      hasMinimizationAlgorithm_ = false;
      return this;
    }

    /** Sets {@code binary_minimization_algorithm}. */
    public Builder setBinaryMinimizationAlgorithm(SatParameters.BinaryMinizationAlgorithm value) {
      if (value == null) {
        throw new NullPointerException();
      }
      binaryMinimizationAlgorithm_ = value.getNumber();
      hasBinaryMinimizationAlgorithm_ = true;
      return this;
    }

    /** Sets the number of {@code binary_minimization_algorithm}. */
    public Builder setBinaryMinimizationAlgorithmValue(int value) {
      binaryMinimizationAlgorithm_ = value;
      hasBinaryMinimizationAlgorithm_ = true;
      return this;
    }

    /** Resets {@code binary_minimization_algorithm} to its default value. */
    public Builder clearBinaryMinimizationAlgorithm() {
      binaryMinimizationAlgorithm_ = 5;
      hasBinaryMinimizationAlgorithm_ = false;
      return this;
    }

    /** Sets {@code subsumption_during_conflict_analysis}. */
    public Builder setSubsumptionDuringConflictAnalysis(boolean value) {
      subsumptionDuringConflictAnalysis_ = value;
      hasSubsumptionDuringConflictAnalysis_ = true;
      return this;
    }

    /** Resets {@code subsumption_during_conflict_analysis} to its default value. */
    public Builder clearSubsumptionDuringConflictAnalysis() {
      subsumptionDuringConflictAnalysis_ = true;
      hasSubsumptionDuringConflictAnalysis_ = false;
      return this;
    }

    /** Sets {@code extra_subsumption_during_conflict_analysis}. */
    public Builder setExtraSubsumptionDuringConflictAnalysis(boolean value) {
      extraSubsumptionDuringConflictAnalysis_ = value;
      hasExtraSubsumptionDuringConflictAnalysis_ = true;
      return this;
    }

    /** Resets {@code extra_subsumption_during_conflict_analysis} to its default value. */
    public Builder clearExtraSubsumptionDuringConflictAnalysis() {
      extraSubsumptionDuringConflictAnalysis_ = true;
      hasExtraSubsumptionDuringConflictAnalysis_ = false;
      return this;
    }

    /** Sets {@code decision_subsumption_during_conflict_analysis}. */
    public Builder setDecisionSubsumptionDuringConflictAnalysis(boolean value) {
      decisionSubsumptionDuringConflictAnalysis_ = value;
      hasDecisionSubsumptionDuringConflictAnalysis_ = true;
      return this;
    }

    /** Resets {@code decision_subsumption_during_conflict_analysis} to its default value. */
    public Builder clearDecisionSubsumptionDuringConflictAnalysis() {
      decisionSubsumptionDuringConflictAnalysis_ = true;
      hasDecisionSubsumptionDuringConflictAnalysis_ = false;
      return this;
    }

    /** Sets {@code eagerly_subsume_last_n_conflicts}. */
    public Builder setEagerlySubsumeLastNConflicts(int value) {
      eagerlySubsumeLastNConflicts_ = value;
      hasEagerlySubsumeLastNConflicts_ = true;
      return this;
    }

    /** Resets {@code eagerly_subsume_last_n_conflicts} to its default value. */
    public Builder clearEagerlySubsumeLastNConflicts() {
      eagerlySubsumeLastNConflicts_ = 4;
      hasEagerlySubsumeLastNConflicts_ = false;
      return this;
    }

    /** Sets {@code subsume_during_vivification}. */
    public Builder setSubsumeDuringVivification(boolean value) {
      subsumeDuringVivification_ = value;
      hasSubsumeDuringVivification_ = true;
      return this;
    }

    /** Resets {@code subsume_during_vivification} to its default value. */
    public Builder clearSubsumeDuringVivification() {
      subsumeDuringVivification_ = true;
      hasSubsumeDuringVivification_ = false;
      return this;
    }

    /** Sets {@code use_chronological_backtracking}. */
    public Builder setUseChronologicalBacktracking(boolean value) {
      useChronologicalBacktracking_ = value;
      hasUseChronologicalBacktracking_ = true;
      return this;
    }

    /** Resets {@code use_chronological_backtracking} to its default value. */
    public Builder clearUseChronologicalBacktracking() {
      useChronologicalBacktracking_ = false;
      hasUseChronologicalBacktracking_ = false;
      return this;
    }

    /** Sets {@code max_backjump_levels}. */
    public Builder setMaxBackjumpLevels(int value) {
      maxBackjumpLevels_ = value;
      hasMaxBackjumpLevels_ = true;
      return this;
    }

    /** Resets {@code max_backjump_levels} to its default value. */
    public Builder clearMaxBackjumpLevels() {
      maxBackjumpLevels_ = 50;
      hasMaxBackjumpLevels_ = false;
      return this;
    }

    /** Sets {@code chronological_backtrack_min_conflicts}. */
    public Builder setChronologicalBacktrackMinConflicts(int value) {
      chronologicalBacktrackMinConflicts_ = value;
      hasChronologicalBacktrackMinConflicts_ = true;
      return this;
    }

    /** Resets {@code chronological_backtrack_min_conflicts} to its default value. */
    public Builder clearChronologicalBacktrackMinConflicts() {
      chronologicalBacktrackMinConflicts_ = 1000;
      hasChronologicalBacktrackMinConflicts_ = false;
      return this;
    }

    /** Sets {@code learn_on_assumptions_unsat}. */
    public Builder setLearnOnAssumptionsUnsat(boolean value) {
      learnOnAssumptionsUnsat_ = value;
      hasLearnOnAssumptionsUnsat_ = true;
      return this;
    }

    /** Resets {@code learn_on_assumptions_unsat} to its default value. */
    public Builder clearLearnOnAssumptionsUnsat() {
      learnOnAssumptionsUnsat_ = true;
      hasLearnOnAssumptionsUnsat_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_period}. */
    public Builder setClauseCleanupPeriod(int value) {
      clauseCleanupPeriod_ = value;
      hasClauseCleanupPeriod_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_period} to its default value. */
    public Builder clearClauseCleanupPeriod() {
      clauseCleanupPeriod_ = 10000;
      hasClauseCleanupPeriod_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_period_increment}. */
    public Builder setClauseCleanupPeriodIncrement(int value) {
      clauseCleanupPeriodIncrement_ = value;
      hasClauseCleanupPeriodIncrement_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_period_increment} to its default value. */
    public Builder clearClauseCleanupPeriodIncrement() {
      clauseCleanupPeriodIncrement_ = 0;
      hasClauseCleanupPeriodIncrement_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_target}. */
    public Builder setClauseCleanupTarget(int value) {
      clauseCleanupTarget_ = value;
      hasClauseCleanupTarget_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_target} to its default value. */
    public Builder clearClauseCleanupTarget() {
      clauseCleanupTarget_ = 0;
      hasClauseCleanupTarget_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_ratio}. */
    public Builder setClauseCleanupRatio(double value) {
      clauseCleanupRatio_ = value;
      hasClauseCleanupRatio_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_ratio} to its default value. */
    public Builder clearClauseCleanupRatio() {
      clauseCleanupRatio_ = 0.5;
      hasClauseCleanupRatio_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_lbd_bound}. */
    public Builder setClauseCleanupLbdBound(int value) {
      clauseCleanupLbdBound_ = value;
      hasClauseCleanupLbdBound_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_lbd_bound} to its default value. */
    public Builder clearClauseCleanupLbdBound() {
      clauseCleanupLbdBound_ = 5;
      hasClauseCleanupLbdBound_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_lbd_tier1}. */
    public Builder setClauseCleanupLbdTier1(int value) {
      clauseCleanupLbdTier1_ = value;
      hasClauseCleanupLbdTier1_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_lbd_tier1} to its default value. */
    public Builder clearClauseCleanupLbdTier1() {
      clauseCleanupLbdTier1_ = 0;
      hasClauseCleanupLbdTier1_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_lbd_tier2}. */
    public Builder setClauseCleanupLbdTier2(int value) {
      clauseCleanupLbdTier2_ = value;
      hasClauseCleanupLbdTier2_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_lbd_tier2} to its default value. */
    public Builder clearClauseCleanupLbdTier2() {
      clauseCleanupLbdTier2_ = 0;
      hasClauseCleanupLbdTier2_ = false;
      return this;
    }

    /** Sets {@code clause_cleanup_ordering}. */
    public Builder setClauseCleanupOrdering(SatParameters.ClauseOrdering value) {
      if (value == null) {
        throw new NullPointerException();
      }
      clauseCleanupOrdering_ = value.getNumber();
      hasClauseCleanupOrdering_ = true;
      return this;
    }

    /** Sets the number of {@code clause_cleanup_ordering}. */
    public Builder setClauseCleanupOrderingValue(int value) {
      clauseCleanupOrdering_ = value;
      hasClauseCleanupOrdering_ = true;
      return this;
    }

    /** Resets {@code clause_cleanup_ordering} to its default value. */
    public Builder clearClauseCleanupOrdering() {
      clauseCleanupOrdering_ = 0;
      hasClauseCleanupOrdering_ = false;
      return this;
    }

    /** Sets {@code pb_cleanup_increment}. */
    public Builder setPbCleanupIncrement(int value) {
      pbCleanupIncrement_ = value;
      hasPbCleanupIncrement_ = true;
      return this;
    }

    /** Resets {@code pb_cleanup_increment} to its default value. */
    public Builder clearPbCleanupIncrement() {
      pbCleanupIncrement_ = 200;
      hasPbCleanupIncrement_ = false;
      return this;
    }

    /** Sets {@code pb_cleanup_ratio}. */
    public Builder setPbCleanupRatio(double value) {
      pbCleanupRatio_ = value;
      hasPbCleanupRatio_ = true;
      return this;
    }

    /** Resets {@code pb_cleanup_ratio} to its default value. */
    public Builder clearPbCleanupRatio() {
      pbCleanupRatio_ = 0.5;
      hasPbCleanupRatio_ = false;
      return this;
    }

    /** Sets {@code variable_activity_decay}. */
    public Builder setVariableActivityDecay(double value) {
      variableActivityDecay_ = value;
      hasVariableActivityDecay_ = true;
      return this;
    }

    /** Resets {@code variable_activity_decay} to its default value. */
    public Builder clearVariableActivityDecay() {
      variableActivityDecay_ = 0.8;
      hasVariableActivityDecay_ = false;
      return this;
    }

    /** Sets {@code max_variable_activity_value}. */
    public Builder setMaxVariableActivityValue(double value) {
      maxVariableActivityValue_ = value;
      hasMaxVariableActivityValue_ = true;
      return this;
    }

    /** Resets {@code max_variable_activity_value} to its default value. */
    public Builder clearMaxVariableActivityValue() {
      maxVariableActivityValue_ = 1e100;
      hasMaxVariableActivityValue_ = false;
      return this;
    }

    /** Sets {@code glucose_max_decay}. */
    public Builder setGlucoseMaxDecay(double value) {
      glucoseMaxDecay_ = value;
      hasGlucoseMaxDecay_ = true;
      return this;
    }

    /** Resets {@code glucose_max_decay} to its default value. */
    public Builder clearGlucoseMaxDecay() {
      glucoseMaxDecay_ = 0.95;
      hasGlucoseMaxDecay_ = false;
      return this;
    }

    /** Sets {@code glucose_decay_increment}. */
    public Builder setGlucoseDecayIncrement(double value) {
      glucoseDecayIncrement_ = value;
      hasGlucoseDecayIncrement_ = true;
      return this;
    }

    /** Resets {@code glucose_decay_increment} to its default value. */
    public Builder clearGlucoseDecayIncrement() {
      glucoseDecayIncrement_ = 0.01;
      hasGlucoseDecayIncrement_ = false;
      return this;
    }

    /** Sets {@code glucose_decay_increment_period}. */
    public Builder setGlucoseDecayIncrementPeriod(int value) {
      glucoseDecayIncrementPeriod_ = value;
      hasGlucoseDecayIncrementPeriod_ = true;
      return this;
    }

    /** Resets {@code glucose_decay_increment_period} to its default value. */
    public Builder clearGlucoseDecayIncrementPeriod() {
      glucoseDecayIncrementPeriod_ = 5000;
      hasGlucoseDecayIncrementPeriod_ = false;
      return this;
    }

    /** Sets {@code clause_activity_decay}. */
    public Builder setClauseActivityDecay(double value) {
      clauseActivityDecay_ = value;
      hasClauseActivityDecay_ = true;
      return this;
    }

    /** Resets {@code clause_activity_decay} to its default value. */
    public Builder clearClauseActivityDecay() {
      clauseActivityDecay_ = 0.999;
      hasClauseActivityDecay_ = false;
      return this;
    }

    /** Sets {@code max_clause_activity_value}. */
    public Builder setMaxClauseActivityValue(double value) {
      maxClauseActivityValue_ = value;
      hasMaxClauseActivityValue_ = true;
      return this;
    }

    /** Resets {@code max_clause_activity_value} to its default value. */
    public Builder clearMaxClauseActivityValue() {
      maxClauseActivityValue_ = 1e20;
      hasMaxClauseActivityValue_ = false;
      return this;
    }

    /** Replaces the element {@code index} of {@code restart_algorithms}. */
    public Builder setRestartAlgorithms(int index, SatParameters.RestartAlgorithm value) {
      if (restartAlgorithms_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      restartAlgorithms_.set(index, java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends a value to {@code restart_algorithms}. */
    public Builder addRestartAlgorithms(SatParameters.RestartAlgorithm value) {
      if (restartAlgorithms_ == null) {
        restartAlgorithms_ = new java.util.ArrayList<>();
      }
      restartAlgorithms_.add(java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends all values to {@code restart_algorithms}. */
    public Builder addAllRestartAlgorithms(Iterable<? extends SatParameters.RestartAlgorithm> values) {
      if (restartAlgorithms_ == null) {
        restartAlgorithms_ = new java.util.ArrayList<>();
      }
      for (SatParameters.RestartAlgorithm v : values) {
        restartAlgorithms_.add(java.util.Objects.requireNonNull(v));
      }
      return this;
    }

    /** Removes all values of {@code restart_algorithms}. */
    public Builder clearRestartAlgorithms() {
      restartAlgorithms_ = null;
      return this;
    }

    /** Sets {@code default_restart_algorithms}. */
    public Builder setDefaultRestartAlgorithms(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      defaultRestartAlgorithms_ = value;
      hasDefaultRestartAlgorithms_ = true;
      return this;
    }

    /** Resets {@code default_restart_algorithms} to its default value. */
    public Builder clearDefaultRestartAlgorithms() {
      defaultRestartAlgorithms_ = "LUBY_RESTART,LBD_MOVING_AVERAGE_RESTART,DL_MOVING_AVERAGE_RESTART";
      hasDefaultRestartAlgorithms_ = false;
      return this;
    }

    /** Sets {@code restart_period}. */
    public Builder setRestartPeriod(int value) {
      restartPeriod_ = value;
      hasRestartPeriod_ = true;
      return this;
    }

    /** Resets {@code restart_period} to its default value. */
    public Builder clearRestartPeriod() {
      restartPeriod_ = 50;
      hasRestartPeriod_ = false;
      return this;
    }

    /** Sets {@code restart_running_window_size}. */
    public Builder setRestartRunningWindowSize(int value) {
      restartRunningWindowSize_ = value;
      hasRestartRunningWindowSize_ = true;
      return this;
    }

    /** Resets {@code restart_running_window_size} to its default value. */
    public Builder clearRestartRunningWindowSize() {
      restartRunningWindowSize_ = 50;
      hasRestartRunningWindowSize_ = false;
      return this;
    }

    /** Sets {@code restart_dl_average_ratio}. */
    public Builder setRestartDlAverageRatio(double value) {
      restartDlAverageRatio_ = value;
      hasRestartDlAverageRatio_ = true;
      return this;
    }

    /** Resets {@code restart_dl_average_ratio} to its default value. */
    public Builder clearRestartDlAverageRatio() {
      restartDlAverageRatio_ = 1.0;
      hasRestartDlAverageRatio_ = false;
      return this;
    }

    /** Sets {@code restart_lbd_average_ratio}. */
    public Builder setRestartLbdAverageRatio(double value) {
      restartLbdAverageRatio_ = value;
      hasRestartLbdAverageRatio_ = true;
      return this;
    }

    /** Resets {@code restart_lbd_average_ratio} to its default value. */
    public Builder clearRestartLbdAverageRatio() {
      restartLbdAverageRatio_ = 1.0;
      hasRestartLbdAverageRatio_ = false;
      return this;
    }

    /** Sets {@code use_blocking_restart}. */
    public Builder setUseBlockingRestart(boolean value) {
      useBlockingRestart_ = value;
      hasUseBlockingRestart_ = true;
      return this;
    }

    /** Resets {@code use_blocking_restart} to its default value. */
    public Builder clearUseBlockingRestart() {
      useBlockingRestart_ = false;
      hasUseBlockingRestart_ = false;
      return this;
    }

    /** Sets {@code blocking_restart_window_size}. */
    public Builder setBlockingRestartWindowSize(int value) {
      blockingRestartWindowSize_ = value;
      hasBlockingRestartWindowSize_ = true;
      return this;
    }

    /** Resets {@code blocking_restart_window_size} to its default value. */
    public Builder clearBlockingRestartWindowSize() {
      blockingRestartWindowSize_ = 5000;
      hasBlockingRestartWindowSize_ = false;
      return this;
    }

    /** Sets {@code blocking_restart_multiplier}. */
    public Builder setBlockingRestartMultiplier(double value) {
      blockingRestartMultiplier_ = value;
      hasBlockingRestartMultiplier_ = true;
      return this;
    }

    /** Resets {@code blocking_restart_multiplier} to its default value. */
    public Builder clearBlockingRestartMultiplier() {
      blockingRestartMultiplier_ = 1.4;
      hasBlockingRestartMultiplier_ = false;
      return this;
    }

    /** Sets {@code num_conflicts_before_strategy_changes}. */
    public Builder setNumConflictsBeforeStrategyChanges(int value) {
      numConflictsBeforeStrategyChanges_ = value;
      hasNumConflictsBeforeStrategyChanges_ = true;
      return this;
    }

    /** Resets {@code num_conflicts_before_strategy_changes} to its default value. */
    public Builder clearNumConflictsBeforeStrategyChanges() {
      numConflictsBeforeStrategyChanges_ = 0;
      hasNumConflictsBeforeStrategyChanges_ = false;
      return this;
    }

    /** Sets {@code strategy_change_increase_ratio}. */
    public Builder setStrategyChangeIncreaseRatio(double value) {
      strategyChangeIncreaseRatio_ = value;
      hasStrategyChangeIncreaseRatio_ = true;
      return this;
    }

    /** Resets {@code strategy_change_increase_ratio} to its default value. */
    public Builder clearStrategyChangeIncreaseRatio() {
      strategyChangeIncreaseRatio_ = 0.0;
      hasStrategyChangeIncreaseRatio_ = false;
      return this;
    }

    /** Sets {@code max_time_in_seconds}. */
    public Builder setMaxTimeInSeconds(double value) {
      maxTimeInSeconds_ = value;
      hasMaxTimeInSeconds_ = true;
      return this;
    }

    /** Resets {@code max_time_in_seconds} to its default value. */
    public Builder clearMaxTimeInSeconds() {
      maxTimeInSeconds_ = Double.POSITIVE_INFINITY;
      hasMaxTimeInSeconds_ = false;
      return this;
    }

    /** Sets {@code max_deterministic_time}. */
    public Builder setMaxDeterministicTime(double value) {
      maxDeterministicTime_ = value;
      hasMaxDeterministicTime_ = true;
      return this;
    }

    /** Resets {@code max_deterministic_time} to its default value. */
    public Builder clearMaxDeterministicTime() {
      maxDeterministicTime_ = Double.POSITIVE_INFINITY;
      hasMaxDeterministicTime_ = false;
      return this;
    }

    /** Sets {@code max_num_deterministic_batches}. */
    public Builder setMaxNumDeterministicBatches(int value) {
      maxNumDeterministicBatches_ = value;
      hasMaxNumDeterministicBatches_ = true;
      return this;
    }

    /** Resets {@code max_num_deterministic_batches} to its default value. */
    public Builder clearMaxNumDeterministicBatches() {
      maxNumDeterministicBatches_ = 0;
      hasMaxNumDeterministicBatches_ = false;
      return this;
    }

    /** Sets {@code max_number_of_conflicts}. */
    public Builder setMaxNumberOfConflicts(long value) {
      maxNumberOfConflicts_ = value;
      hasMaxNumberOfConflicts_ = true;
      return this;
    }

    /** Resets {@code max_number_of_conflicts} to its default value. */
    public Builder clearMaxNumberOfConflicts() {
      maxNumberOfConflicts_ = 0x7FFFFFFFFFFFFFFFL;
      hasMaxNumberOfConflicts_ = false;
      return this;
    }

    /** Sets {@code max_memory_in_mb}. */
    public Builder setMaxMemoryInMb(long value) {
      maxMemoryInMb_ = value;
      hasMaxMemoryInMb_ = true;
      return this;
    }

    /** Resets {@code max_memory_in_mb} to its default value. */
    public Builder clearMaxMemoryInMb() {
      maxMemoryInMb_ = 10000L;
      hasMaxMemoryInMb_ = false;
      return this;
    }

    /** Sets {@code absolute_gap_limit}. */
    public Builder setAbsoluteGapLimit(double value) {
      absoluteGapLimit_ = value;
      hasAbsoluteGapLimit_ = true;
      return this;
    }

    /** Resets {@code absolute_gap_limit} to its default value. */
    public Builder clearAbsoluteGapLimit() {
      absoluteGapLimit_ = 1e-4;
      hasAbsoluteGapLimit_ = false;
      return this;
    }

    /** Sets {@code relative_gap_limit}. */
    public Builder setRelativeGapLimit(double value) {
      relativeGapLimit_ = value;
      hasRelativeGapLimit_ = true;
      return this;
    }

    /** Resets {@code relative_gap_limit} to its default value. */
    public Builder clearRelativeGapLimit() {
      relativeGapLimit_ = 0.0;
      hasRelativeGapLimit_ = false;
      return this;
    }

    /** Sets {@code random_seed}. */
    public Builder setRandomSeed(int value) {
      randomSeed_ = value;
      hasRandomSeed_ = true;
      return this;
    }

    /** Resets {@code random_seed} to its default value. */
    public Builder clearRandomSeed() {
      randomSeed_ = 1;
      hasRandomSeed_ = false;
      return this;
    }

    /** Sets {@code permute_variable_randomly}. */
    public Builder setPermuteVariableRandomly(boolean value) {
      permuteVariableRandomly_ = value;
      hasPermuteVariableRandomly_ = true;
      return this;
    }

    /** Resets {@code permute_variable_randomly} to its default value. */
    public Builder clearPermuteVariableRandomly() {
      permuteVariableRandomly_ = false;
      hasPermuteVariableRandomly_ = false;
      return this;
    }

    /** Sets {@code permute_presolve_constraint_order}. */
    public Builder setPermutePresolveConstraintOrder(boolean value) {
      permutePresolveConstraintOrder_ = value;
      hasPermutePresolveConstraintOrder_ = true;
      return this;
    }

    /** Resets {@code permute_presolve_constraint_order} to its default value. */
    public Builder clearPermutePresolveConstraintOrder() {
      permutePresolveConstraintOrder_ = false;
      hasPermutePresolveConstraintOrder_ = false;
      return this;
    }

    /** Sets {@code use_absl_random}. */
    public Builder setUseAbslRandom(boolean value) {
      useAbslRandom_ = value;
      hasUseAbslRandom_ = true;
      return this;
    }

    /** Resets {@code use_absl_random} to its default value. */
    public Builder clearUseAbslRandom() {
      useAbslRandom_ = false;
      hasUseAbslRandom_ = false;
      return this;
    }

    /** Sets {@code log_search_progress}. */
    public Builder setLogSearchProgress(boolean value) {
      logSearchProgress_ = value;
      hasLogSearchProgress_ = true;
      return this;
    }

    /** Resets {@code log_search_progress} to its default value. */
    public Builder clearLogSearchProgress() {
      logSearchProgress_ = false;
      hasLogSearchProgress_ = false;
      return this;
    }

    /** Sets {@code log_subsolver_statistics}. */
    public Builder setLogSubsolverStatistics(boolean value) {
      logSubsolverStatistics_ = value;
      hasLogSubsolverStatistics_ = true;
      return this;
    }

    /** Resets {@code log_subsolver_statistics} to its default value. */
    public Builder clearLogSubsolverStatistics() {
      logSubsolverStatistics_ = false;
      hasLogSubsolverStatistics_ = false;
      return this;
    }

    /** Sets {@code log_prefix}. */
    public Builder setLogPrefix(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      logPrefix_ = value;
      hasLogPrefix_ = true;
      return this;
    }

    /** Resets {@code log_prefix} to its default value. */
    public Builder clearLogPrefix() {
      logPrefix_ = "";
      hasLogPrefix_ = false;
      return this;
    }

    /** Sets {@code log_to_stdout}. */
    public Builder setLogToStdout(boolean value) {
      logToStdout_ = value;
      hasLogToStdout_ = true;
      return this;
    }

    /** Resets {@code log_to_stdout} to its default value. */
    public Builder clearLogToStdout() {
      logToStdout_ = true;
      hasLogToStdout_ = false;
      return this;
    }

    /** Sets {@code log_to_response}. */
    public Builder setLogToResponse(boolean value) {
      logToResponse_ = value;
      hasLogToResponse_ = true;
      return this;
    }

    /** Resets {@code log_to_response} to its default value. */
    public Builder clearLogToResponse() {
      logToResponse_ = false;
      hasLogToResponse_ = false;
      return this;
    }

    /** Sets {@code use_pb_resolution}. */
    public Builder setUsePbResolution(boolean value) {
      usePbResolution_ = value;
      hasUsePbResolution_ = true;
      return this;
    }

    /** Resets {@code use_pb_resolution} to its default value. */
    public Builder clearUsePbResolution() {
      usePbResolution_ = false;
      hasUsePbResolution_ = false;
      return this;
    }

    /** Sets {@code minimize_reduction_during_pb_resolution}. */
    public Builder setMinimizeReductionDuringPbResolution(boolean value) {
      minimizeReductionDuringPbResolution_ = value;
      hasMinimizeReductionDuringPbResolution_ = true;
      return this;
    }

    /** Resets {@code minimize_reduction_during_pb_resolution} to its default value. */
    public Builder clearMinimizeReductionDuringPbResolution() {
      minimizeReductionDuringPbResolution_ = false;
      hasMinimizeReductionDuringPbResolution_ = false;
      return this;
    }

    /** Sets {@code count_assumption_levels_in_lbd}. */
    public Builder setCountAssumptionLevelsInLbd(boolean value) {
      countAssumptionLevelsInLbd_ = value;
      hasCountAssumptionLevelsInLbd_ = true;
      return this;
    }

    /** Resets {@code count_assumption_levels_in_lbd} to its default value. */
    public Builder clearCountAssumptionLevelsInLbd() {
      countAssumptionLevelsInLbd_ = true;
      hasCountAssumptionLevelsInLbd_ = false;
      return this;
    }

    /** Sets {@code presolve_bve_threshold}. */
    public Builder setPresolveBveThreshold(int value) {
      presolveBveThreshold_ = value;
      hasPresolveBveThreshold_ = true;
      return this;
    }

    /** Resets {@code presolve_bve_threshold} to its default value. */
    public Builder clearPresolveBveThreshold() {
      presolveBveThreshold_ = 500;
      hasPresolveBveThreshold_ = false;
      return this;
    }

    /** Sets {@code filter_sat_postsolve_clauses}. */
    public Builder setFilterSatPostsolveClauses(boolean value) {
      filterSatPostsolveClauses_ = value;
      hasFilterSatPostsolveClauses_ = true;
      return this;
    }

    /** Resets {@code filter_sat_postsolve_clauses} to its default value. */
    public Builder clearFilterSatPostsolveClauses() {
      filterSatPostsolveClauses_ = false;
      hasFilterSatPostsolveClauses_ = false;
      return this;
    }

    /** Sets {@code presolve_bve_clause_weight}. */
    public Builder setPresolveBveClauseWeight(int value) {
      presolveBveClauseWeight_ = value;
      hasPresolveBveClauseWeight_ = true;
      return this;
    }

    /** Resets {@code presolve_bve_clause_weight} to its default value. */
    public Builder clearPresolveBveClauseWeight() {
      presolveBveClauseWeight_ = 3;
      hasPresolveBveClauseWeight_ = false;
      return this;
    }

    /** Sets {@code probing_deterministic_time_limit}. */
    public Builder setProbingDeterministicTimeLimit(double value) {
      probingDeterministicTimeLimit_ = value;
      hasProbingDeterministicTimeLimit_ = true;
      return this;
    }

    /** Resets {@code probing_deterministic_time_limit} to its default value. */
    public Builder clearProbingDeterministicTimeLimit() {
      probingDeterministicTimeLimit_ = 1.0;
      hasProbingDeterministicTimeLimit_ = false;
      return this;
    }

    /** Sets {@code presolve_probing_deterministic_time_limit}. */
    public Builder setPresolveProbingDeterministicTimeLimit(double value) {
      presolveProbingDeterministicTimeLimit_ = value;
      hasPresolveProbingDeterministicTimeLimit_ = true;
      return this;
    }

    /** Resets {@code presolve_probing_deterministic_time_limit} to its default value. */
    public Builder clearPresolveProbingDeterministicTimeLimit() {
      presolveProbingDeterministicTimeLimit_ = 30.0;
      hasPresolveProbingDeterministicTimeLimit_ = false;
      return this;
    }

    /** Sets {@code presolve_blocked_clause}. */
    public Builder setPresolveBlockedClause(boolean value) {
      presolveBlockedClause_ = value;
      hasPresolveBlockedClause_ = true;
      return this;
    }

    /** Resets {@code presolve_blocked_clause} to its default value. */
    public Builder clearPresolveBlockedClause() {
      presolveBlockedClause_ = true;
      hasPresolveBlockedClause_ = false;
      return this;
    }

    /** Sets {@code presolve_use_bva}. */
    public Builder setPresolveUseBva(boolean value) {
      presolveUseBva_ = value;
      hasPresolveUseBva_ = true;
      return this;
    }

    /** Resets {@code presolve_use_bva} to its default value. */
    public Builder clearPresolveUseBva() {
      presolveUseBva_ = true;
      hasPresolveUseBva_ = false;
      return this;
    }

    /** Sets {@code presolve_bva_threshold}. */
    public Builder setPresolveBvaThreshold(int value) {
      presolveBvaThreshold_ = value;
      hasPresolveBvaThreshold_ = true;
      return this;
    }

    /** Resets {@code presolve_bva_threshold} to its default value. */
    public Builder clearPresolveBvaThreshold() {
      presolveBvaThreshold_ = 1;
      hasPresolveBvaThreshold_ = false;
      return this;
    }

    /** Sets {@code max_presolve_iterations}. */
    public Builder setMaxPresolveIterations(int value) {
      maxPresolveIterations_ = value;
      hasMaxPresolveIterations_ = true;
      return this;
    }

    /** Resets {@code max_presolve_iterations} to its default value. */
    public Builder clearMaxPresolveIterations() {
      maxPresolveIterations_ = 3;
      hasMaxPresolveIterations_ = false;
      return this;
    }

    /** Sets {@code cp_model_presolve}. */
    public Builder setCpModelPresolve(boolean value) {
      cpModelPresolve_ = value;
      hasCpModelPresolve_ = true;
      return this;
    }

    /** Resets {@code cp_model_presolve} to its default value. */
    public Builder clearCpModelPresolve() {
      cpModelPresolve_ = true;
      hasCpModelPresolve_ = false;
      return this;
    }

    /** Sets {@code cp_model_direct_solve}. */
    public Builder setCpModelDirectSolve(boolean value) {
      cpModelDirectSolve_ = value;
      hasCpModelDirectSolve_ = true;
      return this;
    }

    /** Resets {@code cp_model_direct_solve} to its default value. */
    public Builder clearCpModelDirectSolve() {
      cpModelDirectSolve_ = false;
      hasCpModelDirectSolve_ = false;
      return this;
    }

    /** Sets {@code cp_model_probing_level}. */
    public Builder setCpModelProbingLevel(int value) {
      cpModelProbingLevel_ = value;
      hasCpModelProbingLevel_ = true;
      return this;
    }

    /** Resets {@code cp_model_probing_level} to its default value. */
    public Builder clearCpModelProbingLevel() {
      cpModelProbingLevel_ = 2;
      hasCpModelProbingLevel_ = false;
      return this;
    }

    /** Sets {@code cp_model_use_sat_presolve}. */
    public Builder setCpModelUseSatPresolve(boolean value) {
      cpModelUseSatPresolve_ = value;
      hasCpModelUseSatPresolve_ = true;
      return this;
    }

    /** Resets {@code cp_model_use_sat_presolve} to its default value. */
    public Builder clearCpModelUseSatPresolve() {
      cpModelUseSatPresolve_ = true;
      hasCpModelUseSatPresolve_ = false;
      return this;
    }

    /** Sets {@code cp_model_pure_sat_presolve}. */
    public Builder setCpModelPureSatPresolve(boolean value) {
      cpModelPureSatPresolve_ = value;
      hasCpModelPureSatPresolve_ = true;
      return this;
    }

    /** Resets {@code cp_model_pure_sat_presolve} to its default value. */
    public Builder clearCpModelPureSatPresolve() {
      cpModelPureSatPresolve_ = false;
      hasCpModelPureSatPresolve_ = false;
      return this;
    }

    /** Sets {@code load_at_most_ones_in_sat_presolve}. */
    public Builder setLoadAtMostOnesInSatPresolve(boolean value) {
      loadAtMostOnesInSatPresolve_ = value;
      hasLoadAtMostOnesInSatPresolve_ = true;
      return this;
    }

    /** Resets {@code load_at_most_ones_in_sat_presolve} to its default value. */
    public Builder clearLoadAtMostOnesInSatPresolve() {
      loadAtMostOnesInSatPresolve_ = false;
      hasLoadAtMostOnesInSatPresolve_ = false;
      return this;
    }

    /** Sets {@code remove_fixed_variables_early}. */
    public Builder setRemoveFixedVariablesEarly(boolean value) {
      removeFixedVariablesEarly_ = value;
      hasRemoveFixedVariablesEarly_ = true;
      return this;
    }

    /** Resets {@code remove_fixed_variables_early} to its default value. */
    public Builder clearRemoveFixedVariablesEarly() {
      removeFixedVariablesEarly_ = true;
      hasRemoveFixedVariablesEarly_ = false;
      return this;
    }

    /** Sets {@code detect_table_with_cost}. */
    public Builder setDetectTableWithCost(boolean value) {
      detectTableWithCost_ = value;
      hasDetectTableWithCost_ = true;
      return this;
    }

    /** Resets {@code detect_table_with_cost} to its default value. */
    public Builder clearDetectTableWithCost() {
      detectTableWithCost_ = false;
      hasDetectTableWithCost_ = false;
      return this;
    }

    /** Sets {@code table_compression_level}. */
    public Builder setTableCompressionLevel(int value) {
      tableCompressionLevel_ = value;
      hasTableCompressionLevel_ = true;
      return this;
    }

    /** Resets {@code table_compression_level} to its default value. */
    public Builder clearTableCompressionLevel() {
      tableCompressionLevel_ = 2;
      hasTableCompressionLevel_ = false;
      return this;
    }

    /** Sets {@code expand_alldiff_constraints}. */
    public Builder setExpandAlldiffConstraints(boolean value) {
      expandAlldiffConstraints_ = value;
      hasExpandAlldiffConstraints_ = true;
      return this;
    }

    /** Resets {@code expand_alldiff_constraints} to its default value. */
    public Builder clearExpandAlldiffConstraints() {
      expandAlldiffConstraints_ = false;
      hasExpandAlldiffConstraints_ = false;
      return this;
    }

    /** Sets {@code max_alldiff_domain_size}. */
    public Builder setMaxAlldiffDomainSize(int value) {
      maxAlldiffDomainSize_ = value;
      hasMaxAlldiffDomainSize_ = true;
      return this;
    }

    /** Resets {@code max_alldiff_domain_size} to its default value. */
    public Builder clearMaxAlldiffDomainSize() {
      maxAlldiffDomainSize_ = 256;
      hasMaxAlldiffDomainSize_ = false;
      return this;
    }

    /** Sets {@code expand_reservoir_constraints}. */
    public Builder setExpandReservoirConstraints(boolean value) {
      expandReservoirConstraints_ = value;
      hasExpandReservoirConstraints_ = true;
      return this;
    }

    /** Resets {@code expand_reservoir_constraints} to its default value. */
    public Builder clearExpandReservoirConstraints() {
      expandReservoirConstraints_ = true;
      hasExpandReservoirConstraints_ = false;
      return this;
    }

    /** Sets {@code max_domain_size_for_linear2_expansion}. */
    public Builder setMaxDomainSizeForLinear2Expansion(int value) {
      maxDomainSizeForLinear2Expansion_ = value;
      hasMaxDomainSizeForLinear2Expansion_ = true;
      return this;
    }

    /** Resets {@code max_domain_size_for_linear2_expansion} to its default value. */
    public Builder clearMaxDomainSizeForLinear2Expansion() {
      maxDomainSizeForLinear2Expansion_ = 8;
      hasMaxDomainSizeForLinear2Expansion_ = false;
      return this;
    }

    /** Sets {@code expand_reservoir_using_circuit}. */
    public Builder setExpandReservoirUsingCircuit(boolean value) {
      expandReservoirUsingCircuit_ = value;
      hasExpandReservoirUsingCircuit_ = true;
      return this;
    }

    /** Resets {@code expand_reservoir_using_circuit} to its default value. */
    public Builder clearExpandReservoirUsingCircuit() {
      expandReservoirUsingCircuit_ = false;
      hasExpandReservoirUsingCircuit_ = false;
      return this;
    }

    /** Sets {@code encode_cumulative_as_reservoir}. */
    public Builder setEncodeCumulativeAsReservoir(boolean value) {
      encodeCumulativeAsReservoir_ = value;
      hasEncodeCumulativeAsReservoir_ = true;
      return this;
    }

    /** Resets {@code encode_cumulative_as_reservoir} to its default value. */
    public Builder clearEncodeCumulativeAsReservoir() {
      encodeCumulativeAsReservoir_ = false;
      hasEncodeCumulativeAsReservoir_ = false;
      return this;
    }

    /** Sets {@code max_lin_max_size_for_expansion}. */
    public Builder setMaxLinMaxSizeForExpansion(int value) {
      maxLinMaxSizeForExpansion_ = value;
      hasMaxLinMaxSizeForExpansion_ = true;
      return this;
    }

    /** Resets {@code max_lin_max_size_for_expansion} to its default value. */
    public Builder clearMaxLinMaxSizeForExpansion() {
      maxLinMaxSizeForExpansion_ = 0;
      hasMaxLinMaxSizeForExpansion_ = false;
      return this;
    }

    /** Sets {@code disable_constraint_expansion}. */
    public Builder setDisableConstraintExpansion(boolean value) {
      disableConstraintExpansion_ = value;
      hasDisableConstraintExpansion_ = true;
      return this;
    }

    /** Resets {@code disable_constraint_expansion} to its default value. */
    public Builder clearDisableConstraintExpansion() {
      disableConstraintExpansion_ = false;
      hasDisableConstraintExpansion_ = false;
      return this;
    }

    /** Sets {@code encode_complex_linear_constraint_with_integer}. */
    public Builder setEncodeComplexLinearConstraintWithInteger(boolean value) {
      encodeComplexLinearConstraintWithInteger_ = value;
      hasEncodeComplexLinearConstraintWithInteger_ = true;
      return this;
    }

    /** Resets {@code encode_complex_linear_constraint_with_integer} to its default value. */
    public Builder clearEncodeComplexLinearConstraintWithInteger() {
      encodeComplexLinearConstraintWithInteger_ = false;
      hasEncodeComplexLinearConstraintWithInteger_ = false;
      return this;
    }

    /** Sets {@code detect_encoded_complex_domain}. */
    public Builder setDetectEncodedComplexDomain(boolean value) {
      detectEncodedComplexDomain_ = value;
      hasDetectEncodedComplexDomain_ = true;
      return this;
    }

    /** Resets {@code detect_encoded_complex_domain} to its default value. */
    public Builder clearDetectEncodedComplexDomain() {
      detectEncodedComplexDomain_ = true;
      hasDetectEncodedComplexDomain_ = false;
      return this;
    }

    /** Sets {@code merge_no_overlap_work_limit}. */
    public Builder setMergeNoOverlapWorkLimit(double value) {
      mergeNoOverlapWorkLimit_ = value;
      hasMergeNoOverlapWorkLimit_ = true;
      return this;
    }

    /** Resets {@code merge_no_overlap_work_limit} to its default value. */
    public Builder clearMergeNoOverlapWorkLimit() {
      mergeNoOverlapWorkLimit_ = 1e12;
      hasMergeNoOverlapWorkLimit_ = false;
      return this;
    }

    /** Sets {@code merge_at_most_one_work_limit}. */
    public Builder setMergeAtMostOneWorkLimit(double value) {
      mergeAtMostOneWorkLimit_ = value;
      hasMergeAtMostOneWorkLimit_ = true;
      return this;
    }

    /** Resets {@code merge_at_most_one_work_limit} to its default value. */
    public Builder clearMergeAtMostOneWorkLimit() {
      mergeAtMostOneWorkLimit_ = 1e8;
      hasMergeAtMostOneWorkLimit_ = false;
      return this;
    }

    /** Sets {@code presolve_substitution_level}. */
    public Builder setPresolveSubstitutionLevel(int value) {
      presolveSubstitutionLevel_ = value;
      hasPresolveSubstitutionLevel_ = true;
      return this;
    }

    /** Resets {@code presolve_substitution_level} to its default value. */
    public Builder clearPresolveSubstitutionLevel() {
      presolveSubstitutionLevel_ = 1;
      hasPresolveSubstitutionLevel_ = false;
      return this;
    }

    /** Sets {@code presolve_extract_integer_enforcement}. */
    public Builder setPresolveExtractIntegerEnforcement(boolean value) {
      presolveExtractIntegerEnforcement_ = value;
      hasPresolveExtractIntegerEnforcement_ = true;
      return this;
    }

    /** Resets {@code presolve_extract_integer_enforcement} to its default value. */
    public Builder clearPresolveExtractIntegerEnforcement() {
      presolveExtractIntegerEnforcement_ = false;
      hasPresolveExtractIntegerEnforcement_ = false;
      return this;
    }

    /** Sets {@code presolve_inclusion_work_limit}. */
    public Builder setPresolveInclusionWorkLimit(long value) {
      presolveInclusionWorkLimit_ = value;
      hasPresolveInclusionWorkLimit_ = true;
      return this;
    }

    /** Resets {@code presolve_inclusion_work_limit} to its default value. */
    public Builder clearPresolveInclusionWorkLimit() {
      presolveInclusionWorkLimit_ = 100000000L;
      hasPresolveInclusionWorkLimit_ = false;
      return this;
    }

    /** Sets {@code ignore_names}. */
    public Builder setIgnoreNames(boolean value) {
      ignoreNames_ = value;
      hasIgnoreNames_ = true;
      return this;
    }

    /** Resets {@code ignore_names} to its default value. */
    public Builder clearIgnoreNames() {
      ignoreNames_ = true;
      hasIgnoreNames_ = false;
      return this;
    }

    /** Sets {@code infer_all_diffs}. */
    public Builder setInferAllDiffs(boolean value) {
      inferAllDiffs_ = value;
      hasInferAllDiffs_ = true;
      return this;
    }

    /** Resets {@code infer_all_diffs} to its default value. */
    public Builder clearInferAllDiffs() {
      inferAllDiffs_ = true;
      hasInferAllDiffs_ = false;
      return this;
    }

    /** Sets {@code find_big_linear_overlap}. */
    public Builder setFindBigLinearOverlap(boolean value) {
      findBigLinearOverlap_ = value;
      hasFindBigLinearOverlap_ = true;
      return this;
    }

    /** Resets {@code find_big_linear_overlap} to its default value. */
    public Builder clearFindBigLinearOverlap() {
      findBigLinearOverlap_ = true;
      hasFindBigLinearOverlap_ = false;
      return this;
    }

    /** Sets {@code find_clauses_that_are_exactly_one}. */
    public Builder setFindClausesThatAreExactlyOne(boolean value) {
      findClausesThatAreExactlyOne_ = value;
      hasFindClausesThatAreExactlyOne_ = true;
      return this;
    }

    /** Resets {@code find_clauses_that_are_exactly_one} to its default value. */
    public Builder clearFindClausesThatAreExactlyOne() {
      findClausesThatAreExactlyOne_ = true;
      hasFindClausesThatAreExactlyOne_ = false;
      return this;
    }

    /** Sets {@code use_sat_inprocessing}. */
    public Builder setUseSatInprocessing(boolean value) {
      useSatInprocessing_ = value;
      hasUseSatInprocessing_ = true;
      return this;
    }

    /** Resets {@code use_sat_inprocessing} to its default value. */
    public Builder clearUseSatInprocessing() {
      useSatInprocessing_ = true;
      hasUseSatInprocessing_ = false;
      return this;
    }

    /** Sets {@code inprocessing_dtime_ratio}. */
    public Builder setInprocessingDtimeRatio(double value) {
      inprocessingDtimeRatio_ = value;
      hasInprocessingDtimeRatio_ = true;
      return this;
    }

    /** Resets {@code inprocessing_dtime_ratio} to its default value. */
    public Builder clearInprocessingDtimeRatio() {
      inprocessingDtimeRatio_ = 0.2;
      hasInprocessingDtimeRatio_ = false;
      return this;
    }

    /** Sets {@code inprocessing_probing_dtime}. */
    public Builder setInprocessingProbingDtime(double value) {
      inprocessingProbingDtime_ = value;
      hasInprocessingProbingDtime_ = true;
      return this;
    }

    /** Resets {@code inprocessing_probing_dtime} to its default value. */
    public Builder clearInprocessingProbingDtime() {
      inprocessingProbingDtime_ = 1.0;
      hasInprocessingProbingDtime_ = false;
      return this;
    }

    /** Sets {@code inprocessing_minimization_dtime}. */
    public Builder setInprocessingMinimizationDtime(double value) {
      inprocessingMinimizationDtime_ = value;
      hasInprocessingMinimizationDtime_ = true;
      return this;
    }

    /** Resets {@code inprocessing_minimization_dtime} to its default value. */
    public Builder clearInprocessingMinimizationDtime() {
      inprocessingMinimizationDtime_ = 1.0;
      hasInprocessingMinimizationDtime_ = false;
      return this;
    }

    /** Sets {@code inprocessing_minimization_use_conflict_analysis}. */
    public Builder setInprocessingMinimizationUseConflictAnalysis(boolean value) {
      inprocessingMinimizationUseConflictAnalysis_ = value;
      hasInprocessingMinimizationUseConflictAnalysis_ = true;
      return this;
    }

    /** Resets {@code inprocessing_minimization_use_conflict_analysis} to its default value. */
    public Builder clearInprocessingMinimizationUseConflictAnalysis() {
      inprocessingMinimizationUseConflictAnalysis_ = true;
      hasInprocessingMinimizationUseConflictAnalysis_ = false;
      return this;
    }

    /** Sets {@code num_ordering_in_vivification}. */
    public Builder setNumOrderingInVivification(int value) {
      numOrderingInVivification_ = value;
      hasNumOrderingInVivification_ = true;
      return this;
    }

    /** Resets {@code num_ordering_in_vivification} to its default value. */
    public Builder clearNumOrderingInVivification() {
      numOrderingInVivification_ = 1;
      hasNumOrderingInVivification_ = false;
      return this;
    }

    /** Sets {@code inprocessing_use_congruence_closure}. */
    public Builder setInprocessingUseCongruenceClosure(boolean value) {
      inprocessingUseCongruenceClosure_ = value;
      hasInprocessingUseCongruenceClosure_ = true;
      return this;
    }

    /** Resets {@code inprocessing_use_congruence_closure} to its default value. */
    public Builder clearInprocessingUseCongruenceClosure() {
      inprocessingUseCongruenceClosure_ = true;
      hasInprocessingUseCongruenceClosure_ = false;
      return this;
    }

    /** Sets {@code inprocessing_detect_and_sweep_circuit}. */
    public Builder setInprocessingDetectAndSweepCircuit(boolean value) {
      inprocessingDetectAndSweepCircuit_ = value;
      hasInprocessingDetectAndSweepCircuit_ = true;
      return this;
    }

    /** Resets {@code inprocessing_detect_and_sweep_circuit} to its default value. */
    public Builder clearInprocessingDetectAndSweepCircuit() {
      inprocessingDetectAndSweepCircuit_ = false;
      hasInprocessingDetectAndSweepCircuit_ = false;
      return this;
    }

    /** Sets {@code inprocessing_use_sat_sweeping}. */
    public Builder setInprocessingUseSatSweeping(boolean value) {
      inprocessingUseSatSweeping_ = value;
      hasInprocessingUseSatSweeping_ = true;
      return this;
    }

    /** Resets {@code inprocessing_use_sat_sweeping} to its default value. */
    public Builder clearInprocessingUseSatSweeping() {
      inprocessingUseSatSweeping_ = false;
      hasInprocessingUseSatSweeping_ = false;
      return this;
    }

    /** Sets {@code inprocessing_use_transitive_reduction}. */
    public Builder setInprocessingUseTransitiveReduction(boolean value) {
      inprocessingUseTransitiveReduction_ = value;
      hasInprocessingUseTransitiveReduction_ = true;
      return this;
    }

    /** Resets {@code inprocessing_use_transitive_reduction} to its default value. */
    public Builder clearInprocessingUseTransitiveReduction() {
      inprocessingUseTransitiveReduction_ = true;
      hasInprocessingUseTransitiveReduction_ = false;
      return this;
    }

    /** Sets {@code num_workers}. */
    public Builder setNumWorkers(int value) {
      numWorkers_ = value;
      hasNumWorkers_ = true;
      return this;
    }

    /** Resets {@code num_workers} to its default value. */
    public Builder clearNumWorkers() {
      numWorkers_ = 0;
      hasNumWorkers_ = false;
      return this;
    }

    /** Sets {@code num_search_workers}. */
    public Builder setNumSearchWorkers(int value) {
      numSearchWorkers_ = value;
      hasNumSearchWorkers_ = true;
      return this;
    }

    /** Resets {@code num_search_workers} to its default value. */
    public Builder clearNumSearchWorkers() {
      numSearchWorkers_ = 0;
      hasNumSearchWorkers_ = false;
      return this;
    }

    /** Sets {@code num_full_subsolvers}. */
    public Builder setNumFullSubsolvers(int value) {
      numFullSubsolvers_ = value;
      hasNumFullSubsolvers_ = true;
      return this;
    }

    /** Resets {@code num_full_subsolvers} to its default value. */
    public Builder clearNumFullSubsolvers() {
      numFullSubsolvers_ = 0;
      hasNumFullSubsolvers_ = false;
      return this;
    }

    /** Replaces the element {@code index} of {@code subsolvers}. */
    public Builder setSubsolvers(int index, String value) {
      if (subsolvers_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      subsolvers_.set(index, java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends a value to {@code subsolvers}. */
    public Builder addSubsolvers(String value) {
      if (subsolvers_ == null) {
        subsolvers_ = new java.util.ArrayList<>();
      }
      subsolvers_.add(java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends all values to {@code subsolvers}. */
    public Builder addAllSubsolvers(Iterable<? extends String> values) {
      if (subsolvers_ == null) {
        subsolvers_ = new java.util.ArrayList<>();
      }
      for (String v : values) {
        subsolvers_.add(java.util.Objects.requireNonNull(v));
      }
      return this;
    }

    /** Removes all values of {@code subsolvers}. */
    public Builder clearSubsolvers() {
      subsolvers_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code extra_subsolvers}. */
    public Builder setExtraSubsolvers(int index, String value) {
      if (extraSubsolvers_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      extraSubsolvers_.set(index, java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends a value to {@code extra_subsolvers}. */
    public Builder addExtraSubsolvers(String value) {
      if (extraSubsolvers_ == null) {
        extraSubsolvers_ = new java.util.ArrayList<>();
      }
      extraSubsolvers_.add(java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends all values to {@code extra_subsolvers}. */
    public Builder addAllExtraSubsolvers(Iterable<? extends String> values) {
      if (extraSubsolvers_ == null) {
        extraSubsolvers_ = new java.util.ArrayList<>();
      }
      for (String v : values) {
        extraSubsolvers_.add(java.util.Objects.requireNonNull(v));
      }
      return this;
    }

    /** Removes all values of {@code extra_subsolvers}. */
    public Builder clearExtraSubsolvers() {
      extraSubsolvers_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code ignore_subsolvers}. */
    public Builder setIgnoreSubsolvers(int index, String value) {
      if (ignoreSubsolvers_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      ignoreSubsolvers_.set(index, java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends a value to {@code ignore_subsolvers}. */
    public Builder addIgnoreSubsolvers(String value) {
      if (ignoreSubsolvers_ == null) {
        ignoreSubsolvers_ = new java.util.ArrayList<>();
      }
      ignoreSubsolvers_.add(java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends all values to {@code ignore_subsolvers}. */
    public Builder addAllIgnoreSubsolvers(Iterable<? extends String> values) {
      if (ignoreSubsolvers_ == null) {
        ignoreSubsolvers_ = new java.util.ArrayList<>();
      }
      for (String v : values) {
        ignoreSubsolvers_.add(java.util.Objects.requireNonNull(v));
      }
      return this;
    }

    /** Removes all values of {@code ignore_subsolvers}. */
    public Builder clearIgnoreSubsolvers() {
      ignoreSubsolvers_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code filter_subsolvers}. */
    public Builder setFilterSubsolvers(int index, String value) {
      if (filterSubsolvers_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      filterSubsolvers_.set(index, java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends a value to {@code filter_subsolvers}. */
    public Builder addFilterSubsolvers(String value) {
      if (filterSubsolvers_ == null) {
        filterSubsolvers_ = new java.util.ArrayList<>();
      }
      filterSubsolvers_.add(java.util.Objects.requireNonNull(value));
      return this;
    }

    /** Appends all values to {@code filter_subsolvers}. */
    public Builder addAllFilterSubsolvers(Iterable<? extends String> values) {
      if (filterSubsolvers_ == null) {
        filterSubsolvers_ = new java.util.ArrayList<>();
      }
      for (String v : values) {
        filterSubsolvers_.add(java.util.Objects.requireNonNull(v));
      }
      return this;
    }

    /** Removes all values of {@code filter_subsolvers}. */
    public Builder clearFilterSubsolvers() {
      filterSubsolvers_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code subsolver_params}. */
    public Builder setSubsolverParams(int index, SatParameters value) {
      if (subsolverParams_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      subsolverParams_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code subsolver_params}. */
    public Builder addSubsolverParams(SatParameters value) {
      if (subsolverParams_ == null) {
        subsolverParams_ = new java.util.ArrayList<>();
      }
      subsolverParams_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code subsolver_params}. */
    public Builder addAllSubsolverParams(Iterable<? extends SatParameters> values) {
      if (subsolverParams_ == null) {
        subsolverParams_ = new java.util.ArrayList<>();
      }
      for (SatParameters v : values) {
        subsolverParams_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code subsolver_params}. */
    public Builder clearSubsolverParams() {
      subsolverParams_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code subsolver_params} by the content of a builder. */
    public Builder setSubsolverParams(int index, SatParameters.Builder builderForValue) {
      return setSubsolverParams(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code subsolver_params}. */
    public Builder addSubsolverParams(SatParameters.Builder builderForValue) {
      return addSubsolverParams(builderForValue.build());
    }

    /** Inserts a value into {@code subsolver_params}. */
    public Builder addSubsolverParams(int index, SatParameters value) {
      if (subsolverParams_ == null) {
        subsolverParams_ = new java.util.ArrayList<>();
      }
      subsolverParams_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code subsolver_params}. */
    public Builder removeSubsolverParams(int index) {
      if (subsolverParams_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      subsolverParams_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code subsolver_params}; changes to it are reflected in this builder. */
    public SatParameters.Builder getSubsolverParamsBuilder(int index) {
      if (subsolverParams_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      SatParameters e = subsolverParams_.get(index);
      if (e instanceof SatParameters.Builder) {
        return (SatParameters.Builder) e;
      }
      SatParameters.Builder b = SatParameters.newBuilder(e);
      subsolverParams_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code subsolver_params} and returns its builder. */
    public SatParameters.Builder addSubsolverParamsBuilder() {
      if (subsolverParams_ == null) {
        subsolverParams_ = new java.util.ArrayList<>();
      }
      SatParameters.Builder b = SatParameters.newBuilder();
      subsolverParams_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code subsolver_params}. */
    public java.util.List<SatParameters.Builder> getSubsolverParamsBuilderList() {
      java.util.List<SatParameters.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getSubsolverParamsCount(); k++) {
        r.add(getSubsolverParamsBuilder(k));
      }
      return r;
    }

    /** Sets {@code interleave_search}. */
    public Builder setInterleaveSearch(boolean value) {
      interleaveSearch_ = value;
      hasInterleaveSearch_ = true;
      return this;
    }

    /** Resets {@code interleave_search} to its default value. */
    public Builder clearInterleaveSearch() {
      interleaveSearch_ = false;
      hasInterleaveSearch_ = false;
      return this;
    }

    /** Sets {@code interleave_batch_size}. */
    public Builder setInterleaveBatchSize(int value) {
      interleaveBatchSize_ = value;
      hasInterleaveBatchSize_ = true;
      return this;
    }

    /** Resets {@code interleave_batch_size} to its default value. */
    public Builder clearInterleaveBatchSize() {
      interleaveBatchSize_ = 0;
      hasInterleaveBatchSize_ = false;
      return this;
    }

    /** Sets {@code share_objective_bounds}. */
    public Builder setShareObjectiveBounds(boolean value) {
      shareObjectiveBounds_ = value;
      hasShareObjectiveBounds_ = true;
      return this;
    }

    /** Resets {@code share_objective_bounds} to its default value. */
    public Builder clearShareObjectiveBounds() {
      shareObjectiveBounds_ = true;
      hasShareObjectiveBounds_ = false;
      return this;
    }

    /** Sets {@code share_level_zero_bounds}. */
    public Builder setShareLevelZeroBounds(boolean value) {
      shareLevelZeroBounds_ = value;
      hasShareLevelZeroBounds_ = true;
      return this;
    }

    /** Resets {@code share_level_zero_bounds} to its default value. */
    public Builder clearShareLevelZeroBounds() {
      shareLevelZeroBounds_ = true;
      hasShareLevelZeroBounds_ = false;
      return this;
    }

    /** Sets {@code share_linear2_bounds}. */
    public Builder setShareLinear2Bounds(boolean value) {
      shareLinear2Bounds_ = value;
      hasShareLinear2Bounds_ = true;
      return this;
    }

    /** Resets {@code share_linear2_bounds} to its default value. */
    public Builder clearShareLinear2Bounds() {
      shareLinear2Bounds_ = false;
      hasShareLinear2Bounds_ = false;
      return this;
    }

    /** Sets {@code share_binary_clauses}. */
    public Builder setShareBinaryClauses(boolean value) {
      shareBinaryClauses_ = value;
      hasShareBinaryClauses_ = true;
      return this;
    }

    /** Resets {@code share_binary_clauses} to its default value. */
    public Builder clearShareBinaryClauses() {
      shareBinaryClauses_ = true;
      hasShareBinaryClauses_ = false;
      return this;
    }

    /** Sets {@code share_glue_clauses}. */
    public Builder setShareGlueClauses(boolean value) {
      shareGlueClauses_ = value;
      hasShareGlueClauses_ = true;
      return this;
    }

    /** Resets {@code share_glue_clauses} to its default value. */
    public Builder clearShareGlueClauses() {
      shareGlueClauses_ = true;
      hasShareGlueClauses_ = false;
      return this;
    }

    /** Sets {@code minimize_shared_clauses}. */
    public Builder setMinimizeSharedClauses(boolean value) {
      minimizeSharedClauses_ = value;
      hasMinimizeSharedClauses_ = true;
      return this;
    }

    /** Resets {@code minimize_shared_clauses} to its default value. */
    public Builder clearMinimizeSharedClauses() {
      minimizeSharedClauses_ = true;
      hasMinimizeSharedClauses_ = false;
      return this;
    }

    /** Sets {@code share_glue_clauses_dtime}. */
    public Builder setShareGlueClausesDtime(double value) {
      shareGlueClausesDtime_ = value;
      hasShareGlueClausesDtime_ = true;
      return this;
    }

    /** Resets {@code share_glue_clauses_dtime} to its default value. */
    public Builder clearShareGlueClausesDtime() {
      shareGlueClausesDtime_ = 1.0;
      hasShareGlueClausesDtime_ = false;
      return this;
    }

    /** Sets {@code check_lrat_proof}. */
    public Builder setCheckLratProof(boolean value) {
      checkLratProof_ = value;
      hasCheckLratProof_ = true;
      return this;
    }

    /** Resets {@code check_lrat_proof} to its default value. */
    public Builder clearCheckLratProof() {
      checkLratProof_ = false;
      hasCheckLratProof_ = false;
      return this;
    }

    /** Sets {@code check_merged_lrat_proof}. */
    public Builder setCheckMergedLratProof(boolean value) {
      checkMergedLratProof_ = value;
      hasCheckMergedLratProof_ = true;
      return this;
    }

    /** Resets {@code check_merged_lrat_proof} to its default value. */
    public Builder clearCheckMergedLratProof() {
      checkMergedLratProof_ = false;
      hasCheckMergedLratProof_ = false;
      return this;
    }

    /** Sets {@code output_lrat_proof}. */
    public Builder setOutputLratProof(boolean value) {
      outputLratProof_ = value;
      hasOutputLratProof_ = true;
      return this;
    }

    /** Resets {@code output_lrat_proof} to its default value. */
    public Builder clearOutputLratProof() {
      outputLratProof_ = false;
      hasOutputLratProof_ = false;
      return this;
    }

    /** Sets {@code debug_postsolve_with_full_solver}. */
    public Builder setDebugPostsolveWithFullSolver(boolean value) {
      debugPostsolveWithFullSolver_ = value;
      hasDebugPostsolveWithFullSolver_ = true;
      return this;
    }

    /** Resets {@code debug_postsolve_with_full_solver} to its default value. */
    public Builder clearDebugPostsolveWithFullSolver() {
      debugPostsolveWithFullSolver_ = false;
      hasDebugPostsolveWithFullSolver_ = false;
      return this;
    }

    /** Sets {@code debug_max_num_presolve_operations}. */
    public Builder setDebugMaxNumPresolveOperations(int value) {
      debugMaxNumPresolveOperations_ = value;
      hasDebugMaxNumPresolveOperations_ = true;
      return this;
    }

    /** Resets {@code debug_max_num_presolve_operations} to its default value. */
    public Builder clearDebugMaxNumPresolveOperations() {
      debugMaxNumPresolveOperations_ = 0;
      hasDebugMaxNumPresolveOperations_ = false;
      return this;
    }

    /** Sets {@code debug_crash_on_bad_hint}. */
    public Builder setDebugCrashOnBadHint(boolean value) {
      debugCrashOnBadHint_ = value;
      hasDebugCrashOnBadHint_ = true;
      return this;
    }

    /** Resets {@code debug_crash_on_bad_hint} to its default value. */
    public Builder clearDebugCrashOnBadHint() {
      debugCrashOnBadHint_ = false;
      hasDebugCrashOnBadHint_ = false;
      return this;
    }

    /** Sets {@code debug_crash_if_presolve_breaks_hint}. */
    public Builder setDebugCrashIfPresolveBreaksHint(boolean value) {
      debugCrashIfPresolveBreaksHint_ = value;
      hasDebugCrashIfPresolveBreaksHint_ = true;
      return this;
    }

    /** Resets {@code debug_crash_if_presolve_breaks_hint} to its default value. */
    public Builder clearDebugCrashIfPresolveBreaksHint() {
      debugCrashIfPresolveBreaksHint_ = false;
      hasDebugCrashIfPresolveBreaksHint_ = false;
      return this;
    }

    /** Sets {@code debug_crash_if_lrat_check_fails}. */
    public Builder setDebugCrashIfLratCheckFails(boolean value) {
      debugCrashIfLratCheckFails_ = value;
      hasDebugCrashIfLratCheckFails_ = true;
      return this;
    }

    /** Resets {@code debug_crash_if_lrat_check_fails} to its default value. */
    public Builder clearDebugCrashIfLratCheckFails() {
      debugCrashIfLratCheckFails_ = false;
      hasDebugCrashIfLratCheckFails_ = false;
      return this;
    }

    /** Sets {@code use_optimization_hints}. */
    public Builder setUseOptimizationHints(boolean value) {
      useOptimizationHints_ = value;
      hasUseOptimizationHints_ = true;
      return this;
    }

    /** Resets {@code use_optimization_hints} to its default value. */
    public Builder clearUseOptimizationHints() {
      useOptimizationHints_ = true;
      hasUseOptimizationHints_ = false;
      return this;
    }

    /** Sets {@code core_minimization_level}. */
    public Builder setCoreMinimizationLevel(int value) {
      coreMinimizationLevel_ = value;
      hasCoreMinimizationLevel_ = true;
      return this;
    }

    /** Resets {@code core_minimization_level} to its default value. */
    public Builder clearCoreMinimizationLevel() {
      coreMinimizationLevel_ = 2;
      hasCoreMinimizationLevel_ = false;
      return this;
    }

    /** Sets {@code find_multiple_cores}. */
    public Builder setFindMultipleCores(boolean value) {
      findMultipleCores_ = value;
      hasFindMultipleCores_ = true;
      return this;
    }

    /** Resets {@code find_multiple_cores} to its default value. */
    public Builder clearFindMultipleCores() {
      findMultipleCores_ = true;
      hasFindMultipleCores_ = false;
      return this;
    }

    /** Sets {@code cover_optimization}. */
    public Builder setCoverOptimization(boolean value) {
      coverOptimization_ = value;
      hasCoverOptimization_ = true;
      return this;
    }

    /** Resets {@code cover_optimization} to its default value. */
    public Builder clearCoverOptimization() {
      coverOptimization_ = true;
      hasCoverOptimization_ = false;
      return this;
    }

    /** Sets {@code max_sat_assumption_order}. */
    public Builder setMaxSatAssumptionOrder(SatParameters.MaxSatAssumptionOrder value) {
      if (value == null) {
        throw new NullPointerException();
      }
      maxSatAssumptionOrder_ = value.getNumber();
      hasMaxSatAssumptionOrder_ = true;
      return this;
    }

    /** Sets the number of {@code max_sat_assumption_order}. */
    public Builder setMaxSatAssumptionOrderValue(int value) {
      maxSatAssumptionOrder_ = value;
      hasMaxSatAssumptionOrder_ = true;
      return this;
    }

    /** Resets {@code max_sat_assumption_order} to its default value. */
    public Builder clearMaxSatAssumptionOrder() {
      maxSatAssumptionOrder_ = 0;
      hasMaxSatAssumptionOrder_ = false;
      return this;
    }

    /** Sets {@code max_sat_reverse_assumption_order}. */
    public Builder setMaxSatReverseAssumptionOrder(boolean value) {
      maxSatReverseAssumptionOrder_ = value;
      hasMaxSatReverseAssumptionOrder_ = true;
      return this;
    }

    /** Resets {@code max_sat_reverse_assumption_order} to its default value. */
    public Builder clearMaxSatReverseAssumptionOrder() {
      maxSatReverseAssumptionOrder_ = false;
      hasMaxSatReverseAssumptionOrder_ = false;
      return this;
    }

    /** Sets {@code max_sat_stratification}. */
    public Builder setMaxSatStratification(SatParameters.MaxSatStratificationAlgorithm value) {
      if (value == null) {
        throw new NullPointerException();
      }
      maxSatStratification_ = value.getNumber();
      hasMaxSatStratification_ = true;
      return this;
    }

    /** Sets the number of {@code max_sat_stratification}. */
    public Builder setMaxSatStratificationValue(int value) {
      maxSatStratification_ = value;
      hasMaxSatStratification_ = true;
      return this;
    }

    /** Resets {@code max_sat_stratification} to its default value. */
    public Builder clearMaxSatStratification() {
      maxSatStratification_ = 1;
      hasMaxSatStratification_ = false;
      return this;
    }

    /** Sets {@code propagation_loop_detection_factor}. */
    public Builder setPropagationLoopDetectionFactor(double value) {
      propagationLoopDetectionFactor_ = value;
      hasPropagationLoopDetectionFactor_ = true;
      return this;
    }

    /** Resets {@code propagation_loop_detection_factor} to its default value. */
    public Builder clearPropagationLoopDetectionFactor() {
      propagationLoopDetectionFactor_ = 10.0;
      hasPropagationLoopDetectionFactor_ = false;
      return this;
    }

    /** Sets {@code use_precedences_in_disjunctive_constraint}. */
    public Builder setUsePrecedencesInDisjunctiveConstraint(boolean value) {
      usePrecedencesInDisjunctiveConstraint_ = value;
      hasUsePrecedencesInDisjunctiveConstraint_ = true;
      return this;
    }

    /** Resets {@code use_precedences_in_disjunctive_constraint} to its default value. */
    public Builder clearUsePrecedencesInDisjunctiveConstraint() {
      usePrecedencesInDisjunctiveConstraint_ = true;
      hasUsePrecedencesInDisjunctiveConstraint_ = false;
      return this;
    }

    /** Sets {@code transitive_precedences_work_limit}. */
    public Builder setTransitivePrecedencesWorkLimit(int value) {
      transitivePrecedencesWorkLimit_ = value;
      hasTransitivePrecedencesWorkLimit_ = true;
      return this;
    }

    /** Resets {@code transitive_precedences_work_limit} to its default value. */
    public Builder clearTransitivePrecedencesWorkLimit() {
      transitivePrecedencesWorkLimit_ = 1000000;
      hasTransitivePrecedencesWorkLimit_ = false;
      return this;
    }

    /** Sets {@code max_size_to_create_precedence_literals_in_disjunctive}. */
    public Builder setMaxSizeToCreatePrecedenceLiteralsInDisjunctive(int value) {
      maxSizeToCreatePrecedenceLiteralsInDisjunctive_ = value;
      hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_ = true;
      return this;
    }

    /** Resets {@code max_size_to_create_precedence_literals_in_disjunctive} to its default value. */
    public Builder clearMaxSizeToCreatePrecedenceLiteralsInDisjunctive() {
      maxSizeToCreatePrecedenceLiteralsInDisjunctive_ = 60;
      hasMaxSizeToCreatePrecedenceLiteralsInDisjunctive_ = false;
      return this;
    }

    /** Sets {@code use_strong_propagation_in_disjunctive}. */
    public Builder setUseStrongPropagationInDisjunctive(boolean value) {
      useStrongPropagationInDisjunctive_ = value;
      hasUseStrongPropagationInDisjunctive_ = true;
      return this;
    }

    /** Resets {@code use_strong_propagation_in_disjunctive} to its default value. */
    public Builder clearUseStrongPropagationInDisjunctive() {
      useStrongPropagationInDisjunctive_ = false;
      hasUseStrongPropagationInDisjunctive_ = false;
      return this;
    }

    /** Sets {@code use_dynamic_precedence_in_disjunctive}. */
    public Builder setUseDynamicPrecedenceInDisjunctive(boolean value) {
      useDynamicPrecedenceInDisjunctive_ = value;
      hasUseDynamicPrecedenceInDisjunctive_ = true;
      return this;
    }

    /** Resets {@code use_dynamic_precedence_in_disjunctive} to its default value. */
    public Builder clearUseDynamicPrecedenceInDisjunctive() {
      useDynamicPrecedenceInDisjunctive_ = false;
      hasUseDynamicPrecedenceInDisjunctive_ = false;
      return this;
    }

    /** Sets {@code use_dynamic_precedence_in_cumulative}. */
    public Builder setUseDynamicPrecedenceInCumulative(boolean value) {
      useDynamicPrecedenceInCumulative_ = value;
      hasUseDynamicPrecedenceInCumulative_ = true;
      return this;
    }

    /** Resets {@code use_dynamic_precedence_in_cumulative} to its default value. */
    public Builder clearUseDynamicPrecedenceInCumulative() {
      useDynamicPrecedenceInCumulative_ = false;
      hasUseDynamicPrecedenceInCumulative_ = false;
      return this;
    }

    /** Sets {@code use_overload_checker_in_cumulative}. */
    public Builder setUseOverloadCheckerInCumulative(boolean value) {
      useOverloadCheckerInCumulative_ = value;
      hasUseOverloadCheckerInCumulative_ = true;
      return this;
    }

    /** Resets {@code use_overload_checker_in_cumulative} to its default value. */
    public Builder clearUseOverloadCheckerInCumulative() {
      useOverloadCheckerInCumulative_ = false;
      hasUseOverloadCheckerInCumulative_ = false;
      return this;
    }

    /** Sets {@code use_conservative_scale_overload_checker}. */
    public Builder setUseConservativeScaleOverloadChecker(boolean value) {
      useConservativeScaleOverloadChecker_ = value;
      hasUseConservativeScaleOverloadChecker_ = true;
      return this;
    }

    /** Resets {@code use_conservative_scale_overload_checker} to its default value. */
    public Builder clearUseConservativeScaleOverloadChecker() {
      useConservativeScaleOverloadChecker_ = false;
      hasUseConservativeScaleOverloadChecker_ = false;
      return this;
    }

    /** Sets {@code use_timetable_edge_finding_in_cumulative}. */
    public Builder setUseTimetableEdgeFindingInCumulative(boolean value) {
      useTimetableEdgeFindingInCumulative_ = value;
      hasUseTimetableEdgeFindingInCumulative_ = true;
      return this;
    }

    /** Resets {@code use_timetable_edge_finding_in_cumulative} to its default value. */
    public Builder clearUseTimetableEdgeFindingInCumulative() {
      useTimetableEdgeFindingInCumulative_ = false;
      hasUseTimetableEdgeFindingInCumulative_ = false;
      return this;
    }

    /** Sets {@code max_num_intervals_for_timetable_edge_finding}. */
    public Builder setMaxNumIntervalsForTimetableEdgeFinding(int value) {
      maxNumIntervalsForTimetableEdgeFinding_ = value;
      hasMaxNumIntervalsForTimetableEdgeFinding_ = true;
      return this;
    }

    /** Resets {@code max_num_intervals_for_timetable_edge_finding} to its default value. */
    public Builder clearMaxNumIntervalsForTimetableEdgeFinding() {
      maxNumIntervalsForTimetableEdgeFinding_ = 100;
      hasMaxNumIntervalsForTimetableEdgeFinding_ = false;
      return this;
    }

    /** Sets {@code use_hard_precedences_in_cumulative}. */
    public Builder setUseHardPrecedencesInCumulative(boolean value) {
      useHardPrecedencesInCumulative_ = value;
      hasUseHardPrecedencesInCumulative_ = true;
      return this;
    }

    /** Resets {@code use_hard_precedences_in_cumulative} to its default value. */
    public Builder clearUseHardPrecedencesInCumulative() {
      useHardPrecedencesInCumulative_ = false;
      hasUseHardPrecedencesInCumulative_ = false;
      return this;
    }

    /** Sets {@code exploit_all_precedences}. */
    public Builder setExploitAllPrecedences(boolean value) {
      exploitAllPrecedences_ = value;
      hasExploitAllPrecedences_ = true;
      return this;
    }

    /** Resets {@code exploit_all_precedences} to its default value. */
    public Builder clearExploitAllPrecedences() {
      exploitAllPrecedences_ = false;
      hasExploitAllPrecedences_ = false;
      return this;
    }

    /** Sets {@code use_disjunctive_constraint_in_cumulative}. */
    public Builder setUseDisjunctiveConstraintInCumulative(boolean value) {
      useDisjunctiveConstraintInCumulative_ = value;
      hasUseDisjunctiveConstraintInCumulative_ = true;
      return this;
    }

    /** Resets {@code use_disjunctive_constraint_in_cumulative} to its default value. */
    public Builder clearUseDisjunctiveConstraintInCumulative() {
      useDisjunctiveConstraintInCumulative_ = true;
      hasUseDisjunctiveConstraintInCumulative_ = false;
      return this;
    }

    /** Sets {@code no_overlap_2d_boolean_relations_limit}. */
    public Builder setNoOverlap2DBooleanRelationsLimit(int value) {
      noOverlap2DBooleanRelationsLimit_ = value;
      hasNoOverlap2DBooleanRelationsLimit_ = true;
      return this;
    }

    /** Resets {@code no_overlap_2d_boolean_relations_limit} to its default value. */
    public Builder clearNoOverlap2DBooleanRelationsLimit() {
      noOverlap2DBooleanRelationsLimit_ = 10;
      hasNoOverlap2DBooleanRelationsLimit_ = false;
      return this;
    }

    /** Sets {@code use_timetabling_in_no_overlap_2d}. */
    public Builder setUseTimetablingInNoOverlap2D(boolean value) {
      useTimetablingInNoOverlap2D_ = value;
      hasUseTimetablingInNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code use_timetabling_in_no_overlap_2d} to its default value. */
    public Builder clearUseTimetablingInNoOverlap2D() {
      useTimetablingInNoOverlap2D_ = false;
      hasUseTimetablingInNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code use_energetic_reasoning_in_no_overlap_2d}. */
    public Builder setUseEnergeticReasoningInNoOverlap2D(boolean value) {
      useEnergeticReasoningInNoOverlap2D_ = value;
      hasUseEnergeticReasoningInNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code use_energetic_reasoning_in_no_overlap_2d} to its default value. */
    public Builder clearUseEnergeticReasoningInNoOverlap2D() {
      useEnergeticReasoningInNoOverlap2D_ = false;
      hasUseEnergeticReasoningInNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code use_area_energetic_reasoning_in_no_overlap_2d}. */
    public Builder setUseAreaEnergeticReasoningInNoOverlap2D(boolean value) {
      useAreaEnergeticReasoningInNoOverlap2D_ = value;
      hasUseAreaEnergeticReasoningInNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code use_area_energetic_reasoning_in_no_overlap_2d} to its default value. */
    public Builder clearUseAreaEnergeticReasoningInNoOverlap2D() {
      useAreaEnergeticReasoningInNoOverlap2D_ = false;
      hasUseAreaEnergeticReasoningInNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code use_try_edge_reasoning_in_no_overlap_2d}. */
    public Builder setUseTryEdgeReasoningInNoOverlap2D(boolean value) {
      useTryEdgeReasoningInNoOverlap2D_ = value;
      hasUseTryEdgeReasoningInNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code use_try_edge_reasoning_in_no_overlap_2d} to its default value. */
    public Builder clearUseTryEdgeReasoningInNoOverlap2D() {
      useTryEdgeReasoningInNoOverlap2D_ = false;
      hasUseTryEdgeReasoningInNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code max_pairs_pairwise_reasoning_in_no_overlap_2d}. */
    public Builder setMaxPairsPairwiseReasoningInNoOverlap2D(int value) {
      maxPairsPairwiseReasoningInNoOverlap2D_ = value;
      hasMaxPairsPairwiseReasoningInNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code max_pairs_pairwise_reasoning_in_no_overlap_2d} to its default value. */
    public Builder clearMaxPairsPairwiseReasoningInNoOverlap2D() {
      maxPairsPairwiseReasoningInNoOverlap2D_ = 1250;
      hasMaxPairsPairwiseReasoningInNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code maximum_regions_to_split_in_disconnected_no_overlap_2d}. */
    public Builder setMaximumRegionsToSplitInDisconnectedNoOverlap2D(int value) {
      maximumRegionsToSplitInDisconnectedNoOverlap2D_ = value;
      hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_ = true;
      return this;
    }

    /** Resets {@code maximum_regions_to_split_in_disconnected_no_overlap_2d} to its default value. */
    public Builder clearMaximumRegionsToSplitInDisconnectedNoOverlap2D() {
      maximumRegionsToSplitInDisconnectedNoOverlap2D_ = 0;
      hasMaximumRegionsToSplitInDisconnectedNoOverlap2D_ = false;
      return this;
    }

    /** Sets {@code use_linear3_for_no_overlap_2d_precedences}. */
    public Builder setUseLinear3ForNoOverlap2DPrecedences(boolean value) {
      useLinear3ForNoOverlap2DPrecedences_ = value;
      hasUseLinear3ForNoOverlap2DPrecedences_ = true;
      return this;
    }

    /** Resets {@code use_linear3_for_no_overlap_2d_precedences} to its default value. */
    public Builder clearUseLinear3ForNoOverlap2DPrecedences() {
      useLinear3ForNoOverlap2DPrecedences_ = true;
      hasUseLinear3ForNoOverlap2DPrecedences_ = false;
      return this;
    }

    /** Sets {@code use_dual_scheduling_heuristics}. */
    public Builder setUseDualSchedulingHeuristics(boolean value) {
      useDualSchedulingHeuristics_ = value;
      hasUseDualSchedulingHeuristics_ = true;
      return this;
    }

    /** Resets {@code use_dual_scheduling_heuristics} to its default value. */
    public Builder clearUseDualSchedulingHeuristics() {
      useDualSchedulingHeuristics_ = true;
      hasUseDualSchedulingHeuristics_ = false;
      return this;
    }

    /** Sets {@code use_all_different_for_circuit}. */
    public Builder setUseAllDifferentForCircuit(boolean value) {
      useAllDifferentForCircuit_ = value;
      hasUseAllDifferentForCircuit_ = true;
      return this;
    }

    /** Resets {@code use_all_different_for_circuit} to its default value. */
    public Builder clearUseAllDifferentForCircuit() {
      useAllDifferentForCircuit_ = false;
      hasUseAllDifferentForCircuit_ = false;
      return this;
    }

    /** Sets {@code routing_cut_subset_size_for_binary_relation_bound}. */
    public Builder setRoutingCutSubsetSizeForBinaryRelationBound(int value) {
      routingCutSubsetSizeForBinaryRelationBound_ = value;
      hasRoutingCutSubsetSizeForBinaryRelationBound_ = true;
      return this;
    }

    /** Resets {@code routing_cut_subset_size_for_binary_relation_bound} to its default value. */
    public Builder clearRoutingCutSubsetSizeForBinaryRelationBound() {
      routingCutSubsetSizeForBinaryRelationBound_ = 0;
      hasRoutingCutSubsetSizeForBinaryRelationBound_ = false;
      return this;
    }

    /** Sets {@code routing_cut_subset_size_for_tight_binary_relation_bound}. */
    public Builder setRoutingCutSubsetSizeForTightBinaryRelationBound(int value) {
      routingCutSubsetSizeForTightBinaryRelationBound_ = value;
      hasRoutingCutSubsetSizeForTightBinaryRelationBound_ = true;
      return this;
    }

    /** Resets {@code routing_cut_subset_size_for_tight_binary_relation_bound} to its default value. */
    public Builder clearRoutingCutSubsetSizeForTightBinaryRelationBound() {
      routingCutSubsetSizeForTightBinaryRelationBound_ = 0;
      hasRoutingCutSubsetSizeForTightBinaryRelationBound_ = false;
      return this;
    }

    /** Sets {@code routing_cut_subset_size_for_exact_binary_relation_bound}. */
    public Builder setRoutingCutSubsetSizeForExactBinaryRelationBound(int value) {
      routingCutSubsetSizeForExactBinaryRelationBound_ = value;
      hasRoutingCutSubsetSizeForExactBinaryRelationBound_ = true;
      return this;
    }

    /** Resets {@code routing_cut_subset_size_for_exact_binary_relation_bound} to its default value. */
    public Builder clearRoutingCutSubsetSizeForExactBinaryRelationBound() {
      routingCutSubsetSizeForExactBinaryRelationBound_ = 8;
      hasRoutingCutSubsetSizeForExactBinaryRelationBound_ = false;
      return this;
    }

    /** Sets {@code routing_cut_subset_size_for_shortest_paths_bound}. */
    public Builder setRoutingCutSubsetSizeForShortestPathsBound(int value) {
      routingCutSubsetSizeForShortestPathsBound_ = value;
      hasRoutingCutSubsetSizeForShortestPathsBound_ = true;
      return this;
    }

    /** Resets {@code routing_cut_subset_size_for_shortest_paths_bound} to its default value. */
    public Builder clearRoutingCutSubsetSizeForShortestPathsBound() {
      routingCutSubsetSizeForShortestPathsBound_ = 8;
      hasRoutingCutSubsetSizeForShortestPathsBound_ = false;
      return this;
    }

    /** Sets {@code routing_cut_dp_effort}. */
    public Builder setRoutingCutDpEffort(double value) {
      routingCutDpEffort_ = value;
      hasRoutingCutDpEffort_ = true;
      return this;
    }

    /** Resets {@code routing_cut_dp_effort} to its default value. */
    public Builder clearRoutingCutDpEffort() {
      routingCutDpEffort_ = 1e7;
      hasRoutingCutDpEffort_ = false;
      return this;
    }

    /** Sets {@code routing_cut_max_infeasible_path_length}. */
    public Builder setRoutingCutMaxInfeasiblePathLength(int value) {
      routingCutMaxInfeasiblePathLength_ = value;
      hasRoutingCutMaxInfeasiblePathLength_ = true;
      return this;
    }

    /** Resets {@code routing_cut_max_infeasible_path_length} to its default value. */
    public Builder clearRoutingCutMaxInfeasiblePathLength() {
      routingCutMaxInfeasiblePathLength_ = 6;
      hasRoutingCutMaxInfeasiblePathLength_ = false;
      return this;
    }

    /** Sets {@code search_branching}. */
    public Builder setSearchBranching(SatParameters.SearchBranching value) {
      if (value == null) {
        throw new NullPointerException();
      }
      searchBranching_ = value.getNumber();
      hasSearchBranching_ = true;
      return this;
    }

    /** Sets the number of {@code search_branching}. */
    public Builder setSearchBranchingValue(int value) {
      searchBranching_ = value;
      hasSearchBranching_ = true;
      return this;
    }

    /** Resets {@code search_branching} to its default value. */
    public Builder clearSearchBranching() {
      searchBranching_ = 0;
      hasSearchBranching_ = false;
      return this;
    }

    /** Sets {@code hint_conflict_limit}. */
    public Builder setHintConflictLimit(int value) {
      hintConflictLimit_ = value;
      hasHintConflictLimit_ = true;
      return this;
    }

    /** Resets {@code hint_conflict_limit} to its default value. */
    public Builder clearHintConflictLimit() {
      hintConflictLimit_ = 10;
      hasHintConflictLimit_ = false;
      return this;
    }

    /** Sets {@code try_hint_as_assumptions}. */
    public Builder setTryHintAsAssumptions(boolean value) {
      tryHintAsAssumptions_ = value;
      hasTryHintAsAssumptions_ = true;
      return this;
    }

    /** Resets {@code try_hint_as_assumptions} to its default value. */
    public Builder clearTryHintAsAssumptions() {
      tryHintAsAssumptions_ = true;
      hasTryHintAsAssumptions_ = false;
      return this;
    }

    /** Sets {@code repair_hint}. */
    public Builder setRepairHint(boolean value) {
      repairHint_ = value;
      hasRepairHint_ = true;
      return this;
    }

    /** Resets {@code repair_hint} to its default value. */
    public Builder clearRepairHint() {
      repairHint_ = false;
      hasRepairHint_ = false;
      return this;
    }

    /** Sets {@code fix_variables_to_their_hinted_value}. */
    public Builder setFixVariablesToTheirHintedValue(boolean value) {
      fixVariablesToTheirHintedValue_ = value;
      hasFixVariablesToTheirHintedValue_ = true;
      return this;
    }

    /** Resets {@code fix_variables_to_their_hinted_value} to its default value. */
    public Builder clearFixVariablesToTheirHintedValue() {
      fixVariablesToTheirHintedValue_ = false;
      hasFixVariablesToTheirHintedValue_ = false;
      return this;
    }

    /** Sets {@code use_probing_search}. */
    public Builder setUseProbingSearch(boolean value) {
      useProbingSearch_ = value;
      hasUseProbingSearch_ = true;
      return this;
    }

    /** Resets {@code use_probing_search} to its default value. */
    public Builder clearUseProbingSearch() {
      useProbingSearch_ = false;
      hasUseProbingSearch_ = false;
      return this;
    }

    /** Sets {@code use_extended_probing}. */
    public Builder setUseExtendedProbing(boolean value) {
      useExtendedProbing_ = value;
      hasUseExtendedProbing_ = true;
      return this;
    }

    /** Resets {@code use_extended_probing} to its default value. */
    public Builder clearUseExtendedProbing() {
      useExtendedProbing_ = true;
      hasUseExtendedProbing_ = false;
      return this;
    }

    /** Sets {@code probing_num_combinations_limit}. */
    public Builder setProbingNumCombinationsLimit(int value) {
      probingNumCombinationsLimit_ = value;
      hasProbingNumCombinationsLimit_ = true;
      return this;
    }

    /** Resets {@code probing_num_combinations_limit} to its default value. */
    public Builder clearProbingNumCombinationsLimit() {
      probingNumCombinationsLimit_ = 20000;
      hasProbingNumCombinationsLimit_ = false;
      return this;
    }

    /** Sets {@code shaving_deterministic_time_in_probing_search}. */
    public Builder setShavingDeterministicTimeInProbingSearch(double value) {
      shavingDeterministicTimeInProbingSearch_ = value;
      hasShavingDeterministicTimeInProbingSearch_ = true;
      return this;
    }

    /** Resets {@code shaving_deterministic_time_in_probing_search} to its default value. */
    public Builder clearShavingDeterministicTimeInProbingSearch() {
      shavingDeterministicTimeInProbingSearch_ = 0.05;
      hasShavingDeterministicTimeInProbingSearch_ = false;
      return this;
    }

    /** Sets {@code shaving_search_deterministic_time}. */
    public Builder setShavingSearchDeterministicTime(double value) {
      shavingSearchDeterministicTime_ = value;
      hasShavingSearchDeterministicTime_ = true;
      return this;
    }

    /** Resets {@code shaving_search_deterministic_time} to its default value. */
    public Builder clearShavingSearchDeterministicTime() {
      shavingSearchDeterministicTime_ = 0.1;
      hasShavingSearchDeterministicTime_ = false;
      return this;
    }

    /** Sets {@code shaving_search_use_random_search}. */
    public Builder setShavingSearchUseRandomSearch(boolean value) {
      shavingSearchUseRandomSearch_ = value;
      hasShavingSearchUseRandomSearch_ = true;
      return this;
    }

    /** Resets {@code shaving_search_use_random_search} to its default value. */
    public Builder clearShavingSearchUseRandomSearch() {
      shavingSearchUseRandomSearch_ = false;
      hasShavingSearchUseRandomSearch_ = false;
      return this;
    }

    /** Sets {@code shaving_search_restart_limit}. */
    public Builder setShavingSearchRestartLimit(int value) {
      shavingSearchRestartLimit_ = value;
      hasShavingSearchRestartLimit_ = true;
      return this;
    }

    /** Resets {@code shaving_search_restart_limit} to its default value. */
    public Builder clearShavingSearchRestartLimit() {
      shavingSearchRestartLimit_ = 10;
      hasShavingSearchRestartLimit_ = false;
      return this;
    }

    /** Sets {@code shaving_search_threshold}. */
    public Builder setShavingSearchThreshold(long value) {
      shavingSearchThreshold_ = value;
      hasShavingSearchThreshold_ = true;
      return this;
    }

    /** Resets {@code shaving_search_threshold} to its default value. */
    public Builder clearShavingSearchThreshold() {
      shavingSearchThreshold_ = 64L;
      hasShavingSearchThreshold_ = false;
      return this;
    }

    /** Sets {@code objective_shaving_search_threshold}. */
    public Builder setObjectiveShavingSearchThreshold(long value) {
      objectiveShavingSearchThreshold_ = value;
      hasObjectiveShavingSearchThreshold_ = true;
      return this;
    }

    /** Resets {@code objective_shaving_search_threshold} to its default value. */
    public Builder clearObjectiveShavingSearchThreshold() {
      objectiveShavingSearchThreshold_ = 1000L;
      hasObjectiveShavingSearchThreshold_ = false;
      return this;
    }

    /** Sets {@code use_objective_lb_search}. */
    public Builder setUseObjectiveLbSearch(boolean value) {
      useObjectiveLbSearch_ = value;
      hasUseObjectiveLbSearch_ = true;
      return this;
    }

    /** Resets {@code use_objective_lb_search} to its default value. */
    public Builder clearUseObjectiveLbSearch() {
      useObjectiveLbSearch_ = false;
      hasUseObjectiveLbSearch_ = false;
      return this;
    }

    /** Sets {@code use_objective_shaving_search}. */
    public Builder setUseObjectiveShavingSearch(boolean value) {
      useObjectiveShavingSearch_ = value;
      hasUseObjectiveShavingSearch_ = true;
      return this;
    }

    /** Resets {@code use_objective_shaving_search} to its default value. */
    public Builder clearUseObjectiveShavingSearch() {
      useObjectiveShavingSearch_ = false;
      hasUseObjectiveShavingSearch_ = false;
      return this;
    }

    /** Sets {@code variables_shaving_level}. */
    public Builder setVariablesShavingLevel(int value) {
      variablesShavingLevel_ = value;
      hasVariablesShavingLevel_ = true;
      return this;
    }

    /** Resets {@code variables_shaving_level} to its default value. */
    public Builder clearVariablesShavingLevel() {
      variablesShavingLevel_ = -1;
      hasVariablesShavingLevel_ = false;
      return this;
    }

    /** Sets {@code pseudo_cost_reliability_threshold}. */
    public Builder setPseudoCostReliabilityThreshold(long value) {
      pseudoCostReliabilityThreshold_ = value;
      hasPseudoCostReliabilityThreshold_ = true;
      return this;
    }

    /** Resets {@code pseudo_cost_reliability_threshold} to its default value. */
    public Builder clearPseudoCostReliabilityThreshold() {
      pseudoCostReliabilityThreshold_ = 100L;
      hasPseudoCostReliabilityThreshold_ = false;
      return this;
    }

    /** Sets {@code optimize_with_core}. */
    public Builder setOptimizeWithCore(boolean value) {
      optimizeWithCore_ = value;
      hasOptimizeWithCore_ = true;
      return this;
    }

    /** Resets {@code optimize_with_core} to its default value. */
    public Builder clearOptimizeWithCore() {
      optimizeWithCore_ = false;
      hasOptimizeWithCore_ = false;
      return this;
    }

    /** Sets {@code optimize_with_lb_tree_search}. */
    public Builder setOptimizeWithLbTreeSearch(boolean value) {
      optimizeWithLbTreeSearch_ = value;
      hasOptimizeWithLbTreeSearch_ = true;
      return this;
    }

    /** Resets {@code optimize_with_lb_tree_search} to its default value. */
    public Builder clearOptimizeWithLbTreeSearch() {
      optimizeWithLbTreeSearch_ = false;
      hasOptimizeWithLbTreeSearch_ = false;
      return this;
    }

    /** Sets {@code save_lp_basis_in_lb_tree_search}. */
    public Builder setSaveLpBasisInLbTreeSearch(boolean value) {
      saveLpBasisInLbTreeSearch_ = value;
      hasSaveLpBasisInLbTreeSearch_ = true;
      return this;
    }

    /** Resets {@code save_lp_basis_in_lb_tree_search} to its default value. */
    public Builder clearSaveLpBasisInLbTreeSearch() {
      saveLpBasisInLbTreeSearch_ = false;
      hasSaveLpBasisInLbTreeSearch_ = false;
      return this;
    }

    /** Sets {@code binary_search_num_conflicts}. */
    public Builder setBinarySearchNumConflicts(int value) {
      binarySearchNumConflicts_ = value;
      hasBinarySearchNumConflicts_ = true;
      return this;
    }

    /** Resets {@code binary_search_num_conflicts} to its default value. */
    public Builder clearBinarySearchNumConflicts() {
      binarySearchNumConflicts_ = -1;
      hasBinarySearchNumConflicts_ = false;
      return this;
    }

    /** Sets {@code optimize_with_max_hs}. */
    public Builder setOptimizeWithMaxHs(boolean value) {
      optimizeWithMaxHs_ = value;
      hasOptimizeWithMaxHs_ = true;
      return this;
    }

    /** Resets {@code optimize_with_max_hs} to its default value. */
    public Builder clearOptimizeWithMaxHs() {
      optimizeWithMaxHs_ = false;
      hasOptimizeWithMaxHs_ = false;
      return this;
    }

    /** Sets {@code use_feasibility_jump}. */
    public Builder setUseFeasibilityJump(boolean value) {
      useFeasibilityJump_ = value;
      hasUseFeasibilityJump_ = true;
      return this;
    }

    /** Resets {@code use_feasibility_jump} to its default value. */
    public Builder clearUseFeasibilityJump() {
      useFeasibilityJump_ = true;
      hasUseFeasibilityJump_ = false;
      return this;
    }

    /** Sets {@code use_ls_only}. */
    public Builder setUseLsOnly(boolean value) {
      useLsOnly_ = value;
      hasUseLsOnly_ = true;
      return this;
    }

    /** Resets {@code use_ls_only} to its default value. */
    public Builder clearUseLsOnly() {
      useLsOnly_ = false;
      hasUseLsOnly_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_decay}. */
    public Builder setFeasibilityJumpDecay(double value) {
      feasibilityJumpDecay_ = value;
      hasFeasibilityJumpDecay_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_decay} to its default value. */
    public Builder clearFeasibilityJumpDecay() {
      feasibilityJumpDecay_ = 0.95;
      hasFeasibilityJumpDecay_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_linearization_level}. */
    public Builder setFeasibilityJumpLinearizationLevel(int value) {
      feasibilityJumpLinearizationLevel_ = value;
      hasFeasibilityJumpLinearizationLevel_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_linearization_level} to its default value. */
    public Builder clearFeasibilityJumpLinearizationLevel() {
      feasibilityJumpLinearizationLevel_ = 2;
      hasFeasibilityJumpLinearizationLevel_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_restart_factor}. */
    public Builder setFeasibilityJumpRestartFactor(int value) {
      feasibilityJumpRestartFactor_ = value;
      hasFeasibilityJumpRestartFactor_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_restart_factor} to its default value. */
    public Builder clearFeasibilityJumpRestartFactor() {
      feasibilityJumpRestartFactor_ = 1;
      hasFeasibilityJumpRestartFactor_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_batch_dtime}. */
    public Builder setFeasibilityJumpBatchDtime(double value) {
      feasibilityJumpBatchDtime_ = value;
      hasFeasibilityJumpBatchDtime_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_batch_dtime} to its default value. */
    public Builder clearFeasibilityJumpBatchDtime() {
      feasibilityJumpBatchDtime_ = 0.1;
      hasFeasibilityJumpBatchDtime_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_var_randomization_probability}. */
    public Builder setFeasibilityJumpVarRandomizationProbability(double value) {
      feasibilityJumpVarRandomizationProbability_ = value;
      hasFeasibilityJumpVarRandomizationProbability_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_var_randomization_probability} to its default value. */
    public Builder clearFeasibilityJumpVarRandomizationProbability() {
      feasibilityJumpVarRandomizationProbability_ = 0.05;
      hasFeasibilityJumpVarRandomizationProbability_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_var_perburbation_range_ratio}. */
    public Builder setFeasibilityJumpVarPerburbationRangeRatio(double value) {
      feasibilityJumpVarPerburbationRangeRatio_ = value;
      hasFeasibilityJumpVarPerburbationRangeRatio_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_var_perburbation_range_ratio} to its default value. */
    public Builder clearFeasibilityJumpVarPerburbationRangeRatio() {
      feasibilityJumpVarPerburbationRangeRatio_ = 0.2;
      hasFeasibilityJumpVarPerburbationRangeRatio_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_enable_restarts}. */
    public Builder setFeasibilityJumpEnableRestarts(boolean value) {
      feasibilityJumpEnableRestarts_ = value;
      hasFeasibilityJumpEnableRestarts_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_enable_restarts} to its default value. */
    public Builder clearFeasibilityJumpEnableRestarts() {
      feasibilityJumpEnableRestarts_ = true;
      hasFeasibilityJumpEnableRestarts_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_max_expanded_constraint_size}. */
    public Builder setFeasibilityJumpMaxExpandedConstraintSize(int value) {
      feasibilityJumpMaxExpandedConstraintSize_ = value;
      hasFeasibilityJumpMaxExpandedConstraintSize_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_max_expanded_constraint_size} to its default value. */
    public Builder clearFeasibilityJumpMaxExpandedConstraintSize() {
      feasibilityJumpMaxExpandedConstraintSize_ = 500;
      hasFeasibilityJumpMaxExpandedConstraintSize_ = false;
      return this;
    }

    /** Sets {@code feasibility_jump_ignore_redundant_constraints}. */
    public Builder setFeasibilityJumpIgnoreRedundantConstraints(boolean value) {
      feasibilityJumpIgnoreRedundantConstraints_ = value;
      hasFeasibilityJumpIgnoreRedundantConstraints_ = true;
      return this;
    }

    /** Resets {@code feasibility_jump_ignore_redundant_constraints} to its default value. */
    public Builder clearFeasibilityJumpIgnoreRedundantConstraints() {
      feasibilityJumpIgnoreRedundantConstraints_ = true;
      hasFeasibilityJumpIgnoreRedundantConstraints_ = false;
      return this;
    }

    /** Sets {@code lns_ignore_redundant_constraints}. */
    public Builder setLnsIgnoreRedundantConstraints(boolean value) {
      lnsIgnoreRedundantConstraints_ = value;
      hasLnsIgnoreRedundantConstraints_ = true;
      return this;
    }

    /** Resets {@code lns_ignore_redundant_constraints} to its default value. */
    public Builder clearLnsIgnoreRedundantConstraints() {
      lnsIgnoreRedundantConstraints_ = true;
      hasLnsIgnoreRedundantConstraints_ = false;
      return this;
    }

    /** Sets {@code num_violation_ls}. */
    public Builder setNumViolationLs(int value) {
      numViolationLs_ = value;
      hasNumViolationLs_ = true;
      return this;
    }

    /** Resets {@code num_violation_ls} to its default value. */
    public Builder clearNumViolationLs() {
      numViolationLs_ = 0;
      hasNumViolationLs_ = false;
      return this;
    }

    /** Sets {@code violation_ls_perturbation_period}. */
    public Builder setViolationLsPerturbationPeriod(int value) {
      violationLsPerturbationPeriod_ = value;
      hasViolationLsPerturbationPeriod_ = true;
      return this;
    }

    /** Resets {@code violation_ls_perturbation_period} to its default value. */
    public Builder clearViolationLsPerturbationPeriod() {
      violationLsPerturbationPeriod_ = 100;
      hasViolationLsPerturbationPeriod_ = false;
      return this;
    }

    /** Sets {@code shared_tree_num_workers}. */
    public Builder setSharedTreeNumWorkers(int value) {
      sharedTreeNumWorkers_ = value;
      hasSharedTreeNumWorkers_ = true;
      return this;
    }

    /** Resets {@code shared_tree_num_workers} to its default value. */
    public Builder clearSharedTreeNumWorkers() {
      sharedTreeNumWorkers_ = -1;
      hasSharedTreeNumWorkers_ = false;
      return this;
    }

    /** Sets {@code use_shared_tree_search}. */
    public Builder setUseSharedTreeSearch(boolean value) {
      useSharedTreeSearch_ = value;
      hasUseSharedTreeSearch_ = true;
      return this;
    }

    /** Resets {@code use_shared_tree_search} to its default value. */
    public Builder clearUseSharedTreeSearch() {
      useSharedTreeSearch_ = false;
      hasUseSharedTreeSearch_ = false;
      return this;
    }

    /** Sets {@code shared_tree_worker_min_restarts_per_subtree}. */
    public Builder setSharedTreeWorkerMinRestartsPerSubtree(int value) {
      sharedTreeWorkerMinRestartsPerSubtree_ = value;
      hasSharedTreeWorkerMinRestartsPerSubtree_ = true;
      return this;
    }

    /** Resets {@code shared_tree_worker_min_restarts_per_subtree} to its default value. */
    public Builder clearSharedTreeWorkerMinRestartsPerSubtree() {
      sharedTreeWorkerMinRestartsPerSubtree_ = 1;
      hasSharedTreeWorkerMinRestartsPerSubtree_ = false;
      return this;
    }

    /** Sets {@code shared_tree_worker_enable_trail_sharing}. */
    public Builder setSharedTreeWorkerEnableTrailSharing(boolean value) {
      sharedTreeWorkerEnableTrailSharing_ = value;
      hasSharedTreeWorkerEnableTrailSharing_ = true;
      return this;
    }

    /** Resets {@code shared_tree_worker_enable_trail_sharing} to its default value. */
    public Builder clearSharedTreeWorkerEnableTrailSharing() {
      sharedTreeWorkerEnableTrailSharing_ = true;
      hasSharedTreeWorkerEnableTrailSharing_ = false;
      return this;
    }

    /** Sets {@code shared_tree_worker_enable_phase_sharing}. */
    public Builder setSharedTreeWorkerEnablePhaseSharing(boolean value) {
      sharedTreeWorkerEnablePhaseSharing_ = value;
      hasSharedTreeWorkerEnablePhaseSharing_ = true;
      return this;
    }

    /** Resets {@code shared_tree_worker_enable_phase_sharing} to its default value. */
    public Builder clearSharedTreeWorkerEnablePhaseSharing() {
      sharedTreeWorkerEnablePhaseSharing_ = true;
      hasSharedTreeWorkerEnablePhaseSharing_ = false;
      return this;
    }

    /** Sets {@code shared_tree_worker_propagate_intermediate_nodes}. */
    public Builder setSharedTreeWorkerPropagateIntermediateNodes(boolean value) {
      sharedTreeWorkerPropagateIntermediateNodes_ = value;
      hasSharedTreeWorkerPropagateIntermediateNodes_ = true;
      return this;
    }

    /** Resets {@code shared_tree_worker_propagate_intermediate_nodes} to its default value. */
    public Builder clearSharedTreeWorkerPropagateIntermediateNodes() {
      sharedTreeWorkerPropagateIntermediateNodes_ = false;
      hasSharedTreeWorkerPropagateIntermediateNodes_ = false;
      return this;
    }

    /** Sets {@code shared_tree_open_leaves_per_worker}. */
    public Builder setSharedTreeOpenLeavesPerWorker(double value) {
      sharedTreeOpenLeavesPerWorker_ = value;
      hasSharedTreeOpenLeavesPerWorker_ = true;
      return this;
    }

    /** Resets {@code shared_tree_open_leaves_per_worker} to its default value. */
    public Builder clearSharedTreeOpenLeavesPerWorker() {
      sharedTreeOpenLeavesPerWorker_ = 2.0;
      hasSharedTreeOpenLeavesPerWorker_ = false;
      return this;
    }

    /** Sets {@code shared_tree_max_nodes_per_worker}. */
    public Builder setSharedTreeMaxNodesPerWorker(int value) {
      sharedTreeMaxNodesPerWorker_ = value;
      hasSharedTreeMaxNodesPerWorker_ = true;
      return this;
    }

    /** Resets {@code shared_tree_max_nodes_per_worker} to its default value. */
    public Builder clearSharedTreeMaxNodesPerWorker() {
      sharedTreeMaxNodesPerWorker_ = 10000;
      hasSharedTreeMaxNodesPerWorker_ = false;
      return this;
    }

    /** Sets {@code shared_tree_split_strategy}. */
    public Builder setSharedTreeSplitStrategy(SatParameters.SharedTreeSplitStrategy value) {
      if (value == null) {
        throw new NullPointerException();
      }
      sharedTreeSplitStrategy_ = value.getNumber();
      hasSharedTreeSplitStrategy_ = true;
      return this;
    }

    /** Sets the number of {@code shared_tree_split_strategy}. */
    public Builder setSharedTreeSplitStrategyValue(int value) {
      sharedTreeSplitStrategy_ = value;
      hasSharedTreeSplitStrategy_ = true;
      return this;
    }

    /** Resets {@code shared_tree_split_strategy} to its default value. */
    public Builder clearSharedTreeSplitStrategy() {
      sharedTreeSplitStrategy_ = 0;
      hasSharedTreeSplitStrategy_ = false;
      return this;
    }

    /** Sets {@code shared_tree_balance_tolerance}. */
    public Builder setSharedTreeBalanceTolerance(int value) {
      sharedTreeBalanceTolerance_ = value;
      hasSharedTreeBalanceTolerance_ = true;
      return this;
    }

    /** Resets {@code shared_tree_balance_tolerance} to its default value. */
    public Builder clearSharedTreeBalanceTolerance() {
      sharedTreeBalanceTolerance_ = 1;
      hasSharedTreeBalanceTolerance_ = false;
      return this;
    }

    /** Sets {@code shared_tree_split_min_dtime}. */
    public Builder setSharedTreeSplitMinDtime(double value) {
      sharedTreeSplitMinDtime_ = value;
      hasSharedTreeSplitMinDtime_ = true;
      return this;
    }

    /** Resets {@code shared_tree_split_min_dtime} to its default value. */
    public Builder clearSharedTreeSplitMinDtime() {
      sharedTreeSplitMinDtime_ = 0.1;
      hasSharedTreeSplitMinDtime_ = false;
      return this;
    }

    /** Sets {@code enumerate_all_solutions}. */
    public Builder setEnumerateAllSolutions(boolean value) {
      enumerateAllSolutions_ = value;
      hasEnumerateAllSolutions_ = true;
      return this;
    }

    /** Resets {@code enumerate_all_solutions} to its default value. */
    public Builder clearEnumerateAllSolutions() {
      enumerateAllSolutions_ = false;
      hasEnumerateAllSolutions_ = false;
      return this;
    }

    /** Sets {@code keep_all_feasible_solutions_in_presolve}. */
    public Builder setKeepAllFeasibleSolutionsInPresolve(boolean value) {
      keepAllFeasibleSolutionsInPresolve_ = value;
      hasKeepAllFeasibleSolutionsInPresolve_ = true;
      return this;
    }

    /** Resets {@code keep_all_feasible_solutions_in_presolve} to its default value. */
    public Builder clearKeepAllFeasibleSolutionsInPresolve() {
      keepAllFeasibleSolutionsInPresolve_ = false;
      hasKeepAllFeasibleSolutionsInPresolve_ = false;
      return this;
    }

    /** Sets {@code fill_tightened_domains_in_response}. */
    public Builder setFillTightenedDomainsInResponse(boolean value) {
      fillTightenedDomainsInResponse_ = value;
      hasFillTightenedDomainsInResponse_ = true;
      return this;
    }

    /** Resets {@code fill_tightened_domains_in_response} to its default value. */
    public Builder clearFillTightenedDomainsInResponse() {
      fillTightenedDomainsInResponse_ = false;
      hasFillTightenedDomainsInResponse_ = false;
      return this;
    }

    /** Sets {@code fill_additional_solutions_in_response}. */
    public Builder setFillAdditionalSolutionsInResponse(boolean value) {
      fillAdditionalSolutionsInResponse_ = value;
      hasFillAdditionalSolutionsInResponse_ = true;
      return this;
    }

    /** Resets {@code fill_additional_solutions_in_response} to its default value. */
    public Builder clearFillAdditionalSolutionsInResponse() {
      fillAdditionalSolutionsInResponse_ = false;
      hasFillAdditionalSolutionsInResponse_ = false;
      return this;
    }

    /** Sets {@code instantiate_all_variables}. */
    public Builder setInstantiateAllVariables(boolean value) {
      instantiateAllVariables_ = value;
      hasInstantiateAllVariables_ = true;
      return this;
    }

    /** Resets {@code instantiate_all_variables} to its default value. */
    public Builder clearInstantiateAllVariables() {
      instantiateAllVariables_ = true;
      hasInstantiateAllVariables_ = false;
      return this;
    }

    /** Sets {@code auto_detect_greater_than_at_least_one_of}. */
    public Builder setAutoDetectGreaterThanAtLeastOneOf(boolean value) {
      autoDetectGreaterThanAtLeastOneOf_ = value;
      hasAutoDetectGreaterThanAtLeastOneOf_ = true;
      return this;
    }

    /** Resets {@code auto_detect_greater_than_at_least_one_of} to its default value. */
    public Builder clearAutoDetectGreaterThanAtLeastOneOf() {
      autoDetectGreaterThanAtLeastOneOf_ = true;
      hasAutoDetectGreaterThanAtLeastOneOf_ = false;
      return this;
    }

    /** Sets {@code stop_after_first_solution}. */
    public Builder setStopAfterFirstSolution(boolean value) {
      stopAfterFirstSolution_ = value;
      hasStopAfterFirstSolution_ = true;
      return this;
    }

    /** Resets {@code stop_after_first_solution} to its default value. */
    public Builder clearStopAfterFirstSolution() {
      stopAfterFirstSolution_ = false;
      hasStopAfterFirstSolution_ = false;
      return this;
    }

    /** Sets {@code stop_after_presolve}. */
    public Builder setStopAfterPresolve(boolean value) {
      stopAfterPresolve_ = value;
      hasStopAfterPresolve_ = true;
      return this;
    }

    /** Resets {@code stop_after_presolve} to its default value. */
    public Builder clearStopAfterPresolve() {
      stopAfterPresolve_ = false;
      hasStopAfterPresolve_ = false;
      return this;
    }

    /** Sets {@code stop_after_root_propagation}. */
    public Builder setStopAfterRootPropagation(boolean value) {
      stopAfterRootPropagation_ = value;
      hasStopAfterRootPropagation_ = true;
      return this;
    }

    /** Resets {@code stop_after_root_propagation} to its default value. */
    public Builder clearStopAfterRootPropagation() {
      stopAfterRootPropagation_ = false;
      hasStopAfterRootPropagation_ = false;
      return this;
    }

    /** Sets {@code lns_initial_difficulty}. */
    public Builder setLnsInitialDifficulty(double value) {
      lnsInitialDifficulty_ = value;
      hasLnsInitialDifficulty_ = true;
      return this;
    }

    /** Resets {@code lns_initial_difficulty} to its default value. */
    public Builder clearLnsInitialDifficulty() {
      lnsInitialDifficulty_ = 0.5;
      hasLnsInitialDifficulty_ = false;
      return this;
    }

    /** Sets {@code lns_initial_deterministic_limit}. */
    public Builder setLnsInitialDeterministicLimit(double value) {
      lnsInitialDeterministicLimit_ = value;
      hasLnsInitialDeterministicLimit_ = true;
      return this;
    }

    /** Resets {@code lns_initial_deterministic_limit} to its default value. */
    public Builder clearLnsInitialDeterministicLimit() {
      lnsInitialDeterministicLimit_ = 0.1;
      hasLnsInitialDeterministicLimit_ = false;
      return this;
    }

    /** Sets {@code use_lns}. */
    public Builder setUseLns(boolean value) {
      useLns_ = value;
      hasUseLns_ = true;
      return this;
    }

    /** Resets {@code use_lns} to its default value. */
    public Builder clearUseLns() {
      useLns_ = true;
      hasUseLns_ = false;
      return this;
    }

    /** Sets {@code use_lns_only}. */
    public Builder setUseLnsOnly(boolean value) {
      useLnsOnly_ = value;
      hasUseLnsOnly_ = true;
      return this;
    }

    /** Resets {@code use_lns_only} to its default value. */
    public Builder clearUseLnsOnly() {
      useLnsOnly_ = false;
      hasUseLnsOnly_ = false;
      return this;
    }

    /** Sets {@code solution_pool_size}. */
    public Builder setSolutionPoolSize(int value) {
      solutionPoolSize_ = value;
      hasSolutionPoolSize_ = true;
      return this;
    }

    /** Resets {@code solution_pool_size} to its default value. */
    public Builder clearSolutionPoolSize() {
      solutionPoolSize_ = 3;
      hasSolutionPoolSize_ = false;
      return this;
    }

    /** Sets {@code solution_pool_diversity_limit}. */
    public Builder setSolutionPoolDiversityLimit(int value) {
      solutionPoolDiversityLimit_ = value;
      hasSolutionPoolDiversityLimit_ = true;
      return this;
    }

    /** Resets {@code solution_pool_diversity_limit} to its default value. */
    public Builder clearSolutionPoolDiversityLimit() {
      solutionPoolDiversityLimit_ = 10;
      hasSolutionPoolDiversityLimit_ = false;
      return this;
    }

    /** Sets {@code alternative_pool_size}. */
    public Builder setAlternativePoolSize(int value) {
      alternativePoolSize_ = value;
      hasAlternativePoolSize_ = true;
      return this;
    }

    /** Resets {@code alternative_pool_size} to its default value. */
    public Builder clearAlternativePoolSize() {
      alternativePoolSize_ = 1;
      hasAlternativePoolSize_ = false;
      return this;
    }

    /** Sets {@code use_rins_lns}. */
    public Builder setUseRinsLns(boolean value) {
      useRinsLns_ = value;
      hasUseRinsLns_ = true;
      return this;
    }

    /** Resets {@code use_rins_lns} to its default value. */
    public Builder clearUseRinsLns() {
      useRinsLns_ = true;
      hasUseRinsLns_ = false;
      return this;
    }

    /** Sets {@code use_feasibility_pump}. */
    public Builder setUseFeasibilityPump(boolean value) {
      useFeasibilityPump_ = value;
      hasUseFeasibilityPump_ = true;
      return this;
    }

    /** Resets {@code use_feasibility_pump} to its default value. */
    public Builder clearUseFeasibilityPump() {
      useFeasibilityPump_ = true;
      hasUseFeasibilityPump_ = false;
      return this;
    }

    /** Sets {@code use_lb_relax_lns}. */
    public Builder setUseLbRelaxLns(boolean value) {
      useLbRelaxLns_ = value;
      hasUseLbRelaxLns_ = true;
      return this;
    }

    /** Resets {@code use_lb_relax_lns} to its default value. */
    public Builder clearUseLbRelaxLns() {
      useLbRelaxLns_ = true;
      hasUseLbRelaxLns_ = false;
      return this;
    }

    /** Sets {@code lb_relax_num_workers_threshold}. */
    public Builder setLbRelaxNumWorkersThreshold(int value) {
      lbRelaxNumWorkersThreshold_ = value;
      hasLbRelaxNumWorkersThreshold_ = true;
      return this;
    }

    /** Resets {@code lb_relax_num_workers_threshold} to its default value. */
    public Builder clearLbRelaxNumWorkersThreshold() {
      lbRelaxNumWorkersThreshold_ = 16;
      hasLbRelaxNumWorkersThreshold_ = false;
      return this;
    }

    /** Sets {@code fp_rounding}. */
    public Builder setFpRounding(SatParameters.FPRoundingMethod value) {
      if (value == null) {
        throw new NullPointerException();
      }
      fpRounding_ = value.getNumber();
      hasFpRounding_ = true;
      return this;
    }

    /** Sets the number of {@code fp_rounding}. */
    public Builder setFpRoundingValue(int value) {
      fpRounding_ = value;
      hasFpRounding_ = true;
      return this;
    }

    /** Resets {@code fp_rounding} to its default value. */
    public Builder clearFpRounding() {
      fpRounding_ = 2;
      hasFpRounding_ = false;
      return this;
    }

    /** Sets {@code diversify_lns_params}. */
    public Builder setDiversifyLnsParams(boolean value) {
      diversifyLnsParams_ = value;
      hasDiversifyLnsParams_ = true;
      return this;
    }

    /** Resets {@code diversify_lns_params} to its default value. */
    public Builder clearDiversifyLnsParams() {
      diversifyLnsParams_ = false;
      hasDiversifyLnsParams_ = false;
      return this;
    }

    /** Sets {@code randomize_search}. */
    public Builder setRandomizeSearch(boolean value) {
      randomizeSearch_ = value;
      hasRandomizeSearch_ = true;
      return this;
    }

    /** Resets {@code randomize_search} to its default value. */
    public Builder clearRandomizeSearch() {
      randomizeSearch_ = false;
      hasRandomizeSearch_ = false;
      return this;
    }

    /** Sets {@code search_random_variable_pool_size}. */
    public Builder setSearchRandomVariablePoolSize(long value) {
      searchRandomVariablePoolSize_ = value;
      hasSearchRandomVariablePoolSize_ = true;
      return this;
    }

    /** Resets {@code search_random_variable_pool_size} to its default value. */
    public Builder clearSearchRandomVariablePoolSize() {
      searchRandomVariablePoolSize_ = 0L;
      hasSearchRandomVariablePoolSize_ = false;
      return this;
    }

    /** Sets {@code push_all_tasks_toward_start}. */
    public Builder setPushAllTasksTowardStart(boolean value) {
      pushAllTasksTowardStart_ = value;
      hasPushAllTasksTowardStart_ = true;
      return this;
    }

    /** Resets {@code push_all_tasks_toward_start} to its default value. */
    public Builder clearPushAllTasksTowardStart() {
      pushAllTasksTowardStart_ = false;
      hasPushAllTasksTowardStart_ = false;
      return this;
    }

    /** Sets {@code use_optional_variables}. */
    public Builder setUseOptionalVariables(boolean value) {
      useOptionalVariables_ = value;
      hasUseOptionalVariables_ = true;
      return this;
    }

    /** Resets {@code use_optional_variables} to its default value. */
    public Builder clearUseOptionalVariables() {
      useOptionalVariables_ = false;
      hasUseOptionalVariables_ = false;
      return this;
    }

    /** Sets {@code use_exact_lp_reason}. */
    public Builder setUseExactLpReason(boolean value) {
      useExactLpReason_ = value;
      hasUseExactLpReason_ = true;
      return this;
    }

    /** Resets {@code use_exact_lp_reason} to its default value. */
    public Builder clearUseExactLpReason() {
      useExactLpReason_ = true;
      hasUseExactLpReason_ = false;
      return this;
    }

    /** Sets {@code use_combined_no_overlap}. */
    public Builder setUseCombinedNoOverlap(boolean value) {
      useCombinedNoOverlap_ = value;
      hasUseCombinedNoOverlap_ = true;
      return this;
    }

    /** Resets {@code use_combined_no_overlap} to its default value. */
    public Builder clearUseCombinedNoOverlap() {
      useCombinedNoOverlap_ = false;
      hasUseCombinedNoOverlap_ = false;
      return this;
    }

    /** Sets {@code at_most_one_max_expansion_size}. */
    public Builder setAtMostOneMaxExpansionSize(int value) {
      atMostOneMaxExpansionSize_ = value;
      hasAtMostOneMaxExpansionSize_ = true;
      return this;
    }

    /** Resets {@code at_most_one_max_expansion_size} to its default value. */
    public Builder clearAtMostOneMaxExpansionSize() {
      atMostOneMaxExpansionSize_ = 3;
      hasAtMostOneMaxExpansionSize_ = false;
      return this;
    }

    /** Sets {@code catch_sigint_signal}. */
    public Builder setCatchSigintSignal(boolean value) {
      catchSigintSignal_ = value;
      hasCatchSigintSignal_ = true;
      return this;
    }

    /** Resets {@code catch_sigint_signal} to its default value. */
    public Builder clearCatchSigintSignal() {
      catchSigintSignal_ = true;
      hasCatchSigintSignal_ = false;
      return this;
    }

    /** Sets {@code use_implied_bounds}. */
    public Builder setUseImpliedBounds(boolean value) {
      useImpliedBounds_ = value;
      hasUseImpliedBounds_ = true;
      return this;
    }

    /** Resets {@code use_implied_bounds} to its default value. */
    public Builder clearUseImpliedBounds() {
      useImpliedBounds_ = true;
      hasUseImpliedBounds_ = false;
      return this;
    }

    /** Sets {@code polish_lp_solution}. */
    public Builder setPolishLpSolution(boolean value) {
      polishLpSolution_ = value;
      hasPolishLpSolution_ = true;
      return this;
    }

    /** Resets {@code polish_lp_solution} to its default value. */
    public Builder clearPolishLpSolution() {
      polishLpSolution_ = false;
      hasPolishLpSolution_ = false;
      return this;
    }

    /** Sets {@code lp_primal_tolerance}. */
    public Builder setLpPrimalTolerance(double value) {
      lpPrimalTolerance_ = value;
      hasLpPrimalTolerance_ = true;
      return this;
    }

    /** Resets {@code lp_primal_tolerance} to its default value. */
    public Builder clearLpPrimalTolerance() {
      lpPrimalTolerance_ = 1e-7;
      hasLpPrimalTolerance_ = false;
      return this;
    }

    /** Sets {@code lp_dual_tolerance}. */
    public Builder setLpDualTolerance(double value) {
      lpDualTolerance_ = value;
      hasLpDualTolerance_ = true;
      return this;
    }

    /** Resets {@code lp_dual_tolerance} to its default value. */
    public Builder clearLpDualTolerance() {
      lpDualTolerance_ = 1e-7;
      hasLpDualTolerance_ = false;
      return this;
    }

    /** Sets {@code convert_intervals}. */
    public Builder setConvertIntervals(boolean value) {
      convertIntervals_ = value;
      hasConvertIntervals_ = true;
      return this;
    }

    /** Resets {@code convert_intervals} to its default value. */
    public Builder clearConvertIntervals() {
      convertIntervals_ = true;
      hasConvertIntervals_ = false;
      return this;
    }

    /** Sets {@code symmetry_level}. */
    public Builder setSymmetryLevel(int value) {
      symmetryLevel_ = value;
      hasSymmetryLevel_ = true;
      return this;
    }

    /** Resets {@code symmetry_level} to its default value. */
    public Builder clearSymmetryLevel() {
      symmetryLevel_ = 2;
      hasSymmetryLevel_ = false;
      return this;
    }

    /** Sets {@code use_symmetry_in_lp}. */
    public Builder setUseSymmetryInLp(boolean value) {
      useSymmetryInLp_ = value;
      hasUseSymmetryInLp_ = true;
      return this;
    }

    /** Resets {@code use_symmetry_in_lp} to its default value. */
    public Builder clearUseSymmetryInLp() {
      useSymmetryInLp_ = false;
      hasUseSymmetryInLp_ = false;
      return this;
    }

    /** Sets {@code keep_symmetry_in_presolve}. */
    public Builder setKeepSymmetryInPresolve(boolean value) {
      keepSymmetryInPresolve_ = value;
      hasKeepSymmetryInPresolve_ = true;
      return this;
    }

    /** Resets {@code keep_symmetry_in_presolve} to its default value. */
    public Builder clearKeepSymmetryInPresolve() {
      keepSymmetryInPresolve_ = false;
      hasKeepSymmetryInPresolve_ = false;
      return this;
    }

    /** Sets {@code symmetry_detection_deterministic_time_limit}. */
    public Builder setSymmetryDetectionDeterministicTimeLimit(double value) {
      symmetryDetectionDeterministicTimeLimit_ = value;
      hasSymmetryDetectionDeterministicTimeLimit_ = true;
      return this;
    }

    /** Resets {@code symmetry_detection_deterministic_time_limit} to its default value. */
    public Builder clearSymmetryDetectionDeterministicTimeLimit() {
      symmetryDetectionDeterministicTimeLimit_ = 1.0;
      hasSymmetryDetectionDeterministicTimeLimit_ = false;
      return this;
    }

    /** Sets {@code new_linear_propagation}. */
    public Builder setNewLinearPropagation(boolean value) {
      newLinearPropagation_ = value;
      hasNewLinearPropagation_ = true;
      return this;
    }

    /** Resets {@code new_linear_propagation} to its default value. */
    public Builder clearNewLinearPropagation() {
      newLinearPropagation_ = true;
      hasNewLinearPropagation_ = false;
      return this;
    }

    /** Sets {@code linear_split_size}. */
    public Builder setLinearSplitSize(int value) {
      linearSplitSize_ = value;
      hasLinearSplitSize_ = true;
      return this;
    }

    /** Resets {@code linear_split_size} to its default value. */
    public Builder clearLinearSplitSize() {
      linearSplitSize_ = 100;
      hasLinearSplitSize_ = false;
      return this;
    }

    /** Sets {@code linearization_level}. */
    public Builder setLinearizationLevel(int value) {
      linearizationLevel_ = value;
      hasLinearizationLevel_ = true;
      return this;
    }

    /** Resets {@code linearization_level} to its default value. */
    public Builder clearLinearizationLevel() {
      linearizationLevel_ = 1;
      hasLinearizationLevel_ = false;
      return this;
    }

    /** Sets {@code boolean_encoding_level}. */
    public Builder setBooleanEncodingLevel(int value) {
      booleanEncodingLevel_ = value;
      hasBooleanEncodingLevel_ = true;
      return this;
    }

    /** Resets {@code boolean_encoding_level} to its default value. */
    public Builder clearBooleanEncodingLevel() {
      booleanEncodingLevel_ = 1;
      hasBooleanEncodingLevel_ = false;
      return this;
    }

    /** Sets {@code max_domain_size_when_encoding_eq_neq_constraints}. */
    public Builder setMaxDomainSizeWhenEncodingEqNeqConstraints(int value) {
      maxDomainSizeWhenEncodingEqNeqConstraints_ = value;
      hasMaxDomainSizeWhenEncodingEqNeqConstraints_ = true;
      return this;
    }

    /** Resets {@code max_domain_size_when_encoding_eq_neq_constraints} to its default value. */
    public Builder clearMaxDomainSizeWhenEncodingEqNeqConstraints() {
      maxDomainSizeWhenEncodingEqNeqConstraints_ = 16;
      hasMaxDomainSizeWhenEncodingEqNeqConstraints_ = false;
      return this;
    }

    /** Sets {@code max_num_cuts}. */
    public Builder setMaxNumCuts(int value) {
      maxNumCuts_ = value;
      hasMaxNumCuts_ = true;
      return this;
    }

    /** Resets {@code max_num_cuts} to its default value. */
    public Builder clearMaxNumCuts() {
      maxNumCuts_ = 10000;
      hasMaxNumCuts_ = false;
      return this;
    }

    /** Sets {@code cut_level}. */
    public Builder setCutLevel(int value) {
      cutLevel_ = value;
      hasCutLevel_ = true;
      return this;
    }

    /** Resets {@code cut_level} to its default value. */
    public Builder clearCutLevel() {
      cutLevel_ = 1;
      hasCutLevel_ = false;
      return this;
    }

    /** Sets {@code only_add_cuts_at_level_zero}. */
    public Builder setOnlyAddCutsAtLevelZero(boolean value) {
      onlyAddCutsAtLevelZero_ = value;
      hasOnlyAddCutsAtLevelZero_ = true;
      return this;
    }

    /** Resets {@code only_add_cuts_at_level_zero} to its default value. */
    public Builder clearOnlyAddCutsAtLevelZero() {
      onlyAddCutsAtLevelZero_ = false;
      hasOnlyAddCutsAtLevelZero_ = false;
      return this;
    }

    /** Sets {@code add_objective_cut}. */
    public Builder setAddObjectiveCut(boolean value) {
      addObjectiveCut_ = value;
      hasAddObjectiveCut_ = true;
      return this;
    }

    /** Resets {@code add_objective_cut} to its default value. */
    public Builder clearAddObjectiveCut() {
      addObjectiveCut_ = false;
      hasAddObjectiveCut_ = false;
      return this;
    }

    /** Sets {@code add_cg_cuts}. */
    public Builder setAddCgCuts(boolean value) {
      addCgCuts_ = value;
      hasAddCgCuts_ = true;
      return this;
    }

    /** Resets {@code add_cg_cuts} to its default value. */
    public Builder clearAddCgCuts() {
      addCgCuts_ = true;
      hasAddCgCuts_ = false;
      return this;
    }

    /** Sets {@code add_mir_cuts}. */
    public Builder setAddMirCuts(boolean value) {
      addMirCuts_ = value;
      hasAddMirCuts_ = true;
      return this;
    }

    /** Resets {@code add_mir_cuts} to its default value. */
    public Builder clearAddMirCuts() {
      addMirCuts_ = true;
      hasAddMirCuts_ = false;
      return this;
    }

    /** Sets {@code add_zero_half_cuts}. */
    public Builder setAddZeroHalfCuts(boolean value) {
      addZeroHalfCuts_ = value;
      hasAddZeroHalfCuts_ = true;
      return this;
    }

    /** Resets {@code add_zero_half_cuts} to its default value. */
    public Builder clearAddZeroHalfCuts() {
      addZeroHalfCuts_ = true;
      hasAddZeroHalfCuts_ = false;
      return this;
    }

    /** Sets {@code add_clique_cuts}. */
    public Builder setAddCliqueCuts(boolean value) {
      addCliqueCuts_ = value;
      hasAddCliqueCuts_ = true;
      return this;
    }

    /** Resets {@code add_clique_cuts} to its default value. */
    public Builder clearAddCliqueCuts() {
      addCliqueCuts_ = true;
      hasAddCliqueCuts_ = false;
      return this;
    }

    /** Sets {@code add_rlt_cuts}. */
    public Builder setAddRltCuts(boolean value) {
      addRltCuts_ = value;
      hasAddRltCuts_ = true;
      return this;
    }

    /** Resets {@code add_rlt_cuts} to its default value. */
    public Builder clearAddRltCuts() {
      addRltCuts_ = true;
      hasAddRltCuts_ = false;
      return this;
    }

    /** Sets {@code max_all_diff_cut_size}. */
    public Builder setMaxAllDiffCutSize(int value) {
      maxAllDiffCutSize_ = value;
      hasMaxAllDiffCutSize_ = true;
      return this;
    }

    /** Resets {@code max_all_diff_cut_size} to its default value. */
    public Builder clearMaxAllDiffCutSize() {
      maxAllDiffCutSize_ = 64;
      hasMaxAllDiffCutSize_ = false;
      return this;
    }

    /** Sets {@code add_lin_max_cuts}. */
    public Builder setAddLinMaxCuts(boolean value) {
      addLinMaxCuts_ = value;
      hasAddLinMaxCuts_ = true;
      return this;
    }

    /** Resets {@code add_lin_max_cuts} to its default value. */
    public Builder clearAddLinMaxCuts() {
      addLinMaxCuts_ = true;
      hasAddLinMaxCuts_ = false;
      return this;
    }

    /** Sets {@code max_integer_rounding_scaling}. */
    public Builder setMaxIntegerRoundingScaling(int value) {
      maxIntegerRoundingScaling_ = value;
      hasMaxIntegerRoundingScaling_ = true;
      return this;
    }

    /** Resets {@code max_integer_rounding_scaling} to its default value. */
    public Builder clearMaxIntegerRoundingScaling() {
      maxIntegerRoundingScaling_ = 600;
      hasMaxIntegerRoundingScaling_ = false;
      return this;
    }

    /** Sets {@code add_lp_constraints_lazily}. */
    public Builder setAddLpConstraintsLazily(boolean value) {
      addLpConstraintsLazily_ = value;
      hasAddLpConstraintsLazily_ = true;
      return this;
    }

    /** Resets {@code add_lp_constraints_lazily} to its default value. */
    public Builder clearAddLpConstraintsLazily() {
      addLpConstraintsLazily_ = true;
      hasAddLpConstraintsLazily_ = false;
      return this;
    }

    /** Sets {@code root_lp_iterations}. */
    public Builder setRootLpIterations(int value) {
      rootLpIterations_ = value;
      hasRootLpIterations_ = true;
      return this;
    }

    /** Resets {@code root_lp_iterations} to its default value. */
    public Builder clearRootLpIterations() {
      rootLpIterations_ = 2000;
      hasRootLpIterations_ = false;
      return this;
    }

    /** Sets {@code min_orthogonality_for_lp_constraints}. */
    public Builder setMinOrthogonalityForLpConstraints(double value) {
      minOrthogonalityForLpConstraints_ = value;
      hasMinOrthogonalityForLpConstraints_ = true;
      return this;
    }

    /** Resets {@code min_orthogonality_for_lp_constraints} to its default value. */
    public Builder clearMinOrthogonalityForLpConstraints() {
      minOrthogonalityForLpConstraints_ = 0.05;
      hasMinOrthogonalityForLpConstraints_ = false;
      return this;
    }

    /** Sets {@code max_cut_rounds_at_level_zero}. */
    public Builder setMaxCutRoundsAtLevelZero(int value) {
      maxCutRoundsAtLevelZero_ = value;
      hasMaxCutRoundsAtLevelZero_ = true;
      return this;
    }

    /** Resets {@code max_cut_rounds_at_level_zero} to its default value. */
    public Builder clearMaxCutRoundsAtLevelZero() {
      maxCutRoundsAtLevelZero_ = 1;
      hasMaxCutRoundsAtLevelZero_ = false;
      return this;
    }

    /** Sets {@code max_consecutive_inactive_count}. */
    public Builder setMaxConsecutiveInactiveCount(int value) {
      maxConsecutiveInactiveCount_ = value;
      hasMaxConsecutiveInactiveCount_ = true;
      return this;
    }

    /** Resets {@code max_consecutive_inactive_count} to its default value. */
    public Builder clearMaxConsecutiveInactiveCount() {
      maxConsecutiveInactiveCount_ = 100;
      hasMaxConsecutiveInactiveCount_ = false;
      return this;
    }

    /** Sets {@code cut_max_active_count_value}. */
    public Builder setCutMaxActiveCountValue(double value) {
      cutMaxActiveCountValue_ = value;
      hasCutMaxActiveCountValue_ = true;
      return this;
    }

    /** Resets {@code cut_max_active_count_value} to its default value. */
    public Builder clearCutMaxActiveCountValue() {
      cutMaxActiveCountValue_ = 1e10;
      hasCutMaxActiveCountValue_ = false;
      return this;
    }

    /** Sets {@code cut_active_count_decay}. */
    public Builder setCutActiveCountDecay(double value) {
      cutActiveCountDecay_ = value;
      hasCutActiveCountDecay_ = true;
      return this;
    }

    /** Resets {@code cut_active_count_decay} to its default value. */
    public Builder clearCutActiveCountDecay() {
      cutActiveCountDecay_ = 0.8;
      hasCutActiveCountDecay_ = false;
      return this;
    }

    /** Sets {@code cut_cleanup_target}. */
    public Builder setCutCleanupTarget(int value) {
      cutCleanupTarget_ = value;
      hasCutCleanupTarget_ = true;
      return this;
    }

    /** Resets {@code cut_cleanup_target} to its default value. */
    public Builder clearCutCleanupTarget() {
      cutCleanupTarget_ = 1000;
      hasCutCleanupTarget_ = false;
      return this;
    }

    /** Sets {@code new_constraints_batch_size}. */
    public Builder setNewConstraintsBatchSize(int value) {
      newConstraintsBatchSize_ = value;
      hasNewConstraintsBatchSize_ = true;
      return this;
    }

    /** Resets {@code new_constraints_batch_size} to its default value. */
    public Builder clearNewConstraintsBatchSize() {
      newConstraintsBatchSize_ = 50;
      hasNewConstraintsBatchSize_ = false;
      return this;
    }

    /** Sets {@code exploit_integer_lp_solution}. */
    public Builder setExploitIntegerLpSolution(boolean value) {
      exploitIntegerLpSolution_ = value;
      hasExploitIntegerLpSolution_ = true;
      return this;
    }

    /** Resets {@code exploit_integer_lp_solution} to its default value. */
    public Builder clearExploitIntegerLpSolution() {
      exploitIntegerLpSolution_ = true;
      hasExploitIntegerLpSolution_ = false;
      return this;
    }

    /** Sets {@code exploit_all_lp_solution}. */
    public Builder setExploitAllLpSolution(boolean value) {
      exploitAllLpSolution_ = value;
      hasExploitAllLpSolution_ = true;
      return this;
    }

    /** Resets {@code exploit_all_lp_solution} to its default value. */
    public Builder clearExploitAllLpSolution() {
      exploitAllLpSolution_ = true;
      hasExploitAllLpSolution_ = false;
      return this;
    }

    /** Sets {@code exploit_best_solution}. */
    public Builder setExploitBestSolution(boolean value) {
      exploitBestSolution_ = value;
      hasExploitBestSolution_ = true;
      return this;
    }

    /** Resets {@code exploit_best_solution} to its default value. */
    public Builder clearExploitBestSolution() {
      exploitBestSolution_ = false;
      hasExploitBestSolution_ = false;
      return this;
    }

    /** Sets {@code exploit_relaxation_solution}. */
    public Builder setExploitRelaxationSolution(boolean value) {
      exploitRelaxationSolution_ = value;
      hasExploitRelaxationSolution_ = true;
      return this;
    }

    /** Resets {@code exploit_relaxation_solution} to its default value. */
    public Builder clearExploitRelaxationSolution() {
      exploitRelaxationSolution_ = false;
      hasExploitRelaxationSolution_ = false;
      return this;
    }

    /** Sets {@code exploit_objective}. */
    public Builder setExploitObjective(boolean value) {
      exploitObjective_ = value;
      hasExploitObjective_ = true;
      return this;
    }

    /** Resets {@code exploit_objective} to its default value. */
    public Builder clearExploitObjective() {
      exploitObjective_ = true;
      hasExploitObjective_ = false;
      return this;
    }

    /** Sets {@code detect_linearized_product}. */
    public Builder setDetectLinearizedProduct(boolean value) {
      detectLinearizedProduct_ = value;
      hasDetectLinearizedProduct_ = true;
      return this;
    }

    /** Resets {@code detect_linearized_product} to its default value. */
    public Builder clearDetectLinearizedProduct() {
      detectLinearizedProduct_ = false;
      hasDetectLinearizedProduct_ = false;
      return this;
    }

    /** Sets {@code use_new_integer_conflict_resolution}. */
    public Builder setUseNewIntegerConflictResolution(boolean value) {
      useNewIntegerConflictResolution_ = value;
      hasUseNewIntegerConflictResolution_ = true;
      return this;
    }

    /** Resets {@code use_new_integer_conflict_resolution} to its default value. */
    public Builder clearUseNewIntegerConflictResolution() {
      useNewIntegerConflictResolution_ = true;
      hasUseNewIntegerConflictResolution_ = false;
      return this;
    }

    /** Sets {@code create_1uip_boolean_during_icr}. */
    public Builder setCreate1UipBooleanDuringIcr(boolean value) {
      create1UipBooleanDuringIcr_ = value;
      hasCreate1UipBooleanDuringIcr_ = true;
      return this;
    }

    /** Resets {@code create_1uip_boolean_during_icr} to its default value. */
    public Builder clearCreate1UipBooleanDuringIcr() {
      create1UipBooleanDuringIcr_ = true;
      hasCreate1UipBooleanDuringIcr_ = false;
      return this;
    }

    /** Sets {@code mip_max_bound}. */
    public Builder setMipMaxBound(double value) {
      mipMaxBound_ = value;
      hasMipMaxBound_ = true;
      return this;
    }

    /** Resets {@code mip_max_bound} to its default value. */
    public Builder clearMipMaxBound() {
      mipMaxBound_ = 1e7;
      hasMipMaxBound_ = false;
      return this;
    }

    /** Sets {@code mip_var_scaling}. */
    public Builder setMipVarScaling(double value) {
      mipVarScaling_ = value;
      hasMipVarScaling_ = true;
      return this;
    }

    /** Resets {@code mip_var_scaling} to its default value. */
    public Builder clearMipVarScaling() {
      mipVarScaling_ = 1.0;
      hasMipVarScaling_ = false;
      return this;
    }

    /** Sets {@code mip_scale_large_domain}. */
    public Builder setMipScaleLargeDomain(boolean value) {
      mipScaleLargeDomain_ = value;
      hasMipScaleLargeDomain_ = true;
      return this;
    }

    /** Resets {@code mip_scale_large_domain} to its default value. */
    public Builder clearMipScaleLargeDomain() {
      mipScaleLargeDomain_ = false;
      hasMipScaleLargeDomain_ = false;
      return this;
    }

    /** Sets {@code mip_automatically_scale_variables}. */
    public Builder setMipAutomaticallyScaleVariables(boolean value) {
      mipAutomaticallyScaleVariables_ = value;
      hasMipAutomaticallyScaleVariables_ = true;
      return this;
    }

    /** Resets {@code mip_automatically_scale_variables} to its default value. */
    public Builder clearMipAutomaticallyScaleVariables() {
      mipAutomaticallyScaleVariables_ = true;
      hasMipAutomaticallyScaleVariables_ = false;
      return this;
    }

    /** Sets {@code mip_scale_to_max_bound}. */
    public Builder setMipScaleToMaxBound(boolean value) {
      mipScaleToMaxBound_ = value;
      hasMipScaleToMaxBound_ = true;
      return this;
    }

    /** Resets {@code mip_scale_to_max_bound} to its default value. */
    public Builder clearMipScaleToMaxBound() {
      mipScaleToMaxBound_ = false;
      hasMipScaleToMaxBound_ = false;
      return this;
    }

    /** Sets {@code mip_presolve_restrict_bounds}. */
    public Builder setMipPresolveRestrictBounds(boolean value) {
      mipPresolveRestrictBounds_ = value;
      hasMipPresolveRestrictBounds_ = true;
      return this;
    }

    /** Resets {@code mip_presolve_restrict_bounds} to its default value. */
    public Builder clearMipPresolveRestrictBounds() {
      mipPresolveRestrictBounds_ = true;
      hasMipPresolveRestrictBounds_ = false;
      return this;
    }

    /** Sets {@code only_solve_ip}. */
    public Builder setOnlySolveIp(boolean value) {
      onlySolveIp_ = value;
      hasOnlySolveIp_ = true;
      return this;
    }

    /** Resets {@code only_solve_ip} to its default value. */
    public Builder clearOnlySolveIp() {
      onlySolveIp_ = false;
      hasOnlySolveIp_ = false;
      return this;
    }

    /** Sets {@code mip_wanted_precision}. */
    public Builder setMipWantedPrecision(double value) {
      mipWantedPrecision_ = value;
      hasMipWantedPrecision_ = true;
      return this;
    }

    /** Resets {@code mip_wanted_precision} to its default value. */
    public Builder clearMipWantedPrecision() {
      mipWantedPrecision_ = 1e-6;
      hasMipWantedPrecision_ = false;
      return this;
    }

    /** Sets {@code mip_max_activity_exponent}. */
    public Builder setMipMaxActivityExponent(int value) {
      mipMaxActivityExponent_ = value;
      hasMipMaxActivityExponent_ = true;
      return this;
    }

    /** Resets {@code mip_max_activity_exponent} to its default value. */
    public Builder clearMipMaxActivityExponent() {
      mipMaxActivityExponent_ = 53;
      hasMipMaxActivityExponent_ = false;
      return this;
    }

    /** Sets {@code mip_check_precision}. */
    public Builder setMipCheckPrecision(double value) {
      mipCheckPrecision_ = value;
      hasMipCheckPrecision_ = true;
      return this;
    }

    /** Resets {@code mip_check_precision} to its default value. */
    public Builder clearMipCheckPrecision() {
      mipCheckPrecision_ = 1e-5;
      hasMipCheckPrecision_ = false;
      return this;
    }

    /** Sets {@code mip_compute_true_objective_bound}. */
    public Builder setMipComputeTrueObjectiveBound(boolean value) {
      mipComputeTrueObjectiveBound_ = value;
      hasMipComputeTrueObjectiveBound_ = true;
      return this;
    }

    /** Resets {@code mip_compute_true_objective_bound} to its default value. */
    public Builder clearMipComputeTrueObjectiveBound() {
      mipComputeTrueObjectiveBound_ = true;
      hasMipComputeTrueObjectiveBound_ = false;
      return this;
    }

    /** Sets {@code mip_max_valid_magnitude}. */
    public Builder setMipMaxValidMagnitude(double value) {
      mipMaxValidMagnitude_ = value;
      hasMipMaxValidMagnitude_ = true;
      return this;
    }

    /** Resets {@code mip_max_valid_magnitude} to its default value. */
    public Builder clearMipMaxValidMagnitude() {
      mipMaxValidMagnitude_ = 1e20;
      hasMipMaxValidMagnitude_ = false;
      return this;
    }

    /** Sets {@code mip_treat_high_magnitude_bounds_as_infinity}. */
    public Builder setMipTreatHighMagnitudeBoundsAsInfinity(boolean value) {
      mipTreatHighMagnitudeBoundsAsInfinity_ = value;
      hasMipTreatHighMagnitudeBoundsAsInfinity_ = true;
      return this;
    }

    /** Resets {@code mip_treat_high_magnitude_bounds_as_infinity} to its default value. */
    public Builder clearMipTreatHighMagnitudeBoundsAsInfinity() {
      mipTreatHighMagnitudeBoundsAsInfinity_ = false;
      hasMipTreatHighMagnitudeBoundsAsInfinity_ = false;
      return this;
    }

    /** Sets {@code mip_drop_tolerance}. */
    public Builder setMipDropTolerance(double value) {
      mipDropTolerance_ = value;
      hasMipDropTolerance_ = true;
      return this;
    }

    /** Resets {@code mip_drop_tolerance} to its default value. */
    public Builder clearMipDropTolerance() {
      mipDropTolerance_ = 1e-16;
      hasMipDropTolerance_ = false;
      return this;
    }

    /** Sets {@code mip_presolve_level}. */
    public Builder setMipPresolveLevel(int value) {
      mipPresolveLevel_ = value;
      hasMipPresolveLevel_ = true;
      return this;
    }

    /** Resets {@code mip_presolve_level} to its default value. */
    public Builder clearMipPresolveLevel() {
      mipPresolveLevel_ = 2;
      hasMipPresolveLevel_ = false;
      return this;
    }

    /** Sets {@code mip_use_tight_bounds}. */
    public Builder setMipUseTightBounds(boolean value) {
      mipUseTightBounds_ = value;
      hasMipUseTightBounds_ = true;
      return this;
    }

    /** Resets {@code mip_use_tight_bounds} to its default value. */
    public Builder clearMipUseTightBounds() {
      mipUseTightBounds_ = true;
      hasMipUseTightBounds_ = false;
      return this;
    }

    /** Sets {@code mip_tight_precision}. */
    public Builder setMipTightPrecision(double value) {
      mipTightPrecision_ = value;
      hasMipTightPrecision_ = true;
      return this;
    }

    /** Resets {@code mip_tight_precision} to its default value. */
    public Builder clearMipTightPrecision() {
      mipTightPrecision_ = 1e-9;
      hasMipTightPrecision_ = false;
      return this;
    }

    /** Sets {@code mip_verify_solution}. */
    public Builder setMipVerifySolution(boolean value) {
      mipVerifySolution_ = value;
      hasMipVerifySolution_ = true;
      return this;
    }

    /** Resets {@code mip_verify_solution} to its default value. */
    public Builder clearMipVerifySolution() {
      mipVerifySolution_ = false;
      hasMipVerifySolution_ = false;
      return this;
    }

    /** Sets {@code mip_proper_relaxation}. */
    public Builder setMipProperRelaxation(boolean value) {
      mipProperRelaxation_ = value;
      hasMipProperRelaxation_ = true;
      return this;
    }

    /** Resets {@code mip_proper_relaxation} to its default value. */
    public Builder clearMipProperRelaxation() {
      mipProperRelaxation_ = false;
      hasMipProperRelaxation_ = false;
      return this;
    }

  }
}
