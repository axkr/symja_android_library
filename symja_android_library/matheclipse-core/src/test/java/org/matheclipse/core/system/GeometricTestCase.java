package org.matheclipse.core.system;

public class GeometricTestCase extends ExprEvaluatorTestCase {
  public GeometricTestCase(String name) {
    super(name);
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


}
