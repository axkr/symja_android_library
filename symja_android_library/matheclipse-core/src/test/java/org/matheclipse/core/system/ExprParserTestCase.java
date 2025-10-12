package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
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
}
