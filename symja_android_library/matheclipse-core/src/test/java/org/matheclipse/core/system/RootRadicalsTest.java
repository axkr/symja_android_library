package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** ToRadicals of quartics, NumericQ of Root objects and CountRoots on a complex rectangle. */
public class RootRadicalsTest extends ExprEvaluatorTestCase {

  @Test
  public void testToRadicalsQuarticWMA() {
    // WMA: cyclotomic, biquadratic and palindromic quartics in nested square roots
    check("ToRadicals(Root(1+#^4&,2))", //
        "(-1)^(3/4)");
    check("ToRadicals(Root(#^4-10*#^2+1&,4))", //
        "Sqrt(5+2*Sqrt(6))");
    check("ToRadicals(Root(#^4+#^3+#^2+#+1&,1))", //
        "-(-1)^(1/5)");
    check("ToRadicals(Root(1+2*#-2*#^2+2*#^3+#^4&,4))", //
        "1/2*(-1+Sqrt(5)+I*Sqrt(2*(-1+Sqrt(5))))");
  }

  @Test
  public void testToRadicalsNamesTheRoot() {
    // Ferrari's formula for #^4+1 builds a radicand which is exactly -2, on the branch cut of Sqrt:
    // the expression was the conjugate root. Every radical form must name its own root.
    check("ps = {1+#^4&, #^4-10*#^2+1&, #^4+#^3+#^2+#+1&, 1+2*#-2*#^2+2*#^3+#^4&, "
        + "#^4-2*#^3+3*#+7&, #^4+4*#+2&, #^4-2&, #^4-#^3+#^2-#+1&, #^4-#^2+1&, "
        + "#^4+3*#^3-5*#^2-3*#+1&, 2*#^4+3*#^2+5&, #^3-2*#+7&, #^3-3*#+1&}; "
        + "Table(Max(Table(Abs(N(Root(p,k),30)-N(ToRadicals(Root(p,k)),30)), "
        + "{k, Exponent(p(x),x)})) < 10^-20, {p, ps})", //
        "{True,True,True,True,True,True,True,True,True,True,True,True,True}");
  }

  @Test
  public void testNumericQRootWMA() {
    check("{NumericQ(Root(1+#^4&,2)), NumericQ(Root(#^5-#+1&,1)), NumericQ(Root(#^2+a&,1)), "
        + "NumericQ(Root({#^2-2&,#^3+1&},{1,1}))}", //
        "{True,True,False,False}");
  }

  @Test
  public void testRootOfNamedParameterFunction() {
    // Function(y, poly) and Function({y}, poly) name the same roots as the slot function; the
    // parameter list {y} was read as the polynomial and the root came out as {}
    check("{Root(Function({y}, y^5-y+1), 1), Root(Function(y, y^2-2), 2), "
        + "Root(Function({y}, y^2-2), 1)}", //
        "{Root(1-#1+#1^5&,1,0),Sqrt(2),-Sqrt(2)}");
    check("{NumericQ(Root(Function({y}, y^5-y+1), 1)), NumericQ(Root(Function(y, y^5-y+1), 1)), "
        + "ToRadicals(Root(Function({y}, y^4-10*y^2+1), 4))}", //
        "{True,True,Sqrt(5+2*Sqrt(6))}");
    // two parameters don't name a polynomial in one variable
    check("Root(Function({y, z}, y^2-z), 1)", //
        "Root(Function({y,z},y^2-z),1)");
  }

  @Test
  public void testCountRootsRectangleWMA() {
    // the closed rectangle: roots on the edges and corners count, with multiplicity
    check("{CountRoots(x^4-1, {x,-1-I,1+I}), CountRoots(x^4-1, {x,-1/2-I/2,2+2*I}), "
        + "CountRoots(x^2+1, {x,-2*I,2*I}), CountRoots((x^2+1)^2*(x-3), {x,-I,4+I}), "
        + "CountRoots(x^3-2, {x,-2-2*I,0})}", //
        "{4,2,2,5,1}");
    // x^5-x+1 has one root in the first quadrant, 0.7649+0.3525*I
    check("{CountRoots(x^5-x+1, {x,-2-2*I,2+2*I}), CountRoots(x^5-x+1, {x,0,2+2*I})}", //
        "{5,1}");
    // a point, segments, and a triple root
    check("{CountRoots(x^2-2*x+2, {x,1+I,1+I}), CountRoots(x^2+1, {x,-1+I,1+I}), "
        + "CountRoots(x^4-1, {x,-I,I}), CountRoots(x^4-1, {x,1,1+I}), CountRoots(x^3, {x,-I,I})}", //
        "{1,1,2,1,3}");
  }
}
