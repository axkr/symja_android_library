package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;


public class FindSpanningTreeTest extends AbstractTestCase {

  @Test
  public void testUndirectedWeighted() {
    // Kruskal, the input's vertex order and undirected edges sorted by position
    check(
        "t = FindSpanningTree(Graph({a,b,c,d,e}, {a<->b, a<->c, b<->c, b<->d, c<->d, d<->e}, "
            + "EdgeWeight -> {4,1,2,5,8,3})); {EdgeList(t), VertexList(t)}", //
        "{{a<->c,b<->c,b<->d,d<->e},{a,b,c,d,e}}");
    check(
        "FindSpanningTree(Graph({a,b,c,d,e}, {a<->b, a<->c, b<->c, b<->d, c<->d, d<->e}, "
            + "EdgeWeight -> {4,1,2,5,8,3}))", //
        "Graph({a,b,c,d,e},{a<->c,b<->c,b<->d,d<->e},{EdgeWeight->{1,2,5,3}})");
    check(
        "EdgeList(FindSpanningTree({Graph({a,b,c,d,e}, {a<->b, a<->c, b<->c, b<->d, c<->d, d<->e}, "
            + "EdgeWeight -> {4,1,2,5,8,3}), c}))", //
        "{a<->c,b<->c,b<->d,d<->e}");
    // a minimum spanning forest, exact weights carried over
    check(
        "FindSpanningTree(Graph({1,2,3,4,5,6}, {1<->2, 2<->3, 1<->3, 4<->5, 5<->6, 4<->6}, "
            + "EdgeWeight -> {3, 1, 2, 5, 1/2, 7}))", //
        "Graph({1,2,3,4,5,6},{1<->3,2<->3,4<->5,5<->6},{EdgeWeight->{2,1,5,1/2}})");
    // exact comparison: the machine number 0.3333333333333333 is below 1/3
    check(
        "FindSpanningTree(Graph({1,2,3},{1<->2, 2<->3, 1<->3}, "
            + "EdgeWeight->{1/3, 0.3333333333333333, 1/2}))", //
        "Graph({1,2,3},{1<->2,2<->3},{EdgeWeight->{1/3,0.333333}})");
    check("{EdgeList(FindSpanningTree(Graph({1,2,3},{1<->2, 2<->3, 1<->3}, "
        + "EdgeWeight->{Sqrt(2), 3/2, 1.4}))), EdgeList(FindSpanningTree(Graph({1<->2, 2<->3, 1<->3}, "
        + "EdgeWeight -> {-1, 5, 3})))}", //
        "{{1<->2,1<->3},{1<->2,1<->3}}");
    // ties broken by vertex position; an edge is written lower position first
    check(
        "EdgeList(FindSpanningTree(Graph({1,2,3,4,5}, {1<->2, 2<->3, 3<->4, 4<->5, 5<->1}, "
            + "EdgeWeight -> {1,1,1,1,1})))", //
        "{1<->2,1<->5,2<->3,3<->4}");
    check("EdgeList(FindSpanningTree(Graph({c,a,b},{b<->c, a<->b, c<->a}, EdgeWeight->{3,2,1})))", //
        "{c<->a,a<->b}");
    // a weight which isn't a real number, and a mixed graph, stay unevaluated
    check(
        "Head(FindSpanningTree(Graph({a,b,c,d}, {a<->b, b<->c, c<->d, a<->d}, "
            + "EdgeWeight->{x,1,2,3})))", //
        "FindSpanningTree");
    check("Head(FindSpanningTree(Graph({1<->2, 2->3})))", //
        "FindSpanningTree");
  }

  @Test
  public void testUndirectedUnweighted() {
    // WMA: a BFS tree
    check("{EdgeList(FindSpanningTree(Graph({1,2,3,4}, {1<->2, 2<->3, 3<->4, 4<->1, 1<->3}))), "
        + "EdgeList(FindSpanningTree({Graph({1,2,3,4}, {1<->2, 2<->3, 3<->4, 4<->1, 1<->3}), 3})), "
        + "EdgeList(FindSpanningTree(CycleGraph(4)))}", //
        "{{1<->2,1<->3,1<->4},{1<->3,2<->3,3<->4},{1<->2,1<->4,2<->3}}");
    // a bare list of edges is a graph, not {graph, root}
    check("EdgeList(FindSpanningTree({1<->2, 2<->3}))", //
        "{1<->2,2<->3}");
  }

  @Test
  public void testDirectedWeighted() {
    // WMA: the minimum arborescence, rooted at 2 (rooted at 1 the best is 9)
    check(
        "FindSpanningTree(Graph({1,2,3,4}, {1->2, 2->3, 1->3, 3->4, 4->1}, "
            + "EdgeWeight -> {5,1,2,3,4}))", //
        "Graph({1,2,3,4},{2->3,3->4,4->1},{EdgeWeight->{1,3,4}})");
    check(
        "EdgeList(FindSpanningTree({Graph({1,2,3,4}, {1->2, 2->3, 1->3, 3->4, 4->1}, "
            + "EdgeWeight -> {5,1,2,3,4}), 1}))", //
        "{1->2,2->3,3->4}");
    // the heaviest arc of a cycle is dropped; the fewest roots beat a lighter forest
    check(
        "EdgeList(FindSpanningTree(Graph({1,2,3,4},{2->1, 3->4, 1->3, 4->2}, EdgeWeight->{1,1,5,1})))", //
        "{2->1,3->4,4->2}");
    check(
        "FindSpanningTree(Graph({1,2,3,4,5},{1->2, 2->3, 3->1, 4->5, 5->4, 3->4}, "
            + "EdgeWeight->{1,2,3,4,5,10}))", //
        "Graph({1,2,3,4,5},{1->2,2->3,3->4,4->5},{EdgeWeight->{1,2,10,4}})");
    // a spanning branching where no single root reaches everything, and WMA's tie choices
    check(
        "{EdgeList(FindSpanningTree(Graph({1,2,3,4},{1->2, 3->2, 3->4}, EdgeWeight->{1,1,2}))), "
            + "EdgeList(FindSpanningTree(Graph({1,2,3,4},{1->2, 3->4}, EdgeWeight->{3,1})))}", //
        "{{3->2,3->4},{1->2,3->4}}");
    check("{EdgeList(FindSpanningTree(Graph({1,2,3,4},{1->2, 2->3, 3->4, 4->1, 1->3}, "
        + "EdgeWeight->{1,1,1,1,1}))), EdgeList(FindSpanningTree(Graph({1,2,3,4},{2->1, 2->3, 3->4, 4->2}, "
        + "EdgeWeight->{1,1,1,1})))}", //
        "{{2->3,3->4,4->1},{2->1,3->4,4->2}}");
    // rooted: only what the root reaches
    check("FindSpanningTree({Graph({1,2,3,4},{1->2, 3->2, 3->4}, EdgeWeight->{1,1,2}), 3})", //
        "Graph({2,3,4},{3->2,3->4},{EdgeWeight->{1,2}})");
  }

  @Test
  public void testDirectedUnweighted() {
    // a BFS branching rooted in decreasing DFS finishing time
    check(
        "{EdgeList(FindSpanningTree(Graph({1,2,3,4},{1->2,2->3,3->1}))), "
            + "EdgeList(FindSpanningTree(Graph({1,2,3,4},{1->2, 3->2, 3->4}))), "
            + "EdgeList(FindSpanningTree(Graph({1,2,3,4},{2->1, 2->3, 3->4, 4->2})))}", //
        "{{1->2,2->3},{3->2,3->4},{2->1,2->3,3->4}}");
  }
}
