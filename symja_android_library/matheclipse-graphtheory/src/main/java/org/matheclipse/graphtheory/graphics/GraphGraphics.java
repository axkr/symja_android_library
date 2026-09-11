package org.matheclipse.graphtheory.graphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.AsSubgraph;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Utility class to convert a {@link GraphExpr} (or Graph IAST) into a {@link S#Graphics} expression
 * suitable for rendering with {@link SVGGraphics}. *
 * <p>
 * Supported Options:
 * <ul>
 * <li>VertexCoordinates: Explicit list of {x,y} or Automatic.</li>
 * <li>VertexStyle: Color/Style for vertices.</li>
 * <li>EdgeStyle: Color/Style for edges.</li>
 * <li>VertexLabels: "Name" to label vertices, or None.</li>
 * <li>VertexSize: Radius of vertex disks.</li>
 * <li>Arrowheads: Size of arrowheads for directed graphs.</li>
 * <li>GraphLayout: "SpringEmbedding" (default), "DiscreteSpiralEmbedding",
 * "LayeredDigraphEmbedding", "LayeredEmbedding", "Tree", "CircularEmbedding", "StarEmbedding".</li>
 * </ul>
 */
public class GraphGraphics {

  // Helper class for Tree Layout
  private static class LayoutNode {
    IExpr id;
    int depth;
    double x;
    double y;
    List<LayoutNode> children = new ArrayList<>();

    LayoutNode(IExpr id, int depth) {
      this.id = id;
      this.depth = depth;
    }
  }

  public final static int X_GRAPH_LAYOUT = 1;

  public final static int X_DIRECTED_EDGES = 0;

  public final static int X_VERTEX_SIZE = 2;

  public final static int X_VERTEX_LABELS = 3;

  public static IBuiltInSymbol[] defaultGraphOptionKeys() {
    return new IBuiltInSymbol[] {S.DirectedEdges, S.GraphLayout, S.VertexSize, S.VertexLabels};
  }

  public static IExpr[] defaultGraphOptionValues() {
    return new IExpr[] {S.False, F.stringx("LayeredEmbedding"), S.Automatic, S.None};
  }

  private final Graph<IExpr, ?> graph;
  private final IAST options;
  // Configuration
  /** The options that describe the graph and mean nothing to <code>Graphics</code>. */
  private static final Set<IExpr> GRAPH_OPTIONS = new HashSet<>(java.util.Arrays.asList(
      S.VertexStyle, S.EdgeStyle, S.VertexSize, S.VertexLabels, S.VertexShapeFunction,
      S.VertexShape, S.GraphLayout, S.GraphStyle, S.DirectedEdges, S.VertexCoordinates,
      S.EdgeWeight, S.VertexWeight, S.VertexLabelStyle, S.EdgeLabels, S.EdgeLabelStyle,
      S.EdgeShapeFunction, S.GraphHighlight, S.GraphHighlightStyle, S.Arrowheads));

  private Map<IExpr, double[]> vertexCoords = new HashMap<>();
  private IExpr vertexStyle = defaultVertexStyle();
  private IExpr edgeStyle = defaultEdgeStyle();
  /** <code>VertexStyle -> {v -> style, ...}</code>, or {@link F#NIL} */
  private IExpr vertexStyles = F.NIL;
  /** <code>VertexSize -> {v -> size, ...}</code>, or {@link F#NIL} */
  private IExpr vertexSizes = F.NIL;
  /** <code>EdgeStyle -> {edge -> style, ...}</code>, or {@link F#NIL} */
  private IExpr edgeStyles = F.NIL;
  private boolean showVertexLabels = false;
  /** a label sits on its vertex instead of below it, as the named graph styles draw it */
  private boolean centeredLabels = false;
  /** the radius of a vertex when no <code>VertexSize</code> is given */
  private double vertexSize = 0.05;
  /**
   * <code>VertexSize -> s</code>: the diameter of a vertex as a fraction of the smallest distance
   * between two vertices, as in Mathematica; <code>NaN</code> when no size is given
   */
  private double vertexSizeFraction = Double.NaN;
  /** <code>"Disk"</code>, <code>"Diamond"</code>, <code>"Square"</code>, ... */
  private String vertexShape = "Disk";
  /** the width and height of a vertex of a named graph style, as fractions of the vertex distance */
  private double themeWidth = 0.2;
  private double themeHeight = 0.2;
  /** the columns of <code>"GridEmbedding"</code>, or 0 for a square grid */
  private int gridColumns = 0;

  private double arrowHeadSize = 0.02;

  private String graphLayout = "SpringEmbedding";

  private boolean directed;

  /**
   * Construct from a GraphExpr or Graph IAST.
   */
  public GraphGraphics(IExpr graphExpr) {
    if (graphExpr instanceof GraphExpr) {
      GraphExpr<?> graphExpr2 = (GraphExpr<?>) graphExpr;
      this.graph = graphExpr2.toData();
      this.options = graphExpr2.options();
    } else if (graphExpr.isAST(S.Graph)) {
      this.graph = GraphExpr.createGraph((IAST) graphExpr);

      IASTAppendable opts = F.ListAlloc();
      for (int i = 1; i <= ((IAST) graphExpr).size(); i++) {
        IExpr arg = ((IAST) graphExpr).get(i);
        if (arg.isRuleAST()) {
          opts.append(arg);
        }
      }
      this.options = opts;
    } else {
      throw new IllegalArgumentException("Input must be a Graph expression.");
    }

    this.directed = this.graph.getType().isDirected();
  }

  private <E> void calculateLayout(Graph<IExpr, E> g) {
    IExpr coordsOpt = getOption(S.VertexCoordinates);
    boolean hasCoords = false;

    if (coordsOpt.isList()) {
      IAST list = (IAST) coordsOpt;
      if (list.argSize() > 0 && list.arg1().isList()) {
        int i = 1;
        for (IExpr v : g.vertexSet()) {
          if (i >= list.size()) {
            break;
          }
          IExpr pt = list.get(i++);
          if (pt.isList() && ((IAST) pt).size() >= 3) {
            vertexCoords.put(v, new double[] {((INumber) ((IAST) pt).arg1()).reDoubleValue(),
                ((INumber) ((IAST) pt).arg2()).reDoubleValue()});
          }
        }
        hasCoords = true;
      } else if (list.argSize() > 0 && list.arg1().isRuleAST()) {
        for (IExpr rule : list) {
          if (rule.isRuleAST()) {
            IExpr v = ((IAST) rule).arg1();
            IExpr c = ((IAST) rule).arg2();
            if (c.isList()) {
              vertexCoords.put(v,
                  new double[] {((INumber) ((IAST) c).arg1()).reDoubleValue(),
                      ((IAST) c).arg2() instanceof INumber
                          ? ((INumber) ((IAST) c).arg2()).reDoubleValue()
                          : 0.0});
            }
          }
        }
        hasCoords = true;
      }
    }

    // Fallback: Automatic Layout
    if (!hasCoords || vertexCoords.size() < g.vertexSet().size()) {
      ConnectivityInspector<IExpr, ?> inspector = new ConnectivityInspector<>(g);
      List<Set<IExpr>> connectedSets = new ArrayList<>(inspector.connectedSets());
      connectedSets.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed());

      int numComponents = connectedSets.size();
      if (numComponents == 0)
        return;

      int gridCols = (int) Math.ceil(Math.sqrt(numComponents));
      int maxComponentSize = connectedSets.get(0).size();
      double componentScale = Math.max(3.0, Math.sqrt(maxComponentSize) * 1.5);
      double cellSize = componentScale * 1.2;

      for (int i = 0; i < numComponents; i++) {
        Set<IExpr> component = connectedSets.get(i);
        int row = i / gridCols;
        int col = i % gridCols;
        double cellCenterX = col * cellSize;
        double cellCenterY = -row * cellSize;

        if ((graphLayout.contains("Tree") || graphLayout.contains("Layered"))) {
          layoutComponentTree(g, component, cellCenterX, cellCenterY, componentScale);
        } else if (graphLayout.contains("DiscreteSpiral")) {
          layoutComponentDiscreteSpiral(g, component, cellCenterX, cellCenterY, componentScale);
        } else if (graphLayout.contains("StarEmbedding")) {
          layoutComponentStar(g, component, cellCenterX, cellCenterY, componentScale);
        } else if (graphLayout.contains("GridEmbedding")) {
          layoutComponentGrid(g, component, cellCenterX, cellCenterY);
        } else if (graphLayout.contains("CircularEmbedding")) {
          layoutComponentCircular(g, component, cellCenterX, cellCenterY, componentScale);
        } else {
          layoutComponentSpring(g, component, cellCenterX, cellCenterY, componentScale);
        }
      }
    }
  }


  /**
   * Layouts a single component using a Circular embedding.
   */
  private <E> void layoutComponentCircular(Graph<IExpr, E> g, Set<IExpr> component, double centerX,
      double centerY, double scale) {
    List<IExpr> vertices = new ArrayList<>(component);
    if (vertices.isEmpty()) {
      return;
    }
    if (vertices.size() == 1) {
      vertexCoords.put(vertices.get(0), new double[] {centerX, centerY});
      return;
    }

    // Sort vertices numerically if possible to handle Range(n) correctly.
    vertices.sort(Comparators.CANONICAL_COMPARATOR);

    if (vertices.size() > 2) {
      reorderOnCircle(g, vertices);
    }

    int n = vertices.size();
    double radius = scale * 0.5;
    double angleStep = 2.0 * Math.PI / n;

    for (int i = 0; i < n; i++) {
      double angle = i * angleStep;
      double x = centerX + radius * Math.cos(angle);
      double y = centerY + radius * Math.sin(angle);
      vertexCoords.put(vertices.get(i), new double[] {x, y});
    }
  }

  private void drawCurvedEdge(double[] p1, double[] p2, IASTAppendable primitives) {
    double x1 = p1[0];
    double y1 = p1[1];
    double x2 = p2[0];
    double y2 = p2[1];

    double mx = (x1 + x2) / 2.0;
    double my = (y1 + y2) / 2.0;
    double dx = x2 - x1;
    double dy = y2 - y1;
    double curv = 0.2;
    double cx = mx - dy * curv;
    double cy = my + dx * curv;

    IASTAppendable points = F.ListAlloc(15);
    int steps = 15;
    for (int i = 0; i <= steps; i++) {
      double t = (double) i / steps;
      double u = 1 - t;
      double px = u * u * x1 + 2 * u * t * cx + t * t * x2;
      double py = u * u * y1 + 2 * u * t * cy + t * t * y2;
      points.append(F.List(F.num(px), F.num(py)));
    }
    primitives.append(F.Arrow(points));
  }

  private <E> void drawEdges(Graph<IExpr, E> g, IASTAppendable primitives) {
    for (E e : g.edgeSet()) {
      IExpr source = g.getEdgeSource(e);
      IExpr target = g.getEdgeTarget(e);

      double[] p1 = vertexCoords.get(source);
      double[] p2 = vertexCoords.get(target);

      if (p1 != null && p2 != null) {
        // an edge with a style of its own is drawn in a list with it, so the style stays there
        IASTAppendable sink = primitives;
        IExpr style = GraphExpr.edgeProperty(edgeStyles, source, target, directed);
        if (style.isPresent()) {
          sink = F.ListAlloc(2);
          sink.append(style);
        }
        if (source.equals(target)) {
          drawSelfLoop(p1, sink);
        } else if (directed && g.containsEdge(target, source)) {
          drawCurvedEdge(p1, p2, sink);
        } else {
          IAST coordList =
              F.List(F.List(F.num(p1[0]), F.num(p1[1])), F.List(F.num(p2[0]), F.num(p2[1])));
          if (directed) {
            sink.append(F.Arrow(coordList));
          } else {
            sink.append(F.Line(coordList));
          }
        }
        if (sink != primitives) {
          primitives.append(sink);
        }
      }
    }
  }

  private void drawSelfLoop(double[] p, IASTAppendable primitives) {
    double x = p[0];
    double y = p[1];
    double r = Math.max(vertexSize * 6.5, 0.4);

    double x1 = x - r * 0.8;
    double y1 = y + r;
    double x2 = x + r * 0.8;
    double y2 = y + r;

    IASTAppendable points = F.ListAlloc(25);
    int steps = 20;
    for (int i = 0; i <= steps; i++) {
      double t = (double) i / steps;
      double u = 1 - t;
      double px = u * u * u * x + 3 * u * u * t * x1 + 3 * u * t * t * x2 + t * t * t * x;
      double py = u * u * u * y + 3 * u * u * t * y1 + 3 * u * t * t * y2 + t * t * t * y;
      points.append(F.List(F.num(px), F.num(py)));
    }

    if (directed) {
      primitives.append(F.Arrow(points));
    } else {
      primitives.append(F.Line(points));
    }
  }

  private IExpr getOption(ISymbol key) {
    for (IExpr opt : options) {
      if (opt.isRuleAST() && opt.first() == key) {
        return opt.second();
      }
    }
    return F.NIL;
  }

  /**
   * Layouts a single component using a discrete spiral on an integer grid.
   */
  private <E> void layoutComponentDiscreteSpiral(Graph<IExpr, E> g, Set<IExpr> component,
      double centerX, double centerY, double scale) {
    List<IExpr> vertices = new ArrayList<>(component);

    // Sort vertices numerically if possible to handle Range(n) correctly.
    vertices.sort(Comparators.CANONICAL_COMPARATOR);

    Map<IExpr, int[]> grid = new HashMap<>();

    // Standard Ulam Spiral Logic
    // Start at center (0,0)
    int x = 0;
    int y = 0;

    // Initial direction: Right (1, 0)
    int dx = 1;
    int dy = 0;

    int segmentLength = 1; // How many steps in current direction
    int segmentPassed = 0; // Steps taken in current segment
    int turn = 0; // Number of turns made

    for (int i = 0; i < vertices.size(); i++) {
      grid.put(vertices.get(i), new int[] {x, y});

      // Stop after placing the last vertex
      if (i == vertices.size() - 1)
        break;

      // Move to next grid point
      x += dx;
      y += dy;
      segmentPassed++;

      // Check if we need to turn
      if (segmentPassed == segmentLength) {
        segmentPassed = 0;

        // Rotate 90 degrees Counter-Clockwise (Standard math spiral)
        // (dx, dy) -> (-dy, dx)
        // Right(1,0) -> Up(0,1) -> Left(-1,0) -> Down(0,-1) -> Right...
        int temp = dx;
        dx = -dy;
        dy = temp;

        turn++;
        // Increase segment length every 2 turns (1, 1, 2, 2, 3, 3...)
        if (turn % 2 == 0) {
          segmentLength++;
        }
      }
    }

    // Normalize coordinates to fit in the bounding box
    double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    for (int[] p : grid.values()) {
      if (p[0] < minX)
        minX = p[0];
      if (p[0] > maxX)
        maxX = p[0];
      if (p[1] < minY)
        minY = p[1];
      if (p[1] > maxY)
        maxY = p[1];
    }

    double w = maxX - minX;
    double h = maxY - minY;
    double maxDim = Math.max(w, h);
    if (maxDim < 1e-9)
      maxDim = 1.0;

    double boxCenterX = minX + w / 2.0;
    double boxCenterY = minY + h / 2.0;

    for (Map.Entry<IExpr, int[]> entry : grid.entrySet()) {
      int[] p = entry.getValue();

      // Normalize to -0.5 ... 0.5 space
      double normX = (p[0] - boxCenterX) / maxDim;
      double normY = (p[1] - boxCenterY) / maxDim;

      // Apply scale and center
      double finalX = centerX + normX * scale;
      double finalY = centerY + normY * scale;

      vertexCoords.put(entry.getKey(), new double[] {finalX, finalY});
    }
  }

  /**
   * Layouts a single component using a force-directed algorithm (Fruchterman-Reingold). This
   * creates the desired "organic" look with central structures and radiating trees.
   */
  private <E> void layoutComponentSpring(Graph<IExpr, E> g, Set<IExpr> component, double centerX,
      double centerY, double scale) {
    int n = component.size();
    if (n == 0)
      return;
    if (n == 1) {
      vertexCoords.put(component.iterator().next(), new double[] {centerX, centerY});
      return;
    }

    // 1. Create a subgraph for the component
    // AsSubgraph creates a live view, which is efficient.
    Graph<IExpr, E> subgraph = new AsSubgraph<>(g, component);

    // 2. Setup the layout model and algorithm
    // Optimal box calculation: Scale box size by sqrt of vertices to maintain density
    double dim = Math.max(200.0, 50.0 * Math.sqrt(n));
    LayoutModel2D<IExpr> layoutModel = new MapLayoutModel2D<IExpr>(new Box2D(dim, dim));

    // Use Fruchterman-Reingold layout.
    // More iterations for larger components ensure better convergence.
    FRLayoutAlgorithm2D<IExpr, E> layoutAlg = new FRLayoutAlgorithm2D<>(Math.max(200, n * 20));

    // 3. Run the layout
    layoutAlg.layout(subgraph, layoutModel);
    // 4. Transform and store coordinates

    // Find the bounds of the computed layout to center and scale it correctly.
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
    for (IExpr v : component) {
      Point2D p = layoutModel.get(v);
      minX = Math.min(minX, p.getX());
      minY = Math.min(minY, p.getY());
      maxX = Math.max(maxX, p.getX());
      maxY = Math.max(maxY, p.getY());
    }

    double layoutWidth = maxX - minX;
    double layoutHeight = maxY - minY;
    double layoutCenterX = minX + layoutWidth / 2.0;
    double layoutCenterY = minY + layoutHeight / 2.0;

    // Prevent division by zero for degenerate cases
    double maxDim = Math.max(layoutWidth, layoutHeight);
    maxDim = Math.max(maxDim, 1e-9);

    for (IExpr v : component) {
      Point2D p = layoutModel.get(v);

      // Translate to (0,0)
      double localX = p.getX() - layoutCenterX;
      double localY = p.getY() - layoutCenterY;

      // Scale to fit within the target box, preserving aspect ratio.
      double normX = localX / maxDim;
      double normY = localY / maxDim;

      // Translate to final grid cell center and apply final scale
      double finalX = centerX + normX * scale;
      double finalY = centerY + normY * scale;

      vertexCoords.put(v, new double[] {finalX, finalY});
    }
  }

  /**
   * Layouts a single component using a Star embedding.
   */
  private <E> void layoutComponentStar(Graph<IExpr, E> g, Set<IExpr> component, double centerX,
      double centerY, double scale) {
    List<IExpr> vertices = new ArrayList<>(component);
    if (vertices.isEmpty()) {
      return;
    }
    if (vertices.size() == 1) {
      vertexCoords.put(vertices.get(0), new double[] {centerX, centerY});
      return;
    }

    // Sort vertices numerically if possible to handle Range(n) correctly.
    vertices.sort(Comparators.CANONICAL_COMPARATOR);

    // Find the vertex with the maximum degree to start as center
    IExpr center = vertices.get(0);
    int maxDegree = -1;
    for (IExpr v : vertices) {
      int d = g.degreeOf(v);
      if (d > maxDegree) {
        maxDegree = d;
        center = v;
      }
    }

    vertexCoords.put(center, new double[] {centerX, centerY});

    // Place remaining vertices in a circle
    List<IExpr> others = new ArrayList<>(vertices);
    others.remove(center);

    if (others.size() > 2) {
      reorderOnCircle(g, others);
    }

    int n = others.size();
    double radius = scale * 0.5;
    double angleStep = 2.0 * Math.PI / n;

    for (int i = 0; i < n; i++) {
      double angle = i * angleStep;
      double x = centerX + radius * Math.cos(angle);
      double y = centerY + radius * Math.sin(angle);
      vertexCoords.put(others.get(i), new double[] {x, y});
    }
  }

  /**
   * Layouts a single component using a Leaf-Counting algorithm. This ensures proper tree structure
   * (parallel lanes).
   */
  private <E> void layoutComponentTree(Graph<IExpr, E> g, Set<IExpr> component, double centerX,
      double centerY, double scale) {
    if (component.isEmpty())
      return;
    if (component.size() == 1) {
      vertexCoords.put(component.iterator().next(), new double[] {centerX, centerY});
      return;
    }

    // 1. Determine Root
    // For directed, find node with in-degree 0.
    // For undirected (or cycle), picking the one with max degree or first available is acceptable
    // fallback.
    IExpr rootExpr = component.iterator().next();
    if (directed) {
      for (IExpr v : component) {
        // We must check degree *within the subgraph* of the component
        // Using Graphs.neighborListOf would check global
        // Simple counting:
        int inDegree = 0;
        for (E e : g.edgeSet()) {
          if (component.contains(g.getEdgeSource(e)) && component.contains(g.getEdgeTarget(e))) {
            if (g.getEdgeTarget(e).equals(v))
              inDegree++;
          }
        }
        if (inDegree == 0) {
          rootExpr = v;
          break;
        }
      }
    }

    // 2. Build Spanning Tree (BFS) for Layout
    LayoutNode root = new LayoutNode(rootExpr, 0);
    Map<IExpr, LayoutNode> nodeMap = new HashMap<>();
    nodeMap.put(rootExpr, root);

    LinkedList<LayoutNode> queue = new LinkedList<>();
    queue.add(root);
    Set<IExpr> visited = new HashSet<>();
    visited.add(rootExpr);

    while (!queue.isEmpty()) {
      LayoutNode current = queue.poll();
      List<IExpr> neighbors = Graphs.neighborListOf(g, current.id);

      // If undirected, we need to filter parent. If directed, neighbors are successors.
      // For undirected tree layout, we treat 'current' as parent.
      for (IExpr neighbor : neighbors) {
        if (component.contains(neighbor) && !visited.contains(neighbor)) {
          visited.add(neighbor);
          LayoutNode child = new LayoutNode(neighbor, current.depth + 1);
          current.children.add(child);
          nodeMap.put(neighbor, child);
          queue.add(child);
        }
      }
    }

    // 3. Recursive Leaf-Counting Layout
    // This assigns X based on the sequence of leaves, ensuring parallel lanes.
    layoutTreeRecursive(root, new int[] {0});

    // Shift root X to 0 so the tree centers nicely on (centerX, centerY)
    double rootX = root.x;

    // 4. Transform to global coordinates
    // Normalize to component bounds
    double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    for (LayoutNode n : nodeMap.values()) {
      // Shift local X by root position
      n.x -= rootX;

      if (n.x < minX)
        minX = n.x;
      if (n.x > maxX)
        maxX = n.x;

      // Y is -depth
      double y = -n.depth;
      n.y = y;
      if (y < minY)
        minY = y;
      if (y > maxY)
        maxY = y;
    }

    double width = maxX - minX;
    double height = maxY - minY;
    double maxDim = Math.max(width, height);
    if (maxDim < 1e-9)
      maxDim = 1.0;

    for (LayoutNode n : nodeMap.values()) {
      // Normalize to a -0.5 to 0.5 coordinate space relative to max dimension
      double normX = n.x / maxDim;

      // For Y, we usually want the root at the top.
      // Current range is [minY, maxY] where maxY=0 (root).
      // Let's center it vertically around 0.
      double normY = (n.y - (minY + maxY) / 2.0) / maxDim;

      // Translate to final grid cell center and apply final scale
      double finalX = centerX + normX * scale;
      double finalY = centerY + normY * scale;

      vertexCoords.put(n.id, new double[] {finalX, finalY});
    }
  }

  /**
   * Recursively assigns X coordinates based on leaf order. - Leaves get sequential integer indices.
   * - Parents are centered over their children.
   */
  private void layoutTreeRecursive(LayoutNode node, int[] leafCounter) {
    if (node.children.isEmpty()) {
      node.x = leafCounter[0];
      leafCounter[0]++;
    } else {
      for (LayoutNode child : node.children) {
        layoutTreeRecursive(child, leafCounter);
      }
      double firstChildX = node.children.get(0).x;
      double lastChildX = node.children.get(node.children.size() - 1).x;
      node.x = (firstChildX + lastChildX) / 2.0;
    }
  }

  private <E> void reorderOnCircle(Graph<IExpr, E> g, List<IExpr> nodes) {
    Set<IExpr> nodeSet = new HashSet<>(nodes);
    List<IExpr> path = new ArrayList<>(nodes.size());
    Set<IExpr> visited = new HashSet<>();

    // Start with the first node
    IExpr current = nodes.get(0);
    path.add(current);
    visited.add(current);

    while (path.size() < nodes.size()) {
      IExpr next = null;
      // Find unvisited neighbor in nodeSet
      Set<E> edges = g.edgesOf(current);

      for (E e : edges) {
        IExpr neighbor = Graphs.getOppositeVertex(g, e, current);
        if (nodeSet.contains(neighbor) && !visited.contains(neighbor)) {
          next = neighbor;
          break;
        }
      }

      if (next != null) {
        visited.add(next);
        path.add(next);
        current = next;
      } else {
        // No unvisited neighbor in the set.
        // Find first unvisited node from original list to restart
        for (IExpr n : nodes) {
          if (!visited.contains(n)) {
            visited.add(n);
            path.add(n);
            current = n;
            // Also check if this new starting node is connected to the previous part
            // (minimizing jumps visually, though purely heuristic here)
            break;
          }
        }
      }
    }

    // Replace nodes with ordered path
    nodes.clear();
    nodes.addAll(path);
  }

  private void parseOptions() {
    // a named style first, so the graph's own options can still change a part of it
    applyGraphStyle(getOption(S.GraphStyle));

    IExpr vs = getOption(S.VertexStyle);
    if (vs.isPresent()) {
      if (GraphExpr.isPropertyRuleList(vs)) {
        this.vertexStyles = vs;
        IExpr common = GraphExpr.propertyDefault(vs);
        if (common.isPresent()) {
          this.vertexStyle = common;
        }
      } else {
        this.vertexStyle = vs;
      }
    }

    IExpr es = getOption(S.EdgeStyle);
    if (es.isPresent()) {
      if (GraphExpr.isPropertyRuleList(es)) {
        this.edgeStyles = es;
        IExpr common = GraphExpr.propertyDefault(es);
        if (common.isPresent()) {
          this.edgeStyle = common;
        }
      } else {
        this.edgeStyle = es;
      }
    }

    IExpr vl = getOption(S.VertexLabels);
    if (vl.isPresent() && (vl.toString().equals("Name") || vl.toString().equals("\"Name\""))) {
      this.showVertexLabels = true;
    }

    IExpr vz = getOption(S.VertexSize);
    if (vz.isPresent()) {
      if (GraphExpr.isPropertyRuleList(vz)) {
        this.vertexSizes = vz;
        vz = GraphExpr.propertyDefault(vz);
      }
      double fraction = sizeFraction(vz);
      if (!Double.isNaN(fraction)) {
        this.vertexSizeFraction = fraction;
      }
    }

    IExpr shape = getOption(S.VertexShapeFunction);
    if (shape.isString()) {
      String name = shape.toString().replace("\"", "");
      if (name.equals("Diamond") || name.equals("Square") || name.equals("Triangle")
          || name.equals("Rectangle")) {
        this.vertexShape = name;
      } else if (name.equals("Circle") || name.equals("Disk")) {
        this.vertexShape = "Disk";
      }
    }

    IExpr ah = getOption(S.Arrowheads);
    if (ah.isPresent() && ah.isNumber()) {
      this.arrowHeadSize = ((INumber) ah).reDoubleValue();
    }

    IExpr gl = getOption(S.GraphLayout);
    if (gl.isPresent()) {
      this.graphLayout = gl.toString();
      this.gridColumns = gridColumns(gl);
    }
  }

  /**
   * The look of a named <code>GraphStyle</code> - the colours, a rectangle with the vertex name on
   * it, and its size - read off Mathematica's drawings of it. Unknown names change nothing.
   */
  private void applyGraphStyle(IExpr style) {
    if (!style.isString()) {
      return;
    }
    switch (style.toString().replace("\"", "")) {
      case "SmallNetwork":
        setTheme(ast(S.Directive, hue(0.625, 0.5, 0.7), ast(S.Thickness, S.Large)),
            hue(0.125, 0.7, 0.9), ast(S.EdgeForm), 0.13, 0.13);
        break;
      case "DiagramGreen":
        setTheme(ast(S.Directive, hue(0.25, 0.4, 0.5)), hue(0.25, 0.4, 0.8), ast(S.EdgeForm), 0.77,
            0.51);
        break;
      case "VintageDiagram":
        setTheme(ast(S.Directive, hue(0.0, 1.0, 0.5)), hue(0.15, 0.2, 1.0),
            ast(S.EdgeForm, ast(S.Directive, ast(S.Thickness, F.num(0.003)), hue(0.15, 1.0, 0.4))),
            0.19, 0.19);
        break;
      default:
        break;
    }
  }

  private void setTheme(IExpr edges, IExpr vertexColor, IExpr vertexOutline, double width,
      double height) {
    this.edgeStyle = edges;
    this.vertexStyle = ast(S.Directive, vertexColor, vertexOutline);
    this.vertexShape = "Theme";
    this.themeWidth = width;
    this.themeHeight = height;
    this.showVertexLabels = true;
    this.centeredLabels = true;
  }

  /**
   * <code>VertexSize -> s</code> as a fraction of the smallest vertex distance: a number, or
   * <code>Tiny</code>, <code>Small</code>, <code>Medium</code>, <code>Large</code>.
   *
   * @return the fraction or <code>NaN</code> if <code>size</code> gives none
   */
  private static double sizeFraction(IExpr size) {
    if (size.isNIL()) {
      return Double.NaN;
    }
    if (size == S.Tiny) {
      return 0.05;
    }
    if (size == S.Small) {
      return 0.1;
    }
    if (size == S.Medium) {
      return 0.2;
    }
    if (size == S.Large) {
      return 0.4;
    }
    double value = size.evalfNaN();
    return value > 0.0 ? value : Double.NaN;
  }

  /**
   * The number of columns of <code>"GridEmbedding"</code>, from
   * <code>"Dimension" -> {columns, rows}</code> anywhere in the layout specification.
   *
   * @return the columns or 0 if the layout gives none
   */
  private static int gridColumns(IExpr layout) {
    if (layout.isRuleAST() && layout.first().isString()
        && layout.first().toString().replace("\"", "").equals("Dimension")
        && layout.second().isList() && layout.second().argSize() == 2) {
      return layout.second().first().toIntDefault(0);
    }
    if (layout.isList() || layout.isRuleAST()) {
      for (IExpr arg : (IAST) layout) {
        int columns = gridColumns(arg);
        if (columns > 0) {
          return columns;
        }
      }
    }
    return 0;
  }

  /** Whether an option describes the graph, and so is not passed on to <code>Graphics</code>. */
  public static boolean isGraphOption(IExpr name) {
    return GRAPH_OPTIONS.contains(name);
  }

  /** <code>Hue(h, s, b)</code> */
  public static IAST hue(double h, double s, double b) {
    return ast(S.Hue, F.num(h), F.num(s), F.num(b));
  }

  private static IAST ast(IExpr head, IExpr... args) {
    IASTAppendable result = F.ast(head, args.length);
    for (IExpr arg : args) {
      result.append(arg);
    }
    return result;
  }

  /** Mathematica's vertices: light blue, with a thin dark outline. */
  private static IExpr defaultVertexStyle() {
    return ast(S.Directive, hue(0.6, 0.5, 1.0), ast(S.EdgeForm,
        ast(S.Directive, ast(S.GrayLevel, F.C0), ast(S.Opacity, F.num(0.7)))));
  }

  /** Mathematica's edges: a translucent darker blue with round ends. */
  private static IExpr defaultEdgeStyle() {
    return ast(S.Directive, ast(S.Opacity, F.num(0.7)), hue(0.6, 0.7, 0.7),
        ast(S.CapForm, F.stringx("Round")));
  }

  /** The smallest distance between two vertices, which Mathematica measures vertex sizes in. */
  private double smallestVertexDistance() {
    List<double[]> points = new ArrayList<>(vertexCoords.values());
    int n = points.size();
    if (n < 2 || n > 5000) {
      return 1.0;
    }
    double smallest = Double.MAX_VALUE;
    for (int i = 0; i < n; i++) {
      double[] p = points.get(i);
      for (int j = i + 1; j < n; j++) {
        double[] q = points.get(j);
        double d = Math.hypot(p[0] - q[0], p[1] - q[1]);
        if (d > 1.0e-12 && d < smallest) {
          smallest = d;
        }
      }
    }
    return smallest == Double.MAX_VALUE ? 1.0 : smallest;
  }

  private double vertexRadius(IExpr vertex, double distance) {
    double fraction = vertexSizeFraction;
    if (vertexSizes.isPresent()) {
      double own = sizeFraction(GraphExpr.vertexProperty(vertexSizes, vertex));
      if (!Double.isNaN(own)) {
        fraction = own;
      }
    }
    return Double.isNaN(fraction) ? vertexSize : fraction * distance / 2.0;
  }

  private static IAST point(double x, double y) {
    return F.List(F.num(x), F.num(y));
  }

  /** The primitive a vertex at <code>p</code> is drawn as, in the shape the options ask for. */
  private IExpr vertexPrimitive(double[] p, double r, double distance) {
    double x = p[0];
    double y = p[1];
    switch (vertexShape) {
      case "Diamond": {
        // Mathematica's diamond reaches a little further than the disk of the same size
        double h = 1.118 * r;
        return ast(S.Polygon,
            F.List(point(x, y - h), point(x + h, y), point(x, y + h), point(x - h, y)));
      }
      case "Square":
        return ast(S.Rectangle, point(x - r, y - r), point(x + r, y + r));
      case "Rectangle":
        return ast(S.Rectangle, point(x - 1.25 * r, y - 0.8 * r), point(x + 1.25 * r, y + 0.8 * r));
      case "Triangle": {
        double c = Math.sqrt(3.0) / 2.0 * r;
        return ast(S.Polygon, F.List(point(x, y + r), point(x - c, y - r / 2.0),
            point(x + c, y - r / 2.0)));
      }
      case "Theme": {
        double w = themeWidth * distance / 2.0;
        double h = themeHeight * distance / 2.0;
        return ast(S.Rectangle, point(x - w, y - h), point(x + w, y + h));
      }
      default:
        return F.Disk(point(x, y), F.num(r));
    }
  }

  /** The vertices row by row on a grid of unit spacing, in the order of the vertex list. */
  private <E> void layoutComponentGrid(Graph<IExpr, E> g, Set<IExpr> component, double centerX,
      double centerY) {
    int n = component.size();
    int columns = gridColumns > 0 ? gridColumns : (int) Math.ceil(Math.sqrt(n));
    int rows = (n + columns - 1) / columns;
    int i = 0;
    for (IExpr v : g.vertexSet()) {
      if (component.contains(v)) {
        int row = i / columns;
        int column = i % columns;
        vertexCoords.put(v, new double[] {centerX + column - (columns - 1) / 2.0,
            centerY + (rows - 1) / 2.0 - row});
        i++;
      }
    }
  }

  /**
   * Generates the Graphics[{primitives}, options] expression.
   */
  public IAST toGraphics() {
    parseOptions();
    calculateLayout(this.graph);

    // Dynamic adjustment for big graphs
    int vertexCount = graph.vertexSet().size();
    if (vertexCount > 500) {
      this.arrowHeadSize = 0.003;
    } else if (vertexCount > 100) {
      this.arrowHeadSize = 0.007;
    } else if (vertexCount > 60) {
      this.arrowHeadSize = 0.01;
    }
    // very large graphs get smaller vertices, unless a size was asked for
    if (!getOption(S.VertexSize).isPresent() && vertexCount > 200) {
      this.vertexSize = 0.025;
    }
    double distance = smallestVertexDistance();

    // the edges and the vertices each in a list of their own, as Mathematica draws them, so the
    // opacity of the edges does not reach the vertices
    IASTAppendable edgePrimitives = F.ListAlloc(graph.edgeSet().size() + 2);
    if (directed) {
      edgePrimitives.append(F.Arrowheads(arrowHeadSize));
    }
    if (!edgeStyle.isNone()) {
      edgePrimitives.append(edgeStyle);
    }
    drawEdges(this.graph, edgePrimitives);

    IASTAppendable vertexPrimitives = F.ListAlloc(2 * vertexCount + 1);
    if (!vertexStyle.isNone()) {
      vertexPrimitives.append(vertexStyle);
    }
    for (IExpr v : graph.vertexSet()) {
      double[] p = vertexCoords.get(v);
      if (p != null) {
        IExpr shape = vertexPrimitive(p, vertexRadius(v, distance), distance);
        IExpr style = GraphExpr.vertexProperty(vertexStyles, v);
        vertexPrimitives.append(style.isPresent() ? F.List(style, shape) : shape);
        if (showVertexLabels) {
          IAST pos = point(p[0], p[1]);
          IExpr text = centeredLabels ? ast(S.Text, v, pos) : F.Text(v, pos, F.List(F.C0, F.CN1));
          vertexPrimitives.append(F.List(F.Black, text));
        }
      }
    }

    IASTAppendable graphicsOptions = F.ListAlloc(options.size());
    for (IExpr option : options) {
      if (!option.isRuleAST() || !isGraphOption(option.first())) {
        graphicsOptions.append(option);
      }
    }
    return F.Graphics(F.List(edgePrimitives, vertexPrimitives), graphicsOptions);
  }

  /**
   * The coordinates of the vertices of a graph in the order of <code>VertexList</code>.
   *
   * <p>
   * The default embedding <code>&quot;CircularEmbedding&quot;</code> places the <code>n</code>
   * vertices counterclockwise on the unit circle, starting with the vertex at the angle
   * <code>Pi/2 + 2*Pi/n</code>. Every other embedding is delegated to the layout algorithms used
   * for <code>GraphPlot</code>.
   * </p>
   *
   * @param graph
   * @param embedding the name of the embedding, for example
   *        <code>&quot;CircularEmbedding&quot;</code> or <code>&quot;SpringEmbedding&quot;</code>
   */
  public static IAST vertexCoordinates(Graph<IExpr, ?> graph, String embedding) {
    Set<IExpr> vertexSet = graph.vertexSet();
    int n = vertexSet.size();
    IASTAppendable result = F.ListAlloc(n);
    if (n == 0) {
      return result;
    }
    if (embedding.isEmpty() || embedding.contains("CircularEmbedding")) {
      if (n == 1) {
        return F.list(F.list(F.CD0, F.CD0));
      }
      double angleStep = 2.0 * Math.PI / n;
      int i = 1;
      for (IExpr vertex : vertexSet) {
        double angle = Math.PI / 2.0 + i++ * angleStep;
        result.append(F.list(F.num(chopZero(Math.cos(angle))), F.num(chopZero(Math.sin(angle)))));
      }
      return result;
    }

    IASTAppendable options = F.ListAlloc(1);
    options.append(F.Rule(S.GraphLayout, F.stringx(embedding)));
    GraphGraphics graphGraphics = new GraphGraphics(GraphExpr.newInstance(graph, options));
    graphGraphics.parseOptions();
    graphGraphics.calculateLayout(graph);
    for (IExpr vertex : vertexSet) {
      double[] coordinates = graphGraphics.vertexCoords.get(vertex);
      if (coordinates == null) {
        result.append(F.list(F.CD0, F.CD0));
      } else {
        result.append(F.list(F.num(coordinates[0]), F.num(coordinates[1])));
      }
    }
    return result;
  }

  /** Map a coordinate which is zero except for a rounding error to an exact <code>0.0</code>. */
  private static double chopZero(double value) {
    return Math.abs(value) < 1.0e-12 ? 0.0 : value;
  }

  public static IASTAppendable createOptionsList(final IExpr[] options) {
    IASTAppendable optionsList = F.ListAlloc(4);
    if (options[X_DIRECTED_EDGES].isTrue()) {
      optionsList.append(F.Rule(S.DirectedEdges, S.True));
    }
    optionsList.append(F.Rule(S.GraphLayout, options[X_GRAPH_LAYOUT]));
    if (options.length > X_VERTEX_SIZE && options[X_VERTEX_SIZE] != S.Automatic) {
      optionsList.append(F.Rule(S.VertexSize, options[X_VERTEX_SIZE]));
    }
    if (options.length > X_VERTEX_LABELS && options[X_VERTEX_LABELS] != S.None) {
      optionsList.append(F.Rule(S.VertexLabels, options[X_VERTEX_LABELS]));
    }
    return optionsList;
  }
}
