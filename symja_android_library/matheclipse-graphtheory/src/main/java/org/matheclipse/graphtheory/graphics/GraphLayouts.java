package org.matheclipse.graphtheory.graphics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The vertex coordinates of the named graph layouts, the values of the option
 * <code>GraphLayout</code>:
 * <ul>
 * <li><code>Automatic</code>: one or two vertices on a line, a path along the x-axis, a tree tidily
 * from its centre, any other graph on a circle;</li>
 * <li><code>"CircularEmbedding"</code>, <code>{"CircularEmbedding", "OptimalOrder" -&gt; False}</code>:
 * the vertices on the unit circle, the first one at the angle <code>Pi/2 + 2*Pi/n</code>;</li>
 * <li><code>"StarEmbedding"</code>, <code>{"StarEmbedding", "Center" -&gt; v}</code>;</li>
 * <li><code>{"GridEmbedding", "Dimension" -&gt; {rows, columns}}</code>;</li>
 * <li><code>{"MultipartiteLayout", "VertexPartition" -&gt; {n1, n2, ...}}</code> and
 * <code>"BipartiteEmbedding"</code>;</li>
 * <li><code>"SpiralEmbedding"</code>, <code>{"RadialEmbedding", "RootVertex" -&gt; v}</code>;</li>
 * <li><code>"LayeredEmbedding"</code>, <code>"LayeredDigraphEmbedding"</code> with the sub-options
 * <code>"RootVertex"</code> and <code>"Orientation"</code>.</li>
 * </ul>
 * The radial layout takes the sub-option <code>"LayerSizeFunction" -&gt; f</code>: the circle of
 * depth <code>k</code> lies <code>f(k)</code> beyond the one of depth <code>k - 1</code> instead of
 * one unit.
 * A specification <code>{"VertexLayout" -&gt; spec, ...}</code> is the layout <code>spec</code>.
 */
public final class GraphLayouts {

  /** <code>1/GoldenRatio</code>, the height of a layered drawing relative to its width */
  private static final double INV_GOLDEN = 0.6180339887498949;

  /** The sub-option of the radial layout which gives the depth of a layer. */
  public static final String LAYER_SIZE_FUNCTION = "LayerSizeFunction";

  /** The largest graph drawn as a tidy tree or in layers; larger ones go on a circle. */
  private static final int MAX_LAYERED_VERTICES = 5000;

  private final int n;

  private final List<IExpr> vertices;

  private final Map<IExpr, Integer> index;

  /** the edges as pairs of vertex indices <code>{source, target}</code>, self-loops left out */
  private final List<int[]> edges;

  /** the neighbours of every vertex in the order of the edges */
  private final int[][] adjacency;

  private final boolean directed;

  private GraphLayouts(Graph<IExpr, ?> graph) {
    this(new ArrayList<IExpr>(graph.vertexSet()), edgePairs(graph), graph.getType().isDirected());
  }

  /**
   * @param vertices the vertices
   * @param edges the edges as pairs of indices into <code>vertices</code>, self-loops left out
   * @param directed whether the graph is directed
   */
  private GraphLayouts(List<IExpr> vertices, List<int[]> edges, boolean directed) {
    this.vertices = vertices;
    this.n = vertices.size();
    this.index = new HashMap<IExpr, Integer>();
    for (int i = 0; i < n; i++) {
      index.put(vertices.get(i), i);
    }
    this.directed = directed;
    this.edges = edges;
    int[] degree = new int[n];
    for (int[] e : edges) {
      degree[e[0]]++;
      degree[e[1]]++;
    }
    this.adjacency = new int[n][];
    for (int i = 0; i < n; i++) {
      adjacency[i] = new int[degree[i]];
    }
    int[] fill = new int[n];
    for (int[] e : edges) {
      adjacency[e[0]][fill[e[0]]++] = e[1];
      adjacency[e[1]][fill[e[1]]++] = e[0];
    }
  }

  /**
   * The edges of <code>graph</code> as pairs <code>{source, target}</code> of positions in its
   * vertex list, self-loops left out.
   */
  private static List<int[]> edgePairs(Graph<IExpr, ?> graph) {
    Map<IExpr, Integer> position = new HashMap<IExpr, Integer>();
    for (IExpr v : graph.vertexSet()) {
      position.put(v, position.size());
    }
    List<int[]> pairs = new ArrayList<int[]>();
    @SuppressWarnings("unchecked")
    Graph<IExpr, Object> g = (Graph<IExpr, Object>) graph;
    for (Object edge : g.edgeSet()) {
      int s = position.get(g.getEdgeSource(edge));
      int t = position.get(g.getEdgeTarget(edge));
      if (s != t) {
        pairs.add(new int[] {s, t});
      }
    }
    return pairs;
  }

  /** The vertices <code>piece</code> and the edges between them, numbered in that order. */
  private GraphLayouts piece(int[] piece) {
    int[] position = new int[n];
    Arrays.fill(position, -1);
    List<IExpr> pieceVertices = new ArrayList<IExpr>(piece.length);
    for (int k = 0; k < piece.length; k++) {
      position[piece[k]] = k;
      pieceVertices.add(vertices.get(piece[k]));
    }
    List<int[]> pieceEdges = new ArrayList<int[]>();
    for (int[] e : edges) {
      if (position[e[0]] >= 0) {
        pieceEdges.add(new int[] {position[e[0]], position[e[1]]});
      }
    }
    return new GraphLayouts(pieceVertices, pieceEdges, directed);
  }

  /**
   * The coordinates of the vertices of <code>graph</code> in the order of its vertex list.
   *
   * @param graph
   * @param layout the value of <code>GraphLayout</code>; <code>Automatic</code> or {@link F#NIL}
   *        for the default layout
   * @return {@link F#NIL} if the layout is none of the layouts of this class
   */
  public static IAST coordinates(Graph<IExpr, ?> graph, IExpr layout) {
    GraphLayouts layouts = new GraphLayouts(graph);
    double[][] points = layouts.layout(layout);
    if (points == null) {
      return F.NIL;
    }
    return pointList(points);
  }

  /**
   * The coordinates of the vertices of <code>graph</code> in the order of its vertex list, drawn
   * in layers along the directions of the edges: every connected piece on its own, the vertices no
   * edge points to on its top layer and every other vertex one layer below the longest chain of
   * edges leading to it; the pieces are packed into a block about as wide as it is high.
   *
   * @param graph
   */
  public static IAST layeredDigraphCoordinates(Graph<IExpr, ?> graph) {
    GraphLayouts layouts = new GraphLayouts(graph);
    if (layouts.n == 0) {
      return F.CEmptyList;
    }
    return pointList(layouts.layeredPieces());
  }

  private static IAST pointList(double[][] points) {
    IASTAppendable result = F.ListAlloc(points.length);
    for (double[] p : points) {
      result.append(F.list(F.num(snap(p[0])), F.num(snap(p[1]))));
    }
    return result;
  }

  private double[][] layout(IExpr layoutSpec) {
    if (n == 0) {
      return new double[0][];
    }
    IExpr spec = vertexLayout(layoutSpec);
    if (spec.isNIL() || spec == S.Automatic) {
      return automatic();
    }
    String name = layoutName(spec);
    if (name.startsWith("Layered")) {
      IExpr root = subOption(spec, "RootVertex");
      IExpr orientation = subOption(spec, "Orientation");
      return layered(orientation.isPresent() ? orientation : S.Top,
          root.isPresent() && index.containsKey(root) ? index.get(root) : -1,
          !name.equals("LayeredDigraphEmbedding"));
    }
    switch (name) {
      case "CircularEmbedding":
        return circular(subOption(spec, "OptimalOrder").isFalse() ? null : circularOrder());
      case "StarEmbedding": {
        IExpr center = subOption(spec, "Center");
        return star(center.isPresent() && index.containsKey(center) ? index.get(center) : 0);
      }
      case "GridEmbedding": {
        IExpr dimension = subOption(spec, "Dimension");
        int rows = dimension.isList2() ? dimension.first().toIntDefault() : -1;
        if (rows <= 0) {
          rows = Math.max(1, (int) Math.floor(Math.sqrt(n)));
        }
        // "PackingLayout" -> None keeps the components on one grid
        return grid(rows, subOption(layoutSpec, "PackingLayout") != S.None);
      }
      case "MultipartiteLayout":
      case "MultipartiteEmbedding": {
        double[][] points = multipartite(subOption(spec, "VertexPartition"));
        return points != null ? points : automatic();
      }
      case "BipartiteEmbedding": {
        double[][] points = bipartite();
        return points != null ? points : automatic();
      }
      case "SpiralEmbedding":
        return n < 2 ? automatic() : spiral();
      case "RadialEmbedding": {
        IExpr root = subOption(spec, "RootVertex");
        return radial(root.isPresent() && index.containsKey(root) ? index.get(root) : center(),
            subOption(spec, LAYER_SIZE_FUNCTION));
      }
      default:
        return null;
    }
  }

  /** The layout of <code>{"VertexLayout" -&gt; layout, ...}</code>, else <code>spec</code>. */
  private static IExpr vertexLayout(IExpr spec) {
    IExpr layout = subOption(spec, "VertexLayout");
    return layout.isPresent() ? layout : spec;
  }

  /** The name of a layout <code>"name"</code> or <code>{"name", opts...}</code>. */
  private static String layoutName(IExpr spec) {
    if (spec.isString()) {
      return spec.toString();
    }
    if (spec.isList() && spec.argSize() > 0 && spec.first().isString()) {
      return spec.first().toString();
    }
    return "";
  }

  /** The value of the rule <code>"key" -&gt; value</code> in a list <code>spec</code>. */
  private static IExpr subOption(IExpr spec, String key) {
    if (spec.isList()) {
      for (IExpr option : (IAST) spec) {
        if (option.isRuleAST() && option.first().isString()
            && option.first().toString().equals(key)) {
          return option.second();
        }
      }
    }
    return F.NIL;
  }

  /**
   * How far from the roots the layers of a layered or radial drawing lie: the layer <code>k</code>,
   * the step from depth <code>k - 1</code> to depth <code>k</code>, is
   * <code>layerSizeFunction(k)</code> deep. Without a function, and where its value is no
   * non-negative number, a layer is one unit deep.
   *
   * @param layerSizeFunction the function or {@link F#NIL}
   * @param deepest the largest depth
   * @return the offsets of the depths <code>0..deepest</code>
   */
  public static double[] layerOffsets(IExpr layerSizeFunction, int deepest) {
    double[] offsets = new double[Math.max(deepest, 0) + 1];
    EvalEngine engine = EvalEngine.get();
    for (int k = 1; k < offsets.length; k++) {
      double size = 1.0;
      if (layerSizeFunction.isPresent()) {
        engine.checkTimeLimit();
        double value = engine.evaluate(F.unaryAST1(layerSizeFunction, F.ZZ(k))).evalfNaN();
        if (Double.isFinite(value) && value >= 0.0) {
          size = value;
        }
      }
      offsets[k] = offsets[k - 1] + size;
    }
    return offsets;
  }

  /**
   * The default layout: one vertex at the origin, two side by side with the first on the right, a
   * path along the x-axis from its first end, a tree tidily from its root (the only vertex no
   * directed edge points to, else its centre) and every other graph on a circle.
   */
  private double[][] automatic() {
    if (n == 1) {
      return new double[][] {{0.0, 0.0}};
    }
    if (n == 2) {
      return new double[][] {{1.0, 0.0}, {0.0, 0.0}};
    }
    if (edges.size() + 1 == n && n <= MAX_LAYERED_VERTICES) {
      int root = -1;
      if (directed) {
        boolean[] target = new boolean[n];
        for (int[] e : edges) {
          target[e[1]] = true;
        }
        for (int v = 0; v < n; v++) {
          if (!target[v]) {
            root = root < 0 ? v : Integer.MAX_VALUE;
          }
        }
      }
      if (root < 0 || root == Integer.MAX_VALUE) {
        root = center();
      }
      int[][] children = treeChildren(root);
      if (children != null) {
        double[][] path = path();
        if (path != null) {
          return path;
        }
        double[][] points = tidyTree(children, root);
        for (int v = 0; v < n; v++) {
          points[v] = orient(points[v][0], points[v][1], S.Top);
        }
        return unitMeanEdge(points);
      }
    }
    return circular(circularOrder());
  }

  /** A path along the x-axis from its first end, or <code>null</code> if the graph is no path. */
  private double[][] path() {
    int start = -1;
    for (int v = 0; v < n; v++) {
      if (adjacency[v].length > 2) {
        return null;
      }
      if (start < 0 && adjacency[v].length <= 1) {
        start = v;
      }
    }
    if (start < 0) {
      return null;
    }
    double[][] points = new double[n][2];
    int previous = -1;
    int v = start;
    for (int k = 0; k < n; k++) {
      points[v][0] = k;
      int next = -1;
      for (int w : adjacency[v]) {
        if (w != previous) {
          next = w;
        }
      }
      if (next < 0) {
        break;
      }
      previous = v;
      v = next;
    }
    return points;
  }

  /**
   * The children of every vertex of the tree hanging from <code>root</code>, edge directions
   * aside, in the order of the edges.
   *
   * @return <code>null</code> if the graph is no tree
   */
  private int[][] treeChildren(int root) {
    if (edges.size() + 1 != n) {
      return null;
    }
    List<List<Integer>> children = new ArrayList<List<Integer>>(n);
    for (int v = 0; v < n; v++) {
      children.add(new ArrayList<Integer>());
    }
    boolean[] seen = new boolean[n];
    seen[root] = true;
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    queue.add(root);
    int reached = 1;
    while (!queue.isEmpty()) {
      int v = queue.poll();
      for (int w : adjacency[v]) {
        if (!seen[w]) {
          seen[w] = true;
          reached++;
          children.get(v).add(w);
          queue.add(w);
        }
      }
    }
    if (reached != n) {
      return null;
    }
    int[][] result = new int[n][];
    for (int v = 0; v < n; v++) {
      result[v] = children.get(v).stream().mapToInt(Integer::intValue).toArray();
    }
    return result;
  }

  /**
   * A tidy drawing of the tree from <code>root</code>: the subtrees side by side as close as their
   * outlines allow, one unit apart on every level, and a parent midway between its first and last
   * child.
   *
   * @return <code>{along, depth}</code> of every vertex
   */
  private double[][] tidyTree(int[][] children, int root) {
    // the vertices breadth first, so every child comes after its parent
    int[] order = new int[n];
    int[] depth = new int[n];
    int count = 0;
    order[count++] = root;
    for (int i = 0; i < count; i++) {
      int v = order[i];
      for (int c : children[v]) {
        depth[c] = depth[v] + 1;
        order[count++] = c;
      }
    }
    // the leftmost and rightmost offset of every level of a subtree from its root
    double[][] left = new double[n][];
    double[][] right = new double[n][];
    double[] shift = new double[n];
    for (int i = count - 1; i >= 0; i--) {
      EvalEngine.get().checkTimeLimit();
      int v = order[i];
      int[] kids = children[v];
      double[] offsets = new double[kids.length];
      double[] rightmost = new double[0];
      for (int k = 0; k < kids.length; k++) {
        int c = kids[k];
        double offset = 0.0;
        if (k > 0) {
          offset = Double.NEGATIVE_INFINITY;
          int levels = Math.min(left[c].length, rightmost.length);
          for (int d = 0; d < levels; d++) {
            offset = Math.max(offset, rightmost[d] - left[c][d] + 1.0);
          }
        }
        offsets[k] = offset;
        if (right[c].length > rightmost.length) {
          int old = rightmost.length;
          rightmost = Arrays.copyOf(rightmost, right[c].length);
          Arrays.fill(rightmost, old, rightmost.length, Double.NEGATIVE_INFINITY);
        }
        for (int d = 0; d < right[c].length; d++) {
          rightmost[d] = Math.max(rightmost[d], right[c][d] + offset);
        }
      }
      double mid = kids.length == 0 ? 0.0 : (offsets[0] + offsets[kids.length - 1]) / 2.0;
      int height = 1;
      for (int c : kids) {
        height = Math.max(height, left[c].length + 1);
      }
      double[] l = new double[height];
      double[] r = new double[height];
      Arrays.fill(l, Double.POSITIVE_INFINITY);
      Arrays.fill(r, Double.NEGATIVE_INFINITY);
      l[0] = 0.0;
      r[0] = 0.0;
      for (int k = 0; k < kids.length; k++) {
        int c = kids[k];
        shift[c] = offsets[k] - mid;
        for (int d = 0; d < left[c].length; d++) {
          l[d + 1] = Math.min(l[d + 1], left[c][d] + shift[c]);
          r[d + 1] = Math.max(r[d + 1], right[c][d] + shift[c]);
        }
        left[c] = null;
        right[c] = null;
      }
      left[v] = l;
      right[v] = r;
    }
    double[][] points = new double[n][2];
    for (int i = 0; i < count; i++) {
      int v = order[i];
      for (int c : children[v]) {
        points[c][0] = points[v][0] + shift[c];
      }
      points[v][1] = depth[v];
    }
    return points;
  }

  /**
   * The coordinates of a point <code>along</code> a layer <code>depth</code> layers away from the
   * roots, the roots on the side <code>orientation</code>.
   */
  private static double[] orient(double along, double depth, IExpr orientation) {
    if (orientation == S.Bottom) {
      return new double[] {along, depth};
    }
    if (orientation == S.Left) {
      return new double[] {depth, -along};
    }
    if (orientation == S.Right) {
      return new double[] {-depth, -along};
    }
    return new double[] {along, -depth};
  }

  /**
   * The layered drawing. A tree, edge directions aside, is drawn tidily from <code>root</code>,
   * else from the only vertex no edge points to, else from its centre; with
   * <code>unitTree</code> it is scaled to edges of mean length 1. Every other graph has each vertex
   * one layer below the longest chain of edges leading to it (breadth first from
   * <code>root</code> if one is given), the vertices of a layer in the order of the mean position
   * of their neighbours, and layers at most <code>1/GoldenRatio</code> times the width apart.
   *
   * @param root the root vertex or <code>-1</code>
   */
  private double[][] layered(IExpr orientation, int root, boolean unitTree) {
    if (n > MAX_LAYERED_VERTICES) {
      return circular(null);
    }
    int[][] next = successors(root >= 0);
    List<Integer> sources = sources();
    int treeRoot = root >= 0 ? root : (sources.size() == 1 ? sources.get(0) : center());
    int[][] children = treeChildren(treeRoot);
    if (children != null) {
      double[][] points = tidyTree(children, treeRoot);
      for (int v = 0; v < n; v++) {
        points[v] = orient(points[v][0], points[v][1], orientation);
      }
      if (unitTree) {
        return unitMeanEdge(points);
      }
      // the deepest layer at 0, the vertices across a layer centred on the root
      int axis = orientation == S.Left || orientation == S.Right ? 0 : 1;
      double low = Double.POSITIVE_INFINITY;
      for (double[] p : points) {
        low = Math.min(low, p[axis]);
      }
      for (double[] p : points) {
        p[axis] -= low;
      }
      return points;
    }
    int[] layer = layers(next, sources, root);
    Layering layering = new Layering(layer, edges);
    return layering.points(layering.slot, orientation);
  }

  /**
   * The vertices every vertex has an edge to, each once in the order of the vertex list; with
   * <code>bothWays</code> the vertices it has an edge from as well.
   */
  private int[][] successors(boolean bothWays) {
    List<List<Integer>> successors = new ArrayList<List<Integer>>(n);
    for (int v = 0; v < n; v++) {
      successors.add(new ArrayList<Integer>());
    }
    for (int[] e : edges) {
      successors.get(e[0]).add(e[1]);
      if (bothWays) {
        successors.get(e[1]).add(e[0]);
      }
    }
    int[][] next = new int[n][];
    for (int v = 0; v < n; v++) {
      next[v] = successors.get(v).stream().mapToInt(Integer::intValue).sorted().distinct()
          .toArray();
    }
    return next;
  }

  /** The vertices no edge points to, in the order of the vertex list. */
  private List<Integer> sources() {
    int[] inDegree = new int[n];
    for (int[] e : edges) {
      inDegree[e[1]]++;
    }
    List<Integer> sources = new ArrayList<Integer>();
    for (int v = 0; v < n; v++) {
      if (inDegree[v] == 0) {
        sources.add(v);
      }
    }
    return sources;
  }

  /**
   * The layered drawing of every connected piece, the pieces packed by
   * {@link #packPieces(List, double[][], double[][])}.
   */
  private double[][] layeredPieces() {
    if (n > MAX_LAYERED_VERTICES) {
      return circular(null);
    }
    List<int[]> pieces = components();
    pieces.sort(Comparator.comparingInt(piece -> piece[0]));
    double[][] points = new double[n][];
    double[][] sizes = new double[pieces.size()][];
    for (int i = 0; i < pieces.size(); i++) {
      int[] piece = pieces.get(i);
      double[][] piecePoints =
          pieces.size() == 1 ? layeredDigraph() : piece(piece).layeredDigraph();
      double width = 0.0;
      double height = 0.0;
      for (int k = 0; k < piece.length; k++) {
        points[piece[k]] = piecePoints[k];
        width = Math.max(width, piecePoints[k][0]);
        height = Math.max(height, piecePoints[k][1]);
      }
      sizes[i] = new double[] {width, height};
    }
    return pieces.size() > 1 ? packPieces(pieces, sizes, points) : points;
  }

  /**
   * The layered drawing of a connected graph along the directions of its edges, moved to the
   * origin. A tree hanging from the only vertex no edge points to is drawn tidily from it. In any
   * other graph the vertices no edge points to make the top layer and every other vertex lies one
   * layer below the longest chain of edges leading to it; each vertex is placed at the median of
   * its neighbours on the layer above, the right one of the two middle ones if there is an even
   * number of them, then the vertices with neighbours below at the median of those, the vertices of
   * a layer at least one unit apart.
   */
  private double[][] layeredDigraph() {
    if (n == 1) {
      return new double[][] {{0.0, 0.0}};
    }
    List<Integer> sources = sources();
    if (sources.size() == 1 && treeChildren(sources.get(0)) != null) {
      return shiftToOrigin(layered(S.Top, -1, false));
    }
    int[] layer = layers(successors(false), sources, -1);
    Layering layering = new Layering(layer, edges);
    return layering.points(layering.medianPositions(), S.Top);
  }

  /**
   * Pack the drawings of several pieces, each moved to the origin and <code>sizes[i] = {width,
   * height}</code> large, in rows from the top left, the pieces in the order of
   * <code>pieces</code>, one unit apart and the tops of a row level. The number of pieces in a
   * row is the one which makes the block closest to a square.
   */
  private static double[][] packPieces(List<int[]> pieces, double[][] sizes, double[][] points) {
    int count = pieces.size();
    int columns = 1;
    double best = Double.POSITIVE_INFINITY;
    for (int c = 1; c <= count; c++) {
      double width = 0.0;
      double height = 0.0;
      for (int start = 0; start < count; start += c) {
        double rowWidth = 0.0;
        double rowHeight = 0.0;
        for (int i = start; i < Math.min(start + c, count); i++) {
          rowWidth += sizes[i][0] + 1.0;
          rowHeight = Math.max(rowHeight, sizes[i][1] + 1.0);
        }
        width = Math.max(width, rowWidth);
        height += rowHeight;
      }
      double misfit = Math.abs(Math.log(width / height));
      if (misfit < best - 1.0e-9) {
        best = misfit;
        columns = c;
      }
    }
    double[][] packed = new double[points.length][];
    double top = 0.0;
    for (int start = 0; start < count; start += columns) {
      double x = 0.0;
      double rowHeight = 0.0;
      for (int i = start; i < Math.min(start + columns, count); i++) {
        for (int v : pieces.get(i)) {
          packed[v] = new double[] {x + points[v][0], top - sizes[i][1] + points[v][1]};
        }
        x += sizes[i][0] + 1.0;
        rowHeight = Math.max(rowHeight, sizes[i][1] + 1.0);
      }
      top -= rowHeight;
    }
    return shiftToOrigin(packed);
  }

  /**
   * The layers of a layered drawing: a long edge passes through a dummy vertex on every layer it
   * crosses, and the vertices of every layer are ordered by the mean position of their neighbours,
   * alternately on the layer above and below.
   */
  private static final class Layering {

    private final int[] layer;

    private final int layers;

    /** the layer of every vertex, the dummy vertices after the vertices of the graph */
    private final List<Integer> rank;

    private final List<List<Integer>> rows;

    /** the neighbours of every vertex on the layer above */
    private final List<List<Integer>> up;

    /** the neighbours of every vertex on the layer below */
    private final List<List<Integer>> down;

    /** the position of every vertex in its layer */
    private final double[] slot;

    Layering(int[] layer, List<int[]> edges) {
      this.layer = layer;
      int count = 1;
      for (int l : layer) {
        count = Math.max(count, l + 1);
      }
      this.layers = count;
      rank = new ArrayList<Integer>(layer.length);
      for (int l : layer) {
        rank.add(l);
      }
      List<int[]> links = new ArrayList<int[]>();
      for (int[] e : edges) {
        int s = e[0];
        int d = e[1];
        if (rank.get(s) > rank.get(d)) {
          s = e[1];
          d = e[0];
        }
        int previous = s;
        for (int l = rank.get(s) + 1; l < rank.get(d); l++) {
          int dummy = rank.size();
          rank.add(l);
          links.add(new int[] {previous, dummy});
          previous = dummy;
        }
        if (!rank.get(previous).equals(rank.get(d))) {
          links.add(new int[] {previous, d});
        }
      }
      int total = rank.size();
      rows = new ArrayList<List<Integer>>(layers);
      for (int l = 0; l < layers; l++) {
        rows.add(new ArrayList<Integer>());
      }
      for (int v = 0; v < total; v++) {
        rows.get(rank.get(v)).add(v);
      }
      up = new ArrayList<List<Integer>>(total);
      down = new ArrayList<List<Integer>>(total);
      for (int v = 0; v < total; v++) {
        up.add(new ArrayList<Integer>());
        down.add(new ArrayList<Integer>());
      }
      for (int[] link : links) {
        down.get(link[0]).add(link[1]);
        up.get(link[1]).add(link[0]);
      }
      slot = new double[total];
      for (List<Integer> row : rows) {
        for (int k = 0; k < row.size(); k++) {
          slot[row.get(k)] = k;
        }
      }
      for (int sweep = 0; sweep < 8; sweep++) {
        EvalEngine.get().checkTimeLimit();
        boolean downward = sweep % 2 == 0;
        List<List<Integer>> neighbours = downward ? up : down;
        for (int i = 0; i < layers - 1; i++) {
          int l = downward ? i + 1 : layers - 2 - i;
          List<Integer> row = rows.get(l);
          double[] bary = new double[total];
          for (int v : row) {
            List<Integer> ns = neighbours.get(v);
            if (ns.isEmpty()) {
              bary[v] = slot[v];
            } else {
              double sum = 0.0;
              for (int u : ns) {
                sum += slot[u];
              }
              bary[v] = sum / ns.size();
            }
          }
          row.sort(Comparator.<Integer>comparingDouble(v -> bary[v])
              .thenComparingDouble(v -> slot[v]));
          for (int k = 0; k < row.size(); k++) {
            slot[row.get(k)] = k;
          }
        }
      }
    }

    /**
     * The position of every vertex across its layer: first at the median of its neighbours on the
     * layer above, layer by layer downwards, then at the median of its neighbours below, layer by
     * layer upwards.
     */
    double[] medianPositions() {
      double[] x = slot.clone();
      for (int l = 1; l < layers; l++) {
        place(rows.get(l), up, x);
      }
      for (int l = layers - 2; l >= 0; l--) {
        place(rows.get(l), down, x);
      }
      return x;
    }

    /**
     * Move the vertices of <code>row</code>, in their order, as close to the median of their
     * <code>neighbours</code> as they can get while staying at least one unit apart: the least sum
     * of the distances, found by pooling adjacent vertices which would otherwise collide.
     */
    private static void place(List<Integer> row, List<List<Integer>> neighbours, double[] x) {
      EvalEngine.get().checkTimeLimit();
      int k = row.size();
      // the targets less the room the vertices to the left take, which must not decrease
      double[] target = new double[k];
      for (int i = 0; i < k; i++) {
        int v = row.get(i);
        List<Integer> ns = neighbours.get(v);
        double wanted = x[v];
        if (!ns.isEmpty()) {
          double[] at = new double[ns.size()];
          for (int j = 0; j < at.length; j++) {
            at[j] = x[ns.get(j)];
          }
          Arrays.sort(at);
          wanted = at[at.length / 2];
        }
        target[i] = wanted - i;
      }
      int[] start = new int[k];
      double[] value = new double[k];
      int blocks = 0;
      for (int i = 0; i < k; i++) {
        start[blocks] = i;
        value[blocks] = target[i];
        blocks++;
        while (blocks > 1 && value[blocks - 2] > value[blocks - 1]) {
          blocks--;
          double[] pooled = Arrays.copyOfRange(target, start[blocks - 1], i + 1);
          Arrays.sort(pooled);
          value[blocks - 1] = pooled[(pooled.length - 1) / 2];
        }
      }
      for (int b = 0; b < blocks; b++) {
        int end = b + 1 < blocks ? start[b + 1] : k;
        for (int i = start[b]; i < end; i++) {
          x[row.get(i)] = value[b] + i;
        }
      }
    }

    /**
     * The coordinates of the vertices of the graph at the positions <code>along</code> their
     * layers, the layers at most <code>1/GoldenRatio</code> times the width apart, moved to the
     * origin.
     */
    double[][] points(double[] along, IExpr orientation) {
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (double a : along) {
        low = Math.min(low, a);
        high = Math.max(high, a);
      }
      double width = along.length > 0 ? high - low : 0.0;
      double span = Math.max(layers - 1.0, 0.0);
      double step =
          width > 0.0 && span > 0.0 ? Math.min(INV_GOLDEN * width / span, 1.0) : 1.0;
      double[][] points = new double[layer.length][];
      for (int v = 0; v < layer.length; v++) {
        points[v] = orient(along[v], layer[v] * step, orientation);
      }
      return shiftToOrigin(points);
    }
  }

  /**
   * The layer of every vertex: breadth first from <code>root</code> if it is not <code>-1</code>,
   * else one below the longest chain of edges from a source, the edges closing a cycle left out.
   */
  private int[] layers(int[][] next, List<Integer> sources, int root) {
    int[] layer = new int[n];
    Arrays.fill(layer, -1);
    if (root >= 0) {
      layer[root] = 0;
      ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
      queue.add(root);
      while (!queue.isEmpty()) {
        int v = queue.poll();
        for (int w : next[v]) {
          if (layer[w] < 0) {
            layer[w] = layer[v] + 1;
            queue.add(w);
          }
        }
      }
    } else {
      // depth first topological order without the back edges
      int[] state = new int[n];
      List<Integer> topological = new ArrayList<Integer>(n);
      List<List<Integer>> forward = new ArrayList<List<Integer>>(n);
      for (int v = 0; v < n; v++) {
        forward.add(new ArrayList<Integer>());
      }
      List<Integer> starts = new ArrayList<Integer>(sources);
      for (int v = 0; v < n; v++) {
        starts.add(v);
      }
      int[] position = new int[n];
      ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
      for (int start : starts) {
        if (state[start] != 0) {
          continue;
        }
        state[start] = 1;
        stack.push(start);
        while (!stack.isEmpty()) {
          int v = stack.peek();
          if (position[v] < next[v].length) {
            int w = next[v][position[v]++];
            if (state[w] != 1) {
              forward.get(v).add(w);
              if (state[w] == 0) {
                state[w] = 1;
                stack.push(w);
              }
            }
          } else {
            state[v] = 2;
            topological.add(v);
            stack.pop();
          }
        }
      }
      for (int i = topological.size() - 1; i >= 0; i--) {
        int v = topological.get(i);
        if (layer[v] < 0) {
          layer[v] = 0;
        }
        for (int w : forward.get(v)) {
          if (layer[w] < layer[v] + 1) {
            layer[w] = layer[v] + 1;
          }
        }
      }
    }
    for (int v = 0; v < n; v++) {
      if (layer[v] < 0) {
        layer[v] = 0;
      }
    }
    return layer;
  }

  /**
   * The order of the vertices around the circle: a depth first walk from the first vertex of every
   * component which tries the neighbour latest in the vertex list first, following directed edges
   * only forwards. In an undirected tree whose walk ends in exactly two dead ends, and in a strongly
   * connected directed cycle with one chord, the first run of the walk - from the root down to the
   * first dead end - is turned round, so that the tree or the cycle is laid out end to end.
   */
  private int[] circularOrder() {
    int[][] successors = new int[n][];
    int[] outDegree = new int[n];
    for (int[] e : edges) {
      outDegree[e[0]]++;
      if (!directed) {
        outDegree[e[1]]++;
      }
    }
    for (int v = 0; v < n; v++) {
      successors[v] = new int[outDegree[v]];
    }
    int[] fill = new int[n];
    // the edges starting at each vertex, which a tree has one fewer of than vertices
    int[] edgesFrom = new int[n];
    for (int[] e : edges) {
      successors[e[0]][fill[e[0]]++] = e[1];
      if (!directed) {
        successors[e[1]][fill[e[1]]++] = e[0];
      }
      edgesFrom[e[0]]++;
    }
    for (int v = 0; v < n; v++) {
      // latest in the vertex list first, each neighbour once
      successors[v] = Arrays.stream(successors[v]).distinct().boxed()
          .sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue).toArray();
    }
    int[] order = new int[n];
    int count = 0;
    boolean[] seen = new boolean[n];
    int[] parent = new int[n];
    Arrays.fill(parent, -1);
    int[] position = new int[n];
    ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
    for (int root = 0; root < n; root++) {
      if (seen[root]) {
        continue;
      }
      int start = count;
      seen[root] = true;
      order[count++] = root;
      stack.push(root);
      while (!stack.isEmpty()) {
        int v = stack.peek();
        if (position[v] < successors[v].length) {
          int w = successors[v][position[v]++];
          if (!seen[w]) {
            seen[w] = true;
            parent[w] = v;
            order[count++] = w;
            stack.push(w);
          }
        } else {
          stack.pop();
        }
      }
      if (directed ? isChordedCycle(order, start, count, successors)
          : isTree(order, start, count, edgesFrom)) {
        reverseFirstRun(order, start, count, parent);
      }
    }
    return order;
  }

  /** Whether the component <code>order[start..end-1]</code> has one edge fewer than vertices. */
  private static boolean isTree(int[] order, int start, int end, int[] edgesFrom) {
    int edgeCount = 0;
    for (int i = start; i < end; i++) {
      edgeCount += edgesFrom[order[i]];
    }
    return edgeCount + 1 == end - start;
  }

  /**
   * Whether the vertices <code>order[start..end-1]</code> a directed walk reached are a strongly
   * connected cycle with one chord: a single vertex with two successors, one of which is the single
   * vertex with two predecessors, every other vertex having one of each.
   */
  private boolean isChordedCycle(int[] order, int start, int end, int[][] successors) {
    int[] inDegree = new int[n];
    boolean[] inComponent = new boolean[n];
    for (int i = start; i < end; i++) {
      inComponent[order[i]] = true;
      for (int w : successors[order[i]]) {
        inDegree[w]++;
      }
    }
    int branch = -1;
    int join = -1;
    for (int i = start; i < end; i++) {
      int v = order[i];
      int out = successors[v].length;
      if (out < 1 || out > 2 || inDegree[v] < 1 || inDegree[v] > 2) {
        return false;
      }
      if (out == 2) {
        if (branch >= 0) {
          return false;
        }
        branch = v;
      }
      if (inDegree[v] == 2) {
        if (join >= 0) {
          return false;
        }
        join = v;
      }
    }
    if (branch < 0 || join < 0) {
      return false;
    }
    final int joinVertex = join;
    if (Arrays.stream(successors[branch]).noneMatch(w -> w == joinVertex)) {
      return false;
    }
    // strongly connected: every vertex the walk reached leads back to its root
    int[][] predecessors = new int[n][];
    int[] fill = new int[n];
    for (int i = start; i < end; i++) {
      predecessors[order[i]] = new int[inDegree[order[i]]];
    }
    for (int i = start; i < end; i++) {
      for (int w : successors[order[i]]) {
        predecessors[w][fill[w]++] = order[i];
      }
    }
    boolean[] reached = new boolean[n];
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    reached[order[start]] = true;
    queue.add(order[start]);
    int reachedCount = 1;
    while (!queue.isEmpty()) {
      for (int u : predecessors[queue.poll()]) {
        if (inComponent[u] && !reached[u]) {
          reached[u] = true;
          reachedCount++;
          queue.add(u);
        }
      }
    }
    return reachedCount == end - start;
  }

  /**
   * Turn round the first run of a walk - the root and its descendants down to the first dead end -
   * when the walk <code>order[start..end-1]</code> ends in exactly two dead ends.
   */
  private static void reverseFirstRun(int[] order, int start, int end, int[] parent) {
    boolean[] hasChild = new boolean[parent.length];
    for (int i = start + 1; i < end; i++) {
      hasChild[parent[order[i]]] = true;
    }
    int deadEnds = 0;
    for (int i = start + 1; i < end; i++) {
      if (!hasChild[order[i]]) {
        deadEnds++;
      }
    }
    int run = 1;
    while (start + run < end && parent[order[start + run]] == order[start + run - 1]) {
      run++;
    }
    if (deadEnds == 2 && run < end - start) {
      for (int i = start, j = start + run - 1; i < j; i++, j--) {
        int t = order[i];
        order[i] = order[j];
        order[j] = t;
      }
    }
  }

  /**
   * The vertices counterclockwise on the unit circle in the given order, the first one at the angle
   * <code>Pi/2 + 2*Pi/n</code>.
   *
   * @param order the vertex indices or <code>null</code> for the order of the vertex list
   */
  private double[][] circular(int[] order) {
    double[][] points = new double[n][2];
    if (n == 1) {
      return points;
    }
    for (int k = 0; k < n; k++) {
      double angle = Math.PI / 2.0 + (k + 1.0) * 2.0 * Math.PI / n;
      int v = order == null ? k : order[k];
      points[v][0] = Math.cos(angle);
      points[v][1] = Math.sin(angle);
    }
    return points;
  }

  /** The centre at the origin and the other vertices on the unit circle. */
  private double[][] star(int center) {
    double[][] points = new double[n][2];
    int leaves = n - 1;
    int j = 0;
    for (int v = 0; v < n; v++) {
      if (v == center) {
        continue;
      }
      double angle = leaves <= 2 ? j * Math.PI : Math.PI / 2.0 + 2.0 * Math.PI * (j + 2.0) / leaves;
      points[v][0] = Math.cos(angle);
      points[v][1] = Math.sin(angle);
      j++;
    }
    return points;
  }

  /**
   * The vertices column by column, <code>rows</code> in a column, one unit apart from 1. With
   * <code>pack</code> every connected component gets a grid of its own, and the grids are packed
   * side by side.
   */
  private double[][] grid(int rows, boolean pack) {
    List<int[]> components = pack ? components()
        : java.util.Collections.singletonList(java.util.stream.IntStream.range(0, n).toArray());
    double[][] points = new double[n][2];
    for (int[] component : components) {
      for (int k = 0; k < component.length; k++) {
        points[component[k]][0] = k / rows + 1.0;
        points[component[k]][1] = k % rows + 1.0;
      }
    }
    return components.size() > 1 ? packGrids(components, points) : points;
  }

  /**
   * The connected components, ignoring the directions of the edges, each a list of vertex indices
   * in the order of the vertex list; the largest first, and components of the same size in the
   * order of their first vertices.
   */
  private List<int[]> components() {
    int[] label = new int[n];
    Arrays.fill(label, -1);
    List<List<Integer>> members = new ArrayList<List<Integer>>();
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    for (int root = 0; root < n; root++) {
      if (label[root] >= 0) {
        continue;
      }
      int c = members.size();
      members.add(new ArrayList<Integer>());
      label[root] = c;
      queue.add(root);
      while (!queue.isEmpty()) {
        int v = queue.poll();
        for (int w : adjacency[v]) {
          if (label[w] < 0) {
            label[w] = c;
            queue.add(w);
          }
        }
      }
    }
    for (int v = 0; v < n; v++) {
      members.get(label[v]).add(v);
    }
    List<int[]> components = new ArrayList<int[]>(members.size());
    for (List<Integer> m : members) {
      components.add(m.stream().mapToInt(Integer::intValue).toArray());
    }
    components.sort((a, b) -> a.length != b.length ? Integer.compare(b.length, a.length)
        : Integer.compare(a[0], b[0]));
    return components;
  }

  /**
   * Pack the grids of several components side by side without scaling them. Every component is
   * padded by an eighth of the mean of the sums of the width and height of the components; the
   * padded boxes go into columns as high as the highest box, each box into the first column with
   * room for it, and the room left in a column is shared out below its boxes, so that every column
   * starts at the top.
   */
  private static double[][] packGrids(List<int[]> components, double[][] points) {
    int count = components.size();
    double[][] bounds = new double[count][];
    double padding = 0.0;
    for (int i = 0; i < count; i++) {
      double xMin = Double.POSITIVE_INFINITY;
      double xMax = Double.NEGATIVE_INFINITY;
      double yMin = Double.POSITIVE_INFINITY;
      double yMax = Double.NEGATIVE_INFINITY;
      for (int v : components.get(i)) {
        xMin = Math.min(xMin, points[v][0]);
        xMax = Math.max(xMax, points[v][0]);
        yMin = Math.min(yMin, points[v][1]);
        yMax = Math.max(yMax, points[v][1]);
      }
      bounds[i] = new double[] {xMin, xMax, yMin, yMax};
      padding += xMax - xMin + yMax - yMin;
    }
    padding /= 8.0 * count;
    double[] heights = new double[count];
    double columnHeight = 0.0;
    for (int i = 0; i < count; i++) {
      heights[i] = bounds[i][3] - bounds[i][2] + 2.0 * padding;
      columnHeight = Math.max(columnHeight, heights[i]);
    }
    List<List<Integer>> columns = new ArrayList<List<Integer>>();
    List<Double> used = new ArrayList<Double>();
    for (int i = 0; i < count; i++) {
      int column = -1;
      for (int c = 0; c < used.size(); c++) {
        if (used.get(c) + heights[i] <= columnHeight + 1.0e-9) {
          column = c;
          break;
        }
      }
      if (column < 0) {
        columns.add(new ArrayList<Integer>());
        used.add(0.0);
        column = columns.size() - 1;
      }
      columns.get(column).add(i);
      used.set(column, used.get(column) + heights[i]);
    }
    double[][] packed = new double[points.length][2];
    double x = 0.0;
    for (int c = 0; c < columns.size(); c++) {
      List<Integer> column = columns.get(c);
      double slack = (columnHeight - used.get(c)) / column.size();
      double y = 0.0;
      double width = 0.0;
      for (int i : column) {
        double[] b = bounds[i];
        for (int v : components.get(i)) {
          packed[v][0] = x + padding + points[v][0] - b[0];
          packed[v][1] = -y - padding + points[v][1] - b[3];
        }
        width = Math.max(width, b[1] - b[0] + 2.0 * padding);
        y += heights[i] + slack;
      }
      x += width;
    }
    return packed;
  }

  /**
   * <code>{"MultipartiteLayout", "VertexPartition" -&gt; {n1, n2, ...}}</code>: the parts of
   * consecutive vertices in columns.
   *
   * @return <code>null</code> if the partition doesn't add up to the vertices
   */
  private double[][] multipartite(IExpr partition) {
    if (!partition.isList()) {
      return null;
    }
    List<int[]> parts = new ArrayList<int[]>();
    int next = 0;
    for (IExpr size : (IAST) partition) {
      int k = size.toIntDefault();
      if (k <= 0 || next + k > n) {
        return null;
      }
      int[] part = new int[k];
      for (int i = 0; i < k; i++) {
        part[i] = next++;
      }
      parts.add(part);
    }
    return next == n ? partColumns(parts) : null;
  }

  /**
   * Part <code>i</code> on the vertical line <code>x = i</code>, its members evenly spaced and
   * centred; the column of the largest part with <code>k</code> members is
   * <code>0.05*(k-1) + (parts-1)/GoldenRatio</code> high.
   */
  private double[][] partColumns(List<int[]> parts) {
    double[][] points = new double[n][2];
    int k = 1;
    for (int[] part : parts) {
      k = Math.max(k, part.length);
    }
    double height = 0.05 * (k - 1.0) + INV_GOLDEN * Math.max(parts.size() - 1.0, 1.0);
    double step = k > 1 ? height / (k - 1.0) : height;
    for (int i = 0; i < parts.size(); i++) {
      int[] part = parts.get(i);
      double span = part.length - 1.0;
      for (int j = 0; j < part.length; j++) {
        points[part[j]][0] = i;
        points[part[j]][1] = (j - span / 2.0) * step;
      }
    }
    return points;
  }

  /**
   * The two colour classes of a bipartite graph in two columns, as far apart as the taller one is
   * high; every component is coloured breadth first from its first vertex.
   *
   * @return <code>null</code> if the graph is not bipartite
   */
  private double[][] bipartite() {
    int[] colour = new int[n];
    Arrays.fill(colour, -1);
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    for (int root = 0; root < n; root++) {
      if (colour[root] >= 0) {
        continue;
      }
      colour[root] = 0;
      queue.add(root);
      while (!queue.isEmpty()) {
        int v = queue.poll();
        for (int w : adjacency[v]) {
          if (colour[w] < 0) {
            colour[w] = 1 - colour[v];
            queue.add(w);
          } else if (colour[w] == colour[v]) {
            return null;
          }
        }
      }
    }
    List<int[]> parts = new ArrayList<int[]>(2);
    for (int c = 0; c < 2; c++) {
      final int col = c;
      parts.add(java.util.stream.IntStream.range(0, n).filter(v -> colour[v] == col).toArray());
    }
    double[][] points = partColumns(parts);
    double k = Math.max(Math.max(parts.get(0).length, parts.get(1).length), 1);
    double step = (0.05 * (k - 1.0) + INV_GOLDEN) / Math.max(k - 1.0, 1.0);
    for (double[] p : points) {
      p[0] *= k * step;
    }
    return points;
  }

  /**
   * The vertices in their order evenly spread along a spiral from pole to pole of a sphere, seen
   * from the side.
   */
  private double[][] spiral() {
    double radius = 0.42 * Math.sqrt(n);
    double theta = 0.0;
    double[][] points = new double[n][2];
    for (int k = 0; k < n; k++) {
      double z = -1.0 + 2.0 * k / (n - 1.0);
      double s = Math.sqrt(Math.max(1.0 - z * z, 0.0));
      double t = 0.0;
      if (k != 0 && k != n - 1) {
        theta += 3.6 / Math.sqrt(n) / s;
        t = theta;
      }
      points[k][0] = radius * s * Math.sin(t);
      points[k][1] = radius * z;
    }
    return shiftToOrigin(points);
  }

  /**
   * The breadth first tree from <code>root</code> on concentric circles, depth <code>d</code> on
   * the circle of radius <code>d</code>; every leaf gets an equal share of the full turn, the first
   * one just past the top, and an inner vertex sits in the middle of the share of its leaves. With
   * a <code>layerSizeFunction</code> the circle of depth <code>d</code> lies
   * <code>layerSizeFunction(d)</code> beyond the one before it. The drawing is scaled to a root
   * mean square edge length of 1.
   */
  private double[][] radial(int root, IExpr layerSizeFunction) {
    int[] depth = new int[n];
    Arrays.fill(depth, -1);
    int[] order = new int[n];
    int[] parent = new int[n];
    int count = 0;
    depth[root] = 0;
    parent[root] = -1;
    order[count++] = root;
    List<List<Integer>> children = new ArrayList<List<Integer>>(n);
    for (int v = 0; v < n; v++) {
      children.add(new ArrayList<Integer>());
    }
    for (int i = 0; i < count; i++) {
      int v = order[i];
      for (int w : adjacency[v]) {
        if (depth[w] < 0) {
          depth[w] = depth[v] + 1;
          parent[w] = v;
          children.get(v).add(w);
          order[count++] = w;
        }
      }
    }
    // the leaves depth first, so the leaves of a subtree are consecutive
    int[] first = new int[n];
    int[] last = new int[n];
    int leaves = 0;
    int[] position = new int[n];
    ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
    stack.push(root);
    first[root] = 0;
    while (!stack.isEmpty()) {
      int v = stack.peek();
      List<Integer> kids = children.get(v);
      if (position[v] == 0 && kids.isEmpty()) {
        leaves++;
      }
      if (position[v] < kids.size()) {
        int c = kids.get(position[v]++);
        first[c] = leaves;
        stack.push(c);
      } else {
        last[v] = leaves;
        stack.pop();
      }
    }
    double total = Math.max(leaves, 1);
    int deepest = 0;
    for (int d : depth) {
      deepest = Math.max(deepest, d);
    }
    double[] radius = layerOffsets(layerSizeFunction, deepest);
    double[][] points = new double[n][2];
    for (int v = 0; v < n; v++) {
      if (depth[v] <= 0) {
        continue;
      }
      double mid = (first[v] + last[v]) / 2.0;
      double angle = Math.PI / 2.0 + 2.0 * Math.PI * mid / total;
      points[v][0] = radius[depth[v]] * Math.cos(angle);
      points[v][1] = radius[depth[v]] * Math.sin(angle);
    }
    return unitRootMeanSquareEdge(points);
  }

  /**
   * The first vertex of the smallest eccentricity of a graph with at least one vertex, the edges
   * taken as undirected.
   */
  public static IExpr center(Graph<IExpr, ?> graph) {
    GraphLayouts layouts = new GraphLayouts(graph);
    return layouts.vertices.get(layouts.center());
  }

  /** The first vertex of the smallest eccentricity, the edges taken as undirected. */
  private int center() {
    if (n > MAX_LAYERED_VERTICES) {
      return 0;
    }
    int best = 0;
    int bestEccentricity = Integer.MAX_VALUE;
    int[] distance = new int[n];
    int[] queue = new int[n];
    for (int s = 0; s < n; s++) {
      Arrays.fill(distance, -1);
      distance[s] = 0;
      int head = 0;
      int tail = 0;
      queue[tail++] = s;
      int far = 0;
      while (head < tail) {
        int v = queue[head++];
        far = Math.max(far, distance[v]);
        for (int w : adjacency[v]) {
          if (distance[w] < 0) {
            distance[w] = distance[v] + 1;
            queue[tail++] = w;
          }
        }
      }
      if (far < bestEccentricity) {
        bestEccentricity = far;
        best = s;
      }
    }
    return best;
  }

  /** The points moved so that the smallest coordinate on each axis is 0. */
  private static double[][] shiftToOrigin(double[][] points) {
    double x0 = Double.POSITIVE_INFINITY;
    double y0 = Double.POSITIVE_INFINITY;
    for (double[] p : points) {
      x0 = Math.min(x0, p[0]);
      y0 = Math.min(y0, p[1]);
    }
    if (Double.isFinite(x0) && Double.isFinite(y0)) {
      for (double[] p : points) {
        p[0] = snap(p[0] - x0);
        p[1] = snap(p[1] - y0);
      }
    }
    return points;
  }

  /** The points scaled to a root mean square edge length of 1 and moved to the origin. */
  private double[][] unitRootMeanSquareEdge(double[][] points) {
    double sum = 0.0;
    int count = 0;
    for (int[] e : edges) {
      double dx = points[e[0]][0] - points[e[1]][0];
      double dy = points[e[0]][1] - points[e[1]][1];
      double squared = dx * dx + dy * dy;
      if (squared > 0.0) {
        sum += squared;
        count++;
      }
    }
    if (count > 0) {
      double rms = Math.sqrt(sum / count);
      for (double[] p : points) {
        p[0] /= rms;
        p[1] /= rms;
      }
    }
    return shiftToOrigin(points);
  }

  /**
   * The suboptions <code>"LayeredDigraphEmbedding"</code> takes. A specification with another one
   * is reported with the message <code>Graph::moptx</code>.
   */
  private static final String[] LAYERED_DIGRAPH_SUBOPTIONS = {"Tolerance", "Rotation",
      "Orientation", "RootVertex", "VertexLayerPosition", "VertexSizes"};

  /**
   * Whether every suboption of a layout specification is one its layout takes; prints the message
   * <code>Graph::moptx</code> for the first one which is not.
   *
   * @param layout the value of <code>GraphLayout</code>
   */
  public static boolean validSuboptions(IExpr layout, EvalEngine engine) {
    IExpr spec = vertexLayout(layout);
    if (!layoutName(spec).equals("LayeredDigraphEmbedding") || !spec.isList()) {
      return true;
    }
    for (IExpr option : (IAST) spec) {
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      String name = key.isString() ? key.toString()
          : key.isSymbol() ? ((org.matheclipse.core.interfaces.ISymbol) key).getSymbolName() : "";
      if (!java.util.Arrays.asList(LAYERED_DIGRAPH_SUBOPTIONS).contains(name)) {
        IASTAppendable known = F.ListAlloc(LAYERED_DIGRAPH_SUBOPTIONS.length);
        for (String suboption : LAYERED_DIGRAPH_SUBOPTIONS) {
          known.append(F.stringx(suboption));
        }
        // Method option `1` in `2` is not one of `3`.
        org.matheclipse.core.eval.Errors.printMessage(S.Graph, "moptx",
            F.list(F.stringx(name), F.stringx("LayeredDigraphDrawing"), known), engine);
        return false;
      }
    }
    return true;
  }

  /** The points scaled to edges of mean length 1 and moved to the origin. */
  private double[][] unitMeanEdge(double[][] points) {
    double sum = 0.0;
    int count = 0;
    for (int[] e : edges) {
      double dx = points[e[0]][0] - points[e[1]][0];
      double dy = points[e[0]][1] - points[e[1]][1];
      double length = Math.sqrt(dx * dx + dy * dy);
      if (length > 0.0) {
        sum += length;
        count++;
      }
    }
    if (count > 0) {
      double mean = sum / count;
      for (double[] p : points) {
        p[0] /= mean;
        p[1] /= mean;
      }
    }
    return shiftToOrigin(points);
  }

  /** Map a value within a rounding error of <code>0, +-1/2, +-1</code> to that value. */
  private static double snap(double value) {
    for (double target : new double[] {0.0, 0.5, -0.5, 1.0, -1.0}) {
      if (Math.abs(value - target) < 1.0e-14) {
        return target;
      }
    }
    return value;
  }
}
