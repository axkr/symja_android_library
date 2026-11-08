package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The grouping of sums written with <code>+</code> and <code>-</code>, as the parser builds it and
 * as held subtractions and sums are printed.
 */
public class HeldSumGroupingTest extends ExprEvaluatorTestCase {

  @Test
  public void testParsedSumIsFlat() {
    // + and - written in a row are one sum
    check("FullForm(Hold(m-n+k))", //
        "Hold(Plus(m, Times(-1, n), k))");
    check("FullForm(Hold(m+n-k+j))", //
        "Hold(Plus(m, n, Times(-1, k), j))");
  }

  @Test
  public void testParenthesizedSumKeepsItsNode() {
    check("FullForm(Hold((m-n+k)-j))", //
        "Hold(Plus(Plus(m, Times(-1, n), k), Times(-1, j)))");
    check("FullForm(Hold((m+n+k)-j))", //
        "Hold(Plus(Plus(m, n, k), Times(-1, j)))");
    check("FullForm(Hold((m-n)-k))", //
        "Hold(Plus(Plus(m, Times(-1, n)), Times(-1, k)))");
    check("Hold((m+n)-k)", //
        "Hold((m+n)-k)");
    check("Hold((m-n+k)-j)", //
        "Hold((m-n+k)-j)");
    // evaluated, the sums are flattened as before
    check("(m-n+k)-j", //
        "-j+k+m-n");
  }

  @Test
  public void testHeldSubtractPrintsItsGrouping() {
    check("Hold(Subtract(m,Plus(n,k,j)))", //
        "Hold(m-(n+k+j))");
    check("Hold(Subtract(Plus(m,n),k))", //
        "Hold((m+n)-k)");
    check("Hold(Subtract(m,Subtract(n,k)))", //
        "Hold(m-(n-k))");
    // a second minus is separated from the first one, not read as a decrement
    check("Hold(Subtract(m,Times(-1,n)))", //
        "Hold(m- -n)");
    check("Hold(Subtract(m,-7))", //
        "Hold(m- -7)");
    check("Hold(Subtract(m,3+2*I))", //
        "Hold(m-(3+2*I))");
    check("Hold(Subtract(m,Complex(3,-2)))", //
        "Hold(m-(Complex(3,-2)))");
    check("Hold(Subtract(m,5*n))", //
        "Hold(m-5*n)");
  }

  @Test
  public void testHeldSubtractAfterFirstSummand() {
    check("Hold(Plus(p,Subtract(q,r)))", //
        "Hold(p+(q-r))");
    check("Hold(Plus(Subtract(p,q),r))", //
        "Hold(p-q+r)");
    check("Hold(Plus(Subtract(p,q),Subtract(r,s)))", //
        "Hold(p-q+(r-s))");
    check("Hold(Plus(p,Subtract(q,r),s))", //
        "Hold(p+(q-r)+s)");
    check("Hold(Plus(Subtract(17,-6),2.5))", //
        "Hold(17- -6+2.5)");
    check("ToString(Hold(Plus(Subtract(p,q),Subtract(r,s))),InputForm)", //
        "Hold(p - q + (r - s))");
    check("ToString(Hold(Subtract(p,Times(-1,q))),InputForm)", //
        "Hold(p - -q)");
  }

  @Test
  public void testHeldQuotientWithNegativeNumerator() {
    check("Hold(Divide(-3,q))", //
        "Hold(-3/q)");
    check("Hold(Plus(p,Divide(-7,q)))", //
        "Hold(p+-7/q)");
    check("Hold(Plus(Times(-4,r),Divide(-7,Divide(q,s))))", //
        "Hold(-4*r+-7/(q/s))");
    check("Hold(Subtract(p,Divide(-7,q)))", //
        "Hold(p- -7/q)");
    check("Hold(Divide(-1.5,q))", //
        "Hold(-1.5/q)");
    check("ToString(Hold(Plus(p,Divide(-7,q))),InputForm)", //
        "Hold(p + -7/q)");
    check("Hold(Times(p,Divide(-7,q)))", //
        "Hold(p*-7/q)");
    check("Hold(Times(Divide(-7,q),p,r))", //
        "Hold(-7/q*p*r)");
    check("Hold(Power(Divide(-7,q),3))", //
        "Hold((-7/q)^3)");
    // a numerator which is no number keeps its parentheses
    check("Hold(Divide(-p,q))", //
        "Hold((-p)/q)");
    // a fraction as numerator is written in its long form
    check("Hold(Divide(Rational(-7,3),q))", //
        "Hold(Rational(-7,3)/q)");
    check("Hold(Divide(Rational(7,3),q))", //
        "Hold(Rational(7,3)/q)");
  }

  @Test
  public void testHeldSubtractOfNumberObject() {
    // a fraction or complex number right of a binary minus is written in its long form, in
    // parentheses if the complex number has a real part which is not an exact zero
    check("Hold(Subtract(p,Rational(-7,3)))", //
        "Hold(p-Rational(-7,3))");
    check("Hold(Subtract(p,Complex(0,-4)))", //
        "Hold(p-Complex(0,-4))");
    check("Hold(Subtract(p,Complex(0,-1.5)))", //
        "Hold(p-(Complex(0.0,-1.5)))");
    check("ToString(Hold(Subtract(p,Rational(-7,3))),InputForm)", //
        "Hold(p - Rational(-7,3))");
    // the long form reads back as the number
    check("Hold(Rational(-7,3))[[1]]===-7/3", //
        "True");
    check("Hold(Complex(0,-4))[[1]]===-4*I", //
        "True");
    check("Hold(Subtract(p,Rational(7,3)))", //
        "Hold(p-Rational(7,3))");
    check("Hold(Subtract(p,Complex(-3,-4)))", //
        "Hold(p-(Complex(-3,-4)))");
    check("Hold(Subtract(p,Complex(0,4)))", //
        "Hold(p-Complex(0,4))");
    // integers and machine reals as before
    check("Hold(Subtract(p,-4))", //
        "Hold(p- -4)");
    check("Hold(Subtract(p,-1.5))", //
        "Hold(p- -1.5)");
  }

  @Test
  public void testHeldSubtractOfImaginaryUnit() {
    // I and -I keep their short form right of a binary minus and as numerator of a quotient
    check("Hold(Subtract(u,I))", //
        "Hold(u-I)");
    check("Hold(Subtract(u,-I))", //
        "Hold(u- -I)");
    check("Hold(Subtract(u,Complex(0,-1)))", //
        "Hold(u- -I)");
    check("Hold(Divide(I,w))", //
        "Hold(I/w)");
    check("ToString({Hold(Subtract(u,I)),Hold(Subtract(u,-I)),Hold(Divide(I,w))},InputForm)", //
        "{Hold(u - I),Hold(u - -I),Hold(I/w)}");
    // other imaginary numbers are written in their long form
    check("Hold(Subtract(u,Complex(0,3)))", //
        "Hold(u-Complex(0,3))");
    check("Hold(Subtract(u,Complex(0,-3)))", //
        "Hold(u-Complex(0,-3))");
    // I as a factor is no number object
    check("Hold(Subtract(u,I*v))", //
        "Hold(u-I*v)");
    check("Hold(Subtract(u,Times(-I,v)))", //
        "Hold(u-(-I)*v)");
    check("held={Hold(Subtract(u,I)),Hold(Subtract(u,-I)),Hold(Divide(I,w)),"
        + "Hold(Subtract(u,Times(-I,v)))};"
        + "{Map(ReleaseHold(ToExpression(ToString(#,InputForm)))===ReleaseHold(#)&,held),"
        + "Map(ReleaseHold(ToExpression(ToString(#)))===ReleaseHold(#)&,held)}", //
        "{{True,True,True,True},{True,True,True,True}}");
  }

  @Test
  public void testNegativeImaginaryUnitAsPowerBase() {
    // -I as a base keeps its parentheses, -I^w would be -(I^w)
    check("(-I)^w", //
        "(-I)^w");
    check("t*(-I)^w+3", //
        "3+(-I)^w*t");
    check("Hold(Power(Complex(0,-1),3))", //
        "Hold((-I)^3)");
    check("Hold(Power(Complex(0,-1),-w))", //
        "Hold((-I)^(-w))");
    check("Hold(Power(Complex(0,-1),-1))", //
        "Hold(1/(-I))");
    check("Hold(Subtract(t,(-I)^w))", //
        "Hold(t-(-I)^w)");
    // other bases as before
    check("(-3*I)^w", //
        "(-I*3)^w");
    check("(-I*t)^w", //
        "(-I*t)^w");
    check("Complex(0.0,-1.0)^w", //
        "(I*(-1.0))^w");
    check("I^w", //
        "I^w");
    check("ToString((-I)^w,InputForm)", //
        "(-I)^w");
    // the printed forms read back with the same value
    check("held={Hold((-I)^w),Hold(Power(Complex(0,-1),3)),Hold(Power(Complex(0,-1),-w)),"
        + "Hold(Power(Complex(0,-1),-1)),Hold(Subtract(t,(-I)^w)),Hold(t*(-I)^w+3)};"
        + "Map(ReleaseHold(ToExpression(ToString(#)))===ReleaseHold(#)&,held)", //
        "{True,True,True,True,True,True}");
  }

  @Test
  public void testHeldComplexNumeratorAndFactor() {
    // a complex number other than I and -I over a denominator is written in its long form
    check("Hold(Divide(Complex(0,5),m))", //
        "Hold((Complex(0,5))/m)");
    check("Hold(Divide(Complex(-3,1),m))", //
        "Hold((Complex(-3,1))/m)");
    check("Hold(Divide(Complex(0.0,1.0),m))", //
        "Hold((Complex(0.0,1.0))/m)");
    check("Hold(Divide(I,m))", //
        "Hold(I/m)");
    check("Hold(Divide(-I,m))", //
        "Hold(-I/m)");
    check("ToString({Hold(Divide(-I,m)),Hold(Divide(Complex(0,5),m))},InputForm)", //
        "{Hold(-I/m),Hold((Complex(0,5))/m)}");
    // a machine imaginary unit is no exact unit
    check("Hold(Subtract(m,Complex(0.0,1.0)))", //
        "Hold(m-(Complex(0.0,1.0)))");
    check("Hold(Subtract(m,Complex(0.0,-1.0)))", //
        "Hold(m-(Complex(0.0,-1.0)))");
    // the factor -I of a product keeps its parentheses right of a binary minus
    check("Hold(Subtract(m,Times(-I,n,k)))", //
        "Hold(m-(-I)*n*k)");
    check("Hold(Subtract(m,Times(-I,n+k)))", //
        "Hold(m-(-I)*(n+k))");
    check("ToString(Hold(Subtract(m,Times(-I,n))),InputForm)", //
        "Hold(m - (-I)*n)");
    check("Hold(Subtract(m,Times(I,n)))", //
        "Hold(m-I*n)");
    check("held={Hold(Divide(Complex(0,5),m)),Hold(Divide(-I,m)),"
        + "Hold(Subtract(m,Complex(0.0,1.0))),Hold(Subtract(m,Times(-I,n,k))),Hold(Subtract(m,Times(-I,n+k)))};"
        + "{Map(ReleaseHold(ToExpression(ToString(#,InputForm)))===ReleaseHold(#)&,held),"
        + "Map(ReleaseHold(ToExpression(ToString(#)))===ReleaseHold(#)&,held)}", //
        "{{True,True,True,True,True},{True,True,True,True,True}}");
  }

  @Test
  public void testHeldQuotientAsDenominator() {
    check("Hold(p+(-7)/(q/r))", //
        "Hold(p-7/(q/r))");
    check("Hold(u/(v/w))", //
        "Hold(u/(v/w))");
    check("HoldForm(2-u/(v/w))", //
        "2-u/(v/w)");
    // evaluated, the quotient is cancelled as before
    check("u/(v/w)", //
        "(u*w)/v");
  }

  @Test
  public void testHeldSumsReadBack() {
    // the printed form of a parsed sum reads back as the same held expression
    check("held={Hold(p+(-7)/(q/r)),Hold(p+(q-r)),Hold((p-q)+r),Hold(p-q+(r-s)),"
        + "Hold(u/(v/w)-1)};"
        + "{Map(ToExpression(ToString(#,InputForm))===#&,held),"
        + "Map(ToExpression(ToString(#))===#&,held)}", //
        "{{True,True,True,True,True},{True,True,True,True,True}}");
    // the printed form of held Subtract and Divide calls reads back with the same value
    check("held={Hold(Plus(p,Divide(-7,q))),Hold(Plus(p,Subtract(q,r))),"
        + "Hold(Plus(Subtract(p,q),Subtract(p,q))),Hold(Subtract(p,Times(-1,q))),"
        + "Hold(Divide(Subtract(9,Divide(4,1)),Plus(Subtract(5,-2),1.5))),"
        + "Hold(Subtract(p,Divide(-7,Divide(q,r)))),Hold(Times(p,Divide(-7,q),r)),"
        + "Hold(Subtract(p,Rational(-7,3))),Hold(Subtract(p,Complex(0,-4))),"
        + "Hold(Subtract(p,Complex(-3,-4))),Hold(Divide(Rational(7,3),q))};"
        + "{Map(ReleaseHold(ToExpression(ToString(#,InputForm)))===ReleaseHold(#)&,held),"
        + "Map(ReleaseHold(ToExpression(ToString(#)))===ReleaseHold(#)&,held)}", //
        "{{True,True,True,True,True,True,True,True,True,True,True},"
            + "{True,True,True,True,True,True,True,True,True,True,True}}");
  }
}
