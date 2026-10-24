package org.matheclipse.ortools.routing;

import java.util.HashSet;
import java.util.Set;

/**
 * Maps the nodes of a routing problem to the variable indices of a {@link Model}, mirroring
 * <code>com.google.ortools.routing.IndexManager</code> of the OR-Tools 10.0 Java wrapper (named
 * <code>RoutingIndexManager</code> before) with the numbering of
 * <code>ortools/routing/index_manager.cc</code> (reimplemented in Java):
 *
 * <ul>
 * <li>nodes which are a start or not an end get the first indices, in node order;
 * <li>a start shared by several vehicles gets an extra index for every further vehicle;
 * <li>every vehicle end gets its own index after all others.
 * </ul>
 */
public class IndexManager {

  /** The index of a node which has no index of its own (a pure end node). */
  private static final long K_UNASSIGNED = -1;

  /** The index returned by {@link #nodeToIndex(int)} for a node which has no index of its own. */
  public static long getKUnassigned() {
    return K_UNASSIGNED;
  }

  private final int numNodes;

  private final int numVehicles;

  private final int numUniqueDepots;

  private final int[] indexToNode;

  private final long[] nodeToIndex;

  private final long[] vehicleToStart;

  private final long[] vehicleToEnd;

  /** All vehicles start and end at <code>depot</code>. */
  public IndexManager(int numNodes, int numVehicles, int depot) {
    this(numNodes, numVehicles, fill(numVehicles, depot), fill(numVehicles, depot));
  }

  private static int[] fill(int n, int value) {
    int[] a = new int[n];
    java.util.Arrays.fill(a, value);
    return a;
  }

  /** Vehicle <code>v</code> starts at node <code>starts[v]</code>, ends at <code>ends[v]</code>. */
  public IndexManager(int numNodes, int numVehicles, int[] starts, int[] ends) {
    if (starts.length != numVehicles || ends.length != numVehicles) {
      throw new IllegalArgumentException("starts and ends need one entry per vehicle");
    }
    this.numNodes = numNodes;
    this.numVehicles = numVehicles;
    Set<Integer> startSet = new HashSet<>();
    Set<Integer> endSet = new HashSet<>();
    Set<Integer> depots = new HashSet<>();
    for (int v = 0; v < numVehicles; v++) {
      if (starts[v] < 0 || ends[v] < 0 || starts[v] >= numNodes || ends[v] >= numNodes) {
        throw new IllegalArgumentException("start or end node out of range");
      }
      startSet.add(starts[v]);
      endSet.add(ends[v]);
      depots.add(starts[v]);
      depots.add(ends[v]);
    }
    numUniqueDepots = depots.size();
    final int size = numNodes + numVehicles - numUniqueDepots;
    indexToNode = new int[size + numVehicles];
    nodeToIndex = new long[numNodes];
    java.util.Arrays.fill(nodeToIndex, K_UNASSIGNED);
    vehicleToStart = new long[numVehicles];
    vehicleToEnd = new long[numVehicles];
    int index = 0;
    for (int i = 0; i < numNodes; i++) {
      if (startSet.contains(i) || !endSet.contains(i)) {
        indexToNode[index] = i;
        nodeToIndex[i] = index;
        index++;
      }
    }
    Set<Integer> seenStarts = new HashSet<>();
    for (int v = 0; v < numVehicles; v++) {
      int start = starts[v];
      if (seenStarts.add(start)) {
        vehicleToStart[v] = nodeToIndex[start];
      } else {
        vehicleToStart[v] = index;
        indexToNode[index] = start;
        index++;
      }
    }
    for (int v = 0; v < numVehicles; v++) {
      indexToNode[index] = ends[v];
      vehicleToEnd[v] = index;
      index++;
    }
  }

  /** The number of nodes. */
  public int getNumberOfNodes() {
    return numNodes;
  }

  /** The number of vehicles. */
  public int getNumberOfVehicles() {
    return numVehicles;
  }

  /** The number of indices (nodes, extra starts and ends). */
  public int getNumberOfIndices() {
    return indexToNode.length;
  }

  /** The number of distinct start and end nodes. */
  public int getNumberOfUniqueDepots() {
    return numUniqueDepots;
  }

  /** The start index of a vehicle. */
  public long getStartIndex(int vehicle) {
    return vehicleToStart[vehicle];
  }

  /** The end index of a vehicle. */
  public long getEndIndex(int vehicle) {
    return vehicleToEnd[vehicle];
  }

  /** The index of a node ({@link #getKUnassigned()} for a pure end node). */
  public long nodeToIndex(int node) {
    return nodeToIndex[node];
  }

  /** The indices of the nodes. */
  public long[] nodesToIndices(int[] nodes) {
    long[] r = new long[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      r[i] = nodeToIndex(nodes[i]);
      if (r[i] == K_UNASSIGNED) {
        throw new IllegalArgumentException("node " + nodes[i] + " has no index");
      }
    }
    return r;
  }

  /** The node of an index. */
  public int indexToNode(long index) {
    return indexToNode[(int) index];
  }

  /** The nodes of the indices. */
  public int[] indicesToNodes(long[] indices) {
    int[] r = new int[indices.length];
    for (int i = 0; i < indices.length; i++) {
      r[i] = indexToNode(indices[i]);
    }
    return r;
  }
}
