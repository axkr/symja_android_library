package org.matheclipse.core.interfaces;

import java.io.IOException;
import java.io.Writer;
import org.matheclipse.core.io.Extension;

/**
 * A graph, as seen from <code>matheclipse-core</code>.
 *
 * <p>
 * The implementation (<code>GraphExpr</code>) and the JGraphT library it wraps live in the
 * <code>matheclipse-graphtheory</code> module. Core, and the modules which format or export a
 * result, only ever need to ask a graph to render or write itself, so they go through this
 * interface and never name the implementation.
 *
 * <p>
 * There is deliberately no factory counterpart. WXF serialization writes a graph through
 * {@link IExpr#fullForm()}, which produces the ordinary <code>Graph(vertices, edges)</code>
 * expression, and reading that back re-evaluates it through the normal evaluator — so nothing in
 * core has to reconstruct a graph object directly.
 */
public interface IGraphExpr extends IExpr {

  /**
   * This graph as a JavaScript literal for the browser-side renderer.
   *
   * @return the JavaScript source, or an empty string when it cannot be produced
   */
  String graphToJSForm();

  /**
   * The <code>Graph(vertices, edges)</code> expression form of this graph.
   *
   * <p>
   * This is what WXF serialization writes: reading it back yields the ordinary expression, which
   * the evaluator turns into a graph again, so no reconstruction hook is needed in core.
   */
  IAST fullForm();

  /**
   * Write this graph in one of the graph file formats.
   *
   * @param writer the destination
   * @param format {@link Extension#DOT}, {@link Extension#GRAPHML}, or anything else for the
   *        default edge-list CSV
   * @throws IOException if writing fails, including a failure reported by the exporter
   */
  void graphExport(Writer writer, Extension format) throws IOException;
}
