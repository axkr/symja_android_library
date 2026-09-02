package org.matheclipse.graphtheory.io;

import java.io.IOException;
import java.io.Reader;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * Readers for the graph file formats, the mirror of
 * {@link GraphExpr#graphExport(java.io.Writer, Extension)}.
 *
 * <p>
 * This lives here rather than in <code>matheclipse-io</code>'s <code>Import</code> so that JGraphT
 * stays inside this module: reading a DOT or GraphML file names half a dozen JGraphT types, which
 * is exactly what the architecture rule forbids everywhere else.
 */
public class GraphImport {

  /**
   * Read a graph from a DOT or GraphML stream.
   *
   * @param format {@link Extension#DOT} or {@link Extension#GRAPHML}; anything else gives
   *        {@link F#NIL}
   * @return the graph, or {@link F#NIL} for an unsupported format
   * @throws IOException if the stream cannot be parsed as the given format
   */
  public static IExpr fromReader(Reader reader, Extension format, EvalEngine engine)
      throws IOException {
    try {
      Graph<IExpr, ExprEdge> result;
      switch (format) {
        case DOT:
          DOTImporter<IExpr, ExprEdge> dotImporter = new DOTImporter<IExpr, ExprEdge>();
          dotImporter.setVertexFactory(label -> engine.parse(label));
          result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          dotImporter.importGraph(result, reader);
          return GraphExpr.newInstance(result);
        case GRAPHML:
          result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          GraphMLImporter<IExpr, ExprEdge> graphMLImporter = new GraphMLImporter<IExpr, ExprEdge>();
          graphMLImporter.setVertexFactory(label -> engine.parse(label));
          graphMLImporter.importGraph(result, reader);
          return GraphExpr.newInstance(result);
        default:
      }
      return F.NIL;
    } catch (ImportException e) {
      throw new IOException(e);
    }
  }

  private GraphImport() {}
}
