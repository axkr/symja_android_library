package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>Precedence</code> reads the parser's operator table, with a few reported exceptions. */
public class PrecedenceTest extends ExprEvaluatorTestCase {

  @Test
  public void testOperators() {
    check("Precedence /@ {CompoundExpression, SetDelayed, Rule, Alternatives, Or, Equal, "
        + "SubsetEqual, UnionPlus, SquareIntersection, Subtract, Star, CenterDot, Vee, Dot, "
        + "NonCommutativeMultiply, StringJoin, Factorial, PatternTest, Map, Implies}", //
        "{10.0,40.0,120.0,160.0,215.0,290.0,250.0,300.0,305.0,310.0,390.0,410.0,430.0,490.0,"
            + "510.0,600.0,610.0,680.0,620.0,200.0}");
    // Apply is spelled @@ (620) and @ (621): the lower one
    check("Precedence(Apply)", //
        "620.0");
    // operators read only from private-use characters
    check("Precedence /@ {ConjugateTranspose, NotVerticalBar, Xnor}", //
        "{670.0,280.0,220.0}");
  }

  @Test
  public void testReportedDiffersFromParser() {
    // the logical operators share one level; Sum and Limit sit with the big operators
    check("Precedence /@ {And, Nand, Xor, Sum, Limit, Composition, RightComposition}", //
        "{215.0,215.0,215.0,320.0,320.0,650.0,648.0}");
    // heads with a precedence but without an operator row
    check("Precedence /@ {Optional, Blank, Postfix, Prefix, D, TensorProduct}", //
        "{140.0,730.0,70.0,640.0,550.0,495.0}");
  }

  @Test
  public void testNoOperator() {
    // a symbol without an operator binds like a function call
    check("Precedence /@ {Sin, myOwnSymbol, Graph}", //
        "{670.0,670.0,670.0}");
    // anything but a symbol
    check("Precedence /@ {42, 1/3, \"str\", g(x), x^2}", //
        "{1000.0,1000.0,1000.0,1000.0,1000.0}");
    check("Precedence(Plus, Times)", //
        "Precedence(Plus,Times)");
  }
}
