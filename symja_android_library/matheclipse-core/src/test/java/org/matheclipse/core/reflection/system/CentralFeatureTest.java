package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class CentralFeatureTest extends ExprEvaluatorTestCase {

  @Test
  public void testCentralFeature() {
    // Numeric 1D list
    check("CentralFeature({1, 2, 10})", //
        "2");

    // List of vectors
    check("CentralFeature({{0,0}, {1,1}, {10,10}})", //
        "{1,1}");

  }

  @Test
  public void testCentralFeatureStrings() {
    // uses EditDistance by default
    check("CentralFeature({\"apple\", \"apply\", \"banana\"})", //
        "apple");
    check("CentralFeature({\"abcd\", \"bcde\", \"abab\", \"abcdef\", \"agi\"})", //
        "abcd");
  }

  @Test
  public void testCentralFeatureRules() {
    check("d= {{1, 2}, {3, 4}, {8, 7}, {6, 5}, {9, 4}, {1, 3}};", //
        "");
    // CentralFeature: data is neither a list of real points nor a valid list of rules.
    check("CentralFeature(data)", //
        "CentralFeature(data)");
    check("CentralFeature(d)", //
        "{3,4}");
    check("CentralFeature(d->Table(g(i), {i, 6}))", //
        "g(2)");
    check(
        "CentralFeature({{1, 2}->g(1), {3, 4}->g(2), {8, 7}->g(3), {6, 5}->g(4), {9, 4}->g(5), {1, 3}->g(6)})", //
        "g(2)");
  }

  @Test
  public void testCentralFeatureBoolean() {
    check(
        "CentralFeature[{{True, False, True}, {True, True, True}, {True, False, False}, {False, False, False}}]", //
        "{True,False,True}");
  }

  @Test
  public void testCentralFeatureSingle() {
    check("CentralFeature({42})", "42");
  }

  @Test
  public void testCentralFeatureSymmetry() {
    // Both 2 and 3 are equally central to {1, 2, 3, 4},
    // the implementation picks the first one encountered.
    check("CentralFeature({1, 2, 3, 4})", //
        "2");
  }
}
