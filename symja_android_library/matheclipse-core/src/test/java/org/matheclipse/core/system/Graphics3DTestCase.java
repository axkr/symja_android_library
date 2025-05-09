package org.matheclipse.core.system;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

/** View with: http://www.rapidtables.com/web/tools/svg-viewer-editor.htm */
public class Graphics3DTestCase extends ExprEvaluatorTestCase {

  @Ignore
  @Test
  public void testPoint001() {
    // checkSVGGraphics(
    // "Show(Graphics3D(Point({3,2,0})))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [1.3, -2.4, 2.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[3.0, 2.0, 0.0], null]], &quot;type&quot;: &quot;point&quot;,
    // &quot;faceColor&quot;: [1, 1, 1, 1]}], &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2,
    // 0.4], &quot;type&quot;: &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0],
    // &quot;position&quot;: [2.0, 0.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.8, 0.0], &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0,
    // 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;}], &quot;axes&quot;:
    // {&quot;hasaxes&quot;: [false, false, false], &quot;ticks&quot;: [[[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4, 0.6000000000000001,
    // 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003,
    // 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9,
    // 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;,
    // &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05,
    // 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55,
    // 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001],
    // [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;,
    // &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;: 1.0, &quot;ymax&quot;: 1.0,
    // &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;: 0.0, &quot;ymin&quot;:
    // 0.0}}\" />");
    // checkSVGGraphics(
    // "Show(Graphics3D(Point({{3,2,0}})))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [1.3, -2.4, 2.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[3.0, 2.0, 0.0], null]], &quot;type&quot;: &quot;point&quot;,
    // &quot;faceColor&quot;: [1, 1, 1, 1]}], &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2,
    // 0.4], &quot;type&quot;: &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0],
    // &quot;position&quot;: [2.0, 0.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.8, 0.0], &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0,
    // 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;}], &quot;axes&quot;:
    // {&quot;hasaxes&quot;: [false, false, false], &quot;ticks&quot;: [[[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4, 0.6000000000000001,
    // 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003,
    // 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9,
    // 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;,
    // &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05,
    // 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55,
    // 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001],
    // [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;,
    // &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;: 1.0, &quot;ymax&quot;: 1.0,
    // &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;: 0.0, &quot;ymin&quot;:
    // 0.0}}\" />");
    //
    // checkSVGGraphics(
    // "Show(Graphics3D(Point({{0,0,0}, {0,1,1}, {1,0,0}}), ViewPoint->Front))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [0.0, -2.0, 0.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[0.0, 0.0, 0.0], null], [[0.0, 1.0, 1.0], null], [[1.0, 0.0, 0.0],
    // null]], &quot;type&quot;: &quot;point&quot;, &quot;faceColor&quot;: [1, 1, 1, 1]}],
    // &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2, 0.4], &quot;type&quot;:
    // &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0], &quot;position&quot;: [2.0, 0.0,
    // 2.0], &quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.8, 0.0],
    // &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}], &quot;axes&quot;: {&quot;hasaxes&quot;: [false, false, false],
    // &quot;ticks&quot;: [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1,
    // 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65,
    // 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;,
    // &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0,
    // 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25,
    // 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75,
    // 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;,
    // &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;:
    // 1.0, &quot;ymax&quot;: 1.0, &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;:
    // 0.0, &quot;ymin&quot;: 0.0}}\" />");
    // checkSVGGraphics(
    // "Show(Graphics3D(Point({{0,0,0}, {0,1,1}, {1,0,0}}), ViewPoint->{0.0, -2.0, 0.0}))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [0.0, -2.0, 0.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[0.0, 0.0, 0.0], null], [[0.0, 1.0, 1.0], null], [[1.0, 0.0, 0.0],
    // null]], &quot;type&quot;: &quot;point&quot;, &quot;faceColor&quot;: [1, 1, 1, 1]}],
    // &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2, 0.4], &quot;type&quot;:
    // &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0], &quot;position&quot;: [2.0, 0.0,
    // 2.0], &quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.8, 0.0],
    // &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}], &quot;axes&quot;: {&quot;hasaxes&quot;: [false, false, false],
    // &quot;ticks&quot;: [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1,
    // 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65,
    // 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;,
    // &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0,
    // 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25,
    // 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75,
    // 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;,
    // &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;:
    // 1.0, &quot;ymax&quot;: 1.0, &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;:
    // 0.0, &quot;ymin&quot;: 0.0}}\" />");
  }

  @Ignore
  @Test
  public void testPolygon001() {
    //
    // checkSVGGraphics(
    // "Show(Graphics3D(Polygon({{0,0,0}, {0,1,1}, {1,0,0}}), ViewPoint->Front))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [0.0, -2.0, 0.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[0.0, 0.0, 0.0], null], [[0.0, 1.0, 1.0], null], [[1.0, 0.0, 0.0],
    // null]], &quot;type&quot;: &quot;polygon&quot;, &quot;faceColor&quot;: [1, 1, 1, 1]}],
    // &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2, 0.4], &quot;type&quot;:
    // &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0], &quot;position&quot;: [2.0, 0.0,
    // 2.0], &quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.8, 0.0],
    // &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}], &quot;axes&quot;: {&quot;hasaxes&quot;: [false, false, false],
    // &quot;ticks&quot;: [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1,
    // 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65,
    // 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;,
    // &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0,
    // 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25,
    // 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75,
    // 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;,
    // &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;:
    // 1.0, &quot;ymax&quot;: 1.0, &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;:
    // 0.0, &quot;ymin&quot;: 0.0}}\" />");
    // checkSVGGraphics(
    // "Show(Graphics3D(Polygon({{0,0,0}, {0,1,1}, {1,0,0}}), ViewPoint->{0.0, -2.0, 0.0}))",
    // "<graphics3d data=\"{&quot;viewpoint&quot;: [0.0, -2.0, 0.0], &quot;elements&quot;:
    // [{&quot;coords&quot;: [[[0.0, 0.0, 0.0], null], [[0.0, 1.0, 1.0], null], [[1.0, 0.0, 0.0],
    // null]], &quot;type&quot;: &quot;polygon&quot;, &quot;faceColor&quot;: [1, 1, 1, 1]}],
    // &quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2, 0.4], &quot;type&quot;:
    // &quot;Ambient&quot;}, {&quot;color&quot;: [0.8, 0.0, 0.0], &quot;position&quot;: [2.0, 0.0,
    // 2.0], &quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.8, 0.0],
    // &quot;position&quot;: [2.0, 2.0, 2.0], &quot;type&quot;: &quot;Directional&quot;},
    // {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0, 2.0, 2.0], &quot;type&quot;:
    // &quot;Directional&quot;}], &quot;axes&quot;: {&quot;hasaxes&quot;: [false, false, false],
    // &quot;ticks&quot;: [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1,
    // 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65,
    // 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;,
    // &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0,
    // 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25,
    // 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75,
    // 0.8500000000000001, 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;,
    // &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]], [[0.0, 0.2, 0.4,
    // 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004,
    // 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001,
    // 0.9, 0.9500000000000001], [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;,
    // &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]]]}, &quot;extent&quot;: {&quot;zmax&quot;:
    // 1.0, &quot;ymax&quot;: 1.0, &quot;zmin&quot;: 0.0, &quot;xmax&quot;: 1.0, &quot;xmin&quot;:
    // 0.0, &quot;ymin&quot;: 0.0}}\" />");
  }
}
