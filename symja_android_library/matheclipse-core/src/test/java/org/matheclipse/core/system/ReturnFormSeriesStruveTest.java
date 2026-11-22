package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Return(value, form)</code> leaving the named iteration construct; sums of a polynomial
 * times <code>c^k/k!</code>; Struve functions of a negative integer order; Newton's method on a
 * steep exponential.
 */
public class ReturnFormSeriesStruveTest extends ExprEvaluatorTestCase {

  @Test
  public void testReturnWithForm() {
    check("Table(If(i==4,Return(10*i,Table),i),{i,6})", //
        "40");
    // the innermost Table is left
    check("Table(Table(If(j==2,Return(i*j,Table),j),{j,3}),{i,2})", //
        "{2,4}");
    check("Do(Table(If(j==2,Return({i,j},Do),j),{j,3}),{i,3})", //
        "{1,2}");
    check("Map(If(#>5,Return(-#,Map),#)&,{2,4,6,8})", //
        "-6");
    check("Sum(If(k==3,Return(100,Sum),k),{k,10})", //
        "100");
    check("Select({5,6,7,8},If(#==7,Return(-#,Select),EvenQ(#))&)", //
        "-7");
    // no Table around: the Do takes the value as of a plain Return
    check("Do(If(i==2,Return(7,Table)),{i,3})", //
        "7");
    // a plain Return in a Table body is the element
    check("Table(If(i==3,Return(i),i),{i,4})", //
        "{1,2,3,4}");
  }

  @Test
  public void testPolynomialTimesExponentialSeries() {
    check("Sum((k-2)^2*3^k/k!,{k,0,Infinity})", //
        "4*E^3");
    check("Sum((k^2+k+1)*y^k/k!,{k,1,Infinity})", //
        "-1+E^y*(1+y)^2");
    // the polynomial factor factored where that is shorter
    check("Sum((k^4-k)*z^k/k!,{k,0,Infinity})", //
        "E^z*z^2*(7+6*z+z^2)");
  }

  @Test
  public void testStruveNegativeIntegerOrder() {
    // FunctionExpand writes the negative order by the positive one
    check("{StruveH(-2,x),FunctionExpand(StruveH(-2,x)),FunctionExpand(StruveL(-2,x))}", //
        "{StruveH(-2,x),-2/(Pi*x)-2/3*x/Pi+StruveH(2,x),-2/(Pi*x)+2/3*x/Pi+StruveL(2,x)}");
    check("N({StruveH(-2,7/10),StruveL(-2,7/10)},18)", //
        "{-1.04378005589633701,-0.746011437684495758}");
  }

  @Test
  public void testConjugateOfIntegerPower() {
    check("{Conjugate(w^5),Conjugate((2+I*w)^(-2)),Conjugate(w^(1/3))}", //
        "{Conjugate(w)^5,1/(2-I*Conjugate(w))^2,Conjugate(w^(1/3))}");
  }

  @Test
  public void testFindRootSteepExponential() {
    // far from the root of E^(200*x)-3 each Newton step is about 1/200
    check("FindRoot(Exp(200*x)-3,{x,1})", //
        "{x->0.00549306}");
  }
}
