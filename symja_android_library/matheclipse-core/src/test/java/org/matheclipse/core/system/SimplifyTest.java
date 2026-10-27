package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SimplifyTest extends ExprEvaluatorTestCase {

  @Test
  public void testSimplify0() {
    check("Factor(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
        "(2*x^(3/2))/(Sqrt(x)-ArcTan(Sqrt(x))-x*ArcTan(Sqrt(x)))");
    check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
        "(2*x^(3/2))/(Sqrt(x)-ArcTan(Sqrt(x))-x*ArcTan(Sqrt(x)))");
  }

  @Test
  public void testSpecialFunctionRecurrences() {
    // Simplify leaves recurrences and shifts of special functions alone
    check("Simplify({BesselJ(m - 1, y) + BesselJ(m + 1, y), Gamma(y + 2)/Gamma(y)})", //
        "{BesselJ(-1+m,y)+BesselJ(1+m,y),Gamma(2+y)/Gamma(y)}");
    // three term recurrences rewrite neighbouring orders onto two of them
    check("FullSimplify({BesselJ(m - 1, y) + BesselJ(m + 1, y), BesselJ(-3/4, y) + BesselJ(5/4, y) - 1/(2*y)*BesselJ(1/4, y)})", //
        "{(2*m*BesselJ(m,y))/y,0}");
    check("FullSimplify({(m + 2)*LegendreP(m + 2, y) - (2*m + 3)*y*LegendreP(m + 1, y) + (m + 1)*LegendreP(m, y), HermiteH(m + 2, y) - 2*y*HermiteH(m + 1, y) + 2*(m + 1)*HermiteH(m, y), ChebyshevU(m + 1, y) + ChebyshevU(m - 1, y)})", //
        "{0,0,2*y*ChebyshevU(m,y)}");
    // first order recurrences reduce quotients and sums
    check("FullSimplify({Gamma(y + 3)/Gamma(y + 1), Pochhammer(c, m + 2)/Pochhammer(c, m), Gamma(y + 2) - y*Gamma(y + 1), PolyGamma(0, y + 2) - PolyGamma(0, y)})", //
        "{(1+y)*(2+y),(c+m)*(1+c+m),Gamma(1+y),1/y+1/(1+y)}");
    // orders which don't differ by an integer, or are not neighbours of a recurrence, stay
    check("FullSimplify({BesselJ(1/4, y) + BesselJ(5/4, y), Gamma(y) + Gamma(y + 1/2), BesselJ(m, y) + BesselY(m + 1, y)})", //
        "{BesselJ(1/4,y)+BesselJ(5/4,y),Gamma(1/2+y)+Gamma(y),BesselJ(m,y)+BesselY(1+m,y)}");
  }

  @Test
  public void testSpecialFunctionCombinations() {
    // Casoratians and Wronskians
    check("FullSimplify({BesselJ(m + 1, 3*y)*BesselY(m, 3*y) - BesselJ(m, 3*y)*BesselY(m + 1, 3*y), BesselI(m + 1, y)*BesselK(m, y) + BesselI(m, y)*BesselK(m + 1, y), AiryAi(2*y)*AiryBiPrime(2*y) - AiryAiPrime(2*y)*AiryBi(2*y)})", //
        "{2/(3*Pi*y),1/y,1/Pi}");
    // Kummer's transformation, Legendre's relation, reflections
    check("FullSimplify({Hypergeometric1F1(c, d, 2*y) - E^(2*y)*Hypergeometric1F1(d - c, d, -2*y), EllipticE(2/7)*EllipticK(5/7) + EllipticE(5/7)*EllipticK(2/7) - EllipticK(2/7)*EllipticK(5/7), PolyGamma(0, 1 - y) - PolyGamma(0, y), Erfc(-2*y) - Erfc(2*y)})", //
        "{0,Pi/2,Pi*Cot(Pi*y),2*Erf(2*y)}");
  }

  @Test
  public void testFullSimplify() {
    // TODO
    // check("FullSimplify(a*b*c/Abs(b))", //
    // "a*c*Sign(b)");


    check("Power(2+Sqrt(5), -1/3) // FullSimplify", //
        "1/2*(-1+Sqrt(5))");

    check("FullSimplify(Mod(Mod(a, c) + Mod(b*q, c) + f(x), c))", //
        "Mod(a+b*q+f(x),c)");

    check("FullSimplify(a*Conjugate(a))", //
        "Abs(a)^2");
    // Gamma(x)*Gamma(-x) == -Pi*Csc(Pi*x)/x pays off when the rewrite lets the product
    // collapse; for the bare pair the folded form is larger and Simplify keeps the input
    check("FullSimplify(Gamma(x)*Gamma(-x)*Sin(Pi*x))", //
        "-Pi/x");
    check("FullSimplify(x*Gamma(x)*Gamma(-x))", //
        "-Pi*Csc(Pi*x)");
    check("FullSimplify(Gamma(x)*Gamma(-x)*x*Sin(Pi*x))", //
        "-Pi");
    check("FullSimplify( 3*E^(-x)+7*E^x )", //
        "10*Cosh(x)+4*Sinh(x)");
    check("Simplify( 3*E^(-x)+7*E^x )", //
        "3/E^x+7*E^x");


    // https://github.com/axkr/symja_android_library/issues/856
    check("FullSimplify((4+3*Sqrt(2)+Sqrt(3)+Sqrt(6))/(2+Sqrt(2)+Sqrt(3)))", //
        "1+Sqrt(2)");

    check("FullSimplify((Cosh(x) - 1)/(Cosh(x) + Sinh(x))-(1 - Exp(-x))^2/2)", //
        "0");
    check("FullSimplify(Sign(z)*Abs(z))", //
        "z");
    check("FullSimplify( Sqrt(9-4*Sqrt(5)))", //
        "-2+Sqrt(5)");

    check("FullSimplify(Gamma(z) / Gamma(z-2))", //
        "2-3*z+z^2");
    check("FullSimplify( Sqrt(-9+4*Sqrt(5)))", //
        "I*(-2+Sqrt(5))");


    // MMA factorizes this to (-2 + x)*(-1 + x) ? Although the ComplexityFunction returns 9 for both
    // expressions
    check("2-3*x+x^2 // FullSimplify", //
        "2-3*x+x^2");
    // MMA doesn't factorize this
    check("f(2-3*Sin(x)+Sin(x)^2) // FullSimplify", //
        "f(2-3*Sin(x)+Sin(x)^2)");

    check("FullSimplify(x^2,ComplexInfinity)", //
        "x^2");
    // see Logarithms#test0128() Rubi rule 2447 ==> -(Sqrt(-(e/d))/(2*e))
    check(
        "FullSimplify( (d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2) )", //
        "1/(2*d*Sqrt(-e/d))");
    check(
        "PolynomialQuotientRemainder((d-2*d*Sqrt(-e/d)*x-e*x^2),(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2),x)",
        //
        "{1/(2*d*Sqrt(-e/d)),0}");

    // check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / "
    //
    // + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +
    // (2*(d*Sqrt(-(e/d)) + e*x))/(d +
    // e*x^2)))", //
    // "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)");
    check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
        + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +   (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))", //
        "-Sqrt(-e/d)/(2*e)");

    check(
        "FullSimplify(Numerator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
            + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +   (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
        "1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2)");
    check(
        "FullSimplify(Denominator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / "
            //
            + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
        "(2*d*(d*Sqrt(-e/d)+2*e*x-e*Sqrt(-e/d)*x^2))/(d+e*x^2)");
    check("Together( 1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2) )", //
        "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(d+e*x^2)");
    check("Together( (2*d*(d*Sqrt(-e/d)+2*e*x-e*Sqrt(-e/d)*x^2))/(d+e*x^2) )", //
        "(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)/(d+e*x^2)");

    // #github #152
    check("FullSimplify(Sqrt(-9-4*Sqrt(5)))", //
        "I*(2+Sqrt(5))");
    // check("FullSimplify( Sqrt(9-4*Sqrt(5)))", //
    // "-2+Sqrt(5)");

    check("FullSimplify(Sqrt(9+4*Sqrt(5)))", //
        "2+Sqrt(5)");
    check("FullSimplify(-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5)))", //
        "4");
    check("-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5.0))", //
        "4.0");
    check("FullSimplify(f(x*y,-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5)),Sqrt(-9-4*Sqrt(5))))", //
        "f(x*y,4,I*(2+Sqrt(5)))");

    check("FullSimplify(Sqrt(2) + Sqrt(3) - Sqrt(5 + 2*Sqrt(6)))", //
        "0");

    // check("Factor((d^2+2*d*e*x^2+e^2*x^4))",//
    // "(d+e*x^2)^2");
    // check("Together(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))",//
    // "(-2*d^2*Sqrt(-e/d)-4*d*e*x+2*d*e*Sqrt(-e/d)*x^2)/(d^2+2*d*e*x^2+e^2*x^4)");

    check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
        "108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
    // check("FullSimplify(p)", //
    // "(1+x)^2*(2+x)^2*(3+x)^3");
    check("FullSimplify(Cosh(x)/(b*Cosh(x)+c*Sinh(x)))", //
        "1/(b+c*Tanh(x))");
    check("FullSimplify((b*Cosh(x)+c*Sinh(x))/Cosh(x))", //
        "b+c*Tanh(x)");
    check("Simplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "Cos(n*ArcCos(x))==ChebyshevT(n,x)");
    // FullSimplify uses FunctionExpand and can test the equation:
    check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "True");
    check("FullSimplify(Cosh(x)+Sinh(x))", //
        "E^x");
  }

  @Test
  public void testFullSimplifySqrtBranchCutSign() {
    // with s = Sqrt(-e/d) (so e == -d*s^2) the result reduces to -2*s/(1+s*x)^2, which is the
    // derivative. Together(...) of the derivative and this form agree numerically, e.g. both
    // are -1.58678-0.809749*I at {d->2.0, e->3.0, x->0.5}.
    check("FullSimplify(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))", //
        "(2*e)/(d*Sqrt(-e/d)-2*e*x-e*Sqrt(-e/d)*x^2)");

    // Sqrt(1/z) != 1/Sqrt(z) in general, so Together must not invert the base of a
    // non-integer power
    check("Together(1/Sqrt(-e/d))", //
        "1/Sqrt(-e/d)");
    check("Together(1/Sqrt(1+1/x))", //
        "1/Sqrt((1+x)/x)");
  }

  @Test
  public void testFullSimplifyIssue856() {
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) / (Sqrt(2) - 4) )", //
        "-Sqrt(3/2)");

    // github issue #856
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (Sqrt(2) + 4))", //
        "7*Sqrt(6)");
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (-Sqrt(2) - 4) )", //
        "-7*Sqrt(6)");

  }

  @Test
  public void testSimplify() {
    // E^(y + I*Pi*k) for an integer k: (-1)^k*E^y, and E^y for an even multiple
    check("Simplify(E^(6*I*k*Pi + w), Element(k, Integers))", //
        "E^w");
    check("Simplify(E^(3*I*k*Pi + w), Element(k, Integers))", //
        "(-1)^k*E^w");
    check("Simplify(E^(6*I*k*Pi + w), Element(k, Reals))", //
        "E^(I*6*k*Pi+w)");
    check("Simplify(1/(Sqrt(2 - x^2)*Sqrt(1 + x^2)))", //
        "1/Sqrt(2+x^2-x^4)");
    check("Simplify((Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))^2)", //
        "d*x^2");
    check("Simplify(2*Cos(x)*Sin(x))", //
        "Sin(2*x)");

    // check("Reduce(p==5&&p==6)", //
    // "False");
    // check("Simplify(p==5&&p==6)", //
    // "");

    check("Simplify((1/x-1/3)/(x-3))", //
        "-1/(3*x)");

    check("s=2*a + 2*Sqrt(a - Sqrt(-b))*Sqrt(a + Sqrt(-b));", //
        "");
    check("Simplify(s, a>0&&b>0)", //
        "2*(a+Sqrt(a^2+b))");

    check("Simplify(a-a*b+a*c)", //
        "a*(1-b+c)");

    // issue #930
    check("Simplify(Sin(Pi*Cosh(45522*Csc(17/36*Pi))))", //
        "Sin(Pi*Cosh(45522*Sec(Pi/36)))");

    check("Simplify((1/x-1/3)/(x-3))", //
        "-1/(3*x)");

    check("Simplify(Abs(Sign(z)),z!=-1&& z!=0&&z!=1)", //
        "1");
    check("Simplify(Sign(x), x<0)", //
        "-1");

    check("Simplify(r0/.{r0->r})", //
        "r");
    check("Simplify(Log(E^n))", //
        "Log(E^n)");
    check("Simplify(Log(E^n),n>0)", //
        "n");

    check("Simplify(0^x, x==0)", //
        "Indeterminate");
    check("Simplify(0^x, x>0)", //
        "0");
    check("Simplify(0^x, x<0)", //
        "ComplexInfinity");

    check(" Simplify(y -> 1+Cot(x)^2)", //
        "y->Csc(x)^2");
    // TODO ???
    // check("Simplify(Sqrt(1+a*x)/Sqrt(1-a^2*x^2) )", //
    // "1/Sqrt(1-a*x)");
    check("Together((-574-2*Sqrt(21))/(-476))", //
        "1/238*(287+Sqrt(21))");

    check("Simplify(Abs(x)^2,Element(x,Reals))", //
        "x^2");
    check("Simplify(Abs(x)^3,Element(x,Reals))", //
        "Abs(x)^3");

    check("Simplify(1+Cot(x)^2)", //
        "Csc(x)^2");
    check("Simplify(1+Tan(x)^2)", //
        "Sec(x)^2");
    check("Simplify(E^(a1*a2*Log(f)+b+Log(g)))", //
        "E^b*f^(a1*a2)*g");
    check("Simplify(E^(a1*a2*Log(f)*Log(h)+b+Log(g)))", //
        "E^(b+a1*a2*Log(f)*Log(h))*g");
    check("Simplify(E^(a1*a2*Log(f)+Log(g)))", //
        "f^(a1*a2)*g");
    check("Simplify((-a+c)*(-a+x))", //
        "(a-c)*(a-x)");
    check("Simplify(Log(a*(a-b)*(a-c)-a^2*x+a*b*x+a*c*x-b*c*x))", //
        "Log((a-b)*(a-c)*(a-x))");
    check("Simplify((2/3-a)^(-5/4)*(2/3+a)^(-5/4))", //
        "1/(4/9-a^2)^(5/4)");
    check("Simplify((2-a)^(c)* (2+a)^(c))", //
        "(4-a^2)^c");
    // FullSimplify tries more steps (even for the same expressions as in Simplify)
    check("FullSimplify((2/3-a)^(-5/4)*(2/3+a)^(-5/4))", //
        "1/(4/9-a^2)^(5/4)");
    check("FullSimplify((2-a)^(c)* (2+a)^(c))", //
        "(4-a^2)^c");

    // https://github.com/axkr/symja_android_library/issues/142
    check("Simplify({{x+y+x*y==9},{x*y*(x+y)==20}})", //
        "{{x+y+x*y==9},{x*y*(x+y)==20}}");
    check("Simplify(-3+2*x+x^2==0)", //
        "x*(2+x)==3");
    check("Simplify({Im(Exp(I*Pi/5)* x), Im(2*x + I)}, x > 3)", //
        "{Im(E^(I*1/5*Pi)*x),1}");

    // check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
    // "(-2*x^(3/2))/(-Sqrt(x) + (1 + x)*ArcTan(Sqrt(x)))");
    // check("Simplify((-1/(Sqrt(x)*(1+x))+(-1-x)/(2*x^(3/2)*(1+x)))/(1+x))", //
    // "(-1 - 3*x)/(2*x^(3/2)*(1 + x)^2)");
    // check("Simplify(1/(ArcTan(Sqrt(x))/(-1/(Sqrt(x)*(1+x)^2)-1/(2*x^(3/2)*(1+x))))", //
    // "(-2*x^(3/2)*(1 + x)^2*ArcTan(Sqrt(x)))/(1 + 3*x)");
    //
    // check("Simplify((1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))/((d+e*x^2)*((-4*e*(d*Sqrt(-e/d)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))))",
    // //
    // "(-d+2*d*Sqrt(-e/d)*x+e*x^2)/(2*d*(-d*Sqrt(-e/d)-2*e*x+e*Sqrt(-e/d)*x^2))");//"-Sqrt(-(e/d))/(2*e)");
    check(
        "Simplify((x*(-Sqrt(-4+x^2)+x^2*Sqrt(-4+x^2)-4*Sqrt(-1+x^2)+x^2*Sqrt(-1+x^2)))/((4-5*x^2+x^4)*(x/Sqrt(-4+x^2)+x/Sqrt(-1+x^2))))", //
        "1");
    // TODO the reference picks -I*Cos(2*x)-Sin(2*x); both have LeafCount 15, and since Factor takes
    // out a Gaussian content it finds the square of the numerator first
    check("Simplify((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
        "I*(I*Cos(x)+Sin(x))^2");

    check("Expand((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
        "a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x)");
    check("Factor(TrigToExp( a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x) ))", //
        "((1/2+I*1/2)*(-I*a+b)*(I+E^(2*x)))/E^x");
    check("Simplify((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
        "(a+I*b)*(Cosh(x)-I*Sinh(x))");
    check("Simplify(Element(x, Reals), x>0)", //
        "True");
    check("Simplify(Sin(n*Pi), Element(n, Integers))", //
        "0");
    check("Simplify(Tan(x + Pi*n), Element(n, Integers))", //
        "Tan(x)");
    check(
        "Simplify(a/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))+(b*Sinh(x))/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b))))", //
        "(a+b*Sinh(x))/(a-I*b)");
    check("Simplify((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))", //
        "a-I*b");
    check("Simplify(-2*Log(2))", //
        "-Log(4)");
    check("Simplify(Log(6)-Log(2))", //
        "Log(3)");
    check("Simplify(a+Log(1/6)+Log(1/7)+z())", //
        "a-Log(42)+z()");
    check("Simplify(a+Log(1/6)+Log(1/7)+Log(3/4)+z())", //
        "a-Log(56)+z()");
    check("Simplify(a+Log(1/6)-2*Log(1/7)+7*Log(3/4)+z())", //
        "a+Log(35721/32768)+z()");
    check("Simplify(1+n/2)", //
        "1/2*(2+n)");
    check("Simplify((9-Sqrt(57))*x^2)", //
        "(9-Sqrt(57))*x^2");
    check("Simplify(-a/(-b+a*c))", //
        "a/(b-a*c)");
    check("Simplify(1/(Cos(x)+I*Sin(x))-(c+d*x)^n)", //
        "-(c+d*x)^n+Cos(x)-I*Sin(x)");
    check("Simplify(1/(Cos(x)+I*Sin(x)))", //
        "Cos(x)-I*Sin(x)");
    check("Expand((Sqrt(-d)*e+d*Sqrt(e)*Sqrt(-e/d))*(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
        "0");
    check("Simplify((e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
        "(e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d))");
    check("Simplify((-b^12*x^456)/x^12+(x^12*(a+b*x^37)^12)/x^12)", //
        "-b^12*x^444+(a+b*x^37)^12");
    check("Simplify(-Cos(x) +Sin(x)^2/(1-Cos(x)))", //
        "1");
    check("Simplify(-Cos(x)/(1-Cos(x))+Sin(x)^2/(1-Cos(x))^2-1/(1-Cos(x)))", //
        "0");

    check("Simplify(x^(5/2) - Sqrt(x^5), x>=0)", //
        "0");
    check("Simplify(-136+40*Sqrt(17))", //
        "8*(-17+5*Sqrt(17))");
    check("Simplify(Sqrt(17)/(5+Sqrt(17)))", //
        "1/8*(-17+5*Sqrt(17))");

    // check("Simplify(Cos(b*x)/(-Cos(b*x)/x^2-CosIntegral(b*x)/x^2))", //
    // "(x^2*Cos(b*x))/(-Cos(b*x)-CosIntegral(b*x))");
    check("Together(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
        "(-Cos(b*x)-b*x*Sin(b*x))/x^2");
    check("Simplify(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
        "(-Cos(b*x)-b*x*Sin(b*x))/x^2");

    check("Simplify(-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12)", //
        "-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12");
    check("Simplify(-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2)))", //
        "-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2))");
    check("Simplify(1 + 1/GoldenRatio - GoldenRatio)", //
        "0");
    // check("Simplify(-15-6*x)/(3*(1+x+x^2))", "");
    check("Simplify(Abs(x), x<0)", //
        "Abs(x)");
    check("complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)", //
        "");
    check("Simplify(Abs(x), x<0, ComplexityFunction->complexity)", //
        "-x");

    check("Simplify(100*Log(2))", //
        "100*Log(2)");
    check("Simplify(2*Sin(x)^2 + 2*Cos(x)^2)", //
        "2");
    check("Simplify(f(x))", //
        "f(x)");
    check("Simplify(a*x^2+b*x^2)", //
        "(a+b)*x^2");

    check("Simplify(5*x*(6*x+30))", //
        "30*x*(5+x)");
    check("Simplify(Sqrt(x^2), Assumptions -> x>0)", //
        "x");
    check("Simplify(Sqrt(x^2), x>0)", //
        "x");
    check("Together(2/(1/Tan(x) + Tan(x)))", //
        "2/(Cot(x)+Tan(x))");
    check("Together(2*Tan(x)/(1 + Tan(x)^2))", //
        "(2*Tan(x))/(1+Tan(x)^2)");
    check("Simplify(Sin(x)^2 + Cos(x)^2)", //
        "1");
    check("Simplify((x - 1)*(x + 1)*(x^2 + 1) + 1)", //
        "x^4");
    check("Simplify(3/(x + 3) + x/(x + 3))", //
        "1");

    check("Simplify(2*Tan(x)/(1 + Tan(x)^2))", //
        "Sin(2*x)");
    check(
        "Simplify((1/3+(1/3)*(-2)^(-1/3)*2^(-2/3)*(1+(0+1*I)*3^(1/2))+(1/6)*(-1)^(1/3)*(1+(0+-1*I)*3^(1/2)))^2)", //
        "1");
    check(
        "Simplify((1/3+(1/3)*(-2)^(-1/3)*2^(-2/3)*(1+(0+-1*I)*3^(1/2))+(1/6)*(-1)^(1/3)*(1+(0+1*I)*3^(1/2)))^2)", //
        "0");

    check("$Assumptions=(x>0);Simplify(0^x)", //
        "0");
  }

  @Test
  public void testSimplifySqrt() {
    // the former expectation (1+1/x)*Sqrt(-1+2/(1+x)) crossed the Sqrt branch cut: it is
    // sign-flipped wherever (x+1)/(1-x) is a negative real, e.g. at x->2.5 the input is
    // -0.916515*I but that form gives +0.916515*I
    check("Simplify( 2/(Sqrt((x+1)/(1-x))-1/Sqrt((x+1)/(1-x))))", //
        "2/(-1/Sqrt(-1-2/(-1+x))+Sqrt(-1-2/(-1+x)))");
    check("FullSimplify((x/Sqrt(x^2+1) + 1)/((Sqrt(x^2+1)+x)^2 + 1))", //
        "1/(2*(1+x^2))");
    check("Simplify((x*(1/Sqrt(x^2+6) - (Sqrt(x^2+6) - Sqrt(6))/x^2))/(Sqrt(x^2+6) - Sqrt(6)))", //
        "Sqrt(6)/(x*Sqrt(6+x^2))");

  }

  @Test
  public void testSimplifyBoolean() {
    check("Simplify((x+1)&&(x+1))", //
        "1+x");
    check("Simplify(b&&b)", //
        "b");
    check("Simplify(a && b && !b )", //
        "False");
    check("Simplify((a || b) && (a || c) )", //
        "a||(b&&c)");
    check("Simplify(a || ! a && b)", //
        "a||b");
    check("Simplify( a || b || ! a || ! b)", //
        "True");
    check("Simplify(a || (a && Infinity))", //
        "a");
    check("Simplify((b || a) && (c || a))", //
        "a||(b&&c)");
    check("Simplify((a || b || c || Not((a && b && c))))", //
        "True");
    check("Simplify((a || b || c || Not((a && b && c && d))))", //
        "True");
    check("Simplify( (a || b || c || d || Not((a && b && c))))", //
        "True");
    check("Simplify((a || b || c || d || Not(a)))", //
        "True");
    check("Simplify((az || b || cz || Not((ay && b && cy && dy))))", //
        "True");
    check("Simplify( a || (a && b && c && d) )", //
        "a");
    check("Simplify(d || (a && b && c && d))", //
        "d");
    check("Simplify(d || e || (a && b && c && d) )", //
        "d||e");
    check("Simplify(d || b || (a && b && c && d))", //
        "b||d");
    check("Simplify(d || b || (a && b && c && d) || ! b)", //
        "True");
    check("Simplify(foo(d || b || (a && b && c && d) || ! b))", //
        "foo(True)");
    check("Simplify(d || e || (a && b && c))", //
        "d||e||(a&&b&&c)");
    check("Simplify(z || z)", //
        "z");
    check("Simplify(z || a || z)", //
        "a||z");
    check("Simplify(a || a && b )", //
        "a");
    check("Simplify(a || !a && b )", //
        "a||b");
    check("Simplify(a || c || ! a && b )", //
        "a||b||c");
    check("Simplify( a || c || ! a && ! c && b )", //
        "a||b||c");
    check("Simplify(a || c || ! a && ! c && ! b)", //
        "a||!b||c");
    check("Simplify(a || c || ! a && ! c && ! b && d )", //
        "a||c||(!b&&d)");
    check("Simplify( c || a || Not(b))", //
        "c||a||!b");
    check("Simplify(And(x1, a, x2, Not(Or(x3, a, x4)), x5))", //
        "False");
    check("Simplify(And(x1, a, x2, Or(x3, a, x4), x5))", //
        "a&&x1&&x2&&x5");
    check("Simplify(a&&b&&a)", //
        "a&&b");
  }

  @Test
  public void testSimplifyNonRecursive() {
    check("Log(Cos(3)*Sec(4))", //
        "Log(Cos(3)*Sec(4))");
    check("Simplify(Log(-Cos(3))-Log(-Cos(4)))", //
        "Log(Cos(3)*Sec(4))");
  }

  @Test
  public void testSimplifyPreferences() {
    check("Simplify(Sqrt(2)^3)", //
        "2*Sqrt(2)");
    check("Simplify(2*Sqrt(2))", //
        "2*Sqrt(2)");
    check("Simplify(6+2*a)", //
        "2*(3+a)");
    check("Simplify(2*(3+a))", //
        "2*(3+a)");
  }

  /**
   * Tests the integration of the Fu trigonometric simplification algorithm within the general
   * Simplify and FullSimplify evaluation loop.
   */
  @Test
  public void testSimplifyTrigFuIntegration() {
    check("Simplify((x^2 - 1)/(x - 1) + Cos(y)^2 + Sin(y)^2)", //
        "2+x");

    // More complex Fu algorithm specific reductions (CTR1 example)
    check("Simplify(Sin(x)^4 - Cos(y)^2 + Sin(y)^2 + 2*Cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    // Angle addition formulas via Simplify
    check("Simplify(Sin(a)*Cos(b) + Cos(a)*Sin(b))", //
        "Sin(a+b)");

    // Double angle formulas
    check("Simplify(Cos(x)^2 - Sin(x)^2)", //
        "Cos(2*x)");

    // FullSimplify integration check
    // Ensures the FullSimplify class properly inherits the trig pass
    check("FullSimplify(Sin(x)^2 + Cos(x)^2 + Tan(y)*Cos(y))", //
        "1+Sin(y)");
  }

  // @Test
  // public void testSimplifyTrig() {
  // TODO
  // check(
  // "Simplify(-(Sech(x/2)^2/(2*(1 + Tanh(x/2))^2)) + Sech(x/2)^2/(2*(1 - Tanh(x/2)^2)) +
  // (Sech(x/2)^2 + Sech(x/2)^2*Tanh(x/2))/(1 + 2*Tanh(x/2) + Tanh(x/2)^2)^2)", //
  // "Cosh(x)*(Cosh(x)-Sinh(x))");
  // }

  @Test
  public void testSimplifyIssue1422() {
    check("Simplify((1*Cos(120*Degree)+1*Sin(120*Degree)*I)^3)", //
        "1");
  }

  @Test
  public void testSimplifyIssue1425() {
    // #1425 follow up problem: this used to run forever, because functionExpand() ran the whole
    // tryTransformations() pipeline on a grown FunctionExpand() result. Both sides are 0.908321.
    check("Simplify(2*Cos(1/180*Pi*(60+3*Tan(1/180*Pi*(45-2*Sin(Pi/60))))))", //
        "2*Cos(1/60*Pi*(20+Tan(Pi*(1/4-Sin(Pi/60)/90))))");

    // issue #1425
    check("FullSimplify(Sqrt(3-Sqrt(5))*(3+Sqrt(5))*(-Sqrt(2)+Sqrt(10)))", //
        "8");

  }

  @Test
  public void testSimplifyIssue1439() {
    // Sqrt(3+Sqrt(13+4*Sqrt(3))) reduces to Sqrt(2*(2+Sqrt(3))), whose base is a Times, and
    // Power#functionExpand() only offered a Plus base to sqrtDenest(). Expanding the base first
    // gets the sum back and denests it: 13 leaves down to 7.
    check("FullSimplify(Sqrt(3+Sqrt(13+4*Sqrt(3))))", //
        "1+Sqrt(3)");
    // the same denesting once the outer square root is already over a sum, and over a product
    check("FullSimplify(Sqrt(4+2*Sqrt(3)))", //
        "1+Sqrt(3)");
    check("FullSimplify(Sqrt(2*(2+Sqrt(3))))", //
        "1+Sqrt(3)");
  }

  /**
   * Sums of the shape <code>c + d/x</code> where Symja's result differs from WMA's but is
   * <b>cheaper</b> under the {@code ComplexityFunction}, so it is the better answer by our own
   * acceptance rule. The weights below are {@code leafCountSimplify()}.
   */
  @Test
  public void testSimplifyReciprocalSums() {
    // -4-2/x (10 leaves); ours weighs 9
    check("Simplify(-4 - 2/x)", //
        "-2*(2+1/x)");
    check("Simplify(-4 - 2/x^2)", //
        "-2*(2+1/x^2)");
    // (-2*(1+x))/x (10 leaves); ours weighs 9
    check("Simplify(-2 - 2/x)", //
        "-2*(1+1/x)");
    // (-2*(1+x+x^2))/x (13 leaves); ours weighs 10.
    check("Simplify(-2 - 2/x - 2*x)", //
        "-2*(1+1/x+x)");
  }
  /**
   * A sum of fractions which is too large for the search of Simplify but identically 0: it is
   * found to be 0 at sample points first and then by its common denominator.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testSimplifyLargeZeroSum() {
    // the derivative of an antiderivative minus the integrand, with a radical
    check("Simplify(D(Integrate((t^2+2)/(t^3*Sqrt(3*t^4-2*t^2+1)), t), t) - (t^2+2)/(t^3*Sqrt(3*t^4-2*t^2+1)))", //
        "0");
    // and with partial fractions over symbolic coefficients
    check("Simplify(D(Integrate(1/(t^2*(p+q*t)^5), t), t) - 1/(t^2*(p+q*t)^5))", //
        "0");
    // a sum which is small at the sample points but not 0 stays a sum
    check("FreeQ(Simplify(D(Integrate(1/(t^2*(p+q*t)^5), t), t) - 1/(t^2*(p+q*t)^5) + 1/10^12), 1/10^12)", //
        "False");
    // constant radicals
    check("Simplify(Sqrt(3)/Sqrt(2+t^2) - Sqrt(3)*Sqrt(2+t^2)/(2+t^2))", //
        "0");
    check("Simplify(Sqrt(10)*Sqrt(t^2+3) - Sqrt(2)*Sqrt(5)*Sqrt(t^2+3))", //
        "0");
  }

  /**
   * A long sum of fractions with different denominators plus a number: the non-numeric summands are
   * too large to be factored in the search for a hidden perfect power (issue #1543).
   */
  @Test
  public void testSimplifyConstantPlusLongSum() {
    // 49 fractions and a constant
    check("With({v1 = 1/2 + Sum(4^(x/2020)/(4^(x/2020)+2), {x, 950, 998})}, " //
        + "With({v2 = FullSimplify(v1)}, " //
        + "{LeafCount(v1), LeafCount(v2) <= LeafCount(v1), Chop(N(v1 - v2))}))", //
        "{739,True,0}");
    // the small case is still found
    check("Simplify(1+c^2+2*c*d+d^2)", //
        "1+(c+d)^2");
  }
  /**
   * Sums which are identically 0 and which the search does not reduce; each is proved exactly.
   * The twin of each case, with one number changed or the region moved, must stay as it is.
   */
  @Test
  public void testZeroIdentities() {
    // inverse tangents of rational numbers: a product of Gaussian rational numbers
    check("FullSimplify(2*ArcTan(1/3) - ArcTan(3/4))", //
        "0");
    check("FullSimplify(2*ArcTan(1/3) - ArcTan(4/3)) === 0", //
        "False");
    check("FullSimplify(ArcTan(1/2) + ArcTan(1/3) - Pi/4)", //
        "0");
    check("FullSimplify(ArcTan(1/2) + ArcTan(1/3) + Pi/4) === 0", //
        "False");
    check("FullSimplify(ArcSin(4/5) + ArcSin(5/13) - ArcSin(63/65))", //
        "0");
    check("FullSimplify(ArcSin(4/5) + ArcSin(5/13) - ArcSin(62/65)) === 0", //
        "False");
    // plain Simplify leaves them
    check("Simplify(2*ArcTan(1/3) - ArcTan(3/4))", //
        "2*ArcTan(1/3)-ArcTan(3/4)");
    // rational multiples of Pi: a root of unity and its cyclotomic polynomial
    check("FullSimplify(Sin(Pi/18)*Sin(5*Pi/18)*Sin(7*Pi/18) - 1/8)", //
        "0");
    check("FullSimplify(Sin(Pi/18)*Sin(5*Pi/18)*Sin(7*Pi/18) - 1/7) === 0", //
        "False");
    check("FullSimplify(Tan(Pi/9)*Tan(2*Pi/9)*Tan(4*Pi/9) - Sqrt(3))", //
        "0");
    check("FullSimplify(Tan(Pi/9)*Tan(2*Pi/9)*Tan(4*Pi/9) + Sqrt(3)) === 0", //
        "False");
    // two terms with square roots under an assumption
    check("Simplify(Sqrt(t-2)*Sqrt(t+2) - Sqrt(t^2-4), t > 2)", //
        "0");
    check("Simplify(Sqrt(t-2)*Sqrt(t+2) - Sqrt(t^2-4), t < -2) === 0", //
        "False");
    check("Simplify(Sqrt(t-2)*Sqrt(t+2) - Sqrt(t^2-5), t > 3) === 0", //
        "False");
    check("Simplify(Sinh(ArcCosh(t)) - Sqrt(t^2-1), t > 1)", //
        "0");
    check("Simplify(Sinh(ArcCosh(t)) - Sqrt(t^2-1)) === 0", //
        "False");
    // ArcTanh of arguments in (-1,1)
    check("FullSimplify(ArcTanh(u) - ArcTanh(v) - ArcTanh((u-v)/(1-u*v)), -1<u<1 && -1<v<1)", //
        "0");
    check("FullSimplify(ArcTanh(u) - ArcTanh(v) - ArcTanh((u-v)/(1-u*v)), u>3 && v>3) === 0", //
        "False");
    check("FullSimplify(ArcTanh(u) - ArcTanh(v) - ArcTanh((u-v)/(1+u*v)), -1<u<1 && -1<v<1) === 0", //
        "False");
    // a constant phase in a hyperbolic function
    check("Simplify(Tanh(t) + Tanh(t + I*Pi/3) + Tanh(t + 2*I*Pi/3) - 3*Tanh(3*t))", //
        "0");
    check("Simplify(Tanh(t) + Tanh(t + I*Pi/3) + Tanh(t + 2*I*Pi/3) - 4*Tanh(3*t)) === 0", //
        "False");
    // the inverse of a function on the part where it is one-to-one
    check("Simplify(ArcCosh(Cosh(t)) - t, t >= 0)", //
        "0");
    check("Simplify(ArcCosh(Cosh(t)) - t)", //
        "-t+ArcCosh(Cosh(t))");
    check("Simplify(ArcSinh(Sinh(t)) - t, Element(t, Reals))", //
        "0");
  }
  /**
   * PossibleZeroQ: terms too large for the machine precision to cancel, and identities with a
   * constant phase, which were reported as not zero.
   */
  @Test
  public void testPossibleZeroQHugeTermsAndPhases() {
    check("PossibleZeroQ(Cosh(12)^24 - (1 - Tanh(12)^2)^(-12))", //
        "True");
    check("PossibleZeroQ(Cosh(12)^24 - (1 - Tanh(12)^2)^(-12) + 1)", //
        "False");
    check("PossibleZeroQ(Cosh(12)^24 + (1 - Tanh(12)^2)^(-12))", //
        "False");
    check("PossibleZeroQ(Sin(t + Pi/6) - Sqrt(3)*Sin(t)/2 - Cos(t)/2)", //
        "True");
    check("PossibleZeroQ(Sin(t + Pi/6) - Sqrt(3)*Sin(t)/2 - Cos(t)/3)", //
        "False");
    check("PossibleZeroQ(Tan(t) + Tan(t + Pi/3) + Tan(t + 2*Pi/3) - 3*Tan(3*t))", //
        "True");
    check("PossibleZeroQ(Tan(t + Pi/3) - Tan(t))", //
        "False");
    check("PossibleZeroQ(Sin(2*t) - 2*Sin(t)*Cos(t) + 10^-12)", //
        "False");
  }
  /**
   * PossibleZeroQ with AppellF1 of symbolic arguments: the numeric test was switched off for it,
   * so every identity was reported as not zero. The sample points are chosen where the series
   * arguments are inside the unit circle.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testPossibleZeroQAppellF1() {
    // the symmetry in the two pairs of arguments
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, u, v) - AppellF1(2/3, 1/2, 1/5, 7/4, v, u))", //
        "True");
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, u, v) - AppellF1(2/3, 1/5, 1/2, 7/4, v, u))", //
        "False");
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, u, v) - AppellF1(2/3, 1/2, 1/5, 7/4, v, u) + 1/10^6)", //
        "False");
    check("PossibleZeroQ(AppellF1(p, q1, q2, r, u, v) - AppellF1(p, q2, q1, r, v, u))", //
        "True");
    // one parameter 0: a Hypergeometric2F1
    check("PossibleZeroQ(AppellF1(p, q, 0, r, u, v) - Hypergeometric2F1(p, q, r, u))", //
        "True");
    check("PossibleZeroQ(AppellF1(p, q, 0, r, u, v) - Hypergeometric2F1(p, q, r, v))", //
        "False");
    // arguments which are expressions in the variables
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, u^2, 1-u) - AppellF1(2/3, 1/2, 1/5, 7/4, 1-u, u^2))", //
        "True");
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, u^2, 1-u) - AppellF1(2/3, 1/2, 1/5, 7/4, 1-u, u^3))", //
        "False");
    // numbers, as before
    check("PossibleZeroQ(AppellF1(2/3, 1/5, 1/2, 7/4, 1/3, 1/6) - AppellF1(2/3, 1/2, 1/5, 7/4, 1/6, 1/3))", //
        "True");
  }

  /**
   * A root of an exponential is merged only if the imaginary part of the exponent is in
   * <code>(-Pi, Pi]</code>; inverse functions of their direct functions, conjugates, half angles
   * and roots of squares under assumptions.
   */
  @Test
  public void testAssumedRootsAndInverseFunctions() {
    // (E^a)^b is not E^(a*b) in general
    check("ExpToTrig(Sqrt(Exp(4*I*t)))", //
        "Sqrt(Cos(4*t)+I*Sin(4*t))");
    check("ExpToTrig((E^t)^s)", //
        "(Cosh(t)+Sinh(t))^s");
    check("ExpToTrig(E^(3*t)^2)", //
        "Cosh(9*t^2)+Sinh(9*t^2)");
    // at t = 2 the difference is not 0
    check("Simplify(Sqrt(Exp(4*I*t))-Exp(2*I*t))", //
        "-E^(I*2*t)+Sqrt(E^(I*4*t))");
    check("Simplify(Sqrt(Exp(4*I*t))-Exp(2*I*t), -Pi/4<t<Pi/4)", //
        "0");
    check("Simplify(Sqrt(Exp(4*I*t))-Exp(2*I*t), Pi/2<t<Pi)", //
        "-E^(I*2*t)+Sqrt(E^(I*4*t))");
    check("N(Sqrt(Exp(4*I*t))-Exp(2*I*t) /. t->2)", //
        "1.30729+I*1.5136");
    check("Simplify(Sqrt(Exp(6*t))-Exp(3*t))", //
        "-E^(3*t)+Sqrt(E^(6*t))");
    check("Simplify(Sqrt(Exp(6*t))-Exp(3*t), Element(t,Reals))", //
        "0");
    check("Simplify((Exp(5*I*t))^(1/5)-Exp(I*t))", //
        "-E^(I*t)+(E^(I*5*t))^(1/5)");
    // no message for the pole of Tan inside the interval
    check("Simplify(Abs(Tan(t))+Tan(t), Pi/2<t<Pi)", //
        "0");
    // the principal range only
    check("Simplify(ArcSin(Sin(t)), -Pi/2<=t<=Pi/2)", //
        "t");
    check("Simplify(ArcSin(Sin(t)), -1<t<1/3)", //
        "t");
    check("Simplify(ArcCos(Cos(t)), 0<=t<=Pi)", //
        "t");
    check("Simplify(ArcCos(Cos(t)), 1/2<t<3)", //
        "t");
    check("Simplify(ArcSin(Sin(t)), 0<t<Pi)", //
        "ArcSin(Sin(t))");
    check("Simplify(ArcCos(Cos(t)), -1<t<1)", //
        "ArcCos(Cos(t))");
    check("Simplify(ArcCos(Cos(t)), Pi<t<3*Pi/2)", //
        "ArcCos(Cos(t))");
    check("Simplify(ArcSin(Sin(t)))", //
        "ArcSin(Sin(t))");
    // functions which are real on the real axis and have no branch cut
    check("Simplify(Conjugate(Cot(u+I*v))-Cot(u-I*v), Element(u,Reals)&&Element(v,Reals))", //
        "0");
    check("Simplify(Conjugate(Sech(2*u+3*I*v)), Element(u,Reals)&&Element(v,Reals))", //
        "Sech(2*u-I*3*v)");
    check("Simplify(Conjugate(Cos(u)), Element(u,Reals))", //
        "Cos(u)");
    check("Simplify(Conjugate(Cot(u+I*v))-Cot(u-I*v))", //
        "Conjugate(Cot(u+I*v))-Cot(u-I*v)");
    check("Simplify(Conjugate(Log(u+I*v)), Element(u,Reals)&&Element(v,Reals))", //
        "Conjugate(Log(u+I*v))");
    check("Conjugate(Cos(3-5*I))", //
        "Cos(3+I*5)");
    // both are E^(Log(s)*Log(t))
    check("Simplify(s^Log(t)-t^Log(s))", //
        "0");
    check("Simplify(s^(2*Log(t))-t^(2*Log(s)))", //
        "0");
    check("Simplify(s^Log(t)-3*t^Log(s))", //
        "s^Log(t)-3*t^Log(s)");
    check("Simplify(s^Log(t)-t^Log(r))", //
        "s^Log(t)-t^Log(r)");
    // half angles under a root
    check("Simplify(Sqrt((1-Cos(t))/2)-Sin(t/2), 0<t<Pi)", //
        "0");
    check("Simplify(Sqrt((1+Cos(t))/2)-Cos(t/2), 0<t<Pi/2)", //
        "0");
    check("Simplify(Sqrt((1-Cos(t))/2)-Sin(t/2), 2*Pi<t<3*Pi)", //
        "-2*Sin(t/2)");
    check("Simplify(Sqrt(3+3*Cos(4*t)), Element(t,Reals))", //
        "Sqrt(6)*Abs(Cos(2*t))");
    check("Simplify(Sqrt(5*Sin(t)^2), Element(t,Reals))", //
        "Sqrt(5)*Abs(Sin(t))");
    check("Simplify(Sqrt(7*t^2), Element(t,Reals))", //
        "Sqrt(7)*Abs(t)");
    // not shorter, stays
    check("Simplify(Sqrt(1-Cos(t)), 0<t<Pi/2)", //
        "Sqrt(1-Cos(t))");
    check("Simplify(Sqrt(3+3*Cos(4*t)))", //
        "Sqrt(3+3*Cos(4*t))");
    check("Refine((t^2)^(3/2), Element(t,Reals))", //
        "Abs(t)^3");
  }

  /**
   * Hyperbolic half angles under a root, parities and <code>Mod</code> of integer expressions;
   * <code>Element({a, b}, dom)</code> is a statement about each element of the list.
   */
  @Test
  public void testIntegerAndHyperbolicAssumptions() {
    check("Simplify(Sqrt(Cosh(4*t)+1)-Sqrt(2)*Cosh(2*t), t>0)", //
        "0");
    check("Simplify(Sqrt(Cosh(4*t)+1)-Sqrt(2)*Cosh(2*t))", //
        "-Sqrt(2)*Cosh(2*t)+Sqrt(1+Cosh(4*t))");
    check("Simplify(Sqrt((Cosh(t)-1)/2)-Sinh(t/2), t>0)", //
        "0");
    check("Simplify(Sqrt((Cosh(t)-1)/2)-Sinh(t/2), t<0)", //
        "-2*Sinh(t/2)");
    check("Simplify(Sqrt((Cosh(t)+1)/2)-Cosh(t/2), t>0)", //
        "0");
    // only for a non-negative argument
    check("Simplify(ArcSech(Sech(t))-t, t>0)", //
        "0");
    check("Simplify(ArcSech(Sech(t))+t, t<0)", //
        "t+ArcSech(Sech(t))");
    // one of two integers which differ by an odd number is even
    check("Simplify((-1)^(k*(k+1)), Element(k,Integers))", //
        "1");
    check("Simplify((-1)^((k+2)*(k+5)), Element(k,Integers))", //
        "1");
    check("Simplify((-1)^(k*(k+2)), Element(k,Integers))", //
        "(-1)^(k*(2+k))");
    check("Simplify((-1)^(k*(k+1)))", //
        "(-1)^(k*(1+k))");
    // the list form of Element
    check("Simplify(Sin((j+k)*Pi), Element({j,k},Integers))", //
        "0");
    check("Simplify(Cos((j+k)*Pi)-(-1)^(j+k), Element({j,k},Integers))", //
        "0");
    check("Simplify(Exp(I*Pi*(j-k))-Exp(I*Pi*(j+k)), Element({j,k},Integers))", //
        "0");
    check("Simplify(Sin((j+k)*Pi), Element({j,k},Reals))", //
        "Sin((j+k)*Pi)");
    check("Simplify(Arg(Exp(I*Pi*(2*k+1/3)))-Pi/3, Element(k,Integers))", //
        "0");
    // a power has the parity of its base
    check("Simplify(Mod(k^3,2)-Mod(k,2), Element(k,Integers))", //
        "0");
    check("Simplify(Mod(j+k,1), Element({j,k},Integers))", //
        "0");
    check("Simplify(Mod(j*k,k), Element({j,k},Integers))", //
        "0");
    check("Simplify(Mod(j*k,k))", //
        "Mod(j*k,k)");
    check("Simplify(Mod(k^3,2)-Mod(k,2))", //
        "-Mod(k,2)+Mod(k^3,2)");
    check("FullSimplify(Binomial(j+1,k)-Binomial(j,k)-Binomial(j,k-1))", //
        "0");
  }

  /**
   * A chained comparison like b > c > 0 in the assumptions, and trigonometric functions of
   * rational multiples of one angle.
   */
  @Test
  public void testChainedAssumptionsAndCommonAngle() {
    check("Refine(Sqrt((b+c)^2*(b-c)^2), b>c>0)", //
        "(b-c)*(b+c)");
    check("Refine(Sqrt((b-c)^2), b>c>0)", //
        "b-c");
    check("Refine(c>0, b>c>0)", //
        "True");
    check("Refine(Sqrt(x^2), 5>x>2)", //
        "x");
    // Csc(2*u)*Sin(u) with u == b*Pi/2
    check("Simplify(Pi*Csc(b*Pi)*Sin(b*Pi/2))", //
        "1/2*Pi*Sec(1/2*b*Pi)");
    check("Simplify(Csc(2*b*Pi/3)*Sin(b*Pi/3))", //
        "Sec(1/3*b*Pi)/2");
    // multiples 1 and 3 do not contract here
    check("Simplify(Cos(x/3)+Cos(x))", //
        "Cos(x/3)+Cos(x)");
    check("Integrate(x^b/(1+x^2), {x,0,Infinity}, Assumptions->-1<b<1)", //
        "1/2*Pi*Sec(1/2*b*Pi)");
  }

  /**
   * The logarithm of a trigonometric or hyperbolic quotient is an inverse hyperbolic function only
   * where the assumptions make the quotient positive.
   */
  @Test
  public void testFullSimplifyLogOfTrigQuotient() {
    check("FullSimplify(Log(Sec(3*x)+Tan(3*x)), -Pi/6<x<Pi/6)", //
        "ArcTanh(Sin(3*x))");
    check("FullSimplify(Log(Csc(x)-Cot(x)), 0<x<Pi)", //
        "-ArcTanh(Cos(x))");
    check("FullSimplify(Log(5*Cot(x/2)), 0<x<Pi)", //
        "ArcTanh(Cos(x))+Log(5)");
    check("FullSimplify(Log(Coth(y)-Csch(y)), y>0)", //
        "-ArcCoth(Cosh(y))");
    check("FullSimplify(Log(2*Coth(x) - 2*Csch(x)), x > 0)", //
        "-ArcCoth(Cosh(x))+Log(2)");
    // the quotient isn't positive: no rewrite
    check("FullSimplify(Log(Sec(3*x)+Tan(3*x)))", //
        "Log(Sec(3*x)+Tan(3*x))");
    check("FullSimplify(Log(Tanh(y/2)), y<0)", //
        "Log(Tanh(y/2))");
    check("FullSimplify(Log(Csc(x)+Cot(x)), Pi<x<2*Pi)", //
        "Log(Cot(x)+Csc(x))");
  }

  @Test
  public void testSimplifyConstantOnInterval() {
    // the derivative is 0 in the interval and the sum vanishes at points all over it
    check("Simplify(ArcCos(1-2*y^2)-2*ArcSin(y), 0<y<1/3)", //
        "0");
    check("Simplify(ArcTan(1/y)+ArcTan(y)-Pi/2, 1/3<y<2)", //
        "0");
    // a bound which is a multiple of Pi
    check("Simplify(ArcSin(Cos(y)) + y - Pi/2, 0 < y < Pi/3)", //
        "0");
    // a jump at 0 which the derivative doesn't see
    check("Simplify(ArcTan(1/y)+ArcTan(y)-Pi/2, -1/2<y<1/2)", //
        "-Pi/2+ArcTan(1/y)+ArcTan(y)");
  }

  @Test
  public void testSimplifyLogFusionIsSound() {
    // Log(z)+Log(1/z) is 2*I*Pi for a negative z
    check("Simplify(Log(z)+Log(1/z))", //
        "Log(1/z)+Log(z)");
    check("Simplify(Log(z)+Log(1/z), z>0)", //
        "0");
    check("Simplify(Log(-z)+Log(-1/z), z>0)", //
        "I*2*Pi");
    check("Simplify(Log(u)+Log(v))", //
        "Log(u)+Log(v)");
    check("Simplify(Log(u)+Log(v), u>0&&v>0)", //
        "Log(u*v)");
    // one argument of unknown sign may join positive ones
    check("Simplify(Log(u)+Log(v), u>0)", //
        "Log(u*v)");
    // an integer multiple of a symbolic logarithm is not made a power
    check("Simplify(3*Log(u)+Log(v), u>0)", //
        "3*Log(u)+Log(v)");
    check("Simplify(3*Log(5)+Log(7))", //
        "Log(875)");
    check("Simplify(2*Log(E+1)+Log(5))", //
        "Log(5*(1+E)^2)");
    // a symbolic power which cancels from the product
    check("Simplify(Log(5*w^3)-3*Log(w), w>0)", //
        "Log(5)");
    check("Simplify(Log(u)+Log(v)-Log(u*v), u>0)", //
        "0");
    // a positive constant factor splits off for every argument
    check("Simplify(Log(5*z)-Log(z))", //
        "Log(5)");
    // a rational common factor
    check("Simplify(1/3*Log(2-z)+1/3*Log((2+z)/(2-z)), 0<z<2)", //
        "Log(2+z)/3");
    check("Simplify(Log(u^2-v^2)-Log(u-v)-Log(u+v), u>v>0)", //
        "0");
    check("Simplify(Log(-7)+Log(-7))", //
        "I*2*Pi+Log(49)");
    check("Simplify(Log(2+I)+Log(2-I))", //
        "Log(5)");
  }
}
