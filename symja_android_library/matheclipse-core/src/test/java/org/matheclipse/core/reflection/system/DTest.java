package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class DTest extends ExprEvaluatorTestCase {

  @Test
  public void testDNonConstants() {
    check("D(x^2, x, NonConstants -> {a})", //
        "2*x");
    check("D(x^3, x, NonConstants -> {a, b})", //
        "3*x^2");
    check("D(Sin(x), x, NonConstants -> {a})", //
        "Cos(x)");
    check("D(x^2, {x, 2}, NonConstants -> {a})", //
        "2");
    check("D(x^2 * y, x, NonConstants -> {a})", //
        "2*x*y");
    check("D(x^2, x, Assumptions -> x > 0)", //
        "D(x^2,x,Assumptions->x>0)");
    check("D(a * x^2, x, NonConstants -> {a})", //
        "2*a*x+x^2*D(a,x,NonConstants->{a})");
    check("D(a, x, NonConstants -> {a})", //
        "D(a,x,NonConstants->{a})");

    // no option - a is a constant
    check("D(a*x^2, x)", //
        "2*a*x");
    check("D(x^2, x, NonConstants -> {})", //
        "2*x");
    // chain rule through a "non constant" expression
    check("D(Sin(a*x), x, NonConstants -> {a})", //
        "Cos(a*x)*(a+x*D(a,x,NonConstants->{a}))");
    // the option must survive the reduction of D(f, x, y) to D(D(f, x), y)
    check("D(a*x*y, x, y, NonConstants -> {a})", //
        "a+x*D(a,x,NonConstants->{a})+y*D(a,y,NonConstants->{a})+x*y*D(a,x,y,NonConstants->{a})");
  }

  @Test
  public void testDSeries() {
    check("Series(F(x,z),{x, g(y), 2}, {z, a, 2}) //InputForm", //
        "SeriesData(x,g(y),{SeriesData(z,a,{F(g(y),a),Derivative(0,1)[F][g(y),a],Derivative(\n" //
            + "0,2)[F][g(y),a]/2},0,3,1),SeriesData(z,a,{Derivative(1,0)[F][g(y),a],Derivative(\n" //
            + "1,1)[F][g(y),a],Derivative(1,2)[F][g(y),a]/2},0,3,1),SeriesData(z,a,{Derivative(\n" //
            + "2,0)[F][g(y),a]/2,Derivative(2,1)[F][g(y),a]/2,Derivative(2,2)[F][g(y),a]/4},0,3,\n" //
            + "1)},0,3,1)");

    check("D(Series(F(x,z),{x, g(y), 2}, {z, a, 2}), y)//InputForm", //
        "SeriesData(x,g(y),{},2,2,1)");

  }

  @Test
  public void testDZTransform() {
    check("D(ZTransform(a, n, z), {z, 2})", //
        "a*(-2/(1-z)^2+(-2*z)/(1-z)^3)");
    check("D(ZTransform(f(n), n, z), {z, 2}) ", //
        "D(ZTransform(f(n),n,z),{z,2})");
    check("D(ZTransform(f(a), n, z), {z, 2})", //
        "(-2/(1-z)^2+(-2*z)/(1-z)^3)*f(a)");
    // TODO
    check("ZTransform((1+n)^2*f(n),n,z)", //
        "-2*z*D(ZTransform(f(n),n,z),z)+ZTransform(f(n),n,z)+z*(D(ZTransform(f(n),n,z),z)+z*(Derivative(\n"
            + "0,1)[D][ZTransform(f(n),n,z),z]+D(ZTransform(f(n),n,z),z)*Derivative(1,0)[D][ZTransform(f(n),n,z),z]))");
    // TODO
    check("ZTransform(n^3*f(n),n,z)", //
        "-z*(D(ZTransform(f(n),n,z),z)+3*z*Derivative(0,1)[D][ZTransform(f(n),n,z),z]+z^2*Derivative(\n"
            + "0,2)[D][ZTransform(f(n),n,z),z]+3*z*D(ZTransform(f(n),n,z),z)*Derivative(1,0)[D][ZTransform(f(n),n,z),z]+z^\n"
            + "2*Derivative(0,1)[D][ZTransform(f(n),n,z),z]*Derivative(1,0)[D][ZTransform(f(n),n,z),z]+z^\n"
            + "2*D(ZTransform(f(n),n,z),z)*Derivative(1,0)[D][ZTransform(f(n),n,z),z]^2+2*z^2*D(ZTransform(f(n),n,z),z)*Derivative(\n"
            + "1,1)[D][ZTransform(f(n),n,z),z]+z^2*D(ZTransform(f(n),n,z),z)^2*Derivative(2,0)[D][ZTransform(f(n),n,z),z])");
  }


  @Test
  public void testD001() {
    check("(1-x^2)/(1-x)^2+(2*x)/(-1+x)", //
        "(2*x)/(-1+x)+(1-x^2)/(1-x)^2");

    check("D(f(x)^(-5),x)", //
        "(-5*f'(x))/f(x)^6");
    check("D(f(x)^(-2),{x,3})", //
        "(-24*f'(x)^3)/f(x)^5+(18*f'(x)*f''(x))/f(x)^4+(-2*Derivative(3)[f][x])/f(x)^3");

    check("D(x/(-1+x^3),{x,4})", //
        "x*((-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3)+4*((-162*x^\n" //
            + "6)/(1-x^3)^4+(-108*x^3)/(1-x^3)^3-6/(1-x^3)^2)");
    check("D(1/(-1+x^3),{x,4})", //
        "(-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3");

    check("D((-x-2*x^2)/(-1+x^3),{x,4})", //
        "(-x-2*x^2)*((-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3)+4*(-\n" //
            + "1-4*x)*((-162*x^6)/(1-x^3)^4+(-108*x^3)/(1-x^3)^3-6/(1-x^3)^2)-24*((-18*x^4)/(1-x^\n" //
            + "3)^3+(-6*x)/(1-x^3)^2)");
    check("D(f(x) / g(x), {x, 2})", //
        "(-2*f'(x)*g'(x))/g(x)^2+f''(x)/g(x)+f(x)*((2*g'(x)^2)/g(x)^3-g''(x)/g(x)^2)");
    check("D(f(x) / g(x), {x, 3})", //
        "(-3*g'(x)*f''(x))/g(x)^2+3*f'(x)*((2*g'(x)^2)/g(x)^3-g''(x)/g(x)^2)+Derivative(3)[f][x]/g(x)+f(x)*((-\n" //
            + "6*g'(x)^3)/g(x)^4+(6*g'(x)*g''(x))/g(x)^3-Derivative(3)[g][x]/g(x)^2)");

    check("D(Sin(x) * Cos(x), {x, 2})", //
        "-4*Cos(x)*Sin(x)");
    check("D(Sin(x) * Cos(x), {x, 3})", //
        "-4*Cos(x)^2+4*Sin(x)^2");
    check("D(Sin(x) * Cos(x), {x, 6})", //
        "-64*Cos(x)*Sin(x)");
    check("D((-1/2-x+x^2)/(-1/2-x/2+5/2*x^2-5/2*x^3),{x,2})", //
        "2/(-1/2-x/2+5/2*x^2-5/2*x^3)+(2*(-1+2*x)*(1/2-5*x+15/2*x^2))/(1/2+x/2-5/2*x^2+5/\n"
            + "2*x^3)^2+(-1/2-x+x^2)*((-2*(1/2-5*x+15/2*x^2)^2)/(1/2+x/2-5/2*x^2+5/2*x^3)^3+(-5+\n"
            + "15*x)/(1/2+x/2-5/2*x^2+5/2*x^3)^2)");
    check("D(Boole(f(x)),{x,10})", //
        "0");
    check("D(Boole(f(x)),x)", //
        "0");
    check("D(Boole(f(x)),{x,n})", //
        "D(Boole(f(x)),{x,n})");
    // check("D(a*3^x,{x,n})", //
    // "a*E^x");
    // TODO
    check("D(a*Exp(x),{x,n})", //
        "a*E^x");
    check("D(a*f(x),{x,10})", //
        "a*Derivative(10)[f][x]");
    check("D(a*Exp(x),{x,3})", //
        "a*E^x");
    check("D(Tan(x),{x,3})", //
        "2*Sec(x)^4+4*Sec(x)^2*Tan(x)^2");
    check("D(Cot(x),{x,5})", //
        "-16*Cot(x)^4*Csc(x)^2-88*Cot(x)^2*Csc(x)^4-16*Csc(x)^6");

    check("D((2*Sqrt(2)*(x^3))/(x^4),x)", //
        "(-2*Sqrt(2))/x^2");
    check("D(z^n*E^(z),{z,n})", //
        "E^z*n!*Hypergeometric1F1(-n,1,-z)");
    check("D(z^n*E^(-z),{z,n})", //
        "(n!*Hypergeometric1F1(-n,1,z))/E^z");

    check("D(Hypergeometric0F1(a,x), x)", //
        "Hypergeometric0F1(1+a,x)/a");
    check("D(Hypergeometric1F1(a,b,x), x)", //
        "(a*Hypergeometric1F1(1+a,1+b,x))/b");
    check("D(Hypergeometric2F1(a,b,c,f(x)), x)", //
        "(a*b*Hypergeometric2F1(1+a,1+b,1+c,f(x))*f'(x))/c");
    check("D(Hypergeometric2F1Regularized(a,b,c,f(x)), x)", //
        "a*b*Hypergeometric2F1Regularized(1+a,1+b,1+c,f(x))*f'(x)");
    check("D(HypergeometricPFQ({}, {}, x), x)", //
        "E^x");
    check("D(HypergeometricPFQ({a}, {}, x), x)", //
        "a/(1-x)^(1+a)");
    check("D(HypergeometricPFQ({a,b}, {u,v,w}, x), x)", //
        "(a*b*HypergeometricPFQ({1+a,1+b},{1+u,1+v,1+w},x))/(u*v*w)");
    check("D(HypergeometricPFQ({a, b,c,d,e,f}, {u,v,w,y,z}, x), x)", //
        "(a*b*c*d*e*f*HypergeometricPFQ({1+a,1+b,1+c,1+d,1+e,1+f},{1+u,1+v,1+w,1+y,1+z},x))/(u*v*w*y*z)");

    check("D(HarmonicNumber(Sin(Cos(x)), y), x)", //
        "y*Cos(Cos(x))*Sin(x)*(HarmonicNumber(Sin(Cos(x)),1+y)-Zeta(1+y))");
    check("D(Sin(f(x)),{x,3})", //
        "-Cos(f(x))*f'(x)^3-3*Sin(f(x))*f'(x)*f''(x)+Cos(f(x))*Derivative(3)[f][x]");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x), {x, n})", //
        "D(f(x),{x,n})");

    check("D(PolyLog(n,z),z)", //
        "PolyLog(-1+n,z)/z");
    check("D(PolyLog(n,p,z),z)", //
        "PolyLog(-1+n,p,z)/z");

    check("D(Polygamma(x),{x,n})", //
        "PolyGamma(n,x)");

    check("D(Sin(x)==x^2==y*x^4,x )", //
        "Cos(x)==2*x==4*x^3*y");

    // message - D: D called with 0 arguments; 1 or more arguments are expected.
    check("D( )", //
        "D()");

    // github #302 - special case - only 1 argument
    check("D(f)", //
        "f");
    // github #302 test case
    check("Integrate(D(x),x)", //
        "x^2/2");
    // github #302 test case
    check("Integrate(D(y)*D(x),x,y)", //
        "1/4*x^2*y^2");
    // github #302 test case
    check("Integrate(D(x),x)*Integrate(D(y),y)", //
        "1/4*x^2*y^2");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");

    check("D(Surd(x,3),x)", //
        "1/(3*Surd(x,3)^2)");
    check("D(Surd(x,-3),x)", //
        "-1/(3*x*Surd(x,3))");
    check("D(Surd(x,4),x)", //
        "1/(4*Surd(x,4)^3)");
    check("D(Surd(x,-4),x)", //
        "-1/(4*Surd(x,4)^5)");
    check("D(Surd(x,5),x)", //
        "1/(5*Surd(x,5)^4)");
    check("D(Surd(x,-5),x)", //
        "-1/(5*x*Surd(x,5))");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x)+ g(x)+h(x), {x, n})", //
        "D(f(x),{x,n})+D(g(x),{x,n})+D(h(x),{x,n})");
    check("D(Sin(x)+ Cos(y), {x, n})", //
        "Piecewise({{Cos(y),n==0}},0)+Sin(1/2*n*Pi+x)");
    check("D(Sin(x),{x,0.5})", //
        "D(Sin(x),{x,0.5})");
    check("D(Sin(x),{x,f(a)})", //
        "D(Sin(x),{x,f(a)})");
    check("D(Sin(x),{x,f(a)+I})", //
        "D(Sin(x),{x,I+f(a)})");
    check("D(x^a, {x,n})", //
        "x^(a-n)*FactorialPower(a,n)");
    check("D(x^a, {x,3})", //
        "((-2+a)*(-1+a)*a)/x^(3-a)");
    check("D(a^x, {x,2})", //
        "a^x*Log(a)^2");
    check("D(Sin(x),{x,n})", //
        "Sin(1/2*n*Pi+x)");
    check("D(InverseFunction(f)[x],x)", //
        "1/f'(InverseFunction(f)[x])");

    // message - D: 2*x is not a valid variable.
    check("D(2*x, 2*x)", //
        "D(2*x,2*x)");

    check("D(E^f(x),x)", //
        "E^f(x)*f'(x)");

    check("D(AiryAi(Sqrt(x)),x)", //
        "AiryAiPrime(Sqrt(x))/(2*Sqrt(x))");
    check("D(AiryAiPrime(Sqrt(x)),x)", //
        "AiryAi(Sqrt(x))/2");

    check("D(Piecewise({{x^2, x < 0}, {x, x > 0}}),x)", //
        "Piecewise({{2*x,x<0},{1,x>0}},0)");
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x != 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x!=1}},0)");

    // TODO simplify result to 1
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x < 1 || x > 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x<1||x>1}},0)");

    check("D(Factorial(b*x),x)", //
        "b*Gamma(1+b*x)*PolyGamma(0,1+b*x)");
    check("D(E^(E^x + x),x)", //
        "(1+E^x)*E^(E^x+x)");
    check("D((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2),x)", //
        "(-4*e*(Sqrt(d)*Sqrt(-e)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^\n"
            + "2)");
    check("D(ArcTan(x,y),x)", //
        "-y/(x^2+y^2)");
    check("D(ArcTan(x,y),y)", //
        "x/(x^2+y^2)");
    check("D(ArcTan(x,x),x)", //
        "0");
    check("D(Cosh(b*x),x)", //
        "b*Sinh(b*x)");
    check("D(Sinh(x),x)", //
        "Cosh(x)");
    check("D(Sinc(x),x)", //
        "Cos(x)/x-Sin(x)/x^2");
    check("D(SinIntegral(x),x)", //
        "Sinc(x)");
    check("D(SinhIntegral(x),x)", //
        "Sinh(x)/x");
    check("D(CoshIntegral(x),x)", //
        "Cosh(x)/x");
    check("D(Cosh(b*x)*CoshIntegral(b*x),x)", //
        "Cosh(b*x)^2/x+b*CoshIntegral(b*x)*Sinh(b*x)");

    // gradient
    check("D(f(x, y), {{x, y}})", //
        "{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
    // hessian matrix
    check("D(f(x, y), {{x, y}}, {{x, y}})", //
        "{{Derivative(2,0)[f][x,y],Derivative(1,1)[f][x,y]},{Derivative(1,1)[f][x,y],Derivative(\n"
            + "0,2)[f][x,y]}}");
    // generalization of Hessian matrix for n > 2
    check("D(f(x, y, z), {{x, y, z}, 3})", //
        "{{{Derivative(3,0,0)[f][x,y,z],Derivative(2,1,0)[f][x,y,z],Derivative(2,0,1)[f][x,y,z]},{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "1,2,0)[f][x,y,z],Derivative(0,3,0)[f][x,y,z],Derivative(0,2,1)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]},{Derivative(\n"
            + "1,0,2)[f][x,y,z],Derivative(0,1,2)[f][x,y,z],Derivative(0,0,3)[f][x,y,z]}}}");

    check("Refine(D(Abs(x),x), Element(x, Reals))", //
        "x/Abs(x)");

    check("D(HarmonicNumber(x), x)", //
        "Pi^2/6-HarmonicNumber(x,2)");

    check("D(ArcCsc(x),{x,2})", //
        "(-1+2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");
    check("D(ArcSec(x),{x,2})", //
        "(1-2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");

    check("D(x*f(x)*f'(x), x)", //
        "f(x)*f'(x)+x*f'(x)^2+x*f(x)*f''(x)");
    check("D(f(x), x)", //
        "f'(x)");
    check("Sin'(2)", //
        "Cos(2)");
    check("D(Sin(x) + Cos(2*x), {x, 2}) /. x -> 0", //
        "-4");

    check("D(Sin(t), {t, 1})", //
        "Cos(t)");
    check("D(Derivative(0,1,0)[f][x,x*y,z+x^2],x)", //
        "2*x*Derivative(0,1,1)[f][x,x*y,x^2+z]+y*Derivative(0,2,0)[f][x,x*y,x^2+z]+Derivative(\n"
            + "1,1,0)[f][x,x*y,x^2+z]");

    check("D(x^3 + x^2, x)", //
        "2*x+3*x^2");
    check("D(x^3 + x^2, {x, 2})", //
        "2+6*x");
    check("D(Sin(Cos(x)), x)", //
        "-Cos(Cos(x))*Sin(x)");
    check("D(Sin(x), {x, 2})", //
        "-Sin(x)");
    check("D(Cos(t), {t, 2})", //
        "-Cos(t)");
    check("D(y, x)", //
        "0");
    check("D(x, x)", //
        "1");
    check("D(f(x), x)", //
        "f'(x)");

    check("D(f(x, x), x)", //
        "Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]");
    // chain rule
    check("D(f(2*x+1, 2*y, x+y), x)", //
        "Derivative(0,0,1)[f][1+2*x,2*y,x+y]+2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]");
    check("D(f(x^2, x, 2*y), {x,2}, y) // Expand", //
        "2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(\n"
            + "1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]");

    check("D(x ^ 3 * Cos(y), {{x, y}})", //
        "{3*x^2*Cos(y),-x^3*Sin(y)}");
    check("D(Sin(x) * Cos(y), {{x,y}, 2})", //
        "{{-Cos(y)*Sin(x),-Cos(x)*Sin(y)},{-Cos(x)*Sin(y),-Cos(y)*Sin(x)}}");
    check("D(2/3*Cos(x) - 1/3*x*Cos(x)*Sin(x) ^ 2,x)//Expand  ", //
        "-2/3*Sin(x)-2/3*x*Cos(x)^2*Sin(x)-1/3*Cos(x)*Sin(x)^2+1/3*x*Sin(x)^3");
    check("D(f(#1), {#1,2})", //
        "f''(#1)");
    check("D((#1&)*(t),{t,4})", //
        "0");
    // TODO allow Attributes(f) = {HoldAll}
    check("Attributes(f) = {HoldAll}; Apart(f''(x + x))", //
        "f''(2*x)");
    check("Attributes(f) = {}; Apart(f''(x + x)) ", //
        "f''(2*x)");

    check("D({#^2}, #)", //
        "{2*#1}");

    // Koepf Seite 40-43
    check("D(Sum(k*x^k, {k,0,10}),x)", //
        "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9");
    check("D((x^2+3)*(3*x+2),x)", //
        "2*x*(2+3*x)+3*(3+x^2)");
    check("D(Sin(x^2),x)", //
        "2*x*Cos(x^2)");
    check("D((1+x^2)^Sin(x),x)", //
        "(1+x^2)^Sin(x)*(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))");
    check("D(Exp(x),x)", //
        "E^x");
    check("D((x^2+3)/(3*x+2),x)", //
        "(2*x)/(2+3*x)+(-3*(3+x^2))/(2+3*x)^2");

    // others -----
    check("D(InverseErf(x),x)", //
        "1/2*E^InverseErf(x)^2*Sqrt(Pi)");

    check("D(f(Sin(x)), {x, 3})", //
        "-Cos(x)*f'(Sin(x))-3*Cos(x)*Sin(x)*f''(Sin(x))+Cos(x)^3*Derivative(3)[f][Sin(x)]");

    check("D(f(Sqrt(x^2 + 1)), {x, 3})", //
        "((3*x^3)/(1+x^2)^(5/2)+(-3*x)/(1+x^2)^(3/2))*f'(Sqrt(1+x^2))+(3*x*(-x^2/(1+x^2)^(\n" //
            + "3/2)+1/Sqrt(1+x^2))*f''(Sqrt(1+x^2)))/Sqrt(1+x^2)+(x^3*Derivative(3)[f][Sqrt(1+x^\n" //
            + "2)])/(1+x^2)^(3/2)");

    check("f(x_) := x^5 + 6*x^3", //
        "");
    check("D(f(x), x)", //
        "18*x^2+5*x^4");
    check("f'(x)", //
        "18*x^2+5*x^4");
    check("D(f(x), x) /. x->5", //
        "3575");
    check("D(f(x), {x, 3}) /. x -> -1", //
        "96");

    check("D(x^2 * E^(5*y), x)", //
        "2*E^(5*y)*x");
    check("D(x^2 * E^(5*y), y)", //
        "5*E^(5*y)*x^2");
    check("D(x^2 * E^(5*y), {x,2}, {y,3})", //
        "250*E^(5*y)");

    check("D(Sin(g(x)) + g''(x), x)", //
        "Cos(g(x))*g'(x)+Derivative(3)[g][x]");

    check("D(Subscript(x, 1)^2 + Sin(Subscript(x, 1)*Subscript(x, 2)), Subscript(x, 1))", //
        "2*Subscript(x,1)+Cos(Subscript(x,1)*Subscript(x,2))*Subscript(x,2)");

    check("D({3*t^2, 4*t, Sin(t)}, t)", //
        "{6*t,4,Cos(t)}");
    check("D({x^n, {Exp(x), Log(x)}, {Sin(x), Cos(x), Tan(x)}}, x)", //
        "{n/x^(1-n),{E^x,1/x},{Cos(x),-Sin(x),Sec(x)^2}}");
    check("D(x^2 + 5*y^3, {{x, y}})", //
        "{2*x,15*y^2}");
    check("D(x^2 + 5*y^3, {{x, y}, 2})", //
        "{{2,0},{0,30*y}}");
    check("D((x^2+5*y^3+z^4)/E^w,{{x,y}})", //
        "{(2*x)/E^w,(15*y^2)/E^w}");
    check("D(E^(-w)*(x^2 + 5*y^3 + z^4), {{{x, y}, {z, w}}})", //
        "{{(2*x)/E^w,(15*y^2)/E^w},{(4*z^3)/E^w,-(x^2+5*y^3+z^4)/E^w}}");
    check("D(ExpIntegralEi(b*x),x)", //
        "E^(b*x)/x");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");
    check("D(StruveH(x,y),x)", //
        "Derivative(1,0)[StruveH][x,y]");
    check("D(StruveL(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveL(-1+n,x)+StruveL(1+n,x))");
    check("D(StruveL(x,y),x)", //
        "Derivative(1,0)[StruveL][x,y]");

    check("D(JacobiAmplitude(x,y),x)", //
        "JacobiDN(x,y)");
    check("D(JacobiAmplitude(x,y),y)", //
        "((x*(-1+y)+EllipticE(JacobiAmplitude(x,y),y))*JacobiDN(x,y)-y*JacobiCN(x,y)*JacobiSN(x,y))/(\n"
            + //
            "2*(-1+y)*y)");

    check("D(CatalanNumber(x),x)", //
        "CatalanNumber(x)*(Log(4)+PolyGamma(0,1/2+x)-PolyGamma(0,2+x))");
    check("D(Sin(x^5)/x^10,x)", //
        "(5*Cos(x^5))/x^6+(-10*Sin(x^5))/x^11");
    check("D(AppellF1(a,b1,b2,c,f1(x),f2(x)),x)", //
        "(a*b1*AppellF1(1+a,1+b1,b2,1+c,f1(x),f2(x))*f1'(x))/c+(a*b2*AppellF1(1+a,b1,1+b2,\n" //
            + "1+c,f1(x),f2(x))*f2'(x))/c");
  }

  @Test
  public void testD002() {

    check("D(f(x, x), x)", //
        "Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]");

    check("D(f(x, y), {{x, y}})", //
        "{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
    check("(1-x^2)/(1-x)^2+(2*x)/(-1+x)", //
        "(2*x)/(-1+x)+(1-x^2)/(1-x)^2");

    check("D(f(x)^(-5),x)", //
        "(-5*f'(x))/f(x)^6");
    check("D(f(x)^(-2),{x,3})", //
        "(-24*f'(x)^3)/f(x)^5+(18*f'(x)*f''(x))/f(x)^4+(-2*Derivative(3)[f][x])/f(x)^3");

    check("D(x/(-1+x^3),{x,4})", //
        "x*((-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3)+4*((-162*x^\n" //
            + "6)/(1-x^3)^4+(-108*x^3)/(1-x^3)^3-6/(1-x^3)^2)");
    check("D(1/(-1+x^3),{x,4})", //
        "(-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3");

    check("D((-x-2*x^2)/(-1+x^3),{x,4})", //
        "(-x-2*x^2)*((-1944*x^8)/(1-x^3)^5+(-1944*x^5)/(1-x^3)^4+(-360*x^2)/(1-x^3)^3)+4*(-\n" //
            + "1-4*x)*((-162*x^6)/(1-x^3)^4+(-108*x^3)/(1-x^3)^3-6/(1-x^3)^2)-24*((-18*x^4)/(1-x^\n" //
            + "3)^3+(-6*x)/(1-x^3)^2)");
    check("D(f(x) / g(x), {x, 2})", //
        "(-2*f'(x)*g'(x))/g(x)^2+f''(x)/g(x)+f(x)*((2*g'(x)^2)/g(x)^3-g''(x)/g(x)^2)");
    check("D(f(x) / g(x), {x, 3})", //
        "(-3*g'(x)*f''(x))/g(x)^2+3*f'(x)*((2*g'(x)^2)/g(x)^3-g''(x)/g(x)^2)+Derivative(3)[f][x]/g(x)+f(x)*((-\n" //
            + "6*g'(x)^3)/g(x)^4+(6*g'(x)*g''(x))/g(x)^3-Derivative(3)[g][x]/g(x)^2)");

    check("D(Sin(x) * Cos(x), {x, 2})", //
        "-4*Cos(x)*Sin(x)");
    check("D(Sin(x) * Cos(x), {x, 3})", //
        "-4*Cos(x)^2+4*Sin(x)^2");
    check("D(Sin(x) * Cos(x), {x, 6})", //
        "-64*Cos(x)*Sin(x)");
    check("D((-1/2-x+x^2)/(-1/2-x/2+5/2*x^2-5/2*x^3),{x,2})", //
        "2/(-1/2-x/2+5/2*x^2-5/2*x^3)+(2*(-1+2*x)*(1/2-5*x+15/2*x^2))/(1/2+x/2-5/2*x^2+5/\n"
            + "2*x^3)^2+(-1/2-x+x^2)*((-2*(1/2-5*x+15/2*x^2)^2)/(1/2+x/2-5/2*x^2+5/2*x^3)^3+(-5+\n"
            + "15*x)/(1/2+x/2-5/2*x^2+5/2*x^3)^2)");
    check("D(Boole(f(x)),{x,10})", //
        "0");
    check("D(Boole(f(x)),x)", //
        "0");
    check("D(Boole(f(x)),{x,n})", //
        "D(Boole(f(x)),{x,n})");
    // check("D(a*3^x,{x,n})", //
    // "a*E^x");
    // TODO
    check("D(a*Exp(x),{x,n})", //
        "a*E^x");
    check("D(a*f(x),{x,10})", //
        "a*Derivative(10)[f][x]");
    check("D(a*Exp(x),{x,3})", //
        "a*E^x");
    check("D(Tan(x),{x,3})", //
        "2*Sec(x)^4+4*Sec(x)^2*Tan(x)^2");
    check("D(Cot(x),{x,5})", //
        "-16*Cot(x)^4*Csc(x)^2-88*Cot(x)^2*Csc(x)^4-16*Csc(x)^6");

    check("D((2*Sqrt(2)*(x^3))/(x^4),x)", //
        "(-2*Sqrt(2))/x^2");
    check("D(z^n*E^(z),{z,n})", //
        "E^z*n!*Hypergeometric1F1(-n,1,-z)");
    check("D(z^n*E^(-z),{z,n})", //
        "(n!*Hypergeometric1F1(-n,1,z))/E^z");

    check("D(Hypergeometric0F1(a,x), x)", //
        "Hypergeometric0F1(1+a,x)/a");
    check("D(Hypergeometric1F1(a,b,x), x)", //
        "(a*Hypergeometric1F1(1+a,1+b,x))/b");
    check("D(Hypergeometric2F1(a,b,c,f(x)), x)", //
        "(a*b*Hypergeometric2F1(1+a,1+b,1+c,f(x))*f'(x))/c");
    check("D(Hypergeometric2F1Regularized(a,b,c,f(x)), x)", //
        "a*b*Hypergeometric2F1Regularized(1+a,1+b,1+c,f(x))*f'(x)");
    check("D(HypergeometricPFQ({}, {}, x), x)", //
        "E^x");
    check("D(HypergeometricPFQ({a}, {}, x), x)", //
        "a/(1-x)^(1+a)");
    check("D(HypergeometricPFQ({a,b}, {u,v,w}, x), x)", //
        "(a*b*HypergeometricPFQ({1+a,1+b},{1+u,1+v,1+w},x))/(u*v*w)");
    check("D(HypergeometricPFQ({a, b,c,d,e,f}, {u,v,w,y,z}, x), x)", //
        "(a*b*c*d*e*f*HypergeometricPFQ({1+a,1+b,1+c,1+d,1+e,1+f},{1+u,1+v,1+w,1+y,1+z},x))/(u*v*w*y*z)");

    check("D(HarmonicNumber(Sin(Cos(x)), y), x)", //
        "y*Cos(Cos(x))*Sin(x)*(HarmonicNumber(Sin(Cos(x)),1+y)-Zeta(1+y))");
    check("D(Sin(f(x)),{x,3})", //
        "-Cos(f(x))*f'(x)^3-3*Sin(f(x))*f'(x)*f''(x)+Cos(f(x))*Derivative(3)[f][x]");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x), {x, n})", //
        "D(f(x),{x,n})");

    check("D(PolyLog(n,z),z)", //
        "PolyLog(-1+n,z)/z");
    check("D(PolyLog(n,p,z),z)", //
        "PolyLog(-1+n,p,z)/z");

    check("D(Polygamma(x),{x,n})", //
        "PolyGamma(n,x)");

    check("D(Sin(x)==x^2==y*x^4,x )", //
        "Cos(x)==2*x==4*x^3*y");

    // message - D: D called with 0 arguments; 1 or more arguments are expected.
    check("D( )", //
        "D()");

    // github #302 - special case - only 1 argument
    check("D(f)", //
        "f");
    // github #302 test case
    check("Integrate(D(x),x)", //
        "x^2/2");
    // github #302 test case
    check("Integrate(D(y)*D(x),x,y)", //
        "1/4*x^2*y^2");
    // github #302 test case
    check("Integrate(D(x),x)*Integrate(D(y),y)", //
        "1/4*x^2*y^2");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");

    check("D(Surd(x,3),x)", //
        "1/(3*Surd(x,3)^2)");
    check("D(Surd(x,-3),x)", //
        "-1/(3*x*Surd(x,3))");
    check("D(Surd(x,4),x)", //
        "1/(4*Surd(x,4)^3)");
    check("D(Surd(x,-4),x)", //
        "-1/(4*Surd(x,4)^5)");
    check("D(Surd(x,5),x)", //
        "1/(5*Surd(x,5)^4)");
    check("D(Surd(x,-5),x)", //
        "-1/(5*x*Surd(x,5))");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x)+ g(x)+h(x), {x, n})", //
        "D(f(x),{x,n})+D(g(x),{x,n})+D(h(x),{x,n})");
    check("D(Sin(x)+ Cos(y), {x, n})", //
        "Piecewise({{Cos(y),n==0}},0)+Sin(1/2*n*Pi+x)");
    check("D(Sin(x),{x,0.5})", //
        "D(Sin(x),{x,0.5})");
    check("D(Sin(x),{x,f(a)})", //
        "D(Sin(x),{x,f(a)})");
    check("D(Sin(x),{x,f(a)+I})", //
        "D(Sin(x),{x,I+f(a)})");
    check("D(x^a, {x,n})", //
        "x^(a-n)*FactorialPower(a,n)");
    check("D(x^a, {x,3})", //
        "((-2+a)*(-1+a)*a)/x^(3-a)");
    check("D(a^x, {x,2})", //
        "a^x*Log(a)^2");
    check("D(Sin(x),{x,n})", //
        "Sin(1/2*n*Pi+x)");
    check("D(InverseFunction(f)[x],x)", //
        "1/f'(InverseFunction(f)[x])");

    // message - D: 2*x is not a valid variable.
    check("D(2*x, 2*x)", //
        "D(2*x,2*x)");

    check("D(E^f(x),x)", //
        "E^f(x)*f'(x)");

    check("D(AiryAi(Sqrt(x)),x)", //
        "AiryAiPrime(Sqrt(x))/(2*Sqrt(x))");
    check("D(AiryAiPrime(Sqrt(x)),x)", //
        "AiryAi(Sqrt(x))/2");

    check("D(Piecewise({{x^2, x < 0}, {x, x > 0}}),x)", //
        "Piecewise({{2*x,x<0},{1,x>0}},0)");
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x != 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x!=1}},0)");

    // TODO simplify result to 1
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x < 1 || x > 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x<1||x>1}},0)");

    check("D(Factorial(b*x),x)", //
        "b*Gamma(1+b*x)*PolyGamma(0,1+b*x)");
    check("D(E^(E^x + x),x)", //
        "(1+E^x)*E^(E^x+x)");
    check("D((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2),x)", //
        "(-4*e*(Sqrt(d)*Sqrt(-e)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^\n"
            + "2)");
    check("D(ArcTan(x,y),x)", //
        "-y/(x^2+y^2)");
    check("D(ArcTan(x,y),y)", //
        "x/(x^2+y^2)");
    check("D(ArcTan(x,x),x)", //
        "0");
    check("D(Cosh(b*x),x)", //
        "b*Sinh(b*x)");
    check("D(Sinh(x),x)", //
        "Cosh(x)");
    check("D(Sinc(x),x)", //
        "Cos(x)/x-Sin(x)/x^2");
    check("D(SinIntegral(x),x)", //
        "Sinc(x)");
    check("D(SinhIntegral(x),x)", //
        "Sinh(x)/x");
    check("D(CoshIntegral(x),x)", //
        "Cosh(x)/x");
    check("D(Cosh(b*x)*CoshIntegral(b*x),x)", //
        "Cosh(b*x)^2/x+b*CoshIntegral(b*x)*Sinh(b*x)");

    // gradient
    check("D(f(x, y), {{x, y}})", //
        "{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
    // hessian matrix
    check("D(f(x, y), {{x, y}}, {{x, y}})", //
        "{{Derivative(2,0)[f][x,y],Derivative(1,1)[f][x,y]},{Derivative(1,1)[f][x,y],Derivative(\n"
            + "0,2)[f][x,y]}}");
    // generalization of Hessian matrix for n > 2
    check("D(f(x, y, z), {{x, y, z}, 3})", //
        "{{{Derivative(3,0,0)[f][x,y,z],Derivative(2,1,0)[f][x,y,z],Derivative(2,0,1)[f][x,y,z]},{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "1,2,0)[f][x,y,z],Derivative(0,3,0)[f][x,y,z],Derivative(0,2,1)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]},{Derivative(\n"
            + "1,0,2)[f][x,y,z],Derivative(0,1,2)[f][x,y,z],Derivative(0,0,3)[f][x,y,z]}}}");

    check("Refine(D(Abs(x),x), Element(x, Reals))", //
        "x/Abs(x)");

    check("D(HarmonicNumber(x), x)", //
        "Pi^2/6-HarmonicNumber(x,2)");

    check("D(ArcCsc(x),{x,2})", //
        "(-1+2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");
    check("D(ArcSec(x),{x,2})", //
        "(1-2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");

    check("D(x*f(x)*f'(x), x)", //
        "f(x)*f'(x)+x*f'(x)^2+x*f(x)*f''(x)");
    check("D(f(x), x)", //
        "f'(x)");
    check("Sin'(2)", //
        "Cos(2)");
    check("D(Sin(x) + Cos(2*x), {x, 2}) /. x -> 0", //
        "-4");

    check("D(Sin(t), {t, 1})", //
        "Cos(t)");
    check("D(Derivative(0,1,0)[f][x,x*y,z+x^2],x)", //
        "2*x*Derivative(0,1,1)[f][x,x*y,x^2+z]+y*Derivative(0,2,0)[f][x,x*y,x^2+z]+Derivative(\n"
            + "1,1,0)[f][x,x*y,x^2+z]");

    check("D(x^3 + x^2, x)", //
        "2*x+3*x^2");
    check("D(x^3 + x^2, {x, 2})", //
        "2+6*x");
    check("D(Sin(Cos(x)), x)", //
        "-Cos(Cos(x))*Sin(x)");
    check("D(Sin(x), {x, 2})", //
        "-Sin(x)");
    check("D(Cos(t), {t, 2})", //
        "-Cos(t)");
    check("D(y, x)", //
        "0");
    check("D(x, x)", //
        "1");
    check("D(f(x), x)", //
        "f'(x)");

    // chain rule
    check("D(f(2*x+1, 2*y, x+y), x)", //
        "Derivative(0,0,1)[f][1+2*x,2*y,x+y]+2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]");
    check("D(f(x^2, x, 2*y), {x,2}, y) // Expand", //
        "2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(\n"
            + "1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]");

    check("D(x ^ 3 * Cos(y), {{x, y}})", //
        "{3*x^2*Cos(y),-x^3*Sin(y)}");
    check("D(Sin(x) * Cos(y), {{x,y}, 2})", //
        "{{-Cos(y)*Sin(x),-Cos(x)*Sin(y)},{-Cos(x)*Sin(y),-Cos(y)*Sin(x)}}");
    check("D(2/3*Cos(x) - 1/3*x*Cos(x)*Sin(x) ^ 2,x)//Expand  ", //
        "-2/3*Sin(x)-2/3*x*Cos(x)^2*Sin(x)-1/3*Cos(x)*Sin(x)^2+1/3*x*Sin(x)^3");
    check("D(f(#1), {#1,2})", //
        "f''(#1)");
    check("D((#1&)*(t),{t,4})", //
        "0");
    // TODO allow Attributes(f) = {HoldAll}
    check("Attributes(f) = {HoldAll}; Apart(f''(x + x))", //
        "f''(2*x)");
    check("Attributes(f) = {}; Apart(f''(x + x)) ", //
        "f''(2*x)");

    check("D({#^2}, #)", //
        "{2*#1}");

    // Koepf Seite 40-43
    check("D(Sum(k*x^k, {k,0,10}),x)", //
        "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9");
    check("D((x^2+3)*(3*x+2),x)", //
        "2*x*(2+3*x)+3*(3+x^2)");
    check("D(Sin(x^2),x)", //
        "2*x*Cos(x^2)");
    check("D((1+x^2)^Sin(x),x)", //
        "(1+x^2)^Sin(x)*(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))");
    check("D(Exp(x),x)", //
        "E^x");
    check("D((x^2+3)/(3*x+2),x)", //
        "(2*x)/(2+3*x)+(-3*(3+x^2))/(2+3*x)^2");

    // others -----
    check("D(InverseErf(x),x)", //
        "1/2*E^InverseErf(x)^2*Sqrt(Pi)");

    check("f(x_) := x^5 + 6*x^3", //
        "");
    check("D(f(x), x)", //
        "18*x^2+5*x^4");
    check("f'(x)", //
        "18*x^2+5*x^4");
    check("D(f(x), x) /. x->5", //
        "3575");
    check("D(f(x), {x, 3}) /. x -> -1", //
        "96");

    check("D(x^2 * E^(5*y), x)", //
        "2*E^(5*y)*x");
    check("D(x^2 * E^(5*y), y)", //
        "5*E^(5*y)*x^2");
    check("D(x^2 * E^(5*y), {x,2}, {y,3})", //
        "250*E^(5*y)");

    check("D(Sin(g(x)) + g''(x), x)", //
        "Cos(g(x))*g'(x)+Derivative(3)[g][x]");

    check("D(Subscript(x, 1)^2 + Sin(Subscript(x, 1)*Subscript(x, 2)), Subscript(x, 1))", //
        "2*Subscript(x,1)+Cos(Subscript(x,1)*Subscript(x,2))*Subscript(x,2)");

    check("D({3*t^2, 4*t, Sin(t)}, t)", //
        "{6*t,4,Cos(t)}");
    check("D({x^n, {Exp(x), Log(x)}, {Sin(x), Cos(x), Tan(x)}}, x)", //
        "{n/x^(1-n),{E^x,1/x},{Cos(x),-Sin(x),Sec(x)^2}}");
    check("D(x^2 + 5*y^3, {{x, y}})", //
        "{2*x,15*y^2}");
    check("D(x^2 + 5*y^3, {{x, y}, 2})", //
        "{{2,0},{0,30*y}}");
    check("D((x^2+5*y^3+z^4)/E^w,{{x,y}})", //
        "{(2*x)/E^w,(15*y^2)/E^w}");
    check("D(E^(-w)*(x^2 + 5*y^3 + z^4), {{{x, y}, {z, w}}})", //
        "{{(2*x)/E^w,(15*y^2)/E^w},{(4*z^3)/E^w,-(x^2+5*y^3+z^4)/E^w}}");
    check("D(ExpIntegralEi(b*x),x)", //
        "E^(b*x)/x");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");
    check("D(StruveH(x,y),x)", //
        "Derivative(1,0)[StruveH][x,y]");
    check("D(StruveL(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveL(-1+n,x)+StruveL(1+n,x))");
    check("D(StruveL(x,y),x)", //
        "Derivative(1,0)[StruveL][x,y]");

    check("D(JacobiAmplitude(x,y),x)", //
        "JacobiDN(x,y)");
    check("D(JacobiAmplitude(x,y),y)", //
        "((x*(-1+y)+EllipticE(JacobiAmplitude(x,y),y))*JacobiDN(x,y)-y*JacobiCN(x,y)*JacobiSN(x,y))/(\n"
            + //
            "2*(-1+y)*y)");

    check("D(CatalanNumber(x),x)", //
        "CatalanNumber(x)*(Log(4)+PolyGamma(0,1/2+x)-PolyGamma(0,2+x))");
    check("D(Sin(x^5)/x^10,x)", //
        "(5*Cos(x^5))/x^6+(-10*Sin(x^5))/x^11");
    check("D(AppellF1(a,b1,b2,c,f1(x),f2(x)),x)", //
        "(a*b1*AppellF1(1+a,1+b1,b2,1+c,f1(x),f2(x))*f1'(x))/c+(a*b2*AppellF1(1+a,b1,1+b2,\n" //
            + "1+c,f1(x),f2(x))*f2'(x))/c");
  }

  @Test
  public void testDRootSum() {
    // #1^7/(8*#1^7) cancels to 1/8, so the summand is Log(x-#1)/8; its derivative telescopes
    // over the roots of 1+#1^8 to the integrand x^7/(1+x^8).
    check("D(RootSum(1+#1^8&,(Log(x-#1)*#1^7)/(8*#1^7)&),x)", //
        "x^7/(1+x^8)");

    // A definite integral whose antiderivative is a RootSum(...Log(x-#1)...) is evaluated at the
    // bounds by substituting x->1 and x->3 into the summand (Limit of a RootSum), matching
    // -RootSum(...,Log(1-#1)/...&) + RootSum(...,Log(3-#1)/...&).
    check("rs=Integrate(1/(x^5 + 11 x + 1), {x, 1, 3})", //
        "-RootSum(#1^5+11*#1+1&,Log(1-#1)/(5*#1^4+11)&)+RootSum(#1^5+11*#1+1&,Log(3-#1)/(\n"
            + "5*#1^4+11)&)");
    // The result is a constant (free of x), so its derivative is 0.
    check("D(rs,x)", //
        "0");
    check(
        "D(RootSum(#1^6-5*#1^4+5*#1^2+4&,((#1^4-3*#1^2+6)*Log(x-#1))/(6*#1^5-20*#1^3+10*#1)&) ,x)", //
        "(6-3*x^2+x^4)/(4+5*x^2-5*x^4+x^6)");

  }
}
