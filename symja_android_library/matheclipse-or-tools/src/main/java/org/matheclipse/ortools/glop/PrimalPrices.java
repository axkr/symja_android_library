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
// ortools/glop/reduced_costs.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.BitSet;
import java.util.Random;

/**
 * The pricing of the primal simplex: maintains the price reduced_cost^2 / edge_norm^2 of each dual
 * infeasible column and returns the best entering column.
 */
public final class PrimalPrices {

  private boolean recompute = true;
  private final DynamicMaximum prices;
  private final VariablesInfo variablesInfo;
  private final PrimalEdgeNorms primalEdgeNorms;
  private final ReducedCosts reducedCosts;

  PrimalPrices(Random random, VariablesInfo variablesInfo, PrimalEdgeNorms primalEdgeNorms,
      ReducedCosts reducedCosts) {
    this.prices = new DynamicMaximum(random);
    this.variablesInfo = variablesInfo;
    this.primalEdgeNorms = primalEdgeNorms;
    this.reducedCosts = reducedCosts;
    reducedCosts.addRecomputationWatcher(() -> recompute = true);
    primalEdgeNorms.addRecomputationWatcher(() -> recompute = true);
  }

  /** Changes the random generator. */
  public void setRandom(Random random) {
    prices.setRandom(random);
  }

  /** Forces a full recomputation of the prices on the next query. */
  public void forceRecomputation() {
    recompute = true;
  }

  /** Updates the prices of the columns of the update row. */
  public void updateBeforeBasisPivot(int enteringCol, UpdateRow updateRow) {
    if (recompute) {
      return;
    }
    updateEnteringCandidates(false, updateRow.getNonZeroPositions(),
        updateRow.getNumNonZeroPositions());
    prices.remove(enteringCol);
  }

  /** Recomputes the price of one column. */
  public void recomputePriceAt(int col) {
    if (recompute) {
      return;
    }
    if (reducedCosts.isValidPrimalEnteringCandidate(col)) {
      final double[] squaredNorms = primalEdgeNorms.getSquaredNorms();
      final double[] rc = reducedCosts.getReducedCosts();
      prices.addOrUpdate(col, rc[col] * rc[col] / squaredNorms[col]);
    } else {
      prices.remove(col);
    }
  }

  /** Removes a column that just became dual feasible. */
  public void setAndDebugCheckThatColumnIsDualFeasible(int col) {
    if (recompute) {
      return;
    }
    prices.remove(col);
  }

  /** Returns the best entering column or -1. */
  public int getBestEnteringColumn() {
    if (recompute) {
      final double[] rc = reducedCosts.getReducedCosts();
      prices.clearAndResize(rc.length);
      final BitSet relevant = variablesInfo.getIsRelevantBitRow();
      updateEnteringCandidatesFromBitSet(relevant);
      recompute = false;
    }
    return prices.getMaximum();
  }

  private void updateEnteringCandidatesFromBitSet(BitSet cols) {
    final double tolerance = reducedCosts.getDualFeasibilityTolerance();
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final double[] squaredNorms = primalEdgeNorms.getSquaredNorms();
    final double[] rc = reducedCosts.getReducedCosts();
    for (int col = cols.nextSetBit(0); col >= 0; col = cols.nextSetBit(col + 1)) {
      final double reducedCost = rc[col];
      final boolean isDualInfeasible = (reducedCost > tolerance && canDecrease.get(col))
          ^ (reducedCost < -tolerance && canIncrease.get(col));
      if (isDualInfeasible) {
        prices.addOrUpdate(col, reducedCost * reducedCost / squaredNorms[col]);
      }
    }
  }

  private void updateEnteringCandidates(boolean fromCleanState, int[] cols, int numCols) {
    final double tolerance = reducedCosts.getDualFeasibilityTolerance();
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final double[] squaredNorms = primalEdgeNorms.getSquaredNorms();
    final double[] rc = reducedCosts.getReducedCosts();
    for (int i = 0; i < numCols; ++i) {
      final int col = cols[i];
      final double reducedCost = rc[col];
      final boolean isDualInfeasible = (reducedCost > tolerance && canDecrease.get(col))
          ^ (reducedCost < -tolerance && canIncrease.get(col));
      if (isDualInfeasible) {
        prices.addOrUpdate(col, reducedCost * reducedCost / squaredNorms[col]);
      } else if (!fromCleanState) {
        prices.remove(col);
      }
    }
  }
}
