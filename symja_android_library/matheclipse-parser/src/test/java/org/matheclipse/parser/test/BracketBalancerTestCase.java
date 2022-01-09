package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.Scanner;

/** Tests parser function for SimpleParserFactory */
class BracketBalancerTestCase {

  @Test
  void testBracketBalancer1() {
    String result = Scanner.balanceCode("int(f(cos(x),x");
    assertEquals("))", result);
  }

  @Test
  void testBracketBalancer2() {
    String result = Scanner.balanceCode("int(sin(cos(x)),x)");
    assertEquals("", result);
  }

  @Test
  void testBracketBalancer3() {
    String result = Scanner.balanceCode("int(f[[2,g(x,y[[z]],{1,2,3");
    assertEquals("})]])", result);
  }

  @Test
  void testBracketBalancer4() {
    String result = Scanner.balanceCode("int(f[[2,g(x,y[[z)){1,2,3");
    assertEquals(null, result);
  }
}
