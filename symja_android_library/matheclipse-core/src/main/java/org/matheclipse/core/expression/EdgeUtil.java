package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Helpers for reading edge expressions, shared by <code>matheclipse-core</code> and the
 * <code>matheclipse-graphtheory</code> module.
 *
 * <p>
 * An edge may be wrapped in presentation heads such as <code>Labeled</code>, <code>Annotation</code>
 * or <code>Style</code>. Stripping those is pure expression work with no graph library involved, so
 * it lives here: <code>AbstractAST.isListOfEdges()</code> needs it to classify an edge list, and it
 * must keep working in a build without the graph module.
 */
public class EdgeUtil {

  /**
   * Strip the presentation heads from an edge expression.
   *
   * @return the innermost expression, which for a well-formed edge is the
   *         <code>DirectedEdge</code> / <code>UndirectedEdge</code> / <code>Rule</code> /
   *         <code>TwoWayRule</code> itself
   */
  public static IExpr unwrapEdge(IExpr expr) {
    while (expr.isAST() && expr.size() > 1) {
      IExpr head = expr.head();
      if (head == S.Labeled || head == S.Annotation || head == S.Style) {
        expr = expr.first();
        continue;
      }
      break;
    }
    return expr;
  }

  private EdgeUtil() {}
}
