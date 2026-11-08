package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** <code>FindGraphCommunities</code>: the communities of the largest modularity. */
public class FindGraphCommunitiesTest extends AbstractTestCase {

  @Test
  public void testDenseGroupsJoinedByOneEdge() {
    check("FindGraphCommunities(Graph({a<->b,b<->c,c<->d,d<->a,a<->c,d<->e,e<->f,f<->g,g<->h,"
        + "h<->e,e<->g}))", //
        "{{a,b,c,d},{e,f,g,h}}");
    check("FindGraphCommunities(PathGraph(Range(8)))", //
        "{{1,2,3,4},{5,6,7,8}}");
  }

  @Test
  public void testDirectionsAreIgnored() {
    check("FindGraphCommunities(Graph({1->2,2->3,3->1,3->4,4->5,5->6,6->4}))", //
        "{{1,2,3},{4,5,6}}");
  }

  @Test
  public void testLargestFirstAndLoneVertices() {
    check("FindGraphCommunities({1<->2,2<->3,3<->1,7<->8})", //
        "{{1,2,3},{7,8}}");
    check("FindGraphCommunities(Graph({x,y,z},{}))", //
        "{{x},{y},{z}}");
    check("FindGraphCommunities(x)", //
        "FindGraphCommunities(x)");
  }

  @Test
  public void testEdgeWeights() {
    // a light middle edge splits the path, unlike the unweighted path
    check("FindGraphCommunities(Graph({p<->q, q<->r, r<->s, s<->t, t<->u}, "
        + "EdgeWeight -> {8, 8, 0.2, 8, 8}))", //
        "{{p,q,r},{s,t,u}}");
    check("FindGraphCommunities(Graph({p<->q, q<->r, r<->s, s<->t, t<->u}))", //
        "{{p,q},{r,s},{t,u}}");
  }
}
