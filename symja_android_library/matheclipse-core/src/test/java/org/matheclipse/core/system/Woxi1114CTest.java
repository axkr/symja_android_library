package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Strings, boxes and notebook functions of the Woxi round 2026-11-14 (area C). */
public class Woxi1114CTest extends ExprEvaluatorTestCase {

  @Test
  public void testStringEndsQ() {
    check("StringEndsQ(\"mathematics\", \"ics\")", //
        "True");
    check("StringEndsQ(\"mathematics\", \"mat\")", //
        "False");
    check("StringEndsQ({\"walking\", \"walked\", \"talk\"}, \"ing\" | \"ed\")", //
        "{True,True,False}");
    check("StringEndsQ(\"ABC\", \"bc\", IgnoreCase -> True)", //
        "True");
    check("StringEndsQ(\"ics\")[\"physics\"]", //
        "True");
  }

  @Test
  public void testStringReverseNoString() {
    check("StringReverse(f(x))", //
        "StringReverse(f(x))");
  }

  @Test
  public void testReleaseHoldHead() {
    check("ReleaseHold(Hold(g)[Hold(2 + 3)])", //
        "g(5)");
    check("ReleaseHold(HoldForm(Times)[2, 5])", //
        "10");
  }

  @Test
  public void testElementListIsAlternatives() {
    check("Element({x, 3, y, z}, Integers)", //
        "(x|y|z)∈Integers");
    check("Element({p, q}, Reals)", //
        "(p|q)∈Reals");
    check("Element({x, 1/2}, Integers)", //
        "False");
    check("Element({x, 4}, Integers)", //
        "x∈Integers");
  }

  @Test
  public void testEditDistanceLists() {
    check("EditDistance({a, b, c, d}, {a, c, d, e})", //
        "2");
    check("EditDistance({\"A\", \"b\"}, {\"a\", \"B\"}, IgnoreCase -> True)", //
        "0");
    check("EditDistance({}, {1, 2})", //
        "2");
  }

  @Test
  public void testMakeExpression() {
    check("MakeExpression(RowBox({\"g\", \"[\", \"y\", \"]\"}), StandardForm)", //
        "HoldComplete(g(y))");
    check("MakeExpression(RowBox({\"2\", \"+\", \"3\"}))", //
        "HoldComplete(2+3)");
    check("MakeExpression(\"qq\", StandardForm)", //
        "HoldComplete(qq)");
  }

  @Test
  public void testAlphabeticSort() {
    check("AlphabeticSort({\"cat\", \"Bat\", \"apple\", \"Apple\"})", //
        "{apple,Apple,Bat,cat}");
    check("AlphabeticSort({\"zorro\", \"ñandú\", \"nadar\"}, \"Spanish\")", //
        "{nadar,ñandú,zorro}");
    check("AlphabeticSort({\"zebra\", \"öra\", \"ärta\", \"åsna\"}, \"Swedish\")", //
        "{zebra,åsna,ärta,öra}");
  }

  @Test
  public void testLongestOrderedSequence() {
    check("LongestOrderedSequence({4, 1, 5, 2, 6, 3, 7})", //
        "{1,2,3,7}");
    check("LongestOrderedSequence({5, 1, 4, 2, 3}, Greater)", //
        "{5,4,3}");
    check("LongestOrderedSequence(\"zabxcd\")", //
        "abcd");
    check("LongestOrderedSequence(\"ZaBc\", IgnoreCase -> True)", //
        "abc");
    check("LongestOrderedSequence({})", //
        "{}");
  }

  @Test
  public void testPalindromeQLanguage() {
    check("PalindromeQ(\"aña\", IgnoreDiacritics -> True, Language -> \"Spanish\")", //
        "True");
    check("PalindromeQ(\"añá\", IgnoreDiacritics -> True)", //
        "True");
    check("PalindromeQ(\"añna\", IgnoreDiacritics -> True, Language -> \"Spanish\")", //
        "False");
  }

  @Test
  public void testLetterCountsExtraLetters() {
    check("LetterCounts(\"ab-12-ba\", 1, {\"1\", \"2\"})", //
        "<|b->2,a->2,2->1,1->1|>");
    check("LetterCounts(\"X9-Y9\", 2, {\"9\"})", //
        "<|X9->1,Y9->1|>");
  }

  @Test
  public void testTemplateSlotArguments() {
    check("TemplateApply(TemplateSlot(1), <|\"x\" -> 7|>)", //
        "<|x->7|>");
    check(
        "TemplateApply(TemplateSlot(\"city\"), {<|\"x\" -> 1|>, <|\"city\" -> \"Rome\"|>, <|\"city\" -> \"Oslo\"|>})", //
        "Rome");
  }

  @Test
  public void testNearestStrings() {
    check("Nearest({\"cart\", \"card\", \"dart\", \"bird\"}, \"carp\")", //
        "{cart,card}");
    check("Nearest({\"cart\", \"card\", \"dart\", \"bird\"}, \"carp\", 3)", //
        "{cart,card,dart}");
  }

  @Test
  public void testFunctionInterpolation() {
    check("f2 = FunctionInterpolation(Cos(t), {t, 0, 2}); {f2(1.0), f2(0.5)}", //
        "{0.540302,0.877583}");
    // the variable is localized as in Block, and a definition for numbers only is used
    check("t = 7; g2(v_?NumericQ) := v^3; f3 = FunctionInterpolation(g2(t), {t, 0, 1}); {f3(0.5), t}", //
        "{0.125,7}");
  }

  @Test
  public void testTuringMachineStartOffset() {
    check("TuringMachine({2506, 2, 2}, {{1, 1, 3}, {0, 1, 0}}, 2)", //
        "{{{1,1,3},{0,1,0}},{{2,2,4},{1,1,0}},{{1,3,5},{1,0,0}}}");
  }

  @Test
  public void testBoxesOfNegativeProducts() {
    check("ToBoxes(-y*z) // InputForm", //
        "RowBox({RowBox({\"-\",\"y\"}),\" \",\"z\"})");
    check("ToBoxes(-3*y*z) // InputForm", //
        "RowBox({RowBox({\"-\",\"3\"}),\" \",\"y\",\" \",\"z\"})");
    check("ToBoxes(-1.5*y) // InputForm", //
        "RowBox({RowBox({\"-\",\"1.5`\"}),\" \",\"y\"})");
    check("ToBoxes(w - y*z) // InputForm", //
        "RowBox({\"w\",\"-\",RowBox({\"y\",\" \",\"z\"})})");
    check("ToBoxes(-y*z - w*v) // InputForm", //
        "RowBox({RowBox({RowBox({\"-\",\"v\"}),\" \",\"w\"}),\"-\",RowBox({\"y\",\" \",\"z\"})})");
    check("ToBoxes(-7) // InputForm", //
        "RowBox({\"-\",\"7\"})");
    check("ToBoxes((-3)^t) // InputForm", //
        "SuperscriptBox(RowBox({\"(\",RowBox({\"-\",\"3\"}),\")\"}),\"t\")");
    // with a factor under the line the minus stays in front of the fraction
    check("ToBoxes(-2*y/z) // InputForm", //
        "RowBox({\"-\",FractionBox(RowBox({\"2\",\" \",\"y\"}),\"z\")})");
    check("ToBoxes(w + (-3 + 2*I)*t) === ToBoxes(w - (3 - 2*I)*t)", //
        "True");
  }

  @Test
  public void testBoxesOfArgumentsAndLogic() {
    check("ToBoxes(Sin(t)) // InputForm", //
        "RowBox({\"Sin\",\"[\",\"t\",\"]\"})");
    check("ToBoxes(g(-y*z)) // InputForm", //
        "RowBox({\"g\",\"[\",RowBox({RowBox({\"-\",\"y\"}),\" \",\"z\"}),\"]\"})");
    check("ToBoxes(!p) // InputForm", //
        "RowBox({\"!\",\"p\"})");
    check("ToBoxes(!(p || q)) // InputForm", //
        "RowBox({\"!\",RowBox({\"(\",RowBox({\"p\",\"||\",\"q\"}),\")\"})})");
    check("ToBoxes(Not(u + v)) // InputForm", //
        "RowBox({\"!\",RowBox({\"u\",\"+\",\"v\"})})");
    check("ToBoxes((p || q) && r) // InputForm", //
        "RowBox({RowBox({\"(\",RowBox({\"p\",\"||\",\"q\"}),\")\"}),\"&&\",\"r\"})");
    check("ToBoxes(p && q || r) // InputForm", //
        "RowBox({RowBox({\"(\",RowBox({\"p\",\"&&\",\"q\"}),\")\"}),\"||\",\"r\"})");
    check("ToBoxes(TraditionalForm(p || Xor(q, r))) // InputForm", //
        "TagBox(FormBox(RowBox({\"p\",\"∨\",RowBox({\"(\",RowBox({\"q\",\"⊻\",\"r\"}),\")\"})}),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testBioSequenceStringsWithoutModule() {
    // without the bio module a sequence stays BioSequence("type", "letters")
    check("StringLength(BioSequence(\"DNA\", \"ACGT\"))", //
        "4");
    check("StringCount(BioSequence(\"DNA\", \"ACGTAC\"), \"AN\")", //
        "2");
    check("StringCount(BioSequence(\"DNA\", \"ACGTAC\"), Verbatim(\"AN\"))", //
        "0");
    check("StringTake(BioSequence(\"DNA\", \"ACGTAC\"), {2, 3})", //
        "BioSequence(DNA,CG)");
  }
}
