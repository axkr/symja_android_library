package org.matheclipse.parser.test;

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import junit.framework.TestCase;

/** Tests parser functions for the simple parser style */
public class RelaxedParserTestCase extends TestCase {

  public RelaxedParserTestCase(String name) {
    super(name);
  }

  public void testParser0() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("Integrate(Sin(x)^2+3*x^4, x)");
      assertEquals(obj.toString(), "Integrate(Plus(Power(Sin(x), 2), Times(3, Power(x, 4))), x)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("1", "0");
    }
  }

  public void testParser1() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("a()(0)(1)f[[x]]");
      assertEquals(obj.toString(), "Times(Times(a(), Times(0, 1)), Part(f, x))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("1", "0");
    }
  }

  public void testParser2() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("1; 2; 3;");
      assertEquals(obj.toString(), "CompoundExpression(1, 2, 3, Null)");

      obj = p.parse("1; 2; 3");
      assertEquals(obj.toString(), "CompoundExpression(1, 2, 3)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("1", "0");
    }
  }

  public void testParser3() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("a sin()cos()x()y z");
      assertEquals(obj.toString(), "Times(Times(Times(Times(Times(a, sin()), cos()), x()), y), z)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("1", "0");
    }
  }

  public void testParser4() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("#1.#123");
      assertEquals(obj.toString(), "Dot(Slot(1), Slot(123))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse5() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("f@ g@ h");
      assertEquals(obj.toString(), "f(g(h))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser6() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("#\"Column Type\"");
      assertEquals(obj.toString(), "Slot(Column Type)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser7() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("#identifier");
      assertEquals(obj.toString(), "Slot(identifier)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser8() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("Fibonacci(1007,Null)");
      assertEquals(obj.toString(), "Fibonacci(1007, Null)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser9() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("2.33`");
      assertEquals(obj.toString(), "2.33");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser10() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("f(t_) = Simplify( r'(t) / Norm( r'(t)), t ∈ Reals);");
      assertEquals(
          obj.toString(), //
          "CompoundExpression(Set(f(t_), Simplify(Times(Derivative(1)[r][t], Power(Norm(Derivative(1)[r][t]), -1)), Element(t, Reals))), Null)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser11() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("x[ [ ] ]");
      assertEquals(
          obj.toString(), //
          "Part(x)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }
}
