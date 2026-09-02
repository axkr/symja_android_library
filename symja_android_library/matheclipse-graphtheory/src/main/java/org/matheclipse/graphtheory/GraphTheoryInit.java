package org.matheclipse.graphtheory;

import org.matheclipse.core.expression.S;
import org.matheclipse.graphtheory.builtin.GraphDataFunctions;
import org.matheclipse.graphtheory.builtin.GraphFunctions;

/**
 * Registers the graph theory functions of the <code>matheclipse-graphtheory</code> module with the
 * evaluation engine. Call this after <code>F.initSymja()</code>;
 * <code>org.matheclipse.io.IOInit</code> already does so for the servlets and the consoles, and
 * <code>org.matheclipse.api.Pods</code> for the API server.
 *
 * <p>
 * <code>matheclipse-core</code> owns the <code>Graph</code>, <code>VertexList</code>, ... symbols
 * but no longer implements them: without this module they stay unevaluated, exactly as
 * <code>SunPosition</code> does without <code>matheclipse-astro</code>.
 */
public class GraphTheoryInit {

  public static void init() {
    GraphFunctions.initialize();
    GraphDataFunctions.initialize();

    S.AcyclicGraphQ.setEvaluator(new org.matheclipse.graphtheory.reflection.AcyclicGraphQ());
    S.AdjacencyGraph.setEvaluator(new org.matheclipse.graphtheory.reflection.AdjacencyGraph());
    S.AdjacencyList.setEvaluator(new org.matheclipse.graphtheory.reflection.AdjacencyList());
    S.ChromaticNumber.setEvaluator(new org.matheclipse.graphtheory.reflection.ChromaticNumber());
    S.ChromaticPolynomial
        .setEvaluator(new org.matheclipse.graphtheory.reflection.ChromaticPolynomial());
    S.CompleteGraphQ.setEvaluator(new org.matheclipse.graphtheory.reflection.CompleteGraphQ());
    S.CompleteKaryTree.setEvaluator(new org.matheclipse.graphtheory.reflection.CompleteKaryTree());
    S.ConnectedGraphComponents
        .setEvaluator(new org.matheclipse.graphtheory.reflection.ConnectedGraphComponents());
    S.DirectedGraphQ.setEvaluator(new org.matheclipse.graphtheory.reflection.DirectedGraphQ());
    S.EdgeAdd.setEvaluator(new org.matheclipse.graphtheory.reflection.EdgeAdd());
    S.EdgeContract.setEvaluator(new org.matheclipse.graphtheory.reflection.EdgeContract());
    S.EdgeDelete.setEvaluator(new org.matheclipse.graphtheory.reflection.EdgeDelete());
    S.FindVertexColoring
        .setEvaluator(new org.matheclipse.graphtheory.reflection.FindVertexColoring());
    S.GlobalClusteringCoefficient
        .setEvaluator(new org.matheclipse.graphtheory.reflection.GlobalClusteringCoefficient());
    S.Graph3D.setEvaluator(new org.matheclipse.graphtheory.graphics.Graph3D());
    S.GraphDistance.setEvaluator(new org.matheclipse.graphtheory.reflection.GraphDistance());
    S.IncidenceMatrix.setEvaluator(new org.matheclipse.graphtheory.reflection.IncidenceMatrix());
    S.KaryTree.setEvaluator(new org.matheclipse.graphtheory.reflection.KaryTree());
    S.KirchhoffMatrix.setEvaluator(new org.matheclipse.graphtheory.reflection.KirchhoffMatrix());
    S.LocalClusteringCoefficient
        .setEvaluator(new org.matheclipse.graphtheory.reflection.LocalClusteringCoefficient());
    S.MeanClusteringCoefficient
        .setEvaluator(new org.matheclipse.graphtheory.reflection.MeanClusteringCoefficient());
    S.NeighborhoodGraph
        .setEvaluator(new org.matheclipse.graphtheory.reflection.NeighborhoodGraph());
    S.Subgraph.setEvaluator(new org.matheclipse.graphtheory.reflection.Subgraph());
    S.TopologicalSort.setEvaluator(new org.matheclipse.graphtheory.reflection.TopologicalSort());
    S.TreeGraph.setEvaluator(new org.matheclipse.graphtheory.reflection.TreeGraph());
    S.TreeGraphQ.setEvaluator(new org.matheclipse.graphtheory.reflection.TreeGraphQ());
    S.TreePlot.setEvaluator(new org.matheclipse.graphtheory.reflection.TreePlot());
    S.VertexAdd.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexAdd());
    S.VertexContract.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexContract());
    S.VertexDegree.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexDegree());
    S.VertexDelete.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexDelete());
    S.VertexInDegree.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexInDegree());
    S.VertexOutDegree.setEvaluator(new org.matheclipse.graphtheory.reflection.VertexOutDegree());
  }

  private GraphTheoryInit() {}
}
