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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/pricing.h C++ to
// Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import org.matheclipse.ortools.lpdata.LpTypes;

/**
 * Maintains a set of candidates in [0, n), each with a value, and returns the candidate of maximum
 * value. A best-effort top-k heap (k = 31) avoids most full scans. Ties are broken randomly.
 */
public final class DynamicMaximum {

  private static final int K = 31;

  private Random random;
  private int[] equivalentChoices = new int[8];
  private int numEquivalentChoices;
  private double[] values = new double[0];
  private BitSet isCandidate = new BitSet();
  private double threshold;
  private final int[] topIndex = new int[K];
  private final double[] topValue = new double[K];
  private int topSize;

  /** Creates the structure with the given random generator for the tie breaking. */
  public DynamicMaximum(Random random) {
    this.random = random;
  }

  /** Changes the random generator. */
  public void setRandom(Random random) {
    this.random = random;
  }

  /** Prepares the structure for indices in [0, n), with no candidate. */
  public void clearAndResize(int n) {
    topSize = 0;
    threshold = -LpTypes.INFINITY;
    if (values.length != n) {
      values = Arrays.copyOf(values, n);
    }
    isCandidate = new BitSet(n);
  }

  /** Same as clearAndResize(0). */
  public void clear() {
    clearAndResize(0);
  }

  /** The current size n. */
  public int size() {
    return values.length;
  }

  /** Removes a candidate. */
  public void remove(int position) {
    isCandidate.clear(position);
  }

  /** Disables the top-k until the next {@link #getMaximum()}, for O(n) updates. */
  public void startDenseUpdates() {
    topSize = 0;
    threshold = LpTypes.INFINITY;
  }

  /** Dense version of {@link #addOrUpdate(int, double)}, see {@link #startDenseUpdates()}. */
  public void denseAddOrUpdate(int position, double value) {
    isCandidate.set(position);
    values[position] = value;
  }

  /** Adds a candidate or updates its value. */
  public void addOrUpdate(int position, double value) {
    isCandidate.set(position);
    values[position] = value;
    if (value >= threshold) {
      updateTopK(position, value);
    }
  }

  private void pushEquivalent(int index) {
    if (numEquivalentChoices == equivalentChoices.length) {
      equivalentChoices = Arrays.copyOf(equivalentChoices, 2 * equivalentChoices.length);
    }
    equivalentChoices[numEquivalentChoices++] = index;
  }

  private int randomizeIfManyChoices(int best) {
    if (numEquivalentChoices == 0) {
      return best;
    }
    pushEquivalent(best);
    return equivalentChoices[random.nextInt(numEquivalentChoices)];
  }

  /** Returns the candidate of maximum value, or -1 if there is none. */
  public int getMaximum() {
    double bestValue = -LpTypes.INFINITY;
    int bestPosition = -1;
    numEquivalentChoices = 0;
    if (topSize > 0) {
      int newSize = 0;
      for (int i = 0; i < topSize; ++i) {
        final int index = topIndex[i];
        final double value = topValue[i];
        if (!isCandidate.get(index)) {
          continue;
        }
        if (values[index] != value) {
          continue;
        }
        topIndex[newSize] = index;
        topValue[newSize] = value;
        ++newSize;
        if (value >= bestValue) {
          if (value == bestValue) {
            pushEquivalent(index);
            continue;
          }
          numEquivalentChoices = 0;
          bestValue = value;
          bestPosition = index;
        }
      }
      topSize = newSize;
      if (newSize != 0) {
        return randomizeIfManyChoices(bestPosition);
      }
    }
    threshold = -LpTypes.INFINITY;
    for (int position = isCandidate.nextSetBit(0); position >= 0;
        position = isCandidate.nextSetBit(position + 1)) {
      final double value = values[position];
      if (value < threshold) {
        continue;
      }
      updateTopK(position, value);
      if (value >= bestValue) {
        if (value == bestValue) {
          pushEquivalent(position);
          continue;
        }
        numEquivalentChoices = 0;
        bestValue = value;
        bestPosition = position;
      }
    }
    return randomizeIfManyChoices(bestPosition);
  }

  // min-heap on topValue: topValue[0] is the smallest kept value
  private void siftDown(int i, int n) {
    while (true) {
      final int l = 2 * i + 1;
      final int r = l + 1;
      int smallest = i;
      if (l < n && topValue[l] < topValue[smallest]) {
        smallest = l;
      }
      if (r < n && topValue[r] < topValue[smallest]) {
        smallest = r;
      }
      if (smallest == i) {
        return;
      }
      swap(i, smallest);
      i = smallest;
    }
  }

  private void swap(int a, int b) {
    final int ti = topIndex[a];
    topIndex[a] = topIndex[b];
    topIndex[b] = ti;
    final double tv = topValue[a];
    topValue[a] = topValue[b];
    topValue[b] = tv;
  }

  private void updateTopK(int position, double value) {
    if (topSize < K) {
      topIndex[topSize] = position;
      topValue[topSize] = value;
      ++topSize;
      if (topSize == K) {
        for (int i = K / 2 - 1; i >= 0; --i) {
          siftDown(i, K);
        }
        threshold = topValue[0];
      }
      return;
    }
    if (value == topValue[0]) {
      if (random.nextBoolean()) {
        topIndex[0] = position;
      }
      return;
    }
    int i = 0;
    final int limit = K / 2;
    while (i < limit) {
      final int leftChild = 2 * i + 1;
      final int rightChild = leftChild + 1;
      final double lValue = topValue[leftChild];
      final double rValue = topValue[rightChild];
      if (lValue > rValue) {
        if (value <= rValue) {
          break;
        }
        topIndex[i] = topIndex[rightChild];
        topValue[i] = topValue[rightChild];
        i = rightChild;
      } else {
        if (value <= lValue) {
          break;
        }
        topIndex[i] = topIndex[leftChild];
        topValue[i] = topValue[leftChild];
        i = leftChild;
      }
    }
    topIndex[i] = position;
    topValue[i] = value;
    threshold = topValue[0];
  }
}
