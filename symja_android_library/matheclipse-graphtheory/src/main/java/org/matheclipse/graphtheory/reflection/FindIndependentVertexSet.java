package org.matheclipse.graphtheory.reflection;

import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.alg.VertexSetSearch;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindIndependentVertexSet(graph)</code> - an independent vertex set of <code>graph</code>
 * with a maximum number of vertices: a set of vertices no two of which are joined by an edge.
 *
 * <p>
 * <code>FindIndependentVertexSet(graph, n)</code> asks for at most <code>n</code> vertices,
 * <code>FindIndependentVertexSet(graph, {n})</code> for exactly <code>n</code>, and
 * <code>FindIndependentVertexSet(graph, {min, max})</code> for a size in that range.
 * <code>FindIndependentVertexSet(graph, nspec, s)</code> returns up to <code>s</code> of them and
 * <code>FindIndependentVertexSet(graph, nspec, All)</code> every one, largest first. The result is
 * an empty list when the size cannot be met.
 *
 * <p>
 * An independent set of a graph is a clique of its complement, so this is a maximum clique search on
 * the complement. Edge directions and edge weights are ignored, and a self loop joins its vertex to
 * itself, which does not stop that vertex from being in a set on its own.
 */
public class FindIndependentVertexSet extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    VertexSetSearch.SizeSpec spec = ast.size() > 2 //
        ? VertexSetSearch.sizeSpec(ast.arg2()) //
        : VertexSetSearch.sizeSpec(F.CInfinity);
    if (spec.invalid) {
      // Non-negative machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C2), engine);
    }
    // without the third argument only one set is reported
    int limit = 1;
    if (ast.size() > 3) {
      IExpr arg3 = ast.arg3();
      if (arg3 == S.All || arg3.isInfinity()) {
        limit = Integer.MAX_VALUE;
      } else {
        limit = arg3.toIntDefault();
        if (limit < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C3), engine);
        }
      }
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    if (GraphExpr.vertexToIExpr(g).argSize() == 0) {
      return F.CEmptyList;
    }

    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);
    List<Set<IExpr>> sets = VertexSetSearch.independentSets(undirected, spec, limit);
    IASTAppendable result = F.ListAlloc(sets.size());
    for (Set<IExpr> set : sets) {
      IASTAppendable vertices = F.ListAlloc(set.size());
      for (IExpr vertex : set) {
        vertices.append(vertex);
      }
      result.append(vertices);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
