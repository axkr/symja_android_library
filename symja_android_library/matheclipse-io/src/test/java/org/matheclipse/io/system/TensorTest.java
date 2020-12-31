package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.script.engine.MathScriptEngine;

/** Tests for tensor functions */
public class TensorTest extends AbstractTestCase {

  public TensorTest(String name) {
    super(name);
  }

  public void testTensorDimensions() {
    check(
        "A=Array(a, {2, 3, 4});TensorDimensions(A)", //
        "{2,3,4}");
    check(
        "TensorDimensions({{1,2},{3,4},{a,b}})", //
        "{3,2}");
  }

  public void testTensorRank() {
    check(
        "A=Array(a, {2, 3, 4});TensorRank(A)", //
        "3");
    check(
        "TensorRank({{1,2},{3,4}})", //
        "2");
  }

  public void testTensorSymmetry() {
    check(
        "TensorSymmetry({{a,b,c,d}, {b,e,f,g}, {c,f,h,i},{d,g,i,j}})", //
        "Symmetric({1,2})");
    check(
        "TensorSymmetry({{0, a, b}, {-a, 0, c}, {-b, -c, 0}})", //
        "AntiSymmetric({1,2})");
    check(
        "TensorSymmetry({{a}})", //
        "Symmetric({1,2})");
    check(
        "TensorSymmetry({{0}})", //
        "ZeroSymmetric({})");
    check(
        "TensorSymmetry({{0,0}, {0,0}})", //
        "ZeroSymmetric({})");
    check(
        "TensorSymmetry({{a,b}, {b,c}})", //
        "Symmetric({1,2})");
  }

  public void testTensorProduct() {
    check(
        "TensorProduct(a + 2*b, c)", //
        "(a+2*b)⊗c");

    check(
        "TensorProduct({2, 3}, {{a, b}, {c, d}} )", //
        "{{{2*a,2*b},{2*c,2*d}},{{3*a,3*b},{3*c,3*d}}}");
    check(
        "TensorProduct({{{2*a,2*b},{2*c,2*d}},{{3*a,3*b},{3*c,3*d}}}, {x, y})", //
        "{{{{2*a*x,2*a*y},{2*b*x,2*b*y}},{{2*c*x,2*c*y},{2*d*x,2*d*y}}},{{{3*a*x,3*a*y},{\n"
            + "3*b*x,3*b*y}},{{3*c*x,3*c*y},{3*d*x,3*d*y}}}}");
    check(
        "TensorProduct({2, 3}, {{a, b}, {c, d}}, {x, y})", //
        "{{{{2*a*x,2*a*y},{2*b*x,2*b*y}},{{2*c*x,2*c*y},{2*d*x,2*d*y}}},{{{3*a*x,3*a*y},{\n"
            + "3*b*x,3*b*y}},{{3*c*x,3*c*y},{3*d*x,3*d*y}}}}");
    check(
        "TensorProduct({a, b}, {x, y})", //
        "{{a*x,a*y},{b*x,b*y}}");
    check(
        "TensorProduct({x,y}, {a,b})", //
        "{{a*x,b*x},{a*y,b*y}}");

    check(
        "TensorProduct({{3, 8, 2, 7, 7}, {0, 3, 9, 9, 8}}, {{8, 10, 4, 9}, {5, 6, 7, 4}, {2, 3, 2, 9}})", //
        "{{{{24,30,12,27},{15,18,21,12},{6,9,6,27}},{{64,80,32,72},{40,48,56,32},{16,24,\n"
            + "16,72}},{{16,20,8,18},{10,12,14,8},{4,6,4,18}},{{56,70,28,63},{35,42,49,28},{14,\n"
            + "21,14,63}},{{56,70,28,63},{35,42,49,28},{14,21,14,63}}},{{{0,0,0,0},{0,0,0,0},{0,\n"
            + "0,0,0}},{{24,30,12,27},{15,18,21,12},{6,9,6,27}},{{72,90,36,81},{45,54,63,36},{\n"
            + "18,27,18,81}},{{72,90,36,81},{45,54,63,36},{18,27,18,81}},{{64,80,32,72},{40,48,\n"
            + "56,32},{16,24,16,72}}}}");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
