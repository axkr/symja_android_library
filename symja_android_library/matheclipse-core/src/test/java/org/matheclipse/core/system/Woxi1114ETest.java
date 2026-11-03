package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Linear algebra, intervals, geometry and argument forms (Woxi round 2026-11-14, part E). */
public class Woxi1114ETest extends ExprEvaluatorTestCase {

  @Test
  public void testMatrixPowerSymbolicExponent() {
    check("MatrixPower({{3,1},{0,3}}, k)", //
        "{{3^k,k/3^(1-k)},\n" //
        + " {0,3^k}}");
    check("MatrixPower({{1,2},{0,1}}, k, {a,b})", //
        "{a+2*b*k,b}");
    check("MatrixPower({{9,0},{0,16}}, 1/2)", //
        "{{3,0},\n" //
        + " {0,4}}");
    check("MatrixPower({{5,1},{0,5}}, 7)", //
        "{{78125,109375},\n" //
        + " {0,78125}}");
  }

  @Test
  public void testDegreesFunctions() {
    check("{SinDegrees(45), CosDegrees(120), TanDegrees(60), SecDegrees(45), SinDegrees(y), SinDegrees(-y), SinDegrees(2)}", //
        "{1/Sqrt(2),-1/2,Sqrt(3),Sqrt(2),SinDegrees(y),-SinDegrees(y),SinDegrees(2)}");
    check("{ArcSinDegrees(1), ArcCosDegrees(0), ArcTanDegrees(-1), ArcTanDegrees(1, 1), ArcCotDegrees(1), ArcSinDegrees(1/3)}", //
        "{90,90,-45,45,45,ArcSinDegrees(1/3)}");
    check("CosDegrees(Interval({0, 60}))", //
        "Interval({1/2,1})");
    check("TanDegrees(Interval({60, 120}))", //
        "Interval({-Infinity,-Sqrt(3)},{Sqrt(3),Infinity})");
    check("ArcTanDegrees(Interval({0, Sqrt(3)}))", //
        "Interval({0,60})");
    check("ArcCscDegrees(Interval({2, 3}))", //
        "Interval({ArcSinDegrees(1/3),30})");
    check("SinDegrees(30.0)", //
        "0.5");
  }

  @Test
  public void testIntervalReciprocalFunctions() {
    check("ArcSec(Interval({3, 4}))", //
        "Interval({ArcCos(1/3),ArcCos(1/4)})");
    check("ArcCsc(Interval({2, 3}))", //
        "Interval({ArcSin(1/3),Pi/6})");
    check("ArcCoth(Interval({2, 3}))", //
        "Interval({ArcTanh(1/3),ArcTanh(1/2)})");
    check("ArcCsch(Interval({-2, 3}))", //
        "Interval({-Infinity,-ArcSinh(1/2)},{ArcSinh(1/3),Infinity})");
    // the reciprocal leaves the domain
    check("ArcSec(Interval({1/3, 3}))", //
        "ArcSec(Interval({1/3,3}))");
    check("Sign(Interval({-7, -5}, {-2, -1}))", //
        "-1");
    check("Sign(Interval({-7, -5}, {2, 3}))", //
        "Interval({-1,-1},{1,1})");
  }

  @Test
  public void testCenteredInterval() {
    check("{Between(Interval({4, 5}), CenteredInterval(4, 2)), Between(7, CenteredInterval(4, 2))}", //
        "{True,False}");
    check("IntervalMemberQ(CenteredInterval(1+I, 1/2+I/2), 5/4+5/4*I)", //
        "True");
    check("IntervalMemberQ(CenteredInterval(1+I, 1/2+I/2), 2+I)", //
        "False");
    check("IntervalIntersection(Interval({0, 3}), CenteredInterval(2, 1/2))", //
        "Interval({3/2,5/2})");
    check("IntervalUnion(Interval({0, 1}), CenteredInterval(3, 1))", //
        "Interval({0,1},{2,4})");
    check("IntervalMemberQ(Interval({1, 2}))[3/2]", //
        "True");
    check("Between(y, Interval({p, q}))", //
        "(p<=y&&y<=q)||(q<=y&&y<=p)");
  }

  @Test
  public void testCirclePointsFractional() {
    check("CirclePoints(3.5)", //
        "{{0.781831,-0.62349},{0.433884,0.900969},{-0.974928,0.222521}}");
    check("CirclePoints(5/2)", //
        "{{Sqrt(5/8+Sqrt(5)/8),1/4*(1-Sqrt(5))},{-Sqrt(5/8-Sqrt(5)/8),1/4*(1+Sqrt(5))}}");
    check("CirclePoints(0.25)", //
        "{}");
  }

  @Test
  public void testSpherePoints() {
    check("p = SpherePoints(13); {Length(p), Max(Abs(Map(Norm, p) - 1)) < 10^-14, p[[7]]}", //
        "{13,True,{-0.460907,-0.887448,0.0}}");
    check("SpherePoints(5)", //
        "SpherePoints(5)");
  }

  @Test
  public void testRegionSymmetricDifference() {
    check("RegionSymmetricDifference(Disk({0,0}, 2), Rectangle({0,0}, {3,3}))", //
        "BooleanRegion(#1⊻#2&,{Disk({0,0},2),Rectangle({0,0},{3,3})})");
    check("RegionSymmetricDifference(Rectangle({0,0}), Rectangle({0,0}), Disk())", //
        "Disk({0,0})");
  }

  @Test
  public void testQRDecomposition() {
    check("QRDecomposition({{2, 0}, {0, 3}})", //
        "{{{1,0},{0,1}},{{2,0},{0,3}}}");
    check("QRDecomposition({{1, 0}, {1, 0}})", //
        "{{{1/Sqrt(2),1/Sqrt(2)}},{{Sqrt(2),0}}}");
    check("QRDecomposition({{0, 0, 0}, {0, 0, 0}})", //
        "{\n" //
        + "{{1,0}},\n" //
        + "{{0,0,0}}}");
    check("QRDecomposition({{2.0, 1.0}, {0.0, 3.0}})", //
        "{\n" //
        + "{{1.0,0.0},\n" //
        + " {0.0,1.0}},\n" //
        + "{{2.0,1.0},\n" //
        + " {0.0,3.0}}}");
    check("{q, r} = QRDecomposition({{2.0, 1.0}, {1.0, 3.0}, {0.0, 1.0}}); {Dimensions(q), Dimensions(r), Chop(Transpose(q).r - {{2.0, 1.0}, {1.0, 3.0}, {0.0, 1.0}})}", //
        "{{2,3},{2,2},{{0,0},{0,0},{0,0}}}");
  }

  @Test
  public void testSingularValueListSpecs() {
    check("SingularValueList({{5, 0, 0}, {0, 0, 0}, {0, 0, 1}}, 3)", //
        "{5,1,0}");
    check("SingularValueList({{5, 0, 0}, {0, 0, 0}, {0, 0, 1}}, -1)", //
        "{0}");
    check("SingularValueList({{5, 0, 0}, {0, 0, 0}, {0, 0, 1}}, {2})", //
        "{1}");
    check("SingularValueList({{5.0, 0, 0}, {0, 4, 0}, {0, 0, 0.001}}, 3, Tolerance -> 0.01)", //
        "{5.0,4.0,0.0}");
  }

  @Test
  public void testNielsenPolyLogNegativeOrder() {
    check("PolyLog(-1, 4, w)", //
        "(-w*Log(1-w)^3)/(6*(1-w))");
    check("PolyLog(-3, 2, w)", //
        "(3*w^2)/(1-w)^2+(3*w^3)/(1-w)^3+(-w*Log(1-w))/(1-w)+(-3*w^2*Log(1-w))/(1-w)^2+(-\n" //
        + "2*w^3*Log(1-w))/(1-w)^3");
  }

  @Test
  public void testDivisorSigmaNegative() {
    check("DivisorSigma(2, -10)", //
        "130");
  }

  @Test
  public void testBoxFunctionsSeveralArguments() {
    check("{HeavisidePi(y, 2), HeavisideLambda(y, 1/4), UnitBox(u, v), UnitBox(u, v, 1/4), UnitTriangle(u, 1/2), UnitBox(1/4, 3/4)}", //
        "{0,3/4*HeavisideLambda(y),UnitBox(u,v),UnitBox(u,v),UnitTriangle(u)/2,0}");
  }

  @Test
  public void testParentDirectoryLevels() {
    // the root is its own parent
    check("ParentDirectory(\"/a/b/c\", 9) === ParentDirectory(\"/a/b/c\", 3)", //
        "True");
    check("ParentDirectory(\"/a/b/c\", 0)", //
        "ParentDirectory(/a/b/c,0)");
  }

  @Test
  public void testDotInexactOne() {
    check("{Sqrt(3), y}.{1.0, 1.0}", //
        "1.73205+y");
    check("Pi*1.0", //
        "3.14159");
  }

  @Test
  public void testVandermondeMatrixRepeatedNodes() {
    check("VandermondeMatrix({u, v, u})", //
        "{{1,u,u^2},\n" //
        + " {1,v,v^2},\n" //
        + " {0,1,2*u}}");
  }

  @Test
  public void testRandomColorTemplate() {
    check("RandomColor(RGBColor(y, _, _))", //
        "RandomColor(RGBColor(y,_,_))");
    check("RandomColor(Hue(1/4, _, 1))[[{1, 3}]]", //
        "Hue(0.25,1.0)");
  }

  @Test
  public void testEigenvectorsSymbolicCubic() {
    check("Length(Eigenvectors({{p, 1, 0}, {1, q, 1}, {0, 1, r}}))", //
        "3");
  }
}
