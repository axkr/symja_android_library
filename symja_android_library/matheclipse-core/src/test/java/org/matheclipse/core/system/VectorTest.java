package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
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
    check(
        "m1 = {{1.32494,1.32205,1.97943,1.22093},{1.40006,1.41398,1.21178,1.64265},{1.08609,1.3407,1.20719,1.50161},{1.7283,1.66998,1.9894,1.51276}};",
        // RandomReal({.99, 2}, {4, 4})",
        "");
    check(
        "m2 = {{0.404199,0.900161,0.17457,0.220848},{0.099938,0.00887308,0.0985215,0.189718},{0.27195,0.525439,0.391606,0.666198},{0.509221,0.528125,0.199934,0.601169}};", //
        // "RandomReal({0, 1.01}, {4, 4})", //
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
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

}
