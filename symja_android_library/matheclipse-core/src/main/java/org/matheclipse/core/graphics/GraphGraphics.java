package org.matheclipse.core.graphics;

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
import org.matheclipse.core.expression.data.GraphExpr;
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

  public static IBuiltInSymbol[] defaultGraphOptionKeys() {
    return new IBuiltInSymbol[] {S.DirectedEdges, S.GraphLayout};
  }

  public static IExpr[] defaultGraphOptionValues() {
    return new IExpr[] {S.False, F.stringx("LayeredEmbedding")};
  }

  private final Graph<IExpr, ?> graph;
  private final IAST options;
  // Configuration
  private Map<IExpr, double[]> vertexCoords = new HashMap<>();
  private IExpr vertexStyle = S.Blue;
  private IExpr edgeStyle = F.Gray;
  private boolean showVertexLabels = false;
  private double vertexSize = 0.05;

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
        if (source.equals(target)) {
          drawSelfLoop(p1, primitives);
        } else if (directed && g.containsEdge(target, source)) {
          drawCurvedEdge(p1, p2, primitives);
        } else {
          IAST coordList =
              F.List(F.List(F.num(p1[0]), F.num(p1[1])), F.List(F.num(p2[0]), F.num(p2[1])));
          if (directed) {
            primitives.append(F.Arrow(coordList));
          } else {
            primitives.append(F.Line(coordList));
          }
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
    IExpr vs = getOption(S.VertexStyle);
    if (vs.isPresent())
      this.vertexStyle = vs;

    IExpr es = getOption(S.EdgeStyle);
    if (es.isPresent())
      this.edgeStyle = es;

    IExpr vl = getOption(S.VertexLabels);
    if (vl.isPresent() && (vl.toString().equals("Name") || vl.toString().equals("\"Name\""))) {
      this.showVertexLabels = true;
    }

    IExpr vz = getOption(S.VertexSize);
    if (vz.isPresent() && vz.isNumber()) {
      this.vertexSize = ((INumber) vz).reDoubleValue();
    }

    IExpr ah = getOption(S.Arrowheads);
    if (ah.isPresent() && ah.isNumber()) {
      this.arrowHeadSize = ((INumber) ah).reDoubleValue();
    }

    IExpr gl = getOption(S.GraphLayout);
    if (gl.isPresent()) {
      this.graphLayout = gl.toString();
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
    // If not set by options, ensure vertices are small for very large graphs
    if (getOption(S.VertexSize).isPresent() && vertexCount > 200) {
      this.vertexSize = 0.025;
    }

    IASTAppendable primitives = F.ListAlloc(graph.vertexSet().size() + graph.edgeSet().size() + 5);

    // 0. Global Directives
    if (directed) {
      primitives.append(F.Arrowheads(arrowHeadSize));
    }

    // 1. Draw Edges
    if (!edgeStyle.isNone()) {
      primitives.append(edgeStyle);
    }
    drawEdges(this.graph, primitives);

    // 2. Draw Vertices
    if (!vertexStyle.isNone()) {
      primitives.append(vertexStyle);
    }

    for (IExpr v : graph.vertexSet()) {
      double[] p = vertexCoords.get(v);
      if (p != null) {
        final IAST pos = F.List(F.num(p[0]), F.num(p[1]));
        primitives.append(F.Disk(pos, F.num(vertexSize)));

        if (showVertexLabels) {
          primitives.append(F.Black);
          primitives.append(F.Text(v, pos, F.List(F.C0, F.CN1)));
          if (!vertexStyle.isNone())
            primitives.append(vertexStyle);
        }
      }
    }

    IASTAppendable graphics = F.Graphics(primitives, options);
    // System.out.println(graphics);
    return graphics;
  }

  public static IASTAppendable createOptionsList(final IExpr[] options) {
    IASTAppendable optionsList = F.ListAlloc(2);
    if (options[X_DIRECTED_EDGES].isTrue()) {
      optionsList.append(F.Rule(S.DirectedEdges, S.True));
    }
    optionsList.append(F.Rule(S.GraphLayout, options[X_GRAPH_LAYOUT]));
    return optionsList;
  }
}
