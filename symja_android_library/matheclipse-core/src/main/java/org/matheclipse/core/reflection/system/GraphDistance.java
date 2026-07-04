package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class GraphDistance extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, final IAST originalAST) {

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();

    IExpr source = ast.arg2();
    if (!g.containsVertex(source)) {
      return F.NIL;
    }

    IExpr target = null;
    if (argSize == 3) {
      target = ast.arg3();
      if (!g.containsVertex(target)) {
        return F.NIL;
      }
    }

    String method = "Automatic";
    if (options[0].isString()) {
      method = options[0].toString();
    } else if (options[0].isSymbol()) {
      method = options[0].toString();
    }

    ShortestPathAlgorithm<IExpr, ?> alg;
    if ("BellmanFord".equalsIgnoreCase(method)) {
      alg = new BellmanFordShortestPath<>(g);
    } else if ("UnitWeight".equalsIgnoreCase(method)) {
      alg = new BFSShortestPath<>(g);
    } else {
      // Default: Automatic or Dijkstra
      if ("Automatic".equalsIgnoreCase(method) && !gex.isWeightedGraph()) {
        alg = new BFSShortestPath<>(g);
      } else {
        alg = new DijkstraShortestPath<>(g);
      }
    }

    SingleSourcePaths<IExpr, ?> iPaths = alg.getPaths(source);

    if (argSize == 3) {
      return getDistance(gex, iPaths, target);
    } else {
      IAST vertexList = GraphExpr.vertexToIExpr(g);
      IASTAppendable result = F.ListAlloc(vertexList.argSize());
      for (int i = 1; i <= vertexList.argSize(); i++) {
        result.append(getDistance(gex, iPaths, vertexList.get(i)));
      }
      return result;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  private IExpr getDistance(GraphExpr<?> gex, SingleSourcePaths<IExpr, ?> iPaths, IExpr target) {
    if (iPaths.getSourceVertex().equals(target)) {
      return F.C0;
    }

    GraphPath<IExpr, ?> path = iPaths.getPath(target);
    if (path == null) {
      return F.CInfinity;
    }

    if (gex.isWeightedGraph()) {
      return F.num(path.getWeight());
    }
    return F.ZZ(path.getLength());
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Method}, new IExpr[] {S.Automatic});
  }
}
