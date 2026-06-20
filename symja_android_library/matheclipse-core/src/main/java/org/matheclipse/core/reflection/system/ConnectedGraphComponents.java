package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ConnectedGraphComponents extends AbstractFunctionEvaluator {

  public ConnectedGraphComponents() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || ast.argSize() > 2) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    GraphExpr graphExpr = null;

    if (arg1 instanceof GraphExpr) {
      graphExpr = (GraphExpr) arg1;
    } else if (arg1.isList()) {
      // If input is a list of rules, try to evaluate it to a Graph first
      // Example: ConnectedGraphComponents({1->2, 3->4})
      IExpr evaluatedGraph = engine.evaluate(F.Graph(arg1));
      if (evaluatedGraph instanceof GraphExpr) {
        graphExpr = (GraphExpr) evaluatedGraph;
      }
    }

    if (graphExpr == null) {
      return F.NIL;
    }
    return GraphUtil.connectedGraphComponents(graphExpr, ast.isAST2() ? ast.arg2() : F.NIL, engine);
  }
}