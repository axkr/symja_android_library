package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class IncidenceMatrix extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    Graph<IExpr, ?> graph = gex.toData();
    if (graph == null) {
      return F.NIL;
    }

    GraphType type = graph.getType();
    boolean isDirected = type.isDirected();

    IAST verticesList = GraphExpr.vertexToIExpr(graph);
    IAST[] edgesIExpr = GraphExpr.edgesToIExpr(graph);

    if (edgesIExpr == null || edgesIExpr.length == 0) {
      return F.NIL;
    }

    IAST edgeList = edgesIExpr[0];

    int rows = verticesList.argSize();
    int cols = edgeList.argSize();

    // Map each vertex to its corresponding 1-based row index
    Map<IExpr, Integer> vertexToIndex = new HashMap<>();
    for (int i = 1; i <= rows; i++) {
      vertexToIndex.put(verticesList.get(i), i);
    }

    IASTAppendable rules = F.ListAlloc();
    for (int j = 1; j <= cols; j++) {
      IExpr edgeExpr = edgeList.get(j);

      if (edgeExpr.isAST2()) {
        IExpr u = edgeExpr.first();
        IExpr v = edgeExpr.second();

        Integer uIdx = vertexToIndex.get(u);
        Integer vIdx = vertexToIndex.get(v);

        if (uIdx != null && vIdx != null) {
          if (isDirected) {
            // Directed self-loops cancel out to 0, which is omitted in a sparse array.
            // For distinct vertices, mark the source as -1 and target as 1.
            if (!uIdx.equals(vIdx)) {
              rules.append(F.Rule(F.List(F.ZZ(uIdx), F.ZZ(j)), F.CN1));
              rules.append(F.Rule(F.List(F.ZZ(vIdx), F.ZZ(j)), F.C1));
            }
          } else {
            // Undirected self-loops have an incidence of 2
            if (uIdx.equals(vIdx)) {
              rules.append(F.Rule(F.List(F.ZZ(uIdx), F.ZZ(j)), F.C2));
            } else {
              rules.append(F.Rule(F.List(F.ZZ(uIdx), F.ZZ(j)), F.C1));
              rules.append(F.Rule(F.List(F.ZZ(vIdx), F.ZZ(j)), F.C1));
            }
          }
        }
      }
    }

    // Construct and return the SparseArray expression
    return F.sparseArray(rules, new int[] {rows, cols});
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
