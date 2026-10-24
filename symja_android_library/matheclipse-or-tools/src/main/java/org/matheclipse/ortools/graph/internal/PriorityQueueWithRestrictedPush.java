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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/generic_max_flow.h C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import java.util.Arrays;

/**
 * Specific but efficient priority queue of <code>int</code> elements with <code>int</code>
 * priorities. It retrieves the element with the highest priority but only allows pushes with a
 * priority greater or equal to the highest priority in the queue minus one. All operations are in
 * O(1). Elements with the same priority are retrieved in LIFO order.
 *
 * <p>
 * The elements are split by the parity of their priority; the push precondition ensures that both
 * stacks are always sorted by increasing priority.
 */
public final class PriorityQueueWithRestrictedPush {
  private int[] evenElements = new int[16];
  private int[] evenPriorities = new int[16];
  private int evenSize;
  private int[] oddElements = new int[16];
  private int[] oddPriorities = new int[16];
  private int oddSize;

  /** Is the queue empty? */
  public boolean isEmpty() {
    return evenSize == 0 && oddSize == 0;
  }

  /** Clears the queue. */
  public void clear() {
    evenSize = 0;
    oddSize = 0;
  }

  /**
   * Pushes a new element. Its priority must be greater or equal to the highest priority present in
   * the queue, minus one.
   */
  public void push(int element, int priority) {
    if ((priority & 1) != 0) {
      if (oddSize == oddElements.length) {
        oddElements = Arrays.copyOf(oddElements, 2 * oddSize);
        oddPriorities = Arrays.copyOf(oddPriorities, 2 * oddSize);
      }
      oddElements[oddSize] = element;
      oddPriorities[oddSize] = priority;
      oddSize++;
    } else {
      if (evenSize == evenElements.length) {
        evenElements = Arrays.copyOf(evenElements, 2 * evenSize);
        evenPriorities = Arrays.copyOf(evenPriorities, 2 * evenSize);
      }
      evenElements[evenSize] = element;
      evenPriorities[evenSize] = priority;
      evenSize++;
    }
  }

  /** Returns the element with the highest priority and removes it from the queue. */
  public int pop() {
    if (evenSize == 0) {
      return oddElements[--oddSize];
    }
    if (oddSize == 0) {
      return evenElements[--evenSize];
    }
    if (oddPriorities[oddSize - 1] > evenPriorities[evenSize - 1]) {
      return oddElements[--oddSize];
    }
    return evenElements[--evenSize];
  }
}
