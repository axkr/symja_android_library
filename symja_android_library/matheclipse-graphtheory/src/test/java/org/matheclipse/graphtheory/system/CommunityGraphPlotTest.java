package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** <code>CommunityGraphPlot</code> with the communities given. */
public class CommunityGraphPlotTest extends AbstractTestCase {

  /** Two squares joined by one edge, and a lone vertex no community names. */
  private static final String GRAPH = "Graph({a<->b, b<->c, c<->d, d<->a, e<->f, f<->g, g<->h, "
      + "h<->e, d<->e, a<->k})";

  @Test
  public void testGraphics() {
    check("Head(CommunityGraphPlot(" + GRAPH + ", {{a,b,c,d}, {e,f,g,h}}))", //
        "Graphics");
    // one grey outline for each community, the lone vertex k included
    check("Count(CommunityGraphPlot(" + GRAPH + ", {{a,b,c,d}, {e,f,g,h}}), "
        + "{EdgeForm(Directive(GrayLevel(0.5))), FaceForm(None), _Polygon}, Infinity)", //
        "3");
    // the vertices of a community share its colour
    check("Cases(CommunityGraphPlot(" + GRAPH + ", {{a,b,c,d}, {e,f,g,h}}), "
        + "{Directive(c_Hue, _EdgeForm), _Disk} :> c, Infinity)", //
        "{Hue(0.0,1.0,0.8),Hue(0.0,1.0,0.8),Hue(0.0,1.0,0.8),Hue(0.0,1.0,0.8),"
            + "Hue(0.14,1.0,0.9),Hue(0.14,1.0,0.9),Hue(0.14,1.0,0.9),Hue(0.14,1.0,0.9),"
            + "Hue(0.8,0.6,0.8)}");
    // every edge is drawn
    check("Count(CommunityGraphPlot(" + GRAPH + ", {{a,b,c,d}, {e,f,g,h}}), _Line, Infinity)", //
        "10");
  }

  @Test
  public void testRegionStyleAndSvg() {
    check("Cases(CommunityGraphPlot(Graph({1<->2, 2<->3, 3<->4}), {{1,2}, {3,4}}, "
        + "CommunityRegionStyle -> LightBlue, CommunityBoundaryStyle -> Red), "
        + "{EdgeForm(b_), FaceForm(r_), _Polygon} :> {b, r}, Infinity)", //
        "{{RGBColor(1,0,0),RGBColor(0.87,0.94,1.0)},{RGBColor(1,0,0),RGBColor(0.87,0.94,1.0)}}");
    check("StringCount(ExportString(CommunityGraphPlot(Graph({1<->2, 2<->3, 3<->4, 4<->5}), "
        + "{{1,2}, {3,4,5}}), \"SVG\"), \"stroke=\\\"rgb(128,128,128)\\\"\")", //
        "2");
  }

  @Test
  public void testUnevaluated() {
    // a vertex which isn't one of the graph's
    check("CommunityGraphPlot(Graph({1<->2, 2<->3}), {{1,2}, {7}})", //
        "CommunityGraphPlot(Graph({1,2,3},{1<->2,2<->3}),{{1,2},{7}})");
    check("CommunityGraphPlot(Graph({1<->2, 2<->3}), {1,2})", //
        "CommunityGraphPlot(Graph({1,2,3},{1<->2,2<->3}),{1,2})");
    check("CommunityGraphPlot(x)", //
        "CommunityGraphPlot(x)");
  }
}
