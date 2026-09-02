package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>KCoreComponents(graph, k)</code> - the k-core components of <code>graph</code>: the maximal
 * weakly connected subgraphs in which every vertex has degree at least <code>k</code>, each given as
 * a list of vertices.
 *
 * <p>
 * <code>KCoreComponents(graph, k, "In")</code> and <code>KCoreComponents(graph, k, "Out")</code>
 * count only the incoming or only the outgoing edges of a directed graph.
 *
 * <p>
 * The k-core is what is left when vertices of degree below <code>k</code> are removed over and over,
 * because removing one lowers the degree of its neighbours and can make them fall below the
 * threshold in turn. A graph without a k-core gives <code>{}</code>.
 */
public class KCoreComponents extends AbstractFunctionEvaluator {

  private static final int DEGREE = 0;
  private static final int IN_DEGREE = 1;
  private static final int OUT_DEGREE = 2;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int k = ast.arg2().toIntDefault();
    if (k < 0) {
      // Non-negative machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C2), engine);
    }
    int mode = DEGREE;
    if (ast.isAST3()) {
      IExpr arg3 = ast.arg3();
      if (arg3.isString("In")) {
        mode = IN_DEGREE;
      } else if (arg3.isString("Out")) {
        mode = OUT_DEGREE;
      } else {
        // `1` is not a valid `2` specification.
        return Errors.printMessage(ast.topHead(), "bspec",
            F.list(arg3, F.stringx("degree")), engine);
      }
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    IAST vertexList = GraphExpr.vertexToIExpr(g);
    if (vertexList.argSize() == 0) {
      return F.CEmptyList;
    }
    // components are weakly connected, so direction never matters for connectivity - only for the
    // degree that decides who survives the peeling
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);

    Set<IExpr> core = new LinkedHashSet<IExpr>(undirected.vertexSet());
    peel(g, undirected, core, k, mode);
    if (core.isEmpty()) {
      return F.CEmptyList;
    }

    List<Set<IExpr>> components =
        new ArrayList<Set<IExpr>>(new ConnectivityInspector<IExpr, ExprEdge>(
            new AsSubgraph<IExpr, ExprEdge>(undirected, core)).connectedSets());
    // the largest component first, as ConnectedComponents reports them
    components.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed());

    IASTAppendable result = F.ListAlloc(components.size());
    for (Set<IExpr> component : components) {
      IASTAppendable vertices = F.ListAlloc(component.size());
      // inside a component, keep the VertexList order
      for (int i = 1; i <= vertexList.argSize(); i++) {
        IExpr vertex = vertexList.get(i);
        if (component.contains(vertex)) {
          vertices.append(vertex);
        }
      }
      result.append(vertices);
    }
    return result;
  }

  /** Drop vertices below the threshold until none is left, counting only surviving neighbours. */
  private static void peel(Graph<IExpr, ?> directed, Graph<IExpr, ExprEdge> undirected,
      Set<IExpr> core, int k, int mode) {
    boolean removed = true;
    while (removed) {
      removed = false;
      for (java.util.Iterator<IExpr> it = core.iterator(); it.hasNext();) {
        IExpr vertex = it.next();
        if (degree(directed, undirected, core, vertex, mode) < k) {
          it.remove();
          removed = true;
        }
      }
    }
  }

  private static int degree(Graph<IExpr, ?> directed, Graph<IExpr, ExprEdge> undirected,
      Set<IExpr> core, IExpr vertex, int mode) {
    if (mode == DEGREE) {
      int count = 0;
      for (ExprEdge edge : undirected.edgesOf(vertex)) {
        if (core.contains(Graphs.getOppositeVertex(undirected, edge, vertex))) {
          count++;
        }
      }
      return count;
    }
    Set<?> edges = mode == IN_DEGREE ? directed.incomingEdgesOf(vertex) //
        : directed.outgoingEdgesOf(vertex);
    int count = 0;
    for (Object edge : edges) {
      @SuppressWarnings("unchecked")
      IExpr other = Graphs.getOppositeVertex((Graph<IExpr, Object>) directed, edge, vertex);
      if (!other.equals(vertex) && core.contains(other)) {
        count++;
      }
    }
    return count;
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
