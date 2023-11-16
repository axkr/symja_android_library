package org.matheclipse.core.system;

import org.junit.Test;

public class ComplexExpandTest extends ExprEvaluatorTestCase {

  @Test
  public void testComplexExpand() {

    check("ComplexExpand(ArcCot(x+I*y))", //
        "-Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2-I*1/4*Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^\n"
            + "2)+I*1/4*Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)");

    check("ComplexExpand(Log(z), z)", //
        "I*Arg(z)+Log(Im(z)^2+Re(z)^2)/2");
    check("ComplexExpand(Log(2*z), z)", //
        "I*Arg(z)+Log(2)+Log(Im(z)^2+Re(z)^2)/2");
    check("ComplexExpand(Abs(2^z*Log(2*z)), z)", //
        "2^Re(z)*Sqrt((Arg(z)+Im(Log(Im(z)^2+Re(z)^2))/2)^2+(Log(2)+Re(Log(Im(z)^2+Re(z)^\n"
            + "2))/2)^2)");

    check("ComplexExpand(E*E)", //
        "E^2");
    check("ComplexExpand(E^(x + I*y))", //
        "E^x*Cos(y)+I*E^x*Sin(y)");
    check("ComplexExpand((-E)^(x + I*y))", //
        "E^(x-Pi*y)*Cos(Pi*x+y)+I*E^(x-Pi*y)*Sin(Pi*x+y)");
    check("ComplexExpand(Pi^(x + I*y))", //
        "Pi^x*Cos(y*Log(Pi))+I*Pi^x*Sin(y*Log(Pi))");
    check("ComplexExpand((-Pi)^(x + I*y))", //
        "(Pi^x*Cos(Pi*x+y*Log(Pi)))/E^(Pi*y)+(I*Pi^x*Sin(Pi*x+y*Log(Pi)))/E^(Pi*y)");

    check("ComplexExpand(Re(z),z)", //
        "Re(z)");

    // cos(Pi/8),sin(Pi/8) evaled in trace
    check("ComplexExpand(Sqrt(1+I))", //
        "(I*Sqrt(2-Sqrt(2)))/2^(3/4)+Sqrt(2+Sqrt(2))/2^(3/4)");
    check("ComplexExpand((1+I)^(-1/2))", //
        "(-I*1/2*Sqrt(2-Sqrt(2)))/2^(1/4)+Sqrt(2+Sqrt(2))/(2*2^(1/4))");

    check("ComplexExpand(a*(b+c))", //
        "a*b+a*c");
    check("ComplexExpand((3/7)^(x+I*y))", //
        "(3/7)^x*Cos(y*Log(7/3))-I*(3/7)^x*Sin(y*Log(7/3))");
    check("ComplexExpand((3/7)^z,z)", //
        "(3/7)^Re(z)*Cos(Im(z)*Log(7/3))-I*(3/7)^Re(z)*Sin(Im(z)*Log(7/3))");
    check("ComplexExpand(Re(2*z^3-z+1), z)", //
        "1-Re(z)-6*Im(z)^2*Re(z)+2*Re(z)^3");
    check("ComplexExpand(Csch(x+I*y))", //
        "(I*2*Cosh(x)*Sin(y))/(Cos(2*y)-Cosh(2*x))+(-2*Cos(y)*Sinh(x))/(Cos(2*y)-Cosh(2*x))");
    check("ComplexExpand(Tanh(x+I*y))", //
        "(I*Sin(2*y))/(Cos(2*y)+Cosh(2*x))+Sinh(2*x)/(Cos(2*y)+Cosh(2*x))");

    check("ComplexExpand(Log(1-Sqrt(2)))", //
        "I*Pi+Log(-1+Sqrt(2))");
    check("ComplexExpand(Log(1+I))", //
        "I*1/4*Pi+Log(2)/2");

    check("ComplexExpand(a^(I*b), {a,b})", //
        "Cos(Arg(a)*Im(b)-1/2*Log(Im(a)^2+Re(a)^2)*Re(b))/(E^(Arg(a)*Re(b))*(Im(a)^2+Re(a)^\n"
            + "2)^(Im(b)/2))+(-I*Sin(Arg(a)*Im(b)-1/2*Log(Im(a)^2+Re(a)^2)*Re(b)))/(E^(Arg(a)*Re(b))*(Im(a)^\n"
            + "2+Re(a)^2)^(Im(b)/2))");
    check("ComplexExpand(a^(I*b) )", //
        "Cos(1/2*b*Log(a^2))/E^(b*Arg(a))+(I*Sin(1/2*b*Log(a^2)))/E^(b*Arg(a))");

    check("Refine(Arg(a+I*x),{Element(a,Reals), Element(x,Reals)})", //
        "Arg(a+I*x)");
    check("ComplexExpand((-42.0)^y)", //
        "42.0^y*Cos(3.14159*y)+(I*1.0)*42.0^y*Sin(3.14159*y)");
    check("ComplexExpand((-1/3)^y)", //
        "(1/3)^y*Cos(Pi*y)+I*(1/3)^y*Sin(Pi*y)");
    check("ComplexExpand((a+I*x)^y)", //
        "(a^2+x^2)^(y/2)*Cos(y*Arg(a+I*x))+I*(a^2+x^2)^(y/2)*Sin(y*Arg(a+I*x))");
    check("ComplexExpand((-1)^(1/20))", //
        "Cos(Pi/20)+I*Sin(Pi/20)");
    check("ComplexExpand(x^y)", //
        "(x^2)^(y/2)*Cos(y*Arg(x))+I*(x^2)^(y/2)*Sin(y*Arg(x))");

    check("ComplexExpand(ArcTan(x+I*y))", //
        "-Arg(1-I*x-y)/2+Arg(1+I*x-y)/2-I*1/4*Log(x^2+(1-y)^2)+I*1/4*Log(x^2+(1+y)^2)");

    check("ComplexExpand(ProductLog(x+I*y))", //
        "I*Im(ProductLog(x+I*y))+Re(ProductLog(x+I*y))");
    check("ComplexExpand(ProductLog(cc))", //
        "I*Im(ProductLog(cc))+Re(ProductLog(cc))");
    check("ComplexExpand(x^2)", //
        "x^2");
    check("ComplexExpand(Sin(x), x)", //
        "Cosh(Im(x))*Sin(Re(x))+I*Cos(Re(x))*Sinh(Im(x))");
    check("ComplexExpand(Tan(x+I*y))", //
        "Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))");
    check("ComplexExpand(a)", //
        "a");
    check("ComplexExpand(42)", //
        "42");
    check("ComplexExpand((-1)^(1/3))", //
        "1/2+I*1/2*Sqrt(3)");
    check("ComplexExpand((-1)^(4/3))", //
        "-1/2-I*1/2*Sqrt(3)");
    check("ComplexExpand(2^(4/3))", //
        "2*2^(1/3)");
    check("ComplexExpand((-2)^(4/3))", //
        "-2^(1/3)-I*2^(1/3)*Sqrt(3)");

    check("ComplexExpand((-1)^(1/3)*(1+I*Sqrt(3)))", //
        "-1+I*Sqrt(3)");

    check("ComplexExpand(Cos(x+I*y))", //
        "Cos(x)*Cosh(y)-I*Sin(x)*Sinh(y)");
    check("ComplexExpand(Sin(x+I*y))", //
        "Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)");
    check("ComplexExpand(Cot(x+I*y))", //
        "-Sin(2*x)/(Cos(2*x)-Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)-Cosh(2*y))");
    check("ComplexExpand(Csc(x+I*y))", //
        "(-2*Cosh(y)*Sin(x))/(Cos(2*x)-Cosh(2*y))+(I*2*Cos(x)*Sinh(y))/(Cos(2*x)-Cosh(2*y))");
    check("ComplexExpand(Sec(x+I*y))", //
        "(2*Cos(x)*Cosh(y))/(Cos(2*x)+Cosh(2*y))+(I*2*Sin(x)*Sinh(y))/(Cos(2*x)+Cosh(2*y))");
    check("ComplexExpand(Tan(x+I*y))", //
        "Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))");
    check("ComplexExpand(Cos(x))", //
        "Cos(x)");
    check("ComplexExpand(Sin(x))", //
        "Sin(x)");
    check("ComplexExpand(Cot(x))", //
        "-Sin(2*x)/(-1+Cos(2*x))");
    check("ComplexExpand(Csc(x))", //
        "(-2*Sin(x))/(-1+Cos(2*x))");
    check("ComplexExpand(Csc(x+I*y))", //
        "(-2*Cosh(y)*Sin(x))/(Cos(2*x)-Cosh(2*y))+(I*2*Cos(x)*Sinh(y))/(Cos(2*x)-Cosh(2*y))");
    check("ComplexExpand(Sec(x))", //
        "(2*Cos(x))/(1+Cos(2*x))");
    check("ComplexExpand(Tan(x))", //
        "Sin(2*x)/(1+Cos(2*x))");


    check("ComplexExpand(3^(I*x))", //
        "Cos(x*Log(3))+I*Sin(x*Log(3))");
    check("ComplexExpand(Sin(x + I*y))", //
        "Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)");
    check("ComplexExpand(Sin(x),x)", //
        "Cosh(Im(x))*Sin(Re(x))+I*Cos(Re(x))*Sinh(Im(x))");
    check("ComplexExpand(ReIm(Cos(I^x)))", //
        "{Cos(Cos(1/2*Pi*x))*Cosh(Sin(1/2*Pi*x)),-Sin(Cos(1/2*Pi*x))*Sinh(Sin(1/2*Pi*x))}");
    check("ComplexExpand(Re(z^5 - 2 z^3 - z + 1), z)", //
        "1-Re(z)+6*Im(z)^2*Re(z)+5*Im(z)^4*Re(z)-2*Re(z)^3-10*Im(z)^2*Re(z)^3+Re(z)^5");
    check("ComplexExpand(Cos(x + I*y) + Tanh(z), {z})", //
        "Cos(x)*Cosh(y)+(I*Sin(2*Im(z)))/(Cos(2*Im(z))+Cosh(2*Re(z)))-I*Sin(x)*Sinh(y)+Sinh(\n"
            + "2*Re(z))/(Cos(2*Im(z))+Cosh(2*Re(z)))");

    check("ComplexExpand(Re(Log(Sin(Exp(x + I*y)^2))))", //
        "Re(Log(Cosh(E^(2*x)*Sin(2*y))^2*Sin(E^(2*x)*Cos(2*y))^2+Cos(E^(2*x)*Cos(2*y))^2*Sinh(E^(\n"
            + "2*x)*Sin(2*y))^2))/2");
    check("ComplexExpand(Re(Tan(z)),z)", //
        "Sin(2*Re(z))/(Cos(2*Re(z))+Cosh(2*Im(z)))");
    check("ComplexExpand(Tan(x+I*y),z)", //
        "Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))");
    check("ComplexExpand(a + x^2, {x})", //
        "a-Im(x)^2+I*2*Im(x)*Re(x)+Re(x)^2");

    check("ComplexExpand(Sin(x)*Exp(y), {x,y})", //
        "E^Re(y)*Cos(Im(y))*Cosh(Im(x))*Sin(Re(x))+I*E^Re(y)*Cosh(Im(x))*Sin(Im(y))*Sin(Re(x))+" //
            + "I*E^Re(y)*Cos(Im(y))*Cos(Re(x))*Sinh(Im(x))-E^Re(y)*Cos(Re(x))*Sin(Im(y))*Sinh(Im(x))");

    check("ComplexExpand(Re(z) == (z + Conjugate(z))/2, z)", //
        "True");
    check("ComplexExpand(Exp(I*z) == Cos(z) + I*Sin(z))", //
        "True");
    check("ComplexExpand(Conjugate(x+I*y))", //
        "x-I*y");
    check("ComplexExpand(Conjugate(z),z)", //
        "-I*Im(z)+Re(z)");
  }

  @Test
  public void testArcCot() {
    check("ComplexExpand(ArcCot(x+I*y))", //
        "-Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2-I*1/4*Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^\n"
            + "2)+I*1/4*Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)");
    check("ComplexExpand(Re(ArcCot(x+I*y)) )", //
        "-Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2");
    check("ComplexExpand(Im(ArcCot(x+I*y)) )", //
        "-Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^2)/4+Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)/\n"
            + "4");
    check("ComplexExpand(Re(ArcCot(x+I*y))+Im(ArcSinh(x-I*y)))", //
        "-Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2+Arg(x+Sqrt(1+(x-I*y)^2)-I*y)");
  }

}
