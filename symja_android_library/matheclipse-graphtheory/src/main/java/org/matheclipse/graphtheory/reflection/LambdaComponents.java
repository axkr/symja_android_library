package org.matheclipse.graphtheory.reflection;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.LambdaSupport;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>LambdaComponents(graph)</code> - the lambda components of <code>graph</code>: sets of
 * vertices joined to each other by more edge-independent paths than to any vertex outside the set.
 *
 * <p>
 * <code>LambdaComponents(graph, {v1, v2, ...})</code> keeps only the components holding at least one
 * of the listed vertices.
 *
 * <p>
 * Writing <code>lambda(u,v)</code> for the number of edge-disjoint paths between two vertices - the
 * smallest number of edges whose removal separates them - the condition is that
 * <code>lambda</code> inside the set exceeds <code>lambda</code> to anything outside it. Because
 * <code>lambda(u,w) &gt;= min(lambda(u,v), lambda(v,w))</code>, the relation
 * "<code>lambda</code> is at least <code>t</code>" is an equivalence for every <code>t</code>, and
 * its classes are exactly the lambda components. They therefore nest rather than overlap, and every
 * threshold contributes its own, so a vertex can belong to several of different sizes.
 *
 * <p>
 * All the pairwise values come from a Gomory-Hu tree, one per connected component: cutting that tree
 * at each of its edge weights produces the classes directly.
 */
public class LambdaComponents extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    IAST wanted = null;
    if (ast.isAST2()) {
      if (!ast.arg2().isList()) {
        return F.NIL;
      }
      wanted = (IAST) ast.arg2();
    }

    Graph<IExpr, ?> g = gex.toData();
    IAST vertexList = GraphExpr.vertexToIExpr(g);
    if (vertexList.argSize() == 0) {
      return F.CEmptyList;
    }
    // edge directions do not carry independent paths any differently here
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);

    List<Set<IExpr>> distinct = LambdaSupport.lambdaComponents(undirected);

    if (wanted != null) {
      final IAST filter = wanted;
      distinct.removeIf(set -> {
        for (int i = 1; i < filter.size(); i++) {
          if (set.contains(filter.get(i))) {
            return false;
          }
        }
        return true;
      });
    }
    distinct.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed()
        .thenComparingInt(set -> firstPosition(vertexList, set)));

    IASTAppendable result = F.ListAlloc(distinct.size());
    for (Set<IExpr> component : distinct) {
      result.append(inVertexListOrder(vertexList, component));
    }
    return result;
  }

  private static int firstPosition(IAST vertexList, Set<IExpr> set) {
    for (int i = 1; i <= vertexList.argSize(); i++) {
      if (set.contains(vertexList.get(i))) {
        return i;
      }
    }
    return Integer.MAX_VALUE;
  }

  private static IAST inVertexListOrder(IAST vertexList, Set<IExpr> set) {
    IASTAppendable vertices = F.ListAlloc(set.size());
    for (int i = 1; i <= vertexList.argSize(); i++) {
      IExpr vertex = vertexList.get(i);
      if (set.contains(vertex)) {
        vertices.append(vertex);
      }
    }
    return vertices;
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
