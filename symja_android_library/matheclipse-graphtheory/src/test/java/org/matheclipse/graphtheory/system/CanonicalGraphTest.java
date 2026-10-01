package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/**
 * <code>CanonicalGraph</code>, <code>GraphAutomorphismGroup</code>, the options of
 * <code>FindMaximumFlow</code> and the Hamiltonian cycle of the knight's graph.
 */
public class CanonicalGraphTest extends AbstractTestCase {

  @Test
  public void testCanonicalGraph() {
    // True - isomorphic graphs have the same canonical graph
    check(
        "CanonicalGraph(CycleGraph(4)) === "
            + "CanonicalGraph(Graph({a <-> b, b <-> d, d <-> c, c <-> a}))", //
        "True");
    check("CanonicalGraph(PathGraph({3, 1, 2})) === CanonicalGraph(PathGraph({x, y, z}))", //
        "True");
    check(
        "CanonicalGraph(PetersenGraph()) === CanonicalGraph(Graph(Range(10), "
            + "EdgeList(PetersenGraph()) /. Thread(Range(10) -> {7,3,9,1,5,10,2,8,4,6})))", //
        "True");
    check("CanonicalGraph(Graph({1 -> 2, 2 -> 3})) === CanonicalGraph(Graph({c -> b, b -> a}))", //
        "True");
    // graphs which aren't isomorphic
    check("CanonicalGraph(CycleGraph(5)) === CanonicalGraph(PathGraph({1,2,3,4,5}))", //
        "False");
    check("CanonicalGraph(Graph({1 -> 2, 2 -> 3})) === CanonicalGraph(Graph({1 -> 2, 3 -> 2}))", //
        "False");
    check("VertexList(CanonicalGraph(Graph({a <-> b, b <-> d, d <-> c, c <-> a})))", //
        "{1,2,3,4}");
  }

  @Test
  public void testGraphAutomorphismGroup() {
    // GroupOrder(GraphAutomorphismGroup(PetersenGraph())) == 120
    check("GroupOrder(GraphAutomorphismGroup(PetersenGraph()))", //
        "120");
    // the dihedral group, the symmetric groups, the hyperoctahedral group and the wreath product
    // S3 wr S2; a directed cycle only has its rotations
    check(
        "GroupOrder /@ GraphAutomorphismGroup /@ {CycleGraph(6), CompleteGraph(5), "
            + "StarGraph(5), PathGraph({1,2,3,4}), HypercubeGraph(4), CompleteGraph({3,3}), "
            + "Graph({1->2,2->3,3->1})}", //
        "{12,120,24,2,384,72,3}");
    check("GroupOrder(GraphAutomorphismGroup(CompleteGraph(8)))", //
        "40320");
    check("Head(GraphAutomorphismGroup(CycleGraph(4)))", //
        "PermutationGroup");
  }

  @Test
  public void testFindMaximumFlowOptions() {
    // 5 and {{0, 3, 2, 0}, {0, 0, 1, 2}, {0, 0, 0, 3}, {0, 0, 0, 0}}
    check(
        "net = Graph({s, a, b, t}, {s -> a, s -> b, a -> b, a -> t, b -> t}); "
            + "FindMaximumFlow(net, s, t, EdgeCapacity -> {3, 2, 1, 2, 3})", //
        "5");
    check("Normal(FindMaximumFlow(net, s, t, \"FlowMatrix\", EdgeCapacity -> {3, 2, 1, 2, 3}))", //
        "{{0,3,2,0},{0,0,1,2},{0,0,0,3},{0,0,0,0}}");
    // without the option every edge has the capacity 1
    check("FindMaximumFlow(net, s, t)", //
        "2");
  }

  @Test
  public void testKnightsTour() {
    // plain backtracking in EdgeList order gives up on the knight's graph; Warnsdorff's rule finds
    // a closed tour of the 6 x 6 and of the 8 x 8 board
    check(
        "moves = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}}; "
            + "knight(n_) := Graph(Flatten(Table(If(1 <= i + m[[1]] <= n && 1 <= j + m[[2]] <= n, "
            + "{i, j} <-> {i + m[[1]], j + m[[2]]}, Nothing), {i, n}, {j, n}, {m, moves}))); "
            + "c = FindHamiltonianCycle(knight(8)); {Length(c), Length(First(c))}", //
        "{1,64}");
    // every square is entered once and left once
    check("Union(Tally(Flatten(List @@@ First(c), 1))[[All, 2]])", //
        "{2}");
    check("Length(First(FindHamiltonianCycle(knight(6))))", //
        "36");
    // 25 squares: the graph is bipartite with an odd number of vertices
    check("FindHamiltonianCycle(knight(5))", //
        "{}");
  }
}
