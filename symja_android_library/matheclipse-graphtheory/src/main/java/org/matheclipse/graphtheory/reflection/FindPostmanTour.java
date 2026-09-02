package org.matheclipse.graphtheory.reflection;

import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.ChinesePostman;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.WeightedPseudograph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindPostmanTour(graph)</code> - a Chinese postman tour of <code>graph</code>: a shortest
 * closed walk traversing every edge at least once.
 *
 * <p>
 * The result is a list of tours, each a list of the edges walked in order. A tour exists only when
 * all the edges lie in one component and, for a directed graph, that component is strongly
 * connected; otherwise the result is <code>{}</code>. Vertices carrying no edge are ignored, since a
 * tour has to cover edges rather than vertices.
 *
 * <p>
 * A graph whose vertices all have even degree is Eulerian and its tour walks each edge exactly once.
 * Otherwise some edges have to be repeated, and the tour is shortest when the repetition is as small
 * as possible - which is what the Edmonds-Johnson matching behind this computes. Repeating an edge
 * means the walk needs a graph that can hold it twice, so the edges are copied into a pseudograph
 * first; a Symja graph is simple and would silently drop the duplicate.
 */
public class FindPostmanTour extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int limit = 1;
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2.isInfinity() || arg2 == org.matheclipse.core.expression.S.All) {
        limit = Integer.MAX_VALUE;
      } else {
        limit = arg2.toIntDefault();
        if (limit < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    @SuppressWarnings("unchecked")
    Graph<IExpr, Object> g = (Graph<IExpr, Object>) gex.toData();
    if (g.edgeSet().isEmpty()) {
      return F.CEmptyList; // nothing to walk
    }

    // a vertex with no edge is nothing for a tour to cover, so leave it out rather than let it
    // count as a second component. The copy is a pseudograph because the tour may have to walk an
    // edge twice, and only a multigraph can carry the repeat.
    boolean directed = g.getType().isDirected();
    Graph<IExpr, DefaultWeightedEdge> walkable = directed //
        ? new DirectedWeightedPseudograph<IExpr, DefaultWeightedEdge>(DefaultWeightedEdge.class) //
        : new WeightedPseudograph<IExpr, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    for (Object edge : g.edgeSet()) {
      IExpr source = g.getEdgeSource(edge);
      IExpr target = g.getEdgeTarget(edge);
      walkable.addVertex(source);
      walkable.addVertex(target);
      DefaultWeightedEdge copy = walkable.addEdge(source, target);
      if (copy != null) {
        walkable.setEdgeWeight(copy, g.getEdgeWeight(edge));
      }
    }
    if (!isTraversable(walkable)) {
      return F.CEmptyList;
    }

    GraphPath<IExpr, DefaultWeightedEdge> tour;
    try {
      tour = new ChinesePostman<IExpr, DefaultWeightedEdge>().getCPPSolution(walkable);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.CEmptyList;
    }
    if (tour == null) {
      return F.CEmptyList;
    }

    final List<IExpr> walk = tour.getVertexList();
    IAST edges = F.mapRange(0, walk.size() - 1, i -> F.DirectedEdge(walk.get(i), walk.get(i + 1)));
    return limit >= 1 ? F.list(edges) : F.CEmptyList;
  }

  /** All the edges have to be reachable from one another, following direction where there is one. */
  private static boolean isTraversable(Graph<IExpr, DefaultWeightedEdge> graph) {
    if (graph.getType().isDirected()) {
      return GraphTests.isStronglyConnected(graph);
    }
    return new ConnectivityInspector<IExpr, DefaultWeightedEdge>(graph).isConnected();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
