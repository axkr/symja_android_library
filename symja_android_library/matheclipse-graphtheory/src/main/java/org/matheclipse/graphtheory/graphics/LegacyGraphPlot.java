package org.matheclipse.graphtheory.graphics;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * The picture <code>GraphPlot</code> draws with its pre-version-6 implementation, which a
 * <code>Method -&gt; {"embedding", suboption -&gt; value, ...}</code> with one of the suboptions
 * <code>"Rotation"</code> or <code>MaxIterations</code> asks for:
 *
 * <pre>
 * Graphics(Annotation(GraphicsComplex(coords, {{RGBColor(0.5, 0., 0.), Line({{i, j}, ...})},
 *     vertices}, {}), VertexCoordinateRules -&gt; coords), FrameTicks -&gt; None, PlotRange -&gt; All,
 *     PlotRangePadding -&gt; Scaled(0.1), AspectRatio -&gt; Automatic, ...)
 * </pre>
 *
 * <p>
 * The vertices are <code>{RGBColor(0, 0, 0.7), Tooltip(Point(i), v), ...}</code>, or with
 * <code>VertexLabeling -&gt; True</code> framed names <code>Text(Framed(v, {...}), i)</code>. The
 * coordinates are moved so that the smallest x and the smallest y are <code>0.</code>.
 */
public final class LegacyGraphPlot {

  private LegacyGraphPlot() {}

  /**
   * Whether <code>GraphPlot(graph, options...)</code> asks for the pre-version-6 picture: its
   * <code>Method</code> is a list of an embedding name followed by suboptions, one of which is only
   * known to that implementation. A plain
   * <code>Method -&gt; "SpringEmbedding"</code> or <code>Method -&gt; {"SpringEmbedding"}</code>
   * does not.
   *
   * @param from the position of the first option in <code>ast</code>
   */
  public static boolean isRequested(IAST ast, int from) {
    for (int i = from; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first() == S.Method) {
        IExpr method = option.second();
        if (method.isList() && method.argSize() >= 2 && method.first().isString()) {
          for (int j = 2; j < method.size(); j++) {
            if (method.getAt(j).isRuleAST() && isLegacySuboption(method.getAt(j).first())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * Whether a suboption of <code>Method</code> belongs to the pre-version-6 implementation only:
   * <code>"Rotation"</code> or <code>MaxIterations</code>. A suboption the current layouts know too,
   * such as <code>"RepulsiveForcePower"</code>, keeps the normal picture.
   */
  private static boolean isLegacySuboption(IExpr name) {
    return name.isString("Rotation") || name == S.MaxIterations;
  }

  /**
   * The pre-version-6 picture of a graph.
   *
   * @param graph the graph
   * @param ast <code>GraphPlot(graph, options...)</code>
   * @param from the position of the first option in <code>ast</code>
   */
  public static IAST plot(GraphExpr<?> graph, IAST ast, int from) {
    Graph<IExpr, ?> g = graph.toData();
    String embedding = "SpringEmbedding";
    boolean labeling = false;
    IASTAppendable options = F.ListAlloc(F.Rule(S.FrameTicks, S.None),
        F.Rule(S.PlotRange, S.All), F.Rule(S.PlotRangePadding, F.Scaled(0.1)),
        F.Rule(S.AspectRatio, S.Automatic));
    for (int i = from; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      if (option.first() == S.Method) {
        if (option.second().isList() && option.second().argSize() >= 1
            && option.second().first().isString()) {
          embedding = option.second().first().toString();
        }
        continue;
      }
      if (GraphGraphics.isVertexLabeling(option.first())) {
        labeling = option.second().isTrue();
        continue;
      }
      if (GraphGraphics.isPlotGraphOption(option)) {
        continue;
      }
      // a Graphics option takes the place of the default of the same name, or is appended
      int position = options.indexOf(x -> x.first().equals(option.first()));
      if (position > 0) {
        options.set(position, option);
      } else {
        options.append(option);
      }
    }

    IAST coordinates = moveToOrigin(GraphGraphics.vertexCoordinates(g, embedding));

    Map<IExpr, Integer> index = new HashMap<>();
    IASTAppendable vertices;
    if (labeling) {
      vertices = F.ListAlloc(g.vertexSet().size());
    } else {
      vertices = F.ListAlloc(g.vertexSet().size() + 1);
      vertices.append(F.RGBColor(F.C0, F.C0, F.num(0.7)));
    }
    int i = 1;
    for (IExpr vertex : g.vertexSet()) {
      index.put(vertex, i);
      vertices.append(labeling ? F.binaryAST2(S.Text, framed(vertex), F.ZZ(i))
          : F.Tooltip(F.Point(F.ZZ(i)), vertex));
      i++;
    }
    IASTAppendable lines = F.ListAlloc(g.edgeSet().size());
    for (Object edge : g.edgeSet()) {
      @SuppressWarnings("unchecked")
      Graph<IExpr, Object> graphOfEdge = (Graph<IExpr, Object>) g;
      lines.append(F.list(F.ZZ(index.get(graphOfEdge.getEdgeSource(edge))),
          F.ZZ(index.get(graphOfEdge.getEdgeTarget(edge)))));
    }
    IAST edges = F.list(F.RGBColor(F.num(0.5), F.CD0, F.CD0), F.Line(lines));
    IAST complex = F.ternaryAST3(S.GraphicsComplex, coordinates, F.list(edges, vertices),
        F.CEmptyList);
    IASTAppendable result = F.ast(S.Graphics, options.argSize() + 1);
    result.append(
        F.binaryAST2(S.Annotation, complex, F.Rule(S.VertexCoordinateRules, coordinates)));
    result.appendArgs(options);
    return result;
  }

  /** A vertex name in a light yellow frame. */
  private static IAST framed(IExpr vertex) {
    return F.binaryAST2(S.Framed, vertex,
        F.list(F.Rule(S.Background, F.RGBColor(F.C1, F.C1, F.num(0.8))),
            F.Rule(S.FrameStyle, F.RGBColor(F.num(0.94), F.num(0.85), F.num(0.36))),
            F.Rule(S.FrameMargins, S.Automatic)));
  }

  /** The points <code>{x, y}</code> moved so that the smallest x and the smallest y are 0. */
  private static IAST moveToOrigin(IAST points) {
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    for (IExpr point : points) {
      minX = Math.min(minX, point.first().evalf());
      minY = Math.min(minY, point.second().evalf());
    }
    IASTAppendable moved = F.ListAlloc(points.argSize());
    for (IExpr point : points) {
      moved.append(
          F.list(F.num(point.first().evalf() - minX), F.num(point.second().evalf() - minY)));
    }
    return moved;
  }
}
