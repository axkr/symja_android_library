package org.matheclipse.core.visjs;

import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/** Graph visualization with <a href="https://visjs.org/">vis-network</a> */
public class SimpleVisGraphExample {
  private static final String VISJS_PAGE = //
      "<html>\n" + //
          "<head>\n" + //
          "<meta charset=\"utf-8\">\n" + //
          "<head>\n" + //
          "  <title>VIS-Network</title>\n" + //
          "\n" + //
          "  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@5.0.0/dist/vis-network.min.js\"></script>\n"
          + //
          "  <style type=\"text/css\">\n" + //
          "    #mynetwork {\n" + //
          "      width: 600px;\n" + //
          "      height: 400px;\n" + //
          "      border: 1px solid lightgray;\n" + //
          "    }\n" + //
          "  </style>\n" + //
          "</head>\n" + //
          "<body>\n" + //
          "\n" + //
          "<h1>VIS-Network</h1>\n" + //
          "\n" + //
          "<div id=\"vis\"></div>\n" + //
          "\n" + //
          "<script type=\"text/javascript\">\n" + //
          "`1`\n" + //
          "  // create a network\n" + //
          "  var container = document.getElementById('vis');\n" + //
          "  var data = {\n" + //
          "    nodes: nodes,\n" + //
          "    edges: edges\n" + //
          "  };\n" + //
          "  var options = {};\n" + //
          "  var network = new vis.Network(container, data, options);\n" + //
          "</script>\n" + //
          "\n" + //
          "\n" + //
          "</body>\n" + //
          "</html>"; //

  public static void main(String[] args) {
    try {
      Config.USE_VISJS = true;
      ExprEvaluator util = new ExprEvaluator();

      // IExpr result = util
      // .eval("Graph({a \\\\[UndirectedEdge] b, b \\\\[UndirectedEdge] c, c \\\\[UndirectedEdge]
      // a})");
      // IExpr result = util.eval("Graph({1 \\\\[DirectedEdge] 2, 2 \\\\[DirectedEdge] 3, 3
      // \\\\[DirectedEdge]
      // 1})");
      // IExpr result = util
      // .eval("Graph({1 \\\\[UndirectedEdge] 2, 2 \\\\[UndirectedEdge] 3, 3 \\\\[UndirectedEdge]
      // 1})");
      IExpr result = util.eval(
          "Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}, {EdgeWeight -> {2, 3, 4}})");
      if (result instanceof GraphExpr) {
        String javaScriptStr = ((GraphExpr) result).graphToJSForm();
        if (javaScriptStr != null) {
          String js = VISJS_PAGE;
          js = StringUtils.replace(js, "`1`", javaScriptStr);
          System.out.println(js);
        }
      }
    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }
}
