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
// Modified for the Symja matheclipse-or-tools module: the part of the ortools/graph/graph.h
// graph interface used by LinearSumAssignmentEngine, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

/**
 * A directed graph whose outgoing arcs can be iterated:
 *
 * <pre>
 * for (int arc = g.firstOutgoingArc(node); arc != OutgoingArcGraph.NIL_ARC; arc = g.nextOutgoingArc(arc))
 * </pre>
 */
public interface OutgoingArcGraph {
  /** Constant that is never a valid arc. */
  int NIL_ARC = Integer.MAX_VALUE;

  /** The number of nodes. */
  int numNodes();

  /** The number of arcs. */
  int numArcs();

  /** The head of an arc. */
  int head(int arc);

  /** The first outgoing arc of a node, or {@link #NIL_ARC}. */
  int firstOutgoingArc(int node);

  /** The next outgoing arc of the tail of <code>arc</code>, or {@link #NIL_ARC}. */
  int nextOutgoingArc(int arc);
}
