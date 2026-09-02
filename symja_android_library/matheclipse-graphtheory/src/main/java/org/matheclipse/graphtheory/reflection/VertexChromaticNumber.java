package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.ExactVertexColoring;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>VertexChromaticNumber(graph)</code> - the smallest number of colors that can be assigned to
 * the vertices of <code>graph</code> so that no two adjacent vertices share a color.
 *
 * <p>
 * This is the number of distinct colors in <code>FindVertexColoring(graph)</code> and is computed by
 * the same exact search.
 */
public class VertexChromaticNumber extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    // self loops, edge directions and edge weights are all ignored, exactly as in
    // FindVertexColoring
    Graph<IExpr, ?> g = gex.toData();
    VertexColoringSupport.GraphData data = VertexColoringSupport.normalize(g);
    int n = data.vertexCount();
    if (n == 0) {
      return F.C0;
    }

    // a graph without edges needs one color and never reaches the branch-and-bound, because there
    // the clique lower bound already equals the DSATUR upper bound
    ExactVertexColoring solver = new ExactVertexColoring(n, data.adjacency);
    ExactVertexColoring.Result result = solver.solve(Integer.MAX_VALUE);
    if (!result.proven) {
      // Iteration limit of `1` exceeded for `2`.
      Errors.printMessage(ast.topHead(), "itlim",
          F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
    }
    return F.ZZ(result.usedColors);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
