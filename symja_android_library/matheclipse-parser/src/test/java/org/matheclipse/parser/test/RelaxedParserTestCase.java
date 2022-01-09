package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/** Tests parser functions for the simple parser style */
class RelaxedParserTestCase {

  @Test
  void testParser0() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("Integrate(Sin(x)^2+3*x^4, x)");
    assertEquals("Integrate(Plus(Power(Sin(x), 2), Times(3, Power(x, 4))), x)", obj.toString());
  }

  @Test
  void testParser1() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("a()(0)(1)f[[x]]");
    assertEquals("Times(Times(a(), Times(0, 1)), Part(f, x))", obj.toString());
  }

  @Test
  void testParser2() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("1; 2; 3;");
    assertEquals("CompoundExpression(1, 2, 3, Null)", obj.toString());

    obj = p.parse("1; 2; 3");
    assertEquals("CompoundExpression(1, 2, 3)", obj.toString());
  }

  @Test
  void testParser3() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("a sin()cos()x()y z");
    assertEquals("Times(Times(Times(Times(Times(a, sin()), cos()), x()), y), z)", obj.toString());
  }

  @Test
  void testParser4() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("#1.#123");
    assertEquals("Dot(Slot(1), Slot(123))", obj.toString());
  }

  @Test
  void testParse5() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("f@ g@ h");
    assertEquals("f(g(h))", obj.toString());
  }

  @Test
  void testParser6() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("#\"Column Type\"");
    assertEquals("Slot(Column Type)", obj.toString());
  }

  @Test
  void testParser7() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("#identifier");
    assertEquals("Slot(identifier)", obj.toString());
  }

  @Test
  void testParser8() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("Fibonacci(1007,Null)");
    assertEquals("Fibonacci(1007, Null)", obj.toString());
  }

  @Test
  void testParser9() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("2.33`");
    assertEquals("2.33", obj.toString());
  }

  @Test
  void testParser10() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("f(t_) = Simplify( r'(t) / Norm( r'(t)), t ∈ Reals);");
    assertEquals(
        "CompoundExpression(Set(f(t_), Simplify(Times(Derivative(1)[r][t], Power(Norm(Derivative(1)[r][t]), -1)), Element(t, Reals))), Null)",
        obj.toString());
  }

  @Test
  void testParser11() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("x[ [ ] ]");
    assertEquals("Part(x)", obj.toString());
  }
}
