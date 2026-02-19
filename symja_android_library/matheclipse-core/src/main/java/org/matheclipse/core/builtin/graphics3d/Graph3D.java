package org.matheclipse.core.builtin.graphics3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.graphics.GraphGraphics;
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

    // Default Vertex Size logic
    double vertexRadius = 0.03; // Default 'Medium'ish
    if (vertexSizeOpt.isNumber()) {
      vertexRadius = ((INumber) vertexSizeOpt).reDoubleValue();
    } else if (vertexSizeOpt.equals(S.Small)) {
      vertexRadius = 0.01;
    } else if (vertexSizeOpt.equals(S.Large)) {
      vertexRadius = 0.05;
    }

    // --- Layout Calculation ---
    boolean hasCoords = false;
    if (vertexCoordinatesOpt.isList()) {
      // Try to parse explicit coordinates
      IAST coordsList = (IAST) vertexCoordinatesOpt;
      if (coordsList.size() > 0 && coordsList.arg1().isList()) {
        // List of coordinates { {x,y,z}, ... } matching vertex order
        // This is simplistic; robust impl would match length or rules
        int i = 1;
        for (IExpr v : vertices) {
          if (i >= coordsList.size())
            break;
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
      computeSpringLayout3D(graph, coordinates);
    }

    // --- Primitive Generation ---
    IASTAppendable primitives = F.ListAlloc(vertices.size() + graph.edgeSet().size());

    // 1. Draw Edges
    // Default edge style
    if (!edgeStyle.equals(S.Automatic) && !edgeStyle.equals(S.None)) {
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
        primitives.append(F.Line(F.List(F.List(F.num(p1.x), F.num(p1.y), F.num(p1.z)),
            F.List(F.num(p2.x), F.num(p2.y), F.num(p2.z)))));
      }
    }

    // 2. Draw Vertices
    // Reset style for vertices if needed, or apply VertexStyle
    if (!vertexStyle.equals(S.Automatic) && !vertexStyle.equals(S.None)) {
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

    // --- Final Assembly ---
    // Merge generated options with defaults (like Boxed -> False)
    IASTAppendable finalOptions = F.ListAlloc();
    finalOptions.append(F.Rule(S.Boxed, boxed ? S.True : S.False));

    // Append user options, overriding defaults if present
    for (int i = 1; i < options.size(); i++) {
      finalOptions.append(options.get(i));
    }

    return F.Graphics3D(primitives, finalOptions);
  }

  /**
   * Calculates a 3D layout using a simple Fruchterman-Reingold force-directed algorithm.
   */
  private void computeSpringLayout3D(Graph<IExpr, ? extends IExprEdge> graph,
      Map<IExpr, Vector3D> coordinates) {
    Set<IExpr> vertices = graph.vertexSet();
    int vertexCount = vertices.size();
    if (vertexCount == 0)
      return;

    // Initialize random positions if not present
    Random rand = new Random(12345); // Fixed seed for reproducibility
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
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
        GraphGraphics.defaultGraphOptionValues());
  }
}
