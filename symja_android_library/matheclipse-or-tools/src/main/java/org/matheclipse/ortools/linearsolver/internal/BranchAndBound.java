package org.matheclipse.ortools.linearsolver.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.matheclipse.ortools.glop.BasisState;
import org.matheclipse.ortools.glop.GlopParameters;
import org.matheclipse.ortools.glop.ProblemStatus;
import org.matheclipse.ortools.glop.RevisedSimplex;
import org.matheclipse.ortools.glop.Status;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.SparseMatrixScaler;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * A branch-and-bound solver for mixed integer linear programs using the Glop revised simplex for
 * the LP relaxations. It is the Java replacement of the MIP solvers (SCIP, CBC, ...) behind
 * {@link MPSolver} and the ModelBuilder API.
 *
 * <ul>
 * <li>The LP relaxation is scaled once (geometric scaling and equilibration); the nodes only change
 * variable bounds and are solved with the dual simplex, warm-started from the basis of the parent
 * node (the last solved node incrementally, other nodes from their stored basis state).
 * <li>Presolve: the bounds of the rows over integer variables with integer coefficients are
 * rounded to multiples of the gcd of the coefficients, and a packing orbitope (interchangeable
 * columns of binary variables, as the identical bins of a multiple knapsack) is detected and
 * broken by linear lexicographic ordering rows.
 * <li>Root cutting planes: lifted knapsack cover cuts on the rows over binary variables.
 * <li>Node propagation: bound tightening from the row activities, and subset sum tightening of
 * the bounds of the rows over integer variables with integer coefficients (the node LP then
 * gets the tightened row bounds).
 * <li>Node selection: depth-first dive (always into the child on the side of the nearest integer)
 * until a first solution is found, then best bound, ties broken by depth, plunging into a child
 * while its bound stays close to the best open bound.
 * <li>Branching with pseudo costs (product rule), the most fractional variable while there is no
 * history.
 * <li>Pruning by bound, with the bound rounded up to the next possible objective value when the
 * objective only takes the multiples of a step (rational coefficients on integer variables only).
 * <li>Reduced cost fixing of the non-basic integer variables at each node once a solution is
 * known.
 * <li>A rounding heuristic at the root and periodically: round the integer variables of the LP
 * solution (to the nearest, down and up for pure integer models) and, if there are continuous
 * variables, solve the LP over them.
 * <li>Termination when the relative gap is reached (RELATIVE_MIP_GAP, default 1e-4), on the time
 * limit, on the node limit (<code>max_nodes</code> in the solver specific parameters) or on
 * interruption.
 * <li>Indicator (enforced) constraints are reformulated with big-M coefficients computed from the
 * variable bounds; an {@link IllegalArgumentException} is thrown when the bounds are infinite.
 * </ul>
 */
public final class BranchAndBound {

  private static final Logger LOGGER = Logger.getLogger(BranchAndBound.class.getName());

  private static final double INTEGRALITY_TOLERANCE = 1e-6;
  private static final double FEASIBILITY_TOLERANCE = 1e-6;
  private static final int HEURISTIC_PERIOD = 20;
  private static final int MAX_CUT_ROUNDS = 20;
  private static final int MAX_CUTS_PER_ROUND = 200;
  private static final int MAX_PROPAGATION_PASSES = 20;
  private static final int SUBSET_SUM_LIMIT = 1 << 14;

  /** The options of a solve. */
  public static final class Options {
    /** The relative gap at which the search stops. */
    public double relativeMipGap = 1e-4;
    /** The time limit in seconds. */
    public double timeLimitSeconds = Double.POSITIVE_INFINITY;
    /** The maximum number of nodes. */
    public long maxNodes = Long.MAX_VALUE;
    /** The parameters of the LP solves (the dual simplex is always used for the nodes). */
    public GlopParameters lpParameters = new GlopParameters();
    /** Whether to log the progress. */
    public boolean logSearchProgress;
    /** An interrupt flag. */
    public AtomicBoolean interrupt = new AtomicBoolean(false);
    /** An optional callback receiving the log lines. */
    public java.util.function.Consumer<String> logCallback;
    /** Whether knapsack cover cuts are added at the root. */
    public boolean useCuts = true;
    /**
     * Whether the rows over integer variables are tightened and symmetry breaking rows are added
     * before the search.
     */
    public boolean usePresolve = true;
  }

  /** A node of the search tree: a set of bound changes on top of its parent. */
  private static final class Node {
    final Node parent;
    final int depth;
    // bound changes relative to the parent: var, lb, ub
    int[] changedVars = new int[0];
    double[] changedLb = new double[0];
    double[] changedUb = new double[0];
    // the bound of the parent LP (min form), used as priority
    double bound;
    // the basis of the parent for the warm start, ordinals of VariableStatus
    byte[] parentState;
    long id;
    // the branching which created the node: variable (-1 for the root), direction, distance of
    // the parent LP value to the new bound
    int branchVar = -1;
    boolean branchUp;
    double branchDistance;

    Node(Node parent) {
      this.parent = parent;
      this.depth = parent == null ? 0 : parent.depth + 1;
    }

    void addChange(int var, double lb, double ub) {
      final int n = changedVars.length;
      changedVars = Arrays.copyOf(changedVars, n + 1);
      changedLb = Arrays.copyOf(changedLb, n + 1);
      changedUb = Arrays.copyOf(changedUb, n + 1);
      changedVars[n] = var;
      changedLb[n] = lb;
      changedUb[n] = ub;
    }
  }

  private final Options options;
  private MPModel model;
  // whether model is a private copy (cuts may be added to it)
  private boolean modelIsOwned;
  // the number of constraints of the input model, the following ones are cuts and symmetry rows
  private int numOriginalConstraints;
  private int numVars;
  private boolean[] isInteger;
  private double[] objective; // minimization form, original units
  private double objectiveOffset; // minimization form
  private double sign;
  private double[] rootLb;
  private double[] rootUb;
  private LinearProgram scaledLp;
  private double[] colScale;
  private double boundScalingFactor;
  private double costScalingFactor;
  // > 0 when the objective (minus its offset) of every integer solution is a multiple of it
  private double objectiveStep;
  private final RevisedSimplex simplex = new RevisedSimplex();
  // pseudo costs: the sum and count of the objective gains per unit of bound change
  private double[] pseudoCostDownSum;
  private int[] pseudoCostDownCount;
  private double[] pseudoCostUpSum;
  private int[] pseudoCostUpCount;
  private long numCuts;
  private Node lastSolvedNode;
  private TimeLimit timeLimit;

  private double[] incumbent;
  private double incumbentObjective = Double.POSITIVE_INFINITY;
  private long numNodes;
  private long numIterations;
  private boolean searchIncomplete;
  private double skippedNodeBound = Double.POSITIVE_INFINITY;
  private long nodeCounter;

  // the rows of the model for the propagation
  private int[][] rowVars;
  private double[][] rowCoefficients;
  private double[] rowLb;
  private double[] rowUb;
  // rows over integer variables with integer coefficients (subset sum tightening)
  private boolean[] integerRow;
  // scaled row bound = row bound / rowScaleDivisor
  private double[] rowScaleDivisor;
  // the unscaled row bounds currently set in the scaled LP
  private double[] scaledRowLbSource;
  private double[] scaledRowUbSource;

  // results of the last node LP: the propagated bounds, the solution
  private double[] nodeLb;
  private double[] nodeUb;
  private double[] nodeValues;
  private double[] nodeReducedCosts;
  private VariableStatus[] nodeStatuses;
  private double nodeObjective;

  /** Creates a solver with the given options. */
  public BranchAndBound(Options options) {
    this.options = options;
  }

  /** The number of nodes of the last solve. */
  public long numNodes() {
    return numNodes;
  }

  /** The number of simplex iterations of the last solve. */
  public long numIterations() {
    return numIterations;
  }

  /**
   * Returns a copy of the model where the indicator constraints are replaced by big-M linear
   * constraints. Throws {@link IllegalArgumentException} if a needed bound is infinite.
   */
  public static MPModel linearizeIndicatorConstraints(MPModel model) {
    if (model.generalConstraints.isEmpty()) {
      return model;
    }
    final MPModel result = model.copy();
    result.generalConstraints.clear();
    for (MPModel.IndicatorConstraint ind : model.generalConstraints) {
      final MPModel.Constraint ct = ind.constraint;
      final MPModel.Variable z = result.variables.get(ind.varIndex);
      if (z.lowerBound < 0.0 || z.upperBound > 1.0) {
        z.lowerBound = Math.max(0.0, z.lowerBound);
        z.upperBound = Math.min(1.0, z.upperBound);
      }
      z.isInteger = true;
      double minActivity = 0.0;
      double maxActivity = 0.0;
      for (int k = 0; k < ct.varIndex.size(); ++k) {
        final MPModel.Variable v = model.variables.get(ct.varIndex.get(k));
        final double c = ct.coefficient.get(k);
        if (c > 0) {
          minActivity += c * v.lowerBound;
          maxActivity += c * v.upperBound;
        } else if (c < 0) {
          minActivity += c * v.upperBound;
          maxActivity += c * v.lowerBound;
        }
      }
      final boolean positive = ind.varValue != 0;
      if (ct.upperBound != Double.POSITIVE_INFINITY) {
        if (!Double.isFinite(maxActivity)) {
          throw new IllegalArgumentException("The enforced constraint "
              + (ind.name == null ? "" : ind.name)
              + " needs finite variable bounds for its big-M reformulation.");
        }
        final double bigM = Math.max(0.0, maxActivity - ct.upperBound);
        final MPModel.Constraint c = new MPModel.Constraint();
        c.name = ind.name == null ? null : ind.name + "_ub";
        c.varIndex.addAll(ct.varIndex);
        c.coefficient.addAll(ct.coefficient);
        c.varIndex.add(ind.varIndex);
        if (positive) {
          // a.x <= ub + M (1 - z)
          c.coefficient.add(bigM);
          c.upperBound = ct.upperBound + bigM;
        } else {
          // a.x <= ub + M z
          c.coefficient.add(-bigM);
          c.upperBound = ct.upperBound;
        }
        result.constraints.add(c);
      }
      if (ct.lowerBound != Double.NEGATIVE_INFINITY) {
        if (!Double.isFinite(minActivity)) {
          throw new IllegalArgumentException("The enforced constraint "
              + (ind.name == null ? "" : ind.name)
              + " needs finite variable bounds for its big-M reformulation.");
        }
        final double bigM = Math.max(0.0, ct.lowerBound - minActivity);
        final MPModel.Constraint c = new MPModel.Constraint();
        c.name = ind.name == null ? null : ind.name + "_lb";
        c.varIndex.addAll(ct.varIndex);
        c.coefficient.addAll(ct.coefficient);
        c.varIndex.add(ind.varIndex);
        if (positive) {
          // a.x >= lb - M (1 - z)
          c.coefficient.add(-bigM);
          c.lowerBound = ct.lowerBound - bigM;
        } else {
          // a.x >= lb - M z
          c.coefficient.add(bigM);
          c.lowerBound = ct.lowerBound;
        }
        result.constraints.add(c);
      }
    }
    return result;
  }

  private void log(String message) {
    if (options.logSearchProgress) {
      LOGGER.info(message);
      if (options.logCallback != null) {
        options.logCallback.accept(message);
      }
    }
  }

  /** Solves the model (integrality of the variables is taken from the model). */
  public MPSolverInterface.SolveResult solve(MPModel inputModel) {
    final long start = System.nanoTime();
    if (options.logSearchProgress) {
      int numIntegerVariables = 0;
      for (MPModel.Variable v : inputModel.variables) {
        if (v.isInteger) {
          ++numIntegerVariables;
        }
      }
      log("Model " + (inputModel.name == null ? "" : "'" + inputModel.name + "' ") + "has "
          + inputModel.variables.size() + " variables (" + numIntegerVariables + " integer), "
          + inputModel.constraints.size() + " constraints and "
          + inputModel.generalConstraints.size() + " general constraints");
    }
    model = linearizeIndicatorConstraints(inputModel);
    modelIsOwned = model != inputModel;
    timeLimit = new TimeLimit(options.timeLimitSeconds, Double.POSITIVE_INFINITY);
    timeLimit.registerExternalBooleanAsLimit(options.interrupt);
    numNodes = 0;
    numIterations = 0;
    searchIncomplete = false;
    skippedNodeBound = Double.POSITIVE_INFINITY;
    incumbent = null;
    incumbentObjective = Double.POSITIVE_INFINITY;
    final MPSolverInterface.SolveResult result = new MPSolverInterface.SolveResult();
    numVars = model.variables.size();
    sign = model.maximize ? -1.0 : 1.0;
    objective = new double[numVars];
    isInteger = new boolean[numVars];
    rootLb = new double[numVars];
    rootUb = new double[numVars];
    objectiveOffset = sign * model.objectiveOffset;
    boolean boundsInfeasible = false;
    for (int j = 0; j < numVars; ++j) {
      final MPModel.Variable v = model.variables.get(j);
      objective[j] = sign * v.objectiveCoefficient;
      isInteger[j] = v.isInteger;
      double lb = v.lowerBound;
      double ub = v.upperBound;
      if (v.isInteger) {
        lb = Math.ceil(lb - INTEGRALITY_TOLERANCE);
        ub = Math.floor(ub + INTEGRALITY_TOLERANCE);
      }
      if (lb > ub || Double.isNaN(lb) || Double.isNaN(ub)) {
        boundsInfeasible = true;
      }
      rootLb[j] = lb;
      rootUb[j] = ub;
    }
    for (MPModel.Constraint c : model.constraints) {
      if (c.lowerBound > c.upperBound) {
        boundsInfeasible = true;
      }
    }
    result.variableValues = new double[numVars];
    result.nodes = 0;
    if (boundsInfeasible) {
      result.status = MPSolver.ResultStatus.INFEASIBLE;
      result.bestObjectiveBound = sign * Double.POSITIVE_INFINITY;
      result.objectiveValue = 0.0;
      result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
      return result;
    }
    objectiveStep = computeObjectiveStep();
    pseudoCostDownSum = new double[numVars];
    pseudoCostDownCount = new int[numVars];
    pseudoCostUpSum = new double[numVars];
    pseudoCostUpCount = new int[numVars];
    numCuts = 0;
    numOriginalConstraints = model.constraints.size();
    useHint();
    if (options.usePresolve) {
      tightenIntegerRows();
      addSymmetryBreakingRows();
    }
    buildScaledLp();

    final GlopParameters nodeParameters = options.lpParameters.copy();
    nodeParameters.setUseDualSimplex(true);
    nodeParameters.setUsePreprocessing(false);
    simplex.setParameters(nodeParameters);

    final Node root = new Node(null);
    root.bound = Double.NEGATIVE_INFINITY;
    root.id = nodeCounter++;
    final ProblemStatus rootStatus = solveNode(root);
    ++numNodes;
    if (rootStatus == ProblemStatus.DUAL_INFEASIBLE || rootStatus == ProblemStatus.PRIMAL_UNBOUNDED
        || rootStatus == ProblemStatus.INFEASIBLE_OR_UNBOUNDED) {
      result.status = rootStatus == ProblemStatus.INFEASIBLE_OR_UNBOUNDED
          ? MPSolver.ResultStatus.INFEASIBLE
          : MPSolver.ResultStatus.UNBOUNDED;
      result.iterations = numIterations;
      result.nodes = numNodes;
      result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
      return result;
    }
    if (rootStatus != ProblemStatus.OPTIMAL) {
      if (rootStatus == ProblemStatus.PRIMAL_INFEASIBLE || rootStatus == ProblemStatus.DUAL_UNBOUNDED) {
        result.status = MPSolver.ResultStatus.INFEASIBLE;
      } else if (timeLimit.limitReached()) {
        result.status = MPSolver.ResultStatus.NOT_SOLVED;
      } else {
        result.status = MPSolver.ResultStatus.ABNORMAL;
      }
      result.iterations = numIterations;
      result.nodes = numNodes;
      result.bestObjectiveBound = sign * Double.POSITIVE_INFINITY;
      result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
      return result;
    }
    log("Root LP bound: " + sign * nodeObjective);
    runRoundingHeuristic(root);
    if (!addRootCuts(root)) {
      // the cuts made the root LP infeasible: no integer solution
      result.status = incumbent == null ? MPSolver.ResultStatus.INFEASIBLE
          : MPSolver.ResultStatus.OPTIMAL;
      if (incumbent != null) {
        result.variableValues = incumbent.clone();
        result.objectiveValue = sign * incumbentObjective;
        result.bestObjectiveBound = result.objectiveValue;
      } else {
        result.bestObjectiveBound = sign * Double.POSITIVE_INFINITY;
      }
      result.iterations = numIterations;
      result.nodes = numNodes;
      result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
      return result;
    }
    double globalBound = nodeObjective;

    final PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> {
      if (a.bound != b.bound) {
        return Double.compare(a.bound, b.bound);
      }
      if (a.depth != b.depth) {
        return Integer.compare(b.depth, a.depth);
      }
      return Long.compare(a.id, b.id);
    });
    // the depth-first stack used until the first solution is found
    final ArrayDeque<Node> dive = new ArrayDeque<>();
    Node next = processSolvedNode(root, open, dive);
    while (true) {
      if (timeLimit.limitReached() || numNodes >= options.maxNodes) {
        searchIncomplete = true;
        break;
      }
      if (next == null && incumbent == null && !dive.isEmpty()) {
        next = dive.pop();
      }
      if (next == null) {
        while (!dive.isEmpty()) {
          open.add(dive.pop());
        }
        // prune the open nodes with the incumbent and take the best one
        while (!open.isEmpty() && canPrune(open.peek().bound)) {
          open.poll();
        }
        if (open.isEmpty()) {
          break;
        }
        if (incumbent != null && gapReached(open.peek().bound)) {
          break;
        }
        next = open.poll();
      }
      if (canPrune(next.bound)) {
        next = null;
        continue;
      }
      final ProblemStatus status = solveNode(next);
      ++numNodes;
      if (options.logSearchProgress && numNodes % 100000 == 0) {
        log("Nodes: " + numNodes + ", open: " + (open.size() + dive.size()) + ", best bound: "
            + (open.isEmpty() ? "-" : String.valueOf(sign * roundBound(open.peek().bound)))
            + ", incumbent: " + (incumbent == null ? "-" : String.valueOf(sign * incumbentObjective)));
      }
      if (status == ProblemStatus.OPTIMAL) {
        updatePseudoCost(next);
        if (canPrune(nodeObjective)) {
          next = null;
          continue;
        }
        if (numNodes % HEURISTIC_PERIOD == 0) {
          runRoundingHeuristic(next);
        }
        next = processSolvedNode(next, open, dive);
      } else if (status == ProblemStatus.PRIMAL_INFEASIBLE
          || status == ProblemStatus.DUAL_UNBOUNDED) {
        next = null;
      } else {
        if (timeLimit.limitReached()) {
          // the node is not finished
          open.add(next);
          next = null;
          searchIncomplete = true;
          break;
        }
        log("Node LP failed with status " + status + ", the node is skipped.");
        searchIncomplete = true;
        skippedNodeBound = Math.min(skippedNodeBound, next.bound);
        next = null;
      }
    }
    while (!dive.isEmpty()) {
      open.add(dive.pop());
    }
    if (next != null && searchIncomplete) {
      open.add(next);
    }
    double lowerBound = Double.POSITIVE_INFINITY;
    for (Node node : open) {
      lowerBound = Math.min(lowerBound, roundBound(node.bound));
    }
    lowerBound = Math.min(lowerBound, roundBound(skippedNodeBound));
    final boolean complete = open.isEmpty() && !searchIncomplete;
    result.iterations = numIterations;
    result.nodes = numNodes;
    if (incumbent != null) {
      lowerBound = Math.min(lowerBound, incumbentObjective);
      final boolean optimal = complete || gapReached(lowerBound);
      result.status = optimal ? MPSolver.ResultStatus.OPTIMAL : MPSolver.ResultStatus.FEASIBLE;
      result.variableValues = incumbent.clone();
      result.objectiveValue = sign * incumbentObjective;
      result.bestObjectiveBound = sign * lowerBound;
    } else if (complete) {
      result.status = MPSolver.ResultStatus.INFEASIBLE;
      result.bestObjectiveBound = sign * Double.POSITIVE_INFINITY;
    } else {
      result.status = MPSolver.ResultStatus.NOT_SOLVED;
      result.bestObjectiveBound =
          sign * (Double.isFinite(lowerBound) ? Math.max(lowerBound, globalBound) : globalBound);
    }
    result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
    log("Branch and bound: " + result.status + ", " + numNodes + " nodes, " + numIterations
        + " iterations, objective " + result.objectiveValue + ", bound "
        + result.bestObjectiveBound);
    return result;
  }

  private static long gcd(long a, long b) {
    while (b != 0) {
      final long t = a % b;
      a = b;
      b = t;
    }
    return a;
  }

  /**
   * Returns the step of the objective: a positive number s such that the objective value (minus
   * the offset) of every solution is a multiple of s, or 0 if there is none. This is the case when
   * only integer variables have nonzero objective coefficients and these are rational with a small
   * denominator: s is then the greatest common divisor of the coefficients.
   */
  private double computeObjectiveStep() {
    boolean any = false;
    for (int j = 0; j < numVars; ++j) {
      if (objective[j] != 0.0) {
        if (!isInteger[j]) {
          return 0.0;
        }
        any = true;
      }
    }
    if (!any) {
      return 0.0;
    }
    for (int denominator = 1; denominator <= 1000; ++denominator) {
      long g = 0;
      boolean integral = true;
      for (int j = 0; j < numVars && integral; ++j) {
        if (objective[j] == 0.0) {
          continue;
        }
        final double scaled = Math.abs(objective[j]) * denominator;
        final double rounded = Math.rint(scaled);
        if (rounded > 1e15 || Math.abs(scaled - rounded) > 1e-9 * Math.max(1.0, scaled)) {
          integral = false;
        } else {
          g = gcd(g, (long) rounded);
        }
      }
      if (integral && g > 0) {
        return (double) g / denominator;
      }
    }
    return 0.0;
  }

  private double roundBound(double bound) {
    if (objectiveStep > 0.0 && Double.isFinite(bound)) {
      return Math.ceil((bound - objectiveOffset) / objectiveStep - 1e-6) * objectiveStep
          + objectiveOffset;
    }
    return bound;
  }

  private boolean gapReached(double bound) {
    if (incumbent == null) {
      return false;
    }
    final double b = roundBound(bound);
    final double gap = incumbentObjective - b;
    if (gap <= 1e-9 * Math.max(1.0, Math.abs(incumbentObjective))) {
      return true;
    }
    return gap <= options.relativeMipGap * Math.max(Math.abs(incumbentObjective), Math.abs(b));
  }

  private boolean canPrune(double bound) {
    if (incumbent == null) {
      return false;
    }
    final double b = roundBound(bound);
    if (objectiveStep > 0.0) {
      return b >= incumbentObjective - 1e-6 * objectiveStep;
    }
    return b >= incumbentObjective - 1e-9 * Math.max(1.0, Math.abs(incumbentObjective))
        || gapReached(b);
  }

  /**
   * Branches on the solved node: returns the child to process next in a dive (or null) and adds
   * the other children to the open set.
   */
  private void updatePseudoCost(Node node) {
    if (node.branchVar < 0 || node.branchDistance <= 0.0) {
      return;
    }
    final double gain = Math.max(0.0, nodeObjective - node.bound) / node.branchDistance;
    if (!Double.isFinite(gain)) {
      return;
    }
    if (node.branchUp) {
      pseudoCostUpSum[node.branchVar] += gain;
      pseudoCostUpCount[node.branchVar]++;
    } else {
      pseudoCostDownSum[node.branchVar] += gain;
      pseudoCostDownCount[node.branchVar]++;
    }
  }

  /**
   * Chooses the branching variable among the fractional integer variables of the node LP solution
   * with the product rule on the pseudo costs; the variables without history get the average
   * pseudo cost (1 when there is none at all, which selects the most fractional variable).
   * Returns -1 if the solution is integral.
   */
  private int selectBranchingVariable() {
    double downTotal = 0.0;
    int downCount = 0;
    double upTotal = 0.0;
    int upCount = 0;
    for (int j = 0; j < numVars; ++j) {
      if (pseudoCostDownCount[j] > 0) {
        downTotal += pseudoCostDownSum[j] / pseudoCostDownCount[j];
        ++downCount;
      }
      if (pseudoCostUpCount[j] > 0) {
        upTotal += pseudoCostUpSum[j] / pseudoCostUpCount[j];
        ++upCount;
      }
    }
    final double downDefault = downCount > 0 ? downTotal / downCount : 1.0;
    final double upDefault = upCount > 0 ? upTotal / upCount : 1.0;
    int branchVar = -1;
    double bestScore = -1.0;
    double bestFractionality = -1.0;
    for (int j = 0; j < numVars; ++j) {
      if (!isInteger[j]) {
        continue;
      }
      final double v = nodeValues[j];
      final double frac = v - Math.floor(v);
      if (frac <= INTEGRALITY_TOLERANCE || frac >= 1.0 - INTEGRALITY_TOLERANCE) {
        continue;
      }
      final double down = (pseudoCostDownCount[j] > 0
          ? pseudoCostDownSum[j] / pseudoCostDownCount[j] : downDefault) * frac;
      final double up = (pseudoCostUpCount[j] > 0
          ? pseudoCostUpSum[j] / pseudoCostUpCount[j] : upDefault) * (1.0 - frac);
      final double score = Math.max(down, 1e-6) * Math.max(up, 1e-6);
      final double fractionality = Math.min(frac, 1.0 - frac);
      if (score > bestScore * (1.0 + 1e-9)
          || (score >= bestScore * (1.0 - 1e-9) && fractionality > bestFractionality)) {
        bestScore = score;
        bestFractionality = fractionality;
        branchVar = j;
      }
    }
    return branchVar;
  }

  private Node processSolvedNode(Node node, PriorityQueue<Node> open, ArrayDeque<Node> dive) {
    // the propagated bounds of the node LP which was just solved
    final double[] lb = nodeLb;
    final double[] ub = nodeUb;
    final int branchVar = selectBranchingVariable();
    if (branchVar < 0) {
      // integral LP solution
      final double[] candidate = nodeValues.clone();
      for (int j = 0; j < numVars; ++j) {
        if (isInteger[j]) {
          candidate[j] = Math.rint(candidate[j]);
        }
      }
      tryIncumbent(candidate);
      return null;
    }
    final byte[] state = encodeState(simplex.getState());
    // reduced cost fixing for the children
    final Node down = new Node(node);
    final Node up = new Node(node);
    if (incumbent != null && nodeReducedCosts != null) {
      final double slack = incumbentObjective - nodeObjective;
      for (int j = 0; j < numVars; ++j) {
        if (!isInteger[j] || j == branchVar) {
          continue;
        }
        final double d = nodeReducedCosts[j];
        if (nodeStatuses[j] == VariableStatus.AT_LOWER_BOUND && d > 1e-9 && Double.isFinite(lb[j])) {
          final double newUb = lb[j] + Math.floor(slack / d + 1e-9);
          if (newUb < ub[j]) {
            down.addChange(j, lb[j], newUb);
            up.addChange(j, lb[j], newUb);
          }
        } else if (nodeStatuses[j] == VariableStatus.AT_UPPER_BOUND && d < -1e-9
            && Double.isFinite(ub[j])) {
          final double newLb = ub[j] - Math.floor(slack / -d + 1e-9);
          if (newLb > lb[j]) {
            down.addChange(j, newLb, ub[j]);
            up.addChange(j, newLb, ub[j]);
          }
        }
      }
    }
    final double value = nodeValues[branchVar];
    down.addChange(branchVar, lb[branchVar], Math.floor(value));
    up.addChange(branchVar, Math.ceil(value), ub[branchVar]);
    down.bound = nodeObjective;
    up.bound = nodeObjective;
    down.parentState = state;
    up.parentState = state;
    down.id = nodeCounter++;
    up.id = nodeCounter++;
    final double frac = value - Math.floor(value);
    down.branchVar = branchVar;
    down.branchUp = false;
    down.branchDistance = frac;
    up.branchVar = branchVar;
    up.branchUp = true;
    up.branchDistance = 1.0 - frac;
    final boolean preferUp = frac >= 0.5;
    final Node preferred = preferUp ? up : down;
    final Node other = preferUp ? down : up;
    if (incumbent == null) {
      dive.push(other);
      return preferred;
    }
    open.add(other);
    // plunge into the preferred child while its bound stays close to the best open bound
    if (!open.isEmpty()) {
      final double best = open.peek().bound;
      if (nodeObjective <= best + 0.5 * (incumbentObjective - best)) {
        return preferred;
      }
    }
    open.add(preferred);
    return null;
  }

  private void ensureModelIsOwned() {
    if (!modelIsOwned) {
      model = model.copy();
      modelIsOwned = true;
    }
  }

  private boolean isBinary(int var) {
    return isInteger[var] && rootLb[var] == 0.0 && rootUb[var] == 1.0;
  }

  /**
   * Rounds the bounds of the rows over integer variables with integer coefficients to multiples
   * of the greatest common divisor of the coefficients (the activity of an integer solution is
   * such a multiple).
   */
  private void tightenIntegerRows() {
    for (int r = 0; r < model.constraints.size(); ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      long g = 0;
      boolean integral = true;
      for (int k = 0; k < c.varIndex.size() && integral; ++k) {
        final double a = c.coefficient.get(k);
        if (a == 0.0) {
          continue;
        }
        if (!isInteger[c.varIndex.get(k)] || Math.abs(a) > 1e15 || a != Math.rint(a)) {
          integral = false;
        } else {
          g = gcd(g, (long) Math.abs(a));
        }
      }
      if (!integral || g == 0) {
        continue;
      }
      final double lb = c.lowerBound;
      final double ub = c.upperBound;
      final double newLb = lb == Double.NEGATIVE_INFINITY ? lb : Math.ceil(lb / g - 1e-6) * g;
      final double newUb = ub == Double.POSITIVE_INFINITY ? ub : Math.floor(ub / g + 1e-6) * g;
      if (newLb > lb || newUb < ub) {
        ensureModelIsOwned();
        final MPModel.Constraint t = model.constraints.get(r);
        t.lowerBound = Math.max(lb, newLb);
        t.upperBound = Math.min(ub, newUb);
      }
    }
  }

  /** A row key independent of the order of its terms, used to compare constraint sets. */
  private static String rowKey(MPModel.Constraint c, int[] permutation) {
    final int size = c.varIndex.size();
    final double[] coefficients = new double[size];
    final Integer[] order = new Integer[size];
    final int[] vars = new int[size];
    for (int k = 0; k < size; ++k) {
      vars[k] = permutation == null ? c.varIndex.get(k) : permutation[c.varIndex.get(k)];
      coefficients[k] = c.coefficient.get(k);
      order[k] = k;
    }
    Arrays.sort(order, (p, q) -> Integer.compare(vars[p], vars[q]));
    final StringBuilder key = new StringBuilder();
    key.append(c.lowerBound).append('|').append(c.upperBound).append('|');
    for (int k = 0; k < size; ++k) {
      if (coefficients[order[k]] == 0.0) {
        continue;
      }
      key.append(vars[order[k]]).append(':').append(coefficients[order[k]]).append(',');
    }
    return key.toString();
  }

  /**
   * Returns true if the variable permutation (an involution) maps the model onto itself: same
   * bounds, integrality and objective coefficients, and the same set of constraints.
   */
  private boolean isAutomorphism(int[] permutation, List<List<Integer>> rowsOfVar) {
    final Set<Integer> touched = new HashSet<>();
    for (int j = 0; j < numVars; ++j) {
      final int p = permutation[j];
      if (p == j) {
        continue;
      }
      if (isInteger[j] != isInteger[p] || rootLb[j] != rootLb[p] || rootUb[j] != rootUb[p]
          || objective[j] != objective[p]) {
        return false;
      }
      touched.addAll(rowsOfVar.get(j));
    }
    final java.util.Map<String, Integer> balance = new java.util.HashMap<>();
    for (int r : touched) {
      final MPModel.Constraint c = model.constraints.get(r);
      balance.merge(rowKey(c, null), 1, Integer::sum);
      balance.merge(rowKey(c, permutation), -1, Integer::sum);
    }
    for (int v : balance.values()) {
      if (v != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Detects a packing orbitope and adds symmetry breaking rows for it. The structure is a family
   * of disjoint packing rows (sum of k binary variables at most 1, the rows of the orbitope) and k
   * disjoint "column rows" each containing exactly one variable of every family row, such that
   * exchanging two columns (their variables, and the other variables of the column rows matched
   * by coefficient) is an automorphism of the model. The columns can then be permuted freely and
   * every solution has an equivalent one whose columns are in decreasing lexicographic order
   * (rows taken in the order of the model). In such a solution, the variable of row f in column
   * c &gt; 0 can only be 1 if column c - 1 has a 1 in an earlier row: x[f][c] &lt;=
   * sum(f' &lt; f) x[f'][c - 1], and the variables of the first row are 0 outside column 0.
   */
  private void addSymmetryBreakingRows() {
    final int numRows = model.constraints.size();
    if (numRows == 0) {
      return;
    }
    final List<List<Integer>> rowsOfVar = new ArrayList<>();
    for (int j = 0; j < numVars; ++j) {
      rowsOfVar.add(new ArrayList<>());
    }
    for (int r = 0; r < numRows; ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      final Set<Integer> seen = new HashSet<>();
      for (int k = 0; k < c.varIndex.size(); ++k) {
        if (!seen.add(c.varIndex.get(k))) {
          return; // duplicate terms: no detection
        }
        rowsOfVar.get(c.varIndex.get(k)).add(r);
      }
    }
    // the packing rows grouped by size
    final java.util.TreeMap<Integer, List<Integer>> packingRowsBySize = new java.util.TreeMap<>();
    for (int r = 0; r < numRows; ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      final int size = c.varIndex.size();
      if (size < 2) {
        continue;
      }
      final double a = c.coefficient.get(0);
      boolean packing = a > 0.0 && c.upperBound <= a * (1.0 + 1e-9);
      for (int k = 0; k < size && packing; ++k) {
        packing = c.coefficient.get(k) == a && isBinary(c.varIndex.get(k));
      }
      if (packing) {
        packingRowsBySize.computeIfAbsent(size, s -> new ArrayList<>()).add(r);
      }
    }
    for (java.util.Map.Entry<Integer, List<Integer>> entry : packingRowsBySize.entrySet()) {
      if (tryOrbitope(entry.getKey(), entry.getValue(), rowsOfVar)) {
        return;
      }
    }
  }

  private boolean tryOrbitope(int k, List<Integer> candidates, List<List<Integer>> rowsOfVar) {
    // a family of disjoint packing rows
    final int[] familyRowOfVar = new int[numVars];
    Arrays.fill(familyRowOfVar, -1);
    final List<Integer> family = new ArrayList<>();
    for (int r : candidates) {
      final MPModel.Constraint c = model.constraints.get(r);
      boolean disjoint = true;
      for (int v : c.varIndex) {
        disjoint &= familyRowOfVar[v] < 0;
      }
      if (!disjoint) {
        continue;
      }
      for (int v : c.varIndex) {
        familyRowOfVar[v] = family.size();
      }
      family.add(r);
    }
    final int numFamilyRows = family.size();
    if (numFamilyRows < 2) {
      return false;
    }
    // the column rows
    final int[] columnOfVar = new int[numVars];
    Arrays.fill(columnOfVar, -1);
    final List<Integer> columns = new ArrayList<>();
    final Set<Integer> familySet = new HashSet<>(family);
    for (int r = 0; r < model.constraints.size() && columns.size() < k; ++r) {
      if (familySet.contains(r)) {
        continue;
      }
      final MPModel.Constraint c = model.constraints.get(r);
      final boolean[] hit = new boolean[numFamilyRows];
      int numHits = 0;
      boolean ok = true;
      for (int v : c.varIndex) {
        final int f = familyRowOfVar[v];
        if (f < 0) {
          continue;
        }
        if (hit[f] || columnOfVar[v] >= 0) {
          ok = false;
          break;
        }
        hit[f] = true;
        ++numHits;
      }
      if (!ok || numHits != numFamilyRows) {
        continue;
      }
      for (int v : c.varIndex) {
        if (familyRowOfVar[v] >= 0) {
          columnOfVar[v] = columns.size();
        }
      }
      columns.add(r);
    }
    if (columns.size() != k) {
      return false;
    }
    final int[][] grid = new int[numFamilyRows][k];
    for (int f = 0; f < numFamilyRows; ++f) {
      for (int v : model.constraints.get(family.get(f)).varIndex) {
        grid[f][columnOfVar[v]] = v;
      }
    }
    // the other variables of the column rows, sorted by coefficient (distinct coefficients)
    final int[][] extras = new int[k][];
    for (int c = 0; c < k; ++c) {
      final MPModel.Constraint row = model.constraints.get(columns.get(c));
      final List<Integer> vars = new ArrayList<>();
      final List<Double> coefficients = new ArrayList<>();
      for (int t = 0; t < row.varIndex.size(); ++t) {
        if (familyRowOfVar[row.varIndex.get(t)] < 0) {
          vars.add(row.varIndex.get(t));
          coefficients.add(row.coefficient.get(t));
        }
      }
      final Integer[] order = new Integer[vars.size()];
      for (int t = 0; t < order.length; ++t) {
        order[t] = t;
      }
      Arrays.sort(order, (p, q) -> Double.compare(coefficients.get(p), coefficients.get(q)));
      extras[c] = new int[order.length];
      for (int t = 0; t < order.length; ++t) {
        extras[c][t] = vars.get(order[t]);
        if (t > 0 && coefficients.get(order[t]).equals(coefficients.get(order[t - 1]))) {
          return false;
        }
      }
      if (c > 0 && extras[c].length != extras[0].length) {
        return false;
      }
    }
    // check that the adjacent column exchanges, which generate all permutations, are symmetries
    for (int c = 0; c + 1 < k; ++c) {
      final int[] permutation = new int[numVars];
      for (int j = 0; j < numVars; ++j) {
        permutation[j] = j;
      }
      for (int f = 0; f < numFamilyRows; ++f) {
        permutation[grid[f][c]] = grid[f][c + 1];
        permutation[grid[f][c + 1]] = grid[f][c];
      }
      for (int t = 0; t < extras[c].length; ++t) {
        final int a = extras[c][t];
        final int b = extras[c + 1][t];
        if (permutation[a] != a || permutation[b] != b) {
          return false;
        }
        permutation[a] = b;
        permutation[b] = a;
      }
      if (!isAutomorphism(permutation, rowsOfVar)) {
        return false;
      }
    }
    ensureModelIsOwned();
    int numAdded = 0;
    for (int c = 1; c < k; ++c) {
      rootUb[grid[0][c]] = 0.0;
    }
    for (int f = 1; f < numFamilyRows; ++f) {
      for (int c = 1; c < k; ++c) {
        final MPModel.Constraint row = new MPModel.Constraint();
        row.lowerBound = Double.NEGATIVE_INFINITY;
        row.upperBound = 0.0;
        row.varIndex.add(grid[f][c]);
        row.coefficient.add(1.0);
        for (int g = 0; g < f; ++g) {
          row.varIndex.add(grid[g][c - 1]);
          row.coefficient.add(-1.0);
        }
        model.constraints.add(row);
        ++numAdded;
      }
    }
    log("Symmetry: " + k + " interchangeable columns over " + numFamilyRows
        + " packing rows, " + numAdded + " symmetry breaking rows added.");
    return true;
  }

  /**
   * Root cutting plane loop: adds the violated knapsack cover cuts of the current root LP solution
   * to the (internal copy of the) model and solves the root LP again, until no cut is found or the
   * bound stalls. Returns false if the root LP became infeasible.
   */
  private boolean addRootCuts(Node root) {
    if (!options.useCuts) {
      return true;
    }
    final Set<String> known = new HashSet<>();
    double lastObjective = nodeObjective;
    for (int round = 0; round < MAX_CUT_ROUNDS && !timeLimit.limitReached(); ++round) {
      final List<MPModel.Constraint> cuts = separateCoverCuts(nodeValues, known);
      if (cuts.isEmpty()) {
        break;
      }
      if (!modelIsOwned) {
        model = model.copy();
        modelIsOwned = true;
      }
      final int numRows = model.constraints.size();
      model.constraints.addAll(cuts);
      buildScaledLp();
      simplex.clearStateForNextSolve();
      lastSolvedNode = null;
      ProblemStatus status = solveNode(root);
      if (status == ProblemStatus.PRIMAL_INFEASIBLE || status == ProblemStatus.DUAL_UNBOUNDED) {
        return false;
      }
      if (status != ProblemStatus.OPTIMAL) {
        // numerical trouble: drop the cuts of this round
        model.constraints.subList(numRows, model.constraints.size()).clear();
        buildScaledLp();
        simplex.clearStateForNextSolve();
        lastSolvedNode = null;
        solveNode(root);
        break;
      }
      numCuts += cuts.size();
      runRoundingHeuristic(root);
      if (nodeObjective - lastObjective <= 1e-6 * Math.max(1.0, Math.abs(nodeObjective))) {
        break;
      }
      lastObjective = nodeObjective;
    }
    if (numCuts > 0) {
      log("Root LP bound after " + numCuts + " cover cuts: " + sign * nodeObjective);
    }
    return true;
  }

  /**
   * Separates lifted knapsack cover inequalities from the rows whose variables are all binary.
   * A row is written as sum a_k y_k &lt;= b with a_k &gt; 0, where y_k is x_k or its complement
   * 1 - x_k. A cover C (sum over C of a_k &gt; b) gives sum over E(C) of y_k &lt;= |C| - 1 where
   * E(C) adds the variables whose coefficient is at least the largest one of C.
   */
  private List<MPModel.Constraint> separateCoverCuts(double[] x, Set<String> known) {
    final List<MPModel.Constraint> cuts = new ArrayList<>();
    final int numRows = model.constraints.size();
    for (int r = 0; r < numRows && cuts.size() < MAX_CUTS_PER_ROUND; ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      final int size = c.varIndex.size();
      if (size < 2) {
        continue;
      }
      boolean binary = true;
      for (int k = 0; k < size && binary; ++k) {
        final int j = c.varIndex.get(k);
        // binary, or fixed to zero
        binary = isInteger[j] && rootLb[j] == 0.0 && (rootUb[j] == 1.0 || rootUb[j] == 0.0);
      }
      if (!binary) {
        continue;
      }
      if (c.upperBound != Double.POSITIVE_INFINITY) {
        separateCover(c, 1.0, c.upperBound, x, known, cuts);
      }
      if (c.lowerBound != Double.NEGATIVE_INFINITY) {
        separateCover(c, -1.0, -c.lowerBound, x, known, cuts);
      }
    }
    return cuts;
  }

  private void separateCover(MPModel.Constraint c, double factor, double rhs, double[] x,
      Set<String> known, List<MPModel.Constraint> cuts) {
    final int size = c.varIndex.size();
    final int[] var = new int[size];
    final double[] a = new double[size];
    final double[] y = new double[size];
    final boolean[] complemented = new boolean[size];
    double b = rhs;
    double total = 0.0;
    int n = 0;
    for (int k = 0; k < size; ++k) {
      final double coefficient = factor * c.coefficient.get(k);
      if (coefficient == 0.0 || rootUb[c.varIndex.get(k)] == 0.0) {
        continue;
      }
      var[n] = c.varIndex.get(k);
      if (coefficient < 0.0) {
        complemented[n] = true;
        a[n] = -coefficient;
        b -= coefficient;
        y[n] = 1.0 - x[var[n]];
      } else {
        a[n] = coefficient;
        y[n] = x[var[n]];
      }
      total += a[n];
      ++n;
    }
    final double eps = 1e-9 * Math.max(1.0, Math.abs(b));
    if (b < 0.0 || total <= b + eps) {
      return;
    }
    // greedy cover: smallest (1 - y) / a first
    final Integer[] order = new Integer[n];
    for (int k = 0; k < n; ++k) {
      order[k] = k;
    }
    Arrays.sort(order, (p, q) -> {
      final int cmp = Double.compare((1.0 - y[p]) / a[p], (1.0 - y[q]) / a[q]);
      if (cmp != 0) {
        return cmp;
      }
      return Double.compare(a[q], a[p]);
    });
    final boolean[] inCover = new boolean[n];
    double sum = 0.0;
    int coverSize = 0;
    for (int t = 0; t < n && sum <= b + eps; ++t) {
      inCover[order[t]] = true;
      sum += a[order[t]];
      ++coverSize;
    }
    if (sum <= b + eps) {
      return;
    }
    // make it minimal, removing the smallest LP values first
    final Integer[] byValue = new Integer[n];
    for (int k = 0; k < n; ++k) {
      byValue[k] = k;
    }
    Arrays.sort(byValue, (p, q) -> Double.compare(y[p], y[q]));
    for (int t = 0; t < n; ++t) {
      final int k = byValue[t];
      if (inCover[k] && sum - a[k] > b + eps) {
        inCover[k] = false;
        sum -= a[k];
        --coverSize;
      }
    }
    double maxA = 0.0;
    for (int k = 0; k < n; ++k) {
      if (inCover[k]) {
        maxA = Math.max(maxA, a[k]);
      }
    }
    double lhs = 0.0;
    final boolean[] inCut = new boolean[n];
    for (int k = 0; k < n; ++k) {
      if (inCover[k] || a[k] >= maxA) {
        inCut[k] = true;
        lhs += y[k];
      }
    }
    final double cutRhs = coverSize - 1;
    if (lhs <= cutRhs + 1e-4) {
      return;
    }
    final MPModel.Constraint cut = new MPModel.Constraint();
    cut.lowerBound = Double.NEGATIVE_INFINITY;
    double ub = cutRhs;
    final int[] sortedVars = new int[n];
    int m = 0;
    for (int k = 0; k < n; ++k) {
      if (inCut[k]) {
        sortedVars[m++] = complemented[k] ? -var[k] - 1 : var[k];
        if (complemented[k]) {
          ub -= 1.0;
        }
      }
    }
    Arrays.sort(sortedVars, 0, m);
    final StringBuilder key = new StringBuilder();
    for (int t = 0; t < m; ++t) {
      final int v = sortedVars[t];
      if (v < 0) {
        cut.varIndex.add(-v - 1);
        cut.coefficient.add(-1.0);
      } else {
        cut.varIndex.add(v);
        cut.coefficient.add(1.0);
      }
      key.append(v).append(',');
    }
    key.append(ub);
    cut.upperBound = ub;
    if (known.add(key.toString())) {
      cuts.add(cut);
    }
  }

  private static byte[] encodeState(BasisState state) {
    final byte[] result = new byte[state.statuses.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = (byte) state.statuses[i].ordinal();
    }
    return result;
  }

  private static BasisState decodeState(byte[] state) {
    final VariableStatus[] all = VariableStatus.values();
    final VariableStatus[] statuses = new VariableStatus[state.length];
    for (int i = 0; i < state.length; ++i) {
      statuses[i] = all[state[i]];
    }
    return new BasisState(statuses);
  }

  private void computeBounds(Node node, double[] lb, double[] ub) {
    System.arraycopy(rootLb, 0, lb, 0, numVars);
    System.arraycopy(rootUb, 0, ub, 0, numVars);
    final List<Node> chain = new ArrayList<>();
    for (Node n = node; n != null; n = n.parent) {
      chain.add(n);
    }
    for (int k = chain.size() - 1; k >= 0; --k) {
      final Node n = chain.get(k);
      for (int i = 0; i < n.changedVars.length; ++i) {
        final int j = n.changedVars[i];
        lb[j] = Math.max(lb[j], n.changedLb[i]);
        ub[j] = Math.min(ub[j], n.changedUb[i]);
      }
    }
  }

  private void buildScaledLp() {
    buildRowArrays();
    final LinearProgram lp = new LinearProgram();
    for (int j = 0; j < numVars; ++j) {
      final int col = lp.createNewVariable();
      lp.setVariableBounds(col, rootLb[j], rootUb[j]);
      lp.setObjectiveCoefficient(col, objective[j]);
    }
    for (MPModel.Constraint c : model.constraints) {
      final int row = lp.createNewConstraint();
      lp.setConstraintBounds(row, c.lowerBound, c.upperBound);
      for (int k = 0; k < c.varIndex.size(); ++k) {
        lp.setCoefficient(row, c.varIndex.get(k), c.coefficient.get(k));
      }
    }
    lp.setObjectiveOffset(objectiveOffset);
    lp.setMaximizationProblem(false);
    lp.cleanUp();
    final SparseMatrixScaler scaler = new SparseMatrixScaler();
    if (options.lpParameters.getUseScaling()) {
      lp.scale(scaler, options.lpParameters.getScalingMethod());
      costScalingFactor = lp.scaleObjective(options.lpParameters.getCostScaling());
      boundScalingFactor = lp.scaleBounds();
    } else {
      scaler.init(lp.getSparseMatrix());
      costScalingFactor = 1.0;
      boundScalingFactor = 1.0;
    }
    colScale = new double[numVars];
    for (int j = 0; j < numVars; ++j) {
      colScale[j] = scaler.colUnscalingFactor(j);
    }
    final int numRows = model.constraints.size();
    rowScaleDivisor = new double[numRows];
    for (int r = 0; r < numRows; ++r) {
      rowScaleDivisor[r] = scaler.rowUnscalingFactor(r) * boundScalingFactor;
    }
    scaledRowLbSource = rowLb.clone();
    scaledRowUbSource = rowUb.clone();
    scaledLp = lp;
  }

  private void setScaledBounds(double[] lb, double[] ub) {
    for (int j = 0; j < numVars; ++j) {
      scaledLp.setVariableBounds(j, lb[j] * colScale[j] / boundScalingFactor,
          ub[j] * colScale[j] / boundScalingFactor);
    }
  }

  private void buildRowArrays() {
    final int numRows = model.constraints.size();
    rowVars = new int[numRows][];
    rowCoefficients = new double[numRows][];
    rowLb = new double[numRows];
    rowUb = new double[numRows];
    for (int r = 0; r < numRows; ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      final int size = c.varIndex.size();
      rowVars[r] = new int[size];
      rowCoefficients[r] = new double[size];
      for (int k = 0; k < size; ++k) {
        rowVars[r][k] = c.varIndex.get(k);
        rowCoefficients[r][k] = c.coefficient.get(k);
      }
      rowLb[r] = c.lowerBound;
      rowUb[r] = c.upperBound;
    }
    integerRow = new boolean[numRows];
    for (int r = 0; r < numRows; ++r) {
      boolean integral = rowVars[r].length > 1;
      for (int k = 0; k < rowVars[r].length && integral; ++k) {
        final double a = rowCoefficients[r][k];
        integral = isInteger[rowVars[r][k]] && a == Math.rint(a) && Math.abs(a) <= SUBSET_SUM_LIMIT;
      }
      integerRow[r] = integral;
    }
  }

  /**
   * Returns the largest sum of a subset of the given positive integer weights which is at most
   * <code>capacity</code> (an integer at most SUBSET_SUM_LIMIT).
   */
  private static int maxSubsetSum(int[] weights, int numWeights, int capacity) {
    final int numWords = (capacity >> 6) + 1;
    final long[] reachable = new long[numWords];
    reachable[0] = 1L;
    int total = 0;
    for (int t = 0; t < numWeights; ++t) {
      final int w = weights[t];
      if (w > capacity) {
        continue;
      }
      total += w;
      // reachable |= reachable << w
      final int wordShift = w >> 6;
      final int bitShift = w & 63;
      for (int i = numWords - 1; i >= wordShift; --i) {
        long shifted = reachable[i - wordShift] << bitShift;
        if (bitShift != 0 && i - wordShift - 1 >= 0) {
          shifted |= reachable[i - wordShift - 1] >>> (64 - bitShift);
        }
        reachable[i] |= shifted;
      }
      if (total <= capacity) {
        continue;
      }
      if ((reachable[capacity >> 6] & (1L << (capacity & 63))) != 0) {
        return capacity;
      }
    }
    if (total <= capacity) {
      return total;
    }
    for (int s = capacity; s >= 0; --s) {
      if ((reachable[s >> 6] & (1L << (s & 63))) != 0) {
        return s;
      }
    }
    return 0;
  }

  /**
   * Tightens the bounds of a row over integer variables with integer coefficients, whose free
   * variables are binary: the activity can only take the values of the fixed part plus a subset
   * sum of the coefficients of the free variables. Returns true if a bound changed.
   */
  private boolean tightenRowBySubsetSum(int r, double[] lb, double[] ub, double[] curRowLb,
      double[] curRowUb, int[] weights) {
    final int[] vars = rowVars[r];
    final double[] a = rowCoefficients[r];
    double minActivity = 0.0;
    double maxActivity = 0.0;
    int numFree = 0;
    for (int k = 0; k < vars.length; ++k) {
      final int j = vars[k];
      final double c = a[k];
      if (c == 0.0) {
        continue;
      }
      if (lb[j] == ub[j]) {
        minActivity += c * lb[j];
        maxActivity += c * lb[j];
        continue;
      }
      if (ub[j] - lb[j] != 1.0) {
        return false;
      }
      if (c > 0.0) {
        minActivity += c * lb[j];
        maxActivity += c * ub[j];
      } else {
        minActivity += c * ub[j];
        maxActivity += c * lb[j];
      }
      weights[numFree++] = (int) Math.abs(c);
    }
    boolean changed = false;
    if (curRowUb[r] != Double.POSITIVE_INFINITY && maxActivity > curRowUb[r]) {
      final double capacity = Math.floor(curRowUb[r] - minActivity + 1e-6);
      if (capacity >= 0.0 && capacity <= SUBSET_SUM_LIMIT) {
        final int best = maxSubsetSum(weights, numFree, (int) capacity);
        if (best < capacity) {
          curRowUb[r] = minActivity + best;
          changed = true;
        }
      }
    }
    if (curRowLb[r] != Double.NEGATIVE_INFINITY && minActivity < curRowLb[r]) {
      final double capacity = Math.floor(maxActivity - curRowLb[r] + 1e-6);
      if (capacity >= 0.0 && capacity <= SUBSET_SUM_LIMIT) {
        final int best = maxSubsetSum(weights, numFree, (int) capacity);
        if (best < capacity) {
          curRowLb[r] = maxActivity - best;
          changed = true;
        }
      }
    }
    return changed;
  }

  /**
   * Bound propagation on the rows: tightens the bounds of the integer variables implied by the
   * minimal and maximal activities of the rows. Returns false if a row cannot be satisfied.
   */
  private boolean propagate(double[] lb, double[] ub, double[] curRowLb, double[] curRowUb) {
    final int numRows = rowVars.length;
    final int[] weights = new int[numVars];
    for (int pass = 0; pass < MAX_PROPAGATION_PASSES; ++pass) {
      boolean changed = false;
      for (int r = 0; r < numRows; ++r) {
        final int[] vars = rowVars[r];
        final double[] a = rowCoefficients[r];
        if (integerRow[r] && tightenRowBySubsetSum(r, lb, ub, curRowLb, curRowUb, weights)) {
          changed = true;
        }
        double minActivity = 0.0;
        double maxActivity = 0.0;
        boolean minFinite = true;
        boolean maxFinite = true;
        for (int k = 0; k < vars.length; ++k) {
          final double c = a[k];
          final int j = vars[k];
          if (c > 0.0) {
            minActivity += c * lb[j];
            maxActivity += c * ub[j];
          } else if (c < 0.0) {
            minActivity += c * ub[j];
            maxActivity += c * lb[j];
          }
        }
        if (Double.isNaN(minActivity) || Double.isInfinite(minActivity)) {
          minFinite = false;
        }
        if (Double.isNaN(maxActivity) || Double.isInfinite(maxActivity)) {
          maxFinite = false;
        }
        final double tolerance = FEASIBILITY_TOLERANCE * Math.max(1.0,
            Math.max(Math.abs(curRowLb[r]) == Double.POSITIVE_INFINITY ? 0.0 : Math.abs(curRowLb[r]),
                Math.abs(curRowUb[r]) == Double.POSITIVE_INFINITY ? 0.0 : Math.abs(curRowUb[r])));
        final boolean useUb = minFinite && curRowUb[r] != Double.POSITIVE_INFINITY;
        final boolean useLb = maxFinite && curRowLb[r] != Double.NEGATIVE_INFINITY;
        if (useUb && minActivity > curRowUb[r] + tolerance) {
          return false;
        }
        if (useLb && maxActivity < curRowLb[r] - tolerance) {
          return false;
        }
        if (!useUb && !useLb) {
          continue;
        }
        for (int k = 0; k < vars.length; ++k) {
          final int j = vars[k];
          final double c = a[k];
          if (!isInteger[j] || c == 0.0 || lb[j] == ub[j]) {
            continue;
          }
          double newLb = lb[j];
          double newUb = ub[j];
          if (useUb) {
            // c x_j <= rowUb - (minActivity - min contribution of j)
            final double slack = curRowUb[r] - minActivity;
            if (c > 0.0) {
              newUb = Math.min(newUb, Math.floor(lb[j] + slack / c + 1e-6));
            } else {
              newLb = Math.max(newLb, Math.ceil(ub[j] + slack / c - 1e-6));
            }
          }
          if (useLb) {
            // c x_j >= rowLb - (maxActivity - max contribution of j)
            final double slack = curRowLb[r] - maxActivity;
            if (c > 0.0) {
              newLb = Math.max(newLb, Math.ceil(ub[j] + slack / c - 1e-6));
            } else {
              newUb = Math.min(newUb, Math.floor(lb[j] + slack / c + 1e-6));
            }
          }
          if (newLb > newUb) {
            return false;
          }
          if (newLb > lb[j] || newUb < ub[j]) {
            // update the activities of this row
            if (c > 0.0) {
              minActivity += c * (newLb - lb[j]);
              maxActivity += c * (newUb - ub[j]);
            } else {
              minActivity += c * (newUb - ub[j]);
              maxActivity += c * (newLb - lb[j]);
            }
            lb[j] = newLb;
            ub[j] = newUb;
            changed = true;
          }
        }
      }
      if (!changed) {
        break;
      }
    }
    return true;
  }

  private ProblemStatus solveNode(Node node) {
    final double[] lb = new double[numVars];
    final double[] ub = new double[numVars];
    computeBounds(node, lb, ub);
    for (int j = 0; j < numVars; ++j) {
      if (lb[j] > ub[j]) {
        return ProblemStatus.PRIMAL_INFEASIBLE;
      }
    }
    final double[] curRowLb = rowLb.clone();
    final double[] curRowUb = rowUb.clone();
    if (!propagate(lb, ub, curRowLb, curRowUb)) {
      return ProblemStatus.PRIMAL_INFEASIBLE;
    }
    nodeLb = lb;
    nodeUb = ub;
    setScaledBounds(lb, ub);
    for (int r = 0; r < curRowLb.length; ++r) {
      if (curRowLb[r] != scaledRowLbSource[r] || curRowUb[r] != scaledRowUbSource[r]) {
        scaledLp.setConstraintBounds(r, curRowLb[r] / rowScaleDivisor[r],
            curRowUb[r] / rowScaleDivisor[r]);
        scaledRowLbSource[r] = curRowLb[r];
        scaledRowUbSource[r] = curRowUb[r];
      }
    }
    if (node.parentState != null && lastSolvedNode != node.parent) {
      simplex.loadStateForNextSolve(decodeState(node.parentState));
    }
    ProblemStatus status = runSimplex();
    if (status != ProblemStatus.OPTIMAL && status != ProblemStatus.PRIMAL_INFEASIBLE
        && status != ProblemStatus.DUAL_UNBOUNDED && !timeLimit.limitReached()) {
      // retry from scratch with the primal simplex
      final GlopParameters p = simplex.getParameters().copy();
      simplex.clearStateForNextSolve();
      final GlopParameters primal = options.lpParameters.copy();
      primal.setUseDualSimplex(false);
      primal.setUsePreprocessing(false);
      simplex.setParameters(primal);
      status = runSimplex();
      simplex.setParameters(p);
    }
    lastSolvedNode = node;
    if (status == ProblemStatus.OPTIMAL) {
      extractSolution();
    }
    return status;
  }

  private ProblemStatus runSimplex() {
    final Status s = simplex.solve(scaledLp, timeLimit);
    numIterations += simplex.getNumberOfIterations();
    if (!s.ok()) {
      simplex.clearStateForNextSolve();
      return ProblemStatus.ABNORMAL;
    }
    return simplex.getProblemStatus();
  }

  private void extractSolution() {
    nodeValues = new double[numVars];
    nodeReducedCosts = new double[numVars];
    nodeStatuses = new VariableStatus[numVars];
    final LpUtils.KahanSum sum = new LpUtils.KahanSum();
    sum.add(objectiveOffset);
    for (int j = 0; j < numVars; ++j) {
      nodeValues[j] = simplex.getVariableValue(j) * boundScalingFactor / colScale[j];
      nodeReducedCosts[j] = simplex.getReducedCost(j) * costScalingFactor * colScale[j];
      nodeStatuses[j] = simplex.getVariableStatus(j);
      sum.add(objective[j] * nodeValues[j]);
    }
    nodeObjective = sum.value();
  }

  private boolean isFeasible(double[] x) {
    for (int j = 0; j < numVars; ++j) {
      final MPModel.Variable v = model.variables.get(j);
      if (x[j] < v.lowerBound - FEASIBILITY_TOLERANCE
          || x[j] > v.upperBound + FEASIBILITY_TOLERANCE) {
        return false;
      }
      if (isInteger[j] && Math.abs(x[j] - Math.rint(x[j])) > INTEGRALITY_TOLERANCE) {
        return false;
      }
    }
    // the constraints of the input model (the cuts and symmetry breaking rows are not checked)
    for (int r = 0; r < numOriginalConstraints; ++r) {
      final MPModel.Constraint c = model.constraints.get(r);
      final LpUtils.KahanSum activity = new LpUtils.KahanSum();
      double magnitude = 1.0;
      for (int k = 0; k < c.varIndex.size(); ++k) {
        final double term = c.coefficient.get(k) * x[c.varIndex.get(k)];
        activity.add(term);
        magnitude = Math.max(magnitude, Math.abs(term));
      }
      final double tol = FEASIBILITY_TOLERANCE * Math.max(1.0, Math.min(magnitude, 1e3));
      if (activity.value() < c.lowerBound - tol || activity.value() > c.upperBound + tol) {
        return false;
      }
    }
    return true;
  }

  private double evaluate(double[] x) {
    final LpUtils.KahanSum sum = new LpUtils.KahanSum();
    sum.add(objectiveOffset);
    for (int j = 0; j < numVars; ++j) {
      sum.add(objective[j] * x[j]);
    }
    return sum.value();
  }

  private void tryIncumbent(double[] candidate) {
    for (int j = 0; j < numVars; ++j) {
      // no negative zero in the reported values
      candidate[j] += 0.0;
    }
    if (!isFeasible(candidate)) {
      return;
    }
    final double value = evaluate(candidate);
    if (value < incumbentObjective) {
      incumbentObjective = value;
      incumbent = candidate;
      log("New solution " + sign * value + " at node " + numNodes);
    }
  }

  private void useHint() {
    if (model.hintVarIndex.isEmpty()) {
      return;
    }
    final double[] x = new double[numVars];
    final boolean[] given = new boolean[numVars];
    for (int k = 0; k < model.hintVarIndex.size(); ++k) {
      x[model.hintVarIndex.get(k)] = model.hintVarValue.get(k);
      given[model.hintVarIndex.get(k)] = true;
    }
    for (int j = 0; j < numVars; ++j) {
      if (!given[j]) {
        return;
      }
    }
    tryIncumbent(x);
  }

  private void runRoundingHeuristic(Node node) {
    if (nodeValues == null) {
      return;
    }
    final double[] x = nodeValues.clone();
    boolean hasContinuous = false;
    for (int j = 0; j < numVars; ++j) {
      if (isInteger[j]) {
        x[j] = Math.rint(x[j]);
      } else {
        hasContinuous = true;
      }
    }
    if (!hasContinuous) {
      // nearest, down (feasible for packing rows) and up (feasible for covering rows)
      tryIncumbent(x);
      final double[] down = nodeValues.clone();
      final double[] up = nodeValues.clone();
      for (int j = 0; j < numVars; ++j) {
        down[j] = Math.floor(down[j] + INTEGRALITY_TOLERANCE);
        up[j] = Math.ceil(up[j] - INTEGRALITY_TOLERANCE);
      }
      tryIncumbent(down);
      tryIncumbent(up);
      return;
    }
    // fix the integer variables and solve the LP over the continuous ones
    final double[] lb = new double[numVars];
    final double[] ub = new double[numVars];
    computeBounds(node, lb, ub);
    for (int j = 0; j < numVars; ++j) {
      if (isInteger[j]) {
        if (x[j] < lb[j] || x[j] > ub[j]) {
          return;
        }
        lb[j] = x[j];
        ub[j] = x[j];
      }
    }
    final LinearProgram lp = new LinearProgram();
    lp.populateFromLinearProgram(scaledLp);
    for (int j = 0; j < numVars; ++j) {
      lp.setVariableBounds(j, lb[j] * colScale[j] / boundScalingFactor,
          ub[j] * colScale[j] / boundScalingFactor);
    }
    final RevisedSimplex heuristic = new RevisedSimplex();
    final GlopParameters p = options.lpParameters.copy();
    p.setUsePreprocessing(false);
    heuristic.setParameters(p);
    final Status s = heuristic.solve(lp, timeLimit);
    numIterations += heuristic.getNumberOfIterations();
    if (!s.ok() || heuristic.getProblemStatus() != ProblemStatus.OPTIMAL) {
      return;
    }
    final double[] candidate = new double[numVars];
    for (int j = 0; j < numVars; ++j) {
      candidate[j] = isInteger[j] ? x[j]
          : heuristic.getVariableValue(j) * boundScalingFactor / colScale[j];
    }
    tryIncumbent(candidate);
  }
}
