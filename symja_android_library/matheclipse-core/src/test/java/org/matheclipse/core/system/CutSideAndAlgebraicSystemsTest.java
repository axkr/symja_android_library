package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Numerical values of functions with a branch cut at exact arguments on the cut, linear systems
 * with algebraic or symbolic entries, partial fractions with dependent radicals and optimization
 * problems with constant constraints.
 */
public class CutSideAndAlgebraicSystemsTest extends ExprEvaluatorTestCase {

  @Test
  public void testPrincipalValueOnTheCut() {
    // 2*Cos(Pi/7)-3 is negative; its numerical value has a rounding residue in the imaginary part
    check("r=(-1)^(1/7)+(-1)^(13/7)-3; Im(N(Sqrt(r)))>0", //
        "True");
    check("r=(-1)^(1/7)+(-1)^(13/7)-3; Im(N(Log(r)))", //
        "3.14159");
    check("r=(-1)^(1/7)+(-1)^(13/7)-3; N(Sqrt(r),30)", //
        "I*1.09456030633088542835314100742");
    check("r=(-1)^(2/9)+(-1)^(16/9)+(-1)^(4/9)+(-1)^(14/9)-5; N(Log(r))", //
        "1.13803+I*3.14159");
    // below -1 on the real axis and above I on the imaginary axis
    check("s=3/2*((-1)^(1/7)+(-1)^(13/7)-3); {N(ArcSin(s)),N(ArcCos(s)),N(ArcTanh(s))}", //
        "{-1.5708+I*1.19097,3.14159+I*(-1.19097),-0.627682+I*1.5708}");
    check("t=I*((-1)^(1/7)+(-1)^(13/7)); {N(ArcTan(t)),N(ArcSinh(t))}", //
        "{1.5708+I*0.625518,1.1942+I*1.5708}");
    // an imaginary part of the value keeps its side
    check("Im(N(Sqrt(-1-10^-20*I*Sqrt(2))))<0", //
        "True");
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testLinearSolveAlgebraicEntries() {
    // the elimination with I*Sqrt(3) entries took longer than half a minute
    check("m=Table((i+j)*I*Sqrt(3)+Boole(i==j)*(i+1)+Sqrt(3)*i,{i,6},{j,6});" //
        + "Chop(N(m.LinearSolve(m,Range(6))-Range(6),30))", //
        "{0,0,0,0,0,0}");
    check("m=Table(a^(i*j)+Boole(i==j)*b,{i,4},{j,4});" //
        + "Together(m.LinearSolve(m,{1,0,0,1})-{1,0,0,1})", //
        "{0,0,0,0}");
  }

  @Test
  public void testLinearSolveSymbolicEntries() {
    check("LinearSolve({{c,1,0,0},{1,c,1,0},{0,1,c,1},{0,0,1,c}},{0,1,0,0})", //
        "{(1-c^2)/(1-3*c^2+c^4),(-c+c^3)/(1-3*c^2+c^4),-c^2/(1-3*c^2+c^4),c/(1-3*c^2+c^4)}");
    check("LinearSolve({{1,Sqrt(3),0,1},{Sqrt(3),3,0,Sqrt(3)},{0,0,1,0},{1,0,0,1}},{1,Sqrt(3),2,5})", //
        "{5,-4/Sqrt(3),2,0}");
  }

  @Test
  public void testApartProportionalFactors() {
    // Sqrt(3)*x+Sqrt(3)*y-3*Sqrt(5) is Sqrt(3)*(x+y-Sqrt(15))
    check("Apart(1/((x+y-Sqrt(15))*(Sqrt(3)*x+Sqrt(3)*y-3*Sqrt(5))),x)", //
        "1/(Sqrt(3)*(Sqrt(15)-x-y)^2)");
    check("Apart(1/((x-Sqrt(21))*(Sqrt(7)*x-7*Sqrt(3))),x)", //
        "1/(Sqrt(7)*(Sqrt(21)-x)^2)");
  }

  @Test
  public void testPolynomialDivisionRootCoefficients() {
    // a Root object is a constant coefficient
    check("PolynomialQuotientRemainder(x^3+Root(#^3-5&,1)*x+2, x^2+1, x)", //
        "{x,2+x*(-1+Root(-5+#1^3&,1,0))}");
    check("PolynomialRemainder(x^4+Root(#^3-2&,1)/(2+Root(#^3-2&,1))*x, x^2-Root(#^3-2&,1), x)", //
        "Root(-2+#1^3&,1,0)^2+(x*Root(-2+#1^3&,1,0))/(2+Root(-2+#1^3&,1,0))");
  }

  @Test
  public void testIntegratePowerOfIrreducibleCubic() {
    // the rules have no answer; the rational part and a RootSum over the cubic
    check("Integrate(1/(x^3-x+3)^2,x) /. _RootSum->0", //
        "(-4/239+27/239*x+6/239*x^2)/(3-x+x^3)");
    check("Cases(Integrate(1/(x^3-x+3)^2,x),_RootSum,{0,Infinity})", //
        "{RootSum(3-#1+#1^3&,(9*Log(x-#1)+Log(x-#1)*#1)/(-1+3*#1^2)&)}");
    check("f=(x^2+1)/(x^3+2*x+5)^3; r=Integrate(f,x); " //
        + "Table(Chop(N((D(r,x)-f) /. x->p,20)),{p,{-2/3,1/5,7/4}})", //
        "{0,0,0}");
  }

  @Test
  public void testIntegratePseudoEllipticCubeRoot() {
    // b*c^2+9*a*d^2==0: a+b*x^2 is a*(1-3*d*x/c)*(1+3*d*x/c), the factor is a^(-1/3)
    check("f=1/((x+3*I*Sqrt(2))*(2+x^2)^(1/3)); r=Integrate(f,x); " //
        + "Table(Chop(N((D(r,x)-f)/f /. x->p,20)),{p,{2/9,-3/7,1+I}})", //
        "{0,0,0}");
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testIntegrateAlgebraicDifferentiatesBack() {
    // the rules' answer for the whole integrand didn't differentiate back; its partial fractions
    // integrate one by one and leave the non-elementary one unevaluated
    check("f=(72+792*x+2232*x^2-3681*x^3-26857*x^4-30516*x^5+59820*x^6+213216*x^7+116400*x^8" //
        + "-286464*x^9-405824*x^10-147456*x^11)/((-x-2*x^2)^(2/3)" //
        + "*(3*x^3+24*x^4+84*x^5+168*x^6+192*x^7+96*x^8)); r=Integrate(f,x); " //
        + "Table(Chop(N((D(r,x)-f)/f /. x->p,20)),{p,{-1/7,-3/7}})", //
        "{0,0}");
  }

  @Test
  public void testPossibleZeroQSingleValuedSample() {
    // decided by the value at one point, without symbolic rewrites
    check("PossibleZeroQ(3+Tan((2+k)*Pi/5))", //
        "False");
    check("PossibleZeroQ(-3^(9/10)*x*Sec((2+k)*Pi/5)+3*Tan((2+k)*Pi/5))", //
        "False");
    // a rounding residue inside a product is no value
    check("PossibleZeroQ(Tan(x)*(Sin(x)^2+Cos(x)^2-1))", //
        "True");
    check("PossibleZeroQ(Sec(y)^2-Tan(y)^2-1)", //
        "True");
    check("PossibleZeroQ(E^(2*z)-(Cosh(z)+Sinh(z))^2)", //
        "True");
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testIntegrateBinomialHighDegree() {
    // the rules' zero tests on Tan((2*k-1)*Pi/n) terms took ten seconds
    check("f=1/(x^14+3); r=Integrate(f,x); " //
        + "{FreeQ(r,Integrate),Table(Chop(N((D(r,x)-f) /. x->p,20)),{p,{3/10,7/5,-2/3}})}", //
        "{True,{0,0,0}}");
  }

  @Test
  public void testOptimizationConstantConstraints() {
    check("Minimize({x^2-4*x, 3>2}, x)", //
        "{-4,{x->2}}");
    check("Maximize({5-(x+1)^2, 2==2}, x)", //
        "{5,{x->-1}}");
    check("Minimize({(x-3)^2+y^2, x+y==1 && 1<4}, {x,y})", //
        "{2,{x->2,y->-1}}");
    check("Minimize({x^2-4*x, 3<2}, x)", //
        "{Infinity,{x->Indeterminate}}");
    check("MinValue({x^2-4*x, True}, x)", //
        "-4");
  }
}
