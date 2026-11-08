package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** The default arrowheads of a drawn directed graph, and <code>Show</code> of a graph. */
public class GraphArrowheadsTest extends AbstractTestCase {

  @Test
  public void testLargeGraphHeadsAreTwoAndAHalfVertexRadii() {
    check("g=Show(Graph(Table(i->Mod(3*i+1,40),{i,40}))); "
        + "h=First(Cases(g,Arrowheads(a_):>a,Infinity)); "
        + "r=First(Cases(g,Disk(p_,a_):>a,Infinity)); c=Cases(g,Disk(p_,a_):>p,Infinity); "
        + "Round(h*(Max(c[[All,1]])-Min(c[[All,1]]))/r,0.001)", //
        "2.5");
  }

  @Test
  public void testSmallGraphHeadsAreMedium() {
    check("Cases(Show(Graph({1->2,2->3,3->1})),_Arrowheads,Infinity)", //
        "{Arrowheads(Medium)}");
    check("Head(Show(Graph({1<->2}),Graph({3<->4})))", //
        "Graphics");
  }
}
