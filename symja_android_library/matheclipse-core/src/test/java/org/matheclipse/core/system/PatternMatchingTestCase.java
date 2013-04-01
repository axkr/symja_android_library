package org.matheclipse.core.system;


/**
 *  Tests pattern matcher
 */
public class PatternMatchingTestCase extends SpecialTestCase {

  public PatternMatchingTestCase(String name) {
    super(name);
  }

  /**
   *  Test system functions
   */
  public void testSimplePatternMatching() {
  	// the space between "x_" nad "." operator is needed:
		checkPattern("x_ . y_", "a.b.c", "[Dot[a, b], c]");
		checkPattern("x_+y_", "a+b+c", "[Plus[a, b], c]");
		checkPattern("f[x_]", "f[a]", "[a]");
		checkPattern("f[x_,y_]", "f[a,b]", "[a, b]");
		checkPattern("g[x_,y_]", "f[a,b]", "");
		checkPattern("g[x_,42, y_]", "g[a,42,b]", "[a, b]");
  }


}
