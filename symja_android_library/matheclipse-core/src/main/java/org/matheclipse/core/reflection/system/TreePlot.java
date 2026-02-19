package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.graphics.GraphGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements TreePlot.
 * <p>
 * Generates a Graphics AST representing the tree structure of a graph.
 */
public class TreePlot extends AbstractFunctionOptionEvaluator {

  public TreePlot() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    
    IExpr arg1 = ast.arg1();
    GraphExpr<?> graphExpr = GraphExpr.newInstance(arg1);
    if (graphExpr == null) {
      return F.NIL;
    }

    IASTAppendable optionsList = F.ListAlloc(2);
    if (options[GraphGraphics.X_DIRECTED_EDGES].isTrue()) {
      optionsList.append(F.Rule(S.DirectedEdges, S.True));
    }
    optionsList.append(F.Rule(S.GraphLayout, options[GraphGraphics.X_GRAPH_LAYOUT]));

    graphExpr.setOptions(optionsList);
    GraphGraphics graphics = new GraphGraphics(graphExpr);
    return graphics.toGraphics();

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
        GraphGraphics.defaultGraphOptionValues());
  }
}
