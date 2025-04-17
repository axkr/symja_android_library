package org.matheclipse.core.system;

import org.junit.Test;

public class MinMaxFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testNArgMax() {
    checkNumeric("NArgMax(-2*x^2-3*x+5,x)", //
        "{-0.7500000000000001}");
    checkNumeric("NArgMax(1-(x*y-3)^2, {x, y})", //
        "{1.0,3.0}");
  }


  @Test
  public void testNArgMin() {
    checkNumeric("NArgMin(2*x^2-3*x+5,x)", //
        "{0.7499999999999998}");

    checkNumeric("NArgMin((x*y-3)^2+1, {x,y})", //
        "{1.0,3.0}");
    checkNumeric("NArgMin({x-2*y, x+y<= 1}, {x, y})", //
        "{0.0,1.0}");
  }

  @Test
  public void testNMaxValue() {
    checkNumeric("NMaxValue(-2*x^2-3*x+5,x)", //
        "6.125");
    checkNumeric("NMaxValue(1-(x*y-3)^2, {x, y})", //
        "1.0");
  }

  @Test
  public void testNMinValue() {
    checkNumeric("NMinValue(2*x^2-3*x+5,x)", //
        "3.875");

    checkNumeric("NMinValue((x*y-3)^2+1, {x,y})", //
        "1.0");
    checkNumeric("NMinValue({x-2*y, x+y<= 1}, {x, y})", //
        "-2.0");
  }

  @Test
  public void testIssue80() {
    // issue #80: LinearProgramming with expressions
    check("NMinimize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", //
        "{-13.0,{x->4.0,y->0.0}}");
    check("NMaximize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", //
        "{-2.0,{x->0.0,y->3.0}}");
  }

  @Test
  public void testNMaximize() {
    // checkNumeric("NMaximize({Sin(x^2), 0 <= x && x <= 0.5}, {x})", //
    // "{0.24740395925452294,{x->0.5}}");

    checkNumeric("NMaximize(-x^4 - 3* x^2 + x, x)", //
        "{0.08258881886826407,{x->0.16373996720676331}}");

    check("NMaximize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-2.0,{x->0.0,y->3.0}}");
    check("NMaximize({-x - y, 3*x + 2*y >= 7 && x + 2*y >= 6}, {x, y})", //
        "{-3.25,{x->0.5,y->2.75}}");
  }

  @Test
  public void testNMinimize() {
    check("NMinimize({-5-2*x+y,x+2*y<=6&&3*x+2*y<=12},{x,y})", //
        "{-13.0,{x->4.0,y->0.0}}");

    check("NMinimize({-Sinc(x)-Sinc(y)}, {x, y})", //
        "{-2.0,{x->0.0,y->0.0}}");

    check("NMinimize(x^2 + y^2 + 2, {x,y})", //
        "{2.0,{x->0.0,y->0.0}}");
    check("NMinimize({Sinc(x)+Sinc(y)}, {x, y})", //
        "{-0.434467,{x->4.49341,y->4.49341}}");

    checkNumeric("NMinimize({Sinc(x)+Sinc(y)}, {x, y})", //
        "{-0.4344672564224433,{x->4.493409457896563,y->4.493409457738778}}");

    checkNumeric("NMinimize(x^4 - 3*x^2 - x, x)", //
        "{-3.513905038934788,{x->1.3008395656679863}}");

    // TODO non-linear not supported
    // check("NMinimize({x^2 - (y - 1)^2, x^2 + y^2 <= 4}, {x, y})", "");
    check("NMinimize({-2*y+x-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-11.0,{x->0.0,y->3.0}}");
    check("NMinimize({-2*y+x-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-11.0,{x->0.0,y->3.0}}");
    check("NMinimize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-13.0,{x->4.0,y->0.0}}");
    check("NMinimize({x + 2*y, -5*x + y == 7 && x + y >= 26 && x >= 3 && y >= 4}, {x, y})", //
        "{48.83333,{x->3.16667,y->22.83333}}");
    check("NMinimize({x + y, 3*x + 2*y >= 7 && x + 2*y >= 6 }, {x, y})", //
        "{3.25,{x->0.5,y->2.75}}");
  }
}
