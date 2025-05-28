package org.matheclipse.core.system;

import org.junit.Test;

public class SubsetTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testSubsetCases() {
    check("SubsetCases(I+{x,-3,-1/2},2*{{}})", //
        "SubsetCases({I+x,-3+I,-1/2+I},{{}})");
    check("SubsetCases(1/0,{{}})", //
        "SubsetCases(ComplexInfinity,{{}})");
    check("SubsetCases({1, 2, 3, a, a, b, c}, {a, _Integer})", //
        "{{a,1},{a,2}}");
    check("SubsetCases({1, 2, 3, a, a, b, c}, {a, x_Integer} :> f(x))", //
        "{f(1),f(2)}");
    check("SubsetCases({a,b,c,d},{x_,y_} :> f(x,y))", //
        "{f(a,b),f(c,d)}");
    check("SubsetCases({a,b,c,d},{x_,y_})", //
        "{{a,b},{c,d}}");
  }

  @Test
  public void testSubsetReplace() {
    check("SubsetReplace({3.14159},ComplexInfinity->True)", //
        "{3.14159}");
    check("SubsetReplace({a},{1}->True)", //
        "{a}");
    check("SubsetReplace({a},1->True)", //
        "SubsetReplace({a},1->True)");
    check("SubsetReplace(ComplexInfinity, {3, 3} :> Splice({x, x, x}))", //
        "ComplexInfinity");
    // TODO add specialized matcher for pattern sequences
    check("SubsetReplace({1, a, 2, b, 3}, {__Integer} :> X)", //
        "{X,a,X,b,X}");

    check("SubsetReplace({1, 2, 3, 2, 3, 4, 3}, {3, 3} :> Splice({x, x, x}))", //
        "{1,2,x,x,x,2,4,3}");

    check("SubsetReplace({a,b,c,d},{x_,y_} :> f(x,y))", //
        "{f(a,b),f(c,d)}");
    check("SubsetReplace({1, 2, 3, a, a, b, c}, {a, x_Integer} :> f(x))", //
        "{3,f(1),f(2),b,c}");
    check("SubsetReplace({1, 2, 3, 4, 5, 6}, {2, 4} -> x)", //
        "{1,x,3,5,6}");
    check("SubsetReplace({a, x_Integer} :> f(x))[{1, 2, 3, a, a, b, c}]", //
        "{3,f(1),f(2),b,c}");
    check("SubsetReplace({1, 2, 3, a, a, b, c},{{2,3}->x, {a, x_Integer} :> f(x)})", //
        "{x,f(1),a,b,c}");
    check("SubsetReplace({1, 2, 3, a, a, b, c},{{2,3}->x, {a, x_Integer} :> f(x)}, 1)", //
        "{1,x,a,a,b,c}");
  }

}
