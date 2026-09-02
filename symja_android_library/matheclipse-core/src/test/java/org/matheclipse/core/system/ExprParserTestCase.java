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
    // Read and render in Wolfram Language syntax, whatever the rest of the suite left the global
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
    assertEquals("Association[Rule[a, 1], Rule[b, 2]]",
        scriptExpressions("<|a -> 1,\nb -> 2|>"));

    // a line ending in an operator carries on
    assertEquals("Set[a, Plus[1, 2]]", scriptExpressions("a = 1 +\n2"));
    assertEquals("SetDelayed[f[Pattern[x, Blank[]]], x]", scriptExpressions("f[x_] :=\nx"));

    // a backslash at the end of a line joins it to the next one explicitly
    assertEquals("Set[a, Plus[1, 2]]", scriptExpressions("a = 1 \\\n+ 2"));

    // ... and an operator at the START of the next line does not: what came before it was already
    // a complete expression, so this is two of them. The Wolfram Language reads it the same way.
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
}
