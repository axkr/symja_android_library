package org.matheclipse.graphtheory.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.builtin.graphics3d.Plot3DTools;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;
import org.matheclipse.graphtheory.graphics.GraphGraphics;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Graph3D extends AbstractFunctionOptionEvaluator {

  private static class Vector3D {
    double x, y, z;

    Vector3D(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    void add(Vector3D v) {
      x += v.x;
      y += v.y;
      z += v.z;
    }

    double distance(Vector3D v) {
      double dx = x - v.x;
      double dy = y - v.y;
      double dz = z - v.z;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Normalize vector length to 1
    void normalize() {
      double d = Math.sqrt(x * x + y * y + z * z);
      if (d > 1e-9) {
        x /= d;
        y /= d;
        z /= d;
      }
    }
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IASTAppendable optionsList = F.ListAlloc();

    if (ast.isAST0()) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    GraphExpr<?> graphExpr = GraphExpr.newInstance(arg1);
    if (graphExpr == null) {
      return F.NIL;
    }

    // if (arg1 instanceof GraphExpr) {
    // graphExpr = (GraphExpr<?>) arg1;
    // // Extract existing options from GraphExpr
    // if (graphExpr.options().size() > 1) {
    // optionsList.appendAll(graphExpr.options(), 1, graphExpr.options().size());
    // }
    // } else if (arg1.isList()) {
    // // Handle Graph3D[{e1, e2...}] or Graph3D[{v...}, {e...}]
    // if (ast.argSize() >= 2 && ast.arg2().isList()) {
    // // Graph3D[{v...}, {e...}]
    // graphExpr = GraphExpr.newInstance(ast.arg1(), ast.arg2());
    // } else {
    // // Graph3D[{e...}]
    // graphExpr = GraphExpr.newInstance(F.NIL, (IAST) arg1);
    // }
    // } else if (arg1.head().equals(S.Graph)) {
    // // Handle Graph3D[Graph[...]] input
    // graphExpr = GraphExpr.newInstance(arg1);
    // }

    // 2. Collect Options passed directly to Graph3D (overriding Graph options)
    int startOptionIndex =
        (ast.argSize() >= 2 && ast.arg2().isList() && !ast.arg2().isRuleAST()) ? 3 : 2;
    for (int i = startOptionIndex; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST()) {
        optionsList.append(arg);
      }
    }

    // 3. Generate the Graphics3D AST
    return createGraphics3D(graphExpr, optionsList);
  }

  private IExpr createGraphics3D(GraphExpr<?> graphExpr, IASTAppendable options) {
    Graph<IExpr, ? extends IExprEdge> graph =
        (Graph<IExpr, ? extends IExprEdge>) graphExpr.toData();
    Set<IExpr> vertices = graph.vertexSet();
    Map<IExpr, Vector3D> coordinates = new HashMap<>();

    // --- Option Parsing ---
    IExpr vertexStyle = getOption(options, S.VertexStyle, S.Automatic);
    IExpr edgeStyle = getOption(options, S.EdgeStyle, S.Automatic);
    IExpr vertexSizeOpt = getOption(options, S.VertexSize, S.Medium);
    IExpr vertexCoordinatesOpt = getOption(options, S.VertexCoordinates, S.Automatic);
    boolean boxed = getOption(options, S.Boxed, S.False).isTrue();
    IExpr vertexLabels = getOption(options, S.VertexLabels, S.None);
    IExpr edgeLabels = getOption(options, S.EdgeLabels, S.None);
    IExpr vertexLabelStyle = getOption(options, S.VertexLabelStyle, S.Automatic);
    IExpr edgeLabelStyle = getOption(options, S.EdgeLabelStyle, S.Automatic);
    IExpr graphLayout = getOption(options, S.GraphLayout, S.Automatic);
    IExpr directedEdgesOption = getOption(options, S.DirectedEdges, S.Automatic);

    // an arrowhead is what tells a directed edge apart from an undirected one, so it follows the
    // graph unless the call says otherwise
    boolean directed = directedEdgesOption.isTrue()
        || (!directedEdgesOption.isFalse() && graph.getType().isDirected());

    // Default Vertex Size logic
    double vertexRadius = 0.03; // Default 'Medium'ish
    if (vertexSizeOpt.isNumber()) {
      vertexRadius = ((INumber) vertexSizeOpt).reDoubleValue();
    } else if (vertexSizeOpt == S.Small) {
      vertexRadius = 0.01;
    } else if (vertexSizeOpt == S.Large) {
      vertexRadius = 0.05;
    }

    // --- Layout Calculation ---
    boolean hasCoords = false;
    if (vertexCoordinatesOpt.isList()) {
      // Try to parse explicit coordinates
      IAST coordsList = (IAST) vertexCoordinatesOpt;
      if (coordsList.argSize() > 0 && coordsList.arg1().isList()) {
        // List of coordinates { {x,y,z}, ... } matching vertex order
        // This is simplistic; robust impl would match length or rules
        int i = 1;
        for (IExpr v : vertices) {
          if (i >= coordsList.size()) {
            break;
          }
          IExpr pt = coordsList.get(i++);
          if (pt.isList() && ((IAST) pt).size() >= 4) {
            coordinates.put(v,
                new Vector3D(((INumber) ((IAST) pt).get(1)).reDoubleValue(),
                    ((INumber) ((IAST) pt).get(2)).reDoubleValue(),
                    ((INumber) ((IAST) pt).get(3)).reDoubleValue()));
          }
        }
        hasCoords = true;
      }
    }

    if (!hasCoords || coordinates.size() < vertices.size()) {
      if (isCircularLayout(graphLayout)) {
        computeCircularLayout3D(vertices, coordinates);
      } else if (isSpiralLayout(graphLayout)) {
        computeSpiralLayout3D(vertices, coordinates);
      } else {
        computeSpringLayout3D(graph, coordinates);
      }
    }

    // --- Primitive Generation ---
    IASTAppendable primitives = F.ListAlloc(vertices.size() + graph.edgeSet().size());

    // 1. Draw Edges
    // Default edge style
    if (edgeStyle != S.Automatic && edgeStyle != S.None) {
      primitives.append(edgeStyle);
    } else {
      primitives.append(F.GrayLevel(0.4)); // Default gray edges
    }

    // Use Lines for edges (Tube/Cylinder can be expensive for large graphs, Line is standard)
    // To make them visible in WebGL, simple Lines are best, or Tubes if styling demanded.
    // For this implementation, we use Line
    for (IExprEdge edge : graph.edgeSet()) {
      IExpr source = edge.lhs();
      IExpr target = edge.rhs();
      Vector3D p1 = coordinates.get(source);
      Vector3D p2 = coordinates.get(target);

      if (p1 != null && p2 != null) {
        IExpr line = F.Line(F.List(F.List(F.num(p1.x), F.num(p1.y), F.num(p1.z)),
            F.List(F.num(p2.x), F.num(p2.y), F.num(p2.z))));
        // the arrowhead has to stop short of the target sphere, or it disappears inside it
        primitives.append(directed ? F.binaryAST2(S.Arrow, line, F.num(vertexRadius)) : line);
      }
    }

    appendEdgeLabels(primitives, graph, coordinates, edgeLabels, edgeLabelStyle);

    // 2. Draw Vertices
    // Reset style for vertices if needed, or apply VertexStyle
    if (vertexStyle != S.Automatic && vertexStyle != S.None) {
      primitives.append(vertexStyle);
    } else {
      primitives.append(F.RGBColor(1.0, 0.5, 0.0)); // Default orange vertices
    }

    for (IExpr v : vertices) {
      Vector3D p = coordinates.get(v);
      if (p != null) {
        // Sphere is the standard 3D representation for graph vertices
        primitives
            .append(F.Sphere(F.List(F.num(p.x), F.num(p.y), F.num(p.z)), F.num(vertexRadius)));
      }
    }

    appendVertexLabels(primitives, vertices, coordinates, vertexLabels, vertexLabelStyle,
        vertexRadius);

    // --- Final Assembly ---
    IASTAppendable result = F.ast(S.Graphics3D, options.size() + 3);
    result.append(primitives);
    // the user's own options come first, so one of theirs beats the default below it
    for (int i = 1; i < options.size(); i++) {
      if (options.get(i).isRuleAST()) {
        result.append(options.get(i));
      }
    }
    result.append(F.Rule(S.Boxed, boxed ? S.True : S.False));
    result.append(F.Rule(S.Axes, S.False));
    return result;
  }

  /**
   * Calculates a 3D layout using a simple Fruchterman-Reingold force-directed algorithm.
   */
  private static boolean isNamedLayout(IExpr graphLayout, String name) {
    if (graphLayout.isString()) {
      return graphLayout.toString().equalsIgnoreCase(name);
    }
    // GraphLayout -> {"name", options...} is the fuller form
    if (graphLayout.isList() && ((IAST) graphLayout).argSize() >= 1) {
      IExpr first = ((IAST) graphLayout).arg1();
      return first.isString() && first.toString().equalsIgnoreCase(name);
    }
    return false;
  }

  private static boolean isCircularLayout(IExpr graphLayout) {
    return isNamedLayout(graphLayout, "CircularEmbedding")
        || isNamedLayout(graphLayout, "CircularMultipartiteEmbedding");
  }

  private static boolean isSpiralLayout(IExpr graphLayout) {
    return isNamedLayout(graphLayout, "SpiralEmbedding");
  }

  /** Vertices spread evenly around a circle, which is what {@code CircularEmbedding} asks for. */
  private static void computeCircularLayout3D(Set<IExpr> vertices,
      Map<IExpr, Vector3D> coordinates) {
    int count = vertices.size();
    int i = 0;
    for (IExpr v : vertices) {
      double angle = count > 0 ? 2.0 * Math.PI * i / count : 0.0;
      coordinates.put(v, new Vector3D(Math.cos(angle), Math.sin(angle), 0.0));
      i++;
    }
  }

  /**
   * Vertices along a helix.
   *
   * <p>
   * The circle above puts every vertex in one plane, which throws away the dimension this plot
   * exists for; a helix keeps the even spacing and still uses the height.
   */
  private static void computeSpiralLayout3D(Set<IExpr> vertices, Map<IExpr, Vector3D> coordinates) {
    int count = vertices.size();
    int i = 0;
    for (IExpr v : vertices) {
      double t = count > 1 ? (double) i / (count - 1) : 0.0;
      double angle = 4.0 * Math.PI * t;
      coordinates.put(v, new Vector3D(Math.cos(angle), Math.sin(angle), 2.0 * t - 1.0));
      i++;
    }
  }

  /**
   * Writes a label beside each vertex.
   *
   * <p>
   * {@code Automatic} and {@code "Name"} both mean the vertex itself, which is how a graph of
   * numbered vertices is usually read. A list of rules names the vertices that get a label, and
   * anything else is taken as one label for all of them.
   */
  private static void appendVertexLabels(IASTAppendable primitives, Set<IExpr> vertices,
      Map<IExpr, Vector3D> coordinates, IExpr vertexLabels, IExpr labelStyle, double vertexRadius) {
    if (vertexLabels.isNone()) {
      return;
    }
    if (labelStyle != S.Automatic && !labelStyle.isNone()) {
      primitives.append(labelStyle);
    }
    for (IExpr v : vertices) {
      Vector3D p = coordinates.get(v);
      if (p == null) {
        continue;
      }
      IExpr label = labelFor(vertexLabels, v, v);
      if (label.isPresent()) {
        // just clear of the sphere, so the text is not buried in it
        primitives.append(F.Text(label,
            F.List(F.num(p.x), F.num(p.y), F.num(p.z + vertexRadius * 2.0))));
      }
    }
  }

  /** Writes a label at the midpoint of each edge. */
  private static void appendEdgeLabels(IASTAppendable primitives,
      Graph<IExpr, ? extends IExprEdge> graph, Map<IExpr, Vector3D> coordinates, IExpr edgeLabels,
      IExpr labelStyle) {
    if (edgeLabels.isNone()) {
      return;
    }
    if (labelStyle != S.Automatic && !labelStyle.isNone()) {
      primitives.append(labelStyle);
    }
    for (IExprEdge edge : graph.edgeSet()) {
      Vector3D p1 = coordinates.get(edge.lhs());
      Vector3D p2 = coordinates.get(edge.rhs());
      if (p1 == null || p2 == null) {
        continue;
      }
      IExpr key = graph.getType().isDirected() ? F.DirectedEdge(edge.lhs(), edge.rhs())
          : F.UndirectedEdge(edge.lhs(), edge.rhs());
      IExpr label = labelFor(edgeLabels, key, key);
      if (label.isPresent()) {
        primitives.append(F.Text(label, F.List(F.num((p1.x + p2.x) / 2.0),
            F.num((p1.y + p2.y) / 2.0), F.num((p1.z + p2.z) / 2.0))));
      }
    }
  }

  /**
   * The label a {@code VertexLabels} or {@code EdgeLabels} value gives this item, or {@link F#NIL}
   * when it gives none.
   */
  private static IExpr labelFor(IExpr labels, IExpr key, IExpr name) {
    if (labels == S.Automatic
        || (labels.isString() && labels.toString().equalsIgnoreCase("Name"))) {
      return name;
    }
    if (labels.isRuleAST()) {
      return ((IAST) labels).arg1().equals(key) ? ((IAST) labels).arg2() : F.NIL;
    }
    if (labels.isList()) {
      IAST list = (IAST) labels;
      for (int i = 1; i < list.size(); i++) {
        IExpr entry = list.get(i);
        if (entry.isRuleAST() && ((IAST) entry).arg1().equals(key)) {
          return ((IAST) entry).arg2();
        }
      }
      return F.NIL;
    }
    return labels;
  }

  private void computeSpringLayout3D(Graph<IExpr, ? extends IExprEdge> graph,
      Map<IExpr, Vector3D> coordinates) {
    Set<IExpr> vertices = graph.vertexSet();
    int vertexCount = vertices.size();
    if (vertexCount == 0)
      return;

    // Initialize random positions if not present
    Random rand = new Random(12345);
    for (IExpr v : vertices) {
      if (!coordinates.containsKey(v)) {
        coordinates.put(v, new Vector3D(rand.nextDouble() * 2.0 - 1.0,
            rand.nextDouble() * 2.0 - 1.0, rand.nextDouble() * 2.0 - 1.0));
      }
    }

    // Parameters
    double area = vertexCount * vertexCount * vertexCount; // Volume heuristic
    double k = Math.pow(area / vertexCount, 1.0 / 3.0); // Optimal distance
    double temperature = 1.0;
    int iterations = 50;

    // Force vectors storage
    Map<IExpr, Vector3D> displacement = new HashMap<>();

    for (int i = 0; i < iterations; i++) {
      // 1. Calculate Repulsive Forces
      for (IExpr v : vertices) {
        displacement.put(v, new Vector3D(0, 0, 0));
        for (IExpr u : vertices) {
          if (v.equals(u))
            continue;

          Vector3D posV = coordinates.get(v);
          Vector3D posU = coordinates.get(u);

          double dx = posV.x - posU.x;
          double dy = posV.y - posU.y;
          double dz = posV.z - posU.z;
          double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
          if (dist < 0.0001)
            dist = 0.0001;

          double force = (k * k) / dist;
          displacement.get(v)
              .add(new Vector3D((dx / dist) * force, (dy / dist) * force, (dz / dist) * force));
        }
      }

      // 2. Calculate Attractive Forces (Edges)
      for (IExprEdge edge : graph.edgeSet()) {
        IExpr v = edge.lhs();
        IExpr u = edge.rhs();
        if (v.equals(u))
          continue; // Ignore self-loops for forces

        Vector3D posV = coordinates.get(v);
        Vector3D posU = coordinates.get(u);

        double dx = posV.x - posU.x;
        double dy = posV.y - posU.y;
        double dz = posV.z - posU.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.0001)
          dist = 0.0001;

        double force = (dist * dist) / k;

        Vector3D attract =
            new Vector3D((dx / dist) * force, (dy / dist) * force, (dz / dist) * force);

        Vector3D dispV = displacement.get(v);
        Vector3D dispU = displacement.get(u);

        dispV.x -= attract.x;
        dispV.y -= attract.y;
        dispV.z -= attract.z;

        dispU.x += attract.x;
        dispU.y += attract.y;
        dispU.z += attract.z;
      }

      // 3. Update positions
      for (IExpr v : vertices) {
        Vector3D disp = displacement.get(v);
        double dist = Math.sqrt(disp.x * disp.x + disp.y * disp.y + disp.z * disp.z);
        if (dist > 0) {
          double limitedDist = Math.min(dist, temperature);
          Vector3D pos = coordinates.get(v);
          pos.x += (disp.x / dist) * limitedDist;
          pos.y += (disp.y / dist) * limitedDist;
          pos.z += (disp.z / dist) * limitedDist;
        }
      }

      // Cool down
      temperature *= 0.95;
    }
  }

  private IExpr getOption(IAST options, ISymbol key, IExpr defaultValue) {
    for (IExpr opt : options) {
      if (opt.isRuleAST() && opt.first().equals(key)) {
        return ((IAST) opt).second();
      }
    }
    return defaultValue;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // options occupy argument slots until they are stripped, so a fixed arity of one rejected
    // every call that carried an option
    return ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // the names read in createGraphics3D have to be the names declared here, or the engine never
    // strips them and the reads never see anything
    GraphicsOptions.OptionSet options = Plot3DTools.frameExtras(Plot3DTools.base3D()
        .add(GraphGraphics.defaultGraphOptionKeys(), GraphGraphics.defaultGraphOptionValues())
        .add(S.Automatic, S.VertexStyle, S.EdgeStyle, S.VertexCoordinates, S.VertexSize,
            S.VertexLabels, S.EdgeLabels, S.VertexLabelStyle, S.EdgeLabelStyle, S.VertexShape,
            S.VertexShapeFunction, S.EdgeShapeFunction, S.VertexWeight, S.EdgeWeight,
            S.GraphHighlightStyle, S.PlotTheme, S.AnnotationRules)
        .add(F.CEmptyList, S.GraphHighlight));
    setOptions(newSymbol, options.keys(), options.values());
  }
}
