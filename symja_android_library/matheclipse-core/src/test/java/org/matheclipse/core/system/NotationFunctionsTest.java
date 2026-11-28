package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the display wrappers OverBar, ColumnForm, PercentForm, Shallow and the other notation heads. */
public class NotationFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testOverBar() {
    check("OverBar(x)", //
        "OverBar(x)");
    check("OverBar(p) = 5; OverBar(p)", //
        "5");
  }

  @Test
  public void testToBoxes() {
    check("ToBoxes(OverBar(x))", //
        "OverscriptBox(x,_)");
    check("ToBoxes(UnderBar(y))", //
        "UnderscriptBox(y,_)");
    check("ToBoxes(OverVector(v))", //
        "OverscriptBox(v,⇀)");
    check("ToBoxes(SubPlus(a + b))", //
        "SubscriptBox(RowBox({(,RowBox({a,+,b}),)}),+)");
    check("ToBoxes(SuperMinus(q))", //
        "SuperscriptBox(q,-)");
    check("ToBoxes(Ket(psi))", //
        "RowBox({Ket,[,psi,]})");
  }

  @Test
  public void testTeXForm() {
    check("TeXForm(OverHat(x) + OverTilde(y) + OverDot(z))", //
        "\\dot{z} + \\hat{x} + \\tilde{y}");
    check("TeXForm(SubStar(k) + SuperPlus(m))", //
        "{k}_* + {m}^+");
    check("TeXForm(SuperStar(w))", //
        "{w}^*");
    check("TeXForm(Ket(psi))", //
        "\\text{Ket}(\\psi)");
  }

  @Test
  public void testAngleBracket() {
    check("AngleBracket(a, b, c)", //
        "〈a,b,c〉");
    check("AngleBracket(a, b) // InputForm", //
        "AngleBracket(a,b)");
    check("AngleBracket(1 + 1, b) // TeXForm", //
        "\\langle 2,b\\rangle");
  }

  @Test
  public void testBra() {
    check("Bra(phi)", //
        "Bra(phi)");
  }

  @Test
  public void testKet() {
    check("Ket(psi)", //
        "Ket(psi)");
  }

  @Test
  public void testColumnForm() {
    check("ColumnForm({a, bbb, cc})", //
        "a\nbbb\ncc");
    check("ColumnForm({a, bbb, cc}, Right)", //
        "  a\nbbb\n cc");
    check("ColumnForm({1, 22, 333}, Center)", //
        " 1\n22\n333");
    check("ColumnForm({1 + 1, x^2})", //
        "2\nx^2");
  }

  @Test
  public void testTextGrid() {
    check("TextGrid({{x, 1}, {yyy, 22}, {z, 333}})", //
        "TextGrid({{x,1},{yyy,22},{z,333}})");
  }

  @Test
  public void testPercentForm() {
    check("PercentForm(0.256)", //
        "25.6%");
    check("PercentForm(1/3)", //
        "1/3");
    check("PercentForm(2)", //
        "200%");
    check("PercentForm(0.123456, 2)", //
        "12%");
    check("PercentForm(r)", //
        "r");
  }

  @Test
  public void testShallow() {
    check("Shallow(Nest(f, x, 10))", //
        "f(f(f(<<1>>)))");
    check("Shallow(Range(25))", //
        "{1,2,3,4,5,6,7,8,9,10,<<15>>}");
    check("Shallow(Nest(g, y, 6), 2)", //
        "g(<<1>>)");
    check("Shallow({{1, {2, {3, {4}}}}}, {2, 1})", //
        "{<<1>>}");
    check("Shallow(h(a, b))", //
        "h(a,b)");
    check("Shallow(OverBar(1 + 1))", //
        "OverBar(2)");
  }

  @Test
  public void testHyperlink() {
    check("Hyperlink(\"Symja\", \"https://example.org\")", //
        "Hyperlink(Symja,https://example.org)");
    check("Hyperlink(\"https://example.org\")", //
        "Hyperlink(https://example.org)");
  }

  @Test
  public void testFrameBox() {
    check("FrameBox(\"x\")", //
        "FrameBox(x)");
  }

  @Test
  public void testAdjustmentBox() {
    check("AdjustmentBox(\"x\", 1)", //
        "AdjustmentBox(x,1)");
  }

  @Test
  public void testDollarDefaultFont() {
    check("$DefaultFont", //
        "{Courier,10.0}");
  }

  @Test
  public void testRowLines() {
    check("Grid({{1, 2}, {3, 4}}, RowLines -> True) // TeXForm", //
        "\\begin{array}{cc}\n 1 & 2 \\\\\n\\hline\n 3 & 4 \\\\\n\\end{array}");
    check("Grid({{1, 2}, {3, 4}}) // TeXForm", //
        "\\begin{array}{cc}\n 1 & 2 \\\\\n 3 & 4 \\\\\n\\end{array}");
  }

  @Test
  public void testGreaterSlantEqual() {
    check("GreaterSlantEqual(3, 2)", //
        "3⩾2");
    check("GreaterSlantEqual(x, 2)", //
        "x⩾2");
  }

  @Test
  public void testLessSlantEqual() {
    check("LessSlantEqual(1/2, 0.4, -1)", //
        "1/2⩽0.4⩽-1");
  }

  @Test
  public void testStringSkeleton() {
    check("StringSkeleton(5 + 7)", //
        "<<12>>");
  }

  @Test
  public void testDisplayWrapperAttributes() {
    check("Attributes({PercentForm, TextGrid, Hyperlink})", //
        "{{NHoldRest,Protected},{Protected,ReadProtected},{Protected,ReadProtected}}");
  }
}
