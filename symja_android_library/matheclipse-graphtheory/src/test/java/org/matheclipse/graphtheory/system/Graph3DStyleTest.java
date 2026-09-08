package org.matheclipse.graphtheory.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.GraphTheoryInit;

/**
 * <code>Graph3D</code> styling, moved here from <code>matheclipse-core</code>'s
 * <code>Plot3DStyleOptionsTest</code> along with the function it exercises.
 */
public class Graph3DStyleTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    GraphTheoryInit.init();
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(x,y,z,i,j,k,n,t,u,v)");
  }

  private static final String RED = "RGBColor(1,0,0";

  private static String plot(String input) {
    // the printer wraps long lines; join them so that contains() is not tripped by a break
    return evaluator.eval(input).toString().replace("\n", "");
  }

  private static IExpr head(String input) {
    IExpr result = evaluator.eval(input);
    return result.isAST() ? ((IAST) result).head() : result;
  }

  private static int count(String input, String head) {
    return evaluator.eval("Length(Cases(" + input + "," + head + ",Infinity))").toIntDefault(-1);
  }

  @Test
  public void graph3DDrawsArrowsLabelsAndNamedLayouts() {
    assertTrue(count("Graph3D[Graph[{1->2,2->3}]]", "_Arrow") > 0, "a directed edge gets a head");
    assertEquals(0, count("Graph3D[Graph[{1<->2,2<->3}]]", "_Arrow"), "an undirected one does not");
    assertEquals(0, count("Graph3D[Graph[{1->2,2->3}],DirectedEdges->False]", "_Arrow"),
        "and the call can say otherwise");

    assertEquals(0, count("Graph3D[Graph[{1->2,2->3}]]", "_Text"), "no labels by default");
    assertTrue(count("Graph3D[Graph[{1->2,2->3}],VertexLabels->Automatic]", "_Text") > 0);
    assertTrue(plot("Graph3D[Graph[{1->2,2->3}],VertexLabels->{1->\"start\"}]").contains("start"));
    assertTrue(count("Graph3D[Graph[{1->2,2->3}],EdgeLabels->\"Name\"]", "_Text") > 0);
    assertTrue(
        plot("Graph3D[Graph[{1->2}],VertexLabels->Automatic,VertexLabelStyle->Red]").contains(RED));

    for (String layout : new String[] {"CircularEmbedding", "SpiralEmbedding"}) {
      assertEquals(S.Graphics3D,
          head("Graph3D[Graph[{1->2,2->3,3->1}],GraphLayout->\"" + layout + "\"]"),
          layout + " is a layout this plot knows");
    }
  }
}
