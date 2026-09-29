package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for <code>Unequal</code> assumptions like <code>x != 0</code> */
public class UnequalAssumptionTest extends ExprEvaluatorTestCase {

  @Test
  public void testUnequalAssumptions() {
    check("Refine(x==0, x!=0)", //
        "False");
    check("Refine(x!=0, x!=0)", //
        "True");
    check("Refine(x!=0, x==0)", //
        "False");
    check("Refine(x==1, x!=1)", //
        "False");
    check("Refine(x==2, x!=1)", //
        "x==2");
    // not(...) is converted to x!=0
    check("Refine(x==0, !(x==0))", //
        "False");

    // two symbolic sides are stored as x-y != 0
    check("Refine(x==y, x!=y)", //
        "False");
    check("Refine(y==x, x!=y)", //
        "False");
    check("Refine(x-y==0, x!=y)", //
        "False");
    check("Refine(x==Pi, x!=Pi)", //
        "False");

    // a product or power of non zero factors is non zero
    check("Refine(x^2==0, x!=0)", //
        "False");
    check("Refine(1/x==0, x!=0)", //
        "False");
    check("Refine(x*y==0, x!=0 && y!=0)", //
        "False");
    check("Refine(x*y==0, x!=0)", //
        "x*y==0");

    // an interval of real values which doesn't contain the value
    check("Refine(x==0, x>0)", //
        "False");
    check("Refine(x!=0, x>0)", //
        "True");

    // Abs(z) > 0 <=> z != 0
    check("Refine(Abs(x)>0, x!=0)", //
        "True");
    check("Refine(Sign(Abs(x)), x!=0)", //
        "1");
    check("Simplify(Abs(Sign(z)), z != -1 && z != 0 && z != 1)", //
        "1");

    // an unequal inside Or is a fact if every branch implies it;
    check("Refine(x==0, x!=0 || x>1)", //
        "False");
    check("Refine(x==0, x!=0 || x<-1)", //
        "False");
    check("Refine(x==0, x!=0 || x>=0)", //
        "x==0");
    check("Refine(x==0, x!=0 || y>1)", //
        "x==0");
    check("Refine(x==0, y!=0)", //
        "x==0");
    // an unequal no longer aborts the list of assumptions
    check("Refine(z>0, {x!=y, z>0})", //
        "True");

    check("Refine(Piecewise({{1, x != 0}}, 3), x != 0)", //
        "1");
    check("Assuming(x!=0, Refine(x==0))", //
        "False");

    // doesn't report x==0 && x!=0 as contradictory and uses x==0
    check("Refine(x, x==0 && x!=0)", //
        "0");
    // a variable which is assumed to be equal to a number is replaced
    check("Refine(x, x==0)", //
        "0");
    check("Refine(x^2+y, x==3)", //
        "9+y");
    check("Refine(x+y, x==y)", //
        "x+y");
  }
}
