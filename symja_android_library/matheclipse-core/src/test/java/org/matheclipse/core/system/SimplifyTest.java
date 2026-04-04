package org.matheclipse.core.system;

import org.junit.Test;

public class SimplifyTest extends ExprEvaluatorTestCase {

  @Test
  public void testSimplify0() {
    check("Factor(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
        "1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2))))");
    check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
        "(2*x^(3/2))/(Sqrt(x)-ArcTan(Sqrt(x))-x*ArcTan(Sqrt(x)))");
  }

  @Test
  public void testFullSimplify() {
    // TODO
    // check("FullSimplify(a*b*c/Abs(b))", //
    // "a*c*Sign(b)");
    check("FullSimplify(Mod(Mod(a, c) + Mod(b*q, c) + f(x), c))", //
        "Mod(a+b*q+f(x),c)");

    check("FullSimplify(a*Conjugate(a))", //
        "Abs(a)^2");
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
        "(d*(2*d*Sqrt(-e/d)+4*e*x-2*e*Sqrt(-e/d)*x^2))/(d+e*x^2)");
    check("Together( 1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2) )", //
        "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(d+e*x^2)");
    check("Together( (d*(2*d*Sqrt(-e/d)+4*e*x-2*e*Sqrt(-e/d)*x^2))/(d+e*x^2) )", //
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
    check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "True");

    // check("Factor((d^2+2*d*e*x^2+e^2*x^4))",//
    // "(d+e*x^2)^2");
    // check("Together(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))",//
    // "(-2*d^2*Sqrt(-e/d)-4*d*e*x+2*d*e*Sqrt(-e/d)*x^2)/(d^2+2*d*e*x^2+e^2*x^4)");
    check("FullSimplify(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))", //
        "(-2*d*Sqrt(-e/d)-4*e*x+2*e*Sqrt(-e/d)*x^2)/(d+2*e*x^2)");

    check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
        "108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
    check("FullSimplify(p)", //
        "(1+x)^2*(2+x)^2*(3+x)^3");
    check("FullSimplify(Cosh(x)/(b*Cosh(x)+c*Sinh(x)))", //
        "1/(b+c*Tanh(x))");
    check("FullSimplify((b*Cosh(x)+c*Sinh(x))/Cosh(x))", //
        "b+c*Tanh(x)");
    check("Simplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "-ChebyshevT(n,x)+Cos(n*ArcCos(x))==0");
    // FullSimplify uses FunctionExpand and can test the equation:
    check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "True");
    check("FullSimplify(Cosh(x)+Sinh(x))", //
        "E^x");
  }

  @Test
  public void testFullSimplifyIssue856() {
    // github issue #856
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (Sqrt(2) + 4))", //
        "7*Sqrt(6)");
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (-Sqrt(2) - 4) )", //
        "-7*Sqrt(6)");
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) / (Sqrt(2) - 4) )", //
        "-Sqrt(3/2)");
  }

  @Test
  public void testSimplify() {
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
        "2*x+x^2==3");
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
    check("Simplify((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
        "-I*Cos(2*x)-Sin(2*x)"); // -I*Cos(2*x)-Sin(2*x)

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
}
