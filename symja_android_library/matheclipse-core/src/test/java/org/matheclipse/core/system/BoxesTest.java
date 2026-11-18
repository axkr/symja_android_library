package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class BoxesTest extends ExprEvaluatorTestCase {

  @Test
  public void testMakeBoxes001() {
    check("MakeBoxes[Beta[a, b], StandardForm] // InputForm", //
        "RowBox({\"Beta\",\"[\",RowBox({\"a\",\",\",\"b\"}),\"]\"})");
  }

  @Test
  public void testFactorialBoxes() {
    check("ToBoxes(k!) // InputForm", //
        "RowBox({\"k\",\"!\"})");
    check("ToBoxes(k!!) // InputForm", //
        "RowBox({\"k\",\"!!\"})");
    check("ToBoxes(HoldForm(11!)) // InputForm", //
        "TagBox(RowBox({\"11\",\"!\"}),HoldForm)");
    // an operand which is no atom or function call is put in parentheses
    check("ToBoxes(HoldForm((p - q)!)) // InputForm", //
        "TagBox(RowBox({RowBox({\"(\",RowBox({\"p\",\"-\",\"q\"}),\")\"}),\"!\"}),HoldForm)");
    check("ToBoxes(HoldForm((-5)!!)) // InputForm", //
        "TagBox(RowBox({RowBox({\"(\",RowBox({\"-\",\"5\"}),\")\"}),\"!!\"}),HoldForm)");
    check("ToBoxes(HoldForm((3!!)!)) // InputForm", //
        "TagBox(RowBox({RowBox({\"(\",RowBox({\"3\",\"!!\"}),\")\"}),\"!\"}),HoldForm)");
    check("ToBoxes(HoldForm(g(k)!)) // InputForm", //
        "TagBox(RowBox({RowBox({\"g\",\"[\",\"k\",\"]\"}),\"!\"}),HoldForm)");
    check("ToBoxes((k^3)!) // InputForm", //
        "RowBox({SuperscriptBox(\"k\",\"3\"),\"!\"})");
  }

  @Test
  public void testOperatorFormBoxes() {
    // the display wrappers Infix, Prefix and Postfix are rows of operands and operators in an
    // InterpretationBox, which reads back as the wrapper
    check("ToBoxes(Infix({p, q, r}, \"%\")) // InputForm", //
        "InterpretationBox(RowBox({\"p\",\"%\",\"q\",\"%\",\"r\"}),Infix({p,q,r},\"%\"),Editable->False)");
    // a list of operators is the list it is
    check("ToBoxes(Infix({p, q}, {\"<\", \">\"})) // InputForm", //
        "InterpretationBox(RowBox({\"p\",RowBox({\"{\",RowBox({\"\\\"<\\\"\",\",\",\"\\\">\\\"\"}),\"}\"}),\"q\"}),Infix({p,q},{\"<\",\">\"}),Editable->False)");
    // the operator ~g~ has a space on its outer sides
    check("ToBoxes(Infix(g(p, q))) // InputForm", //
        "InterpretationBox(RowBox({\"p\",\" \",\"~\",\"g\",\"~\",\" \",\"q\"}),Infix(g(p,q)),Editable->False)");
    check("ToBoxes(Infix({p}, \"%\")) // InputForm", //
        "InterpretationBox(\"p\",Infix({p},\"%\"),Editable->False)");
    check("ToBoxes(Prefix(g(p))) // InputForm", //
        "InterpretationBox(RowBox({\"g\",\"@\",\"p\"}),Prefix(g(p)),Editable->False)");
    check("ToBoxes(Postfix(g(p), \"'\")) // InputForm", //
        "InterpretationBox(RowBox({\"p\",\"'\"}),Postfix(g(p),\"'\"),Editable->False)");
    // an operand which is a sum is put in parentheses
    check("ToBoxes(Infix({p + q, r}, \"%\")) // InputForm", //
        "InterpretationBox(RowBox({RowBox({\"(\",RowBox({\"p\",\"+\",\"q\"}),\")\"}),\"%\",\"r\"}),Infix({p + q,r},\"%\"),Editable->False)");
    // a grouping as fourth argument: the wrapper is boxed as the function it is
    check("ToBoxes(Infix({p, q}, \"%\", 310, Left)) // InputForm", //
        "RowBox({\"Infix\",\"[\",RowBox({RowBox({\"{\",RowBox({\"p\",\",\",\"q\"}),\"}\"}),\",\",\"\\\"%\\\"\",\",\",\"310\",\",\",\"Left\"}),\"]\"})");
  }

  @Test
  public void testPartBoxes() {
    // x[[i]] is boxed with the double brackets, several indices as a row
    check("ToBoxes(Hold(w[[3]])) // InputForm", //
        "RowBox({\"Hold\",\"[\",RowBox({\"w\",\"\\[LeftDoubleBracket]\",\"3\",\"\\[RightDoubleBracket]\"}),\"]\"})");
    check("ToBoxes(Hold(w[[3, -2]])) // InputForm", //
        "RowBox({\"Hold\",\"[\",RowBox({\"w\",\"\\[LeftDoubleBracket]\",RowBox({\"3\",\",\",RowBox({\"-\",\"2\"})}),\"\\[RightDoubleBracket]\"}),\"]\"})");
    // an operator expression is put in parentheses, a function call is not
    check("ToBoxes(Hold((u + v)[[k]])) // InputForm", //
        "RowBox({\"Hold\",\"[\",RowBox({RowBox({\"(\",RowBox({\"u\",\"+\",\"v\"}),\")\"}),\"\\[LeftDoubleBracket]\",\"k\",\"\\[RightDoubleBracket]\"}),\"]\"})");
    check("ToBoxes(Hold(f(w)[[k]])) // InputForm", //
        "RowBox({\"Hold\",\"[\",RowBox({RowBox({\"f\",\"[\",\"w\",\"]\"}),\"\\[LeftDoubleBracket]\",\"k\",\"\\[RightDoubleBracket]\"}),\"]\"})");
    check("ToBoxes(Hold(w[[2]]!)) // InputForm", //
        "RowBox({\"Hold\",\"[\",RowBox({RowBox({\"w\",\"\\[LeftDoubleBracket]\",\"2\",\"\\[RightDoubleBracket]\"}),\"!\"}),\"]\"})");
  }

  @Test
  public void testSphericalHarmonicYTraditionalBoxes() {
    check("ToBoxes(TraditionalForm(SphericalHarmonicY(j, k, u, v))) // InputForm", //
        "TagBox(FormBox(RowBox({SubsuperscriptBox(\"Y\",\"j\",\"k\"),\"(\",RowBox({\"u\",\",\",\"v\"}),\")\"}),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testMakeBoxesList() {
    check("MakeBoxes[{a,b}]// InputForm", //
        "RowBox({\"{\",RowBox({\"a\",\",\",\"b\"}),\"}\"})");
  }

  @Test
  public void testMakeBoxesAssociation() {
    // written with <| |> and ->, as the outputs a WLJS notebook saves show an association
    check("MakeBoxes[<|a->b, c:>d|>] // InputForm", //
        "RowBox({\"<|\",RowBox({RowBox({\"a\",\"->\",\"b\"}),\",\",RowBox({\"c\",\":>\",\"d\"})}),\"|>\"})");
    check("MakeBoxes[<||>] // InputForm", //
        "RowBox({\"<|\",\"|>\"})");
  }

  @Test
  public void testStringBoxesKeepTheirQuotationMarks() {
    // the notebook showed {"123"} as {123} and an association of strings as Association[name->Alice]
    check("ToBoxes[\"Alice\"] === \"\\\"Alice\\\"\"", //
        "True");
    check("StringLength[ToBoxes[\"Alice\"]]", //
        "7");
    // a quotation mark, a backslash and a new line inside are escaped - compared with ===, because
    // InputForm does not escape the strings nested in an expression
    check("ToBoxes[\"a\\\"b\\\\c\\nd\"] === \"\\\"a\\\\\\\"b\\\\\\\\c\\\\nd\\\"\"", //
        "True");
    // the text the notebook puts into the cell is the boxes' strings joined
    check("StringJoin[Cases[ToBoxes[{<|\"name\" -> \"Alice\", \"age\" -> 30|>, {\"123\"}}], _String, Infinity]]", //
        "{<|\"name\"->\"Alice\",\"age\"->30|>,{\"123\"}}");
  }

  @Test
  public void testConstantsAreWrittenAsTheirGlyphs() {
    check("ToBoxes[4 Pi R^2] // InputForm", //
        "RowBox({\"4\",\" \",\"\\[Pi]\",\" \",SuperscriptBox(\"R\",\"2\")})");
    // E and I stay letters, as the notebook's saved outputs spell them
    check("Map[ToBoxes, {E, Infinity, -Infinity, ComplexInfinity}] // InputForm", //
        "{\"E\",\"\\[Infinity]\",RowBox({\"-\",\"\\[Infinity]\"}),\"ComplexInfinity\"}");
  }

  @Test
  public void testSignsCoefficientsAndImaginaryNumbers() {
    // -x was written (-1) x, I as 1 \[ImaginaryI], and (1/2) b instead of b/2
    check("Map[ToBoxes, {-x, I, -I/2, 1 - 2 I, I Sqrt[2], -1/2, a - b/2}] // InputForm", //
        "{RowBox({\"-\",\"x\"}),\"I\",RowBox({\"-\",RowBox({FractionBox(\"1\",\"2\"),\" \",\"I\"})}),"
            + "RowBox({\"1\",\"-\",RowBox({\"2\",\" \",\"I\"})}),RowBox({\"I\",\" \",SqrtBox(\"2\")}),"
            + "RowBox({\"-\",FractionBox(\"1\",\"2\")}),RowBox({\"a\",\"-\",FractionBox(\"b\",\"2\")})}");
    check("ToBoxes[(-b - Sqrt[b^2 - 4 a c])/(2 a)] // InputForm", //
        "FractionBox(RowBox({RowBox({\"-\",\"b\"}),\"-\",SqrtBox(RowBox({SuperscriptBox(\"b\",\"2\"),\"-\",RowBox({\"4\",\" \",\"a\",\" \",\"c\"})}))}),RowBox({\"2\",\" \",\"a\"}))");
  }

  @Test
  public void testEmptyListAndCallHaveNoEmptyRow() {
    // the empty row was joined by the notebook into StringJoin() and showed as {StringJoin[]}
    check("{ToBoxes[{}], ToBoxes[f[]]} // InputForm", //
        "{RowBox({\"{\",\"}\"}),RowBox({\"f\",\"[\",\"]\"})}");
  }

  @Test
  public void testMachineRealsKeepAllTheirDigits() {
    // a notebook showed 0.0159723 where the saved output has 0.8459659909775918`
    check("Map[ToBoxes, {0.8459659909775918, 1., -2.5, 123456., 1234567., 0.00001, 1.5*^-7}] // InputForm", //
        "{\"0.8459659909775918`\",\"1.`\",RowBox({\"-\",\"2.5`\"}),\"123456.`\",\"1.234567`*^6\",\"0.00001`\",\"1.5`*^-7\"}");
  }

  @Test
  public void testColorsTablesAndSeriesAreShapedBoxes() {
    // the notebook turns these box heads into a colour swatch, a grid and the terms of a series
    check("ToBoxes[RGBColor[0, 1, 1]] // InputForm", //
        "TemplateBox(<|\"color\"->RGBColor(0,1,1)|>,\"RGBColorSwatchTemplate\")");
    check("ToBoxes[RGBColor[a, 1, 1]] // Head", //
        "RowBox");
    check("ToBoxes[TableForm[{{1, 2}, {3, 4}}]] // InputForm", //
        "GridBox({{\"1\",\"2\"},{\"3\",\"4\"}})");
    check("ToBoxes[TableForm[{a, b}]] // InputForm", //
        "GridBox({{\"a\"},{\"b\"}})");
    check("ToBoxes[Series[Sin[x] Exp[-x], {x, 0, 5}]] // InputForm", //
        "InterpretationBox(RowBox({RowBox({\"x\",\"-\",SuperscriptBox(\"x\",\"2\"),\"+\",FractionBox(SuperscriptBox(\"x\",\"3\"),\"3\"),\"-\",FractionBox(SuperscriptBox(\"x\",\"5\"),\"30\")}),\"+\",SuperscriptBox(RowBox({\"O\",\"[\",\"x\",\"]\"}),\"6\")}),SeriesData(x,0,{1,-1,1/3,0,-1/30},1,6,1))");
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
    // arguments are separated by commas, so a sum inside brackets needs no parentheses
    check("MakeBoxes[3 g[u + v], StandardForm] // InputForm", //
        "RowBox({\"3\",\" \",RowBox({\"g\",\"[\",RowBox({\"u\",\"+\",\"v\"}),\"]\"})})");
    check("MakeBoxes[3 {u + v, w}, StandardForm] // InputForm", //
        "RowBox({\"3\",\" \",RowBox({\"{\",RowBox({RowBox({\"u\",\"+\",\"v\"}),\",\","
            + "\"w\"}),\"}\"})})");
  }

  @Test
  public void testEveryInfixOperatorIsWrittenInfix() {
    // the precedences and spellings are the parser's own, so an operator the language reads is
    // one it writes - and the brackets follow from the precedence
    check("MakeBoxes[a == b, StandardForm] // InputForm", //
        "RowBox({\"a\",\"==\",\"b\"})");
    check("MakeBoxes[a && b || c, StandardForm] // InputForm", //
        "RowBox({RowBox({\"(\",RowBox({\"a\",\"&&\",\"b\"}),\")\"}),\"||\",\"c\"})");
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
  public void testBoxesOfADefinition() {
    // a definition for c_. * w[...] is kept with w, not with Times or the pattern name c
    check("MakeBoxes[c_. wq[a__], TraditionalForm] := RowBox[{\"W\", ToBoxes[c]}]", //
        "");
    check("{MakeBoxes[wq[1], TraditionalForm], MakeBoxes[5 wq[1], TraditionalForm]} // InputForm",
        "{RowBox({\"W\",\"1\"}),RowBox({\"W\",\"5\"})}");
    check("Length /@ {UpValues[wq], UpValues[c]}", //
        "{1,0}");
    check("MakeBoxes[v_wr, TraditionalForm] := \"WR\"; MakeBoxes[u : ws[_], TraditionalForm] := \"WS\";"
        + " {MakeBoxes[wr[2], TraditionalForm], MakeBoxes[ws[2], TraditionalForm]}", //
        "{WR,WS}");
    // boxes of a definition are a row of several boxes: parenthesized in a product or a power
    check("wt /: MakeBoxes[wt[a_, b_], TraditionalForm] := RowBox[{ToBoxes[a], \"*\", ToBoxes[b]}]",
        "");
    check("ToBoxes[k wt[1, 2], TraditionalForm] // InputForm", //
        "FormBox(RowBox({\"k\",\" \",RowBox({\"(\",RowBox({\"1\",\"*\",\"2\"}),\")\"})}),TraditionalForm)");
    check("ToBoxes[k + wt[1, 2], TraditionalForm] // InputForm", //
        "FormBox(RowBox({\"k\",\"+\",RowBox({\"1\",\"*\",\"2\"})}),TraditionalForm)");
  }

  @Test
  public void testTraditionalFormBoxes() {
    // only StandardForm and TraditionalForm are box formatting types
    check("ToBoxes[z, OutputForm]", //
        "ToBoxes(z,OutputForm)");
    // ToBoxes in TraditionalForm says which form its boxes are written in
    check("ToBoxes[z, TraditionalForm] // InputForm", //
        "FormBox(\"z\",TraditionalForm)");
    check("ToBoxes[z] // InputForm", //
        "\"z\"");
    check("MakeBoxes[Row[{\"(\", z, \")\"}], TraditionalForm] // InputForm", //
        "TemplateBox({\"\\\"(\\\"\",\"z\",\"\\\")\\\"\"},\"RowDefault\")");
    check("MakeBoxes[Style[z, Bold], TraditionalForm] // InputForm", //
        "StyleBox(\"z\",Bold,StripOnInput->False)");
    // the imaginary unit is written \[ImaginaryI]: the input form writes the private use character
    // as its name, unless the encoding is "Unicode"
    check("ToString[MakeBoxes[I, TraditionalForm], InputForm]", //
        "\"\\[ImaginaryI]\"");
    check("ToCharacterCode(ToString[MakeBoxes[I, TraditionalForm], InputForm, CharacterEncoding -> \"Unicode\"])",
        "{34,63310,34}");
    check("ToBoxes(TraditionalForm(E^(I*w))) // InputForm", //
        "TagBox(FormBox(SuperscriptBox(\"\\[ExponentialE]\",RowBox({\"\\[ImaginaryI]\",\" \",\"w\"})),TraditionalForm),TraditionalForm,Editable->True)");
    // a complex number too
    check("ToBoxes(TraditionalForm(3*I*w)) // InputForm", //
        "TagBox(FormBox(RowBox({RowBox({\"3\",\" \",\"\\[ImaginaryI]\"}),\" \",\"w\"}),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(3*I*w) // InputForm", //
        "RowBox({RowBox({\"3\",\" \",\"I\"}),\" \",\"w\"})");
    // the output form shows the characters of the same meaning
    check("ToBoxes(TraditionalForm(E^w))", //
        "TagBox(FormBox(SuperscriptBox(ⅇ,w),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testToBoxes001() {
    check("Map[ToBoxes, {123, 1/23, 1.23, 1 + 23 I}] // InputForm", //
        "{\"123\",FractionBox(\"1\",\"23\"),\"1.23`\",RowBox({\"1\",\"+\",RowBox({\"23\",\" \",\"I\"})})}");
  }

  @Test
  public void testToBoxesRawBoxes() {
    // raw boxes are inserted as they are
    check("ToBoxes[RawBoxes[\"]\"]] // InputForm", //
        "\"]\"");
    check("ToBoxes[g[RawBoxes[SqrtBox[\"k\"]], 3]] // InputForm", //
        "RowBox({\"g\",\"[\",RowBox({SqrtBox(\"k\"),\",\",\"3\"}),\"]\"})");
  }

  @Test
  public void testToBoxesNumberForm() {
    // the digits are a string box drawn without its quotation marks; the tag rebuilds the wrapper
    check("ToBoxes[NumberForm[-6.0372, {4, 1}]] // FullForm", //
        "TagBox(InterpretationBox(StyleBox(\"\\\"-6.0\\\"\", Rule(ShowStringCharacters, False)), -6.0372`, "
            + "Rule(AutoDelete, True)), Function(NumberForm(Slot(1), List(4, 1))))");
    // an integer is not editable
    check("ToBoxes[NumberForm[2047, 6]] // FullForm", //
        "TagBox(InterpretationBox(StyleBox(\"\\\"2047\\\"\", Rule(ShowStringCharacters, False)), 2047, "
            + "Rule(Editable, False)), Function(NumberForm(Slot(1), 6)))");
    // without a specification the tag is the bare form
    check("ToBoxes[PaddedForm[-31.5]] // FullForm", //
        "TagBox(InterpretationBox(StyleBox(\"\\\"-31.5\\\"\", Rule(ShowStringCharacters, False)), -31.5`, "
            + "Rule(AutoDelete, True)), PaddedForm)");
    // scientific notation: the mantissa and the exponent are string boxes
    check("ToBoxes[ScientificForm[0.000713, 2]][[1, 1, 1]] // FullForm", //
        "RowBox(List(\"\\\"7.1\\\"\", \"\\[Times]\", SuperscriptBox(\"10\", \"\\\"-4\\\"\")))");
  }

  @Test
  public void testGridIsATaggedGridBox() {
    check("ToBoxes(Grid({{u, \"w\"}, {1, v}}))", //
        "TagBox(GridBox({{u,\"w\"},{1,v}},AutoDelete->False,GridBoxItemSize->{Columns->{{Automatic}},Rows->{{Automatic}}}),Grid)");
    check("InputForm(ToBoxes(Grid({{u, v}}, Frame -> All)))", //
        "TagBox(GridBox({{\"u\",\"v\"}},AutoDelete->False,GridBoxFrame->{\"Columns\"->{{True}},\"Rows\"->{{True}}},GridBoxItemSize->{\"Columns\"->{{Automatic}},\"Rows\"->{{Automatic}}}),\"Grid\")");
    check("ToExpression(ToBoxes(Grid({{u, 2}}, Frame -> All)))", //
        "Grid({{u,2}},Frame->All)");
    // other options of the grid go into the grid box and come back with the grid
    check("InputForm(ToBoxes(Grid({{u, v}}, Alignment -> Left)))", //
        "TagBox(GridBox({{\"u\",\"v\"}},AutoDelete->False,Alignment->Left,GridBoxItemSize->{\"Columns\"->{{Automatic}},\"Rows\"->{{Automatic}}}),\"Grid\")");
    check("ToExpression(ToBoxes(Grid({{u, 2}}, Alignment -> Left, Frame -> All)))", //
        "Grid({{u,2}},Alignment->Left,Frame->All)");
    // the tag reads back as the Grid
    check("ToExpression(ToBoxes(Grid({{u, 2}, {3, v}})))", //
        "Grid({{u,2},{3,v}})");
    // a table keeps its plain grid
    check("ToBoxes(TableForm({{u, v}}))", //
        "GridBox({{u,v}})");
  }

  @Test
  public void testPlusMinusIsWrittenAsAnOperator() {
    // infix with two or more arguments and prefix with one, as in the output form
    check("InputForm(ToBoxes(PlusMinus(p, q)))", //
        "RowBox({\"p\",\"±\",\"q\"})");
    check("ToBoxes(PlusMinus(p, q, 3))", //
        "RowBox({p,±,q,±,3})");
    check("InputForm(ToBoxes(PlusMinus(w)))", //
        "RowBox({\"±\",\"w\"})");
    // an operand of a lower precedence is put in parentheses
    check("ToBoxes(PlusMinus(u - v))", //
        "RowBox({±,RowBox({(,RowBox({u,-,v}),)})})");
    check("ToBoxes(PlusMinus(2*w)^3)", //
        "SuperscriptBox(RowBox({(,RowBox({±,RowBox({2, ,w})}),)}),3)");
    // a sum binds as tightly as the operator and is put in parentheses too
    check("ToBoxes(PlusMinus(a + 2, 5*c))", //
        "RowBox({RowBox({(,RowBox({2,+,a}),)}),±,RowBox({5, ,c})})");
    check("ToBoxes(TraditionalForm(PlusMinus(p, q)))", //
        "TagBox(FormBox(RowBox({p,±,q}),TraditionalForm),TraditionalForm,Editable->True)");
    check("OutputForm(PlusMinus(p, q))", //
        "p ± q");
    check("PlusMinus(w)", //
        "±w");
    // the prefix form in OutputForm and InputForm
    check("OutputForm(PlusMinus(k))", //
        "±k");
    check("OutputForm(m + PlusMinus(k))", //
        "m + ±k");
    check("InputForm(PlusMinus(k))", //
        "±k");
  }

  @Test
  public void testRowWithSeparatorsTemplate() {
    // the separator as it is drawn and as it was written, then the boxes of the elements
    check("ToBoxes(Row({k, 7}, \":\")) // InputForm", //
        "TemplateBox({\":\",\"\\\":\\\"\",\"k\",\"7\"},\"RowWithSeparators\")");
    check("ToBoxes(Row({k, 7, m}, \"; \")) // InputForm", //
        "TemplateBox({\"; \",\"\\\"; \\\"\",\"k\",\"7\",\"m\"},\"RowWithSeparators\")");
    check("ToBoxes(Row({k, 7}, \"\\[Equal]\")) // InputForm", //
        "TemplateBox({\"\\[Equal]\",\"\\\"\\[Equal]\\\"\",\"k\",\"7\"},\"RowWithSeparators\")");
    // a separator which is no string is boxed as an expression
    check("ToBoxes(Row({k, 7}, m)) // InputForm", //
        "TemplateBox({\"m\",\"k\",\"7\"},\"RowWithSeparator\")");
    // without a separator the row is the default template
    check("ToBoxes(Row({k, 7})) // InputForm", //
        "TemplateBox({\"k\",\"7\"},\"RowDefault\")");
    // the row stays a row; its text forms join the elements
    check("Row({k, 7}, \":\") // InputForm", //
        "Row({k,7},\":\")");
    check("Row({k, 7}, \":\") // OutputForm", //
        "k:7");
    check("Row({k, 7}) // OutputForm", //
        "k7");
    check("ToString(Row({k, 7}, \":\"))", //
        "k:7");
    check("TeXForm(Row({k, 7}, \":\"))", //
        "k\\textnormal{:}7");
    // the text of the template in a picture puts the separator between the elements
    check("StringCount(ExportString(Grid({{RawBoxes(TemplateBox({\":\", \"\\\":\\\"\", \"k\", \"7\", \"m\"},"
        + " \"RowWithSeparators\")), 1}}), \"SVG\"), {\"TemplateBox\", \"k:7:m\"})", //
        "1");
  }

  @Test
  public void testTransformationFunctionIsAMatrixWithDividers() {
    // the matrix in parentheses with a line before the last column and the last row
    check("InputForm(ToBoxes(TranslationTransform({p, q})))", //
        "RowBox({\"TransformationFunction\",\"[\",RowBox({\"(\",TagBox(GridBox({{\"1\",\"0\",\"p\"},{\"0\",\"1\",\"q\"},{\"0\",\"0\",\"1\"}},AutoDelete->False,"
            + "GridBoxDividers->{\"Columns\"->{{False}},\"ColumnsIndexed\"->{-2->True},\"Rows\"->{{False}},\"RowsIndexed\"->{-2->True}},"
            + "GridBoxItemSize->{\"Columns\"->{{Automatic}},\"Rows\"->{{Automatic}}}),#1&),\")\"}),\"]\"})");
    check("InputForm(ToBoxes(TranslationTransform({3, -1, r})))", //
        "RowBox({\"TransformationFunction\",\"[\",RowBox({\"(\",TagBox(GridBox({{\"1\",\"0\",\"0\",\"3\"},{\"0\",\"1\",\"0\",RowBox({\"-\",\"1\"})},{\"0\",\"0\",\"1\",\"r\"},{\"0\",\"0\",\"0\",\"1\"}},AutoDelete->False,"
            + "GridBoxDividers->{\"Columns\"->{{False}},\"ColumnsIndexed\"->{-2->True},\"Rows\"->{{False}},\"RowsIndexed\"->{-2->True}},"
            + "GridBoxItemSize->{\"Columns\"->{{Automatic}},\"Rows\"->{{Automatic}}}),#1&),\")\"}),\"]\"})");
  }
}
