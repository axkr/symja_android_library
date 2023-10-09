package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/** Tests parser functions for the simple parser style */
class RelaxedParserTestCase {
  static Parser PARSE_RELAXED = new Parser(true);

  @Test
  void testParser0() {
    ASTNode obj = PARSE_RELAXED.parse("Integrate(Sin(x)^2+3*x^4, x)");
    assertEquals("Integrate(Plus(Power(Sin(x), 2), Times(3, Power(x, 4))), x)", obj.toString());
  }

  @Test
  void testParser1() {
    ASTNode obj = PARSE_RELAXED.parse("a()(0)(1)f[[x]]");
    assertEquals("Times(Times(a(), Times(0, 1)), Part(f, x))", obj.toString());
  }

  @Test
  void testParser2() {
    ASTNode obj = PARSE_RELAXED.parse("1; 2; 3;");
    assertEquals("CompoundExpression(1, 2, 3, Null)", obj.toString());

    obj = PARSE_RELAXED.parse("1; 2; 3");
    assertEquals("CompoundExpression(1, 2, 3)", obj.toString());
  }

  @Test
  void testParser3() {
    ASTNode obj = PARSE_RELAXED.parse("a sin()cos()x()y z");
    assertEquals("Times(Times(Times(Times(Times(a, sin()), cos()), x()), y), z)", obj.toString());
  }

  @Test
  void testParser4() {
    ASTNode obj = PARSE_RELAXED.parse("#1.#123");
    assertEquals("Dot(Slot(1), Slot(123))", obj.toString());
  }

  @Test
  void testParse5() {
    ASTNode obj = PARSE_RELAXED.parse("f@ g@ h");
    assertEquals("f(g(h))", obj.toString());
  }

  @Test
  void testParser6() {
    ASTNode obj = PARSE_RELAXED.parse("#\"Column Type\"");
    assertEquals("Slot(Column Type)", obj.toString());
  }

  @Test
  void testParser7() {
    ASTNode obj = PARSE_RELAXED.parse("#identifier");
    assertEquals("Slot(identifier)", obj.toString());
  }

  @Test
  void testParser8() {
    ASTNode obj = PARSE_RELAXED.parse("Fibonacci(1007,Null)");
    assertEquals("Fibonacci(1007, Null)", obj.toString());
  }

  @Test
  void testParser9() {
    ASTNode obj = PARSE_RELAXED.parse("2.33`");
    assertEquals("2.33", obj.toString());
  }

  @Test
  void testParser10() {
    ASTNode obj = PARSE_RELAXED.parse("f(t_) = Simplify( r'(t) / Norm( r'(t)), t ∈ Reals);");
    assertEquals(
        "CompoundExpression(Set(f(t_), Simplify(Times(Derivative(1)[r][t], Power(Norm(Derivative(1)[r][t]), -1)), Element(t, Reals))), Null)",
        obj.toString());
  }

  @Test
  void testParser11() {
    ASTNode obj = PARSE_RELAXED.parse("x[ [ ] ]");
    assertEquals("Part(x)", obj.toString());
  }

  @Test
  void testParser12() {
    ASTNode obj = PARSE_RELAXED.parse("I_m==a*c");
    assertEquals("Equal(I_m, Times(a, c))", obj.toString());
  }
}
