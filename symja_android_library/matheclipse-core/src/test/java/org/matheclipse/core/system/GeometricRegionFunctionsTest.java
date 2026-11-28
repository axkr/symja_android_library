package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the geometric constructions and region functions: Hyperplane, ConicHullRegion,
 * TransformedRegion, Midpoint, PerpendicularBisector, DihedralAngle, SolidAngle,
 * CircumscribedBall, RegionConvert, RegionDisjoint, ConvexPolygonQ, SimplePolygonQ,
 * ConnectedMeshComponents, TriangulateMesh, CoordinateTransform and TransformedField.
 */
public class GeometricRegionFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testHyperplane() {
    check("Hyperplane({2,-1}, 3)", //
        "Hyperplane({2,-1},3)");
    check("Hyperplane({1,1})", //
        "Hyperplane({1,1},0)");
  }

  @Test
  public void testRegionMember() {
    check("RegionMember(Hyperplane({2,-1}, 3), {2,1})", //
        "True");
    check("RegionMember(Hyperplane({2,-1}, 3), {1,1})", //
        "False");
    check("RegionMember(Hyperplane({1,1,1,1}, 2), {1,1,0,0})", //
        "True");
    check("RegionMember(TransformedRegion(Disk({0,0},1), ShearingTransform(Pi/4,{1,0},{0,1})), {1,1})", //
        "True");
    check("RegionMember(TransformedRegion(Disk({0,0},1), ShearingTransform(Pi/4,{1,0},{0,1})), {2,0})", //
        "False");
    check("RegionMember(ConicHullRegion({{0,0}},{{1,0},{1,1}}), {3,1})", //
        "True");
    check("RegionMember(ConicHullRegion({{0,0}},{{1,0},{1,1}}), {1,2})", //
        "False");
    check("RegionMember(ConicHullRegion({{1,1}},{{0,1}}), {1,5})", //
        "True");
    check("RegionMember(ConicHullRegion({{0,0},{2,1},{1,3}}), {3,3})", //
        "True");
    check("RegionMember(ConicHullRegion({{0,0},{1,1}}), {3,3})", //
        "True");
    check("RegionMember(ConicHullRegion({{0,0},{1,1}}), {3,2})", //
        "False");
    check("RegionMember(ConicHullRegion({{0,0,0},{1,0,0},{0,1,0}}), {5,-7,0})", //
        "True");
    check("RegionMember(Hyperplane({1,2,2}, 3), {1,1,0})", //
        "True");
    check("RegionMember(Hyperplane({1,2,2}, 3), {1,1,1})", //
        "False");
    check("RegionMember(InfinitePlane({{0,0,0},{1,0,0},{0,1,0}}), {x,y,z})", //
        "z==0");
    check("RegionMember(InfinitePlane({0,0,0}, SparseArray({{1,0,0},{0,1,0}})), {1,2,0})", //
        "True");
  }

  @Test
  public void testRegionDistance() {
    check("RegionDistance(Hyperplane({3,4}, 10), {0,0})", //
        "2");
    check("RegionDistance(Hyperplane({0,0,2}, 4), {5,6,7})", //
        "5");
  }

  @Test
  public void testRegionDimension() {
    check("RegionDimension(Hyperplane({1,2,2}, {1,0,0}))", //
        "2");
    check("RegionDimension(ConicHullRegion({{0,0,0}},{{1,0,0},{0,1,0},{0,0,1}}))", //
        "3");
  }

  @Test
  public void testRegionMeasure() {
    check("RegionMeasure(Hyperplane({1,1}, 0))", //
        "Infinity");
    check("RegionMeasure(TransformedRegion(Disk({0,0},1), ShearingTransform(Pi/4,{1,0},{0,1})))", //
        "Pi");
    check("RegionMeasure(TriangulateMesh(Polygon({{0,0},{3,0},{3,3},{2,3},{2,1},{0,1}})))", //
        "5");
    check("RegionMeasure(TriangulateMesh(Polygon({{0,0},{2,0},{2,2},{0,2}}), MaxCellMeasure -> {\"Length\" -> 0.6}))", //
        "4");
  }

  @Test
  public void testMidpoint() {
    check("Midpoint({{2,5},{8,-1}})", //
        "{5,2}");
    check("Midpoint(Line({{1,0,3},{4,2,-1}}))", //
        "{5/2,1,1}");
    check("Midpoint({{a,b},{c,d}})", //
        "{1/2*(a+c),1/2*(b+d)}");
  }

  @Test
  public void testPerpendicularBisector() {
    check("PerpendicularBisector({{1,1},{5,3}})", //
        "InfiniteLine({3,2},{2,-4})");
    check("PerpendicularBisector(Line({{-2,0},{0,4}}))", //
        "InfiniteLine({-1,2},{4,-2})");
    check("PerpendicularBisector({{0,0,0},{2,4,6}})", //
        "PerpendicularBisector({{0,0,0},{2,4,6}})");
    check("PerpendicularBisector({{1,2},{1,2}})", //
        "PerpendicularBisector({{1,2},{1,2}})");
  }

  @Test
  public void testDihedralAngle() {
    check("DihedralAngle({{0,0,0},{0,0,3}},{{2,0,0},{1,1,5}})", //
        "Pi/4");
    check("DihedralAngle({{1,0,0},{1,2,0}},{{0,0,1},{3,0,-1}})", //
        "ArcCos(-1/Sqrt(10))");
    check("DihedralAngle({{0,0,0},{1,1,1}},{{1,-1,0},{1,0,-1}})", //
        "Pi/3");
  }

  @Test
  public void testSolidAngle() {
    check("SolidAngle({0,0,0}, {{3,0,0},{0,2,0},{0,0,7}})", //
        "Pi/2");
    check("SolidAngle({2,2,2}, {{3,2,2},{2,3,2},{2,2,1}})", //
        "Pi/2");
    check("SolidAngle({0,0,0}, {{1,0,0},{0,1,0},{1,1,1}})", //
        "2*ArcTan(1/(2+Sqrt(3)))");
    check("SolidAngle({1,1}, {{3,1},{1,4}})", //
        "Pi/2");
    check("SolidAngle({0,0,0}, {{1,0,0},{0,1,0},{1,1,0}})", //
        "0");
  }

  @Test
  public void testN() {
    check("N(SolidAngle({0,0,0}, {{1,0,0},{0,1,0},{1,1,1}}))", //
        "0.523599");
  }

  @Test
  public void testCircumscribedBall() {
    check("CircumscribedBall({{0,0},{6,0},{0,8}})", //
        "Ball({3,4},5)");
    check("CircumscribedBall({{0,0},{10,0},{5,1}})", //
        "Ball({5,0},5)");
    check("CircumscribedBall({{1,1},{3,1}})", //
        "Ball({2,1},1)");
    check("CircumscribedBall({{2,3}})", //
        "Ball({2,3},0)");
    check("CircumscribedBall({{0,0,0},{2,0,0},{0,2,0},{0,0,2}})", //
        "Ball({2/3,2/3,2/3},2*Sqrt(2/3))");
    check("CircumscribedBall(Polygon({{0,0},{4,0},{4,2},{0,2}}))", //
        "Ball({2,1},Sqrt(5))");
    check("CircumscribedBall({{0.,0.},{3.,0.},{0.,4.}})", //
        "Ball({1.5,2.0},2.5)");
    check("CircumscribedBall({{x,0},{1,1}})", //
        "CircumscribedBall({{x,0},{1,1}})");
    check("CircumscribedBall(Tuples(Range(0,7),2))", //
        "Ball({7/2,7/2},7/Sqrt(2))");
    check("CircumscribedBall({{0,0},{1,0},{2,0},{3,0}})", //
        "Ball({3/2,0},3/2)");
  }

  @Test
  public void testRegionConvert() {
    check("RegionConvert(Rectangle({-1,0},{2,4}), \"Implicit\")", //
        "ImplicitRegion(-1<=x<=2&&0<=y<=4,{x,y})");
    check("RegionConvert(Disk({2,-1},5), \"Implicit\")", //
        "ImplicitRegion((2-x)^2+(1+y)^2<=25,{x,y})");
    check("RegionConvert(Disk({0,0},{3,2}), \"Implicit\")", //
        "ImplicitRegion(4*x^2+9*y^2<=36,{x,y})");
    check("RegionConvert(Ball({0,1,0},3), \"Implicit\")", //
        "ImplicitRegion(x^2+(1-y)^2+z^2<=9,{x,y,z})");
    check("RegionConvert(Cuboid({0,0,0},{1,2,3}), \"Implicit\")", //
        "ImplicitRegion(0<=x<=1&&0<=y<=2&&0<=z<=3,{x,y,z})");
    check("RegionConvert(Parallelogram({0,0},{{2,0},{1,1}}), \"Implicit\")", //
        "ImplicitRegion(0<=x-y<=2&&0<=y<=1,{x,y})");
    check("RegionConvert(Triangle({{0,0},{2,0},{0,2}}), \"Implicit\")", //
        "ImplicitRegion(x>=0&&y>=0&&x+y<=2,{x,y})");
    check("RegionConvert(ImplicitRegion(x^2<=y, {x,y}), \"Implicit\")", //
        "ImplicitRegion(x^2<=y,{x,y})");
    check("RegionConvert(Hyperplane({1,2}, 3), \"Implicit\")", //
        "ImplicitRegion(x+2*y==3,{x,y})");
    check("RegionConvert(Hyperplane({1,0,2}, {1,1,1}), \"Implicit\")", //
        "ImplicitRegion(x+2*z==3,{x,y,z})");
  }

  @Test
  public void testRegionDisjoint() {
    check("RegionDisjoint(Disk({0,0},1), Disk({5,0},2))", //
        "True");
    check("RegionDisjoint(Disk({0,0},2), Disk({3,0},1))", //
        "False");
    check("RegionDisjoint(Disk({0,0},1), Disk({1,1},1))", //
        "False");
    check("RegionDisjoint(Ball({0,0,0},1), Ball({0,0,3},1), Ball({4,0,0},1))", //
        "True");
    check("RegionDisjoint(Rectangle({0,0},{1,1}), Rectangle({1,1},{2,2}))", //
        "False");
    check("RegionDisjoint(Rectangle({0,0},{1,1}), Rectangle({1.5,0},{2,2}))", //
        "True");
    check("RegionDisjoint(Disk({0,0},1), Rectangle({1,1},{2,2}))", //
        "True");
    check("RegionDisjoint(Disk({0,0},2), Rectangle({1,1},{2,2}))", //
        "False");
    check("RegionDisjoint(Point({3,3}), Disk({0,0},1))", //
        "True");
    check("RegionDisjoint(Disk({0,0},1))[Disk({4,4},1)]", //
        "True(Disk({4,4},1))");
    check("RegionDisjoint(Disk())", //
        "True");
  }

  @Test
  public void testConvexPolygonQ() {
    check("ConvexPolygonQ(Polygon({{0,0},{3,0},{3,2},{0,2}}))", //
        "True");
    check("ConvexPolygonQ(Polygon({{0,0},{3,0},{1,1},{0,3}}))", //
        "False");
    check("ConvexPolygonQ(Polygon({{0,0},{0,2},{2,2},{2,0}}))", //
        "True");
    check("ConvexPolygonQ(Polygon({{0,0},{2,0},{0,2},{2,2}}))", //
        "False");
    check("ConvexPolygonQ(Polygon({{0,0},{1,0},{2,0},{1,1}}))", //
        "True");
    check("ConvexPolygonQ(Polygon({{1,0},{0.30901699437494745,0.9510565162951535},{-0.8090169943749473,0.5877852522924732},{-0.8090169943749476,-0.587785252292473},{0.30901699437494723,-0.9510565162951536}}))", //
        "True");
    check("ConvexPolygonQ(Polygon({{0,0,0},{1,0,1},{1,1,1},{0,1,0}}))", //
        "True");
    check("ConvexPolygonQ(Polygon({{0,0},{1,a},{0,1}}))", //
        "True");
    check("ConvexPolygonQ(Disk())", //
        "False");
    check("ConvexPolygonQ(Polygon({{0,0},{1,a},{0,1}}))", //
        "True");
  }

  @Test
  public void testSimplePolygonQ() {
    check("SimplePolygonQ(Polygon({{0,0},{4,0},{4,3},{2,1},{0,3}}))", //
        "True");
    check("SimplePolygonQ(Polygon({{0,0},{2,2},{2,0},{0,2}}))", //
        "False");
    check("SimplePolygonQ(Polygon({{0,0},{2,0},{2,2},{1,0},{0,2}}))", //
        "False");
    check("SimplePolygonQ(Polygon({{0,0},{2,0},{2,2},{0,0},{0,2}}))", //
        "False");
    check("SimplePolygonQ(Polygon({{0,0},{1,0},{2,0}}))", //
        "False");
    check("SimplePolygonQ(Polygon({{1,0},{0,1},{-1,0},{0,-1}}))", //
        "True");
    check("SimplePolygonQ(Polygon({{100000000.,0.},{100000005.,0.},{100000005.,10000.},{100000000.,10000.}}))", //
        "True");
    check("SimplePolygonQ(Polygon({{0,0},{1,0},{1,1},{0,1}},{{1,2,3,4}}))", //
        "True");
  }

  @Test
  public void testTransformedRegion() {
    check("TransformedRegion(Disk({1,1},2), TranslationTransform({3,-1}))", //
        "Disk({4,0},2)");
    check("TransformedRegion(Disk({0,0},1), ScalingTransform({3,2}))", //
        "Disk({0,0},{3,2})");
    check("TransformedRegion(Ball({0,0,0},2), ScalingTransform({1,2,3}))", //
        "Ellipsoid({0,0,0},{2,4,6})");
    check("TransformedRegion(Disk({1,0},1), RotationTransform(Pi/2))", //
        "Disk({0,1},1)");
    check("TransformedRegion(Rectangle({0,0},{2,1}), ScalingTransform({2,3}))", //
        "Rectangle({0,0},{4,3})");
    check("TransformedRegion(Rectangle({0,0},{2,1}), RotationTransform(Pi/2))", //
        "Rectangle({-1,0},{0,2})");
    check("TransformedRegion(Triangle({{0,0},{1,0},{0,1}}), AffineTransform({{{1,2},{0,1}},{1,1}}))", //
        "Triangle({{1,1},{2,1},{3,2}})");
    check("TransformedRegion(Polygon({{0,0},{2,0},{1,3}}), ReflectionTransform({1,0}))", //
        "Polygon({{0,0},{-2,0},{-1,3}})");
    check("TransformedRegion(HalfSpace({1,0}, 2), TranslationTransform({3,0}))", //
        "HalfSpace({1,0},{5,0})");
    check("TransformedRegion(TransformedRegion(Point({1,2}), TranslationTransform({1,1})), ScalingTransform({2,2}))", //
        "Point({4,6})");
    check("TransformedRegion(Disk({0,0},1), ShearingTransform(Pi/4,{1,0},{0,1}))", //
        "TransformedRegion(Disk({0,0},1),TransformationFunction({{1,1,0},{0,1,0},{0,0,1}}))");
    check("TransformedRegion(Disk(), f)", //
        "TransformedRegion(Disk({0,0}),f)");
    check("TransformedRegion(TransformedRegion(Point({1,2,3}), TranslationTransform({1,1,1})), ScalingTransform({2,2}))", //
        "TransformedRegion(Point({2,3,4}),TransformationFunction({{2,0,0},{0,2,0},{0,0,1}}))");
  }

  @Test
  public void testConicHullRegion() {
    check("ConicHullRegion({{0,0}},{{1,0},{1,1}})", //
        "ConicHullRegion({{0,0}},{{1,0},{1,1}})");
    check("ConicHullRegion({{0,0},{2,1},{1,3}})", //
        "FullRegion(2)");
    check("ConicHullRegion({{0,0},{a,0},{0,b}})", //
        "ConicHullRegion({{0,0},{a,0},{0,b}})");
  }

  @Test
  public void testConnectedMeshComponents() {
    check("ConnectedMeshComponents(MeshRegion({{0,0},{1,0},{0,1},{5,5},{6,5},{5,6}}, {Polygon({1,2,3}), Polygon({4,5,6})}))", //
        "{MeshRegion({{0,0},{1,0},{0,1}},{Polygon({1,2,3})}),MeshRegion({{5,5},{6,5},{5,6}},{Polygon({\n1,2,3})})}");
    check("ConnectedMeshComponents(MeshRegion({{0,0},{1,0},{2,0},{7,7},{8,8}}, {Line({{1,2},{2,3}}), Line({4,5})}))", //
        "{MeshRegion({{0,0},{1,0},{2,0}},{Line({{1,2},{2,3}})}),MeshRegion({{7,7},{8,8}},{Line({\n1,2})})}");
    check("ConnectedMeshComponents(MeshRegion({{0,0},{1,0},{0,1},{1,1}}, {Polygon({{1,2,3},{2,4,3}})}))", //
        "{MeshRegion({{0,0},{1,0},{0,1},{1,1}},{Polygon({{1,2,3},{2,4,3}})})}");
    check("ConnectedMeshComponents(BoundaryMeshRegion({{0,0},{1,0},{1,1},{0,1},{3,0},{4,0},{4,1}}, {Line({1,2,3,4,1}), Line({5,6,7,5})}))", //
        "{BoundaryMeshRegion({{0,0},{1,0},{1,1},{0,1}},Line({1,2,3,4,1})),BoundaryMeshRegion({{\n3,0},{4,0},{4,1}},Line({1,2,3,1}))}");
    check("ConnectedMeshComponents(BoundaryMeshRegion({{0,0},{4,0},{4,4},{0,4},{1,1},{2,1},{2,2},{1,2}}, {Line({1,2,3,4,1}), Line({5,8,7,6,5})}))", //
        "{BoundaryMeshRegion({{0,0},{4,0},{4,4},{0,4},{1,1},{1,2},{2,2},{2,1}},{Line({1,2,\n3,4,1}),Line({5,6,7,8,5})})}");
  }

  @Test
  public void testLength() {
    check("Length(ConnectedMeshComponents(BoundaryMeshRegion({{0,0},{4,0},{4,4},{0,4},{1,1},{2,1},{2,2},{1,2}}, {Line({1,2,3,4,1}), Line({5,8,7,6,5})})))", //
        "1");
  }

  @Test
  public void testTriangulateMesh() {
    check("TriangulateMesh(Polygon({{0,0},{3,0},{3,3},{2,3},{2,1},{0,1}}))", //
        "MeshRegion({{0,0},{3,0},{3,3},{2,3},{2,1},{0,1}},{Polygon({{6,1,2},{2,3,4},{2,4,\n5},{2,5,6}})})");
    check("TriangulateMesh(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1})))", //
        "MeshRegion({{0,0},{2,0},{2,2},{0,2}},{Polygon({{4,1,2},{2,3,4}})})");
    check("TriangulateMesh(Polygon({{0,0},{0,2},{2,2},{2,0}}))", //
        "MeshRegion({{0,0},{0,2},{2,2},{2,0}},{Polygon({{1,4,3},{3,2,1}})})");
    check("TriangulateMesh(Polygon({{0,0},{2,0},{2,2},{0,2}}), MaxCellMeasure -> 1)", //
        "MeshRegion({{0,0},{2,0},{2,2},{0,2},{0,1},{1,0},{1,1},{2,1},{1,2}},{Polygon({{4,\n5,7},{5,1,6},{7,6,2},{5,6,7},{2,8,7},{8,3,9},{7,9,4},{8,9,7}})})");
    check("TriangulateMesh(MeshRegion({{0,0},{2,0},{2,2},{0,2},{3,0},{3,1}}, {Polygon({1,2,3,4}), Polygon({2,5,6})}))", //
        "MeshRegion({{0,0},{2,0},{2,2},{0,2},{3,0},{3,1}},{Polygon({{4,1,2},{2,3,4},{2,5,\n6}})})");
    check("TriangulateMesh(Polygon({{10^20,0},{10^20+1,0},{10^20+1,1},{10^20,1}}))", //
        "MeshRegion({{100000000000000000000,0},{100000000000000000001,0},{\n100000000000000000001,1},{100000000000000000000,1}},{Polygon({{4,1,2},{2,3,4}})})");
  }

  @Test
  public void testMeshCellCount() {
    check("MeshCellCount(TriangulateMesh(Polygon({{0,0},{2,0},{2,2},{0,2}}), MaxCellMeasure -> 0.1), 2)", //
        "128");
  }

  @Test
  public void testCoordinateTransform() {
    check("CoordinateTransform(\"Polar\" -> \"Cartesian\", {2, Pi/3})", //
        "{1,Sqrt(3)}");
    check("CoordinateTransform(\"Cartesian\" -> \"Polar\", {u, v})", //
        "{Sqrt(u^2+v^2),ArcTan(u,v)}");
    check("CoordinateTransform(\"Cartesian\" -> \"Spherical\", {1, 1, Sqrt(2)})", //
        "{2,Pi/4,Pi/4}");
    check("CoordinateTransform(\"Cylindrical\" -> \"Cartesian\", {{1, 0, 2}, {2, Pi/2, -1}})", //
        "{{1,0,2},{0,2,-1}}");
    check("CoordinateTransform(\"Spherical\" -> \"Cylindrical\", {r, t, p})", //
        "{r*Sin(t),p,r*Cos(t)}");
    check("CoordinateTransform(\"Polar\" -> \"Polar\", {1, 2})", //
        "{1,2}");
    check("CoordinateTransform(\"Cylindrical\" -> \"Spherical\", {1, p, 1})", //
        "{Sqrt(2),Pi/4,p}");
  }

  @Test
  public void testTransformedField() {
    check("TransformedField(\"Polar\" -> \"Cartesian\", r^3*Cos(t), {r, t} -> {x, y})", //
        "x*(x^2+y^2)");
    check("TransformedField(\"Cartesian\" -> \"Polar\", x^2 - y^2, {x, y} -> {r, t})", //
        "r^2*Cos(2*t)");
    check("TransformedField(\"Spherical\" -> \"Cartesian\", r^2, {r, t, p} -> {x, y, z})", //
        "x^2+y^2+z^2");
    check("TransformedField(\"Cartesian\" -> \"Polar\", {x, y}, {x, y} -> {r, t})", //
        "{r,0}");
    check("TransformedField(\"Polar\" -> \"Cartesian\", {1, 0}, {r, t} -> {x, y})", //
        "{x/Sqrt(x^2+y^2),y/Sqrt(x^2+y^2)}");
    check("TransformedField(\"Cartesian\" -> \"Cylindrical\", {-y, x, z}, {x, y, z} -> {r, t, h})", //
        "{0,r,h}");
    check("TransformedField(\"Spherical\" -> \"Cartesian\", {r, 0, 0}, {r, t, p} -> {x, y, z})", //
        "{x,y,z}");
  }
}
