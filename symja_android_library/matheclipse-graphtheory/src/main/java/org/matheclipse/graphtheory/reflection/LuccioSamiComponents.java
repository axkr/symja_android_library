package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
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
 * <code>LuccioSamiComponents(graph)</code> - the Luccio-Sami components of <code>graph</code>: sets
 * of vertices in which every proper subset has more ties to the rest of the set than to anything
 * outside it.
 *
 * <p>
 * <code>LuccioSamiComponents(graph, {v1, v2, ...})</code> keeps only the components holding at least
 * one of the listed vertices.
 *
 * <p>
 * A Luccio-Sami component, or LS set, is a stricter thing than a lambda component: the lambda
 * condition compares whole vertices, this one compares every way of splitting the set. Every LS set
 * is a lambda component, which is what makes the search practical - the lambda components are the
 * only candidates, and each is then tested directly.
 */
public class LuccioSamiComponents extends AbstractFunctionEvaluator {

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
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);

    List<Set<IExpr>> components = new ArrayList<Set<IExpr>>();
    boolean skipped = false;
    for (Set<IExpr> candidate : LambdaSupport.lambdaComponents(undirected)) {
      Boolean verdict = LambdaSupport.isLuccioSami(undirected, candidate);
      if (verdict == null) {
        skipped = true; // too large to walk every proper subset of
      } else if (verdict.booleanValue()) {
        components.add(candidate);
      }
    }
    if (skipped) {
      // Iteration limit of `1` exceeded for `2`.
      Errors.printMessage(ast.topHead(), "itlim",
          F.list(F.ZZ(LambdaSupport.MAX_LUCCIO_SAMI_SIZE), ast), engine);
    }

    if (wanted != null) {
      final IAST filter = wanted;
      components.removeIf(set -> {
        for (int i = 1; i < filter.size(); i++) {
          if (set.contains(filter.get(i))) {
            return false;
          }
        }
        return true;
      });
    }
    components.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed()
        .thenComparingInt(set -> firstPosition(vertexList, set)));

    IASTAppendable result = F.ListAlloc(components.size());
    for (Set<IExpr> component : components) {
      IASTAppendable vertices = F.ListAlloc(component.size());
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

  private static int firstPosition(IAST vertexList, Set<IExpr> set) {
    for (int i = 1; i <= vertexList.argSize(); i++) {
      if (set.contains(vertexList.get(i))) {
        return i;
      }
    }
    return Integer.MAX_VALUE;
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
