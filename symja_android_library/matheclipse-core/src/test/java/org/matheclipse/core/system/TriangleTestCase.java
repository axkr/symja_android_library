package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for <code>TriangleCenter</code>, <code>TriangleConstruct</code> and
 * <code>TriangleMeasurement</code>.
 */
public class TriangleTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testTriangleCenter() {
    // Default is Centroid
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}))", //
        "{4/3,1}");
    check("TriangleCenter(Triangle())", //
        "{1/3,1/3}");

    // Centroid
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Centroid\")", //
        "{4/3,1}");
    check("TriangleCenter(Triangle({{a, b}, {c, d}, {e, f}}))", //
        "{1/3*(a+c+e),1/3*(b+d+f)}");

    // Incenter
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Incenter\")", //
        "{1,1}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {0, 1}}), \"Incenter\")", //
        "{1/(2+Sqrt(2)),1/(2+Sqrt(2))}");

    // Circumcenter
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Circumcenter\")", //
        "{2,3/2}");
    check("TriangleCenter(Triangle({{-1, 0}, {5, 1}, {2, 4}}), \"Circumcenter\")", //
        "{27/14,13/14}");

    // Orthocenter
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Orthocenter\")", //
        "{0,0}");
    check("TriangleCenter(Triangle({{-1, 0}, {5, 1}, {2, 4}}), \"Orthocenter\")", //
        "{15/7,22/7}");

    // NinePointCenter
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"NinePointCenter\")", //
        "{1,3/4}");
    check("TriangleCenter(Triangle({{-1, 0}, {5, 1}, {2, 4}}), \"NinePointCenter\")", //
        "{57/28,57/28}");

    // SymmedianPoint
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"SymmedianPoint\")", //
        "{18/25,24/25}");

    // Equilateral centers coincide
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Incenter\")", //
        "{1/2,1/(2*Sqrt(3))}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Circumcenter\")", //
        "{1/2,1/(2*Sqrt(3))}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Orthocenter\")", //
        "{1/2,1/(2*Sqrt(3))}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"NinePointCenter\")", //
        "{1/2,1/(2*Sqrt(3))}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"SymmedianPoint\")", //
        "{1/2,1/(2*Sqrt(3))}");

    // Bare vertex list
    check("TriangleCenter({{0, 0}, {4, 0}, {0, 3}}, \"Incenter\")", //
        "{1,1}");

    // 3D Triangles
    check("TriangleCenter(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"Circumcenter\")", //
        "{2,3/2,0}");

    // Float vertices
    check("TriangleCenter(Triangle({{0., 0.}, {4., 0.}, {0., 3.}}), \"Circumcenter\")", //
        "{2.0,1.5}");

    // Vertex dependent centers; the vertex defaults to the second one
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Excenter\", All})", //
        "{{6,6},{-2,2},{3,-3}}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Excenter\")", //
        "{-2,2}");
    check("TriangleCenter(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), {\"Excenter\", All})", //
        "{{3/2,Sqrt(3)/2},{-1/2,Sqrt(3)/2},{1/2,-Sqrt(3)/2}}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", All})", //
        "{{2,3/2},{0,3/2},{2,0}}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Foot\", All})", //
        "{{36/25,48/25},{0,0},{0,0}}");
    check(
        "TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"AngleBisectingCevianEndpoint\", All})", //
        "{{12/7,12/7},{0,4/3},{3/2,0}}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"SymmedianEndpoint\", All})", //
        "{{36/25,48/25},{0,48/41},{18/17,0}}");

    // A vertex can be given as an index, as coordinates or as a Point
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", 2})", //
        "{0,3/2}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", {4, 0}})", //
        "{0,3/2}");
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", Point({4, 0})})", //
        "{0,3/2}");

    // CevianEndpoint accepts a plain center name or a vertex dependent center
    check(
        "TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"CevianEndpoint\", \"Incenter\", 1})", //
        "{12/7,12/7}");
    check(
        "TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"CevianEndpoint\", \"Centroid\", 1})", //
        "{2,3/2}");
    check(
        "TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"CevianEndpoint\", {\"Excenter\", 1}, 2})", //
        "{0,-12}");

    // Unevaluated cases
    // message TriangleCenter: Foo is not a valid center specification.
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Foo\")", //
        "TriangleCenter(Triangle({{0,0},{4,0},{0,3}}),Foo)");
    // message TriangleCenter: {Foo,1} is not a valid center specification.
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Foo\", 1})", //
        "TriangleCenter(Triangle({{0,0},{4,0},{0,3}}),{Foo,1})");
    // message TriangleCenter: 7 is not a valid vertex specification.
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", 7})", //
        "TriangleCenter(Triangle({{0,0},{4,0},{0,3}}),{Midpoint,7})");
    check("TriangleCenter(foo)", //
        "TriangleCenter(foo)");
  }

  @Test
  public void testTriangleMeasurement() {
    // TriangleMeasurement(tri) is equivalent to Area(Triangle(tri))
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}))", //
        "6");
    check("TriangleMeasurement(Triangle())", //
        "1/2");
    check("TriangleMeasurement(Triangle({{0, 0}, {x, 0}, {0, y}}), \"Area\")", //
        "Abs(x*y)/2");

    // The 3-4-5 right triangle
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Area\")", //
        "6");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Perimeter\")", //
        "12");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Semiperimeter\")", //
        "6");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Inradius\")", //
        "1");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Circumradius\")", //
        "5/2");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"NinePointRadius\")", //
        "5/4");

    // Vertex dependent measurements; the vertex defaults to the second one
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Exradius\", All})", //
        "{6,2,3}");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Exradius\")", //
        "2");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Height\", All})", //
        "{12/5,4,3}");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"InteriorAngle\", All})", //
        "{Pi/2,ArcCos(4/5),ArcCos(3/5)}");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"ExteriorAngle\", All})", //
        "{Pi/2,Pi-ArcCos(4/5),Pi-ArcCos(3/5)}");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"FullExteriorAngle\", All})", //
        "{3/2*Pi,2*Pi-ArcCos(4/5),2*Pi-ArcCos(3/5)}");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"InteriorAngle\", {4, 0}})", //
        "ArcCos(4/5)");
    check("N(TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"InteriorAngle\", All}))", //
        "{1.5708,0.643501,0.927295}");

    // Equilateral triangle
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Area\")", //
        "Sqrt(3)/4");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Perimeter\")", //
        "3");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Inradius\")", //
        "1/(2*Sqrt(3))");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Circumradius\")", //
        "1/Sqrt(3)");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), {\"Height\", 1})", //
        "Sqrt(3)/2");
    check(
        "TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), {\"InteriorAngle\", 1})", //
        "Pi/3");

    // 3D triangles are measured in their own plane
    check("TriangleMeasurement(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"Area\")", //
        "6");
    check("TriangleMeasurement(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"Circumradius\")", //
        "5/2");
    check("TriangleMeasurement(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), {\"Height\", All})", //
        "{12/5,4,3}");

    // Other triangle representations
    check("TriangleMeasurement({{0, 0}, {4, 0}, {0, 3}}, \"Inradius\")", //
        "1");
    check("TriangleMeasurement(Polygon({{0, 0}, {4, 0}, {0, 3}}), \"Inradius\")", //
        "1");
    check("TriangleMeasurement(SSSTriangle(3, 4, 5), \"Inradius\")", //
        "1");

    // Unevaluated cases
    // message TriangleMeasurement: Foo is not a valid measurement specification.
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Foo\")", //
        "TriangleMeasurement(Triangle({{0,0},{4,0},{0,3}}),Foo)");
    // message TriangleMeasurement: 42 is not a valid measurement specification.
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), 42)", //
        "TriangleMeasurement(Triangle({{0,0},{4,0},{0,3}}),42)");
    // message TriangleMeasurement: 7 is not a valid vertex specification.
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Height\", 7})", //
        "TriangleMeasurement(Triangle({{0,0},{4,0},{0,3}}),{Height,7})");
    check("TriangleMeasurement({{0, 0}, {4, 0}}, \"Area\")", //
        "TriangleMeasurement({{0,0},{4,0}},Area)");
    check("TriangleMeasurement(foo)", //
        "TriangleMeasurement(foo)");
  }

  @Test
  public void testTriangleMeasurementMetrics() {
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Area\")", "6");
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Perimeter\")", "12");
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Semiperimeter\")", "6");
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Inradius\")", "1");
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Circumradius\")", "5/2");
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}))", "6");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {0, 1}}), \"Area\")", "1/2");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {0, 1}}), \"Perimeter\")", "2+Sqrt(2)");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {0, 1}}), \"Semiperimeter\")",
        "1+1/Sqrt(2)");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {0, 1}}), \"Circumradius\")", "1/Sqrt(2)");
    check("TriangleMeasurement(Triangle({{0, 0}, {3, 1}, {1, 4}}), \"Perimeter\")",
        "Sqrt(10)+Sqrt(13)+Sqrt(17)");
    check("TriangleMeasurement(Triangle({{0, 0}, {3, 1}, {1, 4}}), \"Area\")", "11/2");
    check("TriangleMeasurement({{0, 0}, {1, 0}, {0, 1}}, \"Area\")", "1/2");
    // check() rounds machine numbers to the engines significant figures, so the full precision
    // result is asserted with checkNumeric()
    check("TriangleMeasurement(Triangle({{0., 0.}, {1., 0.}, {0., 1.}}), \"Circumradius\")",
        "0.707107");
    checkNumeric("TriangleMeasurement(Triangle({{0., 0.}, {1., 0.}, {0., 1.}}), \"Circumradius\")",
        "0.7071067811865476");
    // collinear vertices don't define a triangle
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {2, 0}}), \"Area\")",
        "TriangleMeasurement(Triangle({{0,0},{1,0},{2,0}}),Area)");
    check("TriangleMeasurement(Triangle({{0, 0}, {1, 1}, {2, 2}}), \"Inradius\")",
        "TriangleMeasurement(Triangle({{0,0},{1,1},{2,2}}),Inradius)");
    // message TriangleMeasurement: Foo is not a valid measurement specification.
    check("TriangleMeasurement(Triangle({{0, 0}, {5, 0}, {16/5, 12/5}}), \"Foo\")",
        "TriangleMeasurement(Triangle({{0,0},{5,0},{16/5,12/5}}),Foo)");
  }

  @Test
  public void testTriangleConstruct() {
    // Point constructs
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Centroid\")", //
        "Point({4/3,1})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Incenter\")", //
        "Point({1,1})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Circumcenter\")", //
        "Point({2,3/2})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Orthocenter\")", //
        "Point({0,0})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"NinePointCenter\")", //
        "Point({1,3/4})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"SymmedianPoint\")", //
        "Point({18/25,24/25})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Excenter\", 1})", //
        "Point({6,6})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Midpoint\", 1})", //
        "Point({2,3/2})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Foot\", 1})", //
        "Point({36/25,48/25})");
    check(
        "TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"AngleBisectingCevianEndpoint\", 1})", //
        "Point({12/7,12/7})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"SymmedianEndpoint\", 1})", //
        "Point({36/25,48/25})");
    check(
        "TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"CevianEndpoint\", \"Incenter\", 1})", //
        "Point({12/7,12/7})");

    // Line constructs
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Altitude\", 1})", //
        "Line({{0,0},{36/25,48/25}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Median\", All})", //
        "{Line({{0,0},{2,3/2}}),Line({{4,0},{0,3/2}}),Line({{0,3},{2,0}})}");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"AngleBisectingCevian\", 1})", //
        "Line({{0,0},{12/7,12/7}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Symmedian\", 1})", //
        "Line({{0,0},{36/25,48/25}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Cevian\", \"Incenter\", 1})", //
        "Line({{0,0},{12/7,12/7}})");
    check(
        "TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Cevian\", {\"Excenter\", 1}, 2})", //
        "Line({{4,0},{0,-12}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"OppositeSide\", All})", //
        "{Line({{4,0},{0,3}}),Line({{0,0},{0,3}}),Line({{0,0},{4,0}})}");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Boundary\")", //
        "Line({{0,0},{4,0},{0,3},{0,0}})");

    // InfiniteLine constructs
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"AngleBisector\", 1})", //
        "InfiniteLine({{0,0},{12/7,12/7}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"ExteriorAngleBisector\", 1})", //
        "InfiniteLine({{0,0},{1,-1}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"PerpendicularBisector\", 1})", //
        "InfiniteLine({{2,3/2},{5,11/2}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"EulerLine\")", //
        "InfiniteLine({{2,3/2},{0,0}})");

    // Circle constructs
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Incircle\")", //
        "Circle({1,1},1)");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Circumcircle\")", //
        "Circle({2,3/2},5/2)");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"NinePointCircle\")", //
        "Circle({1,3/4},5/4)");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Excircle\", All})", //
        "{Circle({6,6},6),Circle({-2,2},2),Circle({3,-3},3)}");
    check("TriangleConstruct(Triangle(), \"Circumcircle\")", //
        "Circle({1/2,1/2},1/Sqrt(2))");
    check("TriangleConstruct(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"Incircle\")", //
        "Circle({1/2,1/(2*Sqrt(3))},1/(2*Sqrt(3)))");

    // Triangle constructs
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Triangle\")", //
        "Triangle({{0,0},{4,0},{0,3}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"MedialTriangle\")", //
        "Triangle({{2,3/2},{0,3/2},{2,0}})");
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"AntimedialTriangle\")", //
        "Triangle({{4,3},{-4,3},{4,-3}})");
    check("Area(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"MedialTriangle\"))", //
        "3/2");
    check("Area(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"AntimedialTriangle\"))", //
        "24");

    // Other triangle representations
    check("TriangleConstruct(Polygon({{0, 0}, {4, 0}, {0, 3}}), \"Centroid\")", //
        "Point({4/3,1})");

    // 3D triangles: everything but the circles is defined
    check("TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"Centroid\")", //
        "Point({4/3,1,0})");
    check("TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), {\"Altitude\", 1})", //
        "Line({{0,0,0},{36/25,48/25,0}})");
    check("TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"MedialTriangle\")", //
        "Triangle({{2,3/2,0},{0,3/2,0},{2,0,0}})");
    check(
        "TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), {\"PerpendicularBisector\", 1})", //
        "InfiniteLine({{2,3/2,0},{14/25,-21/50,0}})");
    check("TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), \"Circumcircle\")", //
        "TriangleConstruct(Triangle({{0,0,0},{4,0,0},{0,3,0}}),Circumcircle)");
    check("TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), {\"Excircle\", 1})", //
        "TriangleConstruct(Triangle({{0,0,0},{4,0,0},{0,3,0}}),{Excircle,1})");

    // The centers of an equilateral triangle coincide, so there is no Euler line
    check("TriangleConstruct(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), \"EulerLine\")", //
        "TriangleConstruct(Triangle({{0,0},{1,0},{1/2,Sqrt(3)/2}}),EulerLine)");

    // The constructs agree with TriangleCenter and TriangleMeasurement
    check("ArcLength(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Altitude\", 1}))", //
        "12/5");
    check("TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {\"Height\", 1})", //
        "12/5");
    check(
        "RegionMember(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Circumcircle\"), "
            + "{0, 3})", //
        "True");

    // Unevaluated cases
    // message TriangleConstruct: Foo is not a valid construction specification.
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Foo\")", //
        "TriangleConstruct(Triangle({{0,0},{4,0},{0,3}}),Foo)");
    // message TriangleConstruct: TriangleConstruct called with 1 argument; 2 arguments are
    // expected.
    check("TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}))", //
        "TriangleConstruct(Triangle({{0,0},{4,0},{0,3}}))");
    check("TriangleConstruct(foo, \"Centroid\")", //
        "TriangleConstruct(foo,Centroid)");
  }
}
