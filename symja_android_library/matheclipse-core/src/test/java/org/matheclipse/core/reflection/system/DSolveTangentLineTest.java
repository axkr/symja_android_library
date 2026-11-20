package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;
import org.junit.jupiter.api.Tag;
import org.matheclipse.core.system.TestTags;

/**
 * A first order initial value problem whose solution through the point is the tangent line there,
 * a solution which the general solution doesn't contain or which no method finds.
 */
public class DSolveTangentLineTest extends ExprEvaluatorTestCase {

  @Test
  @Tag(TestTags.SLOW)
  public void testLineWithoutGeneralSolution() {
    check("DSolve({y'(x)==2+(y(x)-2*x)^3,y(1)==2},y,x)", //
        "{{y->Function({x},2*x)}}");
    check("DSolve({y'(x)==3+Sin(y(x)-3*x),y(0)==0},y,x)", //
        "{{y->Function({x},3*x)}}");
    check("DSolve({y'(x)==5+Log(1+(y(x)-5*x)^4),y(1)==5},y,x)", //
        "{{y->Function({x},5*x)}}");
    check("DSolve({y'(x)==Sqrt(x-y(x))+x-y(x)-1,y(0)==-1},y,x)", //
        "{{y->Function({x},-1+x)}}");
  }

  @Test
  public void testLineOutsideTheGeneralSolution() {
    // the Riccati family is a quotient in C(1); the line is its limit C(1) -> Infinity
    check("DSolve({y'(x)==y(x)^2-4*x^2+2,y(0)==0},y,x)", //
        "{{y->Function({x},2*x)}}");
    check("DSolve({y'(x)==y(x)^2-9*x^2+3,y(1)==3},y,x)", //
        "{{y->Function({x},3*x)}}");
    // a symbolic initial point
    check("DSolve({y'(x)==Sqrt(4*x-4*y(x))-1,y(b)==b-1},y,x)", //
        "{{y->Function({x},-1+x)}}");
  }

  @Test
  public void testConstantWithoutGeneralSolution() {
    check("DSolve({y'(x)==Cos(x*y(x))-1,y(0)==0},y,x)", //
        "{{y->Function({x},0)}}");
    check("DSolve({y'(x)==x^2*Log(y(x)/3),y(2)==3},y,x)", //
        "{{y->Function({x},3)}}");
  }

  @Test
  public void testLineNotProven() {
    // with an unknown function the tangent line p(0)*x can't be shown to solve the equation
    check("DSolve({y'(x)==p(x)+y(x)^2,y(0)==0},y,x)", //
        "DSolve({y'(x)==p(x)+y(x)^2,y(0)==0},y,x)");
  }

  @Test
  public void testLineNotUnique() {
    // F_y is infinite on the line y == x, so the line isn't the only solution through the point
    check("DSolve({y'(x)==1+Sqrt(y(x)^2-x^2),y(1)==1},y,x)", //
        "DSolve({y'(x)==1+Sqrt(-x^2+y(x)^2),y(1)==1},y,x)");
    // u == y-2*x solves u' == Sign(u) by 0 and by Max(0,x-c): 2*x isn't the only solution
    check("DSolve({y'(x)==2+Sign(y(x)-2*x),y(0)==0},y,x)", //
        "DSolve({y'(x)==2-Sign(2*x-y(x)),y(0)==0},y,x)");
  }
}
