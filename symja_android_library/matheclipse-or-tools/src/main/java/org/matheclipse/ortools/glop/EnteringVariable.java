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
// ortools/glop/entering_variable.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import org.matheclipse.ortools.lpdata.LpTypes;

/**
 * The dual simplex ratio test: chooses the entering column given the update row of the leaving
 * variable. It implements the Harris ratio test with the bound flipping ratio test of the dual
 * phase II, and the piecewise linear ratio test of the dual phase I.
 */
public final class EnteringVariable {

  /** A breakpoint of the dual ratio test. */
  private static final class ColWithRatio {
    final int col;
    final double ratio;
    final double coeffMagnitude;

    ColWithRatio(int col, double reducedCost, double coeffMagnitude) {
      this.col = col;
      this.ratio = reducedCost / coeffMagnitude;
      this.coeffMagnitude = coeffMagnitude;
    }
  }

  // increasing ratio, then decreasing magnitude, then increasing column
  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "exact ties between equally good columns, as in C++")
  private static int compare(ColWithRatio a, ColWithRatio b) {
    if (a.ratio == b.ratio) {
      if (a.coeffMagnitude == b.coeffMagnitude) {
        return Integer.compare(a.col, b.col);
      }
      return a.coeffMagnitude > b.coeffMagnitude ? -1 : 1;
    }
    return a.ratio < b.ratio ? -1 : 1;
  }

  private final VariablesInfo variablesInfo;
  private Random random;
  private final ReducedCosts reducedCosts;
  private GlopParameters parameters = new GlopParameters();
  private final List<Integer> equivalentEnteringChoices = new ArrayList<>();
  private long numOperations;

  EnteringVariable(VariablesInfo variablesInfo, Random random, ReducedCosts reducedCosts) {
    this.variablesInfo = variablesInfo;
    this.random = random;
    this.reducedCosts = reducedCosts;
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** Changes the random generator. */
  public void setRandom(Random random) {
    this.random = random;
  }

  /** The deterministic time spent in this class. */
  public double deterministicTime() {
    return LpTypes.deterministicTimeForFpOperations(numOperations);
  }

  /**
   * Dual phase II ratio test. Returns the entering column or -1 (dual unbounded); the boxed
   * columns whose bounds must be flipped are added to <code>boundFlipCandidates</code>.
   */
  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "exact ties between equally good columns, as in C++")
  public int dualChooseEnteringColumn(boolean nothingToRecompute, UpdateRow updateRow,
      double costVariation, List<Integer> boundFlipCandidates) {
    final double[] updateCoefficients = updateRow.getCoefficients();
    final double[] rc = reducedCosts.getReducedCosts();
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final BitSet isBoxed = variablesInfo.getNonBasicBoxedVariables();
    final double threshold = nothingToRecompute ? parameters.getMinimumAcceptablePivot()
        : parameters.getRatioTestZeroThreshold();
    double variationMagnitude = Math.abs(costVariation) - threshold;
    final double harrisTolerance =
        parameters.getHarrisToleranceRatio() * reducedCosts.getDualFeasibilityTolerance();
    double harrisRatio = Double.MAX_VALUE;
    final double minimumDelta =
        parameters.getDegenerateMinistepFactor() * reducedCosts.getDualFeasibilityTolerance();
    final int[] positions = updateRow.getNonZeroPositions();
    final int numPositions = updateRow.getNumNonZeroPositions();
    numOperations += 10L * numPositions;
    final PriorityQueue<ColWithRatio> breakpoints =
        new PriorityQueue<>(Math.max(1, numPositions), EnteringVariable::compare);
    for (int i = 0; i < numPositions; ++i) {
      final int col = positions[i];
      final double coeff =
          (costVariation > 0.0) ? updateCoefficients[col] : -updateCoefficients[col];
      ColWithRatio entry;
      if (canDecrease.get(col) && coeff > threshold) {
        if (-rc[col] > harrisRatio * coeff) {
          continue;
        }
        entry = new ColWithRatio(col, -rc[col], coeff);
      } else if (canIncrease.get(col) && coeff < -threshold) {
        if (rc[col] > harrisRatio * -coeff) {
          continue;
        }
        entry = new ColWithRatio(col, rc[col], -coeff);
      } else {
        continue;
      }
      final double hr = Math.max(minimumDelta / entry.coeffMagnitude,
          entry.ratio + harrisTolerance / entry.coeffMagnitude);
      if (hr < harrisRatio) {
        if (isBoxed.get(col)) {
          final double delta = variablesInfo.getBoundDifference(col) * entry.coeffMagnitude;
          if (delta >= variationMagnitude) {
            harrisRatio = hr;
          }
        } else {
          harrisRatio = hr;
        }
      }
      breakpoints.add(entry);
    }

    harrisRatio = Double.MAX_VALUE;
    int enteringCol = LpTypes.INVALID_COL;
    boundFlipCandidates.clear();
    double step = 0.0;
    double bestCoeff = -1.0;
    equivalentEnteringChoices.clear();
    while (!breakpoints.isEmpty()) {
      final ColWithRatio top = breakpoints.peek();
      if (top.ratio > harrisRatio) {
        break;
      }
      if (variationMagnitude > 0.0) {
        if (isBoxed.get(top.col)) {
          variationMagnitude -= variablesInfo.getBoundDifference(top.col) * top.coeffMagnitude;
          if (variationMagnitude > 0.0) {
            boundFlipCandidates.add(top.col);
            breakpoints.poll();
            continue;
          }
        }
      }
      if (top.coeffMagnitude >= bestCoeff) {
        harrisRatio = Math.min(harrisRatio, Math.max(minimumDelta / top.coeffMagnitude,
            top.ratio + harrisTolerance / top.coeffMagnitude));
        if (top.coeffMagnitude == bestCoeff && top.ratio == step) {
          equivalentEnteringChoices.add(top.col);
        } else {
          equivalentEnteringChoices.clear();
          bestCoeff = top.coeffMagnitude;
          enteringCol = top.col;
          step = top.ratio;
        }
      }
      breakpoints.poll();
    }
    if (!equivalentEnteringChoices.isEmpty()) {
      equivalentEnteringChoices.add(enteringCol);
      enteringCol = equivalentEnteringChoices.get(random.nextInt(equivalentEnteringChoices.size()));
    }
    if (enteringCol == LpTypes.INVALID_COL) {
      return enteringCol;
    }
    final double pivotLimit = parameters.getMinimumAcceptablePivot();
    if (bestCoeff < pivotLimit && !boundFlipCandidates.isEmpty()) {
      for (int i = boundFlipCandidates.size() - 1; i >= 0; --i) {
        final int col = boundFlipCandidates.get(i);
        if (Math.abs(updateCoefficients[col]) < pivotLimit) {
          continue;
        }
        enteringCol = col;
        break;
      }
    }
    return enteringCol;
  }

  /** Dual phase I ratio test. Returns the entering column or -1. */
  public int dualPhaseIChooseEnteringColumn(boolean nothingToRecompute, UpdateRow updateRow,
      double costVariation) {
    final double[] updateCoefficients = updateRow.getCoefficients();
    final double[] rc = reducedCosts.getReducedCosts();
    final double threshold = nothingToRecompute ? parameters.getMinimumAcceptablePivot()
        : parameters.getRatioTestZeroThreshold();
    final double dualFeasibilityTolerance = reducedCosts.getDualFeasibilityTolerance();
    final double harrisTolerance = parameters.getHarrisToleranceRatio() * dualFeasibilityTolerance;
    final double minimumDelta = parameters.getDegenerateMinistepFactor() * dualFeasibilityTolerance;
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final int[] positions = updateRow.getNonZeroPositions();
    final int numPositions = updateRow.getNumNonZeroPositions();
    numOperations += 10L * numPositions;
    final PriorityQueue<ColWithRatio> breakpoints =
        new PriorityQueue<>(Math.max(1, numPositions), EnteringVariable::compare);
    for (int i = 0; i < numPositions; ++i) {
      final int col = positions[i];
      if (Math.abs(updateCoefficients[col]) < threshold) {
        continue;
      }
      final double coeff =
          (costVariation > 0.0) ? updateCoefficients[col] : -updateCoefficients[col];
      if (Math.abs(rc[col]) <= dualFeasibilityTolerance) {
        if (coeff > 0 && !canDecrease.get(col)) {
          continue;
        }
        if (coeff < 0 && !canIncrease.get(col)) {
          continue;
        }
        if (coeff * rc[col] > 0.0) {
          breakpoints.add(new ColWithRatio(col,
              Math.max(minimumDelta, harrisTolerance - Math.abs(rc[col])), Math.abs(coeff)));
          continue;
        }
      } else {
        if (coeff * rc[col] > 0.0) {
          continue;
        }
      }
      breakpoints
          .add(new ColWithRatio(col, Math.abs(rc[col]) + harrisTolerance, Math.abs(coeff)));
    }
    double pivotMagnitude = 0.0;
    int enteringCol = LpTypes.INVALID_COL;
    double step = -1.0;
    double improvement = Math.abs(costVariation);
    while (!breakpoints.isEmpty()) {
      final ColWithRatio top = breakpoints.peek();
      if (top.ratio > step && top.coeffMagnitude >= pivotMagnitude) {
        enteringCol = top.col;
        step = top.ratio;
        pivotMagnitude = top.coeffMagnitude;
      }
      improvement -= top.coeffMagnitude;
      if (canDecrease.get(top.col) && canIncrease.get(top.col)
          && Math.abs(rc[top.col]) > threshold) {
        improvement -= top.coeffMagnitude;
      }
      if (improvement <= 0.0) {
        break;
      }
      breakpoints.poll();
    }
    return enteringCol;
  }
}
