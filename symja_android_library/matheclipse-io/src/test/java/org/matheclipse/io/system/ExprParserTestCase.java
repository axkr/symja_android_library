package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/** */
public class ExprParserTestCase extends AbstractTestCase {
  public ExprParserTestCase(String name) {
    super(name);
  }

  public void testIntegerMIN_VALUE() {
    //	  Integer.MIN_VALUE
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("-2147483648");
    assertEquals(expr.toMMA(), "-2147483648");
  }

  public void testLongMIN_VALUE() {
    //	  Long.MIN_VALUE
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("-9223372036854775808");
    assertEquals(expr.toMMA(), "-9223372036854775808");
  }

  public void testParserDoubleMaxValue() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("2.2250738585072014`*^-308 // FullForm");
    IExpr result = engine.evaluate(expr);
    assertEquals(result.toString(), "2.2250738585072014`*^-308");
  }
}
