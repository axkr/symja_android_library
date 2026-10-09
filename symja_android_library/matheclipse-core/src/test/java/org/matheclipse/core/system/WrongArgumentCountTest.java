package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/**
 * A built-in function which was called with a wrong number of arguments stays unevaluated and can
 * be nested in another expression. The functions which look into such an argument have to leave it
 * alone instead of reading an argument which isn't there.
 */
public class WrongArgumentCountTest extends ExprEvaluatorTestCase {

  @Test
  public void testFirstLastWithoutArguments() {
    IAST empty = F.headAST0(S.f);
    assertThrows(IndexOutOfBoundsException.class, () -> empty.first());
    assertThrows(IndexOutOfBoundsException.class, () -> empty.last());
    assertThrows(IndexOutOfBoundsException.class, () -> empty.base());
    assertThrows(IndexOutOfBoundsException.class, () -> F.CEmptyList.first());
    assertThrows(IndexOutOfBoundsException.class, () -> F.CEmptyList.last());
    assertEquals(S.a, F.unaryAST1(S.f, S.a).first());
    assertEquals(S.a, F.unaryAST1(S.f, S.a).last());
    // an atom has no arguments at all
    assertTrue(S.a.first().isNIL());
    assertTrue(S.a.last().isNIL());
    // F.NIL is no expression at all
    assertTrue(F.NIL.first().isNIL());
    assertTrue(F.NIL.last().isNIL());

    check("First({})", //
        "First({})");
    check("Last({})", //
        "Last({})");
    check("First({}, x)", //
        "x");
    check("Last(<||>, x)", //
        "x");
    check("First(f())", //
        "First(f())");
  }

  @Test
  public void testFunctionExpand() {
    check("FunctionExpand(HankelH1())", //
        "HankelH1()");
    check("FunctionExpand(HankelH2(n))", //
        "HankelH2(n)");
    check("FunctionExpand(1+Pochhammer(a))", //
        "1+Pochhammer(a)");
    check("FunctionExpand(SphericalBesselJ())", //
        "SphericalBesselJ()");
    check("FunctionExpand(SphericalBesselY(n))", //
        "SphericalBesselY(n)");
    check("FunctionExpand(KelvinBer())", //
        "KelvinBer()");
    check("FunctionExpand(WeberE(a))", //
        "WeberE(a)");
    check("FunctionExpand(HermiteH())", //
        "HermiteH()");
    check("FunctionExpand(HermiteH(-2))", //
        "HermiteH(-2)");
    check("FunctionExpand(Subfactorial())", //
        "Subfactorial()");
    check("FunctionExpand(EllipticExp(u))", //
        "EllipticExp(u)");
    check("FunctionExpand(EllipticLog())", //
        "EllipticLog()");
    check("FunctionExpand(WeierstrassP(u))", //
        "WeierstrassP(u)");
    check("FunctionExpand(WeierstrassPPrime(u))", //
        "WeierstrassPPrime(u)");
    // too many arguments
    check("FunctionExpand(Pochhammer(a,b,c))", //
        "Pochhammer(a,b,c)");
    check("FunctionExpand(Pochhammer(a,b))", //
        "Gamma(a+b)/Gamma(a)");
  }

  @Test
  public void testNestedCalls() {
    check("Median(QuantityDistribution())", //
        "Median(QuantityDistribution())");
    check("Variance(QuantityDistribution(NormalDistribution()))", //
        "Variance(QuantityDistribution(NormalDistribution(0,1)))");
    check("Element(x, Vectors())", //
        "x∈Vectors()");
    check("Element(x, Matrices())", //
        "x∈Matrices()");
    check("Element(x, Arrays())", //
        "x∈Arrays()");
    check("Limit(Interval(), x->0)", //
        "Interval()");
    check("Limit(Interval({1,2}), x->0)", //
        "Indeterminate");
    check("<|a->1, Splice()|>", //
        "Association(a->1,Splice())");
    check("<|a->1, Splice({b->2})|>", //
        "<|a->1,b->2|>");
    check("Solve(ForAll(), x)", //
        "Solve(ForAll(),x)");
    check("FindMinimum(x^2, {{}})", //
        "FindMinimum(x^2,{{}})");
    check("FunctionDiscontinuities(Piecewise({{x}}), x)", //
        "False");
    check("FunctionDiscontinuities(Piecewise({{}}, x), x)", //
        "False");
    check("RegionMember(Disk({}), {1,2})", //
        "RegionMember(Disk({}),{1,2})");
    check("ListContourPlot({{1,2},{3,4}}, DataRange -> {{}, {}})", //
        "ListContourPlot({{1,2},{3,4}},DataRange->{{},{}})");
    check("NumberLinePlot(IntervalData())", //
        "NumberLinePlot(IntervalData())");
    check("TeXForm(a+Parenthesis())", //
        "a + \\text{Parenthesis}()");
  }

  @Test
  public void testNamedSlotWithoutArguments() {
    check("(#name &)[]", //
        "#name");
    check("(f(#a) &)[]", //
        "f(#a)");
  }

  @Test
  public void testPatternConstructs() {
    // the parser builds the pattern objects on input
    check("Repeated()", //
        "Repeated()");
    check("f(Repeated())", //
        "f(Repeated())");
    check("RepeatedNull()", //
        "RepeatedNull()");
    check("x:Repeated()", //
        "(x:Repeated())");
    check("x:Blank(a,b)", //
        "(x:Blank(a,b))");
    check("MatchQ(1, x:Blank(a,b))", //
        "False");
    check("MatchQ({a,a}, {Repeated()})", //
        "False");
  }

  /** The result of <code>input</code>, followed by the messages which were printed for it. */
  private String evalWithMessages(String input) {
    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.PrintStream messages = new java.io.PrintStream(bytes);
    java.io.PrintStream previous = evaluator.getEvalEngine().getErrorPrintStream();
    evaluator.getEvalEngine().setErrorPrintStream(messages);
    try {
      String result = evaluator.eval(input).toString();
      messages.flush();
      return (result + " | " + bytes.toString().trim()).trim();
    } finally {
      evaluator.getEvalEngine().setErrorPrintStream(previous);
    }
  }

  @Test
  public void testArgumentCountMessages() {
    assertEquals(
        "Condition() | Condition: Condition called with 0 arguments; 2 arguments are expected.",
        evalWithMessages("Condition()"));
    assertEquals(
        "Condition(a) | Condition: Condition called with 1 argument; 2 arguments are expected.",
        evalWithMessages("Condition(a)"));
    assertEquals(
        "Verbatim() | Verbatim: Verbatim called with 0 arguments; 1 argument is expected.",
        evalWithMessages("Verbatim()"));
    assertEquals(
        "Verbatim(a,b) | Verbatim: Verbatim called with 2 arguments; 1 argument is expected.",
        evalWithMessages("Verbatim(a,b)"));
    // the valid forms print nothing
    assertEquals("Verbatim(_) |", evalWithMessages("Verbatim(_)"));
    assertEquals("t |", evalWithMessages("_ /. Verbatim(_)->t"));
    assertEquals("{1,cnd(-1)} |", evalWithMessages("cnd(x_) := x /; x > 0; {cnd(1), cnd(-1)}"));
    // an assignment to a protected head is refused and defines nothing
    assertEquals("1 | Set: Symbol HoldPattern is Protected.",
        evalWithMessages("HoldPattern() = 1"));
    assertEquals("{} |", evalWithMessages("DownValues(HoldPattern)"));
  }

  @Test
  public void testDefinitions() {
    check("HoldPattern() = 1", //
        "1");
    check("PatternTest() = 3", //
        "3");
    check("DownValues() = {}", //
        "DownValues()={}");
    check("emptyBody(x_) := Module({y}, CompoundExpression())", //
        "");
    check("emptyBody(1)", //
        "");
  }

  @Test
  public void testStringPatterns() {
    check("StringMatchQ(\"a\", Repeated())", //
        "StringMatchQ(a,Repeated())");
    check("StringMatchQ(\"a\", \"a\" ~~ Repeated())", //
        "StringMatchQ(a,a~~Repeated())");
    check("StringMatchQ(\"a\", Shortest())", //
        "StringMatchQ(a,Shortest())");
    check("StringMatchQ(\"a\", Except())", //
        "StringMatchQ(a,Except())");
    check("StringMatchQ(\"a\", CharacterRange(\"a\"))", //
        "StringMatchQ(a,CharacterRange(a))");
    // a named pattern around something which is no string pattern
    check("StringMatchQ(\"null\", x:f())", //
        "StringMatchQ(null,(x:f()))");
    check("StringCases(\"anullb\", x:f(1))", //
        "StringCases(anullb,(x:f(1)))");

    // a choice between nothing matches nothing
    check("StringMatchQ(\"\", Alternatives())", //
        "False");
    check("StringCases(\"abc\", Alternatives())", //
        "{}");
    check("StringCases(\"abc\", {})", //
        "{}");
    check("StringReplace(\"abc\", Alternatives() -> \"x\")", //
        "abc");
    check("StringMatchQ(\"\", StringExpression())", //
        "True");

    // Except(c, p) matches p unless it matches c
    check("StringMatchQ(\"b\", Except(\"a\", LetterCharacter))", //
        "True");
    check("StringMatchQ(\"a\", Except(\"a\", LetterCharacter))", //
        "False");
    check("StringCases(\"a1b2\", Except(\"a\", LetterCharacter))", //
        "{b}");
    check("StringCases(\"a1b2\", Except(LetterCharacter))", //
        "{1,2}");
    // Except of a choice
    check("StringMatchQ(\"|\", Except({\"a\",\"b\"}))", //
        "True");
    check("StringMatchQ(\"a\", Except({\"a\",\"b\"}))", //
        "False");
    check("StringCases(\"abcab\", Except(\"a\")..)", //
        "{bc,b}");
    check("StringCases(\"a.b]c\", Except(\"]\")..)", //
        "{a.b,c}");
    check("StringCases(\"a^b\", Except(\"^\")..)", //
        "{a,b}");
  }

  @Test
  public void testRegionWrapper() {
    // Region(reg) is a display wrapper around the region
    check("ConvexRegionQ(Region(Disk()))", //
        "True");
    check("RegionBoundary(Region(Disk()))", //
        "Circle({0,0})");
    check("RegionBoundary(Region())", //
        "RegionBoundary(Region())");
  }

  @Test
  public void testFontSizeDirective() {
    check("MathMLForm(Style(x, FontSize(12)))===MathMLForm(Style(x, FontSize->12))", //
        "True");
    check("StringContainsQ(ToString(MathMLForm(Style(x, FontSize(12)))), \"mathsize\")", //
        "True");
  }
}
