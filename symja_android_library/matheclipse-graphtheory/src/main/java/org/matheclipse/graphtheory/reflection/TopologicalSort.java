package org.matheclipse.graphtheory.reflection;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns a topological ordering of the vertices in a directed acyclic graph. If the graph contains
 * cycles, returns $Failed.
 */
public class TopologicalSort extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // a graph or a list of edges or rules
    IExpr arg1 = ast.arg1();
    if (arg1.isList() && ((IAST) arg1).argSize() > 0 && ((IAST) arg1).forAll(x -> x.isRuleAST())) {
      arg1 = ((IAST) arg1).map(x -> F.DirectedEdge(x.first(), x.second()), 1);
    }
    GraphExpr<?> gex = GraphExpr.newInstance(arg1);
    if (gex == null) {
      return F.NIL;
    }

    Graph<IExpr, ?> g = gex.toData();

    try {
      // of the vertices which may come next, take the first in VertexList order
      Map<IExpr, Integer> position = new HashMap<>();
      for (IExpr v : g.vertexSet()) {
        position.put(v, position.size());
      }
      TopologicalOrderIterator<IExpr, ?> iterator = new TopologicalOrderIterator<>(g,
          (u, v) -> Integer.compare(position.get(u), position.get(v)));
      IASTAppendable result = F.ListAlloc();
      while (iterator.hasNext()) {
        result.append(iterator.next());
      }
      return result;
    } catch (IllegalArgumentException e) {
      // Topological sort is only possible for DAGs
      return S.$Failed;
    }

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
