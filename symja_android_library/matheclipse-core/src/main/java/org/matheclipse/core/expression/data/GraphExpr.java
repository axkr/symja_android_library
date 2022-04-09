package org.matheclipse.core.expression.data;

import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class GraphExpr<T> extends DataExpr<Graph<IExpr, T>> {

  private static final long serialVersionUID = 6160043985328230156L;

  /**
   * Be cautious with this method, no new internal IExpr object is created
   *
   * @param value
   * @return
   */
  public static <T> GraphExpr<T> newInstance(final Graph<IExpr, T> value) {
    return new GraphExpr<T>(value);
  }

  protected GraphExpr(final Graph<IExpr, T> graph) {
    super(S.Graph, graph);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof GraphExpr) {
      return fData.equals(((GraphExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 283 : 283 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return GRAPHEXPRID;
  }

  @Override
  public IExpr copy() {
    return new GraphExpr<T>(fData);
  }

  /**
   * Test if the graph is instance of <code>DefaultDirectedWeightedGraph</code> or <code>
   * DefaultUndirectedWeightedGraph</code>
   *
   * @return <code>true</code> if the graph is a weighted graph
   */
  public boolean isWeightedGraph() {
    return fData instanceof DefaultDirectedWeightedGraph
        || fData instanceof DefaultUndirectedWeightedGraph;
  }

  public boolean isUndirectedGraph() {
    return fData instanceof SimpleGraph;
  }

  @Override
  public String toString() {
    if (fData instanceof AbstractBaseGraph) {
      AbstractBaseGraph<IExpr, ?> g = (AbstractBaseGraph<IExpr, ?>) fData;
      if (g.getType().isWeighted()) {
        return GraphFunctions.weightedGraphToIExpr((AbstractBaseGraph<IExpr, ExprWeightedEdge>) g)
            .toString();
      }
      return GraphFunctions.graphToIExpr((AbstractBaseGraph<IExpr, ExprEdge>) g).toString();
    }

    return fHead + "[" + fData.toString() + "]";
  }
}
