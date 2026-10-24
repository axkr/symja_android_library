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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms.internal;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The multi-dimensional knapsack solver: a best-first branch and bound with one
 * {@link KnapsackCapacityPropagator} per dimension.
 *
 * <p>
 * The search repeatedly pops the node with the highest profit upper bound (ties: highest current
 * profit), moves the propagators incrementally to its state along the {@link KnapsackSearchPath}
 * from the previously explored node, and creates the two children for the next item chosen by the
 * primary propagator (the break item of the first dimension). Children whose upper bound is below
 * the best solution are dropped; the search stops when no node can improve the best solution, or
 * when the time limit is reached.
 */
public final class KnapsackGenericSolver extends BaseKnapsackSolver {
  private static final int PRIMARY_PROPAGATOR_ID = 0;

  private final List<KnapsackPropagator> propagators = new ArrayList<>();
  private int primaryPropagatorId;
  private final KnapsackState state = new KnapsackState();
  private long bestSolutionProfit;
  private boolean[] bestSolution = new boolean[0];

  /** Creates the solver. */
  public KnapsackGenericSolver(String solverName) {
    super(solverName);
    this.primaryPropagatorId = PRIMARY_PROPAGATOR_ID;
    this.bestSolutionProfit = 0;
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("capacities and weights must have the same size");
    }
    clear();
    final int numberOfItems = profits.length;
    final int numberOfDimensions = weights.length;
    state.init(numberOfItems);
    bestSolution = new boolean[numberOfItems];
    for (int i = 0; i < numberOfDimensions; ++i) {
      if (weights[i].length != numberOfItems) {
        throw new IllegalArgumentException("weights[" + i + "] must have one entry per item");
      }
      KnapsackCapacityPropagator propagator = new KnapsackCapacityPropagator(state, capacities[i]);
      propagator.init(profits, weights[i]);
      propagators.add(propagator);
    }
    primaryPropagatorId = PRIMARY_PROPAGATOR_ID;
  }

  /** The number of items. */
  public int getNumberOfItems() {
    return state.getNumberOfItems();
  }

  @Override
  public void getLowerAndUpperBoundWhenItem(int itemId, boolean isItemIn, long[] bounds) {
    KnapsackAssignment assignment = new KnapsackAssignment(itemId, isItemIn);
    final boolean fail = !incrementalUpdate(false, assignment);
    if (fail) {
      bounds[0] = 0L;
      bounds[1] = 0L;
    } else {
      // Note: the lower bound is read before the bounds are recomputed below, i.e. it is the
      // lower bound of the previous computation (a feasible solution value), as upstream.
      bounds[0] = hasOnePropagator() ? propagators.get(primaryPropagatorId).profitLowerBound() : 0L;
      bounds[1] = getAggregatedProfitUpperBound();
    }

    final boolean failRevert = !incrementalUpdate(true, assignment);
    if (failRevert) {
      bounds[0] = 0L;
      bounds[1] = 0L;
    }
  }

  /** Sets which propagator guides the search, in <code>[0, numberOfDimensions)</code>. */
  public void setPrimaryPropagatorId(int primaryPropagatorId) {
    this.primaryPropagatorId = primaryPropagatorId;
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    bestSolutionProfit = 0L;
    isSolutionOptimal[0] = true;

    SearchQueue searchQueue = new SearchQueue();
    final KnapsackAssignment assignment =
        new KnapsackAssignment(KnapsackSearchNode.NO_SELECTION, true);
    KnapsackSearchNode rootNode = new KnapsackSearchNode(null, assignment);
    rootNode.setCurrentProfit(getCurrentProfit());
    rootNode.setProfitUpperBound(getAggregatedProfitUpperBound());
    rootNode.setNextItemId(getNextItemId());

    KnapsackSearchNode child = makeNewNode(rootNode, false);
    if (child != null) {
      searchQueue.push(child);
    }
    child = makeNewNode(rootNode, true);
    if (child != null) {
      searchQueue.push(child);
    }

    KnapsackSearchNode currentNode = rootNode;
    while (!searchQueue.isEmpty() && searchQueue.top().profitUpperBound() > bestSolutionProfit) {
      if (timeLimit.limitReached()) {
        isSolutionOptimal[0] = false;
        break;
      }
      final KnapsackSearchNode node = searchQueue.top();
      searchQueue.pop();

      if (node != currentNode) {
        KnapsackSearchPath path = new KnapsackSearchPath(currentNode, node);
        path.init();
        final boolean noFail = updatePropagators(path);
        currentNode = node;
        if (!noFail) {
          throw new IllegalStateException("the state of a search node is inconsistent");
        }
      }

      child = makeNewNode(node, false);
      if (child != null) {
        searchQueue.push(child);
      }
      child = makeNewNode(node, true);
      if (child != null) {
        searchQueue.push(child);
      }
    }
    return bestSolutionProfit;
  }

  @Override
  public boolean bestSolution(int itemId) {
    return bestSolution[itemId];
  }

  private void clear() {
    propagators.clear();
  }

  // Updates all propagators, reverting/applying all the decisions on the path. Returns false on
  // failure; even then all propagators are updated to stay in a consistent state.
  private boolean updatePropagators(KnapsackSearchPath path) {
    boolean noFail = true;
    // Revert previous changes.
    KnapsackSearchNode node = path.from();
    final KnapsackSearchNode via = path.via();
    while (node != via) {
      noFail = incrementalUpdate(true, node.assignment()) && noFail;
      node = node.parent();
    }
    // Apply current changes.
    node = path.to();
    while (node != via) {
      noFail = incrementalUpdate(false, node.assignment()) && noFail;
      node = node.parent();
    }
    return noFail;
  }

  // The minimum of the upper bounds of all propagators.
  private long getAggregatedProfitUpperBound() {
    long upperBound = Long.MAX_VALUE;
    for (KnapsackPropagator prop : propagators) {
      prop.computeProfitBounds();
      final long propagatorUpperBound = prop.profitUpperBound();
      upperBound = Math.min(upperBound, propagatorUpperBound);
    }
    return upperBound;
  }

  // Returns the new child node of node for its next item, or null if it is not relevant.
  private KnapsackSearchNode makeNewNode(KnapsackSearchNode node, boolean isIn) {
    if (node.nextItemId() == KnapsackSearchNode.NO_SELECTION) {
      return null;
    }
    KnapsackAssignment assignment = new KnapsackAssignment(node.nextItemId(), isIn);
    KnapsackSearchNode newNode = new KnapsackSearchNode(node, assignment);

    KnapsackSearchPath path = new KnapsackSearchPath(node, newNode);
    path.init();
    final boolean noFail = updatePropagators(path);
    if (noFail) {
      newNode.setCurrentProfit(getCurrentProfit());
      newNode.setProfitUpperBound(getAggregatedProfitUpperBound());
      newNode.setNextItemId(getNextItemId());
      updateBestSolution();
    }

    // Revert to be able to create another node from the parent.
    KnapsackSearchPath revertPath = new KnapsackSearchPath(newNode, node);
    revertPath.init();
    updatePropagators(revertPath);

    if (!noFail || newNode.profitUpperBound() < bestSolutionProfit) {
      return null;
    }
    // The node is relevant.
    return newNode;
  }

  // Applies or reverts one decision on the state and all propagators. Returns false on failure;
  // all propagators are updated anyway.
  private boolean incrementalUpdate(boolean revert, KnapsackAssignment assignment) {
    boolean noFail = state.updateState(revert, assignment);
    for (KnapsackPropagator prop : propagators) {
      noFail = prop.update(revert, assignment) && noFail;
    }
    return noFail;
  }

  // Updates the best solution if the current solution is better.
  private void updateBestSolution() {
    final KnapsackPropagator primary = propagators.get(primaryPropagatorId);
    final long profitLowerBound =
        hasOnePropagator() ? primary.profitLowerBound() : primary.currentProfit();

    if (bestSolutionProfit < profitLowerBound) {
      bestSolutionProfit = profitLowerBound;
      primary.copyCurrentStateToSolution(hasOnePropagator(), bestSolution);
    }
  }

  private boolean hasOnePropagator() {
    return propagators.size() == 1;
  }

  private long getCurrentProfit() {
    return propagators.get(primaryPropagatorId).currentProfit();
  }

  private int getNextItemId() {
    return propagators.get(primaryPropagatorId).getNextItemId();
  }

  /**
   * A binary max-heap of search nodes with the push and pop algorithms of the GNU libstdc++
   * <code>std::priority_queue</code>, so that nodes with equal keys are explored in the same order
   * as the reference C++ build. A node is "less" than another one when its profit upper bound is
   * smaller, or equal with a smaller current profit.
   */
  static final class SearchQueue {
    private KnapsackSearchNode[] heap = new KnapsackSearchNode[64];
    private int size;

    private static boolean less(KnapsackSearchNode node1, KnapsackSearchNode node2) {
      final long profitUpperBound1 = node1.profitUpperBound();
      final long profitUpperBound2 = node2.profitUpperBound();
      if (profitUpperBound1 == profitUpperBound2) {
        return node1.currentProfit() < node2.currentProfit();
      }
      return profitUpperBound1 < profitUpperBound2;
    }

    boolean isEmpty() {
      return size == 0;
    }

    int size() {
      return size;
    }

    KnapsackSearchNode top() {
      return heap[0];
    }

    void push(KnapsackSearchNode node) {
      if (size == heap.length) {
        heap = java.util.Arrays.copyOf(heap, 2 * size);
      }
      heap[size] = node;
      size++;
      pushHeap(size - 1, 0, node);
    }

    void pop() {
      // std::pop_heap: move the last element into the hole left by the top.
      final int len = size - 1;
      final KnapsackSearchNode value = heap[len];
      heap[len] = heap[0];
      adjustHeap(0, len, value);
      heap[len] = null;
      size = len;
    }

    // __push_heap: sift value up from holeIndex, not above topIndex.
    private void pushHeap(int holeIndex, int topIndex, KnapsackSearchNode value) {
      int parent = (holeIndex - 1) / 2;
      while (holeIndex > topIndex && less(heap[parent], value)) {
        heap[holeIndex] = heap[parent];
        holeIndex = parent;
        parent = (holeIndex - 1) / 2;
      }
      heap[holeIndex] = value;
    }

    // __adjust_heap: move the hole down to a leaf along the larger children, then push value.
    private void adjustHeap(int holeIndex, int len, KnapsackSearchNode value) {
      final int topIndex = holeIndex;
      int secondChild = holeIndex;
      while (secondChild < (len - 1) / 2) {
        secondChild = 2 * (secondChild + 1);
        if (less(heap[secondChild], heap[secondChild - 1])) {
          secondChild--;
        }
        heap[holeIndex] = heap[secondChild];
        holeIndex = secondChild;
      }
      if ((len & 1) == 0 && secondChild == (len - 2) / 2) {
        secondChild = 2 * (secondChild + 1);
        heap[holeIndex] = heap[secondChild - 1];
        holeIndex = secondChild - 1;
      }
      pushHeap(holeIndex, topIndex, value);
    }
  }
}
