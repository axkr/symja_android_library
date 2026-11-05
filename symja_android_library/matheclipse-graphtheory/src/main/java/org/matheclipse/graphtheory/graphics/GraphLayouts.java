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
 * A specification <code>{"VertexLayout" -&gt; spec, ...}</code> is the layout <code>spec</code>.
 */
public final class GraphLayouts {

  /** <code>1/GoldenRatio</code>, the height of a layered drawing relative to its width */
  private static final double INV_GOLDEN = 0.6180339887498949;

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
    this.vertices = new ArrayList<IExpr>(graph.vertexSet());
    this.n = vertices.size();
    this.index = new HashMap<IExpr, Integer>();
    for (int i = 0; i < n; i++) {
      index.put(vertices.get(i), i);
    }
    this.directed = graph.getType().isDirected();
    this.edges = new ArrayList<int[]>();
    int[] degree = new int[n];
    @SuppressWarnings("unchecked")
    Graph<IExpr, Object> g = (Graph<IExpr, Object>) graph;
    for (Object edge : g.edgeSet()) {
      int s = index.get(g.getEdgeSource(edge));
      int t = index.get(g.getEdgeTarget(edge));
      if (s != t) {
        edges.add(new int[] {s, t});
        degree[s]++;
        degree[t]++;
      }
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
        return grid(rows);
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
        return radial(root.isPresent() && index.containsKey(root) ? index.get(root) : center());
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
    List<List<Integer>> successors = new ArrayList<List<Integer>>(n);
    for (int v = 0; v < n; v++) {
      successors.add(new ArrayList<Integer>());
    }
    int[] inDegree = new int[n];
    for (int[] e : edges) {
      successors.get(e[0]).add(e[1]);
      inDegree[e[1]]++;
      if (root >= 0) {
        successors.get(e[1]).add(e[0]);
      }
    }
    int[][] next = new int[n][];
    for (int v = 0; v < n; v++) {
      next[v] = successors.get(v).stream().mapToInt(Integer::intValue).sorted().distinct()
          .toArray();
    }
    List<Integer> sources = new ArrayList<Integer>();
    for (int v = 0; v < n; v++) {
      if (inDegree[v] == 0) {
        sources.add(v);
      }
    }
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
    int layers = 1;
    for (int l : layer) {
      layers = Math.max(layers, l + 1);
    }
    // a long edge passes through a dummy vertex on every layer it crosses
    List<Integer> rank = new ArrayList<Integer>(n);
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
    List<List<Integer>> rows = new ArrayList<List<Integer>>(layers);
    for (int l = 0; l < layers; l++) {
      rows.add(new ArrayList<Integer>());
    }
    for (int v = 0; v < total; v++) {
      rows.get(rank.get(v)).add(v);
    }
    List<List<Integer>> up = new ArrayList<List<Integer>>(total);
    List<List<Integer>> down = new ArrayList<List<Integer>>(total);
    for (int v = 0; v < total; v++) {
      up.add(new ArrayList<Integer>());
      down.add(new ArrayList<Integer>());
    }
    for (int[] link : links) {
      down.get(link[0]).add(link[1]);
      up.get(link[1]).add(link[0]);
    }
    double[] slot = new double[total];
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
    int widest = 1;
    for (List<Integer> row : rows) {
      widest = Math.max(widest, row.size());
    }
    double width = widest - 1;
    double span = Math.max(layers - 1.0, 0.0);
    double step = width > 0.0 && span > 0.0 ? Math.min(INV_GOLDEN * width / span, 1.0) : 1.0;
    double[][] points = new double[n][];
    for (int v = 0; v < n; v++) {
      points[v] = orient(slot[v], layer[v] * step, orientation);
    }
    return shiftToOrigin(points);
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
   * component, the most recently listed edge first; a directed graph runs the other way round.
   */
  private int[] circularOrder() {
    int[] order = new int[n];
    int count = 0;
    boolean[] seen = new boolean[n];
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
        int[] neighbours = adjacency[v];
        if (position[v] < neighbours.length) {
          int w = neighbours[neighbours.length - 1 - position[v]++];
          if (!seen[w]) {
            seen[w] = true;
            order[count++] = w;
            stack.push(w);
          }
        } else {
          stack.pop();
        }
      }
      if (directed) {
        for (int i = start + 1, j = count - 1; i < j; i++, j--) {
          int t = order[i];
          order[i] = order[j];
          order[j] = t;
        }
      }
    }
    return order;
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

  /** The vertices column by column, <code>rows</code> in a column, one unit apart from 1. */
  private double[][] grid(int rows) {
    double[][] points = new double[n][2];
    for (int k = 0; k < n; k++) {
      points[k][0] = k / rows + 1.0;
      points[k][1] = k % rows + 1.0;
    }
    return points;
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
   * one just past the top, and an inner vertex sits in the middle of the share of its leaves.
   */
  private double[][] radial(int root) {
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
    double[][] points = new double[n][2];
    for (int v = 0; v < n; v++) {
      if (depth[v] <= 0) {
        continue;
      }
      double mid = (first[v] + last[v]) / 2.0;
      double angle = Math.PI / 2.0 + 2.0 * Math.PI * mid / total;
      points[v][0] = depth[v] * Math.cos(angle);
      points[v][1] = depth[v] * Math.sin(angle);
    }
    return unitMeanEdge(points);
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
