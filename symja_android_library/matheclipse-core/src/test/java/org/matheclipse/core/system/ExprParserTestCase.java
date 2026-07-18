package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/** */
public class ExprParserTestCase extends ExprEvaluatorTestCase {

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
