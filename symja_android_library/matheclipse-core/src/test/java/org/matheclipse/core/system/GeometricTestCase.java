package org.matheclipse.core.system;

public class GeometricTestCase extends ExprEvaluatorTestCase {
  public GeometricTestCase(String name) {
    super(name);
  }

  public void testAASTriangle() {
    check("AASTriangle(Pi/2, Pi/3, b)", //
        "Triangle({{0,0},{b/2,0},{0,1/2*Sqrt(3)*b}})");
    check("AASTriangle(Pi/6, Pi/3, 1)", //
        "Triangle({{0,0},{2,0},{3/2,Sqrt(3)/2}})");
  }

  public void testASATriangle() {
    // message ASATriangle: The sum of angles Pi/2 and Pi/2 should be less than Pi.
    check("ASATriangle(Pi/2, a, Pi/2)", //
        "ASATriangle(Pi/2,a,Pi/2)");

    check("ASATriangle(Pi/2, b, Pi/3)", //
        "Triangle({{0,0},{b,0},{0,Sqrt(3)*b}})");

    check("ASATriangle(Pi/6, 1, Pi/3)", //
        "Triangle({{0,0},{1,0},{3/4,Sqrt(3)/4}})");
  }

  public void testArcLength() {
    check("ArcLength(Line({{a,b},{c,d},{e,f}}))", //
        "Sqrt((a-c)^2+(b-d)^2)+Sqrt((c-e)^2+(d-f)^2)");
    check("ArcLength(Circle({a,b},{r1,r2}))", //
        "4*r2*EllipticE(1-r1^2/r2^2)");
    check("ArcLength(Circle({a,b},{r1,r2},{t1,t2}))", //
        "r2*(-EllipticE(t1,1-r1^2/r2^2)+EllipticE(t2,1-r1^2/r2^2))*UnitStep(2*Pi-Abs(-t1+t2))+\n"//
            + "4*r2*EllipticE(1-r1^2/r2^2)*UnitStep(-2*Pi+Abs(-t1+t2))");

  }

  public void testArea() {
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

  public void testPerimeter() {
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
  }

  public void testSASTriangle() {
    check("SASTriangle(1, Pi/3, 1)", //
        "Triangle({{0,0},{1,0},{1/2,Sqrt(3)/2}})");
    check("SASTriangle(1, Pi/2, 2)", //
        "Triangle({{0,0},{Sqrt(5),0},{4/Sqrt(5),2/Sqrt(5)}})");
  }

  public void testSSSTriangle() {
    check("SSSTriangle(10,10,10)", //
        "Triangle({{0,0},{10,0},{5,5*Sqrt(3)}})");
    check("SSSTriangle(3,4,5)", //
        "Triangle({{0,0},{5,0},{16/5,12/5}})");
  }

}
