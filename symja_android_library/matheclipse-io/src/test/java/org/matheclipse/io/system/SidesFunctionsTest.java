package org.matheclipse.io.system;

public class SidesFunctionsTest extends AbstractTestCase {

  public SidesFunctionsTest(String name) {
    super(name);
  }

  public void testAddSides001() {
    check(
        "AddSides(x - 5 == 5, 5)", //
        "x==10");
    check(
        "AddSides(x - b > 7, b)", //
        "x>7+b");
    check(
        "AddSides(Sin(a)/a == Sin(b)/b == Sin(c)/c, r)", //
        "r+Sin(a)/a==r+Sin(b)/b==r+Sin(c)/c");
    check(
        "AddSides(ConditionalExpression(a/c==b/d, c!=0), -b/d)", //
        "ConditionalExpression(a/c-b/d==0,c!=0)");
  }

  public void testApplySides001() {
    check(
        "ApplySides(f, a==a) ", //
        "True");
    check(
        "ApplySides(Log, E^2==b)", //
        "2==Log(b)");
    check(
        "ApplySides(Sqrt, x^2 + 1 > 4)", //
        "Sqrt(1+x^2)>2");
    check(
        "ApplySides(#^4 &, a == I*b)", //
        "a^4==b^4");
    check(
        "ApplySides(Log, a==b^2==c^3)", //
        "Log(a)==Log(b^2)==Log(c^3)");
    check(
        "ApplySides(Exp, Log(x)==7)", //
        "x==E^7");
  }

  public void testDivideSides001() {
    check(
        "DivideSides(a==b,x)", //
        "a/x==b/x");
    // print message
    check(
        "DivideSides(a==b,0)", //
        "DivideSides(a==b,0)");
    check(
        "DivideSides(a>b>c>d,v)", //
        "Piecewise({{a/v>b/v>c/v>d/v,v>0},{d/v>c/v>b/v>a/v,v<0}},a>b>c>d)");
    check(
        "DivideSides(a>=b,v)", //
        "Piecewise({{a/v>=b/v,v>0},{b/v>=a/v,v<0}},a>=b)");
    check(
        "DivideSides(a<b<c<d,v)", //
        "Piecewise({{a/v<b/v<c/v<d/v,v>0},{d/v<c/v<b/v<a/v,v<0}},a<b<c<d)");
    check(
        "DivideSides(a<=b<=c<=d,v)", //
        "Piecewise({{a/v<=b/v<=c/v<=d/v,v>0},{d/v<=c/v<=b/v<=a/v,v<0}},a<=b<=c<=d)");
    check(
        "DivideSides(x/11==122)", //
        "x==1342");
    check(
        "DivideSides(x/11==121,11)", //
        "x==1331");

    check(
        "DivideSides(x/11>=a>=b,3)", //
        "x/33>=a/3>=b/3");
    check(
        "DivideSides(x/11>a>b,-3)", //
        "-b/3>-a/3>-x/33");
    check(
        "DivideSides(1 == 1, c)", //
        "True");
    check(
        "DivideSides(ConditionalExpression(a/c==b/d, c!=0))", //
        "ConditionalExpression(Piecewise({{(a*d)/(b*c)==1},b/d!=0},a/c==b/d),c!=0)");
  }

  public void testMultiplySides001() {
    // TODO
    //    check(
    //        "MultiplySides(Piecewise({{x^2== x/a+b, a>1}}, x^2<=x/a+b), a)", //
    //        " ");
    check(
        "MultiplySides(a==b,x)", //
        "a*x==b*x");
    check(
        "MultiplySides(a>b>c>d,v)", //
        "Piecewise({{a*v>b*v>c*v>d*v,v>0},{d*v>c*v>b*v>a*v,v<0}},a>b>c>d)");
    check(
        "MultiplySides(a>=b,v)", //
        "Piecewise({{a*v>=b*v,v>0},{b*v>=a*v,v<0}},a>=b)");
    check(
        "MultiplySides(a<b<c<d,v)", //
        "Piecewise({{a*v<b*v<c*v<d*v,v>0},{d*v<c*v<b*v<a*v,v<0}},a<b<c<d)");
    check(
        "MultiplySides(a<=b<=c<=d,v)", //
        "Piecewise({{a*v<=b*v<=c*v<=d*v,v>0},{d*v<=c*v<=b*v<=a*v,v<0}},a<=b<=c<=d)");
    check(
        "MultiplySides(x/3==7,3)", //
        "x==21");
    check(
        "MultiplySides(x - b > 7, b)", //
        "Piecewise({{b*(-b+x)>7*b,b>0},{7*b>b*(-b+x),b<0}},-b+x>7)");
    check(
        "MultiplySides(x/11<a<b,3)", //
        "3/11*x<3*a<3*b");
    check(
        "MultiplySides(x/11<a<b,-3)", //
        "-3*b<-3*a<-3/11*x");
    check(
        "MultiplySides(ConditionalExpression(a/c == b/d, c != 0),w)", //
        "ConditionalExpression(Piecewise({{(a*w)/c==(b*w)/d},w!=0},a/c==b/d),c!=0)");
  }

  public void testSubtractSides001() {
    check(
        "SubtractSides(x - 5 == 5)", //
        "x==10");
    check(
        "SubtractSides(x - 5 == 5, 5)", //
        "x==10");
    check(
        "SubtractSides(x - b > 7, b)", //
        "-2*b+x>7-b");
    check(
        "SubtractSides(1 == 0, c)", //
        "False");
    check(
        "SubtractSides(ConditionalExpression(a/c==b/d, c!=0))", //
        "ConditionalExpression(a/c-b/d==0,c!=0)");
  }
}
