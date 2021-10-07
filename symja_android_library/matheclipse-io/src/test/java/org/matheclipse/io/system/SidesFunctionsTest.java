package org.matheclipse.io.system;

public class SidesFunctionsTest extends AbstractTestCase {

  public SidesFunctionsTest(String name) {
    super(name);
  }

  public void testApplySides001() {
    check(
        "ApplySides(Sqrt, x^2 + 1 > 4)", //
        "Sqrt(1+x^2)>2");
    check(
        "ApplySides(#^4 &, a == I*b)", //
        "a^4==b^4");
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
  }

  public void testMultiplySides001() {
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
  }

  public void testDivideSides001() {
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
  }
}
