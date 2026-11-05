package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;

/**
 * <code>WeightedAdjacencyGraph(wmat)</code> or <code>WeightedAdjacencyGraph({v1, v2, ...}, wmat)</code>
 * - the graph with an edge from vertex <code>i</code> to vertex <code>j</code> of the weight
 * <code>wmat[[i, j]]</code> for every entry which is not <code>Infinity</code>. A symmetric matrix
 * gives an undirected graph from its upper triangle, any other a directed graph. Trailing options
 * are passed on to the graph.
 */
public class WeightedAdjacencyGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int positional = 0;
    while (positional < ast.argSize() && !ast.get(positional + 1).isRuleAST()) {
      positional++;
    }
    if (positional < 1 || positional > 2) {
      return F.NIL;
    }
    for (int i = positional + 1; i <= ast.argSize(); i++) {
      if (!ast.get(i).isRuleAST()) {
        return F.NIL;
      }
    }
    IExpr matrixArg = ast.get(positional);
    int[] dimension = matrixArg.isMatrix(false);
    if (dimension == null || dimension[0] != dimension[1]) {
      return F.NIL;
    }
    IAST matrix = (IAST) matrixArg.normal(false);
    int n = dimension[0];
    GraphUtil.checkGraphSize(n, (long) n * n);
    IAST vertices;
    if (positional == 2) {
      if (!ast.arg1().isList() || ast.arg1().argSize() != n) {
        return F.NIL;
      }
      vertices = (IAST) ast.arg1();
    } else {
      IASTAppendable range = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        range.append(F.ZZ(i));
      }
      vertices = range;
    }
    boolean symmetric = true;
    for (int i = 1; i <= n && symmetric; i++) {
      for (int j = i + 1; j <= n; j++) {
        if (!matrix.getPart(i, j).equals(matrix.getPart(j, i))) {
          symmetric = false;
          break;
        }
      }
    }
    IASTAppendable edges = F.ListAlloc();
    IASTAppendable weights = F.ListAlloc();
    for (int i = 1; i <= n; i++) {
      for (int j = symmetric ? i : 1; j <= n; j++) {
        IExpr weight = matrix.getPart(i, j);
        if (!weight.isInfinity()) {
          IExpr from = vertices.get(i);
          IExpr to = vertices.get(j);
          edges.append(symmetric ? F.UndirectedEdge(from, to) : F.DirectedEdge(from, to));
          weights.append(weight);
        }
      }
    }
    IASTAppendable graph = F.ast(S.Graph, ast.argSize() - positional + 3);
    graph.append(vertices);
    graph.append(edges);
    if (weights.argSize() > 0) {
      graph.append(F.Rule(S.EdgeWeight, weights));
    }
    for (int i = positional + 1; i <= ast.argSize(); i++) {
      graph.append(ast.get(i));
    }
    return graph;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
