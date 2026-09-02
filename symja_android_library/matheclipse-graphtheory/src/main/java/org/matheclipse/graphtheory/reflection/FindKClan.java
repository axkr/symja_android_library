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
import org.matheclipse.graphtheory.alg.ExactVertexColoring;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.alg.VertexSetSearch;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindKClan(graph, k)</code> - a largest k-clan of <code>graph</code>: a k-clique whose
 * induced subgraph has diameter at most <code>k</code>.
 *
 * <p>
 * A k-clique only asks that its members lie within <code>k</code> steps of each other in the whole
 * graph, and those paths are free to run through vertices the set does not contain. A k-clan asks
 * for the stronger thing: the members must reach each other within <code>k</code> steps using only
 * edges between themselves. Every k-clan is therefore a k-clique, and
 * <code>FindKClan(graph, 1)</code> agrees with <code>FindClique(graph)</code>.
 *
 * <p>
 * <code>FindKClan(graph, k, n)</code> asks for at most <code>n</code> vertices,
 * <code>FindKClan(graph, k, {n})</code> for exactly <code>n</code>, and
 * <code>FindKClan(graph, k, {min, max})</code> for a size in that range.
 * <code>FindKClan(graph, k, nspec, s)</code> returns up to <code>s</code> of them and
 * <code>FindKClan(graph, k, nspec, All)</code> every one, largest first.
 */
public class FindKClan extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int k = ast.arg2().toIntDefault();
    if (k < 1) {
      // Positive machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
    }
    VertexSetSearch.SizeSpec spec = ast.size() > 3 //
        ? VertexSetSearch.sizeSpec(ast.arg3()) //
        : VertexSetSearch.sizeSpec(F.CInfinity);
    if (spec.invalid) {
      // Non-negative machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C3), engine);
    }
    int limit = 1;
    if (ast.size() > 4) {
      IExpr arg4 = ast.arg4();
      if (arg4 == S.All || arg4.isInfinity()) {
        limit = Integer.MAX_VALUE;
      } else {
        limit = arg4.toIntDefault();
        if (limit < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C4), engine);
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
    VertexSetSearch.SetResult found = VertexSetSearch.kClans(undirected, k, spec, limit);
    if (!found.proven) {
      // Iteration limit of `1` exceeded for `2`.
      Errors.printMessage(ast.topHead(), "itlim",
          F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
    }
    List<Set<IExpr>> sets = found.sets;
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
    return ARGS_2_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
