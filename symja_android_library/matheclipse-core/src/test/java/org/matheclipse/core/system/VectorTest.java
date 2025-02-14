package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for vector comparing functions */
public class VectorTest extends ExprEvaluatorTestCase {

  @Test
  public void testVectorGreaterMatrix() {

    check("m1 = RandomReal({.99, 2}, {4, 4});m2 = RandomReal({0, 1.01}, {4, 4});", //
        "");
    check("VectorGreater({m1, m2})", //
        "True");
  }

  @Test
  public void testVectorGreater() {
    check("VectorGreater({{{1, {2,7}},{3,4}}, {{0,-1}, {2,-1}}})", //
        "True");
    check("VectorGreater({{{1, {2,7}},{3,4}}, {{0,2}, {2,-1}}})", //
        "False");

    check("v = RandomReal(1, 10);", //
        "");
    check("VectorGreater({v, 0})", //
        "True");

    check("VectorGreater({{1, 2, 3},{0, -1, 2}})", //
        "True");


  }

  @Test
  public void testVectorGreaterEqualMatrix() {

    check("m1 = RandomReal({.99, 2}, {4, 4});m2 = RandomReal({0, 1.01}, {4, 4});", //
        "");
    check("VectorGreaterEqual({m1, m2})", //
        "True");
  }

  @Test
  public void testVectorGreaterEqual() {
    check("VectorGreaterEqual({{{1, {2,7}},{3,4}}, {{0,-1}, {2,-1}}})", //
        "True");
    check("VectorGreaterEqual({{{1, {2,7}},{3,4}}, {{0,2}, {2,-1}}})", //
        "True");

    check("v = RandomReal(1, 10);", //
        "");
    check("VectorGreaterEqual({v, 0})", //
        "True");

    check("VectorGreaterEqual({{1, 2, 3},{0, -1, 2}})", //
        "True");
  }

  @Test
  public void testVectorLessMatrix() {

    check("m1 = RandomReal({.99, 2}, {4, 4});m2 = RandomReal({0, 1.01}, {4, 4});", //
        "");
    check("VectorLess({m2, m1})", //
        "True");
  }

  @Test
  public void testVectorLess() {
    check("VectorLess({{{0,-1}, {2,-1}}, {{1, {2,7}},{3,4}}})", //
        "True");
    check("VectorLess({{{0,2}, {2,-1}}, {{1, {2,7}},{3,4}}})", //
        "False");

    check("v = RandomReal(1, 10);", //
        "");
    check("VectorLess({0,v})", //
        "True");

    check("VectorLess({{0, -1, 2},{1, 2, 3}})", //
        "True");


  }

  @Test
  public void testVectorLessEqualMatrix() {

    check("m1 = RandomReal({.99, 2}, {4, 4});m2 = RandomReal({0, 1.01}, {4, 4});", //
        "");
    check("VectorLessEqual({m2, m1})", //
        "True");
  }

  @Test
  public void testVectorLessEqual() {
    check("VectorLessEqual({{{0,-1}, {2,-1}}, {{1, {2,7}},{3,4}}})", //
        "True");
    check("VectorLessEqual({{{0,2}, {2,-1}}, {{1, {2,7}},{3,4}}})", //
        "True");

    check("v = RandomReal(1, 10);", //
        "");
    check("VectorLessEqual({0,v})", //
        "True");

    check("VectorLessEqual({{0, -1, 2},{1, 2, 3}})", //
        "True");


  }


  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
