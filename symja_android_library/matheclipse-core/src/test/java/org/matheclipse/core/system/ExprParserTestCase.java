package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.ParserConfig;

/** */
public class ExprParserTestCase extends ExprEvaluatorTestCase {

  /** Read an input in script mode and join the expressions it holds with " | ". */
  private static String scriptExpressions(String input) {
    // Whatever the rest of the suite left the global
    // parser configuration on: it decides whether a full form is written with `[]` or with `()`,
    // and these tests compare the text.
    boolean lowercaseSymbols = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS;
    try {
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
      EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, false);
      ExprParser parser = new ExprParser(engine, ExprParserFactory.MMA_STYLE_FACTORY, false, true,
          ParserConfig.EXPLICIT_TIMES_OPERATOR);
      parser.beginScript(input);
      StringBuilder buf = new StringBuilder();
      IExpr expr = parser.nextScriptExpression();
      while (expr.isPresent()) {
        if (buf.length() > 0) {
          buf.append(" | ");
        }
        buf.append(expr.fullFormString());
        expr = parser.nextScriptExpression();
      }
      return buf.toString();
    } finally {
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = lowercaseSymbols;
    }
  }

  /**
   * In script mode a newline ends the expression, so an input holding several expressions written
   * one per line is read as several expressions rather than as one.
   *
   * <p>
   * Without it the lines join through implicit multiplication and every definition but the first is
   * silently never made - which is what a script pasted into a web front end used to do.
   */
  @Test
  public void testScriptModeSeparatesLines() {
    assertEquals("Set[a, 1] | Set[b, 2]", scriptExpressions("a = 1\nb = 2"));
    assertEquals("SetDelayed[f[Pattern[x, Blank[]]], x] | SetDelayed[g[Pattern[x, Blank[]]], x]",
        scriptExpressions("f[x_] := x\ng[x_] := x"));
    assertEquals("CompoundExpression[Set[a, 1], Null] | Set[b, 2]",
        scriptExpressions("a = 1;\nb = 2"));
    assertEquals("Set[a, 1] | Set[b, 2]", scriptExpressions("a = 1\n\n\nb = 2"));

    // the shape which used to lose the second definition
    assertEquals(
        "SetDelayed[createImage[Pattern[img, Blank[]]], img]"
            + " | CompoundExpression[SetDelayed[perlin[Pattern[w, Blank[]]], w], Null]",
        scriptExpressions("createImage[img_] := \n img\nperlin[w_] := \n  w;"));
  }

  /**
   * A newline ends the expression only where one can end: not inside brackets, and not part-way
   * through an operator.
   */
  @Test
  public void testScriptModeContinuesIncompleteLines() {
    assertEquals("f[1, 2]", scriptExpressions("f[1,\n2]"));
    assertEquals("List[1, 2]", scriptExpressions("{1,\n2}"));
    assertEquals("Plus[1, 2]", scriptExpressions("(1 +\n2)"));
    assertEquals("Part[m, 1, 2]", scriptExpressions("m[[1,\n2]]"));
    assertEquals("Association[Rule[a, 1], Rule[b, 2]]", scriptExpressions("<|a -> 1,\nb -> 2|>"));

    // a line ending in an operator carries on
    assertEquals("Set[a, Plus[1, 2]]", scriptExpressions("a = 1 +\n2"));
    assertEquals("SetDelayed[f[Pattern[x, Blank[]]], x]", scriptExpressions("f[x_] :=\nx"));

    // a backslash at the end of a line joins it to the next one explicitly
    assertEquals("Set[a, Plus[1, 2]]", scriptExpressions("a = 1 \\\n+ 2"));

    // ... and an operator at the START of the next line does not: what came before it was already
    // a complete expression, so this is two of them.
    assertEquals("Set[a, 1] | 2", scriptExpressions("a = 1\n+ 2"));
  }

  /**
   * A <code>;</code> ends the expression only at the end of a line. Several short statements
   * written on one line stay one expression, the way they are typed.
   */
  @Test
  public void testScriptModeSemicolonEndsOnlyAtLineEnd() {
    assertEquals("CompoundExpression[SetDelayed[f[Pattern[x, Blank[]]], Power[x, 2]], f[3]]",
        scriptExpressions("f[x_]:=x^2; f[3]"));
    assertEquals("CompoundExpression[Set[a, 1], Set[b, 2]]", scriptExpressions("a = 1; b = 2"));
    assertEquals("CompoundExpression[Set[a, 1], Set[b, 2], Null] | Set[c, 3]",
        scriptExpressions("a = 1; b = 2;\nc = 3"));
  }

  @Test
  public void testIntegerMIN_VALUE() {
    // Integer.MIN_VALUE
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("-2147483648");
    assertEquals(expr.toMMA(), "-2147483648");
  }

  @Test
  public void testLongMIN_VALUE() {
    // Long.MIN_VALUE
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("-9223372036854775808");
    assertEquals(expr.toMMA(), "-9223372036854775808");
  }

  @Test
  public void testParserDoubleMaxValue() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("2.2250738585072014`*^-308 // FullForm");
    IExpr result = engine.evaluate(expr);
    assertEquals(result.toString(), "2.2250738585072014`*^-308");
  }

  @Test
  public void testParserApfloatValue() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("4.60421677720057651458449514482636628606`20.6008566975056");
    IExpr result = engine.evaluate(expr);
    // TODO Apfloat only knows "long" type precision, so the result is not exactly the same as the
    // input
    assertEquals(result.toString(), "4.6042167772005765145`20");
  }

  @Test
  public void testParserPatternTest() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("Hold(triangle?x_:=x^2) // FullForm");
    IExpr result = engine.evaluate(expr);
    assertEquals(result.toString(), //
        "Hold(SetDelayed(PatternTest(Triangle, Pattern(x, Blank())), Power(x, 2)))");
  }

  @Test
  public void testParserConvertOnInput() {
    // see issue #787
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser p = new ExprParser(engine, true);
    // the test expression is not useful, but parses the full form as in MMA:
    IExpr expr = p.parse("I_m==a*c");
    assertEquals("Equal(Pattern(I, Blank(m)), Times(a, c))", //
        expr.fullFormString());
  }

  @Test
  public void testParserArctan() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser p = new ExprParser(engine, true);
    IExpr expr = p.parse("(arctan(x)+y)");
    assertEquals("Plus(ArcTan(x), y)", //
        expr.fullFormString());
    IExpr result = engine.evaluate(expr);
    assertEquals("y+ArcTan(x)", //
        result.toString());
  }

  @Test
  public void testParserForAll() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser p = new ExprParser(engine, true);
    IExpr expr = p.parse("∀(a)");
    assertEquals("ForAll(a)", //
        expr.fullFormString());
    IExpr result = engine.evaluate(expr);
    assertEquals("∀a", //
        result.toString());
  }

  @Test
  public void testTransposeToString() {
    // Transpose(List(List(1, 2), List(3, 4), List(5, 6)))
    IExpr parse = new ExprEvaluator().parse("Transpose(List(List(1, 2), List(3, 4), List(5, 6)))");
    String s = parse.toString();

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false, 5, 5);
    String text = outputFormFactory.toString(parse);
    assertEquals(text, "{{1,2},{3,4},{5,6}}\uF3C7");

    // now parse back
    IExpr parseBack = new ExprEvaluator().parse(text);
    assertEquals(parseBack.fullFormString(), "Transpose(List(List(1, 2), List(3, 4), List(5, 6)))");
  }

  /**
   * Source that Symja used to reject, all of it taken from packages that failed to read. These go
   * through {@code Parser}/{@code AST2Expr}, which is the path a file takes.
   */
  @Test
  public void testFormsFromRealPackages() {
    // a pure function whose body ends in a semicolon: the `&` applies to the whole
    // CompoundExpression, so the `;` has Null on its right
    assertEquals("Function[CompoundExpression[Set[n[Slot[1]], a[Slot[1]]], Null]]",
        scriptExpressions("n[#] = a[#]; &"));
    assertEquals("Map[Function[CompoundExpression[a, Null]], List[1]]",
        scriptExpressions("Map[a; &, {1}]"));
    // more generally: an operator with no prefix reading applies to what stands to its left
    assertEquals("Times[Power[b, -1], CompoundExpression[a, Null]]", scriptExpressions("a ;/ b"));

    // a part, an application, and a part again
    assertEquals("Part[Part[t, i][\"pos\"], 2]", scriptExpressions("t[[i]][\"pos\"][[2]]"));

    // a string may begin with a newline - a usage message or a template body is written that way
    assertEquals("Set[MessageName[f, usage], \"\na\"]", scriptExpressions("f::usage = \"\na\""));
  }

  /** A derivative followed by a juxtaposed factor is a product, in the relaxed syntax as well. */
  @Test
  public void testDerivativeFollowedByFactor() {
    check("Hold(f'(x) g'(x) == 1)", //
        "Hold(f'(x)*g'(x)==1)");
    check("Hold(f'(x) == g'(x) h(x))", //
        "Hold(f'(x)==g'(x)*h(x))");
    check("Hold(y(x) y'''(x) == y'(x) y''(x))", //
        "Hold(y(x)*Derivative(3)[y][x]==y'(x)*y''(x))");
    check("Hold(f'(x)^2 + 2 g'(x) - g(x) h'(x))", //
        "Hold(f'(x)^2+2*g'(x)-g(x)*h'(x))");
  }

  /**
   * <code>\!\( ... \)</code> is the expression which the boxes between the delimiters are the
   * typeset form of. A notebook copies a formula as such an escape into plain text.
   */
  @Test
  public void testBoxEscapeFormulas() {
    // \* is followed by a box in ordinary syntax, \( ... \) is the text of a box
    assertEquals("Power[x, 2]", scriptExpressions("\\!\\(\\*SuperscriptBox[\\(x\\), \\(2\\)]\\)"));
    assertEquals("Power[x, 2]", scriptExpressions("\\!\\(\\*SuperscriptBox[\"x\", \"2\"]\\)"));
    assertEquals("Subscript[a, b]",
        scriptExpressions("\\!\\(\\*SubscriptBox[\\(a\\), \\(b\\)]\\)"));
    assertEquals("Power[Subscript[x, 1], 2]",
        scriptExpressions("\\!\\(\\*SubsuperscriptBox[\"x\", \"1\", \"2\"]\\)"));
    assertEquals("Hold[Rational[1,2]]",
        scriptExpressions("Hold[\\!\\(\\*FractionBox[\\(1\\), \\(2\\)]\\)]"));
    // (what the parser makes of Sqrt depends on the configuration the suite left behind)
    assertEquals(scriptExpressions("Sqrt[x + 1]"),
        scriptExpressions("\\!\\(\\*SqrtBox[\\(x + 1\\)]\\)"));
    assertEquals("Surd[x, 3]", scriptExpressions("\\!\\(\\*RadicalBox[\"x\", \"3\"]\\)"));
    assertEquals("4", scriptExpressions("\\!\\(\\*InterpretationBox[\"four\", 4]\\)"));
    assertEquals("List[List[a, b], List[c, d]]",
        scriptExpressions("\\!\\(\\*GridBox[{{\"a\", \"b\"}, {\"c\", \"d\"}}]\\)"));
    assertEquals("Row[List[1, 2]]",
        scriptExpressions("\\!\\(\\*TemplateBox[{\"1\", \"2\"}, \"RowDefault\"]\\)"));
    assertEquals("Quantity[19400, \"USDollars\"]", scriptExpressions(
        "\\!\\(\\*TemplateBox[{\"19400\", \"$\", \"US dollars\", \"\\\"USDollars\\\"\"}, "
            + "\"QuantityPrefix\"]\\)"));
    // a derivative is written with primes, or with its orders in a tagged script
    assertEquals("Derivative[2][f][x]",
        scriptExpressions("\\!\\(\\*SuperscriptBox[\"f\", \"\\[Prime]\\[Prime]\"]\\)[x]"));
    assertEquals("Derivative[1, 0][p]", scriptExpressions("\\!\\(\\*SuperscriptBox[\"p\", "
        + "TagBox[RowBox[{\"(\", RowBox[{\"1\", \",\", \"0\"}], \")\"}], Derivative]]\\)"));
    // a script which is no expression stays the mark it is drawn as
    assertEquals("Overscript[x, \"_\"]",
        scriptExpressions("\\!\\(\\*OverscriptBox[\\(x\\), \\(_\\)]\\)"));
    assertEquals("Superscript[H, \"-\"]",
        scriptExpressions("\\!\\(\\*SuperscriptBox[\"H\", \"-\"]\\)"));
    // the escape is a factor like any other
    assertEquals("Plus[1, Times[Power[y, 3], z]]",
        scriptExpressions("1 + \\!\\(\\*SuperscriptBox[\\(y\\), \\(3\\)]\\) z"));
    assertEquals("f[Subscript[x, 1], 2]",
        scriptExpressions("f[\\!\\(\\*SubscriptBox[\\(x\\), \\(1\\)]\\), 2]"));
    // without a box the text is the expression itself, with the operators of the linear syntax
    assertEquals("Plus[2, 2]", scriptExpressions("\\!\\(2+2\\)"));
    assertEquals("Power[x, 2]", scriptExpressions("\\!\\(x \\^ 2\\)"));
  }

  /** A row is its strings one after the other; some rows are an operator and its body. */
  @Test
  public void testBoxEscapeRows() {
    assertEquals("Sum[i, List[i, 1, n]]", scriptExpressions("\\!\\(\\*RowBox[{UnderoverscriptBox["
        + "\"\\[Sum]\", RowBox[{\"i\", \"=\", \"1\"}], \"n\"], \"i\"}]\\)"));
    assertEquals("Integrate[Power[x, 2], List[x, 0, 1]]",
        scriptExpressions("\\!\\(\\*RowBox[{SubsuperscriptBox[\"\\[Integral]\", \"0\", \"1\"], "
            + "RowBox[{SuperscriptBox[\"x\", \"2\"], RowBox[{\"\\[DifferentialD]\", \"x\"}]}]}]\\)"));
    assertEquals("D[Power[x, 3], x]", scriptExpressions("\\!\\(\\*RowBox[{SubscriptBox["
        + "\"\\[PartialD]\", \"x\"], SuperscriptBox[\"x\", \"3\"]}]\\)"));
    assertEquals("Abs[x]", scriptExpressions("\\!\\(\\*RowBox[{\"\\[LeftBracketingBar]\", \"x\", "
        + "\"\\[RightBracketingBar]\"}]\\)"));
    // f(x, y) of a textbook is a call, two rows side by side are a product
    assertEquals("f[x, y]", scriptExpressions(
        "\\!\\(\\*RowBox[{\"f\", \"(\", RowBox[{\"x\", \",\", \"y\"}], \")\"}]\\)"));
    assertEquals("Times[a, Power[x, 3]]",
        scriptExpressions("\\!\\(\\*RowBox[{\"a\", RowBox[{\"x\", \"^\", \"3\"}]}]\\)"));
    assertEquals("Times[2, x]",
        scriptExpressions("\\!\\(\\*RowBox[{\"2\", \"\\[InvisibleTimes]\", \"x\"}]\\)"));
    assertEquals("Rule[x, 1]",
        scriptExpressions("\\!\\(\\*RowBox[{\"x\", \"\\[Rule]\", \"1\"}]\\)"));
    assertEquals(scriptExpressions("Sqrt[Sin[x]]"),
        scriptExpressions("\\!\\(\\*SqrtBox[RowBox[{\"Sin\", \"[\", \"x\", \"]\"}]]\\)"));
  }

  /** <code>FormBox</code> says which form the boxes are written in. */
  @Test
  public void testBoxEscapeForms() {
    assertEquals(scriptExpressions("Sqrt[x]"),
        scriptExpressions("\\!\\(\\*FormBox[SqrtBox[\"x\"], TraditionalForm]\\)"));
    assertEquals("ArcSin[y]", scriptExpressions(
        "\\!\\(\\*FormBox[RowBox[{\"ArcSin[\", \"y\", \"]\"}], TraditionalForm]\\)"));
    assertEquals("3", scriptExpressions("\\!\\(\\*FormBox[\"3\", TraditionalForm]\\)"));
    assertEquals("\"3\"", scriptExpressions("\\!\\(\\*FormBox[\"\\\"3\\\"\", TraditionalForm]\\)"));
    assertEquals("Plus[x, 1]", scriptExpressions("\\!\\(\\*FormBox[\"x + 1\", TraditionalForm]\\)"));
    // boxes which have no reading are kept as their text
    assertEquals("HoldComplete[\"\\!\\(\\*FormBox[SqrtBox[\"x\"], OutputForm]\\)\"]",
        scriptExpressions("\\!\\(\\*FormBox[SqrtBox[\"x\"], OutputForm]\\)"));
    assertEquals("HoldComplete[\"\\!\\(\\*Graphics3DBox[{}]\\)\"]",
        scriptExpressions("\\!\\(\\*Graphics3DBox[{}]\\)"));
  }

  /** <code>\( ... \)</code> without <code>\!</code> is the boxes of the linear syntax. */
  @Test
  public void testBoxNotation() {
    assertEquals("RowBox[List[FractionBox[\"x\", \"y\"], \"+\", \"z\"]]",
        scriptExpressions("\\(x \\/ y + z\\)"));
    assertEquals("FractionBox[\"x\", RowBox[List[\"(\", RowBox[List[\"y\", \"+\", \"z\"]], \")\"]]]",
        scriptExpressions("\\(x \\/ (y + z)\\)"));
    assertEquals("UnderoverscriptBox[\"a\", \"c\", \"b\"]",
        scriptExpressions("\\( a \\& b \\% c\\)"));
    assertEquals("UnderoverscriptBox[\"a\", \"b\", \"c\"]",
        scriptExpressions("\\( a \\+ b \\% c\\)"));
    // \^ and \_ take everything on their right
    assertEquals("SuperscriptBox[\"x\", SubscriptBox[\"2\", \"4\"]]",
        scriptExpressions("\\( x \\^ 2 \\_ 4 \\)"));
    assertEquals("SqrtBox[\"x\"]", scriptExpressions("\\(\\@ x\\)"));
    assertEquals("FormBox[RowBox[List[\"a\", \"+\", \"b\"]], TraditionalForm]",
        scriptExpressions("\\(TraditionalForm \\` a + b\\)"));
  }

  /** The box of an image is the image, the box of a graphics its primitives. */
  @Test
  public void testBoxEscapeGraphics() {
    // the cell a notebook writes for an image, with the line breaks it has there: the compressed
    // pixels stay compressed, and a newline inside the escape doesn't end the expression
    assertEquals(
        "Closing[Image[Uncompress[\"1:eJxTTMoPSmNiYGAo5gASQYnljkVFiZXBzECOU2ZJEkgGxGYBYgZGRiACEgyM"
            + "DAAX8Qc4\"], \"Bit\", Rule[ColorSpace, Automatic], Rule[Interleaving, None]], "
            + "DiskMatrix[1]]",
        scriptExpressions("Closing[\\!\\(\\*\nGraphicsBox[\nTagBox[RasterBox[CompressedData[\"\n"
            + "1:eJxTTMoPSmNiYGAo5gASQYnljkVFiZXBzECOU2ZJEkgGxGYBYgZGRiAC\nEgyMDAAX8Qc4\n\"], "
            + "{{0, 3}, {4, 0}}, {0, 1},\nColorFunction->GrayLevel],\n"
            + "BoxForm`ImageTag[\"Bit\", ColorSpace -> Automatic, Interleaving -> None],\n"
            + "Selectable->False],\nBaseStyle->\"ImageGraphics\",\nImageSizeRaw->{4, 3},\n"
            + "PlotRange->{{0, 4}, {0, 3}}]\\), DiskMatrix[1]]"));
    // a rectangle which isn't turned over has the rows from the bottom
    assertEquals(
        "ImageReflect[Image[List[List[0, 1], List[2, 3]], \"Byte\"], Rule[Top, Bottom]]",
        scriptExpressions("\\!\\(\\*GraphicsBox[TagBox[RasterBox[{{0, 1}, {2, 3}}, "
            + "{{0, 0}, {2, 2}}, {0, 255}], BoxForm`ImageTag[\"Byte\"]]]\\)"));
    // the box of a 3D image
    assertEquals(
        "Closing[Image3D[List[List[List[0, 1]], List[List[2, 3]]], \"Byte\", "
            + "Rule[ColorSpace, \"Grayscale\"], Rule[Interleaving, None]], 6]",
        scriptExpressions("Closing[\\!\\(\\*\nGraphics3DBox[\nTagBox[Raster3DBox["
            + "{{{0, 1}}, {{2, 3}}}, {{0, 1, 2}, {2, 0, 0}}, {0, 255},\n"
            + "ColorFunction->\"GrayLevelDefaultColorFunction\"],\nBoxForm`ImageTag[\n"
            + "     \"Byte\", ColorSpace -> \"Grayscale\", Interleaving -> None],\n"
            + "Selectable->False],\nBoxed->False,\nImageSizeRaw->2]\\), 6]"));
    assertEquals("Hold[Graphics[Disk[List[0, 0]]]]",
        scriptExpressions("Hold[\\!\\(\\*GraphicsBox[DiskBox[{0, 0}]]\\)]"));
    // the directives of a StyleBox stand before what they style
    assertEquals(
        "Graphics[List[Polygon[List[List[0, 0], List[1, 0], List[0, 1]]], "
            + "List[RGBColor[0, 0, 1], List[Point[List[0, 0]]]]], Rule[ImageSize, 100]]",
        scriptExpressions("\\!\\(\\*GraphicsBox[{PolygonBox[{{0, 0}, {1, 0}, {0, 1}}], "
            + "StyleBox[{PointBox[{0, 0}]}, RGBColor[0, 0, 1], StripOnInput -> False]}, "
            + "ImageSize -> 100]\\)"));
  }

  /**
   * The escape ends at the <code>\)</code> which closes it: escapes nest, a <code>\)</code> in a
   * string is none, and a string keeps an escape as characters of its own which are read again.
   */
  @Test
  public void testBoxEscapeDelimiters() {
    assertEquals("f[x, 1]", scriptExpressions("f[\\!\\(\\*RowBox[{\"x\"}]\\), 1]"));
    assertEquals("\"a)b\"", scriptExpressions("\\!\\(\\*RowBox[{\"\\\"a)b\\\"\"}]\\)"));
    assertEquals("StringLength[\"\\!\\(\\*SubscriptBox[\\(p\\), \\(0\\)]\\)\"]",
        scriptExpressions("StringLength[\"\\!\\(\\*SubscriptBox[\\(p\\), \\(0\\)]\\)\"]"));
    check("StringLength(\"\\!\\(\\*SubscriptBox[\\(p\\), \\(0\\)]\\)\")", //
        "26");
    check("FullForm(ToExpression(\"\\!\\(\\*SubscriptBox[\\(p\\), \\(0\\)]\\)\"))", //
        "Subscript(p, 0)");
    check("FullForm(ToExpression(RowBox({\"1\", \"+\", \"x\"})))", //
        "Plus(1, x)");
    check("FullForm(ToExpression(MakeBoxes(x^2/y)))", //
        "Times(Power(x, 2), Power(y, -1))");
    check("\\!\\(\\*SuperscriptBox[\\(x\\), \\(2\\)]\\) + \\!\\(\\*FractionBox[\\(1\\), \\(2\\)]\\)", //
        "1/2+x^2");
    check("\\!\\(\\*SuperscriptBox[\\(x\\), \\(2\\)]", //
        "Syntax error in line: 1 - box escape - '\\)' expected.\n"
            + "\\!\\(\\*SuperscriptBox[\\(x\\), \\(2\\)]\n" + "   ^");
  }
}
