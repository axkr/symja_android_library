package org.matheclipse.core.sympy.assumptions;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestRefine extends ExprEvaluatorTestCase {

  @Test
  public void testRefinePow() {
    // https://github.com/sympy/sympy/blob/master/sympy/assumptions/tests/test_refine.py
    // assert refine((-1)**x, Q.even(x)) == 1
    check("Refine((-1)^x, Element(x/2,Integers))", //
        "1");
    // assert refine((-1)**x, Q.odd(x)) == -1
    check("Refine((-1)^x, Element((x-1)/2,Integers))", //
        "-1");
    // assert refine((-1)**(x + y), Q.even(x)) == (-1)**y
    check("Refine((-1)^(x+y), Element(x/2,Integers))", //
        "(-1)^y");
    // assert refine((-1)**(x + y + z), Q.odd(x) & Q.odd(z)) == (-1)**y
    check("Refine((-1)^(x+y+z), Element((x-1)/2,Integers)&&Element((z-1)/2,Integers))", //
        "(-1)^y");
    // assert refine((-1)**(x + y + 1), Q.odd(x)) == (-1)**y
    check("Refine((-1)^(x+y+1), Element((x-1)/2,Integers))", //
        "(-1)^y");
    // assert refine((-1)**(x + y + 2), Q.odd(x)) == (-1)**(y + 1)
    check("Refine((-1)^(x+y+2), Element((x-1)/2,Integers))", //
        "(-1)^(1+y)");
    // assert refine((-1)**(x + 3)) == (-1)**(x + 1)
    check("Refine((-1)^(x+3))", //
        "(-1)^(1+x)");
    check("Refine((-1)^x, Element(x,Integers))", //
        "(-1)^x");
    check("Refine((-1)^(2*n), Element(n,Integers))", //
        "1");
  }

  @Test
  public void testRefineAtan2() {
    // assert refine(atan2(y, x), Q.real(y) & Q.positive(x)) == atan(y/x)
    check("Refine(ArcTan(x,y), x>0&&Element(y,Reals))", //
        "ArcTan(y/x)");
    // assert refine(atan2(y, x), Q.negative(y) & Q.positive(x)) == atan(y/x)
    check("Refine(ArcTan(x,y), x>0&&y<0)", //
        "ArcTan(y/x)");
    // assert refine(atan2(y, x), Q.negative(y) & Q.negative(x)) == atan(y/x) - pi
    check("Refine(ArcTan(x,y), x<0&&y<0)", //
        "-Pi+ArcTan(y/x)");
    // assert refine(atan2(y, x), Q.positive(y) & Q.negative(x)) == atan(y/x) + pi
    check("Refine(ArcTan(x,y), x<0&&y>0)", //
        "Pi+ArcTan(y/x)");
    // assert refine(atan2(y, x), Q.zero(y) & Q.negative(x)) == pi
    check("Refine(ArcTan(x,y), x<0&&y==0)", //
        "Pi");
    // assert refine(atan2(y, x), Q.positive(y) & Q.zero(x)) == pi/2
    check("Refine(ArcTan(x,y), x==0&&y>0)", //
        "Pi/2");
    // assert refine(atan2(y, x), Q.negative(y) & Q.zero(x)) == -pi/2
    check("Refine(ArcTan(x,y), x==0&&y<0)", //
        "-Pi/2");
    check("Refine(ArcTan(x,y))", //
        "ArcTan(x,y)");
    check("Refine(ArcTan(x,y), y>0)", //
        "ArcTan(x,y)");
    check("ArcTan(2,y)", //
        "ArcTan(2,y)");
  }

  @Test
  public void testRefineSign() {
    // assert refine(sign(x), Q.positive(x)) == 1
    check("Refine(Sign(x), x>0)", //
        "1");
    // assert refine(sign(x), Q.negative(x)) == -1
    check("Refine(Sign(x), x<0)", //
        "-1");
    // assert refine(sign(x), Q.zero(x)) == 0
    check("Refine(Sign(x), x==0)", //
        "0");
    // assert refine(sign(x), True) == sign(x)
    check("Refine(Sign(x))", //
        "Sign(x)");
    // assert refine(sign(Abs(x)), Q.nonzero(x)) == 1
    // TODO the assumption x!=0 isn't available in the assumptions of the evaluation engine
    check("Refine(Sign(Abs(x)), x!=0)", //
        "Sign(Abs(x))");
    check("Refine(Sign(Abs(x)), x>0)", //
        "1");
    // TODO assumptions for Re(x) and Im(x) aren't available in the evaluation engine:
    // x = Symbol('x', imaginary=True)
    // assert refine(sign(x), Q.positive(im(x))) == S.ImaginaryUnit
    // assert refine(sign(x), Q.negative(im(x))) == -S.ImaginaryUnit
    check("Refine(Sign(2*I*x), x>0)", //
        "I");
    check("Refine(Sign(-I*x), x>0)", //
        "-I");
  }
}
