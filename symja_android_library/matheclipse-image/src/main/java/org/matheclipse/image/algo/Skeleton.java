package org.matheclipse.image.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * The skeleton of a binary image and the graph it forms.
 *
 * <p>
 * The foreground is thinned to lines one pixel wide (Zhang-Suen, Lu-Wang variant), and the pixels a line only needs
 * to turn a corner are dropped, so that every pixel inside a branch has exactly two neighbours.
 * The vertices of the graph are the end and branch points of the skeleton - adjacent branch pixels
 * make one vertex - and its edges are the branches between them. A closed curve without such a
 * point gets one vertex with a loop.
 */
public final class Skeleton {

  /** Row and column offsets of the neighbours in circular order, starting at the one above. */
  private static final int[] DY = {-1, -1, 0, 1, 1, 1, 0, -1};

  private static final int[] DX = {0, 1, 1, 1, 0, -1, -1, -1};

  private final boolean[][] pixels;

  private final int height;

  private final int width;

  /** The vertex a pixel belongs to, <code>-1</code> for a pixel inside a branch or background. */
  private final int[][] vertex;

  /** The pixels of each vertex. */
  private final List<List<int[]>> vertexPixels = new ArrayList<List<int[]>>();

  private final List<int[]> edges = new ArrayList<int[]>();

  private Skeleton(boolean[][] foreground) {
    height = foreground.length;
    width = foreground[0].length;
    pixels = new boolean[height][width];
    for (int y = 0; y < height; y++) {
      pixels[y] = foreground[y].clone();
    }
    vertex = new int[height][width];
  }

  /**
   * <code>Graph({1, ..., n}, edges, VertexCoordinates -&gt; coordinates)</code> of the skeleton of
   * the foreground given row by row from the top. The vertices are numbered in the order their
   * first pixel is met row by row, and a vertex sits at the mean of the centres of its pixels, in
   * the coordinates of the image: <code>x</code> to the right, <code>y</code> up and pixel
   * <code>(row, column)</code> centred at <code>{column - 1/2, height - row + 1/2}</code>.
   *
   * @return the graph without any further option, to which the caller appends its options
   */
  public static IASTAppendable graph(boolean[][] foreground) {
    Skeleton skeleton = new Skeleton(foreground);
    skeleton.thin();
    skeleton.dropCorners();
    skeleton.findVertices();
    skeleton.traceEdges();
    return skeleton.toGraph();
  }

  private boolean at(int y, int x) {
    return y >= 0 && y < height && x >= 0 && x < width && pixels[y][x];
  }

  private int neighbourCount(int y, int x) {
    int count = 0;
    for (int k = 0; k < 8; k++) {
      if (at(y + DY[k], x + DX[k])) {
        count++;
      }
    }
    return count;
  }

  /**
   * Zhang-Suen thinning, in the variant of Lu and Wang: peel off boundary pixels in two alternating
   * passes until none goes.
   */
  private void thin() {
    List<int[]> deletions = new ArrayList<int[]>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int pass = 0; pass < 2; pass++) {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            if (pixels[y][x] && deletable(y, x, pass)) {
              deletions.add(new int[] {y, x});
            }
          }
        }
        for (int[] pixel : deletions) {
          pixels[pixel[0]][pixel[1]] = false;
        }
        changed |= !deletions.isEmpty();
        deletions.clear();
      }
    }
  }

  private boolean deletable(int y, int x, int pass) {
    int count = neighbourCount(y, x);
    // at least 3 neighbours rather than 2 keeps the ends of a diagonal line 2 pixels thick, which
    // would otherwise be peeled away from both ends until nothing is left
    if (count < 3 || count > 6) {
      return false;
    }
    int transitions = 0;
    for (int k = 0; k < 8; k++) {
      if (!at(y + DY[k], x + DX[k]) && at(y + DY[(k + 1) % 8], x + DX[(k + 1) % 8])) {
        transitions++;
      }
    }
    if (transitions != 1) {
      return false;
    }
    boolean north = at(y - 1, x);
    boolean east = at(y, x + 1);
    boolean south = at(y + 1, x);
    boolean west = at(y, x - 1);
    if (pass == 0) {
      return !(north && east && south) && !(east && south && west);
    }
    return !(north && east && west) && !(north && south && west);
  }

  /**
   * Drop the pixels at which a line turns a corner through its only two edge neighbours, when its
   * neighbours stay connected without it; such a pixel and the one after it in a branch would
   * otherwise have three neighbours.
   */
  private void dropCorners() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (pixels[y][x] && hasCorner(y, x) && ringComponents(y, x) == 1) {
          pixels[y][x] = false;
        }
      }
    }
  }

  private boolean hasCorner(int y, int x) {
    int edgeNeighbours = 0;
    boolean corner = false;
    for (int k = 0; k < 8; k += 2) {
      if (at(y + DY[k], x + DX[k])) {
        edgeNeighbours++;
        corner |= at(y + DY[(k + 2) % 8], x + DX[(k + 2) % 8]);
      }
    }
    return corner && edgeNeighbours == 2;
  }

  /**
   * The number of connected groups the neighbours of a pixel form among themselves: neighbours
   * next to each other in the ring are connected, and so are two edge neighbours at a right angle.
   */
  private int ringComponents(int y, int x) {
    int[] group = new int[8];
    for (int k = 0; k < 8; k++) {
      group[k] = at(y + DY[k], x + DX[k]) ? k : -1;
    }
    boolean merged = true;
    while (merged) {
      merged = false;
      for (int k = 0; k < 8; k++) {
        if (group[k] < 0) {
          continue;
        }
        int next = (k + 1) % 8;
        int across = (k + 2) % 8;
        merged |= join(group, k, next);
        if (k % 2 == 0) {
          merged |= join(group, k, across);
        }
      }
    }
    Set<Integer> groups = new HashSet<Integer>();
    for (int k = 0; k < 8; k++) {
      if (group[k] >= 0) {
        groups.add(group[k]);
      }
    }
    return groups.size();
  }

  /** Give two present neighbours the smaller of their group numbers. */
  private static boolean join(int[] group, int i, int j) {
    if (group[i] < 0 || group[j] < 0 || group[i] == group[j]) {
      return false;
    }
    int smaller = Math.min(group[i], group[j]);
    group[i] = smaller;
    group[j] = smaller;
    return true;
  }

  /** A pixel which ends a branch or where branches meet: anything without exactly 2 neighbours. */
  private boolean isNode(int y, int x) {
    return pixels[y][x] && neighbourCount(y, x) != 2;
  }

  /** A pixel where branches meet: one with more than 2 neighbours. */
  private boolean isBranchPoint(int y, int x) {
    return pixels[y][x] && neighbourCount(y, x) > 2;
  }

  /** Number the end and branch points, adjacent branch points as one vertex. */
  private void findVertices() {
    for (int[] row : vertex) {
      Arrays.fill(row, -1);
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (vertex[y][x] < 0 && isNode(y, x)) {
          newVertex(y, x);
        }
      }
    }
  }

  /**
   * A new vertex at the pixel; if it is a branch point it takes in all the branch points connected
   * to it.
   */
  private int newVertex(int y, int x) {
    int number = vertexPixels.size();
    List<int[]> members = new ArrayList<int[]>();
    vertexPixels.add(members);
    vertex[y][x] = number;
    members.add(new int[] {y, x});
    for (int i = 0; isBranchPoint(y, x) && i < members.size(); i++) {
      int[] pixel = members.get(i);
      for (int k = 0; k < 8; k++) {
        int row = pixel[0] + DY[k];
        int column = pixel[1] + DX[k];
        if (at(row, column) && vertex[row][column] < 0 && isBranchPoint(row, column)) {
          vertex[row][column] = number;
          members.add(new int[] {row, column});
        }
      }
    }
    return number;
  }

  /**
   * Follow every branch from the vertices; then a pixel still not visited lies on a closed curve
   * without a vertex, which gets one at its first pixel.
   */
  private void traceEdges() {
    boolean[][] visited = new boolean[height][width];
    Set<Long> touching = new HashSet<Long>();
    for (int v = 0; v < vertexPixels.size(); v++) {
      traceFrom(v, visited, touching);
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (pixels[y][x] && vertex[y][x] < 0 && !visited[y][x]) {
          traceFrom(newVertex(y, x), visited, touching);
        }
      }
    }
  }

  private void traceFrom(int v, boolean[][] visited, Set<Long> touching) {
    List<int[]> members = vertexPixels.get(v);
    for (int i = 0; i < members.size(); i++) {
      int[] start = members.get(i);
      for (int k = 0; k < 8; k++) {
        int row = start[0] + DY[k];
        int column = start[1] + DX[k];
        if (!at(row, column) || vertex[row][column] == v) {
          continue;
        }
        if (vertex[row][column] >= 0) {
          // two vertices next to each other: one edge, whichever pixels touch
          int w = vertex[row][column];
          if (touching.add(((long) Math.min(v, w) << 32) | Math.max(v, w))) {
            edges.add(new int[] {v, w});
          }
        } else if (!visited[row][column]) {
          int end = follow(start, row, column, visited);
          if (end >= 0) {
            edges.add(new int[] {v, end});
          }
        }
      }
    }
  }

  /**
   * Walk along a branch from a vertex pixel through its next pixel.
   *
   * @return the vertex the branch ends in, or <code>-1</code> if it runs into a pixel visited
   *         already
   */
  private int follow(int[] start, int row, int column, boolean[][] visited) {
    int previousRow = start[0];
    int previousColumn = start[1];
    while (true) {
      if (vertex[row][column] >= 0) {
        return vertex[row][column];
      }
      if (visited[row][column]) {
        return -1;
      }
      visited[row][column] = true;
      int nextRow = -1;
      int nextColumn = -1;
      for (int k = 0; k < 8; k++) {
        int r = row + DY[k];
        int c = column + DX[k];
        if (at(r, c) && (r != previousRow || c != previousColumn)) {
          nextRow = r;
          nextColumn = c;
          break;
        }
      }
      if (nextRow < 0) {
        return -1;
      }
      previousRow = row;
      previousColumn = column;
      row = nextRow;
      column = nextColumn;
    }
  }

  private IASTAppendable toGraph() {
    int n = vertexPixels.size();
    IASTAppendable vertices = F.ListAlloc(n);
    IASTAppendable coordinates = F.ListAlloc(n);
    for (int v = 0; v < n; v++) {
      vertices.append(F.ZZ(v + 1));
      double sumX = 0.0;
      double sumY = 0.0;
      for (int[] pixel : vertexPixels.get(v)) {
        sumX += pixel[1] + 0.5;
        sumY += height - pixel[0] - 0.5;
      }
      int size = vertexPixels.get(v).size();
      coordinates.append(F.List(F.num(sumX / size), F.num(sumY / size)));
    }
    IASTAppendable edgeList = F.ListAlloc(edges.size());
    for (int[] edge : edges) {
      edgeList.append(F.UndirectedEdge(F.ZZ(Math.min(edge[0], edge[1]) + 1),
          F.ZZ(Math.max(edge[0], edge[1]) + 1)));
    }
    IASTAppendable graph = F.ast(S.Graph);
    graph.append(vertices);
    graph.append(edgeList);
    graph.append(F.Rule(S.VertexCoordinates, coordinates));
    return graph;
  }
}
