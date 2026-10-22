package org.matheclipse.graphtheory.reflection;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>VertexComponent(g, v)</code> - the vertices from which <code>v</code> can be reached,
 * <code>VertexInComponent(g, v)</code> - the same, and <code>VertexOutComponent(g, v)</code> - the
 * vertices which can be reached from <code>v</code>. In an undirected graph all three are the
 * connected component of <code>v</code>.
 *
 * <p>
 * <code>v</code> may be a list of vertices, and a third argument <code>k</code> limits the
 * components to the vertices at most <code>k</code> edges away. The vertices are listed in
 * breadth-first order from each start vertex in turn, the neighbors of a vertex in the order of
 * their edges.
 */
public class VertexComponent extends AbstractFunctionEvaluator {

  /** <code>true</code> for <code>VertexOutComponent</code>, which follows the edge directions */
  private final boolean out;

  public VertexComponent(boolean out) {
    this.out = out;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    @SuppressWarnings("unchecked")
    Graph<IExpr, Object> g = (Graph<IExpr, Object>) gex.toData();
    int maxDistance = Integer.MAX_VALUE;
    if (ast.isAST3()) {
      maxDistance = ast.arg3().isInfinity() ? Integer.MAX_VALUE : ast.arg3().toIntDefault();
      if (maxDistance < 0) {
        return F.NIL;
      }
    }
    IExpr start = ast.arg2();
    IAST starts;
    if (g.containsVertex(start)) {
      // a list may be a vertex name itself
      starts = F.list(start);
    } else if (start.isList() && ((IAST) start).forAll(v -> g.containsVertex(v))) {
      starts = (IAST) start;
    } else {
      return F.NIL;
    }

    boolean directed = g.getType().isDirected();
    Set<IExpr> component = new LinkedHashSet<IExpr>();
    for (IExpr vertex : starts) {
      if (!component.add(vertex)) {
        continue;
      }
      ArrayDeque<IExpr> queue = new ArrayDeque<IExpr>();
      ArrayDeque<Integer> distances = new ArrayDeque<Integer>();
      queue.add(vertex);
      distances.add(0);
      while (!queue.isEmpty()) {
        IExpr v = queue.poll();
        int distance = distances.poll();
        if (distance >= maxDistance) {
          continue;
        }
        Iterable<Object> edges = !directed ? g.edgesOf(v)
            : out ? g.outgoingEdgesOf(v) : g.incomingEdgesOf(v);
        for (Object edge : edges) {
          IExpr w = Graphs.getOppositeVertex(g, edge, v);
          if (component.add(w)) {
            queue.add(w);
            distances.add(distance + 1);
          }
        }
      }
    }
    IASTAppendable result = F.ListAlloc(component.size());
    for (IExpr v : component) {
      result.append(v);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
