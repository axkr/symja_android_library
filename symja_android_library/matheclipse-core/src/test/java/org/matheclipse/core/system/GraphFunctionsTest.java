package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for graphics functions */
public class GraphFunctionsTest extends ExprEvaluatorTestCase {

  public GraphFunctionsTest(String name) {
    super(name);
  }

  public void testAdjacencyMatrix() {
    // order 1, 2, 3, 4
    check("AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) // Normal", //
        "{{0,1,1,0},\n" //
            + " {0,0,1,0},\n" + " {0,0,0,0},\n" + " {0,1,0,0}}");
    // order 4, 2, 1, 3 is like parsing order of the vertexes in the graph expression
    check("AdjacencyMatrix(Graph({4 -> 2, 1 -> 2, 2 -> 3, 1 -> 3})) // Normal", //
        "{{0,1,0,0},\n" //
            + " {0,0,0,1},\n" + " {0,1,0,1},\n" + " {0,0,0,0}}");
    // order d,b,c,a is like parsing order of the vertexes in the graph expression
    check("AdjacencyMatrix(Graph({d -> b, a -> b, b -> c, a -> c})) // Normal", //
        "{{0,1,0,0},\n" //
            + " {0,0,0,1},\n" + " {0,1,0,1},\n" + " {0,0,0,0}}");

    check(
        "AdjacencyMatrix(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))// Normal", //
        "{{0,1,1},\n" //
            + " {1,0,1},\n" + " {1,1,0}}");
    check(
        "AdjacencyMatrix(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))// Normal", //
        "{{0,1,0},\n" //
            + " {0,0,1},\n" + " {1,0,0}}");
  }

  public void testClosenessCentrality() {

    check("ClosenessCentrality[Graph[{1, 2, 3, 4, 5},{1<->2,1<->3,2<->3,3<->4,3<->5}]]", //
        "{0.666667,0.666667,1.0,0.571429,0.571429}");
  }

  public void testBetweennessCentrality() {
    check(
        "BetweennessCentrality( Graph({agent1, agent2, agent3, agent4, agent5}, \n"
            + "{agent1<->agent2,agent1<->agent3,agent2<->agent3,agent3<->agent4,agent3<->agent5}))", //
        "{0.0,0.0,5.0,0.0,0.0}");
    check(
        "BetweennessCentrality( Graph({1, 3, 2, 6, 4, 5}, \n"
            + "{ 2->5, 3->6, 4->6, 1->5, 5->4, 6->1}))", //
        "{5.0,0.0,0.0,7.0,5.0,7.0}");

    check(
        "BetweennessCentrality(  Graph({1, 2, 3, 6, 4, 5}, {UndirectedEdge(1, 2), \n"
            + "    UndirectedEdge(1, 3), UndirectedEdge(3, 6), UndirectedEdge(4, 6), \n"
            + "    UndirectedEdge(5, 1), UndirectedEdge(5, 4), UndirectedEdge(6, 1)}, \n"
            + "   {VertexShapeFunction -> {\"Name\"}}))  ", //
        "{5.5,0.0,0.0,2.0,0.5,1.0}");
    check("BetweennessCentrality( Graph({1, 3, 2, 6, 4, 5}, \n"
        + "{DirectedEdge(1, 3), DirectedEdge(2, 1), DirectedEdge(3, 6), DirectedEdge(4, 6), DirectedEdge(1, 5), DirectedEdge(5, 4), DirectedEdge(6, 1)}))", //
        "{12.0,2.0,0.0,8.0,3.0,4.0}");
  }

  public void testCompleteGraph() {
    check("CompleteGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},\n" //
            + " {1,0,1,1},\n" + " {1,1,0,1},\n" + " {1,1,1,0}}");
  }

  public void testCycleGraph() {
    check("CycleGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,0,1},\n" //
            + " {1,0,1,0},\n" + " {0,1,0,1},\n" + " {1,0,1,0}}");
  }

  public void testEdgeList() {
    check("EdgeList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
        "{1->2,2->3,1->3,4->2}");
    check(
        "EdgeList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1<->2,2<->3,3<->1}");
    check("EdgeList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
  }

  public void testEdgeRules() {
    check("EdgeRules(Graph({1 <-> 2, 2 <-> 3, 1 <-> 3, 4 <-> 2}))", //
        "{1->2,2->3,1->3,4->2}");
    check(
        "EdgeRules(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
    check("EdgeRules(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
  }

  public void testEdgeQ() {
    check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 3)", //
        "True");
    check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 4)", //
        "False");
  }

  public void testEulerianGraphQ() {
    check("EulerianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "True");
    check("EulerianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "False");
  }

  public void testCycle() {
    check(
        "FindCycle({2 -> 1, 1 -> 4, 3 -> 2, 2 -> 5, 6 -> 3, 5 -> 4, 4 -> 7, 6 -> 5, 8 -> 5, 6 -> 9, 7 -> 8, 7 -> 10, 9 -> 8, 11 -> 8, 12 -> 9, 10 -> 11, 12 -> 11}, {6,6}, All)", //
        "{{8->5,5->4,4->7,7->10,10->11,11->8}}");
    check(
        "FindCycle({2 -> 1, 1 -> 4, 3 -> 2, 2 -> 5, 6 -> 3, 5 -> 4, 4 -> 7, 6 -> 5, 8 -> 5, 6 -> 9, 7 -> 8, 7 -> 10, 9 -> 8, 11 -> 8, 12 -> 9, 10 -> 11, 12 -> 11})", //
        "{{8->5,5->4,4->7,7->8}}");
    check(
        "FindCycle({2 -> 1, 1 -> 4, 3 -> 2, 2 -> 5, 6 -> 3, 5 -> 4, 4 -> 7, 6 -> 5, 8 -> 5, 6 -> 9, 7 -> 8, 7 -> 10, 9 -> 8, 11 -> 8, 12 -> 9, 10 -> 11, 12 -> 11},Infinity,All)", //
        "{{8->5,5->4,4->7,7->8},{8->5,5->4,4->7,7->10,10->11,11->8}}");

    check("FindCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 6, 4 -> 5, 4 -> 6, 5 -> 1}))", //
        "{{1->2,2->3,3->4,4->5,5->1}}");
    check("WheelGraph(4)", //
        "Graph({1,2,3,4},{1<->2,2<->3,3<->1,1<->4,2<->4,3<->4})");
    check("FindCycle(WheelGraph(4))", //
        "{{2<->4,1<->4,1<->2}}");
  }
  
  public void testFindEulerianCycle() {
    check("FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))", //
        "{4->1,1->2,2->3,3->4}");
    check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "{4->1,1->3,3->1,1->2,2->3,3->4}");
    check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "{}");
  }

  public void testFindGraphIsomorphism() {
    check(
        "FindGraphIsomorphism(Graph({1,2,3,4},{1<->2,1<->4,2<->3,3<->4}), Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4}))", //
        "{<|1->2,2->3,3->1,4->4|>}");

    check("g=Graph({a,b,c,d},{a<->b,a<->d,b<->c,c<->d})", //
        "Graph({a,b,c,d},{a<->b,a<->d,b<->c,c<->d})");
    check("h=Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4})", //
        "Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4})");
    check(" FindGraphIsomorphism(g, h)", //
        "{<|a->2,b->3,c->1,d->4|>}");
  }

  public void testFindHamiltonianCycle() {
    check("FindHamiltonianCycle( {1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1} )", //
        "{1->2,2->3,3->4,4->1}");
    check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "{1->2,2->3,3->4,4->1}");
    check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "{}");
  }

  public void testFindVertexCover() {
    // example from wikipedia: https://en.wikipedia.org/wiki/Vertex_cover
    check("FindVertexCover({1<->2,1<->3,2<->3,3<->4,3<->5,3<->6})", //
        "{3,1}");

    check(
        "FindVertexCover({UndirectedEdge(2,1), UndirectedEdge(1,3), UndirectedEdge(3,6), UndirectedEdge(6,1)," //
            + " UndirectedEdge(4,6), UndirectedEdge(1,5), UndirectedEdge(5,4) })", //
        "{1,6,4}");
    check(
        "FindVertexCover({UndirectedEdge(1,2), UndirectedEdge(2,3), UndirectedEdge(3,4), UndirectedEdge(3,6)," //
            + " UndirectedEdge(3,7), UndirectedEdge(6,4), UndirectedEdge(4,7), UndirectedEdge(4,5), UndirectedEdge(5,1)})", //
        "{3,4,1}");

    // print: Graph must be undirected
    // TODO implement for directed graphs
    check(
        "FindVertexCover({DirectedEdge(2,1), DirectedEdge(1,3), DirectedEdge(3,6), DirectedEdge(6,1)," //
            + " DirectedEdge(4,6), DirectedEdge(1,5), DirectedEdge(5,4) })", //
        "FindVertexCover({2->1,1->3,3->6,6->1,4->6,1->5,5->4})");
  }

  public void testFindShortestPath() {
    check(
        "FindShortestPath(Graph({1 -> 2, 2 -> 4, 1 -> 3,  3 -> 2, 3 -> 4},{EdgeWeight->{3.0,1.0,1.0,1.0,3.0}}),1,4)", //
        "{1,3,2,4}");

    check("FindShortestPath({1 -> 2, 2 -> 3, 3 -> 1,  3 -> 4, 4 -> 5, 3 -> 5},1,4)", //
        "{1,2,3,4}");
  }

  public void testFindShortestTour() {
    check("FindShortestTour({{1,2},{2,3},{3,1}})", //
        "{Sqrt(2)+2*Sqrt(5),{1,3,2,1}}");

    check("FindShortestTour({GeoPosition({41, 20}), GeoPosition({5, 20}), GeoPosition({49, 32}), " //
        + "GeoPosition({53, 28}), GeoPosition({47, 29})})", //
        "{6852.025[mi],{1,2,5,3,4,1}}");
    check(
        "FindShortestTour({{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 1}, {2, 3}, {2, 5}, {3, 1}, {3, 2}," //
            + " {3, 4}, {3, 5}, {4, 1}, {4, 3}, {4, 5}, {5, 1}, {5, 2}, {5, 3}, {5, 4}})", //
        "{14+5*Sqrt(2),{1,6,9,13,16,17,18,19,14,10,7,11,15,12,8,5,4,3,2,1}}");
  }

  public void testFindSpanningTree() {
    // example from Wikipedia https://en.wikipedia.org/wiki/Minimum_spanning_tree

    check(
        "FindSpanningTree(Graph({a,b,c,d,e,f},{a<->b,a<->d,b<->c,b<->d,b<->e,c<->e,c<->f,d<->e,e<->f}," //
            + "{EdgeWeight->{1.0,3.0,6.0,5.0,1.0,5.0,2.0,1.0,4.0}}))", //
        "Graph({a,b,d,e,c,f},{a->b,d->e,c->f,b->e,e->f},{EdgeWeight->{1.0,1.0,2.0,1.0,4.0}})");

    check("g=Graph({1,2,3,4,5,6,7,8},\n"
        + "{UndirectedEdge(1,2),UndirectedEdge(1,3),UndirectedEdge(1,4),UndirectedEdge(3,4),UndirectedEdge(2,6),\n"
        + "UndirectedEdge(3,6),UndirectedEdge(5,3),UndirectedEdge(5,4),UndirectedEdge(5,6),UndirectedEdge(5,7),\n"
        + "UndirectedEdge(5,8),UndirectedEdge(6,7),UndirectedEdge(7,8),UndirectedEdge(4,8)});", //
        "");

    check("FindSpanningTree(g)", //
        "Graph({1,2,3,4,6,5,7,8},{1->2,1->3,1->4,2->6,5->3,5->7,5->8})");
  }

  public void testHamiltonianGraphQ() {
    check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1}))", //
        "True");
    check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}}))", //
        "True");

    check("HamiltonianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "True");
    check("HamiltonianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "False");
  }

  public void testHypercubeGraph() {
    check("HypercubeGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,0,1,0,0,0,1,0,0,0,0,0,0,0},\n" //
            + " {1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0},\n" + " {1,0,0,1,0,0,1,0,0,0,1,0,0,0,0,0},\n"
            + " {0,1,1,0,0,0,0,1,0,0,0,1,0,0,0,0},\n" + " {1,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0},\n"
            + " {0,1,0,0,1,0,0,1,0,0,0,0,0,1,0,0},\n" + " {0,0,1,0,1,0,0,1,0,0,0,0,0,0,1,0},\n"
            + " {0,0,0,1,0,1,1,0,0,0,0,0,0,0,0,1},\n" + " {1,0,0,0,0,0,0,0,0,1,1,0,1,0,0,0},\n"
            + " {0,1,0,0,0,0,0,0,1,0,0,1,0,1,0,0},\n" + " {0,0,1,0,0,0,0,0,1,0,0,1,0,0,1,0},\n"
            + " {0,0,0,1,0,0,0,0,0,1,1,0,0,0,0,1},\n" + " {0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0},\n"
            + " {0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1},\n" + " {0,0,0,0,0,0,1,0,0,0,1,0,1,0,0,1},\n"
            + " {0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0}}");
  }

  public void testIsomorphicGraphQ() {
    check(
        "IsomorphicGraphQ(Graph({1,2,3,4},{1<->2,1<->4,2<->3,3<->4}), Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4}))", //
        "True");

    check("g=Graph({a,b,c,d},{a<->b,a<->d,b<->c,c<->d})", //
        "Graph({a,b,c,d},{a<->b,a<->d,b<->c,c<->d})");
    check("h=Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4})", //
        "Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4})");
    check(" IsomorphicGraphQ(g, h)", //
        "True");
  }

  public void testGraphFullForm() {
    check(
        "Graph({1, 2, 3}, {UndirectedEdge(1, 2), UndirectedEdge(2, 3), UndirectedEdge(3, 1)}, {EdgeWeight -> {5, 4, 3}})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{5.0,4.0,3.0}})");
    check("Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})");
    check(
        "Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}, {EdgeWeight -> {2, 3, 4}})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})");
  }

  public void testGraph() {

    check("Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1})");
    check("Graph({1,2,3},{1<->2,2<->3,3<->1})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1})");

    check("Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1})", //
        "Graph({1,2,3},{1->2,2->3,3->1})");
    check("Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1})", //
        "Graph({1,2,3,4},{1->2,2->3,3->1,1->3,3->4,4->1})");

    check("Graph({1,2,3,4,5,6,7,8},\n"
        + "{UndirectedEdge(1,2),UndirectedEdge(1,3),UndirectedEdge(1,4),UndirectedEdge(3,4),UndirectedEdge(2,6),\n"
        + "UndirectedEdge(3,6),UndirectedEdge(5,3),UndirectedEdge(5,4),UndirectedEdge(5,6),UndirectedEdge(5,7),\n"
        + "UndirectedEdge(5,8),UndirectedEdge(6,7),UndirectedEdge(7,8),UndirectedEdge(4,8)})", //
        "Graph({1,2,3,4,5,6,7,8},{1<->2,1<->3,1<->4,3<->4,2<->6,3<->6,5<->3,5<->4,5<->6,5<->7,5<->8,6<->7,7<->8,4<->8})");
  }

  public void testGraphData() {
    check("GraphData(\"PappusGraph\")", //
        "Graph({1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}," //
            + "{1<->2,1<->3,1<->4,2<->5,2<->6,5<->7,5<->8,7<->9,7<->\n" + //
            "10,9<->3,9<->11,3<->12,4<->13,4<->14,6<->15,6<->16,8<->13,8<->17,10<->16,10<->18,11<->17,11<->14,12<->15,12<->18,15<->17,13<->18,16<->\n"
            + "14})");
  }

  public void testGraphCenter() {
    check(
        "GraphCenter(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + //
            "{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
        "{1,3}");

    check(
        "GraphCenter(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
        "{3}");

    check(
        "GraphCenter(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + //
            "{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
        "{1,3}");
    check(
        "GraphCenter({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
        "{1,3}");
  }

  public void testGraphDiameter() {
    check(
        "GraphDiameter(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
        "4");

    check(
        "GraphDiameter(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + //
            "{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
        "2.52");
    check(
        "GraphDiameter({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
        "2");
  }

  public void testGraphRadius() {
    check(
        "GraphRadius(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
        "2");

    check(
        "GraphRadius(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + //
            "{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
        "2.0");
    check(
        "GraphRadius({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
        "1");
  }

  // public void testGraphUnion() {
  // check(
  // "GraphUnion(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}), Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 ->
  // 2, 4 -> 1}) )", //
  // "{3}");
  // }

  public void testGraphPeriphery() {
    check(
        "GraphPeriphery(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
        "{1,4}");

    check(
        "GraphPeriphery(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + //
            "{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
        "{2,4}");
    check(
        "GraphPeriphery({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
        "{2,4}");

    check(
        "GraphPeriphery({UndirectedEdge(1, 2), UndirectedEdge(2, 3), UndirectedEdge(3, 1), UndirectedEdge(3, 4), UndirectedEdge(3, 4), UndirectedEdge(4, 5), UndirectedEdge(5, 3)})", //
        "{1,2,4,5}");
  }

  public void testGraphQ() {
    check("GraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "True");
    check("GraphQ( Sin(x) )", //
        "False");
    check("GraphQ( Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086}) )", //
        "True");
  }

  public void testPetersenGraph() {
    check("PetersenGraph()", //
        "Graph({1,2,3,4,5,6,7,8,9,10},{1<->3,1<->2,2<->6,3<->5,3<->4,4<->8,5<->7,5<->6,6<->10,7<->9,7<->8,8<->2,9<->1,9<->10,10<->4})");
  }

  public void testPlanarGraphQ() {
    check("PlanarGraphQ( PetersenGraph(5,2) )", //
        "False");
    check("PlanarGraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "True");
  }

  public void testLineGraph() {
    // todo
    // check(
    // "LineGraph(Graph({1 -> 2, 1 -> 3, 2 -> 4, 3 -> 4, 3 -> 5, 4 -> 6, 5 -> 6}) )", //
    // "");
    // ([(1 : 2), (1 : 3), (2 : 4), (3 : 4), (3 : 5), (4 : 6), (5 : 6)], [
    // ((1 : 2) : (2 : 4))={(1 : 2),(2 : 4)},
    // ((1 : 3) : (3 : 4))={(1 : 3),(3 : 4)},
    // ((1 : 3) : (3 : 5))={(1 : 3),(3 : 5)},
    // ((2 : 4) : (4 : 6))={(2 : 4),(4 : 6)},
    // ((3 : 4) : (4 : 6))={(3 : 4),(4 : 6)},
    // ((3 : 5) : (5 : 6))={(3 : 5),(5 : 6)}])

    // check(
    // "LineGraph(Graph({1 <-> 2,1 <-> 3,1 <-> 4, 2 <-> 3,2 <-> 4, 3 <->4}) )", //
    // "");
    // ([(1 : 2), (1 : 3), (1 : 4), (2 : 3), (2 : 4), (3 : 4)], [
    // ((1 : 2) : (1 : 3))=
    // {(1 : 2),(1 : 3)},
    // ((1 : 2) : (1 : 4))=
    // {(1 : 2),(1 : 4)},
    // ((1 : 3) : (1 : 4))=
    // {(1 : 3),(1 : 4)},
    // ((1 : 2) : (2 : 3))=
    // {(1 : 2),(2 : 3)},
    // ((1 : 2) : (2 : 4))=
    // {(1 : 2),(2 : 4)},
    // ((2 : 3) : (2 : 4))=
    // {(2 : 3),(2 : 4)},
    // ((1 : 3) : (2 : 3))=
    // {(1 : 3),(2 : 3)},
    // ((1 : 3) : (3 : 4))=
    // {(1 : 3),(3 : 4)},
    // ((2 : 3) : (3 : 4))=
    // {(2 : 3),(3 : 4)},
    // ((1 : 4) : (2 : 4))=
    // {(1 : 4),(2 : 4)},
    // ((1 : 4) : (3 : 4))=
    // {(1 : 4),(3 : 4)},
    // ((2 : 4) : (3 : 4))=
    // {(2 : 4),(3 : 4)}])

  }

  public void testRandomGraph() {
    // random result:

    // check(
    // "AdjacencyMatrix /@ RandomGraph({7,4}, 3) // Normal", //
    // "{\n" //
    // + "{{0,0,1,0,0,1,0},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {1,0,0,0,0,1,1},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {1,0,1,0,0,0,0},\n"
    // + " {0,0,1,0,0,0,0}},\n"
    // + "{{0,1,0,0,0,0,0},\n"
    // + " {1,0,0,0,0,0,0},\n"
    // + " {0,0,0,0,0,1,0},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {0,0,0,0,0,1,1},\n"
    // + " {0,0,1,0,1,0,0},\n"
    // + " {0,0,0,0,1,0,0}},\n"
    // + "{{0,0,1,0,0,0,1},\n"
    // + " {0,0,1,0,0,0,0},\n"
    // + " {1,1,0,0,0,0,0},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {0,0,0,0,0,0,1},\n"
    // + " {0,0,0,0,0,0,0},\n"
    // + " {1,0,0,0,1,0,0}}}");
  }

  public void testStarGraph() {
    check("StarGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},\n" //
            + " {1,0,0,0},\n" + " {1,0,0,0},\n" + " {1,0,0,0}}");
    check("StarGraph(6)", //
        "Graph({1,2,3,4,5,6},{2<->1,3<->1,4<->1,5<->1,6<->1})");
    check(
        "SparseArray(Automatic, {4, 4}, 0, {1, {{0, 3, 4, 5, 6}, {{2}, {3}, {4}, {1}, {1}, {1}}}, {1, 1, 1, 1, 1, 1}}) // Normal", //
        "{{0,1,1,1},\n" //
            + " {1,0,0,0},\n" + " {1,0,0,0},\n" + " {1,0,0,0}}");
  }

  public void testWeightedAdjacencyMatrix() {

    check(
        "WeightedAdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}, EdgeWeight->{5.061,2.282,5.086,1.707})) // Normal", //
        "{{0,5.061,5.086,0},\n" //
            + " {0,0,2.282,0},\n" + " {0,0,0,0},\n" + " {0,1.707,0,0}}");
    check("WeightedAdjacencyMatrix({1 -> 3, 2 -> 1}) // Normal", //
        "{{0,1,0},\n" //
            + " {0,0,0},\n" + " {1,0,0}}");
    check("wg=Graph({1<->2, 2<->3, 3<->1}, EdgeWeight->{5.061,2.282,5.086})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{5.061,2.282,5.086}})");

    check("WeightedAdjacencyMatrix(wg) // Normal", //
        "{{0,5.061,5.086},\n" //
            + " {5.061,0,2.282},\n" + " {5.086,2.282,0}}");

    check("wgd=Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086})", //
        "Graph({1,2,3},{1->2,2->3,3->1},{EdgeWeight->{5.061,2.282,5.086}})");

    check("WeightedAdjacencyMatrix(wgd) // Normal", //
        "{{0,5.061,0},\n" //
            + " {0,0,2.282},\n" + " {5.086,0,0}}");
  }

  public void testWeightedGraphQ() {
    check("WeightedGraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "False");
    check("WeightedGraphQ( Sin(x) ) ", //
        "False");
    check("WeightedGraphQ( Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086}) ) ", //
        "True");
  }

  public void testWheelGraph() {
    check("WheelGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},\n" //
            + " {1,0,1,1},\n" + " {1,1,0,1},\n" + " {1,1,1,0}}");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
