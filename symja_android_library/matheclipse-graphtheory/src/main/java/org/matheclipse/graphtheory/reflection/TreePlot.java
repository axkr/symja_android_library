package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.graphics.GraphGraphics;
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
    org.jgrapht.Graph<IExpr, ?> graph = graphExpr.toData();
    // TreePlot(g, pos) or TreePlot(g, root) or TreePlot(g, pos, root)
    IExpr orientation = S.Top;
    IExpr root = F.NIL;
    if (argSize >= 2) {
      IExpr second = ast.arg2();
      if (isPosition(second)) {
        orientation = second;
      } else if (graph.containsVertex(second)) {
        root = second;
      } else if (!second.isInteger() || argSize == 3) {
        // The second argument `1` of TreePlot must be one of Top, Bottom, Left, Right or Center.
        return Errors.printMessage(S.TreePlot, "rp", F.List(second), engine);
      }
      // an integer which is no vertex is ignored, as in the reference implementation
    }
    if (argSize == 3 && graph.containsVertex(ast.arg3())) {
      root = ast.arg3();
    }

    IASTAppendable optionsList = F.ListAlloc(2);
    if (options[GraphGraphics.X_DIRECTED_EDGES].isTrue()) {
      optionsList.append(F.Rule(S.DirectedEdges, S.True));
    }
    optionsList.append(F.Rule(S.GraphLayout, options[GraphGraphics.X_GRAPH_LAYOUT]));

    graphExpr.setOptions(optionsList);
    GraphGraphics graphics = new GraphGraphics(graphExpr);
    graphics.setTreeRoot(root, orientation);
    return graphics.toGraphics();

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_3;
  }

  private static boolean isPosition(IExpr expr) {
    return expr == S.Top || expr == S.Bottom || expr == S.Left || expr == S.Right
        || expr == S.Center;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(org.matheclipse.core.patternmatching.IPatternMatcher.SET, "rp",
        F.stringx("The second argument `1` of TreePlot must be one of Top, Bottom, Left, Right or Center."));
    setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
        GraphGraphics.defaultGraphOptionValues());
  }
}
