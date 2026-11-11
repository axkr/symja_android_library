package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Cosines of the regular 17-gon in nested square roots, and nested radicals of fields
 * of radicals denested in Simplify.
 */
public class RadicalExpansionTest extends ExprEvaluatorTestCase {

  @Test
  public void testFunctionExpandHeptadecagon() {
    check("FunctionExpand(Cos(2*Pi/17))", //
        "-1/16+Sqrt(17)/16+Sqrt(34-2*Sqrt(17))/16+Sqrt(17+3*Sqrt(17)-Sqrt(34-2*Sqrt(17))-\n"
            + "2*Sqrt(34+2*Sqrt(17)))/8");
    check("FreeQ(Table(FunctionExpand(Cos(k*Pi/17)), {k, 1, 16}), Cos)", //
        "True");
    check("Max(Table(Abs(N(FunctionExpand(Cos(k*Pi/17)) - Cos(k*Pi/17), 30)), {k, 1, 16})) < 10^-25", //
        "True");
    // half angles and sines
    check("Max(Table(Abs(N(FunctionExpand(Cos(k*Pi/34)) - Cos(k*Pi/34), 30)), {k, 1, 33, 2})) < 10^-25", //
        "True");
    check("Abs(N(FunctionExpand(Sin(5*Pi/17)) - Sin(5*Pi/17), 30)) < 10^-25", //
        "True");
    // other denominators stay, and the 257-gon too
    check("FunctionExpand(Cos(Pi/7))", //
        "Cos(Pi/7)");
    check("FunctionExpand(Cos(4*Pi/257))", //
        "Cos(4/257*Pi)");
  }

  @Test
  public void testFunctionExpandProductsOfFermatPrimes() {
    // Cos(a+b) of the angles of the 3-, 5- and 17-gon
    check("FreeQ({FunctionExpand(Cos(2*Pi/15)), FunctionExpand(Sin(7*Pi/15)), "
        + "FunctionExpand(Cos(4*Pi/51)), FunctionExpand(Cos(Pi/85))}, Cos|Sin)", //
        "True");
    check("Max(Abs(N({FunctionExpand(Cos(2*Pi/15)), FunctionExpand(Sin(7*Pi/15)), "
        + "FunctionExpand(Cos(4*Pi/51)), FunctionExpand(Cos(Pi/85))}, 30) - "
        + "N({Cos(2*Pi/15), Sin(7*Pi/15), Cos(4*Pi/51), Cos(Pi/85)}, 30))) < 10^-25", //
        "True");
  }

  @Test
  public void testSimplifyDenestRadicals() {
    // 2*(3^(1/3)-1)^2 under a square root
    check("Simplify(Sqrt(2 - 4*3^(1/3) + 2*3^(2/3)))", //
        "Sqrt(2)*(-1+3^(1/3))");
    // a factor of an expression with a variable in FullSimplify
    check("FullSimplify(x*Sqrt(2 - 4*3^(1/3) + 2*3^(2/3)))", //
        "Sqrt(2)*(-1+3^(1/3))*x");
    // no such field element: unchanged
    check("Simplify(Sqrt(1 + 2^(1/3)))", //
        "Sqrt(1+2^(1/3))");
    // cube roots aren't denested, although 8+6*3^(1/3)+6*3^(2/3) is 2*(1+3^(1/3))^3
    check("Simplify((8 + 6*3^(1/3) + 6*3^(2/3))^(1/3))", //
        "(8+6*3^(1/3)+6*3^(2/3))^(1/3)");
    check("FullSimplify((8 + 6*3^(1/3) + 6*3^(2/3))^(1/3))", //
        "(8+6*3^(1/3)+6*3^(2/3))^(1/3)");
  }
}
