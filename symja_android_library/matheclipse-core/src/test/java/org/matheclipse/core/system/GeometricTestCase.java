package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class GeometricTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testAASTriangle() {
    check("AASTriangle(Pi/2, Pi/3, b)", //
        "Triangle({{0,0},{b/2,0},{0,1/2*Sqrt(3)*b}})");
    check("AASTriangle(Pi/6, Pi/3, 1)", //
        "Triangle({{0,0},{2,0},{3/2,Sqrt(3)/2}})");
  }

  @Test
  public void testASATriangle() {
    // message ASATriangle: The sum of angles Pi/2 and Pi/2 should be less than Pi.
    check("ASATriangle(Pi/2, a, Pi/2)", //
        "ASATriangle(Pi/2,a,Pi/2)");

    check("ASATriangle(Pi/2, b, Pi/3)", //
        "Triangle({{0,0},{b,0},{0,Sqrt(3)*b}})");

    check("ASATriangle(Pi/6, 1, Pi/3)", //
        "Triangle({{0,0},{1,0},{3/4,Sqrt(3)/4}})");
  }

  @Test
  public void testArcLength() {
    check("ArcLength(Circle())", "2*Pi");
    check("ArcLength({Sin(t), Cos(t)}, {t, 0, 2*Pi})", //
        "2*Pi");
    check("ArcLength({3*Cos(t), 3*Sin(t)}, {t, 0, Pi})", "3*Pi");
    check("ArcLength({Cos(t), Sin(t), t}, {t, 0, 2*Pi})", "2*Sqrt(2)*Pi");

    // Infinite lengths
    check("ArcLength(HalfLine({{0, 0}, {1, 1}}))", "Infinity");
    check("ArcLength(InfiniteLine({{0, 0}, {1, 1}}))", "Infinity");

    // Defined boundaries
    check("ArcLength(Circle({0, 0}, r))", "2*Pi*r");
    check("ArcLength(Circle({0, 0}, 5))", "10*Pi");
    check("ArcLength(Line({{0, 0}, {3, 4}}))", "5");
    check("ArcLength(Line({{0, 0}, {1, 0}, {1, 1}}))", "2");

    // Undefined lengths (2D filled shapes)
    check("ArcLength(Polygon({{0, 0}, {1, 0}, {1, 1}, {0, 1}}))", "Undefined");
    check("ArcLength(Disk())", "Undefined");
    check("ArcLength(Triangle({{0, 0}, {1, 0}, {0, 1}}))", "Undefined");
    check("ArcLength(Ellipsoid({0, 0}, {2, 3}))", "Undefined");
    check("ArcLength(Ellipsoid({0, 0, 0}, {1, 2, 3}))", "Undefined");

    check("ArcLength(Line({{a,b},{c,d},{e,f}}))", //
        "Sqrt((a-c)^2+(b-d)^2)+Sqrt((c-e)^2+(d-f)^2)");
    check("ArcLength(Circle({a,b},{r1,r2}))", //
        "4*r2*EllipticE(1-r1^2/r2^2)");
    check("ArcLength(Circle({a,b},{r1,r2},{t1,t2}))", //
        "r2*(-EllipticE(t1,1-r1^2/r2^2)+EllipticE(t2,1-r1^2/r2^2))*UnitStep(2*Pi-Abs(-t1+t2))+\n"//
            + "4*r2*EllipticE(1-r1^2/r2^2)*UnitStep(-2*Pi+Abs(-t1+t2))");

  }

  @Test
  public void testArea() {
    check("Area(Disk())", "Pi");
    check("Area(Disk({0, 0}, 5))", "25*Pi");
    check("Area(Disk({0, 0}, {3, 2}))", "6*Pi");
    check("Area(Rectangle())", "1");
    check("Area(Rectangle({0, 0}, {3, 4}))", "12");
    check("Area(Triangle({{0, 0}, {1, 0}, {0, 1}}))", "1/2");
    check("Area(Polygon({{0, 0}, {4, 0}, {4, 3}, {0, 3}}))", //
        "12");
    check("Area(Circle())", "Undefined");

    // Ellipsoids
    check("Area(Ellipsoid({0, 0}, {2, 3}))", "6*Pi");
    check("Area(Ellipsoid({1, 2}, {3, 5}))", "15*Pi");
    check("Area(Ellipsoid({1, 1}, {4, 4}))", "16*Pi");
    check("Area(Ellipsoid({0, 0, 0}, {2, 3, 4}))", "Undefined"); // 3D solid has no area

    // Symbolic and Defaults
    check("Area(Disk({0, 0}, r))", "Pi*r^2");
    check("Area(Triangle())", "1/2");

    // Regular Polygons
    check("Area(RegularPolygon(6))", "3/2*Sqrt(3)");
    check("Area(RegularPolygon(3))", "3/4*Sqrt(3)");
    check("Area(RegularPolygon(4))", "2");
    check("Area(RegularPolygon(r, 6))", "3/2*Sqrt(3)*r^2");
    check("Area(RegularPolygon(2, 4))", "8");
    check("Area(RegularPolygon({2, Pi/4}, 4))", "8");
    check("Area(RegularPolygon({1, 2}, 2, 4))", "8");

    // Spheres
    check("Area(Sphere())", "4*Pi");
    check("Area(Sphere({c1, c2, c3}, r))", "4*Pi*r^2");
    check("Area(Sphere({0, 0, 0}, 2))", "16*Pi");
    check("Area(Sphere({1, 2, 3}))", "4*Pi");

    check("Area(Triangle({{0,0,0},{1,0,0},{0,1,1}}))", //
        "1/Sqrt(2)");
    check("Area(Triangle({{0, 0}, {1, 0}, {1, 1}}))", //
        "1/2");

    check("Area(Disk({a,b}))", //
        "Pi");
    check("Area(Disk({a,b},{r1,r2}))", //
        "Pi*r1*r2");
    check("Area(Disk({a,b},{r1,r2},{t1,t2}))", //
        "r1*r2*Min(Pi,Abs(-t1+t2)/2)");
    check("Area(Rectangle({a,b},{c,d}))", //
        "Abs((-a+c)*(-b+d))");
  }

  @Test
  public void testCirclePoints() {
    // check("CirclePoints(3)", "{{Sqrt(3)/2,-1/2},{0,1},{-Sqrt(3)/2,-1/2}}");
    check("CirclePoints(2)", //
        "{{1,0},{-1,0}}");

    check("CirclePoints(4)", //
        "{{1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),-\n"
            + "1/Sqrt(2)}}");
    // check("CirclePoints(10)", "");

    // Single point
    check("CirclePoints(1)", //
        "{{0,1}}");

    // Two points
    check("CirclePoints(2)", //
        "{{1,0},{-1,0}}");

    // Three points
    check("CirclePoints(3)", //
        "{{Sqrt(3)/2,-1/2},{0,1},{-Sqrt(3)/2,-1/2}}");

    // Four points
    check("CirclePoints(4)", //
        "{{1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),-\n"
            + "1/Sqrt(2)}}");

    // Six points
    check("CirclePoints(6)", //
        "{{1/2,-Sqrt(3)/2},{1,0},{1/2,Sqrt(3)/2},{-1/2,Sqrt(3)/2},{-1,0},{-1/2,-Sqrt(3)/2}}");

    // CirclePoints({r, theta}, n) starts at angle theta with radius r.
    check("CirclePoints({2, 0}, 4)", //
        "{{2,0},{0,2},{-2,0},{0,-2}}");

    check("CirclePoints({1, Pi/2}, 4)", //
        "{{0,1},{-1,0},{0,-1},{1,0}}");

    // CirclePoints(r, n) uses the default starting angle, scaled by r.
    check("CirclePoints(2, 4)", //
        "{{Sqrt(2),-Sqrt(2)},{Sqrt(2),Sqrt(2)},{-Sqrt(2),Sqrt(2)},{-Sqrt(2),-Sqrt(2)}}");

    // CirclePoints({cx, cy}, {r, theta}, n) translates the points to a center.
    check("CirclePoints({1, 1}, {2, 0}, 4)", //
        "{{3,1},{1,3},{-1,1},{1,-1}}");
  }

  @Test
  public void testFindShortestCurve() {
    // The shortest curve between two points on a circle is the shorter arc,
    // returned as Circle[c, r, {θ1, θ2}] with exact angles.
    check("FindShortestCurve(Circle(), {1, 0}, {0, 1})", //
        "Circle({0,0},1,{0,Pi/2})");
    check("FindShortestCurve(Circle(), {-1, 0}, {0, 1})", //
        "Circle({0,0},1,{Pi/2,Pi})");

    // When the shorter arc crosses the ±π branch cut of ArcTan, the spec
    // continues past π instead of taking the long way around.
    check("FindShortestCurve(Circle(), {-Sqrt(2)/2, Sqrt(2)/2}, {-Sqrt(2)/2, -Sqrt(2)/2})", //
        "Circle({0,0},1,{3/4*Pi,5/4*Pi})");
    check("FindShortestCurve(Circle({1, 2}, 3), {4, 2}, {1, 5})", //
        "Circle({1,2},3,{0,Pi/2})");

    // Antipodal points: both arcs are geodesics; the spec starts at the smaller angle.
    check("FindShortestCurve(Circle(), {1, 0}, {-1, 0})", //
        "Circle({0,0},1,{0,Pi})");

    // Machine-precision coordinates give machine-precision angles.
    check("FindShortestCurve(Circle(), {1, 0}, {0.6, 0.8})", //
        "Circle({0,0},1,{0,0.927295})");

    // A point that is not on the circle leaves the call unevaluated.
    check("FindShortestCurve(Circle(), {2, 0}, {0, 1})", //
        "FindShortestCurve(Circle({0,0}),{2,0},{0,1})");

    // ArcLength closes over the returned arc.
    check("ArcLength(FindShortestCurve(Circle(), {1, 0}, {0, 1}))", //
        "Pi/2");

    // In convex solids the geodesic is the straight segment, kept exact.
    check("FindShortestCurve(Disk(), {1/10, 4/5}, {-1/2, 0})", //
        "Line({{1/10,4/5},{-1/2,0}})");
    check("FindShortestCurve(Triangle({{0, 0}, {4, 0}, {0, 3}}), {1, 1}, {2, 0})", //
        "Line({{1,1},{2,0}})");
    check("FindShortestCurve(Cuboid(), {0, 0, 0}, {1, 1, 1})", //
        "Line({{0,0,0},{1,1,1}})");

    // Convex solid point outside unevaluated
    check("FindShortestCurve(Disk(), {2, 0}, {0, 0})", //
        "FindShortestCurve(Disk({0,0}),{2,0},{0,0})");

    // Curve regions are treated as meshes: the sub-path along the polyline,
    // at machine precision, starting at the first query point.
    check("FindShortestCurve(Line({{1, 0}, {2, 1}, {3, 0}, {4, 1}}), {1, 0}, {3, 0})", //
        "Line({{1.0,0.0},{2.0,1.0},{3.0,0.0}})");
    check("FindShortestCurve(Line({{1, 0}, {2, 1}, {3, 0}, {4, 1}}), {3, 0}, {1, 0})", //
        "Line({{3.0,0.0},{2.0,1.0},{1.0,0.0}})");
    check("FindShortestCurve(Line({{0, 0}, {4, 0}}), {1, 0}, {3, 0})", //
        "Line({{1.0,0.0},{3.0,0.0}})");

    // On a closed chain the shorter way around is taken
    check("FindShortestCurve(Line({{0, 0}, {2, 0}, {2, 2}, {0, 2}, {0, 0}}), {1, 0}, {0, 1})", //
        "Line({{1.0,0.0},{0.0,0.0},{0.0,1.0}})");

    // One-dimensional mesh-style chains
    check("FindShortestCurve(Line({{0}, {1}}), {0}, {1})", //
        "Line({{0.0},{1.0}})");

    // Point not on polyline -> unevaluated
    check("FindShortestCurve(Line({{0, 0}, {1, 0}}), {5, 5}, {0, 0})", //
        "FindShortestCurve(Line({{0,0},{1,0}}),{5,5},{0,0})");

    // Unsupported region unevaluated
    check("FindShortestCurve(Annulus(), {1, 0}, {-0.8, 0.4})", //
        "FindShortestCurve(Annulus(),{1,0},{-0.8,0.4})");
  }

  @Test
  public void testPerimeter() {
    check("Perimeter(Polygon({{0, 0}, {1, 0}, {1, 1}, {0, 1}}))", //
        "4");
    check("Perimeter(Rectangle({0, 0}, {3, 4}))", //
        "14");
    check("Perimeter(Rectangle())", //
        "4");
    check("Perimeter(Triangle({{0, 0}, {1, 0}, {0, 1}}))", "2+Sqrt(2)");
    check("Perimeter(Disk({0, 0}, r))", "2*Pi*r");
    check("Perimeter(Disk())", "2*Pi");
    check("Perimeter(Circle({0, 0}, 3))", "Undefined");
    check("Perimeter(Circle())", "Undefined");

    // Ellipsoids
    check("Perimeter(Ellipsoid({0, 0}, {2, 3}))", "12*EllipticE(5/9)");
    check("Perimeter(Ellipsoid({0, 0}, {3, 2}))", "8*EllipticE(-5/4)");
    check("Perimeter(Ellipsoid({0, 0}, {2, 2}))", "4*Pi");
    check("Perimeter(Ellipsoid({1, 2}, {2, 3}))", "12*EllipticE(5/9)");

    check("Perimeter(Disk({a,b}))", //
        "2*Pi");
    check("Perimeter(Disk({a,b},{r1,r2}))", //
        "4*r2*EllipticE(1-r1^2/r2^2)");
    check("Perimeter(Disk({a,b},{r1,r2},{t1,t2}))", //
        "(r2*(-EllipticE(t1,1-r1^2/r2^2)+EllipticE(t2,1-r1^2/r2^2))+Sqrt(r1^2*Cos(t1)^2+r2^\n" //
            + "2*Sin(t1)^2)+Sqrt(r1^2*Cos(t2)^2+r2^2*Sin(t2)^2))*UnitStep(2*Pi-Abs(-t1+t2))+4*r2*EllipticE(\n"//
            + "1-r1^2/r2^2)*UnitStep(-2*Pi+Abs(-t1+t2))");
    check("Perimeter(Rectangle({a,b},{c,d}))", //
        "2*(Abs(-a+c)+Abs(-b+d))");
    check("Perimeter(SSSTriangle(5,6,7))", //
        "18");
    check("Perimeter(Triangle({{0,0,0},{1,0,0},{0,1,1}}))", //
        "1+Sqrt(2)+Sqrt(3)");

  }

  @Test
  public void testSASTriangle() {
    check("SASTriangle(1, Pi/3, 1)", //
        "Triangle({{0,0},{1,0},{1/2,Sqrt(3)/2}})");
    check("SASTriangle(1, Pi/2, 2)", //
        "Triangle({{0,0},{Sqrt(5),0},{4/Sqrt(5),2/Sqrt(5)}})");
  }

  @Test
  public void testSSSTriangle() {
    check("SSSTriangle(10,10,10)", //
        "Triangle({{0,0},{10,0},{5,5*Sqrt(3)}})");
    check("SSSTriangle(3,4,5)", //
        "Triangle({{0,0},{5,0},{16/5,12/5}})");
  }

  @Test
  public void testShortestCurveDistance() {
    // Circle Geodesics
    check("ShortestCurveDistance(Circle(), {1, 0}, {0, 1})", //
        "Pi/2");
    check("ShortestCurveDistance(Circle({0, 0}, 2), {2, 0}, {-2, 0})", //
        "2*Pi");

    // Sphere Great-Circles
    check("ShortestCurveDistance(Sphere(), {1, 0, 0}, {x, y, z})", //
        "ArcCos(x)");
    check("ShortestCurveDistance(Sphere(), {0, 0, 1}, {0, 1, 0})", //
        "Pi/2");
    check("ShortestCurveDistance(Sphere({0, 0, 0}, 2), {0, 0, 2}, {0, 2, 0})", //
        "Pi");

    // Convex Solids (Euclidean distance fallthrough)
    check("ShortestCurveDistance(Disk(), {0, 1}, {x, y})", //
        "Sqrt(x^2+(1-y)^2)");
    check("ShortestCurveDistance(Ball(), {-0.2, 0.1, 0.3}, {-0.8, 0.19, -0.01})", //
        "0.681322");
    check("ShortestCurveDistance(Rectangle({0, 0}, {4, 3}), {1, 1}, {3, 2})", //
        "Sqrt(5)");
    check("ShortestCurveDistance(Cuboid(), {0, 0, 0}, {1, 1, 1})", //
        "Sqrt(3)");

    // Segmented Polylines
    check("ShortestCurveDistance(Line({{1, 0}, {2, 1}, {3, 0}, {4, 1}}), {1, 0}, {3, 0})", //
        "2.82843");
    check("ShortestCurveDistance(Line({{0}, {1}}), {0}, {1})", //
        "1.0");

    // Off-region validation defaults to unevaluated
    check("ShortestCurveDistance(Disk(), {5, 5}, {0, 0})", //
        "ShortestCurveDistance(Disk({0,0}),{5,5},{0,0})");
  }

  @Test
  public void test2DPoints() {
    // 2D points should evaluate to a Rectangle
    check("BoundingRegion({{0, 0}, {1, 1}})", //
        "Rectangle({0,0},{1,1})");
    check("BoundingRegion({{0, 0}, {2, 3}, {1, -1}})", //
        "Rectangle({0,-1},{2,3})");

    // 3D and higher dimensions should evaluate to a Cuboid
    check("BoundingRegion({{1, 2, 3}, {4, 5, 6}})", //
        "Cuboid({1,2,3},{4,5,6})");
    check("BoundingRegion({{1, 2, 3, 4}, {5, 6, 7, 8}})", //
        "Cuboid({1,2,3,4},{5,6,7,8})");

    // 1D points evaluate to a Cuboid
    check("BoundingRegion({{1}, {5}, {3}})", //
        "Cuboid({1},{5})");

    // Rationals and machine precision reals should be preserved perfectly
    check("BoundingRegion({{1/2, 3}, {2, 1/4}})", //
        "Rectangle({1/2,1/4},{2,3})");
    check("BoundingRegion({{1.5, 2.5}, {3.5, 0.5}})", //
        "Rectangle({1.5,0.5},{3.5,2.5})");

    // A single point results in a degenerate box with min == max
    check("BoundingRegion({{0, 0}})", //
        "Rectangle({0,0},{0,0})");

    // Symbolic variables should remain unevaluated in Min and Max wrappers
    check("BoundingRegion({{a, b}, {c, d}})", "Rectangle({Min(a,c),Min(b,d)},{Max(a,c),Max(b,d)})");

    // Structurally invalid data should remain unevaluated
    check("BoundingRegion()", //
        "BoundingRegion()");
    check("BoundingRegion({})", //
        "BoundingRegion({})");
    check("BoundingRegion({1, 2, 3})", //
        "BoundingRegion({1,2,3})");
    check("BoundingRegion({{1, 2}, {3, 4, 5}})", //
        "BoundingRegion({{1,2},{3,4,5}})");
  }

  @Test
  public void testRegionDimension() {
    // Fixed dimension regions
    check("RegionDimension(Point({1, 2}))", //
        "0");
    check("RegionDimension(Line({{0, 0}, {1, 1}}))", //
        "1");
    check("RegionDimension(Circle())", //
        "1");
    check("RegionDimension(Disk())", //
        "2");
    check("RegionDimension(Rectangle())", //
        "2");
    check("RegionDimension(Triangle({{0, 0}, {1, 0}, {0, 1}}))", //
        "2");
    check("RegionDimension(Polygon({{0, 0}, {1, 0}, {1, 1}}))", //
        "2");
    check("RegionDimension(Cylinder())", //
        "3");
    check("RegionDimension(Cone())", //
        "3");

    // Ball and Cuboid take the dimension of their defining coordinate vector
    check("RegionDimension(Ball())", //
        "3");
    check("RegionDimension(Ball({0, 0}, 2))", //
        "2");
    check("RegionDimension(Ball({0, 0, 0, 0}, 2))", //
        "4");
    check("RegionDimension(Cuboid())", //
        "3");
    check("RegionDimension(Cuboid({0, 0}, {1, 2}))", //
        "2");
    check("RegionDimension(Ellipsoid({0, 0, 0}, {1, 2, 3}))", //
        "3");

    // A Sphere is the (n-1)-dimensional surface of an n-ball
    check("RegionDimension(Sphere())", //
        "2");
    check("RegionDimension(Sphere({0, 0, 0}, 2))", //
        "2");
    check("RegionDimension(Sphere({0, 0}, 2))", //
        "1");

    // Simplex and Parallelepiped
    check("RegionDimension(Simplex(2))", //
        "2");
    check("RegionDimension(Simplex({{0, 0}, {1, 0}, {0, 1}}))", //
        "2");
    check("RegionDimension(Parallelepiped({0, 0}, {{1, 0}, {0, 1}}))", //
        "2");
    check("RegionDimension(Annulus({0, 0}, {1, 2}))", //
        "2");
  }

  @Test
  public void testRegionEmbeddingDimension() {
    // Ambient embedding dimension regions
    check("RegionEmbeddingDimension(Disk())", //
        "2");
    check("RegionEmbeddingDimension(Circle({1, 1}, 2))", //
        "2"); // Circle is a 1-D curve embedded in the plane
    check("RegionEmbeddingDimension(Ball())", //
        "3");
    check("RegionEmbeddingDimension(Cuboid())", //
        "3");
    check("RegionEmbeddingDimension(Sphere({0, 0, 0}, 1))", //
        "3"); // Sphere is a 2-D surface embedded in 3-space

    // Line and Point embedding dimensions follow their point coordinates
    check("RegionEmbeddingDimension(Line({{0, 0, 0}, {1, 1, 1}}))", //
        "3");
    check("RegionEmbeddingDimension(Point({1, 2, 3}))", //
        "3");

    // Unevaluated cases
    check("RegionEmbeddingDimension(x)", //
        "RegionEmbeddingDimension(x)");
  }

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

    // Unevaluated cases
    check("TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), \"Foo\")", //
        "TriangleCenter(Triangle({{0,0},{4,0},{0,3}}),Foo)");
    check("TriangleCenter(foo)", //
        "TriangleCenter(foo)");
  }


  @Test
  public void testVolume() {
    check("Volume(Cylinder({{0, 0, 0}, {1, 1, 1}}, 1/2))", //
        "1/4*Sqrt(3)*Pi");
    check("Volume(Ball({a,b,c}, r))", //
        "4/3*Pi*r^3");
    check("Volume(Cuboid({a,b,c}, {x,y,z}))", //
        "Abs((-a+x)*(-b+y)*(-c+z))");
    check("Volume(Ellipsoid({a,b,c}, {x,y,z}))", //
        "4/3*Pi*x*y*z");
    check("Volume(Ellipsoid({0,0,0}, {3,2,1}))", //
        "8*Pi");

    check("Volume(Cuboid())", "1");
    check("Volume(Cuboid({1, 2, 3}))", "1");
    check("Volume(Cuboid({0, 0, 0}, {2, 3, 4}))", "24");
    check("Volume(Cuboid({a, b, c}, {d, e, f}))", "Abs((-a+d)*(-b+e)*(-c+f))");
    check("Volume(Cuboid({0, 0}, {2, 3}))", "Undefined"); // 2D has no 3-volume

    // Cylinder
    check("Volume(Cylinder())", "2*Pi");
    check("Volume(Cylinder({{0, 0, 0}, {0, 0, 5}}, 2))", "20*Pi");
    check("Volume(Cylinder({{0, 0, 0}, {0, 0, 5}}))", "5*Pi");

    // Cone
    check("Volume(Cone())", "2/3*Pi");
    check("Volume(Cone({{0, 0, 0}, {0, 0, 5}}, 2))", "20/3*Pi");
    check("Volume(Cone({{0, 0, 0}, {0, 0, 5}}))", "5/3*Pi");

    // Ball & Ellipsoid
    check("Volume(Ball({0, 0, 0}, 3))", "36*Pi");
    check("Volume(Ball())", "4/3*Pi");
    check("Volume(Ball({1, 2, 3}))", "4/3*Pi");
    check("Volume(Ball({0, 0, 3}, r))", "4/3*Pi*r^3");
    check("Volume(Ball({0, 0}, 2))", "Undefined");
    check("Volume(Ellipsoid({0, 0, 0}, {1, 2, 3}))", "8*Pi");
    check("Volume(Ellipsoid({0, 0, 0}, {r1, r2, r3}))", //
        "4/3*Pi*r1*r2*r3");
    check("Volume(Ellipsoid({0, 0}, {2, 3}))", //
        "Undefined");

    // Polyhedrons
    check("Volume(Tetrahedron({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))", //
        "1/6");
    check("Volume(Tetrahedron({{0,0,0},{2,0,0},{7,3,0},{0,5,1}}))", //
        "1");
    check("Volume(Simplex({{0,0,0},{2,0,0},{0,3,0},{0,0,6}}))", //
        "6");
    check("Volume(Parallelepiped({0,0,0},{{1,0,0},{0,2,0},{0,0,3}}))", //
        "6");

    // Lower dimensional regions
    check("Volume(Sphere({0, 0, 0}, 2))", //
        "Undefined");
    check("Volume(Disk())", //
        "Undefined");
    check("Volume(Triangle({{0, 0}, {1, 0}, {0, 1}}))", //
        "Undefined");
  }

  @Test
  public void testRegionMeasure() {
    // 3D Solids -> Volume
    check("RegionMeasure(Ellipsoid({0, 0, 0}, {1, 2, 3}))", "8*Pi");
    check("RegionMeasure(Cuboid({0, 0, 0}, {1, 2, 3}))", "6");
    check("RegionMeasure(Cylinder())", "2*Pi");
    check("RegionMeasure(Cone())", "2/3*Pi");
    check("RegionMeasure(Tetrahedron({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))", "1/6");
    check("RegionMeasure(Ball())", "4/3*Pi");
    check("RegionMeasure(Ball({0, 0, 0}, 3))", "36*Pi");
    check("RegionMeasure(Ball({0, 0, 0, 0}, 2))", "8*Pi^2");

    // 2D Solids -> Area
    check("RegionMeasure(Ellipsoid({0, 0}, {2, 3}))", "6*Pi");
    check("RegionMeasure(Disk({0, 0}, 2))", "4*Pi");
    check("RegionMeasure(Disk())", "Pi");
    check("RegionMeasure(Rectangle({0, 0}, {2, 3}))", "6");
    check("RegionMeasure(Triangle({{0, 0}, {1, 0}, {0, 1}}))", "1/2");
    check("RegionMeasure(Polygon({{0, 0}, {2, 0}, {2, 2}, {0, 2}}))", "4");
    check("RegionMeasure(Simplex({{0,0},{2,0},{0,2}}))", "2");
    check("RegionMeasure(Ball({1, 1}))", "Pi");
    check("RegionMeasure(Sphere({0, 0, 0}, 2))", "16*Pi"); // Surface Area

    // 1D Curves -> Length
    check("RegionMeasure(Circle({0, 0}, 2))", "4*Pi");
    check("RegionMeasure(Line({{0, 0}, {3, 4}}))", "5");

    // 0D -> Point counting
    check("RegionMeasure(Point({1, 2}))", "1");
    check("RegionMeasure(Point({{1, 2}, {3, 4}}))", "2");
  }

  @Test
  public void testRegionCentroid() {
    check("RegionCentroid(Point({3, 4}))", "{3,4}");
    check("RegionCentroid(Disk())", "{0,0}");
    check("RegionCentroid(Disk({3, 4}, 2))", "{3,4}");
    check("RegionCentroid(Disk({a, b}, r))", "{a,b}");
    check("RegionCentroid(Rectangle())", "{1/2,1/2}");
    check("RegionCentroid(Rectangle({0, 0}, {2, 3}))", "{1,3/2}");
    check("RegionCentroid(Rectangle({a, b}, {c, d}))", //
        "{1/2*(a+c),1/2*(b+d)}");
    check("RegionCentroid(Triangle({{0, 0}, {1, 0}, {0, 1}}))", "{1/3,1/3}");
    check("RegionCentroid(Polygon({{0, 0}, {1, 0}, {1, 1}, {0, 1}}))", "{1/2,1/2}");
    check("RegionCentroid(Polygon({{0, 0}, {2, 0}, {3, 1}, {1, 1}}))", "{3/2,1/2}");
    check("RegionCentroid(Line({{0, 0}, {1, 1}}))", "{1/2,1/2}");
    check("RegionCentroid(Ball({1, 2, 3}, 5))", "{1,2,3}");
    check("RegionCentroid(Circle({2, 3}, 1))", "{2,3}");
    check("RegionCentroid(Ellipsoid({1, 2}, {3, 4}))", "{1,2}");
    check("RegionCentroid(Tetrahedron({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))", "{1/4,1/4,1/4}");
    check("RegionCentroid(foo)", "RegionCentroid(foo)");
  }

  @Test
  public void testRegionBounds() {
    check("RegionBounds(HalfLine({{0, 0}, {1, 1}}))", //
        "{{0,Infinity},{0,Infinity}}");
    check("RegionBounds(HalfLine({{0, 0}, {-1, -1}}))", //
        "{{-Infinity,0},{-Infinity,0}}");
    check("RegionBounds(HalfLine({{0, 0}, {1, 0}}))", //
        "{{0,Infinity},{0,0}}");
    check("RegionBounds(Line({{0, 0}, {1, 1}}))", //
        "{{0,1},{0,1}}");
    check("RegionBounds(Line({{0, 0}, {1, 1}, {2, -1}}))", //
        "{{0,2},{-1,1}}");

    check("RegionBounds(Disk())", //
        "{{-1,1},{-1,1}}");

    check("RegionBounds(Disk({1, 2}, 3))", //
        "{{-2,4},{-1,5}}");
    check("RegionBounds(Circle({1, 1}, 2))", //
        "{{-1,3},{-1,3}}");
    check("RegionBounds(Ball({0, 0, 0}, 2))", //
        "{{-2,2},{-2,2},{-2,2}}");
    check("RegionBounds(Rectangle({0, 0}, {2, 3}))", //
        "{{0,2},{0,3}}");
    check("RegionBounds(Triangle({{0, 0}, {4, 0}, {1, 3}}))", //
        "{{0,4},{0,3}}");
  }

  @Test
  public void testRegionMember() {
    check("RegionMember(Disk({0, 0}, 1), {0.5, 0.5})", "True");
    check("RegionMember(Disk({0, 0}, 1), {1, 0})", "True");
    check("RegionMember(Disk({0, 0}, 1), {2, 0})", "False");
    check("RegionMember(Disk(), {0.3, 0.3})", "True");
    check("RegionMember(Ball({0, 0, 0}, 1), {0.5, 0.5, 0.5})", "True");
    check("RegionMember(Rectangle({0, 0}, {2, 3}), {1, 1})", "True");
    check("RegionMember(Rectangle({0, 0}, {2, 3}), {3, 1})", "False");

    // Boundaries
    check("RegionMember(Circle({0, 0}, 1), {1, 0})", "True");
    check("RegionMember(Circle({0, 0}, 1), {0.5, 0.5})", "False");

    // Polygons
    check("RegionMember(Triangle({{0, 0}, {4, 0}, {0, 3}}), {1, 1})", "True");
    check("RegionMember(Triangle({{0, 0}, {4, 0}, {0, 3}}), {3, 3})", "False");
    check("RegionMember(Polygon({{0, 0}, {4, 0}, {4, 4}, {2, 1}, {0, 4}}), {2, 3})", "False");
  }

  @Test
  public void testRegionDistance() {
    check("RegionDistance(Disk({0, 0}, 1), {3, 0})", "2");
    check("RegionDistance(Disk({0, 0}, 2), {3, 4})", "3");
    check("RegionDistance(Disk({0, 0}, 1), {0.5, 0})", "0");
    check("RegionDistance(Point({0, 0}), {3, 4})", "5");
    check("RegionDistance(Circle({0, 0}, 1), {0.5, 0})", "0.5");
    check("RegionDistance(Circle({0, 0}, 1), {3, 0})", "2");
    check("RegionDistance(Rectangle({0, 0}, {2, 2}), {3, 1})", "1");
    check("RegionDistance(Rectangle({0, 0}, {2, 2}), {3, 3})", "Sqrt(2)");
    check("RegionDistance(Line({{0, 0}, {4, 0}}), {2, 5})", "5");
    check("RegionDistance(Triangle({{0, 0}, {4, 0}, {0, 3}}), {5, 5})", "23/5");

    // SignedRegionDistance
    check("SignedRegionDistance(Disk({0, 0}, 1), {3, 0})", "2");
    check("SignedRegionDistance(Disk({0, 0}, 1), {0.5, 0})", "-0.5");
    check("SignedRegionDistance(Disk({0, 0}, 1), {0, 0})", "-1");
    check("SignedRegionDistance(Disk({0, 0}, 1), {1, 0})", "0");
    check("SignedRegionDistance(Rectangle({0, 0}, {4, 2}), {1, 1})", "-1");
    check("SignedRegionDistance(Rectangle({0, 0}, {2, 2}), {3, 3})", "Sqrt(2)");
    check("SignedRegionDistance(Triangle({{0, 0}, {4, 0}, {0, 3}}), {1, 1})", "-1");
  }

  @Test
  public void testRegionNearest() {
    check("RegionNearest(x, y)", //
        "RegionNearest(x,y)");

    // Disks project outside to boundary, inside return itself
    check("RegionNearest(Disk({0, 0}, 1), {3, 0})", //
        "{1,0}");
    check("RegionNearest(Disk({0, 0}, 2), {3, 4})", //
        "{6/5,8/5}");
    check("RegionNearest(Disk({0, 0}, 1), {0.5, 0})", //
        "{0.5,0}");

    // Points and Balls
    check("RegionNearest(Point({2, 3}), {5, 7})", //
        "{2,3}");
    check("RegionNearest(Ball({0, 0, 0}, 1), {0, 0, 3})", //
        "{0,0,1}");

    // Circles strictly map to their perimeter
    check("RegionNearest(Circle({0, 0}, 1), {3, 4})", //
        "{3/5,4/5}");

    // Rectangles clamp nearest coordinates
    check("RegionNearest(Rectangle({0, 0}, {2, 2}), {3, 1})", //
        "{2,1}");
    check("RegionNearest(Rectangle({0, 0}, {2, 2}), {3, 3})", //
        "{2,2}");
    check("RegionNearest(Rectangle({0, 0}, {2, 2}), {1, 1})", //
        "{1,1}");

    // Line segments strictly limit bounds to start/end nodes
    check("RegionNearest(Line({{0, 0}, {2, 2}}), {2, 0})", //
        "{1,1}");
    check("RegionNearest(Line({{0, 0}, {4, 0}}), {2, 5})", //
        "{2,0}");
    check("RegionNearest(Line({{0, 0}, {4, 0}}), {-1, 3})", //
        "{0,0}");
    check("RegionNearest(Line({{0, 0}, {3, 3}}), {3, 0})", //
        "{3/2,3/2}");
    check("RegionNearest(Line({{0, 0, 0}, {2, 2, 2}}), {2, 0, 0})", //
        "{2/3,2/3,2/3}");

    // Polylines snap to nearest active segmented path
    check("RegionNearest(Line({{0, 0}, {2, 0}, {2, 2}}), {3, 1})", //
        "{2,1}");

    // Boundary resolving algorithms for non-member paths
    check("RegionNearest(Triangle({{0, 0}, {4, 0}, {0, 3}}), {1, 1})", //
        "{1,1}");
    check("RegionNearest(Triangle({{0, 0}, {4, 0}, {0, 3}}), {5, 5})", //
        "{56/25,33/25}");
    check("RegionNearest(Triangle({{0, 0}, {4, 0}, {0, 3}}), {-1, -1})", //
        "{0,0}");

    // Polygons operate securely with interior checks falling to perimeter mapping
    check("RegionNearest(Polygon({{0,0},{2,0},{2,2},{0,2}}), {3, 1})", //
        "{2,1}");
  }

  @Test
  public void testRegionNearestFunction() {
    // 1. Create the function representation
    check("f = RegionNearestFunction(Rectangle({0, 0}, {2, 2}))", //
        "RegionNearestFunction(Rectangle({0,0},{2,2}))");

    // 2. Apply the pre-calculated DataExpr to a point
    check("f({3, 1})", //
        "{2,1}");

    // 3. Apply to an interior point
    check("f({1, 1})", //
        "{1,1}");

    // 4. Test inline immediate application
    check("RegionNearest(Disk({0, 0}, 1))[{3, 0}]", //
        "{1,0}");

    // 5. Test 1D/2D nested structures
    check("rnf = RegionNearest(Line({{0, 0}, {4, 0}})); rnf({2, 5})", //
        "{2,0}");
  }

  @Test
  public void testRegionWithin() {
    // Identity evaluations short-circuit immediately
    check("RegionWithin(Disk({0, 0}, 2), Disk({0, 0}, 2))", //
        "True");
    check("RegionWithin(Rectangle(), Rectangle())", //
        "True");

    // Circular/Spherical Bounding (Norm(c1 - c2) + r2 <= r1)
    check("RegionWithin(Disk({0, 0}, 5), Disk({0, 0}, 2))", //
        "True");
    check("RegionWithin(Disk({0, 0}, 2), Disk({0, 0}, 5))", //
        "False");
    check("RegionWithin(Disk({0, 0}, 5), Disk({2, 0}, 2))", //
        "True");
    check("RegionWithin(Disk({0, 0}, 5), Disk({4, 0}, 2))", //
        "False");
    check("RegionWithin(Ball({0, 0, 0}, 5), Ball({1, 0, 0}, 2))", //
        "True");

    // Rectangular/Cuboid Bounding (min1 <= min2 && max2 <= max1)
    check("RegionWithin(Rectangle({0, 0}, {5, 5}), Rectangle({1, 1}, {2, 2}))", //
        "True");
    check("RegionWithin(Rectangle({1, 1}, {2, 2}), Rectangle({0, 0}, {5, 5}))", //
        "False");
    check("RegionWithin(Rectangle({0, 0}, {3, 3}), Rectangle({2, 2}, {4, 4}))", //
        "False");
    check("RegionWithin(Cuboid({0, 0, 0}, {5, 5, 5}), Cuboid({1, 1, 1}, {2, 2, 2}))", //
        "True");

    // Single Points and Point Collections fallback reliably to RegionMember
    check("RegionWithin(Disk(), Point({0.5, 0.5}))", //
        "True");
    check("RegionWithin(Disk(), Point({2, 0}))", //
        "False");
    check("RegionWithin(Rectangle(), Point({{0.5, 0.5}, {0.1, 0.1}}))", //
        "True");
    check("RegionWithin(Rectangle(), Point({{0.5, 0.5}, {2, 2}}))", //
        "False");

    // Complex Vertex geometries securely bounding inside Convex Regions
    check("RegionWithin(Rectangle(), Line({{0.1, 0.1}, {0.9, 0.9}}))", //
        "True");
    check("RegionWithin(Rectangle(), Line({{0.1, 0.1}, {2, 2}}))", //
        "False");
    check("RegionWithin(Disk(), Triangle({{0, 0}, {0.5, 0}, {0, 0.5}}))", //
        "True");
    check("RegionWithin(Disk(), Triangle({{0, 0}, {2, 0}, {0, 2}}))", //
        "False");
    check("RegionWithin(Rectangle(), Polygon({{0.1, 0.1}, {0.9, 0.1}, {0.5, 0.9}}))", //
        "True");
  }
}
