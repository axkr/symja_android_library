package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

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
    // a limit beyond the machine integers is reported under SubsetCases
    check("Check(SubsetCases({p,q,r,s},{u_,v_},10^30), failed, SubsetCases::error)", //
        "failed");
  }

  @Test
  public void testSubsetCasesCondition() {
    // the condition is tested on each combination, not only on the first elements which fit
    check("SubsetCases({1, 2, 3, 4, 5, 6}, {x_, y_} /; x + y == 7)", //
        "{{1,6},{2,5},{3,4}}");
    check("SubsetCases({4, 1, 3}, {x_, y_} /; x > y)", //
        "{{4,1}}");
    check("SubsetCases({1, 2, 3, 4, 5, 6}, {x_, y_} /; x + y == 7, 2)", //
        "{{1,6},{2,5}}");
    check("SubsetCases({5, 3, 8, 1}, {x_, y_, z_} /; x < y < z)", //
        "{{3,5,8}}");
    check("SubsetCases(Range(400), {x_, y_} /; x*y == 391)", //
        "{{1,391},{17,23}}");
    // a condition on the left-hand side or on the right-hand side of a rule
    check("SubsetCases({2, 9, 4, 7}, {x_, y_} /; x + y == 11 :> x*y)", //
        "{18,28}");
    check("SubsetCases({2, 9, 4, 7}, {x_, y_} :> x*y /; x + y == 11)", //
        "{18,28}");
    // the elements are tried in every order
    check("SubsetCases({1, 2}, {_Integer, 1})", //
        "{{2,1}}");
    check("SubsetPosition({1, 2, 3, 4, 5, 6}, {x_, y_} /; x + y == 7)", //
        "{{1,6},{2,5},{3,4}}");
  }

  @Test
  public void testSubsetReplace() {
    // message SubsetReplace: List expected at position 1 in
    // SubsetReplace(1/2^(2-I*2),{}->10,1/Sqrt(-1+I)).
    check("SubsetReplace(1/2^(2-I*2),{}->10,1/Sqrt(-1+I))", //
        "SubsetReplace(1/2^(2-I*2),{}->10,1/Sqrt(-1+I))");
    check("SubsetReplace(ComplexInfinity, {3, 3} :> Splice({x, x, x}))", //
        "SubsetReplace(ComplexInfinity,{3,3}:>Splice({x,x,x}))");


    check("SubsetReplace({3.14159},ComplexInfinity->True)", //
        "{3.14159}");
    check("SubsetReplace({a},{1}->True)", //
        "{a}");
    check("SubsetReplace({a},1->True)", //
        "SubsetReplace({a},1->True)");

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
