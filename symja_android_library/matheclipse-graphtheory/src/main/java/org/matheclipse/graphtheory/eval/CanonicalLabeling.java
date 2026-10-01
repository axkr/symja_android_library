package org.matheclipse.graphtheory.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.basic.OperationSystem;

/**
 * A canonical labelling of a graph and generators of its automorphism group, by the
 * individualization-refinement search of B. D. McKay's "Practical graph isomorphism".
 *
 * <p>
 * The vertices are coloured by an equitable partition (colour refinement). Where a colour class has
 * more than one vertex, each of its vertices is individualized in turn and the partition refined
 * again, until every vertex has its own colour. Such a leaf orders the vertices; the leaf whose
 * reordered adjacency matrix is the smallest one is the canonical labelling, so that isomorphic
 * graphs get the same matrix. Two leaves with the same matrix differ by an automorphism, and the
 * automorphisms found this way prune the search: a vertex in the orbit of one which was tried
 * already leads to nothing new.
 */
public final class CanonicalLabeling {
  private final int n;
  /** <code>undirectedCount[u][v]</code> the number of undirected edges between u and v */
  private final int[][] undirectedCount;
  /** <code>directedCount[u][v]</code> the number of directed edges from u to v */
  private final int[][] directedCount;
  private final long maxNodes;
  private long nodes = 0;
  private boolean exhausted = false;

  /** <code>position[v]</code> of the first leaf and of the leaf with the smallest matrix */
  private int[] firstPosition;
  private int[] firstCertificate;
  private int[] bestPosition;
  private int[] bestCertificate;
  /** automorphisms as the images of the vertices */
  private final List<int[]> automorphisms = new ArrayList<int[]>();

  private CanonicalLabeling(GraphView view, long maxNodes) {
    this.n = view.n;
    this.maxNodes = maxNodes;
    undirectedCount = new int[n][n];
    directedCount = new int[n][n];
    for (int e = 0; e < view.m; e++) {
      int u = view.source[e];
      int v = view.target[e];
      if (view.undirected[e]) {
        undirectedCount[u][v]++;
        if (u != v) {
          undirectedCount[v][u]++;
        }
      } else {
        directedCount[u][v]++;
      }
    }
  }

  /**
   * Search for the canonical labelling of the graph.
   *
   * @param maxNodes the size of the search tree after which the search gives up
   * @return <code>null</code> if the search gave up
   */
  public static CanonicalLabeling of(GraphView view, long maxNodes) {
    CanonicalLabeling labeling = new CanonicalLabeling(view, maxNodes);
    int[] color = new int[view.n];
    labeling.refine(color);
    labeling.search(color, new int[view.n], 0);
    return labeling.exhausted ? null : labeling;
  }

  /** The number of vertices. */
  public int size() {
    return n;
  }

  /**
   * <code>position[v]</code>, the 0-based canonical position of vertex <code>v</code>.
   */
  public int[] canonicalPosition() {
    return bestPosition == null ? new int[0] : bestPosition.clone();
  }

  /** The number of undirected edges between the vertices at two canonical positions. */
  public int undirectedEdges(int[] vertexAt, int i, int j) {
    return undirectedCount[vertexAt[i]][vertexAt[j]];
  }

  /** The number of directed edges from one canonical position to another. */
  public int directedEdges(int[] vertexAt, int i, int j) {
    return directedCount[vertexAt[i]][vertexAt[j]];
  }

  /**
   * Generators of the automorphism group, each one as the array of the images of the vertices.
   */
  public List<int[]> generators() {
    return automorphisms;
  }

  /**
   * Refine the colouring until it is equitable: two vertices of one colour have the same number of
   * edges of each kind to the vertices of every colour. The colours stay ordered - a colour is the
   * position its class starts at - and the order depends on the structure only, not on the names
   * of the vertices.
   */
  private void refine(int[] color) {
    Integer[] order = new Integer[n];
    long[][] signature = new long[n][];
    int classes = countColors(color);
    while (true) {
      for (int v = 0; v < n; v++) {
        // per colour c: undirected, outgoing and incoming edges to the vertices of colour c
        long[] s = new long[3 * n + 1];
        s[0] = color[v];
        for (int w = 0; w < n; w++) {
          int c = color[w];
          s[1 + 3 * c] += undirectedCount[v][w];
          s[2 + 3 * c] += directedCount[v][w];
          s[3 + 3 * c] += directedCount[w][v];
        }
        signature[v] = s;
        order[v] = v;
      }
      final long[][] sig = signature;
      Arrays.sort(order, (a, b) -> Arrays.compare(sig[a], sig[b]));
      int[] refined = new int[n];
      int start = 0;
      for (int i = 0; i < n; i++) {
        if (i > 0 && Arrays.compare(sig[order[i]], sig[order[i - 1]]) != 0) {
          start = i;
        }
        refined[order[i]] = start;
      }
      System.arraycopy(refined, 0, color, 0, n);
      int refinedClasses = countColors(color);
      if (refinedClasses == classes) {
        return;
      }
      classes = refinedClasses;
    }
  }

  private int countColors(int[] color) {
    boolean[] used = new boolean[n + 1];
    int count = 0;
    for (int c : color) {
      if (!used[c]) {
        used[c] = true;
        count++;
      }
    }
    return count;
  }

  private void search(int[] color, int[] prefix, int depth) {
    if (exhausted) {
      return;
    }
    if (++nodes > maxNodes) {
      exhausted = true;
      return;
    }
    if ((nodes & 0x3FF) == 0) {
      OperationSystem.checkInterrupt();
    }
    // the first colour class with more than one vertex
    int[] classSize = new int[n + 1];
    for (int c : color) {
      classSize[c]++;
    }
    int target = -1;
    for (int c = 0; c < n; c++) {
      if (classSize[c] > 1) {
        target = c;
        break;
      }
    }
    if (target < 0) {
      leaf(color);
      return;
    }
    List<Integer> explored = new ArrayList<Integer>();
    for (int v = 0; v < n; v++) {
      if (color[v] != target) {
        continue;
      }
      if (inOrbitOfExplored(v, explored, prefix, depth)) {
        continue;
      }
      explored.add(v);
      // individualize v: it keeps the colour, the rest of its class moves behind it
      int[] child = color.clone();
      for (int w = 0; w < n; w++) {
        if (w != v && child[w] == target) {
          child[w] = target + 1;
        }
      }
      refine(child);
      prefix[depth] = v;
      search(child, prefix, depth + 1);
      if (exhausted) {
        return;
      }
    }
  }

  /**
   * Whether an automorphism which fixes every vertex of the prefix maps one of the explored
   * vertices to <code>v</code>.
   */
  private boolean inOrbitOfExplored(int v, List<Integer> explored, int[] prefix, int depth) {
    if (explored.isEmpty() || automorphisms.isEmpty()) {
      return false;
    }
    int[] parent = new int[n];
    for (int i = 0; i < n; i++) {
      parent[i] = i;
    }
    for (int[] automorphism : automorphisms) {
      boolean fixesPrefix = true;
      for (int i = 0; i < depth && fixesPrefix; i++) {
        fixesPrefix = automorphism[prefix[i]] == prefix[i];
      }
      if (fixesPrefix) {
        for (int i = 0; i < n; i++) {
          union(parent, i, automorphism[i]);
        }
      }
    }
    int root = find(parent, v);
    for (int w : explored) {
      if (find(parent, w) == root) {
        return true;
      }
    }
    return false;
  }

  private static int find(int[] parent, int v) {
    while (parent[v] != v) {
      parent[v] = parent[parent[v]];
      v = parent[v];
    }
    return v;
  }

  private static void union(int[] parent, int a, int b) {
    int ra = find(parent, a);
    int rb = find(parent, b);
    if (ra != rb) {
      parent[ra] = rb;
    }
  }

  /** A discrete colouring: the colour of a vertex is its position. */
  private void leaf(int[] position) {
    int[] certificate = new int[2 * n * n];
    for (int u = 0; u < n; u++) {
      for (int v = 0; v < n; v++) {
        certificate[position[u] * n + position[v]] = undirectedCount[u][v];
        certificate[n * n + position[u] * n + position[v]] = directedCount[u][v];
      }
    }
    if (firstCertificate == null) {
      firstCertificate = certificate;
      firstPosition = position.clone();
      bestCertificate = certificate;
      bestPosition = firstPosition;
      return;
    }
    if (Arrays.equals(certificate, firstCertificate)) {
      addAutomorphism(firstPosition, position);
    }
    int comparison = Arrays.compare(certificate, bestCertificate);
    if (comparison < 0) {
      bestCertificate = certificate;
      bestPosition = position.clone();
    } else if (comparison == 0 && bestPosition != firstPosition) {
      addAutomorphism(bestPosition, position);
    }
  }

  /** The automorphism which maps the vertex at each position of one leaf to that of the other. */
  private void addAutomorphism(int[] fromPosition, int[] toPosition) {
    int[] vertexAt = new int[n];
    for (int v = 0; v < n; v++) {
      vertexAt[toPosition[v]] = v;
    }
    int[] image = new int[n];
    boolean identity = true;
    for (int v = 0; v < n; v++) {
      image[v] = vertexAt[fromPosition[v]];
      identity &= image[v] == v;
    }
    if (!identity) {
      automorphisms.add(image);
    }
  }
}
