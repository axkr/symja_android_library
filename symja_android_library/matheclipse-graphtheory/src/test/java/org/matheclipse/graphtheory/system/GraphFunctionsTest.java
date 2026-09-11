package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for graphics functions */
public class GraphFunctionsTest extends AbstractTestCase {

  /**
   * <code>GraphPlot</code> keeps the Graphics options it is given: the WLJS notebook draws every
   * <code>Graph</code> as <code>GraphPlot(g, ImageSize->70, AspectRatio->1, ...)</code>.
   */
  /**
   * A directed <code>Graph3D</code> edge is <code>Arrow[{p1, p2}, setback]</code>: the WLJS notebook
   * reads the points straight from the arrow and drew nothing for <code>Arrow[Line[...]]</code>.
   */
  @Test
  public void testGraph3DArrowsHoldTheirPoints() {
    check("a=Cases(Graph3D({1->2,2->3,3->1}),_Arrow,Infinity);" //
        + "{Length(a), FreeQ(a,_Line), MatchQ(a,{Arrow({{_,_,_},{_,_,_}},_)..})}", //
        "{3,True,True}");
  }

  @Test
  public void testGraphPlotKeepsItsOptions() {
    check("p=GraphPlot(Graph({1->2,2->3,3->1}),ImageSize->70,AspectRatio->1);" //
        + "{Head(p), Cases(List@@p,(ImageSize->s_):>s), Cases(List@@p,(AspectRatio->a_):>a)}", //
        "{Graphics,{200},{1}}");
    // a directed edge ends at the edge of its target vertex, with Mathematica's Medium arrowheads
    check("p=GraphPlot(Graph({1->2}));{Cases(p,_Arrowheads,Infinity),"
        + "Cases(p,Arrow({_,e_}):>e,Infinity)==Cases(p,Disk(c_,_):>c,Infinity)[[{2}]]}", //
        "{{Arrowheads(Medium)},False}");
  }

  @Test
  public void testAdjacencyGraph() {
    check("AdjacencyGraph(SparseArray({{i_, j_} /; 0<Abs(i-j) <= 3 -> 1}, {6, 6}))// InputForm", //
        "Graph({1,2,3,4,5,6},{Null,SparseArray(Automatic,{6,6},0,{1,{{0,3,7,12,17,21,24},{{2},{3},{4},{1},{3},{4},{5},{1},{2},{4},{5},{6},{1},{2},{3},{5},{6},{2},{3},{4},{6},{3},{4},{5}}},Pattern})})");
    check("AdjacencyGraph({{0,1,1,0},{0,0,1,0},{0,0,0,0},{0,1,0,0}}) // InputForm", //
        "Graph({1,2,3,4},{Null,SparseArray(Automatic,{4,4},0,{1,{{0,2,3,3,4},{{2},{3},{3},{2}}},Pattern})})");

  }

  @Test
  public void testAdjacencyMatrix() {
    // order 1, 2, 3, 4
    check("AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) // Normal", //
        "{{0,1,1,0},{0,0,1,0},{0,0,0,0},{0,1,0,0}}");
    // order 4, 2, 1, 3 is like parsing order of the vertexes in the graph expression
    check("AdjacencyMatrix(Graph({4 -> 2, 1 -> 2, 2 -> 3, 1 -> 3})) // Normal", //
        "{{0,1,0,0},{0,0,0,1},{0,1,0,1},{0,0,0,0}}");
    // order d,b,c,a is like parsing order of the vertexes in the graph expression
    check("AdjacencyMatrix(Graph({d -> b, a -> b, b -> c, a -> c})) // Normal", //
        "{{0,1,0,0},{0,0,0,1},{0,1,0,1},{0,0,0,0}}");

    check(
        "AdjacencyMatrix(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))// Normal", //
        "{{0,1,1},{1,0,1},{1,1,0}}");
    check(
        "AdjacencyMatrix(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))// Normal", //
        "{{0,1,0},{0,0,1},{1,0,0}}");
  }

  @Test
  public void testBipartiteGraphQ() {
    check("BipartiteGraphQ(CompleteGraph({2,3}))", //
        "True");
    check("BipartiteGraphQ(WheelGraph(6))", //
        "False");
    check("BipartiteGraphQ(CycleGraph(8))", //
        "True");
    check("BipartiteGraphQ(GridGraph({3, 4}))", //
        "True");
  }

  @Test
  public void testClosenessCentrality() {
    check("ClosenessCentrality(Graph({1, 2, 3, 4, 5},{1<->2,1<->3,2<->3,3<->4,3<->5}))", //
        "{0.666667,0.666667,1.0,0.571429,0.571429}");
  }

  @Test
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

  /**
   * The graphs of the WLJS demo notebook: Mathematica's default look, and the options a
   * <code>Graph</code> keeps for drawing it.
   */
  @Test
  public void testGraphDrawingOptions() {
    // Mathematica's colours; the edges and the vertices each in a list of their own
    check("Cases(GraphPlot(Graph({1->2,2->3,3->1})),_Hue,Infinity)", //
        "{Hue(0.6,0.7,0.7),Hue(0.6,0.5,1.0)}");
    // VertexShapeFunction and VertexSize are kept by Graph
    check("Length(Cases(GraphPlot(Graph({1->2,2->3,3->1},VertexShapeFunction->\"Diamond\","
        + "VertexSize->Medium)),_Polygon,Infinity))", //
        "3");
    // an annotated vertex is the vertex itself, with a size and a style of its own
    check("g=Graph(Table(Annotation(v,{VertexSize->0.2+0.2*Mod(v,5),VertexStyle->Hue(v/15,1,1)}),"
        + "{v,0,14}),Table(v<->Mod(v+1,15),{v,0,14}));"
        + "{VertexCount(g),Length(Cases(GraphPlot(g),Hue(_,1,1),Infinity))}", //
        "{15,15}");
    // a list of rules is options too; a named GraphStyle draws labelled rectangles, and the graph
    // options do not reach Graphics
    check("p=GraphPlot(Graph({1,2,3},{1<->2,2<->3},{GraphStyle->\"DiagramGreen\"}));"
        + "{Length(Cases(p,_Rectangle,Infinity)),Length(Cases(p,_Text,Infinity)),"
        + "FreeQ(p,GraphStyle)}", //
        "{3,3,True}");
    // "GridEmbedding" with "Dimension" -> {columns, rows}
    check("p=GraphPlot(Graph({1,2,3,4,5,6},{1<->2,2<->3,4<->5,5<->6,1<->4},"
        + "GraphLayout->{\"VertexLayout\"->{\"GridEmbedding\",\"Dimension\"->{3,2}}}));"
        + "Length(Union(Cases(p,Disk({x_,y_},_):>y,Infinity)))", //
        "2");
    check("Cases(GraphPlot(HighlightGraph(PathGraph(Range(3)),{Style(2,Green)})),_RGBColor,"
        + "Infinity)", //
        "{RGBColor(0,1,0)}");
    check("{VertexCount(ButterflyGraph(3)),EdgeCount(ButterflyGraph(3))}", //
        "{32,48}");
    // Graph3D(vertices, edges) with annotated vertices
    check("p=Graph3D(Table(Annotation(v,{VertexStyle->Hue(v/15,1,1)}),{v,0,14}),"
        + "Table(v<->Mod(v+1,15),{v,0,14}));"
        + "{Head(p),Length(Cases(p,_Sphere,Infinity)),Length(Cases(p,Hue(_,1,1),Infinity))}", //
        "{Graphics3D,15,15}");
  }

  @Test
  public void testCompleteGraph() {
    check("CompleteGraph({7,3}) // AdjacencyMatrix // Normal", //
        "{{0,0,0,0,0,0,0,1,1,1},{0,0,0,0,0,0,0,1,1,1},{0,0,0,0,0,0,0,1,1,1},{0,0,0,0,0,0,\n"
            + "0,1,1,1},{0,0,0,0,0,0,0,1,1,1},{0,0,0,0,0,0,0,1,1,1},{0,0,0,0,0,0,0,1,1,1},{1,1,\n"
            + "1,1,1,1,1,0,0,0},{1,1,1,1,1,1,1,0,0,0},{1,1,1,1,1,1,1,0,0,0}}");

    check("CompleteGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},{1,0,1,1},{1,1,0,1},{1,1,1,0}}");
  }

  @Test
  public void testConnectedGraphQ() {
    check("ConnectedGraphQ(Graph({1,2,3,4},{1->2, 2->3, 3->4, 2->4}))", //
        "False");
    check("ConnectedGraphQ(Graph({1,2,3,4},{1<->2, 2<->3, 3<->4}))", //
        "True");
    check("ConnectedGraphQ(Graph({1,2,3,4},{1<->2, 3<->4}))", //
        "False");
  }

  @Test
  public void testCycleGraph() {
    check("CycleGraph(1) // AdjacencyMatrix // Normal", //
        "{{1}}");
    check("CycleGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,0,1},{1,0,1,0},{0,1,0,1},{1,0,1,0}}");
  }

  @Test
  public void testEdgeCount() {
    check("EdgeCount(CompleteGraph(4))", //
        "6");
  }

  @Test
  public void testEdgeList() {
    check("EdgeList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
        "{1->2,2->3,1->3,4->2}");
    check(
        "EdgeList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1<->2,2<->3,3<->1}");
    check("EdgeList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
  }

  @Test
  public void testEdgeRules() {
    check("EdgeRules(Graph({1 <-> 2, 2 <-> 3, 1 <-> 3, 4 <-> 2}))", //
        "{1->2,2->3,1->3,4->2}");
    check(
        "EdgeRules(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
    check("EdgeRules(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1->2,2->3,3->1}");
  }

  @Test
  public void testEigenvectorCentrality() {
    // TODO {0.16238, 0.136013, 0.276307, 0.23144, 0.193859}
    // check("EigenvectorCentrality(Graph({a -> b, b -> c, c -> d, d -> e, e -> c, e -> a}))", //
    // "{0.352395,0.295128,0.599617,0.502223,0.420653}");

  }

  @Test
  public void testEdgeQ() {
    check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 3)", //
        "True");
    check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 4)", //
        "False");
  }

  @Test
  public void testEulerianGraphQ() {
    check("EulerianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "False");
    check("EulerianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 }))", //
        "True");
    check("EulerianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7}))", //
        "False");
  }

  @Test
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

  @Test
  public void testFindEulerianCycle() {
    // a list of cycles holding the one that was found, as FindPostmanTour reports its tours
    check("FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))", //
        "{{4->1,1->2,2->3,3->4}}");
    check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "{{4->1,1->3,3->1,1->2,2->3,3->4}}");
    // vertex 7 can be entered but never left, so there is no cycle at all
    check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "{}");

    // an Eulerian cycle walks every edge exactly once, which a postman tour of the same graph
    // therefore matches in length
    check("EulerianGraphQ(CycleGraph(4))", "True");
    check("Length(FindEulerianCycle(CycleGraph(4))[[1]])===EdgeCount(CycleGraph(4))", "True");
    check("Length(FindEulerianCycle(CompleteGraph(5))[[1]])"
        + "===Length(FindPostmanTour(CompleteGraph(5))[[1]])", "True");
    // a graph that is not Eulerian has no cycle, though it still has a postman tour
    check("EulerianGraphQ(CompleteGraph(4))", "False");
    check("FindEulerianCycle(CompleteGraph(4))", "{}");
    check("Length(FindPostmanTour(CompleteGraph(4))[[1]])", "8");

    check("FindEulerianCycle(CycleGraph(4), 1)===FindEulerianCycle(CycleGraph(4))", "True");
    check("Head(FindEulerianCycle(CycleGraph(4), 0))", "FindEulerianCycle");
  }

  @Test
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

  @Test
  public void testFindHamiltonianCycle() {
    // a list of cycles holding the one that was found, as FindEulerianCycle reports its cycles
    check("FindHamiltonianCycle( {1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1} )", //
        "{{1->2,2->3,3->4,4->1}}");
    check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
        "{{1->2,2->3,3->4,4->1}}");
    // vertex 7 can be entered but never left
    check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
        "{}");

    // a cycle visits every vertex exactly once, so it has one edge per vertex
    check("HamiltonianGraphQ(CycleGraph(5))", "True");
    check("Length(FindHamiltonianCycle(CycleGraph(5))[[1]])===VertexCount(CycleGraph(5))", "True");
    // the Petersen graph is the classic graph with no Hamiltonian cycle
    check("HamiltonianGraphQ(PetersenGraph())", "False");
    check("FindHamiltonianCycle(PetersenGraph())", "{}");
    // and it really does visit every vertex
    check("Module({g=CompleteGraph(5),c}, c=FindHamiltonianCycle(g)[[1]];"
        + "Sort(Union(Flatten(List@@@c)))===Sort(VertexList(g)))", "True");

    check("FindHamiltonianCycle(CycleGraph(4), 1)===FindHamiltonianCycle(CycleGraph(4))", "True");
    check("Head(FindHamiltonianCycle(CycleGraph(4), 0))", "FindHamiltonianCycle");
  }

  // @Test
  // public void testFindMinimumCostFlow() {
  // // TODO
  // check("FindMinimumCostFlow(Graph({1,2,3,4},{1->2,2->3,3->1,3->4})," //
  // + "1,4)", //
  // " ");
  // check("FindMinimumCostFlow(Graph({1,2,3,4,5,6},{1->2,2->3,3->4,1->5,5->6,6->4,2->5,3->6})," //
  // + "{1,3,4,-3,-4,-1})", //
  // " ");
  // }

  @Test
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

    check(
        "FindVertexCover({DirectedEdge(2,1), DirectedEdge(1,3), DirectedEdge(3,6), DirectedEdge(6,1),DirectedEdge(4,6), DirectedEdge(1,5), DirectedEdge(5,4) })", //
        "{1,6,4}");
  }

  @Test
  public void testFindShortestPath() {
    check(
        "FindShortestPath(Graph({1 -> 2, 2 -> 4, 1 -> 3,  3 -> 2, 3 -> 4},{EdgeWeight->{3.0,1.0,1.0,1.0,3.0}}),1,4)", //
        "{1,3,2,4}");

    check("FindShortestPath({1 -> 2, 2 -> 3, 3 -> 1,  3 -> 4, 4 -> 5, 3 -> 5},1,4)", //
        "{1,2,3,4}");
  }

  @Test
  public void testFindShortestTour() {
    check("FindShortestTour({{1,2},{2,3},{3,1}})", //
        "{Sqrt(2)+2*Sqrt(5),{1,3,2,1}}");

    check("FindShortestTour({GeoPosition({41, 20}), GeoPosition({5, 20}), GeoPosition({49, 32}), " //
        + "GeoPosition({53, 28}), GeoPosition({47, 29})})", //
        "{Quantity(6852.025,\"Miles\"),{1,2,5,3,4,1}}");
    check(
        "FindShortestTour({{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 1}, {2, 3}, {2, 5}, {3, 1}, {3, 2}," //
            + " {3, 4}, {3, 5}, {4, 1}, {4, 3}, {4, 5}, {5, 1}, {5, 2}, {5, 3}, {5, 4}})", //
        "{14+5*Sqrt(2),{1,6,9,13,16,17,18,19,14,10,7,11,15,12,8,5,4,3,2,1}}");
  }

  @Test
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

  @Test
  public void testHamiltonianGraphQ() {
    check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1}))", //
        "True");
    check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}}))", //
        "True");

    check("HamiltonianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 }))", //
        "True");
    check("HamiltonianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7}))", //
        "False");
  }

  @Test
  public void testHypercubeGraph() {
    check("HypercubeGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,0,1,0,0,0,1,0,0,0,0,0,0,0},{1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0},{1,0,0,1,0,\n"
            + "0,1,0,0,0,1,0,0,0,0,0},{0,1,1,0,0,0,0,1,0,0,0,1,0,0,0,0},{1,0,0,0,0,1,1,0,0,0,0,\n"
            + "0,1,0,0,0},{0,1,0,0,1,0,0,1,0,0,0,0,0,1,0,0},{0,0,1,0,1,0,0,1,0,0,0,0,0,0,1,0},{\n"
            + "0,0,0,1,0,1,1,0,0,0,0,0,0,0,0,1},{1,0,0,0,0,0,0,0,0,1,1,0,1,0,0,0},{0,1,0,0,0,0,\n"
            + "0,0,1,0,0,1,0,1,0,0},{0,0,1,0,0,0,0,0,1,0,0,1,0,0,1,0},{0,0,0,1,0,0,0,0,0,1,1,0,\n"
            + "0,0,0,1},{0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0},{0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1},{0,\n"
            + "0,0,0,0,0,1,0,0,0,1,0,1,0,0,1},{0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0}}");
  }

  @Test
  public void testIncidenceMatrix() {
    check("IncidenceMatrix(Graph({1 -> 2, 2 -> 3})) // InputForm", //
        "SparseArray(Automatic,{3,2},0,{1,{{0,1,3,4},{{1},{1},{2},{2}}},{-1,1,-1,1}})");

    // Directed Graph
    check("IncidenceMatrix(Graph({1 -> 2, 2 -> 3})) // Normal", //
        "{{-1,0},{1,-1},{0,1}}");

    // Undirected Graph
    check("IncidenceMatrix(Graph({1 <-> 2, 2 <-> 3})) // Normal", //
        "{{1,0},{1,1},{0,1}}");

    // Directed Graph with isolated/extra vertices
    check("IncidenceMatrix(Graph({1, 2, 3, 4}, {1 -> 2, 1 -> 3, 2 -> 3, 4 -> 2})) // Normal", //
        "{{-1,-1,0,0},{1,0,-1,1},{0,1,1,0},{0,0,0,-1}}");

    // Undirected cycle
    check(
        "IncidenceMatrix(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1})) // Normal", //
        "{{1,0,1},{1,1,0},{0,1,1}}");

    // Directed cycle
    check(
        "IncidenceMatrix(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1})) // Normal", //
        "{{-1,0,1},{1,-1,0},{0,1,-1}}");

    // Undirected graph with a self-loop (self-loops get an incidence of 2)
    check("IncidenceMatrix(Graph({1, 2}, {1 <-> 2, 2 <-> 2})) // Normal", //
        "{{1,0},{1,2}}");

    // Directed graph with a self-loop (self-loops get an incidence of -2)
    check("IncidenceMatrix(Graph({1, 2}, {1 -> 2, 2 -> 2})) // Normal", //
        "{{-1,0},{1,-2}}");

    // Standard un-normalized output check (testing the SparseArray structure directly)
    check("IncidenceMatrix(Graph({1 -> 2, 2 -> 3})) // FullForm", //
        "SparseArray(Automatic, List(3, 2), 0, List(1, List(List(0, 1, 3, 4), List(List(1), List(1), List(2), List(2))), List(-1, 1, -1, 1)))");

  }

  @Test
  public void testIndexGraph() {
    check("IndexGraph({1 -> 3, 2 -> 1, 3 -> 6, 4 -> 6, 1 -> 5, 5 -> 4,  6 -> 1}, 10)", //
        "Graph({10,11,12,13,14,15},{10->11,12->10,11->13,14->13,10->15,15->14,13->10})");
  }

  @Test
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

  @Test
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

  @Test
  public void testGraphSparseArray() {
    check(
        "Graph({1,2,3,4,5,6}, {Null,SparseArray(Automatic, {6, 6}, 0,  {1, {{0, 3, 7, 12, 17, 21, 24}, {{2}, {3}, {4}, {1}, {3}, {4}, {5}, {1}, {2}, {4}, {5}, {6}, {1}, {2}, {3}, {5}, {6}, {2}, {3}, {4}, {6}, {3}, {4}, {5}}},Pattern})}) // InputForm", //
        "Graph({1,2,3,4,5,6},{Null,SparseArray(Automatic,{6,6},0,{1,{{0,3,7,12,17,21,24},{{2},{3},{4},{1},{3},{4},{5},{1},{2},{4},{5},{6},{1},{2},{3},{5},{6},{2},{3},{4},{6},{3},{4},{5}}},Pattern})})");
  }

  @Test
  public void testGraph() {

    check("Graph({1,2,3},{1<->2,2<->3,3<->1}) // InputForm", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1})");

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

  @Test
  public void testGraphData() {
    check("GraphData()", //
        "{{PappusGraph}}");
    check("GraphData(\"PappusGraph\")", //
        "Graph({1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}," //
            + "{1<->2,1<->3,1<->4,2<->5,2<->6,5<->7,5<->8,7<->9,7<->\n" + //
            "10,9<->3,9<->11,3<->12,4<->13,4<->14,6<->15,6<->16,8<->13,8<->17,10<->16,10<->18,11<->17,11<->14,12<->15,12<->18,15<->17,13<->18,16<->\n"
            + "14})");
  }

  @Test
  public void testGraphDistance() {
    // 3-argument signature: Distance between s and t
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1, 4)", //
        "3");

    // Distance to itself should be 0
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1, 1)", //
        "0");

    // Distance to an unreachable vertex should be Infinity
    check("GraphDistance({1 -> 2, 3 -> 4}, 1, 4)", //
        "Infinity");

    // 2-argument signature: Distance from s to all vertices
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1)", //
        "{0,1,2,3}");

    // Shortest path taking weights into account
    check("GraphDistance(Graph({1 <-> 2, 2 <-> 3, 1 <-> 3}, EdgeWeight -> {2.0, 3.0, 4.0}), 1, 3)", //
        "4.0");

    // Tests for the Method option integration
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1, 4, Method -> \"Dijkstra\")", //
        "3");
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1, 4, Method -> \"BellmanFord\")", //
        "3");
    check("GraphDistance({1 -> 2, 2 -> 3, 3 -> 4}, 1, 4, Method -> \"UnitWeight\")", //
        "3");
  }

  @Test
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

  @Test
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

  @Test
  public void testConnectedGraphComponents() {
    // TODO
    check("ConnectedGraphComponents(Graph(Table(i -> Mod(i^2, 7), {i, 10})))", //
        "{Graph({0},{}),Graph({1},{1->1}),Graph({2,4},{2->4,4->2}),Graph({3},{}),Graph({5},{}),Graph({6},{}),Graph({7},{}),Graph({8},{}),Graph({9},{}),Graph({10},{})}");
  }

  @Test
  public void testGraphComplement() {
    check("GraphComplement({1 -> 2, 1 -> 6, 2 -> 3, 3 -> 4, 4 -> 5, 5 -> 6})", //
        "Graph({1,2,6,3,4,5},{1->3,1->4,1->5,2->1,2->6,2->4,2->5,6->1,6->2,6->3,6->4,6->5,3->1,3->2,3->6,3->5,4->1,4->2,4->6,4->3,\n"
            + "5->1,5->2,5->3,5->4})");
  }

  @Test
  public void testGraphDifference() {
    check(
        "GraphDifference({1 -> 2, 2 -> 3, 3 -> 1, 4 -> 3, 2 -> 4}, {1 -> 2, 3 -> 2, 4 -> 3, 4 -> 1, 5->2})", //
        "Graph({1,2,3,4,5},{2->3,3->1,2->4})");
    check(
        "GraphDifference({1 -> 2, 2 -> 3, 3 -> 1, 4 -> 3, 2 -> 4, 2->5}, {1 -> 2, 3 -> 2, 4 -> 3, 4 -> 1, 5->2})", //
        "Graph({1,2,3,4,5},{2->3,3->1,2->4,2->5})");
    check(
        "GraphDifference({1 -> 2, 2 -> 3, 3 -> 1, 4 -> 3, 2 -> 4, 2->5}, {1 -> 2, 3 -> 2, 4 -> 3, 4 -> 1, 5->2, 2->5})", //
        "Graph({1,2,3,4,5},{2->3,3->1,2->4})");
    // TODO
    // check(
    // "GraphDifference({1 -> 2, 2 -> 3, 3 -> 1, 4 -> 3, 2 -> 4, 2->5}, {1 -> 2, 3 -> 2, 4 -> 3, 4
    // -> 1, 2<->5})", //
    // "Graph({1,2,3,4,5},{2->3,3->1,2->4})");
  }


  @Test
  public void testGraphDisjointUnion() {
    check(
        "GraphDisjointUnion(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}), Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 ->  2, 4 -> 1, 6->2}))", //
        "Graph({1,2,3,4,5,6,7,8,9},{1->2,2->3,1->3,4->2,5->6,6->7,5->7,8->6,8->5,9->6})");

  }

  @Test
  public void testGraphIntersection() {
    check(
        "GraphIntersection({1 -> 2, 2 -> 3, 3 -> 1, 4 -> 3, 2 -> 4}, {1 -> 2, 2 -> 3, 4 -> 3, 4 -> 1})", //
        "Graph({1,2,3,4},{1->2,2->3,4->3})");

    check(
        "GraphIntersection({1 -> 2, 3 -> 2, 3 -> 1, 4 -> 3, 2 -> 4}, {1 -> 2, 2 -> 3, 4 -> 3, 4 -> 1})", //
        "Graph({1,2,3,4},{1->2,4->3})");
    check("GraphIntersection({1 -> 2, 3 -> 2, 3 -> 1, 4 -> 3, 2 -> 4}, {  2 -> 3, 4 -> 3, 4 -> 1})", //
        "Graph({1,2,3,4},{4->3})");
    check(
        "g=Graph({1,2,3,4,5,6,7,8}, {1<->2,1<->4,1<->5,2<->3,2<->6,3<->4,3<->7,4<->8,5<->6,6<->7,7<->8,8<->5});", //
        "");
    check("IsomorphicGraphQ(g, GraphIntersection(g, g))", //
        "True");

  }

  @Test
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

  @Test
  public void testGraphUnion() {
    check(
        "GraphUnion(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}), Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 ->  2, 4 -> 1, 6->2}))", //
        "Graph({1,2,3,4,6},{1->2,2->3,1->3,4->2,4->1,6->2})");

  }

  @Test
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

  @Test
  public void testGraphPower() {
    check("GraphPower({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 5, 5->6}, 2)", //
        "Graph({1,2,3,4,5,6},{1->2,1->3,2->3,2->4,3->4,3->5,4->5,4->6,5->6})");
    check("GraphPower({1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4}, 2)// AdjacencyMatrix // Normal", //
        "{{0,1,1,0},{1,0,1,1},{1,1,0,1},{0,0,0,0}}");
    check("Table(GraphPower(CycleGraph(n),2), {n, 4, 6})", //
        "{Graph({1,2,3,4},{1<->2,1<->3,1<->4,2<->3,2<->4,3<->4})," //
            + "Graph({1,2,3,4,5},{1<->2,1<->3,1<->4,1<->5,2<->3,2<->4,2<->5,3<->4,3<->5,4<->5})," //
            + "Graph({1,2,3,4,5,6},{1<->2,1<->3,1<->5,1<->6,2<->3,2<->4,2<->6,3<->4,3<->5,4<->5,4<->6,5<->6})}");
  }

  @Test
  public void testGraphQ() {
    check("GraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "True");
    check("GraphQ( Sin(x) )", //
        "False");
    check("GraphQ( Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086}) )", //
        "True");
  }

  @Test
  public void testGridGraph() {
    check("GridGraph({3,4}) // AdjacencyMatrix // MatrixForm", //
        "{{0,1,0,1,0,0,0,0,0,0,0,0},\n" //
            + " {1,0,1,0,1,0,0,0,0,0,0,0},\n" //
            + " {0,1,0,0,0,1,0,0,0,0,0,0},\n" //
            + " {1,0,0,0,1,0,1,0,0,0,0,0},\n" //
            + " {0,1,0,1,0,1,0,1,0,0,0,0},\n" //
            + " {0,0,1,0,1,0,0,0,1,0,0,0},\n" //
            + " {0,0,0,1,0,0,0,1,0,1,0,0},\n" //
            + " {0,0,0,0,1,0,1,0,1,0,1,0},\n" //
            + " {0,0,0,0,0,1,0,1,0,0,0,1},\n" //
            + " {0,0,0,0,0,0,1,0,0,0,1,0},\n" //
            + " {0,0,0,0,0,0,0,1,0,1,0,1},\n" //
            + " {0,0,0,0,0,0,0,0,1,0,1,0}}");
  }

  @Test
  public void testPathGraphQ() {
    // TODO multi-graph should return false
    // check("PathGraphQ(Graph({1,2,3},{1->2, 2->3, 2->3}))", //
    // "False");
    check("PathGraphQ(Graph({1,2,3,4},{1->2, 2->3, 3->4}))", //
        "True");
    check("PathGraphQ(Graph({1,2,3,4},{1<->2, 2<->3, 3<->4}))", //
        "True");
    check("PathGraphQ(Graph({1,2,3,4},{1<->2, 3<->4}))", //
        "False");
  }

  @Test
  public void testPathGraph() {
    check("PathGraph({1,2,3,4}) // AdjacencyMatrix // Normal ", //
        "{{0,1,0,0},{1,0,1,0},{0,1,0,1},{0,0,1,0}}");

    check("PathGraph(Range(10))", //
        "Graph({1,2,3,4,5,6,7,8,9,10},{1<->2,2<->3,3<->4,4<->5,5<->6,6<->7,7<->8,8<->9,9<->10})");
  }

  @Test
  public void testPetersenGraph() {
    check("PetersenGraph()", //
        "Graph({1,2,3,4,5,6,7,8,9,10},{1<->3,1<->2,2<->6,3<->5,3<->4,4<->8,5<->7,5<->6,6<->10,7<->9,7<->8,8<->2,9<->1,9<->10,10<->4})");
  }

  @Test
  public void testPlanarGraphQ() {
    check("PlanarGraphQ(CycleGraph(4))", //
        "True");
    check("PlanarGraphQ(CompleteGraph(5))", //
        "False");
    check("PlanarGraphQ(CompleteGraph(4))", //
        "True");
    check("PlanarGraphQ(\"abc\")", //
        "False");
    check("PlanarGraphQ({})", //
        "False");
    check("PlanarGraphQ(Graph({}))", //
        "True");
    check("PlanarGraphQ( PetersenGraph(5,2) )", //
        "False");
    check("PlanarGraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "True");
  }

  @Test
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

  @Test
  public void testRandomGraph() {
    // random result:
    // check("RandomGraph({5,10})", //
    // "Graph({1,2,3,4,5},{5<->3,4<->2,5<->1,2<->3,5<->4,2<->5,2<->1,3<->4,3<->1,1<->4})");
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

  @Test
  public void testStarGraph() {
    check("StarGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},{1,0,0,0},{1,0,0,0},{1,0,0,0}}");
    check("StarGraph(6)", //
        "Graph({1,2,3,4,5,6},{2<->1,3<->1,4<->1,5<->1,6<->1})");
    check(
        "SparseArray(Automatic, {4, 4}, 0, {1, {{0, 3, 4, 5, 6}, {{2}, {3}, {4}, {1}, {1}, {1}}}, {1, 1, 1, 1, 1, 1}}) // Normal", //
        "{{0,1,1,1},{1,0,0,0},{1,0,0,0},{1,0,0,0}}");
  }

  @Test
  public void testVertexCount() {
    check("VertexCount(CompleteGraph(4))", //
        "4");
  }

  @Test
  public void testWeightedAdjacencyMatrix() {

    check(
        "WeightedAdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}, EdgeWeight->{5.061,2.282,5.086,1.707})) // Normal", //
        "{{0,5.061,5.086,0},{0,0,2.282,0},{0,0,0,0},{0,1.707,0,0}}");
    check("WeightedAdjacencyMatrix({1 -> 3, 2 -> 1}) // Normal", //
        "{{0,1,0},{0,0,0},{1,0,0}}");
    check("wg=Graph({1<->2, 2<->3, 3<->1}, EdgeWeight->{5.061,2.282,5.086})", //
        "Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{5.061,2.282,5.086}})");

    check("WeightedAdjacencyMatrix(wg) // Normal", //
        "{{0,5.061,5.086},{5.061,0,2.282},{5.086,2.282,0}}");

    check("wgd=Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086})", //
        "Graph({1,2,3},{1->2,2->3,3->1},{EdgeWeight->{5.061,2.282,5.086}})");

    check("WeightedAdjacencyMatrix(wgd) // Normal", //
        "{{0,5.061,0},{0,0,2.282},{5.086,0,0}}");
  }

  @Test
  public void testWeightedGraphQ() {
    check("WeightedGraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
        "False");
    check("WeightedGraphQ( Sin(x) ) ", //
        "False");
    check("WeightedGraphQ( Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086}) ) ", //
        "True");
  }

  @Test
  public void testWheelGraph() {
    check("WheelGraph(1)", //
        "Graph({1},{})");
    check("WheelGraph(2)", //
        "Graph({1,2},{1<->2,2<->2})");
    check("WheelGraph(3)", //
        "Graph({1,2,3},{1<->2,1<->3,2<->3})");
    check("WheelGraph(4) // AdjacencyMatrix // Normal", //
        "{{0,1,1,1},{1,0,1,1},{1,1,0,1},{1,1,1,0}}");
  }

  @Test
  public void testGraphQTruthTable() {
    check("GraphQ(Graph({1,2},{1->2}))", "True");
    check("GraphQ(Graph({1,2},{1<->2}))", "True");
    check("GraphQ(Graph({1,2,3},{1->2,2->3}))", "True");
    check("GraphQ(5)", "False");
    check("GraphQ(foo)", "False");
  }

  // TODO
  // @Test
  // public void testGraphRejections() {
  // check("GraphQ(Graph({1},{1->1}))", "False");
  // check("GraphQ(Graph({1,2},{1->2,1->2}))", "False");
  // check("GraphQ(Graph({1,2},{1<->2,2<->1}))", "False");
  // check("GraphQ(Graph({1,2},{DirectedEdge(1,2,x)}))", "False");
  // check("GraphQ(Graph({1,2},{1->3}))", "False");
  // check("GraphQ(Graph({1,2},{1->2,2->1}))", "True");
  // }

  @Test
  public void testQueryBuiltins() {
    String g = "Graph({1,2,3,4},{1->2,2->3,3->4,4->1})";
    check("VertexList(" + g + ")", "{1,2,3,4}");
    check("VertexCount(" + g + ")", "4");
    check("EdgeCount(" + g + ")", "4");
    check("VertexDegree(" + g + ", 1)", "2");
    check("VertexInDegree(" + g + ", 1)", "1");
    check("VertexOutDegree(" + g + ", 1)", "1");
    check("VertexDegree(" + g + ")", "{2,2,2,2}");
    check("AdjacencyList(" + g + ", 1)", "{2}");
    check("AdjacencyList(" + g + ")", "{{2},{3},{4},{1}}");
    check("DirectedGraphQ(" + g + ")", "True");
  }

  @Test
  public void testQueryUndirected() {
    String g = "Graph({1,2,3},{1<->2,2<->3})";
    check("VertexDegree(" + g + ")", "{1,2,1}");
    check("AdjacencyList(" + g + ", 2)", "{1,3}");
    check("DirectedGraphQ(" + g + ")", "False");
    check("VertexInDegree(" + g + ", 2)", "2");
    check("VertexOutDegree(" + g + ", 2)", "2");
  }

  @Test
  public void testGraphMatrixViews() {
    String dg = "Graph({1,2,3,4},{1->2,2->3,3->4,4->1})";
    check("Tr(AdjacencyMatrix(" + dg + "))", "0");
    check("Det(AdjacencyMatrix(" + dg + "))", "-1");
    check("EdgeList(AdjacencyGraph(AdjacencyMatrix(" + dg + ")))", //
        "{1->2,2->3,3->4,4->1}");
    check("EdgeList(AdjacencyGraph(AdjacencyMatrix(Graph({1,2,3},{1<->2,2<->3}))))",
        "{1<->2,2<->3}");
    check("IncidenceMatrix(Graph({1,2,3},{1<->2,2<->3})) // Normal", "{{1,0},{1,1},{0,1}}");
  }

  @Test
  public void testGenerators() {
    check("VertexCount(CompleteGraph(5))", "5");
    check("DirectedGraphQ(CompleteGraph(5))", "False");
    check("VertexDegree(CompleteGraph(5))", "{4,4,4,4,4}");

    check("EdgeCount(CycleGraph(5))", "5");
    check("VertexDegree(CycleGraph(5))", "{2,2,2,2,2}");
    check("EdgeList(CycleGraph(4))", "{1<->2,2<->3,3<->4,4<->1}");

    check("EdgeCount(PathGraph({a,b,c,d}))", "3");
    check("VertexDegree(PathGraph({a,b,c,d,e}))", "{1,2,2,2,1}");
    check("EdgeList(PathGraph({a,b,c}))", "{a<->b,b<->c}");
  }

  // TODO
  // @Test
  // public void testGraphConnectivityAndComponents() {
  // check("ConnectedComponents(Graph({1,2,3,4},{1->2,2->3,3->4,4->1}))", "{{1,2,3,4}}");
  // check("ConnectedComponents(Graph({1,2,3,4},{1->2,3->4}))", "{{1,2},{3,4}}");
  // check("WeaklyConnectedComponents(Graph({1,2,3},{1->2,2->3}))", "{{1,2,3}}");
  // check("StronglyConnectedComponents(Graph({1,2,3},{1->2,2->3}))", "{{1},{2},{3}}");
  // check("StronglyConnectedComponents(Graph({1,2,3},{1->2,2->3,3->1}))", "{{1,2,3}}");
  //
  // check("VertexConnectivity(PathGraph(4))", "1");
  // check("VertexConnectivity(CycleGraph(5))", "2");
  // check("VertexConnectivity(CompleteGraph(4))", "3");
  // check("VertexConnectivity(Graph({1,2,3,4},{1<->2,3<->4}))", "0");
  // }

  @Test
  public void testAcyclicGraph() {
    check("TopologicalSort(Graph({1,2,3},{1->2,2->3,1->3}))", "{1,2,3}");
    check("AcyclicGraphQ(Graph({1,2,3},{1->2,2->3,1->3}))", //
        "True");
    check("TopologicalSort(Graph({1,2,3},{1->2,2->3,3->1}))", "$Failed");
    check("AcyclicGraphQ(Graph({1,2,3},{1->2,2->3,3->1}))", "False");
    check("AcyclicGraphQ(Graph({1,2},{1->2,2->1}))", "False");
    check("AcyclicGraphQ(PathGraph({a,b,c,d}))", //
        "True");
    check("TopologicalSort(PathGraph({a,b,c,d}))", //
        "$Failed");
    check("AcyclicGraphQ(Graph({1,2,3,4,5},{1<->2,2<->3,4<->5}))", "True");
    check("AcyclicGraphQ(CycleGraph(4))", "False");

  }

  @Test
  public void testGraphComplementAdvanced() {
    // check("EdgeCount(GraphComplement(Graph({1,2,3},{1->2})))", //
    // "5");

    check("GraphComplement(Graph({1,2,3},{})) // InputForm", //
        "Graph({1,2,3},{1->2,1->3,2->1,2->3,3->1,3->2})");

    check("EdgeCount(GraphComplement(Graph({1,2,3},{})))", //
        "3");
    check("EdgeCount(GraphComplement(CompleteGraph(4)))", //
        "0");
    check("GraphComplement(CycleGraph(4)) // InputForm", //
        "Graph({1,2,3,4},{1<->3,2<->4})");
    check("EdgeCount(GraphComplement(CycleGraph(4)))", //
        "2");
    check("EdgeCount(GraphComplement(GraphComplement(CycleGraph(5))))", //
        "5");
    check("GraphComplement(Graph({1,2,3},{1->2}))", //
        "Graph({1,2,3},{1->3,2->1,2->3,3->1,3->2})");
    // TODO
    // check("EdgeCount(GraphComplement(Graph({1,2,3},{1->2})))", //
    // "5");
    check("DirectedGraphQ(GraphComplement(Graph({1,2,3},{1->2})))", //
        "True");
  }

  @Test
  public void testKirchhoffMatrix() {
    check("k=KirchhoffMatrix(PathGraph( {a,b,c}));", //
        "");
    check("k // InputForm", //
        "SparseArray(Automatic,{3,3},0,{1,{{0,2,5,7},{{1},{2},{1},{2},{3},{2},{3}}},{1,-1,-1,2,-1,-1,1}})");
    check("k // MatrixForm", //
        "{{1,-1,0},\n"//
            + " {-1,2,-1},\n"//
            + " {0,-1,1}}");
    check("k=KirchhoffMatrix(CycleGraph(3));", //
        "");

    check("k  // InputForm", //
        "SparseArray(Automatic,{3,3},0,{1,{{0,3,6,9},{{1},{2},{3},{1},{2},{3},{1},{2},{3}}},{2,-1,-1,-1,2,-1,-1,-1,2}})");
    check("k // MatrixForm", //
        "{{2,-1,-1},\n"//
            + " {-1,2,-1},\n" //
            + " {-1,-1,2}}");

    check("Total(KirchhoffMatrix(CycleGraph(4)))", //
        "{0,0,0,0}");
    check("Tr(KirchhoffMatrix(CycleGraph(4)))", //
        "8");
  }

  // TODO
  // @Test
  // public void testEdgeConnectivity() {
  // check("EdgeConnectivity(CompleteGraph(4))", "3");
  // check("EdgeConnectivity(CycleGraph(5))", "2");
  // check("EdgeConnectivity(PathGraph(4))", "1");
  // check("EdgeConnectivity(Graph({0,1,2,3},{0<->1,0<->2,0<->3}))", "1");
  // check("EdgeConnectivity(Graph({1,2,3},{1<->2}))", "0");
  // check("EdgeConnectivity(Graph({1,2,3},{1->2,2->3,3->1}))", "1");
  // check("EdgeConnectivity(Graph({1,2,3},{1->2,2->3}))", "0");
  // }

  // TODO
  // @Test
  // public void testLineGraphAdvanced() {
  // check("VertexCount(LineGraph(PathGraph(3)))", "2");
  // check("EdgeCount(LineGraph(PathGraph(3)))", "1");
  // check("VertexList(LineGraph(PathGraph(3)))", "{1<->2,2<->3}");
  // check("EdgeCount(LineGraph(CycleGraph(3)))", "3");
  // check("EdgeCount(LineGraph(CycleGraph(5)))", "5");
  // check("VertexCount(LineGraph(CompleteGraph(4)))", "6");
  // check("EdgeCount(LineGraph(CompleteGraph(4)))", "12");
  // check("EdgeCount(LineGraph(Graph({1,2,3},{1->2,2->3})))", "1");
  // check("DirectedGraphQ(LineGraph(Graph({1,2,3},{1->2,2->3})))", "True");
  // }

  // TODO
  // @Test
  // public void testTransitiveClosureAndReduction() {
  // check("EdgeList(TransitiveClosure(Graph({1,2,3},{1->2,2->3})))", "{1->2,1->3,2->3}");
  // check("DirectedGraphQ(TransitiveClosure(Graph({1,2,3},{1->2,2->3})))", "True");
  // check("EdgeCount(TransitiveClosure(Graph({1,2,3},{1->2,2->3,3->1})))", "6");
  // check("EdgeList(TransitiveClosure(PathGraph(3)))", "{1<->2,1<->3,2<->3}");
  // check("EdgeCount(TransitiveClosure(CycleGraph(4)))", "6");
  //
  // check("EdgeList(TransitiveReductionGraph(Graph({1,2,3},{1->2,2->3,1->3})))", "{1->2,2->3}");
  // check("EdgeCount(TransitiveReductionGraph(Graph({1,2,3},{1->2,2->3})))", "2");
  // check("EdgeCount(TransitiveReductionGraph(Graph({1,2,3,4},{1->2,2->3,3->4,1->4})))", "3");
  // }

  @Test
  public void testClusteringCoefficients() {
    // Teste Clustering-Koeffizienten (Lokal, Global, Mean)
    check("LocalClusteringCoefficient(CompleteGraph(4))", "{1,1,1,1}");
    check("LocalClusteringCoefficient(CycleGraph(5))", "{0,0,0,0,0}");
    check("LocalClusteringCoefficient(Graph({1,2,3,4},{1<->2,2<->3,3<->1,3<->4}))", "{1,1,1/3,0}");

    check("GlobalClusteringCoefficient(CompleteGraph(4))", "1");
    check("GlobalClusteringCoefficient(CycleGraph(5))", "0");
    check("GlobalClusteringCoefficient(Graph({1,2,3,4},{1<->2,2<->3,3<->1,3<->4}))", "3/5");

    check("MeanClusteringCoefficient(CompleteGraph(4))", "1");
    check("MeanClusteringCoefficient(Graph({1,2,3,4},{1<->2,2<->3,3<->1,3<->4}))", "7/12");
  }

  @Test
  public void testGraphColoring() {
    check("ChromaticPolynomial(Graph({1,2,3},{}), 2)", "8");
    check("ChromaticPolynomial(CompleteGraph(3), 3)", "6");
    check("ChromaticPolynomial(CompleteGraph(3), 2)", "0");
    check("ChromaticPolynomial(CycleGraph(4), 3)", "18");
    check("ChromaticPolynomial(CycleGraph(5), 2)", "0");

    // VertexChromaticNumber is the new name; Combinatorica's ChromaticNumber is the older Symja
    // name for the same function
    check("VertexChromaticNumber(Graph({1,2,3},{}))", "1");
    check("VertexChromaticNumber(CompleteGraph(4))", "4");
    check("VertexChromaticNumber(CycleGraph(4))", "2");
    check("VertexChromaticNumber(CycleGraph(5))", "3");
    check("VertexChromaticNumber(WheelGraph(6))", "4");
    check("VertexChromaticNumber({1<->2,2<->3,3<->1})", "3");
    check("VertexChromaticNumber(Graph({},{}))", "0");

    // ChromaticNumber was the older Symja name and is gone from the symbol table, so the parser no
    // longer knows it and lowercases it the way it does any other user symbol - which is what makes
    // this row proof of the removal rather than merely of a missing evaluator
    check("Head(ChromaticNumber(CycleGraph(4)))", "chromaticnumber");

    // self loops are ignored, so this agrees with FindVertexColoring
    check("VertexChromaticNumber(Graph({1,2},{1<->1}))", "1");
    check("VertexChromaticNumber(CycleGraph(5))===Max(FindVertexColoring(CycleGraph(5)))", "True");

    check("FindVertexColoring(CompleteGraph(3))", "{1,2,3}");
    check("FindVertexColoring(CycleGraph(4))", "{1,2,1,2}");

    // a one vertex graph used to reach BrownBacktrackColoring, which indexes past the end of its
    // own arrays for n == 1
    check("FindVertexColoring(Graph({1},{}))", "{1}");
    check("FindVertexColoring(Graph({},{}))", "{}");
    check("VertexChromaticNumber(Graph({1},{}))", "1");

    // a bare list of edges is accepted, like FindVertexCover and FindSpanningTree
    check("FindVertexColoring({1<->2,2<->3})", "{1,2,1}");

    // K(2,2): a greedy pass on an unlucky vertex order says 3, a minimal one says 2
    check("Max(FindVertexColoring(Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4})))", "2");
    check("Max(FindVertexColoring(CycleGraph(5)))", "3");
    check("Max(FindVertexColoring(PathGraph({1,2,3,4})))", "2");
    check("Max(FindVertexColoring(PetersenGraph()))", "3");
    // the clique lower bound meets the DSATUR upper bound here, so this must not search at all
    check("Max(FindVertexColoring(CompleteGraph(64)))", "64");
    check("VertexChromaticNumber(CompleteGraph(64))", "64");

    // an edge constrains both endpoints whichever way it points, and the minimum is taken over the
    // whole graph rather than per component
    check("Max(FindVertexColoring(Graph({1,2},{1->2})))", "2");
    check("Max(FindVertexColoring(Graph({1,2,3,4},{1<->2,3<->4})))", "2");

    // self loops are ignored, matching Mathematica: FindVertexColoring[Graph[{1,2},{1<->1}]]
    // answers {1,1} there rather than refusing
    check("FindVertexColoring(Graph({1,2},{1<->1}))", "{1,1}");
    check("FindVertexColoring(Graph({1,2},{1<->1,1<->2}))", "{1,2}");

    // the search is deterministic
    check("FindVertexColoring(CycleGraph(5))===FindVertexColoring(CycleGraph(5))", "True");
    check("Head(FindVertexColoring(5))", "FindVertexColoring");
  }

  @Test
  public void testEdgeChromaticNumber() {
    // the chromatic index is the vertex chromatic number of the line graph. Vizing pins it to the
    // maximum degree or one more, and Koenig makes it exactly the maximum degree for a bipartite
    // graph.
    check("EdgeChromaticNumber(CycleGraph(4))", "2");
    check("EdgeChromaticNumber(CycleGraph(5))", "3");
    check("EdgeChromaticNumber(CycleGraph(6))", "2");
    check("EdgeChromaticNumber(PathGraph({1,2,3,4}))", "2");

    // a complete graph on n vertices needs n colors for odd n and n-1 for even n
    check("EdgeChromaticNumber(CompleteGraph(3))", "3");
    check("EdgeChromaticNumber(CompleteGraph(4))", "3");
    check("EdgeChromaticNumber(CompleteGraph(5))", "5");
    check("EdgeChromaticNumber(CompleteGraph(6))", "5");
    check("EdgeChromaticNumber(CompleteGraph(7))", "7");

    // class 2 graphs, where the answer is the maximum degree plus one
    check("EdgeChromaticNumber(PetersenGraph())", "4");
    // a wheel on n vertices needs n-1
    check("EdgeChromaticNumber(WheelGraph(6))", "5");

    // bipartite graphs need exactly their maximum degree
    check("EdgeChromaticNumber(StarGraph(5))", "4");
    check("EdgeChromaticNumber(GridGraph({3,3}))", "4");
    check("EdgeChromaticNumber(HypercubeGraph(3))", "3");

    check("EdgeChromaticNumber({1<->2,2<->3})", "2");
    check("EdgeChromaticNumber(Graph({1,2,3},{}))", "0");
    check("EdgeChromaticNumber(Graph({},{}))", "0");
    // a self loop is an edge like any other here: it conflicts with the other edges at its vertex
    // but not with itself
    check("EdgeChromaticNumber(Graph({1,2},{1<->1}))", "1");
    check("EdgeChromaticNumber(Graph({1,2},{1<->1,1<->2}))", "2");
    // two opposite directed edges are two edges sharing both endpoints, so they cannot share a
    // color - the one case that leaves the range Vizing guarantees
    check("EdgeChromaticNumber(Graph({1,2},{1->2,2->1}))", "2");
    check("Head(EdgeChromaticNumber(5))", "EdgeChromaticNumber");
  }

  @Test
  public void testFindEdgeColoring() {
    check("FindEdgeColoring(CycleGraph(4))", "{1,2,1,2}");
    check("FindEdgeColoring(CycleGraph(5))", "{1,2,1,2,3}");
    check("FindEdgeColoring(PathGraph({1,2,3,4}))", "{1,2,1}");
    check("FindEdgeColoring({1<->2,2<->3})", "{1,2}");
    check("FindEdgeColoring(Graph({1,2,3},{}))", "{}");

    // the number of colors is the chromatic index
    check("Max(FindEdgeColoring(CompleteGraph(4)))", "3");
    check("Max(FindEdgeColoring(CompleteGraph(5)))", "5");
    check("Max(FindEdgeColoring(PetersenGraph()))", "4");
    check("Max(FindEdgeColoring(WheelGraph(6)))===EdgeChromaticNumber(WheelGraph(6))", "True");

    // one color per edge, in EdgeList order. The properness check below indexes the coloring by
    // edge position, so it only holds if that order is the reported one.
    check("Length(FindEdgeColoring(PetersenGraph()))===EdgeCount(PetersenGraph())", "True");
    check(
        "Module({g=PetersenGraph(),c,e}, c=FindEdgeColoring(g); e=EdgeList(g);"
            + "And@@Flatten(Table(If(Length(Intersection({e[[i]][[1]],e[[i]][[2]]},"
            + "{e[[j]][[1]],e[[j]][[2]]}))>0, c[[i]]=!=c[[j]], True),"
            + "{i,1,Length(e)},{j,i+1,Length(e)})))", //
        "True");

    // FindEdgeColoring(g, l) uses the colors 1, 2, ..., l
    check("FindEdgeColoring(CycleGraph(4), 2)", "{1,2,1,2}");
    check("Head(FindEdgeColoring(CycleGraph(5), 2))", "FindEdgeColoring");
    check("Head(FindEdgeColoring(CompleteGraph(5), 4))", "FindEdgeColoring");

    // FindEdgeColoring(g, {c1, c2, ...}) uses the given colors
    check("FindEdgeColoring(CycleGraph(4), {a,b})", "{a,b,a,b}");
    check("FindEdgeColoring(CompleteGraph(3), {a,b,c})", "{a,b,c}");
    check("Head(FindEdgeColoring(CompleteGraph(3), {a,b}))", "FindEdgeColoring");

    // a self loop keeps its place in the result, so the list stays aligned with EdgeList
    check("FindEdgeColoring(Graph({1,2},{1<->1}))", "{1}");
    check("FindEdgeColoring(Graph({1,2},{1<->1,1<->2}))", "{1,2}");

    check("Head(FindEdgeColoring(5))", "FindEdgeColoring");
    check("Head(FindEdgeColoring(CycleGraph(4), -1))", "FindEdgeColoring");
  }

  @Test
  public void testPlanarFaceList() {
    // a cycle bounds two faces, both walking the same vertices - one each way round
    check("Length(PlanarFaceList(CycleGraph(4)))", "2");
    check("Union(Map(Sort,PlanarFaceList(CycleGraph(4))))", "{{1,2,3,4}}");

    // the four triangles of the tetrahedron. Checked as a set of sorted boundaries, because which
    // vertex a boundary walk starts at and which way it runs are properties of the embedding.
    check("Union(Map(Sort,PlanarFaceList(CompleteGraph(4))))", //
        "{{1,2,3},{1,2,4},{1,3,4},{2,3,4}}");

    // the cube: six faces, four vertices each
    check("Length(PlanarFaceList(HypercubeGraph(3)))", "6");
    check("Union(Map(Length,PlanarFaceList(HypercubeGraph(3))))", "{4}");

    // Euler's formula, and agreement with the face count FindPlanarColoring colors
    check("Length(PlanarFaceList(HypercubeGraph(3)))"
        + "===EdgeCount(HypercubeGraph(3))-VertexCount(HypercubeGraph(3))+2", "True");
    check("Length(PlanarFaceList(GridGraph({3,3})))"
        + "===Length(FindPlanarColoring(GridGraph({3,3})))", "True");

    // a bridge is walked from both sides, so a boundary is not always a simple cycle
    check("PlanarFaceList(PathGraph({1,2,3}))", "{{1,2,3,2}}");
    // without edges nothing bounds a face
    check("PlanarFaceList(Graph({1,2,3},{}))", "{}");

    check("Head(PlanarFaceList(CompleteGraph(5)))", "PlanarFaceList");
    check("Head(PlanarFaceList(PetersenGraph()))", "PlanarFaceList");
    check("Head(PlanarFaceList(5))", "PlanarFaceList");

    // the outer face comes first, so IncludeOuterFace->False drops the head of the list
    check("Options(PlanarFaceList)", "{IncludeOuterFace->True}");
    check("PlanarFaceList(GridGraph({3,3}))[[1]]", "{2,1,4,7,8,9,6,3}");
    check("PlanarFaceList(GridGraph({3,3}), IncludeOuterFace->False)"
        + "===Rest(PlanarFaceList(GridGraph({3,3})))", "True");
    check("Union(Map(Length,PlanarFaceList(GridGraph({3,3}), IncludeOuterFace->False)))", "{4}");
    check("PlanarFaceList(WheelGraph(5))[[1]]", "{2,1,4,3}");
    // a tree has nothing but the outer face
    check("PlanarFaceList(PathGraph({1,2,3}), IncludeOuterFace->False)", "{}");
  }

  @Test
  public void testFindPostmanTour() {
    // an Eulerian graph walks each edge exactly once
    check("EulerianGraphQ(CycleGraph(4))", "True");
    check("FindPostmanTour(CycleGraph(4))", "{{4->1,1->2,2->3,3->4}}");
    check("Length(FindPostmanTour(CompleteGraph(5))[[1]])===EdgeCount(CompleteGraph(5))", "True");
    check("Length(FindPostmanTour(CycleGraph(5))[[1]])===EdgeCount(CycleGraph(5))", "True");

    // otherwise some edges are walked twice. A path repeats both of its edges, a star repeats all
    // four, and K4 has four odd vertices so exactly two edges are repeated.
    check("FindPostmanTour(PathGraph({1,2,3}))", "{{3->2,2->1,1->2,2->3}}");
    check("Length(FindPostmanTour(StarGraph(5))[[1]])", "8");
    check("EulerianGraphQ(CompleteGraph(4))", "False");
    check("Length(FindPostmanTour(CompleteGraph(4))[[1]])", "8");
    check("EdgeCount(CompleteGraph(4))", "6");
    // the Petersen graph is 3-regular, so all ten vertices are odd and five edges are repeated
    check("Length(FindPostmanTour(PetersenGraph())[[1]])" + "===EdgeCount(PetersenGraph())+5",
        "True");

    // and the tour really does traverse every edge
    check(
        "Module({g=CompleteGraph(4),t}, t=FindPostmanTour(g)[[1]];"
            + "Complement(EdgeList(g), Union(Join(t, Reverse/@t)/.DirectedEdge->UndirectedEdge)))",
        "{}");
    check(
        "Module({g=PetersenGraph(),t}, t=FindPostmanTour(g)[[1]];"
            + "Complement(EdgeList(g), Union(Join(t, Reverse/@t)/.DirectedEdge->UndirectedEdge)))",
        "{}");

    // a vertex carrying no edge is nothing for a tour to cover, but edges in two components are
    check("FindPostmanTour(Graph({1,2,3},{1<->2}))", "{{2->1,1->2}}");
    check("FindPostmanTour(Graph({1,2,3,4},{1<->2,3<->4}))", "{}");
    check("FindPostmanTour(Graph({1,2,3},{}))", "{}");
    check("FindPostmanTour(Graph({},{}))", "{}");

    check("FindPostmanTour(CycleGraph(4), 1)===FindPostmanTour(CycleGraph(4))", "True");
    check("Head(FindPostmanTour(CycleGraph(4), 0))", "FindPostmanTour");
    check("Head(FindPostmanTour(5))", "FindPostmanTour");
  }

  @Test
  public void testFindEdgeCover() {
    check("FindEdgeCover(CycleGraph(4))", "{1<->2,3<->4}");
    check("FindEdgeCover(PathGraph({1,2,3}))", "{1<->2,2<->3}");
    check("FindEdgeCover(PathGraph({1,2,3,4}))", "{1<->2,3<->4}");
    check("FindEdgeCover(StarGraph(5))", "{2<->1,3<->1,4<->1,5<->1}");
    check("FindEdgeCover(CompleteGraph(4))", "{1<->2,3<->4}");
    check("FindEdgeCover({1<->2,2<->3})", "{1<->2,2<->3}");

    // a vertex with no incident edge cannot be covered at all
    check("FindEdgeCover(Graph({1,2,3},{}))", "{}");
    check("FindEdgeCover(Graph({1,2,3},{1<->2}))", "{}");
    check("FindEdgeCover(Graph({},{}))", "{}");

    // Gallai: the minimum cover takes one edge per vertex the maximum matching missed, so it has
    // VertexCount - matching size edges
    check("Module({g=PetersenGraph()}, Length(FindEdgeCover(g))"
        + "===VertexCount(g)-Length(FindIndependentEdgeSet(g)))", "True");
    check("Module({g=StarGraph(5)}, Length(FindEdgeCover(g))"
        + "===VertexCount(g)-Length(FindIndependentEdgeSet(g)))", "True");
    check("Module({g=GridGraph({3,3})}, Length(FindEdgeCover(g))"
        + "===VertexCount(g)-Length(FindIndependentEdgeSet(g)))", "True");
    // and it really does touch every vertex
    check("Module({g=PetersenGraph(),c}, c=FindEdgeCover(g);"
        + "Sort(Union(Flatten(List@@@c)))===Sort(VertexList(g)))", "True");
    check("Module({g=GridGraph({3,3}),c}, c=FindEdgeCover(g);"
        + "Sort(Union(Flatten(List@@@c)))===Sort(VertexList(g)))", "True");

    // the edges come back in the form the graph uses
    check("FindEdgeCover(Graph({1,2,3,4},{1->2,3->4}))", "{1->2,3->4}");
    // a self loop covers only its own vertex, so it is used when nothing else can cover it and
    // passed over when something can
    check("FindEdgeCover(Graph({1,2},{1<->1,1<->2}))", "{1<->2}");
    check("FindEdgeCover(Graph({1,2,3},{1<->2,3<->3}))", "{1<->2,3<->3}");

    check("Head(FindEdgeCover(5))", "FindEdgeCover");
  }

  @Test
  public void testFindIndependentEdgeSet() {
    // a maximum matching: no two edges share a vertex, and no larger such set exists
    check("FindIndependentEdgeSet(CycleGraph(4))", "{1<->2,3<->4}");
    check("FindIndependentEdgeSet(PathGraph({1,2,3,4}))", "{1<->2,3<->4}");
    check("FindIndependentEdgeSet({1<->2,2<->3})", "{1<->2}");
    check("FindIndependentEdgeSet(Graph({1,2,3},{}))", "{}");

    // the sizes are the known maximum matching numbers
    check("Length(FindIndependentEdgeSet(CycleGraph(5)))", "2");
    check("Length(FindIndependentEdgeSet(CompleteGraph(5)))", "2");
    check("Length(FindIndependentEdgeSet(CompleteGraph(6)))", "3");
    check("Length(FindIndependentEdgeSet(StarGraph(5)))", "1");
    check("Length(FindIndependentEdgeSet(GridGraph({3,3})))", "4");
    // both of these have a perfect matching, so every vertex is covered
    check("2*Length(FindIndependentEdgeSet(PetersenGraph()))===VertexCount(PetersenGraph())",
        "True");
    check("2*Length(FindIndependentEdgeSet(HypercubeGraph(3)))===VertexCount(HypercubeGraph(3))",
        "True");

    // independence, checked through the language: the edges cover 2 distinct vertices each
    check("Module({m=FindIndependentEdgeSet(PetersenGraph())},"
        + "Length(Union(Flatten(List@@@m)))===2*Length(m))", "True");
    check("Module({m=FindIndependentEdgeSet(CompleteGraph(6))},"
        + "Length(Union(Flatten(List@@@m)))===2*Length(m))", "True");

    // the edges come back in the form the graph uses, so a directed graph gives directed edges
    check("FindIndependentEdgeSet(Graph({1,2,3,4},{1->2,3->4}))", "{1->2,3->4}");
    // a pair of opposite directed edges is one edge of the matching, not two
    check("FindIndependentEdgeSet(Graph({1,2},{1->2,2->1}))", "{1->2}");
    // a self loop touches its vertex twice, so it can never be matched
    check("FindIndependentEdgeSet(Graph({1,2},{1<->1,1<->2}))", "{1<->2}");

    check("Head(FindIndependentEdgeSet(5))", "FindIndependentEdgeSet");
  }

  @Test
  public void testFindClique() {
    // a largest clique: every two of its vertices are joined by an edge
    check("FindClique(CompleteGraph(4))", "{{1,2,3,4}}");
    check("FindClique({1<->2,2<->3,3<->1,3<->4})", "{{1,2,3}}");
    check("FindClique(WheelGraph(5))", "{{1,2,5}}");
    // a triangle-free graph has nothing bigger than an edge
    check("FindClique(CycleGraph(4))", "{{1,2}}");
    check("FindClique(CycleGraph(5))", "{{1,2}}");
    check("FindClique(PetersenGraph())", "{{1,2}}");
    // and without edges, nothing bigger than a single vertex
    check("FindClique(Graph({1,2,3},{}))", "{{1}}");
    check("FindClique(Graph({},{}))", "{}");
    // edge directions are ignored
    check("FindClique(Graph({1,2,3,4},{1->2,2->3,1->3}))", "{{1,2,3}}");

    // a clique of a graph is an independent set of its complement
    check("Length(FindClique(GraphComplement(PetersenGraph()))[[1]])"
        + "===Length(FindIndependentVertexSet(PetersenGraph())[[1]])", "True");

    // a size specification: at most n, exactly n, or a range
    check("FindClique(CompleteGraph(5), 3)", "{{1,2,3}}");
    check("FindClique(CompleteGraph(5), {3})", "{{1,2,3}}");
    check("FindClique(CompleteGraph(5), {2,3})", "{{1,2,3}}");
    check("FindClique(CompleteGraph(4), Infinity)", "{{1,2,3,4}}");
    // nothing that large exists
    check("FindClique(CycleGraph(4), {3})", "{}");

    check("Head(FindClique(5))", "FindClique");
    check("Head(FindClique(CycleGraph(4), -1))", "FindClique");
  }

  @Test
  public void testFindKClan() {
    // a k-clan is a k-clique whose INDUCED subgraph has diameter at most k. A k-clique only asks
    // that its members be within k steps in the whole graph, and those paths may run outside the
    // set - this graph shows the two coming apart.
    check(
        "h=Graph({1,2,3,4,5,6,7,8,9},"
            + "{8<->1,9<->4,7<->3,6<->4,3<->1,1<->5,3<->6,9<->2,8<->7,7<->2,3<->4,9<->5});"
            + "{FindKClique(h,2), FindKClan(h,2)}", //
        "{{{1,3,4,6,7,9}},{{1,3,4,5,9}}}");
    // the six vertex k-clique needs three steps inside itself, the five vertex k-clan needs two
    check("GraphDiameter(Subgraph(h, FindKClique(h,2)[[1]]))", "3");
    check("GraphDiameter(Subgraph(h, FindKClan(h,2)[[1]]))", "2");
    // so asking for the k-clique's size finds no k-clan at all
    check("FindKClan(h, 2, {5})", "{{1,3,4,5,9}}");
    check("FindKClan(h, 2, {6})", "{}");

    // where the whole graph is within reach of itself the two agree
    check("FindKClan(PathGraph({1,2,3}), 2)", "{{1,2,3}}");
    check("FindKClan(CycleGraph(5), 2)", "{{1,2,3,4,5}}");
    check("FindKClan(PetersenGraph(), 2)===FindKClique(PetersenGraph(), 2)", "True");
    check("GraphDiameter(Subgraph(GridGraph({3,3}), FindKClan(GridGraph({3,3}),2)[[1]]))<=2",
        "True");

    // 1-clans are cliques
    check("FindKClan(CompleteGraph(4), 1)", "{{1,2,3,4}}");
    check("FindKClan(CycleGraph(4), 1)===FindClique(CycleGraph(4))", "True");
    check("FindKClan(PetersenGraph(), 1)===FindClique(PetersenGraph())", "True");

    check("FindKClan(Graph({1,2,3},{}), 2)", "{{1}}");
    check("FindKClan(Graph({},{}), 2)", "{}");

    // k has to be a positive integer
    check("Head(FindKClan(CycleGraph(4), 0))", "FindKClan");
    check("Head(FindKClan(5, 1))", "FindKClan");
  }

  @Test
  public void testLuccioSamiComponents() {
    // an LS set asks more than a lambda component does: the lambda condition compares whole
    // vertices, this one compares every way of splitting the set. Here {2,6} is a lambda component
    // but not an LS set, because the subset {2} has one tie inside {2,6} and two to the outside.
    check(
        "w=Graph({1,2,3,4,5,6},{3<->4,2<->6,5<->6,2<->3,3<->6,1<->6,2<->5});"
            + "{LambdaComponents(w), LuccioSamiComponents(w)}", //
        "{{{1,2,3,4,5,6},{2,3,5,6},{2,6}},{{1,2,3,4,5,6}}}");
    // one edge joins 2 to 6 ...
    check("Length(Select(EdgeList(w), MemberQ({2,6},#[[1]])&&MemberQ({2,6},#[[2]])&))", "1");
    // ... and two leave 2 for the outside
    check("Length(Select(EdgeList(w), (MemberQ({2},#[[1]])||MemberQ({2},#[[2]]))"
        + "&&!(MemberQ({2,6},#[[1]])&&MemberQ({2,6},#[[2]]))&))", "2");

    // where they agree, every LS set is a lambda component
    check("t=Graph({1,2,3,4,5,6},{1<->2,2<->3,3<->1,4<->5,5<->6,6<->4,3<->4});"
        + "LuccioSamiComponents(t)===LambdaComponents(t)", "True");
    check(
        "u=Graph({1,2,3,4,5,6,7,8},"
            + "{1<->2,1<->3,1<->4,2<->3,2<->4,3<->4,5<->6,5<->7,5<->8,6<->7,6<->8,7<->8,4<->5});"
            + "LuccioSamiComponents(u)", //
        "{{1,2,3,4,5,6,7,8},{1,2,3,4},{5,6,7,8}}");

    // the second argument keeps only the components holding one of the listed vertices
    check("LuccioSamiComponents(t, {1})", "{{1,2,3,4,5,6},{1,2,3}}");
    check("LuccioSamiComponents(t)===LuccioSamiComponents(t, VertexList(t))", "True");

    check("LuccioSamiComponents(CompleteGraph(4))", "{{1,2,3,4}}");
    check("LuccioSamiComponents(CycleGraph(5))", "{{1,2,3,4,5}}");
    check("LuccioSamiComponents(PetersenGraph())", "{{1,2,3,4,5,6,7,8,9,10}}");
    check("LuccioSamiComponents(Graph({1,2,3,4,5},{1<->2,3<->4}))", "{{1,2},{3,4},{5}}");
    check("LuccioSamiComponents(Graph({},{}))", "{}");

    check("Head(LuccioSamiComponents(5))", "LuccioSamiComponents");
  }

  @Test
  public void testLambdaComponents() {
    // two triangles joined by one edge: lambda is 2 inside a triangle and 1 across the bridge, so
    // each triangle is a lambda component and so is the whole graph
    check(
        "t=Graph({1,2,3,4,5,6},{1<->2,2<->3,3<->1,4<->5,5<->6,6<->4,3<->4});"
            + "LambdaComponents(t)", //
        "{{1,2,3,4,5,6},{1,2,3},{4,5,6}}");
    // the same shape one level up: two complete graphs joined by one edge
    check(
        "u=Graph({1,2,3,4,5,6,7,8},"
            + "{1<->2,1<->3,1<->4,2<->3,2<->4,3<->4,5<->6,5<->7,5<->8,6<->7,6<->8,7<->8,4<->5});"
            + "LambdaComponents(u)", //
        "{{1,2,3,4,5,6,7,8},{1,2,3,4},{5,6,7,8}}");

    // the second argument keeps only the components holding one of the listed vertices
    check("LambdaComponents(t, {1})", "{{1,2,3,4,5,6},{1,2,3}}");
    check("LambdaComponents(t, {1,4})", "{{1,2,3,4,5,6},{1,2,3},{4,5,6}}");
    check("LambdaComponents(t)===LambdaComponents(t, VertexList(t))", "True");

    // where every pair is equally well connected there is nothing to split off
    check("LambdaComponents(CompleteGraph(4))", "{{1,2,3,4}}");
    check("LambdaComponents(CycleGraph(5))", "{{1,2,3,4,5}}");
    check("LambdaComponents(PetersenGraph())", "{{1,2,3,4,5,6,7,8,9,10}}");
    check("LambdaComponents(StarGraph(5))", "{{1,2,3,4,5}}");

    // no edges means no paths at all, so every vertex stands alone
    check("LambdaComponents(Graph({1,2,3,4,5},{1<->2,3<->4}))", "{{1,2},{3,4},{5}}");
    check("LambdaComponents(Graph({1,2,3},{}))", "{{1},{2},{3}}");
    check("LambdaComponents(Graph({},{}))", "{}");

    check("Head(LambdaComponents(5))", "LambdaComponents");
  }

  @Test
  public void testKCoreComponents() {
    // the k-core is what survives removing vertices of degree below k over and over
    check("KCoreComponents(CycleGraph(4), 2)", "{{1,2,3,4}}");
    check("KCoreComponents(CycleGraph(4), 3)", "{}");
    check("KCoreComponents(CompleteGraph(4), 3)", "{{1,2,3,4}}");
    check("KCoreComponents(CompleteGraph(4), 4)", "{}");
    check("KCoreComponents(PetersenGraph(), 3)", "{{1,2,3,4,5,6,7,8,9,10}}");
    check("KCoreComponents(PetersenGraph(), 4)", "{}");

    // removing a vertex lowers its neighbours' degree, so the peeling cascades: the pendant path
    // goes and the triangle stays
    check("KCoreComponents(Graph({1,2,3,4,5},{1<->2,2<->3,3<->1,3<->4,4<->5}), 2)", "{{1,2,3}}");
    check("KCoreComponents(Graph({1,2,3,4,5},{1<->2,2<->3,3<->1,3<->4,4<->5}), 1)",
        "{{1,2,3,4,5}}");
    // a star loses its leaves and then its centre
    check("KCoreComponents(StarGraph(5), 2)", "{}");
    check("KCoreComponents(StarGraph(5), 1)", "{{1,2,3,4,5}}");

    // the core can fall into several components
    check("KCoreComponents(Graph({1,2,3,4,5,6},{1<->2,2<->3,3<->1,4<->5,5<->6,6<->4}), 2)",
        "{{1,2,3},{4,5,6}}");
    // at k = 0 nothing is removed, so this is just ConnectedComponents
    check("KCoreComponents(Graph({1,2,3,4,5},{1<->2,3<->4}), 0)"
        + "===ConnectedComponents(Graph({1,2,3,4,5},{1<->2,3<->4}))", "True");
    check("KCoreComponents(Graph({1,2,3,4,5},{1<->2,3<->4}), 1)", "{{1,2},{3,4}}");
    check("KCoreComponents(Graph({},{}), 1)", "{}");

    // "In" and "Out" count only one direction. A directed cycle keeps every vertex either way, a
    // directed path keeps none, because its ends have no incoming resp. no outgoing edge.
    check("KCoreComponents(Graph({1,2,3},{1->2,2->3,3->1}), 1, \"In\")", "{{1,2,3}}");
    check("KCoreComponents(Graph({1,2,3},{1->2,2->3,3->1}), 1, \"Out\")", "{{1,2,3}}");
    check("KCoreComponents(Graph({1,2,3},{1->2,2->3}), 1, \"In\")", "{}");
    check("KCoreComponents(Graph({1,2,3},{1->2,2->3}), 1, \"Out\")", "{}");

    check("Head(KCoreComponents(CycleGraph(4), -1))", "KCoreComponents");
    check("Head(KCoreComponents(CycleGraph(4), 2, \"Bogus\"))", "KCoreComponents");
    check("Head(KCoreComponents(5, 1))", "KCoreComponents");
  }

  @Test
  public void testFindKClub() {
    // a k-club has to hold together within k steps using only its own edges - the same requirement
    // a k-clan makes - but it need only be CONTAINED in a k-clique rather than be one. This graph
    // has no k-clan at all, and still has k-clubs.
    check("g=Graph({1,2,3,4,5,6,7,8,9},"
        + "{1<->9,2<->3,4<->8,5<->4,1<->5,4<->2,7<->6,7<->5,6<->2,3<->8,3<->9,9<->2,7<->1,8<->7});"
        + "{FindKClique(g,2), FindKClan(g,2), FindKClub(g,2)}", //
        "{{{1,2,3,4,6,7,8,9}},{},{{1,2,3,6,7,8,9}}}");
    check("GraphDiameter(Subgraph(g, FindKClique(g,2)[[1]]))", "3");
    check("GraphDiameter(Subgraph(g, FindKClub(g,2)[[1]]))", "2");

    // every k-clan is a k-club and every k-club sits inside a k-clique, so the sizes are ordered
    check(
        "h=Graph({1,2,3,4,5,6,7,8,9},"
            + "{8<->1,9<->4,7<->3,6<->4,3<->1,1<->5,3<->6,9<->2,8<->7,7<->2,3<->4,9<->5});"
            + "{Length(FindKClique(h,2)[[1]]), Length(FindKClan(h,2)[[1]]),"
            + "Length(FindKClub(h,2)[[1]])}", //
        "{6,5,5}");
    check("GraphDiameter(Subgraph(h, FindKClub(h,2)[[1]]))", "2");
    // the search honours an exact size rather than trimming a larger club
    check("FindKClub(h, 2, {4})", "{{1,3,4,6}}");
    check("GraphDiameter(Subgraph(h, FindKClub(h,2,{4})[[1]]))<=2", "True");

    // where the whole graph holds together the three notions agree
    check("FindKClub(PathGraph({1,2,3}), 2)", "{{1,2,3}}");
    check("FindKClub(CycleGraph(5), 2)", "{{1,2,3,4,5}}");
    check("FindKClub(PetersenGraph(), 2)===FindKClan(PetersenGraph(), 2)", "True");

    // 1-clubs are cliques
    check("FindKClub(CompleteGraph(4), 1)", "{{1,2,3,4}}");
    check("FindKClub(CycleGraph(4), 1)===FindClique(CycleGraph(4))", "True");
    check("FindKClub(PetersenGraph(), 1)===FindClique(PetersenGraph())", "True");

    check("FindKClub(Graph({1,2,3},{}), 2)", "{{1}}");
    check("FindKClub(Graph({},{}), 2)", "{}");

    // k has to be a positive integer
    check("Head(FindKClub(CycleGraph(4), 0))", "FindKClub");
    check("Head(FindKClub(5, 1))", "FindKClub");
  }

  @Test
  public void testFindKPlex() {
    // a k-plex is a set in which every member is adjacent to all but k of the others, so within a
    // set of m vertices every member needs at least m-k neighbours inside it
    check("FindKPlex(CycleGraph(4), 2)", "{{1,2,3,4}}");
    check("FindKPlex(CycleGraph(5), 2)", "{{1,2,3}}");
    check("FindKPlex(CycleGraph(5), 3)", "{{1,2,3,4,5}}");
    check("FindKPlex(PathGraph({1,2,3,4}), 2)", "{{1,2,3}}");

    // a 1-plex is a clique
    check("FindKPlex(CompleteGraph(4), 1)", "{{1,2,3,4}}");
    check("FindKPlex({1<->2,2<->3,3<->1,3<->4}, 1)", "{{1,2,3}}");
    check("FindKPlex(CycleGraph(4), 1)===FindClique(CycleGraph(4))", "True");
    check("FindKPlex(PetersenGraph(), 1)===FindClique(PetersenGraph())", "True");
    // and a complete graph is a k-plex for every k
    check("FindKPlex(CompleteGraph(5), 3)", "{{1,2,3,4,5}}");

    // raising k only relaxes the condition, so the sizes never fall
    check("Table(Length(FindKPlex(CycleGraph(5),k)[[1]]),{k,1,4})", "{2,3,5,5}");
    check("Table(Length(FindKPlex(PetersenGraph(),k)[[1]]),{k,1,3})", "{2,3,5}");

    // the defining condition, checked through the language
    check("Module({g=PetersenGraph(),s,k=2,n,d}, s=FindKPlex(g,k)[[1]]; n=Length(s);"
        + "d=Table(Length(Select(s, EdgeQ(g,UndirectedEdge(s[[i]],#))&)),{i,1,n}); Min(d)>=n-k)",
        "True");
    check("Module({g=GridGraph({3,3}),s,k=3,n,d}, s=FindKPlex(g,k)[[1]]; n=Length(s);"
        + "d=Table(Length(Select(s, EdgeQ(g,UndirectedEdge(s[[i]],#))&)),{i,1,n}); Min(d)>=n-k)",
        "True");

    // without edges every member needs at least m-k neighbours, so m cannot exceed k
    check("FindKPlex(Graph({1,2,3},{}), 1)", "{{1}}");
    check("FindKPlex(Graph({1,2,3},{}), 2)", "{{1,2}}");
    check("FindKPlex(Graph({1,2,3},{}), 3)", "{{1,2,3}}");
    check("FindKPlex(Graph({},{}), 2)", "{}");

    // the size specification is the third argument here
    check("FindKPlex(CompleteGraph(5), 2, {3})", "{{1,2,3}}");
    check("FindKPlex(CycleGraph(4), 1, {3})", "{}");

    // k has to be a positive integer
    check("Head(FindKPlex(CycleGraph(4), 0))", "FindKPlex");
    check("Head(FindKPlex(5, 1))", "FindKPlex");
  }

  @Test
  public void testFindKClique() {
    // Luce's k-clique: a maximal set of vertices at distance no greater than k from each other, so
    // the members need not be joined by edges and the connecting paths may leave the set
    check("FindKClique(PathGraph({1,2,3}), 2)", "{{1,2,3}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2)", "{{1,2,3}}");
    // a graph of diameter k has one k-clique holding every vertex
    check("GraphDiameter(PetersenGraph())", "2");
    check("Length(FindKClique(PetersenGraph(), 2)[[1]])===VertexCount(PetersenGraph())", "True");
    check("FindKClique(CycleGraph(5), 2)", "{{1,2,3,4,5}}");
    check("FindKClique(StarGraph(5), 2)", "{{1,2,3,4,5}}");

    // 1-cliques are the ordinary cliques
    check("FindKClique(CompleteGraph(4), 1)", "{{1,2,3,4}}");
    check("FindKClique(PetersenGraph(), 1)===FindClique(PetersenGraph())", "True");
    check("FindKClique(CycleGraph(4), 1)===FindClique(CycleGraph(4))", "True");

    // vertices in different components are never within any distance of each other
    check("FindKClique(Graph({1,2,3,4},{1<->2,3<->4}), 5)", "{{1,2}}");
    check("FindKClique(Graph({1,2,3},{}), 2)", "{{1}}");
    check("FindKClique(Graph({},{}), 2)", "{}");

    // the size specification is the third argument here
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, {2})", "{{1,2}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, 2)", "{{1,2}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, {4})", "{}");

    // k has to be a positive integer
    check("Head(FindKClique(CycleGraph(4), 0))", "FindKClique");
    check("Head(FindKClique(CycleGraph(4), x))", "FindKClique");
    check("Head(FindKClique(5, 1))", "FindKClique");
  }

  @Test
  public void testFindIndependentVertexSetSeveralSets() {
    // the third argument asks for more than one set, largest first
    check("FindIndependentVertexSet(PathGraph({1,2,3,4}), Infinity, All)", "{{1,3},{1,4},{2,4}}");
    check("FindIndependentVertexSet(CycleGraph(4), Infinity, All)", "{{1,3},{2,4}}");
    check("FindIndependentVertexSet(CycleGraph(5), Infinity, All)",
        "{{1,3},{1,4},{2,4},{2,5},{3,5}}");
    check("FindIndependentVertexSet(CycleGraph(5), Infinity, 2)", "{{1,3},{1,4}}");
    // a complete graph has nothing but singletons; a star has its leaves and its centre
    check("FindIndependentVertexSet(CompleteGraph(4), Infinity, All)", "{{1},{2},{3},{4}}");
    check("FindIndependentVertexSet(StarGraph(5), Infinity, All)", "{{2,3,4,5},{1}}");
    check("FindIndependentVertexSet(Graph({1,2,3},{}), Infinity, All)", "{{1,2,3}}");
    // and one of them is what the shorter forms report
    check("FindIndependentVertexSet(CycleGraph(4), Infinity, All)[[1]]"
        + "===FindIndependentVertexSet(CycleGraph(4))[[1]]", "True");

    // an independent set of a graph is a clique of its complement - set for set, not just in size
    check("FindIndependentVertexSet(CycleGraph(5), Infinity, All)"
        + "===FindClique(GraphComplement(CycleGraph(5)), Infinity, All)", "True");
    check("FindIndependentVertexSet(PetersenGraph(), Infinity, All)"
        + "===FindClique(GraphComplement(PetersenGraph()), Infinity, All)", "True");
    // and the predicate agrees with every one of them
    check("AllTrue(FindIndependentVertexSet(PetersenGraph(), Infinity, All),"
        + "IndependentVertexSetQ(PetersenGraph(), #)&)", "True");

    // s has to be a positive integer or All
    check("Head(FindIndependentVertexSet(CycleGraph(4), Infinity, 0))", "FindIndependentVertexSet");
    check("Head(FindIndependentVertexSet(CycleGraph(4), Infinity, x))", "FindIndependentVertexSet");
  }

  @Test
  public void testFindCliqueSeveralSets() {
    // the third argument asks for more than one clique, largest first
    check("FindClique({1<->2,2<->3,3<->1,3<->4}, Infinity, All)", "{{1,2,3},{3,4}}");
    check("FindClique(CycleGraph(4), Infinity, All)", "{{1,2},{1,4},{2,3},{3,4}}");
    check("FindClique(CycleGraph(5), Infinity, 2)", "{{1,2},{1,5}}");
    check("FindClique(CompleteGraph(4), Infinity, All)", "{{1,2,3,4}}");
    // and one of them is what the shorter forms report
    check("FindClique(CycleGraph(4), Infinity, All)[[1]]===FindClique(CycleGraph(4))[[1]]", "True");

    // a triangle free graph has one maximal clique per edge
    check("Length(FindClique(PetersenGraph(), Infinity, All))===EdgeCount(PetersenGraph())",
        "True");
    // a size specification still applies
    check("FindClique({1<->2,2<->3,3<->1,3<->4}, {2}, All)", "{{1,2},{3,4}}");

    // s has to be a positive integer or All
    check("Head(FindClique(CycleGraph(4), Infinity, 0))", "FindClique");
  }

  @Test
  public void testFindKPlexSeveralSets() {
    // every 2-plex of a five cycle: the five runs of three consecutive vertices
    check("FindKPlex(CycleGraph(5), 2, Infinity, All)",
        "{{1,2,3},{1,2,5},{1,4,5},{2,3,4},{3,4,5}}");
    check("FindKPlex(CycleGraph(5), 2, Infinity, 2)", "{{1,2,3},{1,2,5}}");
    // 1-plexes are cliques, so these two agree set for set
    check("FindKPlex(CycleGraph(4), 1, Infinity, All)===FindClique(CycleGraph(4), Infinity, All)",
        "True");
    check("FindKPlex({1<->2,2<->3,3<->1,3<->4}, 1, Infinity, All)", "{{1,2,3},{3,4}}");
    // without edges any two vertices form a 2-plex, and a third would need a neighbour
    check("FindKPlex(Graph({1,2,3},{}), 2, Infinity, All)", "{{1,2},{1,3},{2,3}}");

    // s has to be a positive integer or All
    check("Head(FindKPlex(CycleGraph(4), 1, Infinity, 0))", "FindKPlex");
  }

  @Test
  public void testFindKClanAndKClubSeveralSets() {
    check(
        "h=Graph({1,2,3,4,5,6,7,8,9},"
            + "{8<->1,9<->4,7<->3,6<->4,3<->1,1<->5,3<->6,9<->2,8<->7,7<->2,3<->4,9<->5});"
            + "FindKClan(h, 2, Infinity, All)", //
        "{{1,3,4,5,9},{2,3,4,7,9},{1,3,5,8},{1,3,7,8},{2,3,7,8}}");
    // five of the eight 2-cliques hold together within two steps of their own
    check("Length(FindKClique(h, 2, Infinity, All))", "8");
    // and every k-clan is a k-club, which is now checked set by set rather than by size
    check("Complement(FindKClan(h,2,Infinity,All), FindKClub(h,2,Infinity,All))", "{}");
    // a graph with no k-clan at all still has k-clubs
    check("g=Graph({1,2,3,4,5,6,7,8,9},"
        + "{1<->9,2<->3,4<->8,5<->4,1<->5,4<->2,7<->6,7<->5,6<->2,3<->8,3<->9,9<->2,7<->1,8<->7});"
        + "{FindKClan(g,2,Infinity,All), Length(FindKClub(g,2,Infinity,All))>0}", //
        "{{},True}");

    // at k = 1 all three families are the cliques
    check("FindKClan(CycleGraph(4), 1, Infinity, All)"
        + "===FindClique(CycleGraph(4), Infinity, All)", "True");
    check("FindKClub(CycleGraph(4), 1, Infinity, All)"
        + "===FindClique(CycleGraph(4), Infinity, All)", "True");
    // and where the paths stay inside the sets the two agree with each other
    check("FindKClan(PathGraph({1,2,3,4,5}), 2, Infinity, All)", "{{1,2,3},{2,3,4},{3,4,5}}");
    check("FindKClub(PathGraph({1,2,3,4,5}), 2, Infinity, All)"
        + "===FindKClan(PathGraph({1,2,3,4,5}), 2, Infinity, All)", "True");

    // one of them is what the shorter forms report
    check("FindKClan(h, 2, Infinity, 1)===FindKClan(h, 2)", "True");

    // s has to be a positive integer or All
    check("Head(FindKClan(CycleGraph(4), 1, Infinity, 0))", "FindKClan");
    check("Head(FindKClub(CycleGraph(4), 1, Infinity, x))", "FindKClub");
  }

  @Test
  public void testFindKCliqueSeveralSets() {
    // the fourth argument asks for more than one k-clique, largest first
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, Infinity, All)", "{{1,2,3},{2,3,4},{3,4,5}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, Infinity, 2)", "{{1,2,3},{2,3,4}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, Infinity, 1)", "{{1,2,3}}");
    // and one of them is what the shorter forms report
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, Infinity, All)[[1]]"
        + "===FindKClique(PathGraph({1,2,3,4,5}), 2)[[1]]", "True");

    // the Petersen graph is triangle free, so its 1-cliques are exactly its edges
    check(
        "Length(FindKClique(PetersenGraph(), 1, Infinity, All))" + "===EdgeCount(PetersenGraph())",
        "True");
    // a complete graph has just the one
    check("FindKClique(CompleteGraph(4), 1, Infinity, All)", "{{1,2,3,4}}");
    // separate components never share a k-clique, however large k is
    check("FindKClique(Graph({1,2,3,4},{1<->2,3<->4}), 5, Infinity, All)", "{{1,2},{3,4}}");

    // a size specification still applies, and sets that trim to the same thing are reported once
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, {2}, All)", "{{1,2},{2,3},{3,4}}");
    check("FindKClique(PathGraph({1,2,3,4,5}), 2, {4}, All)", "{}");

    // s has to be a positive integer or All
    check("Head(FindKClique(CycleGraph(4), 1, Infinity, 0))", "FindKClique");
    check("Head(FindKClique(CycleGraph(4), 1, Infinity, x))", "FindKClique");
  }

  @Test
  public void testFindIndependentVertexSet() {
    // a maximum independent set: no two of its vertices are joined by an edge
    check("FindIndependentVertexSet(CycleGraph(4))", "{{1,3}}");
    check("FindIndependentVertexSet(PathGraph({1,2,3,4}))", "{{1,3}}");
    check("FindIndependentVertexSet(CompleteGraph(4))", "{{1}}");
    check("FindIndependentVertexSet(StarGraph(5))", "{{2,3,4,5}}");
    // without edges every vertex is independent of every other
    check("FindIndependentVertexSet(Graph({1,2,3},{}))", "{{1,2,3}}");
    check("FindIndependentVertexSet(Graph({},{}))", "{}");
    check("FindIndependentVertexSet({1<->2,2<->3})", "{{1,3}}");

    // the known independence numbers
    check("Length(FindIndependentVertexSet(CycleGraph(5))[[1]])", "2");
    check("Length(FindIndependentVertexSet(PetersenGraph())[[1]])", "4");
    check("Length(FindIndependentVertexSet(GridGraph({3,3}))[[1]])", "5");

    // independence, checked through the language: no edge has both ends in the set
    check("Module({g=PetersenGraph(),s}, s=FindIndependentVertexSet(g)[[1]];"
        + "Length(Select(EdgeList(g), MemberQ(s,#[[1]])&&MemberQ(s,#[[2]])&)))", "0");
    // Gallai: a maximum independent set is the complement of a minimum vertex cover
    check("Length(FindIndependentVertexSet(CycleGraph(5))[[1]])"
        + "+Length(FindVertexCover(CycleGraph(5)))===VertexCount(CycleGraph(5))", "True");

    // a size specification: at most n, exactly n, or a range
    check("FindIndependentVertexSet(CycleGraph(5), 2)", "{{1,3}}");
    check("Length(FindIndependentVertexSet(PetersenGraph(), 2)[[1]])", "2");
    check("FindIndependentVertexSet(CycleGraph(5), {2})", "{{1,3}}");
    check("FindIndependentVertexSet(CycleGraph(5), {1,2})", "{{1,3}}");
    check("FindIndependentVertexSet(CycleGraph(4), Infinity)", "{{1,3}}");
    // nothing that large exists
    check("FindIndependentVertexSet(CycleGraph(5), {3})", "{}");
    check("FindIndependentVertexSet(CompleteGraph(4), {2})", "{}");

    // the case this test pinned as unimplemented before
    check(
        "FindIndependentVertexSet("
            + "Graph({1<->2, 1<->3, 2<-> 4, 4<->5, 5<->3, 4<->6, 5<->6, 4<->7, 5<->7}))", //
        "{{2,3,6,7}}");

    check("Head(FindIndependentVertexSet(5))", "FindIndependentVertexSet");
    check("Head(FindIndependentVertexSet(CycleGraph(4), -1))", "FindIndependentVertexSet");
  }

  @Test
  public void testIndependentVertexSetQ() {
    check("IndependentVertexSetQ(CycleGraph(4), {1,3})", "True");
    check("IndependentVertexSetQ(CycleGraph(4), {1})", "True");
    check("IndependentVertexSetQ(StarGraph(5), {2,3,4,5})", "True");
    check("IndependentVertexSetQ(Graph({1,2,3},{}), {1,2,3})", "True");
    // the empty set is an independent vertex set of every graph
    check("IndependentVertexSetQ(CycleGraph(4), {})", "True");

    // 1 and 2 are joined by an edge
    check("IndependentVertexSetQ(CycleGraph(4), {1,2})", "False");
    check("IndependentVertexSetQ(CompleteGraph(4), {1,2})", "False");
    // in a 5-cycle, 5 closes back onto 1
    check("IndependentVertexSetQ(CycleGraph(5), {1,3,5})", "False");
    // a repeated vertex makes the list something other than a set
    check("IndependentVertexSetQ(CycleGraph(4), {1,1})", "False");
    // every entry has to be a vertex of the graph
    check("IndependentVertexSetQ(CycleGraph(4), {1,5})", "False");

    // an edge makes its endpoints dependent whichever way it points, and whichever order they are
    // listed in - unlike IndependentEdgeSetQ, where the edge itself has to be in the graph as given
    check("IndependentVertexSetQ(Graph({1,2,3,4},{1->2}), {1,2})", "False");
    check("IndependentVertexSetQ(Graph({1,2,3,4},{1->2}), {2,1})", "False");
    // a self loop is ignored, so a vertex carrying one can still belong to a set
    check("IndependentVertexSetQ(Graph({1,2},{1<->1}), {1})", "True");

    // what FindIndependentVertexSet returns is one of these, by construction
    check("IndependentVertexSetQ(PetersenGraph(), FindIndependentVertexSet(PetersenGraph())[[1]])",
        "True");
    check("IndependentVertexSetQ(GridGraph({3,3}),"
        + "FindIndependentVertexSet(GridGraph({3,3}))[[1]])", "True");
    // and so is the complement of a vertex cover
    check("IndependentVertexSetQ(CycleGraph(5),"
        + "Complement(VertexList(CycleGraph(5)), FindVertexCover(CycleGraph(5))))", "True");

    // a bare list of edges is a graph, like everywhere else here
    check("IndependentVertexSetQ({1<->2,2<->3}, {1,3})", "True");
    // anything that is not a graph and a list of vertices is just False
    check("IndependentVertexSetQ(CycleGraph(4), 5)", "False");
    check("IndependentVertexSetQ(5, {1})", "False");
  }

  @Test
  public void testVertexCoverQ() {
    check("VertexCoverQ(CycleGraph(4), {1,3})", "True");
    check("VertexCoverQ(CycleGraph(4), {2,4})", "True");
    check("VertexCoverQ(CycleGraph(4), {1,2,3,4})", "True");
    check("VertexCoverQ(StarGraph(5), {1})", "True");
    check("VertexCoverQ(StarGraph(5), {2,3,4,5})", "True");
    check("VertexCoverQ(PathGraph({1,2,3}), {2})", "True");
    check("VertexCoverQ({1<->2,2<->3}, {2})", "True");

    // the edge 3<->4 has neither end in the set
    check("VertexCoverQ(CycleGraph(4), {1,2})", "False");
    check("VertexCoverQ(CycleGraph(4), {1})", "False");
    // the empty list covers a graph only when it has no edges
    check("VertexCoverQ(CycleGraph(4), {})", "False");
    check("VertexCoverQ(Graph({1,2,3},{}), {})", "True");
    // every entry has to be a vertex of the graph
    check("VertexCoverQ(CycleGraph(4), {1,3,5})", "False");
    // a repeated vertex is harmless: covering an edge twice breaks nothing
    check("VertexCoverQ(CycleGraph(4), {1,3,1})", "True");

    // either endpoint covers an edge, so direction does not matter here
    check("VertexCoverQ(Graph({1,2,3,4},{1->2,3->4}), {1,3})", "True");
    check("VertexCoverQ(Graph({1,2,3,4},{1->2,3->4}), {2,4})", "True");
    // a self loop is covered only by its own vertex
    check("VertexCoverQ(Graph({1,2,3},{1<->2,3<->3}), {1,3})", "True");
    check("VertexCoverQ(Graph({1,2,3},{1<->2,3<->3}), {1})", "False");

    // what FindVertexCover returns is one of these, by construction
    check("VertexCoverQ(PetersenGraph(), FindVertexCover(PetersenGraph()))", "True");
    check("VertexCoverQ(GridGraph({3,3}), FindVertexCover(GridGraph({3,3})))", "True");
    check("VertexCoverQ(CycleGraph(5), FindVertexCover(CycleGraph(5)))", "True");

    // a set covers exactly when the vertices it leaves out are independent
    check("Module({g=PetersenGraph(),s={1,2,3,4,5}},"
        + "VertexCoverQ(g,s)===IndependentVertexSetQ(g,Complement(VertexList(g),s)))", "True");
    check("Module({g=CycleGraph(5),s={1,3}},"
        + "VertexCoverQ(g,s)===IndependentVertexSetQ(g,Complement(VertexList(g),s)))", "True");
    check("Module({g=GridGraph({3,3}),s=FindVertexCover(GridGraph({3,3}))},"
        + "IndependentVertexSetQ(g,Complement(VertexList(g),s)))", "True");

    // anything that is not a graph and a list of vertices is just False
    check("VertexCoverQ(CycleGraph(4), 5)", "False");
    check("VertexCoverQ(5, {1})", "False");
  }

  @Test
  public void testEdgeCoverQ() {
    check("EdgeCoverQ(CycleGraph(4), {1<->2,3<->4})", "True");
    check("EdgeCoverQ(CycleGraph(4), {1<->2,2<->3,3<->4,4<->1})", "True");
    check("EdgeCoverQ(PathGraph({1,2,3}), {1<->2,2<->3})", "True");
    check("EdgeCoverQ(CycleGraph(4), EdgeList(CycleGraph(4)))", "True");
    check("EdgeCoverQ({1<->2,2<->3}, {1<->2,2<->3})", "True");

    // vertices 3 and 4 are left untouched
    check("EdgeCoverQ(CycleGraph(4), {1<->2})", "False");
    check("EdgeCoverQ(PathGraph({1,2,3}), {1<->2})", "False");
    // the empty list covers a graph only when it has no vertices at all
    check("EdgeCoverQ(CycleGraph(4), {})", "False");
    check("EdgeCoverQ(Graph({},{}), {})", "True");

    // every edge has to be an edge of the graph
    check("EdgeCoverQ(CycleGraph(4), {1<->3,2<->4})", "False");
    check("EdgeCoverQ(CycleGraph(4), {1<->2,3<->4,5<->6})", "False");

    // a repeated edge is harmless for a cover - covering a vertex twice breaks nothing, where for
    // an independent edge set touching one twice is exactly what breaks it
    check("EdgeCoverQ(CycleGraph(4), {1<->2,3<->4,1<->2})", "True");
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,1<->2})", "False");
    // a self loop is incident to its own vertex and covers it
    check("EdgeCoverQ(Graph({1,2,3},{1<->2,3<->3}), {1<->2,3<->3})", "True");
    check("EdgeCoverQ(Graph({1,2,3},{1<->2,3<->3}), {1<->2})", "False");
    // direction counts in a directed graph, as it does for EdgeQ
    check("EdgeCoverQ(Graph({1,2,3,4},{1->2,3->4}), {1->2,3->4})", "True");
    check("EdgeCoverQ(Graph({1,2,3,4},{1->2,3->4}), {2->1,3->4})", "False");

    // what FindEdgeCover returns is one of these, by construction
    check("EdgeCoverQ(PetersenGraph(), FindEdgeCover(PetersenGraph()))", "True");
    check("EdgeCoverQ(GridGraph({3,3}), FindEdgeCover(GridGraph({3,3})))", "True");
    check("EdgeCoverQ(StarGraph(5), FindEdgeCover(StarGraph(5)))", "True");

    // anything that is not a graph and a list of edges is just False
    check("EdgeCoverQ(CycleGraph(4), 5)", "False");
    check("EdgeCoverQ(5, {1<->2})", "False");
  }

  @Test
  public void testIndependentEdgeSetQ() {
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,3<->4})", "True");
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2})", "True");
    // the empty set is an independent edge set of every graph
    check("IndependentEdgeSetQ(CycleGraph(4), {})", "True");

    // 1<->2 and 2<->3 meet at vertex 2
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,2<->3})", "False");
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,3<->4,4<->1})", "False");
    // the same edge twice touches its vertices twice
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,1<->2})", "False");
    // and a self loop meets itself
    check("IndependentEdgeSetQ(Graph({1,2},{1<->1,1<->2}), {1<->1})", "False");

    // every edge has to be an edge of the graph
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->3})", "False");
    check("IndependentEdgeSetQ(CycleGraph(4), {1<->2,3<->4,5<->6})", "False");
    // direction counts in a directed graph, as it does for EdgeQ
    check("IndependentEdgeSetQ(Graph({1,2,3,4},{1->2,3->4}), {1->2,3->4})", "True");
    check("IndependentEdgeSetQ(Graph({1,2,3,4},{1->2,3->4}), {2->1})", "False");

    // what FindIndependentEdgeSet returns is one of these, by construction
    check("IndependentEdgeSetQ(PetersenGraph(), FindIndependentEdgeSet(PetersenGraph()))", "True");
    check("IndependentEdgeSetQ(CompleteGraph(6), FindIndependentEdgeSet(CompleteGraph(6)))",
        "True");
    check("IndependentEdgeSetQ(GridGraph({3,3}), FindIndependentEdgeSet(GridGraph({3,3})))",
        "True");

    // a bare list of edges is a graph, like everywhere else here
    check("IndependentEdgeSetQ({1<->2,2<->3}, {1<->2})", "True");
    // anything that is not a graph and a list of edges is just False
    check("IndependentEdgeSetQ(CycleGraph(4), {1,2})", "False");
    check("IndependentEdgeSetQ(CycleGraph(4), 5)", "False");
    check("IndependentEdgeSetQ(5, {1<->2})", "False");
  }

  @Test
  public void testDualPlanarGraph() {
    // one vertex per face, numbered as PlanarFaceList lists them
    check("VertexList(DualPlanarGraph(CompleteGraph(4)))", "{1,2,3,4}");
    check("VertexCount(DualPlanarGraph(GridGraph({3,3})))"
        + "===Length(PlanarFaceList(GridGraph({3,3})))", "True");

    // the tetrahedron is self dual
    check("IsomorphicGraphQ(DualPlanarGraph(CompleteGraph(4)), CompleteGraph(4))", "True");
    // the dual of the cube is the octahedron: 6 vertices, 12 edges
    check("VertexCount(DualPlanarGraph(HypercubeGraph(3)))", "6");
    check("EdgeCount(DualPlanarGraph(HypercubeGraph(3)))", "12");
    // a cycle bounds two faces, separated by every edge, so the dual is a single edge
    check("EdgeList(DualPlanarGraph(CycleGraph(4)))", "{1<->2}");

    // coloring the faces of a graph is coloring the vertices of its dual
    check("VertexChromaticNumber(DualPlanarGraph(GridGraph({3,3})))"
        + "===Max(FindPlanarColoring(GridGraph({3,3})))", "True");
    check("VertexChromaticNumber(DualPlanarGraph(CompleteGraph(4)))"
        + "===Max(FindPlanarColoring(CompleteGraph(4)))", "True");

    // a bridge separates a face from itself, and the simple dual carries no self loop for it
    check("DualPlanarGraph(PathGraph({1,2,3}))", "Graph({1},{})");

    check("Head(DualPlanarGraph(CompleteGraph(5)))", "DualPlanarGraph");
    check("Head(DualPlanarGraph(5))", "DualPlanarGraph");
  }

  @Test
  public void testFindPlanarColoring() {
    // the faces of a planar graph, not its vertices: a cycle bounds two faces, the inside and the
    // outside, and they share every edge
    check("FindPlanarColoring(CycleGraph(3))", "{1,2}");
    check("FindPlanarColoring(CycleGraph(4))", "{1,2}");
    check("FindPlanarColoring(CycleGraph(5))", "{1,2}");

    // the tetrahedron has four faces, each touching the other three
    check("FindPlanarColoring(CompleteGraph(4))", "{1,2,3,4}");
    // the cube has six, and opposite faces can share a color
    check("Max(FindPlanarColoring(HypercubeGraph(3)))", "3");
    check("Max(FindPlanarColoring(GridGraph({3,3})))", "3");
    check("Max(FindPlanarColoring(WheelGraph(5)))", "3");

    // one color per face, and Euler's formula fixes how many faces a connected planar graph has
    check("Length(FindPlanarColoring(GridGraph({3,3})))"
        + "===EdgeCount(GridGraph({3,3}))-VertexCount(GridGraph({3,3}))+2", "True");
    check("Length(FindPlanarColoring(HypercubeGraph(3)))"
        + "===EdgeCount(HypercubeGraph(3))-VertexCount(HypercubeGraph(3))+2", "True");

    // every edge of a tree is a bridge, so there is only the outer face
    check("FindPlanarColoring(PathGraph({1,2,3,4}))", "{1}");
    check("FindPlanarColoring(Graph({1,2},{1<->2}))", "{1}");
    // without edges nothing bounds a face
    check("FindPlanarColoring(Graph({1,2,3},{}))", "{}");

    // a non planar graph has no face structure at all
    check("PlanarGraphQ(CompleteGraph(5))", "False");
    check("Head(FindPlanarColoring(CompleteGraph(5)))", "FindPlanarColoring");
    check("Head(FindPlanarColoring(PetersenGraph()))", "FindPlanarColoring");

    // FindPlanarColoring(g, l) and FindPlanarColoring(g, {c1, c2, ...})
    check("FindPlanarColoring(CompleteGraph(4), 4)", "{1,2,3,4}");
    check("Head(FindPlanarColoring(CompleteGraph(4), 3))", "FindPlanarColoring");
    check("FindPlanarColoring(CycleGraph(4), {a,b})", "{a,b}");
    check("Head(FindPlanarColoring(CycleGraph(4), {a}))", "FindPlanarColoring");

    check("Head(FindPlanarColoring(5))", "FindPlanarColoring");
    check("Head(FindPlanarColoring(CycleGraph(4), -1))", "FindPlanarColoring");
  }

  @Test
  public void testFindVertexColoringColorCount() {
    // FindVertexColoring(g, l) uses the colors 1, 2, ..., l
    check("FindVertexColoring(CycleGraph(4), 2)", "{1,2,1,2}");
    check("FindVertexColoring(CycleGraph(4), 2)===FindVertexColoring(CycleGraph(4))", "True");
    check("FindVertexColoring(CompleteGraph(3), 3)", "{1,2,3}");
    // fewer colors than the chromatic number: no coloring exists, and Mathematica returns the
    // expression unevaluated rather than an empty list
    check("Head(FindVertexColoring(CycleGraph(5), 2))", "FindVertexColoring");
    check("Head(FindVertexColoring(CompleteGraph(3), 2))", "FindVertexColoring");
    check("Head(FindVertexColoring(CycleGraph(4), 0))", "FindVertexColoring");
    // more colors than needed: any coloring within the limit is a valid answer
    check("Max(FindVertexColoring(CycleGraph(4), 4))", "2");

    // FindVertexColoring(g, {c1, c2, ...}) uses the given colors
    check("FindVertexColoring(CycleGraph(4), {a,b})", "{a,b,a,b}");
    check("FindVertexColoring(CompleteGraph(3), {a,b,c})", "{a,b,c}");
    check("FindVertexColoring(CompleteGraph(3), {a,b,c,d})", "{a,b,c}");
    check("Head(FindVertexColoring(CompleteGraph(3), {a,b}))", "FindVertexColoring");
    check("Head(FindVertexColoring(CycleGraph(4), {}))", "FindVertexColoring");

    // Non-negative machine-sized integer expected at position `2` in `1`.
    check("Head(FindVertexColoring(CycleGraph(4), -1))", "FindVertexColoring");
    check("Head(FindVertexColoring(CycleGraph(4), x))", "FindVertexColoring");
  }

  @Test
  public void testFindVertexColoringOptions() {
    check("Options(FindVertexColoring)", //
        "{Method->Automatic,PerformanceGoal->$PerformanceGoal}");

    check("FindVertexColoring(CycleGraph(5), Method->\"BacktrackingDS\")", "{1,2,1,2,3}");
    check("Max(FindVertexColoring(CycleGraph(5), Method->\"ILP\"))", "3");
    // HybridEA is the DSATUR heuristic here, so it may use more colors than the chromatic number
    check("Max(FindVertexColoring(CycleGraph(5), Method->\"HybridEA\"))", "3");

    // the JGraphT algorithms, offered as Symja extensions
    check("FindVertexColoring(CycleGraph(6), Method->\"BrownBacktrack\")", "{1,2,1,2,1,2}");
    check("Max(FindVertexColoring(CycleGraph(6), Method->\"Greedy\"))", "2");
    check("Max(FindVertexColoring(CycleGraph(6), Method->\"SaturationDegree\"))", "2");
    check("Max(FindVertexColoring(CycleGraph(6), Method->\"LargestDegreeFirst\"))", "2");
    check("FindVertexColoring(StarGraph(5), Method->\"SmallestDegreeLast\")", "{1,2,2,2,2}");
    check("Max(FindVertexColoring(CompleteGraph(4), Method->\"Chordal\"))", "4");
    // CycleGraph(5) is not chordal, so ChordalGraphColoring declines and the exact search answers
    check("Max(FindVertexColoring(CycleGraph(5), Method->\"Chordal\"))", "3");

    // `1` is not a valid `2` specification.
    check("Head(FindVertexColoring(CycleGraph(5), Method->\"NoSuchThing\"))", //
        "FindVertexColoring");

    // PerformanceGoal is consulted only when no Method was named
    check("FindVertexColoring(CompleteGraph(4), PerformanceGoal->\"Speed\")", "{1,2,3,4}");
    check("Max(FindVertexColoring(CycleGraph(6), PerformanceGoal->\"Speed\"))", "2");
    check("Max(FindVertexColoring(CycleGraph(6), PerformanceGoal->\"Quality\"))", "2");

    // an option and a color count together
    check("FindVertexColoring(CycleGraph(4), 2, Method->\"BacktrackingDS\")", "{1,2,1,2}");
    // a heuristic that overshoots the requested color count must not report "no coloring"
    check("FindVertexColoring(CycleGraph(4), 2, Method->\"Greedy\")", "{1,2,1,2}");
  }

  // TODO
  // @Test
  // public void testCentralityMeasures() {
  // check("DegreeCentrality(CycleGraph(4))", "{2,2,2,2}");
  // check("DegreeCentrality(CompleteGraph(4))", "{3,3,3,3}");
  // check("DegreeCentrality(StarGraph(5))", "{4,1,1,1,1}");
  //
  // check("PageRankCentrality(CycleGraph(4))", "{1/4,1/4,1/4,1/4}");
  // check("PageRankCentrality(CompleteGraph(5))", "{1/5,1/5,1/5,1/5,1/5}");
  // check("Total(PageRankCentrality(StarGraph(5)))", "1");
  //
  // check("KatzCentrality(CycleGraph(4), 0)", "{1,1,1,1}");
  // check("KatzCentrality(Graph({1,2},{1<->2}), 1/10)", "{10/9,10/9}");
  // }

  @Test
  public void testGraphOperations() {
    check("VertexList(IndexGraph(Graph({x,y,z},{x<->z})))", "{1,2,3}");

    check("CompleteGraphQ(Subgraph(CompleteGraph(4), {1,2,3}))", //
        "True");
    check("EdgeCount(Subgraph(CompleteGraph(4), {1,2,3}))", //
        "3");

    check("VertexList(VertexDelete(CompleteGraph(4), 1))", "{2,3,4}");
    check("EdgeCount(EdgeDelete(CycleGraph(4), 1<->2))", "3");
    check("VertexCount(EdgeAdd(Graph({1,2},{1<->2}), 2<->3))", "3");
    check("VertexCount(VertexAdd(CompleteGraph(3), 4))", "4");

    check("CompleteGraphQ(NeighborhoodGraph(CompleteGraph(4), 1))", "True");
    check("EdgeCount(NeighborhoodGraph(CycleGraph(5), 1))", "2");

    check("EdgeList(EdgeContract(Graph({1,2,3},{1<->2,2<->3,3<->1}), 1<->2))", //
        "{1<->3}");
    check("EdgeList(VertexContract(Graph({1,2,3},{1<->2,2<->3,3<->1}), {1,2}))", //
        "{1<->3}");
  }

  // TODO
  // @Test
  // public void testSpecialGraphs() {
  // check("VertexCount(TuranGraph(7,3))", "7");
  // check("EdgeCount(TuranGraph(6,3))", "12");
  //
  // check("VertexCount(CompleteKaryTree(2))", "3");
  // check("EdgeCount(CompleteKaryTree(3))", "6");
  //
  // check("EdgeCount(CirculantGraph(5,{1}))", "5");
  // check("CompleteGraphQ(CirculantGraph(6,{1,2,3}))", "True");
  //
  // check("EdgeCount(LadderGraph(3))", "7");
  // check("BipartiteGraphQ(LadderGraph(4))", "True");
  //
  // check("EdgeCount(CocktailPartyGraph(3))", "12");
  // check("VertexCount(CocktailPartyGraph(3))", "6");
  //
  // check("VertexCount(KneserGraph(5,2))", "10");
  // check("EdgeCount(KneserGraph(5,2))", "15");
  //
  // check("VertexCount(GeneralizedPetersenGraph(5,2))", "10");
  // check("EdgeCount(GeneralizedPetersenGraph(5,2))", "15");
  //
  // check("VertexCount(FriendshipGraph(2))", "5");
  // check("EdgeCount(FriendshipGraph(2))", "6");
  //
  // check("VertexCount(AntiprismGraph(3))", "6");
  // check("EdgeCount(AntiprismGraph(3))", "12");
  //
  // check("VertexCount(PrismGraph(3))", "6");
  // check("EdgeCount(PrismGraph(3))", "9");
  //
  // check("VertexCount(SunletGraph(3))", "6");
  // check("EdgeCount(SunletGraph(3))", "6");
  //
  // check("VertexCount(HelmGraph(3))", "7");
  // check("EdgeCount(HelmGraph(3))", "9");
  //
  // check("VertexCount(GearGraph(3))", "7");
  // check("EdgeCount(GearGraph(3))", "9");
  //
  // check("VertexCount(DodecahedralGraph())", "20");
  // check("EdgeCount(DodecahedralGraph())", "30");
  //
  // check("VertexCount(IcosahedralGraph())", "12");
  // check("EdgeCount(IcosahedralGraph())", "30");
  // }

  // TODO
  // @Test
  // public void testAdvancedAlgorithms() {
  // check("FindDominatingSet(StarGraph(5))", "{1}");
  // check("Length(FindDominatingSet(PathGraph(4)))", "2");
  //
  // check("Length(FindEdgeCover(PathGraph(4)))", "2");
  // check("Length(FindEdgeCover(StarGraph(5)))", "4");
  //
  // check("Length(FindIndependentEdgeSet(CompleteGraph(4)))", "2");
  // check("Length(FindIndependentEdgeSet(CompleteGraph(6)))", "3");
  //
  // check("KCoreComponents(CompleteGraph(4), 3)", "{{1,2,3,4}}");
  // check("KCoreComponents(CompleteGraph(4), 4)", "{}");
  // check("VertexCoreness(CompleteGraph(4))", "{3,3,3,3}");
  // check("Max(VertexCoreness(CompleteGraph(4)))", "3");
  //
  // check("IncidenceList(CycleGraph(4), 1)", "{1<->2,4<->1}");
  // check("Length(IncidenceList(StarGraph(5), 1))", "4");
  //
  // check("VertexOutComponent(Graph({1,2,3},{1->2,2->3}), 1)", "{1,2,3}");
  // check("VertexInComponent(Graph({1,2,3},{1->2,2->3}), 3)", "{1,2,3}");
  // }

  // TODO
  // @Test
  // public void testGraphPredicates() {
  // check("TreeGraphQ(PathGraph(4))", "True");
  // check("TreeGraphQ(CycleGraph(4))", "False");
  //
  // check("StronglyConnectedGraphQ(Graph({1,2,3},{1->2,2->3,3->1}))", "True");
  // check("StronglyConnectedGraphQ(Graph({1,2,3},{1->2,2->3}))", "False");
  //
  // check("RegularGraphQ(CycleGraph(5))", "True");
  // check("RegularGraphQ(PathGraph(4))", "False");
  //
  // check("CompleteGraphQ(CompleteGraph(4))", "True");
  // check("CompleteGraphQ(CycleGraph(4))", "False");
  //
  // check("PathGraphQ(PathGraph(4))", "True");
  // check("PathGraphQ(CycleGraph(4))", "False");
  //
  // check("EmptyGraphQ(Graph({1,2,3},{}))", "True");
  // check("EmptyGraphQ(CycleGraph(3))", "False");
  //
  // check("MixedGraphQ(Graph({1,2,3},{1->2,2<->3}))", "True");
  // check("MixedGraphQ(Graph({1,2,3},{1->2,2->3}))", "False");
  // }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
