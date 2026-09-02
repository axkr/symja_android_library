package org.matheclipse.core.interfaces;

/**
 * The kind of graph a list of edge expressions describes, as classified by
 * {@link IExpr#isListOfEdges()}.
 *
 * <p>
 * This exists so that <code>matheclipse-core</code> can classify an edge list without naming a
 * JGraphT type. <code>IExpr</code> is the root interface of every Symja expression, so a
 * <code>org.jgrapht.GraphType</code> return value there would make the graph library a dependency
 * of the whole core module. The <code>matheclipse-graphtheory</code> module maps these constants
 * onto JGraphT's <code>DefaultGraphType</code> in one place.
 */
public enum EdgeListType {

  /** Every edge is a <code>DirectedEdge</code> or a <code>Rule</code>. */
  DIRECTED,

  /** Every edge is an <code>UndirectedEdge</code> or a <code>TwoWayRule</code>. */
  UNDIRECTED,

  /**
   * The list mixes directed and undirected edges. JGraphT has no counterpart for a mixed graph; see
   * <code>GraphExpr#createMixedGraph()</code>.
   */
  MIXED
}
