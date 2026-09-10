package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class BoxesTest extends ExprEvaluatorTestCase {

  @Test
  public void testMakeBoxes001() {
    check("MakeBoxes[Beta[a, b], StandardForm] // InputForm", //
        "RowBox({\"Beta\",\"[\",RowBox({\"a\",\",\",\"b\"}),\"]\"})");
  }

  @Test
  public void testMakeBoxesList() {
    check("MakeBoxes[{a,b}]// InputForm", //
        "RowBox({\"{\",RowBox({\"a\",\",\",\"b\"}),\"}\"})");
  }

  @Test
  public void testMakeBoxesAssociation() {
    check("MakeBoxes[<|a->b, c:>d|>] // InputForm", //
        "RowBox({\"Association\",\"[\",RowBox({RowBox({\"a\",\"\\[Rule]\",\"b\"}),\",\",RowBox({\"c\",\"\\[RuleDelayed]\",\"d\"})}),\"]\"})");
  }

  @Test
  public void testArithmeticIsWrittenAsItIsRead() {
    // a sum, a product, a power and a root are what a printed expression is mostly made of, and
    // writing them as Plus[Times[…], Power[…]] is what a notebook cell used to show
    check("MakeBoxes[1 + 1, StandardForm] // InputForm", //
        "RowBox({\"1\",\"+\",\"1\"})");
    check("MakeBoxes[a - b, StandardForm] // InputForm", //
        "RowBox({\"a\",\"-\",\"b\"})");
    check("MakeBoxes[2*x*y, StandardForm] // InputForm", //
        "RowBox({\"2\",\" \",\"x\",\" \",\"y\"})");
    check("MakeBoxes[x^2, StandardForm] // InputForm", //
        "SuperscriptBox(\"x\",\"2\")");
    check("MakeBoxes[Sqrt[x], StandardForm] // InputForm", //
        "SqrtBox(\"x\")");
    // a factor of negative power moves under the line, and a lone one needs no brackets there
    check("MakeBoxes[1/x, StandardForm] // InputForm", //
        "FractionBox(\"1\",\"x\")");
    check("MakeBoxes[x^-2, StandardForm] // InputForm", //
        "FractionBox(\"1\",SuperscriptBox(\"x\",\"2\"))");
    check("MakeBoxes[(a + b)/c, StandardForm] // InputForm", //
        "FractionBox(RowBox({\"a\",\"+\",\"b\"}),\"c\")");
    // ...but a factor beside another one does
    check("MakeBoxes[(a + b)*c, StandardForm] // InputForm", //
        "RowBox({RowBox({\"(\",RowBox({\"a\",\"+\",\"b\"}),\")\"}),\" \",\"c\"})");
  }

  @Test
  public void testEveryInfixOperatorIsWrittenInfix() {
    // the precedences and spellings are the parser's own, so an operator the language reads is
    // one it writes - and the brackets follow from the precedence
    check("MakeBoxes[a == b, StandardForm] // InputForm", //
        "RowBox({\"a\",\"==\",\"b\"})");
    check("MakeBoxes[a && b || c, StandardForm] // InputForm", //
        "RowBox({RowBox({\"a\",\"&&\",\"b\"}),\"||\",\"c\"})");
    check("MakeBoxes[a | b, StandardForm] // InputForm", //
        "RowBox({\"a\",\"|\",\"b\"})");
  }

  @Test
  public void testAHeadSaysHowItIsShown() {
    // MakeBoxes is HoldAllComplete, so the evaluator's own up-value probe passes it by: the rule
    // has to be looked for by MakeBoxes itself. Writing one is how a package shows its objects -
    // Graphics /: MakeBoxes[g_Graphics, StandardForm] := ViewBox[…] is how the WLJS notebook hands
    // a picture to the browser instead of printing its primitives
    check("Thing /: MakeBoxes[Thing[n_], StandardForm] := RowBox[{\"<\", ToString[n], \">\"}]", //
        "");
    check("MakeBoxes[Thing[7], StandardForm] // InputForm", //
        "RowBox({\"<\",\"7\",\">\"})");
    // and a head buried in an expression is asked too
    check("ToBoxes[{Thing[7], 1}] // InputForm", //
        "RowBox({\"{\",RowBox({RowBox({\"<\",\"7\",\">\"}),\",\",\"1\"}),\"}\"})");
  }

  @Test
  public void testByteCountMeasuresTheStoredExpression() {
    // what "how much" means is the implementation's own business, so what is asserted is that it
    // answers a number at all, and that a bigger expression is a bigger number. WLJS asks it to
    // decide whether a picture travels inline or as a front end object, and an unevaluated
    // ByteCount left that If undecided and the picture unshown
    check("Head[ByteCount[{1, 2, 3}]]", //
        "Integer");
    check("ByteCount[Range[100]] > ByteCount[Range[10]] > ByteCount[1] > 0", //
        "True");
  }

  @Test
  public void testToBoxes001() {
    check("Map[ToBoxes, {123, 1/23, 1.23, 1 + 23 I}] // InputForm", //
        "{\"123\",FractionBox(\"1\",\"23\"),\"1.23\",RowBox({\"1\",\"+\",RowBox({\"23\",\" \",\"\\[ImaginaryI]\"})})}");
  }
}
